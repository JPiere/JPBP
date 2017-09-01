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
import org.compiere.model.MDocType;
import org.compiere.util.Env;

/**
 *
 *  JPiere Contract Content CallOut
 *
 *  JPIERE-0363:JPBP
 *
 * @author Hideaki Hagiwara
 *
 */
public class JPiereContractContentCallout implements IColumnCallout {

	@Override
	public String start(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{

		if(mField.getColumnName().equals("JP_BaseDocDocType_ID"))
		{
			if( value == null)
			{
				mTab.setValue ("OrderType",  "");
			}else{
				Integer JP_BaseDocDocType_ID = (Integer)value;
				MDocType docType = MDocType.get(ctx, JP_BaseDocDocType_ID.intValue());
				mTab.setValue("IsSOTrx", docType.isSOTrx());
				
				String DocSubTypeSO = docType.getDocSubTypeSO();
				Env.setContext(ctx, WindowNo, "OrderType", DocSubTypeSO);
				mTab.setValue ("OrderType", DocSubTypeSO);
			}
		}

		return "";
	}
	
}
