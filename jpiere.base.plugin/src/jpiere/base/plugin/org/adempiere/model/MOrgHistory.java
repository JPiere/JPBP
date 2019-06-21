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
import org.compiere.util.Msg;


/**
 * JPIERE-0447:Org History
 *
 * @author Hideaki Hagiwara
 *
 */
public class MOrgHistory extends X_JP_Org_History {

	public MOrgHistory(Properties ctx, int JP_Org_History_ID, String trxName)
	{
		super(ctx, JP_Org_History_ID, trxName);
	}

	public MOrgHistory(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}

	@Override
	protected boolean beforeSave(boolean newRecord)
	{
		if(getAD_Org_ID() == 0)
		{
			log.saveError("Error", Msg.getMsg(getCtx(), "AD_Org_ID = 0"));
			return false;
		}

		if(getAD_Client_ID() == 0)
		{
			log.saveError("Error", Msg.getMsg(getCtx(), "AD_Client_ID = 0"));
			return false;
		}

		if(newRecord || is_ValueChanged("DateFrom") || is_ValueChanged("DateTo"))
		{
			if(getDateFrom().compareTo(getDateTo()) > 0)
			{
				log.saveError("Error", Msg.getMsg(getCtx(), Msg.getElement(getCtx(), "DateFrom") + " > " + Msg.getElement(getCtx(), "DateTo") ));
				return false;
			}

			//
			MOrgHistory[] m_OrgHistories = getOtherOrgHistory();
			for(int i = 0 ; i < m_OrgHistories.length; i++)
			{

				if( getDateFrom().compareTo(m_OrgHistories[i].getDateTo()) <= 0
						&& m_OrgHistories[i].getDateFrom().compareTo(getDateTo()) <= 0)
				{
					log.saveError("Error", Msg.getMsg(getCtx(), "JP_OverlapPeriod"));
					return false;

				}

			}
		}

		return true;
	}


	private MOrgHistory[] getOtherOrgHistory()
	{

		MOrgHistory[] m_OrgHistories = null;

		ArrayList<MOrgHistory> list = new ArrayList<MOrgHistory>();
		String sql = "SELECT * FROM JP_Org_History WHERE AD_Org_ID=? AND JP_Org_History_ID <> ?";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, getAD_Org_ID());
			pstmt.setInt(2, getJP_Org_History_ID());
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add(new MOrgHistory (getCtx(), rs, get_TrxName()));
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

		m_OrgHistories = new MOrgHistory[list.size()];
		list.toArray(m_OrgHistories);
		return m_OrgHistories;
	}


}
