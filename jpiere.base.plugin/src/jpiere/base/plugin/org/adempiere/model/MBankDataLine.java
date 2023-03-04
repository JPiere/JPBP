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
import org.compiere.model.MOrder;
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
		//For Import
		if(newRecord && getStmtAmt().compareTo(Env.ZERO) != 0 && getTrxAmt().compareTo(Env.ZERO)== 0 && getChargeAmt().compareTo(Env.ZERO)== 0 && getInterestAmt().compareTo(Env.ZERO)== 0)
			setTrxAmt(getStmtAmt());


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
			setC_Order_ID(0);
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
			setC_Order_ID(0);
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
			setC_Order_ID(0);
			MPayment payment = new MPayment(getCtx(), getC_Payment_ID(), null);
			if(payment.isReconciled())
			{
				log.saveError("JP_PaymentReconciled","");//Payment have reconciled already
				return false;
			}
			setC_BPartner_ID(payment.getC_BPartner_ID());
		}

		if(is_ValueChanged("C_Order_ID") && getC_Order_ID() > 0)
		{
			setC_Invoice_ID(0);
			setJP_Bill_ID(0);
			setC_Payment_ID(0);
			MOrder order = new MOrder(getCtx(), getC_Order_ID(), null);
			if(order.getC_Payment_ID() > 0)
			{
				log.saveError("JP_OrderPaid","");//Order have paid already
				return false;
			}
			setC_BPartner_ID(order.getC_BPartner_ID());
		}

		if(getC_Invoice_ID() > 0 || getJP_Bill_ID() > 0 || getC_Payment_ID() > 0 || getC_Order_ID() > 0)
			setIsMatchedJP(true);
		else
			setIsMatchedJP(false);

		return true;
	}

	@Override
	protected boolean afterSave(boolean newRecord, boolean success)
	{

		if(!success)
			return false;

		if(newRecord)
			return updateHeader(true);
		else
			return updateHeader(false);

	}

	@Override
	protected boolean afterDelete(boolean success)
	{
		if(!success)
			return false;

		return updateHeader(true);
	}


	private boolean updateHeader(boolean forced)
	{
		if(forced || (is_ValueChanged("StmtAmt") || is_ValueChanged("TrxAmt") || is_ValueChanged("ChargeAmt") || is_ValueChanged("InterestAmt")) )
		{
			String sql = "SELECT COALESCE(SUM(StmtAmt),0), COALESCE(SUM(TrxAmt),0), COALESCE(SUM(ChargeAmt),0), COALESCE(SUM(InterestAmt),0) From JP_BankDataLine WHERE JP_BankData_ID = ?";
			BigDecimal StmtAmt = Env.ZERO;
			BigDecimal TrxAmt = Env.ZERO;
			BigDecimal ChargeAmt = Env.ZERO;
			BigDecimal InterestAmt = Env.ZERO;

			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				pstmt = DB.prepareStatement (sql, get_TrxName());
				pstmt.setInt (1, getJP_BankData_ID());
				rs = pstmt.executeQuery();
				if (rs.next ())
				{
					StmtAmt =rs.getBigDecimal(1);
					TrxAmt = rs.getBigDecimal(2);
					ChargeAmt = rs.getBigDecimal(3);
					InterestAmt = rs.getBigDecimal(4);
				}

			} catch (Exception e){

				log.log (Level.SEVERE, sql, e);
				return false;

			} finally {

				DB.close(rs, pstmt);
				rs = null;
				pstmt = null;
			}


			sql = "UPDATE JP_BankData SET StmtAmt=?, TrxAmt=?, ChargeAmt=?, InterestAmt=? WHERE JP_BankData_ID = ?";
			try
			{
				pstmt = DB.prepareStatement (sql, get_TrxName());
				pstmt.setBigDecimal(1, StmtAmt);
				pstmt.setBigDecimal(2, TrxAmt);
				pstmt.setBigDecimal(3, ChargeAmt);
				pstmt.setBigDecimal(4, InterestAmt);
				pstmt.setInt(5, getJP_BankData_ID());

				int no = pstmt.executeUpdate();
				if (no != 1) {
					log.log (Level.SEVERE, sql);
					return false;
				}

			}catch (Exception e) {

				log.log (Level.SEVERE, sql, e);
				return false;

			}finally {

				DB.close(rs, pstmt);
				rs = null;
				pstmt = null;
			}

		}

		return true;
	}




}
