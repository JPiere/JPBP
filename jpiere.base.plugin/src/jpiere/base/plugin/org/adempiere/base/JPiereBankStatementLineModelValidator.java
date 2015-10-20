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

import jpiere.base.plugin.org.adempiere.model.JPiereBankStatementTaxProvider;
import jpiere.base.plugin.org.adempiere.model.MBankStatementTax;

import org.compiere.model.MBankAccount;
import org.compiere.model.MBankStatementLine;
import org.compiere.model.MClient;
import org.compiere.model.MTax;
import org.compiere.model.MTaxProvider;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Msg;


/**
 *  JPiere Bank Statement Line Model Validator
 *  JPIERE-0012
 *
 *  @author  Hideaki Hagiwara（h.hagiwara@oss-erp.co.jp）
 *  @version  $Id: JPiereBankStatementTaxModelValidator.java,v 1.0 2014/08/20
 *
 */
public class JPiereBankStatementLineModelValidator implements ModelValidator {

	private static CLogger log = CLogger.getCLogger(JPiereBankStatementLineModelValidator.class);
	private int AD_Client_ID = -1;
	private int AD_Org_ID = -1;
	private int AD_Role_ID = -1;
	private int AD_User_ID = -1;


	@Override
	public void initialize(ModelValidationEngine engine, MClient client) {
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();
		engine.addModelChange(MBankStatementLine.Table_Name, this);

	}

	@Override
	public int getAD_Client_ID() {

		return AD_Client_ID;
	}

	@Override
	public String login(int AD_Org_ID, int AD_Role_ID, int AD_User_ID) {
		this.AD_Org_ID = AD_Org_ID;
		this.AD_Role_ID = AD_Role_ID;
		this.AD_User_ID = AD_User_ID;

		return null;
	}

	@Override
	public String modelChange(PO po, int type) throws Exception {

		//JPIERE-0087
		if(type == ModelValidator.TYPE_BEFORE_NEW || type == ModelValidator.TYPE_BEFORE_CHANGE)
		{

			MBankStatementLine bsl = (MBankStatementLine)po;
			MBankAccount ba = MBankAccount.get(bsl.getCtx(), bsl.getParent().getC_BankAccount_ID());
			if(bsl.getAD_Org_ID() != ba.getAD_Org_ID())
			{
				return Msg.getMsg(bsl.getCtx(), "JP_DifferentOrg");
			}

			if(bsl.getC_Currency_ID() != ba.getC_Currency_ID())
			{
				return Msg.getMsg(bsl.getCtx(), "JP_DifferentCurrency");
			}

			if(bsl.getChargeAmt().compareTo(Env.ZERO)==0)
			{
				bsl.setC_Charge_ID(0);
				bsl.set_ValueNoCheck("C_Tax_ID", null);
			}

		}
		//JPIERE-0012
		else if(type == ModelValidator.TYPE_AFTER_NEW || type == ModelValidator.TYPE_AFTER_CHANGE)
		{

			boolean newRecord = true;
			if(type == ModelValidator.TYPE_AFTER_CHANGE)
				newRecord = false;

			MBankStatementLine bsl = (MBankStatementLine)po;

			if(bsl.getChargeAmt().compareTo(Env.ZERO)==0){
				PO bst = MBankStatementTax.get(bsl.getCtx(), bsl.getC_BankStatementLine_ID());
				if(bst!=null){
					bst.deleteEx(false);
				}
				return null;
			}

			Object C_Tax_ID = po.get_Value("C_Tax_ID");
			if(C_Tax_ID==null){
				PO bst = MBankStatementTax.get(bsl.getCtx(), bsl.getC_BankStatementLine_ID());
				if(bst!=null){
					bst.deleteEx(false);
				}
				return null;
			}

			MTax tax = new MTax(po.getCtx(), new Integer(C_Tax_ID.toString()).intValue(), po.get_TrxName());
			if(tax.getC_TaxProvider_ID()==0){
				return Msg.getMsg(bsl.getCtx(), "JP_SetTaxProvider");
			}

	        MTaxProvider provider = new MTaxProvider(tax.getCtx(), tax.getC_TaxProvider_ID(), tax.get_TrxName());

	        //If Tax Provider class is Setting "jpiere.taxprovider.JPiereTaxProvider",
	        //JPiere Tax Provider of Bank Statement is "jpiere.bankstatementtax.JPiereBankStatementTaxProvider".
	        if(provider.getTaxProviderClass().equals("jpiere.base.plugin.org.adempiere.model.JPiereTaxProvider")){

				Class<?> ppClass = Class.forName("jpiere.base.plugin.org.adempiere.model.JPiereBankStatementTaxProvider");
				JPiereBankStatementTaxProvider calculator = (JPiereBankStatementTaxProvider)ppClass.newInstance();
				boolean isCalculate = calculator.recalculateTax(provider, bsl, newRecord);
				if(!isCalculate)
					return Msg.getMsg(bsl.getCtx(), "Error");

	        }else{

	    		return Msg.getMsg(bsl.getCtx(), "JP_InvalidTaxProvider");

	        }

		}else if(type == ModelValidator.TYPE_AFTER_DELETE){
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
