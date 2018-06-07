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

/** Generated Interface for I_ProductCategoryJP
 *  @author iDempiere (generated) 
 *  @version Release 5.1
 */
@SuppressWarnings("all")
public interface I_I_ProductCategoryJP 
{

    /** TableName=I_ProductCategoryJP */
    public static final String Table_Name = "I_ProductCategoryJP";

    /** AD_Table_ID=1000209 */
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

    /** Column name A_Asset_Group_ID */
    public static final String COLUMNNAME_A_Asset_Group_ID = "A_Asset_Group_ID";

	/** Set Asset Group.
	  * Group of Assets
	  */
	public void setA_Asset_Group_ID (int A_Asset_Group_ID);

	/** Get Asset Group.
	  * Group of Assets
	  */
	public int getA_Asset_Group_ID();

	public org.compiere.model.I_A_Asset_Group getA_Asset_Group() throws RuntimeException;

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

    /** Column name CostingLevel */
    public static final String COLUMNNAME_CostingLevel = "CostingLevel";

	/** Set Costing Level.
	  * The lowest level to accumulate Costing Information
	  */
	public void setCostingLevel (String CostingLevel);

	/** Get Costing Level.
	  * The lowest level to accumulate Costing Information
	  */
	public String getCostingLevel();

    /** Column name CostingMethod */
    public static final String COLUMNNAME_CostingMethod = "CostingMethod";

	/** Set Costing Method.
	  * Indicates how Costs will be calculated
	  */
	public void setCostingMethod (String CostingMethod);

	/** Get Costing Method.
	  * Indicates how Costs will be calculated
	  */
	public String getCostingMethod();

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

    /** Column name I_ProductCategoryJP_ID */
    public static final String COLUMNNAME_I_ProductCategoryJP_ID = "I_ProductCategoryJP_ID";

	/** Set I_ProductCategoryJP	  */
	public void setI_ProductCategoryJP_ID (int I_ProductCategoryJP_ID);

	/** Get I_ProductCategoryJP	  */
	public int getI_ProductCategoryJP_ID();

    /** Column name I_ProductCategoryJP_UU */
    public static final String COLUMNNAME_I_ProductCategoryJP_UU = "I_ProductCategoryJP_UU";

	/** Set I_ProductCategoryJP_UU	  */
	public void setI_ProductCategoryJP_UU (String I_ProductCategoryJP_UU);

	/** Get I_ProductCategoryJP_UU	  */
	public String getI_ProductCategoryJP_UU();

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

    /** Column name IsSelfService */
    public static final String COLUMNNAME_IsSelfService = "IsSelfService";

	/** Set Self-Service.
	  * This is a Self-Service entry or this entry can be changed via Self-Service
	  */
	public void setIsSelfService (boolean IsSelfService);

	/** Get Self-Service.
	  * This is a Self-Service entry or this entry can be changed via Self-Service
	  */
	public boolean isSelfService();

    /** Column name JP_AcctSchema_Name */
    public static final String COLUMNNAME_JP_AcctSchema_Name = "JP_AcctSchema_Name";

	/** Set Accounting Schema(Name)	  */
	public void setJP_AcctSchema_Name (String JP_AcctSchema_Name);

	/** Get Accounting Schema(Name)	  */
	public String getJP_AcctSchema_Name();

    /** Column name JP_Asset_Group_Name */
    public static final String COLUMNNAME_JP_Asset_Group_Name = "JP_Asset_Group_Name";

	/** Set Asset Group(Name)	  */
	public void setJP_Asset_Group_Name (String JP_Asset_Group_Name);

	/** Get Asset Group(Name)	  */
	public String getJP_Asset_Group_Name();

    /** Column name JP_AverageCostVariance_Value */
    public static final String COLUMNNAME_JP_AverageCostVariance_Value = "JP_AverageCostVariance_Value";

