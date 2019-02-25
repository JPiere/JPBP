package jpiere.base.plugin.org.adempiere.process;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import jpiere.base.plugin.org.adempiere.model.MContract;

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
		now_LocalDateTime = now_LocalDateTime.minusDays(1);
		Timestamp now_Timestamp = Timestamp.valueOf(now_LocalDateTime);

		MContract contract = new MContract(getCtx(),p_JP_Contract_ID, get_TrxName());

		//Check from Prepare to Under Contract
		if(contract.getJP_ContractStatus().equals(MContract.JP_CONTRACTSTATUS_Prepare))
		{
			if(contract.getJP_ContractPeriodDate_From().compareTo(now_Timestamp) >= 0)
			{
				contract.setJP_ContractStatus(MContract.JP_CONTRACTSTATUS_UnderContract);
				contract.saveEx(get_TrxName());
			}
		}

		//Check an indefinite period Contract
		if(contract.getJP_ContractPeriodDate_To() == null)
			return null;


		return null;
	}
}
