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

/** Generated Model for JP_InvValProfileOrg
 *  @author iDempiere (generated) 
 *  @version Release 3.1 - $Id$ */
public class X_JP_InvValProfileOrg extends PO implements I_JP_InvValProfileOrg, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20160917L;

    /** Standard Constructor */
    public X_JP_InvValProfileOrg (Properties ctx, int JP_InvValProfileOrg_ID, String trxName)
    {
      super (ctx, JP_InvValProfileOrg_ID, trxName);
      /** if (JP_InvValProfileOrg_ID == 0)
        {
			setJP_InvValProfileOrg_ID (0);
			setJP_InvValProfile_ID (0);
        } */
    }

    /** Load Constructor */
    public X_JP_InvValProfileOrg (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 1 - Org 
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
      StringBuffer sb = new StringBuffer ("X_JP_InvValProfileOrg[")
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

	/** Set Inventory Valuation Profile Organization.
		@param JP_InvValProfileOrg_ID Inventory Valuation Profile Organization	  */
	public void setJP_InvValProfileOrg_ID (int JP_InvValProfileOrg_ID)
	{
		if (JP_InvValProfileOrg_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_InvValProfileOrg_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_InvValProfileOrg_ID, Integer.valueOf(JP_InvValProfileOrg_ID));
	}

	/** Get Inventory Valuation Profile Organization.
		@return Inventory Valuation Profile Organization	  */
	public int getJP_InvValProfileOrg_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_InvValProfileOrg_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set JP_InvValProfileOrg_UU.
		@param JP_InvValProfileOrg_UU JP_InvValProfileOrg_UU	  */
	public void setJP_InvValProfileOrg_UU (String JP_InvValProfileOrg_UU)
	{
		set_ValueNoCheck (COLUMNNAME_JP_InvValProfileOrg_UU, JP_InvValProfileOrg_UU);
	}

	/** Get JP_InvValProfileOrg_UU.
		@return JP_InvValProfileOrg_UU	  */
	public String getJP_InvValProfileOrg_UU () 
	{
		return (String)get_Value(COLUMNNAME_JP_InvValProfileOrg_UU);
	}

	public I_JP_InvValProfile getJP_InvValProfile() throws RuntimeException
    {
		return (I_JP_InvValProfile)MTable.get(getCtx(), I_JP_InvValProfile.Table_Name)
			.getPO(getJP_InvValProfile_ID(), get_TrxName());	}

	/** Set Inventory Valuation Profile.
		@param JP_InvValProfile_ID Inventory Valuation Profile	  */
	public void setJP_InvValProfile_ID (int JP_InvValProfile_ID)
	{
		if (JP_InvValProfile_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_InvValProfile_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_InvValProfile_ID, Integer.valueOf(JP_InvValProfile_ID));
	}

	/** Get Inventory Valuation Profile.
		@return Inventory Valuation Profile	  */
	public int getJP_InvValProfile_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_InvValProfile_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_DiscountSchema getM_DiscountSchema() throws RuntimeException
    {
		return (org.compiere.model.I_M_DiscountSchema)MTable.get(getCtx(), org.compiere.model.I_M_DiscountSchema.Table_Name)
			.getPO(getM_DiscountSchema_ID(), get_TrxName());	}

	/** Set Discount Schema.
		@param M_DiscountSchema_ID 
		Schema to calculate the trade discount percentage
	  */
	public void setM_DiscountSchema_ID (int M_DiscountSchema_ID)
	{
		if (M_DiscountSchema_ID < 1) 
			set_Value (COLUMNNAME_M_DiscountSchema_ID, null);
		else 
			set_Value (COLUMNNAME_M_DiscountSchema_ID, Integer.valueOf(M_DiscountSchema_ID));
	}

	/** Get Discount Schema.
		@return Schema to calculate the trade discount percentage
	  */
	public int getM_DiscountSchema_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_DiscountSchema_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_PriceList getM_PriceList() throws RuntimeException
    {
		return (org.compiere.model.I_M_PriceList)MTable.get(getCtx(), org.compiere.model.I_M_PriceList.Table_Name)
			.getPO(getM_PriceList_ID(), get_TrxName());	}

	/** Set Price List.
		@param M_PriceList_ID 
		Unique identifier of a Price List
	  */
	public void setM_PriceList_ID (int M_PriceList_ID)
	{
		if (M_PriceList_ID < 1) 
			set_Value (COLUMNNAME_M_PriceList_ID, null);
		else 
			set_Value (COLUMNNAME_M_PriceList_ID, Integer.valueOf(M_PriceList_ID));
	}

	/** Get Price List.
		@return Unique identifier of a Price List
	  */
	public int getM_PriceList_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_PriceList_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}