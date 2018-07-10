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
import org.compiere.model.MCalendar;
import org.compiere.model.MJournal;
import org.compiere.model.MJournalLine;
import org.compiere.model.MPeriod;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.PO;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.X_I_GLJournalJP;
import jpiere.base.plugin.org.adempiere.model.X_I_WarehouseJP;
import jpiere.base.plugin.util.JPiereValidCombinationUtil;

/**
 * 	JPIERE-0407:Import GL Journal
 *
 *  @author Hideaki Hagiwara
 *
 */
public class JPiereImportGLJournal extends SvrProcess  implements ImportProcess
{
	/**	Client to be imported to		*/
	private int		 m_AD_Client_ID = 0;

	private boolean p_deleteOldImported = false;

	/**	Only validate, don't import		*/
	private boolean p_IsValidateOnly = false;

	private IProcessUI processMonitor = null;

	private String docAction = "DR";

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
			else if (name.equals("DocAction"))
				docAction = para[i].getParameterAsString();
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
			sql = new StringBuilder ("DELETE I_GLJournalJP ")
				  .append("WHERE I_IsImported='Y'").append (clientCheck);
			no = DB.executeUpdate(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("Delete Old Impored =" + no);
		}

		//Reset Message
		sql = new StringBuilder ("UPDATE I_GLJournalJP ")
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
		reverseLookupGL_Journal_ID();
		reverseLookupAD_Org_ID();
		reverseLookupAD_OrgTrx_ID();
		reverseLookupC_AcctSchema_ID();
		reverseLookupGL_Budget_ID();
		reverseLookupC_DocType_ID();
		reverseLookupGL_Category_ID();
		reverseLookupC_Currency_ID();
		reverseLookupC_ConversionType_ID();

		reverseLookupAccount_ID();
		reverseLookupC_SubAcct_ID();
		reverseLookupC_UOM_ID();

		reverseLookupC_BPartner_ID();
		reverseLookupM_Product_ID();
		reverseLookupC_Project_ID();
		reverseLookupC_ProjectPhase_ID();
		reverseLookupC_ProjectTask_ID();
		reverseLookupC_SalesRegion_ID();
		reverseLookupC_Campaign_ID();
		reverseLookupC_Activity_ID();
		reverseLookupC_LocFrom_ID();
		reverseLookupC_LocTo_ID();
		reverseLookupUser1_ID();
		reverseLookupUser2_ID();
		reverseLookupA_Asset_ID();
		reverseLookupM_Locator_ID();

		ModelValidationEngine.get().fireImportValidate(this, null, null, ImportValidator.TIMING_AFTER_VALIDATE);

		commitEx();
		if (p_IsValidateOnly)
		{
			return "Validated";
		}

		//
		sql = new StringBuilder ("SELECT * FROM I_GLJournalJP WHERE I_IsImported='N' ")
					.append(clientCheck).append(" ORDER BY JP_DataMigration_Identifier, DocumentNo, Line ");
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int recordsNum = 0;
		int skipNum = 0;
		int errorNum = 0;
		int successNum = 0;
		int successCreateDocHeader = 0;
		int successCreateDocLine = 0;
		int failureCreateDocHeader = 0;
		int failureCreateDocLine = 0;
		String records = Msg.getMsg(getCtx(), "JP_NumberOfRecords");
		String skipRecords = Msg.getMsg(getCtx(), "JP_NumberOfSkipRecords");
		String errorRecords = Msg.getMsg(getCtx(), "JP_NumberOfUnexpectedErrorRecords");
		String success = Msg.getMsg(getCtx(), "JP_Success");
		String failure = Msg.getMsg(getCtx(), "JP_Failure");
		String createHeader = Msg.getMsg(getCtx(), "JP_CreateHeader");
		String createLine = Msg.getMsg(getCtx(), "JP_CreateLine");
		String detail = Msg.getMsg(getCtx(), "JP_DetailLog");

