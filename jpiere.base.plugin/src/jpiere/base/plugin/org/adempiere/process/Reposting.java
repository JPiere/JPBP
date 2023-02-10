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
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.adempiere.base.IModelFactory;
import org.adempiere.base.Service;
import org.adempiere.model.GenericPO;
import org.adempiere.util.IProcessUI;
import org.compiere.model.MPeriod;
import org.compiere.model.MRefList;
import org.compiere.model.PO;
import org.compiere.process.DocumentEngine;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;


/**
 * JPIERE-0591: Reposting
 *
 *
 * @author h.hagiwara
 *
 */
public class Reposting extends SvrProcess {

	private int p_AD_Client_ID = 0;
	private int p_AD_Table_ID = 0;
	private Timestamp p_DateAcct_From = null;
	private Timestamp p_DateAcct_To = null;
	private boolean p_IsSuccessLogJP = false;
	private boolean p_IsCloseLogJP = false;
	
	@Override
	protected void prepare()
	{
		p_AD_Client_ID = getAD_Client_ID();

		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null && para[i].getParameter_To() == null) {
				;
			}else if (name.equals("AD_Table_ID")) {
				p_AD_Table_ID = ((BigDecimal)para[i].getParameter()).intValue();
			}else if (name.equals("DateAcct")) {
				p_DateAcct_From = para[i].getParameterAsTimestamp();
				p_DateAcct_To = para[i].getParameter_ToAsTimestamp();
			}else if(name.equals("IsSuccessLogJP")) {
				p_IsSuccessLogJP = para[i].getParameterAsBoolean();
			}else if(name.equals("IsCloseLogJP")) {
				p_IsCloseLogJP = para[i].getParameterAsBoolean();
			}else {
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}
		}
	}

	private String success = null;
	private String failure = null;
	private String skip = null;
	private String close = null;
	private int successNum = 0;
	private int errorNum = 0;
	private int skipNum = 0;
	
	private IProcessUI processUI = null;

	@Override
	protected String doIt() throws Exception
	{
		processUI = Env.getProcessUI(getCtx());
		success = Msg.getMsg(getCtx(), "JP_Success");
		failure = Msg.getMsg(getCtx(), "JP_Failure");
		skip = Msg.getMsg(getCtx(), "JP_NumberOfSkipRecords");
		close = Msg.getElement(getCtx(), "C_Period_ID") + " " + MRefList.getListName(getCtx(), 177, "C");

		String sql = "SELECT AD_Table_ID, TableName "
				+ "FROM AD_Table t "
				+ "WHERE t.IsView='N'";
			if (p_AD_Table_ID > 0)
				sql += " AND t.AD_Table_ID=" + p_AD_Table_ID;
			sql += " AND EXISTS (SELECT * FROM AD_Column c "
					+ "WHERE t.AD_Table_ID=c.AD_Table_ID AND c.ColumnName='Posted' AND c.IsActive='Y')";

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, get_TrxName());
			rs = pstmt.executeQuery();
			while (rs.next())
			{
				int AD_Table_ID = rs.getInt(1);
				String TableName = rs.getString(2);
				reposting(AD_Table_ID , TableName);
			}
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql, e);
			throw e;
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

		return Msg.getMsg(getCtx(), "ProcessOK") + "("+success + " : " +  successNum + " / " + failure + " : " +errorNum+ " / " + skip + " : " +skipNum+ ")";
	}

	private void reposting(int AD_Table_ID, String TableName)
	{
		ArrayList<Integer> list = new ArrayList<Integer>();
		String sql = "SELECT DISTINCT Record_ID FROM Fact_Acct WHERE AD_Table_ID = ?";
		if(p_DateAcct_From != null)
			sql += " AND DateAcct >= ? AND DateAcct <= ?";
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
			pstmt.setInt(1, AD_Table_ID);
			if(p_DateAcct_From != null)
			{
				pstmt.setTimestamp(2, p_DateAcct_From);
				pstmt.setTimestamp(3, p_DateAcct_To);
			}
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add(Integer.valueOf(rs.getInt(1)));
		}
		catch (SQLException e)
		{
			;
		}
		finally {
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}
		
		int[] records = new int[list.size()];
		for (int i = 0; i < records.length; i++)
			records[i] = ((Integer)list.get(i)).intValue();
		
		PO po = null;
		String docIdentifier = null;
		boolean isOpenPeriod = false;
		String error = null;
		for(int i = 0; i < records.length; i++)
		{
			error = null;
			isOpenPeriod = MPeriod.isOpen(getCtx(),AD_Table_ID, records[i], get_TrxName());
			
			if(isOpenPeriod)
				error = DocumentEngine.postImmediate(Env.getCtx(), p_AD_Client_ID, AD_Table_ID, records[i], true, get_TrxName());

			po = null;
			List<IModelFactory> factoryList = Service.locator().list(IModelFactory.class).getServices();
			if (factoryList != null)
			{
				for(IModelFactory factory : factoryList)
				{
					po = factory.getPO(TableName, records[i], get_TrxName());
					if (po != null)
					{
						if (po.get_ID() != records[i] && records[i] > 0)
							po = null;
						else
							break;
					}
				}
			}

			if (po == null)
			{
				po = new GenericPO(TableName, getCtx(), records[i], get_TrxName());
				if (po.get_ID() != records[i] && records[i] > 0)
					po = null;
			}

			if (po == null)
				continue;


			if(po.get_ColumnIndex("DocumentNo") > 0)
			{
				docIdentifier = po.get_Value("DocumentNo").toString();

			}else if(po.get_ColumnIndex("Name") > 0) {

				docIdentifier = po.get_Value("Name").toString();

			}else {

				docIdentifier = TableName + "[" + records[i] + "]";
			}
			
			
			if(isOpenPeriod)
			{
				if(Util.isEmpty(error))
				{
					if(processUI != null)
						processUI.statusUpdate(success + " : " + docIdentifier);
					
					if(p_IsSuccessLogJP)
						addLog(0, null, null, success + " : " + docIdentifier, AD_Table_ID, records[i]);
					
					successNum++;
					
				}else {//Error
					
					if(processUI != null)
						processUI.statusUpdate(failure + " : " + docIdentifier);
					
					addLog(0, null, null, failure + " : " + docIdentifier, AD_Table_ID, records[i]);
					errorNum++;
				}
				
			}else {
				
				if(processUI != null)
					processUI.statusUpdate(close + " : " + docIdentifier);
				
				if(p_IsCloseLogJP)
					addBufferLog(0, null, null, close + " : " + docIdentifier, AD_Table_ID, records[i]);
				
				skipNum++;
			}

		}//for

	}

}
