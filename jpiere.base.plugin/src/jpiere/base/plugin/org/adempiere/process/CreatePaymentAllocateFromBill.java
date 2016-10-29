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

import org.compiere.model.MInvoice;
import org.compiere.model.MPayment;
import org.compiere.model.MPaymentAllocate;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;


/**
 * 
 * JPIERE-0216
 * 
 * @author h.hagiwara
 *
 */
public class CreatePaymentAllocateFromBill extends SvrProcess {
	
	MPayment payment = null;
	int C_Payment_ID = 0;
	
	MBill bill = null;
	int JP_Bill_ID = 0;

	
	@Override
	protected void prepare() {
		
		C_Payment_ID = getRecord_ID();
		payment = new MPayment(getCtx(), C_Payment_ID, get_TrxName());
		
		JP_Bill_ID = payment.get_ValueAsInt("JP_Bill_ID");
		bill = new MBill(getCtx(), JP_Bill_ID, get_TrxName());
		
	}
	
	@Override
	protected String doIt() throws Exception {
		
		if(payment.getC_Currency_ID() != bill.getC_Currency_ID())
		{
			return Msg.getMsg(getCtx(), "JP_DifferentCurrency");
		}
		
		
		payment.setPayAmt(bill.getJPBillAmt());
		payment.saveEx(get_TrxName());

		MPaymentAllocate[] pAllocs = MPaymentAllocate.get(payment);
		
		MBillLine[] billLines = bill.getLines();
		int counter = 0;
		for(int i = 0; i < billLines.length; i++)
		{
			//Check Paid
			MInvoice inv = MInvoice.get(getCtx(), billLines[i].getC_Invoice_ID());
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
				return e.getLocalizedMessage();
			}
			finally
			{
				DB.close(rs, pstmt);
				rs = null; pstmt = null;
			}
			pAllocate.saveEx(get_TrxName());
			counter++;
		}
		
		return Msg.getMsg(getCtx(), "Created") +" : " + counter;
	}
	
}
