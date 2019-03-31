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
import java.util.Properties;

import org.compiere.model.MClientInfo;
import org.compiere.model.MRole;
import org.compiere.util.DB;
import org.compiere.util.Msg;
import org.compiere.util.Util;

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

	static public ArrayList<MAttachmentFileRecord> getAttachmentFileRecordPO(Properties ctx, int AD_Table_ID, int Record_ID, boolean isCheckRole, String trxName)
	{
		MRole role = MRole.getDefault(ctx, false);
		String orgWhere =role.getOrgWhere(false);

		ArrayList<MAttachmentFileRecord> list = new ArrayList<MAttachmentFileRecord>();
		StringBuilder sql = new StringBuilder("SELECT * FROM JP_AttachmentFileRecord WHERE AD_Table_ID=? AND Record_ID=? AND IsActive='Y'");
		if(!Util.isEmpty(orgWhere))
		{
			sql = sql.append(" AND ").append(orgWhere);

		}

		sql = sql.append(" ORDER BY JP_AttachmentFileRecord_ID");

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql.toString(), trxName);
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
	protected boolean beforeDelete()
	{
		if(!isDeleteable())
		{
			//Could not delte the file;
			log.saveError("Error", Msg.getMsg(getCtx(), "JP_CouldNotDeleteFile"));
			return false;
		}


		return true;
	}

	@Override
	protected boolean afterDelete(boolean success)
	{
		if (attachmentStore == null)
			attachmentStore = storageProvider.getAttachmentStore();

		if (attachmentStore != null)
		{
			boolean isDelete =attachmentStore.deleteFile(this, storageProvider);

			if(!isDelete)
			{
				//Could not delte the file;
				log.saveError("Error", Msg.getMsg(getCtx(), "JP_CouldNotDeleteFile"));
				return false;
			}
		}

		return true;
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

	public static int getID(int Table_ID, int Record_ID)
	{
		String sql="SELECT JP_AttachmentFileRecord_ID FROM JP_AttachmentFileRecord WHERE AD_Table_ID=? AND Record_ID=?";
		int attachid = DB.getSQLValue(null, sql, Table_ID, Record_ID);
		return attachid;
	}


}
