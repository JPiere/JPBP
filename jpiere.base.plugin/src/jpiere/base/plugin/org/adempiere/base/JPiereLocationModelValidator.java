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
package jpiere.base.plugin.org.adempiere.base;

import org.compiere.model.MClient;
import org.compiere.model.MLocation;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import jpiere.base.plugin.util.JPiereLocationUtil;

/**
 *
 * JPIERE-0392: Location Model Validator
 *
 * @author hhagi
 *
 */
public class JPiereLocationModelValidator implements ModelValidator {

	private static CLogger log = CLogger.getCLogger(JPiereLocationModelValidator.class);
	private int AD_Client_ID = -1;
	private int AD_Org_ID = -1;
	private int AD_Role_ID = -1;
	private int AD_User_ID = -1;

	@Override
	public void initialize(ModelValidationEngine engine, MClient client) {
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();
		engine.addModelChange(MLocation.Table_Name, this);
	}

	@Override
	public int getAD_Client_ID() {
		return AD_Client_ID;
	}

	@Override
	public String login(int AD_Org_ID, int AD_Role_ID, int AD_User_ID) {
		this.AD_Org_ID = AD_Org_ID;
		this.AD_Role_ID = AD_Role_ID;
		this.AD_User_ID = AD_User_ID;

		return null;
	}

	@Override
	public String modelChange(PO po, int type) throws Exception {

		//JPIERE-0392
		if(type == ModelValidator.TYPE_BEFORE_NEW)
		{
			MLocation location = (MLocation)po;
			String JP_Location_Label = location.get_ValueAsString("JP_Location_Label");
			if(Util.isEmpty(JP_Location_Label))
			{
				if(!Util.isEmpty(location.getAddress1()) && !Util.isEmpty(location.getAddress2()))
				{
					JP_Location_Label = location.getAddress1() + location.getAddress2();

				}else if(!Util.isEmpty(location.getAddress1())){

					JP_Location_Label = location.getAddress1();

				}else{

					JP_Location_Label = location.get_TrxName();
				}


				location.set_ValueNoCheck("JP_Location_Label", JP_Location_Label);
			}

			int C_Location_ID = JPiereLocationUtil.searchLocationByLabel(location.getCtx(), JP_Location_Label, location.get_TrxName());
			if(C_Location_ID == 0)
			{
				;//Noting to do;
			}else if(C_Location_ID > 0) {

				int i = 1;
				do {
					i++;
				}while(JPiereLocationUtil.searchLocationByLabel(location.getCtx(), JP_Location_Label+"("+ i +")", location.get_TrxName()) > 0 );

				JP_Location_Label = JP_Location_Label+"("+ i +")";

				location.set_ValueNoCheck("JP_Location_Label", JP_Location_Label);
				if(JP_Location_Label.length() > MLocation.getFieldLength("JP_Location_Label"))
				{
					return Msg.getMsg(location.getCtx(), "JP_Unique_Constraint_Label");
				}

			}else {
				return Msg.getMsg(location.getCtx(), "JP_UnexpectedError");
			}
		}

		//JPIERE-0392
		if(type == ModelValidator.TYPE_AFTER_NEW)
		{

			MLocation location = (MLocation)po;
			String JP_Location_Label = location.get_ValueAsString("JP_Location_Label");
			if(JP_Location_Label.equals(location.get_TrxName()))
			{
				int C_Location_ID = location.getC_Location_ID();
				int returnValue = JPiereLocationUtil.searchLocationByLabel(location.getCtx(), String.valueOf(C_Location_ID), location.get_TrxName());
				if(returnValue == 0)
				{
					JP_Location_Label = String.valueOf(C_Location_ID);
				}else if(returnValue == 1) {
					JP_Location_Label = String.valueOf(C_Location_ID) + "(2)";
				}else {
					return Msg.getMsg(location.getCtx(), "JP_UnexpectedError");
				}

				String sql = "UPDATE C_Location l"
					+ " SET JP_Location_Label='"+JP_Location_Label+"'"
					+ " WHERE C_Location_ID = ?";
				int no = DB.executeUpdate(sql, new Object[]{Integer.valueOf(C_Location_ID)}, false, location.get_TrxName(), 0);
				if (no != 1)
					log.warning("(1) #" + no);

			}


		}

		return null;
	}

	@Override
	public String docValidate(PO po, int timing)
	{
		return null;
	}



}
