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

/** Generated Model for JP_CM_BPartner_Location
 *  @author iDempiere (generated)
 *  @version Release 11 - $Id$ */
@org.adempiere.base.Model(table="JP_CM_BPartner_Location")
public class X_JP_CM_BPartner_Location extends PO implements I_JP_CM_BPartner_Location, I_Persistent
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20241228L;

    /** Standard Constructor */
    public X_JP_CM_BPartner_Location (Properties ctx, int JP_CM_BPartner_Location_ID, String trxName)
    {
      super (ctx, JP_CM_BPartner_Location_ID, trxName);
      /** if (JP_CM_BPartner_Location_ID == 0)
        {
			setC_Location_ID (0);
			setIsBillTo (true);
// Y
			setIsPayFrom (true);
// Y
			setIsPreserveCustomName (false);
// N
			setIsRemitTo (true);
// Y
			setIsShipTo (true);
// Y
			setJP_CM_BPartner_ID (0);
			setJP_CM_BPartner_Location_ID (0);
			setName (null);
        } */
    }

    /** Standard Constructor */
    public X_JP_CM_BPartner_Location (Properties ctx, int JP_CM_BPartner_Location_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, JP_CM_BPartner_Location_ID, trxName, virtualColumns);
      /** if (JP_CM_BPartner_Location_ID == 0)
        {
			setC_Location_ID (0);
			setIsBillTo (true);
// Y
			setIsPayFrom (true);
// Y
			setIsPreserveCustomName (false);
// N
			setIsRemitTo (true);
// Y
			setIsShipTo (true);
// Y
			setJP_CM_BPartner_ID (0);
			setJP_CM_BPartner_Location_ID (0);
			setName (null);
        } */
    }

    /** Standard Constructor */
    public X_JP_CM_BPartner_Location (Properties ctx, String JP_CM_BPartner_Location_UU, String trxName)
    {
      super (ctx, JP_CM_BPartner_Location_UU, trxName);
      /** if (JP_CM_BPartner_Location_UU == null)
        {
			setC_Location_ID (0);
			setIsBillTo (true);
// Y
			setIsPayFrom (true);
// Y
			setIsPreserveCustomName (false);
// N
			setIsRemitTo (true);
// Y
			setIsShipTo (true);
// Y
			setJP_CM_BPartner_ID (0);
			setJP_CM_BPartner_Location_ID (0);
			setName (null);
        } */
    }

    /** Standard Constructor */
    public X_JP_CM_BPartner_Location (Properties ctx, String JP_CM_BPartner_Location_UU, String trxName, String ... virtualColumns)
    {
      super (ctx, JP_CM_BPartner_Location_UU, trxName, virtualColumns);
      /** if (JP_CM_BPartner_Location_UU == null)
        {
			setC_Location_ID (0);
			setIsBillTo (true);
// Y
			setIsPayFrom (true);
// Y
			setIsPreserveCustomName (false);
// N
			setIsRemitTo (true);
// Y
			setIsShipTo (true);
// Y
			setJP_CM_BPartner_ID (0);
			setJP_CM_BPartner_Location_ID (0);
			setName (null);
        } */
    }

    /** Load Constructor */
    public X_JP_CM_BPartner_Location (Properties ctx, ResultSet rs, String trxName)
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
      StringBuilder sb = new StringBuilder ("X_JP_CM_BPartner_Location[")
        .append(get_ID()).append(",Name=").append(getName()).append("]");
      return sb.toString();
    }

	public org.compiere.model.I_C_Location getC_Location() throws RuntimeException
	{
		return (org.compiere.model.I_C_Location)MTable.get(getCtx(), org.compiere.model.I_C_Location.Table_ID)
			.getPO(getC_Location_ID(), get_TrxName());
	}

	/** Set Address.
		@param C_Location_ID Location or Address
	*/
	public void setC_Location_ID (int C_Location_ID)
	{
		if (C_Location_ID < 1)
			set_Value (COLUMNNAME_C_Location_ID, null);
		else
			set_Value (COLUMNNAME_C_Location_ID, Integer.valueOf(C_Location_ID));
	}

	/** Get Address.
		@return Location or Address
	  */
	public int getC_Location_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Location_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Customer Address ID.
		@param CustomerAddressID Customer Address ID
	*/
	public void setCustomerAddressID (String CustomerAddressID)
	{
		set_Value (COLUMNNAME_CustomerAddressID, CustomerAddressID);
	}

	/** Get Customer Address ID.
		@return Customer Address ID	  */
	public String getCustomerAddressID()
	{
		return (String)get_Value(COLUMNNAME_CustomerAddressID);
	}

	/** Set Fax.
		@param Fax Facsimile number
	*/
	public void setFax (String Fax)
	{
		set_Value (COLUMNNAME_Fax, Fax);
	}

	/** Get Fax.
		@return Facsimile number
	  */
	public String getFax()
	{
		return (String)get_Value(COLUMNNAME_Fax);
	}

	/** Set Invoice Address.
		@param IsBillTo Business Partner Invoice/Bill Address
	*/
	public void setIsBillTo (boolean IsBillTo)
	{
		set_Value (COLUMNNAME_IsBillTo, Boolean.valueOf(IsBillTo));
	}

	/** Get Invoice Address.
		@return Business Partner Invoice/Bill Address
	  */
	public boolean isBillTo()
	{
		Object oo = get_Value(COLUMNNAME_IsBillTo);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Pay-From Address.
		@param IsPayFrom Business Partner pays from that address and we&#039;ll send dunning letters there
	*/
	public void setIsPayFrom (boolean IsPayFrom)
	{
		set_Value (COLUMNNAME_IsPayFrom, Boolean.valueOf(IsPayFrom));
	}

	/** Get Pay-From Address.
		@return Business Partner pays from that address and we&#039;ll send dunning letters there
	  */
	public boolean isPayFrom()
	{
		Object oo = get_Value(COLUMNNAME_IsPayFrom);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Preserve custom name.
		@param IsPreserveCustomName Preserve custom name
	*/
	public void setIsPreserveCustomName (boolean IsPreserveCustomName)
	{
		set_Value (COLUMNNAME_IsPreserveCustomName, Boolean.valueOf(IsPreserveCustomName));
	}

	/** Get Preserve custom name.
		@return Preserve custom name	  */
	public boolean isPreserveCustomName()
	{
		Object oo = get_Value(COLUMNNAME_IsPreserveCustomName);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Remit-To Address.
		@param IsRemitTo Business Partner payment address
	*/
	public void setIsRemitTo (boolean IsRemitTo)
	{
		set_Value (COLUMNNAME_IsRemitTo, Boolean.valueOf(IsRemitTo));
	}

	/** Get Remit-To Address.
		@return Business Partner payment address
	  */
	public boolean isRemitTo()
	{
		Object oo = get_Value(COLUMNNAME_IsRemitTo);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Ship Address.
		@param IsShipTo Business Partner Shipment Address
	*/
	public void setIsShipTo (boolean IsShipTo)
	{
		set_Value (COLUMNNAME_IsShipTo, Boolean.valueOf(IsShipTo));
	}

	/** Get Ship Address.
		@return Business Partner Shipment Address
	  */
	public boolean isShipTo()
	{
		Object oo = get_Value(COLUMNNAME_IsShipTo);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	public I_JP_CM_BPartner getJP_CM_BPartner() throws RuntimeException
	{
		return (I_JP_CM_BPartner)MTable.get(getCtx(), I_JP_CM_BPartner.Table_ID)
			.getPO(getJP_CM_BPartner_ID(), get_TrxName());
	}

	/** Set Consolidated Business Partner.
		@param JP_CM_BPartner_ID JPIERE-0636:JPPS
	*/
	public void setJP_CM_BPartner_ID (int JP_CM_BPartner_ID)
	{
		if (JP_CM_BPartner_ID < 1)
			set_ValueNoCheck (COLUMNNAME_JP_CM_BPartner_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_JP_CM_BPartner_ID, Integer.valueOf(JP_CM_BPartner_ID));
	}

	/** Get Consolidated Business Partner.
		@return JPIERE-0636:JPPS
	  */
	public int getJP_CM_BPartner_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_CM_BPartner_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Consolidated Partner Location.
		@param JP_CM_BPartner_Location_ID JPIERE-0636:JPPS
	*/
	public void setJP_CM_BPartner_Location_ID (int JP_CM_BPartner_Location_ID)
	{
		if (JP_CM_BPartner_Location_ID < 1)
			set_ValueNoCheck (COLUMNNAME_JP_CM_BPartner_Location_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_JP_CM_BPartner_Location_ID, Integer.valueOf(JP_CM_BPartner_Location_ID));
	}

	/** Get Consolidated Partner Location.
		@return JPIERE-0636:JPPS
	  */
	public int getJP_CM_BPartner_Location_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_CM_BPartner_Location_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Consolidated Partner Location.
		@param JP_CM_BPartner_Location_UU Consolidated Partner Location
	*/
	public void setJP_CM_BPartner_Location_UU (String JP_CM_BPartner_Location_UU)
	{
		set_Value (COLUMNNAME_JP_CM_BPartner_Location_UU, JP_CM_BPartner_Location_UU);
	}

	/** Get Consolidated Partner Location.
		@return Consolidated Partner Location	  */
	public String getJP_CM_BPartner_Location_UU()
	{
		return (String)get_Value(COLUMNNAME_JP_CM_BPartner_Location_UU);
	}

	/** Set Sales Region(Search Key).
		@param JP_SalesRegion_Value Sales coverage region
	*/
	public void setJP_SalesRegion_Value (String JP_SalesRegion_Value)
	{
		set_Value (COLUMNNAME_JP_SalesRegion_Value, JP_SalesRegion_Value);
	}

	/** Get Sales Region(Search Key).
		@return Sales coverage region
	  */
	public String getJP_SalesRegion_Value()
	{
		return (String)get_Value(COLUMNNAME_JP_SalesRegion_Value);
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

	/** Set Phone.
		@param Phone Identifies a telephone number
	*/
	public void setPhone (String Phone)
	{
		set_Value (COLUMNNAME_Phone, Phone);
	}

	/** Get Phone.
		@return Identifies a telephone number
	  */
	public String getPhone()
	{
		return (String)get_Value(COLUMNNAME_Phone);
	}

	/** Set 2nd Phone.
		@param Phone2 Identifies an alternate telephone number.
	*/
	public void setPhone2 (String Phone2)
	{
		set_Value (COLUMNNAME_Phone2, Phone2);
	}

	/** Get 2nd Phone.
		@return Identifies an alternate telephone number.
	  */
	public String getPhone2()
	{
		return (String)get_Value(COLUMNNAME_Phone2);
	}
}