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
import org.compiere.model.MTable;
import org.compiere.model.MTableIndex;
import org.compiere.model.MUser;
import org.compiere.model.PO;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;

import jpiere.base.plugin.org.adempiere.model.MEstimation;
import jpiere.base.plugin.org.adempiere.model.MEstimationLine;
import jpiere.base.plugin.org.adempiere.model.X_I_EstimationJP;

/**
 *  JPIERE-0289
 *	Import Estimation from I_EstimationJP
 *  @author Ken Watanabe
 */
public class JPiereImportEstimation extends SvrProcess
{
	/**	Client to be imported to		*/
	private int				m_AD_Client_ID = 0;
	/**	Organization to be imported to		*/
	private int				m_AD_Org_ID = 0;
	/**	Delete old Imported				*/
	private boolean			m_deleteOldImported = false;
	/**	Document Action					*/
	private String			m_docAction = MEstimation.DOCACTION_Prepare;


	/** Effective						*/
	private Timestamp		m_DateValue = null;

	private boolean			isRecordCommitJP =false;

	private boolean 		isDeleteIndexJP = false;

	private boolean			isInvalidConstraintJP = false;

	private boolean			isMonitoringProcessJP = true;

	@SuppressWarnings("unused")
	private boolean			isUniqueCheckJP = false;

