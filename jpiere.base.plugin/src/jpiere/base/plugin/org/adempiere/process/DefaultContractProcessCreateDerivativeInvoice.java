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

import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MProduct;
import org.compiere.model.MUOM;
import org.compiere.model.PO;
import org.compiere.process.DocAction;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.MContract;
import jpiere.base.plugin.org.adempiere.model.MContractLine;
import jpiere.base.plugin.org.adempiere.model.MContractLogDetail;
import jpiere.base.plugin.org.adempiere.model.MContractProcPeriod;

/** 
* JPIERE-0363
*
* @author Hideaki Hagiwara
*
*/
public class DefaultContractProcessCreateDerivativeInvoice extends AbstractContractProcess {

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
		
		//Check Header Overlap -> Unnecessary. because order : invoice = 1 : N. need overlap. 
//		if(isOverlapPeriodInvoice(orderProcPeriod.getJP_ContractProcPeriod_ID()))
//			return "";
		
		
		MOrder[] orders = m_ContractContent.getOrderByContractPeriod(getCtx(), orderProcPeriod.getJP_ContractProcPeriod_ID(), get_TrxName());
		for(int i = 0; i < orders.length; i++)
		{
			if(!orders[i].getDocStatus().equals(DocAction.STATUS_Completed))
				continue;
			
			/** Pre check - Pre judgment create Document or not. */
			MOrderLine[] orderLines = orders[i].getLines(true, "");
			boolean isCreateDocLine = false;
			boolean isOverQtyOrdered = false;
			ArrayList<MOrderLine> overQtyOrderedLineList = new ArrayList<MOrderLine>();
			for(int j = 0; j < orderLines.length; j++)
			{
				int JP_ContractLine_ID = orderLines[j].get_ValueAsInt("JP_ContractLine_ID");
				if(JP_ContractLine_ID == 0)
					continue;
				
				MContractLine contractLine = MContractLine.get(getCtx(), JP_ContractLine_ID);
				if(!contractLine.isCreateDocLineJP())
					continue;
				
				//Check Overlap
				MInvoiceLine[] iLines = contractLine.getInvoiceLineByContractPeriod(getCtx(), JP_ContractProcPeriod_ID, get_TrxName());;
				if(iLines != null && iLines.length > 0)
					continue;
				
				//check Lump or Divide
				if(contractLine.getJP_DerivativeDocPolicy_Inv().equals("LP"))
				{
					if(contractLine.getJP_ProcPeriod_Lump_Inv_ID() != JP_ContractProcPeriod_ID)
						continue;
				}
				
				//TODO Start と End Period のチェックロジックの実装。
				
				BigDecimal qtyInvoiced = contractLine.getQtyInvoiced();
				BigDecimal qtyToInvoice = orderLines[j].getQtyOrdered().subtract(orderLines[j].getQtyInvoiced());
				if(qtyToInvoice.compareTo(qtyInvoiced) >= 0)
				{
					isCreateDocLine = true;
					break;
				}else{
					isOverQtyOrdered = true;
					overQtyOrderedLineList.add(orderLines[j]);
				}
			}
			
			if(!isCreateDocLine)
			{
				if(isOverQtyOrdered)
				{
					for(MOrderLine oLine : overQtyOrderedLineList)
						createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_OverOrderedQuantity, null, oLine, null);	
				}else{				
					createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_AllContractContentLineWasSkipped, null, orders[i], null);	
				}
				continue;
			}
				
			
			/** Create Invoice header */
			isCreateDocLine = false; //Reset
			MInvoice invoice = new MInvoice(getCtx(), 0, get_TrxName());
			PO.copyValues(orders[i], invoice);
			if(orders[i].getBill_BPartner_ID() > 0)
				invoice.setC_BPartner_ID(orders[i].getBill_BPartner_ID());
			if(orders[i].getBill_Location_ID() > 0)
				invoice.setC_BPartner_Location_ID(orders[i].getBill_Location_ID());			
			if(orders[i].getBill_User_ID() > 0)
				invoice.setAD_User_ID(orders[i].getBill_User_ID());					
			invoice.setC_Order_ID(orders[i].getC_Order_ID());
			invoice.setProcessed(false);
			invoice.setDocStatus(DocAction.STATUS_Drafted);
			invoice.setAD_Org_ID(m_ContractContent.getAD_Org_ID());
			invoice.setAD_OrgTrx_ID(m_ContractContent.getAD_OrgTrx_ID());
			invoice.setDateInvoiced(getDateDoc());		
			invoice.setDocumentNo(""); //Reset Document No
			invoice.setC_DocTypeTarget_ID(orders[i].getC_DocTypeTarget().getC_DocTypeInvoice_ID());
			invoice.setDateAcct(getDateAcct());
			invoice.saveEx(get_TrxName());
			
			
			orders[i].set_TrxName(get_TrxName());
			isCreateDocLine = false; //Reset
			for(int j = 0; j < orderLines.length; j++)
			{
				int JP_ContractLine_ID = orderLines[j].get_ValueAsInt("JP_ContractLine_ID");
				if(JP_ContractLine_ID == 0)
					continue;
				
				MContractLine contractLine = MContractLine.get(getCtx(), JP_ContractLine_ID);
				if(!contractLine.isCreateDocLineJP())
					continue;
				
				//Check Overlap
				MInvoiceLine[] iLines = contractLine.getInvoiceLineByContractPeriod(getCtx(), JP_ContractProcPeriod_ID, get_TrxName());;
				if(iLines != null && iLines.length > 0)
				{
					createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SkippedContractProcessForOverlapContractProcessPeriod, contractLine, iLines[0], null);	
					continue;					
				}
				
				//check Lump or Divide
				//TODO ログ化
				if(contractLine.getJP_DerivativeDocPolicy_Inv().equals("LP"))
				{
					if(contractLine.getJP_ProcPeriod_Lump_Inv_ID() != JP_ContractProcPeriod_ID)
						continue;
				}
				
				//TODO Start と End Period のチェックロジックの実装。
				
				//Check over ordered qty
				BigDecimal qtyInvoiced = contractLine.getQtyInvoiced();
				BigDecimal qtyToInvoice = orderLines[j].getQtyOrdered().subtract(orderLines[j].getQtyInvoiced());
				if(qtyToInvoice.compareTo(qtyInvoiced) >= 0)
				{
					
					MInvoiceLine iLine = new MInvoiceLine(getCtx(), 0, get_TrxName());
					PO.copyValues(orderLines[j], iLine);
					iLine.setC_OrderLine_ID(orderLines[j].getC_OrderLine_ID());
					iLine.setProcessed(false);
					iLine.setC_Invoice_ID(invoice.getC_Invoice_ID());
					iLine.setAD_Org_ID(invoice.getAD_Org_ID());
					iLine.setAD_OrgTrx_ID(invoice.getAD_OrgTrx_ID());
					iLine.setQtyEntered(contractLine.getQtyInvoiced());
					if(iLine.getM_Product_ID() > 0)
						iLine.setC_UOM_ID(MProduct.get(getCtx(), iLine.getM_Product_ID()).getC_UOM_ID());
					else
						iLine.setC_UOM_ID(MUOM.getDefault_UOM_ID(getCtx()));
					iLine.setQtyInvoiced(contractLine.getQtyInvoiced());
					iLine.set_ValueNoCheck("JP_ContractProcPeriod_ID", JP_ContractProcPeriod_ID);
					
					iLine.saveEx(get_TrxName());
					isCreateDocLine = true;
					
				}else{
					
					//Over Ordered Quantity
					createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_OverOrderedQuantity, contractLine, orderLines[j], null);	
				}
				
				
			}//for J
			
			if(isCreateDocLine)
			{
				String docAction = getDocAction();
				if(!Util.isEmpty(docAction))
				{
					invoice.processIt(docAction);
					if(!docAction.equals(DocAction.ACTION_Complete))
						invoice.saveEx(get_TrxName());
				}else{
					invoice.saveEx(get_TrxName());//DocStatus is Draft
				}
				
			}else{
				
				//if by any chance
				invoice.deleteEx(true, get_TrxName());
				createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_AllContractContentLineWasSkipped, null, orders[i], null);				
				continue;
			}
			
			createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_CreatedDocument, null, invoice, null);
			
		}//for i
		
		return "";
	}
	
}
