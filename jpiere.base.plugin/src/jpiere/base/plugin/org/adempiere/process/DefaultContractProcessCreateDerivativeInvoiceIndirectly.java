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

import java.util.ArrayList;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MProduct;
import org.compiere.model.MUOM;
import org.compiere.model.PO;
import org.compiere.process.DocAction;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.MContract;
import jpiere.base.plugin.org.adempiere.model.MContractLogDetail;
import jpiere.base.plugin.org.adempiere.model.MContractPSInvoiceLine;
import jpiere.base.plugin.org.adempiere.model.MContractProcPeriod;
import jpiere.base.plugin.org.adempiere.model.MContractProcSchedule;

/**
* JPIERE-0431
*
* @author Hideaki Hagiwara
*
*/
public class DefaultContractProcessCreateDerivativeInvoiceIndirectly extends AbstractContractProcess {


	ArrayList<MOrderLine> overQtyOrderedLineList = new ArrayList<MOrderLine>();

	@Override
	protected void prepare()
	{

		super.prepare();
	}

	@Override
	protected String doIt() throws Exception
	{
		super.doIt();

		int JP_ContractProcPeriod_ID = 0;
		if(m_ContractContent.getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
			JP_ContractProcPeriod_ID = getJP_ContractProctPeriod_ID();

		if(m_ContractContent.getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract)
				&& JP_ContractProcPeriod_ID == 0)
		{
			String descriptionMsg = Msg.getMsg(getCtx(), "NotFound") + " : " + Msg.getElement(getCtx(), "JP_ContractProcPeriod_ID");
			createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_UnexpectedError, null,  null, descriptionMsg);
			return "";
		}


