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

/** Generated Model for JP_BillSchema
 *  @author iDempiere (generated) 
 *  @version Release 8.2 - $Id$ */
public class X_JP_BillSchema extends PO implements I_JP_BillSchema, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20211007L;

    /** Standard Constructor */
    public X_JP_BillSchema (Properties ctx, int JP_BillSchema_ID, String trxName)
    {
      super (ctx, JP_BillSchema_ID, trxName);
      /** if (JP_BillSchema_ID == 0)
        {
			setC_DocType_ID (0);
			setIsBillOrgJP (false);
// N
			setIsSOTrx (false);
			setIsTaxRecalculateJP (false);
// N
			setJP_BillSchema_ID (0);
			setName (null);
			setValue (null);
        } */
    }

    /** Load Constructor */
    public X_JP_BillSchema (Properties ctx, ResultSet rs, String trxName)
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
      StringBuilder sb = new StringBuilder ("X_JP_BillSchema[")
        .append(get_ID()).append(",Name=").append(getName()).append("]");
      return sb.toString();
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

	public org.compiere.model.I_C_DocType getC_DocType() throws RuntimeException
    {
		return (org.compiere.model.I_C_DocType)MTable.get(getCtx(), org.compiere.model.I_C_DocType.Table_Name)
			.getPO(getC_DocType_ID(), get_TrxName());	}

	/** Set Document Type.
		@param C_DocType_ID 
		Document type or rules
	  */
	public void setC_DocType_ID (int C_DocType_ID)
	{
		if (C_DocType_ID < 0) 
			set_Value (COLUMNNAME_C_DocType_ID, null);
		else 
			set_Value (COLUMNNAME_C_DocType_ID, Integer.valueOf(C_DocType_ID));
	}

	/** Get Document Type.
		@return Document type or rules
	  */
	public int getC_DocType_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_DocType_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Classname.
		@param Classname 
		Java Classname
	  */
	public void setClassname (String Classname)
	{
		set_Value (COLUMNNAME_Classname, Classname);
	}

	/** Get Classname.
		@return Java Classname
	  */
	public String getClassname () 
	{
		return (String)get_Value(COLUMNNAME_Classname);
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

	/** Set IsBillOrgJP.
		@param IsBillOrgJP IsBillOrgJP	  */
	public void setIsBillOrgJP (boolean IsBillOrgJP)
	{
		set_Value (COLUMNNAME_IsBillOrgJP, Boolean.valueOf(IsBillOrgJP));
	}

	/** Get IsBillOrgJP.
		@return IsBillOrgJP	  */
	public boolean isBillOrgJP () 
	{
		Object oo = get_Value(COLUMNNAME_IsBillOrgJP);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Sales Transaction.
		@param IsSOTrx 
		This is a Sales Transaction
	  */
	public void setIsSOTrx (boolean IsSOTrx)
	{
		set_Value (COLUMNNAME_IsSOTrx, Boolean.valueOf(IsSOTrx));
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

	/** Set Tax Recalculation.
		@param IsTaxRecalculateJP 
		JPIERE-0508:JPBP
	  */
	public void setIsTaxRecalculateJP (boolean IsTaxRecalculateJP)
	{
		set_Value (COLUMNNAME_IsTaxRecalculateJP, Boolean.valueOf(IsTaxRecalculateJP));
	}

	/** Get Tax Recalculation.
		@return JPIERE-0508:JPBP
	  */
	public boolean isTaxRecalculateJP () 
	{
		Object oo = get_Value(COLUMNNAME_IsTaxRecalculateJP);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set JP_BillOrg_ID.
		@param JP_BillOrg_ID JP_BillOrg_ID	  */
	public void setJP_BillOrg_ID (int JP_BillOrg_ID)
	{
		if (JP_BillOrg_ID < 1) 
			set_Value (COLUMNNAME_JP_BillOrg_ID, null);
		else 
			set_Value (COLUMNNAME_JP_BillOrg_ID, Integer.valueOf(JP_BillOrg_ID));
	}

	/** Get JP_BillOrg_ID.
		@return JP_BillOrg_ID	  */
	public int getJP_BillOrg_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_BillOrg_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Bill Schema.
		@param JP_BillSchema_ID Bill Schema	  */
	public void setJP_BillSchema_ID (int JP_BillSchema_ID)
	{
		if (JP_BillSchema_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_BillSchema_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_BillSchema_ID, Integer.valueOf(JP_BillSchema_ID));
	}

	/** Get Bill Schema.
		@return Bill Schema	  */
	public int getJP_BillSchema_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_BillSchema_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set JP_BillSchema_UU.
		@param JP_BillSchema_UU JP_BillSchema_UU	  */
	public void setJP_BillSchema_UU (String JP_BillSchema_UU)
	{
		set_ValueNoCheck (COLUMNNAME_JP_BillSchema_UU, JP_BillSchema_UU);
	}

	/** Get JP_BillSchema_UU.
		@return JP_BillSchema_UU	  */
	public String getJP_BillSchema_UU () 
	{
		return (String)get_Value(COLUMNNAME_JP_BillSchema_UU);
	}

	public org.compiere.model.I_C_Charge getJP_TaxAdjust_Charge() throws RuntimeException
    {
		return (org.compiere.model.I_C_Charge)MTable.get(getCtx(), org.compiere.model.I_C_Charge.Table_Name)
			.getPO(getJP_TaxAdjust_Charge_ID(), get_TrxName());	}

	/** Set Charge of Tax Adjust Invoice.
		@param JP_TaxAdjust_Charge_ID 
		JPIERE-0508:JPBP
	  */
	public void setJP_TaxAdjust_Charge_ID (int JP_TaxAdjust_Charge_ID)
	{
		if (JP_TaxAdjust_Charge_ID < 1) 
			set_Value (COLUMNNAME_JP_TaxAdjust_Charge_ID, null);
		else 
			set_Value (COLUMNNAME_JP_TaxAdjust_Charge_ID, Integer.valueOf(JP_TaxAdjust_Charge_ID));
	}

	/** Get Charge of Tax Adjust Invoice.
		@return JPIERE-0508:JPBP
	  */
	public int getJP_TaxAdjust_Charge_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_TaxAdjust_Charge_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Description of Tax Adjust Invoice.
		@param JP_TaxAdjust_Description 
		JPIERE-0508:JPBP
	  */
	public void setJP_TaxAdjust_Description (String JP_TaxAdjust_Description)
	{
		set_Value (COLUMNNAME_JP_TaxAdjust_Description, JP_TaxAdjust_Description);
	}

	/** Get Description of Tax Adjust Invoice.
		@return JPIERE-0508:JPBP
	  */
	public String getJP_TaxAdjust_Description () 
	{
		return (String)get_Value(COLUMNNAME_JP_TaxAdjust_Description);
	}

	public org.compiere.model.I_C_DocType getJP_TaxAdjust_DocType() throws RuntimeException
    {
		return (org.compiere.model.I_C_DocType)MTable.get(getCtx(), org.compiere.model.I_C_DocType.Table_Name)
			.getPO(getJP_TaxAdjust_DocType_ID(), get_TrxName());	}

	/** Set Doc Type of Tax Adjust Invoice.
		@param JP_TaxAdjust_DocType_ID 
		JPIERE-0508:JPBP
	  */
	public void setJP_TaxAdjust_DocType_ID (int JP_TaxAdjust_DocType_ID)
	{
		if (JP_TaxAdjust_DocType_ID < 1) 
			set_Value (COLUMNNAME_JP_TaxAdjust_DocType_ID, null);
		else 
			set_Value (COLUMNNAME_JP_TaxAdjust_DocType_ID, Integer.valueOf(JP_TaxAdjust_DocType_ID));
	}

	/** Get Doc Type of Tax Adjust Invoice.
		@return JPIERE-0508:JPBP
	  */
	public int getJP_TaxAdjust_DocType_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_TaxAdjust_DocType_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_PriceList getJP_TaxAdjust_PriceList() throws RuntimeException
    {
		return (org.compiere.model.I_M_PriceList)MTable.get(getCtx(), org.compiere.model.I_M_PriceList.Table_Name)
			.getPO(getJP_TaxAdjust_PriceList_ID(), get_TrxName());	}

	/** Set Price List of Tax Adjust Invoice.
		@param JP_TaxAdjust_PriceList_ID 
		JPIERE-0508:JPBP
	  */
	public void setJP_TaxAdjust_PriceList_ID (int JP_TaxAdjust_PriceList_ID)
	{
		if (JP_TaxAdjust_PriceList_ID < 1) 
			set_Value (COLUMNNAME_JP_TaxAdjust_PriceList_ID, null);
		else 
			set_Value (COLUMNNAME_JP_TaxAdjust_PriceList_ID, Integer.valueOf(JP_TaxAdjust_PriceList_ID));
	}

	/** Get Price List of Tax Adjust Invoice.
		@return JPIERE-0508:JPBP
	  */
	public int getJP_TaxAdjust_PriceList_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_TaxAdjust_PriceList_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Tax getJP_TaxAdjust_Tax() throws RuntimeException
    {
		return (org.compiere.model.I_C_Tax)MTable.get(getCtx(), org.compiere.model.I_C_Tax.Table_Name)
			.getPO(getJP_TaxAdjust_Tax_ID(), get_TrxName());	}

	/** Set Tax of Tax Adjust Invoice.
		@param JP_TaxAdjust_Tax_ID 
		JPIERE-0508:JPBP
	  */
	public void setJP_TaxAdjust_Tax_ID (int JP_TaxAdjust_Tax_ID)
	{
		if (JP_TaxAdjust_Tax_ID < 1) 
			set_Value (COLUMNNAME_JP_TaxAdjust_Tax_ID, null);
		else 
			set_Value (COLUMNNAME_JP_TaxAdjust_Tax_ID, Integer.valueOf(JP_TaxAdjust_Tax_ID));
	}

	/** Get Tax of Tax Adjust Invoice.
		@return JPIERE-0508:JPBP
	  */
	public int getJP_TaxAdjust_Tax_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_TaxAdjust_Tax_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
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