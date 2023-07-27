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

/** Generated Interface for I_EstimationJP
 *  @author iDempiere (generated) 
 *  @version Release 10
 */
@SuppressWarnings("all")
public interface I_I_EstimationJP 
{

    /** TableName=I_EstimationJP */
    public static final String Table_Name = "I_EstimationJP";

    /** AD_Table_ID=1000098 */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 3 - Client - Org 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(3);

    /** Load Meta Data */

    /** Column name AD_Client_ID */
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";

	/** Get Tenant.
	  * Tenant for this installation.
	  */
	public int getAD_Client_ID();

    /** Column name AD_OrgTrx_ID */
    public static final String COLUMNNAME_AD_OrgTrx_ID = "AD_OrgTrx_ID";

	/** Set Trx Organization.
	  * Performing or initiating organization
	  */
	public void setAD_OrgTrx_ID (int AD_OrgTrx_ID);

	/** Get Trx Organization.
	  * Performing or initiating organization
	  */
	public int getAD_OrgTrx_ID();

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

    /** Column name BPartnerValue */
    public static final String COLUMNNAME_BPartnerValue = "BPartnerValue";

	/** Set Business Partner Key.
	  * Key of the Business Partner
	  */
	public void setBPartnerValue (String BPartnerValue);

	/** Get Business Partner Key.
	  * Key of the Business Partner
	  */
	public String getBPartnerValue();

    /** Column name BillTo_ID */
    public static final String COLUMNNAME_BillTo_ID = "BillTo_ID";

	/** Set Invoice To.
	  * Bill to Address
	  */
	public void setBillTo_ID (int BillTo_ID);

	/** Get Invoice To.
	  * Bill to Address
	  */
	public int getBillTo_ID();

	public org.compiere.model.I_C_BPartner_Location getBillTo() throws RuntimeException;

    /** Column name Bill_BPValue */
    public static final String COLUMNNAME_Bill_BPValue = "Bill_BPValue";

	/** Set Invoice Partner Key	  */
	public void setBill_BPValue (String Bill_BPValue);

	/** Get Invoice Partner Key	  */
	public String getBill_BPValue();

    /** Column name Bill_BPartner_ID */
    public static final String COLUMNNAME_Bill_BPartner_ID = "Bill_BPartner_ID";

	/** Set Invoice Partner.
	  * Business Partner to be invoiced
	  */
	public void setBill_BPartner_ID (int Bill_BPartner_ID);

	/** Get Invoice Partner.
	  * Business Partner to be invoiced
	  */
	public int getBill_BPartner_ID();

	public org.compiere.model.I_C_BPartner getBill_BPartner() throws RuntimeException;

    /** Column name Bill_User_ID */
    public static final String COLUMNNAME_Bill_User_ID = "Bill_User_ID";

	/** Set Invoice Contact.
	  * Business Partner Contact for invoicing
	  */
	public void setBill_User_ID (int Bill_User_ID);

	/** Get Invoice Contact.
	  * Business Partner Contact for invoicing
	  */
	public int getBill_User_ID();

	public org.compiere.model.I_AD_User getBill_User() throws RuntimeException;

    /** Column name C_Activity_ID */
    public static final String COLUMNNAME_C_Activity_ID = "C_Activity_ID";

	/** Set Activity.
	  * Business Activity
	  */
	public void setC_Activity_ID (int C_Activity_ID);

	/** Get Activity.
	  * Business Activity
	  */
	public int getC_Activity_ID();

	public org.compiere.model.I_C_Activity getC_Activity() throws RuntimeException;

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

    /** Column name C_Campaign_ID */
    public static final String COLUMNNAME_C_Campaign_ID = "C_Campaign_ID";

	/** Set Campaign.
	  * Marketing Campaign
	  */
	public void setC_Campaign_ID (int C_Campaign_ID);

	/** Get Campaign.
	  * Marketing Campaign
	  */
	public int getC_Campaign_ID();

	public org.compiere.model.I_C_Campaign getC_Campaign() throws RuntimeException;

    /** Column name C_Charge_ID */
    public static final String COLUMNNAME_C_Charge_ID = "C_Charge_ID";

	/** Set Charge.
	  * Additional document charges
	  */
	public void setC_Charge_ID (int C_Charge_ID);

	/** Get Charge.
	  * Additional document charges
	  */
	public int getC_Charge_ID();

	public org.compiere.model.I_C_Charge getC_Charge() throws RuntimeException;

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

    /** Column name C_Currency_ID */
    public static final String COLUMNNAME_C_Currency_ID = "C_Currency_ID";

	/** Set Currency.
	  * The Currency for this record
	  */
	public void setC_Currency_ID (int C_Currency_ID);

	/** Get Currency.
	  * The Currency for this record
	  */
	public int getC_Currency_ID();

