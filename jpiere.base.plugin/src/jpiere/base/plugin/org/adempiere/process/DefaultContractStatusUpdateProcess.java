package jpiere.base.plugin.org.adempiere.process;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfo;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.MContract;
import jpiere.base.plugin.org.adempiere.model.MContractContent;

/**
 *
 * JPIERE-0435
 *
 * @author hhagi
 *
 */
public class DefaultContractStatusUpdateProcess extends AbstractContractProcess {


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
		Timestamp now_Timestamp = Timestamp.valueOf(now_LocalDateTime);
		LocalDateTime now_LocalDateTime_pre1 = now_LocalDateTime.minusDays(1);
		Timestamp now_Timestamp_pre1 = Timestamp.valueOf(now_LocalDateTime_pre1);

		MContract contract = new MContract(getCtx(),p_JP_Contract_ID, get_TrxName());

		//Check from Prepare to Under Contract
		if(contract.getJP_ContractStatus().equals(MContract.JP_CONTRACTSTATUS_Prepare)
				&& contract.getDocStatus().equals(DocAction.STATUS_Completed))
		{
			if(contract.getJP_ContractPeriodDate_From().compareTo(now_Timestamp) <= 0)
			{
				contract.setJP_ContractStatus(MContract.JP_CONTRACTSTATUS_UnderContract);
				contract.saveEx(get_TrxName());
			}
		}

		//Check an indefinite period Contract
		if(contract.getJP_ContractPeriodDate_To() != null
				&& contract.getJP_ContractStatus().equals(MContract.JP_CONTRACTSTATUS_UnderContract)
				&& contract.getDocStatus().equals(DocAction.STATUS_Completed) )
		{
			if(contract.getJP_ContractPeriodDate_To().compareTo(now_Timestamp_pre1) <= 0)
			{
				contract.setJP_ContractStatus(MContract.JP_CONTRACTSTATUS_ExpirationOfContract);
				contract.saveEx(get_TrxName());
			}
		}

		if(contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
		{
			MContractContent[] contractContents = contract.getContractContents(true, null);
			for(int i = 0; i < contractContents.length; i++)
			{
				if(contractContents[i].isAutomaticUpdateJP())
				{

					String className = contractContents[i].getJP_ContractProcess().getJP_ContractStatusUpdateClass();

					if(Util.isEmpty(className))
					{
						className = "jpiere.base.plugin.org.adempiere.process.DefaultContractProcStatusUpdateProcess";
					}

					ProcessInfo pi = new ProcessInfo("Contract Process Status Update", 0);
					pi.setClassName(className);
					pi.setAD_Client_ID(getAD_Client_ID());
					pi.setAD_User_ID(getAD_User_ID());
					pi.setAD_PInstance_ID(getAD_PInstance_ID());
					pi.setRecord_ID(contractContents[i].getJP_ContractContent_ID());

					ArrayList<ProcessInfoParameter> list = new ArrayList<ProcessInfoParameter>();
					list.add (new ProcessInfoParameter("JP_ContractContent_ID", contractContents[i].getJP_ContractContent_ID(), null, null, null ));
					list.add (new ProcessInfoParameter("m_ContractContent", contractContents[i], null, null, null ));
					list.add (new ProcessInfoParameter("JP_ContractLog", m_ContractLog, null, null, null ));

				}

			}//for
		}

		return null;
	}
}
