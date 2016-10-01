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
import java.util.logging.Level;

import jpiere.base.plugin.org.adempiere.model.MBill;
import jpiere.base.plugin.org.adempiere.model.MBillLine;
import jpiere.base.plugin.org.adempiere.model.MBillSchema;

import org.compiere.model.MBPartner;
import org.compiere.model.MInvoice;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;



/**
 * JPIERE-0107 Crate Bill
 *
 *
 *  @author Hideaki Hagiwara
 *
 */
public class CreateBill extends SvrProcess {

	private int 		p_AD_Client_ID = 0;

	/**Target Organization(Option)*/
	private int			p_AD_Org_ID = 0;

	/**Business Partner Group*/
	private int			p_C_BP_Group_ID = 0;

	/**Target DateAcct Date(Option)*/
	private Timestamp	p_DateInvoiced_From = null;
	private Timestamp	p_DateInvoiced_To = null;

	private Timestamp	p_JPCutOffDate=null;

	/**Payment Term*/
	private int			p_C_PaymentTerm_ID = 0;

	/**Target DocStatus(Mandatory)*/
	private String		p_DocAction = "DR";


	/**
	 *  Prepare - get Parameters.
	 */
	protected void prepare()
	{
		p_AD_Client_ID =getProcessInfo().getAD_Client_ID();
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null){
				;
			}else if (name.equals("AD_Org_ID")){
				p_AD_Org_ID = para[i].getParameterAsInt();
			}else if (name.equals("C_BP_Group_ID")){
				p_C_BP_Group_ID = para[i].getParameterAsInt();
			}else if (name.equals("DateInvoiced")){
				p_DateInvoiced_From = (Timestamp)para[i].getParameter();
				p_DateInvoiced_To = (Timestamp)para[i].getParameter_To();
				if(p_DateInvoiced_To!=null)
				{
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(p_DateInvoiced_To.getTime());
					cal.add(Calendar.DAY_OF_MONTH, 1);
					p_DateInvoiced_To = new Timestamp(cal.getTimeInMillis());
				}
			}else if (name.equals("JPCutOffDate")){
				p_JPCutOffDate = (Timestamp)para[i].getParameter();
			}else if (name.equals("C_PaymentTerm_ID")){
				p_C_PaymentTerm_ID = para[i].getParameterAsInt();
			}else if (name.equals("DocAction")){
				p_DocAction = para[i].getParameterAsString();

			}else{
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}//if
		}//for
	}//	prepare

	/**
	 *  Perform process.
	 *  @return Message (variables are parsed)
	 *  @throws Exception if not successful
	 */
	protected String doIt() throws Exception
	{
		MBPartner[] bpartners = getBPartners();
		int billCounter = 0;

		StringBuilder sql = null;
		String selectSQL = new String("SELECT C_Invoice.* FROM C_Invoice"
				+ " INNER JOIN C_BPartner ON (C_Invoice.C_BPartner_ID = C_BPartner.C_BPartner_ID) ");

		String selectWhereSQL = selectSQL + createWhereClause() + " AND C_Invoice.C_BPartner_ID=?";

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		for(int i = 0; i < bpartners.length; i++)
		{

			sql = null;
			Object JP_BillSchema_ID = bpartners[i].get_Value("JP_BillSchema_ID");
			if(JP_BillSchema_ID == null)
				continue;

			try
			{
				String orderByClause = createOrderByClause(bpartners[i]);
				if(orderByClause != null)
					sql = new StringBuilder(selectWhereSQL+orderByClause);
				else
					sql = new StringBuilder(selectWhereSQL);

				pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
				ArrayList<MInvoice> invoiceList = new ArrayList<MInvoice>();
				pstmt.setInt(1, bpartners[i].getC_BPartner_ID());
				rs = pstmt.executeQuery();
				while (rs.next())
				{
					invoiceList.add(new MInvoice (getCtx(), rs, get_TrxName()));
				}

				billCounter = billCounter + createBill(bpartners[i], invoiceList);
				commitEx();
			}
			catch (Exception e)
			{
				log.log(Level.SEVERE, sql.toString(), e);
			}
			finally
			{
				DB.close(rs, pstmt);
				rs = null; pstmt = null;
			}

		}

		return Msg.getElement(getCtx(), "C_BPartner_ID")+" : " + bpartners.length +"   "+ Msg.getElement(getCtx(), "JP_Bill_ID") +" : " +  billCounter;
	}	//	doIt


	private MBPartner[] getBPartners()
	{
		StringBuilder getBPartnerSQL = new StringBuilder("SELECT DISTINCT C_BPartner.* FROM C_Invoice"
															+ " INNER JOIN C_BPartner ON (C_Invoice.C_BPartner_ID = C_BPartner.C_BPartner_ID) ");
		getBPartnerSQL.append(createWhereClause());

		ArrayList<MBPartner> list = new ArrayList<MBPartner>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(getBPartnerSQL.toString(), get_TrxName());
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add(new MBPartner(getCtx(),rs,get_TrxName()));
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, getBPartnerSQL.toString(), e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}

		return list.toArray(new MBPartner[list.size()]);
	}


	private String createWhereClause()
	{
		StringBuilder whereClause = new StringBuilder(" WHERE C_Invoice.AD_Client_ID ="+ p_AD_Client_ID
											+ " AND C_Invoice.Processed  = 'Y'"
											+ " AND C_Invoice.IsPaid  = 'N'"
											+ " AND C_Invoice.IsSOTrx  = 'Y'"
											+ " AND C_Invoice.C_PaymentTerm_ID ="+ p_C_PaymentTerm_ID
											);
		if (p_AD_Org_ID != 0)
		{
			whereClause.append(" AND C_Invoice.AD_Org_ID =" + p_AD_Org_ID);
		}

		if(p_C_BP_Group_ID != 0)
		{
			whereClause.append(" AND C_BPartner.C_BP_Group_ID =" + p_C_BP_Group_ID);
		}

		if(p_DateInvoiced_From != null)
		{
			whereClause.append(" AND C_Invoice.DateInvoiced >= '" + p_DateInvoiced_From + "'");
		}

		if(p_DateInvoiced_To != null)
		{
			whereClause.append(" AND C_Invoice.DateInvoiced < '" + p_DateInvoiced_To + "'");
		}


		return whereClause.toString();
	}

	private String createOrderByClause(MBPartner bpartner)
	{
		StringBuilder whereClause = new StringBuilder(" ORDER BY C_Invoice.C_Currency_ID "
				+ ",C_Invoice.PaymentRule"
				+ ",C_Invoice.C_BPartner_Location_ID"
				+ ",C_Invoice.AD_User_ID"
				+ ",C_Invoice.AD_Org_ID"
				);

		return whereClause.toString();
	}

	private int createBill(MBPartner bpartner, ArrayList<MInvoice> invoiceList)
	{
		MBill 	 bill = null;
		MInvoice oldInvoice = null;

		Integer JP_BillSchema_ID = (Integer)bpartner.get_Value("JP_BillSchema_ID");
		MBillSchema billSchema =  new MBillSchema(getCtx(),JP_BillSchema_ID.intValue(),get_TrxName());

		int lineCounter = 0;
		int billCounter = 0;
		for(MInvoice invoice :invoiceList)
		{
			if(oldInvoice == null)
			{
				oldInvoice = invoice;
				bill = createBillHeader(invoice,billSchema);
				lineCounter = 0;
				billCounter++;
			}

			if(oldInvoice.getC_Currency_ID() != invoice.getC_Currency_ID())
			{
				bill.processIt(p_DocAction);
				bill.saveEx(get_TrxName());
				addBufferLog(0, null, null, bill.getDocumentNo()+":"+ bpartner.getName(), bill.get_Table_ID(), bill.getJP_Bill_ID());

				bill = createBillHeader(invoice,billSchema);
				lineCounter = 0;
				billCounter++;
			}

			if(!oldInvoice.getPaymentRule().equals(invoice.getPaymentRule()))
			{
				bill.processIt(p_DocAction);
				bill.saveEx(get_TrxName());
				addBufferLog(0, null, null, bill.getDocumentNo()+":"+ bpartner.getName(), bill.get_Table_ID(), bill.getJP_Bill_ID());

				bill = createBillHeader(invoice,billSchema);
				lineCounter = 0;
				billCounter++;
			}

			if(oldInvoice.getC_BPartner_Location_ID() != invoice.getC_BPartner_Location_ID())
			{
				bill.processIt(p_DocAction);
				bill.saveEx(get_TrxName());
				addBufferLog(0, null, null, bill.getDocumentNo()+":"+ bpartner.getName(), bill.get_Table_ID(), bill.getJP_Bill_ID());

				bill = createBillHeader(invoice,billSchema);
				lineCounter = 0;
				billCounter++;
			}

			if(oldInvoice.getAD_User_ID() != invoice.getAD_User_ID())
			{
				bill.processIt(p_DocAction);
				bill.saveEx(get_TrxName());
				addBufferLog(0, null, null, bill.getDocumentNo()+":"+ bpartner.getName(), bill.get_Table_ID(), bill.getJP_Bill_ID());

				bill = createBillHeader(invoice,billSchema);
				lineCounter = 0;
				billCounter++;
			}

			if(billSchema.isBillOrgJP())
			{
				if(oldInvoice.getAD_Org_ID() != invoice.getAD_Org_ID())
				{
					bill.processIt(p_DocAction);
					bill.saveEx(get_TrxName());
					addBufferLog(0, null, null, bill.getDocumentNo()+":"+ bpartner.getName(), bill.get_Table_ID(), bill.getJP_Bill_ID());

					bill = createBillHeader(invoice,billSchema);
					lineCounter = 0;
					billCounter++;
				}
			}

			MBillLine bLine = new MBillLine(getCtx(), 0, get_TrxName());
			bLine.setJP_Bill_ID(bill.getJP_Bill_ID());
			lineCounter++;
			bLine.setLine(lineCounter*10);
			bLine.setC_Invoice_ID(invoice.getC_Invoice_ID());
			bLine.setAD_Org_ID(bill.getAD_Org_ID());
			bLine.saveEx(get_TrxName());

			oldInvoice = invoice;


		}//for

		bill.processIt(p_DocAction);
		bill.saveEx(get_TrxName());
		addBufferLog(0, null, null, bill.getDocumentNo()+":"+ bpartner.getName(), bill.get_Table_ID(), bill.getJP_Bill_ID());

		return billCounter;
	}

	private MBill createBillHeader(MInvoice invoice,MBillSchema billSchema)
	{

		MBill bill = new MBill(getCtx(), 0, get_TrxName());
		if(billSchema.isBillOrgJP())
		{
			bill.setAD_Org_ID(invoice.getAD_Org_ID());
		}else{
			bill.setAD_Org_ID(billSchema.getJP_BillOrg_ID());
		}
		bill.setAD_OrgTrx_ID(invoice.getAD_OrgTrx_ID());
		bill.setJPDateBilled(new Timestamp(Calendar.getInstance().getTimeInMillis()));
		bill.setJPCutOffDate(p_JPCutOffDate);
		bill.setDateAcct(p_JPCutOffDate);
		bill.setSalesRep_ID(Env.getAD_User_ID(getCtx()));
		bill.setC_DocType_ID(billSchema.getC_DocType_ID());
		bill.setC_BPartner_ID(invoice.getC_BPartner_ID());
		bill.setC_BPartner_Location_ID(invoice.getC_BPartner_Location_ID());
		bill.setAD_User_ID(invoice.getAD_User_ID());
		bill.setPaymentRule(invoice.getPaymentRule());
		bill.setC_PaymentTerm_ID(invoice.getC_PaymentTerm_ID());
		bill.setC_Currency_ID(invoice.getC_Currency_ID());
		bill.setDocStatus(DocAction.STATUS_Drafted);
		bill.setDocAction(p_DocAction);
		bill.saveEx(get_TrxName());

		return bill;
	}

}
