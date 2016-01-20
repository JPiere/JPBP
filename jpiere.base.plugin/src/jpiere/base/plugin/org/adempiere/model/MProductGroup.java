package jpiere.base.plugin.org.adempiere.model;

import java.sql.ResultSet;
import java.util.Properties;

/**
 * JPIERE-0150
 *
 * @author Hideaki Hagiwara
 *
 */
public class MProductGroup extends X_JP_ProductGroup {

	public MProductGroup(Properties ctx, int JP_ProductGroup_ID, String trxName) {
		super(ctx, JP_ProductGroup_ID, trxName);
	}

	public MProductGroup(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {
		return super.beforeSave(newRecord);
	}



}
