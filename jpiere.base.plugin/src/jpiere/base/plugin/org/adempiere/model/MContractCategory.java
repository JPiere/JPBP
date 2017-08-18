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
public class MContractCategory extends X_JP_ContractCategory {
	
	public MContractCategory(Properties ctx, int JP_ContractCategory_ID, String trxName) 
	{
		super(ctx, JP_ContractCategory_ID, trxName);
	}
	
	public MContractCategory(Properties ctx, ResultSet rs, String trxName) 
	{
		super(ctx, rs, trxName);

	}
	
	/**	Categopry Cache				*/
	private static CCache<Integer,MContractCategory>	s_cache = new CCache<Integer,MContractCategory>(Table_Name, 20);
	
	/**
	 * 	Get from Cache
	 *	@param ctx context
	 *	@param JP_ContractCategory_ID id
	 *	@return Contract Category
	 */
	public static MContractCategory get (Properties ctx, int JP_ContractCategory_ID)
	{
		Integer ii = new Integer (JP_ContractCategory_ID);
		MContractCategory retValue = (MContractCategory)s_cache.get(ii);
		if (retValue != null)
			return retValue;
		retValue = new MContractCategory (ctx, JP_ContractCategory_ID, null);
		if (retValue.get_ID () != 0)
			s_cache.put (JP_ContractCategory_ID, retValue);
		return retValue;
	}	//	get
	
}