	public org.compiere.model.I_C_Currency getC_Currency() throws RuntimeException;

    /** Column name C_DocType_ID */
    public static final String COLUMNNAME_C_DocType_ID = "C_DocType_ID";

	/** Set Document Type.
	  * Document type or rules
	  */
	public void setC_DocType_ID (int C_DocType_ID);

	/** Get Document Type.
	  * Document type or rules
	  */
	public int getC_DocType_ID();

	public org.compiere.model.I_C_DocType getC_DocType() throws RuntimeException;

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

    /** Column name C_OrderSourceValue */
    public static final String COLUMNNAME_C_OrderSourceValue = "C_OrderSourceValue";

	/** Set Order Source Key	  */
	public void setC_OrderSourceValue (String C_OrderSourceValue);

	/** Get Order Source Key	  */
	public String getC_OrderSourceValue();

    /** Column name C_OrderSource_ID */
    public static final String COLUMNNAME_C_OrderSource_ID = "C_OrderSource_ID";

	/** Set Order Source	  */
	public void setC_OrderSource_ID (int C_OrderSource_ID);

	/** Get Order Source	  */
	public int getC_OrderSource_ID();

	public org.compiere.model.I_C_OrderSource getC_OrderSource() throws RuntimeException;

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

    /** Column name C_Project_ID */
    public static final String COLUMNNAME_C_Project_ID = "C_Project_ID";

	/** Set Project.
	  * Financial Project
	  */
	public void setC_Project_ID (int C_Project_ID);

	/** Get Project.
	  * Financial Project
	  */
	public int getC_Project_ID();

	public org.compiere.model.I_C_Project getC_Project() throws RuntimeException;

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

    /** Column name C_Tax_ID */
    public static final String COLUMNNAME_C_Tax_ID = "C_Tax_ID";

	/** Set Tax.
	  * Tax identifier
	  */
	public void setC_Tax_ID (int C_Tax_ID);

	/** Get Tax.
	  * Tax identifier
	  */
	public int getC_Tax_ID();

	public org.compiere.model.I_C_Tax getC_Tax() throws RuntimeException;

    /** Column name C_UOM_ID */
    public static final String COLUMNNAME_C_UOM_ID = "C_UOM_ID";

	/** Set UOM.
	  * Unit of Measure
	  */
	public void setC_UOM_ID (int C_UOM_ID);

	/** Get UOM.
	  * Unit of Measure
	  */
	public int getC_UOM_ID();

	public org.compiere.model.I_C_UOM getC_UOM() throws RuntimeException;

    /** Column name ChargeName */
    public static final String COLUMNNAME_ChargeName = "ChargeName";

	/** Set Charge Name.
	  * Name of the Charge
	  */
	public void setChargeName (String ChargeName);

	/** Get Charge Name.
	  * Name of the Charge
	  */
	public String getChargeName();

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

    /** Column name DateAcct */
    public static final String COLUMNNAME_DateAcct = "DateAcct";

	/** Set Account Date.
	  * Accounting Date
	  */
	public void setDateAcct (Timestamp DateAcct);

	/** Get Account Date.
	  * Accounting Date
	  */
	public Timestamp getDateAcct();

    /** Column name DateOrdered */
    public static final String COLUMNNAME_DateOrdered = "DateOrdered";

	/** Set Date Ordered.
	  * Date of Order
	  */
	public void setDateOrdered (Timestamp DateOrdered);

	/** Get Date Ordered.
	  * Date of Order
	  */
	public Timestamp getDateOrdered();

    /** Column name DatePromised */
    public static final String COLUMNNAME_DatePromised = "DatePromised";

	/** Set Date Promised.
	  * Date Order was promised
	  */
	public void setDatePromised (Timestamp DatePromised);

	/** Get Date Promised.
	  * Date Order was promised
	  */
	public Timestamp getDatePromised();

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

    /** Column name DocTypeName */
    public static final String COLUMNNAME_DocTypeName = "DocTypeName";

	/** Set Document Type Name.
	  * Name of the Document Type
	  */
	public void setDocTypeName (String DocTypeName);

	/** Get Document Type Name.
	  * Name of the Document Type
	  */
	public String getDocTypeName();

    /** Column name DocumentNo */
    public static final String COLUMNNAME_DocumentNo = "DocumentNo";

	/** Set Document No.
	  * Document sequence number of the document
	  */
	public void setDocumentNo (String DocumentNo);

	/** Get Document No.
	  * Document sequence number of the document
	  */
	public String getDocumentNo();

    /** Column name DropShip_BPartner_ID */
    public static final String COLUMNNAME_DropShip_BPartner_ID = "DropShip_BPartner_ID";

	/** Set Drop Ship Business Partner.
	  * Business Partner to ship to
	  */
	public void setDropShip_BPartner_ID (int DropShip_BPartner_ID);

