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


import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.PO;
import org.compiere.process.DocAction;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.MContract;
import jpiere.base.plugin.org.adempiere.model.MContractLine;
import jpiere.base.plugin.org.adempiere.model.MContractLog;
import jpiere.base.plugin.org.adempiere.model.MContractLogDetail;



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
			m_ContractLog.errorNum++;
			if(p_JP_ContractProcessTraceLevel.equals(MContractLog.JP_CONTRACTPROCESSTRACELEVEL_Fine)
					|| p_JP_ContractProcessTraceLevel.equals(MContractLog.JP_CONTRACTPROCESSTRACELEVEL_Warning)
					|| p_JP_ContractProcessTraceLevel.equals(MContractLog.JP_CONTRACTPROCESSTRACELEVEL_Error))
			{
				MContractLogDetail logDetail = new MContractLogDetail(getCtx(), 0, m_ContractLog.get_TrxName());
				logDetail.setJP_ContractLog_ID(m_ContractLog.getJP_ContractLog_ID());
				logDetail.setJP_ContractLogMsg(MContractLogDetail.JP_CONTRACTLOGMSG_UnexpectedError);
				
				logDetail.setJP_Contract_ID(m_ContractContent.getJP_Contract_ID());
				logDetail.setJP_ContractContent_ID(m_ContractContent.getJP_ContractContent_ID());
				
				logDetail.setJP_ContractProcPeriod_ID(JP_ContractProcPeriod_ID);
				logDetail.setJP_ContractProcess_ID(m_ContractContent.getJP_ContractProcess_ID());
				
				logDetail.setJP_ContractProcessTraceLevel(MContractLog.JP_CONTRACTPROCESSTRACELEVEL_Error);
				logDetail.saveEx();
			}
			
			return "";
		}
		
		
		//Check Overlap
		MInvoice[] invoices = getOverlapPeriodInvoice(JP_ContractProcPeriod_ID);
		if(invoices != null && invoices.length > 0)
		{
			m_ContractLog.skipContractContentNum++;
			if(p_JP_ContractProcessTraceLevel.equals(MContractLog.JP_CONTRACTPROCESSTRACELEVEL_Warning)
					|| p_JP_ContractProcessTraceLevel.equals(MContractLog.JP_CONTRACTPROCESSTRACELEVEL_Error))
			{
				MContractLogDetail logDetail = new MContractLogDetail(getCtx(), 0, m_ContractLog.get_TrxName());
				logDetail.setJP_ContractLog_ID(m_ContractLog.getJP_ContractLog_ID());
				logDetail.setJP_ContractLogMsg(MContractLogDetail.JP_CONTRACTLOGMSG_SkipContractProcessForOverlapContractProcessPeriod);
				
				logDetail.setJP_Contract_ID(m_ContractContent.getJP_Contract_ID());
				logDetail.setJP_ContractContent_ID(m_ContractContent.getJP_ContractContent_ID());
				
				logDetail.setJP_ContractProcPeriod_ID(JP_ContractProcPeriod_ID);
				logDetail.setJP_ContractProcess_ID(m_ContractContent.getJP_ContractProcess_ID());
				logDetail.setC_Invoice_ID(invoices[0].getC_Invoice_ID());
				
				logDetail.setJP_ContractProcessTraceLevel(MContractLog.JP_CONTRACTPROCESSTRACELEVEL_Warning);
				logDetail.saveEx();
			}
			
			return "";
		}//Check Overlap
		
		
		
		/** Create Invoice header */
		MInvoice invoice = new MInvoice(getCtx(), 0, get_TrxName());
		PO.copyValues(m_ContractContent, invoice);
		invoice.setProcessed(false);
		invoice.setDocStatus(DocAction.STATUS_Drafted);
		invoice.setAD_Org_ID(m_ContractContent.getAD_Org_ID());
		invoice.setAD_OrgTrx_ID(m_ContractContent.getAD_OrgTrx_ID());
		invoice.setDateAcct(getDateAcct());		
		invoice.setDocumentNo(""); //Reset Document No
		invoice.setC_DocTypeTarget_ID(m_ContractContent.getJP_BaseDocDocType_ID());
		invoice.set_ValueOfColumn("JP_Contract_ID", m_ContractContent.getParent().getJP_Contract_ID());
		invoice.set_ValueOfColumn("JP_ContractContent_ID", m_ContractContent.getJP_ContractContent_ID());
		if(m_ContractContent.getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
			invoice.set_ValueOfColumn("JP_ContractProcPeriod_ID", JP_ContractProcPeriod_ID);
	
		invoice.saveEx(get_TrxName());
		
		/** Create Invoice Line */
		MContractLine[] 	m_lines = m_ContractContent.getLines();
		boolean isCrateDocLine = false;
		for(int i = 0; i < m_lines.length; i++)
		{
			if(!m_lines[i].isCreateDocLineJP())
				continue;
			
			//Check Overlap
			MInvoiceLine[] iLines = getOverlapPeriodInvoiceLine(m_lines[i],JP_ContractProcPeriod_ID);
			if(iLines != null && iLines.length > 0)
			{
				m_ContractLog.skipContractLineNum++;
				if(p_JP_ContractProcessTraceLevel.equals(MContractLog.JP_CONTRACTPROCESSTRACELEVEL_Warning)
						|| p_JP_ContractProcessTraceLevel.equals(MContractLog.JP_CONTRACTPROCESSTRACELEVEL_Error))
				{
					MContractLogDetail logDetail = new MContractLogDetail(getCtx(), 0, m_ContractLog.get_TrxName());
					logDetail.setJP_ContractLog_ID(m_ContractLog.getJP_ContractLog_ID());
					logDetail.setJP_ContractLogMsg(MContractLogDetail.JP_CONTRACTLOGMSG_SkipContractProcessForOverlapContractProcessPeriod);
					
					logDetail.setJP_Contract_ID(m_ContractContent.getJP_Contract_ID());
					logDetail.setJP_ContractContent_ID(m_ContractContent.getJP_ContractContent_ID());
					logDetail.setJP_ContractLine_ID(m_lines[i].getJP_ContractLine_ID());
					
					logDetail.setJP_ContractProcPeriod_ID(JP_ContractProcPeriod_ID);
					logDetail.setJP_ContractProcess_ID(m_ContractContent.getJP_ContractProcess_ID());
					
					logDetail.setC_Invoice_ID(iLines[0].getC_Invoice_ID());
					logDetail.setC_Invoice_ID(iLines[0].getC_InvoiceLine_ID());
					
					logDetail.setJP_ContractProcessTraceLevel(MContractLog.JP_CONTRACTPROCESSTRACELEVEL_Warning);
					logDetail.saveEx();
				}
				
				continue;
				
			}//Check Overlap
			
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
			
			iline.saveEx(get_TrxName());
			isCrateDocLine = true;
		}
		
		if(isCrateDocLine)
		{
			String docAction = getDocAction();
			updateContractProcStatus();
			if(!Util.isEmpty(docAction))
			{
				invoice.processIt(docAction);
				if(!docAction.equals(DocAction.ACTION_Complete))
					invoice.saveEx(get_TrxName());
			}else{
				invoice.saveEx(get_TrxName());//DocStatus is Draft
			}
			
		}else{
			
			invoice.deleteEx(true, get_TrxName());
			m_ContractLog.skipContractContentNum++;
			if(p_JP_ContractProcessTraceLevel.equals(MContractLog.JP_CONTRACTPROCESSTRACELEVEL_Warning)
					|| p_JP_ContractProcessTraceLevel.equals(MContractLog.JP_CONTRACTPROCESSTRACELEVEL_Error))
			{
				MContractLogDetail logDetail = new MContractLogDetail(getCtx(), 0, m_ContractLog.get_TrxName());
				logDetail.setJP_ContractLog_ID(m_ContractLog.getJP_ContractLog_ID());
				logDetail.setJP_ContractLogMsg(MContractLogDetail.JP_CONTRACTLOGMSG_AllContractContentLineWasSkipped);
				
				logDetail.setJP_Contract_ID(m_ContractContent.getJP_Contract_ID());
				logDetail.setJP_ContractContent_ID(m_ContractContent.getJP_ContractContent_ID());
				
				logDetail.setJP_ContractProcPeriod_ID(JP_ContractProcPeriod_ID);
				logDetail.setJP_ContractProcess_ID(m_ContractContent.getJP_ContractProcess_ID());
				
				logDetail.setJP_ContractProcessTraceLevel(MContractLog.JP_CONTRACTPROCESSTRACELEVEL_Warning);
				logDetail.saveEx();
			}
			
			return "";
		}
		
		addBufferLog(0, null, null, invoice.getDocumentInfo(), MInvoice.Table_ID, invoice.getC_Invoice_ID());
		m_ContractLog.createDocNum++;
		if(p_JP_ContractProcessTraceLevel.equals(MContractLog.JP_CONTRACTPROCESSTRACELEVEL_Fine))
		{
			MContractLogDetail logDetail = new MContractLogDetail(getCtx(), 0, m_ContractLog.get_TrxName());
			logDetail.setJP_ContractLog_ID(m_ContractLog.getJP_ContractLog_ID());
			logDetail.setJP_ContractLogMsg(MContractLogDetail.JP_CONTRACTLOGMSG_CreateDocument);
			
			logDetail.setJP_Contract_ID(m_ContractContent.getJP_Contract_ID());
			logDetail.setJP_ContractContent_ID(m_ContractContent.getJP_ContractContent_ID());
			logDetail.setJP_ContractProcPeriod_ID(JP_ContractProcPeriod_ID);
			logDetail.setJP_ContractProcess_ID(m_ContractContent.getJP_ContractProcess_ID());
			
			logDetail.setC_Invoice_ID(invoice.getC_Invoice_ID());
			logDetail.setJP_ContractProcessTraceLevel(MContractLog.JP_CONTRACTPROCESSTRACELEVEL_Fine);
			logDetail.saveEx();
		}
		
		return "";
	}
	
}
