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
public class MInvValCalLog extends X_JP_InvValCalLog {

	public MInvValCalLog(Properties ctx, int JP_InvValCalLog_ID, String trxName)
	{
		super(ctx, JP_InvValCalLog_ID, trxName);
	}

	public MInvValCalLog(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}

	public MInvValCalLog (MInvValCalLine invValCalLine)
	{
		this (invValCalLine.getCtx(), 0, invValCalLine.get_TrxName());
		if (invValCalLine.get_ID() == 0)
			throw new IllegalArgumentException("Line not saved");
		setJP_InvValCalLine_ID(invValCalLine.getJP_InvValCalLine_ID());	//	parent
		setAD_Org_ID(invValCalLine.getAD_Org_ID());
	}
}
