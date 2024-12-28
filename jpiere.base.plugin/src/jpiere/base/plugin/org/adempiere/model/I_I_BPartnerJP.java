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
package jpiere.base.plugin.org.adempiere.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.compiere.model.*;
import org.compiere.util.KeyNamePair;

/** Generated Interface for I_BPartnerJP
 *  @author iDempiere (generated) 
 *  @version Release 11
 */
@SuppressWarnings("all")
public interface I_I_BPartnerJP 
{

    /** TableName=I_BPartnerJP */
    public static final String Table_Name = "I_BPartnerJP";

    /** AD_Table_ID=1000021 */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 7 - System - Client - Org 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(7);

    /** Load Meta Data */

    /** Column name AD_Client_ID */
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";

	/** Get Tenant.
	  * Tenant for this installation.
	  */
	public int getAD_Client_ID();

    /** Column name AD_Language */
    public static final String COLUMNNAME_AD_Language = "AD_Language";

	/** Set Language.
	  * Language for this entity
	  */
	public void setAD_Language (String AD_Language);

	/** Get Language.
	  * Language for this entity
	  */
	public String getAD_Language();

    /** Column name AD_Org_ID */
    public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";

	/** Set Organization.
	  * Organizational entity within tenant
	  */
	public void setAD_Org_ID (int AD_Org_ID);

	/** Get Organization.
	  * Organizational entity within tenant
	  */
	public int getAD_Org_ID();

    /** Column name AD_User_ID */
    public static final String COLUMNNAME_AD_User_ID = "AD_User_ID";

	/** Set User/Contact.
	  * User within the system - Internal or Business Partner Contact
	  */
	public void setAD_User_ID (int AD_User_ID);

	/** Get User/Contact.
	  * User within the system - Internal or Business Partner Contact
	  */
	public int getAD_User_ID();

	public org.compiere.model.I_AD_User getAD_User() throws RuntimeException;

    /** Column name AcqusitionCost */
    public static final String COLUMNNAME_AcqusitionCost = "AcqusitionCost";

	/** Set Acquisition Cost.
	  * The cost of gaining the prospect as a customer
	  */
	public void setAcqusitionCost (BigDecimal AcqusitionCost);

	/** Get Acquisition Cost.
	  * The cost of gaining the prospect as a customer
	  */
	public BigDecimal getAcqusitionCost();

    /** Column name ActualLifeTimeValue */
    public static final String COLUMNNAME_ActualLifeTimeValue = "ActualLifeTimeValue";

	/** Set Actual Life Time Value.
	  * Actual Life Time Revenue
	  */
	public void setActualLifeTimeValue (BigDecimal ActualLifeTimeValue);

	/** Get Actual Life Time Value.
	  * Actual Life Time Revenue
	  */
	public BigDecimal getActualLifeTimeValue();

    /** Column name Address1 */
    public static final String COLUMNNAME_Address1 = "Address1";

	/** Set Address 1.
	  * Address line 1 for this location
	  */
	public void setAddress1 (String Address1);

	/** Get Address 1.
	  * Address line 1 for this location
	  */
	public String getAddress1();

    /** Column name Address2 */
    public static final String COLUMNNAME_Address2 = "Address2";

	/** Set Address 2.
	  * Address line 2 for this location
	  */
	public void setAddress2 (String Address2);

	/** Get Address 2.
	  * Address line 2 for this location
	  */
	public String getAddress2();

    /** Column name Address3 */
    public static final String COLUMNNAME_Address3 = "Address3";

	/** Set Address 3.
	  * Address Line 3 for the location
	  */
	public void setAddress3 (String Address3);

	/** Get Address 3.
	  * Address Line 3 for the location
	  */
	public String getAddress3();

    /** Column name Address4 */
    public static final String COLUMNNAME_Address4 = "Address4";

	/** Set Address 4.
	  * Address Line 4 for the location
	  */
	public void setAddress4 (String Address4);

	/** Get Address 4.
	  * Address Line 4 for the location
	  */
	public String getAddress4();

    /** Column name Address5 */
    public static final String COLUMNNAME_Address5 = "Address5";

	/** Set Address 5.
	  * Address Line 5 for the location
	  */
	public void setAddress5 (String Address5);

	/** Get Address 5.
	  * Address Line 5 for the location
	  */
	public String getAddress5();

    /** Column name BPContactGreeting */
    public static final String COLUMNNAME_BPContactGreeting = "BPContactGreeting";

	/** Set BP Contact Greeting.
	  * Greeting for Business Partner Contact
	  */
	public void setBPContactGreeting (String BPContactGreeting);

	/** Get BP Contact Greeting.
	  * Greeting for Business Partner Contact
	  */
	public String getBPContactGreeting();

    /** Column name Birthday */
    public static final String COLUMNNAME_Birthday = "Birthday";

	/** Set Birthday.
	  * Birthday or Anniversary day
	  */
	public void setBirthday (Timestamp Birthday);

	/** Get Birthday.
	  * Birthday or Anniversary day
	  */
	public Timestamp getBirthday();

    /** Column name C_BP_Group_ID */
    public static final String COLUMNNAME_C_BP_Group_ID = "C_BP_Group_ID";

	/** Set BPartner Group.
	  * Business Partner Group
	  */
	public void setC_BP_Group_ID (int C_BP_Group_ID);

	/** Get BPartner Group.
	  * Business Partner Group
	  */
	public int getC_BP_Group_ID();

	public org.compiere.model.I_C_BP_Group getC_BP_Group() throws RuntimeException;

    /** Column name C_BPartner_ID */
    public static final String COLUMNNAME_C_BPartner_ID = "C_BPartner_ID";

	/** Set Business Partner.
	  * Identifies a Business Partner
	  */
	public void setC_BPartner_ID (int C_BPartner_ID);

	/** Get Business Partner.
	  * Identifies a Business Partner
	  */
	public int getC_BPartner_ID();

	public org.compiere.model.I_C_BPartner getC_BPartner() throws RuntimeException;

    /** Column name C_BPartner_Location_ID */
    public static final String COLUMNNAME_C_BPartner_Location_ID = "C_BPartner_Location_ID";

	/** Set Partner Location.
	  * Identifies the (ship to) address for this Business Partner
	  */
	public void setC_BPartner_Location_ID (int C_BPartner_Location_ID);

	/** Get Partner Location.
	  * Identifies the (ship to) address for this Business Partner
	  */
	public int getC_BPartner_Location_ID();

	public org.compiere.model.I_C_BPartner_Location getC_BPartner_Location() throws RuntimeException;

    /** Column name C_City_ID */
    public static final String COLUMNNAME_C_City_ID = "C_City_ID";

	/** Set City.
	  * City
	  */
	public void setC_City_ID (int C_City_ID);

	/** Get City.
	  * City
	  */
	public int getC_City_ID();

