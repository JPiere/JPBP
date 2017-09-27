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
import org.compiere.util.Env;
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
		
		//TODO 期間が無い旨のエラーにする
		if(m_ContractContent.getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract)
				&& JP_ContractProcPeriod_ID == 0)
		{
			String descriptionMsg = Msg.getMsg(getCtx(), "NotFound") + " : " + Msg.getElement(getCtx(), "JP_ContractProcPeriod_ID");
			createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_UnexpectedError, null,  null, descriptionMsg);
			return "";
		}
		
		//Check Overlap Header
		MOrder[] orders = m_ContractContent.getOrderByContractPeriod(Env.getCtx(), JP_ContractProcPeriod_ID, get_TrxName());
		if(orders != null && orders.length > 0)
		{
			createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SkipContractProcessForOverlapContractProcessPeriod, null,  orders[0], null);			
			return "";
		}//Check Overlap
		
		
		/** Pre check - Pre judgment create Document or not. */
		MContractLine[] 	m_lines = m_ContractContent.getLines();
		boolean isCreateDocLine = false;
		for(int i = 0; i < m_lines.length; i++)
		{			
			if(!isCreateDocLine(m_lines[i], JP_ContractProcPeriod_ID, false))
				continue;
			
			isCreateDocLine = true;
			break;
		}
		
		
		if(!isCreateDocLine)
		{
			createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_AllContractContentLineWasSkipped, null, null, null);	
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
		isCreateDocLine = false; //Reset
		for(int i = 0; i < m_lines.length; i++)
		{
			if(!isCreateDocLine(m_lines[i], JP_ContractProcPeriod_ID, true))
				continue;
			
			MOrderLine oline = new MOrderLine(getCtx(), 0, get_TrxName());
			PO.copyValues(m_lines[i], oline);
			oline.setC_Order_ID(order.getC_Order_ID());
			oline.setAD_Org_ID(order.getAD_Org_ID());
			oline.setAD_OrgTrx_ID(order.getAD_OrgTrx_ID());
			oline.setProcessed(false);
			oline.setQtyReserved(Env.ZERO);
			oline.setQtyDelivered(Env.ZERO);
			oline.setQtyInvoiced(Env.ZERO);
			oline.set_ValueNoCheck("JP_ContractLine_ID", m_lines[i].getJP_ContractLine_ID());
			if(m_ContractContent.getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
				oline.set_ValueOfColumn("JP_ContractProcPeriod_ID", JP_ContractProcPeriod_ID);
			oline.setDatePromised(getOrderLineDatePromised(m_lines[i]));
			
			oline.saveEx(get_TrxName());
			isCreateDocLine = true;
		}
		
		
		if(isCreateDocLine)
		{
			String docAction = getDocAction();
			updateContractProcStatus();
			if(!Util.isEmpty(docAction))
			{
				order.processIt(docAction);
				if(!docAction.equals(DocAction.ACTION_Complete))
				{
					order.setDocAction(DocAction.ACTION_Complete);
					order.saveEx(get_TrxName());
				}
			}else{
				order.setDocAction(DocAction.ACTION_Complete);
				order.saveEx(get_TrxName());//DocStatus is Draft
			}
			
		}else{
			
			//if by any chance
			order.deleteEx(true, get_TrxName());
			createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_AllContractContentLineWasSkipped, null, null, null);			
			return "";
		}
		

		createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_CreateDocument, null, order, null);
		return "";
		
	}
	
	
	private boolean isCreateDocLine(MContractLine contractLine, int JP_ContractProcPeriod_ID, boolean isCreateLog)
	{
		if(!contractLine.isCreateDocLineJP())
			return false;		
		
		if(!m_ContractContent.getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
			return true;
	
		//Check Overlap
		MOrderLine[] oLines = contractLine.getOrderLineByContractPeriod(getCtx(), JP_ContractProcPeriod_ID, get_TrxName());
		if(oLines != null && oLines.length > 0)
			return false;
	
		if(contractLine.getJP_BaseDocLinePolicy() != null && 
				( m_ContractContent.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_Manual) 
				||  !m_ContractContent.getOrderType().equals(MContractContent.ORDERTYPE_StandardOrder)) )
		{
			if(contractLine.getJP_BaseDocLinePolicy().equals(MContractLine.JP_BASEDOCLINEPOLICY_LumpOnACertainPointOfContractProcessPeriod))
			{
				MContractProcPeriod lump_ContractProcPeriod = MContractProcPeriod.get(getCtx(), contractLine.getJP_ProcPeriod_Lump_ID());
				if(!lump_ContractProcPeriod.isContainedBaseDocContractProcPeriod(JP_ContractProcPeriod_ID))
					return false;
			}

			if(contractLine.getJP_BaseDocLinePolicy().equals(MContractLine.JP_BASEDOCLINEPOLICY_FromStartContractProcessPeriod)
					||contractLine.getJP_BaseDocLinePolicy().equals(MContractLine.JP_BASEDOCLINEPOLICY_FromStartContractProcessPeriodToEnd) )
			{
				MContractProcPeriod start_ContractProcPeriod = MContractProcPeriod.get(getCtx(), contractLine.getJP_ProcPeriod_Start_ID());
				if(!start_ContractProcPeriod.isContainedBaseDocContractProcPeriod(JP_ContractProcPeriod_ID))
					return false;
			}
			
			if(contractLine.getJP_BaseDocLinePolicy().equals(MContractLine.JP_BASEDOCLINEPOLICY_ToEndContractProcessPeriod)
					||contractLine.getJP_BaseDocLinePolicy().equals(MContractLine.JP_BASEDOCLINEPOLICY_FromStartContractProcessPeriodToEnd) )
			{
				MContractProcPeriod end_ContractProcPeriod = MContractProcPeriod.get(getCtx(), contractLine.getJP_ProcPeriod_End_ID());
				if(!end_ContractProcPeriod.isContainedBaseDocContractProcPeriod(JP_ContractProcPeriod_ID))
					return false;
			}
			
			return true;
		}
		
		
		if(m_ContractContent.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceipt) ||
				m_ContractContent.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceiptInvoice) )
		{
			
			
			if(contractLine.getJP_DerivativeDocPolicy_InOut().equals(MContractLine.JP_DERIVATIVEDOCPOLICY_INOUT_LumpOnACertainPointOfContractProcessPeriod))
			{
				MContractProcPeriod lump_ContractProcPeriod = MContractProcPeriod.get(getCtx(), contractLine.getJP_ProcPeriod_Lump_InOut_ID());
				if(!lump_ContractProcPeriod.isContainedBaseDocContractProcPeriod(JP_ContractProcPeriod_ID))
					return false;
			}
			
			if(contractLine.getJP_DerivativeDocPolicy_InOut().equals(MContractLine.JP_DERIVATIVEDOCPOLICY_INOUT_FromStartContractProcessPeriod)
					||contractLine.getJP_DerivativeDocPolicy_InOut().equals(MContractLine.JP_DERIVATIVEDOCPOLICY_INOUT_FromStartContractProcessPeriodToEnd) )
			{
				MContractProcPeriod start_ContractProcPeriod = MContractProcPeriod.get(getCtx(), contractLine.getJP_ProcPeriod_Start_InOut_ID());
				if(!start_ContractProcPeriod.isContainedBaseDocContractProcPeriod(JP_ContractProcPeriod_ID))
					return false;
			}
			
			if(contractLine.getJP_DerivativeDocPolicy_InOut().equals(MContractLine.JP_DERIVATIVEDOCPOLICY_INOUT_ToEndContractProcessPeriod)
					||contractLine.getJP_DerivativeDocPolicy_InOut().equals(MContractLine.JP_DERIVATIVEDOCPOLICY_INOUT_FromStartContractProcessPeriodToEnd) )
			{
				MContractProcPeriod end_ContractProcPeriod = MContractProcPeriod.get(getCtx(), contractLine.getJP_ProcPeriod_End_InOut_ID());
				if(!end_ContractProcPeriod.isContainedBaseDocContractProcPeriod(JP_ContractProcPeriod_ID))
					return false;
			}
		}
		
		
		if(m_ContractContent.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateInvoice) ||
				m_ContractContent.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceiptInvoice) )
		{
			if(contractLine.getJP_DerivativeDocPolicy_Inv().equals(MContractLine.JP_DERIVATIVEDOCPOLICY_INV_LumpOnACertainPointOfContractProcessPeriod))
			{
				MContractProcPeriod lump_ContractProcPeriod = MContractProcPeriod.get(getCtx(),contractLine.getJP_ProcPeriod_Lump_Inv_ID());
				if(!lump_ContractProcPeriod.isContainedBaseDocContractProcPeriod(JP_ContractProcPeriod_ID))
					return false;
			}
			
			if(contractLine.getJP_DerivativeDocPolicy_Inv().equals(MContractLine.JP_DERIVATIVEDOCPOLICY_INV_FromStartContractProcessPeriod)
					||contractLine.getJP_DerivativeDocPolicy_Inv().equals(MContractLine.JP_DERIVATIVEDOCPOLICY_INV_FromStartContractProcessPeriodToEnd) )
			{
				MContractProcPeriod start_ContractProcPeriod = MContractProcPeriod.get(getCtx(),contractLine.getJP_ProcPeriod_Start_Inv_ID());
				if(!start_ContractProcPeriod.isContainedBaseDocContractProcPeriod(JP_ContractProcPeriod_ID))
					return false;
			}
			
			if(contractLine.getJP_DerivativeDocPolicy_Inv().equals(MContractLine.JP_DERIVATIVEDOCPOLICY_INV_ToEndContractProcessPeriod)
					|| contractLine.getJP_DerivativeDocPolicy_Inv().equals(MContractLine.JP_DERIVATIVEDOCPOLICY_INV_FromStartContractProcessPeriodToEnd) )
			{
				MContractProcPeriod end_ContractProcPeriod = MContractProcPeriod.get(getCtx(), contractLine.getJP_ProcPeriod_End_Inv_ID());
				if(!end_ContractProcPeriod.isContainedBaseDocContractProcPeriod(JP_ContractProcPeriod_ID))
					return false;
			}
		}
		
		return true;
	}
}
