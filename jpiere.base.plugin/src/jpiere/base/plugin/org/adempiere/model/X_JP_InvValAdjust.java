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

/** Generated Model for JP_InvValAdjust
 *  @author iDempiere (generated) 
 *  @version Release 3.1 - $Id$ */
public class X_JP_InvValAdjust extends PO implements I_JP_InvValAdjust, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20160411L;

    /** Standard Constructor */
    public X_JP_InvValAdjust (Properties ctx, int JP_InvValAdjust_ID, String trxName)
    {
      super (ctx, JP_InvValAdjust_ID, trxName);
      /** if (JP_InvValAdjust_ID == 0)
        {
			setC_Currency_ID (0);
			setC_DocType_ID (0);
			setDateAcct (new Timestamp( System.currentTimeMillis() ));
// @SQL=SELECT DATE_TRUNC('month', TO_DATE('@#Date@', 'YYYY-MM-DD'))-1
			setDateValue (new Timestamp( System.currentTimeMillis() ));
// @SQL=SELECT DATE_TRUNC('month', TO_DATE('@#Date@', 'YYYY-MM-DD'))-1
			setDocAction (null);
// CO
			setDocStatus (null);
// DR
			setDocumentNo (null);
			setIsApproved (false);
			setJP_InvValAdjust_ID (0);
			setJP_InvValProfile_ID (0);
			setPosted (false);
			setProcessed (false);
			setTotalLines (Env.ZERO);
// 0
        } */
    }

    /** Load Constructor */
    public X_JP_InvValAdjust (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_JP_InvValAdjust[")
        .append(get_ID()).append("]");
      return sb.toString();
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
			set_ValueNoCheck (COLUMNNAME_C_DocType_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_DocType_ID, Integer.valueOf(C_DocType_ID));
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

	/** Set Valuation Date.
		@param DateValue 
		Date of valuation
	  */
	public void setDateValue (Timestamp DateValue)
	{
		set_Value (COLUMNNAME_DateValue, DateValue);
	}

	/** Get Valuation Date.
		@return Date of valuation
	  */
	public Timestamp getDateValue () 
	{
		return (Timestamp)get_Value(COLUMNNAME_DateValue);
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

	public org.compiere.model.I_GL_Journal getGL_Journal() throws RuntimeException
    {
		return (org.compiere.model.I_GL_Journal)MTable.get(getCtx(), org.compiere.model.I_GL_Journal.Table_Name)
			.getPO(getGL_Journal_ID(), get_TrxName());	}

	/** Set Journal.
		@param GL_Journal_ID 
		General Ledger Journal
	  */
	public void setGL_Journal_ID (int GL_Journal_ID)
	{
		if (GL_Journal_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_GL_Journal_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_GL_Journal_ID, Integer.valueOf(GL_Journal_ID));
	}

	/** Get Journal.
		@return General Ledger Journal
	  */
	public int getGL_Journal_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_GL_Journal_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
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

	/** Set Inventory Valuation Adjust Doc.
		@param JP_InvValAdjust_ID Inventory Valuation Adjust Doc	  */
	public void setJP_InvValAdjust_ID (int JP_InvValAdjust_ID)
	{
		if (JP_InvValAdjust_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_InvValAdjust_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_InvValAdjust_ID, Integer.valueOf(JP_InvValAdjust_ID));
	}

	/** Get Inventory Valuation Adjust Doc.
		@return Inventory Valuation Adjust Doc	  */
	public int getJP_InvValAdjust_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_InvValAdjust_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set JP_InvValAdjust_UU.
		@param JP_InvValAdjust_UU JP_InvValAdjust_UU	  */
	public void setJP_InvValAdjust_UU (String JP_InvValAdjust_UU)
	{
		set_ValueNoCheck (COLUMNNAME_JP_InvValAdjust_UU, JP_InvValAdjust_UU);
	}

	/** Get JP_InvValAdjust_UU.
		@return JP_InvValAdjust_UU	  */
	public String getJP_InvValAdjust_UU () 
	{
		return (String)get_Value(COLUMNNAME_JP_InvValAdjust_UU);
	}

	public I_JP_InvValCal getJP_InvValCal() throws RuntimeException
    {
		return (I_JP_InvValCal)MTable.get(getCtx(), I_JP_InvValCal.Table_Name)
			.getPO(getJP_InvValCal_ID(), get_TrxName());	}

	/** Set Inventory Valuation Calculate Doc.
		@param JP_InvValCal_ID Inventory Valuation Calculate Doc	  */
	public void setJP_InvValCal_ID (int JP_InvValCal_ID)
	{
		if (JP_InvValCal_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_InvValCal_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_InvValCal_ID, Integer.valueOf(JP_InvValCal_ID));
	}

	/** Get Inventory Valuation Calculate Doc.
		@return Inventory Valuation Calculate Doc	  */
	public int getJP_InvValCal_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_InvValCal_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_JP_InvValProfile getJP_InvValProfile() throws RuntimeException
    {
		return (I_JP_InvValProfile)MTable.get(getCtx(), I_JP_InvValProfile.Table_Name)
			.getPO(getJP_InvValProfile_ID(), get_TrxName());	}

	/** Set Inventory Valuation Profile.
		@param JP_InvValProfile_ID Inventory Valuation Profile	  */
	public void setJP_InvValProfile_ID (int JP_InvValProfile_ID)
	{
		if (JP_InvValProfile_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_InvValProfile_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_InvValProfile_ID, Integer.valueOf(JP_InvValProfile_ID));
	}

	/** Get Inventory Valuation Profile.
		@return Inventory Valuation Profile	  */
	public int getJP_InvValProfile_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_InvValProfile_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Processed Time.
		@param JP_ProcessedTime1 Processed Time	  */
	public void setJP_ProcessedTime1 (Timestamp JP_ProcessedTime1)
	{
		set_Value (COLUMNNAME_JP_ProcessedTime1, JP_ProcessedTime1);
	}

	/** Get Processed Time.
		@return Processed Time	  */
	public Timestamp getJP_ProcessedTime1 () 
	{
		return (Timestamp)get_Value(COLUMNNAME_JP_ProcessedTime1);
	}

	/** Set Processed Time.
		@param JP_ProcessedTime2 Processed Time	  */
	public void setJP_ProcessedTime2 (Timestamp JP_ProcessedTime2)
	{
		set_Value (COLUMNNAME_JP_ProcessedTime2, JP_ProcessedTime2);
	}

	/** Get Processed Time.
		@return Processed Time	  */
	public Timestamp getJP_ProcessedTime2 () 
	{
		return (Timestamp)get_Value(COLUMNNAME_JP_ProcessedTime2);
	}

	/** Set Processed Time.
		@param JP_ProcessedTime3 Processed Time	  */
	public void setJP_ProcessedTime3 (Timestamp JP_ProcessedTime3)
	{
		set_Value (COLUMNNAME_JP_ProcessedTime3, JP_ProcessedTime3);
	}

	/** Get Processed Time.
		@return Processed Time	  */
	public Timestamp getJP_ProcessedTime3 () 
	{
		return (Timestamp)get_Value(COLUMNNAME_JP_ProcessedTime3);
	}

	/** Set Process Now.
		@param JP_Processing1 Process Now	  */
	public void setJP_Processing1 (String JP_Processing1)
	{
		set_Value (COLUMNNAME_JP_Processing1, JP_Processing1);
	}

	/** Get Process Now.
		@return Process Now	  */
	public String getJP_Processing1 () 
	{
		return (String)get_Value(COLUMNNAME_JP_Processing1);
	}

	/** Set Process Now.
		@param JP_Processing2 Process Now	  */
	public void setJP_Processing2 (String JP_Processing2)
	{
		set_Value (COLUMNNAME_JP_Processing2, JP_Processing2);
	}

	/** Get Process Now.
		@return Process Now	  */
	public String getJP_Processing2 () 
	{
		return (String)get_Value(COLUMNNAME_JP_Processing2);
	}

	/** Set Process Now.
		@param JP_Processing3 Process Now	  */
	public void setJP_Processing3 (String JP_Processing3)
	{
		set_Value (COLUMNNAME_JP_Processing3, JP_Processing3);
	}

	/** Get Process Now.
		@return Process Now	  */
	public String getJP_Processing3 () 
	{
		return (String)get_Value(COLUMNNAME_JP_Processing3);
	}

	/** Set Posted.
		@param Posted 
		Posting status
	  */
	public void setPosted (boolean Posted)
	{
		set_ValueNoCheck (COLUMNNAME_Posted, Boolean.valueOf(Posted));
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

	/** Set Sales Representative.
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

	/** Get Sales Representative.
		@return Sales Representative or Company Agent
	  */
	public int getSalesRep_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_SalesRep_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Total Lines.
		@param TotalLines 
		Total of all document lines
	  */
	public void setTotalLines (BigDecimal TotalLines)
	{
		set_ValueNoCheck (COLUMNNAME_TotalLines, TotalLines);
	}

	/** Get Total Lines.
		@return Total of all document lines
	  */
	public BigDecimal getTotalLines () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_TotalLines);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}
}