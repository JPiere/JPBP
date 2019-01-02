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

import org.compiere.model.PO;
import org.compiere.process.DocAction;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractContentT;
import jpiere.base.plugin.org.adempiere.model.MContractLine;
import jpiere.base.plugin.org.adempiere.model.MContractLineT;
import jpiere.base.plugin.org.adempiere.model.MContractProcessList;
import jpiere.base.plugin.org.adempiere.model.MContractProcessRef;

/** JPIERE-0363
*
* @author Hideaki Hagiwara
*
*/
public class DefaultCreateContractFromTemplate extends AbstractCreateContractFromTemplate {

	@Override
	protected void prepare()
	{
		super.prepare();
	}

	@Override
	protected String doIt() throws Exception
	{
		super.doIt();

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
			contractContent.setDatePromised(calculateDate(m_Contract.getDateAcct(), m_ContractContentTemplates[i].getDeliveryTime_Promised()));
			contractContent.setDateInvoiced(m_Contract.getDateAcct());
			setContractContentProcDate(contractContent, m_ContractContentTemplates[i]);

			int JP_ContractProcessRef_ID = m_ContractContentTemplates[i].getJP_ContractProcessRef_ID();
			if(JP_ContractProcessRef_ID > 0)
			{
				MContractProcessRef  contractProcessRef = MContractProcessRef.get(getCtx(), JP_ContractProcessRef_ID);
				MContractProcessList[] contractProcessLists = contractProcessRef.getContractProcessList(getCtx(), true, get_TrxName());
				if(contractProcessLists.length==1)
					contractContent.setJP_ContractProcess_ID(contractProcessLists[0].getJP_ContractProcess_ID());
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

			setWarehouseOfContractContent(m_ContractContentTemplates[i], contractContent);

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
			contrctLine.setDateOrdered(contractContent.getDateOrdered());
			contrctLine.setDatePromised(calculateDate(contractContent.getDateAcct(), m_ContractLineTemplates[i].getDeliveryTime_Promised())) ;
			contrctLine.setJP_ContractContent_ID(contractContent.getJP_ContractContent_ID());
			contrctLine.setJP_ContractLineT_ID(m_ContractLineTemplates[i].getJP_ContractLineT_ID());

			if(contrctLine.getJP_BaseDocLinePolicy() != null)
			{
				setBaseDocLineProcPeriod(contrctLine, m_ContractLineTemplates[i]);
			}


			int JP_ContractCalRef_InOut_ID = m_ContractLineTemplates[i].getJP_ContractCalRef_InOut_ID();
			if(JP_ContractCalRef_InOut_ID > 0 && !Util.isEmpty(contractContent.getJP_CreateDerivativeDocPolicy()) )
			{
				setDerivativeInOutLineProcPeriod(contrctLine, m_ContractLineTemplates[i]);

			}//if(JP_ContractCalRef_InOut_ID > 0)


			int JP_ContractCalRef_Inv_ID = m_ContractLineTemplates[i].getJP_ContractCalRef_Inv_ID();
			if(JP_ContractCalRef_Inv_ID > 0 && !Util.isEmpty(contractContent.getJP_CreateDerivativeDocPolicy()))
			{
				setDerivativeInvoiceLineProcPeriod(contrctLine, m_ContractLineTemplates[i]);

			}//if(JP_ContractCalRef_Inv_ID > 0)


			int JP_ContractProcRef_InOut_ID = m_ContractLineTemplates[i].getJP_ContractProcRef_InOut_ID();
			if(JP_ContractProcRef_InOut_ID > 0)
			{
				MContractProcessRef  contractProcessRef = MContractProcessRef.get(getCtx(), JP_ContractProcRef_InOut_ID);
				MContractProcessList[] contractProcessLists = contractProcessRef.getContractProcessList(getCtx(), true, get_TrxName());
				if(contractProcessLists.length==1)
				{
					contrctLine.setJP_ContractProcess_InOut_ID(contractProcessLists[0].getJP_ContractProcess_ID());
				}
			}

			int JP_ContractProcRef_Inv_ID = m_ContractLineTemplates[i].getJP_ContractProcRef_Inv_ID();
			if(JP_ContractProcRef_Inv_ID > 0)
			{
				MContractProcessRef  contractProcessRef = MContractProcessRef.get(getCtx(), JP_ContractProcRef_Inv_ID);
				MContractProcessList[] contractProcessLists = contractProcessRef.getContractProcessList(getCtx(), true, get_TrxName());
				if(contractProcessLists.length==1)
				{
					contrctLine.setJP_ContractProcess_Inv_ID(contractProcessLists[0].getJP_ContractProcess_ID());
				}
			}

			contrctLine.saveEx(get_TrxName());
		}//For i

	}//createContractLine

}
