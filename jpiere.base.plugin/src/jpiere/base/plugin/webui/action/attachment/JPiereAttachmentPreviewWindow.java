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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.adempiere.webui.AdempiereWebUI;
import org.adempiere.webui.ClientInfo;
import org.adempiere.webui.adwindow.ADWindow;
import org.adempiere.webui.adwindow.ADWindowContent;
import org.adempiere.webui.adwindow.IADTabbox;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.Window;
import org.adempiere.webui.event.DialogEvents;
import org.adempiere.webui.theme.ThemeManager;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.adempiere.webui.window.FDialog;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Center;
import org.zkoss.zul.Iframe;

import jpiere.base.plugin.org.adempiere.model.MAttachmentFileRecord;


/**
*
* JPIERE-0436: JPiere Attachment File
*
*
* @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
*
*/
public class JPiereAttachmentPreviewWindow extends Window implements EventListener<Event>
{
	private static final long serialVersionUID = 4311076973993361653L;

	private static CLogger log = CLogger.getCLogger(JPiereAttachmentPreviewWindow.class);

	private Iframe preview = new Iframe();

	private Panel previewPanel = new Panel();

	private Borderlayout mainPanel = new Borderlayout();

	private String orientation;

	private static List<String> autoPreviewList;

	private ADWindow adWindow;
	private ADWindowContent  adWindowContent;
	private IADTabbox adTabbox;
	private int AD_Table_ID = 0;
	private int Record_ID = 0;
	private int windowNo = 0;
	private MAttachmentFileRecord attachmentFileRecord;

	private boolean isFileLoad = false;

	static {
		autoPreviewList = new ArrayList<String>();
		autoPreviewList.add("image/jpeg");
		autoPreviewList.add("image/png");
		autoPreviewList.add("image/gif");
		autoPreviewList.add("text/plain");
		autoPreviewList.add("application/pdf");
		autoPreviewList.add("text/html");
	}


	/**
	 *	Constructor.
	 *	loads Attachment, if ID <> 0
	 *  @param WindowNo window no
	 *  @param AD_Attachment_ID attachment
	 *  @param AD_Table_ID table
	 *  @param Record_ID record key
	 *  @param trxName transaction
	 */

	public JPiereAttachmentPreviewWindow(ADWindow adWindow, MAttachmentFileRecord attachmentFileRecord, EventListener<Event> eventListener)
	{
		super();
		this.adWindow = adWindow;
		this.adWindowContent = adWindow.getADWindowContent();
		this.adTabbox = adWindowContent.getADTab();
		this.AD_Table_ID =adTabbox.getSelectedGridTab().getAD_Table_ID();
		this.Record_ID =  adTabbox.getSelectedGridTab().getRecord_ID();
		this.windowNo = adWindowContent.getWindowNo();
		this.attachmentFileRecord = attachmentFileRecord;

		this.addEventListener(DialogEvents.ON_WINDOW_CLOSE, this);
		if (eventListener != null)
		{
			this.addEventListener(DialogEvents.ON_WINDOW_CLOSE, eventListener);
		}

		try
		{
			staticInit();
		}
		catch (Exception ex)
		{
			log.log(Level.SEVERE, "", ex);
		}

		try
		{
			setAttribute(Window.MODE_KEY, Window.MODE_HIGHLIGHTED);
			//AEnv.showWindow(this);
		}
		catch (Exception e)
		{
		}

	}

	void staticInit() throws Exception
	{
		this.setAttribute(AdempiereWebUI.WIDGET_INSTANCE_NAME, "attachment");
		this.setMaximizable(true);
		if (!ThemeManager.isUseCSSForWindowSize())
		{
			ZKUpdateUtil.setWindowWidthX(this, 700);
			ZKUpdateUtil.setHeight(this, "85%");
		}
		else
		{
			addCallback(AFTER_PAGE_ATTACHED, t -> {
				ZKUpdateUtil.setCSSHeight(this);
				ZKUpdateUtil.setCSSWidth(this);
			});
		}
		this.setTitle(Msg.getMsg(Env.getCtx(), "Attachment"));
		this.setClosable(true);
		this.setSizable(true);
		this.setBorder("normal");
		this.setSclass("popup-dialog attachment-dialog");
		this.setShadow(true);
		this.appendChild(mainPanel);
		ZKUpdateUtil.setHeight(mainPanel, "100%");
		ZKUpdateUtil.setWidth(mainPanel, "100%");

		previewPanel.appendChild(preview);
		ZKUpdateUtil.setVflex(preview, "1");
		ZKUpdateUtil.setHflex(preview, "1");


		File file = new File(attachmentFileRecord.getAbsoluteFilePath());
		AMedia media = null;
		try {
			media = new AMedia(attachmentFileRecord.getJP_AttachmentFileName(),attachmentFileRecord.getJP_MediaFormat()
					,attachmentFileRecord.getJP_MediaContentType(),file,true);

		}catch (FileNotFoundException e) {

			FDialog.error(windowNo, this, "AttachmentNotFound", e.toString());
			dispose();
			log.saveError("Error", e);

		}catch (Exception e) {

			FDialog.error(windowNo, this, "Error", e.toString());
			dispose();
			log.saveError("Error", e);
		}

		if(media == null)
			return ;

		isFileLoad = true;

		preview.setContent(media);
		preview.setVisible(true);
		preview.invalidate();
//		preview.setStyle("position:center");
//		previewPanel.setStyle("position:center");
//		mainPanel.setStyle("position:center");

		Center centerPane = new Center();
		centerPane.setSclass("dialog-content");
		//centerPane.setAutoscroll(true); // not required the preview has its own scroll bar
		mainPanel.appendChild(centerPane);
		centerPane.appendChild(previewPanel);
		ZKUpdateUtil.setVflex(previewPanel, "1");
		ZKUpdateUtil.setHflex(previewPanel, "1");

		if (ClientInfo.isMobile())
		{
			orientation = ClientInfo.get().orientation;
			ClientInfo.onClientInfo(this, this::onClientInfo);
		}
	}

	protected void onClientInfo()
	{
		if (getPage() != null)
		{
			String newOrienation = ClientInfo.get().orientation;
			if (!newOrienation.equals(orientation))
			{
				orientation = newOrienation;
				ZKUpdateUtil.setCSSHeight(this);
				ZKUpdateUtil.setCSSWidth(this);
				invalidate();
			}
		}
	}

	/**
	 * 	Dispose
	 */

	public void dispose ()
	{
		preview = null;
		this.detach();
	} // dispose


	public boolean isFileLoad()
	{
		return isFileLoad;
	}

	public void onEvent(Event e)
	{
		//	Save and Close
		if (DialogEvents.ON_WINDOW_CLOSE.equals(e.getName()))
		{
			dispose();
		}

	}	//	onEvent




	static private String getCharset(String contentType) {
		if (contentType != null) {
			int j = contentType.indexOf("charset=");
			if (j >= 0) {
				String cs = contentType.substring(j + 8).trim();
				if (cs.length() > 0) return cs;
			}
		}
		return "UTF-8";
	}
}
