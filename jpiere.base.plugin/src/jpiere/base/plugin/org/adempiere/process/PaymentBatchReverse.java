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
import org.compiere.model.MPayment;
import org.compiere.model.MPaymentBatch;
import org.compiere.model.MRefList;
import org.compiere.model.Query;
import org.compiere.process.DocAction;
import org.compiere.process.SvrProcess;
import org.compiere.util.Msg;

/**
 * JPIERE-0545 Reverse Payment Batch
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
					
					;//Noting to do;
					
				}else if(m_BankStatement.getDocStatus().equals(DocAction.STATUS_Closed)){
					
					throw new Exception(Msg.getMsg(getCtx(), "JP_CannotVoid") + " - " + Msg.getElement(getCtx(),MBankStatement.COLUMNNAME_C_BankStatement_ID) + " : " + m_BankStatement.getDocumentNo()); 
					
				}else{
					
					throw new Exception(Msg.getMsg(getCtx(), "JP_CannotVoid") + " - " + Msg.getElement(getCtx(),MBankStatement.COLUMNNAME_C_BankStatement_ID) + " : " + m_BankStatement.getDocumentNo()); 
				}				
			}
			
		}
		
		int cnt = 0;
		
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
					cnt++ ;
					
				}else {
					
					throw new Exception(Msg.getMsg(getCtx(), "JP_CannotVoid") + " - " + m_Payment.getDocumentNo() + " : " + m_Payment.getProcessMsg()); 
				}
				
			}else if(m_Payment.getDocStatus().equals(DocAction.STATUS_Completed)) {
				
				if(m_Payment.processIt(DocAction.ACTION_Reverse_Correct))
				{
					m_Payment.saveEx(get_TrxName());
					cnt++ ;
					
				}else {
					
					throw new Exception(m_Payment.getDocumentNo() + " : " + m_Payment.getProcessMsg()); 
				}
				
			}else {
			
				addBufferLog(0, null, null, "Skip - " + m_Payment.getDocumentNo() + " - " + Msg.getElement(getCtx(), "DocStatus") + " : " + MRefList.getListName(getCtx(), 131, m_Payment.getDocStatus())
										, m_Payment.get_Table_ID(), m_Payment.getC_Payment_ID());
				continue;
			}
			
		}//for
		
		if(cnt > 0)
		{
			paymentBatch.setProcessed(true);
			paymentBatch.saveEx(get_TrxName());
		}

		return "OK";
		
	}

}
