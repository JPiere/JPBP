package jpiere.base.plugin.org.adempiere.process;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfoParameter;

import jpiere.base.plugin.org.adempiere.model.MContract;
import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractLogDetail;

public class DefaultContractProcStatusUpdateProcess extends AbstractContractProcess {

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
		LocalDateTime now_LocalDateTime = new Timestamp(System.currentTimeMillis()).toLocalDateTime();
		Timestamp now_Timestamp = Timestamp.valueOf(now_LocalDateTime);
		LocalDateTime now_LocalDateTime_pre1 = now_LocalDateTime.minusDays(1);
		Timestamp now_Timestamp_pre1 = Timestamp.valueOf(now_LocalDateTime_pre1);

		if(m_ContractContent.getJP_ContractProcDate_To() == null)
			return "";

		if(m_ContractContent.getJP_ContractProcDate_To() != null && m_ContractContent.getJP_ContractProcDate_To().compareTo(now_Timestamp_pre1) <= 0 )
		{
			String JP_ConstractProcStatus_From = m_ContractContent.getJP_ContractProcStatus();
			if(JP_ConstractProcStatus_From.equals(MContractContent.JP_CONTRACTPROCSTATUS_Unprocessed)
					&& m_ContractContent.getDocAction().equals(DocAction.STATUS_Completed))
			{

				m_ContractContent.setJP_ContractProcStatus(MContractContent.JP_CONTRACTPROCSTATUS_Processed);
				m_ContractContent.saveEx(get_TrxName());

				//Create Contract Log
				MContractLogDetail contentLog = new MContractLogDetail(getCtx(), 0, m_ContractLog.get_TrxName());
				contentLog.setJP_ContractLog_ID(m_ContractLog.getJP_ContractLog_ID());
				contentLog.setJP_ContractLogMsg(MContractLogDetail.JP_CONTRACTLOGMSG_ContractProcessStatusUpdated);
				contentLog.setJP_ContractProcessTraceLevel(MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_ToBeConfirmed);
				contentLog.setJP_Contract_ID(m_ContractContent.getJP_Contract_ID());
				contentLog.setJP_ContractContent_ID(m_ContractContent.getJP_ContractContent_ID());
				contentLog.setJP_ContractProcStatus_From(JP_ConstractProcStatus_From);
				contentLog.setJP_ContractProcStatus_To(MContractContent.JP_CONTRACTPROCSTATUS_Processed);
				contentLog.saveEx( m_ContractLog.get_TrxName());
			}

		}else{

			;//Nothing to do

		}

		return null;
	}

}
