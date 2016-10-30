package jpiere.base.plugin.org.adempiere.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import org.compiere.model.I_C_BPartner_Location;
import org.compiere.model.I_C_Location;
import org.compiere.model.MInOut;
import org.compiere.model.MSalesRegion;
import org.compiere.model.MSysConfig;
import org.compiere.model.Query;
import org.compiere.util.DB;
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

		return -1;
	}
	
	static public Timestamp getInvoiceDate(MInOut io, boolean isHolidayNotInspection)
	{
		int deliveryDays = MSysConfig.getIntValue("JP_DEFAULT_INSPECTION_DAYS", 0, io.getAD_Client_ID(), io.getAD_Org_ID());
		
		//Get Location
		I_C_BPartner_Location bpl = null;
		if(io.getDropShip_Location_ID()==0)
			bpl = io.getC_BPartner_Location();
		else
			bpl = io.getDropShip_Location();
		I_C_Location location = bpl.getC_Location();
		
		if(bpl.getC_SalesRegion_ID() > 0)
		{
			MSalesRegion salesRegion = new MSalesRegion(io.getCtx(), bpl.getC_SalesRegion_ID(), io.get_TrxName());
			int dd = MDeliveryDays.getDeliveryDays(io.getM_Warehouse_ID(), salesRegion.getC_SalesRegion_ID());
			if(dd > -1)
				deliveryDays = dd;
		}
		
		
		Timestamp dateInvoiced = io.getDateAcct();
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(dateInvoiced.getTime());
		
		if(isHolidayNotInspection)
		{
			int i = 0;
			do 
			{
				dateInvoiced = new Timestamp(cal.getTimeInMillis());
				if(isNonBusinessDay(io.getAD_Client_ID(), 0 , location == null ? 0 : location.getC_Country_ID(),dateInvoiced))
				{
					cal.add(Calendar.DAY_OF_MONTH, 1);
				}else{
					if(i == 0)
					{
						;//First Day is not contained delivery days
					}else{
						deliveryDays--;
					}

					if(0 < deliveryDays)
						cal.add(Calendar.DAY_OF_MONTH, 1);
				}
				i++;
				
			} while (0 < deliveryDays);
			
			
		}else{

			cal.add(Calendar.DAY_OF_MONTH, deliveryDays);
			dateInvoiced = new Timestamp(cal.getTimeInMillis());
		}
			
		return dateInvoiced;
	}
	

	static public boolean isNonBusinessDay(int AD_Client_ID, int AD_Org_ID, int C_Country_ID,Timestamp date)
	{
//		String sql = "SELECT nbd.C_Calendar_ID from C_NonBusinessDay nbd "
//						+ "INNER JOIN AD_ClientInfo ci ON (nbd.C_Calendar_ID = ci.C_Calendar_ID) "
//						+ " WHERE nbd.AD_Client_ID =? "
//						+ " AND nbd.AD_Org_ID IN (0,?)"
//						+ " AND (nbd.C_Country_ID is null or nbd.C_Country_ID = ?)"
//						+ " AND nbd.Date1 = TO_DATE('? 00:00:00', 'YYYY-MM-DD HH24:MI:SS');";
		
		String sql = "SELECT nbd.C_Calendar_ID from C_NonBusinessDay nbd "
				+ "INNER JOIN AD_ClientInfo ci ON (nbd.C_Calendar_ID = ci.C_Calendar_ID) "
				+ " WHERE nbd.AD_Client_ID = " + AD_Client_ID
				+ " AND nbd.AD_Org_ID IN (0," + AD_Org_ID + ")"
				+ " AND (nbd.C_Country_ID IS NULL OR nbd.C_Country_ID ="+C_Country_ID+")"
				+ " AND nbd.Date1 = TO_DATE('" +date.toString().substring(0, 10) + " 00:00:00', 'YYYY-MM-DD HH24:MI:SS');";
		
		boolean isNonBusinessDay = false;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, null);
//			pstmt.setInt(1, AD_Client_ID);
//			pstmt.setInt(2, AD_Org_ID);
//			pstmt.setInt(3, C_Country_ID);
//			pstmt.setString(4, date.toString().substring(0, 10));
			rs = pstmt.executeQuery();
			if (rs.next())
				isNonBusinessDay =  true;
		}
		catch (Exception e)
		{
			isNonBusinessDay = false;
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}
		
		return isNonBusinessDay;
	}
}
