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

import org.adempiere.util.IProcessUI;
import org.compiere.model.MBPartner;
import org.compiere.model.MBPartnerLocation;
import org.compiere.model.MLocation;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MTable;
import org.compiere.model.MTableIndex;
import org.compiere.model.MUser;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;

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
public class JPiereImportOrder extends SvrProcess
{
	/**	Client to be imported to		*/
	private int				m_AD_Client_ID = 0;
	/**	Organization to be imported to		*/
	private int				m_AD_Org_ID = 0;
	/**	Delete old Imported				*/
	private boolean			m_deleteOldImported = false;
	/**	Document Action					*/
	private String			m_docAction = MOrder.DOCACTION_Prepare;


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

		//	****	Prepare	****

		if (processMonitor != null)	processMonitor.statusUpdate("インポート済みのデータの削除");
		if (m_deleteOldImported)
		{
			sql = new StringBuilder ("DELETE I_OrderJP ")
				  .append("WHERE I_IsImported='Y'").append (clientCheck);
			no = DB.executeUpdate(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("Delete Old Impored =" + no);
		}

		if (processMonitor != null)	processMonitor.statusUpdate("組織、更新情報の設定");
		sql = new StringBuilder ("UPDATE I_OrderJP ")
			  .append("SET AD_Client_ID = COALESCE (AD_Client_ID,").append (m_AD_Client_ID).append ("),")
			  .append(" AD_Org_ID = COALESCE (AD_Org_ID,").append (m_AD_Org_ID).append ("),")
			  .append(" IsActive = COALESCE (IsActive, 'Y'),")
			  .append(" Created = COALESCE (Created, SysDate),")
			  .append(" CreatedBy = COALESCE (CreatedBy, 0),")
			  .append(" Updated = COALESCE (Updated, SysDate),")
			  .append(" UpdatedBy = COALESCE (UpdatedBy, 0),")
			  .append(" I_ErrorMsg = ' ',")
			  .append(" I_IsImported = 'N' ")
			  .append("WHERE I_IsImported<>'Y' OR I_IsImported IS NULL");
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.INFO)) log.info ("Reset=" + no);

		if (processMonitor != null)	processMonitor.statusUpdate("組織マスタの妥当性チェック");
		sql = new StringBuilder ("UPDATE I_OrderJP o ")
			.append("SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Invalid Org, '")
			.append("WHERE (AD_Org_ID IS NULL OR AD_Org_ID=0")
			.append(" OR EXISTS (SELECT * FROM AD_Org oo WHERE o.AD_Org_ID=oo.AD_Org_ID AND (oo.IsSummary='Y' OR oo.IsActive='N')))")
			.append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (no != 0)
			log.warning ("Invalid Org=" + no);

		//	Document Type - PO - SO
		if (processMonitor != null)	processMonitor.statusUpdate("伝票タイプを伝票タイプの名称より逆引き設定");
		sql = new StringBuilder ("UPDATE I_OrderJP o ")	//	PO Document Type Name
			  .append("SET C_DocType_ID=(SELECT C_DocType_ID FROM C_DocType d WHERE d.Name=o.DocTypeName")
			  .append(" AND d.DocBaseType='POO' AND o.AD_Client_ID=d.AD_Client_ID) ")
			  .append("WHERE C_DocType_ID IS NULL AND IsSOTrx='N' AND DocTypeName IS NOT NULL AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set PO DocType=" + no);
		sql = new StringBuilder ("UPDATE I_OrderJP o ")	//	SO Document Type Name
			  .append("SET C_DocType_ID=(SELECT C_DocType_ID FROM C_DocType d WHERE d.Name=o.DocTypeName")
			  .append(" AND d.DocBaseType='SOO' AND o.AD_Client_ID=d.AD_Client_ID) ")
			  .append("WHERE C_DocType_ID IS NULL AND IsSOTrx='Y' AND DocTypeName IS NOT NULL AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set SO DocType=" + no);
		sql = new StringBuilder ("UPDATE I_OrderJP o ")
			  .append("SET C_DocType_ID=(SELECT C_DocType_ID FROM C_DocType d WHERE d.Name=o.DocTypeName")
			  .append(" AND d.DocBaseType IN ('SOO','POO') AND o.AD_Client_ID=d.AD_Client_ID) ")
			//+ "WHERE C_DocType_ID IS NULL AND IsSOTrx IS NULL AND DocTypeName IS NOT NULL AND I_IsImported<>'Y'").append (clientCheck);
			  .append("WHERE C_DocType_ID IS NULL AND DocTypeName IS NOT NULL AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set DocType=" + no);
		sql = new StringBuilder ("UPDATE I_OrderJP ")	//	Error Invalid Doc Type Name
			  .append("SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Invalid DocTypeName, ' ")
			  .append("WHERE C_DocType_ID IS NULL AND DocTypeName IS NOT NULL")
			  .append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (no != 0)
			log.warning ("Invalid DocTypeName=" + no);
		//	DocType Default
		if (processMonitor != null)	processMonitor.statusUpdate("伝票タイプが設定されていないデータに、伝票タイプを設定する");
		sql = new StringBuilder ("UPDATE I_OrderJP o ")	//	Default PO
			  .append("SET C_DocType_ID=(SELECT MAX(C_DocType_ID) FROM C_DocType d WHERE d.IsDefault='Y'")
			  .append(" AND d.DocBaseType='POO' AND o.AD_Client_ID=d.AD_Client_ID) ")
			  .append("WHERE C_DocType_ID IS NULL AND IsSOTrx='N' AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set PO Default DocType=" + no);
		sql = new StringBuilder ("UPDATE I_OrderJP o ")	//	Default SO
			  .append("SET C_DocType_ID=(SELECT MAX(C_DocType_ID) FROM C_DocType d WHERE d.IsDefault='Y'")
			  .append(" AND d.DocBaseType='SOO' AND o.AD_Client_ID=d.AD_Client_ID) ")
			  .append("WHERE C_DocType_ID IS NULL AND IsSOTrx='Y' AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set SO Default DocType=" + no);
		sql = new StringBuilder ("UPDATE I_OrderJP o ")
			  .append("SET C_DocType_ID=(SELECT MAX(C_DocType_ID) FROM C_DocType d WHERE d.IsDefault='Y'")
			  .append(" AND d.DocBaseType IN('SOO','POO') AND o.AD_Client_ID=d.AD_Client_ID) ")
			  .append("WHERE C_DocType_ID IS NULL AND IsSOTrx IS NULL AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set Default DocType=" + no);
		sql = new StringBuilder ("UPDATE I_OrderJP ")	// No DocType
			  .append("SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=No DocType, ' ")
			  .append("WHERE C_DocType_ID IS NULL")
			  .append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (no != 0)
			log.warning ("No DocType=" + no);

		//	Set IsSOTrx
		if (processMonitor != null)	processMonitor.statusUpdate("伝票タイプからIsSOTrxを設定する");
		sql = new StringBuilder ("UPDATE I_OrderJP o SET IsSOTrx='Y' ")
			  .append("WHERE EXISTS (SELECT * FROM C_DocType d WHERE o.C_DocType_ID=d.C_DocType_ID AND d.DocBaseType='SOO' AND o.AD_Client_ID=d.AD_Client_ID)")
			  .append(" AND C_DocType_ID IS NOT NULL")
			  .append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set IsSOTrx=Y=" + no);
		sql = new StringBuilder ("UPDATE I_OrderJP o SET IsSOTrx='N' ")
			  .append("WHERE EXISTS (SELECT * FROM C_DocType d WHERE o.C_DocType_ID=d.C_DocType_ID AND d.DocBaseType='POO' AND o.AD_Client_ID=d.AD_Client_ID)")
			  .append(" AND C_DocType_ID IS NOT NULL")
			  .append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set IsSOTrx=N=" + no);

		//	Price List
		if (processMonitor != null)	processMonitor.statusUpdate("プライスリストを設定する");
		sql = new StringBuilder ("UPDATE I_OrderJP o ")
			  .append("SET M_PriceList_ID=(SELECT MAX(M_PriceList_ID) FROM M_PriceList p WHERE p.IsDefault='Y'")
			  .append(" AND p.C_Currency_ID=o.C_Currency_ID AND p.IsSOPriceList=o.IsSOTrx AND o.AD_Client_ID=p.AD_Client_ID) ")
			  .append("WHERE M_PriceList_ID IS NULL AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set Default Currency PriceList=" + no);
		sql = new StringBuilder ("UPDATE I_OrderJP o ")
			  .append("SET M_PriceList_ID=(SELECT MAX(M_PriceList_ID) FROM M_PriceList p WHERE p.IsDefault='Y'")
			  .append(" AND p.IsSOPriceList=o.IsSOTrx AND o.AD_Client_ID=p.AD_Client_ID) ")
			  .append("WHERE M_PriceList_ID IS NULL AND C_Currency_ID IS NULL AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set Default PriceList=" + no);
		sql = new StringBuilder ("UPDATE I_OrderJP o ")
			  .append("SET M_PriceList_ID=(SELECT MAX(M_PriceList_ID) FROM M_PriceList p ")
			  .append(" WHERE p.C_Currency_ID=o.C_Currency_ID AND p.IsSOPriceList=o.IsSOTrx AND o.AD_Client_ID=p.AD_Client_ID) ")
			  .append("WHERE M_PriceList_ID IS NULL AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set Currency PriceList=" + no);
		sql = new StringBuilder ("UPDATE I_OrderJP o ")
			  .append("SET M_PriceList_ID=(SELECT MAX(M_PriceList_ID) FROM M_PriceList p ")
			  .append(" WHERE p.IsSOPriceList=o.IsSOTrx AND o.AD_Client_ID=p.AD_Client_ID) ")
			  .append("WHERE M_PriceList_ID IS NULL AND C_Currency_ID IS NULL AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set PriceList=" + no);
		//
		sql = new StringBuilder ("UPDATE I_OrderJP ")
			  .append("SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=No PriceList, ' ")
			  .append("WHERE M_PriceList_ID IS NULL")
			  .append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (no != 0)
			log.warning("No PriceList=" + no);

		// @Trifon - Import Order Source
		if (processMonitor != null)	processMonitor.statusUpdate("Order Sourceを設定する");
		sql = new StringBuilder ("UPDATE I_OrderJP o ")
			  .append("SET C_OrderSource_ID=(SELECT C_OrderSource_ID FROM C_OrderSource p")
			  .append(" WHERE o.C_OrderSourceValue=p.Value AND o.AD_Client_ID=p.AD_Client_ID) ")
			  .append("WHERE C_OrderSource_ID IS NULL AND C_OrderSourceValue IS NOT NULL AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set Order Source=" + no);
		// Set proper error message
		sql = new StringBuilder ("UPDATE I_OrderJP ")
			  .append("SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Not Found Order Source, ' ")
			  .append("WHERE C_OrderSource_ID IS NULL AND C_OrderSourceValue IS NOT NULL AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (no != 0)
			log.warning("No OrderSource=" + no);

		//	Payment Term
		if (processMonitor != null)	processMonitor.statusUpdate("支払条件を設定する");
		sql = new StringBuilder ("UPDATE I_OrderJP o ")
			  .append("SET C_PaymentTerm_ID=(SELECT C_PaymentTerm_ID FROM C_PaymentTerm p")
			  .append(" WHERE o.PaymentTermValue=p.Value AND o.AD_Client_ID=p.AD_Client_ID) ")
			  .append("WHERE C_PaymentTerm_ID IS NULL AND PaymentTermValue IS NOT NULL AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set PaymentTerm=" + no);
		sql = new StringBuilder ("UPDATE I_OrderJP o ")
			  .append("SET C_PaymentTerm_ID=(SELECT MAX(C_PaymentTerm_ID) FROM C_PaymentTerm p")
			  .append(" WHERE p.IsDefault='Y' AND o.AD_Client_ID=p.AD_Client_ID) ")
			  .append("WHERE C_PaymentTerm_ID IS NULL AND o.PaymentTermValue IS NULL AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set Default PaymentTerm=" + no);
		//
		sql = new StringBuilder ("UPDATE I_OrderJP ")
			  .append("SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=No PaymentTerm, ' ")
			  .append("WHERE C_PaymentTerm_ID IS NULL")
			  .append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (no != 0)
			log.warning ("No PaymentTerm=" + no);

		//	Warehouse
		if (processMonitor != null)	processMonitor.statusUpdate("倉庫を設定する");
		sql = new StringBuilder ("UPDATE I_OrderJP o ")
			  .append("SET M_Warehouse_ID=(SELECT MAX(M_Warehouse_ID) FROM M_Warehouse w")
			  .append(" WHERE o.AD_Client_ID=w.AD_Client_ID AND o.AD_Org_ID=w.AD_Org_ID) ")
			  .append("WHERE M_Warehouse_ID IS NULL AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());	//	Warehouse for Org
		if (no != 0)
			if (log.isLoggable(Level.FINE)) log.fine("Set Warehouse=" + no);
		sql = new StringBuilder ("UPDATE I_OrderJP o ")
			  .append("SET M_Warehouse_ID=(SELECT M_Warehouse_ID FROM M_Warehouse w")
			  .append(" WHERE o.AD_Client_ID=w.AD_Client_ID) ")
			  .append("WHERE M_Warehouse_ID IS NULL")
			  .append(" AND EXISTS (SELECT AD_Client_ID FROM M_Warehouse w WHERE w.AD_Client_ID=o.AD_Client_ID GROUP BY AD_Client_ID HAVING COUNT(*)=1)")
			  .append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (no != 0)
			if (log.isLoggable(Level.FINE)) log.fine("Set Only Client Warehouse=" + no);
		//
		sql = new StringBuilder ("UPDATE I_OrderJP ")
			  .append("SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=No Warehouse, ' ")
			  .append("WHERE M_Warehouse_ID IS NULL")
			  .append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (no != 0)
			log.warning ("No Warehouse=" + no);

		//	BP from EMail
		if (processMonitor != null)	processMonitor.statusUpdate("取引先をユーザーのEmailより逆引き設定する");
		sql = new StringBuilder ("UPDATE I_OrderJP o ")
			  .append("SET (C_BPartner_ID,AD_User_ID)=(SELECT C_BPartner_ID,AD_User_ID FROM AD_User u")
			  .append(" WHERE o.EMail=u.EMail AND o.AD_Client_ID=u.AD_Client_ID AND u.C_BPartner_ID IS NOT NULL) ")
			  .append("WHERE C_BPartner_ID IS NULL AND EMail IS NOT NULL")
			  .append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set BP from EMail=" + no);
		//	BP from ContactName
		if (processMonitor != null)	processMonitor.statusUpdate("取引先をユーザーのContact Nameより逆引き設定する");
		sql = new StringBuilder ("UPDATE I_OrderJP o ")
			  .append("SET (C_BPartner_ID,AD_User_ID)=(SELECT C_BPartner_ID,AD_User_ID FROM AD_User u")
			  .append(" WHERE o.ContactName=u.Name AND o.AD_Client_ID=u.AD_Client_ID AND u.C_BPartner_ID IS NOT NULL) ")
			  .append("WHERE C_BPartner_ID IS NULL AND ContactName IS NOT NULL")
			  .append(" AND EXISTS (SELECT Name FROM AD_User u WHERE o.ContactName=u.Name AND o.AD_Client_ID=u.AD_Client_ID AND u.C_BPartner_ID IS NOT NULL GROUP BY Name HAVING COUNT(*)=1)")
			  .append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set BP from ContactName=" + no);
		//	BP from Value
		if (processMonitor != null)	processMonitor.statusUpdate("取引先を取引先の検索キーより逆引き設定する");
		sql = new StringBuilder ("UPDATE I_OrderJP o ")
			  .append("SET C_BPartner_ID=(SELECT MAX(C_BPartner_ID) FROM C_BPartner bp")
			  .append(" WHERE o.BPartnerValue=bp.Value AND o.AD_Client_ID=bp.AD_Client_ID) ")
			  .append("WHERE C_BPartner_ID IS NULL AND BPartnerValue IS NOT NULL")
			  .append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set BP from Value=" + no);
		//	Default BP
		if (processMonitor != null)	processMonitor.statusUpdate("取引先が無いデータに対して、デフォルトの取引先を設定する");
		sql = new StringBuilder ("UPDATE I_OrderJP o ")
			  .append("SET C_BPartner_ID=(SELECT C_BPartnerCashTrx_ID FROM AD_ClientInfo c")
			  .append(" WHERE o.AD_Client_ID=c.AD_Client_ID) ")
			  .append("WHERE C_BPartner_ID IS NULL AND BPartnerValue IS NULL AND Name IS NULL")
			  .append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set Default BP=" + no);

		//	Existing Location ? Exact Match
		if (processMonitor != null)	processMonitor.statusUpdate("取引先と請求先の住所を設定する");
		sql = new StringBuilder ("UPDATE I_OrderJP o ")
			  .append("SET (BillTo_ID,C_BPartner_Location_ID)=(SELECT C_BPartner_Location_ID,C_BPartner_Location_ID")
			  .append(" FROM C_BPartner_Location bpl INNER JOIN C_Location l ON (bpl.C_Location_ID=l.C_Location_ID)")
			  .append(" WHERE o.C_BPartner_ID=bpl.C_BPartner_ID AND bpl.AD_Client_ID=o.AD_Client_ID")
			  .append(" AND DUMP(o.Address1)=DUMP(l.Address1) AND DUMP(o.Address2)=DUMP(l.Address2)")
			  .append(" AND DUMP(o.City)=DUMP(l.City) AND DUMP(o.Postal)=DUMP(l.Postal)")
			  .append(" AND o.C_Region_ID=l.C_Region_ID AND o.C_Country_ID=l.C_Country_ID) ")
			  .append("WHERE C_BPartner_ID IS NOT NULL AND C_BPartner_Location_ID IS NULL")
			  .append(" AND I_IsImported='N'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Found Location=" + no);
		//	Set Bill Location from BPartner
		sql = new StringBuilder ("UPDATE I_OrderJP o ")
			  .append("SET BillTo_ID=(SELECT MAX(C_BPartner_Location_ID) FROM C_BPartner_Location l")
			  .append(" WHERE l.C_BPartner_ID=o.C_BPartner_ID AND o.AD_Client_ID=l.AD_Client_ID")
			  .append(" AND ((l.IsBillTo='Y' AND o.IsSOTrx='Y') OR (l.IsPayFrom='Y' AND o.IsSOTrx='N'))")
			  .append(") ")
			  .append("WHERE C_BPartner_ID IS NOT NULL AND BillTo_ID IS NULL")
			  .append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set BP BillTo from BP=" + no);
		//	Set Location from BPartner
		sql = new StringBuilder ("UPDATE I_OrderJP o ")
			  .append("SET C_BPartner_Location_ID=(SELECT MAX(C_BPartner_Location_ID) FROM C_BPartner_Location l")
			  .append(" WHERE l.C_BPartner_ID=o.C_BPartner_ID AND o.AD_Client_ID=l.AD_Client_ID")
			  .append(" AND ((l.IsShipTo='Y' AND o.IsSOTrx='Y') OR o.IsSOTrx='N')")
			  .append(") ")
			  .append("WHERE C_BPartner_ID IS NOT NULL AND C_BPartner_Location_ID IS NULL")
			  .append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set BP Location from BP=" + no);
		//
		sql = new StringBuilder ("UPDATE I_OrderJP ")
			  .append("SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=No BP Location, ' ")
			  .append("WHERE C_BPartner_ID IS NOT NULL AND (BillTo_ID IS NULL OR C_BPartner_Location_ID IS NULL)")
			  .append(" AND I_IsImported<>'Y'").append (clientCheck);
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
		if (processMonitor != null)	processMonitor.statusUpdate("通貨の設定する");
		sql = new StringBuilder ("UPDATE I_OrderJP o ")
			  .append("SET C_Country_ID=(SELECT C_Country_ID FROM C_Country c")
			  .append(" WHERE o.CountryCode=c.CountryCode AND c.AD_Client_ID IN (0, o.AD_Client_ID)) ")
			  .append("WHERE C_BPartner_ID IS NULL AND C_Country_ID IS NULL AND CountryCode IS NOT NULL")
			  .append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set Country=" + no);
		//
		sql = new StringBuilder ("UPDATE I_OrderJP ")
			  .append("SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Invalid Country, ' ")
			  .append("WHERE C_BPartner_ID IS NULL AND C_Country_ID IS NULL")
			  .append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (no != 0)
			log.warning ("Invalid Country=" + no);

		//	Set Region
		if (processMonitor != null)	processMonitor.statusUpdate("地域(都道府県)の設定");
		sql = new StringBuilder ("UPDATE I_OrderJP o ")
			  .append("Set RegionName=(SELECT MAX(Name) FROM C_Region r")
			  .append(" WHERE r.IsDefault='Y' AND r.C_Country_ID=o.C_Country_ID")
			  .append(" AND r.AD_Client_ID IN (0, o.AD_Client_ID)) ")
			  .append("WHERE C_BPartner_ID IS NULL AND C_Region_ID IS NULL AND RegionName IS NULL")
			  .append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set Region Default=" + no);
		//
		sql = new StringBuilder ("UPDATE I_OrderJP o ")
			  .append("Set C_Region_ID=(SELECT C_Region_ID FROM C_Region r")
			  .append(" WHERE r.Name=o.RegionName AND r.C_Country_ID=o.C_Country_ID")
			  .append(" AND r.AD_Client_ID IN (0, o.AD_Client_ID)) ")
			  .append("WHERE C_BPartner_ID IS NULL AND C_Region_ID IS NULL AND RegionName IS NOT NULL")
			  .append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set Region=" + no);
		//
		sql = new StringBuilder ("UPDATE I_OrderJP o ")
			  .append("SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Invalid Region, ' ")
			  .append("WHERE C_BPartner_ID IS NULL AND C_Region_ID IS NULL ")
			  .append(" AND EXISTS (SELECT * FROM C_Country c")
			  .append(" WHERE c.C_Country_ID=o.C_Country_ID AND c.HasRegion='Y')")
			  .append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (no != 0)
			log.warning ("Invalid Region=" + no);

		//	Product
		if (processMonitor != null)	processMonitor.statusUpdate("品目の設定");
		sql = new StringBuilder ("UPDATE I_OrderJP o ")
			  .append("SET M_Product_ID=(SELECT MAX(M_Product_ID) FROM M_Product p")
			  .append(" WHERE o.ProductValue=p.Value AND o.AD_Client_ID=p.AD_Client_ID) ")
			  .append("WHERE M_Product_ID IS NULL AND ProductValue IS NOT NULL")
			  .append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set Product from Value=" + no);
		sql = new StringBuilder ("UPDATE I_OrderJP o ")
			  .append("SET M_Product_ID=(SELECT MAX(M_Product_ID) FROM M_Product p")
			  .append(" WHERE o.UPC=p.UPC AND o.AD_Client_ID=p.AD_Client_ID) ")
			  .append("WHERE M_Product_ID IS NULL AND UPC IS NOT NULL")
			  .append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set Product from UPC=" + no);
		sql = new StringBuilder ("UPDATE I_OrderJP o ")
			  .append("SET M_Product_ID=(SELECT MAX(M_Product_ID) FROM M_Product p")
			  .append(" WHERE o.SKU=p.SKU AND o.AD_Client_ID=p.AD_Client_ID) ")
			  .append("WHERE M_Product_ID IS NULL AND SKU IS NOT NULL")
			  .append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set Product fom SKU=" + no);
		sql = new StringBuilder ("UPDATE I_OrderJP ")
			  .append("SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Invalid Product, ' ")
			  .append("WHERE M_Product_ID IS NULL AND (ProductValue IS NOT NULL OR UPC IS NOT NULL OR SKU IS NOT NULL)")
			  .append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (no != 0)
			log.warning ("Invalid Product=" + no);

		//	Charge
		if (processMonitor != null)	processMonitor.statusUpdate("料金タイプの設定");
		sql = new StringBuilder ("UPDATE I_OrderJP o ")
			  .append("SET C_Charge_ID=(SELECT C_Charge_ID FROM C_Charge c")
			  .append(" WHERE o.ChargeName=c.Name AND o.AD_Client_ID=c.AD_Client_ID) ")
			  .append("WHERE C_Charge_ID IS NULL AND ChargeName IS NOT NULL AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set Charge=" + no);
		sql = new StringBuilder ("UPDATE I_OrderJP ")
				  .append("SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Invalid Charge, ' ")
				  .append("WHERE C_Charge_ID IS NULL AND (ChargeName IS NOT NULL)")
				  .append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (no != 0)
			log.warning ("Invalid Charge=" + no);
		//

		sql = new StringBuilder ("UPDATE I_OrderJP ")
				  .append("SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Product and Charge, ' ")
				  .append("WHERE M_Product_ID IS NOT NULL AND C_Charge_ID IS NOT NULL ")
				  .append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (no != 0)
			log.warning ("Invalid Product and Charge exclusive=" + no);

		//	Tax
		if (processMonitor != null)	processMonitor.statusUpdate("税金情報の設定");
		sql = new StringBuilder ("UPDATE I_OrderJP o ")
			  .append("SET C_Tax_ID=(SELECT MAX(C_Tax_ID) FROM C_Tax t")
			  .append(" WHERE o.TaxIndicator=t.TaxIndicator AND o.AD_Client_ID=t.AD_Client_ID) ")
			  .append("WHERE C_Tax_ID IS NULL AND TaxIndicator IS NOT NULL")
			  .append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set Tax=" + no);
		sql = new StringBuilder ("UPDATE I_OrderJP ")
			  .append("SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Invalid Tax, ' ")
			  .append("WHERE C_Tax_ID IS NULL AND TaxIndicator IS NOT NULL")
			  .append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (no != 0)
			log.warning ("Invalid Tax=" + no);

		commitEx();



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
}	//	ImportOrder
