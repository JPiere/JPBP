/******************************************************************************
 * Product: JPiere                                                            *
 * Copyright (C) Hideaki Hagiwara (h.hagiwara@oss-erp.co.jp)                  *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
package jpiere.base.plugin.org.compiere.acct;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;

import org.compiere.acct.Doc;
import org.compiere.acct.DocLine;
import org.compiere.acct.DocLine_Allocation;
import org.compiere.acct.DocManager;
import org.compiere.acct.Fact;
import org.compiere.acct.FactLine;
import org.compiere.model.I_C_AllocationLine;
import org.compiere.model.MAccount;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MAcctSchemaElement;
import org.compiere.model.MAllocationHdr;
import org.compiere.model.MAllocationLine;
import org.compiere.model.MCashLine;
import org.compiere.model.MCharge;
import org.compiere.model.MConversionRate;
import org.compiere.model.MDocType;
import org.compiere.model.MFactAcct;
import org.compiere.model.MInvoice;
//import org.compiere.model.MInvoiceLine; //JPIERE Comment out
import org.compiere.model.MOrder;
import org.compiere.model.MPayment;
import org.compiere.model.MRMA;
import org.compiere.model.MSysConfig;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.MContractAcct;
import jpiere.base.plugin.org.adempiere.model.MContractBPAcct;
import jpiere.base.plugin.org.adempiere.model.MContractContent;

/**
 *  Post Allocation Documents.
 *  <pre>
 *  Table:              C_AllocationHdr
 *  Document Types:     CMA
 *  </pre>
 *  @author Jorg Janke
 *  @version  $Id: Doc_Allocation.java,v 1.6 2006/07/30 00:53:33 jjanke Exp $
 *  <p>
 *  FR [ 1840016 ] Avoid usage of clearing accounts - subject to C_AcctSchema.IsPostIfClearingEqual<br/>
 *  Avoid posting if Receipt and both accounts Unallocated Cash and Receivable are equal<br/>
 *  Avoid posting if Payment and both accounts Payment Select and Liability are equal<br/>
 *
 *  @author phib
 *  BF [ 2019262 ] Allocation posting currency gain/loss omits line reference
 *
 *  JPIERE-0052 - JPiere original Doc_AcclocationHdr
 *  
 *  ref: JPIERE-0575: Reverse Accrual in case of Foreign currency is applied original Invoice rate.
 *  ref: JPIERE-0574: Reverse Accrual in case of Foreign currency is applied original payment rate.
 *  ref: JPIERE-0566: Compliant journals in Tax law.
 *  ref: JPIERE-0556: Compliant journals. 
 *  ref: JPIERE-0363: Allocation of Contract Management.
 *  ref: JPIERE-0026: Allocation between different Business Partners.
 *
 */
public class Doc_AllocationHdrJP extends Doc
{
	/**
	 *  Constructor
	 * 	@param as accounting schema
	 * 	@param rs record
	 * 	@param trxName trx
	 */
	public Doc_AllocationHdrJP (MAcctSchema as, ResultSet rs, String trxName)
	{
		super (as, MAllocationHdr.class, rs, DOCTYPE_Allocation, trxName);
	}   //  Doc_Allocation

	/**	Tolerance G&L				*/
	private static final BigDecimal	TOLERANCE = BigDecimal.valueOf(0.02);
	/** Facts						*/
	private ArrayList<Fact>		m_facts = null;
	
	private ArrayList<FactLine>		invGainLossFactLines = null;
	private ArrayList<FactLine>		payGainLossFactLines = null;

	/**
	 *  Load Specific Document Details
	 *  @return error message or null
	 */
	protected String loadDocumentDetails ()
	{
		MAllocationHdr alloc = (MAllocationHdr)getPO();
		setDateDoc(alloc.getDateTrx());
		//	Contained Objects
		p_lines = loadLines(alloc);
		return null;
	}   //  loadDocumentDetails

	/**
	 *	Load Invoice Line
	 *	@param alloc header
	 *  @return DocLine Array
	 */
	private DocLine[] loadLines(MAllocationHdr alloc)
	{
		ArrayList<DocLine> list = new ArrayList<DocLine>();
		MAllocationLine[] lines = alloc.getLines(false);
		for (int i = 0; i < lines.length; i++)
		{
			MAllocationLine line = lines[i];
			DocLine_AllocationJP docLine = new DocLine_AllocationJP(line, this);//JPIERE-0052

			//	Get Payment Conversion Rate
			if (line.getC_Payment_ID() != 0)
			{
				MPayment payment = new MPayment (getCtx(), line.getC_Payment_ID(), getTrxName());
				setPaymentCurrencyRate(docLine, payment);//JPIERE-0052 -set Payment rate basically
			}
			else if (line.getC_Invoice_ID() != 0)
			{
				//Ref: IDEMPIERE-5591 Payment allocation AP/AR GL postings not zero in alternative schema for invoice REVERSE/CORRECT (#1921)
//				MInvoice invoice = new MInvoice (getCtx(), line.getC_Invoice_ID(), getTrxName());
//				int C_ConversionType_ID = invoice.getC_ConversionType_ID();
//				docLine.setC_ConversionType_ID(C_ConversionType_ID);
//				if (invoice.isOverrideCurrencyRate())
//					docLine.setCurrencyRate(invoice.getCurrencyRate());
			}
			//
			if (log.isLoggable(Level.FINE)) log.fine(docLine.toString());
			list.add (docLine);
		}

		//	Return Array
		DocLine[] dls = new DocLine[list.size()];
		list.toArray(dls);
		return dls;
	}	//	loadLines


	/**
	 *  Get Source Currency Balance - subtracts line and tax amounts from total - no rounding
	 *  @return positive amount, if total invoice is bigger than lines
	 */
	public BigDecimal getBalance()
	{
		BigDecimal retValue = Env.ZERO;
		return retValue;
	}   //  getBalance

