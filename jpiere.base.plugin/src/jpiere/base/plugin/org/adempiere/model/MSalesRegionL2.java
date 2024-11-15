package jpiere.base.plugin.org.adempiere.model;

import java.sql.ResultSet;
import java.util.Properties;

/**
 * JPIERE-0151
 *
 * @author Hideaki Hagiwara
 *
 */
public class MSalesRegionL2 extends X_JP_SalesRegionL2 {

	private static final long serialVersionUID = -6552287459254664339L;

	public MSalesRegionL2(Properties ctx, int JP_SalesRegionL2_ID, String trxName) {
		super(ctx, JP_SalesRegionL2_ID, trxName);
	}

	public MSalesRegionL2(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {
		return super.beforeSave(newRecord);
	}



}
