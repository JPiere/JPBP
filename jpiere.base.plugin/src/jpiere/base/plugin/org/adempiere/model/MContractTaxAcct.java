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
public class MContractTaxAcct extends X_JP_Contract_Tax_Acct {

	public MContractTaxAcct(Properties ctx, int JP_Contract_Tax_Acct_ID, String trxName)
	{
		super(ctx, JP_Contract_Tax_Acct_ID, trxName);
	}

	public MContractTaxAcct(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}

	@Override
	protected boolean beforeSave(boolean newRecord)
	{
		if(getJP_TaxDue_Acct() > 0 && getJP_GL_TaxDue_Acct() > 0)
		{
			Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), COLUMNNAME_JP_TaxDue_Acct), Msg.getElement(Env.getCtx(), COLUMNNAME_JP_GL_TaxDue_Acct)};

			//Only either {0} or {1} can be set.
			String msg = Msg.getMsg(Env.getCtx(), "JP_Set_Either", objs);
			log.saveError("Error", msg);
			return false;
		}

		if(getJP_TaxCredit_Acct() > 0 && getJP_GL_TaxCredit_Acct() > 0)
		{
			Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), COLUMNNAME_JP_TaxCredit_Acct), Msg.getElement(Env.getCtx(), COLUMNNAME_JP_GL_TaxCredit_Acct)};

			//Only either {0} or {1} can be set.
			String msg = Msg.getMsg(Env.getCtx(), "JP_Set_Either", objs);
			log.saveError("Error", msg);
			return false;
		}

		if(getJP_TaxExpense_Acct() > 0 && getJP_GL_TaxExpense_Acct() > 0)
		{
			Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), COLUMNNAME_JP_TaxExpense_Acct), Msg.getElement(Env.getCtx(), COLUMNNAME_JP_GL_TaxExpense_Acct)};

			//Only either {0} or {1} can be set.
			String msg = Msg.getMsg(Env.getCtx(), "JP_Set_Either", objs);
			log.saveError("Error", msg);
			return false;
		}

		return true;
	}
}
