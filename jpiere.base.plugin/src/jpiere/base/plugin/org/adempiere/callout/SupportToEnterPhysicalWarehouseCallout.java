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

import java.util.Properties;

import org.adempiere.base.IColumnCallout;
import org.compiere.model.CalloutEngine;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.MWarehouse;
import org.compiere.util.Util;


/**
 * JPIERE-0588: Support to enter Physical Warehouse from org warehouse Call out
 *
 * @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 *
 */
public class SupportToEnterPhysicalWarehouseCallout  extends CalloutEngine implements IColumnCallout {


	@Override
	public String start(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{
		
		String msg = null;

		if(mField.getColumnName().equals("M_Warehouse_ID"))
		{
			msg = callFromM_Warehouse_ID(ctx, WindowNo, mTab, mField, value, oldValue);

		}else if(mField.getColumnName().equals("JP_Warehouse_ID")) {

			msg = callFromJP_Warehouse_ID(ctx, WindowNo, mTab, mField, value, oldValue);

		}else if(mField.getColumnName().equals("JP_WarehouseFrom_ID")) {

			msg = callFromJP_WarehouseFrom_ID(ctx, WindowNo, mTab, mField, value, oldValue);

		}else if(mField.getColumnName().equals("JP_WarehouseTo_ID")) {

			msg = callFromJP_WarehouseTo_ID(ctx, WindowNo, mTab, mField, value, oldValue);

		}

		return msg;

	}
	
	
	public String callFromM_Warehouse_ID(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{
		int orgWarehouse_ID = 0;

		if(value == null || Util.isEmpty(value.toString()))
		{
			;//noting to do
		}else {
			orgWarehouse_ID = Integer.valueOf(value.toString()).intValue();
		}
		
		if(orgWarehouse_ID == 0)
		{
			mTab.setValue("JP_PhysicalWarehouse_ID", null);
			return null;
		}
		
		MWarehouse m_OrgWH = MWarehouse.get(ctx, orgWarehouse_ID);
		int JP_PhysicalWarehouse_ID = m_OrgWH.get_ValueAsInt("JP_PhysicalWarehouse_ID");
		if(JP_PhysicalWarehouse_ID <= 0)
		{
			mTab.setValue("JP_PhysicalWarehouse_ID", null);
		}else {
			mTab.setValue("JP_PhysicalWarehouse_ID", JP_PhysicalWarehouse_ID);
		}
		
		return null;
	}
	
	
	public String callFromJP_Warehouse_ID(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{

		int orgWarehouse_ID = 0;

		if(value == null || Util.isEmpty(value.toString()))
		{
			;//noting to do
		}else {
			orgWarehouse_ID = Integer.valueOf(value.toString()).intValue();
		}
		
		if(orgWarehouse_ID == 0)
		{
			mTab.setValue("JP_PhysicalWarehouse_ID", null);
			return null;
		}
		
		MWarehouse m_OrgWH = MWarehouse.get(ctx, orgWarehouse_ID);
		int JP_PhysicalWarehouse_ID = m_OrgWH.get_ValueAsInt("JP_PhysicalWarehouse_ID");
		if(JP_PhysicalWarehouse_ID <= 0)
		{
			mTab.setValue("JP_PhysicalWarehouse_ID", null);
		}else {
			mTab.setValue("JP_PhysicalWarehouse_ID", JP_PhysicalWarehouse_ID);
		}
		
		return null;
	}
	
	
	public String callFromJP_WarehouseFrom_ID(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{
		int orgWarehouse_ID = 0;

		if(value == null || Util.isEmpty(value.toString()))
		{
			;//noting to do
		}else {
			orgWarehouse_ID = Integer.valueOf(value.toString()).intValue();
		}
		
		if(orgWarehouse_ID == 0)
		{
			mTab.setValue("JP_PhysicalWarehouseFrom_ID", null);
			return null;
		}
		
		MWarehouse m_OrgWH = MWarehouse.get(ctx, orgWarehouse_ID);
		int JP_PhysicalWarehouse_ID = m_OrgWH.get_ValueAsInt("JP_PhysicalWarehouse_ID");
		if(JP_PhysicalWarehouse_ID <= 0)
		{
			mTab.setValue("JP_PhysicalWarehouseFrom_ID", null);
		}else {
			mTab.setValue("JP_PhysicalWarehouseFrom_ID", JP_PhysicalWarehouse_ID);
		}
		
		return null;
	}
	
	
	public String callFromJP_WarehouseTo_ID(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{

		int orgWarehouse_ID = 0;

		if(value == null || Util.isEmpty(value.toString()))
		{
			;//noting to do
		}else {
			orgWarehouse_ID = Integer.valueOf(value.toString()).intValue();
		}
		
		if(orgWarehouse_ID == 0)
		{
			mTab.setValue("JP_PhysicalWarehouseTo_ID", null);
			return null;
		}
		
		MWarehouse m_OrgWH = MWarehouse.get(ctx, orgWarehouse_ID);
		int JP_PhysicalWarehouse_ID = m_OrgWH.get_ValueAsInt("JP_PhysicalWarehouse_ID");
		if(JP_PhysicalWarehouse_ID <= 0)
		{
			mTab.setValue("JP_PhysicalWarehouseTo_ID", null);
		}else {
			mTab.setValue("JP_PhysicalWarehouseTo_ID", JP_PhysicalWarehouse_ID);
		}
		
		return null;
	}
}
