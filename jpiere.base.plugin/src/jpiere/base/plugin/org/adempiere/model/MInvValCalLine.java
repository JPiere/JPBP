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
import java.util.Properties;

import org.compiere.model.Query;
import org.compiere.util.Env;

/**
 * JPIERE-0161:Inventory Valuation Calculate
 *
 * @author Hideaki Hagiwara
 *
 */
public class MInvValCalLine extends X_JP_InvValCalLine {

	private static final long serialVersionUID = 3906392868487384701L;

	public MInvValCalLine(Properties ctx, int JP_InvValCalLine_ID, String trxName)
	{
		super(ctx, JP_InvValCalLine_ID, trxName);
	}

	public MInvValCalLine(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}

	public MInvValCalLine (MInvValCal invValCal)
	{
		this (invValCal.getCtx(), 0, invValCal.get_TrxName());
		if (invValCal.get_ID() == 0)
			throw new IllegalArgumentException("Header not saved");
		setJP_InvValCal_ID(invValCal.getJP_InvValCal_ID());	//	parent
		setAD_Org_ID(invValCal.getAD_Org_ID());
	}

	@Override
	public String toString()
	{
	      StringBuffer sb = new StringBuffer ("MInvValCalLine[")
	        .append(get_ID()).append("]-Line:")
	        .append(getLine());
		return sb.toString();
	}


	/**
	 * 	Get Beginning Inventory Valuation Calculate
	 * 	@param MInvValCal
	 * 	@return Beginning InvValCal
	 */
	public static MInvValCalLine getBeginInvValCalLine(MInvValCalLine invValCalLine)
	{
		String whereClause = "JP_InvValCal_ID = ? AND M_Product_ID = ?";

		MInvValCalLine beginInvValCalLine = new Query(Env.getCtx(), MInvValCalLine.Table_Name, whereClause, null)
								.setParameters(invValCalLine.getJP_InvValCal().getJP_BeginInvValCal_ID(), invValCalLine.getM_Product_ID())
								.firstOnly();

		return beginInvValCalLine;
	}

	private MInvValCal m_InvValCal = null;

	public MInvValCal getParent()
	{
		if(m_InvValCal == null)
		{
			m_InvValCal = new MInvValCal(getCtx(), getJP_InvValCal_ID(), get_TrxName());
		}

		return m_InvValCal;
	}
}
