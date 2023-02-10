/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2006 ComPiere, Inc. All Rights Reserved.                *
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
import org.compiere.model.MCurrency;
import org.compiere.model.MElementValue;
import org.compiere.model.MJournal;
import org.compiere.model.MJournalLine;
import org.compiere.model.MTax;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.base.IJPiereTaxProvider;
import jpiere.base.plugin.org.adempiere.model.JPiereTaxProvider;
import jpiere.base.plugin.util.JPiereUtil;

/**
 *  JPIERE-0544: Calculate Tax Amount automatically at GL Journal.
 *  JPIERE-0553: Qualified　Invoice　Issuer
 *  JPIERE-0556: Add column to the Journal For legal compliance.
 * 
 *  @author h.hagiwara
 */
public class Doc_GLJournalJP extends Doc
{
	/**
	 *  Constructor
	 * 	@param as accounting schema
	 * 	@param rs record
	 * 	@param trxName trx
	 */
	public Doc_GLJournalJP (MAcctSchema as, ResultSet rs, String trxName)
	{
		super(as, MJournal.class, rs, null, trxName);
	}	//	Doc_GL_Journal

	/** Posting Type				*/
	protected String			m_PostingType = null;
	protected int				m_C_AcctSchema_ID = 0;

	/** Contained Optional Tax Lines    */
	protected DocTax[]        m_taxes = null;
	/** Currency Precision				*/
	protected int				m_precision = -1;
	
	/**
	 *  Load Specific Document Details
	 *  @return error message or null
	 */
	protected String loadDocumentDetails ()
	{
		MJournal journal = (MJournal)getPO();
		m_PostingType = journal.getPostingType();
		m_C_AcctSchema_ID = journal.getC_AcctSchema_ID();

		//	Contained Objects
		m_taxes = loadTaxes();
		p_lines = loadLines(journal);
		if (log.isLoggable(Level.FINE)) log.fine("Lines=" + p_lines.length);
		return null;
	}   //  loadDocumentDetails

