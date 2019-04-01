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
import java.io.FileOutputStream;
import java.io.IOException;
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

		StringBuilder folderPath = getDirectoryAbsolutePath(attachmentFileRecord, prov);
		if (folderPath == null) {
			log.severe("no attachmentPath defined");
			return false;
		}

		if (data == null)
			return true;
		if (log.isLoggable(Level.FINE)) log.fine("TextFileSize=" + data.length);
		if (data.length == 0)
			return true;


		final File folder = new File(folderPath.toString());
		if(!folder.exists()){
			if(!folder.mkdirs()){
				log.warning("unable to create folder: " + folder.getPath());
			}
		}

		FileOutputStream fos = null;

		attachmentFileRecord.setJP_AttachmentFilePath(getAttachmentRelativePath(attachmentFileRecord));

		final File destFile = new File(folderPath.append(File.separator).append(attachmentFileRecord.getJP_AttachmentFileName()).toString());
		try
		{
			fos = new FileOutputStream(destFile);
			try {
				fos.write(data);
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}

		attachmentFileRecord.saveEx();

		return true;
	}


	@Override
	public boolean deleteFile(MAttachmentFileRecord attach, MJPiereStorageProvider prov)
	{
		final File deleteFile = new File(getFileAbsolutePath(attach,prov).toString());

		if(!deleteFile.exists())
		{
			return true;
		}

		return deleteFile.delete();
	}

	@Override
	public StringBuilder getFileAbsolutePath(MAttachmentFileRecord attach, MJPiereStorageProvider prov)
	{
		return getDirectoryAbsolutePath(attach,prov).append(File.separator).append(attach.getJP_AttachmentFileName());
	}

	@Override
	public StringBuilder getDirectoryAbsolutePath(MAttachmentFileRecord attachmentFileRecord, MJPiereStorageProvider prov)
	{
		String rootPath = getAttachmentRootRoot(prov);
		if (Util.isEmpty(rootPath)) {
			log.severe("no attachmentPath defined");
			return null;
		}

		String relativePath = getAttachmentRelativePath(attachmentFileRecord);

		return new StringBuilder(rootPath).append(relativePath);
	}


	private String getAttachmentRelativePath(MAttachmentFileRecord attachmentFileRecord)
	{
		String tableName = MTable.getTableName(Env.getCtx(), attachmentFileRecord.getAD_Table_ID());

		StringBuilder msgreturn = new StringBuilder().append(attachmentFileRecord.getAD_Client_ID()).append(File.separator).append(attachmentFileRecord.getAD_Org_ID())
										.append(File.separator).append(tableName).append(File.separator).append(attachmentFileRecord.getRecord_ID());

		return msgreturn.toString();
	}


	private String getAttachmentRootRoot(MJPiereStorageProvider prov)
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
