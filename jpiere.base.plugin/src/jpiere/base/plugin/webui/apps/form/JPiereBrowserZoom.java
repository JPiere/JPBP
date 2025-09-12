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

import org.adempiere.exceptions.AdempiereException;
import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.desktop.IDesktop;
import org.adempiere.webui.panel.ADForm;
import org.adempiere.webui.part.WindowContainer;
import org.adempiere.webui.session.SessionManager;
import org.compiere.model.MColumn;
import org.compiere.model.MProcess;
import org.compiere.model.MProcessPara;
import org.compiere.model.MTable;
import org.compiere.model.PO;
import org.compiere.process.ProcessInfo;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;
import org.zkoss.zk.au.out.AuScript;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;

/**
 *  JPIERE-0653: Browser Zoom
 *  Open permanent link of record at another browser tab.
 *
 *  @author Hideaki Hagiwara（h.hagiwara@oss-erp.co.jp）
 *
 */
public class JPiereBrowserZoom extends ADForm{
	
	private static final long serialVersionUID = 5121327387915143175L;

	@Override
	protected void initForm()
	{
		;
	}

	
	@Override
	public void setProcessInfo(ProcessInfo pi)
	{
		if(pi == null)
			return ;
		
		/** Check of configuration */
		int AD_Process_ID = pi.getAD_Process_ID();
		if(AD_Process_ID <= 0)
			throw new AdempiereException(Msg.getMsg(Env.getCtx(), "Error") + " " + Msg.getMsg(Env.getCtx(), "NotFound") + " " 
					+ Msg.getElement(Env.getCtx(), MProcess.COLUMNNAME_AD_Process_ID));
		
		MProcess m_Process = MProcess.get(AD_Process_ID);
		
		MProcessPara para_TableName = m_Process.getParameter(MTable.COLUMNNAME_TableName);
		if(para_TableName == null)
			throw new AdempiereException(Msg.getMsg(Env.getCtx(), "Error") + " " + Msg.getMsg(Env.getCtx(), "NotFound") + " " 
					+ Msg.getElement(Env.getCtx(), MProcessPara.COLUMNNAME_AD_Process_Para_ID) +" - "+ MTable.COLUMNNAME_TableName) ;
		
		String tableName = para_TableName.getDefaultValue();
		if(Util.isEmpty(tableName))
			throw new AdempiereException(Msg.getMsg(Env.getCtx(), "Error") + " " + Msg.getMsg(Env.getCtx(), "NotFound") + " " 
					+ Msg.getElement(Env.getCtx(), MProcessPara.COLUMNNAME_AD_Process_Para_ID) + " - " + MTable.COLUMNNAME_TableName
					+ " - " + Msg.getElement(Env.getCtx(),MProcessPara.COLUMNNAME_DefaultValue) ) ;
		
		MProcessPara para_ColumnName = m_Process.getParameter("ColumnName");
		if(para_ColumnName == null)
			throw new AdempiereException(Msg.getMsg(Env.getCtx(), "Error") + " " + Msg.getMsg(Env.getCtx(), "NotFound") + " " 
					+ Msg.getElement(Env.getCtx(), MProcessPara.COLUMNNAME_AD_Process_Para_ID) +" - "+ MColumn.COLUMNNAME_ColumnName) ;
		
		String columnName = para_ColumnName.getDefaultValue();
		if(Util.isEmpty(columnName))
			throw new AdempiereException(Msg.getMsg(Env.getCtx(), "Error") + " " + Msg.getMsg(Env.getCtx(), "NotFound") + " " 
					+ Msg.getElement(Env.getCtx(), MProcessPara.COLUMNNAME_AD_Process_Para_ID) + " - " + MColumn.COLUMNNAME_ColumnName
					+ " - " + Msg.getElement(Env.getCtx(),MProcessPara.COLUMNNAME_DefaultValue) ) ;
		
		if(!columnName.endsWith("_ID"))
			throw new AdempiereException(Msg.getMsg(Env.getCtx(), "Error") + " " + Msg.getMsg(Env.getCtx(), "Invalid") + " " 
					+ Msg.getElement(Env.getCtx(), MColumn.COLUMNNAME_ColumnName) + " - " + columnName) ;		
		
		/** Get PO of Selected record */
		int Record_ID = pi.getRecord_ID();
		if(Record_ID <= 0)
			throw new AdempiereException(Msg.getMsg(Env.getCtx(), "Error") + " " + Msg.getMsg(Env.getCtx(), "NotFound") + " " 
					+ Msg.getElement(Env.getCtx(), "Record_ID")) ;
		
		int AD_Table_ID = pi.getTable_ID();
		if(AD_Table_ID <= 0)
			throw new AdempiereException(Msg.getMsg(Env.getCtx(), "Error") + " " + Msg.getMsg(Env.getCtx(), "NotFound") + " " 
					+ Msg.getElement(Env.getCtx(), MTable.COLUMNNAME_AD_Table_ID)) ;
			
		MTable m_Table = MTable.get(AD_Table_ID);
		PO  selected_PO = m_Table.getPO(Record_ID, null);
		

		/** Get Zoom PO that will be displayed at the another browser tab */
		int zoomRecord_ID = selected_PO.get_ValueAsInt(columnName);
		if(zoomRecord_ID <= 0)
			throw new AdempiereException(Msg.getMsg(Env.getCtx(), "Error") + " " + Msg.getMsg(Env.getCtx(), "JP_Null") + Msg.getElement(Env.getCtx(), columnName));
		
		MTable zoomTable = MTable.get(Env.getCtx(), tableName);
		if(zoomTable == null)
			throw new AdempiereException(Msg.getMsg(Env.getCtx(), "Error") + " " + Msg.getMsg(Env.getCtx(), "NotFound") + " " 
					+ Msg.getElement(Env.getCtx(), MTable.COLUMNNAME_TableName) + " - " + tableName) ;
		
		PO zoom_PO = zoomTable.getPO(zoomRecord_ID, null);

		/** Open the another browser tab */
		String uri = AEnv.getZoomUrlTableID(zoom_PO);
		
		//String url = "window.open(\"" + uri +"\", \"popup\")";//pop up
		String url = "window.open(\"" + uri +"\", \"_blank\")";
		Clients.response(new AuScript(url));
		
	}


	@Override
	public Mode getWindowMode()
	{
		//Mode.EMBEDDED,MODAL,OVERLAPPED,POPUP,HIGHLIGHTED
		return Mode.EMBEDDED;
	}


	@Override
	public void onEvent(Event event) throws Exception 
	{
		Component comp = SessionManager.getAppDesktop().getActiveWindow();
		if(comp instanceof JPiereBrowserZoom)
			SessionManager.getAppDesktop().closeActiveWindow();
		
		removeEventListener(WindowContainer.ON_WINDOW_CONTAINER_SELECTION_CHANGED_EVENT, this);
    	SessionManager.getSessionApplication().getKeylistener().removeEventListener(Events.ON_CTRL_KEY, this);
    	removeEventListener(IDesktop.ON_CLOSE_WINDOW_SHORTCUT_EVENT, this);
		this.dispose();
		
		//super.onEvent(event);
	}

	

}
