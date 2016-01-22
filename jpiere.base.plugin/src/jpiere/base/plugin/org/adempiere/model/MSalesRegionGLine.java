package jpiere.base.plugin.org.adempiere.model;

import java.sql.ResultSet;
import java.util.Properties;

/**
 * JPIERE-0152
 *
 * @author Hideaki Hagiwara
 *
 */
public class MSalesRegionGLine extends X_JP_SalesRegionGLine{

	public MSalesRegionGLine(Properties ctx, int JP_SalesRegionGLine_ID, String trxName) {
		super(ctx, JP_SalesRegionGLine_ID, trxName);
	}

	public MSalesRegionGLine(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {
		return super.beforeSave(newRecord);
	}



}
