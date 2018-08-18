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
import org.compiere.model.MCost;
import org.compiere.model.ModelValidationEngine;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;

import jpiere.base.plugin.org.adempiere.model.X_I_CostJP;

/**
 * 	JPIERE-0414:Import Product Cost
 *
 *  @author Hideaki Hagiwara
 *
 */
public class JPiereImportProductCost extends SvrProcess implements ImportProcess
{
	/**	Client to be imported to		*/
	private int	m_AD_Client_ID = 0;

	private boolean p_deleteOldImported = false;

	/**	Only validate, don't import		*/
	private boolean p_IsValidateOnly = false;

	private String message = null;

	private IProcessUI processMonitor = null;

	private long startTime = System.currentTimeMillis();

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
			sql = new StringBuilder ("DELETE I_CostJP ")
				  .append("WHERE I_IsImported='Y'").append (clientCheck);
			try {
				no = DB.executeUpdate(sql.toString(), get_TrxName());
				if (log.isLoggable(Level.FINE)) log.fine("Delete Old Impored =" + no);
			}catch (Exception e) {
				throw new Exception(Msg.getMsg(getCtx(), "Error") + sql );
			}
		}

		//Reset Message
		sql = new StringBuilder ("UPDATE I_CostJP ")
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
		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "AD_Org_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupAD_Org_ID())
			commitEx();
		else
			return message;

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "M_Product_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupM_Product_ID())
			commitEx();
		else
			return message;

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "M_CostType_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupM_CostType_ID())
			commitEx();
		else
			return message;

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "M_CostElement_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupM_CostElement_ID())
			commitEx();
		else
			return message;

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_AcctSchema_ID");
		if (processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupC_AcctSchema_ID())
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
		sql = new StringBuilder ("SELECT * FROM I_CostJP WHERE I_IsImported='N'")
					.append(clientCheck).append(" ORDER BY AD_Org_ID, C_AcctSchema_ID, M_CostType_ID, M_CostElement_ID, M_Product_ID");
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int recordsNum = 0;
		int failureUpdateNum = 0;
		String records = Msg.getMsg(getCtx(), "JP_NumberOfRecords");
		String failure = Msg.getMsg(getCtx(), "JP_Failure");

		try
		{
			pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
			rs = pstmt.executeQuery();
			MCost updateCost = null;
			X_I_CostJP importCost = null;
			while (rs.next())
			{
				recordsNum++;

				importCost = new X_I_CostJP(getCtx(),rs,get_TrxName());
				updateCost = MCost.get(getCtx(), importCost.getAD_Client_ID(), importCost.getAD_Org_ID(), importCost.getM_Product_ID()
						, importCost.getM_CostType_ID(), importCost.getC_AcctSchema_ID(), importCost.getM_CostElement_ID(), 0, get_TrxName());

				if(updateCost == null)
				{
					importCost.setI_ErrorMsg(Msg.getMsg(getCtx(), "NotFound"));
					importCost.save(get_TrxName());
					failureUpdateNum++;
					processMonitor.statusUpdate(records  + " : " + recordsNum + " / " + failure + " : " + failureUpdateNum);
					commitEx();
					continue;
				}

				if(importCost.getCurrentCostPrice() != null && importCost.getCurrentCostPrice().compareTo(Env.ZERO) != 0)
				{
					updateCost.setCurrentCostPrice(importCost.getCurrentCostPrice() );
				}

				if(importCost.getFutureCostPrice() != null && importCost.getFutureCostPrice().compareTo(Env.ZERO) != 0)
				{
					updateCost.setFutureCostPrice(importCost.getFutureCostPrice() );
				}

				if(importCost.getCurrentCostPriceLL() != null && importCost.getCurrentCostPriceLL().compareTo(Env.ZERO) != 0)
				{
					updateCost.setCurrentCostPriceLL(importCost.getCurrentCostPriceLL() );
				}

				if(importCost.getFutureCostPriceLL() != null && importCost.getFutureCostPriceLL().compareTo(Env.ZERO) != 0)
				{
					updateCost.setFutureCostPriceLL(importCost.getFutureCostPrice() );
				}

				if(importCost.getCurrentQty() != null && importCost.getCurrentQty().compareTo(Env.ZERO) != 0)
				{
					updateCost.setCurrentQty(importCost.getCurrentQty() );
				}

				updateCost.setIsCostFrozen(importCost.isCostFrozen());

				if(importCost.getCumulatedQty() != null && importCost.getCumulatedQty().compareTo(Env.ZERO) != 0)
				{
					updateCost.setCumulatedQty(importCost.getCumulatedQty() );
				}

				if(importCost.getCumulatedAmt() != null && importCost.getCumulatedAmt().compareTo(Env.ZERO) != 0)
				{
					updateCost.setCumulatedAmt(importCost.getCumulatedAmt() );
				}

				try {
					updateCost.saveEx(get_TrxName());
				}catch (Exception e) {
					importCost.setI_ErrorMsg(e.toString());
					importCost.save(get_TrxName());
					failureUpdateNum++;
				}

				importCost.setI_ErrorMsg(Msg.getMsg(getCtx(), "Update"));
				importCost.setI_IsImported(true);
				importCost.setProcessed(true);
				importCost.saveEx(get_TrxName());

				commitEx();

				if (processMonitor != null)
				{
					processMonitor.statusUpdate(records  + " : " + recordsNum + " / " + failure + " : " + failureUpdateNum);
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

		return  Msg.getMsg(getCtx(), "ProcessOK") + "  "  + timeFormatted + " ( " + records  + " : " + recordsNum + " / " + failure + " : " + failureUpdateNum + " )" ;

	}	//	doIt


	@Override
	public String getImportTableName() {
		return X_I_CostJP.Table_Name;
	}


	@Override
	public String getWhereClause() {
		StringBuilder msgreturn = new StringBuilder(" AND AD_Client_ID=").append(m_AD_Client_ID);
		return msgreturn.toString();
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
		StringBuilder sql = new StringBuilder ("UPDATE I_CostJP i ")
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
		sql = new StringBuilder ("UPDATE I_CostJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append(" WHERE AD_Org_ID IS Null ")
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
	 * Reverse Look up Product From Value and UPC , VendorProduct No
	 * @throws Exception
	 *
	 */
	private boolean reverseLookupM_Product_ID() throws Exception
	{
		int no = 0;

		//Reverse lookup M_Product_ID From Value
		StringBuilder sql = new StringBuilder ("UPDATE I_CostJP i ")
				.append("SET M_Product_ID=(SELECT M_Product_ID FROM M_Product p")
				.append(" WHERE i.JP_Product_Value=p.Value AND p.AD_Client_ID=i.AD_Client_ID) ")
				.append(" WHERE i.M_Product_ID IS NULL AND i.JP_Product_Value IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Error : Search Key is Invalid
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_Product_Value");
		sql = new StringBuilder ("UPDATE I_CostJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append(" WHERE JP_Product_Value IS NOT NULL AND M_Product_ID IS NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(message  +" : " +  e.toString() +" : " + sql );
		}

		if(no > 0)
		{
			return false;
		}

		//Error : Search Key is null
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "JP_Null")+Msg.getElement(getCtx(), "JP_Product_Value");
		sql = new StringBuilder ("UPDATE I_CostJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append(" WHERE JP_Product_Value IS NULL AND M_Product_ID IS NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(message  +" : " +  e.toString() +" : " + sql );
		}

		if(no > 0)
		{
			return false;
		}

		return true;

	}//reverseLookupM_Product_ID



	/**
	 * Reverse look Up  C_AcctSchema_ID From JP_AcctSchema_Name
	 *
	 * @throws Exception
	 */
	private boolean reverseLookupC_AcctSchema_ID()throws Exception
	{
		StringBuilder sql = new StringBuilder();
		int no = 0;

		//Reverse look Up  C_AcctSchema_ID From JP_AcctSchema_Name
		sql = new StringBuilder ("UPDATE I_CostJP i ")
				.append("SET C_AcctSchema_ID=(SELECT C_AcctSchema_ID FROM C_AcctSchema p")
				.append(" WHERE i.JP_AcctSchema_Name=p.Name AND p.AD_Client_ID=i.AD_Client_ID) ")
				.append(" WHERE i.C_AcctSchema_ID IS NULL AND JP_AcctSchema_Name IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message + " : " + e.toString() + " : " + sql );
		}

		//Invalid JP_AcctSchema_Name
		message = Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_AcctSchema_Name");
		sql = new StringBuilder ("UPDATE I_CostJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append(" WHERE C_AcctSchema_ID IS NULL AND JP_AcctSchema_Name IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message + " : " + e.toString() + " : " + sql );
		}

		if(no > 0)
		{
			return false;
		}

		//Error : JP_AcctSchema_Name is null
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "JP_Null")+Msg.getElement(getCtx(), "JP_AcctSchema_Name");
		sql = new StringBuilder ("UPDATE I_CostJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append(" WHERE JP_AcctSchema_Name IS NULL AND C_AcctSchema_ID IS NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(message  +" : " +  e.toString() +" : " + sql );
		}

		if(no > 0)
		{
			return false;
		}

		return true;

	}//reverseLookupC_AcctSchema_ID


	/**
	 * Reverse Look up M_CostType_ID From JP_CostType_Name
	 *
	 * @throws Exception
	 */
	private boolean reverseLookupM_CostType_ID()throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_CostJP i ")
				.append("SET M_CostType_ID=(SELECT M_CostType_ID FROM M_CostType p")
				.append(" WHERE i.JP_CostType_Name=p.Name AND p.AD_Client_ID=i.AD_Client_ID) ")
				.append(" WHERE i.JP_CostType_Name IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Invalid JP_CostType_Name
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_CostType_Name");
		sql = new StringBuilder ("UPDATE I_CostJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append(" WHERE JP_CostType_Name IS NOT NULL AND M_CostType_ID IS NULL ")
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

		//Error : JP_CostType_Name is null
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "JP_Null")+Msg.getElement(getCtx(), "JP_CostType_Name");
		sql = new StringBuilder ("UPDATE I_CostJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append(" WHERE JP_CostType_Name IS NULL AND M_CostType_ID IS NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(message  +" : " +  e.toString() +" : " + sql );
		}

		if(no > 0)
		{
			return false;
		}

		return true;

	}//reverseLookupM_CostType_ID

	/**
	 * Reverse Look up M_CostElement_ID From JP_CostElement_Name
	 *
	 * @throws Exception
	 */
	private boolean reverseLookupM_CostElement_ID()throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_CostJP i ")
				.append("SET M_CostElement_ID=(SELECT M_CostElement_ID FROM M_CostElement p")
				.append(" WHERE i.JP_CostElement_Name=p.Name AND p.AD_Client_ID=i.AD_Client_ID) ")
				.append(" WHERE i.JP_CostElement_Name IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Invalid JP_CostElement_Name
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_CostElement_Name");
		sql = new StringBuilder ("UPDATE I_CostJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append(" WHERE JP_CostElement_Name IS NOT NULL AND M_CostElement_ID IS NULL ")
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

		//Error : JP_CostElement_Name is null
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "JP_Null")+Msg.getElement(getCtx(), "JP_CostElement_Name");
		sql = new StringBuilder ("UPDATE I_CostJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append(" WHERE JP_CostElement_Name IS NULL AND M_CostElement_ID IS NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(message  +" : " +  e.toString() +" : " + sql );
		}

		if(no > 0)
		{
			return false;
		}

		return true;

	}//reverseLookupM_CostElement_ID


}	//	Import Product Cost
