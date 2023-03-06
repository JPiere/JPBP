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
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.logging.Level;

import org.adempiere.model.ImportValidator;
import org.adempiere.process.ImportProcess;
import org.adempiere.util.IProcessUI;
import org.compiere.model.MDocType;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.PO;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.MDataMigration;
import jpiere.base.plugin.org.adempiere.model.MDataMigrationLine;
import jpiere.base.plugin.org.adempiere.model.X_I_DataMigrationJP;

/**
 * JPIERE-0413:Import Migration Data
 *
 *
 * @author h.hagiwara
 *
 */
public class JPiereImportDataMigration extends SvrProcess implements ImportProcess {

	/**	Client to be imported to		*/
	private int				p_AD_Client_ID = 0;

	/**	Delete old Imported				*/
	private boolean			p_DeleteOldImported = false;

	/**	Only validate, don't import		*/
	private boolean	p_IsValidateOnly = false;

	private String message = null;

	private String p_JP_ImportSalesRepIdentifier = JPiereImportUser.JP_ImportUserIdentifier_Value;

	private String p_JP_ImportUserIdentifier = JPiereImportUser.JP_ImportUserIdentifier_Name;

	private String p_JP_ImportDropShipUserIdentifier = JPiereImportUser.JP_ImportUserIdentifier_Name;

	private String p_JP_ReimportPolicy = JPiereImportGLJournal.JP_ReimportPolicy_NotImport;

	private IProcessUI processMonitor = null;

	private long startTime = System.currentTimeMillis();

