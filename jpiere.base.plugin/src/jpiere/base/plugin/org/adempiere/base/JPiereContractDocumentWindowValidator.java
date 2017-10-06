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

package jpiere.base.plugin.org.adempiere.base;

import org.adempiere.util.Callback;
import org.adempiere.webui.adwindow.validator.WindowValidator;
import org.adempiere.webui.adwindow.validator.WindowValidatorEvent;
import org.adempiere.webui.adwindow.validator.WindowValidatorEventType;
import org.adempiere.webui.window.FDialog;
import org.compiere.model.GridTab;
import org.compiere.util.Env;
import org.compiere.util.Msg;

import jpiere.base.plugin.org.adempiere.model.MContract;

/** 
* JPIERE-0363
*
* @author Hideaki Hagiwara
*
*/
public class JPiereContractDocumentWindowValidator implements WindowValidator {
	
	@Override
	public void onWindowEvent(WindowValidatorEvent event, Callback<Boolean> callback) 
	{
		
		if(event.getName().equals(WindowValidatorEventType.AFTER_SAVE.getName()))
		{
			GridTab gridTab =event.getWindow().getADWindowContent().getActiveGridTab();
			if(gridTab.getTabNo() == 0 )
			{
				
				String JP_ContractType = gridTab.get_ValueAsString("JP_ContractType");
				int Record_ID = gridTab.getRecord_ID();
				if(JP_ContractType.equals("PDC") && Record_ID > 0)
				{
					MContract contract = MContract.get(Env.getCtx(), Record_ID);
					if(contract.getContractContents(true,null).length > 0)
					{
						FDialog.info(event.getWindow().getADWindowContent().getWindowNo()
								, event.getWindow().getComponent(), "JP_ToBeConfirmed", Msg.getElement(Env.getCtx(), "JP_ConfirmContractProcessDateTo"));
						
					}
					
				}//if(JP_ContractType.equals("PD"))
				
			}//gridTab.getTabNo() == 0 
			
		}//After_SAVE
		
		callback.onCallback(true);
	}
	
}
