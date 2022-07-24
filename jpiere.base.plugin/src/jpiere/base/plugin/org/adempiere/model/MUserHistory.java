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

import org.compiere.model.MTax;
import org.compiere.model.Query;
import org.compiere.util.CCache;
import org.compiere.util.DB;
import org.compiere.util.Msg;


/**
 * JPIERE-0564: History of User Name
 * 
 * 
 * @author Hideaki Hagiwara
 *
 */
public class MUserHistory extends X_JP_User_History {

	public MUserHistory(Properties ctx, int JP_User_History_ID, String trxName) 
	{
		super(ctx, JP_User_History_ID, trxName);
	}

	public MUserHistory(Properties ctx, int JP_User_History_ID, String trxName, String... virtualColumns)
	{
		super(ctx, JP_User_History_ID, trxName, virtualColumns);
	}

	public MUserHistory(Properties ctx, ResultSet rs, String trxName) 
	{
		super(ctx, rs, trxName);
	}

	@Override
	protected boolean beforeSave(boolean newRecord)
	{
		if(getAD_User_ID() == 0)
		{
			log.saveError("Error", Msg.getMsg(getCtx(), "C_BankAccount_ID = 0"));
			return false;
		}

//		if(getAD_Client_ID() == 0)
//		{
//			log.saveError("Error", Msg.getMsg(getCtx(), "AD_Client_ID = 0"));
//			return false;
//		}

		if(newRecord || is_ValueChanged("DateFrom") || is_ValueChanged("DateTo"))
		{
			if(getDateFrom().compareTo(getDateTo()) > 0)
			{
				log.saveError("Error", Msg.getMsg(getCtx(), Msg.getElement(getCtx(), "DateFrom") + " > " + Msg.getElement(getCtx(), "DateTo") ));
				return false;
			}

			//
			MUserHistory[] m_Histories = getOtherHistories();
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


	private MUserHistory[] getOtherHistories()
	{
		MUserHistory[] m_Histories = null;

		ArrayList<MUserHistory> list = new ArrayList<MUserHistory>();
		String sql = "SELECT * FROM JP_User_History WHERE AD_User_ID=? AND JP_User_History_ID <> ?";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, getAD_User_ID());
			pstmt.setInt(2, getJP_User_History_ID());
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add(new MUserHistory (getCtx(), rs, get_TrxName()));
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

		m_Histories = new MUserHistory[list.size()];
		list.toArray(m_Histories);
		return m_Histories;
	}


	/**	Cache						*/
	private static CCache<Integer,MUserHistory[]> s_cache	= new CCache<Integer,MUserHistory[]>(Table_Name, 100, 10);	//	10 minutes

	public static MUserHistory[] getHistories (Properties ctx, int AD_User_ID, String trxName)
	{
		MUserHistory[] m_Histories = s_cache.get (Integer.valueOf(AD_User_ID));
		if (m_Histories != null)
		{
			return m_Histories;
		}

		final String whereClause = "AD_User_ID=?";
		List <MUserHistory> list = new Query(ctx, I_JP_User_History.Table_Name, whereClause, trxName)
		.setParameters(AD_User_ID)
		.setOrderBy("DateFrom DESC")
		.list();

		m_Histories = new MUserHistory[list.size()];
		list.toArray(m_Histories);
		s_cache.put(Integer.valueOf(AD_User_ID), m_Histories);

		return m_Histories;

	}//getHistories

	public static MUserHistory getHistory (Properties ctx, int AD_User_ID, Timestamp date, String trxName)
	{
		MUserHistory m_History = null;
		MUserHistory[] m_Histories = getHistories(ctx, AD_User_ID, trxName);
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

	public static String getHistoryName (Properties ctx, int AD_User_ID, Timestamp date, String trxName)
	{
		MUserHistory m_History = getHistory(ctx, AD_User_ID, date, trxName);
		if(m_History == null)
		{
			return MTax.get(ctx, AD_User_ID).getName();

		}else {

			return m_History.getName();

		}

	}//getHistoryName

}
