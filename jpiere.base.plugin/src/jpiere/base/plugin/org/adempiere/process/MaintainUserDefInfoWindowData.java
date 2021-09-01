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
import org.compiere.model.MRole;
import org.compiere.model.MTable;
import org.compiere.model.MUser;
import org.compiere.model.MUserDefInfo;
import org.compiere.model.MUserDefInfoColumn;
import org.compiere.model.Query;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
import org.compiere.util.Msg;

/**
 *
 * JPIERE-0498  Maintain User Definition Info Window Data
 *
 *
 * @author h.hagiwara
 *
 */
public class MaintainUserDefInfoWindowData extends SvrProcess {

	private int p_AD_Org_ID = 0;
	private int p_Record_ID = 0;

	@Override
	protected void prepare()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null){
				;
			}else{
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}//if

		}//for

		p_Record_ID = getRecord_ID();
	}

	private int success = 0;
	private int successColumn = 0;
	private int noMaintenance = 0;

	@Override
	protected String doIt() throws Exception
	{
		String whereClause = "EXISTS (SELECT T_Selection_ID FROM T_Selection WHERE T_Selection.AD_PInstance_ID=? " +
				"AND T_Selection.T_Selection_ID = AD_InfoWindow.AD_InfoWindow_ID)";

		List<MInfoWindow> selectedInfoWindows = new Query(getCtx(), MInfoWindow.Table_Name, whereClause, get_TrxName())
										.setParameters(new Object[]{getAD_PInstance_ID()})
										.list();

		String msg = null;
		if(p_Record_ID != 0)
		{
			MUserDefInfo createdUserInfoWindow = new MUserDefInfo(getCtx(), p_Record_ID, get_TrxName());
			MInfoWindow infoWindow = new MInfoWindow(getCtx(), createdUserInfoWindow.getAD_InfoWindow_ID(), get_TrxName());

			try
			{
				if(maintainInfoWindow(createdUserInfoWindow,  infoWindow))
				{
					msg = Msg.getElement(getCtx(), "AD_UserDef_Info_Column_ID") + " " +Msg.getMsg(getCtx(), "Register") + " : " + successColumn;
				}else {
					msg =Msg.getMsg(getCtx(), "JP_NoMaintenanceRequired") ;
				}

			}catch (Exception e) {
				throw e;
			}finally {
				;
			}

			return msg;

		}else {

			for(MInfoWindow selectedInfoWindow : selectedInfoWindows )
			{
				whereClause = " AD_InfoWindow_ID = ? AND AD_Language = ? AND AD_User_ID IS NOT NULL";

				List<MUserDefInfo>	createdUserInfoWindows = new Query(getCtx(), MUserDefInfo.Table_Name, whereClause, get_TrxName())
						.setClient_ID()
						.setParameters(new Object[]{selectedInfoWindow.getAD_InfoWindow_ID(), Env.getAD_Language(getCtx())})
						.list();

				for(MUserDefInfo createdUserInfoWindow : createdUserInfoWindows)
				{
					try
					{
						if(maintainInfoWindow(createdUserInfoWindow, selectedInfoWindow ))
						{
							success++;
							if(createdUserInfoWindow.getAD_Role_ID()==0)
							{
								msg =createdUserInfoWindow.getName() +" - "+ MUser.get(createdUserInfoWindow.getAD_User_ID()).getName();
							}else {
								msg =createdUserInfoWindow.getName() +" - "+ MUser.get(createdUserInfoWindow.getAD_User_ID()).getName() +" - "+  MRole.get(getCtx(), createdUserInfoWindow.getAD_Role_ID()).getName();
							}
							addBufferLog(0, null, null, msg , MUserDefInfo.Table_ID, createdUserInfoWindow.getAD_UserDef_Info_ID());
						}else {

							noMaintenance++;

						}

					}catch (Exception e) {
						throw e;
					}finally {
						;
					}
				}

			}//for
		}

		if(success > 0)
		{
			return Msg.getElement(getCtx(), "AD_UserDef_Info_ID") + " - " + Msg.getMsg(getCtx(), "JP_Success") + " = " + success
					+ " ( " + Msg.getElement(getCtx(), "AD_UserDef_Info_Column_ID") + " " +Msg.getMsg(getCtx(), "Register") + " = " + successColumn	+ " ) "
					+ " : " + Msg.getMsg(getCtx(), "JP_NoMaintenanceRequired") + " = " +  noMaintenance ;
		}else {
			return Msg.getMsg(getCtx(), "JP_NoMaintenanceRequired");//No maintenance required.
		}
	}

	private boolean maintainInfoWindow(MUserDefInfo createdUserDefInfoWindow, MInfoWindow infoWindow ) throws Exception
	{
		MInfoColumn[] infoColumns = infoWindow.getInfoColumns();
		MUserDefInfoColumn[] uDefCols = getColumns(createdUserDefInfoWindow);

		for(MInfoColumn infoColumn: infoColumns)
		{
			boolean isOK = false;
			for(MUserDefInfoColumn uDefCol: uDefCols)
			{
				if(uDefCol.getAD_InfoColumn_ID() == infoColumn.getAD_InfoColumn_ID())
				{
					isOK = true;
					break;
				}
			}

			if(!isOK)
			{
				MUserDefInfoColumn newUserInfoColumn =	new MUserDefInfoColumn(getCtx(), 0, get_TrxName());
				newUserInfoColumn.setAD_Org_ID(p_AD_Org_ID);
				newUserInfoColumn.setAD_UserDef_Info_ID(createdUserDefInfoWindow.getAD_UserDef_Info_ID());
				newUserInfoColumn.setAD_InfoColumn_ID(infoColumn.getAD_InfoColumn_ID());
				newUserInfoColumn.setName(infoColumn.get_Translation(MUserDefInfoColumn.COLUMNNAME_Name));
				newUserInfoColumn.setSeqNo(0);
				newUserInfoColumn.setSeqNoSelection(0);
				newUserInfoColumn.setIsDisplayed("N");
				newUserInfoColumn.setIsQueryCriteria("N");
				newUserInfoColumn.saveEx(get_TrxName());
				successColumn++;
			}//if

		}//for

		if(successColumn > 0)
		{
			return true;
		}else {
			return false;
		}

	}

	private MUserDefInfoColumn[] getColumns(MUserDefInfo userDefInfoWindow)
	{
		Query query = new Query(getCtx(), MTable.get(getCtx(), MUserDefInfoColumn.Table_ID), MUserDefInfoColumn.COLUMNNAME_AD_UserDef_Info_ID+"=?", get_TrxName());
		List<MUserDefInfoColumn> list = query.setParameters(userDefInfoWindow.getAD_UserDef_Info_ID())
				.setOnlyActiveRecords(true)
				.setOrderBy("SeqNo, AD_InfoColumn_ID")
				.list();

		MUserDefInfoColumn[] uDefInfocolumns = list.toArray(new MUserDefInfoColumn[0]);

		return uDefInfocolumns;
	}

}
