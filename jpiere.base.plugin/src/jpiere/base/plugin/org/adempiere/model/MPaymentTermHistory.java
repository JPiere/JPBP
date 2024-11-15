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

import org.compiere.model.MPaymentTerm;
import org.compiere.model.Query;
import org.compiere.util.CCache;
import org.compiere.util.DB;
import org.compiere.util.Msg;

/**
 * JPIERE-0565: History of Payment Term Name
 * 
 * 
 * @author Hideaki Hagiwara
 *
 */
public class MPaymentTermHistory extends X_JP_PaymentTerm_History {

	private static final long serialVersionUID = -6200853092354310203L;

	public MPaymentTermHistory(Properties ctx, int JP_PaymentTerm_History_ID, String trxName) 
	{
		super(ctx, JP_PaymentTerm_History_ID, trxName);
	}

	public MPaymentTermHistory(Properties ctx, int JP_PaymentTerm_History_ID, String trxName, String... virtualColumns) 
	{
		super(ctx, JP_PaymentTerm_History_ID, trxName, virtualColumns);
	}

	public MPaymentTermHistory(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}
	
	@Override
	protected boolean beforeSave(boolean newRecord)
	{
		if(getC_PaymentTerm_ID() == 0)
		{
			log.saveError("Error", Msg.getMsg(getCtx(), "C_PaymentTerm_ID = 0"));
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
			MPaymentTermHistory[] m_Histories = getOtherHistories();
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


	private MPaymentTermHistory[] getOtherHistories()
	{
		MPaymentTermHistory[] m_Histories = null;

		ArrayList<MPaymentTermHistory> list = new ArrayList<MPaymentTermHistory>();
		String sql = "SELECT * FROM JP_PaymentTerm_History WHERE C_PaymentTerm_ID=? AND JP_PaymentTerm_History_ID <> ?";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, getC_PaymentTerm_ID());
			pstmt.setInt(2, getJP_PaymentTerm_History_ID());
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add(new MPaymentTermHistory (getCtx(), rs, get_TrxName()));
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

		m_Histories = new MPaymentTermHistory[list.size()];
		list.toArray(m_Histories);
		return m_Histories;
	}


	/**	Cache						*/
	private static CCache<Integer,MPaymentTermHistory[]> s_cache	= new CCache<Integer,MPaymentTermHistory[]>(Table_Name, 100, 10);	//	10 minutes

	public static MPaymentTermHistory[] getHistories (Properties ctx, int C_PaymentTerm_ID, String trxName)
	{
		MPaymentTermHistory[] m_Histories = s_cache.get (Integer.valueOf(C_PaymentTerm_ID));
		if (m_Histories != null)
		{
			return m_Histories;
		}

		final String whereClause = "C_PaymentTerm_ID=?";
		List <MPaymentTermHistory> list = new Query(ctx, I_JP_PaymentTerm_History.Table_Name, whereClause, trxName)
		.setParameters(C_PaymentTerm_ID)
		.setOrderBy("DateFrom DESC")
		.list();

		m_Histories = new MPaymentTermHistory[list.size()];
		list.toArray(m_Histories);
		s_cache.put(Integer.valueOf(C_PaymentTerm_ID), m_Histories);

		return m_Histories;

	}//getHistories

	public static MPaymentTermHistory getHistory (Properties ctx, int C_PaymentTerm_ID, Timestamp date, String trxName)
	{
		MPaymentTermHistory m_History = null;
		MPaymentTermHistory[] m_Histories = getHistories(ctx, C_PaymentTerm_ID, trxName);
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

	public static String getHistoryName (Properties ctx, int C_PaymentTerm_ID, Timestamp date, String trxName)
	{
		MPaymentTermHistory m_History = getHistory(ctx, C_PaymentTerm_ID, date, trxName);
		if(m_History == null)
		{
			MPaymentTerm pt = new MPaymentTerm(ctx, C_PaymentTerm_ID, trxName);
			return pt.getName();

		}else {

			return m_History.getName();

		}

	}//getHistoryName

}
