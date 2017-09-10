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
import java.util.ArrayList;
import java.util.logging.Level;

import jpiere.base.plugin.org.adempiere.model.MContractAcct;
import jpiere.base.plugin.org.adempiere.model.MContractBPAcct;
import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractProductAcct;
import jpiere.base.plugin.org.adempiere.model.MContractTaxAcct;
import jpiere.base.plugin.org.adempiere.model.MRecognition;
import jpiere.base.plugin.org.adempiere.model.MRecognitionLine;

import org.compiere.acct.Doc;
import org.compiere.acct.DocLine;
import org.compiere.acct.DocTax;
import org.compiere.acct.Fact;
import org.compiere.acct.FactLine;
import org.compiere.model.MAccount;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MCostDetail;
import org.compiere.model.MCurrency;
import org.compiere.model.MProduct;
import org.compiere.model.MTax;
import org.compiere.model.ProductCost;
import org.compiere.util.DB;
import org.compiere.util.Env;

/**
 * Post Recognition Documents.
 *
 *
 * JPIERE-0364: Recognition Document
 *
 * <pre>
 *   Table:              JP_Recognition
 *   Document Types:     JPR,JPX
 * </pre>
 *
 * @author Jorg Janke
 * @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 * @version α
 */
public class Doc_JPRecognition extends Doc
{
	/**
	 * Constructor
	 * 	@param as accounting schema
	 * 	@param rs record
	 * 	@param trxName trx
	 */
	public Doc_JPRecognition (MAcctSchema as, ResultSet rs, String trxName)
	{
		super (as, MRecognition.class, rs, null, trxName);
	}
	
	/** Contained Optional Tax Lines    */
	protected DocTax[]        m_taxes = null;
	/** Currency Precision				*/
	protected int				m_precision = -1;
	/** All lines are Service			*/
	protected boolean			m_allLinesService = true;
	/** All lines are product item		*/
	protected boolean			m_allLinesItem = true;

