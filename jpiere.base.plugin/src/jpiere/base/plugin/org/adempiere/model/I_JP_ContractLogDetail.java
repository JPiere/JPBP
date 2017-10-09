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

/** Generated Interface for JP_ContractLogDetail
 *  @author iDempiere (generated) 
 *  @version Release 4.1
 */
@SuppressWarnings("all")
public interface I_JP_ContractLogDetail 
{

    /** TableName=JP_ContractLogDetail */
    public static final String Table_Name = "JP_ContractLogDetail";

    /** AD_Table_ID=1000201 */
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

    /** Column name C_OrderLine_ID */
    public static final String COLUMNNAME_C_OrderLine_ID = "C_OrderLine_ID";

	/** Set Sales Order Line.
	  * Sales Order Line
	  */
	public void setC_OrderLine_ID (int C_OrderLine_ID);

	/** Get Sales Order Line.
	  * Sales Order Line
	  */
	public int getC_OrderLine_ID();

	public org.compiere.model.I_C_OrderLine getC_OrderLine() throws RuntimeException;

    /** Column name C_Order_ID */
    public static final String COLUMNNAME_C_Order_ID = "C_Order_ID";

	/** Set Order.
	  * Order
	  */
	public void setC_Order_ID (int C_Order_ID);

	/** Get Order.
	  * Order
	  */
	public int getC_Order_ID();

	public org.compiere.model.I_C_Order getC_Order() throws RuntimeException;

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

    /** Column name JP_Confirmed */
    public static final String COLUMNNAME_JP_Confirmed = "JP_Confirmed";

	/** Set Confirmed	  */
	public void setJP_Confirmed (Timestamp JP_Confirmed);

	/** Get Confirmed	  */
	public Timestamp getJP_Confirmed();

    /** Column name JP_ConfirmedBy */
    public static final String COLUMNNAME_JP_ConfirmedBy = "JP_ConfirmedBy";

	/** Set Confirmed By	  */
	public void setJP_ConfirmedBy (int JP_ConfirmedBy);

	/** Get Confirmed By	  */
	public int getJP_ConfirmedBy();

	public org.compiere.model.I_AD_User getJP_Confirme() throws RuntimeException;

    /** Column name JP_ContractContent_ID */
    public static final String COLUMNNAME_JP_ContractContent_ID = "JP_ContractContent_ID";

	/** Set Contract Content	  */
	public void setJP_ContractContent_ID (int JP_ContractContent_ID);

	/** Get Contract Content	  */
	public int getJP_ContractContent_ID();

	public I_JP_ContractContent getJP_ContractContent() throws RuntimeException;

    /** Column name JP_ContractLine_ID */
    public static final String COLUMNNAME_JP_ContractLine_ID = "JP_ContractLine_ID";

	/** Set Contract Content Line	  */
	public void setJP_ContractLine_ID (int JP_ContractLine_ID);

	/** Get Contract Content Line	  */
	public int getJP_ContractLine_ID();

	public I_JP_ContractLine getJP_ContractLine() throws RuntimeException;

    /** Column name JP_ContractLogDetail_ID */
    public static final String COLUMNNAME_JP_ContractLogDetail_ID = "JP_ContractLogDetail_ID";

	/** Set Contract Management Log Detail	  */
	public void setJP_ContractLogDetail_ID (int JP_ContractLogDetail_ID);

	/** Get Contract Management Log Detail	  */
	public int getJP_ContractLogDetail_ID();

    /** Column name JP_ContractLogDetail_UU */
    public static final String COLUMNNAME_JP_ContractLogDetail_UU = "JP_ContractLogDetail_UU";

	/** Set JP_ContractLogDetail_UU	  */
	public void setJP_ContractLogDetail_UU (String JP_ContractLogDetail_UU);

	/** Get JP_ContractLogDetail_UU	  */
	public String getJP_ContractLogDetail_UU();

    /** Column name JP_ContractLogMsg */
    public static final String COLUMNNAME_JP_ContractLogMsg = "JP_ContractLogMsg";

	/** Set Contract Log Message	  */
	public void setJP_ContractLogMsg (String JP_ContractLogMsg);

	/** Get Contract Log Message	  */
	public String getJP_ContractLogMsg();

    /** Column name JP_ContractLog_ID */
    public static final String COLUMNNAME_JP_ContractLog_ID = "JP_ContractLog_ID";

	/** Set Contract Management Log	  */
	public void setJP_ContractLog_ID (int JP_ContractLog_ID);

	/** Get Contract Management Log	  */
	public int getJP_ContractLog_ID();

	public I_JP_ContractLog getJP_ContractLog() throws RuntimeException;

    /** Column name JP_ContractProcPeriod_ID */
    public static final String COLUMNNAME_JP_ContractProcPeriod_ID = "JP_ContractProcPeriod_ID";

	/** Set Contract Process Period	  */
	public void setJP_ContractProcPeriod_ID (int JP_ContractProcPeriod_ID);

	/** Get Contract Process Period	  */
	public int getJP_ContractProcPeriod_ID();

	public I_JP_ContractProcPeriod getJP_ContractProcPeriod() throws RuntimeException;

    /** Column name JP_ContractProcStatus_From */
    public static final String COLUMNNAME_JP_ContractProcStatus_From = "JP_ContractProcStatus_From";

