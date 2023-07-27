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
import java.util.ArrayList;
import java.util.logging.Level;

import org.adempiere.model.ImportValidator;
import org.adempiere.process.ImportProcess;
import org.adempiere.util.IProcessUI;
import org.adempiere.util.ProcessUtil;
import org.compiere.model.MBPartner;
import org.compiere.model.MBPartnerLocation;
import org.compiere.model.MPriceList;
import org.compiere.model.MPriceListVersion;
import org.compiere.model.MProductPrice;
import org.compiere.model.MUser;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.PO;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfo;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Trx;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.MEstimation;
import jpiere.base.plugin.org.adempiere.model.MEstimationLine;
import jpiere.base.plugin.org.adempiere.model.X_I_BPartnerJP;
import jpiere.base.plugin.org.adempiere.model.X_I_EstimationJP;

/**
 *  JPIERE-0289
 *	Import Estimation from I_EstimationJP
 *  @author Hideaki Hagiwara
 *  
 *  Before  2023.7.27, Ken Watanabe was author.
 *  But Recreated by Hideaki Hagiwara at 2023.7.27
 *  
 */
public class JPiereImportEstimation extends SvrProcess implements ImportProcess
{
	/**	Client to be imported to		*/
	private int				p_AD_Client_ID = 0;

	/**	Delete old Imported				*/
	private boolean			p_deleteOldImported = false;
	/**	Only validate, don't import		*/
	private boolean	p_IsValidateOnly = false;
	/**	Document Action					*/
	private String			p_docAction = null;

	/** Effective						*/
	private Timestamp		p_DateValue = null;

	private String message = null;

	private String p_JP_ImportSalesRepIdentifier = JPiereImportUser.JP_ImportUserIdentifier_Value;

	private String p_JP_ImportUserIdentifier = JPiereImportUser.JP_ImportUserIdentifier_Name;

	private String p_JP_ImportDropShipUserIdentifier = JPiereImportUser.JP_ImportUserIdentifier_Name;

	private String p_JP_ImportInvoiceUserIdentifier = JPiereImportUser.JP_ImportUserIdentifier_Name;

	private IProcessUI processMonitor = null;

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
				p_deleteOldImported = "Y".equals(para[i].getParameter());
			else if (name.equals("IsValidateOnly"))
				p_IsValidateOnly = para[i].getParameterAsBoolean();
			else if (name.equals("DocAction"))
				p_docAction = (String)para[i].getParameter();
			else if (name.equals("JP_ImportSalesRepIdentifier"))
				p_JP_ImportSalesRepIdentifier = para[i].getParameterAsString();
			else if (name.equals("JP_ImportUserIdentifier"))
				p_JP_ImportUserIdentifier = para[i].getParameterAsString();
			else if (name.equals("JP_ImportDropSpUserIdentifier"))
				p_JP_ImportDropShipUserIdentifier = para[i].getParameterAsString();
			else if (name.equals("JP_ImportInvoiceUserIdentifier"))
				p_JP_ImportInvoiceUserIdentifier = para[i].getParameterAsString();
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
		if (p_deleteOldImported)
		{
			sql = new StringBuilder ("DELETE FROM I_EstimationJP ")
				  .append("WHERE I_IsImported='Y' ").append (getWhereClause());
			no = DB.executeUpdate(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("Delete Old Impored =" + no);
		}


		/** Reset I_ErrorMsg */
		sql = new StringBuilder ("UPDATE I_EstimationJP ")
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
		if(reverseLookupJP_Estimation_ID())
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
		
		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "JP_DocTypeSO_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupJP_DocTypeSO_ID())
			commitEx();
		else
			return message;
		
		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_Opportunity_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupC_Opportunity_ID())
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


