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

/** Generated Model for JP_PaymentTerm_History
 *  @author iDempiere (generated) 
 *  @version Release 9 - $Id$ */
@org.adempiere.base.Model(table="JP_PaymentTerm_History")
public class X_JP_PaymentTerm_History extends PO implements I_JP_PaymentTerm_History, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20220725L;

    /** Standard Constructor */
    public X_JP_PaymentTerm_History (Properties ctx, int JP_PaymentTerm_History_ID, String trxName)
    {
      super (ctx, JP_PaymentTerm_History_ID, trxName);
      /** if (JP_PaymentTerm_History_ID == 0)
        {
			setC_PaymentTerm_ID (0);
			setDateFrom (new Timestamp( System.currentTimeMillis() ));
			setDateTo (new Timestamp( System.currentTimeMillis() ));
			setJP_PaymentTerm_History_ID (0);
			setName (null);
        } */
    }

    /** Standard Constructor */
    public X_JP_PaymentTerm_History (Properties ctx, int JP_PaymentTerm_History_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, JP_PaymentTerm_History_ID, trxName, virtualColumns);
      /** if (JP_PaymentTerm_History_ID == 0)
        {
			setC_PaymentTerm_ID (0);
			setDateFrom (new Timestamp( System.currentTimeMillis() ));
			setDateTo (new Timestamp( System.currentTimeMillis() ));
			setJP_PaymentTerm_History_ID (0);
			setName (null);
        } */
    }

    /** Load Constructor */
    public X_JP_PaymentTerm_History (Properties ctx, ResultSet rs, String trxName)
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
      StringBuilder sb = new StringBuilder ("X_JP_PaymentTerm_History[")
        .append(get_ID()).append(",Name=").append(getName()).append("]");
      return sb.toString();
    }

	public org.compiere.model.I_C_PaymentTerm getC_PaymentTerm() throws RuntimeException
	{
		return (org.compiere.model.I_C_PaymentTerm)MTable.get(getCtx(), org.compiere.model.I_C_PaymentTerm.Table_ID)
			.getPO(getC_PaymentTerm_ID(), get_TrxName());
	}

	/** Set Payment Term.
		@param C_PaymentTerm_ID The terms of Payment (timing, discount)
	*/
	public void setC_PaymentTerm_ID (int C_PaymentTerm_ID)
	{
		if (C_PaymentTerm_ID < 1)
			set_ValueNoCheck (COLUMNNAME_C_PaymentTerm_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_C_PaymentTerm_ID, Integer.valueOf(C_PaymentTerm_ID));
	}

	/** Get Payment Term.
		@return The terms of Payment (timing, discount)
	  */
	public int getC_PaymentTerm_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_PaymentTerm_ID);
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

	/** Set JP_PaymentTerm_History.
		@param JP_PaymentTerm_History_ID JPIERE-0564
	*/
	public void setJP_PaymentTerm_History_ID (int JP_PaymentTerm_History_ID)
	{
		if (JP_PaymentTerm_History_ID < 1)
			set_ValueNoCheck (COLUMNNAME_JP_PaymentTerm_History_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_JP_PaymentTerm_History_ID, Integer.valueOf(JP_PaymentTerm_History_ID));
	}

	/** Get JP_PaymentTerm_History.
		@return JPIERE-0564
	  */
	public int getJP_PaymentTerm_History_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_PaymentTerm_History_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set JP_PaymentTerm_History_UU.
		@param JP_PaymentTerm_History_UU JP_PaymentTerm_History_UU
	*/
	public void setJP_PaymentTerm_History_UU (String JP_PaymentTerm_History_UU)
	{
		set_Value (COLUMNNAME_JP_PaymentTerm_History_UU, JP_PaymentTerm_History_UU);
	}

	/** Get JP_PaymentTerm_History_UU.
		@return JP_PaymentTerm_History_UU	  */
	public String getJP_PaymentTerm_History_UU()
	{
		return (String)get_Value(COLUMNNAME_JP_PaymentTerm_History_UU);
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