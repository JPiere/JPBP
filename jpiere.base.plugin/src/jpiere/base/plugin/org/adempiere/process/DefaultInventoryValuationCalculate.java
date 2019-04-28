/******************************************************************************
 * Product: JPiere                                                            *
 * Copyright (C) Hideaki Hagiwara (h.hagiwara@oss-erp.co.jp)                  *
 *                                                                            *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY.                          *
 * See the GNU General Public License for more details.                       *
 *                                                                            *
 * JPiere is maintained by OSS ERP Solutions Co., Ltd.                        *
 * (http://www.oss-erp.co.jp)                                                 *
 *****************************************************************************/
package jpiere.base.plugin.org.adempiere.process;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.adempiere.exceptions.DBException;
import org.compiere.model.I_C_InvoiceLine;
import org.compiere.model.I_C_OrderLine;
import org.compiere.model.MConversionRate;
import org.compiere.model.MConversionRateUtil;
import org.compiere.model.MCurrency;
import org.compiere.model.MDocType;
import org.compiere.model.MInOutLine;
import org.compiere.model.MInvoice;
import org.compiere.model.MMatchInv;
import org.compiere.model.MMatchPO;
import org.compiere.model.MOrder;
import org.compiere.model.MPriceList;
import org.compiere.model.MPriceListVersion;
import org.compiere.model.MProductPrice;
import org.compiere.process.DocAction;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;

import jpiere.base.plugin.org.adempiere.model.MInvValCal;
import jpiere.base.plugin.org.adempiere.model.MInvValCalLine;
import jpiere.base.plugin.org.adempiere.model.MInvValCalLog;
import jpiere.base.plugin.org.adempiere.model.MInvValProfile;
import jpiere.base.plugin.util.JPiereInvValUtil;

/**
 * JPIERE-0161 Inventory Valuation Calculate Doc
 *
 *  Default Inventory Valuation Calculate
 *
 *  @author Hideaki Hagiwara
 *
 */
public class DefaultInventoryValuationCalculate extends SvrProcess {

	private MInvValProfile m_InvValProfile = null;
	private MInvValCal m_InvValCal = null;
	private MInvValCalLine[] lines = null;
	private MCurrency m_Currency = null;

	private LinkedHashMap<Integer, BigDecimal> map_Product_Qty_LastDateValue = null;

	private MPriceList m_PriceList = null;
	private MPriceListVersion m_PriceListVersionOfLastDateValue = null ;
	private MPriceListVersion m_PriceListVersionOfDateValue = null;

	int Record_ID = 0;

	@Override
	protected void prepare()
	{
		Record_ID = getRecord_ID();
		if(Record_ID > 0)
		{
			m_InvValCal = new MInvValCal(getCtx(), Record_ID, null);
			m_InvValProfile = MInvValProfile.get(getCtx(), m_InvValCal.getJP_InvValProfile_ID());
			lines = m_InvValCal.getLines();
			m_Currency = MCurrency.get(getCtx(), m_InvValCal.getC_Currency_ID());

			if(m_InvValProfile.getM_PriceList_ID() > 0)
			{
				m_PriceList = new MPriceList(getCtx(), m_InvValProfile.getM_PriceList_ID(), get_TrxName());
			}

			if(m_PriceList != null && m_InvValCal.getJP_LastDateValue() != null)
			{
				m_PriceListVersionOfLastDateValue = JPiereInvValUtil.getPriceListVersion(getCtx(), m_PriceList.getM_PriceList_ID(), m_InvValCal.getJP_LastDateValue(), get_TrxName());

			}

			//Delete InvValCalLog
			StringBuilder DeleteSQL = new StringBuilder("DELETE FROM " + MInvValCalLog.Table_Name + " WHERE JP_InvValCalLine_ID = ?");
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			for(int i = 0; i < lines.length; i++)
			{
				try
				{
					pstmt = DB.prepareStatement(DeleteSQL.toString(), get_TrxName());
					pstmt.setInt (1, lines[i].get_ID());
					pstmt.executeUpdate();
				}
				catch (SQLException e)
				{
					log.log(Level.SEVERE, DeleteSQL.toString(), e);
					throw new DBException(e, DeleteSQL.toString());
				} finally {
					DB.close(rs, pstmt);
					rs = null; pstmt = null;
				}

			}
		}else{
			log.log(Level.SEVERE, "Record_ID <= 0 ");
		}

	}// prepare()

	@Override
	protected String doIt() throws Exception
	{

		if(m_InvValProfile.getCostingMethod().equals(MInvValCalLine.COSTINGMETHOD_RetailInventoryMethod))
		{
			if(m_PriceList != null && m_InvValCal.getDateValue() != null)
			{
				m_PriceListVersionOfDateValue = JPiereInvValUtil.getPriceListVersion(getCtx(), m_PriceList.getM_PriceList_ID(), m_InvValCal.getDateValue(), get_TrxName());
			}

			if(m_PriceListVersionOfDateValue == null)
			{
				//Price List of Inventory Valuation Profile has not version of Value Date.
				throw new Exception(Msg.getMsg(getCtx(), "JP_DateValue_PriceListVersion"));
			}
		}

		if(m_InvValCal.getJP_LastDateValue() != null
				&& (m_InvValProfile.getCostingMethod().equals(MInvValCalLine.COSTINGMETHOD_AveragePO) || m_InvValProfile.getCostingMethod().equals(MInvValCalLine.COSTINGMETHOD_AverageInvoice)) )
		{
			map_Product_Qty_LastDateValue = JPiereInvValUtil.getAllQtyBookFromStockOrg(getCtx(), m_InvValCal.getJP_LastDateValue() , m_InvValProfile.getOrgs(), " p.M_Product_Category_ID, p.Value");
		}


		for(int i = 0; i < lines.length; i++)
		{
			if(lines[i].getCostingMethod().equals(MInvValCalLine.COSTINGMETHOD_Fifo))
			{
				calculate_Fifo(lines[i]);
			}else if(lines[i].getCostingMethod().equals(MInvValCalLine.COSTINGMETHOD_Lifo)){
				calculate_Lifo(lines[i]);
			}else if(lines[i].getCostingMethod().equals(MInvValCalLine.COSTINGMETHOD_LastPOPrice)){
				calculate_LastPO(lines[i]);
			}else if(lines[i].getCostingMethod().equals(MInvValCalLine.COSTINGMETHOD_LastInvoice)){
				calculate_LastInvoice(lines[i]);
			}else if(lines[i].getCostingMethod().equals(MInvValCalLine.COSTINGMETHOD_AveragePO)){
				calculate_AveragePO(lines[i]);
			}else if(lines[i].getCostingMethod().equals(MInvValCalLine.COSTINGMETHOD_AverageInvoice)){
				calculate_AverageInvoice(lines[i]);
			}else if(lines[i].getCostingMethod().equals(MInvValCalLine.COSTINGMETHOD_RetailInventoryMethod)){
				calculate_RetailInventoryMethod(lines[i]);
			}else if(lines[i].getCostingMethod().equals(MInvValCalLine.COSTINGMETHOD_StandardCosting)){
				return Msg.getMsg(getCtx(), "JP_Can_Not_Calculate_Costing_Method");
			}else{
				return Msg.getMsg(getCtx(), "JP_Can_Not_Calculate_Costing_Method");
			}
		}

		BigDecimal totalLines = JPiereInvValUtil.calculateTotalLines(getCtx(), MInvValCalLine.Table_Name, "JP_InvValCal_ID", Record_ID, get_TrxName());
		if(totalLines == null)
			totalLines = Env.ZERO;
		totalLines = totalLines.setScale(m_Currency.getStdPrecision(), RoundingMode.HALF_UP);
		m_InvValCal.setTotalLines(totalLines);
		m_InvValCal.saveEx(get_TrxName());

		return Msg.getElement(getCtx(), MInvValCal.COLUMNNAME_TotalLines) + " = " + totalLines;

	}// doIt()

