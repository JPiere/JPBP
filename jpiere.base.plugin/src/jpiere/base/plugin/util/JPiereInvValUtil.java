package jpiere.base.plugin.util;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;

import jpiere.base.plugin.org.adempiere.model.MInvValProfileOrg;

import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;

public class JPiereInvValUtil {

	/**	Static Logger				*/
	private static CLogger		s_log = CLogger.getCLogger (JPiereInvValUtil.class);

	public JPiereInvValUtil() {
		;
	}

	static public BigDecimal getQtyBookFromStockOrg(Properties ctx, Timestamp dateValue, int M_Product_ID, MInvValProfileOrg[] Orgs)
	{

		BigDecimal retValue = null;
		StringBuilder sql = new StringBuilder("SELECT SUM(COALESCE(QtyBook ,0)) ")
			.append("FROM JP_StockOrg ")
			.append("WHERE dateValue=? AND AD_Org_ID IN (");
			for(int i = 0; i < Orgs.length; i++)
			{
				if(i==0)
					sql.append(Orgs[i].getAD_Org_ID());
				else
					sql.append(","+Orgs[i].getAD_Org_ID());
			}
			sql.append(") AND MProduct_ID=? AND AD_Client_ID=?");
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (sql.toString(), null);
			pstmt.setTimestamp(1, dateValue);
			pstmt.setInt (2, M_Product_ID);
			pstmt.setInt (3, Env.getAD_Client_ID(ctx));
			rs = pstmt.executeQuery ();
			if (rs.next ())
				retValue = rs.getBigDecimal(1);
		}
		catch (Exception e)
		{
			s_log.log(Level.SEVERE, sql.toString(), e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

		return retValue;
	}

	static public HashMap<Integer, BigDecimal> getAllQtyBookFromStockOrg(Properties ctx, Timestamp dateValue, MInvValProfileOrg[] Orgs)
	{
		HashMap<Integer, BigDecimal> retValue = new HashMap<Integer, BigDecimal> ();

		StringBuilder sql = new StringBuilder("SELECT M_Product_ID, SUM(COALESCE(QtyBook ,0)) ")
		.append("FROM JP_StockOrg ")
		.append("WHERE dateValue=? AND AD_Org_ID IN (");
		for(int i = 0; i < Orgs.length; i++)
		{
			if(i==0)
				sql.append(Orgs[i].getAD_Org_ID());
			else
				sql.append(","+Orgs[i].getAD_Org_ID());
		}
		sql.append(") AND AD_Client_ID=? GROUP BY M_Product_ID");
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (sql.toString(), null);
			pstmt.setTimestamp(1, dateValue);
			pstmt.setInt (2, Env.getAD_Client_ID(ctx));
			rs = pstmt.executeQuery ();
			while (rs.next ())
				retValue.put(rs.getInt(1), rs.getBigDecimal(2));

		}
		catch (Exception e)
		{
			s_log.log(Level.SEVERE, sql.toString(), e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

		return retValue;
	}
}
