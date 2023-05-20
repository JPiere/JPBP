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
package jpiere.base.plugin.org.adempiere.base;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.adempiere.exceptions.DBException;
import org.compiere.model.MCharge;
import org.compiere.model.MCurrency;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceTax;
import org.compiere.model.MJournalLine;
import org.compiere.model.MTax;
import org.compiere.model.MTaxProvider;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.JPiereTaxProvider;
import jpiere.base.plugin.org.adempiere.model.MBill;
import jpiere.base.plugin.org.adempiere.model.MBillLine;
import jpiere.base.plugin.org.adempiere.model.MBillTax;
import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractContentTax;
import jpiere.base.plugin.org.adempiere.model.MContractLine;
import jpiere.base.plugin.org.adempiere.model.MEstimation;
import jpiere.base.plugin.org.adempiere.model.MEstimationLine;
import jpiere.base.plugin.org.adempiere.model.MGLJournalTax;
import jpiere.base.plugin.org.adempiere.model.MRecognition;
import jpiere.base.plugin.org.adempiere.model.MRecognitionLine;

/**
 * Interface JPiere Tax Provider
 *
 * @author Hideaki Hagiwara
 *
 */
public interface IJPiereTaxProvider {

	/**	Logger							*/
	static CLogger	log = CLogger.getCLogger ("IJPiereTaxProvider");

	public BigDecimal calculateTax (MTax m_tax, BigDecimal amount, boolean taxIncluded, int scale, RoundingMode roundingMode);

	public boolean calculateEstimationTaxTotal(MTaxProvider provider, MEstimation estimation);

	public boolean recalculateTax(MTaxProvider provider, MEstimationLine line, boolean newRecord);

	public boolean updateEstimationTax(MTaxProvider provider, MEstimationLine line);

	public boolean updateHeaderTax(MTaxProvider provider, MEstimationLine line);


	public boolean calculateRecognitionTaxTotal(MTaxProvider provider, MRecognition estimation);

	public boolean recalculateTax(MTaxProvider provider, MRecognitionLine line, boolean newRecord);

	public boolean updateRecognitionTax(MTaxProvider provider, MRecognitionLine line);

	public boolean updateHeaderTax(MTaxProvider provider, MRecognitionLine line);


	/**
	 * JPIERE-0508 Recalculation Bill Tax - Calculate Bill Tax Total
	 *
	 * @param provider
	 * @param bill
	 * @return
	 */
	default public boolean calculateBillTaxTotal(MTaxProvider provider, MBill bill)
	{
		return true;
	}

	/**
	 * JPIERE-0508 Recalculation Bill Tax - Recalcuation Tax
	 *
	 * @param billLine
	 * @param invoice
	 * @param invoiceTax
	 * @param isOldTax
	 * @return
	 */
	default public boolean recalculateTax(MTaxProvider provider, MBillLine billLine, MInvoice invoice, MInvoiceTax invoiceTax , boolean isOldTax)
	{
		if(!updateBillTax(provider, billLine, invoice, invoiceTax))
			return false;

		if(isOldTax)
			return true;
		else
			return updateHeaderTax(provider, billLine);

	}


	/**
	 * JPIERE-0508 Recalculation Bill Tax - Update Bill Tax
	 *
	 * @param provider
	 * @param billLine
	 * @param invoice
	 * @param invoiceTax
	 * @return
	 */
	default public boolean updateBillTax(MTaxProvider provider, MBillLine billLine, MInvoice invoice, MInvoiceTax invoiceTax)
	{
		MCurrency currency = MCurrency.get(billLine.getParent().getC_Currency_ID());
		MBillTax billTax = MBillTax.get(billLine, invoiceTax, currency.getStdPrecision(), billLine.get_TrxName());
	    if (billTax != null)
	    {
	    	if (!calculateTaxFromInvoices(billLine, billTax, invoice, invoiceTax))
	    		return false;
	    	if (billTax.getTaxAmt().signum() != 0 || billTax.getTaxBaseAmt().signum() != 0) {
	    		if (!billTax.save(billLine.get_TrxName()))
	    			return false;
	    	} else {
	    		if (!billTax.is_new() && !billTax.isProcessed() && !billTax.delete(false, billLine.get_TrxName()))
	    			return false;
	    	}
		}
	    return true;
	}


