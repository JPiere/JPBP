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
import org.compiere.model.MCharge;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.X_C_Charge_Acct;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.X_I_ChargeJP;
import jpiere.base.plugin.org.adempiere.model.X_I_WarehouseJP;
import jpiere.base.plugin.util.JPiereValidCombinationUtil;

/**
 * 	JPIERE-0403:Import Charge
 *
 *  @author Hideaki Hagiwara
 *
 */
public class JPiereImportCharge extends SvrProcess  implements ImportProcess
{
	/**	Client to be imported to		*/
	private int		 m_AD_Client_ID = 0;

	private boolean p_deleteOldImported = false;

	/**	Only validate, don't import		*/
	private boolean p_IsValidateOnly = false;

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
			else if (name.equals("IsValidateOnly"))
				p_IsValidateOnly = para[i].getParameterAsBoolean();
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
			sql = new StringBuilder ("DELETE FROM I_ChargeJP ")
				  .append("WHERE I_IsImported='Y'").append (clientCheck);
			no = DB.executeUpdate(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("Delete Old Impored =" + no);
		}

		//Reset Message
		sql = new StringBuilder ("UPDATE I_ChargeJP ")
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
		reverseLookupC_Charge_ID();
		reverseLookupAD_Org_ID();
		reverseLookupC_ChargeType_ID();
		reverseLookupC_TaxCategory_ID();
		reverseLookupC_BPartner_ID();
		reverseLookupC_AcctSchema_ID();

		ModelValidationEngine.get().fireImportValidate(this, null, null, ImportValidator.TIMING_AFTER_VALIDATE);

		commitEx();
		if (p_IsValidateOnly)
		{
			return "Validated";
		}

		//
		sql = new StringBuilder ("SELECT * FROM I_ChargeJP WHERE I_IsImported='N'")
					.append(clientCheck).append(" ORDER BY Name ");
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
			String preName = "";
			MCharge charge = null;