	public org.compiere.model.I_C_City getC_City() throws RuntimeException;

    /** Column name C_Country_ID */
    public static final String COLUMNNAME_C_Country_ID = "C_Country_ID";

	/** Set Country.
	  * Country 
	  */
	public void setC_Country_ID (int C_Country_ID);

	/** Get Country.
	  * Country 
	  */
	public int getC_Country_ID();

	public org.compiere.model.I_C_Country getC_Country() throws RuntimeException;

    /** Column name C_Dunning_ID */
    public static final String COLUMNNAME_C_Dunning_ID = "C_Dunning_ID";

	/** Set Dunning.
	  * Dunning Rules for overdue invoices
	  */
	public void setC_Dunning_ID (int C_Dunning_ID);

	/** Get Dunning.
	  * Dunning Rules for overdue invoices
	  */
	public int getC_Dunning_ID();

	public org.compiere.model.I_C_Dunning getC_Dunning() throws RuntimeException;

    /** Column name C_Greeting_ID */
    public static final String COLUMNNAME_C_Greeting_ID = "C_Greeting_ID";

	/** Set Greeting.
	  * Greeting to print on correspondence
	  */
	public void setC_Greeting_ID (int C_Greeting_ID);

	/** Get Greeting.
	  * Greeting to print on correspondence
	  */
	public int getC_Greeting_ID();

	public org.compiere.model.I_C_Greeting getC_Greeting() throws RuntimeException;

    /** Column name C_InvoiceSchedule_ID */
    public static final String COLUMNNAME_C_InvoiceSchedule_ID = "C_InvoiceSchedule_ID";

	/** Set Invoice Schedule.
	  * Schedule for generating Invoices
	  */
	public void setC_InvoiceSchedule_ID (int C_InvoiceSchedule_ID);

	/** Get Invoice Schedule.
	  * Schedule for generating Invoices
	  */
	public int getC_InvoiceSchedule_ID();

	public org.compiere.model.I_C_InvoiceSchedule getC_InvoiceSchedule() throws RuntimeException;

    /** Column name C_Location_ID */
    public static final String COLUMNNAME_C_Location_ID = "C_Location_ID";

	/** Set Address.
	  * Location or Address
	  */
	public void setC_Location_ID (int C_Location_ID);

	/** Get Address.
	  * Location or Address
	  */
	public int getC_Location_ID();

	public org.compiere.model.I_C_Location getC_Location() throws RuntimeException;

    /** Column name C_PaymentTerm_ID */
    public static final String COLUMNNAME_C_PaymentTerm_ID = "C_PaymentTerm_ID";

	/** Set Payment Term.
	  * The terms of Payment (timing, discount)
	  */
	public void setC_PaymentTerm_ID (int C_PaymentTerm_ID);

	/** Get Payment Term.
	  * The terms of Payment (timing, discount)
	  */
	public int getC_PaymentTerm_ID();

	public org.compiere.model.I_C_PaymentTerm getC_PaymentTerm() throws RuntimeException;

    /** Column name C_Region_ID */
    public static final String COLUMNNAME_C_Region_ID = "C_Region_ID";

	/** Set Region.
	  * Identifies a geographical Region
	  */
	public void setC_Region_ID (int C_Region_ID);

	/** Get Region.
	  * Identifies a geographical Region
	  */
	public int getC_Region_ID();

	public org.compiere.model.I_C_Region getC_Region() throws RuntimeException;

    /** Column name C_SalesRegion_ID */
    public static final String COLUMNNAME_C_SalesRegion_ID = "C_SalesRegion_ID";

	/** Set Sales Region.
	  * Sales coverage region
	  */
	public void setC_SalesRegion_ID (int C_SalesRegion_ID);

	/** Get Sales Region.
	  * Sales coverage region
	  */
	public int getC_SalesRegion_ID();

	public org.compiere.model.I_C_SalesRegion getC_SalesRegion() throws RuntimeException;

    /** Column name City */
    public static final String COLUMNNAME_City = "City";

	/** Set City.
	  * Identifies a City
	  */
	public void setCity (String City);

	/** Get City.
	  * Identifies a City
	  */
	public String getCity();

    /** Column name Comments */
    public static final String COLUMNNAME_Comments = "Comments";

	/** Set Comments.
	  * Comments or additional information
	  */
	public void setComments (String Comments);

	/** Get Comments.
	  * Comments or additional information
	  */
	public String getComments();

    /** Column name ContactDescription */
    public static final String COLUMNNAME_ContactDescription = "ContactDescription";

	/** Set Contact Description.
	  * Description of Contact
	  */
	public void setContactDescription (String ContactDescription);

	/** Get Contact Description.
	  * Description of Contact
	  */
	public String getContactDescription();

    /** Column name ContactName */
    public static final String COLUMNNAME_ContactName = "ContactName";

	/** Set Contact Name.
	  * Business Partner Contact Name
	  */
	public void setContactName (String ContactName);

	/** Get Contact Name.
	  * Business Partner Contact Name
	  */
	public String getContactName();

    /** Column name CountryCode */
    public static final String COLUMNNAME_CountryCode = "CountryCode";

	/** Set ISO Country Code.
	  * Upper-case two-letter alphanumeric ISO Country code according to ISO 3166-1 - http://www.chemie.fu-berlin.de/diverse/doc/ISO_3166.html
	  */
	public void setCountryCode (String CountryCode);

	/** Get ISO Country Code.
	  * Upper-case two-letter alphanumeric ISO Country code according to ISO 3166-1 - http://www.chemie.fu-berlin.de/diverse/doc/ISO_3166.html
	  */
	public String getCountryCode();

    /** Column name Created */
    public static final String COLUMNNAME_Created = "Created";

	/** Get Created.
	  * Date this record was created
	  */
	public Timestamp getCreated();

    /** Column name CreatedBy */
    public static final String COLUMNNAME_CreatedBy = "CreatedBy";

	/** Get Created By.
	  * User who created this records
	  */
	public int getCreatedBy();

    /** Column name CustomerAddressID */
    public static final String COLUMNNAME_CustomerAddressID = "CustomerAddressID";

	/** Set Customer Address ID	  */
	public void setCustomerAddressID (String CustomerAddressID);

	/** Get Customer Address ID	  */
	public String getCustomerAddressID();

    /** Column name DUNS */
    public static final String COLUMNNAME_DUNS = "DUNS";

	/** Set D-U-N-S.
	  * Dun &amp;
 Bradstreet Number
	  */
	public void setDUNS (String DUNS);

	/** Get D-U-N-S.
	  * Dun &amp;
 Bradstreet Number
	  */
	public String getDUNS();

    /** Column name Default1099Box_ID */
    public static final String COLUMNNAME_Default1099Box_ID = "Default1099Box_ID";

	/** Set Default 1099 Box	  */
	public void setDefault1099Box_ID (int Default1099Box_ID);

