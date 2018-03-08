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

import java.math.BigDecimal;

import org.compiere.model.MClient;
import org.compiere.model.MRMALine;
import org.compiere.model.MSysConfig;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.util.Msg;

public class JPiereRMALineModelValidator implements ModelValidator {


	private int AD_Client_ID = -1;


	@Override
	public void initialize(ModelValidationEngine engine, MClient client) {
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();
		engine.addModelChange(MRMALine.Table_Name, this);

	}

	@Override
	public int getAD_Client_ID() {
		return AD_Client_ID;
	}

	@Override
	public String login(int AD_Org_ID, int AD_Role_ID, int AD_User_ID) {

		return null;
	}

	@Override
	public String modelChange(PO po, int type) throws Exception
	{

		//JPIERE-0375:Check Over Qty Invoice
		if(type == ModelValidator.TYPE_BEFORE_CHANGE && po.is_ValueChanged("QtyInvoiced") )
		{
			MRMALine ol = (MRMALine)po;
			if ( (ol.getParent().isSOTrx() && MSysConfig.getBooleanValue("JP_CHECK_ORVER_QTYINVOICED_C-RMA", false, ol.getAD_Client_ID(), ol.getAD_Org_ID()) )
					  ||
					 (!ol.getParent().isSOTrx()	&& MSysConfig.getBooleanValue("JP_CHECK_ORVER_QTYINVOICED_V-RMA", false, ol.getAD_Client_ID(), ol.getAD_Org_ID()) )
			    )
			{
				BigDecimal qtyOrdered = ol.getQty();
				BigDecimal qtyInvoiced  = ol.getQtyInvoiced();

				if(qtyOrdered.signum() >= 0)
				{
					if(qtyInvoiced.compareTo(qtyOrdered) > 0)
					{
						return Msg.getMsg(po.getCtx(), "JP_Over_QtyInvoiced") + " : "+ ol.getParent().getDocumentNo() +  " - " + ol.getLine();
					}

				}else {

					if(qtyInvoiced.compareTo(qtyOrdered) < 0)
					{
						return Msg.getMsg(po.getCtx(), "JP_Over_QtyInvoiced") + " : "+ ol.getParent().getDocumentNo() +  " - " + ol.getLine();
					}
				}
			}

		}


		return null;
	}

	@Override
	public String docValidate(PO po, int timing) {

		return null;
	}


}
