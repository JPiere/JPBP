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

import org.compiere.util.CCache;


/**
 * JPIERE-0363
 *
 * @author Hideaki Hagiwara
 *
 */
public class MContractProcPeriodG extends X_JP_ContractProcPeriodG {

	public MContractProcPeriodG(Properties ctx, int JP_ContractProcPeriodG_ID, String trxName)
	{
		super(ctx, JP_ContractProcPeriodG_ID, trxName);
	}

	public MContractProcPeriodG(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}

	/**	Cache				*/
	private static CCache<Integer,MContractProcPeriodG>	s_cache = new CCache<Integer,MContractProcPeriodG>(Table_Name, 20);

	/**
	 * 	Get from Cache
	 *	@param ctx context
	 *	@param JP_ContractProcPeriodG_ID id
	 *	@return Contract Process Period Group
	 */
	public static MContractProcPeriodG get (Properties ctx, int JP_ContractProcPeriodG_ID)
	{
		Integer ii = Integer.valueOf(JP_ContractProcPeriodG_ID);
		MContractProcPeriodG retValue = (MContractProcPeriodG)s_cache.get(ii);
		if (retValue != null)
			return retValue;
		retValue = new MContractProcPeriodG (ctx, JP_ContractProcPeriodG_ID, null);
		if (retValue.get_ID () != 0)
			s_cache.put (JP_ContractProcPeriodG_ID, retValue);
		return retValue;
	}	//	get

}
