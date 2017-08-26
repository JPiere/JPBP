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
public class MContractCalender extends X_JP_ContractCalender {
	
	public MContractCalender(Properties ctx, int JP_ContractCalender_ID, String trxName) 
	{
		super(ctx, JP_ContractCalender_ID, trxName);
	}
	
	public MContractCalender(Properties ctx, ResultSet rs, String trxName) 
	{
		super(ctx, rs, trxName);
	}
	
	
	/**	Cache				*/
	private static CCache<Integer,MContractCalender>	s_cache = new CCache<Integer,MContractCalender>(Table_Name, 20);
	
	/**
	 * 	Get from Cache
	 *	@param ctx context
	 *	@param JP_ContractCalender_ID id
	 *	@return Contract Calender
	 */
	public static MContractCalender get (Properties ctx, int JP_ContractCalender_ID)
	{
		Integer ii = new Integer (JP_ContractCalender_ID);
		MContractCalender retValue = (MContractCalender)s_cache.get(ii);
		if (retValue != null)
			return retValue;
		retValue = new MContractCalender (ctx, JP_ContractCalender_ID, null);
		if (retValue.get_ID () != 0)
			s_cache.put (JP_ContractCalender_ID, retValue);
		return retValue;
	}	//	get
	
}
