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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.model.Query;
import org.compiere.util.CCache;
import org.compiere.util.DB;
import org.compiere.util.Msg;


/**
 * JPIERE-0560: History of Corporation name
 * 
 * 
 * @author Hideaki Hagiwara
 *
 */
public class MCorporationHistory extends X_JP_Corporation_History {

	private static final long serialVersionUID = -1104975937784470602L;

	public MCorporationHistory(Properties ctx, int JP_Corporation_History_ID, String trxName) 
	{
		super(ctx, JP_Corporation_History_ID, trxName);
	}

	public MCorporationHistory(Properties ctx, int JP_Corporation_History_ID, String trxName, String... virtualColumns) 
	{
		super(ctx, JP_Corporation_History_ID, trxName, virtualColumns);
	}

	public MCorporationHistory(Properties ctx, ResultSet rs, String trxName) 
	{
		super(ctx, rs, trxName);
	}

	@Override
	protected boolean beforeSave(boolean newRecord)
	{
		if(getJP_Corporation_ID() == 0)
		{
			log.saveError("Error", Msg.getMsg(getCtx(), "JP_Corporation_ID = 0"));
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
			MCorporationHistory[] m_Histories = getOtherHistories();
			for(int i = 0 ; i < m_Histories.length; i++)
			{

				if( getDateFrom().compareTo(m_Histories[i].getDateTo()) <= 0
						&& m_Histories[i].getDateFrom().compareTo(getDateTo()) <= 0)
				{
					log.saveError("Error", Msg.getMsg(getCtx(), "JP_OverlapPeriod"));
					return false;

				}

			}
		}

		return true;
	}


	private MCorporationHistory[] getOtherHistories()
	{
		MCorporationHistory[] m_Histories = null;

		ArrayList<MCorporationHistory> list = new ArrayList<MCorporationHistory>();
		String sql = "SELECT * FROM JP_Corporation_History WHERE JP_Corporation_ID=? AND JP_Corporation_History_ID <> ?";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, getJP_Corporation_ID());
			pstmt.setInt(2, getJP_Corporation_History_ID());
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add(new MCorporationHistory (getCtx(), rs, get_TrxName()));
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

		m_Histories = new MCorporationHistory[list.size()];
		list.toArray(m_Histories);
		return m_Histories;
	}


	/**	Cache						*/
	private static CCache<Integer,MCorporationHistory[]> s_cache	= new CCache<Integer,MCorporationHistory[]>(Table_Name, 100, 10);	//	10 minutes

	public static MCorporationHistory[] getHistories (Properties ctx, int JP_Corporation_ID, String trxName)
	{
		MCorporationHistory[] m_Histories = s_cache.get (Integer.valueOf(JP_Corporation_ID));
		if (m_Histories != null)
		{
			return m_Histories;
		}

		final String whereClause = "JP_Corporation_ID=?";
		List <MCorporationHistory> list = new Query(ctx, I_JP_Corporation_History.Table_Name, whereClause, trxName)
		.setParameters(JP_Corporation_ID)
		.setOrderBy("DateFrom DESC")
		.list();

		m_Histories = new MCorporationHistory[list.size()];
		list.toArray(m_Histories);
		s_cache.put(Integer.valueOf(JP_Corporation_ID), m_Histories);

		return m_Histories;

	}//getHistories

	public static MCorporationHistory getHistory (Properties ctx, int JP_Corporation_ID, Timestamp date, String trxName)
	{
		MCorporationHistory m_History = null;
		MCorporationHistory[] m_Histories = getHistories(ctx, JP_Corporation_ID, trxName);
		for(int i =0 ; i < m_Histories.length; i++)
		{
			if(m_Histories[i].getDateFrom().compareTo(date) <= 0
					&& m_Histories[i].getDateTo().compareTo(date) >= 0)
			{
				m_History = m_Histories[i];
				break;
			}
		}

		return m_History;

	}//getHistory

	public static String getHistoryName (Properties ctx, int JP_Corporation_ID, Timestamp date, String trxName)
	{
		MCorporationHistory m_History = getHistory(ctx, JP_Corporation_ID, date, trxName);
		if(m_History == null)
		{
			return MCorporation.get(ctx, JP_Corporation_ID).getName();

		}else {

			return m_History.getName();

		}

	}//getHistoryName

	
}
