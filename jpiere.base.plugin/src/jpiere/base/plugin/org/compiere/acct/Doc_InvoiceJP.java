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
import java.sql.Timestamp;
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
import org.compiere.model.MBPartner;
import org.compiere.model.MCharge;
import org.compiere.model.MCostDetail;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MSysConfig;
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
*  JPIERE-0363: Contract Management
*  JPIERE-0543: Tax Base Amt & Tax Amt To the Fact_Acct
*  JPIERE-0553: Qualified　Invoice　Issuer
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

		/**iDempiere Standard Posting Logic*/
		int JP_ContractContent_ID = invoice.get_ValueAsInt("JP_ContractContent_ID");
		if(JP_ContractContent_ID == 0)
		{
			return createFacts_Standard(as);
		}

		MContractContent contractContent = MContractContent.get(getCtx(), JP_ContractContent_ID);
		if(contractContent.getJP_Contract_Acct_ID() == 0)
		{
			return createFacts_Standard(as);
		}

		MContractAcct contractAcct = MContractAcct.get(Env.getCtx(),contractContent.getJP_Contract_Acct_ID());
		if(!contractAcct.isPostingContractAcctJP())
		{
			return createFacts_Standard(as);
		}


		/**JPiere Contract Posting Logic*/
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
			postAPI(as, contractAcct, fact);
		}
		//  APC
		else if (getDocumentType().equals(DOCTYPE_APCredit))
		{
			postAPC(as, contractAcct, fact);
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

	/**
	 * JPIERE Comment
	 * createFactsStandard method is iDempiere Standard createFacts function.
	 * And JPIERE-369 Charge Tax function
	 *
	 */
	private ArrayList<Fact> createFacts_Standard (MAcctSchema as)
	{
		//
		ArrayList<Fact> facts = new ArrayList<Fact>();
		//  create Fact Header
		Fact fact = new Fact(this, as, Fact.POST_Actual);

		//  Cash based accounting
		if (!as.isAccrual())
			return facts;

		//  ** ARI, ARF
		if (getDocumentType().equals(DOCTYPE_ARInvoice)
			|| getDocumentType().equals(DOCTYPE_ARProForma))
		{
			BigDecimal grossAmt = getAmount(Doc.AMTTYPE_Gross);
			BigDecimal serviceAmt = Env.ZERO;

			//  Header Charge           CR
			BigDecimal amt = getAmount(Doc.AMTTYPE_Charge);
			if (amt != null && amt.signum() != 0)
				fact.createLine(null, getAccount(Doc.ACCTTYPE_Charge, as),
					getC_Currency_ID(), null, amt);
			//  TaxDue                  CR
			for (int i = 0; i < m_taxes.length; i++)
			{
				amt = m_taxes[i].getAmount();
				if (amt != null && amt.signum() != 0)
				{
					FactLine tl = fact.createLine(null, m_taxes[i].getAccount(DocTax.ACCTTYPE_TaxDue, as), getC_Currency_ID(), null, amt);
					if (tl != null)
					{
						tl.setC_Tax_ID(m_taxes[i].getC_Tax_ID());
						tl.set_ValueNoCheck("JP_SOPOType", "S");
						tl.set_ValueNoCheck("JP_TaxBaseAmt", m_taxes[i].getTaxBaseAmt());
						tl.set_ValueNoCheck("JP_TaxAmt", m_taxes[i].getAmount());
					}
				}
			}
			//  Revenue                 CR
			for (int i = 0; i < p_lines.length; i++)
			{
				amt = p_lines[i].getAmtSource();

				//JPIERE-369:Start
				int C_Charge_ID = p_lines[i].getPO().get_ValueAsInt("C_Charge_ID");
				if(C_Charge_ID != 0)
				{
					MCharge charge = MCharge.get(getCtx(), C_Charge_ID);
					if(!charge.isSameTax() && charge.isTaxIncluded())
					{
						amt = (BigDecimal)p_lines[i].getPO().get_Value("JP_TaxBaseAmt");
					}
				}
				//JPiere-0369:finish

				BigDecimal dAmt = null;
				if (as.isTradeDiscountPosted())
				{
					BigDecimal discount = p_lines[i].getDiscount();
					if (discount != null && discount.signum() != 0)
					{
						amt = amt.add(discount);
						dAmt = discount;
						fact.createLine (p_lines[i],
								p_lines[i].getAccount(ProductCost.ACCTTYPE_P_TDiscountGrant, as),
								getC_Currency_ID(), dAmt, null);
					}
				}
				FactLine fLine = fact.createLine (p_lines[i], p_lines[i].getAccount(ProductCost.ACCTTYPE_P_Revenue, as), getC_Currency_ID(), null, amt);
				if(fLine != null)
				{
					fLine.setC_Tax_ID(p_lines[i].getC_Tax_ID());
					fLine.set_ValueNoCheck("JP_SOPOType", "S");
					fLine.set_ValueNoCheck("JP_TaxBaseAmt", p_lines[i].getPO().get_Value("JP_TaxBaseAmt"));
					fLine.set_ValueNoCheck("JP_TaxAmt", p_lines[i].getPO().get_Value("JP_TaxAmt"));
				}
				
				if (!p_lines[i].isItem())
				{
					grossAmt = grossAmt.subtract(amt);
					serviceAmt = serviceAmt.add(amt);
				}
			}

			//  Receivables     DR
			int receivables_ID = getValidCombination_ID(Doc.ACCTTYPE_C_Receivable, as);
			int receivablesServices_ID = receivables_ID; // Receivable Services account Deprecated IDEMPIERE-362
			if (m_allLinesItem || !as.isPostServices()
				|| receivables_ID == receivablesServices_ID)
			{
				grossAmt = getAmount(Doc.AMTTYPE_Gross);
				serviceAmt = Env.ZERO;
			}
			else if (m_allLinesService)
			{
				serviceAmt = getAmount(Doc.AMTTYPE_Gross);
				grossAmt = Env.ZERO;
			}
			if (grossAmt.signum() != 0)
				fact.createLine(null, MAccount.get(getCtx(), receivables_ID),
					getC_Currency_ID(), grossAmt, null);
			if (serviceAmt.signum() != 0)
				fact.createLine(null, MAccount.get(getCtx(), receivablesServices_ID),
					getC_Currency_ID(), serviceAmt, null);

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
		}
		//  ARC
		else if (getDocumentType().equals(DOCTYPE_ARCredit))
		{
			BigDecimal grossAmt = getAmount(Doc.AMTTYPE_Gross);
			BigDecimal serviceAmt = Env.ZERO;

			//  Header Charge   DR
			BigDecimal amt = getAmount(Doc.AMTTYPE_Charge);
			if (amt != null && amt.signum() != 0)
				fact.createLine(null, getAccount(Doc.ACCTTYPE_Charge, as),
					getC_Currency_ID(), amt, null);
			//  TaxDue          DR
			for (int i = 0; i < m_taxes.length; i++)
			{
				amt = m_taxes[i].getAmount();
				if (amt != null && amt.signum() != 0)
				{
					FactLine tl = fact.createLine(null, m_taxes[i].getAccount(DocTax.ACCTTYPE_TaxDue, as), getC_Currency_ID(), amt, null);
					if (tl != null)
					{
						tl.setC_Tax_ID(m_taxes[i].getC_Tax_ID());
						tl.set_ValueNoCheck("JP_SOPOType", "S");
						tl.set_ValueNoCheck("JP_TaxBaseAmt", m_taxes[i].getTaxBaseAmt().negate());
						tl.set_ValueNoCheck("JP_TaxAmt", m_taxes[i].getAmount().negate());
					}
				}
			}
			//  Revenue         CR
			for (int i = 0; i < p_lines.length; i++)
			{
				amt = p_lines[i].getAmtSource();

				//JPIERE-369:Start
				int C_Charge_ID = p_lines[i].getPO().get_ValueAsInt("C_Charge_ID");
				if(C_Charge_ID != 0)
				{
					MCharge charge = MCharge.get(getCtx(), C_Charge_ID);
					if(!charge.isSameTax() && charge.isTaxIncluded())
					{
						amt = (BigDecimal)p_lines[i].getPO().get_Value("JP_TaxBaseAmt");
					}
				}
				//JPiere-0369:finish

				BigDecimal dAmt = null;
				if (as.isTradeDiscountPosted())
				{
					BigDecimal discount = p_lines[i].getDiscount();
					if (discount != null && discount.signum() != 0)
					{
						amt = amt.add(discount);
						dAmt = discount;
						fact.createLine (p_lines[i],
								p_lines[i].getAccount (ProductCost.ACCTTYPE_P_TDiscountGrant, as),
								getC_Currency_ID(), null, dAmt);
					}
				}
				FactLine fLine = fact.createLine (p_lines[i], p_lines[i].getAccount (ProductCost.ACCTTYPE_P_Revenue, as), getC_Currency_ID(), amt, null);
				if(fLine != null && p_lines[i].getPO().get_Value("JP_TaxBaseAmt") != null)
				{
					fLine.setC_Tax_ID(p_lines[i].getC_Tax_ID());
					fLine.set_ValueNoCheck("JP_SOPOType", "S");
					fLine.set_ValueNoCheck("JP_TaxBaseAmt", ((BigDecimal)p_lines[i].getPO().get_Value("JP_TaxBaseAmt")).negate());
					fLine.set_ValueNoCheck("JP_TaxAmt", ((BigDecimal)p_lines[i].getPO().get_Value("JP_TaxAmt")).negate());
				}
				
				if (!p_lines[i].isItem())
				{
					grossAmt = grossAmt.subtract(amt);
					serviceAmt = serviceAmt.add(amt);
				}
			}

			//  Receivables             CR
			int receivables_ID = getValidCombination_ID (Doc.ACCTTYPE_C_Receivable, as);
			int receivablesServices_ID = receivables_ID; // Receivable Services account Deprecated IDEMPIERE-362
			if (m_allLinesItem || !as.isPostServices()
				|| receivables_ID == receivablesServices_ID)
			{
				grossAmt = getAmount(Doc.AMTTYPE_Gross);
				serviceAmt = Env.ZERO;
			}
			else if (m_allLinesService)
			{
				serviceAmt = getAmount(Doc.AMTTYPE_Gross);
				grossAmt = Env.ZERO;
			}
			if (grossAmt.signum() != 0)
				fact.createLine(null, MAccount.get(getCtx(), receivables_ID),
					getC_Currency_ID(), null, grossAmt);
			if (serviceAmt.signum() != 0)
				fact.createLine(null, MAccount.get(getCtx(), receivablesServices_ID),
					getC_Currency_ID(), null, serviceAmt);

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
		}

		//  ** API
		else if (getDocumentType().equals(DOCTYPE_APInvoice))
		{
			BigDecimal grossAmt = getAmount(Doc.AMTTYPE_Gross);
			BigDecimal serviceAmt = Env.ZERO;
			
			//JPIERE-0553: Qualified　Invoice　Issuer
			MBPartner bp = MBPartner.get(getCtx(), getC_BPartner_ID());
			boolean IsQualifiedInvoiceIssuerJP = bp.get_ValueAsBoolean("IsQualifiedInvoiceIssuerJP");
			
			//  Charge          DR
			fact.createLine(null, getAccount(Doc.ACCTTYPE_Charge, as),
				getC_Currency_ID(), getAmount(Doc.AMTTYPE_Charge), null);
			//  TaxCredit       DR
			for (int i = 0; i < m_taxes.length; i++)
			{
				FactLine tl = fact.createLine(null, m_taxes[i].getAccount(m_taxes[i].getAPTaxType(), as), getC_Currency_ID(), m_taxes[i].getAmount(), null);
				if (tl != null)
				{
					tl.setC_Tax_ID(m_taxes[i].getC_Tax_ID());
					tl.set_ValueNoCheck("JP_SOPOType", "P");
					tl.set_ValueNoCheck("JP_TaxBaseAmt", m_taxes[i].getTaxBaseAmt());
					tl.set_ValueNoCheck("JP_TaxAmt", m_taxes[i].getAmount());
					
					//JPIERE-0553: Qualified　Invoice　Issuer
					tl.set_ValueNoCheck("IsQualifiedInvoiceIssuerJP", false);	
					if(IsQualifiedInvoiceIssuerJP)
					{
						Object obj_RegisteredDateOfQII = bp.get_Value("JP_RegisteredDateOfQII");
						if(obj_RegisteredDateOfQII == null)
						{
							tl.set_ValueNoCheck("IsQualifiedInvoiceIssuerJP", IsQualifiedInvoiceIssuerJP);
							tl.set_ValueNoCheck("JP_RegisteredNumberOfQII", bp.get_Value("JP_RegisteredNumberOfQII"));
						}else {
							Timestamp JP_RegisteredDateOfQII = (Timestamp)obj_RegisteredDateOfQII;
							if(getDateAcct().compareTo(JP_RegisteredDateOfQII) >= 0)
							{
								tl.set_ValueNoCheck("IsQualifiedInvoiceIssuerJP", IsQualifiedInvoiceIssuerJP);
								tl.set_ValueNoCheck("JP_RegisteredNumberOfQII", bp.get_Value("JP_RegisteredNumberOfQII"));
							}
						}
					}//JPIERE-0553: 
				}
			}
			//  Expense         DR
			for (int i = 0; i < p_lines.length; i++)
			{
				DocLine line = p_lines[i];
				boolean landedCost = landedCost(as, fact, line, true);
				if (landedCost && as.isExplicitCostAdjustment())
				{
					fact.createLine (line, line.getAccount(ProductCost.ACCTTYPE_P_Expense, as),
						getC_Currency_ID(), line.getAmtSource(), null);
					//
					FactLine fl = fact.createLine (line, line.getAccount(ProductCost.ACCTTYPE_P_Expense, as),
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
					MAccount expense = line.getAccount(ProductCost.ACCTTYPE_P_Expense, as);
					if (line.isItem())
						expense = line.getAccount (ProductCost.ACCTTYPE_P_InventoryClearing, as);
					BigDecimal amt = line.getAmtSource();

					//JPIERE-369:Start
					int C_Charge_ID = p_lines[i].getPO().get_ValueAsInt("C_Charge_ID");
					if(C_Charge_ID != 0)
					{
						MCharge charge = MCharge.get(getCtx(), C_Charge_ID);
						if(!charge.isSameTax() && charge.isTaxIncluded())
						{
							amt = (BigDecimal)p_lines[i].getPO().get_Value("JP_TaxBaseAmt");
						}
					}
					//JPiere-0369:finish

					BigDecimal dAmt = null;
					if (as.isTradeDiscountPosted() && !line.isItem())
					{
						BigDecimal discount = line.getDiscount();
						if (discount != null && discount.signum() != 0)
						{
							amt = amt.add(discount);
							dAmt = discount;
							MAccount tradeDiscountReceived = line.getAccount(ProductCost.ACCTTYPE_P_TDiscountRec, as);
							fact.createLine (line, tradeDiscountReceived,
									getC_Currency_ID(), null, dAmt);
						}
					}
					FactLine fLine = fact.createLine (line, expense, getC_Currency_ID(), amt, null);
					if(fLine != null)
					{
						fLine.setC_Tax_ID(p_lines[i].getC_Tax_ID());
						fLine.set_ValueNoCheck("JP_SOPOType", "P");
						fLine.set_ValueNoCheck("JP_TaxBaseAmt", p_lines[i].getPO().get_Value("JP_TaxBaseAmt"));
						fLine.set_ValueNoCheck("JP_TaxAmt", p_lines[i].getPO().get_Value("JP_TaxAmt"));
						
						//JPIERE-0553: Qualified　Invoice　Issuer
						fLine.set_ValueNoCheck("IsQualifiedInvoiceIssuerJP", false);	
						if(IsQualifiedInvoiceIssuerJP)
						{
							Object obj_RegisteredDateOfQII = bp.get_Value("JP_RegisteredDateOfQII");
							if(obj_RegisteredDateOfQII == null)
							{
								fLine.set_ValueNoCheck("IsQualifiedInvoiceIssuerJP", IsQualifiedInvoiceIssuerJP);
								fLine.set_ValueNoCheck("JP_RegisteredNumberOfQII", bp.get_Value("JP_RegisteredNumberOfQII"));
							}else {
								Timestamp JP_RegisteredDateOfQII = (Timestamp)obj_RegisteredDateOfQII;
								if(getDateAcct().compareTo(JP_RegisteredDateOfQII) >= 0)
								{
									fLine.set_ValueNoCheck("IsQualifiedInvoiceIssuerJP", IsQualifiedInvoiceIssuerJP);
									fLine.set_ValueNoCheck("JP_RegisteredNumberOfQII", bp.get_Value("JP_RegisteredNumberOfQII"));
								}
							}
						}//JPIERE-0553: 
					}
					
					if (!line.isItem())
					{
						grossAmt = grossAmt.subtract(amt);
						serviceAmt = serviceAmt.add(amt);
					}
					//
					if(MSysConfig.getBooleanValue("JP_CREATE_COSTDETAIL_OF_SERVICE_PRODUCT", false, getAD_Client_ID()))
					{
						if (line.getM_Product_ID() != 0
							&& line.getProduct().isService())	//	otherwise Inv Matching
							MCostDetail.createInvoice(as, line.getAD_Org_ID(),
								line.getM_Product_ID(), line.getM_AttributeSetInstance_ID(),
								line.get_ID(), 0,		//	No Cost Element
								line.getAmtSource(), line.getQty(),
								line.getDescription(), getTrxName());
					}
				}
			}

			//  Liability               CR
			int payables_ID = getValidCombination_ID (Doc.ACCTTYPE_V_Liability, as);
			int payablesServices_ID = payables_ID; // Liability Services account Deprecated IDEMPIERE-362
			if (m_allLinesItem || !as.isPostServices()
				|| payables_ID == payablesServices_ID)
			{
				grossAmt = getAmount(Doc.AMTTYPE_Gross);
				serviceAmt = Env.ZERO;
			}
			else if (m_allLinesService)
			{
				serviceAmt = getAmount(Doc.AMTTYPE_Gross);
				grossAmt = Env.ZERO;
			}
			if (grossAmt.signum() != 0)
				fact.createLine(null, MAccount.get(getCtx(), payables_ID),
					getC_Currency_ID(), null, grossAmt);
			if (serviceAmt.signum() != 0)
				fact.createLine(null, MAccount.get(getCtx(), payablesServices_ID),
					getC_Currency_ID(), null, serviceAmt);

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

			//
			updateProductPO(as);	//	Only API
		}
		//  APC
		else if (getDocumentType().equals(DOCTYPE_APCredit))
		{
			BigDecimal grossAmt = getAmount(Doc.AMTTYPE_Gross);
			BigDecimal serviceAmt = Env.ZERO;
			
			//JPIERE-0553: Qualified　Invoice　Issuer
			MBPartner bp = MBPartner.get(getCtx(), getC_BPartner_ID());
			boolean IsQualifiedInvoiceIssuerJP = bp.get_ValueAsBoolean("IsQualifiedInvoiceIssuerJP");
			
			//  Charge                  CR
			fact.createLine (null, getAccount(Doc.ACCTTYPE_Charge, as),
				getC_Currency_ID(), null, getAmount(Doc.AMTTYPE_Charge));
			//  TaxCredit               CR
			for (int i = 0; i < m_taxes.length; i++)
			{
				FactLine tl = fact.createLine (null, m_taxes[i].getAccount(m_taxes[i].getAPTaxType(), as), getC_Currency_ID(), null, m_taxes[i].getAmount());
				if (tl != null)
				{
					tl.setC_Tax_ID(m_taxes[i].getC_Tax_ID());
					tl.set_ValueNoCheck("JP_SOPOType", "P");
					tl.set_ValueNoCheck("JP_TaxBaseAmt", m_taxes[i].getTaxBaseAmt().negate());
					tl.set_ValueNoCheck("JP_TaxAmt", m_taxes[i].getAmount().negate());
					
					//JPIERE-0553: Qualified　Invoice　Issuer
					tl.set_ValueNoCheck("IsQualifiedInvoiceIssuerJP", false);	
					if(IsQualifiedInvoiceIssuerJP)
					{
						Object obj_RegisteredDateOfQII = bp.get_Value("JP_RegisteredDateOfQII");
						if(obj_RegisteredDateOfQII == null)
						{
							tl.set_ValueNoCheck("IsQualifiedInvoiceIssuerJP", IsQualifiedInvoiceIssuerJP);
							tl.set_ValueNoCheck("JP_RegisteredNumberOfQII", bp.get_Value("JP_RegisteredNumberOfQII"));
						}else {
							Timestamp JP_RegisteredDateOfQII = (Timestamp)obj_RegisteredDateOfQII;
							if(getDateAcct().compareTo(JP_RegisteredDateOfQII) >= 0)
							{
								tl.set_ValueNoCheck("IsQualifiedInvoiceIssuerJP", IsQualifiedInvoiceIssuerJP);
								tl.set_ValueNoCheck("JP_RegisteredNumberOfQII", bp.get_Value("JP_RegisteredNumberOfQII"));
							}
						}
					}//JPIERE-0553: 
				}
			}
			//  Expense                 CR
			for (int i = 0; i < p_lines.length; i++)
			{
				DocLine line = p_lines[i];
				boolean landedCost = landedCost(as, fact, line, false);
				if (landedCost && as.isExplicitCostAdjustment())
				{
					fact.createLine (line, line.getAccount(ProductCost.ACCTTYPE_P_Expense, as),
						getC_Currency_ID(), null, line.getAmtSource());
					//
					FactLine fl = fact.createLine (line, line.getAccount(ProductCost.ACCTTYPE_P_Expense, as),
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
					MAccount expense = line.getAccount(ProductCost.ACCTTYPE_P_Expense, as);
					if (line.isItem())
						expense = line.getAccount (ProductCost.ACCTTYPE_P_InventoryClearing, as);
					BigDecimal amt = line.getAmtSource();

					//JPIERE-369:Start
					int C_Charge_ID = p_lines[i].getPO().get_ValueAsInt("C_Charge_ID");
					if(C_Charge_ID != 0)
					{
						MCharge charge = MCharge.get(getCtx(), C_Charge_ID);
						if(!charge.isSameTax() && charge.isTaxIncluded())
						{
							amt = (BigDecimal)p_lines[i].getPO().get_Value("JP_TaxBaseAmt");
						}
					}
					//JPiere-0369:finish

					BigDecimal dAmt = null;
					if (as.isTradeDiscountPosted() && !line.isItem())
					{
						BigDecimal discount = line.getDiscount();
						if (discount != null && discount.signum() != 0)
						{
							amt = amt.add(discount);
							dAmt = discount;
							MAccount tradeDiscountReceived = line.getAccount(ProductCost.ACCTTYPE_P_TDiscountRec, as);
							fact.createLine (line, tradeDiscountReceived,
									getC_Currency_ID(), dAmt, null);
						}
					}
					FactLine fLine = fact.createLine (line, expense, getC_Currency_ID(), null, amt);
					if(fLine != null && p_lines[i].getPO().get_Value("JP_TaxBaseAmt") != null)
					{
						fLine.setC_Tax_ID(p_lines[i].getC_Tax_ID());
						fLine.set_ValueNoCheck("JP_SOPOType", "P");
						fLine.set_ValueNoCheck("JP_TaxBaseAmt", ((BigDecimal)p_lines[i].getPO().get_Value("JP_TaxBaseAmt")).negate());
						fLine.set_ValueNoCheck("JP_TaxAmt", ((BigDecimal)p_lines[i].getPO().get_Value("JP_TaxAmt")).negate());
						
						//JPIERE-0553: Qualified　Invoice　Issuer
						fLine.set_ValueNoCheck("IsQualifiedInvoiceIssuerJP", false);	
						if(IsQualifiedInvoiceIssuerJP)
						{
							Object obj_RegisteredDateOfQII = bp.get_Value("JP_RegisteredDateOfQII");
							if(obj_RegisteredDateOfQII == null)
							{
								fLine.set_ValueNoCheck("IsQualifiedInvoiceIssuerJP", IsQualifiedInvoiceIssuerJP);
								fLine.set_ValueNoCheck("JP_RegisteredNumberOfQII", bp.get_Value("JP_RegisteredNumberOfQII"));
							}else {
								Timestamp JP_RegisteredDateOfQII = (Timestamp)obj_RegisteredDateOfQII;
								if(getDateAcct().compareTo(JP_RegisteredDateOfQII) >= 0)
								{
									fLine.set_ValueNoCheck("IsQualifiedInvoiceIssuerJP", IsQualifiedInvoiceIssuerJP);
									fLine.set_ValueNoCheck("JP_RegisteredNumberOfQII", bp.get_Value("JP_RegisteredNumberOfQII"));
								}
							}
						}//JPIERE-0553: 
					}
					
					if (!line.isItem())
					{
						grossAmt = grossAmt.subtract(amt);
						serviceAmt = serviceAmt.add(amt);
					}
					//
					if(MSysConfig.getBooleanValue("JP_CREATE_COSTDETAIL_OF_SERVICE_PRODUCT", false, getAD_Client_ID()))
					{
						if (line.getM_Product_ID() != 0
							&& line.getProduct().isService())	//	otherwise Inv Matching
							MCostDetail.createInvoice(as, line.getAD_Org_ID(),
								line.getM_Product_ID(), line.getM_AttributeSetInstance_ID(),
								line.get_ID(), 0,		//	No Cost Element
								line.getAmtSource().negate(), line.getQty(),
								line.getDescription(), getTrxName());
					}
				}
			}

			//  Liability       DR
			int payables_ID = getValidCombination_ID (Doc.ACCTTYPE_V_Liability, as);
			int payablesServices_ID = payables_ID; // Liability Services account Deprecated IDEMPIERE-362
			if (m_allLinesItem || !as.isPostServices()
				|| payables_ID == payablesServices_ID)
			{
				grossAmt = getAmount(Doc.AMTTYPE_Gross);
				serviceAmt = Env.ZERO;
			}
			else if (m_allLinesService)
			{
				serviceAmt = getAmount(Doc.AMTTYPE_Gross);
				grossAmt = Env.ZERO;
			}
			if (grossAmt.signum() != 0)
				fact.createLine(null, MAccount.get(getCtx(), payables_ID),
					getC_Currency_ID(), grossAmt, null);
			if (serviceAmt.signum() != 0)
				fact.createLine(null, MAccount.get(getCtx(), payablesServices_ID),
					getC_Currency_ID(), serviceAmt, null);

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
		}
		else
		{
			p_Error = "DocumentType unknown: " + getDocumentType();
			log.log(Level.SEVERE, p_Error);
			fact = null;
		}
		//
		facts.add(fact);
		return facts;
	}   //  createFact


	private void postARI(MAcctSchema as, MContractAcct contractAcct, Fact fact)
	{
		BigDecimal amt = Env.ZERO;

		//CR :  TaxDue
		for (int i = 0; i < m_taxes.length; i++)
		{
			amt = m_taxes[i].getAmount();
			if (amt != null && amt.signum() != 0)
			{
				//CR
				FactLine tl = null;
				tl = fact.createLine(null, getInvoiceTaxDueAccount(m_taxes[i], contractAcct, as), getC_Currency_ID(), null, amt);
				if (tl != null)
				{
					tl.setC_Tax_ID(m_taxes[i].getC_Tax_ID());
					tl.set_ValueNoCheck("JP_SOPOType", "S");
					tl.set_ValueNoCheck("JP_TaxBaseAmt", m_taxes[i].getTaxBaseAmt());
					tl.set_ValueNoCheck("JP_TaxAmt", m_taxes[i].getAmount());
				}
			}//if
		}//for

		//CR : Revenue
		for (int i = 0; i < p_lines.length; i++)
		{
			amt = p_lines[i].getAmtSource();

			//JPIERE-369:Start
			int C_Charge_ID = p_lines[i].getPO().get_ValueAsInt("C_Charge_ID");
			if(C_Charge_ID != 0)
			{
				MCharge charge = MCharge.get(getCtx(), C_Charge_ID);
				if(!charge.isSameTax() && charge.isTaxIncluded())
				{
					amt = (BigDecimal)p_lines[i].getPO().get_Value("JP_TaxBaseAmt");
				}
			}
			//JPiere-0369:finish

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
			FactLine fLine = fact.createLine (p_lines[i], getInvoiceRevenueAccount(p_lines[i], contractAcct,  as), getC_Currency_ID(), null, amt);
			if(fLine != null)
			{
				fLine.setC_Tax_ID(p_lines[i].getC_Tax_ID());
				fLine.set_ValueNoCheck("JP_SOPOType", "S");
				fLine.set_ValueNoCheck("JP_TaxBaseAmt", p_lines[i].getPO().get_Value("JP_TaxBaseAmt"));
				fLine.set_ValueNoCheck("JP_TaxAmt", p_lines[i].getPO().get_Value("JP_TaxAmt"));
			}

		}//For

		//DR : Receivables
		BigDecimal grossAmt = getAmount(Doc.AMTTYPE_Gross);
		if (grossAmt.signum() != 0)
			fact.createLine(null, getReceivableAccount(contractAcct,  as),getC_Currency_ID(), grossAmt, null);

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

	}//postARI


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
				{
					tl.setC_Tax_ID(m_taxes[i].getC_Tax_ID());
					tl.set_ValueNoCheck("JP_SOPOType", "S");
					tl.set_ValueNoCheck("JP_TaxBaseAmt", m_taxes[i].getTaxBaseAmt().negate());
					tl.set_ValueNoCheck("JP_TaxAmt", m_taxes[i].getAmount().negate());
				}
			}//if
		}//for

		//DR :  Revenue
		for (int i = 0; i < p_lines.length; i++)
		{
			amt = p_lines[i].getAmtSource();

			//JPIERE-369:Start
			int C_Charge_ID = p_lines[i].getPO().get_ValueAsInt("C_Charge_ID");
			if(C_Charge_ID != 0)
			{
				MCharge charge = MCharge.get(getCtx(), C_Charge_ID);
				if(!charge.isSameTax() && charge.isTaxIncluded())
				{
					amt = (BigDecimal)p_lines[i].getPO().get_Value("JP_TaxBaseAmt");
				}
			}
			//JPiere-0369:finish

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
			FactLine fLine = fact.createLine (p_lines[i], getInvoiceRevenueAccount(p_lines[i], contractAcct,  as), getC_Currency_ID(), amt, null);
			if(fLine != null && p_lines[i].getPO().get_Value("JP_TaxBaseAmt") != null)
			{
				fLine.setC_Tax_ID(p_lines[i].getC_Tax_ID());
				fLine.set_ValueNoCheck("JP_SOPOType", "S");
				fLine.set_ValueNoCheck("JP_TaxBaseAmt", ((BigDecimal)p_lines[i].getPO().get_Value("JP_TaxBaseAmt")).negate());
				fLine.set_ValueNoCheck("JP_TaxAmt", ((BigDecimal)p_lines[i].getPO().get_Value("JP_TaxAmt")).negate());
			}

		}//For

		//CR : Receivables
		BigDecimal grossAmt = getAmount(Doc.AMTTYPE_Gross);
		if (grossAmt.signum() != 0)
			fact.createLine(null, getReceivableAccount(contractAcct,  as), getC_Currency_ID(), null, grossAmt);

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

	}//postARC

	private void postAPI(MAcctSchema as, MContractAcct contractAcct, Fact fact)
	{
		BigDecimal grossAmt = getAmount(Doc.AMTTYPE_Gross);
		BigDecimal serviceAmt = Env.ZERO;

		//JPIERE-0553: Qualified　Invoice　Issuer
		MBPartner bp = MBPartner.get(getCtx(), getC_BPartner_ID());
		boolean IsQualifiedInvoiceIssuerJP = bp.get_ValueAsBoolean("IsQualifiedInvoiceIssuerJP");
		
		//  TaxCredit       DR
		for (int i = 0; i < m_taxes.length; i++)
		{
			FactLine tl = fact.createLine(null, getInvoiceTaxCreditAccount(m_taxes[i], contractAcct, as),
				getC_Currency_ID(), m_taxes[i].getAmount(), null);
			if (tl != null)
			{
				tl.setC_Tax_ID(m_taxes[i].getC_Tax_ID());
				tl.set_ValueNoCheck("JP_SOPOType", "P");
				tl.set_ValueNoCheck("JP_TaxBaseAmt", m_taxes[i].getTaxBaseAmt());
				tl.set_ValueNoCheck("JP_TaxAmt", m_taxes[i].getAmount());
				
				//JPIERE-0553: Qualified　Invoice　Issuer
				tl.set_ValueNoCheck("IsQualifiedInvoiceIssuerJP", false);	
				if(IsQualifiedInvoiceIssuerJP)
				{
					Object obj_RegisteredDateOfQII = bp.get_Value("JP_RegisteredDateOfQII");
					if(obj_RegisteredDateOfQII == null)
					{
						tl.set_ValueNoCheck("IsQualifiedInvoiceIssuerJP", IsQualifiedInvoiceIssuerJP);
						tl.set_ValueNoCheck("JP_RegisteredNumberOfQII", bp.get_Value("JP_RegisteredNumberOfQII"));
					}else {
						Timestamp JP_RegisteredDateOfQII = (Timestamp)obj_RegisteredDateOfQII;
						if(getDateAcct().compareTo(JP_RegisteredDateOfQII) >= 0)
						{
							tl.set_ValueNoCheck("IsQualifiedInvoiceIssuerJP", IsQualifiedInvoiceIssuerJP);
							tl.set_ValueNoCheck("JP_RegisteredNumberOfQII", bp.get_Value("JP_RegisteredNumberOfQII"));
						}
					}
				}//JPIERE-0553: 
			}
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

				//JPIERE-369:Start
				int C_Charge_ID = p_lines[i].getPO().get_ValueAsInt("C_Charge_ID");
				if(C_Charge_ID != 0)
				{
					MCharge charge = MCharge.get(getCtx(), C_Charge_ID);
					if(!charge.isSameTax() && charge.isTaxIncluded())
					{
						amt = (BigDecimal)p_lines[i].getPO().get_Value("JP_TaxBaseAmt");
					}
				}
				//JPiere-0369:finish

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
				FactLine fLine = fact.createLine (line, expense, getC_Currency_ID(), amt, null);
				if(fLine != null)
				{
					fLine.setC_Tax_ID(p_lines[i].getC_Tax_ID());
					fLine.set_ValueNoCheck("JP_SOPOType", "P");
					fLine.set_ValueNoCheck("JP_TaxBaseAmt", p_lines[i].getPO().get_Value("JP_TaxBaseAmt"));
					fLine.set_ValueNoCheck("JP_TaxAmt", p_lines[i].getPO().get_Value("JP_TaxAmt"));
					
					//JPIERE-0553: Qualified　Invoice　Issuer
					fLine.set_ValueNoCheck("IsQualifiedInvoiceIssuerJP", false);	
					if(IsQualifiedInvoiceIssuerJP)
					{
						Object obj_RegisteredDateOfQII = bp.get_Value("JP_RegisteredDateOfQII");
						if(obj_RegisteredDateOfQII == null)
						{
							fLine.set_ValueNoCheck("IsQualifiedInvoiceIssuerJP", IsQualifiedInvoiceIssuerJP);
							fLine.set_ValueNoCheck("JP_RegisteredNumberOfQII", bp.get_Value("JP_RegisteredNumberOfQII"));
						}else {
							Timestamp JP_RegisteredDateOfQII = (Timestamp)obj_RegisteredDateOfQII;
							if(getDateAcct().compareTo(JP_RegisteredDateOfQII) >= 0)
							{
								fLine.set_ValueNoCheck("IsQualifiedInvoiceIssuerJP", IsQualifiedInvoiceIssuerJP);
								fLine.set_ValueNoCheck("JP_RegisteredNumberOfQII", bp.get_Value("JP_RegisteredNumberOfQII"));
							}
						}
					}//JPIERE-0553: 
				}
				if (!line.isItem())
				{
					grossAmt = grossAmt.subtract(amt);
					serviceAmt = serviceAmt.add(amt);
				}
				//
				if(MSysConfig.getBooleanValue("JP_CREATE_COSTDETAIL_OF_SERVICE_PRODUCT", false, getAD_Client_ID()))
				{
					if (line.getM_Product_ID() != 0
						&& line.getProduct().isService())	//	otherwise Inv Matching
						MCostDetail.createInvoice(as, line.getAD_Org_ID(),
							line.getM_Product_ID(), line.getM_AttributeSetInstance_ID(),
							line.get_ID(), 0,		//	No Cost Element
							line.getAmtSource(), line.getQty(),
							line.getDescription(), getTrxName());
				}
			}
		}

		//  Liability               CR
		grossAmt = getAmount(Doc.AMTTYPE_Gross);
		if (grossAmt.signum() != 0)
			fact.createLine(null, getPayableAccount(contractAcct, as),
				getC_Currency_ID(), null, grossAmt);

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

		//
		updateProductPO(as);	//	Only API
	}//postAPI

	private void postAPC(MAcctSchema as, MContractAcct contractAcct, Fact fact)
	{
		BigDecimal grossAmt = getAmount(Doc.AMTTYPE_Gross);
		BigDecimal serviceAmt = Env.ZERO;

		//JPIERE-0553: Qualified　Invoice　Issuer
		MBPartner bp = MBPartner.get(getCtx(), getC_BPartner_ID());
		boolean IsQualifiedInvoiceIssuerJP = bp.get_ValueAsBoolean("IsQualifiedInvoiceIssuerJP");
		
		//  TaxCredit               CR
		for (int i = 0; i < m_taxes.length; i++)
		{
			FactLine tl = fact.createLine (null, getInvoiceTaxCreditAccount(m_taxes[i], contractAcct, as),
				getC_Currency_ID(), null, m_taxes[i].getAmount());
			if (tl != null)
			{
				tl.setC_Tax_ID(m_taxes[i].getC_Tax_ID());
				tl.set_ValueNoCheck("JP_SOPOType", "P");
				tl.set_ValueNoCheck("JP_TaxBaseAmt", m_taxes[i].getTaxBaseAmt().negate());
				tl.set_ValueNoCheck("JP_TaxAmt", m_taxes[i].getAmount().negate());
				
				//JPIERE-0553: Qualified　Invoice　Issuer
				tl.set_ValueNoCheck("IsQualifiedInvoiceIssuerJP", false);	
				if(IsQualifiedInvoiceIssuerJP)
				{
					Object obj_RegisteredDateOfQII = bp.get_Value("JP_RegisteredDateOfQII");
					if(obj_RegisteredDateOfQII == null)
					{
						tl.set_ValueNoCheck("IsQualifiedInvoiceIssuerJP", IsQualifiedInvoiceIssuerJP);
						tl.set_ValueNoCheck("JP_RegisteredNumberOfQII", bp.get_Value("JP_RegisteredNumberOfQII"));
					}else {
						Timestamp JP_RegisteredDateOfQII = (Timestamp)obj_RegisteredDateOfQII;
						if(getDateAcct().compareTo(JP_RegisteredDateOfQII) >= 0)
						{
							tl.set_ValueNoCheck("IsQualifiedInvoiceIssuerJP", IsQualifiedInvoiceIssuerJP);
							tl.set_ValueNoCheck("JP_RegisteredNumberOfQII", bp.get_Value("JP_RegisteredNumberOfQII"));
						}
					}
				}//JPIERE-0553: 
			}
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

				//JPIERE-369:Start
				int C_Charge_ID = p_lines[i].getPO().get_ValueAsInt("C_Charge_ID");
				if(C_Charge_ID != 0)
				{
					MCharge charge = MCharge.get(getCtx(), C_Charge_ID);
					if(!charge.isSameTax() && charge.isTaxIncluded())
					{
						amt = (BigDecimal)p_lines[i].getPO().get_Value("JP_TaxBaseAmt");
					}
				}
				//JPiere-0369:finish

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
				FactLine fLine = fact.createLine (line, expense, getC_Currency_ID(), null, amt);
				if(fLine != null  && p_lines[i].getPO().get_Value("JP_TaxBaseAmt") != null)
				{
					fLine.setC_Tax_ID(p_lines[i].getC_Tax_ID());
					fLine.set_ValueNoCheck("JP_SOPOType", "P");
					fLine.set_ValueNoCheck("JP_TaxBaseAmt", ((BigDecimal)p_lines[i].getPO().get_Value("JP_TaxBaseAmt")).negate());
					fLine.set_ValueNoCheck("JP_TaxAmt", ((BigDecimal)p_lines[i].getPO().get_Value("JP_TaxAmt")).negate());
					
					//JPIERE-0553: Qualified　Invoice　Issuer
					fLine.set_ValueNoCheck("IsQualifiedInvoiceIssuerJP", false);	
					if(IsQualifiedInvoiceIssuerJP)
					{
						Object obj_RegisteredDateOfQII = bp.get_Value("JP_RegisteredDateOfQII");
						if(obj_RegisteredDateOfQII == null)
						{
							fLine.set_ValueNoCheck("IsQualifiedInvoiceIssuerJP", IsQualifiedInvoiceIssuerJP);
							fLine.set_ValueNoCheck("JP_RegisteredNumberOfQII", bp.get_Value("JP_RegisteredNumberOfQII"));
						}else {
							Timestamp JP_RegisteredDateOfQII = (Timestamp)obj_RegisteredDateOfQII;
							if(getDateAcct().compareTo(JP_RegisteredDateOfQII) >= 0)
							{
								fLine.set_ValueNoCheck("IsQualifiedInvoiceIssuerJP", IsQualifiedInvoiceIssuerJP);
								fLine.set_ValueNoCheck("JP_RegisteredNumberOfQII", bp.get_Value("JP_RegisteredNumberOfQII"));
							}
						}
					}//JPIERE-0553: 
				}
				if (!line.isItem())
				{
					grossAmt = grossAmt.subtract(amt);
					serviceAmt = serviceAmt.add(amt);
				}
				//
				if(MSysConfig.getBooleanValue("JP_CREATE_COSTDETAIL_OF_SERVICE_PRODUCT", false, getAD_Client_ID()))
				{
					if (line.getM_Product_ID() != 0
						&& line.getProduct().isService())	//	otherwise Inv Matching
						MCostDetail.createInvoice(as, line.getAD_Org_ID(),
							line.getM_Product_ID(), line.getM_AttributeSetInstance_ID(),
							line.get_ID(), 0,		//	No Cost Element
							line.getAmtSource().negate(), line.getQty(),
							line.getDescription(), getTrxName());
				}
			}
		}

		//  Liability       DR
		grossAmt = getAmount(Doc.AMTTYPE_Gross);
		if (grossAmt.signum() != 0)
			fact.createLine(null, getPayableAccount(contractAcct, as),	getC_Currency_ID(), grossAmt, null);

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

	}//postAPC



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
