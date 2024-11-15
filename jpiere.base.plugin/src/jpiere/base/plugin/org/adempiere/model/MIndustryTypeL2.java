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
public class MIndustryTypeL2 extends X_JP_IndustryTypeL2 {

	private static final long serialVersionUID = 1152901523860077911L;

	public MIndustryTypeL2(Properties ctx, int JP_IndustryTypeL2_ID, String trxName)
	{
		super(ctx, JP_IndustryTypeL2_ID, trxName);
	}

	public MIndustryTypeL2(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}

	/**	Categopry Cache				*/
	private static CCache<Integer,MIndustryTypeL2>	s_cache = new CCache<Integer,MIndustryTypeL2>(Table_Name, 20);

	/**
	 * 	Get from Cache
	 *	@param ctx context
	 *	@param JP_IndustryTypL2_ID id
	 *	@return Industory Type L2
	 */
	public static MIndustryTypeL2 get (Properties ctx, int JP_IndustryTypL2_ID)
	{
		Integer ii = Integer.valueOf(JP_IndustryTypL2_ID);
		MIndustryTypeL2 retValue = (MIndustryTypeL2)s_cache.get(ii);
		if (retValue != null)
			return retValue;
		retValue = new MIndustryTypeL2 (ctx, JP_IndustryTypL2_ID, null);
		if (retValue.get_ID () != 0)
			s_cache.put (JP_IndustryTypL2_ID, retValue);
		return retValue;
	}	//	get

	private MIndustryTypeL1[] m_IndustryTypesL1 = null;

	public MIndustryTypeL1[] getIndustryTypesL1 (boolean requery)
	{
		if(m_IndustryTypesL1 != null && !requery)
			return m_IndustryTypesL1;

		ArrayList<MIndustryTypeL1> list = new ArrayList<MIndustryTypeL1>();
		final String sql = "SELECT JP_IndustryTypeL2_ID FROM JP_IndustryTypeL1 WHERE JP_IndustryTypeL2_ID=? AND IsActive='Y'";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, get_ID());
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add(MIndustryTypeL1.get(getCtx(), rs.getInt(1)));
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

		m_IndustryTypesL1 = new MIndustryTypeL1[list.size()];
		list.toArray(m_IndustryTypesL1);
		return m_IndustryTypesL1;
	}
}
