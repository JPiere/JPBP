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

import org.compiere.model.MOrg;
import org.compiere.util.CCache;
import org.compiere.util.DB;


/**
 *  JPIERE-0293 : Business Unit.
 *
 *  @author Hideaki Hagiwara（h.hagiwara@oss-erp.co.jp）
 *
 */
public class MBusinessUnit extends X_JP_BusinessUnit {

	private static final long serialVersionUID = 7434005716157514606L;

	public MBusinessUnit(Properties ctx, int JP_BusinessUnit_ID, String trxName)
	{
		super(ctx, JP_BusinessUnit_ID, trxName);
	}

	public MBusinessUnit(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}

	/**	Categopry Cache				*/
	private static CCache<Integer,MBusinessUnit>	s_cache = new CCache<Integer,MBusinessUnit>(Table_Name, 20);

	/**
	 * 	Get from Cache
	 *	@param ctx context
	 *	@param JP_BusinessUnit_ID id
	 *	@return Business Unit
	 */
	public static MBusinessUnit get (Properties ctx, int JP_BusinessUnit_ID)
	{
		Integer ii = Integer.valueOf(JP_BusinessUnit_ID);
		MBusinessUnit retValue = (MBusinessUnit)s_cache.get(ii);
		if (retValue != null)
			return retValue;
		retValue = new MBusinessUnit (ctx, JP_BusinessUnit_ID, null);
		if (retValue.get_ID () != 0)
			s_cache.put (JP_BusinessUnit_ID, retValue);
		return retValue;
	}	//	get


	private MOrg[] m_Orgs = null;

	public MOrg[] getOrgs (boolean requery)
	{
		if(m_Orgs != null && !requery)
			return m_Orgs;

		ArrayList<MOrg> list = new ArrayList<MOrg>();
		final String sql = "SELECT AD_Org_ID FROM AD_OrgInfo WHERE JP_BusinessUnit_ID=? AND IsActive='Y'";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, get_ID());
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add( MOrg.get(getCtx(), rs.getInt(1)) );
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

		m_Orgs = new MOrg[list.size()];
		list.toArray(m_Orgs);
		return m_Orgs;
	}
}
