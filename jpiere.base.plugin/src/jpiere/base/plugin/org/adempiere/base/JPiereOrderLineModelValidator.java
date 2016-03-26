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
import org.compiere.model.MOrderLine;
import org.compiere.model.MTax;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.Env;

public class JPiereOrderLineModelValidator implements ModelValidator {

	private static CLogger log = CLogger.getCLogger(JPiereOrderLineModelValidator.class);
	private int AD_Client_ID = -1;
	private int AD_Org_ID = -1;
	private int AD_Role_ID = -1;
	private int AD_User_ID = -1;

	@Override
	public void initialize(ModelValidationEngine engine, MClient client) {
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();
		engine.addModelChange(MOrderLine.Table_Name, this);

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

		//JPIERE-0165
		if(type == ModelValidator.TYPE_BEFORE_NEW ||
				(type == ModelValidator.TYPE_BEFORE_CHANGE && (po.is_ValueChanged("LineNetAmt")|| po.is_ValueChanged("C_Tax_ID"))))
		{
			BigDecimal taxAmt = Env.ZERO;
			MOrderLine ol = (MOrderLine)po;
			MTax m_tax = MTax.get(Env.getCtx(), ol.getC_Tax_ID());

			IJPiereTaxProvider taxCalculater = JPiereUtil.getJPiereTaxProvider(m_tax);
			if(taxCalculater != null)
			{
				taxAmt = taxCalculater.calculateTax(m_tax, ol.getLineNetAmt(), ol.isTaxIncluded()
						, MCurrency.getStdPrecision(po.getCtx(), ol.getParent().getC_Currency_ID())
						, JPiereTaxProvider.getRoundingMode(ol.getParent().getC_BPartner_ID(), ol.getParent().isSOTrx(), m_tax.getC_TaxProvider()));
			}else{
				taxAmt = m_tax.calculateTax(ol.getLineNetAmt(), ol.isTaxIncluded(), MCurrency.getStdPrecision(ol.getCtx(), ol.getParent().getC_Currency_ID()));
			}

			if(ol.isTaxIncluded())
			{
				ol.set_ValueNoCheck("JP_TaxBaseAmt",  ol.getLineNetAmt().subtract(taxAmt));
			}else{
				ol.set_ValueNoCheck("JP_TaxBaseAmt",  ol.getLineNetAmt());
			}

			ol.set_ValueOfColumn("JP_TaxAmt", taxAmt);
		}

		return null;
	}

	@Override
	public String docValidate(PO po, int timing) {

		return null;
	}

}
