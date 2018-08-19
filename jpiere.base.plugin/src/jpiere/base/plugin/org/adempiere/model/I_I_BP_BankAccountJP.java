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

/** Generated Interface for I_BP_BankAccountJP
 *  @author iDempiere (generated) 
 *  @version Release 5.1
 */
@SuppressWarnings("all")
public interface I_I_BP_BankAccountJP 
{

    /** TableName=I_BP_BankAccountJP */
    public static final String Table_Name = "I_BP_BankAccountJP";

    /** AD_Table_ID=1000224 */
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

    /** Column name AD_User_ID */
    public static final String COLUMNNAME_AD_User_ID = "AD_User_ID";

	/** Set User/Contact.
	  * User within the system - Internal or Business Partner Contact
	  */
	public void setAD_User_ID (int AD_User_ID);

	/** Get User/Contact.
	  * User within the system - Internal or Business Partner Contact
	  */
	public int getAD_User_ID();

	public org.compiere.model.I_AD_User getAD_User() throws RuntimeException;

    /** Column name A_City */
    public static final String COLUMNNAME_A_City = "A_City";

	/** Set Account City.
	  * City or the Credit Card or Account Holder
	  */
	public void setA_City (String A_City);

	/** Get Account City.
	  * City or the Credit Card or Account Holder
	  */
	public String getA_City();

    /** Column name A_Country */
    public static final String COLUMNNAME_A_Country = "A_Country";

	/** Set Account Country.
	  * Country
	  */
	public void setA_Country (String A_Country);

	/** Get Account Country.
	  * Country
	  */
	public String getA_Country();

    /** Column name A_EMail */
    public static final String COLUMNNAME_A_EMail = "A_EMail";

	/** Set Account EMail.
	  * Email Address
	  */
	public void setA_EMail (String A_EMail);

	/** Get Account EMail.
	  * Email Address
	  */
	public String getA_EMail();

    /** Column name A_Ident_DL */
    public static final String COLUMNNAME_A_Ident_DL = "A_Ident_DL";

	/** Set Driver License.
	  * Payment Identification - Driver License
	  */
	public void setA_Ident_DL (String A_Ident_DL);

	/** Get Driver License.
	  * Payment Identification - Driver License
	  */
	public String getA_Ident_DL();

    /** Column name A_Ident_SSN */
    public static final String COLUMNNAME_A_Ident_SSN = "A_Ident_SSN";

	/** Set Social Security No.
	  * Payment Identification - Social Security No
	  */
	public void setA_Ident_SSN (String A_Ident_SSN);

	/** Get Social Security No.
	  * Payment Identification - Social Security No
	  */
	public String getA_Ident_SSN();

    /** Column name A_Name */
    public static final String COLUMNNAME_A_Name = "A_Name";

	/** Set Account Name.
	  * Name on Credit Card or Account holder
	  */
	public void setA_Name (String A_Name);

	/** Get Account Name.
	  * Name on Credit Card or Account holder
	  */
	public String getA_Name();

    /** Column name A_State */
    public static final String COLUMNNAME_A_State = "A_State";

	/** Set Account State.
	  * State of the Credit Card or Account holder
	  */
	public void setA_State (String A_State);

	/** Get Account State.
	  * State of the Credit Card or Account holder
	  */
	public String getA_State();

    /** Column name A_Street */
    public static final String COLUMNNAME_A_Street = "A_Street";

	/** Set Account Street.
	  * Street address of the Credit Card or Account holder
	  */
	public void setA_Street (String A_Street);

	/** Get Account Street.
	  * Street address of the Credit Card or Account holder
	  */
	public String getA_Street();

    /** Column name A_Zip */
    public static final String COLUMNNAME_A_Zip = "A_Zip";

	/** Set Account Zip/Postal.
	  * Zip Code of the Credit Card or Account Holder
	  */
	public void setA_Zip (String A_Zip);

	/** Get Account Zip/Postal.
	  * Zip Code of the Credit Card or Account Holder
	  */
	public String getA_Zip();

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

    /** Column name BPBankAcctUse */
    public static final String COLUMNNAME_BPBankAcctUse = "BPBankAcctUse";

