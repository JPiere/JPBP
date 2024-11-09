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
import java.util.ArrayList;
import java.util.logging.Level;

import org.adempiere.model.ImportValidator;
import org.adempiere.process.ImportProcess;
import org.adempiere.util.IProcessUI;
import org.compiere.model.MColumn;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.PO;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.MBankData;
import jpiere.base.plugin.org.adempiere.model.MBankDataLine;
import jpiere.base.plugin.org.adempiere.model.X_I_BankDataJP;


/**
 *	JPIERE-0595:Import Bank Data from I_BankDataJP
 *
 *
 *  @author Hideaki Hagiwara
 */
public class JPiereImportBankData extends SvrProcess implements ImportProcess{

	/**	Client to be imported to		*/
	private int				p_AD_Client_ID = 0;
	
	private int p_JP_BankDataSchema_ID = 0;
	private int p_JP_Year = 0;
	
	/**	Delete old Imported				*/
	private boolean			p_deleteOldImported = false;
	/**	Only validate, don't import		*/
	private boolean	p_IsValidateOnly = false;
	
	private IProcessUI processMonitor = null;
	
	private String message = null;
	
	private String p_JP_ImportSalesRepIdentifier = null;
	
	private ArrayList<MBankData> list_BankData = new ArrayList<MBankData>();
	
	@Override
	protected void prepare() 
	{
		p_AD_Client_ID = getAD_Client_ID();
		
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (name.equals("JP_BankDataSchema_ID"))
				p_JP_BankDataSchema_ID = para[i].getParameterAsInt();
			else if (name.equals("IsValidateOnly"))
				p_IsValidateOnly = para[i].getParameterAsBoolean();
			else if (name.equals("JP_Year"))
				p_JP_Year = para[i].getParameterAsInt();
			else if (name.equals("JP_ImportSalesRepIdentifier"))
				p_JP_ImportSalesRepIdentifier = para[i].getParameterAsString();
			else if (name.equals("DeleteOldImported"))
				p_deleteOldImported = "Y".equals(para[i].getParameter());
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}

