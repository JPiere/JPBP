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

package jpiere.base.plugin.webui.action.attachment;

import org.adempiere.webui.action.IAction;
import org.adempiere.webui.adwindow.ADWindow;
import org.adempiere.webui.window.FDialog;
import org.compiere.model.MClientInfo;
import org.compiere.util.Env;
import org.compiere.util.Msg;

/**
*
* JPIERE-0436: JPiere Attachment File
*
*
* @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
*
*/
public class JPiereAttachment implements IAction {

	@Override
	public void execute(Object target)
	{
		 if(MClientInfo.get(Env.getCtx()).getAD_StorageProvider_ID() == 0)
		 {
			 FDialog.error(0, "Error", Msg.getMsg(Env.getCtx(), "NotFound")
					 + System.lineSeparator() + Msg.getElement(Env.getCtx(), "AD_StorageProvider_ID"));
			 return ;
		 }

		new JPiereAttchmentBaseWindow((ADWindow)target);
	}


}
