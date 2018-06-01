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

import org.compiere.model.MWarehouse;
import org.compiere.model.X_M_Warehouse_Acct;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
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
public class JPiereImportWarehouse extends SvrProcess
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
			sql = new StringBuilder ("DELETE I_WarehouseJP ")
				  .append("WHERE I_IsImported='Y'").append (clientCheck);
			no = DB.executeUpdate(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("Delete Old Impored =" + no);
		}


		//Update AD_Org ID From JP_Org_Value
		sql = new StringBuilder ("UPDATE I_WarehouseJP i ")
				.append("SET AD_Org_ID=(SELECT AD_Org_ID FROM AD_org p")
				.append(" WHERE i.JP_Org_Value=p.Value AND (p.AD_Client_ID=i.AD_Client_ID or p.AD_Client_ID=0) ) ")
				.append(" WHERE i.AD_Org_ID = '0' AND i.JP_Org_Value IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(clientCheck);
		no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Found Organization=" + no);


		//Update M_Warehouse_ID From Value
		sql = new StringBuilder ("UPDATE I_WarehouseJP i ")
				.append("SET M_Warehouse_ID=(SELECT M_Warehouse_ID FROM M_Warehouse p")
				.append(" WHERE i.Value=p.Value AND p.AD_Client_ID=i.AD_Client_ID) ")
				.append(" WHERE i.M_Warehouse_ID IS NULL AND i.Value IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(clientCheck);
		no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Found Warehouse=" + no);


		//Update  C_AcctSchema_ID From JP_AcctSchema_Name
		sql = new StringBuilder ("UPDATE I_WarehouseJP i ")
				.append("SET C_AcctSchema_ID=(SELECT C_AcctSchema_ID FROM C_AcctSchema p")
				.append(" WHERE i.JP_AcctSchema_Name=p.Name AND p.AD_Client_ID=i.AD_Client_ID) ")
				.append(" WHERE i.C_AcctSchema_ID IS NULL AND JP_AcctSchema_Name IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(clientCheck);
		no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Found Acct Schema=" + no);


		//Update C_Location_ID From JP_Location_Label
		sql = new StringBuilder ("UPDATE I_WarehouseJP i ")
				.append("SET C_Location_ID=(SELECT C_Location_ID FROM C_Location p")
				.append(" WHERE i.JP_Location_Label= p.JP_Location_Label AND p.AD_Client_ID=i.AD_Client_ID) ")
				.append(" WHERE i.C_Location_ID IS NULL AND JP_Location_Label IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(clientCheck);
		no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Found Location=" + no);


		//Update JP_LocationOrg_ID from JP_LocationOrg_Value
		sql = new StringBuilder ("UPDATE I_WarehouseJP i ")
				.append("SET JP_LocationOrg_ID=(SELECT AD_Org_ID FROM AD_org p")
				.append(" WHERE i.JP_LocationOrg_Value=p.Value AND (p.AD_Client_ID=i.AD_Client_ID or p.AD_Client_ID=0) ) ")
				.append(" WHERE i.JP_LocationOrg_ID IS NULL AND i.JP_LocationOrg_Value IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(clientCheck);
		no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Found Organization=" + no);

		commitEx();


		//
		sql = new StringBuilder ("SELECT * FROM I_WarehouseJP WHERE I_IsImported='N'")
					.append(clientCheck);
		PreparedStatement pstmt = null;
		ResultSet rs = null;

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
					if(!Util.isEmpty(imp.getName()) && imp.getAD_Org_ID() != 0)
					{
						MWarehouse newWarehouse = new MWarehouse(getCtx (), 0, get_TrxName());
						newWarehouse.setAD_Org_ID(imp.getAD_Org_ID());
						newWarehouse.setValue(imp.getValue());
						newWarehouse.setName(imp.getName());
						newWarehouse.setDescription(imp.getDescription());
						newWarehouse.setIsInTransit(imp.isInTransit());
						newWarehouse.setIsDisallowNegativeInv(imp.isDisallowNegativeInv());
						if(!Util.isEmpty(imp.getSeparator()))
							newWarehouse.setSeparator(imp.getSeparator());
						if(!Util.isEmpty(imp.getReplenishmentClass()))
							newWarehouse.setReplenishmentClass(imp.getReplenishmentClass());

						//Location
						if(imp.getC_Location_ID() > 0)
						{
							newWarehouse.setC_Location_ID(imp.getC_Location_ID());

						}else {
							int C_Location_ID = JPiereLocationUtil.createLocation(
									getCtx()
									,imp.getJP_LocationOrg_ID()
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
							newWarehouse.setC_Location_ID(C_Location_ID);
						}

						newWarehouse.saveEx(get_TrxName());

						//Account Info
						if(!Util.isEmpty(imp.getJP_W_Differences_Value()) && imp.getC_AcctSchema_ID() > 0)
						{

							setMWarehouseAcct(newWarehouse, imp);
						}

						imp.setI_ErrorMsg("New Record");
						imp.setI_IsImported(true);
						imp.setProcessed(true);
						imp.setM_Warehouse_ID(newWarehouse.getM_Warehouse_ID());

					}else{
						if(Util.isEmpty(imp.getName()))
							imp.setI_ErrorMsg("No Name");
						else if(imp.getAD_Org_ID() == 0)
							imp.setI_ErrorMsg("Organization Value 0");

						imp.setI_IsImported(false);
						imp.setProcessed(false);
					}

				}else{//Update

					MWarehouse updateWarehouse = new MWarehouse(getCtx (), imp.getAD_Org_ID(), get_TrxName());
					if(!Util.isEmpty(imp.getName()))
						updateWarehouse.setName(imp.getName());
					if(!Util.isEmpty(imp.getDescription()))
						updateWarehouse.setDescription(imp.getDescription());
					updateWarehouse.setIsInTransit(imp.isInTransit());
					updateWarehouse.setIsDisallowNegativeInv(imp.isDisallowNegativeInv());
					if(!Util.isEmpty(imp.getSeparator()))
						updateWarehouse.setSeparator(imp.getSeparator());
					if(!Util.isEmpty(imp.getReplenishmentClass()))
						updateWarehouse.setReplenishmentClass(imp.getReplenishmentClass());

					//Location
					if(imp.getC_Location_ID() > 0)
					{
						updateWarehouse.setC_Location_ID(imp.getC_Location_ID());
					}else {
						;//Noting to do;
					}

					updateWarehouse.saveEx(get_TrxName());

					//Account Info
					if(!Util.isEmpty(imp.getJP_W_Differences_Value()) && imp.getC_AcctSchema_ID() > 0)
					{

						setMWarehouseAcct(updateWarehouse, imp);
					}

					imp.setI_ErrorMsg("Update Record");
					imp.setI_IsImported(true);
					imp.setProcessed(true);

				}

				imp.saveEx(get_TrxName());

			}//while (rs.next())

		}catch (Exception e){
			log.log(Level.SEVERE, sql.toString(), e);
		}finally{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

		return "";
	}	//	doIt


	private void setMWarehouseAcct(MWarehouse wh, X_I_WarehouseJP imp)
	{
		int C_ValidCombination_ID = JPiereValidCombinationUtil.searchCreateValidCombination (getCtx(), imp.getC_AcctSchema_ID(), imp.getJP_W_Differences_Value(), get_TrxName());
		if(C_ValidCombination_ID == -1)
			return ;

		imp.setW_Differences_Acct(C_ValidCombination_ID);

		String WhereClause = " C_AcctSchema_ID=" +imp.getC_AcctSchema_ID() + " AND M_Warehouse_ID=" + wh.getM_Warehouse_ID() + " AND AD_Client_ID=" +Env.getAD_Client_ID(Env.getCtx());

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
				acct.setW_Differences_Acct(C_ValidCombination_ID);
				acct.saveEx(get_TrxName());
			}

		}catch (Exception e){

			log.log(Level.SEVERE, sql.toString(), e);

		}finally{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}
	}

}	//	ImportPayment