	/**
	 * Calculate FIFO
	 *
	 * @param line
	 */
	private void calculate_Fifo(MInvValCalLine line)
	{
		BigDecimal qtyBook = line.getQtyBook().abs();//abs is for nagative Inventory.
		MInOutLine[] ioLines = JPiereInvValUtil.getInOutLines(getCtx(),line.getM_Product_ID(), m_InvValCal.getJP_LastDateValue()
									, m_InvValCal.getDateValue(), m_InvValProfile.getOrgs(), "io.MovementDate DESC, io.DocumentNo DESC, io.M_InOut_ID DESC, iol.Line DESC, iol.M_InOutLine_ID DESC");

		int lineNo = 1;
		MMatchPO[] matchPos = null;
		MMatchInv[] matchInvs = null;
		for(int i = 0; i < ioLines.length; i++)
		{
			/***Check***/
			//ignore nagative inventory.
			if((ioLines[i].getParent().getC_DocType().getDocBaseType().equals(MDocType.DOCBASETYPE_MaterialReceipt) && ioLines[i].getMovementQty().compareTo(Env.ZERO) <= 0)
					|| ioLines[i].getParent().getC_DocType().getDocBaseType().equals(MDocType.DOCBASETYPE_MaterialDelivery))
				continue;

			matchPos = null;
			matchInvs = null;
			if(m_InvValProfile.getJP_ApplyAmtList().equals(MInvValProfile.JP_APPLYAMTLIST_PurchaseOrder))
			{
				matchPos = JPiereInvValUtil.getMatchPOs(getCtx(), ioLines[i].getM_InOutLine_ID()," DateAcct DESC, M_MatchPO_ID DESC", get_TrxName());
				if(matchPos == null || matchPos.length < 1 )
					continue;

			}else if(m_InvValProfile.getJP_ApplyAmtList().equals(MInvValProfile.JP_APPLYAMTLIST_InvoiceVendor)){
				matchInvs = JPiereInvValUtil.getMatchInvs(getCtx(), ioLines[i].getM_InOutLine_ID()," DateAcct DESC, M_MatchInv_ID DESC", get_TrxName());
				if(matchInvs == null || matchInvs.length < 1 )
					continue;
			}else{
				break;
			}

			/***Calculate***/
			if(m_InvValProfile.getJP_ApplyAmtList().equals(MInvValProfile.JP_APPLYAMTLIST_PurchaseOrder))//TODO
			{
				I_C_OrderLine orderLine =null;
				MOrder order = null;
				for(int j = 0; j < matchPos.length; j++)
				{
					//Check to create Inventory Calculation Log or not
					if(matchPos[j].getDateAcct().compareTo(m_InvValCal.getDateValue()) > 0)
						continue;

					orderLine = matchPos[j].getC_OrderLine();

					if(orderLine.getQtyOrdered().compareTo(Env.ZERO)==0)
						continue;

					order = JPiereInvValUtil.getMOrder(getCtx(),orderLine.getC_Order_ID());
					if(!(order.getDocStatus().equals(DocAction.STATUS_Completed) || order.getDocStatus().equals(DocAction.STATUS_Closed)))
						continue;

					//Create Inventory Calculation Log
					MInvValCalLog log = new MInvValCalLog(line);
					log.setAD_Org_ID(ioLines[i].getAD_Org_ID());
					log.setLine(lineNo * 10);
					log.setM_InOutLine_ID(ioLines[i].getM_InOutLine_ID());
					log.setMovementDate(ioLines[i].getM_InOut().getMovementDate());
					log.setMovementType(ioLines[i].getM_InOut().getMovementType());

					//Set MacthPO Info to Log
					log.setM_MatchPO_ID(matchPos[j].get_ID());
					log.setQty(matchPos[j].getQty());

					//Set Purchase Order Info to Log
					JPiereInvValUtil.copyInfoFromOrderLineToLog(log, orderLine);

					//Set Information to fields that Reference of Fields Group in Window
					log.setJP_CurrencyTo_ID(m_InvValCal.getC_Currency_ID());
					if(orderLine.getC_Currency_ID() != m_InvValCal.getC_Currency_ID())
					{
						BigDecimal rate =MConversionRate.getRate(orderLine.getC_Currency_ID(), m_InvValCal.getC_Currency_ID(), orderLine.getDateOrdered(),
								orderLine.getC_Order().getC_ConversionType_ID(), orderLine.getAD_Client_ID(), orderLine.getAD_Org_ID());
						if(rate == null)
						{
							throw new AdempiereException(Msg.getMsg(getCtx(), MConversionRateUtil.getErrorMessage(getCtx(), "ErrorConvertingCurrencyToBaseCurrency",
									orderLine.getC_Currency_ID(), m_InvValCal.getC_Currency_ID(), m_InvValProfile.getC_ConversionType_ID(), m_InvValCal.getDateValue(), get_TrxName())));
						}
						log.setMultiplyRate(rate);
					}else{
						log.setMultiplyRate(Env.ONE);
					}

					log.setJP_ExchangedAmt(log.getLineNetAmt().multiply(log.getMultiplyRate()).setScale(m_Currency.getCostingPrecision() ,RoundingMode.HALF_UP));

					//Adjust Tax
					if(log.isTaxIncluded())
					{
						log.setJP_ExchangedNoTaxAmt(log.getJP_ExchangedAmt().subtract(log.calculateTax(MInvValProfile.JP_APPLYAMTLIST_PurchaseOrder))
														.setScale(m_Currency.getCostingPrecision() ,RoundingMode.HALF_UP));

					}else{
						log.setJP_ExchangedNoTaxAmt(log.getJP_ExchangedAmt());
					}

					log.setJP_ExchangedPriceActual(log.getJP_ExchangedNoTaxAmt().divide(log.getQtyOrdered(),m_Currency.getCostingPrecision() ,RoundingMode.HALF_UP));

					if(qtyBook.compareTo(log.getQty()) >= 0)
					{
						log.setJP_ApplyQty(log.getQty());
						if(log.getQty().compareTo(log.getQtyOrdered())==0)
							log.setJP_ApplyAmt(log.getJP_ExchangedNoTaxAmt());
						else
							log.setJP_ApplyAmt(log.getJP_ExchangedPriceActual().multiply(log.getQty()).setScale(m_Currency.getCostingPrecision() ,RoundingMode.HALF_UP));
						qtyBook = qtyBook.subtract(log.getQty());
					}else{
						log.setJP_ApplyQty(qtyBook);
						log.setJP_ApplyAmt(qtyBook.multiply(log.getJP_ExchangedPriceActual()).setScale(m_Currency.getCostingPrecision() ,RoundingMode.HALF_UP));
						qtyBook = Env.ZERO;
					}

					log.saveEx(get_TrxName());
					lineNo++;

					if(qtyBook.signum() > 0)
					{
						continue;
					}else{
						break;	//Go Out form Loop j
					}
				}//for j

				if(qtyBook.signum() > 0)
				{
					continue;
				}else{
					break;//Go Out form Loop i
				}

			}else if(m_InvValProfile.getJP_ApplyAmtList().equals(MInvValProfile.JP_APPLYAMTLIST_InvoiceVendor)){//TODO

				I_C_InvoiceLine invoiceLine =null;
				MInvoice invoice = null;
				for(int j = 0; j < matchInvs.length; j++)
				{
					//Check create Log or not
					if(matchInvs[j].getDateAcct().compareTo(m_InvValCal.getDateValue()) > 0)
						continue;

					invoiceLine = matchInvs[j].getC_InvoiceLine();

					if(invoiceLine.getQtyInvoiced().compareTo(Env.ZERO)==0)
						continue;

					invoice = MInvoice.get(getCtx(),invoiceLine.getC_Invoice_ID());
					if(!(invoice.getDocStatus().equals(DocAction.STATUS_Completed) || invoice.getDocStatus().equals(DocAction.STATUS_Closed)))
						continue;

					//Create Inventory Calculation Log
					MInvValCalLog log = new MInvValCalLog(line);
					log.setAD_Org_ID(ioLines[i].getAD_Org_ID());
					log.setLine(lineNo * 10);
					log.setM_InOutLine_ID(ioLines[i].getM_InOutLine_ID());
					log.setMovementDate(ioLines[i].getM_InOut().getMovementDate());
					log.setMovementType(ioLines[i].getM_InOut().getMovementType());

					//Set MacthInv Info to Log
					log.setM_MatchInv_ID(matchInvs[j].get_ID());
					log.setQty(matchInvs[j].getQty());

					//Set AP Invoice Info to log
					invoiceLine = matchInvs[j].getC_InvoiceLine();
					JPiereInvValUtil.copyInfoFromInvoiceLineToLog(log, invoiceLine);

					//Set Information to fields that Reference of Fields Group in Window
					log.setJP_CurrencyTo_ID(m_InvValCal.getC_Currency_ID());
					if(invoiceLine.getC_Invoice().getC_Currency_ID() != m_InvValCal.getC_Currency_ID())
					{
						BigDecimal rate =MConversionRate.getRate(invoiceLine.getC_Invoice().getC_Currency_ID(), m_InvValCal.getC_Currency_ID(), invoiceLine.getC_Invoice().getDateOrdered(),
								invoiceLine.getC_Invoice().getC_ConversionType_ID(), invoiceLine.getAD_Client_ID(), invoiceLine.getAD_Org_ID());
						if(rate == null)
						{
							throw new AdempiereException(Msg.getMsg(getCtx(), MConversionRateUtil.getErrorMessage(getCtx(), "ErrorConvertingCurrencyToBaseCurrency",
									invoiceLine.getC_Invoice().getC_Currency_ID(), m_InvValCal.getC_Currency_ID(), m_InvValProfile.getC_ConversionType_ID(), m_InvValCal.getDateValue(), get_TrxName())));
						}
						log.setMultiplyRate(rate);
					}else{
						log.setMultiplyRate(Env.ONE);
					}

					log.setJP_ExchangedAmt(log.getLineNetAmt().multiply(log.getMultiplyRate()).setScale(m_Currency.getCostingPrecision(), RoundingMode.HALF_UP));

					//Adjust Tax
					if(log.isTaxIncluded())
					{
						log.setJP_ExchangedNoTaxAmt(log.getJP_ExchangedAmt().subtract(log.calculateTax(MInvValProfile.JP_APPLYAMTLIST_InvoiceVendor))
																					.setScale(m_Currency.getCostingPrecision(), RoundingMode.HALF_UP));
					}else{
						log.setJP_ExchangedNoTaxAmt(log.getJP_ExchangedAmt());
					}

					log.setJP_ExchangedPriceActual(log.getJP_ExchangedNoTaxAmt().divide(log.getQtyInvoiced(), m_Currency.getCostingPrecision() ,RoundingMode.HALF_UP));

					if(qtyBook.compareTo(log.getQty()) >= 0)
					{
						log.setJP_ApplyQty(log.getQty());
						if(log.getQty().compareTo(log.getQtyOrdered())==0)
							log.setJP_ApplyAmt(log.getJP_ExchangedNoTaxAmt());
						else
							log.setJP_ApplyAmt(log.getJP_ExchangedPriceActual().multiply(log.getQty()).setScale(m_Currency.getCostingPrecision(), RoundingMode.HALF_UP));
						qtyBook = qtyBook.subtract(log.getQty());
					}else{
						log.setJP_ApplyQty(qtyBook);
						log.setJP_ApplyAmt(qtyBook.multiply(log.getJP_ExchangedPriceActual()).setScale(m_Currency.getCostingPrecision(), RoundingMode.HALF_UP));
						qtyBook = Env.ZERO;
					}

					log.saveEx(get_TrxName());
					lineNo++;

					if(qtyBook.signum() > 0)
					{
						continue;
					}else{
						break;	//Go Out form Loop j
					}
				}//for j

				if(qtyBook.signum() > 0)
				{
					continue;
				}else{
					break;//Go Out from Loop i
				}

			}else{

				MInvValCalLog log = new MInvValCalLog(line);
				log.setAD_Org_ID(ioLines[i].getAD_Org_ID());
				log.setLine(lineNo * 10);
				log.setM_InOutLine_ID(ioLines[i].getM_InOutLine_ID());
				log.setMovementDate(ioLines[i].getM_InOut().getMovementDate());
				log.setMovementType(ioLines[i].getM_InOut().getMovementType());

				log.setDescription("Can not Calculate");
				log.saveEx(get_TrxName());
				return ;
			}

		}//for i

		//Beginning Inventory
		if(qtyBook.signum() > 0)
		{
			MInvValCalLog log = new MInvValCalLog(line);
			log.setLine(lineNo * 10);
			log.setC_Currency_ID(m_InvValCal.getC_Currency_ID());
			log.setJP_CurrencyTo_ID(m_InvValCal.getC_Currency_ID());
			log.setJP_ApplyQty(qtyBook);

			//Get Beginning Inventory Cost From beginInvValCalLine at First
			MInvValCalLine beginInvValCalLine = MInvValCalLine.getBeginInvValCalLine(line);
			if(beginInvValCalLine != null)
			{
				line.setJP_BeginInvValCalLine_ID(beginInvValCalLine.getJP_InvValCalLine_ID());

				log.setJP_ExchangedPriceActual(beginInvValCalLine.getJP_InvValAmt().setScale(m_Currency.getCostingPrecision() ,RoundingMode.HALF_UP));
				log.setJP_ApplyAmt(qtyBook.multiply(beginInvValCalLine.getJP_InvValAmt().setScale(m_Currency.getCostingPrecision() ,RoundingMode.HALF_UP)));
				log.setDescription(Msg.getElement(getCtx(), "JP_ExchangedPriceActual")  + " = " + Msg.getElement(getCtx(), "JP_BeginInvValCalLine_ID"));
				log.setJP_BeginInvValCalLine_ID(beginInvValCalLine.getJP_InvValCalLine_ID());

			}else {

				//Get  Beginning Inventory Cost From Product Price at second
				MProductPrice m_ProductPrice = null;

				if(m_PriceListVersionOfLastDateValue != null)
				{
					m_ProductPrice = MProductPrice.get(getCtx(), m_PriceListVersionOfLastDateValue.getM_PriceList_Version_ID(), line.getM_Product_ID(), get_TrxName());
				}

				if(m_ProductPrice != null)
				{
					log.setJP_ExchangedPriceActual(m_ProductPrice.getPriceStd().setScale(m_Currency.getCostingPrecision() ,RoundingMode.HALF_UP));
					log.setJP_ApplyAmt(qtyBook.multiply(m_ProductPrice.getPriceStd().setScale(m_Currency.getCostingPrecision() ,RoundingMode.HALF_UP)));
					log.setDescription(Msg.getElement(getCtx(), "JP_ExchangedPriceActual") + " = " + Msg.getElement(getCtx(), "M_PriceList_ID") + " : "+ m_PriceList.getName() + " - "
																								+ Msg.getElement(getCtx(), "M_PriceList_Version_ID") + " : "+ m_PriceListVersionOfLastDateValue.getName());

				}else {//Get Beginning Inventory Cost From Current Cost at last

					log.setJP_ExchangedPriceActual(line.getCurrentCostPrice().setScale(m_Currency.getCostingPrecision() ,RoundingMode.HALF_UP));
					log.setJP_ApplyAmt(qtyBook.multiply(line.getCurrentCostPrice()).setScale(m_Currency.getCostingPrecision() ,RoundingMode.HALF_UP));
					log.setDescription(Msg.getElement(getCtx(), "JP_ExchangedPriceActual")  + " = " + Msg.getElement(getCtx(), "CurrentCostPrice"));

				}
			}

			log.setIsTaxIncluded(false);
			log.saveEx(get_TrxName());

		}else{
			;
		}

		BigDecimal JP_InvValTotalAmt = JPiereInvValUtil.calculateInvValTotalAmt(getCtx(), line.get_ID(), get_TrxName());
		JP_InvValTotalAmt = JP_InvValTotalAmt.setScale(m_Currency.getStdPrecision(), RoundingMode.HALF_UP);
		if(line.getQtyBook().compareTo(Env.ZERO) >= 0)
		{
			line.setJP_InvValTotalAmt(JP_InvValTotalAmt);
			if(JP_InvValTotalAmt.compareTo(Env.ZERO)==0)
			{
				line.setJP_InvValAmt(Env.ZERO);
			}else{
				line.setJP_InvValAmt(JP_InvValTotalAmt.divide(line.getQtyBook(), m_Currency.getCostingPrecision() ,RoundingMode.HALF_UP));
			}
		}else{

			if(JP_InvValTotalAmt.compareTo(Env.ZERO)==0)
			{
				line.setJP_InvValTotalAmt(Env.ZERO);
				line.setJP_InvValAmt(Env.ZERO);
			}else{
				line.setJP_InvValTotalAmt(JP_InvValTotalAmt.negate());
				line.setJP_InvValAmt(JP_InvValTotalAmt.divide(line.getQtyBook().abs(), m_Currency.getCostingPrecision() ,RoundingMode.HALF_UP));
			}
		}

		line.saveEx(get_TrxName());

	}//calculate_Fifo

