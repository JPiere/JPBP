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

/** Generated Model for JP_ContractLineT
 *  @author iDempiere (generated) 
 *  @version Release 9 - $Id$ */
@org.adempiere.base.Model(table="JP_ContractLineT")
public class X_JP_ContractLineT extends PO implements I_JP_ContractLineT, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20220822L;

    /** Standard Constructor */
    public X_JP_ContractLineT (Properties ctx, int JP_ContractLineT_ID, String trxName)
    {
      super (ctx, JP_ContractLineT_ID, trxName);
      /** if (JP_ContractLineT_ID == 0)
        {
			setC_Currency_ID (0);
			setC_Tax_ID (0);
			setC_UOM_ID (0);
// @#C_UOM_ID@
			setDocBaseType (null);
			setIsCreateDocLineJP (true);
// Y
			setIsDescription (false);
			setIsSOTrx (false);
// N
			setIsTaxIncluded (false);
// N
			setJP_ContractLineT_ID (0);
			setJP_ContractType (null);
			setJP_ProcPeriodOffs_End (0);
// 0
			setJP_ProcPeriodOffs_End_InOut (0);
// 0
			setJP_ProcPeriodOffs_End_Inv (0);
// 0
			setJP_ProcPeriodOffs_Lump (0);
// 0
			setJP_ProcPeriodOffs_Lump_InOut (0);
// 0
			setJP_ProcPeriodOffs_Lump_Inv (0);
// 0
			setJP_ProcPeriodOffs_Start (0);
// 0
			setJP_ProcPeriodOffs_Start_InOut (0);
// 0
			setJP_ProcPeriodOffs_Start_Inv (0);
// 0
			setLine (0);
// @SQL=SELECT COALESCE(MAX(Line),0)+10 AS DefaultValue FROM JP_ContractLineT WHERE JP_ContractContentT_ID=@JP_ContractContentT_ID@
			setLineNetAmt (Env.ZERO);
			setM_PriceList_ID (0);
			setMovementQty (Env.ZERO);
// 1
			setName (null);
			setOrderType (null);
// --
			setPriceActual (Env.ZERO);
			setPriceEntered (Env.ZERO);
			setPriceLimit (Env.ZERO);
			setPriceList (Env.ZERO);
			setQtyEntered (Env.ZERO);
// 1
			setQtyInvoiced (Env.ZERO);
// 1
			setQtyOrdered (Env.ZERO);
// 1
        } */
    }

    /** Standard Constructor */
    public X_JP_ContractLineT (Properties ctx, int JP_ContractLineT_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, JP_ContractLineT_ID, trxName, virtualColumns);
      /** if (JP_ContractLineT_ID == 0)
        {
			setC_Currency_ID (0);
			setC_Tax_ID (0);
			setC_UOM_ID (0);
// @#C_UOM_ID@
			setDocBaseType (null);
			setIsCreateDocLineJP (true);
// Y
			setIsDescription (false);
			setIsSOTrx (false);
// N
			setIsTaxIncluded (false);
// N
			setJP_ContractLineT_ID (0);
			setJP_ContractType (null);
			setJP_ProcPeriodOffs_End (0);
// 0
			setJP_ProcPeriodOffs_End_InOut (0);
// 0
			setJP_ProcPeriodOffs_End_Inv (0);
// 0
			setJP_ProcPeriodOffs_Lump (0);
// 0
			setJP_ProcPeriodOffs_Lump_InOut (0);
// 0
			setJP_ProcPeriodOffs_Lump_Inv (0);
// 0
			setJP_ProcPeriodOffs_Start (0);
// 0
			setJP_ProcPeriodOffs_Start_InOut (0);
// 0
			setJP_ProcPeriodOffs_Start_Inv (0);
// 0
			setLine (0);
// @SQL=SELECT COALESCE(MAX(Line),0)+10 AS DefaultValue FROM JP_ContractLineT WHERE JP_ContractContentT_ID=@JP_ContractContentT_ID@
			setLineNetAmt (Env.ZERO);
			setM_PriceList_ID (0);
			setMovementQty (Env.ZERO);
// 1
			setName (null);
			setOrderType (null);
// --
			setPriceActual (Env.ZERO);
			setPriceEntered (Env.ZERO);
			setPriceLimit (Env.ZERO);
			setPriceList (Env.ZERO);
			setQtyEntered (Env.ZERO);
// 1
			setQtyInvoiced (Env.ZERO);
// 1
			setQtyOrdered (Env.ZERO);
// 1
        } */
    }

    /** Load Constructor */
    public X_JP_ContractLineT (Properties ctx, ResultSet rs, String trxName)
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
      StringBuilder sb = new StringBuilder ("X_JP_ContractLineT[")
        .append(get_ID()).append(",Name=").append(getName()).append("]");
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
			set_Value (COLUMNNAME_C_BPartner_ID, null);
		else
			set_Value (COLUMNNAME_C_BPartner_ID, Integer.valueOf(C_BPartner_ID));
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
			set_Value (COLUMNNAME_C_Currency_ID, null);
		else
			set_Value (COLUMNNAME_C_Currency_ID, Integer.valueOf(C_Currency_ID));
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
			set_Value (COLUMNNAME_C_ProjectPhase_ID, null);
		else
			set_Value (COLUMNNAME_C_ProjectPhase_ID, Integer.valueOf(C_ProjectPhase_ID));
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
			set_Value (COLUMNNAME_C_ProjectTask_ID, null);
		else
			set_Value (COLUMNNAME_C_ProjectTask_ID, Integer.valueOf(C_ProjectTask_ID));
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

	/** Set Promised Delivery Time.
		@param DeliveryTime_Promised Promised days between order and delivery
	*/
	public void setDeliveryTime_Promised (int DeliveryTime_Promised)
	{
		set_Value (COLUMNNAME_DeliveryTime_Promised, Integer.valueOf(DeliveryTime_Promised));
	}

	/** Get Promised Delivery Time.
		@return Promised days between order and delivery
	  */
	public int getDeliveryTime_Promised()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_DeliveryTime_Promised);
		if (ii == null)
			 return 0;
		return ii.intValue();
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

	/** DocBaseType AD_Reference_ID=183 */
	public static final int DOCBASETYPE_AD_Reference_ID=183;
	/** AP Credit Memo = APC */
	public static final String DOCBASETYPE_APCreditMemo = "APC";
	/** AP Invoice = API */
	public static final String DOCBASETYPE_APInvoice = "API";
	/** AP Payment = APP */
	public static final String DOCBASETYPE_APPayment = "APP";
	/** AR Credit Memo = ARC */
	public static final String DOCBASETYPE_ARCreditMemo = "ARC";
	/** AR Pro Forma Invoice = ARF */
	public static final String DOCBASETYPE_ARProFormaInvoice = "ARF";
	/** AR Invoice = ARI */
	public static final String DOCBASETYPE_ARInvoice = "ARI";
	/** AR Receipt = ARR */
	public static final String DOCBASETYPE_ARReceipt = "ARR";
	/** Payment Allocation = CMA */
	public static final String DOCBASETYPE_PaymentAllocation = "CMA";
	/** Bank Statement = CMB */
	public static final String DOCBASETYPE_BankStatement = "CMB";
	/** Cash Journal = CMC */
	public static final String DOCBASETYPE_CashJournal = "CMC";
	/** Distribution Order = DOO */
	public static final String DOCBASETYPE_DistributionOrder = "DOO";
	/** Fixed Assets Addition = FAA */
	public static final String DOCBASETYPE_FixedAssetsAddition = "FAA";
	/** Fixed Assets Disposal = FAD */
	public static final String DOCBASETYPE_FixedAssetsDisposal = "FAD";
	/** Fixed Assets Depreciation = FDP */
	public static final String DOCBASETYPE_FixedAssetsDepreciation = "FDP";
	/** GL Document = GLD */
	public static final String DOCBASETYPE_GLDocument = "GLD";
	/** GL Journal = GLJ */
	public static final String DOCBASETYPE_GLJournal = "GLJ";
	/** Payroll = HRP */
	public static final String DOCBASETYPE_Payroll = "HRP";
	/** JPiere Contract Proc Schedule = JCS */
	public static final String DOCBASETYPE_JPiereContractProcSchedule = "JCS";
	/** JPiere PP Doc = JDD */
	public static final String DOCBASETYPE_JPierePPDoc = "JDD";
	/** JPiere PP Fact = JDF */
	public static final String DOCBASETYPE_JPierePPFact = "JDF";
	/** JPiere PP Plan = JDP */
	public static final String DOCBASETYPE_JPierePPPlan = "JDP";
	/** JPiere Inventory Valuation Adjust = JPA */
	public static final String DOCBASETYPE_JPiereInventoryValuationAdjust = "JPA";
	/** JPiere Bill = JPB */
	public static final String DOCBASETYPE_JPiereBill = "JPB";
	/** JPiere Contract Doc = JPC */
	public static final String DOCBASETYPE_JPiereContractDoc = "JPC";
	/** JPiere Estimation = JPE */
	public static final String DOCBASETYPE_JPiereEstimation = "JPE";
	/** JPiere Inventory Valuation Calculate = JPI */
	public static final String DOCBASETYPE_JPiereInventoryValuationCalculate = "JPI";
	/** JPiere Payment Request = JPP */
	public static final String DOCBASETYPE_JPierePaymentRequest = "JPP";
	/** JPiere Revenue Recognition = JPR */
	public static final String DOCBASETYPE_JPiereRevenueRecognition = "JPR";
	/** JPiere Revenue Recognition(Credit Memo) = JPS */
	public static final String DOCBASETYPE_JPiereRevenueRecognitionCreditMemo = "JPS";
	/** JPiere Contract Content = JPT */
	public static final String DOCBASETYPE_JPiereContractContent = "JPT";
	/** JPiere Expense Recognition = JPX */
	public static final String DOCBASETYPE_JPiereExpenseRecognition = "JPX";
	/** JPiere Expense Recognition(Credit Memo) = JPY */
	public static final String DOCBASETYPE_JPiereExpenseRecognitionCreditMemo = "JPY";
	/** Manufacturing Cost Collector = MCC */
	public static final String DOCBASETYPE_ManufacturingCostCollector = "MCC";
	/** Material Physical Inventory = MMI */
	public static final String DOCBASETYPE_MaterialPhysicalInventory = "MMI";
	/** Material Movement = MMM */
	public static final String DOCBASETYPE_MaterialMovement = "MMM";
	/** Material Production = MMP */
	public static final String DOCBASETYPE_MaterialProduction = "MMP";
	/** Material Receipt = MMR */
	public static final String DOCBASETYPE_MaterialReceipt = "MMR";
	/** Material Delivery = MMS */
	public static final String DOCBASETYPE_MaterialDelivery = "MMS";
	/** Maintenance Order = MOF */
	public static final String DOCBASETYPE_MaintenanceOrder = "MOF";
	/** Manufacturing Order = MOP */
	public static final String DOCBASETYPE_ManufacturingOrder = "MOP";
	/** Quality Order = MQO */
	public static final String DOCBASETYPE_QualityOrder = "MQO";
	/** Match Invoice = MXI */
	public static final String DOCBASETYPE_MatchInvoice = "MXI";
	/** Match PO = MXP */
	public static final String DOCBASETYPE_MatchPO = "MXP";
	/** Project Issue = PJI */
	public static final String DOCBASETYPE_ProjectIssue = "PJI";
	/** Purchase Order = POO */
	public static final String DOCBASETYPE_PurchaseOrder = "POO";
	/** Purchase Requisition = POR */
	public static final String DOCBASETYPE_PurchaseRequisition = "POR";
	/** Sales Order = SOO */
	public static final String DOCBASETYPE_SalesOrder = "SOO";
	/** Set Document BaseType.
		@param DocBaseType Logical type of document
	*/
	public void setDocBaseType (String DocBaseType)
	{

		set_ValueNoCheck (COLUMNNAME_DocBaseType, DocBaseType);
	}

	/** Get Document BaseType.
		@return Logical type of document
	  */
	public String getDocBaseType()
	{
		return (String)get_Value(COLUMNNAME_DocBaseType);
	}

	/** Set Create Doc Line.
		@param IsCreateDocLineJP Create Doc Line
	*/
	public void setIsCreateDocLineJP (boolean IsCreateDocLineJP)
	{
		set_Value (COLUMNNAME_IsCreateDocLineJP, Boolean.valueOf(IsCreateDocLineJP));
	}

	/** Get Create Doc Line.
		@return Create Doc Line	  */
	public boolean isCreateDocLineJP()
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

	/** Set Sales Transaction.
		@param IsSOTrx This is a Sales Transaction
	*/
	public void setIsSOTrx (boolean IsSOTrx)
	{
		set_ValueNoCheck (COLUMNNAME_IsSOTrx, Boolean.valueOf(IsSOTrx));
	}

	/** Get Sales Transaction.
		@return This is a Sales Transaction
	  */
	public boolean isSOTrx()
	{
		Object oo = get_Value(COLUMNNAME_IsSOTrx);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Price includes Tax.
		@param IsTaxIncluded Tax is included in the price 
	*/
	public void setIsTaxIncluded (boolean IsTaxIncluded)
	{
		set_ValueNoCheck (COLUMNNAME_IsTaxIncluded, Boolean.valueOf(IsTaxIncluded));
	}

	/** Get Price includes Tax.
		@return Tax is included in the price 
	  */
	public boolean isTaxIncluded()
	{
		Object oo = get_Value(COLUMNNAME_IsTaxIncluded);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** For the Duration of Contract process period = DD */
	public static final String JP_BASEDOCLINEPOLICY_ForTheDurationOfContractProcessPeriod = "DD";
	/** Lump on a certain point of Contract process period = LP */
	public static final String JP_BASEDOCLINEPOLICY_LumpOnACertainPointOfContractProcessPeriod = "LP";
	/** From Start Contract process period to End = PB */
	public static final String JP_BASEDOCLINEPOLICY_FromStartContractProcessPeriodToEnd = "PB";
	/** To End Contract process period = PE */
	public static final String JP_BASEDOCLINEPOLICY_ToEndContractProcessPeriod = "PE";
	/** From start Contract process period = PS */
	public static final String JP_BASEDOCLINEPOLICY_FromStartContractProcessPeriod = "PS";
	/** Set Base Doc Line Policy.
		@param JP_BaseDocLinePolicy Base Doc Line Policy
	*/
	public void setJP_BaseDocLinePolicy (String JP_BaseDocLinePolicy)
	{

		set_Value (COLUMNNAME_JP_BaseDocLinePolicy, JP_BaseDocLinePolicy);
	}

	/** Get Base Doc Line Policy.
		@return Base Doc Line Policy	  */
	public String getJP_BaseDocLinePolicy()
	{
		return (String)get_Value(COLUMNNAME_JP_BaseDocLinePolicy);
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

	public I_JP_ContractCalenderRef getJP_ContractCalRef_InOut() throws RuntimeException
	{
		return (I_JP_ContractCalenderRef)MTable.get(getCtx(), I_JP_ContractCalenderRef.Table_ID)
			.getPO(getJP_ContractCalRef_InOut_ID(), get_TrxName());
	}

	/** Set Contract Calender Ref(In/Out).
		@param JP_ContractCalRef_InOut_ID Contract Calender Ref(In/Out)
	*/
	public void setJP_ContractCalRef_InOut_ID (int JP_ContractCalRef_InOut_ID)
	{
		if (JP_ContractCalRef_InOut_ID < 1)
			set_Value (COLUMNNAME_JP_ContractCalRef_InOut_ID, null);
		else
			set_Value (COLUMNNAME_JP_ContractCalRef_InOut_ID, Integer.valueOf(JP_ContractCalRef_InOut_ID));
	}

	/** Get Contract Calender Ref(In/Out).
		@return Contract Calender Ref(In/Out)	  */
	public int getJP_ContractCalRef_InOut_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ContractCalRef_InOut_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_JP_ContractCalenderRef getJP_ContractCalRef_Inv() throws RuntimeException
	{
		return (I_JP_ContractCalenderRef)MTable.get(getCtx(), I_JP_ContractCalenderRef.Table_ID)
			.getPO(getJP_ContractCalRef_Inv_ID(), get_TrxName());
	}

	/** Set Contract Calender Ref(Invoice).
		@param JP_ContractCalRef_Inv_ID Contract Calender Ref(Invoice)
	*/
	public void setJP_ContractCalRef_Inv_ID (int JP_ContractCalRef_Inv_ID)
	{
		if (JP_ContractCalRef_Inv_ID < 1)
			set_Value (COLUMNNAME_JP_ContractCalRef_Inv_ID, null);
		else
			set_Value (COLUMNNAME_JP_ContractCalRef_Inv_ID, Integer.valueOf(JP_ContractCalRef_Inv_ID));
	}

	/** Get Contract Calender Ref(Invoice).
		@return Contract Calender Ref(Invoice)	  */
	public int getJP_ContractCalRef_Inv_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ContractCalRef_Inv_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_JP_ContractContentT getJP_ContractContentT() throws RuntimeException
	{
		return (I_JP_ContractContentT)MTable.get(getCtx(), I_JP_ContractContentT.Table_ID)
			.getPO(getJP_ContractContentT_ID(), get_TrxName());
	}

	/** Set Contract Content Template.
		@param JP_ContractContentT_ID Contract Content Template
	*/
	public void setJP_ContractContentT_ID (int JP_ContractContentT_ID)
	{
		if (JP_ContractContentT_ID < 1)
			set_ValueNoCheck (COLUMNNAME_JP_ContractContentT_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_JP_ContractContentT_ID, Integer.valueOf(JP_ContractContentT_ID));
	}

	/** Get Contract Content Template.
		@return Contract Content Template	  */
	public int getJP_ContractContentT_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ContractContentT_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Not Take over to renew the contract = NO */
	public static final String JP_CONTRACTL_AUTOUPDATEPOLICY_NotTakeOverToRenewTheContract = "NO";
	/** Take over to renew the contract = TO */
	public static final String JP_CONTRACTL_AUTOUPDATEPOLICY_TakeOverToRenewTheContract = "TO";
	/** Set Auto Update Policy of Line.
		@param JP_ContractL_AutoUpdatePolicy Auto Update Policy of Line
	*/
	public void setJP_ContractL_AutoUpdatePolicy (String JP_ContractL_AutoUpdatePolicy)
	{

		set_Value (COLUMNNAME_JP_ContractL_AutoUpdatePolicy, JP_ContractL_AutoUpdatePolicy);
	}

	/** Get Auto Update Policy of Line.
		@return Auto Update Policy of Line	  */
	public String getJP_ContractL_AutoUpdatePolicy()
	{
		return (String)get_Value(COLUMNNAME_JP_ContractL_AutoUpdatePolicy);
	}

	/** Set Contract Content Line Template.
		@param JP_ContractLineT_ID Contract Content Line Template
	*/
	public void setJP_ContractLineT_ID (int JP_ContractLineT_ID)
	{
		if (JP_ContractLineT_ID < 1)
			set_ValueNoCheck (COLUMNNAME_JP_ContractLineT_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_JP_ContractLineT_ID, Integer.valueOf(JP_ContractLineT_ID));
	}

	/** Get Contract Content Line Template.
		@return Contract Content Line Template	  */
	public int getJP_ContractLineT_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ContractLineT_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Contract Content Line Template(UU).
		@param JP_ContractLineT_UU Contract Content Line Template(UU)
	*/
	public void setJP_ContractLineT_UU (String JP_ContractLineT_UU)
	{
		set_ValueNoCheck (COLUMNNAME_JP_ContractLineT_UU, JP_ContractLineT_UU);
	}

	/** Get Contract Content Line Template(UU).
		@return Contract Content Line Template(UU)	  */
	public String getJP_ContractLineT_UU()
	{
		return (String)get_Value(COLUMNNAME_JP_ContractLineT_UU);
	}

	public I_JP_ContractProcessRef getJP_ContractProcRef_InOut() throws RuntimeException
	{
		return (I_JP_ContractProcessRef)MTable.get(getCtx(), I_JP_ContractProcessRef.Table_ID)
			.getPO(getJP_ContractProcRef_InOut_ID(), get_TrxName());
	}

	/** Set Contract Process Ref(In/Out).
		@param JP_ContractProcRef_InOut_ID Contract Process Ref(In/Out)
	*/
	public void setJP_ContractProcRef_InOut_ID (int JP_ContractProcRef_InOut_ID)
	{
		if (JP_ContractProcRef_InOut_ID < 1)
			set_Value (COLUMNNAME_JP_ContractProcRef_InOut_ID, null);
		else
			set_Value (COLUMNNAME_JP_ContractProcRef_InOut_ID, Integer.valueOf(JP_ContractProcRef_InOut_ID));
	}

	/** Get Contract Process Ref(In/Out).
		@return Contract Process Ref(In/Out)	  */
	public int getJP_ContractProcRef_InOut_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ContractProcRef_InOut_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_JP_ContractProcessRef getJP_ContractProcRef_Inv() throws RuntimeException
	{
		return (I_JP_ContractProcessRef)MTable.get(getCtx(), I_JP_ContractProcessRef.Table_ID)
			.getPO(getJP_ContractProcRef_Inv_ID(), get_TrxName());
	}

	/** Set Contract Process Ref(Invoice).
		@param JP_ContractProcRef_Inv_ID Contract Process Ref(Invoice)
	*/
	public void setJP_ContractProcRef_Inv_ID (int JP_ContractProcRef_Inv_ID)
	{
		if (JP_ContractProcRef_Inv_ID < 1)
			set_Value (COLUMNNAME_JP_ContractProcRef_Inv_ID, null);
		else
			set_Value (COLUMNNAME_JP_ContractProcRef_Inv_ID, Integer.valueOf(JP_ContractProcRef_Inv_ID));
	}

	/** Get Contract Process Ref(Invoice).
		@return Contract Process Ref(Invoice)	  */
	public int getJP_ContractProcRef_Inv_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ContractProcRef_Inv_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** General Contract = GLC */
	public static final String JP_CONTRACTTYPE_GeneralContract = "GLC";
	/** Period Contract = PDC */
	public static final String JP_CONTRACTTYPE_PeriodContract = "PDC";
	/** Spot Contract = STC */
	public static final String JP_CONTRACTTYPE_SpotContract = "STC";
	/** Set Contract Type.
		@param JP_ContractType Contract Type
	*/
	public void setJP_ContractType (String JP_ContractType)
	{

		set_ValueNoCheck (COLUMNNAME_JP_ContractType, JP_ContractType);
	}

	/** Get Contract Type.
		@return Contract Type	  */
	public String getJP_ContractType()
	{
		return (String)get_Value(COLUMNNAME_JP_ContractType);
	}

	/** Create Ship/Receipt &amp; Invoice = BT */
	public static final String JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceiptInvoice = "BT";
	/** Create Ship/Receipt = IO */
	public static final String JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceipt = "IO";
	/** Create Invoice = IV */
	public static final String JP_CREATEDERIVATIVEDOCPOLICY_CreateInvoice = "IV";
	/** Manual = MA */
	public static final String JP_CREATEDERIVATIVEDOCPOLICY_Manual = "MA";
	/** Set Create Derivative Doc Policy.
		@param JP_CreateDerivativeDocPolicy Create Derivative Doc Policy
	*/
	public void setJP_CreateDerivativeDocPolicy (String JP_CreateDerivativeDocPolicy)
	{

		set_ValueNoCheck (COLUMNNAME_JP_CreateDerivativeDocPolicy, JP_CreateDerivativeDocPolicy);
	}

	/** Get Create Derivative Doc Policy.
		@return Create Derivative Doc Policy	  */
	public String getJP_CreateDerivativeDocPolicy()
	{
		return (String)get_Value(COLUMNNAME_JP_CreateDerivativeDocPolicy);
	}

	/** For the Duration of Contract process period = DD */
	public static final String JP_DERIVATIVEDOCPOLICY_INOUT_ForTheDurationOfContractProcessPeriod = "DD";
	/** Lump on a certain point of Contract process period = LP */
	public static final String JP_DERIVATIVEDOCPOLICY_INOUT_LumpOnACertainPointOfContractProcessPeriod = "LP";
	/** From Start Contract process period to End = PB */
	public static final String JP_DERIVATIVEDOCPOLICY_INOUT_FromStartContractProcessPeriodToEnd = "PB";
	/** To End Contract process period = PE */
	public static final String JP_DERIVATIVEDOCPOLICY_INOUT_ToEndContractProcessPeriod = "PE";
	/** From start Contract process period = PS */
	public static final String JP_DERIVATIVEDOCPOLICY_INOUT_FromStartContractProcessPeriod = "PS";
	/** Set Derivative Doc Policy(In/Out).
		@param JP_DerivativeDocPolicy_InOut Derivative Doc Policy(In/Out)
	*/
	public void setJP_DerivativeDocPolicy_InOut (String JP_DerivativeDocPolicy_InOut)
	{

		set_Value (COLUMNNAME_JP_DerivativeDocPolicy_InOut, JP_DerivativeDocPolicy_InOut);
	}

	/** Get Derivative Doc Policy(In/Out).
		@return Derivative Doc Policy(In/Out)	  */
	public String getJP_DerivativeDocPolicy_InOut()
	{
		return (String)get_Value(COLUMNNAME_JP_DerivativeDocPolicy_InOut);
	}

	/** For the Duration of Contract process period = DD */
	public static final String JP_DERIVATIVEDOCPOLICY_INV_ForTheDurationOfContractProcessPeriod = "DD";
	/** Lump on a certain point of Contract process period = LP */
	public static final String JP_DERIVATIVEDOCPOLICY_INV_LumpOnACertainPointOfContractProcessPeriod = "LP";
	/** From Start Contract process period to End = PB */
	public static final String JP_DERIVATIVEDOCPOLICY_INV_FromStartContractProcessPeriodToEnd = "PB";
	/** To End Contract process period = PE */
	public static final String JP_DERIVATIVEDOCPOLICY_INV_ToEndContractProcessPeriod = "PE";
	/** From start Contract process period = PS */
	public static final String JP_DERIVATIVEDOCPOLICY_INV_FromStartContractProcessPeriod = "PS";
	/** Set Derivative Doc Policy(Invoice).
		@param JP_DerivativeDocPolicy_Inv Derivative Doc Policy(Invoice)
	*/
	public void setJP_DerivativeDocPolicy_Inv (String JP_DerivativeDocPolicy_Inv)
	{

		set_Value (COLUMNNAME_JP_DerivativeDocPolicy_Inv, JP_DerivativeDocPolicy_Inv);
	}

	/** Get Derivative Doc Policy(Invoice).
		@return Derivative Doc Policy(Invoice)	  */
	public String getJP_DerivativeDocPolicy_Inv()
	{
		return (String)get_Value(COLUMNNAME_JP_DerivativeDocPolicy_Inv);
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

	/** Set Offset of End Contract Process Period.
		@param JP_ProcPeriodOffs_End Offset of End Contract Process Period
	*/
	public void setJP_ProcPeriodOffs_End (int JP_ProcPeriodOffs_End)
	{
		set_Value (COLUMNNAME_JP_ProcPeriodOffs_End, Integer.valueOf(JP_ProcPeriodOffs_End));
	}

	/** Get Offset of End Contract Process Period.
		@return Offset of End Contract Process Period	  */
	public int getJP_ProcPeriodOffs_End()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ProcPeriodOffs_End);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Offset of End Contract Process Period(In/Out).
		@param JP_ProcPeriodOffs_End_InOut Offset of End Contract Process Period(In/Out)
	*/
	public void setJP_ProcPeriodOffs_End_InOut (int JP_ProcPeriodOffs_End_InOut)
	{
		set_Value (COLUMNNAME_JP_ProcPeriodOffs_End_InOut, Integer.valueOf(JP_ProcPeriodOffs_End_InOut));
	}

	/** Get Offset of End Contract Process Period(In/Out).
		@return Offset of End Contract Process Period(In/Out)	  */
	public int getJP_ProcPeriodOffs_End_InOut()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ProcPeriodOffs_End_InOut);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Offset of End Contract Process Period(Invoice).
		@param JP_ProcPeriodOffs_End_Inv Offset of End Contract Process Period(Invoice)
	*/
	public void setJP_ProcPeriodOffs_End_Inv (int JP_ProcPeriodOffs_End_Inv)
	{
		set_Value (COLUMNNAME_JP_ProcPeriodOffs_End_Inv, Integer.valueOf(JP_ProcPeriodOffs_End_Inv));
	}

	/** Get Offset of End Contract Process Period(Invoice).
		@return Offset of End Contract Process Period(Invoice)	  */
	public int getJP_ProcPeriodOffs_End_Inv()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ProcPeriodOffs_End_Inv);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Offset of Period to handle in a lump.
		@param JP_ProcPeriodOffs_Lump Offset of Period to handle in a lump
	*/
	public void setJP_ProcPeriodOffs_Lump (int JP_ProcPeriodOffs_Lump)
	{
		set_Value (COLUMNNAME_JP_ProcPeriodOffs_Lump, Integer.valueOf(JP_ProcPeriodOffs_Lump));
	}

	/** Get Offset of Period to handle in a lump.
		@return Offset of Period to handle in a lump	  */
	public int getJP_ProcPeriodOffs_Lump()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ProcPeriodOffs_Lump);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Offset of Period to handle in a lump(In/Out).
		@param JP_ProcPeriodOffs_Lump_InOut Offset of Period to handle in a lump(In/Out)
	*/
	public void setJP_ProcPeriodOffs_Lump_InOut (int JP_ProcPeriodOffs_Lump_InOut)
	{
		set_Value (COLUMNNAME_JP_ProcPeriodOffs_Lump_InOut, Integer.valueOf(JP_ProcPeriodOffs_Lump_InOut));
	}

	/** Get Offset of Period to handle in a lump(In/Out).
		@return Offset of Period to handle in a lump(In/Out)	  */
	public int getJP_ProcPeriodOffs_Lump_InOut()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ProcPeriodOffs_Lump_InOut);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Offset of Period to handle in a lump(Invoice).
		@param JP_ProcPeriodOffs_Lump_Inv Offset of Period to handle in a lump(Invoice)
	*/
	public void setJP_ProcPeriodOffs_Lump_Inv (int JP_ProcPeriodOffs_Lump_Inv)
	{
		set_Value (COLUMNNAME_JP_ProcPeriodOffs_Lump_Inv, Integer.valueOf(JP_ProcPeriodOffs_Lump_Inv));
	}

	/** Get Offset of Period to handle in a lump(Invoice).
		@return Offset of Period to handle in a lump(Invoice)	  */
	public int getJP_ProcPeriodOffs_Lump_Inv()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ProcPeriodOffs_Lump_Inv);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Offset of Start Contract Process Period.
		@param JP_ProcPeriodOffs_Start Offset of Start Contract Process Period
	*/
	public void setJP_ProcPeriodOffs_Start (int JP_ProcPeriodOffs_Start)
	{
		set_Value (COLUMNNAME_JP_ProcPeriodOffs_Start, Integer.valueOf(JP_ProcPeriodOffs_Start));
	}

	/** Get Offset of Start Contract Process Period.
		@return Offset of Start Contract Process Period	  */
	public int getJP_ProcPeriodOffs_Start()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ProcPeriodOffs_Start);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Offset of Start Contract Process Period(In/Out).
		@param JP_ProcPeriodOffs_Start_InOut Offset of Start Contract Process Period(In/Out)
	*/
	public void setJP_ProcPeriodOffs_Start_InOut (int JP_ProcPeriodOffs_Start_InOut)
	{
		set_Value (COLUMNNAME_JP_ProcPeriodOffs_Start_InOut, Integer.valueOf(JP_ProcPeriodOffs_Start_InOut));
	}

	/** Get Offset of Start Contract Process Period(In/Out).
		@return Offset of Start Contract Process Period(In/Out)	  */
	public int getJP_ProcPeriodOffs_Start_InOut()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ProcPeriodOffs_Start_InOut);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Offset of Start Contract Process Period(Invoice).
		@param JP_ProcPeriodOffs_Start_Inv Offset of Start Contract Process Period(Invoice)
	*/
	public void setJP_ProcPeriodOffs_Start_Inv (int JP_ProcPeriodOffs_Start_Inv)
	{
		set_Value (COLUMNNAME_JP_ProcPeriodOffs_Start_Inv, Integer.valueOf(JP_ProcPeriodOffs_Start_Inv));
	}

	/** Get Offset of Start Contract Process Period(Invoice).
		@return Offset of Start Contract Process Period(Invoice)	  */
	public int getJP_ProcPeriodOffs_Start_Inv()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ProcPeriodOffs_Start_Inv);
		if (ii == null)
			 return 0;
		return ii.intValue();
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

	public org.compiere.model.I_M_PriceList getM_PriceList() throws RuntimeException
	{
		return (org.compiere.model.I_M_PriceList)MTable.get(getCtx(), org.compiere.model.I_M_PriceList.Table_ID)
			.getPO(getM_PriceList_ID(), get_TrxName());
	}

	/** Set Price List.
		@param M_PriceList_ID Unique identifier of a Price List
	*/
	public void setM_PriceList_ID (int M_PriceList_ID)
	{
		if (M_PriceList_ID < 1)
			set_ValueNoCheck (COLUMNNAME_M_PriceList_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_M_PriceList_ID, Integer.valueOf(M_PriceList_ID));
	}

	/** Get Price List.
		@return Unique identifier of a Price List
	  */
	public int getM_PriceList_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_PriceList_ID);
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

	/** Set Movement Quantity.
		@param MovementQty Quantity of a product moved.
	*/
	public void setMovementQty (BigDecimal MovementQty)
	{
		set_Value (COLUMNNAME_MovementQty, MovementQty);
	}

	/** Get Movement Quantity.
		@return Quantity of a product moved.
	  */
	public BigDecimal getMovementQty()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_MovementQty);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Name.
		@param Name Alphanumeric identifier of the entity
	*/
	public void setName (String Name)
	{
		set_Value (COLUMNNAME_Name, Name);
	}

	/** Get Name.
		@return Alphanumeric identifier of the entity
	  */
	public String getName()
	{
		return (String)get_Value(COLUMNNAME_Name);
	}

	/** Other = -- */
	public static final String ORDERTYPE_Other = "--";
	/** Quotation = OB */
	public static final String ORDERTYPE_Quotation = "OB";
	/** Proposal = ON */
	public static final String ORDERTYPE_Proposal = "ON";
	/** Prepay Order = PR */
	public static final String ORDERTYPE_PrepayOrder = "PR";
	/** Return Material = RM */
	public static final String ORDERTYPE_ReturnMaterial = "RM";
	/** Standard Order = SO */
	public static final String ORDERTYPE_StandardOrder = "SO";
	/** On Credit Order = WI */
	public static final String ORDERTYPE_OnCreditOrder = "WI";
	/** Warehouse Order = WP */
	public static final String ORDERTYPE_WarehouseOrder = "WP";
	/** POS Order = WR */
	public static final String ORDERTYPE_POSOrder = "WR";
	/** Set Order Type.
		@param OrderType Type of Order: MRP records grouped by source (Sales Order, Purchase Order, Distribution Order, Requisition)
	*/
	public void setOrderType (String OrderType)
	{

		set_ValueNoCheck (COLUMNNAME_OrderType, OrderType);
	}

	/** Get Order Type.
		@return Type of Order: MRP records grouped by source (Sales Order, Purchase Order, Distribution Order, Requisition)
	  */
	public String getOrderType()
	{
		return (String)get_Value(COLUMNNAME_OrderType);
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
		set_Value (COLUMNNAME_QtyInvoiced, QtyInvoiced);
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