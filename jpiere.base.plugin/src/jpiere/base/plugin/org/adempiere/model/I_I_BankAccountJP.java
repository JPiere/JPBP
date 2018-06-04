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

/** Generated Interface for I_BankAccountJP
 *  @author iDempiere (generated) 
 *  @version Release 5.1
 */
@SuppressWarnings("all")
public interface I_I_BankAccountJP 
{

    /** TableName=I_BankAccountJP */
    public static final String Table_Name = "I_BankAccountJP";

    /** AD_Table_ID=1000211 */
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

    /** Column name AccountNo */
    public static final String COLUMNNAME_AccountNo = "AccountNo";

	/** Set Account No.
	  * Account Number
	  */
	public void setAccountNo (String AccountNo);

	/** Get Account No.
	  * Account Number
	  */
	public String getAccountNo();

    /** Column name BBAN */
    public static final String COLUMNNAME_BBAN = "BBAN";

	/** Set BBAN.
	  * Basic Bank Account Number
	  */
	public void setBBAN (String BBAN);

	/** Get BBAN.
	  * Basic Bank Account Number
	  */
	public String getBBAN();

    /** Column name B_Asset_Acct */
    public static final String COLUMNNAME_B_Asset_Acct = "B_Asset_Acct";

	/** Set Bank Asset.
	  * Bank Asset Account
	  */
	public void setB_Asset_Acct (int B_Asset_Acct);

	/** Get Bank Asset.
	  * Bank Asset Account
	  */
	public int getB_Asset_Acct();

	public I_C_ValidCombination getB_Asset_A() throws RuntimeException;

    /** Column name B_InTransit_Acct */
    public static final String COLUMNNAME_B_InTransit_Acct = "B_InTransit_Acct";

	/** Set Bank In Transit.
	  * Bank In Transit Account
	  */
	public void setB_InTransit_Acct (int B_InTransit_Acct);

	/** Get Bank In Transit.
	  * Bank In Transit Account
	  */
	public int getB_InTransit_Acct();

	public I_C_ValidCombination getB_InTransit_A() throws RuntimeException;

    /** Column name B_InterestExp_Acct */
    public static final String COLUMNNAME_B_InterestExp_Acct = "B_InterestExp_Acct";

	/** Set Bank Interest Expense.
	  * Bank Interest Expense Account
	  */
	public void setB_InterestExp_Acct (int B_InterestExp_Acct);

	/** Get Bank Interest Expense.
	  * Bank Interest Expense Account
	  */
	public int getB_InterestExp_Acct();

	public I_C_ValidCombination getB_InterestExp_A() throws RuntimeException;

    /** Column name B_InterestRev_Acct */
    public static final String COLUMNNAME_B_InterestRev_Acct = "B_InterestRev_Acct";

	/** Set Bank Interest Revenue.
	  * Bank Interest Revenue Account
	  */
	public void setB_InterestRev_Acct (int B_InterestRev_Acct);

	/** Get Bank Interest Revenue.
	  * Bank Interest Revenue Account
	  */
	public int getB_InterestRev_Acct();

	public I_C_ValidCombination getB_InterestRev_A() throws RuntimeException;

    /** Column name B_PaymentSelect_Acct */
    public static final String COLUMNNAME_B_PaymentSelect_Acct = "B_PaymentSelect_Acct";

	/** Set Payment Selection.
	  * AP Payment Selection Clearing Account
	  */
	public void setB_PaymentSelect_Acct (int B_PaymentSelect_Acct);

	/** Get Payment Selection.
	  * AP Payment Selection Clearing Account
	  */
	public int getB_PaymentSelect_Acct();

	public I_C_ValidCombination getB_PaymentSelect_A() throws RuntimeException;

    /** Column name B_UnallocatedCash_Acct */
    public static final String COLUMNNAME_B_UnallocatedCash_Acct = "B_UnallocatedCash_Acct";

	/** Set Unallocated Cash.
	  * Unallocated Cash Clearing Account
	  */
	public void setB_UnallocatedCash_Acct (int B_UnallocatedCash_Acct);

	/** Get Unallocated Cash.
	  * Unallocated Cash Clearing Account
	  */
	public int getB_UnallocatedCash_Acct();

	public I_C_ValidCombination getB_UnallocatedCash_A() throws RuntimeException;

    /** Column name BankAccountType */
    public static final String COLUMNNAME_BankAccountType = "BankAccountType";

	/** Set Bank Account Type.
	  * Bank Account Type
	  */
	public void setBankAccountType (String BankAccountType);

