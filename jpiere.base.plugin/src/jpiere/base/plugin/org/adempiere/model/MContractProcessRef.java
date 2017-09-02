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

import org.compiere.util.Msg;


/** JPIERE-0363
*
* @author Hideaki Hagiwara
*
*/
public class MContractProcessRef extends X_JP_ContractProcessRef {
	
	public MContractProcessRef(Properties ctx, int JP_ContractProcessRef_ID, String trxName) 
	{
		super(ctx, JP_ContractProcessRef_ID, trxName);
	}
	
	public MContractProcessRef(Properties ctx, ResultSet rs, String trxName) 
	{
		super(ctx, rs, trxName);
	}

	@Override
	protected boolean beforeSave(boolean newRecord) 
	{
		if(newRecord || is_ValueChanged("DocBaseType"))
		{
			if(getDocBaseType().equals(MContractProcessRef.DOCBASETYPE_SalesOrder))
			{
				setIsSOTrx(true);
				setIsCreateBaseDocJP(true);
			}else if(getDocBaseType().equals(MContractProcessRef.DOCBASETYPE_MaterialDelivery)){
				setIsSOTrx(true);
				setIsCreateBaseDocJP(false);
			}else if(getDocBaseType().equals(MContractProcessRef.DOCBASETYPE_ARInvoice)){
				setIsSOTrx(true);
			}else if(getDocBaseType().equals(MContractProcessRef.DOCBASETYPE_PurchaseOrder)){
				setIsSOTrx(true);
				setIsCreateBaseDocJP(false);	
			}else if(getDocBaseType().equals(MContractProcessRef.DOCBASETYPE_MaterialReceipt)){
				setIsSOTrx(false);
				setIsCreateBaseDocJP(false);
			}else if(getDocBaseType().equals(MContractProcessRef.DOCBASETYPE_APInvoice)){
				setIsSOTrx(false);
			}else{
				
				log.saveError("Error", Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "DocBaseType") );
				return false;
				
			}
		}
		
		
		return true;
	}
	
	
	
}
