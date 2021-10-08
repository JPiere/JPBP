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

package jpiere.base.plugin.org.adempiere.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MDocType;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceTax;
import org.compiere.model.MTax;
import org.compiere.model.MTaxProvider;
import org.compiere.process.DocAction;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;

import jpiere.base.plugin.org.adempiere.base.IJPiereTaxProvider;
import jpiere.base.plugin.util.JPiereUtil;

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

			if(invoice.getDocStatus().equals(DocAction.STATUS_Completed)
					|| invoice.getDocStatus().equals(DocAction.STATUS_Closed))
			{
				;//noting to do;
			}else{
				log.saveError("Error", Msg.getMsg(getCtx(), "JP_Not_Completed_Document"));//Document is not completed
				return false;
			}

			if(invoice.getC_Currency_ID() != getParent().getC_Currency_ID())
			{
				log.saveError("Error", Msg.getMsg(getCtx(), "JP_DifferentCurrency"));
				return false;
			}

			if(getJP_Subject()== null || getJP_Subject().isEmpty())
				setJP_Subject(invoice.get_ValueAsString("JP_Subject"));

			if(getDescription()==null || getDescription().isEmpty())
				setDescription(invoice.getDescription());

			if(getJP_Remarks()== null || getJP_Remarks().isEmpty())
				setJP_Remarks(invoice.get_ValueAsString("JP_Remarks"));

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
			setOpenAmt(invoice.getOpenAmt());

			MDocType invoiceDocType = MDocType.get(invoice.getC_DocTypeTarget_ID());

			if(invoiceDocType.getDocBaseType().equals(MDocType.DOCBASETYPE_ARCreditMemo)
					|| invoiceDocType.getDocBaseType().equals(MDocType.DOCBASETYPE_APCreditMemo) )
			{
				setTotalLines(getTotalLines().negate());
				setGrandTotal(getGrandTotal().negate());
				setTaxBaseAmt(getTaxBaseAmt().negate());
				setTaxAmt(getTaxAmt().negate());
				setPayAmt(getPayAmt().negate());
				setOpenAmt(getOpenAmt().negate());
//				setOverUnderAmt(getOverUnderAmt().negate());
			}

			//JPIERE-0508 Tax Adjust
			if(isTaxAdjustLineJP())
			{
				if(invoiceDocType.getDocBaseType().equals(MDocType.DOCBASETYPE_ARCreditMemo)
						|| invoiceDocType.getDocBaseType().equals(MDocType.DOCBASETYPE_APCreditMemo))
				{
					setTotalLines(Env.ZERO);
					setGrandTotal(invoice.getGrandTotal().negate());
					setTaxBaseAmt(Env.ZERO);
					setTaxAmt(invoice.getGrandTotal().negate());
					setPayAmt(Env.ZERO);
					setOpenAmt(invoice.getGrandTotal().negate());
					setOverUnderAmt(Env.ZERO);
				}else {
					setTotalLines(Env.ZERO);
					setGrandTotal(invoice.getGrandTotal());
					setTaxBaseAmt(Env.ZERO);
					setTaxAmt(invoice.getGrandTotal());
					setPayAmt(Env.ZERO);
					setOpenAmt(invoice.getGrandTotal());
					setOverUnderAmt(Env.ZERO);
				}
			}


		}

		return true;
	}

	@Override
	protected boolean afterSave(boolean newRecord, boolean success) {

		if(newRecord || is_ValueChanged(COLUMNNAME_C_Invoice_ID))
		{

			if(!updateHeaderAndTax(newRecord, success, false))
				return false;

			//Update relation between Invoice and Bill.
			if(invoice == null)
				invoice = new MInvoice(getCtx(),getC_Invoice_ID(), get_TrxName());

			if(newRecord)
			{
				;

			}else{

				MInvoice invoice_old =new MInvoice(getCtx(),get_ValueOldAsInt(COLUMNNAME_C_Invoice_ID),get_TrxName());
				Integer JP_Bill_ID = (Integer)invoice_old.get_Value(COLUMNNAME_JP_Bill_ID);
				if(JP_Bill_ID != null && JP_Bill_ID.intValue()== getJP_Bill_ID())
				{
					invoice_old.set_ValueNoCheck(COLUMNNAME_JP_Bill_ID, null);
					invoice_old.save(get_TrxName());
				}
			}

			Integer JP_Bill_ID = (Integer)invoice.get_Value(COLUMNNAME_JP_Bill_ID);
			if(JP_Bill_ID == null || JP_Bill_ID.intValue()==0)
			{
				invoice.set_ValueNoCheck(COLUMNNAME_JP_Bill_ID, getJP_Bill_ID());
				invoice.save(get_TrxName());
			}

		}

		return true;
	}

	@Override
	protected boolean afterDelete(boolean success)
	{

		if(!updateHeaderAndTax(false, success, true))
			return false;

		if(invoice == null)
			invoice = new MInvoice(getCtx(),getC_Invoice_ID(), get_TrxName());

		Integer JP_Bill_ID = (Integer)invoice.get_Value(COLUMNNAME_JP_Bill_ID);
		if(JP_Bill_ID != null && JP_Bill_ID.intValue()== getJP_Bill_ID())
		{
			invoice.set_ValueNoCheck(COLUMNNAME_JP_Bill_ID, null);
			invoice.save(get_TrxName());
		}

		return true;
	}

	private boolean updateHeaderAndTax(boolean newRecord, boolean success, boolean isDelete)
	{
		if(getParent().isTaxRecalculateJP())
		{

			if(isTaxAdjustLineJP())
			{
				//Tax Recalculation
				if(invoice == null)
					invoice = new MInvoice(getCtx(), getC_Invoice_ID(), get_TrxName());

				MInvoiceTax[] taxes = invoice.getTaxes(true);
				for(MInvoiceTax iTax : taxes)
				{
					MTax m_tax = MTax.get(iTax.getC_Tax_ID());
					MTaxProvider provider = new MTaxProvider(m_tax.getCtx(), m_tax.getC_TaxProvider_ID(), m_tax.get_TrxName());
					IJPiereTaxProvider taxCalculater = JPiereUtil.getJPiereTaxProvider(m_tax);
					if (taxCalculater == null)
					{
						throw new AdempiereException(Msg.getMsg(getCtx(), "TaxNoProvider"));
					}

					success = taxCalculater.updateHeaderTax(provider, this);
			    	if(!success)
			    		return false;

			    	break;
				}//for

			}else {

				//Old Tax Recalculation
				if(!newRecord && is_ValueChanged(COLUMNNAME_C_Invoice_ID) && !isDelete)
				{
					int old_C_Invoice_ID = get_ValueOldAsInt(COLUMNNAME_C_Invoice_ID);
					MInvoice oldInvoice = new MInvoice(getCtx(), old_C_Invoice_ID, get_TrxName());
					MInvoiceTax[] taxes = oldInvoice.getTaxes(true);
					for(MInvoiceTax iTax : taxes)
					{
						MTax m_tax = MTax.get(iTax.getC_Tax_ID());
						MTaxProvider provider = new MTaxProvider(m_tax.getCtx(), m_tax.getC_TaxProvider_ID(), m_tax.get_TrxName());
						IJPiereTaxProvider taxCalculater = JPiereUtil.getJPiereTaxProvider(m_tax);
						if (taxCalculater == null)
						{
							throw new AdempiereException(Msg.getMsg(getCtx(), "TaxNoProvider"));
						}

						success = taxCalculater.recalculateTax(provider, this, oldInvoice, iTax, true);
				    	if(!success)
				    		return false;
					}//for
				}

				//Tax Recalculation
				if(invoice == null)
					invoice = new MInvoice(getCtx(),getC_Invoice_ID(), get_TrxName());

				MInvoiceTax[] taxes = invoice.getTaxes(true);
				for(MInvoiceTax iTax : taxes)
				{
					MTax m_tax = MTax.get(iTax.getC_Tax_ID());
					MTaxProvider provider = new MTaxProvider(m_tax.getCtx(), m_tax.getC_TaxProvider_ID(), m_tax.get_TrxName());
					IJPiereTaxProvider taxCalculater = JPiereUtil.getJPiereTaxProvider(m_tax);
					if (taxCalculater == null)
					{
						throw new AdempiereException(Msg.getMsg(getCtx(), "TaxNoProvider"));
					}

					success = taxCalculater.recalculateTax(provider, this, invoice, iTax, false);
			    	if(!success)
			    		return false;
				}//for
			}

		}else {

			String sql = "UPDATE JP_Bill b"
					+ " SET (TotalLines"
						+ " ,GrandTotal"
						+ " ,TaxBaseAmt"
						+ " ,TaxAmt"
						+ " ,PayAmt"
						+ " ,OpenAmt"
						+ " ,OverUnderAmt )"
					+ " = (SELECT COALESCE(SUM(TotalLines),0)"
							+ "  ,COALESCE(SUM(GrandTotal),0)"
							+ "  ,COALESCE(SUM(TaxBaseAmt),0)"
							+ "  ,COALESCE(SUM(TaxAmt),0)"
							+ "  ,COALESCE(SUM(PayAmt),0)"
							+ "  ,COALESCE(SUM(OpenAmt),0)"
							+ "  ,COALESCE(SUM(OverUnderAmt),0)"
					+ " FROM JP_BillLine bl WHERE b.JP_Bill_ID=bl.JP_Bill_ID) "
					+ " WHERE JP_Bill_ID= ? " ;

			int no = DB.executeUpdate(sql, getJP_Bill_ID(), false, get_TrxName());
			if (no != 1)
			{
				log.saveError("Error", "MBillLine#updateHeaderAndTax() : " + sql);
				return false;
			}

			sql = "UPDATE JP_Bill SET JPBillAmt = COALESCE(OpenAmt,0) + COALESCE(JPCarriedForwardAmt,0) WHERE JP_Bill_ID= ? ";

			no = DB.executeUpdate(sql, getJP_Bill_ID(), false, get_TrxName());
			if (no != 1)
			{
				log.saveError("Error", "MBillLine#updateHeaderAndTax() : " + sql);
				return false;
			}

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

	public void clearParent()
	{
		this.m_parent = null;
	}

}
