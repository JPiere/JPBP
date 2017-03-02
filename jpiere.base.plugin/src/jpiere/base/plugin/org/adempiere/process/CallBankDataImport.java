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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.logging.Level;

import jpiere.base.plugin.org.adempiere.model.MBankData;
import jpiere.base.plugin.org.adempiere.model.MBankDataSchema;

import org.adempiere.exceptions.AdempiereException;
import org.adempiere.util.ProcessUtil;
import org.compiere.model.MBPGroup;
import org.compiere.process.ProcessInfo;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Trx;
import org.compiere.util.Util;

/**
 * JPIERE-00302 : Import Bank Data
 *
 *
 *  @author Hideaki Hagiwara
 *
 */
public class CallBankDataImport extends SvrProcess {

	MBankDataSchema m_BankDataSchema = null;
	MBankData m_BankData = null;
	int Record_ID = 0;

	@Override
	protected void prepare()
	{
		Record_ID = getRecord_ID();
		if(Record_ID > 0)
		{
			m_BankData = new MBankData(getCtx(), Record_ID, null);
			m_BankDataSchema = new MBankDataSchema(getCtx(), m_BankData.getJP_BankDataSchema_ID(), null);
		}else{
			log.log(Level.SEVERE, "Record_ID <= 0 ");
		}
	}

	@Override
	protected String doIt() throws Exception
	{
		ProcessInfo pi = new ProcessInfo("Title", 0, getTable_ID(), Record_ID);
		if(Util.isEmpty(m_BankDataSchema.getJP_BankDataImportClass()))
			pi.setClassName("jpiere.base.plugin.org.adempiere.process.DefaultBankDataImport");
		else
			pi.setClassName(m_BankDataSchema.getJP_BankDataImportClass());
		pi.setAD_Client_ID(getAD_Client_ID());
		pi.setAD_User_ID(getAD_User_ID());
		pi.setAD_PInstance_ID(getAD_PInstance_ID());
		pi.setParameter(getParameter());
		boolean isOK = ProcessUtil.startJavaProcess(getCtx(), pi, Trx.get(get_TrxName(), true), false, Env.getProcessUI(getCtx()));

		if(isOK)
		{
			m_BankData.setJP_Processing1("Y");
			m_BankData.setJP_ProcessedTime1(new Timestamp(System.currentTimeMillis()));
			
			String sql = "SELECT SUM(StmtAmt), SUM(TrxAmt), SUM(ChargeAmt), SUM(InterestAmt) From JP_BankDataLine WHERE JP_BankData_ID = ?";
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				pstmt = DB.prepareStatement (sql, get_TrxName());
				pstmt.setInt (1, m_BankData.getJP_BankData_ID());
				rs = pstmt.executeQuery();
				if (rs.next ())
				{
					m_BankData.setStmtAmt(rs.getBigDecimal(1));
					m_BankData.setTrxAmt(rs.getBigDecimal(2));
					m_BankData.setChargeAmt(rs.getBigDecimal(3));
					m_BankData.setInterestAmt(rs.getBigDecimal(4));
				}
			}
			catch (Exception e)
			{
				log.log (Level.SEVERE, sql, e);
			}
			finally
			{
				DB.close(rs, pstmt);
				rs = null;
				pstmt = null;
			}
			
			m_BankData.saveEx(get_TrxName());
			
		}else{
			throw new AdempiereException(pi.getSummary());
		}
		
		return "";
	}

}
