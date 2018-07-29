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
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;

import org.adempiere.model.ImportValidator;
import org.adempiere.process.ImportProcess;
import org.adempiere.util.IProcessUI;
import org.compiere.model.MColumn;
import org.compiere.model.MSysConfig;
import org.compiere.model.MTable;
import org.compiere.model.MUser;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.PO;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.X_I_UserJP;

/**
 *	JPIERE-0400:Import User from I_UserJP
 *
 *
 *  @author Hideaki Hagiwara
 */
public class JPiereImportUser extends SvrProcess implements ImportProcess
{
	/**	Client to be imported to		*/
	private int				m_AD_Client_ID = 0;
	/**	Delete old Imported				*/
	private boolean			m_deleteOldImported = false;

	/**	Only validate, don't import		*/
	private boolean			p_IsValidateOnly = false;

	private String p_JP_ImportUserIdentifier = null;
	public static final String JP_ImportUserIdentifier_EMail = "EM";
	public static final String JP_ImportUserIdentifier_Name = "NA";
	public static final String JP_ImportUserIdentifier_Value = "VA";
	public static final String JP_ImportUserIdentifier_ValueEMail = "VE";
	public static final String JP_ImportUserIdentifier_ValueName = "VN";
	public static final String JP_ImportUserIdentifier_ValueNameEmail = "VZ";
	public static final String JP_ImportUserIdentifier_NotCollate= "ZZ";


