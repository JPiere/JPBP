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

/**
 * 
 * Set Default Value to AD_Org_ID for Access Control
 * 
 * JPIERE-0546: Access Update of User Organization.
 * JPIERE-0547: Access Update of Role Organization.
 * JPIERE-0548: Access Update of User Role.
 * JPIERE-0549: Add Access Controle tab to Org Window. 
 * 
 * set Default Value at AD_Org_ID
 * 
 * @author Hideaki Hagiwara
 *
 */
public class JPiereAccessControlOrgCallout implements IColumnCallout {

	
	@Override
	public String start(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue) 
	{

		int AD_Org_ID = 0;
		if(value != null)
			AD_Org_ID = ((Integer)value).intValue();
	
		int old_AD_Org_ID = 0;
		if(oldValue != null)
			old_AD_Org_ID = ((Integer)oldValue).intValue();
		
		if(AD_Org_ID == 0 && old_AD_Org_ID == 0 && mTab.getParentTab() != null && mTab.isNew())
		{
			Object obj_AD_Org_ID = mTab.getParentTab().getValue("AD_Org_ID");
			if(obj_AD_Org_ID != null)
				AD_Org_ID = ((Integer)obj_AD_Org_ID).intValue();
			
			mTab.setValue("AD_Org_ID", AD_Org_ID);
		}
		

		return null;
	}

}
