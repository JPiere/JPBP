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

/** Generated Interface for JP_ContractLineT
 *  @author iDempiere (generated) 
 *  @version Release 5.1
 */
@SuppressWarnings("all")
public interface I_JP_ContractLineT 
{

    /** TableName=JP_ContractLineT */
    public static final String Table_Name = "JP_ContractLineT";

    /** AD_Table_ID=1000185 */
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

    /** Column name C_ProjectPhase_ID */
    public static final String COLUMNNAME_C_ProjectPhase_ID = "C_ProjectPhase_ID";

	/** Set Project Phase.
	  * Phase of a Project
	  */
	public void setC_ProjectPhase_ID (int C_ProjectPhase_ID);

	/** Get Project Phase.
	  * Phase of a Project
	  */
	public int getC_ProjectPhase_ID();

	public org.compiere.model.I_C_ProjectPhase getC_ProjectPhase() throws RuntimeException;

    /** Column name C_ProjectTask_ID */
    public static final String COLUMNNAME_C_ProjectTask_ID = "C_ProjectTask_ID";

	/** Set Project Task.
	  * Actual Project Task in a Phase
	  */
	public void setC_ProjectTask_ID (int C_ProjectTask_ID);

	/** Get Project Task.
	  * Actual Project Task in a Phase
	  */
	public int getC_ProjectTask_ID();

	public org.compiere.model.I_C_ProjectTask getC_ProjectTask() throws RuntimeException;

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

    /** Column name DeliveryTime_Promised */
    public static final String COLUMNNAME_DeliveryTime_Promised = "DeliveryTime_Promised";

	/** Set Promised Delivery Time.
	  * Promised days between order and delivery
	  */
	public void setDeliveryTime_Promised (int DeliveryTime_Promised);

	/** Get Promised Delivery Time.
	  * Promised days between order and delivery
	  */
	public int getDeliveryTime_Promised();

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

    /** Column name Discount */
    public static final String COLUMNNAME_Discount = "Discount";

	/** Set Discount %.
	  * Discount in percent
	  */
	public void setDiscount (BigDecimal Discount);

	/** Get Discount %.
	  * Discount in percent
	  */
	public BigDecimal getDiscount();

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

    /** Column name IsCreateDocLineJP */
    public static final String COLUMNNAME_IsCreateDocLineJP = "IsCreateDocLineJP";

	/** Set Create Doc Line	  */
	public void setIsCreateDocLineJP (boolean IsCreateDocLineJP);

	/** Get Create Doc Line	  */
	public boolean isCreateDocLineJP();

    /** Column name IsDescription */
    public static final String COLUMNNAME_IsDescription = "IsDescription";

	/** Set Description Only.
	  * if true, the line is just description and no transaction
	  */
	public void setIsDescription (boolean IsDescription);

	/** Get Description Only.
	  * if true, the line is just description and no transaction
	  */
	public boolean isDescription();

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

    /** Column name IsTaxIncluded */
    public static final String COLUMNNAME_IsTaxIncluded = "IsTaxIncluded";

	/** Set Price includes Tax.
	  * Tax is included in the price 
	  */
	public void setIsTaxIncluded (boolean IsTaxIncluded);

	/** Get Price includes Tax.
	  * Tax is included in the price 
	  */
	public boolean isTaxIncluded();

    /** Column name JP_BaseDocLinePolicy */
    public static final String COLUMNNAME_JP_BaseDocLinePolicy = "JP_BaseDocLinePolicy";

	/** Set Base Doc Line Policy	  */
	public void setJP_BaseDocLinePolicy (String JP_BaseDocLinePolicy);

	/** Get Base Doc Line Policy	  */
	public String getJP_BaseDocLinePolicy();

    /** Column name JP_ContractCalRef_InOut_ID */
    public static final String COLUMNNAME_JP_ContractCalRef_InOut_ID = "JP_ContractCalRef_InOut_ID";

	/** Set Contract Calender Ref(In/Out)	  */
	public void setJP_ContractCalRef_InOut_ID (int JP_ContractCalRef_InOut_ID);

	/** Get Contract Calender Ref(In/Out)	  */
	public int getJP_ContractCalRef_InOut_ID();

	public I_JP_ContractCalenderRef getJP_ContractCalRef_InOut() throws RuntimeException;