	/** Get Default 1099 Box	  */
	public int getDefault1099Box_ID();

	public org.compiere.model.I_C_1099Box getDefault1099Box() throws RuntimeException;

    /** Column name DeliveryRule */
    public static final String COLUMNNAME_DeliveryRule = "DeliveryRule";

	/** Set Delivery Rule.
	  * Defines the timing of Delivery
	  */
	public void setDeliveryRule (String DeliveryRule);

	/** Get Delivery Rule.
	  * Defines the timing of Delivery
	  */
	public String getDeliveryRule();

    /** Column name DeliveryViaRule */
    public static final String COLUMNNAME_DeliveryViaRule = "DeliveryViaRule";

	/** Set Delivery Via.
	  * How the order will be delivered
	  */
	public void setDeliveryViaRule (String DeliveryViaRule);

	/** Get Delivery Via.
	  * How the order will be delivered
	  */
	public String getDeliveryViaRule();

    /** Column name Description */
    public static final String COLUMNNAME_Description = "Description";

	/** Set Description.
	  * Optional short description of the record
	  */
	public void setDescription (String Description);

	/** Get Description.
	  * Optional short description of the record
	  */
	public String getDescription();

    /** Column name DocumentCopies */
    public static final String COLUMNNAME_DocumentCopies = "DocumentCopies";

	/** Set Document Copies.
	  * Number of copies to be printed
	  */
	public void setDocumentCopies (int DocumentCopies);

	/** Get Document Copies.
	  * Number of copies to be printed
	  */
	public int getDocumentCopies();

    /** Column name DunningGrace */
    public static final String COLUMNNAME_DunningGrace = "DunningGrace";

	/** Set Dunning Grace Date	  */
	public void setDunningGrace (Timestamp DunningGrace);

	/** Get Dunning Grace Date	  */
	public Timestamp getDunningGrace();

    /** Column name EMail */
    public static final String COLUMNNAME_EMail = "EMail";

	/** Set EMail Address.
	  * Electronic Mail Address
	  */
	public void setEMail (String EMail);

	/** Get EMail Address.
	  * Electronic Mail Address
	  */
	public String getEMail();

    /** Column name Fax */
    public static final String COLUMNNAME_Fax = "Fax";

	/** Set Fax.
	  * Facsimile number
	  */
	public void setFax (String Fax);

	/** Get Fax.
	  * Facsimile number
	  */
	public String getFax();

    /** Column name FirstSale */
    public static final String COLUMNNAME_FirstSale = "FirstSale";

	/** Set First Sale.
	  * Date of First Sale
	  */
	public void setFirstSale (Timestamp FirstSale);

	/** Get First Sale.
	  * Date of First Sale
	  */
	public Timestamp getFirstSale();

    /** Column name FlatDiscount */
    public static final String COLUMNNAME_FlatDiscount = "FlatDiscount";

	/** Set Flat Discount %.
	  * Flat discount percentage 
	  */
	public void setFlatDiscount (BigDecimal FlatDiscount);

	/** Get Flat Discount %.
	  * Flat discount percentage 
	  */
	public BigDecimal getFlatDiscount();

    /** Column name GroupValue */
    public static final String COLUMNNAME_GroupValue = "GroupValue";

	/** Set Group Key.
	  * Business Partner Group Key
	  */
	public void setGroupValue (String GroupValue);

	/** Get Group Key.
	  * Business Partner Group Key
	  */
	public String getGroupValue();

    /** Column name ISDN */
    public static final String COLUMNNAME_ISDN = "ISDN";

	/** Set ISDN.
	  * ISDN or modem line
	  */
	public void setISDN (String ISDN);

	/** Get ISDN.
	  * ISDN or modem line
	  */
	public String getISDN();

    /** Column name I_BPartnerJP_ID */
    public static final String COLUMNNAME_I_BPartnerJP_ID = "I_BPartnerJP_ID";

	/** Set JP Import Business Partner	  */
	public void setI_BPartnerJP_ID (int I_BPartnerJP_ID);

	/** Get JP Import Business Partner	  */
	public int getI_BPartnerJP_ID();

    /** Column name I_BPartnerJP_UU */
    public static final String COLUMNNAME_I_BPartnerJP_UU = "I_BPartnerJP_UU";

	/** Set I_BPartnerJP_UU	  */
	public void setI_BPartnerJP_UU (String I_BPartnerJP_UU);

	/** Get I_BPartnerJP_UU	  */
	public String getI_BPartnerJP_UU();

    /** Column name I_ErrorMsg */
    public static final String COLUMNNAME_I_ErrorMsg = "I_ErrorMsg";

	/** Set Import Error Message.
	  * Messages generated from import process
	  */
	public void setI_ErrorMsg (String I_ErrorMsg);

	/** Get Import Error Message.
	  * Messages generated from import process
	  */
	public String getI_ErrorMsg();

    /** Column name I_IsActiveJP */
    public static final String COLUMNNAME_I_IsActiveJP = "I_IsActiveJP";

	/** Set Active(For Import).
	  * Active flag for Import Date
	  */
	public void setI_IsActiveJP (boolean I_IsActiveJP);

	/** Get Active(For Import).
	  * Active flag for Import Date
	  */
	public boolean isI_IsActiveJP();

    /** Column name I_IsImported */
    public static final String COLUMNNAME_I_IsImported = "I_IsImported";

	/** Set Imported.
	  * Has this import been processed
	  */
	public void setI_IsImported (boolean I_IsImported);

	/** Get Imported.
	  * Has this import been processed
	  */
	public boolean isI_IsImported();

    /** Column name InterestAreaName */
    public static final String COLUMNNAME_InterestAreaName = "InterestAreaName";

	/** Set Interest Area.
	  * Name of the Interest Area
	  */
	public void setInterestAreaName (String InterestAreaName);

	/** Get Interest Area.
	  * Name of the Interest Area
	  */
	public String getInterestAreaName();

    /** Column name InvoiceRule */
    public static final String COLUMNNAME_InvoiceRule = "InvoiceRule";

	/** Set Invoice Rule.
	  * Frequency and method of invoicing 
	  */
	public void setInvoiceRule (String InvoiceRule);

	/** Get Invoice Rule.
	  * Frequency and method of invoicing 
	  */
	public String getInvoiceRule();

    /** Column name Invoice_PrintFormat_ID */
    public static final String COLUMNNAME_Invoice_PrintFormat_ID = "Invoice_PrintFormat_ID";

	/** Set Invoice Print Format.
	  * Print Format for printing Invoices
	  */
	public void setInvoice_PrintFormat_ID (int Invoice_PrintFormat_ID);

	/** Get Invoice Print Format.
	  * Print Format for printing Invoices
	  */
	public int getInvoice_PrintFormat_ID();

	public org.compiere.model.I_AD_PrintFormat getInvoice_PrintFormat() throws RuntimeException;

