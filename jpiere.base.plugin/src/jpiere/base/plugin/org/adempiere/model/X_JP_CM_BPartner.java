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

/** Generated Model for JP_CM_BPartner
 *  @author iDempiere (generated)
 *  @version Release 11 - $Id$ */
@org.adempiere.base.Model(table="JP_CM_BPartner")
public class X_JP_CM_BPartner extends PO implements I_JP_CM_BPartner, I_Persistent
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20241228L;

    /** Standard Constructor */
    public X_JP_CM_BPartner (Properties ctx, int JP_CM_BPartner_ID, String trxName)
    {
      super (ctx, JP_CM_BPartner_ID, trxName);
      /** if (JP_CM_BPartner_ID == 0)
        {
			setGroupValue (null);
			setIs1099Vendor (false);
// N
			setIsCustomer (false);
// N
			setIsDiscountPrinted (false);
// N
			setIsEmployee (false);
// N
			setIsIgnore_NMMaster_NotFoundJP (true);
// Y
			setIsManufacturer (false);
// N
			setIsPOTaxExempt (false);
// N
			setIsProspect (false);
// N
			setIsQualifiedInvoiceIssuerJP (false);
// N
			setIsSalesRep (false);
// N
			setIsSummary (false);
// N
			setIsTaxExempt (false);
// N
			setIsVendor (false);
// N
			setJP_CM_BPartner_ID (0);
			setName (null);
        } */
    }

    /** Standard Constructor */
    public X_JP_CM_BPartner (Properties ctx, int JP_CM_BPartner_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, JP_CM_BPartner_ID, trxName, virtualColumns);
      /** if (JP_CM_BPartner_ID == 0)
        {
			setGroupValue (null);
			setIs1099Vendor (false);
// N
			setIsCustomer (false);
// N
			setIsDiscountPrinted (false);
// N
			setIsEmployee (false);
// N
			setIsIgnore_NMMaster_NotFoundJP (true);
// Y
			setIsManufacturer (false);
// N
			setIsPOTaxExempt (false);
// N
			setIsProspect (false);
// N
			setIsQualifiedInvoiceIssuerJP (false);
// N
			setIsSalesRep (false);
// N
			setIsSummary (false);
// N
			setIsTaxExempt (false);
// N
			setIsVendor (false);
// N
			setJP_CM_BPartner_ID (0);
			setName (null);
        } */
    }

    /** Standard Constructor */
    public X_JP_CM_BPartner (Properties ctx, String JP_CM_BPartner_UU, String trxName)
    {
      super (ctx, JP_CM_BPartner_UU, trxName);
      /** if (JP_CM_BPartner_UU == null)
        {
			setGroupValue (null);
			setIs1099Vendor (false);
// N
			setIsCustomer (false);
// N
			setIsDiscountPrinted (false);
// N
			setIsEmployee (false);
// N
			setIsIgnore_NMMaster_NotFoundJP (true);
// Y
			setIsManufacturer (false);
// N
			setIsPOTaxExempt (false);
// N
			setIsProspect (false);
// N
			setIsQualifiedInvoiceIssuerJP (false);
// N
			setIsSalesRep (false);
// N
			setIsSummary (false);
// N
			setIsTaxExempt (false);
// N
			setIsVendor (false);
// N
			setJP_CM_BPartner_ID (0);
			setName (null);
        } */
    }

    /** Standard Constructor */
    public X_JP_CM_BPartner (Properties ctx, String JP_CM_BPartner_UU, String trxName, String ... virtualColumns)
    {
      super (ctx, JP_CM_BPartner_UU, trxName, virtualColumns);
      /** if (JP_CM_BPartner_UU == null)
        {
			setGroupValue (null);
			setIs1099Vendor (false);
// N
			setIsCustomer (false);
// N
			setIsDiscountPrinted (false);
// N
			setIsEmployee (false);
// N
			setIsIgnore_NMMaster_NotFoundJP (true);
// Y
			setIsManufacturer (false);
// N
			setIsPOTaxExempt (false);
// N
			setIsProspect (false);
// N
			setIsQualifiedInvoiceIssuerJP (false);
// N
			setIsSalesRep (false);
// N
			setIsSummary (false);
// N
			setIsTaxExempt (false);
// N
			setIsVendor (false);
// N
			setJP_CM_BPartner_ID (0);
			setName (null);
        } */
    }

    /** Load Constructor */
    public X_JP_CM_BPartner (Properties ctx, ResultSet rs, String trxName)
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
      StringBuilder sb = new StringBuilder ("X_JP_CM_BPartner[")
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

	/** Set Ignore non-mandatory masters if not found.
		@param IsIgnore_NMMaster_NotFoundJP Ignore non-mandatory masters if not found
	*/
	public void setIsIgnore_NMMaster_NotFoundJP (boolean IsIgnore_NMMaster_NotFoundJP)
	{
		set_Value (COLUMNNAME_IsIgnore_NMMaster_NotFoundJP, Boolean.valueOf(IsIgnore_NMMaster_NotFoundJP));
	}

	/** Get Ignore non-mandatory masters if not found.
		@return Ignore non-mandatory masters if not found	  */
	public boolean isIgnore_NMMaster_NotFoundJP()
	{
		Object oo = get_Value(COLUMNNAME_IsIgnore_NMMaster_NotFoundJP);
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

	/** Set Qualified Invoice Issuer.
		@param IsQualifiedInvoiceIssuerJP Qualified Invoice Issuer
	*/
	public void setIsQualifiedInvoiceIssuerJP (boolean IsQualifiedInvoiceIssuerJP)
	{
		set_Value (COLUMNNAME_IsQualifiedInvoiceIssuerJP, Boolean.valueOf(IsQualifiedInvoiceIssuerJP));
	}

	/** Get Qualified Invoice Issuer.
		@return Qualified Invoice Issuer	  */
	public boolean isQualifiedInvoiceIssuerJP()
	{
		Object oo = get_Value(COLUMNNAME_IsQualifiedInvoiceIssuerJP);
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

	/** Set Consolidated Business Partner.
		@param JP_CM_BPartner_UU Consolidated Business Partner
	*/
	public void setJP_CM_BPartner_UU (String JP_CM_BPartner_UU)
	{
		set_Value (COLUMNNAME_JP_CM_BPartner_UU, JP_CM_BPartner_UU);
	}

	/** Get Consolidated Business Partner.
		@return Consolidated Business Partner	  */
	public String getJP_CM_BPartner_UU()
	{
		return (String)get_Value(COLUMNNAME_JP_CM_BPartner_UU);
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

	/** Set Registered Date of QII.
		@param JP_RegisteredDateOfQII Registered Date of QII
	*/
	public void setJP_RegisteredDateOfQII (Timestamp JP_RegisteredDateOfQII)
	{
		set_Value (COLUMNNAME_JP_RegisteredDateOfQII, JP_RegisteredDateOfQII);
	}

	/** Get Registered Date of QII.
		@return Registered Date of QII	  */
	public Timestamp getJP_RegisteredDateOfQII()
	{
		return (Timestamp)get_Value(COLUMNNAME_JP_RegisteredDateOfQII);
	}

	/** Set Registered Number of QII.
		@param JP_RegisteredNumberOfQII Registered Number of QII
	*/
	public void setJP_RegisteredNumberOfQII (String JP_RegisteredNumberOfQII)
	{
		set_Value (COLUMNNAME_JP_RegisteredNumberOfQII, JP_RegisteredNumberOfQII);
	}

	/** Get Registered Number of QII.
		@return Registered Number of QII	  */
	public String getJP_RegisteredNumberOfQII()
	{
		return (String)get_Value(COLUMNNAME_JP_RegisteredNumberOfQII);
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
}