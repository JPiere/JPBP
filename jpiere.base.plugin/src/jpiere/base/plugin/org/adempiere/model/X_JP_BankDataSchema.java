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

/** Generated Model for JP_BankDataSchema
 *  @author iDempiere (generated) 
 *  @version Release 4.1 - $Id$ */
public class X_JP_BankDataSchema extends PO implements I_JP_BankDataSchema, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20170228L;

    /** Standard Constructor */
    public X_JP_BankDataSchema (Properties ctx, int JP_BankDataSchema_ID, String trxName)
    {
      super (ctx, JP_BankDataSchema_ID, trxName);
      /** if (JP_BankDataSchema_ID == 0)
        {
			setIsDefault (false);
			setIsReceipt (false);
			setJP_AcceptableDiffAmt (Env.ZERO);
			setJP_BankDataSchema_ID (0);
			setName (null);
			setValue (null);
        } */
    }

    /** Load Constructor */
    public X_JP_BankDataSchema (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_JP_BankDataSchema[")
        .append(get_ID()).append("]");
      return sb.toString();
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

	/** Set Acceptable Difference Amount.
		@param JP_AcceptableDiffAmt Acceptable Difference Amount	  */
	public void setJP_AcceptableDiffAmt (BigDecimal JP_AcceptableDiffAmt)
	{
		set_Value (COLUMNNAME_JP_AcceptableDiffAmt, JP_AcceptableDiffAmt);
	}

	/** Get Acceptable Difference Amount.
		@return Acceptable Difference Amount	  */
	public BigDecimal getJP_AcceptableDiffAmt () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_JP_AcceptableDiffAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Class of Bank Data create Doc.
		@param JP_BankDataCreateDocClass Class of Bank Data create Doc	  */
	public void setJP_BankDataCreateDocClass (String JP_BankDataCreateDocClass)
	{
		set_Value (COLUMNNAME_JP_BankDataCreateDocClass, JP_BankDataCreateDocClass);
	}

	/** Get Class of Bank Data create Doc.
		@return Class of Bank Data create Doc	  */
	public String getJP_BankDataCreateDocClass () 
	{
		return (String)get_Value(COLUMNNAME_JP_BankDataCreateDocClass);
	}

	/** Set Class of Bank Data Import.
		@param JP_BankDataImportClass Class of Bank Data Import	  */
	public void setJP_BankDataImportClass (String JP_BankDataImportClass)
	{
		set_Value (COLUMNNAME_JP_BankDataImportClass, JP_BankDataImportClass);
	}

	/** Get Class of Bank Data Import.
		@return Class of Bank Data Import	  */
	public String getJP_BankDataImportClass () 
	{
		return (String)get_Value(COLUMNNAME_JP_BankDataImportClass);
	}

	/** Set Class of Bank Data Match BP.
		@param JP_BankDataMatchBPClass Class of Bank Data Match BP	  */
	public void setJP_BankDataMatchBPClass (String JP_BankDataMatchBPClass)
	{
		set_Value (COLUMNNAME_JP_BankDataMatchBPClass, JP_BankDataMatchBPClass);
	}

	/** Get Class of Bank Data Match BP.
		@return Class of Bank Data Match BP	  */
	public String getJP_BankDataMatchBPClass () 
	{
		return (String)get_Value(COLUMNNAME_JP_BankDataMatchBPClass);
	}

	/** Set Class of Bank Data Match Bill.
		@param JP_BankDataMatchBillClass Class of Bank Data Match Bill	  */
	public void setJP_BankDataMatchBillClass (String JP_BankDataMatchBillClass)
	{
		set_Value (COLUMNNAME_JP_BankDataMatchBillClass, JP_BankDataMatchBillClass);
	}

	/** Get Class of Bank Data Match Bill.
		@return Class of Bank Data Match Bill	  */
	public String getJP_BankDataMatchBillClass () 
	{
		return (String)get_Value(COLUMNNAME_JP_BankDataMatchBillClass);
	}

	/** Set Class of Bank Data Match Invoice.
		@param JP_BankDataMatchInvClass Class of Bank Data Match Invoice	  */
	public void setJP_BankDataMatchInvClass (String JP_BankDataMatchInvClass)
	{
		set_Value (COLUMNNAME_JP_BankDataMatchInvClass, JP_BankDataMatchInvClass);
	}

	/** Get Class of Bank Data Match Invoice.
		@return Class of Bank Data Match Invoice	  */
	public String getJP_BankDataMatchInvClass () 
	{
		return (String)get_Value(COLUMNNAME_JP_BankDataMatchInvClass);
	}

	/** Set Class of Bank Data Match Payment.
		@param JP_BankDataMatchPaymentClass Class of Bank Data Match Payment	  */
	public void setJP_BankDataMatchPaymentClass (String JP_BankDataMatchPaymentClass)
	{
		set_Value (COLUMNNAME_JP_BankDataMatchPaymentClass, JP_BankDataMatchPaymentClass);
	}

	/** Get Class of Bank Data Match Payment.
		@return Class of Bank Data Match Payment	  */
	public String getJP_BankDataMatchPaymentClass () 
	{
		return (String)get_Value(COLUMNNAME_JP_BankDataMatchPaymentClass);
	}

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

	/** Set JP_BankDataSchema_UU.
		@param JP_BankDataSchema_UU JP_BankDataSchema_UU	  */
	public void setJP_BankDataSchema_UU (String JP_BankDataSchema_UU)
	{
		set_ValueNoCheck (COLUMNNAME_JP_BankDataSchema_UU, JP_BankDataSchema_UU);
	}

	/** Get JP_BankDataSchema_UU.
		@return JP_BankDataSchema_UU	  */
	public String getJP_BankDataSchema_UU () 
	{
		return (String)get_Value(COLUMNNAME_JP_BankDataSchema_UU);
	}

	/** JP_BankStmt_DocAction AD_Reference_ID=135 */
	public static final int JP_BANKSTMT_DOCACTION_AD_Reference_ID=135;
	/** Complete = CO */
	public static final String JP_BANKSTMT_DOCACTION_Complete = "CO";
	/** Approve = AP */
	public static final String JP_BANKSTMT_DOCACTION_Approve = "AP";
	/** Reject = RJ */
	public static final String JP_BANKSTMT_DOCACTION_Reject = "RJ";
	/** Post = PO */
	public static final String JP_BANKSTMT_DOCACTION_Post = "PO";
	/** Void = VO */
	public static final String JP_BANKSTMT_DOCACTION_Void = "VO";
	/** Close = CL */
	public static final String JP_BANKSTMT_DOCACTION_Close = "CL";
	/** Reverse - Correct = RC */
	public static final String JP_BANKSTMT_DOCACTION_Reverse_Correct = "RC";
	/** Reverse - Accrual = RA */
	public static final String JP_BANKSTMT_DOCACTION_Reverse_Accrual = "RA";
	/** Invalidate = IN */
	public static final String JP_BANKSTMT_DOCACTION_Invalidate = "IN";
	/** Re-activate = RE */
	public static final String JP_BANKSTMT_DOCACTION_Re_Activate = "RE";
	/** <None> = -- */
	public static final String JP_BANKSTMT_DOCACTION_None = "--";
	/** Prepare = PR */
	public static final String JP_BANKSTMT_DOCACTION_Prepare = "PR";
	/** Unlock = XL */
	public static final String JP_BANKSTMT_DOCACTION_Unlock = "XL";
	/** Wait Complete = WC */
	public static final String JP_BANKSTMT_DOCACTION_WaitComplete = "WC";
	/** Set Bank Stmt Doc Action.
		@param JP_BankStmt_DocAction Bank Stmt Doc Action	  */
	public void setJP_BankStmt_DocAction (String JP_BankStmt_DocAction)
	{

		set_Value (COLUMNNAME_JP_BankStmt_DocAction, JP_BankStmt_DocAction);
	}

	/** Get Bank Stmt Doc Action.
		@return Bank Stmt Doc Action	  */
	public String getJP_BankStmt_DocAction () 
	{
		return (String)get_Value(COLUMNNAME_JP_BankStmt_DocAction);
	}

	public org.compiere.model.I_C_DocType getJP_PaymentDocType() throws RuntimeException
    {
		return (org.compiere.model.I_C_DocType)MTable.get(getCtx(), org.compiere.model.I_C_DocType.Table_Name)
			.getPO(getJP_PaymentDocType_ID(), get_TrxName());	}

	/** Set Payment Doc Type.
		@param JP_PaymentDocType_ID Payment Doc Type	  */
	public void setJP_PaymentDocType_ID (int JP_PaymentDocType_ID)
	{
		if (JP_PaymentDocType_ID < 1) 
			set_Value (COLUMNNAME_JP_PaymentDocType_ID, null);
		else 
			set_Value (COLUMNNAME_JP_PaymentDocType_ID, Integer.valueOf(JP_PaymentDocType_ID));
	}

	/** Get Payment Doc Type.
		@return Payment Doc Type	  */
	public int getJP_PaymentDocType_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_PaymentDocType_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** JP_Payment_DocAction AD_Reference_ID=135 */
	public static final int JP_PAYMENT_DOCACTION_AD_Reference_ID=135;
	/** Complete = CO */
	public static final String JP_PAYMENT_DOCACTION_Complete = "CO";
	/** Approve = AP */
	public static final String JP_PAYMENT_DOCACTION_Approve = "AP";
	/** Reject = RJ */
	public static final String JP_PAYMENT_DOCACTION_Reject = "RJ";
	/** Post = PO */
	public static final String JP_PAYMENT_DOCACTION_Post = "PO";
	/** Void = VO */
	public static final String JP_PAYMENT_DOCACTION_Void = "VO";
	/** Close = CL */
	public static final String JP_PAYMENT_DOCACTION_Close = "CL";
	/** Reverse - Correct = RC */
	public static final String JP_PAYMENT_DOCACTION_Reverse_Correct = "RC";
	/** Reverse - Accrual = RA */
	public static final String JP_PAYMENT_DOCACTION_Reverse_Accrual = "RA";
	/** Invalidate = IN */
	public static final String JP_PAYMENT_DOCACTION_Invalidate = "IN";
	/** Re-activate = RE */
	public static final String JP_PAYMENT_DOCACTION_Re_Activate = "RE";
	/** <None> = -- */
	public static final String JP_PAYMENT_DOCACTION_None = "--";
	/** Prepare = PR */
	public static final String JP_PAYMENT_DOCACTION_Prepare = "PR";
	/** Unlock = XL */
	public static final String JP_PAYMENT_DOCACTION_Unlock = "XL";
	/** Wait Complete = WC */
	public static final String JP_PAYMENT_DOCACTION_WaitComplete = "WC";
	/** Set Payment Doc Action.
		@param JP_Payment_DocAction Payment Doc Action	  */
	public void setJP_Payment_DocAction (String JP_Payment_DocAction)
	{

		set_Value (COLUMNNAME_JP_Payment_DocAction, JP_Payment_DocAction);
	}

	/** Get Payment Doc Action.
		@return Payment Doc Action	  */
	public String getJP_Payment_DocAction () 
	{
		return (String)get_Value(COLUMNNAME_JP_Payment_DocAction);
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