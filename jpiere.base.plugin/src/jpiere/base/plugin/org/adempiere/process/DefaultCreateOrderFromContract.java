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
import java.util.logging.Level;

import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.PO;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
import org.compiere.util.Msg;

import jpiere.base.plugin.org.adempiere.model.MContract;
import jpiere.base.plugin.org.adempiere.model.MContractCalender;
import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractLine;
import jpiere.base.plugin.org.adempiere.model.MContractProcPeriod;



/** JPIERE-0363
*
* @author Hideaki Hagiwara
*
*/
public class DefaultCreateOrderFromContract extends SvrProcess {
	
	private int Record_ID = 0;
	private MContractContent m_ContractContent = null;
	
	private Timestamp p_DateDoc = null;
	private Timestamp p_DateAcct = null;
	private int JP_ContractCalender_ID = 0;
	private int JP_ContractProcPeriod_ID = 0;
	private String p_DocAction = null;
	
	@Override
	protected void prepare() 
	{
		Record_ID = getRecord_ID();
		if(Record_ID > 0)
		{
			m_ContractContent = new MContractContent(getCtx(), Record_ID, get_TrxName());
		}else{
			log.log(Level.SEVERE, "Record_ID <= 0 ");
		}
		
		
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null){
				;
			}else if (name.equals("DateDoc")){
				p_DateDoc = para[i].getParameterAsTimestamp();
			}else if(name.equals("DateAcct")){	
				p_DateAcct = para[i].getParameterAsTimestamp();
			}else if (name.equals("JP_ContractCalender_ID")){
				JP_ContractCalender_ID = para[i].getParameterAsInt();
			}else if(name.equals("JP_ContractProcPeriod_ID")){	
				JP_ContractProcPeriod_ID = para[i].getParameterAsInt();
			}else if(name.equals("DocAction")){	
				p_DocAction = para[i].getParameterAsString();
			}else{
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}//if

		}//for
	}
	
	@Override
	protected String doIt() throws Exception 
	{
		if(p_DateAcct==null)
			p_DateAcct = MContractProcPeriod.get(getCtx(), JP_ContractProcPeriod_ID).getDateAcct();
		
		if(p_DateDoc ==null)
			p_DateAcct = MContractProcPeriod.get(getCtx(), JP_ContractProcPeriod_ID).getDateDoc();
		
		
		//Check Overlap
		MOrder order = m_ContractContent.getOrderByContractPeriod(Env.getCtx(), JP_ContractProcPeriod_ID);
		if(order != null)
		{
			String msg = "選択した契約処理期間の伝票は既に作成されています。" 
					 + "  " + Msg.getElement(getCtx(), "JP_ContractProcPeriod_ID") + " : " + MContractProcPeriod.get(getCtx(), JP_ContractProcPeriod_ID).toString()
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
		order.setDateOrdered(p_DateDoc);
		order.setDateAcct(p_DateAcct);
	
		
		//DatePromised
		LocalDateTime dateAcctLocal = p_DateAcct.toLocalDateTime();
		dateAcctLocal = dateAcctLocal.plusDays(m_ContractContent.getDeliveryTime_Promised());
		order.setDatePromised(Timestamp.valueOf(dateAcctLocal));
		
		order.setDocumentNo(""); //Reset Document No
		order.setC_DocTypeTarget_ID(m_ContractContent.getJP_BaseDocDocType_ID());
		order.set_ValueOfColumn("JP_Contract_ID", m_ContractContent.getParent().getJP_Contract_ID());
		order.set_ValueOfColumn("JP_ContractContent_ID", m_ContractContent.getJP_ContractContent_ID());
		if(m_ContractContent.getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
		{
			MContractCalender calender = MContractCalender.get(getCtx(), JP_ContractCalender_ID);
			if(JP_ContractProcPeriod_ID == 0)
			{
				MContractProcPeriod period = calender.getContractProcessPeriod(getCtx(), p_DateAcct);
				JP_ContractProcPeriod_ID = period.getJP_ContractProcPeriod_ID();
				order.set_ValueOfColumn("JP_ContractProcPeriod_ID", JP_ContractProcPeriod_ID);
			}else{
				order.set_ValueOfColumn("JP_ContractProcPeriod_ID", JP_ContractProcPeriod_ID);
			}
		}
		
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
			{
				oline.set_ValueOfColumn("JP_ContractProcPeriod_ID", JP_ContractProcPeriod_ID);
			}
			oline.setDatePromised(Timestamp.valueOf(order.getDateAcct().toLocalDateTime().plusDays(m_lines[i].getDeliveryTime_Promised())));
			
			oline.saveEx(get_TrxName());
			isCrateDocLine = true;
		}
		
		if(isCrateDocLine)
		{
			if(p_DocAction==null)
				p_DocAction = m_ContractContent.getJP_ContractProcess().getDocAction();
			
			order.processIt(p_DocAction);
			if(!p_DocAction.equals(DocAction.ACTION_Complete))
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
