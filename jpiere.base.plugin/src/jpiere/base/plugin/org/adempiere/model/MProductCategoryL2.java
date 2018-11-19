package jpiere.base.plugin.org.adempiere.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.util.CCache;
import org.compiere.util.DB;

/**
 * JPIERE-0148
 *
 * @author Hideaki Hagiwara
 *
 */
public class MProductCategoryL2 extends X_JP_ProductCategoryL2 {

	public MProductCategoryL2(Properties ctx, int JP_ProductCategoryL2_ID, String trxName) {
		super(ctx, JP_ProductCategoryL2_ID, trxName);
	}

	public MProductCategoryL2(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {
		return super.beforeSave(newRecord);
	}

	/**	Categopry Cache				*/
	private static CCache<Integer,MProductCategoryL2>	s_cache = new CCache<Integer,MProductCategoryL2>(Table_Name, 20);

	/**
	 * 	Get from Cache
	 *	@param ctx context
	 *	@param JP_ProductCategoryL2_ID id
	 *	@return Product Category L2
	 */
	public static MProductCategoryL2 get (Properties ctx, int JP_ProductCategoryL2_ID)
	{
		Integer ii = Integer.valueOf(JP_ProductCategoryL2_ID);
		MProductCategoryL2 retValue = (MProductCategoryL2)s_cache.get(ii);
		if (retValue != null)
			return retValue;
		retValue = new MProductCategoryL2 (ctx, JP_ProductCategoryL2_ID, null);
		if (retValue.get_ID () != 0)
			s_cache.put (JP_ProductCategoryL2_ID, retValue);
		return retValue;
	}	//	get


	private MProductCategoryL1[] m_ProductCategoryL1s = null;


	public MProductCategoryL1[] getProductCategoryL1s (boolean requery)
	{
		if(m_ProductCategoryL1s != null && !requery)
			return m_ProductCategoryL1s;

		ArrayList<MProductCategoryL1> list = new ArrayList<MProductCategoryL1>();
		final String sql = "SELECT JP_ProductCategoryL2_ID FROM JP_ProductCategoryL1 WHERE JP_ProductCategoryL2_ID=? AND IsActive='Y'";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, get_ID());
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add(new MProductCategoryL1 (getCtx(), rs.getInt(1), get_TrxName()));
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}

		m_ProductCategoryL1s = new MProductCategoryL1[list.size()];
		list.toArray(m_ProductCategoryL1s);
		return m_ProductCategoryL1s;
	}


}
