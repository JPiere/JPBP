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

import jpiere.base.plugin.util.JPiereInvoiceUtil;

import org.compiere.model.MAcctSchema;
import org.compiere.model.MConversionRate;
import org.compiere.model.MInvoice;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;


public class OpenAmtInvoiceListPointOfTime extends SvrProcess {

	private int		p_AD_PInstance_ID = 0;
	private int		p_AD_Client_ID = 0;
	private int		p_AD_Org_ID = 0;
	private Timestamp	p_JP_PointOfTime = null;	//Mandatory
	private int 		p_C_BPartner_ID = 0;
	private int		p_JP_Corporation_ID = 0;
	private int		p_C_AcctSchema_ID = 0;		//Mandatory
	private MAcctSchema m_MAcctSchema = null;
	private boolean	p_IsSOTrx = false;			//Mandatory


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
				
			}else if(name.equals("IsSOTrx")){
				p_IsSOTrx = para[i].getParameterAsString().equals("Y");
				
			}else{
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}//if
		}//for

		m_MAcctSchema = MAcctSchema.get(getCtx(), p_C_AcctSchema_ID);
		
	}

	@Override
	protected String doIt() throws Exception {
				
		ArrayList<OpemAmtInvoice> list = new ArrayList<OpemAmtInvoice>();
		
		
		/**
		 * Get Data of IsPaid = 'N'
		 */
		StringBuilder sql1 = new StringBuilder("SELECT "
						+"dt.DocBaseType"
						+",bp.JP_Corporation_ID"
						+",vc.Account_ID"
						+",i.*"
					+ " FROM C_Invoice i "
					+ " INNER JOIN C_DocType dt ON (dt.C_DocType_ID = i.C_DocTypeTarget_ID)"
					+ " INNER JOIN C_BPartner bp ON (bp.C_BPartner_ID = i.C_BPartner_ID)");
		
		if(p_IsSOTrx)
		{
			sql1.append(" INNER JOIN C_BP_Customer_Acct bpca ON (bp.C_BPartner_ID = bpca.C_BPartner_ID AND bpca.C_AcctSchema_ID = ?)"//1
					    + " INNER JOIN C_ValidCombination vc ON (bpca.c_receivable_Acct = vc.C_ValidCombination_ID)");
		}else{
			sql1.append(" INNER JOIN C_BP_Vendor_Acct bpva ON (bp.C_BPartner_ID = bpva.C_BPartner_ID AND bpva.C_AcctSchema_ID = ?)"//1
				    + " INNER JOIN C_ValidCombination vc ON (bpca.c_receivable_Acct = vc.C_ValidCombination_ID)");			
		}

		sql1.append(" WHERE i.IsPaid='N' AND i.AD_Client_ID = ? "//2 for use Table index
						+ " AND i.DocStatus IN ('CO','CL','VO','RE') "
						+ " AND i.DateAcct <= ?" //3
						+ " AND i.IsSOTrx = ?"//4
						);

		if(p_AD_Org_ID > 0)
		{
			sql1.append(" AND i.AD_Org_ID= ? ");
		}
		
		if(p_C_BPartner_ID > 0)
		{
			sql1.append(" AND i.C_BPartner_ID= ? ");
		}
		
		if(p_JP_Corporation_ID > 0)
		{
			sql1.append(" AND bp.JP_Corporation_ID= ? ");
		}
		
		PreparedStatement pstmt1 = null;
		ResultSet rs1 = null;
		
		
		try
		{
			pstmt1 = DB.prepareStatement(sql1.toString(), get_TrxName());
			pstmt1.setInt(1, p_C_AcctSchema_ID);
			pstmt1.setInt(2, p_AD_Client_ID);
			pstmt1.setTimestamp(3, p_JP_PointOfTime);
			pstmt1.setString(4, p_IsSOTrx==true? "Y": "N");
			
			int i = 4;
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
			
			
			rs1 = pstmt1.executeQuery();
			while(rs1.next())
			{
				list.add(new OpemAmtInvoice(
							new MInvoice(getCtx(), rs1, get_TrxName()) 	//MInvoice
							,rs1.getString(1)							//docBaseType 
							,rs1.getInt(2)								//JP_Corporation_ID
							,rs1.getInt(3) 								// Account_ID
							)
						);
				
			}

		}catch (Exception e){
			//log.log(Level.SEVERE, preSql, e);
		}
		finally{
			DB.close(rs1, pstmt1);
			rs1 = null; pstmt1 = null;
		}
		
		
		
		/**
		 * Get Date of IsPaid = 'Y'
		 */
		StringBuilder sql2 = new StringBuilder("SELECT DISTINCT "
								+ " dt.DocBaseType"
								+ ",bp.JP_Corporation_ID"
								+" ,vc.Account_ID"
								+" ,i.* "
							+ " FROM C_Invoice i "
					    	+ " INNER JOIN C_AllocationLIne al ON (i.C_Invoice_ID = al.C_Invoice_ID)"
					        + " INNER JOIN C_AllocationHdr a ON (al.C_AllocationHdr_ID = a.C_AllocationHdr_ID)"
					        + " INNER JOIN C_DocType dt ON (dt.C_DocType_ID = i.C_DocTypeTarget_ID)"
					        + " INNER JOIN C_BPartner bp ON (bp.C_BPartner_ID = i.C_BPartner_ID)");
					        
		if(p_IsSOTrx)
		{
			sql2.append(" INNER JOIN C_BP_Customer_Acct bpca ON (bp.C_BPartner_ID = bpca.C_BPartner_ID AND bpca.C_AcctSchema_ID = ?)"//1
					    + " INNER JOIN C_ValidCombination vc ON (bpca.c_receivable_Acct = vc.C_ValidCombination_ID)");
		}else{
			sql2.append(" INNER JOIN C_BP_Vendor_Acct bpva ON (bp.C_BPartner_ID = bpva.C_BPartner_ID AND bpva.C_AcctSchema_ID = ?)"//1
				    + " INNER JOIN C_ValidCombination vc ON (bpca.c_receivable_Acct = vc.C_ValidCombination_ID)");			
		}
			    
		sql2.append(" WHERE i.IsPaid='Y' AND i.AD_Client_ID = ? "//2 for use Table index
								+ " AND i.DocStatus IN ('CO','CL','VO','RE') "
								+ " AND i.DateAcct <= ?" //3
								+ " AND a.DateAcct > ?" //4
								+ " AND i.IsSOTrx = ?"//5
								);

		if(p_AD_Org_ID > 0)
		{
			sql2.append(" AND i.AD_Org_ID= ? ");
		}
		
		if(p_C_BPartner_ID > 0)
		{
			sql2.append(" AND i.C_BPartner_ID= ? ");
		}
		
		if(p_JP_Corporation_ID > 0)
		{
			sql2.append(" AND bp.JP_Corporation_ID= ? ");
		}
		
		PreparedStatement pstmt2 = null;
		ResultSet rs2 = null;
		

		try
		{
			pstmt2 = DB.prepareStatement(sql2.toString(), get_TrxName());
			pstmt2.setInt(1, p_C_AcctSchema_ID);
			pstmt2.setInt(2, p_AD_Client_ID);
			pstmt2.setTimestamp(3, p_JP_PointOfTime);
			pstmt2.setTimestamp(4, p_JP_PointOfTime);
			pstmt2.setString(5, p_IsSOTrx==true? "Y": "N");
			
			int i = 5;
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
			
			
			rs2 = pstmt2.executeQuery();
			OpemAmtInvoice  openAmnInv =null;
			while(rs2.next())
			{
				openAmnInv = new OpemAmtInvoice(
								new MInvoice(getCtx(), rs2, get_TrxName()) 	//MInvoice
								,rs2.getString(1)							//docBaseType 
								,rs2.getInt(2)								//JP_Corporation_ID
								,rs2.getInt(3) 								// Account_ID
							);
				if(openAmnInv.getJP_OpenAmtPointOfTime().compareTo(Env.ZERO) != 0)
				{
					list.add(openAmnInv);	
				}
				
			}

		}catch (Exception e){
			//log.log(Level.SEVERE, preSql, e);
		}
		finally{
			DB.close(rs2, pstmt2);
			rs2 = null; pstmt2 = null;
		}
		
		
		/**
		 * Insert to temporally teble
		 */
		
		StringBuilder sql3 = new StringBuilder("INSERT INTO T_OpenInvPointOfTimeJP("
				+ "AD_Pinstance_ID"			//1
				+ ", C_Invoice_ID"			//2
				+ ", C_Order_ID"			//3
				+ ", AD_Client_ID"			//4
				+ ", AD_Org_ID"				//5
				+ ", AD_OrgTrx_ID"			//6
				+ ", C_BPartner_ID"			//7
				+ ", JP_Corporation_ID"		//8
				+ ", C_Currency_ID"			//9
				+ ", SalesRep_ID"			//10
				+ ", C_PaymentTerm_ID"		//11
				+ ", C_AcctSchema_ID"		//12
				+ ", C_ElementValue_ID"		//13
				+ ", C_Doctype_ID"			//14
				+ ", DocBasetype"			//15
				+ ", Documentno"			//16
				+ ", POReference"			//17
				+ ", PaymentRule"			//18
				+ ", DocStatus"				//19
				+ ", DateInvoiced"			//20
				+ ", DateAcct"				//21
				+ ", IsPaid"				//22
				+ ", IsSOTrx"				//23
				+ ", Grandtotal"			//24
				+ ", OpenAmt"				//25
				+ ", JP_OpenAmtPointOfTime"	//26
				+ ", JP_CurrencyTo_ID"		//27
				+ ", JP_ExchangedGrandtotal"//28
				+ ", JP_ExchangedOpenAmt"	//29
				+ ", JP_ExchangedOpenAmtPOT"//30
				+ ", Rate"					//31
				+ ", JP_PointOfTime"		//32
				+ ")"
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,   ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,   ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,   ?, ?)");
		
		PreparedStatement pstmt3 = null;
		ResultSet rs3 = null;
		

		try
		{
			pstmt3 = DB.prepareStatement(sql3.toString(), get_TrxName());
			for(OpemAmtInvoice openAmtInv : list)
			{
				pstmt3.setInt(1, p_AD_PInstance_ID);
				pstmt3.setInt(2, openAmtInv.getInvoice().getC_Invoice_ID());
				if(openAmtInv.getInvoice().getC_Order_ID() > 0 )
					pstmt3.setInt(3,openAmtInv.getInvoice().getC_Order_ID());
				else
					pstmt3.setNull(3, java.sql.Types.INTEGER);
				pstmt3.setInt(4, p_AD_Client_ID);
				pstmt3.setInt(5, openAmtInv.getInvoice().getAD_Org_ID());
				if(openAmtInv.getInvoice().getAD_OrgTrx_ID() > 0 )
					pstmt3.setInt(6, openAmtInv.getInvoice().getAD_OrgTrx_ID());
				else 
					pstmt3.setNull(6, java.sql.Types.INTEGER);
				pstmt3.setInt(7, openAmtInv.getInvoice().getC_BPartner_ID());
				if(openAmtInv.getJP_Corporation_ID() > 0 )
					pstmt3.setInt(8, openAmtInv.getJP_Corporation_ID());
				else 
					pstmt3.setNull(8, java.sql.Types.INTEGER);				
				pstmt3.setInt(9, openAmtInv.getInvoice().getC_Currency_ID());
				if(openAmtInv.getInvoice().getSalesRep_ID() > 0 )
					pstmt3.setInt(10, openAmtInv.getInvoice().getSalesRep_ID());
				else 
					pstmt3.setNull(10, java.sql.Types.INTEGER);						
				pstmt3.setInt(11, openAmtInv.getInvoice().getC_PaymentTerm_ID());
				pstmt3.setInt(12, p_C_AcctSchema_ID);
				pstmt3.setInt(13, openAmtInv.getAccount_ID());
				pstmt3.setInt(14, openAmtInv.getInvoice().getC_DocTypeTarget_ID());
				pstmt3.setString(15, openAmtInv.getDocBaseType());
				pstmt3.setString(16, openAmtInv.getInvoice().getDocumentNo());
				pstmt3.setString(17, openAmtInv.getInvoice().getPOReference());
				pstmt3.setString(18, openAmtInv.getInvoice().getPaymentRule());
				pstmt3.setString(19, openAmtInv.getInvoice().getDocStatus());
				pstmt3.setTimestamp(20, openAmtInv.getInvoice().getDateInvoiced());
				pstmt3.setTimestamp(21, openAmtInv.getInvoice().getDateAcct());
				pstmt3.setString(22, openAmtInv.getInvoice().isPaid()==true? "Y": "N");
				pstmt3.setString(23, openAmtInv.getInvoice().isSOTrx()==true? "Y": "N");
				if(openAmtInv.getInvoice().isCreditMemo())
					pstmt3.setBigDecimal(24, openAmtInv.getInvoice().getGrandTotal().negate());
				else
					pstmt3.setBigDecimal(24, openAmtInv.getInvoice().getGrandTotal());
				pstmt3.setBigDecimal(25, openAmtInv.getOpenAmt());
				pstmt3.setBigDecimal(26, openAmtInv.getJP_OpenAmtPointOfTime());
				
				//Foreign currency conversion
				pstmt3.setInt(27, m_MAcctSchema.getC_Currency_ID());
				if(openAmtInv.getInvoice().isCreditMemo())
					pstmt3.setBigDecimal(28, openAmtInv.getJP_ExchangedGrandTotal().negate());
				else
					pstmt3.setBigDecimal(28, openAmtInv.getJP_ExchangedGrandTotal());				
				
				pstmt3.setBigDecimal(29, openAmtInv.getJP_ExchangedOpenAmt());			
				pstmt3.setBigDecimal(30, openAmtInv.getJP_ExchangedOpenAmtPOT());	
				pstmt3.setBigDecimal(31, openAmtInv.getRate());
				
				pstmt3.setTimestamp(32, p_JP_PointOfTime);
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

	
	private class OpemAmtInvoice
	{
		private MInvoice invoice = null;
		private String DocBaseType = null;
		private int JP_Corporation_ID = 0;
		private int Account_ID = 0;
		
		private BigDecimal OpenAmt = Env.ZERO;
		private BigDecimal JP_OpenAmtPointOfTime = Env.ZERO;
		private BigDecimal JP_ExchangedGrandTotal  = Env.ZERO;
		private BigDecimal JP_ExchangedOpenAmt  = Env.ZERO;
		private BigDecimal JP_ExchangedOpenAmtPOT  = Env.ZERO;
		private BigDecimal Rate = Env.ONE;
		
		public OpemAmtInvoice (MInvoice invoice, String DocBaseType,int JP_Corporation_ID, int Account_ID)
		{
			this.invoice = invoice;
			this.DocBaseType = DocBaseType;
			this.JP_Corporation_ID = JP_Corporation_ID;
			this.Account_ID = Account_ID;
			OpenAmt =invoice.getOpenAmt(true, null);
			JP_OpenAmtPointOfTime = JPiereInvoiceUtil.getOpenAmtPointOfTime(getCtx(), invoice, p_JP_PointOfTime, true, get_TrxName());
			
			
			if(invoice.getC_Currency_ID() == m_MAcctSchema.getC_Currency_ID())
			{
				JP_ExchangedGrandTotal = invoice.getGrandTotal();
				JP_ExchangedOpenAmt = OpenAmt;
				JP_ExchangedOpenAmtPOT = JP_OpenAmtPointOfTime;
				
			}else{
				JP_ExchangedGrandTotal= MConversionRate.convert (getCtx(),invoice.getGrandTotal(), invoice.getC_Currency_ID(), m_MAcctSchema.getC_Currency_ID(),
														invoice.getDateAcct(), invoice.getC_ConversionType_ID(), invoice.getAD_Client_ID(), invoice.getAD_Org_ID());
				JP_ExchangedOpenAmt= MConversionRate.convert (getCtx(), OpenAmt, invoice.getC_Currency_ID(), m_MAcctSchema.getC_Currency_ID(),
														invoice.getDateAcct(), invoice.getC_ConversionType_ID(), invoice.getAD_Client_ID(), invoice.getAD_Org_ID());
				JP_ExchangedOpenAmtPOT= MConversionRate.convert (getCtx(), JP_OpenAmtPointOfTime, invoice.getC_Currency_ID(), m_MAcctSchema.getC_Currency_ID(),
														invoice.getDateAcct(), invoice.getC_ConversionType_ID(), invoice.getAD_Client_ID(), invoice.getAD_Org_ID());
				Rate = MConversionRate.getRate(invoice.getC_Currency_ID(), m_MAcctSchema.getC_Currency_ID(), 
														invoice.getDateAcct(), invoice.getC_ConversionType_ID(), invoice.getAD_Client_ID(), invoice.getAD_Org_ID());
			}
		}
		
		public MInvoice getInvoice(){return invoice;}
		public String getDocBaseType(){return DocBaseType;}
		public int getJP_Corporation_ID(){return JP_Corporation_ID;}
		public int getAccount_ID(){return  Account_ID;}
		public BigDecimal getOpenAmt(){return OpenAmt;}
		public BigDecimal getJP_OpenAmtPointOfTime(){return JP_OpenAmtPointOfTime;}
		public BigDecimal getJP_ExchangedGrandTotal(){return JP_ExchangedGrandTotal;}
		public BigDecimal getJP_ExchangedOpenAmt(){return JP_ExchangedOpenAmt;}
		public BigDecimal getJP_ExchangedOpenAmtPOT(){return JP_ExchangedOpenAmtPOT;}
		public BigDecimal getRate(){return Rate;}
	}//private class OpemAmtInvoice
	
}//public class OpenAmtInvoiceListPointOfTime 

