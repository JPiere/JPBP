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

import org.adempiere.util.ProcessUtil;
import org.compiere.model.MColumn;
import org.compiere.model.MProcess;
import org.compiere.process.ProcessInfo;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;
import org.compiere.wf.MWFActivity;

import jpiere.base.plugin.org.adempiere.model.MPPFact;
import jpiere.base.plugin.org.adempiere.model.MPPPlan;


/**
 * JPIERE-0501:JPiere PP Fact after complete Process
 *
 * @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 *
 */
public class PPFactProcessAfterComplete extends SvrProcess {

	private int p_JP_PP_Fact_ID = 0;

	@Override
	protected void prepare()
	{
		p_JP_PP_Fact_ID = getRecord_ID();
	}

	@Override
	protected String doIt() throws Exception
	{
		String msg = "@OK@";

		MPPFact ppFact = new MPPFact(getCtx(), p_JP_PP_Fact_ID, get_TrxName());
		MPPPlan parent = ppFact.getParent();

		BigDecimal  productionFactQty = parent.getProductionFactQty(get_TrxName());
		parent.setJP_ProductionQtyFact(productionFactQty);
		parent.saveEx(get_TrxName());

		if(productionFactQty.compareTo(parent.getProductionQty()) >= 0 )
		{
			if(parent.isCompleteAutoJP())
			{
				MPPFact[] ppFacts = parent.getPPFacts(true, null);
				for(MPPFact fact : ppFacts)
				{
					if(!fact.isProcessed())
					{
						//You cannot be completed PP Plan because there is an unprocessed PP Fact.
						msg = Msg.getMsg(getCtx(), "JP_PP_NotCompletePPPlanForUnprocessedPPFact");
						return msg;
					}
				}

				String wfStatus = MWFActivity.getActiveInfo(Env.getCtx(), MPPPlan.Table_ID, parent.getJP_PP_Plan_ID());
				if (Util.isEmpty(wfStatus))
				{
					ProcessInfo pInfo = getProcessInfo();
					pInfo.setPO(parent);
					pInfo.setRecord_ID(parent.getJP_PP_Plan_ID());
					pInfo.setTable_ID(MPPPlan.Table_ID);
					MColumn docActionColumn = MColumn.get(getCtx(), MPPPlan.Table_Name, MPPPlan.COLUMNNAME_DocAction);
					MProcess process = MProcess.get(docActionColumn.getAD_Process_ID());
					ProcessUtil.startWorkFlow(Env.getCtx(), pInfo, process.getAD_Workflow_ID());

				}else {

					msg = Msg.getMsg(getCtx(), "WFActiveForRecord");
					return msg;
				}

			}

		}else {

			if(parent.isSplitWhenDifferenceJP())
			{

			}

		}


		return msg;
	}

}
