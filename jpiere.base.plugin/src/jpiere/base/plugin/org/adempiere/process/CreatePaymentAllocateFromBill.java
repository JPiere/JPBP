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

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;

import jpiere.base.plugin.org.adempiere.model.MBill;
import jpiere.base.plugin.org.adempiere.model.MBillLine;
import jpiere.base.plugin.org.adempiere.model.MCorporation;
import jpiere.base.plugin.org.adempiere.model.MCorporationGroup;

import org.compiere.model.MBPartner;
import org.compiere.model.MInvoice;
import org.compiere.model.MPayment;
import org.compiere.model.MPaymentAllocate;
import org.compiere.model.MRefList;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;


/**
 * 
 * JPIERE-0216: Bills Allocation at Income payment.
 * 
 * @author h.hagiwara
 *
 */
public class CreatePaymentAllocateFromBill extends SvrProcess {
	
	private MPayment payment = null;
	private int C_Payment_ID = 0;
	
	private int[] p_JP_Bill_IDs = null;
	
	private MBPartner m_BPartner = null;
	private MCorporation m_Corporation = null;
	private MCorporationGroup[] m_CorporationGroups = null;
	
	@Override
	protected void prepare()
	{			
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null){
				;
			}else if (name.equals("JP_Bill_ID")){
				p_JP_Bill_IDs = para[i].getParameterAsIntArray();
			}else{
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}//if

		}//for	
	}
	
	@Override
	protected String doIt() throws Exception
	{
		C_Payment_ID = getRecord_ID();
		payment = new MPayment(getCtx(), C_Payment_ID, get_TrxName());
		m_BPartner = new MBPartner(getCtx(), payment.getC_BPartner_ID(), get_TrxName());
		int JP_Corporation_ID = m_BPartner.get_ValueAsInt(MCorporation.COLUMNNAME_JP_Corporation_ID);
		if(JP_Corporation_ID > 0)
		{
			m_Corporation = new MCorporation(getCtx(), JP_Corporation_ID, get_TrxName());
			m_CorporationGroups = m_Corporation.getCorporationGroups();
		}
		
		//Check Payment
		if(DocAction.STATUS_Completed.equals(payment.getDocStatus()) 
				|| DocAction.STATUS_Closed.equals(payment.getDocStatus()) 
				|| DocAction.STATUS_Voided.equals(payment.getDocStatus()) 
				|| DocAction.STATUS_Reversed.equals(payment.getDocStatus()) )
		{
			error_msg = Msg.getElement(getCtx(), "C_Payment_ID", true) + " : " +payment.getDocumentNo()
			+ " - "+ Msg.getElement(getCtx(), "DocStatus") +" : " + MRefList.getListName(getCtx(), 131, payment.getDocStatus());
			throw new Exception(error_msg);
		}
		
		if(!payment.isReceipt())
		{
			error_msg = Msg.getMsg(getCtx(), "JP_Process_Cannot_Perform") + " - " + Msg.getElement(getCtx(), "C_Payment_ID", false);
			throw new Exception(error_msg);
		}
		
		if(payment.getC_Invoice_ID() > 0)
		{
			error_msg = Msg.getMsg(getCtx(), "JP_Process_Cannot_Perform") + " - " + Msg.getElement(getCtx(), "C_Invoice_ID", true);
			throw new Exception(error_msg);
		}

		if(payment.getC_Order_ID() > 0)
		{
			error_msg = Msg.getMsg(getCtx(), "JP_Process_Cannot_Perform") + " - " + Msg.getElement(getCtx(), "C_Order_ID", true);
			throw new Exception(error_msg);
		}
		
		//Allocation JP_Bill_ID
		int JP_Bill_ID = payment.get_ValueAsInt("JP_Bill_ID");
		if(JP_Bill_ID > 0)
		{
			MBill bill = new MBill(getCtx(), JP_Bill_ID, get_TrxName());
			BigDecimal openAmt = bill.getCurrentOpenAmt();
			if(openAmt.compareTo(Env.ZERO) == 0)
			{
				addBufferLog(0, null, null, Msg.getElement(getCtx(), MInvoice.COLUMNNAME_IsPaid, true) + " : " + bill.getDocumentNo(), MBill.Table_ID, bill.getJP_Bill_ID());
			}else {
			
				if(!doAllocation(bill))
				{
					throw new Exception(error_msg);
				}
			}
		}
		
		//Allocation Multiple Selection Search
		int last_JP_Bill_ID = 0;
		if(p_JP_Bill_IDs != null)
		{
			for(int Bill_ID : p_JP_Bill_IDs)
			{
				if(Bill_ID == JP_Bill_ID)
					continue;
				
				MBill bill = new MBill(getCtx(), Bill_ID, get_TrxName());
				BigDecimal openAmt = bill.getCurrentOpenAmt();
				if(openAmt.compareTo(Env.ZERO) == 0)
				{
					addBufferLog(0, null, null, Msg.getElement(getCtx(), MInvoice.COLUMNNAME_IsPaid, true) + " : " + bill.getDocumentNo(), MBill.Table_ID, bill.getJP_Bill_ID());
				}else {
					if(!doAllocation(bill))
					{
						throw new Exception(error_msg);
					}else {
						last_JP_Bill_ID = Bill_ID;
					}
				}
			}
		}
		
		//Update PayAmt in case of 0.
		if(payment.getPayAmt().compareTo(Env.ZERO) == 0)
		{
			MPaymentAllocate[] pAllocs = MPaymentAllocate.get(payment);
			BigDecimal payAmt = Env.ZERO;
			for(MPaymentAllocate pAlloc : pAllocs)
			{
				payAmt = payAmt.add(pAlloc.getAmount());
			}
			payment.setPayAmt(payAmt);
		}
		
		//Update JP_Bill_ID field in case of blank.
		if(JP_Bill_ID == 0 && last_JP_Bill_ID != 0)
		{
			payment.set_ValueNoCheck("JP_Bill_ID", last_JP_Bill_ID);
		}
		payment.saveEx(get_TrxName());
		
		return Msg.getMsg(getCtx(),"Created") + " - " + Msg.getElement(getCtx(), "C_PaymentAllocate_ID", true)  + " : " + counter;
	}
	
	private String error_msg = null;
	private int counter = 0;
	
	private boolean doAllocation(MBill bill)
	{
		//Check Bill
		if(DocAction.STATUS_Completed.equals(bill.getDocStatus()) || DocAction.STATUS_Closed.equals(bill.getDocStatus()) )
		{
			;//OK
		}else {
			error_msg = Msg.getElement(getCtx(), "JP_Bill_ID", true) + " : " +bill.getDocumentNo()
						+ " - "+ Msg.getElement(getCtx(), "DocStatus") +" : " + MRefList.getListName(getCtx(), 131, bill.getDocStatus());
			return false;
		}
		
		if(!bill.isSOTrx())
		{
			error_msg = Msg.getElement(getCtx(), "JP_Bill_ID", false) + " : " +bill.getDocumentNo();
			return false;
		}
		
		if(payment.getC_Currency_ID() != bill.getC_Currency_ID())
		{
			error_msg = Msg.getMsg(getCtx(), "JP_DifferentCurrency") +" - " + Msg.getElement(getCtx(), "JP_Bill_ID", true) + " : " +bill.getDocumentNo();
			return false;
		}
		
		//Check Corporation & Corporation Group
		if(m_Corporation != null)
		{
			MBPartner bii_BP = new MBPartner(getCtx(), bill.getC_BPartner_ID(), get_TrxName());
			int bill_Corporation_ID = bii_BP.get_ValueAsInt(MCorporation.COLUMNNAME_JP_Corporation_ID);
			if(bill_Corporation_ID == 0)
			{
				//Allocation between different Group Corporation is forbidden.
				error_msg = Msg.getMsg(getCtx(), "JP_Allocation_DiffCorp")
								+ " - " + bii_BP.getValue() + "_" + bii_BP.getName()
								+ " : " + Msg.getMsg(getCtx(), "JP_Null") + Msg.getElement(getCtx(), MCorporation.COLUMNNAME_JP_Corporation_ID);
				return false;
			}
			
			if(bill_Corporation_ID == m_Corporation.getJP_Corporation_ID())
			{
				;//OK - Same Corporation.
				
			}else {
				
				MCorporation bill_Corporation = new MCorporation(getCtx(), bill_Corporation_ID, get_TrxName());
				MCorporationGroup[] bill_CorporationGroups = bill_Corporation.getCorporationGroups();
				boolean isOK = false;
				for(MCorporationGroup bill_CorporationGroup : bill_CorporationGroups)
				{
					for(MCorporationGroup payment_CorporationGroup : m_CorporationGroups)
					{
						if(bill_CorporationGroup.getJP_CorporationGroup_ID() == payment_CorporationGroup.getJP_CorporationGroup_ID() )
						{
							isOK = true;
							break;
						}
					}
					
					if(isOK)
					{
						break;
					}
				}
				
				if(!isOK)
				{
					//Allocation between different Group Corporation is forbidden.
					error_msg = Msg.getMsg(getCtx(), "JP_Allocation_DiffCorp")
									+ " : " + bii_BP.getValue() + "_" + bii_BP.getName();
					return false;
				}
			}
		}
		
		
		//Allocation
		MPaymentAllocate[] pAllocs = MPaymentAllocate.get(payment);
		MBillLine[] billLines = bill.getLines();
		for(int i = 0; i < billLines.length; i++)
		{
			//Check Paid
			MInvoice inv = new MInvoice(getCtx(), billLines[i].getC_Invoice_ID(), get_TrxName());
			if(inv.isPaid())
				continue;
			
			//Check Same Invoice
			boolean isContaine=false;
			for(int j = 0; j < pAllocs.length; j++)
			{
				if(inv.getC_Invoice_ID()==pAllocs[j].getC_Invoice_ID())
				{
					isContaine = true;
					break;
				}
			}
			
			if(isContaine)
				continue;
			
			MPaymentAllocate pAllocate = new MPaymentAllocate(getCtx(), 0, get_TrxName());
			pAllocate.setC_Payment_ID(C_Payment_ID);
			pAllocate.setAD_Org_ID(payment.getAD_Org_ID());
			pAllocate.setC_Invoice_ID(inv.getC_Invoice_ID());
			
			int C_InvoicePaySchedule_ID = 0;
			Timestamp ts = payment.getDateTrx();//  Payment Date
			//
			String sql = "SELECT C_BPartner_ID,C_Currency_ID,"		//	1..2
				+ " invoiceOpen(C_Invoice_ID, ?),"					//	3		#1
				+ " invoiceDiscount(C_Invoice_ID,?,?), IsSOTrx "	//	4..5	#2/3
				+ "FROM C_Invoice WHERE C_Invoice_ID=?";			//			#4
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				pstmt = DB.prepareStatement(sql, null);
				pstmt.setInt(1, C_InvoicePaySchedule_ID);
				pstmt.setTimestamp(2, ts);
				pstmt.setInt(3, C_InvoicePaySchedule_ID);
				pstmt.setInt(4, inv.getC_Invoice_ID());
				rs = pstmt.executeQuery();
				if (rs.next())
				{
					//	mTab.setValue("C_BPartner_ID", new Integer(rs.getInt(1)));
					//	int C_Currency_ID = rs.getInt(2);					//	Set Invoice Currency
					//	mTab.setValue("C_Currency_ID", new Integer(C_Currency_ID));
					//
					BigDecimal InvoiceOpen = rs.getBigDecimal(3);		//	Set Invoice OPen Amount
					if (InvoiceOpen == null)
						InvoiceOpen = Env.ZERO;
					BigDecimal DiscountAmt = rs.getBigDecimal(4);		//	Set Discount Amt
					if (DiscountAmt == null)
						DiscountAmt = Env.ZERO;
					
					pAllocate.setInvoiceAmt(InvoiceOpen);
					pAllocate.setAmount(InvoiceOpen);
					pAllocate.setDiscountAmt(Env.ZERO);

				}
			}
			catch (SQLException e)
			{
				log.log(Level.SEVERE, sql, e);
				return false;
			}
			finally
			{
				DB.close(rs, pstmt);
				rs = null; pstmt = null;
			}
			pAllocate.saveEx(get_TrxName());
			counter++;
		}
		
		return true;
	}
	
}
