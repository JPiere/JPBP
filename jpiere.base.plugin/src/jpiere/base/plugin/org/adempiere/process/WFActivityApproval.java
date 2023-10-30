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
import java.util.Collection;
import java.util.logging.Level;

import org.compiere.model.MClient;
import org.compiere.model.MColumn;
import org.compiere.model.MNote;
import org.compiere.model.MRefList;
import org.compiere.model.MRole;
import org.compiere.model.MTable;
import org.compiere.model.MUser;
import org.compiere.model.MUserRoles;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;
import org.compiere.wf.MWFActivity;
import org.compiere.wf.MWFNode;
import org.compiere.wf.MWFProcess;
import org.compiere.wf.MWFResponsible;


/**
 *  JPIERE-0513: Approval of Unprocessed Work flow Activity at Info Window.
 *  JPIERE-0607: Cancel Workflow.
 *
 *  @author Hideaki Hagiwara（h.hagiwara@oss-erp.co.jp）
 *
 */
public class WFActivityApproval extends SvrProcess {

	private String p_JP_IsApproval = "N";
	private String p_Comments = null;
	private Timestamp starTime = Timestamp.valueOf(LocalDateTime.now());
	private static final String COLUMNNAME_JP_CancelWFAction = "JP_CancelWFAction";
	private static final String COLUMNNAME_JP_CancelWFStatus = "JP_CancelWFStatus";
	
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
		MWFNode node = null;

