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
package jpiere.base.plugin.webui.apps.form;

import java.util.Collection;

import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.panel.ADForm;
import org.adempiere.webui.window.FDialog;
import org.compiere.model.Query;
import org.compiere.process.ProcessInfo;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.wf.MWFActivity;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;


/**
 *  JPIERE-0513: Approval of Unprocessed Work flow Activity at Info Window.
 *
 *  @author Hideaki Hagiwara（h.hagiwara@oss-erp.co.jp）
 *
 */
public class WFActivityZoom extends ADForm{

	@Override
	protected void initForm()
	{
		setClosable(true);
		setHeight("50%");
		setWidth0("50%");
	}

	@Override
	public Mode getWindowMode()
	{
		return Mode.OVERLAPPED; //Mode.MODAL, Mode.HIGHLIGHTED, Mode.POPUP, Mode.OVERLAPPED
	}

	@Override
	public void dispose()
	{
		super.dispose();
	}

	@Override
	public void setProcessInfo(ProcessInfo pi)
	{
		if(pi == null)
		{
			super.dispose();
			return;
		}

		super.setProcessInfo(pi);

		String whereClause = "EXISTS (SELECT T_Selection_ID FROM T_Selection WHERE T_Selection.AD_PInstance_ID=? " +
				" AND T_Selection.T_Selection_ID = AD_WF_Activity .AD_WF_Activity_ID)";

		Collection<MWFActivity> m_WFAs = new Query(Env.getCtx(), MWFActivity.Table_Name, whereClause, null)
													.setClient_ID()
													.setParameters(new Object[]{pi.getAD_PInstance_ID()})
													.list();

		if(m_WFAs.size() == 1)
		{
			for(MWFActivity wfa: m_WFAs)
			{
				AEnv.zoom(wfa.getAD_Table_ID(), wfa.getRecord_ID());
			}

		}else {

			//More than one zoom destination is selected. Please select one zoom destination.
			FDialog.error(getWindowNo(), Msg.getMsg(Env.getCtx(), "JP_WF_ZoomOne"));
		}

		addEventListener(Events.ON_CLOSE, this);

		//Events.echoEvent(Events.ON_CLOSE, this, null);
		//Events.sendEvent(Events.ON_CLOSE, this, null);
		Events.postEvent(Events.ON_CLOSE, this, null);

	}

	@Override
	public void onEvent(Event event) throws Exception
	{
		if(event.getName().equals(Events.ON_CLOSE))
		{
			dispose();
		}
	}


}
