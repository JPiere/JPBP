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
import java.sql.Timestamp;
import java.util.Properties;
import org.compiere.model.*;

/** Generated Model for JP_ContractProcessList
 *  @author iDempiere (generated) 
 *  @version Release 4.1 - $Id$ */
public class X_JP_ContractProcessList extends PO implements I_JP_ContractProcessList, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20170915L;

    /** Standard Constructor */
    public X_JP_ContractProcessList (Properties ctx, int JP_ContractProcessList_ID, String trxName)
    {
      super (ctx, JP_ContractProcessList_ID, trxName);
      /** if (JP_ContractProcessList_ID == 0)
        {
			setJP_ContractProcessList_ID (0);
			setJP_ContractProcessRef_ID (0);
			setJP_ContractProcess_ID (0);
        } */
    }

    /** Load Constructor */
    public X_JP_ContractProcessList (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_JP_ContractProcessList[")
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

	/** Set Contract Process List.
		@param JP_ContractProcessList_ID Contract Process List	  */
	public void setJP_ContractProcessList_ID (int JP_ContractProcessList_ID)
	{
		if (JP_ContractProcessList_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_ContractProcessList_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_ContractProcessList_ID, Integer.valueOf(JP_ContractProcessList_ID));
	}

	/** Get Contract Process List.
		@return Contract Process List	  */
	public int getJP_ContractProcessList_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ContractProcessList_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Contract Process List(UU).
		@param JP_ContractProcessList_UU Contract Process List(UU)	  */
	public void setJP_ContractProcessList_UU (String JP_ContractProcessList_UU)
	{
		set_ValueNoCheck (COLUMNNAME_JP_ContractProcessList_UU, JP_ContractProcessList_UU);
	}

	/** Get Contract Process List(UU).
		@return Contract Process List(UU)	  */
	public String getJP_ContractProcessList_UU () 
	{
		return (String)get_Value(COLUMNNAME_JP_ContractProcessList_UU);
	}

	public I_JP_ContractProcessRef getJP_ContractProcessRef() throws RuntimeException
    {
		return (I_JP_ContractProcessRef)MTable.get(getCtx(), I_JP_ContractProcessRef.Table_Name)
			.getPO(getJP_ContractProcessRef_ID(), get_TrxName());	}

	/** Set Contract Process Reference.
		@param JP_ContractProcessRef_ID Contract Process Reference	  */
	public void setJP_ContractProcessRef_ID (int JP_ContractProcessRef_ID)
	{
		if (JP_ContractProcessRef_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_ContractProcessRef_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_ContractProcessRef_ID, Integer.valueOf(JP_ContractProcessRef_ID));
	}

	/** Get Contract Process Reference.
		@return Contract Process Reference	  */
	public int getJP_ContractProcessRef_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ContractProcessRef_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_JP_ContractProcess getJP_ContractProcess() throws RuntimeException
    {
		return (I_JP_ContractProcess)MTable.get(getCtx(), I_JP_ContractProcess.Table_Name)
			.getPO(getJP_ContractProcess_ID(), get_TrxName());	}

	/** Set Contract Process.
		@param JP_ContractProcess_ID Contract Process	  */
	public void setJP_ContractProcess_ID (int JP_ContractProcess_ID)
	{
		if (JP_ContractProcess_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_ContractProcess_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_ContractProcess_ID, Integer.valueOf(JP_ContractProcess_ID));
	}

	/** Get Contract Process.
		@return Contract Process	  */
	public int getJP_ContractProcess_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ContractProcess_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Valid from.
		@param ValidFrom 
		Valid from including this date (first day)
	  */
	public void setValidFrom (Timestamp ValidFrom)
	{
		set_Value (COLUMNNAME_ValidFrom, ValidFrom);
	}

	/** Get Valid from.
		@return Valid from including this date (first day)
	  */
	public Timestamp getValidFrom () 
	{
		return (Timestamp)get_Value(COLUMNNAME_ValidFrom);
	}

	/** Set Valid to.
		@param ValidTo 
		Valid to including this date (last day)
	  */
	public void setValidTo (Timestamp ValidTo)
	{
		set_Value (COLUMNNAME_ValidTo, ValidTo);
	}

	/** Get Valid to.
		@return Valid to including this date (last day)
	  */
	public Timestamp getValidTo () 
	{
		return (Timestamp)get_Value(COLUMNNAME_ValidTo);
	}
}