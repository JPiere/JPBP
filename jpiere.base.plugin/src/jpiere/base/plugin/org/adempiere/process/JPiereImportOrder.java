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
import java.sql.Timestamp;
import java.util.logging.Level;

import org.adempiere.model.ImportValidator;
import org.adempiere.process.ImportProcess;
import org.adempiere.util.IProcessUI;
import org.compiere.model.MBPartner;
import org.compiere.model.MBPartnerLocation;
import org.compiere.model.MLocation;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MTable;
import org.compiere.model.MTableIndex;
import org.compiere.model.MUser;
import org.compiere.model.ModelValidationEngine;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.X_I_OrderJP;

/**
 *	Import Order from I_OrderJP
 *  @author Oscar Gomez
 * 			<li>BF [ 2936629 ] Error when creating bpartner in the importation order
 * 			<li>https://sourceforge.net/tracker/?func=detail&aid=2936629&group_id=176962&atid=879332
 * 	@author 	Jorg Janke
 * 	@version 	$Id: ImportOrder.java,v 1.2 2006/07/30 00:51:02 jjanke Exp $
 *
 *  @author Hideaki Hagiwara
 */
public class JPiereImportOrder extends SvrProcess  implements ImportProcess
{
	/**	Client to be imported to		*/
	private int				m_AD_Client_ID = 0;
	/**	Organization to be imported to		*/
	private int				m_AD_Org_ID = 0;
	/**	Delete old Imported				*/
	private boolean			m_deleteOldImported = false;
	/**	Document Action					*/
	private String			m_docAction = MOrder.DOCACTION_Prepare;

	private String message = null;

	private String p_JP_ImportSalesRepIdentifier = JPiereImportUser.JP_ImportUserIdentifier_Value;

	private String p_JP_ImportUserIdentifier = JPiereImportUser.JP_ImportUserIdentifier_Name;

	private String p_JP_ImportDropShipUserIdentifier = JPiereImportUser.JP_ImportUserIdentifier_Name;

	private String p_JP_ImportInvoiceUserIdentifier = JPiereImportUser.JP_ImportUserIdentifier_Name;


	/** Effective						*/
	private Timestamp		m_DateValue = null;

	private boolean			isRecordCommitJP =false;

	private boolean 		isDeleteIndexJP = false;

	private boolean			isInvalidConstraintJP = false;

	private boolean			isMonitoringProcessJP = true;

	private boolean			isUniqueCheckJP = false;

	private String[] OrderTables = new String[] {
			"C_Order",
			"C_OrderLine"
	};

	private String[] allDocumentTables = new String[] {
			"C_Order",
			"C_OrderLine",
			"M_InOut",
			"M_InOutLine",
			"C_Invoice",
			"C_InvoiceLine",
			"Fact_Acct"
	};

	/**
	 *  Prepare - e.g., get Parameters.
	 */
	protected void prepare()
	{
		m_AD_Client_ID = getAD_Client_ID();
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (name.equals("AD_Client_ID"))
				m_AD_Client_ID = ((BigDecimal)para[i].getParameter()).intValue();
			else if (name.equals("AD_Org_ID"))
				m_AD_Org_ID = ((BigDecimal)para[i].getParameter()).intValue();
			else if (name.equals("DeleteOldImported"))
				m_deleteOldImported = "Y".equals(para[i].getParameter());
			else if (name.equals("DocAction"))
				m_docAction = (String)para[i].getParameter();
			else if (name.equals("IsRecordCommitJP"))
				isRecordCommitJP = "Y".equals(para[i].getParameter());
			else if (name.equals("IsDeleteIndexJP"))
				isDeleteIndexJP = "Y".equals(para[i].getParameter());
			else if (name.equals("IsInvalidConstraintJP"))
				isInvalidConstraintJP = "Y".equals(para[i].getParameter());
			else if (name.equals("IsMonitoringProcessJP"))
				isMonitoringProcessJP = "Y".equals(para[i].getParameter());
			else if (name.equals("IsUniqueCheckJP"))
				isUniqueCheckJP = "Y".equals(para[i].getParameter());
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
		if (m_DateValue == null)
			m_DateValue = new Timestamp (System.currentTimeMillis());
	}	//	prepare


