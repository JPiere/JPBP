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

/** Generated Model for I_CorporationJP
 *  @author iDempiere (generated)
 *  @version Release 11 - $Id$ */
@org.adempiere.base.Model(table="I_CorporationJP")
public class X_I_CorporationJP extends PO implements I_I_CorporationJP, I_Persistent
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20241204L;

    /** Standard Constructor */
    public X_I_CorporationJP (Properties ctx, int I_CorporationJP_ID, String trxName)
    {
      super (ctx, I_CorporationJP_ID, trxName);
      /** if (I_CorporationJP_ID == 0)
        {
			setI_CorporationJP_ID (0);
        } */
    }

    /** Standard Constructor */
    public X_I_CorporationJP (Properties ctx, int I_CorporationJP_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, I_CorporationJP_ID, trxName, virtualColumns);
      /** if (I_CorporationJP_ID == 0)
        {
			setI_CorporationJP_ID (0);
        } */
    }

    /** Standard Constructor */
    public X_I_CorporationJP (Properties ctx, String I_CorporationJP_UU, String trxName)
    {
      super (ctx, I_CorporationJP_UU, trxName);
      /** if (I_CorporationJP_UU == null)
        {
			setI_CorporationJP_ID (0);
        } */
    }

    /** Standard Constructor */
    public X_I_CorporationJP (Properties ctx, String I_CorporationJP_UU, String trxName, String ... virtualColumns)
    {
      super (ctx, I_CorporationJP_UU, trxName, virtualColumns);
      /** if (I_CorporationJP_UU == null)
        {
			setI_CorporationJP_ID (0);
        } */
    }

    /** Load Constructor */
    public X_I_CorporationJP (Properties ctx, ResultSet rs, String trxName)
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
      StringBuilder sb = new StringBuilder ("X_I_CorporationJP[")
        .append(get_ID()).append(",Name=").append(getName()).append("]");
      return sb.toString();
    }

	/** Set BP Search Key.
		@param BPValue Business Partner Key Value
	*/
	public void setBPValue (String BPValue)
	{
		set_Value (COLUMNNAME_BPValue, BPValue);
	}

	/** Get BP Search Key.
		@return Business Partner Key Value
	  */
	public String getBPValue()
	{
		return (String)get_Value(COLUMNNAME_BPValue);
	}

	public org.compiere.model.I_C_BPartner getC_BPartner() throws RuntimeException
	{
		return (org.compiere.model.I_C_BPartner)MTable.get(getCtx(), org.compiere.model.I_C_BPartner.Table_ID)
			.getPO(getC_BPartner_ID(), get_TrxName());
	}

	/** Set Business Partner.
		@param C_BPartner_ID Identifies a Business Partner
	*/
	public void setC_BPartner_ID (int C_BPartner_ID)
	{
		if (C_BPartner_ID < 1)
			set_Value (COLUMNNAME_C_BPartner_ID, null);
		else
			set_Value (COLUMNNAME_C_BPartner_ID, Integer.valueOf(C_BPartner_ID));
	}

	/** Get Business Partner.
		@return Identifies a Business Partner
	  */
	public int getC_BPartner_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_BPartner_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set D-U-N-S.
		@param DUNS Dun &amp; Bradstreet Number
	*/
	public void setDUNS (String DUNS)
	{
		set_Value (COLUMNNAME_DUNS, DUNS);
	}

	/** Get D-U-N-S.
		@return Dun &amp; Bradstreet Number
	  */
	public String getDUNS()
	{
		return (String)get_Value(COLUMNNAME_DUNS);
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

	/** Set I_CorporationJP.
		@param I_CorporationJP_ID I_CorporationJP
	*/
	public void setI_CorporationJP_ID (int I_CorporationJP_ID)
	{
		if (I_CorporationJP_ID < 1)
			set_ValueNoCheck (COLUMNNAME_I_CorporationJP_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_I_CorporationJP_ID, Integer.valueOf(I_CorporationJP_ID));
	}

	/** Get I_CorporationJP.
		@return I_CorporationJP	  */
	public int getI_CorporationJP_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_I_CorporationJP_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set I_CorporationJP_UU.
		@param I_CorporationJP_UU I_CorporationJP_UU
	*/
	public void setI_CorporationJP_UU (String I_CorporationJP_UU)
	{
		set_ValueNoCheck (COLUMNNAME_I_CorporationJP_UU, I_CorporationJP_UU);
	}

	/** Get I_CorporationJP_UU.
		@return I_CorporationJP_UU	  */
	public String getI_CorporationJP_UU()
	{
		return (String)get_Value(COLUMNNAME_I_CorporationJP_UU);
	}

	/** Set Import Error Message.
		@param I_ErrorMsg Messages generated from import process
	*/
	public void setI_ErrorMsg (String I_ErrorMsg)
	{
		set_Value (COLUMNNAME_I_ErrorMsg, I_ErrorMsg);
	}

	/** Get Import Error Message.
		@return Messages generated from import process
	  */
	public String getI_ErrorMsg()
	{
		return (String)get_Value(COLUMNNAME_I_ErrorMsg);
	}

	/** Set Imported.
		@param I_IsImported Has this import been processed
	*/
	public void setI_IsImported (boolean I_IsImported)
	{
		set_Value (COLUMNNAME_I_IsImported, Boolean.valueOf(I_IsImported));
	}

	/** Get Imported.
		@return Has this import been processed
	  */
	public boolean isI_IsImported()
	{
		Object oo = get_Value(COLUMNNAME_I_IsImported);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	public I_JP_Corporation getJP_Corporation() throws RuntimeException
	{
		return (I_JP_Corporation)MTable.get(getCtx(), I_JP_Corporation.Table_ID)
			.getPO(getJP_Corporation_ID(), get_TrxName());
	}

	/** Set Corporation.
		@param JP_Corporation_ID Corporation
	*/
	public void setJP_Corporation_ID (int JP_Corporation_ID)
	{
		if (JP_Corporation_ID < 1)
			set_Value (COLUMNNAME_JP_Corporation_ID, null);
		else
			set_Value (COLUMNNAME_JP_Corporation_ID, Integer.valueOf(JP_Corporation_ID));
	}

	/** Get Corporation.
		@return Corporation	  */
	public int getJP_Corporation_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Corporation_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
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

	/** Set Name 2.
		@param Name2 Additional Name
	*/
	public void setName2 (String Name2)
	{
		set_Value (COLUMNNAME_Name2, Name2);
	}

	/** Get Name 2.
		@return Additional Name
	  */
	public String getName2()
	{
		return (String)get_Value(COLUMNNAME_Name2);
	}

	/** Set Processed.
		@param Processed The document has been processed
	*/
	public void setProcessed (boolean Processed)
	{
		set_Value (COLUMNNAME_Processed, Boolean.valueOf(Processed));
	}

	/** Get Processed.
		@return The document has been processed
	  */
	public boolean isProcessed()
	{
		Object oo = get_Value(COLUMNNAME_Processed);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Process Now.
		@param Processing Process Now
	*/
	public void setProcessing (boolean Processing)
	{
		set_Value (COLUMNNAME_Processing, Boolean.valueOf(Processing));
	}

	/** Get Process Now.
		@return Process Now	  */
	public boolean isProcessing()
	{
		Object oo = get_Value(COLUMNNAME_Processing);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
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