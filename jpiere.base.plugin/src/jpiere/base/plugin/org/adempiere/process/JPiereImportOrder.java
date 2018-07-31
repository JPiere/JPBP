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
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
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

import jpiere.base.plugin.org.adempiere.model.X_I_BPartnerJP;
import jpiere.base.plugin.org.adempiere.model.X_I_OrderJP;

/**
 *	JPIERE-0097:Import Order from I_OrderJP
 *
 * 	@version 	2018-07-29
 *
 *  @author Hideaki Hagiwara
 */
public class JPiereImportOrder extends SvrProcess  implements ImportProcess
{
	/**	Client to be imported to		*/
	private int				p_AD_Client_ID = 0;

	/**	Delete old Imported				*/
	private boolean			p_deleteOldImported = false;
	/**	Document Action					*/
	private String			p_docAction = MOrder.DOCACTION_Prepare;

	/** Effective						*/
	private Timestamp		p_DateValue = null;

	private String message = null;

	private String p_JP_ImportSalesRepIdentifier = JPiereImportUser.JP_ImportUserIdentifier_Value;

	private String p_JP_ImportUserIdentifier = JPiereImportUser.JP_ImportUserIdentifier_Name;

	private String p_JP_ImportDropShipUserIdentifier = JPiereImportUser.JP_ImportUserIdentifier_Name;

	private String p_JP_ImportInvoiceUserIdentifier = JPiereImportUser.JP_ImportUserIdentifier_Name;

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
				p_deleteOldImported = "Y".equals(para[i].getParameter());
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
			else if (name.equals("IsHistoricalDataMigration"))
				p_IsHistoricalDataMigration = para[i].getParameterAsBoolean();
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
			sql = new StringBuilder ("DELETE I_OrderJP ")
				  .append("WHERE I_IsImported='Y' ").append (getWhereClause());
			no = DB.executeUpdate(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("Delete Old Impored =" + no);
		}


		/** Reset I_ErrorMsg */
		sql = new StringBuilder ("UPDATE I_OrderJP ")
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
		if(reverseLookupC_Order_ID())
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


		//Ship & Receipt info
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


		ModelValidationEngine.get().fireImportValidate(this, null, null, ImportValidator.TIMING_AFTER_VALIDATE);


