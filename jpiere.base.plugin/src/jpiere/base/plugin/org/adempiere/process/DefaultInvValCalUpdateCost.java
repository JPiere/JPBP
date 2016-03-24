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

import java.util.logging.Level;

import jpiere.base.plugin.org.adempiere.model.MInvValCal;
import jpiere.base.plugin.org.adempiere.model.MInvValCalLine;
import jpiere.base.plugin.org.adempiere.model.MInvValProfile;
import jpiere.base.plugin.org.adempiere.model.MInvValProfileOrg;
import jpiere.base.plugin.util.JPiereInvValUtil;

import org.compiere.model.MCost;
import org.compiere.model.MCostElement;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;

/**
 * JPIERE-0161 Inventory Valuation Calculate Doc
 *
 *  Default Update Cost
 *
 *  @author Hideaki Hagiwara
 *
 */
public class DefaultInvValCalUpdateCost extends SvrProcess {

	MInvValProfile m_InvValProfile = null;
	MInvValProfileOrg[] profileOrgs = null;
	MInvValCal m_InvValCal = null;
	MInvValCalLine[] lines = null;
	int Record_ID = 0;

	@Override
	protected void prepare()
	{
		Record_ID = getRecord_ID();
		if(Record_ID > 0)
		{
			m_InvValCal = new MInvValCal(getCtx(), Record_ID, null);
			lines = m_InvValCal.getLines();
			m_InvValProfile = MInvValProfile.get(getCtx(), m_InvValCal.getJP_InvValProfile_ID());
			profileOrgs = m_InvValProfile.getOrgs();
		}else{
			log.log(Level.SEVERE, "Record_ID <= 0 ");
		}
	}

	@Override
	protected String doIt() throws Exception
	{
		MCostElement[] costElements = JPiereInvValUtil.getMaterialStandardCostElements (getCtx());
		int C_AcctSchema_ID = 0;
		int M_CostType_ID = 0;
		int M_Product_ID = 0;
		for(int i = 0; i < lines.length; i++)
		{
			C_AcctSchema_ID = lines[i].getC_AcctSchema_ID();
			M_CostType_ID = lines[i].getC_AcctSchema().getM_CostType_ID();
			M_Product_ID = lines[i].getM_Product_ID();

			for(int j = 0; j < costElements.length; j++)
			{
				MCost cost = null;
				if(lines[i].getCostingLevel().equals(MInvValCalLine.COSTINGLEVEL_Client))
				{
					cost = MCost.get(getCtx(), Env.getAD_Client_ID(getCtx()), 0, M_Product_ID, M_CostType_ID, C_AcctSchema_ID
																			,costElements[j].get_ID(), 0, get_TrxName());

					if(cost == null)
						continue;

					if(m_InvValProfile.getJP_UpdateCost().equals(MInvValProfile.JP_UPDATECOST_CurrentCostPrice))
					{
						cost.setCurrentCostPrice(lines[i].getJP_InvValAmt());
					}else if(m_InvValProfile.getJP_UpdateCost().equals(MInvValProfile.JP_UPDATECOST_FutureCostPrice)){
						cost.setFutureCostPrice(lines[i].getJP_InvValAmt());
					}else if(m_InvValProfile.getJP_UpdateCost().equals(MInvValProfile.JP_UPDATECOST_BothCurrentCostAndFutureCost)){
						cost.setCurrentCostPrice(lines[i].getJP_InvValAmt());
						cost.setFutureCostPrice(lines[i].getJP_InvValAmt());
					}
					cost.saveEx(get_TrxName());

				}else if (lines[i].getCostingLevel().equals(MInvValCalLine.COSTINGLEVEL_Organization)){

					for(int k = 0; k < profileOrgs.length; k++)
					{
						cost = MCost.get(getCtx(), Env.getAD_Client_ID(getCtx()), profileOrgs[k].getAD_Org_ID(), M_Product_ID, M_CostType_ID
																	, C_AcctSchema_ID, costElements[j].get_ID(), 0, get_TrxName());

						if(cost == null)
							continue;

						if(m_InvValProfile.getJP_UpdateCost().equals(MInvValProfile.JP_UPDATECOST_CurrentCostPrice))
						{
							cost.setCurrentCostPrice(lines[i].getJP_InvValAmt());
						}else if(m_InvValProfile.getJP_UpdateCost().equals(MInvValProfile.JP_UPDATECOST_FutureCostPrice)){
							cost.setFutureCostPrice(lines[i].getJP_InvValAmt());
						}else if(m_InvValProfile.getJP_UpdateCost().equals(MInvValProfile.JP_UPDATECOST_BothCurrentCostAndFutureCost)){
							cost.setCurrentCostPrice(lines[i].getJP_InvValAmt());
							cost.setFutureCostPrice(lines[i].getJP_InvValAmt());
						}
						cost.saveEx(get_TrxName());

					}//for k

				}else if (lines[i].getCostingLevel().equals(MInvValCalLine.COSTINGLEVEL_BatchLot)){
					cost = MCost.get(getCtx(), Env.getAD_Client_ID(getCtx()), 0, M_Product_ID, M_CostType_ID, C_AcctSchema_ID
									, costElements[j].get_ID(), lines[i].getM_AttributeSetInstance_ID(), get_TrxName());
					if(cost == null)
						continue;

					if(m_InvValProfile.getJP_UpdateCost().equals(MInvValProfile.JP_UPDATECOST_CurrentCostPrice))
					{
						cost.setCurrentCostPrice(lines[i].getJP_InvValAmt());
					}else if(m_InvValProfile.getJP_UpdateCost().equals(MInvValProfile.JP_UPDATECOST_FutureCostPrice)){
						cost.setFutureCostPrice(lines[i].getJP_InvValAmt());
					}else if(m_InvValProfile.getJP_UpdateCost().equals(MInvValProfile.JP_UPDATECOST_BothCurrentCostAndFutureCost)){
						cost.setCurrentCostPrice(lines[i].getJP_InvValAmt());
						cost.setFutureCostPrice(lines[i].getJP_InvValAmt());
					}
					cost.saveEx(get_TrxName());

				}//if

			}//for j

		}//for i

		return null;
	}

}
