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

/** Generated Model for JP_BillTax
 *  @author iDempiere (generated) 
 *  @version Release 8.2 - $Id$ */
public class X_JP_BillTax extends PO implements I_JP_BillTax, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20211007L;

    /** Standard Constructor */
    public X_JP_BillTax (Properties ctx, int JP_BillTax_ID, String trxName)
    {
      super (ctx, JP_BillTax_ID, trxName);
      /** if (JP_BillTax_ID == 0)
        {
			setC_Tax_ID (0);
			setIsDocumentLevel (false);
// N
			setIsTaxIncluded (false);
			setJP_Bill_ID (0);
			setJP_RecalculatedTaxAmt (Env.ZERO);
// 0
			setJP_RecalculatedTaxBaseAmt (Env.ZERO);
// 0
			setJP_TaxAdjust_TaxAmt (Env.ZERO);
// 0
			setProcessed (false);
			setTaxAmt (Env.ZERO);
			setTaxBaseAmt (Env.ZERO);
        } */
    }

    /** Load Constructor */
    public X_JP_BillTax (Properties ctx, ResultSet rs, String trxName)
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
      StringBuilder sb = new StringBuilder ("X_JP_BillTax[")
        .append(get_ID()).append("]");
      return sb.toString();
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

	/** Set Document Level.
		@param IsDocumentLevel 
		Tax is calculated on document level (rather than line by line)
	  */
	public void setIsDocumentLevel (boolean IsDocumentLevel)
	{
		set_ValueNoCheck (COLUMNNAME_IsDocumentLevel, Boolean.valueOf(IsDocumentLevel));
	}

	/** Get Document Level.
		@return Tax is calculated on document level (rather than line by line)
	  */
	public boolean isDocumentLevel () 
	{
		Object oo = get_Value(COLUMNNAME_IsDocumentLevel);
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
		set_Value (COLUMNNAME_IsTaxIncluded, Boolean.valueOf(IsTaxIncluded));
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

	/** Set JP_BillTax_UU.
		@param JP_BillTax_UU JP_BillTax_UU	  */
	public void setJP_BillTax_UU (String JP_BillTax_UU)
	{
		set_Value (COLUMNNAME_JP_BillTax_UU, JP_BillTax_UU);
	}

	/** Get JP_BillTax_UU.
		@return JP_BillTax_UU	  */
	public String getJP_BillTax_UU () 
	{
		return (String)get_Value(COLUMNNAME_JP_BillTax_UU);
	}

	public I_JP_Bill getJP_Bill() throws RuntimeException
    {
		return (I_JP_Bill)MTable.get(getCtx(), I_JP_Bill.Table_Name)
			.getPO(getJP_Bill_ID(), get_TrxName());	}

	/** Set Bill.
		@param JP_Bill_ID Bill	  */
	public void setJP_Bill_ID (int JP_Bill_ID)
	{
		if (JP_Bill_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_Bill_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_Bill_ID, Integer.valueOf(JP_Bill_ID));
	}

	/** Get Bill.
		@return Bill	  */
	public int getJP_Bill_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Bill_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Recalculated Tax Amount.
		@param JP_RecalculatedTaxAmt 
		JPIERE-0508:JPBP
	  */
	public void setJP_RecalculatedTaxAmt (BigDecimal JP_RecalculatedTaxAmt)
	{
		set_Value (COLUMNNAME_JP_RecalculatedTaxAmt, JP_RecalculatedTaxAmt);
	}

	/** Get Recalculated Tax Amount.
		@return JPIERE-0508:JPBP
	  */
	public BigDecimal getJP_RecalculatedTaxAmt () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_JP_RecalculatedTaxAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Recalculated Tax base Amount.
		@param JP_RecalculatedTaxBaseAmt 
		JPIERE-0508:JPBP
	  */
	public void setJP_RecalculatedTaxBaseAmt (BigDecimal JP_RecalculatedTaxBaseAmt)
	{
		set_Value (COLUMNNAME_JP_RecalculatedTaxBaseAmt, JP_RecalculatedTaxBaseAmt);
	}

	/** Get Recalculated Tax base Amount.
		@return JPIERE-0508:JPBP
	  */
	public BigDecimal getJP_RecalculatedTaxBaseAmt () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_JP_RecalculatedTaxBaseAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	public org.compiere.model.I_C_InvoiceLine getJP_TaxAdjust_InvoiceLine() throws RuntimeException
    {
		return (org.compiere.model.I_C_InvoiceLine)MTable.get(getCtx(), org.compiere.model.I_C_InvoiceLine.Table_Name)
			.getPO(getJP_TaxAdjust_InvoiceLine_ID(), get_TrxName());	}

	/** Set Invoice Line of Tax Adjust.
		@param JP_TaxAdjust_InvoiceLine_ID 
		JPIERE-0508:JPBP
	  */
	public void setJP_TaxAdjust_InvoiceLine_ID (int JP_TaxAdjust_InvoiceLine_ID)
	{
		if (JP_TaxAdjust_InvoiceLine_ID < 1) 
			set_Value (COLUMNNAME_JP_TaxAdjust_InvoiceLine_ID, null);
		else 
			set_Value (COLUMNNAME_JP_TaxAdjust_InvoiceLine_ID, Integer.valueOf(JP_TaxAdjust_InvoiceLine_ID));
	}

	/** Get Invoice Line of Tax Adjust.
		@return JPIERE-0508:JPBP
	  */
	public int getJP_TaxAdjust_InvoiceLine_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_TaxAdjust_InvoiceLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Tax Amount of Tax Adjust.
		@param JP_TaxAdjust_TaxAmt 
		JPIERE-0508:JPBP
	  */
	public void setJP_TaxAdjust_TaxAmt (BigDecimal JP_TaxAdjust_TaxAmt)
	{
		set_Value (COLUMNNAME_JP_TaxAdjust_TaxAmt, JP_TaxAdjust_TaxAmt);
	}

	/** Get Tax Amount of Tax Adjust.
		@return JPIERE-0508:JPBP
	  */
	public BigDecimal getJP_TaxAdjust_TaxAmt () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_JP_TaxAdjust_TaxAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
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
		set_ValueNoCheck (COLUMNNAME_TaxAmt, TaxAmt);
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
		set_ValueNoCheck (COLUMNNAME_TaxBaseAmt, TaxBaseAmt);
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