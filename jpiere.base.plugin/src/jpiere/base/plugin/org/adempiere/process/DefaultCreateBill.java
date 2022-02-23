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
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import org.adempiere.util.ProcessUtil;
import org.compiere.model.MColumn;
import org.compiere.model.MInvoice;
import org.compiere.model.MPaymentTerm;
import org.compiere.model.Query;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfo;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;
import org.compiere.wf.MWFProcess;

import jpiere.base.plugin.org.adempiere.model.MBill;
import jpiere.base.plugin.org.adempiere.model.MBillLine;
import jpiere.base.plugin.org.adempiere.model.MBillSchema;

/**
 * JPIERE-0107 Create Bill by Process
 * JPIERE-0275 Create Bill by Manual
 * JPIERE-0276 Create Payment Request by Manual
 * JPIERE-0278 Craate Payment Request by Process
 *
 *  @author Hideaki Hagiwara
 *
 *
 *	modified by Hiroshi Iwama
 */
public class DefaultCreateBill implements I_CreateBill{

	private int 		p_AD_Client_ID = 0;

	/**Target Organization(Option)*/
	private int			p_AD_Org_ID = 0;

	/**Business Partner Group*/
	private int			p_C_BP_Group_ID = 0;

	/**Target DateAcct Date(Option)*/
	private Timestamp	p_DateInvoiced_From = null;

	private Timestamp	p_DateInvoiced_To = null;

	private Timestamp p_JPDateBilled = null;

	private Timestamp	p_JPCutOffDate=null;
	
	MBillSchema m_BillSchema = null;
	
	MPaymentTerm m_paymentTerm = null;
	
	boolean p_IsSOTrx = false;
	String IsSOTrxString = "N";

	/**Target DocStatus(Mandatory)*/
	private String		p_DocAction = "DR";

	/**True if called from info window, and False otherwise.*/
	boolean isCalledFromInfoWindow = false;

	ArrayList<MInvoice> invoiceList = new ArrayList<MInvoice>();


	
	SvrProcess process = null;
	Properties ctx = null;
	int p_AD_PInstance_ID = 0;
	String trxName = null;
	
	int p_Bill_WorkFlow_ID = 0;
	
	@Override
	public String createBills(Properties ctx,int AD_PInstance_ID , SvrProcess process, ProcessInfoParameter[] para
			, MBillSchema billSchema, MPaymentTerm paymentTerm, boolean isSOTrx,  boolean isCalledInfoWindow, String trxName) throws Exception
	{
		//get Parameters.
		p_AD_Client_ID = process.getProcessInfo().getAD_Client_ID();
		this.ctx = ctx;
		p_AD_PInstance_ID = AD_PInstance_ID;
		this.process = process;
		this.m_BillSchema = billSchema;
		this.m_paymentTerm = paymentTerm;
		this.p_IsSOTrx = isSOTrx;
		this.isCalledFromInfoWindow = isCalledInfoWindow;
		this.trxName = trxName;
		//Convert to Y/N
		if(p_IsSOTrx){
			IsSOTrxString = "'Y'";
		}else{
			IsSOTrxString = "'N'";
		}
		
		MColumn m_Column_BillDocAction = MColumn.get(ctx, MBill.Table_Name, MBill.COLUMNNAME_DocAction);
		p_Bill_WorkFlow_ID = m_Column_BillDocAction.getAD_Process().getAD_Workflow_ID();
		
		if(isCalledInfoWindow)
		{
			for(int i = 0; i < para.length; i++)
			{
				String name = para[i].getParameterName();
				if(name.equals("JPDateBilled")){
					p_JPDateBilled = (Timestamp)para[i].getParameter();
				}else if(name.equals("DocAction")){
					p_DocAction = para[i].getParameterAsString();
				}else if(name.equals("JPCutOffDate")){
					p_JPCutOffDate = (Timestamp)para[i].getParameter();
				}
			}
			
			String whereClause = "EXISTS (SELECT T_Selection_ID FROM T_Selection WHERE T_Selection.AD_PInstance_ID=? " +
					"AND T_Selection.T_Selection_ID = C_Invoice.C_Invoice_ID)";

			List<MInvoice> selectedInvoices = new Query(ctx, "C_Invoice", whereClause, trxName)
							.setClient_ID()
							.setParameters(new Object[]{AD_PInstance_ID})
							.setOrderBy(createOrderByClause())
							.list();
			
			invoiceList = (ArrayList<MInvoice>) selectedInvoices;
			
			return createBills(invoiceList);
				
		}else{
			
			for(int i = 0; i < para.length; i++)
			{
				String name = para[i].getParameterName();
				if(para[i].getParameter() == null){
					;
				}else if(name.equals("AD_Org_ID")){
					p_AD_Org_ID = para[i].getParameterAsInt();
				}else if(name.equals("C_BP_Group_ID")){
					p_C_BP_Group_ID = para[i].getParameterAsInt();
				}else if(name.equals("DateInvoiced")){
					p_DateInvoiced_From = (Timestamp)para[i].getParameter();
					p_DateInvoiced_To = (Timestamp)para[i].getParameter_To();
					if(p_DateInvoiced_To != null){
						Calendar cal = Calendar.getInstance();
						cal.setTimeInMillis(p_DateInvoiced_To.getTime());
						cal.add(Calendar.DAY_OF_MONTH, 1);
						p_DateInvoiced_To = new Timestamp(cal.getTimeInMillis());
					}
				}else if(name.equals("JPCutOffDate")){
					p_JPCutOffDate = (Timestamp)para[i].getParameter();				
				}else if(name.equals("JPDateBilled")){
					p_JPDateBilled = (Timestamp)para[i].getParameter();
				}else if(name.equals("DocAction")){
					p_DocAction = para[i].getParameterAsString();
				}
			}
	
			//create a list of parameters-matched invoies.
			String selectSQL = new String("SELECT C_Invoice.* FROM C_Invoice INNER JOIN C_BPartner ON (C_Invoice.C_BPartner_ID = C_BPartner.C_BPartner_ID) ");
			String sql = selectSQL + createWhereClause() + createOrderByClause();
	
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try{
				pstmt = DB.prepareStatement(sql, trxName);
				int i = 0;
				if (p_AD_Org_ID != 0)
					pstmt.setInt(++i, p_AD_Org_ID);
				if(p_C_BP_Group_ID != 0)
					pstmt.setInt(++i, p_C_BP_Group_ID);
				pstmt.setTimestamp(++i,  p_DateInvoiced_From);
				pstmt.setTimestamp(++i,  p_DateInvoiced_To);
				pstmt.setInt(++i, m_paymentTerm.getC_PaymentTerm_ID());
				pstmt.setInt(++i, m_BillSchema.getJP_BillSchema_ID());
				rs = pstmt.executeQuery();
				while(rs.next()){
					invoiceList.add(new MInvoice(ctx, rs, trxName));
				}
			}catch (Exception e) {
				process.addLog(e.toString());
			}finally {
				DB.close(rs, pstmt);
				rs = null; pstmt = null;
			}
	
			return createBills(invoiceList);
		}
	}

