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

import org.compiere.model.MProduct;
import org.compiere.model.Query;
import org.compiere.util.CCache;
import org.compiere.util.DB;
import org.compiere.util.Msg;


/**
 * JPIERE-0558: History of Product name
 * 
 * 
 * @author Hideaki Hagiwara
 *
 */
public class MProductHistory extends X_JP_Product_History {

	private static final long serialVersionUID = -554986296511843832L;

	public MProductHistory(Properties ctx, int JP_Product_History_ID, String trxName) 
	{
		super(ctx, JP_Product_History_ID, trxName);
	}

	public MProductHistory(Properties ctx, int JP_Product_History_ID, String trxName, String... virtualColumns) 
	{
		super(ctx, JP_Product_History_ID, trxName, virtualColumns);
	}

	public MProductHistory(Properties ctx, ResultSet rs, String trxName) 
	{
		super(ctx, rs, trxName);
	}

	@Override
	protected boolean beforeSave(boolean newRecord)
	{
		if(getM_Product_ID() == 0)
		{
			log.saveError("Error", Msg.getMsg(getCtx(), "M_Product_ID = 0"));
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
			MProductHistory[] m_Histories = getOtherHistories();
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


	private MProductHistory[] getOtherHistories()
	{
		MProductHistory[] m_Histories = null;

		ArrayList<MProductHistory> list = new ArrayList<MProductHistory>();
		String sql = "SELECT * FROM JP_Product_History WHERE M_Product_ID=? AND JP_Product_History_ID <> ?";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, getM_Product_ID());
			pstmt.setInt(2, getJP_Product_History_ID());
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add(new MProductHistory (getCtx(), rs, get_TrxName()));
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

		m_Histories = new MProductHistory[list.size()];
		list.toArray(m_Histories);
		return m_Histories;
	}


	/**	Cache						*/
	private static CCache<Integer,MProductHistory[]> s_cache	= new CCache<Integer,MProductHistory[]>(Table_Name, 100, 10);	//	10 minutes

	public static MProductHistory[] getHistories (Properties ctx, int M_Product_ID, String trxName)
	{
		MProductHistory[] m_Histories = s_cache.get (Integer.valueOf(M_Product_ID));
		if (m_Histories != null)
		{
			return m_Histories;
		}

		final String whereClause = "M_Product_ID=?";
		List <MProductHistory> list = new Query(ctx, I_JP_Product_History.Table_Name, whereClause, trxName)
		.setParameters(M_Product_ID)
		.setOrderBy("DateFrom DESC")
		.list();

		m_Histories = new MProductHistory[list.size()];
		list.toArray(m_Histories);
		s_cache.put(Integer.valueOf(M_Product_ID), m_Histories);

		return m_Histories;

	}//getHistories

	public static MProductHistory getHistory (Properties ctx, int M_Product_ID, Timestamp date, String trxName)
	{
		MProductHistory m_History = null;
		MProductHistory[] m_Histories = getHistories(ctx, M_Product_ID, trxName);
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

	public static String getHistoryName (Properties ctx, int M_Product_ID, Timestamp date, String trxName)
	{
		MProductHistory m_History = getHistory(ctx, M_Product_ID, date, trxName);
		if(m_History == null)
		{
			return MProduct.get(ctx, M_Product_ID).getName();

		}else {

			return m_History.getName();

		}

	}//getHistoryName

	
}
