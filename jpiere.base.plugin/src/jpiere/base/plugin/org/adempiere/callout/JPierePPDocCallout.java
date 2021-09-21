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
package jpiere.base.plugin.org.adempiere.callout;

import java.math.BigDecimal;
import java.util.Properties;

import org.compiere.model.CalloutEngine;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.MLocator;
import org.compiere.model.MProduct;
import org.compiere.model.MSysConfig;
import org.compiere.model.MUOMConversion;


/**
 * JPIERE-0501:JPiere PP Doc Callout
 *
 * @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 *
 */
public class JPierePPDocCallout extends CalloutEngine {


	public String convertUom(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{
		int C_UOM_To_ID = Integer.valueOf(mTab.get_ValueAsString("C_UOM_ID")).intValue();
		int M_Product_ID = Integer.valueOf(mTab.get_ValueAsString("M_Product_ID")).intValue();
		BigDecimal QtyEntered = (BigDecimal)mTab.getValue("QtyEntered");

		BigDecimal productionQty = MUOMConversion.convertProductFrom (ctx, M_Product_ID,C_UOM_To_ID, QtyEntered);

		mTab.setValue("ProductionQty", productionQty);

		return null;
	}

	public String product(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{
		int M_Product_ID = Integer.valueOf(mTab.get_ValueAsString("M_Product_ID")).intValue();
		if(M_Product_ID != 0)
		{
			MProduct m_Product = MProduct.get(M_Product_ID);
			mTab.setValue("Name", m_Product.getValue() + MSysConfig.getValue(MSysConfig.IDENTIFIER_SEPARATOR) + m_Product.getName());

			if(mTab.getField("M_Locator_ID") != null && m_Product.getM_Locator_ID() != 0)
			{
				int AD_Org_ID = Integer.valueOf(mTab.get_ValueAsString("AD_Org_ID")).intValue();
				MLocator m_Locator = MLocator.get(m_Product.getM_Locator_ID());
				if(m_Locator.getAD_Org_ID() == AD_Org_ID)
				{
					mTab.setValue("M_Locator_ID", m_Product.getM_Locator_ID());
				}
			}
		}

		return null;
	}

}