	/**
	 *  Create Facts (the accounting logic) for
	 *  CMA.
	 *  <pre>
	 *  AR_Invoice_Payment
	 *      UnAllocatedCash DR
	 *      or C_Prepayment
	 *      DiscountExp     DR
	 *      WriteOff        DR
	 *      Receivables             CR
	 *  AR_Invoice_Cash
	 *      CashTransfer    DR
	 *      DiscountExp     DR
	 *      WriteOff        DR
	 *      Receivables             CR
	 *
	 *  AP_Invoice_Payment
	 *      Liability       DR
	 *      DiscountRev             CR
	 *      WriteOff                CR
	 *      PaymentSelect           CR
	 *      or V_Prepayment
	 *  AP_Invoice_Cash
	 *      Liability       DR
	 *      DiscountRev             CR
	 *      WriteOff                CR
	 *      CashTransfer            CR
	 *  CashBankTransfer
	 *      -
	 *  ==============================
	 *  Realized Gain and Loss
	 * 		AR/AP           DR      CR
	 * 		Realized G/L    DR      CR
	 *
	 *
	 *  </pre>
	 *  Tax needs to be corrected for discount and write-off;
	 *  Currency gain and loss is realized here.
	 *  @param as accounting schema
	 *  @return Fact
	 */
	public ArrayList<Fact> createFacts (MAcctSchema as)
	{
		m_facts = new ArrayList<Fact>();
		invGainLossFactLines = new ArrayList<FactLine>();
		payGainLossFactLines = new ArrayList<FactLine>();

		//  create Fact Header
		Fact fact = new Fact(this, as, Fact.POST_Actual);
		Fact factForRGL = new Fact(this, as, Fact.POST_Actual); // dummy fact (not posted) to calculate Realized Gain & Loss
		boolean isInterOrg = isInterOrg(as);
		MAccount bpAcct = null;		//	Liability/Receivables

		for (int i = 0; i < p_lines.length; i++)
		{
			DocLine_AllocationJP line = (DocLine_AllocationJP)p_lines[i];//JPIERE-0052
			setC_BPartner_ID(line.getC_BPartner_ID());

			//  CashBankTransfer - all references null and Discount/WriteOff = 0
			if (line.getC_Payment_ID() != 0
				&& line.getC_Invoice_ID() == 0 && line.getC_Order_ID() == 0
				&& line.getC_CashLine_ID() == 0 && line.getC_BPartner_ID() == 0
				&& Env.ZERO.compareTo(line.getDiscountAmt()) == 0
				&& Env.ZERO.compareTo(line.getWriteOffAmt()) == 0)
				continue;

			//	Receivables/Liability Amt
			BigDecimal allocationSource = line.getAmtSource()
				.add(line.getDiscountAmt())
				.add(line.getWriteOffAmt());
			BigDecimal allocationSourceForRGL = allocationSource; // for realized gain & loss purposes
			BigDecimal allocationAccounted = Env.ZERO;	// AR/AP balance corrected
			@SuppressWarnings("unused")
			BigDecimal allocationAccountedForRGL = Env.ZERO; // for realized gain & loss purposes

			FactLine fl = null;
			FactLine flForRGL = null;
			//
			MPayment payment = null;
			if (line.getC_Payment_ID() != 0)
				payment = new MPayment (getCtx(), line.getC_Payment_ID(), getTrxName());
			MInvoice invoice = null;
			if (line.getC_Invoice_ID() != 0)
				invoice = new MInvoice (getCtx(), line.getC_Invoice_ID(), getTrxName());

			//JPIERE-0363 - Get Contract Info
			MContractAcct contractAcct = null;
			if (invoice != null)
			{
				int JP_ContractContent_ID = invoice.get_ValueAsInt("JP_ContractContent_ID");
				if(JP_ContractContent_ID > 0)
				{
					MContractContent content = MContractContent.get(getCtx(), JP_ContractContent_ID);
					if(content.getJP_Contract_Acct_ID() > 0)
						contractAcct = MContractAcct.get(getCtx(), content.getJP_Contract_Acct_ID());
				}
			}//JPIERE-0363

			BigDecimal allocPayAccounted = Env.ZERO;
			BigDecimal allocPaySource = Env.ZERO;

			//	No Invoice
			if (invoice == null)
			{
					//	adaxa-pb: allocate to charges
			    	// Charge Only 
				if (line.getC_Invoice_ID() == 0 && line.getC_Payment_ID() == 0 && line.getC_Charge_ID() != 0 )
				{
					fl = fact.createLine (line, line.getChargeAccount(as, line.getAmtSource()),
						getC_Currency_ID(), line.getAmtSource());
					fl.set_ValueNoCheck("JP_Charge_ID", line.getC_Charge_ID());//JPIERE-0556
				}
				//	Payment Only
				else if (line.getC_Invoice_ID() == 0 && line.getC_Payment_ID() != 0)
				{
					fl = fact.createLine (line, getPaymentAcct(as, line.getC_Payment_ID()),
						getC_Currency_ID(), line.getAmtSource(), null);
					if (fl != null && payment != null) {
						setPaymentInfo(fl, payment, true);//JPIERE-0052
						allocPayAccounted = allocPayAccounted.add(fl.getAcctBalance());
					}
				}
				else
				{
					p_Error = "Cannot determine SO/PO";
					log.log(Level.SEVERE, p_Error);
					return null;
				}
			}
			//	Sales Invoice
			else if (invoice.isSOTrx())
			{

				// Avoid usage of clearing accounts
				// If both accounts Unallocated Cash and Receivable are equal
				// then don't post

				MAccount acct_unallocated_cash = null;
				if (line.getC_Payment_ID() != 0)
					acct_unallocated_cash =  getPaymentAcct(as, line.getC_Payment_ID());
				else if (line.getC_CashLine_ID() != 0)
					acct_unallocated_cash =  getCashAcct(as, line.getC_CashLine_ID());
				
				setC_BPartner_ID(invoice.getC_BPartner_ID());//JPIERE-0026,0052
				MAccount acct_receivable = getReceivableAccount(contractAcct, as);//JPIERE-0363
				setC_BPartner_ID(line.getC_BPartner_ID());//JPIERE-0026,0052

				if ((!as.isPostIfClearingEqual()) && acct_unallocated_cash != null && acct_unallocated_cash.equals(acct_receivable) && (!isInterOrg)) {

					// if not using clearing accounts, then don't post amtsource
					// change the allocationsource to be writeoff + discount
					allocationSource = line.getDiscountAmt().add(line.getWriteOffAmt());


				} else {

					// Normal behavior -- unchanged if using clearing accounts

					//	Payment/Cash	DR
					if (line.getC_Payment_ID() != 0)
					{
						fl = fact.createLine (line, getPaymentAcct(as, line.getC_Payment_ID()),
							getC_Currency_ID(), line.getAmtSource(), null);
						if (fl != null && payment != null) {
							setPaymentInfo(fl, payment, true);//JPIERE-0052
							if (payment.getReversal_ID() > 0 )
								allocPayAccounted = allocPayAccounted.add(fl.getAcctBalance().negate());
							else
								allocPayAccounted = allocPayAccounted.add(fl.getAcctBalance());
						}
					}
					else if (line.getC_CashLine_ID() != 0)
					{
						fl = fact.createLine (line, getCashAcct(as, line.getC_CashLine_ID()),
							getC_Currency_ID(), line.getAmtSource(), null);
						MCashLine cashLine = new MCashLine (getCtx(), line.getC_CashLine_ID(), getTrxName());
						if (fl != null && cashLine.get_ID() != 0)
							fl.setAD_Org_ID(cashLine.getAD_Org_ID());
					}

				}
				// End Avoid usage of clearing accounts

				//	Discount		DR
				if (Env.ZERO.compareTo(line.getDiscountAmt()) != 0)
				{
					fl = fact.createLine (line, getAccount(Doc.ACCTTYPE_DiscountExp, as),
						getC_Currency_ID(), line.getDiscountAmt(), null);
					if (fl != null && payment != null)
					{
						setInvoiceInfo(fl, invoice, true); //JPIERE-0052
						setPaymentInfo(fl, payment, false);//JPIERE-0052
					}
				}
				//	Write off		DR
				if (Env.ZERO.compareTo(line.getWriteOffAmt()) != 0)
				{
					fl = fact.createLine (line, getAccount(Doc.ACCTTYPE_WriteOff, as),
						getC_Currency_ID(), line.getWriteOffAmt(), null);
					if (fl != null && payment != null)
					{
						setInvoiceInfo(fl, invoice, true); //JPIERE-0052
						setPaymentInfo(fl, payment, false);//JPIERE-0052
					}
				}

				//	AR Invoice Amount	CR
				if (as.isAccrual())
				{
					setC_BPartner_ID(invoice.getC_BPartner_ID());//JPIERE-0026,0052
					bpAcct = getReceivableAccount(contractAcct, as); //JPIERE-0363
					setInvoiceCurrencyRate(line, invoice, payment);//JPIERE-0052
					fl = fact.createLine (line, bpAcct,
						getC_Currency_ID(), null, allocationSource);		//	payment currency
					if (fl != null)
						allocationAccounted = fl.getAcctBalance().negate();
					if (fl != null && invoice != null)
						setInvoiceInfo(fl, invoice, true); //JPIERE-0052
					
					// for Realized Gain & Loss
					flForRGL = factForRGL.createLine (line, bpAcct,
						getC_Currency_ID(), null, allocationSourceForRGL);		//	payment currency
					if (flForRGL != null)
						allocationAccountedForRGL = flForRGL.getAcctBalance().negate();
				}
				else	//	Cash Based
				{
					allocationAccounted = createCashBasedAcct (as, fact,
						invoice, allocationSource);
					allocationAccountedForRGL = allocationAccounted;
				}
			}
			//	Purchase Invoice
			else
			{
				// Avoid usage of clearing accounts
				// If both accounts Payment Select and Liability are equal
				// then don't post

				MAccount acct_payment_select = null;
				if (line.getC_Payment_ID() != 0)
					acct_payment_select = getPaymentAcct(as, line.getC_Payment_ID());
				else if (line.getC_CashLine_ID() != 0)
					acct_payment_select = getCashAcct(as, line.getC_CashLine_ID());
				
				setC_BPartner_ID(invoice.getC_BPartner_ID());//JPIERE-0026,0052
				MAccount acct_liability = getPayableAccount(contractAcct, as);//JPIERE-0363
				boolean isUsingClearing = true;

				// Save original allocation source for realized gain & loss purposes
				allocationSourceForRGL = allocationSourceForRGL.negate();

				if ((!as.isPostIfClearingEqual()) && acct_payment_select != null && acct_payment_select.equals(acct_liability) && (!isInterOrg)) {

					// if not using clearing accounts, then don't post amtsource
					// change the allocationsource to be writeoff + discount
					allocationSource = line.getDiscountAmt().add(line.getWriteOffAmt());
					isUsingClearing = false;

				}
				// End Avoid usage of clearing accounts

				allocationSource = allocationSource.negate();	//	allocation is negative
				//	AP Invoice Amount	DR
				if (as.isAccrual())
				{
					bpAcct = getPayableAccount(contractAcct, as);//JPIERE-0363
					setInvoiceCurrencyRate(line, invoice, payment);//JPIERE-0052
					fl = fact.createLine (line, bpAcct,
						getC_Currency_ID(), allocationSource, null);		//	payment currency
					if (fl != null)
						allocationAccounted = fl.getAcctBalance();
					if (fl != null && invoice != null)
						setInvoiceInfo(fl, invoice, true); //JPIERE-0052

					// for Realized Gain & Loss
					flForRGL = factForRGL.createLine (line, bpAcct,
						getC_Currency_ID(), allocationSourceForRGL, null);		//	payment currency
					if (flForRGL != null)
						allocationAccountedForRGL = flForRGL.getAcctBalance();
				}
				else	//	Cash Based
				{
					allocationAccounted = createCashBasedAcct (as, fact,
						invoice, allocationSource);
					allocationAccountedForRGL = allocationAccounted;
				}

				setC_BPartner_ID(line.getC_BPartner_ID());//JPIERE-0026,0052
				setPaymentCurrencyRate(line, payment);//JPIERE-0052
				
				//	Discount		CR
				if (Env.ZERO.compareTo(line.getDiscountAmt()) != 0)
				{
					fl = fact.createLine (line, getAccount(Doc.ACCTTYPE_DiscountRev, as),
						getC_Currency_ID(), null, line.getDiscountAmt().negate());
					if (fl != null && payment != null)
					{
						setInvoiceInfo(fl, invoice, true); //JPIERE-0052
						setPaymentInfo(fl, payment, false);//JPIERE-0052
					}
				}
				//	Write off		CR
				if (Env.ZERO.compareTo(line.getWriteOffAmt()) != 0)
				{
					fl = fact.createLine (line, getAccount(Doc.ACCTTYPE_WriteOff, as),
						getC_Currency_ID(), null, line.getWriteOffAmt().negate());
					if (fl != null && payment != null)
					{
						setInvoiceInfo(fl, invoice, true); //JPIERE-0052
						setPaymentInfo(fl, payment, false);//JPIERE-0052
					}
				}
				//	Payment/Cash	CR
				if (isUsingClearing && line.getC_Payment_ID() != 0) // Avoid usage of clearing accounts
				{
					fl = fact.createLine (line, getPaymentAcct(as, line.getC_Payment_ID()),
						getC_Currency_ID(), null, line.getAmtSource().negate());
					if (fl != null && payment != null)
						setPaymentInfo(fl, payment, true);//JPIERE-0052
					if (fl != null)
						allocPayAccounted = allocPayAccounted.add(fl.getAcctBalance().negate());
				}
				else if (isUsingClearing && line.getC_CashLine_ID() != 0) // Avoid usage of clearing accounts
				{
					fl = fact.createLine (line, getCashAcct(as, line.getC_CashLine_ID()),
						getC_Currency_ID(), null, line.getAmtSource().negate());
					MCashLine cashLine = new MCashLine (getCtx(), line.getC_CashLine_ID(), getTrxName());
					if (fl != null && cashLine.get_ID() != 0)
						fl.setAD_Org_ID(cashLine.getAD_Org_ID());
				}
			}
			
			setPaymentCurrencyRate(line, payment);//JPIERE-0052

			//	VAT Tax Correction
			if (invoice != null && as.isTaxCorrection())
			{
				BigDecimal taxCorrectionAmt = Env.ZERO;
				if (as.isTaxCorrectionDiscount())
					taxCorrectionAmt = line.getDiscountAmt();
				if (as.isTaxCorrectionWriteOff())
					taxCorrectionAmt = taxCorrectionAmt.add(line.getWriteOffAmt());
				//
				if (taxCorrectionAmt.signum() != 0)
				{
					if (!createTaxCorrection(as, fact, line,
						getAccount(invoice.isSOTrx() ? Doc.ACCTTYPE_DiscountExp : Doc.ACCTTYPE_DiscountRev, as),
						getAccount(Doc.ACCTTYPE_WriteOff, as), invoice.isSOTrx()))
					{
						p_Error = "Cannot create Tax correction";
						return null;
					}
				}
			}

			//	Realized Gain & Loss
			if (invoice != null && as.isAccrual()
				&& (getC_Currency_ID() != as.getC_Currency_ID()			//	payment allocation in foreign currency
					|| getC_Currency_ID() != line.getInvoiceC_Currency_ID()))	//	allocation <> invoice currency
			{
				p_Error = createInvoiceGainLoss (line, as, fact, bpAcct, invoice,
					allocationSource, allocationAccounted);
				if (p_Error != null)
					return null;
			}
			
			allocPaySource = allocPaySource.add(line.getAmtSource());
			if (payment != null && getC_Currency_ID() != as.getC_Currency_ID())
			{
				p_Error = createPaymentGainLoss (line, as, fact,  getPaymentAcct(as, payment.get_ID()), payment,
						allocPaySource, allocPayAccounted);
				if (p_Error != null)
					return null;				
			}			
		}	//	for all lines

		//JPIERE-0052: commented out as it is not needed.
		// In the end, balanceAccounting() method adjust balance.
		//	rounding adjustment
//		if (getC_Currency_ID() != as.getC_Currency_ID())
//		{
//			p_Error = createInvoiceRoundingCorrection (as, fact,  bpAcct);
//			if (p_Error != null)
//				return null;
//			p_Error = createPaymentRoundingCorrection (as, fact);
//			if (p_Error != null)
//				return null;
//		}//JPIERE-0052

		// FR [ 1840016 ] Avoid usage of clearing accounts - subject to C_AcctSchema.IsPostIfClearingEqual
		if ( (!as.isPostIfClearingEqual()) && p_lines.length > 0 && (!isInterOrg)) {
			boolean allEquals = true;
			// more than one line (i.e. crossing one payment+ with a payment-, or an invoice against a credit memo)
			// verify if the sum of all facts is zero net
			FactLine[] factlines = fact.getLines();
			BigDecimal netBalance = Env.ZERO;
			FactLine prevFactLine = null;
			for (FactLine factLine : factlines) {
				netBalance = netBalance.add(factLine.getAmtSourceDr()).subtract(factLine.getAmtSourceCr());
				if (prevFactLine != null) {
					if (! equalFactLineIDs(prevFactLine, factLine)) {
						allEquals = false;
						break;
					}
				}
				prevFactLine = factLine;
			}
			if (netBalance.compareTo(Env.ZERO) == 0 && allEquals) {
				// delete the postings
				for (FactLine factline : factlines)
					fact.remove(factline);
			}
		}
		
		if (getC_Currency_ID() != as.getC_Currency_ID())
			balanceAccounting(as, fact);

		//	reset line info
		setC_BPartner_ID(0);
		//
		m_facts.add(fact);
		return m_facts;
	}   //  createFact

	/** 
	 * Verify if the posting involves two or more organizations
	 * @return true if there are more than one org involved on the posting
	 */
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

		int startorg = p_lines[0].getAD_Org_ID();
		// validate if the allocation involves more than one org
		for (int i = 0; i < p_lines.length; i++) {
			DocLine_Allocation line = (DocLine_Allocation)p_lines[i];
			int orgpayment = startorg;
			MPayment payment = null;
			if (line.getC_Payment_ID() != 0) {
				payment = new MPayment (getCtx(), line.getC_Payment_ID(), getTrxName());
				orgpayment = payment.getAD_Org_ID();
			}
			int orginvoice = startorg;
			MInvoice invoice = null;
			if (line.getC_Invoice_ID() != 0) {
				invoice = new MInvoice (getCtx(), line.getC_Invoice_ID(), getTrxName());
				orginvoice = invoice.getAD_Org_ID();
			}
			int orgcashline = startorg;
			MCashLine cashline = null;
			if (line.getC_CashLine_ID() != 0) {
				cashline = new MCashLine (getCtx(), line.getC_CashLine_ID(), getTrxName());
				orgcashline = cashline.getAD_Org_ID();
			}
			int orgorder = startorg;
			MOrder order = null;
			if (line.getC_Order_ID() != 0) {
				order = new MOrder (getCtx(), line.getC_Order_ID(), getTrxName());
				orgorder = order.getAD_Org_ID();
			}

			if (   line.getAD_Org_ID() != startorg
				|| orgpayment != startorg
				|| orginvoice != startorg
				|| orgcashline != startorg
				|| orgorder != startorg)
				return true;
		}

