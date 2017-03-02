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

import org.compiere.model.MBankStatement;
import org.compiere.model.MBankStatementLine;
import org.compiere.model.PO;
import org.compiere.process.SvrProcess;

import jpiere.base.plugin.org.adempiere.model.MBankData;
import jpiere.base.plugin.org.adempiere.model.MBankDataLine;
import jpiere.base.plugin.org.adempiere.model.MBankDataSchema;

/**
 * JPIERE-0308 : Default Bank Data create Doc
 * 
 * @author 
 *
 */
public class DefaultBankDataCreateDoc extends SvrProcess {
	
	private int p_JP_BankData_ID = 0;
	private MBankData m_BankData = null;
	
	private MBankDataSchema BDSchema = null;
	
	private int p_AD_Client_ID = 0;

	@Override
	protected void prepare()
	{
		p_AD_Client_ID = getAD_Client_ID();
		p_JP_BankData_ID = getRecord_ID();
		m_BankData = new MBankData(getCtx(), p_JP_BankData_ID, get_TrxName());
		BDSchema = new MBankDataSchema(getCtx(), m_BankData.getJP_BankDataSchema_ID(), get_TrxName());		
	}
	
	@Override
	protected String doIt() throws Exception 
	{
		MBankStatement bs = new MBankStatement(getCtx(), 0, get_TrxName());
		MBankDataLine[] lines =  m_BankData.getLines();
		for(int i = 0 ; i < lines.length; i++)
		{
			if(i == 0)
			{
				PO.copyValues(m_BankData, bs);
				bs.setAD_Org_ID(m_BankData.getAD_Org_ID());
				bs.saveEx(get_TrxName());
				
				m_BankData.setC_BankStatement_ID(bs.getC_BankStatement_ID());
				m_BankData.saveEx(get_TrxName());
			}
			
			MBankStatementLine bsl = new MBankStatementLine(getCtx(), 0, get_TrxName());
			PO.copyValues(lines[i], bsl);
			bsl.setC_BankStatement_ID(bs.getC_BankStatement_ID());
			bsl.setAD_Org_ID(bs.getAD_Org_ID());
			bsl.setC_Currency_ID(m_BankData.getC_BankAccount().getC_Currency_ID());
			bsl.setStmtAmt(lines[i].getStmtAmt());
			bsl.setTrxAmt(lines[i].getTrxAmt());
			bsl.setChargeAmt(lines[i].getChargeAmt());
			bsl.setC_Charge_ID(lines[i].getC_Charge_ID());
			bsl.set_ValueNoCheck("C_Tax_ID", lines[i].get_Value("C_Tax_ID"));
			bsl.setInterestAmt(lines[i].getInterestAmt());
			bsl.saveEx(get_TrxName());
			
			lines[i].setC_BankStatementLine_ID(bsl.getC_BankStatementLine_ID());
			lines[i].saveEx(get_TrxName());
		}

		return null;
	}
	
}
