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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.logging.Level;

import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Msg;

/**
 * JPIERE-0547: Access Update of Role Organization.
 * 
 * JP_AccessUpdateRoleOrganization
 * 
 * 
 * @author h.hagiwara
 *
 */
public class AccessUpdateRoleOrg extends SvrProcess {

	private int p_AD_Org_ID = 0;
	private Timestamp today = null;
	private int activeNum = 0;
	private int inActiveNum = 0;
	
	@Override
	protected void prepare() 
	{
		today = Timestamp.valueOf(LocalDateTime.of(LocalDate.now(), LocalTime.MIN));
		
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
			else if (name.equals("AD_Org_ID"))
				p_AD_Org_ID = para[i].getParameterAsInt();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
	}

	@Override
	protected String doIt() throws Exception 
	{
		doActive();
		doInActive();
		
		addLog(Msg.getMsg(getCtx(), "JP_UpdateActive") + " : "+ activeNum);
		addLog(Msg.getMsg(getCtx(), "JP_UpdateInActive") + " : " + inActiveNum);
		
		return "@OK@";
	}

	
	private void doActive()
	{
		StringBuilder sql = new StringBuilder("Update AD_Role_OrgAccess")
				.append(" SET IsActive = 'Y' ")
				.append(" WHERE (JP_ValidFrom IS NULL OR  JP_ValidFrom <= ?) ")
				.append(" AND (JP_ValidTo IS NULL OR  JP_ValidTo >= ?) ")
				.append(" AND AD_Client_ID = ? AND IsActive = 'N' ");
		;
		
		if(p_AD_Org_ID == 0)
		{
			activeNum = DB.executeUpdate(sql.toString(), new Object[] {today,today,getAD_Client_ID()},false, get_TrxName(),0);
		}else {
			sql = sql.append(" AND AD_Org_ID = ?"); 
			activeNum = DB.executeUpdate(sql.toString(), new Object[] {today,today,getAD_Client_ID(),p_AD_Org_ID},false, get_TrxName(),0);
		}
	}
	
	private void doInActive()
	{
		StringBuilder sql = new StringBuilder("Update AD_Role_OrgAccess")
				.append(" SET IsActive = 'N' ")
				.append(" WHERE ( JP_ValidFrom > ? OR JP_ValidTo < ? ) ")
				.append(" AND AD_Client_ID = ? AND IsActive = 'Y' ")
		;
		
		if(p_AD_Org_ID == 0)
		{
			inActiveNum = DB.executeUpdate(sql.toString(), new Object[] {today,today,getAD_Client_ID()},false, get_TrxName(),0);
		}else {
			sql = sql.append(" AND AD_Org_ID = ?"); 
			inActiveNum = DB.executeUpdate(sql.toString(), new Object[] {today,today,getAD_Client_ID(),p_AD_Org_ID},false, get_TrxName(),0);
		}
		
		
	}
}
