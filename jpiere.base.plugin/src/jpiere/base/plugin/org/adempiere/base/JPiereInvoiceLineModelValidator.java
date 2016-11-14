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

import jpiere.base.plugin.org.adempiere.model.JPiereTaxProvider;
import jpiere.base.plugin.util.JPiereUtil;

import org.compiere.model.MClient;
import org.compiere.model.MCurrency;
import org.compiere.model.MDocType;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MTax;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Msg;

public class JPiereInvoiceLineModelValidator implements ModelValidator {

	private static CLogger log = CLogger.getCLogger(JPiereInvoiceLineModelValidator.class);
	private int AD_Client_ID = -1;
	private int AD_Org_ID = -1;
	private int AD_Role_ID = -1;
	private int AD_User_ID = -1;

	@Override
	public void initialize(ModelValidationEngine engine, MClient client) {
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();
		engine.addModelChange(MInvoiceLine.Table_Name, this);

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

		//JPIERE-0165:
		if(type == ModelValidator.TYPE_BEFORE_NEW ||
				(type == ModelValidator.TYPE_BEFORE_CHANGE && (po.is_ValueChanged("LineNetAmt")|| po.is_ValueChanged("C_Tax_ID"))))
		{
			BigDecimal taxAmt = Env.ZERO;
			MInvoiceLine il = (MInvoiceLine)po;
			MTax m_tax = MTax.get(Env.getCtx(), il.getC_Tax_ID());

			IJPiereTaxProvider taxCalculater = JPiereUtil.getJPiereTaxProvider(m_tax);
			if(taxCalculater != null)
			{
				taxAmt = taxCalculater.calculateTax(m_tax, il.getLineNetAmt(), il.isTaxIncluded()
						, MCurrency.getStdPrecision(po.getCtx(), il.getParent().getC_Currency_ID())
						, JPiereTaxProvider.getRoundingMode(il.getParent().getC_BPartner_ID(), il.getParent().isSOTrx(), m_tax.getC_TaxProvider()));
			}else{
				taxAmt = m_tax.calculateTax(il.getLineNetAmt(), il.isTaxIncluded(), MCurrency.getStdPrecision(il.getCtx(), il.getParent().getC_Currency_ID()));
			}

			if(il.isTaxIncluded())
			{
				il.set_ValueNoCheck("JP_TaxBaseAmt",  il.getLineNetAmt().subtract(taxAmt));
			}else{
				il.set_ValueNoCheck("JP_TaxBaseAmt",  il.getLineNetAmt());
			}

			il.set_ValueOfColumn("JP_TaxAmt", taxAmt);
		}

		//JPIERE-0223:Match Inv control
		if(type == ModelValidator.TYPE_BEFORE_NEW ||
				(type == ModelValidator.TYPE_BEFORE_CHANGE && po.is_ValueChanged("M_InOutLine_ID")))
		{
			MInvoiceLine il = (MInvoiceLine)po;
			if(il.getM_InOutLine_ID() > 0 && !il.getParent().isSOTrx())
			{
				if(il.getParent().getC_DocTypeTarget().getDocBaseType().equals(MDocType.DOCBASETYPE_APInvoice))//AP Invoice
				{
					if(!il.getM_InOutLine().getM_InOut().getC_DocType().getDocBaseType().equals(MDocType.DOCBASETYPE_MaterialReceipt))
					{
						return Msg.getMsg(il.getCtx(), "JP_API_MATCH_MMR_ONLY");//API of Doc Base Type can match MMR of Doc Base type only.
					}
				}else if(il.getParent().getC_DocTypeTarget().getDocBaseType().equals(MDocType.DOCBASETYPE_APCreditMemo)){//AP credit Memo

					if(!il.getM_InOutLine().getM_InOut().getC_DocType().getDocBaseType().equals(MDocType.DOCBASETYPE_MaterialDelivery))
					{
						return Msg.getMsg(il.getCtx(), "JP_APC_MATCH_MMS_ONLY");//API of Doc Base Type can match MMR of Doc Base type only.
					}
				}
			}

		}

		return null;
	}

	@Override
	public String docValidate(PO po, int timing) {

		return null;
	}

}