	/**
	 *
	 * Calculate LIFO
	 *
	 * @param line
	 */
	private void calculate_Lifo(MInvValCalLine line)
	{
		BigDecimal qtyBook = line.getQtyBook().abs();//abs is for nagative Inventory.
		MInOutLine[] ioLines = JPiereInvValUtil.getInOutLines(getCtx(),line.getM_Product_ID(), m_InvValCal.getJP_LastDateValue()
									, m_InvValCal.getDateValue(), m_InvValProfile.getOrgs(), "io.MovementDate ASC, io.DocumentNo ASC, io.M_InOut_ID ASC, iol.Line ASC, iol.M_InOutLine_ID ASC");

		int lineNo = 1;
		for(int i = 0; i < ioLines.length; i++)
		{
			/**Check**/
			//ignore nagative inventory.
			if((ioLines[i].getParent().getC_DocType().getDocBaseType().equals(MDocType.DOCBASETYPE_MaterialReceipt) && ioLines[i].getMovementQty().compareTo(Env.ZERO) <= 0)
					|| ioLines[i].getParent().getC_DocType().getDocBaseType().equals(MDocType.DOCBASETYPE_MaterialDelivery))
				continue;

			if(m_InvValProfile.getJP_ApplyAmtList().equals(MInvValProfile.JP_APPLYAMTLIST_PurchaseOrder))//TODO
			{
				MMatchPO[] matchPos = JPiereInvValUtil.getMatchPOs(getCtx(), ioLines[i].getM_InOutLine_ID()," DateAcct DESC, M_MatchPO_ID DESC", get_TrxName());
				I_C_OrderLine orderLine =null;
				MOrder order = null;
				for(int j = 0; j < matchPos.length; j++)
				{
					//Check create Log or not
					if(matchPos[j].getDateAcct().compareTo(m_InvValCal.getDateValue()) > 0)
						continue;

					orderLine = matchPos[j].getC_OrderLine();

					if(orderLine.getQtyOrdered().compareTo(Env.ZERO)==0)
						continue;

					order = JPiereInvValUtil.getMOrder(getCtx(),orderLine.getC_Order_ID());
					if(!(order.getDocStatus().equals(DocAction.STATUS_Completed) || order.getDocStatus().equals(DocAction.STATUS_Closed)))
						continue;

					//Create Inventory Calculation Log
					MInvValCalLog log = new MInvValCalLog(line);
					log.setAD_Org_ID(ioLines[i].getAD_Org_ID());
					log.setLine(lineNo * 10);
					log.setM_InOutLine_ID(ioLines[i].getM_InOutLine_ID());
					log.setMovementDate(ioLines[i].getM_InOut().getMovementDate());
					log.setMovementType(ioLines[i].getM_InOut().getMovementType());


					//Set MacthPO Info to Log
					log.setM_MatchPO_ID(matchPos[j].get_ID());
					log.setQty(matchPos[j].getQty());

					//Set Purchase Order Info to Log
					JPiereInvValUtil.copyInfoFromOrderLineToLog(log, orderLine);

					//Set Information to fields that Reference of Fields Group in Window
					log.setJP_CurrencyTo_ID(m_InvValCal.getC_Currency_ID());
					if(orderLine.getC_Currency_ID() != m_InvValCal.getC_Currency_ID())
					{
						BigDecimal rate =MConversionRate.getRate(orderLine.getC_Currency_ID(), m_InvValCal.getC_Currency_ID(), orderLine.getDateOrdered(),
								orderLine.getC_Order().getC_ConversionType_ID(), orderLine.getAD_Client_ID(), orderLine.getAD_Org_ID());
						if(rate == null)
						{
							throw new AdempiereException(Msg.getMsg(getCtx(), MConversionRateUtil.getErrorMessage(getCtx(), "ErrorConvertingCurrencyToBaseCurrency",
									orderLine.getC_Currency_ID(), m_InvValCal.getC_Currency_ID(), m_InvValProfile.getC_ConversionType_ID(), m_InvValCal.getDateValue(), get_TrxName())));
						}
						log.setMultiplyRate(rate);
					}else{
						log.setMultiplyRate(Env.ONE);
					}

					log.setJP_ExchangedAmt(log.getLineNetAmt().multiply(log.getMultiplyRate()).setScale(m_Currency.getCostingPrecision() ,RoundingMode.HALF_UP));

					//Adjust Tax
					if(log.isTaxIncluded())
					{
						log.setJP_ExchangedNoTaxAmt(log.getJP_ExchangedAmt().subtract(log.calculateTax(MInvValProfile.JP_APPLYAMTLIST_PurchaseOrder))
																							.setScale(m_Currency.getCostingPrecision() ,RoundingMode.HALF_UP));

					}else{
						log.setJP_ExchangedNoTaxAmt(log.getJP_ExchangedAmt());
					}

					log.setJP_ExchangedPriceActual(log.getJP_ExchangedNoTaxAmt().divide(log.getQtyOrdered(), m_Currency.getCostingPrecision() ,RoundingMode.HALF_UP));

					if(qtyBook.compareTo(log.getQty()) >= 0)
					{
						log.setJP_ApplyQty(log.getQty());
						if(log.getQty().compareTo(log.getQtyOrdered())==0)
							log.setJP_ApplyAmt(log.getJP_ExchangedNoTaxAmt());
						else
							log.setJP_ApplyAmt(log.getJP_ExchangedPriceActual().multiply(log.getQty()).setScale(m_Currency.getCostingPrecision() ,RoundingMode.HALF_UP));
						qtyBook = qtyBook.subtract(log.getQty());
					}else{
						log.setJP_ApplyQty(qtyBook);
						log.setJP_ApplyAmt(qtyBook.multiply(log.getJP_ExchangedPriceActual()).setScale(m_Currency.getCostingPrecision() ,RoundingMode.HALF_UP));
						qtyBook = Env.ZERO;
					}

					log.saveEx(get_TrxName());
					lineNo++;

					if(qtyBook.signum() > 0)
					{
						continue;
					}else{
						break;	//Go Out form Loop j
					}
				}//for j

				if(qtyBook.signum() > 0)
				{
					continue;
				}else{
					break;//Go Out form Loop i
				}

			}else if(m_InvValProfile.getJP_ApplyAmtList().equals(MInvValProfile.JP_APPLYAMTLIST_InvoiceVendor)){//TODO

				MMatchInv[] matchInvs = JPiereInvValUtil.getMatchInvs(getCtx(), ioLines[i].getM_InOutLine_ID()," DateAcct DESC, M_MatchInv_ID DESC", get_TrxName());
				I_C_InvoiceLine invoiceLine =null;
				MInvoice invoice = null;
				for(int j = 0; j < matchInvs.length; j++)
				{
					//Check create Log or not
					if(matchInvs[j].getDateAcct().compareTo(m_InvValCal.getDateValue()) > 0)
						continue;

					invoiceLine = matchInvs[j].getC_InvoiceLine();

					if(invoiceLine.getQtyInvoiced().compareTo(Env.ZERO)==0)
						continue;

					invoice = MInvoice.get(getCtx(),invoiceLine.getC_Invoice_ID());
					if(!(invoice.getDocStatus().equals(DocAction.STATUS_Completed) || invoice.getDocStatus().equals(DocAction.STATUS_Closed)))
						continue;

					//Create Inventory Calculation Log
					MInvValCalLog log = new MInvValCalLog(line);
					log.setAD_Org_ID(ioLines[i].getAD_Org_ID());
					log.setLine(lineNo * 10);
					log.setM_InOutLine_ID(ioLines[i].getM_InOutLine_ID());
					log.setMovementDate(ioLines[i].getM_InOut().getMovementDate());
					log.setMovementType(ioLines[i].getM_InOut().getMovementType());

					//Set MacthInv Info to Log
					log.setM_MatchInv_ID(matchInvs[j].get_ID());
					log.setQty(matchInvs[j].getQty());

					//Set AP Invoice Info to Log
					invoiceLine = matchInvs[j].getC_InvoiceLine();
					JPiereInvValUtil.copyInfoFromInvoiceLineToLog(log, invoiceLine);

					//Set Information to fields that Reference of Fields Group in Window
					log.setJP_CurrencyTo_ID(m_InvValCal.getC_Currency_ID());
					if(invoiceLine.getC_Invoice().getC_Currency_ID() != m_InvValCal.getC_Currency_ID())
					{
						BigDecimal rate =MConversionRate.getRate(invoiceLine.getC_Invoice().getC_Currency_ID(), m_InvValCal.getC_Currency_ID(), invoiceLine.getC_Invoice().getDateOrdered(),
								invoiceLine.getC_Invoice().getC_ConversionType_ID(), invoiceLine.getAD_Client_ID(), invoiceLine.getAD_Org_ID());
						if(rate == null)
						{
							throw new AdempiereException(Msg.getMsg(getCtx(), MConversionRateUtil.getErrorMessage(getCtx(), "ErrorConvertingCurrencyToBaseCurrency",
									invoiceLine.getC_Invoice().getC_Currency_ID(), m_InvValCal.getC_Currency_ID(), m_InvValProfile.getC_ConversionType_ID(), m_InvValCal.getDateValue(), get_TrxName())));
						}
						log.setMultiplyRate(rate);
					}else{
						log.setMultiplyRate(Env.ONE);
					}

					log.setJP_ExchangedAmt(log.getLineNetAmt().multiply(log.getMultiplyRate()).setScale(m_Currency.getCostingPrecision() ,RoundingMode.HALF_UP));

					//Adjust Tax
					if(log.isTaxIncluded())
					{
						log.setJP_ExchangedNoTaxAmt(log.getJP_ExchangedAmt().subtract(log.calculateTax(MInvValProfile.JP_APPLYAMTLIST_InvoiceVendor))
																					.setScale(m_Currency.getCostingPrecision() ,RoundingMode.HALF_UP));
					}else{
						log.setJP_ExchangedNoTaxAmt(log.getJP_ExchangedAmt());
					}

					log.setJP_ExchangedPriceActual(log.getJP_ExchangedNoTaxAmt().divide(log.getQtyInvoiced(), m_Currency.getCostingPrecision() ,RoundingMode.HALF_UP));

					if(qtyBook.compareTo(log.getQty()) >= 0)
					{
						log.setJP_ApplyQty(log.getQty());
						if(log.getQty().compareTo(log.getQtyOrdered())==0)
							log.setJP_ApplyAmt(log.getJP_ExchangedNoTaxAmt());
						else
							log.setJP_ApplyAmt(log.getJP_ExchangedPriceActual().multiply(log.getQty()).setScale(m_Currency.getCostingPrecision() ,RoundingMode.HALF_UP));
						qtyBook = qtyBook.subtract(log.getQty());
					}else{
						log.setJP_ApplyQty(qtyBook);
						log.setJP_ApplyAmt(qtyBook.multiply(log.getJP_ExchangedPriceActual()).setScale(m_Currency.getCostingPrecision() ,RoundingMode.HALF_UP));
						qtyBook = Env.ZERO;
					}

					log.saveEx(get_TrxName());
					lineNo++;

					if(qtyBook.signum() > 0)
					{
						continue;
					}else{
						break;	//Go Out form Loop j
					}
				}//for j

				if(qtyBook.signum() > 0)
				{
					continue;
				}else{
					break;//Go Out form Loop i
				}

			}else{

				MInvValCalLog log = new MInvValCalLog(line);
				log.setAD_Org_ID(ioLines[i].getAD_Org_ID());
				log.setLine(lineNo * 10);
				log.setM_InOutLine_ID(ioLines[i].getM_InOutLine_ID());
				log.setMovementDate(ioLines[i].getM_InOut().getMovementDate());
				log.setMovementType(ioLines[i].getM_InOut().getMovementType());
				log.setDescription("Can not Calculate");
				log.saveEx(get_TrxName());
				return ;
			}

		}//for i

		//Beginning Inventory
		if(qtyBook.signum() > 0)
		{
			MInvValCalLog log = new MInvValCalLog(line);
			log.setLine(lineNo * 10);
			log.setC_Currency_ID(m_InvValCal.getC_Currency_ID());
			log.setJP_CurrencyTo_ID(m_InvValCal.getC_Currency_ID());
			log.setJP_ApplyQty(qtyBook);

			//Get Beginning Inventory Cost From beginInvValCalLine at First
			MInvValCalLine beginInvValCalLine = MInvValCalLine.getBeginInvValCalLine(line);
			if(beginInvValCalLine != null)
			{
				line.setJP_BeginInvValCalLine_ID(beginInvValCalLine.getJP_InvValCalLine_ID());

				log.setJP_ExchangedPriceActual(beginInvValCalLine.getJP_InvValAmt().setScale(m_Currency.getCostingPrecision() ,RoundingMode.HALF_UP));
				log.setJP_ApplyAmt(qtyBook.multiply(beginInvValCalLine.getJP_InvValAmt().setScale(m_Currency.getCostingPrecision() ,RoundingMode.HALF_UP)));
				log.setDescription(Msg.getElement(getCtx(), "Cost")  + " = " + Msg.getElement(getCtx(), "JP_BeginInvValCalLine_ID"));
				log.setJP_BeginInvValCalLine_ID(beginInvValCalLine.getJP_InvValCalLine_ID());

			}else {

				//Get Beginning Inventory Cost From Product Price at Second
				MProductPrice m_ProductPrice = null;

				if(m_PriceListVersionOfLastDateValue != null)
				{
					m_ProductPrice = MProductPrice.get(getCtx(), m_PriceListVersionOfLastDateValue.getM_PriceList_Version_ID(), line.getM_Product_ID(), get_TrxName());
				}

				if(m_ProductPrice != null)
				{
					log.setJP_ExchangedPriceActual(m_ProductPrice.getPriceStd().setScale(m_Currency.getCostingPrecision() ,RoundingMode.HALF_UP));
					log.setJP_ApplyAmt(qtyBook.multiply(m_ProductPrice.getPriceStd().setScale(m_Currency.getCostingPrecision() ,RoundingMode.HALF_UP)));
					log.setDescription(Msg.getElement(getCtx(), "Cost")  + " = " + Msg.getElement(getCtx(), "M_PriceList_ID") + " : "+ m_PriceList.getName() + " - "
																								+ Msg.getElement(getCtx(), "M_PriceList_Version_ID") + " : "+ m_PriceListVersionOfLastDateValue.getName());

				}else {//Get Beginning Cost From Current Cost at last

					log.setJP_ExchangedPriceActual(line.getCurrentCostPrice().setScale(m_Currency.getCostingPrecision() ,RoundingMode.HALF_UP));
					log.setJP_ApplyAmt(qtyBook.multiply(line.getCurrentCostPrice()).setScale(m_Currency.getCostingPrecision() ,RoundingMode.HALF_UP));
					log.setDescription(Msg.getElement(getCtx(), "Cost")  + " = " + Msg.getElement(getCtx(), "CurrentCostPrice"));

				}
			}

			log.setIsTaxIncluded(false);
			log.saveEx(get_TrxName());

		}else{
			;
		}

		BigDecimal JP_InvValTotalAmt = JPiereInvValUtil.calculateInvValTotalAmt(getCtx(), line.get_ID(), get_TrxName());
		JP_InvValTotalAmt = JP_InvValTotalAmt.setScale(m_Currency.getStdPrecision(), RoundingMode.HALF_UP);
		if(line.getQtyBook().compareTo(Env.ZERO) >= 0)
		{
			line.setJP_InvValTotalAmt(JP_InvValTotalAmt);
			if(JP_InvValTotalAmt.compareTo(Env.ZERO)==0)
			{
				line.setJP_InvValAmt(Env.ZERO);
			}else{
				line.setJP_InvValAmt(JP_InvValTotalAmt.divide(line.getQtyBook(), m_Currency.getCostingPrecision() ,RoundingMode.HALF_UP));
			}
		}else{

			if(JP_InvValTotalAmt.compareTo(Env.ZERO)==0)
			{
				line.setJP_InvValTotalAmt(Env.ZERO);
				line.setJP_InvValAmt(Env.ZERO);
			}else{
				line.setJP_InvValTotalAmt(JP_InvValTotalAmt.negate());
				line.setJP_InvValAmt(JP_InvValTotalAmt.divide(line.getQtyBook().abs(), m_Currency.getCostingPrecision() ,RoundingMode.HALF_UP));
			}
		}

		line.saveEx(get_TrxName());

	}//calculate_Lifo

