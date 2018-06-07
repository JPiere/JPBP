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
package jpiere.base.plugin.org.adempiere.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.compiere.model.*;
import org.compiere.util.KeyNamePair;

/** Generated Interface for I_BP_GroupJP
 *  @author iDempiere (generated) 
 *  @version Release 5.1
 */
@SuppressWarnings("all")
public interface I_I_BP_GroupJP 
{

    /** TableName=I_BP_GroupJP */
    public static final String Table_Name = "I_BP_GroupJP";

    /** AD_Table_ID=1000210 */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 3 - Client - Org 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(3);

    /** Load Meta Data */

    /** Column name AD_Client_ID */
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";

	/** Get Client.
	  * Client/Tenant for this installation.
	  */
	public int getAD_Client_ID();

    /** Column name AD_Org_ID */
    public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";

	/** Set Organization.
	  * Organizational entity within client
	  */
	public void setAD_Org_ID (int AD_Org_ID);

	/** Get Organization.
	  * Organizational entity within client
	  */
	public int getAD_Org_ID();

    /** Column name AD_PrintColor_ID */
    public static final String COLUMNNAME_AD_PrintColor_ID = "AD_PrintColor_ID";

	/** Set Print Color.
	  * Color used for printing and display
	  */
	public void setAD_PrintColor_ID (int AD_PrintColor_ID);

	/** Get Print Color.
	  * Color used for printing and display
	  */
	public int getAD_PrintColor_ID();

	public org.compiere.model.I_AD_PrintColor getAD_PrintColor() throws RuntimeException;

    /** Column name C_AcctSchema_ID */
    public static final String COLUMNNAME_C_AcctSchema_ID = "C_AcctSchema_ID";

	/** Set Accounting Schema.
	  * Rules for accounting
	  */
	public void setC_AcctSchema_ID (int C_AcctSchema_ID);

	/** Get Accounting Schema.
	  * Rules for accounting
	  */
	public int getC_AcctSchema_ID();

	public org.compiere.model.I_C_AcctSchema getC_AcctSchema() throws RuntimeException;

    /** Column name C_BP_Group_ID */
    public static final String COLUMNNAME_C_BP_Group_ID = "C_BP_Group_ID";

	/** Set BPartner Group.
	  * Business Partner Group
	  */
	public void setC_BP_Group_ID (int C_BP_Group_ID);

	/** Get BPartner Group.
	  * Business Partner Group
	  */
	public int getC_BP_Group_ID();

	public org.compiere.model.I_C_BP_Group getC_BP_Group() throws RuntimeException;

    /** Column name C_Dunning_ID */
    public static final String COLUMNNAME_C_Dunning_ID = "C_Dunning_ID";

	/** Set Dunning.
	  * Dunning Rules for overdue invoices
	  */
	public void setC_Dunning_ID (int C_Dunning_ID);

	/** Get Dunning.
	  * Dunning Rules for overdue invoices
	  */
	public int getC_Dunning_ID();

	public org.compiere.model.I_C_Dunning getC_Dunning() throws RuntimeException;

    /** Column name C_Prepayment_Acct */
    public static final String COLUMNNAME_C_Prepayment_Acct = "C_Prepayment_Acct";

	/** Set Customer Prepayment.
	  * Account for customer prepayments
	  */
	public void setC_Prepayment_Acct (int C_Prepayment_Acct);

	/** Get Customer Prepayment.
	  * Account for customer prepayments
	  */
	public int getC_Prepayment_Acct();

	public I_C_ValidCombination getC_Prepayment_A() throws RuntimeException;

    /** Column name C_Receivable_Acct */
    public static final String COLUMNNAME_C_Receivable_Acct = "C_Receivable_Acct";

	/** Set Customer Receivables.
	  * Account for Customer Receivables
	  */
	public void setC_Receivable_Acct (int C_Receivable_Acct);

	/** Get Customer Receivables.
	  * Account for Customer Receivables
	  */
	public int getC_Receivable_Acct();

	public I_C_ValidCombination getC_Receivable_A() throws RuntimeException;

    /** Column name Created */
    public static final String COLUMNNAME_Created = "Created";

	/** Get Created.
	  * Date this record was created
	  */
	public Timestamp getCreated();

    /** Column name CreatedBy */
    public static final String COLUMNNAME_CreatedBy = "CreatedBy";

	/** Get Created By.
	  * User who created this records
	  */
	public int getCreatedBy();

