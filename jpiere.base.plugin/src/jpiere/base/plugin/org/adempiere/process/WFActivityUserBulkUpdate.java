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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;

import org.adempiere.util.IProcessUI;
import org.compiere.model.I_AD_WF_Activity;
import org.compiere.model.MPInstance;
import org.compiere.model.MProcess;
import org.compiere.model.Query;
import org.compiere.process.ProcessInfo;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Trx;
import org.compiere.wf.MWFActivity;

/**
 * JPIERE-7 Update user of WF Activity in a bulk process
 *
 * This process update user of WF Activity that WFState is "OS" and Processed is "false".
 *
 *  @author Hideaki Hagiwara
 *  @version $Id: WFActivityUserBatchUpdate.java,v 1.0 2014/03/04 00:00:00 $
 */
public class WFActivityUserBulkUpdate extends SvrProcess
{

	private static final int PROCESS_Manage_Activity = 278;	//AD_WF_Activity_Manage(Manage Activity) Process

	private int 		p_AD_Client_ID = 0;

	/**Target Workflow(Mandatory)	*/
	private int			p_AD_Workflow_ID = 0;

	/**Original User(Mandatory)*/
	private int			p_AD_User_ID = 0;

	/**Substitute User(Mandatory)*/
	private int			p_AD_User_Substitute_ID = 0;

	/**Target Organization(Option)*/
	private int			p_AD_Org_ID = 0;

	/**Target WF Responsible(Option)*/
	private int			p_AD_WF_Responsible_ID = 0;

	/**Target Created Date(Option)*/
	private Timestamp	p_Created_From = null;
	private Timestamp	p_Created_To = null;


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
			}else if (name.equals("AD_User_ID")){
				p_AD_User_ID = para[i].getParameterAsInt();
			}else if (name.equals("AD_User_Substitute_ID")){
				p_AD_User_Substitute_ID = para[i].getParameterAsInt();
			}else if (name.equals("AD_Workflow_ID")){
				p_AD_Workflow_ID = para[i].getParameterAsInt();
			}else if (name.equals("Created")){
				p_Created_From = (Timestamp)para[i].getParameter();
				p_Created_To = (Timestamp)para[i].getParameter_To();
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(p_Created_To.getTime());
				cal.add(Calendar.DAY_OF_MONTH, 1);
				p_Created_To = new Timestamp(cal.getTimeInMillis());
			}else if (name.equals("AD_Org_ID")){
				p_AD_Org_ID = para[i].getParameterAsInt();
			}else if (name.equals("AD_WF_Responsible_ID")){
				p_AD_WF_Responsible_ID = para[i].getParameterAsInt();
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
		//Mandatory parameters
		StringBuilder whereClause = new StringBuilder(MWFActivity.COLUMNNAME_AD_User_ID + " = ? AND "
														+ MWFActivity.COLUMNNAME_AD_Workflow_ID + "= ? AND "
														+ MWFActivity.COLUMNNAME_AD_Client_ID + " = ? AND "
														+ MWFActivity.COLUMNNAME_Processed +" = 'N' AND "
														+ MWFActivity.COLUMNNAME_WFState + " = 'OS' "
														);

		ArrayList<Object> getWFActivitiesParamList = new ArrayList<Object>();
		getWFActivitiesParamList.add(p_AD_User_ID);
		getWFActivitiesParamList.add(p_AD_Workflow_ID);
		getWFActivitiesParamList.add(p_AD_Client_ID);

		//Option parameters
		if (p_AD_Org_ID != 0)
		{
			whereClause.append("AND " + MWFActivity.COLUMNNAME_AD_Org_ID + " = ? ");
			getWFActivitiesParamList.add(p_AD_Org_ID);
		}


		if(p_AD_WF_Responsible_ID !=0)
		{
			whereClause.append("AND " + MWFActivity.COLUMNNAME_AD_WF_Responsible_ID + " = ? ");
			getWFActivitiesParamList.add(p_AD_WF_Responsible_ID);
		}

		if(p_Created_From != null)
		{
			whereClause.append("AND " + MWFActivity.COLUMNNAME_Created + " >= ? ");
			getWFActivitiesParamList.add(p_Created_From);
		}

		if(p_Created_To != null)
		{
			whereClause.append("AND " + MWFActivity.COLUMNNAME_Created + " <= ? ");
			getWFActivitiesParamList.add(p_Created_To);
		}

		//Get Target WF Activities
		List<MWFActivity> list = new Query(getCtx(), I_AD_WF_Activity.Table_Name, whereClause.toString(), get_TrxName())
										.setParameters(getWFActivitiesParamList)
										.list();
		MWFActivity[] activities = list.toArray(new MWFActivity[list.size()]);


		//Prepare AD_WF_Activity_Manage(Manage Activity) process Parameters.
		ProcessInfoParameter[] pipParams = new ProcessInfoParameter[]{new ProcessInfoParameter("AD_User_ID", p_AD_User_Substitute_ID, null, null, null)};

		IProcessUI processMonitor = Env.getProcessUI(getCtx());
		int success = 0;
		int failure = 0;

		for(int i = 0; i < activities.length; i++)
		{
			MProcess process = MProcess.get(getCtx(), PROCESS_Manage_Activity);
			MPInstance pInstance = new MPInstance(process, -1, 0, null);

			ProcessInfo pi = new ProcessInfo(process.getName(), PROCESS_Manage_Activity, MWFActivity.Table_ID, activities[i].getAD_WF_Activity_ID());
			pi.setParameter(pipParams);
			pi.setAD_User_ID(getAD_User_ID());
			pi.setAD_Client_ID(getAD_Client_ID());
			pi.setAD_PInstance_ID(pInstance.getAD_PInstance_ID());

			boolean isOK = process.processItWithoutTrxClose(pi,Trx.get(get_TrxName(), false));
			if(isOK)
			{
				success++;
			}else{
				failure++;
				continue;
			}

			String msg =Msg.getElement(getCtx(), "AD_WF_Process_ID")+" "+Msg.getElement(getCtx(), "TextMsg")+" => "+ activities[i].getAD_WF_Process().getTextMsg();
			addBufferLog(0, null, null, msg, I_AD_WF_Activity.Table_ID, activities[i].get_ID());

			if (processMonitor != null)
			{
				processMonitor.statusUpdate(msg);
			}else{
				processMonitor = Env.getProcessUI(getCtx());
			}

		}//for


		return  "Success" + " = " + success + "    Failure" + " = " + failure ;
	}	//	doIt

}	//WFActivityUserBulkUpdate
