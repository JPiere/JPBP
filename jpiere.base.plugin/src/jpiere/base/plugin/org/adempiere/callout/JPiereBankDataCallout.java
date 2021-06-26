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

import org.adempiere.base.IColumnCallout;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.MInvoice;
import org.compiere.model.MOrder;
import org.compiere.model.MPayment;
import org.compiere.util.Env;

import jpiere.base.plugin.org.adempiere.model.MBill;

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
				mTab.setValue("C_Order_ID", null);
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
				mTab.setValue("C_Order_ID", null);
				MBill bill = new MBill(ctx, JP_Bill_ID.intValue(), null);
				mTab.setValue("C_BPartner_ID", bill.getC_BPartner_ID());
				mTab.setValue("TrxAmt", bill.getCurrentOpenAmt());
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
				mTab.setValue("C_Order_ID", null);
				MPayment payment = new MPayment(ctx, C_Payment_ID.intValue(), null);
				mTab.setValue("C_BPartner_ID", payment.getC_BPartner_ID());
				mTab.setValue("TrxAmt", payment.getPayAmt());
				updateChargeAmt(mTab);
			}
		}

		if(mField.getColumnName().equals("C_Order_ID"))
		{
			Integer C_Order_ID = (Integer)value;
			if(C_Order_ID != null && C_Order_ID.intValue() != 0)
			{
				mTab.setValue("JP_Bill_ID", null);
				mTab.setValue("C_Invoice_ID", null);
				mTab.setValue("C_Payment_ID", null);
				MOrder order = new MOrder(ctx, C_Order_ID.intValue(), null);
				mTab.setValue("C_BPartner_ID", order.getC_BPartner_ID());
				mTab.setValue("TrxAmt", order.getGrandTotal());
				updateChargeAmt(mTab);
			}
		}

		return null;
	}


	private void updateChargeAmt(GridTab mTab)
	{
		BigDecimal stmtAmt = (BigDecimal)mTab.getValue("StmtAmt");
		if(stmtAmt == null)
			stmtAmt = Env.ZERO;

		BigDecimal trxAmt = (BigDecimal)mTab.getValue("TrxAmt");
		if(trxAmt == null)
			trxAmt = Env.ZERO;

		BigDecimal interestAmt = (BigDecimal)mTab.getValue("InterestAmt");
		if(interestAmt == null)
			interestAmt =  Env.ZERO;

		mTab.setValue("ChargeAmt", stmtAmt.subtract(trxAmt).subtract(interestAmt));
	}
}