    /** Column name Is1099Vendor */
    public static final String COLUMNNAME_Is1099Vendor = "Is1099Vendor";

	/** Set 1099 Vendor	  */
	public void setIs1099Vendor (boolean Is1099Vendor);

	/** Get 1099 Vendor	  */
	public boolean is1099Vendor();

    /** Column name IsActive */
    public static final String COLUMNNAME_IsActive = "IsActive";

	/** Set Active.
	  * The record is active in the system
	  */
	public void setIsActive (boolean IsActive);

	/** Get Active.
	  * The record is active in the system
	  */
	public boolean isActive();

    /** Column name IsBillTo */
    public static final String COLUMNNAME_IsBillTo = "IsBillTo";

	/** Set Invoice Address.
	  * Business Partner Invoice/Bill Address
	  */
	public void setIsBillTo (boolean IsBillTo);

	/** Get Invoice Address.
	  * Business Partner Invoice/Bill Address
	  */
	public boolean isBillTo();

    /** Column name IsCustomer */
    public static final String COLUMNNAME_IsCustomer = "IsCustomer";

	/** Set Customer.
	  * Indicates if this Business Partner is a Customer
	  */
	public void setIsCustomer (boolean IsCustomer);

	/** Get Customer.
	  * Indicates if this Business Partner is a Customer
	  */
	public boolean isCustomer();

    /** Column name IsDiscountPrinted */
    public static final String COLUMNNAME_IsDiscountPrinted = "IsDiscountPrinted";

	/** Set Discount Printed.
	  * Print Discount on Invoice and Order
	  */
	public void setIsDiscountPrinted (boolean IsDiscountPrinted);

	/** Get Discount Printed.
	  * Print Discount on Invoice and Order
	  */
	public boolean isDiscountPrinted();

    /** Column name IsEmployee */
    public static final String COLUMNNAME_IsEmployee = "IsEmployee";

	/** Set Employee.
	  * Indicates if  this Business Partner is an employee
	  */
	public void setIsEmployee (boolean IsEmployee);

	/** Get Employee.
	  * Indicates if  this Business Partner is an employee
	  */
	public boolean isEmployee();

    /** Column name IsManufacturer */
    public static final String COLUMNNAME_IsManufacturer = "IsManufacturer";

	/** Set Is Manufacturer.
	  * Indicate role of this Business partner as Manufacturer
	  */
	public void setIsManufacturer (boolean IsManufacturer);

	/** Get Is Manufacturer.
	  * Indicate role of this Business partner as Manufacturer
	  */
	public boolean isManufacturer();

    /** Column name IsPOTaxExempt */
    public static final String COLUMNNAME_IsPOTaxExempt = "IsPOTaxExempt";

	/** Set PO Tax exempt.
	  * Business partner is exempt from tax on purchases
	  */
	public void setIsPOTaxExempt (boolean IsPOTaxExempt);

	/** Get PO Tax exempt.
	  * Business partner is exempt from tax on purchases
	  */
	public boolean isPOTaxExempt();

    /** Column name IsPayFrom */
    public static final String COLUMNNAME_IsPayFrom = "IsPayFrom";

	/** Set Pay-From Address.
	  * Business Partner pays from that address and we&#039;
ll send dunning letters there
	  */
	public void setIsPayFrom (boolean IsPayFrom);

	/** Get Pay-From Address.
	  * Business Partner pays from that address and we&#039;
ll send dunning letters there
	  */
	public boolean isPayFrom();

    /** Column name IsProspect */
    public static final String COLUMNNAME_IsProspect = "IsProspect";

	/** Set Prospect.
	  * Indicates this is a Prospect
	  */
	public void setIsProspect (boolean IsProspect);

	/** Get Prospect.
	  * Indicates this is a Prospect
	  */
	public boolean isProspect();

    /** Column name IsRemitTo */
    public static final String COLUMNNAME_IsRemitTo = "IsRemitTo";

	/** Set Remit-To Address.
	  * Business Partner payment address
	  */
	public void setIsRemitTo (boolean IsRemitTo);

	/** Get Remit-To Address.
	  * Business Partner payment address
	  */
	public boolean isRemitTo();

    /** Column name IsSalesRep */
    public static final String COLUMNNAME_IsSalesRep = "IsSalesRep";

	/** Set Sales Representative.
	  * Indicates if  the business partner is a sales representative or company agent
	  */
	public void setIsSalesRep (boolean IsSalesRep);

	/** Get Sales Representative.
	  * Indicates if  the business partner is a sales representative or company agent
	  */
	public boolean isSalesRep();

    /** Column name IsShipTo */
    public static final String COLUMNNAME_IsShipTo = "IsShipTo";

	/** Set Ship Address.
	  * Business Partner Shipment Address
	  */
	public void setIsShipTo (boolean IsShipTo);

	/** Get Ship Address.
	  * Business Partner Shipment Address
	  */
	public boolean isShipTo();

    /** Column name IsSummary */
    public static final String COLUMNNAME_IsSummary = "IsSummary";

	/** Set Summary Level.
	  * This is a summary entity
	  */
	public void setIsSummary (boolean IsSummary);

	/** Get Summary Level.
	  * This is a summary entity
	  */
	public boolean isSummary();

    /** Column name IsTaxExempt */
    public static final String COLUMNNAME_IsTaxExempt = "IsTaxExempt";

	/** Set SO Tax exempt.
	  * Business partner is exempt from tax on sales
	  */
	public void setIsTaxExempt (boolean IsTaxExempt);

	/** Get SO Tax exempt.
	  * Business partner is exempt from tax on sales
	  */
	public boolean isTaxExempt();

    /** Column name IsVendor */
    public static final String COLUMNNAME_IsVendor = "IsVendor";

	/** Set Vendor.
	  * Indicates if this Business Partner is a Vendor
	  */
	public void setIsVendor (boolean IsVendor);

	/** Get Vendor.
	  * Indicates if this Business Partner is a Vendor
	  */
	public boolean isVendor();

    /** Column name JP_BPartner_Location_Name */
    public static final String COLUMNNAME_JP_BPartner_Location_Name = "JP_BPartner_Location_Name";

	/** Set Partner Location(Name)	  */
	public void setJP_BPartner_Location_Name (String JP_BPartner_Location_Name);

	/** Get Partner Location(Name)	  */
	public String getJP_BPartner_Location_Name();

    /** Column name JP_BillSchemaPO_ID */
    public static final String COLUMNNAME_JP_BillSchemaPO_ID = "JP_BillSchemaPO_ID";

	/** Set Payment Request Schema	  */
	public void setJP_BillSchemaPO_ID (int JP_BillSchemaPO_ID);

	/** Get Payment Request Schema	  */
	public int getJP_BillSchemaPO_ID();

	public I_JP_BillSchema getJP_BillSchemaPO() throws RuntimeException;

