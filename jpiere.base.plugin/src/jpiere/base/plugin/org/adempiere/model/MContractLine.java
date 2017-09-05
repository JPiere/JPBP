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
public class MContractLine extends X_JP_ContractLine {
	
	public MContractLine(Properties ctx, int JP_ContractLine_ID, String trxName) 
	{
		super(ctx, JP_ContractLine_ID, trxName);
	}
	
	public MContractLine(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}
	
	/**	Cache				*/
	private static CCache<Integer,MContractLine>	s_cache = new CCache<Integer,MContractLine>(Table_Name, 20);
	
	/**
	 * 	Get from Cache
	 *	@param ctx context
	 *	@param JP_ContractLine_ID id
	 *	@return Contract Calender
	 */
	public static MContractLine get (Properties ctx, int JP_ContractLine_ID)
	{
		Integer ii = new Integer (JP_ContractLine_ID);
		MContractLine retValue = (MContractLine)s_cache.get(ii);
		if (retValue != null)
			return retValue;
		retValue = new MContractLine (ctx, JP_ContractLine_ID, null);
		if (retValue.get_ID () != 0)
			s_cache.put (JP_ContractLine_ID, retValue);
		return retValue;
	}	//	get
	
	
	/** Parent					*/
	protected MContractContent			m_parent = null;

	public MContractContent getParent()
	{
		if (m_parent == null)
			m_parent = new MContractContent(getCtx(), getJP_ContractContent_ID(), get_TrxName());
		return m_parent;
	}	//	getParent
	
	
	@Override
	protected boolean beforeSave(boolean newRecord) 
	{
		//TODO 契約処理が開始されたら、契約カレンダーは変更できない旨のチェックロジックの実装
		//伝票が作成されたから契約カレンダーを変更されてしまうとデータに整合性がなくなｔってしいまう。
		
		return true;
	}

	@Override
	protected boolean afterSave(boolean newRecord, boolean success) 
	{
		if (!success)
			return success;
		if (getParent().isProcessed())
			return success;
		
		if(newRecord || is_ValueChanged(MContractLine.COLUMNNAME_LineNetAmt))
		{
			String sql = "UPDATE JP_ContractContent cc"
					+ " SET TotalLines = "
					    + "(SELECT COALESCE(SUM(LineNetAmt),0) FROM JP_ContractLine cl WHERE cc.JP_ContractContent_ID=cl.JP_ContractContent_ID)"
					+ "WHERE JP_ContractContent_ID=?";
				int no = DB.executeUpdate(sql, new Object[]{new Integer(getJP_ContractContent_ID())}, false, get_TrxName(), 0);
				if (no != 1)
				{
					log.warning("(1) #" + no);
					return false;
				}
		}
		
		return success;
	}
	
	
}
