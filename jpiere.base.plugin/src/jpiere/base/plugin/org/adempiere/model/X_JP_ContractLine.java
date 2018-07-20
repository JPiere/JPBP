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

/** Generated Model for JP_ContractLine
 *  @author iDempiere (generated) 
 *  @version Release 5.1 - $Id$ */
public class X_JP_ContractLine extends PO implements I_JP_ContractLine, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20180719L;

    /** Standard Constructor */
    public X_JP_ContractLine (Properties ctx, int JP_ContractLine_ID, String trxName)
    {
      super (ctx, JP_ContractLine_ID, trxName);
      /** if (JP_ContractLine_ID == 0)
        {
			setC_Tax_ID (0);
			setC_UOM_ID (0);
// @#C_UOM_ID@
			setIsCreateDocLineJP (true);
// Y
			setIsDescription (false);
			setJP_ContractContent_ID (0);
			setJP_ContractLine_ID (0);
			setLine (0);
// @SQL=SELECT COALESCE(MAX(Line),0)+10 AS DefaultValue FROM JP_ContractLine WHERE JP_ContractContent_ID=@JP_ContractContent_ID@
			setLineNetAmt (Env.ZERO);
			setMovementQty (Env.ZERO);
			setPriceActual (Env.ZERO);
			setPriceEntered (Env.ZERO);
			setPriceLimit (Env.ZERO);
			setPriceList (Env.ZERO);
			setProcessed (false);
// N
			setQtyEntered (Env.ZERO);
// 1
			setQtyInvoiced (Env.ZERO);
			setQtyOrdered (Env.ZERO);
// 1
        } */
    }

    /** Load Constructor */
    public X_JP_ContractLine (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_JP_ContractLine[")
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
			set_ValueNoCheck (COLUMNNAME_C_BPartner_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_BPartner_ID, Integer.valueOf(C_BPartner_ID));
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
			set_Value (COLUMNNAME_C_ProjectPhase_ID, null);
		else 
			set_Value (COLUMNNAME_C_ProjectPhase_ID, Integer.valueOf(C_ProjectPhase_ID));
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
			set_Value (COLUMNNAME_C_ProjectTask_ID, null);
		else 
			set_Value (COLUMNNAME_C_ProjectTask_ID, Integer.valueOf(C_ProjectTask_ID));
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

	/** Set Date Ordered.
		@param DateOrdered 
		Date of Order
	  */
	public void setDateOrdered (Timestamp DateOrdered)
	{
		set_ValueNoCheck (COLUMNNAME_DateOrdered, DateOrdered);
	}

	/** Get Date Ordered.
		@return Date of Order
	  */
	public Timestamp getDateOrdered () 
	{
		return (Timestamp)get_Value(COLUMNNAME_DateOrdered);
	}

	/** Set Date Promised.
		@param DatePromised 
		Date Order was promised
	  */
	public void setDatePromised (Timestamp DatePromised)
	{
		set_Value (COLUMNNAME_DatePromised, DatePromised);
	}

	/** Get Date Promised.
		@return Date Order was promised
	  */
	public Timestamp getDatePromised () 
	{
		return (Timestamp)get_Value(COLUMNNAME_DatePromised);
	}

	/** Set Promised Delivery Time.
		@param DeliveryTime_Promised 
		Promised days between order and delivery
	  */
	public void setDeliveryTime_Promised (int DeliveryTime_Promised)
	{
		set_Value (COLUMNNAME_DeliveryTime_Promised, Integer.valueOf(DeliveryTime_Promised));
	}

	/** Get Promised Delivery Time.
		@return Promised days between order and delivery
	  */
	public int getDeliveryTime_Promised () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_DeliveryTime_Promised);
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

	/** Set Discount %.
		@param Discount 
		Discount in percent
	  */
	public void setDiscount (BigDecimal Discount)
	{
		set_ValueNoCheck (COLUMNNAME_Discount, Discount);
	}

	/** Get Discount %.
		@return Discount in percent
	  */
	public BigDecimal getDiscount () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_Discount);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Create Doc Line.
		@param IsCreateDocLineJP Create Doc Line	  */
	public void setIsCreateDocLineJP (boolean IsCreateDocLineJP)
	{
		set_Value (COLUMNNAME_IsCreateDocLineJP, Boolean.valueOf(IsCreateDocLineJP));
	}

	/** Get Create Doc Line.
		@return Create Doc Line	  */
	public boolean isCreateDocLineJP () 
	{
		Object oo = get_Value(COLUMNNAME_IsCreateDocLineJP);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Description Only.
		@param IsDescription 
		if true, the line is just description and no transaction
	  */
	public void setIsDescription (boolean IsDescription)
	{
		set_Value (COLUMNNAME_IsDescription, Boolean.valueOf(IsDescription));
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

	public I_M_AttributeSetInstance getJP_ASI_From() throws RuntimeException
    {
		return (I_M_AttributeSetInstance)MTable.get(getCtx(), I_M_AttributeSetInstance.Table_Name)
			.getPO(getJP_ASI_From_ID(), get_TrxName());	}

	/** Set Attribute Info(From).
		@param JP_ASI_From_ID Attribute Info(From)	  */
	public void setJP_ASI_From_ID (int JP_ASI_From_ID)
	{
		if (JP_ASI_From_ID < 1) 
			set_Value (COLUMNNAME_JP_ASI_From_ID, null);
		else 
			set_Value (COLUMNNAME_JP_ASI_From_ID, Integer.valueOf(JP_ASI_From_ID));
	}

	/** Get Attribute Info(From).
		@return Attribute Info(From)	  */
	public int getJP_ASI_From_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ASI_From_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_M_AttributeSetInstance getJP_ASI_To() throws RuntimeException
    {
		return (I_M_AttributeSetInstance)MTable.get(getCtx(), I_M_AttributeSetInstance.Table_Name)
			.getPO(getJP_ASI_To_ID(), get_TrxName());	}

	/** Set Attribute Info(To).
		@param JP_ASI_To_ID Attribute Info(To)	  */
	public void setJP_ASI_To_ID (int JP_ASI_To_ID)
	{
		if (JP_ASI_To_ID < 1) 
			set_Value (COLUMNNAME_JP_ASI_To_ID, null);
		else 
			set_Value (COLUMNNAME_JP_ASI_To_ID, Integer.valueOf(JP_ASI_To_ID));
	}

	/** Get Attribute Info(To).
		@return Attribute Info(To)	  */
	public int getJP_ASI_To_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ASI_To_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** For the Duration of Contract process period = DD */
	public static final String JP_BASEDOCLINEPOLICY_ForTheDurationOfContractProcessPeriod = "DD";
	/** Lump on a certain point of Contract process period = LP */
	public static final String JP_BASEDOCLINEPOLICY_LumpOnACertainPointOfContractProcessPeriod = "LP";
	/** From start Contract process period = PS */
	public static final String JP_BASEDOCLINEPOLICY_FromStartContractProcessPeriod = "PS";
	/** To End Contract process period = PE */
	public static final String JP_BASEDOCLINEPOLICY_ToEndContractProcessPeriod = "PE";
	/** From Start Contract process period to End = PB */
	public static final String JP_BASEDOCLINEPOLICY_FromStartContractProcessPeriodToEnd = "PB";
	/** Set Base Doc Line Policy.
		@param JP_BaseDocLinePolicy Base Doc Line Policy	  */
	public void setJP_BaseDocLinePolicy (String JP_BaseDocLinePolicy)
	{

		set_Value (COLUMNNAME_JP_BaseDocLinePolicy, JP_BaseDocLinePolicy);
	}

	/** Get Base Doc Line Policy.
		@return Base Doc Line Policy	  */
	public String getJP_BaseDocLinePolicy () 
	{
		return (String)get_Value(COLUMNNAME_JP_BaseDocLinePolicy);
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

	public I_JP_ContractCalender getJP_ContractCalender_Inv() throws RuntimeException
    {
		return (I_JP_ContractCalender)MTable.get(getCtx(), I_JP_ContractCalender.Table_Name)
			.getPO(getJP_ContractCalender_Inv_ID(), get_TrxName());	}

	/** Set Contract Calender(Invoice).
		@param JP_ContractCalender_Inv_ID Contract Calender(Invoice)	  */
	public void setJP_ContractCalender_Inv_ID (int JP_ContractCalender_Inv_ID)
	{
		if (JP_ContractCalender_Inv_ID < 1) 
			set_Value (COLUMNNAME_JP_ContractCalender_Inv_ID, null);
		else 
			set_Value (COLUMNNAME_JP_ContractCalender_Inv_ID, Integer.valueOf(JP_ContractCalender_Inv_ID));
	}

	/** Get Contract Calender(Invoice).
		@return Contract Calender(Invoice)	  */
	public int getJP_ContractCalender_Inv_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ContractCalender_Inv_ID);
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

    /** Get Record ID/ColumnName
        @return ID/ColumnName pair
      */
    public KeyNamePair getKeyNamePair() 
    {
        return new KeyNamePair(get_ID(), String.valueOf(getJP_ContractContent_ID()));
    }

	public I_JP_ContractLineT getJP_ContractLineT() throws RuntimeException
    {
		return (I_JP_ContractLineT)MTable.get(getCtx(), I_JP_ContractLineT.Table_Name)
			.getPO(getJP_ContractLineT_ID(), get_TrxName());	}

	/** Set Contract Content Line Template.
		@param JP_ContractLineT_ID Contract Content Line Template	  */
	public void setJP_ContractLineT_ID (int JP_ContractLineT_ID)
	{
		if (JP_ContractLineT_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_JP_ContractLineT_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_JP_ContractLineT_ID, Integer.valueOf(JP_ContractLineT_ID));
	}

	/** Get Contract Content Line Template.
		@return Contract Content Line Template	  */
	public int getJP_ContractLineT_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ContractLineT_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

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

	/** Set Contract Content Line(UU).
		@param JP_ContractLine_UU Contract Content Line(UU)	  */
	public void setJP_ContractLine_UU (String JP_ContractLine_UU)
	{
		set_ValueNoCheck (COLUMNNAME_JP_ContractLine_UU, JP_ContractLine_UU);
	}

	/** Get Contract Content Line(UU).
		@return Contract Content Line(UU)	  */
	public String getJP_ContractLine_UU () 
	{
		return (String)get_Value(COLUMNNAME_JP_ContractLine_UU);
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

	public I_JP_ContractProcess getJP_ContractProcess_Inv() throws RuntimeException
    {
		return (I_JP_ContractProcess)MTable.get(getCtx(), I_JP_ContractProcess.Table_Name)
			.getPO(getJP_ContractProcess_Inv_ID(), get_TrxName());	}

	/** Set Contract Process(Invoice).
		@param JP_ContractProcess_Inv_ID Contract Process(Invoice)	  */
	public void setJP_ContractProcess_Inv_ID (int JP_ContractProcess_Inv_ID)
	{
		if (JP_ContractProcess_Inv_ID < 1) 
			set_Value (COLUMNNAME_JP_ContractProcess_Inv_ID, null);
		else 
			set_Value (COLUMNNAME_JP_ContractProcess_Inv_ID, Integer.valueOf(JP_ContractProcess_Inv_ID));
	}

	/** Get Contract Process(Invoice).
		@return Contract Process(Invoice)	  */
	public int getJP_ContractProcess_Inv_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ContractProcess_Inv_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_JP_ContractLine getJP_CounterContractLine() throws RuntimeException
    {
		return (I_JP_ContractLine)MTable.get(getCtx(), I_JP_ContractLine.Table_Name)
			.getPO(getJP_CounterContractLine_ID(), get_TrxName());	}

	/** Set Counter Contract Content Line.
		@param JP_CounterContractLine_ID Counter Contract Content Line	  */
	public void setJP_CounterContractLine_ID (int JP_CounterContractLine_ID)
	{
		if (JP_CounterContractLine_ID < 1) 
			set_Value (COLUMNNAME_JP_CounterContractLine_ID, null);
		else 
			set_Value (COLUMNNAME_JP_CounterContractLine_ID, Integer.valueOf(JP_CounterContractLine_ID));
	}

	/** Get Counter Contract Content Line.
		@return Counter Contract Content Line	  */
	public int getJP_CounterContractLine_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_CounterContractLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** For the Duration of Contract process period = DD */
	public static final String JP_DERIVATIVEDOCPOLICY_INOUT_ForTheDurationOfContractProcessPeriod = "DD";
	/** Lump on a certain point of Contract process period = LP */
	public static final String JP_DERIVATIVEDOCPOLICY_INOUT_LumpOnACertainPointOfContractProcessPeriod = "LP";
	/** From start Contract process period = PS */
	public static final String JP_DERIVATIVEDOCPOLICY_INOUT_FromStartContractProcessPeriod = "PS";
	/** To End Contract process period = PE */
	public static final String JP_DERIVATIVEDOCPOLICY_INOUT_ToEndContractProcessPeriod = "PE";
	/** From Start Contract process period to End = PB */
	public static final String JP_DERIVATIVEDOCPOLICY_INOUT_FromStartContractProcessPeriodToEnd = "PB";
	/** Set Derivative Doc Policy(In/Out).
		@param JP_DerivativeDocPolicy_InOut Derivative Doc Policy(In/Out)	  */
	public void setJP_DerivativeDocPolicy_InOut (String JP_DerivativeDocPolicy_InOut)
	{

		set_Value (COLUMNNAME_JP_DerivativeDocPolicy_InOut, JP_DerivativeDocPolicy_InOut);
	}

	/** Get Derivative Doc Policy(In/Out).
		@return Derivative Doc Policy(In/Out)	  */
	public String getJP_DerivativeDocPolicy_InOut () 
	{
		return (String)get_Value(COLUMNNAME_JP_DerivativeDocPolicy_InOut);
	}

	/** For the Duration of Contract process period = DD */
	public static final String JP_DERIVATIVEDOCPOLICY_INV_ForTheDurationOfContractProcessPeriod = "DD";
	/** Lump on a certain point of Contract process period = LP */
	public static final String JP_DERIVATIVEDOCPOLICY_INV_LumpOnACertainPointOfContractProcessPeriod = "LP";
	/** From start Contract process period = PS */
	public static final String JP_DERIVATIVEDOCPOLICY_INV_FromStartContractProcessPeriod = "PS";
	/** To End Contract process period = PE */
	public static final String JP_DERIVATIVEDOCPOLICY_INV_ToEndContractProcessPeriod = "PE";
	/** From Start Contract process period to End = PB */
	public static final String JP_DERIVATIVEDOCPOLICY_INV_FromStartContractProcessPeriodToEnd = "PB";
	/** Set Derivative Doc Policy(Invoice).
		@param JP_DerivativeDocPolicy_Inv Derivative Doc Policy(Invoice)	  */
	public void setJP_DerivativeDocPolicy_Inv (String JP_DerivativeDocPolicy_Inv)
	{

		set_Value (COLUMNNAME_JP_DerivativeDocPolicy_Inv, JP_DerivativeDocPolicy_Inv);
	}

	/** Get Derivative Doc Policy(Invoice).
		@return Derivative Doc Policy(Invoice)	  */
	public String getJP_DerivativeDocPolicy_Inv () 
	{
		return (String)get_Value(COLUMNNAME_JP_DerivativeDocPolicy_Inv);
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

	public org.compiere.model.I_M_Locator getJP_Locator() throws RuntimeException
    {
		return (org.compiere.model.I_M_Locator)MTable.get(getCtx(), org.compiere.model.I_M_Locator.Table_Name)
			.getPO(getJP_Locator_ID(), get_TrxName());	}

	/** Set Locator.
		@param JP_Locator_ID Locator	  */
	public void setJP_Locator_ID (int JP_Locator_ID)
	{
		if (JP_Locator_ID < 1) 
			set_Value (COLUMNNAME_JP_Locator_ID, null);
		else 
			set_Value (COLUMNNAME_JP_Locator_ID, Integer.valueOf(JP_Locator_ID));
	}

	/** Get Locator.
		@return Locator	  */
	public int getJP_Locator_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Locator_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_JP_ContractProcPeriod getJP_ProcPeriod_End() throws RuntimeException
    {
		return (I_JP_ContractProcPeriod)MTable.get(getCtx(), I_JP_ContractProcPeriod.Table_Name)
			.getPO(getJP_ProcPeriod_End_ID(), get_TrxName());	}

	/** Set End Contract Process Period.
		@param JP_ProcPeriod_End_ID End Contract Process Period	  */
	public void setJP_ProcPeriod_End_ID (int JP_ProcPeriod_End_ID)
	{
		if (JP_ProcPeriod_End_ID < 1) 
			set_Value (COLUMNNAME_JP_ProcPeriod_End_ID, null);
		else 
			set_Value (COLUMNNAME_JP_ProcPeriod_End_ID, Integer.valueOf(JP_ProcPeriod_End_ID));
	}

	/** Get End Contract Process Period.
		@return End Contract Process Period	  */
	public int getJP_ProcPeriod_End_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ProcPeriod_End_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_JP_ContractProcPeriod getJP_ProcPeriod_End_InOut() throws RuntimeException
    {
		return (I_JP_ContractProcPeriod)MTable.get(getCtx(), I_JP_ContractProcPeriod.Table_Name)
			.getPO(getJP_ProcPeriod_End_InOut_ID(), get_TrxName());	}

	/** Set End Contract Process Period(In/Out).
		@param JP_ProcPeriod_End_InOut_ID End Contract Process Period(In/Out)	  */
	public void setJP_ProcPeriod_End_InOut_ID (int JP_ProcPeriod_End_InOut_ID)
	{
		if (JP_ProcPeriod_End_InOut_ID < 1) 
			set_Value (COLUMNNAME_JP_ProcPeriod_End_InOut_ID, null);
		else 
			set_Value (COLUMNNAME_JP_ProcPeriod_End_InOut_ID, Integer.valueOf(JP_ProcPeriod_End_InOut_ID));
	}

	/** Get End Contract Process Period(In/Out).
		@return End Contract Process Period(In/Out)	  */
	public int getJP_ProcPeriod_End_InOut_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ProcPeriod_End_InOut_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_JP_ContractProcPeriod getJP_ProcPeriod_End_Inv() throws RuntimeException
    {
		return (I_JP_ContractProcPeriod)MTable.get(getCtx(), I_JP_ContractProcPeriod.Table_Name)
			.getPO(getJP_ProcPeriod_End_Inv_ID(), get_TrxName());	}

	/** Set End Contract Process Period(Invoice).
		@param JP_ProcPeriod_End_Inv_ID End Contract Process Period(Invoice)	  */
	public void setJP_ProcPeriod_End_Inv_ID (int JP_ProcPeriod_End_Inv_ID)
	{
		if (JP_ProcPeriod_End_Inv_ID < 1) 
			set_Value (COLUMNNAME_JP_ProcPeriod_End_Inv_ID, null);
		else 
			set_Value (COLUMNNAME_JP_ProcPeriod_End_Inv_ID, Integer.valueOf(JP_ProcPeriod_End_Inv_ID));
	}

	/** Get End Contract Process Period(Invoice).
		@return End Contract Process Period(Invoice)	  */
	public int getJP_ProcPeriod_End_Inv_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ProcPeriod_End_Inv_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_JP_ContractProcPeriod getJP_ProcPeriod_Lump() throws RuntimeException
    {
		return (I_JP_ContractProcPeriod)MTable.get(getCtx(), I_JP_ContractProcPeriod.Table_Name)
			.getPO(getJP_ProcPeriod_Lump_ID(), get_TrxName());	}

	/** Set Period to handle in a lump.
		@param JP_ProcPeriod_Lump_ID Period to handle in a lump	  */
	public void setJP_ProcPeriod_Lump_ID (int JP_ProcPeriod_Lump_ID)
	{
		if (JP_ProcPeriod_Lump_ID < 1) 
			set_Value (COLUMNNAME_JP_ProcPeriod_Lump_ID, null);
		else 
			set_Value (COLUMNNAME_JP_ProcPeriod_Lump_ID, Integer.valueOf(JP_ProcPeriod_Lump_ID));
	}

	/** Get Period to handle in a lump.
		@return Period to handle in a lump	  */
	public int getJP_ProcPeriod_Lump_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ProcPeriod_Lump_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_JP_ContractProcPeriod getJP_ProcPeriod_Lump_InOut() throws RuntimeException
    {
		return (I_JP_ContractProcPeriod)MTable.get(getCtx(), I_JP_ContractProcPeriod.Table_Name)
			.getPO(getJP_ProcPeriod_Lump_InOut_ID(), get_TrxName());	}

	/** Set Period to handle in a lump(In/Out).
		@param JP_ProcPeriod_Lump_InOut_ID Period to handle in a lump(In/Out)	  */
	public void setJP_ProcPeriod_Lump_InOut_ID (int JP_ProcPeriod_Lump_InOut_ID)
	{
		if (JP_ProcPeriod_Lump_InOut_ID < 1) 
			set_Value (COLUMNNAME_JP_ProcPeriod_Lump_InOut_ID, null);
		else 
			set_Value (COLUMNNAME_JP_ProcPeriod_Lump_InOut_ID, Integer.valueOf(JP_ProcPeriod_Lump_InOut_ID));
	}

	/** Get Period to handle in a lump(In/Out).
		@return Period to handle in a lump(In/Out)	  */
	public int getJP_ProcPeriod_Lump_InOut_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ProcPeriod_Lump_InOut_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_JP_ContractProcPeriod getJP_ProcPeriod_Lump_Inv() throws RuntimeException
    {
		return (I_JP_ContractProcPeriod)MTable.get(getCtx(), I_JP_ContractProcPeriod.Table_Name)
			.getPO(getJP_ProcPeriod_Lump_Inv_ID(), get_TrxName());	}

	/** Set Period to handle in a lump(Invoice).
		@param JP_ProcPeriod_Lump_Inv_ID Period to handle in a lump(Invoice)	  */
	public void setJP_ProcPeriod_Lump_Inv_ID (int JP_ProcPeriod_Lump_Inv_ID)
	{
		if (JP_ProcPeriod_Lump_Inv_ID < 1) 
			set_Value (COLUMNNAME_JP_ProcPeriod_Lump_Inv_ID, null);
		else 
			set_Value (COLUMNNAME_JP_ProcPeriod_Lump_Inv_ID, Integer.valueOf(JP_ProcPeriod_Lump_Inv_ID));
	}

	/** Get Period to handle in a lump(Invoice).
		@return Period to handle in a lump(Invoice)	  */
	public int getJP_ProcPeriod_Lump_Inv_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ProcPeriod_Lump_Inv_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_JP_ContractProcPeriod getJP_ProcPeriod_Start() throws RuntimeException
    {
		return (I_JP_ContractProcPeriod)MTable.get(getCtx(), I_JP_ContractProcPeriod.Table_Name)
			.getPO(getJP_ProcPeriod_Start_ID(), get_TrxName());	}

	/** Set Start Contract Process Period.
		@param JP_ProcPeriod_Start_ID Start Contract Process Period	  */
	public void setJP_ProcPeriod_Start_ID (int JP_ProcPeriod_Start_ID)
	{
		if (JP_ProcPeriod_Start_ID < 1) 
			set_Value (COLUMNNAME_JP_ProcPeriod_Start_ID, null);
		else 
			set_Value (COLUMNNAME_JP_ProcPeriod_Start_ID, Integer.valueOf(JP_ProcPeriod_Start_ID));
	}

	/** Get Start Contract Process Period.
		@return Start Contract Process Period	  */
	public int getJP_ProcPeriod_Start_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ProcPeriod_Start_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_JP_ContractProcPeriod getJP_ProcPeriod_Start_InOut() throws RuntimeException
    {
		return (I_JP_ContractProcPeriod)MTable.get(getCtx(), I_JP_ContractProcPeriod.Table_Name)
			.getPO(getJP_ProcPeriod_Start_InOut_ID(), get_TrxName());	}

	/** Set Start Contract Process Period(In/Out).
		@param JP_ProcPeriod_Start_InOut_ID Start Contract Process Period(In/Out)	  */
	public void setJP_ProcPeriod_Start_InOut_ID (int JP_ProcPeriod_Start_InOut_ID)
	{
		if (JP_ProcPeriod_Start_InOut_ID < 1) 
			set_Value (COLUMNNAME_JP_ProcPeriod_Start_InOut_ID, null);
		else 
			set_Value (COLUMNNAME_JP_ProcPeriod_Start_InOut_ID, Integer.valueOf(JP_ProcPeriod_Start_InOut_ID));
	}

	/** Get Start Contract Process Period(In/Out).
		@return Start Contract Process Period(In/Out)	  */
	public int getJP_ProcPeriod_Start_InOut_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ProcPeriod_Start_InOut_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_JP_ContractProcPeriod getJP_ProcPeriod_Start_Inv() throws RuntimeException
    {
		return (I_JP_ContractProcPeriod)MTable.get(getCtx(), I_JP_ContractProcPeriod.Table_Name)
			.getPO(getJP_ProcPeriod_Start_Inv_ID(), get_TrxName());	}

	/** Set Start Contract Process Period(Invoice).
		@param JP_ProcPeriod_Start_Inv_ID Start Contract Process Period(Invoice)	  */
	public void setJP_ProcPeriod_Start_Inv_ID (int JP_ProcPeriod_Start_Inv_ID)
	{
		if (JP_ProcPeriod_Start_Inv_ID < 1) 
			set_Value (COLUMNNAME_JP_ProcPeriod_Start_Inv_ID, null);
		else 
			set_Value (COLUMNNAME_JP_ProcPeriod_Start_Inv_ID, Integer.valueOf(JP_ProcPeriod_Start_Inv_ID));
	}

	/** Get Start Contract Process Period(Invoice).
		@return Start Contract Process Period(Invoice)	  */
	public int getJP_ProcPeriod_Start_Inv_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ProcPeriod_Start_Inv_ID);
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

	public org.compiere.model.I_C_UOM getJP_QtyOrderd_UOM() throws RuntimeException
    {
		return (org.compiere.model.I_C_UOM)MTable.get(getCtx(), org.compiere.model.I_C_UOM.Table_Name)
			.getPO(getJP_QtyOrderd_UOM_ID(), get_TrxName());	}

	/** Set Ordered Qty UOM.
		@param JP_QtyOrderd_UOM_ID 
		Ordered Qty Unit of Measure
	  */
	public void setJP_QtyOrderd_UOM_ID (int JP_QtyOrderd_UOM_ID)
	{
		throw new IllegalArgumentException ("JP_QtyOrderd_UOM_ID is virtual column");	}

	/** Get Ordered Qty UOM.
		@return Ordered Qty Unit of Measure
	  */
	public int getJP_QtyOrderd_UOM_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_QtyOrderd_UOM_ID);
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
		set_ValueNoCheck (COLUMNNAME_Line, Integer.valueOf(Line));
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

	/** Set Movement Quantity.
		@param MovementQty 
		Quantity of a product moved.
	  */
	public void setMovementQty (BigDecimal MovementQty)
	{
		set_Value (COLUMNNAME_MovementQty, MovementQty);
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
		set_Value (COLUMNNAME_PriceActual, PriceActual);
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

	/** Set Invoiced Qty.
		@param QtyInvoiced 
		Invoiced Quantity
	  */
	public void setQtyInvoiced (BigDecimal QtyInvoiced)
	{
		set_Value (COLUMNNAME_QtyInvoiced, QtyInvoiced);
	}

	/** Get Invoiced Qty.
		@return Invoiced Quantity
	  */
	public BigDecimal getQtyInvoiced () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_QtyInvoiced);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Ordered Qty.
		@param QtyOrdered 
		Ordered Quantity
	  */
	public void setQtyOrdered (BigDecimal QtyOrdered)
	{
		set_Value (COLUMNNAME_QtyOrdered, QtyOrdered);
	}

	/** Get Ordered Qty.
		@return Ordered Quantity
	  */
	public BigDecimal getQtyOrdered () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_QtyOrdered);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Resource Assign.
		@param S_ResourceAssignment_ID 
		Resource Assignment
	  */
	public void setS_ResourceAssignment_ID (int S_ResourceAssignment_ID)
	{
		if (S_ResourceAssignment_ID < 1) 
			set_Value (COLUMNNAME_S_ResourceAssignment_ID, null);
		else 
			set_Value (COLUMNNAME_S_ResourceAssignment_ID, Integer.valueOf(S_ResourceAssignment_ID));
	}

	/** Get Resource Assign.
		@return Resource Assignment
	  */
	public int getS_ResourceAssignment_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_S_ResourceAssignment_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
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