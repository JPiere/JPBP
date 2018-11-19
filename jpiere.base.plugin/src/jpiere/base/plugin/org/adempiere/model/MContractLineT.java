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

import java.math.RoundingMode;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.logging.Level;

import org.adempiere.exceptions.ProductNotOnPriceListException;
import org.compiere.model.MCurrency;
import org.compiere.model.MDocType;
import org.compiere.model.MProductPricing;
import org.compiere.model.MRole;
import org.compiere.util.CCache;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;



/**
 * JPIERE-0363
 *
 * @author Hideaki Hagiwara
 *
 */
public class MContractLineT extends X_JP_ContractLineT {

	/** Parent					*/
	protected MContractContentT			m_parent = null;
	protected Integer			m_precision = null;
	protected int 			m_M_PriceList_ID = 0;
	protected boolean			m_IsSOTrx = true;
	protected MProductPricing	m_productPrice = null;

	public MContractLineT(Properties ctx, int JP_ContractLineT_ID, String trxName)
	{
		super(ctx, JP_ContractLineT_ID, trxName);
	}

	public MContractLineT(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}



	public MContractContentT getParent()
	{
		if (m_parent == null && getJP_ContractContentT_ID() > 0)
		{
			m_parent = new MContractContentT(getCtx(), getJP_ContractContentT_ID(), get_TrxName());
		}

		return m_parent;
	}	//	getParent