	/** Get Drop Ship Business Partner.
	  * Business Partner to ship to
	  */
	public int getDropShip_BPartner_ID();

	public org.compiere.model.I_C_BPartner getDropShip_BPartner() throws RuntimeException;

    /** Column name DropShip_Location_ID */
    public static final String COLUMNNAME_DropShip_Location_ID = "DropShip_Location_ID";

	/** Set Drop Shipment Location.
	  * Business Partner Location for shipping to
	  */
	public void setDropShip_Location_ID (int DropShip_Location_ID);

	/** Get Drop Shipment Location.
	  * Business Partner Location for shipping to
	  */
	public int getDropShip_Location_ID();

	public org.compiere.model.I_C_BPartner_Location getDropShip_Location() throws RuntimeException;

    /** Column name DropShip_User_ID */
    public static final String COLUMNNAME_DropShip_User_ID = "DropShip_User_ID";

	/** Set Drop Shipment Contact.
	  * Business Partner Contact for drop shipment
	  */
	public void setDropShip_User_ID (int DropShip_User_ID);

	/** Get Drop Shipment Contact.
	  * Business Partner Contact for drop shipment
	  */
	public int getDropShip_User_ID();

	public org.compiere.model.I_AD_User getDropShip_User() throws RuntimeException;

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

    /** Column name FreightAmt */
    public static final String COLUMNNAME_FreightAmt = "FreightAmt";

	/** Set Freight Amount.
	  * Freight Amount 
	  */
	public void setFreightAmt (BigDecimal FreightAmt);

	/** Get Freight Amount.
	  * Freight Amount 
	  */
	public BigDecimal getFreightAmt();

    /** Column name FreightCostRule */
    public static final String COLUMNNAME_FreightCostRule = "FreightCostRule";

	/** Set Freight Cost Rule.
	  * Method for charging Freight
	  */
	public void setFreightCostRule (String FreightCostRule);

	/** Get Freight Cost Rule.
	  * Method for charging Freight
	  */
	public String getFreightCostRule();

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

    /** Column name I_EstimationJP_ID */
    public static final String COLUMNNAME_I_EstimationJP_ID = "I_EstimationJP_ID";

	/** Set Import Estimation	  */
	public void setI_EstimationJP_ID (int I_EstimationJP_ID);

	/** Get Import Estimation	  */
	public int getI_EstimationJP_ID();

    /** Column name I_EstimationJP_UU */
    public static final String COLUMNNAME_I_EstimationJP_UU = "I_EstimationJP_UU";

	/** Set I_EstimationJP_UU	  */
	public void setI_EstimationJP_UU (String I_EstimationJP_UU);

	/** Get I_EstimationJP_UU	  */
	public String getI_EstimationJP_UU();

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

    /** Column name IsDropShip */
    public static final String COLUMNNAME_IsDropShip = "IsDropShip";

	/** Set Drop Shipment.
	  * Drop Shipments are sent directly to the Drop Shipment Location
	  */
	public void setIsDropShip (boolean IsDropShip);

	/** Get Drop Shipment.
	  * Drop Shipments are sent directly to the Drop Shipment Location
	  */
	public boolean isDropShip();

    /** Column name IsSOTrx */
    public static final String COLUMNNAME_IsSOTrx = "IsSOTrx";

	/** Set Sales Transaction.
	  * This is a Sales Transaction
	  */
	public void setIsSOTrx (boolean IsSOTrx);

	/** Get Sales Transaction.
	  * This is a Sales Transaction
	  */
	public boolean isSOTrx();

    /** Column name IsSelectBillToJP */
    public static final String COLUMNNAME_IsSelectBillToJP = "IsSelectBillToJP";

	/** Set Select Bill to BP	  */
	public void setIsSelectBillToJP (boolean IsSelectBillToJP);

	/** Get Select Bill to BP	  */
	public boolean isSelectBillToJP();

    /** Column name JP_Activity_Value */
    public static final String COLUMNNAME_JP_Activity_Value = "JP_Activity_Value";

	/** Set Activity(Search Key)	  */
	public void setJP_Activity_Value (String JP_Activity_Value);

	/** Get Activity(Search Key)	  */
	public String getJP_Activity_Value();

    /** Column name JP_BP_Org_Value */
    public static final String COLUMNNAME_JP_BP_Org_Value = "JP_BP_Org_Value";

	/** Set BP Organization(Search Key)	  */
	public void setJP_BP_Org_Value (String JP_BP_Org_Value);

	/** Get BP Organization(Search Key)	  */
	public String getJP_BP_Org_Value();

    /** Column name JP_BPartner_Location_Name */
    public static final String COLUMNNAME_JP_BPartner_Location_Name = "JP_BPartner_Location_Name";

