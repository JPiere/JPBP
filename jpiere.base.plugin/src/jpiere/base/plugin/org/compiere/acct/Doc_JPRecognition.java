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

import org.compiere.acct.Doc;
import org.compiere.acct.DocLine;
import org.compiere.acct.DocTax;
import org.compiere.acct.Fact;
import org.compiere.acct.FactLine;
import org.compiere.model.MAccount;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MCharge;
import org.compiere.model.MCostDetail;
import org.compiere.model.MCurrency;
import org.compiere.model.MInOutLine;
import org.compiere.model.MInOutLineMA;
import org.compiere.model.MOrderLine;
import org.compiere.model.MProduct;
import org.compiere.model.MSysConfig;
import org.compiere.model.MTax;
import org.compiere.model.ProductCost;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.MContractAcct;
import jpiere.base.plugin.org.adempiere.model.MContractChargeAcct;
import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractProductAcct;
import jpiere.base.plugin.org.adempiere.model.MContractTaxAcct;
import jpiere.base.plugin.org.adempiere.model.MRecognition;
import jpiere.base.plugin.org.adempiere.model.MRecognitionLine;

/**
 * Post Recognition Documents.
 *
 *
 * JPIERE-0364: Recognition Document
 * JPIERE-0521: Add JP_Contract_ID, JP_ContractProcPeriod_ID Columns to Fact Acct Table
 * JPIERE-0536: Journal Policy of Recognition Doc if no accounting config
 *
 * <pre>
 *   Table:              JP_Recognition
 *   Document Types:     JPR,JPS,JPX,JPY
 * </pre>
 *
 * @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
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

	private int				m_Reversal_ID = 0;

	/**
	 *	Load Specific Document Details
	 *  @return error message or null
	 */
	protected String loadDocumentDetails ()
	{
		MRecognition recognition = (MRecognition)getPO();
		setDateDoc(recognition.getDateInvoiced());
		setIsTaxIncluded(recognition.isTaxIncluded());
		m_Reversal_ID = recognition.getReversal_ID();//store original (voided/reversed) document
		//	Amounts
		setAmount(Doc.AMTTYPE_Gross, recognition.getGrandTotal());
		setAmount(Doc.AMTTYPE_Net, recognition.getTotalLines());

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
			docLine.setReversalLine_ID(line.getReversalLine_ID());
			//	Qty
			BigDecimal Qty = line.getQtyInvoiced();
			boolean cm = getDocumentType().equals("JPS")//Recognition Revenu Credit memo
				|| getDocumentType().equals("JPY");//Recognition Expense Credit memo
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
		{
			return facts;
		}

		MContractContent contractContent = MContractContent.get(Env.getCtx(),recog.getJP_ContractContent_ID());
		if(contractContent.getJP_Contract_Acct_ID() == 0)
		{
			return facts;
		}

		MContractAcct contractAcct = MContractAcct.get(Env.getCtx(),contractContent.getJP_Contract_Acct_ID());
		if( !contractAcct.isPostingContractAcctJP() || !contractAcct.isPostingRecognitionDocJP() )
		{
			return facts;
		}

		//  create Fact Header
		Fact fact = new Fact(this, as, Fact.POST_Actual);

		//  Cash based accounting
		if (!as.isAccrual())
			return facts;

		//JPR
		if (getDocumentType().equals("JPR"))//Revenue Recognition
		{
			postJPR(as, contractAcct, fact);
		}
		//JPS
		else if (getDocumentType().equals("JPS"))//Revenue Recognition Credit Memo
		{
			postJPS(as, contractAcct, fact);;
		}
		//JPX
		else if (getDocumentType().equals("JPX")) //Expense Recognition
		{
			postJPX(as, contractAcct, fact);
		}
		//JPY
		else if (getDocumentType().equals("JPY")) //Expense Recognition - Credit memo
		{
			postJPY(as, contractAcct, fact);
		}
		else
		{
			p_Error = "DocumentType unknown: " + getDocumentType();
			log.log(Level.SEVERE, p_Error);
			fact = null;
		}

		FactLine[] fLines = fact.getLines();
		for (int i = 0; i < fLines.length; i++)
		{
			if (fLines[i] != null)
			{
				fLines[i].setLocationFromOrg(fLines[i].getAD_Org_ID(), true);      //  from Loc
				if(recog.getBill_BPartner_ID() > 0)
					fLines[i].setC_BPartner_ID(recog.getBill_BPartner_ID());

				if(recog.getBill_Location_ID() > 0)
					fLines[i].setLocationFromBPartner(recog.getBill_Location_ID(), false);  //  to Loc
				else
					fLines[i].setLocationFromBPartner(getC_BPartner_Location_ID(), false);  //  to Loc

				if(recog.getC_Order_ID() > 0)
					fLines[i].set_ValueNoCheck("JP_Order_ID", recog.getC_Order_ID());

				if(recog.getJP_Contract_ID() > 0)
					fLines[i].set_ValueNoCheck("JP_Contract_ID", recog.getJP_Contract_ID());

				if(recog.getJP_ContractContent_ID() > 0)
					fLines[i].set_ValueNoCheck("JP_ContractContent_ID", recog.getJP_ContractContent_ID());

				if(recog.getJP_ContractProcPeriod_ID() > 0)
					fLines[i].set_ValueNoCheck("JP_ContractProcPeriod_ID", recog.getJP_ContractProcPeriod_ID());
			}
		}//for


		//
		facts.add(fact);
		return facts;
	} // createFact


	/**
	 *
	 * Revenue Recognition
	 *
	 * @param as
	 * @param contractAcct
	 * @param fact
	 * @return
	 */
	private String postJPR(MAcctSchema as, MContractAcct contractAcct, Fact fact)
	{
		//  Line pointers
		FactLine dr = null;
		FactLine cr = null;

		BigDecimal amt = Env.ZERO;

		/*** Tax ***/
		//DR: Invoice  TaxDue      / CR:   Recognition TaxDue
		for (int i = 0; i < m_taxes.length; i++)
		{
			amt = m_taxes[i].getAmount();
			if (amt != null && amt.signum() != 0)
			{
				MAccount recognitionTaxDueAccount = getRecognitionTaxDueAccount(m_taxes[i], contractAcct, as);
				if(recognitionTaxDueAccount != null)
				{
					//DR
					FactLine taxLineDR = fact.createLine(null, getInvoiceTaxDueAccount(m_taxes[i], contractAcct, as), getC_Currency_ID(), amt, null);
					if (taxLineDR != null)
						taxLineDR.setC_Tax_ID(m_taxes[i].getC_Tax_ID());

					//CR
					FactLine taxLineCR = fact.createLine(null, recognitionTaxDueAccount, getC_Currency_ID(), null, amt);
					if (taxLineCR != null)
						taxLineCR.setC_Tax_ID(m_taxes[i].getC_Tax_ID());
				}
			}
		}//for


		/*** Revenue ***/
		//DR:  Invoice Revenue / CR: Recognition Revenue
		for (int i = 0; i < p_lines.length; i++)
		{
			DocLine line = p_lines[i];
			amt = p_lines[i].getAmtSource();

			//JPIERE-0369:Start
			int C_Charge_ID = line.getPO().get_ValueAsInt("C_Charge_ID");
			if(C_Charge_ID != 0)
			{
				MCharge charge = MCharge.get(getCtx(), C_Charge_ID);
				if(!charge.isSameTax() && charge.isTaxIncluded())
				{
					amt = (BigDecimal)line.getPO().get_Value("JP_TaxBaseAmt");
				}
			}
			//JPIERE-0369:finish

			BigDecimal dAmt = null;
			if (as.isTradeDiscountPosted())
			{
				BigDecimal discount = p_lines[i].getDiscount();
				if (discount != null && discount.signum() != 0)
				{
					amt = amt.add(discount);
					dAmt = discount;
					//CR  - Recognition Trade Deiscount Acct
					fact.createLine (line, getRecognitionTDiscountGrantAccount(line, contractAcct,  as), getC_Currency_ID(), dAmt, null);

					//DR  - Invoice Trade Deiscount Acct
					fact.createLine (line, getInvoiceTDiscountGrantAccount(line, contractAcct,  as), getC_Currency_ID(), null, dAmt);
				}
			}

			//DR - Invoice Revenue Acct
			dr = fact.createLine (line, getInvoiceRevenueAccount(line, contractAcct,  as), getC_Currency_ID(), amt, null);
			if(dr != null)
				dr.setQty(line.getQty().negate());

			//CR - Recognition Revenue Acct
			cr = fact.createLine (line, getRecognitionRevenueAccount(line, contractAcct,  as), getC_Currency_ID(), null, amt);
			if(cr != null)
				cr.setQty(line.getQty());


			/*** COGS ***/
			boolean JP_RECOGNITION_COGS_SCHEDULED_COST = MSysConfig.getBooleanValue("JP_RECOGNITION_COGS_SCHEDULED_COST", false, getAD_Client_ID(), getAD_Org_ID());
			MProduct product = line.getProduct();
			BigDecimal costs = Env.ZERO;
			//Scheduled Cost
			if(JP_RECOGNITION_COGS_SCHEDULED_COST)
			{
				if(line.getC_OrderLine_ID() > 0)
				{
					MOrderLine oLine = new MOrderLine(getCtx(), line.getC_OrderLine_ID(), getTrxName() );
					costs  = (BigDecimal)oLine.get_Value("JP_ScheduledCostLineAmt");
				}

				if(costs.compareTo(Env.ZERO) != 0)
				{
					//  CoGS            DR
					dr = fact.createLine(line, getCOGSAccount(line, contractAcct, as), as.getC_Currency_ID(), costs, null);
					if (dr == null)
					{
						p_Error = "FactLine DR not created: " + line;
						log.log(Level.WARNING, p_Error);
						return null;
					}

					int M_InOutLine_ID = line.getPO().get_ValueAsInt("M_InOutLine_ID");
					if(M_InOutLine_ID > 0)
					{
						MInOutLine ioLine =	new MInOutLine(getCtx(),M_InOutLine_ID, getTrxName());
						dr.setM_Locator_ID(ioLine.getM_Locator_ID());
						dr.setLocationFromLocator(ioLine.getM_Locator_ID(), true);    //  from Loc
					}

					dr.setAD_Org_ID(line.getOrder_Org_ID());		//	Revenue X-Org
					dr.setQty(line.getQty().negate());


					//  Inventory               CR
					cr = fact.createLine(line, getAssetAccount(line, contractAcct, as), as.getC_Currency_ID(), null, costs);
					if (cr == null)
					{
						p_Error = "FactLine CR not created: " + line;
						log.log(Level.WARNING, p_Error);
						return null;
					}
					cr.setM_Locator_ID(line.getM_Locator_ID());
					cr.setLocationFromLocator(line.getM_Locator_ID(), true);    // from Loc
				}

			//Current Cost
			}else{

				if (!isReversal(line) && product != null)
				{
					if (MAcctSchema.COSTINGLEVEL_BatchLot.equals(product.getCostingLevel(as)) )
					{
						if (line.getM_AttributeSetInstance_ID() == 0 )
						{
							MInOutLine ioLine = (MInOutLine) line.getPO();
							MInOutLineMA mas[] = MInOutLineMA.get(getCtx(), ioLine.get_ID(), getTrxName());
							if (mas != null && mas.length > 0 )
							{
								costs  = BigDecimal.ZERO;
								for (int j = 0; j < mas.length; j++)
								{
									MInOutLineMA ma = mas[j];
									BigDecimal QtyMA = ma.getMovementQty();
									ProductCost pc = line.getProductCost();
									pc.setQty(QtyMA);
									pc.setM_M_AttributeSetInstance_ID(ma.getM_AttributeSetInstance_ID());
									BigDecimal maCosts = line.getProductCosts(as, line.getAD_Org_ID(), true, "M_InOutLine_ID=?");

									costs = costs.add(maCosts);
								}
							}
						}
						else
						{
							costs = line.getProductCosts(as, line.getAD_Org_ID(), true, "M_InOutLine_ID=?");
						}
					}
					else
					{
						// MZ Goodwill
						// if Shipment CostDetail exist then get Cost from Cost Detail
						costs = line.getProductCosts(as, line.getAD_Org_ID(), true, "M_InOutLine_ID=?");
					}

					// end MZ
					if (costs == null || costs.signum() == 0)	//	zero costs OK
					{
						if (product.isStocked())
						{
							//ok if we have purchased zero cost item from vendor before
							int count = DB.getSQLValue(null, "SELECT Count(*) FROM M_CostDetail WHERE M_Product_ID=? AND Processed='Y' AND Amt=0.00 AND Qty > 0 AND (C_OrderLine_ID > 0 OR C_InvoiceLine_ID > 0)",
									product.getM_Product_ID());
							if (count > 0)
							{
								costs = BigDecimal.ZERO;
							}
							else
							{
								p_Error = "No Costs for " + line.getProduct().getName();
								log.log(Level.WARNING, p_Error);
								return null;
							}
						}
						else	//	ignore service
							continue;
					}
				}
				else
				{
					//temp to avoid NPE
					costs = BigDecimal.ZERO;
				}

				if(costs.compareTo(Env.ZERO) != 0)
				{
					//  CoGS            DR
					dr = fact.createLine(line,
						getCOGSAccount(line, contractAcct, as),
						as.getC_Currency_ID(), costs, null);
					if (dr == null)
					{
						p_Error = "FactLine DR not created: " + line;
						log.log(Level.WARNING, p_Error);
						return null;
					}
					dr.setM_Locator_ID(line.getM_Locator_ID());
					dr.setLocationFromLocator(line.getM_Locator_ID(), true);    //  from Loc
					dr.setLocationFromBPartner(getC_BPartner_Location_ID(), false);  //  to Loc
					dr.setAD_Org_ID(line.getOrder_Org_ID());		//	Revenue X-Org
					dr.setQty(line.getQty().negate());

					if (isReversal(line))
					{
						//	Set AmtAcctDr from Original Shipment/Receipt
						if (!dr.updateReverseLine (MRecognition.Table_ID,
								m_Reversal_ID, line.getReversalLine_ID(),Env.ONE))
						{
							if (product != null && !product.isStocked())	{ //	ignore service
								fact.remove(dr);
								continue;
							}
							p_Error = "Original Shipment/Receipt not posted yet";
							return null;
						}
					}

					//  Inventory               CR
					cr = fact.createLine(line,
						line.getAccount(ProductCost.ACCTTYPE_P_Asset, as),
						as.getC_Currency_ID(), null, costs);
					if (cr == null)
					{
						p_Error = "FactLine CR not created: " + line;
						log.log(Level.WARNING, p_Error);
						return null;
					}
					cr.setM_Locator_ID(line.getM_Locator_ID());
					cr.setLocationFromLocator(line.getM_Locator_ID(), true);    // from Loc
					cr.setLocationFromBPartner(getC_BPartner_Location_ID(), false);  // to Loc

					if (isReversal(line))
					{
						//	Set AmtAcctCr from Original Shipment/Receipt
						if (!cr.updateReverseLine (MRecognition.Table_ID,
								m_Reversal_ID, line.getReversalLine_ID(),Env.ONE))
						{
							p_Error = "Original Shipment/Receipt not posted yet";
							return null;
						}
						costs = cr.getAcctBalance(); //get original cost
					}
				}//if(costs.compareTo(Env.ZERO) != 0)
			}//COGS

			/*** Create Cost Detail ***/
			MRecognitionLine recogLine = (MRecognitionLine) line.getPO();
			if(recogLine.getM_InOutLine_ID() > 0  && product != null )
			{
				MInOutLine ioLine = new MInOutLine(getCtx(), recogLine.getM_InOutLine_ID(),getTrxName());
				if (MAcctSchema.COSTINGLEVEL_BatchLot.equals(product.getCostingLevel(as)) )
				{
					if (line.getM_AttributeSetInstance_ID() == 0 )
					{
						MInOutLineMA mas[] = MInOutLineMA.get(getCtx(), ioLine.get_ID(), getTrxName());
						if (mas != null && mas.length > 0 )
						{
							for (int j = 0; j < mas.length; j++)
							{
								MInOutLineMA ma = mas[j];
								if (!MCostDetail.createShipment(as, ioLine.getAD_Org_ID(),
										ioLine.getM_Product_ID(), ma.getM_AttributeSetInstance_ID(),
										ioLine.get_ID(), 0,
										costs, ma.getMovementQty().negate(),
										ioLine.getDescription(), true, getTrxName()))
								{
									p_Error = "Failed to create cost detail record";
									return null;
								}
							}
						}
					}
					else
					{
						//
						if (ioLine.getM_Product_ID() != 0)
						{
							if (!MCostDetail.createShipment(as, ioLine.getAD_Org_ID(),
								ioLine.getM_Product_ID(), ioLine.getM_AttributeSetInstance_ID(),
								ioLine.get_ID(), 0,
								costs, ioLine.getMovementQty().negate(),
								ioLine.getDescription(), true, getTrxName()))
							{
								p_Error = "Failed to create cost detail record";
								return null;
							}
						}
					}
				}
				else
				{
					//
					if (ioLine.getM_Product_ID() != 0)
					{
						if (!MCostDetail.createShipment(as, ioLine.getAD_Org_ID(),
							ioLine.getM_Product_ID(), ioLine.getM_AttributeSetInstance_ID(),
							ioLine.get_ID(), 0,
							costs, ioLine.getMovementQty().negate(),
							ioLine.getDescription(), true, getTrxName()))
						{
							p_Error = "Failed to create cost detail record";
							return null;
						}
					}
				}

			}//Create Cost Detail

		}	//	for all lines

		return null;
	}//JPR


	/**
	 *
	 * Revenue Recognition Credit Memo
	 *
	 * @param as
	 * @param contractAcct
	 * @param fact
	 * @return
	 */
	private String postJPS(MAcctSchema as, MContractAcct contractAcct, Fact fact)
	{
		//  Line pointers
		FactLine dr = null;
		FactLine cr = null;

		BigDecimal amt = Env.ZERO;

		//DR: Invoice  TaxDue      / CR:   Recognition TaxDue
		for (int i = 0; i < m_taxes.length; i++)
		{
			amt = m_taxes[i].getAmount();
			if (amt != null && amt.signum() != 0)
			{
				MAccount recognitionTaxDueAccount = getRecognitionTaxDueAccount(m_taxes[i], contractAcct, as);
				if(recognitionTaxDueAccount != null)
				{
					//DR -> CR
					FactLine taxLineDR = fact.createLine(null, getInvoiceTaxDueAccount(m_taxes[i], contractAcct, as), getC_Currency_ID(), null, amt);
					if (taxLineDR != null)
						taxLineDR.setC_Tax_ID(m_taxes[i].getC_Tax_ID());

					//CR -> DR
					FactLine taxLineCR = fact.createLine(null, recognitionTaxDueAccount, getC_Currency_ID(), amt, null);
					if (taxLineCR != null)
						taxLineCR.setC_Tax_ID(m_taxes[i].getC_Tax_ID());
				}
			}
		}//for

		//DR:  Invoice Revenue / CR: Recognition Revenue
		for (int i = 0; i < p_lines.length; i++)
		{
			DocLine line = p_lines[i];
			amt = p_lines[i].getAmtSource();
			//JPIERE-0369:Start
			int C_Charge_ID = line.getPO().get_ValueAsInt("C_Charge_ID");
			if(C_Charge_ID != 0)
			{
				MCharge charge = MCharge.get(getCtx(), C_Charge_ID);
				if(!charge.isSameTax() && charge.isTaxIncluded())
				{
					amt = (BigDecimal)line.getPO().get_Value("JP_TaxBaseAmt");
				}
			}
			//JPIERE-0369:finish

			BigDecimal dAmt = null;
			if (as.isTradeDiscountPosted())
			{
				BigDecimal discount = p_lines[i].getDiscount();
				if (discount != null && discount.signum() != 0)
				{
					amt = amt.add(discount);
					dAmt = discount;
					//CR  - Recognition Trade Deiscount Acct -> DR
					fact.createLine (line, getRecognitionTDiscountGrantAccount(line, contractAcct,  as), getC_Currency_ID(), null, dAmt);

					//DR  - Invoice Trade Deiscount Acct -> CR
					fact.createLine (line, getInvoiceTDiscountGrantAccount(line, contractAcct,  as), getC_Currency_ID(), dAmt, null);
				}
			}

			//DR - Invoice Revenue Acct -> CR
			dr = fact.createLine (line, getInvoiceRevenueAccount(line, contractAcct,  as), getC_Currency_ID(), null, amt);
			dr.setQty(line.getQty().negate());

			//CR - Recognition Revenue Acct -> DR
			cr = fact.createLine (line, getRecognitionRevenueAccount(line, contractAcct,  as), getC_Currency_ID(), amt, null);
			cr.setQty(line.getQty());


			/***COGS***/
			boolean JP_RECOGNITION_COGS_SCHEDULED_COST = MSysConfig.getBooleanValue("JP_RECOGNITION_COGS_SCHEDULED_COST", false, getAD_Client_ID(), getAD_Org_ID());
			MProduct product = line.getProduct();
			BigDecimal costs = Env.ZERO;
			if(JP_RECOGNITION_COGS_SCHEDULED_COST)
			{

				if(line.getC_OrderLine_ID() > 0)
				{
					MOrderLine oLine = new MOrderLine(getCtx(), line.getC_OrderLine_ID(), getTrxName() );
					costs  = (BigDecimal)oLine.get_Value("JP_ScheduledCostLineAmt");
				}

				if(costs.compareTo(Env.ZERO) != 0)
				{
					//  CoGS            DR -> CR
					dr = fact.createLine(line,	getCOGSAccount(line, contractAcct, as),	as.getC_Currency_ID(), null, costs);
					if (dr == null)
					{
						p_Error = "FactLine DR not created: " + line;
						log.log(Level.WARNING, p_Error);
						return null;
					}

					int M_InOutLine_ID = line.getPO().get_ValueAsInt("M_InOutLine_ID");
					if(M_InOutLine_ID > 0)
					{
						MInOutLine ioLine =	new MInOutLine(getCtx(),M_InOutLine_ID, getTrxName());
						dr.setM_Locator_ID(ioLine.getM_Locator_ID());
						dr.setLocationFromLocator(ioLine.getM_Locator_ID(), true);    //  from Loc
					}

					dr.setAD_Org_ID(line.getOrder_Org_ID());		//	Revenue X-Org
					dr.setQty(line.getQty().negate());


					//  Inventory               CR -> DR
					cr = fact.createLine(line, getAssetAccount(line, contractAcct, as),	as.getC_Currency_ID(), costs, null);
					if (cr == null)
					{
						p_Error = "FactLine CR not created: " + line;
						log.log(Level.WARNING, p_Error);
						return null;
					}
					cr.setM_Locator_ID(line.getM_Locator_ID());
					cr.setLocationFromLocator(line.getM_Locator_ID(), true);    // from Loc
				}

			}else{

				if (!isReversal(line) && product != null)
				{
					if (MAcctSchema.COSTINGLEVEL_BatchLot.equals(product.getCostingLevel(as)) )
					{
						if (line.getM_AttributeSetInstance_ID() == 0 )
						{
							MInOutLine ioLine = (MInOutLine) line.getPO();
							MInOutLineMA mas[] = MInOutLineMA.get(getCtx(), ioLine.get_ID(), getTrxName());
							costs = BigDecimal.ZERO;
							if (mas != null && mas.length > 0 )
							{
								for (int j = 0; j < mas.length; j++)
								{
									MInOutLineMA ma = mas[j];
									BigDecimal QtyMA = ma.getMovementQty();
									ProductCost pc = line.getProductCost();
									pc.setQty(QtyMA);
									pc.setM_M_AttributeSetInstance_ID(ma.getM_AttributeSetInstance_ID());
									BigDecimal maCosts = line.getProductCosts(as, line.getAD_Org_ID(), true, "M_InOutLine_ID=?");

									costs = costs.add(maCosts);
								}
							}
						}
						else
						{
							costs = line.getProductCosts(as, line.getAD_Org_ID(), true, "M_InOutLine_ID=?");
						}
					}
					else
					{
						// MZ Goodwill
						// if Shipment CostDetail exist then get Cost from Cost Detail
						costs = line.getProductCosts(as, line.getAD_Org_ID(), true, "M_InOutLine_ID=?");
						// end MZ
					}
					if (costs == null || costs.signum() == 0)	//	zero costs OK
					{
						if (product.isStocked())
						{
							p_Error = "No Costs for " + line.getProduct().getName();
							log.log(Level.WARNING, p_Error);
							return null;
						}
						else	//	ignore service
							continue;
					}
				}
				else
				{
					costs = BigDecimal.ZERO;
				}


				if(costs.compareTo(Env.ZERO) != 0)
				{
					//  Inventory               DR
					dr = fact.createLine(line,
						line.getAccount(ProductCost.ACCTTYPE_P_Asset, as),
						as.getC_Currency_ID(), costs.negate(), null);
					if (dr == null)
					{
						p_Error = "FactLine DR not created: " + line;
						log.log(Level.WARNING, p_Error);
						return null;
					}
					dr.setM_Locator_ID(line.getM_Locator_ID());
					dr.setLocationFromLocator(line.getM_Locator_ID(), true);    // from Loc
					dr.setLocationFromBPartner(getC_BPartner_Location_ID(), false);  // to Loc
					if (isReversal(line))
					{
						//	Set AmtAcctDr from Original Shipment/Receipt
						if (!dr.updateReverseLine (MRecognition.Table_ID,
								m_Reversal_ID, line.getReversalLine_ID(),Env.ONE))
						{
							if (product != null && !product.isStocked())	{ //	ignore service
								fact.remove(dr);
								continue;
							}
							p_Error = "Original Shipment/Receipt not posted yet";
							return null;
						}
						costs = dr.getAcctBalance(); //get original cost
					}

					//  CoGS            CR
					cr = fact.createLine(line,
						getCOGSAccount(line, contractAcct, as),
						as.getC_Currency_ID(), null, costs.negate());
					if (cr == null)
					{
						p_Error = "FactLine CR not created: " + line;
						log.log(Level.WARNING, p_Error);
						return null;
					}
					cr.setM_Locator_ID(line.getM_Locator_ID());
					cr.setLocationFromLocator(line.getM_Locator_ID(), true);    //  from Loc
					cr.setLocationFromBPartner(getC_BPartner_Location_ID(), false);  //  to Loc
					cr.setAD_Org_ID(line.getOrder_Org_ID());		//	Revenue X-Org
					cr.setQty(line.getQty().negate());
					if (isReversal(line))
					{
						//	Set AmtAcctCr from Original Shipment/Receipt
						if (!cr.updateReverseLine (MRecognition.Table_ID,
								m_Reversal_ID, line.getReversalLine_ID(),Env.ONE))
						{
							p_Error = "Original Shipment/Receipt not posted yet";
							return null;
						}
					}
				}//if(costs.compareTo(Env.ZERO) != 0)
			}//COGS

			/*** Create Cost Detail ***/
			MRecognitionLine recogLine = (MRecognitionLine) line.getPO();
			if(recogLine.getM_InOutLine_ID() > 0  && product != null)
			{
				MInOutLine ioLine = new MInOutLine(getCtx(), recogLine.getM_InOutLine_ID(),getTrxName());
				if (MAcctSchema.COSTINGLEVEL_BatchLot.equals(product.getCostingLevel(as)) )
				{
					if (ioLine.getM_AttributeSetInstance_ID() == 0 )
					{
						MInOutLineMA mas[] = MInOutLineMA.get(getCtx(), ioLine.get_ID(), getTrxName());
						if (mas != null && mas.length > 0 )
						{
							for (int j = 0; j < mas.length; j++)
							{
								MInOutLineMA ma = mas[j];
								if (!MCostDetail.createShipment(as, ioLine.getAD_Org_ID(),
										ioLine.getM_Product_ID(), ma.getM_AttributeSetInstance_ID(),
										ioLine.get_ID(), 0,
										costs, ma.getMovementQty(),
										ioLine.getDescription(), true, getTrxName()))
								{
									p_Error = "Failed to create cost detail record";
									return null;
								}
							}
						}
					} else
					{
						if (ioLine.getM_Product_ID() != 0)
						{
							if (!MCostDetail.createShipment(as, ioLine.getAD_Org_ID(),
									ioLine.getM_Product_ID(), ioLine.getM_AttributeSetInstance_ID(),
									ioLine.get_ID(), 0,
								costs, ioLine.getMovementQty(),
								ioLine.getDescription(), true, getTrxName()))
							{
								p_Error = "Failed to create cost detail record";
								return null;
							}
						}
					}
				} else
				{
					//
					if (line.getM_Product_ID() != 0)
					{
						if (!MCostDetail.createShipment(as, ioLine.getAD_Org_ID(),
								ioLine.getM_Product_ID(), ioLine.getM_AttributeSetInstance_ID(),
								ioLine.get_ID(), 0,
								costs, ioLine.getMovementQty(),
								ioLine.getDescription(), true, getTrxName()))
						{
							p_Error = "Failed to create cost detail record";
							return null;
						}
					}
				}

			}

		}	//	for all lines

		return null;
	}//JPS


	/**
	 *
	 * Expense Recognition
	 *
	 * @param as
	 * @param contractAcct
	 * @param fact
	 * @return
	 */
	private String postJPX(MAcctSchema as, MContractAcct contractAcct, Fact fact)
	{
		//  Line pointers
		FactLine dr = null;
		FactLine cr = null;

		BigDecimal amt = Env.ZERO;

		//DR: Invoice  TaxDue      / CR:   Recognition TaxDue
		for (int i = 0; i < m_taxes.length; i++)
		{
			amt = m_taxes[i].getAmount();
			if (amt != null && amt.signum() != 0)
			{
				MAccount recognitionTaxCreditAccount = getRecognitionTaxCreditAccount(m_taxes[i], contractAcct, as);
				if(recognitionTaxCreditAccount != null)
				{
					//CR
					FactLine taxLineDR = fact.createLine(null, getInvoiceTaxCreditAccount(m_taxes[i], contractAcct, as), getC_Currency_ID(), null, amt);
					if (taxLineDR != null)
						taxLineDR.setC_Tax_ID(m_taxes[i].getC_Tax_ID());

					//DR
					FactLine taxLineCR = fact.createLine(null, getRecognitionTaxCreditAccount(m_taxes[i], contractAcct, as), getC_Currency_ID(), amt, null);
					if (taxLineCR != null)
						taxLineCR.setC_Tax_ID(m_taxes[i].getC_Tax_ID());
				}
			}
		}//for

		//
		for (int i = 0; i < p_lines.length; i++)
		{
			DocLine line = p_lines[i];
			amt = p_lines[i].getAmtSource();
			//JPIERE-0369:Start
			int C_Charge_ID = line.getPO().get_ValueAsInt("C_Charge_ID");
			if(C_Charge_ID != 0)
			{
				MCharge charge = MCharge.get(getCtx(), C_Charge_ID);
				if(!charge.isSameTax() && charge.isTaxIncluded())
				{
					amt = (BigDecimal)line.getPO().get_Value("JP_TaxBaseAmt");
				}
			}
			//JPIERE-0369:finish

			BigDecimal dAmt = null;
			if (as.isTradeDiscountPosted())
			{
				BigDecimal discount = p_lines[i].getDiscount();
				if (discount != null && discount.signum() != 0)
				{
					MAccount account = getRecognitionTDiscountRecAccount(line, contractAcct,  as);
					if(account != null)
					{
						amt = amt.add(discount);
						dAmt = discount;
						//CR
						fact.createLine (line, getRecognitionTDiscountRecAccount(line, contractAcct,  as), getC_Currency_ID(), null, dAmt);

						//DR
						fact.createLine (line, getInvoiceTDiscountRecAccount(line, contractAcct,  as), getC_Currency_ID(), dAmt, null);
					}
				}
			}

			MAccount account = getRecognitionExpenseAccount(line, contractAcct,  as);
			if(account != null)
			{
				//CR - Invoice Expense Acct
				dr = fact.createLine (line, getInvoiceExpenseAccount(line, contractAcct,  as), getC_Currency_ID(), null, amt);
				if(dr != null)
				{
					dr.setQty(line.getQty().negate());
				}

				//DR - Recognition Expense Acct
				cr = fact.createLine (line, getRecognitionExpenseAccount(line, contractAcct,  as), getC_Currency_ID(), amt, null);
				if(cr != null)
				{
					cr.setQty(line.getQty());

					cr.setM_Locator_ID(line.getM_Locator_ID());
					cr.setLocationFromLocator(line.getM_Locator_ID(), true);    // from Loc
				}
			}

		}	//	for all lines

		return null;
	}//JPX


	/**
	 *
	 * Expense Recognition - Credit memo
	 *
	 * @param as
	 * @param contractAcct
	 * @param fact
	 * @return
	 */
	private String postJPY(MAcctSchema as, MContractAcct contractAcct, Fact fact)
	{
		//  Line pointers
		FactLine dr = null;
		FactLine cr = null;

		BigDecimal amt = Env.ZERO;

		//DR: Invoice  TaxDue      / CR:   Recognition TaxDue
		for (int i = 0; i < m_taxes.length; i++)
		{
			amt = m_taxes[i].getAmount();
			if (amt != null && amt.signum() != 0)
			{
				MAccount recognitionTaxCreditAccount = getRecognitionTaxCreditAccount(m_taxes[i], contractAcct, as);
				if(recognitionTaxCreditAccount != null)
				{
					//CR
					FactLine taxLineDR = fact.createLine(null, getInvoiceTaxCreditAccount(m_taxes[i], contractAcct, as), getC_Currency_ID(), amt, null);
					if (taxLineDR != null)
						taxLineDR.setC_Tax_ID(m_taxes[i].getC_Tax_ID());

					//DR
					FactLine taxLineCR = fact.createLine(null, getRecognitionTaxCreditAccount(m_taxes[i], contractAcct, as), getC_Currency_ID(), null, amt);
					if (taxLineCR != null)
						taxLineCR.setC_Tax_ID(m_taxes[i].getC_Tax_ID());
				}
			}
		}//for

		//
		for (int i = 0; i < p_lines.length; i++)
		{
			DocLine line = p_lines[i];
			amt = p_lines[i].getAmtSource();
			//JPIERE-0369:Start
			int C_Charge_ID = line.getPO().get_ValueAsInt("C_Charge_ID");
			if(C_Charge_ID != 0)
			{
				MCharge charge = MCharge.get(getCtx(), C_Charge_ID);
				if(!charge.isSameTax() && charge.isTaxIncluded())
				{
					amt = (BigDecimal)line.getPO().get_Value("JP_TaxBaseAmt");
				}
			}
			//JPIERE-0369:finish

			BigDecimal dAmt = null;
			if (as.isTradeDiscountPosted())
			{
				BigDecimal discount = p_lines[i].getDiscount();
				if (discount != null && discount.signum() != 0)
				{
					amt = amt.add(discount);
					dAmt = discount;

					MAccount account = getRecognitionTDiscountRecAccount(line, contractAcct,  as);
					if(account != null)
					{
						//CR
						fact.createLine (line, getRecognitionTDiscountRecAccount(line, contractAcct,  as), getC_Currency_ID(), dAmt, null);

						//DR
						fact.createLine (line, getInvoiceTDiscountRecAccount(line, contractAcct,  as), getC_Currency_ID(), null, dAmt);
					}
				}
			}

			MAccount account = getRecognitionExpenseAccount(line, contractAcct,  as);
			if(account != null)
			{
				//CR - Invoice Expense Acct -> DR
				dr = fact.createLine (line, getInvoiceExpenseAccount(line, contractAcct,  as), getC_Currency_ID(), amt, null);
				if(dr != null)
				{
					dr.setQty(line.getQty().negate());
				}

				//DR - Recognition Expense Acct -> CR
				cr = fact.createLine (line, getRecognitionExpenseAccount(line, contractAcct,  as), getC_Currency_ID(), null, amt);
				if(cr != null)
				{
					cr.setQty(line.getQty());

					cr.setM_Locator_ID(line.getM_Locator_ID());
					cr.setLocationFromLocator(line.getM_Locator_ID(), true);    // from Loc
				}
			}

		}	//	for all lines

		return null;
	}//JPY


	/***
	 *
	 *
	 * @param docLine
	 * @param contractAcct
	 * @param as
	 * @return
	 */
	private MAccount getRecognitionRevenueAccount(DocLine docLine, MContractAcct contractAcct,  MAcctSchema as)
	{
		MRecognitionLine line = (MRecognitionLine)docLine.getPO();

		if (line.getM_Product_ID() == 0 && line.getC_Charge_ID() != 0)
		{
			MContractChargeAcct contractChargeAcct =  contractAcct.getContracChargeAcct(line.getC_Charge_ID(), as.getC_AcctSchema_ID(), false);
			if(contractChargeAcct != null && contractChargeAcct.getJP_Ch_Expense_Acct() > 0)
			{
				return MAccount.get(getCtx(), contractChargeAcct.getJP_Ch_Expense_Acct());
			}else{
				return docLine.getAccount (ProductCost.ACCTTYPE_P_Revenue, as);
			}

		}else if(line.getM_Product_ID() > 0){
			MContractProductAcct contractProductAcct = contractAcct.getContractProductAcct(line.getM_Product().getM_Product_Category_ID(), as.getC_AcctSchema_ID(), false);
			if(contractProductAcct != null && contractProductAcct.getJP_Revenue_Acct() > 0)
			{
				return MAccount.get(getCtx(),contractProductAcct.getJP_Revenue_Acct());
			}else{
				return docLine.getAccount (ProductCost.ACCTTYPE_P_Revenue, as);
			}
		}else{
			return docLine.getAccount (ProductCost.ACCTTYPE_P_Revenue, as);
		}
	}


	private MAccount getInvoiceRevenueAccount(DocLine docLine, MContractAcct contractAcct,  MAcctSchema as)
	{

		MRecognitionLine line = (MRecognitionLine)docLine.getPO();
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


	private MAccount getRecognitionExpenseAccount(DocLine docLine, MContractAcct contractAcct,  MAcctSchema as)
	{
		MRecognitionLine line = (MRecognitionLine)docLine.getPO();
		//Charge Account
		if (line.getM_Product_ID() == 0 && line.getC_Charge_ID() != 0)
		{
			MContractChargeAcct contractChargeAcct =  contractAcct.getContracChargeAcct(line.getC_Charge_ID(), as.getC_AcctSchema_ID(), false);
			if(contractChargeAcct != null && contractChargeAcct.getJP_Ch_Expense_Acct() > 0)
			{
				return MAccount.get(getCtx(), contractChargeAcct.getJP_Ch_Expense_Acct());

			}else{

				String JP_Recognition_JournalPolicy =contractAcct.getJP_Recognition_JournalPolicy();
				if(Util.isEmpty(JP_Recognition_JournalPolicy))
				{
					JP_Recognition_JournalPolicy = MContractAcct.JP_RECOGNITION_JOURNALPOLICY_IfNoConfigTheJournalWillNotBePosted;
				}

				if(JP_Recognition_JournalPolicy.equals(MContractAcct.JP_RECOGNITION_JOURNALPOLICY_IfNoConfigTheJournalWillNotBePosted))//NN
				{
					return null;

				}else if(JP_Recognition_JournalPolicy.equals(MContractAcct.JP_RECOGNITION_JOURNALPOLICY_IfNoConfigWillBePostedByDefaultButTaxBeExcluded)) {//DN

					return docLine.getAccount(ProductCost.ACCTTYPE_P_Expense, as);

				}else if(JP_Recognition_JournalPolicy.equals(MContractAcct.JP_RECOGNITION_JOURNALPOLICY_IfNoConfigWillBePostedByDefaultAccount)) {//DD

					return docLine.getAccount(ProductCost.ACCTTYPE_P_Expense, as);
				}

				return docLine.getAccount(ProductCost.ACCTTYPE_P_Expense, as);
			}

		}else if(line.getM_Product_ID() > 0){

			if(docLine.isItem())
			{
				MContractProductAcct contractProductAcct = contractAcct.getContractProductAcct(line.getM_Product().getM_Product_Category_ID(), as.getC_AcctSchema_ID(), false);
				if(contractProductAcct != null && contractProductAcct.getJP_PurchaseOffset_Acct() > 0 && contractProductAcct.getJP_Purchase_Acct() > 0)
				{
					return MAccount.get(getCtx(),contractProductAcct.getJP_Purchase_Acct());

				}else{

					String JP_Recognition_JournalPolicy =contractAcct.getJP_Recognition_JournalPolicy();
					if(Util.isEmpty(JP_Recognition_JournalPolicy))
					{
						JP_Recognition_JournalPolicy = MContractAcct.JP_RECOGNITION_JOURNALPOLICY_IfNoConfigTheJournalWillNotBePosted;
					}

					if(JP_Recognition_JournalPolicy.equals(MContractAcct.JP_RECOGNITION_JOURNALPOLICY_IfNoConfigTheJournalWillNotBePosted))//NN
					{
						return null;

					}else if(JP_Recognition_JournalPolicy.equals(MContractAcct.JP_RECOGNITION_JOURNALPOLICY_IfNoConfigWillBePostedByDefaultButTaxBeExcluded)) {//DN

						return docLine.getAccount(ProductCost.ACCTTYPE_P_InventoryClearing, as);

					}else if(JP_Recognition_JournalPolicy.equals(MContractAcct.JP_RECOGNITION_JOURNALPOLICY_IfNoConfigWillBePostedByDefaultAccount)) {//DD

						return docLine.getAccount(ProductCost.ACCTTYPE_P_InventoryClearing, as);
					}

					return docLine.getAccount(ProductCost.ACCTTYPE_P_InventoryClearing, as);
				}

			}else{

				MContractProductAcct contractProductAcct = contractAcct.getContractProductAcct(line.getM_Product().getM_Product_Category_ID(), as.getC_AcctSchema_ID(), false);
				if(contractProductAcct != null && contractProductAcct.getJP_Expense_Acct() > 0)
				{
					return MAccount.get(getCtx(),contractProductAcct.getJP_Expense_Acct());

				}else{

					String JP_Recognition_JournalPolicy =contractAcct.getJP_Recognition_JournalPolicy();
					if(Util.isEmpty(JP_Recognition_JournalPolicy))
					{
						JP_Recognition_JournalPolicy = MContractAcct.JP_RECOGNITION_JOURNALPOLICY_IfNoConfigTheJournalWillNotBePosted;
					}

					if(JP_Recognition_JournalPolicy.equals(MContractAcct.JP_RECOGNITION_JOURNALPOLICY_IfNoConfigTheJournalWillNotBePosted))//NN
					{
						return null;

					}else if(JP_Recognition_JournalPolicy.equals(MContractAcct.JP_RECOGNITION_JOURNALPOLICY_IfNoConfigWillBePostedByDefaultButTaxBeExcluded)) {//DN

						return docLine.getAccount(ProductCost.ACCTTYPE_P_Expense, as);

					}else if(JP_Recognition_JournalPolicy.equals(MContractAcct.JP_RECOGNITION_JOURNALPOLICY_IfNoConfigWillBePostedByDefaultAccount)) {//DD

						return docLine.getAccount(ProductCost.ACCTTYPE_P_Expense, as);
					}

					return docLine.getAccount(ProductCost.ACCTTYPE_P_Expense, as);
				}
			}
		}else{
			return docLine.getAccount (ProductCost.ACCTTYPE_P_Expense, as);
		}
	}


	private MAccount getInvoiceExpenseAccount(DocLine docLine, MContractAcct contractAcct,  MAcctSchema as)
	{
		MRecognitionLine line = (MRecognitionLine)docLine.getPO();
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
				MContractProductAcct contractProductAcct = contractAcct.getContractProductAcct(line.getM_Product().getM_Product_Category_ID(), as.getC_AcctSchema_ID(), false);
				if(contractProductAcct != null && contractProductAcct.getJP_PurchaseOffset_Acct() > 0 && contractProductAcct.getJP_Purchase_Acct() > 0)
				{
					return MAccount.get(getCtx(),contractProductAcct.getJP_PurchaseOffset_Acct());
				}else{

					return docLine.getAccount(ProductCost.ACCTTYPE_P_InventoryClearing, as);
				}

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

	private MAccount getRecognitionTDiscountGrantAccount(DocLine docLine, MContractAcct contractAcct, MAcctSchema as)
	{
		MRecognitionLine line = (MRecognitionLine)docLine.getPO();

		MContractProductAcct contractProductAcct = contractAcct.getContractProductAcct(line.getM_Product().getM_Product_Category_ID(), as.getC_AcctSchema_ID(), false);
		if(contractProductAcct != null && contractProductAcct.getJP_TradeDiscountGrant_Acct() > 0)
		{
			return MAccount.get(getCtx(),contractProductAcct.getJP_TradeDiscountGrant_Acct());
		}else{
			return docLine.getAccount(ProductCost.ACCTTYPE_P_TDiscountGrant, as);
		}
	}


	private MAccount getInvoiceTDiscountGrantAccount(DocLine docLine, MContractAcct contractAcct, MAcctSchema as)
	{
		MRecognitionLine line = (MRecognitionLine)docLine.getPO();

		MContractProductAcct contractProductAcct = contractAcct.getContractProductAcct(line.getM_Product().getM_Product_Category_ID(), as.getC_AcctSchema_ID(), false);
		if(contractProductAcct != null && contractProductAcct.getP_TradeDiscountGrant_Acct() > 0)
		{
			return MAccount.get(getCtx(),contractProductAcct.getP_TradeDiscountGrant_Acct());
		}else{
			return docLine.getAccount(ProductCost.ACCTTYPE_P_TDiscountGrant, as);
		}
	}

	private MAccount getRecognitionTDiscountRecAccount(DocLine docLine, MContractAcct contractAcct, MAcctSchema as)
	{
		MRecognitionLine line = (MRecognitionLine)docLine.getPO();

		MContractProductAcct contractProductAcct = contractAcct.getContractProductAcct(line.getM_Product().getM_Product_Category_ID(), as.getC_AcctSchema_ID(), false);
		if(contractProductAcct != null && contractProductAcct.getJP_TradeDiscountRec_Acct() > 0)
		{
			return MAccount.get(getCtx(),contractProductAcct.getJP_TradeDiscountRec_Acct());

		}else{

			String JP_Recognition_JournalPolicy =contractAcct.getJP_Recognition_JournalPolicy();
			if(Util.isEmpty(JP_Recognition_JournalPolicy))
			{
				JP_Recognition_JournalPolicy = MContractAcct.JP_RECOGNITION_JOURNALPOLICY_IfNoConfigTheJournalWillNotBePosted;
			}

			if(JP_Recognition_JournalPolicy.equals(MContractAcct.JP_RECOGNITION_JOURNALPOLICY_IfNoConfigTheJournalWillNotBePosted))//NN
			{
				return null;

			}else if(JP_Recognition_JournalPolicy.equals(MContractAcct.JP_RECOGNITION_JOURNALPOLICY_IfNoConfigWillBePostedByDefaultButTaxBeExcluded)) {//DN

				return docLine.getAccount(ProductCost.ACCTTYPE_P_TDiscountRec, as);

			}else if(JP_Recognition_JournalPolicy.equals(MContractAcct.JP_RECOGNITION_JOURNALPOLICY_IfNoConfigWillBePostedByDefaultAccount)) {//DD

				return docLine.getAccount(ProductCost.ACCTTYPE_P_TDiscountRec, as);
			}

			return docLine.getAccount(ProductCost.ACCTTYPE_P_TDiscountRec, as);
		}
	}

	private MAccount getInvoiceTDiscountRecAccount(DocLine docLine, MContractAcct contractAcct, MAcctSchema as)
	{
		MRecognitionLine line = (MRecognitionLine)docLine.getPO();

		MContractProductAcct contractProductAcct = contractAcct.getContractProductAcct(line.getM_Product().getM_Product_Category_ID(), as.getC_AcctSchema_ID(), false);
		if(contractProductAcct != null && contractProductAcct.getP_TradeDiscountRec_Acct() > 0)
		{
			return MAccount.get(getCtx(),contractProductAcct.getP_TradeDiscountRec_Acct());
		}else{
			return docLine.getAccount(ProductCost.ACCTTYPE_P_TDiscountRec, as);
		}
	}

	private MAccount getCOGSAccount(DocLine docLine, MContractAcct contractAcct, MAcctSchema as)
	{
		MRecognitionLine line = (MRecognitionLine)docLine.getPO();
		//Charge Account
		if (line.getM_Product_ID() == 0 && line.getC_Charge_ID() != 0)
		{
			MContractChargeAcct contractChargeAcct =  contractAcct.getContracChargeAcct(line.getC_Charge_ID(), as.getC_AcctSchema_ID(), false);
			if(contractChargeAcct != null && contractChargeAcct.getCh_Expense_Acct() > 0)
			{
				return MAccount.get(getCtx(), contractChargeAcct.getCh_Expense_Acct());
			}else{
				return docLine.getAccount(ProductCost.ACCTTYPE_P_Cogs, as) ;
			}

		}else if(line.getM_Product_ID() > 0){
			MContractProductAcct contractProductAcct = contractAcct.getContractProductAcct(line.getM_Product().getM_Product_Category_ID(), as.getC_AcctSchema_ID(), false);
			if(contractProductAcct != null && contractProductAcct.getP_COGS_Acct() > 0)
			{
				return MAccount.get(getCtx(),contractProductAcct.getP_COGS_Acct());
			}else{
				return docLine.getAccount(ProductCost.ACCTTYPE_P_Cogs, as) ;
			}
		}else{
			return docLine.getAccount(ProductCost.ACCTTYPE_P_Cogs, as) ;
		}
	}


	private MAccount getAssetAccount(DocLine docLine, MContractAcct contractAcct,  MAcctSchema as)
	{
		return docLine.getAccount(ProductCost.ACCTTYPE_P_Asset, as);
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

	private MAccount getRecognitionTaxDueAccount(DocTax doc_Tax, MContractAcct contractAcct,  MAcctSchema as)
	{
		MContractTaxAcct taxAcct = contractAcct.getContracTaxAcct(doc_Tax.getC_Tax_ID(), as.getC_AcctSchema_ID(),false);
		if(taxAcct != null && taxAcct.getJP_TaxDue_Acct() > 0)
		{
			return MAccount.get(getCtx(), taxAcct.getJP_TaxDue_Acct());

		}else{

			String JP_Recognition_JournalPolicy =contractAcct.getJP_Recognition_JournalPolicy();
			if(Util.isEmpty(JP_Recognition_JournalPolicy))
			{
				JP_Recognition_JournalPolicy = MContractAcct.JP_RECOGNITION_JOURNALPOLICY_IfNoConfigTheJournalWillNotBePosted;
			}

			if(JP_Recognition_JournalPolicy.equals(MContractAcct.JP_RECOGNITION_JOURNALPOLICY_IfNoConfigTheJournalWillNotBePosted))//NN
			{
				return null;

			}else if(JP_Recognition_JournalPolicy.equals(MContractAcct.JP_RECOGNITION_JOURNALPOLICY_IfNoConfigWillBePostedByDefaultButTaxBeExcluded)) {//DN

				return null;

			}else if(JP_Recognition_JournalPolicy.equals(MContractAcct.JP_RECOGNITION_JOURNALPOLICY_IfNoConfigWillBePostedByDefaultAccount)) {//DD

				return doc_Tax.getAccount(DocTax.ACCTTYPE_TaxDue,as);
			}

			return null;
		}
	}

	private MAccount getRecognitionTaxCreditAccount(DocTax doc_Tax, MContractAcct contractAcct,  MAcctSchema as)
	{
		MContractTaxAcct taxAcct = contractAcct.getContracTaxAcct(doc_Tax.getC_Tax_ID(), as.getC_AcctSchema_ID(),false);
		MTax tax = MTax.get(getCtx(), doc_Tax.getC_Tax_ID());
		if(tax.isSalesTax())
		{
			if(taxAcct != null && taxAcct.getJP_TaxExpense_Acct() > 0)
			{
				return MAccount.get(getCtx(), taxAcct.getJP_TaxExpense_Acct());

			}else{

				String JP_Recognition_JournalPolicy = contractAcct.getJP_Recognition_JournalPolicy();
				if(Util.isEmpty(JP_Recognition_JournalPolicy))
				{
					JP_Recognition_JournalPolicy = MContractAcct.JP_RECOGNITION_JOURNALPOLICY_IfNoConfigTheJournalWillNotBePosted;
				}

				if(JP_Recognition_JournalPolicy.equals(MContractAcct.JP_RECOGNITION_JOURNALPOLICY_IfNoConfigTheJournalWillNotBePosted))//NN
				{
					return null;

				}else if(JP_Recognition_JournalPolicy.equals(MContractAcct.JP_RECOGNITION_JOURNALPOLICY_IfNoConfigWillBePostedByDefaultButTaxBeExcluded)) {//DN

					return null;

				}else if(JP_Recognition_JournalPolicy.equals(MContractAcct.JP_RECOGNITION_JOURNALPOLICY_IfNoConfigWillBePostedByDefaultAccount)) {//DD

					return doc_Tax.getAccount(DocTax.ACCTTYPE_TaxExpense,as);
				}

				return null;
			}

		}else{

			if(taxAcct != null && taxAcct.getJP_TaxCredit_Acct() > 0)
			{
				return MAccount.get(getCtx(), taxAcct.getJP_TaxCredit_Acct());

			}else{

				String JP_Recognition_JournalPolicy = contractAcct.getJP_Recognition_JournalPolicy();
				if(Util.isEmpty(JP_Recognition_JournalPolicy))
				{
					JP_Recognition_JournalPolicy = MContractAcct.JP_RECOGNITION_JOURNALPOLICY_IfNoConfigTheJournalWillNotBePosted;
				}

				if(JP_Recognition_JournalPolicy.equals(MContractAcct.JP_RECOGNITION_JOURNALPOLICY_IfNoConfigTheJournalWillNotBePosted))//NN
				{
					return null;

				}else if(JP_Recognition_JournalPolicy.equals(MContractAcct.JP_RECOGNITION_JOURNALPOLICY_IfNoConfigWillBePostedByDefaultButTaxBeExcluded)) {//DN

					return null;

				}else if(JP_Recognition_JournalPolicy.equals(MContractAcct.JP_RECOGNITION_JOURNALPOLICY_IfNoConfigWillBePostedByDefaultAccount)) {//DD

					return doc_Tax.getAccount(DocTax.ACCTTYPE_TaxCredit,as);
				}

				return null;

			}
		}
	}


	private boolean isReversal(DocLine line) {
		return m_Reversal_ID !=0 && line.getReversalLine_ID() != 0;
	}
} //