    /** Column name JP_BillSchemaPO_Value */
    public static final String COLUMNNAME_JP_BillSchemaPO_Value = "JP_BillSchemaPO_Value";

	/** Set Payment Request Schema(Search Key)	  */
	public void setJP_BillSchemaPO_Value (String JP_BillSchemaPO_Value);

	/** Get Payment Request Schema(Search Key)	  */
	public String getJP_BillSchemaPO_Value();

    /** Column name JP_BillSchema_ID */
    public static final String COLUMNNAME_JP_BillSchema_ID = "JP_BillSchema_ID";

	/** Set Bill Schema	  */
	public void setJP_BillSchema_ID (int JP_BillSchema_ID);

	/** Get Bill Schema	  */
	public int getJP_BillSchema_ID();

	public I_JP_BillSchema getJP_BillSchema() throws RuntimeException;

    /** Column name JP_BillSchema_Value */
    public static final String COLUMNNAME_JP_BillSchema_Value = "JP_BillSchema_Value";

	/** Set Bill Schema(Search Key)	  */
	public void setJP_BillSchema_Value (String JP_BillSchema_Value);

	/** Get Bill Schema(Search Key)	  */
	public String getJP_BillSchema_Value();

    /** Column name JP_Bill_PrintFormat_ID */
    public static final String COLUMNNAME_JP_Bill_PrintFormat_ID = "JP_Bill_PrintFormat_ID";

	/** Set Bill Print Format.
	  * Print Format for printing Bills
	  */
	public void setJP_Bill_PrintFormat_ID (int JP_Bill_PrintFormat_ID);

	/** Get Bill Print Format.
	  * Print Format for printing Bills
	  */
	public int getJP_Bill_PrintFormat_ID();

	public org.compiere.model.I_AD_PrintFormat getJP_Bill_PrintFormat() throws RuntimeException;

    /** Column name JP_Bill_PrintFormat_Name */
    public static final String COLUMNNAME_JP_Bill_PrintFormat_Name = "JP_Bill_PrintFormat_Name";

	/** Set Bill Print Format(Name)	  */
	public void setJP_Bill_PrintFormat_Name (String JP_Bill_PrintFormat_Name);

	/** Get Bill Print Format(Name)	  */
	public String getJP_Bill_PrintFormat_Name();

    /** Column name JP_CM_BPartner_ID */
    public static final String COLUMNNAME_JP_CM_BPartner_ID = "JP_CM_BPartner_ID";

	/** Set Consolidated Business Partner.
	  * JPIERE-0636:JPPS
	  */
	public void setJP_CM_BPartner_ID (int JP_CM_BPartner_ID);

	/** Get Consolidated Business Partner.
	  * JPIERE-0636:JPPS
	  */
	public int getJP_CM_BPartner_ID();

	public I_JP_CM_BPartner getJP_CM_BPartner() throws RuntimeException;

    /** Column name JP_CM_BPartner_Location_ID */
    public static final String COLUMNNAME_JP_CM_BPartner_Location_ID = "JP_CM_BPartner_Location_ID";

	/** Set Consolidated Partner Location.
	  * JPIERE-0636:JPPS
	  */
	public void setJP_CM_BPartner_Location_ID (int JP_CM_BPartner_Location_ID);

	/** Get Consolidated Partner Location.
	  * JPIERE-0636:JPPS
	  */
	public int getJP_CM_BPartner_Location_ID();

	public I_JP_CM_BPartner_Location getJP_CM_BPartner_Location() throws RuntimeException;

    /** Column name JP_City_Name */
    public static final String COLUMNNAME_JP_City_Name = "JP_City_Name";

	/** Set City(Name)	  */
	public void setJP_City_Name (String JP_City_Name);

	/** Get City(Name)	  */
	public String getJP_City_Name();

    /** Column name JP_CorporationValue */
    public static final String COLUMNNAME_JP_CorporationValue = "JP_CorporationValue";

	/** Set Corporation(Search Key).
	  * Key of the Corporation
	  */
	public void setJP_CorporationValue (String JP_CorporationValue);

	/** Get Corporation(Search Key).
	  * Key of the Corporation
	  */
	public String getJP_CorporationValue();

    /** Column name JP_Corporation_ID */
    public static final String COLUMNNAME_JP_Corporation_ID = "JP_Corporation_ID";

	/** Set Corporation	  */
	public void setJP_Corporation_ID (int JP_Corporation_ID);

	/** Get Corporation	  */
	public int getJP_Corporation_ID();

	public I_JP_Corporation getJP_Corporation() throws RuntimeException;

    /** Column name JP_Default1099Box_Value */
    public static final String COLUMNNAME_JP_Default1099Box_Value = "JP_Default1099Box_Value";

	/** Set Default 1099 Box(Search Key)	  */
	public void setJP_Default1099Box_Value (String JP_Default1099Box_Value);

	/** Get Default 1099 Box(Search Key)	  */
	public String getJP_Default1099Box_Value();

    /** Column name JP_DiscountSchema_Name */
    public static final String COLUMNNAME_JP_DiscountSchema_Name = "JP_DiscountSchema_Name";

	/** Set Discount Schema(Name)	  */
	public void setJP_DiscountSchema_Name (String JP_DiscountSchema_Name);

	/** Get Discount Schema(Name)	  */
	public String getJP_DiscountSchema_Name();

    /** Column name JP_Dunning_Name */
    public static final String COLUMNNAME_JP_Dunning_Name = "JP_Dunning_Name";

	/** Set Dunning(Name)	  */
	public void setJP_Dunning_Name (String JP_Dunning_Name);

	/** Get Dunning(Name)	  */
	public String getJP_Dunning_Name();

    /** Column name JP_Greeting_Name */
    public static final String COLUMNNAME_JP_Greeting_Name = "JP_Greeting_Name";

	/** Set Greeting(Name)	  */
	public void setJP_Greeting_Name (String JP_Greeting_Name);

	/** Get Greeting(Name)	  */
	public String getJP_Greeting_Name();

    /** Column name JP_InvoiceSchedule_Name */
    public static final String COLUMNNAME_JP_InvoiceSchedule_Name = "JP_InvoiceSchedule_Name";

	/** Set Invoice Schedule(Name).
	  * Schedule for generating Invoices
	  */
	public void setJP_InvoiceSchedule_Name (String JP_InvoiceSchedule_Name);

	/** Get Invoice Schedule(Name).
	  * Schedule for generating Invoices
	  */
	public String getJP_InvoiceSchedule_Name();

    /** Column name JP_Invoice_PrintFormat_Name */
    public static final String COLUMNNAME_JP_Invoice_PrintFormat_Name = "JP_Invoice_PrintFormat_Name";

	/** Set Invoice Print Format(Name).
	  * Print Format for printing Invoices
	  */
	public void setJP_Invoice_PrintFormat_Name (String JP_Invoice_PrintFormat_Name);

