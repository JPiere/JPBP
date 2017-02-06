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

		StringBuilder sql = new StringBuilder("SELECT p.C_Payment_ID, p.C_BankAccount_ID, p.PayAmt")
		                        		.append(" FROM C_Payment p")
		                                .append(" WHERE p.C_PaymentBatch_ID = ? AND p.IsReconciled = 'N' ")
		                        		.append(" ORDER BY p.DocumentNo")
		                                .append(";");

		try
		{
			boolean isHeaderExists = false;
			int C_BankAccount_ID = 0;
			MBankStatement bankStatement = null;

			pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
			pstmt.setInt(1, C_PaymentBatch_ID);
			rs = pstmt.executeQuery();

			
			
			while( rs.next())
			{
				if(rs.getBigDecimal(3).compareTo(Env.ZERO) == 0)
					continue;
				
				if(!isHeaderExists)
				{
					C_BankAccount_ID = rs.getInt(2);
					bankStatement = new MBankStatement( getCtx(), 0, get_TrxName() );

					MBankAccount bankAccount = MBankAccount.get(getCtx(), C_BankAccount_ID);
					bankStatement.setC_BankAccount_ID(bankAccount.getC_BankAccount_ID());
					bankStatement.setBeginningBalance(bankAccount.getCurrentBalance());

					bankStatement.setAD_Org_ID(paymentBatch.getAD_Org_ID());
					bankStatement.setDocAction(DocAction.ACTION_Complete);
					bankStatement.setDocStatus(DocAction.STATUS_Drafted);
					bankStatement.setStatementDate(p_StatementDate);
					bankStatement.setDateAcct(p_StatementDate);
					bankStatement.setName(Msg.getElement(getCtx(), "C_PaymentBatch_ID") + " : " + paymentBatch.getDocumentNo());
					bankStatement.setIsManual(false);

					bankStatement.saveEx(get_TrxName());

					isHeaderExists = true;
				}else{
					
					if( C_BankAccount_ID != rs.getInt(2) )
					{
						log.log(Level.SEVERE, "Illigal Bank Account : " + rs.getInt(2) );
						continue;
					}
				}

				MPayment payment = new MPayment( getCtx(), rs.getInt(1), get_TrxName() );
				MBankStatementLine bankStatementLine = new MBankStatementLine( bankStatement );
				bankStatementLine.setStatementLineDate(p_StatementDate);
				bankStatementLine.setPayment(payment);

				bankStatementLine.saveEx(get_TrxName());

				count++;
				
			}//while( rs.next())
			
			if(count == 0)
			{
				return Msg.getMsg(getCtx(), "JP_NoPaymentNeedToWriteBS");//There is no Payment that is need to write into Bank Statement
			
			}else{
				bankStatement.processIt(p_DocAction);
				bankStatement.saveEx(get_TrxName());
			}

			addBufferLog(0, null, null, bankStatement.getDocumentInfo(), bankStatement.get_Table_ID(), bankStatement.getC_BankStatement_ID());
			if(paymentBatch.get_ColumnIndex("JP_BankStatement_ID") != -1)
			{
				paymentBatch.set_ValueNoCheck("JP_BankStatement_ID", bankStatement.getC_BankStatement_ID());
				paymentBatch.saveEx(get_TrxName());
			}
			
			
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
