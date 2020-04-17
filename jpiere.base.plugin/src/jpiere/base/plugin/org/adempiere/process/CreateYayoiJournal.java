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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.adempiere.util.IProcessUI;
import org.compiere.model.MElementValue;
import org.compiere.model.MFactAcct;
import org.compiere.model.MOrg;
import org.compiere.model.MTable;
import org.compiere.model.MTax;
import org.compiere.model.PO;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.MYayoiJournalJP;

/**
 * JPIERE-0454:Create import data to Yayoi
 *
 * @author Hideaki Hagiwara
 *
 */
public class CreateYayoiJournal extends SvrProcess {

	private int p_AD_Org_ID = 0;
	private int p_C_AcctSchema_ID = 0;
	private String p_PostingType = "A";
	private Timestamp p_DateAcct_From = null;
	private Timestamp p_DateAcct_To = null;
	private int p_AD_Table_ID = 0;
	private int p_Record_ID = 0;

	private IProcessUI 		processUI = null;

	private String TAX_EXCLUDE ="対象外";

	@Override
	protected void prepare()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null){
				;
			}else if (name.equals("AD_Org_ID")){
				p_AD_Org_ID = para[i].getParameterAsInt();
			}else if (name.equals("C_AcctSchema_ID")){
				p_C_AcctSchema_ID = para[i].getParameterAsInt();
			}else if (name.equals("PostingType")){
				p_PostingType = para[i].getParameterAsString();
			}else if (name.equals("DateAcct")){
				p_DateAcct_From = para[i].getParameterAsTimestamp();
				p_DateAcct_To = para[i].getParameter_ToAsTimestamp();
			}else if (name.equals("AD_Table_ID")){
				p_AD_Table_ID = para[i].getParameterAsInt();
			}else if (name.equals("Record_ID")){
				p_Record_ID = para[i].getParameterAsInt();
			}else{
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}//if

		}//for

		processUI = Env.getProcessUI(getCtx());
	}



	@Override
	protected String doIt() throws Exception
	{

		//Create Account Map
		if(processUI != null)processUI.statusUpdate("Load Account");

		Map<Integer,MElementValue> accountMap = new HashMap<Integer,MElementValue>();
		StringBuilder sql = new StringBuilder("SELECT * FROM C_ElementValue ")
				.append(" WHERE AD_Client_ID = ? AND IsSummary = 'N'  ");

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql.toString(), null);
			pstmt.setInt(1, getAD_Client_ID());

			rs = pstmt.executeQuery();
			while(rs.next())
			{
				MElementValue ev = new MElementValue(getCtx(),rs, get_TrxName());
				accountMap.put(ev.getC_ElementValue_ID(), ev);
			}
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, e.toString());
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}


		//Create Fact Acct List
		if(processUI != null)processUI.statusUpdate("Load iDempiere Journal");
		ArrayList<MFactAcct> factAcctList = new ArrayList<MFactAcct>();
		sql = new StringBuilder("SELECT * FROM FACT_ACCT f")
										.append(" WHERE C_AcctSchema_ID = ? AND PostingType = ?  AND DateAcct >= ? AND DateAcct <= ? ");

		if(p_AD_Org_ID > 0)
			sql.append(" AND AD_Org_ID = ? ");

		if(p_AD_Table_ID > 0)
			sql.append(" AND AD_Table_ID = ? ");

		if(p_Record_ID > 0)
			sql.append(" AND Record_ID = ? ");

		sql.append(" ORDER BY DateAcct ASC, AD_Table_ID, Record_ID, FACT_ACCT_ID ");

		try
		{
			pstmt = DB.prepareStatement(sql.toString(), null);
			int i = 1;
			pstmt.setInt(i++, p_C_AcctSchema_ID);
			pstmt.setString(i++, p_PostingType);
			pstmt.setTimestamp(i++, p_DateAcct_From);
			pstmt.setTimestamp(i++, p_DateAcct_To);

			if(p_AD_Org_ID > 0)
				pstmt.setInt(i++, p_AD_Org_ID);

			if(p_AD_Table_ID > 0)
				pstmt.setInt(i++, p_AD_Table_ID);

			if(p_Record_ID > 0)
				pstmt.setInt(i++, p_Record_ID);

			rs = pstmt.executeQuery();
			while(rs.next())
				factAcctList.add(new MFactAcct(getCtx(), rs, null));
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, e.toString());
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}

		MYayoiJournalJP m_YayoiJournal = null;
		MYayoiJournalJP last_YayoiJournal = null;
		MElementValue m_ElementValue = null;
		MTable m_Table = null;
		String tableNameTrl = null;
		PO document = null;
		int record_ID = 0;
		int counter = 0;
		int listSize = factAcctList.size();
		for(MFactAcct fa : factAcctList)
		{

			m_YayoiJournal = new MYayoiJournalJP(getCtx(), 0, get_TrxName());
			m_YayoiJournal.setAD_PInstance_ID(getAD_PInstance_ID());
			m_YayoiJournal.setAD_Org_ID(fa.getAD_Org_ID());
			m_YayoiJournal.setDateAcct(fa.getDateAcct());
			m_YayoiJournal.setPostingType(fa.getPostingType());
			m_YayoiJournal.setC_AcctSchema_ID(fa.getC_AcctSchema_ID());
			m_YayoiJournal.setFact_Acct_ID(fa.getFact_Acct_ID());
			m_YayoiJournal.setAD_Table_ID(fa.getAD_Table_ID());
			m_YayoiJournal.setRecord_ID(fa.getRecord_ID());

			//1:JP_Yayoi_IdentifierFlag
			if(record_ID != fa.getRecord_ID())
			{
				record_ID = fa.getRecord_ID();
				m_YayoiJournal.setJP_Yayoi_IdentifierFlag("2110");//First

				m_Table = MTable.get(getCtx(), fa.getAD_Table_ID());
				tableNameTrl = m_Table.get_Translation("Name");
				document = m_Table.getPO(fa.getRecord_ID(), get_TrxName());
				if(last_YayoiJournal != null && last_YayoiJournal.getJP_Yayoi_IdentifierFlag().equals("2100"))
				{
					last_YayoiJournal.setJP_Yayoi_IdentifierFlag("2101");//Last
					last_YayoiJournal.saveEx(get_TrxName());
				}
			}else {
				m_YayoiJournal.setJP_Yayoi_IdentifierFlag("2100");//Middle
			}

			//2:JP_Yayoi_DocNo

			//3:JP_Yayoi_Kessan

			//4:JP_Yayoi_DateAcct
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
			String JP_Yayoi_DateAcct =sdf.format(fa.getDateAcct());
			m_YayoiJournal.setJP_Yayoi_DateAcct(JP_Yayoi_DateAcct);

			//5:JP_Yayoi_DrAcct
			if(fa.getAmtAcctDr().signum() != 0)
			{
				m_ElementValue =  accountMap.get(fa.getAccount_ID());
				if(m_ElementValue.get_Value("JP_IF_Value") == null)
				{
					m_YayoiJournal.setJP_Yayoi_DrAcct(m_ElementValue.getValue());
				}else {
					m_YayoiJournal.setJP_Yayoi_DrAcct(m_ElementValue.get_ValueAsString("JP_IF_Value"));
				}
			}

			//6:JP_Yayoi_DrSubAcct

			//7:JP_Yayoi_DrOrg
			m_YayoiJournal.setJP_Yayoi_DrOrg(MOrg.get(getCtx(), fa.getAD_Org_ID()).getName());

			//8:JP_Yayoi_DrTax
			if(fa.getC_Tax_ID()==0)
			{
				m_YayoiJournal.setJP_Yayoi_DrTax(TAX_EXCLUDE);

			}else {

				if(fa.getAmtAcctDr().signum() == 0)
				{
					m_YayoiJournal.setJP_Yayoi_DrTax(TAX_EXCLUDE);

				}else {

					MTax tax = MTax.get(getCtx(), fa.getC_Tax_ID());
					if(tax.get_Value("JP_IF_Value") == null)
					{
						m_YayoiJournal.setJP_Yayoi_DrTax(TAX_EXCLUDE);

					}else {
						m_YayoiJournal.setJP_Yayoi_DrTax(tax.get_ValueAsString("JP_IF_Value"));
					}

				}
			}

			//9:JP_Yayoi_DrAmt
			m_YayoiJournal.setJP_Yayoi_DrAmt(fa.getAmtAcctDr().intValue());

			//10:JP_Yayoi_DrTaxAmt
			m_YayoiJournal.setJP_Yayoi_DrTaxAmt(0);

			//11:JP_Yayoi_CrAcct
			if(fa.getAmtAcctCr().signum() != 0)
			{
				m_ElementValue =  accountMap.get(fa.getAccount_ID());
				if(m_ElementValue.get_Value("JP_IF_Value") == null)
				{
					m_YayoiJournal.setJP_Yayoi_CrAcct(m_ElementValue.getValue());
				}else {
					m_YayoiJournal.setJP_Yayoi_CrAcct(m_ElementValue.get_ValueAsString("JP_IF_Value"));
				}
			}

			//12:JP_Yayoi_CrSubAcct

			//13:JP_Yayoi_CrOrg
			m_YayoiJournal.setJP_Yayoi_CrOrg(MOrg.get(getCtx(), fa.getAD_Org_ID()).getName());

			//14:JP_Yayoi_CrTax
			if(fa.getC_Tax_ID()==0)
			{
				m_YayoiJournal.setJP_Yayoi_CrTax(TAX_EXCLUDE);

			}else {

				if(fa.getAmtAcctCr().signum() == 0)
				{
					m_YayoiJournal.setJP_Yayoi_CrTax(TAX_EXCLUDE);

				}else {

					MTax tax = MTax.get(getCtx(), fa.getC_Tax_ID());
					if(tax.get_Value("JP_IF_Value") == null)
					{
						m_YayoiJournal.setJP_Yayoi_CrTax(TAX_EXCLUDE);

					}else {
						m_YayoiJournal.setJP_Yayoi_CrTax(tax.get_ValueAsString("JP_IF_Value"));
					}

				}
			}

			//15:JP_Yayoi_CrAmt
			m_YayoiJournal.setJP_Yayoi_CrAmt(fa.getAmtAcctCr().intValue());

			//16:JP_Yayoi_CrTaxAmt
			m_YayoiJournal.setJP_Yayoi_CrTaxAmt(0);

			//17:JP_Yayoi_Tekiyou
			if(document.get_ColumnIndex("DocumentNo") >= 0)
			{
				if(document.get_ColumnIndex("Description") >= 0 && !Util.isEmpty(document.get_ValueAsString("Description")))
				{
					m_YayoiJournal.setJP_Yayoi_Tekiyou(document.get_ValueAsString("DocumentNo") + "(" + tableNameTrl + ")"
														+" "+ document.get_ValueAsString("Description"));
				}else {
					m_YayoiJournal.setJP_Yayoi_Tekiyou(document.get_ValueAsString("DocumentNo") +"(" + tableNameTrl + ")");
				}

			}else if(document.get_ColumnIndex("Name") >= 0) {

				if(document.get_ColumnIndex("Description") >= 0 && !Util.isEmpty(document.get_ValueAsString("Description")))
				{
					m_YayoiJournal.setJP_Yayoi_Tekiyou(document.get_ValueAsString("Name") + "(" + tableNameTrl + ")"
																	+" " + document.get_ValueAsString("Description"));

				}else {
					m_YayoiJournal.setJP_Yayoi_Tekiyou(document.get_ValueAsString("Name")+ "(" + tableNameTrl + ")");
				}
			}else if(document.get_ColumnIndex("Description") >= 0) {
				m_YayoiJournal.setJP_Yayoi_Tekiyou("(" +tableNameTrl + ")" + " " + document.get_ValueAsString("Description"));
			}

			//18:JP_Yayoi_Bango
			m_YayoiJournal.setJP_Yayoi_Bango(String.valueOf(fa.getRecord_ID()));

			//19:JP_Yayoi_Kijitu

			//20:JP_Yayoi_Type
			m_YayoiJournal.setJP_Yayoi_Type(3);

			//21:JP_Yayoi_Seiseimoto

			//22:JP_Yayoi_Shiwakememo
			m_YayoiJournal.setJP_Yayoi_Shiwakememo(fa.getDescription());

			//23:JP_Yayoi_Fusen1

			//24:JP_Yayoi_Fusen2

			//25:JP_Yayoi_Chousei
			m_YayoiJournal.setJP_Yayoi_Chousei("no");

			last_YayoiJournal = m_YayoiJournal;
			counter++;

			if(counter==listSize)//All last
			{
				m_YayoiJournal.setJP_Yayoi_IdentifierFlag("2101");

			}else {

				if(processUI != null)
				{
					processUI.statusUpdate("Convert From iDempiere to Yayoi : "+ counter + " / " + listSize);
				}
			}

			m_YayoiJournal.saveEx(get_TrxName());
		}



		return Msg.getMsg(getCtx(), "Success") + " - " + Msg.getElement(getCtx(), "T_Yayoi_JournalJP_ID") + " : " +listSize;
	}

}