	private String createWhereClause()
	{
		StringBuilder whereClause = new StringBuilder(" WHERE C_Invoice.AD_Client_ID ="+ p_AD_Client_ID
											+ " AND C_Invoice.Processed  = 'Y'"
											+ " AND C_Invoice.IsPaid  = 'N'"
											+ " AND C_Invoice.IsSOTrx  = " + IsSOTrxString
											+ " AND C_Invoice.JP_Bill_ID is null"//
											);
		//Option : AD_Org
		if (p_AD_Org_ID != 0)
		{
			whereClause.append(" AND C_Invoice.AD_Org_ID = ? ");
		}

		//Option : C_BP_Group
		if(p_C_BP_Group_ID != 0)
		{
			whereClause.append(" AND C_BPartner.C_BP_Group_ID = ? ");
		}

		//Mandatory : DateInvoiced
		whereClause.append(" AND C_Invoice.DateInvoiced >= ?");
		whereClause.append(" AND C_Invoice.DateInvoiced < ?");
		
		//Mandatory : C_PaymentTerm_ID
		whereClause.append(" AND C_Invoice.C_PaymentTerm_ID = ? ");
		
		//Mandatory : JP_BillSchema_ID
		if(p_IsSOTrx)
			whereClause.append(" AND C_BPartner.JP_BillSchema_ID = ?  ");
		else
			whereClause.append(" AND C_BPartner.JP_BillSchemaPO_ID = ?  ");
		
		return whereClause.toString();
	}

	private String createOrderByClause()
	{
		StringBuilder orderByClause = new StringBuilder("ORDER BY C_Invoice.C_BPartner_ID, C_Invoice.C_Currency_ID "
				+ ",C_Invoice.PaymentRule"
				+ ",C_Invoice.C_BPartner_Location_ID"
				+ ",C_Invoice.AD_User_ID"
				+ ",C_Invoice.AD_Org_ID"
				+ ",C_Invoice.DocumentNo"
				);

		return orderByClause.toString();
	}


