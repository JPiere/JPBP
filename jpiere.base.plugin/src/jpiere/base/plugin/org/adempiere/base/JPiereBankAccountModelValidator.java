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
package jpiere.base.plugin.org.adempiere.base;

import java.util.logging.Level;

import org.compiere.model.MBankAccount;
import org.compiere.model.MClient;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import jpiere.base.plugin.util.ZenginCheck;

/**
 *  JPiere Bank Account Model Validator
 *
 *  JPIERE-0102: Check of Farm Banking Data
 *
 *  @author  Hideaki Hagiwara（h.hagiwara@oss-erp.co.jp）
 *
 */
public class JPiereBankAccountModelValidator implements ModelValidator {

	private static CLogger log = CLogger.getCLogger(JPiereBankAccountModelValidator.class);
	private int AD_Client_ID = -1;

	@Override
	public void initialize(ModelValidationEngine engine, MClient client)
	{
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();
		engine.addModelChange(MBankAccount.Table_Name, this);

		if (log.isLoggable(Level.FINE)) log.fine("Initialize JPiereBankAccountModelValidator");

	}

	@Override
	public int getAD_Client_ID()
	{
		return AD_Client_ID;
	}

	@Override
	public String login(int AD_Org_ID, int AD_Role_ID, int AD_User_ID)
	{
		return null;
	}

	@Override
	public String modelChange(PO po, int type) throws Exception
	{
		//JPIERE-0102
		if(type == ModelValidator.TYPE_BEFORE_NEW || type == ModelValidator.TYPE_BEFORE_CHANGE)
		{
			MBankAccount bankAcct = (MBankAccount)po;
			String jp_RequesterName = (String)bankAcct.get_Value("JP_RequesterName");
			if(!Util.isEmpty(jp_RequesterName))
			{
				for(int i = 0; i < jp_RequesterName.length(); i++)
				{
					if(!ZenginCheck.charCheck(jp_RequesterName.charAt(i)))
					{
						Object[] objs = new Object[]{jp_RequesterName.charAt(i)};
						return Msg.getMsg(Env.getCtx(),"JP_CanNotUseChar",objs);//You can not use this character : {0}.
					}
				}//for

				if(jp_RequesterName.length() > ZenginCheck.JP_RequesterName)
				{
					Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_RequesterName"),ZenginCheck.JP_RequesterName};
					return Msg.getMsg(Env.getCtx(),"JP_LessThanChars",objs);//{0} is less than {1} characters.
				}


				String jp_RequesterCode = (String)bankAcct.get_Value("JP_RequesterCode");
				if(Util.isEmpty(jp_RequesterCode))
				{
					Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_RequesterCode")};
					return Msg.getMsg(Env.getCtx(),"JP_NOT-INOUT",objs);//It is not input in {0}

				}else{

					if(jp_RequesterCode.length()!=ZenginCheck.JP_RequesterCode)
					{
						//{0} is {1} characters.
						Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_RequesterCode"),ZenginCheck.JP_RequesterCode};
						return Msg.getMsg(Env.getCtx(),"JP_Characters",objs);
					}

					if(!ZenginCheck.numStringCheck(jp_RequesterCode))
					{
						//You can not use this String : {0}.
						Object[] objs = new Object[]{jp_RequesterCode};
						return Msg.getElement(Env.getCtx(), "JP_RequesterCode") + " : " + Msg.getMsg(Env.getCtx(),"JP_CanNotUseString",objs);
					}
				}


				String jp_BranchCode = (String)bankAcct.get_Value("JP_BranchCode");
				if(Util.isEmpty(jp_BranchCode))
				{
					Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_BranchCode")};
					return Msg.getMsg(Env.getCtx(),"JP_NOT-INOUT",objs);//It is not input in {0}

				}else{
					if(jp_BranchCode.length()!=ZenginCheck.JP_BranchCode)
					{
						//{0} is {1} characters.
						Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_BranchCode"),ZenginCheck.JP_BranchCode};
						return Msg.getMsg(Env.getCtx(),"JP_Characters",objs);
					}

					if(!ZenginCheck.numStringCheck(jp_BranchCode))
					{
						//You can not use this String : {0}.
						Object[] objs = new Object[]{jp_BranchCode};
						return Msg.getElement(Env.getCtx(), "JP_BranchCode") + " : " + Msg.getMsg(Env.getCtx(),"JP_CanNotUseString",objs);
					}
				}


				String jp_BranchName_Kana = (String)bankAcct.get_Value("JP_BranchName_Kana");
				if(Util.isEmpty(jp_BranchName_Kana))
				{
					Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_BranchName_Kana")};
					return Msg.getMsg(Env.getCtx(),"JP_NOT-INOUT",objs);//It is not input in {0}

				}else{
					for(int i = 0; i < jp_BranchName_Kana.length(); i++)
					{
						if(!ZenginCheck.charCheck(jp_BranchName_Kana.charAt(i)))
						{
							Object[] objs = new Object[]{jp_BranchName_Kana.charAt(i)};
							return Msg.getMsg(Env.getCtx(),"JP_CanNotUseChar",objs);//You can not use this character : {0}.
						}
					}//for

					if(jp_BranchName_Kana.length() > ZenginCheck.JP_BranchName_Kana)
					{
						Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_BranchName_Kana"),ZenginCheck.JP_BranchName_Kana};
						return Msg.getMsg(Env.getCtx(),"JP_LessThanChars",objs);//{0} is less than {1} characters.
					}

				}

				String accountNo = (String)bankAcct.getAccountNo();
				if(Util.isEmpty(accountNo))
				{
					Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "AccountNo")};
					return Msg.getMsg(Env.getCtx(),"JP_NOT-INOUT",objs);//It is not input in {0}

				}else{

					if(accountNo.length()!=ZenginCheck.JP_AccountNo)
					{
						//{0} is {1} characters.
						Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "AccountNo"),ZenginCheck.JP_AccountNo};
						return Msg.getMsg(Env.getCtx(),"JP_Characters",objs);
					}

					if(!ZenginCheck.numStringCheck(accountNo))
					{
						//You can not use this String : {0}.
						Object[] objs = new Object[]{accountNo};
						return Msg.getElement(Env.getCtx(), "AccountNo") + " : " + Msg.getMsg(Env.getCtx(),"JP_CanNotUseString",objs);
					}
				}

				bankAcct.setPaymentExportClass(ZenginCheck.PAYMENT_EXPORT_CLASS);

			}//if(characters != null)
		}

		return null;
	}

	@Override
	public String docValidate(PO po, int timing)
	{
		return null;
	}

}
