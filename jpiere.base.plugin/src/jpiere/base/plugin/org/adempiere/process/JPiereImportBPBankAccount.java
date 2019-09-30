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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.logging.Level;

import org.adempiere.model.ImportValidator;
import org.adempiere.process.ImportProcess;
import org.adempiere.util.IProcessUI;
import org.compiere.model.MBPBankAccount;
import org.compiere.model.MColumn;
import org.compiere.model.MTable;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.PO;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.X_I_BP_BankAccountJP;
import jpiere.base.plugin.org.adempiere.model.X_I_CostJP;

/**
 * 	JPIERE-0415:Import Business Partner Bank Account
 *
 *  @author Hideaki Hagiwara
 *
 */
public class JPiereImportBPBankAccount extends SvrProcess implements ImportProcess
{
	/**	Client to be imported to		*/
	private int	m_AD_Client_ID = 0;

	private boolean p_deleteOldImported = false;

	/**	Only validate, don't import		*/
	private boolean p_IsValidateOnly = false;

	private String message = null;

	private IProcessUI processMonitor = null;

	private long startTime = System.currentTimeMillis();

	private String p_JP_ImportUserIdentifier = JPiereImportUser.JP_ImportUserIdentifier_Name;

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
			else if (name.equals("IsValidateOnly"))
				p_IsValidateOnly = para[i].getParameterAsBoolean();
			else if (name.equals("JP_ImportUserIdentifier"))
				p_JP_ImportUserIdentifier = para[i].getParameterAsString();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}

		m_AD_Client_ID = getProcessInfo().getAD_Client_ID();

	}	//	prepare

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
		String clientCheck = getWhereClause();

		//Delete Old Imported data
		if (p_deleteOldImported)
		{
			sql = new StringBuilder ("DELETE I_BP_BankAccountJP ")
				  .append("WHERE I_IsImported='Y'").append (clientCheck);
			try {
				no = DB.executeUpdate(sql.toString(), get_TrxName());
				if (log.isLoggable(Level.FINE)) log.fine("Delete Old Impored =" + no);
			}catch (Exception e) {
				throw new Exception(Msg.getMsg(getCtx(), "Error") + sql );
			}
		}

		//Reset Message
		sql = new StringBuilder ("UPDATE I_BP_BankAccountJP ")
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
		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "AD_Org_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupAD_Org_ID())
			commitEx();
		else
			return message;

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_BPartner_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupC_BPartner_ID())
			commitEx();
		else
			return message;

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_BPartner_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupAD_User_ID())
			commitEx();
		else
			return message;

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_Bank_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupC_Bank_ID())
			commitEx();
		else
			return message;

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_BP_BankAccount_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupC_BP_BankAccount_ID())
			commitEx();
		else
			return message;

		ModelValidationEngine.get().fireImportValidate(this, null, null, ImportValidator.TIMING_AFTER_VALIDATE);

		commitEx();

		if (p_IsValidateOnly)
		{
			return "Validated";
		}

		//
		sql = new StringBuilder ("SELECT * FROM I_BP_BankAccountJP WHERE I_IsImported='N'")
					.append(clientCheck).append(" ORDER BY C_BPartner_ID");

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
			X_I_BP_BankAccountJP impBPBankAccount = null;
			MBPBankAccount m_BankAccount = null;
			while (rs.next())
			{
				impBPBankAccount = new X_I_BP_BankAccountJP (getCtx (), rs, get_TrxName());

				boolean isNew = true;
				if(impBPBankAccount.getC_BP_BankAccount_ID() != 0){
					isNew =false;
				}

				if(isNew)//Create
				{
					//New Record
					m_BankAccount = new MBPBankAccount(getCtx (), 0, get_TrxName());
					if(createBPBankAccount(impBPBankAccount,m_BankAccount))
						successNewNum++;
					else
						failureNewNum++;

				}else{//Update

					m_BankAccount = new MBPBankAccount(getCtx (), impBPBankAccount.getC_BP_BankAccount_ID(), get_TrxName());

					if(updateBPBankAccount(impBPBankAccount,m_BankAccount))
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

		long endTime = System.currentTimeMillis();
		long time = endTime - startTime;
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        String timeFormatted = formatter.format(time);

		return  Msg.getMsg(getCtx(), "ProcessOK") + "  "  + timeFormatted + " ( " + records + recordsNum + " = "
			+ newRecord + "( "+  success + " : " + successNewNum + "  /  " +  failure + " : " + failureNewNum + " ) + "
			+ updateRecord + " ( "+  success + " : " + successUpdateNum + "  /  " +  failure + " : " + failureUpdateNum+ " ) ";

	}	//	doIt


	@Override
	public String getImportTableName() {
		return X_I_CostJP.Table_Name;
	}


	@Override
	public String getWhereClause() {
		StringBuilder msgreturn = new StringBuilder(" AND AD_Client_ID=").append(m_AD_Client_ID);
		return msgreturn.toString();
	}



	/**
	 * Reverse Look up Organization From JP_Org_Value
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupAD_Org_ID() throws Exception
	{
		int no = 0;

		//Look up AD_Org ID From JP_Org_Value
		StringBuilder sql = new StringBuilder ("UPDATE I_BP_BankAccountJP i ")
				.append("SET AD_Org_ID=(SELECT AD_Org_ID FROM AD_Org p")
				.append(" WHERE i.JP_Org_Value=p.Value AND (p.AD_Client_ID=i.AD_Client_ID or p.AD_Client_ID=0) AND p.IsSummary='N') ")
				.append(" WHERE i.JP_Org_Value IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}

		//Invalid JP_Org_Value
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "AD_Org_ID");
		sql = new StringBuilder ("UPDATE I_BP_BankAccountJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append(" WHERE AD_Org_ID IS Null ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(message + " : " + e.toString() + " : " + sql );
		}

		if(no > 0)
		{
			return false;
		}

		return true;

	}//reverseLookupAD_Org_ID

	/**
	 * Reverse look up C_BPartner_ID From Value
	 *
	 * @throws Exception
	 */
	private boolean reverseLookupC_BPartner_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_BP_BankAccountJP i ")
			.append("SET C_BPartner_ID=(SELECT C_BPartner_ID FROM C_BPartner p")
			.append(" WHERE i.JP_BPartner_Value=p.Value AND i.AD_Client_ID=p.AD_Client_ID) ")
			.append("WHERE i.C_BPartner_ID IS NULL AND i.JP_BPartner_Value IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());


		try
		{
			DB.executeUpdateEx(sql.toString(), get_TrxName());

		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Invalid JP_BPartner_Value
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_BPartner_Value");
		sql = new StringBuilder ("UPDATE I_BP_BankAccountJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append(" WHERE JP_BPartner_Value IS NOT NULL AND C_BPartner_ID IS NULL")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(message + " : " + e.toString() + " : " + sql );
		}

		if(no > 0)
		{
			return false;
		}

		//Error : Search Key is null
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "JP_Null")+Msg.getElement(getCtx(), "JP_BPartner_Value");
		sql = new StringBuilder ("UPDATE I_BP_BankAccountJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append(" WHERE I_BP_BankAccountJP IS NULL AND C_BPartner_ID IS NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(message  +" : " +  e.toString() +" : " + sql );
		}

		if(no > 0)
		{
			return false;
		}


		return true;

	}//reverseLookupC_BPartner_ID

	/**
	 * Reverse Look up User
	 * @throws Exception
	 *
	 */
	private boolean reverseLookupAD_User_ID() throws Exception
	{
		if(Util.isEmpty(p_JP_ImportUserIdentifier) || p_JP_ImportUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_NotCollate))
			return true;

		StringBuilder sql = null;

		if(p_JP_ImportUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_EMail)) //E-Mail
		{
			sql = new StringBuilder ("UPDATE I_BP_BankAccountJP i ")
					.append("SET AD_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_User_EMail=p.EMail AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.C_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.EMail IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_Name)) { //Name

			sql = new StringBuilder ("UPDATE I_BP_BankAccountJP i ")
					.append("SET AD_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_User_Name=p.Name AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.C_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_User_Name IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_Value)) { //Value

			sql = new StringBuilder ("UPDATE I_BP_BankAccountJP i ")
					.append("SET AD_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_User_Value=p.Value AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.C_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_User_Value IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueEMail)) { //Value + E-Mail

			//In case of EMail is not null
			sql = new StringBuilder ("UPDATE I_BP_BankAccountJP i ")
					.append("SET AD_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_User_Value=p.Value AND i.JP_User_EMail=p.EMail AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.C_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_User_Value IS NOT NULL AND i.JP_User_EMail IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

			try {
				DB.executeUpdateEx(sql.toString(), get_TrxName());
			}catch(Exception e) {
				message = message + " : " +e.toString()+ " : "+sql.toString();
				return false;
			}

			//In case of EMail is null
			sql = new StringBuilder ("UPDATE I_BP_BankAccountJP i ")
					.append("SET AD_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_User_Value=p.Value AND i.JP_User_EMail IS NULL AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.C_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_User_Value IS NOT NULL AND i.JP_User_EMail IS NULL AND i.AD_User_ID IS NOT NULL ")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueName)) { //Value + Name

			sql = new StringBuilder ("UPDATE I_BP_BankAccountJP i ")
					.append("SET AD_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_User_Value=p.Value AND i.JP_User_Name=p.Name AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.C_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_User_Value IS NOT NULL AND i.JP_User_Name IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueNameEmail)) { //Value + Name + EMail

			//In case of EMail is not null
			sql = new StringBuilder ("UPDATE I_BP_BankAccountJP i ")
					.append("SET AD_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_User_Value=p.Value AND i.JP_User_Name=p.Name  AND i.JP_User_EMail=p.EMail AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 )")
					.append(" AND i.C_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_User_Value IS NOT NULL AND i.JP_User_Name IS NOT NULL AND i.JP_User_EMail IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

			try {
				DB.executeUpdateEx(sql.toString(), get_TrxName());
			}catch(Exception e) {
				message = message + " : " +e.toString()+ " : "+sql.toString();
				return false;
			}

			//In case of EMail is null
			sql = new StringBuilder ("UPDATE I_BP_BankAccountJP i ")
					.append("SET AD_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_User_Value=p.Value AND i.JP_User_Name=p.Name  AND i.JP_User_EMail IS NULL AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 )")
					.append(" AND i.C_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_User_Value IS NOT NULL AND i.JP_User_Name IS NOT NULL AND i.JP_User_EMail IS NULL AND i.AD_User_ID IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_NotCollate)){

			return true;

		}else {

			return true;

		}

		try {
			DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {

			message = message + " : " +e.toString()+ " : "+sql.toString();
			return false;

		}

		return true;

	}//reverseLookupAD_User_ID

	/**
	 * Reverse Look up Bank From JP_Bank_Namee
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupC_Bank_ID() throws Exception
	{
		int no = 0;

		//Look up C_Bank ID From JP_Bank_Name
		StringBuilder sql = new StringBuilder ("UPDATE I_BP_BankAccountJP i ")
				.append("SET C_Bank_ID=(SELECT C_Bank_ID FROM C_Bank p")
				.append(" WHERE i.JP_Bank_Name=p.Name AND (p.AD_Client_ID=i.AD_Client_ID or p.AD_Client_ID=0) ) ")
				.append(" WHERE i.JP_Bank_Name IS NOT NULL AND C_Bank_ID IS NULL ")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}

		//Invalid JP_Bank_Name
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_Bank_Name");
		sql = new StringBuilder ("UPDATE I_BP_BankAccountJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append(" WHERE JP_Bank_Name IS Not Null AND C_Bank_ID IS NULL")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(message + " : " + e.toString() + " : " + sql );
		}

		if(no > 0)
		{
			return false;
		}

		return true;

	}//reverseLookupC_Bank_ID


	/**
	 * Reverse Look up Bank Account From C_BP_BankAccount_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupC_BP_BankAccount_ID() throws Exception
	{
		int no = 0;

		//Look up C_BP_BankAccount_ID From Bank Info
		StringBuilder sql = new StringBuilder ("UPDATE I_BP_BankAccountJP i ")
				.append("SET C_BP_BankAccount_ID=(SELECT C_BP_BankAccount_ID FROM C_BP_BankAccount p")
				.append(" WHERE i.C_Bank_ID=p.C_Bank_ID AND i.JP_BranchCode=p.JP_BranchCode AND i.AccountNo=p.AccountNo AND p.C_BPartner_ID=i.C_BPartner_ID ) ")
				.append(" WHERE i.C_Bank_ID IS NOT NULL AND i.JP_BranchCode IS NOT NULL AND i.AccountNo IS NOT NULL AND C_BP_BankAccount_ID IS NULL ")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("Look up C_BP_BankAccount_ID From Bank Info -> #" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}

		//Look up C_BP_BankAccount_ID From Card Info
		sql = new StringBuilder ("UPDATE I_BP_BankAccountJP i ")
				.append("SET C_BP_BankAccount_ID=(SELECT C_BP_BankAccount_ID FROM C_BP_BankAccount p")
				.append(" WHERE i.CreditCardType=p.CreditCardType AND i.CreditCardNumber=p.CreditCardNumber AND p.AD_Client_ID=i.AD_Client_ID ) ")
				.append(" WHERE i.CreditCardType IS NOT NULL AND i.CreditCardNumber IS NOT NULL AND C_BP_BankAccount_ID IS NULL ")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}

		return true;

	}//reverseLookupC_BP_BankAccount_ID

	/**
	 * create BP Bank Account
	 *
	 * @param importBPBankAccount
	 * @param newBPBankAccount
	 * @return
	 */
	private boolean createBPBankAccount(X_I_BP_BankAccountJP importBPBankAccount, MBPBankAccount newBPBankAccount)
	{
		ModelValidationEngine.get().fireImportValidate(this, importBPBankAccount, newBPBankAccount, ImportValidator.TIMING_BEFORE_IMPORT);

		PO.copyValues(importBPBankAccount, newBPBankAccount);

		ModelValidationEngine.get().fireImportValidate(this, importBPBankAccount, newBPBankAccount, ImportValidator.TIMING_AFTER_IMPORT);

		try {
			newBPBankAccount.saveEx(get_TrxName());
		}catch (Exception e) {
			importBPBankAccount.setI_ErrorMsg(Msg.getMsg(getCtx(),"SaveIgnored") + Msg.getElement(getCtx(), "C_BP_BankAccount_ID") +" : " + e.toString());
			importBPBankAccount.setI_IsImported(false);
			importBPBankAccount.setProcessed(false);
			importBPBankAccount.saveEx(get_TrxName());
			return false;
		}

		importBPBankAccount.setC_BP_BankAccount_ID(newBPBankAccount.getC_BP_BankAccount_ID());

		StringBuilder msg = new StringBuilder(Msg.getMsg(getCtx(), "NewRecord"));
		importBPBankAccount.setI_ErrorMsg(msg.toString());
		importBPBankAccount.setI_IsImported(true);
		importBPBankAccount.setProcessed(true);
		importBPBankAccount.saveEx(get_TrxName());

		return true;
	}

	/**
	 * update BP Bank Account
	 *
	 * @param importBPBankAccount
	 * @param updateBPBankAccount
	 * @return
	 */
	private boolean updateBPBankAccount(X_I_BP_BankAccountJP importBPBankAccount, MBPBankAccount updateBPBankAccount)
	{
		ModelValidationEngine.get().fireImportValidate(this, importBPBankAccount, updateBPBankAccount, ImportValidator.TIMING_BEFORE_IMPORT);

		//Update Business Partner
		MTable C_BP_BankAccount_Table = MTable.get(getCtx(), MBPBankAccount.Table_ID, get_TrxName());
		MColumn[] C_BP_BankAccount_Columns = C_BP_BankAccount_Table.getColumns(true);

		MTable I_BP_BankAccountJP_Table = MTable.get(getCtx(), X_I_BP_BankAccountJP.Table_ID, get_TrxName());
		MColumn[] I_BP_BankAccountJP_Columns = I_BP_BankAccountJP_Table.getColumns(true);

		MColumn i_Column = null;
		for(int i = 0 ; i < C_BP_BankAccount_Columns.length; i++)
		{
			i_Column = C_BP_BankAccount_Columns[i];
			if(i_Column.isVirtualColumn() || i_Column.isKey() || i_Column.isUUIDColumn())
				continue;//i

			if(i_Column.getColumnName().equals("IsActive")
				|| i_Column.getColumnName().equals("AD_Client_ID")
				|| i_Column.getColumnName().equals("Value")
				|| i_Column.getColumnName().equals("Processing")
				|| i_Column.getColumnName().equals("Created")
				|| i_Column.getColumnName().equals("CreatedBy")
				|| i_Column.getColumnName().equals("Updated")
				|| i_Column.getColumnName().equals("UpdatedBy") )
				continue;//i

			MColumn j_Column = null;
			Object importValue = null;
			for(int j = 0 ; j < I_BP_BankAccountJP_Columns.length; j++)
			{
				j_Column = I_BP_BankAccountJP_Columns[j];

				if(i_Column.getColumnName().equals(j_Column.getColumnName()))
				{
					importValue = importBPBankAccount.get_Value(j_Column.getColumnName());

					if(importValue == null )
					{
						break;//j

					}else if(importValue instanceof BigDecimal) {

						BigDecimal bigDecimal_Value = (BigDecimal)importValue;
						if(bigDecimal_Value.compareTo(Env.ZERO) == 0)
							break;

					}else if(j_Column.getAD_Reference_ID()==DisplayType.String) {

						String string_Value = (String)importValue;
						if(!Util.isEmpty(string_Value))
						{
							updateBPBankAccount.set_ValueNoCheck(i_Column.getColumnName(), importValue);
						}

						break;

					}else if(j_Column.getColumnName().endsWith("_ID")) {

						Integer p_key = (Integer)importValue;
						if(p_key.intValue() <= 0)
							break;

					}

					if(importValue != null)
					{

						try {
							updateBPBankAccount.set_ValueNoCheck(i_Column.getColumnName(), importValue);
						}catch (Exception e) {

							importBPBankAccount.setI_ErrorMsg(Msg.getMsg(getCtx(), "Error") + " Column = " + i_Column.getColumnName() + " & " + "Value = " +importValue.toString() + " -> " + e.toString());
							importBPBankAccount.setI_IsImported(false);
							importBPBankAccount.setProcessed(false);
							importBPBankAccount.saveEx(get_TrxName());
							return false;
						}

					}

					break;
				}
			}//for j

		}//for i

		ModelValidationEngine.get().fireImportValidate(this, importBPBankAccount, updateBPBankAccount, ImportValidator.TIMING_AFTER_IMPORT);

		try {
			updateBPBankAccount.saveEx(get_TrxName());
		}catch (Exception e) {
			importBPBankAccount.setI_ErrorMsg(Msg.getMsg(getCtx(),"SaveError") + Msg.getElement(getCtx(), "C_BP_BankAccount_ID") + " -> " +e.toString());
			importBPBankAccount.setI_IsImported(false);
			importBPBankAccount.setProcessed(false);
			importBPBankAccount.saveEx(get_TrxName());
			return false;
		}

		StringBuilder msg = new StringBuilder(Msg.getMsg(getCtx(), "Update"));
		importBPBankAccount.setI_ErrorMsg(msg.toString());
		importBPBankAccount.setI_IsImported(true);
		importBPBankAccount.setProcessed(true);
		importBPBankAccount.saveEx(get_TrxName());

		return true;
	}

}	//	Import Business Partner Bank Account
