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

/** Generated Model for JP_ReferenceTest
 *  @author iDempiere (generated) 
 *  @version Release 8.2 - $Id$ */
public class X_JP_ReferenceTest extends PO implements I_JP_ReferenceTest, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20210101L;

    /** Standard Constructor */
    public X_JP_ReferenceTest (Properties ctx, int JP_ReferenceTest_ID, String trxName)
    {
      super (ctx, JP_ReferenceTest_ID, trxName);
      /** if (JP_ReferenceTest_ID == 0)
        {
			setInvoiceRule (null);
// D
			setIsCustomer (true);
// Y
			setJP_ReferenceTest_ID (0);
			setM_PriceList_ID (0);
			setM_Product_ID (0);
			setProcessed (false);
// N
			setTenderType (null);
// D
        } */
    }

    /** Load Constructor */
    public X_JP_ReferenceTest (Properties ctx, ResultSet rs, String trxName)
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
      StringBuilder sb = new StringBuilder ("X_JP_ReferenceTest[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set Image.
		@param AD_Image_ID 
		Image or Icon
	  */
	public void setAD_Image_ID (int AD_Image_ID)
	{
		if (AD_Image_ID < 1) 
			set_Value (COLUMNNAME_AD_Image_ID, null);
		else 
			set_Value (COLUMNNAME_AD_Image_ID, Integer.valueOf(AD_Image_ID));
	}

	/** Get Image.
		@return Image or Icon
	  */
	public int getAD_Image_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_Image_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Trx Organization.
		@param AD_OrgTrx_ID 
		Performing or initiating organization
	  */
	public void setAD_OrgTrx_ID (int AD_OrgTrx_ID)
	{
		if (AD_OrgTrx_ID < 1) 
			set_Value (COLUMNNAME_AD_OrgTrx_ID, null);
		else 
			set_Value (COLUMNNAME_AD_OrgTrx_ID, Integer.valueOf(AD_OrgTrx_ID));
	}

	/** Get Trx Organization.
		@return Performing or initiating organization
	  */
	public int getAD_OrgTrx_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_OrgTrx_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Location getBP_Location() throws RuntimeException
    {
		return (org.compiere.model.I_C_Location)MTable.get(getCtx(), org.compiere.model.I_C_Location.Table_Name)
			.getPO(getBP_Location_ID(), get_TrxName());	}

	/** Set BP Address.
		@param BP_Location_ID 
		Address of the Business Partner
	  */
	public void setBP_Location_ID (int BP_Location_ID)
	{
		if (BP_Location_ID < 1) 
			set_Value (COLUMNNAME_BP_Location_ID, null);
		else 
			set_Value (COLUMNNAME_BP_Location_ID, Integer.valueOf(BP_Location_ID));
	}

	/** Get BP Address.
		@return Address of the Business Partner
	  */
	public int getBP_Location_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_BP_Location_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Backup Folder.
		@param BackupFolder 
		Backup Folder
	  */
	public void setBackupFolder (String BackupFolder)
	{
		set_Value (COLUMNNAME_BackupFolder, BackupFolder);
	}

	/** Get Backup Folder.
		@return Backup Folder
	  */
	public String getBackupFolder () 
	{
		return (String)get_Value(COLUMNNAME_BackupFolder);
	}

	/** Set Broadcast Message.
		@param BroadcastMessage 
		Broadcast Message
	  */
	public void setBroadcastMessage (String BroadcastMessage)
	{
		set_Value (COLUMNNAME_BroadcastMessage, BroadcastMessage);
	}

	/** Get Broadcast Message.
		@return Broadcast Message
	  */
	public String getBroadcastMessage () 
	{
		return (String)get_Value(COLUMNNAME_BroadcastMessage);
	}

	public org.compiere.model.I_C_BPartner getC_BPartner() throws RuntimeException
    {
		return (org.compiere.model.I_C_BPartner)MTable.get(getCtx(), org.compiere.model.I_C_BPartner.Table_Name)
			.getPO(getC_BPartner_ID(), get_TrxName());	}

	/** Set Business Partner .
		@param C_BPartner_ID 
		Identifies a Business Partner
	  */
	public void setC_BPartner_ID (int C_BPartner_ID)
	{
		if (C_BPartner_ID < 1) 
			set_Value (COLUMNNAME_C_BPartner_ID, null);
		else 
			set_Value (COLUMNNAME_C_BPartner_ID, Integer.valueOf(C_BPartner_ID));
	}

	/** Get Business Partner .
		@return Identifies a Business Partner
	  */
	public int getC_BPartner_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_BPartner_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_BPartner_Location getC_BPartner_Location() throws RuntimeException
    {
		return (org.compiere.model.I_C_BPartner_Location)MTable.get(getCtx(), org.compiere.model.I_C_BPartner_Location.Table_Name)
			.getPO(getC_BPartner_Location_ID(), get_TrxName());	}

	/** Set Partner Location.
		@param C_BPartner_Location_ID 
		Identifies the (ship to) address for this Business Partner
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
	public int getC_BPartner_Location_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_BPartner_Location_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Currency getC_Currency() throws RuntimeException
    {
		return (org.compiere.model.I_C_Currency)MTable.get(getCtx(), org.compiere.model.I_C_Currency.Table_Name)
			.getPO(getC_Currency_ID(), get_TrxName());	}

	/** Set Currency.
		@param C_Currency_ID 
		The Currency for this record
	  */
	public void setC_Currency_ID (int C_Currency_ID)
	{
		if (C_Currency_ID < 1) 
			set_Value (COLUMNNAME_C_Currency_ID, null);
		else 
			set_Value (COLUMNNAME_C_Currency_ID, Integer.valueOf(C_Currency_ID));
	}

	/** Get Currency.
		@return The Currency for this record
	  */
	public int getC_Currency_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Currency_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_Location getC_Location() throws RuntimeException
    {
		return (I_C_Location)MTable.get(getCtx(), I_C_Location.Table_Name)
			.getPO(getC_Location_ID(), get_TrxName());	}

	/** Set Address.
		@param C_Location_ID 
		Location or Address
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
	public int getC_Location_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Location_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Number.
		@param CreditCardNumber 
		Credit Card Number 
	  */
	public void setCreditCardNumber (String CreditCardNumber)
	{
		set_Value (COLUMNNAME_CreditCardNumber, CreditCardNumber);
	}

	/** Get Number.
		@return Credit Card Number 
	  */
	public String getCreditCardNumber () 
	{
		return (String)get_Value(COLUMNNAME_CreditCardNumber);
	}

	/** Set Current Cost Price.
		@param CurrentCostPrice 
		The currently used cost price
	  */
	public void setCurrentCostPrice (BigDecimal CurrentCostPrice)
	{
		set_Value (COLUMNNAME_CurrentCostPrice, CurrentCostPrice);
	}

	/** Get Current Cost Price.
		@return The currently used cost price
	  */
	public BigDecimal getCurrentCostPrice () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_CurrentCostPrice);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Date.
		@param Date1 
		Date when business is not conducted
	  */
	public void setDate1 (Timestamp Date1)
	{
		set_Value (COLUMNNAME_Date1, Date1);
	}

	/** Get Date.
		@return Date when business is not conducted
	  */
	public Timestamp getDate1 () 
	{
		return (Timestamp)get_Value(COLUMNNAME_Date1);
	}

	/** Set Transaction Date.
		@param DateTrx 
		Transaction Date
	  */
	public void setDateTrx (Timestamp DateTrx)
	{
		set_Value (COLUMNNAME_DateTrx, DateTrx);
	}

	/** Get Transaction Date.
		@return Transaction Date
	  */
	public Timestamp getDateTrx () 
	{
		return (Timestamp)get_Value(COLUMNNAME_DateTrx);
	}

	/** DeliveryRule AD_Reference_ID=151 */
	public static final int DELIVERYRULE_AD_Reference_ID=151;
	/** After Receipt = R */
	public static final String DELIVERYRULE_AfterReceipt = "R";
	/** Availability = A */
	public static final String DELIVERYRULE_Availability = "A";
	/** Complete Line = L */
	public static final String DELIVERYRULE_CompleteLine = "L";
	/** Complete Order = O */
	public static final String DELIVERYRULE_CompleteOrder = "O";
	/** Force = F */
	public static final String DELIVERYRULE_Force = "F";
	/** Manual = M */
	public static final String DELIVERYRULE_Manual = "M";
	/** Set Delivery Rule.
		@param DeliveryRule 
		Defines the timing of Delivery
	  */
	public void setDeliveryRule (String DeliveryRule)
	{

		set_Value (COLUMNNAME_DeliveryRule, DeliveryRule);
	}

	/** Get Delivery Rule.
		@return Defines the timing of Delivery
	  */
	public String getDeliveryRule () 
	{
		return (String)get_Value(COLUMNNAME_DeliveryRule);
	}

	/** DocStatus AD_Reference_ID=131 */
	public static final int DOCSTATUS_AD_Reference_ID=131;
	/** Drafted = DR */
	public static final String DOCSTATUS_Drafted = "DR";
	/** Completed = CO */
	public static final String DOCSTATUS_Completed = "CO";
	/** Approved = AP */
	public static final String DOCSTATUS_Approved = "AP";
	/** Not Approved = NA */
	public static final String DOCSTATUS_NotApproved = "NA";
	/** Voided = VO */
	public static final String DOCSTATUS_Voided = "VO";
	/** Invalid = IN */
	public static final String DOCSTATUS_Invalid = "IN";
	/** Reversed = RE */
	public static final String DOCSTATUS_Reversed = "RE";
	/** Closed = CL */
	public static final String DOCSTATUS_Closed = "CL";
	/** Unknown = ?? */
	public static final String DOCSTATUS_Unknown = "??";
	/** In Progress = IP */
	public static final String DOCSTATUS_InProgress = "IP";
	/** Waiting Payment = WP */
	public static final String DOCSTATUS_WaitingPayment = "WP";
	/** Waiting Confirmation = WC */
	public static final String DOCSTATUS_WaitingConfirmation = "WC";
	/** Set Document Status.
		@param DocStatus 
		The current status of the document
	  */
	public void setDocStatus (String DocStatus)
	{

		set_Value (COLUMNNAME_DocStatus, DocStatus);
	}

	/** Get Document Status.
		@return The current status of the document
	  */
	public String getDocStatus () 
	{
		return (String)get_Value(COLUMNNAME_DocStatus);
	}

	/** Set File Name.
		@param FileName 
		Name of the local file or URL
	  */
	public void setFileName (String FileName)
	{
		set_Value (COLUMNNAME_FileName, FileName);
	}

	/** Get File Name.
		@return Name of the local file or URL
	  */
	public String getFileName () 
	{
		return (String)get_Value(COLUMNNAME_FileName);
	}

	/** Set Grand Total.
		@param GrandTotal 
		Total amount of document
	  */
	public void setGrandTotal (BigDecimal GrandTotal)
	{
		set_Value (COLUMNNAME_GrandTotal, GrandTotal);
	}

	/** Get Grand Total.
		@return Total amount of document
	  */
	public BigDecimal getGrandTotal () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_GrandTotal);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set HTML.
		@param HTML HTML	  */
	public void setHTML (String HTML)
	{
		set_Value (COLUMNNAME_HTML, HTML);
	}

	/** Get HTML.
		@return HTML	  */
	public String getHTML () 
	{
		return (String)get_Value(COLUMNNAME_HTML);
	}

	/** Set Comment/Help.
		@param Help 
		Comment or Hint
	  */
	public void setHelp (String Help)
	{
		set_Value (COLUMNNAME_Help, Help);
	}

	/** Get Comment/Help.
		@return Comment or Hint
	  */
	public String getHelp () 
	{
		return (String)get_Value(COLUMNNAME_Help);
	}

	/** InvoiceRule AD_Reference_ID=150 */
	public static final int INVOICERULE_AD_Reference_ID=150;
	/** After Order delivered = O */
	public static final String INVOICERULE_AfterOrderDelivered = "O";
	/** After Delivery = D */
	public static final String INVOICERULE_AfterDelivery = "D";
	/** Customer Schedule after Delivery = S */
	public static final String INVOICERULE_CustomerScheduleAfterDelivery = "S";
	/** Immediate = I */
	public static final String INVOICERULE_Immediate = "I";
	/** Set Invoice Rule.
		@param InvoiceRule 
		Frequency and method of invoicing 
	  */
	public void setInvoiceRule (String InvoiceRule)
	{

		set_Value (COLUMNNAME_InvoiceRule, InvoiceRule);
	}

	/** Get Invoice Rule.
		@return Frequency and method of invoicing 
	  */
	public String getInvoiceRule () 
	{
		return (String)get_Value(COLUMNNAME_InvoiceRule);
	}

	/** Set Customer.
		@param IsCustomer 
		Indicates if this Business Partner is a Customer
	  */
	public void setIsCustomer (boolean IsCustomer)
	{
		set_Value (COLUMNNAME_IsCustomer, Boolean.valueOf(IsCustomer));
	}

	/** Get Customer.
		@return Indicates if this Business Partner is a Customer
	  */
	public boolean isCustomer () 
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

	/** JP_BPartner_Multi AD_Reference_ID=138 */
	public static final int JP_BPARTNER_MULTI_AD_Reference_ID=138;
	/** Set Business Partner(Multi Select).
		@param JP_BPartner_Multi Business Partner(Multi Select)	  */
	public void setJP_BPartner_Multi (String JP_BPartner_Multi)
	{

		set_Value (COLUMNNAME_JP_BPartner_Multi, JP_BPartner_Multi);
	}

	/** Get Business Partner(Multi Select).
		@return Business Partner(Multi Select)	  */
	public String getJP_BPartner_Multi () 
	{
		return (String)get_Value(COLUMNNAME_JP_BPartner_Multi);
	}

	/** Set Multiple Selection Grid.
		@param JP_Multi_Grid Multiple Selection Grid	  */
	public void setJP_Multi_Grid (String JP_Multi_Grid)
	{
		set_Value (COLUMNNAME_JP_Multi_Grid, JP_Multi_Grid);
	}

	/** Get Multiple Selection Grid.
		@return Multiple Selection Grid	  */
	public String getJP_Multi_Grid () 
	{
		return (String)get_Value(COLUMNNAME_JP_Multi_Grid);
	}

	/** JP_Multi_List AD_Reference_ID=151 */
	public static final int JP_MULTI_LIST_AD_Reference_ID=151;
	/** After Receipt = R */
	public static final String JP_MULTI_LIST_AfterReceipt = "R";
	/** Availability = A */
	public static final String JP_MULTI_LIST_Availability = "A";
	/** Complete Line = L */
	public static final String JP_MULTI_LIST_CompleteLine = "L";
	/** Complete Order = O */
	public static final String JP_MULTI_LIST_CompleteOrder = "O";
	/** Force = F */
	public static final String JP_MULTI_LIST_Force = "F";
	/** Manual = M */
	public static final String JP_MULTI_LIST_Manual = "M";
	/** Set Multiple Selection List.
		@param JP_Multi_List Multiple Selection List	  */
	public void setJP_Multi_List (String JP_Multi_List)
	{

		set_Value (COLUMNNAME_JP_Multi_List, JP_Multi_List);
	}

	/** Get Multiple Selection List.
		@return Multiple Selection List	  */
	public String getJP_Multi_List () 
	{
		return (String)get_Value(COLUMNNAME_JP_Multi_List);
	}

	/** Set Org Name(History).
		@param JP_Org_History_Name Org Name(History)	  */
	public void setJP_Org_History_Name (String JP_Org_History_Name)
	{
		set_Value (COLUMNNAME_JP_Org_History_Name, JP_Org_History_Name);
	}

	/** Get Org Name(History).
		@return Org Name(History)	  */
	public String getJP_Org_History_Name () 
	{
		return (String)get_Value(COLUMNNAME_JP_Org_History_Name);
	}

	/** Set Product(Multi Select).
		@param JP_Product_Multi Product(Multi Select)	  */
	public void setJP_Product_Multi (String JP_Product_Multi)
	{

		set_Value (COLUMNNAME_JP_Product_Multi, JP_Product_Multi);
	}

	/** Get Product(Multi Select).
		@return Product(Multi Select)	  */
	public String getJP_Product_Multi () 
	{
		return (String)get_Value(COLUMNNAME_JP_Product_Multi);
	}

	/** Set JP_ReferenceTest.
		@param JP_ReferenceTest_ID JP_ReferenceTest	  */
	public void setJP_ReferenceTest_ID (int JP_ReferenceTest_ID)
	{
		if (JP_ReferenceTest_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_ReferenceTest_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_ReferenceTest_ID, Integer.valueOf(JP_ReferenceTest_ID));
	}

	/** Get JP_ReferenceTest.
		@return JP_ReferenceTest	  */
	public int getJP_ReferenceTest_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ReferenceTest_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set JP_ReferenceTest_UU.
		@param JP_ReferenceTest_UU JP_ReferenceTest_UU	  */
	public void setJP_ReferenceTest_UU (String JP_ReferenceTest_UU)
	{
		set_ValueNoCheck (COLUMNNAME_JP_ReferenceTest_UU, JP_ReferenceTest_UU);
	}

	/** Get JP_ReferenceTest_UU.
		@return JP_ReferenceTest_UU	  */
	public String getJP_ReferenceTest_UU () 
	{
		return (String)get_Value(COLUMNNAME_JP_ReferenceTest_UU);
	}

	/** Set Latest Pickup Time.
		@param LatestPickupTime Latest Pickup Time	  */
	public void setLatestPickupTime (Timestamp LatestPickupTime)
	{
		set_Value (COLUMNNAME_LatestPickupTime, LatestPickupTime);
	}

	/** Get Latest Pickup Time.
		@return Latest Pickup Time	  */
	public Timestamp getLatestPickupTime () 
	{
		return (Timestamp)get_Value(COLUMNNAME_LatestPickupTime);
	}

	/** Set Line No.
		@param Line 
		Unique line for this document
	  */
	public void setLine (int Line)
	{
		set_Value (COLUMNNAME_Line, Integer.valueOf(Line));
	}

	/** Get Line No.
		@return Unique line for this document
	  */
	public int getLine () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Line);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_M_AttributeSetInstance getM_AttributeSetInstance() throws RuntimeException
    {
		return (I_M_AttributeSetInstance)MTable.get(getCtx(), I_M_AttributeSetInstance.Table_Name)
			.getPO(getM_AttributeSetInstance_ID(), get_TrxName());	}

	/** Set Attribute Info.
		@param M_AttributeSetInstance_ID 
		Product Attribute Set Instance
	  */
	public void setM_AttributeSetInstance_ID (int M_AttributeSetInstance_ID)
	{
		if (M_AttributeSetInstance_ID < 0) 
			set_Value (COLUMNNAME_M_AttributeSetInstance_ID, null);
		else 
			set_Value (COLUMNNAME_M_AttributeSetInstance_ID, Integer.valueOf(M_AttributeSetInstance_ID));
	}

	/** Get Attribute Info.
		@return Product Attribute Set Instance
	  */
	public int getM_AttributeSetInstance_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_AttributeSetInstance_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_Locator getM_LocatorTo() throws RuntimeException
    {
		return (org.compiere.model.I_M_Locator)MTable.get(getCtx(), org.compiere.model.I_M_Locator.Table_Name)
			.getPO(getM_LocatorTo_ID(), get_TrxName());	}

	/** Set Locator To.
		@param M_LocatorTo_ID 
		Location inventory is moved to
	  */
	public void setM_LocatorTo_ID (int M_LocatorTo_ID)
	{
		if (M_LocatorTo_ID < 1) 
			set_Value (COLUMNNAME_M_LocatorTo_ID, null);
		else 
			set_Value (COLUMNNAME_M_LocatorTo_ID, Integer.valueOf(M_LocatorTo_ID));
	}

	/** Get Locator To.
		@return Location inventory is moved to
	  */
	public int getM_LocatorTo_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_LocatorTo_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_M_Locator getM_Locator() throws RuntimeException
    {
		return (I_M_Locator)MTable.get(getCtx(), I_M_Locator.Table_Name)
			.getPO(getM_Locator_ID(), get_TrxName());	}

	/** Set Locator.
		@param M_Locator_ID 
		Warehouse Locator
	  */
	public void setM_Locator_ID (int M_Locator_ID)
	{
		if (M_Locator_ID < 1) 
			set_Value (COLUMNNAME_M_Locator_ID, null);
		else 
			set_Value (COLUMNNAME_M_Locator_ID, Integer.valueOf(M_Locator_ID));
	}

	/** Get Locator.
		@return Warehouse Locator
	  */
	public int getM_Locator_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Locator_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_PriceList getM_PriceList() throws RuntimeException
    {
		return (org.compiere.model.I_M_PriceList)MTable.get(getCtx(), org.compiere.model.I_M_PriceList.Table_Name)
			.getPO(getM_PriceList_ID(), get_TrxName());	}

	/** Set Price List.
		@param M_PriceList_ID 
		Unique identifier of a Price List
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
	public int getM_PriceList_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_PriceList_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_Product_Category getM_Product_Category() throws RuntimeException
    {
		return (org.compiere.model.I_M_Product_Category)MTable.get(getCtx(), org.compiere.model.I_M_Product_Category.Table_Name)
			.getPO(getM_Product_Category_ID(), get_TrxName());	}

	/** Set Product Category.
		@param M_Product_Category_ID 
		Category of a Product
	  */
	public void setM_Product_Category_ID (int M_Product_Category_ID)
	{
		throw new IllegalArgumentException ("M_Product_Category_ID is virtual column");	}

	/** Get Product Category.
		@return Category of a Product
	  */
	public int getM_Product_Category_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Product_Category_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_Product getM_Product() throws RuntimeException
    {
		return (org.compiere.model.I_M_Product)MTable.get(getCtx(), org.compiere.model.I_M_Product.Table_Name)
			.getPO(getM_Product_ID(), get_TrxName());	}

	/** Set Product.
		@param M_Product_ID 
		Product, Service, Item
	  */
	public void setM_Product_ID (int M_Product_ID)
	{
		if (M_Product_ID < 1) 
			set_Value (COLUMNNAME_M_Product_ID, null);
		else 
			set_Value (COLUMNNAME_M_Product_ID, Integer.valueOf(M_Product_ID));
	}

	/** Get Product.
		@return Product, Service, Item
	  */
	public int getM_Product_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Product_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_ValidCombination getP_Revenue_A() throws RuntimeException
    {
		return (I_C_ValidCombination)MTable.get(getCtx(), I_C_ValidCombination.Table_Name)
			.getPO(getP_Revenue_Acct(), get_TrxName());	}

	/** Set Product Revenue.
		@param P_Revenue_Acct 
		Account for Product Revenue (Sales Account)
	  */
	public void setP_Revenue_Acct (int P_Revenue_Acct)
	{
		set_Value (COLUMNNAME_P_Revenue_Acct, Integer.valueOf(P_Revenue_Acct));
	}

	/** Get Product Revenue.
		@return Account for Product Revenue (Sales Account)
	  */
	public int getP_Revenue_Acct () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_P_Revenue_Acct);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Password.
		@param Password 
		Password of any length (case sensitive)
	  */
	public void setPassword (String Password)
	{
		set_Value (COLUMNNAME_Password, Password);
	}

	/** Get Password.
		@return Password of any length (case sensitive)
	  */
	public String getPassword () 
	{
		return (String)get_Value(COLUMNNAME_Password);
	}

	/** PaymentRule AD_Reference_ID=195 */
	public static final int PAYMENTRULE_AD_Reference_ID=195;
	/** Cash = B */
	public static final String PAYMENTRULE_Cash = "B";
	/** Credit Card = K */
	public static final String PAYMENTRULE_CreditCard = "K";
	/** Direct Deposit = T */
	public static final String PAYMENTRULE_DirectDeposit = "T";
	/** Check = S */
	public static final String PAYMENTRULE_Check = "S";
	/** On Credit = P */
	public static final String PAYMENTRULE_OnCredit = "P";
	/** Direct Debit = D */
	public static final String PAYMENTRULE_DirectDebit = "D";
	/** Mixed POS Payment = M */
	public static final String PAYMENTRULE_MixedPOSPayment = "M";
	/** Set Payment Rule.
		@param PaymentRule 
		How you pay the invoice
	  */
	public void setPaymentRule (String PaymentRule)
	{

		set_Value (COLUMNNAME_PaymentRule, PaymentRule);
	}

	/** Get Payment Rule.
		@return How you pay the invoice
	  */
	public String getPaymentRule () 
	{
		return (String)get_Value(COLUMNNAME_PaymentRule);
	}

	/** Set Processed.
		@param Processed 
		The document has been processed
	  */
	public void setProcessed (boolean Processed)
	{
		set_Value (COLUMNNAME_Processed, Boolean.valueOf(Processed));
	}

	/** Get Processed.
		@return The document has been processed
	  */
	public boolean isProcessed () 
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
		@param Processing Process Now	  */
	public void setProcessing (boolean Processing)
	{
		set_Value (COLUMNNAME_Processing, Boolean.valueOf(Processing));
	}

	/** Get Process Now.
		@return Process Now	  */
	public boolean isProcessing () 
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

	/** Set Quantity.
		@param QtyEntered 
		The Quantity Entered is based on the selected UoM
	  */
	public void setQtyEntered (BigDecimal QtyEntered)
	{
		set_Value (COLUMNNAME_QtyEntered, QtyEntered);
	}

	/** Get Quantity.
		@return The Quantity Entered is based on the selected UoM
	  */
	public BigDecimal getQtyEntered () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_QtyEntered);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Rate.
		@param Rate 
		Rate or Tax or Exchange
	  */
	public void setRate (BigDecimal Rate)
	{
		set_Value (COLUMNNAME_Rate, Rate);
	}

	/** Get Rate.
		@return Rate or Tax or Exchange
	  */
	public BigDecimal getRate () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_Rate);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Reference No.
		@param ReferenceNo 
		Your customer or vendor number at the Business Partner's site
	  */
	public void setReferenceNo (String ReferenceNo)
	{
		set_Value (COLUMNNAME_ReferenceNo, ReferenceNo);
	}

	/** Get Reference No.
		@return Your customer or vendor number at the Business Partner's site
	  */
	public String getReferenceNo () 
	{
		return (String)get_Value(COLUMNNAME_ReferenceNo);
	}

	/** Set Resource Assign.
		@param S_ResourceAssignment_ID 
		Resource Assignment
	  */
	public void setS_ResourceAssignment_ID (int S_ResourceAssignment_ID)
	{
		if (S_ResourceAssignment_ID < 1) 
			set_Value (COLUMNNAME_S_ResourceAssignment_ID, null);
		else 
			set_Value (COLUMNNAME_S_ResourceAssignment_ID, Integer.valueOf(S_ResourceAssignment_ID));
	}

	/** Get Resource Assign.
		@return Resource Assignment
	  */
	public int getS_ResourceAssignment_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_S_ResourceAssignment_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Sales Volume in 1.000.
		@param SalesVolume 
		Total Volume of Sales in Thousands of Currency
	  */
	public void setSalesVolume (int SalesVolume)
	{
		throw new IllegalArgumentException ("SalesVolume is virtual column");	}

	/** Get Sales Volume in 1.000.
		@return Total Volume of Sales in Thousands of Currency
	  */
	public int getSalesVolume () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_SalesVolume);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** TenderType AD_Reference_ID=214 */
	public static final int TENDERTYPE_AD_Reference_ID=214;
	/** Credit Card = C */
	public static final String TENDERTYPE_CreditCard = "C";
	/** Check = K */
	public static final String TENDERTYPE_Check = "K";
	/** Direct Deposit = A */
	public static final String TENDERTYPE_DirectDeposit = "A";
	/** Direct Debit = D */
	public static final String TENDERTYPE_DirectDebit = "D";
	/** Account = T */
	public static final String TENDERTYPE_Account = "T";
	/** Cash = X */
	public static final String TENDERTYPE_Cash = "X";
	/** Set Tender type.
		@param TenderType 
		Method of Payment
	  */
	public void setTenderType (String TenderType)
	{

		set_Value (COLUMNNAME_TenderType, TenderType);
	}

	/** Get Tender type.
		@return Method of Payment
	  */
	public String getTenderType () 
	{
		return (String)get_Value(COLUMNNAME_TenderType);
	}

	/** Set URL.
		@param URL 
		Full URL address - e.g. http://www.idempiere.org
	  */
	public void setURL (String URL)
	{
		set_Value (COLUMNNAME_URL, URL);
	}

	/** Get URL.
		@return Full URL address - e.g. http://www.idempiere.org
	  */
	public String getURL () 
	{
		return (String)get_Value(COLUMNNAME_URL);
	}
}