	/** Set Partner Location(Name)	  */
	public void setJP_BPartner_Location_Name (String JP_BPartner_Location_Name);

	/** Get Partner Location(Name)	  */
	public String getJP_BPartner_Location_Name();

    /** Column name JP_Bill_BP_Location_Name */
    public static final String COLUMNNAME_JP_Bill_BP_Location_Name = "JP_Bill_BP_Location_Name";

	/** Set Bill Partner Location(Name)	  */
	public void setJP_Bill_BP_Location_Name (String JP_Bill_BP_Location_Name);

	/** Get Bill Partner Location(Name)	  */
	public String getJP_Bill_BP_Location_Name();

    /** Column name JP_Bill_User_EMail */
    public static final String COLUMNNAME_JP_Bill_User_EMail = "JP_Bill_User_EMail";

	/** Set Invoice Contact(EMail).
	  * Business Partner Contact for invoicing
	  */
	public void setJP_Bill_User_EMail (String JP_Bill_User_EMail);

	/** Get Invoice Contact(EMail).
	  * Business Partner Contact for invoicing
	  */
	public String getJP_Bill_User_EMail();

    /** Column name JP_Bill_User_Name */
    public static final String COLUMNNAME_JP_Bill_User_Name = "JP_Bill_User_Name";

	/** Set Bill User Name	  */
	public void setJP_Bill_User_Name (String JP_Bill_User_Name);

	/** Get Bill User Name	  */
	public String getJP_Bill_User_Name();

    /** Column name JP_Bill_User_Value */
    public static final String COLUMNNAME_JP_Bill_User_Value = "JP_Bill_User_Value";

	/** Set Invoice Contact(Search Key).
	  * Business Partner Contact for invoicing
	  */
	public void setJP_Bill_User_Value (String JP_Bill_User_Value);

	/** Get Invoice Contact(Search Key).
	  * Business Partner Contact for invoicing
	  */
	public String getJP_Bill_User_Value();

    /** Column name JP_Campaign_Value */
    public static final String COLUMNNAME_JP_Campaign_Value = "JP_Campaign_Value";

	/** Set Campaign(Search Key)	  */
	public void setJP_Campaign_Value (String JP_Campaign_Value);

	/** Get Campaign(Search Key)	  */
	public String getJP_Campaign_Value();

    /** Column name JP_CommunicationColumn */
    public static final String COLUMNNAME_JP_CommunicationColumn = "JP_CommunicationColumn";

	/** Set Communication Column	  */
	public void setJP_CommunicationColumn (String JP_CommunicationColumn);

	/** Get Communication Column	  */
	public String getJP_CommunicationColumn();

    /** Column name JP_DocTypeNameSO */
    public static final String COLUMNNAME_JP_DocTypeNameSO = "JP_DocTypeNameSO";

	/** Set Doc Type Name SO	  */
	public void setJP_DocTypeNameSO (String JP_DocTypeNameSO);

	/** Get Doc Type Name SO	  */
	public String getJP_DocTypeNameSO();

    /** Column name JP_DocTypeSO_ID */
    public static final String COLUMNNAME_JP_DocTypeSO_ID = "JP_DocTypeSO_ID";

	/** Set Doc Type SO	  */
	public void setJP_DocTypeSO_ID (int JP_DocTypeSO_ID);

	/** Get Doc Type SO	  */
	public int getJP_DocTypeSO_ID();

	public org.compiere.model.I_C_DocType getJP_DocTypeSO() throws RuntimeException;

    /** Column name JP_DropShip_BP_Location_Name */
    public static final String COLUMNNAME_JP_DropShip_BP_Location_Name = "JP_DropShip_BP_Location_Name";

	/** Set Drop Ship BP Location(Name)	  */
	public void setJP_DropShip_BP_Location_Name (String JP_DropShip_BP_Location_Name);

	/** Get Drop Ship BP Location(Name)	  */
	public String getJP_DropShip_BP_Location_Name();

    /** Column name JP_DropShip_BP_Value */
    public static final String COLUMNNAME_JP_DropShip_BP_Value = "JP_DropShip_BP_Value";

	/** Set Drop Ship BP(Search Key)	  */
	public void setJP_DropShip_BP_Value (String JP_DropShip_BP_Value);

	/** Get Drop Ship BP(Search Key)	  */
	public String getJP_DropShip_BP_Value();

    /** Column name JP_DropShip_User_EMail */
    public static final String COLUMNNAME_JP_DropShip_User_EMail = "JP_DropShip_User_EMail";

	/** Set Drop Shipment Contact(E-Mail)	  */
	public void setJP_DropShip_User_EMail (String JP_DropShip_User_EMail);

	/** Get Drop Shipment Contact(E-Mail)	  */
	public String getJP_DropShip_User_EMail();

