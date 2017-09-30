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
import java.sql.ResultSet;
import java.util.ArrayList;

import org.compiere.acct.Doc;
import org.compiere.acct.DocLine;
import org.compiere.acct.DocTax;
import org.compiere.acct.Doc_Invoice;
import org.compiere.acct.Fact;
import org.compiere.acct.FactLine;
import org.compiere.model.MAccount;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.ProductCost;
import org.compiere.util.Env;

import jpiere.base.plugin.org.adempiere.model.MContractAcct;
import jpiere.base.plugin.org.adempiere.model.MContractBPAcct;
import jpiere.base.plugin.org.adempiere.model.MContractChargeAcct;
import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractProductAcct;
import jpiere.base.plugin.org.adempiere.model.MContractTaxAcct;

/**
*  JPIERE-0363
*
* @author Hideaki Hagiwara
*
*/
public class Doc_InvoiceJP extends Doc_Invoice {
	
	public Doc_InvoiceJP(MAcctSchema as, ResultSet rs, String trxName) 
	{
		super(as, rs, trxName);
	}

	@Override
	public ArrayList<Fact> createFacts(MAcctSchema as) 
	{
		if (!as.isAccrual())
			return super.createFacts(as);
		
		
		MInvoice invoice = (MInvoice)getPO();
		
		
		/**iDempiere Standard Posting*/
		int JP_ContractContent_ID = invoice.get_ValueAsInt("JP_ContractContent_ID");
		if(JP_ContractContent_ID == 0)
		{
			return super.createFacts(as);
		}
		
		MContractContent contractContent = MContractContent.get(getCtx(), JP_ContractContent_ID);
		if(contractContent.getJP_Contract_Acct_ID() == 0)
		{
			return super.createFacts(as);
		}
		
		MContractAcct contractAcct = MContractAcct.get(Env.getCtx(),contractContent.getJP_Contract_Acct_ID());
		if(!contractAcct.isPostingContractAcctJP())
		{
			return super.createFacts(as);
		}
		
		
		/**JPiere Posting Logic*/
		ArrayList<Fact> facts = new ArrayList<Fact>();
		//  create Fact Header
		Fact fact = new Fact(this, as, Fact.POST_Actual);
		
		//  ** ARI, ARF
		if (getDocumentType().equals(DOCTYPE_ARInvoice)
			|| getDocumentType().equals(DOCTYPE_ARProForma))
		{
			postARI(as, contractAcct, fact);
		}
		//  ARC
		else if (getDocumentType().equals(DOCTYPE_ARCredit))
		{
			postARC(as, contractAcct, fact);
		}
		//  ** API
		else if (getDocumentType().equals(DOCTYPE_APInvoice))
		{
			postAPI(as, contractAcct, fact);//TODO
		}
		//  APC
		else if (getDocumentType().equals(DOCTYPE_APCredit))
		{
			postAPC(as, contractAcct, fact);//TODO
		}
		
		facts.add(fact);
		return facts;
	}
	
	
	private void postARI(MAcctSchema as, MContractAcct contractAcct, Fact fact)
	{
		BigDecimal amt = Env.ZERO;
		//CR : TaxDue
		MContractTaxAcct taxAcct = null;
		for (int i = 0; i < m_taxes.length; i++)
		{
			amt = m_taxes[i].getAmount();
			if (amt != null && amt.signum() != 0)
			{
				//CR
				FactLine tl = null;
				 taxAcct = contractAcct.getContracTaxAcct(m_taxes[i].getC_Tax_ID(), as.getC_AcctSchema_ID(),false);
				if(taxAcct == null || taxAcct.getT_Due_Acct() == 0 )
				{
					tl = fact.createLine(null, m_taxes[i].getAccount(DocTax.ACCTTYPE_TaxDue, as),getC_Currency_ID(), null, amt);
				}else{
					tl = fact.createLine(null, MAccount.get(getCtx(), taxAcct.getT_Due_Acct()), getC_Currency_ID(), null, amt);
				}
				
				if (tl != null)
					tl.setC_Tax_ID(m_taxes[i].getC_Tax_ID());	;
			}//if
		}//for
		
		//CR : Revenue
		for (int i = 0; i < p_lines.length; i++)
		{
			amt = p_lines[i].getAmtSource();
			BigDecimal dAmt = null;
			//DR : Posting Trade Discount
			if (as.isTradeDiscountPosted())
			{
				BigDecimal discount = p_lines[i].getDiscount();
				if (discount != null && discount.signum() != 0)
				{
					amt = amt.add(discount);
					dAmt = discount;
					fact.createLine (p_lines[i], 
							getTDiscountGrantValidCombination(p_lines[i], contractAcct,  as),
							getC_Currency_ID(), dAmt, null);
				}
			}
			
			//CR : Revenue
			fact.createLine (p_lines[i], 
					getRevenueValidCombination(p_lines[i], contractAcct,  as), 
					getC_Currency_ID(), null, amt);
			
		}//For
		
		//  Set Locations
		FactLine[] fLines = fact.getLines();
		for (int i = 0; i < fLines.length; i++)
		{
			if (fLines[i] != null)
			{
				fLines[i].setLocationFromOrg(fLines[i].getAD_Org_ID(), true);      //  from Loc
				fLines[i].setLocationFromBPartner(getC_BPartner_Location_ID(), false);  //  to Loc
			}
		}

		//DR : Receivables
		int receivables_ID = getReceivableValidCombination_ID(contractAcct,  as);
		BigDecimal grossAmt = getAmount(Doc.AMTTYPE_Gross);
		if (grossAmt.signum() != 0)
			fact.createLine(null, MAccount.get(getCtx(), receivables_ID),getC_Currency_ID(), grossAmt, null);
	}
	
	
	private void postARC(MAcctSchema as, MContractAcct contractAcct, Fact fact)
	{
		BigDecimal amt = Env.ZERO;
		//DR :  TaxDue                  
		MContractTaxAcct taxAcct = null;
		for (int i = 0; i < m_taxes.length; i++)
		{
			amt = m_taxes[i].getAmount();
			if (amt != null && amt.signum() != 0)
			{
				//DR
				FactLine tl = null;
				 taxAcct = contractAcct.getContracTaxAcct(m_taxes[i].getC_Tax_ID(), as.getC_AcctSchema_ID(),false);
				if(taxAcct == null || taxAcct.getT_Due_Acct() == 0 )
				{
					tl = fact.createLine(null, m_taxes[i].getAccount(DocTax.ACCTTYPE_TaxDue, as),getC_Currency_ID(), amt, null);
				}else{
					tl = fact.createLine(null, MAccount.get(getCtx(), taxAcct.getT_Due_Acct()), getC_Currency_ID(), amt, null);
				}
				
				if (tl != null)
					tl.setC_Tax_ID(m_taxes[i].getC_Tax_ID());	;
			}//if
		}//for
		
		//DR :  Revenue 
		for (int i = 0; i < p_lines.length; i++)
		{
			amt = p_lines[i].getAmtSource();
			BigDecimal dAmt = null;
			//CR:Posting Trade Discount
			if (as.isTradeDiscountPosted())
			{
				BigDecimal discount = p_lines[i].getDiscount();
				if (discount != null && discount.signum() != 0)
				{
					amt = amt.add(discount);
					dAmt = discount;
					fact.createLine (p_lines[i], 
							getTDiscountGrantValidCombination(p_lines[i], contractAcct,  as),
							getC_Currency_ID(), null, dAmt);
				}
			}
			
			//DR : Revenue
			fact.createLine (p_lines[i], 
					getRevenueValidCombination(p_lines[i], contractAcct,  as), 
					getC_Currency_ID(), amt, null);
			
		}//For
		
		//  Set Locations
		FactLine[] fLines = fact.getLines();
		for (int i = 0; i < fLines.length; i++)
		{
			if (fLines[i] != null)
			{
				fLines[i].setLocationFromOrg(fLines[i].getAD_Org_ID(), true);      //  from Loc
				fLines[i].setLocationFromBPartner(getC_BPartner_Location_ID(), false);  //  to Loc
			}
		}

		//CR : Receivables
		int receivables_ID = getReceivableValidCombination_ID(contractAcct,  as);
		BigDecimal grossAmt = getAmount(Doc.AMTTYPE_Gross);
		if (grossAmt.signum() != 0)
			fact.createLine(null, MAccount.get(getCtx(), receivables_ID),
				getC_Currency_ID(), null, grossAmt);
	}
	
