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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.logging.Level;

import org.compiere.model.MBankAccount;
import org.compiere.model.MBankStatement;
import org.compiere.model.MBankStatementLine;
import org.compiere.model.MPayment;
import org.compiere.model.MPaymentBatch;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;


/**
 * JPIERE-0280 Create Bank Statement From Payment Batch
 *
 *
 * ref:org.compiere.grid.CreateFromStatement.java
 */
public class CreateBankStatementFromPaymentBatch extends SvrProcess {

	private int C_PaymentBatch_ID = 0;
	private String	   p_DocAction = null;
	private Timestamp p_StatementDate = null;

	@Override
	protected void prepare() {

		C_PaymentBatch_ID = getProcessInfo().getRecord_ID();

		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null){
				;
			}else if (name.equals("StatementDate")){
				p_StatementDate = para[i].getParameterAsTimestamp();
			}else if (name.equals("DocAction")){
				p_DocAction = para[i].getParameterAsString();
			}
			else{
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}

		}
		
	}

	@Override
	protected String doIt() throws Exception {
		
		if(p_StatementDate == null)
			return Msg.getMsg(getCtx(), "FillMandatory")+ " : " + Msg.getElement(getCtx(), "StatementDate");
		
		MPaymentBatch paymentBatch = new MPaymentBatch( getCtx(), C_PaymentBatch_ID, get_TrxName() );
		if(paymentBatch.get_ColumnIndex("JP_BankStatement_ID") != -1)
		{
			if(paymentBatch.get_ValueAsInt("JP_BankStatement_ID") > 0)
				return Msg.getMsg(getCtx(), "JP_PaymentBatchWrittenBS");//Payment Batch was written into Bank Statement
		}
		
		
		int count = 0;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		StringBuilder sql = new StringBuilder("SELECT p.* ")
		                        		.append(" FROM C_Payment p")
		                                .append(" WHERE p.C_PaymentBatch_ID = ? AND p.IsReconciled = 'N' ")
		                        		.append(" ORDER BY p.DocumentNo")
		                                .append(";");

		try
		{
			boolean isHeaderExists = false;
			MBankStatement m_bankStatement = null;
			MPayment m_Payment = null;

			pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
			pstmt.setInt(1, C_PaymentBatch_ID);
			rs = pstmt.executeQuery();			
			
			while( rs.next())
			{
				m_Payment = new MPayment(getCtx(), rs, get_TrxName());
				if(m_Payment.getPayAmt().equals(Env.ZERO) 
						|| (!m_Payment.getDocStatus().equals(DocAction.STATUS_Completed) && !m_Payment.getDocStatus().equals(DocAction.STATUS_Closed))
						|| m_Payment.isReconciled() )
				{
					continue;
				}
				
				if(!isHeaderExists)
				{
					m_bankStatement = new MBankStatement( getCtx(), 0, get_TrxName() );
					MBankAccount bankAccount = MBankAccount.get(getCtx(),  m_Payment.getC_BankAccount_ID());
					m_bankStatement.setC_BankAccount_ID(bankAccount.getC_BankAccount_ID());
					m_bankStatement.setBeginningBalance(bankAccount.getCurrentBalance());

					m_bankStatement.setAD_Org_ID(paymentBatch.getAD_Org_ID());
					m_bankStatement.setDocAction(DocAction.ACTION_Complete);
					m_bankStatement.setDocStatus(DocAction.STATUS_Drafted);
					m_bankStatement.setStatementDate(p_StatementDate);
					m_bankStatement.setDateAcct(p_StatementDate);
					m_bankStatement.setName(Msg.getElement(getCtx(), "C_PaymentBatch_ID") + " : " + paymentBatch.getDocumentNo());
					m_bankStatement.setIsManual(false);

					m_bankStatement.saveEx(get_TrxName());

					isHeaderExists = true;
					
				}else{
					
					if( m_bankStatement.getC_BankAccount_ID() != m_Payment.getC_BankAccount_ID())
					{
						//Different between {0} and {1}
						String msg0 = Msg.getElement(Env.getCtx(), MBankStatement.COLUMNNAME_C_BankStatement_ID) +" - " + Msg.getElement(Env.getCtx(), MBankStatement.COLUMNNAME_C_BankAccount_ID);
						String msg1 = Msg.getElement(Env.getCtx(), MPayment.COLUMNNAME_C_Payment_ID, paymentBatch.get_ValueAsBoolean("IsReceiptJP")) +" - " + Msg.getElement(Env.getCtx(),  MPayment.COLUMNNAME_C_BankAccount_ID);
						String msg = Msg.getMsg(Env.getCtx(),"JP_Different",new Object[]{msg0,msg1});
						
						log.log(Level.SEVERE, msg );
						addBufferLog(0, null, null, m_Payment.getDocumentNo() + " " + msg, MPayment.Table_ID, m_Payment.getC_Payment_ID());
						continue;
					}
				}

				MBankStatementLine bankStatementLine = new MBankStatementLine( m_bankStatement );
				bankStatementLine.setStatementLineDate(p_StatementDate);
				bankStatementLine.setPayment(m_Payment);
				bankStatementLine.saveEx(get_TrxName());

				count++;
				
			}//while( rs.next())
			
			if(count == 0)
			{
				return Msg.getMsg(getCtx(), "JP_NoPaymentNeedToWriteBS");//There is no Payment that is need to write into Bank Statement
			
			}else{
				m_bankStatement.processIt(p_DocAction);
				m_bankStatement.saveEx(get_TrxName());
			}

			addBufferLog(0, null, null, m_bankStatement.getDocumentInfo(), m_bankStatement.get_Table_ID(), m_bankStatement.getC_BankStatement_ID());
			if(paymentBatch.get_ColumnIndex("JP_BankStatement_ID") != -1)
			{
				paymentBatch.set_ValueNoCheck("JP_BankStatement_ID", m_bankStatement.getC_BankStatement_ID());
			}
			
			paymentBatch.setProcessed(true);
			paymentBatch.saveEx(get_TrxName());
			
		}catch(Exception e){
			
			log.log(Level.SEVERE, sql.toString(), e);
			throw e;
			
		}finally{
			
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
			
		}//try

		return Msg.getElement(getCtx(), "JP_RegistrationQty") + " : " + String.valueOf(count);
	}

}