	/**
	 *
	 * Calculate Last Purchase Order
	 *
	 * @param line
	 */
	private void calculate_LastPO(MInvValCalLine line)//TODO
	{
		BigDecimal qtyBook = line.getQtyBook().abs();//abs is for nagative Inventory.
		MInOutLine[] ioLines = JPiereInvValUtil.getInOutLines(getCtx(),line.getM_Product_ID(), m_InvValCal.getJP_LastDateValue()
									, m_InvValCal.getDateValue(), m_InvValProfile.getOrgs(), "io.MovementDate DESC, io.DocumentNo DESC, io.M_InOut_ID DESC,  iol.Line DESC, iol.M_InOutLine_ID DESC");

		int lineNo = 10;
		BigDecimal JP_InvValAmt = Env.ZERO;
		for(int i = 0; i < ioLines.length; i++)
		{
			/**Check**/
			//ignore nagative inventory.
			if((ioLines[i].getParent().getC_DocType().getDocBaseType().equals(MDocType.DOCBASETYPE_MaterialReceipt) && ioLines[i].getMovementQty().compareTo(Env.ZERO) <= 0)
					|| ioLines[i].getParent().getC_DocType().getDocBaseType().equals(MDocType.DOCBASETYPE_MaterialDelivery))
				continue;

			MMatchPO[] matchPos = JPiereInvValUtil.getMatchPOs(getCtx(), ioLines[i].getM_InOutLine_ID()," DateAcct DESC, M_MatchPO_ID DESC", get_TrxName());
			if(matchPos.length > 0)
				;
			else
				continue;

			I_C_OrderLine orderLine =null;
			MOrder order = null;
			boolean isBreak = false;
			for(int j = 0; j < matchPos.length; j++)
			{
				//Check create Log or not
				if(matchPos[j].getDateAcct().compareTo(m_InvValCal.getDateValue()) > 0)
					continue;

				orderLine = matchPos[j].getC_OrderLine();

				if(orderLine.getQtyOrdered().compareTo(Env.ZERO)==0)
					continue;

				order = JPiereInvValUtil.getMOrder(getCtx(),orderLine.getC_Order_ID());
				if(!(order.getDocStatus().equals(DocAction.STATUS_Completed) || order.getDocStatus().equals(DocAction.STATUS_Closed)))
					continue;

				if(matchPos[j].getQty().compareTo(Env.ZERO) < 0)
					continue;

				//Create Inventory Calculation Log
				MInvValCalLog log = new MInvValCalLog(line);
				log.setAD_Org_ID(ioLines[i].getAD_Org_ID());
				log.setLine(lineNo);
				log.setM_InOutLine_ID(ioLines[i].getM_InOutLine_ID());
				log.setMovementDate(ioLines[i].getM_InOut().getMovementDate());
				log.setMovementType(ioLines[i].getM_InOut().getMovementType());

				//Set MacthPO Info to Log
				log.setM_MatchPO_ID(matchPos[0].get_ID());
				log.setQty(matchPos[0].getQty());

				//Set Purchase Order Info to Log
				orderLine = matchPos[0].getC_OrderLine();
				JPiereInvValUtil.copyInfoFromOrderLineToLog(log, orderLine);

				//Set Information to fields that Reference of Fields Group in Window
				log.setJP_CurrencyTo_ID(m_InvValCal.getC_Currency_ID());
				if(orderLine.getC_Currency_ID() != m_InvValCal.getC_Currency_ID())
				{
					BigDecimal rate =MConversionRate.getRate(orderLine.getC_Currency_ID(), m_InvValCal.getC_Currency_ID(), orderLine.getDateOrdered(),
							orderLine.getC_Order().getC_ConversionType_ID(), orderLine.getAD_Client_ID(), orderLine.getAD_Org_ID());
					if(rate == null)
					{
						throw new AdempiereException(Msg.getMsg(getCtx(), MConversionRateUtil.getErrorMessage(getCtx(), "ErrorConvertingCurrencyToBaseCurrency",
								orderLine.getC_Currency_ID(), m_InvValCal.getC_Currency_ID(), m_InvValProfile.getC_ConversionType_ID(), m_InvValCal.getDateValue(), get_TrxName())));
					}
					log.setMultiplyRate(rate);
				}else{
					log.setMultiplyRate(Env.ONE);
				}

				log.setJP_ExchangedAmt(log.getLineNetAmt().multiply(log.getMultiplyRate()).setScale(m_Currency.getCostingPrecision(), RoundingMode.HALF_UP));

				//Adjust Tax
				if(log.isTaxIncluded())
				{
					log.setJP_ExchangedNoTaxAmt(log.getJP_ExchangedAmt().subtract(log.calculateTax(MInvValProfile.JP_APPLYAMTLIST_PurchaseOrder))
																						.setScale(m_Currency.getCostingPrecision(), RoundingMode.HALF_UP));

				}else{
					log.setJP_ExchangedNoTaxAmt(log.getJP_ExchangedAmt());
				}

				log.setJP_ExchangedPriceActual(log.getJP_ExchangedNoTaxAmt().divide(log.getQtyOrdered(), m_Currency.getCostingPrecision() ,RoundingMode.HALF_UP));

				if(qtyBook.compareTo(log.getQty()) >= 0)
				{
					log.setJP_ApplyQty(log.getQty());
					if(log.getQty().compareTo(log.getQtyOrdered())==0)
						log.setJP_ApplyAmt(log.getJP_ExchangedNoTaxAmt());
					else
						log.setJP_ApplyAmt(log.getJP_ExchangedPriceActual().multiply(log.getQty()).setScale(m_Currency.getCostingPrecision(), RoundingMode.HALF_UP));
					qtyBook = qtyBook.subtract(log.getQty());
				}else{
					log.setJP_ApplyQty(qtyBook);
					log.setJP_ApplyAmt(qtyBook.multiply(log.getJP_ExchangedPriceActual()).setScale(m_Currency.getCostingPrecision(), RoundingMode.HALF_UP));
					qtyBook = Env.ZERO;
				}

				log.saveEx(get_TrxName());
				JP_InvValAmt = log.getJP_ExchangedPriceActual();
				isBreak = true;
				break;
			}

			if(isBreak)
				break;

		}//for(int i = 0; i < ioLines.length; i++)

		if(JP_InvValAmt.compareTo(Env.ZERO)==0)
			line.setJP_InvValAmt(line.getCurrentCostPrice().setScale(m_Currency.getCostingPrecision(), RoundingMode.HALF_UP));
		else
			line.setJP_InvValAmt(JP_InvValAmt);

		line.setJP_InvValTotalAmt(line.getQtyBook().multiply(line.getJP_InvValAmt()).setScale(m_Currency.getStdPrecision(), RoundingMode.HALF_UP));

		line.saveEx(get_TrxName());

	}//calculate_LastPO

