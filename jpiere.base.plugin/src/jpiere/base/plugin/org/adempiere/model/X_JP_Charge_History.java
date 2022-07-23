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

/** Generated Model for JP_Charge_History
 *  @author iDempiere (generated) 
 *  @version Release 9 - $Id$ */
@org.adempiere.base.Model(table="JP_Charge_History")
public class X_JP_Charge_History extends PO implements I_JP_Charge_History, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20220723L;

    /** Standard Constructor */
    public X_JP_Charge_History (Properties ctx, int JP_Charge_History_ID, String trxName)
    {
      super (ctx, JP_Charge_History_ID, trxName);
      /** if (JP_Charge_History_ID == 0)
        {
			setC_Charge_ID (0);
			setDateFrom (new Timestamp( System.currentTimeMillis() ));
			setDateTo (new Timestamp( System.currentTimeMillis() ));
			setJP_Charge_History_ID (0);
			setName (null);
        } */
    }

    /** Standard Constructor */
    public X_JP_Charge_History (Properties ctx, int JP_Charge_History_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, JP_Charge_History_ID, trxName, virtualColumns);
      /** if (JP_Charge_History_ID == 0)
        {
			setC_Charge_ID (0);
			setDateFrom (new Timestamp( System.currentTimeMillis() ));
			setDateTo (new Timestamp( System.currentTimeMillis() ));
			setJP_Charge_History_ID (0);
			setName (null);
        } */
    }

    /** Load Constructor */
    public X_JP_Charge_History (Properties ctx, ResultSet rs, String trxName)
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
      StringBuilder sb = new StringBuilder ("X_JP_Charge_History[")
        .append(get_ID()).append(",Name=").append(getName()).append("]");
      return sb.toString();
    }

	public org.compiere.model.I_C_Charge getC_Charge() throws RuntimeException
	{
		return (org.compiere.model.I_C_Charge)MTable.get(getCtx(), org.compiere.model.I_C_Charge.Table_ID)
			.getPO(getC_Charge_ID(), get_TrxName());
	}

	/** Set Charge.
		@param C_Charge_ID Additional document charges
	*/
	public void setC_Charge_ID (int C_Charge_ID)
	{
		if (C_Charge_ID < 1)
			set_ValueNoCheck (COLUMNNAME_C_Charge_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_C_Charge_ID, Integer.valueOf(C_Charge_ID));
	}

	/** Get Charge.
		@return Additional document charges
	  */
	public int getC_Charge_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Charge_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Date From.
		@param DateFrom Starting date for a range
	*/
	public void setDateFrom (Timestamp DateFrom)
	{
		set_Value (COLUMNNAME_DateFrom, DateFrom);
	}

	/** Get Date From.
		@return Starting date for a range
	  */
	public Timestamp getDateFrom()
	{
		return (Timestamp)get_Value(COLUMNNAME_DateFrom);
	}

	/** Set Date To.
		@param DateTo End date of a date range
	*/
	public void setDateTo (Timestamp DateTo)
	{
		set_Value (COLUMNNAME_DateTo, DateTo);
	}

	/** Get Date To.
		@return End date of a date range
	  */
	public Timestamp getDateTo()
	{
		return (Timestamp)get_Value(COLUMNNAME_DateTo);
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

	/** Set JP_Charge_History.
		@param JP_Charge_History_ID JPIERE-0559
	*/
	public void setJP_Charge_History_ID (int JP_Charge_History_ID)
	{
		if (JP_Charge_History_ID < 1)
			set_ValueNoCheck (COLUMNNAME_JP_Charge_History_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_JP_Charge_History_ID, Integer.valueOf(JP_Charge_History_ID));
	}

	/** Get JP_Charge_History.
		@return JPIERE-0559
	  */
	public int getJP_Charge_History_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Charge_History_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set JP_Charge_History_UU.
		@param JP_Charge_History_UU JP_Charge_History_UU
	*/
	public void setJP_Charge_History_UU (String JP_Charge_History_UU)
	{
		set_Value (COLUMNNAME_JP_Charge_History_UU, JP_Charge_History_UU);
	}

	/** Get JP_Charge_History_UU.
		@return JP_Charge_History_UU	  */
	public String getJP_Charge_History_UU()
	{
		return (String)get_Value(COLUMNNAME_JP_Charge_History_UU);
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
}