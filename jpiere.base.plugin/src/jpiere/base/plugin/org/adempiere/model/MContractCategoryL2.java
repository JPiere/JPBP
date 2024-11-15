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

/**
 * JPIERE-0363
 *
 * @author Hideaki Hagiwara
 *
 */
public class MContractCategoryL2 extends X_JP_ContractCategoryL2 {

	private static final long serialVersionUID = -9040908570393841023L;

	public MContractCategoryL2(Properties ctx, int JP_ContractCategoryL2_ID, String trxName)
	{
		super(ctx, JP_ContractCategoryL2_ID, trxName);
	}

	public MContractCategoryL2(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {
		return super.beforeSave(newRecord);
	}

	/**	Categopry Cache				*/
	private static CCache<Integer,MContractCategoryL2>	s_cache = new CCache<Integer,MContractCategoryL2>(Table_Name, 20);

	/**
	 * 	Get from Cache
	 *	@param ctx context
	 *	@param JP_ContractCategoryL2_ID id
	 *	@return Contract Category L2
	 */
	public static MContractCategoryL2 get (Properties ctx, int JP_ContractCategoryL2_ID)
	{
		Integer ii = Integer.valueOf(JP_ContractCategoryL2_ID);
		MContractCategoryL2 retValue = (MContractCategoryL2)s_cache.get(ii);
		if (retValue != null)
			return retValue;
		retValue = new MContractCategoryL2 (ctx, JP_ContractCategoryL2_ID, null);
		if (retValue.get_ID () != 0)
			s_cache.put (JP_ContractCategoryL2_ID, retValue);
		return retValue;
	}	//	get


	private MContractCategoryL1[] m_ContractCategoryL1s = null;


	public MContractCategoryL1[] getContractCategoryL1s (boolean requery)
	{
		if(m_ContractCategoryL1s != null && !requery)
			return m_ContractCategoryL1s;

		ArrayList<MContractCategoryL1> list = new ArrayList<MContractCategoryL1>();
		final String sql = "SELECT * FROM JP_ContractCategoryL1 WHERE JP_ContractCategoryL2_ID=? AND IsActive='Y'";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, get_ID());
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add(new MContractCategoryL1 (getCtx(), rs, get_TrxName()));
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

		m_ContractCategoryL1s = new MContractCategoryL1[list.size()];
		list.toArray(m_ContractCategoryL1s);
		return m_ContractCategoryL1s;
	}
}
