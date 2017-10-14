package jpiere.base.plugin.org.adempiere.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.adempiere.util.IProcessUI;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;

import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractLog;
import jpiere.base.plugin.org.adempiere.model.MContractLogDetail;
import jpiere.base.plugin.org.adempiere.model.MRecognition;
import jpiere.base.plugin.org.adempiere.model.MRecognitionLine;

public class CreateInvoiceFromRecogLump extends SvrProcess {
	
	private Timestamp p_DateInvoiced = null;
	private Timestamp p_DateAcct = null;
	private String p_DocAction = null;
	private int p_AD_Org_ID = 0;
	private int p_JP_ContractCategory_ID = 0;
	private boolean p_IsSOTrx = true;
	
	private boolean p_IsRecordCommitJP = false;
	private String p_JP_ContractProcessTraceLevel = null;
	
	private MContractLog m_ContractLog = null;
	
	private IProcessUI processUI = null;
	
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
				
			}else if (name.equals("AD_Org_ID")) {
				
				p_AD_Org_ID = para[i].getParameterAsInt();
				
			}else if (name.equals("JP_ContractCategory_ID")) {
				
				p_JP_ContractCategory_ID = para[i].getParameterAsInt();
			
			}else if (name.equals("IsSOTrx")) {
				
				p_IsSOTrx = para[i].getParameterAsBoolean();
			
			}else if (name.equals("IsRecordCommitJP")){
				
				p_IsRecordCommitJP = para[i].getParameterAsBoolean();	
			
			}else if (name.equals("JP_ContractProcessTraceLevel")){			
				
				p_JP_ContractProcessTraceLevel = para[i].getParameterAsString();

			}else {
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}
		}//for
		
		m_ContractLog = new MContractLog(getCtx(), 0, get_TrxName());
		m_ContractLog.setAD_PInstance_ID(getAD_PInstance_ID());
		m_ContractLog.setJP_ContractProcessTraceLevel(p_JP_ContractProcessTraceLevel);
		m_ContractLog.saveEx(get_TrxName());
		int JP_ContractLog_ID = m_ContractLog.getJP_ContractLog_ID();
		addBufferLog(0, null, null, Msg.getMsg(getCtx(), "JP_DetailLog")+" -> " + Msg.getElement(getCtx(), "JP_ContractLog_ID"), MContractLog.Table_ID, JP_ContractLog_ID);
		
		processUI = Env.getProcessUI(getCtx());
	}
	
	@Override
	protected String doIt() throws Exception 
	{
	
		StringBuilder returnMsg = new StringBuilder("");
		String processMsg = null;
		try{
			processMsg = createInvoice();
		}catch (Exception e) {
		
			if(!p_IsRecordCommitJP)
				throw e;
			
		}finally{
			;//noting to do;
		}
		
		returnMsg.append(Msg.getMsg(getCtx(), "JP_CreateDocNum")).append(":").append(m_ContractLog.createDocNum).append(" / ");
		returnMsg.append(Msg.getMsg(getCtx(), "JP_ToBeConfirmed")).append(":").append(m_ContractLog.confirmNum).append(" / ");//Number of To Be Confirmed
		returnMsg.append(Msg.getMsg(getCtx(), "JP_NumberOfWarnings")).append(":").append(m_ContractLog.warnNum).append(" / ");//Number of warnings
		returnMsg.append(Msg.getMsg(getCtx(), "JP_NumberOfErrors")).append(":").append(m_ContractLog.errorNum).append("  ");//Number of errors
		
		return processMsg + " ( " + returnMsg.toString() + " ) ";
	}
	
	private String createInvoice() throws Exception 
	{
		StringBuilder getContractContentSQL = new StringBuilder("");
		
		if(p_IsSOTrx)
		{
			getContractContentSQL.append("SELECT DISTINCT o.* FROM C_Order o "
					+ " INNER JOIN C_OrderLine ol ON (o.C_Order_ID = ol.C_Order_ID)"
					+ " INNER JOIN JP_ContractContent cc ON (o.JP_ContractContent_ID = cc.JP_ContractContent_ID)"
					+ " INNER JOIN JP_Contract_Acct ca ON (cc.JP_Contract_Acct_ID = ca.JP_Contract_Acct_ID)"
					+ " INNER JOIN JP_Contract c ON (c.JP_Contract_ID = cc.JP_Contract_ID)"
					+ " WHERE o.AD_Client_ID = ?"	//1
					+ " AND o.DocStatus in('CO','CL')"
					+ " AND ol.QtyDelivered = ol.QtyOrdered "
					+ " AND ol.QtyDelivered = ol.JP_QtyRecognized "
					+ " AND ol.QtyDelivered <> ol.QtyInvoiced "//For Warning
					+ " AND ca.JP_RecogToInvoicePolicy = 'LP' "//Lump
					+ " AND o.IsSOTrx='Y' "
					);
			
			if(p_AD_Org_ID > 0)
				getContractContentSQL.append(" AND o.AD_Org_ID = ? ");
			if(p_JP_ContractCategory_ID > 0)
				getContractContentSQL.append(" AND c.JP_ContractCategory_ID = ?");	
		}
		
		ArrayList<MOrder> orderList = new ArrayList<MOrder>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (getContractContentSQL.toString(), null);
			int i = 1;
			pstmt.setInt (i++, getAD_Client_ID());	//1
			if(p_AD_Org_ID > 0)
				pstmt.setInt (i++, p_AD_Org_ID);	//2		
			if(p_JP_ContractCategory_ID > 0)
				pstmt.setInt (i++, p_JP_ContractCategory_ID);	//3
			
			rs = pstmt.executeQuery ();
			while (rs.next ())
			{
				orderList.add(new MOrder(getCtx(), rs, get_TrxName()));
			}
		}
		catch (Exception e)
		{
			throw new Exception(e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}
		
		/** Create Invoice From Recognition*/
		for(MOrder order : orderList)
		{
			boolean isCreateInvoice = true;
			
			/** Check Order Line */
			MOrderLine[] oLines = order.getLines();
			Object obj_QtyRecognized = null;
			BigDecimal JP_QtyRecognized = Env.ZERO;
			for(int i = 0; i < oLines.length; i++)
			{
				obj_QtyRecognized = oLines[i].get_Value("JP_QtyRecognized");
				if(obj_QtyRecognized != null)
				{
					JP_QtyRecognized = (BigDecimal)obj_QtyRecognized;
				}else{
					
					isCreateInvoice = false;
					String msg = Msg.getMsg(getCtx(), "JP_Null") +" : "+Msg.getMsg(getCtx(), "JP_QtyRecognized");
					createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_UnexpectedError, oLines[i], msg, MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_Error);
					break;
				}
					
				if(oLines[i].getQtyOrdered().compareTo(oLines[i].getQtyDelivered()) == 0
						&& oLines[i].getQtyDelivered().compareTo(JP_QtyRecognized) == 0
						&& oLines[i].getQtyInvoiced().compareTo(Env.ZERO) == 0 )
				{
					;//Noting to do
				}else{

					if(oLines[i].getQtyInvoiced().compareTo(Env.ZERO) != 0)
					{
						//C1 : Could not Create Invoice for invoiced partly.
						//Inspite of Policy of Create Invoice From Recognition is Lump After Order All Recognized,	Order was invoiced partly. Please create Invoice by manually
						createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_CouldNotCreateInvoiceForInvoicedPartly //C1
								,oLines[i], Msg.getMsg(getCtx(), "JP_RecogToInvoicePolicy_Lump_Invoiced"), MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_Warning);
						
					}else if(oLines[i].getQtyOrdered().compareTo(oLines[i].getQtyDelivered()) != 0){
						
						//C2 : Skipped for Qty to deliver
						createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForQtyToDeliver //C2
								,oLines[i], "" , MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_Information);
						
					}else if(oLines[i].getQtyDelivered().compareTo(JP_QtyRecognized) != 0){
						
						//C3 : Skipped for Qty to Recognized
						createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForQtyToRecognized //C3
								,oLines[i], "" , MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_Information);
						
					}else{
						
						createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_UnexpectedError,oLines[i],"", MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_Error);
					}
					isCreateInvoice = false;
					break;
				}
				
				//Initialize
				obj_QtyRecognized = null;
				JP_QtyRecognized = Env.ZERO;
				
			}//for i
			
			
			/** Create Invoice */
			if(isCreateInvoice)
			{
				 MRecognition[] recogs = getRecognitionByOrder(order.getC_Order_ID());
				 
				 //Check Recogniton and Lines
				 for(int i = 0; i < recogs.length; i++)
				 {
					 if(recogs[i].getC_Invoice_ID() > 0)
					 {
						 isCreateInvoice = false;
						//Inspite of Policy of Create Invoice From Recognition is Lump After Order All Recognized,	Order was invoiced partly. Please create Invoice by manually
						createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_CouldNotCreateInvoiceForInvoicedPartly //C1
								,recogs[i], Msg.getMsg(getCtx(), "JP_RecogToInvoicePolicy_Lump_Invoiced"),MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_Warning);
						 break;
					 }
					 
					 MRecognitionLine[] rLines = recogs[i].getLines();
					for (int j = 0; j < rLines.length; j++)
					{
						if(rLines[j].getC_InvoiceLine_ID() > 0)
						{
							 isCreateInvoice = false;
							//Inspite of Policy of Create Invoice From Recognition is Lump After Order All Recognized,	Order was invoiced partly. Please create Invoice by manually
							createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_CouldNotCreateInvoiceForInvoicedPartly //C1
									,rLines[j], Msg.getMsg(getCtx(), "JP_RecogToInvoicePolicy_Lump_Invoiced"),MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_Warning);
							 break;
						}
					}
					
					if(!isCreateInvoice)
						break;
				 }//For
				 
				 //Create Invoice From Recognition
				 if(isCreateInvoice)
				 {
					 int linecounter = 1;
					 MInvoice invoice = null;
					 boolean isCreateHeader = false;
					 for(int i = 0; i < recogs.length; i++)
					 {
						 if(!isCreateHeader)
						 {
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
								 createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SaveError, null, e.getMessage(), MContractLog.JP_CONTRACTPROCESSTRACELEVEL_Error);
								 throw e;
							 }finally {
								 ;
							 }
							isCreateHeader = true;
							createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_CreatedDocument, invoice, null, MContractLog.JP_CONTRACTPROCESSTRACELEVEL_Information);
							m_ContractLog.createDocNum++;
							if(processUI != null)
							{
								processUI.statusUpdate(Msg.getMsg(getCtx(), "JP_CreateDocNum") + " : " + (m_ContractLog.createDocNum));
							}
						 }
						 
						 recogs[i].setC_Invoice_ID(invoice.getC_Invoice_ID());
						 recogs[i].setDateInvoiced(p_DateInvoiced);
						 try{
							 recogs[i].saveEx(get_TrxName());
						 } catch (AdempiereException e) {
							 createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SaveError, recogs[i], e.getMessage(), MContractLog.JP_CONTRACTPROCESSTRACELEVEL_Error);
							 throw e;
						 }finally {
							 ;
						 }
						 
						 MRecognitionLine[] rLines = recogs[i].getLines();
						for (int j = 0; j < rLines.length; j++)
						{
							MInvoiceLine iLine = new MInvoiceLine(getCtx(), 0, get_TrxName());
							PO.copyValues(rLines[j], iLine);
							iLine.setC_Invoice_ID(invoice.getC_Invoice_ID());
							iLine.setC_InvoiceLine_ID(0);
							iLine.setAD_Org_ID(order.getAD_Org_ID());
							iLine.setLine(linecounter*10);
							linecounter++;
							iLine.setM_InOutLine_ID(rLines[j].getM_InOutLine_ID());
							iLine.set_ValueNoCheck("JP_RecognitionLine_ID", rLines[j].getJP_RecognitionLine_ID());
							 try{
								 iLine.saveEx(get_TrxName());
							 } catch (AdempiereException e) {
								 createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SaveError, null, e.getMessage(), MContractLog.JP_CONTRACTPROCESSTRACELEVEL_Error);
								 throw e;
							 }finally {
								 ;
							 }
							
							rLines[j].setC_InvoiceLine_ID(iLine.getC_InvoiceLine_ID());
							 try{
								 rLines[j].saveEx(get_TrxName());
							 } catch (AdempiereException e) {
								 createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SaveError, rLines[j], e.getMessage(), MContractLog.JP_CONTRACTPROCESSTRACELEVEL_Error);
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
							createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_DocumentActionError, invoice, invoice.getProcessMsg(), MContractLog.JP_CONTRACTPROCESSTRACELEVEL_Error);
							throw new AdempiereException(invoice.getProcessMsg());
						}
						 
						 if(!invoice.getDocStatus().equals(DocAction.ACTION_Complete))
						 {
							 try{
								 invoice.saveEx(get_TrxName());
							 } catch (AdempiereException e) {
								 createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SaveError, invoice, e.getMessage(), MContractLog.JP_CONTRACTPROCESSTRACELEVEL_Error);
								 throw e;
							 }finally {
								 ;
							 }
						 }
					 }
					 
					 addBufferLog(0, null, null, invoice.getDocumentNo(), MInvoice.Table_ID, invoice.getC_Invoice_ID());
					 
					 if(p_IsRecordCommitJP)
						 commitEx();
					 
				 }//if(isCreateInvoice)
				 
			}//if(isCreateInvoice)
			
		}//for order
		
		return "@Success@";
	}
	
	private MRecognition[] getRecognitionByOrder(int C_Order_ID)
	{
		StringBuilder whereClauseFinal = new StringBuilder("C_Order_ID=? AND M_RMA_ID is null AND DocStatus in ('CO','CL')");
		String	orderClause = "JP_Recognition_ID";
		//
		List<MRecognition> list = new Query(getCtx(), MRecognition.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(C_Order_ID)
										.setOrderBy(orderClause)
										.list();

		//
		return list.toArray(new MRecognition[list.size()]);
		
	}
	
	private void createContractLogDetail(String ContractLogMsg, PO po, String descriptionMsg, String JP_ContractProcessTraceLevel)
	{
		/** Count */
		if(JP_ContractProcessTraceLevel.equals(MContractLog.JP_CONTRACTPROCESSTRACELEVEL_Information))
		{
			;

		}else if(JP_ContractProcessTraceLevel.equals(MContractLog.JP_CONTRACTPROCESSTRACELEVEL_ToBeConfirmed)){
			
			m_ContractLog.confirmNum++;
			
		}else if(JP_ContractProcessTraceLevel.equals(MContractLog.JP_CONTRACTPROCESSTRACELEVEL_Warning)){
			
			m_ContractLog.warnNum++;	
			
		}else if(JP_ContractProcessTraceLevel.equals(MContractLog.JP_CONTRACTPROCESSTRACELEVEL_Error)){
			
			m_ContractLog.errorNum++;	
			
		}	
		
		
		/** Check traceLevel */
		if(p_JP_ContractProcessTraceLevel.equals(MContractLog.JP_CONTRACTPROCESSTRACELEVEL_Information))
		{
			;//Noting to do. All create contract log.

		}else if(p_JP_ContractProcessTraceLevel.equals(MContractLog.JP_CONTRACTPROCESSTRACELEVEL_ToBeConfirmed)){
			
			if(JP_ContractProcessTraceLevel.equals(MContractLog.JP_CONTRACTPROCESSTRACELEVEL_Information))
				return ;	
			
		}else if(p_JP_ContractProcessTraceLevel.equals(MContractLog.JP_CONTRACTPROCESSTRACELEVEL_Warning)){
			
			if(JP_ContractProcessTraceLevel.equals(MContractLog.JP_CONTRACTPROCESSTRACELEVEL_Information)
					|| JP_ContractProcessTraceLevel.equals(MContractLog.JP_CONTRACTPROCESSTRACELEVEL_ToBeConfirmed))
				return ;	
			
		}else if(p_JP_ContractProcessTraceLevel.equals(MContractLog.JP_CONTRACTPROCESSTRACELEVEL_Error)){
			
			if(JP_ContractProcessTraceLevel.equals(MContractLog.JP_CONTRACTPROCESSTRACELEVEL_Information)
					|| JP_ContractProcessTraceLevel.equals(MContractLog.JP_CONTRACTPROCESSTRACELEVEL_ToBeConfirmed)
					|| JP_ContractProcessTraceLevel.equals(MContractLog.JP_CONTRACTPROCESSTRACELEVEL_Warning))
				return ;	
			
		}else if(p_JP_ContractProcessTraceLevel.equals(MContractLog.JP_CONTRACTPROCESSTRACELEVEL_NoLog)){
			return ;
		}
		
		
		/** Create contract Log Detail */
		MContractLogDetail logDetail = new MContractLogDetail(getCtx(), 0, m_ContractLog.get_TrxName());
		logDetail.setJP_ContractLog_ID(m_ContractLog.getJP_ContractLog_ID());
		logDetail.setJP_ContractLogMsg(ContractLogMsg);
		if(descriptionMsg != null)
			logDetail.setDescription(descriptionMsg);
		logDetail.setJP_ContractProcessTraceLevel(JP_ContractProcessTraceLevel);
		
		//Set Log Detail Info
		logDetail.set_ValueNoCheck("AD_Table_ID", po.get_Table_ID());
		logDetail.set_ValueNoCheck("Record_ID", po.get_ID());
		
		if(po.get_TableName().equals(MOrder.Table_Name))
		{
			MOrder order = (MOrder)po;
			logDetail.setC_Order_ID(order.getC_Order_ID());
			
			MContractContent content = MContractContent.get(getCtx(), order.get_ValueAsInt("JP_ContractContent_ID"));
			logDetail.setJP_Contract_ID(content.getJP_Contract_ID());
			logDetail.setJP_ContractContent_ID(content.getJP_ContractContent_ID());
			
		}else if(po.get_TableName().equals(MOrderLine.Table_Name)){
			
			MOrderLine orderLine = (MOrderLine)po;
			logDetail.setC_Order_ID(orderLine.getC_Order_ID());	
			logDetail.setC_OrderLine_ID(orderLine.getC_OrderLine_ID());	
		
			MContractContent content = MContractContent.get(getCtx(), orderLine.getParent().get_ValueAsInt("JP_ContractContent_ID"));
			logDetail.setJP_Contract_ID(content.getJP_Contract_ID());
			logDetail.setJP_ContractContent_ID(content.getJP_ContractContent_ID());
			
			int JP_ContractLine_ID = orderLine.get_ValueAsInt("JP_ContractLine_ID");
			if(JP_ContractLine_ID > 0)
				logDetail.setJP_ContractLine_ID(JP_ContractLine_ID);
			
		}else if(po.get_TableName().equals(MRecognition.Table_Name)){
			
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
