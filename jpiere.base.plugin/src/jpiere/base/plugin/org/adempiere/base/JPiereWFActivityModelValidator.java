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
package jpiere.base.plugin.org.adempiere.base;

import java.io.File;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Properties;

import org.compiere.model.MClient;
import org.compiere.model.MMailText;
import org.compiere.model.MNote;
import org.compiere.model.MSysConfig;
import org.compiere.model.MTable;
import org.compiere.model.MUser;
import org.compiere.model.MUserRoles;
import org.compiere.model.MWFActivityApprover;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.process.DocAction;
import org.compiere.util.CLogger;
import org.compiere.util.Msg;
import org.compiere.util.Util;
import org.compiere.wf.MWFActivity;
import org.compiere.wf.MWFEventAudit;
import org.compiere.wf.MWFNode;
import org.compiere.wf.MWFResponsible;

import jpiere.base.plugin.org.adempiere.model.MWFAutoAddApprovers;
import jpiere.base.plugin.org.adempiere.model.MWFAutoAddUser;
import jpiere.base.plugin.org.adempiere.model.MWFAutoForward;


/**
 * JPIERE-0518 WF Auto Add Approvers
 * JPIERE-0519 WF Auto Forward
 * JPIERE-0538 Send approval request notification
 *
 * @author Hideaki Hagiwara
 *
 */
public class JPiereWFActivityModelValidator implements ModelValidator {

	@SuppressWarnings("unused")
	private static CLogger log = CLogger.getCLogger(JPiereWFActivityModelValidator.class);
	private int AD_Client_ID = -1;

	@Override
	public void initialize(ModelValidationEngine engine, MClient client)
	{
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();
		engine.addModelChange(MWFActivity.Table_Name, this);

	}

	@Override
	public int getAD_Client_ID()
	{
		return AD_Client_ID;
	}

	@Override
	public String login(int AD_Org_ID, int AD_Role_ID, int AD_User_ID)
	{
		return null;
	}