    /** Column name CreditWatchPercent */
    public static final String COLUMNNAME_CreditWatchPercent = "CreditWatchPercent";

	/** Set Credit Watch %.
	  * Credit Watch - Percent of Credit Limit when OK switches to Watch
	  */
	public void setCreditWatchPercent (BigDecimal CreditWatchPercent);

	/** Get Credit Watch %.
	  * Credit Watch - Percent of Credit Limit when OK switches to Watch
	  */
	public BigDecimal getCreditWatchPercent();

    /** Column name Description */
    public static final String COLUMNNAME_Description = "Description";

	/** Set Description.
	  * Optional short description of the record
	  */
	public void setDescription (String Description);

	/** Get Description.
	  * Optional short description of the record
	  */
	public String getDescription();

    /** Column name I_BP_GroupJP_ID */
    public static final String COLUMNNAME_I_BP_GroupJP_ID = "I_BP_GroupJP_ID";

	/** Set I_BP_GroupJP	  */
	public void setI_BP_GroupJP_ID (int I_BP_GroupJP_ID);

	/** Get I_BP_GroupJP	  */
	public int getI_BP_GroupJP_ID();

    /** Column name I_BP_GroupJP_UU */
    public static final String COLUMNNAME_I_BP_GroupJP_UU = "I_BP_GroupJP_UU";

	/** Set I_BP_GroupJP_UU	  */
	public void setI_BP_GroupJP_UU (String I_BP_GroupJP_UU);

	/** Get I_BP_GroupJP_UU	  */
	public String getI_BP_GroupJP_UU();

    /** Column name I_ErrorMsg */
    public static final String COLUMNNAME_I_ErrorMsg = "I_ErrorMsg";

	/** Set Import Error Message.
	  * Messages generated from import process
	  */
	public void setI_ErrorMsg (String I_ErrorMsg);

	/** Get Import Error Message.
	  * Messages generated from import process
	  */
	public String getI_ErrorMsg();

    /** Column name I_IsActiveJP */
    public static final String COLUMNNAME_I_IsActiveJP = "I_IsActiveJP";

	/** Set Active(For Import).
	  * Active flag for Import Date
	  */
	public void setI_IsActiveJP (boolean I_IsActiveJP);

	/** Get Active(For Import).
	  * Active flag for Import Date
	  */
	public boolean isI_IsActiveJP();

    /** Column name I_IsImported */
    public static final String COLUMNNAME_I_IsImported = "I_IsImported";

	/** Set Imported.
	  * Has this import been processed
	  */
	public void setI_IsImported (boolean I_IsImported);

	/** Get Imported.
	  * Has this import been processed
	  */
	public boolean isI_IsImported();

    /** Column name IsActive */
    public static final String COLUMNNAME_IsActive = "IsActive";

	/** Set Active.
	  * The record is active in the system
	  */
	public void setIsActive (boolean IsActive);

	/** Get Active.
	  * The record is active in the system
	  */
	public boolean isActive();

    /** Column name IsConfidentialInfo */
    public static final String COLUMNNAME_IsConfidentialInfo = "IsConfidentialInfo";

	/** Set Confidential Info.
	  * Can enter confidential information
	  */
	public void setIsConfidentialInfo (boolean IsConfidentialInfo);

	/** Get Confidential Info.
	  * Can enter confidential information
	  */
	public boolean isConfidentialInfo();

    /** Column name IsDefault */
    public static final String COLUMNNAME_IsDefault = "IsDefault";

	/** Set Default.
	  * Default value
	  */
	public void setIsDefault (boolean IsDefault);

	/** Get Default.
	  * Default value
	  */
	public boolean isDefault();

    /** Column name JP_AcctSchema_Name */
    public static final String COLUMNNAME_JP_AcctSchema_Name = "JP_AcctSchema_Name";

	/** Set Accounting Schema(Name)	  */
	public void setJP_AcctSchema_Name (String JP_AcctSchema_Name);

	/** Get Accounting Schema(Name)	  */
	public String getJP_AcctSchema_Name();

    /** Column name JP_C_PrePayment_Acct_Value */
    public static final String COLUMNNAME_JP_C_PrePayment_Acct_Value = "JP_C_PrePayment_Acct_Value";

	/** Set Customer Prepayment(Search Key)	  */
	public void setJP_C_PrePayment_Acct_Value (String JP_C_PrePayment_Acct_Value);

	/** Get Customer Prepayment(Search Key)	  */
	public String getJP_C_PrePayment_Acct_Value();

