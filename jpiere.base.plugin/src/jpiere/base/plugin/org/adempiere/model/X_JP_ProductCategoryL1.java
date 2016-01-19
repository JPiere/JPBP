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

/** Generated Model for JP_ProductCategoryL1
 *  @author iDempiere (generated) 
 *  @version Release 3.1 - $Id$ */
public class X_JP_ProductCategoryL1 extends PO implements I_JP_ProductCategoryL1, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20160119L;

    /** Standard Constructor */
    public X_JP_ProductCategoryL1 (Properties ctx, int JP_ProductCategoryL1_ID, String trxName)
    {
      super (ctx, JP_ProductCategoryL1_ID, trxName);
      /** if (JP_ProductCategoryL1_ID == 0)
        {
			setJP_ProductCategoryL1_ID (0);
			setJP_ProductCategoryL2_ID (0);
			setName (null);
			setValue (null);
        } */
    }

    /** Load Constructor */
    public X_JP_ProductCategoryL1 (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_JP_ProductCategoryL1[")
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

	/** Set Product Category Level1.
		@param JP_ProductCategoryL1_ID Product Category Level1	  */
	public void setJP_ProductCategoryL1_ID (int JP_ProductCategoryL1_ID)
	{
		if (JP_ProductCategoryL1_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_ProductCategoryL1_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_ProductCategoryL1_ID, Integer.valueOf(JP_ProductCategoryL1_ID));
	}

	/** Get Product Category Level1.
		@return Product Category Level1	  */
	public int getJP_ProductCategoryL1_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ProductCategoryL1_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set JP_ProductCategoryL1_UU.
		@param JP_ProductCategoryL1_UU JP_ProductCategoryL1_UU	  */
	public void setJP_ProductCategoryL1_UU (String JP_ProductCategoryL1_UU)
	{
		set_ValueNoCheck (COLUMNNAME_JP_ProductCategoryL1_UU, JP_ProductCategoryL1_UU);
	}

	/** Get JP_ProductCategoryL1_UU.
		@return JP_ProductCategoryL1_UU	  */
	public String getJP_ProductCategoryL1_UU () 
	{
		return (String)get_Value(COLUMNNAME_JP_ProductCategoryL1_UU);
	}

	public I_JP_ProductCategoryL2 getJP_ProductCategoryL2() throws RuntimeException
    {
		return (I_JP_ProductCategoryL2)MTable.get(getCtx(), I_JP_ProductCategoryL2.Table_Name)
			.getPO(getJP_ProductCategoryL2_ID(), get_TrxName());	}

	/** Set Product Category Level2.
		@param JP_ProductCategoryL2_ID Product Category Level2	  */
	public void setJP_ProductCategoryL2_ID (int JP_ProductCategoryL2_ID)
	{
		if (JP_ProductCategoryL2_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_ProductCategoryL2_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_ProductCategoryL2_ID, Integer.valueOf(JP_ProductCategoryL2_ID));
	}

	/** Get Product Category Level2.
		@return Product Category Level2	  */
	public int getJP_ProductCategoryL2_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ProductCategoryL2_ID);
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