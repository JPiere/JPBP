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

import jpiere.base.plugin.util.JPierePaymentTerms;

import org.compiere.model.MClient;
import org.compiere.model.MInvoice;
import org.compiere.model.MOrder;
import org.compiere.model.MPaymentTerm;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.Env;

public class JPiereOrderModelValidator implements ModelValidator {

	private static CLogger log = CLogger.getCLogger(JPiereOrderModelValidator.class);
	private int AD_Client_ID = -1;
	private int AD_Org_ID = -1;
	private int AD_Role_ID = -1;
	private int AD_User_ID = -1;

	@Override
	public void initialize(ModelValidationEngine engine, MClient client) {
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();
		engine.addModelChange(MOrder.Table_Name, this);

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

		//For Simple Input Window(JPIERE-0146/JPIERE-0147)
		if(type == ModelValidator.TYPE_BEFORE_NEW 
				|| (type == ModelValidator.TYPE_BEFORE_CHANGE && po.is_ValueChanged("C_DocTypeTarget_ID"))
				|| (type == ModelValidator.TYPE_BEFORE_CHANGE && po.is_ValueChanged("M_PriceList_ID")))
		{
			MOrder order = (MOrder)po;
			order.setIsSOTrx(order.getC_DocTypeTarget().isSOTrx());
			order.setIsTaxIncluded(order.getM_PriceList().isTaxIncluded());
			order.setC_Currency_ID(order.getM_PriceList().getC_Currency_ID());
		}

		return null;
	}

	@Override
	public String docValidate(PO po, int timing) 
	{
		return null;
	}

}
