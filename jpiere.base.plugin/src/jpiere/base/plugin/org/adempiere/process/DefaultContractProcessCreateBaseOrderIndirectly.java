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

		MContractProcSchedule[] contractProcSchedules= MContractProcSchedule.getMContractProcSchedules(m_ContractContent.getJP_ContractContent_ID(), JP_ContractProcPeriod_ID, get_TrxName());
		for(int i = 0;  i < contractProcSchedules.length; i++)
		{

			String msg = getSkipReason_CreateBaseDocIndirectly(getCtx(), contractProcSchedules[i], JP_ContractProcPeriod_ID, false, get_TrxName());
			if(msg != null)
			{
				createContractLogDetail(msg, null, contractProcSchedules[i], null);
				continue;
			}

			/** Create Order header */
			MOrderJP order = new MOrderJP(getCtx(), 0, get_TrxName());
			PO.copyValues(contractProcSchedules[i], order);
			order.setProcessed(false);
			order.setDocStatus(DocAction.STATUS_Drafted);
			order.setAD_Org_ID(contractProcSchedules[i].getAD_Org_ID());
			order.setAD_OrgTrx_ID(contractProcSchedules[i].getAD_OrgTrx_ID());
			order.setDateOrdered(getDateOrdered());
			order.setDateAcct(getDateAcct());
			order.setDatePromised(contractProcSchedules[i].getDatePromised()); //DateAcct is basis.
			order.setDocumentNo(""); //Reset Document No
			order.setC_DocTypeTarget_ID(contractProcSchedules[i].getJP_BaseDocDocType_ID());
			order.setC_DocType_ID(contractProcSchedules[i].getJP_BaseDocDocType_ID());
			order.set_ValueOfColumn("JP_Contract_ID", contractProcSchedules[i].getJP_Contract_ID());
			order.set_ValueOfColumn("JP_ContractContent_ID", contractProcSchedules[i].getJP_ContractContent_ID());
			if(m_ContractContent.getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
				order.set_ValueOfColumn("JP_ContractProcPeriod_ID", contractProcSchedules[i].getJP_ContractProcPeriod_ID());

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
				contractProcSchedules[i].setIsFactCreatedJP(true);
				contractProcSchedules[i].setC_Order_ID(order.getC_Order_ID());
				contractProcSchedules[i].saveEx(get_TrxName());
			} catch (AdempiereException e) {
				createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SaveError, null, contractProcSchedules[i], e.getMessage());
				throw e;
			}finally {
				;
			}

			MContractPSLine[] contractPSLines = contractProcSchedules[i].getContractPSLines();
			for(int j = 0; j < contractPSLines.length; j++)
			{
				MOrderLine oLine = new MOrderLine(getCtx(), 0, get_TrxName());
				PO.copyValues(contractPSLines[j], oLine);
				oLine.setC_Order_ID(order.getC_Order_ID());
				oLine.setAD_Org_ID(order.getAD_Org_ID());
				oLine.setAD_OrgTrx_ID(order.getAD_OrgTrx_ID());
				oLine.setProcessed(false);


				//
				if(contractPSLines[j].getC_BPartner_ID() == 0)
					oLine.setC_BPartner_ID(order.getC_BPartner_ID());
				if(contractPSLines[j].getC_BPartner_Location_ID() == 0)
					oLine.setC_BPartner_Location_ID(order.getC_BPartner_Location_ID());
				oLine.setM_Warehouse_ID(order.getM_Warehouse_ID());
				oLine.setC_Currency_ID(order.getC_Currency_ID());


				//Qty
				if(contractPSLines[j].getM_Product_ID() > 0)
				{
					oLine.setC_UOM_ID(contractPSLines[j].getM_Product().getC_UOM_ID());
					oLine.setQtyEntered(contractPSLines[j].getQtyOrdered());
				}else{
					oLine.setQtyEntered(contractPSLines[j].getQtyEntered());

				}
				oLine.setQtyOrdered(contractPSLines[j].getQtyOrdered());
				oLine.setQtyReserved(Env.ZERO);
				oLine.setQtyDelivered(Env.ZERO);
				oLine.setQtyInvoiced(Env.ZERO);

				//Contract Info
				if(contractPSLines[j].getJP_ContractLine_ID() != 0)
					oLine.set_ValueNoCheck("JP_ContractLine_ID", contractPSLines[j].getJP_ContractLine_ID());
				if(m_ContractContent.getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
					oLine.set_ValueOfColumn("JP_ContractProcPeriod_ID", JP_ContractProcPeriod_ID);

				//Date
				oLine.setDateOrdered(order.getDateOrdered());
				oLine.setDatePromised(contractPSLines[j].getDatePromised());

				try {
					oLine.saveEx(get_TrxName());//DocStatus is Draft
				} catch (AdempiereException e) {
					createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SaveError, null, order, e.getMessage());
					throw e;
				}finally {
					;
				}

				try
				{
					contractPSLines[j].setIsFactCreatedJP(true);
					contractPSLines[j].setC_OrderLine_ID(oLine.getC_OrderLine_ID());
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
