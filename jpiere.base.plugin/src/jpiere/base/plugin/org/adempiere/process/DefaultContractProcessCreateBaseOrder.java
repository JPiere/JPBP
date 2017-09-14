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

import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.PO;
import org.compiere.process.DocAction;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.MContract;
import jpiere.base.plugin.org.adempiere.model.MContractLine;


/** 
* JPIERE-0363
*
* @author Hideaki Hagiwara
*
*/
public class DefaultContractProcessCreateBaseOrder extends AbstractContractProcess 
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
				|| JP_ContractProcPeriod_ID == 0)
		{
			;//TODO エラー処理
		}
		
		//Check Overlap
		if(isOverlapPeriodOrder(JP_ContractProcPeriod_ID))
		{
			//TODO ログは欲しい。
			return "";
		}
		
		/** Create Order header */
		MOrder order = new MOrder(getCtx(), 0, get_TrxName());

		
		PO.copyValues(m_ContractContent, order);
		order.setProcessed(false);
		order.setDocStatus(DocAction.STATUS_Drafted);
		order.setAD_Org_ID(m_ContractContent.getAD_Org_ID());
		order.setAD_OrgTrx_ID(m_ContractContent.getAD_OrgTrx_ID());
		order.setDateOrdered(getDateOrdered());
		order.setDateAcct(getDateAcct());
		order.setDatePromised(getOrderHeaderDatePromised(p_DateAcct)); //DateAcct is basis.
		order.setDocumentNo(""); //Reset Document No
		order.setC_DocTypeTarget_ID(m_ContractContent.getJP_BaseDocDocType_ID());
		order.set_ValueOfColumn("JP_Contract_ID", m_ContractContent.getParent().getJP_Contract_ID());
		order.set_ValueOfColumn("JP_ContractContent_ID", m_ContractContent.getJP_ContractContent_ID());
		if(m_ContractContent.getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
			order.set_ValueOfColumn("JP_ContractProcPeriod_ID", JP_ContractProcPeriod_ID);
		
		order.saveEx(get_TrxName());
		
		/** Create Order Line */
		MContractLine[] 	m_lines = m_ContractContent.getLines();
		boolean isCrateDocLine = false;
		for(int i = 0; i < m_lines.length; i++)
		{
			if(!m_lines[i].isCreateDocLineJP())
				continue;
			
			//Check Overlap
			if(isOverlapPeriodOrderLine(m_lines[i],JP_ContractProcPeriod_ID))
			{
				//TODO ログは欲しい。
				continue;
			}
			
			MOrderLine oline = new MOrderLine(getCtx(), 0, get_TrxName());
			PO.copyValues(m_lines[i], oline);
			oline.setProcessed(false);
			oline.setC_Order_ID(order.getC_Order_ID());
			oline.setAD_Org_ID(order.getAD_Org_ID());
			oline.setAD_OrgTrx_ID(order.getAD_OrgTrx_ID());
			oline.set_ValueNoCheck("JP_ContractLine_ID", m_lines[i].getJP_ContractLine_ID());
			if(m_ContractContent.getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
				oline.set_ValueOfColumn("JP_ContractProcPeriod_ID", JP_ContractProcPeriod_ID);
			oline.setDatePromised(getOrderLineDatePromised(m_lines[i]));
			
			oline.saveEx(get_TrxName());
			isCrateDocLine = true;
		}
		
		
		if(isCrateDocLine)
		{
			String docAction = getDocAction();
			if(!Util.isEmpty(docAction))
			{
				order.processIt(docAction);
				if(!docAction.equals(DocAction.ACTION_Complete))
					order.saveEx(get_TrxName());
			}else{
				order.saveEx(get_TrxName());//DocStatus is Draft
			}
			
		}else{
			
			order.deleteEx(true, get_TrxName());
//			createLog(invoice, invoice.getDocumentInfo(), true); TODO createLog()を書く
		}
		
		createLog(order, order.getDocumentInfo(), null, JP_ContractProcessTraceLevel_Fine ,true);
		
		return "";
	}
	
}
