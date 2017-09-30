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
import org.compiere.model.MInOut;
import org.compiere.util.Env;
import org.compiere.util.Msg;


/**
 *
 *  JPiere Recognition CallOut
 *
 *  JPIERE-0364:JPBP
 *
 * @author Hideaki Hagiwara
 *
 */
public class JPiereRecognitionCallout implements IColumnCallout {

	@Override
	public String start(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{

		if(mField.getColumnName().equals("M_InOut_ID") && value != null)
		{
			MInOut io = new MInOut(ctx, ((Integer)value).intValue(), null);
			if(io.get_ValueAsInt("JP_Contract_ID")==0 || io.get_ValueAsInt("JP_ContractContent_ID")==0)
			{
				String msg = Msg.getMsg(Env.getCtx(), "Invalid")+ " "+Msg.getElement(Env.getCtx(), "M_InOut_ID") + "  "
						+ Msg.getMsg(Env.getCtx(), "JP_ToBeConfirmed") + "  " + Msg.getElement(Env.getCtx(), "JP_ContractType");
				return msg;
			}
			
			mTab.setValue("JP_Contract_ID", io.get_Value("JP_Contract_ID"));
			mTab.setValue("JP_ContractContent_ID", io.get_Value("JP_ContractContent_ID"));
			mTab.setValue("JP_ContractProcPeriod_ID", io.get_Value("JP_ContractProcPeriod_ID"));
		}

		return "";
	}
	

}
