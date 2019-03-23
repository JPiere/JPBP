package jpiere.base.plugin.org.adempiere.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.compiere.model.Query;
import org.compiere.util.DB;

public class MAttachmentFileRecord extends X_JP_AttachmentFileRecord {

	public MAttachmentFileRecord(Properties ctx, int JP_AttachmentFileRecord_ID, String trxName)
	{
		super(ctx, JP_AttachmentFileRecord_ID, trxName);
	}

	public MAttachmentFileRecord(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
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
}
