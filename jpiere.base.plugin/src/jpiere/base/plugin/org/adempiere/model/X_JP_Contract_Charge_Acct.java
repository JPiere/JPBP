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

/** Generated Model for JP_Contract_Charge_Acct
 *  @author iDempiere (generated) 
 *  @version Release 4.1 - $Id$ */
public class X_JP_Contract_Charge_Acct extends PO implements I_JP_Contract_Charge_Acct, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20170923L;

    /** Standard Constructor */
    public X_JP_Contract_Charge_Acct (Properties ctx, int JP_Contract_Charge_Acct_ID, String trxName)
    {
      super (ctx, JP_Contract_Charge_Acct_ID, trxName);
      /** if (JP_Contract_Charge_Acct_ID == 0)
        {
			setC_AcctSchema_ID (0);
			setC_Charge_ID (0);
			setJP_Contract_Acct_ID (0);
			setJP_Contract_Charge_Acct_ID (0);
        } */
    }

    /** Load Constructor */
    public X_JP_Contract_Charge_Acct (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_JP_Contract_Charge_Acct[")
        .append(get_ID()).append("]");
      return sb.toString();
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
			set_ValueNoCheck (COLUMNNAME_C_AcctSchema_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_AcctSchema_ID, Integer.valueOf(C_AcctSchema_ID));
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
			set_ValueNoCheck (COLUMNNAME_C_Charge_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_Charge_ID, Integer.valueOf(C_Charge_ID));
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

	public I_C_ValidCombination getCh_Expense_A() throws RuntimeException
    {
		return (I_C_ValidCombination)MTable.get(getCtx(), I_C_ValidCombination.Table_Name)
			.getPO(getCh_Expense_Acct(), get_TrxName());	}

	/** Set Charge Account.
		@param Ch_Expense_Acct 
		Charge Account
	  */
	public void setCh_Expense_Acct (int Ch_Expense_Acct)
	{
		set_Value (COLUMNNAME_Ch_Expense_Acct, Integer.valueOf(Ch_Expense_Acct));
	}

	/** Get Charge Account.
		@return Charge Account
	  */
	public int getCh_Expense_Acct () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Ch_Expense_Acct);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_ValidCombination getJP_Ch_Expense_A() throws RuntimeException
    {
		return (I_C_ValidCombination)MTable.get(getCtx(), I_C_ValidCombination.Table_Name)
			.getPO(getJP_Ch_Expense_Acct(), get_TrxName());	}

	/** Set Charge(Recognition Doc).
		@param JP_Ch_Expense_Acct Charge(Recognition Doc)	  */
	public void setJP_Ch_Expense_Acct (int JP_Ch_Expense_Acct)
	{
		set_Value (COLUMNNAME_JP_Ch_Expense_Acct, Integer.valueOf(JP_Ch_Expense_Acct));
	}

	/** Get Charge(Recognition Doc).
		@return Charge(Recognition Doc)	  */
	public int getJP_Ch_Expense_Acct () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Ch_Expense_Acct);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_JP_Contract_Acct getJP_Contract_Acct() throws RuntimeException
    {
		return (I_JP_Contract_Acct)MTable.get(getCtx(), I_JP_Contract_Acct.Table_Name)
			.getPO(getJP_Contract_Acct_ID(), get_TrxName());	}

	/** Set Contract Acct Info.
		@param JP_Contract_Acct_ID Contract Acct Info	  */
	public void setJP_Contract_Acct_ID (int JP_Contract_Acct_ID)
	{
		if (JP_Contract_Acct_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_Contract_Acct_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_Contract_Acct_ID, Integer.valueOf(JP_Contract_Acct_ID));
	}

	/** Get Contract Acct Info.
		@return Contract Acct Info	  */
	public int getJP_Contract_Acct_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Contract_Acct_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set JP_Contract_Charge_Acct.
		@param JP_Contract_Charge_Acct_ID JP_Contract_Charge_Acct	  */
	public void setJP_Contract_Charge_Acct_ID (int JP_Contract_Charge_Acct_ID)
	{
		if (JP_Contract_Charge_Acct_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_Contract_Charge_Acct_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_Contract_Charge_Acct_ID, Integer.valueOf(JP_Contract_Charge_Acct_ID));
	}

	/** Get JP_Contract_Charge_Acct.
		@return JP_Contract_Charge_Acct	  */
	public int getJP_Contract_Charge_Acct_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Contract_Charge_Acct_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set JP_Contract_Charge_Acct_UU.
		@param JP_Contract_Charge_Acct_UU JP_Contract_Charge_Acct_UU	  */
	public void setJP_Contract_Charge_Acct_UU (String JP_Contract_Charge_Acct_UU)
	{
		set_ValueNoCheck (COLUMNNAME_JP_Contract_Charge_Acct_UU, JP_Contract_Charge_Acct_UU);
	}

	/** Get JP_Contract_Charge_Acct_UU.
		@return JP_Contract_Charge_Acct_UU	  */
	public String getJP_Contract_Charge_Acct_UU () 
	{
		return (String)get_Value(COLUMNNAME_JP_Contract_Charge_Acct_UU);
	}
}