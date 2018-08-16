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

/** Generated Interface for I_DataMigrationJP
 *  @author iDempiere (generated) 
 *  @version Release 5.1
 */
@SuppressWarnings("all")
public interface I_I_DataMigrationJP 
{

    /** TableName=I_DataMigrationJP */
    public static final String Table_Name = "I_DataMigrationJP";

    /** AD_Table_ID=1000222 */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 1 - Org 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(1);

    /** Load Meta Data */

    /** Column name AD_Client_ID */
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";

	/** Get Client.
	  * Client/Tenant for this installation.
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
	  * Organizational entity within client
	  */
	public void setAD_Org_ID (int AD_Org_ID);

	/** Get Organization.
	  * Organizational entity within client
	  */
	public int getAD_Org_ID();

    /** Column name AD_Table_ID */
    public static final String COLUMNNAME_AD_Table_ID = "AD_Table_ID";

	/** Set Table.
	  * Database Table information
	  */
	public void setAD_Table_ID (int AD_Table_ID);

	/** Get Table.
	  * Database Table information
	  */
	public int getAD_Table_ID();

	public org.compiere.model.I_AD_Table getAD_Table() throws RuntimeException;

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

	/** Set Business Partner .
	  * Identifies a Business Partner
	  */
	public void setC_BPartner_ID (int C_BPartner_ID);

	/** Get Business Partner .
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

    /** Column name C_BankAccount_ID */
    public static final String COLUMNNAME_C_BankAccount_ID = "C_BankAccount_ID";

	/** Set Bank Account.
	  * Account at the Bank
	  */
	public void setC_BankAccount_ID (int C_BankAccount_ID);

	/** Get Bank Account.
	  * Account at the Bank
	  */
	public int getC_BankAccount_ID();

	public org.compiere.model.I_C_BankAccount getC_BankAccount() throws RuntimeException;

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

    /** Column name ChargeAmt */
    public static final String COLUMNNAME_ChargeAmt = "ChargeAmt";

	/** Set Charge amount.
	  * Charge Amount
	  */
	public void setChargeAmt (BigDecimal ChargeAmt);

	/** Get Charge amount.
	  * Charge Amount
	  */
	public BigDecimal getChargeAmt();

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

    /** Column name DateDoc */
    public static final String COLUMNNAME_DateDoc = "DateDoc";

	/** Set Document Date.
	  * Date of the Document
	  */
	public void setDateDoc (Timestamp DateDoc);

	/** Get Document Date.
	  * Date of the Document
	  */
	public Timestamp getDateDoc();

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

    /** Column name DiscountAmt */
    public static final String COLUMNNAME_DiscountAmt = "DiscountAmt";

	/** Set Discount Amount.
	  * Calculated amount of discount
	  */
	public void setDiscountAmt (BigDecimal DiscountAmt);

	/** Get Discount Amount.
	  * Calculated amount of discount
	  */
	public BigDecimal getDiscountAmt();

    /** Column name DocBaseType */
    public static final String COLUMNNAME_DocBaseType = "DocBaseType";

	/** Set Document BaseType.
	  * Logical type of document
	  */
	public void setDocBaseType (String DocBaseType);

	/** Get Document BaseType.
	  * Logical type of document
	  */
	public String getDocBaseType();

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

    /** Column name GrandTotal */
    public static final String COLUMNNAME_GrandTotal = "GrandTotal";

	/** Set Grand Total.
	  * Total amount of document
	  */
	public void setGrandTotal (BigDecimal GrandTotal);

	/** Get Grand Total.
	  * Total amount of document
	  */
	public BigDecimal getGrandTotal();

    /** Column name ISO_Code */
    public static final String COLUMNNAME_ISO_Code = "ISO_Code";

	/** Set ISO Currency Code.
	  * Three letter ISO 4217 Code of the Currency
	  */
	public void setISO_Code (String ISO_Code);

	/** Get ISO Currency Code.
	  * Three letter ISO 4217 Code of the Currency
	  */
	public String getISO_Code();

    /** Column name I_DataMigrationJP_ID */
    public static final String COLUMNNAME_I_DataMigrationJP_ID = "I_DataMigrationJP_ID";

	/** Set I_DataMigrationJP	  */
	public void setI_DataMigrationJP_ID (int I_DataMigrationJP_ID);

	/** Get I_DataMigrationJP	  */
	public int getI_DataMigrationJP_ID();

    /** Column name I_DataMigrationJP_UU */
    public static final String COLUMNNAME_I_DataMigrationJP_UU = "I_DataMigrationJP_UU";

