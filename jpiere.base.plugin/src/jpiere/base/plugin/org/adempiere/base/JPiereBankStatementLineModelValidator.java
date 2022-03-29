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

import java.math.BigDecimal;
import java.util.logging.Level;

import org.compiere.model.MBankAccount;
import org.compiere.model.MBankStatement;
import org.compiere.model.MBankStatementLine;
import org.compiere.model.MClient;
import org.compiere.model.MInvoice;
import org.compiere.model.MPayment;
import org.compiere.model.MTax;
import org.compiere.model.MTaxProvider;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.process.DocAction;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Msg;

import jpiere.base.plugin.org.adempiere.model.JPiereBankStatementTaxProvider;
import jpiere.base.plugin.org.adempiere.model.MBankStatementTax;


/**
 *  JPiere Bank Statement Line Model Validator
 *
 *  JPIERE-0012: Tax at Bank Statement
 *  JPIERE-0087: Org check at Bank Statemnt and Account.
 *  JPIERE-0300: Control of Bank Statemnet and Payment relation.
 *
 *  @author  Hideaki Hagiwara（h.hagiwara@oss-erp.co.jp）
 *
 */
public class JPiereBankStatementLineModelValidator implements ModelValidator {

	private static CLogger log = CLogger.getCLogger(JPiereBankStatementLineModelValidator.class);
	private int AD_Client_ID = -1;


