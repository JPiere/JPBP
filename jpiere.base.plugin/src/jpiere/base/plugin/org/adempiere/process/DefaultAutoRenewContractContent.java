package jpiere.base.plugin.org.adempiere.process;

import org.compiere.process.ProcessInfoParameter;
import org.compiere.util.Msg;

import jpiere.base.plugin.org.adempiere.model.MContract;
import jpiere.base.plugin.org.adempiere.model.MContractContent;

public class DefaultAutoRenewContractContent extends DefaultCreateContractByCopy {

	private MContractContent m_ContractContent = null;
	private MContract m_Contract = null;

	@Override
	protected void prepare()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();

			if (para[i].getParameter() == null)
			{
				;

			}else if (name.equals("m_ContractContent")){

				m_ContractContent = (MContractContent)para[i].getParameter();

			}else if (name.equals("m_Contract")){

				m_Contract = (MContract)para[i].getParameter();

			}else{
//				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}//if
		}

	}

	@Override
	protected String doIt() throws Exception
	{

		if(m_ContractContent.getJP_ContractProcessMethod().equals(MContractContent.JP_CONTRACTPROCESSMETHOD_DirectContractProcess))
		{
			if(m_ContractContent.getJP_ContractC_AutoUpdatePolicy().equals(MContractContent.JP_CONTRACTC_AUTOUPDATEPOLICY_RenewTheContractContent) && !m_ContractContent.isRenewedContractContentJP())
			{
				renewTheContractContent();

			}else if(m_ContractContent.getJP_ContractC_AutoUpdatePolicy().equals(MContractContent.JP_CONTRACTC_AUTOUPDATEPOLICY_ExtendContractProcessDate)) {

				extendContractProcessDate();
			}

		}else if(m_ContractContent.getJP_ContractProcessMethod().equals(MContractContent.JP_CONTRACTPROCESSMETHOD_IndirectContractProcess) && !m_ContractContent.isRenewedContractContentJP()) {

			renewTheContractContent();

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
				throw new Exception(Msg.getMsg(getCtx(), "SaveError") + " " + Msg.getElement(getCtx(), "JP_Contract_ID") + "_" + m_ContractContent.getDocumentNo() + " >>> " + e.getMessage() );
			}
		}


	}

	private void renewTheContractContent() throws Exception
	{
		MContractContent to_ContractContent = new MContractContent(getCtx(), 0, get_TrxName());
		createContractContent(m_ContractContent, to_ContractContent, true);

		m_ContractContent.setIsRenewedContractContentJP(true);
		try
		{
			m_ContractContent.saveEx(get_TrxName());
		}catch (Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "SaveError") + " " + Msg.getElement(getCtx(), "JP_Contract_ID") + "_" + m_ContractContent.getDocumentNo() + " >>> " + e.getMessage() );
		}
	}

}
