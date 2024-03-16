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
import org.compiere.util.Env;

import jpiere.base.plugin.org.adempiere.model.MBankData;
import jpiere.base.plugin.org.adempiere.model.MBankDataLine;
import jpiere.base.plugin.org.adempiere.model.MBankDataSchema;

/**
 * JPIERE-0305 : Default Bank Data match Invoice
 * 
 * @author 
 *
 */
public class DefaultBankDataMatchInv extends SvrProcess {
	
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
		String sql = "SELECT C_Invoice_ID FROM C_Invoice WHERE IsPaid='N' AND AD_Client_ID = ? AND IsSOTrx = 'Y' AND  C_BPartner_ID = ? AND ( DocStatus ='CO' or DocStatus ='CL') "
					+ " AND C_Invoice_ID NOT IN (SELECT DISTINCT COALESCE(C_Invoice_ID,0) FROM JP_BankDataLine WHERE JP_BankData_ID = ? )  ORDER BY DateInvoiced ASC;";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		for(int i = 0 ; i < lines.length; i++)
		{
			if(lines[i].isMatchedJP())
				continue;
			
			if(lines[i].getC_BPartner_ID() == 0)
				continue;
			try
			{
				pstmt = DB.prepareStatement(sql, get_TrxName());
				pstmt.setInt(1, p_AD_Client_ID);
				pstmt.setInt(2, lines[i].getC_BPartner_ID());
				pstmt.setInt(3, p_JP_BankData_ID);
				rs = pstmt.executeQuery();
				while (rs.next())
				{
					int C_Invoice_ID = rs.getInt(1);
					MInvoice inv = new MInvoice(getCtx(), C_Invoice_ID, get_TrxName());
					BigDecimal openAmt = inv.getOpenAmt();
					BigDecimal diffAmt = lines[i].getTrxAmt().subtract(openAmt);
					if(diffAmt.abs().compareTo(acceptableDiffAmt.abs()) <= 0)
					{
						lines[i].setC_Invoice_ID(C_Invoice_ID);
						
						if(diffAmt.compareTo(Env.ZERO) !=0)
						{
							lines[i].setTrxAmt(openAmt);
							lines[i].setChargeAmt(diffAmt);
							lines[i].setC_Charge_ID(BDSchema.getC_Charge_ID());
							lines[i].setC_Tax_ID(BDSchema.getC_Tax_ID());
						}
						
						lines[i].setIsMatchedJP(true);
						lines[i].saveEx(get_TrxName());
						break;
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
