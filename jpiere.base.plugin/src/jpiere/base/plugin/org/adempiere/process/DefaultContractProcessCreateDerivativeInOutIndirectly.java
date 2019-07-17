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

import java.util.ArrayList;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MInOut;
import org.compiere.model.MInOutLine;
import org.compiere.model.MLocator;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MProduct;
import org.compiere.model.MUOM;
import org.compiere.model.MWarehouse;
import org.compiere.model.PO;
import org.compiere.process.DocAction;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.MContract;
import jpiere.base.plugin.org.adempiere.model.MContractLogDetail;
import jpiere.base.plugin.org.adempiere.model.MContractPSInOutLine;
import jpiere.base.plugin.org.adempiere.model.MContractProcPeriod;
import jpiere.base.plugin.org.adempiere.model.MContractProcSchedule;

/**
* JPIERE-0431
*
* @author Hideaki Hagiwara
*
*/
public class DefaultContractProcessCreateDerivativeInOutIndirectly extends AbstractContractProcess {


	ArrayList<MOrderLine> overQtyOrderedLineList = new ArrayList<MOrderLine>();

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


		MContractProcPeriod orderProcPeriod = getBaseDocContractProcPeriodFromDerivativeDocContractProcPeriod(JP_ContractProcPeriod_ID);
		if(m_ContractContent.getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract)
				&& orderProcPeriod == null)
		{
			String descriptionMsg = Msg.getMsg(getCtx(), "NotFound") + " : " + Msg.getElement(getCtx(), "JP_ContractProcPeriod_ID");
			createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_UnexpectedError, null,  null, descriptionMsg);
			return "";
		}


		MContractProcSchedule[] contractProcSchedules= MContractProcSchedule.getMContractProcSchedules(m_ContractContent.getJP_ContractContent_ID(), orderProcPeriod.getJP_ContractProcPeriod_ID(), get_TrxName());
		for(int i = 0;  i < contractProcSchedules.length; i++)
		{

			String msg = getSkipReason_CreateDerivativeInOutIndirectly(getCtx(), contractProcSchedules[i], JP_ContractProcPeriod_ID, get_TrxName());
			if(msg != null)
			{
				createContractLogDetail(msg, null, contractProcSchedules[i], null);
				continue;
			}


			MOrder order = new MOrder(getCtx(), contractProcSchedules[i].getC_Order_ID(), get_TrxName());

			//Check Order
			if(!order.getDocStatus().equals(DocAction.ACTION_Complete))
			{
				createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForDocumentStatusOfOrderIsNotCompleted, null, order, null);
				continue;
			}


			/** Create InOut header */
			MInOut inout = new MInOut(getCtx(), 0, get_TrxName());
			PO.copyValues(order, inout);
			inout.setC_Order_ID(order.getC_Order_ID());
			inout.setProcessed(false);
			inout.setDocStatus(DocAction.STATUS_Drafted);
			inout.setAD_Org_ID(m_ContractContent.getAD_Org_ID());
			inout.setAD_OrgTrx_ID(m_ContractContent.getAD_OrgTrx_ID());
			inout.setDocumentNo(""); //Reset Document No
			inout.setC_DocType_ID(order.getC_DocTypeTarget().getC_DocTypeShipment_ID());
			inout.setMovementDate(getDateAcct());
			inout.setDateAcct(getDateAcct());
			if(order.isSOTrx())
				inout.setMovementType(MInOut.MOVEMENTTYPE_CustomerShipment);
			else
				inout.setMovementType(MInOut.MOVEMENTTYPE_VendorReceipts);


			try{
				inout.saveEx(get_TrxName());
			} catch (AdempiereException e) {
				createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SaveError, null, contractProcSchedules[i], e.getMessage());
				throw e;
			}finally {
				;
			}


			MContractPSInOutLine[] contractPSInOutLines = contractProcSchedules[i].getContractPSInOutLines(JP_ContractProcPeriod_ID, false);
			for(int j = 0; j < contractPSInOutLines.length; j++)
			{

				if(contractPSInOutLines[j].getJP_ContractPSLine().getC_OrderLine_ID() == 0)
					continue;

				if(contractPSInOutLines[j].isFactCreatedJP())
					continue;

				MInOutLine ioLine = new MInOutLine(getCtx(), 0, get_TrxName());
				PO.copyValues(contractPSInOutLines[j], ioLine);
				ioLine.setC_OrderLine_ID(contractPSInOutLines[j].getJP_ContractPSLine().getC_OrderLine_ID());
				ioLine.setProcessed(false);
				ioLine.setM_InOut_ID(inout.getM_InOut_ID());
				ioLine.setAD_Org_ID(inout.getAD_Org_ID());
				ioLine.setAD_OrgTrx_ID(inout.getAD_OrgTrx_ID());
				ioLine.setLine(contractPSInOutLines[j].getLine());

				int M_Locator_ID = contractPSInOutLines[j].getM_Locator_ID();
				if(M_Locator_ID > 0)
				{
					ioLine.setM_Locator_ID(M_Locator_ID);

				}else{

					//Default Locator
					M_Locator_ID = MLocator.getDefault(MWarehouse.get(getCtx(), inout.getM_Warehouse_ID(), get_TrxName())).getM_Locator_ID();

					if(M_Locator_ID > 0)
					{
						ioLine.setM_Locator_ID(M_Locator_ID);
					}else {

						//Not Found Locator
						createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_NotFoundLocator, null, contractPSInOutLines[j], null);
						continue;
					}

				}


				if(ioLine.getM_Product_ID() > 0)
					ioLine.setC_UOM_ID(MProduct.get(getCtx(), ioLine.getM_Product_ID()).getC_UOM_ID());
				else
					ioLine.setC_UOM_ID(MUOM.getDefault_UOM_ID(getCtx()));

				ioLine.setQtyEntered(contractPSInOutLines[j].getMovementQty());
				ioLine.setMovementQty(contractPSInOutLines[j].getMovementQty());

				if(contractPSInOutLines[j].getJP_ContractLine_ID() != 0)
				{
					ioLine.set_ValueNoCheck("JP_ContractProcPeriod_ID", JP_ContractProcPeriod_ID);
					ioLine.set_ValueNoCheck("JP_ContractLine_ID", contractPSInOutLines[j].getJP_ContractLine_ID());
				}

				try{
					ioLine.saveEx(get_TrxName());
				} catch (AdempiereException e) {
					createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SaveError, null, inout, e.getMessage());
					throw e;
				}finally {
					;
				}

				contractPSInOutLines[j].setIsFactCreatedJP(true);
				contractPSInOutLines[j].setM_InOutLine_ID(ioLine.getM_InOutLine_ID());
				try{
					contractPSInOutLines[j].saveEx(get_TrxName());
				} catch (AdempiereException e) {
					createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SaveError, null, contractPSInOutLines[j], e.getMessage());
					throw e;
				}finally {
					;
				}

			}//for J

			//Doc Action
			String docAction = getDocAction();
			if(!Util.isEmpty(docAction))
			{
				if(!inout.processIt(docAction))
				{
					createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_DocumentActionError, null, inout, inout.getProcessMsg());
					throw new AdempiereException(inout.getProcessMsg());
				}

				if(!docAction.equals(DocAction.ACTION_Complete))
				{
					inout.setDocAction(DocAction.ACTION_Complete);
					try {
						inout.saveEx(get_TrxName());
					} catch (AdempiereException e) {
						createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SaveError, null, inout, e.getMessage());
						throw e;
					}finally {
						;
					}
				}

			}else{

				inout.setDocAction(DocAction.ACTION_Complete);
				try {
					inout.saveEx(get_TrxName());//DocStatus is Draft
				} catch (AdempiereException e) {
					createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SaveError, null, inout, e.getMessage());
					throw e;
				}finally {
					;
				}
			}

			createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_CreatedDocument, null, inout, null);

		}//for i


		return "";

	}//doIt()



}
