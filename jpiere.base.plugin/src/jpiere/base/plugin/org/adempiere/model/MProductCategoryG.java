package jpiere.base.plugin.org.adempiere.model;

import java.sql.ResultSet;
import java.util.Properties;

/**
 * JPIERE-0149
 *
 * @author Hideaki Hagiwara
 *
 */
public class MProductCategoryG extends X_JP_ProductCategoryG {

	private static final long serialVersionUID = 7124426999938620679L;

	public MProductCategoryG(Properties ctx, int JP_ProductCategoryG_ID, String trxName) {
		super(ctx, JP_ProductCategoryG_ID, trxName);
	}

	public MProductCategoryG(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {
		return super.beforeSave(newRecord);
	}



}
