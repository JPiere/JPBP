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

/** Generated Model for JP_PaymentExportClass
 *  @author iDempiere (generated)
 *  @version Release 11 - $Id$ */
@org.adempiere.base.Model(table="JP_PaymentExportClass")
public class X_JP_PaymentExportClass extends PO implements I_JP_PaymentExportClass, I_Persistent
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20240509L;

    /** Standard Constructor */
    public X_JP_PaymentExportClass (Properties ctx, int JP_PaymentExportClass_ID, String trxName)
    {
      super (ctx, JP_PaymentExportClass_ID, trxName);
      /** if (JP_PaymentExportClass_ID == 0)
        {
			setIsReceipt (false);
// N
			setJP_PaymentExportClass_ID (0);
			setName (null);
			setPaymentExportClass (null);
        } */
    }

    /** Standard Constructor */
    public X_JP_PaymentExportClass (Properties ctx, int JP_PaymentExportClass_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, JP_PaymentExportClass_ID, trxName, virtualColumns);
      /** if (JP_PaymentExportClass_ID == 0)
        {
			setIsReceipt (false);
// N
			setJP_PaymentExportClass_ID (0);
			setName (null);
			setPaymentExportClass (null);
        } */
    }

    /** Standard Constructor */
    public X_JP_PaymentExportClass (Properties ctx, String JP_PaymentExportClass_UU, String trxName)
    {
      super (ctx, JP_PaymentExportClass_UU, trxName);
      /** if (JP_PaymentExportClass_UU == null)
        {
			setIsReceipt (false);
// N
			setJP_PaymentExportClass_ID (0);
			setName (null);
			setPaymentExportClass (null);
        } */
    }

    /** Standard Constructor */
    public X_JP_PaymentExportClass (Properties ctx, String JP_PaymentExportClass_UU, String trxName, String ... virtualColumns)
    {
      super (ctx, JP_PaymentExportClass_UU, trxName, virtualColumns);
      /** if (JP_PaymentExportClass_UU == null)
        {
			setIsReceipt (false);
// N
			setJP_PaymentExportClass_ID (0);
			setName (null);
			setPaymentExportClass (null);
        } */
    }

    /** Load Constructor */
    public X_JP_PaymentExportClass (Properties ctx, ResultSet rs, String trxName)
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
      StringBuilder sb = new StringBuilder ("X_JP_PaymentExportClass[")
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

	/** Set Receipt.
		@param IsReceipt This is a sales transaction (receipt)
	*/
	public void setIsReceipt (boolean IsReceipt)
	{
		set_Value (COLUMNNAME_IsReceipt, Boolean.valueOf(IsReceipt));
	}

	/** Get Receipt.
		@return This is a sales transaction (receipt)
	  */
	public boolean isReceipt()
	{
		Object oo = get_Value(COLUMNNAME_IsReceipt);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/** CRLF = CL */
	public static final String JP_LINEEND_CRLF = "CL";
	/** CR = CR */
	public static final String JP_LINEEND_CR = "CR";
	/** LF = LF */
	public static final String JP_LINEEND_LF = "LF";
	/** Set Line End.
		@param JP_LineEnd Line End
	*/
	public void setJP_LineEnd (String JP_LineEnd)
	{

		set_Value (COLUMNNAME_JP_LineEnd, JP_LineEnd);
	}

	/** Get Line End.
		@return Line End	  */
	public String getJP_LineEnd()
	{
		return (String)get_Value(COLUMNNAME_JP_LineEnd);
	}

	/** Set Payment Export Class.
		@param JP_PaymentExportClass_ID JPIERE-0615:JPBP
	*/
	public void setJP_PaymentExportClass_ID (int JP_PaymentExportClass_ID)
	{
		if (JP_PaymentExportClass_ID < 1)
			set_ValueNoCheck (COLUMNNAME_JP_PaymentExportClass_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_JP_PaymentExportClass_ID, Integer.valueOf(JP_PaymentExportClass_ID));
	}

	/** Get Payment Export Class.
		@return JPIERE-0615:JPBP
	  */
	public int getJP_PaymentExportClass_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_PaymentExportClass_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Payment Export Class UU.
		@param JP_PaymentExportClass_UU Payment Export Class UU
	*/
	public void setJP_PaymentExportClass_UU (String JP_PaymentExportClass_UU)
	{
		set_Value (COLUMNNAME_JP_PaymentExportClass_UU, JP_PaymentExportClass_UU);
	}

	/** Get Payment Export Class UU.
		@return Payment Export Class UU	  */
	public String getJP_PaymentExportClass_UU()
	{
		return (String)get_Value(COLUMNNAME_JP_PaymentExportClass_UU);
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

	/** Set Payment Export Class.
		@param PaymentExportClass Payment Export Class
	*/
	public void setPaymentExportClass (String PaymentExportClass)
	{
		set_Value (COLUMNNAME_PaymentExportClass, PaymentExportClass);
	}

	/** Get Payment Export Class.
		@return Payment Export Class	  */
	public String getPaymentExportClass()
	{
		return (String)get_Value(COLUMNNAME_PaymentExportClass);
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