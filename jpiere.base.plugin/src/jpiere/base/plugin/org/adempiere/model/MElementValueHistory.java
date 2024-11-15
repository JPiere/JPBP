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

import org.compiere.model.MElementValue;
import org.compiere.model.Query;
import org.compiere.util.CCache;
import org.compiere.util.DB;
import org.compiere.util.Msg;


/**
 * JPIERE-0561: History of Element Value name
 * 
 * 
 * @author Hideaki Hagiwara
 *
 */
public class MElementValueHistory extends X_JP_ElementValue_History {

	private static final long serialVersionUID = 8157209610311606106L;

	public MElementValueHistory(Properties ctx, int JP_ElementValue_History_ID, String trxName) 
	{
		super(ctx, JP_ElementValue_History_ID, trxName);
	}

	public MElementValueHistory(Properties ctx, int JP_ElementValue_History_ID, String trxName, String... virtualColumns)
	{
		super(ctx, JP_ElementValue_History_ID, trxName, virtualColumns);
	}

	public MElementValueHistory(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}
	
	@Override
	protected boolean beforeSave(boolean newRecord)
	{
		if(getC_ElementValue_ID() == 0)
		{
			log.saveError("Error", Msg.getMsg(getCtx(), "C_ElementValue_ID = 0"));
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
			MElementValueHistory[] m_Histories = getOtherHistories();
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


	private MElementValueHistory[] getOtherHistories()
	{
		MElementValueHistory[] m_Histories = null;

		ArrayList<MElementValueHistory> list = new ArrayList<MElementValueHistory>();
		String sql = "SELECT * FROM JP_ElementValue_History WHERE C_ElementValue_ID=? AND JP_ElementValue_History_ID <> ?";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, getC_ElementValue_ID());
			pstmt.setInt(2, getJP_ElementValue_History_ID());
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add(new MElementValueHistory (getCtx(), rs, get_TrxName()));
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

		m_Histories = new MElementValueHistory[list.size()];
		list.toArray(m_Histories);
		return m_Histories;
	}


	/**	Cache						*/
	private static CCache<Integer,MElementValueHistory[]> s_cache	= new CCache<Integer,MElementValueHistory[]>(Table_Name, 100, 10);	//	10 minutes

	public static MElementValueHistory[] getHistories (Properties ctx, int C_ElementValue_ID, String trxName)
	{
		MElementValueHistory[] m_Histories = s_cache.get (Integer.valueOf(C_ElementValue_ID));
		if (m_Histories != null)
		{
			return m_Histories;
		}

		final String whereClause = "C_ElementValue_ID=?";
		List <MElementValueHistory> list = new Query(ctx, I_JP_ElementValue_History.Table_Name, whereClause, trxName)
		.setParameters(C_ElementValue_ID)
		.setOrderBy("DateFrom DESC")
		.list();

		m_Histories = new MElementValueHistory[list.size()];
		list.toArray(m_Histories);
		s_cache.put(Integer.valueOf(C_ElementValue_ID), m_Histories);

		return m_Histories;

	}//getHistories

	public static MElementValueHistory getHistory (Properties ctx, int C_ElementValue_ID, Timestamp date, String trxName)
	{
		MElementValueHistory m_History = null;
		MElementValueHistory[] m_Histories = getHistories(ctx, C_ElementValue_ID, trxName);
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

	public static String getHistoryName (Properties ctx, int C_ElementValue_ID, Timestamp date, String trxName)
	{
		MElementValueHistory m_History = getHistory(ctx, C_ElementValue_ID, date, trxName);
		if(m_History == null)
		{
			MElementValue ev = new MElementValue(ctx, C_ElementValue_ID, trxName);
			return ev.getName();

		}else {

			return m_History.getName();

		}

	}//getHistoryName


}
