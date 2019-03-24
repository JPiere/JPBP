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

package jpiere.base.plugin.webui.action.attachment;

import java.util.ArrayList;

import org.adempiere.webui.AdempiereWebUI;
import org.adempiere.webui.LayoutUtils;
import org.adempiere.webui.adwindow.ADWindow;
import org.adempiere.webui.adwindow.ADWindowContent;
import org.adempiere.webui.adwindow.IADTabbox;
import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.ToolBarButton;
import org.adempiere.webui.component.Window;
import org.adempiere.webui.event.DialogEvents;
import org.adempiere.webui.theme.ThemeManager;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.compiere.model.MQuery;
import org.compiere.model.MSysConfig;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Div;

import jpiere.base.plugin.org.adempiere.model.MAttachmentFileRecord;

/**
*
* JPIERE-0436: JPiere Attachment File
*
*
* @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
*
*/
public class JPiereAttchmentBaseWindow extends Window implements EventListener<Event>{

	protected ADWindow adWindow;
	protected ADWindowContent  adWindowContent;

	protected IADTabbox          	 adTabbox;
	protected int AD_Table_ID = 0;
	protected int Record_ID = 0;

	public JPiereAttchmentBaseWindow(ADWindow adWindow)
	{
		super();

		this.adWindow = adWindow;
		this.adWindowContent = adWindow.getADWindowContent();
		this.adTabbox = adWindowContent.getADTab();
		this.AD_Table_ID =adTabbox.getSelectedGridTab().getAD_Table_ID();
		this.Record_ID =  adTabbox.getSelectedGridTab().getRecord_ID();


//		setStyle("height: 25%; width: 25%;");
		setSclass("popup-dialog");

    	ZKUpdateUtil.setVflex(this, "1");

    	this.setTitle(Msg.getMsg(Env.getCtx(), "Attachment").replaceAll("&", "") + ": " + adWindowContent.getTitle());
    	this.setClosable(true);
    	this.setSizable(true);
    	this.setMaximizable(false);

    	Div div = new Div();
    	ZKUpdateUtil.setVflex(div, "0");
    	div.setStyle("padding:6px;");
    	this.appendChild(div);

    	//Attchment Button
    	Button btnAttachment = new Button();
        btnAttachment.setAttribute("name","btnAttachment");
        btnAttachment.setSclass("img-btn");
        if (ThemeManager.isUseFontIconForImage())
        	btnAttachment.setIconSclass("z-icon-Attachment");
        else
        	btnAttachment.setImage(ThemeManager.getThemeResource("images/Attachment24.png"));
        btnAttachment.addEventListener(Events.ON_CLICK, this);
        btnAttachment.setId("btnAttachment");
        btnAttachment.setStyle("vertical-align: middle;");
        if (ThemeManager.isUseFontIconForImage())
        	LayoutUtils.addSclass("large-toolbarbutton", btnAttachment);

        div.appendChild(btnAttachment);


    	//DownLoad Button
        Button btnExport = new Button();
    	btnExport.setAttribute("name","btnExport");
    	btnExport.setSclass("img-btn");
        if (ThemeManager.isUseFontIconForImage())
        	btnExport.setIconSclass("z-icon-Export");
        else
        	btnExport.setImage(ThemeManager.getThemeResource("images/Export24.png"));
        btnExport.addEventListener(Events.ON_CLICK, this);
        btnExport.setId("btnExport");
        btnExport.setStyle("vertical-align: middle;");
        if (ThemeManager.isUseFontIconForImage())
        	LayoutUtils.addSclass("large-toolbarbutton", btnExport);

        div.appendChild(btnExport);

    	//Zoom Across Button
        Button btnZoomAcross = new Button();
        btnZoomAcross.setSclass("img-btn");
    	btnZoomAcross.setAttribute("name","btnZoomAcross");
        if (ThemeManager.isUseFontIconForImage())
        	btnZoomAcross.setIconSclass("z-icon-Edit");
        else
        	btnZoomAcross.setImage(ThemeManager.getThemeResource("images/Editor24.png"));//Editor24.png or ZoomAcross24.png
        btnZoomAcross.addEventListener(Events.ON_CLICK, this);
        btnZoomAcross.setId("btnZoomAcross");
        btnZoomAcross.setStyle("vertical-align: middle;");
        if (ThemeManager.isUseFontIconForImage())
        	LayoutUtils.addSclass("large-toolbarbutton", btnZoomAcross);

        div.appendChild(btnZoomAcross);



        createAttachemntBaseWindowGridView(div);



        ZKUpdateUtil.setWidth(this, "560px");
//        ZKUpdateUtil.setHeight(this, "250px");


        this.setWidgetAttribute(AdempiereWebUI.WIDGET_INSTANCE_NAME, "findWindow");
        this.setId("findWindow_"+adWindowContent.getWindowNo());
        LayoutUtils.addSclass("find-window", this);
        this.setZindex(100);

        adWindowContent.getComponent().getParent().appendChild(this);
        adWindowContent.showBusyMask(this);


        ToolBarButton toolbarButton =  adWindowContent.getToolbar().getButton("JPiere Attachment");
        LayoutUtils.openOverlappedWindow(toolbarButton, this, "after_start");

	}