    /** Column name JP_DropShip_User_Name */
    public static final String COLUMNNAME_JP_DropShip_User_Name = "JP_DropShip_User_Name";

	/** Set Drop Shipment Contact(Name)	  */
	public void setJP_DropShip_User_Name (String JP_DropShip_User_Name);

	/** Get Drop Shipment Contact(Name)	  */
	public String getJP_DropShip_User_Name();

    /** Column name JP_DropShip_User_Value */
    public static final String COLUMNNAME_JP_DropShip_User_Value = "JP_DropShip_User_Value";

	/** Set Drop Shipment Contact(Search Key)	  */
	public void setJP_DropShip_User_Value (String JP_DropShip_User_Value);

	/** Get Drop Shipment Contact(Search Key)	  */
	public String getJP_DropShip_User_Value();

    /** Column name JP_EstimationLine_ID */
    public static final String COLUMNNAME_JP_EstimationLine_ID = "JP_EstimationLine_ID";

	/** Set Estimation &amp;
 Handwritten Line	  */
	public void setJP_EstimationLine_ID (int JP_EstimationLine_ID);

	/** Get Estimation &amp;
 Handwritten Line	  */
	public int getJP_EstimationLine_ID();

	public I_JP_EstimationLine getJP_EstimationLine() throws RuntimeException;

    /** Column name JP_Estimation_ID */
    public static final String COLUMNNAME_JP_Estimation_ID = "JP_Estimation_ID";

	/** Set Estimation &amp;
 Handwritten	  */
	public void setJP_Estimation_ID (int JP_Estimation_ID);

	/** Get Estimation &amp;
 Handwritten	  */
	public int getJP_Estimation_ID();

	public I_JP_Estimation getJP_Estimation() throws RuntimeException;

    /** Column name JP_Line_Activity_ID */
    public static final String COLUMNNAME_JP_Line_Activity_ID = "JP_Line_Activity_ID";

	/** Set Activity of Line	  */
	public void setJP_Line_Activity_ID (int JP_Line_Activity_ID);

	/** Get Activity of Line	  */
	public int getJP_Line_Activity_ID();

	public org.compiere.model.I_C_Activity getJP_Line_Activity() throws RuntimeException;

    /** Column name JP_Line_Activity_Value */
    public static final String COLUMNNAME_JP_Line_Activity_Value = "JP_Line_Activity_Value";

	/** Set Activity of Line(Search Key)	  */
	public void setJP_Line_Activity_Value (String JP_Line_Activity_Value);

	/** Get Activity of Line(Search Key)	  */
	public String getJP_Line_Activity_Value();

    /** Column name JP_Line_Campaign_ID */
    public static final String COLUMNNAME_JP_Line_Campaign_ID = "JP_Line_Campaign_ID";

	/** Set Campaign of Line	  */
	public void setJP_Line_Campaign_ID (int JP_Line_Campaign_ID);

	/** Get Campaign of Line	  */
	public int getJP_Line_Campaign_ID();

	public org.compiere.model.I_C_Campaign getJP_Line_Campaign() throws RuntimeException;

    /** Column name JP_Line_Campaign_Value */
    public static final String COLUMNNAME_JP_Line_Campaign_Value = "JP_Line_Campaign_Value";

	/** Set Campaign of Line(Search Key)	  */
	public void setJP_Line_Campaign_Value (String JP_Line_Campaign_Value);

	/** Get Campaign of Line(Search Key)	  */
	public String getJP_Line_Campaign_Value();

    /** Column name JP_Line_OrgTrx_ID */
    public static final String COLUMNNAME_JP_Line_OrgTrx_ID = "JP_Line_OrgTrx_ID";

	/** Set Trx Org of Line	  */
	public void setJP_Line_OrgTrx_ID (int JP_Line_OrgTrx_ID);

	/** Get Trx Org of Line	  */
	public int getJP_Line_OrgTrx_ID();

    /** Column name JP_Line_OrgTrx_Value */
    public static final String COLUMNNAME_JP_Line_OrgTrx_Value = "JP_Line_OrgTrx_Value";

	/** Set Trx Org of Line(Search Key)	  */
	public void setJP_Line_OrgTrx_Value (String JP_Line_OrgTrx_Value);

	/** Get Trx Org of Line(Search Key)	  */
	public String getJP_Line_OrgTrx_Value();

    /** Column name JP_Line_Project_ID */
    public static final String COLUMNNAME_JP_Line_Project_ID = "JP_Line_Project_ID";

	/** Set Project of Line	  */
	public void setJP_Line_Project_ID (int JP_Line_Project_ID);

	/** Get Project of Line	  */
	public int getJP_Line_Project_ID();

	public org.compiere.model.I_C_Project getJP_Line_Project() throws RuntimeException;