	/** Set Average Cost Variance(Search key)	  */
	public void setJP_AverageCostVariance_Value (String JP_AverageCostVariance_Value);

	/** Get Average Cost Variance(Search key)	  */
	public String getJP_AverageCostVariance_Value();

    /** Column name JP_COGS_Acct_Value */
    public static final String COLUMNNAME_JP_COGS_Acct_Value = "JP_COGS_Acct_Value";

	/** Set Product COGS(Search key)	  */
	public void setJP_COGS_Acct_Value (String JP_COGS_Acct_Value);

	/** Get Product COGS(Search key)	  */
	public String getJP_COGS_Acct_Value();

    /** Column name JP_CostAdjustment_Value */
    public static final String COLUMNNAME_JP_CostAdjustment_Value = "JP_CostAdjustment_Value";

	/** Set Cost Adjustment(Search Key)	  */
	public void setJP_CostAdjustment_Value (String JP_CostAdjustment_Value);

	/** Get Cost Adjustment(Search Key)	  */
	public String getJP_CostAdjustment_Value();

    /** Column name JP_InventoryClearing_Value */
    public static final String COLUMNNAME_JP_InventoryClearing_Value = "JP_InventoryClearing_Value";

	/** Set Inventory Clearing(Search Key)	  */
	public void setJP_InventoryClearing_Value (String JP_InventoryClearing_Value);

	/** Get Inventory Clearing(Search Key)	  */
	public String getJP_InventoryClearing_Value();

    /** Column name JP_InvoicePriceVariance_Value */
    public static final String COLUMNNAME_JP_InvoicePriceVariance_Value = "JP_InvoicePriceVariance_Value";

	/** Set Invoice Price Variance(Search Key)	  */
	public void setJP_InvoicePriceVariance_Value (String JP_InvoicePriceVariance_Value);

	/** Get Invoice Price Variance(Search Key)	  */
	public String getJP_InvoicePriceVariance_Value();

    /** Column name JP_LandedCostClearing_Value */
    public static final String COLUMNNAME_JP_LandedCostClearing_Value = "JP_LandedCostClearing_Value";

	/** Set Landed Cost Clearing(Search Key)	  */
	public void setJP_LandedCostClearing_Value (String JP_LandedCostClearing_Value);

	/** Get Landed Cost Clearing(Search Key)	  */
	public String getJP_LandedCostClearing_Value();

    /** Column name JP_Org_Value */
    public static final String COLUMNNAME_JP_Org_Value = "JP_Org_Value";

	/** Set Organization(Search Key)	  */
	public void setJP_Org_Value (String JP_Org_Value);

	/** Get Organization(Search Key)	  */
	public String getJP_Org_Value();

    /** Column name JP_PO_PriceVariance_Value */
    public static final String COLUMNNAME_JP_PO_PriceVariance_Value = "JP_PO_PriceVariance_Value";

	/** Set Purchase Price Variance(Search Key)	  */
	public void setJP_PO_PriceVariance_Value (String JP_PO_PriceVariance_Value);

	/** Get Purchase Price Variance(Search Key)	  */
	public String getJP_PO_PriceVariance_Value();

    /** Column name JP_P_Asset_Acct_Value */
    public static final String COLUMNNAME_JP_P_Asset_Acct_Value = "JP_P_Asset_Acct_Value";

	/** Set Product Asset(Search Key)	  */
	public void setJP_P_Asset_Acct_Value (String JP_P_Asset_Acct_Value);

	/** Get Product Asset(Search Key)	  */
	public String getJP_P_Asset_Acct_Value();

    /** Column name JP_P_Expense_Acct_Value */
    public static final String COLUMNNAME_JP_P_Expense_Acct_Value = "JP_P_Expense_Acct_Value";

	/** Set Product Expense(Search Key)	  */
	public void setJP_P_Expense_Acct_Value (String JP_P_Expense_Acct_Value);

	/** Get Product Expense(Search Key)	  */
	public String getJP_P_Expense_Acct_Value();

