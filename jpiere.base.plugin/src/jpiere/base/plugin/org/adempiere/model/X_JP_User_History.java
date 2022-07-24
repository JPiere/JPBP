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

/** Generated Model for JP_User_History
 *  @author iDempiere (generated) 
 *  @version Release 9 - $Id$ */
@org.adempiere.base.Model(table="JP_User_History")
public class X_JP_User_History extends PO implements I_JP_User_History, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20220724L;

    /** Standard Constructor */
    public X_JP_User_History (Properties ctx, int JP_User_History_ID, String trxName)
    {
      super (ctx, JP_User_History_ID, trxName);
      /** if (JP_User_History_ID == 0)
        {
			setAD_User_ID (0);
			setDateFrom (new Timestamp( System.currentTimeMillis() ));
			setDateTo (new Timestamp( System.currentTimeMillis() ));
			setJP_User_History_ID (0);
			setName (null);
        } */
    }

    /** Standard Constructor */
    public X_JP_User_History (Properties ctx, int JP_User_History_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, JP_User_History_ID, trxName, virtualColumns);
      /** if (JP_User_History_ID == 0)
        {
			setAD_User_ID (0);
			setDateFrom (new Timestamp( System.currentTimeMillis() ));
			setDateTo (new Timestamp( System.currentTimeMillis() ));
			setJP_User_History_ID (0);
			setName (null);
        } */
    }

    /** Load Constructor */
    public X_JP_User_History (Properties ctx, ResultSet rs, String trxName)
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
      StringBuilder sb = new StringBuilder ("X_JP_User_History[")
        .append(get_ID()).append(",Name=").append(getName()).append("]");
      return sb.toString();
    }

	public org.compiere.model.I_AD_User getAD_User() throws RuntimeException
	{
		return (org.compiere.model.I_AD_User)MTable.get(getCtx(), org.compiere.model.I_AD_User.Table_ID)
			.getPO(getAD_User_ID(), get_TrxName());
	}

	/** Set User/Contact.
		@param AD_User_ID User within the system - Internal or Business Partner Contact
	*/
	public void setAD_User_ID (int AD_User_ID)
	{
		if (AD_User_ID < 1)
			set_ValueNoCheck (COLUMNNAME_AD_User_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_AD_User_ID, Integer.valueOf(AD_User_ID));
	}

	/** Get User/Contact.
		@return User within the system - Internal or Business Partner Contact
	  */
	public int getAD_User_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_User_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Date From.
		@param DateFrom Starting date for a range
	*/
	public void setDateFrom (Timestamp DateFrom)
	{
		set_Value (COLUMNNAME_DateFrom, DateFrom);
	}

	/** Get Date From.
		@return Starting date for a range
	  */
	public Timestamp getDateFrom()
	{
		return (Timestamp)get_Value(COLUMNNAME_DateFrom);
	}

	/** Set Date To.
		@param DateTo End date of a date range
	*/
	public void setDateTo (Timestamp DateTo)
	{
		set_Value (COLUMNNAME_DateTo, DateTo);
	}

	/** Get Date To.
		@return End date of a date range
	  */
	public Timestamp getDateTo()
	{
		return (Timestamp)get_Value(COLUMNNAME_DateTo);
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

	/** Set First Name.
		@param JP_FirstName First Name
	*/
	public void setJP_FirstName (String JP_FirstName)
	{
		set_Value (COLUMNNAME_JP_FirstName, JP_FirstName);
	}

	/** Get First Name.
		@return First Name	  */
	public String getJP_FirstName()
	{
		return (String)get_Value(COLUMNNAME_JP_FirstName);
	}

	/** Set First Name(Kana).
		@param JP_FirstName_Kana First Name(Kana)
	*/
	public void setJP_FirstName_Kana (String JP_FirstName_Kana)
	{
		set_Value (COLUMNNAME_JP_FirstName_Kana, JP_FirstName_Kana);
	}

	/** Get First Name(Kana).
		@return First Name(Kana)	  */
	public String getJP_FirstName_Kana()
	{
		return (String)get_Value(COLUMNNAME_JP_FirstName_Kana);
	}

	/** Set Last Name.
		@param JP_LastName Last Name
	*/
	public void setJP_LastName (String JP_LastName)
	{
		set_Value (COLUMNNAME_JP_LastName, JP_LastName);
	}

	/** Get Last Name.
		@return Last Name	  */
	public String getJP_LastName()
	{
		return (String)get_Value(COLUMNNAME_JP_LastName);
	}

	/** Set Last Name(Kana).
		@param JP_LastName_Kana Last Name(Kana)
	*/
	public void setJP_LastName_Kana (String JP_LastName_Kana)
	{
		set_Value (COLUMNNAME_JP_LastName_Kana, JP_LastName_Kana);
	}

	/** Get Last Name(Kana).
		@return Last Name(Kana)	  */
	public String getJP_LastName_Kana()
	{
		return (String)get_Value(COLUMNNAME_JP_LastName_Kana);
	}

	/** Set Middle Name.
		@param JP_MiddleName Middle Name
	*/
	public void setJP_MiddleName (String JP_MiddleName)
	{
		set_Value (COLUMNNAME_JP_MiddleName, JP_MiddleName);
	}

	/** Get Middle Name.
		@return Middle Name	  */
	public String getJP_MiddleName()
	{
		return (String)get_Value(COLUMNNAME_JP_MiddleName);
	}

	/** Set Middle Name(Kana).
		@param JP_MiddleName_Kana Middle Name(Kana)
	*/
	public void setJP_MiddleName_Kana (String JP_MiddleName_Kana)
	{
		set_Value (COLUMNNAME_JP_MiddleName_Kana, JP_MiddleName_Kana);
	}

	/** Get Middle Name(Kana).
		@return Middle Name(Kana)	  */
	public String getJP_MiddleName_Kana()
	{
		return (String)get_Value(COLUMNNAME_JP_MiddleName_Kana);
	}

	/** Set JP_User_History.
		@param JP_User_History_ID JPIERE-0564
	*/
	public void setJP_User_History_ID (int JP_User_History_ID)
	{
		if (JP_User_History_ID < 1)
			set_ValueNoCheck (COLUMNNAME_JP_User_History_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_JP_User_History_ID, Integer.valueOf(JP_User_History_ID));
	}

	/** Get JP_User_History.
		@return JPIERE-0564
	  */
	public int getJP_User_History_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_User_History_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set JP_User_History_UU.
		@param JP_User_History_UU JP_User_History_UU
	*/
	public void setJP_User_History_UU (String JP_User_History_UU)
	{
		set_Value (COLUMNNAME_JP_User_History_UU, JP_User_History_UU);
	}

	/** Get JP_User_History_UU.
		@return JP_User_History_UU	  */
	public String getJP_User_History_UU()
	{
		return (String)get_Value(COLUMNNAME_JP_User_History_UU);
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
}