	@Override
	protected boolean beforeSave(boolean newRecord)
	{
		if((newRecord && getJP_ContractContentT_ID() > 0) || (getJP_ContractContentT_ID() > 0 && is_ValueChanged(MContractLineT.COLUMNNAME_JP_ContractContentT_ID)))
		{
			setJP_ContractType(getParent().getJP_ContractType());
			setDocBaseType(getParent().getDocBaseType());
			setJP_CreateDerivativeDocPolicy(getParent().getJP_CreateDerivativeDocPolicy());
			setOrderType(getParent().getOrderType());

			setM_PriceList_ID(getParent().getM_PriceList_ID());
			setIsSOTrx(getParent().isSOTrx());
			setC_Currency_ID(getParent().getC_Currency_ID());
			setIsTaxIncluded(getParent().isTaxIncluded());
		}

		if(Util.isEmpty(getJP_ContractType()))
		{
			Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractType")};
			log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs));
			return false;
		}


		//Check DocBaseType
		if(Util.isEmpty(getDocBaseType()))
		{
			Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "DocBaseType")};
			log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs));
			return false;

		}else if( (newRecord && getJP_ContractContentT_ID() == 0) || (is_ValueChanged(MContractLineT.COLUMNNAME_DocBaseType) && getJP_ContractContentT_ID() == 0)
				|| (is_ValueChanged(MContractLineT.COLUMNNAME_JP_CreateDerivativeDocPolicy) && getJP_ContractContentT_ID() == 0) ){

			if(getDocBaseType().equals("POO") || getDocBaseType().equals("SOO") )
			{
				if(getJP_CreateDerivativeDocPolicy() != null
						&& !getJP_CreateDerivativeDocPolicy().equals(MContractContentT.JP_CREATEDERIVATIVEDOCPOLICY_Manual))
				{
					if(!getOrderType().equals(MDocType.DOCSUBTYPESO_StandardOrder)
							&& !getOrderType().equals(MDocType.DOCSUBTYPESO_Quotation)
							&& !getOrderType().equals(MDocType.DOCSUBTYPESO_Proposal) )
					{
						//Base doc DocType that you selected is available When Create Derivative Doc policy is Manual.
						log.saveError("Error", Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), MContractContentT.COLUMNNAME_JP_BaseDocDocType_ID)
												+ "  :  " + Msg.getMsg(getCtx(), "JP_BaseDocDocType_CreateDerivativeDocPolicy"));
						return false;
					}
				}

			}else{

				setOrderType("--");

			}
		}

		//Check JP_CreateDerivativeDocPolicy
		if( (newRecord && getJP_ContractContentT_ID() == 0) || (is_ValueChanged(MContractLineT.COLUMNNAME_JP_CreateDerivativeDocPolicy) && getJP_ContractContentT_ID() == 0))
		{

			if(getJP_ContractType().equals(MContractLineT.JP_CONTRACTTYPE_PeriodContract) && (getDocBaseType().equals("SOO") || getDocBaseType().equals("POO")))
			{
				if(Util.isEmpty(getJP_CreateDerivativeDocPolicy()))
				{
					Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_CreateDerivativeDocPolicy")};
					log.saveError("Error",Msg.getMsg(Env.getCtx(), "JP_InCaseOfPeriodContract") + Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs));
					return false;
				}

				if(getDocBaseType().equals("POO") || getDocBaseType().equals("SOO") )
				{
					if(!getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_Manual))
					{
						if(!getOrderType().equals(MDocType.DOCSUBTYPESO_StandardOrder)
								&& !getOrderType().equals(MDocType.DOCSUBTYPESO_Quotation)
								&& !getOrderType().equals(MDocType.DOCSUBTYPESO_Proposal) )
						{
							//Base doc DocType that you selected is available When Create Derivative Doc policy is Manual.
							log.saveError("Error", Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), MContractContentT.JP_CREATEDERIVATIVEDOCPOLICY_Manual)
													+ "  :  " + Msg.getMsg(getCtx(), "JP_BaseDocDocType_CreateDerivativeDocPolicy"));
							return false;
						}
					}
				}

			}else {

				setJP_CreateDerivativeDocPolicy(null);

			}
		}

		//Check Period Contract - Derivative Doc Policy
		if(getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
		{
			if(newRecord
					|| is_ValueChanged(MContractLineT.COLUMNNAME_JP_BaseDocLinePolicy)
					|| is_ValueChanged(MContractLineT.COLUMNNAME_JP_ProcPeriodOffs_Lump)
					|| is_ValueChanged(MContractLineT.COLUMNNAME_JP_ProcPeriodOffs_Start)
					|| is_ValueChanged(MContractLineT.COLUMNNAME_JP_ProcPeriodOffs_End)
					|| is_ValueChanged(MContractLineT.COLUMNNAME_JP_DerivativeDocPolicy_InOut)
					|| is_ValueChanged(MContractLineT.COLUMNNAME_JP_ContractCalRef_InOut_ID)
					|| is_ValueChanged(MContractLineT.COLUMNNAME_JP_ContractProcRef_InOut_ID)
					|| is_ValueChanged(MContractLineT.COLUMNNAME_JP_ProcPeriodOffs_Lump_InOut)
					|| is_ValueChanged(MContractLineT.COLUMNNAME_JP_ProcPeriodOffs_Start_InOut)
					|| is_ValueChanged(MContractLineT.COLUMNNAME_JP_ProcPeriodOffs_End_InOut)
					|| is_ValueChanged(MContractLineT.COLUMNNAME_JP_DerivativeDocPolicy_Inv)
					|| is_ValueChanged(MContractLineT.COLUMNNAME_JP_ContractCalRef_Inv_ID)
					|| is_ValueChanged(MContractLineT.COLUMNNAME_JP_ContractProcRef_Inv_ID)
					|| is_ValueChanged(MContractLineT.COLUMNNAME_JP_ProcPeriodOffs_Lump_Inv)
					|| is_ValueChanged(MContractLineT.COLUMNNAME_JP_ProcPeriodOffs_Start_Inv)
					|| is_ValueChanged(MContractLineT.COLUMNNAME_JP_ProcPeriodOffs_End_Inv)
					|| is_ValueChanged(MContractLineT.COLUMNNAME_QtyEntered)
					|| is_ValueChanged(MContractLineT.COLUMNNAME_QtyOrdered)
					|| is_ValueChanged(MContractLineT.COLUMNNAME_MovementQty)
					|| is_ValueChanged(MContractLineT.COLUMNNAME_QtyInvoiced)
					)
			{
				if(!beforeSavePeriodContractCheck(newRecord))
					return false;
			}

		}//Period Contract


		//Check Spot Contract - Derivative Doc Policy
		if(getJP_ContractType().equals(MContractT.JP_CONTRACTTYPE_SpotContract))
		{
			setNullCreateBaseDocLineInfo();
			setNullCreateDerivativeInOutInfo();
			setNullCreateDerivativeInvoiceInfo();
		}

		//	Get Defaults from Parent
		if (getC_BPartner_ID() == 0 || getC_BPartner_Location_ID() == 0)
			setContentTemplateInfo();
		if (m_M_PriceList_ID == 0)
			setHeaderInfo();

		//	Charge
		if (getC_Charge_ID() != 0 && getM_Product_ID() != 0)
				setM_Product_ID(0);

		if (getM_Product_ID() > 0)
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

			boolean enforce = m_IsSOTrx && getM_PriceList().isEnforcePriceLimit();
			if (enforce && MRole.getDefault().isOverwritePriceLimit())
				enforce = false;
			//	Check Price Limit?
			if (enforce && getPriceLimit() != Env.ZERO
			  && getPriceActual().compareTo(getPriceLimit()) < 0)
			{
				log.saveError("UnderLimitPrice", "PriceEntered=" + getPriceEntered() + ", PriceLimit=" + getPriceLimit());
				return false;
			}


			if (!m_productPrice.isCalculated())
			{
				throw new ProductNotOnPriceListException(m_productPrice, getLine());
			}
		}

		return true;

	}//beforeSave


	public void setContentTemplateInfo ()
	{
		m_parent = getParent();
		if(m_parent != null)
		{
			setC_BPartner_ID(m_parent.getC_BPartner_ID());
			setC_BPartner_Location_ID(m_parent.getC_BPartner_Location_ID());
		}
		//
		setHeaderInfo();
	}


	public void setHeaderInfo ()
	{
		m_parent = getParent();
		if(m_parent == null)
		{
			m_precision = Integer.valueOf(MCurrency.getStdPrecision(getCtx(), getC_Currency_ID()));
			m_M_PriceList_ID = getM_PriceList_ID();
			m_IsSOTrx = isSOTrx();

		}else {

			m_precision = Integer.valueOf(m_parent.getPrecision());
			m_M_PriceList_ID = m_parent.getM_PriceList_ID();
			m_IsSOTrx = m_parent.isSOTrx();

		}
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
	}	//	setPrice

	protected MProductPricing getProductPricing (int M_PriceList_ID)
	{
		m_productPrice = new MProductPricing (getM_Product_ID(),
			getC_BPartner_ID(), getQtyOrdered(), m_IsSOTrx, get_TrxName());
		m_productPrice.setM_PriceList_ID(M_PriceList_ID);
//		m_productPrice.setPriceDate(getDateOrdered());
		//
		m_productPrice.calculatePrice();
		return m_productPrice;
	}	//	getProductPrice


	@Override
	protected boolean afterSave(boolean newRecord, boolean success)
	{
		if (!success)
			return success;
//		if (getParent().isProcessed())
//			return success;

		if( (newRecord || is_ValueChanged(MContractLineT.COLUMNNAME_LineNetAmt)) && getJP_ContractContentT_ID() > 0)
		{
			String sql = "UPDATE JP_ContractContentT cct"
					+ " SET TotalLines = "
					    + "(SELECT COALESCE(SUM(LineNetAmt),0) FROM JP_ContractLineT clt WHERE cct.JP_ContractContentT_ID=clt.JP_ContractContentT_ID)"
					+ "WHERE JP_ContractContenTt_ID=?";
				int no = DB.executeUpdate(sql, new Object[]{Integer.valueOf(getJP_ContractContentT_ID())}, false, get_TrxName(), 0);
				if (no != 1)
				{
					log.warning("(1) #" + no);
					return false;
				}
		}

		return success;
	}


	/**	Cache				*/
	private static CCache<Integer,MContractLineT>	s_cache = new CCache<Integer,MContractLineT>(Table_Name, 20);

	/**
	 * 	Get from Cache
	 *	@param ctx context
	 *	@param JP_ContractLineT_ID id
	 *	@return Contract Calender
	 */
	public static MContractLineT get (Properties ctx, int JP_ContractLineT_ID)
	{
		Integer ii = Integer.valueOf(JP_ContractLineT_ID);
		MContractLineT retValue = (MContractLineT)s_cache.get(ii);
		if (retValue != null)
			return retValue;
		retValue = new MContractLineT (ctx, JP_ContractLineT_ID, null);
		if (retValue.get_ID () != 0)
			s_cache.put (JP_ContractLineT_ID, retValue);
		return retValue;
	}	//	get

	private boolean beforeSavePeriodContractCheck(boolean newRecord)
	{

		if(Util.isEmpty(getJP_CreateDerivativeDocPolicy()))
		{
			//Check JP_CreateDerivativeDocPolicy
			if(getOrderType().equals(MContractContent.ORDERTYPE_StandardOrder))
			{
				Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_CreateDerivativeDocPolicy")};
				String msg = Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs);
				log.saveError("Error",msg);
				return false;

			}else{//DocBaseType IN ('API','ARI')

				if(!checkCreateBaseDocLineInfo(newRecord))
					return false;
				setNullCreateDerivativeInOutInfo();
				setNullCreateDerivativeInvoiceInfo();

			}

		//getParent().getJP_CreateDerivativeDocPolicy() is Not Null
		}else{

			if(getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_Manual))
			{
				if(!checkCreateBaseDocLineInfo(newRecord))
					return false;
				setNullCreateDerivativeInOutInfo();
				setNullCreateDerivativeInvoiceInfo();

			}else if(getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceiptInvoice)){

				setNullCreateBaseDocLineInfo();
				if(!checkCreateDerivativeInOutInfo(newRecord))
					return false;
				if(!checkCreateDerivativeInvoiceInfo(newRecord))
					return false;

				/** Check Derivative Doc Policy correspondence between Derivative shi/Recipt And Derivative invoice */
				if( (getJP_DerivativeDocPolicy_InOut().equals("LP") && getJP_DerivativeDocPolicy_Inv().equals("LP"))
						|| (getJP_DerivativeDocPolicy_InOut().equals("LP") && getJP_DerivativeDocPolicy_Inv().equals("PB"))
						|| (getJP_DerivativeDocPolicy_InOut().equals("PB") && getJP_DerivativeDocPolicy_Inv().equals("LP"))
						)
				{
					;//It is ok in this case

				}else if(getJP_DerivativeDocPolicy_InOut().equals("PB") && getJP_DerivativeDocPolicy_Inv().equals("PB")){

					;//It is ok in this case

				}else if(getJP_DerivativeDocPolicy_InOut().equals("PS") && getJP_DerivativeDocPolicy_Inv().equals("PS")){

					;//It is ok in this case

				}else if(getJP_DerivativeDocPolicy_InOut().equals("PE") && getJP_DerivativeDocPolicy_Inv().equals("PE")){

					;//It is ok in this case

				}else if(getJP_DerivativeDocPolicy_InOut().equals("DD") && getJP_DerivativeDocPolicy_Inv().equals("DD")){


					;//It is ok in this case

				}else{

					//Inconsistency between Derivativ Doc Policy(InOut) and Derivative Doc Policy(Invoice)
					log.saveError("Error",Msg.getMsg(getCtx(),"JP_Inconsistency",new Object[]{Msg.getElement(Env.getCtx(), "JP_DerivativeDocPolicy_InOut"),Msg.getElement(Env.getCtx(), "JP_DerivativeDocPolicy_Inv")}));
					return false;

				}//Check Derivative Doc Policy correspondence between Derivative shi/Recipt And Derivative invoice


			}else if(getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceipt)){

				setNullCreateBaseDocLineInfo();
				if(!checkCreateDerivativeInOutInfo(newRecord))
					return false;
				setNullCreateDerivativeInvoiceInfo();

			}else if(getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateInvoice)){

				setNullCreateBaseDocLineInfo();
				setNullCreateDerivativeInOutInfo();
				if(!checkCreateDerivativeInvoiceInfo(newRecord))
					return false;

			}else{

				Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractDerivativeDocPolicy_ID")};
				String msg = Msg.getMsg(Env.getCtx(), "JP_InCaseOfPeriodContract") + Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs);
				log.saveError("Error",msg);
				return false;

			}//if(getParent().getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_Manual))

		}//if(Util.isEmpty(getParent().getJP_CreateDerivativeDocPolicy()))

		return true;
	}

	private void setNullCreateBaseDocLineInfo()
	{
		setJP_BaseDocLinePolicy(null);
		setJP_ProcPeriodOffs_Lump(0);
		setJP_ProcPeriodOffs_Start(0);
		setJP_ProcPeriodOffs_End(0);
	}

	private void setNullCreateDerivativeInOutInfo()
	{
		setJP_DerivativeDocPolicy_InOut(null);
		setJP_ContractCalRef_InOut_ID(0);
		setJP_ContractProcRef_InOut_ID(0);
		setJP_ProcPeriodOffs_Lump_InOut(0);
		setJP_ProcPeriodOffs_Start_InOut(0);
		setJP_ProcPeriodOffs_End_InOut(0);
	}

	private void setNullCreateDerivativeInvoiceInfo()
	{
		setJP_DerivativeDocPolicy_Inv(null);
		setJP_ContractCalRef_Inv_ID(0);
		setJP_ContractProcRef_Inv_ID(0);
		setJP_ProcPeriodOffs_Lump_Inv(0);
		setJP_ProcPeriodOffs_Start_Inv(0);
		setJP_ProcPeriodOffs_End_Inv(0);
	}

	private boolean checkCreateBaseDocLineInfo(boolean newRecord)
	{
		if(Util.isEmpty(getJP_BaseDocLinePolicy()))//Mandetory
		{
			log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_BaseDocLinePolicy")}));
			return false;
		}

		if(getJP_BaseDocLinePolicy().equals("LP"))
		{
			setJP_ProcPeriodOffs_Start(0);
			setJP_ProcPeriodOffs_End(0);
		}else if(getJP_BaseDocLinePolicy().equals("PS")){
			setJP_ProcPeriodOffs_Lump(0);
			setJP_ProcPeriodOffs_End(0);
		}else if(getJP_BaseDocLinePolicy().equals("PE")){
			setJP_ProcPeriodOffs_Lump(0);
			setJP_ProcPeriodOffs_Start(0);
		}else if(getJP_BaseDocLinePolicy().equals("PB")){
			setJP_ProcPeriodOffs_Lump(0);
			if(getJP_ProcPeriodOffs_Start() > getJP_ProcPeriodOffs_End())
			{
				log.saveError("Error",Msg.getMsg(Env.getCtx(),"Invalid") + Msg.getElement(Env.getCtx(), "JP_ProcPeriodOffs_Start")+" > " +Msg.getElement(Env.getCtx(), "JP_ProcPeriodOffs_End"));
				return false;
			}
		}else if(getJP_BaseDocLinePolicy().equals("DD")){
			setJP_ProcPeriodOffs_Lump(0);
			setJP_ProcPeriodOffs_Start(0);
			setJP_ProcPeriodOffs_End(0);
		}

		return true;
	}

	private boolean checkCreateDerivativeInOutInfo(boolean newRecord)
	{
		if(Util.isEmpty(getJP_DerivativeDocPolicy_InOut()))//Mandetory
		{
			log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_DerivativeDocPolicy_InOut")}));
			return false;
		}

		if(getJP_ContractCalRef_InOut_ID() == 0)//Mandetory
		{
			log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractCalRef_InOut_ID")}));
			return false;
		}

		if(getJP_ContractProcRef_InOut_ID() == 0)//Mandetory
		{
			log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractProcRef_InOut_ID")}));
			return false;
		}

		if(getMovementQty().signum()!=0 && getQtyOrdered().signum() != getMovementQty().signum())
		{
			log.saveError("Error",Msg.getMsg(getCtx(),"JP_Inconsistency",new Object[]{Msg.getElement(Env.getCtx(), "MovementQty"),Msg.getElement(Env.getCtx(), "QtyOrdered")}));
			return false;
		}

		if(getMovementQty().abs().compareTo(getQtyOrdered().abs()) > 0)
		{
			log.saveError("Error",Msg.getMsg(getCtx(),"JP_Inconsistency",new Object[]{Msg.getElement(Env.getCtx(), "MovementQty"),Msg.getElement(Env.getCtx(), "QtyOrdered")}));
			return false;
		}


		if(getJP_DerivativeDocPolicy_InOut().equals("LP"))
		{
			setJP_ProcPeriodOffs_Start_InOut(0);
			setJP_ProcPeriodOffs_End_InOut(0);
		}else if(getJP_DerivativeDocPolicy_InOut().equals("PS")){
			setJP_ProcPeriodOffs_Lump_InOut(0);
			setJP_ProcPeriodOffs_End_InOut(0);
		}else if(getJP_DerivativeDocPolicy_InOut().equals("PE")){
			setJP_ProcPeriodOffs_Lump_InOut(0);
			setJP_ProcPeriodOffs_Start_InOut(0);
		}else if(getJP_DerivativeDocPolicy_InOut().equals("PB")){
			setJP_ProcPeriodOffs_Lump_InOut(0);
			if(getJP_ProcPeriodOffs_Start_InOut() > getJP_ProcPeriodOffs_End_InOut())
			{
				log.saveError("Error",Msg.getMsg(Env.getCtx(),"Invalid") + Msg.getElement(Env.getCtx(), "JP_ProcPeriodOffs_Start_InOut")+" > " +Msg.getElement(Env.getCtx(), "JP_ProcPeriodOffs_End_InOut"));
				return false;
			}
		}else{//DD
			setJP_ProcPeriodOffs_Lump_InOut(0);
			setJP_ProcPeriodOffs_Start_InOut(0);
			setJP_ProcPeriodOffs_End_InOut(0);
		}

		return true;
	}

	private boolean checkCreateDerivativeInvoiceInfo(boolean newRecord)
	{
		//Invoice
		if(Util.isEmpty(getJP_DerivativeDocPolicy_Inv()))
		{
			log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_DerivativeDocPolicy_Inｖ")}));
			return false;
		}

		if(getJP_ContractCalRef_Inv_ID() == 0)
		{
			log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractCalRef_Inv_ID")}));
			return false;
		}

		if(getJP_ContractProcRef_Inv_ID() == 0)
		{
			log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractProcRef_Inv_ID")}));
			return false;
		}

		if(getQtyOrdered().signum() != 0 && getQtyOrdered().signum() != getQtyInvoiced().signum())
		{
			log.saveError("Error",Msg.getMsg(getCtx(),"JP_Inconsistency",new Object[]{Msg.getElement(Env.getCtx(), "QtyInvoiced"),Msg.getElement(Env.getCtx(), "QtyOrdered")}));
			return false;
		}


		if(getQtyInvoiced().abs().compareTo(getQtyOrdered().abs()) > 0)
		{
			log.saveError("Error",Msg.getMsg(getCtx(),"JP_Inconsistency",new Object[]{Msg.getElement(Env.getCtx(), "QtyInvoiced"),Msg.getElement(Env.getCtx(), "QtyOrdered")}));
			return false;
		}

		if(getJP_DerivativeDocPolicy_Inv().equals("LP"))
		{
			setJP_ProcPeriodOffs_Start_Inv(0);
			setJP_ProcPeriodOffs_End_Inv(0);
		}else if(getJP_DerivativeDocPolicy_Inv().equals("PS")){
			setJP_ProcPeriodOffs_Lump_Inv(0);
			setJP_ProcPeriodOffs_End_Inv(0);
		}else if(getJP_DerivativeDocPolicy_Inv().equals("PE")){
			setJP_ProcPeriodOffs_Lump_Inv(0);
			setJP_ProcPeriodOffs_Start_Inv(0);
		}else if(getJP_DerivativeDocPolicy_Inv().equals("PB")){
			setJP_ProcPeriodOffs_Lump_Inv(0);
			if(getJP_ProcPeriodOffs_Start_Inv() > getJP_ProcPeriodOffs_End_Inv())
			{
				log.saveError("Error",Msg.getMsg(Env.getCtx(),"Invalid") + Msg.getElement(Env.getCtx(), "JP_ProcPeriodOffs_Start_Inv")+" > " +Msg.getElement(Env.getCtx(), "JP_ProcPeriodOffs_End_Inv"));
				return false;
			}
		}else{//DD
			setJP_ProcPeriodOffs_Lump_Inv(0);
			setJP_ProcPeriodOffs_Start_Inv(0);
			setJP_ProcPeriodOffs_End_Inv(0);
		}

		return true;
	}
}
