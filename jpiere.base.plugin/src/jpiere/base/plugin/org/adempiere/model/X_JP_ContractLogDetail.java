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
import java.util.Properties;
import org.compiere.model.*;

/** Generated Model for JP_ContractLogDetail
 *  @author iDempiere (generated) 
 *  @version Release 4.1 - $Id$ */
public class X_JP_ContractLogDetail extends PO implements I_JP_ContractLogDetail, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20170922L;

    /** Standard Constructor */
    public X_JP_ContractLogDetail (Properties ctx, int JP_ContractLogDetail_ID, String trxName)
    {
      super (ctx, JP_ContractLogDetail_ID, trxName);
      /** if (JP_ContractLogDetail_ID == 0)
        {
			setJP_ContractLogDetail_ID (0);
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

	/** Create Document = A1 */
	public static final String JP_CONTRACTLOGMSG_CreateDocument = "A1";
	/** Create Document Line = A2 */
	public static final String JP_CONTRACTLOGMSG_CreateDocumentLine = "A2";
	/** Skip Contract process for overlap Contract process period = B1 */
	public static final String JP_CONTRACTLOGMSG_SkipContractProcessForOverlapContractProcessPeriod = "B1";
	/** Unexpected Error = ZZ */
	public static final String JP_CONTRACTLOGMSG_UnexpectedError = "ZZ";
	/** All Contract content line was Skipped = B2 */
	public static final String JP_CONTRACTLOGMSG_AllContractContentLineWasSkipped = "B2";
	/** Not Found Locator = W1 */
	public static final String JP_CONTRACTLOGMSG_NotFoundLocator = "W1";
	/** Over Ordered Quantity = W2 */
	public static final String JP_CONTRACTLOGMSG_OverOrderedQuantity = "W2";
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

	/** Fine = FIN */
	public static final String JP_CONTRACTPROCESSTRACELEVEL_Fine = "FIN";
	/** Error = ERR */
	public static final String JP_CONTRACTPROCESSTRACELEVEL_Error = "ERR";
	/** Warning = WAR */
	public static final String JP_CONTRACTPROCESSTRACELEVEL_Warning = "WAR";
	/** No log = NON */
	public static final String JP_CONTRACTPROCESSTRACELEVEL_NoLog = "NON";
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