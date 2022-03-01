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

import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.PO;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.MContract;
import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractLine;
import jpiere.base.plugin.org.adempiere.model.MInvoiceJP;
import jpiere.base.plugin.org.adempiere.model.MOrderJP;


/**
 *
 *  JPIERE-0445
 *
 * Create Document From Spot Contract Content Process
 *
 * @author Hideaki Hagiwara
 *
 */
public class CreateDocFromSpotContractContent extends SvrProcess {

	private String p_DocAction = null;
	private int JP_ContractContent_ID = 0;
	private MContractContent m_ContractContent = null;

	@Override
	protected void prepare()
	{

		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
			{
				;
			}else if (name.equals("DocAction")){
				p_DocAction = para[i].getParameterAsString();
			}else{
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}
		}

		JP_ContractContent_ID = getRecord_ID();
		m_ContractContent = new MContractContent(getCtx(), JP_ContractContent_ID, get_TrxName());

	}

	@Override
	protected String doIt() throws Exception
	{

		if(!m_ContractContent.getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_SpotContract))
		{
			//Spot Contract Only.
			throw new Exception(Msg.getMsg(getCtx(), "JP_SpotContractOnly"));
		}


		if(m_ContractContent.getDocStatus().equals(DocAction.STATUS_Reversed)
				|| m_ContractContent.getDocStatus().equals(DocAction.STATUS_Voided))
		{
			throw new Exception(Msg.getMsg(getCtx(), "JP_NotValidDocStatus"));
		}


		if(m_ContractContent.getDocBaseType().equals(MContractContent.DOCBASETYPE_SalesOrder)
			|| m_ContractContent.getDocBaseType().equals(MContractContent.DOCBASETYPE_PurchaseOrder))
		{

			createOrder();

		}else if(m_ContractContent.getDocBaseType().equals(MContractContent.DOCBASETYPE_ARInvoice)
				|| m_ContractContent.getDocBaseType().equals(MContractContent.DOCBASETYPE_APInvoice)) {

			createInvoice();

		}else {

			throw new Exception(Msg.getMsg(getCtx(), "JP_UnexpectedError"));
		}

		return null;
	}


	private void createOrder() throws Exception
	{

		MOrderJP order = new MOrderJP(getCtx(), 0, get_TrxName());
		PO.copyValues(m_ContractContent, order);

		order.setAD_Org_ID(m_ContractContent.getAD_Org_ID());
		order.setC_DocTypeTarget_ID(m_ContractContent.getJP_BaseDocDocType_ID());
		order.setC_DocType_ID(m_ContractContent.getJP_BaseDocDocType_ID());
		if(order.getC_DocType().isDocNoControlled())
		{
			order.setDocumentNo(null);
		}
		order.set_ValueNoCheck("JP_Contract_ID", m_ContractContent.getJP_Contract_ID());
		order.set_ValueNoCheck("JP_ContractContent_ID", m_ContractContent.getJP_ContractContent_ID());
		order.setDocStatus(DocAction.STATUS_Drafted);
		order.setDocAction(DocAction.ACTION_Complete);
		order.setProcessed(false);

		order.saveEx(get_TrxName());

		MContractLine[] cLines = m_ContractContent.getLines();
		if(cLines.length==0)
		{
			//No Document Lines found
			throw new Exception(Msg.getMsg(getCtx(), "NoLines"));
		}

		for(int i = 0; i < cLines.length; i++)
		{
			if(cLines[i].isCreateDocLineJP())
				createOrderLine(order, cLines[i]);
		}

		if(!Util.isEmpty(p_DocAction))
		{
			order.processIt(p_DocAction);
			order.saveEx(get_TrxName());
		}

		addBufferLog(0, null, null, Msg.getElement(getCtx(), "DocumentNo") + " : " + order.getDocumentNo(), MOrder.Table_ID, order.getC_Order_ID());

	}

	private void createOrderLine(MOrder order, MContractLine cLine)
	{
		MOrderLine oLine = new MOrderLine(order);
		PO.copyValues(cLine, oLine);
		oLine.setQtyEntered(cLine.getQtyEntered());
		oLine.setQtyOrdered(cLine.getQtyOrdered());
		oLine.setQtyInvoiced(Env.ZERO);
		oLine.setQtyDelivered(Env.ZERO);
		oLine.set_ValueNoCheck("JP_QtyRecognized", Env.ZERO);
		oLine.setQtyReserved(Env.ZERO);
		oLine.set_ValueNoCheck("JP_ContractLine_ID", cLine.getJP_ContractLine_ID());
		oLine.setProcessed(false);
		oLine.saveEx(get_TrxName());
	}


	private void createInvoice() throws Exception
	{
		MInvoiceJP invoice = new MInvoiceJP(getCtx(), 0, get_TrxName());
		PO.copyValues(m_ContractContent, invoice);

		invoice.setAD_Org_ID(m_ContractContent.getAD_Org_ID());
		invoice.setDateInvoiced(m_ContractContent.getDateDoc());
		invoice.setC_DocTypeTarget_ID(m_ContractContent.getJP_BaseDocDocType_ID());
		invoice.setC_DocType_ID(m_ContractContent.getJP_BaseDocDocType_ID());
		if(invoice.getC_DocType().isDocNoControlled())
		{
			invoice.setDocumentNo(null);
		}
		invoice.set_ValueNoCheck("JP_Contract_ID", m_ContractContent.getJP_Contract_ID());
		invoice.set_ValueNoCheck("JP_ContractContent_ID", m_ContractContent.getJP_ContractContent_ID());
		invoice.setDocStatus(DocAction.STATUS_Drafted);
		invoice.setDocAction(DocAction.ACTION_Complete);
		invoice.setProcessed(false);

		invoice.saveEx(get_TrxName());

		MContractLine[] cLines = m_ContractContent.getLines();
		if(cLines.length==0)
		{
			//No Document Lines found
			throw new Exception(Msg.getMsg(getCtx(), "NoLines"));
		}

		for(int i = 0; i < cLines.length; i++)
		{
			if(cLines[i].isCreateDocLineJP())
				createInvoiceLine(invoice, cLines[i]);
		}

		if(!Util.isEmpty(p_DocAction))
		{
			invoice.processIt(p_DocAction);
			invoice.saveEx(get_TrxName());
		}

		addBufferLog(0, null, null, Msg.getElement(getCtx(), "DocumentNo") + " : " + invoice.getDocumentNo(), MInvoice.Table_ID, invoice.getC_Invoice_ID());
	}

	private void createInvoiceLine(MInvoice invoice, MContractLine cLine)
	{
		MInvoiceLine iLine = new MInvoiceLine(invoice);
		PO.copyValues(cLine, iLine);
		iLine.setQtyEntered(cLine.getQtyEntered());
		iLine.setQtyInvoiced(cLine.getQtyOrdered());
		iLine.set_ValueNoCheck("JP_ContractLine_ID", cLine.getJP_ContractLine_ID());
		iLine.setProcessed(false);
		iLine.saveEx(get_TrxName());
	}

}
