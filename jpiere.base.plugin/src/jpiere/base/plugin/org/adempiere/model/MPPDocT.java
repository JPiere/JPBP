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

import org.compiere.model.Query;
import org.compiere.util.Util;

/**
 * JPIERE-0502: JPiere PP Doc Template
 *
 * @author Hideaki Hagiwara
 *
 */
public class MPPDocT extends X_JP_PP_DocT {

	public MPPDocT(Properties ctx, int JP_PP_DocT_ID, String trxName)
	{
		super(ctx, JP_PP_DocT_ID, trxName);
	}

	public MPPDocT(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}

	@Override
	protected boolean beforeSave(boolean newRecord)
	{
		return true;
	}

	private MPPPlanT[] m_PPPlanTs = null;

	public MPPPlanT[] getPPPlanTs (String whereClause, String orderClause)
	{
		StringBuilder whereClauseFinal = new StringBuilder(MPPPlanT.COLUMNNAME_JP_PP_DocT_ID+"=? ");
		if (!Util.isEmpty(whereClause, true))
			whereClauseFinal.append(whereClause);
		if (orderClause.length() == 0)
			orderClause = MPPPlanT.COLUMNNAME_SeqNo;
		//
		List<MPPPlanT> list = new Query(getCtx(), MPPPlanT.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(get_ID())
										.setOrderBy(orderClause)
										.list();

		return list.toArray(new MPPPlanT[list.size()]);

	}

	public MPPPlanT[] getPPPlanTs(boolean requery, String orderBy)
	{
		if (m_PPPlanTs != null && !requery) {
			set_TrxName(m_PPPlanTs, get_TrxName());
			return m_PPPlanTs;
		}
		//
		String orderClause = "";
		if (orderBy != null && orderBy.length() > 0)
			orderClause += orderBy;
		else
			orderClause += MPPPlanT.COLUMNNAME_SeqNo;

		m_PPPlanTs = getPPPlanTs(null, orderClause);
		return m_PPPlanTs;
	}

	public MPPPlanT[] getPPPlanTs()
	{
		return getPPPlanTs(false, null);
	}

}
