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

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MOrderLine;
import org.compiere.model.PO;
import org.compiere.process.DocAction;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.MContract;
import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractLogDetail;
import jpiere.base.plugin.org.adempiere.model.MContractPSLine;
import jpiere.base.plugin.org.adempiere.model.MContractProcSchedule;
import jpiere.base.plugin.org.adempiere.model.MOrderJP;


/**
* JPIERE-0431
*
* @author Hideaki Hagiwara
*
*/
public class DefaultContractProcessCreateBaseOrderIndirectly extends AbstractContractProcess
{

	@Override
	protected void prepare()
	{
		super.prepare();
	}

	@Override
	protected String doIt() throws Exception
	{
		super.doIt();

		int JP_ContractProcPeriod_ID = 0;
		if(m_ContractContent.getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
			JP_ContractProcPeriod_ID = getJP_ContractProctPeriod_ID();

		if(m_ContractContent.getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract)
				&& JP_ContractProcPeriod_ID == 0)
		{
			String descriptionMsg = Msg.getMsg(getCtx(), "NotFound") + " : " + Msg.getElement(getCtx(), "JP_ContractProcPeriod_ID");
			createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_UnexpectedError, null,  null, descriptionMsg);
			return "";
		}

		MContractProcSchedule[] contractProcSchdules= MContractProcSchedule.getMContractProcSchedules(m_ContractContent.getJP_ContractContent_ID(), JP_ContractProcPeriod_ID, get_TrxName());
		for(int i = 0;  i < contractProcSchdules.length; i++)
		{

			if(contractProcSchdules[i].isFactCreatedJP())//TODO スキップのログ必要?
				continue;

			if(!contractProcSchdules[i].getDocStatus().equals(DocAction.STATUS_Completed)) //TODO スキップのログ必要?
				continue;

			MContractPSLine[] contractPSLines = contractProcSchdules[i].getContractPSLines();//TODO スキップのログ必要?
			if(contractPSLines.length <= 0)
				continue;

			/** Create Order header */
			MOrderJP order = new MOrderJP(getCtx(), 0, get_TrxName());
			PO.copyValues(contractProcSchdules[i], order);
			order.setProcessed(false);
			order.setDocStatus(DocAction.STATUS_Drafted);
			order.setAD_Org_ID(contractProcSchdules[i].getAD_Org_ID());
			order.setAD_OrgTrx_ID(contractProcSchdules[i].getAD_OrgTrx_ID());
			order.setDateOrdered(getDateOrdered());
			order.setDateAcct(getDateAcct());
			order.setDatePromised(contractProcSchdules[i].getDatePromised()); //DateAcct is basis.
			order.setDocumentNo(""); //Reset Document No
			order.setC_DocTypeTarget_ID(contractProcSchdules[i].getJP_BaseDocDocType_ID());
			order.setC_DocType_ID(contractProcSchdules[i].getJP_BaseDocDocType_ID());
			order.set_ValueOfColumn("JP_Contract_ID", contractProcSchdules[i].getJP_Contract_ID());
			order.set_ValueOfColumn("JP_ContractContent_ID", contractProcSchdules[i].getJP_ContractContent_ID());
			if(m_ContractContent.getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
				order.set_ValueOfColumn("JP_ContractProcPeriod_ID", contractProcSchdules[i].getJP_ContractProcPeriod_ID());

			try
			{
				order.saveEx(get_TrxName());
			} catch (AdempiereException e) {
				createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SaveError, null, null, e.getMessage());
				throw e;
			}finally {
				;
			}

			try
			{
				contractProcSchdules[i].setIsFactCreatedJP(true);
				contractProcSchdules[i].setC_Order_ID(order.getC_Order_ID());
				contractProcSchdules[i].saveEx(get_TrxName());
			} catch (AdempiereException e) {
				createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SaveError, null, contractProcSchdules[i], e.getMessage());
				throw e;
			}finally {
				;
			}

