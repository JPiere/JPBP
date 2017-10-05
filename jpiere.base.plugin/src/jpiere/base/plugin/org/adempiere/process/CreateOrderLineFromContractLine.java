package jpiere.base.plugin.org.adempiere.process;

import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.PO;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
import org.compiere.util.Msg;

import jpiere.base.plugin.org.adempiere.model.MContract;
import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractLine;
import jpiere.base.plugin.org.adempiere.model.MContractProcPeriod;

public class CreateOrderLineFromContractLine extends SvrProcess {
	
	int record_ID = 0;
	MContractContent m_ContractContent = null;
	
	@Override
	protected void prepare() 
	{
		record_ID = getRecord_ID();
		
	}
	
	@Override
	protected String doIt() throws Exception 
	{
		
		if(record_ID == 0)
			throw new Exception(Msg.getMsg(getCtx(), "ProcessRunError"));
		
		MOrder order = new MOrder(getCtx(), record_ID, get_TrxName());
		MOrderLine[] oLines = order.getLines();
		if(oLines.length > 0)
			throw new Exception(Msg.getMsg(getCtx(), "JP_DocLineCreated"));
		
		int JP_ContractContent_ID = order.get_ValueAsInt("JP_ContractContent_ID");
		if(JP_ContractContent_ID <= 0)
			throw new Exception(Msg.getMsg(getCtx(), "NotFound") + " : " + Msg.getElement(getCtx(), "JP_ContractContent_ID"));
		
		m_ContractContent = MContractContent.get(getCtx(), JP_ContractContent_ID);
		int JP_ContractProcPeriod_ID = order.get_ValueAsInt("JP_ContractProcPeriod_ID");
		
		
		MContractLine[] m_lines = m_ContractContent.getLines();
		for(int i = 0; i < m_lines.length; i++)
		{
			if(JP_ContractProcPeriod_ID > 0)
			{
				if(!isCreateDocLine(m_lines[i], JP_ContractProcPeriod_ID, true))
				continue;
			}
			
			MOrderLine oline = new MOrderLine(getCtx(), 0, get_TrxName());
			PO.copyValues(m_lines[i], oline);
			oline.setC_Order_ID(order.getC_Order_ID());
			oline.setAD_Org_ID(order.getAD_Org_ID());
			oline.setAD_OrgTrx_ID(order.getAD_OrgTrx_ID());
			oline.setProcessed(false);
			
			if(m_lines[i].getC_BPartner_ID() == 0)
				oline.setC_BPartner_ID(order.getC_BPartner_ID());
			if(m_lines[i].getC_BPartner_Location_ID() == 0)
				oline.setC_BPartner_Location_ID(order.getC_BPartner_Location_ID());
			oline.setM_Warehouse_ID(order.getM_Warehouse_ID());
			oline.setC_Currency_ID(order.getC_Currency_ID());
			
			//Qty
			if(m_lines[i].getM_Product_ID() > 0)
			{
				oline.setC_UOM_ID(m_lines[i].getM_Product().getC_UOM_ID());
				oline.setQtyEntered(m_lines[i].getQtyOrdered());
			}else{
				oline.setQtyEntered(m_lines[i].getQtyEntered());				
			}	
			oline.setQtyOrdered(m_lines[i].getQtyOrdered());
			oline.setQtyReserved(Env.ZERO);
			oline.setQtyDelivered(Env.ZERO);
			oline.setQtyInvoiced(Env.ZERO);
			
			//Contract Info
			oline.set_ValueNoCheck("JP_ContractLine_ID", m_lines[i].getJP_ContractLine_ID());
			if(m_ContractContent.getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
				oline.set_ValueOfColumn("JP_ContractProcPeriod_ID", JP_ContractProcPeriod_ID);
			
			//Date
			oline.setDateOrdered(order.getDateOrdered());
			oline.setDatePromised(order.getDatePromised());
			
			
			oline.saveEx(get_TrxName());
		}
		
		return Msg.getMsg(getCtx(),"Success");
	}
	
	private boolean isCreateDocLine(MContractLine contractLine, int JP_ContractProcPeriod_ID, boolean isCreateLog)
	{
		if(!contractLine.isCreateDocLineJP())
			return false;
		
		if(!m_ContractContent.getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
			return true;
	
		//Check Overlap
//		MOrderLine[] oLines = contractLine.getOrderLineByContractPeriod(getCtx(), JP_ContractProcPeriod_ID, get_TrxName());
//		if(oLines != null && oLines.length > 0)
//			return false;
		
		//Check Base Doc Line
		if(contractLine.getJP_BaseDocLinePolicy() != null && 
				( m_ContractContent.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_Manual) 
				||  !m_ContractContent.getOrderType().equals(MContractContent.ORDERTYPE_StandardOrder)) )
		{
			//Lump
			if(contractLine.getJP_BaseDocLinePolicy().equals(MContractLine.JP_BASEDOCLINEPOLICY_LumpOnACertainPointOfContractProcessPeriod))
			{
				MContractProcPeriod lump_ContractProcPeriod = MContractProcPeriod.get(getCtx(), contractLine.getJP_ProcPeriod_Lump_ID());
				if(!lump_ContractProcPeriod.isContainedBaseDocContractProcPeriod(JP_ContractProcPeriod_ID))
					return false;
			}

			//Start Period
			if(contractLine.getJP_BaseDocLinePolicy().equals(MContractLine.JP_BASEDOCLINEPOLICY_FromStartContractProcessPeriod)
					||contractLine.getJP_BaseDocLinePolicy().equals(MContractLine.JP_BASEDOCLINEPOLICY_FromStartContractProcessPeriodToEnd) )
			{
				MContractProcPeriod contractLine_Period = MContractProcPeriod.get(getCtx(), contractLine.getJP_ProcPeriod_Start_ID());
				MContractProcPeriod process_Period = MContractProcPeriod.get(getCtx(), JP_ContractProcPeriod_ID);
				if(contractLine_Period.getStartDate().compareTo(process_Period.getStartDate()) <= 0 )
				{
					;//This is OK. contractLine_Period.StartDate <= process_Period.StartDate
				}else{
					
					return false;
					
				}
			}
			
			//End Period
			if(contractLine.getJP_BaseDocLinePolicy().equals(MContractLine.JP_BASEDOCLINEPOLICY_ToEndContractProcessPeriod)
					||contractLine.getJP_BaseDocLinePolicy().equals(MContractLine.JP_BASEDOCLINEPOLICY_FromStartContractProcessPeriodToEnd) )
			{
				MContractProcPeriod contractLine_Period = MContractProcPeriod.get(getCtx(), contractLine.getJP_ProcPeriod_End_ID());
				MContractProcPeriod process_Period = MContractProcPeriod.get(getCtx(), JP_ContractProcPeriod_ID);
				if(contractLine_Period.getEndDate().compareTo(process_Period.getEndDate()) >= 0)
				{
					;//This is OK.  contractLine_Period.EndDate >= process_Period.EndDate
				}else{
				
					return false;
				}
			}
			
			return true;
			
		}//Check Base Doc Line
		
		
		//ignore Base doc line info because carete Derivative Doc
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
					return false;
				}
			}
			