		if(p_JP_Year <= 1970)
		{
			LocalDateTime now = LocalDateTime.now();
			p_JP_Year = now.getYear();
		}
		
	}

	@Override
	protected String doIt() throws Exception 
	{
		processMonitor = Env.getProcessUI(getCtx());

		StringBuilder sql = null;
		int no = 0;

		/** Delete Old Imported */
		if (p_deleteOldImported)
		{
			sql = new StringBuilder ("DELETE FROM I_BankDataJP ")
				  .append("WHERE I_IsImported='Y' ").append (getWhereClause());
			no = DB.executeUpdate(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("Delete Old Impored =" + no);
		}

		/** Reset I_ErrorMsg */
		sql = new StringBuilder ("UPDATE I_BankDataJP ")
				.append(" SET I_ErrorMsg='' ")
				.append(" WHERE I_IsImported<>'Y' ").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(String.valueOf(no));
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + sql );
		}

		
		ModelValidationEngine.get().fireImportValidate(this, null, null, ImportValidator.TIMING_BEFORE_VALIDATE);
		
		
		//AD_Org_ID
		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "AD_Org_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupAD_Org_ID())
			commitEx();
		else
			return message;
		
		
		//AD_OrgTrx_ID
		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "AD_OrgTrx_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupAD_OrgTrx_ID())
			commitEx();
		else
			return message;
		
		
		//C_Bank_ID
		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_Bank_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupC_Bank_ID())
			commitEx();
		else
			return message;
		
		
		//C_BankAccount_ID
		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_BankAccount_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupC_BankAccount_ID())
			commitEx();
		else
			return message;
		
		//Bank Account Type
		//Nothing to do;
		
		//SalesRep_ID
		if(p_JP_ImportSalesRepIdentifier != null)
		{
			message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "SalesRep_ID");
			if(processMonitor != null)	processMonitor.statusUpdate(message);
			if(reverseLookupSalesRep_ID())
				commitEx();
			else
				return message;
		}
		
		
		//Check Month & Date String
		message = Msg.getMsg(getCtx(), "JP_Checking") + " : " + Msg.getMsg(getCtx(), "Date");
		if(checkMonthDateString())
			;
		else
			return message;
		
		
		//StatementDate
		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "StatementDate");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(setStatementDate())
			commitEx();
		else
			return message;
		
		//DateAcct
		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "DateAcct");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(setDateAcct())
			commitEx();
		else
			return message;
		
		//Check StatementDate and DateAcct
		message = Msg.getMsg(getCtx(), "JP_Checking") + " : " + Msg.getMsg(getCtx(), "Date");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(checkDate())
			;
		else
			return message;
		
		
		ModelValidationEngine.get().fireImportValidate(this, null, null, ImportValidator.TIMING_AFTER_VALIDATE);


		commitEx();
		if (p_IsValidateOnly)
		{
			return "Validated";
		}
		
		
		/** Create Bank data */
		message = Msg.getMsg(getCtx(), "CreateNew") + " : " + Msg.getElement(getCtx(), "JP_BankData_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		
		sql = new StringBuilder ("SELECT * FROM I_BankDataJP ")
				  .append("WHERE I_IsImported='N'").append (getWhereClause())
				.append(" ORDER BY C_BankAccount_ID, AD_Org_ID, StatementDate, DateAcct, JP_BankData_ReferenceNo");

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
		
		
		boolean isError = false;
		
		try
		{
			pstmt = DB.prepareStatement (sql.toString(), get_TrxName());
			rs = pstmt.executeQuery ();

			int lastC_BankAccount_ID = 0;
			int lastAD_Org_ID = 0;
			Timestamp lastStatementDate = null;
			Timestamp lastDateAcct = null;
		
			MBankData m_BankData = null;
			MBankDataLine m_line = null;
			X_I_BankDataJP imp = null;
			int lineNo = 0;
			boolean isCreateHeader= true;
			
			while (rs.next ())
			{
				recordsNum++;

				imp = new X_I_BankDataJP (getCtx (), rs, get_TrxName());

				//Re-Import
				if(imp.getJP_BankData_ID() > 0)
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

				//	New BankData
				isCreateHeader= true;
				if (lastC_BankAccount_ID != imp.getC_BankAccount_ID()
					|| lastAD_Org_ID != imp.getAD_Org_ID()
					|| lastStatementDate.compareTo(imp.getStatementDate()) != 0
					|| lastDateAcct.compareTo(imp.getDateAcct()) != 0
					)
				{
					lastC_BankAccount_ID = imp.getC_BankAccount_ID();
					lastAD_Org_ID = imp.getAD_Org_ID();
					lastStatementDate = imp.getStatementDate();
					lastDateAcct = imp.getDateAcct();

				}else {

					isCreateHeader = false;
				}

				if(isCreateHeader)
				{
					m_BankData = new MBankData (getCtx(), 0, get_TrxName());
					lineNo = 0;

					if(createBankData(imp, m_BankData))
					{
						successCreateDocHeader++;
					}else {

						rollback();
						m_BankData = null;

						failureCreateDocHeader++;
						errorNum++;//Error of Header include number of Error.
						imp.setI_ErrorMsg(message);
						imp.setI_IsImported(false);
						imp.setProcessed(false);
						imp.saveEx(get_TrxName());
						commitEx();
						isError = true;
						break;
					}
				}

				if(m_BankData == null)
				{
					rollback();
					errorNum++;
					message = Msg.getMsg(getCtx(), "JP_UnexpectedError");
					imp.setI_ErrorMsg(message);
					imp.setI_IsImported(false);
					imp.setProcessed(false);
					imp.saveEx(get_TrxName());
					commitEx();
					isError = true;
					break;
				}


				imp.setJP_BankData_ID(m_BankData.getJP_BankData_ID());

				//Create BankDataLine
				m_line = new MBankDataLine(getCtx(), 0, get_TrxName());
				lineNo = lineNo + 10;

				if(addBankDataLine(imp, m_BankData, m_line, lineNo))
				{
					successCreateDocLine++;
					successNum++;

				}else {

					rollback();
					m_BankData = null;

					failureCreateDocLine++;
					errorNum++;//Error of Line include number of Error.

					imp.setJP_BankData_ID(0);
					imp.setI_ErrorMsg(message);
					imp.setI_IsImported(false);
					imp.setProcessed(false);
					imp.saveEx(get_TrxName());
					commitEx();
					isError = true;
					break;

				}

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
			}//While
		
		
		}catch (Exception e){

			log.log(Level.SEVERE, sql.toString(), e);
			throw e;

		}finally{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}
		
		if(isError)
			throw new Exception(message);
		
		for(MBankData m_BankData : list_BankData)
		{
			addBufferLog(0, null, null, m_BankData.getName(), MBankData.Table_ID, m_BankData.getJP_BankData_ID());
		}
		
		return Msg.getMsg(getCtx(), "OK");
	}

	@Override
	public String getWhereClause() {
		StringBuilder msgreturn = new StringBuilder(" AND AD_Client_ID=").append(p_AD_Client_ID);
		return msgreturn.toString();
	}


	@Override
	public String getImportTableName() {
		return "I_BankDataJP";
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
		StringBuilder sql = new StringBuilder ("UPDATE I_BankDataJP i ")
				.append("SET AD_Org_ID=(SELECT CASE WHEN MAX(p.AD_Org_ID) > 0 THEN MAX(p.AD_Org_ID) ELSE 0 END  FROM AD_Org p")
				.append(" WHERE (i.JP_Org_Value=p.Value OR p.Value = '0') AND (p.AD_Client_ID=i.AD_Client_ID or p.AD_Client_ID=0) AND p.IsSummary='N') ")
				.append(" WHERE i.JP_Org_Value IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}

		//Invalid JP_Org_Value
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "AD_Org_ID");
		sql = new StringBuilder ("UPDATE I_BankDataJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append(" WHERE AD_Org_ID = 0 AND JP_Org_Value IS NOT NULL ")
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
	 * Reverse Lookup AD_OrgTrx_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupAD_OrgTrx_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_BankDataJP i ")
				.append("SET AD_OrgTrx_ID=(SELECT AD_Org_ID FROM AD_Org p")
				.append(" WHERE i.JP_OrgTrx_Value=p.Value AND (p.AD_Client_ID=i.AD_Client_ID or p.AD_Client_ID=0) AND p.IsSummary='N' ) ")
				.append(" WHERE i.JP_OrgTrx_Value IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message + " : "+ e.toString() + " : "+ sql );
		}

		//Invalid JP_Org_Value
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "JP_OrgTrx_Value");
		sql = new StringBuilder ("UPDATE I_BankDataJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append(" WHERE AD_OrgTrx_ID IS NULL AND JP_OrgTrx_Value IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(message + " : "+ e.toString() + " : "+ sql );
		}

		if(no > 0)
		{
			return false;
		}

		return true;

	}//reverseLookupAD_OrgTrx_ID
	
	
	/**
	 * Reverse Lookup C_Bank_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupC_Bank_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_BankDataJP i ")
				.append("SET C_Bank_ID=(SELECT C_Bank_ID FROM C_Bank p")
				.append(" WHERE i.RoutingNo=p.RoutingNo AND (p.AD_Client_ID=i.AD_Client_ID or p.AD_Client_ID=0) ) ")
				.append(" WHERE i.RoutingNo IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message + " : "+ e.toString() + " : "+ sql );
		}
		
		sql = new StringBuilder ("UPDATE I_BankDataJP i ")
				.append("SET C_Bank_ID=(SELECT C_Bank_ID FROM C_Bank p")
				.append(" WHERE i.JP_Bank_Name=p.Name AND (p.AD_Client_ID=i.AD_Client_ID or p.AD_Client_ID=0) ) ")
				.append(" WHERE i.JP_Bank_Name IS NOT NULL")
				.append(" AND i.I_IsImported='N' AND C_Bank_ID is null ").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message + " : "+ e.toString() + " : "+ sql );
		}		

		sql = new StringBuilder ("UPDATE I_BankDataJP i ")
				.append("SET C_Bank_ID=(SELECT C_Bank_ID FROM C_Bank p")
				.append(" WHERE i.JP_BankName_Kana=p.JP_BankName_Kana AND (p.AD_Client_ID=i.AD_Client_ID or p.AD_Client_ID=0) ) ")
				.append(" WHERE i.JP_BankName_Kana IS NOT NULL")
				.append(" AND i.I_IsImported='N' AND C_Bank_ID is null ").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message + " : "+ e.toString() + " : "+ sql );
		}	
		
		
		
		//Invalid C_Bank_ID
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "C_Bank_ID");
		sql = new StringBuilder ("UPDATE I_BankDataJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append(" WHERE C_Bank_ID IS NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(message + " : "+ e.toString() + " : "+ sql );
		}

		if(no > 0)
		{
			return false;
		}

		return true;

	}//reverseLookupC_Bank_ID
	
	
	/**
	 * Reverse Lookup C_BankAccount_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupC_BankAccount_ID() throws Exception
	{
		int no = 0;

		//Match C_Bank_ID and (JP_BranchCode or JP_BranchName or JP_BranchName_Kana) and AccountNo and BankAccountType
		StringBuilder sql = new StringBuilder ("UPDATE I_BankDataJP i ")
				.append("SET C_BankAccount_ID=(SELECT C_BankAccount_ID FROM C_BankAccount p")
				.append(" WHERE i.C_Bank_ID=p.C_Bank_ID AND (i.JP_BranchCode=p.JP_BranchCode OR i.JP_BranchName=p.JP_BranchName OR i.JP_BranchName_Kana=p.JP_BranchName_Kana) ")
				.append(" AND i.AccountNo = p.AccountNo  AND i.BankAccountType = p.BankAccountType ")
				.append(" AND (p.AD_Client_ID=i.AD_Client_ID or p.AD_Client_ID=0) ) ")
				.append(" WHERE i.C_BankAccount_ID IS NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message + " : "+ e.toString() + " : "+ sql );
		}
		
		//Match C_Bank_ID and (JP_BranchCode or JP_BranchName or JP_BranchName_Kana) and AccountNo
		sql = new StringBuilder ("UPDATE I_BankDataJP i ")
				.append("SET C_BankAccount_ID=(SELECT C_BankAccount_ID FROM C_BankAccount p")
				.append(" WHERE i.C_Bank_ID=p.C_Bank_ID AND (i.JP_BranchCode=p.JP_BranchCode OR i.JP_BranchName=p.JP_BranchName OR i.JP_BranchName_Kana=p.JP_BranchName_Kana) ")
				.append(" AND i.AccountNo = p.AccountNo ")
				.append(" AND (p.AD_Client_ID=i.AD_Client_ID or p.AD_Client_ID=0) ) ")
				.append(" WHERE i.C_BankAccount_ID IS NULL and i.BankAccountType IS NULL ")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message + " : "+ e.toString() + " : "+ sql );
		}
		
		//Match C_Bank_ID and JP_BankAccount_Value
		sql = new StringBuilder ("UPDATE I_BankDataJP i ")
				.append("SET C_BankAccount_ID=(SELECT C_BankAccount_ID FROM C_BankAccount p")
				.append(" WHERE  i.C_Bank_ID=p.C_Bank_ID AND i.JP_BankAccount_Value=p.Value AND (p.AD_Client_ID=i.AD_Client_ID or p.AD_Client_ID=0) ) ")
				.append(" WHERE i.C_BankAccount_ID IS NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message + " : "+ e.toString() + " : "+ sql );
		}
		
		
		//set AD_Org_ID from Bank Account
		sql = new StringBuilder ("UPDATE I_BankDataJP i ")
				.append("SET AD_Org_ID=(SELECT AD_Org_ID FROM C_BankAccount p")
				.append(" WHERE i.C_BankAccount_ID=p.C_BankAccount_ID ) ")
				.append(" WHERE i.C_BankAccount_ID IS NOT NULL AND i.JP_Org_Value IS NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message + " : "+ e.toString() + " : "+ sql );
		}
		
		
		//Invalid C_BankAccount_ID
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "C_BankAccount_ID");
		sql = new StringBuilder ("UPDATE I_BankDataJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append(" WHERE C_BankAccount_ID IS NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(message + " : "+ e.toString() + " : "+ sql );
		}

		if(no > 0)
		{
			return false;
		}

		return true;

	}//reverseLookupC_BankAccount_ID
	
	/**
	 * Reverse Lookup SalesRep_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupSalesRep_ID() throws Exception
	{
		if(Util.isEmpty(p_JP_ImportSalesRepIdentifier) || p_JP_ImportSalesRepIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_NotCollate))
			return true;

		StringBuilder sql = null;

		if(p_JP_ImportSalesRepIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_EMail)) //E-Mail
		{
			sql = new StringBuilder ("UPDATE I_BankDataJP i ")
					.append("SET SalesRep_ID=(SELECT MAX(AD_User_ID) FROM AD_User p INNER JOIN C_BPartner bp ON (p.C_BPartner_ID = bp.C_BPartner_ID) ")
					.append(" WHERE i.JP_SalesRep_EMail=p.EMail AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) AND bp.IsSalesRep='Y' ) ")
					.append(" WHERE i.JP_SalesRep_EMail IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportSalesRepIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_Name)) { //Name

			sql = new StringBuilder ("UPDATE I_BankDataJP i ")
					.append("SET SalesRep_ID=(SELECT MAX(AD_User_ID) FROM AD_User p INNER JOIN C_BPartner bp ON (p.C_BPartner_ID = bp.C_BPartner_ID) ")
					.append(" WHERE i.JP_SalesRep_Name=p.Name AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) AND bp.IsSalesRep='Y') ")
					.append(" WHERE i.JP_SalesRep_Name IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportSalesRepIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_Value)) { //Value

			sql = new StringBuilder ("UPDATE I_BankDataJP i ")
					.append("SET SalesRep_ID=(SELECT MAX(AD_User_ID) FROM AD_User p INNER JOIN C_BPartner bp ON (p.C_BPartner_ID = bp.C_BPartner_ID) ")
					.append(" WHERE i.JP_SalesRep_Value=p.Value AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) AND bp.IsSalesRep='Y'  ) ")
					.append(" WHERE i.JP_SalesRep_Value IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportSalesRepIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueEMail)) { //Value + E-Mail

			//In case of EMail is not null
			sql = new StringBuilder ("UPDATE I_BankDataJP i ")
					.append("SET SalesRep_ID=(SELECT MAX(AD_User_ID) FROM AD_User p INNER JOIN C_BPartner bp ON (p.C_BPartner_ID = bp.C_BPartner_ID) ")
					.append(" WHERE i.JP_SalesRep_Value=p.Value AND i.JP_SalesRep_EMail=p.EMail AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) AND bp.IsSalesRep='Y' ) ")
					.append(" WHERE i.JP_SalesRep_Value IS NOT NULL AND i.JP_SalesRep_EMail IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

			try {
				DB.executeUpdateEx(sql.toString(), get_TrxName());
			}catch(Exception e) {
				message = message + " : " +e.toString()+ " : "+sql.toString();
				return false;
			}

			//In case of EMail is null
			sql = new StringBuilder ("UPDATE I_BankDataJP i ")
					.append("SET SalesRep_ID=(SELECT MAX(AD_User_ID) FROM AD_User p INNER JOIN C_BPartner bp ON (p.C_BPartner_ID = bp.C_BPartner_ID) ")
					.append(" WHERE i.JP_SalesRep_Value=p.Value AND i.JP_SalesRep_EMail IS NULL AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) AND bp.IsSalesRep='Y' ) ")
					.append(" WHERE i.JP_SalesRep_Value IS NOT NULL AND i.JP_SalesRep_EMail IS NULL AND i.SalesRep_ID IS NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());


		}else if(p_JP_ImportSalesRepIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueName)) { //Value + Name

			sql = new StringBuilder ("UPDATE I_BankDataJP i ")
					.append("SET SalesRep_ID=(SELECT MAX(AD_User_ID) FROM AD_User p INNER JOIN C_BPartner bp ON (p.C_BPartner_ID = bp.C_BPartner_ID) ")
					.append(" WHERE i.JP_SalesRep_Value=p.Value AND i.JP_SalesRep_Name=p.Name AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) AND bp.IsSalesRep='Y'  ) ")
					.append(" WHERE i.JP_SalesRep_Value IS NOT NULL AND i.JP_SalesRep_Name IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportSalesRepIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueNameEmail)) { //Value + Name + EMail

			//In case of EMail is not null
			sql = new StringBuilder ("UPDATE I_BankDataJP i ")
					.append("SET SalesRep_ID=(SELECT MAX(AD_User_ID) FROM AD_User p INNER JOIN C_BPartner bp ON (p.C_BPartner_ID = bp.C_BPartner_ID) ")
					.append(" WHERE i.JP_SalesRep_Value=p.Value AND i.JP_SalesRep_Name=p.Name  AND i.JP_SalesRep_EMail=p.EMail AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) AND bp.IsSalesRep='Y'  ) ")
					.append(" WHERE i.JP_SalesRep_Value IS NOT NULL AND i.JP_SalesRep_Name IS NOT NULL AND i.JP_SalesRep_EMail IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

			try {
				DB.executeUpdateEx(sql.toString(), get_TrxName());
			}catch(Exception e) {
				message = message + " : " +e.toString()+ " : "+sql.toString();
				return false;
			}

			//In case of EMail is null
			sql = new StringBuilder ("UPDATE I_BankDataJP i ")
					.append("SET SalesRep_ID=(SELECT MAX(AD_User_ID) FROM AD_User p INNER JOIN C_BPartner bp ON (p.C_BPartner_ID = bp.C_BPartner_ID) ")
					.append(" WHERE i.JP_SalesRep_Value=p.Value AND i.JP_SalesRep_Name=p.Name AND i.JP_SalesRep_EMail IS NULL AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) AND bp.IsSalesRep='Y'  ) ")
					.append(" WHERE i.JP_SalesRep_Value IS NOT NULL AND i.JP_SalesRep_Name IS NOT NULL AND i.JP_SalesRep_EMail IS NULL AND i.SalesRep_ID IS NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportSalesRepIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_NotCollate)){

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

	}
	
	private boolean checkMonthDateString()
	{
		StringBuilder  sql = new StringBuilder ("SELECT * FROM I_BankDataJP ")
  				.append("WHERE I_IsImported='N'").append (getWhereClause());

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		X_I_BankDataJP m_BankDataJP = null;
		try
		{
			pstmt = DB.prepareStatement (sql.toString(), get_TrxName());
			rs = pstmt.executeQuery ();
			
			while (rs.next ())
			{
				m_BankDataJP = new X_I_BankDataJP (getCtx (), rs, get_TrxName());
				if(m_BankDataJP.getStatementDate() == null)
				{
					//Check JP_Month
					if(!checkMonthString(m_BankDataJP.getJP_Month()))
					{
						message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "JP_Month");
						m_BankDataJP.setI_ErrorMsg(message);
						m_BankDataJP.saveEx(get_TrxName());
						return false;
					}
					
					//Check JP_Date
					if(!checkDateString(m_BankDataJP.getJP_Date()))
					{
						message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "JP_Date");
						m_BankDataJP.setI_ErrorMsg(message);
						m_BankDataJP.saveEx(get_TrxName());
						return false;
					}
				}
				
				if(m_BankDataJP.getDateAcct() == null)
				{
					//Check JP_AcctMonth
					if(!checkMonthString(m_BankDataJP.getJP_AcctMonth()))
					{
						message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "JP_AcctMonth");
						m_BankDataJP.setI_ErrorMsg(message);
						m_BankDataJP.saveEx(get_TrxName());
						return false;
					}
					
					//Check JP_AcctDate
					if(!checkDateString(m_BankDataJP.getJP_AcctDate()))
					{
						message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "JP_AcctDate");
						m_BankDataJP.setI_ErrorMsg(message);
						m_BankDataJP.saveEx(get_TrxName());
						return false;
					}
				}
				
			}
		
		}catch (Exception e) {
		
		}

		return true;
	}
	
	private boolean checkMonthString(String month)
	{
		if(Util.isEmpty(month))
			return true;
		
		if(month.length() > 2)
			return false;
		
		int i_month = 0;
		try 
		{
			i_month = Integer.parseInt(month);
		}catch (Exception e) {
			return false;
		}
		
		if(i_month > 12)
			return false;
		
		if(i_month < 1)
			return false;
		
		return true;
	}
	
	private boolean checkDateString(String date)
	{
		if(Util.isEmpty(date))
			return true;
		
		if(date.length() > 2)
			return false;
		
		int i_date = 0;
		try 
		{
			i_date = Integer.parseInt(date);
		}catch (Exception e) {
			return false;
		}
		
		if(i_date > 31)
			return false;
		
		if(i_date < 1)
			return false;
		
		return true;
	}
	
	private boolean checkDate() throws Exception
	{
		int no = 0;
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "FillMandatory") + Msg.getElement(getCtx(), "StatementDate") + " OR " + Msg.getElement(getCtx(), "DateAcct") ;
		StringBuilder sql = new StringBuilder ("UPDATE I_BankDataJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append(" WHERE StatementDate IS NULL AND  DateAcct IS NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(message + " : "+ e.toString() + " : "+ sql );
		}

		if(no > 0)
		{
			return false;
		}

		
		return true;
	}
	
	private boolean setStatementDate() throws Exception
	{
		@SuppressWarnings("unused")
		int no = 0;
		
		message = Msg.getMsg(getCtx(), "CreateNew") + " : " + Msg.getElement(getCtx(), "JP_BankData_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		
		
		StringBuilder sql = new StringBuilder ("UPDATE I_BankDataJP ")
				.append("SET StatementDate= TO_DATE('" + p_JP_Year + "'|| '/' || JP_Month || '/'  || JP_Date,'YYYY/MM/DD') ")
				.append(" WHERE JP_Month IS NOT NULL AND JP_Date IS NOT NULL ")
				.append(" AND I_IsImported='N' AND StatementDate IS NULL ").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message + " : "+ e.toString() + " : "+ sql );
		}
		
		return true;
	}
	

	
	private boolean setDateAcct() throws Exception
	{
		@SuppressWarnings("unused")
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_BankDataJP ")
				.append("SET DateAcct= TO_DATE('" + p_JP_Year + "'|| '/' || JP_AcctMonth || '/'  || JP_AcctDate,'YYYY/MM/DD') ")
				.append(" WHERE JP_AcctMonth IS NOT NULL AND JP_AcctDate IS NOT NULL ")
				.append(" AND I_IsImported='N' AND DateAcct IS NULL ").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message + " : "+ e.toString() + " : "+ sql );
		}
		
		return true;
	}
	
	private boolean createBankData(X_I_BankDataJP imp, MBankData m_BankData)
	{
		ModelValidationEngine.get().fireImportValidate(this, imp, m_BankData, ImportValidator.TIMING_BEFORE_IMPORT);
		
		PO.copyValues(imp, m_BankData);
		m_BankData.setJP_BankData_ID(0);
		m_BankData.setJP_BankDataSchema_ID(p_JP_BankDataSchema_ID);
		m_BankData.setAD_Org_ID(imp.getAD_Org_ID());
		if(imp.getStatementDate() == null && imp.getDateAcct() == null)
		{
			message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "FillMandatory") + Msg.getElement(getCtx(), "StatementDate") + " OR " + Msg.getElement(getCtx(), "DateAcct") ;
			
		} else if(imp.getStatementDate() == null) {
			m_BankData.setStatementDate(imp.getDateAcct());
			m_BankData.setDateAcct(imp.getDateAcct());
			
		}else if(imp.getDateAcct() == null) {
			m_BankData.setStatementDate(imp.getStatementDate());
			m_BankData.setDateAcct(imp.getStatementDate());
		}else {
			m_BankData.setStatementDate(imp.getStatementDate());
			m_BankData.setDateAcct(imp.getDateAcct());
		}
		
	
		String name = LocalDateTime.now().toString();
		m_BankData.setName(name);
		
		m_BankData.setJP_Processing1("Y");
		m_BankData.setJP_ProcessedTime1(Timestamp.valueOf(LocalDateTime.now()));
		
		if(!Util.isEmpty(imp.getJP_BankAccountType()))
		{
			if(imp.getJP_BankAccountType().length() != 1)
				m_BankData.setJP_BankAccountType(null);
		}
		
		ModelValidationEngine.get().fireImportValidate(this, imp, m_BankData, ImportValidator.TIMING_AFTER_IMPORT);

		try {
			m_BankData.saveEx(get_TrxName());
		}catch (Exception e) {

		    message = Msg.getMsg(getCtx(),"SaveIgnored") + Msg.getElement(getCtx(), "JP_BankData_ID") +" : " + e.toString();

			return false;
		}
		
		
		list_BankData.add(m_BankData);
		
		//addBufferLog(0, null, null, m_BankData.getName(), MBankData.Table_ID, m_BankData.getJP_BankData_ID());
		
		return true;
	}
	
	
	private boolean addBankDataLine(X_I_BankDataJP imp, MBankData m_BankData, MBankDataLine m_line, int lineNo)
	{
		ModelValidationEngine.get().fireImportValidate(this, imp, m_line, ImportValidator.TIMING_BEFORE_IMPORT);

		PO.copyValues(imp, m_line);

		m_line.setJP_BankData_ID(m_BankData.getJP_BankData_ID());
		m_line.setAD_Org_ID(m_BankData.getAD_Org_ID());
		m_line.setLine(lineNo);
		m_line.setStatementLineDate(m_BankData.getStatementDate());
		m_line.setDateAcct(m_BankData.getDateAcct());
		m_line.setValutaDate(m_BankData.getDateAcct());
		m_line.setJP_BankName_Kana(imp.getJP_BankName_Kana_Line());
		m_line.setJP_BranchName_Kana(imp.getJP_BranchName_Kana_Line());
		
		if(!Util.isEmpty(imp.getJP_BankAccountType()))
		{
			if(imp.getJP_BankAccountType().length() != 1)
				m_line.setJP_BankAccountType(null);
		}
		
		MColumn m_colmun = MColumn.get(getCtx(), MBankDataLine.Table_Name, MBankDataLine.COLUMNNAME_JP_A_Name_Kana);
		int columnLength = m_colmun.getFieldLength();

		String a_Name_Lana = imp.getJP_A_Name_Kana();
		if(!Util.isEmpty(a_Name_Lana))
		{
			if(a_Name_Lana.length() > columnLength)
			{
				m_line.setJP_A_Name_Kana(a_Name_Lana.substring(0, columnLength));
			}
		}

		ModelValidationEngine.get().fireImportValidate(this, imp, m_line, ImportValidator.TIMING_AFTER_IMPORT);

		try {
			m_line.saveEx(get_TrxName());
		}catch (Exception e) {


				message = Msg.getMsg(getCtx(),"SaveIgnored") + Msg.getElement(getCtx(), "C_OrderLine_ID") +" : " + e.toString();
				return false;
			
		}

		imp.setJP_BankDataLine_ID(m_line.getJP_BankDataLine_ID());
		imp.setI_IsImported(true);
		imp.setProcessed(true);
		imp.saveEx(get_TrxName());

		return true;
	}
}
