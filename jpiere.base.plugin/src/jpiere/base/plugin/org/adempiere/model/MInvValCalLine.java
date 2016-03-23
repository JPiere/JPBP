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

/**
 * JPIERE-0161:Inventory Valuation Calculate
 *
 * @author Hideaki Hagiwara
 *
 */
public class MInvValCalLine extends X_JP_InvValCalLine {

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

}
