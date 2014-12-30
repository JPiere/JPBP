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

/** Generated Model for JP_BankStatementTax
 *  @author iDempiere (generated) 
 *  @version Release 2.1 - $Id$ */
public class X_JP_BankStatementTax extends PO implements I_JP_BankStatementTax, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20141230L;

    /** Standard Constructor */
    public X_JP_BankStatementTax (Properties ctx, int JP_BankStatementTax_ID, String trxName)
    {
      super (ctx, JP_BankStatementTax_ID, trxName);
      /** if (JP_BankStatementTax_ID == 0)
        {
			setC_BankStatementLine_ID (0);
			setC_BankStatement_ID (0);
			setC_Tax_ID (0);
			setIsSOTrx (true);
// Y
			setIsTaxIncluded (true);
// Y
			setProcessed (false);
// N
			setTaxAmt (Env.ZERO);
// 0
			setTaxBaseAmt (Env.ZERO);
// 0
        } */
    }

    /** Load Constructor */
    public X_JP_BankStatementTax (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_JP_BankStatementTax[")
        .append(get_ID()).append("]");
      return sb.toString();
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
			set_ValueNoCheck (COLUMNNAME_C_BankStatement_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_BankStatement_ID, Integer.valueOf(C_BankStatement_ID));
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

	public org.compiere.model.I_C_TaxProvider getC_TaxProvider() throws RuntimeException
    {
		return (org.compiere.model.I_C_TaxProvider)MTable.get(getCtx(), org.compiere.model.I_C_TaxProvider.Table_Name)
			.getPO(getC_TaxProvider_ID(), get_TrxName());	}

	/** Set Tax Provider.
		@param C_TaxProvider_ID Tax Provider	  */
	public void setC_TaxProvider_ID (int C_TaxProvider_ID)
	{
		if (C_TaxProvider_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_C_TaxProvider_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_TaxProvider_ID, Integer.valueOf(C_TaxProvider_ID));
	}

	/** Get Tax Provider.
		@return Tax Provider	  */
	public int getC_TaxProvider_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_TaxProvider_ID);
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
			set_ValueNoCheck (COLUMNNAME_C_Tax_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_Tax_ID, Integer.valueOf(C_Tax_ID));
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

	/** Set Sales Transaction.
		@param IsSOTrx 
		This is a Sales Transaction
	  */
	public void setIsSOTrx (boolean IsSOTrx)
	{
		set_ValueNoCheck (COLUMNNAME_IsSOTrx, Boolean.valueOf(IsSOTrx));
	}

	/** Get Sales Transaction.
		@return This is a Sales Transaction
	  */
	public boolean isSOTrx () 
	{
		Object oo = get_Value(COLUMNNAME_IsSOTrx);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Price includes Tax.
		@param IsTaxIncluded 
		Tax is included in the price 
	  */
	public void setIsTaxIncluded (boolean IsTaxIncluded)
	{
		set_ValueNoCheck (COLUMNNAME_IsTaxIncluded, Boolean.valueOf(IsTaxIncluded));
	}

	/** Get Price includes Tax.
		@return Tax is included in the price 
	  */
	public boolean isTaxIncluded () 
	{
		Object oo = get_Value(COLUMNNAME_IsTaxIncluded);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set JP_BankStatementTax_UU.
		@param JP_BankStatementTax_UU JP_BankStatementTax_UU	  */
	public void setJP_BankStatementTax_UU (String JP_BankStatementTax_UU)
	{
		set_ValueNoCheck (COLUMNNAME_JP_BankStatementTax_UU, JP_BankStatementTax_UU);
	}

	/** Get JP_BankStatementTax_UU.
		@return JP_BankStatementTax_UU	  */
	public String getJP_BankStatementTax_UU () 
	{
		return (String)get_Value(COLUMNNAME_JP_BankStatementTax_UU);
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

	/** Set Tax Amount.
		@param TaxAmt 
		Tax Amount for a document
	  */
	public void setTaxAmt (BigDecimal TaxAmt)
	{
		set_Value (COLUMNNAME_TaxAmt, TaxAmt);
	}

	/** Get Tax Amount.
		@return Tax Amount for a document
	  */
	public BigDecimal getTaxAmt () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_TaxAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Tax base Amount.
		@param TaxBaseAmt 
		Base for calculating the tax amount
	  */
	public void setTaxBaseAmt (BigDecimal TaxBaseAmt)
	{
		set_Value (COLUMNNAME_TaxBaseAmt, TaxBaseAmt);
	}

	/** Get Tax base Amount.
		@return Base for calculating the tax amount
	  */
	public BigDecimal getTaxBaseAmt () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_TaxBaseAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}
}