    /** Column name JP_Line_Project_Value */
    public static final String COLUMNNAME_JP_Line_Project_Value = "JP_Line_Project_Value";

	/** Set Project of Line(Search Key)	  */
	public void setJP_Line_Project_Value (String JP_Line_Project_Value);

	/** Get Project of Line(Search Key)	  */
	public String getJP_Line_Project_Value();

    /** Column name JP_Line_User1_ID */
    public static final String COLUMNNAME_JP_Line_User1_ID = "JP_Line_User1_ID";

	/** Set User Element List 1 of Line.
	  * User defined list element #1
	  */
	public void setJP_Line_User1_ID (int JP_Line_User1_ID);

	/** Get User Element List 1 of Line.
	  * User defined list element #1
	  */
	public int getJP_Line_User1_ID();

	public org.compiere.model.I_C_ElementValue getJP_Line_User1() throws RuntimeException;

    /** Column name JP_Line_User2_ID */
    public static final String COLUMNNAME_JP_Line_User2_ID = "JP_Line_User2_ID";

	/** Set User Element List 2 of Line.
	  * User defined list element #2
	  */
	public void setJP_Line_User2_ID (int JP_Line_User2_ID);

	/** Get User Element List 2 of Line.
	  * User defined list element #2
	  */
	public int getJP_Line_User2_ID();

	public org.compiere.model.I_C_ElementValue getJP_Line_User2() throws RuntimeException;

    /** Column name JP_Line_UserElement1_Value */
    public static final String COLUMNNAME_JP_Line_UserElement1_Value = "JP_Line_UserElement1_Value";

	/** Set User Element List 1 of Line(Search key)	  */
	public void setJP_Line_UserElement1_Value (String JP_Line_UserElement1_Value);

	/** Get User Element List 1 of Line(Search key)	  */
	public String getJP_Line_UserElement1_Value();

    /** Column name JP_Line_UserElement2_Value */
    public static final String COLUMNNAME_JP_Line_UserElement2_Value = "JP_Line_UserElement2_Value";

	/** Set User Element List 2 of Line(Search key)	  */
	public void setJP_Line_UserElement2_Value (String JP_Line_UserElement2_Value);

	/** Get User Element List 2 of Line(Search key)	  */
	public String getJP_Line_UserElement2_Value();

    /** Column name JP_Location_Label */
    public static final String COLUMNNAME_JP_Location_Label = "JP_Location_Label";

	/** Set Location Label	  */
	public void setJP_Location_Label (String JP_Location_Label);

	/** Get Location Label	  */
	public String getJP_Location_Label();

    /** Column name JP_Locator_ID */
    public static final String COLUMNNAME_JP_Locator_ID = "JP_Locator_ID";

	/** Set Locator	  */
	public void setJP_Locator_ID (int JP_Locator_ID);

	/** Get Locator	  */
	public int getJP_Locator_ID();

	public org.compiere.model.I_M_Locator getJP_Locator() throws RuntimeException;

    /** Column name JP_Locator_Value */
    public static final String COLUMNNAME_JP_Locator_Value = "JP_Locator_Value";

	/** Set Locator(Search Key).
	  * Warehouse Locator
	  */
	public void setJP_Locator_Value (String JP_Locator_Value);

	/** Get Locator(Search Key).
	  * Warehouse Locator
	  */
	public String getJP_Locator_Value();

    /** Column name JP_OrgTrx_Value */
    public static final String COLUMNNAME_JP_OrgTrx_Value = "JP_OrgTrx_Value";

	/** Set Trx Organization(Search Key)	  */
	public void setJP_OrgTrx_Value (String JP_OrgTrx_Value);

	/** Get Trx Organization(Search Key)	  */
	public String getJP_OrgTrx_Value();

    /** Column name JP_Org_Value */
    public static final String COLUMNNAME_JP_Org_Value = "JP_Org_Value";

	/** Set Organization(Search Key)	  */
	public void setJP_Org_Value (String JP_Org_Value);

	/** Get Organization(Search Key)	  */
	public String getJP_Org_Value();

    /** Column name JP_PriceList_Name */
    public static final String COLUMNNAME_JP_PriceList_Name = "JP_PriceList_Name";

	/** Set Price List(Name)	  */
	public void setJP_PriceList_Name (String JP_PriceList_Name);

	/** Get Price List(Name)	  */
	public String getJP_PriceList_Name();

    /** Column name JP_Project_Value */
    public static final String COLUMNNAME_JP_Project_Value = "JP_Project_Value";

	/** Set Project(Search Key)	  */
	public void setJP_Project_Value (String JP_Project_Value);

	/** Get Project(Search Key)	  */
	public String getJP_Project_Value();