	/**
	 * JPIERE-0508 Recalculation Bill Tax - Update Header
	 *
	 * @param billLine
	 * @return
	 */
	default public boolean updateHeaderTax(MTaxProvider provider, MBillLine billLine)
	{
		BigDecimal TotalLines = Env.ZERO;
		BigDecimal GrandTotal = Env.ZERO;
		BigDecimal TaxBaseAmt = Env.ZERO;
		BigDecimal TaxAmt = Env.ZERO;
		BigDecimal PayAmt = Env.ZERO;
		BigDecimal OpenAmt = Env.ZERO;
		BigDecimal OverUnderAmt = Env.ZERO;
		BigDecimal JPBillAmt = Env.ZERO;

		String sql = " SELECT COALESCE(SUM(TotalLines),0)"
						+ "  ,COALESCE(SUM(GrandTotal),0)"
						+ "  ,COALESCE(SUM(TaxBaseAmt),0)"
						+ "  ,COALESCE(SUM(TaxAmt),0)"
						+ "  ,COALESCE(SUM(PayAmt),0)"
						+ "  ,COALESCE(SUM(OpenAmt),0)"
						+ "  ,COALESCE(SUM(OverUnderAmt),0)"
				+ " FROM JP_BillLine "
				+ " WHERE JP_Bill_ID=? AND IsTaxAdjustLineJP='N' ";

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (sql, billLine.get_TrxName());
			pstmt.setInt (1, billLine.getJP_Bill_ID());
			rs = pstmt.executeQuery ();
			if (rs.next ())
			{
				TotalLines = rs.getBigDecimal(1);
				GrandTotal = rs.getBigDecimal(2);
				TaxBaseAmt = rs.getBigDecimal(3);
				TaxAmt = rs.getBigDecimal(4);
				PayAmt = rs.getBigDecimal(5);
				OpenAmt = rs.getBigDecimal(6);
				OverUnderAmt= rs.getBigDecimal(7);
			}
		}
		catch (SQLException e)
		{
			throw new DBException(e, sql);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}


		BigDecimal JP_TaxBaseAmt = Env.ZERO;
		BigDecimal JP_TaxAmt = Env.ZERO;
		BigDecimal JP_RecalculatedTaxBaseAmt = Env.ZERO;
		BigDecimal JP_RecalculatedTaxAmt = Env.ZERO;
		BigDecimal JP_TaxAdjust_TaxAmt = Env.ZERO;

		sql = "SELECT "
				+ "  COALESCE(SUM(TaxBaseAmt),0)"
				+ " ,COALESCE(SUM(TaxAmt),0)"
				+ " ,COALESCE(SUM(JP_RecalculatedTaxBaseAmt),0)"
				+ " ,COALESCE(SUM(JP_RecalculatedTaxAmt),0)"
				+ " ,COALESCE(SUM(JP_TaxAdjust_TaxAmt),0)"
				+ " FROM JP_BillTax"
				+ " WHERE JP_Bill_ID=? ";

		try
		{
			pstmt = DB.prepareStatement (sql, billLine.get_TrxName());
			pstmt.setInt (1, billLine.getJP_Bill_ID());
			rs = pstmt.executeQuery ();
			if (rs.next ())
			{
				JP_TaxBaseAmt = rs.getBigDecimal(1);
				JP_TaxAmt = rs.getBigDecimal(2);
				JP_RecalculatedTaxBaseAmt = rs.getBigDecimal(3);
				JP_RecalculatedTaxAmt = rs.getBigDecimal(4);
				JP_TaxAdjust_TaxAmt = rs.getBigDecimal(5);
			}
		}
		catch (SQLException e)
		{
			throw new DBException(e, sql);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}


		if(JP_TaxBaseAmt.compareTo(TaxBaseAmt) != 0)
		{
			;//Check for debug
		}

		if(JP_TaxAmt.compareTo(TaxAmt) != 0)
		{
			;//Check for debug
		}

		GrandTotal = GrandTotal.add(JP_TaxAdjust_TaxAmt);
		OpenAmt = OpenAmt.add(JP_TaxAdjust_TaxAmt);