		/** Create BPartner */
		message = Msg.getMsg(getCtx(), "CreateNew") + " : " + Msg.getElement(getCtx(), "C_BPartner_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(createBPartner())
			commitEx();
		else
			return message;


		/** Create Orders */
		message = Msg.getMsg(getCtx(), "CreateNew") + " : " + Msg.getElement(getCtx(), "C_Order_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);

		sql = new StringBuilder ("SELECT * FROM I_OrderJP ")
			  .append("WHERE I_IsImported='N'").append (getWhereClause())
			.append(" ORDER BY DateOrdered, C_BPartner_ID, BillTo_ID, C_BPartner_Location_ID, DocumentNo, I_OrderJP_ID");

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
			MOrder order = null;
			MOrderLine line = null;
			X_I_OrderJP imp = null;
			int lineNo = 0;
			boolean isCreateHeader= true;

			while (rs.next ())
			{
				recordsNum++;

				imp = new X_I_OrderJP (getCtx (), rs, get_TrxName());

				//Re-Import
				if(imp.getC_Order_ID() > 0)
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

				//	New Order
				isCreateHeader= true;
				if (lastC_BPartner_ID != imp.getC_BPartner_ID()
					|| lastC_BPartner_Location_ID != imp.getC_BPartner_Location_ID()
					|| lastBillTo_ID != imp.getBillTo_ID()
					|| !lastDocumentNo.equals(cmpDocumentNo))
				{
					if (order != null)
					{
						if(p_IsHistoricalDataMigration)
						{
							order.setDocStatus(DocAction.STATUS_Closed);
							order.setDocAction(DocAction.ACTION_None);
							order.setProcessed(true);
							order.setPosted(true);

						}else {

							if(!imp.getDocAction().equals(DocAction.ACTION_None))
							{
								if(!order.processIt(order.getDocAction()))
								{
									rollback();
									message = "Order Process Failed: " + order.getProcessMsg();
									order = null;

									imp.setI_ErrorMsg(Msg.getMsg(getCtx(), "Error") + message);
									imp.saveEx(get_TrxName());
									commitEx();
								}
							}
						}

						if(order != null)
						{
							order.saveEx(get_TrxName());
							order = null;
							commitEx();
						}

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
					order = new MOrder (getCtx(), 0, get_TrxName());
					lineNo = 0;

					if(createOrderHeader(imp, order))
					{
						successCreateDocHeader++;
					}else {

						rollback();
						order = null;

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

				if(order == null)
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


				imp.setC_Order_ID(order.getC_Order_ID());

				//Create OrderLine
				line = new MOrderLine(order);
				lineNo = lineNo + 10;

				if(addOrderLine(imp, order,line, lineNo))
				{
					successCreateDocLine++;
					successNum++;

				}else {

					rollback();
					order = null;

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
			if (order != null)
			{
				if(p_IsHistoricalDataMigration)
				{
					order.setDocStatus(DocAction.STATUS_Closed);
					order.setDocAction(DocAction.ACTION_None);
					order.setProcessed(true);
					order.setPosted(true);

				}else {

					if(!order.processIt (order.getDocAction()))
					{
						rollback();
						message = "Order Process Failed: " + order.getProcessMsg();
						order = null;

						imp.setI_ErrorMsg(Msg.getMsg(getCtx(), "Error") + message);
						imp.saveEx(get_TrxName());
						commitEx();
					}
				}

				if(order != null)
				{
					order.saveEx(get_TrxName());
					order = null;
					commitEx();
				}

			}//if (order != null)

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
		return X_I_OrderJP.Table_Name;
	}


	/**
	 * Reverse Lookup C_Order_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupC_Order_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_OrderJP i ")
				.append("SET C_Order_ID=(SELECT MAX(C_Order_ID) FROM C_Order p")
				.append(" WHERE i.DocumentNo=p.DocumentNo AND p.AD_Client_ID=i.AD_Client_ID ) ")
				.append(" WHERE i.DocumentNo IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}

		return true;

	}//reverseLookupC_Order_ID

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
		StringBuilder sql = new StringBuilder ("UPDATE I_OrderJP i ")
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
		sql = new StringBuilder ("UPDATE I_OrderJP ")
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

		//Lookup -DocType of PO from DocTypeName
		 StringBuilder sql = new StringBuilder ("UPDATE I_OrderJP o ")	//	PO Document Type Name
			  .append("SET C_DocType_ID=(SELECT C_DocType_ID FROM C_DocType d WHERE d.Name=o.DocTypeName")
			  .append(" AND d.DocBaseType='POO' AND o.AD_Client_ID=d.AD_Client_ID) ")
			  .append("WHERE C_DocType_ID IS NULL AND IsSOTrx='N' AND DocTypeName IS NOT NULL AND I_IsImported<>'Y'").append (getWhereClause());

		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}

		//Lookup -DocType of SO from DocTypeName
		sql = new StringBuilder ("UPDATE I_OrderJP o ")	//	SO Document Type Name
			  .append("SET C_DocType_ID=(SELECT C_DocType_ID FROM C_DocType d WHERE d.Name=o.DocTypeName")
			  .append(" AND d.DocBaseType='SOO' AND o.AD_Client_ID=d.AD_Client_ID) ")
			  .append("WHERE C_DocType_ID IS NULL AND IsSOTrx='Y' AND DocTypeName IS NOT NULL AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}

		//Loolup - C_DocType_ID from DocTypeName
		sql = new StringBuilder ("UPDATE I_OrderJP o ")
			  .append("SET C_DocType_ID=(SELECT C_DocType_ID FROM C_DocType d WHERE d.Name=o.DocTypeName")
			  .append(" AND d.DocBaseType IN ('SOO','POO') AND o.AD_Client_ID=d.AD_Client_ID) ")
			  .append("WHERE C_DocType_ID IS NULL AND DocTypeName IS NOT NULL AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}

		//Invalid DocTypeName
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "DocTypeName");
		sql = new StringBuilder ("UPDATE I_OrderJP ")
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

		//Set Default DocType of PO When C_DocType_ID is null
		sql = new StringBuilder ("UPDATE I_OrderJP o ")	//	Default PO
			  .append("SET C_DocType_ID=(SELECT MAX(C_DocType_ID) FROM C_DocType d WHERE d.IsDefault='Y'")
			  .append(" AND d.DocBaseType='POO' AND o.AD_Client_ID=d.AD_Client_ID) ")
			  .append("WHERE C_DocType_ID IS NULL AND IsSOTrx='N' AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}

		//Set Default DocType of SO When C_DocType_ID is null
		sql = new StringBuilder ("UPDATE I_OrderJP o ")
			  .append("SET C_DocType_ID=(SELECT MAX(C_DocType_ID) FROM C_DocType d WHERE d.IsDefault='Y'")
			  .append(" AND d.DocBaseType='SOO' AND o.AD_Client_ID=d.AD_Client_ID) ")
			  .append("WHERE C_DocType_ID IS NULL AND IsSOTrx='Y' AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}

		//Set Default DocType When C_DocType_ID is null
		sql = new StringBuilder ("UPDATE I_OrderJP o ")
			  .append("SET C_DocType_ID=(SELECT MAX(C_DocType_ID) FROM C_DocType d WHERE d.IsDefault='Y'")
			  .append(" AND d.DocBaseType IN('SOO','POO') AND o.AD_Client_ID=d.AD_Client_ID) ")
			  .append("WHERE C_DocType_ID IS NULL AND IsSOTrx IS NULL AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}

		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "JP_Null")+Msg.getElement(getCtx(), "C_DocType_ID");
		sql =  new StringBuilder ("UPDATE I_OrderJP ")
			  .append("SET I_ErrorMsg='"+ message + "'")
			  .append(" WHERE C_DocType_ID IS NULL")
			  .append(" AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}

		//	Set IsSOTrx
		sql = new StringBuilder ("UPDATE I_OrderJP o SET IsSOTrx='Y' ")
			  .append("WHERE EXISTS (SELECT * FROM C_DocType d WHERE o.C_DocType_ID=d.C_DocType_ID AND d.DocBaseType='SOO' AND o.AD_Client_ID=d.AD_Client_ID)")
			  .append(" AND C_DocType_ID IS NOT NULL")
			  .append(" AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}

		sql = new StringBuilder ("UPDATE I_OrderJP o SET IsSOTrx='N' ")
			  .append("WHERE EXISTS (SELECT * FROM C_DocType d WHERE o.C_DocType_ID=d.C_DocType_ID AND d.DocBaseType='POO' AND o.AD_Client_ID=d.AD_Client_ID)")
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
			sql = new StringBuilder ("UPDATE I_OrderJP i ")
					.append("SET SalesRep_ID=(SELECT MAX(AD_User_ID) FROM AD_User p INNER JOIN C_BPartner bp ON (p.C_BPartner_ID = bp.C_BPartner_ID) ")
					.append(" WHERE i.JP_SalesRep_EMail=p.EMail AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) AND bp.IsSalesRep='Y' ) ")
					.append(" WHERE i.JP_SalesRep_EMail IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportSalesRepIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_Name)) { //Name

			sql = new StringBuilder ("UPDATE I_OrderJP i ")
					.append("SET SalesRep_ID=(SELECT MAX(AD_User_ID) FROM AD_User p INNER JOIN C_BPartner bp ON (p.C_BPartner_ID = bp.C_BPartner_ID) ")
					.append(" WHERE i.JP_SalesRep_Name=p.Name AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) AND bp.IsSalesRep='Y') ")
					.append(" WHERE i.JP_SalesRep_Name IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportSalesRepIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_Value)) { //Value

			sql = new StringBuilder ("UPDATE I_OrderJP i ")
					.append("SET SalesRep_ID=(SELECT MAX(AD_User_ID) FROM AD_User p INNER JOIN C_BPartner bp ON (p.C_BPartner_ID = bp.C_BPartner_ID) ")
					.append(" WHERE i.JP_SalesRep_Value=p.Value AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) AND bp.IsSalesRep='Y'  ) ")
					.append(" WHERE i.JP_SalesRep_Value IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportSalesRepIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueEMail)) { //Value + E-Mail

			//In case of EMail is not null
			sql = new StringBuilder ("UPDATE I_OrderJP i ")
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
			sql = new StringBuilder ("UPDATE I_OrderJP i ")
					.append("SET SalesRep_ID=(SELECT MAX(AD_User_ID) FROM AD_User p INNER JOIN C_BPartner bp ON (p.C_BPartner_ID = bp.C_BPartner_ID) ")
					.append(" WHERE i.JP_SalesRep_Value=p.Value AND i.JP_SalesRep_EMail IS NULL l AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) AND bp.IsSalesRep='Y' ) ")
					.append(" WHERE i.JP_SalesRep_Value IS NOT NULL AND i.JP_SalesRep_EMail IS NULL AND i.SalesRep_ID IS NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());


		}else if(p_JP_ImportSalesRepIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueName)) { //Value + Name

			sql = new StringBuilder ("UPDATE I_OrderJP i ")
					.append("SET SalesRep_ID=(SELECT MAX(AD_User_ID) FROM AD_User p INNER JOIN C_BPartner bp ON (p.C_BPartner_ID = bp.C_BPartner_ID) ")
					.append(" WHERE i.JP_SalesRep_Value=p.Value AND i.JP_SalesRep_Name=p.Name AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) AND bp.IsSalesRep='Y'  ) ")
					.append(" WHERE i.JP_SalesRep_Value IS NOT NULL AND i.JP_SalesRep_Name IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportSalesRepIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueNameEmail)) { //Value + Name + EMail

			//In case of EMail is not null
			sql = new StringBuilder ("UPDATE I_OrderJP i ")
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
			sql = new StringBuilder ("UPDATE I_OrderJP i ")
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
		StringBuilder sql = new StringBuilder ("UPDATE I_OrderJP o ")
				  .append("SET M_PriceList_ID=(SELECT MAX(M_PriceList_ID) FROM M_PriceList p ")
				  .append(" WHERE p.Name=o.JP_PriceList_Name AND o.AD_Client_ID=p.AD_Client_ID) ")
				  .append(" WHERE M_PriceList_ID IS NULL AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Set C_Currency_ID from  M_PriceList_ID
		sql = new StringBuilder ("UPDATE I_OrderJP o ")
				  .append("SET C_Currency_ID=(SELECT C_Currency_ID FROM M_PriceList p ")
				  .append(" WHERE p.M_PriceList_ID=o.M_PriceList_ID ) ")
				  .append(" WHERE I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Set M_PriceList_ID from Default
		sql = new StringBuilder ("UPDATE I_OrderJP o ")
				  .append("SET M_PriceList_ID=(SELECT MAX(M_PriceList_ID) FROM M_PriceList p WHERE p.IsDefault='Y'")
				  .append(" AND p.C_Currency_ID=o.C_Currency_ID AND p.IsSOPriceList=o.IsSOTrx AND o.AD_Client_ID=p.AD_Client_ID) ")
				  .append("WHERE M_PriceList_ID IS NULL AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Set M_PriceList_ID from Default
		sql = new StringBuilder ("UPDATE I_OrderJP o ")
			  .append("SET M_PriceList_ID=(SELECT MAX(M_PriceList_ID) FROM M_PriceList p WHERE p.IsDefault='Y'")
			  .append(" AND p.IsSOPriceList=o.IsSOTrx AND o.AD_Client_ID=p.AD_Client_ID) ")
			  .append("WHERE M_PriceList_ID IS NULL AND C_Currency_ID IS NULL AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		sql = new StringBuilder ("UPDATE I_OrderJP o ")
			  .append("SET M_PriceList_ID=(SELECT MAX(M_PriceList_ID) FROM M_PriceList p ")
			  .append(" WHERE p.C_Currency_ID=o.C_Currency_ID AND p.IsSOPriceList=o.IsSOTrx AND o.AD_Client_ID=p.AD_Client_ID) ")
			  .append("WHERE M_PriceList_ID IS NULL AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		sql = new StringBuilder ("UPDATE I_OrderJP o ")
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
		sql = new StringBuilder ("UPDATE I_OrderJP ")
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

		StringBuilder sql = new StringBuilder ("UPDATE I_OrderJP i ")
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
		sql = new StringBuilder ("UPDATE I_OrderJP ")
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
	 * Reverse Lookup C_Campaign_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupC_Campaign_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_OrderJP i ")
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
		sql = new StringBuilder ("UPDATE I_OrderJP ")
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
	 * Reverse Lookup C_Activity_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupC_Activity_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_OrderJP i ")
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
		sql = new StringBuilder ("UPDATE I_OrderJP ")
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

		StringBuilder sql = new StringBuilder ("UPDATE I_OrderJP i ")
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
		sql = new StringBuilder ("UPDATE I_OrderJP ")
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
	 * Reverse Lookup User1_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupUser1_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_OrderJP i ")
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
		sql = new StringBuilder ("UPDATE I_OrderJP ")
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
	 * Reverse Lookup User2_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupUser2_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_OrderJP i ")
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
		sql = new StringBuilder ("UPDATE I_OrderJP ")
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
	 * Reverse Lookup C_OrderSource_ID
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean reverseLookupC_OrderSource_ID() throws Exception
	{
		int no = 0;
		StringBuilder sql = new StringBuilder ("UPDATE I_OrderJP o ")
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
		sql = new StringBuilder ("UPDATE I_OrderJP ")
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

		StringBuilder sql = new StringBuilder ("UPDATE I_OrderJP o ")
			  .append("SET C_PaymentTerm_ID=(SELECT C_PaymentTerm_ID FROM C_PaymentTerm p")
			  .append(" WHERE o.PaymentTermValue=p.Value AND o.AD_Client_ID=p.AD_Client_ID) ")
			  .append("WHERE C_PaymentTerm_ID IS NULL AND PaymentTermValue IS NOT NULL AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Set Default
		sql = new StringBuilder ("UPDATE I_OrderJP o ")
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
		sql = new StringBuilder ("UPDATE I_OrderJP ")
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

		StringBuilder sql = new StringBuilder ("UPDATE I_OrderJP i ")
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
		sql = new StringBuilder ("UPDATE I_OrderJP ")
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

		StringBuilder sql = new StringBuilder ("UPDATE I_OrderJP i ")
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
		sql = new StringBuilder ("UPDATE I_OrderJP ")
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
			sql = new StringBuilder ("UPDATE I_OrderJP i ")
					.append("SET Bill_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_Bill_User_EMail=p.EMail AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.Bill_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_Bill_User_EMail IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportInvoiceUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_Name)) { //Name

			sql = new StringBuilder ("UPDATE I_OrderJP i ")
					.append("SET Bill_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_Bill_User_Name=p.Name AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.Bill_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_Bill_User_Name IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportInvoiceUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_Value)) { //Value

			sql = new StringBuilder ("UPDATE I_OrderJP i ")
					.append("SET Bill_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_Bill_User_Value=p.Value AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.Bill_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_Bill_User_Value IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportInvoiceUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueEMail)) { //Value + E-Mail

			//In case of EMail is not null
			sql = new StringBuilder ("UPDATE I_OrderJP i ")
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
			sql = new StringBuilder ("UPDATE I_OrderJP i ")
					.append("SET Bill_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_Bill_User_Value=p.Value AND i.JP_Bill_User_EMail IS NULL AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.Bill_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_Bill_User_Value IS NOT NULL AND i.JP_Bill_User_EMail IS NULL AND Bill_User_ID IS NOT NULL ")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportInvoiceUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueName)) { //Value + Name

			sql = new StringBuilder ("UPDATE I_OrderJP i ")
					.append("SET Bill_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_Bill_User_Value=p.Value AND i.JP_Bill_User_Name=p.Name AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.Bill_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_Bill_User_Value IS NOT NULL AND i.JP_Bill_User_Name IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportInvoiceUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueNameEmail)) { //Value + Name + EMail

			//In case of EMail is not null
			sql = new StringBuilder ("UPDATE I_OrderJP i ")
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
			sql = new StringBuilder ("UPDATE I_OrderJP i ")
					.append("SET Bill_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_Bill_User_Value=p.Value AND i.JP_Bill_User_Name=p.Name  AND i.JP_Bill_User_EMail IS NULL AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.Bill_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_Bill_User_Value IS NOT NULL AND i.JP_Bill_User_Name IS NOT NULL AND i.JP_Bill_User_EMail IS NULL AND Bill_User_ID IS NOT NULL ")
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
		StringBuilder sql = new StringBuilder ("UPDATE I_OrderJP o ")
				  .append("SET M_Warehouse_ID=(SELECT M_Warehouse_ID FROM M_Warehouse w")
				  .append(" WHERE o.JP_Warehouse_Value=w.Value AND o.AD_Org_ID=w.AD_Org_ID) ")
				  .append(" WHERE M_Warehouse_ID IS NULL AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(message +" : " + e.toString() +" : " + sql);
		}

		sql = new StringBuilder ("UPDATE I_OrderJP o ")
			  .append("SET M_Warehouse_ID=(SELECT MAX(M_Warehouse_ID) FROM M_Warehouse w")
			  .append(" WHERE o.AD_Client_ID=w.AD_Client_ID AND o.AD_Org_ID=w.AD_Org_ID) ")
			  .append("WHERE M_Warehouse_ID IS NULL AND I_IsImported<>'Y'").append (getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(message +" : " + e.toString() +" : " + sql);
		}

		sql = new StringBuilder ("UPDATE I_OrderJP o ")
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
		sql = new StringBuilder ("UPDATE I_OrderJP ")
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

		StringBuilder sql = new StringBuilder ("UPDATE I_OrderJP i ")
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
		sql = new StringBuilder ("UPDATE I_OrderJP ")
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

		StringBuilder sql = new StringBuilder ("UPDATE I_OrderJP i ")
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
		sql = new StringBuilder ("UPDATE I_OrderJP ")
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

		StringBuilder sql = new StringBuilder ("UPDATE I_OrderJP i ")
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
		sql = new StringBuilder ("UPDATE I_OrderJP ")
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

		StringBuilder sql = new StringBuilder ("UPDATE I_OrderJP i ")
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
		sql = new StringBuilder ("UPDATE I_OrderJP ")
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
			sql = new StringBuilder ("UPDATE I_OrderJP i ")
					.append("SET DropShip_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_DropShip_User_EMail=p.EMail AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.DropShip_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_DropShip_User_EMail IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportDropShipUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_Name)) { //Name

			sql = new StringBuilder ("UPDATE I_OrderJP i ")
					.append("SET DropShip_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_DropShip_User_Name=p.Name AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.DropShip_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_DropShip_User_Name IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportDropShipUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_Value)) { //Value

			sql = new StringBuilder ("UPDATE I_OrderJP i ")
					.append("SET DropShip_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_DropShip_User_Value=p.Value AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.DropShip_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_DropShip_User_Value IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportDropShipUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueEMail)) { //Value + E-Mail

			//In case of EMail is not null
			sql = new StringBuilder ("UPDATE I_OrderJP i ")
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
			sql = new StringBuilder ("UPDATE I_OrderJP i ")
					.append("SET DropShip_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_DropShip_User_Value=p.Value AND i.JP_DropShip_User_EMail IS NULL AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.DropShip_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_DropShip_User_Value IS NOT NULL AND i.JP_DropShip_User_EMail IS NULL AND DropShip_User_ID IS NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportDropShipUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueName)) { //Value + Name

			sql = new StringBuilder ("UPDATE I_OrderJP i ")
					.append("SET DropShip_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_DropShip_User_Value=p.Value AND i.JP_Bill_User_Name=p.Name AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.DropShip_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_DropShip_User_Value IS NOT NULL AND i.JP_DropShip_User_Name IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportDropShipUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueNameEmail)) { //Value + Name + EMail

			//In case of EMail is not null
			sql = new StringBuilder ("UPDATE I_OrderJP i ")
					.append("SET DropShip_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_DropShip_User_Value=p.Value AND i.JP_Bill_User_Name=p.Name  AND i.JP_Bill_User_EMail=p.EMail AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
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
			sql = new StringBuilder ("UPDATE I_OrderJP i ")
					.append("SET DropShip_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_DropShip_User_Value=p.Value AND i.JP_Bill_User_Name=p.Name  AND i.JP_Bill_User_EMail IS NULL AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
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
		StringBuilder  sql = new StringBuilder ("UPDATE I_OrderJP i ")
			.append("SET C_BPartner_ID=(SELECT C_BPartner_ID FROM C_BPartner p")
			.append(" WHERE i.BPartnerValue=p.Value AND i.AD_Client_ID=p.AD_Client_ID) ")
			.append("WHERE i.C_BPartner_ID IS NULL AND i.BPartnerValue IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
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

		StringBuilder sql = new StringBuilder ("UPDATE I_OrderJP i ")
				.append("SET C_BPartner_Location_ID=(SELECT C_BPartner_Location_ID FROM C_BPartner_Location p")
				.append(" WHERE i.JP_BPartner_Location_Name=p.Name AND i.C_BPartner_ID=p.C_BPartner_ID) ")
				.append("WHERE i.C_BPartner_Location_ID IS NULL AND i.JP_BPartner_Location_Name IS NOT NULL ")
				.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}


		sql = new StringBuilder ("UPDATE I_OrderJP i ")
				.append("SET C_BPartner_Location_ID=(SELECT max(C_BPartner_Location_ID) FROM C_BPartner_Location p")
				.append(" WHERE i.C_BPartner_ID=p.C_BPartner_ID AND i.Phone = p.phone) ")
				.append(" WHERE i.C_BPartner_Location_ID IS NULL AND i.JP_BPartner_Location_Name IS NULL ")
				.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
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
			sql = new StringBuilder ("UPDATE I_OrderJP i ")
					.append("SET AD_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.EMail=p.EMail AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.C_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.EMail IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_Name)) { //Name

			sql = new StringBuilder ("UPDATE I_OrderJP i ")
					.append("SET AD_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.ContactName=p.Name AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.C_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.ContactName IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_Value)) { //Value

			sql = new StringBuilder ("UPDATE I_OrderJP i ")
					.append("SET AD_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_User_Value=p.Value AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.C_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_User_Value IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueEMail)) { //Value + E-Mail

			//In case of EMail is not null
			sql = new StringBuilder ("UPDATE I_OrderJP i ")
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
			sql = new StringBuilder ("UPDATE I_OrderJP i ")
					.append("SET AD_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_User_Value=p.Value AND i.EMail IS NULL AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.C_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_User_Value IS NOT NULL AND i.EMail IS NULL AND i.AD_User_ID IS NOT NULL ")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueName)) { //Value + Name

			sql = new StringBuilder ("UPDATE I_OrderJP i ")
					.append("SET AD_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_User_Value=p.Value AND i.ContactName=p.Name AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.C_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_User_Value IS NOT NULL AND i.ContactName IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueNameEmail)) { //Value + Name + EMail

			//In case of EMail is not null
			sql = new StringBuilder ("UPDATE I_OrderJP i ")
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
			sql = new StringBuilder ("UPDATE I_OrderJP i ")
					.append("SET AD_User_ID=(SELECT MAX(AD_User_ID) FROM AD_User p")
					.append(" WHERE i.JP_User_Value=p.Value AND i.ContactName=p.Name  AND i.EMail IS NULL AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 )")
					.append(" AND i.C_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_User_Value IS NOT NULL AND i.ContactName IS NOT NULL AND i.EMail IS NULL AND i.AD_User_ID IS NOT NULL")
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
		StringBuilder	sql = new StringBuilder ("UPDATE I_OrderJP o ")
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
		sql = new StringBuilder ("UPDATE I_OrderJP o ")
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
		sql = new StringBuilder ("UPDATE I_OrderJP o ")
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
		sql = new StringBuilder ("UPDATE I_OrderJP ")
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

		StringBuilder sql = new StringBuilder ("UPDATE I_OrderJP o ")
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
		sql = new StringBuilder ("UPDATE I_OrderJP ")
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

		StringBuilder sql = new StringBuilder ("UPDATE I_OrderJP i ")
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
		sql = new StringBuilder ("UPDATE I_OrderJP ")
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

		StringBuilder sql = new StringBuilder ("UPDATE I_OrderJP o ")
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
		sql =  new StringBuilder ("UPDATE I_OrderJP ")
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

		ArrayList<X_I_OrderJP> orderlist = new ArrayList<X_I_OrderJP>();
		String sql = "SELECT * FROM I_OrderJP WHERE I_IsImported<>'Y' AND C_BPartner_ID IS NULL AND BPartnerValue IS NOT NULL "
							+ getWhereClause() + " ORDER BY BPartnerValue ";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, get_TrxName());
			rs = pstmt.executeQuery();
			String preValue = "";
			X_I_OrderJP impOrder =null;

			while (rs.next())
			{
				impOrder = new X_I_OrderJP (getCtx (), rs, get_TrxName());
				if(preValue != null && preValue.equals(impOrder.getBPartnerValue()))
				{
					;//Noting to do
				}else {
					orderlist.add(impOrder);
					preValue = impOrder.getBPartnerValue();
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
		for(X_I_OrderJP i_order : orderlist)
		{
			i_BP = new X_I_BPartnerJP (getCtx (), 0, get_TrxName());
			PO.copyValues(i_order, i_BP);
			i_BP.setJP_Org_Value(i_order.getJP_BP_Org_Value());
			i_BP.setValue(i_order.getBPartnerValue());
			if(Util.isEmpty(i_order.getJP_BPartner_Location_Name()))
				i_BP.setJP_BPartner_Location_Name(i_order.getName());
			if(Util.isEmpty(i_order.getJP_Location_Label()))
				i_BP.setJP_Location_Label(i_BP.getJP_BPartner_Location_Name());
			if(Util.isEmpty(i_order.getContactName()))
				i_BP.setContactName(i_order.getName());

			if(i_order.isSOTrx())
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
	 * Create Order Header
	 *
	 * @param impOrder
	 * @param order
	 * @return
	 */
	private boolean createOrderHeader(X_I_OrderJP impOrder, MOrder order)
	{
		ModelValidationEngine.get().fireImportValidate(this, impOrder, order, ImportValidator.TIMING_BEFORE_IMPORT);

		PO.copyValues(impOrder, order);
		order.setC_Order_ID(0);
		order.setClientOrg (impOrder.getAD_Client_ID(), impOrder.getAD_Org_ID());
		order.setC_DocTypeTarget_ID(impOrder.getC_DocType_ID());
		order.setIsSOTrx(impOrder.isSOTrx());

		//Date
		if(impOrder.getDateOrdered() == null)
		{
			impOrder.setDateOrdered(p_DateValue);
			order.setDateOrdered(p_DateValue);
		}

		if(impOrder.getDateAcct() == null)
		{
			impOrder.setDateAcct(p_DateValue);
			order.setDateAcct(p_DateValue);
		}

		if(impOrder.getDatePromised() == null)
		{
			impOrder.setDatePromised(p_DateValue);
			order.setDatePromised(p_DateValue);
		}

		//Busienss Partner
		MBPartner bp = null;
		if(impOrder.getC_BPartner_ID() > 0)
		{
			bp =MBPartner.get(getCtx(), impOrder.getC_BPartner_ID());

		}else if(!Util.isEmpty(impOrder.getBPartnerValue())) {

			bp =MBPartner.get(getCtx(), impOrder.getBPartnerValue(),get_TrxName());
			impOrder.setC_BPartner_ID(bp.getC_BPartner_ID());
			order.setC_BPartner_ID(bp.getC_BPartner_ID());

		}else {
			message = Msg.getMsg(getCtx(), "JP_Null") + Msg.getElement(getCtx(), "C_BPartner_ID");
			impOrder.setI_ErrorMsg(message);
			return false;
		}

		if(impOrder.getC_BPartner_Location_ID() > 0)
		{
			;
		}else {

			MBPartnerLocation[] locations = bp.getLocations(false);
			if(locations != null && locations.length == 1 )
			{
				impOrder.setC_BPartner_Location_ID(locations[0].getC_BPartner_Location_ID());
				order.setC_BPartner_Location_ID(locations[0].getC_BPartner_Location_ID());
			}
		}

		if(impOrder.getAD_User_ID() > 0)
		{
			;
		}else {

			MUser[] users =MUser.getOfBPartner(getCtx(), impOrder.getC_BPartner_ID(),get_TrxName());
			if(users != null && users.length == 1 )
			{
				impOrder.setAD_User_ID(users[0].getAD_User_ID());
				order.setAD_User_ID(users[0].getAD_User_ID());
			}

		}

		if(Util.isEmpty(impOrder.getDeliveryRule()))
		{
			if(Util.isEmpty(bp.getDeliveryRule()))
			{
				impOrder.setDeliveryRule(X_I_OrderJP.DELIVERYRULE_Availability);
				order.setDeliveryRule(X_I_OrderJP.DELIVERYRULE_Availability);

			}else {
				impOrder.setDeliveryRule(bp.getDeliveryRule());
				order.setDeliveryRule(bp.getDeliveryRule());
			}
		}

		if(Util.isEmpty(impOrder.getPriorityRule()))
		{
			impOrder.setPriorityRule(X_I_OrderJP.PRIORITYRULE_Medium);
			order.setPriorityRule(X_I_OrderJP.PRIORITYRULE_Medium);
		}

		if(Util.isEmpty(impOrder.getDeliveryViaRule()))
		{
			if(Util.isEmpty(bp.getDeliveryViaRule()))
			{
				impOrder.setDeliveryViaRule(X_I_OrderJP.DELIVERYVIARULE_Pickup);
				order.setDeliveryViaRule(X_I_OrderJP.DELIVERYVIARULE_Pickup);

			}else {
				impOrder.setDeliveryViaRule(bp.getDeliveryViaRule());
				order.setDeliveryViaRule(bp.getDeliveryViaRule());
			}
		}

		if(Util.isEmpty(impOrder.getFreightCostRule()))
		{
			if(Util.isEmpty(bp.getFreightCostRule()))
			{
				impOrder.setFreightCostRule(X_I_OrderJP.FREIGHTCOSTRULE_FixPrice);
				order.setFreightCostRule(X_I_OrderJP.FREIGHTCOSTRULE_FixPrice);

			}else {
				impOrder.setFreightCostRule(bp.getFreightCostRule());
				order.setFreightCostRule(bp.getFreightCostRule());
			}
		}

		if(impOrder.getM_PriceList_ID() == 0)
		{
			if(bp.getM_PriceList_ID() == 0)
			{
				;//set Default

			}else {
				impOrder.setM_PriceList_ID(bp.getM_PriceList_ID());
				order.setM_PriceList_ID(bp.getM_PriceList_ID());
			}
		}

		if(Util.isEmpty(impOrder.getInvoiceRule()))
		{
			if(Util.isEmpty(bp.getInvoiceRule()))
			{
				impOrder.setInvoiceRule(X_I_OrderJP.INVOICERULE_Immediate);
				order.setInvoiceRule(X_I_OrderJP.INVOICERULE_Immediate);

			}else {
				impOrder.setInvoiceRule(bp.getInvoiceRule());
				order.setInvoiceRule(bp.getInvoiceRule());
			}
		}

		if(Util.isEmpty(impOrder.getPaymentRule()))
		{
			if(Util.isEmpty(bp.getPaymentRule()))
			{
				//set Default
				impOrder.setPaymentRule(X_I_OrderJP.PAYMENTRULE_OnCredit);
				order.setPaymentRule(X_I_OrderJP.PAYMENTRULE_OnCredit);

			}else {
				impOrder.setPaymentRule(bp.getPaymentRule());
				order.setPaymentRule(bp.getPaymentRule());
			}
		}

		if(impOrder.getC_PaymentTerm_ID() == 0)
		{
			if(bp.getC_PaymentTerm_ID() == 0)
			{
				;//set Default

			}else {
				impOrder.setC_PaymentTerm_ID(bp.getC_PaymentTerm_ID());
				order.setC_PaymentTerm_ID(bp.getC_PaymentTerm_ID());
			}
		}


		//DocStatus
		if(Util.isEmpty(impOrder.getDocStatus()))
		{
			order.setDocStatus(DocAction.STATUS_Drafted);
		}else {
			order.setDocStatus(impOrder.getDocStatus());
		}

		if(Util.isEmpty(impOrder.getDocAction()))
		{
			if (p_docAction != null && p_docAction.length() > 0)
			{
				order.setDocAction(p_docAction);

			}else {

				order.setDocAction(DocAction.ACTION_Complete);
			}

		}else if(impOrder.getDocAction().equals(DocAction.ACTION_None)) {

			//set DocAction but noting to do;
			if(impOrder.getDocStatus().equals(DocAction.STATUS_Closed) || impOrder.getDocStatus().equals(DocAction.STATUS_Voided)
					|| impOrder.getDocStatus().equals(DocAction.STATUS_Reversed))
			{
				order.setDocAction(DocAction.ACTION_None);

			}else if(impOrder.getDocStatus().equals(DocAction.STATUS_Completed)) {

				order.setDocAction(DocAction.ACTION_Close);

			}else {

				order.setDocAction(DocAction.ACTION_Complete);

			}

		}

		ModelValidationEngine.get().fireImportValidate(this, impOrder, order, ImportValidator.TIMING_AFTER_IMPORT);

		try {
			order.saveEx(get_TrxName());
		}catch (Exception e) {

		    message = Msg.getMsg(getCtx(),"SaveIgnored") + Msg.getElement(getCtx(), "C_Order_ID") +" : " + e.toString();

			return false;
		}

		impOrder.setC_Order_ID(order.getC_Order_ID());

		return true;

	}

	/**
	 *
	 * add Order Line
	 *
	 * @param impOrder
	 * @param order
	 * @param line
	 * @param lineNo
	 * @return
	 */
	private boolean addOrderLine(X_I_OrderJP impOrder, MOrder order, MOrderLine line, int lineNo)
	{
		ModelValidationEngine.get().fireImportValidate(this, impOrder, line, ImportValidator.TIMING_BEFORE_IMPORT);

		PO.copyValues(impOrder, line);

		line.setC_Order_ID(order.getC_Order_ID());

		if(impOrder.getLine()==0)
		{
			line.setLine(lineNo);

		}else {
			line.setLine(impOrder.getLine());
		}

		if(line.getQtyEntered().compareTo(Env.ZERO) == 0 && line.getQtyOrdered().compareTo(Env.ZERO) != 0 )
		{
			line.setQtyEntered(line.getQtyOrdered());

		}else if(line.getQtyEntered().compareTo(Env.ZERO) != 0 && line.getQtyOrdered().compareTo(Env.ZERO) == 0 ) {

			line.setQtyOrdered(line.getQtyEntered());
		}

		if(p_IsHistoricalDataMigration)
		{
			line.setQtyDelivered(line.getQtyOrdered());
			line.setQtyInvoiced(line.getQtyOrdered());
			line.setQtyReserved(Env.ZERO);

			if(line.get_ColumnIndex("JP_QtyRecognized") > 0)
			{
				line.set_ValueNoCheck("JP_QtyRecognized", line.getQtyOrdered());
			}
		}

		ModelValidationEngine.get().fireImportValidate(this, impOrder, line, ImportValidator.TIMING_AFTER_IMPORT);

		try {
			line.saveEx(get_TrxName());
		}catch (Exception e) {

			message = Msg.getMsg(getCtx(),"SaveIgnored") + Msg.getElement(getCtx(), "C_OrderLine_ID") +" : " + e.toString();

			return false;
		}

		impOrder.setC_OrderLine_ID(line.getC_OrderLine_ID());
		impOrder.setI_IsImported(true);
		impOrder.setProcessed(true);
		impOrder.saveEx(get_TrxName());

		return true;
	}


}	//	ImportOrder
