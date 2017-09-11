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

import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.PO;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;

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
public class DefaultCreateBaseInvoiceFromContract extends SvrProcess {
	
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
		//TODO:既に同じ契約処理期間の売上請求伝票/仕入請求伝票が登録されていない事のチェック。※Abstract化できるね
		
		
		/** Create Invoice header */
		MInvoice invoice = new MInvoice(getCtx(), 0, get_TrxName());
		PO.copyValues(m_ContractContent, invoice);
		invoice.setProcessed(false);
		invoice.setDocStatus(DocAction.STATUS_Drafted);
		invoice.setAD_Org_ID(m_ContractContent.getAD_Org_ID());
		invoice.setAD_OrgTrx_ID(m_ContractContent.getAD_OrgTrx_ID());
		invoice.setDateOrdered(p_DateDoc);
		invoice.setDateAcct(p_DateAcct);
		
		//DatePromised
		LocalDateTime dateAcctLocal = p_DateAcct.toLocalDateTime();
		dateAcctLocal = dateAcctLocal.plusDays(m_ContractContent.getDeliveryTime_Promised());
		
		invoice.setDocumentNo(""); //Reset Document No
		invoice.setC_DocTypeTarget_ID(m_ContractContent.getJP_BaseDocDocType_ID());
		invoice.set_ValueOfColumn("JP_Contract_ID", m_ContractContent.getParent().getJP_Contract_ID());
		invoice.set_ValueOfColumn("JP_ContractContent_ID", m_ContractContent.getJP_ContractContent_ID());
		if(m_ContractContent.getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
		{
			MContractCalender calender = MContractCalender.get(getCtx(), JP_ContractCalender_ID);
			if(JP_ContractProcPeriod_ID == 0)
			{
				MContractProcPeriod period = calender.getContractProcessPeriod(getCtx(), p_DateAcct);
				JP_ContractProcPeriod_ID = period.getJP_ContractProcPeriod_ID();
				invoice.set_ValueOfColumn("JP_ContractProcPeriod_ID", JP_ContractProcPeriod_ID);
			}else{
				invoice.set_ValueOfColumn("JP_ContractProcPeriod_ID", JP_ContractProcPeriod_ID);
			}
		}
		
		invoice.saveEx(get_TrxName());
		
		/** Create Invoice Line */
		MContractLine[] 	m_lines = m_ContractContent.getLines();
		boolean isCrateDocLine = false;
		for(int i = 0; i < m_lines.length; i++)
		{
			if(!m_lines[i].isCreateDocLineJP())
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
			{
				iline.set_ValueOfColumn("JP_ContractProcPeriod_ID", JP_ContractProcPeriod_ID);
			}
			
			iline.saveEx(get_TrxName());
			isCrateDocLine = true;
		}
		
		if(isCrateDocLine)
		{
			if(p_DocAction==null)
				p_DocAction = m_ContractContent.getJP_ContractProcess().getDocAction();
			
			invoice.processIt(p_DocAction);
			if(!p_DocAction.equals(DocAction.ACTION_Complete))
				invoice.saveEx(get_TrxName());
		}
		
		if(!m_ContractContent.getJP_ContractProcStatus().equals(MContractContent.JP_CONTRACTPROCSTATUS_InProgress))
		{
			m_ContractContent.setJP_ContractProcStatus(MContractContent.JP_CONTRACTPROCSTATUS_InProgress);
			m_ContractContent.saveEx(get_TrxName());
		}
		
		//TODO リターン句でIDを返すのはAbstractにしても良いかも
		
		return String.valueOf(invoice.get_ID());
	}
	
}
