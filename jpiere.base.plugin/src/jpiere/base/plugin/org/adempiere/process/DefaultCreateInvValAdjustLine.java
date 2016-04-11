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

import jpiere.base.plugin.org.adempiere.model.MInvValAdjust;
import jpiere.base.plugin.org.adempiere.model.MInvValAdjustLine;

import jpiere.base.plugin.org.adempiere.model.MInvValProfile;
import jpiere.base.plugin.org.adempiere.model.MInvValProfileOrg;

import org.compiere.process.SvrProcess;
import org.compiere.util.DB;


/**
 * JPIERE-0163 Inventory Valuation Adjust Doc
 *
 * Default Create Inventory Valuation Adjust Doc Line
 *
 *  @author Hideaki Hagiwara
 *
 */
public class DefaultCreateInvValAdjustLine extends SvrProcess {

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
			m_InvValProfile = MInvValProfile.get(getCtx(), m_InvValAdjust.getJP_InvValProfile_ID());
		}else{
			log.log(Level.SEVERE, "Record_ID <= 0 ");
		}
	}

	@Override
	protected String doIt() throws Exception
	{
		StringBuilder sqlDelete = new StringBuilder ("DELETE JP_InvValAdjustLine ")
											.append(" WHERE JP_InvValAdjust_ID=").append(m_InvValAdjust.getJP_InvValAdjust_ID());
		DB.executeUpdateEx(sqlDelete.toString(), get_TrxName());

			
		MInvValProfileOrg[]  Orgs = m_InvValProfile.getOrgs();
		StringBuilder sql = new StringBuilder("SELECT AD_Org_ID, M_Product_ID, Account_ID ")//1 - 3
								.append(",QtyBook, AmtAcctDr, AmtAcctCr, AmtAcctBalance ")	//4 - 7
		.append("FROM JP_InvOrgBalance ")
		.append("WHERE C_AcctSchema_ID=? AND dateValue=? AND AD_Org_ID IN (");
		for(int i = 0; i < Orgs.length; i++)
		{
			if(i==0)
				sql.append(Orgs[i].getAD_Org_ID());
			else
				sql.append(","+Orgs[i].getAD_Org_ID());
		}
		sql.append(") ")
		.append(" ORDER BY M_Product_ID, AD_Org_ID");
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		MInvValAdjustLine ivaLine = null;
		int line = 0;
		try
		{
			pstmt = DB.prepareStatement (sql.toString(), null);
			pstmt.setInt(1, m_InvValProfile.getC_AcctSchema_ID());
			pstmt.setTimestamp(2, m_InvValAdjust.getDateValue());
			rs = pstmt.executeQuery ();
			while (rs.next ())
			{
				line = line + 10;
				ivaLine = new MInvValAdjustLine(getCtx(), 0, get_TrxName());
				ivaLine.setAD_Org_ID(m_InvValAdjust.getAD_Org_ID());
				ivaLine.setAD_OrgTrx_ID(rs.getInt(1));
				ivaLine.setJP_InvValAdjust_ID(m_InvValAdjust.getJP_InvValAdjust_ID());
				ivaLine.setLine(line);
				ivaLine.setM_Product_ID(rs.getInt(2));
				ivaLine.setC_AcctSchema_ID(m_InvValProfile.getC_AcctSchema_ID());
				ivaLine.setCostingMethod(m_InvValProfile.getCostingMethod());
				ivaLine.setCostingLevel(m_InvValProfile.getCostingLevel());
				ivaLine.setAccount_ID(rs.getInt(3));
				ivaLine.setQtyBook(rs.getBigDecimal(4));
				ivaLine.setAmtAcctDr(rs.getBigDecimal(5));
				ivaLine.setAmtAcctCr(rs.getBigDecimal(6));
				ivaLine.setAmtAcctBalance(rs.getBigDecimal(7));
				ivaLine.saveEx(get_TrxName());
			}

		}
		catch (Exception e)
		{
//			s_log.log(Level.SEVERE, sql.toString(), e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}
		
		return "";
	}

}
