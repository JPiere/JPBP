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
package jpiere.base.plugin.org.adempiere.process;

import org.adempiere.webui.apps.IProcessParameterListener;
import org.adempiere.webui.apps.ProcessParameterPanel;
import org.adempiere.webui.editor.WEditor;
import org.adempiere.webui.event.ValueChangeEvent;
import org.compiere.util.Env;

import jpiere.base.plugin.org.adempiere.model.MReportCubeJP;

/**
*
* JPIERE-0460 JPiere Fact Acct BS
*
* @author h.hagiwara
*
*/
public class FactAcctBSParameterListener implements IProcessParameterListener {

	@Override
	public void onChange(ProcessParameterPanel parameterPanel, String columnName, WEditor editor)
	{
		if (editor.getValue() != null)
		{
			if ("PA_ReportCubeJP_ID".equals(editor.getColumnName()))
			{
				Integer PA_ReportCubeJP_ID = (Integer)editor.getValue();
				MReportCubeJP  cube = new MReportCubeJP(Env.getCtx(),PA_ReportCubeJP_ID.intValue(), null);

				WEditor calendar = parameterPanel.getEditor("C_Calendar_ID");
				calendar.setValue(cube.getC_Calendar_ID());
				parameterPanel.valueChange(new ValueChangeEvent(calendar, calendar.getColumnName(), null, calendar.getValue()));

			}

		}else {

			if ("PA_ReportCubeJP_ID".equals(editor.getColumnName()))
			{
				parameterPanel.getEditor("C_Calendar_ID").setValue(null);
				parameterPanel.getEditor("C_Year_ID").setValue(null);
				parameterPanel.getEditor("C_Period_ID").setValue(null);

				WEditor calendar = parameterPanel.getEditor("C_Calendar_ID");
				parameterPanel.valueChange(new ValueChangeEvent(calendar, calendar.getColumnName(), null, calendar.getValue()));
			}

		}

	}

}
