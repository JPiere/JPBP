package jpiere.base.plugin.org.adempiere.base;

import org.adempiere.util.Callback;
import org.adempiere.webui.adwindow.validator.WindowValidator;
import org.adempiere.webui.adwindow.validator.WindowValidatorEvent;
import org.adempiere.webui.adwindow.validator.WindowValidatorEventType;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.process.DocAction;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.MReferenceTest;

public class JPiereReferenceTestWindowValidator implements WindowValidator {

	@Override
	public void onWindowEvent(WindowValidatorEvent event, Callback<Boolean> callback)
	{

		if(event.getName().equals(WindowValidatorEventType.BEFORE_IGNORE.getName()))
		{
			GridTab gridTab =event.getWindow().getADWindowContent().getActiveGridTab();
			GridField gf_DocStatus = gridTab.getField("DocStatus");
			if(gf_DocStatus != null)
			{
				String docStatus = (String)gf_DocStatus.getValue();
				GridField button = gridTab.getField(MReferenceTest.COLUMNNAME_Processing);
				if(!Util.isEmpty(docStatus) && docStatus.equals(DocAction.STATUS_Completed))
				{
					button.getVO().IsReadOnly = true;
				}else {
					button.getVO().IsReadOnly = false;
				}

				if(!Util.isEmpty(docStatus) && docStatus.equals(DocAction.STATUS_Voided))
				{
					gridTab.getVO().IsReadOnly  = true;
				}else {
					gridTab.getVO().IsReadOnly  = false;
				}
			}

			gridTab.dataRefresh();

		}//AFTER_IGNORE

		callback.onCallback(true);
	}

}
