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

/** Generated Interface for JP_CM_BPartner
 *  @author iDempiere (generated) 
 *  @version Release 11
 */
@SuppressWarnings("all")
public interface I_JP_CM_BPartner 
{

    /** TableName=JP_CM_BPartner */
    public static final String Table_Name = "JP_CM_BPartner";

    /** AD_Table_ID=1000332 */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 4 - System 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(4);

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

    /** Column name IsIgnore_NMMaster_NotFoundJP */
    public static final String COLUMNNAME_IsIgnore_NMMaster_NotFoundJP = "IsIgnore_NMMaster_NotFoundJP";

	/** Set Ignore non-mandatory masters if not found	  */
	public void setIsIgnore_NMMaster_NotFoundJP (boolean IsIgnore_NMMaster_NotFoundJP);

	/** Get Ignore non-mandatory masters if not found	  */
	public boolean isIgnore_NMMaster_NotFoundJP();

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

    /** Column name IsQualifiedInvoiceIssuerJP */
    public static final String COLUMNNAME_IsQualifiedInvoiceIssuerJP = "IsQualifiedInvoiceIssuerJP";

	/** Set Qualified Invoice Issuer	  */
	public void setIsQualifiedInvoiceIssuerJP (boolean IsQualifiedInvoiceIssuerJP);

	/** Get Qualified Invoice Issuer	  */
	public boolean isQualifiedInvoiceIssuerJP();

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

    /** Column name JP_BillSchemaPO_Value */
    public static final String COLUMNNAME_JP_BillSchemaPO_Value = "JP_BillSchemaPO_Value";

	/** Set Payment Request Schema(Search Key)	  */
	public void setJP_BillSchemaPO_Value (String JP_BillSchemaPO_Value);

	/** Get Payment Request Schema(Search Key)	  */
	public String getJP_BillSchemaPO_Value();

    /** Column name JP_BillSchema_Value */
    public static final String COLUMNNAME_JP_BillSchema_Value = "JP_BillSchema_Value";

	/** Set Bill Schema(Search Key)	  */
	public void setJP_BillSchema_Value (String JP_BillSchema_Value);

	/** Get Bill Schema(Search Key)	  */
	public String getJP_BillSchema_Value();

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

    /** Column name JP_CM_BPartner_UU */
    public static final String COLUMNNAME_JP_CM_BPartner_UU = "JP_CM_BPartner_UU";

	/** Set Consolidated Business Partner	  */
	public void setJP_CM_BPartner_UU (String JP_CM_BPartner_UU);

	/** Get Consolidated Business Partner	  */
	public String getJP_CM_BPartner_UU();

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

    /** Column name JP_RegisteredDateOfQII */
    public static final String COLUMNNAME_JP_RegisteredDateOfQII = "JP_RegisteredDateOfQII";

	/** Set Registered Date of QII	  */
	public void setJP_RegisteredDateOfQII (Timestamp JP_RegisteredDateOfQII);

	/** Get Registered Date of QII	  */
	public Timestamp getJP_RegisteredDateOfQII();

    /** Column name JP_RegisteredNumberOfQII */
    public static final String COLUMNNAME_JP_RegisteredNumberOfQII = "JP_RegisteredNumberOfQII";

	/** Set Registered Number of QII	  */
	public void setJP_RegisteredNumberOfQII (String JP_RegisteredNumberOfQII);

	/** Get Registered Number of QII	  */
	public String getJP_RegisteredNumberOfQII();

    /** Column name JP_SOTaxRounding */
    public static final String COLUMNNAME_JP_SOTaxRounding = "JP_SOTaxRounding";

	/** Set SO Tax Rounding	  */
	public void setJP_SOTaxRounding (String JP_SOTaxRounding);

	/** Get SO Tax Rounding	  */
	public String getJP_SOTaxRounding();

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
