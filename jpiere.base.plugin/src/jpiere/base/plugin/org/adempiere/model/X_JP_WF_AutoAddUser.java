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

/** Generated Model for JP_WF_AutoAddUser
 *  @author iDempiere (generated) 
 *  @version Release 8.2 - $Id$ */
public class X_JP_WF_AutoAddUser extends PO implements I_JP_WF_AutoAddUser, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20211215L;

    /** Standard Constructor */
    public X_JP_WF_AutoAddUser (Properties ctx, int JP_WF_AutoAddUser_ID, String trxName)
    {
      super (ctx, JP_WF_AutoAddUser_ID, trxName);
      /** if (JP_WF_AutoAddUser_ID == 0)
        {
			setAD_User_ID (0);
			setJP_WF_AutoAddApprovers_ID (0);
			setJP_WF_AutoAddUser_ID (0);
        } */
    }

    /** Load Constructor */
    public X_JP_WF_AutoAddUser (Properties ctx, ResultSet rs, String trxName)
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
      StringBuilder sb = new StringBuilder ("X_JP_WF_AutoAddUser[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public org.compiere.model.I_AD_User getAD_User() throws RuntimeException
    {
		return (org.compiere.model.I_AD_User)MTable.get(getCtx(), org.compiere.model.I_AD_User.Table_Name)
			.getPO(getAD_User_ID(), get_TrxName());	}

	/** Set User/Contact.
		@param AD_User_ID 
		User within the system - Internal or Business Partner Contact
	  */
	public void setAD_User_ID (int AD_User_ID)
	{
		if (AD_User_ID < 1) 
			set_Value (COLUMNNAME_AD_User_ID, null);
		else 
			set_Value (COLUMNNAME_AD_User_ID, Integer.valueOf(AD_User_ID));
	}

	/** Get User/Contact.
		@return User within the system - Internal or Business Partner Contact
	  */
	public int getAD_User_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_User_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_JP_WF_AutoAddApprovers getJP_WF_AutoAddApprovers() throws RuntimeException
    {
		return (I_JP_WF_AutoAddApprovers)MTable.get(getCtx(), I_JP_WF_AutoAddApprovers.Table_Name)
			.getPO(getJP_WF_AutoAddApprovers_ID(), get_TrxName());	}

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

	/** Set JP_WF_AutoAddUser.
		@param JP_WF_AutoAddUser_ID 
		JPIERE-0518:JPBP
	  */
	public void setJP_WF_AutoAddUser_ID (int JP_WF_AutoAddUser_ID)
	{
		if (JP_WF_AutoAddUser_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_WF_AutoAddUser_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_WF_AutoAddUser_ID, Integer.valueOf(JP_WF_AutoAddUser_ID));
	}

	/** Get JP_WF_AutoAddUser.
		@return JPIERE-0518:JPBP
	  */
	public int getJP_WF_AutoAddUser_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_WF_AutoAddUser_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set JP_WF_AutoAddUser_UU.
		@param JP_WF_AutoAddUser_UU JP_WF_AutoAddUser_UU	  */
	public void setJP_WF_AutoAddUser_UU (String JP_WF_AutoAddUser_UU)
	{
		set_Value (COLUMNNAME_JP_WF_AutoAddUser_UU, JP_WF_AutoAddUser_UU);
	}

	/** Get JP_WF_AutoAddUser_UU.
		@return JP_WF_AutoAddUser_UU	  */
	public String getJP_WF_AutoAddUser_UU () 
	{
		return (String)get_Value(COLUMNNAME_JP_WF_AutoAddUser_UU);
	}
}