	@Override
	public String modelChange(PO po, int type) throws Exception
	{
		//JPIERE-0519 & JPIERE-0538
		if(type == ModelValidator.TYPE_BEFORE_NEW || type == ModelValidator.TYPE_BEFORE_CHANGE)
		{
			MWFActivity wfa = (MWFActivity)po;
			MWFNode node = MWFNode.get(wfa.getAD_WF_Node_ID());
			if( MWFNode.ACTION_UserChoice.equals(node.getAction())
					|| MWFNode.ACTION_UserWindow.equals(node.getAction()) )
			{
				//JPIERE-0519 Auto Forward
				if(wfa.getAD_User_ID() != 0
						&& !wfa.isProcessed()
						&& MWFActivity.WFSTATE_Suspended.equals(wfa.getWFState()) )
				{
					MWFAutoForward autoForward = null;

					if(type == ModelValidator.TYPE_BEFORE_NEW)
					{
						autoForward = MWFAutoForward.get(wfa);

					}else if(type == ModelValidator.TYPE_BEFORE_CHANGE){

						LocalDateTime localDateTime = LocalDateTime.now();
						autoForward = MWFAutoForward.get(wfa.getAD_Client_ID(), wfa.getAD_User_ID(), wfa.getAD_WF_Node_ID(), wfa.getAD_Org_ID(), Timestamp.valueOf(localDateTime), wfa.get_TrxName());
					}

					if(autoForward != null)
					{
						if(MSysConfig.getBooleanValue("JP_WF_AUTO_FORWARD_LOG", true, wfa.getAD_Client_ID(), wfa.getAD_Org_ID()))
						{
							MWFEventAudit eventLog = new MWFEventAudit(wfa);
							eventLog.setEventType(MWFEventAudit.EVENTTYPE_StateChanged);
							eventLog.setWFState(MWFEventAudit.WFSTATE_Suspended);
							eventLog.setAttributeName("Auto Forward - AD_WF_Activity");
							eventLog.setOldValue(MUser.getNameOfUser(wfa.getAD_User_ID()));
							eventLog.setNewValue(MUser.getNameOfUser(autoForward.getJP_WF_User_To_ID()));
							eventLog.setDescription(Msg.getElement(wfa.getCtx(), MWFAutoForward.COLUMNNAME_JP_WF_AutoForward_ID));
							eventLog.saveEx(po.get_TrxName());
						}

						wfa.setAD_User_ID(autoForward.getJP_WF_User_To_ID());
					}

					//JPIERE-0538 : Send approval request notification to user
					if( wfa.get_ColumnIndex(IS_PROCESSED_APPROVAL_REQUEST) >= 0 && !wfa.get_ValueAsBoolean(IS_PROCESSED_APPROVAL_REQUEST))
					{
						sendApprovalRequestNotification(po.getCtx(), wfa, wfa.getAD_User_ID(), po.get_TrxName());
						wfa.set_ValueNoCheck(IS_PROCESSED_APPROVAL_REQUEST, "Y");
					}
				}
			}

			//JPIERE-0538 : Send approval request notification to user belong to role
			if(MWFResponsible.RESPONSIBLETYPE_Role.equals(MWFResponsible.get(node.getAD_WF_Responsible_ID()).getResponsibleType())
					&& !wfa.isProcessed()
					&& MWFActivity.WFSTATE_Suspended.equals(wfa.getWFState()) )
			{
				if( wfa.get_ColumnIndex(IS_PROCESSED_APPROVAL_REQUEST) >= 0 && !wfa.get_ValueAsBoolean(IS_PROCESSED_APPROVAL_REQUEST))
				{
					MWFResponsible wfr = MWFResponsible.get(node.getAD_WF_Responsible_ID());
					MUserRoles[] userRoles = MUserRoles.getOfRole(po.getCtx(), wfr.getAD_Role_ID());
					for(MUserRoles userRole : userRoles)
					{
						sendApprovalRequestNotification(po.getCtx(), wfa, userRole.getAD_User_ID(), po.get_TrxName());
					}
					wfa.set_ValueNoCheck(IS_PROCESSED_APPROVAL_REQUEST, "Y");
				}
			}

		}//JPIERE-0519 & JPIERE-0538



		//JPIERE-0518 & JPIERE-0538
		if(type == ModelValidator.TYPE_AFTER_NEW)
		{
			//JPIERE-0518 Auto Add Approvers
			MWFActivity wfa = (MWFActivity)po;
			MWFNode node = MWFNode.get(wfa.getAD_WF_Node_ID());
			if( MWFNode.ACTION_UserChoice.equals(node.getAction())
					|| MWFNode.ACTION_UserWindow.equals(node.getAction()) )
			{
				MWFAutoAddApprovers approvers = MWFAutoAddApprovers.get(wfa);
				if(approvers != null)
				{
					MWFAutoAddUser[] users = approvers.getAutoAddUsers(false);
					MWFActivityApprover wfApprover = null;
					MWFAutoForward autoForward = null;
					ArrayList<Integer> list_AD_User_ID = new ArrayList<Integer>();
					for(MWFAutoAddUser user : users)
					{
						wfApprover = new MWFActivityApprover(po.getCtx(), 0, po.get_TrxName());
						wfApprover.setAD_Org_ID(wfa.getAD_Org_ID());
						wfApprover.setAD_WF_Activity_ID(wfa.getAD_WF_Activity_ID());

						autoForward = MWFAutoForward.get(wfa.getAD_Client_ID(), user.getAD_User_ID(), wfa.getAD_WF_Node_ID(), wfa.getAD_Org_ID(), wfa.getCreated(), wfa.get_TrxName());

						if(autoForward != null)
						{
							//Check unique user
							boolean isSkeip = false;
							for(Integer AD_User_ID : list_AD_User_ID)
							{
								if(autoForward.getJP_WF_User_To_ID() == AD_User_ID.intValue())
								{
									isSkeip = true;
									if(MSysConfig.getBooleanValue("JP_WF_AUTO_FORWARD_LOG", true, wfa.getAD_Client_ID(), wfa.getAD_Org_ID()))
									{
										MWFEventAudit eventLog = new MWFEventAudit(wfa);
										eventLog.setEventType(MWFEventAudit.EVENTTYPE_StateChanged);
										eventLog.setWFState(MWFEventAudit.WFSTATE_Suspended);
										eventLog.setAttributeName("Auto Forward - AD_WF_ActivityApprover - Skip - Duplicate");
										eventLog.setOldValue(MUser.getNameOfUser(user.getAD_User_ID()));
										eventLog.setNewValue(MUser.getNameOfUser(autoForward.getJP_WF_User_To_ID()));
										eventLog.setDescription(Msg.getElement(wfa.getCtx(), MWFAutoForward.COLUMNNAME_JP_WF_AutoForward_ID));
										eventLog.saveEx(po.get_TrxName());
									}
									break;
								}
							}

							if(!isSkeip)
							{
								if(MSysConfig.getBooleanValue("JP_WF_AUTO_FORWARD_LOG", true, wfa.getAD_Client_ID(), wfa.getAD_Org_ID()))
								{
									MWFEventAudit eventLog = new MWFEventAudit(wfa);
									eventLog.setEventType(MWFEventAudit.EVENTTYPE_StateChanged);
									eventLog.setWFState(MWFEventAudit.WFSTATE_Suspended);
									eventLog.setAttributeName("Auto Forward - AD_WF_ActivityApprover");
									eventLog.setOldValue(MUser.getNameOfUser(user.getAD_User_ID()));
									eventLog.setNewValue(MUser.getNameOfUser(autoForward.getJP_WF_User_To_ID()));
									eventLog.setDescription(Msg.getElement(wfa.getCtx(), MWFAutoForward.COLUMNNAME_JP_WF_AutoForward_ID));
									eventLog.saveEx(po.get_TrxName());
								}

								wfApprover.setAD_User_ID(autoForward.getJP_WF_User_To_ID());

								//JPIERE-0538 :  Send approval request notification to additional approval user.
								if( wfApprover.get_ColumnIndex(IS_PROCESSED_APPROVAL_REQUEST) >= 0 && !wfApprover.get_ValueAsBoolean(IS_PROCESSED_APPROVAL_REQUEST))
								{
									sendAdditinalApprovalRequestNotification(po.getCtx(), wfa, wfApprover, po.get_TrxName());
									wfApprover.set_ValueNoCheck(IS_PROCESSED_APPROVAL_REQUEST, "Y");
								}
								wfApprover.saveEx(po.get_TrxName());
								list_AD_User_ID.add(wfApprover.getAD_User_ID());
							}

						}else {

							wfApprover.setAD_User_ID(user.getAD_User_ID());

							//JPIERE-0538 :  Send approval request notification to additional approval user.
							if( wfApprover.get_ColumnIndex(IS_PROCESSED_APPROVAL_REQUEST) >= 0 && !wfApprover.get_ValueAsBoolean(IS_PROCESSED_APPROVAL_REQUEST))
							{
								sendAdditinalApprovalRequestNotification(po.getCtx(), wfa, wfApprover, po.get_TrxName());
								wfApprover.set_ValueNoCheck(IS_PROCESSED_APPROVAL_REQUEST, "Y");
							}
							wfApprover.saveEx(po.get_TrxName());
							list_AD_User_ID.add(wfApprover.getAD_User_ID());
						}

					}//for(MWFAutoAddUser user : users)
				}
			}
		}//JPIERE-0518 & JPIERE-0538

		return null;
	}

