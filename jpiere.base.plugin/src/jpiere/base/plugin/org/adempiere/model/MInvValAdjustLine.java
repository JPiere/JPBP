package jpiere.base.plugin.org.adempiere.model;

import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

import org.compiere.model.Query;
import org.compiere.util.Util;

public class MInvValAdjustLine extends X_JP_InvValAdjustLine {
	
	MInvValAdjust parent = null;
	MInventoryDiffQtyLog[] diffLogs = null;
	
	public MInvValAdjustLine(Properties ctx, int JP_InvValAdjustLine_ID, String trxName)
	{
		super(ctx, JP_InvValAdjustLine_ID, trxName);
	}
	
	public MInvValAdjustLine(Properties ctx, ResultSet rs, String trxName) 
	{
		super(ctx, rs, trxName);
	}
	
	public MInvValAdjustLine (MInvValAdjust invValAdjust)
	{
		this (invValAdjust.getCtx(), 0, invValAdjust.get_TrxName());
		if (invValAdjust.get_ID() == 0)
			throw new IllegalArgumentException("Header not saved");
		setJP_InvValAdjust_ID(invValAdjust.getJP_InvValAdjust_ID());	//	parent
		setAD_Org_ID(invValAdjust.getAD_Org_ID());
	}
	
	public MInvValAdjust getParent()
	{
		if(parent == null)
			parent = new MInvValAdjust(getCtx(), getJP_InvValAdjust_ID(), null);
		
		return parent;
	}
	
	public MInventoryDiffQtyLog[] getDiffQtyLogs (String whereClause, String orderClause)
	{
		StringBuilder whereClauseFinal = new StringBuilder(MInventoryDiffQtyLog.COLUMNNAME_JP_InvValAdjustLine_ID+"=? ");
		if (!Util.isEmpty(whereClause, true))
			whereClauseFinal.append(whereClause);
		if (orderClause.length() == 0)
			orderClause = MInventoryDiffQtyLog.COLUMNNAME_Line;
		
		List<MInventoryDiffQtyLog> list = new Query(getCtx(), MInventoryDiffQtyLog.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(get_ID())
										.setOrderBy(orderClause)
										.list();

		return list.toArray(new MInventoryDiffQtyLog[list.size()]);
	}	//	getDiffQtyLogs

	
	/**
	 * 	Get logs of Inventory Qty Diffrence.
	 * 	@param requery requery
	 * 	@param orderBy optional order by column
	 * 	@return lines
	 */
	public MInventoryDiffQtyLog[] getDiffQtyLogs(boolean requery, String orderBy)
	{
		if (diffLogs != null && !requery) {
			set_TrxName(diffLogs, get_TrxName());
			return diffLogs;
		}
		//
		String orderClause = "";
		if (orderBy != null && orderBy.length() > 0)
			orderClause += orderBy;
		else
			orderClause += "Line";
		diffLogs = getDiffQtyLogs(null, orderClause);
		return diffLogs;
	}	//	getDiffQtyLogs

	
	/**
	 * 	Get logs of Inventory Qty Diffrence.
	 *
	 * 	@return lines
	 */
	public MInventoryDiffQtyLog[] getDiffQtyLogs()
	{
		return getDiffQtyLogs(false, null);
	}	//	getDiffQtyLogs
	
	
	@Override
	public String toString() 
	{
	      StringBuffer sb = new StringBuffer ("MInvValAdjustLine[")
	        .append(get_ID()).append("]-Line:")
	        .append(getLine());
		return sb.toString();
	}
}
