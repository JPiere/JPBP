package jpiere.base.plugin.org.adempiere.model;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.model.MAttachmentEntry;
import org.compiere.model.MClientInfo;
import org.compiere.model.Query;
import org.compiere.util.DB;

import jpiere.base.plugin.webui.action.attachment.IJPiereAttachmentStore;
import jpiere.base.plugin.webui.action.attachment.MJPiereStorageProvider;

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

	/**	List of Entry Data		*/
	public ArrayList<MAttachmentEntry> m_items = null;

//	/**
//	 * 	Get Attachment Entry
//	 * 	@param index index of the item
//	 * 	@return Entry or null
//	 */
//	public MAttachmentEntry getEntry (int index)
//	{
//		if (m_items == null)
//			loadLOBData();
//		if (index < 0 || index >= m_items.size())
//			return null;
//		return (MAttachmentEntry)m_items.get(index);
//	}	//	getEntry

	private MJPiereStorageProvider provider;
	private void initAttachmentStoreDetails(Properties ctx, String trxName)
	{
		MClientInfo clientInfo = MClientInfo.get(ctx, getAD_Client_ID());
		provider= new MJPiereStorageProvider(ctx, clientInfo.getAD_StorageProvider_ID(), trxName);
	}



	@Override
	protected boolean afterDelete(boolean success)
	{
		IJPiereAttachmentStore prov = provider.getAttachmentStore();
		if (prov != null)
		{
			return prov.deleteFile(this, provider);
		}

		return false;
	}

	/**
	 * 	Load Data into local m_data
	 *	@return true if success
	 */
	public boolean upLoadLFile (byte[] data)
	{
		IJPiereAttachmentStore prov = provider.getAttachmentStore();
		if (prov != null)
		{
			return prov.upLoadFile(this, data, provider);
		}

		return false;
	}

	public boolean addEntry (File file)
	{

		if (file == null)
		{
			log.warning("No File");
			return false;
		}
		if (!file.exists() || file.isDirectory() || !file.canRead())
		{
			log.warning("not added - " + file
				+ ", Exists=" + file.exists() + ", Directory=" + file.isDirectory());
			return false;
		}
		if (log.isLoggable(Level.FINE)) log.fine("addEntry - " + file);
		//
		String name = file.getName();
		byte[] data = null;

		// F3P: BF [2992291] modified to be able to close streams in "finally" block

		FileInputStream fis = null;
		ByteArrayOutputStream os = null;

		try
		{
			fis = new FileInputStream (file);
			os = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024*8];   //  8kB
			int length = -1;
			while ((length = fis.read(buffer)) != -1)
				os.write(buffer, 0, length);

			data = os.toByteArray();

		}
		catch (IOException ioe)
		{
			log.log(Level.SEVERE, "(file)", ioe);
		}
		finally
		{
			if(fis != null)
			{
				try { fis.close(); } catch (IOException ex) { log.log(Level.SEVERE, "(file)", ex); };
			}

			if(os != null)
			{
				try { os.close(); } catch (IOException ex) { log.log(Level.SEVERE, "(file)", ex); };
			}
		}


		return upLoadLFile(data);
	}	//	addEntry


}
