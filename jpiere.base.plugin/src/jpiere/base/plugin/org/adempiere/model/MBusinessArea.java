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
 *  JPIERE-0292 : Business Area.
 *
 *  @author Hideaki Hagiwara（h.hagiwara@oss-erp.co.jp）
 *
 */
public class MBusinessArea extends X_JP_BusinessArea {

	private static final long serialVersionUID = -2391924532836814480L;

	public MBusinessArea(Properties ctx, int JP_BusinessArea_ID, String trxName)
	{
		super(ctx, JP_BusinessArea_ID, trxName);
	}

	public MBusinessArea(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}

	/**	Categopry Cache				*/
	private static CCache<Integer,MBusinessArea>	s_cache = new CCache<Integer,MBusinessArea>(Table_Name, 20);

	/**
	 * 	Get from Cache
	 *	@param ctx context
	 *	@param JP_BusinessArea_ID id
	 *	@return Business Area
	 */
	public static MBusinessArea get (Properties ctx, int JP_BusinessArea_ID)
	{
		Integer ii = Integer.valueOf(JP_BusinessArea_ID);
		MBusinessArea retValue = (MBusinessArea)s_cache.get(ii);
		if (retValue != null)
			return retValue;
		retValue = new MBusinessArea (ctx, JP_BusinessArea_ID, null);
		if (retValue.get_ID () != 0)
			s_cache.put (JP_BusinessArea_ID, retValue);
		return retValue;
	}	//	get


	private MBusinessUnit[] m_BusinessUnits = null;

	public MBusinessUnit[] getBusinessUnits (boolean requery)
	{
		if(m_BusinessUnits != null && !requery)
			return m_BusinessUnits;

		ArrayList<MBusinessUnit> list = new ArrayList<MBusinessUnit>();
		final String sql = "SELECT JP_BusinessUnit_ID FROM JP_BusinessUnit WHERE JP_BusinessArea_ID=? AND IsActive='Y'";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, get_ID());
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add(MBusinessUnit.get(getCtx(), rs.getInt(1)) );
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

		m_BusinessUnits = new MBusinessUnit[list.size()];
		list.toArray(m_BusinessUnits);
		return m_BusinessUnits;
	}
}
