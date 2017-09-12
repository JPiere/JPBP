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

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.PO;
import org.compiere.process.DocAction;
import org.compiere.util.Env;
import org.compiere.util.Msg;

import jpiere.base.plugin.org.adempiere.model.MContract;
import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractLine;
import jpiere.base.plugin.org.adempiere.model.MContractProcPeriod;



/** JPIERE-0363
*
* @author Hideaki Hagiwara
*
*/
public class DefaultCreateOrderFromContract extends AbstractContractProcess {
	
	
	@Override
	protected void prepare() 
	{		
		super.prepare();
		
	}
	
	@Override
	protected String doIt() throws Exception 
	{
		super.doIt();		
		
		MContractContent m_ContractContent = getMContractContent();
		
		//Check Overlap
		MOrder order = m_ContractContent.getOrderByContractPeriod(Env.getCtx(), getJP_ContractProcPeriod_ID());
		if(order != null)
		{
			String msg = "選択した契約処理期間の伝票は既に作成されています。" 
					 + "  " + Msg.getElement(getCtx(), "JP_ContractProcPeriod_ID") + " : " + MContractProcPeriod.get(getCtx(), getJP_ContractProcPeriod_ID()).toString()
					 + "  " + m_ContractContent.getDocumentInfo()
					 + "  --> " + order.getDocumentInfo();//TODO メッセージ化
			throw new Exception(msg);
		}
		
		/** Create Order header */
		order = new MOrder(getCtx(), 0, get_TrxName());
		PO.copyValues(m_ContractContent, order);
		order.setProcessed(false);
		order.setDocStatus(DocAction.STATUS_Drafted);
		order.setAD_Org_ID(m_ContractContent.getAD_Org_ID());
		order.setAD_OrgTrx_ID(m_ContractContent.getAD_OrgTrx_ID());
		order.setDateOrdered(getDateDoc());
		order.setDateAcct(getDateAcct());
	
		
		//DatePromised
		LocalDateTime dateAcctLocal = getDateAcct().toLocalDateTime();
		dateAcctLocal = dateAcctLocal.plusDays(m_ContractContent.getDeliveryTime_Promised());
		order.setDatePromised(Timestamp.valueOf(dateAcctLocal));
		
		order.setDocumentNo(""); //Reset Document No
		order.setC_DocTypeTarget_ID(m_ContractContent.getJP_BaseDocDocType_ID());
		order.set_ValueOfColumn("JP_Contract_ID", m_ContractContent.getParent().getJP_Contract_ID());
		order.set_ValueOfColumn("JP_ContractContent_ID", m_ContractContent.getJP_ContractContent_ID());
		if(m_ContractContent.getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
			order.set_ValueOfColumn("JP_ContractProcPeriod_ID", getJP_ContractProcPeriod_ID());
		
		order.saveEx(get_TrxName());
		
		/** Create Order Line */
		MContractLine[] 	m_lines = m_ContractContent.getLines();
		boolean isCrateDocLine = false;
		for(int i = 0; i < m_lines.length; i++)
		{
			if(!m_lines[i].isCreateDocLineJP())
				continue;
			
			MOrderLine oline = new MOrderLine(getCtx(), 0, get_TrxName());
			PO.copyValues(m_lines[i], oline);
			oline.setProcessed(false);
			oline.setC_Order_ID(order.getC_Order_ID());
			oline.setAD_Org_ID(order.getAD_Org_ID());
			oline.setAD_OrgTrx_ID(order.getAD_OrgTrx_ID());
			oline.set_ValueNoCheck("JP_ContractLine_ID", m_lines[i].getJP_ContractLine_ID());
			if(m_ContractContent.getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
				oline.set_ValueOfColumn("JP_ContractProcPeriod_ID", getJP_ContractProcPeriod_ID());
			oline.setDatePromised(Timestamp.valueOf(order.getDateAcct().toLocalDateTime().plusDays(m_lines[i].getDeliveryTime_Promised())));
			
			oline.saveEx(get_TrxName());
			isCrateDocLine = true;
		}
		
		
		String docAction = null;
		if(isCrateDocLine)
		{
			if(getDocAction()==null)
				docAction = m_ContractContent.getJP_ContractProcess().getDocAction();
			else
				docAction = getDocAction();
			
			
			order.processIt(docAction);
			if(!docAction.equals(DocAction.ACTION_Complete))
				order.saveEx(get_TrxName());
		}
		
		if(!m_ContractContent.getJP_ContractProcStatus().equals(MContractContent.JP_CONTRACTPROCSTATUS_InProgress))
		{
			m_ContractContent.setJP_ContractProcStatus(MContractContent.JP_CONTRACTPROCSTATUS_InProgress);
			m_ContractContent.saveEx(get_TrxName());
		}
		
		return String.valueOf(order.get_ID());//Return ID;
	}
	
}
