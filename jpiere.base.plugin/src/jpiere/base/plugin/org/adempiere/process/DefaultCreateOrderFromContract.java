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

import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.PO;
import org.compiere.process.SvrProcess;

import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractLine;



/** JPIERE-0363
*
* @author Hideaki Hagiwara
*
*/
public class DefaultCreateOrderFromContract extends SvrProcess {
	
	int Record_ID = 0;
	MContractContent m_ContractContent = null;
	
	@Override
	protected void prepare() 
	{
		Record_ID = getRecord_ID();
		if(Record_ID > 0)
		{
			m_ContractContent = new MContractContent(getCtx(), Record_ID, get_TrxName());
		}else{
			log.log(Level.SEVERE, "Record_ID <= 0 ");
		}
	}
	
	@Override
	protected String doIt() throws Exception 
	{
		//TODO:既に同じ条件の受注伝票/発注伝票が登録されていない事のチェック。※Abstract化できるね
		
		
		MOrder order = new MOrder(getCtx(), 0, get_TrxName());
		PO.copyValues(m_ContractContent, order);
		order.setAD_Org_ID(m_ContractContent.getAD_Org_ID());
		order.setAD_OrgTrx_ID(m_ContractContent.getAD_OrgTrx_ID());
//		order.setJP_Contract_ID(m_Contract.get_ID()); TODO:
		order.setDateOrdered(m_ContractContent.getDateDoc());//TODO DateOrderedと納品予定日は入力パラメータから算出できるようにする
		order.setDateAcct(m_ContractContent.getDateAcct());
		order.setDocumentNo(""); //Reset Document No
		order.setIsSOTrx(m_ContractContent.isSOTrx());
		
		order.saveEx(get_TrxName());
		
		MContractLine[] 	m_lines = m_ContractContent.getLines();
		for(int i = 0; i < m_lines.length; i++)
		{
			MOrderLine oline = new MOrderLine(getCtx(), 0, get_TrxName());
			PO.copyValues(m_lines[i], oline);
			oline.setC_Order_ID(order.getC_Order_ID());
			oline.setAD_Org_ID(order.getAD_Org_ID());
			oline.setAD_OrgTrx_ID(order.getAD_OrgTrx_ID());
			oline.saveEx(get_TrxName());
		}
		
		
		//TODO リターン句でIDを返すのはAbstractにしても良いかも
		
		return String.valueOf(order.get_ID());
	}
	
}
