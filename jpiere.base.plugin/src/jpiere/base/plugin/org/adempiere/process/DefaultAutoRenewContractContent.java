package jpiere.base.plugin.org.adempiere.process;

import org.compiere.util.Msg;

import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractLogDetail;

/**
 *
 * JPIERE-0435 : Extend Contract Period and Renew Contract and Contract Status Update
 *
 * @author hhagi
 *
 */
public class DefaultAutoRenewContractContent extends DefaultCreateContractByCopy {

	@Override
	protected void prepare()
	{
		super.prepare();
	}

	@Override
	protected String doIt() throws Exception
	{

		if(m_ContractContent.getJP_ContractProcessMethod().equals(MContractContent.JP_CONTRACTPROCESSMETHOD_DirectContractProcess))
		{
			if(m_ContractContent.getJP_ContractC_AutoUpdatePolicy().equals(MContractContent.JP_CONTRACTC_AUTOUPDATEPOLICY_RenewTheContractContent) && !m_ContractContent.isRenewedContractContentJP())
			{
				try
				{
					renewTheContractContent();
				}catch (Exception e) {
					createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_UnexpectedError, null, null, e.getMessage());
				}

			}else if(m_ContractContent.getJP_ContractC_AutoUpdatePolicy().equals(MContractContent.JP_CONTRACTC_AUTOUPDATEPOLICY_ExtendContractProcessDate)) {

				try
				{
					extendContractProcessDate();
				}catch (Exception e) {
					createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_UnexpectedError, null, null, e.getMessage());
				}
			}

		}else if(m_ContractContent.getJP_ContractProcessMethod().equals(MContractContent.JP_CONTRACTPROCESSMETHOD_IndirectContractProcess) && !m_ContractContent.isRenewedContractContentJP()) {

			try
			{
				renewTheContractContent();
			}catch (Exception e) {
				createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_UnexpectedError, null, null, e.getMessage());
			}

		}


		return null;
	}

	private void extendContractProcessDate() throws Exception
	{
		if(m_Contract.getJP_ContractPeriodDate_To().compareTo(m_ContractContent.getJP_ContractProcDate_To()) == 0)
		{

			;//Noting to do;

		}else {

			m_ContractContent.setJP_ContractProcDate_To(m_Contract.getJP_ContractPeriodDate_To());
			try
			{
				m_ContractContent.saveEx(get_TrxName());
			}catch (Exception e) {
				createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SaveError, null, null, e.getMessage());
			}

			createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_ExtendContractProcessDateOfContractContent, null, to_ContractContent, Msg.getMsg(getCtx(), "JP_Success"));

		}



	}

	private void renewTheContractContent() throws Exception
	{
		MContractContent to_ContractContent = new MContractContent(getCtx(), 0, get_TrxName());
		try {
			createContractContent(m_ContractContent, to_ContractContent, true);
		}catch (Exception e) {
			createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_UnexpectedError, null, to_ContractContent, e.getMessage());
		}


		m_ContractContent.setIsRenewedContractContentJP(true);
		try
		{
			m_ContractContent.saveEx(get_TrxName());
		}catch (Exception e) {
			createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SaveError, null, to_ContractContent, e.getMessage());
		}

		createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_RenewTheContractContent, null, to_ContractContent, Msg.getMsg(getCtx(), "JP_Success"));
	}

}
