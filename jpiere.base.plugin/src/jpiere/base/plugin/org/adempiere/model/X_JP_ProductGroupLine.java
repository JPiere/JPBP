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

/** Generated Model for JP_ProductGroupLine
 *  @author iDempiere (generated) 
 *  @version Release 3.1 - $Id$ */
public class X_JP_ProductGroupLine extends PO implements I_JP_ProductGroupLine, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20160120L;

    /** Standard Constructor */
    public X_JP_ProductGroupLine (Properties ctx, int JP_ProductGroupLine_ID, String trxName)
    {
      super (ctx, JP_ProductGroupLine_ID, trxName);
      /** if (JP_ProductGroupLine_ID == 0)
        {
			setJP_ProductGroupLine_ID (0);
			setJP_ProductGroup_ID (0);
			setM_Product_ID (0);
        } */
    }

    /** Load Constructor */
    public X_JP_ProductGroupLine (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_JP_ProductGroupLine[")
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

	/** Set Product of Product Group.
		@param JP_ProductGroupLine_ID Product of Product Group	  */
	public void setJP_ProductGroupLine_ID (int JP_ProductGroupLine_ID)
	{
		if (JP_ProductGroupLine_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_ProductGroupLine_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_ProductGroupLine_ID, Integer.valueOf(JP_ProductGroupLine_ID));
	}

	/** Get Product of Product Group.
		@return Product of Product Group	  */
	public int getJP_ProductGroupLine_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ProductGroupLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set JP_ProductGroupLine_UU.
		@param JP_ProductGroupLine_UU JP_ProductGroupLine_UU	  */
	public void setJP_ProductGroupLine_UU (String JP_ProductGroupLine_UU)
	{
		set_ValueNoCheck (COLUMNNAME_JP_ProductGroupLine_UU, JP_ProductGroupLine_UU);
	}

	/** Get JP_ProductGroupLine_UU.
		@return JP_ProductGroupLine_UU	  */
	public String getJP_ProductGroupLine_UU () 
	{
		return (String)get_Value(COLUMNNAME_JP_ProductGroupLine_UU);
	}

	public I_JP_ProductGroup getJP_ProductGroup() throws RuntimeException
    {
		return (I_JP_ProductGroup)MTable.get(getCtx(), I_JP_ProductGroup.Table_Name)
			.getPO(getJP_ProductGroup_ID(), get_TrxName());	}

	/** Set Product Group.
		@param JP_ProductGroup_ID Product Group	  */
	public void setJP_ProductGroup_ID (int JP_ProductGroup_ID)
	{
		if (JP_ProductGroup_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_ProductGroup_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_ProductGroup_ID, Integer.valueOf(JP_ProductGroup_ID));
	}

	/** Get Product Group.
		@return Product Group	  */
	public int getJP_ProductGroup_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ProductGroup_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_Product getM_Product() throws RuntimeException
    {
		return (org.compiere.model.I_M_Product)MTable.get(getCtx(), org.compiere.model.I_M_Product.Table_Name)
			.getPO(getM_Product_ID(), get_TrxName());	}

	/** Set Product.
		@param M_Product_ID 
		Product, Service, Item
	  */
	public void setM_Product_ID (int M_Product_ID)
	{
		if (M_Product_ID < 1) 
			set_Value (COLUMNNAME_M_Product_ID, null);
		else 
			set_Value (COLUMNNAME_M_Product_ID, Integer.valueOf(M_Product_ID));
	}

	/** Get Product.
		@return Product, Service, Item
	  */
	public int getM_Product_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Product_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}