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
import java.util.logging.Level;

import org.compiere.model.MBPartner;
import org.compiere.model.MBPartnerLocation;
import org.compiere.model.MCharge;
import org.compiere.model.MDocType;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MPayment;
import org.compiere.model.MPaymentAllocate;
import org.compiere.model.MPriceList;
import org.compiere.model.MRefList;
import org.compiere.model.MTax;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
import org.compiere.util.Msg;

/**
 * 
 * JPIERE-0611: Create AR Invoice from Income payment for adjust diff between PayAmt and Allocations.
 * 
 * @author h.hagiwara
 *
 */
public class CreateARInvoiceAdjustPaymentDiff extends SvrProcess {

	private int p_C_Payment_ID = 0;
	
	private int p_C_DocType_ID = 0;
	private int p_M_PriceList_ID = 0;
	private int p_C_PaymentTerm_ID = 0;
	private int p_C_Charge_ID = 0;
	private int p_C_Tax_ID = 0;
	
	@Override
	protected void prepare() 
	{
		p_C_Payment_ID = getRecord_ID();

		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null){
				;
			}else if (name.equals("C_DocType_ID")){
				p_C_DocType_ID = para[i].getParameterAsInt();
			}else if (name.equals("M_PriceList_ID")){
				p_M_PriceList_ID = para[i].getParameterAsInt();
			}else if (name.equals("C_PaymentTerm_ID")){
				p_C_PaymentTerm_ID = para[i].getParameterAsInt();
			}else if (name.equals("C_Charge_ID")){
				p_C_Charge_ID = para[i].getParameterAsInt();
			}else if (name.equals("C_Tax_ID")){
				p_C_Tax_ID = para[i].getParameterAsInt();
			}else{
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}//if
		}//for	
	}

	@Override
	protected String doIt() throws Exception 
	{
		//Check Income payment
		if(p_C_Payment_ID == 0)
			throw new Exception(Msg.getMsg(getCtx(), "FillMandatory") + Msg.getElement(getCtx(), "C_Payment_ID") + " - C_Payment_ID = 0");
		
		MPayment m_Payment = new MPayment(getCtx(), p_C_Payment_ID, get_TrxName());
		if(!m_Payment.isReceipt())
			throw new Exception(Msg.getMsg(getCtx(), "Error") + Msg.getElement(getCtx(), "C_Payment_ID,") + " - IsReceipt = false " );	
		
		if(DocAction.STATUS_Completed.equals(m_Payment.getDocStatus())
				|| DocAction.STATUS_Closed.equals(m_Payment.getDocStatus())
				|| DocAction.STATUS_Reversed.equals(m_Payment.getDocStatus())
				|| DocAction.STATUS_Voided.equals(m_Payment.getDocStatus()))
		{
			throw new Exception(Msg.getMsg(getCtx(), "Error") + Msg.getElement(getCtx(), "DocStatus,") + " - " + MRefList.getListName(getCtx(), 131, m_Payment.getDocStatus()));	
		}
		
		MPaymentAllocate[] pAllocs = MPaymentAllocate.get(m_Payment);
		if(pAllocs.length == 0)
			throw new Exception(Msg.getMsg(getCtx(), "NotFound")+ " - " + Msg.getElement(getCtx(), "C_PaymentAllocate_ID"));
		
		BigDecimal totalAllocationAmt = Env.ZERO;
		for(MPaymentAllocate pAlloc : pAllocs)
		{
			totalAllocationAmt = totalAllocationAmt.add(pAlloc.getAmount());
		}
		
		if(totalAllocationAmt.compareTo(m_Payment.getPayAmt()) < 0)
		{
			//This process cannot be executed because the Payment amount is greater than the amount to be Allocated.
			throw new Exception(Msg.getMsg(getCtx(), "JP_CouldNotProcess_PayAmt>Allocate"));
			
		}else if(totalAllocationAmt.compareTo(m_Payment.getPayAmt()) == 0) {
			
			//This process is need not to run, because the Payment amount and the amount to be Allocated art the same.
			throw new Exception(Msg.getMsg(getCtx(), "JP_CouldNotProcess_PayAmt=Allocate"));
		}
		
		//Check Doc Type.
		if(p_C_DocType_ID == 0)
			throw new Exception(Msg.getMsg(getCtx(), "FillMandatory") + Msg.getElement(getCtx(), "C_DocType_ID"));
		
		MDocType m_DocType = new MDocType(getCtx(),p_C_DocType_ID,get_TrxName());
		if(!MDocType.DOCBASETYPE_ARInvoice.equals(m_DocType.getDocBaseType())
				&& !MDocType.DOCBASETYPE_ARCreditMemo.equals(m_DocType.getDocBaseType()) )
		{
			//Incorrect Document Type
			throw new Exception(Msg.getMsg(getCtx(), "JP_DocTypeIncorrect") );
		}
				
		//Check Price List
		if(p_M_PriceList_ID == 0)
			throw new Exception(Msg.getMsg(getCtx(), "FillMandatory") + Msg.getElement(getCtx(), "M_PriceList_ID"));		
		
		MPriceList m_PriceList = new MPriceList(getCtx(), p_M_PriceList_ID, get_TrxName());
		if(!m_PriceList.isSOPriceList())
			throw new Exception(Msg.getMsg(getCtx(), "Error") + Msg.getElement(getCtx(), "PO_PriceList_ID") );	
		if(!m_PriceList.isTaxIncluded())
			throw new Exception(Msg.getMsg(getCtx(), "Error") + Msg.getElement(getCtx(), "M_PriceList_ID")+"." + Msg.getElement(getCtx(), "IsTaxIncluded") + " = False" );	
		if(m_PriceList.getC_Currency_ID() != m_Payment.getC_Currency_ID())
			throw new Exception(Msg.getMsg(getCtx(), "Error") + Msg.getElement(getCtx(), "M_PriceList_ID")+" - " + Msg.getMsg(getCtx(), "JP_DifferentCurrency"));	
		
		//Check Charge.
		if(p_C_Charge_ID == 0)
			throw new Exception(Msg.getMsg(getCtx(), "FillMandatory") + Msg.getElement(getCtx(), "C_Charge_ID"));
		
		MCharge m_Charge = new MCharge(getCtx(), p_C_Charge_ID, get_TrxName());
		if(!m_Charge.isSameTax())
			throw new Exception(Msg.getMsg(getCtx(), "Error") + Msg.getElement(getCtx(), "C_Charge_ID")+"." + Msg.getElement(getCtx(), "IsSameTax") + " = False" );	
		
		//Check Tax.
		if(p_C_Tax_ID == 0)
			throw new Exception(Msg.getMsg(getCtx(), "FillMandatory") + Msg.getElement(getCtx(), "C_Tax_ID"));
		
		MTax m_Tax = new MTax(getCtx(), p_C_Tax_ID, get_TrxName());
		if(MTax.SOPOTYPE_PurchaseTax.equals(m_Tax.getSOPOType()))
		{
			throw new Exception(Msg.getMsg(getCtx(), "Error") + Msg.getElement(getCtx(), "C_Tax_ID")+"." + Msg.getElement(getCtx(), "SOPOType"));
		}
		
		//Check Payment Term
		if(p_C_PaymentTerm_ID == 0)
		{
			p_C_PaymentTerm_ID = m_Payment.getC_BPartner().getC_PaymentTerm_ID();
			if(p_C_PaymentTerm_ID == 0)
			{
				throw new Exception(Msg.getMsg(getCtx(), "NotFound") + Msg.getElement(getCtx(), "C_PaymentTerm_ID"));
			}
		}
		
		//Create AR Invoice
		BigDecimal diffAmt = totalAllocationAmt.subtract(m_Payment.getPayAmt());
		MInvoice m_Invoice = new MInvoice(getCtx(), 0 , get_TrxName());
		m_Invoice.setAD_Org_ID(m_Payment.getAD_Org_ID());
		m_Invoice.setC_DocType_ID(p_C_DocType_ID);
		m_Invoice.setC_DocTypeTarget_ID(p_C_DocType_ID);
		m_Invoice.setIsSOTrx(true);
		m_Invoice.setDateAcct(m_Payment.getDateAcct());
		m_Invoice.setDateInvoiced(m_Payment.getDateAcct());
		m_Invoice.setSalesRep_ID(Env.getAD_User_ID(getCtx()));
		m_Invoice.setC_BPartner_ID(m_Payment.getC_BPartner_ID());
		MBPartner m_BP = new MBPartner(getCtx(), m_Payment.getC_BPartner_ID(), get_TrxName());
		MBPartnerLocation[] m_BPLocations = m_BP.getLocations(false);
		if(m_BPLocations.length == 0)
			throw new Exception(Msg.getMsg(getCtx(), "NotFound") + Msg.getElement(getCtx(), "C_BPartner_Location_ID"));
		m_Invoice.setC_BPartner_Location_ID(m_BPLocations[0].getC_BPartner_Location_ID());
		m_Invoice.setM_PriceList_ID(p_M_PriceList_ID);
		m_Invoice.setPaymentRule(MInvoice.PAYMENTRULE_OnCredit);
		m_Invoice.setC_PaymentTerm_ID(p_C_PaymentTerm_ID);
		m_Invoice.saveEx(get_TrxName());
		
		//Create AR Invoice Line
		MInvoiceLine m_iLine = new MInvoiceLine(m_Invoice);
		m_iLine.setLine(10);
		m_iLine.setC_Charge_ID(p_C_Charge_ID);
		m_iLine.setQtyEntered(Env.ONE);
		if(MDocType.DOCBASETYPE_ARInvoice.equals(m_DocType.getDocBaseType()))
			m_iLine.setPriceEntered(diffAmt.negate());
		else if(MDocType.DOCBASETYPE_ARCreditMemo.equals(m_DocType.getDocBaseType()))
			m_iLine.setPriceEntered(diffAmt);
		m_iLine.setC_Tax_ID(p_C_Tax_ID);
		m_iLine.setC_UOM_ID(100);//Each
		m_iLine.saveEx(get_TrxName());
		
		//Complete
		if(!m_Invoice.processIt(DocAction.ACTION_Complete))
			throw new Exception(Msg.getMsg(getCtx(), "Error") + m_Invoice.getProcessMsg());
		m_Invoice.saveEx(get_TrxName());
		addLog(0, null, null, m_Invoice.getDocumentNo(), MInvoice.Table_ID, m_Invoice.getC_Invoice_ID());
		
		//Create Payment Allocate
		MPaymentAllocate m_pAllocate = new MPaymentAllocate(getCtx(), 0, get_TrxName());
		m_pAllocate.setC_Payment_ID(p_C_Payment_ID);
		m_pAllocate.setAD_Org_ID(m_Payment.getAD_Org_ID());
		m_pAllocate.setC_Invoice_ID(m_Invoice.getC_Invoice_ID());
		
		m_pAllocate.setInvoiceAmt(diffAmt.negate());
		m_pAllocate.setAmount(diffAmt.negate());
		m_pAllocate.setDiscountAmt(Env.ZERO);
		m_pAllocate.saveEx(get_TrxName());
		
		//Created AR Invoice. And added to Allocate. please Confirm it.
		return Msg.getMsg(getCtx(), "JP_CreateARInvoice-AddToAllocate");
	}

}