    /** Column name JP_ContractCalRef_Inv_ID */
    public static final String COLUMNNAME_JP_ContractCalRef_Inv_ID = "JP_ContractCalRef_Inv_ID";

	/** Set Contract Calender Ref(Invoice)	  */
	public void setJP_ContractCalRef_Inv_ID (int JP_ContractCalRef_Inv_ID);

	/** Get Contract Calender Ref(Invoice)	  */
	public int getJP_ContractCalRef_Inv_ID();

	public I_JP_ContractCalenderRef getJP_ContractCalRef_Inv() throws RuntimeException;

    /** Column name JP_ContractContentT_ID */
    public static final String COLUMNNAME_JP_ContractContentT_ID = "JP_ContractContentT_ID";

	/** Set Contract Content Template	  */
	public void setJP_ContractContentT_ID (int JP_ContractContentT_ID);

	/** Get Contract Content Template	  */
	public int getJP_ContractContentT_ID();

	public I_JP_ContractContentT getJP_ContractContentT() throws RuntimeException;

    /** Column name JP_ContractLineT_ID */
    public static final String COLUMNNAME_JP_ContractLineT_ID = "JP_ContractLineT_ID";

	/** Set Contract Content Line Template	  */
	public void setJP_ContractLineT_ID (int JP_ContractLineT_ID);

	/** Get Contract Content Line Template	  */
	public int getJP_ContractLineT_ID();

    /** Column name JP_ContractLineT_UU */
    public static final String COLUMNNAME_JP_ContractLineT_UU = "JP_ContractLineT_UU";

	/** Set Contract Content Line Template(UU)	  */
	public void setJP_ContractLineT_UU (String JP_ContractLineT_UU);

	/** Get Contract Content Line Template(UU)	  */
	public String getJP_ContractLineT_UU();

    /** Column name JP_ContractProcRef_InOut_ID */
    public static final String COLUMNNAME_JP_ContractProcRef_InOut_ID = "JP_ContractProcRef_InOut_ID";

	/** Set Contract Process Ref(In/Out)	  */
	public void setJP_ContractProcRef_InOut_ID (int JP_ContractProcRef_InOut_ID);

	/** Get Contract Process Ref(In/Out)	  */
	public int getJP_ContractProcRef_InOut_ID();

	public I_JP_ContractProcessRef getJP_ContractProcRef_InOut() throws RuntimeException;

    /** Column name JP_ContractProcRef_Inv_ID */
    public static final String COLUMNNAME_JP_ContractProcRef_Inv_ID = "JP_ContractProcRef_Inv_ID";

	/** Set Contract Process Ref(Invoice)	  */
	public void setJP_ContractProcRef_Inv_ID (int JP_ContractProcRef_Inv_ID);

	/** Get Contract Process Ref(Invoice)	  */
	public int getJP_ContractProcRef_Inv_ID();

	public I_JP_ContractProcessRef getJP_ContractProcRef_Inv() throws RuntimeException;

    /** Column name JP_ContractType */
    public static final String COLUMNNAME_JP_ContractType = "JP_ContractType";

	/** Set Contract Type	  */
	public void setJP_ContractType (String JP_ContractType);

	/** Get Contract Type	  */
	public String getJP_ContractType();

    /** Column name JP_CreateDerivativeDocPolicy */
    public static final String COLUMNNAME_JP_CreateDerivativeDocPolicy = "JP_CreateDerivativeDocPolicy";

	/** Set Create Derivative Doc Policy	  */
	public void setJP_CreateDerivativeDocPolicy (String JP_CreateDerivativeDocPolicy);

	/** Get Create Derivative Doc Policy	  */
	public String getJP_CreateDerivativeDocPolicy();

    /** Column name JP_DerivativeDocPolicy_InOut */
    public static final String COLUMNNAME_JP_DerivativeDocPolicy_InOut = "JP_DerivativeDocPolicy_InOut";

	/** Set Derivative Doc Policy(In/Out)	  */
	public void setJP_DerivativeDocPolicy_InOut (String JP_DerivativeDocPolicy_InOut);

	/** Get Derivative Doc Policy(In/Out)	  */
	public String getJP_DerivativeDocPolicy_InOut();

