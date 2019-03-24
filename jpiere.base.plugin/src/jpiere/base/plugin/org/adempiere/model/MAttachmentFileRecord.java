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
package jpiere.base.plugin.org.adempiere.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.compiere.model.MClientInfo;
import org.compiere.model.Query;
import org.compiere.util.DB;

import jpiere.base.plugin.webui.action.attachment.IJPiereAttachmentStore;
import jpiere.base.plugin.webui.action.attachment.MJPiereStorageProvider;

/**
*
* JPIERE-0436: JPiere Attachment File
*
*
* @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
*
*/
public class MAttachmentFileRecord extends X_JP_AttachmentFileRecord {

	public MAttachmentFileRecord(Properties ctx, int JP_AttachmentFileRecord_ID, String trxName)
	{
		super(ctx, JP_AttachmentFileRecord_ID, trxName);
		initAttachmentStoreDetails(ctx, trxName);
	}

	public MAttachmentFileRecord(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
		initAttachmentStoreDetails(ctx, trxName);
	}

	//TODO アクセス権限を考慮した実装。
	static public MAttachmentFileRecord[] getAttachmentFileRecord(Properties ctx, int AD_Table_ID, int Record_ID, boolean isCheckRole, String trxName)
	{

		StringBuilder whereClauseFinal = new StringBuilder(MAttachmentFileRecord.COLUMNNAME_AD_Table_ID + "=? AND "
												+ MAttachmentFileRecord.COLUMNNAME_Record_ID + "=? ");

		//
		List<MAttachmentFileRecord> list = new Query(ctx, MAttachmentFileRecord.Table_Name, whereClauseFinal.toString(), trxName)
										.setParameters(AD_Table_ID,Record_ID)
										.list();

		//
		return list.toArray(new MAttachmentFileRecord[list.size()]);

	}

	//TODO アクセス権限を考慮した実装。
	static public ArrayList<MAttachmentFileRecord> getAttachmentFileRecordPO(Properties ctx, int AD_Table_ID, int Record_ID, boolean isCheckRole, String trxName)
	{

		ArrayList<MAttachmentFileRecord> list = new ArrayList<MAttachmentFileRecord>();
		String sql = "SELECT * FROM JP_AttachmentFileRecord WHERE AD_Table_ID=? AND Record_ID=? ORDER BY JP_AttachmentFileRecord_ID";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, trxName);
			pstmt.setInt(1, AD_Table_ID);
			pstmt.setInt(2, Record_ID);
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add(new MAttachmentFileRecord (ctx, rs, trxName));
		}
		catch (Exception e)
		{
//			log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}


		return list;

	}

	private MJPiereStorageProvider storageProvider;
	private IJPiereAttachmentStore attachmentStore;

	private void initAttachmentStoreDetails(Properties ctx, String trxName)
	{
		MClientInfo clientInfo = MClientInfo.get(ctx, getAD_Client_ID());
		storageProvider= new MJPiereStorageProvider(ctx, clientInfo.getAD_StorageProvider_ID(), trxName);
	}


	@Override
	protected boolean afterDelete(boolean success)
	{
		if (attachmentStore == null)
			attachmentStore = storageProvider.getAttachmentStore();

		if (attachmentStore != null)
		{
			return attachmentStore.deleteFile(this, storageProvider);
		}

		return false;
	}


	public boolean upLoadLFile (byte[] data)
	{
		if (attachmentStore == null)
			attachmentStore = storageProvider.getAttachmentStore();

		if (attachmentStore != null)
		{
			return attachmentStore.upLoadFile(this, data, storageProvider);
		}

		return false;
	}

	public String getAbsoluteFilePath()
	{
		if (attachmentStore == null)
			attachmentStore = storageProvider.getAttachmentStore();

		if (attachmentStore != null)
		{
			return attachmentStore.getAbsoluteFilePath(this, storageProvider);
		}

		return null;
	}


}
