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
import java.util.ArrayList;
import java.util.logging.Level;

import org.adempiere.util.ProcessUtil;
import org.compiere.model.MElementValue;
import org.compiere.model.MTree_Base;
import org.compiere.process.ProcessInfo;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Trx;


/**
 * 
 * JPIERE-0572
 * 
 * General Ledger that is Selected Summary Level Account for Print Out
 * 
 * @author h.hagiwara
 *
 */

public class TrialBalanceBulkCreate extends SvrProcess {

	private int p_C_ElementValue_ID = 0;
	private int p_AD_Tree_Account_ID = 0;
	private int successNum = 0;
	private int failureNum = 0;
	
	@Override
	protected void prepare() 
	{
		processUI = Env.getProcessUI(getCtx());
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (name.equals("C_ElementValue_ID")) {
				p_C_ElementValue_ID = para[i].getParameterAsInt();
			}else if (name.equals("AD_Tree_ID")) {
				p_AD_Tree_Account_ID = para[i].getParameterAsInt();
			}
		}

	}

	@Override
	protected String doIt() throws Exception
	{
		
		ArrayList<MElementValue> list = new ArrayList<MElementValue>();
		
		if(p_C_ElementValue_ID == 0)
		{
			loadAccount(list);
		}else {
			loadAccount(list,new MElementValue(getCtx(), p_C_ElementValue_ID,  get_TrxName()));
		}
		
		String className = "org.compiere.report.TrialBalance";


		ProcessInfo pi = new ProcessInfo("Trial Balance Bulk Create", 0);
		pi.setClassName(className);
		pi.setAD_Client_ID(getAD_Client_ID());
		pi.setAD_User_ID(getAD_User_ID());
		pi.setAD_PInstance_ID(getAD_PInstance_ID());
		pi.setRecord_ID(0);
		
		for(MElementValue account : list)
		{
			if(processUI != null)
				processUI.statusUpdate(account.getValue() + " -  " +account.getName());
			
			setProcessInfoParameter(pi,account.getC_ElementValue_ID());
			
			if(ProcessUtil.startJavaProcess(getCtx(), pi, Trx.get(get_TrxName(), true), false, processUI))
			{
				successNum++;
			}else {
				failureNum++;
			}
		}
		
		if(processUI != null)
			processUI.statusUpdate(Msg.getMsg(getCtx(), "Processing"));
		
		
		int totalElementValue = successNum + failureNum;
		
		String msg = Msg.getElement(getCtx(), "Account_ID") + " : " +  totalElementValue + " " 
						+ " ( " + Msg.getMsg(getCtx(), "JP_Success") + " : " + successNum
						+ " + " + Msg.getMsg(getCtx(), "JP_Failure") + " : " + failureNum +" ) " ;
		
		addLog(msg);
		
		return msg ;
	}
	
	
	private void loadAccount (ArrayList<MElementValue> list)
	{
		
		MTree_Base tree = MTree_Base.get(getCtx(), p_AD_Tree_Account_ID, get_TrxName());
		String sql =  "SELECT * FROM C_ElementValue "
			+ "WHERE IsActive='Y' AND C_ElementValue_ID IN (SELECT Node_ID FROM "
			+ tree.getNodeTableName()
			+ " WHERE AD_Tree_ID=? AND (Parent_ID IS NULL OR Parent_ID = 0 ) AND IsActive='Y') ORDER BY Value";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (sql, get_TrxName());
			pstmt.setInt (1, tree.getAD_Tree_ID());
			rs = pstmt.executeQuery ();
			while (rs.next ())
			{
				loadAccount (list, new MElementValue(getCtx(), rs,  get_TrxName()));
			}
		}
		catch (Exception e)
		{
			log.log (Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}
	}	//	loadAccount
	
	private void loadAccount (ArrayList<MElementValue> list, MElementValue ev)
	{
		if (list.contains(ev))
			return;
		
		if (!ev.isSummary())
		{
			list.add(ev);
			return;
		}
		
		MTree_Base tree = MTree_Base.get(getCtx(), p_AD_Tree_Account_ID, get_TrxName());
		String sql =  "SELECT * FROM C_ElementValue "
			+ "WHERE IsActive='Y' AND C_ElementValue_ID IN (SELECT Node_ID FROM "
			+ tree.getNodeTableName()
			+ " WHERE AD_Tree_ID=? AND Parent_ID=? AND IsActive='Y') ORDER BY Value";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (sql, get_TrxName());
			pstmt.setInt (1, tree.getAD_Tree_ID());
			pstmt.setInt(2, ev.getC_ElementValue_ID());
			rs = pstmt.executeQuery ();
			while (rs.next ())
			{
				loadAccount (list, new MElementValue(getCtx(), rs,  get_TrxName()));
			}
		}
		catch (Exception e)
		{
			log.log (Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}
	}	//	loadAccount
	
	private void setProcessInfoParameter(ProcessInfo pi, int Account_ID) throws Exception
	{
		ArrayList<ProcessInfoParameter> list = new ArrayList<ProcessInfoParameter>();
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (name.equals("C_AcctSchema_ID")) {
				list.add(para[i]);
			}else if (name.equals("C_Period_ID")) {
				list.add(para[i]);
			}else if (name.equals("DateAcct")){
				list.add(para[i]);
			}else if (name.equals("PA_Hierarchy_ID")) {
				list.add(para[i]);
			}else if (name.equals("AD_Org_ID")) {
				list.add(para[i]);
			}else if (name.equals("C_ElementValue_ID")) {
				list.add (new ProcessInfoParameter("Account_ID",BigDecimal.valueOf(Account_ID), null, para[i].getInfo(), para[i].getInfo_To() ));
			}else if (name.equals("AccountValue")){
				list.add(para[i]);
			}else if (name.equals("C_BPartner_ID")) {
				list.add(para[i]);
			}else if (name.equals("M_Product_ID")) {
				list.add(para[i]);
			}else if (name.equals("C_Project_ID")) {
				list.add(para[i]);
			}else if (name.equals("C_Activity_ID")) {
				list.add(para[i]);
			}else if (name.equals("C_SalesRegion_ID")) {
				list.add(para[i]);
			}else if (name.equals("C_Campaign_ID")) {
				list.add(para[i]);
			}else if (name.equals("PostingType")) {
				list.add(para[i]);
			}else if (name.equals("AD_Tree_ID")) {
				;
			}else {
				list.add(para[i]);
			}
		}

		ProcessInfoParameter[] pars = new ProcessInfoParameter[list.size()];
		list.toArray(pars);
		pi.setParameter(pars);
	}
	
}
