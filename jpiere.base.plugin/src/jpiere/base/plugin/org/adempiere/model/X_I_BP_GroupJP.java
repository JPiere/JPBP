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
import java.util.Properties;
import org.compiere.model.*;
import org.compiere.util.Env;

/** Generated Model for I_BP_GroupJP
 *  @author iDempiere (generated) 
 *  @version Release 5.1 - $Id$ */
public class X_I_BP_GroupJP extends PO implements I_I_BP_GroupJP, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20180607L;

    /** Standard Constructor */
    public X_I_BP_GroupJP (Properties ctx, int I_BP_GroupJP_ID, String trxName)
    {
      super (ctx, I_BP_GroupJP_ID, trxName);
      /** if (I_BP_GroupJP_ID == 0)
        {
			setI_BP_GroupJP_ID (0);
			setI_IsActiveJP (true);
// Y
			setIsConfidentialInfo (false);
// N
			setIsDefault (false);
// N
        } */
    }

    /** Load Constructor */
    public X_I_BP_GroupJP (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_I_BP_GroupJP[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public org.compiere.model.I_AD_PrintColor getAD_PrintColor() throws RuntimeException
    {
		return (org.compiere.model.I_AD_PrintColor)MTable.get(getCtx(), org.compiere.model.I_AD_PrintColor.Table_Name)
			.getPO(getAD_PrintColor_ID(), get_TrxName());	}

	/** Set Print Color.
		@param AD_PrintColor_ID 
		Color used for printing and display
	  */
	public void setAD_PrintColor_ID (int AD_PrintColor_ID)
	{
		if (AD_PrintColor_ID < 1) 
			set_Value (COLUMNNAME_AD_PrintColor_ID, null);
		else 
			set_Value (COLUMNNAME_AD_PrintColor_ID, Integer.valueOf(AD_PrintColor_ID));
	}

	/** Get Print Color.
		@return Color used for printing and display
	  */
	public int getAD_PrintColor_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_PrintColor_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_AcctSchema getC_AcctSchema() throws RuntimeException
    {
		return (org.compiere.model.I_C_AcctSchema)MTable.get(getCtx(), org.compiere.model.I_C_AcctSchema.Table_Name)
			.getPO(getC_AcctSchema_ID(), get_TrxName());	}

	/** Set Accounting Schema.
		@param C_AcctSchema_ID 
		Rules for accounting
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
	public int getC_AcctSchema_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_AcctSchema_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_BP_Group getC_BP_Group() throws RuntimeException
    {
		return (org.compiere.model.I_C_BP_Group)MTable.get(getCtx(), org.compiere.model.I_C_BP_Group.Table_Name)
			.getPO(getC_BP_Group_ID(), get_TrxName());	}

	/** Set BPartner Group.
		@param C_BP_Group_ID 
		Business Partner Group
	  */
	public void setC_BP_Group_ID (int C_BP_Group_ID)
	{
		if (C_BP_Group_ID < 1) 
			set_Value (COLUMNNAME_C_BP_Group_ID, null);
		else 
			set_Value (COLUMNNAME_C_BP_Group_ID, Integer.valueOf(C_BP_Group_ID));
	}

	/** Get BPartner Group.
		@return Business Partner Group
	  */
	public int getC_BP_Group_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_BP_Group_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Dunning getC_Dunning() throws RuntimeException
    {
		return (org.compiere.model.I_C_Dunning)MTable.get(getCtx(), org.compiere.model.I_C_Dunning.Table_Name)
			.getPO(getC_Dunning_ID(), get_TrxName());	}

	/** Set Dunning.
		@param C_Dunning_ID 
		Dunning Rules for overdue invoices
	  */
	public void setC_Dunning_ID (int C_Dunning_ID)
	{
		if (C_Dunning_ID < 1) 
			set_Value (COLUMNNAME_C_Dunning_ID, null);
		else 
			set_Value (COLUMNNAME_C_Dunning_ID, Integer.valueOf(C_Dunning_ID));
	}

	/** Get Dunning.
		@return Dunning Rules for overdue invoices
	  */
	public int getC_Dunning_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Dunning_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_ValidCombination getC_Prepayment_A() throws RuntimeException
    {
		return (I_C_ValidCombination)MTable.get(getCtx(), I_C_ValidCombination.Table_Name)
			.getPO(getC_Prepayment_Acct(), get_TrxName());	}

	/** Set Customer Prepayment.
		@param C_Prepayment_Acct 
		Account for customer prepayments
	  */
	public void setC_Prepayment_Acct (int C_Prepayment_Acct)
	{
		set_Value (COLUMNNAME_C_Prepayment_Acct, Integer.valueOf(C_Prepayment_Acct));
	}

	/** Get Customer Prepayment.
		@return Account for customer prepayments
	  */
	public int getC_Prepayment_Acct () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Prepayment_Acct);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_ValidCombination getC_Receivable_A() throws RuntimeException
    {
		return (I_C_ValidCombination)MTable.get(getCtx(), I_C_ValidCombination.Table_Name)
			.getPO(getC_Receivable_Acct(), get_TrxName());	}

	/** Set Customer Receivables.
		@param C_Receivable_Acct 
		Account for Customer Receivables
	  */
	public void setC_Receivable_Acct (int C_Receivable_Acct)
	{
		set_Value (COLUMNNAME_C_Receivable_Acct, Integer.valueOf(C_Receivable_Acct));
	}

	/** Get Customer Receivables.
		@return Account for Customer Receivables
	  */
	public int getC_Receivable_Acct () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Receivable_Acct);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Credit Watch %.
		@param CreditWatchPercent 
		Credit Watch - Percent of Credit Limit when OK switches to Watch
	  */
	public void setCreditWatchPercent (BigDecimal CreditWatchPercent)
	{
		set_Value (COLUMNNAME_CreditWatchPercent, CreditWatchPercent);
	}

	/** Get Credit Watch %.
		@return Credit Watch - Percent of Credit Limit when OK switches to Watch
	  */
	public BigDecimal getCreditWatchPercent () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_CreditWatchPercent);
		if (bd == null)
			 return Env.ZERO;
		return bd;
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

	/** Set I_BP_GroupJP.
		@param I_BP_GroupJP_ID I_BP_GroupJP	  */
	public void setI_BP_GroupJP_ID (int I_BP_GroupJP_ID)
	{
		if (I_BP_GroupJP_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_I_BP_GroupJP_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_I_BP_GroupJP_ID, Integer.valueOf(I_BP_GroupJP_ID));
	}

	/** Get I_BP_GroupJP.
		@return I_BP_GroupJP	  */
	public int getI_BP_GroupJP_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_I_BP_GroupJP_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set I_BP_GroupJP_UU.
		@param I_BP_GroupJP_UU I_BP_GroupJP_UU	  */
	public void setI_BP_GroupJP_UU (String I_BP_GroupJP_UU)
	{
		set_ValueNoCheck (COLUMNNAME_I_BP_GroupJP_UU, I_BP_GroupJP_UU);
	}

	/** Get I_BP_GroupJP_UU.
		@return I_BP_GroupJP_UU	  */
	public String getI_BP_GroupJP_UU () 
	{
		return (String)get_Value(COLUMNNAME_I_BP_GroupJP_UU);
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

	/** Set Active(For Import).
		@param I_IsActiveJP 
		Active flag for Import Date
	  */
	public void setI_IsActiveJP (boolean I_IsActiveJP)
	{
		set_Value (COLUMNNAME_I_IsActiveJP, Boolean.valueOf(I_IsActiveJP));
	}

	/** Get Active(For Import).
		@return Active flag for Import Date
	  */
	public boolean isI_IsActiveJP () 
	{
		Object oo = get_Value(COLUMNNAME_I_IsActiveJP);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
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

	/** Set Confidential Info.
		@param IsConfidentialInfo 
		Can enter confidential information
	  */
	public void setIsConfidentialInfo (boolean IsConfidentialInfo)
	{
		set_Value (COLUMNNAME_IsConfidentialInfo, Boolean.valueOf(IsConfidentialInfo));
	}

	/** Get Confidential Info.
		@return Can enter confidential information
	  */
	public boolean isConfidentialInfo () 
	{
		Object oo = get_Value(COLUMNNAME_IsConfidentialInfo);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Default.
		@param IsDefault 
		Default value
	  */
	public void setIsDefault (boolean IsDefault)
	{
		set_Value (COLUMNNAME_IsDefault, Boolean.valueOf(IsDefault));
	}

	/** Get Default.
		@return Default value
	  */
	public boolean isDefault () 
	{
		Object oo = get_Value(COLUMNNAME_IsDefault);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Accounting Schema(Name).
		@param JP_AcctSchema_Name Accounting Schema(Name)	  */
	public void setJP_AcctSchema_Name (String JP_AcctSchema_Name)
	{
		set_Value (COLUMNNAME_JP_AcctSchema_Name, JP_AcctSchema_Name);
	}

	/** Get Accounting Schema(Name).
		@return Accounting Schema(Name)	  */
	public String getJP_AcctSchema_Name () 
	{
		return (String)get_Value(COLUMNNAME_JP_AcctSchema_Name);
	}

	/** Set Customer Prepayment(Search Key).
		@param JP_C_PrePayment_Acct_Value Customer Prepayment(Search Key)	  */
	public void setJP_C_PrePayment_Acct_Value (String JP_C_PrePayment_Acct_Value)
	{
		set_Value (COLUMNNAME_JP_C_PrePayment_Acct_Value, JP_C_PrePayment_Acct_Value);
	}

	/** Get Customer Prepayment(Search Key).
		@return Customer Prepayment(Search Key)	  */
	public String getJP_C_PrePayment_Acct_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_C_PrePayment_Acct_Value);
	}

	/** Set Discount Schema(Name).
		@param JP_DiscountSchema_Name Discount Schema(Name)	  */
	public void setJP_DiscountSchema_Name (String JP_DiscountSchema_Name)
	{
		set_Value (COLUMNNAME_JP_DiscountSchema_Name, JP_DiscountSchema_Name);
	}

	/** Get Discount Schema(Name).
		@return Discount Schema(Name)	  */
	public String getJP_DiscountSchema_Name () 
	{
		return (String)get_Value(COLUMNNAME_JP_DiscountSchema_Name);
	}

	/** Set Dunning(Name).
		@param JP_Dunning_Name Dunning(Name)	  */
	public void setJP_Dunning_Name (String JP_Dunning_Name)
	{
		set_Value (COLUMNNAME_JP_Dunning_Name, JP_Dunning_Name);
	}

	/** Get Dunning(Name).
		@return Dunning(Name)	  */
	public String getJP_Dunning_Name () 
	{
		return (String)get_Value(COLUMNNAME_JP_Dunning_Name);
	}

	/** Set Vendor Liability(Search Key).
		@param JP_Liability_Acct_Value Vendor Liability(Search Key)	  */
	public void setJP_Liability_Acct_Value (String JP_Liability_Acct_Value)
	{
		set_Value (COLUMNNAME_JP_Liability_Acct_Value, JP_Liability_Acct_Value);
	}

	/** Get Vendor Liability(Search Key).
		@return Vendor Liability(Search Key)	  */
	public String getJP_Liability_Acct_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_Liability_Acct_Value);
	}

	/** Set Not-invoiced Receipts(Search Key).
		@param JP_NotInvoicedReceipts_Value Not-invoiced Receipts(Search Key)	  */
	public void setJP_NotInvoicedReceipts_Value (String JP_NotInvoicedReceipts_Value)
	{
		set_Value (COLUMNNAME_JP_NotInvoicedReceipts_Value, JP_NotInvoicedReceipts_Value);
	}

	/** Get Not-invoiced Receipts(Search Key).
		@return Not-invoiced Receipts(Search Key)	  */
	public String getJP_NotInvoicedReceipts_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_NotInvoicedReceipts_Value);
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

	/** Set PO Discount Schema(Name).
		@param JP_PO_DiscountSchema_Name PO Discount Schema(Name)	  */
	public void setJP_PO_DiscountSchema_Name (String JP_PO_DiscountSchema_Name)
	{
		set_Value (COLUMNNAME_JP_PO_DiscountSchema_Name, JP_PO_DiscountSchema_Name);
	}

	/** Get PO Discount Schema(Name).
		@return PO Discount Schema(Name)	  */
	public String getJP_PO_DiscountSchema_Name () 
	{
		return (String)get_Value(COLUMNNAME_JP_PO_DiscountSchema_Name);
	}

	/** Set Purchase Pricelist(Name).
		@param JP_PO_PriceList_Name Purchase Pricelist(Name)	  */
	public void setJP_PO_PriceList_Name (String JP_PO_PriceList_Name)
	{
		set_Value (COLUMNNAME_JP_PO_PriceList_Name, JP_PO_PriceList_Name);
	}

	/** Get Purchase Pricelist(Name).
		@return Purchase Pricelist(Name)	  */
	public String getJP_PO_PriceList_Name () 
	{
		return (String)get_Value(COLUMNNAME_JP_PO_PriceList_Name);
	}

	/** Set Payment Discount Expense(Search key).
		@param JP_PayDiscount_Exp_Value Payment Discount Expense(Search key)	  */
	public void setJP_PayDiscount_Exp_Value (String JP_PayDiscount_Exp_Value)
	{
		set_Value (COLUMNNAME_JP_PayDiscount_Exp_Value, JP_PayDiscount_Exp_Value);
	}

	/** Get Payment Discount Expense(Search key).
		@return Payment Discount Expense(Search key)	  */
	public String getJP_PayDiscount_Exp_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_PayDiscount_Exp_Value);
	}

	/** Set Payment Discount Revenue(Search key).
		@param JP_PayDiscount_Rev_Value Payment Discount Revenue(Search key)	  */
	public void setJP_PayDiscount_Rev_Value (String JP_PayDiscount_Rev_Value)
	{
		set_Value (COLUMNNAME_JP_PayDiscount_Rev_Value, JP_PayDiscount_Rev_Value);
	}

	/** Get Payment Discount Revenue(Search key).
		@return Payment Discount Revenue(Search key)	  */
	public String getJP_PayDiscount_Rev_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_PayDiscount_Rev_Value);
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

	/** Set Print Color(Name).
		@param JP_PrintColor_Name Print Color(Name)	  */
	public void setJP_PrintColor_Name (String JP_PrintColor_Name)
	{
		set_Value (COLUMNNAME_JP_PrintColor_Name, JP_PrintColor_Name);
	}

	/** Get Print Color(Name).
		@return Print Color(Name)	  */
	public String getJP_PrintColor_Name () 
	{
		return (String)get_Value(COLUMNNAME_JP_PrintColor_Name);
	}

	/** Set Customer Receivables(Search key).
		@param JP_Receivable_Acct_Value Customer Receivables(Search key)	  */
	public void setJP_Receivable_Acct_Value (String JP_Receivable_Acct_Value)
	{
		set_Value (COLUMNNAME_JP_Receivable_Acct_Value, JP_Receivable_Acct_Value);
	}

	/** Get Customer Receivables(Search key).
		@return Customer Receivables(Search key)	  */
	public String getJP_Receivable_Acct_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_Receivable_Acct_Value);
	}

	/** Set Vendor Prepayment(Search Key).
		@param JP_V_Prepayment_Acct_Value Vendor Prepayment(Search Key)	  */
	public void setJP_V_Prepayment_Acct_Value (String JP_V_Prepayment_Acct_Value)
	{
		set_Value (COLUMNNAME_JP_V_Prepayment_Acct_Value, JP_V_Prepayment_Acct_Value);
	}

	/** Get Vendor Prepayment(Search Key).
		@return Vendor Prepayment(Search Key)	  */
	public String getJP_V_Prepayment_Acct_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_V_Prepayment_Acct_Value);
	}

	/** Set Write-off(Search Key).
		@param JP_WriteOff_Acct_Value Write-off(Search Key)	  */
	public void setJP_WriteOff_Acct_Value (String JP_WriteOff_Acct_Value)
	{
		set_Value (COLUMNNAME_JP_WriteOff_Acct_Value, JP_WriteOff_Acct_Value);
	}

	/** Get Write-off(Search Key).
		@return Write-off(Search Key)	  */
	public String getJP_WriteOff_Acct_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_WriteOff_Acct_Value);
	}

	public org.compiere.model.I_M_DiscountSchema getM_DiscountSchema() throws RuntimeException
    {
		return (org.compiere.model.I_M_DiscountSchema)MTable.get(getCtx(), org.compiere.model.I_M_DiscountSchema.Table_Name)
			.getPO(getM_DiscountSchema_ID(), get_TrxName());	}

	/** Set Discount Schema.
		@param M_DiscountSchema_ID 
		Schema to calculate the trade discount percentage
	  */
	public void setM_DiscountSchema_ID (int M_DiscountSchema_ID)
	{
		if (M_DiscountSchema_ID < 1) 
			set_Value (COLUMNNAME_M_DiscountSchema_ID, null);
		else 
			set_Value (COLUMNNAME_M_DiscountSchema_ID, Integer.valueOf(M_DiscountSchema_ID));
	}

	/** Get Discount Schema.
		@return Schema to calculate the trade discount percentage
	  */
	public int getM_DiscountSchema_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_DiscountSchema_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
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

	public I_C_ValidCombination getNotInvoicedReceipts_A() throws RuntimeException
    {
		return (I_C_ValidCombination)MTable.get(getCtx(), I_C_ValidCombination.Table_Name)
			.getPO(getNotInvoicedReceipts_Acct(), get_TrxName());	}

	/** Set Not-invoiced Receipts.
		@param NotInvoicedReceipts_Acct 
		Account for not-invoiced Material Receipts
	  */
	public void setNotInvoicedReceipts_Acct (int NotInvoicedReceipts_Acct)
	{
		set_Value (COLUMNNAME_NotInvoicedReceipts_Acct, Integer.valueOf(NotInvoicedReceipts_Acct));
	}

	/** Get Not-invoiced Receipts.
		@return Account for not-invoiced Material Receipts
	  */
	public int getNotInvoicedReceipts_Acct () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_NotInvoicedReceipts_Acct);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_DiscountSchema getPO_DiscountSchema() throws RuntimeException
    {
		return (org.compiere.model.I_M_DiscountSchema)MTable.get(getCtx(), org.compiere.model.I_M_DiscountSchema.Table_Name)
			.getPO(getPO_DiscountSchema_ID(), get_TrxName());	}

	/** Set PO Discount Schema.
		@param PO_DiscountSchema_ID 
		Schema to calculate the purchase trade discount percentage
	  */
	public void setPO_DiscountSchema_ID (int PO_DiscountSchema_ID)
	{
		if (PO_DiscountSchema_ID < 1) 
			set_Value (COLUMNNAME_PO_DiscountSchema_ID, null);
		else 
			set_Value (COLUMNNAME_PO_DiscountSchema_ID, Integer.valueOf(PO_DiscountSchema_ID));
	}

	/** Get PO Discount Schema.
		@return Schema to calculate the purchase trade discount percentage
	  */
	public int getPO_DiscountSchema_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_PO_DiscountSchema_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_PriceList getPO_PriceList() throws RuntimeException
    {
		return (org.compiere.model.I_M_PriceList)MTable.get(getCtx(), org.compiere.model.I_M_PriceList.Table_Name)
			.getPO(getPO_PriceList_ID(), get_TrxName());	}

	/** Set Purchase Pricelist.
		@param PO_PriceList_ID 
		Price List used by this Business Partner
	  */
	public void setPO_PriceList_ID (int PO_PriceList_ID)
	{
		if (PO_PriceList_ID < 1) 
			set_Value (COLUMNNAME_PO_PriceList_ID, null);
		else 
			set_Value (COLUMNNAME_PO_PriceList_ID, Integer.valueOf(PO_PriceList_ID));
	}

	/** Get Purchase Pricelist.
		@return Price List used by this Business Partner
	  */
	public int getPO_PriceList_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_PO_PriceList_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_ValidCombination getPayDiscount_Exp_A() throws RuntimeException
    {
		return (I_C_ValidCombination)MTable.get(getCtx(), I_C_ValidCombination.Table_Name)
			.getPO(getPayDiscount_Exp_Acct(), get_TrxName());	}

	/** Set Payment Discount Expense.
		@param PayDiscount_Exp_Acct 
		Payment Discount Expense Account
	  */
	public void setPayDiscount_Exp_Acct (int PayDiscount_Exp_Acct)
	{
		set_Value (COLUMNNAME_PayDiscount_Exp_Acct, Integer.valueOf(PayDiscount_Exp_Acct));
	}

	/** Get Payment Discount Expense.
		@return Payment Discount Expense Account
	  */
	public int getPayDiscount_Exp_Acct () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_PayDiscount_Exp_Acct);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_ValidCombination getPayDiscount_Rev_A() throws RuntimeException
    {
		return (I_C_ValidCombination)MTable.get(getCtx(), I_C_ValidCombination.Table_Name)
			.getPO(getPayDiscount_Rev_Acct(), get_TrxName());	}

	/** Set Payment Discount Revenue.
		@param PayDiscount_Rev_Acct 
		Payment Discount Revenue Account
	  */
	public void setPayDiscount_Rev_Acct (int PayDiscount_Rev_Acct)
	{
		set_Value (COLUMNNAME_PayDiscount_Rev_Acct, Integer.valueOf(PayDiscount_Rev_Acct));
	}

	/** Get Payment Discount Revenue.
		@return Payment Discount Revenue Account
	  */
	public int getPayDiscount_Rev_Acct () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_PayDiscount_Rev_Acct);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Price Match Tolerance.
		@param PriceMatchTolerance 
		PO-Invoice Match Price Tolerance in percent of the purchase price
	  */
	public void setPriceMatchTolerance (BigDecimal PriceMatchTolerance)
	{
		set_Value (COLUMNNAME_PriceMatchTolerance, PriceMatchTolerance);
	}

	/** Get Price Match Tolerance.
		@return PO-Invoice Match Price Tolerance in percent of the purchase price
	  */
	public BigDecimal getPriceMatchTolerance () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_PriceMatchTolerance);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** PriorityBase AD_Reference_ID=350 */
	public static final int PRIORITYBASE_AD_Reference_ID=350;
	/** Same = S */
	public static final String PRIORITYBASE_Same = "S";
	/** Lower = L */
	public static final String PRIORITYBASE_Lower = "L";
	/** Higher = H */
	public static final String PRIORITYBASE_Higher = "H";
	/** Set Priority Base.
		@param PriorityBase 
		Base of Priority
	  */
	public void setPriorityBase (String PriorityBase)
	{

		set_Value (COLUMNNAME_PriorityBase, PriorityBase);
	}

	/** Get Priority Base.
		@return Base of Priority
	  */
	public String getPriorityBase () 
	{
		return (String)get_Value(COLUMNNAME_PriorityBase);
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

	public I_C_ValidCombination getV_Liability_A() throws RuntimeException
    {
		return (I_C_ValidCombination)MTable.get(getCtx(), I_C_ValidCombination.Table_Name)
			.getPO(getV_Liability_Acct(), get_TrxName());	}

	/** Set Vendor Liability.
		@param V_Liability_Acct 
		Account for Vendor Liability
	  */
	public void setV_Liability_Acct (int V_Liability_Acct)
	{
		set_Value (COLUMNNAME_V_Liability_Acct, Integer.valueOf(V_Liability_Acct));
	}

	/** Get Vendor Liability.
		@return Account for Vendor Liability
	  */
	public int getV_Liability_Acct () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_V_Liability_Acct);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_ValidCombination getV_Prepayment_A() throws RuntimeException
    {
		return (I_C_ValidCombination)MTable.get(getCtx(), I_C_ValidCombination.Table_Name)
			.getPO(getV_Prepayment_Acct(), get_TrxName());	}

	/** Set Vendor Prepayment.
		@param V_Prepayment_Acct 
		Account for Vendor Prepayments
	  */
	public void setV_Prepayment_Acct (int V_Prepayment_Acct)
	{
		set_Value (COLUMNNAME_V_Prepayment_Acct, Integer.valueOf(V_Prepayment_Acct));
	}

	/** Get Vendor Prepayment.
		@return Account for Vendor Prepayments
	  */
	public int getV_Prepayment_Acct () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_V_Prepayment_Acct);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Search Key.
		@param Value 
		Search key for the record in the format required - must be unique
	  */
	public void setValue (String Value)
	{
		set_Value (COLUMNNAME_Value, Value);
	}

	/** Get Search Key.
		@return Search key for the record in the format required - must be unique
	  */
	public String getValue () 
	{
		return (String)get_Value(COLUMNNAME_Value);
	}

	public I_C_ValidCombination getWriteOff_A() throws RuntimeException
    {
		return (I_C_ValidCombination)MTable.get(getCtx(), I_C_ValidCombination.Table_Name)
			.getPO(getWriteOff_Acct(), get_TrxName());	}

	/** Set Write-off.
		@param WriteOff_Acct 
		Account for Receivables write-off
	  */
	public void setWriteOff_Acct (int WriteOff_Acct)
	{
		set_Value (COLUMNNAME_WriteOff_Acct, Integer.valueOf(WriteOff_Acct));
	}

	/** Get Write-off.
		@return Account for Receivables write-off
	  */
	public int getWriteOff_Acct () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_WriteOff_Acct);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}