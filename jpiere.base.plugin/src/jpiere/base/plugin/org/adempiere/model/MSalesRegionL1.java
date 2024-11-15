package jpiere.base.plugin.org.adempiere.model;

import java.sql.ResultSet;
import java.util.Properties;

/**
 * JPIERE-0151
 *
 * @author Hideaki Hagiwara
 *
 */
public class MSalesRegionL1 extends X_JP_SalesRegionL1 {

	private static final long serialVersionUID = 7265363294504401109L;

	public MSalesRegionL1(Properties ctx, int JP_SalesRegionL1_ID, String trxName) {
		super(ctx, JP_SalesRegionL1_ID, trxName);
	}

	public MSalesRegionL1(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {
		return super.beforeSave(newRecord);
	}



}