	/** Get Invoice Print Format(Name).
	  * Print Format for printing Invoices
	  */
	public String getJP_Invoice_PrintFormat_Name();

    /** Column name JP_Location_Label */
    public static final String COLUMNNAME_JP_Location_Label = "JP_Location_Label";

	/** Set Location Label	  */
	public void setJP_Location_Label (String JP_Location_Label);

	/** Get Location Label	  */
	public String getJP_Location_Label();

    /** Column name JP_Org_Value */
    public static final String COLUMNNAME_JP_Org_Value = "JP_Org_Value";

	/** Set Organization(Search Key)	  */
	public void setJP_Org_Value (String JP_Org_Value);

	/** Get Organization(Search Key)	  */
	public String getJP_Org_Value();

    /** Column name JP_POTaxRounding */
    public static final String COLUMNNAME_JP_POTaxRounding = "JP_POTaxRounding";

	/** Set PO Tax Rounding	  */
	public void setJP_POTaxRounding (String JP_POTaxRounding);

	/** Get PO Tax Rounding	  */
	public String getJP_POTaxRounding();

    /** Column name JP_PO_DiscountSchema_Name */
    public static final String COLUMNNAME_JP_PO_DiscountSchema_Name = "JP_PO_DiscountSchema_Name";

	/** Set PO Discount Schema(Name)	  */
	public void setJP_PO_DiscountSchema_Name (String JP_PO_DiscountSchema_Name);

	/** Get PO Discount Schema(Name)	  */
	public String getJP_PO_DiscountSchema_Name();

    /** Column name JP_PO_PaymentTerm_Value */
    public static final String COLUMNNAME_JP_PO_PaymentTerm_Value = "JP_PO_PaymentTerm_Value";

	/** Set PO Payment Term(Search Key).
	  * The terms of Payment (timing, discount)
	  */
	public void setJP_PO_PaymentTerm_Value (String JP_PO_PaymentTerm_Value);

	/** Get PO Payment Term(Search Key).
	  * The terms of Payment (timing, discount)
	  */
	public String getJP_PO_PaymentTerm_Value();

    /** Column name JP_PO_PriceList_Name */
    public static final String COLUMNNAME_JP_PO_PriceList_Name = "JP_PO_PriceList_Name";

	/** Set Purchase Pricelist(Name)	  */
	public void setJP_PO_PriceList_Name (String JP_PO_PriceList_Name);

	/** Get Purchase Pricelist(Name)	  */
	public String getJP_PO_PriceList_Name();

    /** Column name JP_PaymentTerm_Value */
    public static final String COLUMNNAME_JP_PaymentTerm_Value = "JP_PaymentTerm_Value";

	/** Set Payment Term(Search Key).
	  * The terms of Payment (timing, discount)
	  */
	public void setJP_PaymentTerm_Value (String JP_PaymentTerm_Value);

	/** Get Payment Term(Search Key).
	  * The terms of Payment (timing, discount)
	  */
	public String getJP_PaymentTerm_Value();

    /** Column name JP_PriceList_Name */
    public static final String COLUMNNAME_JP_PriceList_Name = "JP_PriceList_Name";

	/** Set Price List(Name)	  */
	public void setJP_PriceList_Name (String JP_PriceList_Name);

	/** Get Price List(Name)	  */
	public String getJP_PriceList_Name();

    /** Column name JP_Region_Name */
    public static final String COLUMNNAME_JP_Region_Name = "JP_Region_Name";

	/** Set Region(Name)	  */
	public void setJP_Region_Name (String JP_Region_Name);

	/** Get Region(Name)	  */
	public String getJP_Region_Name();

    /** Column name JP_SOTaxRounding */
    public static final String COLUMNNAME_JP_SOTaxRounding = "JP_SOTaxRounding";

	/** Set SO Tax Rounding	  */
	public void setJP_SOTaxRounding (String JP_SOTaxRounding);

	/** Get SO Tax Rounding	  */
	public String getJP_SOTaxRounding();

    /** Column name JP_SalesRegion_Value */
    public static final String COLUMNNAME_JP_SalesRegion_Value = "JP_SalesRegion_Value";

	/** Set Sales Region(Search Key).
	  * Sales coverage region
	  */
	public void setJP_SalesRegion_Value (String JP_SalesRegion_Value);

	/** Get Sales Region(Search Key).
	  * Sales coverage region
	  */
	public String getJP_SalesRegion_Value();

    /** Column name JP_SalesRep_EMail */
    public static final String COLUMNNAME_JP_SalesRep_EMail = "JP_SalesRep_EMail";

	/** Set Sales Rep(E-Mail)	  */
	public void setJP_SalesRep_EMail (String JP_SalesRep_EMail);

	/** Get Sales Rep(E-Mail)	  */
	public String getJP_SalesRep_EMail();

    /** Column name JP_SalesRep_Name */
    public static final String COLUMNNAME_JP_SalesRep_Name = "JP_SalesRep_Name";

	/** Set Sales Rep(Name)	  */
	public void setJP_SalesRep_Name (String JP_SalesRep_Name);

	/** Get Sales Rep(Name)	  */
	public String getJP_SalesRep_Name();

    /** Column name JP_SalesRep_Value */
    public static final String COLUMNNAME_JP_SalesRep_Value = "JP_SalesRep_Value";

	/** Set Sales Rep(Search Key)	  */
	public void setJP_SalesRep_Value (String JP_SalesRep_Value);

	/** Get Sales Rep(Search Key)	  */
	public String getJP_SalesRep_Value();

    /** Column name JP_User_Greeting_ID */
    public static final String COLUMNNAME_JP_User_Greeting_ID = "JP_User_Greeting_ID";

	/** Set Greeting(User).
	  * Greeting to print on correspondence
	  */
	public void setJP_User_Greeting_ID (int JP_User_Greeting_ID);

	/** Get Greeting(User).
	  * Greeting to print on correspondence
	  */
	public int getJP_User_Greeting_ID();

	public org.compiere.model.I_C_Greeting getJP_User_Greeting() throws RuntimeException;

    /** Column name JP_User_Phone */
    public static final String COLUMNNAME_JP_User_Phone = "JP_User_Phone";

	/** Set Phone(User) 	  */
	public void setJP_User_Phone (String JP_User_Phone);

	/** Get Phone(User) 	  */
	public String getJP_User_Phone();

    /** Column name JP_User_Phone2 */
    public static final String COLUMNNAME_JP_User_Phone2 = "JP_User_Phone2";

	/** Set Phone2(User) 	  */
	public void setJP_User_Phone2 (String JP_User_Phone2);

	/** Get Phone2(User) 	  */
	public String getJP_User_Phone2();

    /** Column name JP_User_Value */
    public static final String COLUMNNAME_JP_User_Value = "JP_User_Value";

