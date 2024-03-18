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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.logging.Level;

import org.adempiere.util.IProcessUI;
import org.compiere.acct.DocLine;
import org.compiere.acct.DocTax;
import org.compiere.acct.FactLine;
import org.compiere.model.MAccount;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MBPartner;
import org.compiere.model.MFactAcct;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MInvoiceTax;
import org.compiere.model.MJournal;
import org.compiere.model.MJournalLine;
import org.compiere.model.MProduct;
import org.compiere.model.ProductCost;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;

import jpiere.base.plugin.org.adempiere.model.MContractAcct;
import jpiere.base.plugin.org.adempiere.model.MContractChargeAcct;
import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractProductAcct;
import jpiere.base.plugin.org.adempiere.model.MContractTaxAcct;

/**
 * JPIERE-0539: 契約管理と振替仕訳伝票による費用収益の見越繰延と認識
 * 
 * 契約管理で、売上/仕入請求伝票と振替仕訳伝票を使用して費用/収益の見越繰延を行う場合に、
 * 振替仕訳伝票に売上/仕入請求伝票の税金情報を引き継ぐデータパッチプロセスです。
 *
 * @author h.hagiwara
 *
 */
public class ContractTaxInfoGLJournalDataPatch extends SvrProcess {

	private int p_C_AcctSchema_ID = 0;
	private Timestamp p_DateAcct_From = null;
	private Timestamp p_DateAcct_To = null;
	private boolean p_IsOverWrite = false;