	/** Set Contract Process Status(From)	  */
	public void setJP_ContractProcStatus_From (String JP_ContractProcStatus_From);

	/** Get Contract Process Status(From)	  */
	public String getJP_ContractProcStatus_From();

    /** Column name JP_ContractProcStatus_To */
    public static final String COLUMNNAME_JP_ContractProcStatus_To = "JP_ContractProcStatus_To";

	/** Set Contract Process Status(To)	  */
	public void setJP_ContractProcStatus_To (String JP_ContractProcStatus_To);

	/** Get Contract Process Status(To)	  */
	public String getJP_ContractProcStatus_To();

    /** Column name JP_ContractProcessTraceLevel */
    public static final String COLUMNNAME_JP_ContractProcessTraceLevel = "JP_ContractProcessTraceLevel";

	/** Set Contract Process Trace Level	  */
	public void setJP_ContractProcessTraceLevel (String JP_ContractProcessTraceLevel);

	/** Get Contract Process Trace Level	  */
	public String getJP_ContractProcessTraceLevel();

    /** Column name JP_ContractProcess_ID */
    public static final String COLUMNNAME_JP_ContractProcess_ID = "JP_ContractProcess_ID";

	/** Set Contract Process	  */
	public void setJP_ContractProcess_ID (int JP_ContractProcess_ID);

	/** Get Contract Process	  */
	public int getJP_ContractProcess_ID();

	public I_JP_ContractProcess getJP_ContractProcess() throws RuntimeException;

    /** Column name JP_ContractStatus_From */
    public static final String COLUMNNAME_JP_ContractStatus_From = "JP_ContractStatus_From";

	/** Set Contract Status(From)	  */
	public void setJP_ContractStatus_From (String JP_ContractStatus_From);

	/** Get Contract Status(From)	  */
	public String getJP_ContractStatus_From();

    /** Column name JP_ContractStatus_To */
    public static final String COLUMNNAME_JP_ContractStatus_To = "JP_ContractStatus_To";

	/** Set Contract Status(To)	  */
	public void setJP_ContractStatus_To (String JP_ContractStatus_To);

	/** Get Contract Status(To)	  */
	public String getJP_ContractStatus_To();

    /** Column name JP_Contract_ID */
    public static final String COLUMNNAME_JP_Contract_ID = "JP_Contract_ID";

	/** Set Contract Document	  */
	public void setJP_Contract_ID (int JP_Contract_ID);

	/** Get Contract Document	  */
	public int getJP_Contract_ID();

	public I_JP_Contract getJP_Contract() throws RuntimeException;

    /** Column name JP_Processing1 */
    public static final String COLUMNNAME_JP_Processing1 = "JP_Processing1";

	/** Set Process Now	  */
	public void setJP_Processing1 (String JP_Processing1);

	/** Get Process Now	  */
	public String getJP_Processing1();

    /** Column name JP_Processing2 */
    public static final String COLUMNNAME_JP_Processing2 = "JP_Processing2";

	/** Set Process Now	  */
	public void setJP_Processing2 (String JP_Processing2);

	/** Get Process Now	  */
	public String getJP_Processing2();

    /** Column name JP_RecognitionLine_ID */
    public static final String COLUMNNAME_JP_RecognitionLine_ID = "JP_RecognitionLine_ID";

	/** Set Revenue and Expense Recognition Line	  */
	public void setJP_RecognitionLine_ID (int JP_RecognitionLine_ID);

	/** Get Revenue and Expense Recognition Line	  */
	public int getJP_RecognitionLine_ID();

	public I_JP_RecognitionLine getJP_RecognitionLine() throws RuntimeException;

    /** Column name JP_Recognition_ID */
    public static final String COLUMNNAME_JP_Recognition_ID = "JP_Recognition_ID";

	/** Set Revenue Recognition Doc	  */
	public void setJP_Recognition_ID (int JP_Recognition_ID);

	/** Get Revenue Recognition Doc	  */
	public int getJP_Recognition_ID();

	public I_JP_Recognition getJP_Recognition() throws RuntimeException;

    /** Column name M_InOutLine_ID */
    public static final String COLUMNNAME_M_InOutLine_ID = "M_InOutLine_ID";

	/** Set Shipment/Receipt Line.
	  * Line on Shipment or Receipt document
	  */
	public void setM_InOutLine_ID (int M_InOutLine_ID);

	/** Get Shipment/Receipt Line.
	  * Line on Shipment or Receipt document
	  */
	public int getM_InOutLine_ID();

	public org.compiere.model.I_M_InOutLine getM_InOutLine() throws RuntimeException;

    /** Column name M_InOut_ID */
    public static final String COLUMNNAME_M_InOut_ID = "M_InOut_ID";

	/** Set Shipment/Receipt.
	  * Material Shipment Document
	  */
	public void setM_InOut_ID (int M_InOut_ID);

	/** Get Shipment/Receipt.
	  * Material Shipment Document
	  */
	public int getM_InOut_ID();

	public org.compiere.model.I_M_InOut getM_InOut() throws RuntimeException;

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

    /** Column name Record_ID */
    public static final String COLUMNNAME_Record_ID = "Record_ID";

	/** Set Record ID.
	  * Direct internal record ID
	  */
	public void setRecord_ID (int Record_ID);

	/** Get Record ID.
	  * Direct internal record ID
	  */
	public int getRecord_ID();

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
