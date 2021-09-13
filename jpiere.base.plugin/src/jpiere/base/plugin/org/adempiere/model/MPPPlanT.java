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
 * JPIERE-0501: JPiere PP Doc Template
 *
 * @author Hideaki Hagiwara
 *
 */
public class MPPPlanT extends X_JP_PP_PlanT {

	public MPPPlanT(Properties ctx, int JP_PP_PlanT_ID, String trxName)
	{
		super(ctx, JP_PP_PlanT_ID, trxName);
	}

	public MPPPlanT(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}

	@Override
	protected boolean beforeSave(boolean newRecord)
	{
		return true;
	}

	private MPPPlanLineT[] m_PPPlanLineTs = null;

	public MPPPlanLineT[] getPPPlanLineTs (String whereClause, String orderClause)
	{
		StringBuilder whereClauseFinal = new StringBuilder(MPPPlanLineT.COLUMNNAME_JP_PP_PlanT_ID+"=? ");
		if (!Util.isEmpty(whereClause, true))
			whereClauseFinal.append(whereClause);
		if (orderClause.length() == 0)
			orderClause = MPPPlanLineT.COLUMNNAME_Line;
		//
		List<MPPPlanLineT> list = new Query(getCtx(), MPPPlanLineT.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(get_ID())
										.setOrderBy(orderClause)
										.list();

		return list.toArray(new MPPPlanLineT[list.size()]);

	}

	public MPPPlanLineT[] getPPPlanLineTs(boolean requery, String orderBy)
	{
		if (m_PPPlanLineTs != null && !requery) {
			set_TrxName(m_PPPlanLineTs, get_TrxName());
			return m_PPPlanLineTs;
		}
		//
		String orderClause = "";
		if (orderBy != null && orderBy.length() > 0)
			orderClause += orderBy;
		else
			orderClause += MPPPlanLineT.COLUMNNAME_Line;

		m_PPPlanLineTs = getPPPlanLineTs(null, orderClause);
		return m_PPPlanLineTs;
	}

	public MPPPlanLineT[] getPPPlanLineTs()
	{
		return getPPPlanLineTs(false, null);
	}
}