	@SuppressWarnings("unused")
	private String[] EstimationTables = new String[] {
			"JP_Estimation",
			"JP_EstimationLine"
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
			sql = new StringBuilder ("DELETE I_EstimationJP ")
				  .append("WHERE I_IsImported='Y'").append (clientCheck);
			no = DB.executeUpdate(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("Delete Old Impored =" + no);
		}

		if (processMonitor != null)	processMonitor.statusUpdate("組織、更新情報の設定");
		sql = new StringBuilder ("UPDATE I_EstimationJP ")
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
		sql = new StringBuilder ("UPDATE I_EstimationJP e ")
			.append("SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Invalid Org, '")
			.append("WHERE (AD_Org_ID IS NULL OR AD_Org_ID=0")
			.append(" OR EXISTS (SELECT * FROM AD_Org o WHERE e.AD_Org_ID=o.AD_Org_ID AND (o.IsSummary='Y' OR o.IsActive='N')))")
			.append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (no != 0)
			log.warning ("Invalid Org=" + no);

		//	Document Type - JPE
		if (processMonitor != null)	processMonitor.statusUpdate("伝票タイプを伝票タイプの名称より逆引き設定");
		sql = new StringBuilder ("UPDATE I_EstimationJP e ")	//	JPE Document Type Name
			  .append("SET C_DocType_ID=(SELECT C_DocType_ID FROM C_DocType d WHERE d.Name=e.DocTypeName")
			  .append(" AND d.DocBaseType='JPE' AND e.AD_Client_ID=d.AD_Client_ID) ")
			  .append("WHERE e.C_DocType_ID IS NULL AND e.DocTypeName IS NOT NULL AND e.I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set JPE DocType=" + no);
		sql = new StringBuilder ("UPDATE I_EstimationJP ")	//	Error Invalid Doc Type Name
			  .append("SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Invalid DocTypeName, ' ")
			  .append("WHERE C_DocType_ID IS NULL AND DocTypeName IS NOT NULL")
			  .append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (no != 0)
			log.warning ("Invalid DocTypeName=" + no);

		//	DocType Default
		if (processMonitor != null)	processMonitor.statusUpdate("伝票タイプが設定されていないデータに、伝票タイプを設定する");
		sql = new StringBuilder ("UPDATE I_EstimationJP e ")	//	Default JPE
			  .append("SET C_DocType_ID=(SELECT MAX(C_DocType_ID) FROM C_DocType d WHERE d.IsDefault='Y'")
			  .append(" AND d.DocBaseType='JPE' AND e.AD_Client_ID=d.AD_Client_ID) ")
			  .append("WHERE C_DocType_ID IS NULL AND IsSOTrx='N' AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set JPE Default DocType=" + no);
		sql = new StringBuilder ("UPDATE I_EstimationJP ")	// No DocType
			  .append("SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=No DocType, ' ")
			  .append("WHERE C_DocType_ID IS NULL")
			  .append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (no != 0)
			log.warning ("No DocType=" + no);

		//	Set IsSOTrx
		if (processMonitor != null)	processMonitor.statusUpdate("伝票タイプからIsSOTrxを設定する");
		sql = new StringBuilder ("UPDATE I_EstimationJP e SET IsSOTrx='Y' ")
			  .append("WHERE EXISTS (SELECT * FROM C_DocType d WHERE e.C_DocType_ID=d.C_DocType_ID AND d.DocBaseType='SOO' AND e.AD_Client_ID=d.AD_Client_ID)")
			  .append(" AND C_DocType_ID IS NOT NULL")
			  .append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set IsSOTrx=Y=" + no);
		sql = new StringBuilder ("UPDATE I_EstimationJP e SET IsSOTrx='N' ")
			  .append("WHERE EXISTS (SELECT * FROM C_DocType d WHERE e.C_DocType_ID=d.C_DocType_ID AND d.DocBaseType='POO' AND e.AD_Client_ID=d.AD_Client_ID)")
			  .append(" AND C_DocType_ID IS NOT NULL")
			  .append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set IsSOTrx=N=" + no);

		//	Price List
		if (processMonitor != null)	processMonitor.statusUpdate("プライスリストを設定する");
		sql = new StringBuilder ("UPDATE I_EstimationJP e ")
			  .append("SET M_PriceList_ID=(SELECT MAX(M_PriceList_ID) FROM M_PriceList p WHERE p.IsDefault='Y'")
			  .append(" AND p.C_Currency_ID=e.C_Currency_ID AND p.IsSOPriceList=e.IsSOTrx AND e.AD_Client_ID=p.AD_Client_ID) ")
			  .append("WHERE M_PriceList_ID IS NULL AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set Default Currency PriceList=" + no);
		sql = new StringBuilder ("UPDATE I_EstimationJP e ")
			  .append("SET M_PriceList_ID=(SELECT MAX(M_PriceList_ID) FROM M_PriceList p WHERE p.IsDefault='Y'")
			  .append(" AND p.IsSOPriceList=e.IsSOTrx AND e.AD_Client_ID=p.AD_Client_ID) ")
			  .append("WHERE M_PriceList_ID IS NULL AND C_Currency_ID IS NULL AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set Default PriceList=" + no);
		sql = new StringBuilder ("UPDATE I_EstimationJP e ")
			  .append("SET M_PriceList_ID=(SELECT MAX(M_PriceList_ID) FROM M_PriceList p ")
			  .append(" WHERE p.C_Currency_ID=e.C_Currency_ID AND p.IsSOPriceList=e.IsSOTrx AND e.AD_Client_ID=p.AD_Client_ID) ")
			  .append("WHERE M_PriceList_ID IS NULL AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set Currency PriceList=" + no);
		sql = new StringBuilder ("UPDATE I_EstimationJP e ")
			  .append("SET M_PriceList_ID=(SELECT MAX(M_PriceList_ID) FROM M_PriceList p ")
			  .append(" WHERE p.IsSOPriceList=e.IsSOTrx AND e.AD_Client_ID=p.AD_Client_ID) ")
			  .append("WHERE M_PriceList_ID IS NULL AND C_Currency_ID IS NULL AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set PriceList=" + no);
		//
		sql = new StringBuilder ("UPDATE I_EstimationJP ")
			  .append("SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=No PriceList, ' ")
			  .append("WHERE M_PriceList_ID IS NULL")
			  .append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (no != 0)
			log.warning("No PriceList=" + no);

		// @Trifon - Import Order Source
		if (processMonitor != null)	processMonitor.statusUpdate("Order Sourceを設定する");
		sql = new StringBuilder ("UPDATE I_EstimationJP e ")
			  .append("SET C_OrderSource_ID=(SELECT C_OrderSource_ID FROM C_OrderSource o")
			  .append(" WHERE e.C_OrderSourceValue=o.Value AND e.AD_Client_ID=o.AD_Client_ID) ")
			  .append("WHERE C_OrderSource_ID IS NULL AND C_OrderSourceValue IS NOT NULL AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set Order Source=" + no);
		// Set proper error message
		sql = new StringBuilder ("UPDATE I_EstimationJP ")
			  .append("SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Not Found Order Source, ' ")
			  .append("WHERE C_OrderSource_ID IS NULL AND C_OrderSourceValue IS NOT NULL AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (no != 0)
			log.warning("No OrderSource=" + no);

		//	Payment Term
		if (processMonitor != null)	processMonitor.statusUpdate("支払条件を設定する");
		sql = new StringBuilder ("UPDATE I_EstimationJP e ")
			  .append("SET C_PaymentTerm_ID=(SELECT C_PaymentTerm_ID FROM C_PaymentTerm p")
			  .append(" WHERE e.PaymentTermValue=p.Value AND e.AD_Client_ID=p.AD_Client_ID) ")
			  .append("WHERE C_PaymentTerm_ID IS NULL AND PaymentTermValue IS NOT NULL AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set PaymentTerm=" + no);
		sql = new StringBuilder ("UPDATE I_EstimationJP e ")
			  .append("SET C_PaymentTerm_ID=(SELECT MAX(C_PaymentTerm_ID) FROM C_PaymentTerm p")
			  .append(" WHERE p.IsDefault='Y' AND e.AD_Client_ID=p.AD_Client_ID) ")
			  .append("WHERE C_PaymentTerm_ID IS NULL AND e.PaymentTermValue IS NULL AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set Default PaymentTerm=" + no);
		//
		sql = new StringBuilder ("UPDATE I_EstimationJP ")
			  .append("SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=No PaymentTerm, ' ")
			  .append("WHERE C_PaymentTerm_ID IS NULL")
			  .append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (no != 0)
			log.warning ("No PaymentTerm=" + no);

		//	Warehouse
		if (processMonitor != null)	processMonitor.statusUpdate("倉庫を設定する");
		sql = new StringBuilder ("UPDATE I_EstimationJP e ")
			  .append("SET M_Warehouse_ID=(SELECT MAX(M_Warehouse_ID) FROM M_Warehouse w")
			  .append(" WHERE e.AD_Client_ID=w.AD_Client_ID AND e.AD_Org_ID=w.AD_Org_ID) ")
			  .append("WHERE M_Warehouse_ID IS NULL AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());	//	Warehouse for Org
		if (no != 0)
			if (log.isLoggable(Level.FINE)) log.fine("Set Warehouse=" + no);
		sql = new StringBuilder ("UPDATE I_EstimationJP e ")
			  .append("SET M_Warehouse_ID=(SELECT M_Warehouse_ID FROM M_Warehouse w")
			  .append(" WHERE e.AD_Client_ID=w.AD_Client_ID) ")
			  .append("WHERE M_Warehouse_ID IS NULL")
			  .append(" AND EXISTS (SELECT AD_Client_ID FROM M_Warehouse w WHERE w.AD_Client_ID=e.AD_Client_ID GROUP BY AD_Client_ID HAVING COUNT(*)=1)")
			  .append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (no != 0)
			if (log.isLoggable(Level.FINE)) log.fine("Set Only Client Warehouse=" + no);
		//
		sql = new StringBuilder ("UPDATE I_EstimationJP ")
			  .append("SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=No Warehouse, ' ")
			  .append("WHERE M_Warehouse_ID IS NULL")
			  .append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (no != 0)
			log.warning ("No Warehouse=" + no);

		//	BP from EMail
		if (processMonitor != null)	processMonitor.statusUpdate("取引先をユーザーのEmailより逆引き設定する");
		sql = new StringBuilder ("UPDATE I_EstimationJP e ")
			  .append("SET (C_BPartner_ID,AD_User_ID)=(SELECT C_BPartner_ID,AD_User_ID FROM AD_User u")
			  .append(" WHERE e.EMail=u.EMail AND e.AD_Client_ID=u.AD_Client_ID AND u.C_BPartner_ID IS NOT NULL) ")
			  .append("WHERE C_BPartner_ID IS NULL AND EMail IS NOT NULL")
			  .append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set BP from EMail=" + no);
		//	BP from ContactName
		if (processMonitor != null)	processMonitor.statusUpdate("取引先をユーザーのContact Nameより逆引き設定する");
		sql = new StringBuilder ("UPDATE I_EstimationJP e ")
			  .append("SET (C_BPartner_ID,AD_User_ID)=(SELECT C_BPartner_ID,AD_User_ID FROM AD_User u")
			  .append(" WHERE e.ContactName=u.Name AND e.AD_Client_ID=u.AD_Client_ID AND u.C_BPartner_ID IS NOT NULL) ")
			  .append("WHERE C_BPartner_ID IS NULL AND ContactName IS NOT NULL")
			  .append(" AND EXISTS (SELECT Name FROM AD_User u WHERE e.ContactName=u.Name AND e.AD_Client_ID=u.AD_Client_ID AND u.C_BPartner_ID IS NOT NULL GROUP BY Name HAVING COUNT(*)=1)")
			  .append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set BP from ContactName=" + no);
		//	BP from Value
		if (processMonitor != null)	processMonitor.statusUpdate("取引先を取引先の検索キーより逆引き設定する");
		sql = new StringBuilder ("UPDATE I_EstimationJP e ")
			  .append("SET C_BPartner_ID=(SELECT MAX(C_BPartner_ID) FROM C_BPartner bp")
			  .append(" WHERE e.BPartnerValue=bp.Value AND e.AD_Client_ID=bp.AD_Client_ID) ")
			  .append("WHERE C_BPartner_ID IS NULL AND BPartnerValue IS NOT NULL")
			  .append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set BP from Value=" + no);
		//	Default BP
		if (processMonitor != null)	processMonitor.statusUpdate("取引先が無いデータに対して、デフォルトの取引先を設定する");
		sql = new StringBuilder ("UPDATE I_EstimationJP e ")
			  .append("SET C_BPartner_ID=(SELECT C_BPartnerCashTrx_ID FROM AD_ClientInfo c")
			  .append(" WHERE e.AD_Client_ID=c.AD_Client_ID) ")
			  .append("WHERE C_BPartner_ID IS NULL AND BPartnerValue IS NULL AND Name IS NULL")
			  .append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set Default BP=" + no);

		//	Existing Location ? Exact Match
		if (processMonitor != null)	processMonitor.statusUpdate("取引先と請求先の住所を設定する");
		sql = new StringBuilder ("UPDATE I_EstimationJP e ")
			  .append("SET (BillTo_ID,C_BPartner_Location_ID)=(SELECT C_BPartner_Location_ID,C_BPartner_Location_ID")
			  .append(" FROM C_BPartner_Location bpl INNER JOIN C_Location l ON (bpl.C_Location_ID=l.C_Location_ID)")
			  .append(" WHERE e.C_BPartner_ID=bpl.C_BPartner_ID AND bpl.AD_Client_ID=e.AD_Client_ID")
			  .append(" AND DUMP(e.Address1)=DUMP(l.Address1) AND DUMP(e.Address2)=DUMP(l.Address2)")
			  .append(" AND DUMP(e.City)=DUMP(l.City) AND DUMP(e.Postal)=DUMP(l.Postal)")
			  .append(" AND e.C_Region_ID=l.C_Region_ID AND e.C_Country_ID=l.C_Country_ID) ")
			  .append("WHERE C_BPartner_ID IS NOT NULL AND C_BPartner_Location_ID IS NULL")
			  .append(" AND I_IsImported='N'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Found Location=" + no);
		//	Set Bill Location from BPartner
		sql = new StringBuilder ("UPDATE I_EstimationJP e ")
			  .append("SET BillTo_ID=(SELECT MAX(C_BPartner_Location_ID) FROM C_BPartner_Location l")
			  .append(" WHERE l.C_BPartner_ID=e.C_BPartner_ID AND e.AD_Client_ID=l.AD_Client_ID")
			  .append(" AND ((l.IsBillTo='Y' AND e.IsSOTrx='Y') OR (l.IsPayFrom='Y' AND e.IsSOTrx='N'))")
			  .append(") ")
			  .append("WHERE C_BPartner_ID IS NOT NULL AND BillTo_ID IS NULL")
			  .append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set BP BillTo from BP=" + no);
		//	Set Location from BPartner
		sql = new StringBuilder ("UPDATE I_EstimationJP e ")
			  .append("SET C_BPartner_Location_ID=(SELECT MAX(C_BPartner_Location_ID) FROM C_BPartner_Location l")
			  .append(" WHERE l.C_BPartner_ID=e.C_BPartner_ID AND e.AD_Client_ID=l.AD_Client_ID")
			  .append(" AND ((l.IsShipTo='Y' AND e.IsSOTrx='Y') OR e.IsSOTrx='N')")
			  .append(") ")
			  .append("WHERE C_BPartner_ID IS NOT NULL AND C_BPartner_Location_ID IS NULL")
			  .append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set BP Location from BP=" + no);
		//
		sql = new StringBuilder ("UPDATE I_EstimationJP ")
			  .append("SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=No BP Location, ' ")
			  .append("WHERE C_BPartner_ID IS NOT NULL AND (BillTo_ID IS NULL OR C_BPartner_Location_ID IS NULL)")
			  .append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (no != 0)
			log.warning ("No BP Location=" + no);

		//	Set Country
		/**
		sql = new StringBuffer ("UPDATE I_EstimationJP e "
			  + "SET CountryCode=(SELECT MAX(CountryCode) FROM C_Country c WHERE c.IsDefault='Y'"
			  + " AND c.AD_Client_ID IN (0, e.AD_Client_ID)) "
			  + "WHERE C_BPartner_ID IS NULL AND CountryCode IS NULL AND C_Country_ID IS NULL"
			  + " AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		log.fine("Set Country Default=" + no);
		**/
		if (processMonitor != null)	processMonitor.statusUpdate("通貨の設定する");
		sql = new StringBuilder ("UPDATE I_EstimationJP e ")
			  .append("SET C_Country_ID=(SELECT C_Country_ID FROM C_Country c")
			  .append(" WHERE e.CountryCode=c.CountryCode AND c.AD_Client_ID IN (0, e.AD_Client_ID)) ")
			  .append("WHERE C_BPartner_ID IS NULL AND C_Country_ID IS NULL AND CountryCode IS NOT NULL")
			  .append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set Country=" + no);
		//
		sql = new StringBuilder ("UPDATE I_EstimationJP ")
			  .append("SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Invalid Country, ' ")
			  .append("WHERE C_BPartner_ID IS NULL AND C_Country_ID IS NULL")
			  .append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (no != 0)
			log.warning ("Invalid Country=" + no);

		//	Set Region
		if (processMonitor != null)	processMonitor.statusUpdate("地域(都道府県)の設定");
		sql = new StringBuilder ("UPDATE I_EstimationJP e ")
			  .append("Set RegionName=(SELECT MAX(Name) FROM C_Region r")
			  .append(" WHERE r.IsDefault='Y' AND r.C_Country_ID=e.C_Country_ID")
			  .append(" AND r.AD_Client_ID IN (0, e.AD_Client_ID)) ")
			  .append("WHERE C_BPartner_ID IS NULL AND C_Region_ID IS NULL AND RegionName IS NULL")
			  .append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set Region Default=" + no);
		//
		sql = new StringBuilder ("UPDATE I_EstimationJP e ")
			  .append("Set C_Region_ID=(SELECT C_Region_ID FROM C_Region r")
			  .append(" WHERE r.Name=e.RegionName AND r.C_Country_ID=e.C_Country_ID")
			  .append(" AND r.AD_Client_ID IN (0, e.AD_Client_ID)) ")
			  .append("WHERE C_BPartner_ID IS NULL AND C_Region_ID IS NULL AND RegionName IS NOT NULL")
			  .append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set Region=" + no);
		//
		sql = new StringBuilder ("UPDATE I_EstimationJP e ")
			  .append("SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Invalid Region, ' ")
			  .append("WHERE C_BPartner_ID IS NULL AND C_Region_ID IS NULL ")
			  .append(" AND EXISTS (SELECT * FROM C_Country c")
			  .append(" WHERE c.C_Country_ID=e.C_Country_ID AND c.HasRegion='Y')")
			  .append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (no != 0)
			log.warning ("Invalid Region=" + no);

		//	Product
		if (processMonitor != null)	processMonitor.statusUpdate("品目の設定");
		sql = new StringBuilder ("UPDATE I_EstimationJP e ")
			  .append("SET M_Product_ID=(SELECT MAX(M_Product_ID) FROM M_Product p")
			  .append(" WHERE e.ProductValue=p.Value AND e.AD_Client_ID=p.AD_Client_ID) ")
			  .append("WHERE M_Product_ID IS NULL AND ProductValue IS NOT NULL")
			  .append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set Product from Value=" + no);
		sql = new StringBuilder ("UPDATE I_EstimationJP e ")
			  .append("SET M_Product_ID=(SELECT MAX(M_Product_ID) FROM M_Product p")
			  .append(" WHERE e.UPC=p.UPC AND e.AD_Client_ID=p.AD_Client_ID) ")
			  .append("WHERE M_Product_ID IS NULL AND UPC IS NOT NULL")
			  .append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set Product from UPC=" + no);
		sql = new StringBuilder ("UPDATE I_EstimationJP e ")
			  .append("SET M_Product_ID=(SELECT MAX(M_Product_ID) FROM M_Product p")
			  .append(" WHERE e.SKU=p.SKU AND e.AD_Client_ID=p.AD_Client_ID) ")
			  .append("WHERE M_Product_ID IS NULL AND SKU IS NOT NULL")
			  .append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set Product fom SKU=" + no);
		sql = new StringBuilder ("UPDATE I_EstimationJP ")
			  .append("SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Invalid Product, ' ")
			  .append("WHERE M_Product_ID IS NULL AND (ProductValue IS NOT NULL OR UPC IS NOT NULL OR SKU IS NOT NULL)")
			  .append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (no != 0)
			log.warning ("Invalid Product=" + no);

		//	Charge
		if (processMonitor != null)	processMonitor.statusUpdate("料金タイプの設定");
		sql = new StringBuilder ("UPDATE I_EstimationJP e ")
			  .append("SET C_Charge_ID=(SELECT C_Charge_ID FROM C_Charge c")
			  .append(" WHERE e.ChargeName=c.Name AND e.AD_Client_ID=c.AD_Client_ID) ")
			  .append("WHERE C_Charge_ID IS NULL AND ChargeName IS NOT NULL AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set Charge=" + no);
		sql = new StringBuilder ("UPDATE I_EstimationJP ")
				  .append("SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Invalid Charge, ' ")
				  .append("WHERE C_Charge_ID IS NULL AND (ChargeName IS NOT NULL)")
				  .append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (no != 0)
			log.warning ("Invalid Charge=" + no);
		//

		sql = new StringBuilder ("UPDATE I_EstimationJP ")
				  .append("SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Product and Charge, ' ")
				  .append("WHERE M_Product_ID IS NOT NULL AND C_Charge_ID IS NOT NULL ")
				  .append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (no != 0)
			log.warning ("Invalid Product and Charge exclusive=" + no);

		//	Tax
		if (processMonitor != null)	processMonitor.statusUpdate("税金情報の設定");
		sql = new StringBuilder ("UPDATE I_EstimationJP e ")
			  .append("SET C_Tax_ID=(SELECT MAX(C_Tax_ID) FROM C_Tax t")
			  .append(" WHERE e.TaxIndicator=t.TaxIndicator AND e.AD_Client_ID=t.AD_Client_ID) ")
			  .append("WHERE C_Tax_ID IS NULL AND TaxIndicator IS NOT NULL")
			  .append(" AND I_IsImported<>'Y'").append (clientCheck);
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Set Tax=" + no);
		sql = new StringBuilder ("UPDATE I_EstimationJP ")
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
					DB.executeUpdateEx(indexDropSql, get_TrxName());
//					addLog(0, null, new BigDecimal(rvalue), indexDropSql.toString());
				}
			}
			commitEx();

			long endDeleteIndex = System.currentTimeMillis();
			addLog("Indexの削除時間: " + (endDeleteIndex - startDeleteIndex) + " ミリ秒");
		}


