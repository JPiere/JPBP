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

import org.adempiere.exceptions.AdempiereException;
import org.adempiere.util.ProcessUtil;
import org.compiere.process.ProcessInfo;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
import org.compiere.util.Trx;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.MInvValAdjust;
import jpiere.base.plugin.org.adempiere.model.MInvValProfile;

/**
 * JPIERE-0161 Inventory Valuation Calculate Doc
 *
 *
 *  @author Hideaki Hagiwara
 *
 */
public class CallInvValAdjustLineClass extends SvrProcess {

	MInvValProfile m_InvValProfile = null;
	MInvValAdjust m_InvValAdjust = null;
	int Record_ID = 0;

	@Override
	protected void prepare()
	{
		Record_ID = getRecord_ID();
		if(Record_ID > 0)
		{
			m_InvValAdjust = new MInvValAdjust(getCtx(), Record_ID, null);
			m_InvValProfile = new MInvValProfile(getCtx(), m_InvValAdjust.getJP_InvValProfile_ID(), null);
		}else{
			log.log(Level.SEVERE, "Record_ID <= 0 ");
		}
	}

	@Override
	protected String doIt() throws Exception
	{

		ProcessInfo pi = new ProcessInfo("Title", 0, getTable_ID(), Record_ID);

		String className = null;
		if(Util.isEmpty(m_InvValProfile.getJP_InvValAdjustLineClass()))
		{
			className = "jpiere.base.plugin.org.adempiere.process.DefaultCreateInvValAdjustLine";

		}else{
			className = m_InvValProfile.getJP_InvValAdjustLineClass();
		}

		pi.setClassName(className);
		pi.setAD_Client_ID(getAD_Client_ID());
		pi.setAD_User_ID(getAD_User_ID());
		pi.setAD_PInstance_ID(getAD_PInstance_ID());
		pi.setParameter(getParameter());
		boolean isOK = ProcessUtil.startJavaProcess(getCtx(), pi, Trx.get(get_TrxName(), true), false, Env.getProcessUI(getCtx()));

		if(isOK)
		{
			m_InvValAdjust.setJP_Processing1("Y");
			m_InvValAdjust.setJP_ProcessedTime1(new Timestamp(System.currentTimeMillis()));
			m_InvValAdjust.setJP_Processing2("N");
			m_InvValAdjust.setJP_ProcessedTime2(null);
			m_InvValAdjust.setJP_Processing3("N");
			m_InvValAdjust.setJP_ProcessedTime3(null);
			m_InvValAdjust.setTotalLines(Env.ZERO);
			m_InvValAdjust.setDifferenceAmt(Env.ZERO);
			m_InvValAdjust.setDifferenceQty(Env.ZERO);
			m_InvValAdjust.saveEx(get_TrxName());

		}else{
			throw new AdempiereException(pi.getSummary());
		}

		return pi.getSummary();
	}

}
