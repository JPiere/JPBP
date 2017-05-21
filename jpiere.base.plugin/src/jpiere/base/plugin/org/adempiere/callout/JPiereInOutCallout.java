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
import org.compiere.model.MOrder;
import org.compiere.util.Env;

/**
 *
 *  JPiere Shipment Document CallOut
 *
 *  JPIERE-0144:JPBP
 *
 * @author Hideaki Hagiwara
 *
 */
public class JPiereInOutCallout implements IColumnCallout {

	@Override
	public String start(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{

		//Jugement of Shipment1
		boolean IsSOTrx = "Y".equals(Env.getContext(ctx, WindowNo, "IsSOTrx"));
		if(!IsSOTrx)
			return "";

		//Jugement of Shipment2
		if(mTab.getValue("MovementType") == null || !mTab.getValue("MovementType").toString().equals("C-"))
			return "";

		Integer C_Order_ID = (Integer)value;
		if (C_Order_ID == null || C_Order_ID.intValue() == 0)
			return "";

		//	Get Details
		MOrder order = new MOrder (ctx, C_Order_ID.intValue(), null);
		if (order.get_ID() != 0)
		{
			mTab.setValue("IsDropShip", order.isDropShip());
			if (order.getDropShip_BPartner_ID() == 0)
				mTab.setValue("DropShip_BPartner_ID", null);
			else
				mTab.setValue("DropShip_BPartner_ID", order.getDropShip_BPartner_ID());

			if (order.getDropShip_BPartner_ID() == 0)
				mTab.setValue("DropShip_Location_ID", null);
			else
				mTab.setValue("DropShip_Location_ID", new Integer(order.getDropShip_BPartner_ID()));

			if (order.getDropShip_User_ID() == 0)
				mTab.setValue("DropShip_User_ID", null);
			else
				mTab.setValue("DropShip_User_ID", new Integer(order.getDropShip_User_ID()));
		}

		return "";
	}

}
