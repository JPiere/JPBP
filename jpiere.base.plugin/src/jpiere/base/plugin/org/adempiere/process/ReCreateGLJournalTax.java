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
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.adempiere.util.IProcessUI;
import org.compiere.model.MJournal;
import org.compiere.model.MJournalLine;
import org.compiere.model.MPeriod;
import org.compiere.model.MTax;
import org.compiere.process.DocAction;
import org.compiere.process.DocumentEngine;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.base.IJPiereTaxProvider;
import jpiere.base.plugin.org.adempiere.model.MGLJournalTax;
import jpiere.base.plugin.util.JPiereUtil;

/**
 *	JPIERE-0544: Tax Calculation at GL Journal - Data patch.
 *
 * 振替仕訳伝票で消費税の計算に不具合があった場合に、正しく再計算するプロセスです。
 *
 *  @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 */
public class ReCreateGLJournalTax extends SvrProcess {
	
	private int p_C_AcctSchema_ID = 0;
	private Timestamp p_DateAcct_From = null;
	private Timestamp p_DateAcct_To = null;
	
	private int GL_Journal_ID = 0;
	
	private IProcessUI processUI = null;
	
	@Override
	protected void prepare() 
	{
		processUI = Env.getProcessUI(getCtx());
		GL_Journal_ID = getRecord_ID();
		
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (name.equals("C_AcctSchema_ID"))
			{
				p_C_AcctSchema_ID =para[i].getParameterAsInt();
			}else if (name.equals("DateAcct")){
				p_DateAcct_From = para[i].getParameterAsTimestamp();
				p_DateAcct_To = para[i].getParameter_ToAsTimestamp();				
			}else{
				//log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}//if

		}
	}

	@Override
	protected String doIt() throws Exception 
	{	

		if(GL_Journal_ID != 0)
		{
			
			MJournal m_GLJournal = new MJournal(getCtx(), GL_Journal_ID, get_TrxName());
			if(reCalculateTax(m_GLJournal))
			{
				addBufferLog(0, null, null, "【成功】" + m_GLJournal.getDocumentNo() + " : " + (message != null? message : "")
					, MJournal.Table_ID, m_GLJournal.getGL_Journal_ID());
				
			}else {
				
				addBufferLog(0, null, null, "【失敗】" + m_GLJournal.getDocumentNo() + "　: " + (message != null? message : "")
					, MJournal.Table_ID, m_GLJournal.getGL_Journal_ID());
			}
			
			return "選択しているレコードの処理が完了しました。";
			
		}else {
			
			if(p_C_AcctSchema_ID == 0)
				throw new Exception("会計スキーマは必須です。");
			
			if(p_DateAcct_From == null || p_DateAcct_To == null)
				throw new Exception("転記日付は必須です。");
			
			int success = 0;
			int failure = 0;
			MJournal[] m_Journals = getGLJournals();
			for(MJournal m_GLJournal : m_Journals)
			{
				if(processUI != null)
					processUI.statusUpdate(m_GLJournal.getDocumentNo());
				
				if(reCalculateTax(m_GLJournal))
				{
					success++;
					addBufferLog(0, null, null, "【成功】" + m_GLJournal.getDocumentNo() + "(" + m_GLJournal.getDocStatus() + ") - " +(message != null? message : "")
							, MJournal.Table_ID, m_GLJournal.getGL_Journal_ID()); 
				}else {
					
					failure++;
					addBufferLog(0, null, null, "【失敗】" + m_GLJournal.getDocumentNo() + "(" + m_GLJournal.getDocStatus() + ") - " +(message != null? message : "")
							, MJournal.Table_ID, m_GLJournal.getGL_Journal_ID());
				}
			}
			
			return "処理対象データ件数 : " + m_Journals.length + " （成功件数 ： " + success + " ／　失敗件数 : " + failure + "）";
		}
	}
	
	private String message = null;
	
