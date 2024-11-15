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

import org.compiere.model.MBPartner;
import org.compiere.model.Query;
import org.compiere.util.CCache;
import org.compiere.util.DB;
import org.compiere.util.Msg;


/**
 * JPIERE-0557: History of BPartner name
 * 
 * 
 * @author Hideaki Hagiwara
 *
 */
public class MBPartnerHistory extends X_JP_BPartner_History {

	private static final long serialVersionUID = -1287296956798428767L;

	public MBPartnerHistory(Properties ctx, int JP_BPartner_History_ID, String trxName) 
	{
		super(ctx, JP_BPartner_History_ID, trxName);
	}

	public MBPartnerHistory(Properties ctx, int JP_BPartner_History_ID, String trxName, String... virtualColumns)
	{
		super(ctx, JP_BPartner_History_ID, trxName, virtualColumns);
	}

	public MBPartnerHistory(Properties ctx, ResultSet rs, String trxName) 
	{
		super(ctx, rs, trxName);
	}

	@Override
	protected boolean beforeSave(boolean newRecord)
	{
		if(getC_BPartner_ID() == 0)
		{
			log.saveError("Error", Msg.getMsg(getCtx(), "C_BPartner_ID = 0"));
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
			MBPartnerHistory[] m_Histories = getOtherBPartnerHistory();
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


	private MBPartnerHistory[] getOtherBPartnerHistory()
	{

		MBPartnerHistory[] m_Histories = null;

		ArrayList<MBPartnerHistory> list = new ArrayList<MBPartnerHistory>();
		String sql = "SELECT * FROM JP_BPartner_History WHERE C_BPartner_ID=? AND JP_BPartner_History_ID <> ?";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, getC_BPartner_ID());
			pstmt.setInt(2, getJP_BPartner_History_ID());
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add(new MBPartnerHistory (getCtx(), rs, get_TrxName()));
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

		m_Histories = new MBPartnerHistory[list.size()];
		list.toArray(m_Histories);
		return m_Histories;
	}


	/**	Cache						*/
	private static CCache<Integer,MBPartnerHistory[]> s_cache	= new CCache<Integer,MBPartnerHistory[]>(Table_Name, 100, 10);	//	10 minutes

	public static MBPartnerHistory[] getHistories (Properties ctx, int C_BPartner_ID, String trxName)
	{
		MBPartnerHistory[] m_Histories = s_cache.get (Integer.valueOf(C_BPartner_ID));
		if (m_Histories != null)
		{
			return m_Histories;
		}

		final String whereClause = "C_BPartner_ID=?";
		List <MBPartnerHistory> list = new Query(ctx, I_JP_BPartner_History.Table_Name, whereClause, trxName)
		.setParameters(C_BPartner_ID)
		.setOrderBy("DateFrom DESC")
		.list();

		m_Histories = new MBPartnerHistory[list.size()];
		list.toArray(m_Histories);
		s_cache.put(Integer.valueOf(C_BPartner_ID), m_Histories);

		return m_Histories;

	}//getHistories

	public static MBPartnerHistory getHistory (Properties ctx, int C_BPartner_ID, Timestamp date, String trxName)
	{
		MBPartnerHistory m_History = null;
		MBPartnerHistory[] m_Histories = getHistories(ctx, C_BPartner_ID, trxName);
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

	public static String getHistoryName (Properties ctx, int C_BPartner_ID, Timestamp date, String trxName)
	{
		MBPartnerHistory m_History = getHistory(ctx, C_BPartner_ID, date, trxName);
		if(m_History == null)
		{
			return MBPartner.get(ctx, C_BPartner_ID).getName();

		}else {

			return m_History.getName();

		}

	}//getHistoryName

}
