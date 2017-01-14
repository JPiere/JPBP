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

import org.compiere.model.I_C_OrderLine;
import org.compiere.model.MInOut;
import org.compiere.model.MInOutLine;
import org.compiere.model.MOrder;
import org.compiere.model.PO;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;

import jpiere.base.plugin.org.adempiere.model.MEstimation;
import jpiere.base.plugin.org.adempiere.model.MEstimationLine;


/**
 * JPIERE-0265 : Create Estimation From Invoice
 *
 * @author Yohei Takamura
 *
 */



public class CreateEstimationFromInOut extends SvrProcess {

	private int		p_M_InOut_ID = 0;
	private int		p_C_DocType_ID = 0;
	private String		p_DocAction = null;
	private MInOut		inout = null;

	@Override
	//Parameters
	protected void prepare() {

		p_M_InOut_ID = getRecord_ID();

		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null){
				;
			}else if (name.equals("C_DocType_ID")){
				p_C_DocType_ID = para[i].getParameterAsInt();

			}else if (name.equals("DocAction")){
				p_DocAction = para[i].getParameterAsString();
			}
			else{
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}

		}

		inout = new MInOut(getCtx(), p_M_InOut_ID, get_TrxName()) ;

	}

	@Override
	protected String doIt() throws Exception
	{

		MInOutLine[] inoutLines = inout.getLines();
        MOrder order = new MOrder(getCtx(), inout.getC_Order_ID(), get_TrxName()) ;
		MEstimation estimation = new MEstimation(getCtx(), 0, get_TrxName()) ;

		PO.copyValues(inout, estimation);
		estimation.setAD_Org_ID(inout.getAD_Org_ID());
		estimation.setDocumentNo(inout.getDocumentNo());
		estimation.setJP_EstimationDate(inout.getMovementDate());
		estimation.setDocAction(DocAction.ACTION_Complete);
		estimation.setDocStatus(DocAction.STATUS_Drafted);
		estimation.setVersion(Env.ONE);
		estimation.setC_DocTypeTarget_ID(p_C_DocType_ID);
		estimation.setC_DocType_ID(p_C_DocType_ID);
		estimation.setC_Currency_ID(order.getC_Currency_ID());
		estimation.setPaymentRule(order.getPaymentRule());
		estimation.setC_PaymentTerm_ID(order.getC_PaymentTerm_ID());
		estimation.setInvoiceRule(order.getInvoiceRule());
		estimation.setTotalLines(Env.ZERO);
		estimation.setGrandTotal(Env.ZERO);
        estimation.setM_PriceList_ID(order.getM_PriceList_ID());
		estimation.setIsTaxIncluded(order.isTaxIncluded());
		estimation.setIsPriviledgedRate(order.isPriviledgedRate());
		estimation.setDatePromised(order.getDatePromised());
		estimation.setM_InOut_ID(p_M_InOut_ID);
		estimation.saveEx(get_TrxName());

		for(int i = 0; i < inoutLines.length; i++)
		{
			MEstimationLine estLine = new MEstimationLine(estimation);
			PO.copyValues(inoutLines[i], estLine);
			I_C_OrderLine ol = inoutLines[i].getC_OrderLine();
			estLine.setAD_Org_ID(ol.getAD_Org_ID());
			estLine.setM_Warehouse_ID(ol.getM_Warehouse_ID());
			estLine.setQtyEntered(inoutLines[i].getQtyEntered());
			estLine.setQtyOrdered(inoutLines[i].getMovementQty());
			estLine.setQtyReserved(Env.ZERO);
			estLine.setQtyDelivered(Env.ZERO);
			estLine.setQtyInvoiced(Env.ZERO);
			estLine.setQtyLostSales(Env.ZERO);
			estLine.setC_Currency_ID(ol.getC_Currency_ID());
			estLine.setPriceEntered(ol.getPriceEntered());
			estLine.setPriceActual(ol.getPriceActual());
			estLine.setPriceList(ol.getPriceList());
			estLine.setPriceLimit(ol.getPriceLimit());
			estLine.setFreightAmt(ol.getFreightAmt());
			estLine.setLineNetAmt(estLine.getQtyOrdered().multiply(estLine.getPriceActual()));
			estLine.setC_Tax_ID(ol.getC_Tax_ID());
			estLine.saveEx(get_TrxName());
		}

		if(inoutLines.length > 0)
		{
			estimation.processIt(p_DocAction);
			estimation.saveEx(get_TrxName());
		}
		addBufferLog(0, null, null, estimation.getDocumentInfo(), estimation.get_Table_ID(), estimation.getJP_Estimation_ID());

		return estimation.getDocumentInfo();

	}

}