    /** Column name JP_DiscountSchema_Name */
    public static final String COLUMNNAME_JP_DiscountSchema_Name = "JP_DiscountSchema_Name";

	/** Set Discount Schema(Name)	  */
	public void setJP_DiscountSchema_Name (String JP_DiscountSchema_Name);

	/** Get Discount Schema(Name)	  */
	public String getJP_DiscountSchema_Name();

    /** Column name JP_Dunning_Name */
    public static final String COLUMNNAME_JP_Dunning_Name = "JP_Dunning_Name";

	/** Set Dunning(Name)	  */
	public void setJP_Dunning_Name (String JP_Dunning_Name);

	/** Get Dunning(Name)	  */
	public String getJP_Dunning_Name();

    /** Column name JP_Liability_Acct_Value */
    public static final String COLUMNNAME_JP_Liability_Acct_Value = "JP_Liability_Acct_Value";

	/** Set Vendor Liability(Search Key)	  */
	public void setJP_Liability_Acct_Value (String JP_Liability_Acct_Value);

	/** Get Vendor Liability(Search Key)	  */
	public String getJP_Liability_Acct_Value();

    /** Column name JP_NotInvoicedReceipts_Value */
    public static final String COLUMNNAME_JP_NotInvoicedReceipts_Value = "JP_NotInvoicedReceipts_Value";

	/** Set Not-invoiced Receipts(Search Key)	  */
	public void setJP_NotInvoicedReceipts_Value (String JP_NotInvoicedReceipts_Value);

	/** Get Not-invoiced Receipts(Search Key)	  */
	public String getJP_NotInvoicedReceipts_Value();

    /** Column name JP_Org_Value */
    public static final String COLUMNNAME_JP_Org_Value = "JP_Org_Value";

	/** Set Organization(Search Key)	  */
	public void setJP_Org_Value (String JP_Org_Value);

	/** Get Organization(Search Key)	  */
	public String getJP_Org_Value();

    /** Column name JP_PO_DiscountSchema_Name */
    public static final String COLUMNNAME_JP_PO_DiscountSchema_Name = "JP_PO_DiscountSchema_Name";

	/** Set PO Discount Schema(Name)	  */
	public void setJP_PO_DiscountSchema_Name (String JP_PO_DiscountSchema_Name);

	/** Get PO Discount Schema(Name)	  */
	public String getJP_PO_DiscountSchema_Name();

    /** Column name JP_PO_PriceList_Name */
    public static final String COLUMNNAME_JP_PO_PriceList_Name = "JP_PO_PriceList_Name";

	/** Set Purchase Pricelist(Name)	  */
	public void setJP_PO_PriceList_Name (String JP_PO_PriceList_Name);

	/** Get Purchase Pricelist(Name)	  */
	public String getJP_PO_PriceList_Name();

    /** Column name JP_PayDiscount_Exp_Value */
    public static final String COLUMNNAME_JP_PayDiscount_Exp_Value = "JP_PayDiscount_Exp_Value";

	/** Set Payment Discount Expense(Search key)	  */
	public void setJP_PayDiscount_Exp_Value (String JP_PayDiscount_Exp_Value);

	/** Get Payment Discount Expense(Search key)	  */
	public String getJP_PayDiscount_Exp_Value();

    /** Column name JP_PayDiscount_Rev_Value */
    public static final String COLUMNNAME_JP_PayDiscount_Rev_Value = "JP_PayDiscount_Rev_Value";

	/** Set Payment Discount Revenue(Search key)	  */
	public void setJP_PayDiscount_Rev_Value (String JP_PayDiscount_Rev_Value);

	/** Get Payment Discount Revenue(Search key)	  */
	public String getJP_PayDiscount_Rev_Value();

    /** Column name JP_PriceList_Name */
    public static final String COLUMNNAME_JP_PriceList_Name = "JP_PriceList_Name";

	/** Set Price List(Name)	  */
	public void setJP_PriceList_Name (String JP_PriceList_Name);

	/** Get Price List(Name)	  */
	public String getJP_PriceList_Name();

    /** Column name JP_PrintColor_Name */
    public static final String COLUMNNAME_JP_PrintColor_Name = "JP_PrintColor_Name";

	/** Set Print Color(Name)	  */
	public void setJP_PrintColor_Name (String JP_PrintColor_Name);

	/** Get Print Color(Name)	  */
	public String getJP_PrintColor_Name();

    /** Column name JP_Receivable_Acct_Value */
    public static final String COLUMNNAME_JP_Receivable_Acct_Value = "JP_Receivable_Acct_Value";

