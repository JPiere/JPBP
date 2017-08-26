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
import org.adempiere.util.Callback;
import org.adempiere.webui.window.FDialog;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.util.Msg;

import jpiere.base.plugin.org.adempiere.model.MContractT;

/**
 *
 *  JPiere Contract CallOut
 *
 *  JPIERE-0363:JPBP
 *
 * @author Hideaki Hagiwara
 *
 */
public class JPiereContractCallout implements IColumnCallout {

	@Override
	public String start(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{

		if(mField.getColumnName().equals("JP_ContractT_ID") && value != null)
		{

			FDialog.ask(WindowNo, null, Msg.getMsg(ctx, "JP_UpdateContractByTemplate"), new Callback<Boolean>() //Would you like to update contract document by template?
			{

				@Override
				public void onCallback(Boolean result)
				{
					if(result)
					{
						updateContract(ctx, WindowNo, mTab, mField, value, oldValue) ;
					}else{
						;//Noting to do
					}
		        }

			});//FDialog.
			
		}

		return "";
	}
	
	private void updateContract(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{
		MContractT contractTemplate = MContractT.get(ctx, ((Integer)value).intValue());
		mTab.setValue("C_DocType_ID", contractTemplate.getC_DocType_ID());
		mTab.setValue("Name", contractTemplate.getName());
		mTab.setValue("Description", contractTemplate.getDescription());
		mTab.setValue("IsAutomaticUpdateJP",contractTemplate.isAutomaticUpdateJP());
		if(contractTemplate.isAutomaticUpdateJP())
		{
			mTab.setValue("JP_ContractCancelTerm_ID", contractTemplate.getJP_ContractCancelTerm_ID());
			mTab.setValue("JP_ContractExtendPeriod_ID", contractTemplate.getJP_ContractExtendPeriod_ID());
		}
	}

}
