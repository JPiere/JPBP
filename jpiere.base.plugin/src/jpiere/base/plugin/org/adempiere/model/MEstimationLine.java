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
import java.sql.ResultSet;
import java.util.Properties;
import java.util.logging.Level;

import org.adempiere.base.Core;
import org.adempiere.base.IProductPricing;
import org.adempiere.exceptions.AdempiereException;
import org.adempiere.exceptions.ProductNotOnPriceListException;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MCharge;
import org.compiere.model.MCurrency;
import org.compiere.model.MDocType;
import org.compiere.model.MLocator;
import org.compiere.model.MOrgInfo;
import org.compiere.model.MPriceList;
import org.compiere.model.MProduct;
import org.compiere.model.MResourceAssignment;
import org.compiere.model.MRole;
import org.compiere.model.MSysConfig;
import org.compiere.model.MTax;
import org.compiere.model.MTaxProvider;
import org.compiere.model.MUOM;
import org.compiere.model.MUOMConversion;
import org.compiere.model.ProductCost;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;

import jpiere.base.plugin.org.adempiere.base.IJPiereTaxProvider;
import jpiere.base.plugin.util.JPiereUtil;

public class MEstimationLine extends X_JP_EstimationLine {

	/** Parent					*/
	protected MEstimation			m_parent = null;

	protected int 			m_M_PriceList_ID = 0;
	//
	protected boolean			m_IsSOTrx = true;
	//	Product Pricing
	protected IProductPricing	m_productPrice = null;

	/** Tax							*/
	protected MTax 		m_tax = null;

	/** Cached Currency Precision	*/
	protected Integer			m_precision = null;
	/**	Product					*/
	protected MProduct 		m_product = null;
	/**	Charge					*/
	protected MCharge 		m_charge = null;

	public MEstimationLine(Properties ctx, int JP_EstimationLine_ID, String trxName) {
		super(ctx, JP_EstimationLine_ID, trxName);
	}

	public MEstimationLine(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}


	public MEstimationLine (MEstimation estimation)
	{
		this (estimation.getCtx(), 0, estimation.get_TrxName());
		if (estimation.get_ID() == 0)
			throw new IllegalArgumentException("Header not saved");
		setJP_Estimation_ID (estimation.getJP_Estimation_ID());	//	parent
		setEstimation(estimation);
	}	//	MOrderLine


	@Override
	protected boolean beforeSave(boolean newRecord)
	{
		//	Get Defaults from Parent
		if (getC_BPartner_ID() == 0 || getC_BPartner_Location_ID() == 0
			|| getM_Warehouse_ID() == 0
			|| getC_Currency_ID() == 0)
			setEstimation(getParent());
		if (m_M_PriceList_ID == 0)
			setHeaderInfo(getParent());

		//	Charge
		if (getC_Charge_ID() != 0 && getM_Product_ID() != 0)
				setM_Product_ID(0);

		//No Product
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
			//
			if (!m_productPrice.isCalculated())
			{
				throw new ProductNotOnPriceListException(m_productPrice, getLine());
			}
		}

		//	UOM
		if (getC_UOM_ID() == 0
			&& (getM_Product_ID() != 0
				|| getPriceEntered().compareTo(Env.ZERO) != 0
				|| getC_Charge_ID() != 0))
		{
			int C_UOM_ID = MUOM.getDefault_UOM_ID(getCtx());
			if (C_UOM_ID > 0)
				setC_UOM_ID (C_UOM_ID);
		}
		//	Qty Precision
		if (newRecord || is_ValueChanged("QtyEntered"))
			setQtyEntered(getQtyEntered());
		if (newRecord || is_ValueChanged("QtyOrdered"))
			setQtyOrdered(getQtyOrdered());


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


		//Check UOM
		if(getM_Product_ID() > 0 && (newRecord || is_ValueChanged("M_Product_ID") || is_ValueChanged("C_UOM_ID")))
		{
			MUOMConversion[]  UOMConversions = MUOMConversion.getProductConversions(getCtx(), getM_Product_ID());
			boolean isOK = false;
			for(int i = 0; i < UOMConversions.length; i++ )
			{
				if(getC_UOM_ID() == UOMConversions[i].getC_UOM_ID())
				{
					isOK = true;
					break;
				}else if(getC_UOM_ID() == UOMConversions[i].getC_UOM_To_ID()){
					isOK = true;
					break;
				}
			}

			if(!isOK)
			{
				log.saveError("Error", Msg.getMsg(getCtx(), "NoUOMConversion"));
				return false;
			}

		}


