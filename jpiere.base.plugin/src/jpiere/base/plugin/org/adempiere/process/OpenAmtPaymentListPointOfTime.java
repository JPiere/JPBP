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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Level;

import org.compiere.model.MAcctSchema;
import org.compiere.model.MConversionRate;
import org.compiere.model.MPayment;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;

import jpiere.base.plugin.util.JPierePaymentUtil;

/**
 * JPIERE-0456,0457 Open Amount of Payment at that time.
 *
 *
 * @author h.hagiwara
 *
 */
public class OpenAmtPaymentListPointOfTime extends SvrProcess {

	private int		p_AD_PInstance_ID = 0;
	private int		p_AD_Client_ID = 0;
	private int		p_AD_Org_ID = 0;
	private Timestamp	p_JP_PointOfTime = null;	//Mandatory
	private int 		p_C_BPartner_ID = 0;
	private int		p_JP_Corporation_ID = 0;
	private int		p_C_AcctSchema_ID = 0;		//Mandatory
	private MAcctSchema m_MAcctSchema = null;
	private int 		p_Account_ID = 0;
	private boolean	p_IsReceipt = false;		//Mandatory
	private int		p_C_BankAccount_ID = 0;


	@Override
	protected void prepare()
	{
		p_AD_PInstance_ID = getAD_PInstance_ID();
		p_AD_Client_ID =getProcessInfo().getAD_Client_ID();

		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null){
				;
			}else if (name.equals("JP_PointOfTime")){
				p_JP_PointOfTime = para[i].getParameterAsTimestamp();

			}else if (name.equals("AD_Org_ID")){
				p_AD_Org_ID = para[i].getParameterAsInt();

			}else if (name.equals("C_BPartner_ID")){
				p_C_BPartner_ID = para[i].getParameterAsInt();

			}else if(name.equals("JP_Corporation_ID")){
				p_JP_Corporation_ID = para[i].getParameterAsInt();

			}else if(name.equals("C_AcctSchema_ID")){
				p_C_AcctSchema_ID = para[i].getParameterAsInt();

			}else if(name.equals("Account_ID")){
				p_Account_ID = para[i].getParameterAsInt();

			}else if(name.equals("IsReceipt")){
				p_IsReceipt = para[i].getParameterAsString().equals("Y");

			}else if(name.equals("C_BankAccount_ID")){
				p_C_BankAccount_ID = para[i].getParameterAsInt();

			}else{
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}//if
		}//for

		m_MAcctSchema = MAcctSchema.get(getCtx(), p_C_AcctSchema_ID);

	}

	@Override
	protected String doIt() throws Exception {

		ArrayList<OpemAmtPayment> list = new ArrayList<OpemAmtPayment>();


		/**
		 * Get IsAllocated = 'N'
		 */
		StringBuilder sql1 = new StringBuilder("SELECT "
						+"dt.DocBaseType"
						+",bp.JP_Corporation_ID"
						+",vc1.Account_ID AS Account_ID"
						+",vc2.Account_ID AS PrepayAccount_ID"
						+",p.*"
					+ " FROM C_Payment p "
					+ " INNER JOIN C_DocType dt ON (dt.C_DocType_ID = p.C_DocType_ID)"
					+ " INNER JOIN C_BPartner bp ON (bp.C_BPartner_ID = p.C_BPartner_ID)");


		//Get for Account1
		if(p_IsReceipt)
		{
			sql1.append(" INNER JOIN C_BankAccount_Acct baa ON (baa.C_BankAccount_ID = p.C_BankAccount_ID AND baa.C_AcctSchema_ID = ?)" //1
					         + " INNER JOIN C_ValidCombination vc1 ON (baa.B_UnallocatedCash_Acct = vc1.C_ValidCombination_ID)" );
		}else{
			sql1.append(" INNER JOIN C_BankAccount_Acct baa ON (baa.C_BankAccount_ID = p.C_BankAccount_ID AND baa.C_AcctSchema_ID = ?)" //1
			         + " INNER JOIN C_ValidCombination vc1 ON (baa.B_PaymentSelect_Acct = vc1.C_ValidCombination_ID)" );
		}

		//Get for Prepay Account
		if(p_IsReceipt)
		{
			sql1.append(" INNER JOIN C_BP_Customer_Acct bpca ON (bp.C_BPartner_ID = bpca.C_BPartner_ID AND bpca.C_AcctSchema_ID = ?)"//2
					    + " INNER JOIN C_ValidCombination vc2 ON (bpca.C_Prepayment_Acct = vc2.C_ValidCombination_ID)");
		}else{
			sql1.append(" INNER JOIN C_BP_Vendor_Acct bpva ON (bp.C_BPartner_ID = bpva.C_BPartner_ID AND bpva.C_AcctSchema_ID = ?)"//2
				    + " INNER JOIN C_ValidCombination vc2 ON (bpva.V_Prepayment_Acct = vc2.C_ValidCombination_ID)");
		}

		sql1.append(" WHERE p.IsAllocated='N' AND p.AD_Client_ID = ? "//3 for use Table index
						+ " AND p.DocStatus IN ('CO','CL','VO','RE') "
						+ " AND p.DateAcct <= ?" //4
						+ " AND p.IsReceipt = ?"//5
						);

		if(p_AD_Org_ID > 0)
		{
			sql1.append(" AND p.AD_Org_ID= ? ");
		}

		if(p_C_BPartner_ID > 0)
		{
			sql1.append(" AND p.C_BPartner_ID= ? ");
		}

		if(p_JP_Corporation_ID > 0)
		{
			sql1.append(" AND bp.JP_Corporation_ID= ? ");
		}

		if(p_C_BankAccount_ID > 0)
		{
			sql1.append(" AND p.C_BankAccount_ID= ? ");
		}

		PreparedStatement pstmt1 = null;
		ResultSet rs1 = null;


		try
		{
			pstmt1 = DB.prepareStatement(sql1.toString(), get_TrxName());
			pstmt1.setInt(1, p_C_AcctSchema_ID);
			pstmt1.setInt(2, p_C_AcctSchema_ID);
			pstmt1.setInt(3, p_AD_Client_ID);
			pstmt1.setTimestamp(4, p_JP_PointOfTime);
			pstmt1.setString(5, p_IsReceipt==true? "Y": "N");

			int i = 5;
			if(p_AD_Org_ID > 0)
			{
				i++;
				pstmt1.setInt(i, p_AD_Org_ID);
			}

			if(p_C_BPartner_ID > 0)
			{
				i++;
				pstmt1.setInt(i, p_C_BPartner_ID);
			}

			if(p_JP_Corporation_ID > 0)
			{
				i++;
				pstmt1.setInt(i, p_JP_Corporation_ID);
			}

			if(p_C_BankAccount_ID > 0)
			{
				i++;
				pstmt1.setInt(i, p_C_BankAccount_ID);
			}


			rs1 = pstmt1.executeQuery();
			MPayment payment = null;
			int account_ID = 0;
			int prepayAccount_ID = 0;
			while(rs1.next())
			{
				payment = new MPayment(getCtx(), rs1, get_TrxName());
				account_ID = rs1.getInt(3) ;
				prepayAccount_ID = rs1.getInt(4) ;

				if(p_Account_ID != 0 && account_ID != p_Account_ID && prepayAccount_ID != p_Account_ID)
					continue;

				list.add(new OpemAmtPayment(payment 	//MIPayment
							,rs1.getString(1)			//docBaseType
							,rs1.getInt(2)				//JP_Corporation_ID
							,account_ID 				//Usual Account_ID
							,prepayAccount_ID 			//Prepay Account_ID
							)
						);

			}

		}catch (Exception e){
			log.log(Level.SEVERE,sql1.toString(), e);
		}
		finally{
			DB.close(rs1, pstmt1);
			rs1 = null; pstmt1 = null;
		}



		/**
		 * Get IsAllocated = 'Y'
		 */
		StringBuilder sql2 = new StringBuilder("SELECT DISTINCT "
								+ " dt.DocBaseType"
								+ ",bp.JP_Corporation_ID"
								+",vc1.Account_ID AS Account_ID"
								+",vc2.Account_ID AS PrepayAccount_ID"
								+" ,p.* "
							+ " FROM C_Payment p "
					    	+ " INNER JOIN C_AllocationLine al ON (p.C_Payment_ID = al.C_Payment_ID)"
					        + " INNER JOIN C_AllocationHdr a ON (al.C_AllocationHdr_ID = a.C_AllocationHdr_ID)"
					        + " INNER JOIN C_DocType dt ON (dt.C_DocType_ID = p.C_DocType_ID)"
					        + " INNER JOIN C_BPartner bp ON (bp.C_BPartner_ID = p.C_BPartner_ID)");

		//Get for Account1
		if(p_IsReceipt)
		{
			sql2.append(" INNER JOIN C_BankAccount_Acct baa ON (baa.C_BankAccount_ID = p.C_BankAccount_ID AND baa.C_AcctSchema_ID = ?)" //1
					         + " INNER JOIN C_ValidCombination vc1 ON (baa.B_UnallocatedCash_Acct = vc1.C_ValidCombination_ID)" );
		}else{
			sql2.append(" INNER JOIN C_BankAccount_Acct baa ON (baa.C_BankAccount_ID = p.C_BankAccount_ID AND baa.C_AcctSchema_ID = ?)" //1
							+ " INNER JOIN C_ValidCombination vc1 ON (baa.B_PaymentSelect_Acct = vc1.C_ValidCombination_ID)" );
		}

		//Get for Prepay Account
		if(p_IsReceipt)
		{
			sql2.append(" INNER JOIN C_BP_Customer_Acct bpca ON (bp.C_BPartner_ID = bpca.C_BPartner_ID AND bpca.C_AcctSchema_ID = ?)"//2
					    + " INNER JOIN C_ValidCombination vc2 ON (bpca.C_Prepayment_Acct = vc2.C_ValidCombination_ID)");
		}else{
			sql2.append(" INNER JOIN C_BP_Vendor_Acct bpva ON (bp.C_BPartner_ID = bpva.C_BPartner_ID AND bpva.C_AcctSchema_ID = ?)"//2
				    + " INNER JOIN C_ValidCombination vc2 ON (bpva.V_Prepayment_Acct = vc2.C_ValidCombination_ID)");
		}

		sql2.append(" WHERE p.IsAllocated='Y' AND p.AD_Client_ID = ? "//3 for use Table index
								+ " AND p.DocStatus IN ('CO','CL','VO','RE') "
								+ " AND p.DateAcct <= ?" //4
								+ " AND a.DateAcct > ?" //5
								+ " AND p.IsReceipt = ?"//6
								);

		if(p_AD_Org_ID > 0)
		{
			sql2.append(" AND p.AD_Org_ID= ? ");
		}

		if(p_C_BPartner_ID > 0)
		{
			sql2.append(" AND p.C_BPartner_ID= ? ");
		}

		if(p_JP_Corporation_ID > 0)
		{
			sql2.append(" AND bp.JP_Corporation_ID= ? ");
		}

		if(p_C_BankAccount_ID > 0)
		{
			sql2.append(" AND p.C_BankAccount_ID= ? ");
		}

		PreparedStatement pstmt2 = null;
		ResultSet rs2 = null;


		try
		{
			pstmt2 = DB.prepareStatement(sql2.toString(), get_TrxName());
			pstmt2.setInt(1, p_C_AcctSchema_ID);
			pstmt2.setInt(2, p_C_AcctSchema_ID);
			pstmt2.setInt(3, p_AD_Client_ID);
			pstmt2.setTimestamp(4, p_JP_PointOfTime);
			pstmt2.setTimestamp(5, p_JP_PointOfTime);
			pstmt2.setString(6, p_IsReceipt==true? "Y": "N");

			int i = 6;
			if(p_AD_Org_ID > 0)
			{
				i++;
				pstmt2.setInt(i, p_AD_Org_ID);
			}

			if(p_C_BPartner_ID > 0)
			{
				i++;
				pstmt2.setInt(i, p_C_BPartner_ID);
			}

			if(p_JP_Corporation_ID > 0)
			{
				i++;
				pstmt2.setInt(i, p_JP_Corporation_ID);
			}

			if(p_C_BankAccount_ID > 0)
			{
				i++;
				pstmt2.setInt(i, p_C_BankAccount_ID);
			}

			rs2 = pstmt2.executeQuery();
			OpemAmtPayment  openAmtPayment =null;
			MPayment payment2 = null;
			int account_ID2 = 0;
			int prepayAccount_ID2 = 0;
			while(rs2.next())
			{
				payment2 = new MPayment(getCtx(), rs2, get_TrxName());
				account_ID2 = rs2.getInt(3) ;
				prepayAccount_ID2 = rs2.getInt(4) ;

				if(p_Account_ID != 0 && account_ID2 != p_Account_ID && prepayAccount_ID2 != p_Account_ID)
					continue;

				openAmtPayment = new OpemAmtPayment(payment2				//MPayment
								,rs2.getString(1)							//docBaseType
								,rs2.getInt(2)								//JP_Corporation_ID
								,account_ID2								// Usual Account_ID
								,prepayAccount_ID2							// Prepay Account_ID
							);
				if(openAmtPayment.getJP_OpenAmtPointOfTime().compareTo(Env.ZERO) != 0)
				{
					list.add(openAmtPayment);
				}

			}

		}catch (Exception e){
			log.log(Level.SEVERE, sql2.toString(), e);
		}
		finally{
			DB.close(rs2, pstmt2);
			rs2 = null; pstmt2 = null;
		}


		/**
		 * Insert to temporally teble
		 */

		StringBuilder sql3 = new StringBuilder("INSERT INTO T_OpenPaymentPointOfTimeJP("
				+ "AD_Pinstance_ID"			//1
				+ ", C_Payment_ID"			//2
				+ ", C_Order_ID"			//3
				+ ", AD_Client_ID"			//4
				+ ", AD_Org_ID"				//5
				+ ", AD_OrgTrx_ID"			//6
				+ ", C_BPartner_ID"			//7
				+ ", JP_Corporation_ID"		//8
				+ ", C_Currency_ID"			//9
				+ ", C_AcctSchema_ID"		//10
				+ ", Account_ID"			//11
				+ ", C_Doctype_ID"			//12
				+ ", DocBasetype"			//13
				+ ", Documentno"			//14
				+ ", DocStatus"				//15
				+ ", DateTrx"				//16
				+ ", DateAcct"				//17
				+ ", IsAllocated"			//18
				+ ", IsReceipt"				//19
				+ ", PayAmt"				//20
				+ ", OpenAmt"				//21
				+ ", JP_OpenAmtPointOfTime"	//22
				+ ", JP_CurrencyTo_ID"		//23
				+ ", JP_ExchangedPayAmt"    //24
				+ ", JP_ExchangedOpenAmt"	//25
				+ ", JP_ExchangedOpenAmtPOT"//26
				+ ", Rate"					//27
				+ ", JP_PointOfTime"		//28
				+ ", C_BankAccount_ID"		//29
				+ ")"
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,   ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,   ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		PreparedStatement pstmt3 = null;
		ResultSet rs3 = null;


		try
		{
			pstmt3 = DB.prepareStatement(sql3.toString(), get_TrxName());
			for(OpemAmtPayment openAmtPayment : list)
			{
				pstmt3.setInt(1, p_AD_PInstance_ID);
				pstmt3.setInt(2, openAmtPayment.getPayment().getC_Payment_ID());
				if(openAmtPayment.getPayment().getC_Order_ID() > 0 )
					pstmt3.setInt(3,openAmtPayment.getPayment().getC_Order_ID());
				else
					pstmt3.setNull(3, java.sql.Types.INTEGER);
				pstmt3.setInt(4, p_AD_Client_ID);
				pstmt3.setInt(5, openAmtPayment.getPayment().getAD_Org_ID());
				if(openAmtPayment.getPayment().getAD_OrgTrx_ID() > 0 )
					pstmt3.setInt(6, openAmtPayment.getPayment().getAD_OrgTrx_ID());
				else
					pstmt3.setNull(6, java.sql.Types.INTEGER);
				pstmt3.setInt(7, openAmtPayment.getPayment().getC_BPartner_ID());
				if(openAmtPayment.getJP_Corporation_ID() > 0 )
					pstmt3.setInt(8, openAmtPayment.getJP_Corporation_ID());
				else
					pstmt3.setNull(8, java.sql.Types.INTEGER);
				pstmt3.setInt(9, openAmtPayment.getPayment().getC_Currency_ID());
				pstmt3.setInt(10, p_C_AcctSchema_ID);

				pstmt3.setInt(11, openAmtPayment.getAccount_ID());
				pstmt3.setInt(12, openAmtPayment.getPayment().getC_DocType_ID());
				pstmt3.setString(13, openAmtPayment.getDocBaseType());
				pstmt3.setString(14, openAmtPayment.getPayment().getDocumentNo());
				pstmt3.setString(15, openAmtPayment.getPayment().getDocStatus());
				pstmt3.setTimestamp(16, openAmtPayment.getPayment().getDateTrx());
				pstmt3.setTimestamp(17, openAmtPayment.getPayment().getDateAcct());
				pstmt3.setString(18, openAmtPayment.getPayment().isAllocated()==true? "Y": "N");
				pstmt3.setString(19, openAmtPayment.getPayment().isReceipt()==true? "Y": "N");
				if(openAmtPayment.getPayment().isReceipt())
					pstmt3.setBigDecimal(20, openAmtPayment.getPayment().getPayAmt());
				else
					pstmt3.setBigDecimal(20, openAmtPayment.getPayment().getPayAmt().negate());

				pstmt3.setBigDecimal(21, openAmtPayment.getOpenAmt());
				pstmt3.setBigDecimal(22, openAmtPayment.getJP_OpenAmtPointOfTime());

				//Foreign currency conversion
				pstmt3.setInt(23, m_MAcctSchema.getC_Currency_ID());
				if(openAmtPayment.getPayment().isReceipt())
					pstmt3.setBigDecimal(24, openAmtPayment.getJP_ExchangedPayAmt());
				else
					pstmt3.setBigDecimal(24, openAmtPayment.getJP_ExchangedPayAmt().negate());
				pstmt3.setBigDecimal(25, openAmtPayment.getJP_ExchangedOpenAmt());
				pstmt3.setBigDecimal(26, openAmtPayment.getJP_ExchangedOpenAmtPOT());
				pstmt3.setBigDecimal(27, openAmtPayment.getRate());
				pstmt3.setTimestamp(28, p_JP_PointOfTime);
				pstmt3.setInt(29, openAmtPayment.getPayment().getC_BankAccount_ID());
				pstmt3.executeUpdate();

			}

		}catch (Exception e){
			//log.log(Level.SEVERE, preSql, e);
		}
		finally{
			DB.close(rs3, pstmt3);
			rs3 = null; pstmt3 = null;
		}


		return "OK";

	}


	private class OpemAmtPayment
	{
		private MPayment payment = null;
		private String DocBaseType = null;
		private int JP_Corporation_ID = 0;
		private int Account_ID = 0;
		private int PrepayAccount_ID = 0;

		private BigDecimal OpenAmt = Env.ZERO;
		private BigDecimal JP_OpenAmtPointOfTime = Env.ZERO;
		private BigDecimal JP_ExchangedPayAmt  = Env.ZERO;
		private BigDecimal JP_ExchangedOpenAmt  = Env.ZERO;
		private BigDecimal JP_ExchangedOpenAmtPOT  = Env.ZERO;
		private BigDecimal Rate = Env.ONE;

		public OpemAmtPayment (MPayment payment, String DocBaseType,int JP_Corporation_ID, int Account_ID,int PrepayAccount_ID)
		{
			this.payment = payment;
			this.DocBaseType = DocBaseType;
			this.JP_Corporation_ID = JP_Corporation_ID;
			this.Account_ID = Account_ID;
			this.PrepayAccount_ID = PrepayAccount_ID;
			OpenAmt = JPierePaymentUtil.getOpenAmtNow(getCtx(), payment, get_TrxName());
			JP_OpenAmtPointOfTime = JPierePaymentUtil.getOpenAmtPointOfTime(getCtx(), payment, p_JP_PointOfTime, get_TrxName());


			if(payment.getC_Currency_ID() == m_MAcctSchema.getC_Currency_ID())
			{
				JP_ExchangedPayAmt = payment.getPayAmt();
				JP_ExchangedOpenAmt = OpenAmt;
				JP_ExchangedOpenAmtPOT = JP_OpenAmtPointOfTime;

			}else{
				JP_ExchangedPayAmt= MConversionRate.convert (getCtx(),payment.getPayAmt(), payment.getC_Currency_ID(), m_MAcctSchema.getC_Currency_ID(),
														payment.getDateAcct(), payment.getC_ConversionType_ID(), payment.getAD_Client_ID(), payment.getAD_Org_ID());
				JP_ExchangedOpenAmt= MConversionRate.convert (getCtx(), OpenAmt, payment.getC_Currency_ID(), m_MAcctSchema.getC_Currency_ID(),
														payment.getDateAcct(), payment.getC_ConversionType_ID(), payment.getAD_Client_ID(), payment.getAD_Org_ID());
				JP_ExchangedOpenAmtPOT= MConversionRate.convert (getCtx(), JP_OpenAmtPointOfTime, payment.getC_Currency_ID(), m_MAcctSchema.getC_Currency_ID(),
														payment.getDateAcct(), payment.getC_ConversionType_ID(), payment.getAD_Client_ID(), payment.getAD_Org_ID());
				Rate = MConversionRate.getRate(payment.getC_Currency_ID(), m_MAcctSchema.getC_Currency_ID(),
														payment.getDateAcct(), payment.getC_ConversionType_ID(), payment.getAD_Client_ID(), payment.getAD_Org_ID());
			}
		}

		public MPayment getPayment(){return payment;}
		public String getDocBaseType(){return DocBaseType;}
		public int getJP_Corporation_ID(){return JP_Corporation_ID;}
		public int getAccount_ID()
		{
			if(payment.isPrepayment())
			{
				return  PrepayAccount_ID;
			}else {
				return  Account_ID;
			}
		}
		public BigDecimal getOpenAmt(){return OpenAmt;}
		public BigDecimal getJP_OpenAmtPointOfTime(){return JP_OpenAmtPointOfTime;}
		public BigDecimal getJP_ExchangedPayAmt(){return JP_ExchangedPayAmt;}
		public BigDecimal getJP_ExchangedOpenAmt(){return JP_ExchangedOpenAmt;}
		public BigDecimal getJP_ExchangedOpenAmtPOT(){return JP_ExchangedOpenAmtPOT;}
		public BigDecimal getRate(){return Rate;}

	}//private class OpemAmtPayment


}//public class OpenAmtPaymentListPointOfTime

