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

import jpiere.base.plugin.org.adempiere.model.MBill;

import org.adempiere.base.IColumnCallout;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.MPayment;
import org.compiere.util.Env;

/**
 *  JPiere Bill Amount Callout
 *
 *  JPIERE-0106:JPBP:Bill
 *
 *  @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 *
 */
public class JPiereBillAmountCallout implements IColumnCallout {


	@Override
	public String start(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue) {

		if(mField.getColumnName().equals("JP_LastBill_ID"))
		{
			if(value == null)
			{
				mTab.setValue("JPLastBillAmt", Env.ZERO);
			}else{
				MBill bill = new MBill(ctx,((Integer)value).intValue(), null);
				mTab.setValue("JPLastBillAmt", bill.getJPBillAmt());
			}
		}

		if(mField.getColumnName().equals("C_Payment_ID"))
		{
			if(value == null)
			{
				mTab.setValue("JPLastPayAmt", Env.ZERO);
			}else{
				MPayment pay = new MPayment(ctx,((Integer)value).intValue(), null);
				mTab.setValue("JPLastPayAmt", pay.getPayAmt());
			}
		}

		BigDecimal lastBillAmt =(BigDecimal)mTab.getValue("JPLastBillAmt");
		BigDecimal lastPayAmt =(BigDecimal)mTab.getValue("JPLastPayAmt");
		BigDecimal JPCarriedForwardAmt = lastBillAmt.subtract(lastPayAmt);
		mTab.setValue("JPCarriedForwardAmt",JPCarriedForwardAmt);

		BigDecimal openAmt =(BigDecimal)mTab.getValue("OpenAmt");
		mTab.setValue("JPBillAmt",JPCarriedForwardAmt.add(openAmt));

		return null;
	}


}
