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
import org.compiere.model.MWarehouse;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.X_M_Warehouse_Acct;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.X_I_WarehouseJP;
import jpiere.base.plugin.util.JPiereLocationUtil;
import jpiere.base.plugin.util.JPiereValidCombinationUtil;

/**
 * 	JPIERE-0393:Import Warehouse
 *
 *  @author Hideaki Hagiwara
 *
 */
public class JPiereImportWarehouse extends SvrProcess  implements ImportProcess
{
	/**	Client to be imported to		*/
	private int				m_AD_Client_ID = 0;

	private boolean p_deleteOldImported = false;

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
			if (name.equals("DeleteOldImported"))
				p_deleteOldImported = "Y".equals(para[i].getParameter());
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
			sql = new StringBuilder ("DELETE I_WarehouseJP ")
				  .append("WHERE I_IsImported='Y'").append (clientCheck);
			no = DB.executeUpdate(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("Delete Old Impored =" + no);
		}

		//Reset Message
		sql = new StringBuilder ("UPDATE I_WarehouseJP ")
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
		reverseLookupM_Warehouse_ID();
		reverseLookupAD_Org_ID();
		reverseLookupC_AcctSchema_ID();
		reverseLookupC_Location_ID();
		reverseLookupLocationAD_Org_ID();

		ModelValidationEngine.get().fireImportValidate(this, null, null, ImportValidator.TIMING_AFTER_VALIDATE);

		commitEx();

		//
		sql = new StringBuilder ("SELECT * FROM I_WarehouseJP WHERE I_IsImported='N'")
					.append(clientCheck);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int recordsNum = 0;
		int successNewNum = 0;
		int successUpdateNum = 0;
		int failureNewNum = 0;
		int failureUpdateNum = 0;
		String records = Msg.getMsg(getCtx(), "JP_NumberOfRecords");
		String success = Msg.getMsg(getCtx(), "JP_Success");
		String failure = Msg.getMsg(getCtx(), "JP_Failure");
		String newRecord = Msg.getMsg(getCtx(), "New");
		String updateRecord = Msg.getMsg(getCtx(), "Update");

