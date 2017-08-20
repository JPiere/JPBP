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


/** JPIERE-0363
*
* @author Hideaki Hagiwara
*
*/
public class MContractProcPeriod extends X_JP_ContractProcPeriod {
	
	public MContractProcPeriod(Properties ctx, int JP_ContractProcPeriod_ID, String trxName) 
	{
		super(ctx, JP_ContractProcPeriod_ID, trxName);
	}
	
	public MContractProcPeriod(Properties ctx, ResultSet rs, String trxName) 
	{
		super(ctx, rs, trxName);
	}
	
	
	/**	Cache				*/
	private static CCache<Integer,MContractProcPeriod>	s_cache = new CCache<Integer,MContractProcPeriod>(Table_Name, 20);
	
	/**
	 * 	Get from Cache
	 *	@param ctx context
	 *	@param JP_ContractProcPeriod_ID id
	 *	@return Contract Process Period
	 */
	public static MContractProcPeriod get (Properties ctx, int JP_ContractProcPeriod_ID)
	{
		Integer ii = new Integer (JP_ContractProcPeriod_ID);
		MContractProcPeriod retValue = (MContractProcPeriod)s_cache.get(ii);
		if (retValue != null)
			return retValue;
		retValue = new MContractProcPeriod (ctx, JP_ContractProcPeriod_ID, null);
		if (retValue.get_ID () != 0)
			s_cache.put (JP_ContractProcPeriod_ID, retValue);
		return retValue;
	}	//	get

	@Override
	public String toString() 
	{
		return getName();
	}
	
	
	
}
