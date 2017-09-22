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

/** Generated Model for JP_ContractCategoryL1
 *  @author iDempiere (generated) 
 *  @version Release 4.1 - $Id$ */
public class X_JP_ContractCategoryL1 extends PO implements I_JP_ContractCategoryL1, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20170922L;

    /** Standard Constructor */
    public X_JP_ContractCategoryL1 (Properties ctx, int JP_ContractCategoryL1_ID, String trxName)
    {
      super (ctx, JP_ContractCategoryL1_ID, trxName);
      /** if (JP_ContractCategoryL1_ID == 0)
        {
			setJP_ContractCategoryL1_ID (0);
			setJP_ContractCategoryL2_ID (0);
			setName (null);
			setValue (null);
        } */
    }

    /** Load Constructor */
    public X_JP_ContractCategoryL1 (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_JP_ContractCategoryL1[")
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

	/** Set Contract Category L1.
		@param JP_ContractCategoryL1_ID Contract Category L1	  */
	public void setJP_ContractCategoryL1_ID (int JP_ContractCategoryL1_ID)
	{
		if (JP_ContractCategoryL1_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_ContractCategoryL1_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_ContractCategoryL1_ID, Integer.valueOf(JP_ContractCategoryL1_ID));
	}

	/** Get Contract Category L1.
		@return Contract Category L1	  */
	public int getJP_ContractCategoryL1_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ContractCategoryL1_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Contract Category L1(UU).
		@param JP_ContractCategoryL1_UU Contract Category L1(UU)	  */
	public void setJP_ContractCategoryL1_UU (String JP_ContractCategoryL1_UU)
	{
		set_ValueNoCheck (COLUMNNAME_JP_ContractCategoryL1_UU, JP_ContractCategoryL1_UU);
	}

	/** Get Contract Category L1(UU).
		@return Contract Category L1(UU)	  */
	public String getJP_ContractCategoryL1_UU () 
	{
		return (String)get_Value(COLUMNNAME_JP_ContractCategoryL1_UU);
	}

	public I_JP_ContractCategoryL2 getJP_ContractCategoryL2() throws RuntimeException
    {
		return (I_JP_ContractCategoryL2)MTable.get(getCtx(), I_JP_ContractCategoryL2.Table_Name)
			.getPO(getJP_ContractCategoryL2_ID(), get_TrxName());	}

	/** Set Contract Category L2.
		@param JP_ContractCategoryL2_ID Contract Category L2	  */
	public void setJP_ContractCategoryL2_ID (int JP_ContractCategoryL2_ID)
	{
		if (JP_ContractCategoryL2_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_ContractCategoryL2_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_ContractCategoryL2_ID, Integer.valueOf(JP_ContractCategoryL2_ID));
	}

	/** Get Contract Category L2.
		@return Contract Category L2	  */
	public int getJP_ContractCategoryL2_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ContractCategoryL2_ID);
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