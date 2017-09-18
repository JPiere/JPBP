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
import java.util.List;
import java.util.Properties;


import org.compiere.model.Query;
import org.compiere.util.CCache;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

/** JPIERE-0363
*
* @author Hideaki Hagiwara
*
*/
public class MContractT extends X_JP_ContractT {
	
	public MContractT(Properties ctx, int JP_ContractT_ID, String trxName) 
	{
		super(ctx, JP_ContractT_ID, trxName);
	}
	
	public MContractT(Properties ctx, ResultSet rs, String trxName) 
	{
		super(ctx, rs, trxName);
	}
	
	/**	Cache				*/
	private static CCache<Integer,MContractT>	s_cache = new CCache<Integer,MContractT>(Table_Name, 20);
	
	
	public static MContractT get (Properties ctx, int JP_ContractT_ID)
	{
		Integer ii = new Integer (JP_ContractT_ID);
		MContractT retValue = (MContractT)s_cache.get(ii);
		if (retValue != null)
			return retValue;
		retValue = new MContractT (ctx, JP_ContractT_ID, null);
		if (retValue.get_ID () != 0)
			s_cache.put (JP_ContractT_ID, retValue);
		return retValue;
	}	//	get

	@Override
	protected boolean beforeSave(boolean newRecord) 
	{
		//Check Automatic update info
		if((newRecord || is_ValueChanged("IsAutomaticUpdateJP")))
		{
			if(isAutomaticUpdateJP())
			{
				
				if(getJP_ContractCancelTerm_ID() == 0)
				{
					Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractCancelTerm_ID")};
					String msg = Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs);
					log.saveError("Error",msg);
					return false;
				}
				
				if(getJP_ContractExtendPeriod_ID() == 0)
				{
					Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractExtendPeriod_ID")};
					String msg = Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs);
					log.saveError("Error",msg);
					return false;
				}
				
			}else{

				setJP_ContractExtendPeriod_ID(0);
				
			}
		}

		return true;
	}
	
	
	private MContractContentT[] m_ContractContentTemplates = null;
	
	public MContractContentT[] getContractContentTemplates (String whereClause, String orderClause)
	{
		StringBuilder whereClauseFinal = new StringBuilder(MContractContentT.COLUMNNAME_JP_ContractT_ID+"=? ");
		if (!Util.isEmpty(whereClause, true))
			whereClauseFinal.append(whereClause);
		if (orderClause.length() == 0)
			orderClause = MContractContentT.COLUMNNAME_JP_ContractContentT_ID;
		//
		List<MContractContentT> list = new Query(getCtx(), MContractContentT.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(get_ID())
										.setOrderBy(orderClause)
										.list();
	
		return list.toArray(new MContractContentT[list.size()]);		

	}
	
	public MContractContentT[] getContractContentTemplates(boolean requery, String orderBy)
	{
		if (m_ContractContentTemplates != null && !requery) {
			set_TrxName(m_ContractContentTemplates, get_TrxName());
			return m_ContractContentTemplates;
		}
		//
		String orderClause = "";
		if (orderBy != null && orderBy.length() > 0)
			orderClause += orderBy;
		else
			orderClause += "JP_ContractContentT_ID";
		m_ContractContentTemplates = getContractContentTemplates(null, orderClause);
		return m_ContractContentTemplates;
	}
	
	public MContractContentT[] getContractContentTemplates()
	{
		return getContractContentTemplates(false, null);
	}
}
