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
import java.util.logging.Level;

import org.adempiere.model.ImportValidator;
import org.adempiere.process.ImportProcess;
import org.adempiere.util.IProcessUI;
import org.compiere.model.MColumn;
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

		if (m_deleteOldImported)
		{
			sql = new StringBuilder ("DELETE I_UserJP ")
				.append("WHERE I_IsImported='Y'").append(clientCheck);
			no = DB.executeUpdate(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.INFO)) log.info("Delete Old Imported =" + no);
		}


		ModelValidationEngine.get().fireImportValidate(this, null, null, ImportValidator.TIMING_BEFORE_VALIDATE);

		//Reverse Lookup Surrogate Key
		reverseLookupAD_User_ID();
		reverseLookupAD_Org_ID();
		reverseLookupAD_OrgTrx_ID();
		reverseLookupJP_Corporation_ID();
		reverseLookupC_BPartner_ID();
		reverseLookupC_BPartner_Location_ID();
		reverseLookupC_Job_ID();
		reverseLookupSupervisor_ID();
		reverseLookupR_DefaultMailText_ID();
		reverseLookupC_Location_ID();
		reverseLookupSalesRep_ID();

		ModelValidationEngine.get().fireImportValidate(this, null, null, ImportValidator.TIMING_AFTER_VALIDATE);

		commitEx();

		sql = new StringBuilder ("SELECT * FROM I_UserJP WHERE I_IsImported='N' ")
				.append(clientCheck).append(" ORDER BY Value ");
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
			rs = pstmt.executeQuery();
			String preValue = "";
			while (rs.next())
			{
				X_I_UserJP imp = new X_I_UserJP (getCtx (), rs, get_TrxName());

				boolean isNew = true;
				if(imp.getAD_User_ID()!=0)
				{
					isNew =false;

				}else{

					if(preValue.equals(imp.getValue()))
					{
						isNew = false;

					}else {

						preValue = imp.getValue();

					}

				}

				if(isNew)
				{
					createNewUser(imp);

				}else{

					updateUser(imp);
				}

			}//while

		}catch (Exception e) {

			log.log(Level.SEVERE, e.toString(), e);

		}finally{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}


		return "";
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
	private void reverseLookupAD_User_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		String msg = new String();
		int no = 0;

		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "AD_User_ID");
		if (processMonitor != null)	processMonitor.statusUpdate(msg);

		//Reverse lookup AD_User_ID From Value && Name
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "AD_User_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "Value" + " : " + Msg.getElement(getCtx(), "Name") );
		sql = new StringBuilder ("UPDATE I_UserJP i ")
				.append("SET AD_User_ID=(SELECT AD_User_ID FROM AD_User p")
				.append(" WHERE i.Value=p.Value AND i.Name=p.Name AND p.AD_Client_ID=i.AD_Client_ID) ")
				.append(" WHERE i.AD_User_ID IS NULL AND i.Value IS NOT NULL AND i.Name IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		//Reverse lookup AD_User_ID From E-Mail
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "AD_User_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "EMail") ;
		sql = new StringBuilder ("UPDATE I_UserJP i ")
			.append("SET AD_User_ID=(SELECT AD_User_ID FROM AD_User p")
			.append(" WHERE i.EMail=p.EMail AND i.AD_Client_ID=p.AD_Client_ID) ")
			.append(" WHERE i.EMail IS NOT NULL AND i.AD_User_ID IS NULL")
			.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no + ":" + sql);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		//Error : Name is null
		msg = Msg.getMsg(getCtx(), "JP_Null") + Msg.getElement(getCtx(), "Name");
		sql = new StringBuilder ("UPDATE I_UserJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append(" WHERE Name IS NULL AND AD_User_ID IS NULL ")
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


	}//reverseLookupM_Product_ID

	/**
	 * Reverse look up JP_Corporation_ID From JP_Corporation_Value
	 *
	 * @throws Exception
	 */
	private void reverseLookupJP_Corporation_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		String msg = new String();
		int no = 0;

		//Reverse lookup C_BPartner_ID From JP_BPartner_Value
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "JP_Corporation_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "JP_Corporation_Value") ;
		sql = new StringBuilder ("UPDATE I_UserJP i ")
			.append("SET JP_Corporation_ID=(SELECT JP_Corporation_ID FROM JP_Corporation p")
			.append(" WHERE i.JP_Corporation_Value=p.Value AND i.AD_Client_ID=p.AD_Client_ID) ")
			.append("WHERE i.JP_Corporation_ID IS NULL AND i.JP_Corporation_Value IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg + "=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		//Invalid BPartner_Value
		msg = Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_Corporation_Value");
		sql = new StringBuilder ("UPDATE I_UserJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append("WHERE JP_Corporation_ID IS NULL AND JP_Corporation_Value IS NOT NULL")
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

	}//reverseLookupC_BPartner_ID


	/**
	 * Reverse look up C_BPartner_ID From JP_BPartner_Value
	 *
	 * @throws Exception
	 */
	private void reverseLookupC_BPartner_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		String msg = new String();
		int no = 0;


		 msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_BPartner_ID");
		if (processMonitor != null)	processMonitor.statusUpdate(msg);

		//Reverse lookup C_BPartner_ID From JP_BPartner_Value
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_BPartner_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "JP_BPartner_Value") ;
		sql = new StringBuilder ("UPDATE I_UserJP i ")
			.append("SET C_BPartner_ID=(SELECT C_BPartner_ID FROM C_BPartner p")
			.append(" WHERE i.JP_BPartner_Value=p.Value AND i.AD_Client_ID=p.AD_Client_ID) ")
			.append("WHERE i.C_BPartner_ID IS NULL AND i.JP_BPartner_Value IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine( msg + "=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		//Invalid BPartner_Value
		msg = Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_BPartner_Value");
		sql = new StringBuilder ("UPDATE I_UserJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append("WHERE C_BPartner_ID IS NULL AND JP_BPartner_Value IS NOT NULL")
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

	}//reverseLookupC_BPartner_ID

	/**
	 * Reverse look up C_BPartner_Location_ID From JP_BPartner_Location_Name
	 *
	 * @throws Exception
	 */
	private void reverseLookupC_BPartner_Location_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		String msg = new String();
		int no = 0;

		 msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_BPartner_Location_ID");
		if (processMonitor != null)	processMonitor.statusUpdate(msg);

		//Reverse lookup C_BPartner_Location_ID From JP_BPartner_Location_Name
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_BPartner_Location_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "JP_BPartner_Location_Name") ;
		sql = new StringBuilder ("UPDATE I_UserJP i ")
			.append("SET C_BPartner_Location_ID=(SELECT C_BPartner_Location_ID FROM C_BPartner_Location p")
			.append(" WHERE i.JP_BPartner_Location_Name=p.Name AND i.C_Bpartner_ID =p.C_BPartner_ID) ")
			.append("WHERE i.C_BPartner_Location_ID IS NULL AND i.JP_BPartner_Location_Name IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine( msg + "=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		//Invalid JP_BPartner_Location_Name
		msg = Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_BPartner_Location_Name");
		sql = new StringBuilder ("UPDATE I_UserJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append("WHERE C_BPartner_Location_ID IS NULL AND JP_BPartner_Location_Name IS NOT NULL")
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

	}//reverseLookupC_BPartner_ID


	/**
	 * Reverse Look up Organization From JP_Org_Value
	 *
	 **/
	private void reverseLookupAD_Org_ID() throws Exception
	{

		StringBuilder sql = new StringBuilder();
		String msg = new String();
		int no = 0;

		 msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "AD_Org_ID");
		if (processMonitor != null)	processMonitor.statusUpdate(msg);

		//Look up AD_Org ID From JP_Org_Value
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "AD_Org_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "JP_Org_Value") ;
		sql = new StringBuilder ("UPDATE I_UserJP i ")
				.append("SET AD_Org_ID=(SELECT AD_Org_ID FROM AD_org p")
				.append(" WHERE i.JP_Org_Value=p.Value AND (p.AD_Client_ID=i.AD_Client_ID or p.AD_Client_ID=0) AND p.IsSummary='N') ")
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
		sql = new StringBuilder ("UPDATE I_UserJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append(" WHERE AD_Org_ID = 0 AND JP_Org_Value IS NOT NULL AND JP_Org_Value <> '0' ")
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

	}//reverseLookupAD_Org_ID

	/**
	 * Reverse Look up Trx Organization From JP_OrgTrx_Value
	 *
	 **/
	private void reverseLookupAD_OrgTrx_ID() throws Exception
	{

		StringBuilder sql = new StringBuilder();
		String msg = new String();
		int no = 0;

		 msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "AD_OrgTrx_ID");
		if (processMonitor != null)	processMonitor.statusUpdate(msg);

		//Look up AD_OrgTrx ID From JP_OrgTrx_Name
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "AD_OrgTrx_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "JP_OrgTrx_Value") ;
		sql = new StringBuilder ("UPDATE I_UserJP i ")
				.append("SET AD_OrgTrx_ID=(SELECT AD_Org_ID FROM AD_org p")
				.append(" WHERE i.JP_OrgTrx_Value=p.Value AND (p.AD_Client_ID=i.AD_Client_ID or p.AD_Client_ID=0) AND p.IsSummary='N') ")
				.append(" WHERE i.JP_OrgTrx_Value IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no );
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + sql );
		}

		//Invalid JP_OrgTrx_Value
		msg = Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_OrgTrx_Value");
		sql = new StringBuilder ("UPDATE I_UserJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append(" WHERE AD_Org_ID IS NULL AND JP_OrgTrx_Value IS NOT NULL ")
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

	}//reverseLookupAD_OrgTrx_ID

	/**
	 *
	 * Reverse Look up C_Job_ID From JP_Job_Name
	 *
	 * @throws Exception
	 */
	private void reverseLookupC_Job_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		String msg = new String();
		int no = 0;

		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_Job_ID");
		if (processMonitor != null)	processMonitor.statusUpdate(msg);


		//Look up C_TaxCategory_ID From JP_TaxCategory_Name
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_Job_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "JP_Job_Name") ;
		sql = new StringBuilder ("UPDATE I_UserJP i ")
				.append("SET C_Job_ID=(SELECT C_Job_ID FROM C_Job p")
				.append(" WHERE i.JP_Job_Name=p.Name AND p.AD_Client_ID=i.AD_Client_ID) ")
				.append(" WHERE i.JP_Job_Name IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		//Invalid JP_TaxCategory_Name
		msg = Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_Job_Name");
		sql = new StringBuilder ("UPDATE I_UserJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append(" WHERE JP_Job_Name IS NOT NULL AND C_Job_ID IS NULL ")
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

	}//reverseLookupC_Job_ID


	/**
	 * Reverse Look up Supervisor_ID From E-Mail of User
	 *
	 * @throws Exception
	 */
	private void reverseLookupSupervisor_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		String msg = new String();
		int no = 0;


		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "Supervisor_ID");
		if (processMonitor != null)	processMonitor.statusUpdate(msg);

		//Look up Supervisor_ID From  E-Mail
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "Supervisor_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "JP_Supervisor_EMail") ;
		sql = new StringBuilder ("UPDATE I_UserJP i ")
				.append("SET Supervisor_ID=(SELECT AD_User_ID FROM AD_User p")
				.append(" WHERE i.JP_Supervisor_EMail=p.EMail AND i.AD_Client_ID=p.AD_Client_ID ) ")
				.append("WHERE JP_Supervisor_EMail IS NOT NULL")
				.append(" AND I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		//Invalid JP_Supervisor_EMail
		msg = Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_Supervisor_EMail");
		sql = new StringBuilder ("UPDATE I_UserJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append(" WHERE JP_Supervisor_EMail IS NOT NULL AND AD_User_ID IS NULL ")
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

	}//reverseLookupSupervisor_ID


	/**
	 * Reverse Look up R_DefaultMailText_ID From JP_MailText_Name
	 *
	 * @throws Exception
	 */
	private void reverseLookupR_DefaultMailText_ID()throws Exception
	{
		StringBuilder sql = new StringBuilder();
		String msg = new String();
		int no = 0;

		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "R_DefaultMailText_ID");
		if (processMonitor != null)	processMonitor.statusUpdate(msg);

		//Look up M_FreightCategory_ID From JP_FreightCategory_Value
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "R_DefaultMailText_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "JP_MailText_Name") ;
		sql = new StringBuilder ("UPDATE I_UserJP i ")
				.append("SET R_DefaultMailText_ID=(SELECT R_DefaultMailText_ID FROM M_FreightCategory p")
				.append(" WHERE i.JP_MailText_Name=p.Name AND p.AD_Client_ID=i.AD_Client_ID) ")
				.append(" WHERE i.JP_MailText_Name IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		//Invalid JP_MailText_Name
		msg = Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_MailText_Name");
		sql = new StringBuilder ("UPDATE I_UserJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append(" WHERE JP_MailText_Name IS NOT NULL AND R_DefaultMailText_ID IS NULL ")
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

	}//reverseLookupR_DefaultMailText_ID


	/**
	 * Reverse Loog up C_Location_ID From JP_Location_Label
	 *
	 * @throws Exception
	 */
	private void reverseLookupC_Location_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		String msg = new String();
		int no = 0;

		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_Location_ID");
		if (processMonitor != null)	processMonitor.statusUpdate(msg);

		//Reverse Loog up C_Location_ID From JP_Location_Label
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_Location_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "JP_Location_Label") ;
		sql = new StringBuilder ("UPDATE I_UserJP i ")
				.append("SET C_Location_ID=(SELECT C_Location_ID FROM C_Location p")
				.append(" WHERE i.JP_Location_Label= p.JP_Location_Label AND p.AD_Client_ID=i.AD_Client_ID) ")
				.append(" WHERE i.C_Location_ID IS NULL AND JP_Location_Label IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + sql );
		}

		//Invalid JP_Location_Label
		msg = Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_Location_Label");
		sql = new StringBuilder ("UPDATE I_UserJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append(" WHERE JP_Location_Label IS NOT NULL AND C_Location_ID IS NULL ")
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
	 * Reverse Look up SalesRep_ID From JP_SalesRep_Email
	 *
	 * @throws Exception
	 */
	private void reverseLookupSalesRep_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		String msg = new String();
		int no = 0;

		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "SalesRep_ID");
		if (processMonitor != null)	processMonitor.statusUpdate(msg);

		//Reverse Look up SalesRep_ID From JP_SalesRep_Email
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "SalesRep_ID=")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "JP_SalesRep_Email") ;
		sql = new StringBuilder ("UPDATE I_UserJP i ")
				.append("SET SalesRep_ID=(SELECT AD_User_ID FROM AD_User p")
				.append(" WHERE i.JP_SalesRep_Email=p.EMail AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ) ")
				.append(" WHERE i.JP_SalesRep_Email IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		//Invalid JP_User_Value
		msg = Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_SalesRep_Email");
		sql = new StringBuilder ("UPDATE I_UserJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append(" WHERE JP_SalesRep_Email IS NOT NULL AND SalesRep_ID IS NULL ")
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

	}//reverseLookupSalesRep_ID



	/**
	 * Create New Product
	 *
	 * @param importUser
	 * @throws SQLException
	 */
	private void createNewUser(X_I_UserJP importUser) throws SQLException
	{
		MUser newUser = new MUser(getCtx(), 0, get_TrxName());

		ModelValidationEngine.get().fireImportValidate(this, importUser, newUser, ImportValidator.TIMING_BEFORE_IMPORT);

		PO.copyValues(importUser, newUser);
		newUser.setIsActive(importUser.isI_IsActiveJP());
		ModelValidationEngine.get().fireImportValidate(this, importUser, newUser, ImportValidator.TIMING_AFTER_IMPORT);

		newUser.saveEx(get_TrxName());

		importUser.setAD_User_ID(newUser.getAD_User_ID());
		importUser.setI_ErrorMsg(Msg.getMsg(getCtx(), "NewRecord"));
		importUser.setI_IsImported(true);
		importUser.setProcessed(true);
		importUser.saveEx(get_TrxName());
		commitEx();

	}

	/**
	 *
	 * Update Product
	 *
	 * @param importUser
	 * @throws SQLException
	 */
	private void updateUser(X_I_UserJP importUser) throws SQLException
	{
		MUser updateUser = new MUser(getCtx(), importUser.getAD_User_ID(), get_TrxName());

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
				|| i_Column.getColumnName().equals("IsStocked")
				|| i_Column.getColumnName().equals("ProductType")
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
							updateUser.set_ValueNoCheck(i_Column.getColumnName(), importValue);
						}

						break;

					}else if(j_Column.getColumnName().endsWith("_ID")) {

						Integer p_key = (Integer)importValue;
						if(p_key.intValue() <= 0 && !j_Column.getColumnName().equals("AD_OrgTrx_ID"))
							break;
					}

					updateUser.set_ValueNoCheck(i_Column.getColumnName(), importValue);
					break;
				}
			}//for j

		}//for i

		updateUser.setIsActive(importUser.isI_IsActiveJP());
		ModelValidationEngine.get().fireImportValidate(this, importUser, updateUser, ImportValidator.TIMING_AFTER_IMPORT);

		updateUser.saveEx(get_TrxName());

		importUser.setI_ErrorMsg(Msg.getMsg(getCtx(), "Update"));
		importUser.setI_IsImported(true);
		importUser.setProcessed(true);
		importUser.saveEx(get_TrxName());
		commitEx();

	}

}	//	Import User