			for(int j = 0; j < contractPSLines.length; j++)
			{
				MOrderLine oline = new MOrderLine(getCtx(), 0, get_TrxName());
				PO.copyValues(contractPSLines[j], oline);
				oline.setC_Order_ID(order.getC_Order_ID());
				oline.setAD_Org_ID(order.getAD_Org_ID());
				oline.setAD_OrgTrx_ID(order.getAD_OrgTrx_ID());
				oline.setProcessed(false);


				//
				if(contractPSLines[j].getC_BPartner_ID() == 0)
					oline.setC_BPartner_ID(order.getC_BPartner_ID());
				if(contractPSLines[j].getC_BPartner_Location_ID() == 0)
					oline.setC_BPartner_Location_ID(order.getC_BPartner_Location_ID());
				oline.setM_Warehouse_ID(order.getM_Warehouse_ID());
				oline.setC_Currency_ID(order.getC_Currency_ID());


				//Qty
				if(contractPSLines[j].getM_Product_ID() > 0)
				{
					oline.setC_UOM_ID(contractPSLines[j].getM_Product().getC_UOM_ID());
					oline.setQtyEntered(contractPSLines[j].getQtyOrdered());
				}else{
					oline.setQtyEntered(contractPSLines[j].getQtyEntered());

				}
				oline.setQtyOrdered(contractPSLines[j].getQtyOrdered());
				oline.setQtyReserved(Env.ZERO);
				oline.setQtyDelivered(Env.ZERO);
				oline.setQtyInvoiced(Env.ZERO);

				//Contract Info
				oline.set_ValueNoCheck("JP_ContractLine_ID", contractPSLines[j].getJP_ContractLine_ID());
				if(m_ContractContent.getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
					oline.set_ValueOfColumn("JP_ContractProcPeriod_ID", JP_ContractProcPeriod_ID);

				//Date
				oline.setDateOrdered(order.getDateOrdered());
				oline.setDatePromised(contractPSLines[j].getDatePromised());

				try {
					oline.saveEx(get_TrxName());//DocStatus is Draft
				} catch (AdempiereException e) {
					createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SaveError, null, order, e.getMessage());
					throw e;
				}finally {
					;
				}

				try
				{
					contractPSLines[j].setIsFactCreatedJP(true);
					contractPSLines[j].setC_OrderLine_ID(oline.getC_OrderLine_ID());
					contractPSLines[j].saveEx(get_TrxName());
				} catch (AdempiereException e) {
					createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SaveError, null, contractPSLines[j], e.getMessage());
					throw e;
				}finally {
					;
				}

			}//for J


			if(m_ContractContent.getJP_ContractProcStatus().equals(MContractContent.JP_CONTRACTPROCSTATUS_Unprocessed))
			{
				m_ContractContent.setJP_ContractProcStatus(MContractContent.JP_CONTRACTPROCSTATUS_InProgress);
				try {
					m_ContractContent.save(get_TrxName());
				} catch (AdempiereException e) {
					createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SaveError, null, m_ContractContent, e.getMessage());
					throw e;
				}finally {
					;
				}
			}


			String docAction = getDocAction();
			updateContractProcStatus();
			if(!Util.isEmpty(docAction))
			{
				if(!order.processIt(docAction))
				{
					createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_DocumentActionError, null, order, order.getProcessMsg());
					throw new AdempiereException(order.getProcessMsg());
				}

				if(!docAction.equals(DocAction.ACTION_Complete))
				{
					order.setDocAction(DocAction.ACTION_Complete);
					try {
						order.saveEx(get_TrxName());
					} catch (AdempiereException e) {
						createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SaveError, null, order, e.getMessage());
						throw e;
					}finally {
						;
					}
				}
			}else{

				order.setDocAction(DocAction.ACTION_Complete);
				try {
					order.saveEx(get_TrxName());//DocStatus is Draft
				} catch (AdempiereException e) {
					createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SaveError, null, order, e.getMessage());
					throw e;
				}finally {
					;
				}

			}

			createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_CreatedDocument, null, order, null);

		}//for i


		return "";

	}



}
