package jpiere.base.plugin.org.adempiere.process;

import org.compiere.process.DocAction;

import jpiere.base.plugin.org.adempiere.model.MContractLogDetail;

/**
 *
 * JPIERE-0435 : Extend Contract Period and Renew Contract and Contract Status Update
 *
 * @author hhagi
 *
 */
public class DefaultContractProcStatusUpdateProcess extends AbstractContractProcess {


	@Override
	protected void prepare()
	{
		super.prepare();
	}



	@Override
	protected String doIt() throws Exception
	{
		if(m_ContractContent.getJP_ContractProcDate_To() == null)
			return "";

		String JP_ContractProcStatus_Before = m_ContractContent.getJP_ContractProcStatus();
		String JP_ContractProcStatus_After = m_ContractContent.updateContractProcStatus(DocAction.ACTION_None, false);
		if(!JP_ContractProcStatus_Before.equals(JP_ContractProcStatus_After))
		{
			try
			{
				m_ContractContent.saveEx(get_TrxName());
			}catch (Exception e) {
				createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SaveError, null, null, e.getMessage());
			}

			//Create Contract Log
			MContractLogDetail contentLog = new MContractLogDetail(getCtx(), 0, get_TrxName());
			contentLog.setJP_ContractLog_ID(m_ContractLog.getJP_ContractLog_ID());
			contentLog.setJP_ContractLogMsg(MContractLogDetail.JP_CONTRACTLOGMSG_ContractProcessStatusUpdated);
			contentLog.setJP_ContractProcessTraceLevel(MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_ToBeConfirmed);
			contentLog.setJP_Contract_ID(m_Contract.getJP_Contract_ID());
			contentLog.setJP_ContractProcStatus_From(JP_ContractProcStatus_Before);
			contentLog.setJP_ContractProcStatus_To(JP_ContractProcStatus_After);
			contentLog.setJP_ContractProcess_ID(getJP_ContractProcess_ID());
			try {
				contentLog.saveEx(get_TrxName());
			}catch (Exception e) {
				createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SaveError, null, m_ContractLog, e.getMessage());
			}

		}

		return null;
	}

}
