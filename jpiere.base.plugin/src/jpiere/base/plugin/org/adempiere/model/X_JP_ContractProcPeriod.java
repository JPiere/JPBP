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

/** Generated Model for JP_ContractProcPeriod
 *  @author iDempiere (generated) 
 *  @version Release 4.1 - $Id$ */
public class X_JP_ContractProcPeriod extends PO implements I_JP_ContractProcPeriod, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20170902L;

    /** Standard Constructor */
    public X_JP_ContractProcPeriod (Properties ctx, int JP_ContractProcPeriod_ID, String trxName)
    {
      super (ctx, JP_ContractProcPeriod_ID, trxName);
      /** if (JP_ContractProcPeriod_ID == 0)
        {
			setDateAcct (new Timestamp( System.currentTimeMillis() ));
			setDateDoc (new Timestamp( System.currentTimeMillis() ));
			setEndDate (new Timestamp( System.currentTimeMillis() ));
			setJP_ContractCalender_ID (0);
// @JP_ContractCalender_ID@
			setJP_ContractProcPeriodG_ID (0);
// @JP_ContractProcPeriodG_ID@
			setJP_ContractProcPeriod_ID (0);
			setName (null);
			setStartDate (new Timestamp( System.currentTimeMillis() ));
        } */
    }

    /** Load Constructor */
    public X_JP_ContractProcPeriod (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_JP_ContractProcPeriod[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set Account Date.
		@param DateAcct 
		Accounting Date
	  */
	public void setDateAcct (Timestamp DateAcct)
	{
		set_Value (COLUMNNAME_DateAcct, DateAcct);
	}

	/** Get Account Date.
		@return Accounting Date
	  */
	public Timestamp getDateAcct () 
	{
		return (Timestamp)get_Value(COLUMNNAME_DateAcct);
	}

	/** Set Document Date.
		@param DateDoc 
		Date of the Document
	  */
	public void setDateDoc (Timestamp DateDoc)
	{
		set_Value (COLUMNNAME_DateDoc, DateDoc);
	}

	/** Get Document Date.
		@return Date of the Document
	  */
	public Timestamp getDateDoc () 
	{
		return (Timestamp)get_Value(COLUMNNAME_DateDoc);
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

	/** Set End Date.
		@param EndDate 
		Last effective date (inclusive)
	  */
	public void setEndDate (Timestamp EndDate)
	{
		set_Value (COLUMNNAME_EndDate, EndDate);
	}

	/** Get End Date.
		@return Last effective date (inclusive)
	  */
	public Timestamp getEndDate () 
	{
		return (Timestamp)get_Value(COLUMNNAME_EndDate);
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
			set_ValueNoCheck (COLUMNNAME_JP_ContractCalender_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_ContractCalender_ID, Integer.valueOf(JP_ContractCalender_ID));
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

	public I_JP_ContractProcPeriodG getJP_ContractProcPeriodG() throws RuntimeException
    {
		return (I_JP_ContractProcPeriodG)MTable.get(getCtx(), I_JP_ContractProcPeriodG.Table_Name)
			.getPO(getJP_ContractProcPeriodG_ID(), get_TrxName());	}

	/** Set Contract Process Period Group.
		@param JP_ContractProcPeriodG_ID Contract Process Period Group	  */
	public void setJP_ContractProcPeriodG_ID (int JP_ContractProcPeriodG_ID)
	{
		if (JP_ContractProcPeriodG_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_ContractProcPeriodG_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_ContractProcPeriodG_ID, Integer.valueOf(JP_ContractProcPeriodG_ID));
	}

	/** Get Contract Process Period Group.
		@return Contract Process Period Group	  */
	public int getJP_ContractProcPeriodG_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ContractProcPeriodG_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Contract Process Period.
		@param JP_ContractProcPeriod_ID Contract Process Period	  */
	public void setJP_ContractProcPeriod_ID (int JP_ContractProcPeriod_ID)
	{
		if (JP_ContractProcPeriod_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_ContractProcPeriod_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_ContractProcPeriod_ID, Integer.valueOf(JP_ContractProcPeriod_ID));
	}

	/** Get Contract Process Period.
		@return Contract Process Period	  */
	public int getJP_ContractProcPeriod_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ContractProcPeriod_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Contract Process Period(UU).
		@param JP_ContractProcPeriod_UU Contract Process Period(UU)	  */
	public void setJP_ContractProcPeriod_UU (String JP_ContractProcPeriod_UU)
	{
		set_ValueNoCheck (COLUMNNAME_JP_ContractProcPeriod_UU, JP_ContractProcPeriod_UU);
	}

	/** Get Contract Process Period(UU).
		@return Contract Process Period(UU)	  */
	public String getJP_ContractProcPeriod_UU () 
	{
		return (String)get_Value(COLUMNNAME_JP_ContractProcPeriod_UU);
	}

	/** Set Contract Process Value.
		@param JP_ContractProcessValue Contract Process Value	  */
	public void setJP_ContractProcessValue (String JP_ContractProcessValue)
	{
		set_Value (COLUMNNAME_JP_ContractProcessValue, JP_ContractProcessValue);
	}

	/** Get Contract Process Value.
		@return Contract Process Value	  */
	public String getJP_ContractProcessValue () 
	{
		return (String)get_Value(COLUMNNAME_JP_ContractProcessValue);
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

	/** Set Start Date.
		@param StartDate 
		First effective day (inclusive)
	  */
	public void setStartDate (Timestamp StartDate)
	{
		set_Value (COLUMNNAME_StartDate, StartDate);
	}

	/** Get Start Date.
		@return First effective day (inclusive)
	  */
	public Timestamp getStartDate () 
	{
		return (Timestamp)get_Value(COLUMNNAME_StartDate);
	}
}