    /** Column name JP_P_Revenue_Acct_Value */
    public static final String COLUMNNAME_JP_P_Revenue_Acct_Value = "JP_P_Revenue_Acct_Value";

	/** Set Product Revenue(Search Key)	  */
	public void setJP_P_Revenue_Acct_Value (String JP_P_Revenue_Acct_Value);

	/** Get Product Revenue(Search Key)	  */
	public String getJP_P_Revenue_Acct_Value();

    /** Column name JP_P_TradeDiscountGrant_Value */
    public static final String COLUMNNAME_JP_P_TradeDiscountGrant_Value = "JP_P_TradeDiscountGrant_Value";

	/** Set Trade Discount Granted(Search Key)	  */
	public void setJP_P_TradeDiscountGrant_Value (String JP_P_TradeDiscountGrant_Value);

	/** Get Trade Discount Granted(Search Key)	  */
	public String getJP_P_TradeDiscountGrant_Value();

    /** Column name JP_P_TradeDiscountRec_Value */
    public static final String COLUMNNAME_JP_P_TradeDiscountRec_Value = "JP_P_TradeDiscountRec_Value";

	/** Set Trade Discount Received(Search Key)	  */
	public void setJP_P_TradeDiscountRec_Value (String JP_P_TradeDiscountRec_Value);

	/** Get Trade Discount Received(Search Key)	  */
	public String getJP_P_TradeDiscountRec_Value();

    /** Column name JP_PrintColor_Name */
    public static final String COLUMNNAME_JP_PrintColor_Name = "JP_PrintColor_Name";

	/** Set Print Color(Name)	  */
	public void setJP_PrintColor_Name (String JP_PrintColor_Name);

	/** Get Print Color(Name)	  */
	public String getJP_PrintColor_Name();

    /** Column name JP_ProductCategoryL1_ID */
    public static final String COLUMNNAME_JP_ProductCategoryL1_ID = "JP_ProductCategoryL1_ID";

	/** Set Product Category Level1	  */
	public void setJP_ProductCategoryL1_ID (int JP_ProductCategoryL1_ID);

	/** Get Product Category Level1	  */
	public int getJP_ProductCategoryL1_ID();

	public I_JP_ProductCategoryL1 getJP_ProductCategoryL1() throws RuntimeException;

    /** Column name JP_ProductCategoryL1_Value */
    public static final String COLUMNNAME_JP_ProductCategoryL1_Value = "JP_ProductCategoryL1_Value";

	/** Set Product Category Lv1(Search Key)	  */
	public void setJP_ProductCategoryL1_Value (String JP_ProductCategoryL1_Value);

	/** Get Product Category Lv1(Search Key)	  */
	public String getJP_ProductCategoryL1_Value();

    /** Column name JP_RateVariance_Acct_Value */
    public static final String COLUMNNAME_JP_RateVariance_Acct_Value = "JP_RateVariance_Acct_Value";

	/** Set Rate Variance(Search Key)	  */
	public void setJP_RateVariance_Acct_Value (String JP_RateVariance_Acct_Value);

	/** Get Rate Variance(Search Key)	  */
	public String getJP_RateVariance_Acct_Value();

    /** Column name MMPolicy */
    public static final String COLUMNNAME_MMPolicy = "MMPolicy";

	/** Set Material Policy.
	  * Material Movement Policy
	  */
	public void setMMPolicy (String MMPolicy);

	/** Get Material Policy.
	  * Material Movement Policy
	  */
	public String getMMPolicy();

    /** Column name M_Product_Category_ID */
    public static final String COLUMNNAME_M_Product_Category_ID = "M_Product_Category_ID";

	/** Set Product Category.
	  * Category of a Product
	  */
	public void setM_Product_Category_ID (int M_Product_Category_ID);

	/** Get Product Category.
	  * Category of a Product
	  */
	public int getM_Product_Category_ID();