			//Start Period
			if(contractLine.getJP_DerivativeDocPolicy_InOut().equals(MContractLine.JP_DERIVATIVEDOCPOLICY_INOUT_FromStartContractProcessPeriod)
					||contractLine.getJP_DerivativeDocPolicy_InOut().equals(MContractLine.JP_DERIVATIVEDOCPOLICY_INOUT_FromStartContractProcessPeriodToEnd) )
			{				
				MContractProcPeriod contractLine_Period = MContractProcPeriod.get(getCtx(), contractLine.getJP_ProcPeriod_Start_InOut_ID());
				MContractProcPeriod process_Period = MContractProcPeriod.get(getCtx(), JP_ContractProcPeriod_ID);
				if(contractLine_Period.getStartDate().compareTo(process_Period.getStartDate()) <= 0
						|| contractLine_Period.getStartDate().compareTo(process_Period.getEndDate()) <= 0)
				{
					;//This is OK. process_Period.StartDate  >=  contractLine_Period.StartDate <= process_Period.EndDate
				}else{
					
					return false;
					
				}
			}
			
			//End Period
			if(contractLine.getJP_DerivativeDocPolicy_InOut().equals(MContractLine.JP_DERIVATIVEDOCPOLICY_INOUT_ToEndContractProcessPeriod)
					||contractLine.getJP_DerivativeDocPolicy_InOut().equals(MContractLine.JP_DERIVATIVEDOCPOLICY_INOUT_FromStartContractProcessPeriodToEnd) )
			{	
				MContractProcPeriod contractLine_Period = MContractProcPeriod.get(getCtx(), contractLine.getJP_ProcPeriod_End_InOut_ID());
				MContractProcPeriod process_Period = MContractProcPeriod.get(getCtx(), JP_ContractProcPeriod_ID);
				if(contractLine_Period.getEndDate().compareTo(process_Period.getEndDate()) >= 0
						|| contractLine_Period.getEndDate().compareTo(process_Period.getStartDate()) >= 0)
				{
					;//This is OK.  process_Period.StartDate  <=  contractLine_Period.EndDate >= process_Period.EndDate
				}else{

					return false;
					
				}
			}
		}
		
		//Check Derivative Invoice Doc Line
		if(m_ContractContent.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateInvoice) ||
				m_ContractContent.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceiptInvoice) )
		{
			//Lump
			if(contractLine.getJP_DerivativeDocPolicy_Inv().equals(MContractLine.JP_DERIVATIVEDOCPOLICY_INV_LumpOnACertainPointOfContractProcessPeriod))
			{
				MContractProcPeriod lump_ContractProcPeriod = MContractProcPeriod.get(getCtx(),contractLine.getJP_ProcPeriod_Lump_Inv_ID());
				if(!lump_ContractProcPeriod.isContainedBaseDocContractProcPeriod(JP_ContractProcPeriod_ID))
				{
					return false;
				}
			}
			
			//Start Period
			if(contractLine.getJP_DerivativeDocPolicy_Inv().equals(MContractLine.JP_DERIVATIVEDOCPOLICY_INV_FromStartContractProcessPeriod)
					||contractLine.getJP_DerivativeDocPolicy_Inv().equals(MContractLine.JP_DERIVATIVEDOCPOLICY_INV_FromStartContractProcessPeriodToEnd) )
			{				
				MContractProcPeriod contractLine_Period = MContractProcPeriod.get(getCtx(), contractLine.getJP_ProcPeriod_Start_Inv_ID());
				MContractProcPeriod process_Period = MContractProcPeriod.get(getCtx(), JP_ContractProcPeriod_ID);
				if(contractLine_Period.getStartDate().compareTo(process_Period.getStartDate()) <= 0
						|| contractLine_Period.getStartDate().compareTo(process_Period.getEndDate()) <= 0 )
				{
					;//This is OK. process_Period.StartDate  >=  contractLine_Period.StartDate <= process_Period.EndDate
				}else{

					return false;
					
				}
			}
			
			//End Period
			if(contractLine.getJP_DerivativeDocPolicy_Inv().equals(MContractLine.JP_DERIVATIVEDOCPOLICY_INV_ToEndContractProcessPeriod)
					|| contractLine.getJP_DerivativeDocPolicy_Inv().equals(MContractLine.JP_DERIVATIVEDOCPOLICY_INV_FromStartContractProcessPeriodToEnd) )
			{
				MContractProcPeriod contractLine_Period = MContractProcPeriod.get(getCtx(), contractLine.getJP_ProcPeriod_End_Inv_ID());
				MContractProcPeriod process_Period = MContractProcPeriod.get(getCtx(), JP_ContractProcPeriod_ID);
				if(contractLine_Period.getEndDate().compareTo(process_Period.getEndDate()) >= 0
						|| contractLine_Period.getEndDate().compareTo(process_Period.getStartDate()) >= 0 )
				{
					;//This is OK.  process_Period.StartDate  <=  contractLine_Period.EndDate >= process_Period.EndDate
					
				}else{
					
					return false;
					
				}
			}
		}
		
		return true;
	}
	
}
