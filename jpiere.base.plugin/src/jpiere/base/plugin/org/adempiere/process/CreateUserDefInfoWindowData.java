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

import java.util.List;
import java.util.logging.Level;

import org.compiere.model.MInfoColumn;
import org.compiere.model.MInfoWindow;
import org.compiere.model.MUserDefInfo;
import org.compiere.model.MUserDefInfoColumn;
import org.compiere.model.Query;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;

/**
 *
 * JPIERE-0497  Create User Definition Info WindowData Data at Info Window
 *
 *
 * @author h.hagiwara
 *
 */
public class CreateUserDefInfoWindowData extends SvrProcess {

	private int p_AD_Org_ID = 0;
	private int p_AD_Role_ID = 0;

	@Override
	protected void prepare()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null){
				;
			}else if (name.equals("AD_Org_ID")){
				//p_AD_Org_ID = para[i].getParameterAsInt();
			}else if (name.equals("AD_Role_ID")){
				p_AD_Role_ID = para[i].getParameterAsInt();
			}else{
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}//if

		}//for
	}

	@Override
	protected String doIt() throws Exception
	{
		String whereClause = "EXISTS (SELECT T_Selection_ID FROM T_Selection WHERE T_Selection.AD_PInstance_ID=? " +
				"AND T_Selection.T_Selection_ID = AD_InfoWindow.AD_InfoWindow_ID)";

		List<MInfoWindow> selectedInfoWindows = new Query(getCtx(), MInfoWindow.Table_Name, whereClause, get_TrxName())
										.setParameters(new Object[]{getAD_PInstance_ID()})
										.list();


		int AD_User_ID = Env.getAD_User_ID(getCtx());
		String AD_Language = Env.getAD_Language(getCtx());

		List<MUserDefInfo> createdInfoWindows = null;
		if(p_AD_Role_ID == 0 )
		{

			whereClause = " AD_User_ID = ? AND AD_Role_ID IS NULL AND AD_Language = ?";

			createdInfoWindows = new Query(getCtx(), MUserDefInfo.Table_Name, whereClause, get_TrxName())
													.setClient_ID()
													.setParameters(new Object[]{AD_User_ID,AD_Language})
													.list();

		}else {

			whereClause = " AD_User_ID = ? AND AD_Role_ID = ? AND AD_Language = ?";

			createdInfoWindows = new Query(getCtx(), MUserDefInfo.Table_Name, whereClause, get_TrxName())
													.setClient_ID()
													.setParameters(new Object[]{AD_User_ID,p_AD_Role_ID,AD_Language})
													.list();
		}



		for(MInfoWindow selectedInfoWindow : selectedInfoWindows )
		{
			boolean isAlreadyCreated = false;
			for(MUserDefInfo createdInfoWindow : createdInfoWindows)
			{
				if(selectedInfoWindow.getAD_InfoWindow_ID() == createdInfoWindow.getAD_InfoWindow_ID())
				{
					isAlreadyCreated = true;
					addBufferLog(0, null, null, createdInfoWindow.getName(), MUserDefInfo.Table_ID, createdInfoWindow.getAD_UserDef_Info_ID());
					break;
				}
			}//for

			if(isAlreadyCreated)
				continue;

			MUserDefInfo uInfo = new MUserDefInfo(getCtx(), 0, get_TrxName());
			uInfo.setAD_Org_ID(p_AD_Org_ID);
			uInfo.setAD_InfoWindow_ID(selectedInfoWindow.getAD_InfoWindow_ID());
			uInfo.setName(selectedInfoWindow.get_Translation(MUserDefInfo.COLUMNNAME_Name));
			uInfo.setDescription(selectedInfoWindow.get_Translation(MUserDefInfo.COLUMNNAME_Description));
			uInfo.setHelp(selectedInfoWindow.get_Translation(MUserDefInfo.COLUMNNAME_Help));
			uInfo.setAD_User_ID(AD_User_ID);
			if(p_AD_Role_ID==0)
			{
				;
			}else {
				uInfo.setAD_Role_ID(p_AD_Role_ID);
			}
			uInfo.setAD_Language(AD_Language);
			uInfo.saveEx(get_TrxName());

			MInfoColumn[] infoColumns = selectedInfoWindow.getInfoColumns();
			for(MInfoColumn infoColumn : infoColumns)
			{
				MUserDefInfoColumn uInfoColumn =	new MUserDefInfoColumn(getCtx(), 0, get_TrxName());
				uInfoColumn.setAD_Org_ID(p_AD_Org_ID);
				uInfoColumn.setAD_UserDef_Info_ID(uInfo.getAD_UserDef_Info_ID());
				uInfoColumn.setAD_InfoColumn_ID(infoColumn.getAD_InfoColumn_ID());
				uInfoColumn.setName(infoColumn.get_Translation(MUserDefInfoColumn.COLUMNNAME_Name));
				uInfoColumn.setSeqNo(infoColumn.getSeqNo());
				uInfoColumn.setSeqNoSelection(infoColumn.getSeqNoSelection());
				uInfoColumn.setIsDisplayed(infoColumn.isDisplayed()?"Y":"N");
				uInfoColumn.setIsQueryCriteria(infoColumn.isQueryCriteria()?"Y":"N");
				uInfoColumn.saveEx(get_TrxName());
			}//for

			addBufferLog(0, null, null, uInfo.getName(), MUserDefInfo.Table_ID, uInfo.getAD_UserDef_Info_ID());

		}//For


		return "@OK@";
	}

}
