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

import org.compiere.model.MBank;
import org.compiere.model.MClient;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Msg;

import jpiere.base.plugin.util.ZenginCheck;


/**
 *  JPiere Bank Model Validator
 *
 *  JPIERE-0102: Check of Farm Banking Data
 *
 *  @author  Hideaki Hagiwara（h.hagiwara@oss-erp.co.jp）
 *
 */
public class JPiereBankModelValidator implements ModelValidator {

	private static CLogger log = CLogger.getCLogger(JPiereBankModelValidator.class);
	private int AD_Client_ID = -1;

	@Override
	public void initialize(ModelValidationEngine engine, MClient client)
	{
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();
		engine.addModelChange(MBank.Table_Name, this);

		if (log.isLoggable(Level.FINE)) log.fine("Initialize JPiereBankModelValidator");
	}

	@Override
	public int getAD_Client_ID() {
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

			MBank bank = (MBank)po;
			String characters = (String)bank.get_Value("JP_BankName_Kana");
			if(characters != null)
			{
				for(int i = 0; i < characters.length(); i++)
				{
					if(!ZenginCheck.charCheck(characters.charAt(i)))
					{
						Object[] objs = new Object[]{characters.charAt(i)};
						return Msg.getMsg(Env.getCtx(),"JP_CanNotUseChar",objs);//You can not use this character : {0}.
					}
				}//for

				if(characters.length() > ZenginCheck.JP_BankName_Kana)
				{
					Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_BankName_Kana"), ZenginCheck.JP_BankName_Kana};
					return Msg.getMsg(Env.getCtx(),"JP_LessThanChars",objs);//{0} is less than {1} characters.
				}

				if(bank.getRoutingNo().length()!= ZenginCheck.JP_RoutingNo)
				{
					//{0} is {1} characters.
					Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "RoutingNo"), ZenginCheck.JP_RoutingNo};
					return Msg.getMsg(Env.getCtx(),"JP_Characters",objs);
				}

				if(!ZenginCheck.numStringCheck(bank.getRoutingNo()))
				{
					//You can not use this String : {0}.
					Object[] objs = new Object[]{bank.getRoutingNo()};
					return Msg.getElement(Env.getCtx(), "RoutingNo") + " : " + Msg.getMsg(Env.getCtx(),"JP_CanNotUseString",objs);

				}

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
