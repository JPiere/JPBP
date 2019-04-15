package jpiere.base.plugin.org.adempiere.process;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.adempiere.util.ProcessUtil;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfo;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.util.Env;
import org.compiere.util.Trx;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.MContract;
import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractLogDetail;
import jpiere.base.plugin.org.adempiere.model.MContractProcPeriod;

/**
 *
 * JPIERE-0435 : Extend Contract Period and Renew Contract and Contract Status Update
 *
 * @author hhagi
 *
 */
public class DefaultContractStatusUpdateProcess extends AbstractContractProcess {


	@Override
	protected void prepare()
	{
		super.prepare();
	}

	@Override
	protected String doIt() throws Exception
	{

		String JP_ContractStatus_Before = m_Contract.getJP_ContractStatus();
		String JP_ContractStatus_After = m_Contract.updateContractStatus(DocAction.ACTION_None);

		if(!JP_ContractStatus_Before.equals(JP_ContractStatus_After))
		{
			try
			{
				m_Contract.saveEx(get_TrxName());
			}catch (Exception e) {
				createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SaveError, null, null, e.getMessage());
			}

			//Create Contract Log
			MContractLogDetail contentLog = new MContractLogDetail(getCtx(), 0, get_TrxName());
			contentLog.setJP_ContractLog_ID(m_ContractLog.getJP_ContractLog_ID());
			contentLog.setJP_ContractLogMsg(MContractLogDetail.JP_CONTRACTLOGMSG_ContractStatusUpdated);
			contentLog.setJP_ContractProcessTraceLevel(MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_ToBeConfirmed);
			m_ContractLog.confirmNum++;
			contentLog.setJP_Contract_ID(m_Contract.getJP_Contract_ID());
			contentLog.setJP_ContractStatus_From(JP_ContractStatus_Before);
			contentLog.setJP_ContractStatus_To(JP_ContractStatus_After);
			try {
				contentLog.saveEx(get_TrxName());
			}catch (Exception e) {
				createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SaveError, null, m_ContractLog, e.getMessage());
			}

		}


		if(m_Contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
		{
			MContractContent[] contractContents = m_Contract.getContractContents(true, null);
			for(int i = 0; i < contractContents.length; i++)
			{
				if(contractContents[i].getJP_ContractProcDate_To() == null)
					continue;

				Timestamp now = new Timestamp(System.currentTimeMillis());
				Timestamp yesterday = Timestamp.valueOf(now.toLocalDateTime().minusDays(1));
				if(yesterday.compareTo(contractContents[i].getJP_ContractProcDate_To()) > 0)
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
					list.add (new ProcessInfoParameter("JP_ContractContent", contractContents[i], null, null, null ));
					list.add (new ProcessInfoParameter("JP_Contract", m_Contract, null, null, null ));
					list.add (new ProcessInfoParameter("JP_ContractLog", m_ContractLog, null, null, null ));
					setProcessInfoParameter(pi, list, null);

					if(processUI == null)
					{
						processUI = Env.getProcessUI(getCtx());

					}

					boolean success = ProcessUtil.startJavaProcess(getCtx(), pi, Trx.get(get_TrxName(), true), false, processUI);
					if(success)
					{
						;

					}else{

						;
					};

				}//if

			}//for
		}

		return null;
	}

	private void setProcessInfoParameter(ProcessInfo pi, ArrayList<ProcessInfoParameter> list ,MContractProcPeriod procPeriod) throws Exception
	{
		ProcessInfoParameter[] para = getParameter();
		for(int i = 0; i < para.length; i++)
		{
			//Modify by Calender of Process Period.
			if(para[i].getParameterName ().equals(MContractProcPeriod.COLUMNNAME_JP_ContractCalender_ID))
			{
				if(procPeriod == null)
				{
					list.add (new ProcessInfoParameter("JP_ContractCalender_ID", para[i].getParameter(), para[i].getParameter_To(), para[i].getInfo(), para[i].getInfo_To() ));
				}else{
					list.add (new ProcessInfoParameter("JP_ContractCalender_ID", procPeriod.getJP_ContractCalender_ID(), null, para[i].getInfo(), para[i].getInfo_To() ));
				}

			//Modify by Process Period.
			}else if (para[i].getParameterName ().equals(MContractProcPeriod.COLUMNNAME_JP_ContractProcPeriod_ID)){

				if(procPeriod == null)
				{
					list.add (new ProcessInfoParameter("JP_ContractProcPeriod_ID", para[i].getParameter(), para[i].getParameter_To(), para[i].getInfo(), para[i].getInfo_To() ));
				}else{
					list.add (new ProcessInfoParameter("JP_ContractProcPeriod_ID", procPeriod.getJP_ContractProcPeriod_ID(), null, para[i].getInfo(), para[i].getInfo_To() ));
				}

			}else{
				list.add (new ProcessInfoParameter(para[i].getParameterName (), para[i].getParameter(), para[i].getParameter_To(), para[i].getInfo(), para[i].getInfo_To()));
			}
		}

		ProcessInfoParameter[] pars = new ProcessInfoParameter[list.size()];
		list.toArray(pars);
		pi.setParameter(pars);
	}
}
