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

package jpiere.base.plugin.org.compiere.acct;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Level;

import org.compiere.acct.Doc;
import org.compiere.acct.DocLine;
import org.compiere.acct.DocTax;
import org.compiere.acct.Fact;
import org.compiere.acct.FactLine;
import org.compiere.model.MAccount;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MBPartner;
import org.compiere.model.MBankAccount;
import org.compiere.model.MBankStatement;
import org.compiere.model.MBankStatementLine;
import org.compiere.model.MConversionRate;
import org.compiere.model.MPayment;
import org.compiere.model.MTax;
import org.compiere.util.DB;
import org.compiere.util.Env;

/**
 *  Post {@link MBankStatement} Documents.
 *  <pre>
 *  Table:              C_BankStatement (392)
 *  Document Types:     CMB
 *  </pre>
 *  @author Jorg Janke
 *  @version  $Id: Doc_Bank.java,v 1.3 2006/07/30 00:53:33 jjanke Exp $
 *  <p>
 *  FR [ 1840016 ] Avoid usage of clearing accounts - subject to C_AcctSchema.IsPostIfClearingEqual.<br/>
 *  Avoid posting if both accounts BankAsset and BankInTransit are equal.
 *  @author victor.perez@e-evolution.com, e-Evolution http://www.e-evolution.com
 * 				<li>FR [ 2520591 ] Support multiples calendar for Org
 * 				@see https://sourceforge.net/p/adempiere/feature-requests/631/
 *
 *  JPIERE-0012: Tax of Bank Statement
 *  JPIERE-0543: Tax Base Amt & Tax Amt To the Fact_Acct
 *  JPIERE-0553: Qualified　Invoice　Issuer
 *  JPIERE-0556: Add column to the Journal For legal compliance.
 *  JPIERE-0576: Apply currency rate in Payment at Bank Statement.
 *
 */
public class Doc_BankStatementJP extends Doc
{
	/**
	 *  Constructor
	 * 	@param as accounting schema
	 * 	@param rs record
	 * 	@param trxName trx
	 */
	public Doc_BankStatementJP (MAcctSchema as, ResultSet rs, String trxName)
	{
		super (as, MBankStatement.class, rs, DOCTYPE_BankStatement, trxName);
	}	//	Doc_Bank

	/** Bank Account			*/
	protected int			m_C_BankAccount_ID = 0;

	/** Contained Optional Tax Lines    */
	@SuppressWarnings("unused")
	private DocTax[]        m_taxes = null;

	/**
	 *  Load Specific Document Details
	 *  @return error message or null
	 */
	@Override
	protected String loadDocumentDetails ()
	{
		MBankStatement bs = (MBankStatement)getPO();
		setDateDoc(bs.getStatementDate());
		setDateAcct(bs.getDateAcct());

		m_C_BankAccount_ID = bs.getC_BankAccount_ID();
		//	Amounts
		setAmount(AMTTYPE_Gross, bs.getStatementDifference());

		//  Set Bank Account Info (Currency)
		MBankAccount ba = MBankAccount.get (getCtx(), m_C_BankAccount_ID);
		setC_Currency_ID (ba.getC_Currency_ID());

		//	Contained Objects
		p_lines = loadLines(bs);
		m_taxes = loadTaxes();

		if (log.isLoggable(Level.FINE)) log.fine("Lines=" + p_lines.length);
		return null;
	}   //  loadDocumentDetails

