/******************************************************************************
 * Product: JPiere                                                            *
 * Copyright (C) Hideaki Hagiwara (h.hagiwara@oss-erp.co.jp)                  *
 *                                                                            *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY.                          *
 * See the GNU General Public License for more details.                       *
 *                                                                            *
 * JPiere is maintained by OSS ERP Solutions Co., Ltd.                        *
 * (http://www.oss-erp.co.jp)                                                 *
 *****************************************************************************/

package jpiere.base.plugin.org.adempiere.model;

import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

import org.compiere.model.MColumn;
import org.compiere.model.Query;
import org.compiere.util.CCache;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

/**
 * JPIERE-0431:Contract Process Schedule
 *
 * @author Hideaki Hagiwara
 *
 */
public class MContractPSLine extends X_JP_ContractPSLine {

	public MContractPSLine(Properties ctx, int JP_ContractPSLine_ID, String trxName)
	{
		super(ctx, JP_ContractPSLine_ID, trxName);
	}

	public MContractPSLine(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}


	@Override
	protected boolean beforeSave(boolean newRecord)
	{
		//Check Update
		if(!newRecord && isFactCreatedJP() && !is_ValueChanged("IsFactCreatedJP"))
		{
			int columnCount = get_ColumnCount();
			String columnName = null;
			MColumn column = null;
			boolean isOk = true;
			for(int i = 0; i < columnCount; i++)
			{
				if(is_ValueChanged(i))
				{
					columnName = get_ColumnName(i);
					if(columnName.equals("IsFactCreatedJP")) {
						continue;
					}else if(columnName.equals("QtyEntered") || columnName.equals("QtyOrdered")) {

						if(getParent().getDocBaseType().equals(MContractProcSchedule.DOCBASETYPE_ARInvoice) ||getParent().getDocBaseType().equals(MContractProcSchedule.DOCBASETYPE_APInvoice))
						{
							isOk = false;
							break;
						}else {

							continue;//Update order Line Qty
						}
					}

					column = MColumn.get(getCtx(), Table_Name, columnName);
					if(column.isAlwaysUpdateable())
					{
						continue;
					}else {
						isOk = false;
						break;
					}
				}

			}//for

			if(!isOk)
			{
				log.saveError("Error",  Msg.getMsg(Env.getCtx(),"JP_CannotChangeField",new Object[]{Msg.getElement(Env.getCtx(), columnName)})+ " : " + Msg.getElement(getCtx(), "IsFactCreatedJP"));
				return false;
			}
		}

		return true;
	}

	/**	Cache				*/
	private static CCache<Integer,MContractPSLine>	s_cache = new CCache<Integer,MContractPSLine>(Table_Name, 20);

	/**
	 * 	Get from Cache
	 *	@param ctx context
	 *	@param JP_ContractPSLine_ID id
	 *	@return Contract Process Schedule Line
	 */
	public static MContractPSLine get (Properties ctx, int JP_ContractPSLine_ID)
	{
		Integer ii = Integer.valueOf(JP_ContractPSLine_ID);
		MContractPSLine retValue = (MContractPSLine)s_cache.get(ii);
		if (retValue != null)
			return retValue;
		retValue = new MContractPSLine (ctx, JP_ContractPSLine_ID, null);
		if (retValue.get_ID () != 0)
			s_cache.put (JP_ContractPSLine_ID, retValue);
		return retValue;
	}	//	get


	@Override
	protected boolean afterSave(boolean newRecord, boolean success)
	{
		if (!success)
			return success;
		if (getParent().isProcessed())
			return success;

		if(newRecord || is_ValueChanged(MContractLine.COLUMNNAME_LineNetAmt))
		{
			String sql = "UPDATE JP_ContractProcSchedule cps "
					+ " SET TotalLines = "
					    + "(SELECT COALESCE(SUM(LineNetAmt),0) FROM JP_ContractPSLine psl WHERE cps.JP_ContractProcSchedule_ID=psl.JP_ContractProcSchedule_ID)"
					+ "WHERE JP_ContractProcSchedule_ID=?";
				int no = DB.executeUpdate(sql, new Object[]{Integer.valueOf(getJP_ContractProcSchedule_ID())}, false, get_TrxName(), 0);
				if (no != 1)
				{
					log.warning("(1) #" + no);
					return false;
				}
		}

		return success;
	}

