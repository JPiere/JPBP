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
import java.util.logging.Level;

import org.adempiere.model.ImportValidator;
import org.adempiere.process.ImportProcess;
import org.adempiere.util.IProcessUI;
import org.compiere.model.MBankAccount;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.X_C_BankAccount_Acct;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.X_I_BankAccountJP;
import jpiere.base.plugin.util.JPiereValidCombinationUtil;
import jpiere.base.plugin.util.ZenginCheck;

/**
 * 	JPIERE-0397:Import Bank Account
 *
 *  @author Hideaki Hagiwara
 *
 */
public class JPiereImportBankAccount extends SvrProcess implements ImportProcess
{
	/**	Client to be imported to		*/
	private int				m_AD_Client_ID = 0;

	private boolean p_deleteOldImported = false;

	/**	Only validate, don't import		*/
	private boolean	p_IsValidateOnly = false;

	private IProcessUI processMonitor = null;

	/**
	 *  Prepare - e.g., get Parameters.
	 */
	protected void prepare()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (name.equals("DeleteOldImported"))
				p_deleteOldImported = "Y".equals(para[i].getParameter());
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}

		m_AD_Client_ID = getProcessInfo().getAD_Client_ID();

	}	//	prepare

	@Override
	public String getImportTableName() {
		return X_I_BankAccountJP.Table_Name;
	}


	@Override
	public String getWhereClause() {
		StringBuilder msgreturn = new StringBuilder(" AND AD_Client_ID=").append(m_AD_Client_ID);
		return msgreturn.toString();
	}

	/**
	 * 	Process
	 *	@return info
	 *	@throws Exception
	 */
	protected String doIt() throws Exception
	{
		processMonitor = Env.getProcessUI(getCtx());

		StringBuilder sql = null;
		int no = 0;
		StringBuilder clientCheck = new StringBuilder(" AND AD_Client_ID=").append(getAD_Client_ID());


		//Delete Old Imported data
		if (p_deleteOldImported)
		{
			sql = new StringBuilder ("DELETE I_BankAccountJP ")
				  .append("WHERE I_IsImported='Y'").append (clientCheck);
			try {
				no = DB.executeUpdate(sql.toString(), get_TrxName());
				if (log.isLoggable(Level.FINE)) log.fine("Delete Old Impored =" + no);
			}catch (Exception e) {
				throw new Exception(Msg.getMsg(getCtx(), "Error") + sql );
			}
		}

		//Reset Message
		sql = new StringBuilder ("UPDATE I_BankAccountJP ")
				.append("SET I_ErrorMsg='' ")
				.append(" WHERE I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(String.valueOf(no));
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + sql );
		}

		ModelValidationEngine.get().fireImportValidate(this, null, null, ImportValidator.TIMING_BEFORE_VALIDATE);

		//Reverse Lookup Surrogate Key
		reverseLookupAD_Org_ID();
		reverseLookupC_Bank_ID();
		reverseLookupC_BankAccount_ID();
		reverseLookupC_Currency_ID();
		reverseLookupC_AcctSchema_ID();

		ModelValidationEngine.get().fireImportValidate(this, null, null, ImportValidator.TIMING_AFTER_VALIDATE);

		commitEx();
		if (p_IsValidateOnly)
		{
			return "Validated";
		}

		//
		sql = new StringBuilder ("SELECT * FROM I_BankAccountJP WHERE I_IsImported='N'")
					.append(clientCheck);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int recordsNum = 0;
		int successNewNum = 0;
		int successUpdateNum = 0;
		int failureNewNum = 0;
		int failureUpdateNum = 0;
		String records = Msg.getMsg(getCtx(), "JP_NumberOfRecords");
		String success = Msg.getMsg(getCtx(), "JP_Success");
		String failure = Msg.getMsg(getCtx(), "JP_Failure");
		String newRecord = Msg.getMsg(getCtx(), "New");
		String updateRecord = Msg.getMsg(getCtx(), "Update");

		try
		{
			pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
			rs = pstmt.executeQuery();
			while (rs.next())
			{
				X_I_BankAccountJP imp = new X_I_BankAccountJP (getCtx (), rs, get_TrxName());

				boolean isNew = true;
				if(imp.getC_BankAccount_ID()!=0){
					isNew =false;
				}

				if(isNew)//Create
				{
					MBankAccount newBankAccount = new MBankAccount(getCtx(), 0, get_TrxName());
					if(createNewBankAccount(imp,newBankAccount))
						successNewNum++;
					else
						failureNewNum++;


				}else{//Update

					MBankAccount updateBankAccount = new MBankAccount(getCtx(), imp.getC_BankAccount_ID(), get_TrxName());

					if(updateBankAccount(imp,updateBankAccount))
						successUpdateNum++;
					else
						failureUpdateNum++;

				}

				commitEx();

				recordsNum++;
				if (processMonitor != null)
				{
					processMonitor.statusUpdate(
						newRecord + "( "+  success + " : " + successNewNum + "  /  " +  failure + " : " + failureNewNum + " ) + "
						+ updateRecord + " ( "+  success + " : " + successUpdateNum + "  /  " +  failure + " : " + failureUpdateNum+ " ) "
						);
				}

			}//while (rs.next())

		}catch (Exception e){
			log.log(Level.SEVERE, sql.toString(), e);
			throw e;
		}finally{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

		return records + recordsNum + " = "	+
		newRecord + "( "+  success + " : " + successNewNum + "  /  " +  failure + " : " + failureNewNum + " ) + "
		+ updateRecord + " ( "+  success + " : " + successUpdateNum + "  /  " +  failure + " : " + failureUpdateNum+ " ) ";

	}	//	doIt


	private void setBankAccountAcct(MBankAccount pc, X_I_BankAccountJP imp)
	{

		X_C_BankAccount_Acct acct = null;

		String WhereClause = " C_AcctSchema_ID=" +imp.getC_AcctSchema_ID() + " AND C_BankAccount_ID=" + pc.getC_BankAccount_ID() + " AND AD_Client_ID=" +Env.getAD_Client_ID(Env.getCtx());

		StringBuilder sql = new StringBuilder ("SELECT * FROM C_BankAccount_Acct WHERE " + WhereClause);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
			rs = pstmt.executeQuery();

			if (rs.next())
			{
				acct = new X_C_BankAccount_Acct (getCtx (), rs, get_TrxName());
			}

		}catch (Exception e){

			log.log(Level.SEVERE, sql.toString(), e);

		}finally{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}


		if(acct == null)
			return ;

		//B_Asset_Acct
		if(!Util.isEmpty(imp.getJP_B_Asset_Acct_Value()))
		{
			int B_Asset_Acct = JPiereValidCombinationUtil.searchCreateValidCombination (getCtx(), imp.getC_AcctSchema_ID(), imp.getJP_B_Asset_Acct_Value(), get_TrxName());
			if(B_Asset_Acct > 0)
			{
				imp.setB_Asset_Acct(B_Asset_Acct);

				if(acct.getB_Asset_Acct() != B_Asset_Acct)
				{
					acct.setB_Asset_Acct(B_Asset_Acct);
					String msg = Msg.getMsg(getCtx(), "Update") + ": " + Msg.getElement(getCtx(), "B_Asset_Acct");

					if(Util.isEmpty(imp.getI_ErrorMsg()))
					{
						imp.setI_ErrorMsg(msg);
					}else {
						imp.setI_ErrorMsg(imp.getI_ErrorMsg()+ " / " + msg);
					}
				}
			}
		}

		//B_InTransit_Acct
		if(!Util.isEmpty(imp.getJP_InTransit_Acct_Value()))
		{
			int B_InTransit_Acct = JPiereValidCombinationUtil.searchCreateValidCombination (getCtx(), imp.getC_AcctSchema_ID(), imp.getJP_InTransit_Acct_Value(), get_TrxName());
			if(B_InTransit_Acct > 0)
			{
				imp.setB_InTransit_Acct(B_InTransit_Acct);
				if(acct.getB_InTransit_Acct() != B_InTransit_Acct)
				{
					acct.setB_Asset_Acct(B_InTransit_Acct);
					String msg = Msg.getMsg(getCtx(), "Update") + ": " + Msg.getElement(getCtx(), "B_InTransit_Acct");

					if(Util.isEmpty(imp.getI_ErrorMsg()))
					{
						imp.setI_ErrorMsg(msg);
					}else {
						imp.setI_ErrorMsg(imp.getI_ErrorMsg()+ " / " + msg);
					}
				}
			}
		}

		//B_PaymentSelect_Acct
		if(!Util.isEmpty(imp.getJP_PaymentSelect_Value()))
		{
			int B_PaymentSelect_Acct = JPiereValidCombinationUtil.searchCreateValidCombination (getCtx(), imp.getC_AcctSchema_ID(), imp.getJP_PaymentSelect_Value(), get_TrxName());
			if(B_PaymentSelect_Acct > 0)
			{
				imp.setB_PaymentSelect_Acct(B_PaymentSelect_Acct);
				if(acct.getB_PaymentSelect_Acct() != B_PaymentSelect_Acct)
				{
					acct.setB_Asset_Acct(B_PaymentSelect_Acct);
					String msg = Msg.getMsg(getCtx(), "Update") + ": " + Msg.getElement(getCtx(), "B_PaymentSelect_Acct");

					if(Util.isEmpty(imp.getI_ErrorMsg()))
					{
						imp.setI_ErrorMsg(msg);
					}else {
						imp.setI_ErrorMsg(imp.getI_ErrorMsg()+ " / " + msg);
					}
				}
			}
		}

		//B_UnallocatedCash_Acct
		if(!Util.isEmpty(imp.getJP_UnallocatedCash_Value()))
		{
			int B_UnallocatedCash_Acct = JPiereValidCombinationUtil.searchCreateValidCombination (getCtx(), imp.getC_AcctSchema_ID(), imp.getJP_UnallocatedCash_Value(), get_TrxName());
			if(B_UnallocatedCash_Acct > 0)
			{
				imp.setB_UnallocatedCash_Acct(B_UnallocatedCash_Acct);
				if(acct.getB_UnallocatedCash_Acct() != B_UnallocatedCash_Acct)
				{
					acct.setB_Asset_Acct(B_UnallocatedCash_Acct);
					String msg = Msg.getMsg(getCtx(), "Update") + ": " + Msg.getElement(getCtx(), "B_UnallocatedCash_Acct");

					if(Util.isEmpty(imp.getI_ErrorMsg()))
					{
						imp.setI_ErrorMsg(msg);
					}else {
						imp.setI_ErrorMsg(imp.getI_ErrorMsg()+ " / " + msg);
					}
				}
			}
		}

		//B_InterestExp_Acct
		if(!Util.isEmpty(imp.getJP_InterestExp_Acct_Value()))
		{
			int B_InterestExp_Acct = JPiereValidCombinationUtil.searchCreateValidCombination (getCtx(), imp.getC_AcctSchema_ID(), imp.getJP_InterestExp_Acct_Value(), get_TrxName());
			if(B_InterestExp_Acct > 0)
			{
				imp.setB_InterestExp_Acct(B_InterestExp_Acct);
				if(acct.getB_InterestExp_Acct() != B_InterestExp_Acct)
				{
					acct.setB_Asset_Acct(B_InterestExp_Acct);
					String msg = Msg.getMsg(getCtx(), "Update") + ": " + Msg.getElement(getCtx(), "B_InterestExp_Acct");

					if(Util.isEmpty(imp.getI_ErrorMsg()))
					{
						imp.setI_ErrorMsg(msg);
					}else {
						imp.setI_ErrorMsg(imp.getI_ErrorMsg()+ " / " + msg);
					}
				}
			}
		}

		//B_InterestRev_Acct
		if(!Util.isEmpty(imp.getJP_InterestRev_Acct_Value()))
		{
			int B_InterestRev_Acct = JPiereValidCombinationUtil.searchCreateValidCombination (getCtx(), imp.getC_AcctSchema_ID(), imp.getJP_InterestRev_Acct_Value(), get_TrxName());
			if(B_InterestRev_Acct > 0)
			{
				imp.setB_InterestRev_Acct(B_InterestRev_Acct);
				if(acct.getB_InterestRev_Acct() != B_InterestRev_Acct)
				{
					acct.setB_Asset_Acct(B_InterestRev_Acct);
					String msg = Msg.getMsg(getCtx(), "Update") + ": " + Msg.getElement(getCtx(), "B_InterestRev_Acct");

					if(Util.isEmpty(imp.getI_ErrorMsg()))
					{
						imp.setI_ErrorMsg(msg);
					}else {
						imp.setI_ErrorMsg(imp.getI_ErrorMsg()+ " / " + msg);
					}
				}
			}
		}

		acct.saveEx(get_TrxName());

	}//setBankAccountAcct


	/**
	 * Reverse Look up Organization From JP_Org_Value
	 *
	 **/
	private void reverseLookupAD_Org_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		String msg = new String();
		int no = 0;

		//Reverese Look up AD_Org ID From JP_Org_Value
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "AD_Org_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "JP_Org_Value") ;
		sql = new StringBuilder ("UPDATE I_BankAccountJP i ")
				.append("SET AD_Org_ID=(SELECT AD_Org_ID FROM AD_org p")
				.append(" WHERE i.JP_Org_Value=p.Value AND (p.AD_Client_ID=i.AD_Client_ID or p.AD_Client_ID=0) )")
				.append(" WHERE i.JP_Org_Value IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no );
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + sql );
		}

		//Invalid JP_Org_Value
		msg = Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_Org_Value");
		sql = new StringBuilder ("UPDATE I_BankAccountJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append(" WHERE AD_Org_ID = 0 AND JP_Org_Value IS NOT NULL AND JP_Org_Value <> '0' ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		if(no > 0)
		{
			commitEx();
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg );
		}

	}//reverseLookupAD_Org_ID

	/**
	 * Reverese Look up C_Bank_ID From JP_Bank_Name
	 *
	 * @throws Exception
	 */
	private void reverseLookupC_Bank_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		String msg = new String();
		int no = 0;

		//Reverese Look up C_Bank_ID From JP_Bank_Name
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_Bank_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "JP_Bank_Name") ;
		sql = new StringBuilder ("UPDATE I_BankAccountJP i ")
				.append("SET C_Bank_ID=(SELECT C_Bank_ID FROM C_Bank p")
				.append(" WHERE i.JP_Bank_Name=p.Name AND p.AD_Client_ID=i.AD_Client_ID) ")
				.append(" WHERE i.C_Bank_ID IS NULL AND JP_Bank_Name IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + sql );
		}

		//Invalid JP_Bank_Name
		msg = Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_Bank_Name");
		sql = new StringBuilder ("UPDATE I_BankAccountJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append(" WHERE C_Bank_ID IS NULL AND JP_Bank_Name IS NOT NULL")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no );
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		if(no > 0)
		{
			commitEx();
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg );
		}

	}//reverseLookupC_Bank_ID

	/**
	 * Reverese Look up C_BankAccount_ID From Value & AccountNo
	 *
	 * @throws Exception
	 */
	private void reverseLookupC_BankAccount_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		String msg = new String();
		int no = 0;

		//Reverese Look up C_BankAccount_ID From JP_BranchCode and AccountNo
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_BankAccount_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "JP_BranchCode")  + " : " + Msg.getElement(getCtx(), "AccountNo") ;
		sql = new StringBuilder ("UPDATE I_BankAccountJP i ")
				.append("SET C_BankAccount_ID=(SELECT C_BankAccount_ID FROM C_BankAccount p")
				.append(" WHERE i.JP_BranchCode=p.JP_BranchCode AND i.AccountNo=p.AccountNo AND i.C_Bank_ID = p.C_Bank_ID) ")
				.append(" WHERE i.AccountNo IS NOT NULL AND i.JP_BranchCode IS NOT NULL ")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + sql );
		}

		//Reverese Look up C_BankAccount_ID From Value
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_BankAccount_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "Value") ;
		sql = new StringBuilder ("UPDATE I_BankAccountJP i ")
				.append("SET C_BankAccount_ID=(SELECT C_BankAccount_ID FROM C_BankAccount p")
				.append(" WHERE i.Value=p.Value AND i.AD_Client_ID=p.AD_Client_ID AND i.C_Bank_ID = p.C_Bank_ID) ")
				.append(" WHERE i.Value IS NOT NULL AND C_BankAccount_ID IS NULL ")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + sql );
		}

	}//reverseLookupC_BankAccount_ID

	/**
	 *
	 * Reverese Look up C_Currency_ID From ISO_Code
	 *
	 * @throws Exception
	 */
	private void reverseLookupC_Currency_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		String msg = new String();
		int no = 0;

		//Reverese Look up C_Currency_ID From ISO_Code
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_Currency_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "ISO_Code") ;
		sql = new StringBuilder ("UPDATE I_BankAccountJP i ")
				.append("SET C_Currency_ID=(SELECT C_Currency_ID FROM C_Currency p")
				.append(" WHERE i.ISO_Code=p.ISO_Code AND (p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0) ) ")
				.append(" WHERE i.C_Currency_ID IS NULL AND ISO_Code IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no + ":" + sql);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + sql );
		}

		//Invalid ISO_Code
		msg = Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "ISO_Code");
		sql = new StringBuilder ("UPDATE I_BankAccountJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append(" WHERE C_Currency_ID IS NULL AND ISO_Code IS NOT NULL")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no + ":" + sql);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		if(no > 0)
		{
			commitEx();
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg );
		}

	}//reverseLookupC_Bank_ID

	/**
	 * Reverse look Up  C_AcctSchema_ID From JP_AcctSchema_Name
	 *
	 * @throws Exception
	 */
	private void reverseLookupC_AcctSchema_ID()throws Exception
	{
		StringBuilder sql = new StringBuilder();
		String msg = new String();
		int no = 0;

		//Reverse look Up  C_AcctSchema_ID From JP_AcctSchema_Name
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_AcctSchema_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "JP_AcctSchema_Name") ;
		sql = new StringBuilder ("UPDATE I_BankAccountJP i ")
				.append("SET C_AcctSchema_ID=(SELECT C_AcctSchema_ID FROM C_AcctSchema p")
				.append(" WHERE i.JP_AcctSchema_Name=p.Name AND p.AD_Client_ID=i.AD_Client_ID) ")
				.append(" WHERE i.C_AcctSchema_ID IS NULL AND JP_AcctSchema_Name IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no + ":" + sql);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + sql );
		}

		//Invalid JP_AcctSchema_Name
		msg = Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_AcctSchema_Name");
		sql = new StringBuilder ("UPDATE I_BankAccountJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append(" WHERE C_AcctSchema_ID IS NULL AND JP_AcctSchema_Name IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		if(no > 0)
		{
			commitEx();
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg );
		}

	}


	/**
	 * Creaet New Bank Account
	 *
	 * @param importBankAccount
	 * @param newBankAccount
	 * @return
	 */
	private boolean createNewBankAccount(X_I_BankAccountJP importBankAccount, MBankAccount newBankAccount)
	{
		ModelValidationEngine.get().fireImportValidate(this, importBankAccount, newBankAccount, ImportValidator.TIMING_BEFORE_IMPORT);

		//AD_Org_ID
		if(importBankAccount.getAD_Org_ID() > 0)
		{
			newBankAccount.setAD_Org_ID(importBankAccount.getAD_Org_ID());
		}else {
			importBankAccount.setI_ErrorMsg(Msg.getMsg(getCtx(), "Error")+ " AD_Org_ID = 0");
			importBankAccount.setI_IsImported(false);
			importBankAccount.setProcessed(false);
			importBankAccount.saveEx(get_TrxName());
			return false;
		}

		//C_Bank_ID
		if(importBankAccount.getC_Bank_ID() > 0)
		{
			newBankAccount.setC_Bank_ID(importBankAccount.getC_Bank_ID());
		}else {
			importBankAccount.setI_ErrorMsg(Msg.getMsg(getCtx(), "Error")+ " C_Bank_ID = 0");
			importBankAccount.setI_IsImported(false);
			importBankAccount.setProcessed(false);
			importBankAccount.saveEx(get_TrxName());
			return false;
		}

		//Value
		if(!Util.isEmpty(importBankAccount.getValue()))
		{
			newBankAccount.setValue(importBankAccount.getValue());
		}else {
			Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "Value")};
			importBankAccount.setI_ErrorMsg(Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs));
			importBankAccount.setI_IsImported(false);
			importBankAccount.setProcessed(false);
			importBankAccount.saveEx(get_TrxName());
			return false;
		}

		//Name
		if(!Util.isEmpty(importBankAccount.getName()))
		{
			newBankAccount.setName(importBankAccount.getName());
		}else {
			Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "Name")};
			importBankAccount.setI_ErrorMsg(Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs));
			importBankAccount.setI_IsImported(false);
			importBankAccount.setProcessed(false);
			importBankAccount.saveEx(get_TrxName());
			return false;
		}

		//Description
		if(!Util.isEmpty(importBankAccount.getDescription()))
			newBankAccount.setValue(importBankAccount.getDescription());

		//IsDefault
		newBankAccount.setIsDefault(importBankAccount.isDefault());

		//JP_BranchCode
		if(!Util.isEmpty(importBankAccount.getJP_BranchCode()))
		{
			String jp_BranchCode = importBankAccount.getJP_BranchCode();
			if(jp_BranchCode.length()!=ZenginCheck.JP_BranchCode)
			{
				//{0} is {1} characters.
				Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_BranchCode"),ZenginCheck.JP_BranchCode};
				importBankAccount.setI_ErrorMsg(Msg.getMsg(Env.getCtx(),"JP_Characters",objs));
				importBankAccount.setI_IsImported(false);
				importBankAccount.setProcessed(false);
				return false;
			}

			if(!ZenginCheck.numStringCheck(jp_BranchCode))
			{
				//You can not use this String : {0}.
				Object[] objs = new Object[]{jp_BranchCode};
				importBankAccount.setI_ErrorMsg(Msg.getElement(getCtx(), "JP_BranchCode") + " : " + Msg.getMsg(Env.getCtx(),"JP_CanNotUseString",objs));
				importBankAccount.setI_IsImported(false);
				importBankAccount.setProcessed(false);
				importBankAccount.saveEx(get_TrxName());
				return false;
			}

			newBankAccount.set_ValueNoCheck("JP_BranchCode", importBankAccount.getJP_BranchCode());
		}

		//JP_BranchName_Kana
		if(!Util.isEmpty(importBankAccount.getJP_BranchName_Kana()))
		{
			String jp_BranchName_Kana = importBankAccount.getJP_BranchName_Kana();
			boolean isOK = true;
			for(int i = 0; i < jp_BranchName_Kana.length(); i++)
			{
				if(!ZenginCheck.charCheck(jp_BranchName_Kana.charAt(i)))
				{
					//You can not use this character : {0}.
					Object[] objs = new Object[]{jp_BranchName_Kana.charAt(i)};
					importBankAccount.setI_ErrorMsg(Msg.getElement(getCtx(), "JP_BranchName_Kana") + " : " + Msg.getMsg(Env.getCtx(),"JP_CanNotUseChar",objs));
					importBankAccount.setI_IsImported(false);
					importBankAccount.setProcessed(false);
					importBankAccount.saveEx(get_TrxName());
					isOK = false;
					break;
				}
			}//for

			if(!isOK)
				return false;

			if(jp_BranchName_Kana.length() > ZenginCheck.JP_BranchName_Kana)
			{
				//{0} is less than {1} characters.
				Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_BankName_Kana"),ZenginCheck.JP_BranchName_Kana};
				importBankAccount.setI_ErrorMsg(Msg.getElement(getCtx(), "JP_BranchName_Kana") + " : " + Msg.getMsg(Env.getCtx(),"JP_LessThanChars",objs));
				importBankAccount.setI_IsImported(false);
				importBankAccount.setProcessed(false);
				return false;
			}

			newBankAccount.set_ValueNoCheck("JP_BranchName_Kana", importBankAccount.getJP_BranchName_Kana());
		}

		//AccountNo
		if(!Util.isEmpty(importBankAccount.getAccountNo()))
		{
			newBankAccount.setAccountNo(importBankAccount.getAccountNo());
		}else {
			importBankAccount.setI_ErrorMsg(Msg.getMsg(getCtx(), "Error")+ " Account No is Empty");
			importBankAccount.setI_IsImported(false);
			importBankAccount.setProcessed(false);
			importBankAccount.saveEx(get_TrxName());
			return false;
		}

		//JP_RequesterCode
		if(!Util.isEmpty(importBankAccount.getJP_RequesterCode()))
		{
			String jp_RequesterCode = importBankAccount.getJP_RequesterCode();
			if(jp_RequesterCode.length()!=ZenginCheck.JP_RequesterCode)
			{
				//{0} is {1} characters.
				Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_RequesterCode"),ZenginCheck.JP_RequesterCode};
				importBankAccount.setI_ErrorMsg(Msg.getMsg(Env.getCtx(),"JP_Characters",objs));
				importBankAccount.setI_IsImported(false);
				importBankAccount.setProcessed(false);
				return false;
			}

			if(!ZenginCheck.numStringCheck(jp_RequesterCode))
			{
				//You can not use this String : {0}.
				Object[] objs = new Object[]{jp_RequesterCode};
				importBankAccount.setI_ErrorMsg(Msg.getElement(getCtx(), "JP_RequesterCode") + " : " + Msg.getMsg(Env.getCtx(),"JP_CanNotUseString",objs));
				importBankAccount.setI_IsImported(false);
				importBankAccount.setProcessed(false);
				importBankAccount.saveEx(get_TrxName());
				return false;
			}

			newBankAccount.set_ValueNoCheck("JP_RequesterCode", importBankAccount.getJP_RequesterCode());
		}

		//JP_RequesterName
		if(!Util.isEmpty(importBankAccount.getJP_RequesterName()))
		{
			String jp_RequesterName = importBankAccount.getJP_RequesterName();
			boolean isOK = true;
			for(int i = 0; i < jp_RequesterName.length(); i++)
			{
				if(!ZenginCheck.charCheck(jp_RequesterName.charAt(i)))
				{
					//You can not use this character : {0}.
					Object[] objs = new Object[]{jp_RequesterName.charAt(i)};
					importBankAccount.setI_ErrorMsg(Msg.getElement(getCtx(), "JP_RequesterName") + " : " + Msg.getMsg(Env.getCtx(),"JP_CanNotUseChar",objs));
					importBankAccount.setI_IsImported(false);
					importBankAccount.setProcessed(false);
					importBankAccount.saveEx(get_TrxName());
					isOK = false;
					break;
				}
			}//for

			if(!isOK)
				return false;

			if(jp_RequesterName.length() > ZenginCheck.JP_RequesterName)
			{
				//{0} is less than {1} characters.
				Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_BankName_Kana"),ZenginCheck.JP_RequesterName};
				importBankAccount.setI_ErrorMsg(Msg.getElement(getCtx(), "JP_RequesterName") + " : " + Msg.getMsg(Env.getCtx(),"JP_LessThanChars",objs));
				importBankAccount.setI_IsImported(false);
				importBankAccount.setProcessed(false);
				return false;
			}

			newBankAccount.set_ValueNoCheck("JP_RequesterName", importBankAccount.getJP_RequesterName());
		}

		//BBAN
		if(!Util.isEmpty(importBankAccount.getBBAN()))
			newBankAccount.setBBAN(importBankAccount.getBBAN());

		//IBAN
		if(!Util.isEmpty(importBankAccount.getIBAN()))
			newBankAccount.setIBAN(importBankAccount.getIBAN());

		//C_Currency_ID
		if(importBankAccount.getC_Currency_ID() > 0) {
			newBankAccount.setC_Currency_ID(importBankAccount.getC_Currency_ID());
		}else {
			Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "C_Currency_ID")};
			importBankAccount.setI_ErrorMsg(Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs));
			importBankAccount.setI_IsImported(false);
			importBankAccount.setProcessed(false);
			importBankAccount.saveEx(get_TrxName());
			return false;
		}

		//BankAccountType
		if(!Util.isEmpty(importBankAccount.getBankAccountType()))
		{
			newBankAccount.setBankAccountType(importBankAccount.getBankAccountType());
		}else {
			Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "BankAccountType")};
			importBankAccount.setI_ErrorMsg(Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs));
			importBankAccount.setI_IsImported(false);
			importBankAccount.setProcessed(false);
			importBankAccount.saveEx(get_TrxName());
			return false;
		}

		//CreditLimit
		if(importBankAccount.getCreditLimit().compareTo(Env.ZERO)> 0)
			newBankAccount.setCreditLimit(importBankAccount.getCreditLimit());

		//CurrentBalance
		if(importBankAccount.getCurrentBalance().compareTo(Env.ZERO)> 0)
			newBankAccount.setCurrentBalance(importBankAccount.getCurrentBalance());

		//IsActive
		newBankAccount.setIsActive(importBankAccount.isI_IsActiveJP());

		ModelValidationEngine.get().fireImportValidate(this, importBankAccount, newBankAccount, ImportValidator.TIMING_AFTER_IMPORT);

		try {
			newBankAccount.saveEx(get_TrxName());
		}catch (Exception e) {
			importBankAccount.setI_ErrorMsg(Msg.getMsg(getCtx(),"SaveIgnored") + Msg.getElement(getCtx(), "C_BankAccount_ID") +" : " + e.toString());
			importBankAccount.setI_IsImported(false);
			importBankAccount.setProcessed(false);
			importBankAccount.saveEx(get_TrxName());
			return false;
		}

		importBankAccount.setC_BankAccount_ID(newBankAccount.getC_BankAccount_ID());

		if(!Util.isEmpty(importBankAccount.getJP_AcctSchema_Name()) && importBankAccount.getC_AcctSchema_ID() > 0)
			setBankAccountAcct(newBankAccount, importBankAccount);

		if(Util.isEmpty(importBankAccount.getI_ErrorMsg()))
		{
			importBankAccount.setI_ErrorMsg(Msg.getMsg(getCtx(), "NewRecord"));
		}else {
			importBankAccount.setI_ErrorMsg(Msg.getMsg(getCtx(), "NewRecord") + "  &  " +importBankAccount.getI_ErrorMsg());
		}

		importBankAccount.setI_IsImported(true);
		importBankAccount.setProcessed(true);
		importBankAccount.saveEx(get_TrxName());

		return true;
	}

	/**
	 * Update Bank Account
	 *
	 *
	 * @param importBankAccount
	 * @param newBankAccount
	 * @return
	 */
	private boolean updateBankAccount(X_I_BankAccountJP importBankAccount, MBankAccount updateBankAccount)
	{
		if(importBankAccount.getAD_Org_ID() > 0)
//			updateBankAccount.setAD_Org_ID(imp.getAD_Org_ID());

		if(!Util.isEmpty(importBankAccount.getName()))
			updateBankAccount.setName(importBankAccount.getName());

		if(!Util.isEmpty(importBankAccount.getDescription()))
			updateBankAccount.setValue(importBankAccount.getDescription());

		updateBankAccount.setIsDefault(importBankAccount.isDefault());

		if(!Util.isEmpty(importBankAccount.getJP_BranchCode()))
			updateBankAccount.set_ValueNoCheck("JP_BranchCode", importBankAccount.getJP_BranchCode());

		if(!Util.isEmpty(importBankAccount.getJP_BranchName_Kana()))
			updateBankAccount.set_ValueNoCheck("JP_BranchName_Kana", importBankAccount.getJP_BranchName_Kana());

		if(!Util.isEmpty(importBankAccount.getAccountNo()))
			updateBankAccount.setAccountNo(importBankAccount.getAccountNo());

		if(!Util.isEmpty(importBankAccount.getJP_RequesterCode()))
			updateBankAccount.set_ValueNoCheck("JP_RequesterCode", importBankAccount.getJP_RequesterCode());

		if(!Util.isEmpty(importBankAccount.getJP_RequesterName()))
			updateBankAccount.set_ValueNoCheck("JP_RequesterName", importBankAccount.getJP_RequesterName());

		if(!Util.isEmpty(importBankAccount.getBBAN()))
			updateBankAccount.setBBAN(importBankAccount.getBBAN());

		if(!Util.isEmpty(importBankAccount.getIBAN()))
			updateBankAccount.setIBAN(importBankAccount.getIBAN());

		if(importBankAccount.getC_Currency_ID() > 0)
			updateBankAccount.setC_Currency_ID(importBankAccount.getC_Currency_ID());

		if(!Util.isEmpty(importBankAccount.getBankAccountType()))
			updateBankAccount.setBankAccountType(importBankAccount.getBankAccountType());

		if(importBankAccount.getCreditLimit().compareTo(Env.ZERO)> 0)
			updateBankAccount.setCreditLimit(importBankAccount.getCreditLimit());

		if(importBankAccount.getCurrentBalance().compareTo(Env.ZERO)> 0)
			updateBankAccount.setCurrentBalance(importBankAccount.getCurrentBalance());

		updateBankAccount.setIsActive(importBankAccount.isI_IsActiveJP());

		try {
			updateBankAccount.saveEx(get_TrxName());
		}catch (Exception e) {
			importBankAccount.setI_ErrorMsg(Msg.getMsg(getCtx(),"SaveError") + Msg.getElement(getCtx(), "C_BankAccount_ID")+" :  " + e.toString());
			importBankAccount.setI_IsImported(false);
			importBankAccount.setProcessed(false);
			importBankAccount.saveEx(get_TrxName());
			return false;
		}

		if(!Util.isEmpty(importBankAccount.getJP_AcctSchema_Name()) && importBankAccount.getC_AcctSchema_ID() > 0)
			setBankAccountAcct(updateBankAccount, importBankAccount);

		if(Util.isEmpty(importBankAccount.getI_ErrorMsg()))
		{
			importBankAccount.setI_ErrorMsg(Msg.getMsg(getCtx(), "Update"));
		}else {
			importBankAccount.setI_ErrorMsg(Msg.getMsg(getCtx(), "Update") + "  &  " + importBankAccount.getI_ErrorMsg());
		}

		importBankAccount.setI_IsImported(true);
		importBankAccount.setProcessed(true);
		importBankAccount.saveEx(get_TrxName());

		return true;
	}

}	//	Import Bank Account