		try
		{
			pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
			rs = pstmt.executeQuery();
			String preJP_DataMigration_Identifier = "";
			String preDocumentNo = "";
			MJournal journal = null;
			MJournalLine journalLine = null;

			while (rs.next())
			{
				recordsNum++;

				X_I_GLJournalJP imp = new X_I_GLJournalJP(getCtx (), rs, get_TrxName());

				if(imp.getGL_Journal_ID() != 0)
				{
					skipNum++;
					String msg = Msg.getMsg(getCtx(), "AlreadyExists");
					imp.setI_ErrorMsg(msg);
					imp.setI_IsImported(false);
					imp.setProcessed(false);
					imp.saveEx(get_TrxName());
					commitEx();
					continue;
				}

				boolean isCreateHeader= true;
				if(!Util.isEmpty(preJP_DataMigration_Identifier) && preJP_DataMigration_Identifier.equals(imp.getJP_DataMigration_Identifier()))
				{
					isCreateHeader = false;
					if(journal.getGL_Journal_ID() == 0)
					{
						errorNum++;
						String msg = Msg.getMsg(getCtx(), "JP_UnexpectedError");
						imp.setI_ErrorMsg(msg);
						imp.setI_IsImported(false);
						imp.setProcessed(false);
						imp.saveEx(get_TrxName());
						commitEx();
						continue;
					}

				}else if(Util.isEmpty(preJP_DataMigration_Identifier) && preDocumentNo.equals(imp.getDocumentNo())){

					isCreateHeader = false;
					if(journal.getGL_Journal_ID() == 0)
					{
						errorNum++;
						String msg = Msg.getMsg(getCtx(), "JP_UnexpectedError");
						imp.setI_ErrorMsg(msg);
						imp.setI_IsImported(false);
						imp.setProcessed(false);
						imp.saveEx(get_TrxName());
						commitEx();
						continue;
					}

				} else {

					if(journal != null && journal.getGL_Journal_ID() != 0)
					{
						if(!Util.isEmpty(docAction))
							journal.processIt(docAction);
						journal.saveEx(get_TrxName());
					}

					preJP_DataMigration_Identifier = imp.getJP_DataMigration_Identifier();
					preDocumentNo = imp.getDocumentNo();

				}

				//Create Header
				if(isCreateHeader)
				{
					journal = new MJournal(getCtx (), 0, get_TrxName());
					if(createHeaderJournal(imp, journal))
					{
						successCreateDocHeader++;
					}else {
						failureCreateDocHeader++;
						errorNum++;//Error of Header include number of Error.
						commitEx();
						continue;
					}
				}

				//Create Line
				journalLine = new MJournalLine(journal);
				if(addJournalLine(imp, journal,journalLine))
				{
					successCreateDocLine++;
					successNum++;
				}else {
					failureCreateDocLine++;
					errorNum++;//Error of Line include number of Error.
					commitEx();
					continue;
				}

				commitEx();

				if (processMonitor != null)
				{
					processMonitor.statusUpdate(
						records + " : " + recordsNum + " = "
						+ skipRecords + " : " + skipNum + " + "
						+ errorRecords + " : " + errorNum + " + "
						+ success + " : " + successNum
						+ "   [" + detail +" --> "
						+ createHeader + "( "+  success + " : " + successCreateDocHeader + "  /  " +  failure + " : " + failureCreateDocHeader + " ) + "
						+ createLine  + " ( "+  success + " : " + successCreateDocLine + "  /  " +  failure + " : " + failureCreateDocLine+ " ) ]"
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

		return records + " : " + recordsNum + " = "
				+ skipRecords + " : " + skipNum + " + "
				+ errorRecords + " : " + errorNum + " + "
				+ success + " : " + successNum
				+ "   [" + detail +" --> "
				+ createHeader + "( "+  success + " : " + successCreateDocHeader + "  /  " +  failure + " : " + failureCreateDocHeader + " ) + "
				+ createLine  + " ( "+  success + " : " + successCreateDocLine + "  /  " +  failure + " : " + failureCreateDocLine+ " ) ]"
				;
	}	//	doIt

	@Override
	public String getImportTableName() {
		return X_I_WarehouseJP.Table_Name;
	}


	@Override
	public String getWhereClause() {
		StringBuilder msgreturn = new StringBuilder(" AND AD_Client_ID=").append(m_AD_Client_ID);
		return msgreturn.toString();
	}

	/**
	 * Reverese Look up  GL_Journal_ID From JP_DataMigration_Identifier and DocumentNo
	 *
	 * @throws Exception
	 */
	private void reverseLookupGL_Journal_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		String msg = new String();
		int no = 0;

		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "GL_Journal_ID");
		if (processMonitor != null)	processMonitor.statusUpdate(msg);

		//Reverese Look up  GL_Journal_ID From JP_DataMigration_Identifier
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "GL_Journal_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "JP_DataMigration_Identifier") ;
		sql = new StringBuilder ("UPDATE I_GLJournalJP i ")
				.append("SET GL_Journal_ID=(SELECT GL_Journal_ID FROM GL_Journal p")
				.append(" WHERE i.JP_DataMigration_Identifier=p.JP_DataMigration_Identifier AND p.AD_Client_ID=i.AD_Client_ID) ")
				.append(" WHERE i.GL_Journal_ID IS NULL AND i.JP_DataMigration_Identifier IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + e.toString() + sql );
		}

		commitEx();

		//Reverese Look up  GL_Journal_ID From DocumentNo
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "GL_Journal_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "DocumentNo") ;
		sql = new StringBuilder ("UPDATE I_GLJournalJP i ")
				.append("SET GL_Journal_ID=(SELECT GL_Journal_ID FROM GL_Journal p")
				.append(" WHERE i.DocumentNo=p.DocumentNo AND p.AD_Client_ID=i.AD_Client_ID) ")
				.append(" WHERE i.GL_Journal_ID IS NULL AND i.DocumentNo IS NOT NULL AND i.JP_DataMigration_Identifier IS NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + e.toString() + sql );
		}

		commitEx();

	}

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

		//Reverese Look up AD_Org ID From JP_Org_Value
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "AD_Org_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "JP_Org_Value") ;
		sql = new StringBuilder ("UPDATE I_GLJournalJP i ")
				.append("SET AD_Org_ID=(SELECT AD_Org_ID FROM AD_Org p")
				.append(" WHERE i.JP_Org_Value=p.Value AND (p.AD_Client_ID=i.AD_Client_ID or p.AD_Client_ID=0) AND p.IsSummary='N' ) ")
				.append(" WHERE i.JP_Org_Value IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no + ":" + sql);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + sql );
		}

		//Invalid JP_Org_Value
		msg = Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_Org_Value");
		sql = new StringBuilder ("UPDATE I_GLJournalJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append(" WHERE AD_Org_ID = 0 AND JP_Org_Value IS NOT NULL AND JP_Org_Value <> '0' ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no );
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		commitEx();

		if(no > 0)
		{
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

		//Reverese Look up AD_OrgTrx ID From JP_OrgTrx_Value
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "AD_OrgTrx_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "JP_OrgTrx_Value") ;
		sql = new StringBuilder ("UPDATE I_GLJournalJP i ")
				.append("SET AD_OrgTrx_ID=(SELECT AD_Org_ID FROM AD_Org p")
				.append(" WHERE i.JP_OrgTrx_Value=p.Value AND (p.AD_Client_ID=i.AD_Client_ID or p.AD_Client_ID=0) AND p.IsSummary='N' ) ")
				.append(" WHERE i.JP_OrgTrx_Value IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no + ":" + sql);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + sql );
		}

		//Invalid JP_Org_Value
		msg = Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_OrgTrx_Value");
		sql = new StringBuilder ("UPDATE I_GLJournalJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append(" WHERE AD_OrgTrx_ID IS NULL AND JP_OrgTrx_Value IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no );
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		commitEx();

		if(no > 0)
		{
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg );
		}

	}//reverseLookupAD_OrgTrx_ID

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

		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_AcctSchema_ID");
		if (processMonitor != null)	processMonitor.statusUpdate(msg);