		//	-- New BPartner ---------------------------------------------------

		//	Go through Estimation Records w/o C_BPartner_ID
		if (processMonitor != null)	processMonitor.statusUpdate("新規取引先の登録");
		long startCreateBP = System.currentTimeMillis();

		sql = new StringBuilder ("SELECT * FROM I_EstimationJP ")
			  .append("WHERE I_IsImported='N' AND C_BPartner_ID IS NULL").append (clientCheck);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (sql.toString(), get_TrxName());
			rs = pstmt.executeQuery ();
			while (rs.next ())
			{
				X_I_EstimationJP imp = new X_I_EstimationJP (getCtx (), rs, get_TrxName());
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
		sql = new StringBuilder ("UPDATE I_EstimationJP ")
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


		//	-- New Estimations -----------------------------------------------------

		long startImport = System.currentTimeMillis();
		if (processMonitor != null)	processMonitor.statusUpdate("見積伝票の登録");

		int noInsert = 0;
		int noInsertLine = 0;

		//	Go through Estimation Records w/o
		sql = new StringBuilder ("SELECT * FROM I_EstimationJP ")
			  .append("WHERE I_IsImported='N'").append (clientCheck)
			.append(" ORDER BY DateOrdered, C_BPartner_ID, BillTo_ID, C_BPartner_Location_ID, I_EstimationJP_ID");
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
			MEstimation estimation = null;
			int lineNo = 0;
			while (rs.next ())
			{
				X_I_EstimationJP imp = new X_I_EstimationJP (getCtx (), rs, get_TrxName());
				String cmpDocumentNo = imp.getDocumentNo();
				if (cmpDocumentNo == null)
					cmpDocumentNo = "";
				//	New Estimation
				if (oldC_BPartner_ID != imp.getC_BPartner_ID()
					|| oldC_BPartner_Location_ID != imp.getC_BPartner_Location_ID()
					|| oldBillTo_ID != imp.getBillTo_ID()
					|| !oldDocumentNo.equals(cmpDocumentNo))
				{
					if (estimation != null)
					{
						if (m_docAction != null && m_docAction.length() > 0)
						{
//							estimation.setDocAction(m_docAction);
							if(!estimation.processIt (m_docAction)) {
								log.warning("Estimation Process Failed: " + estimation + " - " + estimation.getProcessMsg());
								throw new IllegalStateException("Estimation Process Failed: " + estimation + " - " + estimation.getProcessMsg());

							}
						}
						estimation.saveEx();

						if(isRecordCommitJP)
							commitEx();

						if (processMonitor != null)	processMonitor.statusUpdate(estimation.getDocumentNo()+"の処理が完了しました。");
					}
					oldC_BPartner_ID = imp.getC_BPartner_ID();
					oldC_BPartner_Location_ID = imp.getC_BPartner_Location_ID();
					oldBillTo_ID = imp.getBillTo_ID();
					oldDocumentNo = imp.getDocumentNo();
					if (oldDocumentNo == null)
						oldDocumentNo = "";
					//
					estimation = new MEstimation (getCtx(), 0, get_TrxName());
					//estimation.setClientOrg (imp.getAD_Client_ID(), imp.getAD_Org_ID());
					PO.copyValues(imp, estimation);
					estimation.setAD_Org_ID(imp.getAD_Org_ID());
					estimation.setC_DocTypeTarget_ID(imp.getC_DocType_ID());
					estimation.setIsSOTrx(imp.isSOTrx());
					estimation.setDocAction(m_docAction);
					estimation.setDocStatus(DocAction.STATUS_Drafted);
					if (imp.getDeliveryRule() != null ) {
						estimation.setDeliveryRule(imp.getDeliveryRule());
					}
					if (imp.getInvoiceRule() != null ) {
						estimation.setInvoiceRule(imp.getInvoiceRule());
					}
					if (imp.getFreightCostRule() != null ) {
						estimation.setFreightCostRule(imp.getFreightCostRule());
					}
					if (imp.getDeliveryViaRule() != null ) {
						estimation.setDeliveryViaRule(imp.getDeliveryViaRule());
					}
					if (imp.getPriorityRule() != null ) {
						estimation.setPriorityRule(imp.getPriorityRule());
					}
					if (imp.getDocumentNo() != null)
						estimation.setDocumentNo(imp.getDocumentNo());
					//	Ship Partner
					estimation.setC_BPartner_ID(imp.getC_BPartner_ID());
					estimation.setC_BPartner_Location_ID(imp.getC_BPartner_Location_ID());
					if (imp.getAD_User_ID() != 0)
						estimation.setAD_User_ID(imp.getAD_User_ID());
					//	Bill Partner
					estimation.setBill_BPartner_ID(imp.getC_BPartner_ID());
					estimation.setBill_Location_ID(imp.getBillTo_ID());
					//
					if (imp.getDescription() != null)
						estimation.setDescription(imp.getDescription());
					estimation.setPaymentRule(imp.getPaymentRule());
					estimation.setC_PaymentTerm_ID(imp.getC_PaymentTerm_ID());
					estimation.setM_PriceList_ID(imp.getM_PriceList_ID());
					estimation.setM_Warehouse_ID(imp.getM_Warehouse_ID());
					if (imp.getM_Shipper_ID() != 0)
						estimation.setM_Shipper_ID(imp.getM_Shipper_ID());
					//	SalesRep from Import or the person running the import
					if (imp.getSalesRep_ID() != 0)
						estimation.setSalesRep_ID(imp.getSalesRep_ID());
					if (estimation.getSalesRep_ID() == 0)
						estimation.setSalesRep_ID(getAD_User_ID());
					//
					if (imp.getAD_OrgTrx_ID() != 0)
						estimation.setAD_OrgTrx_ID(imp.getAD_OrgTrx_ID());
					if (imp.getC_Activity_ID() != 0)
						estimation.setC_Activity_ID(imp.getC_Activity_ID());
					if (imp.getC_Campaign_ID() != 0)
						estimation.setC_Campaign_ID(imp.getC_Campaign_ID());
					if (imp.getC_Project_ID() != 0)
						estimation.setC_Project_ID(imp.getC_Project_ID());
					//
					if (imp.getDateOrdered() != null)
						estimation.setDateOrdered(imp.getDateOrdered());
					if (imp.getDateAcct() != null)
						estimation.setDateAcct(imp.getDateAcct());
					if (imp.getDatePromised() != null)
						estimation.setDatePromised(imp.getDatePromised());

					// Set Order Source
					if (imp.getC_OrderSource() != null)
						estimation.setC_OrderSource_ID(imp.getC_OrderSource_ID());
					//
					estimation.saveEx();
					noInsert++;
					lineNo = 10;
				}//if
				imp.setJP_Estimation_ID(estimation.getJP_Estimation_ID());
				//	New EstimationLine
				MEstimationLine line = new MEstimationLine (estimation);
				line.setLine(lineNo);
				lineNo += 10;
				if (imp.getM_Product_ID() != 0)
					//line.setM_Product_ID(imp.getM_Product_ID(), true);
					line.setM_Product_ID(imp.getM_Product_ID());
				if (imp.getC_Charge_ID() != 0)
					line.setC_Charge_ID(imp.getC_Charge_ID());
				line.setQtyOrdered(imp.getQtyOrdered());
				line.setPrice();
				if (imp.getPriceActual().compareTo(Env.ZERO) != 0)
					line.setPriceActual(imp.getPriceActual());
				if (imp.getC_Tax_ID() != 0)
					line.setC_Tax_ID(imp.getC_Tax_ID());
				else
				{
				//	line.setTax();
					imp.setC_Tax_ID(line.getC_Tax_ID());
				}
				if (imp.getFreightAmt() != null)
					line.setFreightAmt(imp.getFreightAmt());
				if (imp.getLineDescription() != null)
					line.setDescription(imp.getLineDescription());
				line.saveEx();
				imp.setJP_EstimationLine_ID(line.getJP_EstimationLine_ID());
				imp.setI_IsImported(true);
				imp.setProcessed(true);
				//
				if (imp.save())
					noInsertLine++;
			}//While
			if (estimation != null)
			{
				if (m_docAction != null && m_docAction.length() > 0)
				{
					estimation.setDocAction(m_docAction);
					if(!estimation.processIt (m_docAction)) {
						log.warning("Estimation Process Failed: " + estimation + " - " + estimation.getProcessMsg());
						throw new IllegalStateException("Estimation Process Failed: " + estimation + " - " + estimation.getProcessMsg());

					}
				}
				estimation.saveEx();

				if(isRecordCommitJP)
					commitEx();

				if (processMonitor != null)	processMonitor.statusUpdate(estimation.getDocumentNo()+"の処理が完了しました。");
			}
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, "Estimation - " + sql.toString(), e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

		//	Set Error to indicator to not imported
		sql = new StringBuilder ("UPDATE I_EstimationJP ")
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
		addLog (0, null, new BigDecimal (noInsert), "@JP_Estimation_ID@: @Inserted@");
		addLog (0, null, new BigDecimal (noInsertLine), "@JP_EstimationLine_ID@: @Inserted@");
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
}	//	ImportEstimation