    /** Column name JP_DerivativeDocPolicy_Inv */
    public static final String COLUMNNAME_JP_DerivativeDocPolicy_Inv = "JP_DerivativeDocPolicy_Inv";

	/** Set Derivative Doc Policy(Invoice)	  */
	public void setJP_DerivativeDocPolicy_Inv (String JP_DerivativeDocPolicy_Inv);

	/** Get Derivative Doc Policy(Invoice)	  */
	public String getJP_DerivativeDocPolicy_Inv();

    /** Column name JP_LocatorFrom_ID */
    public static final String COLUMNNAME_JP_LocatorFrom_ID = "JP_LocatorFrom_ID";

	/** Set Locator(From)	  */
	public void setJP_LocatorFrom_ID (int JP_LocatorFrom_ID);

	/** Get Locator(From)	  */
	public int getJP_LocatorFrom_ID();

	public org.compiere.model.I_M_Locator getJP_LocatorFrom() throws RuntimeException;

    /** Column name JP_LocatorTo_ID */
    public static final String COLUMNNAME_JP_LocatorTo_ID = "JP_LocatorTo_ID";

	/** Set Locator(To)	  */
	public void setJP_LocatorTo_ID (int JP_LocatorTo_ID);

	/** Get Locator(To)	  */
	public int getJP_LocatorTo_ID();

	public org.compiere.model.I_M_Locator getJP_LocatorTo() throws RuntimeException;

    /** Column name JP_Locator_ID */
    public static final String COLUMNNAME_JP_Locator_ID = "JP_Locator_ID";

	/** Set Locator	  */
	public void setJP_Locator_ID (int JP_Locator_ID);

	/** Get Locator	  */
	public int getJP_Locator_ID();

	public org.compiere.model.I_M_Locator getJP_Locator() throws RuntimeException;

    /** Column name JP_ProcPeriodOffs_End */
    public static final String COLUMNNAME_JP_ProcPeriodOffs_End = "JP_ProcPeriodOffs_End";

	/** Set Offset of End Contract Process Period	  */
	public void setJP_ProcPeriodOffs_End (int JP_ProcPeriodOffs_End);

	/** Get Offset of End Contract Process Period	  */
	public int getJP_ProcPeriodOffs_End();

    /** Column name JP_ProcPeriodOffs_End_InOut */
    public static final String COLUMNNAME_JP_ProcPeriodOffs_End_InOut = "JP_ProcPeriodOffs_End_InOut";

	/** Set Offset of End Contract Process Period(In/Out)	  */
	public void setJP_ProcPeriodOffs_End_InOut (int JP_ProcPeriodOffs_End_InOut);

	/** Get Offset of End Contract Process Period(In/Out)	  */
	public int getJP_ProcPeriodOffs_End_InOut();

    /** Column name JP_ProcPeriodOffs_End_Inv */
    public static final String COLUMNNAME_JP_ProcPeriodOffs_End_Inv = "JP_ProcPeriodOffs_End_Inv";

	/** Set Offset of End Contract Process Period(Invoice)	  */
	public void setJP_ProcPeriodOffs_End_Inv (int JP_ProcPeriodOffs_End_Inv);

	/** Get Offset of End Contract Process Period(Invoice)	  */
	public int getJP_ProcPeriodOffs_End_Inv();

    /** Column name JP_ProcPeriodOffs_Lump */
    public static final String COLUMNNAME_JP_ProcPeriodOffs_Lump = "JP_ProcPeriodOffs_Lump";

	/** Set Offset of Period to handle in a lump	  */
	public void setJP_ProcPeriodOffs_Lump (int JP_ProcPeriodOffs_Lump);

	/** Get Offset of Period to handle in a lump	  */
	public int getJP_ProcPeriodOffs_Lump();

    /** Column name JP_ProcPeriodOffs_Lump_InOut */
    public static final String COLUMNNAME_JP_ProcPeriodOffs_Lump_InOut = "JP_ProcPeriodOffs_Lump_InOut";

	/** Set Offset of Period to handle in a lump(In/Out)	  */
	public void setJP_ProcPeriodOffs_Lump_InOut (int JP_ProcPeriodOffs_Lump_InOut);

	/** Get Offset of Period to handle in a lump(In/Out)	  */
	public int getJP_ProcPeriodOffs_Lump_InOut();

