/******************************************************************************
 * Product: JPiere(Japan + iDempiere)                                         *
 * Copyright (C) Hideaki Hagiwara (h.hagiwara@oss-erp.co.jp)                  *
 *                                                                            *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY.                          *
 * See the GNU General Public License for more details.                       *
 *                                                                            *
 * JPiere supported by OSS ERP Solutions Co., Ltd.                            *
 * (http://www.oss-erp.co.jp)                                                 *
 *****************************************************************************/
package jpiere.base.plugin.factory;

import java.util.ArrayList;
import java.util.List;

import jpiere.base.plugin.org.adempiere.callout.JPiereBankAcountCallout;
import jpiere.base.plugin.org.adempiere.callout.JPiereBillAmountCallout;
import jpiere.base.plugin.org.adempiere.callout.JPiereBillBPartnerCallout;
import jpiere.base.plugin.org.adempiere.callout.JPiereCityCallout;
import jpiere.base.plugin.org.adempiere.callout.JPiereRegionCallout;
import jpiere.base.plugin.org.adempiere.model.MBill;

import org.adempiere.base.IColumnCallout;
import org.adempiere.base.IColumnCalloutFactory;
import org.compiere.model.MLocation;
import org.compiere.model.MPayment;

/**
 *  JPiere Base Plugin Callout Factory
 *
 *  JPIERE-0106:JPBP:Bill
 *
 *  @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 *
 */
public class JPiereBasePluginColumnCalloutFactory implements IColumnCalloutFactory {

	@Override
	public IColumnCallout[] getColumnCallouts(String tableName, String columnName) {

		List<IColumnCallout> list = new ArrayList<IColumnCallout>();

		if(tableName.equals(MPayment.Table_Name) && columnName.equals(MPayment.COLUMNNAME_C_BankAccount_ID))
		{
			list.add(new JPiereBankAcountCallout());
		}else if(tableName.equals(MLocation.Table_Name) && columnName.equals(MLocation.COLUMNNAME_C_Region_ID)){
			list.add(new JPiereRegionCallout());
		}else if(tableName.equals(MLocation.Table_Name) && columnName.equals(MLocation.COLUMNNAME_C_City_ID)){
			list.add(new JPiereCityCallout());
		}else if(tableName.equals(MBill.Table_Name) && columnName.equals(MBill.COLUMNNAME_C_BPartner_ID)){
			list.add(new JPiereBillBPartnerCallout());
		}else if(tableName.equals(MBill.Table_Name) && (columnName.equals(MBill.COLUMNNAME_JP_LastBill_ID)
														|| columnName.equals(MBill.COLUMNNAME_JPLastBillAmt)
														|| columnName.equals(MBill.COLUMNNAME_C_Payment_ID)
														|| columnName.equals(MBill.COLUMNNAME_JPLastPayAmt))){
			list.add(new JPiereBillAmountCallout());
		}

		return list != null ? list.toArray(new IColumnCallout[0]) : new IColumnCallout[0];
	}

}