	/** Set I_DataMigrationJP_UU	  */
	public void setI_DataMigrationJP_UU (String I_DataMigrationJP_UU);

	/** Get I_DataMigrationJP_UU	  */
	public String getI_DataMigrationJP_UU();

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

    /** Column name InterestAmt */
    public static final String COLUMNNAME_InterestAmt = "InterestAmt";

	/** Set Interest Amount.
	  * Interest Amount
	  */
	public void setInterestAmt (BigDecimal InterestAmt);

	/** Get Interest Amount.
	  * Interest Amount
	  */
	public BigDecimal getInterestAmt();

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

    /** Column name IsDropShip */
    public static final String COLUMNNAME_IsDropShip = "IsDropShip";

	/** Set Drop Shipment.
	  * Drop Shipments are sent from the Vendor directly to the Customer
	  */
	public void setIsDropShip (boolean IsDropShip);

	/** Get Drop Shipment.
	  * Drop Shipments are sent from the Vendor directly to the Customer
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

    /** Column name JP_Activity_Value */
    public static final String COLUMNNAME_JP_Activity_Value = "JP_Activity_Value";

	/** Set Activity(Search Key)	  */
	public void setJP_Activity_Value (String JP_Activity_Value);

	/** Get Activity(Search Key)	  */
	public String getJP_Activity_Value();

    /** Column name JP_BPartner_Location_Name */
    public static final String COLUMNNAME_JP_BPartner_Location_Name = "JP_BPartner_Location_Name";

	/** Set Partner Location(Name)	  */
	public void setJP_BPartner_Location_Name (String JP_BPartner_Location_Name);

	/** Get Partner Location(Name)	  */
	public String getJP_BPartner_Location_Name();

    /** Column name JP_BPartner_Value */
    public static final String COLUMNNAME_JP_BPartner_Value = "JP_BPartner_Value";

	/** Set Business Partner(Search Key)	  */
	public void setJP_BPartner_Value (String JP_BPartner_Value);

	/** Get Business Partner(Search Key)	  */
	public String getJP_BPartner_Value();

    /** Column name JP_BankAccount_Value */
    public static final String COLUMNNAME_JP_BankAccount_Value = "JP_BankAccount_Value";

	/** Set Bank Account(Search Key)	  */
	public void setJP_BankAccount_Value (String JP_BankAccount_Value);

	/** Get Bank Account(Search Key)	  */
	public String getJP_BankAccount_Value();

    /** Column name JP_Bank_Name */
    public static final String COLUMNNAME_JP_Bank_Name = "JP_Bank_Name";

	/** Set Bank Name	  */
	public void setJP_Bank_Name (String JP_Bank_Name);

	/** Get Bank Name	  */
	public String getJP_Bank_Name();

    /** Column name JP_Campaign_Value */
    public static final String COLUMNNAME_JP_Campaign_Value = "JP_Campaign_Value";

	/** Set Campaign(Search Key)	  */
	public void setJP_Campaign_Value (String JP_Campaign_Value);

	/** Get Campaign(Search Key)	  */
	public String getJP_Campaign_Value();

    /** Column name JP_Charge_Name */
    public static final String COLUMNNAME_JP_Charge_Name = "JP_Charge_Name";

	/** Set Charge(Name)	  */
	public void setJP_Charge_Name (String JP_Charge_Name);

	/** Get Charge(Name)	  */
	public String getJP_Charge_Name();

    /** Column name JP_DataMigrationLine_ID */
    public static final String COLUMNNAME_JP_DataMigrationLine_ID = "JP_DataMigrationLine_ID";

	/** Set JP_DataMigrationLine	  */
	public void setJP_DataMigrationLine_ID (int JP_DataMigrationLine_ID);

	/** Get JP_DataMigrationLine	  */
	public int getJP_DataMigrationLine_ID();

	public I_JP_DataMigrationLine getJP_DataMigrationLine() throws RuntimeException;

    /** Column name JP_DataMigration_DocStatus */
    public static final String COLUMNNAME_JP_DataMigration_DocStatus = "JP_DataMigration_DocStatus";

	/** Set Document Status	  */
	public void setJP_DataMigration_DocStatus (String JP_DataMigration_DocStatus);

	/** Get Document Status	  */
	public String getJP_DataMigration_DocStatus();

    /** Column name JP_DataMigration_ID */
    public static final String COLUMNNAME_JP_DataMigration_ID = "JP_DataMigration_ID";

