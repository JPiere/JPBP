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
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.util.CCache;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.wf.MWFActivity;
import org.compiere.wf.MWFNode;
import org.compiere.wf.MWorkflow;


/**
 * JPIERE-0519 WF Auto Forward
 *
 * @author Hideaki Hagiwara
 *
 */
public class MWFAutoForward extends X_JP_WF_AutoForward {

	public MWFAutoForward(Properties ctx, int JP_WF_AutoForward_ID, String trxName)
	{
		super(ctx, JP_WF_AutoForward_ID, trxName);
	}

	public MWFAutoForward(Properties ctx, ResultSet rs, String trxName)
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

		//Unique check
		if(newRecord)
		{
			String sql = null;

			if(get_Value(COLUMNNAME_AD_WF_Node_ID) == null )
			{
				if(get_Value(COLUMNNAME_JP_WF_Org_ID) == null )
				{
					sql = "SELECT * FROM JP_WF_AutoForward WHERE AD_Client_ID=? AND JP_WF_User_From_ID=? AND AD_WF_Node_ID IS NULL AND JP_WF_Org_ID IS NULL AND ValidFrom = ? ";
				}else {
					sql = "SELECT * FROM JP_WF_AutoForward WHERE AD_Client_ID=? AND JP_WF_User_From_ID=? AND AD_WF_Node_ID IS NULL AND JP_WF_Org_ID = ? AND ValidFrom = ? ";
				}
			}else {

				if(get_Value(COLUMNNAME_JP_WF_Org_ID) == null )
				{
					sql = "SELECT * FROM JP_WF_AutoForward WHERE AD_Client_ID=? AND JP_WF_User_From_ID=? AND AD_WF_Node_ID = ? AND JP_WF_Org_ID IS NULL AND ValidFrom = ? ";
				}else {
					sql = "SELECT * FROM JP_WF_AutoForward WHERE AD_Client_ID=? AND JP_WF_User_From_ID=? AND AD_WF_Node_ID = ? AND JP_WF_Org_ID = ? AND ValidFrom = ? ";
				}
			}

			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try
			{
				pstmt = DB.prepareStatement(sql, get_TrxName());
				pstmt.setInt(1, getAD_Client_ID());
				pstmt.setInt(2, getJP_WF_User_From_ID());
				if(get_Value(COLUMNNAME_AD_WF_Node_ID) == null )
				{
					if(get_Value(COLUMNNAME_JP_WF_Org_ID) == null )
					{
						pstmt.setTimestamp(3, getValidFrom());
					}else {
						pstmt.setInt(3, getJP_WF_Org_ID());
						pstmt.setTimestamp(4, getValidFrom());
					}
				}else {
					if(get_Value(COLUMNNAME_JP_WF_Org_ID) == null )
					{
						pstmt.setInt(3, getAD_WF_Node_ID());
						pstmt.setTimestamp(4, getValidFrom());
					}else {
						pstmt.setInt(3, getAD_WF_Node_ID());
						pstmt.setInt(4, getJP_WF_Org_ID());
						pstmt.setTimestamp(5, getValidFrom());
					}
				}

				rs = pstmt.executeQuery();
				if (rs.next())
				{
					log.saveError("Error", Msg.getMsg(getCtx(), "JP_AlreadyRegistered"));
					return false;
				}
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
		}

		//Cache Reset
		if(newRecord
				|| is_ValueChanged(COLUMNNAME_AD_Workflow_ID) || is_ValueChanged(COLUMNNAME_AD_WF_Node_ID) ||  is_ValueChanged(COLUMNNAME_ValidFrom)
				|| is_ValueChanged(COLUMNNAME_JP_WF_Org_ID) ||  is_ValueChanged(COLUMNNAME_JP_WF_User_From_ID) ||  is_ValueChanged(COLUMNNAME_JP_WF_User_To_ID)
				|| is_ValueChanged(COLUMNNAME_IsActive) )
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

	/** Cache			*/
	private static CCache<String, MWFAutoForward> s_cache = new CCache<String, MWFAutoForward>(Table_Name, 100, 60);
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	static public MWFAutoForward get(MWFActivity wfActivity)
	{
		return MWFAutoForward.get(wfActivity.getAD_Client_ID(), wfActivity.getAD_User_ID(), wfActivity.getAD_WF_Node_ID(), wfActivity.getAD_Org_ID(), wfActivity.getCreated(), wfActivity.get_TrxName());
	}

	static public MWFAutoForward get(int AD_Client_ID, int AD_User_ID, int AD_WF_Node_ID, int AD_Org_ID, Timestamp Created, String trxName)
	{
		String key = "" + AD_Client_ID + "_" + AD_User_ID + "_" + AD_WF_Node_ID + "_" + AD_Org_ID + "_" + sdf.format(Created);
		MWFAutoForward autoForward = null;
		if(s_cache.containsKey(key))
		{
			autoForward = s_cache.get(key);
			return autoForward;
		}

		final String sql = "SELECT * FROM JP_WF_AutoForward WHERE AD_Client_ID=? AND JP_WF_User_From_ID = ? "
							+ " AND (AD_WF_Node_ID = ? OR AD_WF_Node_ID IS NULL) AND (JP_WF_Org_ID = ? OR JP_WF_Org_ID IS NULL) AND ValidFrom <= ? AND IsActive = 'Y'"
							+ " ORDER BY AD_WF_Node_ID NULLS LAST, JP_WF_Org_ID NULLS LAST, ValidFrom DESC";

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try
		{
			pstmt = DB.prepareStatement(sql, trxName);
			pstmt.setInt(1, AD_Client_ID);
			pstmt.setInt(2, AD_User_ID);
			pstmt.setInt(3, AD_WF_Node_ID);
			pstmt.setInt(4, AD_Org_ID);
			pstmt.setTimestamp(5, Created);

			rs = pstmt.executeQuery();
			if (rs.next())
				autoForward = new MWFAutoForward (Env.getCtx(), rs, trxName);
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

		if(autoForward != null)
		{
			MWFAutoForward reForward = MWFAutoForward.get(AD_Client_ID, autoForward.getJP_WF_User_To_ID(), AD_WF_Node_ID, AD_Org_ID, Created, trxName);
			if(reForward != null)
			{
				autoForward = reForward;
			}
		}

		s_cache.put(key, autoForward);//In spite of null, I put instance into the cache for performance.

		return autoForward;
	}
}
