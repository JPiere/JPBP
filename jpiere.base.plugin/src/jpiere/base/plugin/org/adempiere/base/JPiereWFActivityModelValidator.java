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

import org.compiere.model.MClient;
import org.compiere.model.MSysConfig;
import org.compiere.model.MUser;
import org.compiere.model.MWFActivityApprover;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.Msg;
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
		//JPIERE-0519 Auto Forward
		if(type == ModelValidator.TYPE_BEFORE_NEW || type == ModelValidator.TYPE_BEFORE_CHANGE)
		{
			MWFActivity wfa = (MWFActivity)po;
			MWFNode node = MWFNode.get(wfa.getAD_WF_Node_ID());
			if( MWFNode.ACTION_UserChoice.equals(node.getAction())
					|| MWFNode.ACTION_UserWindow.equals(node.getAction()) )
			{
				if(wfa.getAD_User_ID() != 0
						&& (type == ModelValidator.TYPE_BEFORE_NEW || wfa.is_ValueChanged("AD_User_ID"))
						&& !MWFResponsible.RESPONSIBLETYPE_Role.equals(MWFResponsible.get(node.getAD_WF_Responsible_ID()).getResponsibleType()) )
				{
					MWFAutoForward autoForward = MWFAutoForward.get(wfa);
					if(autoForward != null)
					{
						if(MSysConfig.getBooleanValue("JP_WF_AUTO_FORWARD_LOG", false, wfa.getAD_Client_ID(), wfa.getAD_Org_ID()))
						{
							MWFEventAudit eventLog = new MWFEventAudit(wfa);
							eventLog.setEventType(MWFEventAudit.EVENTTYPE_StateChanged);
							eventLog.setWFState(MWFEventAudit.WFSTATE_Suspended);
							eventLog.setAttributeName(Msg.getElement(po.getCtx(), MWFAutoForward.COLUMNNAME_JP_WF_AutoForward_ID));
							eventLog.setOldValue(MUser.get(wfa.getAD_User_ID()).getName());
							eventLog.setNewValue(MUser.get(autoForward.getJP_WF_User_To_ID()).getName());
							eventLog.saveEx(po.get_TrxName());
						}

						wfa.setAD_User_ID(autoForward.getJP_WF_User_To_ID());
					}
				}
			}
		}//JPIERE-0519

		//JPIERE-0518 Auto Add Approvers
		if(type == ModelValidator.TYPE_AFTER_NEW)
		{
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
					for(MWFAutoAddUser user : users)
					{
						wfApprover = new MWFActivityApprover(po.getCtx(), 0, po.get_TrxName());
						wfApprover.setAD_Org_ID(wfa.getAD_Org_ID());
						wfApprover.setAD_WF_Activity_ID(wfa.getAD_WF_Activity_ID());

						autoForward = MWFAutoForward.get(wfa.getAD_Client_ID(), user.getAD_User_ID(), wfa.getAD_WF_Node_ID(), wfa.getAD_Org_ID(), wfa.getCreated(), wfa.get_TrxName());
						if(autoForward != null)
						{
							if(MSysConfig.getBooleanValue("JP_WF_AUTO_FORWARD_LOG", false, wfa.getAD_Client_ID(), wfa.getAD_Org_ID()))
							{
								MWFEventAudit eventLog = new MWFEventAudit(wfa);
								eventLog.setEventType(MWFEventAudit.EVENTTYPE_StateChanged);
								eventLog.setWFState(MWFEventAudit.WFSTATE_Suspended);
								eventLog.setAttributeName(Msg.getElement(po.getCtx(), MWFAutoForward.COLUMNNAME_JP_WF_AutoForward_ID));
								eventLog.setOldValue(MUser.get(user.getAD_User_ID()).getName());
								eventLog.setNewValue(MUser.get(autoForward.getJP_WF_User_To_ID()).getName());
								eventLog.saveEx(po.get_TrxName());
							}

							wfApprover.setAD_User_ID(autoForward.getJP_WF_User_To_ID());
						}else {
							wfApprover.setAD_User_ID(user.getAD_User_ID());
						}

						wfApprover.saveEx(po.get_TrxName());
					}
				}
			}
		}//JPIERE-0518

		return null;
	}

	@Override
	public String docValidate(PO po, int timing)
	{
		return null;
	}

}
