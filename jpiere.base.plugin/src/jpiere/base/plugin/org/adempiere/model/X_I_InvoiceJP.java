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

/** Generated Model for I_InvoiceJP
 *  @author iDempiere (generated) 
 *  @version Release 5.1 - $Id$ */
public class X_I_InvoiceJP extends PO implements I_I_InvoiceJP, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20180815L;

    /** Standard Constructor */
    public X_I_InvoiceJP (Properties ctx, int I_InvoiceJP_ID, String trxName)
    {
      super (ctx, I_InvoiceJP_ID, trxName);
      /** if (I_InvoiceJP_ID == 0)
        {
			setI_InvoiceJP_ID (0);
			setI_IsImported (false);
        } */
    }

    /** Load Constructor */
    public X_I_InvoiceJP (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_I_InvoiceJP[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set Trx Organization.
		@param AD_OrgTrx_ID 
		Performing or initiating organization
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
	public int getAD_OrgTrx_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_OrgTrx_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_AD_User getAD_User() throws RuntimeException
    {
		return (org.compiere.model.I_AD_User)MTable.get(getCtx(), org.compiere.model.I_AD_User.Table_Name)
			.getPO(getAD_User_ID(), get_TrxName());	}

	/** Set User/Contact.
		@param AD_User_ID 
		User within the system - Internal or Business Partner Contact
	  */
	public void setAD_User_ID (int AD_User_ID)
	{
		if (AD_User_ID < 1) 
			set_Value (COLUMNNAME_AD_User_ID, null);
		else 
			set_Value (COLUMNNAME_AD_User_ID, Integer.valueOf(AD_User_ID));
	}

	/** Get User/Contact.
		@return User within the system - Internal or Business Partner Contact
	  */
	public int getAD_User_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_User_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Activity getC_Activity() throws RuntimeException
    {
		return (org.compiere.model.I_C_Activity)MTable.get(getCtx(), org.compiere.model.I_C_Activity.Table_Name)
			.getPO(getC_Activity_ID(), get_TrxName());	}

	/** Set Activity.
		@param C_Activity_ID 
		Business Activity
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
	public int getC_Activity_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Activity_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_BPartner getC_BPartner() throws RuntimeException
    {
		return (org.compiere.model.I_C_BPartner)MTable.get(getCtx(), org.compiere.model.I_C_BPartner.Table_Name)
			.getPO(getC_BPartner_ID(), get_TrxName());	}

	/** Set Business Partner .
		@param C_BPartner_ID 
		Identifies a Business Partner
	  */
	public void setC_BPartner_ID (int C_BPartner_ID)
	{
		if (C_BPartner_ID < 1) 
			set_Value (COLUMNNAME_C_BPartner_ID, null);
		else 
			set_Value (COLUMNNAME_C_BPartner_ID, Integer.valueOf(C_BPartner_ID));
	}

	/** Get Business Partner .
		@return Identifies a Business Partner
	  */
	public int getC_BPartner_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_BPartner_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_BPartner_Location getC_BPartner_Location() throws RuntimeException
    {
		return (org.compiere.model.I_C_BPartner_Location)MTable.get(getCtx(), org.compiere.model.I_C_BPartner_Location.Table_Name)
			.getPO(getC_BPartner_Location_ID(), get_TrxName());	}

	/** Set Partner Location.
		@param C_BPartner_Location_ID 
		Identifies the (ship to) address for this Business Partner
	  */
	public void setC_BPartner_Location_ID (int C_BPartner_Location_ID)
	{
		if (C_BPartner_Location_ID < 1) 
			set_Value (COLUMNNAME_C_BPartner_Location_ID, null);
		else 
			set_Value (COLUMNNAME_C_BPartner_Location_ID, Integer.valueOf(C_BPartner_Location_ID));
	}

	/** Get Partner Location.
		@return Identifies the (ship to) address for this Business Partner
	  */
	public int getC_BPartner_Location_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_BPartner_Location_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Campaign getC_Campaign() throws RuntimeException
    {
		return (org.compiere.model.I_C_Campaign)MTable.get(getCtx(), org.compiere.model.I_C_Campaign.Table_Name)
			.getPO(getC_Campaign_ID(), get_TrxName());	}

	/** Set Campaign.
		@param C_Campaign_ID 
		Marketing Campaign
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
	public int getC_Campaign_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Campaign_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Charge getC_Charge() throws RuntimeException
    {
		return (org.compiere.model.I_C_Charge)MTable.get(getCtx(), org.compiere.model.I_C_Charge.Table_Name)
			.getPO(getC_Charge_ID(), get_TrxName());	}

	/** Set Charge.
		@param C_Charge_ID 
		Additional document charges
	  */
	public void setC_Charge_ID (int C_Charge_ID)
	{
		if (C_Charge_ID < 1) 
			set_Value (COLUMNNAME_C_Charge_ID, null);
		else 
			set_Value (COLUMNNAME_C_Charge_ID, Integer.valueOf(C_Charge_ID));
	}

	/** Get Charge.
		@return Additional document charges
	  */
	public int getC_Charge_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Charge_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Currency getC_Currency() throws RuntimeException
    {
		return (org.compiere.model.I_C_Currency)MTable.get(getCtx(), org.compiere.model.I_C_Currency.Table_Name)
			.getPO(getC_Currency_ID(), get_TrxName());	}

	/** Set Currency.
		@param C_Currency_ID 
		The Currency for this record
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
	public int getC_Currency_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Currency_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_DocType getC_DocType() throws RuntimeException
    {
		return (org.compiere.model.I_C_DocType)MTable.get(getCtx(), org.compiere.model.I_C_DocType.Table_Name)
			.getPO(getC_DocType_ID(), get_TrxName());	}

	/** Set Document Type.
		@param C_DocType_ID 
		Document type or rules
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
	public int getC_DocType_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_DocType_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_InvoiceLine getC_InvoiceLine() throws RuntimeException
    {
		return (org.compiere.model.I_C_InvoiceLine)MTable.get(getCtx(), org.compiere.model.I_C_InvoiceLine.Table_Name)
			.getPO(getC_InvoiceLine_ID(), get_TrxName());	}

	/** Set Invoice Line.
		@param C_InvoiceLine_ID 
		Invoice Detail Line
	  */
	public void setC_InvoiceLine_ID (int C_InvoiceLine_ID)
	{
		if (C_InvoiceLine_ID < 1) 
			set_Value (COLUMNNAME_C_InvoiceLine_ID, null);
		else 
			set_Value (COLUMNNAME_C_InvoiceLine_ID, Integer.valueOf(C_InvoiceLine_ID));
	}

	/** Get Invoice Line.
		@return Invoice Detail Line
	  */
	public int getC_InvoiceLine_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_InvoiceLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Invoice getC_Invoice() throws RuntimeException
    {
		return (org.compiere.model.I_C_Invoice)MTable.get(getCtx(), org.compiere.model.I_C_Invoice.Table_Name)
			.getPO(getC_Invoice_ID(), get_TrxName());	}

	/** Set Invoice.
		@param C_Invoice_ID 
		Invoice Identifier
	  */
	public void setC_Invoice_ID (int C_Invoice_ID)
	{
		if (C_Invoice_ID < 1) 
			set_Value (COLUMNNAME_C_Invoice_ID, null);
		else 
			set_Value (COLUMNNAME_C_Invoice_ID, Integer.valueOf(C_Invoice_ID));
	}

	/** Get Invoice.
		@return Invoice Identifier
	  */
	public int getC_Invoice_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Invoice_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Payment getC_PaymentTerm() throws RuntimeException
    {
		return (org.compiere.model.I_C_Payment)MTable.get(getCtx(), org.compiere.model.I_C_Payment.Table_Name)
			.getPO(getC_PaymentTerm_ID(), get_TrxName());	}

	/** Set Payment Term.
		@param C_PaymentTerm_ID 
		The terms of Payment (timing, discount)
	  */
	public void setC_PaymentTerm_ID (int C_PaymentTerm_ID)
	{
		if (C_PaymentTerm_ID < 1) 
			set_Value (COLUMNNAME_C_PaymentTerm_ID, null);
		else 
			set_Value (COLUMNNAME_C_PaymentTerm_ID, Integer.valueOf(C_PaymentTerm_ID));
	}

	/** Get Payment Term.
		@return The terms of Payment (timing, discount)
	  */
	public int getC_PaymentTerm_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_PaymentTerm_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Project getC_Project() throws RuntimeException
    {
		return (org.compiere.model.I_C_Project)MTable.get(getCtx(), org.compiere.model.I_C_Project.Table_Name)
			.getPO(getC_Project_ID(), get_TrxName());	}

	/** Set Project.
		@param C_Project_ID 
		Financial Project
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
	public int getC_Project_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Project_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Tax getC_Tax() throws RuntimeException
    {
		return (org.compiere.model.I_C_Tax)MTable.get(getCtx(), org.compiere.model.I_C_Tax.Table_Name)
			.getPO(getC_Tax_ID(), get_TrxName());	}

	/** Set Tax.
		@param C_Tax_ID 
		Tax identifier
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
	public int getC_Tax_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Tax_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_UOM getC_UOM() throws RuntimeException
    {
		return (org.compiere.model.I_C_UOM)MTable.get(getCtx(), org.compiere.model.I_C_UOM.Table_Name)
			.getPO(getC_UOM_ID(), get_TrxName());	}

	/** Set UOM.
		@param C_UOM_ID 
		Unit of Measure
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
	public int getC_UOM_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_UOM_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Account Date.
		@param DateAcct 
		Accounting Date
	  */
	public void setDateAcct (Timestamp DateAcct)
	{
		set_Value (COLUMNNAME_DateAcct, DateAcct);
	}

	/** Get Account Date.
		@return Accounting Date
	  */
	public Timestamp getDateAcct () 
	{
		return (Timestamp)get_Value(COLUMNNAME_DateAcct);
	}

	/** Set Date Invoiced.
		@param DateInvoiced 
		Date printed on Invoice
	  */
	public void setDateInvoiced (Timestamp DateInvoiced)
	{
		set_Value (COLUMNNAME_DateInvoiced, DateInvoiced);
	}

	/** Get Date Invoiced.
		@return Date printed on Invoice
	  */
	public Timestamp getDateInvoiced () 
	{
		return (Timestamp)get_Value(COLUMNNAME_DateInvoiced);
	}

	/** Set Description.
		@param Description 
		Optional short description of the record
	  */
	public void setDescription (String Description)
	{
		set_Value (COLUMNNAME_Description, Description);
	}

	/** Get Description.
		@return Optional short description of the record
	  */
	public String getDescription () 
	{
		return (String)get_Value(COLUMNNAME_Description);
	}

	/** Set Document No.
		@param DocumentNo 
		Document sequence number of the document
	  */
	public void setDocumentNo (String DocumentNo)
	{
		set_Value (COLUMNNAME_DocumentNo, DocumentNo);
	}

	/** Get Document No.
		@return Document sequence number of the document
	  */
	public String getDocumentNo () 
	{
		return (String)get_Value(COLUMNNAME_DocumentNo);
	}

	/** Set Import Error Message.
		@param I_ErrorMsg 
		Messages generated from import process
	  */
	public void setI_ErrorMsg (String I_ErrorMsg)
	{
		set_Value (COLUMNNAME_I_ErrorMsg, I_ErrorMsg);
	}

	/** Get Import Error Message.
		@return Messages generated from import process
	  */
	public String getI_ErrorMsg () 
	{
		return (String)get_Value(COLUMNNAME_I_ErrorMsg);
	}

	/** Set I_InvoiceJP.
		@param I_InvoiceJP_ID I_InvoiceJP	  */
	public void setI_InvoiceJP_ID (int I_InvoiceJP_ID)
	{
		if (I_InvoiceJP_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_I_InvoiceJP_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_I_InvoiceJP_ID, Integer.valueOf(I_InvoiceJP_ID));
	}

	/** Get I_InvoiceJP.
		@return I_InvoiceJP	  */
	public int getI_InvoiceJP_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_I_InvoiceJP_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set I_InvoiceJP_UU.
		@param I_InvoiceJP_UU I_InvoiceJP_UU	  */
	public void setI_InvoiceJP_UU (String I_InvoiceJP_UU)
	{
		set_ValueNoCheck (COLUMNNAME_I_InvoiceJP_UU, I_InvoiceJP_UU);
	}

	/** Get I_InvoiceJP_UU.
		@return I_InvoiceJP_UU	  */
	public String getI_InvoiceJP_UU () 
	{
		return (String)get_Value(COLUMNNAME_I_InvoiceJP_UU);
	}

	/** Set Imported.
		@param I_IsImported 
		Has this import been processed
	  */
	public void setI_IsImported (boolean I_IsImported)
	{
		set_Value (COLUMNNAME_I_IsImported, Boolean.valueOf(I_IsImported));
	}

	/** Get Imported.
		@return Has this import been processed
	  */
	public boolean isI_IsImported () 
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

	/** Set Discount Printed.
		@param IsDiscountPrinted 
		Print Discount on Invoice and Order
	  */
	public void setIsDiscountPrinted (boolean IsDiscountPrinted)
	{
		set_Value (COLUMNNAME_IsDiscountPrinted, Boolean.valueOf(IsDiscountPrinted));
	}

	/** Get Discount Printed.
		@return Print Discount on Invoice and Order
	  */
	public boolean isDiscountPrinted () 
	{
		Object oo = get_Value(COLUMNNAME_IsDiscountPrinted);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Sales Transaction.
		@param IsSOTrx 
		This is a Sales Transaction
	  */
	public void setIsSOTrx (boolean IsSOTrx)
	{
		set_Value (COLUMNNAME_IsSOTrx, Boolean.valueOf(IsSOTrx));
	}

	/** Get Sales Transaction.
		@return This is a Sales Transaction
	  */
	public boolean isSOTrx () 
	{
		Object oo = get_Value(COLUMNNAME_IsSOTrx);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Activity(Search Key).
		@param JP_Activity_Value Activity(Search Key)	  */
	public void setJP_Activity_Value (String JP_Activity_Value)
	{
		set_Value (COLUMNNAME_JP_Activity_Value, JP_Activity_Value);
	}

	/** Get Activity(Search Key).
		@return Activity(Search Key)	  */
	public String getJP_Activity_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_Activity_Value);
	}

	/** Set Partner Location(Name).
		@param JP_BPartner_Location_Name Partner Location(Name)	  */
	public void setJP_BPartner_Location_Name (String JP_BPartner_Location_Name)
	{
		set_Value (COLUMNNAME_JP_BPartner_Location_Name, JP_BPartner_Location_Name);
	}

	/** Get Partner Location(Name).
		@return Partner Location(Name)	  */
	public String getJP_BPartner_Location_Name () 
	{
		return (String)get_Value(COLUMNNAME_JP_BPartner_Location_Name);
	}

	/** Set Business Partner(Search Key).
		@param JP_BPartner_Value Business Partner(Search Key)	  */
	public void setJP_BPartner_Value (String JP_BPartner_Value)
	{
		set_Value (COLUMNNAME_JP_BPartner_Value, JP_BPartner_Value);
	}

	/** Get Business Partner(Search Key).
		@return Business Partner(Search Key)	  */
	public String getJP_BPartner_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_BPartner_Value);
	}

	/** Set Campaign(Search Key).
		@param JP_Campaign_Value Campaign(Search Key)	  */
	public void setJP_Campaign_Value (String JP_Campaign_Value)
	{
		set_Value (COLUMNNAME_JP_Campaign_Value, JP_Campaign_Value);
	}

	/** Get Campaign(Search Key).
		@return Campaign(Search Key)	  */
	public String getJP_Campaign_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_Campaign_Value);
	}

	/** Set Charge(Name).
		@param JP_Charge_Name Charge(Name)	  */
	public void setJP_Charge_Name (String JP_Charge_Name)
	{
		set_Value (COLUMNNAME_JP_Charge_Name, JP_Charge_Name);
	}

	/** Get Charge(Name).
		@return Charge(Name)	  */
	public String getJP_Charge_Name () 
	{
		return (String)get_Value(COLUMNNAME_JP_Charge_Name);
	}

	/** Set Document Type(Name).
		@param JP_DocType_Name Document Type(Name)	  */
	public void setJP_DocType_Name (String JP_DocType_Name)
	{
		set_Value (COLUMNNAME_JP_DocType_Name, JP_DocType_Name);
	}

	/** Get Document Type(Name).
		@return Document Type(Name)	  */
	public String getJP_DocType_Name () 
	{
		return (String)get_Value(COLUMNNAME_JP_DocType_Name);
	}

	public org.compiere.model.I_C_Activity getJP_Line_Activity() throws RuntimeException
    {
		return (org.compiere.model.I_C_Activity)MTable.get(getCtx(), org.compiere.model.I_C_Activity.Table_Name)
			.getPO(getJP_Line_Activity_ID(), get_TrxName());	}

	/** Set Activity of Line.
		@param JP_Line_Activity_ID Activity of Line	  */
	public void setJP_Line_Activity_ID (int JP_Line_Activity_ID)
	{
		if (JP_Line_Activity_ID < 1) 
			set_Value (COLUMNNAME_JP_Line_Activity_ID, null);
		else 
			set_Value (COLUMNNAME_JP_Line_Activity_ID, Integer.valueOf(JP_Line_Activity_ID));
	}

	/** Get Activity of Line.
		@return Activity of Line	  */
	public int getJP_Line_Activity_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Line_Activity_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Activity of Line(Search Key).
		@param JP_Line_Activity_Value Activity of Line(Search Key)	  */
	public void setJP_Line_Activity_Value (String JP_Line_Activity_Value)
	{
		set_Value (COLUMNNAME_JP_Line_Activity_Value, JP_Line_Activity_Value);
	}

	/** Get Activity of Line(Search Key).
		@return Activity of Line(Search Key)	  */
	public String getJP_Line_Activity_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_Line_Activity_Value);
	}

	public org.compiere.model.I_C_Campaign getJP_Line_Campaign() throws RuntimeException
    {
		return (org.compiere.model.I_C_Campaign)MTable.get(getCtx(), org.compiere.model.I_C_Campaign.Table_Name)
			.getPO(getJP_Line_Campaign_ID(), get_TrxName());	}

	/** Set Campaign of Line.
		@param JP_Line_Campaign_ID Campaign of Line	  */
	public void setJP_Line_Campaign_ID (int JP_Line_Campaign_ID)
	{
		if (JP_Line_Campaign_ID < 1) 
			set_Value (COLUMNNAME_JP_Line_Campaign_ID, null);
		else 
			set_Value (COLUMNNAME_JP_Line_Campaign_ID, Integer.valueOf(JP_Line_Campaign_ID));
	}

	/** Get Campaign of Line.
		@return Campaign of Line	  */
	public int getJP_Line_Campaign_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Line_Campaign_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Campaign of Line(Search Key).
		@param JP_Line_Campaign_Value Campaign of Line(Search Key)	  */
	public void setJP_Line_Campaign_Value (String JP_Line_Campaign_Value)
	{
		set_Value (COLUMNNAME_JP_Line_Campaign_Value, JP_Line_Campaign_Value);
	}

	/** Get Campaign of Line(Search Key).
		@return Campaign of Line(Search Key)	  */
	public String getJP_Line_Campaign_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_Line_Campaign_Value);
	}

	/** Set Trx Org of Line.
		@param JP_Line_OrgTrx_ID Trx Org of Line	  */
	public void setJP_Line_OrgTrx_ID (int JP_Line_OrgTrx_ID)
	{
		if (JP_Line_OrgTrx_ID < 1) 
			set_Value (COLUMNNAME_JP_Line_OrgTrx_ID, null);
		else 
			set_Value (COLUMNNAME_JP_Line_OrgTrx_ID, Integer.valueOf(JP_Line_OrgTrx_ID));
	}

	/** Get Trx Org of Line.
		@return Trx Org of Line	  */
	public int getJP_Line_OrgTrx_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Line_OrgTrx_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Trx Org of Line(Search Key).
		@param JP_Line_OrgTrx_Value Trx Org of Line(Search Key)	  */
	public void setJP_Line_OrgTrx_Value (String JP_Line_OrgTrx_Value)
	{
		set_Value (COLUMNNAME_JP_Line_OrgTrx_Value, JP_Line_OrgTrx_Value);
	}

	/** Get Trx Org of Line(Search Key).
		@return Trx Org of Line(Search Key)	  */
	public String getJP_Line_OrgTrx_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_Line_OrgTrx_Value);
	}

	public org.compiere.model.I_C_Project getJP_Line_Project() throws RuntimeException
    {
		return (org.compiere.model.I_C_Project)MTable.get(getCtx(), org.compiere.model.I_C_Project.Table_Name)
			.getPO(getJP_Line_Project_ID(), get_TrxName());	}

	/** Set Project of Line.
		@param JP_Line_Project_ID Project of Line	  */
	public void setJP_Line_Project_ID (int JP_Line_Project_ID)
	{
		if (JP_Line_Project_ID < 1) 
			set_Value (COLUMNNAME_JP_Line_Project_ID, null);
		else 
			set_Value (COLUMNNAME_JP_Line_Project_ID, Integer.valueOf(JP_Line_Project_ID));
	}

	/** Get Project of Line.
		@return Project of Line	  */
	public int getJP_Line_Project_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Line_Project_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Project of Line(Search Key).
		@param JP_Line_Project_Value Project of Line(Search Key)	  */
	public void setJP_Line_Project_Value (String JP_Line_Project_Value)
	{
		set_Value (COLUMNNAME_JP_Line_Project_Value, JP_Line_Project_Value);
	}

	/** Get Project of Line(Search Key).
		@return Project of Line(Search Key)	  */
	public String getJP_Line_Project_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_Line_Project_Value);
	}

	public org.compiere.model.I_C_ElementValue getJP_Line_User1() throws RuntimeException
    {
		return (org.compiere.model.I_C_ElementValue)MTable.get(getCtx(), org.compiere.model.I_C_ElementValue.Table_Name)
			.getPO(getJP_Line_User1_ID(), get_TrxName());	}

	/** Set User Element List 1 of Line.
		@param JP_Line_User1_ID 
		User defined list element #1
	  */
	public void setJP_Line_User1_ID (int JP_Line_User1_ID)
	{
		if (JP_Line_User1_ID < 1) 
			set_Value (COLUMNNAME_JP_Line_User1_ID, null);
		else 
			set_Value (COLUMNNAME_JP_Line_User1_ID, Integer.valueOf(JP_Line_User1_ID));
	}

	/** Get User Element List 1 of Line.
		@return User defined list element #1
	  */
	public int getJP_Line_User1_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Line_User1_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_ElementValue getJP_Line_User2() throws RuntimeException
    {
		return (org.compiere.model.I_C_ElementValue)MTable.get(getCtx(), org.compiere.model.I_C_ElementValue.Table_Name)
			.getPO(getJP_Line_User2_ID(), get_TrxName());	}

	/** Set User Element List 2 of Line.
		@param JP_Line_User2_ID 
		User defined list element #2
	  */
	public void setJP_Line_User2_ID (int JP_Line_User2_ID)
	{
		if (JP_Line_User2_ID < 1) 
			set_Value (COLUMNNAME_JP_Line_User2_ID, null);
		else 
			set_Value (COLUMNNAME_JP_Line_User2_ID, Integer.valueOf(JP_Line_User2_ID));
	}

	/** Get User Element List 2 of Line.
		@return User defined list element #2
	  */
	public int getJP_Line_User2_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Line_User2_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set User Element List 1 of Line(Search key).
		@param JP_Line_UserElement1_Value User Element List 1 of Line(Search key)	  */
	public void setJP_Line_UserElement1_Value (String JP_Line_UserElement1_Value)
	{
		set_Value (COLUMNNAME_JP_Line_UserElement1_Value, JP_Line_UserElement1_Value);
	}

	/** Get User Element List 1 of Line(Search key).
		@return User Element List 1 of Line(Search key)	  */
	public String getJP_Line_UserElement1_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_Line_UserElement1_Value);
	}

	/** Set User Element List 2 of Line(Search key).
		@param JP_Line_UserElement2_Value User Element List 2 of Line(Search key)	  */
	public void setJP_Line_UserElement2_Value (String JP_Line_UserElement2_Value)
	{
		set_Value (COLUMNNAME_JP_Line_UserElement2_Value, JP_Line_UserElement2_Value);
	}

	/** Get User Element List 2 of Line(Search key).
		@return User Element List 2 of Line(Search key)	  */
	public String getJP_Line_UserElement2_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_Line_UserElement2_Value);
	}

	/** Set Trx Organization(Search Key).
		@param JP_OrgTrx_Value Trx Organization(Search Key)	  */
	public void setJP_OrgTrx_Value (String JP_OrgTrx_Value)
	{
		set_Value (COLUMNNAME_JP_OrgTrx_Value, JP_OrgTrx_Value);
	}

	/** Get Trx Organization(Search Key).
		@return Trx Organization(Search Key)	  */
	public String getJP_OrgTrx_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_OrgTrx_Value);
	}

	/** Set Organization(Search Key).
		@param JP_Org_Value Organization(Search Key)	  */
	public void setJP_Org_Value (String JP_Org_Value)
	{
		set_Value (COLUMNNAME_JP_Org_Value, JP_Org_Value);
	}

	/** Get Organization(Search Key).
		@return Organization(Search Key)	  */
	public String getJP_Org_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_Org_Value);
	}

	/** Set Payment Term(Search Key).
		@param JP_PaymentTerm_Value 
		The terms of Payment (timing, discount)
	  */
	public void setJP_PaymentTerm_Value (String JP_PaymentTerm_Value)
	{
		set_Value (COLUMNNAME_JP_PaymentTerm_Value, JP_PaymentTerm_Value);
	}

	/** Get Payment Term(Search Key).
		@return The terms of Payment (timing, discount)
	  */
	public String getJP_PaymentTerm_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_PaymentTerm_Value);
	}

	/** Set Price List(Name).
		@param JP_PriceList_Name Price List(Name)	  */
	public void setJP_PriceList_Name (String JP_PriceList_Name)
	{
		set_Value (COLUMNNAME_JP_PriceList_Name, JP_PriceList_Name);
	}

	/** Get Price List(Name).
		@return Price List(Name)	  */
	public String getJP_PriceList_Name () 
	{
		return (String)get_Value(COLUMNNAME_JP_PriceList_Name);
	}

	/** Set Product(Search Key).
		@param JP_Product_Value Product(Search Key)	  */
	public void setJP_Product_Value (String JP_Product_Value)
	{
		set_Value (COLUMNNAME_JP_Product_Value, JP_Product_Value);
	}

	/** Get Product(Search Key).
		@return Product(Search Key)	  */
	public String getJP_Product_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_Product_Value);
	}

	/** Set Project(Search Key).
		@param JP_Project_Value Project(Search Key)	  */
	public void setJP_Project_Value (String JP_Project_Value)
	{
		set_Value (COLUMNNAME_JP_Project_Value, JP_Project_Value);
	}

	/** Get Project(Search Key).
		@return Project(Search Key)	  */
	public String getJP_Project_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_Project_Value);
	}

	/** Set Sales Rep(E-Mail).
		@param JP_SalesRep_EMail Sales Rep(E-Mail)	  */
	public void setJP_SalesRep_EMail (String JP_SalesRep_EMail)
	{
		set_Value (COLUMNNAME_JP_SalesRep_EMail, JP_SalesRep_EMail);
	}

	/** Get Sales Rep(E-Mail).
		@return Sales Rep(E-Mail)	  */
	public String getJP_SalesRep_EMail () 
	{
		return (String)get_Value(COLUMNNAME_JP_SalesRep_EMail);
	}

	/** Set Sales Rep(Name).
		@param JP_SalesRep_Name Sales Rep(Name)	  */
	public void setJP_SalesRep_Name (String JP_SalesRep_Name)
	{
		set_Value (COLUMNNAME_JP_SalesRep_Name, JP_SalesRep_Name);
	}

	/** Get Sales Rep(Name).
		@return Sales Rep(Name)	  */
	public String getJP_SalesRep_Name () 
	{
		return (String)get_Value(COLUMNNAME_JP_SalesRep_Name);
	}

	/** Set Sales Rep(Search Key).
		@param JP_SalesRep_Value Sales Rep(Search Key)	  */
	public void setJP_SalesRep_Value (String JP_SalesRep_Value)
	{
		set_Value (COLUMNNAME_JP_SalesRep_Value, JP_SalesRep_Value);
	}

	/** Get Sales Rep(Search Key).
		@return Sales Rep(Search Key)	  */
	public String getJP_SalesRep_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_SalesRep_Value);
	}

	/** Set User Element List 1(Search key).
		@param JP_UserElement1_Value User Element List 1(Search key)	  */
	public void setJP_UserElement1_Value (String JP_UserElement1_Value)
	{
		set_Value (COLUMNNAME_JP_UserElement1_Value, JP_UserElement1_Value);
	}

	/** Get User Element List 1(Search key).
		@return User Element List 1(Search key)	  */
	public String getJP_UserElement1_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_UserElement1_Value);
	}

	/** Set User Element List 2(Search key).
		@param JP_UserElement2_Value User Element List 2(Search key)	  */
	public void setJP_UserElement2_Value (String JP_UserElement2_Value)
	{
		set_Value (COLUMNNAME_JP_UserElement2_Value, JP_UserElement2_Value);
	}

	/** Get User Element List 2(Search key).
		@return User Element List 2(Search key)	  */
	public String getJP_UserElement2_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_UserElement2_Value);
	}

	/** Set User(E-Mail).
		@param JP_User_EMail User(E-Mail)	  */
	public void setJP_User_EMail (String JP_User_EMail)
	{
		set_Value (COLUMNNAME_JP_User_EMail, JP_User_EMail);
	}

	/** Get User(E-Mail).
		@return User(E-Mail)	  */
	public String getJP_User_EMail () 
	{
		return (String)get_Value(COLUMNNAME_JP_User_EMail);
	}

	/** Set User(Name).
		@param JP_User_Name User(Name)	  */
	public void setJP_User_Name (String JP_User_Name)
	{
		set_Value (COLUMNNAME_JP_User_Name, JP_User_Name);
	}

	/** Get User(Name).
		@return User(Name)	  */
	public String getJP_User_Name () 
	{
		return (String)get_Value(COLUMNNAME_JP_User_Name);
	}

	/** Set User(Search Key).
		@param JP_User_Value User(Search Key)	  */
	public void setJP_User_Value (String JP_User_Value)
	{
		set_Value (COLUMNNAME_JP_User_Value, JP_User_Value);
	}

	/** Get User(Search Key).
		@return User(Search Key)	  */
	public String getJP_User_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_User_Value);
	}

	/** Set Line No.
		@param Line 
		Unique line for this document
	  */
	public void setLine (int Line)
	{
		set_Value (COLUMNNAME_Line, Integer.valueOf(Line));
	}

	/** Get Line No.
		@return Unique line for this document
	  */
	public int getLine () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Line);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Line Description.
		@param LineDescription 
		Description of the Line
	  */
	public void setLineDescription (String LineDescription)
	{
		set_Value (COLUMNNAME_LineDescription, LineDescription);
	}

	/** Get Line Description.
		@return Description of the Line
	  */
	public String getLineDescription () 
	{
		return (String)get_Value(COLUMNNAME_LineDescription);
	}

	public org.compiere.model.I_M_PriceList getM_PriceList() throws RuntimeException
    {
		return (org.compiere.model.I_M_PriceList)MTable.get(getCtx(), org.compiere.model.I_M_PriceList.Table_Name)
			.getPO(getM_PriceList_ID(), get_TrxName());	}

	/** Set Price List.
		@param M_PriceList_ID 
		Unique identifier of a Price List
	  */
	public void setM_PriceList_ID (int M_PriceList_ID)
	{
		if (M_PriceList_ID < 1) 
			set_Value (COLUMNNAME_M_PriceList_ID, null);
		else 
			set_Value (COLUMNNAME_M_PriceList_ID, Integer.valueOf(M_PriceList_ID));
	}

	/** Get Price List.
		@return Unique identifier of a Price List
	  */
	public int getM_PriceList_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_PriceList_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_Product getM_Product() throws RuntimeException
    {
		return (org.compiere.model.I_M_Product)MTable.get(getCtx(), org.compiere.model.I_M_Product.Table_Name)
			.getPO(getM_Product_ID(), get_TrxName());	}

	/** Set Product.
		@param M_Product_ID 
		Product, Service, Item
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
	public int getM_Product_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Product_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Order Reference.
		@param POReference 
		Transaction Reference Number (Sales Order, Purchase Order) of your Business Partner
	  */
	public void setPOReference (String POReference)
	{
		set_Value (COLUMNNAME_POReference, POReference);
	}

	/** Get Order Reference.
		@return Transaction Reference Number (Sales Order, Purchase Order) of your Business Partner
	  */
	public String getPOReference () 
	{
		return (String)get_Value(COLUMNNAME_POReference);
	}

	/** PaymentRule AD_Reference_ID=195 */
	public static final int PAYMENTRULE_AD_Reference_ID=195;
	/** Cash = B */
	public static final String PAYMENTRULE_Cash = "B";
	/** Credit Card = K */
	public static final String PAYMENTRULE_CreditCard = "K";
	/** Direct Deposit = T */
	public static final String PAYMENTRULE_DirectDeposit = "T";
	/** Check = S */
	public static final String PAYMENTRULE_Check = "S";
	/** On Credit = P */
	public static final String PAYMENTRULE_OnCredit = "P";
	/** Direct Debit = D */
	public static final String PAYMENTRULE_DirectDebit = "D";
	/** Mixed POS Payment = M */
	public static final String PAYMENTRULE_MixedPOSPayment = "M";
	/** Set Payment Rule.
		@param PaymentRule 
		How you pay the invoice
	  */
	public void setPaymentRule (String PaymentRule)
	{

		set_Value (COLUMNNAME_PaymentRule, PaymentRule);
	}

	/** Get Payment Rule.
		@return How you pay the invoice
	  */
	public String getPaymentRule () 
	{
		return (String)get_Value(COLUMNNAME_PaymentRule);
	}

	/** Set Phone.
		@param Phone 
		Identifies a telephone number
	  */
	public void setPhone (String Phone)
	{
		set_Value (COLUMNNAME_Phone, Phone);
	}

	/** Get Phone.
		@return Identifies a telephone number
	  */
	public String getPhone () 
	{
		return (String)get_Value(COLUMNNAME_Phone);
	}

	/** Set Unit Price.
		@param PriceActual 
		Actual Price 
	  */
	public void setPriceActual (BigDecimal PriceActual)
	{
		set_Value (COLUMNNAME_PriceActual, PriceActual);
	}

	/** Get Unit Price.
		@return Actual Price 
	  */
	public BigDecimal getPriceActual () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_PriceActual);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Price.
		@param PriceEntered 
		Price Entered - the price based on the selected/base UoM
	  */
	public void setPriceEntered (BigDecimal PriceEntered)
	{
		set_Value (COLUMNNAME_PriceEntered, PriceEntered);
	}

	/** Get Price.
		@return Price Entered - the price based on the selected/base UoM
	  */
	public BigDecimal getPriceEntered () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_PriceEntered);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Processed.
		@param Processed 
		The document has been processed
	  */
	public void setProcessed (boolean Processed)
	{
		set_Value (COLUMNNAME_Processed, Boolean.valueOf(Processed));
	}

	/** Get Processed.
		@return The document has been processed
	  */
	public boolean isProcessed () 
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
		@param Processing Process Now	  */
	public void setProcessing (boolean Processing)
	{
		set_Value (COLUMNNAME_Processing, Boolean.valueOf(Processing));
	}

	/** Get Process Now.
		@return Process Now	  */
	public boolean isProcessing () 
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
		@param QtyEntered 
		The Quantity Entered is based on the selected UoM
	  */
	public void setQtyEntered (BigDecimal QtyEntered)
	{
		set_Value (COLUMNNAME_QtyEntered, QtyEntered);
	}

	/** Get Quantity.
		@return The Quantity Entered is based on the selected UoM
	  */
	public BigDecimal getQtyEntered () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_QtyEntered);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Invoiced Qty.
		@param QtyInvoiced 
		Invoiced Quantity
	  */
	public void setQtyInvoiced (BigDecimal QtyInvoiced)
	{
		set_Value (COLUMNNAME_QtyInvoiced, QtyInvoiced);
	}

	/** Get Invoiced Qty.
		@return Invoiced Quantity
	  */
	public BigDecimal getQtyInvoiced () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_QtyInvoiced);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	public org.compiere.model.I_AD_User getSalesRep() throws RuntimeException
    {
		return (org.compiere.model.I_AD_User)MTable.get(getCtx(), org.compiere.model.I_AD_User.Table_Name)
			.getPO(getSalesRep_ID(), get_TrxName());	}

	/** Set Sales Rep.
		@param SalesRep_ID 
		Sales Representative or Company Agent
	  */
	public void setSalesRep_ID (int SalesRep_ID)
	{
		if (SalesRep_ID < 1) 
			set_Value (COLUMNNAME_SalesRep_ID, null);
		else 
			set_Value (COLUMNNAME_SalesRep_ID, Integer.valueOf(SalesRep_ID));
	}

	/** Get Sales Rep.
		@return Sales Representative or Company Agent
	  */
	public int getSalesRep_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_SalesRep_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Tax Indicator.
		@param TaxIndicator 
		Short form for Tax to be printed on documents
	  */
	public void setTaxIndicator (String TaxIndicator)
	{
		set_Value (COLUMNNAME_TaxIndicator, TaxIndicator);
	}

	/** Get Tax Indicator.
		@return Short form for Tax to be printed on documents
	  */
	public String getTaxIndicator () 
	{
		return (String)get_Value(COLUMNNAME_TaxIndicator);
	}

	public org.compiere.model.I_C_ElementValue getUser1() throws RuntimeException
    {
		return (org.compiere.model.I_C_ElementValue)MTable.get(getCtx(), org.compiere.model.I_C_ElementValue.Table_Name)
			.getPO(getUser1_ID(), get_TrxName());	}

	/** Set User Element List 1.
		@param User1_ID 
		User defined list element #1
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
	public int getUser1_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_User1_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_ElementValue getUser2() throws RuntimeException
    {
		return (org.compiere.model.I_C_ElementValue)MTable.get(getCtx(), org.compiere.model.I_C_ElementValue.Table_Name)
			.getPO(getUser2_ID(), get_TrxName());	}

	/** Set User Element List 2.
		@param User2_ID 
		User defined list element #2
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
	public int getUser2_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_User2_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set UOM Code.
		@param X12DE355 
		UOM EDI X12 Code
	  */
	public void setX12DE355 (String X12DE355)
	{
		set_Value (COLUMNNAME_X12DE355, X12DE355);
	}

	/** Get UOM Code.
		@return UOM EDI X12 Code
	  */
	public String getX12DE355 () 
	{
		return (String)get_Value(COLUMNNAME_X12DE355);
	}
}