	/** Get Bank Account Type.
	  * Bank Account Type
	  */
	public String getBankAccountType();

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

    /** Column name C_BankAccount_ID */
    public static final String COLUMNNAME_C_BankAccount_ID = "C_BankAccount_ID";

	/** Set Bank Account.
	  * Account at the Bank
	  */
	public void setC_BankAccount_ID (int C_BankAccount_ID);

	/** Get Bank Account.
	  * Account at the Bank
	  */
	public int getC_BankAccount_ID();

	public org.compiere.model.I_C_BankAccount getC_BankAccount() throws RuntimeException;

    /** Column name C_Bank_ID */
    public static final String COLUMNNAME_C_Bank_ID = "C_Bank_ID";

	/** Set Bank.
	  * Bank
	  */
	public void setC_Bank_ID (int C_Bank_ID);

	/** Get Bank.
	  * Bank
	  */
	public int getC_Bank_ID();

	public org.compiere.model.I_C_Bank getC_Bank() throws RuntimeException;

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

    /** Column name CreditLimit */
    public static final String COLUMNNAME_CreditLimit = "CreditLimit";

	/** Set Credit limit.
	  * Amount of Credit allowed
	  */
	public void setCreditLimit (BigDecimal CreditLimit);

	/** Get Credit limit.
	  * Amount of Credit allowed
	  */
	public BigDecimal getCreditLimit();

    /** Column name CurrentBalance */
    public static final String COLUMNNAME_CurrentBalance = "CurrentBalance";

	/** Set Current balance.
	  * Current Balance
	  */
	public void setCurrentBalance (BigDecimal CurrentBalance);

	/** Get Current balance.
	  * Current Balance
	  */
	public BigDecimal getCurrentBalance();

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

    /** Column name IBAN */
    public static final String COLUMNNAME_IBAN = "IBAN";

	/** Set IBAN.
	  * International Bank Account Number
	  */
	public void setIBAN (String IBAN);

	/** Get IBAN.
	  * International Bank Account Number
	  */
	public String getIBAN();

    /** Column name ISO_Code */
    public static final String COLUMNNAME_ISO_Code = "ISO_Code";

	/** Set ISO Currency Code.
	  * Three letter ISO 4217 Code of the Currency
	  */
	public void setISO_Code (String ISO_Code);

	/** Get ISO Currency Code.
	  * Three letter ISO 4217 Code of the Currency
	  */
	public String getISO_Code();

    /** Column name I_BankAccountJP_ID */
    public static final String COLUMNNAME_I_BankAccountJP_ID = "I_BankAccountJP_ID";

	/** Set I_BankAccountJP	  */
	public void setI_BankAccountJP_ID (int I_BankAccountJP_ID);

	/** Get I_BankAccountJP	  */
	public int getI_BankAccountJP_ID();

    /** Column name I_BankAccountJP_UU */
    public static final String COLUMNNAME_I_BankAccountJP_UU = "I_BankAccountJP_UU";

	/** Set I_BankAccountJP_UU	  */
	public void setI_BankAccountJP_UU (String I_BankAccountJP_UU);

	/** Get I_BankAccountJP_UU	  */
	public String getI_BankAccountJP_UU();

    /** Column name I_ErrorMsg */
    public static final String COLUMNNAME_I_ErrorMsg = "I_ErrorMsg";

	/** Set Import Error Message.
	  * Messages generated from import process
	  */
	public void setI_ErrorMsg (String I_ErrorMsg);

	/** Get Import Error Message.
	  * Messages generated from import process
	  */
	public String getI_ErrorMsg();

    /** Column name I_IsImported */
    public static final String COLUMNNAME_I_IsImported = "I_IsImported";

	/** Set Imported.
	  * Has this import been processed
	  */
	public void setI_IsImported (boolean I_IsImported);

	/** Get Imported.
	  * Has this import been processed
	  */
	public boolean isI_IsImported();

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

    /** Column name JP_AcctSchema_Name */
    public static final String COLUMNNAME_JP_AcctSchema_Name = "JP_AcctSchema_Name";

	/** Set Accounting Schema(Name)	  */
	public void setJP_AcctSchema_Name (String JP_AcctSchema_Name);

	/** Get Accounting Schema(Name)	  */
	public String getJP_AcctSchema_Name();

    /** Column name JP_Asset_Acct_Value */
    public static final String COLUMNNAME_JP_Asset_Acct_Value = "JP_Asset_Acct_Value";

	/** Set Bank Asset(Search key)	  */
	public void setJP_Asset_Acct_Value (String JP_Asset_Acct_Value);

	/** Get Bank Asset(Search key)	  */
	public String getJP_Asset_Acct_Value();

