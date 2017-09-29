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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import org.compiere.model.MClient;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Msg;
import org.compiere.util.Trx;

import jpiere.base.plugin.org.adempiere.model.MContract;
import jpiere.base.plugin.org.adempiere.model.MContractCancelTerm;
import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractExtendPeriod;
import jpiere.base.plugin.org.adempiere.model.MContractLog;
import jpiere.base.plugin.org.adempiere.model.MContractLogDetail;


/** 
* JPIERE-0363
*
* @author Hideaki Hagiwara
*
*/
public class ContractStatusUpdate extends SvrProcess {
	
	
	private int         p_AD_Org_ID = 0;
	private int			p_JP_ContractCategoryL2_ID = 0;
	private int			p_JP_ContractCategoryL1_ID = 0;
	private int			p_JP_ContractCategory_ID = 0;
	
	volatile static HashMap<Integer, Boolean> processingNow = null;
	
	//Contract Log
	private Trx conractLogTrx = null;
	private MContractLog m_ContractLog = null;
	
	private String p_JP_ContractProcessTraceLevel = "TBC";
	private boolean p_IsRecordCommitJP = false;
	
	@Override
	protected void prepare() 
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
			else if (name.equals("AD_Org_ID"))
				 p_AD_Org_ID = para[i].getParameterAsInt();
			else if (name.equals("JP_ContractCategoryL2_ID"))
				p_JP_ContractCategoryL2_ID = para[i].getParameterAsInt();
			else if (name.equals("JP_ContractCategoryL1_ID"))
				p_JP_ContractCategoryL1_ID = para[i].getParameterAsInt();
			else if (name.equals("JP_ContractCategory_ID"))
				p_JP_ContractCategory_ID = para[i].getParameterAsInt();
			else if (name.equals("IsRecordCommitJP"))
				p_IsRecordCommitJP = para[i].getParameterAsBoolean();
			else if (name.equals("JP_ContractProcessTraceLevel"))
				p_JP_ContractProcessTraceLevel = para[i].getParameterAsString();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		
		}//for
	}
	
	LocalDateTime now_LocalDateTime = null;
	Timestamp now_Timestamp = null;
	
	@Override
	protected String doIt() throws Exception 
	{
		
		if(processingNow == null)
		{
			processingNow = new HashMap<Integer, Boolean>();
			MClient[] clients = MClient.getAll(getCtx());
			for(int i = 0; i < clients.length; i++)
			{
				processingNow.put(clients[i].getAD_Client_ID(), false);
			}
		}
		
		
		
		String msg = "";
		try 
		{
			if(processingNow.get(getAD_Client_ID()))
				throw new Exception(Msg.getMsg(getCtx(), "JP_ContractProcessRunningNow"));//Contract process is running now by other user.
			else
				processingNow.put(getAD_Client_ID(), true);

			//Create Contract Management Log
			if(!p_JP_ContractProcessTraceLevel.equals(MContractLog.JP_CONTRACTPROCESSTRACELEVEL_NoLog))
			{
				String trxName = Trx.createTrxName("ConStUp");
				conractLogTrx = Trx.get(trxName, false);
				m_ContractLog = new MContractLog(getCtx(), 0, conractLogTrx.getTrxName());
				m_ContractLog.setJP_ContractProcessTraceLevel(p_JP_ContractProcessTraceLevel);
				m_ContractLog.setAD_PInstance_ID(getAD_PInstance_ID());
				m_ContractLog.saveEx(conractLogTrx.getTrxName());
				int JP_ContractLog_ID = m_ContractLog.getJP_ContractLog_ID();
				addBufferLog(0, null, null, Msg.getMsg(getCtx(), "JP_DetailLog")+" -> " + Msg.getElement(getCtx(), "JP_ContractLog_ID"), MContractLog.Table_ID, JP_ContractLog_ID);
				conractLogTrx.commit();
			}
			
			msg = doContractStatusUpdate();
			
		} catch (Exception e) {
			
			if(conractLogTrx != null)
			{
				if(p_IsRecordCommitJP)
					msg = "--Rollback--";
				else
					msg = "";
				
				m_ContractLog.setDescription( msg + " Error : " +  e.getMessage( ) );
				m_ContractLog.saveEx(conractLogTrx.getTrxName());
				conractLogTrx.commit();
			}
			
			throw e;
			
		} finally {
			
			processingNow.put(getAD_Client_ID(), false);
			if(conractLogTrx != null)
			{
				conractLogTrx.close();
				conractLogTrx = null;
			}
		}
		
		return  msg;
	}
		
	private String doContractStatusUpdate() throws Exception 
	{	
		//Adjust time because of reference time do not have hh:mm info
		now_LocalDateTime = new Timestamp(System.currentTimeMillis()).toLocalDateTime();
		now_LocalDateTime = now_LocalDateTime.minusDays(1);
		now_Timestamp = Timestamp.valueOf(now_LocalDateTime);
		
		
		MContract[] contracts = getContracts();
		MContract contract = null;
		for(int i = 0; i < contracts.length; i++)
		{
			contract = contracts[i];
			
			//Check from Prepare to Under Contract
			if(contract.getJP_ContractStatus().equals(MContract.JP_CONTRACTSTATUS_Prepare))
			{
				if(contract.getJP_ContractPeriodDate_From().compareTo(now_Timestamp) >= 0)
				{
					contract.setJP_ContractStatus(MContract.JP_CONTRACTSTATUS_UnderContract);
					contract.saveEx(get_TrxName());
					continue;
				}
			}
			
			
			//Check an indefinite period Contract
			if(contract.getJP_ContractPeriodDate_To() == null)
				continue;
			
			//Check Auto update Contract
			if(contract.isAutomaticUpdateJP())
			{
				//Auto update Contract
				if(contract.getJP_ContractCancelDate() == null)
				{
						
					if(contract.getJP_ContractCancelDeadline().compareTo(now_Timestamp) <= 0 )
					{
						automaticUpdate(contract);
					}
					
					checkContractProcStatus(contract);
					
				//Cancel Contract
				}else{
					
					if(contract.getJP_ContractPeriodDate_To().compareTo(now_Timestamp) <= 0)
					{
						cancelContract(contract);
						
					}else{
						
						checkContractProcStatus(contract);
					}
					
				}
				
			
			//Not Auto update Contract
			}else{
				
				if(contract.getJP_ContractPeriodDate_To() == null)
					continue;
				
				if(contract.getJP_ContractPeriodDate_To().compareTo(now_Timestamp) <= 0 )
				{
					cancelContract(contract);
					
				}else{
					
					checkContractProcStatus(contract);

				}//if(local_ContractPeriodDate_To.compareTo(now) <= 0 )		
			}//if(contract.isAutomaticUpdateJP())
		}//for i
		
		return "OK";
	}
	
	private MContract[] getContracts()
	{
		ArrayList<MContract> list = new ArrayList<MContract>();
		final StringBuilder sql = new StringBuilder("SELECT * FROM JP_Contract c WHERE c.DocStatus = 'CO' AND c.JP_ContractStatus IN ('PR' ,'UC')");
		if(p_AD_Org_ID > 0)
			sql.append(" AND c.AD_Org_ID = ? ");
		
		if(p_JP_ContractCategory_ID > 0)
			sql.append(" AND c.JP_ContractCategory_ID  = ? ");
		else if(p_JP_ContractCategoryL1_ID > 0)
			sql.append(" AND c.JP_ContractCategoryL1_ID  = ? ");
		else if(p_JP_ContractCategoryL2_ID > 0)
			sql.append(" AND c.JP_ContractCategoryL2_ID  = ? ");
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql.toString(), null);
			int i = 1;
			if(p_AD_Org_ID > 0)
				pstmt.setInt(i++, p_AD_Org_ID);
			if(p_JP_ContractCategory_ID > 0)
				pstmt.setInt(i++, p_JP_ContractCategory_ID);
			else if(p_JP_ContractCategoryL1_ID > 0)
				pstmt.setInt(i++, p_JP_ContractCategoryL1_ID);
			else if(p_JP_ContractCategoryL2_ID > 0)
				pstmt.setInt(i++, p_JP_ContractCategoryL2_ID);
			
			rs = pstmt.executeQuery();
			while(rs.next())
				list.add(new MContract(getCtx(), rs, null));
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
		
		
		MContract[] contracts = new MContract[list.size()];
		list.toArray(contracts);
		return contracts;
	}
	
	
	private void automaticUpdate(MContract contract)
	{
		LocalDateTime  local_ContractPeriodDate_To = contract.getJP_ContractPeriodDate_To().toLocalDateTime();
		MContractExtendPeriod extendPeriod = MContractExtendPeriod.get(getCtx(), contract.getJP_ContractExtendPeriod_ID());
		local_ContractPeriodDate_To = local_ContractPeriodDate_To.plusYears(extendPeriod.getJP_Year()).plusMonths(extendPeriod.getJP_Month()).plusDays(extendPeriod.getJP_Day());
		contract.setJP_ContractPeriodDate_To(Timestamp.valueOf(local_ContractPeriodDate_To));
		
		MContractCancelTerm cancelTerm = MContractCancelTerm.get(getCtx(), contract.getJP_ContractCancelTerm_ID());
		LocalDateTime local_ContractCancelDeadline = local_ContractPeriodDate_To.minusYears(cancelTerm.getJP_Year()).minusMonths(cancelTerm.getJP_Month()).minusDays(cancelTerm.getJP_Day());
		contract.setJP_ContractCancelDeadline(Timestamp.valueOf(local_ContractCancelDeadline));
		
		contract.saveEx(get_TrxName());
		
		//Create Contract Log
		MContractLogDetail contractlog = new MContractLogDetail(getCtx(), 0, m_ContractLog.get_TrxName());
		contractlog.setJP_ContractLog_ID(m_ContractLog.getJP_ContractLog_ID());
		contractlog.setJP_ContractLogMsg(MContractLogDetail.JP_CONTRACTLOGMSG_AutomaticUpdatedOfTheContract);
		contractlog.setJP_ContractProcessTraceLevel(MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_ToBeConfirmed);
		contractlog.setJP_Contract_ID(contract.getJP_Contract_ID());
		contractlog.saveEx( m_ContractLog.get_TrxName());
		
	}
	
	private void cancelContract(MContract contract)
	{
		String JP_ConstractStatus_From = contract.getJP_ContractStatus();
		contract.setJP_ContractStatus(MContract.JP_CONTRACTSTATUS_ExpirationOfContract);
		contract.saveEx(get_TrxName());
		
		//Create Contract Log
		MContractLogDetail contractlog = new MContractLogDetail(getCtx(), 0, m_ContractLog.get_TrxName());
		contractlog.setJP_ContractLog_ID(m_ContractLog.getJP_ContractLog_ID());
		contractlog.setJP_ContractLogMsg(MContractLogDetail.JP_CONTRACTLOGMSG_ContractStatusUpdated);
		contractlog.setJP_ContractProcessTraceLevel(MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_ToBeConfirmed);
		contractlog.setJP_Contract_ID(contract.getJP_Contract_ID());
		contractlog.setJP_ContractStatus_From(JP_ConstractStatus_From);
		contractlog.setJP_ContractStatus_To(MContract.JP_CONTRACTSTATUS_ExpirationOfContract);
		contractlog.saveEx( m_ContractLog.get_TrxName());
		
		
		MContractContent[] contents = contract.getContractContents();
		String JP_ConstractProcStatus_From = null;
		for(int i = 0; i < contents.length; i++)
		{
			if(!contents[i].getJP_ContractProcStatus().equals(MContractContent.JP_CONTRACTPROCSTATUS_Processed))
			{
				JP_ConstractProcStatus_From = contents[i].getJP_ContractProcStatus();
				contents[i].setJP_ContractProcStatus(MContractContent.JP_CONTRACTPROCSTATUS_Processed);
				contents[i].saveEx(get_TrxName());
				
				//Create Contract Log
				MContractLogDetail contentLog = new MContractLogDetail(getCtx(), 0, m_ContractLog.get_TrxName());
				contentLog.setJP_ContractLog_ID(m_ContractLog.getJP_ContractLog_ID());
				contentLog.setJP_ContractLogMsg(MContractLogDetail.JP_CONTRACTLOGMSG_ContractProcessStatusUpdated);
				contentLog.setJP_ContractProcessTraceLevel(MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_ToBeConfirmed);
				contentLog.setJP_Contract_ID(contract.getJP_Contract_ID());
				contentLog.setJP_ContractContent_ID(contents[i].getJP_ContractContent_ID());
				contentLog.setJP_ContractProcStatus_From(JP_ConstractProcStatus_From);
				contentLog.setJP_ContractProcStatus_To(MContractContent.JP_CONTRACTPROCSTATUS_Processed);
				contentLog.saveEx( m_ContractLog.get_TrxName());
			}
		}//for j
	
	}
	
	private void checkContractProcStatus(MContract contract)
	{
		MContractContent[] contents = contract.getContractContents();
		String JP_ConstractProcStatus_From = null;
		for(int i = 0; i < contents.length; i++)
		{
			if(contents[i].getJP_ContractProcDate_To() == null)
				continue;
			
			if(contents[i].getJP_ContractProcDate_To().compareTo(now_Timestamp) <= 0 )
			{
				JP_ConstractProcStatus_From = contents[i].getJP_ContractProcStatus();
				contents[i].setJP_ContractProcStatus(MContractContent.JP_CONTRACTPROCSTATUS_Processed);
				contents[i].saveEx(get_TrxName());
				
				//Create Contract Log
				MContractLogDetail contentLog = new MContractLogDetail(getCtx(), 0, m_ContractLog.get_TrxName());
				contentLog.setJP_ContractLog_ID(m_ContractLog.getJP_ContractLog_ID());
				contentLog.setJP_ContractLogMsg(MContractLogDetail.JP_CONTRACTLOGMSG_ContractProcessStatusUpdated);
				contentLog.setJP_ContractProcessTraceLevel(MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_ToBeConfirmed);
				contentLog.setJP_Contract_ID(contract.getJP_Contract_ID());
				contentLog.setJP_ContractContent_ID(contents[i].getJP_ContractContent_ID());
				contentLog.setJP_ContractProcStatus_From(JP_ConstractProcStatus_From);
				contentLog.setJP_ContractProcStatus_To(MContractContent.JP_CONTRACTPROCSTATUS_Processed);
				contentLog.saveEx( m_ContractLog.get_TrxName());
				
			}else{
				
				;//Nothing to do
				
			}
			
		}//for j
	}
}
