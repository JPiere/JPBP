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
import org.compiere.util.Env;
import org.compiere.util.Msg;

/**
*
* Deduction for bank transfer fee
*
* JPIERE-0581: Auto Calculate Bank Transfer Fee at Payment Selection Line.
*
* @author h.hagiwara
*
*/
public class MBankTransferFeeDeduct extends X_JP_BankTransferFeeDeduct {

	public MBankTransferFeeDeduct(Properties ctx, int JP_BankTransferFeeDeduct_ID, String trxName)
	{
		super(ctx, JP_BankTransferFeeDeduct_ID, trxName);
	}

	public MBankTransferFeeDeduct(Properties ctx, int JP_BankTransferFeeDeduct_ID, String trxName, String... virtualColumns) 
	{
		super(ctx, JP_BankTransferFeeDeduct_ID, trxName, virtualColumns);
	}

	public MBankTransferFeeDeduct(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}

	@Override
	protected boolean beforeSave(boolean newRecord)
	{
		if(newRecord || is_ValueChanged(COLUMNNAME_C_DocType_ID))
		{
			if(getC_DocType().getDocBaseType().equals("API")
					|| getC_DocType().getDocBaseType().equals("APC"))
			{
				;//Noting to do
			}else{
				
				//Incorrect Document Type
				log.saveError("Error", Msg.getMsg(getCtx(), "JP_DocTypeIncorrect"));
				return false;
			}
		}
			
		
		if(newRecord || is_ValueChanged(COLUMNNAME_M_PriceList_ID))
		{
			if(getM_PriceList().isSOPriceList())
			{
				log.saveError("Error", Msg.getElement(getCtx(), "IsSOPriceList"));
				return false;
			}
			
			setIsTaxIncluded(getM_PriceList().isTaxIncluded());
			setC_Currency_ID(getM_PriceList().getC_Currency_ID());
		}
		
		if((getM_Product_ID() > 0 && getC_Charge_ID() > 0)
				|| (getM_Product_ID() == 0 && getC_Charge_ID() == 0))
		{
			//Only either {0} or {1} can be set.
			Object[] objs = new Object[]{Msg.getElement(getCtx(), "M_Product_ID") ,Msg.getElement(getCtx(), "C_Charge_ID")};
			String msg = Msg.getMsg(Env.getCtx(), "JP_Set_Either", objs);
			log.saveError("Error", msg);
			return false;
		}
		
		return true;
	}
	
	/**	Cache				*/
	private static CCache<Integer,MBankTransferFeeDeduct>	s_cache = new CCache<Integer,MBankTransferFeeDeduct>(Table_Name, 20);
	
	static public MBankTransferFeeDeduct get(int JP_BankTransferFeeDeduct_ID)
	{
		Integer ii = Integer.valueOf(JP_BankTransferFeeDeduct_ID);
		MBankTransferFeeDeduct retValue = (MBankTransferFeeDeduct)s_cache.get(ii);
		if (retValue != null)
			return retValue;
		retValue = new MBankTransferFeeDeduct (Env.getCtx(), JP_BankTransferFeeDeduct_ID, null);
		if (retValue.get_ID () != 0)
			s_cache.put (JP_BankTransferFeeDeduct_ID, retValue);
		return retValue;
	}
	





}
