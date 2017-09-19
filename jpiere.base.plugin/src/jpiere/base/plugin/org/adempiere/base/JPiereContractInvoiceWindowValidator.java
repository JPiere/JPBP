package jpiere.base.plugin.org.adempiere.base;

import org.adempiere.util.Callback;
import org.adempiere.webui.adwindow.validator.WindowValidator;
import org.adempiere.webui.adwindow.validator.WindowValidatorEvent;
import org.adempiere.webui.adwindow.validator.WindowValidatorEventType;
import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.window.FDialog;
import org.compiere.model.GridTab;
import org.compiere.model.MInvoice;
import org.compiere.util.Env;
import org.compiere.util.Msg;


import jpiere.base.plugin.org.adempiere.model.MContractContent;

public class JPiereContractInvoiceWindowValidator implements WindowValidator {
	
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
					int JP_ContractContent_ID = ((Integer)gridTab.getValue("JP_ContractContent_ID")).intValue();
					MContractContent content = MContractContent.get(Env.getCtx(), JP_ContractContent_ID);
					MInvoice[] invoices = content.getInvoiceByContractPeriod(Env.getCtx(), JP_ContractProcPeriod_ID, null);
					for(int i = 0; i < invoices.length; i++)
					{
						if(invoices[i].getC_Invoice_ID() == Record_ID)
						{
							continue;
						}else{
								
							String docInfo = Msg.getElement(Env.getCtx(), "DocumentNo") + " : " + invoices[i].getDocumentNo();
							String msg = docInfo + " " + Msg.getMsg(Env.getCtx(),"JP_DoYouConfirmIt");//Do you confirm it?
							final MInvoice invoice = invoices[i];
							Callback<Boolean> isZoom = new Callback<Boolean>()
							{
									@Override
									public void onCallback(Boolean result)
									{
										if(result)
										{
											AEnv.zoom(MInvoice.Table_ID, invoice.getC_Invoice_ID());
										}
									}
								
							};
							FDialog.ask( event.getWindow().getADWindowContent().getWindowNo(), event.getWindow().getComponent(),Msg.getElement(Env.getCtx(), "JP_ContractProcPeriod_ID"), "JP_OverlapPeriod", msg, isZoom);
							break;
						}
					}//for
				}
			}//if(obj == null)
			
			callback.onCallback(true);
			
		}//BEFORE_SAVE
		
		callback.onCallback(true);
	}
	
}