	@Override
	protected void prepare()
	{
		p_AD_Client_ID = getAD_Client_ID();
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (name.equals("DeleteOldImported"))
				p_DeleteOldImported = "Y".equals(para[i].getParameter());
			else if (name.equals("IsValidateOnly"))
				p_IsValidateOnly = para[i].getParameterAsBoolean();
			else if (name.equals("JP_ImportSalesRepIdentifier"))
				p_JP_ImportSalesRepIdentifier = para[i].getParameterAsString();
			else if (name.equals("JP_ImportUserIdentifier"))
				p_JP_ImportUserIdentifier = para[i].getParameterAsString();
			else if (name.equals("JP_ImportDropSpUserIdentifier"))
				p_JP_ImportDropShipUserIdentifier = para[i].getParameterAsString();
			else if (name.equals("JP_ReimportPolicy"))
				p_JP_ReimportPolicy = para[i].getParameterAsString();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}

	}

	@Override
	protected String doIt() throws Exception
	{
		processMonitor = Env.getProcessUI(getCtx());

		StringBuilder sql = null;
		int no = 0;

		/** Delete Old Imported */
		if (p_DeleteOldImported)
		{
			sql = new StringBuilder ("DELETE FROM I_DataMigrationJP ")
				  .append("WHERE I_IsImported='Y' ").append (getWhereClause());
			no = DB.executeUpdate(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("Delete Old Impored =" + no);
		}


		/** Reset I_ErrorMsg */
		sql = new StringBuilder ("UPDATE I_DataMigrationJP ")
				.append("SET I_ErrorMsg='' ")
				.append(" WHERE I_IsImported<>'Y' ").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(String.valueOf(no));
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + sql );
		}


		ModelValidationEngine.get().fireImportValidate(this, null, null, ImportValidator.TIMING_BEFORE_VALIDATE);

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "AD_Org_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupAD_Org_ID())
			commitEx();
		else
			return message;

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "JP_DataMigration_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupJP_DataMigration_ID())
			commitEx();
		else
			return message;

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "AD_Table_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupAD_Table_ID())
			commitEx();
		else
			return message;

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "SalesRep_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupSalesRep_ID())
			commitEx();
		else
			return message;


		//Business partner Info
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

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "AD_User_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupAD_User_ID())
			commitEx();
		else
			return message;


		//Ship & Receipt info
		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "M_Warehouse_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupM_Warehouse_ID())
			commitEx();
		else
			return message;

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "M_Locator_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupM_Locator_ID())
			commitEx();
		else
			return message;

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "M_Shipper_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupM_Shipper_ID())
			commitEx();
		else
			return message;

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "DropShip_BPartner_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupDropShip_BPartner_ID())
			commitEx();
		else
			return message;

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "DropShip_Location_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupDropShip_Location_ID())
			commitEx();
		else
			return message;

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "DropShip_User_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupDropShip_User_ID())
			commitEx();
		else
			return message;



		//Invoice Info
		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "M_PriceList_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupM_PriceList_ID())
			commitEx();
		else
			return message;

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_PaymentTerm_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupC_PaymentTerm_ID())
			commitEx();
		else
			return message;


		//Bank Account INfo
		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_BankAccount_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupC_BankAccount_ID())
			commitEx();
		else
			return message;

		//Reference
		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_Project_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupC_Project_ID())
			commitEx();
		else
			return message;

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_Campaign_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupC_Campaign_ID())
			commitEx();
		else
			return message;

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_Activity_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupC_Activity_ID())
			commitEx();
		else
			return message;

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "AD_OrgTrx_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupAD_OrgTrx_ID())
			commitEx();
		else
			return message;

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "User1_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupUser1_ID())
			commitEx();
		else
			return message;

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "User2_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupUser2_ID())
			commitEx();
		else
			return message;

		//Line info
		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "M_Product_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupM_Product_ID())
			commitEx();
		else
			return message;

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_Charge_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupC_Charge_ID())
			commitEx();
		else
			return message;

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_UOM_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupC_UOM_ID())
			commitEx();
		else
			return message;

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_Tax_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupC_Tax_ID())
			commitEx();
		else
			return message;

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "JP_LocatorFrom_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupJP_LocatorFrom_ID())
			commitEx();
		else
			return message;

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "JP_LocatorTo_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupJP_LocatorTo_ID())
			commitEx();
		else
			return message;


		//Line Reference
		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "JP_Line_Project_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupJP_Line_Project_ID())
			commitEx();
		else
			return message;

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "JP_Line_Campaign_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupJP_Line_Campaign_ID())
			commitEx();
		else
			return message;

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "JP_Line_Activity_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupJP_Line_Activity_ID())
			commitEx();
		else
			return message;

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "JP_Line_OrgTrx_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupJP_Line_OrgTrx_ID())
			commitEx();
		else
			return message;

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "JP_Line_User1_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupJP_Line_User1_ID())
			commitEx();
		else
			return message;

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "User2_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupJP_Line_User2_ID())
			commitEx();
		else
			return message;

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "JP_Line_BPartner_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupJP_Line_BPartner_ID())
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
		sql = new StringBuilder ("SELECT * FROM I_DataMigrationJP WHERE I_IsImported='N' ")
					.append(getWhereClause())
					.append(" ORDER BY JP_DataMigration_Identifier, DocumentNo, Line ");

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
			String lastJP_DataMigration_Identifier = "";
			//String lastDocumentNo = "";
			MDataMigration dataMigration = null;
			MDataMigrationLine dataMigrationLine = null;

			String deleteSQL_DataMigrationLine = "DELETE JP_DataMigrationLine WHERE JP_DataMigration_ID=?";
			String deleteSQL_DataMigration = "DELETE JP_DataMigration WHERE JP_DataMigration_ID=?";

			while (rs.next())
			{
				recordsNum++;

				X_I_DataMigrationJP imp = new X_I_DataMigrationJP(getCtx (), rs, get_TrxName());

				//Re-Import
				if(imp.getJP_DataMigration_ID() != 0)
				{
					if(p_JP_ReimportPolicy.equals(JPiereImportGLJournal.JP_ReimportPolicy_NotImport))
					{
						skipNum++;
						String msg = Msg.getMsg(getCtx(), "AlreadyExists");
						imp.setI_ErrorMsg(msg);
						imp.setI_IsImported(false);
						imp.setProcessed(false);
						imp.saveEx(get_TrxName());
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
						continue;
					}
				}


				boolean isCreateHeader= true;
				if(!Util.isEmpty(lastJP_DataMigration_Identifier) && lastJP_DataMigration_Identifier.equals(imp.getJP_DataMigration_Identifier()))
				{
					isCreateHeader = false;
					if(dataMigration.getJP_DataMigration_ID() == 0)
					{
						errorNum++;
						String msg = Msg.getMsg(getCtx(), "JP_UnexpectedError");
						imp.setI_ErrorMsg(msg);
						imp.setI_IsImported(false);
						imp.setProcessed(false);
						imp.saveEx(get_TrxName());
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
						continue;
					}

				}else {

					lastJP_DataMigration_Identifier = imp.getJP_DataMigration_Identifier();
					//lastDocumentNo = imp.getDocumentNo();
				}


				//Create Header
				if(isCreateHeader)
				{
					if(p_JP_ReimportPolicy.equals(JPiereImportGLJournal.JP_ReimportPolicy_DeleteExistingData))
					{

						//Delete Migration Data Line
						int deleteJournalLineNum =DB.executeUpdate(deleteSQL_DataMigrationLine, imp.getJP_DataMigration_ID(), get_TrxName());
						if (log.isLoggable(Level.FINE)) log.fine("Delete Migration Data Line -> #" + deleteJournalLineNum);

						//Delete Migration Data
						int deleteJournalNum =DB.executeUpdate(deleteSQL_DataMigration, imp.getJP_DataMigration_ID(), get_TrxName());
						if (log.isLoggable(Level.FINE)) log.fine("Delete Migration Data -> #" + deleteJournalNum);

					}

					dataMigration = new MDataMigration(getCtx (), 0, get_TrxName());
					if(createDataMigration(imp, dataMigration))
					{
						successCreateDocHeader++;

					}else {

						failureCreateDocHeader++;
						errorNum++;//Error of Header include number of Error.

						rollback();
						imp.setJP_DataMigration_ID(0);
						imp.setI_ErrorMsg(message);
						imp.setI_IsImported(false);
						imp.setProcessed(false);
						imp.saveEx(get_TrxName());
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
						continue;
					}
				}

				//Create Line
				dataMigrationLine = new MDataMigrationLine(getCtx (), 0, get_TrxName());
				if(!isCreateHeader)
				{
					imp.setJP_DataMigration_ID(dataMigration.getJP_DataMigration_ID());
				}

				if(createDataMigrationLine(imp, dataMigration,dataMigrationLine))
				{
					successCreateDocLine++;
					successNum++;

					imp.setI_IsImported(true);
					imp.setProcessed(true);
					imp.saveEx(get_TrxName());

				}else {

					failureCreateDocLine++;
					errorNum++;//Error of Line include number of Error.

					rollback();
					imp.setJP_DataMigration_ID(0);
					imp.setJP_DataMigrationLine_ID(0);
					imp.setI_ErrorMsg(message);
					imp.setI_IsImported(false);
					imp.setProcessed(false);
					imp.saveEx(get_TrxName());
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


		long endTime = System.currentTimeMillis();
		long time = endTime - startTime;
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        String timeFormatted = formatter.format(time);

		return Msg.getMsg(getCtx(), "ProcessOK") + "  "  + timeFormatted + "  "+ records + " : " + recordsNum + " = "
				+ skipRecords + " : " + skipNum + " + "
				+ errorRecords + " : " + errorNum + " + "
				+ success + " : " + successNum
				+ "   [" + detail +" --> "
				+ createHeader + "( "+  success + " : " + successCreateDocHeader + "  /  " +  failure + " : " + failureCreateDocHeader + " ) + "
				+ createLine  + " ( "+  success + " : " + successCreateDocLine + "  /  " +  failure + " : " + failureCreateDocLine+ " ) ]"
				;

	}

	@Override
	public String getImportTableName()
	{
		return X_I_DataMigrationJP.Table_Name;
	}

	String whereClause = null;

	@Override
	public String getWhereClause()
	{
		if(Util.isEmpty(whereClause))
			whereClause = " AND AD_Client_ID=" + p_AD_Client_ID;

		return whereClause;
	}

	/**
	 * Reverse Lookup AD_Org_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupAD_Org_ID() throws Exception
	{
		int no = 0;

		//Look up AD_Org ID From JP_Org_Value
		StringBuilder sql = new StringBuilder ("UPDATE I_DataMigrationJP i ")
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
		sql = new StringBuilder ("UPDATE I_DataMigrationJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append(" WHERE AD_Org_ID = 0 ")
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
	 * Reverse Lookup JP_DataMigration_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupJP_DataMigration_ID() throws Exception
	{
		int no = 0;

		//Look up JP_DataMigration_ID ID From JP_DataMigration_Identifier
		StringBuilder sql = new StringBuilder ("UPDATE I_DataMigrationJP i ")
				.append("SET JP_DataMigration_ID=(SELECT JP_DataMigration_ID FROM JP_DataMigration p")
				.append(" WHERE i.JP_DataMigration_Identifier=p.JP_DataMigration_Identifier AND p.AD_Client_ID=i.AD_Client_ID ) ")
				.append(" WHERE i.JP_DataMigration_ID IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("Look up JP_DataMigration_ID ID From JP_DataMigration_Identifier -> #" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}

		return true;

	}//reverseLookupAD_Org_ID


	/**
	 * Reverse Lookup AD_Table_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupAD_Table_ID() throws Exception
	{
		int no = 0;

		//Look up AD_Table ID From TableName
		StringBuilder sql = new StringBuilder ("UPDATE I_DataMigrationJP i ")
				.append("SET AD_Table_ID=(SELECT AD_Table_ID FROM AD_Table p")
				.append(" WHERE i.TableName=p.TableName AND p.AD_Client_ID=0 ) ")
				.append(" WHERE i.AD_Table_ID IS NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}

		//Invalid TableName
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "TableName");
		sql = new StringBuilder ("UPDATE I_DataMigrationJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append("WHERE AD_Table_ID IS NULL AND TableName IS NOT NULL")
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

	}//reverseLookupAD_Table_ID


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
			sql = new StringBuilder ("UPDATE I_DataMigrationJP i ")
					.append("SET SalesRep_ID=(SELECT MAX(AD_User_ID) FROM AD_User p INNER JOIN C_BPartner bp ON (p.C_BPartner_ID = bp.C_BPartner_ID) ")
					.append(" WHERE i.JP_SalesRep_EMail=p.EMail AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) AND bp.IsSalesRep='Y' ) ")
					.append(" WHERE i.JP_SalesRep_EMail IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportSalesRepIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_Name)) { //Name

			sql = new StringBuilder ("UPDATE I_DataMigrationJP i ")
					.append("SET SalesRep_ID=(SELECT MAX(AD_User_ID) FROM AD_User p INNER JOIN C_BPartner bp ON (p.C_BPartner_ID = bp.C_BPartner_ID) ")
					.append(" WHERE i.JP_SalesRep_Name=p.Name AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) AND bp.IsSalesRep='Y') ")
					.append(" WHERE i.JP_SalesRep_Name IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportSalesRepIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_Value)) { //Value

			sql = new StringBuilder ("UPDATE I_DataMigrationJP i ")
					.append("SET SalesRep_ID=(SELECT MAX(AD_User_ID) FROM AD_User p INNER JOIN C_BPartner bp ON (p.C_BPartner_ID = bp.C_BPartner_ID) ")
					.append(" WHERE i.JP_SalesRep_Value=p.Value AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) AND bp.IsSalesRep='Y'  ) ")
					.append(" WHERE i.JP_SalesRep_Value IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportSalesRepIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueEMail)) { //Value + E-Mail

			//In case of EMail is not null
			sql = new StringBuilder ("UPDATE I_DataMigrationJP i ")
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
			sql = new StringBuilder ("UPDATE I_DataMigrationJP i ")
					.append("SET SalesRep_ID=(SELECT MAX(AD_User_ID) FROM AD_User p INNER JOIN C_BPartner bp ON (p.C_BPartner_ID = bp.C_BPartner_ID) ")
					.append(" WHERE i.JP_SalesRep_Value=p.Value AND i.JP_SalesRep_EMail IS NULL l AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) AND bp.IsSalesRep='Y' ) ")
					.append(" WHERE i.JP_SalesRep_Value IS NOT NULL AND i.JP_SalesRep_EMail IS NULL AND i.SalesRep_ID IS NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());


		}else if(p_JP_ImportSalesRepIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueName)) { //Value + Name

			sql = new StringBuilder ("UPDATE I_DataMigrationJP i ")
					.append("SET SalesRep_ID=(SELECT MAX(AD_User_ID) FROM AD_User p INNER JOIN C_BPartner bp ON (p.C_BPartner_ID = bp.C_BPartner_ID) ")
					.append(" WHERE i.JP_SalesRep_Value=p.Value AND i.JP_SalesRep_Name=p.Name AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) AND bp.IsSalesRep='Y'  ) ")
					.append(" WHERE i.JP_SalesRep_Value IS NOT NULL AND i.JP_SalesRep_Name IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportSalesRepIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueNameEmail)) { //Value + Name + EMail

			//In case of EMail is not null
			sql = new StringBuilder ("UPDATE I_DataMigrationJP i ")
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
			sql = new StringBuilder ("UPDATE I_DataMigrationJP i ")
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

	/**
	 * Reverse Lookup C_BPartner_ID
	 *
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupC_BPartner_ID()throws Exception
	{
		int no = 0;

		//Reverse lookup C_BPartner_ID From JP_BPartner_Value
		StringBuilder  sql = new StringBuilder ("UPDATE I_DataMigrationJP i ")
			.append("SET C_BPartner_ID=(SELECT C_BPartner_ID FROM C_BPartner p")
			.append(" WHERE i.JP_BPartner_Value=p.Value AND i.AD_Client_ID=p.AD_Client_ID) ")
			.append("WHERE i.C_BPartner_ID IS NULL AND i.JP_BPartner_Value IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("Reverse lookup C_BPartner_ID From JP_BPartner_Value -> #" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}

		return true;

	}

	/**
	 *
	 * Reverse Lookup JP_Line_BPartner_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupJP_Line_BPartner_ID()throws Exception
	{
		int no = 0;

		//Reverse lookup JP_Line_BPartner_ID From JP_Line_BPartner_Value
		StringBuilder  sql = new StringBuilder ("UPDATE I_DataMigrationJP i ")
			.append("SET JP_Line_BPartner_ID=(SELECT C_BPartner_ID FROM C_BPartner p")
			.append(" WHERE i.JP_Line_BPartner_Value=p.Value AND i.AD_Client_ID=p.AD_Client_ID) ")
			.append("WHERE i.JP_Line_BPartner_ID IS NULL AND i.JP_Line_BPartner_Value IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("Reverse lookup JP_Line_BPartner_ID From JP_Line_BPartner_Value -> #" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}

		return true;

	}

	/**
	 * Reverse Lookup C_BPartner_Location_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupC_BPartner_Location_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_DataMigrationJP i ")
				.append("SET C_BPartner_Location_ID=(SELECT C_BPartner_Location_ID FROM C_BPartner_Location p")
				.append(" WHERE i.JP_BPartner_Location_Name=p.Name AND i.C_BPartner_ID=p.C_BPartner_ID) ")
				.append("WHERE i.C_BPartner_Location_ID IS NULL AND i.JP_BPartner_Location_Name IS NOT NULL ")
				.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("Reverse Lookup C_BPartner_Location_ID -> #" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		return true;
	}

	/**
	 * Reverse Lookup AD_User_ID
	 *
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupAD_User_ID() throws Exception
	{
		if(Util.isEmpty(p_JP_ImportUserIdentifier) || p_JP_ImportUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_NotCollate))
			return true;

		StringBuilder sql = null;

		if(p_JP_ImportUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_EMail)) //E-Mail
		{
			sql = new StringBuilder ("UPDATE I_DataMigrationJP i ")
					.append("SET AD_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_User_EMail=p.EMail AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.C_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.EMail IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_Name)) { //Name

			sql = new StringBuilder ("UPDATE I_DataMigrationJP i ")
					.append("SET AD_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_User_Name=p.Name AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.C_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_User_Name IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_Value)) { //Value

			sql = new StringBuilder ("UPDATE I_DataMigrationJP i ")
					.append("SET AD_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_User_Value=p.Value AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.C_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_User_Value IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueEMail)) { //Value + E-Mail

			//In case of EMail is not null
			sql = new StringBuilder ("UPDATE I_DataMigrationJP i ")
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
			sql = new StringBuilder ("UPDATE I_DataMigrationJP i ")
					.append("SET AD_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_User_Value=p.Value AND i.JP_User_EMail IS NULL AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.C_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_User_Value IS NOT NULL AND i.JP_User_EMail IS NULL AND i.AD_User_ID IS NOT NULL ")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueName)) { //Value + Name

			sql = new StringBuilder ("UPDATE I_DataMigrationJP i ")
					.append("SET AD_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_User_Value=p.Value AND i.JP_User_Name=p.Name AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.C_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_User_Value IS NOT NULL AND i.JP_User_Name IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueNameEmail)) { //Value + Name + EMail

			//In case of EMail is not null
			sql = new StringBuilder ("UPDATE I_DataMigrationJP i ")
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
			sql = new StringBuilder ("UPDATE I_DataMigrationJP i ")
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

	}


	/**
	 * Reverse Lookup M_Warehouse_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupM_Warehouse_ID() throws Exception
	{
		int no = 0;
		StringBuilder sql = new StringBuilder ("UPDATE I_DataMigrationJP o ")
				  .append("SET M_Warehouse_ID=(SELECT M_Warehouse_ID FROM M_Warehouse w")
				  .append(" WHERE o.JP_Warehouse_Value=w.Value AND o.AD_Org_ID=w.AD_Org_ID) ")
				  .append(" WHERE M_Warehouse_ID IS NULL AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(message +" : " + e.toString() +" : " + sql);
		}

		//Invalid M_Warehouse_ID
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "M_Warehouse_ID");
		sql = new StringBuilder ("UPDATE I_DataMigrationJP ")
				.append("SET I_ErrorMsg='"+ message + "'")
				.append(" WHERE M_Warehouse_ID IS NULL AND JP_Warehouse_Value IS NOT NULL ")
				.append(" AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(message +" : " + e.toString() +" : " + sql);
		}


		if(no > 0)
		{
			return false;
		}

		return true;
	}

	/**
	 * Reverse Lookup M_Locator_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupM_Locator_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_DataMigrationJP i ")
			.append("SET M_Locator_ID=(SELECT M_Locator_ID FROM M_Locator p")
			.append(" WHERE i.JP_Locator_Value=p.Value AND i.AD_Client_ID=p.AD_Client_ID) ")
			.append(" WHERE i.M_Locator_ID IS NULL AND i.JP_Locator_Value IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Invalid JP_Locator_Value
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "JP_Locator_Value");
		sql = new StringBuilder ("UPDATE I_DataMigrationJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append(" WHERE M_Locator_ID IS NULL AND JP_Locator_Value IS NOT NULL")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(message +" : " + e.toString() +" : " + sql);
		}

		if(no > 0)
		{
			return false;
		}

		return true;
	}


	/**
	 * Reverse Lookup M_Shipper_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupM_Shipper_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_DataMigrationJP i ")
			.append("SET M_Shipper_ID=(SELECT M_Shipper_ID FROM M_Shipper p")
			.append(" WHERE i.JP_Shipper_Name=p.Name AND i.AD_Client_ID=p.AD_Client_ID) ")
			.append(" WHERE i.M_Shipper_ID IS NULL AND i.JP_Shipper_Name IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Invalid JP_Shipper_Name
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "JP_Shipper_Name");
		sql = new StringBuilder ("UPDATE I_DataMigrationJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append(" WHERE M_Shipper_ID IS NULL AND JP_Shipper_Name IS NOT NULL")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(message +" : " + e.toString() +" : " + sql);
		}

		if(no > 0)
		{
			return false;
		}

		return true;
	}

	/**
	 * Reverse Lookup DropShip_BPartner_ID()
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupDropShip_BPartner_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_DataMigrationJP i ")
			.append("SET DropShip_BPartner_ID=(SELECT C_BPartner_ID FROM C_BPartner p")
			.append(" WHERE i.JP_DropShip_BP_Value=p.Value AND i.AD_Client_ID=p.AD_Client_ID) ")
			.append("WHERE i.DropShip_BPartner_ID IS NULL AND i.JP_DropShip_BP_Value IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Invalid BPartner_Value
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "JP_DropShip_BP_Value");
		sql = new StringBuilder ("UPDATE I_DataMigrationJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append("WHERE DropShip_BPartner_ID IS NULL AND JP_DropShip_BP_Value IS NOT NULL")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(message +" : " + e.toString() +" : " + sql);
		}

		if(no > 0)
		{
			return false;
		}

		return true;
	}

	/**
	 * Reverse Lookup DropShip_Location_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupDropShip_Location_ID()throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_DataMigrationJP i ")
				.append("SET DropShip_Location_ID=(SELECT C_BPartner_Location_ID FROM C_BPartner_Location p")
				.append(" WHERE i.JP_DropShip_BP_Location_Name=p.Name AND i.DropShip_BPartner_ID=p.C_BPartner_ID) ")
				.append(" WHERE i.DropShip_Location_ID IS NULL AND i.JP_DropShip_BP_Location_Name IS NOT NULL ")
				.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Invalid JP_DropShip_BP_Location_Name
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "JP_DropShip_BP_Location_Name");
		sql = new StringBuilder ("UPDATE I_DataMigrationJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append(" WHERE DropShip_Location_ID IS NULL AND JP_DropShip_BP_Location_Name IS NOT NULL")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(message +" : " + e.toString() +" : " + sql);
		}

		if(no > 0)
		{
			return false;
		}

		return true;
	}

	/**
	 * Reverse Lookup DropShip_User_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupDropShip_User_ID()throws Exception
	{
		if(Util.isEmpty(p_JP_ImportDropShipUserIdentifier) || p_JP_ImportDropShipUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_NotCollate))
			return true;

		StringBuilder sql = null;

		if(p_JP_ImportDropShipUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_EMail)) //E-Mail
		{
			sql = new StringBuilder ("UPDATE I_DataMigrationJP i ")
					.append("SET DropShip_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_DropShip_User_EMail=p.EMail AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.DropShip_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_DropShip_User_EMail IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportDropShipUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_Name)) { //Name

			sql = new StringBuilder ("UPDATE I_DataMigrationJP i ")
					.append("SET DropShip_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_DropShip_User_Name=p.Name AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.DropShip_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_DropShip_User_Name IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportDropShipUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_Value)) { //Value

			sql = new StringBuilder ("UPDATE I_DataMigrationJP i ")
					.append("SET DropShip_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_DropShip_User_Value=p.Value AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.DropShip_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_DropShip_User_Value IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportDropShipUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueEMail)) { //Value + E-Mail

			//In case of EMail is not null
			sql = new StringBuilder ("UPDATE I_DataMigrationJP i ")
					.append("SET DropShip_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_DropShip_User_Value=p.Value AND i.JP_DropShip_User_EMail=p.EMail AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.DropShip_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_DropShip_User_Value IS NOT NULL AND i.JP_DropShip_User_EMail IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

			try {
				DB.executeUpdateEx(sql.toString(), get_TrxName());
			}catch(Exception e) {
				message = message + " : " +e.toString()+ " : "+sql.toString();
				return false;
			}

			//In case of EMail is null
			sql = new StringBuilder ("UPDATE I_DataMigrationJP i ")
					.append("SET DropShip_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_DropShip_User_Value=p.Value AND i.JP_DropShip_User_EMail IS NULL AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.DropShip_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_DropShip_User_Value IS NOT NULL AND i.JP_DropShip_User_EMail IS NULL AND DropShip_User_ID IS NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportDropShipUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueName)) { //Value + Name

			sql = new StringBuilder ("UPDATE I_DataMigrationJP i ")
					.append("SET DropShip_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_DropShip_User_Value=p.Value AND i.JP_DropShip_User_Name=p.Name AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.DropShip_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_DropShip_User_Value IS NOT NULL AND i.JP_DropShip_User_Name IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportDropShipUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueNameEmail)) { //Value + Name + EMail

			//In case of EMail is not null
			sql = new StringBuilder ("UPDATE I_DataMigrationJP i ")
					.append("SET DropShip_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_DropShip_User_Value=p.Value AND i.JP_DropShip_User_Name=p.Name  AND i.JP_DropShip_User_EMail=p.EMail AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.DropShip_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_DropShip_User_Value IS NOT NULL AND i.JP_DropShip_User_Name IS NOT NULL AND i.JP_DropShip_User_EMail IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

			try {
				DB.executeUpdateEx(sql.toString(), get_TrxName());
			}catch(Exception e) {
				message = message + " : " +e.toString()+ " : "+sql.toString();
				return false;
			}

			//In case of EMail is null
			sql = new StringBuilder ("UPDATE I_DataMigrationJP i ")
					.append("SET DropShip_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_DropShip_User_Value=p.Value AND i.JP_DropShip_User_Name=p.Name  AND i.JP_DropShip_User_EMail IS NULL AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.DropShip_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_DropShip_User_Value IS NOT NULL AND i.JP_DropShip_User_Name IS NOT NULL AND i.JP_DropShip_User_EMail IS NULL AND DropShip_User_ID IS NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportDropShipUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_NotCollate)){

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

	/**
	 * Reverse Lookup M_PriceList_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupM_PriceList_ID() throws Exception
	{
		int no = 0;

		//Set M_PriceList_ID from JP_PriceList_Name
		StringBuilder sql = new StringBuilder ("UPDATE I_DataMigrationJP o ")
				  .append("SET M_PriceList_ID=(SELECT MAX(M_PriceList_ID) FROM M_PriceList p ")
				  .append(" WHERE p.Name=o.JP_PriceList_Name AND o.AD_Client_ID=p.AD_Client_ID) ")
				  .append(" WHERE M_PriceList_ID IS NULL AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Set C_Currency_ID from  M_PriceList_ID
		sql = new StringBuilder ("UPDATE I_DataMigrationJP o ")
				  .append("SET C_Currency_ID=(SELECT C_Currency_ID FROM M_PriceList p ")
				  .append(" WHERE p.M_PriceList_ID=o.M_PriceList_ID ) ")
				  .append(" WHERE I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Set M_PriceList_ID from Default
		sql = new StringBuilder ("UPDATE I_DataMigrationJP o ")
				  .append("SET M_PriceList_ID=(SELECT MAX(M_PriceList_ID) FROM M_PriceList p WHERE p.IsDefault='Y'")
				  .append(" AND p.C_Currency_ID=o.C_Currency_ID AND p.IsSOPriceList=o.IsSOTrx AND o.AD_Client_ID=p.AD_Client_ID) ")
				  .append("WHERE M_PriceList_ID IS NULL AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Set M_PriceList_ID from Default
		sql = new StringBuilder ("UPDATE I_DataMigrationJP o ")
			  .append("SET M_PriceList_ID=(SELECT MAX(M_PriceList_ID) FROM M_PriceList p WHERE p.IsDefault='Y'")
			  .append(" AND p.IsSOPriceList=o.IsSOTrx AND o.AD_Client_ID=p.AD_Client_ID) ")
			  .append("WHERE M_PriceList_ID IS NULL AND C_Currency_ID IS NULL AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		sql = new StringBuilder ("UPDATE I_DataMigrationJP o ")
			  .append("SET M_PriceList_ID=(SELECT MAX(M_PriceList_ID) FROM M_PriceList p ")
			  .append(" WHERE p.C_Currency_ID=o.C_Currency_ID AND p.IsSOPriceList=o.IsSOTrx AND o.AD_Client_ID=p.AD_Client_ID) ")
			  .append("WHERE M_PriceList_ID IS NULL AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		sql = new StringBuilder ("UPDATE I_DataMigrationJP o ")
			  .append("SET M_PriceList_ID=(SELECT MAX(M_PriceList_ID) FROM M_PriceList p ")
			  .append(" WHERE p.IsSOPriceList=o.IsSOTrx AND o.AD_Client_ID=p.AD_Client_ID) ")
			  .append("WHERE M_PriceList_ID IS NULL AND C_Currency_ID IS NULL AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Invalid M_PriceList_ID
		message = Msg.getMsg(getCtx(), "Error")  + Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "M_PriceList_ID");
		sql = new StringBuilder ("UPDATE I_DataMigrationJP ")
				.append("SET I_ErrorMsg='"+ message + "'")
				.append(" WHERE M_PriceList_ID IS NULL")
				.append(" AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception( message +" : " + e.toString() +" : " + sql );
		}

		if(no > 0)
		{
			return false;
		}

		return true;
	}

	/**
	 * Reverse Lookup C_PaymentTerm_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupC_PaymentTerm_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_DataMigrationJP o ")
			  .append("SET C_PaymentTerm_ID=(SELECT C_PaymentTerm_ID FROM C_PaymentTerm p")
			  .append(" WHERE o.JP_PaymentTerm_Value=p.Value AND o.AD_Client_ID=p.AD_Client_ID) ")
			  .append("WHERE C_PaymentTerm_ID IS NULL AND JP_PaymentTerm_Value IS NOT NULL AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Set Default
		sql = new StringBuilder ("UPDATE I_DataMigrationJP o ")
			  .append("SET C_PaymentTerm_ID=(SELECT MAX(C_PaymentTerm_ID) FROM C_PaymentTerm p")
			  .append(" WHERE p.IsDefault='Y' AND o.AD_Client_ID=p.AD_Client_ID) ")
			  .append("WHERE C_PaymentTerm_ID IS NULL AND o.JP_PaymentTerm_Value IS NULL AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Invalid PaymentTermValue
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "C_PaymentTerm_ID");
		sql = new StringBuilder ("UPDATE I_DataMigrationJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			  .append(" WHERE C_PaymentTerm_ID IS NULL")
			  .append(" AND I_IsImported<>'Y'").append (getWhereClause());
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
	}

	/**
	 * Reverse Lookup C_BankAccount_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupC_BankAccount_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_DataMigrationJP i ")
			.append("SET C_BankAccount_ID=(SELECT C_BankAccount_ID FROM C_BankAccount p INNER JOIN C_Bank b ON (p.C_Bank_ID = b.C_Bank_ID )")
			.append(" WHERE i.JP_BankAccount_Value=p.Value AND i.AD_Client_ID=p.AD_Client_ID AND i.JP_Bank_Name = b.Name  ) ")
			.append("WHERE i.C_BankAccount_ID IS NULL AND i.JP_BankAccount_Value IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("Reverse Lookup C_BankAccount_ID -> #" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		return true;
	}

	/**
	 * Reverse Lookup C_Project_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupC_Project_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_DataMigrationJP i ")
			.append("SET C_Project_ID=(SELECT C_Project_ID FROM C_Project p")
			.append(" WHERE i.JP_Project_Value=p.Value AND (i.AD_Client_ID=p.AD_Client_ID or p.AD_Client_ID = 0) ) ")
			.append("WHERE i.C_Project_ID IS NULL AND i.JP_Project_Value IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Invalid JP_Product_Value
		message = Msg.getMsg(getCtx(), "Error")  + Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "JP_Project_Value");
		sql = new StringBuilder ("UPDATE I_DataMigrationJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append("WHERE C_Project_ID IS NULL AND JP_Project_Value IS NOT NULL")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception( message +" : " + e.toString() +" : " + sql );
		}

		if(no > 0)
		{
			return false;
		}

		return true;

	}

	/**
	 * Reverse Lookup JP_Line_Project_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupJP_Line_Project_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_DataMigrationJP i ")
			.append("SET JP_Line_Project_ID=(SELECT C_Project_ID FROM C_Project p")
			.append(" WHERE i.JP_Line_Project_Value=p.Value AND  (i.AD_Client_ID=p.AD_Client_ID or p.AD_Client_ID = 0) ) ")
			.append("WHERE i.JP_Line_Project_ID IS NULL AND i.JP_Line_Project_Value IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Invalid JP_Line_Project_Value
		message = Msg.getMsg(getCtx(), "Error")  + Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "JP_Line_Project_Value");
		sql = new StringBuilder ("UPDATE I_DataMigrationJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append("WHERE JP_Line_Project_ID IS NULL AND JP_Line_Project_Value IS NOT NULL")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception( message +" : " + e.toString() +" : " + sql );
		}

		if(no > 0)
		{
			return false;
		}

		return true;

	}//reverseLookupJP_Line_Project_ID

	/**
	 * Reverse Lookup C_Campaign_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupC_Campaign_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_DataMigrationJP i ")
			.append("SET C_Campaign_ID=(SELECT C_Campaign_ID FROM C_Campaign p")
			.append(" WHERE i.JP_Campaign_Value=p.Value AND  (i.AD_Client_ID=p.AD_Client_ID or p.AD_Client_ID = 0) ) ")
			.append("WHERE i.C_Campaign_ID IS NULL AND i.JP_Campaign_Value IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " +  e.toString() +" : " + sql );
		}

		//Invalid JP_Campaign_Value
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_Campaign_Value");
		sql = new StringBuilder ("UPDATE I_DataMigrationJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append("WHERE C_Campaign_ID IS NULL AND JP_Campaign_Value IS NOT NULL")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(message +" : " +  e.toString() +" : " + sql );
		}

		if(no > 0)
		{
			return false;
		}

		return true;

	}//reverseLookupC_Campaign_ID


	/**
	 * Reverse Lookup JP_Line_Campaign_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupJP_Line_Campaign_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_DataMigrationJP i ")
			.append("SET JP_Line_Campaign_ID=(SELECT C_Campaign_ID FROM C_Campaign p")
			.append(" WHERE i.JP_Line_Campaign_Value=p.Value AND  (i.AD_Client_ID=p.AD_Client_ID or p.AD_Client_ID = 0) ) ")
			.append("WHERE i.JP_Line_Campaign_ID IS NULL AND i.JP_Line_Campaign_Value IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " +  e.toString() +" : " + sql );
		}

		//Invalid JP_Line_Campaign_Value
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_Line_Campaign_Value");
		sql = new StringBuilder ("UPDATE I_DataMigrationJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append("WHERE JP_Line_Campaign_ID IS NULL AND JP_Line_Campaign_Value IS NOT NULL")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(message +" : " +  e.toString() +" : " + sql );
		}

		if(no > 0)
		{
			return false;
		}

		return true;

	}//reverseLookupJP_Line_Campaign_ID

	/**
	 * Reverse Lookup C_Activity_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupC_Activity_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_DataMigrationJP i ")
			.append("SET C_Activity_ID=(SELECT C_Activity_ID FROM C_Activity p")
			.append(" WHERE i.JP_Activity_Value=p.Value AND  (i.AD_Client_ID=p.AD_Client_ID or p.AD_Client_ID = 0) ) ")
			.append("WHERE i.C_Activity_ID IS NULL AND i.JP_Activity_Value IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Invalid JP_Activity_Value
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_Activity_Value");
		sql = new StringBuilder ("UPDATE I_DataMigrationJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append("WHERE C_Activity_ID IS NULL AND JP_Activity_Value IS NOT NULL")
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

	}//reverseLookupC_Activity_ID


	/**
	 * Reverse Lookup JP_Line_Activity_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupJP_Line_Activity_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_DataMigrationJP i ")
			.append("SET JP_Line_Activity_ID=(SELECT C_Activity_ID FROM C_Activity p")
			.append(" WHERE i.JP_Line_Activity_Value=p.Value AND  (i.AD_Client_ID=p.AD_Client_ID or p.AD_Client_ID = 0) ) ")
			.append("WHERE i.JP_Line_Activity_ID IS NULL AND i.JP_Line_Activity_Value IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Invalid JP_Line_Activity_Value
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_Line_Activity_Value");
		sql = new StringBuilder ("UPDATE I_DataMigrationJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append("WHERE JP_Line_Activity_ID IS NULL AND JP_Line_Activity_Value IS NOT NULL")
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

	}//reverseLookupJP_Line_Activity_ID


	/**
	 * Reverse Lookup AD_OrgTrx_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupAD_OrgTrx_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_DataMigrationJP i ")
				.append("SET AD_OrgTrx_ID=(SELECT AD_Org_ID FROM AD_Org p")
				.append(" WHERE i.JP_OrgTrx_Value=p.Value AND (p.AD_Client_ID=i.AD_Client_ID or p.AD_Client_ID=0) AND p.IsSummary='N' ) ")
				.append(" WHERE i.JP_OrgTrx_Value IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message + " : "+ e.toString() + " : "+ sql );
		}

		//Invalid JP_OrgTrx_Value
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "JP_OrgTrx_Value");
		sql = new StringBuilder ("UPDATE I_DataMigrationJP ")
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
	 * Reverse Lookup JP_Line_OrgTrx_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupJP_Line_OrgTrx_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_DataMigrationJP i ")
				.append("SET JP_Line_OrgTrx_ID=(SELECT AD_Org_ID FROM AD_Org p")
				.append(" WHERE i.JP_Line_OrgTrx_Value=p.Value AND (p.AD_Client_ID=i.AD_Client_ID or p.AD_Client_ID=0) AND p.IsSummary='N' ) ")
				.append(" WHERE i.JP_Line_OrgTrx_Value IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message + " : "+ e.toString() + " : "+ sql );
		}

		//Invalid JP_Line_OrgTrx_Value
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "JP_Line_OrgTrx_Value");
		sql = new StringBuilder ("UPDATE I_DataMigrationJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append(" WHERE JP_Line_OrgTrx_ID IS NULL AND JP_Line_OrgTrx_Value IS NOT NULL ")
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

	}//reverseLookupJP_Line_OrgTrx_ID



	/**
	 * Reverse Lookup User1_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupUser1_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_DataMigrationJP i ")
			.append("SET User1_ID=(SELECT C_ElementValue_ID FROM C_ElementValue p")
			.append(" WHERE i.JP_UserElement1_Value=p.Value AND i.AD_Client_ID=p.AD_Client_ID) ")
			.append("WHERE i.User1_ID IS NULL AND i.JP_UserElement1_Value IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Invalid JP_UserElement1_Value
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "JP_UserElement1_Value");
		sql = new StringBuilder ("UPDATE I_DataMigrationJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append("WHERE User1_ID IS NULL AND JP_UserElement1_Value IS NOT NULL")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(message +" : " + e.toString() +" : " + sql);
		}

		if(no > 0)
		{
			return false;
		}

		return true;

	}//reverseLookupUser1_ID


	/**
	 * Reverse Lookup JP_Line_User1_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupJP_Line_User1_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_DataMigrationJP i ")
			.append("SET JP_Line_User1_ID=(SELECT C_ElementValue_ID FROM C_ElementValue p")
			.append(" WHERE i.JP_Line_UserElement1_Value=p.Value AND i.AD_Client_ID=p.AD_Client_ID) ")
			.append("WHERE i.JP_Line_User1_ID IS NULL AND i.JP_Line_UserElement1_Value IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Invalid JP_Line_UserElement1_Value
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "JP_Line_UserElement1_Value");
		sql = new StringBuilder ("UPDATE I_DataMigrationJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append("WHERE JP_Line_User1_ID IS NULL AND JP_Line_UserElement1_Value IS NOT NULL")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(message +" : " + e.toString() +" : " + sql);
		}

		if(no > 0)
		{
			return false;
		}

		return true;

	}//reverseLookupJP_Line_User1_ID


	/**
	 * Reverse Lookup User2_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupUser2_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_DataMigrationJP i ")
			.append("SET User2_ID=(SELECT C_ElementValue_ID FROM C_ElementValue p")
			.append(" WHERE i.JP_UserElement2_Value=p.Value AND i.AD_Client_ID=p.AD_Client_ID) ")
			.append("WHERE i.User2_ID IS NULL AND i.JP_UserElement2_Value IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Invalid JP_UserElement2_Value
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "JP_UserElement2_Value");
		sql = new StringBuilder ("UPDATE I_DataMigrationJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append("WHERE User2_ID IS NULL AND JP_UserElement2_Value IS NOT NULL")
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

	}//reverseLookupUser2_ID


	/**
	 * Reverse Lookup JP_Line_User2_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupJP_Line_User2_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_DataMigrationJP i ")
			.append("SET JP_Line_User2_ID=(SELECT C_ElementValue_ID FROM C_ElementValue p")
			.append(" WHERE i.JP_Line_UserElement2_Value=p.Value AND i.AD_Client_ID=p.AD_Client_ID) ")
			.append("WHERE i.JP_Line_User2_ID IS NULL AND i.JP_Line_UserElement2_Value IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Invalid JP_Line_UserElement2_Value
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "JP_Line_UserElement2_Value");
		sql = new StringBuilder ("UPDATE I_DataMigrationJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append("WHERE JP_Line_User2_ID IS NULL AND JP_Line_UserElement2_Value IS NOT NULL")
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

	}//reverseLookupJP_Line_User2_ID

	/**
	 * Reverse Lookup M_Product_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupM_Product_ID() throws Exception
	{
		int no = 0;

		//Value
		StringBuilder	sql = new StringBuilder ("UPDATE I_DataMigrationJP o ")
			  .append("SET M_Product_ID=(SELECT MAX(M_Product_ID) FROM M_Product p")
			  .append(" WHERE o.JP_Product_Value=p.Value AND o.AD_Client_ID=p.AD_Client_ID) ")
			  .append("WHERE M_Product_ID IS NULL AND JP_Product_Value IS NOT NULL")
			  .append(" AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}


		//Invalid
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "M_Product_ID");
		sql = new StringBuilder ("UPDATE I_DataMigrationJP ")
				.append("SET I_ErrorMsg='"+ message + "'")
				.append(" WHERE M_Product_ID IS NULL AND JP_Product_Value IS NOT NULL ")
				.append(" AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(message +" : " + e.toString() +" : " + sql);
		}

		if(no > 0)
		{
			return false;
		}

		return true;
	}


	/**
	 * Reverse Lookup C_Charge_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupC_Charge_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_DataMigrationJP o ")
			  .append("SET C_Charge_ID=(SELECT C_Charge_ID FROM C_Charge c")
			  .append(" WHERE o.JP_Charge_Name=c.Name AND o.AD_Client_ID=c.AD_Client_ID) ")
			  .append("WHERE C_Charge_ID IS NULL AND JP_Charge_Name IS NOT NULL AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Invalid JP_Charge_Name
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "JP_Charge_Name");
		sql = new StringBuilder ("UPDATE I_DataMigrationJP ")
				.append("SET I_ErrorMsg='"+ message + "'")
				.append(" WHERE C_Charge_ID IS NULL AND JP_Charge_Name IS NOT NULL ")
				.append(" AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(message +" : " + e.toString() +" : " + sql);
		}

		if(no > 0)
		{
			return false;
		}

		return true;

	}


	/**
	 * Reverse Lookup C_UOM_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupC_UOM_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_DataMigrationJP i ")
				.append("SET C_UOM_ID=(SELECT C_UOM_ID FROM C_UOM p")
				.append(" WHERE i.X12DE355=p.X12DE355 AND (i.AD_Client_ID=p.AD_Client_ID OR p.AD_Client_ID = 0) ) ")
				.append("WHERE X12DE355 IS NOT NULL")
				.append(" AND I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Invalid X12DE355
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "X12DE355");
		sql = new StringBuilder ("UPDATE I_DataMigrationJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append(" WHERE X12DE355 IS NOT NULL AND C_UOM_ID IS NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(message +" : " + e.toString() +" : " + sql);
		}

		if(no > 0)
		{
			return false;
		}

		return true;

	}//reverseLookupC_UOM_ID

	/**
	 * Reverse Lookup C_Tax_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupC_Tax_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_DataMigrationJP o ")
			  .append("SET C_Tax_ID=(SELECT MAX(C_Tax_ID) FROM C_Tax t")
			  .append(" WHERE o.TaxIndicator=t.TaxIndicator AND o.AD_Client_ID=t.AD_Client_ID) ")
			  .append("WHERE C_Tax_ID IS NULL AND TaxIndicator IS NOT NULL")
			  .append(" AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Invalid TaxIndicator
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "TaxIndicator");
		sql =  new StringBuilder ("UPDATE I_DataMigrationJP ")
				.append("SET I_ErrorMsg='"+ message + "'")
				.append(" WHERE C_Tax_ID IS NULL AND TaxIndicator IS NOT NULL")
				.append(" AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(message +" : " + e.toString() +" : " + sql);
		}

		if(no > 0)
		{
			return false;
		}

		return true;
	}


	/**
	 * Reverse Lookup JP_LocatorFrom_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupJP_LocatorFrom_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_DataMigrationJP i ")
			.append("SET JP_LocatorFrom_ID=(SELECT M_Locator_ID FROM M_Locator p")
			.append(" WHERE i.JP_LocatorFrom_Value=p.Value AND i.AD_Client_ID=p.AD_Client_ID) ")
			.append(" WHERE i.JP_LocatorFrom_ID IS NULL AND i.JP_LocatorFrom_Value IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Invalid JP_Locator_Value
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "JP_LocatorFrom_Value");
		sql = new StringBuilder ("UPDATE I_DataMigrationJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append(" WHERE JP_LocatorFrom_ID IS NULL AND JP_LocatorFrom_Value IS NOT NULL")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(message +" : " + e.toString() +" : " + sql);
		}

		if(no > 0)
		{
			return false;
		}

		return true;
	}


	/**
	 * Reverse Lookup JP_LocatorTo_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupJP_LocatorTo_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_DataMigrationJP i ")
			.append("SET JP_LocatorTo_ID=(SELECT M_Locator_ID FROM M_Locator p")
			.append(" WHERE i.JP_LocatorTo_Value=p.Value AND i.AD_Client_ID=p.AD_Client_ID) ")
			.append(" WHERE i.JP_LocatorTo_ID IS NULL AND i.JP_LocatorTo_Value IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Invalid JP_Locator_Value
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "JP_LocatorFrom_Value");
		sql = new StringBuilder ("UPDATE I_DataMigrationJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append(" WHERE JP_LocatorTo_ID IS NULL AND JP_LocatorTo_Value IS NOT NULL")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(message +" : " + e.toString() +" : " + sql);
		}

		if(no > 0)
		{
			return false;
		}

		return true;
	}

	/**
	 * Create Data Migration
	 *
	 * @param importDataMigration
	 * @param dataMigration
	 * @return
	 */
	private boolean createDataMigration(X_I_DataMigrationJP importDataMigration, MDataMigration dataMigration)
	{
		//Check AD_Org_ID
		if(importDataMigration.getAD_Org_ID() <= 0)
		{
			message = Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_Org_Value");
			return false;
		}

		ModelValidationEngine.get().fireImportValidate(this, importDataMigration, dataMigration, ImportValidator.TIMING_BEFORE_IMPORT);

		PO.copyValues(importDataMigration, dataMigration);
		dataMigration.setAD_Org_ID(importDataMigration.getAD_Org_ID());
		dataMigration.setDocumentNo(importDataMigration.getDocumentNo());
		dataMigration.setM_PriceList_ID(importDataMigration.getM_PriceList_ID());
		dataMigration.setC_PaymentTerm_ID(importDataMigration.getC_PaymentTerm_ID());
		if(importDataMigration.getDocBaseType().equals(MDocType.DOCBASETYPE_BankStatement))
		{
			;//TODO
		}else if(importDataMigration.getDocBaseType().equals(MDocType.DOCBASETYPE_PaymentAllocation)){
			dataMigration.setPayAmt(Env.ZERO);
		}

		ModelValidationEngine.get().fireImportValidate(this, importDataMigration, dataMigration, ImportValidator.TIMING_AFTER_IMPORT);

		try {
			dataMigration.saveEx(get_TrxName());
		}catch (Exception e) {
			message = Msg.getMsg(getCtx(),"SaveIgnored") + Msg.getElement(getCtx(), "JP_DataMigration_ID") +" : " + e.toString();
			return false;
		}

		importDataMigration.setJP_DataMigration_ID(dataMigration.getJP_DataMigration_ID());

		return true;
	}


	/**
	 * Create Data Migration Line
	 *
	 * @param importDataMigration
	 * @param dataMigration
	 * @param dataMigrationLine
	 * @return
	 */
	private boolean createDataMigrationLine(X_I_DataMigrationJP importDataMigration, MDataMigration dataMigration, MDataMigrationLine dataMigrationLine)
	{
		ModelValidationEngine.get().fireImportValidate(this, importDataMigration, dataMigrationLine, ImportValidator.TIMING_BEFORE_IMPORT);

		PO.copyValues(importDataMigration, dataMigrationLine);
		dataMigrationLine.setJP_DataMigrationLine_ID(0);
		dataMigrationLine.setJP_DataMigration_ID(dataMigration.getJP_DataMigration_ID());
		dataMigrationLine.setAD_Org_ID(importDataMigration.getAD_Org_ID());

		if(importDataMigration.getJP_Line_OrgTrx_ID() > 0)
		{
			dataMigrationLine.setAD_OrgTrx_ID(importDataMigration.getJP_Line_OrgTrx_ID());
		}else {
			dataMigrationLine.setAD_OrgTrx_ID(0);
		}

		if(importDataMigration.getJP_Line_Project_ID() > 0)
		{
			dataMigrationLine.setC_Project_ID(importDataMigration.getJP_Line_Project_ID());
		}else {
			dataMigrationLine.setC_Project_ID(0);
		}

		if(importDataMigration.getJP_Line_Activity_ID() > 0)
		{
			dataMigrationLine.setC_Activity_ID(importDataMigration.getJP_Line_Activity_ID());
		}else {
			dataMigrationLine.setC_Activity_ID(0);
		}

		if(importDataMigration.getJP_Line_Campaign_ID() > 0)
		{
			dataMigrationLine.setC_Campaign_ID(importDataMigration.getJP_Line_Campaign_ID());
		}else {
			dataMigrationLine.setC_Campaign_ID(0);
		}

		if(importDataMigration.getJP_Line_User1_ID() > 0)
		{
			dataMigrationLine.setUser1_ID(importDataMigration.getJP_Line_User1_ID());
		}else {
			dataMigrationLine.setUser1_ID(0);
		}

		if(importDataMigration.getJP_Line_User2_ID() > 0)
		{
			dataMigrationLine.setUser2_ID(importDataMigration.getJP_Line_User2_ID());
		}else {
			dataMigrationLine.setUser2_ID(0);
		}

		if(importDataMigration.getJP_Line_BPartner_ID() > 0)
		{
			dataMigrationLine.setC_BPartner_ID(importDataMigration.getJP_Line_BPartner_ID());
		}else {
			dataMigrationLine.setC_BPartner_ID(0);
		}

		if(importDataMigration.getDocBaseType().equals(MDocType.DOCBASETYPE_BankStatement))
		{
			;//TODO
		}else if(importDataMigration.getDocBaseType().equals(MDocType.DOCBASETYPE_PaymentAllocation)){
			dataMigrationLine.setAmount(importDataMigration.getPayAmt());
		}

		ModelValidationEngine.get().fireImportValidate(this, importDataMigration, dataMigrationLine, ImportValidator.TIMING_AFTER_IMPORT);

		try {
			dataMigrationLine.saveEx(get_TrxName());
		}catch (Exception e) {

			message = Msg.getMsg(getCtx(),"SaveIgnored") + Msg.getElement(getCtx(), "JP_DataMigrationLine_ID") +" : " + e.toString();
			return false;
		}

		importDataMigration.setJP_DataMigrationLine_ID(dataMigrationLine.getJP_DataMigrationLine_ID());

		return true;
	}
}