	/**
	 *
	 * Calculate Last Invoice
	 *
	 * @param line
	 */
	private void calculate_LastInvoice(MInvValCalLine line)//TODO
	{
		BigDecimal qtyBook = line.getQtyBook().abs();//abs is for nagative Inventory.
		MInOutLine[] ioLines = JPiereInvValUtil.getInOutLines(getCtx(),line.getM_Product_ID(), m_InvValCal.getJP_LastDateValue()
									, m_InvValCal.getDateValue(), m_InvValProfile.getOrgs(), "io.MovementDate DESC, io.DocumentNo DESC, io.M_InOut_ID DESC, iol.Line DESC, iol.M_InOutLine_ID DESC");

		int lineNo = 10;
		BigDecimal JP_InvValAmt = Env.ZERO;
		for(int i = 0; i < ioLines.length; i++)
		{
			/**Check**/
			//ignore nagative inventory.
			if((ioLines[i].getParent().getC_DocType().getDocBaseType().equals(MDocType.DOCBASETYPE_MaterialReceipt) && ioLines[i].getMovementQty().compareTo(Env.ZERO) <= 0)
					|| ioLines[i].getParent().getC_DocType().getDocBaseType().equals(MDocType.DOCBASETYPE_MaterialDelivery))
				continue;

			MMatchInv[] matchInvs = JPiereInvValUtil.getMatchInvs(getCtx(), ioLines[i].getM_InOutLine_ID()," DateAcct DESC, M_MatchInv_ID DESC", get_TrxName());
			if(matchInvs == null || matchInvs.length < 1 )
				continue;


			I_C_InvoiceLine invoiceLine =null;
			MInvoice invoice = null;
			boolean isBreak = false;
			for(int j = 0; j < matchInvs.length; j++)
			{
				//Check create Log or not
				if(matchInvs[j].getDateAcct().compareTo(m_InvValCal.getDateValue()) > 0)
					continue;

				invoiceLine = matchInvs[j].getC_InvoiceLine();

				if(invoiceLine.getQtyInvoiced().compareTo(Env.ZERO)==0)
					continue;

				invoice = MInvoice.get(getCtx(),invoiceLine.getC_Invoice_ID());
				if(!(invoice.getDocStatus().equals(DocAction.STATUS_Completed) || invoice.getDocStatus().equals(DocAction.STATUS_Closed)))
					continue;

				if(matchInvs[j].getQty().compareTo(Env.ZERO) < 0)
					continue;

				//Create Inventory Calculation Log
				MInvValCalLog log = new MInvValCalLog(line);
				log.setAD_Org_ID(ioLines[i].getAD_Org_ID());
				log.setLine(lineNo);
				log.setM_InOutLine_ID(ioLines[i].getM_InOutLine_ID());
				log.setMovementDate(ioLines[i].getM_InOut().getMovementDate());
				log.setMovementType(ioLines[i].getM_InOut().getMovementType());

				//Set MacthINv Info to Log
				log.setM_MatchInv_ID(matchInvs[j].get_ID());
				log.setQty(matchInvs[j].getQty());

				//Set PO Info
				JPiereInvValUtil.copyInfoFromInvoiceLineToLog(log, invoiceLine);

				//Set Information to fields that Reference of Fields Group in Window
				log.setJP_CurrencyTo_ID(m_InvValCal.getC_Currency_ID());
				if(invoiceLine.getC_Invoice().getC_Currency_ID() != m_InvValCal.getC_Currency_ID())
				{
					BigDecimal rate =MConversionRate.getRate(invoiceLine.getC_Invoice().getC_Currency_ID(), m_InvValCal.getC_Currency_ID()
							,invoiceLine.getC_Invoice().getDateInvoiced(), invoiceLine.getC_Invoice().getC_ConversionType_ID(), invoiceLine.getAD_Client_ID(), invoiceLine.getAD_Org_ID());
					if(rate == null)
					{
						throw new AdempiereException(Msg.getMsg(getCtx(), MConversionRateUtil.getErrorMessage(getCtx(), "ErrorConvertingCurrencyToBaseCurrency",
								invoiceLine.getC_Invoice().getC_Currency_ID(), m_InvValCal.getC_Currency_ID(), m_InvValProfile.getC_ConversionType_ID(), m_InvValCal.getDateValue(), get_TrxName())));
					}
					log.setMultiplyRate(rate);
				}else{
					log.setMultiplyRate(Env.ONE);
				}

				log.setJP_ExchangedAmt(log.getLineNetAmt().multiply(log.getMultiplyRate()).setScale(m_Currency.getCostingPrecision(), RoundingMode.HALF_UP));

				//Adjust Tax
				if(log.isTaxIncluded())
				{
					log.setJP_ExchangedNoTaxAmt(log.getJP_ExchangedAmt().subtract(log.calculateTax(MInvValProfile.JP_APPLYAMTLIST_InvoiceVendor))
																				.setScale(m_Currency.getCostingPrecision(), RoundingMode.HALF_UP));

				}else{
					log.setJP_ExchangedNoTaxAmt(log.getJP_ExchangedAmt());
				}

				log.setJP_ExchangedPriceActual(log.getJP_ExchangedNoTaxAmt().divide(log.getQtyInvoiced(), m_Currency.getCostingPrecision() ,RoundingMode.HALF_UP));

				if(qtyBook.compareTo(log.getQty()) >= 0)
				{
					log.setJP_ApplyQty(log.getQty());
					if(log.getQty().compareTo(log.getQtyOrdered())==0)
						log.setJP_ApplyAmt(log.getJP_ExchangedNoTaxAmt());
					else
						log.setJP_ApplyAmt(log.getJP_ExchangedPriceActual().multiply(log.getQty()).setScale(m_Currency.getCostingPrecision(), RoundingMode.HALF_UP));
					qtyBook = qtyBook.subtract(log.getQty());
				}else{
					log.setJP_ApplyQty(qtyBook);
					log.setJP_ApplyAmt(qtyBook.multiply(log.getJP_ExchangedPriceActual()).setScale(m_Currency.getCostingPrecision(), RoundingMode.HALF_UP));
					qtyBook = Env.ZERO;
				}

				log.saveEx(get_TrxName());
				JP_InvValAmt = log.getJP_ExchangedPriceActual();
				isBreak = true;
				break;
			}

			if(isBreak)
				break;
		}

		if(JP_InvValAmt.compareTo(Env.ZERO)==0)
			line.setJP_InvValAmt(line.getCurrentCostPrice().setScale(m_Currency.getCostingPrecision(), RoundingMode.HALF_UP));
		else
			line.setJP_InvValAmt(JP_InvValAmt);

		line.setJP_InvValTotalAmt(line.getQtyBook().multiply(line.getJP_InvValAmt()).setScale(m_Currency.getStdPrecision(), RoundingMode.HALF_UP));

		line.saveEx(get_TrxName());

	}//calculate_LastInvoice

