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

import java.io.File;

import org.adempiere.util.IProcessUI;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
import org.compiere.util.Msg;

import jpiere.base.plugin.org.adempiere.model.MAttachmentFileRecord;


/**
*
* JPIERE-0436: JPiere Attachment File
*
*
* @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
*
*/
public class JPiereAttachmentFileDownload extends SvrProcess {

	private int JP_AttachmentFileRecord_ID = 0;

	@Override
	protected void prepare()
	{
		JP_AttachmentFileRecord_ID = getRecord_ID();

	}

	@Override
	protected String doIt() throws Exception
	{
		IProcessUI processMonitor = Env.getProcessUI(getCtx());

		MAttachmentFileRecord  attachmentFileRecord = new MAttachmentFileRecord(Env.getCtx(),JP_AttachmentFileRecord_ID, null);
		File downloadFile = new File(attachmentFileRecord.getAbsoluteFilePath());

		if(!downloadFile.exists())
		{
			return Msg.getMsg(getCtx(), "AttachmentNotFound");

		}

		if(processMonitor != null)
		{
			processMonitor.download(downloadFile);
		}



		return "";
	}

}
