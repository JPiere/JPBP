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

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.PO;
import org.compiere.process.DocAction;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.MContract;
import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractLogDetail;
import jpiere.base.plugin.org.adempiere.model.MContractPSLine;
import jpiere.base.plugin.org.adempiere.model.MContractProcSchedule;


/**
* JPIERE-0431
*
* @author Hideaki Hagiwara
*
*/
public class DefaultContractProcessCreateBaseInvoiceIndirectly extends AbstractContractProcess
{

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

		MContractProcSchedule[] contractProcSchedules= MContractProcSchedule.getMContractProcSchedules(m_ContractContent.getJP_ContractContent_ID(), JP_ContractProcPeriod_ID, get_TrxName());
		for(int i = 0;  i < contractProcSchedules.length; i++)
		{

			String msg = getSkipReason_CreateBaseDocIndirectly(getCtx(), contractProcSchedules[i], JP_ContractProcPeriod_ID, false, get_TrxName());
			if(msg != null)
			{
				createContractLogDetail(msg, null, contractProcSchedules[i], null);
				continue;
			}

			/** Create Order header */
			MInvoice invoice = new MInvoice(getCtx(), 0, get_TrxName());
			PO.copyValues(contractProcSchedules[i], invoice);
			invoice.setProcessed(false);
			invoice.setDocStatus(DocAction.STATUS_Drafted);
			invoice.setAD_Org_ID(contractProcSchedules[i].getAD_Org_ID());
			invoice.setAD_OrgTrx_ID(contractProcSchedules[i].getAD_OrgTrx_ID());
			invoice.setDateOrdered(getDateOrdered());
			invoice.setDateAcct(getDateAcct());
			invoice.setDocumentNo(""); //Reset Document No
			invoice.setC_DocTypeTarget_ID(contractProcSchedules[i].getJP_BaseDocDocType_ID());
			invoice.setC_DocType_ID(contractProcSchedules[i].getJP_BaseDocDocType_ID());
			invoice.set_ValueOfColumn("JP_Contract_ID", contractProcSchedules[i].getJP_Contract_ID());
			invoice.set_ValueOfColumn("JP_ContractContent_ID", contractProcSchedules[i].getJP_ContractContent_ID());
			if(m_ContractContent.getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
				invoice.set_ValueOfColumn("JP_ContractProcPeriod_ID", contractProcSchedules[i].getJP_ContractProcPeriod_ID());

			try
			{
				invoice.saveEx(get_TrxName());
			} catch (AdempiereException e) {
				createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SaveError, null, null, e.getMessage());
				throw e;
			}finally {
				;
			}

			try
			{
				contractProcSchedules[i].setIsFactCreatedJP(true);
				contractProcSchedules[i].setC_Invoice_ID(invoice.getC_Invoice_ID());
				contractProcSchedules[i].saveEx(get_TrxName());
			} catch (AdempiereException e) {
				createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SaveError, null, contractProcSchedules[i], e.getMessage());
				throw e;
			}finally {
				;
			}

			MContractPSLine[] contractPSLines = contractProcSchedules[i].getContractPSLines();
			for(int j = 0; j < contractPSLines.length; j++)
			{
				MInvoiceLine iLine = new MInvoiceLine(getCtx(), 0, get_TrxName());
				PO.copyValues(contractPSLines[j], iLine);
				iLine.setC_Invoice_ID(invoice.getC_Invoice_ID());
				iLine.setAD_Org_ID(invoice.getAD_Org_ID());
				iLine.setAD_OrgTrx_ID(invoice.getAD_OrgTrx_ID());
				iLine.setProcessed(false);


				//Qty
				if(contractPSLines[j].getM_Product_ID() > 0)
				{
					iLine.setC_UOM_ID(contractPSLines[j].getM_Product().getC_UOM_ID());
					iLine.setQtyEntered(contractPSLines[j].getQtyOrdered());
				}else{
					iLine.setQtyEntered(contractPSLines[j].getQtyEntered());

				}
				iLine.setQtyInvoiced(contractPSLines[j].getQtyOrdered());

				//Contract Info
				if(contractPSLines[j].getJP_ContractLine_ID() != 0)
					iLine.set_ValueNoCheck("JP_ContractLine_ID", contractPSLines[j].getJP_ContractLine_ID());
				if(m_ContractContent.getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
					iLine.set_ValueOfColumn("JP_ContractProcPeriod_ID", JP_ContractProcPeriod_ID);


				try {
					iLine.saveEx(get_TrxName());//DocStatus is Draft
				} catch (AdempiereException e) {
					createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SaveError, null, invoice, e.getMessage());
					throw e;
				}finally {
					;
				}

				try
				{
					contractPSLines[j].setIsFactCreatedJP(true);
					contractPSLines[j].setC_InvoiceLine_ID(iLine.getC_InvoiceLine_ID());
					contractPSLines[j].saveEx(get_TrxName());
				} catch (AdempiereException e) {
					createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SaveError, null, contractPSLines[j], e.getMessage());
					throw e;
				}finally {
					;
				}

			}//for J


			if(m_ContractContent.getJP_ContractProcStatus().equals(MContractContent.JP_CONTRACTPROCSTATUS_Unprocessed))
			{
				m_ContractContent.setJP_ContractProcStatus(MContractContent.JP_CONTRACTPROCSTATUS_InProgress);
				try {
					m_ContractContent.save(get_TrxName());
				} catch (AdempiereException e) {
					createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SaveError, null, m_ContractContent, e.getMessage());
					throw e;
				}finally {
					;
				}
			}


			String docAction = getDocAction();
			updateContractProcStatus();
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

	}



}
