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
package jpiere.base.plugin.org.adempiere.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

import jpiere.base.plugin.org.adempiere.base.IJPiereTaxProvider;
import jpiere.base.plugin.org.adempiere.base.IJPiereTaxProviderFactory;

import org.adempiere.base.Service;
import org.compiere.model.MCurrency;
import org.compiere.model.MTax;
import org.compiere.util.Env;

/**
 * JPIERE-0161:Inventory Valuation Calculate
 *
 * @author Hideaki Hagiwara
 *
 */
public class MInvValCalLog extends X_JP_InvValCalLog {

	public MInvValCalLog(Properties ctx, int JP_InvValCalLog_ID, String trxName)
	{
		super(ctx, JP_InvValCalLog_ID, trxName);
	}

	public MInvValCalLog(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}

	public MInvValCalLog (MInvValCalLine invValCalLine)
	{
		this (invValCalLine.getCtx(), 0, invValCalLine.get_TrxName());
		if (invValCalLine.get_ID() == 0)
			throw new IllegalArgumentException("Line not saved");
		setJP_InvValCalLine_ID(invValCalLine.getJP_InvValCalLine_ID());	//	parent
		setAD_Org_ID(invValCalLine.getAD_Org_ID());
	}

	public BigDecimal calculateTax(String JP_ApplyAmtList)
	{
		MTax m_tax = MTax.get(getCtx(), getC_Tax_ID());
		String className = m_tax.getC_TaxProvider().getC_TaxProviderCfg().getTaxProviderClass();
		IJPiereTaxProvider calculator = null;
		BigDecimal taxAmt = Env.ZERO;
		List<IJPiereTaxProviderFactory> factoryList = Service.locator().list(IJPiereTaxProviderFactory.class).getServices();
		if (factoryList != null)
		{
			for (IJPiereTaxProviderFactory factory : factoryList)
			{
				calculator = factory.newJPiereTaxProviderInstance(className);
				if (calculator != null)
				{
					int C_BPatner_ID = 0;
					if(getC_OrderLine() != null && JP_ApplyAmtList.equals(MInvValProfile.JP_APPLYAMTLIST_PurchaseOrder))
						C_BPatner_ID = getC_OrderLine().getC_Order().getC_BPartner_ID();
					else if(getC_InvoiceLine() != null && JP_ApplyAmtList.equals(MInvValProfile.JP_APPLYAMTLIST_InvoiceVendor))
						C_BPatner_ID = getC_InvoiceLine().getC_Invoice().getC_BPartner_ID();

					taxAmt = calculator.calculateTax(m_tax, getJP_ExchangedAmt()
							, isTaxIncluded(), MCurrency.getStdPrecision(getCtx(), getC_Currency_ID())
							, JPiereTaxProvider.getRoundingMode(C_BPatner_ID, false, m_tax.getC_TaxProvider()));
					break;
				}

			}//For
		}

		if(calculator==null)
		{
			taxAmt = m_tax.calculateTax(getJP_ExchangedAmt(), true, MCurrency.getStdPrecision(getCtx(), getC_Currency_ID()));
		}

		return taxAmt;
	}
}
