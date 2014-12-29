/******************************************************************************
 * Product: JPiere Plugin Bank Statement Tax
 * Copyright (C) 2014 Hideaki Hagiwara(OSS ERP Solutions)
 *****************************************************************************/
package jpiere.base.plugin.org.adempiere.base;

import jpiere.base.plugin.org.adempiere.model.JPiereBankStatementTaxProvider;
import jpiere.base.plugin.org.adempiere.model.MBankStatementTax;

import org.compiere.model.MBankStatementLine;
import org.compiere.model.MClient;
import org.compiere.model.MTax;
import org.compiere.model.MTaxProvider;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.Env;


/**
 *  JPiere Bank Statement Tax Model Validator
 *
 *  @author Hideaki Hagiwara
 *  @version  $Id: JPiereBankStatementTaxModelValidator.java,v 1.0 2014/08/20
 *
 */
public class JPiereBankStatementTaxModelValidator implements ModelValidator {

	private static CLogger log = CLogger.getCLogger(JPiereBankStatementTaxModelValidator.class);
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

		if(type == ModelValidator.TYPE_AFTER_NEW || type == ModelValidator.TYPE_AFTER_CHANGE){

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
				return "タックスバリデーターを設定して下さい。";//TODO 多言語化
			}

	        MTaxProvider provider = new MTaxProvider(tax.getCtx(), tax.getC_TaxProvider_ID(), tax.get_TrxName());

	        //If Tax Provider class is Setting "jpiere.taxprovider.JPiereTaxProvider",
	        //JPiere Tax Provider of Bank Statement is "jpiere.bankstatementtax.JPiereBankStatementTaxProvider".
	        if(provider.getTaxProviderClass().equals("jpiere.base.plugin.org.adempiere.model.JPiereTaxProvider")){

				Class<?> ppClass = Class.forName("jpiere.base.plugin.org.adempiere.model.JPiereBankStatementTaxProvider");
				JPiereBankStatementTaxProvider calculator = (JPiereBankStatementTaxProvider)ppClass.newInstance();
				boolean isCalculate = calculator.recalculateTax(provider, bsl, newRecord);
				if(!isCalculate)
					return "エラー";

	        }else{

	    		return "タックスバリデータの値が不適切です。jpiere.base.plugin.org.adempiere.model.JPiereTaxProviderを設定して下さい。";//TODO 多言語化

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
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

}
