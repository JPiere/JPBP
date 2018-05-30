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

import org.compiere.model.MOrg;
import org.compiere.model.MOrgInfo;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
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
public class JPiereImportOrg extends SvrProcess
{

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


		//	Existing Oraganization ? Match Value
		sql = new StringBuilder ("UPDATE I_OrgJP i ")
				.append("SET AD_Org_ID=(SELECT AD_Org_ID FROM AD_org p")
				.append(" WHERE i.Value=p.Value AND p.AD_Client_ID=i.AD_Client_ID) ")
				.append(" WHERE AD_Org_ID = '0' AND Value IS NOT NULL")
				.append(" AND I_IsImported='N'").append(clientCheck);
		no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Found Organization=" + no);


		//	Existing Oraganization Type
		sql = new StringBuilder ("UPDATE I_OrgJP i ")
				.append(" SET AD_OrgType_ID=(SELECT t.AD_OrgType_ID FROM AD_OrgType t")
				.append(" WHERE t.Name=i.JP_OrgType_Name AND t.AD_Client_ID=i.AD_Client_ID) ")
				.append(" WHERE i.JP_OrgType_Name IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(clientCheck);
		no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Found Organization=" + no);

		commitEx();


		//
		sql = new StringBuilder ("SELECT * FROM I_OrgJP WHERE I_IsImported='N'")
					.append(clientCheck);
		PreparedStatement pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
		ResultSet rs = pstmt.executeQuery();
		while (rs.next())
		{
			X_I_OrgJP imp = new X_I_OrgJP (getCtx (), rs, get_TrxName());

			boolean isNew = true;
			if(imp.getAD_Org_ID()!=0){
				isNew =false;
			}

			if(isNew){
				if(imp.getName()!=null && !imp.getName().isEmpty()){
					MOrg newOrg = new MOrg(getCtx (), 0, get_TrxName());
					newOrg.setValue(imp.getValue());
					newOrg.setName(imp.getName());
					newOrg.setDescription(imp.getDescription());
					newOrg.setIsSummary(imp.isSummary());
					newOrg.saveEx(get_TrxName());
					imp.setI_ErrorMsg("New Record");
					imp.setI_IsImported(true);
					imp.setProcessed(true);
					imp.setAD_Org_ID(newOrg.getAD_Org_ID());

				}else{
					imp.setI_ErrorMsg("No Name");
					imp.setI_IsImported(false);
					imp.setProcessed(false);
				}

			}else{//Update
				MOrg updateOrg = new MOrg(getCtx (), imp.getAD_Org_ID(), get_TrxName());
				updateOrg.setName(imp.getName());
				updateOrg.setDescription(imp.getDescription());
				updateOrg.saveEx(get_TrxName());

				imp.setI_ErrorMsg("Update Record");
				imp.setI_IsImported(true);
				imp.setProcessed(true);

			}

			imp.saveEx();
			if(imp.getAD_Org_ID() > 0)
			{
				MOrgInfo orgInfo = MOrgInfo.get(getCtx(), imp.getAD_Org_ID(), get_TrxName());
				orgInfo.setAD_OrgType_ID(imp.getAD_OrgType_ID());
				if(!Util.isEmpty(imp.getDUNS()))
					orgInfo.setDUNS(imp.getDUNS());
				if(!Util.isEmpty(imp.getTaxID()))
					orgInfo.setDUNS(imp.getTaxID());
				if(!Util.isEmpty(imp.getPhone()))
					orgInfo.setDUNS(imp.getPhone());
				if(!Util.isEmpty(imp.getPhone2()))
					orgInfo.setDUNS(imp.getPhone2());
				if(!Util.isEmpty(imp.getFax()))
					orgInfo.setDUNS(imp.getFax());
				if(!Util.isEmpty(imp.getEMail()))
					orgInfo.setDUNS(imp.getEMail());

				//Org Location
				int C_Location_ID = 0;
				if(!Util.isEmpty(imp.getJP_Location_Label()))
				{
					C_Location_ID = JPiereLocationUtil.searchLocationByLabel(getCtx(), imp.getJP_Location_Label(), get_TrxName());
					if(C_Location_ID > 0)
					{
						orgInfo.setC_Location_ID(C_Location_ID);

					}else if(C_Location_ID == 0) {

						C_Location_ID = JPiereLocationUtil.createLocation(
								getCtx()
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

					}else {

						imp.setI_ErrorMsg(Msg.getMsg(getCtx(), "JP_UnexpectedError"));
						imp.setI_IsImported(false);
						imp.setProcessed(false);
					}

				}else {

					if(!Util.isEmpty(imp.getCountryCode()) || !Util.isEmpty(imp.getPostal())  || !Util.isEmpty(imp.getPostal_Add())
							|| !Util.isEmpty(imp.getRegionName())  || !Util.isEmpty(imp.getCity())
							|| !Util.isEmpty(imp.getAddress1()) || !Util.isEmpty(imp.getAddress2()) || !Util.isEmpty(imp.getAddress3())
							|| !Util.isEmpty(imp.getAddress4()) || !Util.isEmpty(imp.getAddress5()))
					{
						C_Location_ID = JPiereLocationUtil.createLocation(
								getCtx()
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

				}

				orgInfo.saveEx(get_TrxName());

			}
		}


		return "";
	}	//	doIt

}	//	ImportPayment
