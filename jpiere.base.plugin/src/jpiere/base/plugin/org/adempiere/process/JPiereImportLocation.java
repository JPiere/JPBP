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

import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.X_I_LocationJP;
import jpiere.base.plugin.util.JPiereLocationUtil;

/**
 * 	JPIERE-0391:Import Location
 *
 *  @author Hideaki Hagiwara
 *
 *
 */
public class JPiereImportLocation extends SvrProcess
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
			sql = new StringBuilder ("DELETE I_LocationJP ")
				  .append("WHERE I_IsImported='Y'").append (clientCheck);
			no = DB.executeUpdate(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("Delete Old Impored =" + no);
		}


		//	Existing Oraganization ? Match Value
		sql = new StringBuilder ("UPDATE I_LocationJP i ")
				.append("SET AD_Org_ID=(SELECT AD_Org_ID FROM AD_org p")
				.append(" WHERE i.JP_Org_Value=p.Value AND p.AD_Client_ID=i.AD_Client_ID) ")
				.append(" WHERE i.AD_Org_ID = '0' AND i.JP_Org_Value IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(clientCheck);
		no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Found Organization=" + no);

		//	Set Client, Org, IsActive, Created/Updated
		sql = new StringBuilder ("UPDATE I_LocationJP ")
				.append("SET AD_Org_ID = COALESCE (AD_Org_ID, 0)")
						.append("WHERE I_IsImported<>'Y' OR I_IsImported IS NULL").append(clientCheck);
		no = DB.executeUpdateEx(sql.toString(), get_TrxName());

		commitEx();


		//
		sql = new StringBuilder ("SELECT * FROM I_LocationJP WHERE I_IsImported='N'")
					.append(clientCheck);
		PreparedStatement pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
		ResultSet rs = pstmt.executeQuery();
		while (rs.next())
		{
			X_I_LocationJP imp = new X_I_LocationJP (getCtx (), rs, get_TrxName());


			//Org Location
			int C_Location_ID = 0;
			if(!Util.isEmpty(imp.getJP_Location_Label()))
			{
				C_Location_ID = JPiereLocationUtil.searchLocationByLabel(getCtx(), imp.getJP_Location_Label(), get_TrxName());
				if(C_Location_ID > 0)
				{
					boolean isOk =JPiereLocationUtil.updateLocation(
							getCtx()
							,C_Location_ID
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

					if(!isOk)
					{
						imp.setI_ErrorMsg("Update Error");
						imp.setI_IsImported(false);
						imp.setProcessed(false);
					}else {
						imp.setI_ErrorMsg("Update Record");
						imp.setI_IsImported(true);
						imp.setProcessed(true);
					}

				}else if(C_Location_ID == 0 || C_Location_ID == -1) {

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

					imp.setI_ErrorMsg("Create Record");
					imp.setI_IsImported(true);
					imp.setProcessed(true);

				}

				imp.setC_Location_ID(C_Location_ID);

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

					imp.setC_Location_ID(C_Location_ID);
					imp.setI_ErrorMsg("Create Record");
					imp.setI_IsImported(true);
					imp.setProcessed(true);
				}

			}

			imp.saveEx(get_TrxName());

		}



		return "OK";
	}	//	doIt

}	//	ImportPayment
