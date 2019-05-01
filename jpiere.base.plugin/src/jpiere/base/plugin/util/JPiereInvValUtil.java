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

import org.compiere.model.I_C_InvoiceLine;
import org.compiere.model.I_C_OrderLine;
import org.compiere.model.I_M_MatchInv;
import org.compiere.model.I_M_PriceList_Version;
import org.compiere.model.MCostElement;
import org.compiere.model.MDocType;
import org.compiere.model.MInOutLine;
import org.compiere.model.MMatchInv;
import org.compiere.model.MMatchPO;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MPriceListVersion;
import org.compiere.model.Query;
import org.compiere.util.CCache;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.MInvValCalLog;
import jpiere.base.plugin.org.adempiere.model.MInvValProfileOrg;

public class JPiereInvValUtil {

	/**	Static Logger				*/
	private static CLogger		s_log = CLogger.getCLogger (JPiereInvValUtil.class);

	public JPiereInvValUtil() {
		;
	}

	static public BigDecimal getQtyBookFromStockOrg(Properties ctx, Timestamp dateValue, int M_Product_ID, int AD_Org_ID)
	{

		BigDecimal retValue = Env.ZERO;
		StringBuilder sql = new StringBuilder("SELECT QtyBook ")
			.append("FROM JP_StockOrg ")
			.append("WHERE dateValue=? AND AD_Org_ID=?")
			.append(" AND M_Product_ID=? AND AD_Client_ID=?");
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (sql.toString(), null);
			pstmt.setTimestamp(1, dateValue);
			pstmt.setInt (2, AD_Org_ID);
			pstmt.setInt (3, M_Product_ID);
			pstmt.setInt (4, Env.getAD_Client_ID(ctx));
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
			sql.append(") AND M_Product_ID=? AND AD_Client_ID=?");
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
		.append(" WHERE s.dateValue=? ");

		if(Orgs!=null && Orgs.length > 0)
		{
			sql.append(" AND s.AD_Org_ID IN (");
			for(int i = 0; i < Orgs.length; i++)
			{
				if(i==0)
					sql.append(Orgs[i].getAD_Org_ID());
				else
					sql.append(","+Orgs[i].getAD_Org_ID());
			}
			sql.append(")");
		}

		sql.append(" AND s.AD_Client_ID=? GROUP BY s.M_Product_ID ");
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

	static public BigDecimal calculateTotalLines(Properties ctx, String TableName, String ColumnName, int Record_ID, String trxName)
	{
		BigDecimal retValue = null;
		StringBuilder sql = new StringBuilder("SELECT SUM(COALESCE(JP_InvValTotalAmt ,0)) ")
		.append("FROM "+ TableName)
		.append(" WHERE "+ ColumnName +" =?");
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (sql.toString(), trxName);
			pstmt.setInt (1, Record_ID);
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

	static public BigDecimal calculateTotals(Properties ctx, String totalColumn, String TableName, String KeyColumnName, int Record_ID, String trxName)
	{
		BigDecimal retValue = null;
		StringBuilder sql = new StringBuilder("SELECT SUM(COALESCE("+ totalColumn +",0)) ")
		.append("FROM "+ TableName)
		.append(" WHERE "+ KeyColumnName +" =?");
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (sql.toString(), trxName);
			pstmt.setInt (1, Record_ID);
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


	public static MInOutLine[] getInOutLines(Properties ctx, int M_Product_ID, Timestamp lastDateValue, Timestamp dateValue, MInvValProfileOrg[] Orgs, String OrderClause)
	{
		StringBuilder DateValueFrom = null;
		StringBuilder DateValueTo = null;
		ArrayList<MInOutLine> list = new ArrayList<MInOutLine> ();

		StringBuilder sql = new StringBuilder("SELECT iol.* FROM M_InOut io ")
								.append("INNER JOIN M_InOutLine iol ON (io.M_InOut_ID = iol.M_InOut_ID) ")
								.append("INNER JOIN C_DocType dt ON (dt.C_DocType_ID = io.C_DocType_ID) ")
								.append("WHERE io.IsSOTrx='N' AND io.DocStatus in ('CO','CL') AND dt.DocBaseType in ('MMR','MMS') AND iol.M_Product_ID=?");
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

		if(lastDateValue != null && dateValue != null)
		{
			DateValueFrom = new StringBuilder(lastDateValue.toString());
			DateValueTo = new StringBuilder(dateValue.toString());

			DateValueFrom = new StringBuilder("TO_DATE('").append(DateValueFrom.substring(0,10)).append(" 24:00:00','YYYY-MM-DD HH24:MI:SS')");//Except lastDateValue
			DateValueTo = new StringBuilder("TO_DATE('").append(DateValueTo.substring(0,10)).append(" 24:00:00','YYYY-MM-DD HH24:MI:SS')");//Include all DateValue

			sql.append(" AND io.MovementDate >=").append(DateValueFrom).append(" AND io.MovementDate < ").append(DateValueTo);

		}else if(lastDateValue != null){
			DateValueFrom = new StringBuilder(lastDateValue.toString());
			DateValueFrom = new StringBuilder("TO_DATE('").append(DateValueFrom.substring(0,10)).append(" 24:00:00','YYYY-MM-DD HH24:MI:SS')");//Except lastDateValue
			sql.append(" AND io.MovementDate >=").append(DateValueFrom);

		}else if(dateValue != null){
			DateValueTo = new StringBuilder(dateValue.toString());
			DateValueTo = new StringBuilder("TO_DATE('").append(DateValueTo.substring(0,10)).append(" 24:00:00','YYYY-MM-DD HH24:MI:SS')");//Include all DateValue
			sql.append(" AND io.MovementDate < ").append(DateValueTo);
		}

		if(Util.isEmpty(OrderClause))
		{
			sql.append(" ORDER BY io.MovementDate, io.DocumentNo, io.M_InOut_ID, iol.Line, iol.M_InOutLine_ID");
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
	public static MOrderLine[] getOrderLinesByInOutLine(Properties ctx, int M_InOutLine_ID, boolean isDateOrderedASC)
	{
		ArrayList<MOrderLine> list = new ArrayList<MOrderLine> ();

		StringBuilder sql = new StringBuilder("SELECT DISTINCT ol.*, ol.DateOrdered FROM C_OrderLine ol  ")
								.append("INNER JOIN C_Order o ON (o.C_Order_ID = ol.C_Order_ID) ")
								.append("INNER JOIN M_MatchPO mp ON (ol.C_OrderLine_ID = mp.C_OrderLine_ID) ")
								.append("INNER JOIN M_InOutLine iol ON (mp.M_InOutLine_ID = iol.M_InOutLine_ID) ")
								.append("WHERE o.DocStatus in ('CO','CL') AND iol.M_InOutLine_ID = ?");
		if(isDateOrderedASC)
		{
			sql.append(" ORDER BY ol.DateOrdered ASC");
		}else{
			sql.append(" ORDER BY ol.DateOrdered DESC");
		}

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

	static public void copyInfoFromOrderLineToLog(MInvValCalLog log, I_C_OrderLine orderLine)
	{
		log.setC_OrderLine_ID(orderLine.getC_OrderLine_ID());
		log.setDateOrdered(orderLine.getDateOrdered());
		log.setIsTaxIncluded(orderLine.getC_Order().isTaxIncluded());
		log.setM_PriceList_ID(orderLine.getC_Order().getM_PriceList_ID());
		log.setC_Currency_ID(orderLine.getC_Order().getC_Currency_ID());
		log.setC_ConversionType_ID(orderLine.getC_Order().getC_ConversionType_ID());
		log.setQtyEntered(orderLine.getQtyEntered());
		log.setC_UOM_ID(orderLine.getC_UOM_ID());
		log.setQtyOrdered(orderLine.getQtyOrdered());
		log.setQtyReserved(orderLine.getQtyReserved());
		log.setQtyDelivered(orderLine.getQtyDelivered());
		log.setQtyInvoiced(orderLine.getQtyInvoiced());
		log.setPriceEntered(orderLine.getPriceEntered());
		log.setPriceActual(orderLine.getPriceActual());
		log.setC_Tax_ID(orderLine.getC_Tax_ID());
		log.setLineNetAmt(orderLine.getLineNetAmt());
	}

	static public void copyInfoFromInvoiceLineToLog(MInvValCalLog log, I_C_InvoiceLine invoiceLine)
	{
		String docBaseType = invoiceLine.getC_Invoice().getC_DocType().getDocBaseType();

		log.setC_InvoiceLine_ID(invoiceLine.getC_InvoiceLine_ID());
		log.setDateInvoiced(invoiceLine.getC_Invoice().getDateInvoiced());
		log.setIsTaxIncluded(invoiceLine.getC_Invoice().isTaxIncluded());
		log.setM_PriceList_ID(invoiceLine.getC_Invoice().getM_PriceList_ID());
		log.setC_Currency_ID(invoiceLine.getC_Invoice().getC_Currency_ID());
		log.setC_ConversionType_ID(invoiceLine.getC_Invoice().getC_ConversionType_ID());
		log.setC_UOM_ID(invoiceLine.getC_UOM_ID());
		log.setC_Tax_ID(invoiceLine.getC_Tax_ID());
		if(docBaseType.equals(MDocType.DOCBASETYPE_APInvoice))
		{
			log.setQtyEntered(invoiceLine.getQtyEntered());
			log.setQtyInvoiced(invoiceLine.getQtyInvoiced());
			log.setPriceEntered(invoiceLine.getPriceEntered());
			log.setPriceActual(invoiceLine.getPriceActual());
			log.setLineNetAmt(invoiceLine.getLineNetAmt());
		}if(docBaseType.equals(MDocType.DOCBASETYPE_APCreditMemo)){
			log.setQtyEntered(invoiceLine.getQtyEntered().negate());
			log.setQtyInvoiced(invoiceLine.getQtyInvoiced());
			log.setPriceEntered(invoiceLine.getPriceEntered());
			log.setPriceActual(invoiceLine.getPriceActual());
			log.setLineNetAmt(invoiceLine.getLineNetAmt());
		}


	}

	static public BigDecimal calculateInvValTotalAmt(Properties ctx, int JP_InvValCalLine_ID, String trxName)
	{
		BigDecimal retValue = null;
		StringBuilder sql = new StringBuilder("SELECT SUM(COALESCE(JP_ApplyAmt,0)) FROM JP_InvValCalLog ")
								.append("WHERE JP_InvValCalLine_ID=?");
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (sql.toString(), trxName);
			pstmt.setInt(1, JP_InvValCalLine_ID);
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

		if(retValue == null)
			return Env.ZERO;

		return retValue;
	}

	static public BigDecimal calculateApplyQty(Properties ctx, int JP_InvValCalLine_ID, String trxName)
	{
		BigDecimal retValue = null;
		StringBuilder sql = new StringBuilder("SELECT SUM(COALESCE(JP_ApplyQty,0)) FROM JP_InvValCalLog ")
								.append("WHERE JP_InvValCalLine_ID=?");
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (sql.toString(), trxName);
			pstmt.setInt(1, JP_InvValCalLine_ID);
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

		if(retValue == null)
			return Env.ZERO;

		return retValue;
	}

	/**
	 * 	Get PO Match of Receipt Line
	 *	@param ctx context
	 *	@param M_InOutLine_ID receipt
	 *	@param OrderClause Order by
	 *	@param trxName transaction
	 *	@return array of matches
	 */
	public static MMatchPO[] getMatchPOs (Properties ctx, int M_InOutLine_ID, String OrderClause, String trxName)
	{
		if (M_InOutLine_ID == 0)
			return new MMatchPO[]{};
		//
		String sql = "SELECT * FROM M_MatchPO WHERE M_InOutLine_ID=?";
		if(!Util.isEmpty(OrderClause))
			sql = sql + " ORDER BY" + OrderClause;

		ArrayList<MMatchPO> list = new ArrayList<MMatchPO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (sql, trxName);
			pstmt.setInt (1, M_InOutLine_ID);
			rs = pstmt.executeQuery ();
			while (rs.next ())
				list.add (new MMatchPO (ctx, rs, trxName));
		}
		catch (Exception e)
		{
			s_log.log(Level.SEVERE, sql, e);
			if (e instanceof RuntimeException)
			{
				throw (RuntimeException)e;
			}
			else
			{
				throw new IllegalStateException(e);
			}
		}
		finally
		{
			DB.close(rs, pstmt);
		}

		MMatchPO[] retValue = new MMatchPO[list.size()];
		list.toArray (retValue);
		return retValue;
	}	//	get

	/**
	 * 	Get Inv Matches for InOutLine
	 *	@param ctx context
	 *	@param M_InOutLine_ID shipment
	 *	@param OrderClause Order by
	 *	@param trxName transaction
	 *	@return array of matches
	 */
	public static MMatchInv[] getMatchInvs (Properties ctx, int M_InOutLine_ID, String OrderClause, String trxName)
		{
			if (M_InOutLine_ID <= 0)
			{
				return new MMatchInv[]{};
			}
			//
			final String whereClause = MMatchInv.COLUMNNAME_M_InOutLine_ID+"=?";
			List<MMatchInv> list = new Query(ctx, I_M_MatchInv.Table_Name, whereClause, trxName)
			.setParameters(M_InOutLine_ID)
			.setOrderBy(OrderClause)
			.list();
			return list.toArray (new MMatchInv[list.size()]);
		}	//	getInOutLine


	/**	Cache						*/
	private static CCache<Integer,MOrder>	s_cache	= new CCache<Integer,MOrder>("C_Order", 20, 2);	//	2 minutes

	/**
	 * 	Get MOrder from Cache
	 *	@param ctx context
	 *	@param C_Order_ID id
	 *	@return MOrder
	 */
	public static MOrder getMOrder (Properties ctx, int C_Order_ID)
	{
		Integer key = Integer.valueOf(C_Order_ID);
		MOrder retValue = (MOrder) s_cache.get (key);
		if (retValue != null)
			return retValue;
		retValue = new MOrder (ctx, C_Order_ID, null);
		if (retValue.get_ID () != 0)
			s_cache.put (key, retValue);
		return retValue;
	} //	get


	public static MPriceListVersion getPriceListVersion (Properties ctx, int M_PriceList_ID, Timestamp valid, String trxName )
	{

		final String whereClause = "M_PriceList_ID=? AND TRUNC(ValidFrom)=?";
		MPriceListVersion m_plv = new Query(ctx, I_M_PriceList_Version.Table_Name, whereClause, trxName)
					.setParameters(M_PriceList_ID, valid)
					.setOnlyActiveRecords(true)
					.first();

		return m_plv;
	}	//	getPriceListVersion
}