		return false;
	}

	/**
	 * Compare the dimension ID's from two factlines
	 * @param allEquals
	 * @param prevFactLine
	 * @param factLine
	 * @return boolean indicating if both dimension ID's are equal
	 */
	private boolean equalFactLineIDs(FactLine prevFactLine, FactLine factLine) {
		return (factLine.getA_Asset_ID() == prevFactLine.getA_Asset_ID()
				&& factLine.getAccount_ID() == prevFactLine.getAccount_ID()
				&& factLine.getAD_Client_ID() == prevFactLine.getAD_Client_ID()
				&& factLine.getAD_Org_ID() == prevFactLine.getAD_Org_ID()
				&& factLine.getAD_OrgTrx_ID() == prevFactLine.getAD_OrgTrx_ID()
				&& factLine.getC_AcctSchema_ID() == prevFactLine.getC_AcctSchema_ID()
				&& factLine.getC_Activity_ID() == prevFactLine.getC_Activity_ID()
				&& factLine.getC_BPartner_ID() == prevFactLine.getC_BPartner_ID()
				&& factLine.getC_Campaign_ID() == prevFactLine.getC_Campaign_ID()
				&& factLine.getC_Currency_ID() == prevFactLine.getC_Currency_ID()
				&& factLine.getC_LocFrom_ID() == prevFactLine.getC_LocFrom_ID()
				&& factLine.getC_LocTo_ID() == prevFactLine.getC_LocTo_ID()
				&& factLine.getC_Period_ID() == prevFactLine.getC_Period_ID()
				&& factLine.getC_Project_ID() == prevFactLine.getC_Project_ID()
				&& factLine.getC_ProjectPhase_ID() == prevFactLine.getC_ProjectPhase_ID()
				&& factLine.getC_ProjectTask_ID() == prevFactLine.getC_ProjectTask_ID()
				&& factLine.getC_SalesRegion_ID() == prevFactLine.getC_SalesRegion_ID()
				&& factLine.getC_SubAcct_ID() == prevFactLine.getC_SubAcct_ID()
				&& factLine.getC_Tax_ID() == prevFactLine.getC_Tax_ID()
				&& factLine.getC_UOM_ID() == prevFactLine.getC_UOM_ID()
				&& factLine.getGL_Budget_ID() == prevFactLine.getGL_Budget_ID()
				&& factLine.getGL_Category_ID() == prevFactLine.getGL_Category_ID()
				&& factLine.getM_Locator_ID() == prevFactLine.getM_Locator_ID()
				&& factLine.getM_Product_ID() == prevFactLine.getM_Product_ID()
				&& factLine.getUserElement1_ID() == prevFactLine.getUserElement1_ID()
				&& factLine.getUserElement2_ID() == prevFactLine.getUserElement2_ID()
				&& factLine.getUser1_ID() == prevFactLine.getUser1_ID()
				&& factLine.getUser2_ID() == prevFactLine.getUser2_ID());
	}

	/**
	 * 	Create Cash Based Acct
	 * 	@param as accounting schema
	 *	@param fact fact
	 *	@param invoice invoice
	 *	@param allocationSource allocation amount (incl discount, writeoff)
	 *	@return Accounted Amt
	 */
	private BigDecimal createCashBasedAcct (MAcctSchema as, Fact fact, MInvoice invoice,
		BigDecimal allocationSource)
	{
		BigDecimal allocationAccounted = Env.ZERO;
		//	Multiplier
		double percent = invoice.getGrandTotal().doubleValue() / allocationSource.doubleValue();
		if (percent > 0.99 && percent < 1.01)
			percent = 1.0;
		if (log.isLoggable(Level.CONFIG)) log.config("Multiplier=" + percent + " - GrandTotal=" + invoice.getGrandTotal()
			+ " - Allocation Source=" + allocationSource);

		//	Get Invoice Postings
		Doc_InvoiceJP docInvoice = (Doc_InvoiceJP)Doc.get(as,			//JPIERE
			MInvoice.Table_ID, invoice.getC_Invoice_ID(), getTrxName());
		docInvoice.loadDocumentDetails();
		allocationAccounted = docInvoice.createFactCash(as, fact, BigDecimal.valueOf(percent));
		if (log.isLoggable(Level.CONFIG)) log.config("Allocation Accounted=" + allocationAccounted);

		//	Cash Based Commitment Release
//		if (as.isCreatePOCommitment() && !invoice.isSOTrx())
//		{
//			MInvoiceLine[] lines = invoice.getLines();
//			for (int i = 0; i < lines.length; i++)
//			{
//				Fact factC = Doc_Order.getCommitmentRelease(as, this,
//					lines[i].getQtyInvoiced(), lines[i].getC_InvoiceLine_ID(), BigDecimal.valueOf(percent));
//				if (factC == null)
//					return null;
//				m_facts.add(factC);
//			}
//		}	//	Commitment

		return allocationAccounted;
	}	//	createCashBasedAcct


	/**
	 * 	Get Payment (Unallocated Payment or Payment Selection) Acct of Bank Account
	 *	@param as accounting schema
	 *	@param C_Payment_ID payment
	 *	@return acct
	 */
	private MAccount getPaymentAcct (MAcctSchema as, int C_Payment_ID)
	{
		setC_BankAccount_ID(0);
		//	Doc.ACCTTYPE_UnallocatedCash (AR) or C_Prepayment
		//	or Doc.ACCTTYPE_PaymentSelect (AP) or V_Prepayment
		int accountType = Doc.ACCTTYPE_UnallocatedCash;
		//
		int C_Charge_ID = 0;
		
		String sql = "SELECT p.C_BankAccount_ID, d.DocBaseType, p.IsReceipt, p.IsPrepayment, p.C_Charge_ID "
				+ "FROM C_Payment p INNER JOIN C_DocType d ON (p.C_DocType_ID=d.C_DocType_ID) "
				+ "WHERE C_Payment_ID=?";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (sql, getTrxName());
			pstmt.setInt (1, C_Payment_ID);
			rs = pstmt.executeQuery ();
			if (rs.next ())
			{
				setC_BankAccount_ID(rs.getInt(1));
				C_Charge_ID = rs.getInt(5);				// Charge
				if (DOCTYPE_APPayment.equals(rs.getString(2)))
					accountType = Doc.ACCTTYPE_PaymentSelect;
				//	Prepayment
				if ("Y".equals(rs.getString(4)))		//	Prepayment
				{
					if ("Y".equals(rs.getString(3)))	//	Receipt
						accountType = Doc.ACCTTYPE_C_Prepayment;
					else
						accountType = Doc.ACCTTYPE_V_Prepayment;
				}
			}
 		}
		catch (Exception e)
		{
			throw new RuntimeException(e.getLocalizedMessage(), e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}

		//
		if (getC_BankAccount_ID() <= 0)
		{
			log.log(Level.SEVERE, "NONE for C_Payment_ID=" + C_Payment_ID);
			return null;
		}
		
		if (C_Charge_ID != 0)
			return MCharge.getAccount(C_Charge_ID, as);
		return getAccount (accountType, as);
	}	//	getPaymentAcct

	/**
	 * 	Get Cash (Transfer) Acct of CashBook
	 *	@param as accounting schema
	 *	@param C_CashLine_ID
	 *	@return acct
	 */
	private MAccount getCashAcct (MAcctSchema as, int C_CashLine_ID)
	{
		String sql = "SELECT c.C_CashBook_ID "
				+ "FROM C_Cash c, C_CashLine cl "
				+ "WHERE c.C_Cash_ID=cl.C_Cash_ID AND cl.C_CashLine_ID=?";
		setC_CashBook_ID(DB.getSQLValue(null, sql, C_CashLine_ID));

		if (getC_CashBook_ID() <= 0)
		{
			log.log(Level.SEVERE, "NONE for C_CashLine_ID=" + C_CashLine_ID);
			return null;
		}
		return getAccount(Doc.ACCTTYPE_CashTransfer, as);
	}	//	getCashAcct


	/**
	 * 	Create Tax Correction.<br/>
	 * 	Requirement: Adjust the tax amount, if you did not receive the full
	 * 	amount of the invoice (payment discount, write-off).
	 * 	Applies to many countries with VAT.
	 * <pre>
	 * 	Example:
	 * 		Invoice:	Net $100 + Tax1 $15 + Tax2 $5 = Total $120
	 * 		Payment:	$115 (i.e. $5 underpayment)
	 * 		Tax Adjustment = Tax1 = 0.63 (15/120*5) Tax2 = 0.21 (5/120/5)
	 *  </pre>
	 * 	@param as accounting schema
	 * 	@param fact fact
	 * 	@param line Allocation line
	 *	@param DiscountAccount discount acct
	 *	@param WriteOffAccoint write off acct
	 *	@return true if created
	 */
	private boolean createTaxCorrection (MAcctSchema as, Fact fact,
		DocLine_Allocation line,
		MAccount DiscountAccount, MAccount WriteOffAccoint, boolean isSOTrx)
	{
		if (log.isLoggable(Level.INFO)) log.info (line.toString());
		BigDecimal discount = Env.ZERO;
		if (as.isTaxCorrectionDiscount())
			discount = line.getDiscountAmt();
		BigDecimal writeOff = Env.ZERO;
		if (as.isTaxCorrectionWriteOff())
			writeOff = line.getWriteOffAmt();

		Doc_AllocationTax tax = new Doc_AllocationTax (
			DiscountAccount, discount, 	WriteOffAccoint, writeOff, isSOTrx);

		//	Get Source Amounts with account
		String sql = "SELECT * "
				+ "FROM Fact_Acct "
				+ "WHERE AD_Table_ID=? AND Record_ID=?"	//	Invoice
				+ " AND C_AcctSchema_ID=?"
				+ " AND Line_ID IS NULL";	//	header lines like tax or total
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, getTrxName());
			pstmt.setInt(1, MInvoice.Table_ID);
			pstmt.setInt(2, line.getC_Invoice_ID());
			pstmt.setInt(3, as.getC_AcctSchema_ID());
			rs = pstmt.executeQuery();
			while (rs.next())
				tax.addInvoiceFact (new MFactAcct(getCtx(), rs, fact.get_TrxName()));
		}
		catch (Exception e)
		{
			throw new RuntimeException(e.getLocalizedMessage(), e);
		}
		finally {
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}
		//	Invoice Not posted
		if (tax.getLineCount() == 0)
		{
			log.warning ("Invoice not posted yet - " + line);
			return false;
		}
		//	size = 1 if no tax
		if (tax.getLineCount() < 2)
			return true;
		return tax.createEntries (as, fact, line);

	}	//	createTaxCorrection

	/**
	 * 	Create Realized Gain & Loss.<br/>
	 * 	Compares the Accounted Amount of the Invoice to the
	 * 	Accounted Amount of the Allocation.
	 *  @param line Allocation line
	 *	@param as accounting schema
	 *	@param fact fact
	 *	@param acct account
	 *	@param invoice invoice
	 *	@param allocationSource source amt
	 *	@param allocationAccounted acct amt
	 *	@return Error Message or null if OK
	 */
	private String createInvoiceGainLoss (DocLine line, MAcctSchema as, Fact fact, MAccount acct,
		MInvoice invoice, BigDecimal allocationSource, BigDecimal allocationAccounted)
	{
		BigDecimal invoiceSource = null;
		BigDecimal invoiceAccounted = null;
		//
		StringBuilder sql = new StringBuilder()
			.append("SELECT SUM(AmtSourceDr), SUM(AmtAcctDr), SUM(AmtSourceCr), SUM(AmtAcctCr)")
			.append(" FROM Fact_Acct ")
			.append("WHERE AD_Table_ID=? AND Record_ID=?")
			.append(" AND C_AcctSchema_ID=?")
			.append(" AND Account_ID=?")
			.append(" AND PostingType='A'");

		// For Invoice
		List<Object> valuesInv = DB.getSQLValueObjectsEx(getTrxName(), sql.toString(),
				MInvoice.Table_ID, invoice.getC_Invoice_ID(), as.getC_AcctSchema_ID(), acct.getAccount_ID());
		if (valuesInv != null && valuesInv.size() >= 4) {
			if (invoice.getReversal_ID() == 0 || invoice.get_ID() < invoice.getReversal_ID())
			{
				if (hasDebitTradeAmt(invoice)) {
					invoiceSource = (BigDecimal) valuesInv.get(0); // AmtSourceDr
					invoiceAccounted = (BigDecimal) valuesInv.get(1); // AmtAcctDr
				} else {
					invoiceSource = (BigDecimal) valuesInv.get(2); // AmtSourceCr
					invoiceAccounted = (BigDecimal) valuesInv.get(3); // AmtAcctCr
				}
			}
			else
			{
				if (hasDebitTradeAmt(invoice)) {
					invoiceSource = (BigDecimal) valuesInv.get(2); // AmtSourceCr
					invoiceAccounted = (BigDecimal) valuesInv.get(3); // AmtAcctCr
				} else {
					invoiceSource = (BigDecimal) valuesInv.get(0); // AmtSourceDr
					invoiceAccounted = (BigDecimal) valuesInv.get(1); // AmtAcctDr
				}
			}
		}
		
		// 	Requires that Invoice is Posted
		if (invoiceSource == null || invoiceAccounted == null)
			return "Gain/Loss - Invoice not posted yet";
		//
		StringBuilder description = new StringBuilder("Invoice=(").append(invoice.getC_Currency_ID()).append(")").append(invoiceSource).append("/").append(invoiceAccounted)
			.append(" - Allocation=(").append(getC_Currency_ID()).append(")").append(allocationSource).append("/").append(allocationAccounted);
		if (log.isLoggable(Level.FINE)) log.fine(description.toString());
		
		BigDecimal acctDifference = null;	//	gain is negative
		//	Full Payment in currency
		if (allocationSource.abs().compareTo(invoiceSource.abs()) == 0)
		{
			acctDifference = invoiceAccounted.abs().subtract(allocationAccounted.abs());	//	gain is negative

			StringBuilder d2 = new StringBuilder("(full) = ").append(acctDifference);
			if (log.isLoggable(Level.FINE)) log.fine(d2.toString());
			description.append(" - ").append(d2);
		}
		else	//	partial or MC
		{
			//JPIERE-0052 bug fix multi currency in case of Override Currency Rate at Invoice
			BigDecimal allocationAccounted0 = Env.ZERO;
			if(invoice.isOverrideCurrencyRate())
			{
				allocationAccounted0 = allocationSource.multiply(invoice.getCurrencyRate());
						
			}else {
				allocationAccounted0 = MConversionRate.convert(getCtx(),
						allocationSource, getC_Currency_ID(),
						as.getC_Currency_ID(), invoice.getDateAcct(),
						invoice.getC_ConversionType_ID(), invoice.getAD_Client_ID(), invoice.getAD_Org_ID());
			}
			//JPIERE-0052 bug fix
			acctDifference = allocationAccounted0.abs().subtract(allocationAccounted.abs());			
			//	ignore Tolerance
			if (acctDifference.abs().compareTo(TOLERANCE) < 0)
				acctDifference = Env.ZERO;
			//	Round
			int precision = as.getStdPrecision();
			if (acctDifference.scale() > precision)
				acctDifference = acctDifference.setScale(precision, RoundingMode.HALF_UP);
			StringBuilder d2 = new StringBuilder("(partial) = ").append(acctDifference);
			if (log.isLoggable(Level.FINE)) log.fine(d2.toString());
			description.append(" - ").append(d2);
		}

		if (acctDifference.signum() == 0)
		{
			log.fine("No Difference");
			return null;
		}

		MAccount gain = MAccount.get (as.getCtx(), as.getAcctSchemaDefault().getRealizedGain_Acct());
		MAccount loss = MAccount.get (as.getCtx(), as.getAcctSchemaDefault().getRealizedLoss_Acct());
		//

		MAllocationHdr alloc = (MAllocationHdr) getPO();
		if (alloc.getReversal_ID() == 0 || alloc.get_ID() < alloc.getReversal_ID())
		{
			if (hasDebitTradeAmt(invoice))
			{
				FactLine fl = fact.createLine (line, loss, gain, as.getC_Currency_ID(), acctDifference);
				fl.setDescription(description.toString());
				setInvoiceInfo(fl, invoice, true);//JPIERE-0052
				setPaymentInfo(fl, line, false);//JPIERE-0052
				invGainLossFactLines.add(fl);
				
				fl = fact.createLine (line, acct, as.getC_Currency_ID(), acctDifference.negate());
				fl.setDescription(description.toString());
				setInvoiceInfo(fl, invoice, true);//JPIERE-0052
			}
			else
			{
				FactLine fl = fact.createLine (line, acct, as.getC_Currency_ID(), acctDifference);
				fl.setDescription(description.toString());
				setInvoiceInfo(fl, invoice, true);//JPIERE-0052
				
				fl = fact.createLine (line, loss, gain, as.getC_Currency_ID(), acctDifference.negate());
				fl.setDescription(description.toString());
				setInvoiceInfo(fl, invoice, true);//JPIERE-0052
				setPaymentInfo(fl, line, false);//JPIERE-0052
				invGainLossFactLines.add(fl);	
			}
		}
		else
		{
			if (hasDebitTradeAmt(invoice))
			{
				FactLine fl = fact.createLine (line, acct, as.getC_Currency_ID(), acctDifference);
				fl.setDescription(description.toString());
				setInvoiceInfo(fl, invoice, true);//JPIERE-0052
				
				fl = fact.createLine (line, gain, loss, as.getC_Currency_ID(), acctDifference.negate());
				fl.setDescription(description.toString());
				setInvoiceInfo(fl, invoice, true);//JPIERE-0052
				setPaymentInfo(fl, line, false);//JPIERE-0052
				invGainLossFactLines.add(fl);
			}
			else
			{
				FactLine fl = fact.createLine (line, gain, loss, as.getC_Currency_ID(), acctDifference);
				fl.setDescription(description.toString());
				setInvoiceInfo(fl, invoice, true);//JPIERE-0052
				setPaymentInfo(fl, line, false);//JPIERE-0052
				invGainLossFactLines.add(fl);
				
				fl = fact.createLine (line, acct, as.getC_Currency_ID(), acctDifference.negate());
				fl.setDescription(description.toString());
				setInvoiceInfo(fl, invoice, true);//JPIERE-0052
			}
		}
		return null;
	}
	
	/**
	 * 	Create Realized Gain & Loss.<br/>
	 * 	Compares the Accounted Amount of the Payment to the
	 * 	Accounted Amount of the Allocation.
	 * 	@param line Allocation line
	 *	@param as accounting schema
	 *	@param fact fact
	 *	@param acct account
	 *	@param payment payment
	 *	@param allocationSource source amt
	 *	@param allocationAccounted acct amt
	 *	@return Error Message or null if OK
	 */
	private String createPaymentGainLoss (DocLine line, MAcctSchema as, Fact fact, MAccount acct,
		MPayment payment, BigDecimal allocationSource, BigDecimal allocationAccounted)
	{
		BigDecimal paymentSource = null;
		BigDecimal paymentAccounted = null;
		//
		StringBuilder sql = new StringBuilder()
			.append("SELECT SUM(AmtSourceDr), SUM(AmtAcctDr), SUM(AmtSourceCr), SUM(AmtAcctCr)")
			.append(" FROM Fact_Acct ")
			.append("WHERE AD_Table_ID=? AND Record_ID=?")
			.append(" AND C_AcctSchema_ID=?")
			.append(" AND Account_ID = ? ")
			.append(" AND PostingType='A'");

		// For Payment
		List<Object> valuesPay = DB.getSQLValueObjectsEx(getTrxName(), sql.toString(),
				MPayment.Table_ID, payment.getC_Payment_ID(), as.getC_AcctSchema_ID(), acct.getAccount_ID());
		if (valuesPay != null && valuesPay.size() >= 4) {
			paymentSource = (BigDecimal) valuesPay.get(0); // AmtSourceDr
			paymentAccounted = (BigDecimal) valuesPay.get(1); // AmtAcctDr
			if (paymentSource != null && paymentAccounted != null) {
				if (paymentSource.signum() == 0 && paymentAccounted.signum() == 0) {
					paymentSource = (BigDecimal) valuesPay.get(2); // AmtSourceCr
					paymentAccounted = (BigDecimal) valuesPay.get(3); // AmtAcctCr
				}
			}
		}
		
		// 	Requires that Allocation is Posted
		if (paymentSource == null || paymentAccounted == null)
			return null; //"Gain/Loss - Payment not posted yet";
		//
		StringBuilder description = new StringBuilder("Payment=(").append(payment.getC_Currency_ID()).append(")").append(paymentSource).append("/").append(paymentAccounted)
			.append(" - Allocation=(").append(getC_Currency_ID()).append(")").append(allocationSource).append("/").append(allocationAccounted);
		if (log.isLoggable(Level.FINE)) log.fine(description.toString());
		
		BigDecimal acctDifference = null;	//	gain is negative
		//	Full Payment in currency
		if (allocationSource.abs().compareTo(paymentSource.abs()) == 0)
		{
			acctDifference = allocationAccounted.abs().subtract(paymentAccounted.abs());	//	gain is negative
			StringBuilder d2 = new StringBuilder("(full) = ").append(acctDifference);
			if (log.isLoggable(Level.FINE)) log.fine(d2.toString());
			description.append(" - ").append(d2);
		}
		else
		{
			//JPIERE-0052 bug fix multi currency in case of Override Currency Rate at Invoice
			BigDecimal allocationAccounted0 = Env.ZERO;
			if(payment.isOverrideCurrencyRate())
			{
				allocationAccounted0 = allocationSource.multiply(payment.getCurrencyRate());
						
			}else {
			
				allocationAccounted0 = MConversionRate.convert(getCtx(),
						allocationSource, getC_Currency_ID(),
						as.getC_Currency_ID(), payment.getDateAcct(),
						payment.getC_ConversionType_ID(), payment.getAD_Client_ID(), payment.getAD_Org_ID());
			}
			//JPIERE-0052 bug fix
			acctDifference = allocationAccounted.abs().subtract(allocationAccounted0.abs());
			//	ignore Tolerance
			if (acctDifference.abs().compareTo(TOLERANCE) < 0)
				acctDifference = Env.ZERO;
			//	Round
			int precision = as.getStdPrecision();
			if (acctDifference.scale() > precision)
				acctDifference = acctDifference.setScale(precision, RoundingMode.HALF_UP);
			StringBuilder d2 = new StringBuilder("(partial) = ").append(acctDifference);
			if (log.isLoggable(Level.FINE)) log.fine(d2.toString());
			description.append(" - ").append(d2);
		}

		if (acctDifference == null || acctDifference.signum() == 0)
		{
			if (log.isLoggable(Level.FINE))
				log.fine("No Difference");
			return null;
		}

		MAccount gain = MAccount.get (as.getCtx(), as.getAcctSchemaDefault().getRealizedGain_Acct());
		MAccount loss = MAccount.get (as.getCtx(), as.getAcctSchemaDefault().getRealizedLoss_Acct());
		//
		if ((payment.isReceipt() && payment.getPayAmt().signum() >= 0) || (!payment.isReceipt() && payment.getPayAmt().signum() < 0))
		{
			FactLine fl = fact.createLine (line, acct, as.getC_Currency_ID(), acctDifference.negate());
			fl.setDescription(description.toString());
			setPaymentInfo(fl, payment, false);//JPIERE-0052
			fl = fact.createLine (line, loss, gain, as.getC_Currency_ID(), acctDifference);
			fl.setDescription(description.toString());
			setPaymentInfo(fl, payment, false);//JPIERE-0052
			payGainLossFactLines.add(fl);
		}
		else
		{
			FactLine fl = fact.createLine (line, acct, as.getC_Currency_ID(), acctDifference);
			fl.setDescription(description.toString());
			setPaymentInfo(fl, payment, false);//JPIERE-0052
			fl = fact.createLine (line, loss, gain, as.getC_Currency_ID(), acctDifference.negate());
			fl.setDescription(description.toString());
			setPaymentInfo(fl, payment, false);//JPIERE-0052
			payGainLossFactLines.add(fl);
		}
		return null;
	}

	/**
	 * 	Create Rounding Correction.
	 * 	Compares the Accounted Amount of the AR/AP Invoice to the
	 * 	Accounted Amount of the AR/AP Allocation
	 *	@param as accounting schema
	 *	@param fact fact
	 *	@param acct account
	 *	@return Error Message or null if OK
	 */
	@SuppressWarnings("unused")//JPIERE-0052
	private String createInvoiceRoundingCorrection (MAcctSchema as, Fact fact, MAccount acct)
	{
		ArrayList<MInvoice> invList = new ArrayList<MInvoice>();
		Hashtable<Integer, Integer> htInvAllocLine = new Hashtable<Integer, Integer>();
		for (int i = 0; i < p_lines.length; i++)
		{
			MInvoice invoice = null;
			DocLine_Allocation line = (DocLine_Allocation)p_lines[i];
			if (line.getC_Invoice_ID() != 0)
			{
				invoice = new MInvoice (getCtx(), line.getC_Invoice_ID(), getTrxName());
				if (!invList.contains(invoice))
					invList.add(invoice);
				htInvAllocLine.put(invoice.getC_Invoice_ID(), line.get_ID());
			}
		}

		Hashtable<Integer, BigDecimal> htInvSource = new Hashtable<Integer, BigDecimal>();
		Hashtable<Integer, BigDecimal> htInvAccounted = new Hashtable<Integer, BigDecimal>();
		for (MInvoice invoice : invList)
		{
			StringBuilder sql = new StringBuilder()
				.append("SELECT SUM(AmtSourceDr), SUM(AmtAcctDr), SUM(AmtSourceCr), SUM(AmtAcctCr)")
				.append(" FROM Fact_Acct ")
				.append("WHERE AD_Table_ID=? AND Record_ID=?")
				.append(" AND C_AcctSchema_ID=?")
				.append(" AND Account_ID=?")
				.append(" AND PostingType='A'");

			// For Invoice
			List<Object> valuesInv = DB.getSQLValueObjectsEx(getTrxName(), sql.toString(),
					MInvoice.Table_ID, invoice.getC_Invoice_ID(), as.getC_AcctSchema_ID(), acct.getAccount_ID());
			if (valuesInv != null && valuesInv.size() >= 4) {
				BigDecimal invoiceSource = null;
				BigDecimal invoiceAccounted = null;
				if (invoice.getReversal_ID() == 0 || invoice.get_ID() < invoice.getReversal_ID())
				{
						if (hasDebitTradeAmt(invoice)) {
							invoiceSource = (BigDecimal) valuesInv.get(0); // AmtSourceDr
							invoiceAccounted = (BigDecimal) valuesInv.get(1); // AmtAcctDr
						} else {
							invoiceSource = (BigDecimal) valuesInv.get(2); // AmtSourceCr
							invoiceAccounted = (BigDecimal) valuesInv.get(3); // AmtAcctCr
						}
				}
				else
				{
						if (hasDebitTradeAmt(invoice)) {
							invoiceSource = (BigDecimal) valuesInv.get(2); // AmtSourceCr
							invoiceAccounted = (BigDecimal) valuesInv.get(3); // AmtAcctCr
						} else {
							invoiceSource = (BigDecimal) valuesInv.get(0); // AmtSourceDr
							invoiceAccounted = (BigDecimal) valuesInv.get(1); // AmtAcctDr
						}
				}
				htInvSource.put(invoice.getC_Invoice_ID(), invoiceSource);
				htInvAccounted.put(invoice.getC_Invoice_ID(), invoiceAccounted);
			}
		}
		
		MAccount gain = MAccount.get (as.getCtx(), as.getAcctSchemaDefault().getRealizedGain_Acct());
		MAccount loss = MAccount.get (as.getCtx(), as.getAcctSchemaDefault().getRealizedLoss_Acct());

		Hashtable<Integer, BigDecimal> htTotalAmtSourceDr = new Hashtable<Integer, BigDecimal>();
		Hashtable<Integer, BigDecimal> htTotalAmtAcctDr = new Hashtable<Integer, BigDecimal>();
		Hashtable<Integer, BigDecimal> htTotalAmtSourceCr = new Hashtable<Integer, BigDecimal>();
		Hashtable<Integer, BigDecimal> htTotalAmtAcctCr = new Hashtable<Integer, BigDecimal>();
		FactLine[] factlines = fact.getLines();
		for (FactLine factLine : factlines)
		{
			if (factLine.getLine_ID() > 0)
			{
				MAllocationLine allocationLine = new MAllocationLine(getCtx(), factLine.getLine_ID(), getTrxName());
				if (allocationLine.getC_Invoice_ID() > 0)
				{
					if (factLine.getAccount_ID() == acct.getAccount_ID())
					{
						BigDecimal totalAmtSourceDr = htTotalAmtSourceDr.get(allocationLine.getC_Invoice_ID());
						if (totalAmtSourceDr == null)
							totalAmtSourceDr = Env.ZERO;
						BigDecimal totalAmtAcctDr = htTotalAmtAcctDr.get(allocationLine.getC_Invoice_ID());
						if (totalAmtAcctDr == null)
							totalAmtAcctDr = Env.ZERO;
						BigDecimal totalAmtSourceCr = htTotalAmtSourceCr.get(allocationLine.getC_Invoice_ID());
						if (totalAmtSourceCr == null)
							totalAmtSourceCr = Env.ZERO;
						BigDecimal totalAmtAcctCr = htTotalAmtAcctCr.get(allocationLine.getC_Invoice_ID());
						if (totalAmtAcctCr == null)
							totalAmtAcctCr = Env.ZERO;
						
						totalAmtSourceDr = totalAmtSourceDr.add(factLine.getAmtSourceDr());
						totalAmtAcctDr = totalAmtAcctDr.add(factLine.getAmtAcctDr());
						totalAmtSourceCr = totalAmtSourceCr.add(factLine.getAmtSourceCr());
						totalAmtAcctCr = totalAmtAcctCr.add(factLine.getAmtAcctCr());
						
						htTotalAmtSourceDr.put(allocationLine.getC_Invoice_ID(), totalAmtSourceDr);
						htTotalAmtAcctDr.put(allocationLine.getC_Invoice_ID(), totalAmtAcctDr);
						htTotalAmtSourceCr.put(allocationLine.getC_Invoice_ID(), totalAmtSourceCr);
						htTotalAmtAcctCr.put(allocationLine.getC_Invoice_ID(), totalAmtAcctCr);
					}
					else if (factLine.getAccount_ID() == gain.getAccount_ID() || factLine.getAccount_ID() == loss.getAccount_ID())
					{
						if (!invGainLossFactLines.contains(factLine))
							continue;
						
						BigDecimal totalAmtSourceDr = htTotalAmtSourceDr.get(allocationLine.getC_Invoice_ID());
						if (totalAmtSourceDr == null)
							totalAmtSourceDr = Env.ZERO;
						BigDecimal totalAmtSourceCr = htTotalAmtSourceCr.get(allocationLine.getC_Invoice_ID());
						if (totalAmtSourceCr == null)
							totalAmtSourceCr = Env.ZERO;
						
						totalAmtSourceDr = totalAmtSourceDr.subtract(factLine.getAmtSourceCr());
						totalAmtSourceCr = totalAmtSourceCr.subtract(factLine.getAmtSourceDr());
						
						htTotalAmtSourceDr.put(allocationLine.getC_Invoice_ID(), totalAmtSourceDr);
						htTotalAmtSourceCr.put(allocationLine.getC_Invoice_ID(), totalAmtSourceCr);
					}
				}
			}
		}

		Hashtable<Integer, BigDecimal> htAllocInvSource = new Hashtable<Integer, BigDecimal>();
		Hashtable<Integer, BigDecimal> htAllocInvAccounted = new Hashtable<Integer, BigDecimal>();
		for (MInvoice invoice : invList)
		{
			BigDecimal allocateSource = Env.ZERO;
			BigDecimal allocateAccounted = Env.ZERO;

			BigDecimal totalAmtSourceDr = htTotalAmtSourceDr.get(invoice.getC_Invoice_ID());
			if (totalAmtSourceDr == null)
				totalAmtSourceDr = Env.ZERO;
			BigDecimal totalAmtAcctDr = htTotalAmtAcctDr.get(invoice.getC_Invoice_ID());
			if (totalAmtAcctDr == null)
				totalAmtAcctDr = Env.ZERO;
			BigDecimal totalAmtSourceCr = htTotalAmtSourceCr.get(invoice.getC_Invoice_ID());
			if (totalAmtSourceCr == null)
				totalAmtSourceCr = Env.ZERO;
			BigDecimal totalAmtAcctCr = htTotalAmtAcctCr.get(invoice.getC_Invoice_ID());
			if (totalAmtAcctCr == null)
				totalAmtAcctCr = Env.ZERO;
			
			if (totalAmtSourceDr.signum() == 0 && totalAmtAcctDr.signum() == 0)
			{
				allocateSource = allocateSource.add(totalAmtSourceCr);
				allocateAccounted = allocateAccounted.add(totalAmtAcctCr);
			}
			else if (totalAmtSourceCr.signum() == 0 && totalAmtAcctCr.signum() == 0)
			{
				allocateSource = allocateSource.add(totalAmtSourceDr);
				allocateAccounted = allocateAccounted.add(totalAmtAcctDr);
			}
			else
			{
				if (totalAmtAcctDr.compareTo(totalAmtAcctCr) > 0)
				{
					allocateSource = allocateSource.add(totalAmtSourceDr).subtract(totalAmtSourceCr);
					allocateAccounted = allocateAccounted.add(totalAmtAcctDr).subtract(totalAmtAcctCr);
				}
				else
				{
					allocateSource = allocateSource.add(totalAmtSourceCr).subtract(totalAmtSourceDr);
					allocateAccounted = allocateAccounted.add(totalAmtAcctCr).subtract(totalAmtAcctDr);
				}
			}			

			MAllocationHdr[] allocations = MAllocationHdr.getOfInvoice(getCtx(), invoice.get_ID(), getTrxName());
			for (MAllocationHdr alloc : allocations)
			{
				if (alloc.get_ID() == get_ID())
					continue;
				
				BigDecimal currencyAdjustment = Env.ZERO;
				StringBuilder sql = new StringBuilder()
					.append("SELECT SUM(AmtSourceDr), SUM(AmtAcctDr), SUM(AmtSourceCr), SUM(AmtAcctCr)")
						.append(" FROM Fact_Acct ")
						.append("WHERE AD_Table_ID=? AND Record_ID=?")	//	allocation
						.append(" AND C_AcctSchema_ID=?")
						.append(" AND PostingType='A'")
						.append(" AND Account_ID=?")
						.append(" AND Line_ID IN (SELECT C_AllocationLine_ID FROM C_AllocationLine WHERE C_AllocationHdr_ID=? AND C_Invoice_ID=?)");
		
				// For Allocation
				List<Object> valuesAlloc = DB.getSQLValueObjectsEx(getTrxName(), sql.toString(),
						MAllocationHdr.Table_ID, alloc.get_ID(), as.getC_AcctSchema_ID(), acct.getAccount_ID(), alloc.get_ID(), invoice.getC_Invoice_ID());
				if (valuesAlloc != null && valuesAlloc.size() >= 4) {
					totalAmtSourceDr = (BigDecimal) valuesAlloc.get(0);
					if (totalAmtSourceDr == null)
						totalAmtSourceDr = Env.ZERO;
					totalAmtAcctDr = (BigDecimal) valuesAlloc.get(1);
					if (totalAmtAcctDr == null)
						totalAmtAcctDr = Env.ZERO;
					totalAmtSourceCr = (BigDecimal) valuesAlloc.get(2);
					if (totalAmtSourceCr == null)
						totalAmtSourceCr = Env.ZERO;
					totalAmtAcctCr = (BigDecimal) valuesAlloc.get(3);
					if (totalAmtAcctCr == null)
						totalAmtAcctCr = Env.ZERO;
					
					if (totalAmtSourceDr.signum() == 0 && totalAmtAcctDr.signum() == 0)
					{
						allocateSource = allocateSource.add(totalAmtSourceCr);
						allocateAccounted = allocateAccounted.add(totalAmtAcctCr);
					}
					else if (totalAmtSourceCr.signum() == 0 && totalAmtAcctCr.signum() == 0)
					{
						allocateSource = allocateSource.add(totalAmtSourceDr);
						allocateAccounted = allocateAccounted.add(totalAmtAcctDr);
					}
					else
					{
						if (totalAmtAcctDr.compareTo(totalAmtAcctCr) > 0)
						{
							allocateSource = allocateSource.add(totalAmtSourceDr);
							allocateAccounted = allocateAccounted.add(totalAmtAcctDr).subtract(totalAmtAcctCr);
							currencyAdjustment = currencyAdjustment.add(totalAmtAcctCr);
						}
						else
						{
							allocateSource = allocateSource.add(totalAmtSourceCr);
							allocateAccounted = allocateAccounted.add(totalAmtAcctCr).subtract(totalAmtAcctDr);
							currencyAdjustment = currencyAdjustment.add(totalAmtAcctDr);
						}
					}
				}
				
				sql = new StringBuilder()
					.append("SELECT SUM(AmtSourceDr), SUM(AmtAcctDr), SUM(AmtSourceCr), SUM(AmtAcctCr)")
					.append(" FROM Fact_Acct ")
					.append("WHERE AD_Table_ID=? AND Record_ID=?")	//	allocation
					.append(" AND C_AcctSchema_ID=?")
					.append(" AND PostingType='A'")
					.append(" AND (Account_ID=? OR Account_ID=? OR Account_ID=?)")
					.append(" AND Description LIKE 'Invoice%'")
					.append(" AND Line_ID IN (SELECT C_AllocationLine_ID FROM C_AllocationLine WHERE C_AllocationHdr_ID=? AND C_Invoice_ID=?)");
				
				// For Allocation
				valuesAlloc = DB.getSQLValueObjectsEx(getTrxName(), sql.toString(),
						MAllocationHdr.Table_ID, alloc.get_ID(), as.getC_AcctSchema_ID(), 
						gain.getAccount_ID(), loss.getAccount_ID(), as.getCurrencyBalancing_Acct().getAccount_ID(),
						alloc.get_ID(), invoice.getC_Invoice_ID());
				if (valuesAlloc != null && valuesAlloc.size() >= 4) {
					totalAmtSourceDr = (BigDecimal) valuesAlloc.get(0);
					if (totalAmtSourceDr == null)
						totalAmtSourceDr = Env.ZERO;
					totalAmtAcctDr = (BigDecimal) valuesAlloc.get(1);
					if (totalAmtAcctDr == null)
						totalAmtAcctDr = Env.ZERO;
					totalAmtSourceCr = (BigDecimal) valuesAlloc.get(2);
					if (totalAmtSourceCr == null)
						totalAmtSourceCr = Env.ZERO;
					totalAmtAcctCr = (BigDecimal) valuesAlloc.get(3);
					if (totalAmtAcctCr == null)
						totalAmtAcctCr = Env.ZERO;
					
					allocateSource = allocateSource.subtract(totalAmtSourceDr).subtract(totalAmtSourceCr).add(currencyAdjustment);
				}
			}
			
			htAllocInvSource.put(invoice.getC_Invoice_ID(), allocateSource);
			htAllocInvAccounted.put(invoice.getC_Invoice_ID(), allocateAccounted);
		}

		for (MInvoice invoice : invList)
		{
			BigDecimal invSource = htInvSource.get(invoice.getC_Invoice_ID());
			if (invSource == null)
				invSource = Env.ZERO;
			BigDecimal invAccounted = htInvAccounted.get(invoice.getC_Invoice_ID());
			if (invAccounted == null)
				invAccounted = Env.ZERO;
			BigDecimal allocInvSource = htAllocInvSource.get(invoice.getC_Invoice_ID());
			if (allocInvSource == null)
				allocInvSource = Env.ZERO;
			BigDecimal allocInvAccounted = htAllocInvAccounted.get(invoice.getC_Invoice_ID());
			if (allocInvAccounted == null)
				allocInvAccounted = Env.ZERO;
			
			StringBuilder description = new StringBuilder("Invoice=(").append(getC_Currency_ID()).append(")").append(invSource).append("/").append(invAccounted)
					.append(" - Allocation=(").append(getC_Currency_ID()).append(")").append(allocInvSource).append("/").append(allocInvAccounted);
			if (log.isLoggable(Level.FINE)) log.fine(description.toString());
			BigDecimal acctDifference = null;
			if (allocInvSource.abs().compareTo(invSource.abs()) == 0)
			{
				acctDifference = allocInvAccounted.abs().subtract(invAccounted.abs());	//	gain is negative
				StringBuilder d2 = new StringBuilder("(full) = ").append(acctDifference);
				if (log.isLoggable(Level.FINE)) log.fine(d2.toString());
				description.append(" - ").append(d2);
			}
			
			if (acctDifference == null || acctDifference.signum() == 0)
			{
				log.fine("No Difference");
				continue;
			}
			
			//
			Integer C_AllocationLine_ID = htInvAllocLine.get(invoice.getC_Invoice_ID());
			MAllocationHdr alloc = (MAllocationHdr) getPO();
			if (alloc.getReversal_ID() == 0 || alloc.get_ID() < alloc.getReversal_ID())
			{
				if (hasDebitTradeAmt(invoice))
				{
					FactLine fl = fact.createLine (null, acct, as.getC_Currency_ID(), acctDifference);
					fl.setDescription(description.toString());
					fl.setLine_ID(C_AllocationLine_ID == null ? 0 : C_AllocationLine_ID);
					if (!fact.isAcctBalanced())
					{
						if (as.isCurrencyBalancing() && as.getC_Currency_ID() != invoice.getC_Currency_ID())
							fl = fact.createLine (null, as.getCurrencyBalancing_Acct(),as.getC_Currency_ID(), acctDifference.negate());
						else
							fl = fact.createLine (null, loss, gain,as.getC_Currency_ID(), acctDifference.negate());
						fl.setDescription(description.toString());
						fl.setLine_ID(C_AllocationLine_ID == null ? 0 : C_AllocationLine_ID);
					}				
				}
				else
				{
					FactLine fl = fact.createLine (null, acct, as.getC_Currency_ID(), acctDifference.negate());
					fl.setDescription(description.toString());
					fl.setLine_ID(C_AllocationLine_ID == null ? 0 : C_AllocationLine_ID);
					if (!fact.isAcctBalanced())
					{
						if (as.isCurrencyBalancing() && as.getC_Currency_ID() != invoice.getC_Currency_ID())
							fl = fact.createLine (null, as.getCurrencyBalancing_Acct(),as.getC_Currency_ID(), acctDifference);
						else
							fl = fact.createLine (null, loss, gain, as.getC_Currency_ID(), acctDifference);
						fl.setDescription(description.toString());
						fl.setLine_ID(C_AllocationLine_ID == null ? 0 : C_AllocationLine_ID);
					}
				}
			}
			else
			{
				if (hasDebitTradeAmt(invoice))
				{
					FactLine fl = fact.createLine (null, acct, as.getC_Currency_ID(), acctDifference.negate());
					fl.setDescription(description.toString());
					fl.setLine_ID(C_AllocationLine_ID == null ? 0 : C_AllocationLine_ID);
					if (!fact.isAcctBalanced())
					{
						if (as.isCurrencyBalancing() && as.getC_Currency_ID() != invoice.getC_Currency_ID())
							fl = fact.createLine (null, as.getCurrencyBalancing_Acct(), as.getC_Currency_ID(), acctDifference);
						else
							fl = fact.createLine (null, gain, loss, as.getC_Currency_ID(), acctDifference);
						fl.setDescription(description.toString());
						fl.setLine_ID(C_AllocationLine_ID == null ? 0 : C_AllocationLine_ID);
					}
				}
				else
				{
					FactLine fl = fact.createLine (null, acct, as.getC_Currency_ID(), acctDifference);
					fl.setDescription(description.toString());
					fl.setLine_ID(C_AllocationLine_ID == null ? 0 : C_AllocationLine_ID);
					if (!fact.isAcctBalanced())
					{
						if (as.isCurrencyBalancing() && as.getC_Currency_ID() != invoice.getC_Currency_ID())
							fl = fact.createLine (null, as.getCurrencyBalancing_Acct(), as.getC_Currency_ID(), acctDifference.negate());
						else 
							fl = fact.createLine (null, gain, loss, as.getC_Currency_ID(), acctDifference.negate());	
						fl.setDescription(description.toString());
						fl.setLine_ID(C_AllocationLine_ID == null ? 0 : C_AllocationLine_ID);
					}
				}
			}
		}
		return null;				
	}	//	createInvoiceRounding

	/**
	 * 	Create Rounding Correction.
	 * 	Compares the Accounted Amount of the Payment to the
	 * 	Accounted Amount of the Allocation
	 *	@param as accounting schema
	 *	@param fact fact
	 *	@return Error Message or null if OK
	 */
	@SuppressWarnings("unused") //JPIERE-0052
	private String createPaymentRoundingCorrection (MAcctSchema as, Fact fact)
	{
		ArrayList<MPayment> payList = new ArrayList<MPayment>();
		Hashtable<Integer, Integer> htPayAllocLine = new Hashtable<Integer, Integer>();
		for (int i = 0; i < p_lines.length; i++)
		{
			MPayment payment = null;
			DocLine_Allocation line = (DocLine_Allocation) p_lines[i];			
			if (line.getC_Payment_ID() != 0)
			{
				payment = new MPayment (getCtx(), line.getC_Payment_ID(), getTrxName());
				if (!payList.contains(payment))
					payList.add(payment);
				htPayAllocLine.put(payment.getC_Payment_ID(), line.get_ID());
			}
		}

		Hashtable<Integer, MAccount> htPayAcct = new Hashtable<Integer, MAccount>();
		Hashtable<Integer, BigDecimal> htPaySource = new Hashtable<Integer, BigDecimal>();
		Hashtable<Integer, BigDecimal> htPayAccounted = new Hashtable<Integer, BigDecimal>();
		for (MPayment payment : payList)
		{
			htPayAcct.put(payment.getC_Payment_ID(), getPaymentAcct(as, payment.getC_Payment_ID()));
			
			StringBuilder sql = new StringBuilder()
				.append("SELECT SUM(AmtSourceDr), SUM(AmtAcctDr), SUM(AmtSourceCr), SUM(AmtAcctCr)")
				.append(" FROM Fact_Acct ")
				.append("WHERE AD_Table_ID=? AND Record_ID=?")
				.append(" AND C_AcctSchema_ID=?")
				.append(" AND Account_ID = ? ")
				.append(" AND PostingType='A'");

			// For Payment
			List<Object> valuesPay = DB.getSQLValueObjectsEx(getTrxName(), sql.toString(),
					MPayment.Table_ID, payment.getC_Payment_ID(), as.getC_AcctSchema_ID(), htPayAcct.get(payment.getC_Payment_ID()).getAccount_ID());
			if (valuesPay != null && valuesPay.size() >= 4) {
				BigDecimal paymentSource = (BigDecimal) valuesPay.get(0); // AmtSourceDr
				BigDecimal paymentAccounted = (BigDecimal) valuesPay.get(1); // AmtAcctDr
				if (paymentSource != null && paymentAccounted != null) {
					if (paymentSource.signum() == 0 && paymentAccounted.signum() == 0) {
						paymentSource = (BigDecimal) valuesPay.get(2); // AmtSourceCr
						paymentAccounted = (BigDecimal) valuesPay.get(3); // AmtAcctCr
					}
					htPaySource.put(payment.getC_Payment_ID(), paymentSource);
					htPayAccounted.put(payment.getC_Payment_ID(), paymentAccounted);
				}
			}
		}
		
		MAccount gain = MAccount.get (as.getCtx(), as.getAcctSchemaDefault().getRealizedGain_Acct());
		MAccount loss = MAccount.get (as.getCtx(), as.getAcctSchemaDefault().getRealizedLoss_Acct());

		Hashtable<Integer, BigDecimal> htTotalAmtSourceDr = new Hashtable<Integer, BigDecimal>();
		Hashtable<Integer, BigDecimal> htTotalAmtAcctDr = new Hashtable<Integer, BigDecimal>();
		Hashtable<Integer, BigDecimal> htTotalAmtSourceCr = new Hashtable<Integer, BigDecimal>();
		Hashtable<Integer, BigDecimal> htTotalAmtAcctCr = new Hashtable<Integer, BigDecimal>();
		FactLine[] factlines = fact.getLines();
		for (FactLine factLine : factlines)
		{
			if (factLine.getLine_ID() > 0)
			{
				MAllocationLine allocationLine = new MAllocationLine(getCtx(), factLine.getLine_ID(), getTrxName());
				if (allocationLine.getC_Payment_ID() > 0)
				{
					if (factLine.getAccount_ID() == htPayAcct.get(allocationLine.getC_Payment_ID()).getAccount_ID())
					{
						BigDecimal totalAmtSourceDr = htTotalAmtSourceDr.get(allocationLine.getC_Payment_ID());
						if (totalAmtSourceDr == null)
							totalAmtSourceDr = Env.ZERO;
						BigDecimal totalAmtAcctDr = htTotalAmtAcctDr.get(allocationLine.getC_Payment_ID());
						if (totalAmtAcctDr == null)
							totalAmtAcctDr = Env.ZERO;
						BigDecimal totalAmtSourceCr = htTotalAmtSourceCr.get(allocationLine.getC_Payment_ID());
						if (totalAmtSourceCr == null)
							totalAmtSourceCr = Env.ZERO;
						BigDecimal totalAmtAcctCr = htTotalAmtAcctCr.get(allocationLine.getC_Payment_ID());
						if (totalAmtAcctCr == null)
							totalAmtAcctCr = Env.ZERO;
						
						totalAmtSourceDr = totalAmtSourceDr.add(factLine.getAmtSourceDr());
						totalAmtAcctDr = totalAmtAcctDr.add(factLine.getAmtAcctDr());
						totalAmtSourceCr = totalAmtSourceCr.add(factLine.getAmtSourceCr());
						totalAmtAcctCr = totalAmtAcctCr.add(factLine.getAmtAcctCr());
						
						htTotalAmtSourceDr.put(allocationLine.getC_Payment_ID(), totalAmtSourceDr);
						htTotalAmtAcctDr.put(allocationLine.getC_Payment_ID(), totalAmtAcctDr);
						htTotalAmtSourceCr.put(allocationLine.getC_Payment_ID(), totalAmtSourceCr);
						htTotalAmtAcctCr.put(allocationLine.getC_Payment_ID(), totalAmtAcctCr);
					}
					else if (factLine.getAccount_ID() == gain.getAccount_ID() || factLine.getAccount_ID() == loss.getAccount_ID())
					{
						if (!payGainLossFactLines.contains(factLine))
							continue;
						
						BigDecimal totalAmtSourceDr = htTotalAmtSourceDr.get(allocationLine.getC_Payment_ID());
						if (totalAmtSourceDr == null)
							totalAmtSourceDr = Env.ZERO;
						BigDecimal totalAmtSourceCr = htTotalAmtSourceCr.get(allocationLine.getC_Payment_ID());
						if (totalAmtSourceCr == null)
							totalAmtSourceCr = Env.ZERO;
						
						totalAmtSourceDr = totalAmtSourceDr.subtract(factLine.getAmtSourceCr());
						totalAmtSourceCr = totalAmtSourceCr.subtract(factLine.getAmtSourceDr());
						
						htTotalAmtSourceDr.put(allocationLine.getC_Payment_ID(), totalAmtSourceDr);
						htTotalAmtSourceCr.put(allocationLine.getC_Payment_ID(), totalAmtSourceCr);
					}
				}
			}
		}

		Hashtable<Integer, BigDecimal> htAllocPaySource = new Hashtable<Integer, BigDecimal>();
		Hashtable<Integer, BigDecimal> htAllocPayAccounted = new Hashtable<Integer, BigDecimal>();
		for (MPayment payment : payList)
		{
			BigDecimal allocateSource = Env.ZERO;
			BigDecimal allocateAccounted = Env.ZERO;

			BigDecimal totalAmtSourceDr = htTotalAmtSourceDr.get(payment.getC_Payment_ID());
			if (totalAmtSourceDr == null)
				totalAmtSourceDr = Env.ZERO;
			BigDecimal totalAmtAcctDr = htTotalAmtAcctDr.get(payment.getC_Payment_ID());
			if (totalAmtAcctDr == null)
				totalAmtAcctDr = Env.ZERO;
			BigDecimal totalAmtSourceCr = htTotalAmtSourceCr.get(payment.getC_Payment_ID());
			if (totalAmtSourceCr == null)
				totalAmtSourceCr = Env.ZERO;
			BigDecimal totalAmtAcctCr = htTotalAmtAcctCr.get(payment.getC_Payment_ID());
			if (totalAmtAcctCr == null)
				totalAmtAcctCr = Env.ZERO;
			
			if (totalAmtSourceDr.signum() == 0 && totalAmtAcctDr.signum() == 0)
			{
				allocateSource = allocateSource.add(totalAmtSourceCr);
				allocateAccounted = allocateAccounted.add(totalAmtAcctCr);
			}
			else if (totalAmtSourceCr.signum() == 0 && totalAmtAcctCr.signum() == 0)
			{
				allocateSource = allocateSource.add(totalAmtSourceDr);
				allocateAccounted = allocateAccounted.add(totalAmtAcctDr);
			}
			else
			{
				if (totalAmtAcctDr.compareTo(totalAmtAcctCr) > 0)
				{
					allocateSource = allocateSource.add(totalAmtSourceDr).subtract(totalAmtSourceCr);
					allocateAccounted = allocateAccounted.add(totalAmtAcctDr).subtract(totalAmtAcctCr);
				}
				else
				{
					allocateSource = allocateSource.add(totalAmtSourceCr).subtract(totalAmtSourceDr);
					allocateAccounted = allocateAccounted.add(totalAmtAcctCr).subtract(totalAmtAcctDr);
				}
			}			

			MAllocationHdr[] allocations = MAllocationHdr.getOfPayment(getCtx(), payment.get_ID(), getTrxName());
			for (MAllocationHdr alloc : allocations)
			{
				if (alloc.get_ID() == get_ID())
					continue;
				
				BigDecimal currencyAdjustment = Env.ZERO;
				StringBuilder sql = new StringBuilder()
					.append("SELECT SUM(AmtSourceDr), SUM(AmtAcctDr), SUM(AmtSourceCr), SUM(AmtAcctCr)")
					.append(" FROM Fact_Acct ")
					.append("WHERE AD_Table_ID=? AND Record_ID=?")	//	allocation
					.append(" AND C_AcctSchema_ID=?")
					.append(" AND PostingType='A'")
					.append(" AND Account_ID=?")
					.append(" AND Line_ID IN (SELECT C_AllocationLine_ID FROM C_AllocationLine WHERE C_AllocationHdr_ID=? AND C_Payment_ID=?)");
				
				// For Allocation
				List<Object> valuesAlloc = DB.getSQLValueObjectsEx(getTrxName(), sql.toString(),
						MAllocationHdr.Table_ID, alloc.get_ID(), as.getC_AcctSchema_ID(), htPayAcct.get(payment.getC_Payment_ID()).getAccount_ID(), alloc.get_ID(), payment.getC_Payment_ID());
				if (valuesAlloc != null && valuesAlloc.size() >= 4) {
					totalAmtSourceDr = (BigDecimal) valuesAlloc.get(0);
					if (totalAmtSourceDr == null)
						totalAmtSourceDr = Env.ZERO;
					totalAmtAcctDr = (BigDecimal) valuesAlloc.get(1);
					if (totalAmtAcctDr == null)
						totalAmtAcctDr = Env.ZERO;
					totalAmtSourceCr = (BigDecimal) valuesAlloc.get(2);
					if (totalAmtSourceCr == null)
						totalAmtSourceCr = Env.ZERO;
					totalAmtAcctCr = (BigDecimal) valuesAlloc.get(3);
					if (totalAmtAcctCr == null)
						totalAmtAcctCr = Env.ZERO;
					
					if (totalAmtSourceDr.signum() == 0 && totalAmtAcctDr.signum() == 0)
					{
						allocateSource = allocateSource.add(totalAmtSourceCr);
						allocateAccounted = allocateAccounted.add(totalAmtAcctCr);
					}
					else if (totalAmtSourceCr.signum() == 0 && totalAmtAcctCr.signum() == 0)
					{
						allocateSource = allocateSource.add(totalAmtSourceDr);
						allocateAccounted = allocateAccounted.add(totalAmtAcctDr);
					}
					else
					{
						if (totalAmtAcctDr.compareTo(totalAmtAcctCr) > 0)
						{
							allocateSource = allocateSource.add(totalAmtSourceDr);
							allocateAccounted = allocateAccounted.add(totalAmtAcctDr).subtract(totalAmtAcctCr);
							currencyAdjustment = currencyAdjustment.add(totalAmtAcctCr);
						}
						else
						{
							allocateSource = allocateSource.add(totalAmtSourceCr);
							allocateAccounted = allocateAccounted.add(totalAmtAcctCr).subtract(totalAmtAcctDr);
							currencyAdjustment = currencyAdjustment.add(totalAmtAcctDr);
						}
					}
				}
				
				sql = new StringBuilder()
					.append("SELECT SUM(AmtSourceDr), SUM(AmtAcctDr), SUM(AmtSourceCr), SUM(AmtAcctCr)")
					.append(" FROM Fact_Acct ")
					.append("WHERE AD_Table_ID=? AND Record_ID=?")	//	allocation
					.append(" AND C_AcctSchema_ID=?")
					.append(" AND PostingType='A'")
					.append(" AND (Account_ID=? OR Account_ID=? OR Account_ID=?)")
					.append(" AND Description LIKE 'Payment%'")
					.append(" AND Line_ID IN (SELECT C_AllocationLine_ID FROM C_AllocationLine WHERE C_AllocationHdr_ID=? AND C_Payment_ID=?)");
				
				// For Allocation
				valuesAlloc = DB.getSQLValueObjectsEx(getTrxName(), sql.toString(),
						MAllocationHdr.Table_ID, alloc.get_ID(), as.getC_AcctSchema_ID(), 
						gain.getAccount_ID(), loss.getAccount_ID(), as.getCurrencyBalancing_Acct().getAccount_ID(),
						alloc.get_ID(), payment.getC_Payment_ID());
				if (valuesAlloc != null && valuesAlloc.size() >= 4) {
					totalAmtSourceDr = (BigDecimal) valuesAlloc.get(0);
					if (totalAmtSourceDr == null)
						totalAmtSourceDr = Env.ZERO;
					totalAmtAcctDr = (BigDecimal) valuesAlloc.get(1);
					if (totalAmtAcctDr == null)
						totalAmtAcctDr = Env.ZERO;
					totalAmtSourceCr = (BigDecimal) valuesAlloc.get(2);
					if (totalAmtSourceCr == null)
						totalAmtSourceCr = Env.ZERO;
					totalAmtAcctCr = (BigDecimal) valuesAlloc.get(3);
					if (totalAmtAcctCr == null)
						totalAmtAcctCr = Env.ZERO;
					
					allocateSource = allocateSource.subtract(totalAmtSourceDr).subtract(totalAmtSourceCr).add(currencyAdjustment);
					if (as.isCurrencyBalancing() && as.getC_Currency_ID() != payment.getC_Currency_ID())
						;
					else
						allocateAccounted = allocateAccounted.add(currencyAdjustment);
				}
			}
			
			htAllocPaySource.put(payment.getC_Payment_ID(), allocateSource);
			htAllocPayAccounted.put(payment.getC_Payment_ID(), allocateAccounted);
		}
		
		for (MPayment payment : payList)
		{
			BigDecimal paySource = htPaySource.get(payment.getC_Payment_ID());
			if (paySource == null)
				paySource = Env.ZERO;
			BigDecimal payAccounted = htPayAccounted.get(payment.getC_Payment_ID());
			if (payAccounted == null)
				payAccounted = Env.ZERO;
			BigDecimal allocPaySource = htAllocPaySource.get(payment.getC_Payment_ID());
			if (allocPaySource == null)
				allocPaySource = Env.ZERO;
			BigDecimal allocPayAccounted = htAllocPayAccounted.get(payment.getC_Payment_ID());
			if (allocPayAccounted == null)
				allocPayAccounted = Env.ZERO;
			
			StringBuilder description = new StringBuilder("Payment=(").append(getC_Currency_ID()).append(")").append(paySource).append("/").append(payAccounted)
					.append(" - Allocation=(").append(getC_Currency_ID()).append(")").append(allocPaySource).append("/").append(allocPayAccounted);
			if (log.isLoggable(Level.FINE)) log.fine(description.toString());
			BigDecimal acctDifference = null;
			if (allocPaySource.abs().compareTo(paySource.abs()) == 0)
			{
				acctDifference = allocPayAccounted.abs().subtract(payAccounted.abs());	//	gain is negative
				StringBuilder d2 = new StringBuilder("(full) = ").append(acctDifference);
				if (log.isLoggable(Level.FINE)) log.fine(d2.toString());
				description.append(" - ").append(d2);
			}
			
			if (acctDifference == null || acctDifference.signum() == 0)
			{
				log.fine("No Difference");
				continue;
			}
			
			//
			Integer C_AllocationLine_ID = htPayAllocLine.get(payment.getC_Payment_ID());
			if ((payment.isReceipt() && payment.getPayAmt().signum() >= 0) || (!payment.isReceipt() && payment.getPayAmt().signum() < 0))
			{
				FactLine fl = fact.createLine (null, htPayAcct.get(payment.getC_Payment_ID()), as.getC_Currency_ID(), acctDifference.negate());
				fl.setDescription(description.toString());
				fl.setLine_ID(C_AllocationLine_ID == null ? 0 : C_AllocationLine_ID);
				if (!fact.isAcctBalanced())
				{
					if (as.isCurrencyBalancing() && as.getC_Currency_ID() != payment.getC_Currency_ID())
						fl = fact.createLine (null, as.getCurrencyBalancing_Acct(), as.getC_Currency_ID(), acctDifference);
					else
						fl = fact.createLine (null, loss, gain,as.getC_Currency_ID(), acctDifference);
					fl.setDescription(description.toString());
					fl.setLine_ID(C_AllocationLine_ID == null ? 0 : C_AllocationLine_ID);
				}
			}
			else
			{
				FactLine fl = fact.createLine (null, htPayAcct.get(payment.getC_Payment_ID()), as.getC_Currency_ID(), acctDifference);
				fl.setDescription(description.toString());
				fl.setLine_ID(C_AllocationLine_ID == null ? 0 : C_AllocationLine_ID);
				if (!fact.isAcctBalanced())
				{
					if (as.isCurrencyBalancing() && as.getC_Currency_ID() != payment.getC_Currency_ID())
						fl = fact.createLine (null, as.getCurrencyBalancing_Acct(), as.getC_Currency_ID(), acctDifference.negate());
					else 
						fl = fact.createLine (null, loss, gain, as.getC_Currency_ID(), acctDifference.negate());	
					fl.setDescription(description.toString());
					fl.setLine_ID(C_AllocationLine_ID == null ? 0 : C_AllocationLine_ID);
				}
			}
		}
		return null;
	}
	
	/**
	 * Balance Accounting
	 * @param as accounting schema
	 * @param fact
	 * @return fact line
	 */
	private FactLine balanceAccounting(MAcctSchema as, Fact fact)
	{
		FactLine line = null;
		//JPIERE-0052 Need not Check unbalanced. because JPiere need Calculate realised gain and loss this method always.
		//if (!fact.isAcctBalanced()) avoid log warning when unbalanced.
		//{
			MAccount gain = MAccount.get(as.getCtx(), as.getAcctSchemaDefault().getRealizedGain_Acct());
			MAccount loss = MAccount.get(as.getCtx(), as.getAcctSchemaDefault().getRealizedLoss_Acct());

			BigDecimal totalAmtAcctDr = Env.ZERO;
			BigDecimal totalAmtAcctCr = Env.ZERO;
			for (FactLine factLine : fact.getLines())
			{
				totalAmtAcctDr = totalAmtAcctDr.add(factLine.getAmtAcctDr());
				totalAmtAcctCr = totalAmtAcctCr.add(factLine.getAmtAcctCr());
			}
			
			BigDecimal acctDifference = totalAmtAcctDr.subtract(totalAmtAcctCr);
			if(acctDifference.compareTo(Env.ZERO) != 0)
			{
				if (as.isCurrencyBalancing() && acctDifference.abs().compareTo(TOLERANCE) < 0)
					line = fact.createLine (null, as.getCurrencyBalancing_Acct(), as.getC_Currency_ID(), acctDifference.negate());
				else
					line = fact.createLine(null, loss, gain, as.getC_Currency_ID(), acctDifference.negate());
			}
		//}
		return line;
	}
	
	/**
	 * Has Debit Receivables/Payables Trade Amount
	 * @param invoice
	 * @return true 
	 */
	private boolean hasDebitTradeAmt(MInvoice invoice)
	{
		return (invoice.isSOTrx() && invoice.getGrandTotal().signum() >= 0 && !invoice.isCreditMemo()) 
				|| (invoice.isSOTrx() && invoice.getGrandTotal().signum() < 0 && invoice.isCreditMemo())
				|| (!invoice.isSOTrx() && invoice.getGrandTotal().signum() >= 0 && invoice.isCreditMemo())
				|| (!invoice.isSOTrx() && invoice.getGrandTotal().signum() < 0 && !invoice.isCreditMemo());
	}

	/**
	 * JPIERE-0363
	 *
	 * @param contractAcct
	 * @param as
	 * @return
	 */
	private MAccount getReceivableAccount(MContractAcct contractAcct, MAcctSchema as)
	{
		if(contractAcct != null)
		{
			MContractBPAcct bpAcct = contractAcct.getContractBPAcct(as.getC_AcctSchema_ID(), false);
			if(bpAcct != null && bpAcct.getC_Receivable_Acct() > 0)
			{
				return MAccount.get(getCtx(),bpAcct.getC_Receivable_Acct());
			}
		}

		return MAccount.get(getCtx(), getValidCombination_ID(Doc.ACCTTYPE_C_Receivable, as));
	}

	/**
	 * JPIERE-0363
	 *
	 * @param contractAcct
	 * @param as
	 * @return
	 */
	private MAccount getPayableAccount(MContractAcct contractAcct, MAcctSchema as)
	{
		if(contractAcct != null)
		{
			MContractBPAcct bpAcct = contractAcct.getContractBPAcct(as.getC_AcctSchema_ID(), false);
			if(bpAcct != null && bpAcct.getV_Liability_Acct() > 0)
			{
				return MAccount.get(Env.getCtx(),bpAcct.getV_Liability_Acct());
			}
		}

		return MAccount.get(getCtx(), getValidCombination_ID(Doc.ACCTTYPE_V_Liability, as));
	}

	
	/**
	 * JPIERE-0052 - Set Payment Currency Rate.
	 * 
	 * Allocation should use Payment Rate basically.
	 * 
	 */
	private void setPaymentCurrencyRate(DocLine_AllocationJP docLine, MPayment payment)
	{
		if(docLine == null || payment == null)
			return ;
		
		//	Get Invoice Currency Conversion Rate
		int C_ConversionType_ID = payment.getC_ConversionType_ID();
		docLine.setC_ConversionType_ID(C_ConversionType_ID);
		if (payment.isOverrideCurrencyRate())
		{
			docLine.setCurrencyRate(payment.getCurrencyRate());
			
		}else {
			
			BigDecimal rate = MConversionRate.getRate(payment.getC_Currency_ID(),getAcctSchema().getC_Currency_ID(),payment.getDateAcct(),C_ConversionType_ID,getAD_Client_ID(),payment.getAD_Org_ID());
			docLine.setCurrencyRate(rate);
		}
		
	}
	
	/**
	 * JPIERE-0052 - Set Invoice Currency Rate
	 * 
	 * Allocation should use Invoice Rate in case of Receivable & Payable
	 * 
	 */	
	private void setInvoiceCurrencyRate(DocLine_AllocationJP docLine, MInvoice invoice, MPayment payment)
	{
		if(docLine == null || invoice == null)
			return ;
		
		
		if(payment != null && MSysConfig.getBooleanValue("JP_APPLY_PAYMENT_RATE_TO_INVOICE", true, getAD_Client_ID()))
		{
			setPaymentCurrencyRate(docLine, payment);
			return ;
		}
		
		//	Get Invoice Currency Conversion Rate
		int C_ConversionType_ID = invoice.getC_ConversionType_ID();
		docLine.setC_ConversionType_ID(C_ConversionType_ID);
		if (invoice.isOverrideCurrencyRate())
		{
			docLine.setCurrencyRate(invoice.getCurrencyRate());
			
		}else {
			
			BigDecimal rate = MConversionRate.getRate(invoice.getC_Currency_ID(),getAcctSchema().getC_Currency_ID(),invoice.getDateAcct(),C_ConversionType_ID,getAD_Client_ID(),invoice.getAD_Org_ID());
			docLine.setCurrencyRate(rate);
		}
	}
	
	
	/**
	 * JPIERE-0052 & JPIERE-0363
	 * 
	 */
	private void setInvoiceInfo(FactLine fl, MInvoice invoice, boolean isSetOrgAndBP)
	{
		if(fl == null || invoice == null)
			return ;
		
		
		if(isSetOrgAndBP)
		{
			fl.setAD_Org_ID(invoice.getAD_Org_ID());
			fl.setAD_OrgTrx_ID(invoice.getAD_OrgTrx_ID());
			fl.setC_BPartner_ID(invoice.getC_BPartner_ID());//JPIERE-0026: Allocation between different Business Partners.
		}
		
		int JP_Order_ID = 0;
		if(invoice.getC_Order_ID() > 0)
		{
			JP_Order_ID = invoice.getC_Order_ID();
		}else if(invoice.getM_RMA_ID() > 0){
			int M_RMA_ID = invoice.getM_RMA_ID();
			MRMA rma = new MRMA (Env.getCtx(),M_RMA_ID, getTrxName());
			JP_Order_ID = rma.get_ValueAsInt("JP_Order_ID");
		}
		int JP_Contract_ID = invoice.get_ValueAsInt("JP_Contract_ID");
		int JP_ContractContent_ID = invoice.get_ValueAsInt("JP_ContractContent_ID");
		int JP_ContractProcPeriod_ID = invoice.get_ValueAsInt("JP_ContractProcPeriod_ID");

		if(JP_Contract_ID > 0)
			fl.set_ValueNoCheck("JP_Contract_ID", JP_Contract_ID);
		if(JP_ContractContent_ID > 0)
			fl.set_ValueNoCheck("JP_ContractContent_ID", JP_ContractContent_ID);
		if(JP_ContractProcPeriod_ID > 0)
			fl.set_ValueNoCheck("JP_ContractProcPeriod_ID", JP_ContractProcPeriod_ID);
		if(JP_Order_ID > 0)
			fl.set_ValueNoCheck("JP_Order_ID", JP_Order_ID);
		
		fl.set_ValueNoCheck("JP_Invoice_ID", invoice.getC_Invoice_ID());
	}
	
	/**
	 * JPIERE-0052 & JPIERE-0363 & JPIERE-0556 & JPIERE-0566
	 * 
	 */

	private void setPaymentInfo(FactLine fl, DocLine line, boolean isSetOrgAndBP)
	{	
		if(fl == null || line == null)
			return ;
		
		PO po = line.getPO();
		if(po instanceof I_C_AllocationLine)
		{
			I_C_AllocationLine aLine = (I_C_AllocationLine)po;
			if(aLine.getC_Payment_ID() != 0)
			{
				MPayment payment = new MPayment(Env.getCtx(),aLine.getC_Payment_ID(), null);
				setPaymentInfo(fl, payment, isSetOrgAndBP);
			}
		}
	}
	
	private void setPaymentInfo(FactLine fl, MPayment payment, boolean isSetOrgAndBP)
	{		
		if(fl == null || payment == null)
			return ;
		
		if(isSetOrgAndBP)
		{
			fl.setAD_Org_ID(payment.getAD_Org_ID());
			fl.setAD_OrgTrx_ID(payment.getAD_OrgTrx_ID());
			fl.setC_BPartner_ID(payment.getC_BPartner_ID());//JPIERE-0026: Allocation between different Business Partners.
		}
		
		fl.set_ValueNoCheck("JP_BankAccount_ID", payment.getC_BankAccount_ID());
		fl.set_ValueNoCheck("JP_Payment_ID", payment.getC_Payment_ID());
	}

	
}   //  Doc_Allocation


