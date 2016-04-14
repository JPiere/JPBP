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

/** Generated Model for JP_InvValCalLine
 *  @author iDempiere (generated) 
 *  @version Release 3.1 - $Id$ */
public class X_JP_InvValCalLine extends PO implements I_JP_InvValCalLine, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20160414L;

    /** Standard Constructor */
    public X_JP_InvValCalLine (Properties ctx, int JP_InvValCalLine_ID, String trxName)
    {
      super (ctx, JP_InvValCalLine_ID, trxName);
      /** if (JP_InvValCalLine_ID == 0)
        {
			setCostingLevel (null);
			setCostingMethod (null);
// x
			setCurrentCostPrice (Env.ZERO);
// 0
			setFutureCostPrice (Env.ZERO);
// 0
			setJP_InvValAmt (Env.ZERO);
// 0
			setJP_InvValCalLine_ID (0);
			setJP_InvValCal_ID (0);
			setJP_InvValTotalAmt (Env.ZERO);
// 0
			setLine (0);
// @SQL=SELECT COALESCE(MAX(Line),0)+10 AS DefaultValue FROM JP_InvValCalLine WHERE JP_InvValCal_ID=@JP_InvValCal_ID@
			setM_Product_ID (0);
			setQtyBook (Env.ZERO);
// 0
        } */
    }

    /** Load Constructor */
    public X_JP_InvValCalLine (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_JP_InvValCalLine[")
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

	/** Set Current Cost Price.
		@param CurrentCostPrice 
		The currently used cost price
	  */
	public void setCurrentCostPrice (BigDecimal CurrentCostPrice)
	{
		set_Value (COLUMNNAME_CurrentCostPrice, CurrentCostPrice);
	}

	/** Get Current Cost Price.
		@return The currently used cost price
	  */
	public BigDecimal getCurrentCostPrice () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_CurrentCostPrice);
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

	/** Set Future Cost Price.
		@param FutureCostPrice Future Cost Price	  */
	public void setFutureCostPrice (BigDecimal FutureCostPrice)
	{
		set_Value (COLUMNNAME_FutureCostPrice, FutureCostPrice);
	}

	/** Get Future Cost Price.
		@return Future Cost Price	  */
	public BigDecimal getFutureCostPrice () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_FutureCostPrice);
		if (bd == null)
			 return Env.ZERO;
		return bd;
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

	/** Set Inventory Valuation Calculate Document Line.
		@param JP_InvValCalLine_ID Inventory Valuation Calculate Document Line	  */
	public void setJP_InvValCalLine_ID (int JP_InvValCalLine_ID)
	{
		if (JP_InvValCalLine_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_InvValCalLine_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_InvValCalLine_ID, Integer.valueOf(JP_InvValCalLine_ID));
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

	/** Set JP_InvValCalLine_UU.
		@param JP_InvValCalLine_UU JP_InvValCalLine_UU	  */
	public void setJP_InvValCalLine_UU (String JP_InvValCalLine_UU)
	{
		set_ValueNoCheck (COLUMNNAME_JP_InvValCalLine_UU, JP_InvValCalLine_UU);
	}

	/** Get JP_InvValCalLine_UU.
		@return JP_InvValCalLine_UU	  */
	public String getJP_InvValCalLine_UU () 
	{
		return (String)get_Value(COLUMNNAME_JP_InvValCalLine_UU);
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

    /** Get Record ID/ColumnName
        @return ID/ColumnName pair
      */
    public KeyNamePair getKeyNamePair() 
    {
        return new KeyNamePair(get_ID(), String.valueOf(getJP_InvValCal_ID()));
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

	/** Set Line No.
		@param Line 
		Unique line for this document
	  */
	public void setLine (int Line)
	{
		set_ValueNoCheck (COLUMNNAME_Line, Integer.valueOf(Line));
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

	/** Set Attribute Set Instance.
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

	/** Get Attribute Set Instance.
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
}