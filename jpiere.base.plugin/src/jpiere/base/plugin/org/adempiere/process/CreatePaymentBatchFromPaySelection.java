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
import java.util.ArrayList;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MInvoice;
import org.compiere.model.MPaySelection;
import org.compiere.model.MPaySelectionCheck;
import org.compiere.model.MPaySelectionLine;
import org.compiere.model.MPayment;
import org.compiere.model.MPaymentBatch;
import org.compiere.model.X_C_Payment;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Msg;

/**
 * JPIERE-0273 : Create Payment Batch From Pay Selection
 * JPIERE-0580: Select BP Bank Account
 
 * 
 * @author BIT
 * @author h.hagiwara
 *
 */
public class CreatePaymentBatchFromPaySelection extends SvrProcess {

	private String p_PaymentRule = null;
	private int p_C_PaySelection_ID = 0;
	private int p_C_DocTYpe_ID = 0;

	private static final CLogger s_log = CLogger.getCLogger (CreatePaymentBatchFromPaySelection.class);

	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null){
				;
			}else if (name.equals("C_DocType_ID")){
				p_C_DocTYpe_ID = para[i].getParameterAsInt();
			}else if (name.equals("PaymentRule")){
				p_PaymentRule = para[i].getParameterAsString();
			}else{
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}//if

		}//for

		p_C_PaySelection_ID = getRecord_ID();

	}

	@Override
	protected String doIt() throws Exception {

		MPaySelection paySelection = new MPaySelection(getCtx(), p_C_PaySelection_ID, get_TrxName());

		if( paySelection.get_ColumnIndex("JP_PaymentBatch_ID") > 0)
		{
			int JP_PaymentBatch_ID = paySelection.get_ValueAsInt("JP_PaymentBatch_ID");
			if(JP_PaymentBatch_ID > 0)
				return Msg.getMsg(getCtx(), "JP_PaymentBatchAlreadyCreated");//Payment Batch already Created
		}

		MPaySelectionCheck[] checks = MPaySelectionCheck.get(p_C_PaySelection_ID, p_PaymentRule, get_TrxName());
		if(checks == null || checks.length == 0)
		{

			return Msg.getMsg(getCtx(), "FindZeroRecords");//No Records found
		}

		//Double payment check
		MPaySelectionLine[] pLines = paySelection.getLines(true);
		ArrayList <MInvoice> paidInvoiceList = new ArrayList <MInvoice>();
		MInvoice invoice = null;
		for(int i = 0; pLines.length > i ; i++)
		{
			invoice = new MInvoice(getCtx(), pLines[i].getC_Invoice_ID(), get_TrxName());
			if(invoice.isPaid())
			{
				paidInvoiceList.add(invoice);
				addBufferLog(getProcessInfo().getAD_Process_ID(), null, null, invoice.getDocumentNo(), MInvoice.Table_ID, invoice.getC_Invoice_ID());
			}
		}

		if(paidInvoiceList.size() > 0)
		{
			return Msg.getMsg(getCtx(), "Error")
							+ Msg.getElement(getCtx(), "IsPaid", paySelection.get_ValueAsBoolean("IsReceiptJP"))
							+ Msg.getElement(getCtx(), "C_Invoice_ID", paySelection.get_ValueAsBoolean("IsReceiptJP"))
							;
		}


		MPaymentBatch batch = MPaymentBatch.getForPaySelection (getCtx(), getRecord_ID(), get_TrxName());
		batch.setProcessingDate(paySelection.getPayDate());
		batch.set_ValueOfColumnReturningBoolean("IsReceiptJP", paySelection.get_ValueAsBoolean("IsReceiptJP"));//JPIERE-0298
		batch.saveEx(get_TrxName());
		for(MPaySelectionCheck check : checks)
		{
			createAndAllocatePayment(check, batch);
		}


		if( paySelection.get_ColumnIndex("JP_PaymentBatch_ID") > 0)
		{
			paySelection.set_ValueNoCheck("JP_PaymentBatch_ID", batch.getC_PaymentBatch_ID());
			paySelection.saveEx(get_TrxName());
		}

		return Msg.getMsg(getCtx(), "ProcessOK") + " : " + batch.getDocumentNo();
	}

	/**
	 * 	Create And Allocate Payment
	 *  * Partial copy from org.compiere.model.MPaySelectionCheck
	 *  								#confirmPrint (MPaySelectionCheck check, MPaymentBatch batch)
	 * 	@param check MPaySelectionCheck
	 * 	@param batch batch
	 */
	private void createAndAllocatePayment(MPaySelectionCheck check, MPaymentBatch batch)
	{
		MPayment payment = new MPayment(getCtx(), check.getC_Payment_ID(), get_TrxName());
		//	Existing Payment
		if (check.getC_Payment_ID() != 0){

			return ;

		}else{//	New Payment
			payment = new MPayment(check.getCtx(), 0, get_TrxName());
			payment.setAD_Org_ID(check.getAD_Org_ID());
			//
			if (check.getPaymentRule().equals(MPaySelectionCheck.PAYMENTRULE_Check)){
				payment.setBankCheck (check.getParent().getC_BankAccount_ID(), false, check.getDocumentNo());
			}
			else if (check.getPaymentRule().equals(MPaySelectionCheck.PAYMENTRULE_CreditCard)){
				payment.setTenderType(X_C_Payment.TENDERTYPE_CreditCard);
			}
			else if (check.getPaymentRule().equals(MPaySelectionCheck.PAYMENTRULE_DirectDeposit)
				|| check.getPaymentRule().equals(MPaySelectionCheck.PAYMENTRULE_DirectDebit)){
				payment.setBankACH(check);
			}
			else{
				s_log.log(Level.SEVERE, "Unsupported Payment Rule=" + check.getPaymentRule());
				return;
			}
			payment.setTrxType(X_C_Payment.TRXTYPE_CreditPayment);
			payment.setAmount(check.getParent().getC_Currency_ID(), check.getPayAmt());
			payment.setDiscountAmt(check.getDiscountAmt());
			payment.setWriteOffAmt(check.getWriteOffAmt());
			payment.setDateTrx(check.getParent().getPayDate());
			payment.setDateAcct(payment.getDateTrx());
			payment.setC_BPartner_ID(check.getC_BPartner_ID());
			payment.setC_PaymentBatch_ID(batch.getC_PaymentBatch_ID());
			payment.setC_BP_BankAccount_ID(check.getC_BP_BankAccount_ID());

			//	Link to Invoice
			MPaySelectionLine[] psls = check.getPaySelectionLines(true);
			if (s_log.isLoggable(Level.FINE)){
				s_log.fine("createAndAllocatePayment - " + check + " (#SelectionLines=" + psls.length + ")");
			}
			if (check.getQty() == 1 && psls != null && psls.length == 1){
				MPaySelectionLine psl = psls[0];
				if (s_log.isLoggable(Level.FINE)){
					s_log.fine("Map to Invoice " + psl);
				}
				//
				payment.setC_Invoice_ID (psl.getC_Invoice_ID());
				payment.setDiscountAmt (psl.getDiscountAmt());
				payment.setWriteOffAmt (psl.getWriteOffAmt());
				BigDecimal overUnder = psl.getOpenAmt().subtract(psl.getPayAmt())
					.subtract(psl.getDiscountAmt()).subtract(psl.getWriteOffAmt()).subtract(psl.getDifferenceAmt());
				payment.setOverUnderAmt(overUnder);
			}else{
				payment.setWriteOffAmt(Env.ZERO);
				payment.setDiscountAmt(Env.ZERO);
			}
			payment.setC_DocType_ID(p_C_DocTYpe_ID);
			payment.saveEx(get_TrxName());
			//
			int C_Payment_ID = payment.get_ID();
			if (C_Payment_ID < 1){
				s_log.log(Level.SEVERE, "Payment not created=" + check);
			}
			else{
				check.setC_Payment_ID (C_Payment_ID);
				check.saveEx(get_TrxName());	//	Payment process needs it
				// added AdempiereException by zuhri
				if (!payment.processIt(DocAction.ACTION_Complete)){
					throw new AdempiereException("Failed when processing document - " + payment.getProcessMsg());
				}
				// end added
				payment.saveEx(get_TrxName());
			}
		}	//	new Payment

		check.setIsPrinted(true);
		check.setProcessed(true);
		check.saveEx(get_TrxName());
	}	//	createAndAllocatePayment
}