	Grid grid =  null;
	private static final int DEFAULT_PAGE_SIZE = 5;

	private void createAttachemntBaseWindowGridView(Div div)
	{
		ArrayList<MAttachmentFileRecord>  attachmentFileRecordList = MAttachmentFileRecord.getAttachmentFileRecordPO(Env.getCtx(), AD_Table_ID, Record_ID, true, null);
		JPiereAttachmentFileRecordGridTable AFRGridTable= new JPiereAttachmentFileRecordGridTable(attachmentFileRecordList,adWindow);
		JPiereAttachmentFileRecordListModel listModel = new JPiereAttachmentFileRecordListModel(AFRGridTable);

//		listModel.addTableModelListener(this);

		if(attachmentFileRecordList != null)
		{
	        //Grid
			Div divGrid = new Div();
	    	ZKUpdateUtil.setVflex(divGrid, "0");
//	    	divGrid.setStyle("padding: 5px");
	    	divGrid.setStyle("margin: 6px");
	    	this.appendChild(divGrid);

	        grid = new Grid();
	        divGrid.appendChild(grid);

	        grid.setModel(listModel);

	        JPiereAttachmntFileRecordRenderer renderer = new JPiereAttachmntFileRecordRenderer(listModel);

	        grid.setRowRenderer(renderer);
	        grid.setMold("paging");
	        int pageSize = MSysConfig.getIntValue("JPIERE_ATTACHMENT_FILE_RECORD_PAGING_SIZE", DEFAULT_PAGE_SIZE, Env.getAD_Client_ID(Env.getCtx()));
	        grid.setPageSize(pageSize);

		}
	}


	@Override
	public void onClose()
	{
		grid = null;
		if(attachmentWindow != null)
		{
			attachmentWindow.onClose();
		}

		adWindowContent.hideBusyMask();
		super.onClose();
	}


	JPiereAttachmentWindow attachmentWindow = null;

	@Override
	public void onEvent(Event event) throws Exception
	{
		Object target = event.getTarget();

		if(target instanceof Button)
		{
			Button btn = (Button)target;
			if(btn.getId().equals("btnAttachment"))
			{
				EventListener<Event> listener = new EventListener<Event>()
				{
					@Override
					public void onEvent(Event event) throws Exception {
//						toolbar.getButton("Attachment").setPressed(adTabbox.getSelectedGridTab().hasAttachment());
//						focusToActivePanel();
					}
				};

				if(attachmentWindow == null)
				{
					attachmentWindow = new JPiereAttachmentWindow (adWindow, null, listener);
					attachmentWindow.addEventListener(DialogEvents.ON_WINDOW_CLOSE, new EventListener<Event>() {
						@Override
						public void onEvent(Event event) throws Exception
						{
							ArrayList<MAttachmentFileRecord>  attachmentFileRecordList = MAttachmentFileRecord.getAttachmentFileRecordPO(Env.getCtx(), AD_Table_ID, Record_ID, true, null);
							JPiereAttachmentFileRecordGridTable AFRGridTable= new JPiereAttachmentFileRecordGridTable(attachmentFileRecordList, adWindow);
							JPiereAttachmentFileRecordListModel listModel = new JPiereAttachmentFileRecordListModel(AFRGridTable);
							if(grid != null)
							{
								grid.setModel(listModel);
								//grid.renderAll();
							}

						}
					});

					this.getParent().appendChild(attachmentWindow);
				}

				LayoutUtils.openOverlappedWindow(btn, attachmentWindow, "after_pointer");
				attachmentWindow.focus();

				return;

			}else if(btn.getId().equals("btnExport")) {

				;

			}else if(btn.getId().equals("btnZoomAcross")) {

				MQuery query = new MQuery(MAttachmentFileRecord.Table_Name);
				query.addRestriction("AD_Table_ID", MQuery.EQUAL, AD_Table_ID);
				query.addRestriction("Record_ID", MQuery.EQUAL, Record_ID);
				AEnv.zoom(query);

			}
		}


	}

}
