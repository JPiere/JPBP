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
package jpiere.base.plugin.org.adempiere.callout;

import java.sql.Timestamp;
import java.util.Properties;

import org.compiere.model.CalloutEngine;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.MMovement;
import org.compiere.util.Msg;


/**
*
* JPiere Movement Callout
*
* JPIERE-0227: Common Warehouse
* JPIERE-0582: Register Route of Movement
* 
* @author h.hagiwara(h.hagiwara@oss-erp.co.jp)
*
*/
public class JPiereMovementCallout  extends CalloutEngine  {
	
	private static final String IsRecordRouteJP = "IsRecordRouteJP";
	
	//From Warehouse
	private static final String JP_WarehouseFrom_ID = "JP_WarehouseFrom_ID";
	private static final String JP_PhysicalWarehouseFrom_ID = "JP_PhysicalWarehouseFrom_ID";
	
	//To Warehouse
	private static final String JP_WarehouseTo_ID = "JP_WarehouseTo_ID";
	private static final String JP_PhysicalWarehouseTo_ID = "JP_PhysicalWarehouseTo_ID";
	
	//Next Warehouse
	private static final String JP_WarehouseNext_ID = "JP_WarehouseNext_ID";
	private static final String JP_PhysicalWarehouseNext_ID = "JP_PhysicalWarehouseNext_ID";
	@SuppressWarnings("unused")
	private static final String JP_MovementDateNext = "JP_MovementDateNext";
	
	//Departure Warehouse
	private static final String JP_WarehouseDep_ID = "JP_WarehouseDep_ID";
	private static final String JP_PhysicalWarehouseDep_ID = "JP_PhysicalWarehouseDep_ID";
	private static final String JP_MovementDateDep = "JP_MovementDateDep";
	
	//Destination warehouse
	private static final String JP_WarehouseDst_ID = "JP_WarehouseDst_ID";
	private static final String JP_PhysicalWarehouseDst_ID = "JP_PhysicalWarehouseDst_ID";
	private static final String JP_MovementDateDst = "JP_MovementDateDst";
	