		//Reverse look Up  C_AcctSchema_ID From JP_AcctSchema_Name
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_AcctSchema_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "JP_AcctSchema_Name") ;
		sql = new StringBuilder ("UPDATE I_GLJournalJP i ")
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
		sql = new StringBuilder ("UPDATE I_GLJournalJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append(" WHERE C_AcctSchema_ID IS NULL AND JP_AcctSchema_Name IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no + ":" + sql);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		commitEx();

		if(no > 0)
		{
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg );
		}

	}


	/**
	 * Reverse lookup GL_Budget_ID From JP_GL_Budget_Name
	 *
	 * @throws Exception
	 */
	private void reverseLookupGL_Budget_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		String msg = new String();
		int no = 0;

		 msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "GL_Budget_ID");
		if (processMonitor != null)	processMonitor.statusUpdate(msg);

		//Reverse lookup GL_Budget_ID From JP_GL_Budget_Name
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "GL_Budget_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "JP_GL_Budget_Name") ;
		sql = new StringBuilder ("UPDATE I_GLJournalJP i ")
			.append("SET GL_Budget_ID=(SELECT GL_Budget_ID FROM GL_Budget p")
			.append(" WHERE i.JP_GL_Budget_Name=p.Name AND i.AD_Client_ID=p.AD_Client_ID) ")
			.append("WHERE i.GL_Budget_ID IS NULL AND i.JP_GL_Budget_Name IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine( msg + "=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		//Invalid JP_GL_Budget_Value
		msg = Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_GL_Budget_Name");
		sql = new StringBuilder ("UPDATE I_GLJournalJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append("WHERE GL_Budget_ID IS NULL AND JP_GL_Budget_Name IS NOT NULL")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		commitEx();

		if(no > 0)
		{
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg + " : " + no );
		}

	}//reverseLookupGL_Budget_ID


	/**
	 * Reverse lookup C_DocType_ID From JP_DocType_Name
	 *
	 * @throws Exception
	 */
	private void reverseLookupC_DocType_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		String msg = new String();
		int no = 0;

		 msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_DocType_ID");
		if (processMonitor != null)	processMonitor.statusUpdate(msg);

		//Reverse lookup C_DocType_ID From JP_DocType_Name
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_DocType_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "JP_DocType_Name") ;
		sql = new StringBuilder ("UPDATE I_GLJournalJP i ")
			.append("SET C_DocType_ID=(SELECT C_DocType_ID FROM C_DocType p")
			.append(" WHERE i.JP_DocType_Name=p.Name AND i.AD_Client_ID=p.AD_Client_ID) ")
			.append("WHERE i.C_DocType_ID IS NULL AND i.JP_DocType_Name IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine( msg + "=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		//Invalid JP_DocType_Name
		msg = Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_DocType_Name");
		sql = new StringBuilder ("UPDATE I_GLJournalJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append("WHERE C_DocType_ID IS NULL AND JP_DocType_Name IS NOT NULL")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		commitEx();

		if(no > 0)
		{
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg + " : " + no );
		}

	}//reverseLookupC_DocType_ID


	/**
	 * Reverse lookup GL_Category_ID From JP_GL_Category_Name
	 *
	 * @throws Exception
	 */
	private void reverseLookupGL_Category_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		String msg = new String();
		int no = 0;

		 msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "GL_Category_ID");
		if (processMonitor != null)	processMonitor.statusUpdate(msg);

		//Reverse lookup GL_Category_ID From JP_GL_Category_Name
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "GL_Category_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "JP_GL_Category_Name") ;
		sql = new StringBuilder ("UPDATE I_GLJournalJP i ")
			.append("SET GL_Category_ID=(SELECT GL_Category_ID FROM GL_Category p")
			.append(" WHERE i.JP_GL_Category_Name=p.Name AND i.AD_Client_ID=p.AD_Client_ID) ")
			.append("WHERE i.GL_Category_ID IS NULL AND i.JP_GL_Category_Name IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine( msg + "=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		//Invalid JP_GL_Category_Name
		msg = Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_GL_Category_Name");
		sql = new StringBuilder ("UPDATE I_GLJournalJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append("WHERE GL_Category_ID IS NULL AND JP_GL_Category_Name IS NOT NULL")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		commitEx();

		if(no > 0)
		{
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg + " : " + no );
		}

	}//reverseLookupGL_Category_ID

	/**
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
		sql = new StringBuilder ("UPDATE I_GLJournalJP i ")
				.append("SET C_Currency_ID=(SELECT C_Currency_ID FROM C_Currency p")
				.append(" WHERE i.ISO_Code=p.ISO_Code AND (p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0) ) ")
				.append(" WHERE i.C_Currency_ID IS NULL AND ISO_Code IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no + ":" + sql);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg + e.toString() + " : " + sql );
		}

		//Invalid ISO_Code
		msg = Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "ISO_Code");
		sql = new StringBuilder ("UPDATE I_GLJournalJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append(" WHERE C_Currency_ID IS NULL AND ISO_Code IS NOT NULL")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg + e.toString() + " : " + sql );
		}

		commitEx();

		if(no > 0)
		{
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg );
		}

	}//reverseLookupC_Currency_ID

	/**
	 *
	 *
	 * @throws Exception
	 */
	private void reverseLookupC_ConversionType_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		String msg = new String();
		int no = 0;

		//Reverese Look up C_ConversionType_ID From JP_ConversionType_Value
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_ConversionType_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "JP_ConversionType_Value") ;
		sql = new StringBuilder ("UPDATE I_GLJournalJP i ")
				.append("SET C_ConversionType_ID=(SELECT C_ConversionType_ID FROM C_ConversionType p")
				.append(" WHERE i.JP_ConversionType_Value=p.Value AND (p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0) ) ")
				.append(" WHERE i.C_ConversionType_ID IS NULL AND JP_ConversionType_Value IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no + ":" + sql);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg + e.toString() + " : " + sql );
		}

		//Invalid ISO_Code
		msg = Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_ConversionType_Value");
		sql = new StringBuilder ("UPDATE I_GLJournalJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append(" WHERE C_ConversionType_ID IS NULL AND JP_ConversionType_Value IS NOT NULL")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg + e.toString() + " : " + sql );
		}

		commitEx();

		if(no > 0)
		{
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg );
		}

	}//reverseLookupC_Currency_ID


	/**
	 * Reverse lookup Account_ID From JP_ElementValue_Value
	 *
	 * @throws Exception
	 */
	private void reverseLookupAccount_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		String msg = new String();
		int no = 0;

		 msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "Account_ID");
		if (processMonitor != null)	processMonitor.statusUpdate(msg);

		//Reverse lookup Account_ID From JP_ElementValue_Value
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "Account_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "JP_ElementValue_Value") ;
		sql = new StringBuilder ("UPDATE I_GLJournalJP i ")
			.append("SET Account_ID=(SELECT C_ElementValue_ID FROM C_ElementValue p")
			.append(" WHERE i.JP_ElementValue_Value=p.Value AND i.AD_Client_ID=p.AD_Client_ID) ")
			.append("WHERE i.Account_ID IS NULL AND i.JP_ElementValue_Value IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine( msg + "=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		//Invalid JP_ElementValue_Value
		msg = Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_ElementValue_Value");
		sql = new StringBuilder ("UPDATE I_GLJournalJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append("WHERE Account_ID IS NULL AND JP_ElementValue_Value IS NOT NULL")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		commitEx();

		if(no > 0)
		{
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg + " : " + no );
		}

	}//reverseLookupAccount_ID


	/**
	 * Reverse lookup C_SubAcct_ID From JP_SubAcct_Value
	 *
	 * @throws Exception
	 */
	private void reverseLookupC_SubAcct_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		String msg = new String();
		int no = 0;

		 msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_SubAcct_ID");
		if (processMonitor != null)	processMonitor.statusUpdate(msg);

		//Reverse lookup C_SubAcct_ID From JP_SubAcct_Value
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_SubAcct_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "JP_SubAcct_Value") ;
		sql = new StringBuilder ("UPDATE I_GLJournalJP i ")
			.append("SET C_SubAcct_ID=(SELECT C_SubAcct_ID FROM C_SubAcct p")
			.append(" WHERE i.JP_SubAcct_Value=p.Value AND i.AD_Client_ID=p.AD_Client_ID) ")
			.append("WHERE i.C_SubAcct_ID IS NULL AND i.JP_SubAcct_Value IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine( msg + "=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		//Invalid JP_SubAcct_Value
		msg = Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_SubAcct_Value");
		sql = new StringBuilder ("UPDATE I_GLJournalJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append("WHERE C_SubAcct_ID IS NULL AND JP_SubAcct_Value IS NOT NULL")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		commitEx();

		if(no > 0)
		{
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg + " : " + no );
		}

	}//reverseLookupC_SubAcct_ID


	/**
	 * Look up C_UOM_ID From X12DE355
	 *
	 * @throws Exception
	 */
	private void reverseLookupC_UOM_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		String msg = new String();
		int no = 0;

		//Look up C_UOM_ID From X12DE355
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_UOM_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "X12DE355") ;
		sql = new StringBuilder ("UPDATE I_GLJournalJP i ")
				.append("SET C_UOM_ID=(SELECT C_UOM_ID FROM C_UOM p")
				.append(" WHERE i.X12DE355=p.X12DE355 AND (i.AD_Client_ID=p.AD_Client_ID OR p.AD_Client_ID = 0) ) ")
				.append("WHERE X12DE355 IS NOT NULL")
				.append(" AND I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no + ":" + sql);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		//Invalid X12DE355
		msg = Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "X12DE355");
		sql = new StringBuilder ("UPDATE I_GLJournalJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append(" WHERE X12DE355 IS NOT NULL AND C_UOM_ID IS NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no + ":" + sql);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		commitEx();

		if(no > 0)
		{
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg );
		}

	}//reverseLookupC_UOM_ID


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
		sql = new StringBuilder ("UPDATE I_GLJournalJP i ")
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
		sql = new StringBuilder ("UPDATE I_GLJournalJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append("WHERE C_BPartner_ID IS NULL AND JP_BPartner_Value IS NOT NULL")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		commitEx();

		if(no > 0)
		{
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg + " : " + no );
		}

	}//reverseLookupC_BPartner_ID


	/**
	 * reverseLookupM_Product_ID
	 *
	 * @throws Exception
	 */
	private void reverseLookupM_Product_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		String msg = new String();
		int no = 0;

		 msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "M_Product_ID");
		if (processMonitor != null)	processMonitor.statusUpdate(msg);

		//Reverse lookup M_Product_ID From JP_Product_Value
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "M_Product_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "JP_Product_Value") ;
		sql = new StringBuilder ("UPDATE I_GLJournalJP i ")
			.append("SET M_Product_ID=(SELECT M_Product_ID FROM M_Product p")
			.append(" WHERE i.JP_Product_Value=p.Value AND i.AD_Client_ID=p.AD_Client_ID) ")
			.append("WHERE i.M_Product_ID IS NULL AND i.JP_Product_Value IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine( msg + "=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		//Invalid JP_Product_Value
		msg = Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_Product_Value");
		sql = new StringBuilder ("UPDATE I_GLJournalJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append("WHERE M_Product_ID IS NULL AND JP_Product_Value IS NOT NULL")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		commitEx();

		if(no > 0)
		{
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg + " : " + no );
		}

	}//reverseLookupM_Product_ID

	/**
	 * Reverse lookup C_Project_ID From JP_Project_Value
	 *
	 * @throws Exception
	 */
	private void reverseLookupC_Project_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		String msg = new String();
		int no = 0;

		 msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_Project_ID");
		if (processMonitor != null)	processMonitor.statusUpdate(msg);

		//Reverse lookup C_Project_ID From JP_Project_Value
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_Project_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "JP_Project_Value") ;
		sql = new StringBuilder ("UPDATE I_GLJournalJP i ")
			.append("SET C_Project_ID=(SELECT C_Project_ID FROM C_Project p")
			.append(" WHERE i.JP_Project_Value=p.Value AND i.AD_Client_ID=p.AD_Client_ID) ")
			.append("WHERE i.C_Project_ID IS NULL AND i.JP_Project_Value IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine( msg + "=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		//Invalid JP_Product_Value
		msg = Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_Project_Value");
		sql = new StringBuilder ("UPDATE I_GLJournalJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append("WHERE C_Project_ID IS NULL AND JP_Project_Value IS NOT NULL")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		commitEx();

		if(no > 0)
		{
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg + " : " + no );
		}

	}//reverseLookupC_Project_ID


	/**
	 *
	 * Reverse lookup C_ProjectPhase_ID From JP_ProjectPhase_Value
	 *
	 *
	 * @throws Exception
	 */
	private void reverseLookupC_ProjectPhase_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		String msg = new String();
		int no = 0;

		 msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_ProjectPhase_ID");
		if (processMonitor != null)	processMonitor.statusUpdate(msg);

		//Reverse lookup C_ProjectPhase_ID From JP_ProjectPhase_Value
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_ProjectPhase_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "JP_ProjectPhase_Name") ;
		sql = new StringBuilder ("UPDATE I_GLJournalJP i ")
			.append("SET C_ProjectPhase_ID=(SELECT C_ProjectPhase_ID FROM C_ProjectPhase p")
			.append(" WHERE i.JP_ProjectPhase_Name=p.Name AND i.C_Project_ID=p.C_Project_ID ) ")
			.append("WHERE i.C_ProjectPhase_ID IS NULL AND i.JP_ProjectPhase_Name IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine( msg + "=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		//Invalid JP_ProjectPhase_Name
		msg = Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_ProjectPhase_Name");
		sql = new StringBuilder ("UPDATE I_GLJournalJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append("WHERE C_ProjectPhase_ID IS NULL AND JP_ProjectPhase_Name IS NOT NULL")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		commitEx();

		if(no > 0)
		{
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg + " : " + no );
		}


	}//reverseLookupC_ProjectPhase_ID


	/**
	 *
	 * Reverse lookup C_ProjectTask_ID From JP_ProjectTask_Name
	 *
	 * @throws Exception
	 */
	private void reverseLookupC_ProjectTask_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		String msg = new String();
		int no = 0;

		 msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_ProjectTask_ID");
		if (processMonitor != null)	processMonitor.statusUpdate(msg);

		//Reverse lookup C_ProjectTask_ID From JP_ProjectTask_Name
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_ProjectTask_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "JP_ProjectTask_Name") ;
		sql = new StringBuilder ("UPDATE I_GLJournalJP i ")
			.append("SET C_ProjectTask_ID=(SELECT C_ProjectTask_ID FROM C_ProjectTask p")
			.append(" WHERE i.JP_ProjectTask_Name=p.Name AND i.C_ProjectPhase_ID=p.C_ProjectPhase_ID ) ")
			.append("WHERE i.C_ProjectTask_ID IS NULL AND i.JP_ProjectTask_Name IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine( msg + "=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		//Invalid JP_ProjectTask_Name
		msg = Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_ProjectTask_Name");
		sql = new StringBuilder ("UPDATE I_GLJournalJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append("WHERE C_ProjectTask_ID IS NULL AND JP_ProjectTask_Name IS NOT NULL")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		commitEx();

		if(no > 0)
		{
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg + " : " + no );
		}


	}//reverseLookupC_ProjectTask_ID


	/**
	 *
	 * Reverse lookup C_SalesRegion_ID From JP_SalesRegion_Value
	 *
	 * @throws Exception
	 */
	private void reverseLookupC_SalesRegion_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		String msg = new String();
		int no = 0;

		 msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_SalesRegion_ID");
		if (processMonitor != null)	processMonitor.statusUpdate(msg);

		//Reverse lookup C_SalesRegion_ID From JP_SalesRegion_Value
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_SalesRegion_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "JP_SalesRegion_Value") ;
		sql = new StringBuilder ("UPDATE I_GLJournalJP i ")
			.append("SET C_SalesRegion_ID=(SELECT C_SalesRegion_ID FROM C_SalesRegion p")
			.append(" WHERE i.JP_SalesRegion_Value=p.Value AND i.AD_Client_ID=p.AD_Client_ID) ")
			.append("WHERE i.C_SalesRegion_ID IS NULL AND i.JP_SalesRegion_Value IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine( msg + "=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		//Invalid JP_SalesRegion_Value
		msg = Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_SalesRegion_Value");
		sql = new StringBuilder ("UPDATE I_GLJournalJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append("WHERE C_SalesRegion_ID IS NULL AND JP_SalesRegion_Value IS NOT NULL")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		commitEx();

		if(no > 0)
		{
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg + " : " + no );
		}

	}//reverseLookupC_SalesRegion_ID


	/**
	 * Reverse lookup C_Campaign_ID From JP_Campaign_Value
	 *
	 * @throws Exception
	 */
	private void reverseLookupC_Campaign_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		String msg = new String();
		int no = 0;

		 msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_Campaign_ID");
		if (processMonitor != null)	processMonitor.statusUpdate(msg);

		//Reverse lookup C_Campaign_ID From JP_Campaign_Value
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_Campaign_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "JP_Campaign_Value") ;
		sql = new StringBuilder ("UPDATE I_GLJournalJP i ")
			.append("SET C_Campaign_ID=(SELECT C_Campaign_ID FROM C_Campaign p")
			.append(" WHERE i.JP_Campaign_Value=p.Value AND i.AD_Client_ID=p.AD_Client_ID) ")
			.append("WHERE i.C_Campaign_ID IS NULL AND i.JP_Campaign_Value IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine( msg + "=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		//Invalid JP_Campaign_Value
		msg = Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_Campaign_Value");
		sql = new StringBuilder ("UPDATE I_GLJournalJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append("WHERE C_Campaign_ID IS NULL AND JP_Campaign_Value IS NOT NULL")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		commitEx();

		if(no > 0)
		{
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg + " : " + no );
		}

	}//reverseLookupC_Campaign_ID


	/**
	 * Reverse lookup C_Activity_ID From JP_Activity_Value
	 *
	 * @throws Exception
	 */
	private void reverseLookupC_Activity_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		String msg = new String();
		int no = 0;

		 msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_Activity_ID");
		if (processMonitor != null)	processMonitor.statusUpdate(msg);

		//Reverse lookup C_Activity_ID From JP_Activity_Value
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_Activity_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "JP_Activity_Value") ;
		sql = new StringBuilder ("UPDATE I_GLJournalJP i ")
			.append("SET C_Activity_ID=(SELECT C_Activity_ID FROM C_Activity p")
			.append(" WHERE i.JP_Activity_Value=p.Value AND i.AD_Client_ID=p.AD_Client_ID) ")
			.append("WHERE i.C_Activity_ID IS NULL AND i.JP_Activity_Value IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine( msg + "=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		//Invalid JP_Activity_Value
		msg = Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_Activity_Value");
		sql = new StringBuilder ("UPDATE I_GLJournalJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append("WHERE C_Activity_ID IS NULL AND JP_Activity_Value IS NOT NULL")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		commitEx();

		if(no > 0)
		{
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg + " : " + no );
		}

	}//reverseLookupC_Activity_ID


	/**
	 * Reverse Loog up C_LocFrom_ID From JP_LocFrom_Label
	 *
	 *
	 * @throws Exception
	 */
	private void reverseLookupC_LocFrom_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		String msg = new String();
		int no = 0;

		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_LocFrom_ID");
		if (processMonitor != null)	processMonitor.statusUpdate(msg);


		//Reverse Loog up C_LocFrom_ID From JP_LocFrom_Label
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_LocFrom_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "JP_LocFrom_Label") ;
		sql = new StringBuilder ("UPDATE I_GLJournalJP i ")
				.append("SET C_LocFrom_ID=(SELECT C_Location_ID FROM C_Location p")
				.append(" WHERE i.JP_LocFrom_Label= p.JP_Location_Label AND p.AD_Client_ID=i.AD_Client_ID) ")
				.append(" WHERE i.C_LocFrom_ID IS NULL AND JP_LocFrom_Label IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no + ":" + sql);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + sql );
		}

		//Invalid JP_LocFrom_Label
		msg = Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_LocFrom_Label");
		sql = new StringBuilder ("UPDATE I_GLJournalJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append("WHERE C_LocFrom_ID IS NULL AND JP_LocFrom_Label IS NOT NULL")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		commitEx();

		if(no > 0)
		{
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg + " : " + no );
		}
	}//reverseLookupC_LocFrom_ID

	/**
	 *
	 * Reverse Loog up C_LocTo_ID From JP_LocTo_Label
	 *
	 * @throws Exception
	 */
	private void reverseLookupC_LocTo_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		String msg = new String();
		int no = 0;

		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_LocTo_ID");
		if (processMonitor != null)	processMonitor.statusUpdate(msg);


		//Reverse Loog up C_LocTo_ID From JP_LocTo_Label
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_LocTo_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "JP_LocTo_Label") ;
		sql = new StringBuilder ("UPDATE I_GLJournalJP i ")
				.append("SET C_LocTo_ID=(SELECT C_Location_ID FROM C_Location p")
				.append(" WHERE i.JP_LocTo_Label= p.JP_Location_Label AND p.AD_Client_ID=i.AD_Client_ID) ")
				.append(" WHERE i.C_LocTo_ID IS NULL AND JP_LocTo_Label IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no + ":" + sql);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + sql );
		}

		//Invalid JP_LocTo_Label
		msg = Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_LocTo_Label");
		sql = new StringBuilder ("UPDATE I_GLJournalJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append("WHERE C_LocTo_ID IS NULL AND JP_LocTo_Label IS NOT NULL")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		commitEx();

		if(no > 0)
		{
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg + " : " + no );
		}
	}//reverseLookupC_LocTo_ID


	/**
	 * Reverse lookup User1_ID From JP_UserElement1_Value
	 *
	 * @throws Exception
	 */
	private void reverseLookupUser1_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		String msg = new String();
		int no = 0;

		 msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "User1_ID");
		if (processMonitor != null)	processMonitor.statusUpdate(msg);

		//Reverse lookup User1_ID From JP_UserElement1_Value
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "User1_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "JP_UserElement1_Value") ;
		sql = new StringBuilder ("UPDATE I_GLJournalJP i ")
			.append("SET User1_ID=(SELECT C_ElementValue_ID FROM C_ElementValue p")
			.append(" WHERE i.JP_UserElement1_Value=p.Value AND i.AD_Client_ID=p.AD_Client_ID) ")
			.append("WHERE i.User1_ID IS NULL AND i.JP_UserElement1_Value IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine( msg + "=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		//Invalid JP_UserElement1_Value
		msg = Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_UserElement1_Value");
		sql = new StringBuilder ("UPDATE I_GLJournalJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append("WHERE User1_ID IS NULL AND JP_UserElement1_Value IS NOT NULL")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		commitEx();

		if(no > 0)
		{
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg + " : " + no );
		}

	}//reverseLookupUser1_ID


	/**
	 * Reverse lookup User2_ID From JP_UserElement2_Value
	 *
	 * @throws Exception
	 */
	private void reverseLookupUser2_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		String msg = new String();
		int no = 0;

		 msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "User2_ID");
		if (processMonitor != null)	processMonitor.statusUpdate(msg);

		//Reverse lookup User2_ID From JP_UserElement2_Value
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "User2_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "JP_UserElement2_Value") ;
		sql = new StringBuilder ("UPDATE I_GLJournalJP i ")
			.append("SET User2_ID=(SELECT C_ElementValue_ID FROM C_ElementValue p")
			.append(" WHERE i.JP_UserElement2_Value=p.Value AND i.AD_Client_ID=p.AD_Client_ID) ")
			.append("WHERE i.User2_ID IS NULL AND i.JP_UserElement2_Value IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine( msg + "=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		//Invalid JP_UserElement2_Value
		msg = Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_UserElement2_Value");
		sql = new StringBuilder ("UPDATE I_GLJournalJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append("WHERE User2_ID IS NULL AND JP_UserElement2_Value IS NOT NULL")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		commitEx();

		if(no > 0)
		{
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg + " : " + no );
		}

	}//reverseLookupUser2_ID


	/**
	 * Reverse lookup A_Asset_ID From JP_Asset_Value
	 *
	 * @throws Exception
	 */
	private void reverseLookupA_Asset_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		String msg = new String();
		int no = 0;

		 msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "A_Asset_ID");
		if (processMonitor != null)	processMonitor.statusUpdate(msg);

		//Reverse lookup A_Asset_ID From JP_Asset_Value
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "A_Asset_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "JP_Asset_Value") ;
		sql = new StringBuilder ("UPDATE I_GLJournalJP i ")
			.append("SET A_Asset_ID=(SELECT A_Asset_ID FROM A_Asset p")
			.append(" WHERE i.JP_Asset_Value=p.Value AND i.AD_Client_ID=p.AD_Client_ID) ")
			.append("WHERE i.A_Asset_ID IS NULL AND i.JP_Asset_Value IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine( msg + "=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		//Invalid JP_Asset_Value
		msg = Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_Asset_Value");
		sql = new StringBuilder ("UPDATE I_GLJournalJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append("WHERE A_Asset_ID IS NULL AND JP_Asset_Value IS NOT NULL")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		commitEx();

		if(no > 0)
		{
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg + " : " + no );
		}

	}//reverseLookupA_Asset_ID

	/**
	 * Reverse lookup M_Locator_ID From JP_Locator_Value
	 *
	 * @throws Exception
	 */
	private void reverseLookupM_Locator_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		String msg = new String();
		int no = 0;

		 msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "M_Locator_ID");
		if (processMonitor != null)	processMonitor.statusUpdate(msg);

		//Reverse lookup M_Locator_ID From JP_Locator_Value
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "M_Locator_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "JP_Locator_Value") ;
		sql = new StringBuilder ("UPDATE I_GLJournalJP i ")
			.append("SET M_Locator_ID=(SELECT M_Locator_ID FROM M_Locator p")
			.append(" WHERE i.JP_Locator_Value=p.Value AND i.AD_Client_ID=p.AD_Client_ID) ")
			.append("WHERE i.M_Locator_ID IS NULL AND i.JP_Locator_Value IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine( msg + "=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		//Invalid JP_Locator_Value
		msg = Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_Locator_Value");
		sql = new StringBuilder ("UPDATE I_GLJournalJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append("WHERE M_Locator_ID IS NULL AND JP_Locator_Value IS NOT NULL")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		commitEx();

		if(no > 0)
		{
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg + " : " + no );
		}

	}//reverseLookupM_Locator_ID




	/**
	 * Create Journal
	 *
	 * @param impCharge
	 * @param newCharge
	 * @return
	 */
	private boolean createHeaderJournal(X_I_GLJournalJP impJournal, MJournal newJournal)
	{
		//Check AD_Org_ID
		if(impJournal.getAD_Org_ID() <= 0)
		{
			impJournal.setI_ErrorMsg(Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_Org_Value"));
			impJournal.setI_IsImported(false);
			impJournal.setProcessed(false);
			impJournal.saveEx(get_TrxName());
			return false;
		}

		//Check C_AcctSchema_ID
		if(impJournal.getC_AcctSchema_ID() <= 0)
		{
			impJournal.setI_ErrorMsg(Msg.getMsg(getCtx(), "JP_Null")+Msg.getElement(getCtx(), "C_AcctSchema_ID"));
			impJournal.setI_IsImported(false);
			impJournal.setProcessed(false);
			impJournal.saveEx(get_TrxName());
			return false;
		}

		//Check C_DocType_ID
		if(impJournal.getC_DocType_ID() <= 0)
		{
			impJournal.setI_ErrorMsg(Msg.getMsg(getCtx(), "JP_Null")+Msg.getElement(getCtx(), "C_DocType_ID"));
			impJournal.setI_IsImported(false);
			impJournal.setProcessed(false);
			impJournal.saveEx(get_TrxName());
			return false;
		}

		//Check PostingType
		if(Util.isEmpty(impJournal.getPostingType()))
		{
			impJournal.setI_ErrorMsg(Msg.getMsg(getCtx(), "JP_Null")+Msg.getElement(getCtx(), "PostingType"));
			impJournal.setI_IsImported(false);
			impJournal.setProcessed(false);
			impJournal.saveEx(get_TrxName());
			return false;
		}

		if(impJournal.getDateAcct() == null)
		{
			impJournal.setI_ErrorMsg(Msg.getMsg(getCtx(), "JP_Null")+Msg.getElement(getCtx(), "DateAcct"));
			impJournal.setI_IsImported(false);
			impJournal.setProcessed(false);
			impJournal.saveEx(get_TrxName());
			return false;
		}

		MCalendar baseCalendar = MCalendar.getDefault(getCtx(), m_AD_Client_ID);
		MPeriod period = MPeriod.findByCalendar(getCtx(),impJournal.getDateAcct(),baseCalendar.getC_Calendar_ID(), get_TrxName());

		if(impJournal.getDateTrx() == null)
		{
			impJournal.setDateTrx(impJournal.getDateAcct());
		}

		//Check Mandatory - Currency
		if(impJournal.getC_Currency_ID() <= 0)
		{
			impJournal.setI_ErrorMsg(Msg.getMsg(getCtx(), "JP_Null")+Msg.getElement(getCtx(), "C_Currency_ID"));
			impJournal.setI_IsImported(false);
			impJournal.setProcessed(false);
			impJournal.saveEx(get_TrxName());
			return false;
		}

		//Check Mandatory - Description
		if(Util.isEmpty(impJournal.getJP_Description_Header()))
		{
			if(!Util.isEmpty(impJournal.getJP_Description_Line()))
			{
				impJournal.setJP_Description_Header(impJournal.getJP_Description_Line());

			}else {

				Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_Description_Header")};
				impJournal.setI_ErrorMsg(Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs));
				impJournal.setI_IsImported(false);
				impJournal.setProcessed(false);
				impJournal.saveEx(get_TrxName());
				return false;

			}
		}

		ModelValidationEngine.get().fireImportValidate(this, impJournal, newJournal, ImportValidator.TIMING_BEFORE_IMPORT);

		PO.copyValues(impJournal, newJournal);
		newJournal.setAD_Org_ID(impJournal.getAD_Org_ID());
		newJournal.setC_Period_ID(period.getC_Period_ID());

		newJournal.setDescription(impJournal.getJP_Description_Header());

		ModelValidationEngine.get().fireImportValidate(this, impJournal, newJournal, ImportValidator.TIMING_AFTER_IMPORT);

		try {
			newJournal.saveEx(get_TrxName());
		}catch (Exception e) {
			impJournal.setI_ErrorMsg(Msg.getMsg(getCtx(),"SaveIgnored") + Msg.getElement(getCtx(), "GL_Journal_ID") +" : " + e.toString());
			impJournal.setI_IsImported(false);
			impJournal.setProcessed(false);
			impJournal.saveEx(get_TrxName());
			return false;
		}

		impJournal.setGL_Journal_ID(newJournal.getGL_Journal_ID());

		return true;
	}

	/**
	 * add JournalLine
	 *
	 * @param impCharge
	 * @param updateCharge
	 * @return
	 */
	private boolean addJournalLine(X_I_GLJournalJP impJournal, MJournal journal, MJournalLine journalLine)
	{

		ModelValidationEngine.get().fireImportValidate(this, impJournal, journal, ImportValidator.TIMING_BEFORE_IMPORT);

		PO.copyValues(impJournal, journalLine);
		journalLine.setGL_Journal_ID(journal.getGL_Journal_ID());
		if(impJournal.getGL_Journal_ID()==0)
			impJournal.setGL_Journal_ID(journal.getGL_Journal_ID());
		if(!Util.isEmpty(impJournal.getJP_Description_Line()))
			journalLine.setDescription(impJournal.getJP_Description_Line());

		int C_ValidCombination_ID = JPiereValidCombinationUtil.searchCreateValidCombination (getCtx(), impJournal.getC_AcctSchema_ID()
				, impJournal.getJP_ElementValue_Value(), get_TrxName());
		journalLine.setC_ValidCombination_ID(C_ValidCombination_ID);

		ModelValidationEngine.get().fireImportValidate(this, impJournal, journal, ImportValidator.TIMING_AFTER_IMPORT);

		try {
			journalLine.saveEx(get_TrxName());
		}catch (Exception e) {

			rollback();//Roll Back from Header.

			impJournal.setI_ErrorMsg(Msg.getMsg(getCtx(),"SaveIgnored") + Msg.getElement(getCtx(), "GL_JournalLine_ID") +" : " + e.toString());
			impJournal.setI_IsImported(false);
			impJournal.setProcessed(false);
			impJournal.saveEx(get_TrxName());
			return false;
		}

		impJournal.setGL_JournalLine_ID(journalLine.getGL_JournalLine_ID());

		impJournal.setI_IsImported(true);
		impJournal.setProcessed(true);
		impJournal.saveEx(get_TrxName());

		return true;
	}

}	//	Import Warehouse
