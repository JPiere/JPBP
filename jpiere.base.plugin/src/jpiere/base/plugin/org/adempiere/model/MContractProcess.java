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


/** JPIERE-0363
*
* @author Hideaki Hagiwara
*
*/
public class MContractProcess extends X_JP_ContractProcess {
	
	public MContractProcess(Properties ctx, int JP_ContractProcess_ID, String trxName) 
	{
		super(ctx, JP_ContractProcess_ID, trxName);
	}
	
	public MContractProcess(Properties ctx, ResultSet rs, String trxName) 
	{
		super(ctx, rs, trxName);
	}

	
	@Override
	protected boolean beforeSave(boolean newRecord)
	{
		if(newRecord || is_ValueChanged("DocBaseType") || is_ValueChanged("IsCreateBaseDocJP"))
		{
			if(getDocBaseType().equals(MContractProcess.DOCBASETYPE_SalesOrder)||getDocBaseType().equals(MContractProcess.DOCBASETYPE_PurchaseOrder))
			{
				setIsCreateBaseDocJP(true);
			}else if(getDocBaseType().equals(MContractProcess.DOCBASETYPE_MaterialDelivery)||getDocBaseType().equals(MContractProcess.DOCBASETYPE_MaterialReceipt))
			{
				setIsCreateBaseDocJP(false);
			}
		}
		
		return true;
	}




	/**	Cache				*/
	private static CCache<Integer,MContractProcess>	s_cache = new CCache<Integer,MContractProcess>(Table_Name, 20);
	
	/**
	 * 	Get from Cache
	 *	@param ctx context
	 *	@param JP_ContractProcess_ID id
	 *	@return Contract Calender
	 */
	public static MContractProcess get (Properties ctx, int JP_ContractProcess_ID)
	{
		Integer ii = new Integer (JP_ContractProcess_ID);
		MContractProcess retValue = (MContractProcess)s_cache.get(ii);
		if (retValue != null)
			return retValue;
		retValue = new MContractProcess (ctx, JP_ContractProcess_ID, null);
		if (retValue.get_ID () != 0)
			s_cache.put (JP_ContractProcess_ID, retValue);
		return retValue;
	}	//	get
	
	
}
