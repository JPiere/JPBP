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
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.util.Env;
import org.compiere.util.Msg;


import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractLine;


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
			GridField gf_ContractProcPeriod_ID = gridTab.getField("JP_ContractProcPeriod_ID");
			if(gf_ContractProcPeriod_ID != null)
			{
				int old_ContractProcPeriod_ID = 0;
				int new_ContractProcPeriod_ID = 0;
				Object old_value = gf_ContractProcPeriod_ID.getOldValue();
				Object new_value = gf_ContractProcPeriod_ID.getValue();
				if(old_value == null)
					old_ContractProcPeriod_ID = 0;
				else
					old_ContractProcPeriod_ID = ((Integer)old_value).intValue();
					
				if(new_value == null)
					new_ContractProcPeriod_ID = 0;
				else
					new_ContractProcPeriod_ID = ((Integer)new_value).intValue();
				
				int Record_ID =((Integer)gridTab.getRecord_ID()).intValue();
				if(Record_ID > 0 && old_ContractProcPeriod_ID == new_ContractProcPeriod_ID)
				{
					;//Notihg to do
					
				}else{
					
					if(gridTab.getTabNo() == 0 && new_ContractProcPeriod_ID > 0)
					{
						Object obj_ContracContent_ID = gridTab.getValue("JP_ContractContent_ID");
						if(obj_ContracContent_ID == null)
						{
							;//Nothing to do
						}else{
							
							int JP_ContractContent_ID = ((Integer)obj_ContracContent_ID).intValue();
							MContractContent content = MContractContent.get(Env.getCtx(), JP_ContractContent_ID);
							MOrder[] orderes = content.getOrderByContractPeriod(Env.getCtx(), new_ContractProcPeriod_ID, null);
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
					}//gridTab.getTabNo() == 0
					
					else if(gridTab.getTabNo() == 1 && new_ContractProcPeriod_ID > 0)
					{
						Object obj_ContracLine_ID = gridTab.getValue("JP_ContractLine_ID");
						if(obj_ContracLine_ID == null)
						{
							;//Nothing to do
						}else{
							int JP_ContractLine_ID = ((Integer)obj_ContracLine_ID).intValue();
							MContractLine contractline = MContractLine.get(Env.getCtx(), JP_ContractLine_ID);
							MOrderLine[] orderLines = contractline.getOrderLineByContractPeriod(Env.getCtx(), new_ContractProcPeriod_ID, null);
							for(int i = 0; i < orderLines.length; i++)
							{
								if(orderLines[i].getC_OrderLine_ID() == Record_ID)
								{
									continue;
								}else{
										
									String docInfo = Msg.getElement(Env.getCtx(), "DocumentNo") + " : " + orderLines[i].getParent().getDocumentNo()
														+" - " + Msg.getElement(Env.getCtx(), "C_InvoiceLine_ID") + " : " + orderLines[i].getLine();
									String msg = docInfo + " " + Msg.getMsg(Env.getCtx(),"JP_DoYouConfirmIt");//Do you confirm it?
									final MOrderLine orderLine = orderLines[i];
									Callback<Boolean> isZoom = new Callback<Boolean>()
									{
											@Override
											public void onCallback(Boolean result)
											{
												if(result)
												{
													AEnv.zoom(MOrderLine.Table_ID, orderLine.getC_OrderLine_ID());
												}
											}
										
									};
									FDialog.ask( event.getWindow().getADWindowContent().getWindowNo(), event.getWindow().getComponent(),Msg.getElement(Env.getCtx(), "JP_ContractProcPeriod_ID"), "JP_OverlapPeriod", msg, isZoom);
									break;
								}
							}//for
						}
						
					}//gridTab.getTabNo() == 1
					
				}//if(Record_ID > 0
				
			}//if(gf_ContractProcPeriod_ID != null)
			
		}//BEFORE_SAVE
		
		callback.onCallback(true);
	}
	
}