	private static final String JP_MovementPre_ID = "JP_MovementPre_ID";

	
	public String setMovementDate(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{
		if(!mTab.getValueAsBoolean(IsRecordRouteJP))
			return null;
		
		Object obj_MovementPre_ID = mTab.getValue(JP_MovementPre_ID);
		if(obj_MovementPre_ID == null)
		{
			mTab.setValue(JP_MovementDateDep, value);
		}
		
		return null;
	}
	
	public String setIsRecordRoute(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{
		if(!mTab.getValueAsBoolean(IsRecordRouteJP))
		{
			//Set Departure Warehouse
			//mTab.setValue(JP_WarehouseDep_ID, null);
			//mTab.setValue(JP_PhysicalWarehouseDep_ID, null);
			
			//Set Destination Warehouse
			//mTab.setValue(JP_WarehouseDst_ID, null);
			//mTab.setValue(JP_PhysicalWarehouseDst_ID, null);
			
		}else {
			
			//Set Departure Warehouse
			Object obj_MovementPre_ID = mTab.getValue(JP_MovementPre_ID);
			if(obj_MovementPre_ID == null)
			{
				Object  obj_WarehouseDep_ID = mTab.getValue(JP_WarehouseDep_ID);
				if(obj_WarehouseDep_ID == null)
				{
					mTab.setValue(JP_WarehouseDep_ID, mTab.getValue(JP_WarehouseFrom_ID));
				}
				
				Object  obj_PhysicalWarehouseDep_ID = mTab.getValue(JP_PhysicalWarehouseDep_ID);
				if(obj_PhysicalWarehouseDep_ID == null)
				{
					mTab.setValue(JP_PhysicalWarehouseDep_ID, mTab.getValue(JP_PhysicalWarehouseFrom_ID));
				}
				
				mTab.setValue(JP_MovementDateDep, mTab.getValue(MMovement.COLUMNNAME_MovementDate));
			}
		}
		
		
		return null;
	}
	
	public String setJP_WarehouseFrom_ID(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{
		if(!mTab.getValueAsBoolean(IsRecordRouteJP))
			return null;
		
		Object obj_MovementPre_ID = mTab.getValue(JP_MovementPre_ID);
		if(obj_MovementPre_ID == null)
		{
			mTab.setValue(JP_WarehouseDep_ID, value);
			mTab.setValue(JP_MovementDateDep, mTab.getValue(MMovement.COLUMNNAME_MovementDate));
		}
		
		return null;
	}

	public String setJP_PhysicalWarehouseFrom_ID(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{
		if(!mTab.getValueAsBoolean(IsRecordRouteJP))
			return null;
		
		Object obj_MovementPre_ID = mTab.getValue(JP_MovementPre_ID);
		if(obj_MovementPre_ID == null)
		{
			mTab.setValue(JP_PhysicalWarehouseDep_ID, value);
			mTab.setValue(JP_MovementDateDep, mTab.getValue(MMovement.COLUMNNAME_MovementDate));
		}
		
		return null;
	}

	public String setJP_WarehouseTo_ID(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{
		if(!mTab.getValueAsBoolean(IsRecordRouteJP))
			return null;
		
		
		Object  obj_WarehouseNext_ID = mTab.getValue(JP_WarehouseNext_ID);
		Object  obj_WarehouseDst_ID = mTab.getValue(JP_WarehouseDst_ID);
		if(obj_WarehouseNext_ID == null && obj_WarehouseDst_ID == null)
		{
			mTab.setValue(JP_WarehouseDst_ID, value);
		}
		
		return null;
	}

	public String setJP_PhysicalWarehouseTo_ID(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{
		if(!mTab.getValueAsBoolean(IsRecordRouteJP))
			return null;
		
		
		Object  obj_PhysicalWarehouseNext_ID = mTab.getValue(JP_PhysicalWarehouseNext_ID);
		Object  obj_PhysicalWarehouseDst_ID = mTab.getValue(JP_PhysicalWarehouseDst_ID);
		if(obj_PhysicalWarehouseNext_ID == null && obj_PhysicalWarehouseDst_ID == null)
		{
			mTab.setValue(JP_PhysicalWarehouseDst_ID, value);
		}
		
		return null;
	}
	
	public String setJP_WarehouseNext_ID(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{
		if(!mTab.getValueAsBoolean(IsRecordRouteJP))
			return null;
		
		Object  obj_WarehouseDst_ID = mTab.getValue(JP_WarehouseDst_ID);
		if(obj_WarehouseDst_ID == null)
		{
			mTab.setValue(JP_WarehouseDst_ID, value);
			
		}else {
			
			Object  obj_WarehouseTo_ID = mTab.getValue(JP_WarehouseTo_ID);
			if(obj_WarehouseTo_ID == null)
			{
				;//Nothing to do;
			}else {
				
				int int_WarehouseTo_ID = ((Integer)obj_WarehouseTo_ID).intValue();
				int int_WarehouseDst_ID = ((Integer)obj_WarehouseDst_ID).intValue();;

				if(int_WarehouseTo_ID == int_WarehouseDst_ID)
				{
					mTab.setValue(JP_WarehouseDst_ID, value);
				}else {
					;//Nothing to do
				}
			}
		}		
		
		return null;
	}

	public String setJP_PhysicalWarehouseNext_ID(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{
		if(!mTab.getValueAsBoolean(IsRecordRouteJP))
			return null;
		
		Object  obj_PhysicalWarehouseDst_ID = mTab.getValue(JP_PhysicalWarehouseDst_ID);
		if(obj_PhysicalWarehouseDst_ID == null)
		{
			mTab.setValue(JP_PhysicalWarehouseDst_ID, value);
			
		}else {
			
			Object  obj_PhysicalWarehouseTo_ID = mTab.getValue(JP_PhysicalWarehouseTo_ID);
			if(obj_PhysicalWarehouseTo_ID == null)
			{
				;//Nothing to do;
			}else {
				
				int int_PhysicalWarehouseTo_ID = ((Integer)obj_PhysicalWarehouseTo_ID).intValue();
				int int_PhysicalWarehouseDst_ID = ((Integer)obj_PhysicalWarehouseDst_ID).intValue();;

				if(int_PhysicalWarehouseTo_ID == int_PhysicalWarehouseDst_ID)
				{
					mTab.setValue(JP_PhysicalWarehouseDst_ID, value);
				}else {
					;//Nothing to do
				}
			}
		}		
		
		return null;
	}
	
	public String setJP_MovementDateNext(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{
		if(!mTab.getValueAsBoolean(IsRecordRouteJP))
			return null;
		
		if(value != null)
		{
			Timestamp ts_MovementDateNext = (Timestamp)value;
			Object  obj_MovementDate = mTab.getValue("MovementDate");
			if(obj_MovementDate != null)
			{
				Timestamp ts_MovementDate = (Timestamp)obj_MovementDate;
				if(ts_MovementDateNext.compareTo(ts_MovementDate) < 0)
				{
					mTab.fireDataStatusEEvent("Warning", Msg.getElement(ctx, "MovementDate") + " > " + Msg.getElement(ctx, "JP_MovementDateNext"), false);
				}
			}
			
			Object  obj_MovementDateDst = mTab.getValue(JP_MovementDateDst);
			if(obj_MovementDateDst == null)
			{
				mTab.setValue(JP_MovementDateDst, value);
				
			}
		}		
		
		return null;
	}
	
	public String setJP_MovementPre_ID(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{
		if(!mTab.getValueAsBoolean(IsRecordRouteJP))
			return null;
		
			
		if(value != null)
		{
			int int_JP_MovementPre_ID = ((Integer)value).intValue();
			MMovement mm = new MMovement(ctx, int_JP_MovementPre_ID, null);
			
			if(mm.get_ValueAsInt(JP_WarehouseDep_ID) > 0)
			{
				mTab.setValue(JP_WarehouseDep_ID, mm.get_ValueAsInt(JP_WarehouseDep_ID));
				
			}else {
				mTab.setValue(JP_WarehouseDep_ID, mm.get_ValueAsInt(JP_WarehouseFrom_ID));
			}
			
			
			if(mm.get_ValueAsInt(JP_PhysicalWarehouseDep_ID) > 0)
			{
				mTab.setValue(JP_PhysicalWarehouseDep_ID, mm.get_ValueAsInt(JP_PhysicalWarehouseDep_ID));
				
			}else {
				mTab.setValue(JP_PhysicalWarehouseDep_ID, mm.get_ValueAsInt(JP_PhysicalWarehouseFrom_ID));
			}
			
			mTab.setValue(JP_MovementDateDep, mm.get_Value(MMovement.COLUMNNAME_MovementDate));
		}
		
		return null;
	}
	
	public String setJP_MovementDateDst(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{
		if(!mTab.getValueAsBoolean(IsRecordRouteJP))
			return null;
		
		if(value != null)
		{
			Timestamp ts_MovementDst = (Timestamp)value;
			Object  obj_MovementDate = mTab.getValue("MovementDate");
			if(obj_MovementDate != null)
			{
				Timestamp ts_MovementDate = (Timestamp)obj_MovementDate;
				if(ts_MovementDst.compareTo(ts_MovementDate) < 0)
				{
					mTab.fireDataStatusEEvent("Warning", Msg.getElement(ctx, "MovementDate") + " > " + Msg.getElement(ctx, "JP_MovementDateDst"), false);
				}
			}
		}		
		
		return null;

	}
}
