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

/** Generated Model for JP_ProductCategoryGLine
 *  @author iDempiere (generated) 
 *  @version Release 3.1 - $Id$ */
public class X_JP_ProductCategoryGLine extends PO implements I_JP_ProductCategoryGLine, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20160120L;

    /** Standard Constructor */
    public X_JP_ProductCategoryGLine (Properties ctx, int JP_ProductCategoryGLine_ID, String trxName)
    {
      super (ctx, JP_ProductCategoryGLine_ID, trxName);
      /** if (JP_ProductCategoryGLine_ID == 0)
        {
			setJP_ProductCategoryGLine_ID (0);
			setJP_ProductCategoryG_ID (0);
			setM_Product_Category_ID (0);
        } */
    }

    /** Load Constructor */
    public X_JP_ProductCategoryGLine (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_JP_ProductCategoryGLine[")
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

	/** Set Category of Product Category Group.
		@param JP_ProductCategoryGLine_ID Category of Product Category Group	  */
	public void setJP_ProductCategoryGLine_ID (int JP_ProductCategoryGLine_ID)
	{
		if (JP_ProductCategoryGLine_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_ProductCategoryGLine_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_ProductCategoryGLine_ID, Integer.valueOf(JP_ProductCategoryGLine_ID));
	}

	/** Get Category of Product Category Group.
		@return Category of Product Category Group	  */
	public int getJP_ProductCategoryGLine_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ProductCategoryGLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set JP_ProductCategoryGLine_UU.
		@param JP_ProductCategoryGLine_UU JP_ProductCategoryGLine_UU	  */
	public void setJP_ProductCategoryGLine_UU (String JP_ProductCategoryGLine_UU)
	{
		set_ValueNoCheck (COLUMNNAME_JP_ProductCategoryGLine_UU, JP_ProductCategoryGLine_UU);
	}

	/** Get JP_ProductCategoryGLine_UU.
		@return JP_ProductCategoryGLine_UU	  */
	public String getJP_ProductCategoryGLine_UU () 
	{
		return (String)get_Value(COLUMNNAME_JP_ProductCategoryGLine_UU);
	}

	public I_JP_ProductCategoryG getJP_ProductCategoryG() throws RuntimeException
    {
		return (I_JP_ProductCategoryG)MTable.get(getCtx(), I_JP_ProductCategoryG.Table_Name)
			.getPO(getJP_ProductCategoryG_ID(), get_TrxName());	}

	/** Set Product Category Group.
		@param JP_ProductCategoryG_ID Product Category Group	  */
	public void setJP_ProductCategoryG_ID (int JP_ProductCategoryG_ID)
	{
		if (JP_ProductCategoryG_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_ProductCategoryG_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_ProductCategoryG_ID, Integer.valueOf(JP_ProductCategoryG_ID));
	}

	/** Get Product Category Group.
		@return Product Category Group	  */
	public int getJP_ProductCategoryG_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ProductCategoryG_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_Product_Category getM_Product_Category() throws RuntimeException
    {
		return (org.compiere.model.I_M_Product_Category)MTable.get(getCtx(), org.compiere.model.I_M_Product_Category.Table_Name)
			.getPO(getM_Product_Category_ID(), get_TrxName());	}

	/** Set Product Category.
		@param M_Product_Category_ID 
		Category of a Product
	  */
	public void setM_Product_Category_ID (int M_Product_Category_ID)
	{
		if (M_Product_Category_ID < 1) 
			set_Value (COLUMNNAME_M_Product_Category_ID, null);
		else 
			set_Value (COLUMNNAME_M_Product_Category_ID, Integer.valueOf(M_Product_Category_ID));
	}

	/** Get Product Category.
		@return Category of a Product
	  */
	public int getM_Product_Category_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Product_Category_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}