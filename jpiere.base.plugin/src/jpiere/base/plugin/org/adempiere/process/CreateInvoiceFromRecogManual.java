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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MOrder;
import org.compiere.model.MRefList;
import org.compiere.model.PO;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;

import jpiere.base.plugin.org.adempiere.model.MContractAcct;
import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractLog;
import jpiere.base.plugin.org.adempiere.model.MContractLogDetail;
import jpiere.base.plugin.org.adempiere.model.MRecognition;
import jpiere.base.plugin.org.adempiere.model.MRecognitionLine;


/**
 * JPIERE-0365 Create Invoice From Recognition
 * 
 * 
 * @author h.hagiwara
 *
 */
public class CreateInvoiceFromRecogManual extends SvrProcess {
	
	private Timestamp p_DateInvoiced = null;
	private Timestamp p_DateAcct = null;
	private String p_DocAction = null;
	
	private MContractLog m_ContractLog = null;
	
	@Override
	protected void prepare() 
	{
		ProcessInfoParameter[] para = getParameter();
		for(int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if(para[i].getParameter() == null){
				;
			}else if (name.equals("DateInvoiced")){
				
				p_DateInvoiced = para[i].getParameterAsTimestamp();
				
			}else if (name.equals("DateAcct")){
				
				p_DateAcct = para[i].getParameterAsTimestamp();
				
			}else if (name.equals("DocAction")){
				
				p_DocAction = para[i].getParameterAsString();
				
			}else {
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}
		}//for
		
		m_ContractLog = new MContractLog(getCtx(), 0, get_TrxName());
		m_ContractLog.setAD_PInstance_ID(getAD_PInstance_ID());
		m_ContractLog.saveEx(get_TrxName());
		int JP_ContractLog_ID = m_ContractLog.getJP_ContractLog_ID();
		addBufferLog(0, null, null, Msg.getMsg(getCtx(), "JP_DetailLog")+" -> " + Msg.getElement(getCtx(), "JP_ContractLog_ID"), MContractLog.Table_ID, JP_ContractLog_ID);
	}
	
	@Override
	protected String doIt() throws Exception 
	{
		MRecognition[] recogs = getRocognitions();
		MInvoice invoice = null;
		int linecounter = 1;
		for(int i = 0; i < recogs.length; i++)
		{
			if(i == 0)
			{
				MContractContent content =  MContractContent.get(getCtx(), recogs[i].getJP_ContractContent_ID());
				MContractAcct acct = MContractAcct.get(getCtx(), content.getJP_Contract_Acct_ID());
				if(acct.getJP_RecogToInvoicePolicy() == null ||
						acct.getJP_RecogToInvoicePolicy().equals(MContractAcct.JP_RECOGTOINVOICEPOLICY_NotCreateInvoiceFromRecognition))
				{
					String JP_RecogToInvoicePolic = null;
					if(acct.getJP_RecogToInvoicePolicy().equals(MContractAcct.JP_RECOGTOINVOICEPOLICY_NotCreateInvoiceFromRecognition))
						JP_RecogToInvoicePolic = MRefList.getListDescription(getCtx(), "JP_RecogToInvoicePolicy", acct.getJP_RecogToInvoicePolicy());
					else if (acct.getJP_RecogToInvoicePolicy() == null)
						JP_RecogToInvoicePolic = Msg.getMsg(getCtx(),"JP_Null");
					
					String msg = Msg.getMsg(getCtx(), "JP_CouldNotCreate") + "   " + Msg.getElement(getCtx(), "JP_RecogToInvoicePolicy")  + " : " + JP_RecogToInvoicePolic;
					createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_Warning, recogs[i], msg);
					break;
				}
			}//if(i == 0)
			
			//Check Recog Doc Status
			if(!recogs[i].getDocStatus().equals(DocAction.ACTION_Complete)
					&& !recogs[i].getDocStatus().equals(DocAction.ACTION_Close))
			{
				//Skip for Invalid Doc Status
				createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_Skipped, recogs[i], Msg.getMsg(getCtx(), "Invalid") + " : " + Msg.getElement(getCtx(), "DocStatus"));
				continue;
			}
			
