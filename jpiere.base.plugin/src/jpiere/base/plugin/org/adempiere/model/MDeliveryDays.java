package jpiere.base.plugin.org.adempiere.model;

import java.sql.ResultSet;
import java.util.Properties;

/**
 * JPIERE-0153
 *
 * @author Hideaki Hagiwara
 *
 */
public class MDeliveryDays extends X_JP_DeliveryDays {

	public MDeliveryDays(Properties ctx, int JP_DeliveryDays_ID, String trxName) {
		super(ctx, JP_DeliveryDays_ID, trxName);
	}

	public MDeliveryDays(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {
		return super.beforeSave(newRecord);
	}



}
