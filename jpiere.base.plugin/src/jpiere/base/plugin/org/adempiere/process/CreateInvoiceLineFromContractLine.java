package jpiere.base.plugin.org.adempiere.process;

import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.PO;
import org.compiere.process.SvrProcess;
import org.compiere.util.Msg;

import jpiere.base.plugin.org.adempiere.model.MContract;
import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractLine;
import jpiere.base.plugin.org.adempiere.model.MContractProcPeriod;

public class CreateInvoiceLineFromContractLine extends SvrProcess {
	
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
		
		
		MInvoice invoice = new MInvoice(getCtx(), record_ID, get_TrxName());
		MInvoiceLine[] iLines = invoice.getLines();
		if(iLines.length > 0)
			throw new Exception(Msg.getMsg(getCtx(), "JP_DocLineCreated"));
		
		
		int JP_ContractContent_ID = invoice.get_ValueAsInt("JP_ContractContent_ID");
		if(JP_ContractContent_ID <= 0)
			throw new Exception(Msg.getMsg(getCtx(), "NotFound") + " : " + Msg.getElement(getCtx(), "JP_ContractContent_ID"));

		
		m_ContractContent = MContractContent.get(getCtx(), JP_ContractContent_ID);
		int JP_ContractProcPeriod_ID = invoice.get_ValueAsInt("JP_ContractProcPeriod_ID");
		MContractLine[] m_lines = m_ContractContent.getLines();
		for(int i = 0; i < m_lines.length; i++)
		{
			if(JP_ContractProcPeriod_ID > 0)
			{
				if(!isCreateDocLine(m_lines[i], JP_ContractProcPeriod_ID, true))
				continue;
			}
			
			MInvoiceLine iline = new MInvoiceLine(getCtx(), 0, get_TrxName());
			PO.copyValues(m_lines[i], iline);
			iline.setProcessed(false);
			iline.setC_Invoice_ID(invoice.getC_Invoice_ID());
			iline.setAD_Org_ID(invoice.getAD_Org_ID());
			iline.setAD_OrgTrx_ID(invoice.getAD_OrgTrx_ID());
			iline.setQtyInvoiced(m_lines[i].getQtyOrdered());//QtyInvoiced = QtyOrdered because Base Doc
			iline.set_ValueNoCheck("JP_ContractLine_ID", m_lines[i].getJP_ContractLine_ID());
			if(m_ContractContent.getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
				iline.set_ValueOfColumn("JP_ContractProcPeriod_ID", JP_ContractProcPeriod_ID);

			iline.saveEx(get_TrxName());	
		}//for
		
		
		return Msg.getMsg(getCtx(),"Success");
	}
	
	private boolean isCreateDocLine(MContractLine contractLine, int JP_ContractProcPeriod_ID, boolean isCreateLog)
	{
		if(!contractLine.isCreateDocLineJP())
		{
			return false;
		}
		
		if(!m_ContractContent.getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
			return true;
	
		//Check Overlap
//		MInvoiceLine[] iLines = contractLine.getInvoiceLineByContractPeriod(getCtx(), JP_ContractProcPeriod_ID, get_TrxName());
//		if(iLines != null && iLines.length > 0)
//		{
//			return false;
//		}
		
		//Check Base Doc Line
		if(contractLine.getJP_BaseDocLinePolicy() != null)
		{
			//Lump
			if(contractLine.getJP_BaseDocLinePolicy().equals(MContractLine.JP_BASEDOCLINEPOLICY_LumpOnACertainPointOfContractProcessPeriod))
			{
				MContractProcPeriod lump_ContractProcPeriod = MContractProcPeriod.get(getCtx(), contractLine.getJP_ProcPeriod_Lump_ID());
				if(!lump_ContractProcPeriod.isContainedBaseDocContractProcPeriod(JP_ContractProcPeriod_ID))
				{					
					return false;
				}
			}

			//Start Period
			if(contractLine.getJP_BaseDocLinePolicy().equals(MContractLine.JP_BASEDOCLINEPOLICY_FromStartContractProcessPeriod)
					||contractLine.getJP_BaseDocLinePolicy().equals(MContractLine.JP_BASEDOCLINEPOLICY_FromStartContractProcessPeriodToEnd) )
			{
				MContractProcPeriod contractLine_Period = MContractProcPeriod.get(getCtx(), contractLine.getJP_ProcPeriod_Start_ID());
				MContractProcPeriod process_Period = MContractProcPeriod.get(getCtx(), JP_ContractProcPeriod_ID);
				if(contractLine_Period.getStartDate().compareTo(process_Period.getStartDate()) <= 0)
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
				MContractProcPeriod process_ContractProcPeriod = MContractProcPeriod.get(getCtx(), JP_ContractProcPeriod_ID);
				if(contractLine_Period.getEndDate().compareTo(process_ContractProcPeriod.getEndDate()) >= 0)
				{
					;//This is OK. contractLine_Period.EndDate >= process_Period.EndDate
				}else{
					
					return false;
				}
			}
			
			return true;
		}
		
		return true;
	}
	
}