    /** Column name JP_Bank_Name */
    public static final String COLUMNNAME_JP_Bank_Name = "JP_Bank_Name";

	/** Set Bank Name	  */
	public void setJP_Bank_Name (String JP_Bank_Name);

	/** Get Bank Name	  */
	public String getJP_Bank_Name();

    /** Column name JP_BranchCode */
    public static final String COLUMNNAME_JP_BranchCode = "JP_BranchCode";

	/** Set Branch Code	  */
	public void setJP_BranchCode (String JP_BranchCode);

	/** Get Branch Code	  */
	public String getJP_BranchCode();

    /** Column name JP_BranchName_Kana */
    public static final String COLUMNNAME_JP_BranchName_Kana = "JP_BranchName_Kana";

	/** Set Branch Name(Kana)	  */
	public void setJP_BranchName_Kana (String JP_BranchName_Kana);

	/** Get Branch Name(Kana)	  */
	public String getJP_BranchName_Kana();

    /** Column name JP_InTransit_Acct_Value */
    public static final String COLUMNNAME_JP_InTransit_Acct_Value = "JP_InTransit_Acct_Value";

	/** Set Bank In Transit(Search key)	  */
	public void setJP_InTransit_Acct_Value (String JP_InTransit_Acct_Value);

	/** Get Bank In Transit(Search key)	  */
	public String getJP_InTransit_Acct_Value();

    /** Column name JP_InterestExp_Acct_Value */
    public static final String COLUMNNAME_JP_InterestExp_Acct_Value = "JP_InterestExp_Acct_Value";

	/** Set Bank Interest Expense(Search Key)	  */
	public void setJP_InterestExp_Acct_Value (String JP_InterestExp_Acct_Value);

	/** Get Bank Interest Expense(Search Key)	  */
	public String getJP_InterestExp_Acct_Value();

    /** Column name JP_InterestRev_Acct_Value */
    public static final String COLUMNNAME_JP_InterestRev_Acct_Value = "JP_InterestRev_Acct_Value";

	/** Set Bank Interest Revenue(Search key)	  */
	public void setJP_InterestRev_Acct_Value (String JP_InterestRev_Acct_Value);

	/** Get Bank Interest Revenue(Search key)	  */
	public String getJP_InterestRev_Acct_Value();

    /** Column name JP_Org_Value */
    public static final String COLUMNNAME_JP_Org_Value = "JP_Org_Value";

	/** Set Organization(Search Key)	  */
	public void setJP_Org_Value (String JP_Org_Value);

	/** Get Organization(Search Key)	  */
	public String getJP_Org_Value();

    /** Column name JP_PaymentSelect_Value */
    public static final String COLUMNNAME_JP_PaymentSelect_Value = "JP_PaymentSelect_Value";

	/** Set Payment Selection(Search Key)	  */
	public void setJP_PaymentSelect_Value (String JP_PaymentSelect_Value);

	/** Get Payment Selection(Search Key)	  */
	public String getJP_PaymentSelect_Value();

    /** Column name JP_RequesterCode */
    public static final String COLUMNNAME_JP_RequesterCode = "JP_RequesterCode";

	/** Set Requester Code	  */
	public void setJP_RequesterCode (String JP_RequesterCode);

	/** Get Requester Code	  */
	public String getJP_RequesterCode();

    /** Column name JP_RequesterName */
    public static final String COLUMNNAME_JP_RequesterName = "JP_RequesterName";

	/** Set Requester Name	  */
	public void setJP_RequesterName (String JP_RequesterName);

	/** Get Requester Name	  */
	public String getJP_RequesterName();

    /** Column name JP_UnallocatedCash_Value */
    public static final String COLUMNNAME_JP_UnallocatedCash_Value = "JP_UnallocatedCash_Value";

	/** Set Unallocated Cash(Search Key)	  */
	public void setJP_UnallocatedCash_Value (String JP_UnallocatedCash_Value);

	/** Get Unallocated Cash(Search Key)	  */
	public String getJP_UnallocatedCash_Value();

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

    /** Column name Processed */
    public static final String COLUMNNAME_Processed = "Processed";

	/** Set Processed.
	  * The document has been processed
	  */
	public void setProcessed (boolean Processed);

	/** Get Processed.
	  * The document has been processed
	  */
	public boolean isProcessed();

    /** Column name Processing */
    public static final String COLUMNNAME_Processing = "Processing";

	/** Set Process Now	  */
	public void setProcessing (boolean Processing);

	/** Get Process Now	  */
	public boolean isProcessing();

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