	/**
	 *
	 * Calculate Average Purchase Order
	 *
	 * @param line
	 */
	private void calculate_AveragePO(MInvValCalLine line)//TODO
	{
		MInOutLine[] ioLines = JPiereInvValUtil.getInOutLines(getCtx(),line.getM_Product_ID(), m_InvValCal.getJP_LastDateValue()
									, m_InvValCal.getDateValue(), m_InvValProfile.getOrgs(), "io.MovementDate DESC, io.DocumentNo DESC, io.M_InOut_ID DESC, iol.Line DESC, iol.M_InOutLine_ID DESC");

		int lineNo = 1;
		MMatchPO[] matchPos = null;
		for(int i = 0; i < ioLines.length; i++)
		{
			/**Check**/
			//ignore nagative inventory.
			if((ioLines[i].getParent().getC_DocType().getDocBaseType().equals(MDocType.DOCBASETYPE_MaterialReceipt) && ioLines[i].getMovementQty().compareTo(Env.ZERO) <= 0)
					|| ioLines[i].getParent().getC_DocType().getDocBaseType().equals(MDocType.DOCBASETYPE_MaterialDelivery))
				continue;

			matchPos = JPiereInvValUtil.getMatchPOs(getCtx(), ioLines[i].getM_InOutLine_ID()," DateAcct DESC, M_MatchPO_ID DESC", get_TrxName());
			if(matchPos == null || matchPos.length < 1 )
					continue;


			/**Calculate**/
			I_C_OrderLine orderLine =null;
			MOrder order = null;
			for(int j = 0; j < matchPos.length; j++)
			{
				//Check create Log or not
				if(matchPos[j].getDateAcct().compareTo(m_InvValCal.getDateValue()) > 0)
					continue;

				orderLine = matchPos[j].getC_OrderLine();

				if(orderLine.getQtyOrdered().compareTo(Env.ZERO)==0)
					continue;

				order = JPiereInvValUtil.getMOrder(getCtx(),orderLine.getC_Order_ID());
				if(!(order.getDocStatus().equals(DocAction.STATUS_Completed) || order.getDocStatus().equals(DocAction.STATUS_Closed)))
					continue;

				//Create Inventory Calculation Log
				MInvValCalLog log = new MInvValCalLog(line);
				log.setAD_Org_ID(ioLines[i].getAD_Org_ID());
				log.setLine(lineNo * 10);
				log.setM_InOutLine_ID(ioLines[i].getM_InOutLine_ID());
				log.setMovementDate(ioLines[i].getM_InOut().getMovementDate());
				log.setMovementType(ioLines[i].getM_InOut().getMovementType());


				//Set MacthPO Info to Log
				log.setM_MatchPO_ID(matchPos[j].get_ID());
				log.setQty(matchPos[j].getQty());

				//Set Purachse Order Info to Log
				orderLine = matchPos[j].getC_OrderLine();
				JPiereInvValUtil.copyInfoFromOrderLineToLog(log, orderLine);

				//Set Information to fields that Reference of Fields Group in Window
				log.setJP_CurrencyTo_ID(m_InvValCal.getC_Currency_ID());
				if(orderLine.getC_Currency_ID() != m_InvValCal.getC_Currency_ID())
				{
					BigDecimal rate =MConversionRate.getRate(orderLine.getC_Currency_ID(), m_InvValCal.getC_Currency_ID(), orderLine.getDateOrdered(),
							orderLine.getC_Order().getC_ConversionType_ID(), orderLine.getAD_Client_ID(), orderLine.getAD_Org_ID());
					if(rate == null)
					{
						throw new AdempiereException(Msg.getMsg(getCtx(), MConversionRateUtil.getErrorMessage(getCtx(), "ErrorConvertingCurrencyToBaseCurrency",
								orderLine.getC_Currency_ID(), m_InvValCal.getC_Currency_ID(), m_InvValProfile.getC_ConversionType_ID(), m_InvValCal.getDateValue(), get_TrxName())));
					}
					log.setMultiplyRate(rate);
				}else{
					log.setMultiplyRate(Env.ONE);
				}

				log.setJP_ExchangedAmt(log.getLineNetAmt().multiply(log.getMultiplyRate()).setScale(m_Currency.getCostingPrecision(), RoundingMode.HALF_UP));

				//Adjust Tax
				if(log.isTaxIncluded())
				{
					log.setJP_ExchangedNoTaxAmt(log.getJP_ExchangedAmt().subtract(log.calculateTax(MInvValProfile.JP_APPLYAMTLIST_PurchaseOrder))
																				.setScale(m_Currency.getCostingPrecision(), RoundingMode.HALF_UP));

				}else{
					log.setJP_ExchangedNoTaxAmt(log.getJP_ExchangedAmt());
				}

				log.setJP_ExchangedPriceActual(log.getJP_ExchangedNoTaxAmt().divide(log.getQtyOrdered(), m_Currency.getCostingPrecision() ,RoundingMode.HALF_UP));
				log.setJP_ApplyQty(log.getQty());
				log.setJP_ApplyAmt(log.getJP_ExchangedPriceActual().multiply(log.getQty()).setScale(m_Currency.getCostingPrecision(), RoundingMode.HALF_UP));
				log.saveEx(get_TrxName());
				lineNo++;

			}//for j

		}//for i


		//Beginning Inventory
		MInvValCalLine beginInvValCalLine = null;
		if(m_InvValProfile.getJP_TypeOfAverageCost().equals(MInvValProfile.JP_TYPEOFAVERAGECOST_GrossAverage))
		{
			beginInvValCalLine = MInvValCalLine.getBeginInvValCalLine(line);
			if(beginInvValCalLine != null)
			{
				line.setJP_BeginInvValCalLine_ID(beginInvValCalLine.getJP_InvValCalLine_ID());

				MInvValCalLog log = new MInvValCalLog(line);
				log.setLine(lineNo * 10);

				log.setC_Currency_ID(m_InvValCal.getC_Currency_ID());
				log.setJP_CurrencyTo_ID(m_InvValCal.getC_Currency_ID());

				log.setJP_ExchangedPriceActual(beginInvValCalLine.getJP_InvValAmt().setScale(m_Currency.getCostingPrecision() ,RoundingMode.HALF_UP));
				log.setJP_ApplyQty(beginInvValCalLine.getQtyBook());
				log.setJP_ApplyAmt(beginInvValCalLine.getJP_InvValTotalAmt().setScale(m_Currency.getCostingPrecision() ,RoundingMode.HALF_UP));
				log.setDescription(Msg.getElement(getCtx(), "JP_ApplyQty") + " = " + Msg.getElement(getCtx(), "JP_BeginInvValCalLine_ID") + " , "
											+ Msg.getElement(getCtx(), "JP_ExchangedPriceActual") + " = " + Msg.getElement(getCtx(), "JP_BeginInvValCalLine_ID") );
				log.setJP_BeginInvValCalLine_ID(beginInvValCalLine.getJP_InvValCalLine_ID());
				log.saveEx(get_TrxName());

			}else if(map_Product_Qty_LastDateValue != null){

				MProductPrice m_ProductPrice = null;
				if(m_PriceListVersionOfLastDateValue != null)
				{
					m_ProductPrice = MProductPrice.get(getCtx(), m_PriceListVersionOfLastDateValue.getM_PriceList_Version_ID(), line.getM_Product_ID(), get_TrxName());
				}

				BigDecimal QtyBook = map_Product_Qty_LastDateValue.get(line.getM_Product_ID());

				if(QtyBook != null)
				{
					MInvValCalLog log = new MInvValCalLog(line);
					log.setLine(lineNo * 10);
					log.setC_Currency_ID(m_InvValCal.getC_Currency_ID());
					log.setJP_CurrencyTo_ID(m_InvValCal.getC_Currency_ID());
					if(m_ProductPrice != null)
					{
						log.setJP_ExchangedPriceActual(m_ProductPrice.getPriceStd().setScale(m_Currency.getCostingPrecision() ,RoundingMode.HALF_UP));
						log.setJP_ApplyAmt(QtyBook.multiply(m_ProductPrice.getPriceStd()).setScale(m_Currency.getCostingPrecision() ,RoundingMode.HALF_UP));
						log.setDescription(Msg.getElement(getCtx(), "JP_ApplyQty") + " = " + Msg.getElement(getCtx(), "QtyBook") + " - " + Msg.getElement(getCtx(), "JP_LastDateValue") + " , "
											+ Msg.getElement(getCtx(), "JP_ExchangedPriceActual") + " = " + Msg.getElement(getCtx(), "M_PriceList_ID") + " : "+ m_PriceList.getName() + " - "
											+ Msg.getElement(getCtx(), "M_PriceList_Version_ID") + " : "+ m_PriceListVersionOfLastDateValue.getName());
					}else {

						log.setJP_ExchangedPriceActual(line.getCurrentCostPrice().setScale(m_Currency.getCostingPrecision() ,RoundingMode.HALF_UP));
						log.setJP_ApplyAmt(QtyBook.multiply(line.getCurrentCostPrice().setScale(m_Currency.getCostingPrecision() ,RoundingMode.HALF_UP)));
						log.setDescription(Msg.getElement(getCtx(), "JP_ApplyQty") + " = " + Msg.getElement(getCtx(), "QtyBook") + " - " + Msg.getElement(getCtx(), "JP_LastDateValue") + " , "
								+ Msg.getElement(getCtx(), "JP_ExchangedPriceActual") + " = " +Msg.getElement(getCtx(), "CurrentCostPrice"));

					}
					log.setJP_ApplyQty(QtyBook);
					log.saveEx(get_TrxName());
				}
			}

		}//MInvValProfile.JP_TYPEOFAVERAGECOST_GrossAverage


		BigDecimal JP_InvValTotalAmt = JPiereInvValUtil.calculateInvValTotalAmt(getCtx(), line.get_ID(), get_TrxName());
		JP_InvValTotalAmt = JP_InvValTotalAmt.setScale(m_Currency.getStdPrecision(), RoundingMode.HALF_UP);
		BigDecimal JP_ApplyQty = JPiereInvValUtil.calculateApplyQty(getCtx(), line.get_ID(), get_TrxName());
		if(JP_ApplyQty.compareTo(Env.ZERO)==0)
		{
			MPriceListVersion m_PriceListVersionOfDateValue = null;
			MProductPrice m_ProductPrice = null;

			if(m_PriceList != null && m_InvValCal.getDateValue() != null)
			{
				m_PriceListVersionOfDateValue = m_PriceList.getPriceListVersion(m_InvValCal.getDateValue());
			}

			if(m_PriceListVersionOfDateValue != null)
			{
				m_ProductPrice = MProductPrice.get(getCtx(), m_PriceListVersionOfDateValue.getM_PriceList_Version_ID(), line.getM_Product_ID(), get_TrxName());
			}

			if(m_ProductPrice != null)
			{
				line.setJP_InvValAmt(m_ProductPrice.getPriceStd().setScale(m_Currency.getCostingPrecision() ,RoundingMode.HALF_UP));

			}else {

				if(beginInvValCalLine != null)
				{
					line.setJP_InvValAmt(beginInvValCalLine.getJP_InvValAmt());

				}else {

					if(m_PriceListVersionOfLastDateValue != null)
					{
						m_ProductPrice = MProductPrice.get(getCtx(), m_PriceListVersionOfLastDateValue.getM_PriceList_Version_ID(), line.getM_Product_ID(), get_TrxName());
					}

					if(m_ProductPrice != null)
					{
						line.setJP_InvValAmt(m_ProductPrice.getPriceStd().setScale(m_Currency.getCostingPrecision() ,RoundingMode.HALF_UP));

					}else {

						line.setJP_InvValAmt(line.getCurrentCostPrice());

					}
				}

			}

		}else{

			line.setJP_InvValAmt(JP_InvValTotalAmt.divide(JP_ApplyQty, m_Currency.getCostingPrecision() ,RoundingMode.HALF_UP));

		}

		if(line.getQtyBook().compareTo(Env.ZERO)==0)
		{
			line.setJP_InvValTotalAmt(Env.ZERO);
		}else{
			line.setJP_InvValTotalAmt(line.getQtyBook().multiply(line.getJP_InvValAmt()).setScale(m_Currency.getStdPrecision(), RoundingMode.HALF_UP));
		}
		line.saveEx(get_TrxName());

	}//calculate_AveragePO

