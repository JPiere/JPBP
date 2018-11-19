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
		Integer ii = Integer.valueOf (JP_ContractCategory_ID);
		MContractCategory retValue = (MContractCategory)s_cache.get(ii);
		if (retValue != null)
			return retValue;
		retValue = new MContractCategory (ctx, JP_ContractCategory_ID, null);
		if (retValue.get_ID () != 0)
			s_cache.put (JP_ContractCategory_ID, retValue);
		return retValue;
	}	//	get


	private MContract[] m_Contracts = null;

	public MContract[] getContracts (boolean requery)
	{
		if(m_Contracts != null && !requery)
			return m_Contracts;

		ArrayList<MContract> list = new ArrayList<MContract>();
		final String sql = "SELECT JP_Contract_ID FROM JP_Contract WHERE JP_ContractCategory_ID=? AND IsActive='Y'";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, get_ID());
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add(new MContract (getCtx(), rs.getInt(1), get_TrxName()));
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

		m_Contracts = new MContract[list.size()];
		list.toArray(m_Contracts);
		return m_Contracts;
	}

	private MContractT[] m_ContractTemplates = null;

	public MContractT[] getContractTemplates (boolean requery)
	{
		if(m_ContractTemplates != null && !requery)
			return m_ContractTemplates;

		ArrayList<MContractT> list = new ArrayList<MContractT>();
		final String sql = "SELECT JP_ContractT_ID FROM JP_ContractT WHERE JP_ContractCategory_ID=? AND IsActive='Y'";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, get_ID());
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add(new MContractT (getCtx(), rs.getInt(1), get_TrxName()));
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

		m_ContractTemplates = new MContractT[list.size()];
		list.toArray(m_ContractTemplates);
		return m_ContractTemplates;
	}
}