/**
 * 	Allocation Document Tax Handing
 *
 *  @author Jorg Janke
 *  @version $Id: Doc_Allocation.java,v 1.6 2006/07/30 00:53:33 jjanke Exp $
 */
class Doc_AllocationTax
{
	/**
	 * 	Allocation Tax Adjustment
	 *	@param DiscountAccount discount acct
	 *	@param DiscountAmt discount amt
	 *	@param WriteOffAccount write off acct
	 *	@param WriteOffAmt write off amt
	 */
	public Doc_AllocationTax (MAccount DiscountAccount, BigDecimal DiscountAmt,
		MAccount WriteOffAccount, BigDecimal WriteOffAmt, boolean isSOTrx)
	{
		m_DiscountAccount = DiscountAccount;
		m_DiscountAmt = DiscountAmt;
		m_WriteOffAccount = WriteOffAccount;
		m_WriteOffAmt = WriteOffAmt;
		m_IsSOTrx = isSOTrx;
	}	//	Doc_AllocationTax

	private static final CLogger	log = CLogger.getCLogger(Doc_AllocationTax.class);

	private MAccount			m_DiscountAccount;
	private BigDecimal 			m_DiscountAmt;
	private MAccount			m_WriteOffAccount;
	private BigDecimal 			m_WriteOffAmt;
	private boolean 			m_IsSOTrx;

