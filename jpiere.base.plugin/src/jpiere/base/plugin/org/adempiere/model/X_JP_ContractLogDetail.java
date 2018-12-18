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

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;
import org.compiere.model.*;

/** Generated Model for JP_ContractLogDetail
 *  @author iDempiere (generated) 
 *  @version Release 6.1 - $Id$ */
public class X_JP_ContractLogDetail extends PO implements I_JP_ContractLogDetail, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20181209L;

    /** Standard Constructor */
    public X_JP_ContractLogDetail (Properties ctx, int JP_ContractLogDetail_ID, String trxName)
    {
      super (ctx, JP_ContractLogDetail_ID, trxName);
      /** if (JP_ContractLogDetail_ID == 0)
        {
			setJP_ContractLogDetail_ID (0);
			setJP_Processing1 (null);
// N
			setJP_Processing2 (null);
// N
			setProcessed (false);
// N
			setProcessing (false);
// N
        } */
    }

    /** Load Constructor */
    public X_JP_ContractLogDetail (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 3 - Client - Org 
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
      StringBuffer sb = new StringBuffer ("X_JP_ContractLogDetail[")
        .append(get_ID()).append("]");
      return sb.toString();
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

	public org.compiere.model.I_C_InvoiceLine getC_InvoiceLine() throws RuntimeException
    {
		return (org.compiere.model.I_C_InvoiceLine)MTable.get(getCtx(), org.compiere.model.I_C_InvoiceLine.Table_Name)
			.getPO(getC_InvoiceLine_ID(), get_TrxName());	}

	/** Set Invoice Line.
		@param C_InvoiceLine_ID 
		Invoice Detail Line
	  */
	public void setC_InvoiceLine_ID (int C_InvoiceLine_ID)
	{
		if (C_InvoiceLine_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_C_InvoiceLine_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_InvoiceLine_ID, Integer.valueOf(C_InvoiceLine_ID));
	}

	/** Get Invoice Line.
		@return Invoice Detail Line
	  */
	public int getC_InvoiceLine_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_InvoiceLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Invoice getC_Invoice() throws RuntimeException
    {
		return (org.compiere.model.I_C_Invoice)MTable.get(getCtx(), org.compiere.model.I_C_Invoice.Table_Name)
			.getPO(getC_Invoice_ID(), get_TrxName());	}

	/** Set Invoice.
		@param C_Invoice_ID 
		Invoice Identifier
	  */
	public void setC_Invoice_ID (int C_Invoice_ID)
	{
		if (C_Invoice_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_C_Invoice_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_Invoice_ID, Integer.valueOf(C_Invoice_ID));
	}

	/** Get Invoice.
		@return Invoice Identifier
	  */
	public int getC_Invoice_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Invoice_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_OrderLine getC_OrderLine() throws RuntimeException
    {
		return (org.compiere.model.I_C_OrderLine)MTable.get(getCtx(), org.compiere.model.I_C_OrderLine.Table_Name)
			.getPO(getC_OrderLine_ID(), get_TrxName());	}

	/** Set Sales Order Line.
		@param C_OrderLine_ID 
		Sales Order Line
	  */
	public void setC_OrderLine_ID (int C_OrderLine_ID)
	{
		if (C_OrderLine_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_C_OrderLine_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_OrderLine_ID, Integer.valueOf(C_OrderLine_ID));
	}

	/** Get Sales Order Line.
		@return Sales Order Line
	  */
	public int getC_OrderLine_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_OrderLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Order getC_Order() throws RuntimeException
    {
		return (org.compiere.model.I_C_Order)MTable.get(getCtx(), org.compiere.model.I_C_Order.Table_Name)
			.getPO(getC_Order_ID(), get_TrxName());	}

	/** Set Order.
		@param C_Order_ID 
		Order
	  */
	public void setC_Order_ID (int C_Order_ID)
	{
		if (C_Order_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_C_Order_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_Order_ID, Integer.valueOf(C_Order_ID));
	}

	/** Get Order.
		@return Order
	  */
	public int getC_Order_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Order_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Description.
		@param Description 
		Optional short description of the record
	  */
	public void setDescription (String Description)
	{
		set_ValueNoCheck (COLUMNNAME_Description, Description);
	}

	/** Get Description.
		@return Optional short description of the record
	  */
	public String getDescription () 
	{
		return (String)get_Value(COLUMNNAME_Description);
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

	/** Set Confirmed.
		@param JP_Confirmed Confirmed	  */
	public void setJP_Confirmed (Timestamp JP_Confirmed)
	{
		set_Value (COLUMNNAME_JP_Confirmed, JP_Confirmed);
	}

	/** Get Confirmed.
		@return Confirmed	  */
	public Timestamp getJP_Confirmed () 
	{
		return (Timestamp)get_Value(COLUMNNAME_JP_Confirmed);
	}

	public org.compiere.model.I_AD_User getJP_Confirme() throws RuntimeException
    {
		return (org.compiere.model.I_AD_User)MTable.get(getCtx(), org.compiere.model.I_AD_User.Table_Name)
			.getPO(getJP_ConfirmedBy(), get_TrxName());	}

	/** Set Confirmed By.
		@param JP_ConfirmedBy Confirmed By	  */
	public void setJP_ConfirmedBy (int JP_ConfirmedBy)
	{
		set_Value (COLUMNNAME_JP_ConfirmedBy, Integer.valueOf(JP_ConfirmedBy));
	}

	/** Get Confirmed By.
		@return Confirmed By	  */
	public int getJP_ConfirmedBy () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ConfirmedBy);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_JP_ContractContent getJP_ContractContent() throws RuntimeException
    {
		return (I_JP_ContractContent)MTable.get(getCtx(), I_JP_ContractContent.Table_Name)
			.getPO(getJP_ContractContent_ID(), get_TrxName());	}

	/** Set Contract Content.
		@param JP_ContractContent_ID Contract Content	  */
	public void setJP_ContractContent_ID (int JP_ContractContent_ID)
	{
		if (JP_ContractContent_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_ContractContent_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_ContractContent_ID, Integer.valueOf(JP_ContractContent_ID));
	}

	/** Get Contract Content.
		@return Contract Content	  */
	public int getJP_ContractContent_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ContractContent_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_JP_ContractLine getJP_ContractLine() throws RuntimeException
    {
		return (I_JP_ContractLine)MTable.get(getCtx(), I_JP_ContractLine.Table_Name)
			.getPO(getJP_ContractLine_ID(), get_TrxName());	}

	/** Set Contract Content Line.
		@param JP_ContractLine_ID Contract Content Line	  */
	public void setJP_ContractLine_ID (int JP_ContractLine_ID)
	{
		if (JP_ContractLine_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_ContractLine_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_ContractLine_ID, Integer.valueOf(JP_ContractLine_ID));
	}

	/** Get Contract Content Line.
		@return Contract Content Line	  */
	public int getJP_ContractLine_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ContractLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Contract Management Log Detail.
		@param JP_ContractLogDetail_ID Contract Management Log Detail	  */
	public void setJP_ContractLogDetail_ID (int JP_ContractLogDetail_ID)
	{
		if (JP_ContractLogDetail_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_ContractLogDetail_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_ContractLogDetail_ID, Integer.valueOf(JP_ContractLogDetail_ID));
	}

	/** Get Contract Management Log Detail.
		@return Contract Management Log Detail	  */
	public int getJP_ContractLogDetail_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ContractLogDetail_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set JP_ContractLogDetail_UU.
		@param JP_ContractLogDetail_UU JP_ContractLogDetail_UU	  */
	public void setJP_ContractLogDetail_UU (String JP_ContractLogDetail_UU)
	{
		set_ValueNoCheck (COLUMNNAME_JP_ContractLogDetail_UU, JP_ContractLogDetail_UU);
	}

	/** Get JP_ContractLogDetail_UU.
		@return JP_ContractLogDetail_UU	  */
	public String getJP_ContractLogDetail_UU () 
	{
		return (String)get_Value(COLUMNNAME_JP_ContractLogDetail_UU);
	}

	/** Created Document = A1 */
	public static final String JP_CONTRACTLOGMSG_CreatedDocument = "A1";
	/** Created Document Line = A2 */
	public static final String JP_CONTRACTLOGMSG_CreatedDocumentLine = "A2";
	/** Skipped Contract process for overlap Contract process period = B1 */
	public static final String JP_CONTRACTLOGMSG_SkippedContractProcessForOverlapContractProcessPeriod = "B1";
	/** Unexpected Error = ZZ */
	public static final String JP_CONTRACTLOGMSG_UnexpectedError = "ZZ";
	/** All Contract content line was Skipped = B2 */
	public static final String JP_CONTRACTLOGMSG_AllContractContentLineWasSkipped = "B2";
	/** Not Found Locator = W1 */
	public static final String JP_CONTRACTLOGMSG_NotFoundLocator = "W1";
	/** Over Ordered Quantity = W2 */
	public static final String JP_CONTRACTLOGMSG_OverOrderedQuantity = "W2";
	/** Contract Status Updated = S1 */
	public static final String JP_CONTRACTLOGMSG_ContractStatusUpdated = "S1";
	/** Contract Process Status Updated = S2 */
	public static final String JP_CONTRACTLOGMSG_ContractProcessStatusUpdated = "S2";
	/** Automatic updated of the contract = S3 */
	public static final String JP_CONTRACTLOGMSG_AutomaticUpdatedOfTheContract = "S3";
	/** Skipped for Create Doc Line is False = B3 */
	public static final String JP_CONTRACTLOGMSG_SkippedForCreateDocLineIsFalse = "B3";
	/** Skipped for outside of the Derivative doc period = B4 */
	public static final String JP_CONTRACTLOGMSG_SkippedForOutsideOfTheDerivativeDocPeriod = "B4";
	/** Skipped for outside of the Base doc line period = B5 */
	public static final String JP_CONTRACTLOGMSG_SkippedForOutsideOfTheBaseDocLinePeriod = "B5";
	/** Save Error = Z1 */
	public static final String JP_CONTRACTLOGMSG_SaveError = "Z1";
	/** Document Action Error = Z2 */
	public static final String JP_CONTRACTLOGMSG_DocumentActionError = "Z2";
	/** Skipped for create Derivative Doc  manually = B6 */
	public static final String JP_CONTRACTLOGMSG_SkippedForCreateDerivativeDocManually = "B6";
	/** Skipped for Document Status of Order is not Completed = B7 */
	public static final String JP_CONTRACTLOGMSG_SkippedForDocumentStatusOfOrderIsNotCompleted = "B7";
	/** Skipped for Qty of Contract Line is Zero = B8 */
	public static final String JP_CONTRACTLOGMSG_SkippedForQtyOfContractLineIsZero = "B8";
	/** Could not Create Invoice for invoiced partly = C1 */
	public static final String JP_CONTRACTLOGMSG_CouldNotCreateInvoiceForInvoicedPartly = "C1";
	/** Skipped for Qty to Deliver = C2 */
	public static final String JP_CONTRACTLOGMSG_SkippedForQtyToDeliver = "C2";
	/** Skipped for Qty to Recognized = C3 */
	public static final String JP_CONTRACTLOGMSG_SkippedForQtyToRecognized = "C3";
	/** Skipped = B9 */
	public static final String JP_CONTRACTLOGMSG_Skipped = "B9";
	/** Warning = W9 */
	public static final String JP_CONTRACTLOGMSG_Warning = "W9";
	/** Set Contract Log Message.
		@param JP_ContractLogMsg Contract Log Message	  */
	public void setJP_ContractLogMsg (String JP_ContractLogMsg)
	{

		set_ValueNoCheck (COLUMNNAME_JP_ContractLogMsg, JP_ContractLogMsg);
	}

	/** Get Contract Log Message.
		@return Contract Log Message	  */
	public String getJP_ContractLogMsg () 
	{
		return (String)get_Value(COLUMNNAME_JP_ContractLogMsg);
	}

	public I_JP_ContractLog getJP_ContractLog() throws RuntimeException
    {
		return (I_JP_ContractLog)MTable.get(getCtx(), I_JP_ContractLog.Table_Name)
			.getPO(getJP_ContractLog_ID(), get_TrxName());	}

	/** Set Contract Management Log.
		@param JP_ContractLog_ID Contract Management Log	  */
	public void setJP_ContractLog_ID (int JP_ContractLog_ID)
	{
		if (JP_ContractLog_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_ContractLog_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_ContractLog_ID, Integer.valueOf(JP_ContractLog_ID));
	}

	/** Get Contract Management Log.
		@return Contract Management Log	  */
	public int getJP_ContractLog_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ContractLog_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_JP_ContractPSInOutLine getJP_ContractPSInOutLine() throws RuntimeException
    {
		return (I_JP_ContractPSInOutLine)MTable.get(getCtx(), I_JP_ContractPSInOutLine.Table_Name)
			.getPO(getJP_ContractPSInOutLine_ID(), get_TrxName());	}

	/** Set JP_ContractPSInOutLine.
		@param JP_ContractPSInOutLine_ID JP_ContractPSInOutLine	  */
	public void setJP_ContractPSInOutLine_ID (int JP_ContractPSInOutLine_ID)
	{
		if (JP_ContractPSInOutLine_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_ContractPSInOutLine_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_ContractPSInOutLine_ID, Integer.valueOf(JP_ContractPSInOutLine_ID));
	}

	/** Get JP_ContractPSInOutLine.
		@return JP_ContractPSInOutLine	  */
	public int getJP_ContractPSInOutLine_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ContractPSInOutLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_JP_ContractPSInvoiceLine getJP_ContractPSInvoiceLine() throws RuntimeException
    {
		return (I_JP_ContractPSInvoiceLine)MTable.get(getCtx(), I_JP_ContractPSInvoiceLine.Table_Name)
			.getPO(getJP_ContractPSInvoiceLine_ID(), get_TrxName());	}

	/** Set JP_ContractPSInvoiceLine.
		@param JP_ContractPSInvoiceLine_ID JP_ContractPSInvoiceLine	  */
	public void setJP_ContractPSInvoiceLine_ID (int JP_ContractPSInvoiceLine_ID)
	{
		if (JP_ContractPSInvoiceLine_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_ContractPSInvoiceLine_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_ContractPSInvoiceLine_ID, Integer.valueOf(JP_ContractPSInvoiceLine_ID));
	}

	/** Get JP_ContractPSInvoiceLine.
		@return JP_ContractPSInvoiceLine	  */
	public int getJP_ContractPSInvoiceLine_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ContractPSInvoiceLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_JP_ContractPSLine getJP_ContractPSLine() throws RuntimeException
    {
		return (I_JP_ContractPSLine)MTable.get(getCtx(), I_JP_ContractPSLine.Table_Name)
			.getPO(getJP_ContractPSLine_ID(), get_TrxName());	}

	/** Set Contract Process Schedule Line.
		@param JP_ContractPSLine_ID Contract Process Schedule Line	  */
	public void setJP_ContractPSLine_ID (int JP_ContractPSLine_ID)
	{
		if (JP_ContractPSLine_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_ContractPSLine_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_ContractPSLine_ID, Integer.valueOf(JP_ContractPSLine_ID));
	}

	/** Get Contract Process Schedule Line.
		@return Contract Process Schedule Line	  */
	public int getJP_ContractPSLine_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ContractPSLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_JP_ContractProcPeriod getJP_ContractProcPeriod() throws RuntimeException
    {
		return (I_JP_ContractProcPeriod)MTable.get(getCtx(), I_JP_ContractProcPeriod.Table_Name)
			.getPO(getJP_ContractProcPeriod_ID(), get_TrxName());	}

	/** Set Contract Process Period.
		@param JP_ContractProcPeriod_ID Contract Process Period	  */
	public void setJP_ContractProcPeriod_ID (int JP_ContractProcPeriod_ID)
	{
		if (JP_ContractProcPeriod_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_ContractProcPeriod_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_ContractProcPeriod_ID, Integer.valueOf(JP_ContractProcPeriod_ID));
	}

	/** Get Contract Process Period.
		@return Contract Process Period	  */
	public int getJP_ContractProcPeriod_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ContractProcPeriod_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_JP_ContractProcSchedule getJP_ContractProcSchedule() throws RuntimeException
    {
		return (I_JP_ContractProcSchedule)MTable.get(getCtx(), I_JP_ContractProcSchedule.Table_Name)
			.getPO(getJP_ContractProcSchedule_ID(), get_TrxName());	}

	/** Set Contract Process Schedule Doc.
		@param JP_ContractProcSchedule_ID Contract Process Schedule Doc	  */
	public void setJP_ContractProcSchedule_ID (int JP_ContractProcSchedule_ID)
	{
		if (JP_ContractProcSchedule_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_ContractProcSchedule_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_ContractProcSchedule_ID, Integer.valueOf(JP_ContractProcSchedule_ID));
	}

	/** Get Contract Process Schedule Doc.
		@return Contract Process Schedule Doc	  */
	public int getJP_ContractProcSchedule_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ContractProcSchedule_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** In Progress = IP */
	public static final String JP_CONTRACTPROCSTATUS_FROM_InProgress = "IP";
	/** Invalid = IN */
	public static final String JP_CONTRACTPROCSTATUS_FROM_Invalid = "IN";
	/** Unprocessed = UN */
	public static final String JP_CONTRACTPROCSTATUS_FROM_Unprocessed = "UN";
	/** Processed = PD */
	public static final String JP_CONTRACTPROCSTATUS_FROM_Processed = "PD";
	/** Suspend = SD */
	public static final String JP_CONTRACTPROCSTATUS_FROM_Suspend = "SD";
	/** Set Contract Process Status(From).
		@param JP_ContractProcStatus_From Contract Process Status(From)	  */
	public void setJP_ContractProcStatus_From (String JP_ContractProcStatus_From)
	{

		set_Value (COLUMNNAME_JP_ContractProcStatus_From, JP_ContractProcStatus_From);
	}

	/** Get Contract Process Status(From).
		@return Contract Process Status(From)	  */
	public String getJP_ContractProcStatus_From () 
	{
		return (String)get_Value(COLUMNNAME_JP_ContractProcStatus_From);
	}

	/** In Progress = IP */
	public static final String JP_CONTRACTPROCSTATUS_TO_InProgress = "IP";
	/** Invalid = IN */
	public static final String JP_CONTRACTPROCSTATUS_TO_Invalid = "IN";
	/** Unprocessed = UN */
	public static final String JP_CONTRACTPROCSTATUS_TO_Unprocessed = "UN";
	/** Processed = PD */
	public static final String JP_CONTRACTPROCSTATUS_TO_Processed = "PD";
	/** Suspend = SD */
	public static final String JP_CONTRACTPROCSTATUS_TO_Suspend = "SD";
	/** Set Contract Process Status(To).
		@param JP_ContractProcStatus_To Contract Process Status(To)	  */
	public void setJP_ContractProcStatus_To (String JP_ContractProcStatus_To)
	{

		set_Value (COLUMNNAME_JP_ContractProcStatus_To, JP_ContractProcStatus_To);
	}

	/** Get Contract Process Status(To).
		@return Contract Process Status(To)	  */
	public String getJP_ContractProcStatus_To () 
	{
		return (String)get_Value(COLUMNNAME_JP_ContractProcStatus_To);
	}

	/** Information = FIN */
	public static final String JP_CONTRACTPROCESSTRACELEVEL_Information = "FIN";
	/** Error = ERR */
	public static final String JP_CONTRACTPROCESSTRACELEVEL_Error = "ERR";
	/** Warning = WAR */
	public static final String JP_CONTRACTPROCESSTRACELEVEL_Warning = "WAR";
	/** No log = NON */
	public static final String JP_CONTRACTPROCESSTRACELEVEL_NoLog = "NON";
	/** To Be Confirmed = TBC */
	public static final String JP_CONTRACTPROCESSTRACELEVEL_ToBeConfirmed = "TBC";
	/** Set Contract Process Trace Level.
		@param JP_ContractProcessTraceLevel Contract Process Trace Level	  */
	public void setJP_ContractProcessTraceLevel (String JP_ContractProcessTraceLevel)
	{

		set_ValueNoCheck (COLUMNNAME_JP_ContractProcessTraceLevel, JP_ContractProcessTraceLevel);
	}

	/** Get Contract Process Trace Level.
		@return Contract Process Trace Level	  */
	public String getJP_ContractProcessTraceLevel () 
	{
		return (String)get_Value(COLUMNNAME_JP_ContractProcessTraceLevel);
	}

	public I_JP_ContractProcess getJP_ContractProcess() throws RuntimeException
    {
		return (I_JP_ContractProcess)MTable.get(getCtx(), I_JP_ContractProcess.Table_Name)
			.getPO(getJP_ContractProcess_ID(), get_TrxName());	}

	/** Set Contract Process.
		@param JP_ContractProcess_ID Contract Process	  */
	public void setJP_ContractProcess_ID (int JP_ContractProcess_ID)
	{
		if (JP_ContractProcess_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_ContractProcess_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_ContractProcess_ID, Integer.valueOf(JP_ContractProcess_ID));
	}

	/** Get Contract Process.
		@return Contract Process	  */
	public int getJP_ContractProcess_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ContractProcess_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Prepare = PR */
	public static final String JP_CONTRACTSTATUS_FROM_Prepare = "PR";
	/** Under Contract = UC */
	public static final String JP_CONTRACTSTATUS_FROM_UnderContract = "UC";
	/** Expiration of Contract = EC */
	public static final String JP_CONTRACTSTATUS_FROM_ExpirationOfContract = "EC";
	/** Invalid = IN */
	public static final String JP_CONTRACTSTATUS_FROM_Invalid = "IN";
	/** Set Contract Status(From).
		@param JP_ContractStatus_From Contract Status(From)	  */
	public void setJP_ContractStatus_From (String JP_ContractStatus_From)
	{

		set_Value (COLUMNNAME_JP_ContractStatus_From, JP_ContractStatus_From);
	}

	/** Get Contract Status(From).
		@return Contract Status(From)	  */
	public String getJP_ContractStatus_From () 
	{
		return (String)get_Value(COLUMNNAME_JP_ContractStatus_From);
	}

	/** Prepare = PR */
	public static final String JP_CONTRACTSTATUS_TO_Prepare = "PR";
	/** Under Contract = UC */
	public static final String JP_CONTRACTSTATUS_TO_UnderContract = "UC";
	/** Expiration of Contract = EC */
	public static final String JP_CONTRACTSTATUS_TO_ExpirationOfContract = "EC";
	/** Invalid = IN */
	public static final String JP_CONTRACTSTATUS_TO_Invalid = "IN";
	/** Set Contract Status(To).
		@param JP_ContractStatus_To Contract Status(To)	  */
	public void setJP_ContractStatus_To (String JP_ContractStatus_To)
	{

		set_Value (COLUMNNAME_JP_ContractStatus_To, JP_ContractStatus_To);
	}

	/** Get Contract Status(To).
		@return Contract Status(To)	  */
	public String getJP_ContractStatus_To () 
	{
		return (String)get_Value(COLUMNNAME_JP_ContractStatus_To);
	}

	public I_JP_Contract getJP_Contract() throws RuntimeException
    {
		return (I_JP_Contract)MTable.get(getCtx(), I_JP_Contract.Table_Name)
			.getPO(getJP_Contract_ID(), get_TrxName());	}

	/** Set Contract Document.
		@param JP_Contract_ID Contract Document	  */
	public void setJP_Contract_ID (int JP_Contract_ID)
	{
		if (JP_Contract_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_Contract_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_Contract_ID, Integer.valueOf(JP_Contract_ID));
	}

	/** Get Contract Document.
		@return Contract Document	  */
	public int getJP_Contract_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Contract_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Process Now.
		@param JP_Processing1 Process Now	  */
	public void setJP_Processing1 (String JP_Processing1)
	{
		set_Value (COLUMNNAME_JP_Processing1, JP_Processing1);
	}

	/** Get Process Now.
		@return Process Now	  */
	public String getJP_Processing1 () 
	{
		return (String)get_Value(COLUMNNAME_JP_Processing1);
	}

	/** Set Process Now.
		@param JP_Processing2 Process Now	  */
	public void setJP_Processing2 (String JP_Processing2)
	{
		set_Value (COLUMNNAME_JP_Processing2, JP_Processing2);
	}

	/** Get Process Now.
		@return Process Now	  */
	public String getJP_Processing2 () 
	{
		return (String)get_Value(COLUMNNAME_JP_Processing2);
	}

	public I_JP_RecognitionLine getJP_RecognitionLine() throws RuntimeException
    {
		return (I_JP_RecognitionLine)MTable.get(getCtx(), I_JP_RecognitionLine.Table_Name)
			.getPO(getJP_RecognitionLine_ID(), get_TrxName());	}

	/** Set Revenue and Expense Recognition Line.
		@param JP_RecognitionLine_ID Revenue and Expense Recognition Line	  */
	public void setJP_RecognitionLine_ID (int JP_RecognitionLine_ID)
	{
		if (JP_RecognitionLine_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_RecognitionLine_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_RecognitionLine_ID, Integer.valueOf(JP_RecognitionLine_ID));
	}

	/** Get Revenue and Expense Recognition Line.
		@return Revenue and Expense Recognition Line	  */
	public int getJP_RecognitionLine_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_RecognitionLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_JP_Recognition getJP_Recognition() throws RuntimeException
    {
		return (I_JP_Recognition)MTable.get(getCtx(), I_JP_Recognition.Table_Name)
			.getPO(getJP_Recognition_ID(), get_TrxName());	}

	/** Set Revenue Recognition Doc.
		@param JP_Recognition_ID Revenue Recognition Doc	  */
	public void setJP_Recognition_ID (int JP_Recognition_ID)
	{
		if (JP_Recognition_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_Recognition_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_Recognition_ID, Integer.valueOf(JP_Recognition_ID));
	}

	/** Get Revenue Recognition Doc.
		@return Revenue Recognition Doc	  */
	public int getJP_Recognition_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Recognition_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_InOutLine getM_InOutLine() throws RuntimeException
    {
		return (org.compiere.model.I_M_InOutLine)MTable.get(getCtx(), org.compiere.model.I_M_InOutLine.Table_Name)
			.getPO(getM_InOutLine_ID(), get_TrxName());	}

	/** Set Shipment/Receipt Line.
		@param M_InOutLine_ID 
		Line on Shipment or Receipt document
	  */
	public void setM_InOutLine_ID (int M_InOutLine_ID)
	{
		if (M_InOutLine_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_M_InOutLine_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_M_InOutLine_ID, Integer.valueOf(M_InOutLine_ID));
	}

	/** Get Shipment/Receipt Line.
		@return Line on Shipment or Receipt document
	  */
	public int getM_InOutLine_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_InOutLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_InOut getM_InOut() throws RuntimeException
    {
		return (org.compiere.model.I_M_InOut)MTable.get(getCtx(), org.compiere.model.I_M_InOut.Table_Name)
			.getPO(getM_InOut_ID(), get_TrxName());	}

	/** Set Shipment/Receipt.
		@param M_InOut_ID 
		Material Shipment Document
	  */
	public void setM_InOut_ID (int M_InOut_ID)
	{
		if (M_InOut_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_M_InOut_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_M_InOut_ID, Integer.valueOf(M_InOut_ID));
	}

	/** Get Shipment/Receipt.
		@return Material Shipment Document
	  */
	public int getM_InOut_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_InOut_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
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

	/** Set Record ID.
		@param Record_ID 
		Direct internal record ID
	  */
	public void setRecord_ID (int Record_ID)
	{
		if (Record_ID < 0) 
			set_Value (COLUMNNAME_Record_ID, null);
		else 
			set_Value (COLUMNNAME_Record_ID, Integer.valueOf(Record_ID));
	}

	/** Get Record ID.
		@return Direct internal record ID
	  */
	public int getRecord_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Record_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}