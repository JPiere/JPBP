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

/** Generated Model for JP_EstimationLine
 *  @author iDempiere (generated) 
 *  @version Release 9 - $Id$ */
@org.adempiere.base.Model(table="JP_EstimationLine")
public class X_JP_EstimationLine extends PO implements I_JP_EstimationLine, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20220822L;

    /** Standard Constructor */
    public X_JP_EstimationLine (Properties ctx, int JP_EstimationLine_ID, String trxName)
    {
      super (ctx, JP_EstimationLine_ID, trxName);
      /** if (JP_EstimationLine_ID == 0)
        {
			setC_Currency_ID (0);
// @C_Currency_ID@
			setC_Tax_ID (0);
			setC_UOM_ID (0);
// @#C_UOM_ID@
			setDateOrdered (new Timestamp( System.currentTimeMillis() ));
// @DateOrdered@
			setDatePromised (new Timestamp( System.currentTimeMillis() ));
// @DatePromised@
			setFreightAmt (Env.ZERO);
			setIsDescription (false);
// N
			setJP_EstimationLine_ID (0);
			setJP_Estimation_ID (0);
			setLine (0);
// @SQL=SELECT COALESCE(MAX(Line),0)+10 AS DefaultValue FROM JP_EstimationLine WHERE JP_Estimation_ID=@JP_Estimation_ID@
			setLineNetAmt (Env.ZERO);
			setM_AttributeSetInstance_ID (0);
			setM_Warehouse_ID (0);
// @M_Warehouse_ID@
			setPriceActual (Env.ZERO);
			setPriceEntered (Env.ZERO);
			setPriceLimit (Env.ZERO);
			setPriceList (Env.ZERO);
			setQtyDelivered (Env.ZERO);
			setQtyEntered (Env.ZERO);
// 1
			setQtyInvoiced (Env.ZERO);
			setQtyLostSales (Env.ZERO);
			setQtyOrdered (Env.ZERO);
// 1
			setQtyReserved (Env.ZERO);
        } */
    }

    /** Standard Constructor */
    public X_JP_EstimationLine (Properties ctx, int JP_EstimationLine_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, JP_EstimationLine_ID, trxName, virtualColumns);
      /** if (JP_EstimationLine_ID == 0)
        {
			setC_Currency_ID (0);
// @C_Currency_ID@
			setC_Tax_ID (0);
			setC_UOM_ID (0);
// @#C_UOM_ID@
			setDateOrdered (new Timestamp( System.currentTimeMillis() ));
// @DateOrdered@
			setDatePromised (new Timestamp( System.currentTimeMillis() ));
// @DatePromised@
			setFreightAmt (Env.ZERO);
			setIsDescription (false);
// N
			setJP_EstimationLine_ID (0);
			setJP_Estimation_ID (0);
			setLine (0);
// @SQL=SELECT COALESCE(MAX(Line),0)+10 AS DefaultValue FROM JP_EstimationLine WHERE JP_Estimation_ID=@JP_Estimation_ID@
			setLineNetAmt (Env.ZERO);
			setM_AttributeSetInstance_ID (0);
			setM_Warehouse_ID (0);
// @M_Warehouse_ID@
			setPriceActual (Env.ZERO);
			setPriceEntered (Env.ZERO);
			setPriceLimit (Env.ZERO);
			setPriceList (Env.ZERO);
			setQtyDelivered (Env.ZERO);
			setQtyEntered (Env.ZERO);
// 1
			setQtyInvoiced (Env.ZERO);
			setQtyLostSales (Env.ZERO);
			setQtyOrdered (Env.ZERO);
// 1
			setQtyReserved (Env.ZERO);
        } */
    }

    /** Load Constructor */
    public X_JP_EstimationLine (Properties ctx, ResultSet rs, String trxName)
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
      StringBuilder sb = new StringBuilder ("X_JP_EstimationLine[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set Trx Organization.
		@param AD_OrgTrx_ID Performing or initiating organization
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
	public int getAD_OrgTrx_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_OrgTrx_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Activity getC_Activity() throws RuntimeException
	{
		return (org.compiere.model.I_C_Activity)MTable.get(getCtx(), org.compiere.model.I_C_Activity.Table_ID)
			.getPO(getC_Activity_ID(), get_TrxName());
	}

	/** Set Activity.
		@param C_Activity_ID Business Activity
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
	public int getC_Activity_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Activity_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_BPartner getC_BPartner() throws RuntimeException
	{
		return (org.compiere.model.I_C_BPartner)MTable.get(getCtx(), org.compiere.model.I_C_BPartner.Table_ID)
			.getPO(getC_BPartner_ID(), get_TrxName());
	}

	/** Set Business Partner.
		@param C_BPartner_ID Identifies a Business Partner
	*/
	public void setC_BPartner_ID (int C_BPartner_ID)
	{
		if (C_BPartner_ID < 1)
			set_ValueNoCheck (COLUMNNAME_C_BPartner_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_C_BPartner_ID, Integer.valueOf(C_BPartner_ID));
	}

	/** Get Business Partner.
		@return Identifies a Business Partner
	  */
	public int getC_BPartner_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_BPartner_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_BPartner_Location getC_BPartner_Location() throws RuntimeException
	{
		return (org.compiere.model.I_C_BPartner_Location)MTable.get(getCtx(), org.compiere.model.I_C_BPartner_Location.Table_ID)
			.getPO(getC_BPartner_Location_ID(), get_TrxName());
	}

	/** Set Partner Location.
		@param C_BPartner_Location_ID Identifies the (ship to) address for this Business Partner
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
	public int getC_BPartner_Location_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_BPartner_Location_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Campaign getC_Campaign() throws RuntimeException
	{
		return (org.compiere.model.I_C_Campaign)MTable.get(getCtx(), org.compiere.model.I_C_Campaign.Table_ID)
			.getPO(getC_Campaign_ID(), get_TrxName());
	}

	/** Set Campaign.
		@param C_Campaign_ID Marketing Campaign
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
	public int getC_Campaign_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Campaign_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Charge getC_Charge() throws RuntimeException
	{
		return (org.compiere.model.I_C_Charge)MTable.get(getCtx(), org.compiere.model.I_C_Charge.Table_ID)
			.getPO(getC_Charge_ID(), get_TrxName());
	}

	/** Set Charge.
		@param C_Charge_ID Additional document charges
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
	public int getC_Charge_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Charge_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Currency getC_Currency() throws RuntimeException
	{
		return (org.compiere.model.I_C_Currency)MTable.get(getCtx(), org.compiere.model.I_C_Currency.Table_ID)
			.getPO(getC_Currency_ID(), get_TrxName());
	}

	/** Set Currency.
		@param C_Currency_ID The Currency for this record
	*/
	public void setC_Currency_ID (int C_Currency_ID)
	{
		if (C_Currency_ID < 1)
			set_ValueNoCheck (COLUMNNAME_C_Currency_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_C_Currency_ID, Integer.valueOf(C_Currency_ID));
	}

	/** Get Currency.
		@return The Currency for this record
	  */
	public int getC_Currency_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Currency_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_ProjectPhase getC_ProjectPhase() throws RuntimeException
	{
		return (org.compiere.model.I_C_ProjectPhase)MTable.get(getCtx(), org.compiere.model.I_C_ProjectPhase.Table_ID)
			.getPO(getC_ProjectPhase_ID(), get_TrxName());
	}

	/** Set Project Phase.
		@param C_ProjectPhase_ID Phase of a Project
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
	public int getC_ProjectPhase_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_ProjectPhase_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_ProjectTask getC_ProjectTask() throws RuntimeException
	{
		return (org.compiere.model.I_C_ProjectTask)MTable.get(getCtx(), org.compiere.model.I_C_ProjectTask.Table_ID)
			.getPO(getC_ProjectTask_ID(), get_TrxName());
	}

	/** Set Project Task.
		@param C_ProjectTask_ID Actual Project Task in a Phase
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
	public int getC_ProjectTask_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_ProjectTask_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Project getC_Project() throws RuntimeException
	{
		return (org.compiere.model.I_C_Project)MTable.get(getCtx(), org.compiere.model.I_C_Project.Table_ID)
			.getPO(getC_Project_ID(), get_TrxName());
	}

	/** Set Project.
		@param C_Project_ID Financial Project
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
	public int getC_Project_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Project_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Tax getC_Tax() throws RuntimeException
	{
		return (org.compiere.model.I_C_Tax)MTable.get(getCtx(), org.compiere.model.I_C_Tax.Table_ID)
			.getPO(getC_Tax_ID(), get_TrxName());
	}

	/** Set Tax.
		@param C_Tax_ID Tax identifier
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
	public int getC_Tax_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Tax_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_UOM getC_UOM() throws RuntimeException
	{
		return (org.compiere.model.I_C_UOM)MTable.get(getCtx(), org.compiere.model.I_C_UOM.Table_ID)
			.getPO(getC_UOM_ID(), get_TrxName());
	}

	/** Set UOM.
		@param C_UOM_ID Unit of Measure
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
	public int getC_UOM_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_UOM_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Date Delivered.
		@param DateDelivered Date when the product was delivered
	*/
	public void setDateDelivered (Timestamp DateDelivered)
	{
		set_ValueNoCheck (COLUMNNAME_DateDelivered, DateDelivered);
	}

	/** Get Date Delivered.
		@return Date when the product was delivered
	  */
	public Timestamp getDateDelivered()
	{
		return (Timestamp)get_Value(COLUMNNAME_DateDelivered);
	}

	/** Set Date Invoiced.
		@param DateInvoiced Date printed on Invoice
	*/
	public void setDateInvoiced (Timestamp DateInvoiced)
	{
		set_ValueNoCheck (COLUMNNAME_DateInvoiced, DateInvoiced);
	}

	/** Get Date Invoiced.
		@return Date printed on Invoice
	  */
	public Timestamp getDateInvoiced()
	{
		return (Timestamp)get_Value(COLUMNNAME_DateInvoiced);
	}

	/** Set Date Ordered.
		@param DateOrdered Date of Order
	*/
	public void setDateOrdered (Timestamp DateOrdered)
	{
		set_Value (COLUMNNAME_DateOrdered, DateOrdered);
	}

	/** Get Date Ordered.
		@return Date of Order
	  */
	public Timestamp getDateOrdered()
	{
		return (Timestamp)get_Value(COLUMNNAME_DateOrdered);
	}

	/** Set Date Promised.
		@param DatePromised Date Order was promised
	*/
	public void setDatePromised (Timestamp DatePromised)
	{
		set_Value (COLUMNNAME_DatePromised, DatePromised);
	}

	/** Get Date Promised.
		@return Date Order was promised
	  */
	public Timestamp getDatePromised()
	{
		return (Timestamp)get_Value(COLUMNNAME_DatePromised);
	}

	/** Set Description.
		@param Description Optional short description of the record
	*/
	public void setDescription (String Description)
	{
		set_Value (COLUMNNAME_Description, Description);
	}

	/** Get Description.
		@return Optional short description of the record
	  */
	public String getDescription()
	{
		return (String)get_Value(COLUMNNAME_Description);
	}

	/** Set Discount %.
		@param Discount Discount in percent
	*/
	public void setDiscount (BigDecimal Discount)
	{
		set_Value (COLUMNNAME_Discount, Discount);
	}

	/** Get Discount %.
		@return Discount in percent
	  */
	public BigDecimal getDiscount()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_Discount);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Freight Amount.
		@param FreightAmt Freight Amount 
	*/
	public void setFreightAmt (BigDecimal FreightAmt)
	{
		set_Value (COLUMNNAME_FreightAmt, FreightAmt);
	}

	/** Get Freight Amount.
		@return Freight Amount 
	  */
	public BigDecimal getFreightAmt()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_FreightAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Description Only.
		@param IsDescription if true, the line is just description and no transaction
	*/
	public void setIsDescription (boolean IsDescription)
	{
		set_Value (COLUMNNAME_IsDescription, Boolean.valueOf(IsDescription));
	}

	/** Get Description Only.
		@return if true, the line is just description and no transaction
	  */
	public boolean isDescription()
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
		return (I_M_AttributeSetInstance)MTable.get(getCtx(), I_M_AttributeSetInstance.Table_ID)
			.getPO(getJP_ASI_From_ID(), get_TrxName());
	}

	/** Set Attribute Info(From).
		@param JP_ASI_From_ID Attribute Info(From)
	*/
	public void setJP_ASI_From_ID (int JP_ASI_From_ID)
	{
		if (JP_ASI_From_ID < 1)
			set_Value (COLUMNNAME_JP_ASI_From_ID, null);
		else
			set_Value (COLUMNNAME_JP_ASI_From_ID, Integer.valueOf(JP_ASI_From_ID));
	}

	/** Get Attribute Info(From).
		@return Attribute Info(From)	  */
	public int getJP_ASI_From_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ASI_From_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_M_AttributeSetInstance getJP_ASI_To() throws RuntimeException
	{
		return (I_M_AttributeSetInstance)MTable.get(getCtx(), I_M_AttributeSetInstance.Table_ID)
			.getPO(getJP_ASI_To_ID(), get_TrxName());
	}

	/** Set Attribute Info(To).
		@param JP_ASI_To_ID Attribute Info(To)
	*/
	public void setJP_ASI_To_ID (int JP_ASI_To_ID)
	{
		if (JP_ASI_To_ID < 1)
			set_Value (COLUMNNAME_JP_ASI_To_ID, null);
		else
			set_Value (COLUMNNAME_JP_ASI_To_ID, Integer.valueOf(JP_ASI_To_ID));
	}

	/** Get Attribute Info(To).
		@return Attribute Info(To)	  */
	public int getJP_ASI_To_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ASI_To_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Communication Column.
		@param JP_CommunicationColumn Communication Column
	*/
	public void setJP_CommunicationColumn (String JP_CommunicationColumn)
	{
		set_Value (COLUMNNAME_JP_CommunicationColumn, JP_CommunicationColumn);
	}

	/** Get Communication Column.
		@return Communication Column	  */
	public String getJP_CommunicationColumn()
	{
		return (String)get_Value(COLUMNNAME_JP_CommunicationColumn);
	}

	/** Set Estimation &amp; Handwritten Line.
		@param JP_EstimationLine_ID Estimation &amp; Handwritten Line
	*/
	public void setJP_EstimationLine_ID (int JP_EstimationLine_ID)
	{
		if (JP_EstimationLine_ID < 1)
			set_ValueNoCheck (COLUMNNAME_JP_EstimationLine_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_JP_EstimationLine_ID, Integer.valueOf(JP_EstimationLine_ID));
	}

	/** Get Estimation &amp; Handwritten Line.
		@return Estimation &amp; Handwritten Line	  */
	public int getJP_EstimationLine_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_EstimationLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set JP_EstimationLine_UU.
		@param JP_EstimationLine_UU JP_EstimationLine_UU
	*/
	public void setJP_EstimationLine_UU (String JP_EstimationLine_UU)
	{
		set_Value (COLUMNNAME_JP_EstimationLine_UU, JP_EstimationLine_UU);
	}

	/** Get JP_EstimationLine_UU.
		@return JP_EstimationLine_UU	  */
	public String getJP_EstimationLine_UU()
	{
		return (String)get_Value(COLUMNNAME_JP_EstimationLine_UU);
	}

	public I_JP_Estimation getJP_Estimation() throws RuntimeException
	{
		return (I_JP_Estimation)MTable.get(getCtx(), I_JP_Estimation.Table_ID)
			.getPO(getJP_Estimation_ID(), get_TrxName());
	}

	/** Set Estimation &amp; Handwritten.
		@param JP_Estimation_ID Estimation &amp; Handwritten
	*/
	public void setJP_Estimation_ID (int JP_Estimation_ID)
	{
		if (JP_Estimation_ID < 1)
			set_ValueNoCheck (COLUMNNAME_JP_Estimation_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_JP_Estimation_ID, Integer.valueOf(JP_Estimation_ID));
	}

	/** Get Estimation &amp; Handwritten.
		@return Estimation &amp; Handwritten	  */
	public int getJP_Estimation_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Estimation_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_Locator getJP_LocatorFrom() throws RuntimeException
	{
		return (org.compiere.model.I_M_Locator)MTable.get(getCtx(), org.compiere.model.I_M_Locator.Table_ID)
			.getPO(getJP_LocatorFrom_ID(), get_TrxName());
	}

	/** Set Locator(From).
		@param JP_LocatorFrom_ID Locator(From)
	*/
	public void setJP_LocatorFrom_ID (int JP_LocatorFrom_ID)
	{
		if (JP_LocatorFrom_ID < 1)
			set_Value (COLUMNNAME_JP_LocatorFrom_ID, null);
		else
			set_Value (COLUMNNAME_JP_LocatorFrom_ID, Integer.valueOf(JP_LocatorFrom_ID));
	}

	/** Get Locator(From).
		@return Locator(From)	  */
	public int getJP_LocatorFrom_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_LocatorFrom_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_Locator getJP_LocatorTo() throws RuntimeException
	{
		return (org.compiere.model.I_M_Locator)MTable.get(getCtx(), org.compiere.model.I_M_Locator.Table_ID)
			.getPO(getJP_LocatorTo_ID(), get_TrxName());
	}

	/** Set Locator(To).
		@param JP_LocatorTo_ID Locator(To)
	*/
	public void setJP_LocatorTo_ID (int JP_LocatorTo_ID)
	{
		if (JP_LocatorTo_ID < 1)
			set_Value (COLUMNNAME_JP_LocatorTo_ID, null);
		else
			set_Value (COLUMNNAME_JP_LocatorTo_ID, Integer.valueOf(JP_LocatorTo_ID));
	}

	/** Get Locator(To).
		@return Locator(To)	  */
	public int getJP_LocatorTo_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_LocatorTo_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_Locator getJP_Locator() throws RuntimeException
	{
		return (org.compiere.model.I_M_Locator)MTable.get(getCtx(), org.compiere.model.I_M_Locator.Table_ID)
			.getPO(getJP_Locator_ID(), get_TrxName());
	}

	/** Set Locator.
		@param JP_Locator_ID Locator
	*/
	public void setJP_Locator_ID (int JP_Locator_ID)
	{
		if (JP_Locator_ID < 1)
			set_Value (COLUMNNAME_JP_Locator_ID, null);
		else
			set_Value (COLUMNNAME_JP_Locator_ID, Integer.valueOf(JP_Locator_ID));
	}

	/** Get Locator.
		@return Locator	  */
	public int getJP_Locator_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Locator_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Scheduled Cost.
		@param JP_ScheduledCost Scheduled Cost
	*/
	public void setJP_ScheduledCost (BigDecimal JP_ScheduledCost)
	{
		set_Value (COLUMNNAME_JP_ScheduledCost, JP_ScheduledCost);
	}

	/** Get Scheduled Cost.
		@return Scheduled Cost	  */
	public BigDecimal getJP_ScheduledCost()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_JP_ScheduledCost);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Scheduled Cost Line Amt.
		@param JP_ScheduledCostLineAmt Scheduled Cost Line Amt
	*/
	public void setJP_ScheduledCostLineAmt (BigDecimal JP_ScheduledCostLineAmt)
	{
		set_Value (COLUMNNAME_JP_ScheduledCostLineAmt, JP_ScheduledCostLineAmt);
	}

	/** Get Scheduled Cost Line Amt.
		@return Scheduled Cost Line Amt	  */
	public BigDecimal getJP_ScheduledCostLineAmt()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_JP_ScheduledCostLineAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Tax Amt.
		@param JP_TaxAmt Tax Amt
	*/
	public void setJP_TaxAmt (BigDecimal JP_TaxAmt)
	{
		set_Value (COLUMNNAME_JP_TaxAmt, JP_TaxAmt);
	}

	/** Get Tax Amt.
		@return Tax Amt	  */
	public BigDecimal getJP_TaxAmt()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_JP_TaxAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Tax base Amt.
		@param JP_TaxBaseAmt Tax base Amt
	*/
	public void setJP_TaxBaseAmt (BigDecimal JP_TaxBaseAmt)
	{
		set_Value (COLUMNNAME_JP_TaxBaseAmt, JP_TaxBaseAmt);
	}

	/** Get Tax base Amt.
		@return Tax base Amt	  */
	public BigDecimal getJP_TaxBaseAmt()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_JP_TaxBaseAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Line No.
		@param Line Unique line for this document
	*/
	public void setLine (int Line)
	{
		set_Value (COLUMNNAME_Line, Integer.valueOf(Line));
	}

	/** Get Line No.
		@return Unique line for this document
	  */
	public int getLine()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Line);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Line Amount.
		@param LineNetAmt Line Extended Amount (Quantity * Actual Price) without Freight and Charges
	*/
	public void setLineNetAmt (BigDecimal LineNetAmt)
	{
		set_ValueNoCheck (COLUMNNAME_LineNetAmt, LineNetAmt);
	}

	/** Get Line Amount.
		@return Line Extended Amount (Quantity * Actual Price) without Freight and Charges
	  */
	public BigDecimal getLineNetAmt()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_LineNetAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	public org.compiere.model.I_C_OrderLine getLink_OrderLine() throws RuntimeException
	{
		return (org.compiere.model.I_C_OrderLine)MTable.get(getCtx(), org.compiere.model.I_C_OrderLine.Table_ID)
			.getPO(getLink_OrderLine_ID(), get_TrxName());
	}

	/** Set Linked Order Line.
		@param Link_OrderLine_ID This field links a sales order line to the purchase order line that is generated from it.
	*/
	public void setLink_OrderLine_ID (int Link_OrderLine_ID)
	{
		if (Link_OrderLine_ID < 1)
			set_ValueNoCheck (COLUMNNAME_Link_OrderLine_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_Link_OrderLine_ID, Integer.valueOf(Link_OrderLine_ID));
	}

	/** Get Linked Order Line.
		@return This field links a sales order line to the purchase order line that is generated from it.
	  */
	public int getLink_OrderLine_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Link_OrderLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_M_AttributeSetInstance getM_AttributeSetInstance() throws RuntimeException
	{
		return (I_M_AttributeSetInstance)MTable.get(getCtx(), I_M_AttributeSetInstance.Table_ID)
			.getPO(getM_AttributeSetInstance_ID(), get_TrxName());
	}

	/** Set Attribute Info.
		@param M_AttributeSetInstance_ID Product Attribute Set Instance
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
	public int getM_AttributeSetInstance_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_AttributeSetInstance_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_Product getM_Product() throws RuntimeException
	{
		return (org.compiere.model.I_M_Product)MTable.get(getCtx(), org.compiere.model.I_M_Product.Table_ID)
			.getPO(getM_Product_ID(), get_TrxName());
	}

	/** Set Product.
		@param M_Product_ID Product, Service, Item
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
	public int getM_Product_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Product_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

//	public org.compiere.model.I_M_Promotion getM_Promotion() throws RuntimeException
//	{
//		return (org.compiere.model.I_M_Promotion)MTable.get(getCtx(), org.compiere.model.I_M_Promotion.Table_ID)
//			.getPO(getM_Promotion_ID(), get_TrxName());
//	}

	/** Set Promotion.
		@param M_Promotion_ID Promotion
	*/
	public void setM_Promotion_ID (int M_Promotion_ID)
	{
		if (M_Promotion_ID < 1)
			set_Value (COLUMNNAME_M_Promotion_ID, null);
		else
			set_Value (COLUMNNAME_M_Promotion_ID, Integer.valueOf(M_Promotion_ID));
	}

	/** Get Promotion.
		@return Promotion	  */
	public int getM_Promotion_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Promotion_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_Shipper getM_Shipper() throws RuntimeException
	{
		return (org.compiere.model.I_M_Shipper)MTable.get(getCtx(), org.compiere.model.I_M_Shipper.Table_ID)
			.getPO(getM_Shipper_ID(), get_TrxName());
	}

	/** Set Shipper.
		@param M_Shipper_ID Method or manner of product delivery
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
	public int getM_Shipper_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Shipper_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_Warehouse getM_Warehouse() throws RuntimeException
	{
		return (org.compiere.model.I_M_Warehouse)MTable.get(getCtx(), org.compiere.model.I_M_Warehouse.Table_ID)
			.getPO(getM_Warehouse_ID(), get_TrxName());
	}

	/** Set Org Warehouse.
		@param M_Warehouse_ID Storage Warehouse and Service Point
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
	public int getM_Warehouse_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Warehouse_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.eevolution.model.I_PP_Cost_Collector getPP_Cost_Collector() throws RuntimeException
	{
		return (org.eevolution.model.I_PP_Cost_Collector)MTable.get(getCtx(), org.eevolution.model.I_PP_Cost_Collector.Table_ID)
			.getPO(getPP_Cost_Collector_ID(), get_TrxName());
	}

	/** Set Manufacturing Cost Collector.
		@param PP_Cost_Collector_ID Manufacturing Cost Collector
	*/
	public void setPP_Cost_Collector_ID (int PP_Cost_Collector_ID)
	{
		if (PP_Cost_Collector_ID < 1)
			set_Value (COLUMNNAME_PP_Cost_Collector_ID, null);
		else
			set_Value (COLUMNNAME_PP_Cost_Collector_ID, Integer.valueOf(PP_Cost_Collector_ID));
	}

	/** Get Manufacturing Cost Collector.
		@return Manufacturing Cost Collector	  */
	public int getPP_Cost_Collector_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_PP_Cost_Collector_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Unit Price.
		@param PriceActual Actual Price 
	*/
	public void setPriceActual (BigDecimal PriceActual)
	{
		set_ValueNoCheck (COLUMNNAME_PriceActual, PriceActual);
	}

	/** Get Unit Price.
		@return Actual Price 
	  */
	public BigDecimal getPriceActual()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_PriceActual);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Cost Price.
		@param PriceCost Price per Unit of Measure including all indirect costs (Freight, etc.)
	*/
	public void setPriceCost (BigDecimal PriceCost)
	{
		set_Value (COLUMNNAME_PriceCost, PriceCost);
	}

	/** Get Cost Price.
		@return Price per Unit of Measure including all indirect costs (Freight, etc.)
	  */
	public BigDecimal getPriceCost()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_PriceCost);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Price.
		@param PriceEntered Price Entered - the price based on the selected/base UoM
	*/
	public void setPriceEntered (BigDecimal PriceEntered)
	{
		set_Value (COLUMNNAME_PriceEntered, PriceEntered);
	}

	/** Get Price.
		@return Price Entered - the price based on the selected/base UoM
	  */
	public BigDecimal getPriceEntered()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_PriceEntered);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Limit Price.
		@param PriceLimit Lowest price for a product
	*/
	public void setPriceLimit (BigDecimal PriceLimit)
	{
		set_Value (COLUMNNAME_PriceLimit, PriceLimit);
	}

	/** Get Limit Price.
		@return Lowest price for a product
	  */
	public BigDecimal getPriceLimit()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_PriceLimit);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set List Price.
		@param PriceList List Price
	*/
	public void setPriceList (BigDecimal PriceList)
	{
		set_Value (COLUMNNAME_PriceList, PriceList);
	}

	/** Get List Price.
		@return List Price
	  */
	public BigDecimal getPriceList()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_PriceList);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Delivered Qty.
		@param QtyDelivered Delivered Quantity
	*/
	public void setQtyDelivered (BigDecimal QtyDelivered)
	{
		set_ValueNoCheck (COLUMNNAME_QtyDelivered, QtyDelivered);
	}

	/** Get Delivered Qty.
		@return Delivered Quantity
	  */
	public BigDecimal getQtyDelivered()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_QtyDelivered);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Quantity.
		@param QtyEntered The Quantity Entered is based on the selected UoM
	*/
	public void setQtyEntered (BigDecimal QtyEntered)
	{
		set_Value (COLUMNNAME_QtyEntered, QtyEntered);
	}

	/** Get Quantity.
		@return The Quantity Entered is based on the selected UoM
	  */
	public BigDecimal getQtyEntered()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_QtyEntered);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Invoiced Qty.
		@param QtyInvoiced Invoiced Quantity
	*/
	public void setQtyInvoiced (BigDecimal QtyInvoiced)
	{
		set_ValueNoCheck (COLUMNNAME_QtyInvoiced, QtyInvoiced);
	}

	/** Get Invoiced Qty.
		@return Invoiced Quantity
	  */
	public BigDecimal getQtyInvoiced()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_QtyInvoiced);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Lost Sales Qty.
		@param QtyLostSales Quantity of potential sales
	*/
	public void setQtyLostSales (BigDecimal QtyLostSales)
	{
		set_Value (COLUMNNAME_QtyLostSales, QtyLostSales);
	}

	/** Get Lost Sales Qty.
		@return Quantity of potential sales
	  */
	public BigDecimal getQtyLostSales()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_QtyLostSales);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Ordered Qty.
		@param QtyOrdered Ordered Quantity
	*/
	public void setQtyOrdered (BigDecimal QtyOrdered)
	{
		set_Value (COLUMNNAME_QtyOrdered, QtyOrdered);
	}

	/** Get Ordered Qty.
		@return Ordered Quantity
	  */
	public BigDecimal getQtyOrdered()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_QtyOrdered);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Reserved Qty.
		@param QtyReserved Reserved Quantity
	*/
	public void setQtyReserved (BigDecimal QtyReserved)
	{
		set_ValueNoCheck (COLUMNNAME_QtyReserved, QtyReserved);
	}

	/** Get Reserved Qty.
		@return Reserved Quantity
	  */
	public BigDecimal getQtyReserved()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_QtyReserved);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Revenue Recognition Amt.
		@param RRAmt Revenue Recognition Amount
	*/
	public void setRRAmt (BigDecimal RRAmt)
	{
		set_Value (COLUMNNAME_RRAmt, RRAmt);
	}

	/** Get Revenue Recognition Amt.
		@return Revenue Recognition Amount
	  */
	public BigDecimal getRRAmt()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_RRAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Revenue Recognition Start.
		@param RRStartDate Revenue Recognition Start Date
	*/
	public void setRRStartDate (Timestamp RRStartDate)
	{
		set_Value (COLUMNNAME_RRStartDate, RRStartDate);
	}

	/** Get Revenue Recognition Start.
		@return Revenue Recognition Start Date
	  */
	public Timestamp getRRStartDate()
	{
		return (Timestamp)get_Value(COLUMNNAME_RRStartDate);
	}

	public org.compiere.model.I_C_OrderLine getRef_OrderLine() throws RuntimeException
	{
		return (org.compiere.model.I_C_OrderLine)MTable.get(getCtx(), org.compiere.model.I_C_OrderLine.Table_ID)
			.getPO(getRef_OrderLine_ID(), get_TrxName());
	}

	/** Set Ref Order Line.
		@param Ref_OrderLine_ID Reference to corresponding Sales/Purchase Order
	*/
	public void setRef_OrderLine_ID (int Ref_OrderLine_ID)
	{
		if (Ref_OrderLine_ID < 1)
			set_Value (COLUMNNAME_Ref_OrderLine_ID, null);
		else
			set_Value (COLUMNNAME_Ref_OrderLine_ID, Integer.valueOf(Ref_OrderLine_ID));
	}

	/** Get Ref Order Line.
		@return Reference to corresponding Sales/Purchase Order
	  */
	public int getRef_OrderLine_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Ref_OrderLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Resource Assign.
		@param S_ResourceAssignment_ID Resource Assignment
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
	public int getS_ResourceAssignment_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_S_ResourceAssignment_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_ElementValue getUser1() throws RuntimeException
	{
		return (org.compiere.model.I_C_ElementValue)MTable.get(getCtx(), org.compiere.model.I_C_ElementValue.Table_ID)
			.getPO(getUser1_ID(), get_TrxName());
	}

	/** Set User Element List 1.
		@param User1_ID User defined list element #1
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
	public int getUser1_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_User1_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_ElementValue getUser2() throws RuntimeException
	{
		return (org.compiere.model.I_C_ElementValue)MTable.get(getCtx(), org.compiere.model.I_C_ElementValue.Table_ID)
			.getPO(getUser2_ID(), get_TrxName());
	}

	/** Set User Element List 2.
		@param User2_ID User defined list element #2
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
	public int getUser2_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_User2_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}