	/**
	 *	Load Invoice Taxes
	 *  @return DocTax Array
	 */
	private DocTax[] loadTaxes()
	{
		ArrayList<DocTax> list = new ArrayList<DocTax>();
		String sql = "SELECT it.C_Tax_ID, t.Name, t.Rate, it.TaxBaseAmt, it.TaxAmt, it.JP_SOPOType "
				+ "FROM C_Tax t, JP_GLJournalTax it "
				+ "WHERE t.C_Tax_ID=it.C_Tax_ID AND it.GL_Journal_ID=?";
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
				boolean salesTax = "S".equals(rs.getString(6));
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
	 *	Load Invoice Line
	 *	@param journal journal
	 *  @return DocLine Array
	 */
	protected DocLine[] loadLines(MJournal journal)
	{
		
		ArrayList<DocLine> list = new ArrayList<DocLine>();
		MJournalLine[] journalLines = journal.getLines(false);
		for (int i = 0; i < journalLines.length; i++)
		{
			MJournalLine journalLine = journalLines[i];
			DocLine docLine = new DocLine(journalLine, this);
			
			// -- Quantity
			docLine.setQty(journalLine.getQty(), false);
			
			//Summaraize Tax
			BigDecimal JP_TaxAmt = Env.ZERO;
			if(journalLine.get_Value("JP_TaxAmt") != null)
				JP_TaxAmt = (BigDecimal)journalLine.get_Value("JP_TaxAmt");
			
			int C_Tax_ID = docLine.getC_Tax_ID();
			String JP_SOPOType = journalLine.get_ValueAsString("JP_SOPOType");
			MTax tax = null;
			if(C_Tax_ID != 0)
				tax = MTax.get(getCtx(), C_Tax_ID);
			
			if (tax != null && !tax.isZeroTax() 
					&& ("S".equals(JP_SOPOType) || "P".equals(JP_SOPOType) ) 
					&& JP_TaxAmt.compareTo(Env.ZERO) != 0 )
			{					
				for (int t = 0; t < m_taxes.length; t++)
				{
					if (m_taxes[t].getC_Tax_ID() == C_Tax_ID)
					{
						if( ("S".equals(JP_SOPOType) && m_taxes[t].isSalesTax())
								|| ("P".equals(JP_SOPOType) && !m_taxes[t].isSalesTax()) )
						{
							m_taxes[t].addIncludedTax(JP_TaxAmt);
							break;
						}
					}
				}
			}

			//  --  Source Amounts
			docLine.setAmount (journalLine.getAmtSourceDr(), journalLine.getAmtSourceCr());
			
			//  --  Account
			MAccount account = journalLine.getAccount_Combi();
			docLine.setAccount (account);
			//	--	Organization of Line was set to Org of Account
			list.add(docLine);
			
			if (docLine.getC_Currency_ID() != getC_Currency_ID())
				setIsMultiCurrency(true);	
		}
		
		//	Convert to Array
		DocLine[] dls = new DocLine[list.size()];
		list.toArray(dls);
	
		return dls;
	}	//	loadLines


	/**************************************************************************
	 *  Get Source Currency Balance - subtracts line and tax amounts from total - no rounding
	 *  @return positive amount, if total invoice is bigger than lines
	 */
	public BigDecimal getBalance()
	{
		BigDecimal retValue = Env.ZERO;
		StringBuilder sb = new StringBuilder (" [");
		//  Lines
		for (int i = 0; i < p_lines.length; i++)
		{
			retValue = retValue.add(p_lines[i].getAmtSource());
			sb.append("+").append(p_lines[i].getAmtSource());
		}
		sb.append("]");
		//
		if (log.isLoggable(Level.FINE)) log.fine(toString() + " Balance=" + retValue + sb.toString());
		return retValue;
	}   //  getBalance

	/**
	 *  Create Facts (the accounting logic) for
	 *  GLJ.
	 *  (only for the accounting scheme, it was created)
	 *  <pre>
	 *      account     DR          CR
	 *  </pre>
	 *  @param as acct schema
	 *  @return Fact
	 */
	public ArrayList<Fact> createFacts (MAcctSchema as)
	{
		ArrayList<Fact> facts = new ArrayList<Fact>();
		//	Other Acct Schema
		if (as.getC_AcctSchema_ID() != m_C_AcctSchema_ID)
			return facts;

		//  create Fact Header
		Fact fact = new Fact (this, as, m_PostingType);

		//  GLJ
		if (getDocumentType().equals(DOCTYPE_GLJournal))
		{
			//  account     DR      CR
			DocLine docLine = null;
			int C_Tax_ID = 0;
			String JP_SOPOType = null;
			BigDecimal amtSourceDr = Env.ZERO;
			//BigDecimal amtSourceCr = Env.ZERO;
			BigDecimal JP_TaxBaseAmt = Env.ZERO;
			BigDecimal JP_TaxAmt = Env.ZERO;
			FactLine fLine = null;
			DocTax docTax = null;
			MElementValue  elementValue = null;//JPIERE-0556
			int C_BankAccount_ID = 0;//JPIERE-0556
			
			for (int i = 0; i < p_lines.length; i++)
			{
				docLine = p_lines[i];
				if(docLine.getPO().get_Value("JP_BankAccount_ID") != null)//JPIERE-0556
				{
					C_BankAccount_ID = ((Integer)docLine.getPO().get_Value("JP_BankAccount_ID")).intValue();
					
				}else if(docLine.getAccount() != null){ 
					
					elementValue = docLine.getAccount().getAccount();
					if(elementValue != null)
					{
						C_BankAccount_ID = elementValue.getC_BankAccount_ID();
					}else {
						C_BankAccount_ID = 0;
					}
					
				}else {
					elementValue = null;
					C_BankAccount_ID = 0;
				}
				C_Tax_ID = docLine.getC_Tax_ID();
				JP_SOPOType = docLine.getPO().get_ValueAsString("JP_SOPOType");
				if(docLine.getPO().get_Value("JP_TaxBaseAmt") != null)
					JP_TaxBaseAmt = (BigDecimal)docLine.getPO().get_Value("JP_TaxBaseAmt");
				else
					JP_TaxBaseAmt = Env.ZERO;
				
				if(docLine.getPO().get_Value("JP_TaxAmt") != null)
					JP_TaxAmt = (BigDecimal)docLine.getPO().get_Value("JP_TaxAmt");
				else
					JP_TaxAmt = Env.ZERO;
				
				amtSourceDr = docLine.getAmtSourceDr();
				//amtSourceCr = docLine.getAmtSourceCr();
				
				docTax = getDocTax(C_Tax_ID, JP_SOPOType);
				
				if(docTax != null && ("S".equals(JP_SOPOType) || "P".equals(JP_SOPOType) )
						&& JP_TaxBaseAmt.compareTo(Env.ZERO) !=0 && JP_TaxAmt.compareTo(Env.ZERO) != 0 )
				{
					
					if("S".equals(JP_SOPOType))
					{
						if(amtSourceDr.compareTo(Env.ZERO) == 0)
						{
							fLine = fact.createLine (docLine, docLine.getAccount(), getC_Currency_ID(), Env.ZERO, JP_TaxBaseAmt);
							setTaxInfo(fLine, C_Tax_ID, JP_SOPOType,JP_TaxBaseAmt, JP_TaxAmt);
							fLine.set_ValueNoCheck("JP_PriceActual" ,p_lines[i].getPO().get_Value("JP_PriceActual"));//JPIERE-0556
							if(C_BankAccount_ID > 0)
								fLine.set_ValueNoCheck("JP_BankAccount_ID" ,C_BankAccount_ID);//JPIERE-0556
							
							fLine = fact.createLine(docLine, docTax.getAccount(DocTax.ACCTTYPE_TaxDue, as), getC_Currency_ID(), Env.ZERO, JP_TaxAmt);
							setTaxInfo(fLine, C_Tax_ID, JP_SOPOType,JP_TaxBaseAmt, JP_TaxAmt);
							fLine.set_ValueNoCheck("JP_PriceActual" ,Env.ZERO);//JPIERE-0556
							fLine.set_ValueNoCheck("Qty" ,Env.ZERO);//JPIERE-0556
							fLine.set_ValueNoCheck("C_UOM_ID", null);//JPIERE-0556

						}else {
							
							fLine = fact.createLine (docLine,docLine.getAccount (), getC_Currency_ID(), JP_TaxBaseAmt.negate(), Env.ZERO);
							setTaxInfo(fLine, C_Tax_ID, JP_SOPOType,JP_TaxBaseAmt, JP_TaxAmt);
							fLine.set_ValueNoCheck("JP_PriceActual" ,p_lines[i].getPO().get_Value("JP_PriceActual"));//JPIERE-0556
							if(C_BankAccount_ID > 0)
								fLine.set_ValueNoCheck("JP_BankAccount_ID" ,C_BankAccount_ID);//JPIERE-0556

							fLine = fact.createLine(docLine, docTax.getAccount(DocTax.ACCTTYPE_TaxDue, as), getC_Currency_ID(), JP_TaxAmt.negate(), Env.ZERO);
							setTaxInfo(fLine, C_Tax_ID, JP_SOPOType,JP_TaxBaseAmt, JP_TaxAmt);
							fLine.set_ValueNoCheck("JP_PriceActual" ,Env.ZERO);//JPIERE-0556
							fLine.set_ValueNoCheck("Qty" ,Env.ZERO);//JPIERE-0556
							fLine.set_ValueNoCheck("C_UOM_ID", null);//JPIERE-0556
						}
						
					}else if("P".equals(JP_SOPOType)) {
						
						boolean isSalesTax = MTax.get(docLine.getC_Tax_ID()).isSalesTax();
						
						if(amtSourceDr.compareTo(Env.ZERO) == 0)
						{
							fLine = fact.createLine (docLine, docLine.getAccount(), getC_Currency_ID(), Env.ZERO, JP_TaxBaseAmt.negate());
							setTaxInfo(fLine, C_Tax_ID, JP_SOPOType,JP_TaxBaseAmt, JP_TaxAmt);
							fLine.set_ValueNoCheck("JP_PriceActual" ,p_lines[i].getPO().get_Value("JP_PriceActual"));//JPIERE-0556
							if(C_BankAccount_ID > 0)
								fLine.set_ValueNoCheck("JP_BankAccount_ID" ,C_BankAccount_ID);//JPIERE-0556

							fLine = fact.createLine(docLine, docTax.getAccount(isSalesTax ? DocTax.ACCTTYPE_TaxExpense : DocTax.ACCTTYPE_TaxCredit, as), getC_Currency_ID(), Env.ZERO, JP_TaxAmt.negate());
							setTaxInfo(fLine, C_Tax_ID, JP_SOPOType,JP_TaxBaseAmt, JP_TaxAmt);
							fLine.set_ValueNoCheck("JP_PriceActual" ,Env.ZERO);//JPIERE-0556
							fLine.set_ValueNoCheck("Qty" ,Env.ZERO);//JPIERE-0556
							fLine.set_ValueNoCheck("C_UOM_ID", null);//JPIERE-0556
							
						}else {
							
							fLine = fact.createLine (docLine,docLine.getAccount (), getC_Currency_ID(), JP_TaxBaseAmt, Env.ZERO);
							setTaxInfo(fLine, C_Tax_ID, JP_SOPOType,JP_TaxBaseAmt, JP_TaxAmt);
							fLine.set_ValueNoCheck("JP_PriceActual" ,p_lines[i].getPO().get_Value("JP_PriceActual"));//JPIERE-0556
							if(C_BankAccount_ID > 0)
								fLine.set_ValueNoCheck("JP_BankAccount_ID" ,C_BankAccount_ID);//JPIERE-0556

							fLine = fact.createLine(docLine, docTax.getAccount(isSalesTax ? DocTax.ACCTTYPE_TaxExpense : DocTax.ACCTTYPE_TaxCredit, as),	getC_Currency_ID(), JP_TaxAmt, Env.ZERO);
							setTaxInfo(fLine, C_Tax_ID, JP_SOPOType,JP_TaxBaseAmt, JP_TaxAmt);
							fLine.set_ValueNoCheck("JP_PriceActual" ,Env.ZERO);//JPIERE-0556
							fLine.set_ValueNoCheck("Qty" ,Env.ZERO);//JPIERE-0556
							fLine.set_ValueNoCheck("C_UOM_ID", null);//JPIERE-0556

						}
					}
					
				}else {
					
					fLine = fact.createLine (docLine, docLine.getAccount (), docLine.getC_Currency_ID(), docLine.getAmtSourceDr (), docLine.getAmtSourceCr ());
					setTaxInfo(fLine, C_Tax_ID, JP_SOPOType,JP_TaxBaseAmt, JP_TaxAmt);
					fLine.set_ValueNoCheck("JP_PriceActual" ,p_lines[i].getPO().get_Value("JP_PriceActual"));//JPIERE-0556
					if(C_BankAccount_ID > 0)
						fLine.set_ValueNoCheck("JP_BankAccount_ID" ,C_BankAccount_ID);//JPIERE-0556
					
				}
				
			}	//	for all lines
		}
		else
		{
			p_Error = "DocumentType unknown: " + getDocumentType();
			log.log(Level.SEVERE, p_Error);
			fact = null;
		}
		
		//Adjustment of Document Level Tax 
		for (int i = 0; i < m_taxes.length; i++)
		{
			if(!MTax.get(m_taxes[i].getC_Tax_ID()).isDocumentLevel())
				continue;
			
			if(m_taxes[i].isIncludedTaxDifference())
			{	
				
				BigDecimal differenceAmt = m_taxes[i].getIncludedTaxDifference();
				if(m_taxes[i].isSalesTax())
				{
					FactLine fLine = fact.createLine(null, m_taxes[i].getAccount(DocTax.ACCTTYPE_TaxDue, as), getC_Currency_ID(), null, differenceAmt);
					setTaxInfo(fLine, m_taxes[i].getC_Tax_ID(), "S", differenceAmt.negate(), differenceAmt);
					
					AdjustTaxInfo[] adjustTaxInfos = getAdjustTaxInfo(m_taxes[i], "S");
					for(AdjustTaxInfo adjustTaxInfo : adjustTaxInfos)
					{
						fLine = fact.createLine (null, adjustTaxInfo.getAccount(), getC_Currency_ID(), adjustTaxInfo.getAdjustTaxAmt(), null);
						setTaxInfo(fLine, m_taxes[i].getC_Tax_ID(), "S", adjustTaxInfo.getAdjustTaxBaseAmt(), adjustTaxInfo.getAdjustTaxAmt());
					}
					
				}else {
	
					boolean isSalesTax = MTax.get(m_taxes[i].getC_Tax_ID()).isSalesTax();
					FactLine fLine = fact.createLine(null, m_taxes[i].getAccount( isSalesTax ? DocTax.ACCTTYPE_TaxExpense : DocTax.ACCTTYPE_TaxCredit, as), getC_Currency_ID(),  differenceAmt, null);
					setTaxInfo(fLine, m_taxes[i].getC_Tax_ID(), "P", differenceAmt.negate(), differenceAmt);
					
					AdjustTaxInfo[] adjustTaxInfos = getAdjustTaxInfo(m_taxes[i], "P");
					for(AdjustTaxInfo adjustTaxInfo : adjustTaxInfos)
					{
						fLine = fact.createLine (null, adjustTaxInfo.getAccount(), getC_Currency_ID(), null, adjustTaxInfo.getAdjustTaxAmt());
						setTaxInfo(fLine, m_taxes[i].getC_Tax_ID(), "P", adjustTaxInfo.getAdjustTaxBaseAmt(), adjustTaxInfo.getAdjustTaxAmt());
					}
					
				}			
			}
		}//for
		
		//
		facts.add(fact);
		return facts;
	}   //  createFact
	
	private DocTax getDocTax(int C_Tax_ID, String JP_SOPOType)
	{
		for (int i = 0; i < m_taxes.length; i++)
		{
			if(m_taxes[i].getC_Tax_ID() == C_Tax_ID
					&&( (m_taxes[i].isSalesTax() && "S".equals(JP_SOPOType)) || (!m_taxes[i].isSalesTax() && "P".equals(JP_SOPOType)) ) )
			{
				return m_taxes[i];
			}
		}
		return null;
	}
	
	private void setTaxInfo(FactLine fLine, int C_Tax_ID, String JP_SOPOType, BigDecimal JP_TaxBaseAmt, BigDecimal JP_TaxAmt)
	{
		if (fLine != null)
		{
			if(C_Tax_ID != 0)
				fLine.setC_Tax_ID(C_Tax_ID);
			
			if(!Util.isEmpty(JP_SOPOType))
				fLine.set_ValueNoCheck("JP_SOPOType", JP_SOPOType);
			
			if(JP_TaxBaseAmt == null)
				fLine.set_ValueNoCheck("JP_TaxBaseAmt", Env.ZERO);
			else
				fLine.set_ValueNoCheck("JP_TaxBaseAmt", JP_TaxBaseAmt);
			
			if(JP_TaxAmt == null)
				fLine.set_ValueNoCheck("JP_TaxAmt", Env.ZERO);
			else
				fLine.set_ValueNoCheck("JP_TaxAmt", JP_TaxAmt);
			
			int C_BPartner_ID = fLine.getC_BPartner_ID();
			if(C_BPartner_ID > 0 && "P".equals(JP_SOPOType))
			{
				MBPartner bp = MBPartner.get(getCtx(), C_BPartner_ID);
				boolean IsQualifiedInvoiceIssuerJP = bp.get_ValueAsBoolean("IsQualifiedInvoiceIssuerJP");
				
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
		}
	}
	

	private AdjustTaxInfo[] getAdjustTaxInfo(DocTax tax, String JP_SOPOType)
	{
		ArrayList<AdjustTaxInfo> adjustTaxInfoList = new ArrayList<AdjustTaxInfo> ();
		for (int i = 0; i < p_lines.length; i++)
		{
			if(JP_SOPOType.equals(p_lines[i].getPO().get_ValueAsString("JP_SOPOType")) && p_lines[i].getC_Tax_ID() == tax.getC_Tax_ID())
			{		
				AdjustTaxInfo adjustTaxInfo = null;
				for(AdjustTaxInfo ati : adjustTaxInfoList)
				{
					if(p_lines[i].getAccount().getAccount_ID() == ati.getAccount().getAccount_ID())
					{
						adjustTaxInfo = ati;
						break;
					}
				}
				
				if(adjustTaxInfo == null)
				{
					AdjustTaxInfo ati = new AdjustTaxInfo(p_lines[i], tax,JP_SOPOType );
					ati.addJP_TaxBaseAmt((BigDecimal)p_lines[i].getPO().get_Value("JP_TaxBaseAmt"));
					ati.addJP_TaxAmt((BigDecimal)p_lines[i].getPO().get_Value("JP_TaxAmt"));
					adjustTaxInfoList.add(ati);
				}else {
					adjustTaxInfo.addJP_TaxBaseAmt((BigDecimal)p_lines[i].getPO().get_Value("JP_TaxBaseAmt"));
					adjustTaxInfo.addJP_TaxAmt((BigDecimal)p_lines[i].getPO().get_Value("JP_TaxAmt"));
				}
			}
		}
		
		BigDecimal accumulate_JP_TaxBaseAmt = Env.ZERO;
		BigDecimal accumulate_JP_TaxAmt = Env.ZERO;
		for(AdjustTaxInfo ati : adjustTaxInfoList)
		{
			ati.reCalculateTax();
			accumulate_JP_TaxBaseAmt = accumulate_JP_TaxBaseAmt.add(ati.getReCalculateJP_TaxBaseAmt());
			accumulate_JP_TaxAmt = accumulate_JP_TaxAmt.add(ati.getReCalculateJP_TaxAmt());
		}
		
		
		//In case of remains, adjust the Account that have most big Tax Base amount.
		if(accumulate_JP_TaxBaseAmt.compareTo(tax.getTaxBaseAmt()) != 0 || accumulate_JP_TaxAmt.compareTo(tax.getAmount()) != 0)
		{
			BigDecimal diff_JP_TaxBaseAmt = tax.getTaxBaseAmt().subtract(accumulate_JP_TaxBaseAmt);
			BigDecimal diff_JP_TaxAmt = tax.getAmount().subtract(accumulate_JP_TaxAmt);
		
			AdjustTaxInfo maxAdjustTaxInfo = null;
			BigDecimal maxJP_TaxBaseAmtABS = Env.ZERO;
			BigDecimal tempJP_TaxBaseAmtABS = Env.ZERO;
			for (AdjustTaxInfo ati : adjustTaxInfoList)
			{
				if(maxAdjustTaxInfo == null)
				{
					maxAdjustTaxInfo = ati;
					maxJP_TaxBaseAmtABS = ati.getJP_TaxBaseAmt().abs();
					
				}else {
					
					tempJP_TaxBaseAmtABS = ati.getJP_TaxBaseAmt().abs();
					if(tempJP_TaxBaseAmtABS.compareTo(maxJP_TaxBaseAmtABS) > 0)
					{
						maxAdjustTaxInfo = ati;
						maxJP_TaxBaseAmtABS = tempJP_TaxBaseAmtABS;
					}
				}
			}//for
			
			maxAdjustTaxInfo.addRecalculateJP_TaxBaseAmt(diff_JP_TaxBaseAmt);
			maxAdjustTaxInfo.addRecalculateJP_TaxAmt(diff_JP_TaxAmt);
		}
		
		//	Return Array
		AdjustTaxInfo[] adjustTaxInfos = new AdjustTaxInfo[adjustTaxInfoList.size()];
		adjustTaxInfoList.toArray(adjustTaxInfos);
		
		return adjustTaxInfos;
	}
	
	class AdjustTaxInfo
	{
		private DocLine docLine = null;
		private DocTax docTax = null;
		private String JP_SOPOType;

		private BigDecimal JP_TaxBaseAmt = Env.ZERO;
		private BigDecimal JP_TaxAmt = Env.ZERO;
		
		private BigDecimal reCalculate_TaxBaseAmt = Env.ZERO;
		private BigDecimal reCalculate_TaxAmt = Env.ZERO;
		
		public AdjustTaxInfo(DocLine docLine, DocTax docTax, String JP_SOPOType)
		{
			this.docLine = docLine;
			this.docTax = docTax;
			this.JP_SOPOType = JP_SOPOType;

		}
		
		public void addJP_TaxBaseAmt(BigDecimal JP_TaxBaseAmt)
		{
			this.JP_TaxBaseAmt = this.JP_TaxBaseAmt.add(JP_TaxBaseAmt);
		}
		
		public void addJP_TaxAmt(BigDecimal JP_TaxAmt)
		{
			this.JP_TaxAmt = this.JP_TaxAmt.add(JP_TaxAmt);
		}
		
		public void addRecalculateJP_TaxBaseAmt(BigDecimal JP_TaxBaseAmt)
		{
			this.reCalculate_TaxBaseAmt = this.reCalculate_TaxBaseAmt.add(JP_TaxBaseAmt);
		}
		
		public void addRecalculateJP_TaxAmt(BigDecimal JP_TaxAmt)
		{
			this.reCalculate_TaxAmt = this.reCalculate_TaxAmt.add(JP_TaxAmt);
		}
		
		public MAccount getAccount()
		{
			return docLine.getAccount();
		}
		
		public BigDecimal getJP_TaxBaseAmt()
		{
			return JP_TaxBaseAmt;
		}
		
		public BigDecimal getJP_TaxAmt()
		{
			return JP_TaxAmt;
		}
		
		
		public BigDecimal getReCalculateJP_TaxBaseAmt()
		{
			return reCalculate_TaxBaseAmt;
		}
		
		public BigDecimal getReCalculateJP_TaxAmt()
		{
			return reCalculate_TaxAmt;
		}
		
		public BigDecimal getAdjustTaxBaseAmt()
		{
			BigDecimal diff = JP_TaxBaseAmt.subtract(reCalculate_TaxBaseAmt);
			return diff.negate();
		}
		
		public BigDecimal getAdjustTaxAmt()
		{
			BigDecimal diff = JP_TaxAmt.subtract(reCalculate_TaxAmt);
			return diff.negate();
		}
		
		public void reCalculateTax()
		{
			MTax m_tax = MTax.get(docTax.getC_Tax_ID());
			BigDecimal IncludeTaxAmt = JP_TaxBaseAmt.add(JP_TaxAmt);
			
			IJPiereTaxProvider taxCalculater = JPiereUtil.getJPiereTaxProvider(m_tax);
			if(taxCalculater != null)
			{
				reCalculate_TaxAmt = taxCalculater.calculateTax(m_tax, IncludeTaxAmt, true
						, MCurrency.getStdPrecision(Env.getCtx(), docLine.getC_Currency_ID())
						, JPiereTaxProvider.getRoundingMode(docLine.getC_BPartner_ID(), JP_SOPOType == "S"? true : false, m_tax.getC_TaxProvider()));
			}else{
				reCalculate_TaxAmt = m_tax.calculateTax(IncludeTaxAmt, true, MCurrency.getStdPrecision(Env.getCtx(), docLine.getC_Currency_ID()));
			}
			
			reCalculate_TaxBaseAmt = IncludeTaxAmt.subtract(reCalculate_TaxAmt);
		}
	}
	
}   //  Doc_GLJournal