	/** Set JP_DataMigration	  */
	public void setJP_DataMigration_ID (int JP_DataMigration_ID);

	/** Get JP_DataMigration	  */
	public int getJP_DataMigration_ID();

	public I_JP_DataMigration getJP_DataMigration() throws RuntimeException;

    /** Column name JP_DataMigration_Identifier */
    public static final String COLUMNNAME_JP_DataMigration_Identifier = "JP_DataMigration_Identifier";

	/** Set Data Migration Identifier	  */
	public void setJP_DataMigration_Identifier (String JP_DataMigration_Identifier);

	/** Get Data Migration Identifier	  */
	public String getJP_DataMigration_Identifier();

    /** Column name JP_DocType_Name */
    public static final String COLUMNNAME_JP_DocType_Name = "JP_DocType_Name";

	/** Set Document Type(Name)	  */
	public void setJP_DocType_Name (String JP_DocType_Name);

	/** Get Document Type(Name)	  */
	public String getJP_DocType_Name();

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

    /** Column name JP_Invoice_DocumentNo */
    public static final String COLUMNNAME_JP_Invoice_DocumentNo = "JP_Invoice_DocumentNo";

	/** Set Invoice Document No	  */
	public void setJP_Invoice_DocumentNo (String JP_Invoice_DocumentNo);

	/** Get Invoice Document No	  */
	public String getJP_Invoice_DocumentNo();

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

    /** Column name JP_Line_BPartner_ID */
    public static final String COLUMNNAME_JP_Line_BPartner_ID = "JP_Line_BPartner_ID";

	/** Set Business Partner of Line	  */
	public void setJP_Line_BPartner_ID (int JP_Line_BPartner_ID);

	/** Get Business Partner of Line	  */
	public int getJP_Line_BPartner_ID();

	public org.compiere.model.I_C_BPartner getJP_Line_BPartner() throws RuntimeException;

    /** Column name JP_Line_BPartner_Value */
    public static final String COLUMNNAME_JP_Line_BPartner_Value = "JP_Line_BPartner_Value";

	/** Set Business Partner of Line(Search Key)	  */
	public void setJP_Line_BPartner_Value (String JP_Line_BPartner_Value);

	/** Get Business Partner of Line(Search Key)	  */
	public String getJP_Line_BPartner_Value();

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

    /** Column name JP_Link_Order_DocumentNo */
    public static final String COLUMNNAME_JP_Link_Order_DocumentNo = "JP_Link_Order_DocumentNo";

	/** Set Linked Order Document No	  */
	public void setJP_Link_Order_DocumentNo (String JP_Link_Order_DocumentNo);

	/** Get Linked Order Document No	  */
	public String getJP_Link_Order_DocumentNo();

    /** Column name JP_LocatorFrom_ID */
    public static final String COLUMNNAME_JP_LocatorFrom_ID = "JP_LocatorFrom_ID";

	/** Set Locator(From)	  */
	public void setJP_LocatorFrom_ID (int JP_LocatorFrom_ID);

	/** Get Locator(From)	  */
	public int getJP_LocatorFrom_ID();

	public org.compiere.model.I_M_Locator getJP_LocatorFrom() throws RuntimeException;

    /** Column name JP_LocatorFrom_Value */
    public static final String COLUMNNAME_JP_LocatorFrom_Value = "JP_LocatorFrom_Value";

	/** Set Locator(From)(Search Key)	  */
	public void setJP_LocatorFrom_Value (String JP_LocatorFrom_Value);

	/** Get Locator(From)(Search Key)	  */
	public String getJP_LocatorFrom_Value();

    /** Column name JP_LocatorTo_ID */
    public static final String COLUMNNAME_JP_LocatorTo_ID = "JP_LocatorTo_ID";

	/** Set Locator(To)	  */
	public void setJP_LocatorTo_ID (int JP_LocatorTo_ID);

	/** Get Locator(To)	  */
	public int getJP_LocatorTo_ID();

	public org.compiere.model.I_M_Locator getJP_LocatorTo() throws RuntimeException;

    /** Column name JP_LocatorTo_Value */
    public static final String COLUMNNAME_JP_LocatorTo_Value = "JP_LocatorTo_Value";

	/** Set Locator(To)(Search Key)	  */
	public void setJP_LocatorTo_Value (String JP_LocatorTo_Value);

	/** Get Locator(To)(Search Key)	  */
	public String getJP_LocatorTo_Value();

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