	private ArrayList<MFactAcct>	m_facts  = new ArrayList<MFactAcct>();
	private int					m_totalIndex = 0;

	/**
	 * 	Add Invoice Fact Line
	 *	@param fact fact line
	 */
	public void addInvoiceFact (MFactAcct fact)
	{
		m_facts.add(fact);
	}	//	addInvoiceLine

	/**
	 * 	Get Line Count
	 *	@return number of lines
	 */
	public int getLineCount()
	{
		return m_facts.size();
	}	//	getLineCount

	/**
	 * 	Create Accounting Entries
	 *	@param as account schema
	 *	@param fact fact to add lines
	 *	@param line line
	 *	@return true if created
	 */
	public boolean createEntries (MAcctSchema as, Fact fact, DocLine line)
	{
		//	get total index (the Receivables/Liabilities line)
		BigDecimal total = Env.ZERO;
		for (int i = 0; i < m_facts.size(); i++)
		{
			MFactAcct factAcct = (MFactAcct)m_facts.get(i);
			if (   (factAcct.getAmtSourceDr().signum() > 0 && factAcct.getAmtSourceDr().compareTo(total) > 0)
				|| (factAcct.getAmtSourceDr().signum() < 0 && factAcct.getAmtSourceDr().compareTo(total) < 0))
			{
				total = factAcct.getAmtSourceDr();
				m_totalIndex = i;
			}
			if (   (factAcct.getAmtSourceCr().signum() > 0 && factAcct.getAmtSourceCr().compareTo(total) > 0)
				|| (factAcct.getAmtSourceCr().signum() < 0 && factAcct.getAmtSourceCr().compareTo(total) < 0))
			{
				total = factAcct.getAmtSourceCr();
				m_totalIndex = i;
			}
		}

		MFactAcct factAcct = (MFactAcct)m_facts.get(m_totalIndex);
		if (log.isLoggable(Level.INFO)) log.info ("Total Invoice = " + total + " - " +  factAcct);
		int precision = as.getStdPrecision();
		FactLine fl = null; //JPIERE-0052
		for (int i = 0; i < m_facts.size(); i++)
		{
			//	No Tax Line
			if (i == m_totalIndex)
				continue;

			factAcct = (MFactAcct)m_facts.get(i);
			if (log.isLoggable(Level.INFO)) log.info (i + ": " + factAcct);

			//	Create Tax Account
			MAccount taxAcct = factAcct.getMAccount();
			if (taxAcct == null || taxAcct.get_ID() == 0)
			{
				log.severe ("Tax Account not found/created");
				return false;
			}

			Doc doc = DocManager.getDocument(as, MInvoice.Table_ID, factAcct.getRecord_ID(), line.getPO().get_TrxName());
			MDocType dt = new MDocType(Env.getCtx(), (doc!=null)?doc.getC_DocType_ID():-1, line.getPO().get_TrxName());
			String docBaseType=(dt.getC_DocType_ID()>0)?dt.getDocBaseType():"";

//			Discount Amount
			if (m_DiscountAmt.signum() != 0)
			{
				//	Original Tax is DR - need to correct it CR
				if (Env.ZERO.compareTo(factAcct.getAmtSourceDr()) != 0)
				{
					BigDecimal amount = calcAmount(factAcct.getAmtSourceDr(),
						total, m_DiscountAmt, precision);

					if (amount.signum() != 0)
					{
						//for sales actions
						if (m_IsSOTrx) {
							if(docBaseType.equals(MDocType.DOCBASETYPE_ARCreditMemo)) {
								fl = fact.createLine (line, m_DiscountAccount,
										line.getC_Currency_ID(), amount.negate(), null);
								copyAcctInfo(fl, factAcct, false);//JPIERE-0052
								fl = fact.createLine (line, taxAcct,
										line.getC_Currency_ID(), null, amount.negate());
								copyAcctInfo(fl, factAcct, true);//JPIERE-0052
							}else {

								fl = fact.createLine (line, m_DiscountAccount,
										line.getC_Currency_ID(), amount, null);
								copyAcctInfo(fl, factAcct, false);//JPIERE-0052
								fl = fact.createLine (line, taxAcct,
										line.getC_Currency_ID(), null, amount);
								copyAcctInfo(fl, factAcct, true);//JPIERE-0052
							}
						} else {
						//for purchase actions
							if(docBaseType.equals(MDocType.DOCBASETYPE_APCreditMemo)) {
								fl = fact.createLine (line, m_DiscountAccount,
										line.getC_Currency_ID(), amount, null);
								copyAcctInfo(fl, factAcct, false);//JPIERE-0052
								fl = fact.createLine (line, taxAcct,
										line.getC_Currency_ID(), null, amount);
								copyAcctInfo(fl, factAcct, true);//JPIERE-0052
							} else {
								fl = fact.createLine (line, m_DiscountAccount,
										line.getC_Currency_ID(), amount.negate(), null);
								copyAcctInfo(fl, factAcct, false);//JPIERE-0052
								fl = fact.createLine (line, taxAcct,
										line.getC_Currency_ID(), null, amount.negate());
								copyAcctInfo(fl, factAcct, true);//JPIERE-0052
							}
						}
					}
				}
				//	Original Tax is CR - need to correct it DR
				else
				{
					BigDecimal amount = calcAmount(factAcct.getAmtSourceCr(),
						total, m_DiscountAmt, precision);
					if (amount.signum() != 0)
					{
//						for sales actions
						if (m_IsSOTrx) {
							if(docBaseType.equals(MDocType.DOCBASETYPE_ARCreditMemo)) {
								fl = fact.createLine (line, taxAcct,
										line.getC_Currency_ID(), amount.negate(), null);
								copyAcctInfo(fl, factAcct, true);//JPIERE-0052
								fl = fact.createLine (line, m_DiscountAccount,
										line.getC_Currency_ID(), null, amount.negate());
								copyAcctInfo(fl, factAcct, false);//JPIERE-0052
							}else {

								fl = fact.createLine (line, taxAcct,
										line.getC_Currency_ID(), amount, null);
								copyAcctInfo(fl, factAcct, true);//JPIERE-0052
								fl = fact.createLine (line, m_DiscountAccount,
										line.getC_Currency_ID(), null, amount);
								copyAcctInfo(fl, factAcct, false);//JPIERE-0052
							}

						} else {
							if(docBaseType.equals(MDocType.DOCBASETYPE_APCreditMemo)) {
								fl = fact.createLine (line, taxAcct,
										line.getC_Currency_ID(), amount, null);
								copyAcctInfo(fl, factAcct, true);//JPIERE-0052
								fl = fact.createLine (line, m_DiscountAccount,
										line.getC_Currency_ID(), null, amount);
								copyAcctInfo(fl, factAcct, false);//JPIERE-0052
							}else {

								fl = fact.createLine (line, taxAcct,
										line.getC_Currency_ID(), amount.negate(), null);
								copyAcctInfo(fl, factAcct, true);//JPIERE-0052
								fl = fact.createLine (line, m_DiscountAccount,
										line.getC_Currency_ID(), null, amount.negate());
								copyAcctInfo(fl, factAcct, false);//JPIERE-0052
							}

						}
					}
				}
			}	//	Discount

			//	WriteOff Amount
			if (m_WriteOffAmt.signum() != 0)
			{
				//	Original Tax is DR - need to correct it CR
				if (Env.ZERO.compareTo(factAcct.getAmtSourceDr()) != 0)
				{
					BigDecimal amount = calcAmount(factAcct.getAmtSourceDr(),
						total, m_WriteOffAmt, precision);
					if (amount.signum() != 0)
					{
						if (m_IsSOTrx) {
							if(docBaseType.equals(MDocType.DOCBASETYPE_ARCreditMemo)) {
								fl = fact.createLine (line, m_WriteOffAccount,
										line.getC_Currency_ID(), amount.negate(), null);
								copyAcctInfo(fl, factAcct, false);//JPIERE-0052
								fl = fact.createLine (line, taxAcct,
										line.getC_Currency_ID(), null, amount.negate());
								copyAcctInfo(fl, factAcct, true);//JPIERE-0052
							} else {
								fl = fact.createLine (line, m_WriteOffAccount,
										line.getC_Currency_ID(), amount, null);
								copyAcctInfo(fl, factAcct, false);//JPIERE-0052
								fl = fact.createLine (line, taxAcct,
										line.getC_Currency_ID(), null, amount);
								copyAcctInfo(fl, factAcct, true);//JPIERE-0052
							}
						} else {
							if(docBaseType.equals(MDocType.DOCBASETYPE_APCreditMemo)) {
								fl = fact.createLine (line, m_WriteOffAccount,
										line.getC_Currency_ID(), amount, null);
								copyAcctInfo(fl, factAcct, false);//JPIERE-0052
								fl = fact.createLine (line, taxAcct,
										line.getC_Currency_ID(), null, amount);
								copyAcctInfo(fl, factAcct, true);//JPIERE-0052
							} else {
								fl = fact.createLine (line, m_WriteOffAccount,
										line.getC_Currency_ID(), amount.negate(), null);
								copyAcctInfo(fl, factAcct, false);//JPIERE-0052
								fl = fact.createLine (line, taxAcct,
										line.getC_Currency_ID(), null, amount.negate());
								copyAcctInfo(fl, factAcct, true);//JPIERE-0052
							}
						}
					}
				}
				//	Original Tax is CR - need to correct it DR
				else
				{
					BigDecimal amount = calcAmount(factAcct.getAmtSourceCr(),
						total, m_WriteOffAmt, precision);
					if (amount.signum() != 0)
					{
						if(m_IsSOTrx) {
							if(docBaseType.equals(MDocType.DOCBASETYPE_ARCreditMemo)) {
								fl = fact.createLine (line, taxAcct,
										line.getC_Currency_ID(), amount.negate(), null);
								copyAcctInfo(fl, factAcct, true);//JPIERE-0052
								fl = fact.createLine (line, m_WriteOffAccount,
										line.getC_Currency_ID(), null, amount.negate());
								copyAcctInfo(fl, factAcct, false);//JPIERE-0052
							} else {
								fl = fact.createLine (line, taxAcct,
										line.getC_Currency_ID(), amount, null);
								copyAcctInfo(fl, factAcct, true);//JPIERE-0052
								fl = fact.createLine (line, m_WriteOffAccount,
										line.getC_Currency_ID(), null, amount);
								copyAcctInfo(fl, factAcct, false);//JPIERE-0052
							}
						} else {
							if(docBaseType.equals(MDocType.DOCBASETYPE_APCreditMemo)) {
								fl = fact.createLine (line, taxAcct,
										line.getC_Currency_ID(), amount, null);
								copyAcctInfo(fl, factAcct, true);//JPIERE-0052
								fl = fact.createLine (line, m_WriteOffAccount,
										line.getC_Currency_ID(), null, amount);
								copyAcctInfo(fl, factAcct, false);//JPIERE-0052
							} else {
								fl = fact.createLine (line, taxAcct,
										line.getC_Currency_ID(), amount.negate(), null);
								copyAcctInfo(fl, factAcct, true);//JPIERE-0052
								fl = fact.createLine (line, m_WriteOffAccount,
										line.getC_Currency_ID(), null, amount.negate());
								copyAcctInfo(fl, factAcct, false);//JPIERE-0052
							}
						}
					}
				}
			}	//	WriteOff
		}	//	for all lines
		return true;
	}	//	createEntries