	@Override
	protected void prepare()
	{
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
			}else if (name.equals("IsOverWrite")) {
				
				p_IsOverWrite = para[i].getParameterAsBoolean();
				
			}else{
				//log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}//if

		}

	}

	@Override
	protected String doIt() throws Exception
	{
		if(p_C_AcctSchema_ID == 0)
			throw new Exception("会計スキーマは必須です。");
		
		MAcctSchema m_AcctSchema = MAcctSchema.get(p_C_AcctSchema_ID);
		if(m_AcctSchema.isTradeDiscountPosted())
			throw new Exception("割引転記している場合には対応していません。");
		
		if(p_DateAcct_From == null || p_DateAcct_To == null)
			throw new Exception("転記日付は必須です。");
		
		MJournal[] m_Journals = getGLJournals();

		int JP_ContractContent_ID = 0;
		MContractContent m_ContractContent = null;
		int JP_Contract_Acct_ID = 0;
		MContractAcct m_ContractAcct = null;
		int JP_Invoice_ID = 0;
		MInvoice m_Invoice = null;
		MInvoiceLine[] m_InvoiceLines = null;
		MInvoiceTax[] m_InvoiceTaxes = null;
		MJournalLine[] m_JournalLines = null;

		MAccount m_AccountReverse = null;
		MAccount m_AccountTransfer = null;
		boolean isReversalGL = false;		//振替仕訳伝票が赤伝かどうかの判定

		IProcessUI processUI = Env.getProcessUI(getCtx());
		
		int countVoid = 0;
		for(MJournal m_Journal : m_Journals)
		{
			if(processUI != null)
				processUI.statusUpdate(m_Journal.getDocumentNo());
			
			//ボイドは貸借0円になっているはずなので無視。
			if(m_Journal.getDocStatus().equals(DocAction.STATUS_Voided))
			{
				countVoid++;
				addBufferLog(0, null, null, "ボイドされているので処理の対象外です : " + m_Journal.getDocumentNo() , MJournal.Table_ID, m_Journal.getGL_Journal_ID());
				continue;
			}
			
			//初期化
			isReversalGL = false; 
			m_ContractAcct = null;
			m_Invoice = null;
			m_InvoiceLines = null;
			m_InvoiceTaxes = null;
			m_JournalLines = m_Journal.getLines(true);
			
			//振替仕訳伝票が赤伝かどうかの判定。
			//伝票ステータスがリバースの場合、Reversal_IDに対応する伝票が入力されている。
			//GL_Journal_IDがReversal_IDより小さい場合は、赤伝を作成する対象となったもともとの黒伝と判断。
			//GL_Journal_IDがReversal_IDより大きい場合は、赤伝と判断。
			if(m_Journal.getDocStatus().equals(DocAction.STATUS_Reversed))
			{
				int GL_Journal_ID = m_Journal.getGL_Journal_ID ();
				int Reversal_ID = m_Journal.getReversal_ID();
				if(GL_Journal_ID > Reversal_ID)
				{
					isReversalGL = true;
				}else {
					isReversalGL = false;
				}
			}
			
			//売上/仕入請求伝票の取得
			JP_Invoice_ID = m_Journal.get_ValueAsInt("JP_Invoice_ID");
			m_Invoice = new MInvoice(getCtx(),JP_Invoice_ID, get_TrxName());
			
			if(!isReversalGL)//普通の振替仕訳伝票(売上/仕入請求伝票から作成された振替仕訳伝票)
			{
				/**売上/仕入請求伝票明細の税金情報を、振替仕訳伝票明細にセットする*/
	
				//契約会計情報の取得
				JP_ContractContent_ID = m_Journal.get_ValueAsInt(MContractContent.COLUMNNAME_JP_ContractContent_ID);
				m_ContractContent = new MContractContent(getCtx(),JP_ContractContent_ID, get_TrxName());
				JP_Contract_Acct_ID = m_ContractContent.getJP_Contract_Acct_ID();
				m_ContractAcct = MContractAcct.get(getCtx(), JP_Contract_Acct_ID);
				
				m_InvoiceLines = m_Invoice.getLines();
				m_InvoiceTaxes = m_Invoice.getTaxes(true);
				
				for(MJournalLine glLine : m_JournalLines)
				{
					if(glLine.get_ValueAsInt("JP_InvoiceLine_ID") == 0)//仮受消費税/仮払消費税の見越/繰延処理
					{
						for(MInvoiceTax iTax : m_InvoiceTaxes)
						{
							if(m_Invoice.isSOTrx())//売上請求伝票の仮受消費税の勘定科目取得
							{
								m_AccountTransfer = getJP_GL_TaxDue_Acct(m_Invoice, iTax, m_ContractAcct, m_AcctSchema);
								m_AccountReverse = getT_TaxDue_Acct(m_Invoice, iTax, m_ContractAcct, m_AcctSchema);
								
							}else {//仕入請求伝票の仮受消費税の勘定科目の取得
								
								if(iTax.getC_Tax().isSalesTax())
								{
									m_AccountTransfer = getJP_GL_TaxExpense_Acct(m_Invoice, iTax, m_ContractAcct, m_AcctSchema);
									m_AccountReverse = getT_TaxExpense_Acct(m_Invoice, iTax, m_ContractAcct, m_AcctSchema);
								}else {
									m_AccountTransfer = getJP_GL_TaxCredit_Acct(m_Invoice, iTax, m_ContractAcct, m_AcctSchema);
									m_AccountReverse = getT_TaxCredit_Acct(m_Invoice, iTax, m_ContractAcct, m_AcctSchema);
								}
							}
							
							//税金情報のセット - 前受/前払い計上している取り崩し対象となる勘定科目
							if(m_AccountReverse != null && glLine.getAccount_ID() == m_AccountReverse.getAccount_ID())
							{
								//１つの伝票で２つ以上の税金情報マスタが使用されている場合で、それらが同じ勘定科目を使用している場合、完全に仕訳を分別することは不可能。
								//金額で判断するしかないので、金額の絶対値で判断する。金額が同じになるケースはほぼ無いと思われるので・・・。
								BigDecimal taxAmt = iTax.getTaxAmt();
								if(taxAmt.abs().compareTo(glLine.getAmtSourceDr().abs()) == 0
										|| taxAmt.abs().compareTo(glLine.getAmtSourceCr().abs()) == 0)
								{
									if(p_IsOverWrite || glLine.get_Value("C_Tax_ID") == null)
									{
										glLine.set_ValueNoCheck("C_Tax_ID" , iTax.getC_Tax_ID());
										glLine.set_ValueNoCheck("JP_SOPOType" , "N");
										glLine.set_ValueNoCheck("JP_TaxBaseAmt" , iTax.getTaxBaseAmt().negate());
										glLine.set_ValueNoCheck("JP_TaxAmt" , iTax.getTaxAmt().negate());
										glLine.saveEx(get_TrxName());
									}
								}
							}
							
							//税金情報のセット - 振替先となる勘定科目
							if(m_AccountTransfer != null && glLine.getAccount_ID() == m_AccountTransfer.getAccount_ID())
							{	
								//１つの伝票で２つ以上の税金情報マスタが使用されている場合で、それらが同じ勘定科目を使用している場合、完全に仕訳を分別することは不可能。
								//金額で判断するしかないので、金額の絶対値で判断する。金額が同じになるケースはほぼ無いと思われるので・・・。
								BigDecimal taxAmt = iTax.getTaxAmt();
								if(taxAmt.abs().compareTo(glLine.getAmtSourceDr().abs()) == 0
										|| taxAmt.abs().compareTo(glLine.getAmtSourceCr().abs()) == 0)
								{
									if(p_IsOverWrite || glLine.get_Value("C_Tax_ID") == null)
									{
										glLine.set_ValueNoCheck("C_Tax_ID" , iTax.getC_Tax_ID());
										glLine.set_ValueNoCheck("JP_SOPOType" , "N");
										glLine.set_ValueNoCheck("JP_TaxBaseAmt" , iTax.getTaxBaseAmt());
										glLine.set_ValueNoCheck("JP_TaxAmt" , iTax.getTaxAmt());
										glLine.saveEx(get_TrxName());
									}
								}
							}
						}
						
					}else {//収益/費用の見越繰延処理
					
						for(MInvoiceLine iLine : m_InvoiceLines)
						{
							if(glLine.get_ValueAsInt("JP_InvoiceLine_ID") != iLine.getC_InvoiceLine_ID())
								continue;
							
							//振替仕訳伝票を作成する時に使用しているはずの勘定科目の取得
							if(m_Invoice.isSOTrx())//売上請求伝票
							{
								if(iLine.getM_Product_ID() > 0)
								{
									m_AccountReverse = getP_Revenue_Acct(m_Invoice, iLine, m_ContractAcct, m_AcctSchema);
									m_AccountTransfer = getJP_GL_Revenue_Acct(m_Invoice,iLine, m_ContractAcct, m_AcctSchema);
									
								}else if(iLine.getC_Charge_ID() > 0) {
									
									m_AccountReverse = getCh_Expense_Acct(m_Invoice, iLine, m_ContractAcct, m_AcctSchema);
									m_AccountTransfer = getJP_GL_Ch_Expense_Acct(m_Invoice,iLine, m_ContractAcct, m_AcctSchema);
								}
								
							}else {//仕入請求伝票
								
								if(iLine.getM_Product_ID() > 0)
								{
									m_AccountReverse = getP_Expense_Acct(m_Invoice,iLine, m_ContractAcct, m_AcctSchema);
									m_AccountTransfer = getJP_GL_Expense_Acct(m_Invoice,iLine, m_ContractAcct, m_AcctSchema);
	
								}else if(iLine.getC_Charge_ID() > 0) {
	
									m_AccountReverse = getCh_Expense_Acct(m_Invoice,iLine, m_ContractAcct, m_AcctSchema);
									m_AccountTransfer = getJP_GL_Ch_Expense_Acct(m_Invoice,iLine, m_ContractAcct, m_AcctSchema);
	
								}
							}
							
							//税金情報のセット - 前受/前払い計上している取り崩し対象となる勘定科目
							if(m_AccountReverse != null && glLine.getAccount_ID() == m_AccountReverse.getAccount_ID())
							{
								if(p_IsOverWrite || glLine.get_Value("C_Tax_ID") == null)
								{
									glLine.set_ValueNoCheck("C_Tax_ID" , iLine.getC_Tax_ID());
									glLine.set_ValueNoCheck("JP_SOPOType" , "N");
									Object obj_TaxBaseAmt = iLine.get_Value("JP_TaxBaseAmt");
									if(obj_TaxBaseAmt != null)
										glLine.set_ValueNoCheck("JP_TaxBaseAmt" , ((BigDecimal)obj_TaxBaseAmt).negate());
									
									Object obj_TaxAmt = iLine.get_Value("JP_TaxAmt");
									if(obj_TaxAmt != null)
										glLine.set_ValueNoCheck("JP_TaxAmt" , ((BigDecimal)obj_TaxAmt).negate());
									
									glLine.saveEx(get_TrxName());
								}
								
							}
							
							//税金情報のセット - 振替先となる勘定科目
							if(m_AccountTransfer != null && glLine.getAccount_ID() == m_AccountTransfer.getAccount_ID()) 
							{
								if(p_IsOverWrite || glLine.get_Value("C_Tax_ID") == null)
								{
									glLine.set_ValueNoCheck("C_Tax_ID" , iLine.getC_Tax_ID());
									glLine.set_ValueNoCheck("JP_SOPOType" , "N");
									Object obj_TaxBaseAmt = iLine.get_Value("JP_TaxBaseAmt");
									if(obj_TaxBaseAmt != null)
										glLine.set_ValueNoCheck("JP_TaxBaseAmt" , obj_TaxBaseAmt);
									
									Object obj_TaxAmt = iLine.get_Value("JP_TaxAmt");
									if(obj_TaxAmt != null)
										glLine.set_ValueNoCheck("JP_TaxAmt" , obj_TaxAmt);
									
									glLine.saveEx(get_TrxName());
								}
							}
							
						}//for(MInvoiceLine iLine : m_InvoiceLines)
					
					}//if(glLine.get_ValueAsInt("JP_InvoiceLine") == 0)
					
					//Fact_Acctに契約情報と税金情報をセット
					synchronizeFactAcct(m_Invoice, m_Journal, glLine);
					
				}//for(MJournalLine glLine : m_JournalLines)

				//振替仕訳伝票をリバースした時に作成する赤伝には契約情報を引き継いでいない可能性があるため契約情報をセットする。
				if(m_Journal.getReversal_ID() != 0)
				{
					int Reversal_ID = m_Journal.getReversal_ID();
					MJournal reverseGLJ = new MJournal(getCtx(),Reversal_ID, get_TrxName());
					if(p_IsOverWrite || (m_Journal.get_ValueAsInt("JP_Contract_ID") != 0 && reverseGLJ.get_ValueAsInt("JP_Contract_ID")==0))
					{
						if(m_Journal.get_ValueAsInt("JP_Contract_ID") != 0)
							reverseGLJ.set_ValueNoCheck("JP_Contract_ID", m_Journal.get_ValueAsInt("JP_Contract_ID"));
					}
					if(p_IsOverWrite || (m_Journal.get_ValueAsInt("JP_ContractContent_ID") != 0 && reverseGLJ.get_ValueAsInt("JP_ContractContent_ID")==0))
					{
						if(m_Journal.get_ValueAsInt("JP_ContractContent_ID") != 0)
							reverseGLJ.set_ValueNoCheck("JP_ContractContent_ID", m_Journal.get_ValueAsInt("JP_ContractContent_ID"));
					}
					if(p_IsOverWrite || (m_Journal.get_ValueAsInt("JP_ContractProcPeriod_ID") != 0 && reverseGLJ.get_ValueAsInt("JP_ContractProcPeriod_ID")==0))
					{
						if(m_Journal.get_ValueAsInt("JP_ContractProcPeriod_ID") != 0)
							reverseGLJ.set_ValueNoCheck("JP_ContractProcPeriod_ID", m_Journal.get_ValueAsInt("JP_ContractProcPeriod_ID"));
					}
					if(p_IsOverWrite || (m_Journal.get_ValueAsInt("JP_Order_ID") != 0 && reverseGLJ.get_ValueAsInt("JP_Order_ID")==0))
					{
						if(m_Journal.get_ValueAsInt("JP_Order_ID") != 0)
							reverseGLJ.set_ValueNoCheck("JP_Order_ID", m_Journal.get_ValueAsInt("JP_Order_ID"));
					}
					if(p_IsOverWrite || (m_Journal.get_ValueAsInt("JP_Invoice_ID") != 0 && reverseGLJ.get_ValueAsInt("JP_Invoice_ID")==0))
					{
						if(m_Journal.get_ValueAsInt("JP_Invoice_ID") != 0)
							reverseGLJ.set_ValueNoCheck("JP_Invoice_ID", m_Journal.get_ValueAsInt("JP_Invoice_ID"));
					}
					reverseGLJ.saveEx(get_TrxName());
					
					//税金情報を引き継ぐ
					updateReverseGLLines(m_Invoice, m_Journal , reverseGLJ);
				}
				
			}else {//リバースされて赤伝として作成された振替仕訳伝票の対応

				/**リバース元となった振替仕訳伝票を取得してそこから税金情報をセットする。*/
				m_Journal.load(get_TrxName());
				int Reversal_ID = m_Journal.getReversal_ID();
				MJournal originalGLJ = new MJournal(getCtx(),Reversal_ID, get_TrxName());
				updateReverseGLLines(m_Invoice, originalGLJ , m_Journal);
				
			}//if(!isReversalGL)

		}//for(MJournal m_Journal : m_Journals)

		if(countVoid > 0)
		{
			return "プロセスは成功しました。処理対象となった振替仕訳伝票は " + m_Journals.length +" 件です。"
					+ "ボイドになっている処理対象の振替仕訳伝票が " + countVoid + " 件あります。確認して下さい。";
		}
		
		return "プロセスは成功しました。処理対象となった振替仕訳伝票は " + m_Journals.length +" 件です。";
	}
	
	private void updateReverseGLLines(MInvoice m_Invoice, MJournal originalGLJ , MJournal reverseGLJ ) throws Exception
	{
		/**リバース元となった振替仕訳伝票を取得してそこから税金情報をセットする。*/
		MJournalLine[] originalGLJLines = originalGLJ.getLines(true);
		MJournalLine[] reverseGLJLines = reverseGLJ.getLines(true);
		for(int i = 0; i < originalGLJLines.length; i++)
		{		
			if(p_IsOverWrite || (originalGLJLines[i].get_ValueAsInt("C_Tax_ID")!= 0 && reverseGLJLines[i].get_ValueAsInt("C_Tax_ID")== 0) )
			{
				reverseGLJLines[i].set_ValueNoCheck("C_Tax_ID" , originalGLJLines[i].get_ValueAsInt("C_Tax_ID") == 0 ? null: originalGLJLines[i].get_ValueAsInt("C_Tax_ID"));
			}

			if(p_IsOverWrite || (originalGLJLines[i].get_Value("JP_SOPOType")!= null && reverseGLJLines[i].get_Value("JP_SOPOType")== null) )
			{
				reverseGLJLines[i].set_ValueNoCheck("JP_SOPOType" , originalGLJLines[i].get_Value("JP_SOPOType"));
			}
			
			if(p_IsOverWrite || originalGLJLines[i].get_Value("JP_TaxBaseAmt")!= null)
			{
				Object obj_TaxBaseAmt = originalGLJLines[i].get_Value("JP_TaxBaseAmt");
				if(obj_TaxBaseAmt != null)
				{
					reverseGLJLines[i].set_ValueNoCheck("JP_TaxBaseAmt" , ((BigDecimal)obj_TaxBaseAmt).negate());
				}
			}
			
			if(p_IsOverWrite || originalGLJLines[i].get_Value("JP_TaxAmt")!= null)
			{
				Object obj_TaxAmt = originalGLJLines[i].get_Value("JP_TaxAmt");
				if(obj_TaxAmt != null)
				{
					reverseGLJLines[i].set_ValueNoCheck("JP_TaxAmt" , ((BigDecimal)obj_TaxAmt).negate());
				}
			}

			reverseGLJLines[i].saveEx(get_TrxName());
			synchronizeFactAcct(m_Invoice, reverseGLJ, reverseGLJLines[i]);//Fact_Acctに契約情報と税金情報をセット
			
		}//for
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

		String sql = "SELECT * FROM GL_Journal WHERE DateAcct >= ? AND DateAcct < ? AND JP_Invoice_ID IS NOT NULL AND JP_Contract_ID IS NOT NULL  AND JP_ContractContent_ID IS NOT NULL"
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
	
	/**
	 * 売上/仕入請求伝票の仕訳(Fact_Acct)の取得
	 * 
	 * @param C_AcctSchema_ID
	 * @param C_Invoice_ID
	 * @return
	 */
	@SuppressWarnings("unused")
	private MFactAcct[] getInvoiceFactAcct(int C_AcctSchema_ID, int C_Invoice_ID)
	{
		ArrayList<MFactAcct> list = new ArrayList<MFactAcct>();
		String sql = "SELECT * FROM Fact_Acct WHERE C_AcctSchema_ID=? AND AD_Table_ID=" + MInvoice.Table_ID + " AND Record_ID=? ORDER BY Fact_Acct_ID ASC ";

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, C_AcctSchema_ID);
			pstmt.setInt(2, C_Invoice_ID);
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add(new MFactAcct (getCtx(), rs, get_TrxName()));
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

		MFactAcct[] m_FactAccts = new MFactAcct[list.size()];
		list.toArray(m_FactAccts);
		return m_FactAccts;
	}
	
	/**
	 * 振替仕訳伝票の仕訳(Fact_Acct)の取得
	 * 
	 * @param C_AcctSchema_ID
	 * @param GL_Journal_ID
	 * @param GL_JournalLine_ID
	 * @return
	 */
	private MFactAcct[] getGLJournalLineFactAcct(int C_AcctSchema_ID, int GL_Journal_ID, int GL_JournalLine_ID)
	{
		ArrayList<MFactAcct> list = new ArrayList<MFactAcct>();
		String sql = "SELECT * FROM Fact_Acct WHERE C_AcctSchema_ID=? AND AD_Table_ID=" + MJournal.Table_ID + " AND Record_ID=? AND Line_ID=? ORDER BY Fact_Acct_ID ASC ";

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, C_AcctSchema_ID);
			pstmt.setInt(2, GL_Journal_ID);
			pstmt.setInt(3, GL_JournalLine_ID);
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add(new MFactAcct (getCtx(), rs, get_TrxName()));
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

		MFactAcct[] m_FactAccts = new MFactAcct[list.size()];
		list.toArray(m_FactAccts);
		return m_FactAccts;
	}
	
	/**
	 * 振替仕訳伝票明細の税金情報と同じようになるように仕訳の税金情報を同期します。
	 * 
	 * @param m_Invoice
	 * @param m_Journal
	 * @param glLine
	 * @throws Exception
	 */
	private void synchronizeFactAcct(MInvoice m_Invoice, MJournal m_Journal, MJournalLine glLine) throws Exception
	{
		MFactAcct[] m_FactAccts = getGLJournalLineFactAcct(m_Journal.getC_AcctSchema_ID(), glLine.getGL_Journal_ID(), glLine.getGL_JournalLine_ID());
		if(m_FactAccts.length > 1)
			throw new Exception("振替仕訳伝票から起票されている仕訳が1明細から複数存在しました。想定外です。"
					+ " 振替仕訳伝票 : "+m_Journal.getDocumentNo() + " - 明細番号 : " + glLine.getLine());
		
		if(m_FactAccts.length == 0)//まだ転記されていない場合
			return ;
		
		MFactAcct m_FactAcct = m_FactAccts[0];//1行だけのはず
		if(m_Journal.get_ValueAsInt("JP_Contract_ID") != 0)
		{
			m_FactAcct.set_ValueNoCheck("JP_Contract_ID", m_Journal.get_ValueAsInt("JP_Contract_ID"));
		}else {
			m_FactAcct.set_ValueNoCheck("JP_Contract_ID", null);
		}
		
		if(m_Journal.get_ValueAsInt("JP_ContractContent_ID") != 0)
		{
			m_FactAcct.set_ValueNoCheck("JP_ContractContent_ID", m_Journal.get_ValueAsInt("JP_ContractContent_ID"));
		}else {
			m_FactAcct.set_ValueNoCheck("JP_ContractContent_ID", null);
		}
		
		if(m_Journal.get_ValueAsInt("JP_ContractProcPeriod_ID") != 0)
		{
			m_FactAcct.set_ValueNoCheck("JP_ContractProcPeriod_ID", m_Journal.get_ValueAsInt("JP_ContractProcPeriod_ID"));
		}else {
			m_FactAcct.set_ValueNoCheck("JP_ContractProcPeriod_ID", null);
		}
		
		if(m_Journal.get_ValueAsInt("JP_Order_ID") != 0)
		{
			m_FactAcct.set_ValueNoCheck("JP_Order_ID", m_Journal.get_ValueAsInt("JP_Order_ID"));
		}else {
			m_FactAcct.set_ValueNoCheck("JP_Order_ID", null);
		}
		
		if(m_Journal.get_ValueAsInt("JP_Invoice_ID") != 0)
		{
			m_FactAcct.set_ValueNoCheck("JP_Invoice_ID", m_Journal.get_ValueAsInt("JP_Invoice_ID"));
		}else {
			m_FactAcct.set_ValueNoCheck("JP_Invoice_ID", null);
		}
		
			
		if(glLine.get_ValueAsInt("C_Tax_ID") == 0)
		{
			m_FactAcct.set_ValueNoCheck("C_Tax_ID" , null);
			m_FactAcct.set_ValueNoCheck("JP_SOPOType" , null);
			m_FactAcct.set_ValueNoCheck("JP_TaxBaseAmt" , Env.ZERO);
			m_FactAcct.set_ValueNoCheck("JP_TaxAmt" , Env.ZERO);
			m_FactAcct.set_ValueOfColumn("IsQualifiedInvoiceIssuerJP", false);
			m_FactAcct.set_ValueOfColumn("JP_RegisteredNumberOfQII", null);
			
		}else {	
			
			m_FactAcct.set_ValueNoCheck("C_Tax_ID" , glLine.get_ValueAsInt("C_Tax_ID"));
			m_FactAcct.set_ValueNoCheck("JP_SOPOType" , glLine.get_Value("JP_SOPOType"));
			m_FactAcct.set_ValueNoCheck("JP_TaxBaseAmt" , glLine.get_Value("JP_TaxBaseAmt"));
			m_FactAcct.set_ValueNoCheck("JP_TaxAmt" , glLine.get_Value("JP_TaxAmt"));
			
			//適格請求書発行事業者の情報登録
			if(!m_Invoice.isSOTrx())
			{
				MBPartner m_BP = MBPartner.get(getCtx(), m_Invoice.getC_BPartner_ID());
				Object JP_RegisteredDateOfQII = m_BP.get_Value("JP_RegisteredDateOfQII");
				if(JP_RegisteredDateOfQII == null)
				{
					boolean IsQualifiedInvoiceIssuerJP = m_BP.get_ValueAsBoolean("IsQualifiedInvoiceIssuerJP");
					m_FactAcct.set_ValueOfColumn("IsQualifiedInvoiceIssuerJP", IsQualifiedInvoiceIssuerJP);
					
					String JP_RegisteredNumberOfQII = m_BP.get_ValueAsString("JP_RegisteredNumberOfQII");
					m_FactAcct.set_ValueOfColumn("JP_RegisteredNumberOfQII", JP_RegisteredNumberOfQII);
					
				}else {
					
					Timestamp ts_JP_RegisteredDateOfQII =(Timestamp)JP_RegisteredDateOfQII;
					if(m_FactAcct.getDateAcct().compareTo(ts_JP_RegisteredDateOfQII) >= 0)
					{
						boolean IsQualifiedInvoiceIssuerJP = m_BP.get_ValueAsBoolean("IsQualifiedInvoiceIssuerJP");
						m_FactAcct.set_ValueOfColumn("IsQualifiedInvoiceIssuerJP", IsQualifiedInvoiceIssuerJP);
						
						String JP_RegisteredNumberOfQII = m_BP.get_ValueAsString("JP_RegisteredNumberOfQII");
						m_FactAcct.set_ValueOfColumn("JP_RegisteredNumberOfQII", JP_RegisteredNumberOfQII);									
					}
					
				}
				
			}//適格請求書発行事業者の情報登録
		}//if(glLine.get_ValueAsInt("C_Tax_ID") == 0)
		
		m_FactAcct.saveEx(get_TrxName());
			
	}
	
	/********************************************************************************************************************************************/
	/********************************************************************************************************************************************/
	/****************************************【自動仕訳の勘定科目を取得する処理です】******************************************************************/
	/********************************************************************************************************************************************/
	/********************************************************************************************************************************************/
	private MAccount getJP_GL_Revenue_Acct(MInvoice m_Invoice, MInvoiceLine m_InvoiceLine, MContractAcct m_ContractAcct, MAcctSchema m_AcctSchema)
	{
		int M_Product_ID = m_InvoiceLine.getM_Product_ID();
		if(M_Product_ID > 0)
		{
			MProduct m_Product = MProduct.get(M_Product_ID);
			if(MProduct.PRODUCTTYPE_Item.equals(m_Product.getProductType()))
			{
				return null;
			}

			MContractProductAcct m_ContractProductAcct = m_ContractAcct.getContractProductAcct(m_Product.getM_Product_Category_ID(), m_AcctSchema.getC_AcctSchema_ID(), false);
			if(m_ContractProductAcct == null)
				return null;

			int JP_GL_Revenue_Acct = m_ContractProductAcct.getJP_GL_Revenue_Acct();
			if(JP_GL_Revenue_Acct == 0)
				return null;

			return MAccount.get(m_Invoice.getCtx(), JP_GL_Revenue_Acct);

		}

		return null;
	}

	@SuppressWarnings("unused")
	private MAccount getJP_GL_TradeDiscountGrant_Acct(MInvoice m_Invoice, MInvoiceLine m_InvoiceLine, MContractAcct m_ContractAcct, MAcctSchema m_AcctSchema, FactLine factLine)
	{
		int M_Product_ID = m_InvoiceLine.getM_Product_ID();
		if(M_Product_ID > 0)
		{
			MProduct m_Product = MProduct.get(M_Product_ID);
			if(MProduct.PRODUCTTYPE_Item.equals(m_Product.getProductType()))
			{
				return null;
			}

			MContractProductAcct m_ContractProductAcct = m_ContractAcct.getContractProductAcct(m_Product.getM_Product_Category_ID(), m_AcctSchema.getC_AcctSchema_ID(), false);
			if(m_ContractProductAcct == null)
				return null;

			int JP_GL_TradeDiscountGrant_Acct = m_ContractProductAcct.getJP_GL_TradeDiscountGrant_Acct();
			if(JP_GL_TradeDiscountGrant_Acct == 0)
				return getP_TradeDiscountGrant_Acct(m_Invoice, m_InvoiceLine, m_ContractAcct, m_AcctSchema);

			return MAccount.get(m_Invoice.getCtx(), JP_GL_TradeDiscountGrant_Acct);

		}

		return factLine.getAccount();
	}

	private MAccount getP_Revenue_Acct(MInvoice m_Invoice, MInvoiceLine m_InvoiceLine, MContractAcct m_ContractAcct, MAcctSchema m_AcctSchema)
	{
		int M_Product_ID = m_InvoiceLine.getM_Product_ID();
		if(M_Product_ID > 0)
		{
			MProduct m_Product = MProduct.get(M_Product_ID);
			if(MProduct.PRODUCTTYPE_Item.equals(m_Product.getProductType()))
			{
				return null;
			}

			MContractProductAcct m_ContractProductAcct = m_ContractAcct.getContractProductAcct(m_Product.getM_Product_Category_ID(), m_AcctSchema.getC_AcctSchema_ID(), false);
			if(m_ContractProductAcct == null)
				return null;

			int P_Revenue_Acct = m_ContractProductAcct.getP_Revenue_Acct();
			if(P_Revenue_Acct == 0)
			{
				//Get Default Account
				return getProductCost(m_InvoiceLine).getAccount(ProductCost.ACCTTYPE_P_Revenue,m_AcctSchema);
			}

			return MAccount.get(m_Invoice.getCtx(), P_Revenue_Acct);

		}

		return null;
	}

	private MAccount getP_TradeDiscountGrant_Acct(MInvoice m_Invoice, MInvoiceLine m_InvoiceLine, MContractAcct m_ContractAcct, MAcctSchema m_AcctSchema)
	{
		if(m_InvoiceLine.getM_Product_ID() > 0)
		{
			MContractProductAcct contractProductAcct = m_ContractAcct.getContractProductAcct(m_InvoiceLine.getM_Product().getM_Product_Category_ID(), m_AcctSchema.getC_AcctSchema_ID(), false);
			if(contractProductAcct != null && contractProductAcct.getP_TradeDiscountGrant_Acct() > 0)
			{
				return MAccount.get(m_Invoice.getCtx(),contractProductAcct.getP_TradeDiscountGrant_Acct());
			}else{

				DocLine docLine = new DocLine (m_InvoiceLine , null);
				return docLine.getAccount(ProductCost.ACCTTYPE_P_TDiscountGrant, m_AcctSchema);
			}

		}else {

			return MAccount.get(m_Invoice.getCtx(), m_AcctSchema.getAcctSchemaDefault().getP_TradeDiscountGrant_Acct());
		}

	}

	private MAccount getJP_GL_Expense_Acct(MInvoice m_Invoice, MInvoiceLine m_InvoiceLine, MContractAcct m_ContractAcct, MAcctSchema m_AcctSchema)
	{
		int M_Product_ID = m_InvoiceLine.getM_Product_ID();
		if(M_Product_ID > 0)
		{
			MProduct m_Product = MProduct.get(M_Product_ID);
			if(MProduct.PRODUCTTYPE_Item.equals(m_Product.getProductType()))
			{
				return null;
			}

			MContractProductAcct m_ContractProductAcct = m_ContractAcct.getContractProductAcct(m_Product.getM_Product_Category_ID(), m_AcctSchema.getC_AcctSchema_ID(), false);
			if(m_ContractProductAcct == null)
				return null;

			int JP_GL_Expense_Acct = m_ContractProductAcct.getJP_GL_Expense_Acct();
			if(JP_GL_Expense_Acct == 0)
				return null;

			return MAccount.get(m_Invoice.getCtx(), JP_GL_Expense_Acct);

		}
		return null;
	}

	private MAccount getP_Expense_Acct(MInvoice m_Invoice, MInvoiceLine m_InvoiceLine, MContractAcct m_ContractAcct, MAcctSchema m_AcctSchema)
	{
		int M_Product_ID = m_InvoiceLine.getM_Product_ID();
		if(M_Product_ID > 0)
		{
			MProduct m_Product = MProduct.get(M_Product_ID);
			if(MProduct.PRODUCTTYPE_Item.equals(m_Product.getProductType()))
			{
				return null;
			}

			MContractProductAcct m_ContractProductAcct = m_ContractAcct.getContractProductAcct(m_Product.getM_Product_Category_ID(), m_AcctSchema.getC_AcctSchema_ID(), false);
			if(m_ContractProductAcct == null)
				return null;

			int P_Expense_Acct = m_ContractProductAcct.getP_Expense_Acct();
			if(P_Expense_Acct == 0)
			{
				//Get Default Account
				return getProductCost(m_InvoiceLine).getAccount(ProductCost.ACCTTYPE_P_Expense, m_AcctSchema);
			}

			return MAccount.get(m_Invoice.getCtx(), P_Expense_Acct);

		}

		return null;
	}

	private MAccount getP_TradeDiscountRec_Acct(MInvoice m_Invoice, MInvoiceLine m_InvoiceLine, MContractAcct m_ContractAcct, MAcctSchema m_AcctSchema)
	{
		if(m_InvoiceLine.getM_Product_ID() > 0)
		{
			MContractProductAcct contractProductAcct = m_ContractAcct.getContractProductAcct(m_InvoiceLine.getM_Product().getM_Product_Category_ID(), m_AcctSchema.getC_AcctSchema_ID(), false);
			if(contractProductAcct != null && contractProductAcct.getP_TradeDiscountRec_Acct() > 0)
			{
				return MAccount.get(m_Invoice.getCtx(),contractProductAcct.getP_TradeDiscountRec_Acct());
			}else{

				DocLine docLine = new DocLine (m_InvoiceLine , null);
				return docLine.getAccount(ProductCost.ACCTTYPE_P_TDiscountRec, m_AcctSchema);
			}

		}else {

			return MAccount.get(m_Invoice.getCtx(), m_AcctSchema.getAcctSchemaDefault().getP_TradeDiscountRec_Acct());
		}

	}

	@SuppressWarnings("unused")
	private MAccount getJP_GL_TradeDiscountRec_Acct(MInvoice m_Invoice, MInvoiceLine m_InvoiceLine, MContractAcct m_ContractAcct, MAcctSchema m_AcctSchema, FactLine factLine)
	{
		int M_Product_ID = m_InvoiceLine.getM_Product_ID();
		if(M_Product_ID > 0)
		{
			MProduct m_Product = MProduct.get(M_Product_ID);
			if(MProduct.PRODUCTTYPE_Item.equals(m_Product.getProductType()))
			{
				return null;
			}

			MContractProductAcct m_ContractProductAcct = m_ContractAcct.getContractProductAcct(m_Product.getM_Product_Category_ID(), m_AcctSchema.getC_AcctSchema_ID(), false);
			if(m_ContractProductAcct == null)
				return null;

			int JP_GL_TradeDiscountRec_Acct = m_ContractProductAcct.getJP_GL_TradeDiscountRec_Acct();
			if(JP_GL_TradeDiscountRec_Acct == 0)
				return getP_TradeDiscountRec_Acct(m_Invoice, m_InvoiceLine, m_ContractAcct, m_AcctSchema);

			return MAccount.get(m_Invoice.getCtx(), JP_GL_TradeDiscountRec_Acct);

		}

		return factLine.getAccount();
	}

	private MAccount getJP_GL_Ch_Expense_Acct(MInvoice m_Invoice, MInvoiceLine m_InvoiceLine, MContractAcct m_ContractAcct, MAcctSchema m_AcctSchema)
	{
		MContractChargeAcct m_ContractChargeAcct = m_ContractAcct.getContracChargeAcct(m_InvoiceLine.getC_Charge_ID(), m_AcctSchema.getC_AcctSchema_ID(), false);
		if(m_ContractChargeAcct == null)
			return null;

		int JP_GL_Ch_Expense_Acct = m_ContractChargeAcct.getJP_GL_Ch_Expense_Acct();
		if(JP_GL_Ch_Expense_Acct == 0)
			return null;

		return MAccount.get(m_Invoice.getCtx(), JP_GL_Ch_Expense_Acct);
	}

	private MAccount getCh_Expense_Acct(MInvoice m_Invoice, MInvoiceLine m_InvoiceLine, MContractAcct m_ContractAcct, MAcctSchema m_AcctSchema)
	{
		MContractChargeAcct m_ContractChargeAcct = m_ContractAcct.getContracChargeAcct(m_InvoiceLine.getC_Charge_ID(), m_AcctSchema.getC_AcctSchema_ID(), false);
		if(m_ContractChargeAcct == null || m_ContractChargeAcct.getCh_Expense_Acct() == 0)
		{
			//Get Default Account
			return getProductCost(m_InvoiceLine).getAccount(ProductCost.ACCTTYPE_P_Expense, m_AcctSchema);

		}else {

			return MAccount.get(m_Invoice.getCtx(), m_ContractChargeAcct.getCh_Expense_Acct());

		}
	}

	private MAccount getJP_GL_TaxDue_Acct(MInvoice m_Invoice, MInvoiceTax m_InvoiceTax, MContractAcct m_ContractAcct, MAcctSchema m_AcctSchema)
	{
		MContractTaxAcct m_ContractTaxAcct = m_ContractAcct.getContracTaxAcct(m_InvoiceTax.getC_Tax_ID(), m_AcctSchema.getC_AcctSchema_ID(),false);
		if(m_ContractTaxAcct == null)
			return null;

		int JP_GL_TaxDue_Acct = m_ContractTaxAcct.getJP_GL_TaxDue_Acct();
		if(JP_GL_TaxDue_Acct == 0)
			return null;

		return MAccount.get(m_Invoice.getCtx(), JP_GL_TaxDue_Acct);
	}

	private MAccount getT_TaxDue_Acct(MInvoice m_Invoice, MInvoiceTax m_InvoiceTax, MContractAcct m_ContractAcct, MAcctSchema m_AcctSchema)
	{
		MContractTaxAcct m_ContractTaxAcct = m_ContractAcct.getContracTaxAcct(m_InvoiceTax.getC_Tax_ID(), m_AcctSchema.getC_AcctSchema_ID(),false);
		if(m_ContractTaxAcct != null && m_ContractTaxAcct.getT_Due_Acct() > 0)
		{
			return MAccount.get(m_Invoice.getCtx(), m_ContractTaxAcct.getT_Due_Acct());

		}else{

			DocTax docTax = new DocTax (m_InvoiceTax.getC_Tax_ID(), "", Env.ZERO, Env.ZERO, Env.ZERO, m_Invoice.isSOTrx());
			return docTax.getAccount(DocTax.ACCTTYPE_TaxDue, m_AcctSchema);
		}
	}

	private MAccount getJP_GL_TaxCredit_Acct(MInvoice m_Invoice, MInvoiceTax m_InvoiceTax, MContractAcct m_ContractAcct, MAcctSchema m_AcctSchema)
	{
		MContractTaxAcct m_ContractTaxAcct = m_ContractAcct.getContracTaxAcct(m_InvoiceTax.getC_Tax_ID(), m_AcctSchema.getC_AcctSchema_ID(),false);
		if(m_ContractTaxAcct == null)
			return null;

		int JP_GL_TaxCredit_Acct = m_ContractTaxAcct.getJP_GL_TaxCredit_Acct();
		if(JP_GL_TaxCredit_Acct == 0)
			return null;

		return MAccount.get(m_Invoice.getCtx(), JP_GL_TaxCredit_Acct);
	}

	private MAccount getT_TaxCredit_Acct(MInvoice m_Invoice, MInvoiceTax m_InvoiceTax, MContractAcct m_ContractAcct, MAcctSchema m_AcctSchema)
	{
		MContractTaxAcct m_ContractTaxAcct = m_ContractAcct.getContracTaxAcct(m_InvoiceTax.getC_Tax_ID(), m_AcctSchema.getC_AcctSchema_ID(),false);
		if(m_ContractTaxAcct != null && m_ContractTaxAcct.getT_Credit_Acct() > 0)
		{
			return MAccount.get(m_Invoice.getCtx(), m_ContractTaxAcct.getT_Credit_Acct());

		}else{

			DocTax docTax = new DocTax (m_InvoiceTax.getC_Tax_ID(), "", Env.ZERO, Env.ZERO, Env.ZERO, m_Invoice.isSOTrx());
			return docTax.getAccount(DocTax.ACCTTYPE_TaxCredit, m_AcctSchema);
		}
	}

	private MAccount getJP_GL_TaxExpense_Acct(MInvoice m_Invoice, MInvoiceTax m_InvoiceTax, MContractAcct m_ContractAcct, MAcctSchema m_AcctSchema)
	{
		MContractTaxAcct m_ContractTaxAcct = m_ContractAcct.getContracTaxAcct(m_InvoiceTax.getC_Tax_ID(), m_AcctSchema.getC_AcctSchema_ID(),false);
		if(m_ContractTaxAcct == null)
			return null;

		int JP_GL_TaxExpense_Acct = m_ContractTaxAcct.getJP_GL_TaxExpense_Acct();
		if(JP_GL_TaxExpense_Acct == 0)
			return null;

		return MAccount.get(m_Invoice.getCtx(), JP_GL_TaxExpense_Acct);
	}

	private MAccount getT_TaxExpense_Acct(MInvoice m_Invoice, MInvoiceTax m_InvoiceTax, MContractAcct m_ContractAcct, MAcctSchema m_AcctSchema)
	{
		MContractTaxAcct m_ContractTaxAcct = m_ContractAcct.getContracTaxAcct(m_InvoiceTax.getC_Tax_ID(), m_AcctSchema.getC_AcctSchema_ID(),false);
		if(m_ContractTaxAcct != null && m_ContractTaxAcct.getT_Expense_Acct() > 0)
		{
			return MAccount.get(m_Invoice.getCtx(), m_ContractTaxAcct.getT_Expense_Acct());

		}else{

			DocTax docTax = new DocTax (m_InvoiceTax.getC_Tax_ID(), "", Env.ZERO, Env.ZERO, Env.ZERO, m_Invoice.isSOTrx());
			return docTax.getAccount(DocTax.ACCTTYPE_TaxExpense, m_AcctSchema);
		}
	}
	
	private ProductCost getProductCost(MInvoiceLine m_InvoiceLine)
	{
		ProductCost	m_productCost = new ProductCost (Env.getCtx(),
					m_InvoiceLine.getM_Product_ID(), m_InvoiceLine.getM_AttributeSetInstance_ID(), m_InvoiceLine.get_TrxName());

		return m_productCost;
	}	//	getProductCost
}