		billLine.clearParent();
		BigDecimal JPCarriedForwardAmt = Env.ZERO;
		MBill bill = billLine.getParent();
		JPCarriedForwardAmt = bill.getJPCarriedForwardAmt();
		JPBillAmt = JPCarriedForwardAmt.add(OpenAmt);

		sql = "UPDATE JP_Bill b"
				+ " SET TotalLines = ?"		//1
					+ " ,GrandTotal= ?"		//2
					+ " ,TaxBaseAmt= ?"		//3
					+ " ,TaxAmt= ?"			//4
					+ " ,PayAmt= ?"			//5
					+ " ,OpenAmt= ?"		//6
					+ " ,OverUnderAmt = ?"	//7
					+ " ,JPBillAmt = ?"		//8
				+ " WHERE JP_Bill_ID= ? " ;	//9

		Object[] para = new Object[]{TotalLines
									 ,GrandTotal
									 ,JP_RecalculatedTaxBaseAmt
									 ,JP_RecalculatedTaxAmt
									 ,PayAmt
									 ,OpenAmt
									 ,OverUnderAmt
									 ,JPBillAmt
									 ,billLine.getJP_Bill_ID()};
		int no = DB.executeUpdate(sql, para, false, billLine.get_TrxName());
		if (no != 1)
		{
			//log.saveError("Error", "MBillLine#updateHeaderAndTax() : " + sql);
			return false;
		}


