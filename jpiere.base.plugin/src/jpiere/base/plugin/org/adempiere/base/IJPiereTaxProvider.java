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

import org.adempiere.exceptions.AdempiereException;
import org.adempiere.exceptions.DBException;
import org.compiere.model.MCurrency;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceTax;
import org.compiere.model.MTax;
import org.compiere.model.MTaxProvider;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;

import jpiere.base.plugin.org.adempiere.model.JPiereTaxProvider;
import jpiere.base.plugin.org.adempiere.model.MBill;
import jpiere.base.plugin.org.adempiere.model.MBillLine;
import jpiere.base.plugin.org.adempiere.model.MBillTax;
import jpiere.base.plugin.org.adempiere.model.MEstimation;
import jpiere.base.plugin.org.adempiere.model.MEstimationLine;
import jpiere.base.plugin.org.adempiere.model.MRecognition;
import jpiere.base.plugin.org.adempiere.model.MRecognitionLine;

/**
 * Interface JPiere Tax Provider
 *
 * @author Hideaki Hagiwara
 *
 */
public interface IJPiereTaxProvider {

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


}