    /** Column name JP_Order_DocumentNo */
    public static final String COLUMNNAME_JP_Order_DocumentNo = "JP_Order_DocumentNo";

	/** Set JP_Order_DocumentNo	  */
	public void setJP_Order_DocumentNo (String JP_Order_DocumentNo);

	/** Get JP_Order_DocumentNo	  */
	public String getJP_Order_DocumentNo();

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

    /** Column name JP_Payment_DocumentNo */
    public static final String COLUMNNAME_JP_Payment_DocumentNo = "JP_Payment_DocumentNo";

	/** Set Payment Document No	  */
	public void setJP_Payment_DocumentNo (String JP_Payment_DocumentNo);

	/** Get Payment Document No	  */
	public String getJP_Payment_DocumentNo();

    /** Column name JP_PhysicalWarehouse_ID */
    public static final String COLUMNNAME_JP_PhysicalWarehouse_ID = "JP_PhysicalWarehouse_ID";

	/** Set Physical Warehouse	  */
	public void setJP_PhysicalWarehouse_ID (int JP_PhysicalWarehouse_ID);

	/** Get Physical Warehouse	  */
	public int getJP_PhysicalWarehouse_ID();

	public I_JP_PhysicalWarehouse getJP_PhysicalWarehouse() throws RuntimeException;

    /** Column name JP_PhysicalWarehouse_Value */
    public static final String COLUMNNAME_JP_PhysicalWarehouse_Value = "JP_PhysicalWarehouse_Value";

	/** Set Physical Warehouse(Search Key)	  */
	public void setJP_PhysicalWarehouse_Value (String JP_PhysicalWarehouse_Value);

	/** Get Physical Warehouse(Search Key)	  */
	public String getJP_PhysicalWarehouse_Value();

    /** Column name JP_PriceList_Name */
    public static final String COLUMNNAME_JP_PriceList_Name = "JP_PriceList_Name";

	/** Set Price List(Name)	  */
	public void setJP_PriceList_Name (String JP_PriceList_Name);

	/** Get Price List(Name)	  */
	public String getJP_PriceList_Name();

    /** Column name JP_Product_Value */
    public static final String COLUMNNAME_JP_Product_Value = "JP_Product_Value";

	/** Set Product(Search Key)	  */
	public void setJP_Product_Value (String JP_Product_Value);

	/** Get Product(Search Key)	  */
	public String getJP_Product_Value();

    /** Column name JP_Project_Value */
    public static final String COLUMNNAME_JP_Project_Value = "JP_Project_Value";

	/** Set Project(Search Key)	  */
	public void setJP_Project_Value (String JP_Project_Value);

	/** Get Project(Search Key)	  */
	public String getJP_Project_Value();

    /** Column name JP_Ref_Order_DocumentNo */
    public static final String COLUMNNAME_JP_Ref_Order_DocumentNo = "JP_Ref_Order_DocumentNo";

	/** Set Ref Order Document No	  */
	public void setJP_Ref_Order_DocumentNo (String JP_Ref_Order_DocumentNo);

	/** Get Ref Order Document No	  */
	public String getJP_Ref_Order_DocumentNo();

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

    /** Column name JP_User_EMail */
    public static final String COLUMNNAME_JP_User_EMail = "JP_User_EMail";

	/** Set User(E-Mail)	  */
	public void setJP_User_EMail (String JP_User_EMail);

	/** Get User(E-Mail)	  */
	public String getJP_User_EMail();

    /** Column name JP_User_Name */
    public static final String COLUMNNAME_JP_User_Name = "JP_User_Name";

	/** Set User(Name)	  */
	public void setJP_User_Name (String JP_User_Name);

	/** Get User(Name)	  */
	public String getJP_User_Name();

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

    /** Column name LineNetAmt */
    public static final String COLUMNNAME_LineNetAmt = "LineNetAmt";

	/** Set Line Amount.
	  * Line Extended Amount (Quantity * Actual Price) without Freight and Charges
	  */
	public void setLineNetAmt (BigDecimal LineNetAmt);

	/** Get Line Amount.
	  * Line Extended Amount (Quantity * Actual Price) without Freight and Charges
	  */
	public BigDecimal getLineNetAmt();

    /** Column name M_FreightCategory_ID */
    public static final String COLUMNNAME_M_FreightCategory_ID = "M_FreightCategory_ID";

	/** Set Freight Category.
	  * Category of the Freight
	  */
	public void setM_FreightCategory_ID (int M_FreightCategory_ID);