		billLine.clearParent();
		return true;
	}




	/**
	 * JPIERE-0508 Recalculation Bill Tax - calculate Tax From Invoices
	 *
	 * @param billLine
	 * @param billTax
	 * @param invoice
	 * @param invoiceTax
	 * @return
	 */
	private boolean calculateTaxFromInvoices (MBillLine billLine, MBillTax billTax, MInvoice invoice, MInvoiceTax invoiceTax)
	{
		BigDecimal taxBaseAmt = Env.ZERO;
		BigDecimal taxAmt = Env.ZERO;

		MTax tax = MTax.get(billTax.getC_Tax_ID());
		boolean documentLevel = tax.isDocumentLevel();

		RoundingMode roundingMode = JPiereTaxProvider.getRoundingMode(billLine.getParent().getC_BPartner_ID(), billLine.getParent().isSOTrx(), tax.getC_TaxProvider());

		String sql = " SELECT COALESCE(SUM(CASE WHEN dt.DocBaseType='APC' THEN it.TaxBaseAmt*-1"
											+ " WHEN dt.DocBaseType='ARC' THEN it.TaxBaseAmt*-1"
											+ "	ELSE it.TaxBaseAmt END),0)"
						+ "  ,COALESCE(SUM(CASE WHEN dt.DocBaseType='APC' THEN it.TaxAmt*-1"
											+ " WHEN dt.DocBaseType='ARC' THEN it.TaxAmt*-1"
											+ " ELSE it.TaxAmt END),0)"
				+ " FROM C_InvoiceTax it "
				+ " INNER JOIN C_Invoice i ON (it.C_Invoice_ID = i.C_Invoice_ID) "
				+ " INNER JOIN C_DocType dt ON (i.C_DocTypeTarget_ID = dt.C_DocType_ID) "
				+ " INNER JOIN JP_BillLine bl ON (i.C_Invoice_ID = bl.C_Invoice_ID) "
				+ " WHERE bl.JP_Bill_ID=? AND it.C_Tax_ID = ? AND it.IsTaxIncluded=? " ;

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (sql, billLine.get_TrxName());
			pstmt.setInt (1, billTax.getJP_Bill_ID());
			pstmt.setInt (2, billTax.getC_Tax_ID());
			pstmt.setString (3, billTax.isTaxIncluded()? "Y" : "N" );
			rs = pstmt.executeQuery ();
			if (rs.next ())
			{
				taxBaseAmt = rs.getBigDecimal(1);
				taxAmt = rs.getBigDecimal(2);
			}
		}
		catch (SQLException e)
		{
			throw new DBException(e, sql);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}

		billTax.setTaxBaseAmt(taxBaseAmt);
		billTax.setTaxAmt(taxAmt);
		billTax.setJP_RecalculatedTaxBaseAmt(taxBaseAmt);
		billTax.setJP_RecalculatedTaxAmt(taxAmt);
		billTax.setJP_TaxAdjust_TaxAmt(Env.ZERO);

		if(documentLevel && billLine.getParent().isTaxRecalculateJP())
		{
			//JPIERE-0369 Include Tax lines and exclude Tax lines in a Invoice
			if(invoice.isTaxIncluded()==invoiceTax.isTaxIncluded())
			{
				BigDecimal lineNetAmt = Env.ZERO;

				sql = "SELECT COALESCE(sum(CASE  WHEN dt.DocBaseType='APC' THEN il.LineNetAmt*-1 "
						+ " WHEN dt.DocBaseType='ARC' THEN il.LineNetAmt*-1"
						+ " ELSE il.LineNetAmt END),0) "
						+ "FROM C_InvoiceLine il"
						+ " INNER JOIN C_Invoice i 	ON (il.C_Invoice_ID = i.C_Invoice_ID) "
						+ " INNER JOIN C_DocType dt ON (i.C_DocTypeTarget_ID = dt.C_DocType_ID) "
						+ " INNER JOIN JP_BillLine bl ON (i.C_Invoice_ID=bl.C_Invoice_ID) "
						+ " WHERE bl.JP_Bill_ID=? AND il.C_Tax_ID=? AND i.IsTaxIncluded=? ";

				lineNetAmt = DB.getSQLValueBD(billLine.get_TrxName(), sql, billTax.getJP_Bill_ID(), billTax.getC_Tax_ID(), billTax.isTaxIncluded()? "Y" : "N" );

				taxAmt = calculateTax(tax, lineNetAmt, billTax.isTaxIncluded(), billTax.getPrecision(), roundingMode);
				billTax.setJP_RecalculatedTaxAmt(taxAmt);

				//	Set Base
				if (billTax.isTaxIncluded())
				{
					billTax.setJP_RecalculatedTaxBaseAmt (lineNetAmt.subtract(taxAmt));
				}else {
					billTax.setJP_RecalculatedTaxBaseAmt (lineNetAmt);
				}

				billTax.setJP_TaxAdjust_TaxAmt(billTax.getJP_RecalculatedTaxAmt().subtract(billTax.getTaxAmt()));

			}else {

				//If a single Invoice contains both tax-excluded and tax-included lines, tax recalculation at the document level cannot be performed.
				throw new AdempiereException(Msg.getMsg(Env.getCtx(), "JP_TaxAdjust_ErrorOfMixTax")
												+ " - "+ invoice.getDocumentNo() + " : "+ tax.getName());

			}

		}//if(documentLevel)

		return true;
	}	//	calculateTaxFromLines


	/**
	 * JPIERE-0541: Calculate Contract Content Tax
	 *
	 * @param provider
	 * @param contractContent
	 * @return
	 */
	default public boolean calculateContractContentTaxTotal(MTaxProvider provider, MContractContent contractContent)
	{
		BigDecimal totalLines = Env.ZERO;
		ArrayList<Integer> taxList = new ArrayList<Integer>();
		MContractLine[] lines = contractContent.getLines();
		for (int i = 0; i < lines.length; i++)
		{
			MContractLine line = lines[i];
			totalLines = totalLines.add(line.getLineNetAmt());
			Integer taxID = Integer.valueOf(line.getC_Tax_ID());
			if (!taxList.contains(taxID))
			{
				MContractContentTax eTax = MContractContentTax.get (line, contractContent.getPrecision(), false, contractContent.get_TrxName());	//	current Tax

				//JPIERE-0369:Start
				if(eTax.isTaxIncluded() != contractContent.isTaxIncluded())
				{
					if(line.getC_Charge_ID() != 0)
					{
                        MCharge charge = MCharge.get(Env.getCtx(), line.getC_Charge_ID());
                        if(charge.isSameTax())
                        {
    						//Don't setting common Tax Rate master for tax-included lines and tax-excluded lines
    						log.saveError("Error", Msg.getMsg(Env.getCtx(), "JP_NotSet_Common_TaxRate"));
    						return false;

                        }else {

                            if(eTax.isTaxIncluded() == charge.isTaxIncluded())
                            {
                            	;//No Problem
                            }else {

                            	//Don't setting common Tax Rate master for tax-included lines and tax-excluded lines
        						log.saveError("Error", Msg.getMsg(Env.getCtx(), "JP_NotSet_Common_TaxRate"));
        						return false;
                            }
                        }

					}else {

						//Don't setting common Tax Rate master for tax-included lines and tax-excluded lines
						log.saveError("Error", Msg.getMsg(Env.getCtx(), "JP_NotSet_Common_TaxRate"));
						return false;
					}

				}else{

					if(line.getC_Charge_ID() != 0)
					{
						MCharge charge = MCharge.get(Env.getCtx(), line.getC_Charge_ID());
						if(!charge.isSameTax())
						{
							if(eTax.isTaxIncluded() != charge.isTaxIncluded())
							{
								//Don't setting common Tax Rate master for tax-included lines and tax-excluded lines
								log.saveError("Error", Msg.getMsg(Env.getCtx(), "JP_NotSet_Common_TaxRate"));
								return false;
							}
						}
					}

				}
				//JPiere-0369: End

				if (!calculateTaxFromContractLines(line, eTax))
					return false;
				if (!eTax.save(contractContent.get_TrxName()))
					return false;
				taxList.add(taxID);
			}
		}

		//	Taxes
		BigDecimal grandTotal = totalLines;
		MContractContentTax[] taxes = contractContent.getTaxes(true);

		RoundingMode roundingMode = JPiereTaxProvider.getRoundingMode(lines[0].getParent().getC_BPartner_ID(), lines[0].getParent().isSOTrx(), provider);

		for (int i = 0; i < taxes.length; i++)
		{
			MContractContentTax eTax = taxes[i];
			MTax tax = MTax.get(eTax.getCtx(), eTax.getC_Tax_ID());
			if (tax.isSummary())
			{
				MTax[] cTaxes = tax.getChildTaxes(false);
				for (int j = 0; j < cTaxes.length; j++)
				{
					MTax cTax = cTaxes[j];
					BigDecimal taxAmt = calculateTax(cTax, eTax.getTaxBaseAmt(), eTax.isTaxIncluded(), contractContent.getPrecision(), roundingMode);//JPIERE-0369
					//
					MContractContentTax newOTax = new MContractContentTax(contractContent.getCtx(), 0, contractContent.get_TrxName());
					newOTax.set_ValueOfColumn("AD_Client_ID", contractContent.getAD_Client_ID());
					newOTax.setAD_Org_ID(contractContent.getAD_Org_ID());
					newOTax.setJP_ContractContent_ID(contractContent.getJP_ContractContent_ID());
					newOTax.setC_Tax_ID(cTax.getC_Tax_ID());
					newOTax.setIsTaxIncluded(eTax.isTaxIncluded());//JPIERE-0369
					newOTax.setTaxBaseAmt(eTax.getTaxBaseAmt());
					newOTax.setTaxAmt(taxAmt);
					if (!newOTax.save(contractContent.get_TrxName()))
						return false;
					//
					if (!eTax.isTaxIncluded())//JPIERE-0369
						grandTotal = grandTotal.add(taxAmt);
				}
				if (!eTax.delete(true, contractContent.get_TrxName()))
					return false;
				if (!eTax.save(contractContent.get_TrxName()))
					return false;
			}
			else
			{
				if (!eTax.isTaxIncluded())//JPIERE-0369
					grandTotal = grandTotal.add(eTax.getTaxAmt());
			}
		}
		//
		contractContent.setTotalLines(totalLines);
		contractContent.setGrandTotal(grandTotal);
		return true;
	}


	/**
	 * JPIERE-0541: Calculate Contract Content Tax
	 *
	 * @param provider
	 * @param line
	 * @param newRecord
	 * @return
	 */
	default public boolean recalculateTax(MTaxProvider provider, MContractLine line, boolean newRecord)
	{
		if (!newRecord && line.is_ValueChanged(MContractLine.COLUMNNAME_C_Tax_ID) && !line.getParent().isProcessed())
		{
    		if (!updateContractContentTax(line, true))
				return false;
		}

		if(!updateContractContentTax(line, false))
			return false;

		return updateHeaderTax(provider, line);
	}


	/**
	 * JPIERE-0541: Calculate Contract Content Tax
	 *
	 * @param provider
	 * @param line
	 * @return
	 */
	default public boolean updateContractContentTax(MTaxProvider provider, MContractLine line)
	{
		return  updateContractContentTax(line, false);
	}


	/**
	 * JPIERE-0541: Calculate Contract Content Tax
	 *
	 * @param line
	 * @param oldTax
	 * @return
	 */
	private boolean updateContractContentTax(MContractLine line, boolean oldTax)
	{
		MContractContentTax tax = MContractContentTax.get (line, line.getPrecision(), oldTax, line.get_TrxName());
		if (tax != null)
		{
			//JPIERE-0369:Start
			if(tax.isTaxIncluded() != line.getParent().isTaxIncluded())
			{
				if(line.getC_Charge_ID() != 0)
				{
                    MCharge charge = MCharge.get(Env.getCtx(), line.getC_Charge_ID());
                    if(charge.isSameTax())
                    {
						//Don't setting common Tax Rate master for tax-included lines and tax-excluded lines
						log.saveError("Error", Msg.getMsg(Env.getCtx(), "JP_NotSet_Common_TaxRate"));
						return false;

                    }else {

                        if(tax.isTaxIncluded() == charge.isTaxIncluded())
                        {
                        	;//No Problem
                        }else {

                        	//Don't setting common Tax Rate master for tax-included lines and tax-excluded lines
    						log.saveError("Error", Msg.getMsg(Env.getCtx(), "JP_NotSet_Common_TaxRate"));
    						return false;
                        }
                    }

				}else {

					//Don't setting common Tax Rate master for tax-included lines and tax-excluded lines
					log.saveError("Error", Msg.getMsg(Env.getCtx(), "JP_NotSet_Common_TaxRate"));
					return false;
				}

			}else{

				if(line.getC_Charge_ID() != 0)
				{
					MCharge charge = MCharge.get(Env.getCtx(), line.getC_Charge_ID());
					if(!charge.isSameTax())
					{
						if(tax.isTaxIncluded() != charge.isTaxIncluded())
						{
							//Don't setting common Tax Rate master for tax-included lines and tax-excluded lines
							log.saveError("Error", Msg.getMsg(Env.getCtx(), "JP_NotSet_Common_TaxRate"));
							return false;
						}
					}
				}

			}
			//JPiere-0369: End

			if (!calculateTaxFromContractLines(line,tax))
				return false;
			if (tax.getTaxAmt().signum() != 0 || tax.getTaxBaseAmt().signum() != 0) {
				if (!tax.save(line.get_TrxName()))
					return false;
			} else {
				if (!tax.is_new() && !tax.delete(false, line.get_TrxName()))
					return false;
			}
		}
		return true;
	}


	/**
	 * JPIERE-0541: Calculate Contract Content Tax
	 *
	 * @param provider
	 * @param line
	 * @return
	 */
	default public boolean updateHeaderTax(MTaxProvider provider, MContractLine line)
	{
		//Update Contract Content
		String sql = "UPDATE JP_ContractContent cc"
		+ " SET TotalLines = "
		    + "(SELECT COALESCE(SUM(LineNetAmt),0) FROM JP_ContractLine cl WHERE cc.JP_ContractContent_ID=cl.JP_ContractContent_ID)"
		+ "WHERE JP_ContractContent_ID=?";
		int no = DB.executeUpdate(sql, new Object[]{Integer.valueOf(line.getJP_ContractContent_ID())}, false, line.get_TrxName(), 0);
		if (no != 1)
		{
			log.warning("(1) #" + no);
			return false;
		}

		sql = "UPDATE JP_ContractContent cc "
				+ " SET GrandTotal=TotalLines+"
					+ "(SELECT COALESCE(SUM(TaxAmt),0) FROM JP_ContractContentTax cct WHERE cc.JP_ContractContent_ID=cct.JP_ContractContent_ID AND cct.IsTaxIncluded='N' ) "
					+ "WHERE JP_ContractContent_ID=?";
		no = DB.executeUpdate(sql, new Object[]{Integer.valueOf(line.getJP_ContractContent_ID())}, false, line.get_TrxName(), 0);
		if (no != 1)
			log.warning("(2) #" + no);

		line.clearParent();
		return no == 1;
	}


	/**
	 * JPIERE-0541: Calculate Contract Content Tax
	 *
	 * @param line
	 * @param m_ContractContentTax
	 * @return
	 */
	private boolean calculateTaxFromContractLines (MContractLine line, MContractContentTax m_ContractContentTax)
	{
		BigDecimal taxBaseAmt = Env.ZERO;
		BigDecimal taxAmt = Env.ZERO;

		MTax tax = MTax.get(m_ContractContentTax.getCtx(), m_ContractContentTax.getC_Tax_ID());
		boolean documentLevel = tax.isDocumentLevel();

		RoundingMode roundingMode = JPiereTaxProvider.getRoundingMode(line.getParent().getC_BPartner_ID(), line.getParent().isSOTrx(), tax.getC_TaxProvider());

		//
		String sql = "SELECT LineNetAmt FROM JP_ContractLine WHERE JP_ContractContent_ID=? AND C_Tax_ID=?";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (sql, m_ContractContentTax.get_TrxName());
			pstmt.setInt (1,m_ContractContentTax.getJP_ContractContent_ID());
			pstmt.setInt (2, m_ContractContentTax.getC_Tax_ID());
			rs = pstmt.executeQuery ();
			while (rs.next ())
			{
				BigDecimal baseAmt = rs.getBigDecimal(1);
				taxBaseAmt = taxBaseAmt.add(baseAmt);
				//
				if (!documentLevel)		// calculate line tax
					taxAmt = taxAmt.add(calculateTax(tax, baseAmt, m_ContractContentTax.isTaxIncluded(), line.getPrecision(), roundingMode));
			}
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, m_ContractContentTax.get_TrxName(), e);
			taxBaseAmt = null;
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}
		//
		if (taxBaseAmt == null)
			return false;

		//	Calculate Tax
		if (documentLevel)		//	document level
			taxAmt = calculateTax(tax, taxBaseAmt, m_ContractContentTax.isTaxIncluded(), line.getPrecision(), roundingMode);
		m_ContractContentTax.setTaxAmt(taxAmt);

		//	Set Base
		if (m_ContractContentTax.isTaxIncluded())
			m_ContractContentTax.setTaxBaseAmt (taxBaseAmt.subtract(taxAmt));
		else
			m_ContractContentTax.setTaxBaseAmt (taxBaseAmt);
		if (log.isLoggable(Level.FINE)) log.fine(toString());
		return true;

	}	//	calculateTaxFromLines
	
	
	/**
	 * JPIERE-0544: Calculate GL Journal Tax
	 *
	 * @param provider
	 * @param line
	 * @param newRecord
	 * @return
	 */
	default public boolean recalculateTax(MTaxProvider provider, MJournalLine line, boolean newRecord)
	{
		if (!newRecord && !line.getParent().isProcessed() &&
				(line.is_ValueChanged(MGLJournalTax.COLUMNNAME_C_Tax_ID)
				|| line.is_ValueChanged(MGLJournalTax.COLUMNNAME_JP_SOPOType)
				|| line.is_ValueChanged(MJournalLine.COLUMNNAME_AmtSourceDr)
				|| line.is_ValueChanged(MJournalLine.COLUMNNAME_AmtSourceCr)
				))
		{
    		if (!updateGLJournalTax(line, true))
				return false;
		}

		String JP_SOPOType = line.get_ValueAsString(MGLJournalTax.COLUMNNAME_JP_SOPOType);
		if("S".equals(JP_SOPOType) || "P".equals(JP_SOPOType))
		{
			if(!updateGLJournalTax(line, false))
				return false;
		}

		return true;
	}

	/**
	 * JPIERE-0541: Calculate Contract Content Tax
	 *
	 * @param line
	 * @param oldTax
	 * @return
	 */
	private boolean updateGLJournalTax(MJournalLine line, boolean oldTax)
	{
		
		String JP_SOPOType = null;
		if(oldTax)
		{
			Object obj = line.get_ValueOld("JP_SOPOType");
			if(obj != null)
				JP_SOPOType = (String)obj;
			
		}else {
			JP_SOPOType = line.get_ValueAsString("JP_SOPOType");
		}
		
		MGLJournalTax tax = MGLJournalTax.get (line, line.getPrecision(), oldTax, line.get_TrxName());
		if(tax != null)
		{
			if (!calculateTaxFromJournalLines(line,tax,JP_SOPOType))
				return false;
			if (tax.getTaxAmt().signum() != 0 || tax.getTaxBaseAmt().signum() != 0) {
				if (!tax.save(line.get_TrxName()))
					return false;
			} else {
				if (!tax.is_new() && !tax.delete(false, line.get_TrxName()))
					return false;
			}
		}

		return true;
	}
	

	/**
	 * JPIERE-0544: Calculate GL Journal Tax
	 *
	 * @param line
	 * @param m_GLJournalTax
	 * @return
	 */
	private boolean calculateTaxFromJournalLines (MJournalLine line, MGLJournalTax m_GLJournalTax, String JP_SOPOType)
	{	
		if(Util.isEmpty(JP_SOPOType) || "N".equals(JP_SOPOType))
			return true;
		
		BigDecimal taxBaseAmt = Env.ZERO;
		BigDecimal taxAmt = Env.ZERO;

		MTax tax = MTax.get(m_GLJournalTax.getCtx(), m_GLJournalTax.getC_Tax_ID());
		boolean documentLevel = tax.isDocumentLevel();

		RoundingMode roundingMode = JPiereTaxProvider.getRoundingMode(line.getC_BPartner_ID(), JP_SOPOType.equals("S"), tax.getC_TaxProvider());
		int Precision =MCurrency.getStdPrecision(Env.getCtx(), line.getC_Currency_ID());
		//
		String sql = null;
		
		if(JP_SOPOType.equals("S"))
		{
			sql = "SELECT AmtSourceCr - AmtSourceDr FROM GL_JournalLine WHERE GL_Journal_ID=? AND JP_SOPOType=? AND C_Tax_ID=? AND AD_Org_ID = ?";
			
		}else if(JP_SOPOType.equals("P")){
			
			sql = "SELECT AmtSourceDr - AmtSourceCr FROM GL_JournalLine WHERE GL_Journal_ID=? AND JP_SOPOType=? AND C_Tax_ID=? AND AD_Org_ID = ?";
			
		}
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (sql, m_GLJournalTax.get_TrxName());
			pstmt.setInt (1,m_GLJournalTax.getGL_Journal_ID());
			pstmt.setString(2, JP_SOPOType);
			pstmt.setInt (3, m_GLJournalTax.getC_Tax_ID());
			pstmt.setInt (4, m_GLJournalTax.getAD_Org_ID());
			rs = pstmt.executeQuery ();
			while (rs.next ())
			{
				BigDecimal baseAmt = rs.getBigDecimal(1);
				taxBaseAmt = taxBaseAmt.add(baseAmt);
				//
				if (!documentLevel)		// calculate line tax
					taxAmt = taxAmt.add(calculateTax(tax, baseAmt, m_GLJournalTax.isTaxIncluded(), Precision, roundingMode));
			}
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, m_GLJournalTax.get_TrxName(), e);
			taxBaseAmt = null;
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}
		//
		if (taxBaseAmt == null)
			return false;

		//	Calculate Tax
		if (documentLevel)		//	document level
			taxAmt = calculateTax(tax, taxBaseAmt, m_GLJournalTax.isTaxIncluded(), Precision, roundingMode);
		m_GLJournalTax.setTaxAmt(taxAmt);

		//	Set Base
		if (m_GLJournalTax.isTaxIncluded())
			m_GLJournalTax.setTaxBaseAmt (taxBaseAmt.subtract(taxAmt));
		else
			m_GLJournalTax.setTaxBaseAmt (taxBaseAmt);
		if (log.isLoggable(Level.FINE)) log.fine(toString());
		return true;

	}	//	calculateTaxFromLines
}