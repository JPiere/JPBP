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

public class JPiereContractOrderWindowValidator implements WindowValidator {
	
	@Override
	public void onWindowEvent(WindowValidatorEvent event, Callback<Boolean> callback) 
	{
		
		if(event.getName().equals(WindowValidatorEventType.BEFORE_SAVE.getName()))
		{
			GridTab gridTab =event.getWindow().getADWindowContent().getActiveGridTab();
			int JP_ContractProcPeriod_ID = ((Integer)gridTab.getValue("JP_ContractProcPeriod_ID")).intValue();
			if(JP_ContractProcPeriod_ID > 0)
			{
				int Record_ID =((Integer)gridTab.getRecord_ID()).intValue();
				int JP_ContractContent_ID = ((Integer)gridTab.getValue("JP_ContractContent_ID")).intValue();
				MContractContent content = MContractContent.get(Env.getCtx(), JP_ContractContent_ID);
				MOrder[] orders = content.getOrderByContractPeriod(Env.getCtx(), JP_ContractProcPeriod_ID, null);
				for(int i = 0; i < orders.length; i++)
				{
					if(orders[i].getC_Order_ID() == Record_ID)
					{
						continue;
					}else{
							
						String docInfo = Msg.getElement(Env.getCtx(), "DocumentNo") + " : " + orders[i].getDocumentNo();
						String msg = docInfo + " " + Msg.getMsg(Env.getCtx(),"JP_DoYouConfirmIt");//Do you confirm it?
						final MOrder order = orders[i];
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

			}else{
				callback.onCallback(true);
			}
		}
		
		callback.onCallback(true);
	}
	
}
