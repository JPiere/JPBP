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

/** Generated Model for I_ProductCategoryJP
 *  @author iDempiere (generated) 
 *  @version Release 5.1 - $Id$ */
public class X_I_ProductCategoryJP extends PO implements I_I_ProductCategoryJP, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20180601L;

    /** Standard Constructor */
    public X_I_ProductCategoryJP (Properties ctx, int I_ProductCategoryJP_ID, String trxName)
    {
      super (ctx, I_ProductCategoryJP_ID, trxName);
      /** if (I_ProductCategoryJP_ID == 0)
        {
			setI_ProductCategoryJP_ID (0);
			setIsDefault (false);
			setIsSelfService (false);
			setMMPolicy (null);
// F
			setPlannedMargin (Env.ZERO);
			setValue (null);
        } */
    }

    /** Load Constructor */
    public X_I_ProductCategoryJP (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_I_ProductCategoryJP[")
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

	public org.compiere.model.I_A_Asset_Group getA_Asset_Group() throws RuntimeException
    {
		return (org.compiere.model.I_A_Asset_Group)MTable.get(getCtx(), org.compiere.model.I_A_Asset_Group.Table_Name)
			.getPO(getA_Asset_Group_ID(), get_TrxName());	}

	/** Set Asset Group.
		@param A_Asset_Group_ID 
		Group of Assets
	  */
	public void setA_Asset_Group_ID (int A_Asset_Group_ID)
	{
		if (A_Asset_Group_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_A_Asset_Group_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_A_Asset_Group_ID, Integer.valueOf(A_Asset_Group_ID));
	}

	/** Get Asset Group.
		@return Group of Assets
	  */
	public int getA_Asset_Group_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_A_Asset_Group_ID);
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
			set_ValueNoCheck (COLUMNNAME_C_AcctSchema_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_AcctSchema_ID, Integer.valueOf(C_AcctSchema_ID));
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

	/** CostingLevel AD_Reference_ID=355 */
	public static final int COSTINGLEVEL_AD_Reference_ID=355;
	/** Client = C */
	public static final String COSTINGLEVEL_Client = "C";
	/** Organization = O */
	public static final String COSTINGLEVEL_Organization = "O";
	/** Batch/Lot = B */
	public static final String COSTINGLEVEL_BatchLot = "B";
	/** Set Costing Level.
		@param CostingLevel 
		The lowest level to accumulate Costing Information
	  */
	public void setCostingLevel (String CostingLevel)
	{

		set_Value (COLUMNNAME_CostingLevel, CostingLevel);
	}

	/** Get Costing Level.
		@return The lowest level to accumulate Costing Information
	  */
	public String getCostingLevel () 
	{
		return (String)get_Value(COLUMNNAME_CostingLevel);
	}

	/** CostingMethod AD_Reference_ID=122 */
	public static final int COSTINGMETHOD_AD_Reference_ID=122;
	/** Standard Costing = S */
	public static final String COSTINGMETHOD_StandardCosting = "S";
	/** Average PO = A */
	public static final String COSTINGMETHOD_AveragePO = "A";
	/** Lifo = L */
	public static final String COSTINGMETHOD_Lifo = "L";
	/** Fifo = F */
	public static final String COSTINGMETHOD_Fifo = "F";
	/** Last PO Price = p */
	public static final String COSTINGMETHOD_LastPOPrice = "p";
	/** Average Invoice = I */
	public static final String COSTINGMETHOD_AverageInvoice = "I";
	/** Last Invoice = i */
	public static final String COSTINGMETHOD_LastInvoice = "i";
	/** User Defined = U */
	public static final String COSTINGMETHOD_UserDefined = "U";
	/** _ = x */
	public static final String COSTINGMETHOD__ = "x";
	/** Set Costing Method.
		@param CostingMethod 
		Indicates how Costs will be calculated
	  */
	public void setCostingMethod (String CostingMethod)
	{

		set_Value (COLUMNNAME_CostingMethod, CostingMethod);
	}

	/** Get Costing Method.
		@return Indicates how Costs will be calculated
	  */
	public String getCostingMethod () 
	{
		return (String)get_Value(COLUMNNAME_CostingMethod);
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

	/** Set I_ProductCategoryJP.
		@param I_ProductCategoryJP_ID I_ProductCategoryJP	  */
	public void setI_ProductCategoryJP_ID (int I_ProductCategoryJP_ID)
	{
		if (I_ProductCategoryJP_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_I_ProductCategoryJP_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_I_ProductCategoryJP_ID, Integer.valueOf(I_ProductCategoryJP_ID));
	}

	/** Get I_ProductCategoryJP.
		@return I_ProductCategoryJP	  */
	public int getI_ProductCategoryJP_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_I_ProductCategoryJP_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set I_ProductCategoryJP_UU.
		@param I_ProductCategoryJP_UU I_ProductCategoryJP_UU	  */
	public void setI_ProductCategoryJP_UU (String I_ProductCategoryJP_UU)
	{
		set_ValueNoCheck (COLUMNNAME_I_ProductCategoryJP_UU, I_ProductCategoryJP_UU);
	}

	/** Get I_ProductCategoryJP_UU.
		@return I_ProductCategoryJP_UU	  */
	public String getI_ProductCategoryJP_UU () 
	{
		return (String)get_Value(COLUMNNAME_I_ProductCategoryJP_UU);
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

	/** Set Self-Service.
		@param IsSelfService 
		This is a Self-Service entry or this entry can be changed via Self-Service
	  */
	public void setIsSelfService (boolean IsSelfService)
	{
		set_ValueNoCheck (COLUMNNAME_IsSelfService, Boolean.valueOf(IsSelfService));
	}

	/** Get Self-Service.
		@return This is a Self-Service entry or this entry can be changed via Self-Service
	  */
	public boolean isSelfService () 
	{
		Object oo = get_Value(COLUMNNAME_IsSelfService);
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

	/** Set Asset Group(Name).
		@param JP_Asset_Group_Name Asset Group(Name)	  */
	public void setJP_Asset_Group_Name (String JP_Asset_Group_Name)
	{
		set_Value (COLUMNNAME_JP_Asset_Group_Name, JP_Asset_Group_Name);
	}

	/** Get Asset Group(Name).
		@return Asset Group(Name)	  */
	public String getJP_Asset_Group_Name () 
	{
		return (String)get_Value(COLUMNNAME_JP_Asset_Group_Name);
	}

	/** Set Average Cost Variance(Search key).
		@param JP_AverageCostVariance_Value Average Cost Variance(Search key)	  */
	public void setJP_AverageCostVariance_Value (String JP_AverageCostVariance_Value)
	{
		set_ValueNoCheck (COLUMNNAME_JP_AverageCostVariance_Value, JP_AverageCostVariance_Value);
	}

	/** Get Average Cost Variance(Search key).
		@return Average Cost Variance(Search key)	  */
	public String getJP_AverageCostVariance_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_AverageCostVariance_Value);
	}

	/** Set Product COGS(Search key).
		@param JP_COGS_Acct_Value Product COGS(Search key)	  */
	public void setJP_COGS_Acct_Value (String JP_COGS_Acct_Value)
	{
		set_ValueNoCheck (COLUMNNAME_JP_COGS_Acct_Value, JP_COGS_Acct_Value);
	}

	/** Get Product COGS(Search key).
		@return Product COGS(Search key)	  */
	public String getJP_COGS_Acct_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_COGS_Acct_Value);
	}

	/** Set Cost Adjustment(Search Key).
		@param JP_CostAdjustment_Value Cost Adjustment(Search Key)	  */
	public void setJP_CostAdjustment_Value (String JP_CostAdjustment_Value)
	{
		set_ValueNoCheck (COLUMNNAME_JP_CostAdjustment_Value, JP_CostAdjustment_Value);
	}

	/** Get Cost Adjustment(Search Key).
		@return Cost Adjustment(Search Key)	  */
	public String getJP_CostAdjustment_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_CostAdjustment_Value);
	}

	/** Set Inventory Clearing(Search Key).
		@param JP_InventoryClearing_Value Inventory Clearing(Search Key)	  */
	public void setJP_InventoryClearing_Value (String JP_InventoryClearing_Value)
	{
		set_ValueNoCheck (COLUMNNAME_JP_InventoryClearing_Value, JP_InventoryClearing_Value);
	}

	/** Get Inventory Clearing(Search Key).
		@return Inventory Clearing(Search Key)	  */
	public String getJP_InventoryClearing_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_InventoryClearing_Value);
	}

	/** Set Invoice Price Variance(Search Key).
		@param JP_InvoicePriceVariance_Value Invoice Price Variance(Search Key)	  */
	public void setJP_InvoicePriceVariance_Value (String JP_InvoicePriceVariance_Value)
	{
		set_ValueNoCheck (COLUMNNAME_JP_InvoicePriceVariance_Value, JP_InvoicePriceVariance_Value);
	}

	/** Get Invoice Price Variance(Search Key).
		@return Invoice Price Variance(Search Key)	  */
	public String getJP_InvoicePriceVariance_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_InvoicePriceVariance_Value);
	}

	/** Set Landed Cost Clearing(Search Key).
		@param JP_LandedCostClearing_Value Landed Cost Clearing(Search Key)	  */
	public void setJP_LandedCostClearing_Value (String JP_LandedCostClearing_Value)
	{
		set_ValueNoCheck (COLUMNNAME_JP_LandedCostClearing_Value, JP_LandedCostClearing_Value);
	}

	/** Get Landed Cost Clearing(Search Key).
		@return Landed Cost Clearing(Search Key)	  */
	public String getJP_LandedCostClearing_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_LandedCostClearing_Value);
	}

	/** Set Organization(Search Key).
		@param JP_Org_Value Organization(Search Key)	  */
	public void setJP_Org_Value (String JP_Org_Value)
	{
		set_ValueNoCheck (COLUMNNAME_JP_Org_Value, JP_Org_Value);
	}

	/** Get Organization(Search Key).
		@return Organization(Search Key)	  */
	public String getJP_Org_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_Org_Value);
	}

	/** Set Purchase Price Variance(Search Key).
		@param JP_PO_PriceVariance_Value Purchase Price Variance(Search Key)	  */
	public void setJP_PO_PriceVariance_Value (String JP_PO_PriceVariance_Value)
	{
		set_ValueNoCheck (COLUMNNAME_JP_PO_PriceVariance_Value, JP_PO_PriceVariance_Value);
	}

	/** Get Purchase Price Variance(Search Key).
		@return Purchase Price Variance(Search Key)	  */
	public String getJP_PO_PriceVariance_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_PO_PriceVariance_Value);
	}

	/** Set Product Asset(Search Key).
		@param JP_P_Asset_Acct_Value Product Asset(Search Key)	  */
	public void setJP_P_Asset_Acct_Value (String JP_P_Asset_Acct_Value)
	{
		set_ValueNoCheck (COLUMNNAME_JP_P_Asset_Acct_Value, JP_P_Asset_Acct_Value);
	}

	/** Get Product Asset(Search Key).
		@return Product Asset(Search Key)	  */
	public String getJP_P_Asset_Acct_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_P_Asset_Acct_Value);
	}

	/** Set Product Expense(Search Key).
		@param JP_P_Expense_Acct_Value Product Expense(Search Key)	  */
	public void setJP_P_Expense_Acct_Value (String JP_P_Expense_Acct_Value)
	{
		set_ValueNoCheck (COLUMNNAME_JP_P_Expense_Acct_Value, JP_P_Expense_Acct_Value);
	}

	/** Get Product Expense(Search Key).
		@return Product Expense(Search Key)	  */
	public String getJP_P_Expense_Acct_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_P_Expense_Acct_Value);
	}

	/** Set Product Revenue(Search Key).
		@param JP_P_Revenue_Acct_Value Product Revenue(Search Key)	  */
	public void setJP_P_Revenue_Acct_Value (String JP_P_Revenue_Acct_Value)
	{
		set_ValueNoCheck (COLUMNNAME_JP_P_Revenue_Acct_Value, JP_P_Revenue_Acct_Value);
	}

	/** Get Product Revenue(Search Key).
		@return Product Revenue(Search Key)	  */
	public String getJP_P_Revenue_Acct_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_P_Revenue_Acct_Value);
	}

	/** Set Trade Discount Granted(Search Key).
		@param JP_P_TradeDiscountGrant_Value Trade Discount Granted(Search Key)	  */
	public void setJP_P_TradeDiscountGrant_Value (String JP_P_TradeDiscountGrant_Value)
	{
		set_ValueNoCheck (COLUMNNAME_JP_P_TradeDiscountGrant_Value, JP_P_TradeDiscountGrant_Value);
	}

	/** Get Trade Discount Granted(Search Key).
		@return Trade Discount Granted(Search Key)	  */
	public String getJP_P_TradeDiscountGrant_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_P_TradeDiscountGrant_Value);
	}

	/** Set Trade Discount Received(Search Key).
		@param JP_P_TradeDiscountRec_Value Trade Discount Received(Search Key)	  */
	public void setJP_P_TradeDiscountRec_Value (String JP_P_TradeDiscountRec_Value)
	{
		set_ValueNoCheck (COLUMNNAME_JP_P_TradeDiscountRec_Value, JP_P_TradeDiscountRec_Value);
	}

	/** Get Trade Discount Received(Search Key).
		@return Trade Discount Received(Search Key)	  */
	public String getJP_P_TradeDiscountRec_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_P_TradeDiscountRec_Value);
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

	public I_JP_ProductCategoryL1 getJP_ProductCategoryL1() throws RuntimeException
    {
		return (I_JP_ProductCategoryL1)MTable.get(getCtx(), I_JP_ProductCategoryL1.Table_Name)
			.getPO(getJP_ProductCategoryL1_ID(), get_TrxName());	}

	/** Set Product Category Level1.
		@param JP_ProductCategoryL1_ID Product Category Level1	  */
	public void setJP_ProductCategoryL1_ID (int JP_ProductCategoryL1_ID)
	{
		if (JP_ProductCategoryL1_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_ProductCategoryL1_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_ProductCategoryL1_ID, Integer.valueOf(JP_ProductCategoryL1_ID));
	}

	/** Get Product Category Level1.
		@return Product Category Level1	  */
	public int getJP_ProductCategoryL1_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ProductCategoryL1_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Product Category Lv1(Search Key).
		@param JP_ProductCategoryL1_Value Product Category Lv1(Search Key)	  */
	public void setJP_ProductCategoryL1_Value (String JP_ProductCategoryL1_Value)
	{
		set_ValueNoCheck (COLUMNNAME_JP_ProductCategoryL1_Value, JP_ProductCategoryL1_Value);
	}

	/** Get Product Category Lv1(Search Key).
		@return Product Category Lv1(Search Key)	  */
	public String getJP_ProductCategoryL1_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_ProductCategoryL1_Value);
	}

	/** Set Rate Variance(Search Key).
		@param JP_RateVariance_Acct_Value Rate Variance(Search Key)	  */
	public void setJP_RateVariance_Acct_Value (String JP_RateVariance_Acct_Value)
	{
		set_ValueNoCheck (COLUMNNAME_JP_RateVariance_Acct_Value, JP_RateVariance_Acct_Value);
	}

	/** Get Rate Variance(Search Key).
		@return Rate Variance(Search Key)	  */
	public String getJP_RateVariance_Acct_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_RateVariance_Acct_Value);
	}

	/** MMPolicy AD_Reference_ID=335 */
	public static final int MMPOLICY_AD_Reference_ID=335;
	/** LiFo = L */
	public static final String MMPOLICY_LiFo = "L";
	/** FiFo = F */
	public static final String MMPOLICY_FiFo = "F";
	/** Set Material Policy.
		@param MMPolicy 
		Material Movement Policy
	  */
	public void setMMPolicy (String MMPolicy)
	{

		set_Value (COLUMNNAME_MMPolicy, MMPolicy);
	}

	/** Get Material Policy.
		@return Material Movement Policy
	  */
	public String getMMPolicy () 
	{
		return (String)get_Value(COLUMNNAME_MMPolicy);
	}

	public org.compiere.model.I_M_Product_Category getM_Product_Category() throws RuntimeException
    {
		return (org.compiere.model.I_M_Product_Category)MTable.get(getCtx(), org.compiere.model.I_M_Product_Category.Table_Name)
			.getPO(getM_Product_Category_ID(), get_TrxName());	}

	/** Set Product Category.
		@param M_Product_Category_ID 
		Category of a Product
	  */
	public void setM_Product_Category_ID (int M_Product_Category_ID)
	{
		if (M_Product_Category_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_M_Product_Category_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_M_Product_Category_ID, Integer.valueOf(M_Product_Category_ID));
	}

	/** Get Product Category.
		@return Category of a Product
	  */
	public int getM_Product_Category_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Product_Category_ID);
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

	public I_C_ValidCombination getP_Asset_A() throws RuntimeException
    {
		return (I_C_ValidCombination)MTable.get(getCtx(), I_C_ValidCombination.Table_Name)
			.getPO(getP_Asset_Acct(), get_TrxName());	}

	/** Set Product Asset.
		@param P_Asset_Acct 
		Account for Product Asset (Inventory)
	  */
	public void setP_Asset_Acct (int P_Asset_Acct)
	{
		set_Value (COLUMNNAME_P_Asset_Acct, Integer.valueOf(P_Asset_Acct));
	}

	/** Get Product Asset.
		@return Account for Product Asset (Inventory)
	  */
	public int getP_Asset_Acct () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_P_Asset_Acct);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_ValidCombination getP_AverageCostVariance_A() throws RuntimeException
    {
		return (I_C_ValidCombination)MTable.get(getCtx(), I_C_ValidCombination.Table_Name)
			.getPO(getP_AverageCostVariance_Acct(), get_TrxName());	}

	/** Set Average Cost Variance.
		@param P_AverageCostVariance_Acct 
		Average Cost Variance
	  */
	public void setP_AverageCostVariance_Acct (int P_AverageCostVariance_Acct)
	{
		set_Value (COLUMNNAME_P_AverageCostVariance_Acct, Integer.valueOf(P_AverageCostVariance_Acct));
	}

	/** Get Average Cost Variance.
		@return Average Cost Variance
	  */
	public int getP_AverageCostVariance_Acct () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_P_AverageCostVariance_Acct);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_ValidCombination getP_COGS_A() throws RuntimeException
    {
		return (I_C_ValidCombination)MTable.get(getCtx(), I_C_ValidCombination.Table_Name)
			.getPO(getP_COGS_Acct(), get_TrxName());	}

	/** Set Product COGS.
		@param P_COGS_Acct 
		Account for Cost of Goods Sold
	  */
	public void setP_COGS_Acct (int P_COGS_Acct)
	{
		set_Value (COLUMNNAME_P_COGS_Acct, Integer.valueOf(P_COGS_Acct));
	}

	/** Get Product COGS.
		@return Account for Cost of Goods Sold
	  */
	public int getP_COGS_Acct () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_P_COGS_Acct);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_ValidCombination getP_CostAdjustment_A() throws RuntimeException
    {
		return (I_C_ValidCombination)MTable.get(getCtx(), I_C_ValidCombination.Table_Name)
			.getPO(getP_CostAdjustment_Acct(), get_TrxName());	}

	/** Set Cost Adjustment.
		@param P_CostAdjustment_Acct 
		Product Cost Adjustment Account
	  */
	public void setP_CostAdjustment_Acct (int P_CostAdjustment_Acct)
	{
		set_Value (COLUMNNAME_P_CostAdjustment_Acct, Integer.valueOf(P_CostAdjustment_Acct));
	}

	/** Get Cost Adjustment.
		@return Product Cost Adjustment Account
	  */
	public int getP_CostAdjustment_Acct () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_P_CostAdjustment_Acct);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_ValidCombination getP_Expense_A() throws RuntimeException
    {
		return (I_C_ValidCombination)MTable.get(getCtx(), I_C_ValidCombination.Table_Name)
			.getPO(getP_Expense_Acct(), get_TrxName());	}

	/** Set Product Expense.
		@param P_Expense_Acct 
		Account for Product Expense
	  */
	public void setP_Expense_Acct (int P_Expense_Acct)
	{
		set_Value (COLUMNNAME_P_Expense_Acct, Integer.valueOf(P_Expense_Acct));
	}

	/** Get Product Expense.
		@return Account for Product Expense
	  */
	public int getP_Expense_Acct () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_P_Expense_Acct);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_ValidCombination getP_InventoryClearing_A() throws RuntimeException
    {
		return (I_C_ValidCombination)MTable.get(getCtx(), I_C_ValidCombination.Table_Name)
			.getPO(getP_InventoryClearing_Acct(), get_TrxName());	}

	/** Set Inventory Clearing.
		@param P_InventoryClearing_Acct 
		Product Inventory Clearing Account
	  */
	public void setP_InventoryClearing_Acct (int P_InventoryClearing_Acct)
	{
		set_Value (COLUMNNAME_P_InventoryClearing_Acct, Integer.valueOf(P_InventoryClearing_Acct));
	}

	/** Get Inventory Clearing.
		@return Product Inventory Clearing Account
	  */
	public int getP_InventoryClearing_Acct () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_P_InventoryClearing_Acct);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_ValidCombination getP_InvoicePriceVariance_A() throws RuntimeException
    {
		return (I_C_ValidCombination)MTable.get(getCtx(), I_C_ValidCombination.Table_Name)
			.getPO(getP_InvoicePriceVariance_Acct(), get_TrxName());	}

	/** Set Invoice Price Variance.
		@param P_InvoicePriceVariance_Acct 
		Difference between Costs and Invoice Price (IPV)
	  */
	public void setP_InvoicePriceVariance_Acct (int P_InvoicePriceVariance_Acct)
	{
		set_Value (COLUMNNAME_P_InvoicePriceVariance_Acct, Integer.valueOf(P_InvoicePriceVariance_Acct));
	}

	/** Get Invoice Price Variance.
		@return Difference between Costs and Invoice Price (IPV)
	  */
	public int getP_InvoicePriceVariance_Acct () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_P_InvoicePriceVariance_Acct);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_ValidCombination getP_LandedCostClearing_A() throws RuntimeException
    {
		return (I_C_ValidCombination)MTable.get(getCtx(), I_C_ValidCombination.Table_Name)
			.getPO(getP_LandedCostClearing_Acct(), get_TrxName());	}

	/** Set Landed Cost Clearing.
		@param P_LandedCostClearing_Acct 
		Product Landed Cost Clearing Account
	  */
	public void setP_LandedCostClearing_Acct (int P_LandedCostClearing_Acct)
	{
		set_Value (COLUMNNAME_P_LandedCostClearing_Acct, Integer.valueOf(P_LandedCostClearing_Acct));
	}

	/** Get Landed Cost Clearing.
		@return Product Landed Cost Clearing Account
	  */
	public int getP_LandedCostClearing_Acct () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_P_LandedCostClearing_Acct);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_ValidCombination getP_PurchasePriceVariance_A() throws RuntimeException
    {
		return (I_C_ValidCombination)MTable.get(getCtx(), I_C_ValidCombination.Table_Name)
			.getPO(getP_PurchasePriceVariance_Acct(), get_TrxName());	}

	/** Set Purchase Price Variance.
		@param P_PurchasePriceVariance_Acct 
		Difference between Standard Cost and Purchase Price (PPV)
	  */
	public void setP_PurchasePriceVariance_Acct (int P_PurchasePriceVariance_Acct)
	{
		set_Value (COLUMNNAME_P_PurchasePriceVariance_Acct, Integer.valueOf(P_PurchasePriceVariance_Acct));
	}

	/** Get Purchase Price Variance.
		@return Difference between Standard Cost and Purchase Price (PPV)
	  */
	public int getP_PurchasePriceVariance_Acct () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_P_PurchasePriceVariance_Acct);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_ValidCombination getP_RateVariance_A() throws RuntimeException
    {
		return (I_C_ValidCombination)MTable.get(getCtx(), I_C_ValidCombination.Table_Name)
			.getPO(getP_RateVariance_Acct(), get_TrxName());	}

	/** Set Rate Variance.
		@param P_RateVariance_Acct 
		The Rate Variance account is the account used Manufacturing Order
	  */
	public void setP_RateVariance_Acct (int P_RateVariance_Acct)
	{
		set_Value (COLUMNNAME_P_RateVariance_Acct, Integer.valueOf(P_RateVariance_Acct));
	}

	/** Get Rate Variance.
		@return The Rate Variance account is the account used Manufacturing Order
	  */
	public int getP_RateVariance_Acct () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_P_RateVariance_Acct);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_ValidCombination getP_Revenue_A() throws RuntimeException
    {
		return (I_C_ValidCombination)MTable.get(getCtx(), I_C_ValidCombination.Table_Name)
			.getPO(getP_Revenue_Acct(), get_TrxName());	}

	/** Set Product Revenue.
		@param P_Revenue_Acct 
		Account for Product Revenue (Sales Account)
	  */
	public void setP_Revenue_Acct (int P_Revenue_Acct)
	{
		set_Value (COLUMNNAME_P_Revenue_Acct, Integer.valueOf(P_Revenue_Acct));
	}

	/** Get Product Revenue.
		@return Account for Product Revenue (Sales Account)
	  */
	public int getP_Revenue_Acct () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_P_Revenue_Acct);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_ValidCombination getP_TradeDiscountGrant_A() throws RuntimeException
    {
		return (I_C_ValidCombination)MTable.get(getCtx(), I_C_ValidCombination.Table_Name)
			.getPO(getP_TradeDiscountGrant_Acct(), get_TrxName());	}

	/** Set Trade Discount Granted.
		@param P_TradeDiscountGrant_Acct 
		Trade Discount Granted Account
	  */
	public void setP_TradeDiscountGrant_Acct (int P_TradeDiscountGrant_Acct)
	{
		set_Value (COLUMNNAME_P_TradeDiscountGrant_Acct, Integer.valueOf(P_TradeDiscountGrant_Acct));
	}

	/** Get Trade Discount Granted.
		@return Trade Discount Granted Account
	  */
	public int getP_TradeDiscountGrant_Acct () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_P_TradeDiscountGrant_Acct);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_ValidCombination getP_TradeDiscountRec_A() throws RuntimeException
    {
		return (I_C_ValidCombination)MTable.get(getCtx(), I_C_ValidCombination.Table_Name)
			.getPO(getP_TradeDiscountRec_Acct(), get_TrxName());	}

	/** Set Trade Discount Received.
		@param P_TradeDiscountRec_Acct 
		Trade Discount Receivable Account
	  */
	public void setP_TradeDiscountRec_Acct (int P_TradeDiscountRec_Acct)
	{
		set_Value (COLUMNNAME_P_TradeDiscountRec_Acct, Integer.valueOf(P_TradeDiscountRec_Acct));
	}

	/** Get Trade Discount Received.
		@return Trade Discount Receivable Account
	  */
	public int getP_TradeDiscountRec_Acct () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_P_TradeDiscountRec_Acct);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Planned Margin %.
		@param PlannedMargin 
		Project's planned margin as a percentage
	  */
	public void setPlannedMargin (BigDecimal PlannedMargin)
	{
		set_Value (COLUMNNAME_PlannedMargin, PlannedMargin);
	}

	/** Get Planned Margin %.
		@return Project's planned margin as a percentage
	  */
	public BigDecimal getPlannedMargin () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_PlannedMargin);
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
}