package jpiere.base.plugin.util;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.logging.Level;

import jpiere.base.plugin.org.adempiere.model.MInvValProfileOrg;

import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Util;

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

	static public LinkedHashMap<Integer, BigDecimal> getAllQtyBookFromStockOrg(Properties ctx, Timestamp dateValue, MInvValProfileOrg[] Orgs, String OrderClause)
	{
		LinkedHashMap<Integer, BigDecimal> retValue = new LinkedHashMap<Integer, BigDecimal> ();

		StringBuilder sql = new StringBuilder("SELECT s.M_Product_ID, SUM(COALESCE(s.QtyBook ,0)) ")
		.append("FROM JP_StockOrg s INNER JOIN M_Product p ON (s.M_Product_ID=p.M_Product_ID) ")
		.append("WHERE s.dateValue=? AND s.AD_Org_ID IN (");
		for(int i = 0; i < Orgs.length; i++)
		{
			if(i==0)
				sql.append(Orgs[i].getAD_Org_ID());
			else
				sql.append(","+Orgs[i].getAD_Org_ID());
		}
		sql.append(") AND s.AD_Client_ID=? GROUP BY s.M_Product_ID ");
		if(Util.isEmpty(OrderClause))
		{
			sql.append(",p.Value ORDER BY p.Value");
		}else{
			sql.append("," + OrderClause).append(" ORDER BY ").append(OrderClause);
		}
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

	static public BigDecimal calculateTotalLines(Properties ctx, int JP_InvValCal_ID, String trxName)
	{
		BigDecimal retValue = null;
		StringBuilder sql = new StringBuilder("SELECT SUM(COALESCE(JP_InvValTotalAmt ,0)) ")
		.append("FROM JP_InvValCalLine ")
		.append("WHERE JP_InvValCal_ID=? ");
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (sql.toString(), trxName);
			pstmt.setInt (1, JP_InvValCal_ID);
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
}
