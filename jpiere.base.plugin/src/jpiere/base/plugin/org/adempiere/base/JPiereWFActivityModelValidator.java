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
import org.compiere.model.MWFActivityApprover;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.wf.MWFActivity;
import org.compiere.wf.MWFNode;

import jpiere.base.plugin.org.adempiere.model.MWFAutoAddApprovers;
import jpiere.base.plugin.org.adempiere.model.MWFAutoAddUser;


/**
 * JPIERE-0518 WF Auto Add Approvers
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
					for(MWFAutoAddUser user : users)
					{
						MWFActivityApprover wfApprover = new MWFActivityApprover(po.getCtx(), 0, po.get_TrxName());
						wfApprover.setAD_Org_ID(wfa.getAD_Org_ID());
						wfApprover.setAD_WF_Activity_ID(wfa.getAD_WF_Activity_ID());
						wfApprover.setAD_User_ID(user.getAD_User_ID());
						wfApprover.saveEx(po.get_TrxName());
					}
				}
			}
		}

		return null;
	}

	@Override
	public String docValidate(PO po, int timing)
	{
		return null;
	}

}
