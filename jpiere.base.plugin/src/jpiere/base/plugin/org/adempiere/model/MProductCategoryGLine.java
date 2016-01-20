package jpiere.base.plugin.org.adempiere.model;

import java.sql.ResultSet;
import java.util.Properties;

/**
 * JPIERE-0149
 *
 * @author Hideaki Hagiwara
 *
 */
public class MProductCategoryGLine extends X_JP_ProductCategoryGLine {

	public MProductCategoryGLine(Properties ctx, int JP_ProductCategoryGLine_ID, String trxName) {
		super(ctx, JP_ProductCategoryGLine_ID, trxName);
	}

	public MProductCategoryGLine(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {
		return super.beforeSave(newRecord);
	}



}
