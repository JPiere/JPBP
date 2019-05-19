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

import java.sql.Timestamp;
import java.util.Properties;

import org.adempiere.base.IColumnCallout;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.MDocType;
import org.compiere.model.MPriceList;
import org.compiere.model.MPriceListVersion;
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
		String msg = null;

		if(mField.getColumnName().equals("JP_ContractLineT_ID") || mField.getColumnName().equals("M_PriceList_ID"))
		{

			msg = calloutSetPriceListInfo(ctx, WindowNo, mTab, mField, value, oldValue);

		}else if(mField.getColumnName().equals("DocBaseType")){

			msg = calloutDocBaseType(ctx, WindowNo, mTab, mField, value, oldValue);

		}

		return msg;
	}

	private String calloutSetPriceListInfo(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{

		Integer M_PriceList_ID = (Integer) mTab.getValue("M_PriceList_ID");
		if (M_PriceList_ID == null || M_PriceList_ID.intValue()== 0)
			return "";

		MPriceList pl = MPriceList.get(ctx, M_PriceList_ID, null);
		if (pl != null && pl.getM_PriceList_ID() == M_PriceList_ID)
		{

			//	Tax Included
			mTab.setValue("IsTaxIncluded", pl.isTaxIncluded());
			//	Currency
			mTab.setValue("C_Currency_ID", pl.getC_Currency_ID());

			//	Price Limit Enforce
			Env.setContext(ctx, WindowNo, "EnforcePriceLimit", pl.isEnforcePriceLimit());

			//PriceList Version
			Timestamp date = null;
			if(mTab.getParentTab()==null)
			{
				date = Env.getContextAsDate(ctx, "@#Date@");
			}else {

				date = (Timestamp)mTab.getParentTab().getValue("DateInvoiced");
				if(date == null)
					date = Env.getContextAsDate(ctx, "@#Date@");
			}

			MPriceListVersion plv = pl.getPriceListVersion(date);
			if (plv != null && plv.getM_PriceList_Version_ID() > 0) {
				Env.setContext(ctx, WindowNo, "M_PriceList_Version_ID", plv.getM_PriceList_Version_ID());
			} else {
				Env.setContext(ctx, WindowNo, "M_PriceList_Version_ID", (String) null);
			}

		}else {

			mTab.setValue("C_Currency_ID", null);
			mTab.setValue("IsTaxIncluded", false);

		}

		return null;
	}

	private String calloutDocBaseType(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{

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

		return null;
	}

}
