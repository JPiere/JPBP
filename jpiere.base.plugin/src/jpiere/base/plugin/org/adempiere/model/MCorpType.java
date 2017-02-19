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
/**
 *  JPIERE-0291 : Corporation Type.
 *
 *  @author Hideaki Hagiwara（h.hagiwara@oss-erp.co.jp）
 *
 */
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.util.CCache;
import org.compiere.util.DB;

public class MCorpType extends X_JP_CorpType {
	
	public MCorpType(Properties ctx, int JP_CorpType_ID, String trxName) 
	{
		super(ctx, JP_CorpType_ID, trxName);
	}
	
	public MCorpType(Properties ctx, ResultSet rs, String trxName) 
	{
		super(ctx, rs, trxName);
	}
	
	/**	Categopry Cache				*/
	private static CCache<Integer,MCorpType>	s_cache = new CCache<Integer,MCorpType>(Table_Name, 20);
	
	/**
	 * 	Get from Cache
	 *	@param ctx context
	 *	@param JP_CorpType_ID id
	 *	@return Corp Type
	 */
	public static MCorpType get (Properties ctx, int JP_CorpType_ID)
	{
		Integer ii = new Integer (JP_CorpType_ID);
		MCorpType retValue = (MCorpType)s_cache.get(ii);
		if (retValue != null)
			return retValue;
		retValue = new MCorpType (ctx, JP_CorpType_ID, null);
		if (retValue.get_ID () != 0)
			s_cache.put (JP_CorpType_ID, retValue);
		return retValue;
	}	//	get
	
	private MCorporation[] m_Corporations = null;
	
	public MCorporation[] getCorporations (boolean requery)
	{
		if(m_Corporations != null && !requery)
			return m_Corporations;

		ArrayList<MCorporation> list = new ArrayList<MCorporation>();
		final String sql = "SELECT JP_Corporation_ID FROM JP_Corporation WHERE JP_CorpType_ID=? AND IsActive='Y'";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, get_ID());
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add(new MCorporation (getCtx(), rs.getInt(1), get_TrxName()));
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

		m_Corporations = new MCorporation[list.size()];
		list.toArray(m_Corporations);
		return m_Corporations;
	}
	
}
