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
import org.compiere.util.KeyNamePair;

/** Generated Model for JP_BulkUpdateProfile
 *  @author iDempiere (generated)
 *  @version Release 11 - $Id$ */
@org.adempiere.base.Model(table="JP_BulkUpdateProfile")
public class X_JP_BulkUpdateProfile extends PO implements I_JP_BulkUpdateProfile, I_Persistent
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20241012L;

    /** Standard Constructor */
    public X_JP_BulkUpdateProfile (Properties ctx, int JP_BulkUpdateProfile_ID, String trxName)
    {
      super (ctx, JP_BulkUpdateProfile_ID, trxName);
      /** if (JP_BulkUpdateProfile_ID == 0)
        {
			setJP_BulkUpdateProfile_ID (0);
			setJP_ConfirmOfExecution (null);
			setName (null);
			setValue (null);
        } */
    }

    /** Standard Constructor */
    public X_JP_BulkUpdateProfile (Properties ctx, int JP_BulkUpdateProfile_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, JP_BulkUpdateProfile_ID, trxName, virtualColumns);
      /** if (JP_BulkUpdateProfile_ID == 0)
        {
			setJP_BulkUpdateProfile_ID (0);
			setJP_ConfirmOfExecution (null);
			setName (null);
			setValue (null);
        } */
    }

    /** Standard Constructor */
    public X_JP_BulkUpdateProfile (Properties ctx, String JP_BulkUpdateProfile_UU, String trxName)
    {
      super (ctx, JP_BulkUpdateProfile_UU, trxName);
      /** if (JP_BulkUpdateProfile_UU == null)
        {
			setJP_BulkUpdateProfile_ID (0);
			setJP_ConfirmOfExecution (null);
			setName (null);
			setValue (null);
        } */
    }

    /** Standard Constructor */
    public X_JP_BulkUpdateProfile (Properties ctx, String JP_BulkUpdateProfile_UU, String trxName, String ... virtualColumns)
    {
      super (ctx, JP_BulkUpdateProfile_UU, trxName, virtualColumns);
      /** if (JP_BulkUpdateProfile_UU == null)
        {
			setJP_BulkUpdateProfile_ID (0);
			setJP_ConfirmOfExecution (null);
			setName (null);
			setValue (null);
        } */
    }

    /** Load Constructor */
    public X_JP_BulkUpdateProfile (Properties ctx, ResultSet rs, String trxName)
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
      StringBuilder sb = new StringBuilder ("X_JP_BulkUpdateProfile[")
        .append(get_ID()).append(",Name=").append(getName()).append("]");
      return sb.toString();
    }

	/** Set Description.
		@param Description Optional short description of the record
	*/
	public void setDescription (String Description)
	{
		set_Value (COLUMNNAME_Description, Description);
	}

	/** Get Description.
		@return Optional short description of the record
	  */
	public String getDescription()
	{
		return (String)get_Value(COLUMNNAME_Description);
	}

	/** Set Comment/Help.
		@param Help Comment or Hint
	*/
	public void setHelp (String Help)
	{
		set_Value (COLUMNNAME_Help, Help);
	}

	/** Get Comment/Help.
		@return Comment or Hint
	  */
	public String getHelp()
	{
		return (String)get_Value(COLUMNNAME_Help);
	}

	/** Set Bulk Update Profile.
		@param JP_BulkUpdateProfile_ID JPIERE-0621:JPBP
	*/
	public void setJP_BulkUpdateProfile_ID (int JP_BulkUpdateProfile_ID)
	{
		if (JP_BulkUpdateProfile_ID < 1)
			set_ValueNoCheck (COLUMNNAME_JP_BulkUpdateProfile_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_JP_BulkUpdateProfile_ID, Integer.valueOf(JP_BulkUpdateProfile_ID));
	}

	/** Get Bulk Update Profile.
		@return JPIERE-0621:JPBP
	  */
	public int getJP_BulkUpdateProfile_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_BulkUpdateProfile_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Bulk Update Profile(UU).
		@param JP_BulkUpdateProfile_UU Bulk Update Profile(UU)
	*/
	public void setJP_BulkUpdateProfile_UU (String JP_BulkUpdateProfile_UU)
	{
		set_Value (COLUMNNAME_JP_BulkUpdateProfile_UU, JP_BulkUpdateProfile_UU);
	}

	/** Get Bulk Update Profile(UU).
		@return Bulk Update Profile(UU)	  */
	public String getJP_BulkUpdateProfile_UU()
	{
		return (String)get_Value(COLUMNNAME_JP_BulkUpdateProfile_UU);
	}

	/** Confirm = C */
	public static final String JP_CONFIRMOFEXECUTION_Confirm = "C";
	/** Data Base = D */
	public static final String JP_CONFIRMOFEXECUTION_DataBase = "D";
	/** Host = H */
	public static final String JP_CONFIRMOFEXECUTION_Host = "H";
	/** Nothing = N */
	public static final String JP_CONFIRMOFEXECUTION_Nothing = "N";
	/** Search Key = V */
	public static final String JP_CONFIRMOFEXECUTION_SearchKey = "V";
	/** Set Confirm of execution.
		@param JP_ConfirmOfExecution Confirm of execution
	*/
	public void setJP_ConfirmOfExecution (String JP_ConfirmOfExecution)
	{

		set_Value (COLUMNNAME_JP_ConfirmOfExecution, JP_ConfirmOfExecution);
	}

	/** Get Confirm of execution.
		@return Confirm of execution	  */
	public String getJP_ConfirmOfExecution()
	{
		return (String)get_Value(COLUMNNAME_JP_ConfirmOfExecution);
	}

	/** Set Name.
		@param Name Alphanumeric identifier of the entity
	*/
	public void setName (String Name)
	{
		set_Value (COLUMNNAME_Name, Name);
	}

	/** Get Name.
		@return Alphanumeric identifier of the entity
	  */
	public String getName()
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

	/** Set Search Key.
		@param Value Search key for the record in the format required - must be unique
	*/
	public void setValue (String Value)
	{
		set_Value (COLUMNNAME_Value, Value);
	}

	/** Get Search Key.
		@return Search key for the record in the format required - must be unique
	  */
	public String getValue()
	{
		return (String)get_Value(COLUMNNAME_Value);
	}
}