	/**
	 *	Load bank statement lines.
	 *	@param bs bank statement
	 *  <pre>
	 *  4 amounts
	 *  AMTTYPE_Payment
	 *  AMTTYPE_Statement2
	 *  AMTTYPE_Charge
	 *  AMTTYPE_Interest
	 *  </pre>
	 *  @return DocLine Array
	 */
	protected DocLine[] loadLines(MBankStatement bs)
	{
		ArrayList<DocLine_BankStatementJP> list = new ArrayList<DocLine_BankStatementJP>();
		MBankStatementLine[] lines = bs.getLines(false);
		for (int i = 0; i < lines.length; i++)
		{
			MBankStatementLine line = lines[i];
			if(line.isActive())
			{
				DocLine_BankStatementJP docLine = new DocLine_BankStatementJP(line, this);
				
				//JPIERE-0576: Apply currency rate in Payment.
				if(line.getC_Payment_ID() != 0)
				{
					MPayment payment = new MPayment (getCtx(), line.getC_Payment_ID(), getTrxName());
					int C_ConversionType_ID = payment.getC_ConversionType_ID();
					docLine.setC_ConversionType_ID(C_ConversionType_ID);
					if (payment.isOverrideCurrencyRate())
					{
						docLine.setCurrencyRate(payment.getCurrencyRate());
					}else {
						
						BigDecimal rate = MConversionRate.getRate(payment.getC_Currency_ID(),getAcctSchema().getC_Currency_ID(),payment.getDateAcct(),C_ConversionType_ID,getAD_Client_ID(),payment.getAD_Org_ID());
						docLine.setCurrencyRate(rate);
					}
				}//JPIERE-0576
				
				list.add(docLine);
			}
		}

		//	Return Array
		DocLine_BankStatementJP[] dls = new DocLine_BankStatementJP[list.size()];
		list.toArray(dls);
		return dls;
	}	//	loadLines

