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

/** Generated Model for I_DataMigrationJP
 *  @author iDempiere (generated) 
 *  @version Release 5.1 - $Id$ */
public class X_I_DataMigrationJP extends PO implements I_I_DataMigrationJP, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20180815L;

    /** Standard Constructor */
    public X_I_DataMigrationJP (Properties ctx, int I_DataMigrationJP_ID, String trxName)
    {
      super (ctx, I_DataMigrationJP_ID, trxName);
      /** if (I_DataMigrationJP_ID == 0)
        {
			setI_DataMigrationJP_ID (0);
        } */
    }

    /** Load Constructor */
    public X_I_DataMigrationJP (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_I_DataMigrationJP[")
        .append(get_ID()).append("]");
      return sb.toString();
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

	public org.compiere.model.I_AD_Table getAD_Table() throws RuntimeException
    {
		return (org.compiere.model.I_AD_Table)MTable.get(getCtx(), org.compiere.model.I_AD_Table.Table_Name)
			.getPO(getAD_Table_ID(), get_TrxName());	}

	/** Set Table.
		@param AD_Table_ID 
		Database Table information
	  */
	public void setAD_Table_ID (int AD_Table_ID)
	{
		if (AD_Table_ID < 1) 
			set_Value (COLUMNNAME_AD_Table_ID, null);
		else 
			set_Value (COLUMNNAME_AD_Table_ID, Integer.valueOf(AD_Table_ID));
	}

	/** Get Table.
		@return Database Table information
	  */
	public int getAD_Table_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_Table_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_AD_User getAD_User() throws RuntimeException
    {
		return (org.compiere.model.I_AD_User)MTable.get(getCtx(), org.compiere.model.I_AD_User.Table_Name)
			.getPO(getAD_User_ID(), get_TrxName());	}

	/** Set User/Contact.
		@param AD_User_ID 
		User within the system - Internal or Business Partner Contact
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
	public int getAD_User_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_User_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Activity getC_Activity() throws RuntimeException
    {
		return (org.compiere.model.I_C_Activity)MTable.get(getCtx(), org.compiere.model.I_C_Activity.Table_Name)
			.getPO(getC_Activity_ID(), get_TrxName());	}

	/** Set Activity.
		@param C_Activity_ID 
		Business Activity
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
	public int getC_Activity_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Activity_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
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

	public org.compiere.model.I_C_BankAccount getC_BankAccount() throws RuntimeException
    {
		return (org.compiere.model.I_C_BankAccount)MTable.get(getCtx(), org.compiere.model.I_C_BankAccount.Table_Name)
			.getPO(getC_BankAccount_ID(), get_TrxName());	}

	/** Set Bank Account.
		@param C_BankAccount_ID 
		Account at the Bank
	  */
	public void setC_BankAccount_ID (int C_BankAccount_ID)
	{
		if (C_BankAccount_ID < 1) 
			set_Value (COLUMNNAME_C_BankAccount_ID, null);
		else 
			set_Value (COLUMNNAME_C_BankAccount_ID, Integer.valueOf(C_BankAccount_ID));
	}

	/** Get Bank Account.
		@return Account at the Bank
	  */
	public int getC_BankAccount_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_BankAccount_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Campaign getC_Campaign() throws RuntimeException
    {
		return (org.compiere.model.I_C_Campaign)MTable.get(getCtx(), org.compiere.model.I_C_Campaign.Table_Name)
			.getPO(getC_Campaign_ID(), get_TrxName());	}

	/** Set Campaign.
		@param C_Campaign_ID 
		Marketing Campaign
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
	public int getC_Campaign_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Campaign_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Charge getC_Charge() throws RuntimeException
    {
		return (org.compiere.model.I_C_Charge)MTable.get(getCtx(), org.compiere.model.I_C_Charge.Table_Name)
			.getPO(getC_Charge_ID(), get_TrxName());	}

	/** Set Charge.
		@param C_Charge_ID 
		Additional document charges
	  */
	public void setC_Charge_ID (int C_Charge_ID)
	{
		if (C_Charge_ID < 1) 
			set_Value (COLUMNNAME_C_Charge_ID, null);
		else 
			set_Value (COLUMNNAME_C_Charge_ID, Integer.valueOf(C_Charge_ID));
	}

	/** Get Charge.
		@return Additional document charges
	  */
	public int getC_Charge_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Charge_ID);
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

	public org.compiere.model.I_C_PaymentTerm getC_PaymentTerm() throws RuntimeException
    {
		return (org.compiere.model.I_C_PaymentTerm)MTable.get(getCtx(), org.compiere.model.I_C_PaymentTerm.Table_Name)
			.getPO(getC_PaymentTerm_ID(), get_TrxName());	}

	/** Set Payment Term.
		@param C_PaymentTerm_ID 
		The terms of Payment (timing, discount)
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
	public int getC_PaymentTerm_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_PaymentTerm_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Project getC_Project() throws RuntimeException
    {
		return (org.compiere.model.I_C_Project)MTable.get(getCtx(), org.compiere.model.I_C_Project.Table_Name)
			.getPO(getC_Project_ID(), get_TrxName());	}

	/** Set Project.
		@param C_Project_ID 
		Financial Project
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
	public int getC_Project_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Project_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Tax getC_Tax() throws RuntimeException
    {
		return (org.compiere.model.I_C_Tax)MTable.get(getCtx(), org.compiere.model.I_C_Tax.Table_Name)
			.getPO(getC_Tax_ID(), get_TrxName());	}

	/** Set Tax.
		@param C_Tax_ID 
		Tax identifier
	  */
	public void setC_Tax_ID (int C_Tax_ID)
	{
		if (C_Tax_ID < 1) 
			set_Value (COLUMNNAME_C_Tax_ID, null);
		else 
			set_Value (COLUMNNAME_C_Tax_ID, Integer.valueOf(C_Tax_ID));
	}

	/** Get Tax.
		@return Tax identifier
	  */
	public int getC_Tax_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Tax_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_UOM getC_UOM() throws RuntimeException
    {
		return (org.compiere.model.I_C_UOM)MTable.get(getCtx(), org.compiere.model.I_C_UOM.Table_Name)
			.getPO(getC_UOM_ID(), get_TrxName());	}

	/** Set UOM.
		@param C_UOM_ID 
		Unit of Measure
	  */
	public void setC_UOM_ID (int C_UOM_ID)
	{
		if (C_UOM_ID < 1) 
			set_Value (COLUMNNAME_C_UOM_ID, null);
		else 
			set_Value (COLUMNNAME_C_UOM_ID, Integer.valueOf(C_UOM_ID));
	}

	/** Get UOM.
		@return Unit of Measure
	  */
	public int getC_UOM_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_UOM_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Charge amount.
		@param ChargeAmt 
		Charge Amount
	  */
	public void setChargeAmt (BigDecimal ChargeAmt)
	{
		set_Value (COLUMNNAME_ChargeAmt, ChargeAmt);
	}

	/** Get Charge amount.
		@return Charge Amount
	  */
	public BigDecimal getChargeAmt () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_ChargeAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Document Date.
		@param DateDoc 
		Date of the Document
	  */
	public void setDateDoc (Timestamp DateDoc)
	{
		set_Value (COLUMNNAME_DateDoc, DateDoc);
	}

	/** Get Document Date.
		@return Date of the Document
	  */
	public Timestamp getDateDoc () 
	{
		return (Timestamp)get_Value(COLUMNNAME_DateDoc);
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

	/** DeliveryViaRule AD_Reference_ID=152 */
	public static final int DELIVERYVIARULE_AD_Reference_ID=152;
	/** Pickup = P */
	public static final String DELIVERYVIARULE_Pickup = "P";
	/** Delivery = D */
	public static final String DELIVERYVIARULE_Delivery = "D";
	/** Shipper = S */
	public static final String DELIVERYVIARULE_Shipper = "S";
	/** Set Delivery Via.
		@param DeliveryViaRule 
		How the order will be delivered
	  */
	public void setDeliveryViaRule (String DeliveryViaRule)
	{

		set_Value (COLUMNNAME_DeliveryViaRule, DeliveryViaRule);
	}

	/** Get Delivery Via.
		@return How the order will be delivered
	  */
	public String getDeliveryViaRule () 
	{
		return (String)get_Value(COLUMNNAME_DeliveryViaRule);
	}

	/** Set Description.
		@param Description 
		Optional short description of the record
	  */
	public void setDescription (String Description)
	{
		set_Value (COLUMNNAME_Description, Description);
	}

	/** Get Description.
		@return Optional short description of the record
	  */
	public String getDescription () 
	{
		return (String)get_Value(COLUMNNAME_Description);
	}

	/** Set Discount Amount.
		@param DiscountAmt 
		Calculated amount of discount
	  */
	public void setDiscountAmt (BigDecimal DiscountAmt)
	{
		set_Value (COLUMNNAME_DiscountAmt, DiscountAmt);
	}

	/** Get Discount Amount.
		@return Calculated amount of discount
	  */
	public BigDecimal getDiscountAmt () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_DiscountAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** DocBaseType AD_Reference_ID=183 */
	public static final int DOCBASETYPE_AD_Reference_ID=183;
	/** GL Journal = GLJ */
	public static final String DOCBASETYPE_GLJournal = "GLJ";
	/** GL Document = GLD */
	public static final String DOCBASETYPE_GLDocument = "GLD";
	/** AP Invoice = API */
	public static final String DOCBASETYPE_APInvoice = "API";
	/** AP Payment = APP */
	public static final String DOCBASETYPE_APPayment = "APP";
	/** AR Invoice = ARI */
	public static final String DOCBASETYPE_ARInvoice = "ARI";
	/** AR Receipt = ARR */
	public static final String DOCBASETYPE_ARReceipt = "ARR";
	/** Sales Order = SOO */
	public static final String DOCBASETYPE_SalesOrder = "SOO";
	/** AR Pro Forma Invoice = ARF */
	public static final String DOCBASETYPE_ARProFormaInvoice = "ARF";
	/** Material Delivery = MMS */
	public static final String DOCBASETYPE_MaterialDelivery = "MMS";
	/** Material Receipt = MMR */
	public static final String DOCBASETYPE_MaterialReceipt = "MMR";
	/** Material Movement = MMM */
	public static final String DOCBASETYPE_MaterialMovement = "MMM";
	/** Purchase Order = POO */
	public static final String DOCBASETYPE_PurchaseOrder = "POO";
	/** Purchase Requisition = POR */
	public static final String DOCBASETYPE_PurchaseRequisition = "POR";
	/** Material Physical Inventory = MMI */
	public static final String DOCBASETYPE_MaterialPhysicalInventory = "MMI";
	/** AP Credit Memo = APC */
	public static final String DOCBASETYPE_APCreditMemo = "APC";
	/** AR Credit Memo = ARC */
	public static final String DOCBASETYPE_ARCreditMemo = "ARC";
	/** Bank Statement = CMB */
	public static final String DOCBASETYPE_BankStatement = "CMB";
	/** Cash Journal = CMC */
	public static final String DOCBASETYPE_CashJournal = "CMC";
	/** Payment Allocation = CMA */
	public static final String DOCBASETYPE_PaymentAllocation = "CMA";
	/** Material Production = MMP */
	public static final String DOCBASETYPE_MaterialProduction = "MMP";
	/** Match Invoice = MXI */
	public static final String DOCBASETYPE_MatchInvoice = "MXI";
	/** Match PO = MXP */
	public static final String DOCBASETYPE_MatchPO = "MXP";
	/** Project Issue = PJI */
	public static final String DOCBASETYPE_ProjectIssue = "PJI";
	/** Maintenance Order = MOF */
	public static final String DOCBASETYPE_MaintenanceOrder = "MOF";
	/** Manufacturing Order = MOP */
	public static final String DOCBASETYPE_ManufacturingOrder = "MOP";
	/** Quality Order = MQO */
	public static final String DOCBASETYPE_QualityOrder = "MQO";
	/** Payroll = HRP */
	public static final String DOCBASETYPE_Payroll = "HRP";
	/** Distribution Order = DOO */
	public static final String DOCBASETYPE_DistributionOrder = "DOO";
	/** Manufacturing Cost Collector = MCC */
	public static final String DOCBASETYPE_ManufacturingCostCollector = "MCC";
	/** Fixed Assets Addition = FAA */
	public static final String DOCBASETYPE_FixedAssetsAddition = "FAA";
	/** Fixed Assets Disposal = FAD */
	public static final String DOCBASETYPE_FixedAssetsDisposal = "FAD";
	/** Fixed Assets Depreciation = FDP */
	public static final String DOCBASETYPE_FixedAssetsDepreciation = "FDP";
	/** JPiere Bill = JPB */
	public static final String DOCBASETYPE_JPiereBill = "JPB";
	/** JPiere Inventory Valuation Calculate = JPI */
	public static final String DOCBASETYPE_JPiereInventoryValuationCalculate = "JPI";
	/** Inventory Valuation Adjust = JPA */
	public static final String DOCBASETYPE_InventoryValuationAdjust = "JPA";
	/** JPiere Estimation = JPE */
	public static final String DOCBASETYPE_JPiereEstimation = "JPE";
	/** JPiere Payment Request = JPP */
	public static final String DOCBASETYPE_JPierePaymentRequest = "JPP";
	/** JPiere Contract Doc = JPC */
	public static final String DOCBASETYPE_JPiereContractDoc = "JPC";
	/** JPiere Contract Content = JPT */
	public static final String DOCBASETYPE_JPiereContractContent = "JPT";
	/** JPiere Revenue Recognition = JPR */
	public static final String DOCBASETYPE_JPiereRevenueRecognition = "JPR";
	/** JPiere Expense Recognition = JPX */
	public static final String DOCBASETYPE_JPiereExpenseRecognition = "JPX";
	/** JPiere Revenue Recognition(Credit Memo) = JPS */
	public static final String DOCBASETYPE_JPiereRevenueRecognitionCreditMemo = "JPS";
	/** JPiere Expense Recognition(Credit Memo) = JPY */
	public static final String DOCBASETYPE_JPiereExpenseRecognitionCreditMemo = "JPY";
	/** Set Document BaseType.
		@param DocBaseType 
		Logical type of document
	  */
	public void setDocBaseType (String DocBaseType)
	{

		set_Value (COLUMNNAME_DocBaseType, DocBaseType);
	}

	/** Get Document BaseType.
		@return Logical type of document
	  */
	public String getDocBaseType () 
	{
		return (String)get_Value(COLUMNNAME_DocBaseType);
	}

	/** Set Document No.
		@param DocumentNo 
		Document sequence number of the document
	  */
	public void setDocumentNo (String DocumentNo)
	{
		set_Value (COLUMNNAME_DocumentNo, DocumentNo);
	}

	/** Get Document No.
		@return Document sequence number of the document
	  */
	public String getDocumentNo () 
	{
		return (String)get_Value(COLUMNNAME_DocumentNo);
	}

	public org.compiere.model.I_C_BPartner getDropShip_BPartner() throws RuntimeException
    {
		return (org.compiere.model.I_C_BPartner)MTable.get(getCtx(), org.compiere.model.I_C_BPartner.Table_Name)
			.getPO(getDropShip_BPartner_ID(), get_TrxName());	}

	/** Set Drop Ship Business Partner.
		@param DropShip_BPartner_ID 
		Business Partner to ship to
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
	public int getDropShip_BPartner_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_DropShip_BPartner_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_BPartner_Location getDropShip_Location() throws RuntimeException
    {
		return (org.compiere.model.I_C_BPartner_Location)MTable.get(getCtx(), org.compiere.model.I_C_BPartner_Location.Table_Name)
			.getPO(getDropShip_Location_ID(), get_TrxName());	}

	/** Set Drop Shipment Location.
		@param DropShip_Location_ID 
		Business Partner Location for shipping to
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
	public int getDropShip_Location_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_DropShip_Location_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_AD_User getDropShip_User() throws RuntimeException
    {
		return (org.compiere.model.I_AD_User)MTable.get(getCtx(), org.compiere.model.I_AD_User.Table_Name)
			.getPO(getDropShip_User_ID(), get_TrxName());	}

	/** Set Drop Shipment Contact.
		@param DropShip_User_ID 
		Business Partner Contact for drop shipment
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
	public int getDropShip_User_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_DropShip_User_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Freight Amount.
		@param FreightAmt 
		Freight Amount 
	  */
	public void setFreightAmt (BigDecimal FreightAmt)
	{
		set_Value (COLUMNNAME_FreightAmt, FreightAmt);
	}

	/** Get Freight Amount.
		@return Freight Amount 
	  */
	public BigDecimal getFreightAmt () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_FreightAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** FreightCostRule AD_Reference_ID=153 */
	public static final int FREIGHTCOSTRULE_AD_Reference_ID=153;
	/** Freight included = I */
	public static final String FREIGHTCOSTRULE_FreightIncluded = "I";
	/** Fix price = F */
	public static final String FREIGHTCOSTRULE_FixPrice = "F";
	/** Calculated = C */
	public static final String FREIGHTCOSTRULE_Calculated = "C";
	/** Line = L */
	public static final String FREIGHTCOSTRULE_Line = "L";
	/** Set Freight Cost Rule.
		@param FreightCostRule 
		Method for charging Freight
	  */
	public void setFreightCostRule (String FreightCostRule)
	{

		set_Value (COLUMNNAME_FreightCostRule, FreightCostRule);
	}

	/** Get Freight Cost Rule.
		@return Method for charging Freight
	  */
	public String getFreightCostRule () 
	{
		return (String)get_Value(COLUMNNAME_FreightCostRule);
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

	/** Set ISO Currency Code.
		@param ISO_Code 
		Three letter ISO 4217 Code of the Currency
	  */
	public void setISO_Code (String ISO_Code)
	{
		set_Value (COLUMNNAME_ISO_Code, ISO_Code);
	}

	/** Get ISO Currency Code.
		@return Three letter ISO 4217 Code of the Currency
	  */
	public String getISO_Code () 
	{
		return (String)get_Value(COLUMNNAME_ISO_Code);
	}

	/** Set I_DataMigrationJP.
		@param I_DataMigrationJP_ID I_DataMigrationJP	  */
	public void setI_DataMigrationJP_ID (int I_DataMigrationJP_ID)
	{
		if (I_DataMigrationJP_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_I_DataMigrationJP_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_I_DataMigrationJP_ID, Integer.valueOf(I_DataMigrationJP_ID));
	}

	/** Get I_DataMigrationJP.
		@return I_DataMigrationJP	  */
	public int getI_DataMigrationJP_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_I_DataMigrationJP_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set I_DataMigrationJP_UU.
		@param I_DataMigrationJP_UU I_DataMigrationJP_UU	  */
	public void setI_DataMigrationJP_UU (String I_DataMigrationJP_UU)
	{
		set_ValueNoCheck (COLUMNNAME_I_DataMigrationJP_UU, I_DataMigrationJP_UU);
	}

	/** Get I_DataMigrationJP_UU.
		@return I_DataMigrationJP_UU	  */
	public String getI_DataMigrationJP_UU () 
	{
		return (String)get_Value(COLUMNNAME_I_DataMigrationJP_UU);
	}

	/** Set Import Error Message.
		@param I_ErrorMsg 
		Messages generated from import process
	  */
	public void setI_ErrorMsg (String I_ErrorMsg)
	{
		set_Value (COLUMNNAME_I_ErrorMsg, I_ErrorMsg);
	}

	/** Get Import Error Message.
		@return Messages generated from import process
	  */
	public String getI_ErrorMsg () 
	{
		return (String)get_Value(COLUMNNAME_I_ErrorMsg);
	}

	/** Set Imported.
		@param I_IsImported 
		Has this import been processed
	  */
	public void setI_IsImported (boolean I_IsImported)
	{
		set_Value (COLUMNNAME_I_IsImported, Boolean.valueOf(I_IsImported));
	}

	/** Get Imported.
		@return Has this import been processed
	  */
	public boolean isI_IsImported () 
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

	/** Set Interest Amount.
		@param InterestAmt 
		Interest Amount
	  */
	public void setInterestAmt (BigDecimal InterestAmt)
	{
		set_Value (COLUMNNAME_InterestAmt, InterestAmt);
	}

	/** Get Interest Amount.
		@return Interest Amount
	  */
	public BigDecimal getInterestAmt () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_InterestAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Drop Shipment.
		@param IsDropShip 
		Drop Shipments are sent from the Vendor directly to the Customer
	  */
	public void setIsDropShip (boolean IsDropShip)
	{
		set_Value (COLUMNNAME_IsDropShip, Boolean.valueOf(IsDropShip));
	}

	/** Get Drop Shipment.
		@return Drop Shipments are sent from the Vendor directly to the Customer
	  */
	public boolean isDropShip () 
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

	/** Set Sales Transaction.
		@param IsSOTrx 
		This is a Sales Transaction
	  */
	public void setIsSOTrx (boolean IsSOTrx)
	{
		set_Value (COLUMNNAME_IsSOTrx, Boolean.valueOf(IsSOTrx));
	}

	/** Get Sales Transaction.
		@return This is a Sales Transaction
	  */
	public boolean isSOTrx () 
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

	/** Set Activity(Search Key).
		@param JP_Activity_Value Activity(Search Key)	  */
	public void setJP_Activity_Value (String JP_Activity_Value)
	{
		set_Value (COLUMNNAME_JP_Activity_Value, JP_Activity_Value);
	}

	/** Get Activity(Search Key).
		@return Activity(Search Key)	  */
	public String getJP_Activity_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_Activity_Value);
	}

	/** Set Partner Location(Name).
		@param JP_BPartner_Location_Name Partner Location(Name)	  */
	public void setJP_BPartner_Location_Name (String JP_BPartner_Location_Name)
	{
		set_Value (COLUMNNAME_JP_BPartner_Location_Name, JP_BPartner_Location_Name);
	}

	/** Get Partner Location(Name).
		@return Partner Location(Name)	  */
	public String getJP_BPartner_Location_Name () 
	{
		return (String)get_Value(COLUMNNAME_JP_BPartner_Location_Name);
	}

	/** Set Business Partner(Search Key).
		@param JP_BPartner_Value Business Partner(Search Key)	  */
	public void setJP_BPartner_Value (String JP_BPartner_Value)
	{
		set_Value (COLUMNNAME_JP_BPartner_Value, JP_BPartner_Value);
	}

	/** Get Business Partner(Search Key).
		@return Business Partner(Search Key)	  */
	public String getJP_BPartner_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_BPartner_Value);
	}

	/** Set Bank Account(Search Key).
		@param JP_BankAccount_Value Bank Account(Search Key)	  */
	public void setJP_BankAccount_Value (String JP_BankAccount_Value)
	{
		set_Value (COLUMNNAME_JP_BankAccount_Value, JP_BankAccount_Value);
	}

	/** Get Bank Account(Search Key).
		@return Bank Account(Search Key)	  */
	public String getJP_BankAccount_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_BankAccount_Value);
	}

	/** Set Bank Name.
		@param JP_Bank_Name Bank Name	  */
	public void setJP_Bank_Name (String JP_Bank_Name)
	{
		set_Value (COLUMNNAME_JP_Bank_Name, JP_Bank_Name);
	}

	/** Get Bank Name.
		@return Bank Name	  */
	public String getJP_Bank_Name () 
	{
		return (String)get_Value(COLUMNNAME_JP_Bank_Name);
	}

	/** Set Campaign(Search Key).
		@param JP_Campaign_Value Campaign(Search Key)	  */
	public void setJP_Campaign_Value (String JP_Campaign_Value)
	{
		set_Value (COLUMNNAME_JP_Campaign_Value, JP_Campaign_Value);
	}

	/** Get Campaign(Search Key).
		@return Campaign(Search Key)	  */
	public String getJP_Campaign_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_Campaign_Value);
	}

	/** Set Charge(Name).
		@param JP_Charge_Name Charge(Name)	  */
	public void setJP_Charge_Name (String JP_Charge_Name)
	{
		set_Value (COLUMNNAME_JP_Charge_Name, JP_Charge_Name);
	}

	/** Get Charge(Name).
		@return Charge(Name)	  */
	public String getJP_Charge_Name () 
	{
		return (String)get_Value(COLUMNNAME_JP_Charge_Name);
	}

	public I_JP_DataMigrationLine getJP_DataMigrationLine() throws RuntimeException
    {
		return (I_JP_DataMigrationLine)MTable.get(getCtx(), I_JP_DataMigrationLine.Table_Name)
			.getPO(getJP_DataMigrationLine_ID(), get_TrxName());	}

	/** Set JP_DataMigrationLine.
		@param JP_DataMigrationLine_ID JP_DataMigrationLine	  */
	public void setJP_DataMigrationLine_ID (int JP_DataMigrationLine_ID)
	{
		if (JP_DataMigrationLine_ID < 1) 
			set_Value (COLUMNNAME_JP_DataMigrationLine_ID, null);
		else 
			set_Value (COLUMNNAME_JP_DataMigrationLine_ID, Integer.valueOf(JP_DataMigrationLine_ID));
	}

	/** Get JP_DataMigrationLine.
		@return JP_DataMigrationLine	  */
	public int getJP_DataMigrationLine_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_DataMigrationLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** JP_DataMigration_DocStatus AD_Reference_ID=131 */
	public static final int JP_DATAMIGRATION_DOCSTATUS_AD_Reference_ID=131;
	/** Drafted = DR */
	public static final String JP_DATAMIGRATION_DOCSTATUS_Drafted = "DR";
	/** Completed = CO */
	public static final String JP_DATAMIGRATION_DOCSTATUS_Completed = "CO";
	/** Approved = AP */
	public static final String JP_DATAMIGRATION_DOCSTATUS_Approved = "AP";
	/** Not Approved = NA */
	public static final String JP_DATAMIGRATION_DOCSTATUS_NotApproved = "NA";
	/** Voided = VO */
	public static final String JP_DATAMIGRATION_DOCSTATUS_Voided = "VO";
	/** Invalid = IN */
	public static final String JP_DATAMIGRATION_DOCSTATUS_Invalid = "IN";
	/** Reversed = RE */
	public static final String JP_DATAMIGRATION_DOCSTATUS_Reversed = "RE";
	/** Closed = CL */
	public static final String JP_DATAMIGRATION_DOCSTATUS_Closed = "CL";
	/** Unknown = ?? */
	public static final String JP_DATAMIGRATION_DOCSTATUS_Unknown = "??";
	/** In Progress = IP */
	public static final String JP_DATAMIGRATION_DOCSTATUS_InProgress = "IP";
	/** Waiting Payment = WP */
	public static final String JP_DATAMIGRATION_DOCSTATUS_WaitingPayment = "WP";
	/** Waiting Confirmation = WC */
	public static final String JP_DATAMIGRATION_DOCSTATUS_WaitingConfirmation = "WC";
	/** Set Document Status.
		@param JP_DataMigration_DocStatus Document Status	  */
	public void setJP_DataMigration_DocStatus (String JP_DataMigration_DocStatus)
	{

		set_Value (COLUMNNAME_JP_DataMigration_DocStatus, JP_DataMigration_DocStatus);
	}

	/** Get Document Status.
		@return Document Status	  */
	public String getJP_DataMigration_DocStatus () 
	{
		return (String)get_Value(COLUMNNAME_JP_DataMigration_DocStatus);
	}

	public I_JP_DataMigration getJP_DataMigration() throws RuntimeException
    {
		return (I_JP_DataMigration)MTable.get(getCtx(), I_JP_DataMigration.Table_Name)
			.getPO(getJP_DataMigration_ID(), get_TrxName());	}

	/** Set JP_DataMigration.
		@param JP_DataMigration_ID JP_DataMigration	  */
	public void setJP_DataMigration_ID (int JP_DataMigration_ID)
	{
		if (JP_DataMigration_ID < 1) 
			set_Value (COLUMNNAME_JP_DataMigration_ID, null);
		else 
			set_Value (COLUMNNAME_JP_DataMigration_ID, Integer.valueOf(JP_DataMigration_ID));
	}

	/** Get JP_DataMigration.
		@return JP_DataMigration	  */
	public int getJP_DataMigration_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_DataMigration_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Data Migration Identifier.
		@param JP_DataMigration_Identifier Data Migration Identifier	  */
	public void setJP_DataMigration_Identifier (String JP_DataMigration_Identifier)
	{
		set_Value (COLUMNNAME_JP_DataMigration_Identifier, JP_DataMigration_Identifier);
	}

	/** Get Data Migration Identifier.
		@return Data Migration Identifier	  */
	public String getJP_DataMigration_Identifier () 
	{
		return (String)get_Value(COLUMNNAME_JP_DataMigration_Identifier);
	}

	/** Set Document Type(Name).
		@param JP_DocType_Name Document Type(Name)	  */
	public void setJP_DocType_Name (String JP_DocType_Name)
	{
		set_Value (COLUMNNAME_JP_DocType_Name, JP_DocType_Name);
	}

	/** Get Document Type(Name).
		@return Document Type(Name)	  */
	public String getJP_DocType_Name () 
	{
		return (String)get_Value(COLUMNNAME_JP_DocType_Name);
	}

	/** Set Drop Ship BP Location(Name).
		@param JP_DropShip_BP_Location_Name Drop Ship BP Location(Name)	  */
	public void setJP_DropShip_BP_Location_Name (String JP_DropShip_BP_Location_Name)
	{
		set_Value (COLUMNNAME_JP_DropShip_BP_Location_Name, JP_DropShip_BP_Location_Name);
	}

	/** Get Drop Ship BP Location(Name).
		@return Drop Ship BP Location(Name)	  */
	public String getJP_DropShip_BP_Location_Name () 
	{
		return (String)get_Value(COLUMNNAME_JP_DropShip_BP_Location_Name);
	}

	/** Set Drop Ship BP(Search Key).
		@param JP_DropShip_BP_Value Drop Ship BP(Search Key)	  */
	public void setJP_DropShip_BP_Value (String JP_DropShip_BP_Value)
	{
		set_Value (COLUMNNAME_JP_DropShip_BP_Value, JP_DropShip_BP_Value);
	}

	/** Get Drop Ship BP(Search Key).
		@return Drop Ship BP(Search Key)	  */
	public String getJP_DropShip_BP_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_DropShip_BP_Value);
	}

	/** Set Drop Shipment Contact(E-Mail).
		@param JP_DropShip_User_EMail Drop Shipment Contact(E-Mail)	  */
	public void setJP_DropShip_User_EMail (String JP_DropShip_User_EMail)
	{
		set_Value (COLUMNNAME_JP_DropShip_User_EMail, JP_DropShip_User_EMail);
	}

	/** Get Drop Shipment Contact(E-Mail).
		@return Drop Shipment Contact(E-Mail)	  */
	public String getJP_DropShip_User_EMail () 
	{
		return (String)get_Value(COLUMNNAME_JP_DropShip_User_EMail);
	}

	/** Set Drop Shipment Contact(Name).
		@param JP_DropShip_User_Name Drop Shipment Contact(Name)	  */
	public void setJP_DropShip_User_Name (String JP_DropShip_User_Name)
	{
		set_Value (COLUMNNAME_JP_DropShip_User_Name, JP_DropShip_User_Name);
	}

	/** Get Drop Shipment Contact(Name).
		@return Drop Shipment Contact(Name)	  */
	public String getJP_DropShip_User_Name () 
	{
		return (String)get_Value(COLUMNNAME_JP_DropShip_User_Name);
	}

	/** Set Drop Shipment Contact(Search Key).
		@param JP_DropShip_User_Value Drop Shipment Contact(Search Key)	  */
	public void setJP_DropShip_User_Value (String JP_DropShip_User_Value)
	{
		set_Value (COLUMNNAME_JP_DropShip_User_Value, JP_DropShip_User_Value);
	}

	/** Get Drop Shipment Contact(Search Key).
		@return Drop Shipment Contact(Search Key)	  */
	public String getJP_DropShip_User_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_DropShip_User_Value);
	}

	/** Set Invoice Document No.
		@param JP_Invoice_DocumentNo Invoice Document No	  */
	public void setJP_Invoice_DocumentNo (String JP_Invoice_DocumentNo)
	{
		set_Value (COLUMNNAME_JP_Invoice_DocumentNo, JP_Invoice_DocumentNo);
	}

	/** Get Invoice Document No.
		@return Invoice Document No	  */
	public String getJP_Invoice_DocumentNo () 
	{
		return (String)get_Value(COLUMNNAME_JP_Invoice_DocumentNo);
	}

	public org.compiere.model.I_C_Activity getJP_Line_Activity() throws RuntimeException
    {
		return (org.compiere.model.I_C_Activity)MTable.get(getCtx(), org.compiere.model.I_C_Activity.Table_Name)
			.getPO(getJP_Line_Activity_ID(), get_TrxName());	}

	/** Set Activity of Line.
		@param JP_Line_Activity_ID Activity of Line	  */
	public void setJP_Line_Activity_ID (int JP_Line_Activity_ID)
	{
		if (JP_Line_Activity_ID < 1) 
			set_Value (COLUMNNAME_JP_Line_Activity_ID, null);
		else 
			set_Value (COLUMNNAME_JP_Line_Activity_ID, Integer.valueOf(JP_Line_Activity_ID));
	}

	/** Get Activity of Line.
		@return Activity of Line	  */
	public int getJP_Line_Activity_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Line_Activity_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Activity of Line(Search Key).
		@param JP_Line_Activity_Value Activity of Line(Search Key)	  */
	public void setJP_Line_Activity_Value (String JP_Line_Activity_Value)
	{
		set_Value (COLUMNNAME_JP_Line_Activity_Value, JP_Line_Activity_Value);
	}

	/** Get Activity of Line(Search Key).
		@return Activity of Line(Search Key)	  */
	public String getJP_Line_Activity_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_Line_Activity_Value);
	}

	public org.compiere.model.I_C_BPartner getJP_Line_BPartner() throws RuntimeException
    {
		return (org.compiere.model.I_C_BPartner)MTable.get(getCtx(), org.compiere.model.I_C_BPartner.Table_Name)
			.getPO(getJP_Line_BPartner_ID(), get_TrxName());	}

	/** Set Business Partner of Line.
		@param JP_Line_BPartner_ID Business Partner of Line	  */
	public void setJP_Line_BPartner_ID (int JP_Line_BPartner_ID)
	{
		if (JP_Line_BPartner_ID < 1) 
			set_Value (COLUMNNAME_JP_Line_BPartner_ID, null);
		else 
			set_Value (COLUMNNAME_JP_Line_BPartner_ID, Integer.valueOf(JP_Line_BPartner_ID));
	}

	/** Get Business Partner of Line.
		@return Business Partner of Line	  */
	public int getJP_Line_BPartner_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Line_BPartner_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Business Partner of Line(Search Key).
		@param JP_Line_BPartner_Value Business Partner of Line(Search Key)	  */
	public void setJP_Line_BPartner_Value (String JP_Line_BPartner_Value)
	{
		set_Value (COLUMNNAME_JP_Line_BPartner_Value, JP_Line_BPartner_Value);
	}

	/** Get Business Partner of Line(Search Key).
		@return Business Partner of Line(Search Key)	  */
	public String getJP_Line_BPartner_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_Line_BPartner_Value);
	}

	public org.compiere.model.I_C_Campaign getJP_Line_Campaign() throws RuntimeException
    {
		return (org.compiere.model.I_C_Campaign)MTable.get(getCtx(), org.compiere.model.I_C_Campaign.Table_Name)
			.getPO(getJP_Line_Campaign_ID(), get_TrxName());	}

	/** Set Campaign of Line.
		@param JP_Line_Campaign_ID Campaign of Line	  */
	public void setJP_Line_Campaign_ID (int JP_Line_Campaign_ID)
	{
		if (JP_Line_Campaign_ID < 1) 
			set_Value (COLUMNNAME_JP_Line_Campaign_ID, null);
		else 
			set_Value (COLUMNNAME_JP_Line_Campaign_ID, Integer.valueOf(JP_Line_Campaign_ID));
	}

	/** Get Campaign of Line.
		@return Campaign of Line	  */
	public int getJP_Line_Campaign_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Line_Campaign_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Campaign of Line(Search Key).
		@param JP_Line_Campaign_Value Campaign of Line(Search Key)	  */
	public void setJP_Line_Campaign_Value (String JP_Line_Campaign_Value)
	{
		set_Value (COLUMNNAME_JP_Line_Campaign_Value, JP_Line_Campaign_Value);
	}

	/** Get Campaign of Line(Search Key).
		@return Campaign of Line(Search Key)	  */
	public String getJP_Line_Campaign_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_Line_Campaign_Value);
	}

	/** Set Trx Org of Line.
		@param JP_Line_OrgTrx_ID Trx Org of Line	  */
	public void setJP_Line_OrgTrx_ID (int JP_Line_OrgTrx_ID)
	{
		if (JP_Line_OrgTrx_ID < 1) 
			set_Value (COLUMNNAME_JP_Line_OrgTrx_ID, null);
		else 
			set_Value (COLUMNNAME_JP_Line_OrgTrx_ID, Integer.valueOf(JP_Line_OrgTrx_ID));
	}

	/** Get Trx Org of Line.
		@return Trx Org of Line	  */
	public int getJP_Line_OrgTrx_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Line_OrgTrx_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Trx Org of Line(Search Key).
		@param JP_Line_OrgTrx_Value Trx Org of Line(Search Key)	  */
	public void setJP_Line_OrgTrx_Value (String JP_Line_OrgTrx_Value)
	{
		set_Value (COLUMNNAME_JP_Line_OrgTrx_Value, JP_Line_OrgTrx_Value);
	}

	/** Get Trx Org of Line(Search Key).
		@return Trx Org of Line(Search Key)	  */
	public String getJP_Line_OrgTrx_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_Line_OrgTrx_Value);
	}

	public org.compiere.model.I_C_Project getJP_Line_Project() throws RuntimeException
    {
		return (org.compiere.model.I_C_Project)MTable.get(getCtx(), org.compiere.model.I_C_Project.Table_Name)
			.getPO(getJP_Line_Project_ID(), get_TrxName());	}

	/** Set Project of Line.
		@param JP_Line_Project_ID Project of Line	  */
	public void setJP_Line_Project_ID (int JP_Line_Project_ID)
	{
		if (JP_Line_Project_ID < 1) 
			set_Value (COLUMNNAME_JP_Line_Project_ID, null);
		else 
			set_Value (COLUMNNAME_JP_Line_Project_ID, Integer.valueOf(JP_Line_Project_ID));
	}

	/** Get Project of Line.
		@return Project of Line	  */
	public int getJP_Line_Project_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Line_Project_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Project of Line(Search Key).
		@param JP_Line_Project_Value Project of Line(Search Key)	  */
	public void setJP_Line_Project_Value (String JP_Line_Project_Value)
	{
		set_Value (COLUMNNAME_JP_Line_Project_Value, JP_Line_Project_Value);
	}

	/** Get Project of Line(Search Key).
		@return Project of Line(Search Key)	  */
	public String getJP_Line_Project_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_Line_Project_Value);
	}

	public org.compiere.model.I_C_ElementValue getJP_Line_User1() throws RuntimeException
    {
		return (org.compiere.model.I_C_ElementValue)MTable.get(getCtx(), org.compiere.model.I_C_ElementValue.Table_Name)
			.getPO(getJP_Line_User1_ID(), get_TrxName());	}

	/** Set User Element List 1 of Line.
		@param JP_Line_User1_ID 
		User defined list element #1
	  */
	public void setJP_Line_User1_ID (int JP_Line_User1_ID)
	{
		if (JP_Line_User1_ID < 1) 
			set_Value (COLUMNNAME_JP_Line_User1_ID, null);
		else 
			set_Value (COLUMNNAME_JP_Line_User1_ID, Integer.valueOf(JP_Line_User1_ID));
	}

	/** Get User Element List 1 of Line.
		@return User defined list element #1
	  */
	public int getJP_Line_User1_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Line_User1_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_ElementValue getJP_Line_User2() throws RuntimeException
    {
		return (org.compiere.model.I_C_ElementValue)MTable.get(getCtx(), org.compiere.model.I_C_ElementValue.Table_Name)
			.getPO(getJP_Line_User2_ID(), get_TrxName());	}

	/** Set User Element List 2 of Line.
		@param JP_Line_User2_ID 
		User defined list element #2
	  */
	public void setJP_Line_User2_ID (int JP_Line_User2_ID)
	{
		if (JP_Line_User2_ID < 1) 
			set_Value (COLUMNNAME_JP_Line_User2_ID, null);
		else 
			set_Value (COLUMNNAME_JP_Line_User2_ID, Integer.valueOf(JP_Line_User2_ID));
	}

	/** Get User Element List 2 of Line.
		@return User defined list element #2
	  */
	public int getJP_Line_User2_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Line_User2_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set User Element List 1 of Line(Search key).
		@param JP_Line_UserElement1_Value User Element List 1 of Line(Search key)	  */
	public void setJP_Line_UserElement1_Value (String JP_Line_UserElement1_Value)
	{
		set_Value (COLUMNNAME_JP_Line_UserElement1_Value, JP_Line_UserElement1_Value);
	}

	/** Get User Element List 1 of Line(Search key).
		@return User Element List 1 of Line(Search key)	  */
	public String getJP_Line_UserElement1_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_Line_UserElement1_Value);
	}

	/** Set User Element List 2 of Line(Search key).
		@param JP_Line_UserElement2_Value User Element List 2 of Line(Search key)	  */
	public void setJP_Line_UserElement2_Value (String JP_Line_UserElement2_Value)
	{
		set_Value (COLUMNNAME_JP_Line_UserElement2_Value, JP_Line_UserElement2_Value);
	}

	/** Get User Element List 2 of Line(Search key).
		@return User Element List 2 of Line(Search key)	  */
	public String getJP_Line_UserElement2_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_Line_UserElement2_Value);
	}

	/** Set Linked Order Document No.
		@param JP_Link_Order_DocumentNo Linked Order Document No	  */
	public void setJP_Link_Order_DocumentNo (String JP_Link_Order_DocumentNo)
	{
		set_Value (COLUMNNAME_JP_Link_Order_DocumentNo, JP_Link_Order_DocumentNo);
	}

	/** Get Linked Order Document No.
		@return Linked Order Document No	  */
	public String getJP_Link_Order_DocumentNo () 
	{
		return (String)get_Value(COLUMNNAME_JP_Link_Order_DocumentNo);
	}

	public org.compiere.model.I_M_Locator getJP_LocatorFrom() throws RuntimeException
    {
		return (org.compiere.model.I_M_Locator)MTable.get(getCtx(), org.compiere.model.I_M_Locator.Table_Name)
			.getPO(getJP_LocatorFrom_ID(), get_TrxName());	}

	/** Set Locator(From).
		@param JP_LocatorFrom_ID Locator(From)	  */
	public void setJP_LocatorFrom_ID (int JP_LocatorFrom_ID)
	{
		if (JP_LocatorFrom_ID < 1) 
			set_Value (COLUMNNAME_JP_LocatorFrom_ID, null);
		else 
			set_Value (COLUMNNAME_JP_LocatorFrom_ID, Integer.valueOf(JP_LocatorFrom_ID));
	}

	/** Get Locator(From).
		@return Locator(From)	  */
	public int getJP_LocatorFrom_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_LocatorFrom_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Locator(From)(Search Key).
		@param JP_LocatorFrom_Value Locator(From)(Search Key)	  */
	public void setJP_LocatorFrom_Value (String JP_LocatorFrom_Value)
	{
		set_Value (COLUMNNAME_JP_LocatorFrom_Value, JP_LocatorFrom_Value);
	}

	/** Get Locator(From)(Search Key).
		@return Locator(From)(Search Key)	  */
	public String getJP_LocatorFrom_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_LocatorFrom_Value);
	}

	public org.compiere.model.I_M_Locator getJP_LocatorTo() throws RuntimeException
    {
		return (org.compiere.model.I_M_Locator)MTable.get(getCtx(), org.compiere.model.I_M_Locator.Table_Name)
			.getPO(getJP_LocatorTo_ID(), get_TrxName());	}

	/** Set Locator(To).
		@param JP_LocatorTo_ID Locator(To)	  */
	public void setJP_LocatorTo_ID (int JP_LocatorTo_ID)
	{
		if (JP_LocatorTo_ID < 1) 
			set_Value (COLUMNNAME_JP_LocatorTo_ID, null);
		else 
			set_Value (COLUMNNAME_JP_LocatorTo_ID, Integer.valueOf(JP_LocatorTo_ID));
	}

	/** Get Locator(To).
		@return Locator(To)	  */
	public int getJP_LocatorTo_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_LocatorTo_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Locator(To)(Search Key).
		@param JP_LocatorTo_Value Locator(To)(Search Key)	  */
	public void setJP_LocatorTo_Value (String JP_LocatorTo_Value)
	{
		set_Value (COLUMNNAME_JP_LocatorTo_Value, JP_LocatorTo_Value);
	}

	/** Get Locator(To)(Search Key).
		@return Locator(To)(Search Key)	  */
	public String getJP_LocatorTo_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_LocatorTo_Value);
	}

	/** Set Locator(Search Key).
		@param JP_Locator_Value 
		Warehouse Locator
	  */
	public void setJP_Locator_Value (String JP_Locator_Value)
	{
		set_Value (COLUMNNAME_JP_Locator_Value, JP_Locator_Value);
	}

	/** Get Locator(Search Key).
		@return Warehouse Locator
	  */
	public String getJP_Locator_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_Locator_Value);
	}

	/** Set JP_Order_DocumentNo.
		@param JP_Order_DocumentNo JP_Order_DocumentNo	  */
	public void setJP_Order_DocumentNo (String JP_Order_DocumentNo)
	{
		set_Value (COLUMNNAME_JP_Order_DocumentNo, JP_Order_DocumentNo);
	}

	/** Get JP_Order_DocumentNo.
		@return JP_Order_DocumentNo	  */
	public String getJP_Order_DocumentNo () 
	{
		return (String)get_Value(COLUMNNAME_JP_Order_DocumentNo);
	}

	/** Set Trx Organization(Search Key).
		@param JP_OrgTrx_Value Trx Organization(Search Key)	  */
	public void setJP_OrgTrx_Value (String JP_OrgTrx_Value)
	{
		set_Value (COLUMNNAME_JP_OrgTrx_Value, JP_OrgTrx_Value);
	}

	/** Get Trx Organization(Search Key).
		@return Trx Organization(Search Key)	  */
	public String getJP_OrgTrx_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_OrgTrx_Value);
	}

	/** Set Organization(Search Key).
		@param JP_Org_Value Organization(Search Key)	  */
	public void setJP_Org_Value (String JP_Org_Value)
	{
		set_Value (COLUMNNAME_JP_Org_Value, JP_Org_Value);
	}

	/** Get Organization(Search Key).
		@return Organization(Search Key)	  */
	public String getJP_Org_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_Org_Value);
	}

	/** Set Payment Term(Search Key).
		@param JP_PaymentTerm_Value 
		The terms of Payment (timing, discount)
	  */
	public void setJP_PaymentTerm_Value (String JP_PaymentTerm_Value)
	{
		set_Value (COLUMNNAME_JP_PaymentTerm_Value, JP_PaymentTerm_Value);
	}

	/** Get Payment Term(Search Key).
		@return The terms of Payment (timing, discount)
	  */
	public String getJP_PaymentTerm_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_PaymentTerm_Value);
	}

	/** Set Payment Document No.
		@param JP_Payment_DocumentNo Payment Document No	  */
	public void setJP_Payment_DocumentNo (String JP_Payment_DocumentNo)
	{
		set_Value (COLUMNNAME_JP_Payment_DocumentNo, JP_Payment_DocumentNo);
	}

	/** Get Payment Document No.
		@return Payment Document No	  */
	public String getJP_Payment_DocumentNo () 
	{
		return (String)get_Value(COLUMNNAME_JP_Payment_DocumentNo);
	}

	public I_JP_PhysicalWarehouse getJP_PhysicalWarehouse() throws RuntimeException
    {
		return (I_JP_PhysicalWarehouse)MTable.get(getCtx(), I_JP_PhysicalWarehouse.Table_Name)
			.getPO(getJP_PhysicalWarehouse_ID(), get_TrxName());	}

	/** Set Physical Warehouse.
		@param JP_PhysicalWarehouse_ID Physical Warehouse	  */
	public void setJP_PhysicalWarehouse_ID (int JP_PhysicalWarehouse_ID)
	{
		if (JP_PhysicalWarehouse_ID < 1) 
			set_Value (COLUMNNAME_JP_PhysicalWarehouse_ID, null);
		else 
			set_Value (COLUMNNAME_JP_PhysicalWarehouse_ID, Integer.valueOf(JP_PhysicalWarehouse_ID));
	}

	/** Get Physical Warehouse.
		@return Physical Warehouse	  */
	public int getJP_PhysicalWarehouse_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_PhysicalWarehouse_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Physical Warehouse(Search Key).
		@param JP_PhysicalWarehouse_Value Physical Warehouse(Search Key)	  */
	public void setJP_PhysicalWarehouse_Value (String JP_PhysicalWarehouse_Value)
	{
		set_Value (COLUMNNAME_JP_PhysicalWarehouse_Value, JP_PhysicalWarehouse_Value);
	}

	/** Get Physical Warehouse(Search Key).
		@return Physical Warehouse(Search Key)	  */
	public String getJP_PhysicalWarehouse_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_PhysicalWarehouse_Value);
	}

	/** Set Price List(Name).
		@param JP_PriceList_Name Price List(Name)	  */
	public void setJP_PriceList_Name (String JP_PriceList_Name)
	{
		set_Value (COLUMNNAME_JP_PriceList_Name, JP_PriceList_Name);
	}

	/** Get Price List(Name).
		@return Price List(Name)	  */
	public String getJP_PriceList_Name () 
	{
		return (String)get_Value(COLUMNNAME_JP_PriceList_Name);
	}

	/** Set Product(Search Key).
		@param JP_Product_Value Product(Search Key)	  */
	public void setJP_Product_Value (String JP_Product_Value)
	{
		set_Value (COLUMNNAME_JP_Product_Value, JP_Product_Value);
	}

	/** Get Product(Search Key).
		@return Product(Search Key)	  */
	public String getJP_Product_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_Product_Value);
	}

	/** Set Project(Search Key).
		@param JP_Project_Value Project(Search Key)	  */
	public void setJP_Project_Value (String JP_Project_Value)
	{
		set_Value (COLUMNNAME_JP_Project_Value, JP_Project_Value);
	}

	/** Get Project(Search Key).
		@return Project(Search Key)	  */
	public String getJP_Project_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_Project_Value);
	}

	/** Set Ref Order Document No.
		@param JP_Ref_Order_DocumentNo Ref Order Document No	  */
	public void setJP_Ref_Order_DocumentNo (String JP_Ref_Order_DocumentNo)
	{
		set_Value (COLUMNNAME_JP_Ref_Order_DocumentNo, JP_Ref_Order_DocumentNo);
	}

	/** Get Ref Order Document No.
		@return Ref Order Document No	  */
	public String getJP_Ref_Order_DocumentNo () 
	{
		return (String)get_Value(COLUMNNAME_JP_Ref_Order_DocumentNo);
	}

	/** Set Sales Rep(E-Mail).
		@param JP_SalesRep_EMail Sales Rep(E-Mail)	  */
	public void setJP_SalesRep_EMail (String JP_SalesRep_EMail)
	{
		set_Value (COLUMNNAME_JP_SalesRep_EMail, JP_SalesRep_EMail);
	}

	/** Get Sales Rep(E-Mail).
		@return Sales Rep(E-Mail)	  */
	public String getJP_SalesRep_EMail () 
	{
		return (String)get_Value(COLUMNNAME_JP_SalesRep_EMail);
	}

	/** Set Sales Rep(Name).
		@param JP_SalesRep_Name Sales Rep(Name)	  */
	public void setJP_SalesRep_Name (String JP_SalesRep_Name)
	{
		set_Value (COLUMNNAME_JP_SalesRep_Name, JP_SalesRep_Name);
	}

	/** Get Sales Rep(Name).
		@return Sales Rep(Name)	  */
	public String getJP_SalesRep_Name () 
	{
		return (String)get_Value(COLUMNNAME_JP_SalesRep_Name);
	}

	/** Set Sales Rep(Search Key).
		@param JP_SalesRep_Value Sales Rep(Search Key)	  */
	public void setJP_SalesRep_Value (String JP_SalesRep_Value)
	{
		set_Value (COLUMNNAME_JP_SalesRep_Value, JP_SalesRep_Value);
	}

	/** Get Sales Rep(Search Key).
		@return Sales Rep(Search Key)	  */
	public String getJP_SalesRep_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_SalesRep_Value);
	}

	/** Set Shipper(Name).
		@param JP_Shipper_Name 
		Method or manner of product delivery
	  */
	public void setJP_Shipper_Name (String JP_Shipper_Name)
	{
		set_Value (COLUMNNAME_JP_Shipper_Name, JP_Shipper_Name);
	}

	/** Get Shipper(Name).
		@return Method or manner of product delivery
	  */
	public String getJP_Shipper_Name () 
	{
		return (String)get_Value(COLUMNNAME_JP_Shipper_Name);
	}

	/** Set User Element List 1(Search key).
		@param JP_UserElement1_Value User Element List 1(Search key)	  */
	public void setJP_UserElement1_Value (String JP_UserElement1_Value)
	{
		set_Value (COLUMNNAME_JP_UserElement1_Value, JP_UserElement1_Value);
	}

	/** Get User Element List 1(Search key).
		@return User Element List 1(Search key)	  */
	public String getJP_UserElement1_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_UserElement1_Value);
	}

	/** Set User Element List 2(Search key).
		@param JP_UserElement2_Value User Element List 2(Search key)	  */
	public void setJP_UserElement2_Value (String JP_UserElement2_Value)
	{
		set_Value (COLUMNNAME_JP_UserElement2_Value, JP_UserElement2_Value);
	}

	/** Get User Element List 2(Search key).
		@return User Element List 2(Search key)	  */
	public String getJP_UserElement2_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_UserElement2_Value);
	}

	/** Set User(E-Mail).
		@param JP_User_EMail User(E-Mail)	  */
	public void setJP_User_EMail (String JP_User_EMail)
	{
		set_Value (COLUMNNAME_JP_User_EMail, JP_User_EMail);
	}

	/** Get User(E-Mail).
		@return User(E-Mail)	  */
	public String getJP_User_EMail () 
	{
		return (String)get_Value(COLUMNNAME_JP_User_EMail);
	}

	/** Set User(Name).
		@param JP_User_Name User(Name)	  */
	public void setJP_User_Name (String JP_User_Name)
	{
		set_Value (COLUMNNAME_JP_User_Name, JP_User_Name);
	}

	/** Get User(Name).
		@return User(Name)	  */
	public String getJP_User_Name () 
	{
		return (String)get_Value(COLUMNNAME_JP_User_Name);
	}

	/** Set User(Search Key).
		@param JP_User_Value User(Search Key)	  */
	public void setJP_User_Value (String JP_User_Value)
	{
		set_Value (COLUMNNAME_JP_User_Value, JP_User_Value);
	}

	/** Get User(Search Key).
		@return User(Search Key)	  */
	public String getJP_User_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_User_Value);
	}

	/** Set Org Warehouse(Search Key).
		@param JP_Warehouse_Value Org Warehouse(Search Key)	  */
	public void setJP_Warehouse_Value (String JP_Warehouse_Value)
	{
		set_Value (COLUMNNAME_JP_Warehouse_Value, JP_Warehouse_Value);
	}

	/** Get Org Warehouse(Search Key).
		@return Org Warehouse(Search Key)	  */
	public String getJP_Warehouse_Value () 
	{
		return (String)get_Value(COLUMNNAME_JP_Warehouse_Value);
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

	/** Set Line Description.
		@param LineDescription 
		Description of the Line
	  */
	public void setLineDescription (String LineDescription)
	{
		set_Value (COLUMNNAME_LineDescription, LineDescription);
	}

	/** Get Line Description.
		@return Description of the Line
	  */
	public String getLineDescription () 
	{
		return (String)get_Value(COLUMNNAME_LineDescription);
	}

	/** Set Line Amount.
		@param LineNetAmt 
		Line Extended Amount (Quantity * Actual Price) without Freight and Charges
	  */
	public void setLineNetAmt (BigDecimal LineNetAmt)
	{
		set_Value (COLUMNNAME_LineNetAmt, LineNetAmt);
	}

	/** Get Line Amount.
		@return Line Extended Amount (Quantity * Actual Price) without Freight and Charges
	  */
	public BigDecimal getLineNetAmt () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_LineNetAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	public org.compiere.model.I_M_FreightCategory getM_FreightCategory() throws RuntimeException
    {
		return (org.compiere.model.I_M_FreightCategory)MTable.get(getCtx(), org.compiere.model.I_M_FreightCategory.Table_Name)
			.getPO(getM_FreightCategory_ID(), get_TrxName());	}

	/** Set Freight Category.
		@param M_FreightCategory_ID 
		Category of the Freight
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
	public int getM_FreightCategory_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_FreightCategory_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_Locator getM_Locator() throws RuntimeException
    {
		return (org.compiere.model.I_M_Locator)MTable.get(getCtx(), org.compiere.model.I_M_Locator.Table_Name)
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

	public org.compiere.model.I_M_Shipper getM_Shipper() throws RuntimeException
    {
		return (org.compiere.model.I_M_Shipper)MTable.get(getCtx(), org.compiere.model.I_M_Shipper.Table_Name)
			.getPO(getM_Shipper_ID(), get_TrxName());	}

	/** Set Shipper.
		@param M_Shipper_ID 
		Method or manner of product delivery
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
	public int getM_Shipper_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Shipper_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_Warehouse getM_Warehouse() throws RuntimeException
    {
		return (org.compiere.model.I_M_Warehouse)MTable.get(getCtx(), org.compiere.model.I_M_Warehouse.Table_Name)
			.getPO(getM_Warehouse_ID(), get_TrxName());	}

	/** Set Org Warehouse.
		@param M_Warehouse_ID 
		Storage Warehouse and Service Point
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
	public int getM_Warehouse_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Warehouse_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Name.
		@param Name 
		Alphanumeric identifier of the entity
	  */
	public void setName (String Name)
	{
		set_Value (COLUMNNAME_Name, Name);
	}

	/** Get Name.
		@return Alphanumeric identifier of the entity
	  */
	public String getName () 
	{
		return (String)get_Value(COLUMNNAME_Name);
	}

	/** Set Over/Under Payment.
		@param OverUnderAmt 
		Over-Payment (unallocated) or Under-Payment (partial payment) Amount
	  */
	public void setOverUnderAmt (BigDecimal OverUnderAmt)
	{
		set_Value (COLUMNNAME_OverUnderAmt, OverUnderAmt);
	}

	/** Get Over/Under Payment.
		@return Over-Payment (unallocated) or Under-Payment (partial payment) Amount
	  */
	public BigDecimal getOverUnderAmt () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_OverUnderAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Order Reference.
		@param POReference 
		Transaction Reference Number (Sales Order, Purchase Order) of your Business Partner
	  */
	public void setPOReference (String POReference)
	{
		set_Value (COLUMNNAME_POReference, POReference);
	}

	/** Get Order Reference.
		@return Transaction Reference Number (Sales Order, Purchase Order) of your Business Partner
	  */
	public String getPOReference () 
	{
		return (String)get_Value(COLUMNNAME_POReference);
	}

	/** Set Payment amount.
		@param PayAmt 
		Amount being paid
	  */
	public void setPayAmt (BigDecimal PayAmt)
	{
		set_Value (COLUMNNAME_PayAmt, PayAmt);
	}

	/** Get Payment amount.
		@return Amount being paid
	  */
	public BigDecimal getPayAmt () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_PayAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
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

	/** Set Price.
		@param Price 
		Price
	  */
	public void setPrice (BigDecimal Price)
	{
		set_Value (COLUMNNAME_Price, Price);
	}

	/** Get Price.
		@return Price
	  */
	public BigDecimal getPrice () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_Price);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** PriorityRule AD_Reference_ID=154 */
	public static final int PRIORITYRULE_AD_Reference_ID=154;
	/** High = 3 */
	public static final String PRIORITYRULE_High = "3";
	/** Medium = 5 */
	public static final String PRIORITYRULE_Medium = "5";
	/** Low = 7 */
	public static final String PRIORITYRULE_Low = "7";
	/** Urgent = 1 */
	public static final String PRIORITYRULE_Urgent = "1";
	/** Minor = 9 */
	public static final String PRIORITYRULE_Minor = "9";
	/** Set Priority.
		@param PriorityRule 
		Priority of a document
	  */
	public void setPriorityRule (String PriorityRule)
	{

		set_Value (COLUMNNAME_PriorityRule, PriorityRule);
	}

	/** Get Priority.
		@return Priority of a document
	  */
	public String getPriorityRule () 
	{
		return (String)get_Value(COLUMNNAME_PriorityRule);
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
		@param Qty 
		Quantity
	  */
	public void setQty (BigDecimal Qty)
	{
		set_Value (COLUMNNAME_Qty, Qty);
	}

	/** Get Quantity.
		@return Quantity
	  */
	public BigDecimal getQty () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_Qty);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Quantity book.
		@param QtyBook 
		Book Quantity
	  */
	public void setQtyBook (BigDecimal QtyBook)
	{
		set_Value (COLUMNNAME_QtyBook, QtyBook);
	}

	/** Get Quantity book.
		@return Book Quantity
	  */
	public BigDecimal getQtyBook () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_QtyBook);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Quantity count.
		@param QtyCount 
		Counted Quantity
	  */
	public void setQtyCount (BigDecimal QtyCount)
	{
		set_Value (COLUMNNAME_QtyCount, QtyCount);
	}

	/** Get Quantity count.
		@return Counted Quantity
	  */
	public BigDecimal getQtyCount () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_QtyCount);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	public org.compiere.model.I_AD_User getSalesRep() throws RuntimeException
    {
		return (org.compiere.model.I_AD_User)MTable.get(getCtx(), org.compiere.model.I_AD_User.Table_Name)
			.getPO(getSalesRep_ID(), get_TrxName());	}

	/** Set Sales Rep.
		@param SalesRep_ID 
		Sales Representative or Company Agent
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
	public int getSalesRep_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_SalesRep_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Statement amount.
		@param StmtAmt 
		Statement Amount
	  */
	public void setStmtAmt (BigDecimal StmtAmt)
	{
		set_Value (COLUMNNAME_StmtAmt, StmtAmt);
	}

	/** Get Statement amount.
		@return Statement Amount
	  */
	public BigDecimal getStmtAmt () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_StmtAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set DB Table Name.
		@param TableName 
		Name of the table in the database
	  */
	public void setTableName (String TableName)
	{
		set_Value (COLUMNNAME_TableName, TableName);
	}

	/** Get DB Table Name.
		@return Name of the table in the database
	  */
	public String getTableName () 
	{
		return (String)get_Value(COLUMNNAME_TableName);
	}

	/** Set Tax Indicator.
		@param TaxIndicator 
		Short form for Tax to be printed on documents
	  */
	public void setTaxIndicator (String TaxIndicator)
	{
		set_Value (COLUMNNAME_TaxIndicator, TaxIndicator);
	}

	/** Get Tax Indicator.
		@return Short form for Tax to be printed on documents
	  */
	public String getTaxIndicator () 
	{
		return (String)get_Value(COLUMNNAME_TaxIndicator);
	}

	/** Set Total Lines.
		@param TotalLines 
		Total of all document lines
	  */
	public void setTotalLines (BigDecimal TotalLines)
	{
		set_Value (COLUMNNAME_TotalLines, TotalLines);
	}

	/** Get Total Lines.
		@return Total of all document lines
	  */
	public BigDecimal getTotalLines () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_TotalLines);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Transaction Amount.
		@param TrxAmt 
		Amount of a transaction
	  */
	public void setTrxAmt (BigDecimal TrxAmt)
	{
		set_Value (COLUMNNAME_TrxAmt, TrxAmt);
	}

	/** Get Transaction Amount.
		@return Amount of a transaction
	  */
	public BigDecimal getTrxAmt () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_TrxAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	public org.compiere.model.I_C_ElementValue getUser1() throws RuntimeException
    {
		return (org.compiere.model.I_C_ElementValue)MTable.get(getCtx(), org.compiere.model.I_C_ElementValue.Table_Name)
			.getPO(getUser1_ID(), get_TrxName());	}

	/** Set User Element List 1.
		@param User1_ID 
		User defined list element #1
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
	public int getUser1_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_User1_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_ElementValue getUser2() throws RuntimeException
    {
		return (org.compiere.model.I_C_ElementValue)MTable.get(getCtx(), org.compiere.model.I_C_ElementValue.Table_Name)
			.getPO(getUser2_ID(), get_TrxName());	}

	/** Set User Element List 2.
		@param User2_ID 
		User defined list element #2
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
	public int getUser2_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_User2_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Write-off Amount.
		@param WriteOffAmt 
		Amount to write-off
	  */
	public void setWriteOffAmt (BigDecimal WriteOffAmt)
	{
		set_Value (COLUMNNAME_WriteOffAmt, WriteOffAmt);
	}

	/** Get Write-off Amount.
		@return Amount to write-off
	  */
	public BigDecimal getWriteOffAmt () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_WriteOffAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set UOM Code.
		@param X12DE355 
		UOM EDI X12 Code
	  */
	public void setX12DE355 (String X12DE355)
	{
		set_Value (COLUMNNAME_X12DE355, X12DE355);
	}

	/** Get UOM Code.
		@return UOM EDI X12 Code
	  */
	public String getX12DE355 () 
	{
		return (String)get_Value(COLUMNNAME_X12DE355);
	}
}