	/** Set Account Usage.
	  * Business Partner Bank Account usage
	  */
	public void setBPBankAcctUse (String BPBankAcctUse);

	/** Get Account Usage.
	  * Business Partner Bank Account usage
	  */
	public String getBPBankAcctUse();

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

    /** Column name C_BP_BankAccount_ID */
    public static final String COLUMNNAME_C_BP_BankAccount_ID = "C_BP_BankAccount_ID";

	/** Set Partner Bank Account.
	  * Bank Account of the Business Partner
	  */
	public void setC_BP_BankAccount_ID (int C_BP_BankAccount_ID);

	/** Get Partner Bank Account.
	  * Bank Account of the Business Partner
	  */
	public int getC_BP_BankAccount_ID();

	public org.compiere.model.I_C_BP_BankAccount getC_BP_BankAccount() throws RuntimeException;

    /** Column name C_BPartner_ID */
    public static final String COLUMNNAME_C_BPartner_ID = "C_BPartner_ID";

	/** Set Business Partner .
	  * Identifies a Business Partner
	  */
	public void setC_BPartner_ID (int C_BPartner_ID);

	/** Get Business Partner .
	  * Identifies a Business Partner
	  */
	public int getC_BPartner_ID();

	public org.compiere.model.I_C_BPartner getC_BPartner() throws RuntimeException;

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

    /** Column name C_PaymentProcessor_ID */
    public static final String COLUMNNAME_C_PaymentProcessor_ID = "C_PaymentProcessor_ID";

	/** Set Payment Processor.
	  * Payment processor for electronic payments
	  */
	public void setC_PaymentProcessor_ID (int C_PaymentProcessor_ID);

	/** Get Payment Processor.
	  * Payment processor for electronic payments
	  */
	public int getC_PaymentProcessor_ID();

	public org.compiere.model.I_C_PaymentProcessor getC_PaymentProcessor() throws RuntimeException;

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

    /** Column name CreditCardExpMM */
    public static final String COLUMNNAME_CreditCardExpMM = "CreditCardExpMM";

	/** Set Exp. Month.
	  * Expiry Month
	  */
	public void setCreditCardExpMM (int CreditCardExpMM);

	/** Get Exp. Month.
	  * Expiry Month
	  */
	public int getCreditCardExpMM();

    /** Column name CreditCardExpYY */
    public static final String COLUMNNAME_CreditCardExpYY = "CreditCardExpYY";

	/** Set Exp. Year.
	  * Expiry Year
	  */
	public void setCreditCardExpYY (int CreditCardExpYY);

	/** Get Exp. Year.
	  * Expiry Year
	  */
	public int getCreditCardExpYY();

    /** Column name CreditCardNumber */
    public static final String COLUMNNAME_CreditCardNumber = "CreditCardNumber";

	/** Set Number.
	  * Credit Card Number 
	  */
	public void setCreditCardNumber (String CreditCardNumber);

	/** Get Number.
	  * Credit Card Number 
	  */
	public String getCreditCardNumber();

    /** Column name CreditCardType */
    public static final String COLUMNNAME_CreditCardType = "CreditCardType";

	/** Set Credit Card.
	  * Credit Card (Visa, MC, AmEx)
	  */
	public void setCreditCardType (String CreditCardType);

	/** Get Credit Card.
	  * Credit Card (Visa, MC, AmEx)
	  */
	public String getCreditCardType();

    /** Column name CreditCardVV */
    public static final String COLUMNNAME_CreditCardVV = "CreditCardVV";

	/** Set Verification Code.
	  * Credit Card Verification code on credit card
	  */
	public void setCreditCardVV (String CreditCardVV);

	/** Get Verification Code.
	  * Credit Card Verification code on credit card
	  */
	public String getCreditCardVV();

    /** Column name CustomerPaymentProfileID */
    public static final String COLUMNNAME_CustomerPaymentProfileID = "CustomerPaymentProfileID";

	/** Set Customer Payment Profile ID	  */
	public void setCustomerPaymentProfileID (String CustomerPaymentProfileID);

	/** Get Customer Payment Profile ID	  */
	public String getCustomerPaymentProfileID();

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