	public org.compiere.model.I_M_Product_Category getM_Product_Category() throws RuntimeException;

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

    /** Column name P_Asset_Acct */
    public static final String COLUMNNAME_P_Asset_Acct = "P_Asset_Acct";

	/** Set Product Asset.
	  * Account for Product Asset (Inventory)
	  */
	public void setP_Asset_Acct (int P_Asset_Acct);

	/** Get Product Asset.
	  * Account for Product Asset (Inventory)
	  */
	public int getP_Asset_Acct();

	public I_C_ValidCombination getP_Asset_A() throws RuntimeException;

    /** Column name P_AverageCostVariance_Acct */
    public static final String COLUMNNAME_P_AverageCostVariance_Acct = "P_AverageCostVariance_Acct";

	/** Set Average Cost Variance.
	  * Average Cost Variance
	  */
	public void setP_AverageCostVariance_Acct (int P_AverageCostVariance_Acct);

	/** Get Average Cost Variance.
	  * Average Cost Variance
	  */
	public int getP_AverageCostVariance_Acct();

	public I_C_ValidCombination getP_AverageCostVariance_A() throws RuntimeException;

    /** Column name P_COGS_Acct */
    public static final String COLUMNNAME_P_COGS_Acct = "P_COGS_Acct";

	/** Set Product COGS.
	  * Account for Cost of Goods Sold
	  */
	public void setP_COGS_Acct (int P_COGS_Acct);

	/** Get Product COGS.
	  * Account for Cost of Goods Sold
	  */
	public int getP_COGS_Acct();

	public I_C_ValidCombination getP_COGS_A() throws RuntimeException;

    /** Column name P_CostAdjustment_Acct */
    public static final String COLUMNNAME_P_CostAdjustment_Acct = "P_CostAdjustment_Acct";

	/** Set Cost Adjustment.
	  * Product Cost Adjustment Account
	  */
	public void setP_CostAdjustment_Acct (int P_CostAdjustment_Acct);

	/** Get Cost Adjustment.
	  * Product Cost Adjustment Account
	  */
	public int getP_CostAdjustment_Acct();

	public I_C_ValidCombination getP_CostAdjustment_A() throws RuntimeException;

    /** Column name P_Expense_Acct */
    public static final String COLUMNNAME_P_Expense_Acct = "P_Expense_Acct";

	/** Set Product Expense.
	  * Account for Product Expense
	  */
	public void setP_Expense_Acct (int P_Expense_Acct);

	/** Get Product Expense.
	  * Account for Product Expense
	  */
	public int getP_Expense_Acct();

	public I_C_ValidCombination getP_Expense_A() throws RuntimeException;

    /** Column name P_InventoryClearing_Acct */
    public static final String COLUMNNAME_P_InventoryClearing_Acct = "P_InventoryClearing_Acct";

	/** Set Inventory Clearing.
	  * Product Inventory Clearing Account
	  */
	public void setP_InventoryClearing_Acct (int P_InventoryClearing_Acct);

	/** Get Inventory Clearing.
	  * Product Inventory Clearing Account
	  */
	public int getP_InventoryClearing_Acct();

	public I_C_ValidCombination getP_InventoryClearing_A() throws RuntimeException;

    /** Column name P_InvoicePriceVariance_Acct */
    public static final String COLUMNNAME_P_InvoicePriceVariance_Acct = "P_InvoicePriceVariance_Acct";

	/** Set Invoice Price Variance.
	  * Difference between Costs and Invoice Price (IPV)
	  */
	public void setP_InvoicePriceVariance_Acct (int P_InvoicePriceVariance_Acct);

	/** Get Invoice Price Variance.
	  * Difference between Costs and Invoice Price (IPV)
	  */
	public int getP_InvoicePriceVariance_Acct();

	public I_C_ValidCombination getP_InvoicePriceVariance_A() throws RuntimeException;