		for(MWFActivity m_activity : m_WFAs)
		{			
			m_Table = MTable.get(m_activity.getAD_Table_ID());
			m_PO = m_Table.getPO(m_activity.getRecord_ID(), get_TrxName());
			if(m_PO == null)//JPIERE-0607 Cancel WF. In case of Delete Doc.
			{
				//The workflow ends because the target document cannot be found.
				msg = Msg.getMsg(getCtx(), "JP_CancelWF_NotFound");
				MWFProcess wfpr = new MWFProcess(getCtx(), m_activity.getAD_WF_Process_ID(), get_TrxName());
				wfpr.setWFState(MWFProcess.WFSTATE_Terminated);
				wfpr.setTextMsg(msg);
				wfpr.save(get_TrxName());
				continue;
			}
			
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
			
			node = m_activity.getNode();			
			MWFProcess wfpr = null;
			if (MWFNode.ACTION_UserChoice.equals(node.getAction()))
			{
				
				String  JP_CancelWFAction = m_PO.get_ValueAsString(COLUMNNAME_JP_CancelWFAction);//JPIERE-0607
				if(Util.isEmpty(JP_CancelWFAction))//Standard Work flow
				{
					try
					{
						m_activity.setEndWaitTime(Timestamp.valueOf(LocalDateTime.now()));
						m_activity.setUserChoice(Env.getAD_User_ID(getCtx()), p_JP_IsApproval, DisplayType.YesNo, p_Comments);
						if(!p_JP_IsApproval.equals("Y"))//Not Approved
						{
							wfpr = new MWFProcess(getCtx(), m_activity.getAD_WF_Process_ID(), get_TrxName());
							wfpr.setWFState(MWFProcess.WFSTATE_Aborted);
							wfpr.save(get_TrxName());
	
						}else if(m_PO instanceof DocAction) {//Approved
	
							m_PO.load(get_TrxName());
							String docStatus = ((DocAction)m_PO).getDocStatus();
							if(DocAction.STATUS_Completed.equals(docStatus))
							{
								wfpr = new MWFProcess(getCtx(), m_activity.getAD_WF_Process_ID(), get_TrxName());
								wfpr.setWFState(MWFProcess.WFSTATE_Completed);
								wfpr.saveEx(get_TrxName());
							}
						}
	
					}catch (Exception e) {
	
						log.log(Level.SEVERE, node.getName(), e);
						throw e;
					}
					
				}else {//JPIERE-0607: Cancel WF
					
					m_activity.setEndWaitTime(Timestamp.valueOf(LocalDateTime.now()));
					m_activity.setTextMsg(p_Comments);
					if(!p_JP_IsApproval.equals("Y"))//JPIERE-0607: Cancel WF - Not Approved
					{
						wfpr = new MWFProcess(getCtx(), m_activity.getAD_WF_Process_ID(), get_TrxName());
						wfpr.setWFState(MWFProcess.WFSTATE_Aborted);
						wfpr.save(get_TrxName());
						
						m_PO.set_ValueNoCheck(COLUMNNAME_JP_CancelWFStatus, DocAction.STATUS_NotApproved);
						m_PO.set_ValueNoCheck(COLUMNNAME_JP_CancelWFAction, null);
						m_PO.saveEx(get_TrxName());
						
						//Send Mail or Note - Ref: MWFActivity#setUserChoice()
						if(m_PO instanceof DocAction)
						{
							DocAction doc = (DocAction)m_PO;
						
							MUser to = new MUser(getCtx(), doc.getDoc_User_ID(), null);

							// send EMail
							if (to.isNotificationEMail()) 
							{
								MClient client = MClient.get(getCtx(), doc.getAD_Client_ID());
								client.sendEMail(doc.getDoc_User_ID(), Msg.getMsg(getCtx(), "NotApproved")
										+ ": " + doc.getDocumentNo(),
										(doc.getSummary() != null ? doc.getSummary() + "\n" : "" )
										+ (doc.getProcessMsg() != null ? doc.getProcessMsg() + "\n" : "")
										+ (m_activity.getTextMsg() != null ? m_activity.getTextMsg() : ""), null);
							}
	
							// Send Note
							if (to.isNotificationNote()) 
							{
								MNote note = new MNote(getCtx(), "NotApproved", doc.getDoc_User_ID(), null);
								note.setTextMsg((doc.getSummary() != null ? doc.getSummary() + "\n" : "" )
										+ (doc.getProcessMsg() != null ? doc.getProcessMsg() + "\n" : "")
										+ (m_activity.getTextMsg() != null ? m_activity.getTextMsg() : ""));
								note.setRecord(m_PO.get_Table_ID(), m_PO.get_ID());
								note.saveEx();
							}
						}
						
						m_activity.saveEx(get_TrxName());
						
					}else if(m_PO instanceof DocAction) {//JPIERE-0607: Cancel WF - Approved

						DocAction doc = (DocAction)m_PO;
						wfpr = new MWFProcess(getCtx(), m_activity.getAD_WF_Process_ID(), get_TrxName());
						
						boolean isOwnDoc = false;//Check Self-Approval
						if(m_activity.isInvoker())
						{
							//JPIERE-0485 - Start
							MWFResponsible resp = m_activity.getResponsible();
							if(resp != null && resp.getResponsibleType().equals(MWFResponsible.RESPONSIBLETYPE_Organization))
							{								
								if(Env.getAD_User_ID(getCtx()) == wfpr.getCreatedBy()
										|| Env.getAD_User_ID(getCtx()) == doc.getDoc_User_ID()) //self approval
								{
									if(!MRole.getDefault().isCanApproveOwnDoc())
									{
										isOwnDoc = true;
									}

								}
								
								if(!isOwnDoc)
								{
									m_activity.setWFState(MWFActivity.WFSTATE_Completed);
								}else {
									throw new Exception(Msg.getMsg(getCtx(), "JP_NotSelfApproveRole"));//Your role can not self-approve.
								}

							}else {//JPIERE-0485

								int startAD_User_ID = Env.getAD_User_ID(getCtx());
								if (startAD_User_ID == 0)
									startAD_User_ID = doc.getDoc_User_ID();
								int nextAD_User_ID = m_activity.getApprovalUser(startAD_User_ID,
									doc.getC_Currency_ID(), doc.getApprovalAmt(),
									doc.getAD_Org_ID(),
									(startAD_User_ID == doc.getDoc_User_ID()
										|| startAD_User_ID == wfpr.getCreatedBy()) //JPIER-0551
									);	//	own doc
								//	No Approver
								if (nextAD_User_ID <= 0)
								{									
									wfpr.setWFState(MWFProcess.WFSTATE_Aborted);
									wfpr.setTextMsg(Msg.getMsg(getCtx(), "NoApprover"));
									wfpr.save(get_TrxName());
									
									m_PO.set_ValueNoCheck(COLUMNNAME_JP_CancelWFStatus, DocAction.STATUS_NotApproved);
									m_PO.set_ValueNoCheck(COLUMNNAME_JP_CancelWFAction, null);
									m_PO.saveEx(get_TrxName());
								}
								else if (startAD_User_ID != nextAD_User_ID)
								{
									m_activity.forwardTo(nextAD_User_ID, "Next Approver");
								}
								else	//	Approve
								{
									m_activity.setWFState(MWFActivity.WFSTATE_Completed);
								}

							}//JPIERE-0485 - End
							
						}else {//JPIERE-0487 & JPIERE-0488 - Start
							
							MWFResponsible resp = m_activity.getResponsible();

							if(resp.isHuman())//JPIERE-0488
							{
								if(Env.getAD_User_ID(getCtx()) == wfpr.getCreatedBy()
										|| Env.getAD_User_ID(getCtx()) == doc.getDoc_User_ID()) //self approval
								{
									if(!MRole.getDefault().isCanApproveOwnDoc())
									{
										isOwnDoc = true;
									}

								}

							}else if(resp.isRole()) {//JPIERE-0487
								
								MUserRoles[] urs = MUserRoles.getOfRole(getCtx(), resp.getAD_Role_ID());
								for(int i = 0; i < urs.length; i++)
								{
									if(Env.getAD_User_ID(getCtx()) == wfpr.getCreatedBy()
											|| Env.getAD_User_ID(getCtx()) == doc.getDoc_User_ID()) //self approval
									{
										if(!MRole.getDefault().isCanApproveOwnDoc())
										{
											isOwnDoc = true;
											break;
										}

									}
								}
							}

							if(!isOwnDoc)
							{
								m_activity.setWFState(MWFActivity.WFSTATE_Completed);
							}else {
								throw new Exception(Msg.getMsg(getCtx(), "JP_NotSelfApproveRole"));////Your role can not self-approve.
							}

						}//JPIERE-0487 & JPIERE-0488 - End
						
					}//JPIERE-0607: Cancel WF - Approved
					
				}//JPIERE-0607: Cancel WF 

			}else { //if (!MWFNode.ACTION_UserChoice.equals(node.getAction()))

				try
				{
					m_activity.setEndWaitTime(Timestamp.valueOf(LocalDateTime.now()));
					m_activity.setUserConfirmation(Env.getAD_User_ID(getCtx()), p_Comments);
					wfpr = new MWFProcess(getCtx(), m_activity.getAD_WF_Process_ID(), get_TrxName());
					wfpr.checkCloseActivities(get_TrxName());

				}catch (Exception e){

					log.log(Level.SEVERE, node.getName(), e);
					throw e;
				}
			}

			if(wfpr == null)
				wfpr = new MWFProcess(getCtx(), m_activity.getAD_WF_Process_ID(), get_TrxName());
			
			MWFActivity[] activities = wfpr.getActivities (true, false, get_TrxName());
			for (int i = 0; i < activities.length; i++)
			{
				if(MWFActivity.WFSTATE_Terminated.equals(activities[i].getWFState()))
				{
					if(activities[i].getUpdated().compareTo(starTime) > 0)
					{
						throw new Exception(msg + " - [ " + 
									Msg.getElement(getCtx(), MWFActivity.COLUMNNAME_AD_WF_Node_ID)+ " : " + activities[i].getNode().getName()
									+ " - " + MRefList.getListName(getCtx(), 305, MWFActivity.WFSTATE_Terminated)
									+ " ] - " + wfpr.getTextMsg());
					}
				}
			}//for i
			
			addBufferLog(0, null, null, msg, m_activity.getAD_Table_ID(), m_activity.getRecord_ID());

		}//for

		return null;
	}

}
