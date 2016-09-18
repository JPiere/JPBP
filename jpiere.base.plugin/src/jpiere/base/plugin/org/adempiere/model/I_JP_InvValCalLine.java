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

/** Generated Interface for JP_InvValCalLine
 *  @author iDempiere (generated) 
 *  @version Release 3.1
 */
@SuppressWarnings("all")
public interface I_JP_InvValCalLine 
{

    /** TableName=JP_InvValCalLine */
    public static final String Table_Name = "JP_InvValCalLine";

    /** AD_Table_ID=1000068 */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 1 - Org 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(1);

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

    /** Column name CurrentCostPrice */
    public static final String COLUMNNAME_CurrentCostPrice = "CurrentCostPrice";

	/** Set Current Cost Price.
	  * The currently used cost price
	  */
	public void setCurrentCostPrice (BigDecimal CurrentCostPrice);

	/** Get Current Cost Price.
	  * The currently used cost price
	  */
	public BigDecimal getCurrentCostPrice();

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

    /** Column name FutureCostPrice */
    public static final String COLUMNNAME_FutureCostPrice = "FutureCostPrice";

	/** Set Future Cost Price	  */
	public void setFutureCostPrice (BigDecimal FutureCostPrice);

	/** Get Future Cost Price	  */
	public BigDecimal getFutureCostPrice();

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

    /** Column name JP_BeginInvValCalLine_ID */
    public static final String COLUMNNAME_JP_BeginInvValCalLine_ID = "JP_BeginInvValCalLine_ID";

	/** Set Beginning Inventory Valuation Line	  */
	public void setJP_BeginInvValCalLine_ID (int JP_BeginInvValCalLine_ID);

	/** Get Beginning Inventory Valuation Line	  */
	public int getJP_BeginInvValCalLine_ID();

	public I_JP_InvValCalLine getJP_BeginInvValCalLine() throws RuntimeException;

    /** Column name JP_InvValAmt */
    public static final String COLUMNNAME_JP_InvValAmt = "JP_InvValAmt";

	/** Set Inventory Valuation Amount	  */
	public void setJP_InvValAmt (BigDecimal JP_InvValAmt);

	/** Get Inventory Valuation Amount	  */
	public BigDecimal getJP_InvValAmt();

    /** Column name JP_InvValCalLine_ID */
    public static final String COLUMNNAME_JP_InvValCalLine_ID = "JP_InvValCalLine_ID";

	/** Set Inventory Valuation Calculate Document Line	  */
	public void setJP_InvValCalLine_ID (int JP_InvValCalLine_ID);

	/** Get Inventory Valuation Calculate Document Line	  */
	public int getJP_InvValCalLine_ID();

    /** Column name JP_InvValCalLine_UU */
    public static final String COLUMNNAME_JP_InvValCalLine_UU = "JP_InvValCalLine_UU";

	/** Set JP_InvValCalLine_UU	  */
	public void setJP_InvValCalLine_UU (String JP_InvValCalLine_UU);

	/** Get JP_InvValCalLine_UU	  */
	public String getJP_InvValCalLine_UU();

    /** Column name JP_InvValCal_ID */
    public static final String COLUMNNAME_JP_InvValCal_ID = "JP_InvValCal_ID";

	/** Set Inventory Valuation Calculate Doc	  */
	public void setJP_InvValCal_ID (int JP_InvValCal_ID);

	/** Get Inventory Valuation Calculate Doc	  */
	public int getJP_InvValCal_ID();

	public I_JP_InvValCal getJP_InvValCal() throws RuntimeException;

    /** Column name JP_InvValTotalAmt */
    public static final String COLUMNNAME_JP_InvValTotalAmt = "JP_InvValTotalAmt";

	/** Set Inventory Valuation Total Amount	  */
	public void setJP_InvValTotalAmt (BigDecimal JP_InvValTotalAmt);

	/** Get Inventory Valuation Total Amount	  */
	public BigDecimal getJP_InvValTotalAmt();

    /** Column name Line */
    public static final String COLUMNNAME_Line = "Line";

	/** Set Line No.
	  * Unique line for this document
	  */
	public void setLine (int Line);

	/** Get Line No.
	  * Unique line for this document
	  */
	public int getLine();

    /** Column name M_AttributeSetInstance_ID */
    public static final String COLUMNNAME_M_AttributeSetInstance_ID = "M_AttributeSetInstance_ID";

	/** Set Attribute Set Instance.
	  * Product Attribute Set Instance
	  */
	public void setM_AttributeSetInstance_ID (int M_AttributeSetInstance_ID);

	/** Get Attribute Set Instance.
	  * Product Attribute Set Instance
	  */
	public int getM_AttributeSetInstance_ID();

	public I_M_AttributeSetInstance getM_AttributeSetInstance() throws RuntimeException;

    /** Column name M_Product_ID */
    public static final String COLUMNNAME_M_Product_ID = "M_Product_ID";

	/** Set Product.
	  * Product, Service, Item
	  */
	public void setM_Product_ID (int M_Product_ID);

	/** Get Product.
	  * Product, Service, Item
	  */
	public int getM_Product_ID();

	public org.compiere.model.I_M_Product getM_Product() throws RuntimeException;

    /** Column name QtyBook */
    public static final String COLUMNNAME_QtyBook = "QtyBook";

	/** Set Quantity book.
	  * Book Quantity
	  */
	public void setQtyBook (BigDecimal QtyBook);

	/** Get Quantity book.
	  * Book Quantity
	  */
	public BigDecimal getQtyBook();

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
}
