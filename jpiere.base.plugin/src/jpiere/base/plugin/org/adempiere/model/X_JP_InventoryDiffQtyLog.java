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

/** Generated Model for JP_InventoryDiffQtyLog
 *  @author iDempiere (generated) 
 *  @version Release 3.1 - $Id$ */
public class X_JP_InventoryDiffQtyLog extends PO implements I_JP_InventoryDiffQtyLog, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20160412L;

    /** Standard Constructor */
    public X_JP_InventoryDiffQtyLog (Properties ctx, int JP_InventoryDiffQtyLog_ID, String trxName)
    {
      super (ctx, JP_InventoryDiffQtyLog_ID, trxName);
      /** if (JP_InventoryDiffQtyLog_ID == 0)
        {
			setJP_InvValAdjustLine_ID (0);
			setJP_InventoryDiffQtyLog_ID (0);
			setLine (0);
// @SQL=SELECT COALESCE(MAX(Line),0)+10 AS DefaultValue FROM JP_InventoryDiffQtyLog WHERE JP_InvValAdjustLine_ID=@JP_InvValAdjustLine_ID@
        } */
    }

    /** Load Constructor */
    public X_JP_InventoryDiffQtyLog (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_JP_InventoryDiffQtyLog[")
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

	/** Set Adjust to Acct Qty.
		@param JP_AdjustToAcctQty Adjust to Acct Qty	  */
	public void setJP_AdjustToAcctQty (BigDecimal JP_AdjustToAcctQty)
	{
		set_Value (COLUMNNAME_JP_AdjustToAcctQty, JP_AdjustToAcctQty);
	}

	/** Get Adjust to Acct Qty.
		@return Adjust to Acct Qty	  */
	public BigDecimal getJP_AdjustToAcctQty () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_JP_AdjustToAcctQty);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	public I_JP_InvValAdjustLine getJP_InvValAdjustLine() throws RuntimeException
    {
		return (I_JP_InvValAdjustLine)MTable.get(getCtx(), I_JP_InvValAdjustLine.Table_Name)
			.getPO(getJP_InvValAdjustLine_ID(), get_TrxName());	}

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

	/** Set Inventory Difference Qty Log.
		@param JP_InventoryDiffQtyLog_ID Inventory Difference Qty Log	  */
	public void setJP_InventoryDiffQtyLog_ID (int JP_InventoryDiffQtyLog_ID)
	{
		if (JP_InventoryDiffQtyLog_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_InventoryDiffQtyLog_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_InventoryDiffQtyLog_ID, Integer.valueOf(JP_InventoryDiffQtyLog_ID));
	}

	/** Get Inventory Difference Qty Log.
		@return Inventory Difference Qty Log	  */
	public int getJP_InventoryDiffQtyLog_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_InventoryDiffQtyLog_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set JP_InventoryDiffQtyLog_UU.
		@param JP_InventoryDiffQtyLog_UU JP_InventoryDiffQtyLog_UU	  */
	public void setJP_InventoryDiffQtyLog_UU (String JP_InventoryDiffQtyLog_UU)
	{
		set_ValueNoCheck (COLUMNNAME_JP_InventoryDiffQtyLog_UU, JP_InventoryDiffQtyLog_UU);
	}

	/** Get JP_InventoryDiffQtyLog_UU.
		@return JP_InventoryDiffQtyLog_UU	  */
	public String getJP_InventoryDiffQtyLog_UU () 
	{
		return (String)get_Value(COLUMNNAME_JP_InventoryDiffQtyLog_UU);
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

	public org.compiere.model.I_M_InOutLine getM_InOutLine() throws RuntimeException
    {
		return (org.compiere.model.I_M_InOutLine)MTable.get(getCtx(), org.compiere.model.I_M_InOutLine.Table_Name)
			.getPO(getM_InOutLine_ID(), get_TrxName());	}

	/** Set Shipment/Receipt Line.
		@param M_InOutLine_ID 
		Line on Shipment or Receipt document
	  */
	public void setM_InOutLine_ID (int M_InOutLine_ID)
	{
		if (M_InOutLine_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_M_InOutLine_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_M_InOutLine_ID, Integer.valueOf(M_InOutLine_ID));
	}

	/** Get Shipment/Receipt Line.
		@return Line on Shipment or Receipt document
	  */
	public int getM_InOutLine_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_InOutLine_ID);
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

	public org.compiere.model.I_M_Transaction getM_Transaction() throws RuntimeException
    {
		return (org.compiere.model.I_M_Transaction)MTable.get(getCtx(), org.compiere.model.I_M_Transaction.Table_Name)
			.getPO(getM_Transaction_ID(), get_TrxName());	}

	/** Set Inventory Transaction.
		@param M_Transaction_ID Inventory Transaction	  */
	public void setM_Transaction_ID (int M_Transaction_ID)
	{
		if (M_Transaction_ID < 1) 
			set_Value (COLUMNNAME_M_Transaction_ID, null);
		else 
			set_Value (COLUMNNAME_M_Transaction_ID, Integer.valueOf(M_Transaction_ID));
	}

	/** Get Inventory Transaction.
		@return Inventory Transaction	  */
	public int getM_Transaction_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Transaction_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Movement Date.
		@param MovementDate 
		Date a product was moved in or out of inventory
	  */
	public void setMovementDate (Timestamp MovementDate)
	{
		set_ValueNoCheck (COLUMNNAME_MovementDate, MovementDate);
	}

	/** Get Movement Date.
		@return Date a product was moved in or out of inventory
	  */
	public Timestamp getMovementDate () 
	{
		return (Timestamp)get_Value(COLUMNNAME_MovementDate);
	}

	/** Set Movement Quantity.
		@param MovementQty 
		Quantity of a product moved.
	  */
	public void setMovementQty (BigDecimal MovementQty)
	{
		set_Value (COLUMNNAME_MovementQty, MovementQty);
	}

	/** Get Movement Quantity.
		@return Quantity of a product moved.
	  */
	public BigDecimal getMovementQty () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_MovementQty);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** MovementType AD_Reference_ID=189 */
	public static final int MOVEMENTTYPE_AD_Reference_ID=189;
	/** Customer Shipment = C- */
	public static final String MOVEMENTTYPE_CustomerShipment = "C-";
	/** Customer Returns = C+ */
	public static final String MOVEMENTTYPE_CustomerReturns = "C+";
	/** Vendor Receipts = V+ */
	public static final String MOVEMENTTYPE_VendorReceipts = "V+";
	/** Vendor Returns = V- */
	public static final String MOVEMENTTYPE_VendorReturns = "V-";
	/** Inventory Out = I- */
	public static final String MOVEMENTTYPE_InventoryOut = "I-";
	/** Inventory In = I+ */
	public static final String MOVEMENTTYPE_InventoryIn = "I+";
	/** Movement From = M- */
	public static final String MOVEMENTTYPE_MovementFrom = "M-";
	/** Movement To = M+ */
	public static final String MOVEMENTTYPE_MovementTo = "M+";
	/** Production + = P+ */
	public static final String MOVEMENTTYPE_ProductionPlus = "P+";
	/** Production - = P- */
	public static final String MOVEMENTTYPE_Production_ = "P-";
	/** Work Order + = W+ */
	public static final String MOVEMENTTYPE_WorkOrderPlus = "W+";
	/** Work Order - = W- */
	public static final String MOVEMENTTYPE_WorkOrder_ = "W-";
	/** Set Movement Type.
		@param MovementType 
		Method of moving the inventory
	  */
	public void setMovementType (String MovementType)
	{

		set_Value (COLUMNNAME_MovementType, MovementType);
	}

	/** Get Movement Type.
		@return Method of moving the inventory
	  */
	public String getMovementType () 
	{
		return (String)get_Value(COLUMNNAME_MovementType);
	}
}