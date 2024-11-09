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
import java.io.FileNotFoundException;
import java.nio.file.Files;

import org.compiere.model.MStorageProvider;
import org.compiere.tools.FileUtil;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.MGenericExpFormat;

/**
 * JPIERE-0628 Generic Export Process
 *
 *  @author Hideaki Hagiwara
 *
 */
public class DefaultGenericExportStorageProvider implements I_GenericExportStorageProvider {

	@Override
	public boolean exportFile(MGenericExpFormat m_MGenericExpFormat, File file) throws Exception 
	{
		
		MStorageProvider sp = MStorageProvider.get(m_MGenericExpFormat.getAD_StorageProvider_ID());
		if(!MStorageProvider.METHOD_FileSystem.equals(sp.getMethod()))
		{
			//Method of Storage Provider need to be File System. 
			throw new Exception(Msg.getMsg(Env.getCtx(), "JP_StorageProviderMethodFileSystem"));
		}
		
		String folder = sp.getFolder();
		if(Util.isEmpty(folder))
		{
			throw new Exception(Msg.getMsg(Env.getCtx(), "JP_Null") + Msg.getElement(Env.getCtx(), MStorageProvider.COLUMNNAME_Folder));
		}
		
		if(!folder.endsWith("\\"))
		{
			folder = sp.getFolder()+"\\";
		}
			
		File exportFile = new File(folder + file.getName());
		if(exportFile.exists())
		{
			try {
				FileUtil.deleteFolderRecursive(exportFile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		Files.copy(file.toPath(), exportFile.toPath());
		
		return true;
	}

}
