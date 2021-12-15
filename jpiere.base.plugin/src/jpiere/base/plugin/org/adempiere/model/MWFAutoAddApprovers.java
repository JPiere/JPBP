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
package jpiere.base.plugin.org.adempiere.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.util.CCache;
import org.compiere.util.DB;
import org.compiere.util.Msg;
import org.compiere.wf.MWFActivity;
import org.compiere.wf.MWFNode;
import org.compiere.wf.MWorkflow;


/**
 * JPIERE-0518 WF Auto Add Approvers
 *
 * @author Hideaki Hagiwara
 *
 */
public class MWFAutoAddApprovers extends X_JP_WF_AutoAddApprovers {

	public MWFAutoAddApprovers(Properties ctx, int JP_WF_AutoAddApprovers_ID, String trxName)
	{
		super(ctx, JP_WF_AutoAddApprovers_ID, trxName);
	}

	public MWFAutoAddApprovers(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}

	@Override
	protected boolean beforeSave(boolean newRecord)
	{

		if(newRecord && is_ValueChanged(COLUMNNAME_AD_Workflow_ID))
		{
			if(getAD_Workflow_ID() == 0)
			{
				log.saveError("Error", Msg.getMsg(getCtx(), "FillMandatory") + Msg.getElement(getCtx(), COLUMNNAME_AD_Workflow_ID));
				return false;
			}

			MWorkflow m_wf = MWorkflow.get(getAD_Workflow_ID());
			if(!MWorkflow.WORKFLOWTYPE_DocumentProcess.equals(m_wf.getWorkflowType())
					&& !MWorkflow.WORKFLOWTYPE_DocumentValue.equals(m_wf.getWorkflowType()) )
			{
				//Please select Workflow that Workflow Type is Document Process or Document Value
				log.saveError("Error", "JP_WF_Check_WFType");
				return false;
			}

		}

		if(newRecord && is_ValueChanged(COLUMNNAME_AD_WF_Node_ID))
		{
			if(getAD_WF_Node_ID() == 0)
			{
				log.saveError("Error", Msg.getMsg(getCtx(), "FillMandatory") + Msg.getElement(getCtx(), COLUMNNAME_AD_WF_Node_ID));
				return false;
			}

			MWFNode m_wfNode = MWFNode.get(getAD_WF_Node_ID());
			if(!MWFNode.ACTION_UserChoice.equals(m_wfNode.getAction())
					&& !MWFNode.ACTION_UserWindow.equals(m_wfNode.getAction()))
			{
				//Selected WF Node could not set. Please Check Action of WF Node.
				log.saveError("Error", "JP_WF_Check_Node_Action");
				return false;
			}

		}

		//Cache Reset
		if(!newRecord
				&& ( is_ValueChanged(COLUMNNAME_AD_Workflow_ID) || is_ValueChanged(COLUMNNAME_AD_WF_Node_ID) ||  is_ValueChanged(COLUMNNAME_ValidFrom) ||  is_ValueChanged(COLUMNNAME_JP_WF_Org_ID) ) )
		{
			s_cache.reset();
		}

		return true;
	}



	@Override
	protected boolean beforeDelete()
	{
		s_cache.reset();
		return true;
	}



	private MWFAutoAddUser[] autoAddUsers = null;

	public MWFAutoAddUser[] getAutoAddUsers(boolean reload)
	{
		if (reload || autoAddUsers == null || autoAddUsers.length == 0)
			;
		else
			return autoAddUsers;

		ArrayList<MWFAutoAddUser> list = new ArrayList<MWFAutoAddUser>();
		final String sql = "SELECT * FROM JP_WF_AutoAddUser WHERE JP_WF_AutoAddApprovers_ID=? AND IsActive = 'Y' ORDER BY AD_User_ID";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, getJP_WF_AutoAddApprovers_ID());
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add(new MWFAutoAddUser (getCtx(), rs, get_TrxName()));
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}

		autoAddUsers = new MWFAutoAddUser[list.size()];
		list.toArray(autoAddUsers);

		return autoAddUsers;
	}

	/** Cache			*/
	private static CCache<String, MWFAutoAddApprovers> s_cache = new CCache<String, MWFAutoAddApprovers>(Table_Name, 100, 60);
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	static public MWFAutoAddApprovers get(MWFActivity wfA)
	{
		String key = "" + wfA.getAD_Client_ID() + "_" + wfA.getAD_WF_Node_ID() + "_" + wfA.getAD_Org_ID() + "_" + sdf.format(wfA.getCreated());
		MWFAutoAddApprovers approvers = null;
		if(s_cache.containsKey(key))
		{
			approvers = s_cache.get(key);
			return approvers;
		}

		final String sql = "SELECT * FROM JP_WF_AutoAddApprovers WHERE AD_Client_ID=? AND AD_WF_Node_ID = ? AND (JP_WF_Org_ID = ? OR JP_WF_Org_ID IS NULL) AND ValidFrom <= ? AND IsActive = 'Y'"
							+ " ORDER BY JP_WF_Org_ID NULLS LAST, ValidFrom DESC";

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try
		{
			pstmt = DB.prepareStatement(sql, wfA.get_TrxName());
			pstmt.setInt(1, wfA.getAD_Client_ID());
			pstmt.setInt(2, wfA.getAD_WF_Node_ID());
			pstmt.setInt(3, wfA.getAD_Org_ID());
			pstmt.setTimestamp(4, wfA.getCreated());

			rs = pstmt.executeQuery();
			if (rs.next())
				approvers = new MWFAutoAddApprovers (wfA.getCtx(), rs, wfA.get_TrxName());
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

		s_cache.put(key, approvers);//In spite of null, I put instance into the cache for performance.

		return approvers;
	}

	static public int cacheCrear()
	{
		return s_cache.reset();
	}

}
