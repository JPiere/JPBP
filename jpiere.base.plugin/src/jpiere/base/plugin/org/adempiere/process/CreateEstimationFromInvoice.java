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

package jpiere.base.plugin.org.adempiere.process;

import java.util.logging.Level;

import org.compiere.model.MBPartner;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MOrder;
import org.compiere.model.PO;
import org.compiere.model.X_C_Order;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Msg;

import jpiere.base.plugin.org.adempiere.model.MEstimation;
import jpiere.base.plugin.org.adempiere.model.MEstimationLine;

/**
 * JPIERE-0266 : Create Estimation From Invoice
 *
 * @author Hiroshi Iwama
 *
 */

public class CreateEstimationFromInvoice extends SvrProcess {

	/*
	 * Parameters
	 */

	private int	p_C_DocType_ID = 0;

	//Document Action (mandetory)
	private String p_DocAction = null;


	//Warehouse belonging to the current organization (mandetory)
	private int p_M_Warehouse_ID = 0;

	private int p_C_Invoice_ID = 0;
	private MInvoice invoice = null;

	@Override
	protected void prepare() {

		//get the current invoice ID
		p_C_Invoice_ID = getRecord_ID();

		//get parameters
		ProcessInfoParameter[] para = getParameter();
		for(int i = 0; i < para.length; i++){
			String name = para[i].getParameterName();
			if(para[i].getParameter() == null){
				;
			}else if (name.equals("C_DocType_ID")){
				p_C_DocType_ID = para[i].getParameterAsInt();

			}else if (name.equals("DocAction")){
				p_DocAction = para[i].getParameterAsString();
			}
			else if (name.equals("M_Warehouse_ID")) {
				p_M_Warehouse_ID = para[i].getParameterAsInt();
			}
			else {
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}
		}

		//get the current invoice record
		invoice = new MInvoice(getCtx(), p_C_Invoice_ID, get_TrxName());

	}

	@Override
	protected String doIt() throws Exception {

		//get the relevant invoice lines
		MInvoiceLine[] invoiceLines = invoice.getLines();

		//create the estimation
		MEstimation estimation = new MEstimation(getCtx(), 0, get_TrxName());

		//get the relevant bpartner
		MBPartner bpartner = new MBPartner(getCtx(), invoice.getC_BPartner_ID(), get_TrxName());

		//create the header
		PO.copyValues(invoice, estimation);
		estimation.setAD_Org_ID(invoice.getAD_Org_ID());
		estimation.setDocumentNo(invoice.getDocumentNo());
		estimation.setJP_EstimationDate(invoice.getDateInvoiced());
		estimation.setC_DocTypeTarget_ID(p_C_DocType_ID);
		estimation.setC_DocType_ID(p_C_DocType_ID);
		estimation.setDocStatus(DocAction.STATUS_Drafted);
		estimation.setDocAction(DocAction.ACTION_Complete);
		estimation.setM_Warehouse_ID(p_M_Warehouse_ID);

		//if the relevant SO exists,
		if(invoice.getC_Order_ID() != 0){
			MOrder order = new MOrder(getCtx(), invoice.getC_Order_ID(), get_TrxName());
			estimation.setInvoiceRule(order.getInvoiceRule());
			estimation.setDeliveryRule(order.getDeliveryRule());
			estimation.setFreightCostRule(order.getFreightCostRule());
			estimation.setDeliveryViaRule(order.getDeliveryViaRule());
			estimation.setPriorityRule(order.getPriorityRule());
			estimation.setDateOrdered(order.getDateOrdered());
			estimation.setDatePromised(order.getDatePromised());
		}
		//if the relevant SO doesn't exist,
		else{
			/*
			 * Get the rules from bpartner if there are ones,
			 * or from the default values in SO, otherwise.
			 */
			if(bpartner.getInvoiceRule() != null){
				estimation.setInvoiceRule(bpartner.getInvoiceRule());
			}else{
				estimation.setInvoiceRule(X_C_Order.INVOICERULE_Immediate);
			}
			if(bpartner.getDeliveryRule() != null){
				estimation.setDeliveryRule(bpartner.getDeliveryRule());
			}else{
				estimation.setDeliveryRule(X_C_Order.DELIVERYRULE_Availability);
			}
			if(bpartner.getFreightCostRule() != null){
				estimation.setFreightCostRule(bpartner.getFreightCostRule());
			}else{
				estimation.setFreightCostRule(X_C_Order.FREIGHTCOSTRULE_FreightIncluded);
			}
			if(bpartner.getDeliveryViaRule() != null){
				estimation.setDeliveryViaRule(bpartner.getDeliveryViaRule());
			}else{
				estimation.setDeliveryViaRule(X_C_Order.DELIVERYVIARULE_Pickup);
			}
			estimation.setDateOrdered(invoice.getDateInvoiced());
			estimation.setDatePromised(invoice.getDateInvoiced());
			estimation.setPriorityRule(X_C_Order.PRIORITYRULE_Medium);
		}

		estimation.setC_Invoice_ID(p_C_Invoice_ID);
		estimation.saveEx(get_TrxName());

		for(int i = 0; i < invoiceLines.length; i++){

			MEstimationLine estimationLines = new MEstimationLine(estimation);
			PO.copyValues(invoiceLines[i], estimationLines);
			estimationLines.setAD_Org_ID(invoice.getAD_Org_ID());
			estimationLines.setC_BPartner_ID(invoice.getC_BPartner_ID());
			estimationLines.setC_BPartner_Location_ID(invoice.getC_BPartner_Location_ID());
			estimationLines.setDateInvoiced(invoice.getDateInvoiced());
			estimationLines.setM_Warehouse_ID(p_M_Warehouse_ID);
			estimationLines.setQtyOrdered(invoiceLines[i].getQtyInvoiced());
			estimationLines.saveEx(get_TrxName());
		}

		if(invoiceLines.length > 0 && p_DocAction != null) 
		{
			
			//Requery
			estimation = new MEstimation(getCtx(), estimation.getJP_Estimation_ID(), get_TrxName());
			
			if(!estimation.processIt(p_DocAction)){
				throw new Exception(Msg.getMsg(getCtx(), "ProcessRunError"));
			}
			estimation.saveEx(get_TrxName());
		}

		addBufferLog(0, null, null, estimation.getDocumentInfo(), estimation.get_Table_ID(), estimation.getJP_Estimation_ID());

		return invoice.getDocumentInfo();

	}

}



