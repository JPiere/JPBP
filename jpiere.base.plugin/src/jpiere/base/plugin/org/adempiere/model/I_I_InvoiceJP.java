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

/** Generated Interface for I_InvoiceJP
 *  @author iDempiere (generated) 
 *  @version Release 5.1
 */
@SuppressWarnings("all")
public interface I_I_InvoiceJP 
{

    /** TableName=I_InvoiceJP */
    public static final String Table_Name = "I_InvoiceJP";

    /** AD_Table_ID=1000219 */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 3 - Client - Org 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(3);

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

    /** Column name C_InvoiceLine_ID */
    public static final String COLUMNNAME_C_InvoiceLine_ID = "C_InvoiceLine_ID";

	/** Set Invoice Line.
	  * Invoice Detail Line
	  */
	public void setC_InvoiceLine_ID (int C_InvoiceLine_ID);

	/** Get Invoice Line.
	  * Invoice Detail Line
	  */
	public int getC_InvoiceLine_ID();

	public org.compiere.model.I_C_InvoiceLine getC_InvoiceLine() throws RuntimeException;

    /** Column name C_Invoice_ID */
    public static final String COLUMNNAME_C_Invoice_ID = "C_Invoice_ID";

	/** Set Invoice.
	  * Invoice Identifier
	  */
	public void setC_Invoice_ID (int C_Invoice_ID);

	/** Get Invoice.
	  * Invoice Identifier
	  */
	public int getC_Invoice_ID();

	public org.compiere.model.I_C_Invoice getC_Invoice() throws RuntimeException;

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

	public org.compiere.model.I_C_Payment getC_PaymentTerm() throws RuntimeException;

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

    /** Column name DateInvoiced */
    public static final String COLUMNNAME_DateInvoiced = "DateInvoiced";

	/** Set Date Invoiced.
	  * Date printed on Invoice
	  */
	public void setDateInvoiced (Timestamp DateInvoiced);

	/** Get Date Invoiced.
	  * Date printed on Invoice
	  */
	public Timestamp getDateInvoiced();

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

    /** Column name I_InvoiceJP_ID */
    public static final String COLUMNNAME_I_InvoiceJP_ID = "I_InvoiceJP_ID";

	/** Set I_InvoiceJP	  */
	public void setI_InvoiceJP_ID (int I_InvoiceJP_ID);

	/** Get I_InvoiceJP	  */
	public int getI_InvoiceJP_ID();

    /** Column name I_InvoiceJP_UU */
    public static final String COLUMNNAME_I_InvoiceJP_UU = "I_InvoiceJP_UU";

	/** Set I_InvoiceJP_UU	  */
	public void setI_InvoiceJP_UU (String I_InvoiceJP_UU);

	/** Get I_InvoiceJP_UU	  */
	public String getI_InvoiceJP_UU();

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

    /** Column name JP_DocType_Name */
    public static final String COLUMNNAME_JP_DocType_Name = "JP_DocType_Name";

	/** Set Document Type(Name)	  */
	public void setJP_DocType_Name (String JP_DocType_Name);

	/** Get Document Type(Name)	  */
	public String getJP_DocType_Name();

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

    /** Column name QtyInvoiced */
    public static final String COLUMNNAME_QtyInvoiced = "QtyInvoiced";

	/** Set Invoiced Qty.
	  * Invoiced Quantity
	  */
	public void setQtyInvoiced (BigDecimal QtyInvoiced);

	/** Get Invoiced Qty.
	  * Invoiced Quantity
	  */
	public BigDecimal getQtyInvoiced();

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
