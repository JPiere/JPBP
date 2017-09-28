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
import java.util.ArrayList;

import org.compiere.model.MInOut;
import org.compiere.model.MInOutLine;
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
import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractLine;
import jpiere.base.plugin.org.adempiere.model.MContractLogDetail;
import jpiere.base.plugin.org.adempiere.model.MContractProcPeriod;

/** 
* JPIERE-0363
*
* @author Hideaki Hagiwara
*
*/
public class DefaultContractProcessCreateDerivativeInOut extends AbstractContractProcess {

	
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
		
		
		//Check header Overlap -> Unnecessary. because order : invoice = 1 : N. need overlap. 
		//MInout[] inouts = m_ContractContent.getInOutByContractPeriod(Env.getCtx(), JP_ContractProcPeriod_ID, get_TrxName());
//		if(inouts != null && orders.length > 0)
//		{ return "";}
		
		
		MOrder[] orders = m_ContractContent.getOrderByContractPeriod(getCtx(), orderProcPeriod.getJP_ContractProcPeriod_ID(), get_TrxName());
		for(int i = 0; i < orders.length; i++)
		{
			if(!orders[i].getDocStatus().equals(DocAction.STATUS_Completed))
				continue;
			
			/** Pre check - Pre judgment create Document or not. */
			MOrderLine[] orderLines = orders[i].getLines(true, "");
			boolean isCreateDocLine = false;
			ArrayList<MOrderLine> overQtyOrderedLineList = new ArrayList<MOrderLine>();
			for(int j = 0; j < orderLines.length; j++)
			{
				if(!isCreateInOutLine(orderLines[j], JP_ContractProcPeriod_ID, false))
					continue;	
				
				isCreateDocLine = true;
				break;
			}
			
			if(!isCreateDocLine)
			{
				if(overQtyOrderedLineList.size() > 0)
				{
					for(MOrderLine oLine : overQtyOrderedLineList)
						createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_OverOrderedQuantity, null, oLine, null);	
				}else{				
					createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_AllContractContentLineWasSkipped, null, orders[i], null);	
				}
				continue;
			}
			
			/** Create InOut header */
			MInOut inout = new MInOut(getCtx(), 0, get_TrxName());
			PO.copyValues(orders[i], inout);
			inout.setC_Order_ID(orders[i].getC_Order_ID());
			inout.setProcessed(false);
			inout.setDocStatus(DocAction.STATUS_Drafted);
			inout.setAD_Org_ID(m_ContractContent.getAD_Org_ID());
			inout.setAD_OrgTrx_ID(m_ContractContent.getAD_OrgTrx_ID());	
			inout.setDocumentNo(""); //Reset Document No
			inout.setC_DocType_ID(orders[i].getC_DocTypeTarget().getC_DocTypeShipment_ID());
			inout.setMovementDate(getDateAcct());
			inout.setDateAcct(getDateAcct());
			if(orders[i].isSOTrx())
				inout.setMovementType(MInOut.MOVEMENTTYPE_CustomerShipment);
			else
				inout.setMovementType(MInOut.MOVEMENTTYPE_VendorReceipts);
			
			inout.saveEx(get_TrxName());
			
			
			orders[i].set_TrxName(get_TrxName());
			isCreateDocLine = false; //Reset
			for(int j = 0; j < orderLines.length; j++)
			{
				if(!isCreateInOutLine(orderLines[j], JP_ContractProcPeriod_ID, true))
					continue;	
				
				int JP_ContractLine_ID = orderLines[j].get_ValueAsInt("JP_ContractLine_ID");				
				MContractLine contractLine = MContractLine.get(getCtx(), JP_ContractLine_ID);
				MInOutLine ioLine = new MInOutLine(getCtx(), 0, get_TrxName());
				PO.copyValues(orderLines[j], ioLine);
				ioLine.setC_OrderLine_ID(orderLines[j].getC_OrderLine_ID());
				ioLine.setProcessed(false);
				ioLine.setM_InOut_ID(inout.getM_InOut_ID());
				ioLine.setAD_Org_ID(inout.getAD_Org_ID());
				ioLine.setAD_OrgTrx_ID(inout.getAD_OrgTrx_ID());
				int M_Locator_ID = orderLines[j].get_ValueAsInt("JP_Locator_ID");
				if(M_Locator_ID > 0)
				{
					ioLine.setM_Locator_ID(M_Locator_ID);
				
				}else if(contractLine.getJP_Locator().getM_Warehouse_ID() == orderLines[j].getM_Warehouse_ID()) {
				
					ioLine.setM_Locator_ID(contractLine.getJP_Locator_ID());
					
				}else if(MWarehouse.get(getCtx(), inout.getM_Warehouse_ID()).getDefaultLocator().getM_Locator_ID() > 0){
					
					ioLine.setM_Locator_ID(MWarehouse.get(getCtx(), inout.getM_Warehouse_ID()).getDefaultLocator().getM_Locator_ID());
					
				}else{
					
					//Not Found Locator
					createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_NotFoundLocator, contractLine, orderLines[j], null);
					continue;
					
				}
				
				
				if(ioLine.getM_Product_ID() > 0)
					ioLine.setC_UOM_ID(MProduct.get(getCtx(), ioLine.getM_Product_ID()).getC_UOM_ID());
				else
					ioLine.setC_UOM_ID(MUOM.getDefault_UOM_ID(getCtx()));
				
				ioLine.setQtyEntered(contractLine.getMovementQty());
				ioLine.setMovementQty(contractLine.getMovementQty());
				ioLine.set_ValueNoCheck("JP_ContractProcPeriod_ID", JP_ContractProcPeriod_ID);
				
				ioLine.saveEx(get_TrxName());
				isCreateDocLine = true;
						
			}//for J
			
			if(isCreateDocLine)
			{
				String docAction = getDocAction();
				if(!Util.isEmpty(docAction))
				{
					inout.processIt(docAction);
					if(!docAction.equals(DocAction.ACTION_Complete))
						inout.saveEx(get_TrxName());
				}else{
					inout.saveEx(get_TrxName());//DocStatus is Draft
				}
				
			}else{
				
				//if by any chance
				inout.deleteEx(true, get_TrxName());
				createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_AllContractContentLineWasSkipped, null, null, null);	
				continue;
			}
			
			createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_CreatedDocument, null, inout, null);
			
		}//for i
		
		
		return "";
	}
	
	private boolean isCreateInOutLine(MOrderLine orderLine, int JP_ContractProcPeriod_ID, boolean isCreateLog)
	{
		int JP_ContractLine_ID = orderLine.get_ValueAsInt("JP_ContractLine_ID");
		if(JP_ContractLine_ID == 0)
			return false;
		
		MContractLine contractLine = MContractLine.get(getCtx(), JP_ContractLine_ID);
		
		//Check Contract Process
		if(contractLine.getJP_ContractProcess_InOut_ID() != getJP_ContractProcess_ID())
			return false;
		
		
		if(!contractLine.isCreateDocLineJP())
		{
			if(isCreateLog)
				createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForCreateDocLineIsFalse, contractLine, null, null);
			
			return false;
		}
		
		
		//Check Overlap
		MInOutLine[] ioLines = contractLine.getInOutLineByContractPeriod(getCtx(), JP_ContractProcPeriod_ID, get_TrxName());;
		if(ioLines != null && ioLines.length > 0)
		{
			if(isCreateLog)
				createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SkippedContractProcessForOverlapContractProcessPeriod, contractLine, ioLines[0], null);
			
			return false;
		}
		
		
		//Check Derivative Ship/Recipt Doc Line
		if(m_ContractContent.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceipt) ||
				m_ContractContent.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceiptInvoice) )
		{
			
			//Lump
			if(contractLine.getJP_DerivativeDocPolicy_InOut().equals(MContractLine.JP_DERIVATIVEDOCPOLICY_INOUT_LumpOnACertainPointOfContractProcessPeriod))
			{
				MContractProcPeriod lump_ContractProcPeriod = MContractProcPeriod.get(getCtx(), contractLine.getJP_ProcPeriod_Lump_InOut_ID());
				if(!lump_ContractProcPeriod.isContainedBaseDocContractProcPeriod(JP_ContractProcPeriod_ID))
				{
					if(isCreateLog)
						createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForOutsideOfTheDerivativeDocPeriod, contractLine, null, null);
					
					return false;
				}
			}
			
			//Start Period
			if(contractLine.getJP_DerivativeDocPolicy_InOut().equals(MContractLine.JP_DERIVATIVEDOCPOLICY_INOUT_FromStartContractProcessPeriod)
					||contractLine.getJP_DerivativeDocPolicy_InOut().equals(MContractLine.JP_DERIVATIVEDOCPOLICY_INOUT_FromStartContractProcessPeriodToEnd) )
			{
				MContractProcPeriod start_ContractProcPeriod = MContractProcPeriod.get(getCtx(), contractLine.getJP_ProcPeriod_Start_InOut_ID());
				if(!start_ContractProcPeriod.isContainedBaseDocContractProcPeriod(JP_ContractProcPeriod_ID))
				{
					if(isCreateLog)
						createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForOutsideOfTheDerivativeDocPeriod, contractLine, null, null);
					
					return false;
				}
			}
			
			//End Period
			if(contractLine.getJP_DerivativeDocPolicy_InOut().equals(MContractLine.JP_DERIVATIVEDOCPOLICY_INOUT_ToEndContractProcessPeriod)
					||contractLine.getJP_DerivativeDocPolicy_InOut().equals(MContractLine.JP_DERIVATIVEDOCPOLICY_INOUT_FromStartContractProcessPeriodToEnd) )
			{
				MContractProcPeriod end_ContractProcPeriod = MContractProcPeriod.get(getCtx(), contractLine.getJP_ProcPeriod_End_InOut_ID());
				if(!end_ContractProcPeriod.isContainedBaseDocContractProcPeriod(JP_ContractProcPeriod_ID))
				{
					if(isCreateLog)
						createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForOutsideOfTheDerivativeDocPeriod, contractLine, null, null);	
					
					return false;
				}
			}
		}
		
		
		BigDecimal movementQty = contractLine.getMovementQty();
		BigDecimal qtyToDeliver = orderLine.getQtyOrdered().subtract(orderLine.getQtyDelivered());
		if(qtyToDeliver.compareTo(movementQty) < 0)
		{
			if(isCreateLog)
				createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_OverOrderedQuantity, contractLine, orderLine, null);
			else
				overQtyOrderedLineList.add(orderLine);
			
			return false;
		}
		
		return true;
	}
	
	
}
