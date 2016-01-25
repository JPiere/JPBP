package jpiere.base.plugin.org.adempiere.model;

import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

import org.compiere.model.Query;
import org.compiere.util.Env;

/**
 * JPIERE-0153
 *
 * @author Hideaki Hagiwara
 *
 */
public class MDeliveryDays extends X_JP_DeliveryDays {

	static MDeliveryDays[] m_deliveryDays = null;

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

	static public int getDeliveryDays(int M_Warehouse_ID, int C_SalesRegion_ID)
	{
		if(m_deliveryDays == null)
		{
			StringBuilder whereClauseFinal = new StringBuilder(MDeliveryDays.COLUMNNAME_AD_Client_ID+"=? ");
			List<MDeliveryDays> list = new Query(Env.getCtx(), MDeliveryDays.Table_Name, whereClauseFinal.toString(), null)
											.setParameters(Env.getAD_Client_ID(Env.getCtx()))
											.setOrderBy("M_Warehouse_ID,C_SalesRegion_ID")
											.list();

			m_deliveryDays = list.toArray(new MDeliveryDays[list.size()]);
		}

		for(int i =0; i < m_deliveryDays.length; i++)
		{
			if(m_deliveryDays[i].getM_Warehouse_ID()==M_Warehouse_ID)
			{
				if(m_deliveryDays[i].getC_SalesRegion_ID()==C_SalesRegion_ID)
				{
					return m_deliveryDays[i].getDeliveryDays();
				}
			}
		}//for

		return 0;
	}
}
