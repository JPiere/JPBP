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
import jpiere.base.plugin.org.adempiere.model.MContractProcPeriod;
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
		
		for(int i = 0 ; i < m_ContractContentTemplates.length; i++)
		{
			MContractContent contrctContent = new MContractContent(getCtx(), 0, get_TrxName());
			PO.copyValues(m_ContractContentTemplates[i], contrctContent);
			contrctContent.setAD_Org_ID(m_Contract.getAD_Org_ID());
			contrctContent.setAD_OrgTrx_ID(m_Contract.getAD_OrgTrx_ID());
			contrctContent.setJP_Contract_ID(m_Contract.get_ID());
			contrctContent.setJP_ContractContentT_ID(m_ContractContentTemplates[i].get_ID());
			contrctContent.setJP_Contract_Acct_ID(m_ContractContentTemplates[i].getJP_Contract_Acct_ID());
			contrctContent.setDateDoc(m_Contract.getDateDoc());
			contrctContent.setDateAcct(m_Contract.getDateAcct());
			
			//TODO 契約処理期間オフセットの処理
			if(m_ContractContentTemplates[i].getJP_ContractProcPOffset() == 0)
			{
				contrctContent.setJP_ContractProcDate_From(m_Contract.getJP_ContractPeriodDate_From());
			}else{
				int ContractProcessPeriodOffset = m_ContractContentTemplates[i].getJP_ContractProcPOffset() ;
			}
			
			//TODO 契約処理期間数の処理
			if(m_ContractContentTemplates[i].getJP_ContractProcPeriodNum() == 0)
			{
				contrctContent.setJP_ContractProcDate_To(m_Contract.getJP_ContractPeriodDate_To());
			}//TODO Else文の追加
			
			
			if(m_ContractContentTemplates[i].getC_BPartner_ID()==0)
			{
				contrctContent.setC_BPartner_ID(m_Contract.getC_BPartner_ID());
				contrctContent.setC_BPartner_Location_ID(m_Contract.getC_BPartner_Location_ID());
				contrctContent.setAD_User_ID(m_Contract.getAD_User_ID());
			}
			contrctContent.setTotalLines(Env.ZERO);
			contrctContent.setDocStatus(DocAction.STATUS_Drafted);
			contrctContent.setDocAction(DocAction.ACTION_Complete);
			contrctContent.setJP_ContractProcStatus(MContractContent.JP_CONTRACTPROCSTATUS_InProgress);
			if(contrctContent.getM_Warehouse_ID() == 0)
				contrctContent.setM_Warehouse_ID(MOrgInfo.get(null, contrctContent.getAD_Org_ID(),get_TrxName()).getM_Warehouse_ID());
			
			//TODO 通貨とプライスリストの処理
			contrctContent.setC_Currency_ID(m_Contract.getC_Currency_ID());
			contrctContent.saveEx(get_TrxName());
			
			//TODO 明細の登録処理
			MContractLineT[] m_ContractLineTemplates = m_ContractContentTemplates[i].getContractLineTemplates();
			for(int j = 0; j < m_ContractLineTemplates.length; j++)
			{
				MContractLine contrctLine = new MContractLine(getCtx(), 0, get_TrxName());
				PO.copyValues(m_ContractLineTemplates[j], contrctLine);
				contrctLine.setAD_Org_ID(contrctContent.getAD_Org_ID());
				contrctLine.setAD_OrgTrx_ID(contrctContent.getAD_OrgTrx_ID());
				contrctLine.setJP_ContractContent_ID(contrctContent.getJP_ContractContent_ID());
				contrctLine.setJP_ContractLineT_ID(m_ContractLineTemplates[j].getJP_ContractLineT_ID());
				contrctLine.saveEx(get_TrxName());
			}
		}
		
		return Msg.getMsg(getCtx(), "OK");
	}
	
}
