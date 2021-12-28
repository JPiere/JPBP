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

import java.util.Collection;
import java.util.logging.Level;

import org.adempiere.model.GenericPO;
import org.adempiere.util.ProcessUtil;
import org.compiere.model.MColumn;
import org.compiere.model.MInvoice;
import org.compiere.model.MProcess;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfo;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;
import org.compiere.wf.MWFActivity;
import org.compiere.wf.MWFProcess;

/**
 *  JPIERE-0221
 *  JPIERE-0222
 *
 *  Document Status of AP/AR Bulk Update
 *
 *  @author Hideaki Hagiwara（h.hagiwara@oss-erp.co.jp）
 *
 */
public class BulkUpdateInvoiceDocStatusInfoWindow extends SvrProcess {

	String p_DocAction = DocAction.ACTION_Complete;

	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
			else if (name.equals("DocAction"))
				p_DocAction = para[i].getParameterAsString();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
	}

	@Override
	protected String doIt() throws Exception {

		String whereClause = "EXISTS (SELECT T_Selection_ID FROM T_Selection WHERE T_Selection.AD_PInstance_ID=? " +
							"AND T_Selection.T_Selection_ID = C_Invoice.C_Invoice_ID)";

		Collection<GenericPO> genericPOs = new Query(getCtx(), MInvoice.Table_Name, whereClause, get_TrxName())
									.setClient_ID()
									.setParameters(new Object[]{getAD_PInstance_ID()})
									.list();

		int successNo = 0;
		int failureNo = 0;
		String success = Msg.getMsg(getCtx(), "JP_Success");
		String failure = Msg.getMsg(getCtx(), "JP_Failure");

		ProcessInfo pInfo = getProcessInfo();
		MColumn docActionColumn = MColumn.get(getCtx(), MInvoice.Table_Name, MInvoice.COLUMNNAME_DocAction);
		MProcess process = MProcess.get(docActionColumn.getAD_Process_ID());

		for(PO po : genericPOs)
		{
			MInvoice inv = new MInvoice(getCtx(), po.get_ID(),get_TrxName());
			try
			{
				if(inv.isProcessed())
					continue;

				String wfStatus = MWFActivity.getActiveInfo(Env.getCtx(), MInvoice.Table_ID, inv.getC_Invoice_ID());
				if (Util.isEmpty(wfStatus))
				{
					if(DocAction.ACTION_Complete.equals(p_DocAction))
					{
						pInfo.setPO(inv);
						pInfo.setRecord_ID(inv.getC_Invoice_ID());
						pInfo.setTable_ID(MInvoice.Table_ID);
						MWFProcess wfProcess = ProcessUtil.startWorkFlow(Env.getCtx(), pInfo, process.getAD_Workflow_ID());
						if(wfProcess.getWFState().equals(MWFProcess.WFSTATE_Terminated))
						{
							failureNo++;
							inv.saveEx(get_TrxName());
							addBufferLog(0, null, null, failure +":" + inv.getDocumentNo(), MInvoice.Table_ID, inv.getC_Invoice_ID());

						}else {

							successNo++;
							inv.saveEx(get_TrxName());
							addBufferLog(0, null, null, success +":" + inv.getDocumentNo(), MInvoice.Table_ID, inv.getC_Invoice_ID());

						}

					}else if(DocAction.ACTION_Prepare.equals(p_DocAction)) {

						if(inv.processIt(p_DocAction))
						{
							successNo++;
							inv.saveEx(get_TrxName());
							addBufferLog(0, null, null, success +":" + inv.getDocumentNo(), MInvoice.Table_ID, inv.getC_Invoice_ID());
						}else {

							failureNo++;
							inv.saveEx(get_TrxName());
							addBufferLog(0, null, null, failure +":" + inv.getDocumentNo(), MInvoice.Table_ID, inv.getC_Invoice_ID());
						}

					}

				}else {

					failureNo++;
					addBufferLog(0, null, null, Msg.getMsg(getCtx(), "WFActiveForRecord") +":" + inv.getDocumentNo(), MInvoice.Table_ID, inv.getC_Invoice_ID());
				}

			}catch(Exception e){
				failureNo++;
				addBufferLog(0, null, null, failure +":" + inv.getDocumentNo(), MInvoice.Table_ID, inv.getC_Invoice_ID());
			}

		}


		return success + " : " + successNo + "  /  " +  failure + " " + failureNo;
	}

}
