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
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
import org.compiere.util.Msg;

import jpiere.base.plugin.org.adempiere.model.MContract;
import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractContentT;
import jpiere.base.plugin.org.adempiere.model.MContractLine;
import jpiere.base.plugin.org.adempiere.model.MContractLineT;
import jpiere.base.plugin.org.adempiere.model.MContractT;

/** JPIERE-0363
*
* @author Hideaki Hagiwara
*
*/
public class DefaultCreateContractFromTemplate extends SvrProcess {
	
	
	MContract m_Contract = null;
	MContractT m_ContractTemplate = null;
	MContractContentT[] m_ContractContentTemplates = null;
	
	int Record_ID = 0;
	
	
	@Override
	protected void prepare() 
	{
		Record_ID = getRecord_ID();
		if(Record_ID > 0)
		{
			m_Contract = new MContract(getCtx(), Record_ID, get_TrxName());
			m_ContractTemplate = new MContractT(getCtx(),m_Contract.getJP_ContractT_ID(), get_TrxName());
			m_ContractContentTemplates = m_ContractTemplate.getContractContentTemplates();
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
		
		
		MContractContent[]  m_ContractContents = m_Contract.getContractContents();
		if(m_ContractContents.length > 0)
		{
			throw new Exception("JP_ContractContentCreated");//Contract Content has already been created
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
				//TODO 日付の計算処理
			}
			
			if(m_ContractContentTemplates[i].getJP_ContractProcPOffset()==0)
			{
				contractContent.setJP_ContractProcDate_To(m_Contract.getJP_ContractPeriodDate_To());
			}else{
				;//TODO 日付の計算式処理
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
			
			//Create Contract Content Line
			MContractLineT[] m_ContractLineTemplates = m_ContractContentTemplates[i].getContractLineTemplates();
			for(int j = 0; j < m_ContractLineTemplates.length; j++)
			{
				MContractLine contrctLine = new MContractLine(getCtx(), 0, get_TrxName());
				PO.copyValues(m_ContractLineTemplates[j], contrctLine);
				contrctLine.setAD_Org_ID(contractContent.getAD_Org_ID());
				contrctLine.setAD_OrgTrx_ID(contractContent.getAD_OrgTrx_ID());
				contrctLine.setJP_ContractContent_ID(contractContent.getJP_ContractContent_ID());
				contrctLine.setJP_ContractLineT_ID(m_ContractLineTemplates[j].getJP_ContractLineT_ID());
				contrctLine.saveEx(get_TrxName());
			}//For j
		}//For i
		
		return Msg.getMsg(getCtx(), "OK");
	}
	
}
