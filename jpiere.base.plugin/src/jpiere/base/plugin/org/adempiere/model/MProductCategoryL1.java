package jpiere.base.plugin.org.adempiere.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.model.MProductCategory;
import org.compiere.util.CCache;
import org.compiere.util.DB;

/**
 * JPIERE-0148
 *
 * @author Hideaki Hagiwara
 *
 */
public class MProductCategoryL1 extends X_JP_ProductCategoryL1 {

	private static final long serialVersionUID = 810760130568853850L;

	public MProductCategoryL1(Properties ctx, int JP_ProductCategoryL1_ID, String trxName) {
		super(ctx, JP_ProductCategoryL1_ID, trxName);
	}

	public MProductCategoryL1(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {
		return super.beforeSave(newRecord);
	}

	/**	Categopry Cache				*/
	private static CCache<Integer,MProductCategoryL1>	s_cache = new CCache<Integer,MProductCategoryL1>(Table_Name, 20);

	/**
	 * 	Get from Cache
	 *	@param ctx context
	 *	@param JP_ProductCategoryL1_ID id
	 *	@return Product Category L1
	 */
	public static MProductCategoryL1 get (Properties ctx, int JP_ProductCategoryL1_ID)
	{
		Integer ii = Integer.valueOf(JP_ProductCategoryL1_ID);
		MProductCategoryL1 retValue = (MProductCategoryL1)s_cache.get(ii);
		if (retValue != null)
			return retValue;
		retValue = new MProductCategoryL1 (ctx, JP_ProductCategoryL1_ID, null);
		if (retValue.get_ID () != 0)
			s_cache.put (JP_ProductCategoryL1_ID, retValue);
		return retValue;
	}	//	get

	private MProductCategory[] m_ProductCategories = null;


	public MProductCategory[] getProductCategories (boolean requery)
	{
		if(m_ProductCategories != null && !requery)
			return m_ProductCategories;

		ArrayList<MProductCategory> list = new ArrayList<MProductCategory>();
		final String sql = "SELECT JP_ProductCategoryL1_ID FROM M_Product_Category WHERE JP_ProductCategoryL1_ID=? AND IsActive='Y'";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, get_ID());
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add(new MProductCategory (getCtx(), rs.getInt(1), get_TrxName()));
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

		m_ProductCategories = new MProductCategory[list.size()];
		list.toArray(m_ProductCategories);
		return m_ProductCategories;
	}


}
