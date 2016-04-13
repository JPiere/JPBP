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
package jpiere.base.plugin.org.adempiere.process;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.logging.Level;

import jpiere.base.plugin.org.adempiere.model.MInvValCal;
import jpiere.base.plugin.org.adempiere.model.MInvValCalLine;
import jpiere.base.plugin.org.adempiere.model.MInvValProfile;
import jpiere.base.plugin.util.JPiereInvValUtil;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MClientInfo;
import org.compiere.model.MConversionRate;
import org.compiere.model.MConversionRateUtil;
import org.compiere.model.MCost;
import org.compiere.model.MCostElement;
import org.compiere.model.MProduct;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
import org.compiere.util.Msg;

/**
 * JPIERE-0161 Inventory Valuation Calculate Doc
 *
 * Default Create Inventory Valuation Calculate Doc Line
 *
 *  @author Hideaki Hagiwara
 *
 */
public class DefaultCreateInvValCalLine extends SvrProcess {

	MInvValProfile m_InvValProfile = null;
	MInvValCal m_InvValCal = null;
	int Record_ID = 0;

	@Override
	protected void prepare()
	{
		Record_ID = getRecord_ID();
		if(Record_ID > 0)
		{
			m_InvValCal = new MInvValCal(getCtx(), Record_ID, null);
			m_InvValProfile = MInvValProfile.get(getCtx(), m_InvValCal.getJP_InvValProfile_ID());
		}else{
			log.log(Level.SEVERE, "Record_ID <= 0 ");
		}
	}

	@Override
	protected String doIt() throws Exception
	{
		LinkedHashMap<Integer, BigDecimal> map_Product_Qty = JPiereInvValUtil.getAllQtyBookFromStockOrg(getCtx(), m_InvValCal.getDateValue()
				, m_InvValProfile.getOrgs(), " p.M_Product_Category_ID, p.Value");
		Set<Integer> set_M_Product_IDs = map_Product_Qty.keySet();
		int line = 0;
		MCostElement[] costElements = JPiereInvValUtil.getMaterialStandardCostElements (getCtx());
		MAcctSchema m_AcctSchema =null;
		
		for(Integer M_Product_ID :set_M_Product_IDs)
		{
			MProduct product = MProduct.get(getCtx(), M_Product_ID);
			if(product.getM_Product_ID()==0 || !product.getProductType().equals(MProduct.PRODUCTTYPE_Item) || !product.isStocked())
				continue;

			BigDecimal QtyBook =map_Product_Qty.get(M_Product_ID);
			if(QtyBook.compareTo(Env.ZERO)==0)
			{
				if(!m_InvValProfile.isZeroStockInvValJP())
					continue;
			}

			MInvValCalLine ivcLine = new MInvValCalLine(m_InvValCal);
			line++;
			ivcLine.setLine(line*10);
			ivcLine.setM_Product_ID(M_Product_ID.intValue());
			ivcLine.setQtyBook(QtyBook);
			ivcLine.setC_AcctSchema_ID(m_InvValProfile.getC_AcctSchema_ID());
			ivcLine.setCostingMethod(m_InvValProfile.getCostingMethod());
			ivcLine.setCostingLevel(m_InvValProfile.getCostingLevel());

			int C_AcctSchema_ID = m_InvValProfile.getC_AcctSchema_ID();
			if(ivcLine.getC_AcctSchema_ID()==0)
			{
				C_AcctSchema_ID = MClientInfo.get(getCtx()).getC_AcctSchema1_ID();
			}
			m_AcctSchema =MAcctSchema.get(getCtx(), C_AcctSchema_ID);
			int M_CostType_ID =  m_AcctSchema.getM_CostType_ID();

			//If CostElement is not one, CurrentCostPrice and FutureCostPrice are Overwritten.
			for(int j = 0; j < costElements.length; j++)
			{
				MCost cost = null;
				if(ivcLine.getCostingLevel().equals(MInvValCalLine.COSTINGLEVEL_Client))
				{
					cost = MCost.get(getCtx(), Env.getAD_Client_ID(getCtx()), 0, M_Product_ID, M_CostType_ID, C_AcctSchema_ID
																			,costElements[j].get_ID(), 0, get_TrxName());

					if(cost == null)
						continue;

				}else if (ivcLine.getCostingLevel().equals(MInvValCalLine.COSTINGLEVEL_Organization)){


					cost = MCost.get(getCtx(), Env.getAD_Client_ID(getCtx()), ivcLine.getAD_Org_ID(), M_Product_ID, M_CostType_ID
																, C_AcctSchema_ID, costElements[j].get_ID(), 0, get_TrxName());

					if(cost == null)
						continue;


				}else if (ivcLine.getCostingLevel().equals(MInvValCalLine.COSTINGLEVEL_BatchLot)){
					cost = MCost.get(getCtx(), Env.getAD_Client_ID(getCtx()), 0, M_Product_ID, M_CostType_ID, C_AcctSchema_ID
									, costElements[j].get_ID(), ivcLine.getM_AttributeSetInstance_ID(), get_TrxName());
					if(cost == null)
						continue;
				}//if

				if(m_AcctSchema.getC_Currency_ID()==m_InvValCal.getC_Currency_ID())
				{
					ivcLine.setCurrentCostPrice(cost.getCurrentCostPrice());
					ivcLine.setFutureCostPrice(cost.getFutureCostPrice());
				}else{
					BigDecimal rate =MConversionRate.getRate(m_AcctSchema.getC_Currency_ID(), m_InvValCal.getC_Currency_ID(), m_InvValCal.getDateValue(),
							m_InvValProfile.getC_ConversionType_ID(), ivcLine.getAD_Client_ID(), ivcLine.getAD_Org_ID());
					if(rate == null)
					{
						throw new AdempiereException(Msg.getMsg(getCtx(), MConversionRateUtil.getErrorMessage(getCtx(), "ErrorConvertingCurrencyToBaseCurrency",
								m_AcctSchema.getC_Currency_ID(), m_InvValProfile.getC_Currency_ID(), m_InvValProfile.getC_ConversionType_ID(), m_InvValCal.getDateValue(), get_TrxName())));
					}else{
						ivcLine.setCurrentCostPrice(cost.getCurrentCostPrice().multiply(rate));
						ivcLine.setFutureCostPrice(cost.getFutureCostPrice().multiply(rate));
					}
					
				}

			}//for j

			ivcLine.saveEx(get_TrxName());

		}


		int insertedNo = line;
		String inserted = Msg.getMsg(getCtx(), "Inserted");
		String retVal = null;
		retVal = inserted + " : " + insertedNo;
		addLog(retVal);

		return retVal;
	}

}