		//Ship info
		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "M_Warehouse_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupM_Warehouse_ID())
			commitEx();
		else
			return message;
		
		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "JP_Locator_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupJP_Locator_ID())
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

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "Bill_BPartner_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupBill_BPartner_ID())
			commitEx();
		else
			return message;

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "BillTo_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupBill_Location_ID())
			commitEx();
		else
			return message;

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "Bill_User_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupBill_User_ID())
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

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_OrderSource_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupC_OrderSource_ID())
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

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "JP_Line_User2_ID");
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


		/** Create BPartner */
		message = Msg.getMsg(getCtx(), "CreateNew") + " : " + Msg.getElement(getCtx(), "C_BPartner_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(createBPartner())
			commitEx();
		else
			return message;


		/** Create Estimations */
		message = Msg.getMsg(getCtx(), "CreateNew") + " : " + Msg.getElement(getCtx(), "JP_Estimation_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);

		sql = new StringBuilder ("SELECT * FROM I_EstimationJP ")
			  .append("WHERE I_IsImported='N'").append (getWhereClause())
			.append(" ORDER BY JP_EstimationDate, C_BPartner_ID, BillTo_ID, C_BPartner_Location_ID, DocumentNo, I_EstimationJP_ID");

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
			int lastBillTo_ID = 0;
			int lastC_BPartner_Location_ID = 0;
			String lastDocumentNo = "";
			//
			MEstimation estimation = null;
			MEstimationLine line = null;
			X_I_EstimationJP imp = null;
			int lineNo = 0;
			boolean isCreateHeader= true;

			while (rs.next ())
			{
				recordsNum++;

				imp = new X_I_EstimationJP (getCtx (), rs, get_TrxName());

				//Re-Import
				if(imp.getJP_Estimation_ID() > 0)
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

				//	New Estimation
				isCreateHeader= true;
				if (lastC_BPartner_ID != imp.getC_BPartner_ID()
					|| lastC_BPartner_Location_ID != imp.getC_BPartner_Location_ID()
					|| lastBillTo_ID != imp.getBillTo_ID()
					|| !lastDocumentNo.equals(cmpDocumentNo))
				{
					if (estimation != null)
					{
						if(p_docAction != null)
						{
							if(!estimation.processIt(p_docAction))
							{
								rollback();
								message = "Estimation Process Failed: " + estimation.getProcessMsg();
								estimation = null;

								imp.setI_ErrorMsg(Msg.getMsg(getCtx(), "Error") + message);
								imp.saveEx(get_TrxName());
								commitEx();
							}	
						}
						
						estimation.saveEx(get_TrxName());
						estimation = null;
						commitEx();
						
					}

					lastC_BPartner_ID = imp.getC_BPartner_ID();
					lastC_BPartner_Location_ID = imp.getC_BPartner_Location_ID();
					lastBillTo_ID = imp.getBillTo_ID();
					lastDocumentNo = imp.getDocumentNo();
					if (lastDocumentNo == null)
						lastDocumentNo = "";

				}else {

					isCreateHeader = false;
				}

				if(isCreateHeader)
				{
					estimation = new MEstimation (getCtx(), 0, get_TrxName());
					lineNo = 0;

					if(createEstimationHeader(imp, estimation))
					{
						successCreateDocHeader++;
					}else {

						rollback();
						estimation = null;

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

				if(estimation == null)
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


				imp.setJP_Estimation_ID(estimation.getJP_Estimation_ID());

				//Create EstimationLine
				line = new MEstimationLine(estimation);
				lineNo = lineNo + 10;

				if(addEstimationLine(imp, estimation,line, lineNo))
				{
					successCreateDocLine++;
					successNum++;

				}else {

					rollback();
					estimation = null;

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

			//last estimation
			if (estimation != null)
			{
				if(p_docAction != null)
				{
					if(!estimation.processIt (p_docAction))
					{
						rollback();
						message = "Estimation Process Failed: " + estimation.getProcessMsg();
						estimation = null;
	
						imp.setI_ErrorMsg(Msg.getMsg(getCtx(), "Error") + message);
						imp.saveEx(get_TrxName());
						commitEx();
					}
				}
				
				estimation.saveEx(get_TrxName());
				estimation = null;
				commitEx();

			}//if (estimation != null)

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
		return X_I_EstimationJP.Table_Name;
	}


	/**
	 * Reverse Lookup JP_Estimation_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupJP_Estimation_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_EstimationJP i ")
				.append("SET JP_Estimation_ID=(SELECT MAX(JP_Estimation_ID) FROM JP_Estimation p")
				.append(" WHERE i.DocumentNo=p.DocumentNo AND p.AD_Client_ID=i.AD_Client_ID ) ")
				.append(" WHERE i.DocumentNo IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("Reverse Lookup JP_Estimation_ID = #" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}

		return true;

	}//reverseLookupJP_Estimation_ID

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
		StringBuilder sql = new StringBuilder ("UPDATE I_EstimationJP i ")
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
		sql = new StringBuilder ("UPDATE I_EstimationJP ")
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

		//Lookup -DocType of SO from DocTypeName
		StringBuilder sql = new StringBuilder ("UPDATE I_EstimationJP o ")	//	SO Document Type Name
			  .append("SET C_DocType_ID=(SELECT C_DocType_ID FROM C_DocType d WHERE d.Name=o.DocTypeName")
			  .append(" AND d.DocBaseType='JPE' AND o.AD_Client_ID=d.AD_Client_ID) ")
			  .append("WHERE C_DocType_ID IS NULL AND DocTypeName IS NOT NULL AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}

		//Invalid DocTypeName
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "DocTypeName");
		sql = new StringBuilder ("UPDATE I_EstimationJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append(" WHERE C_DocType_ID IS NULL AND DocTypeName IS NOT NULL  ")
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

		//Set Default DocType When C_DocType_ID is null
		sql = new StringBuilder ("UPDATE I_EstimationJP o ")
			  .append("SET C_DocType_ID=(SELECT MAX(C_DocType_ID) FROM C_DocType d WHERE d.IsDefault='Y'")
			  .append(" AND d.DocBaseType='JPE' AND o.AD_Client_ID=d.AD_Client_ID) ")
			  .append("WHERE C_DocType_ID IS NULL AND IsSOTrx='Y' AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}

		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "JP_Null")+Msg.getElement(getCtx(), "C_DocType_ID");
		sql =  new StringBuilder ("UPDATE I_EstimationJP ")
			  .append("SET I_ErrorMsg='"+ message + "'")
			  .append(" WHERE C_DocType_ID IS NULL")
			  .append(" AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}

		if(no > 0)
		{
			return false;
		}
		
		return true;
	}

	/**
	 *  Reverse Lookup C_DocType_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupJP_DocTypeSO_ID() throws Exception
	{
		int no = 0;

		//Lookup -DocType of SO from DocTypeName
		StringBuilder sql = new StringBuilder ("UPDATE I_EstimationJP o ")	//	SO Document Type Name
			  .append("SET JP_DocTypeSO_ID=(SELECT C_DocType_ID FROM C_DocType d WHERE d.Name=o.JP_DocTypeNameSO")
			  .append(" AND d.DocBaseType='SOO' AND o.AD_Client_ID=d.AD_Client_ID) ")
			  .append("WHERE JP_DocTypeSO_ID IS NULL AND JP_DocTypeNameSO IS NOT NULL AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}

		//Invalid DocTypeName
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_DocTypeNameSO");
		sql = new StringBuilder ("UPDATE I_EstimationJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append(" WHERE JP_DocTypeSO_ID IS NULL AND JP_DocTypeNameSO IS NOT NULL  ")
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
	}
	
	
	/**
	 * Reverse Lookup C_Opportunity_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupC_Opportunity_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_EstimationJP i ")
			.append("SET C_Opportunity_ID=(SELECT C_Opportunity_ID FROM C_Opportunity p")
			.append(" WHERE i.JP_Opportunity_DocumentNo=p.DocumentNo AND i.AD_Client_ID=p.AD_Client_ID) ")
			.append("WHERE i.C_Opportunity_ID IS NULL AND i.JP_Opportunity_DocumentNo IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Invalid JP_Opportunity_DocumentNo
		message = Msg.getMsg(getCtx(), "Error")  + Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "JP_Opportunity_DocumentNo");
		sql = new StringBuilder ("UPDATE I_EstimationJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append("WHERE C_Opportunity_ID IS NULL AND JP_Opportunity_DocumentNo IS NOT NULL")
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
			sql = new StringBuilder ("UPDATE I_EstimationJP i ")
					.append("SET SalesRep_ID=(SELECT MAX(AD_User_ID) FROM AD_User p INNER JOIN C_BPartner bp ON (p.C_BPartner_ID = bp.C_BPartner_ID) ")
					.append(" WHERE i.JP_SalesRep_EMail=p.EMail AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) AND bp.IsSalesRep='Y' ) ")
					.append(" WHERE i.JP_SalesRep_EMail IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportSalesRepIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_Name)) { //Name

			sql = new StringBuilder ("UPDATE I_EstimationJP i ")
					.append("SET SalesRep_ID=(SELECT MAX(AD_User_ID) FROM AD_User p INNER JOIN C_BPartner bp ON (p.C_BPartner_ID = bp.C_BPartner_ID) ")
					.append(" WHERE i.JP_SalesRep_Name=p.Name AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) AND bp.IsSalesRep='Y') ")
					.append(" WHERE i.JP_SalesRep_Name IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportSalesRepIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_Value)) { //Value

			sql = new StringBuilder ("UPDATE I_EstimationJP i ")
					.append("SET SalesRep_ID=(SELECT MAX(AD_User_ID) FROM AD_User p INNER JOIN C_BPartner bp ON (p.C_BPartner_ID = bp.C_BPartner_ID) ")
					.append(" WHERE i.JP_SalesRep_Value=p.Value AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) AND bp.IsSalesRep='Y'  ) ")
					.append(" WHERE i.JP_SalesRep_Value IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportSalesRepIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueEMail)) { //Value + E-Mail

			//In case of EMail is not null
			sql = new StringBuilder ("UPDATE I_EstimationJP i ")
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
			sql = new StringBuilder ("UPDATE I_EstimationJP i ")
					.append("SET SalesRep_ID=(SELECT MAX(AD_User_ID) FROM AD_User p INNER JOIN C_BPartner bp ON (p.C_BPartner_ID = bp.C_BPartner_ID) ")
					.append(" WHERE i.JP_SalesRep_Value=p.Value AND i.JP_SalesRep_EMail IS NULL l AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) AND bp.IsSalesRep='Y' ) ")
					.append(" WHERE i.JP_SalesRep_Value IS NOT NULL AND i.JP_SalesRep_EMail IS NULL AND i.SalesRep_ID IS NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());


		}else if(p_JP_ImportSalesRepIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueName)) { //Value + Name

			sql = new StringBuilder ("UPDATE I_EstimationJP i ")
					.append("SET SalesRep_ID=(SELECT MAX(AD_User_ID) FROM AD_User p INNER JOIN C_BPartner bp ON (p.C_BPartner_ID = bp.C_BPartner_ID) ")
					.append(" WHERE i.JP_SalesRep_Value=p.Value AND i.JP_SalesRep_Name=p.Name AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) AND bp.IsSalesRep='Y'  ) ")
					.append(" WHERE i.JP_SalesRep_Value IS NOT NULL AND i.JP_SalesRep_Name IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportSalesRepIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueNameEmail)) { //Value + Name + EMail

			//In case of EMail is not null
			sql = new StringBuilder ("UPDATE I_EstimationJP i ")
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
			sql = new StringBuilder ("UPDATE I_EstimationJP i ")
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
		StringBuilder sql = new StringBuilder ("UPDATE I_EstimationJP o ")
				  .append("SET M_PriceList_ID=(SELECT MAX(M_PriceList_ID) FROM M_PriceList p ")
				  .append(" WHERE p.Name=o.JP_PriceList_Name AND o.AD_Client_ID=p.AD_Client_ID) ")
				  .append(" WHERE M_PriceList_ID IS NULL AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Set C_Currency_ID from  M_PriceList_ID
		sql = new StringBuilder ("UPDATE I_EstimationJP o ")
				  .append("SET C_Currency_ID=(SELECT C_Currency_ID FROM M_PriceList p ")
				  .append(" WHERE p.M_PriceList_ID=o.M_PriceList_ID ) ")
				  .append(" WHERE I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Set M_PriceList_ID from Default
		sql = new StringBuilder ("UPDATE I_EstimationJP o ")
				  .append("SET M_PriceList_ID=(SELECT MAX(M_PriceList_ID) FROM M_PriceList p WHERE p.IsDefault='Y'")
				  .append(" AND p.C_Currency_ID=o.C_Currency_ID AND p.IsSOPriceList=o.IsSOTrx AND o.AD_Client_ID=p.AD_Client_ID) ")
				  .append("WHERE M_PriceList_ID IS NULL AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Set M_PriceList_ID from Default
		sql = new StringBuilder ("UPDATE I_EstimationJP o ")
			  .append("SET M_PriceList_ID=(SELECT MAX(M_PriceList_ID) FROM M_PriceList p WHERE p.IsDefault='Y'")
			  .append(" AND p.IsSOPriceList=o.IsSOTrx AND o.AD_Client_ID=p.AD_Client_ID) ")
			  .append("WHERE M_PriceList_ID IS NULL AND C_Currency_ID IS NULL AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		sql = new StringBuilder ("UPDATE I_EstimationJP o ")
			  .append("SET M_PriceList_ID=(SELECT MAX(M_PriceList_ID) FROM M_PriceList p ")
			  .append(" WHERE p.C_Currency_ID=o.C_Currency_ID AND p.IsSOPriceList=o.IsSOTrx AND o.AD_Client_ID=p.AD_Client_ID) ")
			  .append("WHERE M_PriceList_ID IS NULL AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		sql = new StringBuilder ("UPDATE I_EstimationJP o ")
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
		sql = new StringBuilder ("UPDATE I_EstimationJP ")
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

		StringBuilder sql = new StringBuilder ("UPDATE I_EstimationJP i ")
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
		sql = new StringBuilder ("UPDATE I_EstimationJP ")
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

		StringBuilder sql = new StringBuilder ("UPDATE I_EstimationJP i ")
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
		sql = new StringBuilder ("UPDATE I_EstimationJP ")
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

		StringBuilder sql = new StringBuilder ("UPDATE I_EstimationJP i ")
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
		sql = new StringBuilder ("UPDATE I_EstimationJP ")
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

		StringBuilder sql = new StringBuilder ("UPDATE I_EstimationJP i ")
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
		sql = new StringBuilder ("UPDATE I_EstimationJP ")
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
	 * Reverse Lookup JP_Line_Activity_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupJP_Line_Activity_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_EstimationJP i ")
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
		sql = new StringBuilder ("UPDATE I_EstimationJP ")
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
	 * Reverse Lookup C_Activity_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupC_Activity_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_EstimationJP i ")
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
		sql = new StringBuilder ("UPDATE I_EstimationJP ")
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
	 * Reverse Lookup AD_OrgTrx_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupAD_OrgTrx_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_EstimationJP i ")
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
		sql = new StringBuilder ("UPDATE I_EstimationJP ")
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

		StringBuilder sql = new StringBuilder ("UPDATE I_EstimationJP i ")
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
		sql = new StringBuilder ("UPDATE I_EstimationJP ")
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

		StringBuilder sql = new StringBuilder ("UPDATE I_EstimationJP i ")
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
		sql = new StringBuilder ("UPDATE I_EstimationJP ")
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

		StringBuilder sql = new StringBuilder ("UPDATE I_EstimationJP i ")
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
		sql = new StringBuilder ("UPDATE I_EstimationJP ")
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

		StringBuilder sql = new StringBuilder ("UPDATE I_EstimationJP i ")
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
		sql = new StringBuilder ("UPDATE I_EstimationJP ")
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

		StringBuilder sql = new StringBuilder ("UPDATE I_EstimationJP i ")
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
		sql = new StringBuilder ("UPDATE I_EstimationJP ")
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
	 * Reverse Lookup C_OrderSource_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupC_OrderSource_ID() throws Exception
	{
		int no = 0;
		StringBuilder sql = new StringBuilder ("UPDATE I_EstimationJP o ")
			  .append("SET C_OrderSource_ID=(SELECT C_OrderSource_ID FROM C_OrderSource p")
			  .append(" WHERE o.C_OrderSourceValue=p.Value AND o.AD_Client_ID=p.AD_Client_ID) ")
			  .append("WHERE C_OrderSource_ID IS NULL AND C_OrderSourceValue IS NOT NULL AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Invalid C_OrderSourceValue
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "C_OrderSourceValue");
		sql = new StringBuilder ("UPDATE I_EstimationJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append(" WHERE C_OrderSource_ID IS NULL AND C_OrderSourceValue IS NOT NULL")
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
	 * Reverse Lookup C_PaymentTerm_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupC_PaymentTerm_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_EstimationJP o ")
			  .append("SET C_PaymentTerm_ID=(SELECT C_PaymentTerm_ID FROM C_PaymentTerm p")
			  .append(" WHERE o.PaymentTermValue=p.Value AND o.AD_Client_ID=p.AD_Client_ID) ")
			  .append("WHERE C_PaymentTerm_ID IS NULL AND PaymentTermValue IS NOT NULL AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Set Default
		sql = new StringBuilder ("UPDATE I_EstimationJP o ")
			  .append("SET C_PaymentTerm_ID=(SELECT MAX(C_PaymentTerm_ID) FROM C_PaymentTerm p")
			  .append(" WHERE p.IsDefault='Y' AND o.AD_Client_ID=p.AD_Client_ID) ")
			  .append("WHERE C_PaymentTerm_ID IS NULL AND o.PaymentTermValue IS NULL AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Invalid PaymentTermValue
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "C_PaymentTerm_ID");
		sql = new StringBuilder ("UPDATE I_EstimationJP ")
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
	 * Reverse Lookup Bill_BPartner_ID()
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupBill_BPartner_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_EstimationJP i ")
			.append("SET Bill_BPartner_ID=(SELECT C_BPartner_ID FROM C_BPartner p")
			.append(" WHERE i.Bill_BPValue=p.Value AND i.AD_Client_ID=p.AD_Client_ID) ")
			.append("WHERE i.Bill_BPartner_ID IS NULL AND i.Bill_BPValue IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Invalid BPartner_Value
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "Bill_BPValue");
		sql = new StringBuilder ("UPDATE I_EstimationJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append("WHERE Bill_BPartner_ID IS NULL AND Bill_BPValue IS NOT NULL")
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
	 * Reverse Lookup Bill_Location_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupBill_Location_ID()throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_EstimationJP i ")
				.append("SET BillTo_ID=(SELECT C_BPartner_Location_ID FROM C_BPartner_Location p")
				.append(" WHERE i.JP_Bill_BP_Location_Name=p.Name AND i.Bill_BPartner_ID=p.C_BPartner_ID) ")
				.append("WHERE i.BillTo_ID IS NULL AND i.JP_Bill_BP_Location_Name IS NOT NULL ")
				.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Invalid JP_Bill_BP_Location_Name
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "JP_Bill_BP_Location_Name");
		sql = new StringBuilder ("UPDATE I_EstimationJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append("WHERE BillTo_ID IS NULL AND JP_Bill_BP_Location_Name IS NOT NULL")
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
	 * Reverse Lookup Bill_User_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupBill_User_ID()throws Exception
	{
		if(Util.isEmpty(p_JP_ImportInvoiceUserIdentifier) || p_JP_ImportInvoiceUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_NotCollate))
			return true;

		StringBuilder sql = null;

		if(p_JP_ImportInvoiceUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_EMail)) //E-Mail
		{
			sql = new StringBuilder ("UPDATE I_EstimationJP i ")
					.append("SET Bill_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_Bill_User_EMail=p.EMail AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.Bill_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_Bill_User_EMail IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportInvoiceUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_Name)) { //Name

			sql = new StringBuilder ("UPDATE I_EstimationJP i ")
					.append("SET Bill_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_Bill_User_Name=p.Name AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.Bill_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_Bill_User_Name IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportInvoiceUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_Value)) { //Value

			sql = new StringBuilder ("UPDATE I_EstimationJP i ")
					.append("SET Bill_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_Bill_User_Value=p.Value AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.Bill_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_Bill_User_Value IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportInvoiceUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueEMail)) { //Value + E-Mail

			//In case of EMail is not null
			sql = new StringBuilder ("UPDATE I_EstimationJP i ")
					.append("SET Bill_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_Bill_User_Value=p.Value AND i.JP_Bill_User_EMail=p.EMail AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.Bill_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_Bill_User_Value IS NOT NULL AND i.JP_Bill_User_EMail IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

			try {
				DB.executeUpdateEx(sql.toString(), get_TrxName());
			}catch(Exception e) {
				message = message + " : " +e.toString()+ " : "+sql.toString();
				return false;
			}

			//In case of EMail is null
			sql = new StringBuilder ("UPDATE I_EstimationJP i ")
					.append("SET Bill_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_Bill_User_Value=p.Value AND i.JP_Bill_User_EMail IS NULL AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.Bill_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_Bill_User_Value IS NOT NULL AND i.JP_Bill_User_EMail IS NULL AND Bill_User_ID IS NOT NULL ")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportInvoiceUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueName)) { //Value + Name

			sql = new StringBuilder ("UPDATE I_EstimationJP i ")
					.append("SET Bill_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_Bill_User_Value=p.Value AND i.JP_Bill_User_Name=p.Name AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.Bill_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_Bill_User_Value IS NOT NULL AND i.JP_Bill_User_Name IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportInvoiceUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueNameEmail)) { //Value + Name + EMail

			//In case of EMail is not null
			sql = new StringBuilder ("UPDATE I_EstimationJP i ")
					.append("SET Bill_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_Bill_User_Value=p.Value AND i.JP_Bill_User_Name=p.Name  AND i.JP_Bill_User_EMail=p.EMail AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.Bill_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_Bill_User_Value IS NOT NULL AND i.JP_Bill_User_Name IS NOT NULL AND i.JP_Bill_User_EMail IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

			try {
				DB.executeUpdateEx(sql.toString(), get_TrxName());
			}catch(Exception e) {
				message = message + " : " +e.toString()+ " : "+sql.toString();
				return false;
			}

			//In case of EMail is null
			sql = new StringBuilder ("UPDATE I_EstimationJP i ")
					.append("SET Bill_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_Bill_User_Value=p.Value AND i.JP_Bill_User_Name=p.Name  AND i.JP_Bill_User_EMail IS NULL AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.Bill_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_Bill_User_Value IS NOT NULL AND i.JP_Bill_User_Name IS NOT NULL AND i.JP_Bill_User_EMail IS NULL AND Bill_User_ID IS NULL ")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());


		}else if(p_JP_ImportInvoiceUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_NotCollate)){

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
		StringBuilder sql = new StringBuilder ("UPDATE I_EstimationJP o ")
				  .append("SET M_Warehouse_ID=(SELECT M_Warehouse_ID FROM M_Warehouse w")
				  .append(" WHERE o.JP_Warehouse_Value=w.Value AND o.AD_Org_ID=w.AD_Org_ID) ")
				  .append(" WHERE M_Warehouse_ID IS NULL AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(message +" : " + e.toString() +" : " + sql);
		}

		sql = new StringBuilder ("UPDATE I_EstimationJP o ")
			  .append("SET M_Warehouse_ID=(SELECT MAX(M_Warehouse_ID) FROM M_Warehouse w")
			  .append(" WHERE o.AD_Client_ID=w.AD_Client_ID AND o.AD_Org_ID=w.AD_Org_ID) ")
			  .append("WHERE M_Warehouse_ID IS NULL AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(message +" : " + e.toString() +" : " + sql);
		}

		sql = new StringBuilder ("UPDATE I_EstimationJP o ")
			  .append("SET M_Warehouse_ID=(SELECT M_Warehouse_ID FROM M_Warehouse w")
			  .append(" WHERE o.AD_Client_ID=w.AD_Client_ID) ")
			  .append("WHERE M_Warehouse_ID IS NULL")
			  .append(" AND EXISTS (SELECT AD_Client_ID FROM M_Warehouse w WHERE w.AD_Client_ID=o.AD_Client_ID GROUP BY AD_Client_ID HAVING COUNT(*)=1)")
			  .append(" AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(message +" : " + e.toString() +" : " + sql);
		}

		//Invalid M_Warehouse_ID
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "M_Warehouse_ID");
		sql = new StringBuilder ("UPDATE I_EstimationJP ")
				.append("SET I_ErrorMsg='"+ message + "'")
				.append(" WHERE M_Warehouse_ID IS NULL")
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
	 * Reverse Lookup JP_Locator_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupJP_Locator_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_EstimationJP i ")
			.append("SET JP_Locator_ID=(SELECT M_Locator_ID FROM M_Locator p")
			.append(" WHERE i.JP_Locator_Value=p.Value AND i.AD_Client_ID=p.AD_Client_ID) ")
			.append(" WHERE i.JP_Locator_ID IS NULL AND i.JP_Locator_Value IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Invalid JP_Locator_Value
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "JP_Locator_Value");
		sql = new StringBuilder ("UPDATE I_EstimationJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append(" WHERE JP_Locator_ID IS NULL AND JP_Locator_Value IS NOT NULL")
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

		StringBuilder sql = new StringBuilder ("UPDATE I_EstimationJP i ")
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
		sql = new StringBuilder ("UPDATE I_EstimationJP ")
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

		StringBuilder sql = new StringBuilder ("UPDATE I_EstimationJP i ")
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
		sql = new StringBuilder ("UPDATE I_EstimationJP ")
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

		StringBuilder sql = new StringBuilder ("UPDATE I_EstimationJP i ")
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
		sql = new StringBuilder ("UPDATE I_EstimationJP ")
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
			sql = new StringBuilder ("UPDATE I_EstimationJP i ")
					.append("SET DropShip_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_DropShip_User_EMail=p.EMail AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.DropShip_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_DropShip_User_EMail IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportDropShipUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_Name)) { //Name

			sql = new StringBuilder ("UPDATE I_EstimationJP i ")
					.append("SET DropShip_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_DropShip_User_Name=p.Name AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.DropShip_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_DropShip_User_Name IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportDropShipUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_Value)) { //Value

			sql = new StringBuilder ("UPDATE I_EstimationJP i ")
					.append("SET DropShip_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_DropShip_User_Value=p.Value AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.DropShip_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_DropShip_User_Value IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportDropShipUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueEMail)) { //Value + E-Mail

			//In case of EMail is not null
			sql = new StringBuilder ("UPDATE I_EstimationJP i ")
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
			sql = new StringBuilder ("UPDATE I_EstimationJP i ")
					.append("SET DropShip_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_DropShip_User_Value=p.Value AND i.JP_DropShip_User_EMail IS NULL AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.DropShip_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_DropShip_User_Value IS NOT NULL AND i.JP_DropShip_User_EMail IS NULL AND DropShip_User_ID IS NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportDropShipUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueName)) { //Value + Name

			sql = new StringBuilder ("UPDATE I_EstimationJP i ")
					.append("SET DropShip_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_DropShip_User_Value=p.Value AND i.JP_DropShip_User_Name=p.Name AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.DropShip_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_DropShip_User_Value IS NOT NULL AND i.JP_DropShip_User_Name IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportDropShipUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueNameEmail)) { //Value + Name + EMail

			//In case of EMail is not null
			sql = new StringBuilder ("UPDATE I_EstimationJP i ")
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
			sql = new StringBuilder ("UPDATE I_EstimationJP i ")
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
		StringBuilder  sql = new StringBuilder ("UPDATE I_EstimationJP i ")
			.append("SET C_BPartner_ID=(SELECT C_BPartner_ID FROM C_BPartner p")
			.append(" WHERE i.BPartnerValue=p.Value AND i.AD_Client_ID=p.AD_Client_ID) ")
			.append("WHERE i.C_BPartner_ID IS NULL AND i.BPartnerValue IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("Reverse Lookup C_BPartner_ID = #" + no);
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

		StringBuilder sql = new StringBuilder ("UPDATE I_EstimationJP i ")
				.append("SET C_BPartner_Location_ID=(SELECT C_BPartner_Location_ID FROM C_BPartner_Location p")
				.append(" WHERE i.JP_BPartner_Location_Name=p.Name AND i.C_BPartner_ID=p.C_BPartner_ID) ")
				.append("WHERE i.C_BPartner_Location_ID IS NULL AND i.JP_BPartner_Location_Name IS NOT NULL ")
				.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("Reverse Lookup C_BPartner_Location_ID = 1#" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}


		sql = new StringBuilder ("UPDATE I_EstimationJP i ")
				.append("SET C_BPartner_Location_ID=(SELECT max(C_BPartner_Location_ID) FROM C_BPartner_Location p")
				.append(" WHERE i.C_BPartner_ID=p.C_BPartner_ID AND i.Phone = p.phone) ")
				.append(" WHERE i.C_BPartner_Location_ID IS NULL AND i.JP_BPartner_Location_Name IS NULL ")
				.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("Reverse Lookup C_BPartner_Location_ID = 2#" + no);
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
			sql = new StringBuilder ("UPDATE I_EstimationJP i ")
					.append("SET AD_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.EMail=p.EMail AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.C_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.EMail IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_Name)) { //Name

			sql = new StringBuilder ("UPDATE I_EstimationJP i ")
					.append("SET AD_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.ContactName=p.Name AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.C_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.ContactName IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_Value)) { //Value

			sql = new StringBuilder ("UPDATE I_EstimationJP i ")
					.append("SET AD_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_User_Value=p.Value AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.C_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_User_Value IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueEMail)) { //Value + E-Mail

			//In case of EMail is not null
			sql = new StringBuilder ("UPDATE I_EstimationJP i ")
					.append("SET AD_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_User_Value=p.Value AND i.EMail=p.EMail AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.C_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_User_Value IS NOT NULL AND i.EMail IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

			try {
				DB.executeUpdateEx(sql.toString(), get_TrxName());
			}catch(Exception e) {
				message = message + " : " +e.toString()+ " : "+sql.toString();
				return false;
			}

			//In case of EMail is null
			sql = new StringBuilder ("UPDATE I_EstimationJP i ")
					.append("SET AD_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_User_Value=p.Value AND i.EMail IS NULL AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.C_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_User_Value IS NOT NULL AND i.EMail IS NULL AND i.AD_User_ID IS NOT NULL ")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueName)) { //Value + Name

			sql = new StringBuilder ("UPDATE I_EstimationJP i ")
					.append("SET AD_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_User_Value=p.Value AND i.ContactName=p.Name AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.C_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_User_Value IS NOT NULL AND i.ContactName IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueNameEmail)) { //Value + Name + EMail

			//In case of EMail is not null
			sql = new StringBuilder ("UPDATE I_EstimationJP i ")
					.append("SET AD_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_User_Value=p.Value AND i.ContactName=p.Name  AND i.EMail=p.EMail AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 )")
					.append(" AND i.C_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_User_Value IS NOT NULL AND i.ContactName IS NOT NULL AND i.EMail IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

			try {
				DB.executeUpdateEx(sql.toString(), get_TrxName());
			}catch(Exception e) {
				message = message + " : " +e.toString()+ " : "+sql.toString();
				return false;
			}

			//In case of EMail is null
			sql = new StringBuilder ("UPDATE I_EstimationJP i ")
					.append("SET AD_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_User_Value=p.Value AND i.ContactName=p.Name  AND i.EMail IS NULL AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 )")
					.append(" AND i.C_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_User_Value IS NOT NULL AND i.ContactName IS NOT NULL AND i.EMail IS NULL AND i.AD_User_ID IS NULL")
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
		StringBuilder	sql = new StringBuilder ("UPDATE I_EstimationJP o ")
			  .append("SET M_Product_ID=(SELECT MAX(M_Product_ID) FROM M_Product p")
			  .append(" WHERE o.ProductValue=p.Value AND o.AD_Client_ID=p.AD_Client_ID) ")
			  .append("WHERE M_Product_ID IS NULL AND ProductValue IS NOT NULL")
			  .append(" AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//UPC
		sql = new StringBuilder ("UPDATE I_EstimationJP o ")
			  .append("SET M_Product_ID=(SELECT MAX(M_Product_ID) FROM M_Product p")
			  .append(" WHERE o.UPC=p.UPC AND o.AD_Client_ID=p.AD_Client_ID) ")
			  .append("WHERE M_Product_ID IS NULL AND UPC IS NOT NULL")
			  .append(" AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//SKU
		sql = new StringBuilder ("UPDATE I_EstimationJP o ")
			  .append("SET M_Product_ID=(SELECT MAX(M_Product_ID) FROM M_Product p")
			  .append(" WHERE o.SKU=p.SKU AND o.AD_Client_ID=p.AD_Client_ID) ")
			  .append("WHERE M_Product_ID IS NULL AND SKU IS NOT NULL")
			  .append(" AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Invalid
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "M_Product_ID");
		sql = new StringBuilder ("UPDATE I_EstimationJP ")
				.append("SET I_ErrorMsg='"+ message + "'")
				.append(" WHERE M_Product_ID IS NULL AND (ProductValue IS NOT NULL OR UPC IS NOT NULL OR SKU IS NOT NULL)")
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

		StringBuilder sql = new StringBuilder ("UPDATE I_EstimationJP o ")
			  .append("SET C_Charge_ID=(SELECT C_Charge_ID FROM C_Charge c")
			  .append(" WHERE o.ChargeName=c.Name AND o.AD_Client_ID=c.AD_Client_ID) ")
			  .append("WHERE C_Charge_ID IS NULL AND ChargeName IS NOT NULL AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Invalid ChargeName
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "ChargeName");
		sql = new StringBuilder ("UPDATE I_EstimationJP ")
				.append("SET I_ErrorMsg='"+ message + "'")
				.append(" WHERE C_Charge_ID IS NULL AND ChargeName IS NOT NULL ")
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

		StringBuilder sql = new StringBuilder ("UPDATE I_EstimationJP i ")
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
		sql = new StringBuilder ("UPDATE I_EstimationJP ")
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

		StringBuilder sql = new StringBuilder ("UPDATE I_EstimationJP o ")
			  .append("SET C_Tax_ID=(SELECT MAX(C_Tax_ID) FROM C_Tax t")
			  .append(" WHERE o.TaxIndicator=t.TaxIndicator AND o.AD_Client_ID=t.AD_Client_ID) ")
			  .append("WHERE C_Tax_ID IS NULL AND TaxIndicator IS NOT NULL")
			  .append(" AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Invalid ChargeName
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "TaxIndicator");
		sql =  new StringBuilder ("UPDATE I_EstimationJP ")
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

	private boolean createBPartner() throws Exception
	{

		ArrayList<X_I_EstimationJP> estimationlist = new ArrayList<X_I_EstimationJP>();
		String sql = "SELECT * FROM I_EstimationJP WHERE I_IsImported<>'Y' AND C_BPartner_ID IS NULL AND BPartnerValue IS NOT NULL "
							+ getWhereClause() + " ORDER BY BPartnerValue, I_EstimationJP_ID ASC ";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, get_TrxName());
			rs = pstmt.executeQuery();
			String preValue = "";
			X_I_EstimationJP impEstimation =null;

			while (rs.next())
			{
				impEstimation = new X_I_EstimationJP (getCtx (), rs, get_TrxName());
				if(preValue != null && preValue.equals(impEstimation.getBPartnerValue()))
				{
					;//Noting to do
				}else {
					estimationlist.add(impEstimation);
					preValue = impEstimation.getBPartnerValue();
				}


			}
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

		X_I_BPartnerJP i_BP = null;
		for(X_I_EstimationJP i_estimation : estimationlist)
		{
			i_BP = new X_I_BPartnerJP (getCtx (), 0, get_TrxName());
			PO.copyValues(i_estimation, i_BP);
			i_BP.setJP_Org_Value(i_estimation.getJP_BP_Org_Value());
			i_BP.setValue(i_estimation.getBPartnerValue());
			if(Util.isEmpty(i_estimation.getJP_BPartner_Location_Name()))
				i_BP.setJP_BPartner_Location_Name(i_estimation.getName());
			if(Util.isEmpty(i_estimation.getJP_Location_Label()))
				i_BP.setJP_Location_Label(i_BP.getJP_BPartner_Location_Name());
			if(Util.isEmpty(i_estimation.getContactName()))
				i_BP.setContactName(i_estimation.getName());

			if(i_estimation.isSOTrx())
			{
				i_BP.setIsCustomer(true);
				i_BP.setIsVendor(false);
				i_BP.setIsEmployee(false);
				i_BP.setIsSalesRep(false);
			}else {
				i_BP.setIsCustomer(false);
				i_BP.setIsVendor(true);
				i_BP.setIsEmployee(false);
				i_BP.setIsSalesRep(false);
			}

			i_BP.saveEx(get_TrxName());
			commitEx();

			ProcessInfo pi = new ProcessInfo("CreateBPartner", 0);
			String className =  "jpiere.base.plugin.org.adempiere.process.JPiereImportBPartner";
			pi.setClassName(className);
			pi.setAD_Client_ID(getAD_Client_ID());
			pi.setAD_User_ID(getAD_User_ID());
			pi.setAD_PInstance_ID(getAD_PInstance_ID());
			pi.setRecord_ID(0);

			//Update ProcessInfoParameter
			ArrayList<ProcessInfoParameter> list = new ArrayList<ProcessInfoParameter>();
			list.add (new ProcessInfoParameter("DeleteOldImported", false, null, null, null ));
			list.add (new ProcessInfoParameter("IsValidateOnly", false, null, null, null ));
			list.add (new ProcessInfoParameter("JP_ImportUserIdentifier", p_JP_ImportUserIdentifier, null, null, null ));
			list.add (new ProcessInfoParameter("I_BPartnerJP_ID", i_BP.getI_BPartnerJP_ID(), null, null, null ));

			ProcessInfoParameter[] pars = new ProcessInfoParameter[list.size()];
			list.toArray(pars);
			pi.setParameter(pars);

			if(!ProcessUtil.startJavaProcess(getCtx(), pi, Trx.get(get_TrxName(), true), false, processUI))
			{
				message = Msg.getMsg(getCtx(), "ProcessRunError");

				return false;
			}

		}

		//Business partner Info
		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_BPartner_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupC_BPartner_ID())
			commitEx();
		else
			return false;

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_BPartner_Location_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupC_BPartner_Location_ID())
			commitEx();
		else
			return false;

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "AD_UserID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupAD_User_ID())
			commitEx();
		else
			return false;

		return true;

	}

	/**
	 * Create Estimation Header
	 *
	 * @param impEstimation
	 * @param estimation
	 * @return
	 */
	private boolean createEstimationHeader(X_I_EstimationJP impEstimation, MEstimation estimation)
	{
		ModelValidationEngine.get().fireImportValidate(this, impEstimation, estimation, ImportValidator.TIMING_BEFORE_IMPORT);

		PO.copyValues(impEstimation, estimation);
		estimation.setJP_Estimation_ID(0);
		estimation.setAD_Org_ID(impEstimation.getAD_Org_ID());
		estimation.setC_DocTypeTarget_ID(impEstimation.getC_DocType_ID());
		estimation.setIsSOTrx(impEstimation.isSOTrx());
		estimation.setDocStatus(DocAction.STATUS_Drafted);
		estimation.setDocAction(DocAction.ACTION_Complete);

		//Date
		if(impEstimation.getJP_EstimationDate() == null)
		{
			impEstimation.setJP_EstimationDate(p_DateValue);
			estimation.setJP_EstimationDate(p_DateValue);
		}
		
		if(impEstimation.getDateOrdered() == null)
		{
			impEstimation.setDateOrdered(p_DateValue);
			estimation.setDateOrdered(p_DateValue);
		}

		if(impEstimation.getDateAcct() == null)
		{
			impEstimation.setDateAcct(p_DateValue);
			estimation.setDateAcct(p_DateValue);
		}

		if(impEstimation.getDatePromised() == null)
		{
			impEstimation.setDatePromised(p_DateValue);
			estimation.setDatePromised(p_DateValue);
		}

		//Busienss Partner
		MBPartner bp = null;
		if(impEstimation.getC_BPartner_ID() > 0)
		{
			bp =MBPartner.get(getCtx(), impEstimation.getC_BPartner_ID());

		}else if(!Util.isEmpty(impEstimation.getBPartnerValue())) {

			bp =MBPartner.get(getCtx(), impEstimation.getBPartnerValue(),get_TrxName());
			impEstimation.setC_BPartner_ID(bp.getC_BPartner_ID());
			estimation.setC_BPartner_ID(bp.getC_BPartner_ID());

		}else {
			message = Msg.getMsg(getCtx(), "JP_Null") + Msg.getElement(getCtx(), "C_BPartner_ID");
			impEstimation.setI_ErrorMsg(message);
			return false;
		}

		if(impEstimation.getC_BPartner_Location_ID() > 0)
		{
			;
		}else {

			MBPartnerLocation[] locations = bp.getLocations(false);
			if(locations != null && locations.length == 1 )
			{
				impEstimation.setC_BPartner_Location_ID(locations[0].getC_BPartner_Location_ID());
				estimation.setC_BPartner_Location_ID(locations[0].getC_BPartner_Location_ID());
			}
		}

		if(impEstimation.getAD_User_ID() > 0)
		{
			;
		}else {

			MUser[] users =MUser.getOfBPartner(getCtx(), impEstimation.getC_BPartner_ID(),get_TrxName());
			if(users != null && users.length == 1 )
			{
				impEstimation.setAD_User_ID(users[0].getAD_User_ID());
				estimation.setAD_User_ID(users[0].getAD_User_ID());
			}

		}

		if(Util.isEmpty(impEstimation.getDeliveryRule()))
		{
			if(Util.isEmpty(bp.getDeliveryRule()))
			{
				impEstimation.setDeliveryRule(X_I_EstimationJP.DELIVERYRULE_Availability);
				estimation.setDeliveryRule(X_I_EstimationJP.DELIVERYRULE_Availability);

			}else {
				impEstimation.setDeliveryRule(bp.getDeliveryRule());
				estimation.setDeliveryRule(bp.getDeliveryRule());
			}
		}

		if(Util.isEmpty(impEstimation.getPriorityRule()))
		{
			impEstimation.setPriorityRule(X_I_EstimationJP.PRIORITYRULE_Medium);
			estimation.setPriorityRule(X_I_EstimationJP.PRIORITYRULE_Medium);
		}

		if(Util.isEmpty(impEstimation.getDeliveryViaRule()))
		{
			if(Util.isEmpty(bp.getDeliveryViaRule()))
			{
				impEstimation.setDeliveryViaRule(X_I_EstimationJP.DELIVERYVIARULE_Pickup);
				estimation.setDeliveryViaRule(X_I_EstimationJP.DELIVERYVIARULE_Pickup);

			}else {
				impEstimation.setDeliveryViaRule(bp.getDeliveryViaRule());
				estimation.setDeliveryViaRule(bp.getDeliveryViaRule());
			}
		}

		if(Util.isEmpty(impEstimation.getFreightCostRule()))
		{
			if(Util.isEmpty(bp.getFreightCostRule()))
			{
				impEstimation.setFreightCostRule(X_I_EstimationJP.FREIGHTCOSTRULE_FreightIncluded);
				estimation.setFreightCostRule(X_I_EstimationJP.FREIGHTCOSTRULE_FreightIncluded);

			}else {
				impEstimation.setFreightCostRule(bp.getFreightCostRule());
				estimation.setFreightCostRule(bp.getFreightCostRule());
			}
		}

		if(impEstimation.getM_PriceList_ID() == 0)
		{
			if(bp.getM_PriceList_ID() == 0)
			{
				;//set Default

			}else {
				impEstimation.setM_PriceList_ID(bp.getM_PriceList_ID());
				estimation.setM_PriceList_ID(bp.getM_PriceList_ID());
			}
		}

		if(Util.isEmpty(impEstimation.getInvoiceRule()))
		{
			if(Util.isEmpty(bp.getInvoiceRule()))
			{
				impEstimation.setInvoiceRule(X_I_EstimationJP.INVOICERULE_Immediate);
				estimation.setInvoiceRule(X_I_EstimationJP.INVOICERULE_Immediate);

			}else {
				impEstimation.setInvoiceRule(bp.getInvoiceRule());
				estimation.setInvoiceRule(bp.getInvoiceRule());
			}
		}

		if(Util.isEmpty(impEstimation.getPaymentRule()))
		{
			if(Util.isEmpty(bp.getPaymentRule()))
			{
				//set Default
				impEstimation.setPaymentRule(X_I_EstimationJP.PAYMENTRULE_OnCredit);
				estimation.setPaymentRule(X_I_EstimationJP.PAYMENTRULE_OnCredit);

			}else {
				impEstimation.setPaymentRule(bp.getPaymentRule());
				estimation.setPaymentRule(bp.getPaymentRule());
			}
		}

		if(impEstimation.getC_PaymentTerm_ID() == 0)
		{
			if(bp.getC_PaymentTerm_ID() == 0)
			{
				;//set Default

			}else {
				impEstimation.setC_PaymentTerm_ID(bp.getC_PaymentTerm_ID());
				estimation.setC_PaymentTerm_ID(bp.getC_PaymentTerm_ID());
			}
		}

		ModelValidationEngine.get().fireImportValidate(this, impEstimation, estimation, ImportValidator.TIMING_AFTER_IMPORT);

		try {
			estimation.saveEx(get_TrxName());
		}catch (Exception e) {

		    message = Msg.getMsg(getCtx(),"SaveIgnored") + Msg.getElement(getCtx(), "JP_Estimation_ID") +" : " + e.toString();

			return false;
		}

		impEstimation.setJP_Estimation_ID(estimation.getJP_Estimation_ID());

		return true;

	}

	/**
	 *
	 * add Estimation Line
	 *
	 * @param impEstimation
	 * @param estimation
	 * @param line
	 * @param lineNo
	 * @return
	 */
	private boolean addEstimationLine(X_I_EstimationJP impEstimation, MEstimation estimation, MEstimationLine line, int lineNo)
	{
		ModelValidationEngine.get().fireImportValidate(this, impEstimation, line, ImportValidator.TIMING_BEFORE_IMPORT);

		PO.copyValues(impEstimation, line);

		line.setJP_Estimation_ID(estimation.getJP_Estimation_ID());
		line.setDescription(null);
		line.setJP_CommunicationColumn(null);

		if(impEstimation.getLine()==0)
		{
			line.setLine(lineNo);
			impEstimation.setLine(lineNo);

		}else {
			line.setLine(impEstimation.getLine());
		}

		if(!Util.isEmpty(impEstimation.getLineDescription()))
			line.setDescription(impEstimation.getLineDescription());

		if(line.getQtyEntered().compareTo(Env.ZERO) == 0 && line.getQtyOrdered().compareTo(Env.ZERO) != 0 )
		{
			line.setQtyEntered(line.getQtyOrdered());

		}else if(line.getQtyEntered().compareTo(Env.ZERO) != 0 && line.getQtyOrdered().compareTo(Env.ZERO) == 0 ) {

			line.setQtyOrdered(line.getQtyEntered());
		}


		if(impEstimation.getJP_Line_OrgTrx_ID() > 0)
		{
			line.setAD_OrgTrx_ID(impEstimation.getJP_Line_OrgTrx_ID());
		}else {
			line.setAD_OrgTrx_ID(0);
		}

		if(impEstimation.getJP_Line_Project_ID() > 0)
		{
			line.setC_Project_ID(impEstimation.getJP_Line_Project_ID());
		}else {
			line.setC_Project_ID(0);
		}

		if(impEstimation.getJP_Line_Activity_ID() > 0)
		{
			line.setC_Activity_ID(impEstimation.getJP_Line_Activity_ID());
		}else {
			line.setC_Activity_ID(0);
		}

		if(impEstimation.getJP_Line_Campaign_ID() > 0)
		{
			line.setC_Campaign_ID(impEstimation.getJP_Line_Campaign_ID());
		}else {
			line.setC_Campaign_ID(0);
		}

		if(impEstimation.getJP_Line_User1_ID() > 0)
		{
			line.setUser1_ID(impEstimation.getJP_Line_User1_ID());
		}else {
			line.setUser1_ID(0);
		}

		if(impEstimation.getJP_Line_User2_ID() > 0)
		{
			line.setUser2_ID(impEstimation.getJP_Line_User2_ID());
		}else {
			line.setUser2_ID(0);
		}


		ModelValidationEngine.get().fireImportValidate(this, impEstimation, line, ImportValidator.TIMING_AFTER_IMPORT);

		try {
			line.saveEx(get_TrxName());
		}catch (Exception e) {

			if(isEntryToPriceList(estimation,line))
			{
				;//Noting to do
			}else {
				message = Msg.getMsg(getCtx(),"SaveIgnored") + Msg.getElement(getCtx(), "JP_EstimationLine_ID") +" : " + e.toString();
				return false;
			}

		}

		impEstimation.setJP_EstimationLine_ID(line.getJP_EstimationLine_ID());
		impEstimation.setI_IsImported(true);
		impEstimation.setProcessed(true);
		impEstimation.saveEx(get_TrxName());

		return true;
	}


	/**
	 *
	 * Entry to Price List
	 *
	 *
	 * @param estimation
	 * @param line
	 * @return
	 */
	private boolean isEntryToPriceList(MEstimation estimation, MEstimationLine line)
	{
		if(estimation.getM_PriceList_ID() <= 0 || line.getM_Product_ID() <= 0)
			return false;


		MPriceList m_PriceList = new MPriceList(getCtx(), estimation.getM_PriceList_ID(),get_TrxName());
		MPriceListVersion m_PriceListVersion = m_PriceList.getPriceListVersion(estimation.getDateOrdered());
		MProductPrice m_ProductPrice = MProductPrice.get(getCtx(), m_PriceListVersion.getM_PriceList_Version_ID(), line.getM_Product_ID(), get_TrxName());
		if(m_ProductPrice == null)
		{
			m_ProductPrice = new MProductPrice(getCtx(),m_PriceListVersion.getM_PriceList_Version_ID(),line.getM_Product_ID(), get_TrxName());
			try {
				m_ProductPrice.saveEx(get_TrxName());
				//line.setPrice(order.getM_PriceList_ID());
			}catch (Exception e) {
				return false;
			}

		}else {
			return false;
		}

		try {
			line.saveEx(get_TrxName());
		}catch (Exception e) {
			return false;
		}

		return true;
	}

}	//	Import Estimation