	private String message = null;
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
			if (name.equals("AD_Client_ID"))
				m_AD_Client_ID = ((BigDecimal)para[i].getParameter()).intValue();
			else if (name.equals("DeleteOldImported"))
				m_deleteOldImported = "Y".equals(para[i].getParameter());
			else if (name.equals("IsValidateOnly"))
				p_IsValidateOnly = para[i].getParameterAsBoolean();
			else if (name.equals("JP_ImportUserIdentifier"))
				p_JP_ImportUserIdentifier = para[i].getParameterAsString();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}

		m_AD_Client_ID =Env.getAD_Client_ID(getCtx());
	}	//	prepare

	/**
	 *  Perform process.
	 *  @return Message
	 *  @throws Exception
	 */
	protected String doIt() throws java.lang.Exception
	{
		processMonitor = Env.getProcessUI(getCtx());

		StringBuilder sql = null;
		int no = 0;
		String clientCheck = getWhereClause();

		/** Delete Old Imported */
		if (m_deleteOldImported)
		{
			sql = new StringBuilder ("DELETE I_UserJP ")
				.append("WHERE I_IsImported='Y'").append(clientCheck);
			try {
				no = DB.executeUpdate(sql.toString(), get_TrxName());
				if (log.isLoggable(Level.INFO)) log.info("Delete Old Imported =" + no);
			}catch(Exception e) {
				throw new Exception(Msg.getMsg(getCtx(), "Error") + sql );
			}
		}


		/** Reset I_ErrorMsg */
		sql = new StringBuilder ("UPDATE I_UserJP ")
				.append("SET I_ErrorMsg='' ")
				.append(" WHERE I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(String.valueOf(no));
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + sql );
		}

		ModelValidationEngine.get().fireImportValidate(this, null, null, ImportValidator.TIMING_BEFORE_VALIDATE);


		/** Reverse Lookup Surrogate Key */
		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "AD_User_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupAD_User_ID())
			commitEx();
		else
			return message;


		 message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "AD_Org_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupAD_Org_ID())
			commitEx();
		else
			return message;

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "AD_OrgTrx_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupAD_OrgTrx_ID())
			commitEx();
		else
			return message;

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "JP_Corporation_ID") ;
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupJP_Corporation_ID())
			commitEx();
		else
			return message;

		 message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_BPartner_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupC_BPartner_ID())
			commitEx();
		else
			return message;

		 message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_BPartner_Location_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupC_BPartner_Location_ID())
			commitEx();
		else
			return message;

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_Job_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupC_Job_ID())
			commitEx();
		else
			return message;

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "Supervisor_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupSupervisor_ID())
			commitEx();
		else
			return message;

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "R_DefaultMailText_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupR_DefaultMailText_ID())
			commitEx();
		else
			return message;

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_Location_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupC_Location_ID())
			commitEx();
		else
			return message;

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "SalesRep_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupSalesRep_ID())
			commitEx();
		else
			return message;

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_Greeting_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupC_Greeting_ID())
			commitEx();
		else
			return message;


		ModelValidationEngine.get().fireImportValidate(this, null, null, ImportValidator.TIMING_AFTER_VALIDATE);

		commitEx();
		if (p_IsValidateOnly)
		{
			return "Validated";
		}


		/** Register & Update User */
		message = Msg.getMsg(getCtx(), "Register") +" & "+ Msg.getMsg(getCtx(), "Update")  + " " + Msg.getElement(getCtx(), "AD_User_ID");
		if (processMonitor != null)	processMonitor.statusUpdate(message);

		sql = new StringBuilder ("SELECT * FROM I_UserJP WHERE I_IsImported='N' ").append(getWhereClause());
		if(Util.isEmpty(p_JP_ImportUserIdentifier)) {
			sql.append(clientCheck).append(" ORDER BY Value, Name, EMail ");
		}else if(p_JP_ImportUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_EMail)) {
			sql.append(clientCheck).append(" ORDER BY EMail ");
		}else if(p_JP_ImportUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_Name)) {
			sql.append(clientCheck).append(" ORDER BY Name ");
		}else if(p_JP_ImportUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_Value)) {
			sql.append(clientCheck).append(" ORDER BY Value ");
		}else if(p_JP_ImportUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueEMail)) {
			sql.append(clientCheck).append(" ORDER BY Value, EMail ");
		}else if(p_JP_ImportUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueName)) {
			sql.append(clientCheck).append(" ORDER BY Value, Name ");
		}else if(p_JP_ImportUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueNameEmail)) {
			sql.append(clientCheck).append(" ORDER BY Value, Name, EMail ");
		}else if(p_JP_ImportUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_NotCollate)) {
			sql.append(clientCheck).append(" ORDER BY Value, Name, EMail ");
		}else {
			sql.append(clientCheck).append(" ORDER BY Value, Name, EMail ");
		}

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
			String preValue = "";
			String preName = "";
			String preEMail = "";
			MUser user = null;

			while (rs.next())
			{
				X_I_UserJP imp = new X_I_UserJP (getCtx (), rs, get_TrxName());

				boolean isNew = true;
				if(imp.getAD_User_ID() != 0)
				{
					isNew =false;
					user = new MUser(getCtx(), imp.getAD_User_ID(), get_TrxName());

				}else{

					if(Util.isEmpty(p_JP_ImportUserIdentifier)) {

						isNew = true;

					}else if(p_JP_ImportUserIdentifier.equals(JP_ImportUserIdentifier_EMail)) {

						if(preEMail.equals(imp.getEMail()))
							isNew = false;

					}else if(p_JP_ImportUserIdentifier.equals(JP_ImportUserIdentifier_Name)) {

						if(preName.equals(imp.getName()))
							isNew = false;

					}else if(p_JP_ImportUserIdentifier.equals(JP_ImportUserIdentifier_Value)) {

						if(preValue.equals(imp.getValue()))
							isNew = false;

					}else if(p_JP_ImportUserIdentifier.equals(JP_ImportUserIdentifier_ValueEMail)) {

						if(preValue.equals(imp.getValue()) && preEMail.equals(imp.getEMail()))
							isNew = false;

					}else if(p_JP_ImportUserIdentifier.equals(JP_ImportUserIdentifier_ValueName)) {

						if(preValue.equals(imp.getValue()) && preName.equals(imp.getName()))
							isNew = false;

					}else if(p_JP_ImportUserIdentifier.equals(JP_ImportUserIdentifier_ValueNameEmail)) {

						if(preValue.equals(imp.getValue()) && preName.equals(imp.getName()) && preEMail.equals(imp.getEMail()))
							isNew = false;

					}else if(p_JP_ImportUserIdentifier.equals(JP_ImportUserIdentifier_NotCollate)) {

						isNew = true;

					}else {

						isNew = true;
					}

				}

				preValue = imp.getValue();
				preName = imp.getName();
				preEMail = imp.getEMail();

				if(isNew)
				{
					user = new MUser(getCtx(), 0, get_TrxName());
					if(createNewUser(imp, user))
						successNewNum++;
					else
						failureNewNum++;

				}else{

					if(updateUser(imp, user))
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

			}//while

		}catch (Exception e) {

			log.log(Level.SEVERE, e.toString(), e);
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


	@Override
	public String getImportTableName() {
		return X_I_UserJP.Table_Name;
	}


	@Override
	public String getWhereClause() {
		StringBuilder msgreturn = new StringBuilder(" AND AD_Client_ID=").append(m_AD_Client_ID);
		return msgreturn.toString();
	}


	/**
	 * Reverse Look up User From E-Mail and (Value && Name)
	 * @throws Exception
	 *
	 */
	private boolean reverseLookupAD_User_ID() throws Exception
	{
		if(Util.isEmpty(p_JP_ImportUserIdentifier) || p_JP_ImportUserIdentifier.equals(JP_ImportUserIdentifier_NotCollate))
			return true;

		//Check user Duplication!!
		StringBuilder checkUserDuplicationSql = new StringBuilder ("SELECT * FROM I_UserJP WHERE I_IsImported='N' AND AD_Client_ID=? ");
		PreparedStatement checkUserDuplicationPSTMT = null;
		ResultSet checkUserDuplicationRS = null;
		boolean isUserDuplication = false;
		message = Msg.getMsg(getCtx(), "JP_Checking") + Msg.getElement(getCtx(), "JP_ImportUserIdentifier");
		String JP_Checked = Msg.getMsg(getCtx(), "JP_Checked");
		String Error = Msg.getMsg(getCtx(), "Error");
		int i = 0;
		int j = 0;
		try
		{
			checkUserDuplicationPSTMT = DB.prepareStatement(checkUserDuplicationSql.toString(), get_TrxName());
			checkUserDuplicationPSTMT.setInt(1, getAD_Client_ID());
			checkUserDuplicationRS = checkUserDuplicationPSTMT.executeQuery();
			while (checkUserDuplicationRS.next())
			{
				i++;
				if(processMonitor != null)	processMonitor.statusUpdate(message +"("+ Error + j + " / " + JP_Checked + i + ")");

				X_I_UserJP imp = new X_I_UserJP (getCtx (), checkUserDuplicationRS, get_TrxName());
				if(isUserDuplication(getCtx(), imp.getValue(), imp.getName(), imp.getEMail(), p_JP_ImportUserIdentifier, get_TrxName()))
				{
					j++;
					imp.setI_ErrorMsg(Error + JP_Checked + Msg.getElement(getCtx(), "JP_ImportUserIdentifier"));
					isUserDuplication = true;
					imp.saveEx(get_TrxName());
					commitEx();
				}
			}

		}catch (Exception e) {
			message = message + e.toString();
			throw e;
		}finally {
			DB.close(checkUserDuplicationRS, checkUserDuplicationPSTMT);
			checkUserDuplicationRS = null;
			checkUserDuplicationPSTMT = null;
		}

		if(isUserDuplication)
		{
			message = Msg.getMsg(getCtx(), "JP_Checked") + Msg.getElement(getCtx(), "JP_ImportUserIdentifier")
																+"("+ Error + j + " / " + JP_Checked + i + ")";
			return false;
		}


		StringBuilder sql = null;
		int no = 0;

		if(p_JP_ImportUserIdentifier.equals(JP_ImportUserIdentifier_EMail))//E-Mail
		{
			sql = new StringBuilder ("UPDATE I_UserJP i ")
				.append("SET AD_User_ID=(SELECT AD_User_ID FROM AD_User p")
				.append(" WHERE i.EMail=p.EMail AND  ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ) ")
				.append(" WHERE i.EMail IS NOT NULL AND i.AD_User_ID IS NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportUserIdentifier.equals(JP_ImportUserIdentifier_Name)) { //Name

			sql = new StringBuilder ("UPDATE I_UserJP i ")
					.append("SET AD_User_ID=(SELECT AD_User_ID FROM AD_User p")
					.append(" WHERE i.Name=p.Name AND  ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ) ")
					.append(" WHERE i.Name IS NOT NULL AND i.AD_User_ID IS NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportUserIdentifier.equals(JP_ImportUserIdentifier_Value)) { //Value

			sql = new StringBuilder ("UPDATE I_UserJP i ")
					.append("SET AD_User_ID=(SELECT AD_User_ID FROM AD_User p")
					.append(" WHERE i.Value=p.Value AND  ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ) ")
					.append(" WHERE i.AD_User_ID IS NULL AND i.Value IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportUserIdentifier.equals(JP_ImportUserIdentifier_ValueEMail)) { //Value + E-Mail

			sql = new StringBuilder ("UPDATE I_UserJP i ")
					.append("SET AD_User_ID=(SELECT AD_User_ID FROM AD_User p")
					.append(" WHERE i.Value=p.Value AND i.EMail=p.EMail AND  ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ) ")
					.append(" WHERE i.AD_User_ID IS NULL ")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportUserIdentifier.equals(JP_ImportUserIdentifier_ValueName)) { //Value + Name

			sql = new StringBuilder ("UPDATE I_UserJP i ")
					.append("SET AD_User_ID=(SELECT AD_User_ID FROM AD_User p")
					.append(" WHERE i.Value=p.Value AND i.Name=p.Name AND  ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ) ")
					.append(" WHERE i.AD_User_ID IS NULL ")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportUserIdentifier.equals(JP_ImportUserIdentifier_ValueNameEmail)) { //Value + Name + EMail

			sql = new StringBuilder ("UPDATE I_UserJP i ")
					.append("SET AD_User_ID=(SELECT AD_User_ID FROM AD_User p")
					.append(" WHERE i.Value=p.Value AND i.Name=p.Name AND i.EMail=p.EMail AND  ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ) ")
					.append(" WHERE i.AD_User_ID IS NULL ")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportUserIdentifier.equals(JP_ImportUserIdentifier_NotCollate)) {

			return true;
		}else {


			return true;

		}


		try {

			no = DB.executeUpdateEx(sql.toString(), get_TrxName());

		}catch(Exception e) {

			message = message + e.toString();
			return false;
		}

		return true;

	}//reverseLookupAD_User_ID

	/**
	 *
	 * Check User Duplication
	 *
	 * @param ctx
	 * @param Value
	 * @param Name
	 * @param EMail
	 * @param JP_ImportUserIdentifier
	 * @param trxName
	 * @return
	 * @throws Exception
	 */
	static public boolean isUserDuplication(Properties ctx, String Value,String Name, String EMail, String JP_ImportUserIdentifier, String trxName) throws Exception
	{
		StringBuffer sql = new StringBuffer("SELECT count(*) From AD_User WHERE (AD_Client_ID = 0 or AD_Client_ID =?) AND ");

		if(JP_ImportUserIdentifier.equals(JP_ImportUserIdentifier_EMail))
		{
			if(EMail == null)
				sql.append("EMail IS NULL");
			else
				sql.append("EMail=?");

		}else if(JP_ImportUserIdentifier.equals(JP_ImportUserIdentifier_Name)) {

			if(Name == null)
				sql.append("Name IS NULL");
			else
				sql.append("Name=?");

		}else if(JP_ImportUserIdentifier.equals(JP_ImportUserIdentifier_Value)) {

			if(Value == null)
				sql.append("Value IS NULL");
			else
				sql.append("Value=?");

		}else if(JP_ImportUserIdentifier.equals(JP_ImportUserIdentifier_ValueEMail)) {

			if(Value == null && EMail == null)
				sql.append("Value IS NULL AND EMail IS NULL");
			else if (Value != null && EMail == null)
				sql.append("Value=? AND EMail IS NULL");
			else if (Value == null && EMail != null)
				sql.append("Value IS NULL AND EMail=?");
			else
				sql.append("Value=? AND EMail=?");

		}else if(JP_ImportUserIdentifier.equals(JP_ImportUserIdentifier_ValueName)) {

			if(Value == null && Name == null)
				sql.append("Value IS NULL AND Name IS NULL");
			else if (Value != null && Name == null)
				sql.append("Value=? AND Name IS NULL");
			else if (Value == null && Name != null)
				sql.append("Value IS NULL AND Name=?");
			else
				sql.append("Value=? AND Name=?");

		}else if(JP_ImportUserIdentifier.equals(JP_ImportUserIdentifier_ValueNameEmail)) {

			if(Value == null && Name == null && EMail == null)
				sql.append("Value IS NULL AND Name IS NULL AND  EMail IS NULL");
			else if (Value == null && Name == null  && EMail != null)
				sql.append("Value IS NULL AND Name IS NULL AND EMail=?");
			else if(Value == null && Name != null && EMail == null)
				sql.append("Value IS NULL AND Name = ? AND  EMail IS NULL");
			else if (Value == null && Name != null  && EMail != null)
				sql.append("Value IS NULL AND Name=? AND EMail=?");
			else if(Value != null && Name == null && EMail == null)
				sql.append("Value=? AND Name IS NULL AND  EMail IS NULL");
			else if (Value != null && Name == null  && EMail != null)
				sql.append("Value=? AND Name IS NULL AND EMail=?");
			else if(Value != null && Name != null && EMail == null)
				sql.append("Value=? AND Name = ? AND  EMail IS NULL");
			else if (Value != null && Name != null  && EMail != null)
				sql.append("Value=? AND Name=? AND EMail=?");
			else
				sql.append("Value=? AND Name=? AND EMail=?");

		}else if(JP_ImportUserIdentifier.equals(JP_ImportUserIdentifier_NotCollate)) {
			return false;
		}else {
			return false;
		}

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try
		{
			pstmt = DB.prepareStatement(sql.toString(), trxName);
			pstmt.setInt(1, Env.getAD_Client_ID(ctx));
			if(JP_ImportUserIdentifier.equals(JP_ImportUserIdentifier_EMail))
			{
				if(EMail==null)
					;//Noting
				else
					pstmt.setString(2, EMail);

			}else if(JP_ImportUserIdentifier.equals(JP_ImportUserIdentifier_Name)) {

				if(Name == null)
					;//Noting
				else
					pstmt.setString(2, Name);

			}else if(JP_ImportUserIdentifier.equals(JP_ImportUserIdentifier_Value)) {

				if(Value == null)
					;//Noting
				else
					pstmt.setString(2, Value);

			}else if(JP_ImportUserIdentifier.equals(JP_ImportUserIdentifier_ValueEMail)) {

				if(Value == null && EMail == null) {
					;//Noting
				}else if (Value != null && EMail == null) {
					pstmt.setString(2, Value);
				}else if (Value == null && EMail != null) {
					pstmt.setString(2, EMail);
				}else {
					pstmt.setString(2, Value);
					pstmt.setString(3, EMail);
				}

			}else if(JP_ImportUserIdentifier.equals(JP_ImportUserIdentifier_ValueName)) {

				if(Value == null && Name == null) {
					;//Noting
				}else if (Value != null && Name == null) {
					pstmt.setString(2, Value);
				}else if (Value == null && Name != null) {
					pstmt.setString(2, Name);
				}else {
					pstmt.setString(2, Value);
					pstmt.setString(3, Name);
				}
			}else if(JP_ImportUserIdentifier.equals(JP_ImportUserIdentifier_ValueNameEmail)) {

				if(Value == null && Name == null && EMail == null) {
					;//Noting to do;
				}else if (Value == null && Name == null  && EMail != null) {
					pstmt.setString(2, EMail);
				}else if(Value == null && Name != null && EMail == null) {
					pstmt.setString(2, Name);
				}else if (Value == null && Name != null  && EMail != null) {
					pstmt.setString(2, Name);
					pstmt.setString(3, EMail);
				}else if(Value != null && Name == null && EMail == null) {
					pstmt.setString(2, Value);
				}else if (Value != null && Name == null  && EMail != null) {
					pstmt.setString(2, Value);
					pstmt.setString(3, EMail);
				}else if(Value != null && Name != null && EMail == null) {
					pstmt.setString(2, Value);
					pstmt.setString(3, Name);

				}else if (Value != null && Name != null  && EMail != null) {
					pstmt.setString(2, Value);
					pstmt.setString(3, Name);
					pstmt.setString(4, EMail);
				}else {
					pstmt.setString(2, Value);
					pstmt.setString(3, Name);
					pstmt.setString(4, EMail);
				}
			}else if(JP_ImportUserIdentifier.equals(JP_ImportUserIdentifier_NotCollate)) {
				return false;
			}else {
				return false;
			}

			rs = pstmt.executeQuery();
			int no = 0;
			if (rs.next())
			{
				no = rs.getInt(1);
				if(no > 1)
					return true;
			}

		}catch (Exception e) {
			throw e;
		}finally {
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

		return false;
	}

	/**
	 * Reverse look up JP_Corporation_ID From JP_Corporation_Value
	 *
	 * @throws Exception
	 */
	private boolean reverseLookupJP_Corporation_ID() throws Exception
	{
		int no = 0;

		//Reverse lookup JP_Corporation_ID From JP_Corporation_Value
		StringBuilder  sql = new StringBuilder ("UPDATE I_UserJP i ")
			.append("SET JP_Corporation_ID=(SELECT JP_Corporation_ID FROM JP_Corporation p")
			.append(" WHERE i.JP_Corporation_Value=p.Value AND i.AD_Client_ID=p.AD_Client_ID) ")
			.append("WHERE i.JP_Corporation_ID IS NULL AND i.JP_Corporation_Value IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message + " : " + e.toString() + " : " + sql );
		}

		//Invalid JP_Corporation_Value
		message = Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "JP_Corporation_Value");
		sql = new StringBuilder ("UPDATE I_UserJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append("WHERE JP_Corporation_ID IS NULL AND JP_Corporation_Value IS NOT NULL")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message + " : " + e.toString() + " : " + sql );
		}

		//Return all ture, because JP_Corporation_ID may not be registered when migration data.
		return true;

	}//reverseLookupC_BPartner_ID


	/**
	 * Reverse look up C_BPartner_ID From JP_BPartner_Value
	 *
	 * @throws Exception
	 */
	private boolean reverseLookupC_BPartner_ID() throws Exception
	{
		int no = 0;

		//Reverse lookup C_BPartner_ID From JP_BPartner_Value
		StringBuilder  sql = new StringBuilder ("UPDATE I_UserJP i ")
			.append("SET C_BPartner_ID=(SELECT C_BPartner_ID FROM C_BPartner p")
			.append(" WHERE i.JP_BPartner_Value=p.Value AND i.AD_Client_ID=p.AD_Client_ID) ")
			.append("WHERE i.C_BPartner_ID IS NULL AND i.JP_BPartner_Value IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}

		//Invalid BPartner_Value
		message = Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_BPartner_Value");
		sql = new StringBuilder ("UPDATE I_UserJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append("WHERE C_BPartner_ID IS NULL AND JP_BPartner_Value IS NOT NULL")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message + " : "+ e.toString() + " : " + sql );
		}

		//Return all ture, because C_BPartner_ID may not be registered when migration data.
		return true;

	}//reverseLookupC_BPartner_ID

	/**
	 * Reverse look up C_BPartner_Location_ID From JP_BPartner_Location_Name
	 *
	 * @throws Exception
	 */
	private boolean reverseLookupC_BPartner_Location_ID() throws Exception
	{
		int no = 0;

		//Reverse lookup C_BPartner_Location_ID From JP_BPartner_Location_Name
		StringBuilder sql = new StringBuilder ("UPDATE I_UserJP i ")
			.append("SET C_BPartner_Location_ID=(SELECT C_BPartner_Location_ID FROM C_BPartner_Location p")
			.append(" WHERE i.JP_BPartner_Location_Name=p.Name AND i.C_Bpartner_ID =p.C_BPartner_ID) ")
			.append(" WHERE i.C_BPartner_Location_ID IS NULL AND i.JP_BPartner_Location_Name IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}

		//Invalid JP_BPartner_Location_Name
		message = Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_BPartner_Location_Name");
		sql = new StringBuilder ("UPDATE I_UserJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append("WHERE C_BPartner_Location_ID IS NULL AND JP_BPartner_Location_Name IS NOT NULL AND C_BPartner_ID IS NOT NULL")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message + " : " + e.toString() + " : " + sql );
		}

		//Return all ture, because C_BPartner_Location_ID may not be registered when migration data.
		return true;

	}//reverseLookuppC_BPartner_Location_ID


	/**
	 * Reverse Look up Organization From JP_Org_Value
	 *
	 **/
	private boolean reverseLookupAD_Org_ID() throws Exception
	{
		int no = 0;

		//Look up AD_Org ID From JP_Org_Value
		StringBuilder sql = new StringBuilder ("UPDATE I_UserJP i ")
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
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_Org_Value");
		sql = new StringBuilder ("UPDATE I_UserJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append(" WHERE AD_Org_ID = 0 AND JP_Org_Value IS NOT NULL AND JP_Org_Value <> '0' ")
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
	 * Reverse Look up Trx Organization From JP_OrgTrx_Value
	 *
	 **/
	private boolean reverseLookupAD_OrgTrx_ID() throws Exception
	{
		int no = 0;

		//Look up AD_OrgTrx ID From JP_OrgTrx_Name
		StringBuilder sql = new StringBuilder ("UPDATE I_UserJP i ")
				.append("SET AD_OrgTrx_ID=(SELECT AD_Org_ID FROM AD_org p")
				.append(" WHERE i.JP_OrgTrx_Value=p.Value AND (p.AD_Client_ID=i.AD_Client_ID or p.AD_Client_ID=0) AND p.IsSummary='N') ")
				.append(" WHERE i.JP_OrgTrx_Value IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}

		//Invalid JP_OrgTrx_Value
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "JP_OrgTrx_Value");
		sql = new StringBuilder ("UPDATE I_UserJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append(" WHERE AD_Org_ID IS NULL AND JP_OrgTrx_Value IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(message +" : " + e.toString() + " : " + sql );
		}

		if(no > 0)
		{
			return false;
		}

		return true;

	}//reverseLookupAD_OrgTrx_ID

	/**
	 *
	 * Reverse Look up C_Job_ID From JP_Job_Name
	 *
	 * @throws Exception
	 */
	private boolean reverseLookupC_Job_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_UserJP i ")
				.append("SET C_Job_ID=(SELECT C_Job_ID FROM C_Job p")
				.append(" WHERE i.JP_Job_Name=p.Name AND p.AD_Client_ID=i.AD_Client_ID) ")
				.append(" WHERE i.JP_Job_Name IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}

		//Invalid JP_TaxCategory_Name
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_Job_Name");
		sql = new StringBuilder ("UPDATE I_UserJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append(" WHERE JP_Job_Name IS NOT NULL AND C_Job_ID IS NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(message +" : " + e.toString() + " : " + sql );
		}

		if(no > 0)
		{
			return false;
		}

		return true;

	}//reverseLookupC_Job_ID


	/**
	 * Reverse Look up Supervisor_ID From E-Mail of User
	 *
	 * @throws Exception
	 */
	private boolean reverseLookupSupervisor_ID() throws Exception
	{
		if(Util.isEmpty(p_JP_ImportUserIdentifier) || p_JP_ImportUserIdentifier.equals(JP_ImportUserIdentifier_NotCollate))
			return true;

		StringBuilder sql = null;
		int no = 0;

		if(p_JP_ImportUserIdentifier.equals(JP_ImportUserIdentifier_EMail))//E-Mail
		{
			sql = new StringBuilder ("UPDATE I_UserJP i ")
					.append("SET Supervisor_ID=(SELECT AD_User_ID FROM AD_User p")
					.append(" WHERE i.JP_Supervisor_EMail=p.EMail AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ) ")
					.append("WHERE JP_Supervisor_EMail IS NOT NULL")
					.append(" AND I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportUserIdentifier.equals(JP_ImportUserIdentifier_Name)) { //Name

			sql = new StringBuilder ("UPDATE I_UserJP i ")
					.append("SET Supervisor_ID=(SELECT AD_User_ID FROM AD_User p")
					.append(" WHERE i.JP_Supervisor_Name=p.Name AND  ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ) ")
					.append(" WHERE i.JP_Supervisor_Name IS NOT NULL AND i.Supervisor_ID IS NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportUserIdentifier.equals(JP_ImportUserIdentifier_Value)) { //Value

			sql = new StringBuilder ("UPDATE I_UserJP i ")
					.append("SET Supervisor_ID=(SELECT AD_User_ID FROM AD_User p")
					.append(" WHERE i.JP_Supervisor_Value=p.Value AND  ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ) ")
					.append(" WHERE i.Supervisor_ID IS NULL AND i.JP_Supervisor_Value IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportUserIdentifier.equals(JP_ImportUserIdentifier_ValueEMail)) { //Value + E-Mail

			sql = new StringBuilder ("UPDATE I_UserJP i ")
					.append("SET Supervisor_ID=(SELECT AD_User_ID FROM AD_User p")
					.append(" WHERE i.JP_Supervisor_Value=p.Value AND i.JP_Supervisor_EMail=p.EMail AND  ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ) ")
					.append(" WHERE i.Supervisor_ID IS NULL ")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportUserIdentifier.equals(JP_ImportUserIdentifier_ValueName)) { //Value + Name

			sql = new StringBuilder ("UPDATE I_UserJP i ")
					.append("SET Supervisor_ID=(SELECT AD_User_ID FROM AD_User p")
					.append(" WHERE i.JP_Supervisor_Value=p.Value AND i.JP_Supervisor_Name=p.Name AND  ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ) ")
					.append(" WHERE i.Supervisor_ID IS NULL ")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportUserIdentifier.equals(JP_ImportUserIdentifier_ValueNameEmail)) { //Value + Name + EMail

			sql = new StringBuilder ("UPDATE I_UserJP i ")
					.append("SET Supervisor_ID=(SELECT AD_User_ID FROM AD_User p")
					.append(" WHERE i.JP_Supervisor_Value=p.Value AND i.JP_Supervisor_Name=p.Name AND i.JP_Supervisor_EMail=p.EMail AND  ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ) ")
					.append(" WHERE i.Supervisor_ID IS NULL ")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportUserIdentifier.equals(JP_ImportUserIdentifier_NotCollate)) {

			return true;

		}else {

			return true;

		}


		try {

			no = DB.executeUpdateEx(sql.toString(), get_TrxName());

		}catch(Exception e) {

			message = message + " : " +e.toString()+ " : "+sql.toString();
			return false;
		}

		return true;

	}//reverseLookupSupervisor_ID


	/**
	 * Reverse Look up R_DefaultMailText_ID From JP_MailText_Name
	 *
	 * @throws Exception
	 */
	private boolean reverseLookupR_DefaultMailText_ID()throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_UserJP i ")
				.append("SET R_DefaultMailText_ID=(SELECT R_DefaultMailText_ID FROM M_FreightCategory p")
				.append(" WHERE i.JP_MailText_Name=p.Name AND p.AD_Client_ID=i.AD_Client_ID) ")
				.append(" WHERE i.JP_MailText_Name IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message + " : "+ e.toString() + " : " + sql );
		}

		//Invalid JP_MailText_Name
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_MailText_Name");
		sql = new StringBuilder ("UPDATE I_UserJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append(" WHERE JP_MailText_Name IS NOT NULL AND R_DefaultMailText_ID IS NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(message +" : " + e.toString() +" : " + sql );
		}

		if(no > 0)
		{
			return false;
		}

		return true;

	}//reverseLookupR_DefaultMailText_ID


	/**
	 * Reverse Loog up C_Location_ID From JP_Location_Label
	 *
	 * @throws Exception
	 */
	private boolean reverseLookupC_Location_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_UserJP i ")
				.append("SET C_Location_ID=(SELECT C_Location_ID FROM C_Location p")
				.append(" WHERE i.JP_Location_Label= p.JP_Location_Label AND p.AD_Client_ID=i.AD_Client_ID) ")
				.append(" WHERE i.C_Location_ID IS NULL AND JP_Location_Label IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message + " : " + e.toString() + " : " + sql );
		}

		//Invalid JP_Location_Label
		message = Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_Location_Label");
		sql = new StringBuilder ("UPDATE I_UserJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append(" WHERE JP_Location_Label IS NOT NULL AND C_Location_ID IS NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message + " : " + e.toString() + " : " +  sql );
		}

		if(no > 0)
		{
			return false;
		}

		return true;
	}



	/**
	 * Reverse Look up SalesRep_ID From JP_SalesRep_Email
	 *
	 * @throws Exception
	 */
	private boolean reverseLookupSalesRep_ID() throws Exception
	{
		if(Util.isEmpty(p_JP_ImportUserIdentifier)||p_JP_ImportUserIdentifier.equals(JP_ImportUserIdentifier_NotCollate))
			return true;

		StringBuilder sql = null;
		int no = 0;

		if(p_JP_ImportUserIdentifier.equals(JP_ImportUserIdentifier_EMail))//E-Mail
		{
			sql = new StringBuilder ("UPDATE I_UserJP i ")
					.append("SET SalesRep_ID=(SELECT AD_User_ID FROM AD_User p")
					.append(" WHERE i.JP_SalesRep_Email=p.EMail AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ) ")
					.append(" WHERE i.JP_SalesRep_Email IS NOT NULL  AND i.SalesRep_ID IS NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportUserIdentifier.equals(JP_ImportUserIdentifier_Name)) { //Name

			sql = new StringBuilder ("UPDATE I_UserJP i ")
					.append("SET SalesRep_ID=(SELECT AD_User_ID FROM AD_User p")
					.append(" WHERE i.JP_SalesRep_Name=p.Name AND  ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ) ")
					.append(" WHERE i.JP_SalesRep_Name IS NOT NULL AND i.SalesRep_ID IS NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportUserIdentifier.equals(JP_ImportUserIdentifier_Value)) { //Value

			sql = new StringBuilder ("UPDATE I_UserJP i ")
					.append("SET SalesRep_ID=(SELECT AD_User_ID FROM AD_User p")
					.append(" WHERE i.JP_SalesRep_Value=p.Value AND  ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ) ")
					.append(" WHERE i.SalesRep_ID IS NULL AND i.JP_SalesRep_Value IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportUserIdentifier.equals(JP_ImportUserIdentifier_ValueEMail)) { //Value + E-Mail

			sql = new StringBuilder ("UPDATE I_UserJP i ")
					.append("SET SalesRep_ID=(SELECT AD_User_ID FROM AD_User p")
					.append(" WHERE i.JP_SalesRep_Value=p.Value AND i.JP_SalesRep_EMail=p.EMail AND  ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ) ")
					.append(" WHERE i.SalesRep_ID IS NULL ")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportUserIdentifier.equals(JP_ImportUserIdentifier_ValueName)) { //Value + Name

			sql = new StringBuilder ("UPDATE I_UserJP i ")
					.append("SET SalesRep_ID=(SELECT AD_User_ID FROM AD_User p")
					.append(" WHERE i.JP_SalesRep_Value=p.Value AND i.JP_SalesRep_Name=p.Name AND  ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ) ")
					.append(" WHERE i.SalesRep_ID IS NULL ")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportUserIdentifier.equals(JP_ImportUserIdentifier_ValueNameEmail)) { //Value + Name + EMail

			sql = new StringBuilder ("UPDATE I_UserJP i ")
					.append("SET SalesRep_ID=(SELECT AD_User_ID FROM AD_User p")
					.append(" WHERE i.JP_SalesRep_Value=p.Value AND i.JP_SalesRep_Name=p.Name AND i.JP_SalesRep_EMail=p.EMail AND  ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ) ")
					.append(" WHERE i.SalesRep_ID IS NULL ")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportUserIdentifier.equals(JP_ImportUserIdentifier_NotCollate)) {
				return true;

		}else {

			return true;

		}


		try {

			no = DB.executeUpdateEx(sql.toString(), get_TrxName());

		}catch(Exception e) {

			message = e.toString();
			return false;
		}

		return true;

	}//reverseLookupSalesRep_ID


	/**
	 * Reverse Look up C_Greeting_ID From JP_Greeting_Name
	 *
	 * @throws Exception
	 */
	private boolean reverseLookupC_Greeting_ID() throws Exception
	{
		int no = 0;

		//Reverse Look up C_Greeting_ID From JP_SalesRep_Email
		StringBuilder sql = new StringBuilder ("UPDATE I_UserJP i ")
				.append("SET C_Greeting_ID=(SELECT C_Greeting_ID FROM C_Greeting p")
				.append(" WHERE i.JP_Greeting_Name=p.Name AND p.AD_Client_ID=i.AD_Client_ID ) ")
				.append(" WHERE i.JP_Greeting_Name IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message + " : " + e.toString() +" : " + sql );
		}

		//Invalid JP_Greeting_Name
		message= Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "JP_Greeting_Name");
		sql = new StringBuilder ("UPDATE I_UserJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append(" WHERE JP_Greeting_Name IS NOT NULL AND C_Greeting_ID IS NULL ")
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

	}//reverseLookupC_Greeting_ID


	/**
	 * Create New Product
	 *
	 * @param importUser
	 * @throws SQLException
	 */
	private boolean createNewUser(X_I_UserJP importUser, MUser newUser) throws SQLException
	{
		boolean isEMailLogin =  MSysConfig.getBooleanValue(MSysConfig.USE_EMAIL_FOR_LOGIN, false, getAD_Client_ID());
		if(isEMailLogin)
		{
			if(!Util.isEmpty(importUser.getPassword()) && Util.isEmpty(importUser.getEMail()))
			{

				Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "EMail")};
				importUser.setI_ErrorMsg(Msg.getMsg(getCtx(), "Error") + Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs));
				importUser.setI_IsImported(false);
				importUser.setProcessed(false);
				importUser.saveEx(get_TrxName());
				return false;
			}
		}

		if(!Util.isEmpty(importUser.getEMail()))
		{
			String email = importUser.getEMail();
			if(email.indexOf("@") == -1)
			{
				importUser.setI_ErrorMsg(Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(Env.getCtx(), "EMail"));
				importUser.setI_IsImported(false);
				importUser.setProcessed(false);
				importUser.saveEx(get_TrxName());
				return false;
			}
		}

		if(Util.isEmpty(importUser.getName()))
		{
			importUser.setI_ErrorMsg(Msg.getMsg(getCtx(), "JP_Null") + Msg.getElement(getCtx(),"Name"));
			importUser.setI_IsImported(false);
			importUser.setProcessed(false);
			importUser.saveEx(get_TrxName());
			return false;
		}

		ModelValidationEngine.get().fireImportValidate(this, importUser, newUser, ImportValidator.TIMING_BEFORE_IMPORT);

		PO.copyValues(importUser, newUser);

		//Replace <CRLF>->\r\n,  <CR> -> \r,  <LF> -> \n
		if(!Util.isEmpty(newUser.getDescription()))
		{
			newUser.setDescription(newUser.getDescription().replaceAll("<CRLF>", "\r\n"));
			newUser.setDescription(newUser.getDescription().replaceAll("<CR>", "\r"));
			newUser.setDescription(newUser.getDescription().replaceAll("<LF>", "\n"));
		}

		if(!Util.isEmpty(importUser.getComments()))
		{
			newUser.setComments(newUser.getComments().replaceAll("<CRLF>", "\r\n"));
			newUser.setComments(newUser.getComments().replaceAll("<CR>", "\r"));
			newUser.setComments(newUser.getComments().replaceAll("<LF>", "\n"));
		}

		newUser.setIsActive(importUser.isI_IsActiveJP());

		ModelValidationEngine.get().fireImportValidate(this, importUser, newUser, ImportValidator.TIMING_AFTER_IMPORT);

		try {
			newUser.saveEx(get_TrxName());
		}catch (Exception e) {
			importUser.setI_ErrorMsg(Msg.getMsg(getCtx(),"SaveIgnored") + " : "+ Msg.getElement(getCtx(), "AD_User_ID") + " : " + e.toString());
			importUser.setI_IsImported(false);
			importUser.setProcessed(false);
			importUser.saveEx(get_TrxName());
			return false;
		}

		importUser.setAD_User_ID(newUser.getAD_User_ID());

		importUser.setI_ErrorMsg(Msg.getMsg(getCtx(), "NewRecord"));
		importUser.setI_IsImported(true);
		importUser.setProcessed(true);
		importUser.saveEx(get_TrxName());

		return true;

	}

	/**
	 *
	 * Update Product
	 *
	 * @param importUser
	 * @throws SQLException
	 */
	private boolean updateUser(X_I_UserJP importUser, MUser updateUser) throws SQLException
	{
		ModelValidationEngine.get().fireImportValidate(this, importUser, updateUser, ImportValidator.TIMING_BEFORE_IMPORT);

		//Update Product
		MTable AD_User_Table = MTable.get(getCtx(), MUser.Table_ID, get_TrxName());
		MColumn[] AD_User_Columns = AD_User_Table.getColumns(true);

		MTable I_UserJP_Table = MTable.get(getCtx(), X_I_UserJP.Table_ID, get_TrxName());
		MColumn[] I_UserJP_Columns = I_UserJP_Table.getColumns(true);

		MColumn i_Column = null;
		for(int i = 0 ; i < AD_User_Columns.length; i++)
		{
			i_Column = AD_User_Columns[i];
			if(i_Column.isVirtualColumn() || i_Column.isKey() || i_Column.isUUIDColumn())
				continue;//i

			if(i_Column.getColumnName().equals("IsActive")
//				|| i_Column.getColumnName().equals("EMail") //Can not Update EMail
				|| i_Column.getColumnName().equals("AD_Client_ID")
				|| i_Column.getColumnName().equals("Value")
				|| i_Column.getColumnName().equals("Name")
				|| i_Column.getColumnName().equals("Processing")
				|| i_Column.getColumnName().equals("Created")
				|| i_Column.getColumnName().equals("CreatedBy")
				|| i_Column.getColumnName().equals("Updated")
				|| i_Column.getColumnName().equals("UpdatedBy") )
				continue;//i

			MColumn j_Column = null;
			Object importValue = null;
			for(int j = 0 ; j < I_UserJP_Columns.length; j++)
			{
				j_Column = I_UserJP_Columns[j];

				if(i_Column.getColumnName().equals(j_Column.getColumnName()))
				{
					importValue = importUser.get_Value(j_Column.getColumnName());

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
							//Replace <CRLF>->\r\n,  <CR> -> \r,  <LF> -> \n
							if(j_Column.getColumnName().equals("Descripton"))
							{
								importValue = importValue.toString().replaceAll("<CRLF>", "\r\n");
								importValue = importValue.toString().replaceAll("<CR>", "\r");
								importValue = importValue.toString().replaceAll("<LF>", "\n");
							}

							if(j_Column.getColumnName().equals("Commnets"))
							{
								importValue = importValue.toString().replaceAll("<CRLF>", "\r\n");
								importValue = importValue.toString().replaceAll("<CR>", "\r");
								importValue = importValue.toString().replaceAll("<LF>", "\n");
							}

							updateUser.set_ValueNoCheck(i_Column.getColumnName(), importValue);
						}

						break;

					}else if(j_Column.getColumnName().endsWith("_ID")) {

						Integer p_key = (Integer)importValue;
						if(p_key.intValue() <= 0 && !j_Column.getColumnName().equals("AD_OrgTrx_ID"))
							break;
					}

					if(importValue != null)
					{
						try
						{
							updateUser.set_ValueNoCheck(i_Column.getColumnName(), importValue);

							//Replace <CRLF>->\r\n,  <CR> -> \r,  <LF> -> \n
							if(j_Column.getColumnName().equals("Descripton"))
							{
								updateUser.setDescription(updateUser.getDescription().replaceAll("<CRLF>", "\r\n"));
								updateUser.setDescription(updateUser.getDescription().replaceAll("<CR>", "\r"));
								updateUser.setDescription(updateUser.getDescription().replaceAll("<LF>", "\n"));
							}

							if(j_Column.getColumnName().equals("Comments"))
							{
								updateUser.setComments(updateUser.getComments().replaceAll("<CRLF>", "\r\n"));
								updateUser.setComments(updateUser.getComments().replaceAll("<CR>", "\r"));
								updateUser.setComments(updateUser.getComments().replaceAll("<LF>", "\n"));
							}

						}catch (Exception e) {

							importUser.setI_ErrorMsg(Msg.getMsg(getCtx(), "Error") + " Column = " + i_Column.getColumnName() + " & " + "Value = " +importValue.toString() +" -> " + e.toString());
							importUser.setI_IsImported(false);
							importUser.setProcessed(false);
							importUser.saveEx(get_TrxName());
							return false;
						}
					}

					break;
				}
			}//for j

		}//for i

		updateUser.setIsActive(importUser.isI_IsActiveJP());
		if(importUser.getAD_User_ID() == 0)
			importUser.setAD_User_ID(updateUser.getAD_User_ID());

		ModelValidationEngine.get().fireImportValidate(this, importUser, updateUser, ImportValidator.TIMING_AFTER_IMPORT);

		try {
			updateUser.saveEx(get_TrxName());
		}catch (Exception e) {
			importUser.setI_ErrorMsg(Msg.getMsg(getCtx(),"SaveError")  + " : " + Msg.getElement(getCtx(), "AD_User_ID") + " : " + e.toString());
			importUser.setI_IsImported(false);
			importUser.setProcessed(false);
			importUser.saveEx(get_TrxName());
			return false;
		}

		importUser.setI_ErrorMsg(Msg.getMsg(getCtx(), "Update"));
		importUser.setI_IsImported(true);
		importUser.setProcessed(true);
		importUser.saveEx(get_TrxName());

		return true;

	}

}	//	Import User
