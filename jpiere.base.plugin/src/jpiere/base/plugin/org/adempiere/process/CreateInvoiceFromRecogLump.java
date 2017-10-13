package jpiere.base.plugin.org.adempiere.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.I_C_OrderLine;
import org.compiere.model.MDocType;
import org.compiere.model.MInOut;
import org.compiere.model.MInOutLine;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MInvoicePaySchedule;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MOrderPaySchedule;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MRecognition;
import jpiere.base.plugin.org.adempiere.model.MRecognitionLine;

public class CreateInvoiceFromRecogLump extends SvrProcess {
	
	private Timestamp p_DateInvoiced = null;
	private Timestamp p_DateAcct = null;
	private String p_DocAction = null;
	private int p_AD_Org_ID = 0;
	private int p_JP_ContractCategory_ID = 0;
	private boolean p_IsSOTrx = true;
	
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

			}else {
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}
		}
	}
	
	@Override
	protected String doIt() throws Exception 
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
					+ " AND ol.QtyOrdered = ol.QtyDelivered "
					+ " AND ol.JP_QtyRecognized = ol.QtyDelivered "
					+ " AND ol.QtyInvoiced = 0 "
					+ " AND ca.JP_RecogToInvoicePolicy = 'LP' "
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
		
		for(MOrder order : orderList)
		{
			
			//Check to Create Invoice
			MOrderLine[] oLines = order.getLines();
			Object obj_QtyRecognized = null;
			BigDecimal JP_QtyRecognized = Env.ZERO;
			boolean isCreateInvoice = true;
			for(int i = 0; i < oLines.length; i++)
			{
				obj_QtyRecognized = oLines[i].get_Value("JP_QtyRecognized");
				if(obj_QtyRecognized != null)
				{
					JP_QtyRecognized = (BigDecimal)obj_QtyRecognized;
				}else{
					isCreateInvoice = false;
					break;
				}
					
				if(oLines[i].getQtyOrdered().compareTo(oLines[i].getQtyDelivered()) == 0
						&& oLines[i].getQtyDelivered().compareTo(JP_QtyRecognized) == 0
						&& oLines[i].getQtyInvoiced().compareTo(Env.ZERO) == 0	 )
				{
					;//Noting to do
				}else{

					isCreateInvoice = false;
					break;
				}
				
				//Initialize
				obj_QtyRecognized = null;
				JP_QtyRecognized = Env.ZERO;
				
			}//for i
			
			//Create Invoice From Recognition
			if(isCreateInvoice)
			{
				 MRecognition[] recogs = getRecognitionByOrder(order.getC_Order_ID());
				 int linecounter = 1;
				 MInvoice invoice = null;
				 for(int i = 0; i < recogs.length; i++)
				 {
					 if(i == 0)
					 {
						invoice = new MInvoice (order, order.getC_DocTypeTarget().getC_DocTypeInvoice_ID(), p_DateInvoiced);
						invoice.setDocumentNo(null);
						invoice.setTotalLines(Env.ZERO);
						invoice.setGrandTotal(Env.ZERO);
						invoice.setDocStatus(DocAction.STATUS_Drafted);
						invoice.setDocAction(DocAction.ACTION_Complete);
						invoice.saveEx(get_TrxName());
					 }
					 
					 recogs[i].setC_Invoice_ID(invoice.getC_Invoice_ID());
					 recogs[i].saveEx(get_TrxName());
					 
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
						iLine.saveEx(get_TrxName());
						
						rLines[j].setC_InvoiceLine_ID(iLine.getC_InvoiceLine_ID());
						rLines[j].saveEx(get_TrxName());

					}//For j
					
				 }//For i
				 
				 if(invoice != null && p_DocAction != null)
				 {
					 invoice.processIt(p_DocAction);
					 if(!invoice.getDocStatus().equals(DocAction.ACTION_Complete))
					 {
						 invoice.saveEx(get_TrxName());
					 }
				 }
				 
			}//if(isCreateInvoice)
			
		}//for order
		
		return null;
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
	
}
