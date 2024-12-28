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

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;
import org.compiere.model.*;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;

/** Generated Model for I_BPartnerJP
 *  @author iDempiere (generated)
 *  @version Release 11 - $Id$ */
@org.adempiere.base.Model(table="I_BPartnerJP")
public class X_I_BPartnerJP extends PO implements I_I_BPartnerJP, I_Persistent
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20241228L;

    /** Standard Constructor */
    public X_I_BPartnerJP (Properties ctx, int I_BPartnerJP_ID, String trxName)
    {
      super (ctx, I_BPartnerJP_ID, trxName);
      /** if (I_BPartnerJP_ID == 0)
        {
			setI_BPartnerJP_ID (0);
			setI_IsImported (false);
// N
        } */
    }

    /** Standard Constructor */
    public X_I_BPartnerJP (Properties ctx, int I_BPartnerJP_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, I_BPartnerJP_ID, trxName, virtualColumns);
      /** if (I_BPartnerJP_ID == 0)
        {
			setI_BPartnerJP_ID (0);
			setI_IsImported (false);
// N
        } */
    }

    /** Standard Constructor */
    public X_I_BPartnerJP (Properties ctx, String I_BPartnerJP_UU, String trxName)
    {
      super (ctx, I_BPartnerJP_UU, trxName);
      /** if (I_BPartnerJP_UU == null)
        {
			setI_BPartnerJP_ID (0);
			setI_IsImported (false);
// N
        } */
    }

    /** Standard Constructor */
    public X_I_BPartnerJP (Properties ctx, String I_BPartnerJP_UU, String trxName, String ... virtualColumns)
    {
      super (ctx, I_BPartnerJP_UU, trxName, virtualColumns);
      /** if (I_BPartnerJP_UU == null)
        {
			setI_BPartnerJP_ID (0);
			setI_IsImported (false);
// N
        } */
    }

    /** Load Constructor */
    public X_I_BPartnerJP (Properties ctx, ResultSet rs, String trxName)
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
      StringBuilder sb = new StringBuilder ("X_I_BPartnerJP[")
        .append(get_ID()).append(",Name=").append(getName()).append("]");
      return sb.toString();
    }

	/** AD_Language AD_Reference_ID=106 */
	public static final int AD_LANGUAGE_AD_Reference_ID=106;
	/** Set Language.
		@param AD_Language Language for this entity
	*/
	public void setAD_Language (String AD_Language)
	{

		set_Value (COLUMNNAME_AD_Language, AD_Language);
	}

	/** Get Language.
		@return Language for this entity
	  */
	public String getAD_Language()
	{
		return (String)get_Value(COLUMNNAME_AD_Language);
	}

	public org.compiere.model.I_AD_User getAD_User() throws RuntimeException
	{
		return (org.compiere.model.I_AD_User)MTable.get(getCtx(), org.compiere.model.I_AD_User.Table_ID)
			.getPO(getAD_User_ID(), get_TrxName());
	}

	/** Set User/Contact.
		@param AD_User_ID User within the system - Internal or Business Partner Contact
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
	public int getAD_User_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_User_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Acquisition Cost.
		@param AcqusitionCost The cost of gaining the prospect as a customer
	*/
	public void setAcqusitionCost (BigDecimal AcqusitionCost)
	{
		set_Value (COLUMNNAME_AcqusitionCost, AcqusitionCost);
	}

	/** Get Acquisition Cost.
		@return The cost of gaining the prospect as a customer
	  */
	public BigDecimal getAcqusitionCost()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_AcqusitionCost);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Actual Life Time Value.
		@param ActualLifeTimeValue Actual Life Time Revenue
	*/
	public void setActualLifeTimeValue (BigDecimal ActualLifeTimeValue)
	{
		set_Value (COLUMNNAME_ActualLifeTimeValue, ActualLifeTimeValue);
	}

	/** Get Actual Life Time Value.
		@return Actual Life Time Revenue
	  */
	public BigDecimal getActualLifeTimeValue()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_ActualLifeTimeValue);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Address 1.
		@param Address1 Address line 1 for this location
	*/
	public void setAddress1 (String Address1)
	{
		set_Value (COLUMNNAME_Address1, Address1);
	}

	/** Get Address 1.
		@return Address line 1 for this location
	  */
	public String getAddress1()
	{
		return (String)get_Value(COLUMNNAME_Address1);
	}

	/** Set Address 2.
		@param Address2 Address line 2 for this location
	*/
	public void setAddress2 (String Address2)
	{
		set_Value (COLUMNNAME_Address2, Address2);
	}

	/** Get Address 2.
		@return Address line 2 for this location
	  */
	public String getAddress2()
	{
		return (String)get_Value(COLUMNNAME_Address2);
	}

	/** Set Address 3.
		@param Address3 Address Line 3 for the location
	*/
	public void setAddress3 (String Address3)
	{
		set_Value (COLUMNNAME_Address3, Address3);
	}

	/** Get Address 3.
		@return Address Line 3 for the location
	  */
	public String getAddress3()
	{
		return (String)get_Value(COLUMNNAME_Address3);
	}

	/** Set Address 4.
		@param Address4 Address Line 4 for the location
	*/
	public void setAddress4 (String Address4)
	{
		set_Value (COLUMNNAME_Address4, Address4);
	}

	/** Get Address 4.
		@return Address Line 4 for the location
	  */
	public String getAddress4()
	{
		return (String)get_Value(COLUMNNAME_Address4);
	}

	/** Set Address 5.
		@param Address5 Address Line 5 for the location
	*/
	public void setAddress5 (String Address5)
	{
		set_Value (COLUMNNAME_Address5, Address5);
	}

	/** Get Address 5.
		@return Address Line 5 for the location
	  */
	public String getAddress5()
	{
		return (String)get_Value(COLUMNNAME_Address5);
	}

	/** Set BP Contact Greeting.
		@param BPContactGreeting Greeting for Business Partner Contact
	*/
	public void setBPContactGreeting (String BPContactGreeting)
	{
		set_Value (COLUMNNAME_BPContactGreeting, BPContactGreeting);
	}

	/** Get BP Contact Greeting.
		@return Greeting for Business Partner Contact
	  */
	public String getBPContactGreeting()
	{
		return (String)get_Value(COLUMNNAME_BPContactGreeting);
	}

	/** Set Birthday.
		@param Birthday Birthday or Anniversary day
	*/
	public void setBirthday (Timestamp Birthday)
	{
		set_Value (COLUMNNAME_Birthday, Birthday);
	}

	/** Get Birthday.
		@return Birthday or Anniversary day
	  */
	public Timestamp getBirthday()
	{
		return (Timestamp)get_Value(COLUMNNAME_Birthday);
	}

	public org.compiere.model.I_C_BP_Group getC_BP_Group() throws RuntimeException
	{
		return (org.compiere.model.I_C_BP_Group)MTable.get(getCtx(), org.compiere.model.I_C_BP_Group.Table_ID)
			.getPO(getC_BP_Group_ID(), get_TrxName());
	}

	/** Set BPartner Group.
		@param C_BP_Group_ID Business Partner Group
	*/
	public void setC_BP_Group_ID (int C_BP_Group_ID)
	{
		if (C_BP_Group_ID < 1)
			set_Value (COLUMNNAME_C_BP_Group_ID, null);
		else
			set_Value (COLUMNNAME_C_BP_Group_ID, Integer.valueOf(C_BP_Group_ID));
	}

	/** Get BPartner Group.
		@return Business Partner Group
	  */
	public int getC_BP_Group_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_BP_Group_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
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

	public org.compiere.model.I_C_BPartner_Location getC_BPartner_Location() throws RuntimeException
	{
		return (org.compiere.model.I_C_BPartner_Location)MTable.get(getCtx(), org.compiere.model.I_C_BPartner_Location.Table_ID)
			.getPO(getC_BPartner_Location_ID(), get_TrxName());
	}

	/** Set Partner Location.
		@param C_BPartner_Location_ID Identifies the (ship to) address for this Business Partner
	*/
	public void setC_BPartner_Location_ID (int C_BPartner_Location_ID)
	{
		if (C_BPartner_Location_ID < 1)
			set_Value (COLUMNNAME_C_BPartner_Location_ID, null);
		else
			set_Value (COLUMNNAME_C_BPartner_Location_ID, Integer.valueOf(C_BPartner_Location_ID));
	}

	/** Get Partner Location.
		@return Identifies the (ship to) address for this Business Partner
	  */
	public int getC_BPartner_Location_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_BPartner_Location_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_City getC_City() throws RuntimeException
	{
		return (org.compiere.model.I_C_City)MTable.get(getCtx(), org.compiere.model.I_C_City.Table_ID)
			.getPO(getC_City_ID(), get_TrxName());
	}

	/** Set City.
		@param C_City_ID City
	*/
	public void setC_City_ID (int C_City_ID)
	{
		if (C_City_ID < 1)
			set_Value (COLUMNNAME_C_City_ID, null);
		else
			set_Value (COLUMNNAME_C_City_ID, Integer.valueOf(C_City_ID));
	}

	/** Get City.
		@return City
	  */
	public int getC_City_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_City_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Country getC_Country() throws RuntimeException
	{
		return (org.compiere.model.I_C_Country)MTable.get(getCtx(), org.compiere.model.I_C_Country.Table_ID)
			.getPO(getC_Country_ID(), get_TrxName());
	}

	/** Set Country.
		@param C_Country_ID Country 
	*/
	public void setC_Country_ID (int C_Country_ID)
	{
		if (C_Country_ID < 1)
			set_Value (COLUMNNAME_C_Country_ID, null);
		else
			set_Value (COLUMNNAME_C_Country_ID, Integer.valueOf(C_Country_ID));
	}

	/** Get Country.
		@return Country 
	  */
	public int getC_Country_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Country_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Dunning getC_Dunning() throws RuntimeException
	{
		return (org.compiere.model.I_C_Dunning)MTable.get(getCtx(), org.compiere.model.I_C_Dunning.Table_ID)
			.getPO(getC_Dunning_ID(), get_TrxName());
	}

	/** Set Dunning.
		@param C_Dunning_ID Dunning Rules for overdue invoices
	*/
	public void setC_Dunning_ID (int C_Dunning_ID)
	{
		if (C_Dunning_ID < 1)
			set_Value (COLUMNNAME_C_Dunning_ID, null);
		else
			set_Value (COLUMNNAME_C_Dunning_ID, Integer.valueOf(C_Dunning_ID));
	}

	/** Get Dunning.
		@return Dunning Rules for overdue invoices
	  */
	public int getC_Dunning_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Dunning_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Greeting getC_Greeting() throws RuntimeException
	{
		return (org.compiere.model.I_C_Greeting)MTable.get(getCtx(), org.compiere.model.I_C_Greeting.Table_ID)
			.getPO(getC_Greeting_ID(), get_TrxName());
	}

	/** Set Greeting.
		@param C_Greeting_ID Greeting to print on correspondence
	*/
	public void setC_Greeting_ID (int C_Greeting_ID)
	{
		if (C_Greeting_ID < 1)
			set_Value (COLUMNNAME_C_Greeting_ID, null);
		else
			set_Value (COLUMNNAME_C_Greeting_ID, Integer.valueOf(C_Greeting_ID));
	}

	/** Get Greeting.
		@return Greeting to print on correspondence
	  */
	public int getC_Greeting_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Greeting_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_InvoiceSchedule getC_InvoiceSchedule() throws RuntimeException
	{
		return (org.compiere.model.I_C_InvoiceSchedule)MTable.get(getCtx(), org.compiere.model.I_C_InvoiceSchedule.Table_ID)
			.getPO(getC_InvoiceSchedule_ID(), get_TrxName());
	}

	/** Set Invoice Schedule.
		@param C_InvoiceSchedule_ID Schedule for generating Invoices
	*/
	public void setC_InvoiceSchedule_ID (int C_InvoiceSchedule_ID)
	{
		if (C_InvoiceSchedule_ID < 1)
			set_Value (COLUMNNAME_C_InvoiceSchedule_ID, null);
		else
			set_Value (COLUMNNAME_C_InvoiceSchedule_ID, Integer.valueOf(C_InvoiceSchedule_ID));
	}

	/** Get Invoice Schedule.
		@return Schedule for generating Invoices
	  */
	public int getC_InvoiceSchedule_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_InvoiceSchedule_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
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
			set_Value (COLUMNNAME_C_PaymentTerm_ID, null);
		else
			set_Value (COLUMNNAME_C_PaymentTerm_ID, Integer.valueOf(C_PaymentTerm_ID));
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

	public org.compiere.model.I_C_Region getC_Region() throws RuntimeException
	{
		return (org.compiere.model.I_C_Region)MTable.get(getCtx(), org.compiere.model.I_C_Region.Table_ID)
			.getPO(getC_Region_ID(), get_TrxName());
	}

	/** Set Region.
		@param C_Region_ID Identifies a geographical Region
	*/
	public void setC_Region_ID (int C_Region_ID)
	{
		if (C_Region_ID < 1)
			set_Value (COLUMNNAME_C_Region_ID, null);
		else
			set_Value (COLUMNNAME_C_Region_ID, Integer.valueOf(C_Region_ID));
	}

	/** Get Region.
		@return Identifies a geographical Region
	  */
	public int getC_Region_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Region_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_SalesRegion getC_SalesRegion() throws RuntimeException
	{
		return (org.compiere.model.I_C_SalesRegion)MTable.get(getCtx(), org.compiere.model.I_C_SalesRegion.Table_ID)
			.getPO(getC_SalesRegion_ID(), get_TrxName());
	}

	/** Set Sales Region.
		@param C_SalesRegion_ID Sales coverage region
	*/
	public void setC_SalesRegion_ID (int C_SalesRegion_ID)
	{
		if (C_SalesRegion_ID < 1)
			set_Value (COLUMNNAME_C_SalesRegion_ID, null);
		else
			set_Value (COLUMNNAME_C_SalesRegion_ID, Integer.valueOf(C_SalesRegion_ID));
	}

	/** Get Sales Region.
		@return Sales coverage region
	  */
	public int getC_SalesRegion_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_SalesRegion_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set City.
		@param City Identifies a City
	*/
	public void setCity (String City)
	{
		set_Value (COLUMNNAME_City, City);
	}

	/** Get City.
		@return Identifies a City
	  */
	public String getCity()
	{
		return (String)get_Value(COLUMNNAME_City);
	}

	/** Set Comments.
		@param Comments Comments or additional information
	*/
	public void setComments (String Comments)
	{
		set_Value (COLUMNNAME_Comments, Comments);
	}

	/** Get Comments.
		@return Comments or additional information
	  */
	public String getComments()
	{
		return (String)get_Value(COLUMNNAME_Comments);
	}

	/** Set Contact Description.
		@param ContactDescription Description of Contact
	*/
	public void setContactDescription (String ContactDescription)
	{
		set_Value (COLUMNNAME_ContactDescription, ContactDescription);
	}

	/** Get Contact Description.
		@return Description of Contact
	  */
	public String getContactDescription()
	{
		return (String)get_Value(COLUMNNAME_ContactDescription);
	}

	/** Set Contact Name.
		@param ContactName Business Partner Contact Name
	*/
	public void setContactName (String ContactName)
	{
		set_Value (COLUMNNAME_ContactName, ContactName);
	}

	/** Get Contact Name.
		@return Business Partner Contact Name
	  */
	public String getContactName()
	{
		return (String)get_Value(COLUMNNAME_ContactName);
	}

	/** Set ISO Country Code.
		@param CountryCode Upper-case two-letter alphanumeric ISO Country code according to ISO 3166-1 - http://www.chemie.fu-berlin.de/diverse/doc/ISO_3166.html
	*/
	public void setCountryCode (String CountryCode)
	{
		set_Value (COLUMNNAME_CountryCode, CountryCode);
	}

	/** Get ISO Country Code.
		@return Upper-case two-letter alphanumeric ISO Country code according to ISO 3166-1 - http://www.chemie.fu-berlin.de/diverse/doc/ISO_3166.html
	  */
	public String getCountryCode()
	{
		return (String)get_Value(COLUMNNAME_CountryCode);
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

	public org.compiere.model.I_C_1099Box getDefault1099Box() throws RuntimeException
	{
		return (org.compiere.model.I_C_1099Box)MTable.get(getCtx(), org.compiere.model.I_C_1099Box.Table_ID)
			.getPO(getDefault1099Box_ID(), get_TrxName());
	}

	/** Set Default 1099 Box.
		@param Default1099Box_ID Default 1099 Box
	*/
	public void setDefault1099Box_ID (int Default1099Box_ID)
	{
		if (Default1099Box_ID < 1)
			set_Value (COLUMNNAME_Default1099Box_ID, null);
		else
			set_Value (COLUMNNAME_Default1099Box_ID, Integer.valueOf(Default1099Box_ID));
	}

	/** Get Default 1099 Box.
		@return Default 1099 Box	  */
	public int getDefault1099Box_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Default1099Box_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** DeliveryRule AD_Reference_ID=151 */
	public static final int DELIVERYRULE_AD_Reference_ID=151;
	/** Availability = A */
	public static final String DELIVERYRULE_Availability = "A";
	/** Force = F */
	public static final String DELIVERYRULE_Force = "F";
	/** Complete Line = L */
	public static final String DELIVERYRULE_CompleteLine = "L";
	/** Manual = M */
	public static final String DELIVERYRULE_Manual = "M";
	/** Complete Order = O */
	public static final String DELIVERYRULE_CompleteOrder = "O";
	/** After Payment = R */
	public static final String DELIVERYRULE_AfterPayment = "R";
	/** Set Delivery Rule.
		@param DeliveryRule Defines the timing of Delivery
	*/
	public void setDeliveryRule (String DeliveryRule)
	{

		set_Value (COLUMNNAME_DeliveryRule, DeliveryRule);
	}

	/** Get Delivery Rule.
		@return Defines the timing of Delivery
	  */
	public String getDeliveryRule()
	{
		return (String)get_Value(COLUMNNAME_DeliveryRule);
	}

	/** DeliveryViaRule AD_Reference_ID=152 */
	public static final int DELIVERYVIARULE_AD_Reference_ID=152;
	/** Delivery = D */
	public static final String DELIVERYVIARULE_Delivery = "D";
	/** Pickup = P */
	public static final String DELIVERYVIARULE_Pickup = "P";
	/** Shipper = S */
	public static final String DELIVERYVIARULE_Shipper = "S";
	/** Set Delivery Via.
		@param DeliveryViaRule How the order will be delivered
	*/
	public void setDeliveryViaRule (String DeliveryViaRule)
	{

		set_Value (COLUMNNAME_DeliveryViaRule, DeliveryViaRule);
	}

	/** Get Delivery Via.
		@return How the order will be delivered
	  */
	public String getDeliveryViaRule()
	{
		return (String)get_Value(COLUMNNAME_DeliveryViaRule);
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

	/** Set Document Copies.
		@param DocumentCopies Number of copies to be printed
	*/
	public void setDocumentCopies (int DocumentCopies)
	{
		set_Value (COLUMNNAME_DocumentCopies, Integer.valueOf(DocumentCopies));
	}

	/** Get Document Copies.
		@return Number of copies to be printed
	  */
	public int getDocumentCopies()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_DocumentCopies);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Dunning Grace Date.
		@param DunningGrace Dunning Grace Date
	*/
	public void setDunningGrace (Timestamp DunningGrace)
	{
		set_Value (COLUMNNAME_DunningGrace, DunningGrace);
	}

	/** Get Dunning Grace Date.
		@return Dunning Grace Date	  */
	public Timestamp getDunningGrace()
	{
		return (Timestamp)get_Value(COLUMNNAME_DunningGrace);
	}

	/** Set EMail Address.
		@param EMail Electronic Mail Address
	*/
	public void setEMail (String EMail)
	{
		set_Value (COLUMNNAME_EMail, EMail);
	}

	/** Get EMail Address.
		@return Electronic Mail Address
	  */
	public String getEMail()
	{
		return (String)get_Value(COLUMNNAME_EMail);
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

	/** Set First Sale.
		@param FirstSale Date of First Sale
	*/
	public void setFirstSale (Timestamp FirstSale)
	{
		set_Value (COLUMNNAME_FirstSale, FirstSale);
	}

	/** Get First Sale.
		@return Date of First Sale
	  */
	public Timestamp getFirstSale()
	{
		return (Timestamp)get_Value(COLUMNNAME_FirstSale);
	}

	/** Set Flat Discount %.
		@param FlatDiscount Flat discount percentage 
	*/
	public void setFlatDiscount (BigDecimal FlatDiscount)
	{
		set_Value (COLUMNNAME_FlatDiscount, FlatDiscount);
	}

	/** Get Flat Discount %.
		@return Flat discount percentage 
	  */
	public BigDecimal getFlatDiscount()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_FlatDiscount);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Group Key.
		@param GroupValue Business Partner Group Key
	*/
	public void setGroupValue (String GroupValue)
	{
		set_Value (COLUMNNAME_GroupValue, GroupValue);
	}

	/** Get Group Key.
		@return Business Partner Group Key
	  */
	public String getGroupValue()
	{
		return (String)get_Value(COLUMNNAME_GroupValue);
	}

	/** Set ISDN.
		@param ISDN ISDN or modem line
	*/
	public void setISDN (String ISDN)
	{
		set_Value (COLUMNNAME_ISDN, ISDN);
	}

	/** Get ISDN.
		@return ISDN or modem line
	  */
	public String getISDN()
	{
		return (String)get_Value(COLUMNNAME_ISDN);
	}

	/** Set JP Import Business Partner.
		@param I_BPartnerJP_ID JP Import Business Partner
	*/
	public void setI_BPartnerJP_ID (int I_BPartnerJP_ID)
	{
		if (I_BPartnerJP_ID < 1)
			set_ValueNoCheck (COLUMNNAME_I_BPartnerJP_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_I_BPartnerJP_ID, Integer.valueOf(I_BPartnerJP_ID));
	}

	/** Get JP Import Business Partner.
		@return JP Import Business Partner	  */
	public int getI_BPartnerJP_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_I_BPartnerJP_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set I_BPartnerJP_UU.
		@param I_BPartnerJP_UU I_BPartnerJP_UU
	*/
	public void setI_BPartnerJP_UU (String I_BPartnerJP_UU)
	{
		set_Value (COLUMNNAME_I_BPartnerJP_UU, I_BPartnerJP_UU);
	}

	/** Get I_BPartnerJP_UU.
		@return I_BPartnerJP_UU	  */
	public String getI_BPartnerJP_UU()
	{
		return (String)get_Value(COLUMNNAME_I_BPartnerJP_UU);
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

	/** Set Active(For Import).
		@param I_IsActiveJP Active flag for Import Date
	*/
	public void setI_IsActiveJP (boolean I_IsActiveJP)
	{
		set_Value (COLUMNNAME_I_IsActiveJP, Boolean.valueOf(I_IsActiveJP));
	}

	/** Get Active(For Import).
		@return Active flag for Import Date
	  */
	public boolean isI_IsActiveJP()
	{
		Object oo = get_Value(COLUMNNAME_I_IsActiveJP);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
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

	/** Set Interest Area.
		@param InterestAreaName Name of the Interest Area
	*/
	public void setInterestAreaName (String InterestAreaName)
	{
		set_Value (COLUMNNAME_InterestAreaName, InterestAreaName);
	}

	/** Get Interest Area.
		@return Name of the Interest Area
	  */
	public String getInterestAreaName()
	{
		return (String)get_Value(COLUMNNAME_InterestAreaName);
	}

	/** InvoiceRule AD_Reference_ID=150 */
	public static final int INVOICERULE_AD_Reference_ID=150;
	/** After Delivery = D */
	public static final String INVOICERULE_AfterDelivery = "D";
	/** Immediate = I */
	public static final String INVOICERULE_Immediate = "I";
	/** After Order delivered = O */
	public static final String INVOICERULE_AfterOrderDelivered = "O";
	/** Customer Schedule after Delivery = S */
	public static final String INVOICERULE_CustomerScheduleAfterDelivery = "S";
	/** Set Invoice Rule.
		@param InvoiceRule Frequency and method of invoicing 
	*/
	public void setInvoiceRule (String InvoiceRule)
	{

		set_Value (COLUMNNAME_InvoiceRule, InvoiceRule);
	}

	/** Get Invoice Rule.
		@return Frequency and method of invoicing 
	  */
	public String getInvoiceRule()
	{
		return (String)get_Value(COLUMNNAME_InvoiceRule);
	}

	public org.compiere.model.I_AD_PrintFormat getInvoice_PrintFormat() throws RuntimeException
	{
		return (org.compiere.model.I_AD_PrintFormat)MTable.get(getCtx(), org.compiere.model.I_AD_PrintFormat.Table_ID)
			.getPO(getInvoice_PrintFormat_ID(), get_TrxName());
	}

	/** Set Invoice Print Format.
		@param Invoice_PrintFormat_ID Print Format for printing Invoices
	*/
	public void setInvoice_PrintFormat_ID (int Invoice_PrintFormat_ID)
	{
		if (Invoice_PrintFormat_ID < 1)
			set_Value (COLUMNNAME_Invoice_PrintFormat_ID, null);
		else
			set_Value (COLUMNNAME_Invoice_PrintFormat_ID, Integer.valueOf(Invoice_PrintFormat_ID));
	}

	/** Get Invoice Print Format.
		@return Print Format for printing Invoices
	  */
	public int getInvoice_PrintFormat_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Invoice_PrintFormat_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set 1099 Vendor.
		@param Is1099Vendor 1099 Vendor
	*/
	public void setIs1099Vendor (boolean Is1099Vendor)
	{
		set_Value (COLUMNNAME_Is1099Vendor, Boolean.valueOf(Is1099Vendor));
	}

	/** Get 1099 Vendor.
		@return 1099 Vendor	  */
	public boolean is1099Vendor()
	{
		Object oo = get_Value(COLUMNNAME_Is1099Vendor);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
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

	/** Set Customer.
		@param IsCustomer Indicates if this Business Partner is a Customer
	*/
	public void setIsCustomer (boolean IsCustomer)
	{
		set_Value (COLUMNNAME_IsCustomer, Boolean.valueOf(IsCustomer));
	}

	/** Get Customer.
		@return Indicates if this Business Partner is a Customer
	  */
	public boolean isCustomer()
	{
		Object oo = get_Value(COLUMNNAME_IsCustomer);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Discount Printed.
		@param IsDiscountPrinted Print Discount on Invoice and Order
	*/
	public void setIsDiscountPrinted (boolean IsDiscountPrinted)
	{
		set_Value (COLUMNNAME_IsDiscountPrinted, Boolean.valueOf(IsDiscountPrinted));
	}

	/** Get Discount Printed.
		@return Print Discount on Invoice and Order
	  */
	public boolean isDiscountPrinted()
	{
		Object oo = get_Value(COLUMNNAME_IsDiscountPrinted);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Employee.
		@param IsEmployee Indicates if  this Business Partner is an employee
	*/
	public void setIsEmployee (boolean IsEmployee)
	{
		set_Value (COLUMNNAME_IsEmployee, Boolean.valueOf(IsEmployee));
	}

	/** Get Employee.
		@return Indicates if  this Business Partner is an employee
	  */
	public boolean isEmployee()
	{
		Object oo = get_Value(COLUMNNAME_IsEmployee);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Is Manufacturer.
		@param IsManufacturer Indicate role of this Business partner as Manufacturer
	*/
	public void setIsManufacturer (boolean IsManufacturer)
	{
		set_Value (COLUMNNAME_IsManufacturer, Boolean.valueOf(IsManufacturer));
	}

	/** Get Is Manufacturer.
		@return Indicate role of this Business partner as Manufacturer
	  */
	public boolean isManufacturer()
	{
		Object oo = get_Value(COLUMNNAME_IsManufacturer);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set PO Tax exempt.
		@param IsPOTaxExempt Business partner is exempt from tax on purchases
	*/
	public void setIsPOTaxExempt (boolean IsPOTaxExempt)
	{
		set_Value (COLUMNNAME_IsPOTaxExempt, Boolean.valueOf(IsPOTaxExempt));
	}

	/** Get PO Tax exempt.
		@return Business partner is exempt from tax on purchases
	  */
	public boolean isPOTaxExempt()
	{
		Object oo = get_Value(COLUMNNAME_IsPOTaxExempt);
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

	/** Set Prospect.
		@param IsProspect Indicates this is a Prospect
	*/
	public void setIsProspect (boolean IsProspect)
	{
		set_Value (COLUMNNAME_IsProspect, Boolean.valueOf(IsProspect));
	}

	/** Get Prospect.
		@return Indicates this is a Prospect
	  */
	public boolean isProspect()
	{
		Object oo = get_Value(COLUMNNAME_IsProspect);
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

	/** Set Sales Representative.
		@param IsSalesRep Indicates if  the business partner is a sales representative or company agent
	*/
	public void setIsSalesRep (boolean IsSalesRep)
	{
		set_Value (COLUMNNAME_IsSalesRep, Boolean.valueOf(IsSalesRep));
	}

	/** Get Sales Representative.
		@return Indicates if  the business partner is a sales representative or company agent
	  */
	public boolean isSalesRep()
	{
		Object oo = get_Value(COLUMNNAME_IsSalesRep);
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

	/** Set Summary Level.
		@param IsSummary This is a summary entity
	*/
	public void setIsSummary (boolean IsSummary)
	{
		set_Value (COLUMNNAME_IsSummary, Boolean.valueOf(IsSummary));
	}

	/** Get Summary Level.
		@return This is a summary entity
	  */
	public boolean isSummary()
	{
		Object oo = get_Value(COLUMNNAME_IsSummary);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set SO Tax exempt.
		@param IsTaxExempt Business partner is exempt from tax on sales
	*/
	public void setIsTaxExempt (boolean IsTaxExempt)
	{
		set_Value (COLUMNNAME_IsTaxExempt, Boolean.valueOf(IsTaxExempt));
	}

	/** Get SO Tax exempt.
		@return Business partner is exempt from tax on sales
	  */
	public boolean isTaxExempt()
	{
		Object oo = get_Value(COLUMNNAME_IsTaxExempt);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Vendor.
		@param IsVendor Indicates if this Business Partner is a Vendor
	*/
	public void setIsVendor (boolean IsVendor)
	{
		set_Value (COLUMNNAME_IsVendor, Boolean.valueOf(IsVendor));
	}

	/** Get Vendor.
		@return Indicates if this Business Partner is a Vendor
	  */
	public boolean isVendor()
	{
		Object oo = get_Value(COLUMNNAME_IsVendor);
		if (oo != null)
		{
			 if (oo instanceof Boolean)
				 return ((Boolean)oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Partner Location(Name).
		@param JP_BPartner_Location_Name Partner Location(Name)
	*/
	public void setJP_BPartner_Location_Name (String JP_BPartner_Location_Name)
	{
		set_Value (COLUMNNAME_JP_BPartner_Location_Name, JP_BPartner_Location_Name);
	}

	/** Get Partner Location(Name).
		@return Partner Location(Name)	  */
	public String getJP_BPartner_Location_Name()
	{
		return (String)get_Value(COLUMNNAME_JP_BPartner_Location_Name);
	}

	public I_JP_BillSchema getJP_BillSchemaPO() throws RuntimeException
	{
		return (I_JP_BillSchema)MTable.get(getCtx(), I_JP_BillSchema.Table_ID)
			.getPO(getJP_BillSchemaPO_ID(), get_TrxName());
	}

	/** Set Payment Request Schema.
		@param JP_BillSchemaPO_ID Payment Request Schema
	*/
	public void setJP_BillSchemaPO_ID (int JP_BillSchemaPO_ID)
	{
		if (JP_BillSchemaPO_ID < 1)
			set_Value (COLUMNNAME_JP_BillSchemaPO_ID, null);
		else
			set_Value (COLUMNNAME_JP_BillSchemaPO_ID, Integer.valueOf(JP_BillSchemaPO_ID));
	}

	/** Get Payment Request Schema.
		@return Payment Request Schema	  */
	public int getJP_BillSchemaPO_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_BillSchemaPO_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Payment Request Schema(Search Key).
		@param JP_BillSchemaPO_Value Payment Request Schema(Search Key)
	*/
	public void setJP_BillSchemaPO_Value (String JP_BillSchemaPO_Value)
	{
		set_Value (COLUMNNAME_JP_BillSchemaPO_Value, JP_BillSchemaPO_Value);
	}

	/** Get Payment Request Schema(Search Key).
		@return Payment Request Schema(Search Key)	  */
	public String getJP_BillSchemaPO_Value()
	{
		return (String)get_Value(COLUMNNAME_JP_BillSchemaPO_Value);
	}

	public I_JP_BillSchema getJP_BillSchema() throws RuntimeException
	{
		return (I_JP_BillSchema)MTable.get(getCtx(), I_JP_BillSchema.Table_ID)
			.getPO(getJP_BillSchema_ID(), get_TrxName());
	}

	/** Set Bill Schema.
		@param JP_BillSchema_ID Bill Schema
	*/
	public void setJP_BillSchema_ID (int JP_BillSchema_ID)
	{
		if (JP_BillSchema_ID < 1)
			set_Value (COLUMNNAME_JP_BillSchema_ID, null);
		else
			set_Value (COLUMNNAME_JP_BillSchema_ID, Integer.valueOf(JP_BillSchema_ID));
	}

	/** Get Bill Schema.
		@return Bill Schema	  */
	public int getJP_BillSchema_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_BillSchema_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Bill Schema(Search Key).
		@param JP_BillSchema_Value Bill Schema(Search Key)
	*/
	public void setJP_BillSchema_Value (String JP_BillSchema_Value)
	{
		set_Value (COLUMNNAME_JP_BillSchema_Value, JP_BillSchema_Value);
	}

	/** Get Bill Schema(Search Key).
		@return Bill Schema(Search Key)	  */
	public String getJP_BillSchema_Value()
	{
		return (String)get_Value(COLUMNNAME_JP_BillSchema_Value);
	}

	public org.compiere.model.I_AD_PrintFormat getJP_Bill_PrintFormat() throws RuntimeException
	{
		return (org.compiere.model.I_AD_PrintFormat)MTable.get(getCtx(), org.compiere.model.I_AD_PrintFormat.Table_ID)
			.getPO(getJP_Bill_PrintFormat_ID(), get_TrxName());
	}

	/** Set Bill Print Format.
		@param JP_Bill_PrintFormat_ID Print Format for printing Bills
	*/
	public void setJP_Bill_PrintFormat_ID (int JP_Bill_PrintFormat_ID)
	{
		if (JP_Bill_PrintFormat_ID < 1)
			set_Value (COLUMNNAME_JP_Bill_PrintFormat_ID, null);
		else
			set_Value (COLUMNNAME_JP_Bill_PrintFormat_ID, Integer.valueOf(JP_Bill_PrintFormat_ID));
	}

	/** Get Bill Print Format.
		@return Print Format for printing Bills
	  */
	public int getJP_Bill_PrintFormat_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Bill_PrintFormat_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Bill Print Format(Name).
		@param JP_Bill_PrintFormat_Name Bill Print Format(Name)
	*/
	public void setJP_Bill_PrintFormat_Name (String JP_Bill_PrintFormat_Name)
	{
		set_Value (COLUMNNAME_JP_Bill_PrintFormat_Name, JP_Bill_PrintFormat_Name);
	}

	/** Get Bill Print Format(Name).
		@return Bill Print Format(Name)	  */
	public String getJP_Bill_PrintFormat_Name()
	{
		return (String)get_Value(COLUMNNAME_JP_Bill_PrintFormat_Name);
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
			set_Value (COLUMNNAME_JP_CM_BPartner_ID, null);
		else
			set_Value (COLUMNNAME_JP_CM_BPartner_ID, Integer.valueOf(JP_CM_BPartner_ID));
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

	public I_JP_CM_BPartner_Location getJP_CM_BPartner_Location() throws RuntimeException
	{
		return (I_JP_CM_BPartner_Location)MTable.get(getCtx(), I_JP_CM_BPartner_Location.Table_ID)
			.getPO(getJP_CM_BPartner_Location_ID(), get_TrxName());
	}

	/** Set Consolidated Partner Location.
		@param JP_CM_BPartner_Location_ID JPIERE-0636:JPPS
	*/
	public void setJP_CM_BPartner_Location_ID (int JP_CM_BPartner_Location_ID)
	{
		if (JP_CM_BPartner_Location_ID < 1)
			set_Value (COLUMNNAME_JP_CM_BPartner_Location_ID, null);
		else
			set_Value (COLUMNNAME_JP_CM_BPartner_Location_ID, Integer.valueOf(JP_CM_BPartner_Location_ID));
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

	/** Set City(Name).
		@param JP_City_Name City(Name)
	*/
	public void setJP_City_Name (String JP_City_Name)
	{
		set_Value (COLUMNNAME_JP_City_Name, JP_City_Name);
	}

	/** Get City(Name).
		@return City(Name)	  */
	public String getJP_City_Name()
	{
		return (String)get_Value(COLUMNNAME_JP_City_Name);
	}

	/** Set Corporation(Search Key).
		@param JP_CorporationValue Key of the Corporation
	*/
	public void setJP_CorporationValue (String JP_CorporationValue)
	{
		set_Value (COLUMNNAME_JP_CorporationValue, JP_CorporationValue);
	}

	/** Get Corporation(Search Key).
		@return Key of the Corporation
	  */
	public String getJP_CorporationValue()
	{
		return (String)get_Value(COLUMNNAME_JP_CorporationValue);
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

	/** Set Default 1099 Box(Search Key).
		@param JP_Default1099Box_Value Default 1099 Box(Search Key)
	*/
	public void setJP_Default1099Box_Value (String JP_Default1099Box_Value)
	{
		set_Value (COLUMNNAME_JP_Default1099Box_Value, JP_Default1099Box_Value);
	}

	/** Get Default 1099 Box(Search Key).
		@return Default 1099 Box(Search Key)	  */
	public String getJP_Default1099Box_Value()
	{
		return (String)get_Value(COLUMNNAME_JP_Default1099Box_Value);
	}

	/** Set Discount Schema(Name).
		@param JP_DiscountSchema_Name Discount Schema(Name)
	*/
	public void setJP_DiscountSchema_Name (String JP_DiscountSchema_Name)
	{
		set_Value (COLUMNNAME_JP_DiscountSchema_Name, JP_DiscountSchema_Name);
	}

	/** Get Discount Schema(Name).
		@return Discount Schema(Name)	  */
	public String getJP_DiscountSchema_Name()
	{
		return (String)get_Value(COLUMNNAME_JP_DiscountSchema_Name);
	}

	/** Set Dunning(Name).
		@param JP_Dunning_Name Dunning(Name)
	*/
	public void setJP_Dunning_Name (String JP_Dunning_Name)
	{
		set_Value (COLUMNNAME_JP_Dunning_Name, JP_Dunning_Name);
	}

	/** Get Dunning(Name).
		@return Dunning(Name)	  */
	public String getJP_Dunning_Name()
	{
		return (String)get_Value(COLUMNNAME_JP_Dunning_Name);
	}

	/** Set Greeting(Name).
		@param JP_Greeting_Name Greeting(Name)
	*/
	public void setJP_Greeting_Name (String JP_Greeting_Name)
	{
		set_Value (COLUMNNAME_JP_Greeting_Name, JP_Greeting_Name);
	}

	/** Get Greeting(Name).
		@return Greeting(Name)	  */
	public String getJP_Greeting_Name()
	{
		return (String)get_Value(COLUMNNAME_JP_Greeting_Name);
	}

	/** Set Invoice Schedule(Name).
		@param JP_InvoiceSchedule_Name Schedule for generating Invoices
	*/
	public void setJP_InvoiceSchedule_Name (String JP_InvoiceSchedule_Name)
	{
		set_Value (COLUMNNAME_JP_InvoiceSchedule_Name, JP_InvoiceSchedule_Name);
	}

	/** Get Invoice Schedule(Name).
		@return Schedule for generating Invoices
	  */
	public String getJP_InvoiceSchedule_Name()
	{
		return (String)get_Value(COLUMNNAME_JP_InvoiceSchedule_Name);
	}

	/** Set Invoice Print Format(Name).
		@param JP_Invoice_PrintFormat_Name Print Format for printing Invoices
	*/
	public void setJP_Invoice_PrintFormat_Name (String JP_Invoice_PrintFormat_Name)
	{
		set_Value (COLUMNNAME_JP_Invoice_PrintFormat_Name, JP_Invoice_PrintFormat_Name);
	}

	/** Get Invoice Print Format(Name).
		@return Print Format for printing Invoices
	  */
	public String getJP_Invoice_PrintFormat_Name()
	{
		return (String)get_Value(COLUMNNAME_JP_Invoice_PrintFormat_Name);
	}

	/** Set Location Label.
		@param JP_Location_Label Location Label
	*/
	public void setJP_Location_Label (String JP_Location_Label)
	{
		set_Value (COLUMNNAME_JP_Location_Label, JP_Location_Label);
	}

	/** Get Location Label.
		@return Location Label	  */
	public String getJP_Location_Label()
	{
		return (String)get_Value(COLUMNNAME_JP_Location_Label);
	}

	/** Set Organization(Search Key).
		@param JP_Org_Value Organization(Search Key)
	*/
	public void setJP_Org_Value (String JP_Org_Value)
	{
		set_Value (COLUMNNAME_JP_Org_Value, JP_Org_Value);
	}

	/** Get Organization(Search Key).
		@return Organization(Search Key)	  */
	public String getJP_Org_Value()
	{
		return (String)get_Value(COLUMNNAME_JP_Org_Value);
	}

	/** UP = 0 */
	public static final String JP_POTAXROUNDING_UP = "0";
	/** DOWN = 1 */
	public static final String JP_POTAXROUNDING_DOWN = "1";
	/** CEILING = 2 */
	public static final String JP_POTAXROUNDING_CEILING = "2";
	/** FLOOR = 3 */
	public static final String JP_POTAXROUNDING_FLOOR = "3";
	/** HALF_UP = 4 */
	public static final String JP_POTAXROUNDING_HALF_UP = "4";
	/** HALF_DOWN = 5 */
	public static final String JP_POTAXROUNDING_HALF_DOWN = "5";
	/** HALF_EVEN = 6 */
	public static final String JP_POTAXROUNDING_HALF_EVEN = "6";
	/** UNNECESSARY = 7 */
	public static final String JP_POTAXROUNDING_UNNECESSARY = "7";
	/** Set PO Tax Rounding.
		@param JP_POTaxRounding PO Tax Rounding
	*/
	public void setJP_POTaxRounding (String JP_POTaxRounding)
	{

		set_Value (COLUMNNAME_JP_POTaxRounding, JP_POTaxRounding);
	}

	/** Get PO Tax Rounding.
		@return PO Tax Rounding	  */
	public String getJP_POTaxRounding()
	{
		return (String)get_Value(COLUMNNAME_JP_POTaxRounding);
	}

	/** Set PO Discount Schema(Name).
		@param JP_PO_DiscountSchema_Name PO Discount Schema(Name)
	*/
	public void setJP_PO_DiscountSchema_Name (String JP_PO_DiscountSchema_Name)
	{
		set_Value (COLUMNNAME_JP_PO_DiscountSchema_Name, JP_PO_DiscountSchema_Name);
	}

	/** Get PO Discount Schema(Name).
		@return PO Discount Schema(Name)	  */
	public String getJP_PO_DiscountSchema_Name()
	{
		return (String)get_Value(COLUMNNAME_JP_PO_DiscountSchema_Name);
	}

	/** Set PO Payment Term(Search Key).
		@param JP_PO_PaymentTerm_Value The terms of Payment (timing, discount)
	*/
	public void setJP_PO_PaymentTerm_Value (String JP_PO_PaymentTerm_Value)
	{
		set_Value (COLUMNNAME_JP_PO_PaymentTerm_Value, JP_PO_PaymentTerm_Value);
	}

	/** Get PO Payment Term(Search Key).
		@return The terms of Payment (timing, discount)
	  */
	public String getJP_PO_PaymentTerm_Value()
	{
		return (String)get_Value(COLUMNNAME_JP_PO_PaymentTerm_Value);
	}

	/** Set Purchase Pricelist(Name).
		@param JP_PO_PriceList_Name Purchase Pricelist(Name)
	*/
	public void setJP_PO_PriceList_Name (String JP_PO_PriceList_Name)
	{
		set_Value (COLUMNNAME_JP_PO_PriceList_Name, JP_PO_PriceList_Name);
	}

	/** Get Purchase Pricelist(Name).
		@return Purchase Pricelist(Name)	  */
	public String getJP_PO_PriceList_Name()
	{
		return (String)get_Value(COLUMNNAME_JP_PO_PriceList_Name);
	}

	/** Set Payment Term(Search Key).
		@param JP_PaymentTerm_Value The terms of Payment (timing, discount)
	*/
	public void setJP_PaymentTerm_Value (String JP_PaymentTerm_Value)
	{
		set_Value (COLUMNNAME_JP_PaymentTerm_Value, JP_PaymentTerm_Value);
	}

	/** Get Payment Term(Search Key).
		@return The terms of Payment (timing, discount)
	  */
	public String getJP_PaymentTerm_Value()
	{
		return (String)get_Value(COLUMNNAME_JP_PaymentTerm_Value);
	}

	/** Set Price List(Name).
		@param JP_PriceList_Name Price List(Name)
	*/
	public void setJP_PriceList_Name (String JP_PriceList_Name)
	{
		set_Value (COLUMNNAME_JP_PriceList_Name, JP_PriceList_Name);
	}

	/** Get Price List(Name).
		@return Price List(Name)	  */
	public String getJP_PriceList_Name()
	{
		return (String)get_Value(COLUMNNAME_JP_PriceList_Name);
	}

	/** Set Region(Name).
		@param JP_Region_Name Region(Name)
	*/
	public void setJP_Region_Name (String JP_Region_Name)
	{
		set_Value (COLUMNNAME_JP_Region_Name, JP_Region_Name);
	}

	/** Get Region(Name).
		@return Region(Name)	  */
	public String getJP_Region_Name()
	{
		return (String)get_Value(COLUMNNAME_JP_Region_Name);
	}

	/** UP = 0 */
	public static final String JP_SOTAXROUNDING_UP = "0";
	/** DOWN = 1 */
	public static final String JP_SOTAXROUNDING_DOWN = "1";
	/** CEILING = 2 */
	public static final String JP_SOTAXROUNDING_CEILING = "2";
	/** FLOOR = 3 */
	public static final String JP_SOTAXROUNDING_FLOOR = "3";
	/** HALF_UP = 4 */
	public static final String JP_SOTAXROUNDING_HALF_UP = "4";
	/** HALF_DOWN = 5 */
	public static final String JP_SOTAXROUNDING_HALF_DOWN = "5";
	/** HALF_EVEN = 6 */
	public static final String JP_SOTAXROUNDING_HALF_EVEN = "6";
	/** UNNECESSARY = 7 */
	public static final String JP_SOTAXROUNDING_UNNECESSARY = "7";
	/** Set SO Tax Rounding.
		@param JP_SOTaxRounding SO Tax Rounding
	*/
	public void setJP_SOTaxRounding (String JP_SOTaxRounding)
	{

		set_Value (COLUMNNAME_JP_SOTaxRounding, JP_SOTaxRounding);
	}

	/** Get SO Tax Rounding.
		@return SO Tax Rounding	  */
	public String getJP_SOTaxRounding()
	{
		return (String)get_Value(COLUMNNAME_JP_SOTaxRounding);
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

	/** Set Sales Rep(E-Mail).
		@param JP_SalesRep_EMail Sales Rep(E-Mail)
	*/
	public void setJP_SalesRep_EMail (String JP_SalesRep_EMail)
	{
		set_Value (COLUMNNAME_JP_SalesRep_EMail, JP_SalesRep_EMail);
	}

	/** Get Sales Rep(E-Mail).
		@return Sales Rep(E-Mail)	  */
	public String getJP_SalesRep_EMail()
	{
		return (String)get_Value(COLUMNNAME_JP_SalesRep_EMail);
	}

	/** Set Sales Rep(Name).
		@param JP_SalesRep_Name Sales Rep(Name)
	*/
	public void setJP_SalesRep_Name (String JP_SalesRep_Name)
	{
		set_Value (COLUMNNAME_JP_SalesRep_Name, JP_SalesRep_Name);
	}

	/** Get Sales Rep(Name).
		@return Sales Rep(Name)	  */
	public String getJP_SalesRep_Name()
	{
		return (String)get_Value(COLUMNNAME_JP_SalesRep_Name);
	}

	/** Set Sales Rep(Search Key).
		@param JP_SalesRep_Value Sales Rep(Search Key)
	*/
	public void setJP_SalesRep_Value (String JP_SalesRep_Value)
	{
		set_Value (COLUMNNAME_JP_SalesRep_Value, JP_SalesRep_Value);
	}

	/** Get Sales Rep(Search Key).
		@return Sales Rep(Search Key)	  */
	public String getJP_SalesRep_Value()
	{
		return (String)get_Value(COLUMNNAME_JP_SalesRep_Value);
	}

	public org.compiere.model.I_C_Greeting getJP_User_Greeting() throws RuntimeException
	{
		return (org.compiere.model.I_C_Greeting)MTable.get(getCtx(), org.compiere.model.I_C_Greeting.Table_ID)
			.getPO(getJP_User_Greeting_ID(), get_TrxName());
	}

	/** Set Greeting(User).
		@param JP_User_Greeting_ID Greeting to print on correspondence
	*/
	public void setJP_User_Greeting_ID (int JP_User_Greeting_ID)
	{
		if (JP_User_Greeting_ID < 1)
			set_Value (COLUMNNAME_JP_User_Greeting_ID, null);
		else
			set_Value (COLUMNNAME_JP_User_Greeting_ID, Integer.valueOf(JP_User_Greeting_ID));
	}

	/** Get Greeting(User).
		@return Greeting to print on correspondence
	  */
	public int getJP_User_Greeting_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_User_Greeting_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Phone(User) .
		@param JP_User_Phone Phone(User) 
	*/
	public void setJP_User_Phone (String JP_User_Phone)
	{
		set_Value (COLUMNNAME_JP_User_Phone, JP_User_Phone);
	}

	/** Get Phone(User) .
		@return Phone(User) 	  */
	public String getJP_User_Phone()
	{
		return (String)get_Value(COLUMNNAME_JP_User_Phone);
	}

	/** Set Phone2(User) .
		@param JP_User_Phone2 Phone2(User) 
	*/
	public void setJP_User_Phone2 (String JP_User_Phone2)
	{
		set_Value (COLUMNNAME_JP_User_Phone2, JP_User_Phone2);
	}

	/** Get Phone2(User) .
		@return Phone2(User) 	  */
	public String getJP_User_Phone2()
	{
		return (String)get_Value(COLUMNNAME_JP_User_Phone2);
	}

	/** Set User(Search Key).
		@param JP_User_Value User(Search Key)
	*/
	public void setJP_User_Value (String JP_User_Value)
	{
		set_Value (COLUMNNAME_JP_User_Value, JP_User_Value);
	}

	/** Get User(Search Key).
		@return User(Search Key)	  */
	public String getJP_User_Value()
	{
		return (String)get_Value(COLUMNNAME_JP_User_Value);
	}

	public org.compiere.model.I_M_DiscountSchema getM_DiscountSchema() throws RuntimeException
	{
		return (org.compiere.model.I_M_DiscountSchema)MTable.get(getCtx(), org.compiere.model.I_M_DiscountSchema.Table_ID)
			.getPO(getM_DiscountSchema_ID(), get_TrxName());
	}

	/** Set Discount Schema.
		@param M_DiscountSchema_ID Schema to calculate the trade discount percentage
	*/
	public void setM_DiscountSchema_ID (int M_DiscountSchema_ID)
	{
		if (M_DiscountSchema_ID < 1)
			set_Value (COLUMNNAME_M_DiscountSchema_ID, null);
		else
			set_Value (COLUMNNAME_M_DiscountSchema_ID, Integer.valueOf(M_DiscountSchema_ID));
	}

	/** Get Discount Schema.
		@return Schema to calculate the trade discount percentage
	  */
	public int getM_DiscountSchema_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_DiscountSchema_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_PriceList getM_PriceList() throws RuntimeException
	{
		return (org.compiere.model.I_M_PriceList)MTable.get(getCtx(), org.compiere.model.I_M_PriceList.Table_ID)
			.getPO(getM_PriceList_ID(), get_TrxName());
	}

	/** Set Price List.
		@param M_PriceList_ID Unique identifier of a Price List
	*/
	public void setM_PriceList_ID (int M_PriceList_ID)
	{
		if (M_PriceList_ID < 1)
			set_Value (COLUMNNAME_M_PriceList_ID, null);
		else
			set_Value (COLUMNNAME_M_PriceList_ID, Integer.valueOf(M_PriceList_ID));
	}

	/** Get Price List.
		@return Unique identifier of a Price List
	  */
	public int getM_PriceList_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_PriceList_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set NAICS/SIC.
		@param NAICS Standard Industry Code or its successor NAIC - http://www.osha.gov/oshstats/sicser.html
	*/
	public void setNAICS (String NAICS)
	{
		set_Value (COLUMNNAME_NAICS, NAICS);
	}

	/** Get NAICS/SIC.
		@return Standard Industry Code or its successor NAIC - http://www.osha.gov/oshstats/sicser.html
	  */
	public String getNAICS()
	{
		return (String)get_Value(COLUMNNAME_NAICS);
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

	/** Set Employees.
		@param NumberEmployees Number of employees
	*/
	public void setNumberEmployees (int NumberEmployees)
	{
		set_Value (COLUMNNAME_NumberEmployees, Integer.valueOf(NumberEmployees));
	}

	/** Get Employees.
		@return Number of employees
	  */
	public int getNumberEmployees()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_NumberEmployees);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Order Reference.
		@param POReference Transaction Reference Number (Sales Order, Purchase Order) of your Business Partner
	*/
	public void setPOReference (String POReference)
	{
		set_Value (COLUMNNAME_POReference, POReference);
	}

	/** Get Order Reference.
		@return Transaction Reference Number (Sales Order, Purchase Order) of your Business Partner
	  */
	public String getPOReference()
	{
		return (String)get_Value(COLUMNNAME_POReference);
	}

	public org.compiere.model.I_M_DiscountSchema getPO_DiscountSchema() throws RuntimeException
	{
		return (org.compiere.model.I_M_DiscountSchema)MTable.get(getCtx(), org.compiere.model.I_M_DiscountSchema.Table_ID)
			.getPO(getPO_DiscountSchema_ID(), get_TrxName());
	}

	/** Set PO Discount Schema.
		@param PO_DiscountSchema_ID Schema to calculate the purchase trade discount percentage
	*/
	public void setPO_DiscountSchema_ID (int PO_DiscountSchema_ID)
	{
		if (PO_DiscountSchema_ID < 1)
			set_Value (COLUMNNAME_PO_DiscountSchema_ID, null);
		else
			set_Value (COLUMNNAME_PO_DiscountSchema_ID, Integer.valueOf(PO_DiscountSchema_ID));
	}

	/** Get PO Discount Schema.
		@return Schema to calculate the purchase trade discount percentage
	  */
	public int getPO_DiscountSchema_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_PO_DiscountSchema_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_PaymentTerm getPO_PaymentTerm() throws RuntimeException
	{
		return (org.compiere.model.I_C_PaymentTerm)MTable.get(getCtx(), org.compiere.model.I_C_PaymentTerm.Table_ID)
			.getPO(getPO_PaymentTerm_ID(), get_TrxName());
	}

	/** Set PO Payment Term.
		@param PO_PaymentTerm_ID Payment rules for a purchase order
	*/
	public void setPO_PaymentTerm_ID (int PO_PaymentTerm_ID)
	{
		if (PO_PaymentTerm_ID < 1)
			set_Value (COLUMNNAME_PO_PaymentTerm_ID, null);
		else
			set_Value (COLUMNNAME_PO_PaymentTerm_ID, Integer.valueOf(PO_PaymentTerm_ID));
	}

	/** Get PO Payment Term.
		@return Payment rules for a purchase order
	  */
	public int getPO_PaymentTerm_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_PO_PaymentTerm_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_PriceList getPO_PriceList() throws RuntimeException
	{
		return (org.compiere.model.I_M_PriceList)MTable.get(getCtx(), org.compiere.model.I_M_PriceList.Table_ID)
			.getPO(getPO_PriceList_ID(), get_TrxName());
	}

	/** Set Purchase Price List.
		@param PO_PriceList_ID Price List used by this Business Partner
	*/
	public void setPO_PriceList_ID (int PO_PriceList_ID)
	{
		if (PO_PriceList_ID < 1)
			set_Value (COLUMNNAME_PO_PriceList_ID, null);
		else
			set_Value (COLUMNNAME_PO_PriceList_ID, Integer.valueOf(PO_PriceList_ID));
	}

	/** Get Purchase Price List.
		@return Price List used by this Business Partner
	  */
	public int getPO_PriceList_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_PO_PriceList_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Password.
		@param Password Password of any length (case sensitive)
	*/
	public void setPassword (String Password)
	{
		set_Value (COLUMNNAME_Password, Password);
	}

	/** Get Password.
		@return Password of any length (case sensitive)
	  */
	public String getPassword()
	{
		return (String)get_Value(COLUMNNAME_Password);
	}

	/** PaymentRule AD_Reference_ID=195 */
	public static final int PAYMENTRULE_AD_Reference_ID=195;
	/** Cash = B */
	public static final String PAYMENTRULE_Cash = "B";
	/** Direct Debit = D */
	public static final String PAYMENTRULE_DirectDebit = "D";
	/** Credit Card = K */
	public static final String PAYMENTRULE_CreditCard = "K";
	/** Mixed POS Payment = M */
	public static final String PAYMENTRULE_MixedPOSPayment = "M";
	/** On Credit = P */
	public static final String PAYMENTRULE_OnCredit = "P";
	/** Check = S */
	public static final String PAYMENTRULE_Check = "S";
	/** Direct Deposit = T */
	public static final String PAYMENTRULE_DirectDeposit = "T";
	/** Set Payment Rule.
		@param PaymentRule How you pay the invoice
	*/
	public void setPaymentRule (String PaymentRule)
	{

		set_Value (COLUMNNAME_PaymentRule, PaymentRule);
	}

	/** Get Payment Rule.
		@return How you pay the invoice
	  */
	public String getPaymentRule()
	{
		return (String)get_Value(COLUMNNAME_PaymentRule);
	}

	/** PaymentRulePO AD_Reference_ID=195 */
	public static final int PAYMENTRULEPO_AD_Reference_ID=195;
	/** Cash = B */
	public static final String PAYMENTRULEPO_Cash = "B";
	/** Direct Debit = D */
	public static final String PAYMENTRULEPO_DirectDebit = "D";
	/** Credit Card = K */
	public static final String PAYMENTRULEPO_CreditCard = "K";
	/** Mixed POS Payment = M */
	public static final String PAYMENTRULEPO_MixedPOSPayment = "M";
	/** On Credit = P */
	public static final String PAYMENTRULEPO_OnCredit = "P";
	/** Check = S */
	public static final String PAYMENTRULEPO_Check = "S";
	/** Direct Deposit = T */
	public static final String PAYMENTRULEPO_DirectDeposit = "T";
	/** Set Payment Rule.
		@param PaymentRulePO Purchase payment option
	*/
	public void setPaymentRulePO (String PaymentRulePO)
	{

		set_Value (COLUMNNAME_PaymentRulePO, PaymentRulePO);
	}

	/** Get Payment Rule.
		@return Purchase payment option
	  */
	public String getPaymentRulePO()
	{
		return (String)get_Value(COLUMNNAME_PaymentRulePO);
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

	/** Set ZIP.
		@param Postal Postal code
	*/
	public void setPostal (String Postal)
	{
		set_Value (COLUMNNAME_Postal, Postal);
	}

	/** Get ZIP.
		@return Postal code
	  */
	public String getPostal()
	{
		return (String)get_Value(COLUMNNAME_Postal);
	}

	/** Set Additional Zip.
		@param Postal_Add Additional ZIP or Postal code
	*/
	public void setPostal_Add (String Postal_Add)
	{
		set_Value (COLUMNNAME_Postal_Add, Postal_Add);
	}

	/** Get Additional Zip.
		@return Additional ZIP or Postal code
	  */
	public String getPostal_Add()
	{
		return (String)get_Value(COLUMNNAME_Postal_Add);
	}

	/** Set Potential Life Time Value.
		@param PotentialLifeTimeValue Total Revenue expected
	*/
	public void setPotentialLifeTimeValue (BigDecimal PotentialLifeTimeValue)
	{
		set_Value (COLUMNNAME_PotentialLifeTimeValue, PotentialLifeTimeValue);
	}

	/** Get Potential Life Time Value.
		@return Total Revenue expected
	  */
	public BigDecimal getPotentialLifeTimeValue()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_PotentialLifeTimeValue);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Processed.
		@param Processed The document has been processed
	*/
	public void setProcessed (boolean Processed)
	{
		set_ValueNoCheck (COLUMNNAME_Processed, Boolean.valueOf(Processed));
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

	public org.compiere.model.I_R_InterestArea getR_InterestArea() throws RuntimeException
	{
		return (org.compiere.model.I_R_InterestArea)MTable.get(getCtx(), org.compiere.model.I_R_InterestArea.Table_ID)
			.getPO(getR_InterestArea_ID(), get_TrxName());
	}

	/** Set Interest Area.
		@param R_InterestArea_ID Interest Area or Topic
	*/
	public void setR_InterestArea_ID (int R_InterestArea_ID)
	{
		if (R_InterestArea_ID < 1)
			set_Value (COLUMNNAME_R_InterestArea_ID, null);
		else
			set_Value (COLUMNNAME_R_InterestArea_ID, Integer.valueOf(R_InterestArea_ID));
	}

	/** Get Interest Area.
		@return Interest Area or Topic
	  */
	public int getR_InterestArea_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_R_InterestArea_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Rating.
		@param Rating Classification or Importance
	*/
	public void setRating (String Rating)
	{
		set_Value (COLUMNNAME_Rating, Rating);
	}

	/** Get Rating.
		@return Classification or Importance
	  */
	public String getRating()
	{
		return (String)get_Value(COLUMNNAME_Rating);
	}

	/** Set Reference No.
		@param ReferenceNo Your customer or vendor number at the Business Partner&#039;s site
	*/
	public void setReferenceNo (String ReferenceNo)
	{
		set_Value (COLUMNNAME_ReferenceNo, ReferenceNo);
	}

	/** Get Reference No.
		@return Your customer or vendor number at the Business Partner&#039;s site
	  */
	public String getReferenceNo()
	{
		return (String)get_Value(COLUMNNAME_ReferenceNo);
	}

	/** Set Region.
		@param RegionName Name of the Region
	*/
	public void setRegionName (String RegionName)
	{
		set_Value (COLUMNNAME_RegionName, RegionName);
	}

	/** Get Region.
		@return Name of the Region
	  */
	public String getRegionName()
	{
		return (String)get_Value(COLUMNNAME_RegionName);
	}

	/** SOCreditStatus AD_Reference_ID=289 */
	public static final int SOCREDITSTATUS_AD_Reference_ID=289;
	/** Credit Hold = H */
	public static final String SOCREDITSTATUS_CreditHold = "H";
	/** Credit OK = O */
	public static final String SOCREDITSTATUS_CreditOK = "O";
	/** Credit Stop = S */
	public static final String SOCREDITSTATUS_CreditStop = "S";
	/** Credit Watch = W */
	public static final String SOCREDITSTATUS_CreditWatch = "W";
	/** No Credit Check = X */
	public static final String SOCREDITSTATUS_NoCreditCheck = "X";
	/** Set Credit Status.
		@param SOCreditStatus Business Partner Credit Status
	*/
	public void setSOCreditStatus (String SOCreditStatus)
	{

		set_Value (COLUMNNAME_SOCreditStatus, SOCreditStatus);
	}

	/** Get Credit Status.
		@return Business Partner Credit Status
	  */
	public String getSOCreditStatus()
	{
		return (String)get_Value(COLUMNNAME_SOCreditStatus);
	}

	/** Set Credit Limit.
		@param SO_CreditLimit Total outstanding invoice amounts allowed
	*/
	public void setSO_CreditLimit (BigDecimal SO_CreditLimit)
	{
		set_Value (COLUMNNAME_SO_CreditLimit, SO_CreditLimit);
	}

	/** Get Credit Limit.
		@return Total outstanding invoice amounts allowed
	  */
	public BigDecimal getSO_CreditLimit()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_SO_CreditLimit);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Order Description.
		@param SO_Description Description to be used on orders
	*/
	public void setSO_Description (String SO_Description)
	{
		set_Value (COLUMNNAME_SO_Description, SO_Description);
	}

	/** Get Order Description.
		@return Description to be used on orders
	  */
	public String getSO_Description()
	{
		return (String)get_Value(COLUMNNAME_SO_Description);
	}

	public org.compiere.model.I_AD_User getSalesRep() throws RuntimeException
	{
		return (org.compiere.model.I_AD_User)MTable.get(getCtx(), org.compiere.model.I_AD_User.Table_ID)
			.getPO(getSalesRep_ID(), get_TrxName());
	}

	/** Set Sales Rep.
		@param SalesRep_ID Sales Representative or Company Agent
	*/
	public void setSalesRep_ID (int SalesRep_ID)
	{
		if (SalesRep_ID < 1)
			set_Value (COLUMNNAME_SalesRep_ID, null);
		else
			set_Value (COLUMNNAME_SalesRep_ID, Integer.valueOf(SalesRep_ID));
	}

	/** Get Sales Rep.
		@return Sales Representative or Company Agent
	  */
	public int getSalesRep_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_SalesRep_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Sales Volume in 1.000.
		@param SalesVolume Total Volume of Sales in Thousands of Currency
	*/
	public void setSalesVolume (int SalesVolume)
	{
		set_Value (COLUMNNAME_SalesVolume, Integer.valueOf(SalesVolume));
	}

	/** Get Sales Volume in 1.000.
		@return Total Volume of Sales in Thousands of Currency
	  */
	public int getSalesVolume()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_SalesVolume);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Share.
		@param ShareOfCustomer Share of Customer&#039;s business as a percentage
	*/
	public void setShareOfCustomer (int ShareOfCustomer)
	{
		set_Value (COLUMNNAME_ShareOfCustomer, Integer.valueOf(ShareOfCustomer));
	}

	/** Get Share.
		@return Share of Customer&#039;s business as a percentage
	  */
	public int getShareOfCustomer()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_ShareOfCustomer);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Min Shelf Life %.
		@param ShelfLifeMinPct Minimum Shelf Life in percent based on Product Instance Guarantee Date
	*/
	public void setShelfLifeMinPct (int ShelfLifeMinPct)
	{
		set_Value (COLUMNNAME_ShelfLifeMinPct, Integer.valueOf(ShelfLifeMinPct));
	}

	/** Get Min Shelf Life %.
		@return Minimum Shelf Life in percent based on Product Instance Guarantee Date
	  */
	public int getShelfLifeMinPct()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_ShelfLifeMinPct);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Tax ID.
		@param TaxID Tax Identification
	*/
	public void setTaxID (String TaxID)
	{
		set_Value (COLUMNNAME_TaxID, TaxID);
	}

	/** Get Tax ID.
		@return Tax Identification
	  */
	public String getTaxID()
	{
		return (String)get_Value(COLUMNNAME_TaxID);
	}

	/** Set Title.
		@param Title Name this entity is referred to as
	*/
	public void setTitle (String Title)
	{
		set_Value (COLUMNNAME_Title, Title);
	}

	/** Get Title.
		@return Name this entity is referred to as
	  */
	public String getTitle()
	{
		return (String)get_Value(COLUMNNAME_Title);
	}

	/** Set URL.
		@param URL Full URL address - e.g. http://www.idempiere.org
	*/
	public void setURL (String URL)
	{
		set_Value (COLUMNNAME_URL, URL);
	}

	/** Get URL.
		@return Full URL address - e.g. http://www.idempiere.org
	  */
	public String getURL()
	{
		return (String)get_Value(COLUMNNAME_URL);
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

    /** Get Record ID/ColumnName
        @return ID/ColumnName pair
      */
    public KeyNamePair getKeyNamePair()
    {
        return new KeyNamePair(get_ID(), getValue());
    }
}