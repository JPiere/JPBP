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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

import org.compiere.model.MColumn;
import org.compiere.model.MRefList;
import org.compiere.model.MSysConfig;
import org.compiere.model.MTable;
import org.compiere.model.MUser;
import org.compiere.model.MWFActivityApprover;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;
import org.compiere.wf.MWFActivity;
import org.compiere.wf.MWFEventAudit;

import jpiere.base.plugin.org.adempiere.base.JPiereWFActivityModelValidator;
import jpiere.base.plugin.org.adempiere.model.MWFAutoForward;


/**
 *  JPIERE-0513: Approval of Unprocessed Work flow Activity at Info Window.
 *  JPIERE-0519: WF Auto Forward
 *  JPIERE-0538: Send approval request notification
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
		int AD_User_ID = getAD_User_ID();
		MWFActivityApprover[] m_ActivityApprovers = null;

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

			String wfState = m_activity.getWFState();
			if(!MWFActivity.WFSTATE_Suspended.equals(wfState))
			{
				MColumn column = MColumn.get(getCtx(), "AD_WF_Activity", "WFState");
				int AD_Reference_Value_ID = column.getAD_Reference_Value_ID();
				if(AD_Reference_Value_ID == 0)
					AD_Reference_Value_ID = 305; //WF_Instance State
				
				msg = msg + " - " + Msg.getMsg(getCtx(), "DocProcessed")
						+ " - " + Msg.getElement(getCtx(), "WFState") + ":"+MRefList.getListName(getCtx(), AD_Reference_Value_ID, wfState);
				addBufferLog(0, null, null, msg, m_activity.getAD_Table_ID(), m_activity.getRecord_ID());
				continue;
			}

			/**
			 * Forward
			 */
			if(p_JP_WF_Forward_User_ID != 0)
			{
				if(m_activity.getAD_User_ID() == AD_User_ID)//Forward - AD_WF_Activity.AD_User_ID
				{
					if(p_JP_WF_Forward_User_ID == AD_User_ID)
					{
						//The user is already an approver.
						addLog(msg + " : " + MUser.getNameOfUser(AD_User_ID) + " - " + Msg.getMsg(getCtx(), "JP_WF_UserAlreadyApprover"));

					}else {

						//JPIERE-0519: WF Auto Forward - Start
						LocalDateTime localDateTime = LocalDateTime.now();
						MWFAutoForward autoForward = MWFAutoForward.get(m_activity.getAD_Client_ID(), p_JP_WF_Forward_User_ID, m_activity.getAD_WF_Node_ID()
																				, m_activity.getAD_Org_ID(), Timestamp.valueOf(localDateTime), get_TrxName());
						if(autoForward != null)
						{
							if(MSysConfig.getBooleanValue("JP_WF_AUTO_FORWARD_LOG", true, m_activity.getAD_Client_ID(), m_activity.getAD_Org_ID()))
							{
								MWFEventAudit eventLog = new MWFEventAudit(m_activity);
								eventLog.setEventType(MWFEventAudit.EVENTTYPE_StateChanged);
								eventLog.setWFState(MWFEventAudit.WFSTATE_Suspended);
								eventLog.setAttributeName("Auto Forward - AD_WF_Activity");
								eventLog.setOldValue(MUser.getNameOfUser(p_JP_WF_Forward_User_ID));
								eventLog.setNewValue(MUser.getNameOfUser(autoForward.getJP_WF_User_To_ID()));
								eventLog.setDescription(Msg.getElement(getCtx(), MWFAutoForward.COLUMNNAME_JP_WF_AutoForward_ID));
								eventLog.saveEx(get_TrxName());
							}

							p_JP_WF_Forward_User_ID = autoForward.getJP_WF_User_To_ID();

						}//JPIERE-0519: WF Auto Forward - End

						//Forward
						if(!m_activity.forwardTo(p_JP_WF_Forward_User_ID, p_Comments))
						{
							throw new Exception(Msg.getMsg(getCtx(), "CannotForward"));
						}

						//JPIERE-0538: Send approval request notification
						if(m_activity.get_ColumnIndex(JPiereWFActivityModelValidator.IS_PROCESSED_APPROVAL_REQUEST) >= 0 )
						{
							JPiereWFActivityModelValidator.sendApprovalRequestNotification(getCtx(), m_activity, p_JP_WF_Forward_User_ID, p_Comments, get_TrxName());
							m_activity.set_ValueNoCheck(JPiereWFActivityModelValidator.IS_PROCESSED_APPROVAL_REQUEST, "Y");
						}//JPIERE-0538

						MUser oldUser = MUser.get(getCtx(), AD_User_ID);
						MUser newUser = MUser.get(getCtx(), p_JP_WF_Forward_User_ID);

						MWFEventAudit ea = createWFEventAudit(m_activity);
						ea.setOldValue(oldUser.getName()+ "("+oldUser.getAD_User_ID()+")");
						ea.setNewValue(newUser.getName()+ "("+newUser.getAD_User_ID()+")");
						ea.saveEx(get_TrxName());
					}


				}else {//Forward - AD_WF_ActivityApprover.AD_User_ID


					//JPIERE-0519: WF Auto Forward - Start
					LocalDateTime localDateTime = LocalDateTime.now();
					MWFAutoForward autoForward = MWFAutoForward.get(m_activity.getAD_Client_ID(), p_JP_WF_Forward_User_ID, m_activity.getAD_WF_Node_ID()
																			, m_activity.getAD_Org_ID(), Timestamp.valueOf(localDateTime), get_TrxName());
					if(autoForward != null)
					{
						if(MSysConfig.getBooleanValue("JP_WF_AUTO_FORWARD_LOG", true, m_activity.getAD_Client_ID(), m_activity.getAD_Org_ID()))
						{
							MWFEventAudit eventLog = new MWFEventAudit(m_activity);
							eventLog.setEventType(MWFEventAudit.EVENTTYPE_StateChanged);
							eventLog.setWFState(MWFEventAudit.WFSTATE_Suspended);
							eventLog.setAttributeName("Auto Forward - AD_WF_ActivityApprover");
							eventLog.setOldValue(MUser.getNameOfUser(p_JP_WF_Forward_User_ID));
							eventLog.setNewValue(MUser.getNameOfUser(autoForward.getJP_WF_User_To_ID()));
							eventLog.setDescription(Msg.getElement(getCtx(), MWFAutoForward.COLUMNNAME_JP_WF_AutoForward_ID));
							eventLog.saveEx(get_TrxName());
						}

						p_JP_WF_Forward_User_ID = autoForward.getJP_WF_User_To_ID();

					}//JPIERE-0519: WF Auto Forward - End

					m_ActivityApprovers = getActivityApprovers(m_activity.getAD_WF_Activity_ID());
					boolean isAlreadyRegistered = false;
					int additional_User_ID = 0;
					for(String Additional_User_ID : p_JP_WF_Additional_User_Multi)
					{
						isAlreadyRegistered = false;
						additional_User_ID = Integer.valueOf(Additional_User_ID).intValue();
						if(p_JP_WF_Forward_User_ID == additional_User_ID)
						{
							isAlreadyRegistered = true;

							if(autoForward == null)
							{
								//The user is already an approver.
								addLog(msg + " : " + MUser.getNameOfUser(additional_User_ID) + " - " + Msg.getMsg(getCtx(), "JP_WF_UserAlreadyApprover"));
							}else {
								//The user is already an approver.
								addLog(msg + " : " + Msg.getElement(getCtx(), MWFAutoForward.COLUMNNAME_JP_WF_AutoForward_ID)
													+ " - " + MUser.getNameOfUser(p_JP_WF_Forward_User_ID) + " - " + Msg.getMsg(getCtx(), "JP_WF_UserAlreadyApprover"));
							}
							break;
						}
					}//for

					if(!isAlreadyRegistered)
					{
						for(MWFActivityApprover approver  : m_ActivityApprovers)
						{
							if(approver.getAD_User_ID() == AD_User_ID)
							{
								//Duplicate Check
								for(MWFActivityApprover approver2  : m_ActivityApprovers)
								{
									if(approver2.getAD_User_ID() == p_JP_WF_Forward_User_ID)
									{
										isAlreadyRegistered =true;
										if(autoForward == null)
										{
											//The user is already an approver.
											addLog(msg + " : " + MUser.getNameOfUser(p_JP_WF_Forward_User_ID) + " - " + Msg.getMsg(getCtx(), "JP_WF_UserAlreadyApprover"));

										}else {
											//The user is already an approver.
											addLog(msg + " : " + Msg.getElement(getCtx(), MWFAutoForward.COLUMNNAME_JP_WF_AutoForward_ID)
																+ " - " + MUser.getNameOfUser(p_JP_WF_Forward_User_ID) + " - " + Msg.getMsg(getCtx(), "JP_WF_UserAlreadyApprover"));
										}
										break;
									}
								}

								if(!isAlreadyRegistered)
								{
									MUser oldUser = MUser.get(getCtx(), AD_User_ID);
									MUser newUser = MUser.get(getCtx(), p_JP_WF_Forward_User_ID);

									approver.setAD_User_ID(p_JP_WF_Forward_User_ID);

									//JPIERE-0538: Send approval request notification
									if(approver.get_ColumnIndex(JPiereWFActivityModelValidator.IS_PROCESSED_APPROVAL_REQUEST) >= 0 )
									{
										JPiereWFActivityModelValidator.sendAdditinalApprovalRequestNotification(getCtx(), m_activity, approver, p_Comments, get_TrxName());
										approver.set_ValueNoCheck(JPiereWFActivityModelValidator.IS_PROCESSED_APPROVAL_REQUEST, "Y");
									}//JPIERE-0538

									approver.saveEx(get_TrxName());

									m_activity.setTextMsg(p_Comments);
									m_activity.saveEx(get_TrxName());

									MWFEventAudit ea = createWFEventAudit(m_activity);
									ea.setOldValue(oldUser.getName()+ "("+oldUser.getAD_User_ID()+")");
									ea.setNewValue(newUser.getName()+ "("+newUser.getAD_User_ID()+")");
									ea.saveEx(get_TrxName());
									break;
								}
							}
						}//for
					}
				}

			}else {//p_JP_WF_Forward_User_ID == 0

				if(p_JP_WF_Additional_User_Multi.length==0)
				{
					//Please enter either Forward User or Additional Approver.
					throw new Exception(Msg.getMsg(getCtx(), "JP_WFActivityForwardMandatory"));
				}

				m_activity.setTextMsg(p_Comments);
				m_activity.saveEx(get_TrxName());

			}//if(p_JP_WF_Forward_User_ID != 0)



			/**
			 * Additional Approver
			 */
			if(m_ActivityApprovers == null)
				m_ActivityApprovers = getActivityApprovers(m_activity.getAD_WF_Activity_ID());
			int additional_User_ID = 0;
			boolean isAlreadyRegistered = false;
			MUser additionalUser = null;
			MWFAutoForward autoForward = null;
			ArrayList<Integer> list_AutoForward_User_ID = new ArrayList<Integer>();//Check Auto Forward Duplicate.

			for(String string_Additional_User_ID : p_JP_WF_Additional_User_Multi)
			{
				isAlreadyRegistered = false;
				additional_User_ID = Integer.valueOf(string_Additional_User_ID).intValue();

				//JPIERE-0519: WF Auto Forward - Start
				LocalDateTime localDateTime = LocalDateTime.now();
				autoForward = MWFAutoForward.get(m_activity.getAD_Client_ID(), additional_User_ID, m_activity.getAD_WF_Node_ID()
																		, m_activity.getAD_Org_ID(), Timestamp.valueOf(localDateTime), get_TrxName());
				if(autoForward != null)
				{
					additional_User_ID = autoForward.getJP_WF_User_To_ID();
					if(list_AutoForward_User_ID.contains(additional_User_ID))
					{
						if(MSysConfig.getBooleanValue("JP_WF_AUTO_FORWARD_LOG", true, m_activity.getAD_Client_ID(), m_activity.getAD_Org_ID()))
						{
							MWFEventAudit eventLog = new MWFEventAudit(m_activity);
							eventLog.setEventType(MWFEventAudit.EVENTTYPE_StateChanged);
							eventLog.setWFState(MWFEventAudit.WFSTATE_Suspended);
							eventLog.setAttributeName("Auto Forward - AD_WF_ActivityApprover - Skip - Duplicate");
							eventLog.setOldValue(MUser.getNameOfUser(Integer.valueOf(string_Additional_User_ID).intValue()));
							eventLog.setNewValue(MUser.getNameOfUser(autoForward.getJP_WF_User_To_ID()));
							eventLog.setDescription(Msg.getElement(getCtx(), MWFAutoForward.COLUMNNAME_JP_WF_AutoForward_ID));
							eventLog.saveEx(get_TrxName());
						}
						continue;

					}else {

						list_AutoForward_User_ID.add(additional_User_ID);
						if(MSysConfig.getBooleanValue("JP_WF_AUTO_FORWARD_LOG", true, m_activity.getAD_Client_ID(), m_activity.getAD_Org_ID()))
						{
							MWFEventAudit eventLog = new MWFEventAudit(m_activity);
							eventLog.setEventType(MWFEventAudit.EVENTTYPE_StateChanged);
							eventLog.setWFState(MWFEventAudit.WFSTATE_Suspended);
							eventLog.setAttributeName("Auto Forward - AD_WF_ActivityApprover");
							eventLog.setOldValue(MUser.getNameOfUser(Integer.valueOf(string_Additional_User_ID).intValue()));
							eventLog.setNewValue(MUser.getNameOfUser(autoForward.getJP_WF_User_To_ID()));
							eventLog.setDescription(Msg.getElement(getCtx(), MWFAutoForward.COLUMNNAME_JP_WF_AutoForward_ID));
							eventLog.saveEx(get_TrxName());
						}
					}
				}//JPIERE-0519: WF Auto Forward - End

				for(MWFActivityApprover approver : m_ActivityApprovers)
				{
					if(approver.getAD_User_ID() == additional_User_ID)
					{
						isAlreadyRegistered = true;
						if(autoForward == null)
						{
							//The user is already an approver.
							addLog(msg + " : " + MUser.getNameOfUser(additional_User_ID) + " - " + Msg.getMsg(getCtx(), "JP_WF_UserAlreadyApprover"));
						}else {
							//The user is already an approver.
							addLog(msg + " : " + Msg.getElement(getCtx(), MWFAutoForward.COLUMNNAME_JP_WF_AutoForward_ID)
												+ " - " + MUser.getNameOfUser(additional_User_ID) + " - " + Msg.getMsg(getCtx(), "JP_WF_UserAlreadyApprover"));
						}
						break;
					}
				}

				if(isAlreadyRegistered)
					continue;

				MWFActivityApprover wfApprover = new MWFActivityApprover(getCtx(), 0, get_TrxName());
				wfApprover.setAD_Org_ID(m_activity.getAD_Org_ID());
				wfApprover.setAD_WF_Activity_ID(m_activity.getAD_WF_Activity_ID());
				wfApprover.setAD_User_ID(additional_User_ID);

				//JPIERE-0538: Send approval request notification
				if(wfApprover.get_ColumnIndex(JPiereWFActivityModelValidator.IS_PROCESSED_APPROVAL_REQUEST) >= 0 )
				{
					JPiereWFActivityModelValidator.sendAdditinalApprovalRequestNotification(getCtx(), m_activity, wfApprover, p_Comments, get_TrxName());
					wfApprover.set_ValueNoCheck(JPiereWFActivityModelValidator.IS_PROCESSED_APPROVAL_REQUEST, "Y");
				}//JPIERE-0538

				wfApprover.saveEx(get_TrxName());

				additionalUser = MUser.get(additional_User_ID);
				MWFEventAudit ea = createWFEventAudit(m_activity);
				ea.setDescription("WFActivityForward - Additional Approver");
				ea.setNewValue(additionalUser.getName()+ "("+additionalUser.getAD_User_ID()+")");
				ea.saveEx(get_TrxName());
			}//for

			addBufferLog(0, null, null, msg, m_activity.getAD_Table_ID(), m_activity.getRecord_ID());
		}

		return null;
	}

	private MWFActivityApprover[] getActivityApprovers(int AD_WF_Activity_ID)
	{
		StringBuilder whereClauseFinal = new StringBuilder(MWFActivityApprover.COLUMNNAME_AD_WF_Activity_ID+"=? ");
		String orderClause = MWFActivityApprover.COLUMNNAME_AD_User_ID;
		//
		List<MWFActivityApprover> list = new Query(getCtx(), MWFActivityApprover.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(AD_WF_Activity_ID)
										.setOrderBy(orderClause)
										.setOnlyActiveRecords(true)
										.list();

		return list.toArray(new MWFActivityApprover[list.size()]);
	}

	private MWFEventAudit createWFEventAudit(MWFActivity m_activity )
	{
		MWFEventAudit wfEventAudit = new MWFEventAudit(m_activity);
		wfEventAudit.setAD_WF_Process_ID(m_activity.getAD_WF_Process_ID());
		wfEventAudit.setAD_WF_Node_ID(m_activity.getAD_WF_Node_ID());
		wfEventAudit.setWFState(m_activity.getWFState());
		wfEventAudit.setAD_WF_Responsible_ID(m_activity.getAD_WF_Responsible_ID());
		wfEventAudit.setAD_User_ID(m_activity.getAD_User_ID());
		wfEventAudit.setAD_Table_ID(m_activity.getAD_Table_ID());
		wfEventAudit.setRecord_ID(m_activity.getRecord_ID());
		wfEventAudit.setEventType(MWFEventAudit.EVENTTYPE_ProcessCreated);
		wfEventAudit.setAttributeName("AD_User_ID");
		wfEventAudit.setDescription("WFActivityForward");
		wfEventAudit.setTextMsg(p_Comments);
		wfEventAudit.setElapsedTimeMS(Env.ZERO);

		return wfEventAudit;
	}

}
