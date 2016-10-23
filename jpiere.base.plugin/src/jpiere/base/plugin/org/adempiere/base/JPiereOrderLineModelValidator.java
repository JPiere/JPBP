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
package jpiere.base.plugin.org.adempiere.base;

import java.math.BigDecimal;

import jpiere.base.plugin.org.adempiere.model.JPiereTaxProvider;
import jpiere.base.plugin.util.JPiereUtil;

import org.compiere.model.MAcctSchema;
import org.compiere.model.MClient;
import org.compiere.model.MCurrency;
import org.compiere.model.MOrderLine;
import org.compiere.model.MSysConfig;
import org.compiere.model.MTax;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.model.ProductCost;
import org.compiere.util.CCache;
import org.compiere.util.Env;
import org.compiere.util.Msg;

public class JPiereOrderLineModelValidator implements ModelValidator {

	//Qty
	static final String SALES_ORDER_UNDER_QUANTITY_PROHIBIT = "SUP";	
	static final String SALES_ORDER_CHANGE_QUANTITY_PROHIBIT = "SCP";
	static final String PURCHASE_ORDER_UNDER_QUANTITY_PROHIBIT = "PUP";	
	static final String PURCHASE_ORDER_CHANGE_QUANTITY_PROHIBIT = "PCP";
	static final String BOTH_ORDER_UNDER_QUANTITY_PROHIBIT = "BUP";	
	static final String BOTH_ORDER_CHANGE_QUANTITY_PROHIBIT = "BCP";
	
	//Amt
	static final String SALES_ORDER_CHANGE_AMOUNT_PROHIBIT = "SCP";
	static final String PURCHASE_ORDER_CHANGE_AMOUNT_PROHIBIT = "PCP";
	static final String BOTH_ORDER_CHANGE_AMOUNT_PROHIBIT = "BCP";
	static final String BOTH_ORDER_NO_PROHIBIT = "NON";
	
	private int AD_Client_ID = -1;


	@Override
	public void initialize(ModelValidationEngine engine, MClient client) {
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();
		engine.addModelChange(MOrderLine.Table_Name, this);

	}

	@Override
	public int getAD_Client_ID() {
		return AD_Client_ID;
	}

	@Override
	public String login(int AD_Org_ID, int AD_Role_ID, int AD_User_ID) {

		return null;
	}

