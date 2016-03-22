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
import java.util.logging.Level;

import jpiere.base.plugin.org.adempiere.model.MInvValCal;
import jpiere.base.plugin.org.adempiere.model.MInvValProfile;

import org.adempiere.util.ProcessUtil;
import org.compiere.process.ProcessInfo;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Trx;

/**
 * JPIERE-0161 Inventory Valuation Calculate Doc
 *
 *
 *  @author Hideaki Hagiwara
 *
 */
public class CallInvValCalClass extends SvrProcess {

	MInvValProfile m_InvValProfile = null;
	MInvValCal m_InvValCal = null;
	int Record_ID = 0;

	@Override
	protected void prepare()
	{
		Record_ID = getRecord_ID();
		if(Record_ID > 0)
		{
			m_InvValCal = new MInvValCal(getCtx(), Record_ID, null);
			m_InvValProfile = new MInvValProfile(getCtx(), m_InvValCal.getJP_InvValProfile_ID(), null);
		}else{
			log.log(Level.SEVERE, "Record_ID <= 0 ");
		}
	}

	@Override
	protected String doIt() throws Exception
	{
		ProcessInfo pi = new ProcessInfo("Title", 0, getTable_ID(), Record_ID);
		pi.setClassName(m_InvValProfile.getJP_InvValCalClass());
		pi.setAD_Client_ID(getAD_PInstance_ID());
		pi.setAD_User_ID(getAD_User_ID());
		pi.setAD_PInstance_ID(getAD_PInstance_ID());
		boolean isOK = ProcessUtil.startJavaProcess(getCtx(), pi, Trx.get(get_TrxName(), true), false, Env.getProcessUI(getCtx()));

		m_InvValCal.setJP_Processing1("Y");
		m_InvValCal.setJP_ProcessedTime1(new Timestamp(System.currentTimeMillis()));
		m_InvValCal.saveEx(get_TrxName());

		if(isOK)
			return Msg.getMsg(getCtx(), "ProcessOK");
		else
			return Msg.getMsg(getCtx(), "ProcessFailed");
	}

}
