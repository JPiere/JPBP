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

import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.*;

/** Generated Model for I_BP_BankAccountJP
 *  @author iDempiere (generated) 
 *  @version Release 5.1 - $Id$ */
public class X_I_BP_BankAccountJP extends PO implements I_I_BP_BankAccountJP, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20180818L;

    /** Standard Constructor */
    public X_I_BP_BankAccountJP (Properties ctx, int I_BP_BankAccountJP_ID, String trxName)
    {
      super (ctx, I_BP_BankAccountJP_ID, trxName);
      /** if (I_BP_BankAccountJP_ID == 0)
        {
			setI_BP_BankAccountJP_ID (0);
			setI_IsImported (false);
			setIsACH (false);
        } */
    }

    /** Load Constructor */
    public X_I_BP_BankAccountJP (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 3 - Client - Org 
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
      StringBuffer sb = new StringBuffer ("X_I_BP_BankAccountJP[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public org.compiere.model.I_AD_User getAD_User() throws RuntimeException
    {
		return (org.compiere.model.I_AD_User)MTable.get(getCtx(), org.compiere.model.I_AD_User.Table_Name)
			.getPO(getAD_User_ID(), get_TrxName());	}

	/** Set User/Contact.
		@param AD_User_ID 
		User within the system - Internal or Business Partner Contact
	  */
	public void setAD_User_ID (int AD_User_ID)
	{
		if (AD_User_ID < 1) 
			set_Value (COLUMNNAME_AD_User_ID, null);
		else 
			set_Value (COLUMNNAME_AD_User_ID, Integer.valueOf(AD_User_ID));
	}

	/** Get User/Contact.
		@return User within the system - Internal or Business Partner Contact
	  */
	public int getAD_User_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_User_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Account City.
		@param A_City 
		City or the Credit Card or Account Holder
	  */
	public void setA_City (String A_City)
	{
		set_Value (COLUMNNAME_A_City, A_City);
	}

	/** Get Account City.
		@return City or the Credit Card or Account Holder
	  */
	public String getA_City () 
	{
		return (String)get_Value(COLUMNNAME_A_City);
	}

	/** Set Account Country.
		@param A_Country 
		Country
	  */
	public void setA_Country (String A_Country)
	{
		set_Value (COLUMNNAME_A_Country, A_Country);
	}

	/** Get Account Country.
		@return Country
	  */
	public String getA_Country () 
	{
		return (String)get_Value(COLUMNNAME_A_Country);
	}

	/** Set Account EMail.
		@param A_EMail 
		Email Address
	  */
	public void setA_EMail (String A_EMail)
	{
		set_Value (COLUMNNAME_A_EMail, A_EMail);
	}

	/** Get Account EMail.
		@return Email Address
	  */
	public String getA_EMail () 
	{
		return (String)get_Value(COLUMNNAME_A_EMail);
	}

	/** Set Driver License.
		@param A_Ident_DL 
		Payment Identification - Driver License
	  */
	public void setA_Ident_DL (String A_Ident_DL)
	{
		set_Value (COLUMNNAME_A_Ident_DL, A_Ident_DL);
	}

	/** Get Driver License.
		@return Payment Identification - Driver License
	  */
	public String getA_Ident_DL () 
	{
		return (String)get_Value(COLUMNNAME_A_Ident_DL);
	}

	/** Set Social Security No.
		@param A_Ident_SSN 
		Payment Identification - Social Security No
	  */
	public void setA_Ident_SSN (String A_Ident_SSN)
	{
		set_Value (COLUMNNAME_A_Ident_SSN, A_Ident_SSN);
	}

	/** Get Social Security No.
		@return Payment Identification - Social Security No
	  */
	public String getA_Ident_SSN () 
	{
		return (String)get_Value(COLUMNNAME_A_Ident_SSN);
	}

	/** Set Account Name.
		@param A_Name 
		Name on Credit Card or Account holder
	  */
	public void setA_Name (String A_Name)
	{
		set_Value (COLUMNNAME_A_Name, A_Name);
	}

	/** Get Account Name.
		@return Name on Credit Card or Account holder
	  */
	public String getA_Name () 
	{
		return (String)get_Value(COLUMNNAME_A_Name);
	}

	/** Set Account State.
		@param A_State 
		State of the Credit Card or Account holder
	  */
	public void setA_State (String A_State)
	{
		set_Value (COLUMNNAME_A_State, A_State);
	}

	/** Get Account State.
		@return State of the Credit Card or Account holder
	  */
	public String getA_State () 
	{
		return (String)get_Value(COLUMNNAME_A_State);
	}

	/** Set Account Street.
		@param A_Street 
		Street address of the Credit Card or Account holder
	  */
	public void setA_Street (String A_Street)
	{
		set_Value (COLUMNNAME_A_Street, A_Street);
	}

	/** Get Account Street.
		@return Street address of the Credit Card or Account holder
	  */
	public String getA_Street () 
	{
		return (String)get_Value(COLUMNNAME_A_Street);
	}

	/** Set Account Zip/Postal.
		@param A_Zip 
		Zip Code of the Credit Card or Account Holder
	  */
	public void setA_Zip (String A_Zip)
	{
		set_Value (COLUMNNAME_A_Zip, A_Zip);
	}

	/** Get Account Zip/Postal.
		@return Zip Code of the Credit Card or Account Holder
	  */
	public String getA_Zip () 
	{
		return (String)get_Value(COLUMNNAME_A_Zip);
	}

	/** Set Account No.
		@param AccountNo 
		Account Number
	  */
	public void setAccountNo (String AccountNo)
	{
		set_Value (COLUMNNAME_AccountNo, AccountNo);
	}

	/** Get Account No.
		@return Account Number
	  */
	public String getAccountNo () 
	{
		return (String)get_Value(COLUMNNAME_AccountNo);
	}

	/** BPBankAcctUse AD_Reference_ID=393 */
	public static final int BPBANKACCTUSE_AD_Reference_ID=393;
	/** None = N */
	public static final String BPBANKACCTUSE_None = "N";
	/** Both = B */
	public static final String BPBANKACCTUSE_Both = "B";
	/** Direct Debit = D */
	public static final String BPBANKACCTUSE_DirectDebit = "D";
	/** Direct Deposit = T */
	public static final String BPBANKACCTUSE_DirectDeposit = "T";
	/** Set Account Usage.
		@param BPBankAcctUse 
		Business Partner Bank Account usage
	  */
	public void setBPBankAcctUse (String BPBankAcctUse)
	{

		set_Value (COLUMNNAME_BPBankAcctUse, BPBankAcctUse);
	}

	/** Get Account Usage.
		@return Business Partner Bank Account usage
	  */
	public String getBPBankAcctUse () 
	{
		return (String)get_Value(COLUMNNAME_BPBankAcctUse);
	}

	/** Cash = B */
	public static final String BANKACCOUNTTYPE_Cash = "B";
	/** Checking = C */
	public static final String BANKACCOUNTTYPE_Checking = "C";
	/** Card = D */
	public static final String BANKACCOUNTTYPE_Card = "D";
	/** Savings = S */
	public static final String BANKACCOUNTTYPE_Savings = "S";
	/** Cash in Register = R */
	public static final String BANKACCOUNTTYPE_CashInRegister = "R";
	/** Gold note = G */
	public static final String BANKACCOUNTTYPE_GoldNote = "G";
	/** Set Bank Account Type.
		@param BankAccountType 
		Bank Account Type
	  */
	public void setBankAccountType (String BankAccountType)
	{

		set_Value (COLUMNNAME_BankAccountType, BankAccountType);
	}

	/** Get Bank Account Type.
		@return Bank Account Type
	  */
	public String getBankAccountType () 
	{
		return (String)get_Value(COLUMNNAME_BankAccountType);
	}

	public org.compiere.model.I_C_BP_BankAccount getC_BP_BankAccount() throws RuntimeException
    {
		return (org.compiere.model.I_C_BP_BankAccount)MTable.get(getCtx(), org.compiere.model.I_C_BP_BankAccount.Table_Name)
			.getPO(getC_BP_BankAccount_ID(), get_TrxName());	}

	/** Set Partner Bank Account.
		@param C_BP_BankAccount_ID 
		Bank Account of the Business Partner
	  */
	public void setC_BP_BankAccount_ID (int C_BP_BankAccount_ID)
	{
		if (C_BP_BankAccount_ID < 1) 
			set_Value (COLUMNNAME_C_BP_BankAccount_ID, null);
		else 
			set_Value (COLUMNNAME_C_BP_BankAccount_ID, Integer.valueOf(C_BP_BankAccount_ID));
	}

	/** Get Partner Bank Account.
		@return Bank Account of the Business Partner
	  */
	public int getC_BP_BankAccount_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_BP_BankAccount_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_BPartner getC_BPartner() throws RuntimeException
    {
		return (org.compiere.model.I_C_BPartner)MTable.get(getCtx(), org.compiere.model.I_C_BPartner.Table_Name)
			.getPO(getC_BPartner_ID(), get_TrxName());	}

	/** Set Business Partner .
		@param C_BPartner_ID 
		Identifies a Business Partner
	  */
	public void setC_BPartner_ID (int C_BPartner_ID)
	{
		if (C_BPartner_ID < 1) 
			set_Value (COLUMNNAME_C_BPartner_ID, null);
		else 
			set_Value (COLUMNNAME_C_BPartner_ID, Integer.valueOf(C_BPartner_ID));
	}

	/** Get Business Partner .
		@return Identifies a Business Partner
	  */
	public int getC_BPartner_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_BPartner_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Bank getC_Bank() throws RuntimeException
    {
		return (org.compiere.model.I_C_Bank)MTable.get(getCtx(), org.compiere.model.I_C_Bank.Table_Name)
			.getPO(getC_Bank_ID(), get_TrxName());	}

	/** Set Bank.
		@param C_Bank_ID 
		Bank
	  */
	public void setC_Bank_ID (int C_Bank_ID)
	{
		if (C_Bank_ID < 1) 
			set_Value (COLUMNNAME_C_Bank_ID, null);
		else 
			set_Value (COLUMNNAME_C_Bank_ID, Integer.valueOf(C_Bank_ID));
	}

	/** Get Bank.
		@return Bank
	  */
	public int getC_Bank_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Bank_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_PaymentProcessor getC_PaymentProcessor() throws RuntimeException
    {
		return (org.compiere.model.I_C_PaymentProcessor)MTable.get(getCtx(), org.compiere.model.I_C_PaymentProcessor.Table_Name)
			.getPO(getC_PaymentProcessor_ID(), get_TrxName());	}

	/** Set Payment Processor.
		@param C_PaymentProcessor_ID 
		Payment processor for electronic payments
	  */
	public void setC_PaymentProcessor_ID (int C_PaymentProcessor_ID)
	{
		if (C_PaymentProcessor_ID < 1) 
			set_Value (COLUMNNAME_C_PaymentProcessor_ID, null);
		else 
			set_Value (COLUMNNAME_C_PaymentProcessor_ID, Integer.valueOf(C_PaymentProcessor_ID));
	}

	/** Get Payment Processor.
		@return Payment processor for electronic payments
	  */
	public int getC_PaymentProcessor_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_PaymentProcessor_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Exp. Month.
		@param CreditCardExpMM 
		Expiry Month
	  */
	public void setCreditCardExpMM (int CreditCardExpMM)
	{
		set_Value (COLUMNNAME_CreditCardExpMM, Integer.valueOf(CreditCardExpMM));
	}

	/** Get Exp. Month.
		@return Expiry Month
	  */
	public int getCreditCardExpMM () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_CreditCardExpMM);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Exp. Year.
		@param CreditCardExpYY 
		Expiry Year
	  */
	public void setCreditCardExpYY (int CreditCardExpYY)
	{
		set_Value (COLUMNNAME_CreditCardExpYY, Integer.valueOf(CreditCardExpYY));
	}

	/** Get Exp. Year.
		@return Expiry Year
	  */
	public int getCreditCardExpYY () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_CreditCardExpYY);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Number.
		@param CreditCardNumber 
		Credit Card Number 
	  */
	public void setCreditCardNumber (String CreditCardNumber)
	{
		set_Value (COLUMNNAME_CreditCardNumber, CreditCardNumber);
	}

	/** Get Number.
		@return Credit Card Number 
	  */
	public String getCreditCardNumber () 
	{
		return (String)get_Value(COLUMNNAME_CreditCardNumber);
	}

	/** CreditCardType AD_Reference_ID=149 */
	public static final int CREDITCARDTYPE_AD_Reference_ID=149;
	/** Amex = A */
	public static final String CREDITCARDTYPE_Amex = "A";
	/** MasterCard = M */
	public static final String CREDITCARDTYPE_MasterCard = "M";
	/** Visa = V */
	public static final String CREDITCARDTYPE_Visa = "V";
	/** ATM = C */
	public static final String CREDITCARDTYPE_ATM = "C";
	/** Diners = D */
	public static final String CREDITCARDTYPE_Diners = "D";
	/** Discover = N */
	public static final String CREDITCARDTYPE_Discover = "N";
	/** Purchase Card = P */
	public static final String CREDITCARDTYPE_PurchaseCard = "P";
	/** Set Credit Card.
		@param CreditCardType 
		Credit Card (Visa, MC, AmEx)
	  */
	public void setCreditCardType (String CreditCardType)
	{

		set_Value (COLUMNNAME_CreditCardType, CreditCardType);
	}

	/** Get Credit Card.
		@return Credit Card (Visa, MC, AmEx)
	  */
	public String getCreditCardType () 
	{
		return (String)get_Value(COLUMNNAME_CreditCardType);
	}

	/** Set Verification Code.
		@param CreditCardVV 
		Credit Card Verification code on credit card
	  */
	public void setCreditCardVV (String CreditCardVV)
	{
		set_Value (COLUMNNAME_CreditCardVV, CreditCardVV);
	}

	/** Get Verification Code.
		@return Credit Card Verification code on credit card
	  */
	public String getCreditCardVV () 
	{
		return (String)get_Value(COLUMNNAME_CreditCardVV);
	}

	/** Set Customer Payment Profile ID.
		@param CustomerPaymentProfileID Customer Payment Profile ID	  */
	public void setCustomerPaymentProfileID (String CustomerPaymentProfileID)
	{
		set_Value (COLUMNNAME_CustomerPaymentProfileID, CustomerPaymentProfileID);
	}

	/** Get Customer Payment Profile ID.
		@return Customer Payment Profile ID	  */
	public String getCustomerPaymentProfileID () 
	{
		return (String)get_Value(COLUMNNAME_CustomerPaymentProfileID);
	}

	/** Set IBAN.
		@param IBAN 
		International Bank Account Number
	  */
	public void setIBAN (String IBAN)
	{
		set_Value (COLUMNNAME_IBAN, IBAN);
	}

	/** Get IBAN.
		@return International Bank Account Number
	  */
	public String getIBAN () 
	{
		return (String)get_Value(COLUMNNAME_IBAN);
	}

	/** Set I_BP_BankAccountJP.
		@param I_BP_BankAccountJP_ID I_BP_BankAccountJP	  */
	public void setI_BP_BankAccountJP_ID (int I_BP_BankAccountJP_ID)
	{
		if (I_BP_BankAccountJP_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_I_BP_BankAccountJP_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_I_BP_BankAccountJP_ID, Integer.valueOf(I_BP_BankAccountJP_ID));
	}

	/** Get I_BP_BankAccountJP.
		@return I_BP_BankAccountJP	  */
	public int getI_BP_BankAccountJP_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_I_BP_BankAccountJP_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set I_BP_BankAccountJP_UU.
		@param I_BP_BankAccountJP_UU I_BP_BankAccountJP_UU	  */
	public void setI_BP_BankAccountJP_UU (String I_BP_BankAccountJP_UU)
	{
		set_ValueNoCheck (COLUMNNAME_I_BP_BankAccountJP_UU, I_BP_BankAccountJP_UU);
	}

	/** Get I_BP_BankAccountJP_UU.
		@return I_BP_BankAccountJP_UU	  */
	public String getI_BP_BankAccountJP_UU () 
	{
		return (String)get_Value(COLUMNNAME_I_BP_BankAccountJP_UU);
	}

	/** Set Import Error Message.
		@param I_ErrorMsg 
		Messages generated from import process
	  */
	public void setI_ErrorMsg (String I_ErrorMsg)
	{
		set_Value (COLUMNNAME_I_ErrorMsg, I_ErrorMsg);
	}

	/** Get Import Error Message.
		@return Messages generated from import process
	  */
	public String getI_ErrorMsg () 
	{
		return (String)get_Value(COLUMNNAME_I_ErrorMsg);
	}

	/** Set Imported.
		@param I_IsImported 
		Has this import been processed
	  */
	public void setI_IsImported (boolean I_IsImported)
	{
		set_Value (COLUMNNAME_I_IsImported, Boolean.valueOf(I_IsImported));
	}

	/** Get Imported.
		@return Has this import been processed
	  */
	public boolean isI_IsImported () 
	{
		Object oo = get_Value(COLUMNNAME_I_IsImported);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set ACH.
		@param IsACH 
		Automatic Clearing House
	  */
	public void setIsACH (boolean IsACH)
	{
		set_Value (COLUMNNAME_IsACH, Boolean.valueOf(IsACH));
	}

	/** Get ACH.
		@return Automatic Clearing House
	  */
	public boolean isACH () 
	{
		Object oo = get_Value(COLUMNNAME_IsACH);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Default.
		@param IsDefault 
		Default value
	  */
	public void setIsDefault (boolean IsDefault)
	{
		set_Value (COLUMNNAME_IsDefault, Boolean.valueOf(IsDefault));
	}

	/** Get Default.
		@return Default value
	  */
	public boolean isDefault () 
	{
		Object oo = get_Value(COLUMNNAME_IsDefault);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Account Name(Kana).
		@param JP_A_Name_Kana Account Name(Kana)	  */
	public void setJP_A_Name_Kana (String JP_A_Name_Kana)
	{
		set_Value (COLUMNNAME_JP_A_Name_Kana, JP_A_Name_Kana);
	}

	/** Get Account Name(Kana).
		@return Account Name(Kana)	  */
	public String getJP_A_Name_Kana () 
	{
		return (String)get_Value(COLUMNNAME_JP_A_Name_Kana);
	}

	/** Set Business Partner(Search Key).
		@param JP_BPartner_Value Business Partner(Search Key)	  */
	public void setJP_BPartner_Value (String JP_BPartner_Value)
	{
		set_Value (COLUMNNAME_JP_BPartner_Value, JP_BPartner_Value);
	}

	/** Get Business Partner(Search Key).
		@return Business Partner(Search Key)	  */
	public String getJP_BPartner_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_BPartner_Value);
	}

	/** Set Bank Name.
		@param JP_Bank_Name Bank Name	  */
	public void setJP_Bank_Name (String JP_Bank_Name)
	{
		set_Value (COLUMNNAME_JP_Bank_Name, JP_Bank_Name);
	}

	/** Get Bank Name.
		@return Bank Name	  */
	public String getJP_Bank_Name () 
	{
		return (String)get_Value(COLUMNNAME_JP_Bank_Name);
	}

	/** Set Branch Code.
		@param JP_BranchCode Branch Code	  */
	public void setJP_BranchCode (String JP_BranchCode)
	{
		set_Value (COLUMNNAME_JP_BranchCode, JP_BranchCode);
	}

	/** Get Branch Code.
		@return Branch Code	  */
	public String getJP_BranchCode () 
	{
		return (String)get_Value(COLUMNNAME_JP_BranchCode);
	}

	/** Set Branch Name(Kana).
		@param JP_BranchName_Kana Branch Name(Kana)	  */
	public void setJP_BranchName_Kana (String JP_BranchName_Kana)
	{
		set_Value (COLUMNNAME_JP_BranchName_Kana, JP_BranchName_Kana);
	}

	/** Get Branch Name(Kana).
		@return Branch Name(Kana)	  */
	public String getJP_BranchName_Kana () 
	{
		return (String)get_Value(COLUMNNAME_JP_BranchName_Kana);
	}

	/** Set Organization(Search Key).
		@param JP_Org_Value Organization(Search Key)	  */
	public void setJP_Org_Value (String JP_Org_Value)
	{
		set_Value (COLUMNNAME_JP_Org_Value, JP_Org_Value);
	}

	/** Get Organization(Search Key).
		@return Organization(Search Key)	  */
	public String getJP_Org_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_Org_Value);
	}

	/** Set User(E-Mail).
		@param JP_User_EMail User(E-Mail)	  */
	public void setJP_User_EMail (String JP_User_EMail)
	{
		set_Value (COLUMNNAME_JP_User_EMail, JP_User_EMail);
	}

	/** Get User(E-Mail).
		@return User(E-Mail)	  */
	public String getJP_User_EMail () 
	{
		return (String)get_Value(COLUMNNAME_JP_User_EMail);
	}

	/** Set User(Name).
		@param JP_User_Name User(Name)	  */
	public void setJP_User_Name (String JP_User_Name)
	{
		set_Value (COLUMNNAME_JP_User_Name, JP_User_Name);
	}

	/** Get User(Name).
		@return User(Name)	  */
	public String getJP_User_Name () 
	{
		return (String)get_Value(COLUMNNAME_JP_User_Name);
	}

	/** Set User(Search Key).
		@param JP_User_Value User(Search Key)	  */
	public void setJP_User_Value (String JP_User_Value)
	{
		set_Value (COLUMNNAME_JP_User_Value, JP_User_Value);
	}

	/** Get User(Search Key).
		@return User(Search Key)	  */
	public String getJP_User_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_User_Value);
	}

	/** Set Processed.
		@param Processed 
		The document has been processed
	  */
	public void setProcessed (boolean Processed)
	{
		set_Value (COLUMNNAME_Processed, Boolean.valueOf(Processed));
	}

	/** Get Processed.
		@return The document has been processed
	  */
	public boolean isProcessed () 
	{
		Object oo = get_Value(COLUMNNAME_Processed);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Process Now.
		@param Processing Process Now	  */
	public void setProcessing (boolean Processing)
	{
		set_Value (COLUMNNAME_Processing, Boolean.valueOf(Processing));
	}

	/** Get Process Now.
		@return Process Now	  */
	public boolean isProcessing () 
	{
		Object oo = get_Value(COLUMNNAME_Processing);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** R_AvsAddr AD_Reference_ID=213 */
	public static final int R_AVSADDR_AD_Reference_ID=213;
	/** Match = Y */
	public static final String R_AVSADDR_Match = "Y";
	/** No Match = N */
	public static final String R_AVSADDR_NoMatch = "N";
	/** Unavailable = X */
	public static final String R_AVSADDR_Unavailable = "X";
	/** Set Address verified.
		@param R_AvsAddr 
		This address has been verified
	  */
	public void setR_AvsAddr (String R_AvsAddr)
	{

		set_Value (COLUMNNAME_R_AvsAddr, R_AvsAddr);
	}

	/** Get Address verified.
		@return This address has been verified
	  */
	public String getR_AvsAddr () 
	{
		return (String)get_Value(COLUMNNAME_R_AvsAddr);
	}

	/** R_AvsZip AD_Reference_ID=213 */
	public static final int R_AVSZIP_AD_Reference_ID=213;
	/** Match = Y */
	public static final String R_AVSZIP_Match = "Y";
	/** No Match = N */
	public static final String R_AVSZIP_NoMatch = "N";
	/** Unavailable = X */
	public static final String R_AVSZIP_Unavailable = "X";
	/** Set Zip verified.
		@param R_AvsZip 
		The Zip Code has been verified
	  */
	public void setR_AvsZip (String R_AvsZip)
	{

		set_Value (COLUMNNAME_R_AvsZip, R_AvsZip);
	}

	/** Get Zip verified.
		@return The Zip Code has been verified
	  */
	public String getR_AvsZip () 
	{
		return (String)get_Value(COLUMNNAME_R_AvsZip);
	}

	/** Set Routing No.
		@param RoutingNo 
		Bank Routing Number
	  */
	public void setRoutingNo (String RoutingNo)
	{
		set_Value (COLUMNNAME_RoutingNo, RoutingNo);
	}

	/** Get Routing No.
		@return Bank Routing Number
	  */
	public String getRoutingNo () 
	{
		return (String)get_Value(COLUMNNAME_RoutingNo);
	}
}