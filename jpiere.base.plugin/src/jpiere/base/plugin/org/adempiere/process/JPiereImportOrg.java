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
import org.compiere.model.MOrg;
import org.compiere.model.MOrgInfo;
import org.compiere.model.ModelValidationEngine;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.X_I_OrgJP;
import jpiere.base.plugin.util.JPiereLocationUtil;

/**
 * 	JPIERE-0053:Import Organization
 *
 *  @author Hideaki Hagiwara
 *  @version $Id: ImportOrg.java,v 1.0 2015/01/02 $
 *
 */
public class JPiereImportOrg extends SvrProcess implements ImportProcess
{

	/**	Client to be imported to		*/
	private int				m_AD_Client_ID = 0;

	private boolean p_deleteOldImported = false;

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
		StringBuilder sql = null;
		int no = 0;
		StringBuilder clientCheck = new StringBuilder(" AND AD_Client_ID=").append(getAD_Client_ID());


		//Delete Old Imported data
		if (p_deleteOldImported)
		{
			sql = new StringBuilder ("DELETE I_OrgJP ")
				  .append("WHERE I_IsImported='Y'").append (clientCheck);
			no = DB.executeUpdate(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("Delete Old Impored =" + no);
		}

		ModelValidationEngine.get().fireImportValidate(this, null, null, ImportValidator.TIMING_BEFORE_VALIDATE);

		//Reverse Lookup Surrogate Key
		reverseLookupAD_Org_ID();
		reverseLookupAD_OrgType_ID();
		reverseLookupC_Location_ID();

		ModelValidationEngine.get().fireImportValidate(this, null, null, ImportValidator.TIMING_AFTER_VALIDATE);

		commitEx();


		sql = new StringBuilder ("SELECT * FROM I_OrgJP WHERE I_IsImported='N'")
					.append(clientCheck);
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try
		{
			pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
			rs = pstmt.executeQuery();
			while (rs.next())
			{
				X_I_OrgJP imp = new X_I_OrgJP (getCtx (), rs, get_TrxName());

				boolean isNew = true;
				if(imp.getAD_Org_ID()!=0){
					isNew =false;
				}

				if(isNew)//Create
				{
					//Check Mandatory
					if(Util.isEmpty(imp.getValue()))
					{
						Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "Value")};
						imp.setI_ErrorMsg(Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs));
						imp.setI_IsImported(false);
						imp.setProcessed(false);
						imp.saveEx(get_TrxName());
						commitEx();
						continue;
					}

					if(Util.isEmpty(imp.getName()))
					{
						Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "Name")};
						imp.setI_ErrorMsg(Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs));
						imp.setI_IsImported(false);
						imp.setProcessed(false);
						imp.saveEx(get_TrxName());
						commitEx();
						continue;
					}

					//New Record
					MOrg newOrg = new MOrg(getCtx (), 0, get_TrxName());
					ModelValidationEngine.get().fireImportValidate(this, imp, newOrg, ImportValidator.TIMING_BEFORE_IMPORT);

					newOrg.setValue(imp.getValue());
					newOrg.setName(imp.getName());
					newOrg.setDescription(imp.getDescription());
					newOrg.setIsActive(imp.isI_IsActiveJP());

					ModelValidationEngine.get().fireImportValidate(this, imp, newOrg, ImportValidator.TIMING_AFTER_IMPORT);

					newOrg.setIsSummary(imp.isSummary());
					newOrg.saveEx(get_TrxName());
					commitEx();

					imp.setAD_Org_ID(newOrg.getAD_Org_ID());
					imp.setI_ErrorMsg(Msg.getMsg(getCtx(), "NewRecord"));
					imp.setI_IsImported(true);
					imp.setProcessed(true);


				}else{//Update

					//Check Mandatory
					if(Util.isEmpty(imp.getValue()))
					{
						Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "Value")};
						imp.setI_ErrorMsg(Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs));
						imp.setI_IsImported(false);
						imp.setProcessed(false);
						imp.saveEx(get_TrxName());
						commitEx();
						continue;
					}

					MOrg updateOrg = new MOrg(getCtx (), imp.getAD_Org_ID(), get_TrxName());
					ModelValidationEngine.get().fireImportValidate(this, imp, updateOrg, ImportValidator.TIMING_BEFORE_IMPORT);

					updateOrg.setName(imp.getName());
					updateOrg.setDescription(imp.getDescription());
					updateOrg.setIsActive(imp.isI_IsActiveJP());

					ModelValidationEngine.get().fireImportValidate(this, imp, updateOrg, ImportValidator.TIMING_AFTER_IMPORT);

					updateOrg.saveEx(get_TrxName());
					commitEx();

					imp.setI_ErrorMsg(Msg.getMsg(getCtx(), "Update"));
					imp.setI_IsImported(true);
					imp.setProcessed(true);

				}

				imp.saveEx();
				commitEx();

				if(imp.getAD_Org_ID() > 0)
				{
					MOrgInfo orgInfo = MOrgInfo.get(getCtx(), imp.getAD_Org_ID(), get_TrxName());
					ModelValidationEngine.get().fireImportValidate(this, imp, orgInfo, ImportValidator.TIMING_BEFORE_IMPORT);

					orgInfo.setAD_OrgType_ID(imp.getAD_OrgType_ID());
					if(!Util.isEmpty(imp.getDUNS()))
						orgInfo.setDUNS(imp.getDUNS());
					if(!Util.isEmpty(imp.getTaxID()))
						orgInfo.setTaxID(imp.getTaxID());
					if(!Util.isEmpty(imp.getPhone()))
						orgInfo.setPhone(imp.getPhone());
					if(!Util.isEmpty(imp.getPhone2()))
						orgInfo.setPhone2(imp.getPhone2());
					if(!Util.isEmpty(imp.getFax()))
						orgInfo.setFax(imp.getFax());
					if(!Util.isEmpty(imp.getEMail()))
						orgInfo.setEMail(imp.getEMail());

					//Org Location
					int C_Location_ID = imp.getC_Location_ID();
					if(C_Location_ID > 0)
					{
						orgInfo.setC_Location_ID(C_Location_ID);

					}else if(!Util.isEmpty(imp.getJP_Location_Label())){

						C_Location_ID = JPiereLocationUtil.createLocation(
								getCtx()
								,"0"
								,imp.getJP_Location_Label()
								,imp.getComments()
								,imp.getCountryCode()
								,imp.getPostal()
								,imp.getPostal_Add()
								,imp.getRegionName()
								,imp.getCity()
								,imp.getAddress1()
								,imp.getAddress2()
								,imp.getAddress3()
								,imp.getAddress4()
								,imp.getAddress5()
								,get_TrxName() );

						orgInfo.setC_Location_ID(C_Location_ID);

					}

					ModelValidationEngine.get().fireImportValidate(this, imp, orgInfo, ImportValidator.TIMING_AFTER_IMPORT);

					orgInfo.saveEx(get_TrxName());
					commitEx();

				}
			}//while (rs.next())

		}catch (Exception e){
			log.log(Level.SEVERE, sql.toString(), e);
		}finally{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

		return Msg.getMsg(getCtx(), "Success");
	}	//	doIt

	@Override
	public String getImportTableName() {
		return X_I_OrgJP.Table_Name;
	}


	@Override
	public String getWhereClause() {
		StringBuilder msgreturn = new StringBuilder(" AND AD_Client_ID=").append(m_AD_Client_ID);
		return msgreturn.toString();
	}

	/**
	 * Reverse Look up AD_Org ID From Value
	 *
	 * @throws Exception
	 */
	private void reverseLookupAD_Org_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		String msg = new String();
		int no = 0;

		//Reverse Look up AD_Org ID From Value
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "AD_Org_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "Value") ;
		sql = new StringBuilder ("UPDATE I_OrgJP i ")
				.append("SET AD_Org_ID=(SELECT AD_Org_ID FROM AD_org p")
				.append(" WHERE i.Value=p.Value AND p.AD_Client_ID=i.AD_Client_ID) ")
				.append(" WHERE AD_Org_ID = '0' AND Value IS NOT NULL")
				.append(" AND I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no + ":" + sql);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + sql );
		}

		//New Record : Set AD_Org_ID = 0
		msg = Msg.getMsg(getCtx(), "NewRecord");
		sql = new StringBuilder ("UPDATE I_OrgJP ")
			.append("SET AD_Org_ID=0")
			.append(" WHERE AD_Org_ID IS NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no + ":" + sql);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		//Error : Search Key is null
		msg = Msg.getMsg(getCtx(), "JP_Null")+Msg.getElement(getCtx(), "Value");
		sql = new StringBuilder ("UPDATE I_OrgJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append(" WHERE Value IS NULL AND AD_Org_ID = 0 ")
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
	 * Reverse Look up AD_OrgType_ID From JP_OrgType_Name
	 *
	 * @throws Exception
	 */
	private void reverseLookupAD_OrgType_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		String msg = new String();
		int no = 0;

		//Reverse Look up AD_OrgType_ID From JP_OrgType_Name
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "AD_OrgType_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "JP_OrgType_Name") ;
		sql = new StringBuilder ("UPDATE I_OrgJP i ")
				.append(" SET AD_OrgType_ID=(SELECT t.AD_OrgType_ID FROM AD_OrgType t")
				.append(" WHERE t.Name=i.JP_OrgType_Name AND t.AD_Client_ID=i.AD_Client_ID) ")
				.append(" WHERE i.JP_OrgType_Name IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no + ":" + sql);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + sql );
		}

		//Invalid JP_OrgType_Name
		msg = Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_OrgType_Name");
		sql = new StringBuilder ("UPDATE I_OrgJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append(" WHERE JP_OrgType_Name IS NOT NULL AND AD_OrgType_ID IS NULL ")
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

		//Reverse Loog up C_Location_ID From JP_Location_Label
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_Location_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "JP_Location_Label") ;
		sql = new StringBuilder ("UPDATE I_OrgJP i ")
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

}	//	ImportPayment
