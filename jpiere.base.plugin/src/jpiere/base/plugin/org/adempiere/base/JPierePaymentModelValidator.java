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

import java.util.List;
import java.util.logging.Level;

import org.compiere.acct.Fact;
import org.compiere.acct.FactLine;
import org.compiere.model.FactsValidator;
import org.compiere.model.I_C_Payment;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MBankAccount;
import org.compiere.model.MClient;
import org.compiere.model.MPayment;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Msg;


/**
 *  JPiere Payment Model Validator
 *
 *  JPIERE-0087: Check between Payment and Bank Statement
 *  JPIERE-0091: Check between Payment and Bank Statement
 *  JPIERE-0556: Add column to the Journal For legal compliance.
 *
 *  @author  Hideaki Hagiwara（h.hagiwara@oss-erp.co.jp）
 *
 */
public class JPierePaymentModelValidator implements ModelValidator,FactsValidator {

	private static CLogger log = CLogger.getCLogger(JPierePaymentModelValidator.class);
	private int AD_Client_ID = -1;


	@Override
	public void initialize(ModelValidationEngine engine, MClient client)
	{
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();
		engine.addModelChange(MPayment.Table_Name, this);
		engine.addDocValidate(MPayment.Table_Name, this);
		engine.addFactsValidate(MPayment.Table_Name, this);//JPIERE-0556

		if (log.isLoggable(Level.FINE)) log.fine("Initialize JPierePaymentModelValidator");

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

		//JPIERE-0087
		if(type == ModelValidator.TYPE_BEFORE_NEW || type == ModelValidator.TYPE_BEFORE_CHANGE)
		{

			MPayment payment = (MPayment)po;
			MBankAccount ba = MBankAccount.get(payment.getCtx(), payment.getC_BankAccount_ID());
			if(payment.getAD_Org_ID() != ba.getAD_Org_ID())
			{
				//Different between {0} and {1}
				String msg0 = Msg.getElement(Env.getCtx(), "C_BankAccount_ID") + " - " + Msg.getElement(Env.getCtx(), "AD_Org_ID");
				String msg1 = Msg.getElement(Env.getCtx(), "C_Payment_ID") + " - " + Msg.getElement(Env.getCtx(), "AD_Org_ID");
				return Msg.getMsg(Env.getCtx(),"JP_Different",new Object[]{msg0,msg1});

			}

			if(payment.getC_Currency_ID() != ba.getC_Currency_ID())
			{
				//Different between {0} and {1}
				String msg0 = Msg.getElement(Env.getCtx(), "C_BankAccount_ID") + " - " + Msg.getElement(Env.getCtx(), "C_Currency_ID");
				String msg1 = Msg.getElement(Env.getCtx(), "C_Payment_ID") + " - " + Msg.getElement(Env.getCtx(), "C_Currency_ID");
				return Msg.getMsg(Env.getCtx(),"JP_Different",new Object[]{msg0,msg1});
			}

		}

		return null;
	}

	@Override
	public String docValidate(PO po, int timing)
	{

		//JPIERE-0091
		if(timing == ModelValidator.TIMING_BEFORE_VOID ||
				timing == ModelValidator.TIMING_BEFORE_REVERSEACCRUAL ||
				timing == ModelValidator.TIMING_BEFORE_REVERSECORRECT ||
				timing == ModelValidator.TIMING_AFTER_VOID ||
				timing == ModelValidator.TIMING_AFTER_REVERSEACCRUAL ||
				timing == ModelValidator.TIMING_AFTER_REVERSECORRECT )
		{
			MPayment payment = (MPayment)po;
			if(payment.isReconciled())
			{
				//This Payment has been written in Bank Statement. Please void or reverse the Bank Statement.
				return Msg.getMsg(Env.getCtx(),"JP_PaymentWrittenBankStatement");
			}
		}

		return null;
	}

	@Override
	public String factsValidate(MAcctSchema schema, List<Fact> facts, PO po) 
	{
		if(po instanceof I_C_Payment)
		{
			I_C_Payment i_Payment = (I_C_Payment)po;
		
			//JPIERE-0556: Add column to the Journal For legal compliance.
			for(Fact fact : facts)
			{
				FactLine[]  factLine = fact.getLines();
				for(int i = 0; i < factLine.length; i++)
				{
					factLine[i].set_ValueNoCheck("JP_BankAccount_ID", i_Payment.getC_BankAccount_ID());
					factLine[i].set_ValueNoCheck("JP_Charge_ID", i_Payment.getC_Charge_ID());
				}//for
	
			}//for
		}
		
		return null;
	}

}
