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

import java.sql.Timestamp;
import java.util.logging.Level;

import org.compiere.model.MAcctSchema;
import org.compiere.model.MCost;
import org.compiere.model.MCostElement;
import org.compiere.model.MPriceListVersion;
import org.compiere.model.MProduct;
import org.compiere.model.MProductCategoryAcct;
import org.compiere.model.MProductPrice;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.MInvValCalLine;
import jpiere.base.plugin.org.adempiere.model.MInvValProfile;
import jpiere.base.plugin.org.adempiere.model.MInvValProfileOrg;
import jpiere.base.plugin.util.JPiereInvValUtil;

/**
 * JPIERE-0440 Update Cost From Price List
 *
 *  Update Cost From Price List
 *
 *  @author Hideaki Hagiwara
 *
 */
public class UpdateCostFromPriceList extends SvrProcess {

	MInvValProfile m_InvValProfile = null;
	MInvValProfileOrg[] profileOrgs = null;
	int Record_ID = 0;
	Timestamp p_DateValue = null;

	@Override
	protected void prepare()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null){
				;
			}else if (name.equals("DateValue")){
				p_DateValue = para[i].getParameterAsTimestamp();
			}else{
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}//if

		}//for


		Record_ID = getRecord_ID();
		if(Record_ID > 0)
		{
			m_InvValProfile = new MInvValProfile(getCtx(), Record_ID, null);
			profileOrgs = m_InvValProfile.getOrgs();
		}else{
			log.log(Level.SEVERE, "Record_ID <= 0 ");
		}
	}

	@Override
	protected String doIt() throws Exception
	{
		if(m_InvValProfile.getJP_UpdateCost() == null)
		{
			throw new Exception(Msg.getMsg(getCtx(), "FillMandatory") + Msg.getElement(getCtx(), "JP_UpdateCost"));
		}

		if(m_InvValProfile.getM_PriceList_ID() == 0)
		{
			throw new Exception(Msg.getMsg(getCtx(), "FillMandatory") + Msg.getElement(getCtx(), "M_PriceList_ID"));
		}

		if(p_DateValue == null)
		{
			throw new Exception(Msg.getMsg(getCtx(), "FillMandatory") + Msg.getElement(getCtx(), "DateValue"));
		}

		MCostElement[] costElements = JPiereInvValUtil.getMaterialStandardCostElements (getCtx());
		int C_AcctSchema_ID = m_InvValProfile.getC_AcctSchema_ID();
		int M_CostType_ID = m_InvValProfile.getC_AcctSchema().getM_CostType_ID();
		int M_PriceList_ID = m_InvValProfile.getM_PriceList_ID();
		MPriceListVersion m_PriceList_Version = JPiereInvValUtil.getPriceListVersion(getCtx(), M_PriceList_ID, p_DateValue, get_TrxName());
		if(m_PriceList_Version == null)
			throw new Exception(Msg.getMsg(getCtx(), "NotFound") + " : " +Msg.getElement(getCtx(), "M_PriceList_Version_ID"));


		MProductPrice[] m_ProductPrices = m_PriceList_Version.getProductPrice(true);

		if(m_InvValProfile.getCostingLevel().equals(MInvValCalLine.COSTINGLEVEL_Client))
		{

			for(int i = 0; i < m_ProductPrices.length; i++)
			{
				uppdateCostFromPriceList(m_ProductPrices[i], costElements, M_CostType_ID, C_AcctSchema_ID, 0);
			}


		}else if (m_InvValProfile.getCostingLevel().equals(MInvValCalLine.COSTINGLEVEL_Organization)){

			for(int i = 0; i < profileOrgs.length; i++)
			{
				for(int j = 0; j < m_ProductPrices.length; j++)
				{
					uppdateCostFromPriceList(m_ProductPrices[j], costElements, M_CostType_ID, C_AcctSchema_ID, profileOrgs[i].getAD_Org_ID());
				}
			}

		}else if (m_InvValProfile.getCostingLevel().equals(MInvValCalLine.COSTINGLEVEL_BatchLot)){

			;//Noting to do;

		}

		return Msg.getMsg(getCtx(), "Success");
	}


	private void uppdateCostFromPriceList(MProductPrice m_ProductPrice, MCostElement[] costElements, int M_CostType_ID, int C_AcctSchema_ID, int AD_Org_ID)
	{
		if(m_InvValProfile.getM_Product_Category_ID() != 0)
		{
			if(m_ProductPrice.getM_Product().getM_Product_Category_ID() != m_InvValProfile.getM_Product_Category_ID())
				return ;
		}

		MProductCategoryAcct m_ProductCategoryAcct = MProductCategoryAcct.get(getCtx(), m_ProductPrice.getM_Product().getM_Product_Category_ID(), C_AcctSchema_ID, get_TrxName());
		if(m_ProductCategoryAcct == null)
		{
			return ;
		}

		if(!Util.isEmpty(m_ProductCategoryAcct.getCostingLevel()) && !m_ProductCategoryAcct.getCostingLevel().equals(m_InvValProfile.getCostingLevel()))
		{
			return ;
		}

		int M_Product_ID = m_ProductPrice.getM_Product_ID();

		for(int i = 0; i < costElements.length; i++)
		{
			MCost cost = MCost.get(getCtx()
									, Env.getAD_Client_ID(getCtx())
									, AD_Org_ID
									, M_Product_ID
									, M_CostType_ID
									, C_AcctSchema_ID
									, costElements[i].get_ID()
									, 0
									, get_TrxName());

			if(cost == null)
			{
				cost = new MCost(
						new MProduct(getCtx(),M_Product_ID, get_TrxName())
						, 0
						, MAcctSchema.get(getCtx(), C_AcctSchema_ID, get_TrxName())
						, AD_Org_ID
						, costElements[i].getM_CostElement_ID())	;
			}


			if(m_InvValProfile.getJP_UpdateCost().equals(MInvValProfile.JP_UPDATECOST_CurrentCostPrice))
			{
				cost.setCurrentCostPrice(m_ProductPrice.getPriceStd());
			}else if(m_InvValProfile.getJP_UpdateCost().equals(MInvValProfile.JP_UPDATECOST_FutureCostPrice)){
				cost.setFutureCostPrice(m_ProductPrice.getPriceStd());
			}else if(m_InvValProfile.getJP_UpdateCost().equals(MInvValProfile.JP_UPDATECOST_BothCurrentCostAndFutureCost)){
				cost.setCurrentCostPrice(m_ProductPrice.getPriceStd());
				cost.setFutureCostPrice(m_ProductPrice.getPriceList());
			}
			cost.saveEx(get_TrxName());


		}//for j
	}

}
