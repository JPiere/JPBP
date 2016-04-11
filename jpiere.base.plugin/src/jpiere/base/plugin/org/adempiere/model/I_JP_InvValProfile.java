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

/** Generated Interface for JP_InvValProfile
 *  @author iDempiere (generated) 
 *  @version Release 3.1
 */
@SuppressWarnings("all")
public interface I_JP_InvValProfile 
{

    /** TableName=JP_InvValProfile */
    public static final String Table_Name = "JP_InvValProfile";

    /** AD_Table_ID=1000065 */
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

    /** Column name C_Currency_ID */
    public static final String COLUMNNAME_C_Currency_ID = "C_Currency_ID";

	/** Set Currency.
	  * The Currency for this record
	  */
	public void setC_Currency_ID (int C_Currency_ID);

	/** Get Currency.
	  * The Currency for this record
	  */
	public int getC_Currency_ID();

	public org.compiere.model.I_C_Currency getC_Currency() throws RuntimeException;

    /** Column name C_DocType_ID */
    public static final String COLUMNNAME_C_DocType_ID = "C_DocType_ID";

	/** Set Document Type.
	  * Document type or rules
	  */
	public void setC_DocType_ID (int C_DocType_ID);

	/** Get Document Type.
	  * Document type or rules
	  */
	public int getC_DocType_ID();

	public org.compiere.model.I_C_DocType getC_DocType() throws RuntimeException;

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

    /** Column name DocAction */
    public static final String COLUMNNAME_DocAction = "DocAction";

	/** Set Document Action.
	  * The targeted status of the document
	  */
	public void setDocAction (String DocAction);

	/** Get Document Action.
	  * The targeted status of the document
	  */
	public String getDocAction();

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

    /** Column name IsZeroStockInvValJP */
    public static final String COLUMNNAME_IsZeroStockInvValJP = "IsZeroStockInvValJP";

	/** Set Do Inventory Valuation when 0 stock	  */
	public void setIsZeroStockInvValJP (boolean IsZeroStockInvValJP);

	/** Get Do Inventory Valuation when 0 stock	  */
	public boolean isZeroStockInvValJP();

    /** Column name JP_ApplyAmtList */
    public static final String COLUMNNAME_JP_ApplyAmtList = "JP_ApplyAmtList";

	/** Set Apply Amt List	  */
	public void setJP_ApplyAmtList (String JP_ApplyAmtList);

	/** Get Apply Amt List	  */
	public String getJP_ApplyAmtList();

    /** Column name JP_GLJournalCreateClass */
    public static final String COLUMNNAME_JP_GLJournalCreateClass = "JP_GLJournalCreateClass";

	/** Set GL Journal Create Class	  */
	public void setJP_GLJournalCreateClass (String JP_GLJournalCreateClass);

	/** Get GL Journal Create Class	  */
	public String getJP_GLJournalCreateClass();

    /** Column name JP_InvValAdjustCalClass */
    public static final String COLUMNNAME_JP_InvValAdjustCalClass = "JP_InvValAdjustCalClass";

	/** Set Inv Val Adjust Calculation Class	  */
	public void setJP_InvValAdjustCalClass (String JP_InvValAdjustCalClass);

	/** Get Inv Val Adjust Calculation Class	  */
	public String getJP_InvValAdjustCalClass();

    /** Column name JP_InvValAdjustLineClass */
    public static final String COLUMNNAME_JP_InvValAdjustLineClass = "JP_InvValAdjustLineClass";

	/** Set Inv Val Adjust Line Create Class	  */
	public void setJP_InvValAdjustLineClass (String JP_InvValAdjustLineClass);

	/** Get Inv Val Adjust Line Create Class	  */
	public String getJP_InvValAdjustLineClass();

    /** Column name JP_InvValCalClass */
    public static final String COLUMNNAME_JP_InvValCalClass = "JP_InvValCalClass";

	/** Set Inventory Valuation Calculation Class	  */
	public void setJP_InvValCalClass (String JP_InvValCalClass);

	/** Get Inventory Valuation Calculation Class	  */
	public String getJP_InvValCalClass();

    /** Column name JP_InvValCalLineClass */
    public static final String COLUMNNAME_JP_InvValCalLineClass = "JP_InvValCalLineClass";

	/** Set Inv Val Cal Line Create Class	  */
	public void setJP_InvValCalLineClass (String JP_InvValCalLineClass);

	/** Get Inv Val Cal Line Create Class	  */
	public String getJP_InvValCalLineClass();

    /** Column name JP_InvValProfile_ID */
    public static final String COLUMNNAME_JP_InvValProfile_ID = "JP_InvValProfile_ID";

	/** Set Inventory Valuation Profile	  */
	public void setJP_InvValProfile_ID (int JP_InvValProfile_ID);

	/** Get Inventory Valuation Profile	  */
	public int getJP_InvValProfile_ID();

    /** Column name JP_InvValProfile_UU */
    public static final String COLUMNNAME_JP_InvValProfile_UU = "JP_InvValProfile_UU";

	/** Set JP_InvValProfile_UU	  */
	public void setJP_InvValProfile_UU (String JP_InvValProfile_UU);

	/** Get JP_InvValProfile_UU	  */
	public String getJP_InvValProfile_UU();

    /** Column name JP_InvValUpdateCostClass */
    public static final String COLUMNNAME_JP_InvValUpdateCostClass = "JP_InvValUpdateCostClass";

	/** Set Product Cost Update Class	  */
	public void setJP_InvValUpdateCostClass (String JP_InvValUpdateCostClass);

	/** Get Product Cost Update Class	  */
	public String getJP_InvValUpdateCostClass();

    /** Column name JP_UpdateCost */
    public static final String COLUMNNAME_JP_UpdateCost = "JP_UpdateCost";

	/** Set Update Cost	  */
	public void setJP_UpdateCost (String JP_UpdateCost);

	/** Get Update Cost	  */
	public String getJP_UpdateCost();

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