	@Override
	public void initialize(ModelValidationEngine engine, MClient client)
	{
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();
		engine.addModelChange(MBankStatementLine.Table_Name, this);

		if (log.isLoggable(Level.FINE)) log.fine("Initialize JPiereBankStatementLineModelValidator");

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

		//JPIERE-0012 & JPIERE-0087
		if(type == ModelValidator.TYPE_BEFORE_NEW || type == ModelValidator.TYPE_BEFORE_CHANGE)
		{
			MBankStatementLine bsl = (MBankStatementLine)po;
			
			//JPIERE-0012 -- Calculate Tax of Bank Statemet Line 
			if(bsl.getChargeAmt().compareTo(Env.ZERO) == 0)
			{
				
				bsl.setC_Charge_ID(0);
				bsl.set_ValueNoCheck("C_Tax_ID", null);
				bsl.set_ValueNoCheck("JP_SOPOType", null);
				
			}else {
			
				Object C_Tax_ID = bsl.get_Value("C_Tax_ID");
				Object JP_SOPOType = bsl.get_Value("JP_SOPOType");
				if(C_Tax_ID != null && JP_SOPOType == null)
				{
					if(bsl.getChargeAmt().compareTo(Env.ZERO) > 0)
					{
						bsl.set_ValueNoCheck("JP_SOPOType", "S");
					}else if(bsl.getChargeAmt().compareTo(Env.ZERO) < 0) {
						bsl.set_ValueNoCheck("JP_SOPOType", "P");
					}else{
						bsl.set_ValueNoCheck("JP_SOPOType", "N");
					}
				}	
			}

			//JPIERE-0087
			MBankAccount ba = MBankAccount.get(bsl.getCtx(), bsl.getParent().getC_BankAccount_ID());
			if(bsl.getAD_Org_ID() != ba.getAD_Org_ID())
			{
				return Msg.getMsg(bsl.getCtx(), "JP_DifferentOrg");
			}

			if(bsl.getC_Currency_ID() != ba.getC_Currency_ID())
			{
				return Msg.getMsg(bsl.getCtx(), "JP_DifferentCurrency");
			}

		}
		
		
		//JPIERE-0012 & JPIERE-0300
		if(type == ModelValidator.TYPE_AFTER_NEW || type == ModelValidator.TYPE_AFTER_CHANGE)
		{

			MBankStatementLine bsl = (MBankStatementLine)po;

			//JPIERE-0300
			if(type == ModelValidator.TYPE_AFTER_NEW || bsl.is_ValueChanged("C_Payment_ID"))
			{
				int new_Payment_ID = bsl.getC_Payment_ID();
				int old_Payment_ID = bsl.get_ValueOldAsInt("C_Payment_ID");

				if(new_Payment_ID > 0)
				{
					MPayment newPayment = new MPayment(bsl.getCtx(), new_Payment_ID, bsl.get_TrxName());
					if(newPayment.getDocStatus().equals(DocAction.ACTION_Complete) || newPayment.getDocStatus().equals(DocAction.ACTION_Close))
					{
						;//Nothing to do;
					}else if(newPayment.getDocStatus().equals(DocAction.STATUS_Voided) || newPayment.getDocStatus().equals(DocAction.STATUS_Reversed)
							|| newPayment.getDocStatus().equals(DocAction.STATUS_Invalid) )
					{
						return Msg.getMsg(bsl.getCtx(), "JP_NotValidDocStatus");//Not Valid Doc Status

					}else{

						MBankStatement bs = bsl.getParent();
						if(bs.getDocStatus().equals(DocAction.STATUS_Completed) || bs.getDocStatus().equals(DocAction.STATUS_Closed))
						{
							return Msg.getMsg(bsl.getCtx(), "JP_NotMatchIncompletePaymentAndCompleteBS");//Not match incomplete Payment and complete Bank Statement
						}

					}


					if(newPayment.isReconciled())
						return Msg.getMsg(bsl.getCtx(), "JP_AlreadyReconciled");//Payment was reconciled with bank statement already

					if(bsl.getC_BPartner_ID() != newPayment.getC_BPartner_ID())
						return Msg.getMsg(bsl.getCtx(), "JP_DifferentBusinessPartner_Payment");//Different business partner between Payment and BP field

					BigDecimal payAmt = newPayment.getPayAmt();
					if(!newPayment.isReceipt())
						payAmt = payAmt.negate();

					if(bsl.getTrxAmt().compareTo(payAmt) != 0)
					{
						return Msg.getMsg(bsl.getCtx(), "JP_DifferentAmt");//Different Amount
					}


					MBankStatement bs = bsl.getParent();
					if(bs.getDocStatus().equals(DocAction.STATUS_Completed) || bs.getDocStatus().equals(DocAction.STATUS_Closed))
					{
						newPayment.setIsReconciled(true);
						newPayment.saveEx(bsl.get_TrxName());
					}

				}

				if(old_Payment_ID > 0 && new_Payment_ID != old_Payment_ID)
				{
					old_Payment_ID = bsl.get_ValueOldAsInt("C_Payment_ID");
					MPayment oldPayment = new MPayment(bsl.getCtx(), old_Payment_ID, bsl.get_TrxName());
					oldPayment.setIsReconciled(false);
					oldPayment.saveEx(bsl.get_TrxName());
				}
			}

			//JPIERE-0300
			if(type == ModelValidator.TYPE_AFTER_NEW || bsl.is_ValueChanged("C_BPartner_ID"))
			{
				int new_BPartner_ID = bsl.getC_BPartner_ID();
				int C_Payment_ID = bsl.getC_Payment_ID();
				if(C_Payment_ID > 0)
				{
					MPayment payment = new MPayment(bsl.getCtx(), C_Payment_ID, bsl.get_TrxName());
					if(new_BPartner_ID != payment.getC_BPartner_ID())
						return Msg.getMsg(bsl.getCtx(), "JP_DifferentBusinessPartner_Payment");//Different business partner between Payment and BP field
				}

				int C_Invoice_ID = bsl.getC_Invoice_ID();
				if(C_Invoice_ID > 0)
				{
					MInvoice invoice = new MInvoice(bsl.getCtx(), C_Invoice_ID, bsl.get_TrxName());
					if(new_BPartner_ID != invoice.getC_BPartner_ID())
						return Msg.getMsg(bsl.getCtx(), "JP_DifferentBusinessPartner_Invoice");//Different business partner between Invoice and BP field
				}

			}


			//JPIERE-0012
			boolean newRecord = true;
			if(type == ModelValidator.TYPE_AFTER_CHANGE)
				newRecord = false;

			if(bsl.getChargeAmt().compareTo(Env.ZERO)==0)
			{
				PO bst = MBankStatementTax.get(bsl.getCtx(), bsl.getC_BankStatementLine_ID());
				if(bst!=null)
				{
					bst.deleteEx(false);
				}
				return null;
			}

			Object C_Tax_ID = bsl.get_Value("C_Tax_ID");
			if(C_Tax_ID==null)
			{
				PO bst = MBankStatementTax.get(bsl.getCtx(), bsl.getC_BankStatementLine_ID());
				if(bst!=null)
				{
					bst.deleteEx(false);
				}
				return null;
			}
			
			String JP_SOPOType = bsl.get_ValueAsString("JP_SOPOType");
			if(JP_SOPOType == null || "N".equals(JP_SOPOType))
			{
				PO bst = MBankStatementTax.get(bsl.getCtx(), bsl.getC_BankStatementLine_ID());
				if(bst!=null)
				{
					bst.deleteEx(false);
				}
				return null;
			}
			
			MTax tax = new MTax(po.getCtx(), Integer.valueOf(C_Tax_ID.toString()).intValue(), po.get_TrxName());
			if(tax.getC_TaxProvider_ID()==0){
				return Msg.getMsg(bsl.getCtx(), "JP_SetTaxProvider");
			}

	        MTaxProvider provider = new MTaxProvider(tax.getCtx(), tax.getC_TaxProvider_ID(), tax.get_TrxName());

	        //If Tax Provider class is Setting "jpiere.taxprovider.JPiereTaxProvider",
	        //JPiere Tax Provider of Bank Statement is "jpiere.bankstatementtax.JPiereBankStatementTaxProvider".
	        if(provider.getTaxProviderClass().equals("jpiere.base.plugin.org.adempiere.model.JPiereTaxProvider"))
	        {
				Class<?> ppClass = Class.forName("jpiere.base.plugin.org.adempiere.model.JPiereBankStatementTaxProvider");
				JPiereBankStatementTaxProvider calculator = (JPiereBankStatementTaxProvider)ppClass.getDeclaredConstructor().newInstance();
				boolean isCalculate = calculator.recalculateTax(provider, bsl, newRecord);
				if(!isCalculate)
					return Msg.getMsg(bsl.getCtx(), "Error");

	        }else{

	    		return Msg.getMsg(bsl.getCtx(), "JP_InvalidTaxProvider");

	        }

		}//JPIERE-0012 & JPIERE-0300
		
		
		//JPIERE-0012
		if(type == ModelValidator.TYPE_AFTER_DELETE)
		{
			MBankStatementLine bsl = (MBankStatementLine)po;
			PO bst = MBankStatementTax.get(bsl.getCtx(), bsl.getC_BankStatementLine_ID());
			if(bst!=null){
				bst.deleteEx(false);
			}
		}

		return null;
	}

	@Override
	public String docValidate(PO po, int timing) {

		return null;
	}

}
