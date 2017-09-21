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

import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.*;

/** Generated Model for JP_Contract_Product_Acct
 *  @author iDempiere (generated) 
 *  @version Release 4.1 - $Id$ */
public class X_JP_Contract_Product_Acct extends PO implements I_JP_Contract_Product_Acct, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20170921L;

    /** Standard Constructor */
    public X_JP_Contract_Product_Acct (Properties ctx, int JP_Contract_Product_Acct_ID, String trxName)
    {
      super (ctx, JP_Contract_Product_Acct_ID, trxName);
      /** if (JP_Contract_Product_Acct_ID == 0)
        {
			setC_AcctSchema_ID (0);
			setJP_Contract_Acct_ID (0);
			setJP_Contract_Product_Acct_ID (0);
			setM_Product_Category_ID (0);
        } */
    }

    /** Load Constructor */
    public X_JP_Contract_Product_Acct (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_JP_Contract_Product_Acct[")
        .append(get_ID()).append("]");
      return sb.toString();
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

	public I_JP_Contract_Acct getJP_Contract_Acct() throws RuntimeException
    {
		return (I_JP_Contract_Acct)MTable.get(getCtx(), I_JP_Contract_Acct.Table_Name)
			.getPO(getJP_Contract_Acct_ID(), get_TrxName());	}

	/** Set Contract Acct Info.
		@param JP_Contract_Acct_ID Contract Acct Info	  */
	public void setJP_Contract_Acct_ID (int JP_Contract_Acct_ID)
	{
		if (JP_Contract_Acct_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_Contract_Acct_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_Contract_Acct_ID, Integer.valueOf(JP_Contract_Acct_ID));
	}

	/** Get Contract Acct Info.
		@return Contract Acct Info	  */
	public int getJP_Contract_Acct_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Contract_Acct_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Product Contract Acct.
		@param JP_Contract_Product_Acct_ID Product Contract Acct	  */
	public void setJP_Contract_Product_Acct_ID (int JP_Contract_Product_Acct_ID)
	{
		if (JP_Contract_Product_Acct_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_Contract_Product_Acct_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_Contract_Product_Acct_ID, Integer.valueOf(JP_Contract_Product_Acct_ID));
	}

	/** Get Product Contract Acct.
		@return Product Contract Acct	  */
	public int getJP_Contract_Product_Acct_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Contract_Product_Acct_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set JP_Contract_Product_Acct_UU.
		@param JP_Contract_Product_Acct_UU JP_Contract_Product_Acct_UU	  */
	public void setJP_Contract_Product_Acct_UU (String JP_Contract_Product_Acct_UU)
	{
		set_ValueNoCheck (COLUMNNAME_JP_Contract_Product_Acct_UU, JP_Contract_Product_Acct_UU);
	}

	/** Get JP_Contract_Product_Acct_UU.
		@return JP_Contract_Product_Acct_UU	  */
	public String getJP_Contract_Product_Acct_UU () 
	{
		return (String)get_Value(COLUMNNAME_JP_Contract_Product_Acct_UU);
	}

	public I_C_ValidCombination getJP_Expense_A() throws RuntimeException
    {
		return (I_C_ValidCombination)MTable.get(getCtx(), I_C_ValidCombination.Table_Name)
			.getPO(getJP_Expense_Acct(), get_TrxName());	}

	/** Set Product Expense(Recognition Doc).
		@param JP_Expense_Acct Product Expense(Recognition Doc)	  */
	public void setJP_Expense_Acct (int JP_Expense_Acct)
	{
		set_Value (COLUMNNAME_JP_Expense_Acct, Integer.valueOf(JP_Expense_Acct));
	}

	/** Get Product Expense(Recognition Doc).
		@return Product Expense(Recognition Doc)	  */
	public int getJP_Expense_Acct () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Expense_Acct);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_ValidCombination getJP_PurchaseOffset_A() throws RuntimeException
    {
		return (I_C_ValidCombination)MTable.get(getCtx(), I_C_ValidCombination.Table_Name)
			.getPO(getJP_PurchaseOffset_Acct(), get_TrxName());	}

	/** Set Purchase Offset Acct.
		@param JP_PurchaseOffset_Acct Purchase Offset Acct	  */
	public void setJP_PurchaseOffset_Acct (int JP_PurchaseOffset_Acct)
	{
		set_Value (COLUMNNAME_JP_PurchaseOffset_Acct, Integer.valueOf(JP_PurchaseOffset_Acct));
	}

	/** Get Purchase Offset Acct.
		@return Purchase Offset Acct	  */
	public int getJP_PurchaseOffset_Acct () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_PurchaseOffset_Acct);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_ValidCombination getJP_Purchase_A() throws RuntimeException
    {
		return (I_C_ValidCombination)MTable.get(getCtx(), I_C_ValidCombination.Table_Name)
			.getPO(getJP_Purchase_Acct(), get_TrxName());	}

	/** Set Purchase Acct.
		@param JP_Purchase_Acct Purchase Acct	  */
	public void setJP_Purchase_Acct (int JP_Purchase_Acct)
	{
		set_Value (COLUMNNAME_JP_Purchase_Acct, Integer.valueOf(JP_Purchase_Acct));
	}

	/** Get Purchase Acct.
		@return Purchase Acct	  */
	public int getJP_Purchase_Acct () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Purchase_Acct);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_ValidCombination getJP_Revenue_A() throws RuntimeException
    {
		return (I_C_ValidCombination)MTable.get(getCtx(), I_C_ValidCombination.Table_Name)
			.getPO(getJP_Revenue_Acct(), get_TrxName());	}

	/** Set Product Revenue(Recognition Doc).
		@param JP_Revenue_Acct Product Revenue(Recognition Doc)	  */
	public void setJP_Revenue_Acct (int JP_Revenue_Acct)
	{
		set_Value (COLUMNNAME_JP_Revenue_Acct, Integer.valueOf(JP_Revenue_Acct));
	}

	/** Get Product Revenue(Recognition Doc).
		@return Product Revenue(Recognition Doc)	  */
	public int getJP_Revenue_Acct () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Revenue_Acct);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_ValidCombination getJP_TradeDiscountGrant_A() throws RuntimeException
    {
		return (I_C_ValidCombination)MTable.get(getCtx(), I_C_ValidCombination.Table_Name)
			.getPO(getJP_TradeDiscountGrant_Acct(), get_TrxName());	}

	/** Set Trade Discount Granted(Recognition Doc).
		@param JP_TradeDiscountGrant_Acct 
		Trade Discount Granted Account
	  */
	public void setJP_TradeDiscountGrant_Acct (int JP_TradeDiscountGrant_Acct)
	{
		set_Value (COLUMNNAME_JP_TradeDiscountGrant_Acct, Integer.valueOf(JP_TradeDiscountGrant_Acct));
	}

	/** Get Trade Discount Granted(Recognition Doc).
		@return Trade Discount Granted Account
	  */
	public int getJP_TradeDiscountGrant_Acct () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_TradeDiscountGrant_Acct);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_ValidCombination getJP_TradeDiscountRec_A() throws RuntimeException
    {
		return (I_C_ValidCombination)MTable.get(getCtx(), I_C_ValidCombination.Table_Name)
			.getPO(getJP_TradeDiscountRec_Acct(), get_TrxName());	}

	/** Set Trade Discount Received(Recognition).
		@param JP_TradeDiscountRec_Acct 
		Trade Discount Receivable Account
	  */
	public void setJP_TradeDiscountRec_Acct (int JP_TradeDiscountRec_Acct)
	{
		set_Value (COLUMNNAME_JP_TradeDiscountRec_Acct, Integer.valueOf(JP_TradeDiscountRec_Acct));
	}

	/** Get Trade Discount Received(Recognition).
		@return Trade Discount Receivable Account
	  */
	public int getJP_TradeDiscountRec_Acct () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_TradeDiscountRec_Acct);
		if (ii == null)
			 return 0;
		return ii.intValue();
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
}