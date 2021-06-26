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

import org.adempiere.exceptions.DBException;
import org.compiere.util.DB;
import org.compiere.util.Msg;
import org.compiere.util.Util;


/**
 * JPIERE-0489 User Location
 *
 * @author Hideaki Hagiwara
 *
 */
public class MUserLocation extends X_JP_User_Location {

	public MUserLocation(Properties ctx, int JP_User_Location_ID, String trxName)
	{
		super(ctx, JP_User_Location_ID, trxName);
	}

	public MUserLocation(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}

	@Override
	protected boolean beforeSave(boolean newRecord)
	{
		if (isDefault())
		{
			int cnt = DB.getSQLValue(get_TrxName(),
							"SELECT COUNT(*) FROM JP_User_Location WHERE AD_User_ID=? AND AD_Client_ID=? AND IsDefault='Y' ",
							getAD_User_ID(), getAD_Client_ID());
			if (cnt > 0) {
				log.saveError("SaveError", Msg.getMsg(getCtx(), DBException.SAVE_ERROR_NOT_UNIQUE_MSG, true) + Msg.getElement(getCtx(), COLUMNNAME_IsDefault));
				return false;
			}
		}


		if(Util.isEmpty(getJP_Location_Label()))
		{
			setJP_Location_Label(getAD_User().getName());
		}


		return super.beforeSave(newRecord);
	}



}
