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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.adempiere.util.ProcessUtil;
import org.compiere.model.MColumn;
import org.compiere.model.MProcess;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfo;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;
import org.compiere.wf.MWFActivity;
import org.compiere.wf.MWFProcess;

import jpiere.base.plugin.org.adempiere.model.MRecognition;

/**
 *  JPIERE-0523 - Revenue Recognition(Info Window)
 *  JPIERE-0524 - Expense Recognition(Info Window)
 *  JPIERE-0525 - Revenue Recognition(Process)
 *  JPIERE-0526 - Expense Recognition(Process)
 *
 *  Document Status of Revenue/Expense Recognition Bulk Update
 *
 *  @author Hideaki Hagiwara（h.hagiwara@oss-erp.co.jp）
 *
 */
public class BulkUpdateRecognitionDocStatus extends SvrProcess
{

	private Timestamp p_DateAcct_From = null;
	private Timestamp p_DateAcct_To = null;
	private int p_AD_Org_ID = 0;
	private int p_C_DocTypeTarget_ID = 0;
	private int p_JP_Contract_ID = 0;
	private int p_JP_ContractContent_ID = 0;
	private boolean p_IsSOTrx = true;


	private String p_DocAction = DocAction.ACTION_Complete;
	private Timestamp p_JP_OverwriteDateAcct = null;

	private String p_JP_Process_Value = null;

