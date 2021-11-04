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

import org.compiere.model.MTable;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.wf.MWFActivity;


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

			m_activity.setUserChoice(Env.getAD_User_ID(getCtx()), p_JP_IsApproval, DisplayType.YesNo, p_Comments);
			addBufferLog(0, null, null, msg, m_activity.getAD_Table_ID(), m_activity.getRecord_ID());
		}

		return null;
	}

}
