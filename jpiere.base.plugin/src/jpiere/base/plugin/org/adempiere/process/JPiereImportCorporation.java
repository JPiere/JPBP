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
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.PO;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.MCorporation;
import jpiere.base.plugin.org.adempiere.model.X_I_CorporationJP;

/**
 * 	JPIERE-0093: Import Corporation
 *
 *  @author Hideaki Hagiwara
 *  @version $Id: ImportCorporation.java,v 1.0 2015/05/06 $
 *
 */
public class JPiereImportCorporation extends SvrProcess implements ImportProcess
{

	private boolean p_deleteOldImported = false;

	/**	Only validate, don't import		*/
	private boolean			p_IsValidateOnly = false;

	/**	Client to be imported to		*/
	private int				p_AD_Client_ID = 0;
	
	private IProcessUI processMonitor = null;
	
	private String message = null;
	
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
		
		p_AD_Client_ID = getAD_Client_ID();
		
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


		/**	Delete Old Imported */
		if (p_deleteOldImported)
		{
			sql = new StringBuilder ("DELETE FROM I_CorporationJP ")
				  .append("WHERE I_IsImported='Y'").append (clientCheck);
			no = DB.executeUpdate(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("Delete Old Impored =" + no);
		}

		/** Reset Message */
		sql = new StringBuilder ("UPDATE I_CorporationJP ")
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
		//Set JP_Corporation_ID
		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "JP_Corporation_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupJP_Corporation_ID())
			commitEx();
		else
			return message;

		//Set AD_Org_ID
		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "AD_Org_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupAD_Org_ID())
			commitEx();
		else
			return message;

		//Set C_BPartner_ID
		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_BPartner_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupC_BPartner_ID())
			commitEx();
		else
			return message;

		//Set JP_CorpType_ID
		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "JP_CorpType_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupJP_CorpType_ID())
			commitEx();
		else
			return message;
		
		//Set JP_CM_CorpType_ID
		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "JP_CM_CorpType_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupJP_CM_CorpType_ID())
			commitEx();
		else
			return message;
		
