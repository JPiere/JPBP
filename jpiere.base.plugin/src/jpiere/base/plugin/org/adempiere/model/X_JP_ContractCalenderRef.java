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

/** Generated Model for JP_ContractCalenderRef
 *  @author iDempiere (generated) 
 *  @version Release 4.1 - $Id$ */
public class X_JP_ContractCalenderRef extends PO implements I_JP_ContractCalenderRef, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20170929L;

    /** Standard Constructor */
    public X_JP_ContractCalenderRef (Properties ctx, int JP_ContractCalenderRef_ID, String trxName)
    {
      super (ctx, JP_ContractCalenderRef_ID, trxName);
      /** if (JP_ContractCalenderRef_ID == 0)
        {
			setJP_ContractCalenderRef_ID (0);
			setName (null);
			setValue (null);
        } */
    }

    /** Load Constructor */
    public X_JP_ContractCalenderRef (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_JP_ContractCalenderRef[")
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

	/** Set Contract Calender Reference.
		@param JP_ContractCalenderRef_ID Contract Calender Reference	  */
	public void setJP_ContractCalenderRef_ID (int JP_ContractCalenderRef_ID)
	{
		if (JP_ContractCalenderRef_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_ContractCalenderRef_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_ContractCalenderRef_ID, Integer.valueOf(JP_ContractCalenderRef_ID));
	}

	/** Get Contract Calender Reference.
		@return Contract Calender Reference	  */
	public int getJP_ContractCalenderRef_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ContractCalenderRef_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Contract Calender Ref(UU).
		@param JP_ContractCalenderRef_UU Contract Calender Ref(UU)	  */
	public void setJP_ContractCalenderRef_UU (String JP_ContractCalenderRef_UU)
	{
		set_ValueNoCheck (COLUMNNAME_JP_ContractCalenderRef_UU, JP_ContractCalenderRef_UU);
	}

	/** Get Contract Calender Ref(UU).
		@return Contract Calender Ref(UU)	  */
	public String getJP_ContractCalenderRef_UU () 
	{
		return (String)get_Value(COLUMNNAME_JP_ContractCalenderRef_UU);
	}

	public I_JP_ContractCategory getJP_ContractCategory() throws RuntimeException
    {
		return (I_JP_ContractCategory)MTable.get(getCtx(), I_JP_ContractCategory.Table_Name)
			.getPO(getJP_ContractCategory_ID(), get_TrxName());	}

	/** Set Contract Category.
		@param JP_ContractCategory_ID Contract Category	  */
	public void setJP_ContractCategory_ID (int JP_ContractCategory_ID)
	{
		if (JP_ContractCategory_ID < 1) 
			set_Value (COLUMNNAME_JP_ContractCategory_ID, null);
		else 
			set_Value (COLUMNNAME_JP_ContractCategory_ID, Integer.valueOf(JP_ContractCategory_ID));
	}

	/** Get Contract Category.
		@return Contract Category	  */
	public int getJP_ContractCategory_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ContractCategory_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_JP_ContractContentT getJP_ContractContentT() throws RuntimeException
    {
		return (I_JP_ContractContentT)MTable.get(getCtx(), I_JP_ContractContentT.Table_Name)
			.getPO(getJP_ContractContentT_ID(), get_TrxName());	}

	/** Set Contract Content Template.
		@param JP_ContractContentT_ID Contract Content Template	  */
	public void setJP_ContractContentT_ID (int JP_ContractContentT_ID)
	{
		if (JP_ContractContentT_ID < 1) 
			set_Value (COLUMNNAME_JP_ContractContentT_ID, null);
		else 
			set_Value (COLUMNNAME_JP_ContractContentT_ID, Integer.valueOf(JP_ContractContentT_ID));
	}

	/** Get Contract Content Template.
		@return Contract Content Template	  */
	public int getJP_ContractContentT_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ContractContentT_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_JP_ContractLineT getJP_ContractLineT() throws RuntimeException
    {
		return (I_JP_ContractLineT)MTable.get(getCtx(), I_JP_ContractLineT.Table_Name)
			.getPO(getJP_ContractLineT_ID(), get_TrxName());	}

	/** Set Contract Content Line Template.
		@param JP_ContractLineT_ID Contract Content Line Template	  */
	public void setJP_ContractLineT_ID (int JP_ContractLineT_ID)
	{
		if (JP_ContractLineT_ID < 1) 
			set_Value (COLUMNNAME_JP_ContractLineT_ID, null);
		else 
			set_Value (COLUMNNAME_JP_ContractLineT_ID, Integer.valueOf(JP_ContractLineT_ID));
	}

	/** Get Contract Content Line Template.
		@return Contract Content Line Template	  */
	public int getJP_ContractLineT_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ContractLineT_ID);
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