	/**
	 *
	 * Calculate Average Invoice
	 *
	 * @param line
	 */
	private void calculate_AverageInvoice(MInvValCalLine line)//TODO
	{
		MInOutLine[] ioLines = JPiereInvValUtil.getInOutLines(getCtx(),line.getM_Product_ID(), m_InvValCal.getJP_LastDateValue()
									, m_InvValCal.getDateValue(), m_InvValProfile.getOrgs(), "io.MovementDate DESC, io.DocumentNo DESC, io.M_InOut_ID DESC, iol.Line DESC, iol.M_InOutLine_ID DESC");

		int lineNo = 1;
		MMatchInv[] matchInvs = null;
		for(int i = 0; i < ioLines.length; i++)
		{

			/**Check**/
			matchInvs =JPiereInvValUtil.getMatchInvs(getCtx(), ioLines[i].getM_InOutLine_ID()," DateAcct DESC, M_MatchInv_ID DESC", get_TrxName());
			if(matchInvs == null || matchInvs.length < 1 )
					continue;


			/**Calcualte**/
			I_C_InvoiceLine invoiceLine =null;
			MInvoice invoice = null;
			for(int j = 0; j < matchInvs.length; j++)
			{
				//Check create Log or not
				if(matchInvs[j].getDateAcct().compareTo(m_InvValCal.getDateValue()) > 0)
					continue;

				invoiceLine = matchInvs[j].getC_InvoiceLine();

				if(invoiceLine.getQtyInvoiced().compareTo(Env.ZERO)==0)
					continue;

				invoice = MInvoice.get(getCtx(),invoiceLine.getC_Invoice_ID());
				if(!(invoice.getDocStatus().equals(DocAction.STATUS_Completed) || invoice.getDocStatus().equals(DocAction.STATUS_Closed)))
					continue;

				//Create Inventory Calculation Log
				MInvValCalLog log = new MInvValCalLog(line);
				log.setAD_Org_ID(ioLines[i].getAD_Org_ID());
				log.setLine(lineNo * 10);
				log.setM_InOutLine_ID(ioLines[i].getM_InOutLine_ID());
				log.setMovementDate(ioLines[i].getM_InOut().getMovementDate());
				log.setMovementType(ioLines[i].getM_InOut().getMovementType());

				//Set MacthInv Info to Log
				log.setM_MatchInv_ID(matchInvs[j].get_ID());
				if(ioLines[i].getParent().getC_DocType().getDocBaseType().equals(MDocType.DOCBASETYPE_MaterialReceipt))
					log.setQty(matchInvs[j].getQty());
				else if(ioLines[i].getParent().getC_DocType().getDocBaseType().equals(MDocType.DOCBASETYPE_MaterialDelivery))
					log.setQty(matchInvs[j].getQty().negate());

				//Set AP Invoice Info to Log
				JPiereInvValUtil.copyInfoFromInvoiceLineToLog(log, invoiceLine);

				//Set Information to fields that Reference of Fields Group in Window
				log.setJP_CurrencyTo_ID(m_InvValCal.getC_Currency_ID());
				if(invoiceLine.getC_Invoice().getC_Currency_ID() != m_InvValCal.getC_Currency_ID())
				{
					BigDecimal rate =MConversionRate.getRate(invoiceLine.getC_Invoice().getC_Currency_ID(), m_InvValCal.getC_Currency_ID(), invoiceLine.getC_Invoice().getDateOrdered(),
							invoiceLine.getC_Invoice().getC_ConversionType_ID(), invoiceLine.getAD_Client_ID(), invoiceLine.getAD_Org_ID());
					if(rate == null)
					{
						throw new AdempiereException(Msg.getMsg(getCtx(), MConversionRateUtil.getErrorMessage(getCtx(), "ErrorConvertingCurrencyToBaseCurrency",
								invoiceLine.getC_Invoice().getC_Currency_ID(), m_InvValCal.getC_Currency_ID(), m_InvValProfile.getC_ConversionType_ID(), m_InvValCal.getDateValue(), get_TrxName())));
					}
					log.setMultiplyRate(rate);
				}else{
					log.setMultiplyRate(Env.ONE);
				}

				log.setJP_ExchangedAmt(log.getLineNetAmt().multiply(log.getMultiplyRate()).setScale(m_Currency.getStdPrecision(), RoundingMode.HALF_UP));

				//Adjust Tax
				if(log.isTaxIncluded())
				{
					log.setJP_ExchangedNoTaxAmt(log.getJP_ExchangedAmt().subtract(log.calculateTax(MInvValProfile.JP_APPLYAMTLIST_InvoiceVendor))
																						.setScale(m_Currency.getStdPrecision(), RoundingMode.HALF_UP));
				}else{
					log.setJP_ExchangedNoTaxAmt(log.getJP_ExchangedAmt());
				}

				log.setJP_ExchangedPriceActual(log.getJP_ExchangedNoTaxAmt().divide(log.getQtyInvoiced(), m_Currency.getCostingPrecision() ,RoundingMode.HALF_UP));
				log.setJP_ApplyQty(log.getQty());
				log.setJP_ApplyAmt(log.getJP_ExchangedPriceActual().multiply(log.getQty()).setScale(m_Currency.getStdPrecision(), RoundingMode.HALF_UP));
				log.saveEx(get_TrxName());
				lineNo++;

			}//for j

		}//for i

		//Beginning Inventory
		MInvValCalLine beginInvValCalLine = null;
		if(m_InvValProfile.getJP_TypeOfAverageCost().equals(MInvValProfile.JP_TYPEOFAVERAGECOST_GrossAverage))
		{
			beginInvValCalLine = MInvValCalLine.getBeginInvValCalLine(line);
			if(beginInvValCalLine != null)
			{
				line.setJP_BeginInvValCalLine_ID(beginInvValCalLine.getJP_InvValCalLine_ID());

				MInvValCalLog log = new MInvValCalLog(line);
				log.setLine(lineNo * 10);

				log.setC_Currency_ID(m_InvValCal.getC_Currency_ID());
				log.setJP_CurrencyTo_ID(m_InvValCal.getC_Currency_ID());

				log.setJP_ExchangedPriceActual(beginInvValCalLine.getJP_InvValAmt().setScale(m_Currency.getCostingPrecision() ,RoundingMode.HALF_UP));
				log.setJP_ApplyQty(beginInvValCalLine.getQtyBook());
				log.setJP_ApplyAmt(beginInvValCalLine.getJP_InvValTotalAmt().setScale(m_Currency.getCostingPrecision() ,RoundingMode.HALF_UP));
				log.setDescription(Msg.getElement(getCtx(), "JP_ApplyQty") + " = " + Msg.getElement(getCtx(), "JP_BeginInvValCalLine_ID") + " , "
												+ Msg.getElement(getCtx(), "JP_ExchangedPriceActual") + " = " + Msg.getElement(getCtx(), "JP_BeginInvValCalLine_ID") );
				log.setJP_BeginInvValCalLine_ID(beginInvValCalLine.getJP_InvValCalLine_ID());
				log.saveEx(get_TrxName());

			}else if(map_Product_Qty_LastDateValue != null){

				MProductPrice m_ProductPrice = null;
				if(m_PriceListVersionOfLastDateValue != null)
				{
					m_ProductPrice = MProductPrice.get(getCtx(), m_PriceListVersionOfLastDateValue.getM_PriceList_Version_ID(), line.getM_Product_ID(), get_TrxName());
				}

				BigDecimal QtyBook = map_Product_Qty_LastDateValue.get(line.getM_Product_ID());

				if(QtyBook != null)
				{
					MInvValCalLog log = new MInvValCalLog(line);
					log.setLine(lineNo * 10);
					log.setC_Currency_ID(m_InvValCal.getC_Currency_ID());
					log.setJP_CurrencyTo_ID(m_InvValCal.getC_Currency_ID());
					if(m_ProductPrice != null)
					{
						log.setJP_ExchangedPriceActual(m_ProductPrice.getPriceStd().setScale(m_Currency.getCostingPrecision() ,RoundingMode.HALF_UP));
						log.setJP_ApplyAmt(QtyBook.multiply(m_ProductPrice.getPriceStd()).setScale(m_Currency.getCostingPrecision() ,RoundingMode.HALF_UP));
						log.setDescription(Msg.getElement(getCtx(), "JP_ApplyQty") + " = " + Msg.getElement(getCtx(), "QtyBook") + " - " + Msg.getElement(getCtx(), "JP_LastDateValue") + " , "
								+ Msg.getElement(getCtx(), "JP_ExchangedPriceActual") + " = " + Msg.getElement(getCtx(), "M_PriceList_ID") + " : "+ m_PriceList.getName() + " - "
								+ Msg.getElement(getCtx(), "M_PriceList_Version_ID") + " : "+ m_PriceListVersionOfLastDateValue.getName());
					}else {

						log.setJP_ExchangedPriceActual(line.getCurrentCostPrice().setScale(m_Currency.getCostingPrecision() ,RoundingMode.HALF_UP));
						log.setJP_ApplyAmt(QtyBook.multiply(line.getCurrentCostPrice().setScale(m_Currency.getCostingPrecision() ,RoundingMode.HALF_UP)));
						log.setDescription(Msg.getElement(getCtx(), "JP_ApplyQty") + " = " + Msg.getElement(getCtx(), "QtyBook") + " - " + Msg.getElement(getCtx(), "JP_LastDateValue") + " , "
								+ Msg.getElement(getCtx(), "JP_ExchangedPriceActual") + " = " +Msg.getElement(getCtx(), "CurrentCostPrice"));

					}
					log.setJP_ApplyQty(QtyBook);
					log.saveEx(get_TrxName());
				}
			}

		}//MInvValProfile.JP_TYPEOFAVERAGECOST_GrossAverage


		BigDecimal JP_InvValTotalAmt = JPiereInvValUtil.calculateInvValTotalAmt(getCtx(), line.get_ID(), get_TrxName());
		JP_InvValTotalAmt = JP_InvValTotalAmt.setScale(m_Currency.getStdPrecision(), RoundingMode.HALF_UP);
		BigDecimal JP_ApplyQty = JPiereInvValUtil.calculateApplyQty(getCtx(), line.get_ID(), get_TrxName());

		if(JP_ApplyQty.compareTo(Env.ZERO)==0)
		{
			MPriceListVersion m_PriceListVersionOfDateValue = null;
			MProductPrice m_ProductPrice = null;

			if(m_PriceList != null && m_InvValCal.getDateValue() != null)
			{
				m_PriceListVersionOfDateValue = m_PriceList.getPriceListVersion(m_InvValCal.getDateValue());
			}

			if(m_PriceListVersionOfDateValue != null)
			{
				m_ProductPrice = MProductPrice.get(getCtx(), m_PriceListVersionOfDateValue.getM_PriceList_Version_ID(), line.getM_Product_ID(), get_TrxName());
			}

			if(m_ProductPrice != null)
			{
				line.setJP_InvValAmt(m_ProductPrice.getPriceStd().setScale(m_Currency.getCostingPrecision() ,RoundingMode.HALF_UP));

			}else {

				if(beginInvValCalLine != null)
				{
					line.setJP_InvValAmt(beginInvValCalLine.getJP_InvValAmt());

				}else {

					if(m_PriceListVersionOfLastDateValue != null)
					{
						m_ProductPrice = MProductPrice.get(getCtx(), m_PriceListVersionOfLastDateValue.getM_PriceList_Version_ID(), line.getM_Product_ID(), get_TrxName());
					}

					if(m_ProductPrice != null)
					{
						line.setJP_InvValAmt(m_ProductPrice.getPriceStd().setScale(m_Currency.getCostingPrecision() ,RoundingMode.HALF_UP));

					}else {

						line.setJP_InvValAmt(line.getCurrentCostPrice());

					}
				}

			}

		}else{
			line.setJP_InvValAmt(JP_InvValTotalAmt.divide(JP_ApplyQty, m_Currency.getCostingPrecision() ,RoundingMode.HALF_UP));
		}

		if(line.getQtyBook().compareTo(Env.ZERO)==0)
		{
			line.setJP_InvValTotalAmt(Env.ZERO);
		}else{
			line.setJP_InvValTotalAmt(line.getQtyBook().multiply(line.getJP_InvValAmt()).setScale(m_Currency.getStdPrecision(), RoundingMode.HALF_UP));
		}
		line.saveEx(get_TrxName());

	}//calculate_AverageInvoice


	private void calculate_RetailInventoryMethod(MInvValCalLine line) //TODO
	{
		MProductPrice m_ProductPrice = null;
		if(m_PriceListVersionOfDateValue != null)
		{
			m_ProductPrice = MProductPrice.get(getCtx(), m_PriceListVersionOfDateValue.getM_PriceList_Version_ID(), line.getM_Product_ID(), get_TrxName());
		}

		if(m_ProductPrice != null)
		{
			line.setJP_InvValAmt(m_ProductPrice.getPriceStd().setScale(m_Currency.getCostingPrecision() ,RoundingMode.HALF_UP));

		}else {

			line.setJP_InvValAmt(line.getCurrentCostPrice());

		}

		if(line.getQtyBook().compareTo(Env.ZERO)==0)
		{
			line.setJP_InvValTotalAmt(Env.ZERO);
		}else{
			line.setJP_InvValTotalAmt(line.getQtyBook().multiply(line.getJP_InvValAmt()).setScale(m_Currency.getStdPrecision(), RoundingMode.HALF_UP));
		}
		line.saveEx(get_TrxName());

	}

//	private void calculate_StandardCosting(MInvValCalLine line)
//	{
//		;
//	}

}
