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
import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.window.FDialog;
import org.compiere.model.GridTab;
import org.compiere.model.MOrder;
import org.compiere.util.Env;
import org.compiere.util.Msg;


import jpiere.base.plugin.org.adempiere.model.MContractContent;


/** 
* JPIERE-0363
*
* @author Hideaki Hagiwara
*
*/
public class JPiereContractOrderWindowValidator implements WindowValidator {
	
	@Override
	public void onWindowEvent(WindowValidatorEvent event, Callback<Boolean> callback) 
	{
		
		if(event.getName().equals(WindowValidatorEventType.BEFORE_SAVE.getName()))
		{
			GridTab gridTab =event.getWindow().getADWindowContent().getActiveGridTab();
			Object obj = gridTab.getValue("JP_ContractProcPeriod_ID");
			if(obj == null)
			{
				;//Notihg to do
				
			}else{	
				
				int JP_ContractProcPeriod_ID = ((Integer)obj).intValue();
				if(JP_ContractProcPeriod_ID > 0)
				{
					int Record_ID =((Integer)gridTab.getRecord_ID()).intValue();
					Object obj_ContracContent_ID = gridTab.getValue("JP_ContractContent_ID");
					if(obj_ContracContent_ID == null)
					{
						;//Nothing to do
					}else{
						
						int JP_ContractContent_ID = ((Integer)obj_ContracContent_ID).intValue();
						MContractContent content = MContractContent.get(Env.getCtx(), JP_ContractContent_ID);
						MOrder[] orderes = content.getOrderByContractPeriod(Env.getCtx(), JP_ContractProcPeriod_ID, null);
						for(int i = 0; i < orderes.length; i++)
						{
							if(orderes[i].getC_Order_ID() == Record_ID)
							{
								continue;
							}else{
									
								String docInfo = Msg.getElement(Env.getCtx(), "DocumentNo") + " : " + orderes[i].getDocumentNo();
								String msg = docInfo + " " + Msg.getMsg(Env.getCtx(),"JP_DoYouConfirmIt");//Do you confirm it?
								final MOrder order = orderes[i];
								Callback<Boolean> isZoom = new Callback<Boolean>()
								{
										@Override
										public void onCallback(Boolean result)
										{
											if(result)
											{
												AEnv.zoom(MOrder.Table_ID, order.getC_Order_ID());
											}
										}
									
								};
								FDialog.ask( event.getWindow().getADWindowContent().getWindowNo(), event.getWindow().getComponent(),Msg.getElement(Env.getCtx(), "JP_ContractProcPeriod_ID"), "JP_OverlapPeriod", msg, isZoom);
								break;
							}
						}//for
					}
				}
			}//if(obj == null)
			callback.onCallback(true);
		}//BEFORE_SAVE
		
		callback.onCallback(true);
	}
	
}
