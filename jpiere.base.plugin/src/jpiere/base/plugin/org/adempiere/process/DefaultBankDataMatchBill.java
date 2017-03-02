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
import java.util.logging.Level;

import org.compiere.model.MInvoice;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;

import jpiere.base.plugin.org.adempiere.model.MBankData;
import jpiere.base.plugin.org.adempiere.model.MBankDataLine;
import jpiere.base.plugin.org.adempiere.model.MBankDataSchema;
import jpiere.base.plugin.org.adempiere.model.MBill;

/**
 * JPIERE-0306 : Default Bank Data match Bill
 * 
 * @author 
 *
 */
public class DefaultBankDataMatchBill extends SvrProcess {
	

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
		BigDecimal acceptableDiffAmt = BDSchema.getJP_AcceptableDiffAmt();
		
		
		MBankDataLine[] lines =  m_BankData.getLines();
		String sql = "SELECT JP_Bill_ID FROM JP_Bill WHERE AD_Client_ID = ? AND IsSOTrx = 'Y' AND  C_BPartner_ID = ? AND ( DocStatus ='CO' or DocStatus ='CL' )";//TODO:日付指定を含めないとデータが多すぎる…
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		for(int i = 0 ; i < lines.length; i++)
		{
			try
			{
				pstmt = DB.prepareStatement(sql, get_TrxName());
				pstmt.setInt(1, p_AD_Client_ID);
				pstmt.setInt(2, lines[i].getC_BPartner_ID());
				rs = pstmt.executeQuery();
				while (rs.next())
				{
					if(lines[i].isMatchedJP())
						break;
					
					int JP_Bill_ID = rs.getInt(1);
					MBill inv = new MBill(getCtx(), JP_Bill_ID, get_TrxName());
					BigDecimal openAmt = inv.getOpenAmt();
					BigDecimal diffAmt = lines[i].getTrxAmt().subtract(openAmt);
					if(diffAmt.abs().compareTo(acceptableDiffAmt.abs()) <= 0)
					{
						lines[i].setJP_Bill_ID(JP_Bill_ID);
						
						//TODO:差異があった場合の料金タイプ処理の実装
						
						lines[i].setIsMatchedJP(true);
						lines[i].saveEx(get_TrxName());
					}
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
