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
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;

import org.adempiere.util.IProcessUI;
import org.adempiere.util.ProcessUtil;
import org.compiere.model.I_GL_Journal;
import org.compiere.model.MColumn;
import org.compiere.model.MJournal;
import org.compiere.model.MPeriod;
import org.compiere.model.MProcess;
import org.compiere.model.Query;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfo;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;
import org.compiere.wf.MWFActivity;
import org.compiere.wf.MWFProcess;

import jpiere.base.plugin.org.adempiere.model.MContract;
import jpiere.base.plugin.org.adempiere.model.MContractContent;



/**
 * JPIERE-0010: GL Journal Bulk Complete / Document Status of GL Journal Doc Bulk Update(Process)
 * JPIERE-0539: Create GL Journal From Invoice
 * JPIERE-0540: Document Status of GL Journal Doc Bulk Update(Manual)
 *
 *  @author Hideaki Hagiwara
 *  @version $Id: GLJournalBulkComplete.java,v 1.0 2014/05/10 00:00:00 $
 */
public class GLJournalBulkComplete extends SvrProcess {

	private int 		p_AD_Client_ID = 0;

	/**Target Organization(Option)*/
	private int			p_AD_Org_ID = 0;

	/**Target DateAcct Date(Option)*/
	private Timestamp	p_DateAcct_From = null;
	private Timestamp	p_DateAcct_To = null;

	/**Target DocStatus(Mandatory)*/
	private String		p_DocStatus = "DR";

	/**Original User(Option)*/
	private int			p_AD_User_ID = 0;

	/**Target Created Date(Option)*/
	private Timestamp	p_Created_From = null;
	private Timestamp	p_Created_To = null;

	private int p_C_DocType_ID = 0;
	private int p_JP_Contract_ID = 0;
	private int p_JP_ContractContent_ID = 0;

	private Timestamp p_JP_OverwriteDateDoc = null;
	private Timestamp p_JP_OverwriteDateAcct = null;

	private String p_DocAction = DocAction.ACTION_Complete;

	private String p_JP_Process_Value = null;

