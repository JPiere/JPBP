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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import jpiere.base.plugin.org.adempiere.model.MInvValCal;
import jpiere.base.plugin.org.adempiere.model.MInvValCalLine;
import jpiere.base.plugin.org.adempiere.model.MInvValCalLog;
import jpiere.base.plugin.org.adempiere.model.MInvValProfile;
import jpiere.base.plugin.util.JPiereInvValUtil;

import org.adempiere.exceptions.DBException;
import org.compiere.model.I_C_InvoiceLine;
import org.compiere.model.I_C_OrderLine;
import org.compiere.model.MConversionRate;
import org.compiere.model.MInOutLine;
import org.compiere.model.MMatchInv;
import org.compiere.model.MMatchPO;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;

/**
 * JPIERE-0161 Inventory Valuation Calculate Doc
 *
 *  Default Inventory Valuation Calculate
 *
 *  @author Hideaki Hagiwara
 *
 */
public class DefaultInventoryValuationCalculate extends SvrProcess {

	MInvValProfile m_InvValProfile = null;
	MInvValCal m_InvValCal = null;
	MInvValCalLine[] lines = null;
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
	}

	@Override
	protected String doIt() throws Exception
	{
		for(int i = 0; i < lines.length; i++)
		{
			if(lines[i].getCostingMethod().equals(MInvValCalLine.COSTINGMETHOD_Fifo))
			{
				calculate_Fifo(lines[i]);
			}else if(lines[i].getCostingMethod().equals(MInvValCalLine.COSTINGMETHOD_Lifo)){
				calculate_Lifo(lines[i]);
			}else if(lines[i].getCostingMethod().equals(MInvValCalLine.COSTINGMETHOD_LastPOPrice)){
				calculate_LastPOPrice(lines[i]);
			}else if(lines[i].getCostingMethod().equals(MInvValCalLine.COSTINGMETHOD_LastInvoice)){
				calculate_LastInvoice(lines[i]);
			}else if(lines[i].getCostingMethod().equals(MInvValCalLine.COSTINGMETHOD_AveragePO)){
				calculate_AveragePO(lines[i]);
			}else if(lines[i].getCostingMethod().equals(MInvValCalLine.COSTINGMETHOD_AverageInvoice)){
				calculate_AverageInvoice(lines[i]);
			}else if(lines[i].getCostingMethod().equals(MInvValCalLine.COSTINGMETHOD_StandardCosting)){
				calculate_StandardCosting(lines[i]);
			}
		}

		BigDecimal totalLines = JPiereInvValUtil.calculateTotalLines(getCtx(), Record_ID, get_TrxName());
		m_InvValCal.setTotalLines(totalLines);
		m_InvValCal.saveEx(get_TrxName());

		return Msg.getElement(getCtx(), MInvValCal.COLUMNNAME_TotalLines) + " = " + totalLines;
	}

	/**
	 *
	 *
	 * @param line
	 */
	private void calculate_Fifo(MInvValCalLine line)
	{
		BigDecimal qtyBook = line.getQtyBook().abs();//abs is for nagative Inventory.
		MInOutLine[] ioLines = JPiereInvValUtil.getInOutLines(getCtx(),line.getM_Product_ID(), m_InvValCal.getJP_LastDateValue()
									, m_InvValCal.getDateValue(), m_InvValProfile.getOrgs(), "io.MovementDate DESC");

		int lineNo = 1;
		for(int i = 0; i < ioLines.length; i++)
		{
			if(ioLines[i].getMovementQty().compareTo(Env.ZERO) <= 0)//ignore nagative inventory.
				continue;

			MInvValCalLog log = new MInvValCalLog(line);
			log.setAD_Org_ID(ioLines[i].getAD_Org_ID());
			log.setLine(lineNo * 10);
			log.setM_InOutLine_ID(ioLines[i].getM_InOutLine_ID());
			log.setMovementDate(ioLines[i].getM_InOut().getMovementDate());
			log.setMovementType(ioLines[i].getM_InOut().getMovementType());
			if(m_InvValProfile.getJP_ApplyAmtList().equals(MInvValProfile.JP_APPLYAMTLIST_PurchaseOrder))
			{
				MMatchPO[] matchPos = MMatchPO.get(getCtx(), ioLines[i].getM_InOutLine_ID(), get_TrxName());
				I_C_OrderLine orderLine =null;
				for(int j = 0; j < matchPos.length; j++)
				{
					//Set Macth PO Info
					log.setM_MatchPO_ID(matchPos[j].get_ID());
					log.setQty(matchPos[j].getQty());

					//Set PO Info
					orderLine = matchPos[j].getC_OrderLine();
					JPiereInvValUtil.copyInfoFromOrderLineToLog(log, orderLine);

					//Set Reference Field Gorup
					log.setC_Currency_ID_To(m_InvValCal.getC_Currency_ID());
					if(orderLine.getC_Currency_ID() != m_InvValCal.getC_Currency_ID())
					{
						BigDecimal rate =MConversionRate.getRate(orderLine.getC_Currency_ID(), m_InvValCal.getC_Currency_ID(), orderLine.getDateOrdered(),
								orderLine.getC_Order().getC_ConversionType_ID(), orderLine.getAD_Client_ID(), orderLine.getAD_Org_ID());
						log.setMultiplyRate(rate);
					}else{
						log.setMultiplyRate(Env.ONE);
					}

					log.setJP_ExchangedAmt(log.getLineNetAmt().multiply(log.getMultiplyRate()));

					if(log.isTaxIncluded())
					{
						;//TODO:税抜処理の実装
					}else{
						log.setJP_ExchangedNoTaxAmt(log.getJP_ExchangedAmt());
					}

					log.setJP_ExchangedPriceActual(log.getJP_ExchangedNoTaxAmt().divide(log.getQty()));

					if(qtyBook.compareTo(log.getQty()) >= 0)
					{
						log.setJP_ApplyQty(log.getQty());
						if(log.getQty().compareTo(log.getQtyOrdered())==0)
							log.setJP_ApplyAmt(log.getJP_ExchangedNoTaxAmt());
						else
							log.setJP_ApplyAmt(log.getJP_ExchangedPriceActual().multiply(log.getQty()));
						qtyBook = qtyBook.subtract(log.getQty());
					}else{
						log.setJP_ApplyQty(qtyBook);
						log.setJP_ApplyAmt(qtyBook.multiply(log.getJP_ExchangedPriceActual()));
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

			}else if(m_InvValProfile.getJP_ApplyAmtList().equals(MInvValProfile.JP_APPLYAMTLIST_InvoiceVendor)){

				MMatchInv[] matchInvs = MMatchInv.getInOutLine(getCtx(), ioLines[i].getM_InOutLine_ID(), get_TrxName());
				I_C_InvoiceLine invoiceLine =null;
				for(int j = 0; j < matchInvs.length; j++)
				{
					//Set Macth PO Info
					log.setM_MatchInv_ID(matchInvs[j].get_ID());
					log.setQty(matchInvs[j].getQty());

					//Set PO Info
					invoiceLine = matchInvs[j].getC_InvoiceLine();
					JPiereInvValUtil.copyInfoFromInvoiceLineToLog(log, invoiceLine);

					//Set Reference Field Gorup
					log.setC_Currency_ID_To(m_InvValCal.getC_Currency_ID());
					if(invoiceLine.getC_Invoice().getC_Currency_ID() != m_InvValCal.getC_Currency_ID())
					{
						BigDecimal rate =MConversionRate.getRate(invoiceLine.getC_Invoice().getC_Currency_ID(), m_InvValCal.getC_Currency_ID(), invoiceLine.getC_Invoice().getDateOrdered(),
								invoiceLine.getC_Invoice().getC_ConversionType_ID(), invoiceLine.getAD_Client_ID(), invoiceLine.getAD_Org_ID());
						log.setMultiplyRate(rate);
					}else{
						log.setMultiplyRate(Env.ONE);
					}

					log.setJP_ExchangedAmt(log.getLineNetAmt().multiply(log.getMultiplyRate()));

					if(log.isTaxIncluded())
					{
						;//TODO:税抜処理の実装
					}else{
						log.setJP_ExchangedNoTaxAmt(log.getJP_ExchangedAmt());
					}

					log.setJP_ExchangedPriceActual(log.getJP_ExchangedNoTaxAmt().divide(log.getQty()));

					if(qtyBook.compareTo(log.getQty()) >= 0)
					{
						log.setJP_ApplyQty(log.getQty());
						if(log.getQty().compareTo(log.getQtyOrdered())==0)
							log.setJP_ApplyAmt(log.getJP_ExchangedNoTaxAmt());
						else
							log.setJP_ApplyAmt(log.getJP_ExchangedPriceActual().multiply(log.getQty()));
						qtyBook = qtyBook.subtract(log.getQty());
					}else{
						log.setJP_ApplyQty(qtyBook);
						log.setJP_ApplyAmt(qtyBook.multiply(log.getJP_ExchangedPriceActual()));
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
				log.setDescription("Can not Calculate");
				log.saveEx(get_TrxName());
				return ;
			}

		}//for i

		if(qtyBook.signum() > 0)
		{
			MInvValCalLog log = new MInvValCalLog(line);
			log.setLine(lineNo * 10);
			log.setC_Currency_ID(m_InvValCal.getC_Currency_ID());
			log.setC_Currency_ID_To(m_InvValCal.getC_Currency_ID());
			log.setJP_ExchangedPriceActual(line.getCurrentCostPrice());
			log.setJP_ApplyQty(qtyBook);
			log.setJP_ApplyAmt(qtyBook.multiply(line.getCurrentCostPrice()));
			log.setDescription(Msg.getElement(getCtx(), "CurrentCostPrice"));
			log.saveEx(get_TrxName());

		}else{
			;
		}

		BigDecimal JP_InvValTotalAmt = JPiereInvValUtil.calculateInvValTotalAmt(getCtx(), line.get_ID(), get_TrxName());
		if(line.getQtyBook().compareTo(Env.ZERO) >= 0)
		{
			line.setJP_InvValTotalAmt(JP_InvValTotalAmt);
			line.setJP_InvValAmt(JP_InvValTotalAmt.divide(line.getQtyBook(),2));
		}else{
			line.setJP_InvValTotalAmt(JP_InvValTotalAmt.negate());
			line.setJP_InvValAmt(JP_InvValTotalAmt.divide(line.getQtyBook().abs(),2));
		}
		line.saveEx(get_TrxName());

	}

	private void calculate_Lifo(MInvValCalLine line)
	{
		;
	}

	private void calculate_LastPOPrice(MInvValCalLine line)
	{
		;
	}

	private void calculate_LastInvoice(MInvValCalLine line)
	{
		;
	}

	private void calculate_AveragePO(MInvValCalLine line)
	{
		;
	}

	private void calculate_AverageInvoice(MInvValCalLine line)
	{
		;
	}

	private void calculate_StandardCosting(MInvValCalLine line)
	{
		;
	}

}
