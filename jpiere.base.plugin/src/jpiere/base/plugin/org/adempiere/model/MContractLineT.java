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

import java.sql.ResultSet;
import java.util.Properties;

import org.compiere.util.CCache;
import org.compiere.util.DB;



/**
 * JPIERE-0363
 *
 * @author Hideaki Hagiwara
 *
 */
public class MContractLineT extends X_JP_ContractLineT {
	
	public MContractLineT(Properties ctx, int JP_ContractLineT_ID, String trxName)
	{
		super(ctx, JP_ContractLineT_ID, trxName);
	}
	
	public MContractLineT(Properties ctx, ResultSet rs, String trxName) 
	{
		super(ctx, rs, trxName);
	}

	@Override
	protected boolean beforeSave(boolean newRecord)
	{
		return true;
	}
	
	
	/** Parent					*/
	protected MContractContentT			m_parent = null;

	public MContractContentT getParent()
	{
		if (m_parent == null)
			m_parent = new MContractContentT(getCtx(), getJP_ContractContentT_ID(), get_TrxName());
		return m_parent;
	}	//	getParent
	
	
	@Override
	protected boolean afterSave(boolean newRecord, boolean success) 
	{
		if (!success)
			return success;
//		if (getParent().isProcessed())
//			return success;
		
		if(!newRecord && is_ValueChanged(MContractLineT.COLUMNNAME_LineNetAmt))
		{
			String sql = "UPDATE JP_ContractContentT cct"
					+ " SET TotalLines = "
					    + "(SELECT COALESCE(SUM(LineNetAmt),0) FROM JP_ContractLineT clt WHERE cct.JP_ContractContentT_ID=clt.JP_ContractContentT_ID)"
					+ "WHERE JP_ContractContenTt_ID=?";
				int no = DB.executeUpdate(sql, new Object[]{new Integer(getJP_ContractContentT_ID())}, false, get_TrxName(), 0);
				if (no != 1)
				{
					log.warning("(1) #" + no);
					return false;
				}
		}
		
		return success;
	}
	
	
	/**	Cache				*/
	private static CCache<Integer,MContractLineT>	s_cache = new CCache<Integer,MContractLineT>(Table_Name, 20);
	
	/**
	 * 	Get from Cache
	 *	@param ctx context
	 *	@param JP_ContractLineT_ID id
	 *	@return Contract Calender
	 */
	public static MContractLineT get (Properties ctx, int JP_ContractLineT_ID)
	{
		Integer ii = new Integer (JP_ContractLineT_ID);
		MContractLineT retValue = (MContractLineT)s_cache.get(ii);
		if (retValue != null)
			return retValue;
		retValue = new MContractLineT (ctx, JP_ContractLineT_ID, null);
		if (retValue.get_ID () != 0)
			s_cache.put (JP_ContractLineT_ID, retValue);
		return retValue;
	}	//	get
	
	
}
