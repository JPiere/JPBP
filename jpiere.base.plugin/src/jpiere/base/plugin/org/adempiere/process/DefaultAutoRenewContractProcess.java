package jpiere.base.plugin.org.adempiere.process;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.adempiere.exceptions.AdempiereException;
import org.adempiere.util.ProcessUtil;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfo;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Trx;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.MContract;
import jpiere.base.plugin.org.adempiere.model.MContractCancelTerm;
import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractExtendPeriod;
import jpiere.base.plugin.org.adempiere.model.MContractLogDetail;
import jpiere.base.plugin.org.adempiere.model.MContractProcPeriod;

/**
 *
 * JPIERE-0435 : Extend Contract Period and Renew Contract and Contract Status Update
 *
 *
 * @author hhagi
 *
 */
public class DefaultAutoRenewContractProcess extends AbstractContractProcess {


	@Override
	protected void prepare()
	{
		super.prepare();
	}


	@Override
	protected String doIt() throws Exception
	{
		LocalDateTime now_LocalDateTime = new Timestamp(System.currentTimeMillis()).toLocalDateTime();
		now_LocalDateTime = now_LocalDateTime.minusDays(1);
		Timestamp now_Timestamp = Timestamp.valueOf(now_LocalDateTime);

		if(m_Contract==null)
			return "";

		if(m_Contract.isAutomaticUpdateJP())
		{
			//Auto update Contract
			if(m_Contract.getJP_ContractCancelDate() == null)
			{

				if(m_Contract.getJP_ContractCancelDeadline().compareTo(now_Timestamp) <= 0 )
				{
					autoRenewContract(m_Contract);

					if(m_Contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
					{
						MContractContent[] contractContents = m_Contract.getContractContents(true, null);
						for(int i = 0; i < contractContents.length; i++)
						{
							if(contractContents[i].isAutomaticUpdateJP())
							{

								if(contractContents[i].getJP_ContractProcStatus().equals(MContractContent.JP_CONTRACTPROCSTATUS_Invalid)
										|| contractContents[i].getJP_ContractProcStatus().equals(MContractContent.JP_CONTRACTPROCSTATUS___) )
								{
									continue;
								}

								if(contractContents[i].getDocStatus().equals(DocAction.STATUS_Closed)
										|| contractContents[i].getDocStatus().equals(DocAction.STATUS_Reversed)
										|| contractContents[i].getDocStatus().equals(DocAction.STATUS_Voided))
								{
									continue;
								}

								if(contractContents[i].isRenewedContractContentJP())
								{
									continue;
								}


								if(m_Contract.getJP_ContractPeriodDate_To().compareTo(contractContents[i].getJP_ContractProcDate_To()) == 0)
								{
									continue;

								}else if(m_Contract.getJP_ContractPeriodDate_To().compareTo(contractContents[i].getJP_ContractProcDate_To()) < 0) {

									//Outside the specified period
									createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_UnexpectedError, null, null,Msg.getMsg(getCtx(),"JP_OutsidePperiod"));
								}

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
								pi.setRecord_ID(0);

								ArrayList<ProcessInfoParameter> list = new ArrayList<ProcessInfoParameter>();
								list.add (new ProcessInfoParameter("JP_ContractContent_ID", contractContents[i].getJP_ContractContent_ID(), null, null, null ));
								list.add (new ProcessInfoParameter("JP_ContractContent", contractContents[i], null, null, null ));
								list.add (new ProcessInfoParameter("JP_Contract", m_Contract, null, null, null ));
								list.add (new ProcessInfoParameter("JP_ContractLog", m_ContractLog, null, null, null ));
								list.add(new ProcessInfoParameter("JP_ContractTabLevel", AbstractCreateContractFromTemplate.JP_ContractTabLevel_Content, null, null, null ));
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
								}

							}

						}//for
					}
				}

			}
		}

		return null;
	}

	private void autoRenewContract(MContract contract) throws Exception
	{
		LocalDateTime  local_ContractPeriodDate_To = contract.getJP_ContractPeriodDate_To().toLocalDateTime();
		MContractExtendPeriod extendPeriod = MContractExtendPeriod.get(getCtx(), contract.getJP_ContractExtendPeriod_ID());
		local_ContractPeriodDate_To = local_ContractPeriodDate_To.plusYears(extendPeriod.getJP_Year()).plusMonths(extendPeriod.getJP_Month()).plusDays(extendPeriod.getJP_Day());
		contract.setJP_ContractPeriodDate_To(Timestamp.valueOf(local_ContractPeriodDate_To));

		MContractCancelTerm cancelTerm = MContractCancelTerm.get(getCtx(), contract.getJP_ContractCancelTerm_ID());
		LocalDateTime local_ContractCancelDeadline = local_ContractPeriodDate_To.minusYears(cancelTerm.getJP_Year()).minusMonths(cancelTerm.getJP_Month()).minusDays(cancelTerm.getJP_Day());
		contract.setJP_ContractCancelDeadline(Timestamp.valueOf(local_ContractCancelDeadline));

		try{
			contract.saveEx(get_TrxName());
		} catch (AdempiereException e) {
			createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SaveError, null, contract, e.getMessage());
			throw e;
		}finally {
			;
		}

		createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_AutomaticUpdatedOfTheContract, null, contract
						, Msg.getMsg(getCtx(), "JP_Success"), MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_ToBeConfirmed);

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