    /** Column name JP_Remarks */
    public static final String COLUMNNAME_JP_Remarks = "JP_Remarks";

	/** Set Remarks.
	  * JPIERE-0490:JPBP
	  */
	public void setJP_Remarks (String JP_Remarks);

	/** Get Remarks.
	  * JPIERE-0490:JPBP
	  */
	public String getJP_Remarks();

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

    /** Column name JP_Shipper_Name */
    public static final String COLUMNNAME_JP_Shipper_Name = "JP_Shipper_Name";

	/** Set Shipper(Name).
	  * Method or manner of product delivery
	  */
	public void setJP_Shipper_Name (String JP_Shipper_Name);

	/** Get Shipper(Name).
	  * Method or manner of product delivery
	  */
	public String getJP_Shipper_Name();

    /** Column name JP_Subject */
    public static final String COLUMNNAME_JP_Subject = "JP_Subject";

	/** Set Subject.
	  * JPIERE-0490:JPBP
	  */
	public void setJP_Subject (String JP_Subject);

	/** Get Subject.
	  * JPIERE-0490:JPBP
	  */
	public String getJP_Subject();

    /** Column name JP_UserElement1_Value */
    public static final String COLUMNNAME_JP_UserElement1_Value = "JP_UserElement1_Value";

	/** Set User Element List 1(Search key)	  */
	public void setJP_UserElement1_Value (String JP_UserElement1_Value);

	/** Get User Element List 1(Search key)	  */
	public String getJP_UserElement1_Value();

    /** Column name JP_UserElement2_Value */
    public static final String COLUMNNAME_JP_UserElement2_Value = "JP_UserElement2_Value";

	/** Set User Element List 2(Search key)	  */
	public void setJP_UserElement2_Value (String JP_UserElement2_Value);

	/** Get User Element List 2(Search key)	  */
	public String getJP_UserElement2_Value();

    /** Column name JP_User_Value */
    public static final String COLUMNNAME_JP_User_Value = "JP_User_Value";

	/** Set User(Search Key)	  */
	public void setJP_User_Value (String JP_User_Value);

	/** Get User(Search Key)	  */
	public String getJP_User_Value();

    /** Column name JP_Warehouse_Value */
    public static final String COLUMNNAME_JP_Warehouse_Value = "JP_Warehouse_Value";

	/** Set Org Warehouse(Search Key)	  */
	public void setJP_Warehouse_Value (String JP_Warehouse_Value);

	/** Get Org Warehouse(Search Key)	  */
	public String getJP_Warehouse_Value();

    /** Column name Line */
    public static final String COLUMNNAME_Line = "Line";

	/** Set Line No.
	  * Unique line for this document
	  */
	public void setLine (int Line);

	/** Get Line No.
	  * Unique line for this document
	  */
	public int getLine();

    /** Column name LineDescription */
    public static final String COLUMNNAME_LineDescription = "LineDescription";

	/** Set Line Description.
	  * Description of the Line
	  */
	public void setLineDescription (String LineDescription);

	/** Get Line Description.
	  * Description of the Line
	  */
	public String getLineDescription();

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

    /** Column name M_Product_ID */
    public static final String COLUMNNAME_M_Product_ID = "M_Product_ID";

	/** Set Product.
	  * Product, Service, Item
	  */
	public void setM_Product_ID (int M_Product_ID);

	/** Get Product.
	  * Product, Service, Item
	  */
	public int getM_Product_ID();

	public org.compiere.model.I_M_Product getM_Product() throws RuntimeException;

    /** Column name M_Shipper_ID */
    public static final String COLUMNNAME_M_Shipper_ID = "M_Shipper_ID";

	/** Set Shipper.
	  * Method or manner of product delivery
	  */
	public void setM_Shipper_ID (int M_Shipper_ID);

	/** Get Shipper.
	  * Method or manner of product delivery
	  */
	public int getM_Shipper_ID();

	public org.compiere.model.I_M_Shipper getM_Shipper() throws RuntimeException;

    /** Column name M_Warehouse_ID */
    public static final String COLUMNNAME_M_Warehouse_ID = "M_Warehouse_ID";

	/** Set Org Warehouse.
	  * Storage Warehouse and Service Point
	  */
	public void setM_Warehouse_ID (int M_Warehouse_ID);

	/** Get Org Warehouse.
	  * Storage Warehouse and Service Point
	  */
	public int getM_Warehouse_ID();

	public org.compiere.model.I_M_Warehouse getM_Warehouse() throws RuntimeException;

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

    /** Column name PaymentTermValue */
    public static final String COLUMNNAME_PaymentTermValue = "PaymentTermValue";

	/** Set Payment Term Key.
	  * Key of the Payment Term
	  */
	public void setPaymentTermValue (String PaymentTermValue);

