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

import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.PO;
import org.compiere.process.DocAction;
import org.compiere.util.Env;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.MContract;
import jpiere.base.plugin.org.adempiere.model.MContractLine;
import jpiere.base.plugin.org.adempiere.model.MContractLog;
import jpiere.base.plugin.org.adempiere.model.MContractLogDetail;


/** 
* JPIERE-0363
*
* @author Hideaki Hagiwara
*
*/
public class DefaultContractProcessCreateBaseOrder extends AbstractContractProcess 
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
				|| JP_ContractProcPeriod_ID == 0)
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
		MOrder[] orders = getOverlapPeriodOrder(JP_ContractProcPeriod_ID);
		if(orders != null && orders.length > 0)
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
				logDetail.setC_Order_ID(orders[0].getC_Order_ID());
				
				logDetail.setJP_ContractProcessTraceLevel(MContractLog.JP_CONTRACTPROCESSTRACELEVEL_Warning);
				logDetail.saveEx();
			}
			
			return "";
		}//Check Overlap
		
		/** Create Order header */
		MOrder order = new MOrder(getCtx(), 0, get_TrxName());

		
		PO.copyValues(m_ContractContent, order);
		order.setProcessed(false);
		order.setDocStatus(DocAction.STATUS_Drafted);
		order.setAD_Org_ID(m_ContractContent.getAD_Org_ID());
		order.setAD_OrgTrx_ID(m_ContractContent.getAD_OrgTrx_ID());
		order.setDateOrdered(getDateOrdered());
		order.setDateAcct(getDateAcct());
		order.setDatePromised(getOrderHeaderDatePromised(p_DateAcct)); //DateAcct is basis.
		order.setDocumentNo(""); //Reset Document No
		order.setC_DocTypeTarget_ID(m_ContractContent.getJP_BaseDocDocType_ID());
		order.set_ValueOfColumn("JP_Contract_ID", m_ContractContent.getParent().getJP_Contract_ID());
		order.set_ValueOfColumn("JP_ContractContent_ID", m_ContractContent.getJP_ContractContent_ID());
		if(m_ContractContent.getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
			order.set_ValueOfColumn("JP_ContractProcPeriod_ID", JP_ContractProcPeriod_ID);
		
		order.saveEx(get_TrxName());
		
		/** Create Order Line */
		MContractLine[] 	m_lines = m_ContractContent.getLines();
		boolean isCrateDocLine = false;
		for(int i = 0; i < m_lines.length; i++)
		{
			if(!m_lines[i].isCreateDocLineJP())
				continue;
			
			//Check Overlap
			MOrderLine[] oLines = getOverlapPeriodOrderLine(m_lines[i],JP_ContractProcPeriod_ID);
			if(oLines != null && oLines.length > 0)
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
					
					logDetail.setJP_ContractProcessTraceLevel(MContractLog.JP_CONTRACTPROCESSTRACELEVEL_Warning);
					logDetail.saveEx();
				}
				
				continue;
				
			}//Check Overlap
			
			MOrderLine oline = new MOrderLine(getCtx(), 0, get_TrxName());
			PO.copyValues(m_lines[i], oline);
			oline.setC_Order_ID(order.getC_Order_ID());
			oline.setAD_Org_ID(order.getAD_Org_ID());
			oline.setAD_OrgTrx_ID(order.getAD_OrgTrx_ID());
			oline.setProcessed(false);
			oline.setQtyReserved(Env.ZERO);
			oline.setQtyDelivered(Env.ZERO);
			oline.setQtyInvoiced(Env.ZERO);
			oline.set_ValueNoCheck("JP_ContractLine_ID", m_lines[i].getJP_ContractLine_ID());
			if(m_ContractContent.getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
				oline.set_ValueOfColumn("JP_ContractProcPeriod_ID", JP_ContractProcPeriod_ID);
			oline.setDatePromised(getOrderLineDatePromised(m_lines[i]));
			
			oline.saveEx(get_TrxName());
			isCrateDocLine = true;
		}
		
		
		if(isCrateDocLine)
		{
			String docAction = getDocAction();
			updateContractProcStatus();
			if(!Util.isEmpty(docAction))
			{
				order.processIt(docAction);
				if(!docAction.equals(DocAction.ACTION_Complete))
					order.saveEx(get_TrxName());
			}else{
				order.saveEx(get_TrxName());//DocStatus is Draft
			}
			
		}else{
			
			order.deleteEx(true, get_TrxName());
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
		

		addBufferLog(0, null, null, order.getDocumentInfo(), MOrder.Table_ID, order.getC_Order_ID());
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
			
			logDetail.setC_Order_ID(order.getC_Order_ID());
			logDetail.setJP_ContractProcessTraceLevel(MContractLog.JP_CONTRACTPROCESSTRACELEVEL_Fine);
			logDetail.saveEx();
		}

		
		return "";
	}
	
}
