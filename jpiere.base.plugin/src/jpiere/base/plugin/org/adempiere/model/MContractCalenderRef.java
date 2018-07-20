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

import org.compiere.util.CCache;
import org.compiere.util.DB;


/** JPIERE-0363
*
* @author Hideaki Hagiwara
*
*/
public class MContractCalenderRef extends X_JP_ContractCalenderRef {

	public MContractCalenderRef(Properties ctx, int JP_ContractCalenderRef_ID, String trxName)
	{
		super(ctx, JP_ContractCalenderRef_ID, trxName);
	}

	public MContractCalenderRef(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}


	/**	Cache				*/
	private static CCache<Integer,MContractCalenderRef>	s_cache = new CCache<Integer,MContractCalenderRef>(Table_Name, 20);


	/**
	 * 	Get from Cache
	 *	@param ctx context
	 *	@param JP_ContractCalenderRef_ID id
	 *	@return Contract Calender Reference
	 */
	public static MContractCalenderRef get (Properties ctx, int JP_ContractCalenderRef_ID)
	{
		Integer ii = new Integer (JP_ContractCalenderRef_ID);
		MContractCalenderRef retValue = (MContractCalenderRef)s_cache.get(ii);
		if (retValue != null)
			return retValue;
		retValue = new MContractCalenderRef (ctx, JP_ContractCalenderRef_ID, null);
		if (retValue.get_ID () != 0)
			s_cache.put (JP_ContractCalenderRef_ID, retValue);
		return retValue;
	}	//	get


	private MContractCalenderList[]		m_ContractCalenderLists = null;

	/**
	 * Get Contract Calender List
	 *
	 * @param ctx
	 * @param requery
	 * @param trxName
	 * @return
	 */
	public MContractCalenderList[] getContractCalenderList(Properties ctx, boolean requery, String trxName)
	{
		if (m_ContractCalenderLists != null && m_ContractCalenderLists.length >= 0 && !requery)	//	re-load
			return m_ContractCalenderLists;
		//
		ArrayList<MContractCalenderList> list = new ArrayList<MContractCalenderList>();
		String sql = "SELECT * FROM JP_ContractCalenderList WHERE JP_ContractCalenderRef_ID=? AND IsActive='Y'";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, trxName);
			pstmt.setInt(1, getJP_ContractCalenderRef_ID());
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add(new MContractCalenderList (ctx, rs, trxName));
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

		m_ContractCalenderLists = new MContractCalenderList[list.size()];
		list.toArray(m_ContractCalenderLists);
		return m_ContractCalenderLists;
	}

}
