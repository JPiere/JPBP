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

import org.compiere.model.MClient;
import org.compiere.model.MPaymentTerm;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Msg;

import jpiere.base.plugin.util.JPierePaymentTerms;

/**
*
* JPiere Payment Term Model Validator
*
* JPIERE-0105:Multi cut off date.
*
* @author h.hagiwara
*
*/
public class JPierePaymentTermModelValidator implements ModelValidator {

	private static CLogger log = CLogger.getCLogger(JPierePaymentTermModelValidator.class);
	private int AD_Client_ID = -1;

	@Override
	public void initialize(ModelValidationEngine engine, MClient client)
	{
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();
		engine.addModelChange(MPaymentTerm.Table_Name, this);

		if (log.isLoggable(Level.FINE)) log.fine("Initialize JPierePaymentTermModelValidator");

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
		//JPIERE-0105
		if(type == ModelValidator.TYPE_BEFORE_NEW || type == ModelValidator.TYPE_BEFORE_CHANGE)
		{
			MPaymentTerm paymentTerm = (MPaymentTerm)po;
			Integer JP_PaymentTerms_ID = (Integer)paymentTerm.get_Value("JP_PaymentTerms_ID");
			if(JP_PaymentTerms_ID == null || JP_PaymentTerms_ID.intValue()==0)
			{
				return null;
			}else{

				if(!paymentTerm.isDueFixed()){
					return Msg.getMsg(paymentTerm.getCtx(), "JP_PaymentTermsBlank_DueFixedOff");//Please Payment Terms blank when Fixed due date will be off
				}

				Boolean IsPaymentTerms = (Boolean)paymentTerm.get_Value("IsPaymentTermsJP");
				if(IsPaymentTerms.booleanValue())
				{
					return Msg.getMsg(paymentTerm.getCtx(), "JP_PaymentTermsBlank_DPaymentTermsOff");//Please Payment Terms blank when Payment Terms will be off
				}

				MPaymentTerm[] paymentTerms = JPierePaymentTerms.getPaymentTerms(Env.getCtx(), JP_PaymentTerms_ID);
				for(int i = 0 ; i < paymentTerms.length; i++)
				{
					if(paymentTerm.getFixMonthCutoff() == paymentTerms[i].getFixMonthCutoff()
							&& paymentTerm.getC_PaymentTerm_ID() != paymentTerms[i].getC_PaymentTerm_ID())
					{
						return Msg.getMsg(paymentTerm.getCtx(), "JP_SameCondition_SamePaymeTerms");//There are same payment term condition in a Payment Terms
					}

					;
				}//for

			}//if

		}//if

		return null;
	}

	@Override
	public String docValidate(PO po, int timing)
	{
		return null;
	}

}