		try
		{
			pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
			rs = pstmt.executeQuery();
			while (rs.next())
			{
				X_I_WarehouseJP imp = new X_I_WarehouseJP (getCtx (), rs, get_TrxName());

				boolean isNew = true;
				if(imp.getM_Warehouse_ID()!=0){
					isNew =false;
				}

				if(isNew)//Create
				{
					MWarehouse newWarehouse = new MWarehouse(getCtx (), 0, get_TrxName());
					if(createNewWarehouse(imp, newWarehouse))
						successNewNum++;
					else
						failureNewNum++;

				}else{//Update

					MWarehouse updateWarehouse = new MWarehouse(getCtx (), imp.getM_Warehouse_ID(), get_TrxName());
					if(updateWarehouse(imp, updateWarehouse))
						successUpdateNum++;
					else
						failureUpdateNum++;
				}

				commitEx();

				recordsNum++;
				if (processMonitor != null)
				{
					processMonitor.statusUpdate(
						newRecord + "( "+  success + " : " + successNewNum + "  /  " +  failure + " : " + failureNewNum + " ) + "
						+ updateRecord + " ( "+  success + " : " + successUpdateNum + "  /  " +  failure + " : " + failureUpdateNum+ " ) "
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

		return records + recordsNum + " = "	+
		newRecord + "( "+  success + " : " + successNewNum + "  /  " +  failure + " : " + failureNewNum + " ) + "
		+ updateRecord + " ( "+  success + " : " + successUpdateNum + "  /  " +  failure + " : " + failureUpdateNum+ " ) ";

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
	 * Reverese Look up  M_Warehouse_ID From Value
	 *
	 * @throws Exception
	 */
	private void reverseLookupM_Warehouse_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		String msg = new String();
		int no = 0;

		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "M_Warehouse_ID");
		if (processMonitor != null)	processMonitor.statusUpdate(msg);

		//Reverese Look up  M_Warehouse_ID From Value
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "M_Warehouse_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "Value") ;
		sql = new StringBuilder ("UPDATE I_WarehouseJP i ")
				.append("SET M_Warehouse_ID=(SELECT M_Warehouse_ID FROM M_Warehouse p")
				.append(" WHERE i.Value=p.Value AND p.AD_Client_ID=i.AD_Client_ID) ")
				.append(" WHERE i.M_Warehouse_ID IS NULL AND i.Value IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no + ":" + sql);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + sql );
		}

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
		sql = new StringBuilder ("UPDATE I_WarehouseJP i ")
				.append("SET AD_Org_ID=(SELECT AD_Org_ID FROM AD_org p")
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
		sql = new StringBuilder ("UPDATE I_WarehouseJP ")
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
		sql = new StringBuilder ("UPDATE I_WarehouseJP i ")
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
		sql = new StringBuilder ("UPDATE I_WarehouseJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append(" WHERE C_AcctSchema_ID IS NULL AND JP_AcctSchema_Name IS NOT NULL ")
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

	}

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
		sql = new StringBuilder ("UPDATE I_WarehouseJP i ")
				.append("SET C_Location_ID=(SELECT C_Location_ID FROM C_Location p")
				.append(" WHERE i.JP_Location_Label= p.JP_Location_Label AND p.AD_Client_ID=i.AD_Client_ID) ")
				.append(" WHERE i.C_Location_ID IS NULL AND JP_Location_Label IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no + ":" + sql);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + sql );
		}

	}

	/**
	 * Reverese Look up AD_Org ID From JP_LocationOrg_Value
	 *
	 * @throws Exception
	 */
	private void reverseLookupLocationAD_Org_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		String msg = new String();
		int no = 0;

		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "JP_LocationOrg_Value");
		if (processMonitor != null)	processMonitor.statusUpdate(msg);

		//Reverese Look up AD_Org ID From JP_LocationOrg_Value
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "AD_Org_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "JP_LocationOrg_Value") ;
		//Update JP_LocationOrg_ID from JP_LocationOrg_Value
		sql = new StringBuilder ("UPDATE I_WarehouseJP i ")
				.append("SET JP_LocationOrg_ID=(SELECT AD_Org_ID FROM AD_org p")
				.append(" WHERE i.JP_LocationOrg_Value=p.Value AND (p.AD_Client_ID=i.AD_Client_ID or p.AD_Client_ID=0) ) ")
				.append(" WHERE i.JP_LocationOrg_ID IS NULL AND i.JP_LocationOrg_Value IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no + ":" + sql);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + sql );
		}

		//Invalid JP_LocationOrg_Value
		msg = Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_LocationOrg_Value");
		sql = new StringBuilder ("UPDATE I_WarehouseJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append(" WHERE JP_LocationOrg_ID = 0 AND JP_LocationOrg_Value IS NOT NULL AND JP_LocationOrg_Value <> '0' ")
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
	 * Set Warehouse Acct
	 *
	 * @param wh
	 * @param impWarehouse
	 */
	private void setMWarehouseAcct(MWarehouse wh, X_I_WarehouseJP impWarehouse)
	{
		int C_ValidCombination_ID = JPiereValidCombinationUtil.searchCreateValidCombination (getCtx(), impWarehouse.getC_AcctSchema_ID(), impWarehouse.getJP_W_Differences_Value(), get_TrxName());
		if(C_ValidCombination_ID == -1)
			return ;

		impWarehouse.setW_Differences_Acct(C_ValidCombination_ID);

		String WhereClause = " C_AcctSchema_ID=" +impWarehouse.getC_AcctSchema_ID() + " AND M_Warehouse_ID=" + wh.getM_Warehouse_ID() + " AND AD_Client_ID=" +Env.getAD_Client_ID(Env.getCtx());

		StringBuilder sql = new StringBuilder ("SELECT * FROM M_Warehouse_Acct WHERE " + WhereClause);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
			rs = pstmt.executeQuery();

			if (rs.next())
			{
				X_M_Warehouse_Acct acct = new X_M_Warehouse_Acct (getCtx (), rs, get_TrxName());
				ModelValidationEngine.get().fireImportValidate(this, impWarehouse, acct, ImportValidator.TIMING_BEFORE_IMPORT);

				if(acct.getW_Differences_Acct() != C_ValidCombination_ID)
				{
					acct.setW_Differences_Acct(C_ValidCombination_ID);
					String msg = Msg.getMsg(getCtx(), "Update") + ": " + Msg.getElement(getCtx(), "W_Differences_Acct");

					if(Util.isEmpty(impWarehouse.getI_ErrorMsg()))
					{
						impWarehouse.setI_ErrorMsg(msg);
					}else {
						impWarehouse.setI_ErrorMsg(impWarehouse.getI_ErrorMsg()+ " / " + msg);
					}
				}

				ModelValidationEngine.get().fireImportValidate(this, impWarehouse, acct, ImportValidator.TIMING_AFTER_IMPORT);

				acct.saveEx(get_TrxName());
				commitEx();
			}

		}catch (Exception e){

			log.log(Level.SEVERE, sql.toString(), e);

		}finally{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}
	}

	/**
	 * Create Warehouse
	 *
	 * @param impWarehouse
	 * @param newWarehouse
	 * @return
	 */
	private boolean createNewWarehouse(X_I_WarehouseJP impWarehouse, MWarehouse newWarehouse)
	{
		//Check AD_Org_ID
		if(impWarehouse.getAD_Org_ID() <= 0)
		{
			impWarehouse.setI_ErrorMsg(Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_Org_Value"));
			impWarehouse.setI_IsImported(false);
			impWarehouse.setProcessed(false);
			impWarehouse.saveEx(get_TrxName());
			return false;
		}

		//Check Mandatory - Value
		if(Util.isEmpty(impWarehouse.getValue()))
		{
			Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "Value")};
			impWarehouse.setI_ErrorMsg(Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs));
			impWarehouse.setI_IsImported(false);
			impWarehouse.setProcessed(false);
			impWarehouse.saveEx(get_TrxName());
			return false;
		}

