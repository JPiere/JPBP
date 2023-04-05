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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.logging.Level;

import org.compiere.model.MBankAccount;
import org.compiere.model.MDepositBatch;
import org.compiere.model.MDepositBatchLine;
import org.compiere.model.MDocType;
import org.compiere.model.MPayment;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Msg;

/**
*
* JPIERE-0601 - Create Deposit Batch
*
*
* @author Hideaki Hagiwara
*
*/
public class CreateDepositBatch extends SvrProcess {

	private String p_BankAccountType = null;
	
	private int p_C_BankAccount_ID = 0;
	
	private Timestamp p_DateTrx_From = null;
	
	private Timestamp p_DateTrx_To = null;
	
	private int p_C_DocType_ID = 0;
	
	@Override
	protected void prepare() 
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
			{
				;
			}else if (name.equals("BankAccountType")){
				p_BankAccountType =  para[i].getParameterAsString();
			}else if (name.equals("C_BankAccount_ID")){
				p_C_BankAccount_ID = para[i].getParameterAsInt();
			}else if (name.equals("DateTrx")){
				p_DateTrx_From = para[i].getParameterAsTimestamp();
				p_DateTrx_To = para[i].getParameter_ToAsTimestamp();
			}else if (name.equals("C_DocType_ID")){
				p_C_DocType_ID = para[i].getParameterAsInt();
			}else{
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}
		}
		
		if(p_DateTrx_To != null)
		{
			p_DateTrx_To = Timestamp.valueOf(LocalDateTime.of(p_DateTrx_To.toLocalDateTime().toLocalDate(), LocalTime.MAX));
		}
		
		if(p_C_DocType_ID == 0)
		{
			p_C_DocType_ID = MDocType.getDocType("JDB");
		}
		
	}

	@Override
	protected String doIt() throws Exception 
	{
		if(p_C_BankAccount_ID == 0)
		{
			 MBankAccount[] m_BankAccounts = getBankAccounts();
			 for(MBankAccount account : m_BankAccounts)
			 {
				 createDepositBatch(account);
			 }
			
		}else {
			
			MBankAccount account = new MBankAccount(getCtx(), p_C_BankAccount_ID, get_TrxName());
			createDepositBatch(account);
		}
		
		return Msg.getMsg(getCtx(), "Success");
	}

	private boolean  createDepositBatch(MBankAccount account)
	{
		ArrayList<MPayment> list = new ArrayList<MPayment>();
		String sql = "SELECT * FROM C_Payment WHERE C_BankAccount_ID = ? AND DateTrx >= ? AND  DateTrx <= ? "//1 - 3
						+ "AND IsActive='Y' AND IsReceipt='Y' AND DocStatus in ('CO','CL') AND AD_Client_ID= ? "
						+ " AND C_DepositBatch_ID is null AND PayAmt <> 0 ";
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, account.getC_BankAccount_ID());
			pstmt.setTimestamp(2, p_DateTrx_From);
			pstmt.setTimestamp(3, p_DateTrx_To);
			pstmt.setInt(4, getAD_Client_ID());
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add(new MPayment (getCtx(), rs, get_TrxName()));
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}
		
		
		if(list.size() == 0)
			return true;
		
		MDepositBatch m_DepositBatch = new MDepositBatch(getCtx(), 0, get_TrxName());
		m_DepositBatch.setC_BankAccount_ID(account.getC_BankAccount_ID());
		m_DepositBatch.setAD_Org_ID(account.getAD_Org_ID());
		m_DepositBatch.setC_DocType_ID(p_C_DocType_ID);
		m_DepositBatch.setDateDeposit(p_DateTrx_To);
		m_DepositBatch.setDateAcct(p_DateTrx_To);
		m_DepositBatch.setDateDoc(p_DateTrx_To);
		m_DepositBatch.setIsActive(true);
		m_DepositBatch.setProcessed(false);
		m_DepositBatch.saveEx(get_TrxName());
		
		int lineNo = 0;
		
		for(MPayment payment : list )
		{
			lineNo = lineNo + 10;
			MDepositBatchLine line = new MDepositBatchLine(getCtx(), 0, get_TrxName());
			line.setAD_Org_ID(m_DepositBatch.getAD_Org_ID());
			line.setC_DepositBatch_ID(m_DepositBatch.getC_DepositBatch_ID());
			line.setLine(lineNo);
			line.setC_Payment_ID(payment.getC_Payment_ID());
			line.saveEx(get_TrxName());
		}
		
		addBufferLog(0, null, null, m_DepositBatch.getDocumentNo(), MDepositBatch.Table_ID, m_DepositBatch.getC_DepositBatch_ID());
		
		return true;
	}
	
	private MBankAccount[] getBankAccounts()
	{
		ArrayList<MBankAccount> list = new ArrayList<MBankAccount>();
		String sql = "SELECT * FROM C_BankAccount WHERE BankAccountType=? AND IsActive='Y' AND AD_Client_ID=? ";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setString(1, p_BankAccountType);
			pstmt.setInt(2, getAD_Client_ID());
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add(new MBankAccount (getCtx(), rs, get_TrxName()));
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

		MBankAccount[] m_accounts = new MBankAccount[list.size()];
		list.toArray(m_accounts);
		return m_accounts;
	}
}