	/** Set Customer Receivables(Search key)	  */
	public void setJP_Receivable_Acct_Value (String JP_Receivable_Acct_Value);

	/** Get Customer Receivables(Search key)	  */
	public String getJP_Receivable_Acct_Value();

    /** Column name JP_V_Prepayment_Acct_Value */
    public static final String COLUMNNAME_JP_V_Prepayment_Acct_Value = "JP_V_Prepayment_Acct_Value";

	/** Set Vendor Prepayment(Search Key)	  */
	public void setJP_V_Prepayment_Acct_Value (String JP_V_Prepayment_Acct_Value);

	/** Get Vendor Prepayment(Search Key)	  */
	public String getJP_V_Prepayment_Acct_Value();

    /** Column name JP_WriteOff_Acct_Value */
    public static final String COLUMNNAME_JP_WriteOff_Acct_Value = "JP_WriteOff_Acct_Value";

	/** Set Write-off(Search Key)	  */
	public void setJP_WriteOff_Acct_Value (String JP_WriteOff_Acct_Value);

	/** Get Write-off(Search Key)	  */
	public String getJP_WriteOff_Acct_Value();

    /** Column name M_DiscountSchema_ID */
    public static final String COLUMNNAME_M_DiscountSchema_ID = "M_DiscountSchema_ID";

	/** Set Discount Schema.
	  * Schema to calculate the trade discount percentage
	  */
	public void setM_DiscountSchema_ID (int M_DiscountSchema_ID);

	/** Get Discount Schema.
	  * Schema to calculate the trade discount percentage
	  */
	public int getM_DiscountSchema_ID();

	public org.compiere.model.I_M_DiscountSchema getM_DiscountSchema() throws RuntimeException;

    /** Column name M_PriceList_ID */
    public static final String COLUMNNAME_M_PriceList_ID = "M_PriceList_ID";

	/** Set Price List.
	  * Unique identifier of a Price List
	  */
	public void setM_PriceList_ID (int M_PriceList_ID);

	/** Get Price List.
	  * Unique identifier of a Price List
	  */
	public int getM_PriceList_ID();

	public org.compiere.model.I_M_PriceList getM_PriceList() throws RuntimeException;

    /** Column name Name */
    public static final String COLUMNNAME_Name = "Name";

	/** Set Name.
	  * Alphanumeric identifier of the entity
	  */
	public void setName (String Name);

	/** Get Name.
	  * Alphanumeric identifier of the entity
	  */
	public String getName();

    /** Column name NotInvoicedReceipts_Acct */
    public static final String COLUMNNAME_NotInvoicedReceipts_Acct = "NotInvoicedReceipts_Acct";

	/** Set Not-invoiced Receipts.
	  * Account for not-invoiced Material Receipts
	  */
	public void setNotInvoicedReceipts_Acct (int NotInvoicedReceipts_Acct);

	/** Get Not-invoiced Receipts.
	  * Account for not-invoiced Material Receipts
	  */
	public int getNotInvoicedReceipts_Acct();

	public I_C_ValidCombination getNotInvoicedReceipts_A() throws RuntimeException;

    /** Column name PO_DiscountSchema_ID */
    public static final String COLUMNNAME_PO_DiscountSchema_ID = "PO_DiscountSchema_ID";

	/** Set PO Discount Schema.
	  * Schema to calculate the purchase trade discount percentage
	  */
	public void setPO_DiscountSchema_ID (int PO_DiscountSchema_ID);

	/** Get PO Discount Schema.
	  * Schema to calculate the purchase trade discount percentage
	  */
	public int getPO_DiscountSchema_ID();

	public org.compiere.model.I_M_DiscountSchema getPO_DiscountSchema() throws RuntimeException;

    /** Column name PO_PriceList_ID */
    public static final String COLUMNNAME_PO_PriceList_ID = "PO_PriceList_ID";

	/** Set Purchase Pricelist.
	  * Price List used by this Business Partner
	  */
	public void setPO_PriceList_ID (int PO_PriceList_ID);

	/** Get Purchase Pricelist.
	  * Price List used by this Business Partner
	  */
	public int getPO_PriceList_ID();

	public org.compiere.model.I_M_PriceList getPO_PriceList() throws RuntimeException;

    /** Column name PayDiscount_Exp_Acct */
    public static final String COLUMNNAME_PayDiscount_Exp_Acct = "PayDiscount_Exp_Acct";

