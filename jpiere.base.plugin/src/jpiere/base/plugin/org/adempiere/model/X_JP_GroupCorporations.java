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

/** Generated Model for JP_GroupCorporations
 *  @author iDempiere (generated) 
 *  @version Release 2.1 - $Id$ */
public class X_JP_GroupCorporations extends PO implements I_JP_GroupCorporations, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20141230L;

    /** Standard Constructor */
    public X_JP_GroupCorporations (Properties ctx, int JP_GroupCorporations_ID, String trxName)
    {
      super (ctx, JP_GroupCorporations_ID, trxName);
      /** if (JP_GroupCorporations_ID == 0)
        {
			setJP_CorporationGroup_ID (0);
			setJP_Corporation_ID (0);
			setJP_GroupCorporations_ID (0);
        } */
    }

    /** Load Constructor */
    public X_JP_GroupCorporations (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_JP_GroupCorporations[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public I_JP_CorporationGroup getJP_CorporationGroup() throws RuntimeException
    {
		return (I_JP_CorporationGroup)MTable.get(getCtx(), I_JP_CorporationGroup.Table_Name)
			.getPO(getJP_CorporationGroup_ID(), get_TrxName());	}

	/** Set JP_CorporationGroup.
		@param JP_CorporationGroup_ID JP_CorporationGroup	  */
	public void setJP_CorporationGroup_ID (int JP_CorporationGroup_ID)
	{
		if (JP_CorporationGroup_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_CorporationGroup_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_CorporationGroup_ID, Integer.valueOf(JP_CorporationGroup_ID));
	}

	/** Get JP_CorporationGroup.
		@return JP_CorporationGroup	  */
	public int getJP_CorporationGroup_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_CorporationGroup_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_JP_Corporation getJP_Corporation() throws RuntimeException
    {
		return (I_JP_Corporation)MTable.get(getCtx(), I_JP_Corporation.Table_Name)
			.getPO(getJP_Corporation_ID(), get_TrxName());	}

	/** Set JP_Corporation.
		@param JP_Corporation_ID JP_Corporation	  */
	public void setJP_Corporation_ID (int JP_Corporation_ID)
	{
		if (JP_Corporation_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_Corporation_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_Corporation_ID, Integer.valueOf(JP_Corporation_ID));
	}

	/** Get JP_Corporation.
		@return JP_Corporation	  */
	public int getJP_Corporation_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Corporation_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set JP_GroupCorporations.
		@param JP_GroupCorporations_ID JP_GroupCorporations	  */
	public void setJP_GroupCorporations_ID (int JP_GroupCorporations_ID)
	{
		if (JP_GroupCorporations_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_GroupCorporations_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_GroupCorporations_ID, Integer.valueOf(JP_GroupCorporations_ID));
	}

	/** Get JP_GroupCorporations.
		@return JP_GroupCorporations	  */
	public int getJP_GroupCorporations_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_GroupCorporations_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set JP_GroupCorporations_UU.
		@param JP_GroupCorporations_UU JP_GroupCorporations_UU	  */
	public void setJP_GroupCorporations_UU (String JP_GroupCorporations_UU)
	{
		set_ValueNoCheck (COLUMNNAME_JP_GroupCorporations_UU, JP_GroupCorporations_UU);
	}

	/** Get JP_GroupCorporations_UU.
		@return JP_GroupCorporations_UU	  */
	public String getJP_GroupCorporations_UU () 
	{
		return (String)get_Value(COLUMNNAME_JP_GroupCorporations_UU);
	}
}