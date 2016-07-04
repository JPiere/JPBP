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
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;

import jpiere.base.plugin.org.adempiere.base.IJPiereTaxProvider;
import jpiere.base.plugin.util.JPiereUtil;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MCharge;
import org.compiere.model.MCurrency;
import org.compiere.model.MPriceList;
import org.compiere.model.MProduct;
import org.compiere.model.MProductPricing;
import org.compiere.model.MTax;
import org.compiere.model.MTaxProvider;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;

public class MEstimationLine extends X_JP_EstimationLine {
	
	/** Parent					*/
	protected MEstimation			m_parent = null;
	
	protected int 			m_M_PriceList_ID = 0;
	//
	protected boolean			m_IsSOTrx = true;
	//	Product Pricing
	protected MProductPricing	m_productPrice = null;

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

	@Override
	protected boolean beforeSave(boolean newRecord)
	{

		if(getDateOrdered() == null && getParent().getDateOrdered() != null)
		{
			setDateOrdered(getParent().getDateOrdered());
		}
		
		if(getDatePromised() == null && getParent().getDatePromised() != null)
		{
			setDatePromised(getParent().getDatePromised());
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
				if(taxCalculater != null)
				{
					taxAmt = taxCalculater.calculateTax(m_tax, getLineNetAmt(), isTaxIncluded()
							, MCurrency.getStdPrecision(getCtx(), getParent().getC_Currency_ID())
							, JPiereTaxProvider.getRoundingMode(getParent().getC_BPartner_ID(), getParent().isSOTrx(), m_tax.getC_TaxProvider()));
				}else{
					taxAmt = m_tax.calculateTax(getLineNetAmt(), isTaxIncluded(), MCurrency.getStdPrecision(getCtx(), getParent().getC_Currency_ID()));
				}
	
				if(isTaxIncluded())
				{
					set_ValueNoCheck("JP_TaxBaseAmt",  getLineNetAmt().subtract(taxAmt));
				}else{
					set_ValueNoCheck("JP_TaxBaseAmt",  getLineNetAmt());
				}
	
				set_ValueOfColumn("JP_TaxAmt", taxAmt);
				
			}
		}//Tax Calculation
		
		
		return true;
	}

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
	    	return taxCalculater.recalculateTax(provider, this, newRecord);
		}
		
		return success;
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
		m_precision = new Integer(estimation.getPrecision());
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
			setOrder (getParent());
			if (m_precision != null)
				return m_precision.intValue();
		}
		if (getC_Currency_ID() != 0)
		{
			MCurrency cur = MCurrency.get(getCtx(), getC_Currency_ID());
			if (cur.get_ID() != 0)
			{
				m_precision = new Integer (cur.getStdPrecision());
				return m_precision.intValue();
			}
		}
		//	Fallback
		String sql = "SELECT c.StdPrecision "
			+ "FROM C_Currency c INNER JOIN JP_Estimation x ON (x.C_Currency_ID=c.C_Currency_ID) "
			+ "WHERE x.JP_Estimation_ID=?";
		int i = DB.getSQLValue(get_TrxName(), sql, getJP_Estimation_ID());
		m_precision = new Integer(i);
		return m_precision.intValue();
	}	//	getPrecision
	
	/**
	 * 	Set Defaults from Order.
	 * 	Does not set Parent !!
	 * 	@param estimation order
	 */
	public void setOrder (MEstimation estimation)
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