			//Check RMA
			if(recogs[i].getM_RMA_ID() > 0)
			{
				//Skip for RMA
				createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_Skipped, recogs[i], Msg.getElement(getCtx(), "M_RMA_ID", recogs[i].isSOTrx()));
				continue;
			}
			
			
			MRecognitionLine[] rLines =  recogs[i].getLines();
			for(int j = 0; j < rLines.length; j++)
			{
				//Check Invoiced
				if(rLines[j].getC_InvoiceLine_ID() > 0)
				{
					//Skip for Invoice
					MInvoiceLine iLine = new MInvoiceLine(getCtx(),rLines[j].getC_InvoiceLine_ID() ,get_TrxName());
					String msg = Msg.getElement(getCtx(), "C_Invoice_ID", recogs[i].isSOTrx()) + " : " + iLine.getParent().getDocumentNo()
									+ " - " + Msg.getElement(getCtx(), "Line") + " : " + iLine.getLine();
					createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_Skipped, rLines[j], msg);
					continue;
				}
				
				if(invoice == null)
				{
					int C_Order_ID = recogs[i].getC_Order_ID();
					MOrder order = new MOrder(getCtx(), C_Order_ID, get_TrxName());
					invoice = new MInvoice (order, order.getC_DocTypeTarget().getC_DocTypeInvoice_ID(), p_DateInvoiced);
					invoice.setDateAcct(p_DateAcct);
					invoice.setDocumentNo(null);
					invoice.setTotalLines(Env.ZERO);
					invoice.setGrandTotal(Env.ZERO);
					invoice.setDocStatus(DocAction.STATUS_Drafted);
					invoice.setDocAction(DocAction.ACTION_Complete);
					 try{
						 invoice.saveEx(get_TrxName());
					 } catch (AdempiereException e) {
						 createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SaveError, null, e.getMessage());
						 throw e;
					 }finally {
						 ;
					 }
					 
					createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_CreatedDocument, invoice, null);
					addBufferLog(0, null, null, invoice.getDocumentNo(), MInvoice.Table_ID, invoice.getC_Invoice_ID());
				}
				
				MInvoiceLine iLine = new MInvoiceLine(getCtx(), 0, get_TrxName());
				PO.copyValues(rLines[j], iLine);
				iLine.setC_Invoice_ID(invoice.getC_Invoice_ID());
				iLine.setC_InvoiceLine_ID(0);
				iLine.setAD_Org_ID(invoice.getAD_Org_ID());
				iLine.setLine(linecounter*10);
				linecounter++;
				iLine.setM_InOutLine_ID(rLines[j].getM_InOutLine_ID());
				iLine.set_ValueNoCheck("JP_RecognitionLine_ID", rLines[j].getJP_RecognitionLine_ID());
				 try{
					 iLine.saveEx(get_TrxName());
				 } catch (AdempiereException e) {
					 createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SaveError, null, e.getMessage());
					 throw e;
				 }finally {
					 ;
				 }
				
				rLines[j].setC_InvoiceLine_ID(iLine.getC_InvoiceLine_ID());
				 try{
					 rLines[j].saveEx(get_TrxName());
				 } catch (AdempiereException e) {
					 createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SaveError, rLines[j], e.getMessage());
					 throw e;
				 }finally {
					 ;
				 }
				
			}//for j
			
		}//for i
		
		 if(invoice != null && p_DocAction != null)
		 {
			 
			if(!invoice.processIt(p_DocAction))
			{
				createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_DocumentActionError, invoice, invoice.getProcessMsg());
				throw new AdempiereException(invoice.getProcessMsg());
			}
			 
			 if(!invoice.getDocStatus().equals(DocAction.ACTION_Complete))
			 {
				 try{
					 invoice.saveEx(get_TrxName());
				 } catch (AdempiereException e) {
					 createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SaveError, invoice, e.getMessage());
					 throw e;
				 }finally {
					 ;
				 }
			 }
		 }
		 
		return null;
	}
	
	private MRecognition[] getRocognitions()
	{
		MRecognition[] recogs = null;
		ArrayList<MRecognition> list = new ArrayList<MRecognition>();
		String sql = " SELECT r.* FROM T_Selection t INNER JOIN JP_Recognition r ON (t.T_Selection_ID = r.JP_Recognition_ID) WHERE t.AD_PInstance_ID=? ORDER BY r.DateAcct ASC, r.JP_Recognition_ID ASC";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, getAD_PInstance_ID());
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add(new MRecognition (getCtx(), rs, get_TrxName()));
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

		recogs = new MRecognition[list.size()];
		list.toArray(recogs);
		
		return recogs;
	}
	
	private void createContractLogDetail(String ContractLogMsg, PO po, String descriptionMsg)
	{
		
		/** Create contract Log Detail */
		MContractLogDetail logDetail = new MContractLogDetail(getCtx(), 0, m_ContractLog.get_TrxName());
		logDetail.setJP_ContractLog_ID(m_ContractLog.getJP_ContractLog_ID());
		logDetail.setJP_ContractLogMsg(ContractLogMsg);
		if(descriptionMsg != null)
			logDetail.setDescription(descriptionMsg);
		
		//Set Log Detail Info
		logDetail.set_ValueNoCheck("AD_Table_ID", po.get_Table_ID());
		logDetail.set_ValueNoCheck("Record_ID", po.get_ID());
		
		if(po.get_TableName().equals(MRecognition.Table_Name)){
			
			MRecognition recog = (MRecognition)po;
			logDetail.setC_Order_ID(recog.getC_Order_ID());	
			logDetail.setM_InOut_ID(recog.getM_InOut_ID());	
			logDetail.setJP_Recognition_ID(recog.getJP_Recognition_ID());	
			logDetail.setC_Invoice_ID(recog.getC_Invoice_ID());
			
			MContractContent content = MContractContent.get(getCtx(), recog.getJP_ContractContent_ID());
			logDetail.setJP_Contract_ID(content.getJP_Contract_ID());
			logDetail.setJP_ContractContent_ID(content.getJP_ContractContent_ID());
			
		}else if(po.get_TableName().equals(MRecognitionLine.Table_Name)){
			
			MRecognitionLine rLine = (MRecognitionLine)po;
			logDetail.setC_OrderLine_ID(rLine.getC_OrderLine_ID());		
			logDetail.setM_InOutLine_ID(rLine.getM_InOutLine_ID());
			logDetail.setJP_Recognition_ID(rLine.getJP_Recognition_ID());
			logDetail.setJP_RecognitionLine_ID(rLine.getJP_RecognitionLine_ID());
			logDetail.setC_InvoiceLine_ID(rLine.getC_InvoiceLine_ID());
			
			MContractContent content = MContractContent.get(getCtx(), rLine.getParent().getJP_ContractContent_ID());
			logDetail.setJP_Contract_ID(content.getJP_Contract_ID());
			logDetail.setJP_ContractContent_ID(content.getJP_ContractContent_ID());
			
			int JP_ContractLine_ID = rLine.getJP_ContractLine_ID();
			if(JP_ContractLine_ID > 0)
				logDetail.setJP_ContractLine_ID(JP_ContractLine_ID);
			
		}else if(po.get_TableName().equals(MInvoice.Table_Name)){
			
			MInvoice invoice = (MInvoice)po;
			logDetail.setC_Order_ID(invoice.getC_Order_ID());	
			logDetail.setC_Invoice_ID(invoice.getC_Invoice_ID());	
			
			MContractContent content = MContractContent.get(getCtx(), invoice.get_ValueAsInt("JP_ContractContent_ID"));
			logDetail.setJP_Contract_ID(content.getJP_Contract_ID());
			logDetail.setJP_ContractContent_ID(content.getJP_ContractContent_ID());
		}
		
		logDetail.saveEx(get_TrxName());
		
	}
}
