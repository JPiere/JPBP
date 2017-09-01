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

import org.compiere.model.MDocType;
import org.compiere.model.Query;
import org.compiere.util.Msg;
import org.compiere.util.Util;

/**
 * JPIERE-0363
 *
 * @author Hideaki Hagiwara
 *
 */
public class MContractContentT extends X_JP_ContractContentT {
	
	public MContractContentT(Properties ctx, int JP_ContractContentT_ID, String trxName) 
	{
		super(ctx, JP_ContractContentT_ID, trxName);
	}
	
	public MContractContentT(Properties ctx, ResultSet rs, String trxName) 
	{
		super(ctx, rs, trxName);
	}

	@Override
	protected boolean beforeSave(boolean newRecord) 
	{
		if(newRecord)
		{
			//Check - General Contract can not have Contract Content
			if(getParent().getJP_ContractType().equals(MContractT.JP_CONTRACTTYPE_GeneralContract))
			{
				log.saveError("Error", Msg.getMsg(getCtx(), "JP_GeneralContractContent"));
				return false;
			}
			
			//Check - Template of Spot Contract can have only one Contract Content template.
			if(getParent().getJP_ContractType().equals(MContractT.JP_CONTRACTTYPE_SpotContract)
					&& getParent().getContractContentTemplates(true, null).length > 0 )
			{
				log.saveError("Error", Msg.getMsg(getCtx(), "JP_SpotContractContentTemplate"));
				return false;
			}
		}
		
		if(newRecord || is_ValueChanged(MContractContentT.COLUMNNAME_JP_BaseDocDocType_ID)
				|| is_ValueChanged(MContractContentT.COLUMNNAME_C_DocType_ID))
		{
			MDocType docType = MDocType.get(getCtx(), getJP_BaseDocDocType_ID());
			setIsSOTrx(docType.isSOTrx());
			
			if(!getDocBaseType().equals(docType.getDocBaseType()))
			{
				log.saveError("Error", Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), MContractContentT.COLUMNNAME_JP_BaseDocDocType_ID));
				return false;
			}
		}
		
		return true;
	}
	
	private MContractT parent = null;
	
	public MContractT getParent()
	{
		if(parent == null)
		{
			parent = new MContractT(getCtx(), getJP_ContractT_ID(), null);
		}
		
		return parent;
	}
	
	
	private MContractLineT[] m_ContractLineTemplates = null;
	
	public MContractLineT[] getContractLineTemplates (String whereClause, String orderClause)
	{
		StringBuilder whereClauseFinal = new StringBuilder(MContractLineT.COLUMNNAME_JP_ContractContentT_ID+"=? ");
		if (!Util.isEmpty(whereClause, true))
			whereClauseFinal.append(whereClause);
		if (orderClause.length() == 0)
			orderClause = MContractLineT.COLUMNNAME_Line;
		//
		List<MContractLineT> list = new Query(getCtx(), MContractLineT.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(get_ID())
										.setOrderBy(orderClause)
										.list();
		
		return list.toArray(new MContractLineT[list.size()]);		
	}
	
	public MContractLineT[] getContractLineTemplates (boolean requery, String orderBy)
	{
		if (m_ContractLineTemplates != null && !requery) {
			set_TrxName(m_ContractLineTemplates, get_TrxName());
			return m_ContractLineTemplates;
		}
		//
		String orderClause = "";
		if (orderBy != null && orderBy.length() > 0)
			orderClause += orderBy;
		else
			orderClause += "Line";
		m_ContractLineTemplates = getContractLineTemplates(null, orderClause);
		return m_ContractLineTemplates;
	}


	public MContractLineT[] getContractLineTemplates()
	{
		return getContractLineTemplates(false, null);
	}
	

}
