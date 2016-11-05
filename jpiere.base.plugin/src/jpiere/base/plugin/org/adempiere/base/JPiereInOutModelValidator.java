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


import java.sql.Timestamp;

import jpiere.base.plugin.org.adempiere.model.MDeliveryDays;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MClient;
import org.compiere.model.MDocType;
import org.compiere.model.MInOut;
import org.compiere.model.MInOutLine;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MOrder;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.process.DocAction;
import org.compiere.util.CLogger;

public class JPiereInOutModelValidator implements ModelValidator {

	private static CLogger log = CLogger.getCLogger(JPiereInOutModelValidator.class);
	private int AD_Client_ID = -1;
	private int AD_Org_ID = -1;
	private int AD_Role_ID = -1;
	private int AD_User_ID = -1;

	@Override
	public void initialize(ModelValidationEngine engine, MClient client) {
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();
		engine.addDocValidate(MInOut.Table_Name, this);

	}

	@Override
	public int getAD_Client_ID() {
		return AD_Client_ID;
	}

	@Override
	public String login(int AD_Org_ID, int AD_Role_ID, int AD_User_ID) {
		this.AD_Org_ID = AD_Org_ID;
		this.AD_Role_ID = AD_Role_ID;
		this.AD_User_ID = AD_User_ID;

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
		if(timing == ModelValidator.TIMING_AFTER_COMPLETE)
		{
			MInOut io = (MInOut)po;
			String trxName = po.get_TrxName();
			boolean isReversal = io.isReversal();
			MDocType ioDocType = MDocType.get(po.getCtx(), io.getC_DocType_ID());
			if(ioDocType.get_ValueAsBoolean("IsCreateInvoiceJP"))
			{
				if(io.getC_Order_ID()==0)
					return null;
				
				
				MOrder order = new MOrder(po.getCtx(), io.getC_Order_ID(), trxName);
				MDocType orderDocType = MDocType.get(po.getCtx(), order.getC_DocTypeTarget_ID());
				if(orderDocType.equals(MOrder.DocSubTypeSO_OnCredit) 
						|| orderDocType.equals(MOrder.DocSubTypeSO_POS)
						|| orderDocType.equals(MOrder.DocSubTypeSO_Prepay))
				{
					return null;
				}
				
				if(orderDocType.getC_DocTypeInvoice_ID() == 0)
					return null;
				
				
				if(!isReversal && ioDocType.get_ValueAsBoolean("IsInspectionInvoiceJP"))
				{
					Timestamp invoiceDate = MDeliveryDays.getInvoiceDate(io, ioDocType.get_ValueAsBoolean("IsHolidayNotInspectionJP"));
					io.setDateAcct(invoiceDate);
					io.saveEx(trxName);
				}
					
				MInvoice invoice = new MInvoice (order, orderDocType.getC_DocTypeInvoice_ID(), io.getDateAcct());
				if (!invoice.save(trxName))
				{
//					m_processMsg = "Could not create Invoice";
					return null;
				}
				
				MInOutLine[] sLines = io.getLines(false);
				for (int i = 0; i < sLines.length; i++)
				{
					MInOutLine sLine = sLines[i];
					//
					MInvoiceLine iLine = new MInvoiceLine(invoice);
					iLine.setShipLine(sLine);
					//	Qty = Delivered	
					if (sLine.sameOrderLineUOM())
						iLine.setQtyEntered(sLine.getQtyEntered());
					else
						iLine.setQtyEntered(sLine.getMovementQty());
					iLine.setQtyInvoiced(sLine.getMovementQty());
					if (!iLine.save(io.get_TrxName()))
					{
//						m_processMsg = "Could not create Invoice Line from Shipment Line";
						return null;
					}
					//
					sLine.setIsInvoiced(true);
					if (!sLine.save(trxName))
					{
						log.warning("Could not update Shipment line: " + sLine);
					}
				}//for
				
				if (!invoice.processIt(DocAction.ACTION_Complete))
					throw new AdempiereException("Failed when processing document - " + invoice.getProcessMsg());
				
				invoice.saveEx(trxName);
				if (!invoice.getDocStatus().equals(DocAction.STATUS_Completed))
				{
//					m_processMsg = "@C_Invoice_ID@: " + invoice.getProcessMsg();
					return null;
				}
				;
			}
		}

		return null;
	}

}