	private boolean reCalculateTax(MJournal m_GLJournal) throws Exception
	{

		try 
		{
			MPeriod.testPeriodOpen(getCtx(), m_GLJournal.getDateAcct(), m_GLJournal.getC_DocType_ID(), m_GLJournal.getAD_Org_ID());
		}catch(Exception e) {
			message = e.toString();
			return false;
		}
		
		if(DocAction.STATUS_Voided.equals(m_GLJournal.getDocStatus()))
		{
			message = "ボイドされているので処理の対象外です。";
			return true;
		}
		
		//振替仕訳伝票の税額計算タブをいったんすべて削除
		int deleteNum = DB.executeUpdateEx("DELETE FROM JP_GLJournalTax WHERE GL_Journal_ID = " + m_GLJournal.getGL_Journal_ID(), get_TrxName());
		
		//税額計算タブの再計算
		MJournalLine[] m_GLLines = m_GLJournal.getLines(true);
		int C_Tax_ID = 0;
		String JP_SOPOType = null; 
		for(MJournalLine glLine : m_GLLines)
		{
			C_Tax_ID = glLine.get_ValueAsInt(MGLJournalTax.COLUMNNAME_C_Tax_ID);
			if(C_Tax_ID == 0)//自動計算対象外
				continue;
			
			JP_SOPOType = glLine.get_ValueAsString(MGLJournalTax.COLUMNNAME_JP_SOPOType);
			if("N".equals(JP_SOPOType))//自動計算対象外
				continue;
			
			MTax m_tax = new MTax(getCtx(), C_Tax_ID, get_TrxName());
			IJPiereTaxProvider taxCalculater = JPiereUtil.getJPiereTaxProvider(m_tax);
			if (taxCalculater == null)
				throw new AdempiereException(Msg.getMsg(getCtx(), "TaxNoProvider"));
			
			boolean success = taxCalculater.recalculateTax(null, glLine, true);
	    	if(!success)
	    	{
	    		message = "税金計算のエラー : 明細番号 - " + glLine.getLine();
	    		return false;
	    	}
		}
		
		boolean isPosting = false;
		if(DocAction.STATUS_Completed.equals(m_GLJournal.getDocStatus())
				|| DocAction.STATUS_Closed.equals(m_GLJournal.getDocStatus())
				|| DocAction.STATUS_Reversed.equals(m_GLJournal.getDocStatus()))
		{
			String errorMsg = DocumentEngine.postImmediate(Env.getCtx(), m_GLJournal.getAD_Client_ID(), MJournal.Table_ID, m_GLJournal.getGL_Journal_ID(), true, get_TrxName());
			if(!Util.isEmpty(errorMsg))
			{
				message = "転記エラー";
				return false;
			}
			isPosting = true;
		}
		
		message = "税額計算タブのレコードを " + deleteNum + " 件削除して再作成しています。";
		if(isPosting)
			message = message + " 再転記済みです。";
		return true;
	}

	/**
	 * 処理対象となる振替仕訳伝票を取得する
	 * 
	 * @return
	 */
	private MJournal[] getGLJournals()
	{
		ArrayList<MJournal> list = new ArrayList<MJournal>();

		LocalDateTime localDateTime_To = p_DateAcct_To.toLocalDateTime();
		LocalDate localDate_To = localDateTime_To.toLocalDate().plusDays(1);
		Timestamp searchCondition_DateAcct_To =  Timestamp.valueOf(localDate_To.atStartOfDay());

		String sql = "SELECT * FROM GL_Journal WHERE DateAcct >= ? AND DateAcct < ? "
							+ " AND AD_Client_ID = ? AND C_AcctSchema_ID = ? ORDER BY GL_Journal_ID ASC";

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setTimestamp(1, p_DateAcct_From);
			pstmt.setTimestamp(2, searchCondition_DateAcct_To);
			pstmt.setInt(3, getAD_Client_ID());
			pstmt.setInt(4, p_C_AcctSchema_ID);
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add(new MJournal (getCtx(), rs, get_TrxName()));
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

		MJournal[] m_Journals = new MJournal[list.size()];
		list.toArray(m_Journals);
		return m_Journals;

	}
}