	@Override
	public String modelChange(PO po, int type) throws Exception {

		//JPIERE-0165
		if(type == ModelValidator.TYPE_BEFORE_NEW ||
				(type == ModelValidator.TYPE_BEFORE_CHANGE && (po.is_ValueChanged("LineNetAmt")|| po.is_ValueChanged("C_Tax_ID"))))
		{
			BigDecimal taxAmt = Env.ZERO;
			MOrderLine ol = (MOrderLine)po;
			MTax m_tax = MTax.get(Env.getCtx(), ol.getC_Tax_ID());
			if(m_tax == null)
				return null;

			IJPiereTaxProvider taxCalculater = JPiereUtil.getJPiereTaxProvider(m_tax);
			if(taxCalculater != null)
			{
				taxAmt = taxCalculater.calculateTax(m_tax, ol.getLineNetAmt(), ol.isTaxIncluded()
						, MCurrency.getStdPrecision(po.getCtx(), ol.getParent().getC_Currency_ID())
						, JPiereTaxProvider.getRoundingMode(ol.getParent().getC_BPartner_ID(), ol.getParent().isSOTrx(), m_tax.getC_TaxProvider()));
			}else{
				taxAmt = m_tax.calculateTax(ol.getLineNetAmt(), ol.isTaxIncluded(), MCurrency.getStdPrecision(ol.getCtx(), ol.getParent().getC_Currency_ID()));
			}

			if(ol.isTaxIncluded())
			{
				ol.set_ValueNoCheck("JP_TaxBaseAmt",  ol.getLineNetAmt().subtract(taxAmt));
			}else{
				ol.set_ValueNoCheck("JP_TaxBaseAmt",  ol.getLineNetAmt());
			}

			ol.set_ValueOfColumn("JP_TaxAmt", taxAmt);
		}
		
		//JPIERE-0202:Set cost to OrderLine automatically.
		if(type == ModelValidator.TYPE_BEFORE_NEW || type == ModelValidator.TYPE_BEFORE_CHANGE)
		{	
			MOrderLine ol = (MOrderLine)po;
			if(ol.getM_Product_ID() != 0 
					&& ( (type == ModelValidator.TYPE_BEFORE_NEW && ol.getPriceCost().compareTo(Env.ZERO) == 0) 
							|| (type == ModelValidator.TYPE_BEFORE_CHANGE && ol.is_ValueChanged("QtyOrdered") && !ol.is_ValueChanged("PriceCost")) )
					&& !MSysConfig.getValue("JPIERE_SET_COST_TO_ORDER-LINE", "NO", Env.getAD_Client_ID(Env.getCtx())).equals("NO"))
			{

				String config = MSysConfig.getValue("JPIERE_SET_COST_TO_ORDER-LINE", "NO", Env.getAD_Client_ID(Env.getCtx()));
				if(config.equals("BT"))//Both SO and PO
					setPriceCost(ol);
				else if(config.equals("SO") && ol.getParent().isSOTrx())
					setPriceCost(ol);
				else if(config.equals("PO") && !ol.getParent().isSOTrx())
					setPriceCost(ol);	
			}
		}
		
		//JPIEERE-0207:Order Re-Activate Check
		if(type == ModelValidator.TYPE_BEFORE_CHANGE)
		{
			MOrderLine ol = (MOrderLine)po;
			
			//Check Qty
			if(ol.is_ValueChanged("QtyOrdered") && ( ol.getQtyDelivered().compareTo(Env.ZERO) != 0 || ol.getQtyInvoiced().compareTo(Env.ZERO) != 0 ))
			{
				String  QTY_CHECK = MSysConfig.getValue("JP_ORDER_REACTIVATE_QTY_CHECK", BOTH_ORDER_NO_PROHIBIT, ol.getAD_Client_ID(), ol.getAD_Org_ID());
				if(!QTY_CHECK.equals(BOTH_ORDER_NO_PROHIBIT))
				{
					if(ol.getParent().isSOTrx())
					{
						if(QTY_CHECK.equals(SALES_ORDER_CHANGE_QUANTITY_PROHIBIT) || QTY_CHECK.equals(BOTH_ORDER_CHANGE_QUANTITY_PROHIBIT))
						{
							return  Msg.getMsg(Env.getCtx(), "JP_CanNotChangeQtyForQtyDeliveredOrQtyInvoiced");//You can not change Qty. Because Delivered or Invoiced Qty are not 0.
							
						}else if(QTY_CHECK.equals(SALES_ORDER_UNDER_QUANTITY_PROHIBIT) || QTY_CHECK.equals(BOTH_ORDER_UNDER_QUANTITY_PROHIBIT)){
							
							if(ol.getQtyOrdered().compareTo(ol.getQtyDelivered()) < 0 || ol.getQtyOrdered().compareTo(ol.getQtyInvoiced()) < 0 )
							{
								return Msg.getMsg(Env.getCtx(), "JP_CanNotChangeQtyLessThanQtyDeliveredOrQtyInvoiced");//You can not change Qty less than Delivered or Invoiced Qty.
							}
						}
						
						
					}else{
						
						if(QTY_CHECK.equals(PURCHASE_ORDER_CHANGE_QUANTITY_PROHIBIT) || QTY_CHECK.equals(BOTH_ORDER_CHANGE_QUANTITY_PROHIBIT))
						{
							return Msg.getMsg(Env.getCtx(), "JP_CanNotChangeQtyForQtyDeliveredOrQtyInvoiced");//You can not change Qty. Because Delivered or Invoiced Qty are not 0.
							
						}else if(QTY_CHECK.equals(PURCHASE_ORDER_UNDER_QUANTITY_PROHIBIT) || QTY_CHECK.equals(BOTH_ORDER_UNDER_QUANTITY_PROHIBIT)){
							
							if(ol.getQtyOrdered().compareTo(ol.getQtyDelivered()) < 0 || ol.getQtyOrdered().compareTo(ol.getQtyInvoiced()) < 0 )
							{
								return Msg.getMsg(Env.getCtx(), "JP_CanNotChangeQtyLessThanQtyDeliveredOrQtyInvoiced");//You can not change Qty less than Delivered or Invoiced Qty.
							}
						}						
					}
				}//if(!QTY_CHECK.equals(BOTH_ORDER_NO_PROHIBIT))
			}//Check Qty
			
			//Check Amount
			if((ol.is_ValueChanged("PriceEntered") || ol.is_ValueChanged("C_Tax_ID"))  && ol.getQtyInvoiced().compareTo(Env.ZERO) != 0 )
			{
				String  AMT_CHECK = MSysConfig.getValue("JP_ORDER_REACTIVATE_AMT_CHECK", BOTH_ORDER_NO_PROHIBIT, ol.getAD_Client_ID(), ol.getAD_Org_ID());
				if(!AMT_CHECK.equals(BOTH_ORDER_NO_PROHIBIT))
				{
					if(ol.getParent().isSOTrx())
					{
						if(AMT_CHECK.equals(SALES_ORDER_CHANGE_AMOUNT_PROHIBIT) || AMT_CHECK.equals(BOTH_ORDER_CHANGE_AMOUNT_PROHIBIT))
						{
							return Msg.getMsg(Env.getCtx(), "JP_CanNotChangeAmountForQtyInvoiced");//You can not change Amount. Because invoice was issued.
						}
						
					}else{
						if(AMT_CHECK.equals(PURCHASE_ORDER_CHANGE_AMOUNT_PROHIBIT) || AMT_CHECK.equals(BOTH_ORDER_CHANGE_AMOUNT_PROHIBIT))
						{
							return Msg.getMsg(Env.getCtx(), "JP_CanNotChangeAmountForQtyInvoiced");//You can not change Amount. Because invoice was issued.
							
						}
					}
				}
			}//Check Amount
			
		}

		return null;
	}