	/** Get Payment Term Key.
	  * Key of the Payment Term
	  */
	public String getPaymentTermValue();

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

    /** Column name PriceActual */
    public static final String COLUMNNAME_PriceActual = "PriceActual";

	/** Set Unit Price.
	  * Actual Price 
	  */
	public void setPriceActual (BigDecimal PriceActual);

	/** Get Unit Price.
	  * Actual Price 
	  */
	public BigDecimal getPriceActual();

    /** Column name PriceEntered */
    public static final String COLUMNNAME_PriceEntered = "PriceEntered";

	/** Set Price.
	  * Price Entered - the price based on the selected/base UoM
	  */
	public void setPriceEntered (BigDecimal PriceEntered);

	/** Get Price.
	  * Price Entered - the price based on the selected/base UoM
	  */
	public BigDecimal getPriceEntered();

    /** Column name PriorityRule */
    public static final String COLUMNNAME_PriorityRule = "PriorityRule";

	/** Set Priority.
	  * Priority of a document
	  */
	public void setPriorityRule (String PriorityRule);

	/** Get Priority.
	  * Priority of a document
	  */
	public String getPriorityRule();

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

    /** Column name ProductValue */
    public static final String COLUMNNAME_ProductValue = "ProductValue";

	/** Set Product Key.
	  * Key of the Product
	  */
	public void setProductValue (String ProductValue);

	/** Get Product Key.
	  * Key of the Product
	  */
	public String getProductValue();

    /** Column name QtyEntered */
    public static final String COLUMNNAME_QtyEntered = "QtyEntered";

	/** Set Quantity.
	  * The Quantity Entered is based on the selected UoM
	  */
	public void setQtyEntered (BigDecimal QtyEntered);

	/** Get Quantity.
	  * The Quantity Entered is based on the selected UoM
	  */
	public BigDecimal getQtyEntered();

    /** Column name QtyOrdered */
    public static final String COLUMNNAME_QtyOrdered = "QtyOrdered";

	/** Set Ordered Qty.
	  * Ordered Quantity
	  */
	public void setQtyOrdered (BigDecimal QtyOrdered);

	/** Get Ordered Qty.
	  * Ordered Quantity
	  */
	public BigDecimal getQtyOrdered();

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

    /** Column name SKU */
    public static final String COLUMNNAME_SKU = "SKU";

	/** Set SKU.
	  * Stock Keeping Unit
	  */
	public void setSKU (String SKU);

	/** Get SKU.
	  * Stock Keeping Unit
	  */
	public String getSKU();

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

    /** Column name TaxAmt */
    public static final String COLUMNNAME_TaxAmt = "TaxAmt";

	/** Set Tax Amount.
	  * Tax Amount for a document
	  */
	public void setTaxAmt (BigDecimal TaxAmt);

	/** Get Tax Amount.
	  * Tax Amount for a document
	  */
	public BigDecimal getTaxAmt();

    /** Column name TaxIndicator */
    public static final String COLUMNNAME_TaxIndicator = "TaxIndicator";

	/** Set Tax Indicator.
	  * Short form for Tax to be printed on documents
	  */
	public void setTaxIndicator (String TaxIndicator);

	/** Get Tax Indicator.
	  * Short form for Tax to be printed on documents
	  */
	public String getTaxIndicator();

    /** Column name UPC */
    public static final String COLUMNNAME_UPC = "UPC";

	/** Set UPC/EAN.
	  * Bar Code (Universal Product Code or its superset European Article Number)
	  */
	public void setUPC (String UPC);

	/** Get UPC/EAN.
	  * Bar Code (Universal Product Code or its superset European Article Number)
	  */
	public String getUPC();

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

    /** Column name User1_ID */
    public static final String COLUMNNAME_User1_ID = "User1_ID";

	/** Set User Element List 1.
	  * User defined list element #1
	  */
	public void setUser1_ID (int User1_ID);

	/** Get User Element List 1.
	  * User defined list element #1
	  */
	public int getUser1_ID();

	public org.compiere.model.I_C_ElementValue getUser1() throws RuntimeException;

    /** Column name User2_ID */
    public static final String COLUMNNAME_User2_ID = "User2_ID";

	/** Set User Element List 2.
	  * User defined list element #2
	  */
	public void setUser2_ID (int User2_ID);

	/** Get User Element List 2.
	  * User defined list element #2
	  */
	public int getUser2_ID();

	public org.compiere.model.I_C_ElementValue getUser2() throws RuntimeException;

    /** Column name X12DE355 */
    public static final String COLUMNNAME_X12DE355 = "X12DE355";

	/** Set UOM Code.
	  * UOM EDI X12 Code
	  */
	public void setX12DE355 (String X12DE355);

	/** Get UOM Code.
	  * UOM EDI X12 Code
	  */
	public String getX12DE355();
}
