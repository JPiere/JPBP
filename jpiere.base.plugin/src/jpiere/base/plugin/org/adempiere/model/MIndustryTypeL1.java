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
 * JPIERE-0290 Industory Types Master
 *
 * @author h.hagiwara
 *
 */
public class MIndustryTypeL1 extends X_JP_IndustryTypeL1 {

	public MIndustryTypeL1(Properties ctx, int JP_IndustryTypeL1_ID, String trxName)
	{
		super(ctx, JP_IndustryTypeL1_ID, trxName);
	}

	public MIndustryTypeL1(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}

	/**	Categopry Cache				*/
	private static CCache<Integer,MIndustryTypeL1>	s_cache = new CCache<Integer,MIndustryTypeL1>(Table_Name, 20);

	/**
	 * 	Get from Cache
	 *	@param ctx context
	 *	@param JP_IndustryTypL1_ID id
	 *	@return Industory Type L1
	 */
	public static MIndustryTypeL1 get (Properties ctx, int JP_IndustryTypL1_ID)
	{
		Integer ii = Integer.valueOf(JP_IndustryTypL1_ID);
		MIndustryTypeL1 retValue = (MIndustryTypeL1)s_cache.get(ii);
		if (retValue != null)
			return retValue;
		retValue = new MIndustryTypeL1 (ctx, JP_IndustryTypL1_ID, null);
		if (retValue.get_ID () != 0)
			s_cache.put (JP_IndustryTypL1_ID, retValue);
		return retValue;
	}	//	get



	private MIndustryType[] m_IndustryTypes = null;

	public MIndustryType[] getIndustryTypes (boolean requery)
	{
		if(m_IndustryTypes != null && !requery)
			return m_IndustryTypes;

		ArrayList<MIndustryType> list = new ArrayList<MIndustryType>();
		final String sql = "SELECT JP_IndustryTypeL1_ID FROM JP_IndustryType WHERE JP_IndustryTypeL1_ID=? AND IsActive='Y'";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, get_ID());
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add(MIndustryType.get(getCtx(), rs.getInt(1)));
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

		m_IndustryTypes = new MIndustryType[list.size()];
		list.toArray(m_IndustryTypes);
		return m_IndustryTypes;
	}
}
