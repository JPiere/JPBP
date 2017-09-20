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

import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MProduct;
import org.compiere.model.MUOM;
import org.compiere.model.PO;
import org.compiere.process.DocAction;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.MContract;
import jpiere.base.plugin.org.adempiere.model.MContractLine;
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
			;//TODO エラー処理
			return "";
		}
		
		MContractProcPeriod orderProcPeriod = getBaseDocContractProcPeriodFromDerivativeDocContractProcPeriod(JP_ContractProcPeriod_ID);
		if(m_ContractContent.getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract)
				&& orderProcPeriod == null)
		{
			;//TODO エラー処理
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
			
			/** Create Invoice header */
			MInvoice invoice = new MInvoice(getCtx(), 0, get_TrxName());
			PO.copyValues(orders[i], invoice);
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
			MOrderLine[] orderLines = orders[i].getLines(true, "");
			boolean isCrateDocLine = false;
			for(int j = 0; j < orderLines.length; j++)
			{
				int JP_ContractLine_ID = orderLines[j].get_ValueAsInt("JP_ContractLine_ID");
				if(JP_ContractLine_ID == 0)
					continue;
				
				MContractLine contractLine = MContractLine.get(getCtx(), JP_ContractLine_ID);
				if(!contractLine.isCreateDocLineJP())
					continue;
				
				//Check Overlap
				if(isOverlapPeriodInvoiceLine(contractLine, JP_ContractProcPeriod_ID))
				{
					//TODO ログは欲しい。
					continue;
				}
				
				//check Lump or Divide
				if(contractLine.getJP_DerivativeDocPolicy_Inv().equals("LP"))
				{
					if(contractLine.getJP_ContractProcPeriod_Inv_ID() != JP_ContractProcPeriod_ID)
						continue;
				}
				
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
					isCrateDocLine = true;
					
					
				}else{
					
					;//TODO 数量が足りないのでエラー。ログは欲しい
					
				}
				
				
			}//for J
			
			if(isCrateDocLine)
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
				
				invoice.deleteEx(true, get_TrxName());
//				createLog(invoice, invoice.getDocumentInfo(), true); TODO createLog()を書く
			}
			
			createLog(invoice, invoice.getDocumentNo(), null, JP_ContractProcessTraceLevel_Fine ,true);
			
			
		}//for i
		
		return "";
	}
	
}
