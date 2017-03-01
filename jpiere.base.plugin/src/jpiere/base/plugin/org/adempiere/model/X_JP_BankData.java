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

/** Generated Model for JP_BankData
 *  @author iDempiere (generated) 
 *  @version Release 4.1 - $Id$ */
public class X_JP_BankData extends PO implements I_JP_BankData, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20170228L;

    /** Standard Constructor */
    public X_JP_BankData (Properties ctx, int JP_BankData_ID, String trxName)
    {
      super (ctx, JP_BankData_ID, trxName);
      /** if (JP_BankData_ID == 0)
        {
			setC_BankAccount_ID (0);
			setChargeAmt (Env.ZERO);
// 0
			setDateAcct (new Timestamp( System.currentTimeMillis() ));
// @#Date@
			setInterestAmt (Env.ZERO);
// 0
			setIsReceipt (true);
// Y
			setJP_BankDataSchema_ID (0);
			setJP_BankData_ID (0);
			setName (null);
			setNumLines (0);
// 0
			setStatementDate (new Timestamp( System.currentTimeMillis() ));
			setStmtAmt (Env.ZERO);
// 0
			setTotalAmt (Env.ZERO);
// 0
			setTrxAmt (Env.ZERO);
// 0
        } */
    }

    /** Load Constructor */
    public X_JP_BankData (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 1 - Org 
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
      StringBuffer sb = new StringBuffer ("X_JP_BankData[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set Trx Organization.
		@param AD_OrgTrx_ID 
		Performing or initiating organization
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
	public int getAD_OrgTrx_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_OrgTrx_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
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

	public org.compiere.model.I_C_BankStatement getC_BankStatement() throws RuntimeException
    {
		return (org.compiere.model.I_C_BankStatement)MTable.get(getCtx(), org.compiere.model.I_C_BankStatement.Table_Name)
			.getPO(getC_BankStatement_ID(), get_TrxName());	}

	/** Set Bank Statement.
		@param C_BankStatement_ID 
		Bank Statement of account
	  */
	public void setC_BankStatement_ID (int C_BankStatement_ID)
	{
		if (C_BankStatement_ID < 1) 
			set_Value (COLUMNNAME_C_BankStatement_ID, null);
		else 
			set_Value (COLUMNNAME_C_BankStatement_ID, Integer.valueOf(C_BankStatement_ID));
	}

	/** Get Bank Statement.
		@return Bank Statement of account
	  */
	public int getC_BankStatement_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_BankStatement_ID);
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
		throw new IllegalArgumentException ("C_Currency_ID is virtual column");	}

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

	/** Set Charge amount.
		@param ChargeAmt 
		Charge Amount
	  */
	public void setChargeAmt (BigDecimal ChargeAmt)
	{
		set_Value (COLUMNNAME_ChargeAmt, ChargeAmt);
	}

	/** Get Charge amount.
		@return Charge Amount
	  */
	public BigDecimal getChargeAmt () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_ChargeAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Account Date.
		@param DateAcct 
		Accounting Date
	  */
	public void setDateAcct (Timestamp DateAcct)
	{
		set_Value (COLUMNNAME_DateAcct, DateAcct);
	}

	/** Get Account Date.
		@return Accounting Date
	  */
	public Timestamp getDateAcct () 
	{
		return (Timestamp)get_Value(COLUMNNAME_DateAcct);
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

	/** Set Interest Amount.
		@param InterestAmt 
		Interest Amount
	  */
	public void setInterestAmt (BigDecimal InterestAmt)
	{
		set_Value (COLUMNNAME_InterestAmt, InterestAmt);
	}

	/** Get Interest Amount.
		@return Interest Amount
	  */
	public BigDecimal getInterestAmt () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_InterestAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Receipt.
		@param IsReceipt 
		This is a sales transaction (receipt)
	  */
	public void setIsReceipt (boolean IsReceipt)
	{
		set_Value (COLUMNNAME_IsReceipt, Boolean.valueOf(IsReceipt));
	}

	/** Get Receipt.
		@return This is a sales transaction (receipt)
	  */
	public boolean isReceipt () 
	{
		Object oo = get_Value(COLUMNNAME_IsReceipt);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Bank Account Type.
		@param JP_BankAccountType Bank Account Type	  */
	public void setJP_BankAccountType (String JP_BankAccountType)
	{
		set_Value (COLUMNNAME_JP_BankAccountType, JP_BankAccountType);
	}

	/** Get Bank Account Type.
		@return Bank Account Type	  */
	public String getJP_BankAccountType () 
	{
		return (String)get_Value(COLUMNNAME_JP_BankAccountType);
	}

	/** Set Bank Data Classification.
		@param JP_BankDataClassification Bank Data Classification	  */
	public void setJP_BankDataClassification (String JP_BankDataClassification)
	{
		set_Value (COLUMNNAME_JP_BankDataClassification, JP_BankDataClassification);
	}

	/** Get Bank Data Classification.
		@return Bank Data Classification	  */
	public String getJP_BankDataClassification () 
	{
		return (String)get_Value(COLUMNNAME_JP_BankDataClassification);
	}

	/** Set Bank Data Code Type.
		@param JP_BankDataCodeType Bank Data Code Type	  */
	public void setJP_BankDataCodeType (String JP_BankDataCodeType)
	{
		set_Value (COLUMNNAME_JP_BankDataCodeType, JP_BankDataCodeType);
	}

	/** Get Bank Data Code Type.
		@return Bank Data Code Type	  */
	public String getJP_BankDataCodeType () 
	{
		return (String)get_Value(COLUMNNAME_JP_BankDataCodeType);
	}

	public I_JP_BankDataSchema getJP_BankDataSchema() throws RuntimeException
    {
		return (I_JP_BankDataSchema)MTable.get(getCtx(), I_JP_BankDataSchema.Table_Name)
			.getPO(getJP_BankDataSchema_ID(), get_TrxName());	}

	/** Set Import Bank Data Schema.
		@param JP_BankDataSchema_ID Import Bank Data Schema	  */
	public void setJP_BankDataSchema_ID (int JP_BankDataSchema_ID)
	{
		if (JP_BankDataSchema_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_BankDataSchema_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_BankDataSchema_ID, Integer.valueOf(JP_BankDataSchema_ID));
	}

	/** Get Import Bank Data Schema.
		@return Import Bank Data Schema	  */
	public int getJP_BankDataSchema_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_BankDataSchema_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Bank Datat Type(Footer).
		@param JP_BankDataType_Footer Bank Datat Type(Footer)	  */
	public void setJP_BankDataType_Footer (String JP_BankDataType_Footer)
	{
		set_Value (COLUMNNAME_JP_BankDataType_Footer, JP_BankDataType_Footer);
	}

	/** Get Bank Datat Type(Footer).
		@return Bank Datat Type(Footer)	  */
	public String getJP_BankDataType_Footer () 
	{
		return (String)get_Value(COLUMNNAME_JP_BankDataType_Footer);
	}

	/** Set Bank Data Type(Header).
		@param JP_BankDataType_Header Bank Data Type(Header)	  */
	public void setJP_BankDataType_Header (String JP_BankDataType_Header)
	{
		set_Value (COLUMNNAME_JP_BankDataType_Header, JP_BankDataType_Header);
	}

	/** Get Bank Data Type(Header).
		@return Bank Data Type(Header)	  */
	public String getJP_BankDataType_Header () 
	{
		return (String)get_Value(COLUMNNAME_JP_BankDataType_Header);
	}

	/** Set Import Bank Data.
		@param JP_BankData_ID Import Bank Data	  */
	public void setJP_BankData_ID (int JP_BankData_ID)
	{
		if (JP_BankData_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_BankData_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_BankData_ID, Integer.valueOf(JP_BankData_ID));
	}

	/** Get Import Bank Data.
		@return Import Bank Data	  */
	public int getJP_BankData_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_BankData_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set JP_BankData_UU.
		@param JP_BankData_UU JP_BankData_UU	  */
	public void setJP_BankData_UU (String JP_BankData_UU)
	{
		set_ValueNoCheck (COLUMNNAME_JP_BankData_UU, JP_BankData_UU);
	}

	/** Get JP_BankData_UU.
		@return JP_BankData_UU	  */
	public String getJP_BankData_UU () 
	{
		return (String)get_Value(COLUMNNAME_JP_BankData_UU);
	}

	/** Set Bank Name(Kana).
		@param JP_BankName_Kana Bank Name(Kana)	  */
	public void setJP_BankName_Kana (String JP_BankName_Kana)
	{
		set_Value (COLUMNNAME_JP_BankName_Kana, JP_BankName_Kana);
	}

	/** Get Bank Name(Kana).
		@return Bank Name(Kana)	  */
	public String getJP_BankName_Kana () 
	{
		return (String)get_Value(COLUMNNAME_JP_BankName_Kana);
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

	/** Set Processed Time.
		@param JP_ProcessedTime1 Processed Time	  */
	public void setJP_ProcessedTime1 (Timestamp JP_ProcessedTime1)
	{
		set_Value (COLUMNNAME_JP_ProcessedTime1, JP_ProcessedTime1);
	}

	/** Get Processed Time.
		@return Processed Time	  */
	public Timestamp getJP_ProcessedTime1 () 
	{
		return (Timestamp)get_Value(COLUMNNAME_JP_ProcessedTime1);
	}

	/** Set Processed Time.
		@param JP_ProcessedTime2 Processed Time	  */
	public void setJP_ProcessedTime2 (Timestamp JP_ProcessedTime2)
	{
		set_Value (COLUMNNAME_JP_ProcessedTime2, JP_ProcessedTime2);
	}

	/** Get Processed Time.
		@return Processed Time	  */
	public Timestamp getJP_ProcessedTime2 () 
	{
		return (Timestamp)get_Value(COLUMNNAME_JP_ProcessedTime2);
	}

	/** Set Processed Time.
		@param JP_ProcessedTime3 Processed Time	  */
	public void setJP_ProcessedTime3 (Timestamp JP_ProcessedTime3)
	{
		set_Value (COLUMNNAME_JP_ProcessedTime3, JP_ProcessedTime3);
	}

	/** Get Processed Time.
		@return Processed Time	  */
	public Timestamp getJP_ProcessedTime3 () 
	{
		return (Timestamp)get_Value(COLUMNNAME_JP_ProcessedTime3);
	}

	/** Set Processed Time.
		@param JP_ProcessedTime4 Processed Time	  */
	public void setJP_ProcessedTime4 (Timestamp JP_ProcessedTime4)
	{
		set_Value (COLUMNNAME_JP_ProcessedTime4, JP_ProcessedTime4);
	}

	/** Get Processed Time.
		@return Processed Time	  */
	public Timestamp getJP_ProcessedTime4 () 
	{
		return (Timestamp)get_Value(COLUMNNAME_JP_ProcessedTime4);
	}

	/** Set Processed Time.
		@param JP_ProcessedTime5 Processed Time	  */
	public void setJP_ProcessedTime5 (Timestamp JP_ProcessedTime5)
	{
		set_Value (COLUMNNAME_JP_ProcessedTime5, JP_ProcessedTime5);
	}

	/** Get Processed Time.
		@return Processed Time	  */
	public Timestamp getJP_ProcessedTime5 () 
	{
		return (Timestamp)get_Value(COLUMNNAME_JP_ProcessedTime5);
	}

	/** Set Processed Time.
		@param JP_ProcessedTime6 Processed Time	  */
	public void setJP_ProcessedTime6 (Timestamp JP_ProcessedTime6)
	{
		set_Value (COLUMNNAME_JP_ProcessedTime6, JP_ProcessedTime6);
	}

	/** Get Processed Time.
		@return Processed Time	  */
	public Timestamp getJP_ProcessedTime6 () 
	{
		return (Timestamp)get_Value(COLUMNNAME_JP_ProcessedTime6);
	}

	/** Set Process Now.
		@param JP_Processing1 Process Now	  */
	public void setJP_Processing1 (String JP_Processing1)
	{
		set_Value (COLUMNNAME_JP_Processing1, JP_Processing1);
	}

	/** Get Process Now.
		@return Process Now	  */
	public String getJP_Processing1 () 
	{
		return (String)get_Value(COLUMNNAME_JP_Processing1);
	}

	/** Set Process Now.
		@param JP_Processing2 Process Now	  */
	public void setJP_Processing2 (String JP_Processing2)
	{
		set_Value (COLUMNNAME_JP_Processing2, JP_Processing2);
	}

	/** Get Process Now.
		@return Process Now	  */
	public String getJP_Processing2 () 
	{
		return (String)get_Value(COLUMNNAME_JP_Processing2);
	}

	/** Set Process Now.
		@param JP_Processing3 Process Now	  */
	public void setJP_Processing3 (String JP_Processing3)
	{
		set_Value (COLUMNNAME_JP_Processing3, JP_Processing3);
	}

	/** Get Process Now.
		@return Process Now	  */
	public String getJP_Processing3 () 
	{
		return (String)get_Value(COLUMNNAME_JP_Processing3);
	}

	/** Set Process Now.
		@param JP_Processing4 Process Now	  */
	public void setJP_Processing4 (String JP_Processing4)
	{
		set_Value (COLUMNNAME_JP_Processing4, JP_Processing4);
	}

	/** Get Process Now.
		@return Process Now	  */
	public String getJP_Processing4 () 
	{
		return (String)get_Value(COLUMNNAME_JP_Processing4);
	}

	/** Set Process Now.
		@param JP_Processing5 Process Now	  */
	public void setJP_Processing5 (String JP_Processing5)
	{
		set_Value (COLUMNNAME_JP_Processing5, JP_Processing5);
	}

	/** Get Process Now.
		@return Process Now	  */
	public String getJP_Processing5 () 
	{
		return (String)get_Value(COLUMNNAME_JP_Processing5);
	}

	/** Set Process Now.
		@param JP_Processing6 Process Now	  */
	public void setJP_Processing6 (String JP_Processing6)
	{
		set_Value (COLUMNNAME_JP_Processing6, JP_Processing6);
	}

	/** Get Process Now.
		@return Process Now	  */
	public String getJP_Processing6 () 
	{
		return (String)get_Value(COLUMNNAME_JP_Processing6);
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

	/** Set Number of Lines.
		@param NumLines 
		Number of lines for a field
	  */
	public void setNumLines (int NumLines)
	{
		set_Value (COLUMNNAME_NumLines, Integer.valueOf(NumLines));
	}

	/** Get Number of Lines.
		@return Number of lines for a field
	  */
	public int getNumLines () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_NumLines);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Payment date.
		@param PayDate 
		Date Payment made
	  */
	public void setPayDate (Timestamp PayDate)
	{
		set_Value (COLUMNNAME_PayDate, PayDate);
	}

	/** Get Payment date.
		@return Date Payment made
	  */
	public Timestamp getPayDate () 
	{
		return (Timestamp)get_Value(COLUMNNAME_PayDate);
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

	public org.compiere.model.I_AD_User getSalesRep() throws RuntimeException
    {
		return (org.compiere.model.I_AD_User)MTable.get(getCtx(), org.compiere.model.I_AD_User.Table_Name)
			.getPO(getSalesRep_ID(), get_TrxName());	}

	/** Set Sales Representative.
		@param SalesRep_ID 
		Sales Representative or Company Agent
	  */
	public void setSalesRep_ID (int SalesRep_ID)
	{
		if (SalesRep_ID < 1) 
			set_Value (COLUMNNAME_SalesRep_ID, null);
		else 
			set_Value (COLUMNNAME_SalesRep_ID, Integer.valueOf(SalesRep_ID));
	}

	/** Get Sales Representative.
		@return Sales Representative or Company Agent
	  */
	public int getSalesRep_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_SalesRep_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Statement date.
		@param StatementDate 
		Date of the statement
	  */
	public void setStatementDate (Timestamp StatementDate)
	{
		set_Value (COLUMNNAME_StatementDate, StatementDate);
	}

	/** Get Statement date.
		@return Date of the statement
	  */
	public Timestamp getStatementDate () 
	{
		return (Timestamp)get_Value(COLUMNNAME_StatementDate);
	}

	/** Set Statement amount.
		@param StmtAmt 
		Statement Amount
	  */
	public void setStmtAmt (BigDecimal StmtAmt)
	{
		set_Value (COLUMNNAME_StmtAmt, StmtAmt);
	}

	/** Get Statement amount.
		@return Statement Amount
	  */
	public BigDecimal getStmtAmt () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_StmtAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Total Amount.
		@param TotalAmt 
		Total Amount
	  */
	public void setTotalAmt (BigDecimal TotalAmt)
	{
		set_Value (COLUMNNAME_TotalAmt, TotalAmt);
	}

	/** Get Total Amount.
		@return Total Amount
	  */
	public BigDecimal getTotalAmt () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_TotalAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Transaction Amount.
		@param TrxAmt 
		Amount of a transaction
	  */
	public void setTrxAmt (BigDecimal TrxAmt)
	{
		set_Value (COLUMNNAME_TrxAmt, TrxAmt);
	}

	/** Get Transaction Amount.
		@return Amount of a transaction
	  */
	public BigDecimal getTrxAmt () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_TrxAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}
}