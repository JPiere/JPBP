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

import org.compiere.model.I_M_LocatorType;
import org.compiere.model.MClient;
import org.compiere.model.MLocator;
import org.compiere.model.MLocatorType;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.util.Env;
import org.compiere.util.Msg;


/**
 * JPIERE
 * 
 * @author hhagi
 *
 */
public class JPiereLocatorModelValidator implements ModelValidator {
	
	private int AD_Client_ID = -1;
	private int AD_Org_ID = -1;
	private int AD_Role_ID = -1;
	private int AD_User_ID = -1;
	
	@Override
	public void initialize(ModelValidationEngine engine, MClient client) 
	{
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();
		engine.addModelChange(MLocator.Table_Name, this);		
	}
	
	@Override
	public int getAD_Client_ID() 
	{
		return AD_Client_ID;
	}
	
	@Override
	public String login(int AD_Org_ID, int AD_Role_ID, int AD_User_ID) 
	{
		this.AD_Org_ID = AD_Org_ID;
		this.AD_Role_ID = AD_Role_ID;
		this.AD_User_ID = AD_User_ID;

		return null;
	}
	
	@Override
	public String modelChange(PO po, int type) throws Exception 
	{
		//JPIERE-0317 Physical Warehouse
		if(type == ModelValidator.TYPE_BEFORE_NEW 
				|| (type == ModelValidator.TYPE_BEFORE_CHANGE &&
					(po.is_ValueChanged("JP_PhysicalWarehouse_ID") || po.is_ValueChanged("M_LocatorType_ID")) ) )
		{
			MLocator locator = (MLocator)po;
			int JP_PhysicalWarehouse_ID = locator.get_ValueAsInt("JP_PhysicalWarehouse_ID");
			MLocatorType  locatorType =MLocatorType.get(Env.getCtx(), locator.getM_LocatorType_ID());
			int LocType_PhyWH_ID =locatorType.get_ValueAsInt("JP_PhysicalWarehouse_ID");
			if(LocType_PhyWH_ID != 0 && LocType_PhyWH_ID != JP_PhysicalWarehouse_ID)
			{
				return Msg.getMsg(Env.getCtx(), "JP_DiffPhyWH");//Different physical warehouse between Locator and Locator Type.
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
