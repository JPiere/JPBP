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

import java.util.logging.Level;

import jpiere.base.plugin.org.adempiere.model.MInvValCal;
import jpiere.base.plugin.org.adempiere.model.MInvValCalLine;
import jpiere.base.plugin.org.adempiere.model.MInvValProfile;

import org.compiere.process.SvrProcess;

/**
 * JPIERE-0161 Inventory Valuation Calculate Doc
 *
 *
 *  @author Hideaki Hagiwara
 *
 */
public class DefaultInvValCalUpdateCost extends SvrProcess {

	MInvValProfile m_InvValProfile = null;
	MInvValCal m_InvValCal = null;
	MInvValCalLine[] lines = null;
	int Record_ID = 0;

	@Override
	protected void prepare()
	{
		Record_ID = getRecord_ID();
		if(Record_ID > 0)
		{
			m_InvValCal = new MInvValCal(getCtx(), Record_ID, null);
			m_InvValProfile = MInvValProfile.get(getCtx(), m_InvValCal.getJP_InvValProfile_ID());
			lines = m_InvValCal.getLines();
		}else{
			log.log(Level.SEVERE, "Record_ID <= 0 ");
		}
	}

	@Override
	protected String doIt() throws Exception
	{
		for(int i = 0; i < lines.length; i++)
		{
			//TODO:Update MCost

			if(m_InvValProfile.getJP_UpdateCost().equals(MInvValProfile.JP_UPDATECOST_CurrentCostPrice))
				;
			else if(m_InvValProfile.getJP_UpdateCost().equals(MInvValProfile.JP_UPDATECOST_FutureCostPrice))
				;
			else if(m_InvValProfile.getJP_UpdateCost().equals(MInvValProfile.JP_UPDATECOST_BothCurrentCostAndFutureCost))
				;
		}

		return null;
	}

}
