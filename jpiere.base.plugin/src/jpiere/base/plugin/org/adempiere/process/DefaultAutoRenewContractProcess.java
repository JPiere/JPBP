package jpiere.base.plugin.org.adempiere.process;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.compiere.process.ProcessInfo;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.MContract;
import jpiere.base.plugin.org.adempiere.model.MContractCancelTerm;
import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractExtendPeriod;
import jpiere.base.plugin.org.adempiere.model.MContractLogDetail;

/**
 * JPIERE-0435
 *
 *
 * @author hhagi
 *
 */
public class DefaultAutoRenewContractProcess extends AbstractContractProcess {

	private int p_JP_Contract_ID = 0;



	@Override
	protected void prepare()
	{

		p_JP_Contract_ID = getRecord_ID();

	}

	@Override
	protected String doIt() throws Exception
	{
		LocalDateTime now_LocalDateTime = new Timestamp(System.currentTimeMillis()).toLocalDateTime();
		now_LocalDateTime = now_LocalDateTime.minusDays(1);
		Timestamp now_Timestamp = Timestamp.valueOf(now_LocalDateTime);

		MContract contract = new MContract(getCtx(),p_JP_Contract_ID, get_TrxName());

		if(contract.isAutomaticUpdateJP())
		{
			//Auto update Contract
			if(contract.getJP_ContractCancelDate() == null)
			{

				if(contract.getJP_ContractCancelDeadline().compareTo(now_Timestamp) <= 0 )
				{
					autoRenewContract(contract);
				}
			}
		}

		return null;
	}

	private void autoRenewContract(MContract contract)
	{
		LocalDateTime  local_ContractPeriodDate_To = contract.getJP_ContractPeriodDate_To().toLocalDateTime();
		MContractExtendPeriod extendPeriod = MContractExtendPeriod.get(getCtx(), contract.getJP_ContractExtendPeriod_ID());
		local_ContractPeriodDate_To = local_ContractPeriodDate_To.plusYears(extendPeriod.getJP_Year()).plusMonths(extendPeriod.getJP_Month()).plusDays(extendPeriod.getJP_Day());
		contract.setJP_ContractPeriodDate_To(Timestamp.valueOf(local_ContractPeriodDate_To));

		MContractCancelTerm cancelTerm = MContractCancelTerm.get(getCtx(), contract.getJP_ContractCancelTerm_ID());
		LocalDateTime local_ContractCancelDeadline = local_ContractPeriodDate_To.minusYears(cancelTerm.getJP_Year()).minusMonths(cancelTerm.getJP_Month()).minusDays(cancelTerm.getJP_Day());
		contract.setJP_ContractCancelDeadline(Timestamp.valueOf(local_ContractCancelDeadline));

		contract.saveEx(get_TrxName());

		//Create Contract Log
		MContractLogDetail contractlog = new MContractLogDetail(getCtx(), 0, m_ContractLog.get_TrxName());
		contractlog.setJP_ContractLog_ID(m_ContractLog.getJP_ContractLog_ID());
		contractlog.setJP_ContractLogMsg(MContractLogDetail.JP_CONTRACTLOGMSG_AutomaticUpdatedOfTheContract);
		contractlog.setJP_ContractProcessTraceLevel(MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_ToBeConfirmed);
		contractlog.setJP_Contract_ID(contract.getJP_Contract_ID());
		contractlog.saveEx( m_ContractLog.get_TrxName());


		if(contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
		{
			MContractContent[] contractContents = contract.getContractContents(true, null);
			for(int i = 0; i < contractContents.length; i++)
			{
				if(contractContents[i].isAutomaticUpdateJP())
				{

					String className = contractContents[i].getJP_ContractProcess().getJP_ContractAutoRenewClass();

					if(Util.isEmpty(className))
					{
						className = "jpiere.base.plugin.org.adempiere.process.DefaultAutoRenewContractContent";
					}

					ProcessInfo pi = new ProcessInfo("Auto Renew the Contract Content", 0);
					pi.setClassName(className);
					pi.setAD_Client_ID(getAD_Client_ID());
					pi.setAD_User_ID(getAD_User_ID());
					pi.setAD_PInstance_ID(getAD_PInstance_ID());
					pi.setRecord_ID(contractContents[i].getJP_ContractContent_ID());

					ArrayList<ProcessInfoParameter> list = new ArrayList<ProcessInfoParameter>();
					list.add (new ProcessInfoParameter("JP_ContractContent_ID", contractContents[i].getJP_ContractContent_ID(), null, null, null ));
					list.add (new ProcessInfoParameter("m_ContractContent", contractContents[i], null, null, null ));
					list.add (new ProcessInfoParameter("m_Contract", contract, null, null, null ));
					list.add (new ProcessInfoParameter("JP_ContractLog", m_ContractLog, null, null, null ));

				}

			}//for
		}

	}

}