	/** Set Payment Discount Expense.
	  * Payment Discount Expense Account
	  */
	public void setPayDiscount_Exp_Acct (int PayDiscount_Exp_Acct);

	/** Get Payment Discount Expense.
	  * Payment Discount Expense Account
	  */
	public int getPayDiscount_Exp_Acct();

	public I_C_ValidCombination getPayDiscount_Exp_A() throws RuntimeException;

    /** Column name PayDiscount_Rev_Acct */
    public static final String COLUMNNAME_PayDiscount_Rev_Acct = "PayDiscount_Rev_Acct";

	/** Set Payment Discount Revenue.
	  * Payment Discount Revenue Account
	  */
	public void setPayDiscount_Rev_Acct (int PayDiscount_Rev_Acct);

	/** Get Payment Discount Revenue.
	  * Payment Discount Revenue Account
	  */
	public int getPayDiscount_Rev_Acct();

	public I_C_ValidCombination getPayDiscount_Rev_A() throws RuntimeException;

    /** Column name PriceMatchTolerance */
    public static final String COLUMNNAME_PriceMatchTolerance = "PriceMatchTolerance";

	/** Set Price Match Tolerance.
	  * PO-Invoice Match Price Tolerance in percent of the purchase price
	  */
	public void setPriceMatchTolerance (BigDecimal PriceMatchTolerance);

	/** Get Price Match Tolerance.
	  * PO-Invoice Match Price Tolerance in percent of the purchase price
	  */
	public BigDecimal getPriceMatchTolerance();

    /** Column name PriorityBase */
    public static final String COLUMNNAME_PriorityBase = "PriorityBase";

	/** Set Priority Base.
	  * Base of Priority
	  */
	public void setPriorityBase (String PriorityBase);

	/** Get Priority Base.
	  * Base of Priority
	  */
	public String getPriorityBase();

    /** Column name Processed */
    public static final String COLUMNNAME_Processed = "Processed";

	/** Set Processed.
	  * The document has been processed
	  */
	public void setProcessed (boolean Processed);

	/** Get Processed.
	  * The document has been processed
	  */
	public boolean isProcessed();

    /** Column name Processing */
    public static final String COLUMNNAME_Processing = "Processing";

	/** Set Process Now	  */
	public void setProcessing (boolean Processing);

	/** Get Process Now	  */
	public boolean isProcessing();

    /** Column name Updated */
    public static final String COLUMNNAME_Updated = "Updated";

	/** Get Updated.
	  * Date this record was updated
	  */
	public Timestamp getUpdated();

    /** Column name UpdatedBy */
    public static final String COLUMNNAME_UpdatedBy = "UpdatedBy";

	/** Get Updated By.
	  * User who updated this records
	  */
	public int getUpdatedBy();

    /** Column name V_Liability_Acct */
    public static final String COLUMNNAME_V_Liability_Acct = "V_Liability_Acct";

	/** Set Vendor Liability.
	  * Account for Vendor Liability
	  */
	public void setV_Liability_Acct (int V_Liability_Acct);

	/** Get Vendor Liability.
	  * Account for Vendor Liability
	  */
	public int getV_Liability_Acct();

	public I_C_ValidCombination getV_Liability_A() throws RuntimeException;

    /** Column name V_Prepayment_Acct */
    public static final String COLUMNNAME_V_Prepayment_Acct = "V_Prepayment_Acct";

	/** Set Vendor Prepayment.
	  * Account for Vendor Prepayments
	  */
	public void setV_Prepayment_Acct (int V_Prepayment_Acct);

	/** Get Vendor Prepayment.
	  * Account for Vendor Prepayments
	  */
	public int getV_Prepayment_Acct();

	public I_C_ValidCombination getV_Prepayment_A() throws RuntimeException;

    /** Column name Value */
    public static final String COLUMNNAME_Value = "Value";

	/** Set Search Key.
	  * Search key for the record in the format required - must be unique
	  */
	public void setValue (String Value);

	/** Get Search Key.
	  * Search key for the record in the format required - must be unique
	  */
	public String getValue();

    /** Column name WriteOff_Acct */
    public static final String COLUMNNAME_WriteOff_Acct = "WriteOff_Acct";

	/** Set Write-off.
	  * Account for Receivables write-off
	  */
	public void setWriteOff_Acct (int WriteOff_Acct);

	/** Get Write-off.
	  * Account for Receivables write-off
	  */
	public int getWriteOff_Acct();

	public I_C_ValidCombination getWriteOff_A() throws RuntimeException;
}