	/**
	 *	Load Specific Document Details
	 *  @return error message or null
	 */
	protected String loadDocumentDetails ()
	{
		MRecognition recognition = (MRecognition)getPO();
		setDateDoc(recognition.getDateInvoiced());
		setIsTaxIncluded(recognition.isTaxIncluded());
		//	Amounts
		setAmount(Doc.AMTTYPE_Gross, recognition.getGrandTotal());
		setAmount(Doc.AMTTYPE_Net, recognition.getTotalLines());
//		setAmount(Doc.AMTTYPE_Charge, recognition.getChargeAmt()); TODO ヘッダーのChargeAmtが必要かどうか要確認!!

		//	Contained Objects
		m_taxes = loadTaxes();
		p_lines = loadLines(recognition);
		if (log.isLoggable(Level.FINE)) log.fine("Lines=" + p_lines.length + ", Taxes=" + m_taxes.length);
		return null;
	}	// loadDocumentDetails

	
	/**
	 *	Load Invoice Taxes
	 *  @return DocTax Array
	 */
	private DocTax[] loadTaxes()
	{
		ArrayList<DocTax> list = new ArrayList<DocTax>();
		String sql = "SELECT rt.C_Tax_ID, t.Name, t.Rate, rt.TaxBaseAmt, rt.TaxAmt, t.IsSalesTax "
				+ "FROM C_Tax t, JP_RecognitionTax rt "
				+ "WHERE t.C_Tax_ID=rt.C_Tax_ID AND rt.JP_Recognition_ID=?";
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
				//
				DocTax taxLine = new DocTax(C_Tax_ID, name, rate,
					taxBaseAmt, amount, salesTax);
				if (log.isLoggable(Level.FINE)) log.fine(taxLine.toString());
				list.add(taxLine);
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


	/**
	 *	Load Recognition Line
	 *	@param recognition Recognition
	 *  @return DocLine Array
	 */
	private DocLine[] loadLines (MRecognition recognition)
	{
		ArrayList<DocLine> list = new ArrayList<DocLine>();
		//
		MRecognitionLine[] lines = recognition.getLines(false);
		for (int i = 0; i < lines.length; i++)
		{
			MRecognitionLine line = lines[i];
			if (line.isDescription())
				continue;
			DocLine docLine = new DocLine(line, this);
			//	Qty
			BigDecimal Qty = line.getQtyInvoiced();
			boolean cm = getDocumentType().equals("ARS")//Recognition Revenu Credit memo
				|| getDocumentType().equals("ARY");//Recognition Expense Credit memo
			docLine.setQty(cm ? Qty.negate() : Qty, recognition.isSOTrx());
			//
			BigDecimal LineNetAmt = line.getLineNetAmt();
			BigDecimal PriceList = line.getPriceList();
			int C_Tax_ID = docLine.getC_Tax_ID();
			//	Correct included Tax
			if (isTaxIncluded() && C_Tax_ID != 0)
			{
				MTax tax = MTax.get(getCtx(), C_Tax_ID);
				if (!tax.isZeroTax())
				{
					BigDecimal LineNetAmtTax = tax.calculateTax(LineNetAmt, true, getStdPrecision());
					if (log.isLoggable(Level.FINE)) log.fine("LineNetAmt=" + LineNetAmt + " - Tax=" + LineNetAmtTax);
					LineNetAmt = LineNetAmt.subtract(LineNetAmtTax);

					if (tax.isSummary()) {
						BigDecimal sumChildLineNetAmtTax = Env.ZERO;
						DocTax taxToApplyDiff = null;
						for (MTax childTax : tax.getChildTaxes(false)) {
							if (!childTax.isZeroTax())
							{
								BigDecimal childLineNetAmtTax = childTax.calculateTax(LineNetAmt, false, getStdPrecision());
								if (log.isLoggable(Level.FINE)) log.fine("LineNetAmt=" + LineNetAmt + " - Child Tax=" + childLineNetAmtTax);
								for (int t = 0; t < m_taxes.length; t++)
								{
									if (m_taxes[t].getC_Tax_ID() == childTax.getC_Tax_ID())
									{
										m_taxes[t].addIncludedTax(childLineNetAmtTax);
										taxToApplyDiff = m_taxes[t];
										sumChildLineNetAmtTax = sumChildLineNetAmtTax.add(childLineNetAmtTax);
										break;
									}
								}
							}
						}
						BigDecimal diffChildVsSummary = LineNetAmtTax.subtract(sumChildLineNetAmtTax);
						if (diffChildVsSummary.signum() != 0 && taxToApplyDiff != null) {
							taxToApplyDiff.addIncludedTax(diffChildVsSummary);
						}
					} else {
						for (int t = 0; t < m_taxes.length; t++)
						{
							if (m_taxes[t].getC_Tax_ID() == C_Tax_ID)
							{
								m_taxes[t].addIncludedTax(LineNetAmtTax);
								break;
							}
						}
					}
					
					BigDecimal PriceListTax = tax.calculateTax(PriceList, true, getStdPrecision());
					PriceList = PriceList.subtract(PriceListTax);
				}
			}	//	correct included Tax

			docLine.setAmount (LineNetAmt, PriceList, Qty);	//	qty for discount calc
			if (docLine.isItem())
				m_allLinesService = false;
			else
				m_allLinesItem = false;
			//
			if (log.isLoggable(Level.FINE)) log.fine(docLine.toString());
			list.add(docLine);
		}

		//	Convert to Array
		DocLine[] dls = new DocLine[list.size()];
		list.toArray(dls);

		//	Included Tax - make sure that no difference
		if (isTaxIncluded())
		{
			for (int i = 0; i < m_taxes.length; i++)
			{
				if (m_taxes[i].isIncludedTaxDifference())
				{
					BigDecimal diff = m_taxes[i].getIncludedTaxDifference();
					for (int j = 0; j < dls.length; j++)
					{
						MTax lineTax = MTax.get(getCtx(), dls[j].getC_Tax_ID());
						MTax[] composingTaxes = null;
						if (lineTax.isSummary()) {
							composingTaxes = lineTax.getChildTaxes(false);
						} else {
							composingTaxes = new MTax[1];
							composingTaxes[0] = lineTax;
						}
						for (MTax mTax : composingTaxes) {
							if (mTax.getC_Tax_ID() == m_taxes[i].getC_Tax_ID())
							{
								dls[j].setLineNetAmtDifference(diff);
								m_taxes[i].addIncludedTax(diff.negate());
								diff = Env.ZERO;
								break;
							}
						}
						if (diff.signum() == 0) {
							break;
						}
					}	//	for all lines
				}	//	tax difference
			}	//	for all taxes
		}	//	Included Tax difference

		//	Return Array
		return dls;
	}	//	loadLines

	/**
	 * 	Get Currency Precision
	 *	@return precision
	 */
	private int getStdPrecision()
	{
		if (m_precision == -1)
			m_precision = MCurrency.getStdPrecision(getCtx(), getC_Currency_ID());
		return m_precision;
	}	//	getPrecision
	

	/***************************************************************************
	 * Get Source Currency Balance - subtracts line and tax amounts from total -
	 * no rounding
	 *
	 * @return positive amount, if total invoice is bigger than lines
	 */
	public BigDecimal getBalance ()
	{
		BigDecimal retValue = Env.ZERO;
		return retValue;
	}	// getBalance

	/***************************************************************************
	 * Create Facts (the accounting logic) for POR.
	 * <pre>
	 * Reservation
	 * 	Expense		CR
	 * 	Offset			DR
	 * </pre>
	 * @param as accounting schema
	 * @return Fact
	 */
	public ArrayList<Fact> createFacts (MAcctSchema as)
	{
		ArrayList<Fact> facts = new ArrayList<Fact>();
		
		MRecognition recog = (MRecognition)getPO();
		
		if(recog.getJP_ContractContent_ID()==0)
			return facts;
		
		MContractContent contractContent = MContractContent.get(Env.getCtx(),recog.getJP_ContractContent_ID());
		if(contractContent.getJP_Contract_Acct_ID() == 0)
			return facts;
		
		MContractAcct contractAcct = MContractAcct.get(Env.getCtx(),contractContent.getJP_Contract_Acct_ID());
		
		if( !(contractAcct.isPostingContractAcctJP() && contractAcct.isPostingRecognitionDocJP()) )
			return facts;
		
		//  create Fact Header
		Fact fact = new Fact(this, as, Fact.POST_Actual);		
		
		//  Cash based accounting
		if (!as.isAccrual())
			return facts;

		//  ** JPR - Revenue Recognition
		if (getDocumentType().equals("JPR"))
		{
			BigDecimal amt = Env.ZERO;

			//DR: Invoice  TaxDue      / CR:   Recognition TaxDue
			MContractTaxAcct taxAcct = null;
			for (int i = 0; i < m_taxes.length; i++)
			{
				amt = m_taxes[i].getAmount();
				taxAcct = contractAcct.getContracTaxAcct(m_taxes[i].getC_Tax_ID(), as.getC_AcctSchema_ID(),false);
				if (amt != null && amt.signum() != 0)
				{
					//DR
					FactLine taxLineDR = fact.createLine(null, MAccount.get(getCtx(), taxAcct.getT_Due_Acct()), getC_Currency_ID(), amt, null);
					if (taxLineDR != null)
						taxLineDR.setC_Tax_ID(m_taxes[i].getC_Tax_ID());
					
					//CR
					FactLine taxLineCR = fact.createLine(null, MAccount.get(getCtx(), taxAcct.getJP_TaxDue_Acct()), getC_Currency_ID(), null, amt);
					if (taxLineCR != null)
						taxLineCR.setC_Tax_ID(m_taxes[i].getC_Tax_ID());	
				}
			}//for
			
			//DR:  Invoice Revenue / CR: Recognition Revenue  
			for (int i = 0; i < p_lines.length; i++)
			{
				amt = p_lines[i].getAmtSource();
				BigDecimal dAmt = null;
				if (as.isTradeDiscountPosted())
				{
					BigDecimal discount = p_lines[i].getDiscount();
					if (discount != null && discount.signum() != 0)
					{
						amt = amt.add(discount);
						dAmt = discount;
						//DR  - Invoice Trade Deiscount Acct
						fact.createLine (p_lines[i], MAccount.get(getCtx(), getInvoiceTDiscountGrantValidCombinationID(p_lines[i], contractAcct,  as.getC_AcctSchema_ID())), getC_Currency_ID(), null, dAmt);
						
						//CR  - Recognition Trade Deiscount Acct
						fact.createLine (p_lines[i], MAccount.get(getCtx(), getRecognitionTDiscountGrantValidCombinationID(p_lines[i], contractAcct,  as.getC_AcctSchema_ID())), getC_Currency_ID(), null, dAmt);
					}
				}
				
				//DR - Invoice Revenue Acct
				fact.createLine (p_lines[i], MAccount.get(getCtx(), getInvoiceRevenueValidCombinationID(p_lines[i], contractAcct,  as.getC_AcctSchema_ID())), getC_Currency_ID(), amt, null);
				
				//CR - Recognition Revenue Acct
				fact.createLine (p_lines[i], MAccount.get(getCtx(), getRecognitionRevenueValidCombinationID(p_lines[i], contractAcct,  as.getC_AcctSchema_ID())), getC_Currency_ID(), null, amt);
			}
			
			//  Set Locations And Order Info
			FactLine[] fLines = fact.getLines();
			for (int i = 0; i < fLines.length; i++)
			{
				if (fLines[i] != null)
				{
					fLines[i].setLocationFromOrg(fLines[i].getAD_Org_ID(), true);      //  from Loc
					fLines[i].setLocationFromBPartner(getC_BPartner_Location_ID(), false);  //  to Loc
					fLines[i].set_ValueNoCheck("JP_SalesOrder_ID", recog.getC_Order_ID());
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
					FactLine tl = fact.createLine(null, m_taxes[i].getAccount(DocTax.ACCTTYPE_TaxDue, as),//TODO getAccount
						getC_Currency_ID(), amt, null);
					if (tl != null)
						tl.setC_Tax_ID(m_taxes[i].getC_Tax_ID());
				}
			}
			//  Revenue         CR
			for (int i = 0; i < p_lines.length; i++)
			{
				amt = p_lines[i].getAmtSource();
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
				fact.createLine (p_lines[i],
					p_lines[i].getAccount (ProductCost.ACCTTYPE_P_Revenue, as),//TODO getAccount
					getC_Currency_ID(), amt, null);
				if (!p_lines[i].isItem())
				{
					grossAmt = grossAmt.subtract(amt);
					serviceAmt = serviceAmt.add(amt);
				}
			}
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
			//  Receivables             CR
			int receivables_ID = getValidCombination_ID (Doc.ACCTTYPE_C_Receivable, as);//TODO getAccount
			int receivablesServices_ID = getValidCombination_ID (Doc.ACCTTYPE_C_Receivable_Services, as);
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
		}

		//  ** API
		else if (getDocumentType().equals(DOCTYPE_APInvoice))
		{
			BigDecimal grossAmt = getAmount(Doc.AMTTYPE_Gross);
			BigDecimal serviceAmt = Env.ZERO;

			//  Charge          DR
			fact.createLine(null, getAccount(Doc.ACCTTYPE_Charge, as),
				getC_Currency_ID(), getAmount(Doc.AMTTYPE_Charge), null);
			//  TaxCredit       DR
			for (int i = 0; i < m_taxes.length; i++)
			{
				FactLine tl = fact.createLine(null, m_taxes[i].getAccount(m_taxes[i].getAPTaxType(), as),//TODO getAccount
					getC_Currency_ID(), m_taxes[i].getAmount(), null);
				if (tl != null)
					tl.setC_Tax_ID(m_taxes[i].getC_Tax_ID());
			}
			//  Expense         DR
			for (int i = 0; i < p_lines.length; i++)
			{
				DocLine line = p_lines[i];
				boolean landedCost = false;
//				if (landedCost && as.isExplicitCostAdjustment())
//				{
//					fact.createLine (line, line.getAccount(ProductCost.ACCTTYPE_P_Expense, as),
//						getC_Currency_ID(), line.getAmtSource(), null);
//					//
//					FactLine fl = fact.createLine (line, line.getAccount(ProductCost.ACCTTYPE_P_Expense, as),
//						getC_Currency_ID(), null, line.getAmtSource());
//					String desc = line.getDescription();
//					if (desc == null)
//						desc = "100%";
//					else
//						desc += " 100%";
//					fl.setDescription(desc);
//				}
				if (!landedCost)
				{
					MAccount expense = line.getAccount(ProductCost.ACCTTYPE_P_Expense, as);//TODO getAccount
					if (line.isItem())
						expense = line.getAccount (ProductCost.ACCTTYPE_P_InventoryClearing, as);//TODO アイテムなら何もしない―――消費税どうする？？
					BigDecimal amt = line.getAmtSource();
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
			int payables_ID = getValidCombination_ID (Doc.ACCTTYPE_V_Liability, as);//TODO getAccount
			int payablesServices_ID = getValidCombination_ID (Doc.ACCTTYPE_V_Liability_Services, as);
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
			//
//			updateProductPO(as);	//	Only API
		}
		//  APC
		else if (getDocumentType().equals(DOCTYPE_APCredit))
		{
			BigDecimal grossAmt = getAmount(Doc.AMTTYPE_Gross);
			BigDecimal serviceAmt = Env.ZERO;
			//  Charge                  CR
			fact.createLine (null, getAccount(Doc.ACCTTYPE_Charge, as),
				getC_Currency_ID(), null, getAmount(Doc.AMTTYPE_Charge));
			//  TaxCredit               CR
			for (int i = 0; i < m_taxes.length; i++)
			{
				FactLine tl = fact.createLine (null, m_taxes[i].getAccount(m_taxes[i].getAPTaxType(), as),
					getC_Currency_ID(), null, m_taxes[i].getAmount());
				if (tl != null)
					tl.setC_Tax_ID(m_taxes[i].getC_Tax_ID());
			}
			//  Expense                 CR
			for (int i = 0; i < p_lines.length; i++)
			{
				DocLine line = p_lines[i];
				boolean landedCost = false;
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
			int payables_ID = getValidCombination_ID (Doc.ACCTTYPE_V_Liability, as);
			int payablesServices_ID = getValidCombination_ID (Doc.ACCTTYPE_V_Liability_Services, as);
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
	} // createFact
	
	
	/**
	 * 
	 * @param docLine
	 * @param contractAcct
	 * @param C_AcctSchema_ID
	 * @return
	 */
	private int getRecognitionRevenueValidCombinationID(DocLine docLine, MContractAcct contractAcct,  int C_AcctSchema_ID)
	{
		MRecognitionLine line = (MRecognitionLine)docLine.getPO();
		//	Charge Account
		if (line.getM_Product_ID() == 0 && line.getC_Charge_ID() != 0)
		{
			return contractAcct.getContracChargeAcct(line.getC_Charge_ID(), C_AcctSchema_ID, false).getJP_Ch_Expense_Acct() ;
		
		}else if(line.getM_Product_ID() > 0){
			
			return contractAcct.getContractProductAcct(line.getM_Product().getM_Product_Category_ID(), C_AcctSchema_ID, false).getJP_Revenue_Acct() ;
		}
		
		return 0;
	}
	
	private int getInvoiceRevenueValidCombinationID(DocLine docLine, MContractAcct contractAcct,  int C_AcctSchema_ID)
	{
		MRecognitionLine line = (MRecognitionLine)docLine.getPO();
		//	Charge Account
		if (line.getM_Product_ID() == 0 && line.getC_Charge_ID() != 0)
		{
			return contractAcct.getContracChargeAcct(
					line.getC_Charge_ID(), C_AcctSchema_ID, false).getCh_Expense_Acct() ;
		
		}else if(line.getM_Product_ID() > 0){
			
			return contractAcct.getContractProductAcct(
					line.getM_Product().getM_Product_Category_ID(), C_AcctSchema_ID, false).getP_Revenue_Acct() ;
		}
		
		return 0;
	}
	
	private int getRecognitionTDiscountGrantValidCombinationID(DocLine docLine, MContractAcct contractAcct,  int C_AcctSchema_ID)
	{
		MRecognitionLine line = (MRecognitionLine)docLine.getPO();
		return contractAcct.getContractProductAcct(
				line.getM_Product().getM_Product_Category_ID(), C_AcctSchema_ID, false).getJP_TradeDiscountGrant_Acct() ;
	}
	
	private int getInvoiceTDiscountGrantValidCombinationID(DocLine docLine, MContractAcct contractAcct,  int C_AcctSchema_ID)
	{
		MRecognitionLine line = (MRecognitionLine)docLine.getPO();
		return contractAcct.getContractProductAcct(
				line.getM_Product().getM_Product_Category_ID(), C_AcctSchema_ID, false).getP_TradeDiscountGrant_Acct() ;
	}
	
} //
