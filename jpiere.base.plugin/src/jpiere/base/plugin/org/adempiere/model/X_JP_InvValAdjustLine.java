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
import org.compiere.util.KeyNamePair;

/** Generated Model for JP_InvValAdjustLine
 *  @author iDempiere (generated) 
 *  @version Release 6.2 - $Id$ */
public class X_JP_InvValAdjustLine extends PO implements I_JP_InvValAdjustLine, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20190427L;

    /** Standard Constructor */
    public X_JP_InvValAdjustLine (Properties ctx, int JP_InvValAdjustLine_ID, String trxName)
    {
      super (ctx, JP_InvValAdjustLine_ID, trxName);
      /** if (JP_InvValAdjustLine_ID == 0)
        {
			setAccount_ID (0);
			setAmtAcctBalance (Env.ZERO);
// 0
			setAmtAcctCr (Env.ZERO);
// 0
			setAmtAcctDr (Env.ZERO);
// 0
			setC_AcctSchema_ID (0);
			setCostingLevel (null);
			setCostingMethod (null);
// x
			setDifferenceAmt (Env.ZERO);
// 0
			setDifferenceQty (Env.ZERO);
// 0
			setIsConfirmed (false);
// N
			setJP_InvValAdjustLine_ID (0);
			setJP_InvValAdjust_ID (0);
			setJP_InvValAmt (Env.ZERO);
// 0
			setJP_InvValTotalAmt (Env.ZERO);
// 0
			setLine (0);
			setM_Product_ID (0);
			setQtyBook (Env.ZERO);
// 0
			setQtyOnHand (Env.ZERO);
// 0
        } */
    }

    /** Load Constructor */
    public X_JP_InvValAdjustLine (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_JP_InvValAdjustLine[")
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

	public org.compiere.model.I_C_ElementValue getAccount() throws RuntimeException
    {
		return (org.compiere.model.I_C_ElementValue)MTable.get(getCtx(), org.compiere.model.I_C_ElementValue.Table_Name)
			.getPO(getAccount_ID(), get_TrxName());	}

	/** Set Account.
		@param Account_ID 
		Account used
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
	public int getAccount_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Account_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Accounted Balance.
		@param AmtAcctBalance 
		Accounted Balance Amount
	  */
	public void setAmtAcctBalance (BigDecimal AmtAcctBalance)
	{
		set_Value (COLUMNNAME_AmtAcctBalance, AmtAcctBalance);
	}

	/** Get Accounted Balance.
		@return Accounted Balance Amount
	  */
	public BigDecimal getAmtAcctBalance () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_AmtAcctBalance);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Accounted Credit.
		@param AmtAcctCr 
		Accounted Credit Amount
	  */
	public void setAmtAcctCr (BigDecimal AmtAcctCr)
	{
		set_Value (COLUMNNAME_AmtAcctCr, AmtAcctCr);
	}

	/** Get Accounted Credit.
		@return Accounted Credit Amount
	  */
	public BigDecimal getAmtAcctCr () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_AmtAcctCr);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Accounted Debit.
		@param AmtAcctDr 
		Accounted Debit Amount
	  */
	public void setAmtAcctDr (BigDecimal AmtAcctDr)
	{
		set_Value (COLUMNNAME_AmtAcctDr, AmtAcctDr);
	}

	/** Get Accounted Debit.
		@return Accounted Debit Amount
	  */
	public BigDecimal getAmtAcctDr () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_AmtAcctDr);
		if (bd == null)
			 return Env.ZERO;
		return bd;
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

	/** Average PO = A */
	public static final String COSTINGMETHOD_AveragePO = "A";
	/** Fifo = F */
	public static final String COSTINGMETHOD_Fifo = "F";
	/** Average Invoice = I */
	public static final String COSTINGMETHOD_AverageInvoice = "I";
	/** Lifo = L */
	public static final String COSTINGMETHOD_Lifo = "L";
	/** Standard Costing = S */
	public static final String COSTINGMETHOD_StandardCosting = "S";
	/** User Defined = U */
	public static final String COSTINGMETHOD_UserDefined = "U";
	/** Last Invoice = i */
	public static final String COSTINGMETHOD_LastInvoice = "i";
	/** Last PO Price = p */
	public static final String COSTINGMETHOD_LastPOPrice = "p";
	/** _ = x */
	public static final String COSTINGMETHOD__ = "x";
	/** Retail Inventory method = R */
	public static final String COSTINGMETHOD_RetailInventoryMethod = "R";
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

	/** Set Difference.
		@param DifferenceAmt 
		Difference Amount
	  */
	public void setDifferenceAmt (BigDecimal DifferenceAmt)
	{
		set_Value (COLUMNNAME_DifferenceAmt, DifferenceAmt);
	}

	/** Get Difference.
		@return Difference Amount
	  */
	public BigDecimal getDifferenceAmt () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_DifferenceAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Difference.
		@param DifferenceQty 
		Difference Quantity
	  */
	public void setDifferenceQty (BigDecimal DifferenceQty)
	{
		set_Value (COLUMNNAME_DifferenceQty, DifferenceQty);
	}

	/** Get Difference.
		@return Difference Quantity
	  */
	public BigDecimal getDifferenceQty () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_DifferenceQty);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Confirmed.
		@param IsConfirmed 
		Assignment is confirmed
	  */
	public void setIsConfirmed (boolean IsConfirmed)
	{
		set_Value (COLUMNNAME_IsConfirmed, Boolean.valueOf(IsConfirmed));
	}

	/** Get Confirmed.
		@return Assignment is confirmed
	  */
	public boolean isConfirmed () 
	{
		Object oo = get_Value(COLUMNNAME_IsConfirmed);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Inventory Valuation Adjust Doc Line.
		@param JP_InvValAdjustLine_ID Inventory Valuation Adjust Doc Line	  */
	public void setJP_InvValAdjustLine_ID (int JP_InvValAdjustLine_ID)
	{
		if (JP_InvValAdjustLine_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_InvValAdjustLine_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_InvValAdjustLine_ID, Integer.valueOf(JP_InvValAdjustLine_ID));
	}

	/** Get Inventory Valuation Adjust Doc Line.
		@return Inventory Valuation Adjust Doc Line	  */
	public int getJP_InvValAdjustLine_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_InvValAdjustLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set JP_InvValAdjustLine_UU.
		@param JP_InvValAdjustLine_UU JP_InvValAdjustLine_UU	  */
	public void setJP_InvValAdjustLine_UU (String JP_InvValAdjustLine_UU)
	{
		set_ValueNoCheck (COLUMNNAME_JP_InvValAdjustLine_UU, JP_InvValAdjustLine_UU);
	}

	/** Get JP_InvValAdjustLine_UU.
		@return JP_InvValAdjustLine_UU	  */
	public String getJP_InvValAdjustLine_UU () 
	{
		return (String)get_Value(COLUMNNAME_JP_InvValAdjustLine_UU);
	}

	public I_JP_InvValAdjust getJP_InvValAdjust() throws RuntimeException
    {
		return (I_JP_InvValAdjust)MTable.get(getCtx(), I_JP_InvValAdjust.Table_Name)
			.getPO(getJP_InvValAdjust_ID(), get_TrxName());	}

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

    /** Get Record ID/ColumnName
        @return ID/ColumnName pair
      */
    public KeyNamePair getKeyNamePair() 
    {
        return new KeyNamePair(get_ID(), String.valueOf(getJP_InvValAdjust_ID()));
    }

	/** Set Inventory Valuation Amount.
		@param JP_InvValAmt Inventory Valuation Amount	  */
	public void setJP_InvValAmt (BigDecimal JP_InvValAmt)
	{
		set_Value (COLUMNNAME_JP_InvValAmt, JP_InvValAmt);
	}

	/** Get Inventory Valuation Amount.
		@return Inventory Valuation Amount	  */
	public BigDecimal getJP_InvValAmt () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_JP_InvValAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	public I_JP_InvValCalLine getJP_InvValCalLine() throws RuntimeException
    {
		return (I_JP_InvValCalLine)MTable.get(getCtx(), I_JP_InvValCalLine.Table_Name)
			.getPO(getJP_InvValCalLine_ID(), get_TrxName());	}

	/** Set Inventory Valuation Calculate Document Line.
		@param JP_InvValCalLine_ID Inventory Valuation Calculate Document Line	  */
	public void setJP_InvValCalLine_ID (int JP_InvValCalLine_ID)
	{
		if (JP_InvValCalLine_ID < 1) 
			set_Value (COLUMNNAME_JP_InvValCalLine_ID, null);
		else 
			set_Value (COLUMNNAME_JP_InvValCalLine_ID, Integer.valueOf(JP_InvValCalLine_ID));
	}

	/** Get Inventory Valuation Calculate Document Line.
		@return Inventory Valuation Calculate Document Line	  */
	public int getJP_InvValCalLine_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_InvValCalLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Inventory Valuation Total Amount.
		@param JP_InvValTotalAmt Inventory Valuation Total Amount	  */
	public void setJP_InvValTotalAmt (BigDecimal JP_InvValTotalAmt)
	{
		set_Value (COLUMNNAME_JP_InvValTotalAmt, JP_InvValTotalAmt);
	}

	/** Get Inventory Valuation Total Amount.
		@return Inventory Valuation Total Amount	  */
	public BigDecimal getJP_InvValTotalAmt () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_JP_InvValTotalAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	public org.compiere.model.I_GL_JournalLine getJP_JournalLineCr() throws RuntimeException
    {
		return (org.compiere.model.I_GL_JournalLine)MTable.get(getCtx(), org.compiere.model.I_GL_JournalLine.Table_Name)
			.getPO(getJP_JournalLineCr_ID(), get_TrxName());	}

	/** Set Journal Line CR.
		@param JP_JournalLineCr_ID Journal Line CR	  */
	public void setJP_JournalLineCr_ID (int JP_JournalLineCr_ID)
	{
		if (JP_JournalLineCr_ID < 1) 
			set_Value (COLUMNNAME_JP_JournalLineCr_ID, null);
		else 
			set_Value (COLUMNNAME_JP_JournalLineCr_ID, Integer.valueOf(JP_JournalLineCr_ID));
	}

	/** Get Journal Line CR.
		@return Journal Line CR	  */
	public int getJP_JournalLineCr_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_JournalLineCr_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_GL_JournalLine getJP_JournalLineDr() throws RuntimeException
    {
		return (org.compiere.model.I_GL_JournalLine)MTable.get(getCtx(), org.compiere.model.I_GL_JournalLine.Table_Name)
			.getPO(getJP_JournalLineDr_ID(), get_TrxName());	}

	/** Set Journal Line DR.
		@param JP_JournalLineDr_ID Journal Line DR	  */
	public void setJP_JournalLineDr_ID (int JP_JournalLineDr_ID)
	{
		if (JP_JournalLineDr_ID < 1) 
			set_Value (COLUMNNAME_JP_JournalLineDr_ID, null);
		else 
			set_Value (COLUMNNAME_JP_JournalLineDr_ID, Integer.valueOf(JP_JournalLineDr_ID));
	}

	/** Get Journal Line DR.
		@return Journal Line DR	  */
	public int getJP_JournalLineDr_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_JournalLineDr_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
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

	public I_M_AttributeSetInstance getM_AttributeSetInstance() throws RuntimeException
    {
		return (I_M_AttributeSetInstance)MTable.get(getCtx(), I_M_AttributeSetInstance.Table_Name)
			.getPO(getM_AttributeSetInstance_ID(), get_TrxName());	}

	/** Set Attribute Info.
		@param M_AttributeSetInstance_ID 
		Product Attribute Set Instance
	  */
	public void setM_AttributeSetInstance_ID (int M_AttributeSetInstance_ID)
	{
		if (M_AttributeSetInstance_ID < 0) 
			set_Value (COLUMNNAME_M_AttributeSetInstance_ID, null);
		else 
			set_Value (COLUMNNAME_M_AttributeSetInstance_ID, Integer.valueOf(M_AttributeSetInstance_ID));
	}

	/** Get Attribute Info.
		@return Product Attribute Set Instance
	  */
	public int getM_AttributeSetInstance_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_AttributeSetInstance_ID);
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

	/** Set Quantity book.
		@param QtyBook 
		Book Quantity
	  */
	public void setQtyBook (BigDecimal QtyBook)
	{
		set_Value (COLUMNNAME_QtyBook, QtyBook);
	}

	/** Get Quantity book.
		@return Book Quantity
	  */
	public BigDecimal getQtyBook () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_QtyBook);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set On Hand Quantity.
		@param QtyOnHand 
		On Hand Quantity
	  */
	public void setQtyOnHand (BigDecimal QtyOnHand)
	{
		set_Value (COLUMNNAME_QtyOnHand, QtyOnHand);
	}

	/** Get On Hand Quantity.
		@return On Hand Quantity
	  */
	public BigDecimal getQtyOnHand () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_QtyOnHand);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}
}