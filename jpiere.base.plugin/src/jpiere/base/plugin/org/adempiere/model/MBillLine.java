/******************************************************************************
 * Product: JPiere(Japan + iDempiere)                                         *
 * Copyright (C) Hideaki Hagiwara (h.hagiwara@oss-erp.co.jp)                  *
 *                                                                            *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY.                          *
 * See the GNU General Public License for more details.                       *
 *                                                                            *
 * JPiere supported by OSS ERP Solutions Co., Ltd.                            *
 * (http://www.oss-erp.co.jp)                                                 *
 *****************************************************************************/

package jpiere.base.plugin.org.adempiere.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;

import org.compiere.model.MDocType;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceTax;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;

/**
 *	MBillLine
 *
 *	JPIERE-0106:JPBP:Bill
 *
 *  @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 */

public class MBillLine extends X_JP_BillLine {

	public MBillLine(Properties ctx, int JP_BillLine_ID, String trxName) {
		super(ctx, JP_BillLine_ID, trxName);
	}

	public MBillLine(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	private MInvoice invoice = null;

	@Override
	protected boolean beforeSave(boolean newRecord) {

		if(newRecord || is_ValueChanged("C_Invoice_ID"))
		{
			if(getC_Invoice_ID()== 0)
			{
				log.saveError("FillMandatory", Msg.getElement(getCtx(), "C_Invoice_ID"));
				return false;
			}

			invoice = new MInvoice(getCtx(),getC_Invoice_ID(), get_TrxName());

			if(invoice.getC_Currency_ID() != getParent().getC_Currency_ID())
			{
				log.saveError("Error", Msg.getMsg(getCtx(), "JP_DifferentCurrency"));
				return false;
			}

			if(getDescription()==null || getDescription().isEmpty())
				setDescription(invoice.getDescription());

			setC_DocType_ID(invoice.getC_DocType_ID());
			setDateInvoiced(invoice.getDateInvoiced());
			setDateAcct(invoice.getDateAcct());
			setC_BPartner_ID(invoice.getC_BPartner_ID());
			setC_BPartner_Location_ID(invoice.getC_BPartner_Location_ID());
			setAD_User_ID(invoice.getAD_User_ID());
			setM_PriceList_ID(invoice.getM_PriceList_ID());
			setSalesRep_ID(invoice.getSalesRep_ID());
			setPaymentRule(invoice.getPaymentRule());
			setC_PaymentTerm_ID(invoice.getC_PaymentTerm_ID());
			setC_Currency_ID(invoice.getC_Currency_ID());

			setTotalLines(invoice.getTotalLines());
			setGrandTotal(invoice.getGrandTotal());

			BigDecimal TaxBaseAmt = Env.ZERO;
			BigDecimal TaxAmt = Env.ZERO;
			MInvoiceTax[] invTaxes = invoice.getTaxes(false);
			for(int i = 0; i < invTaxes.length; i++)
			{
				TaxBaseAmt = TaxBaseAmt.add(invTaxes[i].getTaxBaseAmt());
				TaxAmt = TaxAmt.add(invTaxes[i].getTaxAmt());
			}
			setTaxBaseAmt(TaxBaseAmt);
			setTaxAmt(TaxAmt);
			setPayAmt(invoice.getGrandTotal().subtract(invoice.getOpenAmt()));
			setOverUnderAmt(invoice.getOpenAmt());

			if(invoice.getC_DocType().getDocBaseType().equals(MDocType.DOCBASETYPE_ARCreditMemo))
			{
				setTotalLines(getTotalLines().negate());
				setGrandTotal(getGrandTotal().negate());
				setTaxBaseAmt(getTaxBaseAmt().negate());
				setTaxAmt(getTaxAmt().negate());
				setPayAmt(invoice.getGrandTotal().add(invoice.getOpenAmt()));
//				setOverUnderAmt(getOverUnderAmt().negate());
			}


		}

		return true;
	}

	@Override
	protected boolean afterSave(boolean newRecord, boolean success) {

		if(newRecord || is_ValueChanged("C_Invoice_ID"))
		{

//			String sql = "UPDATE JP_Bill b"
//					+ " SET TotalLines = (SELECT COALESCE(SUM(TotalLines),0) FROM JP_BillLine bl WHERE b.JP_Bill_ID=bl.JP_Bill_ID) "
//						+ ",GrandTotal = (SELECT COALESCE(SUM(GrandTotal),0) FROM JP_BillLine bl WHERE b.JP_Bill_ID=bl.JP_Bill_ID) "
//						+ ",TaxBaseAmt = (SELECT COALESCE(SUM(TaxBaseAmt),0) FROM JP_BillLine bl WHERE b.JP_Bill_ID=bl.JP_Bill_ID) "
//						+ ",TaxAmt = (SELECT COALESCE(SUM(TaxAmt),0) FROM JP_BillLine bl WHERE b.JP_Bill_ID=bl.JP_Bill_ID) "
//						+ ",PayAmt     = (SELECT COALESCE(SUM(PayAmt),0) FROM JP_BillLine bl WHERE b.JP_Bill_ID=bl.JP_Bill_ID) "
//						+ ",OverUnderAmt     = (SELECT COALESCE(SUM(OverUnderAmt),0) FROM JP_BillLine bl WHERE b.JP_Bill_ID=bl.JP_Bill_ID) "
//					+ "WHERE JP_Bill_ID=" + getJP_Bill_ID();

			String sql = "UPDATE JP_Bill b"
					+ " SET (TotalLines"
						+ " ,GrandTotal"
						+ " ,TaxBaseAmt"
						+ " ,TaxAmt"
						+ " ,PayAmt"
						+ " ,OverUnderAmt )"
					+ " = (SELECT COALESCE(SUM(TotalLines),0)"
							+ "  ,COALESCE(SUM(GrandTotal),0)"
							+ "  ,COALESCE(SUM(TaxBaseAmt),0)"
							+ "  ,COALESCE(SUM(TaxAmt),0)"
							+ "  ,COALESCE(SUM(PayAmt),0)"
							+ "  ,COALESCE(SUM(OverUnderAmt),0)"
					+ " FROM JP_BillLine bl WHERE b.JP_Bill_ID=bl.JP_Bill_ID) "
					+ " WHERE JP_Bill_ID=" + getJP_Bill_ID() ;

			int no = DB.executeUpdate(sql, get_TrxName());
			if (no != 1)
			{
				log.saveError("Error", Msg.getMsg(getCtx(), "SaveErrorNotUnique"));
				return false;
			}

			sql = "UPDATE JP_Bill b"
					+" SET JPBillAmt =(SELECT COALESCE(OverUnderAmt,0) + COALESCE(JPCarriedForwardAmt,0) FROM JP_Bill WHERE JP_Bill_ID="+ getJP_Bill_ID() +" )"
					+ " WHERE JP_Bill_ID=" + getJP_Bill_ID() ;
			no = DB.executeUpdate(sql, get_TrxName());
			if (no != 1)
			{
				log.saveError("Error", Msg.getMsg(getCtx(), "SaveErrorNotUnique"));
				return false;
			}


			if(invoice == null)
				invoice = new MInvoice(getCtx(),getC_Invoice_ID(), get_TrxName());

			if(newRecord)
			{
				;
			}else{

				MInvoice invoice_old =new MInvoice(getCtx(),get_ValueOldAsInt("C_Invoice_ID"),get_TrxName());
				Integer JP_Bill_ID = (Integer)invoice_old.get_Value("JP_Bill_ID");
				if(JP_Bill_ID != null && JP_Bill_ID.intValue()== getJP_Bill_ID())
				{
					invoice_old.set_ValueNoCheck("JP_Bill_ID", null);
					invoice_old.save(get_TrxName());
				}
			}

			Integer JP_Bill_ID = (Integer)invoice.get_Value("JP_Bill_ID");
			if(JP_Bill_ID == null || JP_Bill_ID.intValue()==0)
			{
				invoice.set_ValueNoCheck("JP_Bill_ID", getJP_Bill_ID());
				invoice.save(get_TrxName());
			}

		}

		return true;
	}

	@Override
	protected boolean afterDelete(boolean success) {

		String sql = "UPDATE JP_Bill b"
				+ " SET (TotalLines"
					+ " ,GrandTotal"
					+ " ,TaxBaseAmt"
					+ " ,TaxAmt"
					+ " ,PayAmt"
					+ " ,OverUnderAmt )"
				+ " = (SELECT COALESCE(SUM(TotalLines),0)"
						+ "  ,COALESCE(SUM(GrandTotal),0)"
						+ "  ,COALESCE(SUM(TaxBaseAmt),0)"
						+ "  ,COALESCE(SUM(TaxAmt),0)"
						+ "  ,COALESCE(SUM(PayAmt),0)"
						+ "  ,COALESCE(SUM(OverUnderAmt),0)"
				+ " FROM JP_BillLine bl WHERE b.JP_Bill_ID=bl.JP_Bill_ID) "
				+ " WHERE JP_Bill_ID=" + getJP_Bill_ID() ;

		int no = DB.executeUpdate(sql, get_TrxName());
		if (no != 1)
		{
			log.saveError("Error", Msg.getMsg(getCtx(), "SaveErrorNotUnique"));
			return false;
		}

		sql = "UPDATE JP_Bill b"
				+" SET JPBillAmt =(SELECT COALESCE(OverUnderAmt,0) + COALESCE(JPCarriedForwardAmt,0) FROM JP_Bill WHERE JP_Bill_ID="+ getJP_Bill_ID() +" )"
				+ " WHERE JP_Bill_ID=" + getJP_Bill_ID() ;
		no = DB.executeUpdate(sql, get_TrxName());
		if (no != 1)
		{
			log.saveError("Error", Msg.getMsg(getCtx(), "SaveErrorNotUnique"));
			return false;
		}

		if(invoice == null)
			invoice = new MInvoice(getCtx(),getC_Invoice_ID(), get_TrxName());

		Integer JP_Bill_ID = (Integer)invoice.get_Value("JP_Bill_ID");
		if(JP_Bill_ID != null && JP_Bill_ID.intValue()== getJP_Bill_ID())
		{
			invoice.set_ValueNoCheck("JP_Bill_ID", null);
			invoice.save(get_TrxName());
		}

		return true;
	}

	private MBill m_parent = null;

	public MBill getParent()
	{
		if(m_parent == null)
			m_parent = new MBill(getCtx(), getJP_Bill_ID(),get_TrxName());

		return m_parent;
	}

}