	@Override
	protected void prepare()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
			{
				;
			}else if (name.equals("DateAcct")){
				p_DateAcct_From = para[i].getParameterAsTimestamp();
				p_DateAcct_To = para[i].getParameter_ToAsTimestamp();
			}else if (name.equals("AD_Org_ID")) {
				p_AD_Org_ID = para[i].getParameterAsInt();
			}else if (name.equals("C_DocTypeTarget_ID")) {
				p_C_DocTypeTarget_ID = para[i].getParameterAsInt();
			}else if (name.equals("JP_Contract_ID")) {
				p_JP_Contract_ID = para[i].getParameterAsInt();
			}else if (name.equals("JP_ContractContent_ID")) {
				p_JP_ContractContent_ID = para[i].getParameterAsInt();
			}else if (name.equals("JP_OverwriteDateAcct")) {
				p_JP_OverwriteDateAcct = para[i].getParameterAsTimestamp();
			}else if (name.equals("DocAction")) {
				p_DocAction = para[i].getParameterAsString();
			}else if (name.equals("JP_Process_Value")) {
				p_JP_Process_Value = para[i].getParameterAsString();
			}else if (name.equals("IsSOTrx")) {
				p_IsSOTrx = para[i].getParameterAsBoolean();
			}else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
	}

	@Override
	protected String doIt() throws Exception
	{
		MRecognition[] list = getRecognition();
		if(list == null)
		{
			return "ok";
		}

		int successNo = 0;
		int failureNo = 0;
		String success = Msg.getMsg(getCtx(), "JP_Success");
		String failure = Msg.getMsg(getCtx(), "JP_Failure");

		ProcessInfo pInfo = getProcessInfo();
		MColumn docActionColumn = MColumn.get(getCtx(), MRecognition.Table_Name, MRecognition.COLUMNNAME_DocAction);
		MProcess process = MProcess.get(docActionColumn.getAD_Process_ID());

		for(MRecognition recog : list)
		{
			try
			{
				if(recog.isProcessed())
					continue;

				if(p_JP_OverwriteDateAcct != null)
					recog.setDateAcct(p_JP_OverwriteDateAcct);

				String wfStatus = MWFActivity.getActiveInfo(Env.getCtx(), MRecognition.Table_ID, recog.getJP_Recognition_ID());
				if (Util.isEmpty(wfStatus))
				{
					if(DocAction.ACTION_Complete.equals(p_DocAction))
					{
						if(p_JP_OverwriteDateAcct != null)
							recog.saveEx(get_TrxName());

						pInfo.setPO(recog);
						pInfo.setRecord_ID(recog.getJP_Recognition_ID());
						pInfo.setTable_ID(MRecognition.Table_ID);
						MWFProcess wfProcess = ProcessUtil.startWorkFlow(Env.getCtx(), pInfo, process.getAD_Workflow_ID());
						if(wfProcess.getWFState().equals(MWFProcess.WFSTATE_Terminated))
						{
							failureNo++;
							recog.saveEx(get_TrxName());
							addBufferLog(0, null, null, failure +":" + recog.getDocumentNo(), MRecognition.Table_ID, recog.getJP_Recognition_ID());

						}else {

							successNo++;
							recog.saveEx(get_TrxName());
							addBufferLog(0, null, null, success +":" +  recog.getDocumentNo(), MRecognition.Table_ID, recog.getJP_Recognition_ID());

						}

					}else if(DocAction.ACTION_Prepare.equals(p_DocAction)) {

						if(recog.processIt(p_DocAction))
						{
							successNo++;
							recog.saveEx(get_TrxName());
							addBufferLog(0, null, null, success +":" + recog.getDocumentNo(), MRecognition.Table_ID, recog.getJP_Recognition_ID());
						}else {

							failureNo++;
							recog.saveEx(get_TrxName());
							addBufferLog(0, null, null, failure +":" + recog.getDocumentNo(), MRecognition.Table_ID, recog.getJP_Recognition_ID());
						}

					}

				}else {

					failureNo++;
					addBufferLog(0, null, null, Msg.getMsg(getCtx(), "WFActiveForRecord") +":" + recog.getDocumentNo(), MRecognition.Table_ID, recog.getJP_Recognition_ID());
				}

			}catch(Exception e){
				failureNo++;
				addBufferLog(0, null, null, failure +":" + recog.getDocumentNo(), MRecognition.Table_ID, recog.getJP_Recognition_ID());
			}

		}


		return success + " : " + successNo + "  /  " +  failure + " " + failureNo;
	}

	private MRecognition[] getRecognition() throws Exception
	{

		if("Y".equals(p_JP_Process_Value))//From Menu of Process
		{
			//Mandatory parameters
			StringBuilder whereClause = new StringBuilder(MRecognition.COLUMNNAME_AD_Client_ID + " = ? AND "
															+ MRecognition.COLUMNNAME_Processed + " = 'N' AND "
															+ MRecognition.COLUMNNAME_IsSOTrx + " = " + (p_IsSOTrx? "'Y'" : "'N'")
															);

			ArrayList<Object> docListParams = new ArrayList<Object>();
			docListParams.add(getAD_Client_ID());

			//Option parameters
			if (p_AD_Org_ID != 0)
			{
				whereClause.append(" AND " + MRecognition.COLUMNNAME_AD_Org_ID + " = ? ");
				docListParams.add(p_AD_Org_ID);
			}

			if(p_DateAcct_From != null)
			{
				whereClause.append(" AND " + MRecognition.COLUMNNAME_DateAcct + " >= ? ");
				docListParams.add(p_DateAcct_From);
			}

			if(p_DateAcct_To != null)
			{
				whereClause.append(" AND " + MRecognition.COLUMNNAME_DateAcct + " <= ? ");
				docListParams.add(p_DateAcct_To);
			}

			if(p_C_DocTypeTarget_ID != 0)
			{
				whereClause.append(" AND " + MRecognition.COLUMNNAME_C_DocTypeTarget_ID + " = ? ");
				docListParams.add(p_C_DocTypeTarget_ID);
			}

			if(p_JP_Contract_ID != 0)
			{
				whereClause.append(" AND " + MRecognition.COLUMNNAME_JP_Contract_ID + " = ? ");
				docListParams.add(p_JP_Contract_ID);
			}


			if(p_JP_ContractContent_ID != 0)
			{
				whereClause.append(" AND " + MRecognition.COLUMNNAME_JP_ContractContent_ID + " = ? ");
				docListParams.add(p_JP_ContractContent_ID);
			}


			//Get Target WF Activities
			List<PO> list = new Query(getCtx(), MRecognition.Table_Name, whereClause.toString(), get_TrxName())
											.setParameters(docListParams)
											.list();

			MRecognition[] recogs = list.toArray(new MRecognition[list.size()]);

			return recogs ;

		}else {	//From Menu of Info Window

			ArrayList<MRecognition> list = new ArrayList<MRecognition>();

			String sql = "SELECT r.* FROM JP_Recognition r "
					+ " INNER JOIN T_Selection ts ON (r.JP_Recognition_ID = ts.T_Selection_ID AND ts.AD_PInstance_ID=?)" ;

			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				pstmt = DB.prepareStatement(sql, get_TrxName());
				pstmt.setInt(1, getAD_PInstance_ID());

				rs = pstmt.executeQuery();
				while (rs.next())
				{
					list.add(new MRecognition (getCtx(), rs, get_TrxName()));
				}
			}
			catch (Exception e)
			{
				log.log(Level.SEVERE, sql, e);
				throw e;
			}
			finally
			{
				DB.close(rs, pstmt);
				rs = null; pstmt = null;
			}

			MRecognition[] recogs = list.toArray(new MRecognition[list.size()]);

			return recogs ;
		}

	}
}
