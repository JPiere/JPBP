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

/** Generated Model for JP_BulkUpdateProfileAccess
 *  @author iDempiere (generated)
 *  @version Release 11 - $Id$ */
@org.adempiere.base.Model(table="JP_BulkUpdateProfileAccess")
public class X_JP_BulkUpdateProfileAccess extends PO implements I_JP_BulkUpdateProfileAccess, I_Persistent
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20241012L;

    /** Standard Constructor */
    public X_JP_BulkUpdateProfileAccess (Properties ctx, int JP_BulkUpdateProfileAccess_ID, String trxName)
    {
      super (ctx, JP_BulkUpdateProfileAccess_ID, trxName);
      /** if (JP_BulkUpdateProfileAccess_ID == 0)
        {
			setAD_Role_ID (0);
			setJP_BulkUpdateProfileAccess_ID (0);
			setJP_BulkUpdateProfile_ID (0);
        } */
    }

    /** Standard Constructor */
    public X_JP_BulkUpdateProfileAccess (Properties ctx, int JP_BulkUpdateProfileAccess_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, JP_BulkUpdateProfileAccess_ID, trxName, virtualColumns);
      /** if (JP_BulkUpdateProfileAccess_ID == 0)
        {
			setAD_Role_ID (0);
			setJP_BulkUpdateProfileAccess_ID (0);
			setJP_BulkUpdateProfile_ID (0);
        } */
    }

    /** Standard Constructor */
    public X_JP_BulkUpdateProfileAccess (Properties ctx, String JP_BulkUpdateProfileAccess_UU, String trxName)
    {
      super (ctx, JP_BulkUpdateProfileAccess_UU, trxName);
      /** if (JP_BulkUpdateProfileAccess_UU == null)
        {
			setAD_Role_ID (0);
			setJP_BulkUpdateProfileAccess_ID (0);
			setJP_BulkUpdateProfile_ID (0);
        } */
    }

    /** Standard Constructor */
    public X_JP_BulkUpdateProfileAccess (Properties ctx, String JP_BulkUpdateProfileAccess_UU, String trxName, String ... virtualColumns)
    {
      super (ctx, JP_BulkUpdateProfileAccess_UU, trxName, virtualColumns);
      /** if (JP_BulkUpdateProfileAccess_UU == null)
        {
			setAD_Role_ID (0);
			setJP_BulkUpdateProfileAccess_ID (0);
			setJP_BulkUpdateProfile_ID (0);
        } */
    }

    /** Load Constructor */
    public X_JP_BulkUpdateProfileAccess (Properties ctx, ResultSet rs, String trxName)
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
      StringBuilder sb = new StringBuilder ("X_JP_BulkUpdateProfileAccess[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public org.compiere.model.I_AD_Role getAD_Role() throws RuntimeException
	{
		return (org.compiere.model.I_AD_Role)MTable.get(getCtx(), org.compiere.model.I_AD_Role.Table_ID)
			.getPO(getAD_Role_ID(), get_TrxName());
	}

	/** Set Role.
		@param AD_Role_ID Responsibility Role
	*/
	public void setAD_Role_ID (int AD_Role_ID)
	{
		if (AD_Role_ID < 0)
			set_Value (COLUMNNAME_AD_Role_ID, null);
		else
			set_Value (COLUMNNAME_AD_Role_ID, Integer.valueOf(AD_Role_ID));
	}

	/** Get Role.
		@return Responsibility Role
	  */
	public int getAD_Role_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_Role_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Bulk Update Profile Access.
		@param JP_BulkUpdateProfileAccess_ID Bulk Update Profile Access
	*/
	public void setJP_BulkUpdateProfileAccess_ID (int JP_BulkUpdateProfileAccess_ID)
	{
		if (JP_BulkUpdateProfileAccess_ID < 1)
			set_ValueNoCheck (COLUMNNAME_JP_BulkUpdateProfileAccess_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_JP_BulkUpdateProfileAccess_ID, Integer.valueOf(JP_BulkUpdateProfileAccess_ID));
	}

	/** Get Bulk Update Profile Access.
		@return Bulk Update Profile Access	  */
	public int getJP_BulkUpdateProfileAccess_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_BulkUpdateProfileAccess_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Bulk Update Profile Access(UU).
		@param JP_BulkUpdateProfileAccess_UU Bulk Update Profile Access(UU)
	*/
	public void setJP_BulkUpdateProfileAccess_UU (String JP_BulkUpdateProfileAccess_UU)
	{
		set_Value (COLUMNNAME_JP_BulkUpdateProfileAccess_UU, JP_BulkUpdateProfileAccess_UU);
	}

	/** Get Bulk Update Profile Access(UU).
		@return Bulk Update Profile Access(UU)	  */
	public String getJP_BulkUpdateProfileAccess_UU()
	{
		return (String)get_Value(COLUMNNAME_JP_BulkUpdateProfileAccess_UU);
	}

	public I_JP_BulkUpdateProfile getJP_BulkUpdateProfile() throws RuntimeException
	{
		return (I_JP_BulkUpdateProfile)MTable.get(getCtx(), I_JP_BulkUpdateProfile.Table_ID)
			.getPO(getJP_BulkUpdateProfile_ID(), get_TrxName());
	}

	/** Set Bulk Update Profile.
		@param JP_BulkUpdateProfile_ID JPIERE-0621:JPBP
	*/
	public void setJP_BulkUpdateProfile_ID (int JP_BulkUpdateProfile_ID)
	{
		if (JP_BulkUpdateProfile_ID < 1)
			set_ValueNoCheck (COLUMNNAME_JP_BulkUpdateProfile_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_JP_BulkUpdateProfile_ID, Integer.valueOf(JP_BulkUpdateProfile_ID));
	}

	/** Get Bulk Update Profile.
		@return JPIERE-0621:JPBP
	  */
	public int getJP_BulkUpdateProfile_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_BulkUpdateProfile_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}