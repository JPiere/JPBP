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
import org.compiere.util.KeyNamePair;

/** Generated Model for JP_Org_History
 *  @author iDempiere (generated) 
 *  @version Release 6.2 - $Id$ */
public class X_JP_Org_History extends PO implements I_JP_Org_History, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20190614L;

    /** Standard Constructor */
    public X_JP_Org_History (Properties ctx, int JP_Org_History_ID, String trxName)
    {
      super (ctx, JP_Org_History_ID, trxName);
      /** if (JP_Org_History_ID == 0)
        {
			setDateFrom (new Timestamp( System.currentTimeMillis() ));
			setDateTo (new Timestamp( System.currentTimeMillis() ));
			setJP_Org_History_ID (0);
			setName (null);
        } */
    }

    /** Load Constructor */
    public X_JP_Org_History (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 7 - System - Client - Org 
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
      StringBuffer sb = new StringBuffer ("X_JP_Org_History[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public org.compiere.model.I_C_Location getC_Location() throws RuntimeException
    {
		return (org.compiere.model.I_C_Location)MTable.get(getCtx(), org.compiere.model.I_C_Location.Table_Name)
			.getPO(getC_Location_ID(), get_TrxName());	}

	/** Set Address.
		@param C_Location_ID 
		Location or Address
	  */
	public void setC_Location_ID (int C_Location_ID)
	{
		if (C_Location_ID < 1) 
			set_Value (COLUMNNAME_C_Location_ID, null);
		else 
			set_Value (COLUMNNAME_C_Location_ID, Integer.valueOf(C_Location_ID));
	}

	/** Get Address.
		@return Location or Address
	  */
	public int getC_Location_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Location_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Date From.
		@param DateFrom 
		Starting date for a range
	  */
	public void setDateFrom (Timestamp DateFrom)
	{
		set_Value (COLUMNNAME_DateFrom, DateFrom);
	}

	/** Get Date From.
		@return Starting date for a range
	  */
	public Timestamp getDateFrom () 
	{
		return (Timestamp)get_Value(COLUMNNAME_DateFrom);
	}

	/** Set Date To.
		@param DateTo 
		End date of a date range
	  */
	public void setDateTo (Timestamp DateTo)
	{
		set_Value (COLUMNNAME_DateTo, DateTo);
	}

	/** Get Date To.
		@return End date of a date range
	  */
	public Timestamp getDateTo () 
	{
		return (Timestamp)get_Value(COLUMNNAME_DateTo);
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

	public I_JP_BusinessUnit getJP_BusinessUnit() throws RuntimeException
    {
		return (I_JP_BusinessUnit)MTable.get(getCtx(), I_JP_BusinessUnit.Table_Name)
			.getPO(getJP_BusinessUnit_ID(), get_TrxName());	}

	/** Set Business Unit.
		@param JP_BusinessUnit_ID Business Unit	  */
	public void setJP_BusinessUnit_ID (int JP_BusinessUnit_ID)
	{
		if (JP_BusinessUnit_ID < 1) 
			set_Value (COLUMNNAME_JP_BusinessUnit_ID, null);
		else 
			set_Value (COLUMNNAME_JP_BusinessUnit_ID, Integer.valueOf(JP_BusinessUnit_ID));
	}

	/** Get Business Unit.
		@return Business Unit	  */
	public int getJP_BusinessUnit_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_BusinessUnit_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Org History.
		@param JP_Org_History_ID Org History	  */
	public void setJP_Org_History_ID (int JP_Org_History_ID)
	{
		if (JP_Org_History_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_Org_History_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_Org_History_ID, Integer.valueOf(JP_Org_History_ID));
	}

	/** Get Org History.
		@return Org History	  */
	public int getJP_Org_History_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Org_History_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set JP_Org_History_UU.
		@param JP_Org_History_UU JP_Org_History_UU	  */
	public void setJP_Org_History_UU (String JP_Org_History_UU)
	{
		set_Value (COLUMNNAME_JP_Org_History_UU, JP_Org_History_UU);
	}

	/** Get JP_Org_History_UU.
		@return JP_Org_History_UU	  */
	public String getJP_Org_History_UU () 
	{
		return (String)get_Value(COLUMNNAME_JP_Org_History_UU);
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

    /** Get Record ID/ColumnName
        @return ID/ColumnName pair
      */
    public KeyNamePair getKeyNamePair() 
    {
        return new KeyNamePair(get_ID(), getName());
    }

	public org.compiere.model.I_AD_User getSupervisor() throws RuntimeException
    {
		return (org.compiere.model.I_AD_User)MTable.get(getCtx(), org.compiere.model.I_AD_User.Table_Name)
			.getPO(getSupervisor_ID(), get_TrxName());	}

	/** Set Supervisor.
		@param Supervisor_ID 
		Supervisor for this user/organization - used for escalation and approval
	  */
	public void setSupervisor_ID (int Supervisor_ID)
	{
		if (Supervisor_ID < 1) 
			set_Value (COLUMNNAME_Supervisor_ID, null);
		else 
			set_Value (COLUMNNAME_Supervisor_ID, Integer.valueOf(Supervisor_ID));
	}

	/** Get Supervisor.
		@return Supervisor for this user/organization - used for escalation and approval
	  */
	public int getSupervisor_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Supervisor_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}