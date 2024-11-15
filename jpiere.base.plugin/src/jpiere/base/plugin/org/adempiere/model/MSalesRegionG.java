package jpiere.base.plugin.org.adempiere.model;

import java.sql.ResultSet;
import java.util.Properties;

/**
 * JPIERE-0152
 *
 * @author Hideaki Hagiwara
 *
 */
public class MSalesRegionG extends X_JP_SalesRegionG{

	private static final long serialVersionUID = 1622262602974748146L;

	public MSalesRegionG(Properties ctx, int JP_SalesRegionG_ID, String trxName) {
		super(ctx, JP_SalesRegionG_ID, trxName);
	}

	public MSalesRegionG(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {
		return super.beforeSave(newRecord);
	}



}
