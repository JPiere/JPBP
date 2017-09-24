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

import java.util.logging.Level;

import org.compiere.model.MOrgInfo;
import org.compiere.model.PO;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
import org.compiere.util.Msg;

import jpiere.base.plugin.org.adempiere.model.MContract;
import jpiere.base.plugin.org.adempiere.model.MContractCalender;
import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractContentT;
import jpiere.base.plugin.org.adempiere.model.MContractLine;
import jpiere.base.plugin.org.adempiere.model.MContractLineT;
import jpiere.base.plugin.org.adempiere.model.MContractProcPeriod;
import jpiere.base.plugin.org.adempiere.model.MContractT;

/** JPIERE-0363
*
* @author Hideaki Hagiwara
*
*/
public class AbstractCreateContractFromTemplate extends SvrProcess {
	
	
	protected MContract m_Contract = null;
	protected MContractContent m_ContractContent = null;
	protected MContractT m_ContractTemplate = null;
	protected MContractContentT[] m_ContractContentTemplates = null;
	
	protected String p_JP_ContractTabLevel = null;
	protected  static final String JP_ContractTabLevel_Document  = "CD";
	protected  static final String JP_ContractTabLevel_Content  = "CC";
	
	
	int Record_ID = 0;
	
	
	@Override
	protected void prepare() 
	{
		Record_ID = getRecord_ID();
		if(Record_ID > 0)
		{			
			
			ProcessInfoParameter[] para = getParameter();
			for (int i = 0; i < para.length; i++)
			{
				String name = para[i].getParameterName();

				if (para[i].getParameter() == null)
				{
					;
					
				}else if (name.equals("JP_ContractTabLevel")){
					
					p_JP_ContractTabLevel = para[i].getParameterAsString();
					
				}else{
					log.log(Level.SEVERE, "Unknown Parameter: " + name);
				}//if
			}//for
			
			
			if(p_JP_ContractTabLevel.equals(JP_ContractTabLevel_Document))
			{
				m_Contract = new MContract(getCtx(), Record_ID, get_TrxName());
				m_ContractTemplate = new MContractT(getCtx(),m_Contract.getJP_ContractT_ID(), get_TrxName());
				m_ContractContentTemplates = m_ContractTemplate.getContractContentTemplates();
			
			}else if(p_JP_ContractTabLevel.equals(JP_ContractTabLevel_Content)){
				
				m_ContractContent = new MContractContent(getCtx(), Record_ID, get_TrxName());
				m_Contract = m_ContractContent.getParent();
			}
			

		}else{
			log.log(Level.SEVERE, "Record_ID <= 0 ");
		}
	}
	
	@Override
	protected String doIt() throws Exception 
	{
		if(m_Contract.equals(MContract.JP_CONTRACTTYPE_GeneralContract))
		{
			throw new Exception("JP_GeneralContractContent");//General Contract can not have Contract Content.
		}
		
		if(p_JP_ContractTabLevel.equals(JP_ContractTabLevel_Document))
		{
			createContractContent();
			
		}else if(p_JP_ContractTabLevel.equals(JP_ContractTabLevel_Content)){
			

			createContractLine(m_ContractContent, MContractContentT.get(getCtx(), m_ContractContent.getJP_ContractContentT_ID()));
		}
		
		return Msg.getMsg(getCtx(), "Success");
	}
	