	private String createBills(ArrayList<MInvoice> invoiceList) throws Exception
	{
		MBill 	 bill = null;
		MInvoice oldInvoice = null;

		int lineCounter = 0;
		int billCounter = 0;
		for(MInvoice invoice :invoiceList)
		{
			if(invoice.get_ValueAsInt("JP_Bill_ID") !=0)
				continue;
			
			
			if(oldInvoice == null)
			{
				oldInvoice = invoice;
				bill = createBillHeader(invoice,m_BillSchema);
				lineCounter = 0;
				billCounter++;
			}
			
			//
			if(oldInvoice.getC_BPartner_ID() != invoice.getC_BPartner_ID() )
			{
				billDocActionProcess(bill);

				bill = createBillHeader(invoice,m_BillSchema);
				lineCounter = 0;
				billCounter++;
			}
			else if(oldInvoice.getC_Currency_ID() != invoice.getC_Currency_ID())
			{
				billDocActionProcess(bill);

				bill = createBillHeader(invoice,m_BillSchema);
				lineCounter = 0;
				billCounter++;
			}
			else if( !oldInvoice.getPaymentRule().equals(invoice.getPaymentRule()) )
			{
				billDocActionProcess(bill);

				bill = createBillHeader(invoice,m_BillSchema);
				lineCounter = 0;
				billCounter++;
			}
			else if(oldInvoice.getC_BPartner_Location_ID() != invoice.getC_BPartner_Location_ID())
			{
				billDocActionProcess(bill);
				
				bill = createBillHeader(invoice,m_BillSchema);
				lineCounter = 0;
				billCounter++;
			}
			else if(oldInvoice.getAD_User_ID() != invoice.getAD_User_ID())
			{
				billDocActionProcess(bill);

				bill = createBillHeader(invoice,m_BillSchema);
				lineCounter = 0;
				billCounter++;
			}
			else if(m_BillSchema.isBillOrgJP())
			{
				if(oldInvoice.getAD_Org_ID() != invoice.getAD_Org_ID())
				{
					billDocActionProcess(bill);

					bill = createBillHeader(invoice,m_BillSchema);
					lineCounter = 0;
					billCounter++;
				}
			}

			MBillLine bLine = new MBillLine(ctx, 0, trxName);
			bLine.setJP_Bill_ID(bill.getJP_Bill_ID());
			lineCounter++;
			bLine.setLine(lineCounter*10);
			bLine.setC_Invoice_ID(invoice.getC_Invoice_ID());
			bLine.setAD_Org_ID(bill.getAD_Org_ID());
			bLine.saveEx(trxName);

			oldInvoice = invoice;

		}//for

		if(bill != null)
		{
			billDocActionProcess(bill);
		}
		
		return Msg.getElement(ctx, "JP_Bill_ID",p_IsSOTrx) + " #" + billCounter;
	}

	private MBill createBillHeader(MInvoice invoice,MBillSchema billSchema)
	{

		//Get duedate from paymentterm and cutoffdate.
		String sql = new String("select jp_paymenttermduedate(?,?) from Dual");
		Timestamp dueDate = DB.getSQLValueTS(trxName, sql, m_paymentTerm.getC_PaymentTerm_ID() ,p_JPCutOffDate);

		MBill bill = new MBill(ctx, 0, trxName);

		if(billSchema.isBillOrgJP())
		{
			bill.setAD_Org_ID(invoice.getAD_Org_ID());
		}else{
			bill.setAD_Org_ID(billSchema.getJP_BillOrg_ID());
		}
		bill.setAD_OrgTrx_ID(invoice.getAD_OrgTrx_ID());
		bill.setJPDateBilled(p_JPDateBilled);
		bill.setJPCutOffDate(p_JPCutOffDate);
		bill.setDateAcct(p_JPCutOffDate);
		bill.setSalesRep_ID(Env.getAD_User_ID(ctx));
		bill.setC_DocType_ID(billSchema.getC_DocType_ID());
		bill.setC_BPartner_ID(invoice.getC_BPartner_ID());
		bill.setC_BPartner_Location_ID(invoice.getC_BPartner_Location_ID());
		bill.setAD_User_ID(invoice.getAD_User_ID());
		bill.setPaymentRule(invoice.getPaymentRule());
		bill.setC_PaymentTerm_ID(invoice.getC_PaymentTerm_ID());
		bill.setC_Currency_ID(invoice.getC_Currency_ID());
		
		if(billSchema.getC_BankAccount_ID() > 0)
			bill.setC_BankAccount_ID(billSchema.getC_BankAccount_ID());
		bill.setJP_PromisedPayDate(dueDate);
		bill.setIsSOTrx(p_IsSOTrx);
		
		bill.setDocStatus(DocAction.STATUS_Drafted);
		bill.setDocAction(p_DocAction);
		bill.saveEx(trxName);

		return bill;
	}

	private boolean billDocActionProcess(MBill bill) throws Exception
	{
		bill.load(trxName);
		
		if(Util.isEmpty(p_DocAction))
		{
			process.addBufferLog(0, null, null, bill.getDocumentNo()+":"+ bill.getC_BPartner().getName(), bill.get_Table_ID(), bill.getJP_Bill_ID());
			return true;
		}
		
		if(process == null || p_Bill_WorkFlow_ID == 0)
		{
			if(!bill.processIt(p_DocAction))
			{
				throw new Exception(bill.getProcessMsg());
			}
			
		}else {
			
			ProcessInfo pInfo = process.getProcessInfo();
			pInfo.setPO(bill);
			pInfo.setRecord_ID(bill.getJP_Bill_ID());
			pInfo.setTable_ID(MBill.Table_ID);	
			MWFProcess wfProcess = ProcessUtil.startWorkFlow(Env.getCtx(), pInfo, p_Bill_WorkFlow_ID);
			if(wfProcess.getWFState().equals(MWFProcess.WFSTATE_Terminated))
			{
				throw new Exception(bill.getProcessMsg());
			}
		}
		
		bill.saveEx(trxName);
		process.addBufferLog(0, null, null, bill.getDocumentNo()+" : "+ bill.getC_BPartner().getName(), bill.get_Table_ID(), bill.getJP_Bill_ID());
		
		return true;
	}


}