		//Check Mandatory - Name
		if(Util.isEmpty(impWarehouse.getName()))
		{
			Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "Name")};
			impWarehouse.setI_ErrorMsg(Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs));
			impWarehouse.setI_IsImported(false);
			impWarehouse.setProcessed(false);
			impWarehouse.saveEx(get_TrxName());
			return false;
		}

		ModelValidationEngine.get().fireImportValidate(this, impWarehouse, newWarehouse, ImportValidator.TIMING_BEFORE_IMPORT);

		newWarehouse.setAD_Org_ID(impWarehouse.getAD_Org_ID());
		newWarehouse.setValue(impWarehouse.getValue());
		newWarehouse.setName(impWarehouse.getName());
		if(!Util.isEmpty(impWarehouse.getDescription()))
			newWarehouse.setDescription(impWarehouse.getDescription());
		newWarehouse.setIsInTransit(impWarehouse.isInTransit());
		newWarehouse.setIsDisallowNegativeInv(impWarehouse.isDisallowNegativeInv());
		if(!Util.isEmpty(impWarehouse.getSeparator()))
			newWarehouse.setSeparator(impWarehouse.getSeparator());
		if(!Util.isEmpty(impWarehouse.getReplenishmentClass()))
			newWarehouse.setReplenishmentClass(impWarehouse.getReplenishmentClass());

		//Location
		if(impWarehouse.getC_Location_ID() > 0)
		{
			newWarehouse.setC_Location_ID(impWarehouse.getC_Location_ID());

		}else {
			int C_Location_ID = JPiereLocationUtil.createLocation(
					getCtx()
					,impWarehouse.getJP_LocationOrg_ID()
					,impWarehouse.getJP_Location_Label()
					,impWarehouse.getComments()
					,impWarehouse.getCountryCode()
					,impWarehouse.getPostal()
					,impWarehouse.getPostal_Add()
					,impWarehouse.getRegionName()
					,impWarehouse.getCity()
					,impWarehouse.getAddress1()
					,impWarehouse.getAddress2()
					,impWarehouse.getAddress3()
					,impWarehouse.getAddress4()
					,impWarehouse.getAddress5()
					,get_TrxName() );
			newWarehouse.setC_Location_ID(C_Location_ID);
			impWarehouse.setC_Location_ID(C_Location_ID);
		}

		newWarehouse.setIsActive(impWarehouse.isI_IsActiveJP());

		ModelValidationEngine.get().fireImportValidate(this, impWarehouse, newWarehouse, ImportValidator.TIMING_AFTER_IMPORT);

		try {
			newWarehouse.saveEx(get_TrxName());
		}catch (Exception e) {
			impWarehouse.setI_ErrorMsg(Msg.getMsg(getCtx(),"SaveIgnored") + Msg.getElement(getCtx(), "M_Warehouse_ID") +" : " + e.toString());
			impWarehouse.setI_IsImported(false);
			impWarehouse.setProcessed(false);
			impWarehouse.saveEx(get_TrxName());
			return false;
		}

		impWarehouse.setM_Warehouse_ID(newWarehouse.getM_Warehouse_ID());

		//Account Info
		if(!Util.isEmpty(impWarehouse.getJP_W_Differences_Value()) && impWarehouse.getC_AcctSchema_ID() > 0)
		{

			setMWarehouseAcct(newWarehouse, impWarehouse);
		}


		if(Util.isEmpty(impWarehouse.getI_ErrorMsg()))
		{
			impWarehouse.setI_ErrorMsg(Msg.getMsg(getCtx(), "NewRecord"));
		}else {
			impWarehouse.setI_ErrorMsg(Msg.getMsg(getCtx(), "NewRecord")+ " / " +impWarehouse.getI_ErrorMsg());
		}

		impWarehouse.setI_IsImported(true);
		impWarehouse.setProcessed(true);
		impWarehouse.saveEx(get_TrxName());
		return true;
	}

	/**
	 * Update Warehouse
	 *
	 * @param impWarehouse
	 * @param updateWarehouse
	 * @return
	 */
	private boolean updateWarehouse(X_I_WarehouseJP impWarehouse, MWarehouse updateWarehouse)
	{
		//Check Mandatory - Value
		if(Util.isEmpty(impWarehouse.getValue()))
		{
			Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "Value")};
			impWarehouse.setI_ErrorMsg(Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs));
			impWarehouse.setI_IsImported(false);
			impWarehouse.setProcessed(false);
			impWarehouse.saveEx(get_TrxName());
			return false;
		}

		ModelValidationEngine.get().fireImportValidate(this, impWarehouse, updateWarehouse, ImportValidator.TIMING_BEFORE_IMPORT);

		if(!Util.isEmpty(impWarehouse.getName()))
			updateWarehouse.setName(impWarehouse.getName());
		if(!Util.isEmpty(impWarehouse.getDescription()))
			updateWarehouse.setDescription(impWarehouse.getDescription());
		updateWarehouse.setIsInTransit(impWarehouse.isInTransit());
		updateWarehouse.setIsDisallowNegativeInv(impWarehouse.isDisallowNegativeInv());
		if(!Util.isEmpty(impWarehouse.getSeparator()))
			updateWarehouse.setSeparator(impWarehouse.getSeparator());
		if(!Util.isEmpty(impWarehouse.getReplenishmentClass()))
			updateWarehouse.setReplenishmentClass(impWarehouse.getReplenishmentClass());

		//Location
		if(impWarehouse.getC_Location_ID() > 0)
		{
			updateWarehouse.setC_Location_ID(impWarehouse.getC_Location_ID());
		}else {
			;//Noting to do;
		}

		updateWarehouse.setIsActive(impWarehouse.isI_IsActiveJP());

		ModelValidationEngine.get().fireImportValidate(this, impWarehouse, updateWarehouse, ImportValidator.TIMING_AFTER_IMPORT);

		try {
			updateWarehouse.saveEx(get_TrxName());
		}catch (Exception e) {
			impWarehouse.setI_ErrorMsg(Msg.getMsg(getCtx(),"SaveError") + Msg.getElement(getCtx(), "M_Warehouse_ID")+" :  " + e.toString());
			impWarehouse.setI_IsImported(false);
			impWarehouse.setProcessed(false);
			impWarehouse.saveEx(get_TrxName());
			return false;
		}
		//Account Info
		if(!Util.isEmpty(impWarehouse.getJP_W_Differences_Value()) && impWarehouse.getC_AcctSchema_ID() > 0)
		{

			setMWarehouseAcct(updateWarehouse, impWarehouse);
		}

		if(Util.isEmpty(impWarehouse.getI_ErrorMsg()))
		{
			impWarehouse.setI_ErrorMsg(Msg.getMsg(getCtx(), "Update"));
		}else {
			impWarehouse.setI_ErrorMsg(Msg.getMsg(getCtx(), "Update")+ " / " +impWarehouse.getI_ErrorMsg());
		}

		impWarehouse.setI_IsImported(true);
		impWarehouse.setProcessed(true);
		impWarehouse.saveEx(get_TrxName());
		return true;
	}

}	//	Import Warehouse
