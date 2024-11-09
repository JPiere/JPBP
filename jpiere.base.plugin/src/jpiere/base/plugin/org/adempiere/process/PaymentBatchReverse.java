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

import java.util.List;

import org.compiere.model.MBankStatement;
import org.compiere.model.MInvoice;
import org.compiere.model.MPaySelection;
import org.compiere.model.MPaySelectionLine;
import org.compiere.model.MPayment;
import org.compiere.model.MPaymentBatch;
import org.compiere.model.MRefList;
import org.compiere.model.MSysConfig;
import org.compiere.model.Query;
import org.compiere.process.DocAction;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
import org.compiere.util.Msg;

/**
 * JPIERE-0545 Reverse Payment Batch
 * JPIERE-0581: Auto Calculate Bank Transfer Fee at Payment Selection Line.
 *
 * @author h.hagiwara
 */
public class PaymentBatchReverse extends SvrProcess {

	private int C_PaymentBatch_ID = 0;
	
	@Override
	protected void prepare()
	{
		C_PaymentBatch_ID = getProcessInfo().getRecord_ID();
	}

	@Override
	protected String doIt() throws Exception 
	{
		MPaymentBatch paymentBatch = new MPaymentBatch( getCtx(), C_PaymentBatch_ID, get_TrxName() );
		if(paymentBatch.get_ColumnIndex("JP_BankStatement_ID") != -1)
		{
			if(paymentBatch.get_ValueAsInt("JP_BankStatement_ID") > 0)
			{
				MBankStatement m_BankStatement = new MBankStatement(getCtx(),paymentBatch.get_ValueAsInt("JP_BankStatement_ID"), get_TrxName());
				if(m_BankStatement.getDocStatus().equals(DocAction.STATUS_Completed))
				{
					if(m_BankStatement.processIt(DocAction.ACTION_Void))
					{
						m_BankStatement.saveEx(get_TrxName());
					}else {
						throw new Exception(m_BankStatement.getProcessMsg());
					}
					
				}else if(m_BankStatement.getDocStatus().equals(DocAction.STATUS_Drafted)
						|| m_BankStatement.getDocStatus().equals(DocAction.STATUS_InProgress)){
					
					if(m_BankStatement.processIt(DocAction.ACTION_Void))
					{
						m_BankStatement.saveEx(get_TrxName());
					}else {
						throw new Exception(m_BankStatement.getProcessMsg());
					}
					
				}else if(m_BankStatement.getDocStatus().equals(DocAction.STATUS_Voided)
						|| m_BankStatement.getDocStatus().equals(DocAction.STATUS_Reversed) ){
					
					;//Nothing to do;
					
				}else if(m_BankStatement.getDocStatus().equals(DocAction.STATUS_Closed)){
					
					throw new Exception(Msg.getMsg(getCtx(), "JP_CannotVoid") + " - " + Msg.getElement(getCtx(),MBankStatement.COLUMNNAME_C_BankStatement_ID) + " : " + m_BankStatement.getDocumentNo()); 
					
				}else{
					
					throw new Exception(Msg.getMsg(getCtx(), "JP_CannotVoid") + " - " + Msg.getElement(getCtx(),MBankStatement.COLUMNNAME_C_BankStatement_ID) + " : " + m_BankStatement.getDocumentNo()); 
				}				
			}
			
		}
		
		int successPayment = 0;
		int skipPayment = 0;
		
		int successInvoice = 0;
		int skipInvoice = 0;
		
		String whereClause = "C_PaymentBatch_ID = ?";
		String orderClause = MPayment.COLUMNNAME_DocumentNo;
		List<MPayment> list = new Query(getCtx(), MPayment.Table_Name, whereClause, get_TrxName())
										.setParameters(C_PaymentBatch_ID)
										.setOrderBy(orderClause)
										.list();
		
		for(MPayment m_Payment : list)
		{
			if(m_Payment.isReconciled())
			{
				addBufferLog(0, null, null, "Skip - " + m_Payment.getDocumentNo() + " - " + Msg.getElement(getCtx(), "Reconciled") , m_Payment.get_Table_ID(), m_Payment.getC_Payment_ID());
				continue;
			}
			
			if(m_Payment.getDocStatus().equals(DocAction.STATUS_Drafted)
					|| m_Payment.getDocStatus().equals(DocAction.STATUS_InProgress) )
			{
				if(m_Payment.processIt(DocAction.ACTION_Void))
				{
					m_Payment.saveEx(get_TrxName());
					successPayment++ ;
					
				}else {
					
					throw new Exception(Msg.getMsg(getCtx(), "JP_CannotVoid") + " - " + m_Payment.getDocumentNo() + " : " + m_Payment.getProcessMsg()); 
				}
				
			}else if(m_Payment.getDocStatus().equals(DocAction.STATUS_Completed)) {
				
				if(m_Payment.processIt(DocAction.ACTION_Reverse_Correct))
				{
					m_Payment.saveEx(get_TrxName());
					successPayment++ ;
					
				}else {
					
					throw new Exception(m_Payment.getDocumentNo() + " : " + m_Payment.getProcessMsg()); 
				}
				
			}else {
			
				skipPayment++;
				addBufferLog(0, null, null, "Skip - " + m_Payment.getDocumentNo() + " - " + Msg.getElement(getCtx(), "DocStatus") + " : " + MRefList.getListName(getCtx(), 131, m_Payment.getDocStatus())
										, m_Payment.get_Table_ID(), m_Payment.getC_Payment_ID());
				continue;
			}
			
		}//for
		
		if(successPayment > 0)
		{
			paymentBatch.setProcessed(true);
			paymentBatch.saveEx(get_TrxName());
		}
		
		String returnMsg = Msg.getElement(getCtx(), "C_Payment_ID",paymentBatch.get_ValueAsBoolean("IsReceiptJP")) +" ( " 
								+ Msg.getMsg(getCtx(), "JP_Success") + " : " + successPayment +  " , "
								+ Msg.getMsg(getCtx(), "JP_Failure") + " : "+  skipPayment + " ) ";
		
		
		//JPIERE-0581: Auto Calculate Bank Transfer Fee at Payment Selection Line.
		//Reverse - Invoice That is calculated Bank Transfer Fee at Payment Selection Line automatically.
		boolean isReverse = MSysConfig.getBooleanValue("JP_REVERSE_BANK_TRANSFER_FEE_INVOICE", true, paymentBatch.getAD_Client_ID(), paymentBatch.getAD_Org_ID());	
		if(!isReverse)
		{
			return returnMsg;
		}
		
		
		whereClause = "JP_PaymentBatch_ID = ?";
		List<MPaySelection> m_PaySelectionList = new Query(getCtx(), MPaySelection.Table_Name, whereClause, get_TrxName())
											.setParameters(C_PaymentBatch_ID)
											.list();
		
		if(m_PaySelectionList.size() > 0)
		{
			if(m_PaySelectionList.size() > 1)
			{
				throw new Exception(Msg.getMsg(getCtx(), "JP_UnexpectedError") +" : C_PaySelection.JP_PaymentBatch_ID is Duplicated.");
			}
			MPaySelection paySelection = m_PaySelectionList.get(0);
			
			if(!paySelection.get_ValueAsBoolean("IsReceiptJP"))
			{
				MPaySelectionLine[] paySelectionLines = paySelection.getLines(true);
				for(MPaySelectionLine line : paySelectionLines)
				{
					boolean IsAutoCalBankTransferfeeJP = line.get_ValueAsBoolean("IsAutoCalBankTransferfeeJP");
					if(IsAutoCalBankTransferfeeJP)
					{
						MInvoice m_Invoice = new MInvoice(Env.getCtx(), line.getC_Invoice_ID(), get_TrxName());
						if(m_Invoice.isPaid())
						{
							skipInvoice++;
							addBufferLog(0, null, null, "Skip - " + Msg.getMsg(getCtx(), "JP_Reverse_Bank_Transfer_Fee_Invoice") + " : "
							+  m_Invoice.getDocumentNo() + " - " + Msg.getElement(getCtx(), "DocStatus") + " : " + MRefList.getListName(getCtx(), 131, m_Invoice.getDocStatus())
							, m_Invoice.get_Table_ID(), m_Invoice.getC_Invoice_ID());
							continue;
						}
						
						if(m_Invoice.getDocStatus().equals(DocAction.STATUS_Drafted)
								|| m_Invoice.getDocStatus().equals(DocAction.STATUS_InProgress) )
						{
							if(m_Invoice.processIt(DocAction.ACTION_Void))
							{
								m_Invoice.saveEx(get_TrxName());
								successInvoice++ ;
								
							}else {
								
								throw new Exception(Msg.getMsg(getCtx(), "JP_CannotVoid") + " - " + m_Invoice.getDocumentNo() + " : " + m_Invoice.getProcessMsg()); 
							}
							
						}else if(m_Invoice.getDocStatus().equals(DocAction.STATUS_Completed)){
							
							boolean isOK = m_Invoice.processIt(DocAction.ACTION_Reverse_Correct);
							if(isOK)
							{
								successInvoice++;
								m_Invoice.saveEx(get_TrxName());
							}else {
								
								throw new Exception(Msg.getMsg(getCtx(), "JP_Reverse_Bank_Transfer_Fee_Invoice") + " : " +m_Invoice.getDocumentNo() + " : " + m_Invoice.getProcessMsg()); 
							}
						}else {
							
							skipInvoice++;
							addBufferLog(0, null, null, "Skip - " + Msg.getMsg(getCtx(), "JP_Reverse_Bank_Transfer_Fee_Invoice") + " : "
							+ m_Invoice.getDocumentNo() + " - " + Msg.getElement(getCtx(), "DocStatus") + " : " + MRefList.getListName(getCtx(), 131, m_Invoice.getDocStatus())
							, m_Invoice.get_Table_ID(), m_Invoice.getC_Invoice_ID());
							continue;
							
						}
					}
				}//for
			}
		}
		

		if(skipInvoice != 0 || successInvoice != 0) 
		{
			returnMsg = returnMsg + " / " + Msg.getMsg(getCtx(), "JP_Reverse_Bank_Transfer_Fee_Invoice")  +" ( "  
					+ Msg.getMsg(getCtx(), "JP_Success") + " : " + successInvoice +  " , "
					+ Msg.getMsg(getCtx(), "JP_Failure") + " : "+  skipInvoice + " ) ";
		}
		
		if(skipPayment == 0 && skipInvoice == 0) {
			addBufferLog(0,null,null, Msg.getMsg(getCtx(), "JP_Success"), 0, 0);
		}
		
		return returnMsg;
		
	}

}