	private void postAPI(MAcctSchema as, MContractAcct contractAcct, Fact fact)
	{
		;//TODO 実装
	}
	
	private void postAPC(MAcctSchema as, MContractAcct contractAcct, Fact fact)
	{
		;//TODO 実装
	}
	
	/**
	 * 
	 * @param docLine
	 * @param contractAcct
	 * @param C_AcctSchema_ID
	 * @return
	 */	
	private MAccount getRevenueValidCombination(DocLine docLine, MContractAcct contractAcct,  MAcctSchema as)
	{
		MInvoiceLine line = (MInvoiceLine)docLine.getPO();
		//	Charge Account
		if (line.getM_Product_ID() == 0 && line.getC_Charge_ID() != 0)
		{
			MContractChargeAcct cAcct = contractAcct.getContracChargeAcct(line.getC_Charge_ID(), as.getC_AcctSchema_ID(), false);
			if(cAcct == null || cAcct.getCh_Expense_Acct() == 0)
			{
				return docLine.getAccount(ProductCost.ACCTTYPE_P_Revenue, as);
			}else{
				
				return MAccount.get(getCtx(), cAcct.getCh_Expense_Acct()) ;
			}
			
		
		}else{
			
			MContractProductAcct pAcct = contractAcct.getContractProductAcct(line.getM_Product().getM_Product_Category_ID(), as.getC_AcctSchema_ID(), false);
			if(pAcct == null || pAcct.getP_Revenue_Acct() == 0)
			{
				return docLine.getAccount(ProductCost.ACCTTYPE_P_Revenue, as);
			}else{
				return MAccount.get(getCtx(),pAcct.getP_Revenue_Acct()) ;
			}
			
		}
		
	}
	
	
	private MAccount getTDiscountGrantValidCombination(DocLine docLine, MContractAcct contractAcct,  MAcctSchema as)
	{
		MInvoiceLine line = (MInvoiceLine)docLine.getPO();
		if (line.getM_Product_ID() == 0 && line.getC_Charge_ID() != 0)
		{
			MContractChargeAcct cAcct = contractAcct.getContracChargeAcct(line.getC_Charge_ID(), as.getC_AcctSchema_ID(), false);
			if(cAcct == null || cAcct.getCh_Expense_Acct() == 0)
			{
				return docLine.getAccount(ProductCost.ACCTTYPE_P_TDiscountGrant, as);
				
				
			}else{
				return MAccount.get(getCtx(),cAcct.getCh_Expense_Acct());
			}
		}else {
			MContractProductAcct pAcct = contractAcct.getContractProductAcct(line.getM_Product().getM_Product_Category_ID(), as.getC_AcctSchema_ID(), false);
			if(pAcct == null || pAcct.getP_Revenue_Acct() == 0)
			{
				return docLine.getAccount(ProductCost.ACCTTYPE_P_TDiscountGrant, as);
			}else{
				return MAccount.get(getCtx(),pAcct.getP_TradeDiscountGrant_Acct()) ;
			}
			
		}
		
	}
	
	
	private int getReceivableValidCombination_ID(MContractAcct contractAcct,  MAcctSchema as)
	{
		MContractBPAcct bpAcct = contractAcct.getContractBPAcct(as.getC_AcctSchema_ID(), false);
		if(bpAcct == null || bpAcct.getC_Receivable_Acct() == 0)
		{
			return getValidCombination_ID(Doc.ACCTTYPE_C_Receivable, as);
		}else{
			return bpAcct.getC_Receivable_Acct();
		}
	}
}
