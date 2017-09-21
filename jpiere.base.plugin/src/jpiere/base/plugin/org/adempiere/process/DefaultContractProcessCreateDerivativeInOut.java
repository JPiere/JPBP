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

import org.compiere.model.MInOut;
import org.compiere.model.MInOutLine;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MProduct;
import org.compiere.model.MUOM;
import org.compiere.model.MWarehouse;
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
public class DefaultContractProcessCreateDerivativeInOut extends AbstractContractProcess {

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
		
		//Check header Overlap -> Unnecessary. because order : invoice = 1 : N. need overlap. 
//		if(isOverlapPeriodInOut(orderProcPeriod.getJP_ContractProcPeriod_ID()))
//			return "";
		
		
		MOrder[] orders = m_ContractContent.getOrderByContractPeriod(getCtx(), orderProcPeriod.getJP_ContractProcPeriod_ID(), get_TrxName());
		for(int i = 0; i < orders.length; i++)
		{
			if(!orders[i].getDocStatus().equals(DocAction.STATUS_Completed))
				continue;
			
			
			//TODO ヘッダーを作る前に、処理対象となる明細があるかどうかのチェク。あればヘッダーを作成する
			
			
			
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
					
				//Check Overlap Line
				if(isOverlapPeriodInOutLine(contractLine, JP_ContractProcPeriod_ID))
				{
					//TODO ログは欲しい。
					continue;
				}
				
				//check Lump or Divide
				if(contractLine.getJP_DerivativeDocPolicy_InOut().equals("LP"))
				{
					if(contractLine.getJP_ContractProcPeriod_InOut_ID() != JP_ContractProcPeriod_ID)
						continue;
				}
				
				
				BigDecimal movementQty = contractLine.getMovementQty();
				BigDecimal qtyToDeliver = orderLines[j].getQtyOrdered().subtract(orderLines[j].getQtyDelivered());
				if(qtyToDeliver.compareTo(movementQty) >= 0)
				{
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
						
						;//TODO Erroe 保管場所が見つかりません。
					}
					
					
					if(ioLine.getM_Product_ID() > 0)
						ioLine.setC_UOM_ID(MProduct.get(getCtx(), ioLine.getM_Product_ID()).getC_UOM_ID());
					else
						ioLine.setC_UOM_ID(MUOM.getDefault_UOM_ID(getCtx()));
					
					ioLine.setQtyEntered(contractLine.getMovementQty());
					ioLine.setMovementQty(contractLine.getMovementQty());
					ioLine.set_ValueNoCheck("JP_ContractProcPeriod_ID", JP_ContractProcPeriod_ID);
					
					ioLine.saveEx(get_TrxName());
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
					inout.processIt(docAction);
					if(!docAction.equals(DocAction.ACTION_Complete))
						inout.saveEx(get_TrxName());
				}else{
					inout.saveEx(get_TrxName());//DocStatus is Draft
				}
				
			}else{
				
				inout.deleteEx(true, get_TrxName());
//				createLog(invoice, invoice.getDocumentInfo(), true); TODO createLog()を書く
			}
			
			createLog(inout, inout.getDocumentNo(), null, "FIN" ,true);
			
			
		}//for i
		

		

		
		return "";
	}
	
	
	
	
}
