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

/** Generated Interface for JP_Contract_Product_Acct
 *  @author iDempiere (generated) 
 *  @version Release 4.1
 */
@SuppressWarnings("all")
public interface I_JP_Contract_Product_Acct 
{

    /** TableName=JP_Contract_Product_Acct */
    public static final String Table_Name = "JP_Contract_Product_Acct";

    /** AD_Table_ID=1000192 */
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

    /** Column name JP_COGS_Clearing_Acct */
    public static final String COLUMNNAME_JP_COGS_Clearing_Acct = "JP_COGS_Clearing_Acct";

	/** Set Product COGS Clearing	  */
	public void setJP_COGS_Clearing_Acct (int JP_COGS_Clearing_Acct);

	/** Get Product COGS Clearing	  */
	public int getJP_COGS_Clearing_Acct();

	public I_C_ValidCombination getJP_COGS_Clearing_A() throws RuntimeException;

    /** Column name JP_Contract_Acct_ID */
    public static final String COLUMNNAME_JP_Contract_Acct_ID = "JP_Contract_Acct_ID";

	/** Set Contract Acct Info	  */
	public void setJP_Contract_Acct_ID (int JP_Contract_Acct_ID);

	/** Get Contract Acct Info	  */
	public int getJP_Contract_Acct_ID();

	public I_JP_Contract_Acct getJP_Contract_Acct() throws RuntimeException;

    /** Column name JP_Contract_Product_Acct_ID */
    public static final String COLUMNNAME_JP_Contract_Product_Acct_ID = "JP_Contract_Product_Acct_ID";

	/** Set Product Contract Acct	  */
	public void setJP_Contract_Product_Acct_ID (int JP_Contract_Product_Acct_ID);

	/** Get Product Contract Acct	  */
	public int getJP_Contract_Product_Acct_ID();

    /** Column name JP_Contract_Product_Acct_UU */
    public static final String COLUMNNAME_JP_Contract_Product_Acct_UU = "JP_Contract_Product_Acct_UU";

	/** Set JP_Contract_Product_Acct_UU	  */
	public void setJP_Contract_Product_Acct_UU (String JP_Contract_Product_Acct_UU);

	/** Get JP_Contract_Product_Acct_UU	  */
	public String getJP_Contract_Product_Acct_UU();

    /** Column name JP_Expense_Acct */
    public static final String COLUMNNAME_JP_Expense_Acct = "JP_Expense_Acct";

	/** Set Product Expense(Recognition Doc)	  */
	public void setJP_Expense_Acct (int JP_Expense_Acct);

	/** Get Product Expense(Recognition Doc)	  */
	public int getJP_Expense_Acct();

	public I_C_ValidCombination getJP_Expense_A() throws RuntimeException;

    /** Column name JP_PurchaseOffset_Acct */
    public static final String COLUMNNAME_JP_PurchaseOffset_Acct = "JP_PurchaseOffset_Acct";

	/** Set Purchase Offset Acct	  */
	public void setJP_PurchaseOffset_Acct (int JP_PurchaseOffset_Acct);

	/** Get Purchase Offset Acct	  */
	public int getJP_PurchaseOffset_Acct();

	public I_C_ValidCombination getJP_PurchaseOffset_A() throws RuntimeException;

    /** Column name JP_Purchase_Acct */
    public static final String COLUMNNAME_JP_Purchase_Acct = "JP_Purchase_Acct";

	/** Set Purchase Acct	  */
	public void setJP_Purchase_Acct (int JP_Purchase_Acct);

	/** Get Purchase Acct	  */
	public int getJP_Purchase_Acct();

	public I_C_ValidCombination getJP_Purchase_A() throws RuntimeException;

    /** Column name JP_Revenue_Acct */
    public static final String COLUMNNAME_JP_Revenue_Acct = "JP_Revenue_Acct";

	/** Set Product Revenue(Recognition Doc)	  */
	public void setJP_Revenue_Acct (int JP_Revenue_Acct);

	/** Get Product Revenue(Recognition Doc)	  */
	public int getJP_Revenue_Acct();

	public I_C_ValidCombination getJP_Revenue_A() throws RuntimeException;

    /** Column name JP_TradeDiscountGrant_Acct */
    public static final String COLUMNNAME_JP_TradeDiscountGrant_Acct = "JP_TradeDiscountGrant_Acct";

	/** Set Trade Discount Granted(Recognition Doc).
	  * Trade Discount Granted Account
	  */
	public void setJP_TradeDiscountGrant_Acct (int JP_TradeDiscountGrant_Acct);

	/** Get Trade Discount Granted(Recognition Doc).
	  * Trade Discount Granted Account
	  */
	public int getJP_TradeDiscountGrant_Acct();

	public I_C_ValidCombination getJP_TradeDiscountGrant_A() throws RuntimeException;

    /** Column name JP_TradeDiscountRec_Acct */
    public static final String COLUMNNAME_JP_TradeDiscountRec_Acct = "JP_TradeDiscountRec_Acct";

	/** Set Trade Discount Received(Recognition).
	  * Trade Discount Receivable Account
	  */
	public void setJP_TradeDiscountRec_Acct (int JP_TradeDiscountRec_Acct);

	/** Get Trade Discount Received(Recognition).
	  * Trade Discount Receivable Account
	  */
	public int getJP_TradeDiscountRec_Acct();

	public I_C_ValidCombination getJP_TradeDiscountRec_A() throws RuntimeException;

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
