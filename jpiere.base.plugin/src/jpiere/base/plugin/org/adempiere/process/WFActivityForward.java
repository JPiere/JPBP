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
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;

import org.compiere.model.MTable;
import org.compiere.model.MUser;
import org.compiere.model.MWFActivityApprover;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Msg;
import org.compiere.util.Util;
import org.compiere.wf.MWFActivity;


/**
 *  JPIERE-0513: Approval of Unprocessed Work flow Activity at Info Window.
 *
 *  @author Hideaki Hagiwara（h.hagiwara@oss-erp.co.jp）
 *
 */
public class WFActivityForward extends SvrProcess {

	private int p_JP_WF_Forward_User_ID = 0;
	private String[] p_JP_WF_Additional_User_Multi = new String[] {};
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
			}else if (name.equals("JP_WF_Forward_User_ID")){

				p_JP_WF_Forward_User_ID = para[i].getParameterAsInt();

			}else if (name.equals("Comments")){

				p_Comments = para[i].getParameterAsString();

			}else if (name.equals("JP_WF_Additional_User_Multi")){

				String JP_WF_Additional_User_Multi = para[i].getParameterAsString();
				if(!Util.isEmpty(JP_WF_Additional_User_Multi))
					p_JP_WF_Additional_User_Multi = JP_WF_Additional_User_Multi.split(",");

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

			if(p_JP_WF_Forward_User_ID != 0)
			{
				if(!m_activity.forwardTo(p_JP_WF_Forward_User_ID, p_Comments))
				{
					throw new Exception(Msg.getMsg(getCtx(), "CannotForward"));
				}
			}

			MUser[] m_ActivityApprovers = getActivityApprovers(m_activity.getAD_WF_Activity_ID());
			int additional_User_ID = 0;
			boolean isAlreadyRegistered = false;

			for(String Additional_User_ID : p_JP_WF_Additional_User_Multi)
			{
				isAlreadyRegistered = false;
				additional_User_ID = Integer.valueOf(Additional_User_ID).intValue();
				for(MUser approver : m_ActivityApprovers)
				{
					if(approver.getAD_User_ID() == additional_User_ID)
					{
						isAlreadyRegistered = true;
						break;
					}
				}

				if(isAlreadyRegistered)
					continue;

				MWFActivityApprover wfApprover = new MWFActivityApprover(getCtx(), 0, get_TrxName());
				wfApprover.setAD_WF_Activity_ID(m_activity.getAD_WF_Activity_ID());
				wfApprover.setAD_User_ID(additional_User_ID);
				wfApprover.saveEx(get_TrxName());
			}

			addBufferLog(0, null, null, msg, m_activity.getAD_Table_ID(), m_activity.getRecord_ID());
		}

		return null;
	}

	public MUser[] getActivityApprovers(int AD_WF_Activity_ID)
	{
		ArrayList<MUser> list = new ArrayList<MUser>();
		String sql = "SELECT u.* FROM AD_WF_ActivityApprover a INNER JOIN AD_User u ON (a.AD_User_ID=u.AD_User_ID) WHERE a.AD_WF_Activity_ID=? AND a.IsActive='Y' AND u.IsActive='Y'";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, AD_WF_Activity_ID);
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add(new MUser (getCtx(), rs, get_TrxName()));
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

		return list.toArray(new MUser[list.size()]);

	}

}