	/** Get Freight Category.
	  * Category of the Freight
	  */
	public int getM_FreightCategory_ID();

	public org.compiere.model.I_M_FreightCategory getM_FreightCategory() throws RuntimeException;

    /** Column name M_Locator_ID */
    public static final String COLUMNNAME_M_Locator_ID = "M_Locator_ID";

	/** Set Locator.
	  * Warehouse Locator
	  */
	public void setM_Locator_ID (int M_Locator_ID);

	/** Get Locator.
	  * Warehouse Locator
	  */
	public int getM_Locator_ID();

	public org.compiere.model.I_M_Locator getM_Locator() throws RuntimeException;

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

    /** Column name OverUnderAmt */
    public static final String COLUMNNAME_OverUnderAmt = "OverUnderAmt";

	/** Set Over/Under Payment.
	  * Over-Payment (unallocated) or Under-Payment (partial payment) Amount
	  */
	public void setOverUnderAmt (BigDecimal OverUnderAmt);

	/** Get Over/Under Payment.
	  * Over-Payment (unallocated) or Under-Payment (partial payment) Amount
	  */
	public BigDecimal getOverUnderAmt();

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

    /** Column name PayAmt */
    public static final String COLUMNNAME_PayAmt = "PayAmt";

	/** Set Payment amount.
	  * Amount being paid
	  */
	public void setPayAmt (BigDecimal PayAmt);

	/** Get Payment amount.
	  * Amount being paid
	  */
	public BigDecimal getPayAmt();

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

    /** Column name Price */
    public static final String COLUMNNAME_Price = "Price";

	/** Set Price.
	  * Price
	  */
	public void setPrice (BigDecimal Price);

	/** Get Price.
	  * Price
	  */
	public BigDecimal getPrice();

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

    /** Column name Qty */
    public static final String COLUMNNAME_Qty = "Qty";

	/** Set Quantity.
	  * Quantity
	  */
	public void setQty (BigDecimal Qty);

	/** Get Quantity.
	  * Quantity
	  */
	public BigDecimal getQty();

    /** Column name QtyBook */
    public static final String COLUMNNAME_QtyBook = "QtyBook";

	/** Set Quantity book.
	  * Book Quantity
	  */
	public void setQtyBook (BigDecimal QtyBook);

	/** Get Quantity book.
	  * Book Quantity
	  */
	public BigDecimal getQtyBook();

    /** Column name QtyCount */
    public static final String COLUMNNAME_QtyCount = "QtyCount";

	/** Set Quantity count.
	  * Counted Quantity
	  */
	public void setQtyCount (BigDecimal QtyCount);

	/** Get Quantity count.
	  * Counted Quantity
	  */
	public BigDecimal getQtyCount();

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

    /** Column name StmtAmt */
    public static final String COLUMNNAME_StmtAmt = "StmtAmt";

	/** Set Statement amount.
	  * Statement Amount
	  */
	public void setStmtAmt (BigDecimal StmtAmt);

	/** Get Statement amount.
	  * Statement Amount
	  */
	public BigDecimal getStmtAmt();

    /** Column name TableName */
    public static final String COLUMNNAME_TableName = "TableName";

	/** Set DB Table Name.
	  * Name of the table in the database
	  */
	public void setTableName (String TableName);

	/** Get DB Table Name.
	  * Name of the table in the database
	  */
	public String getTableName();

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

    /** Column name TotalLines */
    public static final String COLUMNNAME_TotalLines = "TotalLines";

	/** Set Total Lines.
	  * Total of all document lines
	  */
	public void setTotalLines (BigDecimal TotalLines);

	/** Get Total Lines.
	  * Total of all document lines
	  */
	public BigDecimal getTotalLines();

    /** Column name TrxAmt */
    public static final String COLUMNNAME_TrxAmt = "TrxAmt";

	/** Set Transaction Amount.
	  * Amount of a transaction
	  */
	public void setTrxAmt (BigDecimal TrxAmt);

	/** Get Transaction Amount.
	  * Amount of a transaction
	  */
	public BigDecimal getTrxAmt();

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

    /** Column name WriteOffAmt */
    public static final String COLUMNNAME_WriteOffAmt = "WriteOffAmt";

	/** Set Write-off Amount.
	  * Amount to write-off
	  */
	public void setWriteOffAmt (BigDecimal WriteOffAmt);

	/** Get Write-off Amount.
	  * Amount to write-off
	  */
	public BigDecimal getWriteOffAmt();

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