		//Set C_Greeting_ID
		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_Greeting_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupC_Greeting_ID())
			commitEx();
		else
			return message;
		
		//Set JP_IndustryType_ID
		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "JP_IndustryType_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupJP_IndustryType_ID())
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
		sql = new StringBuilder ("SELECT * FROM I_CorporationJP WHERE (I_IsImported='N' OR Processed='N') ")
					.append(clientCheck);
		PreparedStatement pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
		ResultSet rs = pstmt.executeQuery();
		while (rs.next())
		{
			X_I_CorporationJP imp = new X_I_CorporationJP (getCtx (), rs, get_TrxName());

			boolean isNew = true;
			if(imp.getJP_Corporation_ID()!=0){
				isNew =false;
			}

			if(isNew)
			{
				if(imp.getName()!=null && !imp.getName().isEmpty())
				{
					MCorporation newCorp = new MCorporation(getCtx (), 0, get_TrxName());
					PO.copyValues(imp, newCorp);
					newCorp.setValue(imp.getValue());
					newCorp.setName(imp.getName());
					newCorp.setName2(imp.getName2());
					newCorp.setDescription(imp.getDescription());
					newCorp.setDUNS(imp.getDUNS());
					newCorp.setC_BPartner_ID(imp.getC_BPartner_ID());

					newCorp.saveEx();
					imp.setJP_Corporation_ID(newCorp.getJP_Corporation_ID());
					imp.setI_ErrorMsg("New Record");
					imp.setI_IsImported(true);
					imp.setProcessed(true);

				}else{
					
					imp.setI_ErrorMsg("No Name");
					imp.setI_IsImported(false);
					imp.setProcessed(false);
				}

			}else{//Update
				
				MCorporation updateCorp = new MCorporation(getCtx (), imp.getJP_Corporation_ID(), get_TrxName());
				if(!Util.isEmpty(imp.getName()))
					updateCorp.setName(imp.getName());
				if(!Util.isEmpty(imp.getName2()))
					updateCorp.setName2(imp.getName2());
				if(!Util.isEmpty(imp.getDescription()))
					updateCorp.setDescription(imp.getDescription());
				if(!Util.isEmpty(imp.getDUNS()))
					updateCorp.setDUNS(imp.getDUNS());
				if(imp.getC_BPartner_ID() > 0)
					updateCorp.setC_BPartner_ID(imp.getC_BPartner_ID());
				if(imp.getC_Greeting_ID() > 0)
					updateCorp.setC_Greeting_ID(imp.getC_Greeting_ID());
				if(imp.getJP_CorpType_ID() > 0)
					updateCorp.setJP_CorpType_ID(imp.getJP_CorpType_ID());
				if(imp.getJP_CM_CorpType_ID() > 0)
					updateCorp.setJP_CM_CorpType_ID(imp.getJP_CM_CorpType_ID());
				if(imp.getJP_IndustryType_ID() > 0)
					updateCorp.setJP_IndustryType_ID(imp.getJP_IndustryType_ID());
				if(!Util.isEmpty(imp.getURL()))
					updateCorp.setURL(imp.getURL());
				updateCorp.setJP_Capital(imp.getJP_Capital());
				
				updateCorp.saveEx();
				imp.setI_ErrorMsg("Update Record");
				imp.setI_IsImported(true);
				imp.setProcessed(true);
			}
			imp.saveEx();
		}

		return "";
	}	//	doIt

	@Override
	public String getImportTableName() 
	{
		return X_I_CorporationJP.Table_Name;
	}

	@Override
	public String getWhereClause() 
	{
		StringBuilder msgreturn = new StringBuilder(" AND AD_Client_ID=").append(p_AD_Client_ID);
		return msgreturn.toString();
	}
	
	/**
	 * Reverse look up JP_Corporation_ID From Value
	 *
	 * @throws Exception
	 */
	private boolean reverseLookupJP_Corporation_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder ("UPDATE I_CorporationJP i ")
				.append("SET JP_Corporation_ID=(SELECT JP_Corporation_ID FROM JP_Corporation p")
				.append(" WHERE i.Value=p.Value AND p.AD_Client_ID=i.AD_Client_ID) ")
				.append(" WHERE i.JP_Corporation_ID is Null AND i.Value IS NOT NULL")
				.append(" AND I_IsImported='N'").append(getWhereClause());	
		
		try 
		{
			DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		return true;

	}//reverseLookupJP_Corporation_ID
	
	/**
	 * Reverse Look up Organization From JP_Org_Value
	 *
	 **/
	private boolean reverseLookupAD_Org_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_CorporationJP i ")
				.append("SET AD_Org_ID=(SELECT AD_Org_ID FROM AD_org p")
				.append(" WHERE i.JP_Org_Value=p.Value AND (p.AD_Client_ID=i.AD_Client_ID or p.AD_Client_ID=0) AND p.IsSummary='N') ")
				.append(" WHERE i.JP_Org_Value IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());

		try
		{
			DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Invalid JP_Org_Value
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "JP_Org_Value");
		sql = new StringBuilder ("UPDATE I_CorporationJP ")
			.append("SET I_ErrorMsg='" + message + "'")
			.append(" WHERE AD_Org_ID = 0 AND JP_Org_Value IS NOT NULL AND JP_Org_Value <> '0' ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());

		try
		{
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception( message + " : " + e.toString() + " : " + sql);
		}

		if(no > 0)
		{
			return false;
		}

		return true;

	}//reverseLookupAD_Org_ID
	
	/**
	 * Reverse Look up Business Partner From BPValue
	 *
	 **/
	private boolean reverseLookupC_BPartner_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_CorporationJP i ")
				.append("SET C_BPartner_ID=(SELECT C_BPartner_ID FROM C_BPartner p")
				.append(" WHERE i.BPValue=p.Value AND p.AD_Client_ID=i.AD_Client_ID) ")
				.append(" WHERE i.C_BPartner_ID is Null AND i.BPValue IS NOT NULL")
				.append(" AND I_IsImported='N'").append(getWhereClause());
		

		try
		{
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Invalid BPValue
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "BPValue");
		sql = new StringBuilder ("UPDATE I_CorporationJP ")
			.append("SET I_ErrorMsg='" + message + "'")
			.append(" WHERE C_BPartner_ID IS NULL AND BPValue IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());

		try
		{
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception( message + " : " + e.toString() + " : " + sql);
		}

		if(no > 0)
		{
			return false;
		}

		return true;

	}//reverseLookupC_BPartner_ID JP_CorpType_ID

	/**
	 * Reverse Look up Corporation Type From JP_CorpType_Value
	 *
	 **/
	private boolean reverseLookupJP_CorpType_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_CorporationJP i ")
				.append("SET JP_CorpType_ID=(SELECT JP_CorpType_ID FROM JP_CorpType p")
				.append(" WHERE i.JP_CorpType_Value=p.Value AND (p.AD_Client_ID=i.AD_Client_ID AND p.AD_Client_ID=0 ) ) ")
				.append(" WHERE i.JP_CorpType_ID is Null AND i.JP_CorpType_Value IS NOT NULL")
				.append(" AND I_IsImported='N'").append(getWhereClause());
		
		try
		{
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Invalid JP_CorpType_Value
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "JP_CorpType_Value");
		sql = new StringBuilder ("UPDATE I_CorporationJP ")
			.append("SET I_ErrorMsg='" + message + "'")
			.append(" WHERE JP_CorpType_ID IS NULL AND JP_CorpType_Value IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());

		try
		{
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception( message + " : " + e.toString() + " : " + sql);
		}

		if(no > 0)
		{
			return false;
		}

		return true;

	}//reverseLookupJP_CorpType_ID 
	
	/**
	 * Reverse Look up Consolidated Corporation Type From JP_CM_CorpType_Value
	 *
	 **/
	private boolean reverseLookupJP_CM_CorpType_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_CorporationJP i ")
				.append("SET JP_CM_CorpType_ID=(SELECT JP_CM_CorpType_ID FROM JP_CM_CorpType p")
				.append(" WHERE i.JP_CM_CorpType_Value=p.Value AND p.AD_Client_ID=0 ) ")
				.append(" WHERE i.JP_CM_CorpType_ID is Null AND i.JP_CM_CorpType_Value IS NOT NULL")
				.append(" AND I_IsImported='N'").append(getWhereClause());
		
		try
		{
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Invalid JP_CM_CorpType_Value
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "JP_CM_CorpType_Value");
		sql = new StringBuilder ("UPDATE I_CorporationJP ")
			.append("SET I_ErrorMsg='" + message + "'")
			.append(" WHERE JP_CM_CorpType_ID IS NULL AND JP_CM_CorpType_Value IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());

		try
		{
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception( message + " : " + e.toString() + " : " + sql);
		}

		if(no > 0)
		{
			return false;
		}

		return true;

	}//reverseLookupJP_CM_CorpType_ID 
	
	/**
	 * Reverse Look up C_Greeting_ID From JP_Greeting_Name
	 *
	 **/
	private boolean reverseLookupC_Greeting_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_CorporationJP i ")
				.append("SET C_Greeting_ID=(SELECT C_Greeting_ID FROM C_Greeting p")
				.append(" WHERE i.JP_Greeting_Name=p.Name AND p.AD_Client_ID=i.AD_Client_ID) ")
				.append(" WHERE i.C_Greeting_ID is Null AND i.JP_Greeting_Name IS NOT NULL")
				.append(" AND I_IsImported='N'").append(getWhereClause());
		
		try
		{
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Invalid JP_Greeting_Name
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "JP_Greeting_Name");
		sql = new StringBuilder ("UPDATE I_CorporationJP ")
			.append("SET I_ErrorMsg='" + message + "'")
			.append(" WHERE C_Greeting_ID IS NULL AND JP_Greeting_Name IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());

		try
		{
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception( message + " : " + e.toString() + " : " + sql);
		}

		if(no > 0)
		{
			return false;
		}

		return true;

	}//reverseLookupC_Greeting_ID
	
	/**
	 * Reverse Look up JP_IndustryType_ID From JP_IndustryType_Value
	 *
	 **/
	private boolean reverseLookupJP_IndustryType_ID() throws Exception
	{
		int no = 0;

		StringBuilder sql = new StringBuilder ("UPDATE I_CorporationJP i ")
				.append("SET JP_IndustryType_ID=(SELECT JP_IndustryType_ID FROM JP_IndustryType p")
				.append(" WHERE i.JP_IndustryType_Value=p.Value AND (p.AD_Client_ID=i.AD_Client_ID AND p.AD_Client_ID=0) ) ")
				.append(" WHERE i.JP_IndustryType_ID is Null AND i.JP_IndustryType_Value IS NOT NULL")
				.append(" AND I_IsImported='N'").append(getWhereClause());
		
		try
		{
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + message +" : " + e.toString() +" : " + sql );
		}

		//Invalid JP_IndustryType_Value
		message = Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "JP_IndustryType_Value");
		sql = new StringBuilder ("UPDATE I_CorporationJP ")
			.append("SET I_ErrorMsg='" + message + "'")
			.append(" WHERE JP_IndustryType_ID IS NULL AND JP_IndustryType_Value IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());

		try
		{
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception( message + " : " + e.toString() + " : " + sql);
		}

		if(no > 0)
		{
			return false;
		}

		return true;

	}//reverseLookupC_Greeting_ID 
	
}	//	Import Corporation