		MContractProcPeriod orderProcPeriod = getBaseDocContractProcPeriodFromDerivativeDocContractProcPeriod(JP_ContractProcPeriod_ID);
		if(m_ContractContent.getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract)
				&& orderProcPeriod == null)
		{
			String descriptionMsg = Msg.getMsg(getCtx(), "NotFound") + " : " + Msg.getElement(getCtx(), "JP_ContractProcPeriod_ID");
			createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_UnexpectedError, null,  null, descriptionMsg);
			return "";
		}


		MContractProcSchedule[] contractProcSchedules= MContractProcSchedule.getMContractProcSchedules(m_ContractContent.getJP_ContractContent_ID(), orderProcPeriod.getJP_ContractProcPeriod_ID(), get_TrxName());
		for(int i = 0;  i < contractProcSchedules.length; i++)
		{

			String msg = getSkipReason_CreateDerivativeInvoiceIndirectly(getCtx(), contractProcSchedules[i], JP_ContractProcPeriod_ID, get_TrxName());
			if(msg != null)
			{
				createContractLogDetail(msg, null, contractProcSchedules[i], null);
				continue;
			}


			MOrder order = new MOrder(getCtx(), contractProcSchedules[i].getC_Order_ID(), get_TrxName());

			//Check Order
			if(!order.getDocStatus().equals(DocAction.ACTION_Complete))
			{
				createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForDocumentStatusOfOrderIsNotCompleted, null, order, null);
				continue;
			}


			/** Create InOut header */
			MInvoice invoice = new MInvoice(getCtx(), 0, get_TrxName());
			PO.copyValues(order, invoice);
			invoice.setC_Order_ID(order.getC_Order_ID());
			invoice.setProcessed(false);
			invoice.setDocStatus(DocAction.STATUS_Drafted);
			invoice.setAD_Org_ID(m_ContractContent.getAD_Org_ID());
			invoice.setAD_OrgTrx_ID(m_ContractContent.getAD_OrgTrx_ID());
			invoice.setDocumentNo(""); //Reset Document No
			invoice.setC_DocType_ID(order.getC_DocTypeTarget().getC_DocTypeInvoice_ID());
			invoice.setC_DocTypeTarget_ID(order.getC_DocTypeTarget().getC_DocTypeInvoice_ID());
			invoice.setDateInvoiced(getDateDoc());
			invoice.setDateAcct(getDateAcct());

			try{
				invoice.saveEx(get_TrxName());
			} catch (AdempiereException e) {
				createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SaveError, null, contractProcSchedules[i], e.getMessage());
				throw e;
			}finally {
				;
			}


			MContractPSInvoiceLine[] contractPSInvoiceLines = contractProcSchedules[i].getContractPSInvoiceLines(JP_ContractProcPeriod_ID, false);
			for(int j = 0; j < contractPSInvoiceLines.length; j++)
			{

				if(contractPSInvoiceLines[j].getJP_ContractPSLine().getC_OrderLine_ID() == 0)
					continue;

				if(contractPSInvoiceLines[j].isFactCreatedJP())
					continue;

				MInvoiceLine iLine = new MInvoiceLine(getCtx(), 0, get_TrxName());
				PO.copyValues(contractPSInvoiceLines[j], iLine);
				iLine.setC_OrderLine_ID(contractPSInvoiceLines[j].getJP_ContractPSLine().getC_OrderLine_ID());
				iLine.setProcessed(false);
				iLine.setC_Invoice_ID(invoice.getC_Invoice_ID());
				iLine.setAD_Org_ID(invoice.getAD_Org_ID());
				iLine.setAD_OrgTrx_ID(invoice.getAD_OrgTrx_ID());
				iLine.setLine(contractPSInvoiceLines[j].getLine());

				if(iLine.getM_Product_ID() > 0)
					iLine.setC_UOM_ID(MProduct.get(getCtx(), iLine.getM_Product_ID()).getC_UOM_ID());
				else
					iLine.setC_UOM_ID(MUOM.getDefault_UOM_ID(getCtx()));

				iLine.setQtyEntered(contractPSInvoiceLines[j].getQtyInvoiced());
				iLine.setQtyInvoiced(contractPSInvoiceLines[j].getQtyInvoiced());
				if(contractPSInvoiceLines[j].getJP_ContractLine_ID() != 0)
				{
					iLine.set_ValueNoCheck("JP_ContractProcPeriod_ID", JP_ContractProcPeriod_ID);
					iLine.set_ValueNoCheck("JP_ContractLine_ID", contractPSInvoiceLines[j].getJP_ContractLine_ID());
				}



				try{
					iLine.saveEx(get_TrxName());
				} catch (AdempiereException e) {
					createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SaveError, null, invoice, e.getMessage());
					throw e;
				}finally {
					;
				}

				contractPSInvoiceLines[j].setIsFactCreatedJP(true);
				contractPSInvoiceLines[j].setC_InvoiceLine_ID(iLine.getC_InvoiceLine_ID());
				try{
					contractPSInvoiceLines[j].saveEx(get_TrxName());
				} catch (AdempiereException e) {
					createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SaveError, null, contractPSInvoiceLines[j], e.getMessage());
					throw e;
				}finally {
					;
				}

			}//for J

			//Doc Action
			String docAction = getDocAction();
			if(!Util.isEmpty(docAction))
			{
				if(!invoice.processIt(docAction))
				{
					createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_DocumentActionError, null, invoice, invoice.getProcessMsg());
					throw new AdempiereException(invoice.getProcessMsg());
				}

				if(!docAction.equals(DocAction.ACTION_Complete))
				{
					invoice.setDocAction(DocAction.ACTION_Complete);
				}
				
				try {
					invoice.saveEx(get_TrxName());
				} catch (AdempiereException e) {
					createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SaveError, null, invoice, e.getMessage());
					throw e;
				}finally {
					;
				}

			}else{

				invoice.setDocAction(DocAction.ACTION_Complete);
				try {
					invoice.saveEx(get_TrxName());//DocStatus is Draft
				} catch (AdempiereException e) {
					createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SaveError, null, invoice, e.getMessage());
					throw e;
				}finally {
					;
				}
			}

			createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_CreatedDocument, null, invoice, null);

		}//for i


		return "";

	}//doIt()



}
