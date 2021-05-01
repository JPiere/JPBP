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

/** Generated Model for JP_ReferenceTestLine
 *  @author iDempiere (generated) 
 *  @version Release 8.2 - $Id$ */
public class X_JP_ReferenceTestLine extends PO implements I_JP_ReferenceTestLine, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20210501L;

    /** Standard Constructor */
    public X_JP_ReferenceTestLine (Properties ctx, int JP_ReferenceTestLine_ID, String trxName)
    {
      super (ctx, JP_ReferenceTestLine_ID, trxName);
      /** if (JP_ReferenceTestLine_ID == 0)
        {
			setJP_ReferenceTestLine_ID (0);
			setName (null);
        } */
    }

    /** Load Constructor */
    public X_JP_ReferenceTestLine (Properties ctx, ResultSet rs, String trxName)
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
      StringBuilder sb = new StringBuilder ("X_JP_ReferenceTestLine[")
        .append(get_ID()).append(",Name=").append(getName()).append("]");
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

	/** Set JP_ReferenceTestLine.
		@param JP_ReferenceTestLine_ID JP_ReferenceTestLine	  */
	public void setJP_ReferenceTestLine_ID (int JP_ReferenceTestLine_ID)
	{
		if (JP_ReferenceTestLine_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_ReferenceTestLine_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_ReferenceTestLine_ID, Integer.valueOf(JP_ReferenceTestLine_ID));
	}

	/** Get JP_ReferenceTestLine.
		@return JP_ReferenceTestLine	  */
	public int getJP_ReferenceTestLine_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ReferenceTestLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set JP_ReferenceTestLine_UU.
		@param JP_ReferenceTestLine_UU JP_ReferenceTestLine_UU	  */
	public void setJP_ReferenceTestLine_UU (String JP_ReferenceTestLine_UU)
	{
		set_Value (COLUMNNAME_JP_ReferenceTestLine_UU, JP_ReferenceTestLine_UU);
	}

	/** Get JP_ReferenceTestLine_UU.
		@return JP_ReferenceTestLine_UU	  */
	public String getJP_ReferenceTestLine_UU () 
	{
		return (String)get_Value(COLUMNNAME_JP_ReferenceTestLine_UU);
	}

	public I_JP_ReferenceTest getJP_ReferenceTest() throws RuntimeException
    {
		return (I_JP_ReferenceTest)MTable.get(getCtx(), I_JP_ReferenceTest.Table_Name)
			.getPO(getJP_ReferenceTest_ID(), get_TrxName());	}

	/** Set JP_ReferenceTest.
		@param JP_ReferenceTest_ID JP_ReferenceTest	  */
	public void setJP_ReferenceTest_ID (int JP_ReferenceTest_ID)
	{
		if (JP_ReferenceTest_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_ReferenceTest_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_ReferenceTest_ID, Integer.valueOf(JP_ReferenceTest_ID));
	}

	/** Get JP_ReferenceTest.
		@return JP_ReferenceTest	  */
	public int getJP_ReferenceTest_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ReferenceTest_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Line No.
		@param Line 
		Unique line for this document
	  */
	public void setLine (int Line)
	{
		set_ValueNoCheck (COLUMNNAME_Line, Integer.valueOf(Line));
	}

	/** Get Line No.
		@return Unique line for this document
	  */
	public int getLine () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Line);
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

    /** Get Record ID/ColumnName
        @return ID/ColumnName pair
      */
    public KeyNamePair getKeyNamePair() 
    {
        return new KeyNamePair(get_ID(), getName());
    }
}