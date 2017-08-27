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
public class MContractT extends X_JP_ContractT {
	
	public MContractT(Properties ctx, int JP_ContractT_ID, String trxName) 
	{
		super(ctx, JP_ContractT_ID, trxName);
	}
	
	public MContractT(Properties ctx, ResultSet rs, String trxName) 
	{
		super(ctx, rs, trxName);
	}
	
	/**	Cache				*/
	private static CCache<Integer,MContractT>	s_cache = new CCache<Integer,MContractT>(Table_Name, 20);
	
	
	public static MContractT get (Properties ctx, int JP_ContractT_ID)
	{
		Integer ii = new Integer (JP_ContractT_ID);
		MContractT retValue = (MContractT)s_cache.get(ii);
		if (retValue != null)
			return retValue;
		retValue = new MContractT (ctx, JP_ContractT_ID, null);
		if (retValue.get_ID () != 0)
			s_cache.put (JP_ContractT_ID, retValue);
		return retValue;
	}	//	get

	@Override
	protected boolean beforeSave(boolean newRecord) 
	{
		if(newRecord || is_ValueChanged("IsAutomaticUpdateJP"))
		{
			if(!isAutomaticUpdateJP())
			{
				setJP_ContractCancelTerm_ID(0);
				setJP_ContractExtendPeriod_ID(0);
			}
				
		}
		
		if(newRecord || is_ValueChanged("JP_ContractType"))
		{
			if(getJP_ContractType().equals(MContractT.JP_CONTRACTTYPE_SpotContract))
			{
				setIsAutomaticUpdateJP(false);
				setJP_ContractCancelTerm_ID(0);
				setJP_ContractExtendPeriod_ID(0);
			}
				
		}
		
		
		return true;
	}
	
	
	
}
