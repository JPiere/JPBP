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
import org.compiere.util.KeyNamePair;

/** Generated Model for JP_Contract
 *  @author iDempiere (generated) 
 *  @version Release 4.1 - $Id$ */
public class X_JP_Contract extends PO implements I_JP_Contract, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20170913L;

    /** Standard Constructor */
    public X_JP_Contract (Properties ctx, int JP_Contract_ID, String trxName)
    {
      super (ctx, JP_Contract_ID, trxName);
      /** if (JP_Contract_ID == 0)
        {
			setC_BPartner_ID (0);
			setC_Currency_ID (0);
// @$C_Currency_ID@
			setC_DocType_ID (0);
			setDateAcct (new Timestamp( System.currentTimeMillis() ));
// @#Date@
			setDateDoc (new Timestamp( System.currentTimeMillis() ));
// @#Date@
			setDocAction (null);
// CO
			setDocStatus (null);
// DR
			setDocumentNo (null);
			setIsApproved (false);
// N
			setIsAutomaticUpdateJP (false);
// N
			setJP_ContractCategory_ID (0);
			setJP_ContractDocAmt (Env.ZERO);
// 0
			setJP_ContractPeriodDate_From (new Timestamp( System.currentTimeMillis() ));
// @#Date@
			setJP_ContractStatus (null);
// PR
			setJP_ContractT_ID (0);
			setJP_ContractType (null);
// PDC
			setJP_Contract_ID (0);
			setName (null);
			setPosted (false);
// N
			setProcessed (false);
// N
        } */
    }

    /** Load Constructor */
    public X_JP_Contract (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 1 - Org 
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
      StringBuffer sb = new StringBuffer ("X_JP_Contract[")
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

	public org.compiere.model.I_C_Opportunity getC_Opportunity() throws RuntimeException
    {
		return (org.compiere.model.I_C_Opportunity)MTable.get(getCtx(), org.compiere.model.I_C_Opportunity.Table_Name)
			.getPO(getC_Opportunity_ID(), get_TrxName());	}

	/** Set Sales Opportunity.
		@param C_Opportunity_ID Sales Opportunity	  */
	public void setC_Opportunity_ID (int C_Opportunity_ID)
	{
		if (C_Opportunity_ID < 1) 
			set_Value (COLUMNNAME_C_Opportunity_ID, null);
		else 
			set_Value (COLUMNNAME_C_Opportunity_ID, Integer.valueOf(C_Opportunity_ID));
	}

	/** Get Sales Opportunity.
		@return Sales Opportunity	  */
	public int getC_Opportunity_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Opportunity_ID);
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

	/** Set Document Date.
		@param DateDoc 
		Date of the Document
	  */
	public void setDateDoc (Timestamp DateDoc)
	{
		set_Value (COLUMNNAME_DateDoc, DateDoc);
	}

	/** Get Document Date.
		@return Date of the Document
	  */
	public Timestamp getDateDoc () 
	{
		return (Timestamp)get_Value(COLUMNNAME_DateDoc);
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

	/** DocAction AD_Reference_ID=135 */
	public static final int DOCACTION_AD_Reference_ID=135;
	/** Complete = CO */
	public static final String DOCACTION_Complete = "CO";
	/** Approve = AP */
	public static final String DOCACTION_Approve = "AP";
	/** Reject = RJ */
	public static final String DOCACTION_Reject = "RJ";
	/** Post = PO */
	public static final String DOCACTION_Post = "PO";
	/** Void = VO */
	public static final String DOCACTION_Void = "VO";
	/** Close = CL */
	public static final String DOCACTION_Close = "CL";
	/** Reverse - Correct = RC */
	public static final String DOCACTION_Reverse_Correct = "RC";
	/** Reverse - Accrual = RA */
	public static final String DOCACTION_Reverse_Accrual = "RA";
	/** Invalidate = IN */
	public static final String DOCACTION_Invalidate = "IN";
	/** Re-activate = RE */
	public static final String DOCACTION_Re_Activate = "RE";
	/** <None> = -- */
	public static final String DOCACTION_None = "--";
	/** Prepare = PR */
	public static final String DOCACTION_Prepare = "PR";
	/** Unlock = XL */
	public static final String DOCACTION_Unlock = "XL";
	/** Wait Complete = WC */
	public static final String DOCACTION_WaitComplete = "WC";
	/** Set Document Action.
		@param DocAction 
		The targeted status of the document
	  */
	public void setDocAction (String DocAction)
	{

		set_Value (COLUMNNAME_DocAction, DocAction);
	}

	/** Get Document Action.
		@return The targeted status of the document
	  */
	public String getDocAction () 
	{
		return (String)get_Value(COLUMNNAME_DocAction);
	}

	/** DocStatus AD_Reference_ID=131 */
	public static final int DOCSTATUS_AD_Reference_ID=131;
	/** Drafted = DR */
	public static final String DOCSTATUS_Drafted = "DR";
	/** Completed = CO */
	public static final String DOCSTATUS_Completed = "CO";
	/** Approved = AP */
	public static final String DOCSTATUS_Approved = "AP";
	/** Not Approved = NA */
	public static final String DOCSTATUS_NotApproved = "NA";
	/** Voided = VO */
	public static final String DOCSTATUS_Voided = "VO";
	/** Invalid = IN */
	public static final String DOCSTATUS_Invalid = "IN";
	/** Reversed = RE */
	public static final String DOCSTATUS_Reversed = "RE";
	/** Closed = CL */
	public static final String DOCSTATUS_Closed = "CL";
	/** Unknown = ?? */
	public static final String DOCSTATUS_Unknown = "??";
	/** In Progress = IP */
	public static final String DOCSTATUS_InProgress = "IP";
	/** Waiting Payment = WP */
	public static final String DOCSTATUS_WaitingPayment = "WP";
	/** Waiting Confirmation = WC */
	public static final String DOCSTATUS_WaitingConfirmation = "WC";
	/** Set Document Status.
		@param DocStatus 
		The current status of the document
	  */
	public void setDocStatus (String DocStatus)
	{

		set_Value (COLUMNNAME_DocStatus, DocStatus);
	}

	/** Get Document Status.
		@return The current status of the document
	  */
	public String getDocStatus () 
	{
		return (String)get_Value(COLUMNNAME_DocStatus);
	}

	/** Set Document No.
		@param DocumentNo 
		Document sequence number of the document
	  */
	public void setDocumentNo (String DocumentNo)
	{
		set_ValueNoCheck (COLUMNNAME_DocumentNo, DocumentNo);
	}

	/** Get Document No.
		@return Document sequence number of the document
	  */
	public String getDocumentNo () 
	{
		return (String)get_Value(COLUMNNAME_DocumentNo);
	}

    /** Get Record ID/ColumnName
        @return ID/ColumnName pair
      */
    public KeyNamePair getKeyNamePair() 
    {
        return new KeyNamePair(get_ID(), getDocumentNo());
    }

	/** Set Comment/Help.
		@param Help 
		Comment or Hint
	  */
	public void setHelp (String Help)
	{
		set_Value (COLUMNNAME_Help, Help);
	}

	/** Get Comment/Help.
		@return Comment or Hint
	  */
	public String getHelp () 
	{
		return (String)get_Value(COLUMNNAME_Help);
	}

	/** Set Approved.
		@param IsApproved 
		Indicates if this document requires approval
	  */
	public void setIsApproved (boolean IsApproved)
	{
		set_ValueNoCheck (COLUMNNAME_IsApproved, Boolean.valueOf(IsApproved));
	}

	/** Get Approved.
		@return Indicates if this document requires approval
	  */
	public boolean isApproved () 
	{
		Object oo = get_Value(COLUMNNAME_IsApproved);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Automatic Update.
		@param IsAutomaticUpdateJP Automatic Update	  */
	public void setIsAutomaticUpdateJP (boolean IsAutomaticUpdateJP)
	{
		set_Value (COLUMNNAME_IsAutomaticUpdateJP, Boolean.valueOf(IsAutomaticUpdateJP));
	}

	/** Get Automatic Update.
		@return Automatic Update	  */
	public boolean isAutomaticUpdateJP () 
	{
		Object oo = get_Value(COLUMNNAME_IsAutomaticUpdateJP);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Contract BP No.
		@param JP_ContractBPNo Contract BP No	  */
	public void setJP_ContractBPNo (String JP_ContractBPNo)
	{
		set_Value (COLUMNNAME_JP_ContractBPNo, JP_ContractBPNo);
	}

	/** Get Contract BP No.
		@return Contract BP No	  */
	public String getJP_ContractBPNo () 
	{
		return (String)get_Value(COLUMNNAME_JP_ContractBPNo);
	}

	public I_JP_ContractCancelCause getJP_ContractCancelCause() throws RuntimeException
    {
		return (I_JP_ContractCancelCause)MTable.get(getCtx(), I_JP_ContractCancelCause.Table_Name)
			.getPO(getJP_ContractCancelCause_ID(), get_TrxName());	}

	/** Set Contract Cancel Cause.
		@param JP_ContractCancelCause_ID Contract Cancel Cause	  */
	public void setJP_ContractCancelCause_ID (int JP_ContractCancelCause_ID)
	{
		if (JP_ContractCancelCause_ID < 1) 
			set_Value (COLUMNNAME_JP_ContractCancelCause_ID, null);
		else 
			set_Value (COLUMNNAME_JP_ContractCancelCause_ID, Integer.valueOf(JP_ContractCancelCause_ID));
	}

	/** Get Contract Cancel Cause.
		@return Contract Cancel Cause	  */
	public int getJP_ContractCancelCause_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ContractCancelCause_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Contract Cancel Date.
		@param JP_ContractCancelDate Contract Cancel Date	  */
	public void setJP_ContractCancelDate (Timestamp JP_ContractCancelDate)
	{
		set_Value (COLUMNNAME_JP_ContractCancelDate, JP_ContractCancelDate);
	}

	/** Get Contract Cancel Date.
		@return Contract Cancel Date	  */
	public Timestamp getJP_ContractCancelDate () 
	{
		return (Timestamp)get_Value(COLUMNNAME_JP_ContractCancelDate);
	}

	/** Set Cancel Deadline.
		@param JP_ContractCancelDeadline Cancel Deadline	  */
	public void setJP_ContractCancelDeadline (Timestamp JP_ContractCancelDeadline)
	{
		set_Value (COLUMNNAME_JP_ContractCancelDeadline, JP_ContractCancelDeadline);
	}

	/** Get Cancel Deadline.
		@return Cancel Deadline	  */
	public Timestamp getJP_ContractCancelDeadline () 
	{
		return (Timestamp)get_Value(COLUMNNAME_JP_ContractCancelDeadline);
	}

	/** Set Contract Cancel Memo.
		@param JP_ContractCancelMemo Contract Cancel Memo	  */
	public void setJP_ContractCancelMemo (String JP_ContractCancelMemo)
	{
		set_Value (COLUMNNAME_JP_ContractCancelMemo, JP_ContractCancelMemo);
	}

	/** Get Contract Cancel Memo.
		@return Contract Cancel Memo	  */
	public String getJP_ContractCancelMemo () 
	{
		return (String)get_Value(COLUMNNAME_JP_ContractCancelMemo);
	}

	/** Set Cancel Offer Date.
		@param JP_ContractCancelOfferDate Cancel Offer Date	  */
	public void setJP_ContractCancelOfferDate (Timestamp JP_ContractCancelOfferDate)
	{
		set_Value (COLUMNNAME_JP_ContractCancelOfferDate, JP_ContractCancelOfferDate);
	}

	/** Get Cancel Offer Date.
		@return Cancel Offer Date	  */
	public Timestamp getJP_ContractCancelOfferDate () 
	{
		return (Timestamp)get_Value(COLUMNNAME_JP_ContractCancelOfferDate);
	}

	public I_JP_ContractCancelTerm getJP_ContractCancelTerm() throws RuntimeException
    {
		return (I_JP_ContractCancelTerm)MTable.get(getCtx(), I_JP_ContractCancelTerm.Table_Name)
			.getPO(getJP_ContractCancelTerm_ID(), get_TrxName());	}

	/** Set Contract Cancel Term.
		@param JP_ContractCancelTerm_ID Contract Cancel Term	  */
	public void setJP_ContractCancelTerm_ID (int JP_ContractCancelTerm_ID)
	{
		if (JP_ContractCancelTerm_ID < 1) 
			set_Value (COLUMNNAME_JP_ContractCancelTerm_ID, null);
		else 
			set_Value (COLUMNNAME_JP_ContractCancelTerm_ID, Integer.valueOf(JP_ContractCancelTerm_ID));
	}

	/** Get Contract Cancel Term.
		@return Contract Cancel Term	  */
	public int getJP_ContractCancelTerm_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ContractCancelTerm_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_AD_User getJP_ContractCancel_SalesRep() throws RuntimeException
    {
		return (org.compiere.model.I_AD_User)MTable.get(getCtx(), org.compiere.model.I_AD_User.Table_Name)
			.getPO(getJP_ContractCancel_SalesRep_ID(), get_TrxName());	}

	/** Set Cancel Sales Rep.
		@param JP_ContractCancel_SalesRep_ID Cancel Sales Rep	  */
	public void setJP_ContractCancel_SalesRep_ID (int JP_ContractCancel_SalesRep_ID)
	{
		if (JP_ContractCancel_SalesRep_ID < 1) 
			set_Value (COLUMNNAME_JP_ContractCancel_SalesRep_ID, null);
		else 
			set_Value (COLUMNNAME_JP_ContractCancel_SalesRep_ID, Integer.valueOf(JP_ContractCancel_SalesRep_ID));
	}

	/** Get Cancel Sales Rep.
		@return Cancel Sales Rep	  */
	public int getJP_ContractCancel_SalesRep_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ContractCancel_SalesRep_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_AD_User getJP_ContractCancel_User() throws RuntimeException
    {
		return (org.compiere.model.I_AD_User)MTable.get(getCtx(), org.compiere.model.I_AD_User.Table_Name)
			.getPO(getJP_ContractCancel_User_ID(), get_TrxName());	}

	/** Set Cancel User.
		@param JP_ContractCancel_User_ID Cancel User	  */
	public void setJP_ContractCancel_User_ID (int JP_ContractCancel_User_ID)
	{
		if (JP_ContractCancel_User_ID < 1) 
			set_Value (COLUMNNAME_JP_ContractCancel_User_ID, null);
		else 
			set_Value (COLUMNNAME_JP_ContractCancel_User_ID, Integer.valueOf(JP_ContractCancel_User_ID));
	}

	/** Get Cancel User.
		@return Cancel User	  */
	public int getJP_ContractCancel_User_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ContractCancel_User_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_JP_ContractCategory getJP_ContractCategory() throws RuntimeException
    {
		return (I_JP_ContractCategory)MTable.get(getCtx(), I_JP_ContractCategory.Table_Name)
			.getPO(getJP_ContractCategory_ID(), get_TrxName());	}

	/** Set Contract Category.
		@param JP_ContractCategory_ID Contract Category	  */
	public void setJP_ContractCategory_ID (int JP_ContractCategory_ID)
	{
		if (JP_ContractCategory_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_ContractCategory_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_ContractCategory_ID, Integer.valueOf(JP_ContractCategory_ID));
	}

	/** Get Contract Category.
		@return Contract Category	  */
	public int getJP_ContractCategory_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ContractCategory_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Contract Doc Amt.
		@param JP_ContractDocAmt Contract Doc Amt	  */
	public void setJP_ContractDocAmt (BigDecimal JP_ContractDocAmt)
	{
		set_Value (COLUMNNAME_JP_ContractDocAmt, JP_ContractDocAmt);
	}

	/** Get Contract Doc Amt.
		@return Contract Doc Amt	  */
	public BigDecimal getJP_ContractDocAmt () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_JP_ContractDocAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Contract Doc Date.
		@param JP_ContractDocDate Contract Doc Date	  */
	public void setJP_ContractDocDate (Timestamp JP_ContractDocDate)
	{
		set_Value (COLUMNNAME_JP_ContractDocDate, JP_ContractDocDate);
	}

	/** Get Contract Doc Date.
		@return Contract Doc Date	  */
	public Timestamp getJP_ContractDocDate () 
	{
		return (Timestamp)get_Value(COLUMNNAME_JP_ContractDocDate);
	}

	/** Set Contract Doc Date(From).
		@param JP_ContractDocDate_From Contract Doc Date(From)	  */
	public void setJP_ContractDocDate_From (Timestamp JP_ContractDocDate_From)
	{
		set_Value (COLUMNNAME_JP_ContractDocDate_From, JP_ContractDocDate_From);
	}

	/** Get Contract Doc Date(From).
		@return Contract Doc Date(From)	  */
	public Timestamp getJP_ContractDocDate_From () 
	{
		return (Timestamp)get_Value(COLUMNNAME_JP_ContractDocDate_From);
	}

	/** Set Contract Doc Date(To).
		@param JP_ContractDocDate_To Contract Doc Date(To)	  */
	public void setJP_ContractDocDate_To (Timestamp JP_ContractDocDate_To)
	{
		set_Value (COLUMNNAME_JP_ContractDocDate_To, JP_ContractDocDate_To);
	}

	/** Get Contract Doc Date(To).
		@return Contract Doc Date(To)	  */
	public Timestamp getJP_ContractDocDate_To () 
	{
		return (Timestamp)get_Value(COLUMNNAME_JP_ContractDocDate_To);
	}

	public I_JP_ContractExtendPeriod getJP_ContractExtendPeriod() throws RuntimeException
    {
		return (I_JP_ContractExtendPeriod)MTable.get(getCtx(), I_JP_ContractExtendPeriod.Table_Name)
			.getPO(getJP_ContractExtendPeriod_ID(), get_TrxName());	}

	/** Set Contract Extend Period.
		@param JP_ContractExtendPeriod_ID Contract Extend Period	  */
	public void setJP_ContractExtendPeriod_ID (int JP_ContractExtendPeriod_ID)
	{
		if (JP_ContractExtendPeriod_ID < 1) 
			set_Value (COLUMNNAME_JP_ContractExtendPeriod_ID, null);
		else 
			set_Value (COLUMNNAME_JP_ContractExtendPeriod_ID, Integer.valueOf(JP_ContractExtendPeriod_ID));
	}

	/** Get Contract Extend Period.
		@return Contract Extend Period	  */
	public int getJP_ContractExtendPeriod_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ContractExtendPeriod_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Monthly Expense Amt.
		@param JP_ContractMonthlyExpenseAmt Monthly Expense Amt	  */
	public void setJP_ContractMonthlyExpenseAmt (BigDecimal JP_ContractMonthlyExpenseAmt)
	{
		set_Value (COLUMNNAME_JP_ContractMonthlyExpenseAmt, JP_ContractMonthlyExpenseAmt);
	}

	/** Get Monthly Expense Amt.
		@return Monthly Expense Amt	  */
	public BigDecimal getJP_ContractMonthlyExpenseAmt () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_JP_ContractMonthlyExpenseAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Monthly Revenue Amt.
		@param JP_ContractMonthlyRevenueAmt Monthly Revenue Amt	  */
	public void setJP_ContractMonthlyRevenueAmt (BigDecimal JP_ContractMonthlyRevenueAmt)
	{
		set_Value (COLUMNNAME_JP_ContractMonthlyRevenueAmt, JP_ContractMonthlyRevenueAmt);
	}

	/** Get Monthly Revenue Amt.
		@return Monthly Revenue Amt	  */
	public BigDecimal getJP_ContractMonthlyRevenueAmt () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_JP_ContractMonthlyRevenueAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Contract No.
		@param JP_ContractNo Contract No	  */
	public void setJP_ContractNo (String JP_ContractNo)
	{
		set_Value (COLUMNNAME_JP_ContractNo, JP_ContractNo);
	}

	/** Get Contract No.
		@return Contract No	  */
	public String getJP_ContractNo () 
	{
		return (String)get_Value(COLUMNNAME_JP_ContractNo);
	}

	/** Set Contract Period Date(From).
		@param JP_ContractPeriodDate_From Contract Period Date(From)	  */
	public void setJP_ContractPeriodDate_From (Timestamp JP_ContractPeriodDate_From)
	{
		set_Value (COLUMNNAME_JP_ContractPeriodDate_From, JP_ContractPeriodDate_From);
	}

	/** Get Contract Period Date(From).
		@return Contract Period Date(From)	  */
	public Timestamp getJP_ContractPeriodDate_From () 
	{
		return (Timestamp)get_Value(COLUMNNAME_JP_ContractPeriodDate_From);
	}

	/** Set Contract Period Date(To).
		@param JP_ContractPeriodDate_To Contract Period Date(To)	  */
	public void setJP_ContractPeriodDate_To (Timestamp JP_ContractPeriodDate_To)
	{
		set_Value (COLUMNNAME_JP_ContractPeriodDate_To, JP_ContractPeriodDate_To);
	}

	/** Get Contract Period Date(To).
		@return Contract Period Date(To)	  */
	public Timestamp getJP_ContractPeriodDate_To () 
	{
		return (Timestamp)get_Value(COLUMNNAME_JP_ContractPeriodDate_To);
	}

	/** Prepare = PR */
	public static final String JP_CONTRACTSTATUS_Prepare = "PR";
	/** Under Contract = UC */
	public static final String JP_CONTRACTSTATUS_UnderContract = "UC";
	/** Expiration of Contract = EC */
	public static final String JP_CONTRACTSTATUS_ExpirationOfContract = "EC";
	/** Invalid = IN */
	public static final String JP_CONTRACTSTATUS_Invalid = "IN";
	/** Set Contract Status.
		@param JP_ContractStatus Contract Status	  */
	public void setJP_ContractStatus (String JP_ContractStatus)
	{

		set_Value (COLUMNNAME_JP_ContractStatus, JP_ContractStatus);
	}

	/** Get Contract Status.
		@return Contract Status	  */
	public String getJP_ContractStatus () 
	{
		return (String)get_Value(COLUMNNAME_JP_ContractStatus);
	}

	/** Set Updated date to Expiration of contract.
		@param JP_ContractStatus_EC_Date Updated date to Expiration of contract	  */
	public void setJP_ContractStatus_EC_Date (Timestamp JP_ContractStatus_EC_Date)
	{
		set_Value (COLUMNNAME_JP_ContractStatus_EC_Date, JP_ContractStatus_EC_Date);
	}

	/** Get Updated date to Expiration of contract.
		@return Updated date to Expiration of contract	  */
	public Timestamp getJP_ContractStatus_EC_Date () 
	{
		return (Timestamp)get_Value(COLUMNNAME_JP_ContractStatus_EC_Date);
	}

	/** Set Updated date to Invalid.
		@param JP_ContractStatus_IN_Date Updated date to Invalid	  */
	public void setJP_ContractStatus_IN_Date (Timestamp JP_ContractStatus_IN_Date)
	{
		set_Value (COLUMNNAME_JP_ContractStatus_IN_Date, JP_ContractStatus_IN_Date);
	}

	/** Get Updated date to Invalid.
		@return Updated date to Invalid	  */
	public Timestamp getJP_ContractStatus_IN_Date () 
	{
		return (Timestamp)get_Value(COLUMNNAME_JP_ContractStatus_IN_Date);
	}

	/** Set Updated date to Under Contract.
		@param JP_ContractStatus_UC_Date Updated date to Under Contract	  */
	public void setJP_ContractStatus_UC_Date (Timestamp JP_ContractStatus_UC_Date)
	{
		set_Value (COLUMNNAME_JP_ContractStatus_UC_Date, JP_ContractStatus_UC_Date);
	}

	/** Get Updated date to Under Contract.
		@return Updated date to Under Contract	  */
	public Timestamp getJP_ContractStatus_UC_Date () 
	{
		return (Timestamp)get_Value(COLUMNNAME_JP_ContractStatus_UC_Date);
	}

	public I_JP_ContractT getJP_ContractT() throws RuntimeException
    {
		return (I_JP_ContractT)MTable.get(getCtx(), I_JP_ContractT.Table_Name)
			.getPO(getJP_ContractT_ID(), get_TrxName());	}

	/** Set Contract Doc Template.
		@param JP_ContractT_ID Contract Doc Template	  */
	public void setJP_ContractT_ID (int JP_ContractT_ID)
	{
		if (JP_ContractT_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_ContractT_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_ContractT_ID, Integer.valueOf(JP_ContractT_ID));
	}

	/** Get Contract Doc Template.
		@return Contract Doc Template	  */
	public int getJP_ContractT_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ContractT_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Period Contract = PDC */
	public static final String JP_CONTRACTTYPE_PeriodContract = "PDC";
	/** Spot Contract = STC */
	public static final String JP_CONTRACTTYPE_SpotContract = "STC";
	/** General Contract = GLC */
	public static final String JP_CONTRACTTYPE_GeneralContract = "GLC";
	/** Set Contract Type.
		@param JP_ContractType Contract Type	  */
	public void setJP_ContractType (String JP_ContractType)
	{

		set_ValueNoCheck (COLUMNNAME_JP_ContractType, JP_ContractType);
	}

	/** Get Contract Type.
		@return Contract Type	  */
	public String getJP_ContractType () 
	{
		return (String)get_Value(COLUMNNAME_JP_ContractType);
	}

	/** Set Contract Document.
		@param JP_Contract_ID Contract Document	  */
	public void setJP_Contract_ID (int JP_Contract_ID)
	{
		if (JP_Contract_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_Contract_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_Contract_ID, Integer.valueOf(JP_Contract_ID));
	}

	/** Get Contract Document.
		@return Contract Document	  */
	public int getJP_Contract_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Contract_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_JP_Contract getJP_Contract_Link() throws RuntimeException
    {
		return (I_JP_Contract)MTable.get(getCtx(), I_JP_Contract.Table_Name)
			.getPO(getJP_Contract_Link_ID(), get_TrxName());	}

	/** Set Linked Contract.
		@param JP_Contract_Link_ID Linked Contract	  */
	public void setJP_Contract_Link_ID (int JP_Contract_Link_ID)
	{
		if (JP_Contract_Link_ID < 1) 
			set_Value (COLUMNNAME_JP_Contract_Link_ID, null);
		else 
			set_Value (COLUMNNAME_JP_Contract_Link_ID, Integer.valueOf(JP_Contract_Link_ID));
	}

	/** Get Linked Contract.
		@return Linked Contract	  */
	public int getJP_Contract_Link_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Contract_Link_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_JP_Contract getJP_Contract_Parent() throws RuntimeException
    {
		return (I_JP_Contract)MTable.get(getCtx(), I_JP_Contract.Table_Name)
			.getPO(getJP_Contract_Parent_ID(), get_TrxName());	}

	/** Set Parent Contract Doc.
		@param JP_Contract_Parent_ID Parent Contract Doc	  */
	public void setJP_Contract_Parent_ID (int JP_Contract_Parent_ID)
	{
		if (JP_Contract_Parent_ID < 1) 
			set_Value (COLUMNNAME_JP_Contract_Parent_ID, null);
		else 
			set_Value (COLUMNNAME_JP_Contract_Parent_ID, Integer.valueOf(JP_Contract_Parent_ID));
	}

	/** Get Parent Contract Doc.
		@return Parent Contract Doc	  */
	public int getJP_Contract_Parent_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Contract_Parent_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Contract Doc(UU).
		@param JP_Contract_UU Contract Doc(UU)	  */
	public void setJP_Contract_UU (String JP_Contract_UU)
	{
		set_ValueNoCheck (COLUMNNAME_JP_Contract_UU, JP_Contract_UU);
	}

	/** Get Contract Doc(UU).
		@return Contract Doc(UU)	  */
	public String getJP_Contract_UU () 
	{
		return (String)get_Value(COLUMNNAME_JP_Contract_UU);
	}

	public I_JP_Estimation getJP_Estimation() throws RuntimeException
    {
		return (I_JP_Estimation)MTable.get(getCtx(), I_JP_Estimation.Table_Name)
			.getPO(getJP_Estimation_ID(), get_TrxName());	}

	/** Set Estimation & Handwritten.
		@param JP_Estimation_ID Estimation & Handwritten	  */
	public void setJP_Estimation_ID (int JP_Estimation_ID)
	{
		if (JP_Estimation_ID < 1) 
			set_Value (COLUMNNAME_JP_Estimation_ID, null);
		else 
			set_Value (COLUMNNAME_JP_Estimation_ID, Integer.valueOf(JP_Estimation_ID));
	}

	/** Get Estimation & Handwritten.
		@return Estimation & Handwritten	  */
	public int getJP_Estimation_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Estimation_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Name.
		@param Name 
		Alphanumeric identifier of the entity
	  */
	public void setName (String Name)
	{
		set_Value (COLUMNNAME_Name, Name);
	}

	/** Get Name.
		@return Alphanumeric identifier of the entity
	  */
	public String getName () 
	{
		return (String)get_Value(COLUMNNAME_Name);
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

	/** Set Posted.
		@param Posted 
		Posting status
	  */
	public void setPosted (boolean Posted)
	{
		set_Value (COLUMNNAME_Posted, Boolean.valueOf(Posted));
	}

	/** Get Posted.
		@return Posting status
	  */
	public boolean isPosted () 
	{
		Object oo = get_Value(COLUMNNAME_Posted);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
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

	/** Set Processed On.
		@param ProcessedOn 
		The date+time (expressed in decimal format) when the document has been processed
	  */
	public void setProcessedOn (BigDecimal ProcessedOn)
	{
		set_Value (COLUMNNAME_ProcessedOn, ProcessedOn);
	}

	/** Get Processed On.
		@return The date+time (expressed in decimal format) when the document has been processed
	  */
	public BigDecimal getProcessedOn () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_ProcessedOn);
		if (bd == null)
			 return Env.ZERO;
		return bd;
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
}