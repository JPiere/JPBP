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
import java.sql.Timestamp;
import java.util.logging.Level;

import org.compiere.model.MTree;
import org.compiere.model.MTree_Node;
import org.compiere.model.PO;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;

import jpiere.base.plugin.org.adempiere.model.MPPDoc;
import jpiere.base.plugin.org.adempiere.model.MPPDocT;
import jpiere.base.plugin.org.adempiere.model.MPPPlan;
import jpiere.base.plugin.org.adempiere.model.MPPPlanLine;
import jpiere.base.plugin.org.adempiere.model.MPPPlanLineT;
import jpiere.base.plugin.org.adempiere.model.MPPPlanT;


/**
 * JPIERE-0501: Create JPiere PP Doc from Template
 *
 * @author Hideaki Hagiwara
 *
 */
public class PPCreateDocFromTemplate extends SvrProcess {

	private int p_JP_PP_DocT_ID = 0;
	private BigDecimal p_QtyEntered = Env.ZERO;
	private Timestamp p_JP_PP_ScheduledStart = null;


	@Override
	protected void prepare()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if ("JP_PP_DocT_ID".equals(name))
				p_JP_PP_DocT_ID = para[i].getParameterAsInt();
			else if ("QtyEntered".equals(name))
				p_QtyEntered = para[i].getParameterAsBigDecimal();
			else if ("JP_PP_ScheduledStart".equals(name))
				p_JP_PP_ScheduledStart  =  para[i].getParameterAsTimestamp();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}

		if(p_JP_PP_DocT_ID == 0)
			p_JP_PP_DocT_ID = getRecord_ID();

	}

	@Override
	protected String doIt() throws Exception
	{

		MPPDocT ppDocT = new MPPDocT(getCtx(), p_JP_PP_DocT_ID, get_TrxName());
		MPPDoc ppDoc = new  MPPDoc(getCtx(), 0, get_TrxName());

		PO.copyValues(ppDocT, ppDoc);
		ppDoc.setJP_PP_DocT_ID(p_JP_PP_DocT_ID);
		ppDoc.setValue(ppDocT.getValue());
		ppDoc.setAD_Org_ID(ppDocT.getAD_Org_ID());
		ppDoc.setQtyEntered(p_QtyEntered);
		ppDoc.setC_UOM_ID(ppDocT.getC_UOM_ID());
		ppDoc.setProductionQty(p_QtyEntered.multiply(ppDocT.getProductionQty()));
		ppDoc.setJP_PP_ScheduledStart(p_JP_PP_ScheduledStart);//TODO - ロジック適用
		ppDoc.setJP_PP_ScheduledEnd(p_JP_PP_ScheduledStart);//TODO - ロジック適用
		ppDoc.setDateAcct(p_JP_PP_ScheduledStart);//TODO - ロジック適用
		ppDoc.setDocStatus(DocAction.STATUS_Drafted);
		ppDoc.setJP_PP_Status(MPPDoc.JP_PP_STATUS_NotYetStarted);
		ppDoc.saveEx(get_TrxName());

		MPPPlanT[] ppPlanTs = ppDocT.getPPPlanTs(true, null);
		MPPPlan ppPlan = null;
		MPPPlanLineT[] ppPlanLineTs = null;
		MPPPlanLine ppPlanLine = null;
		for(MPPPlanT ppPlanT : ppPlanTs)
		{
			ppPlan = new  MPPPlan(getCtx(), 0, get_TrxName());
			PO.copyValues(ppPlanT, ppPlan);
			ppPlan.setJP_PP_Doc_ID(ppDoc.getJP_PP_Doc_ID());
			ppPlan.setJP_PP_PlanT_ID(ppPlanT.getJP_PP_PlanT_ID());
			ppPlan.setValue(ppPlanT.getValue());
			ppPlan.setAD_Org_ID(ppPlanT.getAD_Org_ID());
			ppPlan.setSeqNo(ppPlanT.getSeqNo());
			ppPlan.setProductionQty(p_QtyEntered.multiply(ppPlanT.getProductionQty()));
			ppPlan.setJP_PP_ScheduledStart(p_JP_PP_ScheduledStart);//TODO - ロジック適用
			ppPlan.setJP_PP_ScheduledStart(p_JP_PP_ScheduledStart);//TODO - ロジック適用
			ppPlan.setDateAcct(p_JP_PP_ScheduledStart);//TODO - ロジック適用
			ppPlan.setDocStatus(DocAction.STATUS_Drafted);
			ppPlan.setJP_PP_Status(MPPDoc.JP_PP_STATUS_NotYetStarted);
			ppPlan.saveEx(get_TrxName());

			ppPlanLineTs = ppPlanT.getPPPlanLineTs(true, null);
			for(MPPPlanLineT ppPlanLineT : ppPlanLineTs)
			{
				ppPlanLine = new  MPPPlanLine(getCtx(), 0, get_TrxName());
				PO.copyValues(ppPlanLineT, ppPlanLine);
				ppPlanLine.setJP_PP_Plan_ID(ppPlan.getJP_PP_Plan_ID());
				ppPlanLine.setJP_PP_PlanLineT_ID(ppPlanLineT.getJP_PP_PlanLineT_ID());
				ppPlanLine.setAD_Org_ID(ppPlanLineT.getAD_Org_ID());
				ppPlanLine.setLine(ppPlanLineT.getLine());
				ppPlanLine.setPlannedQty(p_QtyEntered.multiply(ppPlanLineT.getPlannedQty()));
				if(!ppPlanLineT.isEndProduct())
					ppPlanLine.setQtyUsed(p_QtyEntered.multiply(ppPlanLineT.getQtyUsed()));
				ppPlanLine.setMovementQty(p_QtyEntered.multiply(ppPlanLineT.getMovementQty()));
				ppPlanLine.saveEx(get_TrxName());

			}


			if(ppPlanT.isCreatePPFactJP())
			{
				ppPlan.createFact();
			}

		}


		//Update Tree
		int p_AD_TreeFrom_ID = MTree.getDefaultAD_Tree_ID(getAD_Client_ID(), "JP_PP_PlanT_ID");
		int p_AD_TreeTo_ID = MTree.getDefaultAD_Tree_ID(getAD_Client_ID(), "JP_PP_Plan_ID");
		MTree treeFrom =  new MTree(getCtx(), p_AD_TreeFrom_ID, get_TrxName());
		MTree treeTo =  new MTree(getCtx(), p_AD_TreeTo_ID, get_TrxName());

		MPPPlan[] ppPlans = ppDoc.getPPPlans(true, null);
		for(int i = 0;  i < ppPlans.length ; i++)
		{
			MTree_Node nodeTo = MTree_Node.get(treeTo, ppPlans[i].getJP_PP_Plan_ID());
			MTree_Node nodeFrom = MTree_Node.get(treeFrom, ppPlans[i].getJP_PP_PlanT_ID());

			for(int j = 0; j < ppPlans.length ; j++)
			{
				if(nodeFrom.getParent_ID() == 0)
				{
					nodeTo.setParent_ID(0);
					nodeTo.setSeqNo(nodeFrom.getSeqNo());
					nodeTo.save(get_TrxName());
					break;

				}else if(nodeFrom.getParent_ID() == ppPlans[j].getJP_PP_PlanT_ID()){

					nodeTo.setParent_ID(ppPlans[j].getJP_PP_Plan_ID());
					nodeTo.setSeqNo(nodeFrom.getSeqNo());
					nodeTo.save(get_TrxName());
					break;
				}

			}//for j
		}//for i


		addBufferLog(0, null, null, "予実製造指図伝票", MPPDoc.Table_ID, ppDoc.getJP_PP_Doc_ID());//TODO
		;
		return "@OK@";
	}

}
