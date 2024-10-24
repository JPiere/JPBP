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
package jpiere.base.plugin.factory;

import org.adempiere.webui.factory.IInfoFactory;
import org.adempiere.webui.info.InfoWindow;
import org.adempiere.webui.panel.InfoGeneralPanel;
import org.adempiere.webui.panel.InfoPanel;
import org.compiere.model.GridField;
import org.compiere.model.Lookup;
import org.compiere.model.MSysConfig;
import org.compiere.util.Env;

/**
 * JPIERE-0230 - Product Info Window Single Selection
 * JPIERE-0614(v11) - Single Selection Info Window
 * 
 * @author HideakiHagiwara
 *
 */
public class JPiereBasePluginInfoFactory implements IInfoFactory {

	@Override
	public InfoPanel create(int WindowNo, String tableName, String keyColumn,
			String value, boolean multiSelection, String whereClause, int AD_InfoWindow_ID, boolean lookup) {

		if (tableName.equals("M_Product") && AD_InfoWindow_ID > 0 && MSysConfig.getBooleanValue("JP_PRODUCT_INFOWINDOW", true, Env.getAD_Client_ID(Env.getCtx()), Env.getAD_Org_ID(Env.getCtx())) && lookup)
		{
        	InfoPanel info = new InfoWindow(WindowNo, tableName, keyColumn, value, multiSelection, whereClause, AD_InfoWindow_ID, lookup);
        	if (!info.loadedOK())
        	{
	            info = new InfoGeneralPanel (value, WindowNo, tableName, keyColumn, multiSelection, whereClause, lookup, null);
	        	if (!info.loadedOK()) {
	        		info.dispose(false);
	        		info = null;
	        	}
        	}

        	return info;
        }
		
		//JPIERE-0614(v11) - Single Selection Info Window
		if(MSysConfig.getBooleanValue("JP_SINGLESELECTION_INFOWINDOW", true, Env.getAD_Client_ID(Env.getCtx()), Env.getAD_Org_ID(Env.getCtx())) && lookup)
		{
        	InfoPanel info = new InfoWindow(WindowNo, tableName, keyColumn, value, multiSelection, whereClause, AD_InfoWindow_ID, lookup);
        	if (!info.loadedOK())
        	{
	            info = new InfoGeneralPanel (value, WindowNo, tableName, keyColumn, multiSelection, whereClause, lookup, null);
	        	if (!info.loadedOK()) {
	        		info.dispose(false);
	        		info = null;
	        	}
        	}

        	return info;			
		}
		
        //
        return null;
	}

	@Override
	public InfoPanel create(Lookup lookup, GridField field, String tableName,
			String keyColumn, String queryValue, boolean multiSelection,
			String whereClause, int AD_InfoWindow_ID) {

		String col = lookup.getColumnName();		//	fully qualified name

		if (col.indexOf('.') != -1)
			col = col.substring(col.indexOf('.')+1);

		if (col.equals("M_Product_ID") && AD_InfoWindow_ID > 0 && MSysConfig.getBooleanValue("JP_PRODUCT_INFOWINDOW", true, Env.getAD_Client_ID(Env.getCtx()), Env.getAD_Org_ID(Env.getCtx())))
		{
			InfoPanel info = create(lookup.getWindowNo(), tableName, keyColumn, queryValue, false, whereClause, AD_InfoWindow_ID, true);
			return info;
		}

		//JPIERE-0614(v11) - Single Selection Info Window
		if(MSysConfig.getBooleanValue("JP_SINGLESELECTION_INFOWINDOW", true, Env.getAD_Client_ID(Env.getCtx()), Env.getAD_Org_ID(Env.getCtx())))
		{
			InfoPanel info = create(lookup.getWindowNo(), tableName, keyColumn, queryValue, false, whereClause, AD_InfoWindow_ID, true);
			return info;
		}
		return null;
	}

	@Override
	public InfoWindow create(int AD_InfoWindow_ID) {

			return null;
	}

	@Override
	public InfoPanel create(int WindowNo, String tableName, String keyColumn, String value, boolean multiSelection,
			String whereClause, int AD_InfoWindow_ID, boolean lookup, GridField field) {
		
		return null;
	}

}
