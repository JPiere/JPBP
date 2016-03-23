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

import org.compiere.model.MAcctSchema;
import org.compiere.model.Query;
import org.compiere.util.CCache;

/**
 * JPIERE-0160:Inventory Valuation Profile
 *
 * @author Hideaki Hagiwara
 *
 */
public class MInvValProfile extends X_JP_InvValProfile {

	private MInvValProfileOrg[] orgs = null;

	public MInvValProfile(Properties ctx, int JP_InvValProfile_ID, String trxName) {
		super(ctx, JP_InvValProfile_ID, trxName);
	}

	public MInvValProfile(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}


	/**
	 * 	Get MInventoryProfileOrgs
	 * 	@return Orgs
	 */
	public MInvValProfileOrg[] getOrgs ()
	{
		if (orgs != null) {
			set_TrxName(orgs, get_TrxName());
			return orgs;
		}

		StringBuilder whereClauseFinal = new StringBuilder(MInvValProfileOrg.COLUMNNAME_JP_InvValProfile_ID+"=? AND IsActive='Y'");
		List<MInvValProfileOrg> list = new Query(getCtx(), MInvValProfileOrg.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(get_ID())
										.list();

		orgs = list.toArray(new MInvValProfileOrg[list.size()]);

		return orgs;
	}	//	getOrgs


	@Override
	protected boolean beforeSave(boolean newRecord)
	{

		if((newRecord && getC_AcctSchema_ID() != 0)
				|| (is_ValueChanged("C_AcctSchema_ID") && getC_AcctSchema_ID() != 0))
		{
			MAcctSchema as = MAcctSchema.get(getCtx(), getC_AcctSchema_ID());
			setCostingLevel(as.getCostingLevel());
			setC_Currency_ID(as.getC_Currency_ID());
		}

		return true;
	}

	/**	Cache						*/
	private static CCache<Integer,MInvValProfile> s_cache	= new CCache<Integer,MInvValProfile>(Table_Name, 40, 5);	//	5 minutes

	public static MInvValProfile get (Properties ctx, int JP_InvValProfile_ID)
	{
		if (JP_InvValProfile_ID <= 0)
		{
			return null;
		}
		Integer key = new Integer (JP_InvValProfile_ID);
		MInvValProfile retValue = (MInvValProfile) s_cache.get (key);
		if (retValue != null)
		{
			return retValue;
		}
		retValue = new MInvValProfile (ctx, JP_InvValProfile_ID, null);
		if (retValue.get_ID () != 0)
		{
			s_cache.put (key, retValue);
		}
		return retValue;
	}	//	get
}
