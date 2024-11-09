/******************************************************************************
 * Product: JPiere                                                            *
 * Copyright (C) Hideaki Hagiwara (h.hagiwara@oss-erp.co.jp)                  *
 *                                                                            *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY.                          *
 * See the GNU General Public License for more details.                       *
 *                                                                            *
 * JPiere is maintained by OSS ERP Solutions Co., Ltd.                        *
 * (http://www.oss-erp.co.jp)                                                 *
 *****************************************************************************/

package jpiere.base.plugin.org.adempiere.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.adempiere.exceptions.ProductNotOnPriceListException;
import org.compiere.model.MCharge;
import org.compiere.model.MCurrency;
import org.compiere.model.MDocType;
import org.compiere.model.MInOutLine;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MOrderLine;
import org.compiere.model.MPriceList;
import org.compiere.model.MProductPricing;
import org.compiere.model.MResourceAssignment;
import org.compiere.model.MRole;
import org.compiere.model.MSysConfig;
import org.compiere.model.MTax;
import org.compiere.model.MTaxProvider;
import org.compiere.util.CCache;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.base.IJPiereTaxProvider;
import jpiere.base.plugin.util.JPiereUtil;

/**
 * JPIERE-0363: Contract Management
 * JPIERE-0541: Calculate Contract Content Tax
 *
 * @author Hideaki Hagiwara
 *
 */
public class MContractLine extends X_JP_ContractLine {

	/** Parent					*/
	protected MContractContent			m_parent = null;
	protected Integer			m_precision = null;
	protected int 			m_M_PriceList_ID = 0;
	protected boolean			m_IsSOTrx = true;
	protected MProductPricing	m_productPrice = null;
	protected Timestamp		m_DateDoc = null;

	public MContractLine(Properties ctx, int JP_ContractLine_ID, String trxName)
	{
		super(ctx, JP_ContractLine_ID, trxName);
	}

	public MContractLine(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}

	/**	Cache				*/
	private static CCache<Integer,MContractLine>	s_cache = new CCache<Integer,MContractLine>(Table_Name, 20);

	/**
	 * 	Get from Cache
	 *	@param ctx context
	 *	@param JP_ContractLine_ID id
	 *	@return Contract Content Line
	 */
	public static MContractLine get (Properties ctx, int JP_ContractLine_ID)
	{
		Integer ii = Integer.valueOf(JP_ContractLine_ID);
		MContractLine retValue = (MContractLine)s_cache.get(ii);
		if (retValue != null)
			return retValue;
		retValue = new MContractLine (ctx, JP_ContractLine_ID, null);
		if (retValue.get_ID () != 0)
			s_cache.put (JP_ContractLine_ID, retValue);
		return retValue;
	}	//	get

	public MContractContent getParent()
	{
		if (m_parent == null)
			m_parent = new MContractContent(getCtx(), getJP_ContractContent_ID(), get_TrxName());
		return m_parent;
	}	//	getParent

	/**
	 *	Is Tax Included in Amount
	 *	@return true if tax calculated
	 */
	public boolean isTaxIncluded()
	{
		if (m_M_PriceList_ID == 0)
		{
			m_M_PriceList_ID = DB.getSQLValue(get_TrxName(),
				"SELECT M_PriceList_ID FROM JP_ContractContent WHERE JP_ContractContent_ID=?",
				getJP_ContractContent_ID());
		}

		MPriceList pl = MPriceList.get(getCtx(), m_M_PriceList_ID, get_TrxName());
		return pl.isTaxIncluded();
	}	//	isTaxIncluded