	/** Set User(Search Key)	  */
	public void setJP_User_Value (String JP_User_Value);

	/** Get User(Search Key)	  */
	public String getJP_User_Value();

    /** Column name M_DiscountSchema_ID */
    public static final String COLUMNNAME_M_DiscountSchema_ID = "M_DiscountSchema_ID";

	/** Set Discount Schema.
	  * Schema to calculate the trade discount percentage
	  */
	public void setM_DiscountSchema_ID (int M_DiscountSchema_ID);

	/** Get Discount Schema.
	  * Schema to calculate the trade discount percentage
	  */
	public int getM_DiscountSchema_ID();

	public org.compiere.model.I_M_DiscountSchema getM_DiscountSchema() throws RuntimeException;

    /** Column name M_PriceList_ID */
    public static final String COLUMNNAME_M_PriceList_ID = "M_PriceList_ID";

	/** Set Price List.
	  * Unique identifier of a Price List
	  */
	public void setM_PriceList_ID (int M_PriceList_ID);

	/** Get Price List.
	  * Unique identifier of a Price List
	  */
	public int getM_PriceList_ID();

	public org.compiere.model.I_M_PriceList getM_PriceList() throws RuntimeException;

    /** Column name NAICS */
    public static final String COLUMNNAME_NAICS = "NAICS";

	/** Set NAICS/SIC.
	  * Standard Industry Code or its successor NAIC - http://www.osha.gov/oshstats/sicser.html
	  */
	public void setNAICS (String NAICS);

	/** Get NAICS/SIC.
	  * Standard Industry Code or its successor NAIC - http://www.osha.gov/oshstats/sicser.html
	  */
	public String getNAICS();

    /** Column name Name */
    public static final String COLUMNNAME_Name = "Name";

	/** Set Name.
	  * Alphanumeric identifier of the entity
	  */
	public void setName (String Name);

	/** Get Name.
	  * Alphanumeric identifier of the entity
	  */
	public String getName();

    /** Column name Name2 */
    public static final String COLUMNNAME_Name2 = "Name2";

	/** Set Name 2.
	  * Additional Name
	  */
	public void setName2 (String Name2);

	/** Get Name 2.
	  * Additional Name
	  */
	public String getName2();

    /** Column name NumberEmployees */
    public static final String COLUMNNAME_NumberEmployees = "NumberEmployees";

	/** Set Employees.
	  * Number of employees
	  */
	public void setNumberEmployees (int NumberEmployees);

	/** Get Employees.
	  * Number of employees
	  */
	public int getNumberEmployees();

    /** Column name POReference */
    public static final String COLUMNNAME_POReference = "POReference";

	/** Set Order Reference.
	  * Transaction Reference Number (Sales Order, Purchase Order) of your Business Partner
	  */
	public void setPOReference (String POReference);

	/** Get Order Reference.
	  * Transaction Reference Number (Sales Order, Purchase Order) of your Business Partner
	  */
	public String getPOReference();

    /** Column name PO_DiscountSchema_ID */
    public static final String COLUMNNAME_PO_DiscountSchema_ID = "PO_DiscountSchema_ID";

	/** Set PO Discount Schema.
	  * Schema to calculate the purchase trade discount percentage
	  */
	public void setPO_DiscountSchema_ID (int PO_DiscountSchema_ID);

	/** Get PO Discount Schema.
	  * Schema to calculate the purchase trade discount percentage
	  */
	public int getPO_DiscountSchema_ID();

	public org.compiere.model.I_M_DiscountSchema getPO_DiscountSchema() throws RuntimeException;

    /** Column name PO_PaymentTerm_ID */
    public static final String COLUMNNAME_PO_PaymentTerm_ID = "PO_PaymentTerm_ID";

	/** Set PO Payment Term.
	  * Payment rules for a purchase order
	  */
	public void setPO_PaymentTerm_ID (int PO_PaymentTerm_ID);

	/** Get PO Payment Term.
	  * Payment rules for a purchase order
	  */
	public int getPO_PaymentTerm_ID();

	public org.compiere.model.I_C_PaymentTerm getPO_PaymentTerm() throws RuntimeException;

    /** Column name PO_PriceList_ID */
    public static final String COLUMNNAME_PO_PriceList_ID = "PO_PriceList_ID";

	/** Set Purchase Price List.
	  * Price List used by this Business Partner
	  */
	public void setPO_PriceList_ID (int PO_PriceList_ID);

	/** Get Purchase Price List.
	  * Price List used by this Business Partner
	  */
	public int getPO_PriceList_ID();

	public org.compiere.model.I_M_PriceList getPO_PriceList() throws RuntimeException;

    /** Column name Password */
    public static final String COLUMNNAME_Password = "Password";

	/** Set Password.
	  * Password of any length (case sensitive)
	  */
	public void setPassword (String Password);

	/** Get Password.
	  * Password of any length (case sensitive)
	  */
	public String getPassword();

    /** Column name PaymentRule */
    public static final String COLUMNNAME_PaymentRule = "PaymentRule";

	/** Set Payment Rule.
	  * How you pay the invoice
	  */
	public void setPaymentRule (String PaymentRule);

	/** Get Payment Rule.
	  * How you pay the invoice
	  */
	public String getPaymentRule();

    /** Column name PaymentRulePO */
    public static final String COLUMNNAME_PaymentRulePO = "PaymentRulePO";

	/** Set Payment Rule.
	  * Purchase payment option
	  */
	public void setPaymentRulePO (String PaymentRulePO);

	/** Get Payment Rule.
	  * Purchase payment option
	  */
	public String getPaymentRulePO();

    /** Column name Phone */
    public static final String COLUMNNAME_Phone = "Phone";

	/** Set Phone.
	  * Identifies a telephone number
	  */
	public void setPhone (String Phone);

	/** Get Phone.
	  * Identifies a telephone number
	  */
	public String getPhone();

    /** Column name Phone2 */
    public static final String COLUMNNAME_Phone2 = "Phone2";

	/** Set 2nd Phone.
	  * Identifies an alternate telephone number.
	  */
	public void setPhone2 (String Phone2);

	/** Get 2nd Phone.
	  * Identifies an alternate telephone number.
	  */
	public String getPhone2();

    /** Column name Postal */
    public static final String COLUMNNAME_Postal = "Postal";

	/** Set ZIP.
	  * Postal code
	  */
	public void setPostal (String Postal);

	/** Get ZIP.
	  * Postal code
	  */
	public String getPostal();

    /** Column name Postal_Add */
    public static final String COLUMNNAME_Postal_Add = "Postal_Add";

	/** Set Additional Zip.
	  * Additional ZIP or Postal code
	  */
	public void setPostal_Add (String Postal_Add);

	/** Get Additional Zip.
	  * Additional ZIP or Postal code
	  */
	public String getPostal_Add();

