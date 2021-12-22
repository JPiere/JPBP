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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;

import org.compiere.model.MTable;
import org.compiere.model.PO;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.StateEngine;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.wf.MWFProcess;

/**
 * JPIERE-0520 Workflow abort at Unprocessed Wrokflow Activity Info window
 *
 *  Ref: Manage Workflow Process(WFProcessManage.java)
 *
 *  @author H.Hagiwara
 *
 */
public class WFProcessAbort extends SvrProcess
{

	private String p_Comments = null;

	/**
	 *  Prepare - e.g., get Parameters.
	 */
	protected void prepare()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
			{
				;

			}else if (name.equals("Comments")){

				p_Comments = para[i].getParameterAsString();

			}else{
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}//if
		}//for
	}	//	prepare

	/**
	 *  Perform process.
	 *  @return Message (variables are parsed)
	 *  @throws Exception if not successful
	 */
	protected String doIt() throws Exception
	{
		String sql = "SELECT DISTINCT AD_WF_Process_ID FROM AD_WF_Activity wa"
				+ " INNER JOIN T_Selection ts ON (wa.AD_WF_Activity_ID = ts.T_Selection_ID AND ts.AD_PInstance_ID=?)" ;


		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, getAD_PInstance_ID());

			rs = pstmt.executeQuery();
			int p_AD_WF_Process_ID = 0;
			MWFProcess process = null;
			MTable m_Table = null;
			PO m_PO = null;
			String msg = null;
			while (rs.next())
			{
				p_AD_WF_Process_ID = rs.getInt(1);
				process = new MWFProcess (getCtx(), p_AD_WF_Process_ID, get_TrxName());
				process.setTextMsg(p_Comments);
				process.setAD_User_ID(getAD_User_ID());
				process.setWFState(StateEngine.STATE_Aborted);
				process.saveEx();

				m_Table = MTable.get(process.getAD_Table_ID());
				m_PO = m_Table.getPO(process.getRecord_ID(), get_TrxName());
				if(m_PO.columnExists("DocumentNo"))
				{
					msg = m_PO.get_ValueAsString("DocumentNo");
				}else if(m_PO.columnExists("Name")) {
					msg = m_PO.get_ValueAsString("Name");
				}else {
					msg = m_PO.toString();
				}
				addBufferLog(0, null, null, msg, process.getAD_Table_ID(), process.getRecord_ID());
			}
		}
		catch (Exception e)
		{
			//log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}

		return "OK";

	}	//	doIt

}	//	WFProcessAbort
