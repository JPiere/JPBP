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

import org.adempiere.model.ImportValidator;
import org.adempiere.process.ImportProcess;
import org.adempiere.util.IProcessUI;
import org.compiere.model.MBPartner;
import org.compiere.model.MBPartnerLocation;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MUser;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.PO;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.X_I_InvoiceJP;

/**
 *	JPIERE-0412:Import Invoice from I_InvoiceJP
 *
 * 	@version 	2018-07-29
 *
 *  @author Hideaki Hagiwara
 */
public class JPiereImportInvoice extends SvrProcess  implements ImportProcess
{
	/**	Client to be imported to		*/
	private int				p_AD_Client_ID = 0;

	/**	Delete old Imported				*/
	private boolean			p_DeleteOldImported = false;
	/**	Only validate, don't import		*/
	private boolean	p_IsValidateOnly = false;
	/**	Document Action					*/
	private String			p_DocAction = MInvoice.DOCACTION_Prepare;

	/** Effective						*/
	private Timestamp		p_DateValue = null;

	private String message = null;

	private String p_JP_ImportSalesRepIdentifier = JPiereImportUser.JP_ImportUserIdentifier_Value;

	private String p_JP_ImportUserIdentifier = JPiereImportUser.JP_ImportUserIdentifier_Name;

	private IProcessUI processMonitor = null;

	private boolean p_IsHistoricalDataMigration = false;

	/**
	 *  Prepare - e.g., get Parameters.
	 */
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
			else if (name.equals("DocAction"))
				p_DocAction = (String)para[i].getParameter();
			else if (name.equals("JP_ImportSalesRepIdentifier"))
				p_JP_ImportSalesRepIdentifier = para[i].getParameterAsString();
			else if (name.equals("JP_ImportUserIdentifier"))
				p_JP_ImportUserIdentifier = para[i].getParameterAsString();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
		if (p_DateValue == null)
			p_DateValue = new Timestamp (System.currentTimeMillis());
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

