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
import org.compiere.model.MPriceList;
import org.compiere.model.PO;
import org.compiere.model.X_C_Order;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.MBill;
import jpiere.base.plugin.org.adempiere.model.MBillLine;
import jpiere.base.plugin.org.adempiere.model.MEstimation;
import jpiere.base.plugin.org.adempiere.model.MEstimationLine;

/**
 * JPIERE-0267 : Create Estimation From Bill
 * @author BIT
 *
 */
public class CreateEstimationFromBill extends SvrProcess {

	private int		p_JP_Bill_ID = 0;
	private String		p_DocAction = null;
	private int		p_C_DocTypeTarget_ID = 0;
	private int		p_M_Warehouse_ID = 0;
	private MBill		bill		= null;

	@Override
	protected void prepare() {

		p_JP_Bill_ID = getRecord_ID();

		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();

			if (para[i].getParameter() == null){
				;
			}else if (name.equals("DocAction")){
				p_DocAction = para[i].getParameterAsString();
			}else if (name.equals("C_DocType_ID")){
				p_C_DocTypeTarget_ID = para[i].getParameterAsInt();
			}else if (name.equals("M_Warehouse_ID")){
				p_M_Warehouse_ID = para[i].getParameterAsInt();
			}else{
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}//if

		}//for

		bill = new MBill(getCtx(), p_JP_Bill_ID, get_TrxName());

	}

	@Override
	protected String doIt() throws Exception {

		// Create of header
		MEstimation estimation = createEstimation();

		// Create of line
		createOfEstimationLine(estimation);

		if(!estimation.processIt(p_DocAction)){
			throw new Exception(Msg.getMsg(getCtx(), "ProcessRunError"));
		}

		estimation.saveEx(get_TrxName());

		addBufferLog(0, null, null, estimation.getDocumentInfo(), estimation.get_Table_ID(), estimation.getJP_Estimation_ID());

		return estimation.getDocumentInfo();
	}

	/**
	 * Create of Estimation
	 * @return MEstimation
	 */
	private MEstimation createEstimation() throws Exception {

		// Create of header information
		MEstimation estimation = new MEstimation(getCtx(), 0, get_TrxName());

		PO.copyValues(bill, estimation);
		estimation.setAD_Org_ID(bill.getAD_Org_ID());
		estimation.setC_Currency_ID(bill.getC_Currency_ID());
		estimation.setDocStatus(DocAction.STATUS_Drafted);
		estimation.setDocAction(DocAction.ACTION_Complete);
		estimation.setJP_EstimationDate(bill.getJPDateBilled());
		estimation.setVersion(Env.ONE);
		estimation.setC_DocTypeTarget_ID(p_C_DocTypeTarget_ID);
		estimation.setDateOrdered(bill.getJPCutOffDate());
		estimation.setDatePromised(bill.getJPCutOffDate());
		estimation.setDocumentNo(bill.getDocumentNo());

		MBPartner partner = new MBPartner(getCtx(), bill.getC_BPartner_ID(), get_TrxName());
		estimation.setIsDiscountPrinted(partner.isDiscountPrinted());

		MBillLine[] bLines = bill.getLines();
		if(bLines.length > 0){
			MInvoice invoice = new MInvoice(getCtx(), bLines[0].getC_Invoice_ID(), get_TrxName());

			estimation.setM_PriceList_ID(invoice.getM_PriceList_ID());

			// Exist Order receipts linked to invoices
			if(invoice.getC_Order_ID() != 0){
				MOrder order = new MOrder(getCtx(), invoice.getC_Order_ID(), get_TrxName());
				estimation.setInvoiceRule(order.getInvoiceRule());
				estimation.setDeliveryRule(order.getDeliveryRule());
				estimation.setFreightCostRule(order.getFreightCostRule());
				estimation.setDeliveryViaRule(order.getDeliveryViaRule());
				estimation.setPriorityRule(order.getPriorityRule());
			}else if(invoice.getC_Order_ID() == 0){
				setEstimationFromPartnerOrDefaultValue(estimation, partner);
				estimation.setPriorityRule(X_C_Order.PRIORITYRULE_Medium);
			}
		}

		estimation.setM_Warehouse_ID(p_M_Warehouse_ID);
		MPriceList priceList = new MPriceList(getCtx(), estimation.getM_PriceList_ID(), get_TrxName());

		estimation.setIsTaxIncluded(priceList.isTaxIncluded());
		estimation.setSendEMail(partner.isSendEMail());
		estimation.setJP_Bill_ID(bill.getJP_Bill_ID());

		estimation.saveEx(get_TrxName());

		return estimation;
	}

	/**
	 * Set Estimation from partner or Default Value
	 * If exist Partner then Get From Partner,
	 * If not exist Partner then Default Value
	 * @param estimation MEstimation
	 * @param partner MBPartner
	 */
	private void setEstimationFromPartnerOrDefaultValue(MEstimation estimation, MBPartner partner){

		if(partner.getInvoiceRule() != null){
			estimation.setInvoiceRule(partner.getInvoiceRule());
		}else{
			estimation.setInvoiceRule(X_C_Order.INVOICERULE_Immediate);
		}

		if(partner.getDeliveryRule() != null){
			estimation.setDeliveryRule(partner.getDeliveryRule());
		}else{
			estimation.setDeliveryRule(X_C_Order.DELIVERYRULE_Force);
		}

		if(partner.getFreightCostRule() != null){
			estimation.setFreightCostRule(partner.getFreightCostRule());
		}else{
			estimation.setFreightCostRule(X_C_Order.FREIGHTCOSTRULE_FreightIncluded);
		}

		if(partner.getDeliveryViaRule() != null){
			estimation.setDeliveryViaRule(partner.getDeliveryViaRule());
		}else{
			estimation.setDeliveryViaRule(X_C_Order.DELIVERYVIARULE_Pickup);
		}

	}

	/**
	 * Create of EstimationLine
	 * @param estimation parent
	 * @return EstimationLine
	 */
	private void createOfEstimationLine(MEstimation estimation){

		int lineCounter = 0;

		for(MBillLine billLine : bill.getLines())
		{
			MInvoice invoice = new MInvoice(getCtx(), billLine.getC_Invoice_ID(), get_TrxName());

			for(MInvoiceLine iLine : invoice.getLines())
			{
				MEstimationLine estLine = new MEstimationLine(estimation);
				// Copy of MBillLine
				PO.copyValues(iLine, estLine);
				estLine.setDateInvoiced(billLine.getDateInvoiced());

				if(invoice.getDateOrdered() == null){
					estLine.setDateOrdered(estimation.getDateOrdered());
				}
				else{
					estLine.setDateOrdered(invoice.getDateOrdered());
				}

				if(iLine.getC_OrderLine_ID() > 0)
				{
					estLine.setDatePromised(iLine.getC_OrderLine().getDatePromised());
					estLine.setLink_OrderLine_ID(iLine.getC_OrderLine_ID());
				}else{
					estLine.setDatePromised(estimation.getDatePromised());
				}

				if(iLine.getM_InOutLine_ID() > 0)
				{
					estLine.setDateDelivered(iLine.getM_InOutLine().getM_InOut().getMovementDate());
					String inOutDocInfo = Msg.getElement(getCtx(), "M_InOut_ID", true)+" : " + iLine.getM_InOutLine().getM_InOut().getDocumentNo()
							+ " - "+Msg.getElement(getCtx(), "Line") + " : " + iLine.getM_InOutLine().getLine();
					if(Util.isEmpty(estLine.getDescription()))
						estLine.setDescription(inOutDocInfo);
					else
						estLine.setDescription(estLine.getDescription() + " " + inOutDocInfo);
				}

				estLine.setM_Warehouse_ID(estimation.getM_Warehouse_ID());

				estLine.setQtyEntered(iLine.getQtyEntered());
				estLine.setQtyOrdered(iLine.getQtyInvoiced());
				estLine.setQtyDelivered(Env.ZERO);
				estLine.setQtyInvoiced(Env.ZERO);
				estLine.setQtyReserved(Env.ZERO);
				estLine.setFreightAmt(Env.ZERO);
				estLine.setQtyLostSales(Env.ZERO);

				estLine.setPriceEntered(iLine.getPriceEntered());
				estLine.setPriceList(iLine.getPriceList());
				estLine.setPriceActual(iLine.getPriceActual());
				estLine.setPriceLimit(iLine.getPriceLimit());
				estLine.setLineNetAmt(iLine.getLineNetAmt());
				estLine.setC_Charge_ID(iLine.getC_Charge_ID());
				estLine.setC_Tax_ID(iLine.getC_Tax_ID());

				lineCounter++;
				estLine.setLine(lineCounter*10);
				estLine.saveEx(get_TrxName());

			}//for
		}//for
	}
}
