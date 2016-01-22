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

/** Generated Model for JP_SalesRegionL1
 *  @author iDempiere (generated) 
 *  @version Release 3.1 - $Id$ */
public class X_JP_SalesRegionL1 extends PO implements I_JP_SalesRegionL1, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20160122L;

    /** Standard Constructor */
    public X_JP_SalesRegionL1 (Properties ctx, int JP_SalesRegionL1_ID, String trxName)
    {
      super (ctx, JP_SalesRegionL1_ID, trxName);
      /** if (JP_SalesRegionL1_ID == 0)
        {
			setJP_SalesRegionL1_ID (0);
			setJP_SalesRegionL2_ID (0);
			setName (null);
			setValue (null);
        } */
    }

    /** Load Constructor */
    public X_JP_SalesRegionL1 (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_JP_SalesRegionL1[")
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

	/** Set Sales Region Level1.
		@param JP_SalesRegionL1_ID Sales Region Level1	  */
	public void setJP_SalesRegionL1_ID (int JP_SalesRegionL1_ID)
	{
		if (JP_SalesRegionL1_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_SalesRegionL1_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_SalesRegionL1_ID, Integer.valueOf(JP_SalesRegionL1_ID));
	}

	/** Get Sales Region Level1.
		@return Sales Region Level1	  */
	public int getJP_SalesRegionL1_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_SalesRegionL1_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set JP_SalesRegionL1_UU.
		@param JP_SalesRegionL1_UU JP_SalesRegionL1_UU	  */
	public void setJP_SalesRegionL1_UU (String JP_SalesRegionL1_UU)
	{
		set_ValueNoCheck (COLUMNNAME_JP_SalesRegionL1_UU, JP_SalesRegionL1_UU);
	}

	/** Get JP_SalesRegionL1_UU.
		@return JP_SalesRegionL1_UU	  */
	public String getJP_SalesRegionL1_UU () 
	{
		return (String)get_Value(COLUMNNAME_JP_SalesRegionL1_UU);
	}

	public I_JP_SalesRegionL2 getJP_SalesRegionL2() throws RuntimeException
    {
		return (I_JP_SalesRegionL2)MTable.get(getCtx(), I_JP_SalesRegionL2.Table_Name)
			.getPO(getJP_SalesRegionL2_ID(), get_TrxName());	}

	/** Set Sales Region Level2.
		@param JP_SalesRegionL2_ID Sales Region Level2	  */
	public void setJP_SalesRegionL2_ID (int JP_SalesRegionL2_ID)
	{
		if (JP_SalesRegionL2_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_SalesRegionL2_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_SalesRegionL2_ID, Integer.valueOf(JP_SalesRegionL2_ID));
	}

	/** Get Sales Region Level2.
		@return Sales Region Level2	  */
	public int getJP_SalesRegionL2_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_SalesRegionL2_ID);
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