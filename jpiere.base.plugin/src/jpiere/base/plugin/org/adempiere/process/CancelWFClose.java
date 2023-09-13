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
import java.util.logging.Level;

import org.compiere.model.MRefList;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Msg;
import org.compiere.wf.MWFActivity;
import org.compiere.wf.MWFProcess;

/**
 *  JPIERE-0607: Cancel Workflow.
 *  Close of Cancel Workflow.
 *
 *  @author Hideaki Hagiwara（h.hagiwara@oss-erp.co.jp）
 *
 */
public class CancelWFClose extends SvrProcess {
	
	private static final String COLUMNNAME_JP_CancelWFAction = "JP_CancelWFAction";
	 
	@Override
	protected void prepare() 
	{
		;
	}

	@Override
	protected String doIt() throws Exception
	{
		int AD_Table_ID = getTable_ID();
		int Record_ID = getRecord_ID();
		
		MWFProcess[] wfProcesses = getUnprocessedWF(AD_Table_ID, Record_ID);
		MWFActivity[] activities = null;
		for(MWFProcess wfProcess : wfProcesses)
		{
			activities =	wfProcess.getActivities(true, false, get_TrxName());
			
			//3 is meaning Cancel WF base node only,
			//In case of Cancel WF base node only, We Need not close Cancel WF.
			//In other words, over 3 is assumed that there are some approval node and need to close Cancel WF.
			if(activities.length > 3)
			{
				wfProcess.setWFState(MWFProcess.WFSTATE_Completed);
				wfProcess.saveEx(get_TrxName());
			}
		}
		
		//AD_Reference_ID=305(WF_Instance State)		
		String wfStatus = MRefList.getListName(getCtx(), 305,  MWFProcess.WFSTATE_Completed) ;
		
		return Msg.getElement(getCtx(), COLUMNNAME_JP_CancelWFAction) + " : " + MWFProcess.WFSTATE_Completed + "(" + wfStatus + ")" ;
	}
	
	private MWFProcess[] getUnprocessedWF(int AD_Table_ID, int Record_ID)
	{
		ArrayList<MWFProcess> list = new ArrayList<MWFProcess>();
		String sql = "SELECT * FROM AD_WF_Process WHERE AD_Table_ID=? AND Record_ID=? AND Processed<>'Y'";
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, AD_Table_ID);
			pstmt.setInt(2, Record_ID);
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add(new MWFProcess (getCtx(), rs, get_TrxName()));
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

		MWFProcess[] wfProcesses = new MWFProcess[list.size()];
		list.toArray(wfProcesses);
		return wfProcesses;
		
	}

}
