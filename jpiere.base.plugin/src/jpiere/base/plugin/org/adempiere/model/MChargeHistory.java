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

import org.compiere.model.MCharge;
import org.compiere.model.Query;
import org.compiere.util.CCache;
import org.compiere.util.DB;
import org.compiere.util.Msg;

/**
 * JPIERE-0559: History of Charge name
 * 
 * 
 * @author Hideaki Hagiwara
 *
 */
public class MChargeHistory extends X_JP_Charge_History {

	private static final long serialVersionUID = 476402552856829211L;

	public MChargeHistory(Properties ctx, int JP_Charge_History_ID, String trxName)
	{
		super(ctx, JP_Charge_History_ID, trxName);
	}

	public MChargeHistory(Properties ctx, int JP_Charge_History_ID, String trxName, String... virtualColumns) 
	{
		super(ctx, JP_Charge_History_ID, trxName, virtualColumns);
	}

	public MChargeHistory(Properties ctx, ResultSet rs, String trxName) 
	{
		super(ctx, rs, trxName);
	}
	
	@Override
	protected boolean beforeSave(boolean newRecord)
	{
		if(getC_Charge_ID() == 0)
		{
			log.saveError("Error", Msg.getMsg(getCtx(), "C_Charge_ID = 0"));
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
			MChargeHistory[] m_Histories = getOtherHistories();
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


	private MChargeHistory[] getOtherHistories()
	{
		MChargeHistory[] m_Histories = null;

		ArrayList<MChargeHistory> list = new ArrayList<MChargeHistory>();
		String sql = "SELECT * FROM JP_Charge_History WHERE C_Charge_ID=? AND JP_Charge_History_ID <> ?";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, getC_Charge_ID());
			pstmt.setInt(2, getJP_Charge_History_ID());
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add(new MChargeHistory (getCtx(), rs, get_TrxName()));
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

		m_Histories = new MChargeHistory[list.size()];
		list.toArray(m_Histories);
		return m_Histories;
	}


	/**	Cache						*/
	private static CCache<Integer,MChargeHistory[]> s_cache	= new CCache<Integer,MChargeHistory[]>(Table_Name, 100, 10);	//	10 minutes

	public static MChargeHistory[] getHistories (Properties ctx, int C_Charge_ID, String trxName)
	{
		MChargeHistory[] m_Histories = s_cache.get (Integer.valueOf(C_Charge_ID));
		if (m_Histories != null)
		{
			return m_Histories;
		}

		final String whereClause = "C_Charge_ID=?";
		List <MChargeHistory> list = new Query(ctx, I_JP_Charge_History.Table_Name, whereClause, trxName)
		.setParameters(C_Charge_ID)
		.setOrderBy("DateFrom DESC")
		.list();

		m_Histories = new MChargeHistory[list.size()];
		list.toArray(m_Histories);
		s_cache.put(Integer.valueOf(C_Charge_ID), m_Histories);

		return m_Histories;

	}//getHistories

	public static MChargeHistory getHistory (Properties ctx, int C_Charge_ID, Timestamp date, String trxName)
	{
		MChargeHistory m_History = null;
		MChargeHistory[] m_Histories = getHistories(ctx, C_Charge_ID, trxName);
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

	public static String getHistoryName (Properties ctx, int C_Charge_ID, Timestamp date, String trxName)
	{
		MChargeHistory m_History = getHistory(ctx, C_Charge_ID, date, trxName);
		if(m_History == null)
		{
			return MCharge.get(ctx, C_Charge_ID).getName();

		}else {

			return m_History.getName();

		}

	}//getHistoryName

}
