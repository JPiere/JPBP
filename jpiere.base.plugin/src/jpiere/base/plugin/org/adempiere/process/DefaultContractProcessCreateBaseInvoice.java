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
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.MContract;
import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractLine;
import jpiere.base.plugin.org.adempiere.model.MContractLogDetail;
import jpiere.base.plugin.org.adempiere.model.MContractProcPeriod;



/** JPIERE-0363
*
* @author Hideaki Hagiwara
*
*/
public class DefaultContractProcessCreateBaseInvoice extends AbstractContractProcess {
	
	
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
		
		
		//Check Overlap Header
		MInvoice[] invoices = m_ContractContent.getInvoiceByContractPeriod(Env.getCtx(), JP_ContractProcPeriod_ID, get_TrxName());
		if(invoices != null && invoices.length > 0)
		{
			createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SkippedContractProcessForOverlapContractProcessPeriod, null,  invoices[0], null);			
			return "";			
		}
		
		
		/** Pre check - Pre judgment create Document or not. */
		MContractLine[] 	m_lines = m_ContractContent.getLines();
		boolean isCreateDocLine = false;
		for(int i = 0; i < m_lines.length; i++)
		{
			if(!isCreateDocLine(m_lines[i], JP_ContractProcPeriod_ID, false))
				continue;
			
			isCreateDocLine = true;
			break;
		}
		
		if(!isCreateDocLine)
		{
			createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_AllContractContentLineWasSkipped, null, null, null);	
			return "";
		}
		
		
		/** Create Invoice header */
		MInvoice invoice = new MInvoice(getCtx(), 0, get_TrxName());
		PO.copyValues(m_ContractContent, invoice);
		invoice.setProcessed(false);
		invoice.setDocStatus(DocAction.STATUS_Drafted);
		invoice.setAD_Org_ID(m_ContractContent.getAD_Org_ID());
		invoice.setAD_OrgTrx_ID(m_ContractContent.getAD_OrgTrx_ID());
		invoice.setDateInvoiced(getDateInvoiced());
		invoice.setDateAcct(getDateAcct());		
		invoice.setDocumentNo(""); //Reset Document No
		invoice.setC_DocTypeTarget_ID(m_ContractContent.getJP_BaseDocDocType_ID());
		invoice.setC_DocType_ID(m_ContractContent.getJP_BaseDocDocType_ID());
		invoice.set_ValueOfColumn("JP_Contract_ID", m_ContractContent.getParent().getJP_Contract_ID());
		invoice.set_ValueOfColumn("JP_ContractContent_ID", m_ContractContent.getJP_ContractContent_ID());
		if(m_ContractContent.getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
			invoice.set_ValueOfColumn("JP_ContractProcPeriod_ID", JP_ContractProcPeriod_ID);
	
		try {
			invoice.saveEx(get_TrxName());
		} catch (AdempiereException e) {
			createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SaveError, null, null, e.getMessage());
			throw e;
		}finally {
			;
		}
		
		/** Create Invoice Line */
		isCreateDocLine = false; //Reset
		for(int i = 0; i < m_lines.length; i++)
		{
			if(!isCreateDocLine(m_lines[i], JP_ContractProcPeriod_ID, true))
				continue;
			
			MInvoiceLine iline = new MInvoiceLine(getCtx(), 0, get_TrxName());
			PO.copyValues(m_lines[i], iline);
			iline.setProcessed(false);
			iline.setC_Invoice_ID(invoice.getC_Invoice_ID());
			iline.setAD_Org_ID(invoice.getAD_Org_ID());
			iline.setAD_OrgTrx_ID(invoice.getAD_OrgTrx_ID());
			iline.setQtyInvoiced(m_lines[i].getQtyOrdered());//QtyInvoiced = QtyOrdered because Base Doc
			iline.set_ValueNoCheck("JP_ContractLine_ID", m_lines[i].getJP_ContractLine_ID());
			if(m_ContractContent.getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
				iline.set_ValueOfColumn("JP_ContractProcPeriod_ID", getJP_ContractProctPeriod_ID());
			
			try{
				iline.saveEx(get_TrxName());
			} catch (AdempiereException e) {
				createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SaveError, null, invoice, e.getMessage());
				throw e;
			}finally {
				;
			}
			isCreateDocLine = true;
		}
		
		if(isCreateDocLine)
		{
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
					try{
						invoice.saveEx(get_TrxName());
					} catch (AdempiereException e) {
						createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SaveError, null, invoice, e.getMessage());
						throw e;
					}finally {
						;
					}
				}
					
			}else{
				
				invoice.setDocAction(DocAction.ACTION_Complete);
				try{
					invoice.saveEx(get_TrxName());//DocStatus is Draft
				} catch (AdempiereException e) {
					createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SaveError, null, invoice, e.getMessage());
					throw e;
				}finally {
					;
				}
				
			}
			
		}else{
			
			//if by any chance
			invoice.deleteEx(true, get_TrxName());
			createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_AllContractContentLineWasSkipped, null, null, null);	
			return "";
		}
		
		
		createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_CreatedDocument, null, invoice, null);
		return "";
	}
	
	private boolean isCreateDocLine(MContractLine contractLine, int JP_ContractProcPeriod_ID, boolean isCreateLog)
	{
		if(!contractLine.isCreateDocLineJP())
		{
			if(isCreateLog)
				createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForCreateDocLineIsFalse, contractLine, null, null);
			
			return false;
		}
		
		if(!m_ContractContent.getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
			return true;
	
		//Check Overlap
		MInvoiceLine[] iLines = contractLine.getInvoiceLineByContractPeriod(getCtx(), JP_ContractProcPeriod_ID, get_TrxName());
		if(iLines != null && iLines.length > 0)
		{
			if(isCreateLog)
				createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SkippedContractProcessForOverlapContractProcessPeriod, contractLine, iLines[0], null);
			
			return false;
		}
	
		
		//Check Base Doc Line
		if(contractLine.getJP_BaseDocLinePolicy() != null)
		{
			//Lump
			if(contractLine.getJP_BaseDocLinePolicy().equals(MContractLine.JP_BASEDOCLINEPOLICY_LumpOnACertainPointOfContractProcessPeriod))
			{
				MContractProcPeriod lump_ContractProcPeriod = MContractProcPeriod.get(getCtx(), contractLine.getJP_ProcPeriod_Lump_ID());
				if(!lump_ContractProcPeriod.isContainedBaseDocContractProcPeriod(JP_ContractProcPeriod_ID))
				{
					if(isCreateLog)
						createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForOutsideOfTheBaseDocLinePeriod, contractLine, null, null);
					
					return false;
				}
			}

			//Start Period
			if(contractLine.getJP_BaseDocLinePolicy().equals(MContractLine.JP_BASEDOCLINEPOLICY_FromStartContractProcessPeriod)
					||contractLine.getJP_BaseDocLinePolicy().equals(MContractLine.JP_BASEDOCLINEPOLICY_FromStartContractProcessPeriodToEnd) )
			{
				MContractProcPeriod contractLine_Period = MContractProcPeriod.get(getCtx(), contractLine.getJP_ProcPeriod_Start_ID());
				MContractProcPeriod process_Period = MContractProcPeriod.get(getCtx(), JP_ContractProcPeriod_ID);
				if(contractLine_Period.getStartDate().compareTo(process_Period.getStartDate()) <= 0)
				{
					;//This is OK. contractLine_Period.StartDate <= process_Period.StartDate
				}else{
					
					if(isCreateLog)
						createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForOutsideOfTheBaseDocLinePeriod, contractLine, null, Msg.getElement(getCtx(), "JP_ProcPeriod_Start_ID"));
					
					return false;
				}
			}
			
			//End Period
			if(contractLine.getJP_BaseDocLinePolicy().equals(MContractLine.JP_BASEDOCLINEPOLICY_ToEndContractProcessPeriod)
					||contractLine.getJP_BaseDocLinePolicy().equals(MContractLine.JP_BASEDOCLINEPOLICY_FromStartContractProcessPeriodToEnd) )
			{
				MContractProcPeriod contractLine_Period = MContractProcPeriod.get(getCtx(), contractLine.getJP_ProcPeriod_End_ID());
				MContractProcPeriod process_ContractProcPeriod = MContractProcPeriod.get(getCtx(), JP_ContractProcPeriod_ID);
				if(contractLine_Period.getEndDate().compareTo(process_ContractProcPeriod.getEndDate()) >= 0)
				{
					;//This is OK. contractLine_Period.EndDate >= process_Period.EndDate
				}else{
					
					if(isCreateLog)
						createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForOutsideOfTheBaseDocLinePeriod, contractLine, null, "JP_ProcPeriod_End_ID");
					
					return false;
				}
			}
			
			return true;
		}
		

		
		
		return true;
	}
}
