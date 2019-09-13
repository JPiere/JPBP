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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import org.adempiere.base.IColumnCallout;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.MLocator;
import org.compiere.util.DB;

/**
 *
 *  JPiere Order Document CallOut
 *
 *  JPIERE-0227 Common Warehouse
 *  JPIERE-0317 Physical Warehouse
 *
 * @author Hideaki Hagiwara
 *
 */
public class JPiereOrderCallout implements IColumnCallout {

	@Override
	public String start(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{
		//JPIERE-0227 Common Warehouse & JPIERE-0317 Physical Warehouse
		if(mField.getColumnName().equals("JP_LocatorTo_ID"))
		{
			if(mTab.isNew())
				;
			else
				mTab.setValue("JP_Locator_ID", value);

			return "";
		}

		if(mField.getColumnName().equals("JP_LocatorFrom_ID") && mTab.getValue("JP_LocatorFrom_ID") != null)
		{
			Integer JP_LocatorFrom_ID = (Integer)value;
			MLocator fromLocator =  MLocator.get(ctx, JP_LocatorFrom_ID.intValue());
			MLocator toLocator = getToLocator(ctx, fromLocator, ((Integer)mTab.getValue("M_Warehouse_ID")).intValue());
			if(toLocator != null)
			{
				mTab.setValue("JP_LocatorTo_ID",toLocator.getM_Locator_ID()) ;
				mTab.setValue("JP_Locator_ID", toLocator.getM_Locator_ID());
			}

			return "";
		}


		return "";
	}

	private MLocator getToLocator(Properties ctx, MLocator fromLocator, int M_Warehouse_ID)
	{
		int JP_PhysicalWarehouse_ID = fromLocator.get_ValueAsInt("JP_PhysicalWarehouse_ID");
		int M_LocatorType_ID = fromLocator.get_ValueAsInt("M_LocatorType_ID");

		final String sql = "SELECT * FROM M_Locator WHERE M_Warehouse_ID=? AND JP_PhysicalWarehouse_ID = ? AND M_LocatorType_ID = ? ORDER BY IsDefault DESC, PriorityNo DESC";
		MLocator toLocator = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, null);
			pstmt.setInt(1, M_Warehouse_ID);
			pstmt.setInt(2, JP_PhysicalWarehouse_ID);
			pstmt.setInt(3, M_LocatorType_ID);
			rs = pstmt.executeQuery();
			if (rs.next())
				toLocator= new MLocator (ctx, rs, null);
		}
		catch (Exception e)
		{
//			log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}

		return toLocator;
	}

}