	@Override
	public String docValidate(PO po, int timing)
	{
		return null;
	}

	public static final String IS_PROCESSED_APPROVAL_REQUEST = "IsProcessedApprovalRequestJP";

	/**
	 * Send Approval Request Notification
	 *
	 * @param ctx
	 * @param m_WFActivity Instance of MWFActivity
	 * @param AD_User_ID user who send approval request
	 * @param trxName
	 * @return
	 */
	static public boolean sendApprovalRequestNotification(Properties ctx, MWFActivity m_WFActivity, int AD_User_ID, String trxName)
	{
		if( m_WFActivity == null || AD_User_ID < 0)
		{
			return false;
		}

		MUser user = MUser.get(ctx, AD_User_ID);
		MWFNode m_node = MWFNode.get(m_WFActivity.getAD_WF_Node_ID());

		if(user.isNotificationEMail())
		{
			sendEMail(ctx, m_node, m_WFActivity, user, trxName);
		}

		if(user.isNotificationNote())
		{
			createNote(ctx, m_node, m_WFActivity, user, trxName);
		}

		return true;
	}

	/**
	 * Send Aditional Approval Request Notification
	 *
	 * @param ctx
	 * @param m_WFActivity
	 * @param approver
	 * @param trxName
	 * @return
	 */
	static public boolean sendAdditinalApprovalRequestNotification(Properties ctx, MWFActivity m_WFActivity, MWFActivityApprover approver, String trxName)
	{
		if( m_WFActivity == null || approver == null)
		{
			return false;
		}

		MUser user = MUser.get(ctx, approver.getAD_User_ID());
		MWFNode m_node = MWFNode.get(m_WFActivity.getAD_WF_Node_ID());

		if(user.isNotificationEMail())
		{
			sendEMail(ctx, m_node, m_WFActivity, user, trxName);
		}

		if(user.isNotificationNote())
		{
			createNote(ctx, m_node, m_WFActivity, user, trxName);
		}

		return true;
	}

