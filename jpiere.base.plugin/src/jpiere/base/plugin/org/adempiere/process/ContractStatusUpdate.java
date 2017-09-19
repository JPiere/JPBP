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
import java.util.logging.Level;

import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;

import jpiere.base.plugin.org.adempiere.model.MContract;
import jpiere.base.plugin.org.adempiere.model.MContractCancelTerm;
import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractExtendPeriod;


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
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		
		}//for
	}
	
	LocalDateTime now_LocalDateTime = null;
	Timestamp now_Timestamp = null;
	
	@Override
	protected String doIt() throws Exception 
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
			if(contract.getJP_ContractPeriodDate_To() == null)
				continue;
			
			//Auto update Contract
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
		
		return null;
	}
	
	private MContract[] getContracts()
	{
		ArrayList<MContract> list = new ArrayList<MContract>();
		final StringBuilder sql = new StringBuilder("SELECT * FROM JP_Contract c WHERE c.DocStatus = 'CO' AND c.JP_ContractStatus IN ('PR' ,'UC')");
		if(p_AD_Org_ID > 0)
			sql.append(" c.AD_Org_ID = ? ");
		
		if(p_JP_ContractCategory_ID > 0)
			sql.append(" c.JP_ContractCategory_ID  = ? ");
		else if(p_JP_ContractCategoryL1_ID > 0)
			sql.append(" c.JP_ContractCategoryL1_ID  = ? ");
		else if(p_JP_ContractCategoryL2_ID > 0)
			sql.append(" c.JP_ContractCategoryL2_ID  = ? ");
		
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
//			log.log(Level.SEVERE, sql, e);
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
	}
	
	private void cancelContract(MContract contract)
	{
		contract.setJP_ContractStatus(MContract.JP_CONTRACTSTATUS_ExpirationOfContract);
		contract.saveEx(get_TrxName());
		MContractContent[] contents = contract.getContractContents();
		for(int i = 0; i < contents.length; i++)
		{
			if(!contents[i].getJP_ContractProcStatus().equals(MContractContent.JP_CONTRACTPROCSTATUS_Processed))
			{
				contents[i].setJP_ContractProcStatus(MContractContent.JP_CONTRACTPROCSTATUS_Processed);
				contents[i].saveEx(get_TrxName());
			}
		}//for j
	
	}
	
	private void checkContractProcStatus(MContract contract)
	{
		MContractContent[] contents = contract.getContractContents();
		for(int i = 0; i < contents.length; i++)
		{
			if(contents[i].getJP_ContractProcDate_To() == null)
				continue;
			
			if(contents[i].getJP_ContractProcDate_To().compareTo(now_Timestamp) <= 0 )
			{
				contents[i].setJP_ContractProcStatus(MContractContent.JP_CONTRACTPROCSTATUS_Processed);
				contents[i].saveEx(get_TrxName());
				
			}else{
				
				;//Nothing to do
				
			}
			
		}//for j
	}
}