	/**
	 *	Load Taxes
	 *  @return DocTax Array
	 */
	private DocTax[] loadTaxes()
	{
		ArrayList<DocTax> list = new ArrayList<DocTax>();
		String sql = "SELECT bst.C_Tax_ID, t.Name, t.Rate, bst.TaxBaseAmt, bst.TaxAmt, t.IsSalesTax,bst.C_BankStatementLine_ID "
				+ "FROM C_Tax t, JP_BankStatementTax bst "
				+ "WHERE t.C_Tax_ID=bst.C_Tax_ID AND bst.C_BankStatement_ID=?";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, getTrxName());
			pstmt.setInt(1, get_ID());
			rs = pstmt.executeQuery();
			//
			while (rs.next())
			{
				int C_Tax_ID = rs.getInt(1);
				String name = rs.getString(2);
				BigDecimal rate = rs.getBigDecimal(3);
				BigDecimal taxBaseAmt = rs.getBigDecimal(4);
				BigDecimal amount = rs.getBigDecimal(5);
				boolean salesTax = "Y".equals(rs.getString(6));
				int C_BankStatementLine_ID = rs.getInt(7);
				//
				DocTax docTax = new DocTax(C_Tax_ID, name, rate, taxBaseAmt, amount, salesTax);
				list.add(docTax);

				for(int i = 0; i < p_lines.length; i++){
					DocLine_BankStatementJP docLine = (DocLine_BankStatementJP)p_lines[i];
					if(docLine.get_ID() == C_BankStatementLine_ID){
						docLine.setDocTax(docTax);
						docLine.setTaxBaseAmt(taxBaseAmt);
						docLine.setTaxAmt(amount);
						break;
					}

				}
			}
		}
		catch (SQLException e)
		{
			log.log(Level.SEVERE, sql, e);
			return null;
		}
		finally {
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}

		//	Return Array
		DocTax[] tl = new DocTax[list.size()];
		list.toArray(tl);
		return tl;
	}	//	loadTaxes


	/**************************************************************************
	 *  Get Source Currency Balance - subtracts line amounts from total - no rounding
	 *  @return positive amount, if total invoice is bigger than lines
	 */
	@Override
	public BigDecimal getBalance()
	{
		BigDecimal retValue = Env.ZERO;
		StringBuilder sb = new StringBuilder (" [");
		//  Total
		retValue = retValue.add(getAmount(Doc.AMTTYPE_Gross));
		sb.append(getAmount(Doc.AMTTYPE_Gross));
		//  - Lines
		for (int i = 0; i < p_lines.length; i++)
		{
			BigDecimal lineBalance = ((DocLine_BankStatementJP)p_lines[i]).getStmtAmt();
			retValue = retValue.subtract(lineBalance);
			sb.append("-").append(lineBalance);
		}
		sb.append("]");
		//
		if (log.isLoggable(Level.FINE)) log.fine(toString() + " Balance=" + retValue + sb.toString());
		return retValue;
	}   //  getBalance

	/**
	 *  Create Facts (the accounting logic) for
	 *  CMB.
	 *  <pre>
	 *      BankAsset       DR      CR  (Statement)
	 *      BankInTransit   DR      CR  (Payment)
	 *      Charge          DR          (Charge)
	 *      Interest        DR      CR  (Interest)
	 *  </pre>
	 *  @param as accounting schema
	 *  @return Fact
	 */
	@Override
	public ArrayList<Fact> createFacts (MAcctSchema as)
	{
		//  create Fact Header
		Fact fact = new Fact(this, as, Fact.POST_Actual);
		// boolean isInterOrg = isInterOrg(as);

		//  Header -- there may be different currency amounts

		FactLine fl = null;
		int AD_Org_ID = getBank_Org_ID();	//	Bank Account Org
		//  Lines
		for (int i = 0; i < p_lines.length; i++)
		{
			DocLine_BankStatementJP line = (DocLine_BankStatementJP)p_lines[i];
			int C_BPartner_ID = line.getC_BPartner_ID();

			// Avoid usage of clearing accounts
			// If both accounts BankAsset and BankInTransit are equal
			// then remove the posting

			MAccount acct_bank_asset =  getAccount(Doc.ACCTTYPE_BankAsset, as);
			MAccount acct_bank_in_transit = getAccount(Doc.ACCTTYPE_BankInTransit, as);

			// don't validate interorg on banks for this - normally banks are balanced by orgs
			if ((!as.isPostIfClearingEqual()) && acct_bank_asset.equals(acct_bank_in_transit)) {
				// Not using clearing accounts
				// just post the difference (if any)

				BigDecimal amt_stmt_minus_trx = line.getStmtAmt().subtract(line.getTrxAmt());
				if (amt_stmt_minus_trx.compareTo(Env.ZERO) != 0) {

					//  BankAsset       DR      CR  (Statement minus Payment)
					fl = fact.createLine(line,
						getAccount(Doc.ACCTTYPE_BankAsset, as),
						line.getC_Currency_ID(), amt_stmt_minus_trx);
					if (fl != null && AD_Org_ID != 0)
						fl.setAD_Org_ID(AD_Org_ID);
					if (fl != null && C_BPartner_ID != 0)
						fl.setC_BPartner_ID(C_BPartner_ID);
					
					setAccountInfo(fl);//JPIERE-0556
				}

			} else {

				// Normal Adempiere behavior -- unchanged if using clearing accounts

				//  BankAsset       DR      CR  (Statement)
				fl = fact.createLine(line,
					getAccount(Doc.ACCTTYPE_BankAsset, as),
					line.getC_Currency_ID(), line.getStmtAmt());
				if (fl != null && AD_Org_ID != 0)
					fl.setAD_Org_ID(AD_Org_ID);
				if (fl != null && C_BPartner_ID != 0)
					fl.setC_BPartner_ID(C_BPartner_ID);
				
				setAccountInfo(fl);//JPIERE-0556

				//  BankInTransit   DR      CR              (Payment)
				fl = fact.createLine(line,
					getAccount(Doc.ACCTTYPE_BankInTransit, as),
					line.getC_Currency_ID(), line.getTrxAmt().negate());
				if (fl != null)
				{
					if (C_BPartner_ID != 0)
						fl.setC_BPartner_ID(C_BPartner_ID);
					if (AD_Org_ID != 0)
						fl.setAD_Org_ID(AD_Org_ID);
					else
						fl.setAD_Org_ID(line.getAD_Org_ID(true)); // from payment
					
					setAccountInfo(fl);//JPIERE-0556
				}

			}
			// End Avoid usage of clearing accounts

			//JPIERE-0012 -- Calculate Tax of Bank Statemet Line 
			if (line.getChargeAmt().compareTo(Env.ZERO) > 0) 
			{
				if(line.getDocTax() == null)
				{
					fl = fact.createLine(line,
							line.getChargeAccount(as, line.getChargeAmt().negate()),
							line.getC_Currency_ID(), null, line.getChargeAmt());
					if(fl != null)
					{
						if(line.getC_Tax_ID() != 0)
						{
							fl.setC_Tax_ID(line.getC_Tax_ID());
							fl.set_ValueNoCheck("JP_SOPOType", "N");
							fl.set_ValueNoCheck("JP_TaxBaseAmt", Env.ZERO);
							fl.set_ValueNoCheck("JP_TaxAmt", Env.ZERO);
						}
						
						setAdditionalChargeInfo(fl,line);//JPIERE-0556
					}
					
				}else{
					
					if(line.getPO().get_Value("JP_SOPOType") == null || line.getPO().get_ValueAsString("JP_SOPOType").equals("S"))
					{
						fl = fact.createLine(line, line.getDocTax().getAccount(DocTax.ACCTTYPE_TaxDue, as),
								getC_Currency_ID(), null,line.getDocTax().getAmount());
						if(fl != null)
						{
							fl.setC_Tax_ID(line.getC_Tax_ID());
							fl.set_ValueNoCheck("JP_SOPOType", "S");
							fl.set_ValueNoCheck("JP_TaxBaseAmt", line.getTaxBaseAmt());
							fl.set_ValueNoCheck("JP_TaxAmt", line.getTaxAmt());
							
							setAccountInfo(fl);//JPIERE-0556
						}
	
						fl = fact.createLine(line,
								line.getChargeAccount(as, line.getChargeAmt().negate()),
								line.getC_Currency_ID(), null, line.getTaxBaseAmt());
						if(fl != null)
						{
							fl.setC_Tax_ID(line.getC_Tax_ID());
							fl.set_ValueNoCheck("JP_SOPOType", "S");
							fl.set_ValueNoCheck("JP_TaxBaseAmt", line.getTaxBaseAmt());
							fl.set_ValueNoCheck("JP_TaxAmt", line.getTaxAmt());
							
							setAdditionalChargeInfo(fl,line);//JPIERE-0556
						}
						
					}else {//ChargeAmt > 0 && JP_SOPOType == P
						
						//JPIERE-0553: Qualified　Invoice　Issuer
						MBPartner bp = null;
						if(C_BPartner_ID > 0)
						{
							bp = MBPartner.get(getCtx(), C_BPartner_ID);
						}
						
						boolean isSalesTax = MTax.get(line.getC_Tax_ID()).isSalesTax();
						fl = fact.createLine(line, line.getDocTax().getAccount(isSalesTax ? DocTax.ACCTTYPE_TaxExpense : DocTax.ACCTTYPE_TaxCredit, as),
								getC_Currency_ID(), null,line.getDocTax().getAmount().negate());
						if(fl != null)
						{
							fl.setC_Tax_ID(line.getC_Tax_ID());
							fl.set_ValueNoCheck("JP_SOPOType", "P");
							fl.set_ValueNoCheck("JP_TaxBaseAmt", line.getTaxBaseAmt());
							fl.set_ValueNoCheck("JP_TaxAmt", line.getTaxAmt());
							
							setAccountInfo(fl);//JPIERE-0556
							
							//JPIERE-0553: Qualified　Invoice　Issuer
							if(bp != null)
							{
								boolean IsQualifiedInvoiceIssuerJP = bp.get_ValueAsBoolean("IsQualifiedInvoiceIssuerJP");
								fl.set_ValueNoCheck("IsQualifiedInvoiceIssuerJP", false);	
								if(IsQualifiedInvoiceIssuerJP)
								{
									Object obj_RegisteredDateOfQII = bp.get_Value("JP_RegisteredDateOfQII");
									if(obj_RegisteredDateOfQII == null)
									{
										fl.set_ValueNoCheck("IsQualifiedInvoiceIssuerJP", IsQualifiedInvoiceIssuerJP);
										fl.set_ValueNoCheck("JP_RegisteredNumberOfQII", bp.get_Value("JP_RegisteredNumberOfQII"));
									}else {
										Timestamp JP_RegisteredDateOfQII = (Timestamp)obj_RegisteredDateOfQII;
										if(getDateAcct().compareTo(JP_RegisteredDateOfQII) >= 0)
										{
											fl.set_ValueNoCheck("IsQualifiedInvoiceIssuerJP", IsQualifiedInvoiceIssuerJP);
											fl.set_ValueNoCheck("JP_RegisteredNumberOfQII", bp.get_Value("JP_RegisteredNumberOfQII"));
										}
									}
								}//JPIERE-0553: 
							}
						}
	
						fl = fact.createLine(line,
								line.getChargeAccount(as, line.getChargeAmt().negate()),
								line.getC_Currency_ID(), null, line.getTaxBaseAmt().negate());
						if(fl != null)
						{
							fl.setC_Tax_ID(line.getC_Tax_ID());
							fl.set_ValueNoCheck("JP_SOPOType", "P");
							fl.set_ValueNoCheck("JP_TaxBaseAmt", line.getTaxBaseAmt());
							fl.set_ValueNoCheck("JP_TaxAmt", line.getTaxAmt());
							
							//JPIERE-0553: Qualified　Invoice　Issuer
							if(bp != null)
							{
								boolean IsQualifiedInvoiceIssuerJP = bp.get_ValueAsBoolean("IsQualifiedInvoiceIssuerJP");
								fl.set_ValueNoCheck("IsQualifiedInvoiceIssuerJP", false);	
								if(IsQualifiedInvoiceIssuerJP)
								{
									Object obj_RegisteredDateOfQII = bp.get_Value("JP_RegisteredDateOfQII");
									if(obj_RegisteredDateOfQII == null)
									{
										fl.set_ValueNoCheck("IsQualifiedInvoiceIssuerJP", IsQualifiedInvoiceIssuerJP);
										fl.set_ValueNoCheck("JP_RegisteredNumberOfQII", bp.get_Value("JP_RegisteredNumberOfQII"));
									}else {
										Timestamp JP_RegisteredDateOfQII = (Timestamp)obj_RegisteredDateOfQII;
										if(getDateAcct().compareTo(JP_RegisteredDateOfQII) >= 0)
										{
											fl.set_ValueNoCheck("IsQualifiedInvoiceIssuerJP", IsQualifiedInvoiceIssuerJP);
											fl.set_ValueNoCheck("JP_RegisteredNumberOfQII", bp.get_Value("JP_RegisteredNumberOfQII"));
										}
									}
								}//JPIERE-0553: 
							}
							
							setAdditionalChargeInfo(fl,line);//JPIERE-0556
						}
					}
				}
				
			} else {
				
				if(line.getDocTax() == null)
				{
					fl = fact.createLine(line,
							line.getChargeAccount(as, line.getChargeAmt().negate()),
							line.getC_Currency_ID(), line.getChargeAmt().negate(), null);
					if(fl != null)
					{
						if(line.getC_Tax_ID() != 0)
						{
							fl.setC_Tax_ID(line.getC_Tax_ID());
							fl.set_ValueNoCheck("JP_SOPOType", "N");
							fl.set_ValueNoCheck("JP_TaxBaseAmt", Env.ZERO);
							fl.set_ValueNoCheck("JP_TaxAmt", Env.ZERO);
						}
						
						setAdditionalChargeInfo(fl,line);//JPIERE-0556
					}
					
				}else{
					
					if(line.getPO().get_Value("JP_SOPOType") == null || line.getPO().get_ValueAsString("JP_SOPOType").equals("P"))
					{
						//JPIERE-0553: Qualified　Invoice　Issuer
						MBPartner bp = null;
						if(C_BPartner_ID > 0)
						{
							bp = MBPartner.get(getCtx(), C_BPartner_ID);
						}
						
						boolean isSalesTax = MTax.get(line.getC_Tax_ID()).isSalesTax();
						fl = fact.createLine(line, line.getDocTax().getAccount(isSalesTax ? DocTax.ACCTTYPE_TaxExpense : DocTax.ACCTTYPE_TaxCredit, as),
								getC_Currency_ID(), line.getDocTax().getAmount(),  null);
						if(fl != null)
						{
							fl.setC_Tax_ID(line.getC_Tax_ID());
							fl.set_ValueNoCheck("JP_SOPOType", "P");
							fl.set_ValueNoCheck("JP_TaxBaseAmt", line.getTaxBaseAmt());
							fl.set_ValueNoCheck("JP_TaxAmt", line.getTaxAmt());
							
							setAccountInfo(fl);//JPIERE-0556
							
							//JPIERE-0553: Qualified　Invoice　Issuer
							if(bp != null)
							{
								boolean IsQualifiedInvoiceIssuerJP = bp.get_ValueAsBoolean("IsQualifiedInvoiceIssuerJP");
								fl.set_ValueNoCheck("IsQualifiedInvoiceIssuerJP", false);	
								if(IsQualifiedInvoiceIssuerJP)
								{
									Object obj_RegisteredDateOfQII = bp.get_Value("JP_RegisteredDateOfQII");
									if(obj_RegisteredDateOfQII == null)
									{
										fl.set_ValueNoCheck("IsQualifiedInvoiceIssuerJP", IsQualifiedInvoiceIssuerJP);
										fl.set_ValueNoCheck("JP_RegisteredNumberOfQII", bp.get_Value("JP_RegisteredNumberOfQII"));
									}else {
										Timestamp JP_RegisteredDateOfQII = (Timestamp)obj_RegisteredDateOfQII;
										if(getDateAcct().compareTo(JP_RegisteredDateOfQII) >= 0)
										{
											fl.set_ValueNoCheck("IsQualifiedInvoiceIssuerJP", IsQualifiedInvoiceIssuerJP);
											fl.set_ValueNoCheck("JP_RegisteredNumberOfQII", bp.get_Value("JP_RegisteredNumberOfQII"));
										}
									}
								}//JPIERE-0553: 
							}
						}
	
						fl = fact.createLine(line,
								line.getChargeAccount(as, line.getChargeAmt().negate()),
								line.getC_Currency_ID(), line.getTaxBaseAmt(), null);
						{
							fl.setC_Tax_ID(line.getC_Tax_ID());
							fl.set_ValueNoCheck("JP_SOPOType", "P");
							fl.set_ValueNoCheck("JP_TaxBaseAmt", line.getTaxBaseAmt());
							fl.set_ValueNoCheck("JP_TaxAmt", line.getTaxAmt());
							
							//JPIERE-0553: Qualified　Invoice　Issuer
							if(bp != null)
							{
								boolean IsQualifiedInvoiceIssuerJP = bp.get_ValueAsBoolean("IsQualifiedInvoiceIssuerJP");
								fl.set_ValueNoCheck("IsQualifiedInvoiceIssuerJP", false);	
								if(IsQualifiedInvoiceIssuerJP)
								{
									Object obj_RegisteredDateOfQII = bp.get_Value("JP_RegisteredDateOfQII");
									if(obj_RegisteredDateOfQII == null)
									{
										fl.set_ValueNoCheck("IsQualifiedInvoiceIssuerJP", IsQualifiedInvoiceIssuerJP);
										fl.set_ValueNoCheck("JP_RegisteredNumberOfQII", bp.get_Value("JP_RegisteredNumberOfQII"));
									}else {
										Timestamp JP_RegisteredDateOfQII = (Timestamp)obj_RegisteredDateOfQII;
										if(getDateAcct().compareTo(JP_RegisteredDateOfQII) >= 0)
										{
											fl.set_ValueNoCheck("IsQualifiedInvoiceIssuerJP", IsQualifiedInvoiceIssuerJP);
											fl.set_ValueNoCheck("JP_RegisteredNumberOfQII", bp.get_Value("JP_RegisteredNumberOfQII"));
										}
									}
								}//JPIERE-0553: 
							}
							
							setAdditionalChargeInfo(fl,line);//JPIERE-0556
						}
						
					}else {//ChargeAmt < 0 && JP_SOPOType == S

						fl = fact.createLine(line, line.getDocTax().getAccount(DocTax.ACCTTYPE_TaxDue, as),
								getC_Currency_ID(), line.getDocTax().getAmount().negate(),  null);
						if(fl != null)
						{
							fl.setC_Tax_ID(line.getC_Tax_ID());
							fl.set_ValueNoCheck("JP_SOPOType", "S");
							fl.set_ValueNoCheck("JP_TaxBaseAmt", line.getTaxBaseAmt());
							fl.set_ValueNoCheck("JP_TaxAmt", line.getTaxAmt());
							
							setAccountInfo(fl);//JPIERE-0556
						}
	
						fl = fact.createLine(line,
								line.getChargeAccount(as, line.getChargeAmt().negate()),
								line.getC_Currency_ID(), line.getTaxBaseAmt().negate(), null);
						{
							fl.setC_Tax_ID(line.getC_Tax_ID());
							fl.set_ValueNoCheck("JP_SOPOType", "S");
							fl.set_ValueNoCheck("JP_TaxBaseAmt", line.getTaxBaseAmt());
							fl.set_ValueNoCheck("JP_TaxAmt", line.getTaxAmt());
							
							setAdditionalChargeInfo(fl,line);//JPIERE-0556
						}
					}
				}
			}
			
			
			
			if (fl != null && C_BPartner_ID != 0)
				fl.setC_BPartner_ID(C_BPartner_ID);

			//  Interest        DR      CR  (Interest)
			if (line.getInterestAmt().signum() < 0)
				fl = fact.createLine(line,
					getAccount(Doc.ACCTTYPE_InterestExp, as), getAccount(Doc.ACCTTYPE_InterestExp, as),
					line.getC_Currency_ID(), line.getInterestAmt().negate());
			else
				fl = fact.createLine(line,
					getAccount(Doc.ACCTTYPE_InterestRev, as), getAccount(Doc.ACCTTYPE_InterestRev, as),
					line.getC_Currency_ID(), line.getInterestAmt().negate());
			if (fl != null && C_BPartner_ID != 0)
				fl.setC_BPartner_ID(C_BPartner_ID);
			
			setAccountInfo(fl);//JPIERE-0556
			//
		//	fact.createTaxCorrection();
		}
		//
		ArrayList<Fact> facts = new ArrayList<Fact>();
		facts.add(fact);
		return facts;
	}   //  createFact

	/** Verify if the posting involves two or more organizations
	@return true if there are more than one org involved on the posting
	private boolean isInterOrg(MAcctSchema as) {
		MAcctSchemaElement elementorg = as.getAcctSchemaElement(MAcctSchemaElement.ELEMENTTYPE_Organization);
		if (elementorg == null || !elementorg.isBalanced()) {
			// no org element or not need to be balanced
			return false;
		}

		if (p_lines.length <= 0) {
			// no lines
			return false;
		}

		int startorg = getBank_Org_ID();
		if (startorg == 0)
			startorg = p_lines[0].getAD_Org_ID();
		// validate if the allocation involves more than one org
		for (int i = 0; i < p_lines.length; i++) {
			if (p_lines[i].getAD_Org_ID() != startorg)
				return true;
		}

		return false;
	}
	 */

	/**
	 * 	Get AD_Org_ID from Bank Account
	 * 	@return AD_Org_ID or 0
	 */
	protected int getBank_Org_ID ()
	{
		if (m_C_BankAccount_ID == 0)
			return 0;
		//
		MBankAccount ba = MBankAccount.get(getCtx(), m_C_BankAccount_ID);
		return ba.getAD_Org_ID();
	}	//	getBank_Org_ID

	private void setAdditionalChargeInfo(FactLine fl,DocLine_BankStatementJP line)
	{
		setAccountInfo(fl);
		fl.set_ValueNoCheck("JP_Charge_ID", line.getPO().get_Value("C_Charge_ID"));
		fl.set_ValueNoCheck("JP_PriceActual" ,line.getPO().get_Value("JP_PriceActual"));;
		fl.set_ValueNoCheck("Qty" ,line.getPO().get_Value("JP_Qty"));
		fl.set_ValueNoCheck("M_Product_ID" ,line.getPO().get_Value("JP_Product_ID"));
		fl.set_ValueNoCheck("C_UOM_ID" ,line.getPO().get_Value("JP_UOM_ID"));
		return;
	}
	
	private void setAccountInfo(FactLine fl)
	{
		if(fl != null)
			fl.set_ValueNoCheck("JP_BankAccount_ID", m_C_BankAccount_ID);
	}
}   //  Doc_Bank
