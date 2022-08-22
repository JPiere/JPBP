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

/** Generated Model for JP_ContractContent
 *  @author iDempiere (generated) 
 *  @version Release 9 - $Id$ */
@org.adempiere.base.Model(table="JP_ContractContent")
public class X_JP_ContractContent extends PO implements I_JP_ContractContent, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20220822L;

    /** Standard Constructor */
    public X_JP_ContractContent (Properties ctx, int JP_ContractContent_ID, String trxName)
    {
      super (ctx, JP_ContractContent_ID, trxName);
      /** if (JP_ContractContent_ID == 0)
        {
			setC_BPartner_ID (0);
			setC_BPartner_Location_ID (0);
			setC_Currency_ID (0);
// @C_Currency_ID@
			setC_DocType_ID (0);
			setC_PaymentTerm_ID (0);
			setDateAcct (new Timestamp( System.currentTimeMillis() ));
// @#Date@
			setDateDoc (new Timestamp( System.currentTimeMillis() ));
// @#Date@
			setDeliveryRule (null);
// F
			setDeliveryViaRule (null);
// P
			setDocAction (null);
// CO
			setDocBaseType (null);
			setDocStatus (null);
// DR
			setDocumentNo (null);
			setFreightCostRule (null);
// I
			setGrandTotal (Env.ZERO);
// 0
			setInvoiceRule (null);
// I
			setIsApproved (false);
// N
			setIsAutomaticUpdateJP (false);
// N
			setIsDiscountPrinted (false);
// N
			setIsDropShip (false);
// N
			setIsRenewedContractContentJP (false);
// N
			setIsSOTrx (true);
// Y
			setIsScheduleCreatedJP (false);
// N
			setIsTaxIncluded (false);
			setJP_BaseDocDocType_ID (0);
			setJP_ContractContentT_ID (0);
			setJP_ContractContent_ID (0);
			setJP_ContractProcStatus (null);
// UN
			setJP_Contract_Acct_ID (0);
			setJP_Contract_ID (0);
			setM_PriceList_ID (0);
			setName (null);
			setOrderType (null);
// --
			setPaymentRule (null);
// B
			setPosted (false);
// N
			setPriorityRule (null);
// 5
			setProcessed (false);
			setSendEMail (false);
			setTotalLines (Env.ZERO);
        } */
    }

    /** Standard Constructor */
    public X_JP_ContractContent (Properties ctx, int JP_ContractContent_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, JP_ContractContent_ID, trxName, virtualColumns);
      /** if (JP_ContractContent_ID == 0)
        {
			setC_BPartner_ID (0);
			setC_BPartner_Location_ID (0);
			setC_Currency_ID (0);
// @C_Currency_ID@
			setC_DocType_ID (0);
			setC_PaymentTerm_ID (0);
			setDateAcct (new Timestamp( System.currentTimeMillis() ));
// @#Date@
			setDateDoc (new Timestamp( System.currentTimeMillis() ));
// @#Date@
			setDeliveryRule (null);
// F
			setDeliveryViaRule (null);
// P
			setDocAction (null);
// CO
			setDocBaseType (null);
			setDocStatus (null);
// DR
			setDocumentNo (null);
			setFreightCostRule (null);
// I
			setGrandTotal (Env.ZERO);
// 0
			setInvoiceRule (null);
// I
			setIsApproved (false);
// N
			setIsAutomaticUpdateJP (false);
// N
			setIsDiscountPrinted (false);
// N
			setIsDropShip (false);
// N
			setIsRenewedContractContentJP (false);
// N
			setIsSOTrx (true);
// Y
			setIsScheduleCreatedJP (false);
// N
			setIsTaxIncluded (false);
			setJP_BaseDocDocType_ID (0);
			setJP_ContractContentT_ID (0);
			setJP_ContractContent_ID (0);
			setJP_ContractProcStatus (null);
// UN
			setJP_Contract_Acct_ID (0);
			setJP_Contract_ID (0);
			setM_PriceList_ID (0);
			setName (null);
			setOrderType (null);
// --
			setPaymentRule (null);
// B
			setPosted (false);
// N
			setPriorityRule (null);
// 5
			setProcessed (false);
			setSendEMail (false);
			setTotalLines (Env.ZERO);
        } */
    }

    /** Load Constructor */
    public X_JP_ContractContent (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 1 - Org 
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
      StringBuilder sb = new StringBuilder ("X_JP_ContractContent[")
        .append(get_ID()).append(",Name=").append(getName()).append("]");
      return sb.toString();
    }

	/** Set Trx Organization.
		@param AD_OrgTrx_ID Performing or initiating organization
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
	public int getAD_OrgTrx_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_OrgTrx_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
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

	public org.compiere.model.I_C_BPartner getBill_BPartner() throws RuntimeException
	{
		return (org.compiere.model.I_C_BPartner)MTable.get(getCtx(), org.compiere.model.I_C_BPartner.Table_ID)
			.getPO(getBill_BPartner_ID(), get_TrxName());
	}

	/** Set Invoice Partner.
		@param Bill_BPartner_ID Business Partner to be invoiced
	*/
	public void setBill_BPartner_ID (int Bill_BPartner_ID)
	{
		if (Bill_BPartner_ID < 1)
			set_Value (COLUMNNAME_Bill_BPartner_ID, null);
		else
			set_Value (COLUMNNAME_Bill_BPartner_ID, Integer.valueOf(Bill_BPartner_ID));
	}

	/** Get Invoice Partner.
		@return Business Partner to be invoiced
	  */
	public int getBill_BPartner_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Bill_BPartner_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_BPartner_Location getBill_Location() throws RuntimeException
	{
		return (org.compiere.model.I_C_BPartner_Location)MTable.get(getCtx(), org.compiere.model.I_C_BPartner_Location.Table_ID)
			.getPO(getBill_Location_ID(), get_TrxName());
	}

	/** Set Invoice Location.
		@param Bill_Location_ID Business Partner Location for invoicing
	*/
	public void setBill_Location_ID (int Bill_Location_ID)
	{
		if (Bill_Location_ID < 1)
			set_Value (COLUMNNAME_Bill_Location_ID, null);
		else
			set_Value (COLUMNNAME_Bill_Location_ID, Integer.valueOf(Bill_Location_ID));
	}

	/** Get Invoice Location.
		@return Business Partner Location for invoicing
	  */
	public int getBill_Location_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Bill_Location_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_AD_User getBill_User() throws RuntimeException
	{
		return (org.compiere.model.I_AD_User)MTable.get(getCtx(), org.compiere.model.I_AD_User.Table_ID)
			.getPO(getBill_User_ID(), get_TrxName());
	}

	/** Set Invoice Contact.
		@param Bill_User_ID Business Partner Contact for invoicing
	*/
	public void setBill_User_ID (int Bill_User_ID)
	{
		if (Bill_User_ID < 1)
			set_Value (COLUMNNAME_Bill_User_ID, null);
		else
			set_Value (COLUMNNAME_Bill_User_ID, Integer.valueOf(Bill_User_ID));
	}

	/** Get Invoice Contact.
		@return Business Partner Contact for invoicing
	  */
	public int getBill_User_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Bill_User_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Activity getC_Activity() throws RuntimeException
	{
		return (org.compiere.model.I_C_Activity)MTable.get(getCtx(), org.compiere.model.I_C_Activity.Table_ID)
			.getPO(getC_Activity_ID(), get_TrxName());
	}

	/** Set Activity.
		@param C_Activity_ID Business Activity
	*/
	public void setC_Activity_ID (int C_Activity_ID)
	{
		if (C_Activity_ID < 1)
			set_Value (COLUMNNAME_C_Activity_ID, null);
		else
			set_Value (COLUMNNAME_C_Activity_ID, Integer.valueOf(C_Activity_ID));
	}

	/** Get Activity.
		@return Business Activity
	  */
	public int getC_Activity_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Activity_ID);
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

	public org.compiere.model.I_C_Campaign getC_Campaign() throws RuntimeException
	{
		return (org.compiere.model.I_C_Campaign)MTable.get(getCtx(), org.compiere.model.I_C_Campaign.Table_ID)
			.getPO(getC_Campaign_ID(), get_TrxName());
	}

	/** Set Campaign.
		@param C_Campaign_ID Marketing Campaign
	*/
	public void setC_Campaign_ID (int C_Campaign_ID)
	{
		if (C_Campaign_ID < 1)
			set_Value (COLUMNNAME_C_Campaign_ID, null);
		else
			set_Value (COLUMNNAME_C_Campaign_ID, Integer.valueOf(C_Campaign_ID));
	}

	/** Get Campaign.
		@return Marketing Campaign
	  */
	public int getC_Campaign_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Campaign_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_ConversionType getC_ConversionType() throws RuntimeException
	{
		return (org.compiere.model.I_C_ConversionType)MTable.get(getCtx(), org.compiere.model.I_C_ConversionType.Table_ID)
			.getPO(getC_ConversionType_ID(), get_TrxName());
	}

	/** Set Currency Type.
		@param C_ConversionType_ID Currency Conversion Rate Type
	*/
	public void setC_ConversionType_ID (int C_ConversionType_ID)
	{
		if (C_ConversionType_ID < 1)
			set_Value (COLUMNNAME_C_ConversionType_ID, null);
		else
			set_Value (COLUMNNAME_C_ConversionType_ID, Integer.valueOf(C_ConversionType_ID));
	}

	/** Get Currency Type.
		@return Currency Conversion Rate Type
	  */
	public int getC_ConversionType_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_ConversionType_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Currency getC_Currency() throws RuntimeException
	{
		return (org.compiere.model.I_C_Currency)MTable.get(getCtx(), org.compiere.model.I_C_Currency.Table_ID)
			.getPO(getC_Currency_ID(), get_TrxName());
	}

	/** Set Currency.
		@param C_Currency_ID The Currency for this record
	*/
	public void setC_Currency_ID (int C_Currency_ID)
	{
		if (C_Currency_ID < 1)
			set_ValueNoCheck (COLUMNNAME_C_Currency_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_C_Currency_ID, Integer.valueOf(C_Currency_ID));
	}

	/** Get Currency.
		@return The Currency for this record
	  */
	public int getC_Currency_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Currency_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_DocType getC_DocType() throws RuntimeException
	{
		return (org.compiere.model.I_C_DocType)MTable.get(getCtx(), org.compiere.model.I_C_DocType.Table_ID)
			.getPO(getC_DocType_ID(), get_TrxName());
	}

	/** Set Document Type.
		@param C_DocType_ID Document type or rules
	*/
	public void setC_DocType_ID (int C_DocType_ID)
	{
		if (C_DocType_ID < 0)
			set_Value (COLUMNNAME_C_DocType_ID, null);
		else
			set_Value (COLUMNNAME_C_DocType_ID, Integer.valueOf(C_DocType_ID));
	}

	/** Get Document Type.
		@return Document type or rules
	  */
	public int getC_DocType_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_DocType_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Opportunity getC_Opportunity() throws RuntimeException
	{
		return (org.compiere.model.I_C_Opportunity)MTable.get(getCtx(), org.compiere.model.I_C_Opportunity.Table_ID)
			.getPO(getC_Opportunity_ID(), get_TrxName());
	}

	/** Set Sales Opportunity.
		@param C_Opportunity_ID Sales Opportunity
	*/
	public void setC_Opportunity_ID (int C_Opportunity_ID)
	{
		if (C_Opportunity_ID < 1)
			set_Value (COLUMNNAME_C_Opportunity_ID, null);
		else
			set_Value (COLUMNNAME_C_Opportunity_ID, Integer.valueOf(C_Opportunity_ID));
	}

	/** Get Sales Opportunity.
		@return Sales Opportunity	  */
	public int getC_Opportunity_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Opportunity_ID);
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

	public org.compiere.model.I_C_Project getC_Project() throws RuntimeException
	{
		return (org.compiere.model.I_C_Project)MTable.get(getCtx(), org.compiere.model.I_C_Project.Table_ID)
			.getPO(getC_Project_ID(), get_TrxName());
	}

	/** Set Project.
		@param C_Project_ID Financial Project
	*/
	public void setC_Project_ID (int C_Project_ID)
	{
		if (C_Project_ID < 1)
			set_Value (COLUMNNAME_C_Project_ID, null);
		else
			set_Value (COLUMNNAME_C_Project_ID, Integer.valueOf(C_Project_ID));
	}

	/** Get Project.
		@return Financial Project
	  */
	public int getC_Project_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Project_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Account Date.
		@param DateAcct Accounting Date
	*/
	public void setDateAcct (Timestamp DateAcct)
	{
		set_Value (COLUMNNAME_DateAcct, DateAcct);
	}

	/** Get Account Date.
		@return Accounting Date
	  */
	public Timestamp getDateAcct()
	{
		return (Timestamp)get_Value(COLUMNNAME_DateAcct);
	}

	/** Set Document Date.
		@param DateDoc Date of the Document
	*/
	public void setDateDoc (Timestamp DateDoc)
	{
		set_Value (COLUMNNAME_DateDoc, DateDoc);
	}

	/** Get Document Date.
		@return Date of the Document
	  */
	public Timestamp getDateDoc()
	{
		return (Timestamp)get_Value(COLUMNNAME_DateDoc);
	}

	/** Set Date Invoiced.
		@param DateInvoiced Date printed on Invoice
	*/
	public void setDateInvoiced (Timestamp DateInvoiced)
	{
		set_Value (COLUMNNAME_DateInvoiced, DateInvoiced);
	}

	/** Get Date Invoiced.
		@return Date printed on Invoice
	  */
	public Timestamp getDateInvoiced()
	{
		return (Timestamp)get_Value(COLUMNNAME_DateInvoiced);
	}

	/** Set Date Ordered.
		@param DateOrdered Date of Order
	*/
	public void setDateOrdered (Timestamp DateOrdered)
	{
		set_Value (COLUMNNAME_DateOrdered, DateOrdered);
	}

	/** Get Date Ordered.
		@return Date of Order
	  */
	public Timestamp getDateOrdered()
	{
		return (Timestamp)get_Value(COLUMNNAME_DateOrdered);
	}

	/** Set Date Promised.
		@param DatePromised Date Order was promised
	*/
	public void setDatePromised (Timestamp DatePromised)
	{
		set_Value (COLUMNNAME_DatePromised, DatePromised);
	}

	/** Get Date Promised.
		@return Date Order was promised
	  */
	public Timestamp getDatePromised()
	{
		return (Timestamp)get_Value(COLUMNNAME_DatePromised);
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

	/** Set Promised Delivery Time.
		@param DeliveryTime_Promised Promised days between order and delivery
	*/
	public void setDeliveryTime_Promised (int DeliveryTime_Promised)
	{
		set_Value (COLUMNNAME_DeliveryTime_Promised, Integer.valueOf(DeliveryTime_Promised));
	}

	/** Get Promised Delivery Time.
		@return Promised days between order and delivery
	  */
	public int getDeliveryTime_Promised()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_DeliveryTime_Promised);
		if (ii == null)
			 return 0;
		return ii.intValue();
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

	/** DocAction AD_Reference_ID=135 */
	public static final int DOCACTION_AD_Reference_ID=135;
	/** &lt;None&gt; = -- */
	public static final String DOCACTION_None = "--";
	/** Approve = AP */
	public static final String DOCACTION_Approve = "AP";
	/** Close = CL */
	public static final String DOCACTION_Close = "CL";
	/** Complete = CO */
	public static final String DOCACTION_Complete = "CO";
	/** Invalidate = IN */
	public static final String DOCACTION_Invalidate = "IN";
	/** Post = PO */
	public static final String DOCACTION_Post = "PO";
	/** Prepare = PR */
	public static final String DOCACTION_Prepare = "PR";
	/** Reverse - Accrual = RA */
	public static final String DOCACTION_Reverse_Accrual = "RA";
	/** Reverse - Correct = RC */
	public static final String DOCACTION_Reverse_Correct = "RC";
	/** Re-activate = RE */
	public static final String DOCACTION_Re_Activate = "RE";
	/** Reject = RJ */
	public static final String DOCACTION_Reject = "RJ";
	/** Void = VO */
	public static final String DOCACTION_Void = "VO";
	/** Wait Complete = WC */
	public static final String DOCACTION_WaitComplete = "WC";
	/** Unlock = XL */
	public static final String DOCACTION_Unlock = "XL";
	/** Set Document Action.
		@param DocAction The targeted status of the document
	*/
	public void setDocAction (String DocAction)
	{

		set_Value (COLUMNNAME_DocAction, DocAction);
	}

	/** Get Document Action.
		@return The targeted status of the document
	  */
	public String getDocAction()
	{
		return (String)get_Value(COLUMNNAME_DocAction);
	}

	/** AP Invoice = API */
	public static final String DOCBASETYPE_APInvoice = "API";
	/** AR Invoice = ARI */
	public static final String DOCBASETYPE_ARInvoice = "ARI";
	/** Contract Proc Schedule = JCS */
	public static final String DOCBASETYPE_ContractProcSchedule = "JCS";
	/** Material Receipt = MMR */
	public static final String DOCBASETYPE_MaterialReceipt = "MMR";
	/** Material Delivery = MMS */
	public static final String DOCBASETYPE_MaterialDelivery = "MMS";
	/** Purchase Order = POO */
	public static final String DOCBASETYPE_PurchaseOrder = "POO";
	/** Sales Order = SOO */
	public static final String DOCBASETYPE_SalesOrder = "SOO";
	/** Set Document BaseType.
		@param DocBaseType Logical type of document
	*/
	public void setDocBaseType (String DocBaseType)
	{

		set_Value (COLUMNNAME_DocBaseType, DocBaseType);
	}

	/** Get Document BaseType.
		@return Logical type of document
	  */
	public String getDocBaseType()
	{
		return (String)get_Value(COLUMNNAME_DocBaseType);
	}

	/** DocStatus AD_Reference_ID=131 */
	public static final int DOCSTATUS_AD_Reference_ID=131;
	/** Unknown = ?? */
	public static final String DOCSTATUS_Unknown = "??";
	/** Approved = AP */
	public static final String DOCSTATUS_Approved = "AP";
	/** Closed = CL */
	public static final String DOCSTATUS_Closed = "CL";
	/** Completed = CO */
	public static final String DOCSTATUS_Completed = "CO";
	/** Drafted = DR */
	public static final String DOCSTATUS_Drafted = "DR";
	/** Invalid = IN */
	public static final String DOCSTATUS_Invalid = "IN";
	/** In Progress = IP */
	public static final String DOCSTATUS_InProgress = "IP";
	/** Not Approved = NA */
	public static final String DOCSTATUS_NotApproved = "NA";
	/** Reversed = RE */
	public static final String DOCSTATUS_Reversed = "RE";
	/** Voided = VO */
	public static final String DOCSTATUS_Voided = "VO";
	/** Waiting Confirmation = WC */
	public static final String DOCSTATUS_WaitingConfirmation = "WC";
	/** Waiting Payment = WP */
	public static final String DOCSTATUS_WaitingPayment = "WP";
	/** Set Document Status.
		@param DocStatus The current status of the document
	*/
	public void setDocStatus (String DocStatus)
	{

		set_Value (COLUMNNAME_DocStatus, DocStatus);
	}

	/** Get Document Status.
		@return The current status of the document
	  */
	public String getDocStatus()
	{
		return (String)get_Value(COLUMNNAME_DocStatus);
	}

	/** Set Document No.
		@param DocumentNo Document sequence number of the document
	*/
	public void setDocumentNo (String DocumentNo)
	{
		set_ValueNoCheck (COLUMNNAME_DocumentNo, DocumentNo);
	}

	/** Get Document No.
		@return Document sequence number of the document
	  */
	public String getDocumentNo()
	{
		return (String)get_Value(COLUMNNAME_DocumentNo);
	}

    /** Get Record ID/ColumnName
        @return ID/ColumnName pair
      */
    public KeyNamePair getKeyNamePair() 
    {
        return new KeyNamePair(get_ID(), getDocumentNo());
    }

	public org.compiere.model.I_C_BPartner getDropShip_BPartner() throws RuntimeException
	{
		return (org.compiere.model.I_C_BPartner)MTable.get(getCtx(), org.compiere.model.I_C_BPartner.Table_ID)
			.getPO(getDropShip_BPartner_ID(), get_TrxName());
	}

	/** Set Drop Ship Business Partner.
		@param DropShip_BPartner_ID Business Partner to ship to
	*/
	public void setDropShip_BPartner_ID (int DropShip_BPartner_ID)
	{
		if (DropShip_BPartner_ID < 1)
			set_Value (COLUMNNAME_DropShip_BPartner_ID, null);
		else
			set_Value (COLUMNNAME_DropShip_BPartner_ID, Integer.valueOf(DropShip_BPartner_ID));
	}

	/** Get Drop Ship Business Partner.
		@return Business Partner to ship to
	  */
	public int getDropShip_BPartner_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_DropShip_BPartner_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_BPartner_Location getDropShip_Location() throws RuntimeException
	{
		return (org.compiere.model.I_C_BPartner_Location)MTable.get(getCtx(), org.compiere.model.I_C_BPartner_Location.Table_ID)
			.getPO(getDropShip_Location_ID(), get_TrxName());
	}

	/** Set Drop Shipment Location.
		@param DropShip_Location_ID Business Partner Location for shipping to
	*/
	public void setDropShip_Location_ID (int DropShip_Location_ID)
	{
		if (DropShip_Location_ID < 1)
			set_Value (COLUMNNAME_DropShip_Location_ID, null);
		else
			set_Value (COLUMNNAME_DropShip_Location_ID, Integer.valueOf(DropShip_Location_ID));
	}

	/** Get Drop Shipment Location.
		@return Business Partner Location for shipping to
	  */
	public int getDropShip_Location_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_DropShip_Location_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_AD_User getDropShip_User() throws RuntimeException
	{
		return (org.compiere.model.I_AD_User)MTable.get(getCtx(), org.compiere.model.I_AD_User.Table_ID)
			.getPO(getDropShip_User_ID(), get_TrxName());
	}

	/** Set Drop Shipment Contact.
		@param DropShip_User_ID Business Partner Contact for drop shipment
	*/
	public void setDropShip_User_ID (int DropShip_User_ID)
	{
		if (DropShip_User_ID < 1)
			set_Value (COLUMNNAME_DropShip_User_ID, null);
		else
			set_Value (COLUMNNAME_DropShip_User_ID, Integer.valueOf(DropShip_User_ID));
	}

	/** Get Drop Shipment Contact.
		@return Business Partner Contact for drop shipment
	  */
	public int getDropShip_User_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_DropShip_User_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Freight Amount.
		@param FreightAmt Freight Amount 
	*/
	public void setFreightAmt (BigDecimal FreightAmt)
	{
		set_Value (COLUMNNAME_FreightAmt, FreightAmt);
	}

	/** Get Freight Amount.
		@return Freight Amount 
	  */
	public BigDecimal getFreightAmt()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_FreightAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** FreightCostRule AD_Reference_ID=153 */
	public static final int FREIGHTCOSTRULE_AD_Reference_ID=153;
	/** Calculated = C */
	public static final String FREIGHTCOSTRULE_Calculated = "C";
	/** Fix price = F */
	public static final String FREIGHTCOSTRULE_FixPrice = "F";
	/** Freight included = I */
	public static final String FREIGHTCOSTRULE_FreightIncluded = "I";
	/** Line = L */
	public static final String FREIGHTCOSTRULE_Line = "L";
	/** Set Freight Cost Rule.
		@param FreightCostRule Method for charging Freight
	*/
	public void setFreightCostRule (String FreightCostRule)
	{

		set_Value (COLUMNNAME_FreightCostRule, FreightCostRule);
	}

	/** Get Freight Cost Rule.
		@return Method for charging Freight
	  */
	public String getFreightCostRule()
	{
		return (String)get_Value(COLUMNNAME_FreightCostRule);
	}

	/** Set Grand Total.
		@param GrandTotal Total amount of document
	*/
	public void setGrandTotal (BigDecimal GrandTotal)
	{
		set_Value (COLUMNNAME_GrandTotal, GrandTotal);
	}

	/** Get Grand Total.
		@return Total amount of document
	  */
	public BigDecimal getGrandTotal()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_GrandTotal);
		if (bd == null)
			 return Env.ZERO;
		return bd;
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

	/** Set Approved.
		@param IsApproved Indicates if this document requires approval
	*/
	public void setIsApproved (boolean IsApproved)
	{
		set_ValueNoCheck (COLUMNNAME_IsApproved, Boolean.valueOf(IsApproved));
	}

	/** Get Approved.
		@return Indicates if this document requires approval
	  */
	public boolean isApproved()
	{
		Object oo = get_Value(COLUMNNAME_IsApproved);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Automatic Update.
		@param IsAutomaticUpdateJP Automatic Update
	*/
	public void setIsAutomaticUpdateJP (boolean IsAutomaticUpdateJP)
	{
		set_Value (COLUMNNAME_IsAutomaticUpdateJP, Boolean.valueOf(IsAutomaticUpdateJP));
	}

	/** Get Automatic Update.
		@return Automatic Update	  */
	public boolean isAutomaticUpdateJP()
	{
		Object oo = get_Value(COLUMNNAME_IsAutomaticUpdateJP);
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

	/** Set Drop Shipment.
		@param IsDropShip Drop Shipments are sent directly to the Drop Shipment Location
	*/
	public void setIsDropShip (boolean IsDropShip)
	{
		set_Value (COLUMNNAME_IsDropShip, Boolean.valueOf(IsDropShip));
	}

	/** Get Drop Shipment.
		@return Drop Shipments are sent directly to the Drop Shipment Location
	  */
	public boolean isDropShip()
	{
		Object oo = get_Value(COLUMNNAME_IsDropShip);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Renewed Contract Content.
		@param IsRenewedContractContentJP Renewed Contract Content
	*/
	public void setIsRenewedContractContentJP (boolean IsRenewedContractContentJP)
	{
		set_Value (COLUMNNAME_IsRenewedContractContentJP, Boolean.valueOf(IsRenewedContractContentJP));
	}

	/** Get Renewed Contract Content.
		@return Renewed Contract Content	  */
	public boolean isRenewedContractContentJP()
	{
		Object oo = get_Value(COLUMNNAME_IsRenewedContractContentJP);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Sales Transaction.
		@param IsSOTrx This is a Sales Transaction
	*/
	public void setIsSOTrx (boolean IsSOTrx)
	{
		set_Value (COLUMNNAME_IsSOTrx, Boolean.valueOf(IsSOTrx));
	}

	/** Get Sales Transaction.
		@return This is a Sales Transaction
	  */
	public boolean isSOTrx()
	{
		Object oo = get_Value(COLUMNNAME_IsSOTrx);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Schedule was created.
		@param IsScheduleCreatedJP Schedule was created
	*/
	public void setIsScheduleCreatedJP (boolean IsScheduleCreatedJP)
	{
		set_Value (COLUMNNAME_IsScheduleCreatedJP, Boolean.valueOf(IsScheduleCreatedJP));
	}

	/** Get Schedule was created.
		@return Schedule was created	  */
	public boolean isScheduleCreatedJP()
	{
		Object oo = get_Value(COLUMNNAME_IsScheduleCreatedJP);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Price includes Tax.
		@param IsTaxIncluded Tax is included in the price 
	*/
	public void setIsTaxIncluded (boolean IsTaxIncluded)
	{
		set_ValueNoCheck (COLUMNNAME_IsTaxIncluded, Boolean.valueOf(IsTaxIncluded));
	}

	/** Get Price includes Tax.
		@return Tax is included in the price 
	  */
	public boolean isTaxIncluded()
	{
		Object oo = get_Value(COLUMNNAME_IsTaxIncluded);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	public org.compiere.model.I_C_DocType getJP_BaseDocDocType() throws RuntimeException
	{
		return (org.compiere.model.I_C_DocType)MTable.get(getCtx(), org.compiere.model.I_C_DocType.Table_ID)
			.getPO(getJP_BaseDocDocType_ID(), get_TrxName());
	}

	/** Set Base Doc DocType.
		@param JP_BaseDocDocType_ID Base Doc DocType
	*/
	public void setJP_BaseDocDocType_ID (int JP_BaseDocDocType_ID)
	{
		if (JP_BaseDocDocType_ID < 1)
			set_Value (COLUMNNAME_JP_BaseDocDocType_ID, null);
		else
			set_Value (COLUMNNAME_JP_BaseDocDocType_ID, Integer.valueOf(JP_BaseDocDocType_ID));
	}

	/** Get Base Doc DocType.
		@return Base Doc DocType	  */
	public int getJP_BaseDocDocType_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_BaseDocDocType_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Communication Column.
		@param JP_CommunicationColumn Communication Column
	*/
	public void setJP_CommunicationColumn (String JP_CommunicationColumn)
	{
		set_Value (COLUMNNAME_JP_CommunicationColumn, JP_CommunicationColumn);
	}

	/** Get Communication Column.
		@return Communication Column	  */
	public String getJP_CommunicationColumn()
	{
		return (String)get_Value(COLUMNNAME_JP_CommunicationColumn);
	}

	/** Extend Contract Process Date  = EX */
	public static final String JP_CONTRACTC_AUTOUPDATEPOLICY_ExtendContractProcessDate = "EX";
	/** Renew the contract Content = RE */
	public static final String JP_CONTRACTC_AUTOUPDATEPOLICY_RenewTheContractContent = "RE";
	/** Set Auto Update Policy.
		@param JP_ContractC_AutoUpdatePolicy Auto Update Policy
	*/
	public void setJP_ContractC_AutoUpdatePolicy (String JP_ContractC_AutoUpdatePolicy)
	{

		set_Value (COLUMNNAME_JP_ContractC_AutoUpdatePolicy, JP_ContractC_AutoUpdatePolicy);
	}

	/** Get Auto Update Policy.
		@return Auto Update Policy	  */
	public String getJP_ContractC_AutoUpdatePolicy()
	{
		return (String)get_Value(COLUMNNAME_JP_ContractC_AutoUpdatePolicy);
	}

	public I_JP_ContractCalender getJP_ContractCalender() throws RuntimeException
	{
		return (I_JP_ContractCalender)MTable.get(getCtx(), I_JP_ContractCalender.Table_ID)
			.getPO(getJP_ContractCalender_ID(), get_TrxName());
	}

	/** Set Contract Calendar.
		@param JP_ContractCalender_ID Contract Calendar
	*/
	public void setJP_ContractCalender_ID (int JP_ContractCalender_ID)
	{
		if (JP_ContractCalender_ID < 1)
			set_Value (COLUMNNAME_JP_ContractCalender_ID, null);
		else
			set_Value (COLUMNNAME_JP_ContractCalender_ID, Integer.valueOf(JP_ContractCalender_ID));
	}

	/** Get Contract Calendar.
		@return Contract Calendar	  */
	public int getJP_ContractCalender_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ContractCalender_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_JP_ContractContentT getJP_ContractContentT() throws RuntimeException
	{
		return (I_JP_ContractContentT)MTable.get(getCtx(), I_JP_ContractContentT.Table_ID)
			.getPO(getJP_ContractContentT_ID(), get_TrxName());
	}

	/** Set Contract Content Template.
		@param JP_ContractContentT_ID Contract Content Template
	*/
	public void setJP_ContractContentT_ID (int JP_ContractContentT_ID)
	{
		if (JP_ContractContentT_ID < 1)
			set_ValueNoCheck (COLUMNNAME_JP_ContractContentT_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_JP_ContractContentT_ID, Integer.valueOf(JP_ContractContentT_ID));
	}

	/** Get Contract Content Template.
		@return Contract Content Template	  */
	public int getJP_ContractContentT_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ContractContentT_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Contract Content.
		@param JP_ContractContent_ID Contract Content
	*/
	public void setJP_ContractContent_ID (int JP_ContractContent_ID)
	{
		if (JP_ContractContent_ID < 1)
			set_ValueNoCheck (COLUMNNAME_JP_ContractContent_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_JP_ContractContent_ID, Integer.valueOf(JP_ContractContent_ID));
	}

	/** Get Contract Content.
		@return Contract Content	  */
	public int getJP_ContractContent_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ContractContent_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Contract Content(UU).
		@param JP_ContractContent_UU Contract Content(UU)
	*/
	public void setJP_ContractContent_UU (String JP_ContractContent_UU)
	{
		set_ValueNoCheck (COLUMNNAME_JP_ContractContent_UU, JP_ContractContent_UU);
	}

	/** Get Contract Content(UU).
		@return Contract Content(UU)	  */
	public String getJP_ContractContent_UU()
	{
		return (String)get_Value(COLUMNNAME_JP_ContractContent_UU);
	}

	/** Set Contract Process Date(From).
		@param JP_ContractProcDate_From Contract Process Date(From)
	*/
	public void setJP_ContractProcDate_From (Timestamp JP_ContractProcDate_From)
	{
		set_Value (COLUMNNAME_JP_ContractProcDate_From, JP_ContractProcDate_From);
	}

	/** Get Contract Process Date(From).
		@return Contract Process Date(From)	  */
	public Timestamp getJP_ContractProcDate_From()
	{
		return (Timestamp)get_Value(COLUMNNAME_JP_ContractProcDate_From);
	}

	/** Set Contract Process Date(To).
		@param JP_ContractProcDate_To Contract Process Date(To)
	*/
	public void setJP_ContractProcDate_To (Timestamp JP_ContractProcDate_To)
	{
		set_Value (COLUMNNAME_JP_ContractProcDate_To, JP_ContractProcDate_To);
	}

	/** Get Contract Process Date(To).
		@return Contract Process Date(To)	  */
	public Timestamp getJP_ContractProcDate_To()
	{
		return (Timestamp)get_Value(COLUMNNAME_JP_ContractProcDate_To);
	}

	/** -- = -- */
	public static final String JP_CONTRACTPROCSTATUS___ = "--";
	/** Invalid = IN */
	public static final String JP_CONTRACTPROCSTATUS_Invalid = "IN";
	/** In Progress = IP */
	public static final String JP_CONTRACTPROCSTATUS_InProgress = "IP";
	/** Processed = PD */
	public static final String JP_CONTRACTPROCSTATUS_Processed = "PD";
	/** Suspend = SD */
	public static final String JP_CONTRACTPROCSTATUS_Suspend = "SD";
	/** Unprocessed = UN */
	public static final String JP_CONTRACTPROCSTATUS_Unprocessed = "UN";
	/** Set Contract Process Status.
		@param JP_ContractProcStatus Contract Process Status
	*/
	public void setJP_ContractProcStatus (String JP_ContractProcStatus)
	{

		set_Value (COLUMNNAME_JP_ContractProcStatus, JP_ContractProcStatus);
	}

	/** Get Contract Process Status.
		@return Contract Process Status	  */
	public String getJP_ContractProcStatus()
	{
		return (String)get_Value(COLUMNNAME_JP_ContractProcStatus);
	}

	/** Direct Contract Process = DC */
	public static final String JP_CONTRACTPROCESSMETHOD_DirectContractProcess = "DC";
	/** Indirect Contract Process = IC */
	public static final String JP_CONTRACTPROCESSMETHOD_IndirectContractProcess = "IC";
	/** Set Contract Process Method.
		@param JP_ContractProcessMethod Contract Process Method
	*/
	public void setJP_ContractProcessMethod (String JP_ContractProcessMethod)
	{

		set_Value (COLUMNNAME_JP_ContractProcessMethod, JP_ContractProcessMethod);
	}

	/** Get Contract Process Method.
		@return Contract Process Method	  */
	public String getJP_ContractProcessMethod()
	{
		return (String)get_Value(COLUMNNAME_JP_ContractProcessMethod);
	}

	public I_JP_ContractProcess getJP_ContractProcess() throws RuntimeException
	{
		return (I_JP_ContractProcess)MTable.get(getCtx(), I_JP_ContractProcess.Table_ID)
			.getPO(getJP_ContractProcess_ID(), get_TrxName());
	}

	/** Set Contract Process.
		@param JP_ContractProcess_ID Contract Process
	*/
	public void setJP_ContractProcess_ID (int JP_ContractProcess_ID)
	{
		if (JP_ContractProcess_ID < 1)
			set_Value (COLUMNNAME_JP_ContractProcess_ID, null);
		else
			set_Value (COLUMNNAME_JP_ContractProcess_ID, Integer.valueOf(JP_ContractProcess_ID));
	}

	/** Get Contract Process.
		@return Contract Process	  */
	public int getJP_ContractProcess_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ContractProcess_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_JP_Contract_Acct getJP_Contract_Acct() throws RuntimeException
	{
		return (I_JP_Contract_Acct)MTable.get(getCtx(), I_JP_Contract_Acct.Table_ID)
			.getPO(getJP_Contract_Acct_ID(), get_TrxName());
	}

	/** Set Contract Acct Info.
		@param JP_Contract_Acct_ID Contract Acct Info
	*/
	public void setJP_Contract_Acct_ID (int JP_Contract_Acct_ID)
	{
		if (JP_Contract_Acct_ID < 1)
			set_Value (COLUMNNAME_JP_Contract_Acct_ID, null);
		else
			set_Value (COLUMNNAME_JP_Contract_Acct_ID, Integer.valueOf(JP_Contract_Acct_ID));
	}

	/** Get Contract Acct Info.
		@return Contract Acct Info	  */
	public int getJP_Contract_Acct_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Contract_Acct_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_JP_Contract getJP_Contract() throws RuntimeException
	{
		return (I_JP_Contract)MTable.get(getCtx(), I_JP_Contract.Table_ID)
			.getPO(getJP_Contract_ID(), get_TrxName());
	}

	/** Set Contract Document.
		@param JP_Contract_ID Contract Document
	*/
	public void setJP_Contract_ID (int JP_Contract_ID)
	{
		if (JP_Contract_ID < 1)
			set_ValueNoCheck (COLUMNNAME_JP_Contract_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_JP_Contract_ID, Integer.valueOf(JP_Contract_ID));
	}

	/** Get Contract Document.
		@return Contract Document	  */
	public int getJP_Contract_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Contract_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_JP_ContractContent getJP_CounterContractContent() throws RuntimeException
	{
		return (I_JP_ContractContent)MTable.get(getCtx(), I_JP_ContractContent.Table_ID)
			.getPO(getJP_CounterContractContent_ID(), get_TrxName());
	}

	/** Set Counter Contract Content.
		@param JP_CounterContractContent_ID Counter Contract Content
	*/
	public void setJP_CounterContractContent_ID (int JP_CounterContractContent_ID)
	{
		if (JP_CounterContractContent_ID < 1)
			set_Value (COLUMNNAME_JP_CounterContractContent_ID, null);
		else
			set_Value (COLUMNNAME_JP_CounterContractContent_ID, Integer.valueOf(JP_CounterContractContent_ID));
	}

	/** Get Counter Contract Content.
		@return Counter Contract Content	  */
	public int getJP_CounterContractContent_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_CounterContractContent_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Create Ship/Receipt &amp; Invoice = BT */
	public static final String JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceiptInvoice = "BT";
	/** Create Ship/Receipt = IO */
	public static final String JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceipt = "IO";
	/** Create Invoice = IV */
	public static final String JP_CREATEDERIVATIVEDOCPOLICY_CreateInvoice = "IV";
	/** Manual = MA */
	public static final String JP_CREATEDERIVATIVEDOCPOLICY_Manual = "MA";
	/** Set Create Derivative Doc Policy.
		@param JP_CreateDerivativeDocPolicy Create Derivative Doc Policy
	*/
	public void setJP_CreateDerivativeDocPolicy (String JP_CreateDerivativeDocPolicy)
	{

		set_Value (COLUMNNAME_JP_CreateDerivativeDocPolicy, JP_CreateDerivativeDocPolicy);
	}

	/** Get Create Derivative Doc Policy.
		@return Create Derivative Doc Policy	  */
	public String getJP_CreateDerivativeDocPolicy()
	{
		return (String)get_Value(COLUMNNAME_JP_CreateDerivativeDocPolicy);
	}

	public I_JP_Estimation getJP_Estimation() throws RuntimeException
	{
		return (I_JP_Estimation)MTable.get(getCtx(), I_JP_Estimation.Table_ID)
			.getPO(getJP_Estimation_ID(), get_TrxName());
	}

	/** Set Estimation &amp; Handwritten.
		@param JP_Estimation_ID Estimation &amp; Handwritten
	*/
	public void setJP_Estimation_ID (int JP_Estimation_ID)
	{
		if (JP_Estimation_ID < 1)
			set_Value (COLUMNNAME_JP_Estimation_ID, null);
		else
			set_Value (COLUMNNAME_JP_Estimation_ID, Integer.valueOf(JP_Estimation_ID));
	}

	/** Get Estimation &amp; Handwritten.
		@return Estimation &amp; Handwritten	  */
	public int getJP_Estimation_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Estimation_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_Locator getJP_Locator() throws RuntimeException
	{
		return (org.compiere.model.I_M_Locator)MTable.get(getCtx(), org.compiere.model.I_M_Locator.Table_ID)
			.getPO(getJP_Locator_ID(), get_TrxName());
	}

	/** Set Locator.
		@param JP_Locator_ID Locator
	*/
	public void setJP_Locator_ID (int JP_Locator_ID)
	{
		if (JP_Locator_ID < 1)
			set_Value (COLUMNNAME_JP_Locator_ID, null);
		else
			set_Value (COLUMNNAME_JP_Locator_ID, Integer.valueOf(JP_Locator_ID));
	}

	/** Get Locator.
		@return Locator	  */
	public int getJP_Locator_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Locator_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_JP_ContractContent getJP_PreContractContent() throws RuntimeException
	{
		return (I_JP_ContractContent)MTable.get(getCtx(), I_JP_ContractContent.Table_ID)
			.getPO(getJP_PreContractContent_ID(), get_TrxName());
	}

	/** Set Precontract Content.
		@param JP_PreContractContent_ID Precontract Content
	*/
	public void setJP_PreContractContent_ID (int JP_PreContractContent_ID)
	{
		if (JP_PreContractContent_ID < 1)
			set_Value (COLUMNNAME_JP_PreContractContent_ID, null);
		else
			set_Value (COLUMNNAME_JP_PreContractContent_ID, Integer.valueOf(JP_PreContractContent_ID));
	}

	/** Get Precontract Content.
		@return Precontract Content	  */
	public int getJP_PreContractContent_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_PreContractContent_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Process Now.
		@param JP_Processing1 Process Now
	*/
	public void setJP_Processing1 (String JP_Processing1)
	{
		set_Value (COLUMNNAME_JP_Processing1, JP_Processing1);
	}

	/** Get Process Now.
		@return Process Now	  */
	public String getJP_Processing1()
	{
		return (String)get_Value(COLUMNNAME_JP_Processing1);
	}

	/** Set Process Now.
		@param JP_Processing2 Process Now
	*/
	public void setJP_Processing2 (String JP_Processing2)
	{
		set_Value (COLUMNNAME_JP_Processing2, JP_Processing2);
	}

	/** Get Process Now.
		@return Process Now	  */
	public String getJP_Processing2()
	{
		return (String)get_Value(COLUMNNAME_JP_Processing2);
	}

	/** Set Process Now.
		@param JP_Processing3 Process Now
	*/
	public void setJP_Processing3 (String JP_Processing3)
	{
		set_Value (COLUMNNAME_JP_Processing3, JP_Processing3);
	}

	/** Get Process Now.
		@return Process Now	  */
	public String getJP_Processing3()
	{
		return (String)get_Value(COLUMNNAME_JP_Processing3);
	}

	/** Set Process Now.
		@param JP_Processing4 Process Now
	*/
	public void setJP_Processing4 (String JP_Processing4)
	{
		set_Value (COLUMNNAME_JP_Processing4, JP_Processing4);
	}

	/** Get Process Now.
		@return Process Now	  */
	public String getJP_Processing4()
	{
		return (String)get_Value(COLUMNNAME_JP_Processing4);
	}

	/** Set Process Now.
		@param JP_Processing5 Process Now
	*/
	public void setJP_Processing5 (String JP_Processing5)
	{
		set_Value (COLUMNNAME_JP_Processing5, JP_Processing5);
	}

	/** Get Process Now.
		@return Process Now	  */
	public String getJP_Processing5()
	{
		return (String)get_Value(COLUMNNAME_JP_Processing5);
	}

	/** Set Remarks.
		@param JP_Remarks JPIERE-0490:JPBP
	*/
	public void setJP_Remarks (String JP_Remarks)
	{
		set_Value (COLUMNNAME_JP_Remarks, JP_Remarks);
	}

	/** Get Remarks.
		@return JPIERE-0490:JPBP
	  */
	public String getJP_Remarks()
	{
		return (String)get_Value(COLUMNNAME_JP_Remarks);
	}

	/** Set Subject.
		@param JP_Subject JPIERE-0490:JPBP
	*/
	public void setJP_Subject (String JP_Subject)
	{
		set_Value (COLUMNNAME_JP_Subject, JP_Subject);
	}

	/** Get Subject.
		@return JPIERE-0490:JPBP
	  */
	public String getJP_Subject()
	{
		return (String)get_Value(COLUMNNAME_JP_Subject);
	}

	public org.compiere.model.I_M_FreightCategory getM_FreightCategory() throws RuntimeException
	{
		return (org.compiere.model.I_M_FreightCategory)MTable.get(getCtx(), org.compiere.model.I_M_FreightCategory.Table_ID)
			.getPO(getM_FreightCategory_ID(), get_TrxName());
	}

	/** Set Freight Category.
		@param M_FreightCategory_ID Category of the Freight
	*/
	public void setM_FreightCategory_ID (int M_FreightCategory_ID)
	{
		if (M_FreightCategory_ID < 1)
			set_Value (COLUMNNAME_M_FreightCategory_ID, null);
		else
			set_Value (COLUMNNAME_M_FreightCategory_ID, Integer.valueOf(M_FreightCategory_ID));
	}

	/** Get Freight Category.
		@return Category of the Freight
	  */
	public int getM_FreightCategory_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_FreightCategory_ID);
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

	public org.compiere.model.I_M_Shipper getM_Shipper() throws RuntimeException
	{
		return (org.compiere.model.I_M_Shipper)MTable.get(getCtx(), org.compiere.model.I_M_Shipper.Table_ID)
			.getPO(getM_Shipper_ID(), get_TrxName());
	}

	/** Set Shipper.
		@param M_Shipper_ID Method or manner of product delivery
	*/
	public void setM_Shipper_ID (int M_Shipper_ID)
	{
		if (M_Shipper_ID < 1)
			set_Value (COLUMNNAME_M_Shipper_ID, null);
		else
			set_Value (COLUMNNAME_M_Shipper_ID, Integer.valueOf(M_Shipper_ID));
	}

	/** Get Shipper.
		@return Method or manner of product delivery
	  */
	public int getM_Shipper_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Shipper_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_Warehouse getM_Warehouse() throws RuntimeException
	{
		return (org.compiere.model.I_M_Warehouse)MTable.get(getCtx(), org.compiere.model.I_M_Warehouse.Table_ID)
			.getPO(getM_Warehouse_ID(), get_TrxName());
	}

	/** Set Org Warehouse.
		@param M_Warehouse_ID Storage Warehouse and Service Point
	*/
	public void setM_Warehouse_ID (int M_Warehouse_ID)
	{
		if (M_Warehouse_ID < 1)
			set_Value (COLUMNNAME_M_Warehouse_ID, null);
		else
			set_Value (COLUMNNAME_M_Warehouse_ID, Integer.valueOf(M_Warehouse_ID));
	}

	/** Get Org Warehouse.
		@return Storage Warehouse and Service Point
	  */
	public int getM_Warehouse_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Warehouse_ID);
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

	/** Other = -- */
	public static final String ORDERTYPE_Other = "--";
	/** Quotation = OB */
	public static final String ORDERTYPE_Quotation = "OB";
	/** Proposal = ON */
	public static final String ORDERTYPE_Proposal = "ON";
	/** Prepay Order = PR */
	public static final String ORDERTYPE_PrepayOrder = "PR";
	/** Return Material = RM */
	public static final String ORDERTYPE_ReturnMaterial = "RM";
	/** Standard Order = SO */
	public static final String ORDERTYPE_StandardOrder = "SO";
	/** On Credit Order = WI */
	public static final String ORDERTYPE_OnCreditOrder = "WI";
	/** Warehouse Order = WP */
	public static final String ORDERTYPE_WarehouseOrder = "WP";
	/** POS Order = WR */
	public static final String ORDERTYPE_POSOrder = "WR";
	/** Set Order Type.
		@param OrderType Type of Order: MRP records grouped by source (Sales Order, Purchase Order, Distribution Order, Requisition)
	*/
	public void setOrderType (String OrderType)
	{

		set_Value (COLUMNNAME_OrderType, OrderType);
	}

	/** Get Order Type.
		@return Type of Order: MRP records grouped by source (Sales Order, Purchase Order, Distribution Order, Requisition)
	  */
	public String getOrderType()
	{
		return (String)get_Value(COLUMNNAME_OrderType);
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

	/** Set Posted.
		@param Posted Posting status
	*/
	public void setPosted (boolean Posted)
	{
		set_Value (COLUMNNAME_Posted, Boolean.valueOf(Posted));
	}

	/** Get Posted.
		@return Posting status
	  */
	public boolean isPosted()
	{
		Object oo = get_Value(COLUMNNAME_Posted);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** PriorityRule AD_Reference_ID=154 */
	public static final int PRIORITYRULE_AD_Reference_ID=154;
	/** Urgent = 1 */
	public static final String PRIORITYRULE_Urgent = "1";
	/** High = 3 */
	public static final String PRIORITYRULE_High = "3";
	/** Medium = 5 */
	public static final String PRIORITYRULE_Medium = "5";
	/** Low = 7 */
	public static final String PRIORITYRULE_Low = "7";
	/** Minor = 9 */
	public static final String PRIORITYRULE_Minor = "9";
	/** Set Priority.
		@param PriorityRule Priority of a document
	*/
	public void setPriorityRule (String PriorityRule)
	{

		set_Value (COLUMNNAME_PriorityRule, PriorityRule);
	}

	/** Get Priority.
		@return Priority of a document
	  */
	public String getPriorityRule()
	{
		return (String)get_Value(COLUMNNAME_PriorityRule);
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

	/** Set Processed On.
		@param ProcessedOn The date+time (expressed in decimal format) when the document has been processed
	*/
	public void setProcessedOn (BigDecimal ProcessedOn)
	{
		set_Value (COLUMNNAME_ProcessedOn, ProcessedOn);
	}

	/** Get Processed On.
		@return The date+time (expressed in decimal format) when the document has been processed
	  */
	public BigDecimal getProcessedOn()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_ProcessedOn);
		if (bd == null)
			 return Env.ZERO;
		return bd;
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

	/** Set Send EMail.
		@param SendEMail Enable sending Document EMail
	*/
	public void setSendEMail (boolean SendEMail)
	{
		set_Value (COLUMNNAME_SendEMail, Boolean.valueOf(SendEMail));
	}

	/** Get Send EMail.
		@return Enable sending Document EMail
	  */
	public boolean isSendEMail()
	{
		Object oo = get_Value(COLUMNNAME_SendEMail);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Total Lines.
		@param TotalLines Total of all document lines
	*/
	public void setTotalLines (BigDecimal TotalLines)
	{
		set_Value (COLUMNNAME_TotalLines, TotalLines);
	}

	/** Get Total Lines.
		@return Total of all document lines
	  */
	public BigDecimal getTotalLines()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_TotalLines);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	public org.compiere.model.I_C_ElementValue getUser1() throws RuntimeException
	{
		return (org.compiere.model.I_C_ElementValue)MTable.get(getCtx(), org.compiere.model.I_C_ElementValue.Table_ID)
			.getPO(getUser1_ID(), get_TrxName());
	}

	/** Set User Element List 1.
		@param User1_ID User defined list element #1
	*/
	public void setUser1_ID (int User1_ID)
	{
		if (User1_ID < 1)
			set_Value (COLUMNNAME_User1_ID, null);
		else
			set_Value (COLUMNNAME_User1_ID, Integer.valueOf(User1_ID));
	}

	/** Get User Element List 1.
		@return User defined list element #1
	  */
	public int getUser1_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_User1_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_ElementValue getUser2() throws RuntimeException
	{
		return (org.compiere.model.I_C_ElementValue)MTable.get(getCtx(), org.compiere.model.I_C_ElementValue.Table_ID)
			.getPO(getUser2_ID(), get_TrxName());
	}

	/** Set User Element List 2.
		@param User2_ID User defined list element #2
	*/
	public void setUser2_ID (int User2_ID)
	{
		if (User2_ID < 1)
			set_Value (COLUMNNAME_User2_ID, null);
		else
			set_Value (COLUMNNAME_User2_ID, Integer.valueOf(User2_ID));
	}

	/** Get User Element List 2.
		@return User defined list element #2
	  */
	public int getUser2_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_User2_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}