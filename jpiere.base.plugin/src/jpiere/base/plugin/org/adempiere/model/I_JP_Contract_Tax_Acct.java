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

/** Generated Interface for JP_Contract_Tax_Acct
 *  @author iDempiere (generated) 
 *  @version Release 8.2
 */
@SuppressWarnings("all")
public interface I_JP_Contract_Tax_Acct 
{

    /** TableName=JP_Contract_Tax_Acct */
    public static final String Table_Name = "JP_Contract_Tax_Acct";

    /** AD_Table_ID=1000193 */
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

    /** Column name C_Tax_ID */
    public static final String COLUMNNAME_C_Tax_ID = "C_Tax_ID";

	/** Set Tax.
	  * Tax identifier
	  */
	public void setC_Tax_ID (int C_Tax_ID);

	/** Get Tax.
	  * Tax identifier
	  */
	public int getC_Tax_ID();

	public org.compiere.model.I_C_Tax getC_Tax() throws RuntimeException;

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

    /** Column name JP_Contract_Acct_ID */
    public static final String COLUMNNAME_JP_Contract_Acct_ID = "JP_Contract_Acct_ID";

	/** Set Contract Acct Info	  */
	public void setJP_Contract_Acct_ID (int JP_Contract_Acct_ID);

	/** Get Contract Acct Info	  */
	public int getJP_Contract_Acct_ID();

	public I_JP_Contract_Acct getJP_Contract_Acct() throws RuntimeException;

    /** Column name JP_Contract_Tax_Acct_ID */
    public static final String COLUMNNAME_JP_Contract_Tax_Acct_ID = "JP_Contract_Tax_Acct_ID";

	/** Set Tax Contract Acct	  */
	public void setJP_Contract_Tax_Acct_ID (int JP_Contract_Tax_Acct_ID);

	/** Get Tax Contract Acct	  */
	public int getJP_Contract_Tax_Acct_ID();

    /** Column name JP_Contract_Tax_Acct_UU */
    public static final String COLUMNNAME_JP_Contract_Tax_Acct_UU = "JP_Contract_Tax_Acct_UU";

	/** Set JP_Contract_Tax_Acct_UU	  */
	public void setJP_Contract_Tax_Acct_UU (String JP_Contract_Tax_Acct_UU);

	/** Get JP_Contract_Tax_Acct_UU	  */
	public String getJP_Contract_Tax_Acct_UU();

    /** Column name JP_GL_TaxCredit_Acct */
    public static final String COLUMNNAME_JP_GL_TaxCredit_Acct = "JP_GL_TaxCredit_Acct";

	/** Set Tax Credit(GL Journal)	  */
	public void setJP_GL_TaxCredit_Acct (int JP_GL_TaxCredit_Acct);

	/** Get Tax Credit(GL Journal)	  */
	public int getJP_GL_TaxCredit_Acct();

	public I_C_ValidCombination getJP_GL_TaxCredit_A() throws RuntimeException;

    /** Column name JP_GL_TaxDue_Acct */
    public static final String COLUMNNAME_JP_GL_TaxDue_Acct = "JP_GL_TaxDue_Acct";

	/** Set Tax Due(GL Journal)	  */
	public void setJP_GL_TaxDue_Acct (int JP_GL_TaxDue_Acct);

	/** Get Tax Due(GL Journal)	  */
	public int getJP_GL_TaxDue_Acct();

	public I_C_ValidCombination getJP_GL_TaxDue_A() throws RuntimeException;

    /** Column name JP_GL_TaxExpense_Acct */
    public static final String COLUMNNAME_JP_GL_TaxExpense_Acct = "JP_GL_TaxExpense_Acct";

	/** Set Tax Expense(GL Journal)	  */
	public void setJP_GL_TaxExpense_Acct (int JP_GL_TaxExpense_Acct);

	/** Get Tax Expense(GL Journal)	  */
	public int getJP_GL_TaxExpense_Acct();

	public I_C_ValidCombination getJP_GL_TaxExpense_A() throws RuntimeException;

    /** Column name JP_TaxCredit_Acct */
    public static final String COLUMNNAME_JP_TaxCredit_Acct = "JP_TaxCredit_Acct";

	/** Set Tax Credit(Recognition Doc)	  */
	public void setJP_TaxCredit_Acct (int JP_TaxCredit_Acct);

	/** Get Tax Credit(Recognition Doc)	  */
	public int getJP_TaxCredit_Acct();

	public I_C_ValidCombination getJP_TaxCredit_A() throws RuntimeException;

    /** Column name JP_TaxDue_Acct */
    public static final String COLUMNNAME_JP_TaxDue_Acct = "JP_TaxDue_Acct";

	/** Set Tax Due(Recognition Doc)	  */
	public void setJP_TaxDue_Acct (int JP_TaxDue_Acct);

	/** Get Tax Due(Recognition Doc)	  */
	public int getJP_TaxDue_Acct();

	public I_C_ValidCombination getJP_TaxDue_A() throws RuntimeException;

    /** Column name JP_TaxExpense_Acct */
    public static final String COLUMNNAME_JP_TaxExpense_Acct = "JP_TaxExpense_Acct";

	/** Set Tax Expense(Recognition Doc)	  */
	public void setJP_TaxExpense_Acct (int JP_TaxExpense_Acct);

	/** Get Tax Expense(Recognition Doc)	  */
	public int getJP_TaxExpense_Acct();

	public I_C_ValidCombination getJP_TaxExpense_A() throws RuntimeException;

    /** Column name T_Credit_Acct */
    public static final String COLUMNNAME_T_Credit_Acct = "T_Credit_Acct";

	/** Set Tax Credit.
	  * Account for Tax you can reclaim
	  */
	public void setT_Credit_Acct (int T_Credit_Acct);

	/** Get Tax Credit.
	  * Account for Tax you can reclaim
	  */
	public int getT_Credit_Acct();

	public I_C_ValidCombination getT_Credit_A() throws RuntimeException;

    /** Column name T_Due_Acct */
    public static final String COLUMNNAME_T_Due_Acct = "T_Due_Acct";

	/** Set Tax Due.
	  * Account for Tax you have to pay
	  */
	public void setT_Due_Acct (int T_Due_Acct);

	/** Get Tax Due.
	  * Account for Tax you have to pay
	  */
	public int getT_Due_Acct();

	public I_C_ValidCombination getT_Due_A() throws RuntimeException;

    /** Column name T_Expense_Acct */
    public static final String COLUMNNAME_T_Expense_Acct = "T_Expense_Acct";

	/** Set Tax Expense.
	  * Account for paid tax you cannot reclaim
	  */
	public void setT_Expense_Acct (int T_Expense_Acct);

	/** Get Tax Expense.
	  * Account for paid tax you cannot reclaim
	  */
	public int getT_Expense_Acct();

	public I_C_ValidCombination getT_Expense_A() throws RuntimeException;

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
