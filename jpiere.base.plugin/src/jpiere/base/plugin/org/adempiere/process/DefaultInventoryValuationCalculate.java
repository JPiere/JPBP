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

import java.math.BigDecimal;
import java.util.logging.Level;

import jpiere.base.plugin.org.adempiere.model.MInvValCal;
import jpiere.base.plugin.org.adempiere.model.MInvValCalLine;
import jpiere.base.plugin.org.adempiere.model.MInvValProfile;
import jpiere.base.plugin.util.JPiereInvValUtil;

import org.compiere.process.SvrProcess;
import org.compiere.util.Msg;

/**
 * JPIERE-0161 Inventory Valuation Calculate Doc
 *
 *
 *  @author Hideaki Hagiwara
 *
 */
public class DefaultInventoryValuationCalculate extends SvrProcess {

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
			if(lines[i].getCostingMethod().equals(MInvValCalLine.COSTINGMETHOD_Fifo))
				calculate_Fifo(lines[i]);
			else if(lines[i].getCostingMethod().equals(MInvValCalLine.COSTINGMETHOD_Lifo))
				calculate_Lifo(lines[i]);
			else if(lines[i].getCostingMethod().equals(MInvValCalLine.COSTINGMETHOD_LastPOPrice))
				calculate_LastPOPrice(lines[i]);
			else if(lines[i].getCostingMethod().equals(MInvValCalLine.COSTINGMETHOD_LastInvoice))
				calculate_LastInvoice(lines[i]);
			else if(lines[i].getCostingMethod().equals(MInvValCalLine.COSTINGMETHOD_AveragePO))
				calculate_AveragePO(lines[i]);
			else if(lines[i].getCostingMethod().equals(MInvValCalLine.COSTINGMETHOD_AverageInvoice))
				calculate_AverageInvoice(lines[i]);
			else if(lines[i].getCostingMethod().equals(MInvValCalLine.COSTINGMETHOD_StandardCosting))
				calculate_StandardCosting(lines[i]);
		}

		BigDecimal totalLines = JPiereInvValUtil.calculateTotalLines(getCtx(), Record_ID, get_TrxName());
		m_InvValCal.setTotalLines(totalLines);
		m_InvValCal.saveEx(get_TrxName());

		return Msg.getElement(getCtx(), MInvValCal.COLUMNNAME_TotalLines) + " = " + totalLines;
	}

	private void calculate_Fifo(MInvValCalLine line)
	{
		;
	}

	private void calculate_Lifo(MInvValCalLine line)
	{
		;
	}

	private void calculate_LastPOPrice(MInvValCalLine line)
	{
		;
	}

	private void calculate_LastInvoice(MInvValCalLine line)
	{
		;
	}

	private void calculate_AveragePO(MInvValCalLine line)
	{
		;
	}

	private void calculate_AverageInvoice(MInvValCalLine line)
	{
		;
	}

	private void calculate_StandardCosting(MInvValCalLine line)
	{
		;
	}

}