	/**
	 * 	Calc Amount tax / total * amt
	 *	@param tax tax
	 *	@param total total
	 *	@param amt reduction amt
	 *	@param precision precision
	 *	@return tax / total * amt
	 */
	private BigDecimal calcAmount (BigDecimal tax, BigDecimal total, BigDecimal amt, int precision)
	{
		if (log.isLoggable(Level.FINE)) log.fine("Amt=" + amt + " - Total=" + total + ", Tax=" + tax);
		if (tax.signum() == 0
			|| total.signum() == 0
			|| amt.signum() == 0)
			return Env.ZERO;
		//
		BigDecimal multiplier = tax.divide(total, 10, RoundingMode.HALF_UP);
		BigDecimal retValue = multiplier.multiply(amt);
		if (retValue.scale() > precision)
			retValue = retValue.setScale(precision, RoundingMode.HALF_UP);
		if (log.isLoggable(Level.FINE)) log.fine(retValue + " (Mult=" + multiplier + "(Prec=" + precision + ")");
		return retValue;
	}	//	calcAmount

	private void copyAcctInfo(FactLine fl, MFactAcct factAcct,boolean isCopyTaxInfo)
	{
		fl.setAD_Org_ID(factAcct.getAD_Org_ID());
		fl.setAD_OrgTrx_ID(factAcct.getAD_OrgTrx_ID());
		String TaxCorrectionType = Msg.getElement(Env.getCtx(), "TaxCorrectionType");
		fl.setDescription(TaxCorrectionType + " : "+fl.getDescription());
		fl.setC_BPartner_ID(factAcct.getC_BPartner_ID());
		fl.setC_Project_ID(factAcct.getC_Project_ID());
		fl.setC_ProjectPhase_ID(factAcct.getC_ProjectPhase_ID());
		fl.setC_ProjectTask_ID(factAcct.getC_ProjectTask_ID());
		fl.setC_Campaign_ID(factAcct.getC_Campaign_ID());
		fl.setC_Activity_ID(factAcct.getC_Activity_ID());
		fl.setC_SalesRegion_ID(factAcct.getC_SalesRegion_ID());
		fl.setC_LocFrom_ID(factAcct.getC_LocFrom_ID());
		fl.setC_LocTo_ID(factAcct.getC_LocTo_ID());
		fl.setM_Locator_ID(factAcct.getM_Locator_ID());
		fl.setUser1_ID(factAcct.getUser1_ID());
		fl.setUser2_ID(factAcct.getUser2_ID());
		fl.setUserElement1_ID(factAcct.getUserElement1_ID());
		fl.setUserElement2_ID(factAcct.getUserElement2_ID());
		fl.set_ValueNoCheck("JP_TaxBaseAmt", Env.ZERO);
		fl.set_ValueNoCheck("JP_TaxAmt", Env.ZERO);
		
		if(isCopyTaxInfo)
		{
			fl.setC_Tax_ID(factAcct.getC_Tax_ID());
			
			String JP_SOPOType = factAcct.get_ValueAsString("JP_SOPOType");
			if(!Util.isEmpty(JP_SOPOType))
			{
				fl.set_ValueNoCheck("JP_SOPOType", JP_SOPOType);
			}
			
			boolean IsQualifiedInvoiceIssuerJP = factAcct.get_ValueAsBoolean("IsQualifiedInvoiceIssuerJP");
			fl.set_ValueNoCheck("IsQualifiedInvoiceIssuerJP", IsQualifiedInvoiceIssuerJP);
			
			String JP_RegisteredNumberOfQII = factAcct.get_ValueAsString("JP_RegisteredNumberOfQII");
			if(!Util.isEmpty(JP_RegisteredNumberOfQII))
			{
				fl.set_ValueNoCheck("JP_RegisteredNumberOfQII", JP_RegisteredNumberOfQII);
			}
		}
		
		int JP_Contract_ID = factAcct.get_ValueAsInt("JP_Contract_ID");
		if(JP_Contract_ID > 0)
			fl.set_ValueNoCheck("JP_Contract_ID", JP_Contract_ID);
		
		int JP_ContractContent_ID = factAcct.get_ValueAsInt("JP_ContractContent_ID");
		if(JP_ContractContent_ID > 0)
			fl.set_ValueNoCheck("JP_ContractContent_ID", JP_ContractContent_ID);
		
		int JP_ContractProcPeriod_ID = factAcct.get_ValueAsInt("JP_ContractProcPeriod_ID");
		if(JP_ContractProcPeriod_ID > 0)
			fl.set_ValueNoCheck("JP_ContractProcPeriod_ID", JP_ContractProcPeriod_ID);
		
		int JP_Order_ID = factAcct.get_ValueAsInt("JP_Order_ID");
		if(JP_Order_ID > 0)
			fl.set_ValueNoCheck("JP_Order_ID", JP_Order_ID);
		
		int JP_Invoice_ID = factAcct.get_ValueAsInt("JP_Invoice_ID");
		if(JP_Invoice_ID > 0)
			fl.set_ValueNoCheck("JP_Invoice_ID", JP_Invoice_ID);
		
		int JP_Payment_ID = factAcct.get_ValueAsInt("JP_Payment_ID");
		if(JP_Payment_ID > 0)
			fl.set_ValueNoCheck("JP_Payment_ID", JP_Payment_ID);
		
		int JP_BankAccount_ID = factAcct.get_ValueAsInt("JP_BankAccount_ID");
		if(JP_BankAccount_ID > 0)
			fl.set_ValueNoCheck("JP_BankAccount_ID", JP_BankAccount_ID);
	}
}	//	Doc_AllocationTax
