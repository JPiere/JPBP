/******************************************************************************
 * Product: JPiere Plugin Bank Statement Tax
 * Copyright (C) 2014 Hideaki Hagiwara(OSS ERP Solutions)
 *****************************************************************************/
package jpiere.base.plugin.org.compiere.acct;

import java.math.BigDecimal;

import org.compiere.acct.DocLine;
import org.compiere.acct.DocTax;
import org.compiere.model.MBankStatementLine;
import org.compiere.util.DB;
import org.compiere.util.Env;

/**
 *  Bank Statement Line
 *
 *  @author Hideaki Hagiwara
 *  @version  $Id: Doc_JPiereBankStatementTax.java,v 1.0 2014/08/20
 */
public class DocLine_JPiereBankStatementTax extends DocLine
{
	/**
	 *  Constructor
	 *  @param line statement line
	 *  @param doc header
	 */
	public DocLine_JPiereBankStatementTax (MBankStatementLine line, Doc_JPiereBankStatementTax doc)
	{
		super (line, doc);
		m_C_Payment_ID = line.getC_Payment_ID();
		m_IsReversal = line.isReversal();
		//
		m_StmtAmt = line.getStmtAmt();
		m_InterestAmt = line.getInterestAmt();
		m_TrxAmt = line.getTrxAmt();
		//
		setDateDoc(line.getValutaDate());
		setDateAcct(doc.getDateAcct());  // adaxa-pb use statement date
		setC_BPartner_ID(line.getC_BPartner_ID());
	}

	/** Reversal Flag			*/
	private boolean     m_IsReversal = false;
	/** Payment					*/
	private int         m_C_Payment_ID = 0;

	private BigDecimal  m_TrxAmt = Env.ZERO;
	private BigDecimal  m_StmtAmt = Env.ZERO;
	private BigDecimal  m_InterestAmt = Env.ZERO;

	/**
	 *  Get Payment
	 *  @return C_Paymnet_ID
	 */
	public int getC_Payment_ID()
	{
		return m_C_Payment_ID;
	}   //  getC_Payment_ID

	/**
	 * 	Get AD_Org_ID
	 * 	@param payment if true get Org from payment
	 *	@return org
	 */
	public int getAD_Org_ID (boolean payment)
	{
		if (payment && getC_Payment_ID() != 0)
		{
			String sql = "SELECT AD_Org_ID FROM C_Payment WHERE C_Payment_ID=?";
			int id = DB.getSQLValue(null, sql, getC_Payment_ID());
			if (id > 0)
				return id;
		}
		return super.getAD_Org_ID();
	}	//	getAD_Org_ID

	/**
	 *  Is Reversal
	 *  @return true if reversal
	 */
	public boolean isReversal()
	{
		return m_IsReversal;
	}   //  isReversal

	/**
	 *  Get Interest
	 *  @return InterestAmount
	 */
	public BigDecimal getInterestAmt()
	{
		return m_InterestAmt;
	}   //  getInterestAmt

	/**
	 *  Get Statement
	 *  @return Starement Amount
	 */
	public BigDecimal getStmtAmt()
	{
		return m_StmtAmt;
	}   //  getStrmtAmt

	/**
	 *  Get Transaction
	 *  @return transaction amount
	 */
	public BigDecimal getTrxAmt()
	{
		return m_TrxAmt;
	}   //  getTrxAmt


	/********************************************
	 * Modification DocLine_Bank
	 */
	private DocTax docTax = null;
	private BigDecimal taxBaseAmt =Env.ZERO;

	public void setDocTax(DocTax docTax){
		this.docTax = docTax;
	}

	public DocTax getDocTax(){
		return docTax;
	}

	public void setTaxBaseAmt(BigDecimal taxBaseAmt){
		this.taxBaseAmt = taxBaseAmt;
	}

	public BigDecimal getTaxBaseAmt(){
		return taxBaseAmt;
	}

}   //  DocLine_JPiereBankStatementTax
