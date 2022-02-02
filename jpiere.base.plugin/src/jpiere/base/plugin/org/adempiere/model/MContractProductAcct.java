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

import org.compiere.util.Env;
import org.compiere.util.Msg;


/**
 * JPIERE-0363: Contract Management
 * JPIERE-0539: Create GL Journal From Invoice
 *
 * @author Hideaki Hagiwara
 *
 **/
public class MContractProductAcct extends X_JP_Contract_Product_Acct {

	public MContractProductAcct(Properties ctx, int JP_Contract_Product_Acct_ID, String trxName)
	{
		super(ctx, JP_Contract_Product_Acct_ID, trxName);
	}

	public MContractProductAcct(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}

	@Override
	protected boolean beforeSave(boolean newRecord)
	{
		if(getJP_Revenue_Acct() > 0 && getJP_GL_Revenue_Acct() > 0)
		{
			Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), COLUMNNAME_JP_Revenue_Acct), Msg.getElement(Env.getCtx(), COLUMNNAME_JP_GL_Revenue_Acct)};

			//Only either {0} or {1} can be set.
			String msg = Msg.getMsg(Env.getCtx(), "JP_Set_Either", objs);
			log.saveError("Error", msg);
			return false;
		}

		if(getJP_TradeDiscountGrant_Acct() > 0 && getJP_GL_TradeDiscountGrant_Acct() > 0)
		{
			Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), COLUMNNAME_JP_TradeDiscountGrant_Acct), Msg.getElement(Env.getCtx(), COLUMNNAME_JP_GL_TradeDiscountGrant_Acct)};

			//Only either {0} or {1} can be set.
			String msg = Msg.getMsg(Env.getCtx(), "JP_Set_Either", objs);
			log.saveError("Error", msg);
			return false;
		}


		if(getJP_Expense_Acct() > 0 && getJP_GL_Expense_Acct() > 0)
		{
			Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), COLUMNNAME_JP_Expense_Acct), Msg.getElement(Env.getCtx(), COLUMNNAME_JP_GL_Expense_Acct)};

			//Only either {0} or {1} can be set.
			String msg = Msg.getMsg(Env.getCtx(), "JP_Set_Either", objs);
			log.saveError("Error", msg);
			return false;
		}


		if(getJP_TradeDiscountRec_Acct() > 0 && getJP_GL_TradeDiscountRec_Acct() > 0)
		{
			Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), COLUMNNAME_JP_TradeDiscountRec_Acct), Msg.getElement(Env.getCtx(), COLUMNNAME_JP_GL_TradeDiscountRec_Acct)};

			//Only either {0} or {1} can be set.
			String msg = Msg.getMsg(Env.getCtx(), "JP_Set_Either", objs);
			log.saveError("Error", msg);
			return false;
		}

		return true;
	}



}
