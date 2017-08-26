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

/** Generated Interface for JP_Contract
 *  @author iDempiere (generated) 
 *  @version Release 4.1
 */
@SuppressWarnings("all")
public interface I_JP_Contract 
{

    /** TableName=JP_Contract */
    public static final String Table_Name = "JP_Contract";

    /** AD_Table_ID=1000180 */
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

    /** Column name DocAction */
    public static final String COLUMNNAME_DocAction = "DocAction";

	/** Set Document Action.
	  * The targeted status of the document
	  */
	public void setDocAction (String DocAction);

	/** Get Document Action.
	  * The targeted status of the document
	  */
	public String getDocAction();

    /** Column name DocStatus */
    public static final String COLUMNNAME_DocStatus = "DocStatus";

	/** Set Document Status.
	  * The current status of the document
	  */
	public void setDocStatus (String DocStatus);

	/** Get Document Status.
	  * The current status of the document
	  */
	public String getDocStatus();

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

    /** Column name Help */
    public static final String COLUMNNAME_Help = "Help";

	/** Set Comment/Help.
	  * Comment or Hint
	  */
	public void setHelp (String Help);

	/** Get Comment/Help.
	  * Comment or Hint
	  */
	public String getHelp();

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

    /** Column name IsApproved */
    public static final String COLUMNNAME_IsApproved = "IsApproved";

	/** Set Approved.
	  * Indicates if this document requires approval
	  */
	public void setIsApproved (boolean IsApproved);

	/** Get Approved.
	  * Indicates if this document requires approval
	  */
	public boolean isApproved();

    /** Column name IsAutomaticUpdateJP */
    public static final String COLUMNNAME_IsAutomaticUpdateJP = "IsAutomaticUpdateJP";

	/** Set Automatic Update	  */
	public void setIsAutomaticUpdateJP (boolean IsAutomaticUpdateJP);

	/** Get Automatic Update	  */
	public boolean isAutomaticUpdateJP();

    /** Column name JP_ContractCancelCause_ID */
    public static final String COLUMNNAME_JP_ContractCancelCause_ID = "JP_ContractCancelCause_ID";

	/** Set Contract Cancel Cause	  */
	public void setJP_ContractCancelCause_ID (int JP_ContractCancelCause_ID);

	/** Get Contract Cancel Cause	  */
	public int getJP_ContractCancelCause_ID();

	public I_JP_ContractCancelCause getJP_ContractCancelCause() throws RuntimeException;

    /** Column name JP_ContractCancelDate */
    public static final String COLUMNNAME_JP_ContractCancelDate = "JP_ContractCancelDate";

	/** Set Contract Cancel Date	  */
	public void setJP_ContractCancelDate (Timestamp JP_ContractCancelDate);

	/** Get Contract Cancel Date	  */
	public Timestamp getJP_ContractCancelDate();

    /** Column name JP_ContractCancelDeadline */
    public static final String COLUMNNAME_JP_ContractCancelDeadline = "JP_ContractCancelDeadline";

	/** Set Cancel Deadline	  */
	public void setJP_ContractCancelDeadline (Timestamp JP_ContractCancelDeadline);

	/** Get Cancel Deadline	  */
	public Timestamp getJP_ContractCancelDeadline();

    /** Column name JP_ContractCancelOfferDate */
    public static final String COLUMNNAME_JP_ContractCancelOfferDate = "JP_ContractCancelOfferDate";

	/** Set Cancel Offer Date	  */
	public void setJP_ContractCancelOfferDate (Timestamp JP_ContractCancelOfferDate);

	/** Get Cancel Offer Date	  */
	public Timestamp getJP_ContractCancelOfferDate();

    /** Column name JP_ContractCancelTerm_ID */
    public static final String COLUMNNAME_JP_ContractCancelTerm_ID = "JP_ContractCancelTerm_ID";

	/** Set Contract Cancel Term	  */
	public void setJP_ContractCancelTerm_ID (int JP_ContractCancelTerm_ID);

	/** Get Contract Cancel Term	  */
	public int getJP_ContractCancelTerm_ID();

	public I_JP_ContractCancelTerm getJP_ContractCancelTerm() throws RuntimeException;

