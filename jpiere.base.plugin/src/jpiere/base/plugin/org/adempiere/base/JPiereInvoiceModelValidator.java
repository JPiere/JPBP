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

import jpiere.base.plugin.org.adempiere.model.MInvoiceJP;
import jpiere.base.plugin.util.JPierePaymentTerms;

import org.compiere.model.MClient;
import org.compiere.model.MDocType;
import org.compiere.model.MInvoice;
import org.compiere.model.MPaymentTerm;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Msg;

public class JPiereInvoiceModelValidator implements ModelValidator {

	private static CLogger log = CLogger.getCLogger(JPiereInvoiceModelValidator.class);
	private int AD_Client_ID = -1;
	private int AD_Org_ID = -1;
	private int AD_Role_ID = -1;
	private int AD_User_ID = -1;

	@Override
	public void initialize(ModelValidationEngine engine, MClient client) {
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();
		engine.addModelChange(MInvoice.Table_Name, this);
		engine.addDocValidate(MInvoice.Table_Name, this);

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
		//JPIERE-0105
		if(type == ModelValidator.TYPE_BEFORE_NEW
				|| (type == ModelValidator.TYPE_BEFORE_CHANGE && po.is_ValueChanged("C_PaymentTerm_ID")))
		{
			MInvoice invoice = (MInvoice)po;
			MPaymentTerm paymentTerm = new MPaymentTerm(Env.getCtx(),invoice.getC_PaymentTerm_ID(),null);
			Boolean IsPaymentTerms = (Boolean)paymentTerm.get_Value("IsPaymentTermsJP");
			if(IsPaymentTerms.booleanValue())
			{
				MPaymentTerm pt= JPierePaymentTerms.getPaymentTerm(Env.getCtx(), invoice.getC_PaymentTerm_ID(), invoice.getDateInvoiced());
				if(pt != null)
				{
					invoice.setC_PaymentTerm_ID(pt.get_ID());
				}

			}//if

		}

		//For Simple Input Window(JPIERE-0175/JPIERE-0176)
		if(type == ModelValidator.TYPE_BEFORE_NEW
				|| (type == ModelValidator.TYPE_BEFORE_CHANGE && po.is_ValueChanged("C_DocTypeTarget_ID"))
				|| (type == ModelValidator.TYPE_BEFORE_CHANGE && po.is_ValueChanged("M_PriceList_ID")))
		{
			MInvoice invoice = (MInvoice)po;
			invoice.setIsSOTrx(invoice.getC_DocTypeTarget().isSOTrx());
			invoice.setIsTaxIncluded(invoice.getM_PriceList().isTaxIncluded());
			invoice.setC_Currency_ID(invoice.getM_PriceList().getC_Currency_ID());
		}

		//JPIERE-0223
		if(type == ModelValidator.TYPE_BEFORE_CHANGE && po.is_ValueChanged("C_DocTypeTarget_ID"))
		{
			MInvoice invoice = (MInvoice)po;
			int C_DocTypeTarget_ID = invoice.get_ValueOldAsInt("C_DocTypeTarget_ID");
			MDocType oldDocType = MDocType.get(po.getCtx(), C_DocTypeTarget_ID);
			if(!invoice.getC_DocTypeTarget().getDocBaseType().equals(oldDocType.getDocBaseType()))
				return Msg.getMsg(po.getCtx(), "JP_Can_Not_Change_Diff_DocBaseType");//You can not change different Doc Base type.
		}

		return null;
	}

	@Override
	public String docValidate(PO po, int timing)
	{
		if(timing ==  ModelValidator.TIMING_BEFORE_PREPARE)//JPIERE-0295
		{
			MInvoiceJP inv = (MInvoiceJP)po;
			inv.explodeBOM();
		}
		
		return null;
	}

}
