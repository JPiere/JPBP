package jpiere.base.plugin.org.adempiere.model;

import java.sql.ResultSet;
import java.util.Properties;

/**
 * JPIERE-0150
 *
 * @author Hideaki Hagiwara
 *
 */
public class MProductGroupLine extends X_JP_ProductGroupLine {

	private static final long serialVersionUID = 6915547857850228963L;

	public MProductGroupLine(Properties ctx, int JP_ProductGroupLine_ID, String trxName) {
		super(ctx, JP_ProductGroupLine_ID, trxName);
	}

	public MProductGroupLine(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {
		return super.beforeSave(newRecord);
	}



}
