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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.util.CCache;
import org.compiere.util.DB;

public class MContractCategoryL1 extends X_JP_ContractCategoryL1 {
	
	/**	Categopry Cache				*/
	private static CCache<Integer,MContractCategoryL1>	s_cache = new CCache<Integer,MContractCategoryL1>(Table_Name, 20);
	
	public MContractCategoryL1(Properties ctx, int JP_ContractCategoryL1_ID, String trxName) 
	{
		super(ctx, JP_ContractCategoryL1_ID, trxName);
	}
	
	public MContractCategoryL1(Properties ctx, ResultSet rs, String trxName) 
	{
		super(ctx, rs, trxName);
	}
	
	@Override
	protected boolean beforeSave(boolean newRecord) {
		return super.beforeSave(newRecord);
	}

	
	/**
	 * 	Get from Cache
	 *	@param ctx context
	 *	@param JP_ContractCategoryL1_ID id
	 *	@return Contract Category L1
	 */
	public static MContractCategoryL1 get (Properties ctx, int JP_ContractCategoryL1_ID)
	{
		Integer ii = new Integer (JP_ContractCategoryL1_ID);
		MContractCategoryL1 retValue = (MContractCategoryL1)s_cache.get(ii);
		if (retValue != null)
			return retValue;
		retValue = new MContractCategoryL1 (ctx, JP_ContractCategoryL1_ID, null);
		if (retValue.get_ID () != 0)
			s_cache.put (JP_ContractCategoryL1_ID, retValue);
		return retValue;
	}	//	get
	
	
	
	private MContractCategory[] m_ContractCategories = null;
	
	public MContractCategory[] getContractCategories (boolean requery)
	{
		if(m_ContractCategories != null && !requery)
			return m_ContractCategories;

		ArrayList<MContractCategory> list = new ArrayList<MContractCategory>();
		final String sql = "SELECT * FROM JP_ContractCategory WHERE JP_ContractCategoryL1_ID=? AND IsActive='Y'";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, get_ID());
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add(new MContractCategory (getCtx(), rs, get_TrxName()));
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}

		m_ContractCategories = new MContractCategory[list.size()];
		list.toArray(m_ContractCategories);
		return m_ContractCategories;
	}

	
}