	/**
	 *  Perform process.
	 *  @return Message
	 *  @throws Exception
	 */
	protected String doIt() throws java.lang.Exception
	{
		//処理の計測
		long start = System.currentTimeMillis();

		//プロセス状況のモニタリング
		IProcessUI processMonitor = null;
		if(isMonitoringProcessJP)
		{
			processMonitor = Env.getProcessUI(getCtx());
		}

		StringBuilder sql = null;
		int no = 0;
		StringBuilder clientCheck = new StringBuilder(" AND AD_Client_ID=").append(m_AD_Client_ID);


		/** Delete Old Imported */
		if (m_deleteOldImported)
		{
			sql = new StringBuilder ("DELETE I_OrderJP ")
				  .append("WHERE I_IsImported='Y'").append (clientCheck);
			no = DB.executeUpdate(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("Delete Old Impored =" + no);
		}


		/** Reset I_ErrorMsg */
		sql = new StringBuilder ("UPDATE I_OrderJP ")
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
		//Header
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


		//TODO Business partner Info
		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_BPartner_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupC_BPartner_ID())
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


		//前処理終了
		long endPreProcessing = System.currentTimeMillis();
		addLog("前処理時間: " + (endPreProcessing - start) + " ミリ秒");


		/**
		 * 制約の無効とIndexの削除//TODO
		 */
		//制約の無効
		if(isInvalidConstraintJP)
		{
			if (processMonitor != null)	processMonitor.statusUpdate("制約の無効化");
			long startInvalidConstraint = System.currentTimeMillis();

			for(int i = 0; i < allDocumentTables.length; i++)
			{
				StringBuilder invalidConstraint = new StringBuilder (" update pg_trigger set tgenabled = 'D' "
												+ "where oid in (select tr.oid from pg_trigger tr INNER JOIN pg_class cl on (tr.tgrelid = cl.oid) WHERE cl.relname="
												+ "lower('" + allDocumentTables[i] + "') )");

				DB.executeUpdate(invalidConstraint.toString(), get_TrxName());
			}
			commitEx();

			long endInvalidConstraint = System.currentTimeMillis();
			addLog("制約無効の処理時間: " + (endInvalidConstraint - startInvalidConstraint) + " ミリ秒");
		}

		//indexの削除
		if(isDeleteIndexJP)
		{
			if (processMonitor != null)	processMonitor.statusUpdate("Indexの削除");
			long startDeleteIndex = System.currentTimeMillis();

			for(int i = 0; i < allDocumentTables.length; i++)
			{
				MTable mTable = MTable.get(getCtx(), allDocumentTables[i]);
				MTableIndex[] indexes = MTableIndex.get(mTable);
				for(int j = 0; j < indexes.length; j++)
				{
					String indexDropSql = indexes[j].getDropDDL();
					int rvalue = DB.executeUpdateEx(indexDropSql, get_TrxName());
//					addLog(0, null, new BigDecimal(rvalue), indexDropSql.toString());
				}
			}
			commitEx();

			long endDeleteIndex = System.currentTimeMillis();
			addLog("Indexの削除時間: " + (endDeleteIndex - startDeleteIndex) + " ミリ秒");
		}


		//	-- New BPartner ---------------------------------------------------

		//	Go through Order Records w/o C_BPartner_ID
		if (processMonitor != null)	processMonitor.statusUpdate("新規取引先の登録");
		long startCreateBP = System.currentTimeMillis();

		sql = new StringBuilder ("SELECT * FROM I_OrderJP ")
			  .append("WHERE I_IsImported='N' AND C_BPartner_ID IS NULL").append (clientCheck);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (sql.toString(), get_TrxName());
			rs = pstmt.executeQuery ();
			while (rs.next ())
			{
				X_I_OrderJP imp = new X_I_OrderJP (getCtx (), rs, get_TrxName());
				if (imp.getBPartnerValue () == null)
				{
					if (imp.getEMail () != null)
						imp.setBPartnerValue (imp.getEMail ());
					else if (imp.getName () != null)
						imp.setBPartnerValue (imp.getName ());
					else
						continue;
				}
				if (imp.getName () == null)
				{
					if (imp.getContactName () != null)
						imp.setName (imp.getContactName ());
					else
						imp.setName (imp.getBPartnerValue ());
				}
				//	BPartner
				MBPartner bp = MBPartner.get (getCtx(), imp.getBPartnerValue());
				if (bp == null)
				{
					bp = new MBPartner (getCtx (), -1, get_TrxName());
					bp.setClientOrg (imp.getAD_Client_ID (), imp.getAD_Org_ID ());
					bp.setValue (imp.getBPartnerValue ());
					bp.setName (imp.getName ());
					if (!bp.save ())
						continue;
				}
				imp.setC_BPartner_ID (bp.getC_BPartner_ID ());

				//	BP Location
				MBPartnerLocation bpl = null;
				MBPartnerLocation[] bpls = bp.getLocations(true);
				for (int i = 0; bpl == null && i < bpls.length; i++)
				{
					if (imp.getC_BPartner_Location_ID() == bpls[i].getC_BPartner_Location_ID())
						bpl = bpls[i];
					//	Same Location ID
					else if (imp.getC_Location_ID() == bpls[i].getC_Location_ID())
						bpl = bpls[i];
					//	Same Location Info
					else if (imp.getC_Location_ID() == 0)
					{
						MLocation loc = bpls[i].getLocation(false);
						if (loc.equals(imp.getC_Country_ID(), imp.getC_Region_ID(),
								imp.getPostal(), "", imp.getCity(),
								imp.getAddress1(), imp.getAddress2()))
							bpl = bpls[i];
					}
				}
				if (bpl == null)
				{
					//	New Location
					MLocation loc = new MLocation (getCtx (), 0, get_TrxName());
					loc.setAddress1 (imp.getAddress1 ());
					loc.setAddress2 (imp.getAddress2 ());
					loc.setCity (imp.getCity ());
					loc.setPostal (imp.getPostal ());
					if (imp.getC_Region_ID () != 0)
						loc.setC_Region_ID (imp.getC_Region_ID ());
					loc.setC_Country_ID (imp.getC_Country_ID ());
					if (!loc.save ())
						continue;
					//
					bpl = new MBPartnerLocation (bp);
					bpl.setC_Location_ID (loc.getC_Location_ID ());
					if (!bpl.save ())
						continue;
				}
				imp.setC_Location_ID (bpl.getC_Location_ID ());
				imp.setBillTo_ID (bpl.getC_BPartner_Location_ID ());
				imp.setC_BPartner_Location_ID (bpl.getC_BPartner_Location_ID ());

				//	User/Contact
				if (imp.getContactName () != null
					|| imp.getEMail () != null
					|| imp.getPhone () != null)
				{
					MUser[] users = bp.getContacts(true);
					MUser user = null;
					for (int i = 0; user == null && i < users.length;  i++)
					{
						String name = users[i].getName();
						if (name.equals(imp.getContactName())
							|| name.equals(imp.getName()))
						{
							user = users[i];
							imp.setAD_User_ID (user.getAD_User_ID ());
						}
					}
					if (user == null)
					{
						user = new MUser (bp);
						if (imp.getContactName () == null)
							user.setName (imp.getName ());
						else
							user.setName (imp.getContactName ());
						user.setEMail (imp.getEMail ());
						user.setPhone (imp.getPhone ());
						if (user.save ())
							imp.setAD_User_ID (user.getAD_User_ID ());
					}
				}
				imp.save ();
			}	//	for all new BPartners
			//
		}
		catch (SQLException e)
		{
			log.log(Level.SEVERE, "BP - " + sql.toString(), e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}
		sql = new StringBuilder ("UPDATE I_OrderJP ")
			  .append("SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=No BPartner, ' ")
			  .append("WHERE C_BPartner_ID IS NULL")
			  .append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (no != 0)
			log.warning ("No BPartner=" + no);

		commitEx();

		/**
		 * 新規取引先の登録処理終了//TODO
		 */
		long endCreateBP = System.currentTimeMillis();
		addLog("新規取引先の登録時間: " + (endCreateBP - startCreateBP) + " ミリ秒");


		//	-- New Orders -----------------------------------------------------

		long startImport = System.currentTimeMillis();
		if (processMonitor != null)	processMonitor.statusUpdate("受注伝票の登録");

		int noInsert = 0;
		int noInsertLine = 0;

		//	Go through Order Records w/o
		sql = new StringBuilder ("SELECT * FROM I_OrderJP ")
			  .append("WHERE I_IsImported='N'").append (clientCheck)
			.append(" ORDER BY DateOrdered, C_BPartner_ID, BillTo_ID, C_BPartner_Location_ID, I_OrderJP_ID");
		try
		{
			pstmt = DB.prepareStatement (sql.toString(), get_TrxName());
			rs = pstmt.executeQuery ();
			//
			int oldC_BPartner_ID = 0;
			int oldBillTo_ID = 0;
			int oldC_BPartner_Location_ID = 0;
			String oldDocumentNo = "";
			//
			MOrder order = null;
			int lineNo = 0;
			while (rs.next ())
			{
				X_I_OrderJP imp = new X_I_OrderJP (getCtx (), rs, get_TrxName());
				String cmpDocumentNo = imp.getDocumentNo();
				if (cmpDocumentNo == null)
					cmpDocumentNo = "";
				//	New Order
				if (oldC_BPartner_ID != imp.getC_BPartner_ID()
					|| oldC_BPartner_Location_ID != imp.getC_BPartner_Location_ID()
					|| oldBillTo_ID != imp.getBillTo_ID()
					|| !oldDocumentNo.equals(cmpDocumentNo))
				{
					if (order != null)
					{
						if (m_docAction != null && m_docAction.length() > 0)
						{
//							order.setDocAction(m_docAction);
							if(!order.processIt (m_docAction)) {
								log.warning("Order Process Failed: " + order + " - " + order.getProcessMsg());
								throw new IllegalStateException("Order Process Failed: " + order + " - " + order.getProcessMsg());

							}
						}
						order.saveEx();

						if(isRecordCommitJP)
							commitEx();

						if (processMonitor != null)	processMonitor.statusUpdate(order.getDocumentNo()+"の処理が完了しました。");
					}
					oldC_BPartner_ID = imp.getC_BPartner_ID();
					oldC_BPartner_Location_ID = imp.getC_BPartner_Location_ID();
					oldBillTo_ID = imp.getBillTo_ID();
					oldDocumentNo = imp.getDocumentNo();
					if (oldDocumentNo == null)
						oldDocumentNo = "";
					//
					order = new MOrder (getCtx(), 0, get_TrxName());
					order.setClientOrg (imp.getAD_Client_ID(), imp.getAD_Org_ID());
					order.setC_DocTypeTarget_ID(imp.getC_DocType_ID());
					order.setIsSOTrx(imp.isSOTrx());
					order.setDocAction(m_docAction);
					if (imp.getDeliveryRule() != null ) {
						order.setDeliveryRule(imp.getDeliveryRule());
					}
					if (imp.getDocumentNo() != null)
						order.setDocumentNo(imp.getDocumentNo());
					//	Ship Partner
					order.setC_BPartner_ID(imp.getC_BPartner_ID());
					order.setC_BPartner_Location_ID(imp.getC_BPartner_Location_ID());
					if (imp.getAD_User_ID() != 0)
						order.setAD_User_ID(imp.getAD_User_ID());
					//	Bill Partner
					order.setBill_BPartner_ID(imp.getC_BPartner_ID());
					order.setBill_Location_ID(imp.getBillTo_ID());
					//
					if (imp.getDescription() != null)
						order.setDescription(imp.getDescription());
					order.setC_PaymentTerm_ID(imp.getC_PaymentTerm_ID());
					order.setM_PriceList_ID(imp.getM_PriceList_ID());
					order.setM_Warehouse_ID(imp.getM_Warehouse_ID());
					if (imp.getM_Shipper_ID() != 0)
						order.setM_Shipper_ID(imp.getM_Shipper_ID());
					//	SalesRep from Import or the person running the import
					if (imp.getSalesRep_ID() != 0)
						order.setSalesRep_ID(imp.getSalesRep_ID());
					if (order.getSalesRep_ID() == 0)
						order.setSalesRep_ID(getAD_User_ID());
					//
					if (imp.getAD_OrgTrx_ID() != 0)
						order.setAD_OrgTrx_ID(imp.getAD_OrgTrx_ID());
					if (imp.getC_Activity_ID() != 0)
						order.setC_Activity_ID(imp.getC_Activity_ID());
					if (imp.getC_Campaign_ID() != 0)
						order.setC_Campaign_ID(imp.getC_Campaign_ID());
					if (imp.getC_Project_ID() != 0)
						order.setC_Project_ID(imp.getC_Project_ID());
					//
					if (imp.getDateOrdered() != null)
						order.setDateOrdered(imp.getDateOrdered());
					if (imp.getDateAcct() != null)
						order.setDateAcct(imp.getDateAcct());

					// Set Order Source
					if (imp.getC_OrderSource() != null)
						order.setC_OrderSource_ID(imp.getC_OrderSource_ID());
					//
					order.saveEx();
					noInsert++;
					lineNo = 10;
				}//if
				imp.setC_Order_ID(order.getC_Order_ID());
				//	New OrderLine
				MOrderLine line = new MOrderLine (order);
				line.setLine(lineNo);
				lineNo += 10;
				if (imp.getM_Product_ID() != 0)
					line.setM_Product_ID(imp.getM_Product_ID(), true);
				if (imp.getC_Charge_ID() != 0)
					line.setC_Charge_ID(imp.getC_Charge_ID());
				line.setQty(imp.getQtyOrdered());
				line.setPrice();
				if (imp.getPriceActual().compareTo(Env.ZERO) != 0)
					line.setPrice(imp.getPriceActual());
				if (imp.getC_Tax_ID() != 0)
					line.setC_Tax_ID(imp.getC_Tax_ID());
				else
				{
					line.setTax();
					imp.setC_Tax_ID(line.getC_Tax_ID());
				}
				if (imp.getFreightAmt() != null)
					line.setFreightAmt(imp.getFreightAmt());
				if (imp.getLineDescription() != null)
					line.setDescription(imp.getLineDescription());
				line.saveEx();
				imp.setC_OrderLine_ID(line.getC_OrderLine_ID());
				imp.setI_IsImported(true);
				imp.setProcessed(true);
				//
				if (imp.save())
					noInsertLine++;
			}//While
			if (order != null)
			{
				if (m_docAction != null && m_docAction.length() > 0)
				{
					order.setDocAction(m_docAction);
					if(!order.processIt (m_docAction)) {
						log.warning("Order Process Failed: " + order + " - " + order.getProcessMsg());
						throw new IllegalStateException("Order Process Failed: " + order + " - " + order.getProcessMsg());

					}
				}
				order.saveEx();

				if(isRecordCommitJP)
					commitEx();

				if (processMonitor != null)	processMonitor.statusUpdate(order.getDocumentNo()+"の処理が完了しました。");
			}
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, "Order - " + sql.toString(), e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

		//	Set Error to indicator to not imported
		sql = new StringBuilder ("UPDATE I_OrderJP ")
			.append("SET I_IsImported='N', Updated=SysDate ")
			.append("WHERE I_IsImported<>'Y'").append(clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		addLog (0, null, new BigDecimal (no), "@Errors@");

//		commitEx();
		long endImport = System.currentTimeMillis();
		addLog("インポート処理時間: " + (endImport - startImport) + " ミリ秒");


		/**
		 * indexの作成と制約の有効化の処理//TODO
		 */

		//indexの作成
		if(isDeleteIndexJP)
		{
			if (processMonitor != null)	processMonitor.statusUpdate("Indexの再作成");
			long startCreateIndex = System.currentTimeMillis();

			createIndex();
			commitEx();

			long endCreateIndex = System.currentTimeMillis();
			addLog("Indexの再作成時間: " + (endCreateIndex - startCreateIndex ) + " ミリ秒");
		}

		//制約の有効化
		if(isInvalidConstraintJP)
		{
			if (processMonitor != null)	processMonitor.statusUpdate("制約の有効化");
			long startValidConstraint = System.currentTimeMillis();

			validConstraint();
			commitEx();

			long endValidConstraint = System.currentTimeMillis();
			addLog("制約の有効化の処理時間: " + (endValidConstraint - startValidConstraint ) + " ミリ秒");
		}

		long finish = System.currentTimeMillis();

		addLog("*****処理が無事終了しました*****");
		addLog("合計時間処理時間: " + (finish - start) + " ミリ秒");

		//
		addLog (0, null, new BigDecimal (noInsert), "@C_Order_ID@: @Inserted@");
		addLog (0, null, new BigDecimal (noInsertLine), "@C_OrderLine_ID@: @Inserted@");
		StringBuilder msgreturn = new StringBuilder("#").append(noInsert).append("/").append(noInsertLine);
		return msgreturn.toString();
	}	//	doIt


	private boolean createIndex()
	{
		for(int i = 0; i < allDocumentTables.length; i++)
		{
			MTable mTable = MTable.get(getCtx(), allDocumentTables[i]);
			MTableIndex[] indexes = MTableIndex.get(mTable);
			for(int j = 0; j < indexes.length; j++)
			{
				String indexDropSql = indexes[j].getDDL();
				DB.executeUpdateEx(indexDropSql, get_TrxName());
//				addLog(0, null, new BigDecimal(rvalue), indexDropSql.toString());
			}
		}

		return true;
	}

	private boolean validConstraint()
	{
		for(int i = 0; i < allDocumentTables.length; i++)
		{
			StringBuilder invalidConstraint = new StringBuilder (" update pg_trigger set tgenabled = 'O' "
											+ "where oid in (select tr.oid from pg_trigger tr INNER JOIN pg_class cl on (tr.tgrelid = cl.oid) WHERE cl.relname="
											+ "lower('" + allDocumentTables[i] + "') )");

			DB.executeUpdate(invalidConstraint.toString(), get_TrxName());
		}
		return true;
	}

	@Override
	public String getWhereClause() {
		StringBuilder msgreturn = new StringBuilder(" AND AD_Client_ID=").append(m_AD_Client_ID);
		return msgreturn.toString();
	}


	@Override
	public String getImportTableName() {
		return X_I_OrderJP.Table_Name;
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
		no = DB.executeUpdate(sql.toString(), get_TrxName());
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
					.append("SET SalesRep_ID=(SELECT AD_User_ID FROM AD_User p")
					.append(" WHERE i.JP_SalesRep_EMail=p.EMail AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ) ")
					.append(" WHERE i.JP_SalesRep_EMail IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportSalesRepIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_Name)) { //Name

			sql = new StringBuilder ("UPDATE I_OrderJP i ")
					.append("SET SalesRep_ID=(SELECT AD_User_ID FROM AD_User p")
					.append(" WHERE i.JP_SalesRep_Name=p.Name AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ) ")
					.append(" WHERE i.JP_SalesRep_Name IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportSalesRepIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_Value)) { //Value

			sql = new StringBuilder ("UPDATE I_OrderJP i ")
					.append("SET SalesRep_ID=(SELECT AD_User_ID FROM AD_User p")
					.append(" WHERE i.JP_SalesRep_Value=p.Value AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ) ")
					.append(" WHERE i.JP_SalesRep_Value IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportSalesRepIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueEMail)) { //Value + E-Mail

			sql = new StringBuilder ("UPDATE I_OrderJP i ")
					.append("SET SalesRep_ID=(SELECT AD_User_ID FROM AD_User p")
					.append(" WHERE i.JP_SalesRep_Value=p.Value AND i.JP_SalesRep_EMail=p.EMail AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ) ")
					.append(" WHERE i.JP_SalesRep_Value IS NOT NULL AND i.JP_SalesRep_EMail IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportSalesRepIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueName)) { //Value + Name

			sql = new StringBuilder ("UPDATE I_OrderJP i ")
					.append("SET SalesRep_ID=(SELECT AD_User_ID FROM AD_User p")
					.append(" WHERE i.JP_SalesRep_Value=p.Value AND i.JP_SalesRep_Name=p.Name AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ) ")
					.append(" WHERE i.JP_SalesRep_Value IS NOT NULL AND i.JP_SalesRep_Name IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportSalesRepIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueNameEmail)) { //Value + Name + EMail

			sql = new StringBuilder ("UPDATE I_OrderJP i ")
					.append("SET SalesRep_ID=(SELECT AD_User_ID FROM AD_User p")
					.append(" WHERE i.JP_SalesRep_Value=p.Value AND i.JP_SalesRep_Name=p.Name  AND i.JP_SalesRep_EMail=p.EMail AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ) ")
					.append(" WHERE i.JP_SalesRep_Value IS NOT NULL AND i.JP_SalesRep_Name IS NOT NULL AND i.JP_SalesRep_EMail IS NOT NULL")
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
				.append(" WHERE i.JP_Bill_BP_Location_Name=p.Name AND i.C_BPartner_ID=p.C_BPartner_ID) ")
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
					.append("SET Bill_User_ID=(SELECT AD_User_ID FROM AD_User p")
					.append(" WHERE i.JP_Bill_User_EMail=p.EMail AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.Bill_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_Bill_User_EMail IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportInvoiceUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_Name)) { //Name

			sql = new StringBuilder ("UPDATE I_OrderJP i ")
					.append("SET Bill_User_ID=(SELECT AD_User_ID FROM AD_User p")
					.append(" WHERE i.JP_Bill_User_Name=p.Name AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.Bill_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_Bill_User_Name IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportInvoiceUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_Value)) { //Value

			sql = new StringBuilder ("UPDATE I_OrderJP i ")
					.append("SET Bill_User_ID=(SELECT AD_User_ID FROM AD_User p")
					.append(" WHERE i.JP_Bill_User_Value=p.Value AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.Bill_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_Bill_User_Value IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportInvoiceUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueEMail)) { //Value + E-Mail

			sql = new StringBuilder ("UPDATE I_OrderJP i ")
					.append("SET Bill_User_ID=(SELECT AD_User_ID FROM AD_User p")
					.append(" WHERE i.JP_Bill_User_Value=p.Value AND i.JP_Bill_User_EMail=p.EMail AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.Bill_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_Bill_User_Value IS NOT NULL AND i.JP_Bill_User_EMail IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportInvoiceUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueName)) { //Value + Name

			sql = new StringBuilder ("UPDATE I_OrderJP i ")
					.append("SET Bill_User_ID=(SELECT AD_User_ID FROM AD_User p")
					.append(" WHERE i.JP_Bill_User_Value=p.Value AND i.JP_Bill_User_Name=p.Name AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.Bill_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_Bill_User_Value IS NOT NULL AND i.JP_Bill_User_Name IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportInvoiceUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueNameEmail)) { //Value + Name + EMail

			sql = new StringBuilder ("UPDATE I_OrderJP i ")
					.append("SET Bill_User_ID=(SELECT AD_User_ID FROM AD_User p")
					.append(" WHERE i.JP_Bill_User_Value=p.Value AND i.JP_Bill_User_Name=p.Name  AND i.JP_Bill_User_EMail=p.EMail AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.Bill_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_Bill_User_Value IS NOT NULL AND i.JP_Bill_User_Name IS NOT NULL AND i.JP_Bill_User_EMail IS NOT NULL")
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
				.append("SET BillTo_ID=(SELECT C_BPartner_Location_ID FROM C_BPartner_Location p")
				.append(" WHERE i.JP_DropShip_BP_Location_Name=p.Name AND i.C_BPartner_ID=p.C_BPartner_ID) ")
				.append("WHERE i.DropShip_Location_ID IS NULL AND i.JP_DropShip_BP_Location_Name IS NOT NULL ")
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
			.append("WHERE DropShip_Location_ID IS NULL AND JP_DropShip_BP_Location_Name IS NOT NULL")
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
					.append("SET DropShip_User_ID=(SELECT AD_User_ID FROM AD_User p")
					.append(" WHERE i.JP_DropShip_User_EMail=p.EMail AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.DropShip_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_DropShip_User_EMail IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportDropShipUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_Name)) { //Name

			sql = new StringBuilder ("UPDATE I_OrderJP i ")
					.append("SET DropShip_User_ID=(SELECT AD_User_ID FROM AD_User p")
					.append(" WHERE i.JP_DropShip_User_Name=p.Name AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.DropShip_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_DropShip_User_Name IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportDropShipUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_Value)) { //Value

			sql = new StringBuilder ("UPDATE I_OrderJP i ")
					.append("SET DropShip_User_ID=(SELECT AD_User_ID FROM AD_User p")
					.append(" WHERE i.JP_DropShip_User_Value=p.Value AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.DropShip_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_DropShip_User_Value IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportDropShipUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueEMail)) { //Value + E-Mail

			sql = new StringBuilder ("UPDATE I_OrderJP i ")
					.append("SET DropShip_User_ID=(SELECT AD_User_ID FROM AD_User p")
					.append(" WHERE i.JP_DropShip_User_Value=p.Value AND i.JP_DropShip_User_EMail=p.EMail AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.DropShip_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_DropShip_User_Value IS NOT NULL AND i.JP_DropShip_User_EMail IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportDropShipUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueName)) { //Value + Name

			sql = new StringBuilder ("UPDATE I_OrderJP i ")
					.append("SET DropShip_User_ID=(SELECT AD_User_ID FROM AD_User p")
					.append(" WHERE i.JP_DropShip_User_Value=p.Value AND i.JP_Bill_User_Name=p.Name AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.DropShip_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_DropShip_User_Value IS NOT NULL AND i.JP_DropShip_User_Name IS NOT NULL")
					.append(" AND i.I_IsImported='N'").append(getWhereClause());

		}else if(p_JP_ImportDropShipUserIdentifier.equals(JPiereImportUser.JP_ImportUserIdentifier_ValueNameEmail)) { //Value + Name + EMail

			sql = new StringBuilder ("UPDATE I_OrderJP i ")
					.append("SET DropShip_User_ID=(SELECT AD_User_ID FROM AD_User p")
					.append(" WHERE i.JP_DropShip_User_Value=p.Value AND i.JP_Bill_User_Name=p.Name  AND i.JP_Bill_User_EMail=p.EMail AND ( p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0 ) ")
					.append(" AND i.DropShip_BPartner_ID = p.C_BPartner_ID )")
					.append(" WHERE i.JP_DropShip_User_Value IS NOT NULL AND i.JP_DropShip_User_Name IS NOT NULL AND i.JP_DropShip_User_EMail IS NOT NULL")
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

	private boolean reverseLookupC_BPartner_ID()throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_OrderJP o ")
			  .append("SET (C_BPartner_ID,AD_User_ID)=(SELECT C_BPartner_ID,AD_User_ID FROM AD_User u")
			  .append(" WHERE o.EMail=u.EMail AND o.AD_Client_ID=u.AD_Client_ID AND u.C_BPartner_ID IS NOT NULL) ")
			  .append("WHERE C_BPartner_ID IS NULL AND EMail IS NOT NULL")
			  .append(" AND I_IsImported<>'Y'").append (getWhereClause());
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set BP from EMail=" + no);

		//	BP from ContactName
		sql = new StringBuilder ("UPDATE I_OrderJP o ")
			  .append("SET (C_BPartner_ID,AD_User_ID)=(SELECT C_BPartner_ID,AD_User_ID FROM AD_User u")
			  .append(" WHERE o.ContactName=u.Name AND o.AD_Client_ID=u.AD_Client_ID AND u.C_BPartner_ID IS NOT NULL) ")
			  .append("WHERE C_BPartner_ID IS NULL AND ContactName IS NOT NULL")
			  .append(" AND EXISTS (SELECT Name FROM AD_User u WHERE o.ContactName=u.Name AND o.AD_Client_ID=u.AD_Client_ID AND u.C_BPartner_ID IS NOT NULL GROUP BY Name HAVING COUNT(*)=1)")
			  .append(" AND I_IsImported<>'Y'").append (getWhereClause());
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set BP from ContactName=" + no);

		//	BP from Value
		sql = new StringBuilder ("UPDATE I_OrderJP o ")
			  .append("SET C_BPartner_ID=(SELECT MAX(C_BPartner_ID) FROM C_BPartner bp")
			  .append(" WHERE o.BPartnerValue=bp.Value AND o.AD_Client_ID=bp.AD_Client_ID) ")
			  .append("WHERE C_BPartner_ID IS NULL AND BPartnerValue IS NOT NULL")
			  .append(" AND I_IsImported<>'Y'").append (getWhereClause());
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set BP from Value=" + no);

		//	Default BP
		sql = new StringBuilder ("UPDATE I_OrderJP o ")
			  .append("SET C_BPartner_ID=(SELECT C_BPartnerCashTrx_ID FROM AD_ClientInfo c")
			  .append(" WHERE o.AD_Client_ID=c.AD_Client_ID) ")
			  .append("WHERE C_BPartner_ID IS NULL AND BPartnerValue IS NULL AND Name IS NULL")
			  .append(" AND I_IsImported<>'Y'").append (getWhereClause());
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set Default BP=" + no);

		//	Existing Location ? Exact Match
		sql = new StringBuilder ("UPDATE I_OrderJP o ")
			  .append("SET (BillTo_ID,C_BPartner_Location_ID)=(SELECT C_BPartner_Location_ID,C_BPartner_Location_ID")
			  .append(" FROM C_BPartner_Location bpl INNER JOIN C_Location l ON (bpl.C_Location_ID=l.C_Location_ID)")
			  .append(" WHERE o.C_BPartner_ID=bpl.C_BPartner_ID AND bpl.AD_Client_ID=o.AD_Client_ID")
			  .append(" AND DUMP(o.Address1)=DUMP(l.Address1) AND DUMP(o.Address2)=DUMP(l.Address2)")
			  .append(" AND DUMP(o.City)=DUMP(l.City) AND DUMP(o.Postal)=DUMP(l.Postal)")
			  .append(" AND o.C_Region_ID=l.C_Region_ID AND o.C_Country_ID=l.C_Country_ID) ")
			  .append("WHERE C_BPartner_ID IS NOT NULL AND C_BPartner_Location_ID IS NULL")
			  .append(" AND I_IsImported='N'").append (getWhereClause());
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Found Location=" + no);
		//	Set Bill Location from BPartner
		sql = new StringBuilder ("UPDATE I_OrderJP o ")
			  .append("SET BillTo_ID=(SELECT MAX(C_BPartner_Location_ID) FROM C_BPartner_Location l")
			  .append(" WHERE l.C_BPartner_ID=o.C_BPartner_ID AND o.AD_Client_ID=l.AD_Client_ID")
			  .append(" AND ((l.IsBillTo='Y' AND o.IsSOTrx='Y') OR (l.IsPayFrom='Y' AND o.IsSOTrx='N'))")
			  .append(") ")
			  .append("WHERE C_BPartner_ID IS NOT NULL AND BillTo_ID IS NULL")
			  .append(" AND I_IsImported<>'Y'").append (getWhereClause());
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set BP BillTo from BP=" + no);
		//	Set Location from BPartner
		sql = new StringBuilder ("UPDATE I_OrderJP o ")
			  .append("SET C_BPartner_Location_ID=(SELECT MAX(C_BPartner_Location_ID) FROM C_BPartner_Location l")
			  .append(" WHERE l.C_BPartner_ID=o.C_BPartner_ID AND o.AD_Client_ID=l.AD_Client_ID")
			  .append(" AND ((l.IsShipTo='Y' AND o.IsSOTrx='Y') OR o.IsSOTrx='N')")
			  .append(") ")
			  .append("WHERE C_BPartner_ID IS NOT NULL AND C_BPartner_Location_ID IS NULL")
			  .append(" AND I_IsImported<>'Y'").append (getWhereClause());
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set BP Location from BP=" + no);
		//
		sql = new StringBuilder ("UPDATE I_OrderJP ")
			  .append("SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=No BP Location, ' ")
			  .append("WHERE C_BPartner_ID IS NOT NULL AND (BillTo_ID IS NULL OR C_BPartner_Location_ID IS NULL)")
			  .append(" AND I_IsImported<>'Y'").append (getWhereClause());
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (no != 0)
			log.warning ("No BP Location=" + no);

		//	Set Country
		/**
		sql = new StringBuffer ("UPDATE I_OrderJP o "
			  + "SET CountryCode=(SELECT MAX(CountryCode) FROM C_Country c WHERE c.IsDefault='Y'"
			  + " AND c.AD_Client_ID IN (0, o.AD_Client_ID)) "
			  + "WHERE C_BPartner_ID IS NULL AND CountryCode IS NULL AND C_Country_ID IS NULL"
			  + " AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		log.fine("Set Country Default=" + no);
		**/
		sql = new StringBuilder ("UPDATE I_OrderJP o ")
			  .append("SET C_Country_ID=(SELECT C_Country_ID FROM C_Country c")
			  .append(" WHERE o.CountryCode=c.CountryCode AND c.AD_Client_ID IN (0, o.AD_Client_ID)) ")
			  .append("WHERE C_BPartner_ID IS NULL AND C_Country_ID IS NULL AND CountryCode IS NOT NULL")
			  .append(" AND I_IsImported<>'Y'").append (getWhereClause());
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set Country=" + no);
		//
		sql = new StringBuilder ("UPDATE I_OrderJP ")
			  .append("SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Invalid Country, ' ")
			  .append("WHERE C_BPartner_ID IS NULL AND C_Country_ID IS NULL")
			  .append(" AND I_IsImported<>'Y'").append (getWhereClause());
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (no != 0)
			log.warning ("Invalid Country=" + no);

		//	Set Region
		sql = new StringBuilder ("UPDATE I_OrderJP o ")
			  .append("Set RegionName=(SELECT MAX(Name) FROM C_Region r")
			  .append(" WHERE r.IsDefault='Y' AND r.C_Country_ID=o.C_Country_ID")
			  .append(" AND r.AD_Client_ID IN (0, o.AD_Client_ID)) ")
			  .append("WHERE C_BPartner_ID IS NULL AND C_Region_ID IS NULL AND RegionName IS NULL")
			  .append(" AND I_IsImported<>'Y'").append (getWhereClause());
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set Region Default=" + no);
		//
		sql = new StringBuilder ("UPDATE I_OrderJP o ")
			  .append("Set C_Region_ID=(SELECT C_Region_ID FROM C_Region r")
			  .append(" WHERE r.Name=o.RegionName AND r.C_Country_ID=o.C_Country_ID")
			  .append(" AND r.AD_Client_ID IN (0, o.AD_Client_ID)) ")
			  .append("WHERE C_BPartner_ID IS NULL AND C_Region_ID IS NULL AND RegionName IS NOT NULL")
			  .append(" AND I_IsImported<>'Y'").append (getWhereClause());
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set Region=" + no);
		//
		sql = new StringBuilder ("UPDATE I_OrderJP o ")
			  .append("SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Invalid Region, ' ")
			  .append("WHERE C_BPartner_ID IS NULL AND C_Region_ID IS NULL ")
			  .append(" AND EXISTS (SELECT * FROM C_Country c")
			  .append(" WHERE c.C_Country_ID=o.C_Country_ID AND c.HasRegion='Y')")
			  .append(" AND I_IsImported<>'Y'").append (getWhereClause());
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (no != 0)
			log.warning ("Invalid Region=" + no);


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

}	//	ImportOrder
