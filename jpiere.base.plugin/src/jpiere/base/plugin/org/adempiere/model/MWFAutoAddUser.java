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
 * JPIERE-0518 WF Auto Add Approvers
 *
 * @author Hideaki Hagiwara
 *
 */
public class MWFAutoAddUser extends X_JP_WF_AutoAddUser {

	private static final long serialVersionUID = -6567789658422241735L;

	public MWFAutoAddUser(Properties ctx, int JP_WF_AutoAddUser_ID, String trxName)
	{
		super(ctx, JP_WF_AutoAddUser_ID, trxName);
	}

	public MWFAutoAddUser(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}

	@Override
    public String toString()
    {
      StringBuilder sb = new StringBuilder ("X_JP_WF_AutoAddApprovers[")
        .append(get_ID()).append(", AD_User_ID=").append(getAD_User_ID()).append("]");
      return sb.toString();
    }

	@Override
	protected boolean afterSave(boolean newRecord, boolean success)
	{
		MWFAutoAddApprovers.cacheCrear();

		return success;
	}

	@Override
	protected boolean afterDelete(boolean success)
	{
		MWFAutoAddApprovers.cacheCrear();

		return success;
	}



}