    /** Column name JP_ContractCancel_SalesRep_ID */
    public static final String COLUMNNAME_JP_ContractCancel_SalesRep_ID = "JP_ContractCancel_SalesRep_ID";

	/** Set Cancel Sales Rep	  */
	public void setJP_ContractCancel_SalesRep_ID (int JP_ContractCancel_SalesRep_ID);

	/** Get Cancel Sales Rep	  */
	public int getJP_ContractCancel_SalesRep_ID();

	public org.compiere.model.I_AD_User getJP_ContractCancel_SalesRep() throws RuntimeException;

    /** Column name JP_ContractCancel_User_ID */
    public static final String COLUMNNAME_JP_ContractCancel_User_ID = "JP_ContractCancel_User_ID";

	/** Set Cancel User	  */
	public void setJP_ContractCancel_User_ID (int JP_ContractCancel_User_ID);

	/** Get Cancel User	  */
	public int getJP_ContractCancel_User_ID();

	public org.compiere.model.I_AD_User getJP_ContractCancel_User() throws RuntimeException;

    /** Column name JP_ContractCategory_ID */
    public static final String COLUMNNAME_JP_ContractCategory_ID = "JP_ContractCategory_ID";

	/** Set Contract Category	  */
	public void setJP_ContractCategory_ID (int JP_ContractCategory_ID);

	/** Get Contract Category	  */
	public int getJP_ContractCategory_ID();

	public I_JP_ContractCategory getJP_ContractCategory() throws RuntimeException;

    /** Column name JP_ContractDocAmt */
    public static final String COLUMNNAME_JP_ContractDocAmt = "JP_ContractDocAmt";

	/** Set Contract Doc Amt	  */
	public void setJP_ContractDocAmt (BigDecimal JP_ContractDocAmt);

	/** Get Contract Doc Amt	  */
	public BigDecimal getJP_ContractDocAmt();

    /** Column name JP_ContractDocDate */
    public static final String COLUMNNAME_JP_ContractDocDate = "JP_ContractDocDate";

	/** Set Contract Doc Date	  */
	public void setJP_ContractDocDate (Timestamp JP_ContractDocDate);

	/** Get Contract Doc Date	  */
	public Timestamp getJP_ContractDocDate();

    /** Column name JP_ContractDocDate_From */
    public static final String COLUMNNAME_JP_ContractDocDate_From = "JP_ContractDocDate_From";

	/** Set Contract Doc Date(From)	  */
	public void setJP_ContractDocDate_From (Timestamp JP_ContractDocDate_From);

	/** Get Contract Doc Date(From)	  */
	public Timestamp getJP_ContractDocDate_From();

    /** Column name JP_ContractDocDate_To */
    public static final String COLUMNNAME_JP_ContractDocDate_To = "JP_ContractDocDate_To";

	/** Set Contract Doc Date(To)	  */
	public void setJP_ContractDocDate_To (Timestamp JP_ContractDocDate_To);

	/** Get Contract Doc Date(To)	  */
	public Timestamp getJP_ContractDocDate_To();

    /** Column name JP_ContractExtendPeriod_ID */
    public static final String COLUMNNAME_JP_ContractExtendPeriod_ID = "JP_ContractExtendPeriod_ID";

	/** Set Contract Extend Period	  */
	public void setJP_ContractExtendPeriod_ID (int JP_ContractExtendPeriod_ID);

	/** Get Contract Extend Period	  */
	public int getJP_ContractExtendPeriod_ID();

	public I_JP_ContractExtendPeriod getJP_ContractExtendPeriod() throws RuntimeException;

    /** Column name JP_ContractMonthlyExpenseAmt */
    public static final String COLUMNNAME_JP_ContractMonthlyExpenseAmt = "JP_ContractMonthlyExpenseAmt";

	/** Set Monthly Expense Amt	  */
	public void setJP_ContractMonthlyExpenseAmt (BigDecimal JP_ContractMonthlyExpenseAmt);

	/** Get Monthly Expense Amt	  */
	public BigDecimal getJP_ContractMonthlyExpenseAmt();

    /** Column name JP_ContractMonthlyRevenueAmt */
    public static final String COLUMNNAME_JP_ContractMonthlyRevenueAmt = "JP_ContractMonthlyRevenueAmt";

