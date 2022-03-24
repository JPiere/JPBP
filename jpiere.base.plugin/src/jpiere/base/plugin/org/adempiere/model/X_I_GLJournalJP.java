/******************************************************************************
 * Product: iDempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2012 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY, without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program, if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
/** Generated Model - DO NOT CHANGE */
package jpiere.base.plugin.org.adempiere.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;
import org.compiere.model.*;
import org.compiere.util.Env;

/** Generated Model for I_GLJournalJP
 *  @author iDempiere (generated) 
 *  @version Release 9 - $Id$ */
@org.adempiere.base.Model(table="I_GLJournalJP")
public class X_I_GLJournalJP extends PO implements I_I_GLJournalJP, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20220324L;

    /** Standard Constructor */
    public X_I_GLJournalJP (Properties ctx, int I_GLJournalJP_ID, String trxName)
    {
      super (ctx, I_GLJournalJP_ID, trxName);
      /** if (I_GLJournalJP_ID == 0)
        {
			setAmtAcctCr (Env.ZERO);
			setAmtAcctDr (Env.ZERO);
			setAmtSourceCr (Env.ZERO);
			setAmtSourceDr (Env.ZERO);
			setI_GLJournalJP_ID (0);
        } */
    }

    /** Standard Constructor */
    public X_I_GLJournalJP (Properties ctx, int I_GLJournalJP_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, I_GLJournalJP_ID, trxName, virtualColumns);
      /** if (I_GLJournalJP_ID == 0)
        {
			setAmtAcctCr (Env.ZERO);
			setAmtAcctDr (Env.ZERO);
			setAmtSourceCr (Env.ZERO);
			setAmtSourceDr (Env.ZERO);
			setI_GLJournalJP_ID (0);
        } */
    }

    /** Load Constructor */
    public X_I_GLJournalJP (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 3 - Client - Org 
      */
    protected int get_AccessLevel()
    {
      return accessLevel.intValue();
    }

    /** Load Meta Data */
    protected POInfo initPO (Properties ctx)
    {
      POInfo poi = POInfo.getPOInfo (ctx, Table_ID, get_TrxName());
      return poi;
    }

    public String toString()
    {
      StringBuilder sb = new StringBuilder ("X_I_GLJournalJP[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set Trx Organization.
		@param AD_OrgTrx_ID Performing or initiating organization
	*/
	public void setAD_OrgTrx_ID (int AD_OrgTrx_ID)
	{
		if (AD_OrgTrx_ID < 1)
			set_Value (COLUMNNAME_AD_OrgTrx_ID, null);
		else
			set_Value (COLUMNNAME_AD_OrgTrx_ID, Integer.valueOf(AD_OrgTrx_ID));
	}

	/** Get Trx Organization.
		@return Performing or initiating organization
	  */
	public int getAD_OrgTrx_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_OrgTrx_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_A_Asset getA_Asset() throws RuntimeException
	{
		return (org.compiere.model.I_A_Asset)MTable.get(getCtx(), org.compiere.model.I_A_Asset.Table_ID)
			.getPO(getA_Asset_ID(), get_TrxName());
	}

	/** Set Asset.
		@param A_Asset_ID Asset used internally or by customers
	*/
	public void setA_Asset_ID (int A_Asset_ID)
	{
		if (A_Asset_ID < 1)
			set_Value (COLUMNNAME_A_Asset_ID, null);
		else
			set_Value (COLUMNNAME_A_Asset_ID, Integer.valueOf(A_Asset_ID));
	}

	/** Get Asset.
		@return Asset used internally or by customers
	  */
	public int getA_Asset_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_A_Asset_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_ElementValue getAccount() throws RuntimeException
	{
		return (org.compiere.model.I_C_ElementValue)MTable.get(getCtx(), org.compiere.model.I_C_ElementValue.Table_ID)
			.getPO(getAccount_ID(), get_TrxName());
	}

	/** Set Account.
		@param Account_ID Account used
	*/
	public void setAccount_ID (int Account_ID)
	{
		if (Account_ID < 1)
			set_Value (COLUMNNAME_Account_ID, null);
		else
			set_Value (COLUMNNAME_Account_ID, Integer.valueOf(Account_ID));
	}

	/** Get Account.
		@return Account used
	  */
	public int getAccount_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Account_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Accounted Credit.
		@param AmtAcctCr Accounted Credit Amount
	*/
	public void setAmtAcctCr (BigDecimal AmtAcctCr)
	{
		set_Value (COLUMNNAME_AmtAcctCr, AmtAcctCr);
	}

	/** Get Accounted Credit.
		@return Accounted Credit Amount
	  */
	public BigDecimal getAmtAcctCr()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_AmtAcctCr);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Accounted Debit.
		@param AmtAcctDr Accounted Debit Amount
	*/
	public void setAmtAcctDr (BigDecimal AmtAcctDr)
	{
		set_Value (COLUMNNAME_AmtAcctDr, AmtAcctDr);
	}

	/** Get Accounted Debit.
		@return Accounted Debit Amount
	  */
	public BigDecimal getAmtAcctDr()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_AmtAcctDr);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Source Credit.
		@param AmtSourceCr Source Credit Amount
	*/
	public void setAmtSourceCr (BigDecimal AmtSourceCr)
	{
		set_Value (COLUMNNAME_AmtSourceCr, AmtSourceCr);
	}

	/** Get Source Credit.
		@return Source Credit Amount
	  */
	public BigDecimal getAmtSourceCr()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_AmtSourceCr);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Source Debit.
		@param AmtSourceDr Source Debit Amount
	*/
	public void setAmtSourceDr (BigDecimal AmtSourceDr)
	{
		set_Value (COLUMNNAME_AmtSourceDr, AmtSourceDr);
	}

	/** Get Source Debit.
		@return Source Debit Amount
	  */
	public BigDecimal getAmtSourceDr()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_AmtSourceDr);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	public org.compiere.model.I_C_AcctSchema getC_AcctSchema() throws RuntimeException
	{
		return (org.compiere.model.I_C_AcctSchema)MTable.get(getCtx(), org.compiere.model.I_C_AcctSchema.Table_ID)
			.getPO(getC_AcctSchema_ID(), get_TrxName());
	}

	/** Set Accounting Schema.
		@param C_AcctSchema_ID Rules for accounting
	*/
	public void setC_AcctSchema_ID (int C_AcctSchema_ID)
	{
		if (C_AcctSchema_ID < 1)
			set_Value (COLUMNNAME_C_AcctSchema_ID, null);
		else
			set_Value (COLUMNNAME_C_AcctSchema_ID, Integer.valueOf(C_AcctSchema_ID));
	}

	/** Get Accounting Schema.
		@return Rules for accounting
	  */
	public int getC_AcctSchema_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_AcctSchema_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Activity getC_Activity() throws RuntimeException
	{
		return (org.compiere.model.I_C_Activity)MTable.get(getCtx(), org.compiere.model.I_C_Activity.Table_ID)
			.getPO(getC_Activity_ID(), get_TrxName());
	}

	/** Set Activity.
		@param C_Activity_ID Business Activity
	*/
	public void setC_Activity_ID (int C_Activity_ID)
	{
		if (C_Activity_ID < 1)
			set_Value (COLUMNNAME_C_Activity_ID, null);
		else
			set_Value (COLUMNNAME_C_Activity_ID, Integer.valueOf(C_Activity_ID));
	}

	/** Get Activity.
		@return Business Activity
	  */
	public int getC_Activity_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Activity_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_BPartner getC_BPartner() throws RuntimeException
	{
		return (org.compiere.model.I_C_BPartner)MTable.get(getCtx(), org.compiere.model.I_C_BPartner.Table_ID)
			.getPO(getC_BPartner_ID(), get_TrxName());
	}

	/** Set Business Partner.
		@param C_BPartner_ID Identifies a Business Partner
	*/
	public void setC_BPartner_ID (int C_BPartner_ID)
	{
		if (C_BPartner_ID < 1)
			set_Value (COLUMNNAME_C_BPartner_ID, null);
		else
			set_Value (COLUMNNAME_C_BPartner_ID, Integer.valueOf(C_BPartner_ID));
	}

	/** Get Business Partner.
		@return Identifies a Business Partner
	  */
	public int getC_BPartner_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_BPartner_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Campaign getC_Campaign() throws RuntimeException
	{
		return (org.compiere.model.I_C_Campaign)MTable.get(getCtx(), org.compiere.model.I_C_Campaign.Table_ID)
			.getPO(getC_Campaign_ID(), get_TrxName());
	}

	/** Set Campaign.
		@param C_Campaign_ID Marketing Campaign
	*/
	public void setC_Campaign_ID (int C_Campaign_ID)
	{
		if (C_Campaign_ID < 1)
			set_Value (COLUMNNAME_C_Campaign_ID, null);
		else
			set_Value (COLUMNNAME_C_Campaign_ID, Integer.valueOf(C_Campaign_ID));
	}

	/** Get Campaign.
		@return Marketing Campaign
	  */
	public int getC_Campaign_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Campaign_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_ConversionType getC_ConversionType() throws RuntimeException
	{
		return (org.compiere.model.I_C_ConversionType)MTable.get(getCtx(), org.compiere.model.I_C_ConversionType.Table_ID)
			.getPO(getC_ConversionType_ID(), get_TrxName());
	}

	/** Set Currency Type.
		@param C_ConversionType_ID Currency Conversion Rate Type
	*/
	public void setC_ConversionType_ID (int C_ConversionType_ID)
	{
		if (C_ConversionType_ID < 1)
			set_Value (COLUMNNAME_C_ConversionType_ID, null);
		else
			set_Value (COLUMNNAME_C_ConversionType_ID, Integer.valueOf(C_ConversionType_ID));
	}

	/** Get Currency Type.
		@return Currency Conversion Rate Type
	  */
	public int getC_ConversionType_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_ConversionType_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Currency getC_Currency() throws RuntimeException
	{
		return (org.compiere.model.I_C_Currency)MTable.get(getCtx(), org.compiere.model.I_C_Currency.Table_ID)
			.getPO(getC_Currency_ID(), get_TrxName());
	}

	/** Set Currency.
		@param C_Currency_ID The Currency for this record
	*/
	public void setC_Currency_ID (int C_Currency_ID)
	{
		if (C_Currency_ID < 1)
			set_Value (COLUMNNAME_C_Currency_ID, null);
		else
			set_Value (COLUMNNAME_C_Currency_ID, Integer.valueOf(C_Currency_ID));
	}

	/** Get Currency.
		@return The Currency for this record
	  */
	public int getC_Currency_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Currency_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_DocType getC_DocType() throws RuntimeException
	{
		return (org.compiere.model.I_C_DocType)MTable.get(getCtx(), org.compiere.model.I_C_DocType.Table_ID)
			.getPO(getC_DocType_ID(), get_TrxName());
	}

	/** Set Document Type.
		@param C_DocType_ID Document type or rules
	*/
	public void setC_DocType_ID (int C_DocType_ID)
	{
		if (C_DocType_ID < 0)
			set_Value (COLUMNNAME_C_DocType_ID, null);
		else
			set_Value (COLUMNNAME_C_DocType_ID, Integer.valueOf(C_DocType_ID));
	}

	/** Get Document Type.
		@return Document type or rules
	  */
	public int getC_DocType_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_DocType_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Location getC_LocFrom() throws RuntimeException
	{
		return (org.compiere.model.I_C_Location)MTable.get(getCtx(), org.compiere.model.I_C_Location.Table_ID)
			.getPO(getC_LocFrom_ID(), get_TrxName());
	}

	/** Set Location From.
		@param C_LocFrom_ID Location that inventory was moved from
	*/
	public void setC_LocFrom_ID (int C_LocFrom_ID)
	{
		if (C_LocFrom_ID < 1)
			set_Value (COLUMNNAME_C_LocFrom_ID, null);
		else
			set_Value (COLUMNNAME_C_LocFrom_ID, Integer.valueOf(C_LocFrom_ID));
	}

	/** Get Location From.
		@return Location that inventory was moved from
	  */
	public int getC_LocFrom_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_LocFrom_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Location getC_LocTo() throws RuntimeException
	{
		return (org.compiere.model.I_C_Location)MTable.get(getCtx(), org.compiere.model.I_C_Location.Table_ID)
			.getPO(getC_LocTo_ID(), get_TrxName());
	}

	/** Set Location To.
		@param C_LocTo_ID Location that inventory was moved to
	*/
	public void setC_LocTo_ID (int C_LocTo_ID)
	{
		if (C_LocTo_ID < 1)
			set_Value (COLUMNNAME_C_LocTo_ID, null);
		else
			set_Value (COLUMNNAME_C_LocTo_ID, Integer.valueOf(C_LocTo_ID));
	}

	/** Get Location To.
		@return Location that inventory was moved to
	  */
	public int getC_LocTo_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_LocTo_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Period getC_Period() throws RuntimeException
	{
		return (org.compiere.model.I_C_Period)MTable.get(getCtx(), org.compiere.model.I_C_Period.Table_ID)
			.getPO(getC_Period_ID(), get_TrxName());
	}

	/** Set Period.
		@param C_Period_ID Period of the Calendar
	*/
	public void setC_Period_ID (int C_Period_ID)
	{
		if (C_Period_ID < 1)
			set_Value (COLUMNNAME_C_Period_ID, null);
		else
			set_Value (COLUMNNAME_C_Period_ID, Integer.valueOf(C_Period_ID));
	}

	/** Get Period.
		@return Period of the Calendar
	  */
	public int getC_Period_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Period_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_ProjectPhase getC_ProjectPhase() throws RuntimeException
	{
		return (org.compiere.model.I_C_ProjectPhase)MTable.get(getCtx(), org.compiere.model.I_C_ProjectPhase.Table_ID)
			.getPO(getC_ProjectPhase_ID(), get_TrxName());
	}

	/** Set Project Phase.
		@param C_ProjectPhase_ID Phase of a Project
	*/
	public void setC_ProjectPhase_ID (int C_ProjectPhase_ID)
	{
		if (C_ProjectPhase_ID < 1)
			set_Value (COLUMNNAME_C_ProjectPhase_ID, null);
		else
			set_Value (COLUMNNAME_C_ProjectPhase_ID, Integer.valueOf(C_ProjectPhase_ID));
	}

	/** Get Project Phase.
		@return Phase of a Project
	  */
	public int getC_ProjectPhase_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_ProjectPhase_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_ProjectTask getC_ProjectTask() throws RuntimeException
	{
		return (org.compiere.model.I_C_ProjectTask)MTable.get(getCtx(), org.compiere.model.I_C_ProjectTask.Table_ID)
			.getPO(getC_ProjectTask_ID(), get_TrxName());
	}

	/** Set Project Task.
		@param C_ProjectTask_ID Actual Project Task in a Phase
	*/
	public void setC_ProjectTask_ID (int C_ProjectTask_ID)
	{
		if (C_ProjectTask_ID < 1)
			set_Value (COLUMNNAME_C_ProjectTask_ID, null);
		else
			set_Value (COLUMNNAME_C_ProjectTask_ID, Integer.valueOf(C_ProjectTask_ID));
	}

	/** Get Project Task.
		@return Actual Project Task in a Phase
	  */
	public int getC_ProjectTask_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_ProjectTask_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Project getC_Project() throws RuntimeException
	{
		return (org.compiere.model.I_C_Project)MTable.get(getCtx(), org.compiere.model.I_C_Project.Table_ID)
			.getPO(getC_Project_ID(), get_TrxName());
	}

	/** Set Project.
		@param C_Project_ID Financial Project
	*/
	public void setC_Project_ID (int C_Project_ID)
	{
		if (C_Project_ID < 1)
			set_Value (COLUMNNAME_C_Project_ID, null);
		else
			set_Value (COLUMNNAME_C_Project_ID, Integer.valueOf(C_Project_ID));
	}

	/** Get Project.
		@return Financial Project
	  */
	public int getC_Project_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Project_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_SalesRegion getC_SalesRegion() throws RuntimeException
	{
		return (org.compiere.model.I_C_SalesRegion)MTable.get(getCtx(), org.compiere.model.I_C_SalesRegion.Table_ID)
			.getPO(getC_SalesRegion_ID(), get_TrxName());
	}

	/** Set Sales Region.
		@param C_SalesRegion_ID Sales coverage region
	*/
	public void setC_SalesRegion_ID (int C_SalesRegion_ID)
	{
		if (C_SalesRegion_ID < 1)
			set_Value (COLUMNNAME_C_SalesRegion_ID, null);
		else
			set_Value (COLUMNNAME_C_SalesRegion_ID, Integer.valueOf(C_SalesRegion_ID));
	}

	/** Get Sales Region.
		@return Sales coverage region
	  */
	public int getC_SalesRegion_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_SalesRegion_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_SubAcct getC_SubAcct() throws RuntimeException
	{
		return (org.compiere.model.I_C_SubAcct)MTable.get(getCtx(), org.compiere.model.I_C_SubAcct.Table_ID)
			.getPO(getC_SubAcct_ID(), get_TrxName());
	}

	/** Set Sub Account.
		@param C_SubAcct_ID Sub account for Element Value
	*/
	public void setC_SubAcct_ID (int C_SubAcct_ID)
	{
		if (C_SubAcct_ID < 1)
			set_Value (COLUMNNAME_C_SubAcct_ID, null);
		else
			set_Value (COLUMNNAME_C_SubAcct_ID, Integer.valueOf(C_SubAcct_ID));
	}

	/** Get Sub Account.
		@return Sub account for Element Value
	  */
	public int getC_SubAcct_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_SubAcct_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Tax getC_Tax() throws RuntimeException
	{
		return (org.compiere.model.I_C_Tax)MTable.get(getCtx(), org.compiere.model.I_C_Tax.Table_ID)
			.getPO(getC_Tax_ID(), get_TrxName());
	}

	/** Set Tax.
		@param C_Tax_ID Tax identifier
	*/
	public void setC_Tax_ID (int C_Tax_ID)
	{
		if (C_Tax_ID < 1)
			set_Value (COLUMNNAME_C_Tax_ID, null);
		else
			set_Value (COLUMNNAME_C_Tax_ID, Integer.valueOf(C_Tax_ID));
	}

	/** Get Tax.
		@return Tax identifier
	  */
	public int getC_Tax_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Tax_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_UOM getC_UOM() throws RuntimeException
	{
		return (org.compiere.model.I_C_UOM)MTable.get(getCtx(), org.compiere.model.I_C_UOM.Table_ID)
			.getPO(getC_UOM_ID(), get_TrxName());
	}

	/** Set UOM.
		@param C_UOM_ID Unit of Measure
	*/
	public void setC_UOM_ID (int C_UOM_ID)
	{
		if (C_UOM_ID < 1)
			set_Value (COLUMNNAME_C_UOM_ID, null);
		else
			set_Value (COLUMNNAME_C_UOM_ID, Integer.valueOf(C_UOM_ID));
	}

	/** Get UOM.
		@return Unit of Measure
	  */
	public int getC_UOM_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_UOM_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Rate.
		@param CurrencyRate Currency Conversion Rate
	*/
	public void setCurrencyRate (BigDecimal CurrencyRate)
	{
		set_Value (COLUMNNAME_CurrencyRate, CurrencyRate);
	}

	/** Get Rate.
		@return Currency Conversion Rate
	  */
	public BigDecimal getCurrencyRate()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_CurrencyRate);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Account Date.
		@param DateAcct Accounting Date
	*/
	public void setDateAcct (Timestamp DateAcct)
	{
		set_Value (COLUMNNAME_DateAcct, DateAcct);
	}

	/** Get Account Date.
		@return Accounting Date
	  */
	public Timestamp getDateAcct()
	{
		return (Timestamp)get_Value(COLUMNNAME_DateAcct);
	}

	/** Set Transaction Date.
		@param DateTrx Transaction Date
	*/
	public void setDateTrx (Timestamp DateTrx)
	{
		set_Value (COLUMNNAME_DateTrx, DateTrx);
	}

	/** Get Transaction Date.
		@return Transaction Date
	  */
	public Timestamp getDateTrx()
	{
		return (Timestamp)get_Value(COLUMNNAME_DateTrx);
	}

	/** Set Document No.
		@param DocumentNo Document sequence number of the document
	*/
	public void setDocumentNo (String DocumentNo)
	{
		set_Value (COLUMNNAME_DocumentNo, DocumentNo);
	}

	/** Get Document No.
		@return Document sequence number of the document
	  */
	public String getDocumentNo()
	{
		return (String)get_Value(COLUMNNAME_DocumentNo);
	}

	public org.compiere.model.I_GL_Budget getGL_Budget() throws RuntimeException
	{
		return (org.compiere.model.I_GL_Budget)MTable.get(getCtx(), org.compiere.model.I_GL_Budget.Table_ID)
			.getPO(getGL_Budget_ID(), get_TrxName());
	}

	/** Set Budget.
		@param GL_Budget_ID General Ledger Budget
	*/
	public void setGL_Budget_ID (int GL_Budget_ID)
	{
		if (GL_Budget_ID < 1)
			set_Value (COLUMNNAME_GL_Budget_ID, null);
		else
			set_Value (COLUMNNAME_GL_Budget_ID, Integer.valueOf(GL_Budget_ID));
	}

	/** Get Budget.
		@return General Ledger Budget
	  */
	public int getGL_Budget_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_GL_Budget_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_GL_Category getGL_Category() throws RuntimeException
	{
		return (org.compiere.model.I_GL_Category)MTable.get(getCtx(), org.compiere.model.I_GL_Category.Table_ID)
			.getPO(getGL_Category_ID(), get_TrxName());
	}

	/** Set GL Category.
		@param GL_Category_ID General Ledger Category
	*/
	public void setGL_Category_ID (int GL_Category_ID)
	{
		if (GL_Category_ID < 1)
			set_Value (COLUMNNAME_GL_Category_ID, null);
		else
			set_Value (COLUMNNAME_GL_Category_ID, Integer.valueOf(GL_Category_ID));
	}

	/** Get GL Category.
		@return General Ledger Category
	  */
	public int getGL_Category_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_GL_Category_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_GL_JournalLine getGL_JournalLine() throws RuntimeException
	{
		return (org.compiere.model.I_GL_JournalLine)MTable.get(getCtx(), org.compiere.model.I_GL_JournalLine.Table_ID)
			.getPO(getGL_JournalLine_ID(), get_TrxName());
	}

	/** Set Journal Line.
		@param GL_JournalLine_ID General Ledger Journal Line
	*/
	public void setGL_JournalLine_ID (int GL_JournalLine_ID)
	{
		if (GL_JournalLine_ID < 1)
			set_Value (COLUMNNAME_GL_JournalLine_ID, null);
		else
			set_Value (COLUMNNAME_GL_JournalLine_ID, Integer.valueOf(GL_JournalLine_ID));
	}

	/** Get Journal Line.
		@return General Ledger Journal Line
	  */
	public int getGL_JournalLine_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_GL_JournalLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_GL_Journal getGL_Journal() throws RuntimeException
	{
		return (org.compiere.model.I_GL_Journal)MTable.get(getCtx(), org.compiere.model.I_GL_Journal.Table_ID)
			.getPO(getGL_Journal_ID(), get_TrxName());
	}

	/** Set Journal.
		@param GL_Journal_ID General Ledger Journal
	*/
	public void setGL_Journal_ID (int GL_Journal_ID)
	{
		if (GL_Journal_ID < 1)
			set_Value (COLUMNNAME_GL_Journal_ID, null);
		else
			set_Value (COLUMNNAME_GL_Journal_ID, Integer.valueOf(GL_Journal_ID));
	}

	/** Get Journal.
		@return General Ledger Journal
	  */
	public int getGL_Journal_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_GL_Journal_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set ISO Currency Code.
		@param ISO_Code Three letter ISO 4217 Code of the Currency
	*/
	public void setISO_Code (String ISO_Code)
	{
		set_Value (COLUMNNAME_ISO_Code, ISO_Code);
	}

	/** Get ISO Currency Code.
		@return Three letter ISO 4217 Code of the Currency
	  */
	public String getISO_Code()
	{
		return (String)get_Value(COLUMNNAME_ISO_Code);
	}

	/** Set Import Error Message.
		@param I_ErrorMsg Messages generated from import process
	*/
	public void setI_ErrorMsg (String I_ErrorMsg)
	{
		set_Value (COLUMNNAME_I_ErrorMsg, I_ErrorMsg);
	}

	/** Get Import Error Message.
		@return Messages generated from import process
	  */
	public String getI_ErrorMsg()
	{
		return (String)get_Value(COLUMNNAME_I_ErrorMsg);
	}

	/** Set I_GLJournalJP.
		@param I_GLJournalJP_ID I_GLJournalJP
	*/
	public void setI_GLJournalJP_ID (int I_GLJournalJP_ID)
	{
		if (I_GLJournalJP_ID < 1)
			set_ValueNoCheck (COLUMNNAME_I_GLJournalJP_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_I_GLJournalJP_ID, Integer.valueOf(I_GLJournalJP_ID));
	}

	/** Get I_GLJournalJP.
		@return I_GLJournalJP	  */
	public int getI_GLJournalJP_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_I_GLJournalJP_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set I_GLJournalJP_UU.
		@param I_GLJournalJP_UU I_GLJournalJP_UU
	*/
	public void setI_GLJournalJP_UU (String I_GLJournalJP_UU)
	{
		set_ValueNoCheck (COLUMNNAME_I_GLJournalJP_UU, I_GLJournalJP_UU);
	}

	/** Get I_GLJournalJP_UU.
		@return I_GLJournalJP_UU	  */
	public String getI_GLJournalJP_UU()
	{
		return (String)get_Value(COLUMNNAME_I_GLJournalJP_UU);
	}

	/** Set Imported.
		@param I_IsImported Has this import been processed
	*/
	public void setI_IsImported (boolean I_IsImported)
	{
		set_Value (COLUMNNAME_I_IsImported, Boolean.valueOf(I_IsImported));
	}

	/** Get Imported.
		@return Has this import been processed
	  */
	public boolean isI_IsImported()
	{
		Object oo = get_Value(COLUMNNAME_I_IsImported);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Accounting Schema(Name).
		@param JP_AcctSchema_Name Accounting Schema(Name)
	*/
	public void setJP_AcctSchema_Name (String JP_AcctSchema_Name)
	{
		set_Value (COLUMNNAME_JP_AcctSchema_Name, JP_AcctSchema_Name);
	}

	/** Get Accounting Schema(Name).
		@return Accounting Schema(Name)	  */
	public String getJP_AcctSchema_Name()
	{
		return (String)get_Value(COLUMNNAME_JP_AcctSchema_Name);
	}

	/** Set Activity(Search Key).
		@param JP_Activity_Value Activity(Search Key)
	*/
	public void setJP_Activity_Value (String JP_Activity_Value)
	{
		set_Value (COLUMNNAME_JP_Activity_Value, JP_Activity_Value);
	}

	/** Get Activity(Search Key).
		@return Activity(Search Key)	  */
	public String getJP_Activity_Value()
	{
		return (String)get_Value(COLUMNNAME_JP_Activity_Value);
	}

	/** Set Asset(Search Key).
		@param JP_Asset_Value Asset(Search Key)
	*/
	public void setJP_Asset_Value (String JP_Asset_Value)
	{
		set_Value (COLUMNNAME_JP_Asset_Value, JP_Asset_Value);
	}

	/** Get Asset(Search Key).
		@return Asset(Search Key)	  */
	public String getJP_Asset_Value()
	{
		return (String)get_Value(COLUMNNAME_JP_Asset_Value);
	}

	/** Set Business Partner(Search Key).
		@param JP_BPartner_Value Business Partner(Search Key)
	*/
	public void setJP_BPartner_Value (String JP_BPartner_Value)
	{
		set_Value (COLUMNNAME_JP_BPartner_Value, JP_BPartner_Value);
	}

	/** Get Business Partner(Search Key).
		@return Business Partner(Search Key)	  */
	public String getJP_BPartner_Value()
	{
		return (String)get_Value(COLUMNNAME_JP_BPartner_Value);
	}

	/** Set Campaign(Search Key).
		@param JP_Campaign_Value Campaign(Search Key)
	*/
	public void setJP_Campaign_Value (String JP_Campaign_Value)
	{
		set_Value (COLUMNNAME_JP_Campaign_Value, JP_Campaign_Value);
	}

	/** Get Campaign(Search Key).
		@return Campaign(Search Key)	  */
	public String getJP_Campaign_Value()
	{
		return (String)get_Value(COLUMNNAME_JP_Campaign_Value);
	}

	/** Set Contract Content(Document No).
		@param JP_ContractContent_DocNo Contract Content(Document No)
	*/
	public void setJP_ContractContent_DocNo (String JP_ContractContent_DocNo)
	{
		set_Value (COLUMNNAME_JP_ContractContent_DocNo, JP_ContractContent_DocNo);
	}

	/** Get Contract Content(Document No).
		@return Contract Content(Document No)	  */
	public String getJP_ContractContent_DocNo()
	{
		return (String)get_Value(COLUMNNAME_JP_ContractContent_DocNo);
	}

	public I_JP_ContractContent getJP_ContractContent() throws RuntimeException
	{
		return (I_JP_ContractContent)MTable.get(getCtx(), I_JP_ContractContent.Table_ID)
			.getPO(getJP_ContractContent_ID(), get_TrxName());
	}

	/** Set Contract Content.
		@param JP_ContractContent_ID Contract Content
	*/
	public void setJP_ContractContent_ID (int JP_ContractContent_ID)
	{
		if (JP_ContractContent_ID < 1)
			set_Value (COLUMNNAME_JP_ContractContent_ID, null);
		else
			set_Value (COLUMNNAME_JP_ContractContent_ID, Integer.valueOf(JP_ContractContent_ID));
	}

	/** Get Contract Content.
		@return Contract Content	  */
	public int getJP_ContractContent_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ContractContent_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_JP_ContractProcPeriod getJP_ContractProcPeriod() throws RuntimeException
	{
		return (I_JP_ContractProcPeriod)MTable.get(getCtx(), I_JP_ContractProcPeriod.Table_ID)
			.getPO(getJP_ContractProcPeriod_ID(), get_TrxName());
	}

	/** Set Contract Process Period.
		@param JP_ContractProcPeriod_ID Contract Process Period
	*/
	public void setJP_ContractProcPeriod_ID (int JP_ContractProcPeriod_ID)
	{
		if (JP_ContractProcPeriod_ID < 1)
			set_Value (COLUMNNAME_JP_ContractProcPeriod_ID, null);
		else
			set_Value (COLUMNNAME_JP_ContractProcPeriod_ID, Integer.valueOf(JP_ContractProcPeriod_ID));
	}

	/** Get Contract Process Period.
		@return Contract Process Period	  */
	public int getJP_ContractProcPeriod_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ContractProcPeriod_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Contract Process Period Name.
		@param JP_ContractProcPeriod_Name Contract Process Period Name
	*/
	public void setJP_ContractProcPeriod_Name (String JP_ContractProcPeriod_Name)
	{
		set_Value (COLUMNNAME_JP_ContractProcPeriod_Name, JP_ContractProcPeriod_Name);
	}

	/** Get Contract Process Period Name.
		@return Contract Process Period Name	  */
	public String getJP_ContractProcPeriod_Name()
	{
		return (String)get_Value(COLUMNNAME_JP_ContractProcPeriod_Name);
	}

	/** Set Conversion Type(Search key).
		@param JP_ConversionType_Value Conversion Type(Search key)
	*/
	public void setJP_ConversionType_Value (String JP_ConversionType_Value)
	{
		set_Value (COLUMNNAME_JP_ConversionType_Value, JP_ConversionType_Value);
	}

	/** Get Conversion Type(Search key).
		@return Conversion Type(Search key)	  */
	public String getJP_ConversionType_Value()
	{
		return (String)get_Value(COLUMNNAME_JP_ConversionType_Value);
	}

	/** Set Data Migration Identifier.
		@param JP_DataMigration_Identifier Data Migration Identifier
	*/
	public void setJP_DataMigration_Identifier (String JP_DataMigration_Identifier)
	{
		set_Value (COLUMNNAME_JP_DataMigration_Identifier, JP_DataMigration_Identifier);
	}

	/** Get Data Migration Identifier.
		@return Data Migration Identifier	  */
	public String getJP_DataMigration_Identifier()
	{
		return (String)get_Value(COLUMNNAME_JP_DataMigration_Identifier);
	}

	/** Set Description(Header).
		@param JP_Description_Header Description(Header)
	*/
	public void setJP_Description_Header (String JP_Description_Header)
	{
		set_Value (COLUMNNAME_JP_Description_Header, JP_Description_Header);
	}

	/** Get Description(Header).
		@return Description(Header)	  */
	public String getJP_Description_Header()
	{
		return (String)get_Value(COLUMNNAME_JP_Description_Header);
	}

	/** Set Description(Line).
		@param JP_Description_Line Description(Line)
	*/
	public void setJP_Description_Line (String JP_Description_Line)
	{
		set_Value (COLUMNNAME_JP_Description_Line, JP_Description_Line);
	}

	/** Get Description(Line).
		@return Description(Line)	  */
	public String getJP_Description_Line()
	{
		return (String)get_Value(COLUMNNAME_JP_Description_Line);
	}

	/** Set Document Type(Name).
		@param JP_DocType_Name Document Type(Name)
	*/
	public void setJP_DocType_Name (String JP_DocType_Name)
	{
		set_Value (COLUMNNAME_JP_DocType_Name, JP_DocType_Name);
	}

	/** Get Document Type(Name).
		@return Document Type(Name)	  */
	public String getJP_DocType_Name()
	{
		return (String)get_Value(COLUMNNAME_JP_DocType_Name);
	}

	/** Set Account Element(Search Key).
		@param JP_ElementValue_Value Account Element(Search Key)
	*/
	public void setJP_ElementValue_Value (String JP_ElementValue_Value)
	{
		set_Value (COLUMNNAME_JP_ElementValue_Value, JP_ElementValue_Value);
	}

	/** Get Account Element(Search Key).
		@return Account Element(Search Key)	  */
	public String getJP_ElementValue_Value()
	{
		return (String)get_Value(COLUMNNAME_JP_ElementValue_Value);
	}

	/** Set Budget(Name).
		@param JP_GL_Budget_Name Budget(Name)
	*/
	public void setJP_GL_Budget_Name (String JP_GL_Budget_Name)
	{
		set_Value (COLUMNNAME_JP_GL_Budget_Name, JP_GL_Budget_Name);
	}

	/** Get Budget(Name).
		@return Budget(Name)	  */
	public String getJP_GL_Budget_Name()
	{
		return (String)get_Value(COLUMNNAME_JP_GL_Budget_Name);
	}

	/** Set GL Category(Name).
		@param JP_GL_Category_Name GL Category(Name)
	*/
	public void setJP_GL_Category_Name (String JP_GL_Category_Name)
	{
		set_Value (COLUMNNAME_JP_GL_Category_Name, JP_GL_Category_Name);
	}

	/** Get GL Category(Name).
		@return GL Category(Name)	  */
	public String getJP_GL_Category_Name()
	{
		return (String)get_Value(COLUMNNAME_JP_GL_Category_Name);
	}

	/** Set Location From(Label).
		@param JP_LocFrom_Label Location From(Label)
	*/
	public void setJP_LocFrom_Label (String JP_LocFrom_Label)
	{
		set_Value (COLUMNNAME_JP_LocFrom_Label, JP_LocFrom_Label);
	}

	/** Get Location From(Label).
		@return Location From(Label)	  */
	public String getJP_LocFrom_Label()
	{
		return (String)get_Value(COLUMNNAME_JP_LocFrom_Label);
	}

	/** Set Location To(Label).
		@param JP_LocTo_Label Location To(Label)
	*/
	public void setJP_LocTo_Label (String JP_LocTo_Label)
	{
		set_Value (COLUMNNAME_JP_LocTo_Label, JP_LocTo_Label);
	}

	/** Get Location To(Label).
		@return Location To(Label)	  */
	public String getJP_LocTo_Label()
	{
		return (String)get_Value(COLUMNNAME_JP_LocTo_Label);
	}

	/** Set Locator(Search Key).
		@param JP_Locator_Value Warehouse Locator
	*/
	public void setJP_Locator_Value (String JP_Locator_Value)
	{
		set_Value (COLUMNNAME_JP_Locator_Value, JP_Locator_Value);
	}

	/** Get Locator(Search Key).
		@return Warehouse Locator
	  */
	public String getJP_Locator_Value()
	{
		return (String)get_Value(COLUMNNAME_JP_Locator_Value);
	}

	/** Set JP_Order_DocumentNo.
		@param JP_Order_DocumentNo JP_Order_DocumentNo
	*/
	public void setJP_Order_DocumentNo (String JP_Order_DocumentNo)
	{
		set_Value (COLUMNNAME_JP_Order_DocumentNo, JP_Order_DocumentNo);
	}

	/** Get JP_Order_DocumentNo.
		@return JP_Order_DocumentNo	  */
	public String getJP_Order_DocumentNo()
	{
		return (String)get_Value(COLUMNNAME_JP_Order_DocumentNo);
	}

	public org.compiere.model.I_C_Order getJP_Order() throws RuntimeException
	{
		return (org.compiere.model.I_C_Order)MTable.get(getCtx(), org.compiere.model.I_C_Order.Table_ID)
			.getPO(getJP_Order_ID(), get_TrxName());
	}

	/** Set Sales Order.
		@param JP_Order_ID Sales Order
	*/
	public void setJP_Order_ID (int JP_Order_ID)
	{
		if (JP_Order_ID < 1)
			set_Value (COLUMNNAME_JP_Order_ID, null);
		else
			set_Value (COLUMNNAME_JP_Order_ID, Integer.valueOf(JP_Order_ID));
	}

	/** Get Sales Order.
		@return Sales Order	  */
	public int getJP_Order_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Order_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Trx Organization(Search Key).
		@param JP_OrgTrx_Value Trx Organization(Search Key)
	*/
	public void setJP_OrgTrx_Value (String JP_OrgTrx_Value)
	{
		set_Value (COLUMNNAME_JP_OrgTrx_Value, JP_OrgTrx_Value);
	}

	/** Get Trx Organization(Search Key).
		@return Trx Organization(Search Key)	  */
	public String getJP_OrgTrx_Value()
	{
		return (String)get_Value(COLUMNNAME_JP_OrgTrx_Value);
	}

	/** Set Organization(Search Key).
		@param JP_Org_Value Organization(Search Key)
	*/
	public void setJP_Org_Value (String JP_Org_Value)
	{
		set_Value (COLUMNNAME_JP_Org_Value, JP_Org_Value);
	}

	/** Get Organization(Search Key).
		@return Organization(Search Key)	  */
	public String getJP_Org_Value()
	{
		return (String)get_Value(COLUMNNAME_JP_Org_Value);
	}

	/** Set Product(Search Key).
		@param JP_Product_Value Product(Search Key)
	*/
	public void setJP_Product_Value (String JP_Product_Value)
	{
		set_Value (COLUMNNAME_JP_Product_Value, JP_Product_Value);
	}

	/** Get Product(Search Key).
		@return Product(Search Key)	  */
	public String getJP_Product_Value()
	{
		return (String)get_Value(COLUMNNAME_JP_Product_Value);
	}

	/** Set Project Phase(Name).
		@param JP_ProjectPhase_Name Project Phase(Name)
	*/
	public void setJP_ProjectPhase_Name (String JP_ProjectPhase_Name)
	{
		set_Value (COLUMNNAME_JP_ProjectPhase_Name, JP_ProjectPhase_Name);
	}

	/** Get Project Phase(Name).
		@return Project Phase(Name)	  */
	public String getJP_ProjectPhase_Name()
	{
		return (String)get_Value(COLUMNNAME_JP_ProjectPhase_Name);
	}

	/** Set Project Task(Name).
		@param JP_ProjectTask_Name Project Task(Name)
	*/
	public void setJP_ProjectTask_Name (String JP_ProjectTask_Name)
	{
		set_Value (COLUMNNAME_JP_ProjectTask_Name, JP_ProjectTask_Name);
	}

	/** Get Project Task(Name).
		@return Project Task(Name)	  */
	public String getJP_ProjectTask_Name()
	{
		return (String)get_Value(COLUMNNAME_JP_ProjectTask_Name);
	}

	/** Set Project(Search Key).
		@param JP_Project_Value Project(Search Key)
	*/
	public void setJP_Project_Value (String JP_Project_Value)
	{
		set_Value (COLUMNNAME_JP_Project_Value, JP_Project_Value);
	}

	/** Get Project(Search Key).
		@return Project(Search Key)	  */
	public String getJP_Project_Value()
	{
		return (String)get_Value(COLUMNNAME_JP_Project_Value);
	}

	/** Auto Tax Calculation not applicable = N */
	public static final String JP_SOPOTYPE_AutoTaxCalculationNotApplicable = "N";
	/** Purchase Tax = P */
	public static final String JP_SOPOTYPE_PurchaseTax = "P";
	/** Sales Tax = S */
	public static final String JP_SOPOTYPE_SalesTax = "S";
	/** Set SO/PO Type.
		@param JP_SOPOType JPIERE-0543:JPBP
	*/
	public void setJP_SOPOType (String JP_SOPOType)
	{

		set_Value (COLUMNNAME_JP_SOPOType, JP_SOPOType);
	}

	/** Get SO/PO Type.
		@return JPIERE-0543:JPBP
	  */
	public String getJP_SOPOType()
	{
		return (String)get_Value(COLUMNNAME_JP_SOPOType);
	}

	/** Set Sales Region(Search Key).
		@param JP_SalesRegion_Value Sales coverage region
	*/
	public void setJP_SalesRegion_Value (String JP_SalesRegion_Value)
	{
		set_Value (COLUMNNAME_JP_SalesRegion_Value, JP_SalesRegion_Value);
	}

	/** Get Sales Region(Search Key).
		@return Sales coverage region
	  */
	public String getJP_SalesRegion_Value()
	{
		return (String)get_Value(COLUMNNAME_JP_SalesRegion_Value);
	}

	/** Set Sub Account(Search Key).
		@param JP_SubAcct_Value Sub Account(Search Key)
	*/
	public void setJP_SubAcct_Value (String JP_SubAcct_Value)
	{
		set_Value (COLUMNNAME_JP_SubAcct_Value, JP_SubAcct_Value);
	}

	/** Get Sub Account(Search Key).
		@return Sub Account(Search Key)	  */
	public String getJP_SubAcct_Value()
	{
		return (String)get_Value(COLUMNNAME_JP_SubAcct_Value);
	}

	/** Set Tax(Name).
		@param JP_Tax_Name Tax(Name)
	*/
	public void setJP_Tax_Name (String JP_Tax_Name)
	{
		set_Value (COLUMNNAME_JP_Tax_Name, JP_Tax_Name);
	}

	/** Get Tax(Name).
		@return Tax(Name)	  */
	public String getJP_Tax_Name()
	{
		return (String)get_Value(COLUMNNAME_JP_Tax_Name);
	}

	/** Set User Element List 1(Search key).
		@param JP_UserElement1_Value User Element List 1(Search key)
	*/
	public void setJP_UserElement1_Value (String JP_UserElement1_Value)
	{
		set_Value (COLUMNNAME_JP_UserElement1_Value, JP_UserElement1_Value);
	}

	/** Get User Element List 1(Search key).
		@return User Element List 1(Search key)	  */
	public String getJP_UserElement1_Value()
	{
		return (String)get_Value(COLUMNNAME_JP_UserElement1_Value);
	}

	/** Set User Element List 2(Search key).
		@param JP_UserElement2_Value User Element List 2(Search key)
	*/
	public void setJP_UserElement2_Value (String JP_UserElement2_Value)
	{
		set_Value (COLUMNNAME_JP_UserElement2_Value, JP_UserElement2_Value);
	}

	/** Get User Element List 2(Search key).
		@return User Element List 2(Search key)	  */
	public String getJP_UserElement2_Value()
	{
		return (String)get_Value(COLUMNNAME_JP_UserElement2_Value);
	}

	/** Set Line No.
		@param Line Unique line for this document
	*/
	public void setLine (int Line)
	{
		set_Value (COLUMNNAME_Line, Integer.valueOf(Line));
	}

	/** Get Line No.
		@return Unique line for this document
	  */
	public int getLine()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Line);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_Locator getM_Locator() throws RuntimeException
	{
		return (org.compiere.model.I_M_Locator)MTable.get(getCtx(), org.compiere.model.I_M_Locator.Table_ID)
			.getPO(getM_Locator_ID(), get_TrxName());
	}

	/** Set Locator.
		@param M_Locator_ID Warehouse Locator
	*/
	public void setM_Locator_ID (int M_Locator_ID)
	{
		if (M_Locator_ID < 1)
			set_Value (COLUMNNAME_M_Locator_ID, null);
		else
			set_Value (COLUMNNAME_M_Locator_ID, Integer.valueOf(M_Locator_ID));
	}

	/** Get Locator.
		@return Warehouse Locator
	  */
	public int getM_Locator_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Locator_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_Product getM_Product() throws RuntimeException
	{
		return (org.compiere.model.I_M_Product)MTable.get(getCtx(), org.compiere.model.I_M_Product.Table_ID)
			.getPO(getM_Product_ID(), get_TrxName());
	}

	/** Set Product.
		@param M_Product_ID Product, Service, Item
	*/
	public void setM_Product_ID (int M_Product_ID)
	{
		if (M_Product_ID < 1)
			set_Value (COLUMNNAME_M_Product_ID, null);
		else
			set_Value (COLUMNNAME_M_Product_ID, Integer.valueOf(M_Product_ID));
	}

	/** Get Product.
		@return Product, Service, Item
	  */
	public int getM_Product_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Product_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** PostingType AD_Reference_ID=125 */
	public static final int POSTINGTYPE_AD_Reference_ID=125;
	/** Actual = A */
	public static final String POSTINGTYPE_Actual = "A";
	/** Budget = B */
	public static final String POSTINGTYPE_Budget = "B";
	/** Commitment = E */
	public static final String POSTINGTYPE_Commitment = "E";
	/** Reservation = R */
	public static final String POSTINGTYPE_Reservation = "R";
	/** Statistical = S */
	public static final String POSTINGTYPE_Statistical = "S";
	/** Set PostingType.
		@param PostingType The type of posted amount for the transaction
	*/
	public void setPostingType (String PostingType)
	{

		set_Value (COLUMNNAME_PostingType, PostingType);
	}

	/** Get PostingType.
		@return The type of posted amount for the transaction
	  */
	public String getPostingType()
	{
		return (String)get_Value(COLUMNNAME_PostingType);
	}

	/** Set Processed.
		@param Processed The document has been processed
	*/
	public void setProcessed (boolean Processed)
	{
		set_Value (COLUMNNAME_Processed, Boolean.valueOf(Processed));
	}

	/** Get Processed.
		@return The document has been processed
	  */
	public boolean isProcessed()
	{
		Object oo = get_Value(COLUMNNAME_Processed);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Process Now.
		@param Processing Process Now
	*/
	public void setProcessing (boolean Processing)
	{
		set_Value (COLUMNNAME_Processing, Boolean.valueOf(Processing));
	}

	/** Get Process Now.
		@return Process Now	  */
	public boolean isProcessing()
	{
		Object oo = get_Value(COLUMNNAME_Processing);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Quantity.
		@param Qty Quantity
	*/
	public void setQty (BigDecimal Qty)
	{
		set_Value (COLUMNNAME_Qty, Qty);
	}

	/** Get Quantity.
		@return Quantity
	  */
	public BigDecimal getQty()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_Qty);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	public org.compiere.model.I_C_ElementValue getUser1() throws RuntimeException
	{
		return (org.compiere.model.I_C_ElementValue)MTable.get(getCtx(), org.compiere.model.I_C_ElementValue.Table_ID)
			.getPO(getUser1_ID(), get_TrxName());
	}

	/** Set User Element List 1.
		@param User1_ID User defined list element #1
	*/
	public void setUser1_ID (int User1_ID)
	{
		if (User1_ID < 1)
			set_Value (COLUMNNAME_User1_ID, null);
		else
			set_Value (COLUMNNAME_User1_ID, Integer.valueOf(User1_ID));
	}

	/** Get User Element List 1.
		@return User defined list element #1
	  */
	public int getUser1_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_User1_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_ElementValue getUser2() throws RuntimeException
	{
		return (org.compiere.model.I_C_ElementValue)MTable.get(getCtx(), org.compiere.model.I_C_ElementValue.Table_ID)
			.getPO(getUser2_ID(), get_TrxName());
	}

	/** Set User Element List 2.
		@param User2_ID User defined list element #2
	*/
	public void setUser2_ID (int User2_ID)
	{
		if (User2_ID < 1)
			set_Value (COLUMNNAME_User2_ID, null);
		else
			set_Value (COLUMNNAME_User2_ID, Integer.valueOf(User2_ID));
	}

	/** Get User Element List 2.
		@return User defined list element #2
	  */
	public int getUser2_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_User2_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set User Column 1.
		@param UserElement1_ID User defined accounting Element
	*/
	public void setUserElement1_ID (int UserElement1_ID)
	{
		if (UserElement1_ID < 1)
			set_Value (COLUMNNAME_UserElement1_ID, null);
		else
			set_Value (COLUMNNAME_UserElement1_ID, Integer.valueOf(UserElement1_ID));
	}

	/** Get User Column 1.
		@return User defined accounting Element
	  */
	public int getUserElement1_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_UserElement1_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set User Column 2.
		@param UserElement2_ID User defined accounting Element
	*/
	public void setUserElement2_ID (int UserElement2_ID)
	{
		if (UserElement2_ID < 1)
			set_Value (COLUMNNAME_UserElement2_ID, null);
		else
			set_Value (COLUMNNAME_UserElement2_ID, Integer.valueOf(UserElement2_ID));
	}

	/** Get User Column 2.
		@return User defined accounting Element
	  */
	public int getUserElement2_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_UserElement2_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set UOM Code.
		@param X12DE355 UOM EDI X12 Code
	*/
	public void setX12DE355 (String X12DE355)
	{
		set_Value (COLUMNNAME_X12DE355, X12DE355);
	}

	/** Get UOM Code.
		@return UOM EDI X12 Code
	  */
	public String getX12DE355()
	{
		return (String)get_Value(COLUMNNAME_X12DE355);
	}
}