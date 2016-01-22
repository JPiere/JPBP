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

/** Generated Model for JP_SalesRegionGLine
 *  @author iDempiere (generated) 
 *  @version Release 3.1 - $Id$ */
public class X_JP_SalesRegionGLine extends PO implements I_JP_SalesRegionGLine, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20160122L;

    /** Standard Constructor */
    public X_JP_SalesRegionGLine (Properties ctx, int JP_SalesRegionGLine_ID, String trxName)
    {
      super (ctx, JP_SalesRegionGLine_ID, trxName);
      /** if (JP_SalesRegionGLine_ID == 0)
        {
			setC_SalesRegion_ID (0);
			setJP_SalesRegionGLine_ID (0);
			setJP_SalesRegionG_ID (0);
        } */
    }

    /** Load Constructor */
    public X_JP_SalesRegionGLine (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_JP_SalesRegionGLine[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public org.compiere.model.I_C_SalesRegion getC_SalesRegion() throws RuntimeException
    {
		return (org.compiere.model.I_C_SalesRegion)MTable.get(getCtx(), org.compiere.model.I_C_SalesRegion.Table_Name)
			.getPO(getC_SalesRegion_ID(), get_TrxName());	}

	/** Set Sales Region.
		@param C_SalesRegion_ID 
		Sales coverage region
	  */
	public void setC_SalesRegion_ID (int C_SalesRegion_ID)
	{
		if (C_SalesRegion_ID < 1) 
			set_Value (COLUMNNAME_C_SalesRegion_ID, null);
		else 
			set_Value (COLUMNNAME_C_SalesRegion_ID, Integer.valueOf(C_SalesRegion_ID));
	}

	/** Get Sales Region.
		@return Sales coverage region
	  */
	public int getC_SalesRegion_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_SalesRegion_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
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

	/** Set Sales Region of Sales Region Group.
		@param JP_SalesRegionGLine_ID Sales Region of Sales Region Group	  */
	public void setJP_SalesRegionGLine_ID (int JP_SalesRegionGLine_ID)
	{
		if (JP_SalesRegionGLine_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_SalesRegionGLine_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_SalesRegionGLine_ID, Integer.valueOf(JP_SalesRegionGLine_ID));
	}

	/** Get Sales Region of Sales Region Group.
		@return Sales Region of Sales Region Group	  */
	public int getJP_SalesRegionGLine_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_SalesRegionGLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set JP_SalesRegionGLine_UU.
		@param JP_SalesRegionGLine_UU JP_SalesRegionGLine_UU	  */
	public void setJP_SalesRegionGLine_UU (String JP_SalesRegionGLine_UU)
	{
		set_ValueNoCheck (COLUMNNAME_JP_SalesRegionGLine_UU, JP_SalesRegionGLine_UU);
	}

	/** Get JP_SalesRegionGLine_UU.
		@return JP_SalesRegionGLine_UU	  */
	public String getJP_SalesRegionGLine_UU () 
	{
		return (String)get_Value(COLUMNNAME_JP_SalesRegionGLine_UU);
	}

	public I_JP_SalesRegionG getJP_SalesRegionG() throws RuntimeException
    {
		return (I_JP_SalesRegionG)MTable.get(getCtx(), I_JP_SalesRegionG.Table_Name)
			.getPO(getJP_SalesRegionG_ID(), get_TrxName());	}

	/** Set Sales Region Group.
		@param JP_SalesRegionG_ID Sales Region Group	  */
	public void setJP_SalesRegionG_ID (int JP_SalesRegionG_ID)
	{
		if (JP_SalesRegionG_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_SalesRegionG_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_SalesRegionG_ID, Integer.valueOf(JP_SalesRegionG_ID));
	}

	/** Get Sales Region Group.
		@return Sales Region Group	  */
	public int getJP_SalesRegionG_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_SalesRegionG_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}