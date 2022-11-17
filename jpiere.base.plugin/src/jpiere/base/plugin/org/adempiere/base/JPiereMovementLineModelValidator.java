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

import java.util.logging.Level;

import org.compiere.model.MClient;
import org.compiere.model.MLocator;
import org.compiere.model.MMovement;
import org.compiere.model.MMovementLine;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Msg;


/**
*
* JPiere Movement Line model Validator
*
* JPIERE-0227: Common Warehouse
* JPIERE-0582: Register Route of Movement
* 
* @author h.hagiwara(h.hagiwara@oss-erp.co.jp)
*
*/
public class JPiereMovementLineModelValidator implements ModelValidator {

	private static CLogger log = CLogger.getCLogger(JPiereMovementLineModelValidator.class);
	private int AD_Client_ID = -1;

	
	@Override
	public void initialize(ModelValidationEngine engine, MClient client) 
	{
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();
		engine.addModelChange(MMovementLine.Table_Name, this);

		if (log.isLoggable(Level.FINE)) log.fine("Initialize JPiereMovementLineModelValidator");

	}

	@Override
	public int getAD_Client_ID() 
	{
		return AD_Client_ID;
	}

	@Override
	public String login(int AD_Org_ID, int AD_Role_ID, int AD_User_ID)
	{
		return null;
	}
	
	//From Warehouse
	private static final String JP_WarehouseFrom_ID = "JP_WarehouseFrom_ID";
	private static final String JP_PhysicalWarehouseFrom_ID = "JP_PhysicalWarehouseFrom_ID";
	private static final String JP_PhysicalWarehouse_ID = "JP_PhysicalWarehouse_ID";
	
	//To Warehouse
	private static final String JP_WarehouseTo_ID = "JP_WarehouseTo_ID";
	private static final String JP_PhysicalWarehouseTo_ID = "JP_PhysicalWarehouseTo_ID";

	@Override
	public String modelChange(PO po, int type) throws Exception 
	{
		//JPIERE-0582
		if(type ==  ModelValidator.TYPE_BEFORE_NEW || type ==  ModelValidator.TYPE_BEFORE_CHANGE)
		{
			MMovementLine mLine = (MMovementLine)po;
			
			if(type ==  ModelValidator.TYPE_BEFORE_NEW || mLine.is_ValueChanged("M_Locator_ID"))
			{
				MMovement mm = mLine.getParent();
				
				int int_WarehouseFrom_ID = mm.get_ValueAsInt(JP_WarehouseFrom_ID);
				if(int_WarehouseFrom_ID != 0)
				{
					if(MLocator.get(mLine.getM_Locator_ID()).getM_Warehouse_ID() != int_WarehouseFrom_ID)
					{
						String msg0 = Msg.getElement(Env.getCtx(), JP_WarehouseFrom_ID);
						String msg1 = Msg.getElement(Env.getCtx(), "M_Locator_ID");
						String msg = Msg.getMsg(Env.getCtx(),"JP_Different",new Object[]{msg0,msg1});//Different between {0} and {1}
						return msg;
					}
				}
				
				int int_PhysicalWarehouseFrom_ID =  mm.get_ValueAsInt(JP_PhysicalWarehouseFrom_ID);
				if(int_PhysicalWarehouseFrom_ID != 0)
				{
					MLocator loc = MLocator.get(mLine.getM_Locator_ID());
					int int_PhysicalWarehouse_ID = loc.get_ValueAsInt(JP_PhysicalWarehouse_ID);
					if(int_PhysicalWarehouseFrom_ID != int_PhysicalWarehouse_ID)
					{				
						String msg0 = Msg.getElement(Env.getCtx(), JP_PhysicalWarehouseFrom_ID);
						String msg1 = Msg.getElement(Env.getCtx(), "M_Locator_ID");
						String msg = Msg.getMsg(Env.getCtx(),"JP_Different",new Object[]{msg0,msg1});//Different between {0} and {1}
						return msg;
					}
				}
				
			}
			
			if(type ==  ModelValidator.TYPE_BEFORE_NEW || mLine.is_ValueChanged("M_LocatorTo_ID"))
			{
				MMovement mm = mLine.getParent();
				int int_WarehouseTo_ID = mm.get_ValueAsInt(JP_WarehouseTo_ID);
				if(int_WarehouseTo_ID != 0)
				{
					if(MLocator.get(mLine.getM_LocatorTo_ID()).getM_Warehouse_ID() != int_WarehouseTo_ID)
					{
						String msg0 = Msg.getElement(Env.getCtx(), JP_WarehouseTo_ID);
						String msg1 = Msg.getElement(Env.getCtx(), "M_LocatorTo_ID");
						String msg = Msg.getMsg(Env.getCtx(),"JP_Different",new Object[]{msg0,msg1});//Different between {0} and {1}
						return msg;
					}
				}
				
				int int_PhysicalWarehouseTo_ID = mm.get_ValueAsInt(JP_PhysicalWarehouseTo_ID);
				if(int_PhysicalWarehouseTo_ID != 0)
				{
					MLocator loc = MLocator.get(mLine.getM_LocatorTo_ID());
					int int_PhysicalWarehouse_ID = loc.get_ValueAsInt(JP_PhysicalWarehouse_ID);
					if(int_PhysicalWarehouseTo_ID != int_PhysicalWarehouse_ID)
					{				
						String msg0 = Msg.getElement(Env.getCtx(), JP_PhysicalWarehouseTo_ID);
						String msg1 = Msg.getElement(Env.getCtx(), "M_LocatorTo_ID");
						String msg = Msg.getMsg(Env.getCtx(),"JP_Different",new Object[]{msg0,msg1});//Different between {0} and {1}
						return msg;
					}
				}
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
