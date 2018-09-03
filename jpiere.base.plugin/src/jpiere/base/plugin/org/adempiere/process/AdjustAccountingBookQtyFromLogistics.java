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
import java.util.HashMap;
import java.util.logging.Level;

import org.compiere.model.MAcctSchema;
import org.compiere.model.MCalendar;
import org.compiere.model.MConversionType;
import org.compiere.model.MDocType;
import org.compiere.model.MJournal;
import org.compiere.model.MJournalLine;
import org.compiere.model.MPeriod;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;


/**
 * JPIERE-0419 : Adjust Accounting Book Qty From Logistics
 *
 * @author Hideaki Hagiwara
 *
 */
public class AdjustAccountingBookQtyFromLogistics extends SvrProcess {


	private int p_AD_Client_ID = 0;
	private Timestamp p_DateValue = null;
	private int p_C_AcctSchema_ID = 0;
	private int p_AD_Org_ID = 0;
	private int p_M_Product_ID = 0;
	private int p_C_DocType_ID = 0;
	private String p_DocAction = DocAction.ACTION_Complete;
	private HashMap<Integer, MJournal> orgJournalMap = new HashMap<Integer, MJournal> ();//Key:AD_Org_ID Value:MJournal


	@Override
	protected void prepare()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
			else if (name.equals("DateValue"))
				p_DateValue = para[i].getParameterAsTimestamp();
			else if (name.equals("C_AcctSchema_ID"))
				p_C_AcctSchema_ID = para[i].getParameterAsInt();
			else if (name.equals("AD_Org_ID"))
				p_AD_Org_ID = para[i].getParameterAsInt();
			else if (name.equals("M_Product_ID"))
				p_M_Product_ID = para[i].getParameterAsInt();
			else if (name.equals("C_DocType_ID"))
				p_C_DocType_ID = para[i].getParameterAsInt();
			else if (name.equals("DocAction"))
				p_DocAction = para[i].getParameterAsString();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}

		p_AD_Client_ID = getAD_Client_ID();

	}

	@Override
	protected String doIt() throws Exception
	{

		doCheckFromJP_StockOrgToJP_InvOrgBalance();
		doCheckFromJP_InvOrgBalanceToJP_StockOrg();

		if(orgJournalMap.size()>0)
		{
			for (Integer key : orgJournalMap.keySet())
			{
			   MJournal Journal = orgJournalMap.get(key);
			   addBufferLog(0, null, null, Journal.getDocumentNo(), MJournal.Table_ID, Journal.getGL_Journal_ID());
			   if(!Util.isEmpty(p_DocAction))
			   {
				   Journal.processIt(p_DocAction);
				   Journal.saveEx(get_TrxName());
			   }
			}//for

		}else {

			return Msg.getMsg(getCtx(),"JP_NoDiff");

		}

		return Msg.getMsg(getCtx(),"Success");

	}

	private void doCheckFromJP_StockOrgToJP_InvOrgBalance() throws Exception
	{
		StringBuilder sql = new StringBuilder("SELECT AD_Org_ID, M_Product_ID, QtyBook ")//1 - 3
				.append("FROM JP_StockOrg ")
				.append("WHERE dateValue=? ");


		if(p_M_Product_ID != 0 )
		{
			sql.append(" AND M_Product_ID = ?");
		}


		if(p_AD_Org_ID != 0 )
		{
			sql.append(" AND AD_Org_ID = ?");
		}

		sql.append(" AND AD_Client_ID=? ");

		sql.append(" ORDER BY AD_Org_ID, M_Product_ID ");

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (sql.toString(), null);
			int i = 1;
			pstmt.setTimestamp(i, p_DateValue);
			i++;
			if(p_M_Product_ID != 0 )
			{
				pstmt.setInt(i, p_M_Product_ID);
				i++;
			}

			if(p_AD_Org_ID != 0 )
			{
				pstmt.setInt(i, p_AD_Org_ID);
				i++;
			}

			pstmt.setInt(i, p_AD_Client_ID);


			rs = pstmt.executeQuery ();

			int counter = 0;
			while (rs.next ())
			{
				counter++;

				int AD_Org_ID = rs.getInt(1);
				int M_Product_ID = rs.getInt(2);
				BigDecimal logisticsBookQty = rs.getBigDecimal(3);
				StringBuilder sql2 = new StringBuilder("SELECT Account_ID ,QtyBook ")//1 - 2
											.append("FROM JP_InvOrgBalance ")
											.append("WHERE C_AcctSchema_ID=? AND dateValue=? AND AD_Org_ID=? AND M_Product_ID=? ");

				PreparedStatement pstmt2 = null;
				ResultSet rs2 = null;
				try
				{
					pstmt2 = DB.prepareStatement (sql2.toString(), null);
					pstmt2.setInt(1, p_C_AcctSchema_ID);
					pstmt2.setTimestamp(2, p_DateValue);
					pstmt2.setInt(3, AD_Org_ID);
					pstmt2.setInt(4, M_Product_ID);
					rs2 = pstmt2.executeQuery ();
					if (rs2.next ())
					{
						int account_ID = rs2.getInt(1);
						BigDecimal accountBookQty = rs2.getBigDecimal(2);

						if(logisticsBookQty.compareTo(accountBookQty) != 0)
						{
							MJournal journal =  orgJournalMap.get(AD_Org_ID);
							if(journal == null)
								journal = createJournal(AD_Org_ID);

							MJournalLine line = new MJournalLine(journal);
							line.setM_Product_ID(M_Product_ID);
							line.setAccount_ID(account_ID);
							BigDecimal diffQty = logisticsBookQty.subtract(accountBookQty);
							line.setQty(diffQty);
							line.saveEx(get_TrxName());
						}

					}else {

						BigDecimal accountBookQty = Env.ZERO;
						if(logisticsBookQty.compareTo(accountBookQty) != 0)
						{
							int account_ID  =getP_Asset_Acct(M_Product_ID, p_C_AcctSchema_ID);
							if(account_ID > 0)
							{
								MJournal journal =  orgJournalMap.get(AD_Org_ID);
								if(journal == null)
									journal = createJournal(AD_Org_ID);

								MJournalLine line = new MJournalLine(journal);
								line.setM_Product_ID(M_Product_ID);

								line.setAccount_ID(account_ID);
								line.setQty(logisticsBookQty);
								line.saveEx(get_TrxName());
							}
						}
					}

				}catch (Exception e){
					throw e;
				}
				finally
				{
					DB.close(rs2, pstmt2);
					rs2 = null;
					pstmt2 = null;
				}

			}//while

			if(counter == 0)
				throw new Exception(Msg.getMsg(getCtx(), "not.found"));

		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

	}

	private void doCheckFromJP_InvOrgBalanceToJP_StockOrg() throws Exception
	{
		StringBuilder sql = new StringBuilder("SELECT AD_Org_ID, M_Product_ID, QtyBook, Account_ID ")//1 - 4
				.append("FROM JP_InvOrgBalance ")
				.append("WHERE C_AcctSchema_ID=? AND dateValue=? ");

		if(p_AD_Org_ID != 0 )
		{
			sql.append(" AND AD_Org_ID = ?");
		}

		if(p_M_Product_ID != 0 )
		{
			sql.append(" AND M_Product_ID = ?");
		}

		sql.append(" ORDER BY AD_Org_ID, M_Product_ID ");

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (sql.toString(), null);
			int i = 1;
			pstmt.setInt(i, p_C_AcctSchema_ID);
			i++;
			pstmt.setTimestamp(i, p_DateValue);
			i++;
			if(p_AD_Org_ID != 0 )
			{
				pstmt.setInt(i, p_AD_Org_ID);
				i++;
			}
			if(p_M_Product_ID != 0 )
			{
				pstmt.setInt(i, p_M_Product_ID);
			}

			rs = pstmt.executeQuery ();

			int counter = 0;
			while (rs.next ())
			{
				counter++;

				int AD_Org_ID = rs.getInt(1);
				int M_Product_ID = rs.getInt(2);
				BigDecimal accountBookQty = rs.getBigDecimal(3);
				int account_ID = rs.getInt(4);
				StringBuilder sql2 = new StringBuilder("SELECT QtyBook ")//1
											.append("FROM JP_StockOrg ")
											.append("WHERE dateValue=? AND M_Product_ID=? AND AD_Org_ID=? ");

				PreparedStatement pstmt2 = null;
				ResultSet rs2 = null;
				try
				{
					pstmt2 = DB.prepareStatement (sql2.toString(), null);
					pstmt2.setTimestamp(1, p_DateValue);
					pstmt2.setInt(2, M_Product_ID);
					pstmt2.setInt(3, AD_Org_ID);
					rs2 = pstmt2.executeQuery ();
					if (rs2.next ())
					{
						//Noting to do;

					}else {

						BigDecimal logisticsBookQty = Env.ZERO;
						if(logisticsBookQty.compareTo(accountBookQty) != 0)
						{
							if(account_ID > 0)
							{
								MJournal journal =  orgJournalMap.get(AD_Org_ID);
								if(journal == null)
									journal = createJournal(AD_Org_ID);

								MJournalLine line = new MJournalLine(journal);
								line.setM_Product_ID(M_Product_ID);

								line.setAccount_ID(account_ID);
								line.setQty(accountBookQty.negate());
								line.saveEx(get_TrxName());
							}
						}
					}

				}catch (Exception e){
					throw e;
				}
				finally
				{
					DB.close(rs2, pstmt2);
					rs2 = null;
					pstmt2 = null;
				}

			}//while

			if(counter == 0)
				throw new Exception(Msg.getMsg(getCtx(), "not.found"));

		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

	}


	private MDocType docType = null;
	private MAcctSchema acctSchema = null;
	private MPeriod period =null;

	private MJournal createJournal(int AD_Org_ID)
	{
		MJournal glj = new MJournal(getCtx(), 0, get_TrxName());
		glj.setAD_Org_ID(AD_Org_ID);
		glj.setC_DocType_ID(p_C_DocType_ID);
		if(docType == null)
			docType = new MDocType(getCtx(),p_C_DocType_ID, get_TrxName());
		glj.setGL_Category_ID(docType.getGL_Category_ID());
		glj.setC_ConversionType_ID(MConversionType.getDefault(p_AD_Client_ID));
		glj.setC_AcctSchema_ID(p_C_AcctSchema_ID);
		if(acctSchema == null)
			acctSchema = new MAcctSchema(getCtx(),p_C_AcctSchema_ID,get_TrxName());
		glj.setC_Currency_ID(acctSchema.getC_Currency_ID());
		glj.setPostingType("A");
		glj.setDateDoc(p_DateValue);
		glj.setDateAcct(p_DateValue);
		if(period == null)
		{
			MCalendar baseCalendar = MCalendar.getDefault(getCtx(), p_AD_Client_ID);
			period = MPeriod.findByCalendar(getCtx(),glj.getDateAcct(),baseCalendar.getC_Calendar_ID(), get_TrxName());
		}
		glj.setC_Period_ID(period.getC_Period_ID());
		glj.setDescription(getProcessInfo().getTitle());
		glj.saveEx(get_TrxName());

		orgJournalMap.put(AD_Org_ID, glj);

		return glj;
	}

	private int getP_Asset_Acct(int M_Product_ID, int p_C_AcctSchema_ID)
	{
		StringBuilder sql = new StringBuilder("SELECT vc.Account_ID FROM M_Product p ")//1
				.append(" INNER JOIN adempiere.M_Product_Acct pa ON (p.M_Product_ID=pa.M_Product_ID) ")
				.append(" INNER JOIN adempiere.C_ValidCombination vc ON (pa.P_Asset_Acct = vc.C_ValidCombination_ID )")
				.append(" WHERE p.M_Product_ID=? AND pa.C_AcctSchema_ID=?");

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (sql.toString(), null);
			pstmt.setInt(1, M_Product_ID);
			pstmt.setInt(2, p_C_AcctSchema_ID);
			rs = pstmt.executeQuery ();
			if (rs.next ())
			{
				int account_ID = rs.getInt(1);
				return account_ID;
			}

		}catch (Exception e){
//			throw e;
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

		return 0;
	}

}
