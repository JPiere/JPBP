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
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.logging.Level;

import org.compiere.model.MTable;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.wf.MWFActivity;
import org.compiere.wf.MWFNode;
import org.compiere.wf.MWFProcess;


/**
 *  JPIERE-0513: Approval of Unprocessed Work flow Activity at Info Window.
 *
 *  @author Hideaki Hagiwara（h.hagiwara@oss-erp.co.jp）
 *
 */
public class WFActivityApproval extends SvrProcess {

	private String p_JP_IsApproval = "N";
	private String p_Comments = null;

	@Override
	protected void prepare()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null){
				;
			}else if (name.equals("JP_IsApproval")){

				p_JP_IsApproval = para[i].getParameterAsString();

			}else if (name.equals("Comments")){

				p_Comments = para[i].getParameterAsString();

			}else{
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}//if
		}//for

	}

	@Override
	protected String doIt() throws Exception
	{
		String whereClause = "EXISTS (SELECT T_Selection_ID FROM T_Selection WHERE T_Selection.AD_PInstance_ID=? " +
				" AND T_Selection.T_Selection_ID = AD_WF_Activity .AD_WF_Activity_ID)";

		Collection<MWFActivity> m_WFAs = new Query(getCtx(), MWFActivity.Table_Name, whereClause, get_TrxName())
													.setClient_ID()
													.setParameters(new Object[]{getAD_PInstance_ID()})
													.list();

		MTable m_Table = null;
		PO m_PO = null;
		String msg = null;
		MWFNode node = null;

		for(MWFActivity m_activity : m_WFAs)
		{
			m_Table = MTable.get(m_activity.getAD_Table_ID());
			m_PO = m_Table.getPO(m_activity.getRecord_ID(), get_TrxName());

			if(m_PO.columnExists("DocumentNo"))
			{
				msg = m_PO.get_ValueAsString("DocumentNo");
			}else if(m_PO.columnExists("Name")) {
				msg = m_PO.get_ValueAsString("Name");
			}else {
				msg = m_PO.toString();
			}

			node = m_activity.getNode();

			if (MWFNode.ACTION_UserChoice.equals(node.getAction()))
			{
				try
				{
					m_activity.setEndWaitTime(Timestamp.valueOf(LocalDateTime.now()));
					m_activity.setUserChoice(Env.getAD_User_ID(getCtx()), p_JP_IsApproval, DisplayType.YesNo, p_Comments);
					if(!p_JP_IsApproval.equals("Y"))
					{
						MWFProcess wfpr = new MWFProcess(getCtx(), m_activity.getAD_WF_Process_ID(), get_TrxName());
						wfpr.checkCloseActivities(get_TrxName());

					}else if(m_PO instanceof DocAction) {

						m_PO.load(get_TrxName());
						String docStatus = ((DocAction)m_PO).getDocStatus();
						if(DocAction.STATUS_Completed.equals(docStatus))
						{
							MWFProcess wfpr = new MWFProcess(getCtx(), m_activity.getAD_WF_Process_ID(), get_TrxName());
							wfpr.checkCloseActivities(get_TrxName());
						}
					}

				}catch (Exception e) {

					log.log(Level.SEVERE, node.getName(), e);
					throw e;
				}

			}else {

				try
				{
					m_activity.setEndWaitTime(Timestamp.valueOf(LocalDateTime.now()));
					m_activity.setUserConfirmation(Env.getAD_User_ID(getCtx()), p_Comments);
					MWFProcess wfpr = new MWFProcess(getCtx(), m_activity.getAD_WF_Process_ID(), get_TrxName());
					wfpr.checkCloseActivities(get_TrxName());

				}catch (Exception e){

					log.log(Level.SEVERE, node.getName(), e);
					throw e;
				}
			}

			addBufferLog(0, null, null, msg, m_activity.getAD_Table_ID(), m_activity.getRecord_ID());

		}//for

		return null;
	}

}
