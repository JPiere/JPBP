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

/** Generated Model for JP_WF_AutoAddApprovers
 *  @author iDempiere (generated) 
 *  @version Release 8.2 - $Id$ */
public class X_JP_WF_AutoAddApprovers extends PO implements I_JP_WF_AutoAddApprovers, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20211215L;

    /** Standard Constructor */
    public X_JP_WF_AutoAddApprovers (Properties ctx, int JP_WF_AutoAddApprovers_ID, String trxName)
    {
      super (ctx, JP_WF_AutoAddApprovers_ID, trxName);
      /** if (JP_WF_AutoAddApprovers_ID == 0)
        {
			setAD_WF_Node_ID (0);
			setAD_Workflow_ID (0);
			setJP_WF_AutoAddApprovers_ID (0);
			setName (null);
			setValidFrom (new Timestamp( System.currentTimeMillis() ));
        } */
    }

    /** Load Constructor */
    public X_JP_WF_AutoAddApprovers (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 7 - System - Client - Org 
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
      StringBuilder sb = new StringBuilder ("X_JP_WF_AutoAddApprovers[")
        .append(get_ID()).append(",Name=").append(getName()).append("]");
      return sb.toString();
    }

	public org.compiere.model.I_AD_WF_Node getAD_WF_Node() throws RuntimeException
    {
		return (org.compiere.model.I_AD_WF_Node)MTable.get(getCtx(), org.compiere.model.I_AD_WF_Node.Table_Name)
			.getPO(getAD_WF_Node_ID(), get_TrxName());	}

	/** Set Node.
		@param AD_WF_Node_ID 
		Workflow Node (activity), step or process
	  */
	public void setAD_WF_Node_ID (int AD_WF_Node_ID)
	{
		if (AD_WF_Node_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_AD_WF_Node_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_AD_WF_Node_ID, Integer.valueOf(AD_WF_Node_ID));
	}

	/** Get Node.
		@return Workflow Node (activity), step or process
	  */
	public int getAD_WF_Node_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_WF_Node_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_AD_Workflow getAD_Workflow() throws RuntimeException
    {
		return (org.compiere.model.I_AD_Workflow)MTable.get(getCtx(), org.compiere.model.I_AD_Workflow.Table_Name)
			.getPO(getAD_Workflow_ID(), get_TrxName());	}

	/** Set Workflow.
		@param AD_Workflow_ID 
		Workflow or combination of tasks
	  */
	public void setAD_Workflow_ID (int AD_Workflow_ID)
	{
		if (AD_Workflow_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_AD_Workflow_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_AD_Workflow_ID, Integer.valueOf(AD_Workflow_ID));
	}

	/** Get Workflow.
		@return Workflow or combination of tasks
	  */
	public int getAD_Workflow_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_Workflow_ID);
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

	/** Set WF Auto Add Approvers.
		@param JP_WF_AutoAddApprovers_ID 
		JPIERE-0518:JPBP
	  */
	public void setJP_WF_AutoAddApprovers_ID (int JP_WF_AutoAddApprovers_ID)
	{
		if (JP_WF_AutoAddApprovers_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_WF_AutoAddApprovers_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_WF_AutoAddApprovers_ID, Integer.valueOf(JP_WF_AutoAddApprovers_ID));
	}

	/** Get WF Auto Add Approvers.
		@return JPIERE-0518:JPBP
	  */
	public int getJP_WF_AutoAddApprovers_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_WF_AutoAddApprovers_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set JP_WF_AutoAddApprovers_UU.
		@param JP_WF_AutoAddApprovers_UU JP_WF_AutoAddApprovers_UU	  */
	public void setJP_WF_AutoAddApprovers_UU (String JP_WF_AutoAddApprovers_UU)
	{
		set_Value (COLUMNNAME_JP_WF_AutoAddApprovers_UU, JP_WF_AutoAddApprovers_UU);
	}

	/** Get JP_WF_AutoAddApprovers_UU.
		@return JP_WF_AutoAddApprovers_UU	  */
	public String getJP_WF_AutoAddApprovers_UU () 
	{
		return (String)get_Value(COLUMNNAME_JP_WF_AutoAddApprovers_UU);
	}

	/** Set WF Organization.
		@param JP_WF_Org_ID WF Organization	  */
	public void setJP_WF_Org_ID (int JP_WF_Org_ID)
	{
		if (JP_WF_Org_ID < 1) 
			set_Value (COLUMNNAME_JP_WF_Org_ID, null);
		else 
			set_Value (COLUMNNAME_JP_WF_Org_ID, Integer.valueOf(JP_WF_Org_ID));
	}

	/** Get WF Organization.
		@return WF Organization	  */
	public int getJP_WF_Org_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_WF_Org_ID);
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