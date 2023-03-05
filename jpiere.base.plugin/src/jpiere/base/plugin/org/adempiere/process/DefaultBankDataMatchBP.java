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
import java.util.ArrayList;
import java.util.logging.Level;

import org.compiere.model.MBPBankAccount;
import org.compiere.model.MBPartner;
import org.compiere.model.MColumn;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.MBankData;
import jpiere.base.plugin.org.adempiere.model.MBankDataLine;
import jpiere.base.plugin.org.adempiere.model.MBankDataSchema;

/**
 * JPIERE-0304 : Default Bank Data match Business Partner
 * 
 * @author 
 *
 */
public class DefaultBankDataMatchBP extends SvrProcess {
	
	private int p_JP_BankData_ID = 0;
	private MBankData m_BankData = null;
	private MBankDataSchema m_BankDataSchema = null;
	private String p_BPartnerColumn = MBankDataLine.COLUMNNAME_JP_A_Name_Kana;
	
	private int p_AD_Client_ID = 0;

	private int p_BPartnerColumn_Length = 0; 
	private boolean isTrim = false;
	
	@Override
	protected void prepare()
	{
		p_AD_Client_ID = getAD_Client_ID();
		p_JP_BankData_ID = getRecord_ID();
		m_BankData = new MBankData(getCtx(), p_JP_BankData_ID, get_TrxName());
		m_BankDataSchema = new MBankDataSchema(getCtx(), m_BankData.getJP_BankDataSchema_ID(), get_TrxName());
		if(m_BankDataSchema.columnExists("BPartnerColumn"))
		{
			String string_BankDataSchema = m_BankDataSchema.get_ValueAsString("BPartnerColumn");
			if(!Util.isEmpty(string_BankDataSchema))
			{
				MColumn column = MColumn.get(getCtx(), MBPBankAccount.Table_Name, string_BankDataSchema);
				if(column != null)
					p_BPartnerColumn = string_BankDataSchema;
			}
		}
		
		MColumn m_colmun = MColumn.get(getCtx(), MBPBankAccount.Table_Name, p_BPartnerColumn);
		p_BPartnerColumn_Length = m_colmun.getFieldLength();
		
	}
	
	@Override
	protected String doIt() throws Exception 
	{
		
		String trim_JP_A_Name_Kana = null;
		String message = null;
		MBPartner[] m_BPartners = null;
		MBPartner m_BP = null;
		
		MBankDataLine[] lines =  m_BankData.getLines();
		for(int i = 0 ; i < lines.length; i++)
		{
			isTrim = false;
			message = null;
			
			if(Util.isEmpty(lines[i].getJP_A_Name_Kana()))
			{
				continue;
			}
			
			trim_JP_A_Name_Kana = lines[i].getJP_A_Name_Kana().trim();
			if(Util.isEmpty(trim_JP_A_Name_Kana))
			{
				continue;
			}
				
			if(trim_JP_A_Name_Kana.length() > p_BPartnerColumn_Length)
			{
				trim_JP_A_Name_Kana = trim_JP_A_Name_Kana.substring(0, p_BPartnerColumn_Length);
				isTrim = true;
			}
			
			m_BPartners = getMBPartner(trim_JP_A_Name_Kana);
			
			for(int j = 0; j < m_BPartners.length; j++)
			{
				m_BP = m_BPartners[j];
				lines[i].setC_BPartner_ID(m_BP.getC_BPartner_ID());
					
				if(m_BPartners.length == 1)
				{
					if(isTrim)
					{
						message = "先頭から" + p_BPartnerColumn_Length + "文字で取引先を照合しています。";
					}
					
				}else {
					
					if(j == 0)
					{
						if(isTrim)
						{
							message = "先頭から" + p_BPartnerColumn_Length + "文字で取引先を照合しています。" + System.lineSeparator() 
										+"複数の取引先が照合しました。"+ System.lineSeparator() 
										+ m_BP.getValue() + "_" + m_BP.getName();
						}else {
							message = "複数の取引先が照合しました。"+ System.lineSeparator() 
										+ m_BP.getValue() + "_" + m_BP.getName();
						}
						
					}else {
						message = lines[i].getI_ErrorMsg() + System.lineSeparator() + m_BP.getValue() + "_" + m_BP.getName();
					}
				}
				
				if(!Util.isEmpty(message))
					lines[i].setI_ErrorMsg(message);
				
				lines[i].saveEx(get_TrxName());
			}//for j

		}//for i
		
		
		return null;
	}
	
	private  MBPartner[] getMBPartner(String JP_A_Name_Kana)
	{		
 		ArrayList<MBPartner> list = new ArrayList<MBPartner>();
 		String sql = null;
 		if(Util.isEmpty(p_BPartnerColumn))
 		{
 			sql = "SELECT DISTINCT C_BPartner_ID  FROM C_BP_BankAccount WHERE AD_Client_ID = ? AND JP_A_Name_Kana = ? ORDER BY C_BPartner_ID DESC";
 		}else {
 			sql = "SELECT DISTINCT C_BPartner_ID  FROM C_BP_BankAccount WHERE AD_Client_ID = ? AND " + p_BPartnerColumn + " = ? ORDER BY C_BPartner_ID DESC";
 		}
 		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, p_AD_Client_ID);
			pstmt.setString(2, JP_A_Name_Kana);
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add(new MBPartner (getCtx(), rs.getInt(1), get_TrxName()));
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

		MBPartner[] m_BPs = new MBPartner[list.size()];
		list.toArray(m_BPs);
		return m_BPs;
		 

	}
	
	
}
