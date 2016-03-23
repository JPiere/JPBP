package jpiere.base.plugin.util;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import jpiere.base.plugin.org.adempiere.model.MInvValProfileOrg;

import org.compiere.model.MCostElement;
import org.compiere.model.MInOutLine;
import org.compiere.model.MOrderLine;
import org.compiere.model.Query;
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

	/**
	 * 	Get Material Standard Cost Element
	 *
	 *	@param ctx
	 *	@return cost element
	 */
	public static MCostElement[] getMaterialStandardCostElements (Properties ctx)
	{
		//
		final String whereClause = "AD_Client_ID=? AND CostingMethod='S' AND CostElementType='M'";
		List<MCostElement> list = new Query(ctx, MCostElement.Table_Name, whereClause, null)
			.setParameters(Env.getAD_Client_ID(ctx))
			.setOrderBy("AD_Org_ID")
			.list();

		return list.toArray(new MCostElement[list.size()]);
	}	//	getStandardMaterialCostElements


	public static MInOutLine[] getMInOutLine(Properties ctx, int M_Product_ID, Timestamp fromDate, Timestamp toDate, MInvValProfileOrg[] Orgs, String OrderClause)
	{
		StringBuilder DateValueFrom = null;
		StringBuilder DateValueTo = null;
		ArrayList<MInOutLine> list = new ArrayList<MInOutLine> ();

		StringBuilder sql = new StringBuilder("SELECT iol.* FROM M_InOut io ")
								.append("INNER JOIN M_InOutLine iol ON (io.M_InOut_ID = iol.M_InOut_ID) ")
								.append("INNER JOIN C_DocType dt ON (dt.C_DocType_ID = io.C_DocType_ID) ")
								.append("WHERE io.IsSOTrx='N' AND io.DocStatus in ('CO','CL') AND dt.DocBaseType='MMR' AND iol.M_Product_ID=?");
		if(Orgs != null && Orgs.length != 0)
		{
			sql.append(" AND io.AD_Org_ID IN (");
					for(int i = 0; i < Orgs.length; i++)
					{
						if(i==0)
							sql.append(Orgs[i].getAD_Org_ID());
						else
							sql.append(","+Orgs[i].getAD_Org_ID());
					}
			sql.append(")");
		}

		if(fromDate != null && toDate != null)
		{
			DateValueFrom = new StringBuilder(fromDate.toString());
			DateValueTo = new StringBuilder(toDate.toString());

			DateValueFrom = new StringBuilder("TO_DATE('").append(DateValueFrom.substring(0,10)).append(" 00:00:00','YYYY-MM-DD HH24:MI:SS')");
			DateValueTo = new StringBuilder("TO_DATE('").append(DateValueTo.substring(0,10)).append(" 24:00:00','YYYY-MM-DD HH24:MI:SS')");

			sql.append(" AND io.MovementDate >=").append(DateValueFrom).append(" AND io.MovementDate < ").append(DateValueTo);

		}else if(fromDate != null){
			DateValueFrom = new StringBuilder(fromDate.toString());
			DateValueFrom = new StringBuilder("TO_DATE('").append(DateValueFrom.substring(0,10)).append(" 00:00:00','YYYY-MM-DD HH24:MI:SS')");
			sql.append(" AND io.MovementDate >=").append(DateValueFrom);

		}else if(toDate != null){
			DateValueTo = new StringBuilder(toDate.toString());
			DateValueTo = new StringBuilder("TO_DATE('").append(DateValueTo.substring(0,10)).append(" 24:00:00','YYYY-MM-DD HH24:MI:SS')");
			sql.append(" AND io.MovementDate < ").append(DateValueTo);
		}

		if(Util.isEmpty(OrderClause))
		{
			sql.append(" ORDER BY io.MovementDate");
		}else{
			sql.append(" ORDER BY ").append(OrderClause);
		}
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (sql.toString(), null);
			pstmt.setInt(1, M_Product_ID);
			rs = pstmt.executeQuery ();
			while (rs.next ())
				list.add(new MInOutLine(ctx, rs, null));

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

		return  list.toArray(new MInOutLine[list.size()]);
	}

	/**
	 * get Order Lines From InOut Line
	 *
	 * @param ctx
	 * @param M_InOutLine_ID
	 * @param OrderClause
	 * @return
	 */
	public static MOrderLine[] getMOrderLines(Properties ctx, int M_InOutLine_ID, String OrderClause)
	{
		ArrayList<MOrderLine> list = new ArrayList<MOrderLine> ();

		StringBuilder sql = new StringBuilder("SELECT DISTINCT ol.* FROM C_OrderLine ol  ")
								.append("INNER JOIN C_Order o ON (o.C_Order_ID = ol.C_Order_ID) ")
								.append("INNER JOIN M_MatchPO mp ON (ol.C_OrderLine_ID = mp.C_OrderLine_ID) ")
								.append("INNER JOIN M_InOutLine iol ON (mp.M_InOutLine_ID = iol.M_InOutLine_ID) ")
								.append("WHERE o.DocStatus in ('CO','CL') AND iol.M_InOutLine_ID = ?");
//		if(Util.isEmpty(OrderClause))
//		{
//			sql.append(" ORDER BY ol.DateOrdered");
//		}else{
//			sql.append(" ORDER BY ").append(OrderClause);
//		}

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (sql.toString(), null);
			pstmt.setInt(1, M_InOutLine_ID);
			rs = pstmt.executeQuery ();
			while (rs.next ())
				list.add(new MOrderLine(ctx, rs, null));

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

		return  list.toArray(new MOrderLine[list.size()]);
	}
}
