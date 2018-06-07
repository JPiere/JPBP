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

/** Generated Model for I_BankAccountJP
 *  @author iDempiere (generated) 
 *  @version Release 5.1 - $Id$ */
public class X_I_BankAccountJP extends PO implements I_I_BankAccountJP, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20180607L;

    /** Standard Constructor */
    public X_I_BankAccountJP (Properties ctx, int I_BankAccountJP_ID, String trxName)
    {
      super (ctx, I_BankAccountJP_ID, trxName);
      /** if (I_BankAccountJP_ID == 0)
        {
			setCreditLimit (Env.ZERO);
// 0
			setCurrentBalance (Env.ZERO);
// 0
			setI_BankAccountJP_ID (0);
			setI_IsActiveJP (true);
// Y
			setIsDefault (false);
        } */
    }

    /** Load Constructor */
    public X_I_BankAccountJP (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_I_BankAccountJP[")
        .append(get_ID()).append("]");
      return sb.toString();
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

	/** Set BBAN.
		@param BBAN 
		Basic Bank Account Number
	  */
	public void setBBAN (String BBAN)
	{
		set_Value (COLUMNNAME_BBAN, BBAN);
	}

	/** Get BBAN.
		@return Basic Bank Account Number
	  */
	public String getBBAN () 
	{
		return (String)get_Value(COLUMNNAME_BBAN);
	}

	public I_C_ValidCombination getB_Asset_A() throws RuntimeException
    {
		return (I_C_ValidCombination)MTable.get(getCtx(), I_C_ValidCombination.Table_Name)
			.getPO(getB_Asset_Acct(), get_TrxName());	}

	/** Set Bank Asset.
		@param B_Asset_Acct 
		Bank Asset Account
	  */
	public void setB_Asset_Acct (int B_Asset_Acct)
	{
		set_Value (COLUMNNAME_B_Asset_Acct, Integer.valueOf(B_Asset_Acct));
	}

	/** Get Bank Asset.
		@return Bank Asset Account
	  */
	public int getB_Asset_Acct () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_B_Asset_Acct);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_ValidCombination getB_InTransit_A() throws RuntimeException
    {
		return (I_C_ValidCombination)MTable.get(getCtx(), I_C_ValidCombination.Table_Name)
			.getPO(getB_InTransit_Acct(), get_TrxName());	}

	/** Set Bank In Transit.
		@param B_InTransit_Acct 
		Bank In Transit Account
	  */
	public void setB_InTransit_Acct (int B_InTransit_Acct)
	{
		set_Value (COLUMNNAME_B_InTransit_Acct, Integer.valueOf(B_InTransit_Acct));
	}

	/** Get Bank In Transit.
		@return Bank In Transit Account
	  */
	public int getB_InTransit_Acct () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_B_InTransit_Acct);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_ValidCombination getB_InterestExp_A() throws RuntimeException
    {
		return (I_C_ValidCombination)MTable.get(getCtx(), I_C_ValidCombination.Table_Name)
			.getPO(getB_InterestExp_Acct(), get_TrxName());	}

	/** Set Bank Interest Expense.
		@param B_InterestExp_Acct 
		Bank Interest Expense Account
	  */
	public void setB_InterestExp_Acct (int B_InterestExp_Acct)
	{
		set_Value (COLUMNNAME_B_InterestExp_Acct, Integer.valueOf(B_InterestExp_Acct));
	}

	/** Get Bank Interest Expense.
		@return Bank Interest Expense Account
	  */
	public int getB_InterestExp_Acct () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_B_InterestExp_Acct);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_ValidCombination getB_InterestRev_A() throws RuntimeException
    {
		return (I_C_ValidCombination)MTable.get(getCtx(), I_C_ValidCombination.Table_Name)
			.getPO(getB_InterestRev_Acct(), get_TrxName());	}

	/** Set Bank Interest Revenue.
		@param B_InterestRev_Acct 
		Bank Interest Revenue Account
	  */
	public void setB_InterestRev_Acct (int B_InterestRev_Acct)
	{
		set_Value (COLUMNNAME_B_InterestRev_Acct, Integer.valueOf(B_InterestRev_Acct));
	}

	/** Get Bank Interest Revenue.
		@return Bank Interest Revenue Account
	  */
	public int getB_InterestRev_Acct () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_B_InterestRev_Acct);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_ValidCombination getB_PaymentSelect_A() throws RuntimeException
    {
		return (I_C_ValidCombination)MTable.get(getCtx(), I_C_ValidCombination.Table_Name)
			.getPO(getB_PaymentSelect_Acct(), get_TrxName());	}

	/** Set Payment Selection.
		@param B_PaymentSelect_Acct 
		AP Payment Selection Clearing Account
	  */
	public void setB_PaymentSelect_Acct (int B_PaymentSelect_Acct)
	{
		set_Value (COLUMNNAME_B_PaymentSelect_Acct, Integer.valueOf(B_PaymentSelect_Acct));
	}

	/** Get Payment Selection.
		@return AP Payment Selection Clearing Account
	  */
	public int getB_PaymentSelect_Acct () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_B_PaymentSelect_Acct);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_ValidCombination getB_UnallocatedCash_A() throws RuntimeException
    {
		return (I_C_ValidCombination)MTable.get(getCtx(), I_C_ValidCombination.Table_Name)
			.getPO(getB_UnallocatedCash_Acct(), get_TrxName());	}

	/** Set Unallocated Cash.
		@param B_UnallocatedCash_Acct 
		Unallocated Cash Clearing Account
	  */
	public void setB_UnallocatedCash_Acct (int B_UnallocatedCash_Acct)
	{
		set_Value (COLUMNNAME_B_UnallocatedCash_Acct, Integer.valueOf(B_UnallocatedCash_Acct));
	}

	/** Get Unallocated Cash.
		@return Unallocated Cash Clearing Account
	  */
	public int getB_UnallocatedCash_Acct () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_B_UnallocatedCash_Acct);
		if (ii == null)
			 return 0;
		return ii.intValue();
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

	public org.compiere.model.I_C_BankAccount getC_BankAccount() throws RuntimeException
    {
		return (org.compiere.model.I_C_BankAccount)MTable.get(getCtx(), org.compiere.model.I_C_BankAccount.Table_Name)
			.getPO(getC_BankAccount_ID(), get_TrxName());	}

	/** Set Bank Account.
		@param C_BankAccount_ID 
		Account at the Bank
	  */
	public void setC_BankAccount_ID (int C_BankAccount_ID)
	{
		if (C_BankAccount_ID < 1) 
			set_Value (COLUMNNAME_C_BankAccount_ID, null);
		else 
			set_Value (COLUMNNAME_C_BankAccount_ID, Integer.valueOf(C_BankAccount_ID));
	}

	/** Get Bank Account.
		@return Account at the Bank
	  */
	public int getC_BankAccount_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_BankAccount_ID);
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

	public org.compiere.model.I_C_Currency getC_Currency() throws RuntimeException
    {
		return (org.compiere.model.I_C_Currency)MTable.get(getCtx(), org.compiere.model.I_C_Currency.Table_Name)
			.getPO(getC_Currency_ID(), get_TrxName());	}

	/** Set Currency.
		@param C_Currency_ID 
		The Currency for this record
	  */
	public void setC_Currency_ID (int C_Currency_ID)
	{
		if (C_Currency_ID < 1) 
			set_Value (COLUMNNAME_C_Currency_ID, null);
		else 
			set_Value (COLUMNNAME_C_Currency_ID, Integer.valueOf(C_Currency_ID));
	}

	/** Get Currency.
		@return The Currency for this record
	  */
	public int getC_Currency_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Currency_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Credit limit.
		@param CreditLimit 
		Amount of Credit allowed
	  */
	public void setCreditLimit (BigDecimal CreditLimit)
	{
		set_Value (COLUMNNAME_CreditLimit, CreditLimit);
	}

	/** Get Credit limit.
		@return Amount of Credit allowed
	  */
	public BigDecimal getCreditLimit () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_CreditLimit);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Current balance.
		@param CurrentBalance 
		Current Balance
	  */
	public void setCurrentBalance (BigDecimal CurrentBalance)
	{
		set_Value (COLUMNNAME_CurrentBalance, CurrentBalance);
	}

	/** Get Current balance.
		@return Current Balance
	  */
	public BigDecimal getCurrentBalance () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_CurrentBalance);
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

	/** Set ISO Currency Code.
		@param ISO_Code 
		Three letter ISO 4217 Code of the Currency
	  */
	public void setISO_Code (String ISO_Code)
	{
		set_Value (COLUMNNAME_ISO_Code, ISO_Code);
	}

	/** Get ISO Currency Code.
		@return Three letter ISO 4217 Code of the Currency
	  */
	public String getISO_Code () 
	{
		return (String)get_Value(COLUMNNAME_ISO_Code);
	}

	/** Set I_BankAccountJP.
		@param I_BankAccountJP_ID I_BankAccountJP	  */
	public void setI_BankAccountJP_ID (int I_BankAccountJP_ID)
	{
		if (I_BankAccountJP_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_I_BankAccountJP_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_I_BankAccountJP_ID, Integer.valueOf(I_BankAccountJP_ID));
	}

	/** Get I_BankAccountJP.
		@return I_BankAccountJP	  */
	public int getI_BankAccountJP_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_I_BankAccountJP_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set I_BankAccountJP_UU.
		@param I_BankAccountJP_UU I_BankAccountJP_UU	  */
	public void setI_BankAccountJP_UU (String I_BankAccountJP_UU)
	{
		set_ValueNoCheck (COLUMNNAME_I_BankAccountJP_UU, I_BankAccountJP_UU);
	}

	/** Get I_BankAccountJP_UU.
		@return I_BankAccountJP_UU	  */
	public String getI_BankAccountJP_UU () 
	{
		return (String)get_Value(COLUMNNAME_I_BankAccountJP_UU);
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

	/** Set Active(For Import).
		@param I_IsActiveJP 
		Active flag for Import Date
	  */
	public void setI_IsActiveJP (boolean I_IsActiveJP)
	{
		set_Value (COLUMNNAME_I_IsActiveJP, Boolean.valueOf(I_IsActiveJP));
	}

	/** Get Active(For Import).
		@return Active flag for Import Date
	  */
	public boolean isI_IsActiveJP () 
	{
		Object oo = get_Value(COLUMNNAME_I_IsActiveJP);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
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

	/** Set Accounting Schema(Name).
		@param JP_AcctSchema_Name Accounting Schema(Name)	  */
	public void setJP_AcctSchema_Name (String JP_AcctSchema_Name)
	{
		set_Value (COLUMNNAME_JP_AcctSchema_Name, JP_AcctSchema_Name);
	}

	/** Get Accounting Schema(Name).
		@return Accounting Schema(Name)	  */
	public String getJP_AcctSchema_Name () 
	{
		return (String)get_Value(COLUMNNAME_JP_AcctSchema_Name);
	}

	/** Set Bank Asset(Search key).
		@param JP_B_Asset_Acct_Value Bank Asset(Search key)	  */
	public void setJP_B_Asset_Acct_Value (String JP_B_Asset_Acct_Value)
	{
		set_Value (COLUMNNAME_JP_B_Asset_Acct_Value, JP_B_Asset_Acct_Value);
	}

	/** Get Bank Asset(Search key).
		@return Bank Asset(Search key)	  */
	public String getJP_B_Asset_Acct_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_B_Asset_Acct_Value);
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

	/** Set Bank In Transit(Search key).
		@param JP_InTransit_Acct_Value Bank In Transit(Search key)	  */
	public void setJP_InTransit_Acct_Value (String JP_InTransit_Acct_Value)
	{
		set_Value (COLUMNNAME_JP_InTransit_Acct_Value, JP_InTransit_Acct_Value);
	}

	/** Get Bank In Transit(Search key).
		@return Bank In Transit(Search key)	  */
	public String getJP_InTransit_Acct_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_InTransit_Acct_Value);
	}

	/** Set Bank Interest Expense(Search Key).
		@param JP_InterestExp_Acct_Value Bank Interest Expense(Search Key)	  */
	public void setJP_InterestExp_Acct_Value (String JP_InterestExp_Acct_Value)
	{
		set_Value (COLUMNNAME_JP_InterestExp_Acct_Value, JP_InterestExp_Acct_Value);
	}

	/** Get Bank Interest Expense(Search Key).
		@return Bank Interest Expense(Search Key)	  */
	public String getJP_InterestExp_Acct_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_InterestExp_Acct_Value);
	}

	/** Set Bank Interest Revenue(Search key).
		@param JP_InterestRev_Acct_Value Bank Interest Revenue(Search key)	  */
	public void setJP_InterestRev_Acct_Value (String JP_InterestRev_Acct_Value)
	{
		set_Value (COLUMNNAME_JP_InterestRev_Acct_Value, JP_InterestRev_Acct_Value);
	}

	/** Get Bank Interest Revenue(Search key).
		@return Bank Interest Revenue(Search key)	  */
	public String getJP_InterestRev_Acct_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_InterestRev_Acct_Value);
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

	/** Set Payment Selection(Search Key).
		@param JP_PaymentSelect_Value Payment Selection(Search Key)	  */
	public void setJP_PaymentSelect_Value (String JP_PaymentSelect_Value)
	{
		set_Value (COLUMNNAME_JP_PaymentSelect_Value, JP_PaymentSelect_Value);
	}

	/** Get Payment Selection(Search Key).
		@return Payment Selection(Search Key)	  */
	public String getJP_PaymentSelect_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_PaymentSelect_Value);
	}

	/** Set Requester Code.
		@param JP_RequesterCode Requester Code	  */
	public void setJP_RequesterCode (String JP_RequesterCode)
	{
		set_Value (COLUMNNAME_JP_RequesterCode, JP_RequesterCode);
	}

	/** Get Requester Code.
		@return Requester Code	  */
	public String getJP_RequesterCode () 
	{
		return (String)get_Value(COLUMNNAME_JP_RequesterCode);
	}

	/** Set Requester Name.
		@param JP_RequesterName Requester Name	  */
	public void setJP_RequesterName (String JP_RequesterName)
	{
		set_Value (COLUMNNAME_JP_RequesterName, JP_RequesterName);
	}

	/** Get Requester Name.
		@return Requester Name	  */
	public String getJP_RequesterName () 
	{
		return (String)get_Value(COLUMNNAME_JP_RequesterName);
	}

	/** Set Unallocated Cash(Search Key).
		@param JP_UnallocatedCash_Value Unallocated Cash(Search Key)	  */
	public void setJP_UnallocatedCash_Value (String JP_UnallocatedCash_Value)
	{
		set_Value (COLUMNNAME_JP_UnallocatedCash_Value, JP_UnallocatedCash_Value);
	}

	/** Get Unallocated Cash(Search Key).
		@return Unallocated Cash(Search Key)	  */
	public String getJP_UnallocatedCash_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_UnallocatedCash_Value);
	}

	/** Set Name.
		@param Name 
		Alphanumeric identifier of the entity
	  */
	public void setName (String Name)
	{
		set_Value (COLUMNNAME_Name, Name);
	}

	/** Get Name.
		@return Alphanumeric identifier of the entity
	  */
	public String getName () 
	{
		return (String)get_Value(COLUMNNAME_Name);
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

	/** Set Search Key.
		@param Value 
		Search key for the record in the format required - must be unique
	  */
	public void setValue (String Value)
	{
		set_Value (COLUMNNAME_Value, Value);
	}

	/** Get Search Key.
		@return Search key for the record in the format required - must be unique
	  */
	public String getValue () 
	{
		return (String)get_Value(COLUMNNAME_Value);
	}
}