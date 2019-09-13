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

import java.sql.Timestamp;
import java.util.logging.Level;

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

import jpiere.base.plugin.org.adempiere.model.MInvoiceJP;
import jpiere.base.plugin.util.JPierePaymentTerms;

/**
*
* JPiere Invoice Model Validator
*
* JPIERE-0105: Multi cut off date.
* JPIERE-0175: Simple Input Window (AR Invoice)
* JPIERE-0176: Simple Input Window (AP Invoice)
* JPIERE-0223: Restrict Match Inv.
* JPIERE-0295: Explode BOM
* JPIERE-0368: Period Closing by Payment Term
*
* @author h.hagiwara
*
*/
public class JPiereInvoiceModelValidator implements ModelValidator {

	private static CLogger log = CLogger.getCLogger(JPiereInvoiceModelValidator.class);
	private int AD_Client_ID = -1;

	@Override
	public void initialize(ModelValidationEngine engine, MClient client)
	{
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();
		engine.addModelChange(MInvoice.Table_Name, this);
		engine.addDocValidate(MInvoice.Table_Name, this);

		if (log.isLoggable(Level.FINE)) log.fine("Initialize JPiereInvoiceModelValidator");

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
		//JPIERE-0295 Explode BOM
		if(timing ==  ModelValidator.TIMING_BEFORE_PREPARE)
		{
			if(po instanceof MInvoiceJP)
			{
				MInvoiceJP inv = (MInvoiceJP)po;
				inv.explodeBOM();
			}
		}

		//JPIERE-0368 Closing by Payment Term
		if(timing ==  ModelValidator.TIMING_BEFORE_PREPARE)
		{
			MInvoice invoice = (MInvoice)po;
			MPaymentTerm paymentTerm = new MPaymentTerm(Env.getCtx(),invoice.getC_PaymentTerm_ID(),null);

			if(invoice.isSOTrx())
			{
				Object obj_AR_ClosingDate= paymentTerm.get_Value("JP_AR_ClosingDate");
				if(obj_AR_ClosingDate != null)
				{
					Timestamp JP_AR_ClosingDate = (Timestamp)obj_AR_ClosingDate;
					if(invoice.getDateInvoiced().compareTo(JP_AR_ClosingDate) <= 0
							|| invoice.getDateAcct().compareTo(JP_AR_ClosingDate) <= 0)
					{
						//Closing Date By Payment Term
						return Msg.getMsg(po.getCtx(), "JP_PaymentTerm_ClosingDate");
					}
				}

			}else {

				Object obj_AP_ClosingDate= paymentTerm.get_Value("JP_AP_ClosingDate");
				if(obj_AP_ClosingDate != null)
				{
					Timestamp JP_AP_ClosingDate = (Timestamp)obj_AP_ClosingDate;
					if(invoice.getDateInvoiced().compareTo(JP_AP_ClosingDate) <= 0
							|| invoice.getDateAcct().compareTo(JP_AP_ClosingDate) <= 0)
					{
						//Closing Date By Payment Term
						return Msg.getMsg(po.getCtx(), "JP_PaymentTerm_ClosingDate");
					}
				}
			}
		}

		return null;
	}

}
