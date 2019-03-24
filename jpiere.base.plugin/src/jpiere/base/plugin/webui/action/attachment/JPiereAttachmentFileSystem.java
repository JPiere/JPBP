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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.logging.Level;

import org.compiere.model.MTable;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.MAttachmentFileRecord;

/**
*
* JPIERE-0436: JPiere Attachment File
*
*
* @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
*
*/
public class JPiereAttachmentFileSystem implements IJPiereAttachmentStore {

	private final CLogger log = CLogger.getCLogger(getClass());


	@Override
	public boolean upLoadFile(MAttachmentFileRecord attachmentFileRecord, byte[] data, MJPiereStorageProvider prov)
	{

		StringBuilder path = getAbsolutePath(attachmentFileRecord, prov);
		if (path == null) {
			log.severe("no attachmentPath defined");
			return false;
		}



		if (data == null)
			return true;
		if (log.isLoggable(Level.FINE)) log.fine("TextFileSize=" + data.length);
		if (data.length == 0)
			return true;


		final File destFolder = new File(path.toString());
		if(!destFolder.exists()){
			if(!destFolder.mkdirs()){
				log.warning("unable to create folder: " + destFolder.getPath());
			}
		}

		FileChannel in = null;
		FileChannel out = null;
		FileInputStream fis = null;
		FileOutputStream fos = null;

		attachmentFileRecord.setJP_AttachmentFilePath(getAttachmentPathSnippet(attachmentFileRecord));

		final File destFile = new File(path.append(File.separator).append(attachmentFileRecord.getJP_AttachmentFileName()).toString());
		try
		{
			fos = new FileOutputStream(destFile);
			try {
				fos.write(data);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
//			out = fos.getChannel();
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}

		/* IDEMPIERE-2864
		if(entryFile.exists()){
			if(!entryFile.delete()){
				entryFile.deleteOnExit();
			}
		}*/
//		entryFile = destFile;

		attachmentFileRecord.saveEx();

		return true;
	}


	@Override
	public boolean deleteFile(MAttachmentFileRecord attach, MJPiereStorageProvider prov)
	{
		final File destFile = new File(getAbsoluteFilePath(attach,prov));
		return destFile.delete();
	}

	@Override
	public String getAbsoluteFilePath(MAttachmentFileRecord attach, MJPiereStorageProvider prov)
	{
		StringBuilder msgfile = getAbsolutePath(attach,prov);

		return msgfile.append(File.separator).append(attach.getJP_AttachmentFileName()).toString();
	}

	private StringBuilder getAbsolutePath(MAttachmentFileRecord attachmentFileRecord, MJPiereStorageProvider prov)
	{
		String attachmentPathRoot = getAttachmentPathRoot(prov);
		if (Util.isEmpty(attachmentPathRoot)) {
			log.severe("no attachmentPath defined");
			return null;
		}

		String attachmentPathSnippet = getAttachmentPathSnippet(attachmentFileRecord);

		StringBuilder msgfile = new StringBuilder().append(attachmentPathRoot).append(attachmentPathSnippet);

		return msgfile;
	}

	private String getAttachmentPathSnippet(MAttachmentFileRecord attachmentFileRecord)
	{
		String tableName = MTable.getTableName(Env.getCtx(), attachmentFileRecord.getAD_Table_ID());

		StringBuilder msgreturn = new StringBuilder().append(tableName).append(File.separator).append(attachmentFileRecord.getAD_Client_ID()).append(File.separator)
				.append(attachmentFileRecord.getAD_Org_ID()).append(File.separator).append(attachmentFileRecord.getRecord_ID());

		return msgreturn.toString();
	}


	private String getAttachmentPathRoot(MJPiereStorageProvider prov)
	{
		String attachmentPathRoot = prov.getFolder();
		if (attachmentPathRoot == null)
			attachmentPathRoot = "";
		if (Util.isEmpty(attachmentPathRoot)) {
			log.severe("no attachmentPath defined");
		} else if (!attachmentPathRoot.endsWith(File.separator)){
			attachmentPathRoot = attachmentPathRoot + File.separator;
			log.fine(attachmentPathRoot);
		}
		return attachmentPathRoot;
	}





}