	/** Set Monthly Revenue Amt	  */
	public void setJP_ContractMonthlyRevenueAmt (BigDecimal JP_ContractMonthlyRevenueAmt);

	/** Get Monthly Revenue Amt	  */
	public BigDecimal getJP_ContractMonthlyRevenueAmt();

    /** Column name JP_ContractPeriodDate_From */
    public static final String COLUMNNAME_JP_ContractPeriodDate_From = "JP_ContractPeriodDate_From";

	/** Set Contract Period Date(From)	  */
	public void setJP_ContractPeriodDate_From (Timestamp JP_ContractPeriodDate_From);

	/** Get Contract Period Date(From)	  */
	public Timestamp getJP_ContractPeriodDate_From();

    /** Column name JP_ContractPeriodDate_To */
    public static final String COLUMNNAME_JP_ContractPeriodDate_To = "JP_ContractPeriodDate_To";

	/** Set Contract Period Date(To)	  */
	public void setJP_ContractPeriodDate_To (Timestamp JP_ContractPeriodDate_To);

	/** Get Contract Period Date(To)	  */
	public Timestamp getJP_ContractPeriodDate_To();

    /** Column name JP_ContractStatus */
    public static final String COLUMNNAME_JP_ContractStatus = "JP_ContractStatus";

	/** Set Contract Status	  */
	public void setJP_ContractStatus (String JP_ContractStatus);

	/** Get Contract Status	  */
	public String getJP_ContractStatus();

    /** Column name JP_ContractT_ID */
    public static final String COLUMNNAME_JP_ContractT_ID = "JP_ContractT_ID";

	/** Set Contract Doc Template	  */
	public void setJP_ContractT_ID (int JP_ContractT_ID);

	/** Get Contract Doc Template	  */
	public int getJP_ContractT_ID();

	public I_JP_ContractT getJP_ContractT() throws RuntimeException;

    /** Column name JP_ContractType */
    public static final String COLUMNNAME_JP_ContractType = "JP_ContractType";

	/** Set Contract Type	  */
	public void setJP_ContractType (String JP_ContractType);

	/** Get Contract Type	  */
	public String getJP_ContractType();

    /** Column name JP_Contract_ID */
    public static final String COLUMNNAME_JP_Contract_ID = "JP_Contract_ID";

	/** Set Contract Document	  */
	public void setJP_Contract_ID (int JP_Contract_ID);

	/** Get Contract Document	  */
	public int getJP_Contract_ID();

    /** Column name JP_Contract_Parent_ID */
    public static final String COLUMNNAME_JP_Contract_Parent_ID = "JP_Contract_Parent_ID";

	/** Set Parent Contract Doc	  */
	public void setJP_Contract_Parent_ID (int JP_Contract_Parent_ID);

	/** Get Parent Contract Doc	  */
	public int getJP_Contract_Parent_ID();

    /** Column name JP_Contract_UU */
    public static final String COLUMNNAME_JP_Contract_UU = "JP_Contract_UU";

	/** Set Contract Doc(UU)	  */
	public void setJP_Contract_UU (String JP_Contract_UU);

	/** Get Contract Doc(UU)	  */
	public String getJP_Contract_UU();

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

    /** Column name Posted */
    public static final String COLUMNNAME_Posted = "Posted";

	/** Set Posted.
	  * Posting status
	  */
	public void setPosted (boolean Posted);

	/** Get Posted.
	  * Posting status
	  */
	public boolean isPosted();

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

    /** Column name ProcessedOn */
    public static final String COLUMNNAME_ProcessedOn = "ProcessedOn";

	/** Set Processed On.
	  * The date+time (expressed in decimal format) when the document has been processed
	  */
	public void setProcessedOn (BigDecimal ProcessedOn);

	/** Get Processed On.
	  * The date+time (expressed in decimal format) when the document has been processed
	  */
	public BigDecimal getProcessedOn();

    /** Column name Processing */
    public static final String COLUMNNAME_Processing = "Processing";

	/** Set Process Now	  */
	public void setProcessing (boolean Processing);

	/** Get Process Now	  */
	public boolean isProcessing();

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
}
