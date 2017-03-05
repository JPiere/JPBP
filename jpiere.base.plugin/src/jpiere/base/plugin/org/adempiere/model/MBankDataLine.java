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
package jpiere.base.plugin.org.adempiere.model;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.model.MInvoice;
import org.compiere.model.MPayment;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;

/**
 * JPIERE-0302 Import Bank Data Window
 * 
 * 
 * @author h.hagiwara
 *
 */
public class MBankDataLine extends X_JP_BankDataLine {
	
	public MBankDataLine(Properties ctx, int JP_BankDataLine_ID, String trxName) 
	{
		super(ctx, JP_BankDataLine_ID, trxName);
	}
	
	public MBankDataLine(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}

	private MBankData m_BankData = null;
	
	public MBankData getParent()
	{
		if (m_BankData == null)
			m_BankData = new MBankData(getCtx(), getJP_BankData_ID(), get_TrxName());
		return m_BankData;
	}	//	getParent
	
	
	@Override
	protected boolean beforeSave(boolean newRecord)
	{
		//	Calculate Charge = Statement - trx - Interest  
		if(is_ValueChanged("StmtAmt") || is_ValueChanged("TrxAmt") || is_ValueChanged("ChargeAmt") || is_ValueChanged("InterestAmt"))
		{
			BigDecimal amt = getStmtAmt();
			amt = amt.subtract(getTrxAmt());
			amt = amt.subtract(getInterestAmt());
			if (amt.compareTo(getChargeAmt()) != 0)
				setChargeAmt (amt);
		}
		
		if (getChargeAmt().signum() != 0 && getC_Charge_ID() == 0)
		{
			log.saveError("FillMandatory", Msg.getElement(getCtx(), "C_Charge_ID"));
			return false;
		}
		
		if(is_ValueChanged("C_Invoice_ID") && getC_Invoice_ID() > 0)
		{
			setJP_Bill_ID(0);
			setC_Payment_ID(0);
			MInvoice invoice = new MInvoice(getCtx(), getC_Invoice_ID(), null);
			if(invoice.isPaid())
			{
				log.saveError("JP_InvoicePaid","");//Invoice have paid already
				return false;
			}
			setC_BPartner_ID(invoice.getC_BPartner_ID());
		}
		
		
		if(is_ValueChanged("JP_Bill_ID") && getJP_Bill_ID() > 0)
		{
			setC_Invoice_ID(0);
			setC_Payment_ID(0);
			MBill bill = new MBill(getCtx(), getJP_Bill_ID(), null);
			BigDecimal currentOpenAmt =  bill.getCurrentOpenAmt();
			if(!(currentOpenAmt.compareTo(Env.ZERO) > 0))
			{
				log.saveError("JP_BillPaid","");//Bill have paid already
				return false;				
			}
			setC_BPartner_ID(bill.getC_BPartner_ID());
		}
		
		
		if(is_ValueChanged("C_Payment_ID") && getC_Payment_ID() > 0)
		{
			setC_Invoice_ID(0);
			setJP_Bill_ID(0);
			MPayment payment = new MPayment(getCtx(), getC_Payment_ID(), null);
			if(payment.isReconciled())
			{
				log.saveError("JP_PaymentReconciled","");//Payment have reconciled already
				return false;
			}
			setC_BPartner_ID(payment.getC_BPartner_ID());
		}		
				
		if(getC_Invoice_ID() > 0 || getJP_Bill_ID() > 0 || getC_Payment_ID() > 0)
			setIsMatchedJP(true);
		else
			setIsMatchedJP(false);
		
		return true;
	}

	@Override
	protected boolean afterSave(boolean newRecord, boolean success) 
	{
		
		if(is_ValueChanged("StmtAmt") || is_ValueChanged("TrxAmt") || is_ValueChanged("ChargeAmt") || is_ValueChanged("InterestAmt"))
		{
			String sql = "SELECT SUM(StmtAmt), SUM(TrxAmt), SUM(ChargeAmt), SUM(InterestAmt) From JP_BankDataLine WHERE JP_BankData_ID = ?";
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				pstmt = DB.prepareStatement (sql, get_TrxName());
				pstmt.setInt (1, getParent().getJP_BankData_ID());
				rs = pstmt.executeQuery();
				if (rs.next ())
				{
					m_BankData.setStmtAmt(rs.getBigDecimal(1));
					m_BankData.setTrxAmt(rs.getBigDecimal(2));
					m_BankData.setChargeAmt(rs.getBigDecimal(3));
					m_BankData.setInterestAmt(rs.getBigDecimal(4));
				}
			}
			catch (Exception e)
			{
				log.log (Level.SEVERE, sql, e);
			}
			finally
			{
				DB.close(rs, pstmt);
				rs = null;
				pstmt = null;
			}
			
			m_BankData.saveEx(get_TrxName());
		}
		
		return true;
	}
	

	
	
}