	/**
	 *  Prepare - get Parameters.
	 */
	protected void prepare()
	{
		p_AD_Client_ID =getProcessInfo().getAD_Client_ID();
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null){
				;
			}else if (name.equals("AD_Org_ID")){
				p_AD_Org_ID = para[i].getParameterAsInt();
			}else if (name.equals("DateAcct")){
				p_DateAcct_From = (Timestamp)para[i].getParameter();
				p_DateAcct_To = (Timestamp)para[i].getParameter_To();
				if(p_DateAcct_To!=null)
				{
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(p_DateAcct_To.getTime());
					cal.add(Calendar.DAY_OF_MONTH, 1);
					p_DateAcct_To = new Timestamp(cal.getTimeInMillis());
				}
			}else if (name.equals("DocStatus")){
				p_DocStatus = para[i].getParameterAsString();
			}else if (name.equals("AD_User_ID")){
				p_AD_User_ID = para[i].getParameterAsInt();
			}else if (name.equals("Created")){
				p_Created_From = (Timestamp)para[i].getParameter();
				p_Created_To = (Timestamp)para[i].getParameter_To();
				if(p_Created_To!=null)
				{
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(p_Created_To.getTime());
					cal.add(Calendar.DAY_OF_MONTH, 1);
					p_Created_To = new Timestamp(cal.getTimeInMillis());
				}
			}else if (name.equals("C_DocType_ID")){
				p_C_DocType_ID = para[i].getParameterAsInt();
			}else if (name.equals("JP_Contract_ID")){
				p_JP_Contract_ID = para[i].getParameterAsInt();
			}else if (name.equals("JP_ContractContent_ID")){
				p_JP_ContractContent_ID = para[i].getParameterAsInt();
			}else if (name.equals("JP_OverwriteDateDoc")){
				p_JP_OverwriteDateDoc = (Timestamp)para[i].getParameter();
			}else if (name.equals("JP_OverwriteDateAcct")){
				p_JP_OverwriteDateAcct = (Timestamp)para[i].getParameter();
			}else if (name.equals("DocAction")){
				p_DocAction = para[i].getParameterAsString();
			}else if (name.equals("JP_Process_Value")) {
				p_JP_Process_Value = para[i].getParameterAsString();
			}else{
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}//if
		}//for

	}//	prepare

	/**
	 *  Perform process.
	 *  @return Message (variables are parsed)
	 *  @throws Exception if not successful
	 */
	protected String doIt() throws Exception
	{
		MJournal[] glJournals = getGLjournals();
		if(glJournals == null)
		{
			return "ok";

		}

		IProcessUI processMonitor = Env.getProcessUI(getCtx());
		int successNo = 0;
		int failureNo = 0;
		String success = Msg.getMsg(getCtx(), "JP_Success");
		String failure = Msg.getMsg(getCtx(), "JP_Failure");

		ProcessInfo pInfo = getProcessInfo();
		MColumn docActionColumn = MColumn.get(getCtx(), MJournal.Table_Name, MJournal.COLUMNNAME_DocAction);
		MProcess process = MProcess.get(docActionColumn.getAD_Process_ID());

		for(int i = 0; i < glJournals.length; i++)
		{
			MJournal mj = glJournals[i];

			if(mj.isProcessed())
				continue;

			//Check validate period
			if(p_JP_OverwriteDateAcct != null)
			{
				int C_Period_ID = MPeriod.getC_Period_ID(getCtx(), p_JP_OverwriteDateAcct, mj.getAD_Org_ID());
				if (C_Period_ID == 0)
				{
					String msg = Msg.getMsg(getCtx(), "PeriodNotFound") + " : " + DisplayType.getDateFormat().format(p_JP_OverwriteDateAcct) + " - " + mj.getDocumentNo() ;
					throw new Exception(msg);
				}
			}

			if(p_JP_OverwriteDateDoc != null)
				mj.setDateDoc(p_JP_OverwriteDateDoc);

			if(p_JP_OverwriteDateAcct != null)
				mj.setDateDoc(p_JP_OverwriteDateAcct);

			String wfStatus = MWFActivity.getActiveInfo(Env.getCtx(), MJournal.Table_ID, mj.getGL_Journal_ID());
			if (Util.isEmpty(wfStatus))
			{
				try
				{
					if(DocAction.ACTION_Complete.equals(p_DocAction))
					{
						pInfo.setPO(mj);
						pInfo.setRecord_ID(mj.getGL_Journal_ID());
						pInfo.setTable_ID(MJournal.Table_ID);
						MWFProcess wfProcess = ProcessUtil.startWorkFlow(Env.getCtx(), pInfo, process.getAD_Workflow_ID());
						if(wfProcess.getWFState().equals(MWFProcess.WFSTATE_Terminated))
						{
							failureNo++;
							mj.saveEx(get_TrxName());
							addBufferLog(0, null, null, mj.getDocumentNo(), MJournal.Table_ID, mj.getGL_Journal_ID());

						}else {

							successNo++;
							mj.saveEx(get_TrxName());
							addBufferLog(0, null, null, mj.getDocumentNo(), MJournal.Table_ID, mj.getGL_Journal_ID());

						}

					}else if(DocAction.ACTION_Prepare.equals(p_DocAction)) {

						if(mj.processIt(p_DocAction))
						{
							successNo++;
							mj.saveEx(get_TrxName());
							addBufferLog(0, null, null, mj.getDocumentNo(), MJournal.Table_ID, mj.getGL_Journal_ID());
						}else {

							failureNo++;
							mj.saveEx(get_TrxName());
							addBufferLog(0, null, null, mj.getDocumentNo(), MJournal.Table_ID, mj.getGL_Journal_ID());
						}

					}
				}catch(Exception e){
					failureNo++;
					addBufferLog(0, null, null, mj.getDocumentNo(),  MJournal.Table_ID, mj.getGL_Journal_ID());
				}


			}else {

				failureNo++;
				addBufferLog(0, null, null, Msg.getMsg(getCtx(), "WFActiveForRecord") +":" + mj.getDocumentNo(), MJournal.Table_ID, mj.getGL_Journal_ID());
			}


			if (processMonitor != null)
			{
				String msg = Msg.getElement(getCtx(), "DocumentNo") + " : " + mj.getDocumentNo()
									+ " - " + Msg.getElement(getCtx(), "DocStatus") + " : " + mj.getDocStatus();
				processMonitor.statusUpdate(msg);

			}else{
				processMonitor = Env.getProcessUI(getCtx());
			}

		}//for


		return success + " : " + successNo + "  /  " +  failure + " " + failureNo;
	}	//	doIt


	private MJournal[] getGLjournals() throws Exception
	{
		if("Y".equals(p_JP_Process_Value))//From Menu of Process
		{
			//Mandatory parameters
			StringBuilder whereClause = new StringBuilder(MJournal.COLUMNNAME_AD_Client_ID + " = ? AND "
															+ MJournal.COLUMNNAME_Processed + " = 'N' AND "
															+ MJournal.COLUMNNAME_DocStatus + " = " + "'" + p_DocStatus + "'"
															);

			ArrayList<Object> docListParams = new ArrayList<Object>();
			docListParams.add(p_AD_Client_ID);

			//Option parameters
			if (p_AD_Org_ID != 0)
			{
				whereClause.append(" AND " + MJournal.COLUMNNAME_AD_Org_ID + " = ? ");
				docListParams.add(p_AD_Org_ID);
			}

			if(p_DateAcct_From != null)
			{
				whereClause.append(" AND " + MJournal.COLUMNNAME_DateAcct + " >= ? ");
				docListParams.add(p_DateAcct_From);
			}

			if(p_DateAcct_To != null)
			{
				whereClause.append(" AND " + MJournal.COLUMNNAME_DateAcct + " <= ? ");
				docListParams.add(p_DateAcct_To);
			}


			if(p_AD_User_ID != 0)
			{
				whereClause.append(" AND " + MJournal.COLUMNNAME_CreatedBy + " = ? ");
				docListParams.add(p_AD_User_ID);
			}


			if(p_Created_From != null)
			{
				whereClause.append(" AND " + MJournal.COLUMNNAME_Created + " >= ? ");
				docListParams.add(p_Created_From);
			}

			if(p_Created_To != null)
			{
				whereClause.append(" AND " + MJournal.COLUMNNAME_Created + " <= ? ");
				docListParams.add(p_Created_To);
			}

			if (p_C_DocType_ID != 0)
			{
				whereClause.append(" AND " + MJournal.COLUMNNAME_C_DocType_ID + " = ? ");
				docListParams.add(p_AD_Org_ID);
			}

			if (p_JP_Contract_ID != 0)
			{
				whereClause.append(" AND " + MContract.COLUMNNAME_JP_Contract_ID + " = ? ");
				docListParams.add(p_JP_Contract_ID);
			}

			if (p_JP_ContractContent_ID != 0)
			{
				whereClause.append(" AND " + MContractContent.COLUMNNAME_JP_ContractContent_ID + " = ? ");
				docListParams.add(p_JP_ContractContent_ID);
			}

			List<MJournal> list = new Query(getCtx(), I_GL_Journal.Table_Name, whereClause.toString(), get_TrxName())
											.setParameters(docListParams)
											.list();
			MJournal[] glJournals = list.toArray(new MJournal[list.size()]);
			return glJournals;


		}else {	//From Menu of Info Window

			ArrayList<MJournal> list = new ArrayList<MJournal>();

			String sql = "SELECT gl.* FROM GL_Journal gl "
					+ " INNER JOIN T_Selection ts ON (gl.GL_Journal_ID = ts.T_Selection_ID AND ts.AD_PInstance_ID=?)" ;

			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				pstmt = DB.prepareStatement(sql, get_TrxName());
				pstmt.setInt(1, getAD_PInstance_ID());

				rs = pstmt.executeQuery();
				while (rs.next())
				{
					list.add(new MJournal (getCtx(), rs, get_TrxName()));
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

			MJournal[] glJournals = list.toArray(new MJournal[list.size()]);
			return glJournals ;
		}
	}

}
