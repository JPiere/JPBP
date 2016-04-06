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

/** Generated Interface for JP_InvValAdjustLine
 *  @author iDempiere (generated) 
 *  @version Release 3.1
 */
@SuppressWarnings("all")
public interface I_JP_InvValAdjustLine 
{

    /** TableName=JP_InvValAdjustLine */
    public static final String Table_Name = "JP_InvValAdjustLine";

    /** AD_Table_ID=1000072 */
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

    /** Column name Account_ID */
    public static final String COLUMNNAME_Account_ID = "Account_ID";

	/** Set Account.
	  * Account used
	  */
	public void setAccount_ID (int Account_ID);

	/** Get Account.
	  * Account used
	  */
	public int getAccount_ID();

	public org.compiere.model.I_C_ElementValue getAccount() throws RuntimeException;

    /** Column name AmtAcctBalance */
    public static final String COLUMNNAME_AmtAcctBalance = "AmtAcctBalance";

	/** Set Accounted Balance.
	  * Accounted Balance Amount
	  */
	public void setAmtAcctBalance (BigDecimal AmtAcctBalance);

	/** Get Accounted Balance.
	  * Accounted Balance Amount
	  */
	public BigDecimal getAmtAcctBalance();

    /** Column name AmtAcctCr */
    public static final String COLUMNNAME_AmtAcctCr = "AmtAcctCr";

	/** Set Accounted Credit.
	  * Accounted Credit Amount
	  */
	public void setAmtAcctCr (BigDecimal AmtAcctCr);

	/** Get Accounted Credit.
	  * Accounted Credit Amount
	  */
	public BigDecimal getAmtAcctCr();

    /** Column name AmtAcctDr */
    public static final String COLUMNNAME_AmtAcctDr = "AmtAcctDr";

	/** Set Accounted Debit.
	  * Accounted Debit Amount
	  */
	public void setAmtAcctDr (BigDecimal AmtAcctDr);

	/** Get Accounted Debit.
	  * Accounted Debit Amount
	  */
	public BigDecimal getAmtAcctDr();

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

    /** Column name DifferenceAmt */
    public static final String COLUMNNAME_DifferenceAmt = "DifferenceAmt";

	/** Set Difference.
	  * Difference Amount
	  */
	public void setDifferenceAmt (BigDecimal DifferenceAmt);

	/** Get Difference.
	  * Difference Amount
	  */
	public BigDecimal getDifferenceAmt();

    /** Column name DifferenceQty */
    public static final String COLUMNNAME_DifferenceQty = "DifferenceQty";

	/** Set Difference.
	  * Difference Quantity
	  */
	public void setDifferenceQty (BigDecimal DifferenceQty);

	/** Get Difference.
	  * Difference Quantity
	  */
	public BigDecimal getDifferenceQty();

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

    /** Column name IsConfirmed */
    public static final String COLUMNNAME_IsConfirmed = "IsConfirmed";

	/** Set Confirmed.
	  * Assignment is confirmed
	  */
	public void setIsConfirmed (boolean IsConfirmed);

	/** Get Confirmed.
	  * Assignment is confirmed
	  */
	public boolean isConfirmed();

    /** Column name JP_InvValAdjustLine_ID */
    public static final String COLUMNNAME_JP_InvValAdjustLine_ID = "JP_InvValAdjustLine_ID";

	/** Set Inventory Valuation Adjust Doc Line	  */
	public void setJP_InvValAdjustLine_ID (int JP_InvValAdjustLine_ID);

	/** Get Inventory Valuation Adjust Doc Line	  */
	public int getJP_InvValAdjustLine_ID();

    /** Column name JP_InvValAdjustLine_UU */
    public static final String COLUMNNAME_JP_InvValAdjustLine_UU = "JP_InvValAdjustLine_UU";

	/** Set JP_InvValAdjustLine_UU	  */
	public void setJP_InvValAdjustLine_UU (String JP_InvValAdjustLine_UU);

	/** Get JP_InvValAdjustLine_UU	  */
	public String getJP_InvValAdjustLine_UU();

    /** Column name JP_InvValAdjust_ID */
    public static final String COLUMNNAME_JP_InvValAdjust_ID = "JP_InvValAdjust_ID";

	/** Set Inventory Valuation Adjust Doc	  */
	public void setJP_InvValAdjust_ID (int JP_InvValAdjust_ID);

	/** Get Inventory Valuation Adjust Doc	  */
	public int getJP_InvValAdjust_ID();

	public I_JP_InvValAdjust getJP_InvValAdjust() throws RuntimeException;

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

	public I_JP_InvValCalLine getJP_InvValCalLine() throws RuntimeException;

    /** Column name JP_InvValTotalAmt */
    public static final String COLUMNNAME_JP_InvValTotalAmt = "JP_InvValTotalAmt";

	/** Set Inventory Valuation Total Amount	  */
	public void setJP_InvValTotalAmt (BigDecimal JP_InvValTotalAmt);

	/** Get Inventory Valuation Total Amount	  */
	public BigDecimal getJP_InvValTotalAmt();

    /** Column name JP_JournalLineCr_ID */
    public static final String COLUMNNAME_JP_JournalLineCr_ID = "JP_JournalLineCr_ID";

	/** Set Journal Line CR	  */
	public void setJP_JournalLineCr_ID (int JP_JournalLineCr_ID);

	/** Get Journal Line CR	  */
	public int getJP_JournalLineCr_ID();

	public org.compiere.model.I_GL_JournalLine getJP_JournalLineCr() throws RuntimeException;

    /** Column name JP_JournalLineDr_ID */
    public static final String COLUMNNAME_JP_JournalLineDr_ID = "JP_JournalLineDr_ID";

	/** Set Journal Line DR	  */
	public void setJP_JournalLineDr_ID (int JP_JournalLineDr_ID);

	/** Get Journal Line DR	  */
	public int getJP_JournalLineDr_ID();

	public org.compiere.model.I_GL_JournalLine getJP_JournalLineDr() throws RuntimeException;

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

    /** Column name QtyOnHand */
    public static final String COLUMNNAME_QtyOnHand = "QtyOnHand";

	/** Set On Hand Quantity.
	  * On Hand Quantity
	  */
	public void setQtyOnHand (BigDecimal QtyOnHand);

	/** Get On Hand Quantity.
	  * On Hand Quantity
	  */
	public BigDecimal getQtyOnHand();

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