		//JPIERE-0202:Set Cost to Estimation Line
		String config = MSysConfig.getValue("JPIERE_SET_COST_TO_ORDER-LINE", "NO", Env.getAD_Client_ID(Env.getCtx()));
		if(getM_Product_ID() != 0 && !config.equals("NO")
				&& (newRecord || is_ValueChanged("M_Product_ID") || is_ValueChanged("QtyOrdered") || is_ValueChanged("JP_ScheduledCost")) )
		{

			BigDecimal cost = getJP_ScheduledCost();
			if( (newRecord && cost.compareTo(Env.ZERO)==0)
					|| (!newRecord && is_ValueChanged("M_Product_ID") && !is_ValueChanged("JP_ScheduledCost") ) )
			{
				if(config.equals("BT"))//Both SO and PO
					setScheduledCost();
				else if(config.equals("SO") && getParent().isSOTrx())
					setScheduledCost();
				else if(config.equals("PO") && !getParent().isSOTrx())
					setScheduledCost();

				cost = getJP_ScheduledCost();
			}

			if(config.equals("BT"))//Both SO and PO
				setJP_ScheduledCostLineAmt(cost.multiply(getQtyOrdered()));
			else if(config.equals("SO") && getParent().isSOTrx())
				setJP_ScheduledCostLineAmt(cost.multiply(getQtyOrdered()));
			else if(config.equals("PO") && !getParent().isSOTrx())
				setJP_ScheduledCostLineAmt(cost.multiply(getQtyOrdered()));

		}else if(getM_Product_ID() == 0){
			setJP_ScheduledCost(Env.ZERO);
			setJP_ScheduledCostLineAmt(Env.ZERO);
		}
		//JPiere-0202


		//IDEMPIERE-178 Orders and Invoices must disallow amount lines without product/charge
		if (getParent().getC_DocTypeTarget().isChargeOrProductMandatory()) {
			if (getC_Charge_ID() == 0 && getM_Product_ID() == 0 && getPriceEntered().signum() != 0) {
				log.saveError("FillMandatory", Msg.translate(getCtx(), "ChargeOrProductMandatory"));
				return false;
			}
		}


		//JPIERE-0227
		if(getJP_LocatorFrom_ID() != 0 && (newRecord || is_ValueChanged("JP_LocatorFrom_ID")))
		{
			MLocator fromLocator =  MLocator.get(getCtx(), getJP_LocatorFrom_ID());
			MOrgInfo fromLocatorOrgInfo = MOrgInfo.get(getCtx(), fromLocator.getAD_Org_ID(), get_TrxName());
			MOrgInfo lineOrgInfo = MOrgInfo.get(getCtx(), getAD_Org_ID(), get_TrxName());
			if(fromLocatorOrgInfo.get_ValueAsInt("JP_Corporation_ID") != lineOrgInfo.get_ValueAsInt("JP_Corporation_ID"))
			{
				log.saveError("Error", Msg.getMsg(Env.getCtx(), "JP_CanNotCreateMMForDiffCorp"));//You can not create Material Movement doc. Because of different corporation Locator.
				return false;
			}

			MDocType docType = MDocType.get(getCtx(), getParent().getJP_DocTypeSO_ID());
			int JP_DocTypeMM_ID = docType.get_ValueAsInt("JP_DocTypeMM_ID");
			if(JP_DocTypeMM_ID > 0 && (getJP_LocatorFrom_ID() != 0 || getJP_LocatorTo_ID() !=0) )
			{

				if(getJP_LocatorFrom_ID() > 0 && getJP_LocatorTo_ID()==0)
				{
					log.saveError("Error", Msg.getMsg(Env.getCtx(), "JP_PleaseInputToField")+Msg.getElement(Env.getCtx(), "JP_LocatorTo_ID")) ;//Please input a value into the field.
					return false;
				}

				if(getJP_LocatorFrom_ID() == 0 && getJP_LocatorTo_ID() > 0)
				{
					log.saveError("Error",Msg.getMsg(Env.getCtx(), "JP_PleaseInputToField")+Msg.getElement(Env.getCtx(), "JP_LocatorFrom_ID")) ;//Please input a value into the field.
					return false;
				}
				if(getJP_LocatorFrom_ID() == getJP_LocatorTo_ID())
				{
					log.saveError("Error",Msg.getMsg(Env.getCtx(), "JP_SameLocatorMM"));//You are goring to create Inventory Move Doc at same Locator.
					return false;
				}

				if(!MLocator.get(getCtx(), getJP_LocatorFrom_ID()).get_Value("JP_PhysicalWarehouse_ID").equals(MLocator.get(getCtx(), getJP_LocatorTo_ID()).get_Value("JP_PhysicalWarehouse_ID")))
				{
					log.saveError("Error",Msg.getMsg(Env.getCtx(), "JP_CanNotCreateMMforDiffPhyWH"));//You can not create Inventory move doc at Sales Order because of different Physical Warehouse.
					return false;
				}

			}else{

				if(getJP_LocatorFrom_ID() != 0 || getJP_LocatorTo_ID() !=0)
				{
					log.saveError("Error",Msg.getMsg(Env.getCtx(), "JP_CanNotCreateMMforDocType"));//You can not create Inventory move doc because of missing Doc Type setting.
					return false;
				}
			}
		}



