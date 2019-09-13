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
package jpiere.base.plugin.org.adempiere.base;


import java.util.logging.Level;

import org.compiere.model.MClient;
import org.compiere.model.MMovement;
import org.compiere.model.MOrder;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.process.DocAction;
import org.compiere.util.CLogger;
import org.compiere.util.Msg;


/**
*
* JPiere Order Line model Validator
*
* JPIERE-0227: Common Warehouse
*
* @author h.hagiwara
*
*/
public class JPiereMovementModelValidator implements ModelValidator {

	private static CLogger log = CLogger.getCLogger(JPiereMovementModelValidator.class);
	private int AD_Client_ID = -1;

	@Override
	public void initialize(ModelValidationEngine engine, MClient client)
	{
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();
		engine.addDocValidate(MMovement.Table_Name, this);

		if (log.isLoggable(Level.FINE)) log.fine("Initialize JPiereMovementModelValidator");

	}

	@Override
	public int getAD_Client_ID()
	{
		return AD_Client_ID;
	}

	@Override
	public String login(int AD_Org_ID, int AD_Role_ID, int AD_User_ID)
	{
		return null;
	}

	@Override
	public String modelChange(PO po, int type) throws Exception
	{
		return null;
	}


	@Override
	public String docValidate(PO po, int timing)
	{
		//JPIERE-0227
		if(timing ==  ModelValidator.TIMING_BEFORE_CLOSE)
		{
			if(po instanceof MMovement)
			{
				int JP_Order_ID = po.get_ValueAsInt("JP_Order_ID");
				if(JP_Order_ID > 0)
				{
					MOrder order = new MOrder(po.getCtx(),JP_Order_ID, po.get_TrxName());
					if(!order.getDocStatus().equals(DocAction.STATUS_Closed))
					{
						//You can not close Movement document, because Document status of Sales order is not close.
						return Msg.getMsg(po.getCtx(), "JP_Order_MM_CloseCheck");
					}
				}

			}
		}

		return null;
	}



}