	protected MContractProcSchedule m_parent = null;

	public MContractProcSchedule getParent()
	{
		if (m_parent == null)
			m_parent = new MContractProcSchedule(getCtx(), getJP_ContractProcSchedule_ID(), get_TrxName());
		return m_parent;
	}	//	getParent


	private MContractPSInOutLine[] 	m_ContractPSInOutlines = null;

	public MContractPSInOutLine[] getContractPSInOutLines (String whereClause, String orderClause)
	{
		StringBuilder whereClauseFinal = new StringBuilder(MContractPSInOutLine.COLUMNNAME_JP_ContractPSLine_ID+"=? ");
		if (!Util.isEmpty(whereClause, true))
			whereClauseFinal.append(whereClause);
		if (orderClause.length() == 0)
			orderClause = MContractPSInOutLine.COLUMNNAME_Line;

		List<MContractPSInOutLine> list = new Query(getCtx(), MContractPSInOutLine.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(get_ID())
										.setOrderBy(orderClause)
										.list();

		//
		return list.toArray(new MContractPSInOutLine[list.size()]);
	}	//	getContractPSInOutLines

	public MContractPSInOutLine[] getContractPSInOutLines (boolean requery, String orderBy)
	{
		if (m_ContractPSInOutlines != null && !requery) {
			set_TrxName(m_ContractPSInOutlines, get_TrxName());
			return m_ContractPSInOutlines;
		}
		//
		String orderClause = "";
		if (orderBy != null && orderBy.length() > 0)
			orderClause += orderBy;
		else
			orderClause += "Line";
		m_ContractPSInOutlines = getContractPSInOutLines(null, orderClause);
		return m_ContractPSInOutlines;
	}	//	getContractPSInOutLines


	public MContractPSInOutLine[] getContractPSInOutLines()
	{
		return getContractPSInOutLines(false, null);
	}	//	getContractPSInOutLines


	private MContractPSInvoiceLine[] 	m_ContractPSInvoicelines = null;

	public MContractPSInvoiceLine[] getContractPSInvoiceLines (String whereClause, String orderClause)
	{
		StringBuilder whereClauseFinal = new StringBuilder(MContractPSInvoiceLine.COLUMNNAME_JP_ContractPSLine_ID+"=? ");
		if (!Util.isEmpty(whereClause, true))
			whereClauseFinal.append(whereClause);
		if (orderClause.length() == 0)
			orderClause = MContractPSInvoiceLine.COLUMNNAME_Line;

		List<MContractPSInvoiceLine> list = new Query(getCtx(), MContractPSInvoiceLine.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(get_ID())
										.setOrderBy(orderClause)
										.list();

		//
		return list.toArray(new MContractPSInvoiceLine[list.size()]);
	}	//	getContractPSInvoiceLines

	public MContractPSInvoiceLine[] getContractPSInvoiceLines (boolean requery, String orderBy)
	{
		if (m_ContractPSInvoicelines != null && !requery) {
			set_TrxName(m_ContractPSInvoicelines, get_TrxName());
			return m_ContractPSInvoicelines;
		}
		//
		String orderClause = "";
		if (orderBy != null && orderBy.length() > 0)
			orderClause += orderBy;
		else
			orderClause += "Line";
		m_ContractPSInvoicelines = getContractPSInvoiceLines(null, orderClause);
		return m_ContractPSInvoicelines;
	}	//	getContractPSInvoiceLines


	public MContractPSInvoiceLine[] getContractPSInvoiceLines()
	{
		return getContractPSInvoiceLines(false, null);
	}	//	getContractPSInvoiceLines

}