		return true;
	}

	/**
	 * 	Set Price for Product and PriceList.
	 * 	Use only if newly created.
	 * 	Uses standard price list of not set by order constructor
	 */
	public void setPrice()
	{
		if (getM_Product_ID() == 0)
			return;
		if (m_M_PriceList_ID == 0)
			throw new IllegalStateException("PriceList unknown!");
		setPrice (m_M_PriceList_ID);
	}

	/**
	 * 	Set Price for Product and PriceList
	 * 	@param M_PriceList_ID price list
	 */
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


	/**
	 * 	Get and calculate Product Pricing
	 *	@param M_PriceList_ID id
	 *	@return product pricing
	 */
	protected IProductPricing getProductPricing (int M_PriceList_ID)
	{
		m_productPrice = Core.getProductPricing();
		m_productPrice.setInitialValues(getM_Product_ID(), getC_BPartner_ID(), getQtyOrdered(), m_IsSOTrx, get_TrxName());
		m_productPrice.setM_PriceList_ID(M_PriceList_ID);
		m_productPrice.setPriceDate(getDateOrdered());
		//
		m_productPrice.calculatePrice();
		return m_productPrice;
	}	//	getProductPrice


	/**
	 * 	Set Qty Entered - enforce entered UOM
	 *	@param QtyEntered
	 */
	public void setQtyEntered (BigDecimal QtyEntered)
	{
		if (QtyEntered != null && getC_UOM_ID() != 0)
		{
			int precision = MUOM.getPrecision(getCtx(), getC_UOM_ID());
			QtyEntered = QtyEntered.setScale(precision, RoundingMode.HALF_UP);
		}
		super.setQtyEntered (QtyEntered);
	}	//	setQtyEntered


	/**
	 * 	Get Product
	 *	@return product or null
	 */
	public MProduct getProduct()
	{
		if (m_product == null && getM_Product_ID() != 0)
			m_product =  MProduct.get (getCtx(), getM_Product_ID());
		return m_product;
	}	//	getProduct

	/**
	 * 	Set Qty Ordered - enforce Product UOM
	 *	@param QtyOrdered
	 */
	public void setQtyOrdered (BigDecimal QtyOrdered)
	{
		MProduct product = getProduct();
		if (QtyOrdered != null && product != null)
		{
			int precision = product.getUOMPrecision();
			QtyOrdered = QtyOrdered.setScale(precision, RoundingMode.HALF_UP);
		}
		super.setQtyOrdered(QtyOrdered);
	}	//	setQtyOrdered


	//JPIERE-0202
	private void setScheduledCost()
	{
		MAcctSchema as = MAcctSchema.get(Env.getCtx(), Env.getContextAsInt(Env.getCtx(), "$C_AcctSchema_ID"));
		BigDecimal cost = getProductCosts(as, getAD_Org_ID(), true);
		setJP_ScheduledCost(cost);
	}

	//JPIERE-0202
	private BigDecimal getProductCosts (MAcctSchema as, int AD_Org_ID, boolean zeroCostsOK)
	{
		ProductCost pc = new ProductCost (Env.getCtx(), getM_Product_ID(), getM_AttributeSetInstance_ID(), get_TrxName());
		pc.setQty(Env.ONE);
		String costingMethod = null;
		BigDecimal costs = pc.getProductCosts(as, AD_Org_ID, costingMethod, 0, zeroCostsOK);
		if (costs != null)
			return costs;
		return Env.ZERO;
	}//  getProductCosts


	@Override
	protected boolean afterSave(boolean newRecord, boolean success) {

		if (!success)
			return success;
		if (getParent().isProcessed())
			return success;
		if (   newRecord
			|| is_ValueChanged(MEstimationLine.COLUMNNAME_C_Tax_ID)
			|| is_ValueChanged(MEstimationLine.COLUMNNAME_LineNetAmt)) {
			MTax m_tax = new MTax(getCtx(), getC_Tax_ID(), get_TrxName());
			IJPiereTaxProvider taxCalculater = JPiereUtil.getJPiereTaxProvider(m_tax);
			MTaxProvider provider = new MTaxProvider(m_tax.getCtx(), m_tax.getC_TaxProvider_ID(), m_tax.get_TrxName());
			if (taxCalculater == null)
				throw new AdempiereException(Msg.getMsg(getCtx(), "TaxNoProvider"));
	    	success = taxCalculater.recalculateTax(provider, this, newRecord);
	    	if(!success)
	    		return false;
		}

		if(!newRecord && is_ValueChanged(MEstimationLine.COLUMNNAME_JP_ScheduledCost))
		{
			String sql = "UPDATE JP_Estimation i"
					+ " SET JP_ScheduledCostTotalLines = "
					    + "(SELECT COALESCE(SUM(JP_ScheduledCostLineAmt),0) FROM JP_EstimationLine il WHERE i.JP_Estimation_ID=il.JP_Estimation_ID)"
					+ "WHERE JP_Estimation_ID=?";
				int no = DB.executeUpdate(sql, new Object[]{Integer.valueOf(getJP_Estimation_ID())}, false, get_TrxName(), 0);
				if (no != 1)
				{
					log.warning("(1) #" + no);
					return false;
				}
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

	/**
	 * 	Get Parent
	 *	@return parent
	 */
	public MEstimation getParent()
	{
		if (m_parent == null)
			m_parent = new MEstimation(getCtx(), getJP_Estimation_ID(), get_TrxName());
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
				"SELECT M_PriceList_ID FROM JP_Estimation WHERE JP_Estimation_ID=?",
				getJP_Estimation_ID());
		}

		MPriceList pl = MPriceList.get(getCtx(), m_M_PriceList_ID, get_TrxName());
		return pl.isTaxIncluded();
	}	//	isTaxIncluded

	/**
	 * 	Set Header Info
	 *	@param order order
	 */
	public void setHeaderInfo (MEstimation estimation)
	{
		m_parent = estimation;
		m_precision = Integer.valueOf(estimation.getPrecision());
		m_M_PriceList_ID = estimation.getM_PriceList_ID();
		m_IsSOTrx = estimation.isSOTrx();
	}	//	setHeaderInfo


	/**
	 * 	Get Currency Precision from Currency
	 *	@return precision
	 */
	public int getPrecision()
	{
		if (m_precision != null)
			return m_precision.intValue();
		//
		if (getC_Currency_ID() == 0)
		{
			setEstimation (getParent());
			if (m_precision != null)
				return m_precision.intValue();
		}
		if (getC_Currency_ID() != 0)
		{
			MCurrency cur = MCurrency.get(getCtx(), getC_Currency_ID());
			if (cur.get_ID() != 0)
			{
				m_precision = Integer.valueOf(cur.getStdPrecision());
				return m_precision.intValue();
			}
		}
		//	Fallback
		String sql = "SELECT c.StdPrecision "
			+ "FROM C_Currency c INNER JOIN JP_Estimation x ON (x.C_Currency_ID=c.C_Currency_ID) "
			+ "WHERE x.JP_Estimation_ID=?";
		int i = DB.getSQLValue(get_TrxName(), sql, getJP_Estimation_ID());
		m_precision = Integer.valueOf(i);
		return m_precision.intValue();
	}	//	getPrecision

	/**
	 * 	Set Defaults from Estimtion.
	 * 	Does not set Parent !!
	 * 	@param MEstimation estimation
	 */
	public void setEstimation (MEstimation estimation)
	{
		setClientOrg(estimation);
		setC_BPartner_ID(estimation.getC_BPartner_ID());
		setC_BPartner_Location_ID(estimation.getC_BPartner_Location_ID());
		setM_Warehouse_ID(estimation.getM_Warehouse_ID());
		setDateOrdered(estimation.getDateOrdered());
		setDatePromised(estimation.getDatePromised());
		setC_Currency_ID(estimation.getC_Currency_ID());
		//
		setHeaderInfo(estimation);	//	sets m_order
		//	Don't set Activity, etc as they are overwrites
	}	//	setOrder

	public void clearParent()
	{
		this.m_parent = null;
	}
}