	@Override
	protected boolean beforeSave(boolean newRecord)
	{
		//Check update.
		if(getParent().getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
		{
			if( newRecord
					|| is_ValueChanged(MContractLine.COLUMNNAME_M_Product_ID)
					|| is_ValueChanged(MContractLine.COLUMNNAME_C_Charge_ID)
					|| is_ValueChanged(MContractLine.COLUMNNAME_IsCreateDocLineJP)
					|| is_ValueChanged(MContractLine.COLUMNNAME_QtyEntered)
					|| is_ValueChanged(MContractLine.COLUMNNAME_C_UOM_ID)//IsUpdatable = 'N'
					|| is_ValueChanged(MContractLine.COLUMNNAME_QtyOrdered)
					|| is_ValueChanged(MContractLine.COLUMNNAME_MovementQty)
					|| is_ValueChanged(MContractLine.COLUMNNAME_QtyInvoiced)
					//Base Doc Line Info
					|| is_ValueChanged(MContractLine.COLUMNNAME_JP_BaseDocLinePolicy)
					|| is_ValueChanged(MContractLine.COLUMNNAME_JP_ProcPeriod_Lump_Date)
					|| is_ValueChanged(MContractLine.COLUMNNAME_JP_ProcPeriod_Lump_ID)
					|| is_ValueChanged(MContractLine.COLUMNNAME_JP_ProcPeriod_Start_Date)
					|| is_ValueChanged(MContractLine.COLUMNNAME_JP_ProcPeriod_Start_ID)
					|| is_ValueChanged(MContractLine.COLUMNNAME_JP_ProcPeriod_End_Date)
					|| is_ValueChanged(MContractLine.COLUMNNAME_JP_ProcPeriod_End_ID)
					//Derivative Doc Info
					|| is_ValueChanged(MContractLine.COLUMNNAME_JP_DerivativeDocPolicy_InOut)
					|| is_ValueChanged(MContractLine.COLUMNNAME_JP_DerivativeDocPolicy_Inv)
					|| is_ValueChanged(MContractLine.COLUMNNAME_JP_ContractCalender_InOut_ID)
					|| is_ValueChanged(MContractLine.COLUMNNAME_JP_ContractCalender_Inv_ID)
					|| is_ValueChanged(MContractLine.COLUMNNAME_JP_ProcPeriod_Lump_InOut_Date)
					|| is_ValueChanged(MContractLine.COLUMNNAME_JP_ProcPeriod_Lump_InOut_ID)
					|| is_ValueChanged(MContractLine.COLUMNNAME_JP_ProcPeriod_Start_InOut_Date)
					|| is_ValueChanged(MContractLine.COLUMNNAME_JP_ProcPeriod_Start_InOut_ID)
					|| is_ValueChanged(MContractLine.COLUMNNAME_JP_ProcPeriod_End_InOut_Date)
					|| is_ValueChanged(MContractLine.COLUMNNAME_JP_ProcPeriod_End_InOut_ID)
					|| is_ValueChanged(MContractLine.COLUMNNAME_JP_ProcPeriod_Lump_Inv_Date)
					|| is_ValueChanged(MContractLine.COLUMNNAME_JP_ProcPeriod_Lump_Inv_ID)
					|| is_ValueChanged(MContractLine.COLUMNNAME_JP_ProcPeriod_Start_Inv_Date)
					|| is_ValueChanged(MContractLine.COLUMNNAME_JP_ProcPeriod_Start_Inv_ID)
					|| is_ValueChanged(MContractLine.COLUMNNAME_JP_ProcPeriod_End_Inv_Date)
					|| is_ValueChanged(MContractLine.COLUMNNAME_JP_ProcPeriod_End_Inv_ID)
					|| is_ValueChanged(MContractLine.COLUMNNAME_JP_ContractProcess_InOut_ID)
					|| is_ValueChanged(MContractLine.COLUMNNAME_JP_ContractProcess_Inv_ID)
					)
			{
				if(!getParent().getJP_ContractProcStatus().equals(MContractContent.JP_CONTRACTPROCSTATUS_Unprocessed))
				{
					StringBuilder msg = new StringBuilder(Msg.getMsg(getCtx(), "JP_ContractLineUpdate_PeriodContract"));
					if(is_ValueChanged(MContractLine.COLUMNNAME_M_Product_ID))
						msg = msg.append(" ").append(Msg.getElement(getCtx(), MContractLine.COLUMNNAME_M_Product_ID));
					else if(is_ValueChanged(MContractLine.COLUMNNAME_C_Charge_ID))
						msg = msg.append(" ").append(Msg.getElement(getCtx(), MContractLine.COLUMNNAME_C_Charge_ID));
					else if(is_ValueChanged(MContractLine.COLUMNNAME_IsCreateDocLineJP))
						msg = msg.append(" ").append(Msg.getElement(getCtx(), MContractLine.COLUMNNAME_IsCreateDocLineJP));
					else if(is_ValueChanged(MContractLine.COLUMNNAME_QtyEntered))
						msg = msg.append(" ").append(Msg.getElement(getCtx(), MContractLine.COLUMNNAME_QtyEntered));
					else if(is_ValueChanged(MContractLine.COLUMNNAME_C_UOM_ID))//IsUpdatable = 'N'
						msg = msg.append(" ").append(Msg.getElement(getCtx(), MContractLine.COLUMNNAME_C_UOM_ID));
					else if(is_ValueChanged(MContractLine.COLUMNNAME_QtyOrdered))
						msg = msg.append(" ").append(Msg.getElement(getCtx(), MContractLine.COLUMNNAME_QtyOrdered));
					else if(is_ValueChanged(MContractLine.COLUMNNAME_MovementQty))
						msg = msg.append(" ").append(Msg.getElement(getCtx(), MContractLine.COLUMNNAME_MovementQty));
					else if(is_ValueChanged(MContractLine.COLUMNNAME_QtyInvoiced))
						msg = msg.append(" ").append(Msg.getElement(getCtx(), MContractLine.COLUMNNAME_QtyInvoiced));
					else if(is_ValueChanged(MContractLine.COLUMNNAME_JP_BaseDocLinePolicy))
						msg = msg.append(" ").append(Msg.getElement(getCtx(), MContractLine.COLUMNNAME_JP_BaseDocLinePolicy));
					else if(is_ValueChanged(MContractLine.COLUMNNAME_JP_ProcPeriod_Lump_Date))
						msg = msg.append(" ").append(Msg.getElement(getCtx(), MContractLine.COLUMNNAME_JP_ProcPeriod_Lump_Date));
					else if(is_ValueChanged(MContractLine.COLUMNNAME_JP_ProcPeriod_Lump_ID))
						msg = msg.append(" ").append(Msg.getElement(getCtx(), MContractLine.COLUMNNAME_JP_ProcPeriod_Lump_ID));
					else if(is_ValueChanged(MContractLine.COLUMNNAME_JP_ProcPeriod_Start_Date))
						msg = msg.append(" ").append(Msg.getElement(getCtx(), MContractLine.COLUMNNAME_JP_ProcPeriod_Start_Date));
					else if(is_ValueChanged(MContractLine.COLUMNNAME_JP_ProcPeriod_Start_ID))
						msg = msg.append(" ").append(Msg.getElement(getCtx(), MContractLine.COLUMNNAME_JP_ProcPeriod_Start_ID));
					else if(is_ValueChanged(MContractLine.COLUMNNAME_JP_ProcPeriod_End_Date))
						msg = msg.append(" ").append(Msg.getElement(getCtx(), MContractLine.COLUMNNAME_JP_ProcPeriod_End_Date));
					else if(is_ValueChanged(MContractLine.COLUMNNAME_JP_ProcPeriod_End_ID))
						msg = msg.append(" ").append(Msg.getElement(getCtx(), MContractLine.COLUMNNAME_JP_ProcPeriod_End_ID));
					else if(is_ValueChanged(MContractLine.COLUMNNAME_JP_DerivativeDocPolicy_InOut))
						msg = msg.append(" ").append(Msg.getElement(getCtx(), MContractLine.COLUMNNAME_JP_DerivativeDocPolicy_InOut));
					else if(is_ValueChanged(MContractLine.COLUMNNAME_JP_DerivativeDocPolicy_Inv))
						msg = msg.append(" ").append(Msg.getElement(getCtx(), MContractLine.COLUMNNAME_JP_DerivativeDocPolicy_Inv));
					else if(is_ValueChanged(MContractLine.COLUMNNAME_JP_ContractCalender_InOut_ID))
						msg = msg.append(" ").append(Msg.getElement(getCtx(), MContractLine.COLUMNNAME_JP_ContractCalender_InOut_ID));
					else if(is_ValueChanged(MContractLine.COLUMNNAME_JP_ContractCalender_Inv_ID))
						msg = msg.append(" ").append(Msg.getElement(getCtx(), MContractLine.COLUMNNAME_JP_ContractCalender_Inv_ID));
					else if(is_ValueChanged(MContractLine.COLUMNNAME_JP_ProcPeriod_Lump_InOut_Date))
						msg = msg.append(" ").append(Msg.getElement(getCtx(), MContractLine.COLUMNNAME_JP_ProcPeriod_Lump_InOut_Date));
					else if(is_ValueChanged(MContractLine.COLUMNNAME_JP_ProcPeriod_Lump_InOut_ID))
						msg = msg.append(" ").append(Msg.getElement(getCtx(), MContractLine.COLUMNNAME_JP_ProcPeriod_Lump_InOut_ID));
					else if(is_ValueChanged(MContractLine.COLUMNNAME_JP_ProcPeriod_Start_InOut_Date))
						msg = msg.append(" ").append(Msg.getElement(getCtx(), MContractLine.COLUMNNAME_JP_ProcPeriod_Start_InOut_Date));
					else if(is_ValueChanged(MContractLine.COLUMNNAME_JP_ProcPeriod_Start_InOut_ID))
						msg = msg.append(" ").append(Msg.getElement(getCtx(), MContractLine.COLUMNNAME_JP_ProcPeriod_Start_InOut_ID));
					else if(is_ValueChanged(MContractLine.COLUMNNAME_JP_ProcPeriod_End_InOut_Date))
						msg = msg.append(" ").append(Msg.getElement(getCtx(), MContractLine.COLUMNNAME_JP_ProcPeriod_End_InOut_Date));
					else if(is_ValueChanged(MContractLine.COLUMNNAME_JP_ProcPeriod_End_InOut_ID))
						msg = msg.append(" ").append(Msg.getElement(getCtx(), MContractLine.COLUMNNAME_JP_ProcPeriod_End_InOut_ID));
					else if(is_ValueChanged(MContractLine.COLUMNNAME_JP_ProcPeriod_Lump_Inv_Date))
						msg = msg.append(" ").append(Msg.getElement(getCtx(), MContractLine.COLUMNNAME_JP_ProcPeriod_Lump_Inv_Date));
					else if(is_ValueChanged(MContractLine.COLUMNNAME_JP_ProcPeriod_Lump_Inv_ID))
						msg = msg.append(" ").append(Msg.getElement(getCtx(), MContractLine.COLUMNNAME_JP_ProcPeriod_Lump_Inv_ID));
					else if(is_ValueChanged(MContractLine.COLUMNNAME_JP_ProcPeriod_Start_Inv_Date))
						msg = msg.append(" ").append(Msg.getElement(getCtx(), MContractLine.COLUMNNAME_JP_ProcPeriod_Start_Inv_Date));
					else if(is_ValueChanged(MContractLine.COLUMNNAME_JP_ProcPeriod_Start_Inv_ID))
						msg = msg.append(" ").append(Msg.getElement(getCtx(), MContractLine.COLUMNNAME_JP_ProcPeriod_Start_Inv_ID));
					else if(is_ValueChanged(MContractLine.COLUMNNAME_JP_ProcPeriod_End_Inv_Date))
						msg = msg.append(" ").append(Msg.getElement(getCtx(), MContractLine.COLUMNNAME_JP_ProcPeriod_End_Inv_Date));
					else if(is_ValueChanged(MContractLine.COLUMNNAME_JP_ProcPeriod_End_Inv_ID))
						msg = msg.append(" ").append(Msg.getElement(getCtx(), MContractLine.COLUMNNAME_JP_ProcPeriod_End_Inv_ID));
					else if( is_ValueChanged(MContractLine.COLUMNNAME_JP_ContractProcess_InOut_ID))
						msg = msg.append(" ").append(Msg.getElement(getCtx(), MContractLine.COLUMNNAME_JP_ContractProcess_InOut_ID));
					else if( is_ValueChanged(MContractLine.COLUMNNAME_JP_ContractProcess_Inv_ID))
						msg = msg.append(" ").append(Msg.getElement(getCtx(), MContractLine.COLUMNNAME_JP_ContractProcess_Inv_ID));

					log.saveError("Error", msg.toString());
					return false;

				//Unprocessed
				}else{

					if(!checkPeriodContractInfo(newRecord, false))
					{
						log.saveError("Error", m_processMsg);
						return false;
					}

				}

			}//New or change Period Contract Info

		}//Period Contract check

		//Check Spot Contract - Derivative Doc Policy
		if(getParent().getParent().getJP_ContractType().equals(MContractT.JP_CONTRACTTYPE_SpotContract))
		{
			setNullCreateBaseDocLineInfo();
			setNullCreateDerivativeInOutInfo();
			setNullCreateDerivativeInvoiceInfo();
		}

		//JPIERE-0435 Check Extend Contract Period and Renew Contract
		if(getParent().isAutomaticUpdateJP() && getParent().getJP_ContractC_AutoUpdatePolicy().equals(MContractContent.JP_CONTRACTC_AUTOUPDATEPOLICY_RenewTheContractContent)
				&& getParent().getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
		{
			if(Util.isEmpty(getJP_ContractL_AutoUpdatePolicy()))
			{
				Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractL_AutoUpdatePolicy")};
				log.saveError("Error",Msg.getMsg(getCtx(), "JP_Mandatory",objs));
				return false ;
			}

		}else {

			setJP_ContractL_AutoUpdatePolicy(null);

		}

		//	Get Defaults from Parent
		if (getC_BPartner_ID() == 0 || getC_BPartner_Location_ID() == 0)
			setContentInfo();
		if (m_M_PriceList_ID == 0)
			setHeaderInfo();

		//	Charge
		if (getC_Charge_ID() != 0 && getM_Product_ID() != 0)
				setM_Product_ID(0);
		//	No Product
		if (getM_Product_ID() == 0)
			setM_AttributeSetInstance_ID(0);
		//	Product
		else	//	Set/check Product Price
		{
			//	Set Price if Actual = 0
			if (m_productPrice == null
				&&  Env.ZERO.compareTo(getPriceActual()) == 0
				&&  Env.ZERO.compareTo(getPriceList()) == 0)
				setPrice();
			//	Check if on Price list
			if (m_productPrice == null)
				getProductPricing(m_M_PriceList_ID);
			// IDEMPIERE-1574 Sales Order Line lets Price under the Price Limit when updating
			//	Check PriceLimit
			boolean enforce = m_IsSOTrx && getParent().getM_PriceList().isEnforcePriceLimit();
			if (enforce && MRole.getDefault().isOverwritePriceLimit())
				enforce = false;
			//	Check Price Limit?
			if (enforce && getPriceLimit() != Env.ZERO
			  && getPriceActual().compareTo(getPriceLimit()) < 0)
			{
				log.saveError("UnderLimitPrice", "PriceEntered=" + getPriceEntered() + ", PriceLimit=" + getPriceLimit());
				return false;
			}
		
			if(newRecord || is_ValueChanged(COLUMNNAME_M_Product_ID))
			{
				String docBasaeTpye = getParent().getDocBaseType();
				if(docBasaeTpye.equals(MDocType.DOCBASETYPE_SalesOrder) || docBasaeTpye.equals(MDocType.DOCBASETYPE_PurchaseOrder))
				{
					int C_DocType_ID = getParent().getJP_BaseDocDocType_ID();
					MDocType docType = MDocType.get(getCtx(), C_DocType_ID);
					if (!docType.isNoPriceListCheck() && !m_productPrice.isCalculated())
					{
						throw new ProductNotOnPriceListException(m_productPrice, getLine());
					}
				}
			}			
		}

		//JPIERE-0408:Check Counter Contract Info
		if(getJP_CounterContractLine_ID() > 0 && (newRecord || is_ValueChanged("JP_CounterContractLine_ID")))
		{
			MContractLine counterContractLine = new MContractLine(getCtx(),getJP_CounterContractLine_ID(),get_TrxName());

			//Check Product & Qty
			if(getM_Product_ID() != counterContractLine.getM_Product_ID())
			{
				String msg0 = Msg.getElement(Env.getCtx(), "JP_CounterContractLine_ID") +" - " + Msg.getElement(Env.getCtx(), "M_Product_ID");
				String msg1 = Msg.getElement(Env.getCtx(), "JP_ContractLine_ID") +" - " + Msg.getElement(Env.getCtx(), "M_Product_ID");
				log.saveError("Error", Msg.getMsg(Env.getCtx(),"JP_Different",new Object[]{msg0,msg1}));//Different between {0} and {1}
				return false;
			}

			if(getC_Charge_ID() != counterContractLine.getC_Charge_ID())
			{
				String msg0 = Msg.getElement(Env.getCtx(), "JP_CounterContractLine_ID") +" - " + Msg.getElement(Env.getCtx(), "C_Charge_ID");
				String msg1 = Msg.getElement(Env.getCtx(), "JP_ContractLine_ID") +" - " + Msg.getElement(Env.getCtx(), "C_Charge_ID");
				log.saveError("Error", Msg.getMsg(Env.getCtx(),"JP_Different",new Object[]{msg0,msg1}));//Different between {0} and {1}
				return false;
			}

			if(getQtyEntered() != null && !getQtyEntered().equals(counterContractLine.getQtyEntered()))
			{
				String msg0 = Msg.getElement(Env.getCtx(), "JP_CounterContractLine_ID") +" - " + Msg.getElement(Env.getCtx(), "QtyEntered");
				String msg1 = Msg.getElement(Env.getCtx(), "JP_ContractLine_ID") +" - " + Msg.getElement(Env.getCtx(), "QtyEntered");
				log.saveError("Error", Msg.getMsg(Env.getCtx(),"JP_Different",new Object[]{msg0,msg1}));//Different between {0} and {1}
				return false;
			}

			if(getC_UOM_ID() != counterContractLine.getC_UOM_ID())
			{
				String msg0 = Msg.getElement(Env.getCtx(), "JP_CounterContractLine_ID") +" - " + Msg.getElement(Env.getCtx(), "C_UOM_ID");
				String msg1 = Msg.getElement(Env.getCtx(), "JP_ContractLine_ID") +" - " + Msg.getElement(Env.getCtx(), "C_UOM_ID");
				log.saveError("Error", Msg.getMsg(Env.getCtx(),"JP_Different",new Object[]{msg0,msg1}));//Different between {0} and {1}
				return false;
			}

			//Check Derivative InOut Info
			if(getJP_DerivativeDocPolicy_InOut()!= null && !getJP_DerivativeDocPolicy_InOut().equals(counterContractLine.getJP_DerivativeDocPolicy_InOut()) )
			{
				String msg0 = Msg.getElement(Env.getCtx(), "JP_CounterContractLine_ID") +" - " + Msg.getElement(Env.getCtx(), "JP_DerivativeDocPolicy_InOut");
				String msg1 = Msg.getElement(Env.getCtx(), "JP_ContractLine_ID") +" - " + Msg.getElement(Env.getCtx(), "JP_DerivativeDocPolicy_InOut");
				log.saveError("Error", Msg.getMsg(Env.getCtx(),"JP_Different",new Object[]{msg0,msg1}));//Different between {0} and {1}
				return false;
			}

			if(getJP_ContractCalender_InOut_ID() != counterContractLine.getJP_ContractCalender_InOut_ID())
			{
				String msg0 = Msg.getElement(Env.getCtx(), "JP_CounterContractLine_ID") +" - " + Msg.getElement(Env.getCtx(), "JP_ContractCalender_InOut_ID");
				String msg1 = Msg.getElement(Env.getCtx(), "JP_ContractLine_ID") +" - " + Msg.getElement(Env.getCtx(), "JP_ContractCalender_InOut_ID");
				log.saveError("Error", Msg.getMsg(Env.getCtx(),"JP_Different",new Object[]{msg0,msg1}));//Different between {0} and {1}
				return false;
			}

			if(getJP_ProcPeriod_Lump_InOut_ID() != counterContractLine.getJP_ProcPeriod_Lump_InOut_ID())
			{
				String msg0 = Msg.getElement(Env.getCtx(), "JP_CounterContractLine_ID") +" - " + Msg.getElement(Env.getCtx(), "JP_ProcPeriod_Lump_InOut_ID");
				String msg1 = Msg.getElement(Env.getCtx(), "JP_ContractLine_ID") +" - " + Msg.getElement(Env.getCtx(), "JP_ProcPeriod_Lump_InOut_ID");
				log.saveError("Error", Msg.getMsg(Env.getCtx(),"JP_Different",new Object[]{msg0,msg1}));//Different between {0} and {1}
				return false;
			}

			if(getJP_ProcPeriod_Start_InOut_ID() != counterContractLine.getJP_ProcPeriod_Start_InOut_ID())
			{
				String msg0 = Msg.getElement(Env.getCtx(), "JP_CounterContractLine_ID") +" - " + Msg.getElement(Env.getCtx(), "JP_ProcPeriod_Start_InOut_ID");
				String msg1 = Msg.getElement(Env.getCtx(), "JP_ContractLine_ID") +" - " + Msg.getElement(Env.getCtx(), "JP_ProcPeriod_Start_InOut_ID");
				log.saveError("Error", Msg.getMsg(Env.getCtx(),"JP_Different",new Object[]{msg0,msg1}));//Different between {0} and {1}
				return false;
			}

			if(getJP_ProcPeriod_End_InOut_ID() != counterContractLine.getJP_ProcPeriod_End_InOut_ID())
			{
				String msg0 = Msg.getElement(Env.getCtx(), "JP_CounterContractLine_ID") +" - " + Msg.getElement(Env.getCtx(), "JP_ProcPeriod_End_InOut_ID");
				String msg1 = Msg.getElement(Env.getCtx(), "JP_ContractLine_ID") +" - " + Msg.getElement(Env.getCtx(), "JP_ProcPeriod_End_InOut_ID");
				log.saveError("Error", Msg.getMsg(Env.getCtx(),"JP_Different",new Object[]{msg0,msg1}));//Different between {0} and {1}
				return false;
			}

			if(getMovementQty() != null && !getMovementQty().equals(counterContractLine.getMovementQty()))
			{
				String msg0 = Msg.getElement(Env.getCtx(), "JP_CounterContractLine_ID") +" - " + Msg.getElement(Env.getCtx(), "MovementQty");
				String msg1 = Msg.getElement(Env.getCtx(), "JP_ContractLine_ID") +" - " + Msg.getElement(Env.getCtx(), "MovementQty");
				log.saveError("Error", Msg.getMsg(Env.getCtx(),"JP_Different",new Object[]{msg0,msg1}));//Different between {0} and {1}
				return false;
			}

			//Check Derivative Inv Info
			if(getJP_DerivativeDocPolicy_Inv()!= null && !getJP_DerivativeDocPolicy_Inv().equals(counterContractLine.getJP_DerivativeDocPolicy_Inv()) )
			{
				String msg0 = Msg.getElement(Env.getCtx(), "JP_CounterContractLine_ID") +" - " + Msg.getElement(Env.getCtx(), "JP_DerivativeDocPolicy_Inv");
				String msg1 = Msg.getElement(Env.getCtx(), "JP_ContractLine_ID") +" - " + Msg.getElement(Env.getCtx(), "JP_DerivativeDocPolicy_Inv");
				log.saveError("Error", Msg.getMsg(Env.getCtx(),"JP_Different",new Object[]{msg0,msg1}));//Different between {0} and {1}
				return false;
			}

			if(getJP_ContractCalender_Inv_ID() != counterContractLine.getJP_ContractCalender_Inv_ID())
			{
				String msg0 = Msg.getElement(Env.getCtx(), "JP_CounterContractLine_ID") +" - " + Msg.getElement(Env.getCtx(), "JP_ContractCalender_Inv_ID");
				String msg1 = Msg.getElement(Env.getCtx(), "JP_ContractLine_ID") +" - " + Msg.getElement(Env.getCtx(), "JP_ContractCalender_Inv_ID");
				log.saveError("Error", Msg.getMsg(Env.getCtx(),"JP_Different",new Object[]{msg0,msg1}));//Different between {0} and {1}
				return false;
			}

			if(getJP_ProcPeriod_Lump_Inv_ID() != counterContractLine.getJP_ProcPeriod_Lump_Inv_ID())
			{
				String msg0 = Msg.getElement(Env.getCtx(), "JP_CounterContractLine_ID") +" - " + Msg.getElement(Env.getCtx(), "JP_ProcPeriod_Lump_Inv_ID");
				String msg1 = Msg.getElement(Env.getCtx(), "JP_ContractLine_ID") +" - " + Msg.getElement(Env.getCtx(), "JP_ProcPeriod_Lump_Inv_ID");
				log.saveError("Error", Msg.getMsg(Env.getCtx(),"JP_Different",new Object[]{msg0,msg1}));//Different between {0} and {1}
				return false;
			}

			if(getJP_ProcPeriod_Start_Inv_ID() != counterContractLine.getJP_ProcPeriod_Start_Inv_ID())
			{
				String msg0 = Msg.getElement(Env.getCtx(), "JP_CounterContractLine_ID") +" - " + Msg.getElement(Env.getCtx(), "JP_ProcPeriod_Start_Inv_ID");
				String msg1 = Msg.getElement(Env.getCtx(), "JP_ContractLine_ID") +" - " + Msg.getElement(Env.getCtx(), "JP_ProcPeriod_Start_Inv_ID");
				log.saveError("Error", Msg.getMsg(Env.getCtx(),"JP_Different",new Object[]{msg0,msg1}));//Different between {0} and {1}
				return false;
			}

			if(getJP_ProcPeriod_End_Inv_ID() != counterContractLine.getJP_ProcPeriod_End_Inv_ID())
			{
				String msg0 = Msg.getElement(Env.getCtx(), "JP_CounterContractLine_ID") +" - " + Msg.getElement(Env.getCtx(), "JP_ProcPeriod_End_Inv_ID");
				String msg1 = Msg.getElement(Env.getCtx(), "JP_ContractLine_ID") +" - " + Msg.getElement(Env.getCtx(), "JP_ProcPeriod_End_Inv_ID");
				log.saveError("Error", Msg.getMsg(Env.getCtx(),"JP_Different",new Object[]{msg0,msg1}));//Different between {0} and {1}
				return false;
			}

			if(getQtyInvoiced() != null && !getQtyInvoiced().equals(counterContractLine.getQtyInvoiced()))
			{
				String msg0 = Msg.getElement(Env.getCtx(), "JP_CounterContractLine_ID") +" - " + Msg.getElement(Env.getCtx(), "QtyInvoiced");
				String msg1 = Msg.getElement(Env.getCtx(), "JP_ContractLine_ID") +" - " + Msg.getElement(Env.getCtx(), "QtyInvoiced");
				log.saveError("Error", Msg.getMsg(Env.getCtx(),"JP_Different",new Object[]{msg0,msg1}));//Different between {0} and {1}
				return false;
			}
		}

		//JPIERE-0517 - Restriction of Contract Calendar
		if(getJP_ContractCalender_InOut_ID() > 0 && is_ValueChanged("JP_ContractCalender_InOut_ID"))
		{
			if(MSysConfig.getBooleanValue("JP_RESTRICTION_OF_CONTRACT_CALENDAR", true, getAD_Client_ID(), getAD_Org_ID()))
			{
				MContractCalender cc = new MContractCalender(getCtx(), getJP_ContractCalender_InOut_ID(), get_TrxName());
				if(cc.getJP_Contract_ID() != 0 && cc.getJP_Contract_ID() != getParent().getJP_Contract_ID())
				{
					//The contract calendar is used in other contracts.
					log.saveError("Error", Msg.getMsg(getCtx(), "JP_RestrictionErrorOfContractCalendar") + " - " + cc.getName());
					return false;
				}
			}
		}

		if(getJP_ContractCalender_Inv_ID() > 0 && is_ValueChanged("JP_ContractCalender_Inv_ID"))
		{
			if(MSysConfig.getBooleanValue("JP_RESTRICTION_OF_CONTRACT_CALENDAR", true, getAD_Client_ID(), getAD_Org_ID()))
			{
				MContractCalender cc = new MContractCalender(getCtx(), getJP_ContractCalender_Inv_ID(), get_TrxName());
				if(cc.getJP_Contract_ID() != 0 && cc.getJP_Contract_ID() != getParent().getJP_Contract_ID())
				{
					//The contract calendar is used in other contracts.
					log.saveError("Error", Msg.getMsg(getCtx(), "JP_RestrictionErrorOfContractCalendar") + " - " + cc.getName());
					return false;
				}
			}
		}


		//Tax Calculation
		if(newRecord || is_ValueChanged("LineNetAmt") || is_ValueChanged("C_Tax_ID"))
		{
			BigDecimal taxAmt = Env.ZERO;
			MTax m_tax = MTax.get(Env.getCtx(), getC_Tax_ID());
			if(m_tax == null)
			{
				;//Nothing to do;
			}else{

				IJPiereTaxProvider taxCalculater = JPiereUtil.getJPiereTaxProvider(m_tax);
				//JPIERE-0369:Start
				boolean isTaxIncluded = isTaxIncluded();
				if(getC_Charge_ID() != 0)
				{
					MCharge charge = MCharge.get(getCtx(), getC_Charge_ID());
					if(!charge.isSameTax())
					{
						isTaxIncluded = charge.isTaxIncluded();
					}
				}
				//JPiere-0369:finish

				if(taxCalculater != null)
				{
					taxAmt = taxCalculater.calculateTax(m_tax, getLineNetAmt(), isTaxIncluded //JPIERE-0369
							, MCurrency.getStdPrecision(getCtx(), getParent().getC_Currency_ID())
							, JPiereTaxProvider.getRoundingMode(getParent().getC_BPartner_ID(), getParent().isSOTrx(), m_tax.getC_TaxProvider()));
				}else{
					taxAmt = m_tax.calculateTax(getLineNetAmt(), isTaxIncluded, MCurrency.getStdPrecision(getCtx(), getParent().getC_Currency_ID()));//JPIERE-0369
				}

				if(isTaxIncluded)//JPIERE-0369
				{
					set_ValueNoCheck("JP_TaxBaseAmt",  getLineNetAmt().subtract(taxAmt));
				}else{
					set_ValueNoCheck("JP_TaxBaseAmt",  getLineNetAmt());
				}

				set_ValueOfColumn("JP_TaxAmt", taxAmt);

			}
		}//Tax Calculation

		return true;

	}//beforeSave

	public void setContentInfo ()
	{
		m_parent = getParent();
		setC_BPartner_ID(m_parent.getC_BPartner_ID());
		setC_BPartner_Location_ID(m_parent.getC_BPartner_Location_ID());
		setDateOrdered(m_parent.getDateOrdered());
		setDatePromised(m_parent.getDatePromised());
		//
		setHeaderInfo();
	}

	public void setHeaderInfo ()
	{
		m_parent = getParent();
		m_precision = Integer.valueOf(m_parent.getPrecision());
		m_M_PriceList_ID = m_parent.getM_PriceList_ID();
		m_IsSOTrx = m_parent.isSOTrx();
		m_DateDoc = m_parent.getDateDoc();
	}	//	setHeaderInfo


	public void setPrice()
	{
		if (getM_Product_ID() == 0)
			return;
		if (m_M_PriceList_ID == 0)
			throw new IllegalStateException("PriceList unknown!");
		setPrice (m_M_PriceList_ID);
	}	//	setPrice


	public void setPrice (int M_PriceList_ID)
	{
		if (getM_Product_ID() == 0)
			return;
		//
		if (log.isLoggable(Level.FINE)) log.fine(toString() + " - M_PriceList_ID=" + M_PriceList_ID);
		getProductPricing (M_PriceList_ID);
		setPriceActual (m_productPrice.getPriceStd());
		setPriceList (m_productPrice.getPriceList());
		setPriceLimit (m_productPrice.getPriceLimit());
		//
		if (getQtyEntered().compareTo(getQtyOrdered()) == 0)
			setPriceEntered(getPriceActual());
		else
			setPriceEntered(getPriceActual().multiply(getQtyOrdered()
				.divide(getQtyEntered(), 12, RoundingMode.HALF_UP)));	//	recision

		//	Calculate Discount
		setDiscount(m_productPrice.getDiscount());
		//	Set UOM
		setC_UOM_ID(m_productPrice.getC_UOM_ID());

		setLineNetAmt ();
	}	//	setPrice

	public void setLineNetAmt ()
	{
		//	Calculations & Rounding
		BigDecimal bd = getPriceActual().multiply(getQtyOrdered());
		int precision = Integer.valueOf(getParent().getPrecision());
		if (bd.scale() > precision)
			bd = bd.setScale(precision, RoundingMode.HALF_UP);
		super.setLineNetAmt (bd);
	}	//	setLineNetAmt


	protected MProductPricing getProductPricing (int M_PriceList_ID)
	{
		m_productPrice = new MProductPricing (getM_Product_ID(),
			getC_BPartner_ID(), getQtyOrdered(), getParent().isSOTrx(), get_TrxName());
		m_productPrice.setM_PriceList_ID(M_PriceList_ID);
		m_productPrice.setPriceDate(m_DateDoc);
		//
		m_productPrice.calculatePrice();
		return m_productPrice;
	}	//	getProductPrice

	@Override
	protected boolean afterSave(boolean newRecord, boolean success)
	{
		if (!success)
			return success;
		if (getParent().isProcessed())
			return success;

		if(newRecord
				|| is_ValueChanged(MContractLine.COLUMNNAME_C_Tax_ID)
				|| is_ValueChanged(MContractLine.COLUMNNAME_LineNetAmt))
		{
			MTax m_tax = new MTax(getCtx(), getC_Tax_ID(), get_TrxName());
			IJPiereTaxProvider taxCalculater = JPiereUtil.getJPiereTaxProvider(m_tax);
			if (taxCalculater == null)
				throw new AdempiereException(Msg.getMsg(getCtx(), "TaxNoProvider"));

			success = taxCalculater.recalculateTax(null, this, newRecord);
	    	if(!success)
	    		return false;
		}

		return success;
	}

	@Override
	protected boolean afterDelete(boolean success) {
		if (!success)
			return success;
		if (getS_ResourceAssignment_ID() != 0)
		{
			MResourceAssignment ra = new MResourceAssignment(getCtx(), getS_ResourceAssignment_ID(), get_TrxName());
			ra.delete(true);
		}

		MTax m_tax = new MTax(getCtx(), getC_Tax_ID(), get_TrxName());
		IJPiereTaxProvider taxCalculater = JPiereUtil.getJPiereTaxProvider(m_tax);
		MTaxProvider provider = new MTaxProvider(m_tax.getCtx(), m_tax.getC_TaxProvider_ID(), m_tax.get_TrxName());
		if (taxCalculater == null)
			throw new AdempiereException(Msg.getMsg(getCtx(), "TaxNoProvider"));
    	return taxCalculater.recalculateTax(provider, this, false);

	}


	public MOrderLine[] getOrderLineByContractPeriod(Properties ctx, int JP_ContractProcPeriod_ID, String trxName)
	{
		ArrayList<MOrderLine> list = new ArrayList<MOrderLine>();
		final String sql = "SELECT ol.* FROM C_OrderLine ol  INNER JOIN  C_Order o ON(o.C_Order_ID = ol.C_Order_ID) "
					+ " WHERE ol.JP_ContractLine_ID=? AND ol.JP_ContractProcPeriod_ID=? AND o.DocStatus NOT IN ('VO','RE')";

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, trxName);
			pstmt.setInt(1, get_ID());
			pstmt.setInt(2, JP_ContractProcPeriod_ID);
			rs = pstmt.executeQuery();
			while(rs.next())
				list.add(new MOrderLine(getCtx(), rs, trxName));
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}

		MOrderLine[] iLines = new MOrderLine[list.size()];
		list.toArray(iLines);
		return iLines;
	}