	/**
	 * Send EMail
	 *
	 * @param ctx
	 * @param m_node
	 * @param m_WFActivity
	 * @param m_User
	 * @param trxName
	 */
	static private void sendEMail(Properties ctx, MWFNode m_node, MWFActivity m_WFActivity, MUser m_User, String trxName)
	{
		if(m_node == null || m_node.getR_MailText_ID() <= 0 || Util.isEmpty(m_User.getEMail()))
		{
			return ;
		}

		MClient client = MClient.get(ctx,  m_WFActivity.getAD_Client_ID());
		if(Util.isEmpty(client.getRequestEMail()) || (client.isSmtpAuthorization() && Util.isEmpty(client.getRequestUserPW())) )
		{
			return ;
		}

		MMailText text = new MMailText (ctx, m_node.getR_MailText_ID(), trxName);
		PO m_po = MTable.get(m_WFActivity.getAD_Table_ID()).getPO(m_WFActivity.getRecord_ID(), trxName);
		text.setPO(m_po, true);

		String subject = null;
		String message = null;
		File pdf = null;
		if(m_po instanceof DocAction)
		{
			DocAction  doc = (DocAction)m_po;

			subject = doc.getDocumentInfo()
					+ ": " + text.getMailHeader();

			message = text.getMailText(true);

			//I think that we need not to create attachment file at WF approval request.
			//If you need to create attachment file , Please uncomment below.
			//pdf = doc.createPDF();

		}else {

			subject = text.getMailHeader();
			message = text.getMailText(true);
		}

		client.sendEMail(null, m_User, subject, message, pdf, text.isHtml());

	}	//	sendEMail


	/**
	 * Create Note
	 *
	 * @param ctx
	 * @param m_node
	 * @param m_WFActivity
	 * @param m_User
	 * @param trxName
	 */
	static private void createNote(Properties ctx, MWFNode m_node, MWFActivity m_WFActivity, MUser m_User, String trxName)
	{
		if(m_node == null || m_node.getR_MailText_ID() <= 0)
		{
			return ;
		}

		MMailText text = new MMailText (ctx, m_node.getR_MailText_ID(), trxName);
		PO m_po = MTable.get(m_WFActivity.getAD_Table_ID()).getPO(m_WFActivity.getRecord_ID(), trxName);
		text.setPO(m_po, true);

		MNote note = new MNote(ctx, "JP_WFApprovalRequest", m_User.getAD_User_ID(), trxName);//WF Approval Request

		String subject = null;
		String message = null;

		if(m_po instanceof DocAction)
		{
			DocAction  doc = (DocAction)m_po;

			subject = doc.getDocumentInfo()
					+ ": " + text.getMailHeader();

			message = text.getMailText(true);

		}else {

			subject = text.getMailHeader();
			message = text.getMailText(true);

		}

		note.setAD_WF_Activity_ID(m_WFActivity.getAD_WF_Activity_ID());
		note.setReference(subject);
		note.setTextMsg(message);
		note.setRecord(m_WFActivity.getAD_Table_ID(), m_WFActivity.getRecord_ID());
		note.saveEx();
	}

}
