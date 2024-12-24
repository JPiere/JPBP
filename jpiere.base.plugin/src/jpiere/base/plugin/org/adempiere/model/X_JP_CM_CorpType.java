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

/** Generated Model for JP_CM_CorpType
 *  @author iDempiere (generated)
 *  @version Release 11 - $Id$ */
@org.adempiere.base.Model(table="JP_CM_CorpType")
public class X_JP_CM_CorpType extends PO implements I_JP_CM_CorpType, I_Persistent
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20241224L;

    /** Standard Constructor */
    public X_JP_CM_CorpType (Properties ctx, int JP_CM_CorpType_ID, String trxName)
    {
      super (ctx, JP_CM_CorpType_ID, trxName);
      /** if (JP_CM_CorpType_ID == 0)
        {
			setJP_CM_CorpType_ID (0);
			setName (null);
        } */
    }

    /** Standard Constructor */
    public X_JP_CM_CorpType (Properties ctx, int JP_CM_CorpType_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, JP_CM_CorpType_ID, trxName, virtualColumns);
      /** if (JP_CM_CorpType_ID == 0)
        {
			setJP_CM_CorpType_ID (0);
			setName (null);
        } */
    }

    /** Standard Constructor */
    public X_JP_CM_CorpType (Properties ctx, String JP_CM_CorpType_UU, String trxName)
    {
      super (ctx, JP_CM_CorpType_UU, trxName);
      /** if (JP_CM_CorpType_UU == null)
        {
			setJP_CM_CorpType_ID (0);
			setName (null);
        } */
    }

    /** Standard Constructor */
    public X_JP_CM_CorpType (Properties ctx, String JP_CM_CorpType_UU, String trxName, String ... virtualColumns)
    {
      super (ctx, JP_CM_CorpType_UU, trxName, virtualColumns);
      /** if (JP_CM_CorpType_UU == null)
        {
			setJP_CM_CorpType_ID (0);
			setName (null);
        } */
    }

    /** Load Constructor */
    public X_JP_CM_CorpType (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 4 - System
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
      StringBuilder sb = new StringBuilder ("X_JP_CM_CorpType[")
        .append(get_ID()).append(",Name=").append(getName()).append("]");
      return sb.toString();
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

	/** Set Comment/Help.
		@param Help Comment or Hint
	*/
	public void setHelp (String Help)
	{
		set_Value (COLUMNNAME_Help, Help);
	}

	/** Get Comment/Help.
		@return Comment or Hint
	  */
	public String getHelp()
	{
		return (String)get_Value(COLUMNNAME_Help);
	}

	/** Set Consolidated Corp Type.
		@param JP_CM_CorpType_ID JPIERE-0635:JPPS
	*/
	public void setJP_CM_CorpType_ID (int JP_CM_CorpType_ID)
	{
		if (JP_CM_CorpType_ID < 1)
			set_ValueNoCheck (COLUMNNAME_JP_CM_CorpType_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_JP_CM_CorpType_ID, Integer.valueOf(JP_CM_CorpType_ID));
	}

	/** Get Consolidated Corp Type.
		@return JPIERE-0635:JPPS
	  */
	public int getJP_CM_CorpType_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_CM_CorpType_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Consolidated Corp Type.
		@param JP_CM_CorpType_UU Consolidated Corp Type
	*/
	public void setJP_CM_CorpType_UU (String JP_CM_CorpType_UU)
	{
		set_Value (COLUMNNAME_JP_CM_CorpType_UU, JP_CM_CorpType_UU);
	}

	/** Get Consolidated Corp Type.
		@return Consolidated Corp Type	  */
	public String getJP_CM_CorpType_UU()
	{
		return (String)get_Value(COLUMNNAME_JP_CM_CorpType_UU);
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

	/** Set Search Key.
		@param Value Search key for the record in the format required - must be unique
	*/
	public void setValue (String Value)
	{
		set_Value (COLUMNNAME_Value, Value);
	}

	/** Get Search Key.
		@return Search key for the record in the format required - must be unique
	  */
	public String getValue()
	{
		return (String)get_Value(COLUMNNAME_Value);
	}
}