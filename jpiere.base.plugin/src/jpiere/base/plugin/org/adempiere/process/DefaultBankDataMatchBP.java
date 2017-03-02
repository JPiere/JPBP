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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;

import org.compiere.process.SvrProcess;
import org.compiere.util.DB;

import jpiere.base.plugin.org.adempiere.model.MBankData;
import jpiere.base.plugin.org.adempiere.model.MBankDataLine;

/**
 * JPIERE-0304 : Default Bank Data match Business Partner
 * 
 * @author 
 *
 */
public class DefaultBankDataMatchBP extends SvrProcess {
	
	private int p_JP_BankData_ID = 0;
	private MBankData m_BankData = null;
	
	private int p_AD_Client_ID = 0;

	@Override
	protected void prepare()
	{
		p_AD_Client_ID = getAD_Client_ID();
		p_JP_BankData_ID = getRecord_ID();
		m_BankData = new MBankData(getCtx(), p_JP_BankData_ID, get_TrxName());
		
	}
	
	@Override
	protected String doIt() throws Exception 
	{

		MBankDataLine[] lines =  m_BankData.getLines();
		String sql = "SELECT C_BPartner_ID FROM C_BP_BankAccount WHERE AD_Client_ID = ? AND JP_A_Name_Kana = ? ORDER BY C_BP_BankAccount_ID ASC";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		for(int i = 0 ; i < lines.length; i++)
		{
			try
			{
				pstmt = DB.prepareStatement(sql, get_TrxName());
				pstmt.setInt(1, p_AD_Client_ID);
				pstmt.setString(2, lines[i].getJP_A_Name_Kana().trim());
				rs = pstmt.executeQuery();
				while (rs.next())
				{
					int C_BPartner_ID = rs.getInt(1);
					lines[i].setC_BPartner_ID(C_BPartner_ID);
					lines[i].saveEx(get_TrxName());
				}
			}
			catch (Exception e)
			{
				log.log(Level.SEVERE, sql, e);
			}
			finally
			{
				DB.close(rs, pstmt);
				rs = null;
				pstmt = null;
			}

		}
		
		
		return null;
	}
	
}
