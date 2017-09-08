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

/** Generated Model for JP_Contract_Acct
 *  @author iDempiere (generated) 
 *  @version Release 4.1 - $Id$ */
public class X_JP_Contract_Acct extends PO implements I_JP_Contract_Acct, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20170908L;

    /** Standard Constructor */
    public X_JP_Contract_Acct (Properties ctx, int JP_Contract_Acct_ID, String trxName)
    {
      super (ctx, JP_Contract_Acct_ID, trxName);
      /** if (JP_Contract_Acct_ID == 0)
        {
			setIsOrderInfoMandatoryJP (false);
// N
			setIsPostingContractAcctJP (false);
// N
			setIsPostingRecognitionDocJP (false);
// N
			setIsSOTrx (true);
// Y
			setJP_Contract_Acct_ID (0);
			setName (null);
			setValue (null);
        } */
    }

    /** Load Constructor */
    public X_JP_Contract_Acct (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_JP_Contract_Acct[")
        .append(get_ID()).append("]");
      return sb.toString();
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

	/** Set Order Info Mandatory.
		@param IsOrderInfoMandatoryJP Order Info Mandatory	  */
	public void setIsOrderInfoMandatoryJP (boolean IsOrderInfoMandatoryJP)
	{
		set_Value (COLUMNNAME_IsOrderInfoMandatoryJP, Boolean.valueOf(IsOrderInfoMandatoryJP));
	}

	/** Get Order Info Mandatory.
		@return Order Info Mandatory	  */
	public boolean isOrderInfoMandatoryJP () 
	{
		Object oo = get_Value(COLUMNNAME_IsOrderInfoMandatoryJP);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Posting by Contract Acct.
		@param IsPostingContractAcctJP Posting by Contract Acct	  */
	public void setIsPostingContractAcctJP (boolean IsPostingContractAcctJP)
	{
		set_Value (COLUMNNAME_IsPostingContractAcctJP, Boolean.valueOf(IsPostingContractAcctJP));
	}

	/** Get Posting by Contract Acct.
		@return Posting by Contract Acct	  */
	public boolean isPostingContractAcctJP () 
	{
		Object oo = get_Value(COLUMNNAME_IsPostingContractAcctJP);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Posting at Recognition Doc.
		@param IsPostingRecognitionDocJP Posting at Recognition Doc	  */
	public void setIsPostingRecognitionDocJP (boolean IsPostingRecognitionDocJP)
	{
		set_Value (COLUMNNAME_IsPostingRecognitionDocJP, Boolean.valueOf(IsPostingRecognitionDocJP));
	}

	/** Get Posting at Recognition Doc.
		@return Posting at Recognition Doc	  */
	public boolean isPostingRecognitionDocJP () 
	{
		Object oo = get_Value(COLUMNNAME_IsPostingRecognitionDocJP);
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

	/** Set Contract Acct Info(UU).
		@param JP_Contract_Acct_UU Contract Acct Info(UU)	  */
	public void setJP_Contract_Acct_UU (String JP_Contract_Acct_UU)
	{
		set_ValueNoCheck (COLUMNNAME_JP_Contract_Acct_UU, JP_Contract_Acct_UU);
	}

	/** Get Contract Acct Info(UU).
		@return Contract Acct Info(UU)	  */
	public String getJP_Contract_Acct_UU () 
	{
		return (String)get_Value(COLUMNNAME_JP_Contract_Acct_UU);
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