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
import java.util.logging.Level;

import org.compiere.acct.Doc;
import org.compiere.acct.DocLine;
import org.compiere.acct.DocTax;
import org.compiere.acct.Doc_Invoice;
import org.compiere.acct.Fact;
import org.compiere.acct.FactLine;
import org.compiere.model.MAccount;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MCostDetail;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MTax;
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
	protected String loadDocumentDetails() 
	{
		return super.loadDocumentDetails();
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
		else
		{
			p_Error = "DocumentType unknown: " + getDocumentType();
			log.log(Level.SEVERE, p_Error);
			fact = null;
		}
		
		
		facts.add(fact);
		return facts;
	}
	
	
	private void postARI(MAcctSchema as, MContractAcct contractAcct, Fact fact)
	{
		BigDecimal amt = Env.ZERO;

		//DR :  TaxDue  
		for (int i = 0; i < m_taxes.length; i++)
		{
			amt = m_taxes[i].getAmount();
			if (amt != null && amt.signum() != 0)
			{
				//CR
				FactLine tl = null;
				tl = fact.createLine(null, getInvoiceTaxDueAccount(m_taxes[i], contractAcct, as), getC_Currency_ID(), null, amt);				
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
							getInvoiceTDiscountGrantAccount(p_lines[i], contractAcct,  as),
							getC_Currency_ID(), dAmt, null);
				}
			}
			
			//CR : Revenue
			fact.createLine (p_lines[i], 
					getInvoiceRevenueAccount(p_lines[i], contractAcct,  as), 
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
		BigDecimal grossAmt = getAmount(Doc.AMTTYPE_Gross);
		if (grossAmt.signum() != 0)
			fact.createLine(null, getReceivableAccount(contractAcct,  as),getC_Currency_ID(), grossAmt, null);
	}
	
	
	private void postARC(MAcctSchema as, MContractAcct contractAcct, Fact fact)
	{
		BigDecimal amt = Env.ZERO;
		
		//DR :  TaxDue                  
		for (int i = 0; i < m_taxes.length; i++)
		{
			amt = m_taxes[i].getAmount();
			if (amt != null && amt.signum() != 0)
			{
				//DR
				FactLine tl = null;
				tl = fact.createLine(null,getInvoiceTaxDueAccount(m_taxes[i], contractAcct, as), getC_Currency_ID(), amt, null);
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
							getInvoiceTDiscountGrantAccount(p_lines[i], contractAcct,  as),
							getC_Currency_ID(), null, dAmt);
				}
			}
			
			//DR : Revenue
			fact.createLine (p_lines[i], 
					getInvoiceRevenueAccount(p_lines[i], contractAcct,  as), 
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
		BigDecimal grossAmt = getAmount(Doc.AMTTYPE_Gross);
		if (grossAmt.signum() != 0)
			fact.createLine(null, getReceivableAccount(contractAcct,  as), getC_Currency_ID(), null, grossAmt);
	}
	
	private void postAPI(MAcctSchema as, MContractAcct contractAcct, Fact fact)
	{
		BigDecimal grossAmt = getAmount(Doc.AMTTYPE_Gross);
		BigDecimal serviceAmt = Env.ZERO;

		//  TaxCredit       DR
		for (int i = 0; i < m_taxes.length; i++)
		{
			FactLine tl = fact.createLine(null, getInvoiceTaxCreditAccount(m_taxes[i], contractAcct, as),
				getC_Currency_ID(), m_taxes[i].getAmount(), null);
			if (tl != null)
				tl.setC_Tax_ID(m_taxes[i].getC_Tax_ID());
		}
		//  Expense         DR
		for (int i = 0; i < p_lines.length; i++)
		{
			DocLine line = p_lines[i];
			boolean landedCost = landedCost(as, fact, line, true);
			if (landedCost && as.isExplicitCostAdjustment())
			{
				fact.createLine (line, getInvoiceExpenseAccount(line, contractAcct, as),
					getC_Currency_ID(), line.getAmtSource(), null);
				//
				FactLine fl = fact.createLine (line,  getInvoiceExpenseAccount(line, contractAcct, as),
					getC_Currency_ID(), null, line.getAmtSource());
				String desc = line.getDescription();
				if (desc == null)
					desc = "100%";
				else
					desc += " 100%";
				fl.setDescription(desc);
			}
			if (!landedCost)
			{
				MAccount expense =  getInvoiceExpenseAccount(line, contractAcct, as);
				BigDecimal amt = line.getAmtSource();
				BigDecimal dAmt = null;
				if (as.isTradeDiscountPosted() && !line.isItem())
				{
					BigDecimal discount = line.getDiscount();
					if (discount != null && discount.signum() != 0)
					{
						amt = amt.add(discount);
						dAmt = discount;
						MAccount tradeDiscountReceived = getInvoiceTDiscountRecAccount(line, contractAcct, as);
						fact.createLine (line, tradeDiscountReceived,
								getC_Currency_ID(), null, dAmt);
					}
				}
				fact.createLine (line, expense,
					getC_Currency_ID(), amt, null);
				if (!line.isItem())
				{
					grossAmt = grossAmt.subtract(amt);
					serviceAmt = serviceAmt.add(amt);
				}
				//
				if (line.getM_Product_ID() != 0
					&& line.getProduct().isService())	//	otherwise Inv Matching
					MCostDetail.createInvoice(as, line.getAD_Org_ID(),
						line.getM_Product_ID(), line.getM_AttributeSetInstance_ID(),
						line.get_ID(), 0,		//	No Cost Element
						line.getAmtSource(), line.getQty(),
						line.getDescription(), getTrxName());
			}
		}
		//  Set Locations
		FactLine[] fLines = fact.getLines();
		for (int i = 0; i < fLines.length; i++)
		{
			if (fLines[i] != null)
			{
				fLines[i].setLocationFromBPartner(getC_BPartner_Location_ID(), true);  //  from Loc
				fLines[i].setLocationFromOrg(fLines[i].getAD_Org_ID(), false);    //  to Loc
			}
		}

		//  Liability               CR
		grossAmt = getAmount(Doc.AMTTYPE_Gross);
		if (grossAmt.signum() != 0)
			fact.createLine(null, getPayableAccount(contractAcct, as),
				getC_Currency_ID(), null, grossAmt);

		//
		updateProductPO(as);	//	Only API
	}
	
	private void postAPC(MAcctSchema as, MContractAcct contractAcct, Fact fact)
	{
		BigDecimal grossAmt = getAmount(Doc.AMTTYPE_Gross);
		BigDecimal serviceAmt = Env.ZERO;

		//  TaxCredit               CR
		for (int i = 0; i < m_taxes.length; i++)
		{
			FactLine tl = fact.createLine (null, getInvoiceTaxCreditAccount(m_taxes[i], contractAcct, as),
				getC_Currency_ID(), null, m_taxes[i].getAmount());
			if (tl != null)
				tl.setC_Tax_ID(m_taxes[i].getC_Tax_ID());
		}
		//  Expense                 CR
		for (int i = 0; i < p_lines.length; i++)
		{
			DocLine line = p_lines[i];
			boolean landedCost = landedCost(as, fact, line, false);
			if (landedCost && as.isExplicitCostAdjustment())
			{
				fact.createLine (line, getInvoiceExpenseAccount(line, contractAcct, as),
					getC_Currency_ID(), null, line.getAmtSource());
				//
				FactLine fl = fact.createLine (line,getInvoiceExpenseAccount(line, contractAcct, as),
					getC_Currency_ID(), line.getAmtSource(), null);
				String desc = line.getDescription();
				if (desc == null)
					desc = "100%";
				else
					desc += " 100%";
				fl.setDescription(desc);
			}
			if (!landedCost)
			{
				MAccount expense = getInvoiceExpenseAccount(line, contractAcct, as);
				BigDecimal amt = line.getAmtSource();
				BigDecimal dAmt = null;
				if (as.isTradeDiscountPosted() && !line.isItem())
				{
					BigDecimal discount = line.getDiscount();
					if (discount != null && discount.signum() != 0)
					{
						amt = amt.add(discount);
						dAmt = discount;
						MAccount tradeDiscountReceived = getInvoiceTDiscountRecAccount(line, contractAcct, as);
						fact.createLine (line, tradeDiscountReceived,
								getC_Currency_ID(), dAmt, null);
					}
				}
				fact.createLine (line, expense,
					getC_Currency_ID(), null, amt);
				if (!line.isItem())
				{
					grossAmt = grossAmt.subtract(amt);
					serviceAmt = serviceAmt.add(amt);
				}
				//
				if (line.getM_Product_ID() != 0
					&& line.getProduct().isService())	//	otherwise Inv Matching
					MCostDetail.createInvoice(as, line.getAD_Org_ID(),
						line.getM_Product_ID(), line.getM_AttributeSetInstance_ID(),
						line.get_ID(), 0,		//	No Cost Element
						line.getAmtSource().negate(), line.getQty(),
						line.getDescription(), getTrxName());
			}
		}
		//  Set Locations
		FactLine[] fLines = fact.getLines();
		for (int i = 0; i < fLines.length; i++)
		{
			if (fLines[i] != null)
			{
				fLines[i].setLocationFromBPartner(getC_BPartner_Location_ID(), true);  //  from Loc
				fLines[i].setLocationFromOrg(fLines[i].getAD_Org_ID(), false);    //  to Loc
			}
		}
		//  Liability       DR
		grossAmt = getAmount(Doc.AMTTYPE_Gross);
	
		if (grossAmt.signum() != 0)
			fact.createLine(null, getPayableAccount(contractAcct, as),
				getC_Currency_ID(), grossAmt, null);

	}
	

	
	/**
	 * 
	 * 
	 * @param docLine
	 * @param contractAcct
	 * @param as
	 * @return
	 */
	private MAccount getInvoiceRevenueAccount(DocLine docLine, MContractAcct contractAcct,  MAcctSchema as)
	{

		MInvoiceLine line = (MInvoiceLine)docLine.getPO();
		//Charge Account
		if (line.getM_Product_ID() == 0 && line.getC_Charge_ID() != 0)
		{
			MContractChargeAcct contractChargeAcct =  contractAcct.getContracChargeAcct(line.getC_Charge_ID(), as.getC_AcctSchema_ID(), false);
			if(contractChargeAcct != null && contractChargeAcct.getCh_Expense_Acct() > 0)
			{
				return MAccount.get(getCtx(), contractChargeAcct.getCh_Expense_Acct());
			}else{
				return docLine.getAccount (ProductCost.ACCTTYPE_P_Revenue, as);
			}
			
		}else if(line.getM_Product_ID() > 0){
			MContractProductAcct contractProductAcct = contractAcct.getContractProductAcct(line.getM_Product().getM_Product_Category_ID(), as.getC_AcctSchema_ID(), false);
			if(contractProductAcct != null && contractProductAcct.getP_Revenue_Acct() > 0)
			{
				return MAccount.get(getCtx(),contractProductAcct.getP_Revenue_Acct());
			}else{
				return docLine.getAccount (ProductCost.ACCTTYPE_P_Revenue, as);
			}
		}else{
			return docLine.getAccount (ProductCost.ACCTTYPE_P_Revenue, as);
		}
	}
	
	private MAccount getInvoiceExpenseAccount(DocLine docLine, MContractAcct contractAcct,  MAcctSchema as)
	{
		MInvoiceLine line = (MInvoiceLine)docLine.getPO();
		//Charge Account
		if (line.getM_Product_ID() == 0 && line.getC_Charge_ID() != 0)
		{
			MContractChargeAcct contractChargeAcct =  contractAcct.getContracChargeAcct(line.getC_Charge_ID(), as.getC_AcctSchema_ID(), false);
			if(contractChargeAcct != null && contractChargeAcct.getCh_Expense_Acct() > 0)
			{
				return MAccount.get(getCtx(), contractChargeAcct.getCh_Expense_Acct());
			}else{
				return docLine.getAccount(ProductCost.ACCTTYPE_P_Expense, as);
			}
			
		}else if(line.getM_Product_ID() > 0){
			if(docLine.isItem())
			{
				return docLine.getAccount (ProductCost.ACCTTYPE_P_InventoryClearing, as);
			}else{
			
				MContractProductAcct contractProductAcct = contractAcct.getContractProductAcct(line.getM_Product().getM_Product_Category_ID(), as.getC_AcctSchema_ID(), false);
				if(contractProductAcct != null && contractProductAcct.getP_Expense_Acct() > 0)
				{
					return MAccount.get(getCtx(),contractProductAcct.getP_Expense_Acct());
				}else{
					return docLine.getAccount(ProductCost.ACCTTYPE_P_Expense, as);
				}
			}
		}else{
			return docLine.getAccount (ProductCost.ACCTTYPE_P_Expense, as);
		}
	}
	
	
	private MAccount getInvoiceTDiscountGrantAccount(DocLine docLine, MContractAcct contractAcct, MAcctSchema as)
	{
		MInvoiceLine line = (MInvoiceLine)docLine.getPO();
		
		MContractProductAcct contractProductAcct = contractAcct.getContractProductAcct(line.getM_Product().getM_Product_Category_ID(), as.getC_AcctSchema_ID(), false);
		if(contractProductAcct != null && contractProductAcct.getP_TradeDiscountGrant_Acct() > 0)
		{
			return MAccount.get(getCtx(),contractProductAcct.getP_TradeDiscountGrant_Acct());
		}else{
			return docLine.getAccount(ProductCost.ACCTTYPE_P_TDiscountGrant, as);
		}
	}
	
	private MAccount getInvoiceTDiscountRecAccount(DocLine docLine, MContractAcct contractAcct, MAcctSchema as)
	{
		MInvoiceLine line = (MInvoiceLine)docLine.getPO();
		
		MContractProductAcct contractProductAcct = contractAcct.getContractProductAcct(line.getM_Product().getM_Product_Category_ID(), as.getC_AcctSchema_ID(), false);
		if(contractProductAcct != null && contractProductAcct.getP_TradeDiscountRec_Acct() > 0)
		{
			return MAccount.get(getCtx(),contractProductAcct.getP_TradeDiscountRec_Acct());
		}else{
			return docLine.getAccount(ProductCost.ACCTTYPE_P_TDiscountRec, as);
		}
	}
	
	private MAccount getReceivableAccount(MContractAcct contractAcct, MAcctSchema as)
	{		
		MContractBPAcct bpAcct = contractAcct.getContractBPAcct(as.getC_AcctSchema_ID(), false);
		if(bpAcct != null && bpAcct.getC_Receivable_Acct() > 0)
		{
			return MAccount.get(getCtx(),bpAcct.getC_Receivable_Acct());
		}else{
			return MAccount.get(getCtx(), getValidCombination_ID(Doc.ACCTTYPE_C_Receivable, as));
		}
	}

	private MAccount getPayableAccount(MContractAcct contractAcct, MAcctSchema as)
	{		
		MContractBPAcct bpAcct = contractAcct.getContractBPAcct(as.getC_AcctSchema_ID(), false);
		if(bpAcct != null && bpAcct.getV_Liability_Acct() > 0)
		{
			return MAccount.get(getCtx(),bpAcct.getV_Liability_Acct());
		}else{
			return MAccount.get(getCtx(), getValidCombination_ID(Doc.ACCTTYPE_V_Liability, as));
		}
	}
	
	
	private MAccount getInvoiceTaxDueAccount(DocTax doc_Tax, MContractAcct contractAcct,  MAcctSchema as)
	{
		MContractTaxAcct taxAcct = contractAcct.getContracTaxAcct(doc_Tax.getC_Tax_ID(), as.getC_AcctSchema_ID(),false);
		if(taxAcct != null && taxAcct.getT_Due_Acct() > 0)
		{
			return MAccount.get(getCtx(), taxAcct.getT_Due_Acct());
		}else{
			return doc_Tax.getAccount(DocTax.ACCTTYPE_TaxDue,as);
		}
	}
	
	private MAccount getInvoiceTaxCreditAccount(DocTax doc_Tax, MContractAcct contractAcct,  MAcctSchema as)
	{
		MContractTaxAcct taxAcct = contractAcct.getContracTaxAcct(doc_Tax.getC_Tax_ID(), as.getC_AcctSchema_ID(),false);
		MTax tax = MTax.get(getCtx(), doc_Tax.getC_Tax_ID());
		if(tax.isSalesTax())
		{
			if(taxAcct != null && taxAcct.getT_Expense_Acct() > 0)
			{
				return MAccount.get(getCtx(), taxAcct.getT_Expense_Acct());
			}else{
				return doc_Tax.getAccount(DocTax.ACCTTYPE_TaxExpense,as);
			}
		}else{
			if(taxAcct != null && taxAcct.getT_Credit_Acct() > 0)
			{
				return MAccount.get(getCtx(), taxAcct.getT_Credit_Acct());
			}else{
				return doc_Tax.getAccount(DocTax.ACCTTYPE_TaxCredit,as);
			}
		}
	}
	
}
