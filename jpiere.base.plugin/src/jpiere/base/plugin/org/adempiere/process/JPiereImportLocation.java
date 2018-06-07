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

import org.compiere.model.MLocation;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Msg;

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


		//Update AD_Org_ID From JP_Org_Value
		sql = new StringBuilder ("UPDATE I_LocationJP i ")
				.append("SET AD_Org_ID=(SELECT AD_Org_ID FROM AD_org p")
				.append(" WHERE i.JP_Org_Value=p.Value AND (p.AD_Client_ID=i.AD_Client_ID or p.AD_Client_ID=0) ) ")
				.append(" WHERE i.JP_Org_Value IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(clientCheck);
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("Found Organization=" + no);

		}catch(Exception e) {

			throw new Exception(Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "JP_CouldNotUpdate")
					+ "Update AD_Org_ID From JP_Org_Value");

		}


		//Update C_Location_ID From JP_Location_Label
		sql = new StringBuilder ("UPDATE I_LocationJP i ")
				.append("SET C_Location_ID=(SELECT C_Location_ID FROM C_Location p")
				.append(" WHERE i.JP_Location_Label= p.JP_Location_Label AND p.AD_Client_ID=i.AD_Client_ID) ")
				.append(" WHERE i.C_Location_ID IS NULL AND JP_Location_Label IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(clientCheck);
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("Found Location=" + no);
		}catch(Exception e) {

			throw new Exception(Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "JP_CouldNotUpdate")
					+ "Update C_Location_ID From JP_Location_Label");
		}


		commitEx();

		//
		sql = new StringBuilder ("SELECT * FROM I_LocationJP WHERE I_IsImported='N'")
					.append(clientCheck);
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try
		{
			pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
			rs = pstmt.executeQuery();

			while (rs.next())
			{
				X_I_LocationJP imp = new X_I_LocationJP (getCtx (), rs, get_TrxName());

				boolean isNew = true;
				if(imp.getC_Location_ID()!=0){
					isNew =false;
				}

				if(isNew)
				{
					int C_Location_ID = JPiereLocationUtil.createLocation(
							getCtx()
							,imp.getAD_Org_ID()
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

					if(!imp.isI_IsActiveJP())
					{
						MLocation location = new MLocation(getCtx(),C_Location_ID,get_TrxName());
						location.setIsActive(false);
						location.saveEx(get_TrxName());
						commitEx();
					}

					imp.setC_Location_ID(C_Location_ID);
					imp.setI_ErrorMsg(Msg.getMsg(getCtx(), "NewRecord"));
					imp.setI_IsImported(true);
					imp.setProcessed(true);

				}else {

					boolean isOk =JPiereLocationUtil.updateLocation(
							getCtx()
							,imp.getC_Location_ID()
							,imp.getAD_Org_ID()
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
							,imp.isI_IsActiveJP()
							,get_TrxName() );

					if(!isOk)
					{
						imp.setI_ErrorMsg(Msg.getMsg(getCtx(), "JP_CouldNotUpdate"));
						imp.setI_IsImported(false);
						imp.setProcessed(false);
					}else {
						imp.setI_ErrorMsg(Msg.getMsg(getCtx(), "Update"));
						imp.setI_IsImported(true);
						imp.setProcessed(true);
					}
				}

				imp.saveEx(get_TrxName());
				commitEx();

			}//while (rs.next())


		}catch (Exception e){
			log.log(Level.SEVERE, sql.toString(), e);
		}finally{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

		return "OK";
	}	//	doIt

}	//	ImportPayment