    /** Column name I_BP_BankAccountJP_ID */
    public static final String COLUMNNAME_I_BP_BankAccountJP_ID = "I_BP_BankAccountJP_ID";

	/** Set I_BP_BankAccountJP	  */
	public void setI_BP_BankAccountJP_ID (int I_BP_BankAccountJP_ID);

	/** Get I_BP_BankAccountJP	  */
	public int getI_BP_BankAccountJP_ID();

    /** Column name I_BP_BankAccountJP_UU */
    public static final String COLUMNNAME_I_BP_BankAccountJP_UU = "I_BP_BankAccountJP_UU";

	/** Set I_BP_BankAccountJP_UU	  */
	public void setI_BP_BankAccountJP_UU (String I_BP_BankAccountJP_UU);

	/** Get I_BP_BankAccountJP_UU	  */
	public String getI_BP_BankAccountJP_UU();

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

    /** Column name IsACH */
    public static final String COLUMNNAME_IsACH = "IsACH";

	/** Set ACH.
	  * Automatic Clearing House
	  */
	public void setIsACH (boolean IsACH);

	/** Get ACH.
	  * Automatic Clearing House
	  */
	public boolean isACH();

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

    /** Column name JP_A_Name_Kana */
    public static final String COLUMNNAME_JP_A_Name_Kana = "JP_A_Name_Kana";

	/** Set Account Name(Kana)	  */
	public void setJP_A_Name_Kana (String JP_A_Name_Kana);

	/** Get Account Name(Kana)	  */
	public String getJP_A_Name_Kana();

    /** Column name JP_BPartner_Value */
    public static final String COLUMNNAME_JP_BPartner_Value = "JP_BPartner_Value";

	/** Set Business Partner(Search Key)	  */
	public void setJP_BPartner_Value (String JP_BPartner_Value);

	/** Get Business Partner(Search Key)	  */
	public String getJP_BPartner_Value();

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

    /** Column name JP_Org_Value */
    public static final String COLUMNNAME_JP_Org_Value = "JP_Org_Value";

	/** Set Organization(Search Key)	  */
	public void setJP_Org_Value (String JP_Org_Value);

	/** Get Organization(Search Key)	  */
	public String getJP_Org_Value();

    /** Column name JP_User_EMail */
    public static final String COLUMNNAME_JP_User_EMail = "JP_User_EMail";

	/** Set User(E-Mail)	  */
	public void setJP_User_EMail (String JP_User_EMail);

	/** Get User(E-Mail)	  */
	public String getJP_User_EMail();

    /** Column name JP_User_Name */
    public static final String COLUMNNAME_JP_User_Name = "JP_User_Name";

	/** Set User(Name)	  */
	public void setJP_User_Name (String JP_User_Name);

	/** Get User(Name)	  */
	public String getJP_User_Name();

    /** Column name JP_User_Value */
    public static final String COLUMNNAME_JP_User_Value = "JP_User_Value";

	/** Set User(Search Key)	  */
	public void setJP_User_Value (String JP_User_Value);

	/** Get User(Search Key)	  */
	public String getJP_User_Value();

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

    /** Column name R_AvsAddr */
    public static final String COLUMNNAME_R_AvsAddr = "R_AvsAddr";

	/** Set Address verified.
	  * This address has been verified
	  */
	public void setR_AvsAddr (String R_AvsAddr);

	/** Get Address verified.
	  * This address has been verified
	  */
	public String getR_AvsAddr();

    /** Column name R_AvsZip */
    public static final String COLUMNNAME_R_AvsZip = "R_AvsZip";

	/** Set Zip verified.
	  * The Zip Code has been verified
	  */
	public void setR_AvsZip (String R_AvsZip);

	/** Get Zip verified.
	  * The Zip Code has been verified
	  */
	public String getR_AvsZip();

    /** Column name RoutingNo */
    public static final String COLUMNNAME_RoutingNo = "RoutingNo";

	/** Set Routing No.
	  * Bank Routing Number
	  */
	public void setRoutingNo (String RoutingNo);

	/** Get Routing No.
	  * Bank Routing Number
	  */
	public String getRoutingNo();

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
