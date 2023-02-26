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

/** Generated Model for I_BankDataJP
 *  @author iDempiere (generated) 
 *  @version Release 10 - $Id$ */
@org.adempiere.base.Model(table="I_BankDataJP")
public class X_I_BankDataJP extends PO implements I_I_BankDataJP, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20230225L;

    /** Standard Constructor */
    public X_I_BankDataJP (Properties ctx, int I_BankDataJP_ID, String trxName)
    {
      super (ctx, I_BankDataJP_ID, trxName);
      /** if (I_BankDataJP_ID == 0)
        {
			setI_BankDataJP_ID (0);
			setI_IsImported (false);
// N
			setProcessed (false);
// N
			setStmtAmt (Env.ZERO);
// 0
			setTrxAmt (Env.ZERO);
// 0
        } */
    }

    /** Standard Constructor */
    public X_I_BankDataJP (Properties ctx, int I_BankDataJP_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, I_BankDataJP_ID, trxName, virtualColumns);
      /** if (I_BankDataJP_ID == 0)
        {
			setI_BankDataJP_ID (0);
			setI_IsImported (false);
// N
			setProcessed (false);
// N
			setStmtAmt (Env.ZERO);
// 0
			setTrxAmt (Env.ZERO);
// 0
        } */
    }

    /** Load Constructor */
    public X_I_BankDataJP (Properties ctx, ResultSet rs, String trxName)
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
      StringBuilder sb = new StringBuilder ("X_I_BankDataJP[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set Trx Organization.
		@param AD_OrgTrx_ID Performing or initiating organization
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
	public int getAD_OrgTrx_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_OrgTrx_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Account No.
		@param AccountNo Account Number
	*/
	public void setAccountNo (String AccountNo)
	{
		set_Value (COLUMNNAME_AccountNo, AccountNo);
	}

	/** Get Account No.
		@return Account Number
	  */
	public String getAccountNo()
	{
		return (String)get_Value(COLUMNNAME_AccountNo);
	}

	/** Cash = B */
	public static final String BANKACCOUNTTYPE_Cash = "B";
	/** Checking = C */
	public static final String BANKACCOUNTTYPE_Checking = "C";
	/** Card = D */
	public static final String BANKACCOUNTTYPE_Card = "D";
	/** Gold note = G */
	public static final String BANKACCOUNTTYPE_GoldNote = "G";
	/** Cash in Register = R */
	public static final String BANKACCOUNTTYPE_CashInRegister = "R";
	/** Savings = S */
	public static final String BANKACCOUNTTYPE_Savings = "S";
	/** Set Bank Account Type.
		@param BankAccountType Bank Account Type
	*/
	public void setBankAccountType (String BankAccountType)
	{

		set_Value (COLUMNNAME_BankAccountType, BankAccountType);
	}

	/** Get Bank Account Type.
		@return Bank Account Type
	  */
	public String getBankAccountType()
	{
		return (String)get_Value(COLUMNNAME_BankAccountType);
	}

	public org.compiere.model.I_C_BankAccount getC_BankAccount() throws RuntimeException
	{
		return (org.compiere.model.I_C_BankAccount)MTable.get(getCtx(), org.compiere.model.I_C_BankAccount.Table_ID)
			.getPO(getC_BankAccount_ID(), get_TrxName());
	}

	/** Set Bank Account.
		@param C_BankAccount_ID Account at the Bank
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
	public int getC_BankAccount_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_BankAccount_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Bank getC_Bank() throws RuntimeException
	{
		return (org.compiere.model.I_C_Bank)MTable.get(getCtx(), org.compiere.model.I_C_Bank.Table_ID)
			.getPO(getC_Bank_ID(), get_TrxName());
	}

	/** Set Bank.
		@param C_Bank_ID Bank
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
	public int getC_Bank_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Bank_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Account Date.
		@param DateAcct Accounting Date
	*/
	public void setDateAcct (Timestamp DateAcct)
	{
		set_Value (COLUMNNAME_DateAcct, DateAcct);
	}

	/** Get Account Date.
		@return Accounting Date
	  */
	public Timestamp getDateAcct()
	{
		return (Timestamp)get_Value(COLUMNNAME_DateAcct);
	}

	/** Set I_BankDataJP.
		@param I_BankDataJP_ID JPIERE-0595:JPBP
	*/
	public void setI_BankDataJP_ID (int I_BankDataJP_ID)
	{
		if (I_BankDataJP_ID < 1)
			set_ValueNoCheck (COLUMNNAME_I_BankDataJP_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_I_BankDataJP_ID, Integer.valueOf(I_BankDataJP_ID));
	}

	/** Get I_BankDataJP.
		@return JPIERE-0595:JPBP
	  */
	public int getI_BankDataJP_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_I_BankDataJP_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set I_BankDataJP_UU.
		@param I_BankDataJP_UU I_BankDataJP_UU
	*/
	public void setI_BankDataJP_UU (String I_BankDataJP_UU)
	{
		set_Value (COLUMNNAME_I_BankDataJP_UU, I_BankDataJP_UU);
	}

	/** Get I_BankDataJP_UU.
		@return I_BankDataJP_UU	  */
	public String getI_BankDataJP_UU()
	{
		return (String)get_Value(COLUMNNAME_I_BankDataJP_UU);
	}

	/** Set Import Error Message.
		@param I_ErrorMsg Messages generated from import process
	*/
	public void setI_ErrorMsg (String I_ErrorMsg)
	{
		set_Value (COLUMNNAME_I_ErrorMsg, I_ErrorMsg);
	}

	/** Get Import Error Message.
		@return Messages generated from import process
	  */
	public String getI_ErrorMsg()
	{
		return (String)get_Value(COLUMNNAME_I_ErrorMsg);
	}

	/** Set Imported.
		@param I_IsImported Has this import been processed
	*/
	public void setI_IsImported (boolean I_IsImported)
	{
		set_Value (COLUMNNAME_I_IsImported, Boolean.valueOf(I_IsImported));
	}

	/** Get Imported.
		@return Has this import been processed
	  */
	public boolean isI_IsImported()
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

	/** Set Account Name.
		@param JP_A_Name Account Name
	*/
	public void setJP_A_Name (String JP_A_Name)
	{
		set_Value (COLUMNNAME_JP_A_Name, JP_A_Name);
	}

	/** Get Account Name.
		@return Account Name	  */
	public String getJP_A_Name()
	{
		return (String)get_Value(COLUMNNAME_JP_A_Name);
	}

	/** Set Account Name(Kana).
		@param JP_A_Name_Kana Account Name(Kana)
	*/
	public void setJP_A_Name_Kana (String JP_A_Name_Kana)
	{
		set_Value (COLUMNNAME_JP_A_Name_Kana, JP_A_Name_Kana);
	}

	/** Get Account Name(Kana).
		@return Account Name(Kana)	  */
	public String getJP_A_Name_Kana()
	{
		return (String)get_Value(COLUMNNAME_JP_A_Name_Kana);
	}

	/** Set Date of Account Date.
		@param JP_AcctDate Date of Account Date
	*/
	public void setJP_AcctDate (String JP_AcctDate)
	{
		set_Value (COLUMNNAME_JP_AcctDate, JP_AcctDate);
	}

	/** Get Date of Account Date.
		@return Date of Account Date	  */
	public String getJP_AcctDate()
	{
		return (String)get_Value(COLUMNNAME_JP_AcctDate);
	}

	/** Set Month of Account Date.
		@param JP_AcctMonth Month of Account Date
	*/
	public void setJP_AcctMonth (String JP_AcctMonth)
	{
		set_Value (COLUMNNAME_JP_AcctMonth, JP_AcctMonth);
	}

	/** Get Month of Account Date.
		@return Month of Account Date	  */
	public String getJP_AcctMonth()
	{
		return (String)get_Value(COLUMNNAME_JP_AcctMonth);
	}

	/** Set Bank Account Type.
		@param JP_BankAccountType Bank Account Type
	*/
	public void setJP_BankAccountType (String JP_BankAccountType)
	{
		set_Value (COLUMNNAME_JP_BankAccountType, JP_BankAccountType);
	}

	/** Get Bank Account Type.
		@return Bank Account Type	  */
	public String getJP_BankAccountType()
	{
		return (String)get_Value(COLUMNNAME_JP_BankAccountType);
	}

	/** Set Bank Account(Search Key).
		@param JP_BankAccount_Value Bank Account(Search Key)
	*/
	public void setJP_BankAccount_Value (String JP_BankAccount_Value)
	{
		set_Value (COLUMNNAME_JP_BankAccount_Value, JP_BankAccount_Value);
	}

	/** Get Bank Account(Search Key).
		@return Bank Account(Search Key)	  */
	public String getJP_BankAccount_Value()
	{
		return (String)get_Value(COLUMNNAME_JP_BankAccount_Value);
	}

	/** Set Bank Data Customer Code1.
		@param JP_BankDataCustomerCode1 Bank Data Customer Code1
	*/
	public void setJP_BankDataCustomerCode1 (String JP_BankDataCustomerCode1)
	{
		set_Value (COLUMNNAME_JP_BankDataCustomerCode1, JP_BankDataCustomerCode1);
	}

	/** Get Bank Data Customer Code1.
		@return Bank Data Customer Code1	  */
	public String getJP_BankDataCustomerCode1()
	{
		return (String)get_Value(COLUMNNAME_JP_BankDataCustomerCode1);
	}

	/** Set Bank Data Customer Code2.
		@param JP_BankDataCustomerCode2 Bank Data Customer Code2
	*/
	public void setJP_BankDataCustomerCode2 (String JP_BankDataCustomerCode2)
	{
		set_Value (COLUMNNAME_JP_BankDataCustomerCode2, JP_BankDataCustomerCode2);
	}

	/** Get Bank Data Customer Code2.
		@return Bank Data Customer Code2	  */
	public String getJP_BankDataCustomerCode2()
	{
		return (String)get_Value(COLUMNNAME_JP_BankDataCustomerCode2);
	}

	public I_JP_BankDataLine getJP_BankDataLine() throws RuntimeException
	{
		return (I_JP_BankDataLine)MTable.get(getCtx(), I_JP_BankDataLine.Table_ID)
			.getPO(getJP_BankDataLine_ID(), get_TrxName());
	}

	/** Set Import Bank Data Line.
		@param JP_BankDataLine_ID Import Bank Data Line
	*/
	public void setJP_BankDataLine_ID (int JP_BankDataLine_ID)
	{
		if (JP_BankDataLine_ID < 1)
			set_ValueNoCheck (COLUMNNAME_JP_BankDataLine_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_JP_BankDataLine_ID, Integer.valueOf(JP_BankDataLine_ID));
	}

	/** Get Import Bank Data Line.
		@return Import Bank Data Line	  */
	public int getJP_BankDataLine_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_BankDataLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set BankData EDI Info.
		@param JP_BankData_EDI_Info BankData EDI Info
	*/
	public void setJP_BankData_EDI_Info (String JP_BankData_EDI_Info)
	{
		set_Value (COLUMNNAME_JP_BankData_EDI_Info, JP_BankData_EDI_Info);
	}

	/** Get BankData EDI Info.
		@return BankData EDI Info	  */
	public String getJP_BankData_EDI_Info()
	{
		return (String)get_Value(COLUMNNAME_JP_BankData_EDI_Info);
	}

	public I_JP_BankData getJP_BankData() throws RuntimeException
	{
		return (I_JP_BankData)MTable.get(getCtx(), I_JP_BankData.Table_ID)
			.getPO(getJP_BankData_ID(), get_TrxName());
	}

	/** Set Import Bank Data.
		@param JP_BankData_ID Import Bank Data
	*/
	public void setJP_BankData_ID (int JP_BankData_ID)
	{
		if (JP_BankData_ID < 1)
			set_ValueNoCheck (COLUMNNAME_JP_BankData_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_JP_BankData_ID, Integer.valueOf(JP_BankData_ID));
	}

	/** Get Import Bank Data.
		@return Import Bank Data	  */
	public int getJP_BankData_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_BankData_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Bank Data ReferenceNo.
		@param JP_BankData_ReferenceNo Bank Data ReferenceNo
	*/
	public void setJP_BankData_ReferenceNo (String JP_BankData_ReferenceNo)
	{
		set_Value (COLUMNNAME_JP_BankData_ReferenceNo, JP_BankData_ReferenceNo);
	}

	/** Get Bank Data ReferenceNo.
		@return Bank Data ReferenceNo	  */
	public String getJP_BankData_ReferenceNo()
	{
		return (String)get_Value(COLUMNNAME_JP_BankData_ReferenceNo);
	}

	/** Set Bank Name(Kana).
		@param JP_BankName_Kana Bank Name(Kana)
	*/
	public void setJP_BankName_Kana (String JP_BankName_Kana)
	{
		set_Value (COLUMNNAME_JP_BankName_Kana, JP_BankName_Kana);
	}

	/** Get Bank Name(Kana).
		@return Bank Name(Kana)	  */
	public String getJP_BankName_Kana()
	{
		return (String)get_Value(COLUMNNAME_JP_BankName_Kana);
	}

	/** Set Bank Name(Kana) Line.
		@param JP_BankName_Kana_Line Bank Name(Kana) Line
	*/
	public void setJP_BankName_Kana_Line (String JP_BankName_Kana_Line)
	{
		set_Value (COLUMNNAME_JP_BankName_Kana_Line, JP_BankName_Kana_Line);
	}

	/** Get Bank Name(Kana) Line.
		@return Bank Name(Kana) Line	  */
	public String getJP_BankName_Kana_Line()
	{
		return (String)get_Value(COLUMNNAME_JP_BankName_Kana_Line);
	}

	/** Set Bank Name.
		@param JP_Bank_Name Bank Name
	*/
	public void setJP_Bank_Name (String JP_Bank_Name)
	{
		set_Value (COLUMNNAME_JP_Bank_Name, JP_Bank_Name);
	}

	/** Get Bank Name.
		@return Bank Name	  */
	public String getJP_Bank_Name()
	{
		return (String)get_Value(COLUMNNAME_JP_Bank_Name);
	}

	/** Set Branch Code.
		@param JP_BranchCode Branch Code
	*/
	public void setJP_BranchCode (String JP_BranchCode)
	{
		set_Value (COLUMNNAME_JP_BranchCode, JP_BranchCode);
	}

	/** Get Branch Code.
		@return Branch Code	  */
	public String getJP_BranchCode()
	{
		return (String)get_Value(COLUMNNAME_JP_BranchCode);
	}

	/** Set Branch Name.
		@param JP_BranchName Branch Name
	*/
	public void setJP_BranchName (String JP_BranchName)
	{
		set_Value (COLUMNNAME_JP_BranchName, JP_BranchName);
	}

	/** Get Branch Name.
		@return Branch Name	  */
	public String getJP_BranchName()
	{
		return (String)get_Value(COLUMNNAME_JP_BranchName);
	}

	/** Set Branch Name(Kana).
		@param JP_BranchName_Kana Branch Name(Kana)
	*/
	public void setJP_BranchName_Kana (String JP_BranchName_Kana)
	{
		set_Value (COLUMNNAME_JP_BranchName_Kana, JP_BranchName_Kana);
	}

	/** Get Branch Name(Kana).
		@return Branch Name(Kana)	  */
	public String getJP_BranchName_Kana()
	{
		return (String)get_Value(COLUMNNAME_JP_BranchName_Kana);
	}

	/** Set Branch Name(Kana) Line.
		@param JP_BranchName_Kana_Line Branch Name(Kana) Line
	*/
	public void setJP_BranchName_Kana_Line (String JP_BranchName_Kana_Line)
	{
		set_Value (COLUMNNAME_JP_BranchName_Kana_Line, JP_BranchName_Kana_Line);
	}

	/** Get Branch Name(Kana) Line.
		@return Branch Name(Kana) Line	  */
	public String getJP_BranchName_Kana_Line()
	{
		return (String)get_Value(COLUMNNAME_JP_BranchName_Kana_Line);
	}

	/** Set Date.
		@param JP_Date Date
	*/
	public void setJP_Date (String JP_Date)
	{
		set_Value (COLUMNNAME_JP_Date, JP_Date);
	}

	/** Get Date.
		@return Date
	  */
	public String getJP_Date()
	{
		return (String)get_Value(COLUMNNAME_JP_Date);
	}

	/** Set Line Description.
		@param JP_Line_Description Line Description
	*/
	public void setJP_Line_Description (String JP_Line_Description)
	{
		set_Value (COLUMNNAME_JP_Line_Description, JP_Line_Description);
	}

	/** Get Line Description.
		@return Line Description	  */
	public String getJP_Line_Description()
	{
		return (String)get_Value(COLUMNNAME_JP_Line_Description);
	}

	/** Set Month.
		@param JP_Month Month
	*/
	public void setJP_Month (String JP_Month)
	{
		set_Value (COLUMNNAME_JP_Month, JP_Month);
	}

	/** Get Month.
		@return Month	  */
	public String getJP_Month()
	{
		return (String)get_Value(COLUMNNAME_JP_Month);
	}

	/** Set Trx Organization(Search Key).
		@param JP_OrgTrx_Value Trx Organization(Search Key)
	*/
	public void setJP_OrgTrx_Value (String JP_OrgTrx_Value)
	{
		set_Value (COLUMNNAME_JP_OrgTrx_Value, JP_OrgTrx_Value);
	}

	/** Get Trx Organization(Search Key).
		@return Trx Organization(Search Key)	  */
	public String getJP_OrgTrx_Value()
	{
		return (String)get_Value(COLUMNNAME_JP_OrgTrx_Value);
	}

	/** Set Organization(Search Key).
		@param JP_Org_Value Organization(Search Key)
	*/
	public void setJP_Org_Value (String JP_Org_Value)
	{
		set_Value (COLUMNNAME_JP_Org_Value, JP_Org_Value);
	}

	/** Get Organization(Search Key).
		@return Organization(Search Key)	  */
	public String getJP_Org_Value()
	{
		return (String)get_Value(COLUMNNAME_JP_Org_Value);
	}

	/** Set Requester Name.
		@param JP_RequesterName Requester Name
	*/
	public void setJP_RequesterName (String JP_RequesterName)
	{
		set_Value (COLUMNNAME_JP_RequesterName, JP_RequesterName);
	}

	/** Get Requester Name.
		@return Requester Name	  */
	public String getJP_RequesterName()
	{
		return (String)get_Value(COLUMNNAME_JP_RequesterName);
	}

	/** Set Sales Rep(E-Mail).
		@param JP_SalesRep_EMail Sales Rep(E-Mail)
	*/
	public void setJP_SalesRep_EMail (String JP_SalesRep_EMail)
	{
		set_Value (COLUMNNAME_JP_SalesRep_EMail, JP_SalesRep_EMail);
	}

	/** Get Sales Rep(E-Mail).
		@return Sales Rep(E-Mail)	  */
	public String getJP_SalesRep_EMail()
	{
		return (String)get_Value(COLUMNNAME_JP_SalesRep_EMail);
	}

	/** Set Sales Rep(Name).
		@param JP_SalesRep_Name Sales Rep(Name)
	*/
	public void setJP_SalesRep_Name (String JP_SalesRep_Name)
	{
		set_Value (COLUMNNAME_JP_SalesRep_Name, JP_SalesRep_Name);
	}

	/** Get Sales Rep(Name).
		@return Sales Rep(Name)	  */
	public String getJP_SalesRep_Name()
	{
		return (String)get_Value(COLUMNNAME_JP_SalesRep_Name);
	}

	/** Set Sales Rep(Search Key).
		@param JP_SalesRep_Value Sales Rep(Search Key)
	*/
	public void setJP_SalesRep_Value (String JP_SalesRep_Value)
	{
		set_Value (COLUMNNAME_JP_SalesRep_Value, JP_SalesRep_Value);
	}

	/** Get Sales Rep(Search Key).
		@return Sales Rep(Search Key)	  */
	public String getJP_SalesRep_Value()
	{
		return (String)get_Value(COLUMNNAME_JP_SalesRep_Value);
	}

	/** Set Processed.
		@param Processed The document has been processed
	*/
	public void setProcessed (boolean Processed)
	{
		set_Value (COLUMNNAME_Processed, Boolean.valueOf(Processed));
	}

	/** Get Processed.
		@return The document has been processed
	  */
	public boolean isProcessed()
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
		@param Processing Process Now
	*/
	public void setProcessing (boolean Processing)
	{
		set_Value (COLUMNNAME_Processing, Boolean.valueOf(Processing));
	}

	/** Get Process Now.
		@return Process Now	  */
	public boolean isProcessing()
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

	/** Set Routing No.
		@param RoutingNo Bank Routing Number
	*/
	public void setRoutingNo (String RoutingNo)
	{
		set_Value (COLUMNNAME_RoutingNo, RoutingNo);
	}

	/** Get Routing No.
		@return Bank Routing Number
	  */
	public String getRoutingNo()
	{
		return (String)get_Value(COLUMNNAME_RoutingNo);
	}

	public org.compiere.model.I_AD_User getSalesRep() throws RuntimeException
	{
		return (org.compiere.model.I_AD_User)MTable.get(getCtx(), org.compiere.model.I_AD_User.Table_ID)
			.getPO(getSalesRep_ID(), get_TrxName());
	}

	/** Set Sales Rep.
		@param SalesRep_ID Sales Representative or Company Agent
	*/
	public void setSalesRep_ID (int SalesRep_ID)
	{
		if (SalesRep_ID < 1)
			set_Value (COLUMNNAME_SalesRep_ID, null);
		else
			set_Value (COLUMNNAME_SalesRep_ID, Integer.valueOf(SalesRep_ID));
	}

	/** Get Sales Rep.
		@return Sales Representative or Company Agent
	  */
	public int getSalesRep_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_SalesRep_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Statement date.
		@param StatementDate Date of the statement
	*/
	public void setStatementDate (Timestamp StatementDate)
	{
		set_Value (COLUMNNAME_StatementDate, StatementDate);
	}

	/** Get Statement date.
		@return Date of the statement
	  */
	public Timestamp getStatementDate()
	{
		return (Timestamp)get_Value(COLUMNNAME_StatementDate);
	}

	/** Set Statement amount.
		@param StmtAmt Statement Amount
	*/
	public void setStmtAmt (BigDecimal StmtAmt)
	{
		set_Value (COLUMNNAME_StmtAmt, StmtAmt);
	}

	/** Get Statement amount.
		@return Statement Amount
	  */
	public BigDecimal getStmtAmt()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_StmtAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Transaction Amount.
		@param TrxAmt Amount of a transaction
	*/
	public void setTrxAmt (BigDecimal TrxAmt)
	{
		set_Value (COLUMNNAME_TrxAmt, TrxAmt);
	}

	/** Get Transaction Amount.
		@return Amount of a transaction
	  */
	public BigDecimal getTrxAmt()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_TrxAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}
}