	public MInOutLine[] getInOutLineByContractPeriod(Properties ctx, int JP_ContractProcPeriod_ID, String trxName)
	{
		ArrayList<MInOutLine> list = new ArrayList<MInOutLine>();
		final String sql = "SELECT iol.* FROM M_InOutLine iol  INNER JOIN  M_InOut io ON(io.M_InOut_ID = iol.M_InOut_ID) "
					+ " WHERE iol.JP_ContractLine_ID=? AND iol.JP_ContractProcPeriod_ID=? AND io.DocStatus NOT IN ('VO','RE')";

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, trxName);
			pstmt.setInt(1, get_ID());
			pstmt.setInt(2, JP_ContractProcPeriod_ID);
			rs = pstmt.executeQuery();
			while(rs.next())
				list.add(new MInOutLine(getCtx(), rs, trxName));
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}

		MInOutLine[] iLines = new MInOutLine[list.size()];
		list.toArray(iLines);
		return iLines;
	}

	public MInvoiceLine[] getInvoiceLineByContractPeriod(Properties ctx, int JP_ContractProcPeriod_ID, String trxName)
	{
		ArrayList<MInvoiceLine> list = new ArrayList<MInvoiceLine>();
		final String sql = "SELECT il.* FROM C_InvoiceLine il  INNER JOIN  C_Invoice i ON(i.C_Invoice_ID = il.C_Invoice_ID) "
					+ " WHERE il.JP_ContractLine_ID=? AND il.JP_ContractProcPeriod_ID=? AND i.DocStatus NOT IN ('VO','RE')";

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, trxName);
			pstmt.setInt(1, get_ID());
			pstmt.setInt(2, JP_ContractProcPeriod_ID);
			rs = pstmt.executeQuery();
			while(rs.next())
				list.add(new MInvoiceLine(getCtx(), rs, trxName));
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}

		MInvoiceLine[] iLines = new MInvoiceLine[list.size()];
		list.toArray(iLines);
		return iLines;
	}

	public MRecognitionLine[] getRecognitionLineByContractPeriod(Properties ctx, int JP_ContractProcPeriod_ID, String trxName)
	{
		ArrayList<MRecognitionLine> list = new ArrayList<MRecognitionLine>();
		final String sql = "SELECT rl.* FROM JP_RecognitionLine rl  INNER JOIN  JP_Recognition r ON(r.JP_Recognition_ID = rl.JP_Recognition_ID) "
					+ " WHERE rl.JP_ContractLine_ID=? AND rl.JP_ContractProcPeriod_ID=? AND r.DocStatus NOT IN ('VO','RE')";

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, trxName);
			pstmt.setInt(1, get_ID());
			pstmt.setInt(2, JP_ContractProcPeriod_ID);
			rs = pstmt.executeQuery();
			while(rs.next())
				list.add(new MRecognitionLine(getCtx(), rs, trxName));
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}

		MRecognitionLine[] iLines = new MRecognitionLine[list.size()];
		list.toArray(iLines);
		return iLines;
	}

	@Override
	public String toString()
	{
	      StringBuffer sb = new StringBuffer ("JP_ContractLine_ID[")
	    	        .append(get_ID()).append("]");
	 	  return sb.toString();
	}

	/**	Process Message 			*/
	private String		m_processMsg = null;

	/**
	 * 	Get Process Message
	 *	@return clear text error message
	 */
	public String getProcessMsg()
	{
		return m_processMsg;
	}	//	getProcessMsg


	public boolean checkPeriodContractInfo(boolean newRecord, boolean isCheckCalendarMandatory)
	{
		if(Util.isEmpty(getParent().getJP_CreateDerivativeDocPolicy()) && !getParent().getDocBaseType().equals("API") && !getParent().getDocBaseType().equals("ARI") )
		{
			//Check JP_CreateDerivativeDocPolicy
			if(getParent().getOrderType().equals(MContractContent.ORDERTYPE_StandardOrder))
			{
				Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_CreateDerivativeDocPolicy")};
				m_processMsg = Msg.getMsg(Env.getCtx(), "JP_Mandatory", objs);
				return false;

			}

		}else if( getParent().getDocBaseType().equals("SOO") || getParent().getDocBaseType().equals("POO") ){

			/** Policy of Create Derivative Doc is Manual */
			if(getParent().getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_Manual))
			{
				//Create Base Doc Line
				if(!checkCreateBaseDocLineInfo(newRecord, isCheckCalendarMandatory))
					return false;
				setNullCreateDerivativeInOutInfo();
				setNullCreateDerivativeInvoiceInfo();

			/** Policy of Create Derivative Doc is Ship & Receipt & Invoice */
			}else if(getParent().getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceiptInvoice)){

				setNullCreateBaseDocLineInfo();

				if(!checkCreateDerivativeInOutInfo(newRecord, isCheckCalendarMandatory))
					return false;

				if(!checkCreateDerivativeInvoiceInfo(newRecord, isCheckCalendarMandatory))
					return false;

				if(!checkDerivativeDocPeriodCorrespondence(newRecord, isCheckCalendarMandatory))
					return false;

			/** Policy of Create Derivative Doc is Ship & Receipt */
			}else if(getParent().getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceipt)){

				setNullCreateBaseDocLineInfo();

				if(!checkCreateDerivativeInOutInfo(newRecord, isCheckCalendarMandatory))
					return false;

				setNullCreateDerivativeInvoiceInfo();

				if(!checkDerivativeInOutAndBaseDocPeriodCorrespondence(newRecord, isCheckCalendarMandatory))
					return false;

			/** Policy of Create Derivative Doc is Invoice */
			}else if(getParent().getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateInvoice)){

				setNullCreateBaseDocLineInfo();
				setNullCreateDerivativeInOutInfo();

				if(!checkCreateDerivativeInvoiceInfo(newRecord, isCheckCalendarMandatory))
					return false;

				if(!checkDerivativeInvoiceAndBaseDocPeriodCorrespondence(newRecord, isCheckCalendarMandatory))
					return false;

			}else{

				Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractDerivativeDocPolicy_ID")};
				m_processMsg = Msg.getMsg(Env.getCtx(), "JP_InCaseOfPeriodContract") + Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs);
				return false;

			}//if(getParent().getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_Manual))

		//DocBaseType IN ('API', 'ARI')
		}else{

			//Create Base Doc Line
			if(!checkCreateBaseDocLineInfo(newRecord, isCheckCalendarMandatory))
				return false;
			setNullCreateDerivativeInOutInfo();
			setNullCreateDerivativeInvoiceInfo();
		}


		return true;
	}//checkPeriodContractInfo


	private void setNullCreateBaseDocLineInfo()
	{
		setJP_BaseDocLinePolicy(null);
		setJP_ProcPeriod_Lump_ID(0);
		setJP_ProcPeriod_Lump_Date(null);
		setJP_ProcPeriod_Start_ID(0);
		setJP_ProcPeriod_Start_Date(null);
		setJP_ProcPeriod_End_ID(0);
		setJP_ProcPeriod_End_Date(null);
	}

	private void setNullCreateDerivativeInOutInfo()
	{
		setJP_DerivativeDocPolicy_InOut(null);
		setJP_ContractCalender_InOut_ID(0);
		setJP_ProcPeriod_Lump_InOut_ID(0);
		setJP_ProcPeriod_Lump_InOut_Date(null);
		setJP_ProcPeriod_Start_InOut_ID(0);
		setJP_ProcPeriod_Start_InOut_Date(null);
		setJP_ProcPeriod_End_InOut_ID(0);
		setJP_ProcPeriod_End_InOut_Date(null);
		setJP_ContractProcess_InOut_ID(0);
	}

	private void setNullCreateDerivativeInvoiceInfo()
	{
		setJP_DerivativeDocPolicy_Inv(null);
		setJP_ContractCalender_Inv_ID(0);
		setJP_ProcPeriod_Lump_Inv_ID(0);
		setJP_ProcPeriod_Lump_Inv_Date(null);
		setJP_ProcPeriod_Start_Inv_ID(0);
		setJP_ProcPeriod_Start_Inv_Date(null);
		setJP_ProcPeriod_End_Inv_ID(0);
		setJP_ProcPeriod_End_Inv_Date(null);
		setJP_ContractProcess_Inv_ID(0);
	}

	private boolean checkCreateBaseDocLineInfo(boolean newRecord, boolean isCheckCalendarMandatory)
	{
		//Base Doc Line
		if(Util.isEmpty(getJP_BaseDocLinePolicy()))
		{
			m_processMsg = Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_BaseDocLinePolicy")});
			return false;
		}

		if(getJP_BaseDocLinePolicy().equals("LP")|| getJP_BaseDocLinePolicy().equals("PS")
				 || getJP_BaseDocLinePolicy().equals("PE") || getJP_BaseDocLinePolicy().equals("PB"))
		{
			if(getJP_BaseDocLinePolicy().equals("LP"))
			{
				if(isCheckCalendarMandatory && getJP_ProcPeriod_Lump_ID() == 0)
				{
					m_processMsg = Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ProcPeriod_Lump_ID")});
					return false;
				}

				if(getJP_ProcPeriod_Lump_ID() > 0)
				{
					if(getParent().getJP_ContractCalender_ID() == 0)
					{
						//Please enter {0} field first.
						Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractCalender_ID")};
						m_processMsg = Msg.getMsg(Env.getCtx(), "JP_EnterFirst", objs);
						return false;
					}

					MContractProcPeriod period = new MContractProcPeriod(getCtx(), getJP_ProcPeriod_Lump_ID(), get_TrxName());

					if(!checkContainContractProcDate(period))
					{
						m_processMsg = Msg.getMsg(Env.getCtx(),"JP_OutsideContractProcessPeriod") + " : " + Msg.getElement(getCtx(), "JP_ProcPeriod_Lump_ID");
						return false;
					}

					if(getJP_ProcPeriod_Lump_Date() != null)
					{
						if(!checkContainContractProcDate(getJP_ProcPeriod_Lump_Date()))
						{
							m_processMsg = Msg.getMsg(Env.getCtx(),"JP_OutsideContractProcessPeriod") + " : " + Msg.getElement(getCtx(), "JP_ProcPeriod_Lump_Date");
							return false;
						}
					}

					if(period.getJP_ContractCalender_ID() != getParent().getJP_ContractCalender_ID())
					{
						//Inconsistency between Contract Calender and Contract Process Period
						m_processMsg = Msg.getMsg(getCtx(),"JP_Inconsistency",new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractCalender_ID"),Msg.getElement(Env.getCtx(), "JP_ProcPeriod_Lump_ID")});
						return false;
					}
				}

				setJP_ProcPeriod_Start_ID(0);
				setJP_ProcPeriod_Start_Date(null);
				setJP_ProcPeriod_End_ID(0);
				setJP_ProcPeriod_End_Date(null);

			}//LP

			if(getJP_BaseDocLinePolicy().equals("PS") || getJP_BaseDocLinePolicy().equals("PB"))
			{
				if(isCheckCalendarMandatory && getJP_ProcPeriod_Start_ID() == 0)
				{
					m_processMsg = Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ProcPeriod_Start_ID")});
					return false;
				}

				if(getJP_ProcPeriod_Start_ID()  > 0)
				{
					if(getParent().getJP_ContractCalender_ID() == 0)
					{
						//Please enter {0} field first.
						Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractCalender_ID")};
						m_processMsg = Msg.getMsg(Env.getCtx(), "JP_EnterFirst", objs);
						return false;
					}

					MContractProcPeriod period = new MContractProcPeriod(getCtx(), getJP_ProcPeriod_Start_ID(), get_TrxName());

					if(!checkContainContractProcDate(period))
					{
						m_processMsg = Msg.getMsg(Env.getCtx(),"JP_OutsideContractProcessPeriod") + " : " + Msg.getElement(getCtx(), "JP_ProcPeriod_Start_ID");
						return false;
					}

					if(getJP_ProcPeriod_Start_Date() != null)
					{
						if(!checkContainContractProcDate(getJP_ProcPeriod_Start_Date()))
						{
							m_processMsg = Msg.getMsg(Env.getCtx(),"JP_OutsideContractProcessPeriod") + " : " + Msg.getElement(getCtx(), "JP_ProcPeriod_Start_Date");
							return false;
						}
					}

					if(period.getJP_ContractCalender_ID() != getParent().getJP_ContractCalender_ID())
					{
						//Inconsistency between Contract Calender and Contract Process Period
						m_processMsg = Msg.getMsg(getCtx(),"JP_Inconsistency",new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractCalender_ID"),Msg.getElement(Env.getCtx(), "JP_ProcPeriod_Start_ID")});
						return false;
					}
				}

				setJP_ProcPeriod_Lump_ID(0);
				setJP_ProcPeriod_Lump_Date(null);
				if(getJP_BaseDocLinePolicy().equals("PS"))
				{
					setJP_ProcPeriod_End_ID(0);
					setJP_ProcPeriod_End_Date(null);
				}

			}//PS,PB

			if(getJP_BaseDocLinePolicy().equals("PE") || getJP_BaseDocLinePolicy().equals("PB"))
			{
				if(isCheckCalendarMandatory && getJP_ProcPeriod_End_ID() == 0)
				{
					m_processMsg = Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ProcPeriod_End_ID")});
					return false;
				}

				if(getJP_ProcPeriod_End_ID() > 0)
				{
					if(getParent().getJP_ContractCalender_ID() == 0)
					{
						//Please enter {0} field first.
						Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractCalender_ID")};
						m_processMsg = Msg.getMsg(Env.getCtx(), "JP_EnterFirst", objs);
						return false;
					}

					MContractProcPeriod period = new MContractProcPeriod(getCtx(), getJP_ProcPeriod_End_ID(), get_TrxName());

					if(!checkContainContractProcDate(period))
					{
						m_processMsg = Msg.getMsg(Env.getCtx(),"JP_OutsideContractProcessPeriod") + " : " + Msg.getElement(getCtx(), "JP_ProcPeriod_End_ID");
						return false;
					}

					if(getJP_ProcPeriod_End_Date() != null)
					{
						if(!checkContainContractProcDate(getJP_ProcPeriod_End_Date()))
						{
							m_processMsg = Msg.getMsg(Env.getCtx(),"JP_OutsideContractProcessPeriod") + " : " + Msg.getElement(getCtx(), "JP_ProcPeriod_End_Date");
							return false;
						}
					}

					if(period.getJP_ContractCalender_ID() != getParent().getJP_ContractCalender_ID())
					{
						//Inconsistency between Contract Calender and Contract Process Period
						m_processMsg = Msg.getMsg(getCtx(),"JP_Inconsistency",new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractCalender_ID"),Msg.getElement(Env.getCtx(), "JP_ProcPeriod_End_ID")});
						return false;
					}
				}

				setJP_ProcPeriod_Lump_ID(0);
				setJP_ProcPeriod_Lump_Date(null);

				if(getJP_BaseDocLinePolicy().equals("PE"))
				{
					setJP_ProcPeriod_Start_ID(0);
					setJP_ProcPeriod_Start_Date(null);
				}

				if(getJP_BaseDocLinePolicy().equals("PB") && getJP_ProcPeriod_Start_ID() > 0 && getJP_ProcPeriod_End_ID() > 0 )
				{
					MContractProcPeriod startPeriod = new MContractProcPeriod(getCtx(), getJP_ProcPeriod_Start_ID(), get_TrxName());
					MContractProcPeriod endPeriod = new MContractProcPeriod(getCtx(), getJP_ProcPeriod_End_ID(), get_TrxName());
					if(startPeriod.getStartDate().compareTo(endPeriod.getStartDate()) > 0 )
					{
						m_processMsg = Msg.getMsg(Env.getCtx(),"Invalid") + " " + Msg.getElement(getCtx(), "JP_ProcPeriod_Start_ID") + " > " + Msg.getElement(getCtx(), "JP_ProcPeriod_End_ID");
						return false;
					}
				}
			}//PS,PB

		}else{ //DD

			setJP_ProcPeriod_Lump_ID(0);
			setJP_ProcPeriod_Lump_Date(null);
			setJP_ProcPeriod_Start_ID(0);
			setJP_ProcPeriod_Start_Date(null);
			setJP_ProcPeriod_End_ID(0);
			setJP_ProcPeriod_End_Date(null);
		}

		return true;
	}


	private boolean checkCreateDerivativeInOutInfo(boolean newRecord, boolean isCheckCalendarMandatory)
	{
		if(Util.isEmpty(getJP_DerivativeDocPolicy_InOut()))
		{
			m_processMsg = Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_DerivativeDocPolicy_InOut")});
			return false;
		}

		if(isCheckCalendarMandatory && getJP_ContractCalender_InOut_ID() == 0)
		{
			m_processMsg = Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractCalender_InOut_ID")});
			return false;
		}

		if(getMovementQty().signum()!=0 && getQtyOrdered().signum() != getMovementQty().signum())
		{
			m_processMsg = Msg.getMsg(getCtx(),"JP_Inconsistency",new Object[]{Msg.getElement(Env.getCtx(), "MovementQty"),Msg.getElement(Env.getCtx(), "QtyOrdered")});
			return false;
		}

		if(getMovementQty().abs().compareTo(getQtyOrdered().abs()) > 0)
		{
			m_processMsg = Msg.getMsg(getCtx(),"JP_Inconsistency",new Object[]{Msg.getElement(Env.getCtx(), "MovementQty"),Msg.getElement(Env.getCtx(), "QtyOrdered")});
			return false;
		}


		if(getJP_DerivativeDocPolicy_InOut().equals("LP")|| getJP_DerivativeDocPolicy_InOut().equals("PS")
				 || getJP_DerivativeDocPolicy_InOut().equals("PE") || getJP_DerivativeDocPolicy_InOut().equals("PB"))
		{
			if(getJP_DerivativeDocPolicy_InOut().equals("LP"))
			{
				if(isCheckCalendarMandatory && getJP_ProcPeriod_Lump_InOut_ID() == 0)
				{
					m_processMsg = Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ProcPeriod_Lump_InOut_ID")});
					return false;
				}

				if(getJP_ProcPeriod_Lump_InOut_ID() > 0)
				{
					if(getJP_ContractCalender_InOut_ID() == 0)
					{
						//Please enter {0} field first.
						Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractCalender_InOut_ID")};
						m_processMsg = Msg.getMsg(Env.getCtx(), "JP_EnterFirst", objs);
						return false;
					}

					MContractProcPeriod period = new MContractProcPeriod(getCtx(), getJP_ProcPeriod_Lump_InOut_ID(), get_TrxName());

					if(!checkContainContractProcDate(period))
					{
						m_processMsg = Msg.getMsg(Env.getCtx(),"JP_OutsideContractProcessPeriod") + " : " + Msg.getElement(getCtx(), "JP_ProcPeriod_Lump_InOut_ID");
						return false;
					}

					if(getJP_ProcPeriod_Lump_InOut_Date() != null)
					{
						if(!checkContainContractProcDate(getJP_ProcPeriod_Lump_InOut_Date()))
						{
							m_processMsg = Msg.getMsg(Env.getCtx(),"JP_OutsideContractProcessPeriod") + " : " + Msg.getElement(getCtx(), "JP_ProcPeriod_Lump_InOut_Date");
							return false;
						}
					}

					if(period.getJP_ContractCalender_ID() != getJP_ContractCalender_InOut_ID())
					{
						//Inconsistency between Contract Calender and Contract Process Period
						m_processMsg = Msg.getMsg(getCtx(),"JP_Inconsistency",new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractCalender_InOut_ID"),Msg.getElement(Env.getCtx(), "JP_ProcPeriod_Lump_InOut_ID")});
						return false;
					}

				}
				setJP_ProcPeriod_Start_InOut_ID(0);
				setJP_ProcPeriod_Start_InOut_Date(null);
				setJP_ProcPeriod_End_InOut_ID(0);
				setJP_ProcPeriod_End_InOut_Date(null);
			}//LP

			if(getJP_DerivativeDocPolicy_InOut().equals("PS") || getJP_DerivativeDocPolicy_InOut().equals("PB"))
			{
				if(isCheckCalendarMandatory && getJP_ProcPeriod_Start_InOut_ID() == 0)
				{
					m_processMsg = Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ProcPeriod_Start_InOut_ID")});
					return false;
				}

				if(getJP_ProcPeriod_Start_InOut_ID() > 0)
				{
					if(getJP_ContractCalender_InOut_ID() == 0)
					{
						//Please enter {0} field first.
						Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractCalender_InOut_ID")};
						m_processMsg = Msg.getMsg(Env.getCtx(), "JP_EnterFirst", objs);
						return false;
					}

					MContractProcPeriod period = new MContractProcPeriod(getCtx(), getJP_ProcPeriod_Start_InOut_ID(), get_TrxName());

					if(!checkContainContractProcDate(period))
					{
						m_processMsg = Msg.getMsg(Env.getCtx(),"JP_OutsideContractProcessPeriod") + " : " + Msg.getElement(getCtx(), "JP_ProcPeriod_Start_InOut_ID");
						return false;
					}

					if(getJP_ProcPeriod_Start_InOut_Date() != null)
					{
						if(!checkContainContractProcDate(getJP_ProcPeriod_Start_InOut_Date()))
						{
							m_processMsg = Msg.getMsg(Env.getCtx(),"JP_OutsideContractProcessPeriod") + " : " + Msg.getElement(getCtx(), "JP_ProcPeriod_Start_InOut_Date");
							return false;
						}
					}

					if(period.getJP_ContractCalender_ID() != getJP_ContractCalender_InOut_ID())
					{
						//Inconsistency between Contract Calender and Contract Process Period
						m_processMsg = Msg.getMsg(getCtx(),"JP_Inconsistency",new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractCalender_InOut_ID"),Msg.getElement(Env.getCtx(), "JP_ProcPeriod_Start_InOut_ID")});
						return false;
					}
				}
				setJP_ProcPeriod_Lump_InOut_ID(0);
				setJP_ProcPeriod_Lump_InOut_Date(null);
				if(getJP_DerivativeDocPolicy_InOut().equals("PS"))
				{
					setJP_ProcPeriod_End_InOut_ID(0);
					setJP_ProcPeriod_End_InOut_Date(null);
				}

			}//PS,PB

			if(getJP_DerivativeDocPolicy_InOut().equals("PE") || getJP_DerivativeDocPolicy_InOut().equals("PB"))
			{
				if(isCheckCalendarMandatory && getJP_ProcPeriod_End_InOut_ID() == 0)
				{
					m_processMsg = Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ProcPeriod_End_InOut_ID")});
					return false;
				}

				if(getJP_ProcPeriod_End_InOut_ID() > 0)
				{
					if(getJP_ContractCalender_InOut_ID() == 0)
					{
						//Please enter {0} field first.
						Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractCalender_InOut_ID")};
						m_processMsg = Msg.getMsg(Env.getCtx(), "JP_EnterFirst", objs);
						return false;
					}

					MContractProcPeriod period = new MContractProcPeriod(getCtx(), getJP_ProcPeriod_End_InOut_ID(), get_TrxName());

					if(!checkContainContractProcDate(period))
					{
						m_processMsg = Msg.getMsg(Env.getCtx(),"JP_OutsideContractProcessPeriod") + " : " + Msg.getElement(getCtx(), "JP_ProcPeriod_End_InOut_ID");
						return false;
					}

					if(getJP_ProcPeriod_End_InOut_Date() != null)
					{
						if(!checkContainContractProcDate(getJP_ProcPeriod_End_InOut_Date()))
						{
							m_processMsg = Msg.getMsg(Env.getCtx(),"JP_OutsideContractProcessPeriod") + " : " + Msg.getElement(getCtx(), "JP_ProcPeriod_End_InOut_Date");
							return false;
						}
					}

					if(period.getJP_ContractCalender_ID() != getJP_ContractCalender_InOut_ID())
					{
						//Inconsistency between Contract Calender and Contract Process Period
						m_processMsg = Msg.getMsg(getCtx(),"JP_Inconsistency",new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractCalender_InOut_ID"),Msg.getElement(Env.getCtx(), "JP_ProcPeriod_End_InOut_ID")});
						return false;
					}
				}

				setJP_ProcPeriod_Lump_InOut_ID(0);
				setJP_ProcPeriod_Lump_InOut_Date(null);

				if(getJP_DerivativeDocPolicy_InOut().equals("PE"))
				{
					setJP_ProcPeriod_Start_InOut_ID(0);
					setJP_ProcPeriod_Start_InOut_Date(null);
				}

				if(getJP_DerivativeDocPolicy_InOut().equals("PB") && getJP_ProcPeriod_Start_InOut_ID() > 0 && getJP_ProcPeriod_End_InOut_ID() > 0 )
				{
					MContractProcPeriod startPeriod = new MContractProcPeriod(getCtx(), getJP_ProcPeriod_Start_InOut_ID() ,get_TrxName());
					MContractProcPeriod endPeriod = new MContractProcPeriod(getCtx(), getJP_ProcPeriod_End_InOut_ID() ,get_TrxName());
					if(startPeriod.getStartDate().compareTo(endPeriod.getStartDate()) > 0 )
					{
						m_processMsg = Msg.getMsg(Env.getCtx(),"Invalid") + " " + Msg.getElement(getCtx(), "JP_ProcPeriod_Start_InOut_ID") + " > " + Msg.getElement(getCtx(), "JP_ProcPeriod_End_InOut_ID");
						return false;
					}
				}
			}//PS,PB

		}else{//DD

			setJP_ProcPeriod_Lump_InOut_ID(0);
			setJP_ProcPeriod_Lump_InOut_Date(null);
			setJP_ProcPeriod_Start_InOut_ID(0);
			setJP_ProcPeriod_Start_InOut_Date(null);
			setJP_ProcPeriod_End_InOut_ID(0);
			setJP_ProcPeriod_End_InOut_Date(null);

			if(isCheckCalendarMandatory || getJP_ContractCalender_InOut_ID() > 0)
			{
				if(getJP_ContractCalender_InOut_ID() == 0)
				{
						m_processMsg = Msg.getMsg(getCtx(), "NotFound") + " : " + Msg.getElement(getCtx(), "JP_ContractCalender_InOut_ID") ;
						return false;
				}

				MContractCalender inOut_Calender = new MContractCalender(getCtx(), getJP_ContractCalender_InOut_ID(), get_TrxName());

				MContractProcPeriod inOut_Start_ProcPeriod = inOut_Calender.getContractProcessPeriod(getCtx(), getParent().getJP_ContractProcDate_From());
				if(inOut_Start_ProcPeriod == null)
				{
					m_processMsg = Msg.getMsg(getCtx(), "NotFound") + " : " +
							Msg.getElement(getCtx(), "JP_ContractCalender_InOut_ID") + " - " + Msg.getElement(getCtx(), "JP_ContractProcPeriod_ID") + " - " + Msg.getElement(getCtx(), "JP_ContractProcDate_From");
					return false;
				}

				if(getParent().getJP_ContractProcDate_To() != null)
				{
					MContractProcPeriod inout_End_ProcPeriod = inOut_Calender.getContractProcessPeriod(getCtx(), getParent().getJP_ContractProcDate_To());
					if(inout_End_ProcPeriod == null)
					{
						m_processMsg = Msg.getMsg(getCtx(), "NotFound") + " : " +
								Msg.getElement(getCtx(), "JP_ContractCalender_InOut_ID") + " - " + Msg.getElement(getCtx(), "JP_ContractProcPeriod_ID") + " - " + Msg.getElement(getCtx(), "JP_ContractProcDate_To");
						return false;
					}
				}
			}
		}

		if(!newRecord && getJP_ContractProcess_InOut_ID() == 0)
		{
			m_processMsg = Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractProcess_InOut_ID")});
			return false;
		}

		return true;
	}

	private boolean checkCreateDerivativeInvoiceInfo(boolean newRecord, boolean isCheckCalendarMandatory)
	{
		if(Util.isEmpty(getJP_DerivativeDocPolicy_Inv()))
		{
			m_processMsg = Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_DerivativeDocPolicy_Inv")});
			return false;
		}

		if(isCheckCalendarMandatory && getJP_ContractCalender_Inv_ID() == 0)
		{
			m_processMsg = Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractCalender_Inv_ID")});
			return false;
		}

		if(getQtyInvoiced().signum()!=0 && getQtyOrdered().signum() != getQtyInvoiced().signum())
		{
			m_processMsg = Msg.getMsg(getCtx(),"JP_Inconsistency",new Object[]{Msg.getElement(Env.getCtx(), "QtyInvoiced"),Msg.getElement(Env.getCtx(), "QtyOrdered")});
			return false;
		}

		if(getQtyInvoiced().abs().compareTo(getQtyOrdered().abs()) > 0)
		{
			m_processMsg = Msg.getMsg(getCtx(),"JP_Inconsistency",new Object[]{Msg.getElement(Env.getCtx(), "QtyInvoiced"),Msg.getElement(Env.getCtx(), "QtyOrdered")});
			return false;
		}


		if(getJP_DerivativeDocPolicy_Inv().equals("LP")|| getJP_DerivativeDocPolicy_Inv().equals("PS")
				 || getJP_DerivativeDocPolicy_Inv().equals("PE") || getJP_DerivativeDocPolicy_Inv().equals("PB"))
		{
			if(getJP_DerivativeDocPolicy_Inv().equals("LP"))
			{
				if(isCheckCalendarMandatory && getJP_ProcPeriod_Lump_Inv_ID() == 0)
				{
					m_processMsg = Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ProcPeriod_Lump_Inv_ID")});
					return false;
				}

				if(getJP_ProcPeriod_Lump_Inv_ID() > 0)
				{
					if(getJP_ContractCalender_Inv_ID() == 0)
					{
						//Please enter {0} field first.
						Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractCalender_Inv_ID")};
						m_processMsg = Msg.getMsg(Env.getCtx(), "JP_EnterFirst", objs);
						return false;
					}

					MContractProcPeriod period = new MContractProcPeriod(getCtx(), getJP_ProcPeriod_Lump_Inv_ID() ,get_TrxName());

					if(!checkContainContractProcDate(period))
					{
						m_processMsg = Msg.getMsg(Env.getCtx(),"JP_OutsideContractProcessPeriod") + " : " + Msg.getElement(getCtx(), "JP_ProcPeriod_Lump_Inv_ID");
						return false;
					}

					if(getJP_ProcPeriod_Lump_Inv_Date() != null)
					{
						if(!checkContainContractProcDate(getJP_ProcPeriod_Lump_Inv_Date()))
						{
							m_processMsg = Msg.getMsg(Env.getCtx(),"JP_OutsideContractProcessPeriod") + " : " + Msg.getElement(getCtx(), "JP_ProcPeriod_Lump_Inv_Date");
							return false;
						}
					}

					if(period.getJP_ContractCalender_ID() != getJP_ContractCalender_Inv_ID())
					{
						//Inconsistency between Contract Calender and Contract Process Period
						m_processMsg = Msg.getMsg(getCtx(),"JP_Inconsistency",new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractCalender_Inv_ID"),Msg.getElement(Env.getCtx(), "JP_ProcPeriod_Lump_Inv_ID")});
						return false;
					}
				}
				setJP_ProcPeriod_Start_Inv_ID(0);
				setJP_ProcPeriod_Start_Inv_Date(null);
				setJP_ProcPeriod_End_Inv_ID(0);
				setJP_ProcPeriod_End_Inv_Date(null);
			}//LP

			if(getJP_DerivativeDocPolicy_Inv().equals("PS") || getJP_DerivativeDocPolicy_Inv().equals("PB"))
			{
				if(isCheckCalendarMandatory && getJP_ProcPeriod_Start_Inv_ID() == 0)
				{
					m_processMsg = Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ProcPeriod_Start_Inv_ID")});
					return false;
				}

				if(getJP_ProcPeriod_Start_Inv_ID() > 0)
				{
					if(getJP_ContractCalender_Inv_ID() == 0)
					{
						//Please enter {0} field first.
						Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractCalender_Inv_ID")};
						m_processMsg = Msg.getMsg(Env.getCtx(), "JP_EnterFirst", objs);
						return false;
					}

					MContractProcPeriod period = new MContractProcPeriod(getCtx(), getJP_ProcPeriod_Start_Inv_ID(), get_TrxName());

					if(!checkContainContractProcDate(period))
					{
						m_processMsg = Msg.getMsg(Env.getCtx(),"JP_OutsideContractProcessPeriod") + " : " + Msg.getElement(getCtx(), "JP_ProcPeriod_Start_Inv_ID");
						return false;
					}

					if(getJP_ProcPeriod_Start_Inv_Date() != null)
					{
						if(!checkContainContractProcDate(getJP_ProcPeriod_Start_Inv_Date()))
						{
							m_processMsg = Msg.getMsg(Env.getCtx(),"JP_OutsideContractProcessPeriod") + " : " + Msg.getElement(getCtx(), "JP_ProcPeriod_Start_Inv_Date");
							return false;
						}
					}

					if(period.getJP_ContractCalender_ID() != getJP_ContractCalender_Inv_ID())
					{
						//Inconsistency between Contract Calender and Contract Process Period
						m_processMsg = Msg.getMsg(getCtx(),"JP_Inconsistency",new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractCalender_Inv_ID"),Msg.getElement(Env.getCtx(), "JP_ProcPeriod_Start_Inv_ID")});
						return false;
					}
				}
				setJP_ProcPeriod_Lump_Inv_ID(0);
				setJP_ProcPeriod_Lump_Inv_Date(null);
				if(getJP_DerivativeDocPolicy_Inv().equals("PS"))
				{
					setJP_ProcPeriod_End_Inv_ID(0);
					setJP_ProcPeriod_End_Inv_Date(null);
				}

			}//PS,PB

			if(getJP_DerivativeDocPolicy_Inv().equals("PE") || getJP_DerivativeDocPolicy_Inv().equals("PB"))
			{
				if(isCheckCalendarMandatory && getJP_ProcPeriod_End_Inv_ID() == 0)
				{
					m_processMsg = Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ProcPeriod_End_Inv_ID")});
					return false;
				}

				if(getJP_ProcPeriod_End_Inv_ID() > 0)
				{
					if(getJP_ContractCalender_Inv_ID() == 0)
					{
						//Please enter {0} field first.
						Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractCalender_Inv_ID")};
						m_processMsg = Msg.getMsg(Env.getCtx(), "JP_EnterFirst", objs);
						return false;
					}

					MContractProcPeriod period = new MContractProcPeriod(getCtx(), getJP_ProcPeriod_End_Inv_ID(), get_TrxName());

					if(!checkContainContractProcDate(period))
					{
						m_processMsg = Msg.getMsg(Env.getCtx(),"JP_OutsideContractProcessPeriod") + " : " + Msg.getElement(getCtx(), "JP_ProcPeriod_End_Inv_ID");
						return false;
					}

					if(getJP_ProcPeriod_End_Inv_Date() != null)
					{
						if(!checkContainContractProcDate(getJP_ProcPeriod_End_Inv_Date()))
						{
							m_processMsg = Msg.getMsg(Env.getCtx(),"JP_OutsideContractProcessPeriod") + " : " + Msg.getElement(getCtx(), "JP_ProcPeriod_End_Inv_Date");
							return false;
						}
					}

					if(period.getJP_ContractCalender_ID() != getJP_ContractCalender_Inv_ID())
					{
						//Inconsistency between Contract Calender and Contract Process Period
						m_processMsg = Msg.getMsg(getCtx(),"JP_Inconsistency",new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractCalender_Inv_ID"),Msg.getElement(Env.getCtx(), "JP_ProcPeriod_End_Inv_ID")});
						return false;
					}
				}

				setJP_ProcPeriod_Lump_Inv_ID(0);
				setJP_ProcPeriod_Lump_Inv_Date(null);

				if(getJP_DerivativeDocPolicy_Inv().equals("PE"))
				{
					setJP_ProcPeriod_Start_Inv_ID(0);
					setJP_ProcPeriod_Start_Inv_Date(null);
				}

				if(getJP_DerivativeDocPolicy_Inv().equals("PB") && getJP_ProcPeriod_Start_Inv_ID() > 0 && getJP_ProcPeriod_End_Inv_ID() > 0 )
				{
					MContractProcPeriod startPeriod = new MContractProcPeriod(getCtx(), getJP_ProcPeriod_Start_Inv_ID(), get_TrxName());
					MContractProcPeriod endPeriod = new MContractProcPeriod(getCtx(), getJP_ProcPeriod_End_Inv_ID(), get_TrxName());
					if(startPeriod.getStartDate().compareTo(endPeriod.getStartDate()) > 0 )
					{
						m_processMsg = Msg.getMsg(Env.getCtx(),"Invalid") + " " + Msg.getElement(getCtx(), "JP_ProcPeriod_Start_Inv_ID") + " > " + Msg.getElement(getCtx(), "JP_ProcPeriod_End_Inv_ID");
						return false;
					}
				}
			}//PS,PB

		}else{//DD

			setJP_ProcPeriod_Lump_Inv_ID(0);
			setJP_ProcPeriod_Lump_Inv_Date(null);
			setJP_ProcPeriod_Start_Inv_ID(0);
			setJP_ProcPeriod_Start_Inv_Date(null);
			setJP_ProcPeriod_End_Inv_ID(0);
			setJP_ProcPeriod_End_Inv_Date(null);

			if(isCheckCalendarMandatory || getJP_ContractCalender_Inv_ID() > 0)
			{
				if(getJP_ContractCalender_Inv_ID() == 0)
				{
					m_processMsg = Msg.getMsg(getCtx(), "NotFound") + " : " + 	Msg.getElement(getCtx(), "JP_ContractCalender_Inv_ID");
					return false;
				}

				MContractCalender inv_Calender =new MContractCalender(getCtx(), getJP_ContractCalender_Inv_ID(), get_TrxName());

				MContractProcPeriod inv_Start_ProcPeriod = inv_Calender.getContractProcessPeriod(getCtx(), getParent().getJP_ContractProcDate_From());
				if(inv_Start_ProcPeriod == null)
				{
					m_processMsg = Msg.getMsg(getCtx(), "NotFound") + " : " +
							Msg.getElement(getCtx(), "JP_ContractCalender_Inv_ID") + " - " +Msg.getElement(getCtx(), "JP_ContractProcPeriod_ID") + " - " + Msg.getElement(getCtx(), "JP_ContractProcDate_From");
					return false;
				}

				if(getParent().getJP_ContractProcDate_To() != null)
				{
					MContractProcPeriod inv_End_ProcPeriod = inv_Calender.getContractProcessPeriod(getCtx(), getParent().getJP_ContractProcDate_To());
					if(inv_End_ProcPeriod == null)
					{
						m_processMsg = Msg.getMsg(getCtx(), "NotFound") + " : " +
								Msg.getElement(getCtx(), "JP_ContractCalender_Inv_ID") + " - " + Msg.getElement(getCtx(), "JP_ContractProcPeriod_ID") + " - " + Msg.getElement(getCtx(), "JP_ContractProcDate_To");
						return false;
					}
				}
			}
		}


		if(!newRecord && getJP_ContractProcess_Inv_ID() == 0)
		{
			m_processMsg = Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractProcess_Inv_ID")});
			return false;
		}

		return true;
	}

	private boolean checkDerivativeDocPeriodCorrespondence(boolean newRecord, boolean isCheckCalendarMandatory)
	{
		int parentJP_ContractCalender_ID =  getParent().getJP_ContractCalender_ID();

		/** Check Contract Process Period correspondence between Derivative shi/Recipt And Derivative invoice */
		if( (getJP_DerivativeDocPolicy_InOut().equals("LP") && getJP_DerivativeDocPolicy_Inv().equals("LP"))
				|| (getJP_DerivativeDocPolicy_InOut().equals("LP") && getJP_DerivativeDocPolicy_Inv().equals("PB"))
				|| (getJP_DerivativeDocPolicy_InOut().equals("PB") && getJP_DerivativeDocPolicy_Inv().equals("LP"))
				)
		{
			//It is necessary to be single order Contract process Period
			MContractCalender order_Calender = MContractCalender.get(getCtx(), parentJP_ContractCalender_ID);

			if(getJP_DerivativeDocPolicy_InOut().equals("LP"))
			{
				if(!isCheckCalendarMandatory && (getJP_ProcPeriod_Lump_InOut_ID() == 0 || parentJP_ContractCalender_ID == 0) )
					return true;

				MContractProcPeriod inout_ProcPeriod = new MContractProcPeriod(getCtx(), getJP_ProcPeriod_Lump_InOut_ID(), get_TrxName());
				MContractProcPeriod order_Start_ProcPeriod = order_Calender.getContractProcessPeriod(getCtx(), inout_ProcPeriod.getStartDate());
				MContractProcPeriod order_End_ProcPeriod = order_Calender.getContractProcessPeriod(getCtx(), inout_ProcPeriod.getEndDate());
				if(order_Start_ProcPeriod.getJP_ContractProcPeriod_ID() != order_End_ProcPeriod.getJP_ContractProcPeriod_ID())
				{
					//A certain point of Derivative Contract process period step over Base Doc contract Process period.
					m_processMsg = Msg.getMsg(getCtx(), "JP_StepOverBaseDocContractProcessPeriod");
					return false;
				}

				int inout_Order_ProcPeriod_ID = order_Start_ProcPeriod.getJP_ContractProcPeriod_ID();

				if(getJP_DerivativeDocPolicy_Inv().equals("LP"))// LP && LP
				{
					if(!isCheckCalendarMandatory && (getJP_ProcPeriod_Lump_Inv_ID() == 0 || parentJP_ContractCalender_ID == 0) )
						return true;

					MContractProcPeriod inv_ProcPeriod = new MContractProcPeriod(getCtx(), getJP_ProcPeriod_Lump_Inv_ID(), get_TrxName());
					order_Start_ProcPeriod = order_Calender.getContractProcessPeriod(getCtx(), inv_ProcPeriod.getStartDate());
					order_End_ProcPeriod = order_Calender.getContractProcessPeriod(getCtx(), inv_ProcPeriod.getEndDate());
					if(order_Start_ProcPeriod.getJP_ContractProcPeriod_ID() != order_End_ProcPeriod.getJP_ContractProcPeriod_ID())
					{
						//A certain point of Derivative Contract process period step over Base Doc contract Process period.
						m_processMsg = Msg.getMsg(getCtx(), "JP_StepOverBaseDocContractProcessPeriod");
						return false;
					}

					int inv_Order_ProcPeriod_ID = order_Start_ProcPeriod.getJP_ContractProcPeriod_ID();
					if(inout_Order_ProcPeriod_ID != inv_Order_ProcPeriod_ID)
					{
						//Inconsistency Contract Process Period between Derivative Doc.
						m_processMsg = Msg.getMsg(getCtx(), "JP_InconsistencyContractProcPeriodBetweenDerivativeDoc");
						return false;
					}


				}else if(getJP_DerivativeDocPolicy_Inv().equals("PB")){ //LP && PB

					if(!isCheckCalendarMandatory && (getJP_ProcPeriod_Start_Inv_ID() == 0 || getJP_ProcPeriod_End_Inv_ID() == 0 || parentJP_ContractCalender_ID == 0) )
						return true;

					MContractProcPeriod procPeriod_Start_inv = new MContractProcPeriod(getCtx(), getJP_ProcPeriod_Start_Inv_ID(), get_TrxName());
					MContractProcPeriod procPeriod_End_inv = new MContractProcPeriod(getCtx(), getJP_ProcPeriod_End_Inv_ID(), get_TrxName());
					order_Start_ProcPeriod = order_Calender.getContractProcessPeriod(getCtx(), procPeriod_Start_inv.getStartDate());
					order_End_ProcPeriod = order_Calender.getContractProcessPeriod(getCtx(), procPeriod_End_inv.getEndDate());
					if(order_Start_ProcPeriod.getJP_ContractProcPeriod_ID() != order_End_ProcPeriod.getJP_ContractProcPeriod_ID())
					{
						//A certain point of Derivative Contract process period step over Base Doc contract Process period.
						m_processMsg = Msg.getMsg(getCtx(), "JP_StepOverBaseDocContractProcessPeriod");
						return false;
					}
					int inv_Order_ProcPeriod_ID = order_Start_ProcPeriod.getJP_ContractProcPeriod_ID();

					if(inv_Order_ProcPeriod_ID != inout_Order_ProcPeriod_ID)
					{
						//Inconsistency Contract Process Period between Derivative Doc.
						m_processMsg = Msg.getMsg(getCtx(), "JP_InconsistencyContractProcPeriodBetweenDerivativeDoc");
						return false;
					}
				}

			}else if(getJP_DerivativeDocPolicy_InOut().equals("PB")){

				if(!isCheckCalendarMandatory && (getJP_ProcPeriod_Start_InOut_ID() == 0 || getJP_ProcPeriod_End_InOut_ID() == 0 || parentJP_ContractCalender_ID == 0) )
					return true;

				if(getJP_DerivativeDocPolicy_Inv().equals("LP"))//PB && LP
				{

					if(!isCheckCalendarMandatory && (getJP_ProcPeriod_Lump_Inv_ID() == 0 || parentJP_ContractCalender_ID == 0) )
						return true;

					MContractProcPeriod inv_ProcPeriod = new MContractProcPeriod(getCtx(), getJP_ProcPeriod_Lump_Inv_ID(), get_TrxName());
					MContractProcPeriod order_Start_ProcPeriod = order_Calender.getContractProcessPeriod(getCtx(), inv_ProcPeriod.getStartDate());
					MContractProcPeriod order_End_ProcPeriod = order_Calender.getContractProcessPeriod(getCtx(), inv_ProcPeriod.getEndDate());
					if(order_Start_ProcPeriod.getJP_ContractProcPeriod_ID() != order_End_ProcPeriod.getJP_ContractProcPeriod_ID())
					{
						//A certain point of Derivative Contract process period step over Base Doc contract Process period.
						m_processMsg = Msg.getMsg(getCtx(), "JP_StepOverBaseDocContractProcessPeriod");
						return false;
					}
					int inv_Order_ProcPeriod_ID = order_Start_ProcPeriod.getJP_ContractProcPeriod_ID();

					MContractProcPeriod procPeriod_Start_inout = new MContractProcPeriod(getCtx(), getJP_ProcPeriod_Start_InOut_ID(), get_TrxName());
					MContractProcPeriod procPeriod_End_inout = new MContractProcPeriod(getCtx(), getJP_ProcPeriod_End_InOut_ID(), get_TrxName());
					order_Start_ProcPeriod = order_Calender.getContractProcessPeriod(getCtx(), procPeriod_Start_inout.getStartDate());
					order_End_ProcPeriod = order_Calender.getContractProcessPeriod(getCtx(), procPeriod_End_inout.getEndDate());
					if(order_Start_ProcPeriod.getJP_ContractProcPeriod_ID() != order_End_ProcPeriod.getJP_ContractProcPeriod_ID())
					{
						//A certain point of Derivative Contract process period step over Base Doc contract Process period.
						m_processMsg = Msg.getMsg(getCtx(), "JP_StepOverBaseDocContractProcessPeriod");
						return false;
					}
					int inout_Order_ProcPeriod_ID = order_Start_ProcPeriod.getJP_ContractProcPeriod_ID();

					if(inv_Order_ProcPeriod_ID != inout_Order_ProcPeriod_ID)
					{
						//Inconsistency Contract Process Period between Derivative Doc.
						m_processMsg = Msg.getMsg(getCtx(), "JP_InconsistencyContractProcPeriodBetweenDerivativeDoc");
						return false;
					}
				}

			}

		}else if(getJP_DerivativeDocPolicy_InOut().equals("PB") && getJP_DerivativeDocPolicy_Inv().equals("PB")){

			if(!isCheckCalendarMandatory &&( getJP_ProcPeriod_Start_InOut_ID()==0 || getJP_ProcPeriod_Start_Inv_ID()==0
					||  getJP_ProcPeriod_End_InOut_ID() ==0 || getJP_ProcPeriod_End_Inv_ID() ==0 || parentJP_ContractCalender_ID == 0) )
				return true;

			MContractCalender order_Calender = MContractCalender.get(getCtx(), getParent().getJP_ContractCalender_ID());

			MContractProcPeriod inout_ProcPeriod = new MContractProcPeriod(getCtx(), getJP_ProcPeriod_Start_InOut_ID(), get_TrxName());
			MContractProcPeriod order_Start_ProcPeriod_InOut = order_Calender.getContractProcessPeriod(getCtx(), inout_ProcPeriod.getStartDate());
			MContractProcPeriod invoice_ProcPeriod = new MContractProcPeriod(getCtx(), getJP_ProcPeriod_Start_Inv_ID(), get_TrxName());
			MContractProcPeriod order_Start_ProcPeriod_Inv = order_Calender.getContractProcessPeriod(getCtx(), invoice_ProcPeriod.getStartDate());
			if(order_Start_ProcPeriod_InOut.getJP_ContractProcPeriod_ID() != order_Start_ProcPeriod_Inv.getJP_ContractProcPeriod_ID())
			{
				//Inconsistency Start Contract Process Period between Contract Process Period(In/Out) and Contract Process Period(Invoice)
				m_processMsg = Msg.getMsg(getCtx(), "JP_InconsistencyStartProcPeriodBetweenDerivativeDoc");
				return false;
			}

			inout_ProcPeriod = new MContractProcPeriod(getCtx(), getJP_ProcPeriod_End_InOut_ID(), get_TrxName());
			MContractProcPeriod order_End_ProcPeriod_InOut = order_Calender.getContractProcessPeriod(getCtx(), inout_ProcPeriod.getEndDate());
			invoice_ProcPeriod = new MContractProcPeriod(getCtx(), getJP_ProcPeriod_End_Inv_ID(), get_TrxName());
			MContractProcPeriod order_End_ProcPeriod_Inv = order_Calender.getContractProcessPeriod(getCtx(), invoice_ProcPeriod.getEndDate());

			if(order_End_ProcPeriod_InOut.getJP_ContractProcPeriod_ID() != order_End_ProcPeriod_Inv.getJP_ContractProcPeriod_ID())
			{
				//Inconsistency End Contract Process Period between Contract Process Period(In/Out) and Contract Process Period(Invoice).
				m_processMsg = Msg.getMsg(getCtx(), "JP_InconsistencyEndProcPeriodBetweenDerivativeDoc");
				return false;
			}


		}else if(getJP_DerivativeDocPolicy_InOut().equals("PS") && getJP_DerivativeDocPolicy_Inv().equals("PS")){

			if(!isCheckCalendarMandatory && (getJP_ProcPeriod_Start_InOut_ID()==0 || getJP_ProcPeriod_Start_Inv_ID()==0 || parentJP_ContractCalender_ID == 0))
				return true;

			MContractCalender order_Calender = MContractCalender.get(getCtx(), getParent().getJP_ContractCalender_ID());

			MContractProcPeriod inout_ProcPeriod = new MContractProcPeriod(getCtx(), getJP_ProcPeriod_Start_InOut_ID(), get_TrxName());
			MContractProcPeriod order_Start_ProcPeriod_InOut = order_Calender.getContractProcessPeriod(getCtx(), inout_ProcPeriod.getStartDate());
			MContractProcPeriod invoice_ProcPeriod = new MContractProcPeriod(getCtx(), getJP_ProcPeriod_Start_Inv_ID(), get_TrxName());
			MContractProcPeriod order_Start_ProcPeriod_Inv = order_Calender.getContractProcessPeriod(getCtx(), invoice_ProcPeriod.getStartDate());
			if(order_Start_ProcPeriod_InOut.getJP_ContractProcPeriod_ID() != order_Start_ProcPeriod_Inv.getJP_ContractProcPeriod_ID())
			{
				//Inconsistency Start Contract Process Period between Contract Process Period(In/Out) and Contract Process Period(Invoice)
				m_processMsg = Msg.getMsg(getCtx(), "JP_InconsistencyStartProcPeriodBetweenDerivativeDoc");
				return false;
			}

		}else if(getJP_DerivativeDocPolicy_InOut().equals("PE") && getJP_DerivativeDocPolicy_Inv().equals("PE")){

			if(!isCheckCalendarMandatory && (getJP_ProcPeriod_End_InOut_ID() ==0 || getJP_ProcPeriod_End_Inv_ID() ==0  || parentJP_ContractCalender_ID == 0 ))
				return true;

			MContractCalender order_Calender = MContractCalender.get(getCtx(), getParent().getJP_ContractCalender_ID());

			MContractProcPeriod inout_ProcPeriod = new MContractProcPeriod(getCtx(), getJP_ProcPeriod_End_InOut_ID(), get_TrxName());
			MContractProcPeriod order_End_ProcPeriod_InOut = order_Calender.getContractProcessPeriod(getCtx(), inout_ProcPeriod.getEndDate());
			MContractProcPeriod invoice_ProcPeriod = new MContractProcPeriod(getCtx(), getJP_ProcPeriod_End_Inv_ID(), get_TrxName());
			MContractProcPeriod order_End_ProcPeriod_Inv = order_Calender.getContractProcessPeriod(getCtx(), invoice_ProcPeriod.getEndDate());

			if(order_End_ProcPeriod_InOut.getJP_ContractProcPeriod_ID() != order_End_ProcPeriod_Inv.getJP_ContractProcPeriod_ID())
			{
				//Inconsistency End Contract Process Period between Contract Process Period(In/Out) and Contract Process Period(Invoice).
				m_processMsg = Msg.getMsg(getCtx(), "JP_InconsistencyEndProcPeriodBetweenDerivativeDoc");
				return false;
			}

		}else if(getJP_DerivativeDocPolicy_InOut().equals("DD") && getJP_DerivativeDocPolicy_Inv().equals("DD")){

			;//Nothing to do;

		}else{

			//Inconsistency between Derivativ Doc Policy(InOut) and Derivative Doc Policy(Invoice)
			m_processMsg = Msg.getMsg(getCtx(),"JP_Inconsistency",new Object[]{Msg.getElement(Env.getCtx(), "JP_DerivativeDocPolicy_InOut"),Msg.getElement(Env.getCtx(), "JP_DerivativeDocPolicy_Inv")});
			return false;

		}//Check Contract Process Period correspondence between Derivative shi/Recipt And Derivative invoice

		return true;
	}

	private boolean checkDerivativeInOutAndBaseDocPeriodCorrespondence(boolean newRecord, boolean isCheckCalendarMandatory)
	{
		/** Check Contract Process Period correspondence between Derivative shi/Recipt And  Base doc Order */
		if(getJP_DerivativeDocPolicy_InOut().equals("LP"))
		{
			if(!isCheckCalendarMandatory && (getJP_ProcPeriod_Lump_InOut_ID()==0 || getParent().getJP_ContractCalender_ID()==0 ) )
				return true;

			//It is necessary to be single order Contract process Period
			MContractCalender order_Calender = MContractCalender.get(getCtx(), getParent().getJP_ContractCalender_ID());
			MContractProcPeriod inout_ProcPeriod = new MContractProcPeriod(getCtx(), getJP_ProcPeriod_Lump_InOut_ID(), get_TrxName());
			MContractProcPeriod order_Start_ProcPeriod = order_Calender.getContractProcessPeriod(getCtx(), inout_ProcPeriod.getStartDate());
			MContractProcPeriod order_End_ProcPeriod = order_Calender.getContractProcessPeriod(getCtx(), inout_ProcPeriod.getEndDate());
			if(order_Start_ProcPeriod.getJP_ContractProcPeriod_ID() !=order_End_ProcPeriod.getJP_ContractProcPeriod_ID())
			{
				//A certain point of Derivative Contract process period step over Base Doc contract Process period.
				m_processMsg = Msg.getMsg(getCtx(), "JP_StepOverBaseDocContractProcessPeriod");
				return false;
			}

		}

		return true;
	}

	private boolean checkDerivativeInvoiceAndBaseDocPeriodCorrespondence(boolean newRecord, boolean isCheckCalendarMandatory)
	{
		/** Check Contract Process Period correspondence between Derivative shi/Recipt And  Base doc Order */
		if(getJP_DerivativeDocPolicy_Inv().equals("LP"))
		{
			if(!isCheckCalendarMandatory && (getJP_ProcPeriod_Lump_Inv_ID()==0 || getParent().getJP_ContractCalender_ID()==0 ))
				return true;

			//It is necessary to be single order Contract process Period
			MContractCalender order_Calender = MContractCalender.get(getCtx(), getParent().getJP_ContractCalender_ID());
			MContractProcPeriod inv_ProcPeriod = new MContractProcPeriod(getCtx(), getJP_ProcPeriod_Lump_Inv_ID(), get_TrxName());
			MContractProcPeriod order_Start_ProcPeriod = order_Calender.getContractProcessPeriod(getCtx(), inv_ProcPeriod.getStartDate());
			MContractProcPeriod order_End_ProcPeriod = order_Calender.getContractProcessPeriod(getCtx(), inv_ProcPeriod.getEndDate());
			if(order_Start_ProcPeriod.getJP_ContractProcPeriod_ID() != order_End_ProcPeriod.getJP_ContractProcPeriod_ID())
			{
				//A certain point of Derivative Contract process period step over Base Doc contract Process period.
				m_processMsg = Msg.getMsg(getCtx(), "JP_StepOverBaseDocContractProcessPeriod");
				return false;
			}

		}

		return true;
	}


	private boolean checkContainContractProcDate(MContractProcPeriod period)
	{
		if(getParent().getJP_ContractProcDate_To() == null)
		{
			if(getParent().getJP_ContractProcDate_From().compareTo(period.getEndDate()) <= 0)
			{
				return true;
			}else{
				return false;
			}
		}else{
			if(getParent().getJP_ContractProcDate_From().compareTo(period.getEndDate()) <= 0
					&& (getParent().getJP_ContractProcDate_To().compareTo(period.getStartDate()) >= 0) )
			{
				return true;
			}else{
				return false;
			}
		}
	}

	private boolean checkContainContractProcDate(Timestamp date)
	{
		if(getParent().getJP_ContractProcDate_To() == null)
		{
			if(getParent().getJP_ContractProcDate_From().compareTo(date) <= 0)
			{
				return true;
			}else{
				return false;
			}
		}else{
			if(getParent().getJP_ContractProcDate_From().compareTo(date) <= 0
					&& (getParent().getJP_ContractProcDate_To().compareTo(date) >= 0) )
			{
				return true;
			}else{
				return false;
			}
		}
	}

	/**
	 * 	Get Currency Precision
	 *	@return precision
	 */
	public int getPrecision()
	{
		if (m_precision != null)
			return m_precision.intValue();

		String sql = "SELECT c.StdPrecision "
			+ "FROM C_Currency c INNER JOIN JP_ContractContent x ON (x.C_Currency_ID=c.C_Currency_ID) "
			+ "WHERE x.JP_ContractContent_ID=?";
		int i = DB.getSQLValue(get_TrxName(), sql, getJP_ContractContent_ID());
		if (i < 0)
		{
			log.warning("getPrecision = " + i + " - set to 2");
			i = 2;
		}
		m_precision = Integer.valueOf(i);
		return m_precision.intValue();
	}	//	getPrecision

	public void clearParent()
	{
		this.m_parent = null;
	}
}
