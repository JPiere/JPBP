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
import java.sql.Timestamp;
import java.util.Properties;
import org.compiere.model.*;

/** Generated Model for JP_ContractCalenderList
 *  @author iDempiere (generated) 
 *  @version Release 4.1 - $Id$ */
public class X_JP_ContractCalenderList extends PO implements I_JP_ContractCalenderList, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20170901L;

    /** Standard Constructor */
    public X_JP_ContractCalenderList (Properties ctx, int JP_ContractCalenderList_ID, String trxName)
    {
      super (ctx, JP_ContractCalenderList_ID, trxName);
      /** if (JP_ContractCalenderList_ID == 0)
        {
			setJP_ContractCalenderList_ID (0);
			setJP_ContractCalenderRef_ID (0);
			setJP_ContractCalender_ID (0);
        } */
    }

    /** Load Constructor */
    public X_JP_ContractCalenderList (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_JP_ContractCalenderList[")
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

	/** Set Contract Calender List.
		@param JP_ContractCalenderList_ID Contract Calender List	  */
	public void setJP_ContractCalenderList_ID (int JP_ContractCalenderList_ID)
	{
		if (JP_ContractCalenderList_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_ContractCalenderList_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_ContractCalenderList_ID, Integer.valueOf(JP_ContractCalenderList_ID));
	}

	/** Get Contract Calender List.
		@return Contract Calender List	  */
	public int getJP_ContractCalenderList_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ContractCalenderList_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Contract Calender List(UU).
		@param JP_ContractCalenderList_UU Contract Calender List(UU)	  */
	public void setJP_ContractCalenderList_UU (String JP_ContractCalenderList_UU)
	{
		set_ValueNoCheck (COLUMNNAME_JP_ContractCalenderList_UU, JP_ContractCalenderList_UU);
	}

	/** Get Contract Calender List(UU).
		@return Contract Calender List(UU)	  */
	public String getJP_ContractCalenderList_UU () 
	{
		return (String)get_Value(COLUMNNAME_JP_ContractCalenderList_UU);
	}

	public I_JP_ContractCalenderRef getJP_ContractCalenderRef() throws RuntimeException
    {
		return (I_JP_ContractCalenderRef)MTable.get(getCtx(), I_JP_ContractCalenderRef.Table_Name)
			.getPO(getJP_ContractCalenderRef_ID(), get_TrxName());	}

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

	public I_JP_ContractCalender getJP_ContractCalender() throws RuntimeException
    {
		return (I_JP_ContractCalender)MTable.get(getCtx(), I_JP_ContractCalender.Table_Name)
			.getPO(getJP_ContractCalender_ID(), get_TrxName());	}

	/** Set Contract Calender.
		@param JP_ContractCalender_ID Contract Calender	  */
	public void setJP_ContractCalender_ID (int JP_ContractCalender_ID)
	{
		if (JP_ContractCalender_ID < 1) 
			set_Value (COLUMNNAME_JP_ContractCalender_ID, null);
		else 
			set_Value (COLUMNNAME_JP_ContractCalender_ID, Integer.valueOf(JP_ContractCalender_ID));
	}

	/** Get Contract Calender.
		@return Contract Calender	  */
	public int getJP_ContractCalender_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ContractCalender_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Valid from.
		@param ValidFrom 
		Valid from including this date (first day)
	  */
	public void setValidFrom (Timestamp ValidFrom)
	{
		set_Value (COLUMNNAME_ValidFrom, ValidFrom);
	}

	/** Get Valid from.
		@return Valid from including this date (first day)
	  */
	public Timestamp getValidFrom () 
	{
		return (Timestamp)get_Value(COLUMNNAME_ValidFrom);
	}

	/** Set Valid to.
		@param ValidTo 
		Valid to including this date (last day)
	  */
	public void setValidTo (Timestamp ValidTo)
	{
		set_Value (COLUMNNAME_ValidTo, ValidTo);
	}

	/** Get Valid to.
		@return Valid to including this date (last day)
	  */
	public Timestamp getValidTo () 
	{
		return (Timestamp)get_Value(COLUMNNAME_ValidTo);
	}
}