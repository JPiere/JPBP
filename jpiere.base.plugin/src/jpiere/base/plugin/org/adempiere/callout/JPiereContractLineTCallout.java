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

import java.util.Properties;

import org.adempiere.base.IColumnCallout;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.MDocType;
import org.compiere.model.MPriceList;
import org.compiere.util.Env;

/**
 *
 *  JPiere Contract Content Line Template CallOut
 *
 *  JPIERE-0427:JPBP
 *
 * @author Hideaki Hagiwara
 *
 */
public class JPiereContractLineTCallout implements IColumnCallout {

	@Override
	public String start(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{

		if(mField.getColumnName().equals("M_PriceList_ID"))
		{
			if( value != null )
			{
				MPriceList pl = MPriceList.get(Env.getCtx(), Integer.parseInt(value.toString()), null);
				mTab.setValue("C_Currency_ID", pl.getC_Currency_ID());
				mTab.setValue("IsSOTrx", pl.isSOPriceList());
				mTab.setValue("IsTaxIncluded", pl.isTaxIncluded());
			}else {

				mTab.setValue("C_Currency_ID", null);
				mTab.setValue("IsTaxIncluded", false);

			}

		}else if(mField.getColumnName().equals("DocBaseType")){

			if( value == null)
			{
				mTab.setValue ("OrderType",  "--");
				mTab.setValue("JP_CreateDerivativeDocPolicy", null);
				mTab.setValue("IsSOTrx", false);
				mTab.setValue("M_PriceList_ID", null);
				mTab.setValue("C_Currency_ID", null);
				mTab.setValue("IsTaxIncluded", false);

			}else{

				String docBaseType = (String)value;
				if(docBaseType.equals(MDocType.DOCBASETYPE_SalesOrder) || docBaseType.equals(MDocType.DOCBASETYPE_ARInvoice) )
				{
					mTab.setValue("IsSOTrx", true);
				}else {
					mTab.setValue("IsSOTrx", false);
				}

				if(docBaseType.equals(MDocType.DOCBASETYPE_SalesOrder) || docBaseType.equals(MDocType.DOCBASETYPE_PurchaseOrder) )
				{
					mTab.setValue("OrderType", "SO");
				}else {
					mTab.setValue ("OrderType",  "--");
				}

			}
		}

		return "";
	}

}
