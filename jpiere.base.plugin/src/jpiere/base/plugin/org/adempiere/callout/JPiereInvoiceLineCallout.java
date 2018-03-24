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
import org.compiere.model.MOrderLine;

/**
 *
 *  JPiere Invoice Line Document CallOut
 *
 *  JPIERE-0381:JPBP
 *
 * @author Hideaki Hagiwara
 *
 */
public class JPiereInvoiceLineCallout implements IColumnCallout {

	@Override
	public String start(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{

		/**************************************************************************
		 * 	OrderLine Callout
		 */
		if(mField.getColumnName().equals("C_OrderLine_ID"))
		{
			Integer C_OrderLine_ID = (Integer)value;
			if (C_OrderLine_ID == null || C_OrderLine_ID.intValue() == 0)
				return "";

			//	Get Details
			MOrderLine ol = new MOrderLine (ctx, C_OrderLine_ID.intValue(), null);
			if (ol.get_ID() != 0)
			{
				if (ol.getC_Charge_ID() > 0 && ol.getM_Product_ID() <= 0) {
					mTab.setValue("C_Charge_ID", new Integer(ol.getC_Charge_ID()));
					mTab.setValue("M_Product_ID", null);
					mTab.setValue("M_AttributeSetInstance_ID", null);
				}
				else {
					mTab.setValue("M_Product_ID", new Integer(ol.getM_Product_ID()));
					mTab.setValue("M_AttributeSetInstance_ID", new Integer(ol.getM_AttributeSetInstance_ID()));
					mTab.setValue("C_Charge_ID", null);
				}
				//
				mTab.setValue("C_UOM_ID", new Integer(ol.getC_UOM_ID()));
				BigDecimal qtyInvoiced = ol.getQtyOrdered().subtract(ol.getQtyInvoiced());
				mTab.setValue("QtyInvoiced", qtyInvoiced);
				BigDecimal QtyEntered = qtyInvoiced;
				if (ol.getQtyEntered().compareTo(ol.getQtyOrdered()) != 0)
					QtyEntered = QtyEntered.multiply(ol.getQtyEntered())
						.divide(ol.getQtyOrdered(), 12, BigDecimal.ROUND_HALF_UP);
				mTab.setValue("QtyEntered", QtyEntered);
				//
				mTab.setValue("C_Activity_ID", new Integer(ol.getC_Activity_ID()));
				mTab.setValue("C_Campaign_ID", new Integer(ol.getC_Campaign_ID()));
				mTab.setValue("C_Project_ID", new Integer(ol.getC_Project_ID()));
				mTab.setValue("C_ProjectPhase_ID", new Integer(ol.getC_ProjectPhase_ID()));
				mTab.setValue("C_ProjectTask_ID", new Integer(ol.getC_ProjectTask_ID()));
				mTab.setValue("AD_OrgTrx_ID", new Integer(ol.getAD_OrgTrx_ID()));
				mTab.setValue("User1_ID", new Integer(ol.getUser1_ID()));
				mTab.setValue("User2_ID", new Integer(ol.getUser2_ID()));
			}
		}	//	orderLine

		return "";
	}

}
