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
import java.util.Properties;
import org.compiere.model.*;
import org.compiere.util.Env;

/** Generated Model for JP_ContractPSInOutLine
 *  @author iDempiere (generated) 
 *  @version Release 6.1 - $Id$ */
public class X_JP_ContractPSInOutLine extends PO implements I_JP_ContractPSInOutLine, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20181205L;

    /** Standard Constructor */
    public X_JP_ContractPSInOutLine (Properties ctx, int JP_ContractPSInOutLine_ID, String trxName)
    {
      super (ctx, JP_ContractPSInOutLine_ID, trxName);
      /** if (JP_ContractPSInOutLine_ID == 0)
        {
			setC_Tax_ID (0);
			setC_UOM_ID (0);
			setIsDescription (false);
			setIsFactCreatedJP (false);
// N
			setJP_ContractCalender_InOut_ID (0);
			setJP_ContractLine_ID (0);
// @JP_ContractLine_ID@
			setJP_ContractPSInOutLine_ID (0);
			setJP_ContractPSLine_ID (0);
			setJP_ContractProcPeriod_ID (0);
			setJP_ContractProcSchedule_ID (0);
// @JP_ContractProcSchedule_ID@
			setJP_ContractProcess_InOut_ID (0);
			setLine (0);
// @SQL=SELECT COALESCE(MAX(Line),0)+10 AS DefaultValue FROM JP_ContractPSInOutLine WHERE JP_ContractPSLine_ID=@JP_ContractPSLine_ID@
			setLineNetAmt (Env.ZERO);
			setMovementQty (Env.ZERO);
			setPriceActual (Env.ZERO);
			setPriceEntered (Env.ZERO);
			setPriceLimit (Env.ZERO);
			setPriceList (Env.ZERO);
			setProcessed (false);
			setQtyEntered (Env.ZERO);
        } */
    }

    /** Load Constructor */
    public X_JP_ContractPSInOutLine (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_JP_ContractPSInOutLine[")
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
			set_ValueNoCheck (COLUMNNAME_C_Activity_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_Activity_ID, Integer.valueOf(C_Activity_ID));
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
			set_ValueNoCheck (COLUMNNAME_C_Campaign_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_Campaign_ID, Integer.valueOf(C_Campaign_ID));
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
			set_ValueNoCheck (COLUMNNAME_C_Charge_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_Charge_ID, Integer.valueOf(C_Charge_ID));
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

	public org.compiere.model.I_C_ProjectPhase getC_ProjectPhase() throws RuntimeException
    {
		return (org.compiere.model.I_C_ProjectPhase)MTable.get(getCtx(), org.compiere.model.I_C_ProjectPhase.Table_Name)
			.getPO(getC_ProjectPhase_ID(), get_TrxName());	}

	/** Set Project Phase.
		@param C_ProjectPhase_ID 
		Phase of a Project
	  */
	public void setC_ProjectPhase_ID (int C_ProjectPhase_ID)
	{
		if (C_ProjectPhase_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_C_ProjectPhase_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_ProjectPhase_ID, Integer.valueOf(C_ProjectPhase_ID));
	}

	/** Get Project Phase.
		@return Phase of a Project
	  */
	public int getC_ProjectPhase_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_ProjectPhase_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_ProjectTask getC_ProjectTask() throws RuntimeException
    {
		return (org.compiere.model.I_C_ProjectTask)MTable.get(getCtx(), org.compiere.model.I_C_ProjectTask.Table_Name)
			.getPO(getC_ProjectTask_ID(), get_TrxName());	}

	/** Set Project Task.
		@param C_ProjectTask_ID 
		Actual Project Task in a Phase
	  */
	public void setC_ProjectTask_ID (int C_ProjectTask_ID)
	{
		if (C_ProjectTask_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_C_ProjectTask_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_ProjectTask_ID, Integer.valueOf(C_ProjectTask_ID));
	}

	/** Get Project Task.
		@return Actual Project Task in a Phase
	  */
	public int getC_ProjectTask_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_ProjectTask_ID);
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
			set_ValueNoCheck (COLUMNNAME_C_Project_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_Project_ID, Integer.valueOf(C_Project_ID));
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
			set_ValueNoCheck (COLUMNNAME_C_UOM_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_UOM_ID, Integer.valueOf(C_UOM_ID));
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

	/** Set Description Only.
		@param IsDescription 
		if true, the line is just description and no transaction
	  */
	public void setIsDescription (boolean IsDescription)
	{
		set_ValueNoCheck (COLUMNNAME_IsDescription, Boolean.valueOf(IsDescription));
	}

	/** Get Description Only.
		@return if true, the line is just description and no transaction
	  */
	public boolean isDescription () 
	{
		Object oo = get_Value(COLUMNNAME_IsDescription);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Fact was created.
		@param IsFactCreatedJP Fact was created	  */
	public void setIsFactCreatedJP (boolean IsFactCreatedJP)
	{
		set_Value (COLUMNNAME_IsFactCreatedJP, Boolean.valueOf(IsFactCreatedJP));
	}

	/** Get Fact was created.
		@return Fact was created	  */
	public boolean isFactCreatedJP () 
	{
		Object oo = get_Value(COLUMNNAME_IsFactCreatedJP);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	public I_JP_ContractCalender getJP_ContractCalender_InOut() throws RuntimeException
    {
		return (I_JP_ContractCalender)MTable.get(getCtx(), I_JP_ContractCalender.Table_Name)
			.getPO(getJP_ContractCalender_InOut_ID(), get_TrxName());	}

	/** Set Contract Calender(In/Out).
		@param JP_ContractCalender_InOut_ID Contract Calender(In/Out)	  */
	public void setJP_ContractCalender_InOut_ID (int JP_ContractCalender_InOut_ID)
	{
		if (JP_ContractCalender_InOut_ID < 1) 
			set_Value (COLUMNNAME_JP_ContractCalender_InOut_ID, null);
		else 
			set_Value (COLUMNNAME_JP_ContractCalender_InOut_ID, Integer.valueOf(JP_ContractCalender_InOut_ID));
	}

	/** Get Contract Calender(In/Out).
		@return Contract Calender(In/Out)	  */
	public int getJP_ContractCalender_InOut_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ContractCalender_InOut_ID);
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

	/** Set JP_ContractPSInOutLine_UU.
		@param JP_ContractPSInOutLine_UU JP_ContractPSInOutLine_UU	  */
	public void setJP_ContractPSInOutLine_UU (String JP_ContractPSInOutLine_UU)
	{
		set_ValueNoCheck (COLUMNNAME_JP_ContractPSInOutLine_UU, JP_ContractPSInOutLine_UU);
	}

	/** Get JP_ContractPSInOutLine_UU.
		@return JP_ContractPSInOutLine_UU	  */
	public String getJP_ContractPSInOutLine_UU () 
	{
		return (String)get_Value(COLUMNNAME_JP_ContractPSInOutLine_UU);
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
			set_Value (COLUMNNAME_JP_ContractProcPeriod_ID, null);
		else 
			set_Value (COLUMNNAME_JP_ContractProcPeriod_ID, Integer.valueOf(JP_ContractProcPeriod_ID));
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

	public I_JP_ContractProcess getJP_ContractProcess_InOut() throws RuntimeException
    {
		return (I_JP_ContractProcess)MTable.get(getCtx(), I_JP_ContractProcess.Table_Name)
			.getPO(getJP_ContractProcess_InOut_ID(), get_TrxName());	}

	/** Set Contract Process(In/Out).
		@param JP_ContractProcess_InOut_ID Contract Process(In/Out)	  */
	public void setJP_ContractProcess_InOut_ID (int JP_ContractProcess_InOut_ID)
	{
		if (JP_ContractProcess_InOut_ID < 1) 
			set_Value (COLUMNNAME_JP_ContractProcess_InOut_ID, null);
		else 
			set_Value (COLUMNNAME_JP_ContractProcess_InOut_ID, Integer.valueOf(JP_ContractProcess_InOut_ID));
	}

	/** Get Contract Process(In/Out).
		@return Contract Process(In/Out)	  */
	public int getJP_ContractProcess_InOut_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ContractProcess_InOut_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
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

	/** Set Line Amount.
		@param LineNetAmt 
		Line Extended Amount (Quantity * Actual Price) without Freight and Charges
	  */
	public void setLineNetAmt (BigDecimal LineNetAmt)
	{
		set_ValueNoCheck (COLUMNNAME_LineNetAmt, LineNetAmt);
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
			set_ValueNoCheck (COLUMNNAME_M_Product_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_M_Product_ID, Integer.valueOf(M_Product_ID));
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

	/** Set Movement Quantity.
		@param MovementQty 
		Quantity of a product moved.
	  */
	public void setMovementQty (BigDecimal MovementQty)
	{
		set_ValueNoCheck (COLUMNNAME_MovementQty, MovementQty);
	}

	/** Get Movement Quantity.
		@return Quantity of a product moved.
	  */
	public BigDecimal getMovementQty () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_MovementQty);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Unit Price.
		@param PriceActual 
		Actual Price 
	  */
	public void setPriceActual (BigDecimal PriceActual)
	{
		set_ValueNoCheck (COLUMNNAME_PriceActual, PriceActual);
	}

	/** Get Unit Price.
		@return Actual Price 
	  */
	public BigDecimal getPriceActual () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_PriceActual);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Price.
		@param PriceEntered 
		Price Entered - the price based on the selected/base UoM
	  */
	public void setPriceEntered (BigDecimal PriceEntered)
	{
		set_Value (COLUMNNAME_PriceEntered, PriceEntered);
	}

	/** Get Price.
		@return Price Entered - the price based on the selected/base UoM
	  */
	public BigDecimal getPriceEntered () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_PriceEntered);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Limit Price.
		@param PriceLimit 
		Lowest price for a product
	  */
	public void setPriceLimit (BigDecimal PriceLimit)
	{
		set_Value (COLUMNNAME_PriceLimit, PriceLimit);
	}

	/** Get Limit Price.
		@return Lowest price for a product
	  */
	public BigDecimal getPriceLimit () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_PriceLimit);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set List Price.
		@param PriceList 
		List Price
	  */
	public void setPriceList (BigDecimal PriceList)
	{
		set_Value (COLUMNNAME_PriceList, PriceList);
	}

	/** Get List Price.
		@return List Price
	  */
	public BigDecimal getPriceList () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_PriceList);
		if (bd == null)
			 return Env.ZERO;
		return bd;
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
}