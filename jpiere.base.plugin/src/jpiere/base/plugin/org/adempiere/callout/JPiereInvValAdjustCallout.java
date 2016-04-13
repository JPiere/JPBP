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

import jpiere.base.plugin.org.adempiere.model.MInvValProfile;

import org.adempiere.base.IColumnCallout;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;

/**
 *
 * JPIERE-0161:
 *
 * @author Hideaki Hagiwara
 *
 */
public class JPiereInvValAdjustCallout implements IColumnCallout {

	@Override
	public String start(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{
		if(value == null)
			return "";

		if(mField.getColumnName().equals("JP_InvValProfile_ID"))
		{
			MInvValProfile ivProfile = MInvValProfile.get(ctx, (Integer)value);
			mTab.setValue("C_Currency_ID", ivProfile.getC_Currency_ID());
		}else if(mField.getColumnName().equals("DateValue")){
			mTab.setValue("DateAcct", value);
		}

		return null;
	}

}
