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

/** Generated Model for JP_BankDataLine
 *  @author iDempiere (generated) 
 *  @version Release 4.1 - $Id$ */
public class X_JP_BankDataLine extends PO implements I_JP_BankDataLine, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20170311L;

    /** Standard Constructor */
    public X_JP_BankDataLine (Properties ctx, int JP_BankDataLine_ID, String trxName)
    {
      super (ctx, JP_BankDataLine_ID, trxName);
      /** if (JP_BankDataLine_ID == 0)
        {
			setChargeAmt (Env.ZERO);
// 0
			setDateAcct (new Timestamp( System.currentTimeMillis() ));
			setInterestAmt (Env.ZERO);
// 0
			setIsMatchedJP (false);
// N
			setJP_BankDataLine_ID (0);
			setJP_BankData_ID (0);
			setLine (0);
			setStatementLineDate (new Timestamp( System.currentTimeMillis() ));
			setStmtAmt (Env.ZERO);
// 0
			setTrxAmt (Env.ZERO);
// 0
			setValutaDate (new Timestamp( System.currentTimeMillis() ));
        } */
    }

    /** Load Constructor */
    public X_JP_BankDataLine (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_JP_BankDataLine[")
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

	public org.compiere.model.I_C_BankStatementLine getC_BankStatementLine() throws RuntimeException
    {
		return (org.compiere.model.I_C_BankStatementLine)MTable.get(getCtx(), org.compiere.model.I_C_BankStatementLine.Table_Name)
			.getPO(getC_BankStatementLine_ID(), get_TrxName());	}

	/** Set Bank statement line.
		@param C_BankStatementLine_ID 
		Line on a statement from this Bank
	  */
	public void setC_BankStatementLine_ID (int C_BankStatementLine_ID)
	{
		if (C_BankStatementLine_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_C_BankStatementLine_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_BankStatementLine_ID, Integer.valueOf(C_BankStatementLine_ID));
	}

	/** Get Bank statement line.
		@return Line on a statement from this Bank
	  */
	public int getC_BankStatementLine_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_BankStatementLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Charge getC_Charge() throws RuntimeException
    {
		return (org.compiere.model.I_C_Charge)MTable.get(getCtx(), org.compiere.model.I_C_Charge.Table_Name)
			.getPO(getC_Charge_ID(), get_TrxName());	}

	/** Set Charge.
		@param C_Charge_ID 
		Additional document charges
	  */
	public void setC_Charge_ID (int C_Charge_ID)
	{
		if (C_Charge_ID < 1) 
			set_Value (COLUMNNAME_C_Charge_ID, null);
		else 
			set_Value (COLUMNNAME_C_Charge_ID, Integer.valueOf(C_Charge_ID));
	}

	/** Get Charge.
		@return Additional document charges
	  */
	public int getC_Charge_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Charge_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Invoice getC_Invoice() throws RuntimeException
    {
		return (org.compiere.model.I_C_Invoice)MTable.get(getCtx(), org.compiere.model.I_C_Invoice.Table_Name)
			.getPO(getC_Invoice_ID(), get_TrxName());	}

	/** Set Invoice.
		@param C_Invoice_ID 
		Invoice Identifier
	  */
	public void setC_Invoice_ID (int C_Invoice_ID)
	{
		if (C_Invoice_ID < 1) 
			set_Value (COLUMNNAME_C_Invoice_ID, null);
		else 
			set_Value (COLUMNNAME_C_Invoice_ID, Integer.valueOf(C_Invoice_ID));
	}

	/** Get Invoice.
		@return Invoice Identifier
	  */
	public int getC_Invoice_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Invoice_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Order getC_Order() throws RuntimeException
    {
		return (org.compiere.model.I_C_Order)MTable.get(getCtx(), org.compiere.model.I_C_Order.Table_Name)
			.getPO(getC_Order_ID(), get_TrxName());	}

	/** Set Order.
		@param C_Order_ID 
		Order
	  */
	public void setC_Order_ID (int C_Order_ID)
	{
		if (C_Order_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_C_Order_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_Order_ID, Integer.valueOf(C_Order_ID));
	}

	/** Get Order.
		@return Order
	  */
	public int getC_Order_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Order_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Payment getC_Payment() throws RuntimeException
    {
		return (org.compiere.model.I_C_Payment)MTable.get(getCtx(), org.compiere.model.I_C_Payment.Table_Name)
			.getPO(getC_Payment_ID(), get_TrxName());	}

	/** Set Payment.
		@param C_Payment_ID 
		Payment identifier
	  */
	public void setC_Payment_ID (int C_Payment_ID)
	{
		if (C_Payment_ID < 1) 
			set_Value (COLUMNNAME_C_Payment_ID, null);
		else 
			set_Value (COLUMNNAME_C_Payment_ID, Integer.valueOf(C_Payment_ID));
	}

	/** Get Payment.
		@return Payment identifier
	  */
	public int getC_Payment_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Payment_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Tax getC_Tax() throws RuntimeException
    {
		return (org.compiere.model.I_C_Tax)MTable.get(getCtx(), org.compiere.model.I_C_Tax.Table_Name)
			.getPO(getC_Tax_ID(), get_TrxName());	}

	/** Set Tax.
		@param C_Tax_ID 
		Tax identifier
	  */
	public void setC_Tax_ID (int C_Tax_ID)
	{
		if (C_Tax_ID < 1) 
			set_Value (COLUMNNAME_C_Tax_ID, null);
		else 
			set_Value (COLUMNNAME_C_Tax_ID, Integer.valueOf(C_Tax_ID));
	}

	/** Get Tax.
		@return Tax identifier
	  */
	public int getC_Tax_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Tax_ID);
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
		set_ValueNoCheck (COLUMNNAME_DateAcct, DateAcct);
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

	/** Set Matched.
		@param IsMatchedJP Matched	  */
	public void setIsMatchedJP (boolean IsMatchedJP)
	{
		set_Value (COLUMNNAME_IsMatchedJP, Boolean.valueOf(IsMatchedJP));
	}

	/** Get Matched.
		@return Matched	  */
	public boolean isMatchedJP () 
	{
		Object oo = get_Value(COLUMNNAME_IsMatchedJP);
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

	/** Set Bank Data Customer Code1.
		@param JP_BankDataCustomerCode1 Bank Data Customer Code1	  */
	public void setJP_BankDataCustomerCode1 (String JP_BankDataCustomerCode1)
	{
		set_Value (COLUMNNAME_JP_BankDataCustomerCode1, JP_BankDataCustomerCode1);
	}

	/** Get Bank Data Customer Code1.
		@return Bank Data Customer Code1	  */
	public String getJP_BankDataCustomerCode1 () 
	{
		return (String)get_Value(COLUMNNAME_JP_BankDataCustomerCode1);
	}

	/** Set Bank Data Customer Code2.
		@param JP_BankDataCustomerCode2 Bank Data Customer Code2	  */
	public void setJP_BankDataCustomerCode2 (String JP_BankDataCustomerCode2)
	{
		set_Value (COLUMNNAME_JP_BankDataCustomerCode2, JP_BankDataCustomerCode2);
	}

	/** Get Bank Data Customer Code2.
		@return Bank Data Customer Code2	  */
	public String getJP_BankDataCustomerCode2 () 
	{
		return (String)get_Value(COLUMNNAME_JP_BankDataCustomerCode2);
	}

	/** Set Import Bank Data Line.
		@param JP_BankDataLine_ID Import Bank Data Line	  */
	public void setJP_BankDataLine_ID (int JP_BankDataLine_ID)
	{
		if (JP_BankDataLine_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_BankDataLine_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_BankDataLine_ID, Integer.valueOf(JP_BankDataLine_ID));
	}

	/** Get Import Bank Data Line.
		@return Import Bank Data Line	  */
	public int getJP_BankDataLine_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_BankDataLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set JP_BankDataLine_UU.
		@param JP_BankDataLine_UU JP_BankDataLine_UU	  */
	public void setJP_BankDataLine_UU (String JP_BankDataLine_UU)
	{
		set_ValueNoCheck (COLUMNNAME_JP_BankDataLine_UU, JP_BankDataLine_UU);
	}

	/** Get JP_BankDataLine_UU.
		@return JP_BankDataLine_UU	  */
	public String getJP_BankDataLine_UU () 
	{
		return (String)get_Value(COLUMNNAME_JP_BankDataLine_UU);
	}

	/** Set Bank Data New Code.
		@param JP_BankDataNewCode Bank Data New Code	  */
	public void setJP_BankDataNewCode (String JP_BankDataNewCode)
	{
		set_Value (COLUMNNAME_JP_BankDataNewCode, JP_BankDataNewCode);
	}

	/** Get Bank Data New Code.
		@return Bank Data New Code	  */
	public String getJP_BankDataNewCode () 
	{
		return (String)get_Value(COLUMNNAME_JP_BankDataNewCode);
	}

	/** Set Bank Data Type(Line).
		@param JP_BankDataType_Line Bank Data Type(Line)	  */
	public void setJP_BankDataType_Line (String JP_BankDataType_Line)
	{
		set_Value (COLUMNNAME_JP_BankDataType_Line, JP_BankDataType_Line);
	}

	/** Get Bank Data Type(Line).
		@return Bank Data Type(Line)	  */
	public String getJP_BankDataType_Line () 
	{
		return (String)get_Value(COLUMNNAME_JP_BankDataType_Line);
	}

	public I_JP_BankData getJP_BankData() throws RuntimeException
    {
		return (I_JP_BankData)MTable.get(getCtx(), I_JP_BankData.Table_Name)
			.getPO(getJP_BankData_ID(), get_TrxName());	}

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

	public I_JP_Bill getJP_Bill() throws RuntimeException
    {
		return (I_JP_Bill)MTable.get(getCtx(), I_JP_Bill.Table_Name)
			.getPO(getJP_Bill_ID(), get_TrxName());	}

	/** Set JP Bill.
		@param JP_Bill_ID JP Bill	  */
	public void setJP_Bill_ID (int JP_Bill_ID)
	{
		if (JP_Bill_ID < 1) 
			set_Value (COLUMNNAME_JP_Bill_ID, null);
		else 
			set_Value (COLUMNNAME_JP_Bill_ID, Integer.valueOf(JP_Bill_ID));
	}

	/** Get JP Bill.
		@return JP Bill	  */
	public int getJP_Bill_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Bill_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
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

	/** Set Clearing House.
		@param JP_ClearingHouse Clearing House	  */
	public void setJP_ClearingHouse (String JP_ClearingHouse)
	{
		set_Value (COLUMNNAME_JP_ClearingHouse, JP_ClearingHouse);
	}

	/** Get Clearing House.
		@return Clearing House	  */
	public String getJP_ClearingHouse () 
	{
		return (String)get_Value(COLUMNNAME_JP_ClearingHouse);
	}

	/** Set Line No.
		@param Line 
		Unique line for this document
	  */
	public void setLine (int Line)
	{
		set_Value (COLUMNNAME_Line, Integer.valueOf(Line));
	}

	/** Get Line No.
		@return Unique line for this document
	  */
	public int getLine () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Line);
		if (ii == null)
			 return 0;
		return ii.intValue();
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

	/** Set Statement Line Date.
		@param StatementLineDate 
		Date of the Statement Line
	  */
	public void setStatementLineDate (Timestamp StatementLineDate)
	{
		set_Value (COLUMNNAME_StatementLineDate, StatementLineDate);
	}

	/** Get Statement Line Date.
		@return Date of the Statement Line
	  */
	public Timestamp getStatementLineDate () 
	{
		return (Timestamp)get_Value(COLUMNNAME_StatementLineDate);
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

	/** Set Effective date.
		@param ValutaDate 
		Date when money is available
	  */
	public void setValutaDate (Timestamp ValutaDate)
	{
		set_Value (COLUMNNAME_ValutaDate, ValutaDate);
	}

	/** Get Effective date.
		@return Date when money is available
	  */
	public Timestamp getValutaDate () 
	{
		return (Timestamp)get_Value(COLUMNNAME_ValutaDate);
	}
}