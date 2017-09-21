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

/** Generated Model for JP_ContractExtendPeriod
 *  @author iDempiere (generated) 
 *  @version Release 4.1 - $Id$ */
public class X_JP_ContractExtendPeriod extends PO implements I_JP_ContractExtendPeriod, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20170921L;

    /** Standard Constructor */
    public X_JP_ContractExtendPeriod (Properties ctx, int JP_ContractExtendPeriod_ID, String trxName)
    {
      super (ctx, JP_ContractExtendPeriod_ID, trxName);
      /** if (JP_ContractExtendPeriod_ID == 0)
        {
			setIsDueFixed (false);
// N
			setJP_ContractExtendPeriod_ID (0);
			setJP_Day (0);
// 0
			setJP_Month (0);
// 0
			setJP_Year (0);
// 0
			setName (null);
			setValue (null);
        } */
    }

    /** Load Constructor */
    public X_JP_ContractExtendPeriod (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_JP_ContractExtendPeriod[")
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

	/** Set Fixed due date.
		@param IsDueFixed 
		Payment is due on a fixed date
	  */
	public void setIsDueFixed (boolean IsDueFixed)
	{
		set_Value (COLUMNNAME_IsDueFixed, Boolean.valueOf(IsDueFixed));
	}

	/** Get Fixed due date.
		@return Payment is due on a fixed date
	  */
	public boolean isDueFixed () 
	{
		Object oo = get_Value(COLUMNNAME_IsDueFixed);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Contract Extend Period.
		@param JP_ContractExtendPeriod_ID Contract Extend Period	  */
	public void setJP_ContractExtendPeriod_ID (int JP_ContractExtendPeriod_ID)
	{
		if (JP_ContractExtendPeriod_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_ContractExtendPeriod_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_ContractExtendPeriod_ID, Integer.valueOf(JP_ContractExtendPeriod_ID));
	}

	/** Get Contract Extend Period.
		@return Contract Extend Period	  */
	public int getJP_ContractExtendPeriod_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ContractExtendPeriod_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Contract Extend Period(UU).
		@param JP_ContractExtendPeriod_UU Contract Extend Period(UU)	  */
	public void setJP_ContractExtendPeriod_UU (String JP_ContractExtendPeriod_UU)
	{
		set_ValueNoCheck (COLUMNNAME_JP_ContractExtendPeriod_UU, JP_ContractExtendPeriod_UU);
	}

	/** Get Contract Extend Period(UU).
		@return Contract Extend Period(UU)	  */
	public String getJP_ContractExtendPeriod_UU () 
	{
		return (String)get_Value(COLUMNNAME_JP_ContractExtendPeriod_UU);
	}

	/** Set Day.
		@param JP_Day Day	  */
	public void setJP_Day (int JP_Day)
	{
		set_Value (COLUMNNAME_JP_Day, Integer.valueOf(JP_Day));
	}

	/** Get Day.
		@return Day	  */
	public int getJP_Day () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Day);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Month.
		@param JP_Month Month	  */
	public void setJP_Month (int JP_Month)
	{
		set_Value (COLUMNNAME_JP_Month, Integer.valueOf(JP_Month));
	}

	/** Get Month.
		@return Month	  */
	public int getJP_Month () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Month);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Year.
		@param JP_Year Year	  */
	public void setJP_Year (int JP_Year)
	{
		set_Value (COLUMNNAME_JP_Year, Integer.valueOf(JP_Year));
	}

	/** Get Year.
		@return Year	  */
	public int getJP_Year () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Year);
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