		/** Delete Old Imported */
		if (p_DeleteOldImported)
		{
			sql = new StringBuilder ("DELETE FROM I_InvoiceJP ")
				  .append("WHERE I_IsImported='Y' ").append (getWhereClause());
			no = DB.executeUpdate(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("Delete Old Impored =" + no);
		}


		/** Reset I_ErrorMsg */
		sql = new StringBuilder ("UPDATE I_InvoiceJP ")
				.append("SET I_ErrorMsg='' ")
				.append(" WHERE I_IsImported<>'Y' ").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(String.valueOf(no));
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + sql );
		}

		ModelValidationEngine.get().fireImportValidate(this, null, null, ImportValidator.TIMING_BEFORE_VALIDATE);

		/** Reverse Lookup Surrogate Key */
		//Header
		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "DocumentNo");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupC_Invoice_ID())
			commitEx();
		else
			return message;

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "AD_Org_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupAD_Org_ID())
			commitEx();
		else
			return message;

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_DocType_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupC_DocType_ID())
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


		ModelValidationEngine.get().fireImportValidate(this, null, null, ImportValidator.TIMING_AFTER_VALIDATE);


		commitEx();
		if (p_IsValidateOnly)
		{
			return "Validated";
		}


		/** Create Invoice */
		message = Msg.getMsg(getCtx(), "CreateNew") + " : " + Msg.getElement(getCtx(), "C_Invoice_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);

		sql = new StringBuilder ("SELECT * FROM I_InvoiceJP ")
			  .append("WHERE I_IsImported='N'").append (getWhereClause())
			.append(" ORDER BY DateInvoiced, C_BPartner_ID, C_BPartner_Location_ID, DocumentNo, I_InvoiceJP_ID");

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
			pstmt = DB.prepareStatement (sql.toString(), get_TrxName());
			rs = pstmt.executeQuery ();
			//
			int lastC_BPartner_ID = 0;
			int lastC_BPartner_Location_ID = 0;
			String lastDocumentNo = "";
			//
			MInvoice invoice = null;
			MInvoiceLine line = null;
			X_I_InvoiceJP imp = null;
			int lineNo = 0;
			boolean isCreateHeader= true;

			while (rs.next ())
			{
				recordsNum++;

				imp = new X_I_InvoiceJP (getCtx (), rs, get_TrxName());

				//Re-Import
				if(imp.getC_Invoice_ID() > 0)
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

				String cmpDocumentNo = imp.getDocumentNo();
				if (cmpDocumentNo == null)
					cmpDocumentNo = "";

				//	New Invoice
				isCreateHeader= true;
				if (lastC_BPartner_ID != imp.getC_BPartner_ID()
					|| lastC_BPartner_Location_ID != imp.getC_BPartner_Location_ID()
					|| !lastDocumentNo.equals(cmpDocumentNo))
				{
					if (invoice != null)
					{

						if(!Util.isEmpty(p_DocAction))
						{
							if(!invoice.processIt(p_DocAction))
							{
								rollback();
								message = "Invoice Process Failed: " + invoice.getProcessMsg();
								invoice = null;

								imp.setI_ErrorMsg(Msg.getMsg(getCtx(), "Error") + message);
								imp.saveEx(get_TrxName());
								commitEx();
							}
						}


						if(invoice != null)
						{
							invoice.saveEx(get_TrxName());
							invoice = null;
							commitEx();
						}

					}

					lastC_BPartner_ID = imp.getC_BPartner_ID();
					lastC_BPartner_Location_ID = imp.getC_BPartner_Location_ID();
					lastDocumentNo = imp.getDocumentNo();
					if (lastDocumentNo == null)
						lastDocumentNo = "";

				}else {

					isCreateHeader = false;
				}

				if(isCreateHeader)
				{
					invoice = new MInvoice (getCtx(), 0, get_TrxName());
					lineNo = 0;

					if(createInvoiceHeader(imp, invoice))
					{
						successCreateDocHeader++;
					}else {

						rollback();
						invoice = null;

						failureCreateDocHeader++;
						errorNum++;//Error of Header include number of Error.
						imp.setI_ErrorMsg(message);
						imp.setI_IsImported(false);
						imp.setProcessed(false);
						imp.saveEx(get_TrxName());
						commitEx();
						continue;
					}
				}

				if(invoice == null)
				{
					rollback();
					errorNum++;
					String msg = Msg.getMsg(getCtx(), "JP_UnexpectedError");
					imp.setI_ErrorMsg(msg);
					imp.setI_IsImported(false);
					imp.setProcessed(false);
					imp.saveEx(get_TrxName());
					commitEx();;
					continue;
				}


				imp.setC_Invoice_ID(invoice.getC_Invoice_ID());

				//Create Invoice Line
				line = new MInvoiceLine(invoice);
				lineNo = lineNo + 10;

				if(addInvoiceLine(imp, invoice,line, lineNo))
				{
					successCreateDocLine++;
					successNum++;

				}else {

					rollback();
					invoice = null;

					failureCreateDocLine++;
					errorNum++;//Error of Line include number of Error.

					imp.setI_ErrorMsg(message);
					imp.setI_IsImported(false);
					imp.setProcessed(false);
					imp.saveEx(get_TrxName());
					commitEx();
					continue;

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

			//last Journal
			if (invoice != null)
			{
				if(p_IsHistoricalDataMigration)
				{
					invoice.setDocStatus(DocAction.STATUS_Closed);
					invoice.setDocAction(DocAction.ACTION_None);
					invoice.setProcessed(true);
					invoice.setPosted(true);

				}else {

					if(!Util.isEmpty(p_DocAction))
					{
						if(!invoice.processIt (p_DocAction))
						{
							rollback();
							message = "Invoice Process Failed: " + invoice.getProcessMsg();
							invoice = null;

							imp.setI_ErrorMsg(Msg.getMsg(getCtx(), "Error") + message);
							imp.saveEx(get_TrxName());
							commitEx();
						}
					}
				}

				if(invoice != null)
				{
					invoice.saveEx(get_TrxName());
					invoice = null;
					commitEx();
				}

			}//if (invoice != null)

		}catch (Exception e){

			log.log(Level.SEVERE, sql.toString(), e);
			throw e;

		}finally{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}


		message = records + " : " + recordsNum + " = "
				+ skipRecords + " : " + skipNum + " + "
				+ errorRecords + " : " + errorNum + " + "
				+ success + " : " + successNum
				+ "   [" + detail +" --> "
				+ createHeader + "( "+  success + " : " + successCreateDocHeader + "  /  " +  failure + " : " + failureCreateDocHeader + " ) + "
				+ createLine  + " ( "+  success + " : " + successCreateDocLine + "  /  " +  failure + " : " + failureCreateDocLine+ " ) ]";

		return message;
	}	//	doIt


	@Override
	public String getWhereClause() {
		StringBuilder msgreturn = new StringBuilder(" AND AD_Client_ID=").append(p_AD_Client_ID);
		return msgreturn.toString();
	}


	@Override
	public String getImportTableName() {
		return X_I_InvoiceJP.Table_Name;
	}


	/**
	 * Reverse Lookup C_Invoice_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupC_Invoice_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_InvoiceJP i ")
				.append("SET C_Invoice_ID=(SELECT MAX(C_Invoice_ID) FROM C_Invoice p")
				.append(" WHERE i.DocumentNo=p.DocumentNo AND p.AD_Client_ID=i.AD_Client_ID ) ")
				.append(" WHERE i.DocumentNo IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("Reverse Lookup C_Invoice_ID -> #" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}

		return true;

	}//reverseLookupC_Invoice_ID

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
		StringBuilder sql = new StringBuilder ("UPDATE I_InvoiceJP i ")
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
		sql = new StringBuilder ("UPDATE I_InvoiceJP ")
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
	 *  Reverse Lookup C_DocType_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupC_DocType_ID() throws Exception
	{
		int no = 0;

		//Lookup -DocType of PO from JP_DocType_Name
		 StringBuilder sql = new StringBuilder ("UPDATE I_InvoiceJP o ")	//	PO Document Type Name
			  .append("SET C_DocType_ID=(SELECT C_DocType_ID FROM C_DocType d WHERE d.Name=o.JP_DocType_Name")
			  .append(" AND d.DocBaseType='API' AND o.AD_Client_ID=d.AD_Client_ID) ")
			  .append("WHERE C_DocType_ID IS NULL AND IsSOTrx='N' AND JP_DocType_Name IS NOT NULL AND I_IsImported<>'Y'").append (getWhereClause());

		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}

		//Lookup -DocType of SO from JP_DocType_Name
		sql = new StringBuilder ("UPDATE I_InvoiceJP o ")	//	SO Document Type Name
			  .append("SET C_DocType_ID=(SELECT C_DocType_ID FROM C_DocType d WHERE d.Name=o.JP_DocType_Name")
			  .append(" AND d.DocBaseType='ARI' AND o.AD_Client_ID=d.AD_Client_ID) ")
			  .append("WHERE C_DocType_ID IS NULL AND IsSOTrx='Y' AND JP_DocType_Name IS NOT NULL AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}

		//Loolup - C_DocType_ID from JP_DocType_Name
		sql = new StringBuilder ("UPDATE I_InvoiceJP o ")
			  .append("SET C_DocType_ID=(SELECT C_DocType_ID FROM C_DocType d WHERE d.Name=o.JP_DocType_Name")
			  .append(" AND d.DocBaseType IN ('API','ARI','APC','ARC') AND o.AD_Client_ID=d.AD_Client_ID) ")
			  .append("WHERE C_DocType_ID IS NULL AND JP_DocType_Name IS NOT NULL AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}

		//Invalid JP_DocType_Name
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_DocType_Name");
		sql = new StringBuilder ("UPDATE I_InvoiceJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append(" WHERE C_DocType_ID IS NULL AND JP_DocType_Name IS NOT NULL  ")
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

		//Set Default DocType of PO When C_DocType_ID is null
		sql = new StringBuilder ("UPDATE I_InvoiceJP o ")	//	Default PO
			  .append("SET C_DocType_ID=(SELECT MAX(C_DocType_ID) FROM C_DocType d WHERE d.IsDefault='Y'")
			  .append(" AND d.DocBaseType='API' AND o.AD_Client_ID=d.AD_Client_ID) ")
			  .append("WHERE C_DocType_ID IS NULL AND IsSOTrx='N' AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}

		//Set Default DocType of SO When C_DocType_ID is null
		sql = new StringBuilder ("UPDATE I_InvoiceJP o ")
			  .append("SET C_DocType_ID=(SELECT MAX(C_DocType_ID) FROM C_DocType d WHERE d.IsDefault='Y'")
			  .append(" AND d.DocBaseType='ARI' AND o.AD_Client_ID=d.AD_Client_ID) ")
			  .append("WHERE C_DocType_ID IS NULL AND IsSOTrx='Y' AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}

		//Set Default DocType When C_DocType_ID is null
		sql = new StringBuilder ("UPDATE I_InvoiceJP o ")
			  .append("SET C_DocType_ID=(SELECT MAX(C_DocType_ID) FROM C_DocType d WHERE d.IsDefault='Y'")
			  .append(" AND d.DocBaseType IN('API','ARI') AND o.AD_Client_ID=d.AD_Client_ID) ")
			  .append("WHERE C_DocType_ID IS NULL AND IsSOTrx IS NULL AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}

		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "JP_Null")+Msg.getElement(getCtx(), "C_DocType_ID");
		sql =  new StringBuilder ("UPDATE I_InvoiceJP ")
			  .append("SET I_ErrorMsg='"+ message + "'")
			  .append(" WHERE C_DocType_ID IS NULL")
			  .append(" AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}

		//	Set IsSOTrx
		sql = new StringBuilder ("UPDATE I_InvoiceJP o SET IsSOTrx='Y' ")
			  .append("WHERE EXISTS (SELECT * FROM C_DocType d WHERE o.C_DocType_ID=d.C_DocType_ID AND d.DocBaseType='ARI' AND o.AD_Client_ID=d.AD_Client_ID)")
			  .append(" AND C_DocType_ID IS NOT NULL")
			  .append(" AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}

		sql = new StringBuilder ("UPDATE I_InvoiceJP o SET IsSOTrx='N' ")
			  .append("WHERE EXISTS (SELECT * FROM C_DocType d WHERE o.C_DocType_ID=d.C_DocType_ID AND d.DocBaseType='API' AND o.AD_Client_ID=d.AD_Client_ID)")
			  .append(" AND C_DocType_ID IS NOT NULL")
			  .append(" AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}

		return true;
	}

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
			sql = new StringBuilder ("UPDATE I_InvoiceJP i ")
					.append("SET SalesRep_ID=(SELECT MAX(AD_User_ID) FROM AD_User p INNER JOIN C_BPartner bp ON (p.C_BPartner_ID = bp.C_BPartner_ID) ")
					.append(" WHERE i.JP_SalesRep_EMail=p.EMail AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) AND bp.IsSalesRep='Y' ) ")
					.append(" WHERE i.JP_SalesRep_EMail IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportSalesRepIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_Name)) { //Name

			sql = new StringBuilder ("UPDATE I_InvoiceJP i ")
					.append("SET SalesRep_ID=(SELECT MAX(AD_User_ID) FROM AD_User p INNER JOIN C_BPartner bp ON (p.C_BPartner_ID = bp.C_BPartner_ID) ")
					.append(" WHERE i.JP_SalesRep_Name=p.Name AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) AND bp.IsSalesRep='Y') ")
					.append(" WHERE i.JP_SalesRep_Name IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportSalesRepIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_Value)) { //Value

			sql = new StringBuilder ("UPDATE I_InvoiceJP i ")
					.append("SET SalesRep_ID=(SELECT MAX(AD_User_ID) FROM AD_User p INNER JOIN C_BPartner bp ON (p.C_BPartner_ID = bp.C_BPartner_ID) ")
					.append(" WHERE i.JP_SalesRep_Value=p.Value AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) AND bp.IsSalesRep='Y'  ) ")
					.append(" WHERE i.JP_SalesRep_Value IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportSalesRepIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueEMail)) { //Value + E-Mail

			//In case of EMail is not null
			sql = new StringBuilder ("UPDATE I_InvoiceJP i ")
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
			sql = new StringBuilder ("UPDATE I_InvoiceJP i ")
					.append("SET SalesRep_ID=(SELECT MAX(AD_User_ID) FROM AD_User p INNER JOIN C_BPartner bp ON (p.C_BPartner_ID = bp.C_BPartner_ID) ")
					.append(" WHERE i.JP_SalesRep_Value=p.Value AND i.JP_SalesRep_EMail IS NULL l AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) AND bp.IsSalesRep='Y' ) ")
					.append(" WHERE i.JP_SalesRep_Value IS NOT NULL AND i.JP_SalesRep_EMail IS NULL AND i.SalesRep_ID IS NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());


		}else if(p_JP_ImportSalesRepIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueName)) { //Value + Name

			sql = new StringBuilder ("UPDATE I_InvoiceJP i ")
					.append("SET SalesRep_ID=(SELECT MAX(AD_User_ID) FROM AD_User p INNER JOIN C_BPartner bp ON (p.C_BPartner_ID = bp.C_BPartner_ID) ")
					.append(" WHERE i.JP_SalesRep_Value=p.Value AND i.JP_SalesRep_Name=p.Name AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) AND bp.IsSalesRep='Y'  ) ")
					.append(" WHERE i.JP_SalesRep_Value IS NOT NULL AND i.JP_SalesRep_Name IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportSalesRepIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueNameEmail)) { //Value + Name + EMail

			//In case of EMail is not null
			sql = new StringBuilder ("UPDATE I_InvoiceJP i ")
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
			sql = new StringBuilder ("UPDATE I_InvoiceJP i ")
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
	 * Reverse Lookup M_PriceList_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupM_PriceList_ID() throws Exception
	{
		int no = 0;

		//Set M_PriceList_ID from JP_PriceList_Name
		StringBuilder sql = new StringBuilder ("UPDATE I_InvoiceJP o ")
				  .append("SET M_PriceList_ID=(SELECT MAX(M_PriceList_ID) FROM M_PriceList p ")
				  .append(" WHERE p.Name=o.JP_PriceList_Name AND o.AD_Client_ID=p.AD_Client_ID) ")
				  .append(" WHERE M_PriceList_ID IS NULL AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Set C_Currency_ID from  M_PriceList_ID
		sql = new StringBuilder ("UPDATE I_InvoiceJP o ")
				  .append("SET C_Currency_ID=(SELECT C_Currency_ID FROM M_PriceList p ")
				  .append(" WHERE p.M_PriceList_ID=o.M_PriceList_ID ) ")
				  .append(" WHERE I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Set M_PriceList_ID from Default
		sql = new StringBuilder ("UPDATE I_InvoiceJP o ")
				  .append("SET M_PriceList_ID=(SELECT MAX(M_PriceList_ID) FROM M_PriceList p WHERE p.IsDefault='Y'")
				  .append(" AND p.C_Currency_ID=o.C_Currency_ID AND p.IsSOPriceList=o.IsSOTrx AND o.AD_Client_ID=p.AD_Client_ID) ")
				  .append("WHERE M_PriceList_ID IS NULL AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Set M_PriceList_ID from Default
		sql = new StringBuilder ("UPDATE I_InvoiceJP o ")
			  .append("SET M_PriceList_ID=(SELECT MAX(M_PriceList_ID) FROM M_PriceList p WHERE p.IsDefault='Y'")
			  .append(" AND p.IsSOPriceList=o.IsSOTrx AND o.AD_Client_ID=p.AD_Client_ID) ")
			  .append("WHERE M_PriceList_ID IS NULL AND C_Currency_ID IS NULL AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		sql = new StringBuilder ("UPDATE I_InvoiceJP o ")
			  .append("SET M_PriceList_ID=(SELECT MAX(M_PriceList_ID) FROM M_PriceList p ")
			  .append(" WHERE p.C_Currency_ID=o.C_Currency_ID AND p.IsSOPriceList=o.IsSOTrx AND o.AD_Client_ID=p.AD_Client_ID) ")
			  .append("WHERE M_PriceList_ID IS NULL AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		sql = new StringBuilder ("UPDATE I_InvoiceJP o ")
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
		sql = new StringBuilder ("UPDATE I_InvoiceJP ")
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

	private boolean reverseLookupC_Project_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_InvoiceJP i ")
			.append("SET C_Project_ID=(SELECT C_Project_ID FROM C_Project p")
			.append(" WHERE i.JP_Project_Value=p.Value AND i.AD_Client_ID=p.AD_Client_ID) ")
			.append("WHERE i.C_Project_ID IS NULL AND i.JP_Project_Value IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Invalid JP_Product_Value
		message = Msg.getMsg(getCtx(), "Error")  + Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "JP_Project_Value");
		sql = new StringBuilder ("UPDATE I_InvoiceJP ")
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

		StringBuilder sql = new StringBuilder ("UPDATE I_InvoiceJP i ")
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
		sql = new StringBuilder ("UPDATE I_InvoiceJP ")
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

		StringBuilder sql = new StringBuilder ("UPDATE I_InvoiceJP i ")
			.append("SET C_Campaign_ID=(SELECT C_Campaign_ID FROM C_Campaign p")
			.append(" WHERE i.JP_Campaign_Value=p.Value AND i.AD_Client_ID=p.AD_Client_ID) ")
			.append("WHERE i.C_Campaign_ID IS NULL AND i.JP_Campaign_Value IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " +  e.toString() +" : " + sql );
		}

		//Invalid JP_Campaign_Value
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_Campaign_Value");
		sql = new StringBuilder ("UPDATE I_InvoiceJP ")
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

		StringBuilder sql = new StringBuilder ("UPDATE I_InvoiceJP i ")
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
		sql = new StringBuilder ("UPDATE I_InvoiceJP ")
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

		StringBuilder sql = new StringBuilder ("UPDATE I_InvoiceJP i ")
			.append("SET C_Activity_ID=(SELECT C_Activity_ID FROM C_Activity p")
			.append(" WHERE i.JP_Activity_Value=p.Value AND i.AD_Client_ID=p.AD_Client_ID) ")
			.append("WHERE i.C_Activity_ID IS NULL AND i.JP_Activity_Value IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Invalid JP_Activity_Value
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_Activity_Value");
		sql = new StringBuilder ("UPDATE I_InvoiceJP ")
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

		StringBuilder sql = new StringBuilder ("UPDATE I_InvoiceJP i ")
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
		sql = new StringBuilder ("UPDATE I_InvoiceJP ")
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

		StringBuilder sql = new StringBuilder ("UPDATE I_InvoiceJP i ")
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
		sql = new StringBuilder ("UPDATE I_InvoiceJP ")
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

		StringBuilder sql = new StringBuilder ("UPDATE I_InvoiceJP i ")
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
		sql = new StringBuilder ("UPDATE I_InvoiceJP ")
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

		StringBuilder sql = new StringBuilder ("UPDATE I_InvoiceJP i ")
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
		sql = new StringBuilder ("UPDATE I_InvoiceJP ")
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

		StringBuilder sql = new StringBuilder ("UPDATE I_InvoiceJP i ")
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
		sql = new StringBuilder ("UPDATE I_InvoiceJP ")
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

		StringBuilder sql = new StringBuilder ("UPDATE I_InvoiceJP i ")
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
		sql = new StringBuilder ("UPDATE I_InvoiceJP ")
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

		StringBuilder sql = new StringBuilder ("UPDATE I_InvoiceJP i ")
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
		sql = new StringBuilder ("UPDATE I_InvoiceJP ")
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
	 * Reverse Lookup C_PaymentTerm_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupC_PaymentTerm_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_InvoiceJP o ")
			  .append("SET C_PaymentTerm_ID=(SELECT C_PaymentTerm_ID FROM C_PaymentTerm p")
			  .append(" WHERE o.JP_PaymentTerm_Value=p.Value AND o.AD_Client_ID=p.AD_Client_ID) ")
			  .append("WHERE C_PaymentTerm_ID IS NULL AND JP_PaymentTerm_Value IS NOT NULL AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Set Default
		sql = new StringBuilder ("UPDATE I_InvoiceJP o ")
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
		sql = new StringBuilder ("UPDATE I_InvoiceJP ")
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
		StringBuilder  sql = new StringBuilder ("UPDATE I_InvoiceJP i ")
			.append("SET C_BPartner_ID=(SELECT C_BPartner_ID FROM C_BPartner p")
			.append(" WHERE i.JP_BPartner_Value=p.Value AND i.AD_Client_ID=p.AD_Client_ID) ")
			.append("WHERE i.C_BPartner_ID IS NULL AND i.JP_BPartner_Value IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}


		//Invalid JP_BPartner_Value
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_BPartner_Value");
		sql = new StringBuilder ("UPDATE I_InvoiceJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append("WHERE C_BPartner_ID IS NULL ")
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

		StringBuilder sql = new StringBuilder ("UPDATE I_InvoiceJP i ")
				.append("SET C_BPartner_Location_ID=(SELECT C_BPartner_Location_ID FROM C_BPartner_Location p")
				.append(" WHERE i.JP_BPartner_Location_Name=p.Name AND i.C_BPartner_ID=p.C_BPartner_ID) ")
				.append("WHERE i.C_BPartner_Location_ID IS NULL AND i.JP_BPartner_Location_Name IS NOT NULL ")
				.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		sql = new StringBuilder ("UPDATE I_InvoiceJP i ")
				.append("SET C_BPartner_Location_ID=(SELECT max(C_BPartner_Location_ID) FROM C_BPartner_Location p")
				.append(" WHERE i.C_BPartner_ID=p.C_BPartner_ID AND i.Phone = p.phone) ")
				.append(" WHERE i.C_BPartner_Location_ID IS NULL AND i.JP_BPartner_Location_Name IS NULL ")
				.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Invalid JP_BPartner_Value
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_BPartner_Location_Name");
		sql = new StringBuilder ("UPDATE I_InvoiceJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append("WHERE C_BPartner_Location_ID IS NULL ")
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
			sql = new StringBuilder ("UPDATE I_InvoiceJP i ")
					.append("SET AD_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_User_EMail=p.EMail AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.C_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.EMail IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_Name)) { //Name

			sql = new StringBuilder ("UPDATE I_InvoiceJP i ")
					.append("SET AD_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_User_Name=p.Name AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.C_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_User_Name IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_Value)) { //Value

			sql = new StringBuilder ("UPDATE I_InvoiceJP i ")
					.append("SET AD_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_User_Value=p.Value AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.C_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_User_Value IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueEMail)) { //Value + E-Mail

			//In case of EMail is not null
			sql = new StringBuilder ("UPDATE I_InvoiceJP i ")
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
			sql = new StringBuilder ("UPDATE I_InvoiceJP i ")
					.append("SET AD_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_User_Value=p.Value AND i.JP_User_EMail IS NULL AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.C_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_User_Value IS NOT NULL AND i.JP_User_EMail IS NULL AND i.AD_User_ID IS NOT NULL ")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueName)) { //Value + Name

			sql = new StringBuilder ("UPDATE I_InvoiceJP i ")
					.append("SET AD_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_User_Value=p.Value AND i.JP_User_Name=p.Name AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.C_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_User_Value IS NOT NULL AND i.JP_User_Name IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueNameEmail)) { //Value + Name + EMail

			//In case of EMail is not null
			sql = new StringBuilder ("UPDATE I_InvoiceJP i ")
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
			sql = new StringBuilder ("UPDATE I_InvoiceJP i ")
					.append("SET AD_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_User_Value=p.Value AND i.JP_User_Name=p.Name  AND i.JP_User_EMail IS NULL AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 )")
					.append(" AND i.C_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_User_Value IS NOT NULL AND i.JP_User_Name IS NOT NULL AND i.JP_User_EMail IS NULL AND i.AD_User_ID IS NULL")
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
	 * Reverse Lookup M_Product_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupM_Product_ID() throws Exception
	{
		int no = 0;

		//Value
		StringBuilder	sql = new StringBuilder ("UPDATE I_InvoiceJP o ")
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
		sql = new StringBuilder ("UPDATE I_InvoiceJP ")
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

		StringBuilder sql = new StringBuilder ("UPDATE I_InvoiceJP o ")
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
		sql = new StringBuilder ("UPDATE I_InvoiceJP ")
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

		StringBuilder sql = new StringBuilder ("UPDATE I_InvoiceJP i ")
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
		sql = new StringBuilder ("UPDATE I_InvoiceJP ")
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

		StringBuilder sql = new StringBuilder ("UPDATE I_InvoiceJP o ")
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
		sql =  new StringBuilder ("UPDATE I_InvoiceJP ")
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
	 * Create Invoice Header
	 *
	 * @param impInvoice
	 * @param invoice
	 * @return
	 */
	private boolean createInvoiceHeader(X_I_InvoiceJP impInvoice, MInvoice invoice)
	{
		ModelValidationEngine.get().fireImportValidate(this, impInvoice, invoice, ImportValidator.TIMING_BEFORE_IMPORT);

		PO.copyValues(impInvoice, invoice);
		invoice.setC_Invoice_ID(0);
		invoice.setClientOrg (impInvoice.getAD_Client_ID(), impInvoice.getAD_Org_ID());
		invoice.setC_DocTypeTarget_ID(impInvoice.getC_DocType_ID());
		invoice.setIsSOTrx(impInvoice.isSOTrx());

		//Date
		if(impInvoice.getDateInvoiced() == null)
		{
			impInvoice.setDateInvoiced(p_DateValue);
			invoice.setDateInvoiced(p_DateValue);
		}

		if(impInvoice.getDateAcct() == null)
		{
			impInvoice.setDateAcct(p_DateValue);
			invoice.setDateAcct(p_DateValue);
		}


		//Busienss Partner
		MBPartner bp = null;
		if(impInvoice.getC_BPartner_ID() > 0)
		{
			bp =MBPartner.get(getCtx(), impInvoice.getC_BPartner_ID());

		}else {
			message = Msg.getMsg(getCtx(), "JP_Null") + Msg.getElement(getCtx(), "C_BPartner_ID");
			return false;
		}

		if(impInvoice.getC_BPartner_Location_ID() > 0)
		{
			;
		}else {

			MBPartnerLocation[] locations = bp.getLocations(false);
			if(locations != null && locations.length == 1 )
			{
				impInvoice.setC_BPartner_Location_ID(locations[0].getC_BPartner_Location_ID());
				invoice.setC_BPartner_Location_ID(locations[0].getC_BPartner_Location_ID());
			}
		}

		if(impInvoice.getAD_User_ID() > 0)
		{
			;
		}else {

			MUser[] users =MUser.getOfBPartner(getCtx(), impInvoice.getC_BPartner_ID(),get_TrxName());
			if(users != null && users.length == 1 )
			{
				impInvoice.setAD_User_ID(users[0].getAD_User_ID());
				invoice.setAD_User_ID(users[0].getAD_User_ID());
			}

		}



		if(impInvoice.getM_PriceList_ID() == 0)
		{
			if(bp.getM_PriceList_ID() == 0)
			{
				;//set Default

			}else {
				impInvoice.setM_PriceList_ID(bp.getM_PriceList_ID());
				invoice.setM_PriceList_ID(bp.getM_PriceList_ID());
			}
		}

		if(Util.isEmpty(impInvoice.getPaymentRule()))
		{
			if(Util.isEmpty(bp.getPaymentRule()))
			{
				//set Default
				impInvoice.setPaymentRule(X_I_InvoiceJP.PAYMENTRULE_OnCredit);
				invoice.setPaymentRule(X_I_InvoiceJP.PAYMENTRULE_OnCredit);

			}else {
				impInvoice.setPaymentRule(bp.getPaymentRule());
				invoice.setPaymentRule(bp.getPaymentRule());
			}
		}

		if(impInvoice.getC_PaymentTerm_ID() == 0)
		{
			if(bp.getC_PaymentTerm_ID() == 0)
			{
				;//set Default

			}else {
				impInvoice.setC_PaymentTerm_ID(bp.getC_PaymentTerm_ID());
				invoice.setC_PaymentTerm_ID(bp.getC_PaymentTerm_ID());
			}
		}


		//DocStatus
		invoice.setDocStatus(DocAction.STATUS_Drafted);
		invoice.setDocAction(DocAction.ACTION_Complete);


		ModelValidationEngine.get().fireImportValidate(this, impInvoice, invoice, ImportValidator.TIMING_AFTER_IMPORT);

		try {
			invoice.saveEx(get_TrxName());
		}catch (Exception e) {

		    message = Msg.getMsg(getCtx(),"SaveIgnored") + Msg.getElement(getCtx(), "C_Invoice_ID") +" : " + e.toString();

			return false;
		}

		impInvoice.setC_Invoice_ID(invoice.getC_Invoice_ID());

		return true;

	}

	/**
	 *
	 * add Invoice Line
	 *
	 * @param impInvoice
	 * @param invoice
	 * @param line
	 * @param lineNo
	 * @return
	 */
	private boolean addInvoiceLine(X_I_InvoiceJP impInvoice, MInvoice invoice, MInvoiceLine line, int lineNo)
	{
		ModelValidationEngine.get().fireImportValidate(this, impInvoice, line, ImportValidator.TIMING_BEFORE_IMPORT);

		PO.copyValues(impInvoice, line);
		line.setDescription(null);

		line.setC_Invoice_ID(invoice.getC_Invoice_ID());

		if(impInvoice.getLine()==0)
		{
			line.setLine(lineNo);
			impInvoice.setLine(lineNo);

		}else {
			line.setLine(impInvoice.getLine());
		}

		if(!Util.isEmpty(impInvoice.getLineDescription()))
			line.setDescription(impInvoice.getLineDescription());

		if(line.getQtyEntered().compareTo(Env.ZERO) == 0 && line.getQtyInvoiced().compareTo(Env.ZERO) != 0 )
		{
			line.setQtyEntered(line.getQtyInvoiced());

		}else if(line.getQtyEntered().compareTo(Env.ZERO) != 0 && line.getQtyInvoiced().compareTo(Env.ZERO) == 0 ) {

			line.setQtyInvoiced(line.getQtyEntered());
		}

		if(impInvoice.getJP_Line_OrgTrx_ID() > 0)
		{
			line.setAD_OrgTrx_ID(impInvoice.getJP_Line_OrgTrx_ID());
		}else {
			line.setAD_OrgTrx_ID(0);
		}

		if(impInvoice.getJP_Line_Project_ID() > 0)
		{
			line.setC_Project_ID(impInvoice.getJP_Line_Project_ID());
		}else {
			line.setC_Project_ID(0);
		}

		if(impInvoice.getJP_Line_Activity_ID() > 0)
		{
			line.setC_Activity_ID(impInvoice.getJP_Line_Activity_ID());
		}else {
			line.setC_Activity_ID(0);
		}

		if(impInvoice.getJP_Line_Campaign_ID() > 0)
		{
			line.setC_Campaign_ID(impInvoice.getJP_Line_Campaign_ID());
		}else {
			line.setC_Campaign_ID(0);
		}

		if(impInvoice.getJP_Line_User1_ID() > 0)
		{
			line.setUser1_ID(impInvoice.getJP_Line_User1_ID());
		}else {
			line.setUser1_ID(0);
		}

		if(impInvoice.getJP_Line_User2_ID() > 0)
		{
			line.setUser2_ID(impInvoice.getJP_Line_User2_ID());
		}else {
			line.setUser2_ID(0);
		}

		ModelValidationEngine.get().fireImportValidate(this, impInvoice, line, ImportValidator.TIMING_AFTER_IMPORT);

		try {
			line.saveEx(get_TrxName());
		}catch (Exception e) {

			message = Msg.getMsg(getCtx(),"SaveIgnored") + Msg.getElement(getCtx(), "C_InvoiceLine_ID") +" : " + e.toString();

			return false;
		}

		impInvoice.setC_InvoiceLine_ID(line.getC_InvoiceLine_ID());
		impInvoice.setI_IsImported(true);
		impInvoice.setProcessed(true);
		impInvoice.saveEx(get_TrxName());

		return true;
	}


}	//	ImportInvoice