	@Override
	public String docValidate(PO po, int timing) {

		return null;
	}
	
	//JPIERE-0202
	private void setPriceCost(MOrderLine ol)
	{
		MAcctSchema as = MAcctSchema.get(Env.getCtx(), Env.getContextAsInt(Env.getCtx(), "$C_AcctSchema_ID"));
		BigDecimal cost = getProductCosts(ol, as, ol.getAD_Org_ID(), true);
		ol.setPriceCost(cost);
	}
	
	//JPIERE-0202
	private BigDecimal getProductCosts (MOrderLine ol, MAcctSchema as, int AD_Org_ID, boolean zeroCostsOK)
	{
		ProductCost pc = getProductCost(ol);
		pc.setQty(ol.getQtyOrdered());
		int C_OrderLine_ID = ol.getC_OrderLine_ID();
		String costingMethod = null;
		BigDecimal costs = pc.getProductCosts(as, AD_Org_ID, costingMethod, C_OrderLine_ID, zeroCostsOK);
		if (costs != null)
			return costs;
		return Env.ZERO;
	}//  getProductCosts
	
	//JPIERE-0202
	private static CCache<String, ProductCost> s_cache = new CCache<String, ProductCost>("OderLineCost", 1000, 10, true);

	//JPIERE-0202
	private ProductCost getProductCost(MOrderLine ol)
	{
		String key = ol.getM_Product_ID() + "-" +ol.getM_AttributeSetInstance_ID();
		ProductCost	m_productCost = s_cache.get(key);
		if(m_productCost == null)
		{
			m_productCost = new ProductCost (Env.getCtx(), 
						ol.getM_Product_ID(), ol.getM_AttributeSetInstance_ID(), ol.get_TrxName());
			s_cache.put(key, m_productCost);
		}
		return m_productCost;
	}	//	getProductCost

}
