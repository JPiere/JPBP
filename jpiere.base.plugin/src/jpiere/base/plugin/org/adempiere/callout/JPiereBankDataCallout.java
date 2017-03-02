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
import org.compiere.model.MInvoice;
import org.compiere.model.MPayment;
import org.compiere.util.Env;

/**
 *
 * JPIERE-0302: Callout of Import Bank Data
 *
 * @author Hideaki Hagiwara
 *
 */
public class JPiereBankDataCallout implements IColumnCallout {

	@Override
	public String start(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{
		if(mField.getColumnName().equals("TrxAmt"))
		{
			updateChargeAmt(mTab);
		}
		
		if(mField.getColumnName().equals("C_Invoice_ID"))
		{
			Integer C_Invoice_ID = (Integer)value;
			if(C_Invoice_ID != null && C_Invoice_ID.intValue() != 0)
			{
				mTab.setValue("JP_Bill_ID", null);
				mTab.setValue("C_Payment_ID", null);
				MInvoice invoice = new MInvoice(ctx, C_Invoice_ID.intValue(), null);
				mTab.setValue("C_BPartner_ID", invoice.getC_BPartner_ID());
				mTab.setValue("TrxAmt", invoice.getOpenAmt());
				updateChargeAmt(mTab);
			}
		}
		
		if(mField.getColumnName().equals("JP_Bill_ID"))
		{
			Integer JP_Bill_ID = (Integer)value;
			if(JP_Bill_ID != null && JP_Bill_ID.intValue() != 0)
			{
				mTab.setValue("C_Invoice_ID", null);
				mTab.setValue("C_Payment_ID", null);
				MBill bill = new MBill(ctx, JP_Bill_ID.intValue(), null);
				mTab.setValue("C_BPartner_ID", bill.getC_BPartner_ID());
				mTab.setValue("TrxAmt", bill.getOpenAmt());//TODO:現在の未回収金額を取得できた方が良いと思われる…。MBillに追加するか…。
				updateChargeAmt(mTab);
			}
		}
		
		if(mField.getColumnName().equals("C_Payment_ID"))
		{
			Integer C_Payment_ID = (Integer)value;
			if(C_Payment_ID != null && C_Payment_ID.intValue() != 0)
			{
				mTab.setValue("JP_Bill_ID", null);
				mTab.setValue("C_Invoice_ID", null);
				MPayment payment = new MPayment(ctx, C_Payment_ID.intValue(), null);
				mTab.setValue("C_BPartner_ID", payment.getC_BPartner_ID());
				mTab.setValue("TrxAmt", payment.getPayAmt());
				updateChargeAmt(mTab);
			}
		}
		
		return null;
	}

	
	private void updateChargeAmt(GridTab mTab)
	{
		BigDecimal stmtAmt = (BigDecimal)mTab.getValue("StmtAmt");
		BigDecimal trxAmt = (BigDecimal)mTab.getValue("TrxAmt");
		BigDecimal interestAmt = (BigDecimal)mTab.getValue("InterestAmt");
		mTab.setValue("ChargeAmt", stmtAmt.subtract(trxAmt).subtract(interestAmt));
	}
}
