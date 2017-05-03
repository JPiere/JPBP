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
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.MLocator;

/**
 *
 *  JPiere Order Document CallOut
 *
 *  JPIERE-0227 : Logical Material Movement
 *
 * @author Hideaki Hagiwara
 *
 */
public class JPiereOrderCallout implements IColumnCallout {

	@Override
	public String start(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{
		if(mField.getColumnName().equals("JP_LocatorTo_ID"))
		{
			if(value != null)
				mTab.setValue("JP_Locator_ID", value);
		}

		if(mField.getColumnName().equals("JP_LocatorFrom_ID") && mTab.getValue("JP_Locator_ID") != null && mTab.getValue("JP_LocatorFrom_ID") != null)
		{
			Integer JP_Locator_ID = 	(Integer)mTab.getValue("JP_Locator_ID");
			MLocator shipLocator = MLocator.get(ctx, JP_Locator_ID.intValue());
		
			Integer JP_LocatorFrom_ID = 	(Integer)mTab.getValue("JP_LocatorFrom_ID");
			MLocator fromLocator =  MLocator.get(ctx, JP_LocatorFrom_ID.intValue());
			if(shipLocator.get_ValueAsInt("JP_PhysicalWarehouse_ID") == fromLocator.get_ValueAsInt("JP_PhysicalWarehouse_ID"))
			{
				mTab.setValue("JP_LocatorTo_ID",JP_Locator_ID) ;
			}
		}
		
		
		return "";
	}

}
