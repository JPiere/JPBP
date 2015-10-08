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

import org.compiere.util.DB;

/**
 *  Corporation Group(企業グループマスタ) Model.
 *
 *  @author Hideaki Hagiwara（萩原 秀明:h.hagiwara@oss-erp.co.jp）
 *
 */
public class MCorporationGroup extends X_JP_CorporationGroup {

	public MCorporationGroup(Properties ctx, int JP_CorporationGroup_ID,
			String trxName) {
		super(ctx, JP_CorporationGroup_ID, trxName);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public MCorporationGroup(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO 自動生成されたコンストラクター・スタブ
	}


	private MCorporation[] m_Corporations = null;

	public MCorporation[] getCorporations (boolean requery)
	{
		if(m_Corporations != null && !requery)
			return m_Corporations;

		ArrayList<MCorporation> list = new ArrayList<MCorporation>();
		final String sql = "SELECT JP_Corporation_ID FROM JP_GroupCorporations WHERE JP_CorporationGroup_ID=? AND IsActive='Y'";
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

	public MCorporation[] getCorporations()
	{
		return getCorporations (false);
	}


}