	protected void createContractContent() throws Exception 
	{
		
		MContractContent[]  m_ContractContents = m_Contract.getContractContents();
		if(m_ContractContents.length > 0)
		{
			throw new Exception(Msg.getMsg(getCtx(), "JP_ContractContentCreated"));//Contract Content has already been created
		}
		
		//Create Contract Content
		for(int i = 0 ; i < m_ContractContentTemplates.length; i++)
		{
			MContractContent contractContent = new MContractContent(getCtx(), 0, get_TrxName());
			PO.copyValues(m_ContractContentTemplates[i], contractContent);
			contractContent.setAD_Org_ID(m_Contract.getAD_Org_ID());
			contractContent.setAD_OrgTrx_ID(m_Contract.getAD_OrgTrx_ID());
			contractContent.setJP_Contract_ID(m_Contract.get_ID());
			contractContent.setJP_ContractContentT_ID(m_ContractContentTemplates[i].get_ID());
			contractContent.setJP_Contract_Acct_ID(m_ContractContentTemplates[i].getJP_Contract_Acct_ID());
			contractContent.setDateDoc(m_Contract.getDateDoc());
			contractContent.setDateAcct(m_Contract.getDateAcct());
			
			if(m_ContractContentTemplates[i].getJP_ContractProcPOffset()==0)
			{
				contractContent.setJP_ContractProcDate_From(m_Contract.getJP_ContractPeriodDate_From());
				
			}else{
				MContractCalender calender = MContractCalender.get(getCtx(), contractContent.getJP_ContractCalender_ID());
				MContractProcPeriod period = calender.getContractProcessPeriod(getCtx(), m_Contract.getJP_ContractPeriodDate_From(), null, m_ContractContentTemplates[i].getJP_ContractProcPOffset());
				contractContent.setJP_ContractProcDate_From(period.getStartDate());
			}
			
			if(m_ContractContentTemplates[i].getJP_ContractProcPeriodNum()==0)
			{
				contractContent.setJP_ContractProcDate_To(m_Contract.getJP_ContractPeriodDate_To());
			}else{
				MContractCalender calender = MContractCalender.get(getCtx(), contractContent.getJP_ContractCalender_ID());
				MContractProcPeriod period = calender.getContractProcessPeriod(getCtx(), contractContent.getJP_ContractProcDate_From(), null, m_ContractContentTemplates[i].getJP_ContractProcPeriodNum());
				if(m_Contract.getJP_ContractPeriodDate_To() == null)
				{
					contractContent.setJP_ContractProcDate_To(period.getEndDate());
					
				}else{
					
					if(m_Contract.getJP_ContractPeriodDate_To().compareTo(period.getEndDate()) >= 0)
					{
						contractContent.setJP_ContractProcDate_To(period.getEndDate());
						
					}else{
						
						contractContent.setJP_ContractProcDate_To(m_Contract.getJP_ContractPeriodDate_To());
					}
					
				}
			}
			
			if(m_ContractContentTemplates[i].getC_BPartner_ID()==0)
			{
				contractContent.setC_BPartner_ID(m_Contract.getC_BPartner_ID());
				contractContent.setC_BPartner_Location_ID(m_Contract.getC_BPartner_Location_ID());
				contractContent.setAD_User_ID(m_Contract.getAD_User_ID());
			}
			contractContent.setTotalLines(Env.ZERO);
			contractContent.setDocStatus(DocAction.STATUS_Drafted);
			contractContent.setDocAction(DocAction.ACTION_Complete);
			contractContent.setJP_ContractProcStatus(MContractContent.JP_CONTRACTPROCSTATUS_Unprocessed);
			if(contractContent.getM_Warehouse_ID() == 0)
				contractContent.setM_Warehouse_ID(MOrgInfo.get(null, contractContent.getAD_Org_ID(),get_TrxName()).getM_Warehouse_ID());
			
			contractContent.setC_Currency_ID(contractContent.getM_PriceList().getC_Currency_ID());
			contractContent.saveEx(get_TrxName());
			createContractLine(contractContent,m_ContractContentTemplates[i]);
			
		}//For i
		
	}//createContractContent
	
		
	protected void createContractLine(MContractContent contractContent, MContractContentT template) throws Exception 
	{
		MContractLine[] m_ContractLine = contractContent.getLines();
		if(m_ContractLine.length > 0) 
		{
			throw new Exception(Msg.getMsg(getCtx(), "JP_ContractContentLineCreated"));//Contract Content Line has already been created
		}
		
		
		//Create Contract Content Line
		MContractLineT[] m_ContractLineTemplates = template.getContractLineTemplates();
		for(int i = 0; i < m_ContractLineTemplates.length; i++)
		{
			MContractLine contrctLine = new MContractLine(getCtx(), 0, get_TrxName());
			PO.copyValues(m_ContractLineTemplates[i], contrctLine);
			contrctLine.setAD_Org_ID(contractContent.getAD_Org_ID());
			contrctLine.setAD_OrgTrx_ID(contractContent.getAD_OrgTrx_ID());
			contrctLine.setJP_ContractContent_ID(contractContent.getJP_ContractContent_ID());
			contrctLine.setJP_ContractLineT_ID(m_ContractLineTemplates[i].getJP_ContractLineT_ID());
			contrctLine.saveEx(get_TrxName());
		}//For i
		
	}//createContractLine
	
}
