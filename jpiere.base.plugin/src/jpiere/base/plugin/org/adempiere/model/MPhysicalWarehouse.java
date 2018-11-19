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

import org.compiere.model.I_M_Locator;
import org.compiere.model.MLocator;
import org.compiere.model.MWarehouse;
import org.compiere.model.Query;
import org.compiere.util.CCache;


/**
 * JPIERE-0317:Physical Warehouse
 *
 * @author Hideaki Hagiwara
 *
 */
public class MPhysicalWarehouse extends X_JP_PhysicalWarehouse {

	/**	Physical Warehouse Locators				*/
	private MLocator[]	m_locators = null;

	/**	Cache					*/
	private static CCache<Integer,MPhysicalWarehouse> s_cache = new CCache<Integer,MPhysicalWarehouse>(Table_Name, 50 );


	public MPhysicalWarehouse(Properties ctx, int JP_PhysicalWarehouse_ID, String trxName)
	{
		super(ctx, JP_PhysicalWarehouse_ID, trxName);
	}

	public MPhysicalWarehouse(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}



	/**
	 * 	Get from Cache
	 *	@param ctx context
	 *	@param JP_PhysicalWarehouse_ID id
	 *	@return Physical warehouse
	 */
	public static MPhysicalWarehouse get (Properties ctx, int JP_PhysicalWarehouse_ID)
	{
		return get(ctx, JP_PhysicalWarehouse_ID, null);
	}


	/**
	 * Retrieves Physical warehouse from cache under transaction scope
	 * @param ctx				context
	 * @param JP_PhysicalWarehouse_ID	id of warehouse to load
	 * @param trxName			transaction name
	 * @return					Physical warehouse
	 */
	public static MPhysicalWarehouse get (Properties ctx, int JP_PhysicalWarehouse_ID, String trxName)
	{
		Integer key = Integer.valueOf(JP_PhysicalWarehouse_ID);
		MPhysicalWarehouse retValue = (MPhysicalWarehouse)s_cache.get(key);
		if (retValue != null)
			return retValue;
		//
		retValue = new MPhysicalWarehouse (ctx, JP_PhysicalWarehouse_ID, trxName);
		s_cache.put (key, retValue);
		return retValue;
	}	//	get

	/**
	 * 	Get Locators
	 *	@param reload if true reload
	 *	@return array of locators
	 */
	public MLocator[] getLocators(boolean reload)
	{
		if (!reload && m_locators != null)
			return m_locators;
		//
		final String whereClause = "JP_PhysicalWarehouse_ID=?";
		List<MLocator> list = new Query(getCtx(), I_M_Locator.Table_Name, whereClause, null)
										.setParameters(getJP_PhysicalWarehouse_ID())
										.setOnlyActiveRecords(true)
										.setOrderBy("X,Y,Z")
										.list();
		m_locators = list.toArray(new MLocator[list.size()]);
		return m_locators;
	}	//	getLocators


	/**
	 * 	Get Default Locator
	 *	@return (first) default locator
	 */
	public MLocator getDefaultLocator(MWarehouse orgWH)
	{
		MLocator[] locators = getLocators(false);
		for (int i = 0; i < locators.length; i++)
		{
			if (locators[i].isDefault() && locators[i].isActive() && locators[i].getM_Warehouse_ID() == orgWH.getM_Warehouse_ID())
				return locators[i];
		}

		return null;
	}	//	getDefaultLocator


}