    /** Column name PotentialLifeTimeValue */
    public static final String COLUMNNAME_PotentialLifeTimeValue = "PotentialLifeTimeValue";

	/** Set Potential Life Time Value.
	  * Total Revenue expected
	  */
	public void setPotentialLifeTimeValue (BigDecimal PotentialLifeTimeValue);

	/** Get Potential Life Time Value.
	  * Total Revenue expected
	  */
	public BigDecimal getPotentialLifeTimeValue();

    /** Column name Processed */
    public static final String COLUMNNAME_Processed = "Processed";

	/** Set Processed.
	  * The document has been processed
	  */
	public void setProcessed (boolean Processed);

	/** Get Processed.
	  * The document has been processed
	  */
	public boolean isProcessed();

    /** Column name Processing */
    public static final String COLUMNNAME_Processing = "Processing";

	/** Set Process Now	  */
	public void setProcessing (boolean Processing);

	/** Get Process Now	  */
	public boolean isProcessing();

    /** Column name R_InterestArea_ID */
    public static final String COLUMNNAME_R_InterestArea_ID = "R_InterestArea_ID";

	/** Set Interest Area.
	  * Interest Area or Topic
	  */
	public void setR_InterestArea_ID (int R_InterestArea_ID);

	/** Get Interest Area.
	  * Interest Area or Topic
	  */
	public int getR_InterestArea_ID();

	public org.compiere.model.I_R_InterestArea getR_InterestArea() throws RuntimeException;

    /** Column name Rating */
    public static final String COLUMNNAME_Rating = "Rating";

	/** Set Rating.
	  * Classification or Importance
	  */
	public void setRating (String Rating);

	/** Get Rating.
	  * Classification or Importance
	  */
	public String getRating();

    /** Column name ReferenceNo */
    public static final String COLUMNNAME_ReferenceNo = "ReferenceNo";

	/** Set Reference No.
	  * Your customer or vendor number at the Business Partner&#039;
s site
	  */
	public void setReferenceNo (String ReferenceNo);

	/** Get Reference No.
	  * Your customer or vendor number at the Business Partner&#039;
s site
	  */
	public String getReferenceNo();

    /** Column name RegionName */
    public static final String COLUMNNAME_RegionName = "RegionName";

	/** Set Region.
	  * Name of the Region
	  */
	public void setRegionName (String RegionName);

	/** Get Region.
	  * Name of the Region
	  */
	public String getRegionName();

    /** Column name SOCreditStatus */
    public static final String COLUMNNAME_SOCreditStatus = "SOCreditStatus";

	/** Set Credit Status.
	  * Business Partner Credit Status
	  */
	public void setSOCreditStatus (String SOCreditStatus);

	/** Get Credit Status.
	  * Business Partner Credit Status
	  */
	public String getSOCreditStatus();

    /** Column name SO_CreditLimit */
    public static final String COLUMNNAME_SO_CreditLimit = "SO_CreditLimit";

	/** Set Credit Limit.
	  * Total outstanding invoice amounts allowed
	  */
	public void setSO_CreditLimit (BigDecimal SO_CreditLimit);

	/** Get Credit Limit.
	  * Total outstanding invoice amounts allowed
	  */
	public BigDecimal getSO_CreditLimit();

    /** Column name SO_Description */
    public static final String COLUMNNAME_SO_Description = "SO_Description";

	/** Set Order Description.
	  * Description to be used on orders
	  */
	public void setSO_Description (String SO_Description);

	/** Get Order Description.
	  * Description to be used on orders
	  */
	public String getSO_Description();

    /** Column name SalesRep_ID */
    public static final String COLUMNNAME_SalesRep_ID = "SalesRep_ID";

	/** Set Sales Rep.
	  * Sales Representative or Company Agent
	  */
	public void setSalesRep_ID (int SalesRep_ID);

	/** Get Sales Rep.
	  * Sales Representative or Company Agent
	  */
	public int getSalesRep_ID();

	public org.compiere.model.I_AD_User getSalesRep() throws RuntimeException;

    /** Column name SalesVolume */
    public static final String COLUMNNAME_SalesVolume = "SalesVolume";

	/** Set Sales Volume in 1.000.
	  * Total Volume of Sales in Thousands of Currency
	  */
	public void setSalesVolume (int SalesVolume);

	/** Get Sales Volume in 1.000.
	  * Total Volume of Sales in Thousands of Currency
	  */
	public int getSalesVolume();

    /** Column name ShareOfCustomer */
    public static final String COLUMNNAME_ShareOfCustomer = "ShareOfCustomer";

	/** Set Share.
	  * Share of Customer&#039;
s business as a percentage
	  */
	public void setShareOfCustomer (int ShareOfCustomer);

	/** Get Share.
	  * Share of Customer&#039;
s business as a percentage
	  */
	public int getShareOfCustomer();

    /** Column name ShelfLifeMinPct */
    public static final String COLUMNNAME_ShelfLifeMinPct = "ShelfLifeMinPct";

	/** Set Min Shelf Life %.
	  * Minimum Shelf Life in percent based on Product Instance Guarantee Date
	  */
	public void setShelfLifeMinPct (int ShelfLifeMinPct);

	/** Get Min Shelf Life %.
	  * Minimum Shelf Life in percent based on Product Instance Guarantee Date
	  */
	public int getShelfLifeMinPct();

    /** Column name TaxID */
    public static final String COLUMNNAME_TaxID = "TaxID";

	/** Set Tax ID.
	  * Tax Identification
	  */
	public void setTaxID (String TaxID);

	/** Get Tax ID.
	  * Tax Identification
	  */
	public String getTaxID();

    /** Column name Title */
    public static final String COLUMNNAME_Title = "Title";

	/** Set Title.
	  * Name this entity is referred to as
	  */
	public void setTitle (String Title);

	/** Get Title.
	  * Name this entity is referred to as
	  */
	public String getTitle();

    /** Column name URL */
    public static final String COLUMNNAME_URL = "URL";

	/** Set URL.
	  * Full URL address - e.g. http://www.idempiere.org
	  */
	public void setURL (String URL);

	/** Get URL.
	  * Full URL address - e.g. http://www.idempiere.org
	  */
	public String getURL();

    /** Column name Updated */
    public static final String COLUMNNAME_Updated = "Updated";

	/** Get Updated.
	  * Date this record was updated
	  */
	public Timestamp getUpdated();

    /** Column name UpdatedBy */
    public static final String COLUMNNAME_UpdatedBy = "UpdatedBy";

	/** Get Updated By.
	  * User who updated this records
	  */
	public int getUpdatedBy();

    /** Column name Value */
    public static final String COLUMNNAME_Value = "Value";

	/** Set Search Key.
	  * Search key for the record in the format required - must be unique
	  */
	public void setValue (String Value);

	/** Get Search Key.
	  * Search key for the record in the format required - must be unique
	  */
	public String getValue();
}