			while (rs.next())
			{
				X_I_ChargeJP imp = new X_I_ChargeJP (getCtx (), rs, get_TrxName());

				boolean isNew = true;
				if(imp.getC_Charge_ID()!=0){
					isNew =false;
					charge = new MCharge(getCtx (), imp.getC_Charge_ID(), get_TrxName());
				}else{

					if(preName.equals(imp.getName()))
					{
						isNew = false;

					}else {

						preName = imp.getName();

					}

				}

				if(isNew)//Create
				{
					charge = new MCharge(getCtx (), 0, get_TrxName());
					if(createNewCharge(imp, charge))
						successNewNum++;
					else
						failureNewNum++;

				}else{//Update


					if(updateCharge(imp, charge))
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
	 * Reverese Look up  C_Charge_ID From Name
	 *
	 * @throws Exception
	 */
	private void reverseLookupC_Charge_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		String msg = new String();
		int no = 0;

		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_Charge_ID");
		if (processMonitor != null)	processMonitor.statusUpdate(msg);

		//Reverese Look up  C_Charge_ID From Name
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_Charge_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "Name") ;
		sql = new StringBuilder ("UPDATE I_ChargeJP i ")
				.append("SET C_Charge_ID=(SELECT C_Charge_ID FROM C_Charge p")
				.append(" WHERE i.Name=p.Name AND p.AD_Client_ID=i.AD_Client_ID) ")
				.append(" WHERE i.C_Charge_ID IS NULL AND i.Name IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + e.toString() + sql );
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
		sql = new StringBuilder ("UPDATE I_ChargeJP i ")
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
		sql = new StringBuilder ("UPDATE I_ChargeJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append(" WHERE AD_Org_ID = 0 AND JP_Org_Value IS NOT NULL AND JP_Org_Value <> '0' ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no );
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
	 * Reverse lookup C_ChargeType_ID From JP_Charge_Value
	 *
	 * @throws Exception
	 */
	private void reverseLookupC_ChargeType_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		String msg = new String();
		int no = 0;

		 msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_ChargeType_ID");
		if (processMonitor != null)	processMonitor.statusUpdate(msg);

		//Reverse lookup C_ChargeType_ID From JP_Charge_Value
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_ChargeType_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "JP_ChargeType_Value") ;
		sql = new StringBuilder ("UPDATE I_ChargeJP i ")
			.append("SET C_ChargeType_ID=(SELECT C_ChargeType_ID FROM C_ChargeType p")
			.append(" WHERE i.JP_ChargeType_Value=p.Value AND i.AD_Client_ID=p.AD_Client_ID) ")
			.append("WHERE i.C_ChargeType_ID IS NULL AND i.JP_ChargeType_Value IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine( msg + "=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		//Invalid JP_ChargeType_Value
		msg = Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_ChargeType_Value");
		sql = new StringBuilder ("UPDATE I_ChargeJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append("WHERE C_ChargeType_ID IS NULL AND JP_ChargeType_Value IS NOT NULL")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		if(no > 0)
		{
			commitEx();
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg + " : " + no );
		}

	}//reverseLookupC_ChargeType_ID

	/**
	 * Reverse lookup C_TaxCategory_ID From JP_TaxCategory_Name
	 *
	 * @throws Exception
	 */
	private void reverseLookupC_TaxCategory_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		String msg = new String();
		int no = 0;

		 msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_TaxCategory_ID");
		if (processMonitor != null)	processMonitor.statusUpdate(msg);

		//Reverse lookup C_TaxCategory_ID From JP_TaxCategory_Name
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_TaxCategory_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "JP_TaxCategory_Name") ;
		sql = new StringBuilder ("UPDATE I_ChargeJP i ")
			.append("SET C_TaxCategory_ID=(SELECT C_TaxCategory_ID FROM C_TaxCategory p")
			.append(" WHERE i.JP_TaxCategory_Name=p.Name AND i.AD_Client_ID=p.AD_Client_ID) ")
			.append("WHERE i.C_TaxCategory_ID IS NULL AND i.JP_TaxCategory_Name IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine( msg + "=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		//Invalid JP_TaxCategory_Name
		msg = Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_TaxCategory_Name");
		sql = new StringBuilder ("UPDATE I_ChargeJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append("WHERE C_TaxCategory_ID IS NULL AND JP_TaxCategory_Name IS NOT NULL")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		if(no > 0)
		{
			commitEx();
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg + " : " + no );
		}

	}//reverseLookupC_TaxCategory_ID


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
		sql = new StringBuilder ("UPDATE I_ChargeJP i ")
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
		sql = new StringBuilder ("UPDATE I_ChargeJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append("WHERE C_BPartner_ID IS NULL AND JP_BPartner_Value IS NOT NULL")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		if(no > 0)
		{
			commitEx();
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg + " : " + no );
		}

	}//reverseLookupC_BPartner_ID


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
		sql = new StringBuilder ("UPDATE I_ChargeJP i ")
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
		sql = new StringBuilder ("UPDATE I_ChargeJP ")
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
	 * Set Charge Acct
	 *
	 * @param charge
	 * @param impCharge
	 */
	private void setChargeAcct(MCharge charge, X_I_ChargeJP impCharge)
	{
		int C_ValidCombination_ID = JPiereValidCombinationUtil.searchCreateValidCombination (getCtx(), impCharge.getC_AcctSchema_ID(), impCharge.getJP_Ch_Expense_Acct_Value(), get_TrxName());
		if(C_ValidCombination_ID == -1)
			return ;

		impCharge.setCh_Expense_Acct(C_ValidCombination_ID);

		String WhereClause = " C_AcctSchema_ID=" +impCharge.getC_AcctSchema_ID() + " AND C_Charge_ID=" + charge.getC_Charge_ID() + " AND AD_Client_ID=" +Env.getAD_Client_ID(Env.getCtx());

		StringBuilder sql = new StringBuilder ("SELECT * FROM C_Charge_Acct WHERE " + WhereClause);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
			rs = pstmt.executeQuery();

			if (rs.next())
			{
				X_C_Charge_Acct acct = new X_C_Charge_Acct (getCtx (), rs, get_TrxName());
				ModelValidationEngine.get().fireImportValidate(this, impCharge, acct, ImportValidator.TIMING_BEFORE_IMPORT);

				if(acct.getCh_Expense_Acct() != C_ValidCombination_ID)
				{
					acct.setCh_Expense_Acct(C_ValidCombination_ID);
					String msg = Msg.getMsg(getCtx(), "Update") + ": " + Msg.getElement(getCtx(), "Ch_Expense_Acct");

					if(Util.isEmpty(impCharge.getI_ErrorMsg()))
					{
						impCharge.setI_ErrorMsg(msg);
					}else {
						impCharge.setI_ErrorMsg(impCharge.getI_ErrorMsg()+ " / " + msg);
					}
				}

				ModelValidationEngine.get().fireImportValidate(this, impCharge, acct, ImportValidator.TIMING_AFTER_IMPORT);

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
	 * @param impCharge
	 * @param newCharge
	 * @return
	 */
	private boolean createNewCharge(X_I_ChargeJP impCharge, MCharge newCharge)
	{
		//Check AD_Org_ID
		if(impCharge.getAD_Org_ID() < 0)
		{
			impCharge.setI_ErrorMsg(Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_Org_Value"));
			impCharge.setI_IsImported(false);
			impCharge.setProcessed(false);
			impCharge.saveEx(get_TrxName());
			return false;
		}

		//Check Mandatory - Name
		if(Util.isEmpty(impCharge.getName()))
		{
			Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "Name")};
			impCharge.setI_ErrorMsg(Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs));
			impCharge.setI_IsImported(false);
			impCharge.setProcessed(false);
			impCharge.saveEx(get_TrxName());
			return false;
		}

		//Check Mandatory - C_TaxCategory_ID
		if(impCharge.getC_TaxCategory_ID()==0)
		{
			Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "C_TaxCategory_ID")};
			impCharge.setI_ErrorMsg(Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs));
			impCharge.setI_IsImported(false);
			impCharge.setProcessed(false);
			impCharge.saveEx(get_TrxName());
			return false;
		}

		ModelValidationEngine.get().fireImportValidate(this, impCharge, newCharge, ImportValidator.TIMING_BEFORE_IMPORT);

		newCharge.setAD_Org_ID(impCharge.getAD_Org_ID());
		newCharge.setName(impCharge.getName());
		if(!Util.isEmpty(impCharge.getDescription()))
			newCharge.setDescription(impCharge.getDescription());
		newCharge.setIsSameTax(impCharge.isSameTax());
		newCharge.setIsTaxIncluded(impCharge.isTaxIncluded());
		if(impCharge.getC_ChargeType_ID() > 0)
			newCharge.setC_ChargeType_ID(impCharge.getC_ChargeType_ID());
		if(impCharge.getC_TaxCategory_ID() > 0)
			newCharge.setC_TaxCategory_ID(impCharge.getC_TaxCategory_ID());
		if(impCharge.getC_BPartner_ID() > 0)
			newCharge.setC_BPartner_ID(impCharge.getC_BPartner_ID());
		if(impCharge.getChargeAmt() != null)
			newCharge.setChargeAmt(impCharge.getChargeAmt());

		newCharge.setIsActive(impCharge.isI_IsActiveJP());

		ModelValidationEngine.get().fireImportValidate(this, impCharge, newCharge, ImportValidator.TIMING_AFTER_IMPORT);

		try {
			newCharge.saveEx(get_TrxName());
		}catch (Exception e) {
			impCharge.setI_ErrorMsg(Msg.getMsg(getCtx(),"SaveIgnored") + Msg.getElement(getCtx(), "C_Charge_ID") +" : " + e.toString());
			impCharge.setI_IsImported(false);
			impCharge.setProcessed(false);
			impCharge.saveEx(get_TrxName());
			return false;
		}

		impCharge.setC_Charge_ID(newCharge.getC_Charge_ID());

		//Account Info
		if(!Util.isEmpty(impCharge.getJP_Ch_Expense_Acct_Value()) && impCharge.getC_AcctSchema_ID() > 0)
		{

			setChargeAcct(newCharge, impCharge);
		}


		if(Util.isEmpty(impCharge.getI_ErrorMsg()))
		{
			impCharge.setI_ErrorMsg(Msg.getMsg(getCtx(), "NewRecord"));
		}else {
			impCharge.setI_ErrorMsg(Msg.getMsg(getCtx(), "NewRecord")+ " / " +impCharge.getI_ErrorMsg());
		}

		impCharge.setI_IsImported(true);
		impCharge.setProcessed(true);
		impCharge.saveEx(get_TrxName());
		return true;
	}

	/**
	 * Update Warehouse
	 *
	 * @param impCharge
	 * @param updateCharge
	 * @return
	 */
	private boolean updateCharge(X_I_ChargeJP impCharge, MCharge updateCharge)
	{
		//Check Mandatory - Value
		if(Util.isEmpty(impCharge.getName()))
		{
			Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "Name")};
			impCharge.setI_ErrorMsg(Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs));
			impCharge.setI_IsImported(false);
			impCharge.setProcessed(false);
			impCharge.saveEx(get_TrxName());
			return false;
		}

		ModelValidationEngine.get().fireImportValidate(this, impCharge, updateCharge, ImportValidator.TIMING_BEFORE_IMPORT);

		if(!Util.isEmpty(impCharge.getDescription()))
			updateCharge.setDescription(impCharge.getDescription());
		updateCharge.setIsSameTax(impCharge.isSameTax());
		updateCharge.setIsTaxIncluded(impCharge.isTaxIncluded());

		if(impCharge.getC_ChargeType_ID() > 0)
			updateCharge.setC_ChargeType_ID(impCharge.getC_ChargeType_ID());
		if(impCharge.getC_TaxCategory_ID() > 0)
			updateCharge.setC_TaxCategory_ID(impCharge.getC_TaxCategory_ID());
		if(impCharge.getC_BPartner_ID() > 0)
			updateCharge.setC_BPartner_ID(impCharge.getC_BPartner_ID());
		if(impCharge.getChargeAmt() != null)
			updateCharge.setChargeAmt(impCharge.getChargeAmt());

		updateCharge.setIsActive(impCharge.isI_IsActiveJP());

		ModelValidationEngine.get().fireImportValidate(this, impCharge, updateCharge, ImportValidator.TIMING_AFTER_IMPORT);

		try {
			updateCharge.saveEx(get_TrxName());
		}catch (Exception e) {
			impCharge.setI_ErrorMsg(Msg.getMsg(getCtx(),"SaveError") + Msg.getElement(getCtx(), "C_Charge_ID")+" :  " + e.toString());
			impCharge.setI_IsImported(false);
			impCharge.setProcessed(false);
			impCharge.saveEx(get_TrxName());
			return false;
		}
		//Account Info
		if(!Util.isEmpty(impCharge.getJP_Ch_Expense_Acct_Value()) && impCharge.getC_AcctSchema_ID() > 0)
		{

			setChargeAcct(updateCharge, impCharge);
		}

		if(Util.isEmpty(impCharge.getI_ErrorMsg()))
		{
			impCharge.setI_ErrorMsg(Msg.getMsg(getCtx(), "Update"));
		}else {
			impCharge.setI_ErrorMsg(Msg.getMsg(getCtx(), "Update")+ " / " +impCharge.getI_ErrorMsg());
		}

		impCharge.setI_IsImported(true);
		impCharge.setProcessed(true);
		impCharge.saveEx(get_TrxName());
		return true;
	}

}	//	Import Warehouse