    /** Column name JP_ProcPeriodOffs_Lump_Inv */
    public static final String COLUMNNAME_JP_ProcPeriodOffs_Lump_Inv = "JP_ProcPeriodOffs_Lump_Inv";

	/** Set Offset of Period to handle in a lump(Invoice)	  */
	public void setJP_ProcPeriodOffs_Lump_Inv (int JP_ProcPeriodOffs_Lump_Inv);

	/** Get Offset of Period to handle in a lump(Invoice)	  */
	public int getJP_ProcPeriodOffs_Lump_Inv();

    /** Column name JP_ProcPeriodOffs_Start */
    public static final String COLUMNNAME_JP_ProcPeriodOffs_Start = "JP_ProcPeriodOffs_Start";

	/** Set Offset of Start Contract Process Period	  */
	public void setJP_ProcPeriodOffs_Start (int JP_ProcPeriodOffs_Start);

	/** Get Offset of Start Contract Process Period	  */
	public int getJP_ProcPeriodOffs_Start();

    /** Column name JP_ProcPeriodOffs_Start_InOut */
    public static final String COLUMNNAME_JP_ProcPeriodOffs_Start_InOut = "JP_ProcPeriodOffs_Start_InOut";

	/** Set Offset of Start Contract Process Period(In/Out)	  */
	public void setJP_ProcPeriodOffs_Start_InOut (int JP_ProcPeriodOffs_Start_InOut);

	/** Get Offset of Start Contract Process Period(In/Out)	  */
	public int getJP_ProcPeriodOffs_Start_InOut();

    /** Column name JP_ProcPeriodOffs_Start_Inv */
    public static final String COLUMNNAME_JP_ProcPeriodOffs_Start_Inv = "JP_ProcPeriodOffs_Start_Inv";

	/** Set Offset of Start Contract Process Period(Invoice)	  */
	public void setJP_ProcPeriodOffs_Start_Inv (int JP_ProcPeriodOffs_Start_Inv);

	/** Get Offset of Start Contract Process Period(Invoice)	  */
	public int getJP_ProcPeriodOffs_Start_Inv();

    /** Column name JP_QtyOrderd_UOM_ID */
    public static final String COLUMNNAME_JP_QtyOrderd_UOM_ID = "JP_QtyOrderd_UOM_ID";

	/** Set Ordered Qty UOM.
	  * Ordered Qty Unit of Measure
	  */
	public void setJP_QtyOrderd_UOM_ID (int JP_QtyOrderd_UOM_ID);

	/** Get Ordered Qty UOM.
	  * Ordered Qty Unit of Measure
	  */
	public int getJP_QtyOrderd_UOM_ID();

	public org.compiere.model.I_C_UOM getJP_QtyOrderd_UOM() throws RuntimeException;

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

    /** Column name MovementQty */
    public static final String COLUMNNAME_MovementQty = "MovementQty";

	/** Set Movement Quantity.
	  * Quantity of a product moved.
	  */
	public void setMovementQty (BigDecimal MovementQty);

	/** Get Movement Quantity.
	  * Quantity of a product moved.
	  */
	public BigDecimal getMovementQty();

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

    /** Column name OrderType */
    public static final String COLUMNNAME_OrderType = "OrderType";

	/** Set Order Type.
	  * Type of Order: MRP records grouped by source (Sales Order, Purchase Order, Distribution Order, Requisition)
	  */
	public void setOrderType (String OrderType);

	/** Get Order Type.
	  * Type of Order: MRP records grouped by source (Sales Order, Purchase Order, Distribution Order, Requisition)
	  */
	public String getOrderType();

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

    /** Column name PriceLimit */
    public static final String COLUMNNAME_PriceLimit = "PriceLimit";

	/** Set Limit Price.
	  * Lowest price for a product
	  */
	public void setPriceLimit (BigDecimal PriceLimit);

	/** Get Limit Price.
	  * Lowest price for a product
	  */
	public BigDecimal getPriceLimit();

    /** Column name PriceList */
    public static final String COLUMNNAME_PriceList = "PriceList";

	/** Set List Price.
	  * List Price
	  */
	public void setPriceList (BigDecimal PriceList);

	/** Get List Price.
	  * List Price
	  */
	public BigDecimal getPriceList();

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
}
