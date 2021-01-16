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
package jpiere.base.plugin.util;

import java.util.List;

import org.adempiere.base.Service;
import org.compiere.model.MTax;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.base.IJPiereTaxProvider;
import jpiere.base.plugin.org.adempiere.base.IJPiereTaxProviderFactory;

public class JPiereUtil {

	public JPiereUtil() {
		;
	}

	public static final String JPIERE_DEFAULT_TAX_PROVIDER = "jpiere.base.plugin.org.adempiere.model.JPiereTaxProvider";

	public static IJPiereTaxProvider getJPiereTaxProvider(MTax m_tax)
	{
		String className = m_tax.getC_TaxProvider().getC_TaxProviderCfg().getTaxProviderClass();
		if(Util.isEmpty(className))
			className = JPIERE_DEFAULT_TAX_PROVIDER;

		IJPiereTaxProvider calculator = null;
		List<IJPiereTaxProviderFactory> factoryList = Service.locator().list(IJPiereTaxProviderFactory.class).getServices();
		if (factoryList != null)
		{
			for (IJPiereTaxProviderFactory factory : factoryList)
			{
				calculator = factory.newJPiereTaxProviderInstance(className);
				if (calculator != null)
				{
					return calculator;
				}

			}//For
		}

		return null;
	}
}