    /** Column name P_LandedCostClearing_Acct */
    public static final String COLUMNNAME_P_LandedCostClearing_Acct = "P_LandedCostClearing_Acct";

	/** Set Landed Cost Clearing.
	  * Product Landed Cost Clearing Account
	  */
	public void setP_LandedCostClearing_Acct (int P_LandedCostClearing_Acct);

	/** Get Landed Cost Clearing.
	  * Product Landed Cost Clearing Account
	  */
	public int getP_LandedCostClearing_Acct();

	public I_C_ValidCombination getP_LandedCostClearing_A() throws RuntimeException;

    /** Column name P_PurchasePriceVariance_Acct */
    public static final String COLUMNNAME_P_PurchasePriceVariance_Acct = "P_PurchasePriceVariance_Acct";

	/** Set Purchase Price Variance.
	  * Difference between Standard Cost and Purchase Price (PPV)
	  */
	public void setP_PurchasePriceVariance_Acct (int P_PurchasePriceVariance_Acct);

	/** Get Purchase Price Variance.
	  * Difference between Standard Cost and Purchase Price (PPV)
	  */
	public int getP_PurchasePriceVariance_Acct();

	public I_C_ValidCombination getP_PurchasePriceVariance_A() throws RuntimeException;

    /** Column name P_RateVariance_Acct */
    public static final String COLUMNNAME_P_RateVariance_Acct = "P_RateVariance_Acct";

	/** Set Rate Variance.
	  * The Rate Variance account is the account used Manufacturing Order
	  */
	public void setP_RateVariance_Acct (int P_RateVariance_Acct);

	/** Get Rate Variance.
	  * The Rate Variance account is the account used Manufacturing Order
	  */
	public int getP_RateVariance_Acct();

	public I_C_ValidCombination getP_RateVariance_A() throws RuntimeException;

    /** Column name P_Revenue_Acct */
    public static final String COLUMNNAME_P_Revenue_Acct = "P_Revenue_Acct";

	/** Set Product Revenue.
	  * Account for Product Revenue (Sales Account)
	  */
	public void setP_Revenue_Acct (int P_Revenue_Acct);

	/** Get Product Revenue.
	  * Account for Product Revenue (Sales Account)
	  */
	public int getP_Revenue_Acct();

	public I_C_ValidCombination getP_Revenue_A() throws RuntimeException;

    /** Column name P_TradeDiscountGrant_Acct */
    public static final String COLUMNNAME_P_TradeDiscountGrant_Acct = "P_TradeDiscountGrant_Acct";

	/** Set Trade Discount Granted.
	  * Trade Discount Granted Account
	  */
	public void setP_TradeDiscountGrant_Acct (int P_TradeDiscountGrant_Acct);

	/** Get Trade Discount Granted.
	  * Trade Discount Granted Account
	  */
	public int getP_TradeDiscountGrant_Acct();

	public I_C_ValidCombination getP_TradeDiscountGrant_A() throws RuntimeException;

    /** Column name P_TradeDiscountRec_Acct */
    public static final String COLUMNNAME_P_TradeDiscountRec_Acct = "P_TradeDiscountRec_Acct";

	/** Set Trade Discount Received.
	  * Trade Discount Receivable Account
	  */
	public void setP_TradeDiscountRec_Acct (int P_TradeDiscountRec_Acct);

	/** Get Trade Discount Received.
	  * Trade Discount Receivable Account
	  */
	public int getP_TradeDiscountRec_Acct();

	public I_C_ValidCombination getP_TradeDiscountRec_A() throws RuntimeException;

    /** Column name PlannedMargin */
    public static final String COLUMNNAME_PlannedMargin = "PlannedMargin";

	/** Set Planned Margin %.
	  * Project's planned margin as a percentage
	  */
	public void setPlannedMargin (BigDecimal PlannedMargin);

	/** Get Planned Margin %.
	  * Project's planned margin as a percentage
	  */
	public BigDecimal getPlannedMargin();

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
}
