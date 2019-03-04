/******************************************************************************
 * Product: JPiere                                                            *
 * Copyright (C) Hideaki Hagiwara (h.hagiwara@oss-erp.co.jp)                  *
 *                                                                            *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY.                          *
 * See the GNU General Public License for more details.                       *
 *                                                                            *
 * JPiere is maintained by OSS ERP Solutions Co., Ltd.                        *
 * (http://www.oss-erp.co.jp)                                                 *
 *****************************************************************************/

package jpiere.base.plugin.org.adempiere.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.adempiere.util.IProcessUI;
import org.adempiere.util.ProcessUtil;
import org.compiere.model.MPInstance;
import org.compiere.model.MSession;
import org.compiere.model.Query;
import org.compiere.process.ProcessInfo;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Trx;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.MContract;
import jpiere.base.plugin.org.adempiere.model.MContractCalender;
import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractLog;
import jpiere.base.plugin.org.adempiere.model.MContractLogDetail;
import jpiere.base.plugin.org.adempiere.model.MContractProcPeriod;
import jpiere.base.plugin.org.adempiere.model.MContractProcess;


/**
*  JPIERE-0363
*
* @author Hideaki Hagiwara
*
*/
public class CallContractProcess extends SvrProcess {

	private String p_JP_ContractProcessUnit = null;
//	private int p_JP_ContractCalender_ID = 0;
	private int p_JP_ContractProcPeriodG_ID = 0;
	private int p_JP_ContractProcPeriod_ID = 0;
	private String p_JP_ContractProcessValue = null;
	private Timestamp p_DateAcct = null;
	private Timestamp p_DateDoc = null;
//	private String p_DocAction = null;
	private int p_AD_Org_ID = 0;
	private int p_JP_ContractCategoryL2_ID = 0;
	private int p_JP_ContractCategoryL1_ID = 0;
	private int p_JP_ContractCategory_ID = 0;
	private int p_C_DocType_ID = 0;
	private String p_DocBaseType = null;
	private boolean p_IsCreateBaseDocJP = false;
	private boolean p_IsRecordCommitJP = false;
	private String p_JP_ContractProcessTraceLevel = "WAR";
	private String p_JP_IndirectContractProcType = AbstractContractProcess.JP_IndirectContractProcType_AllValidContractProcessSchedule;

	private int p_JP_Contract_ID = 0;
	private int p_JP_ContractContent_ID = 0;
	private String p_JP_ContractProcessMethod = null;

	private String p_JP_ContractProcessType = AbstractContractProcess.JP_ContractProcessType_CreateDocument;
	private String p_JP_ContractAutoRenewClass = null;
	private String p_JP_ContractStatusUpdateClass = null;

	//Contract Log
	private Trx contractLogTrx = null;
	private MContractLog m_ContractLog = null;

	private int successNum = 0;
	private int failureNum = 0;
	private int processContractContentNum = 0;
	private int processContractLineNum = 0;



	@Override
	protected void prepare()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();

			if (para[i].getParameter() == null){
				;
			}else if (name.equals("JP_ContractProcessUnit")){
				p_JP_ContractProcessUnit = para[i].getParameterAsString();
//			}else if (name.equals("JP_ContractCalender_ID")){
//				p_JP_ContractCalender_ID = para[i].getParameterAsInt();
			}else if (name.equals("JP_ContractProcPeriodG_ID")){
				p_JP_ContractProcPeriodG_ID = para[i].getParameterAsInt();
			}else if (name.equals("JP_ContractProcPeriod_ID")){
				p_JP_ContractProcPeriod_ID = para[i].getParameterAsInt();
			}else if (name.equals("JP_ContractProcessValue")){
				p_JP_ContractProcessValue = para[i].getParameterAsString();
			}else if (name.equals("DateAcct")){
				p_DateAcct = para[i].getParameterAsTimestamp();
			}else if (name.equals("DateDoc")){
				p_DateDoc = para[i].getParameterAsTimestamp();
//			}else if (name.equals("DocAction")){
//				p_DocAction = para[i].getParameterAsString();
			}else if (name.equals("AD_Org_ID")){
				p_AD_Org_ID = para[i].getParameterAsInt();
			}else if (name.equals("JP_ContractCategoryL2_ID")){
				p_JP_ContractCategoryL2_ID = para[i].getParameterAsInt();
			}else if (name.equals("JP_ContractCategoryL1_ID")){
				p_JP_ContractCategoryL1_ID = para[i].getParameterAsInt();
			}else if (name.equals("JP_ContractCategory_ID")){
				p_JP_ContractCategory_ID = para[i].getParameterAsInt();
			}else if (name.equals("C_DocType_ID")){
				p_C_DocType_ID = para[i].getParameterAsInt();
			}else if (name.equals("DocBaseType")){
				p_DocBaseType = para[i].getParameterAsString();
			}else if (name.equals("IsCreateBaseDocJP")){
				p_IsCreateBaseDocJP = para[i].getParameterAsBoolean();
			}else if (name.equals("IsRecordCommitJP")){
				p_IsRecordCommitJP = para[i].getParameterAsBoolean();
			}else if (name.equals("JP_ContractProcessTraceLevel")){
				p_JP_ContractProcessTraceLevel = para[i].getParameterAsString();
			}else if (name.equals("JP_Contract_ID")){
				p_JP_Contract_ID = para[i].getParameterAsInt();
			}else if (name.equals("JP_ContractContent_ID")){
				p_JP_ContractContent_ID = para[i].getParameterAsInt();
			}else if (name.equals("JP_ContractProcessMethod")){
				p_JP_ContractProcessMethod = para[i].getParameterAsString();
			} else if (name.equals("JP_IndirectContractProcType")) {
				p_JP_IndirectContractProcType = para[i].getParameterAsString();
			} else if(name.equals("JP_ContractProcessType")) {
				p_JP_ContractProcessType = para[i].getParameterAsString();
			} else if(name.equals("JP_ContractAutoRenewClass")) {
				p_JP_ContractAutoRenewClass  = para[i].getParameterAsString();
			}else if (name.equals("JP_ContractStatusUpdateClass")) {
				p_JP_ContractStatusUpdateClass = para[i].getParameterAsString();
			}else{
//				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}//if

		}//fo

	}

	@Override
	protected String doIt() throws Exception
	{

		String msg = "";
		try
		{
			if(isProcessRunning())
			{
				msg = "@Error@" +" : " + Msg.getMsg(getCtx(), "JP_ContractProcessRunningNow");
				throw new Exception(Msg.getMsg(getCtx(), "JP_ContractProcessRunningNow"));//Contract process is running now by other user.
			}

			//Create Contract Management Log
			if(!p_JP_ContractProcessTraceLevel.equals(MContractLog.JP_CONTRACTPROCESSTRACELEVEL_NoLog))
			{
				String trxName = Trx.createTrxName("Contract");
				contractLogTrx = Trx.get(trxName, false);
				m_ContractLog = new MContractLog(getCtx(), 0, contractLogTrx.getTrxName());
				m_ContractLog.setJP_ContractProcessTraceLevel(p_JP_ContractProcessTraceLevel);
				m_ContractLog.setAD_PInstance_ID(getAD_PInstance_ID());
				m_ContractLog.setJP_ContractProcessUnit(p_JP_ContractProcessUnit);
				m_ContractLog.saveEx(contractLogTrx.getTrxName());
				int JP_ContractLog_ID = m_ContractLog.getJP_ContractLog_ID();
				addBufferLog(0, null, null, Msg.getMsg(getCtx(), "JP_DetailLog")+" -> " + Msg.getElement(getCtx(), "JP_ContractLog_ID"), MContractLog.Table_ID, JP_ContractLog_ID);
				contractLogTrx.commit();
			}

			msg = doContractProcess();

		} catch (Exception e) {

			if(contractLogTrx != null)
			{
				if(p_IsRecordCommitJP)
					msg = "-- Stop Process for Error --  ";
				else
					msg = "-- ALL Rollback--  " + " Error : " +  e.getMessage( ) ;

				m_ContractLog.setDescription( msg + " Error : " +  e.getMessage( ));
				m_ContractLog.saveEx(contractLogTrx.getTrxName());
				contractLogTrx.commit();
			}

			if(p_IsRecordCommitJP)
			{
				;
			}else{
				rollback();
			}

//			throw e;

		} finally {

			if(contractLogTrx != null)
			{
				contractLogTrx.close();
				contractLogTrx = null;
			}

		}

		return  msg;
	}


	private String doContractProcess() throws Exception
	{

		if(p_JP_ContractProcessUnit == null)
		{
			if(p_JP_ContractProcessType.equals(AbstractContractProcess.JP_ContractProcessType_CreateDocument)
					|| p_JP_ContractProcessType.equals(AbstractContractProcess.JP_ContractProcessType_Report))
			{
				String msg = Msg.getMsg(getCtx(), "FillMandatory") + Msg.getElement(getCtx(), "JP_ContractProcessUnit");
				if(contractLogTrx !=null)
				{
					m_ContractLog.setDescription(msg);
					m_ContractLog.saveEx(contractLogTrx.getTrxName());
					contractLogTrx.commit();
				}

				throw new Exception(msg);
			}

		}


		//ContractProcessType
		if(p_JP_ContractProcessType.equals(AbstractContractProcess.JP_ContractProcessType_CreateDocument))
		{
			//Process is kicked from the window by Per Contract Content.
			if(p_JP_ContractProcessUnit.equals(AbstractContractProcess.JP_ContractProcessUnit_PerContractContent))
			{
				int Record_ID = getRecord_ID();
				processContractContentNum = 1;
				if(Record_ID > 0)
				{
					MContractContent contractContent = new MContractContent(getCtx(),Record_ID, get_TrxName());
					processContractLineNum = processContractLineNum + contractContent.getLines().length;

					if(p_JP_ContractProcessMethod.equals(MContractContent.JP_CONTRACTPROCESSMETHOD_DirectContractProcess))
					{

						if(contractContent.getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_SpotContract))
						{
							callCreateBaseDocDirectly(contractContent, null);

						}if(contractContent.getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract)){

							MContractProcPeriod period = null;
							if(p_JP_ContractProcPeriod_ID == 0)
							{
								MContractCalender calender = MContractCalender.get(getCtx(), contractContent.getJP_ContractCalender_ID());
								period = calender.getContractProcessPeriod(getCtx(), p_DateAcct);
								p_JP_ContractProcPeriod_ID = period.getJP_ContractProcPeriod_ID();

							}else{

								period = MContractProcPeriod.get(getCtx(), p_JP_ContractProcPeriod_ID);
							}

							//Create Base Doc contract process
							if(p_IsCreateBaseDocJP)
							{
								callCreateBaseDocDirectly(contractContent, period);

							//Create Derivative Doc Contract process
							}else{

								callCreateDerivativeDocDirectly(contractContent, period);

							}
						}

					}else if(p_JP_ContractProcessMethod.equals(MContractContent.JP_CONTRACTPROCESSMETHOD_IndirectContractProcess)) {



						if(p_DocBaseType.equals("JCS"))
						{
							callCreateContractProcSchdule(contractContent, null);

						}else {

							//Get Contract Process Period
							ArrayList<MContractProcPeriod> contractProcPeriodList = getContractProcPeriodList();
							for(MContractProcPeriod procPeriod : contractProcPeriodList)
							{
								if(p_IsCreateBaseDocJP) {

									callCreateBaseDocIndirectly(contractContent, procPeriod);

								}else {

									callCreateDerivativeDocIndirectly(contractContent, procPeriod);

								}
							}

						}


					}else {
						;//TODO エラー
					}


				}else{
					log.log(Level.SEVERE, "Record_ID <= 0 ");
				}

			//Process is kicked from a contract process.
			}else{

				//Get Contract Process Period
				ArrayList<MContractProcPeriod> contractProcPeriodList = getContractProcPeriodList();

				for(MContractProcPeriod procPeriod : contractProcPeriodList)
				{
					//Get Contract Content from Contract Process Period
					ArrayList<MContractContent> contractContentList = getContractContentList(procPeriod);
					processContractContentNum = processContractContentNum + contractContentList.size();

					for(MContractContent contractContent : contractContentList)
					{
						processContractLineNum = processContractLineNum + contractContent.getLines().length;

						if(p_JP_ContractProcessMethod.equals(MContractContent.JP_CONTRACTPROCESSMETHOD_DirectContractProcess))
						{
							if(p_IsCreateBaseDocJP)
							{
								callCreateBaseDocDirectly(contractContent, procPeriod);

							}else{

								callCreateDerivativeDocDirectly(contractContent, procPeriod);
							}

						}else if(p_JP_ContractProcessMethod.equals(MContractContent.JP_CONTRACTPROCESSMETHOD_IndirectContractProcess)) {

							if(p_DocBaseType.equals("JCS"))
							{
								callCreateContractProcSchdule(contractContent, procPeriod);

							}else if(p_IsCreateBaseDocJP) {

								callCreateBaseDocIndirectly(contractContent, procPeriod);

							}else {

								callCreateDerivativeDocIndirectly(contractContent, procPeriod);

							}
						}else {
							;//TODO エラー
						}

					}//for:MContractContent

				}//for:MContractProcPeriod

			}//if

		}else if(p_JP_ContractProcessType.equals(AbstractContractProcess.JP_ContractProcessType_Report)){

			;//not implemented yet.

		}else if(p_JP_ContractProcessType.equals(AbstractContractProcess.JP_ContractProcessType_AutoRenewContract)){//TODO

			ArrayList<MContract> autoRenewContractList = getAutoRenewContractList();
			for(MContract autoRenewContract : autoRenewContractList)
			{
				callAutoRenewContractProcess(autoRenewContract);
			}


		}else if(p_JP_ContractProcessType.equals(AbstractContractProcess.JP_ContractProcessType_ContractStatusUpdate)){

			ArrayList<MContract> contractStatusUpdateList = getContractStatusUpdateList();
			for(MContract contract : contractStatusUpdateList)
			{
				callContractStatusUpdateProcess(contract);
			}

		}


		if(p_JP_ContractProcessTraceLevel.equals(MContractLog.JP_CONTRACTPROCESSTRACELEVEL_NoLog))
			return "";

		StringBuilder returnMsg = new StringBuilder("");
		returnMsg.append(Msg.getMsg(getCtx(), "JP_CreateDocNum")).append(":").append(m_ContractLog.createDocNum).append(" / ");//Number of documents to create
		returnMsg.append(Msg.getMsg(getCtx(), "JP_ToBeConfirmed")).append(":").append(m_ContractLog.confirmNum).append(" / ");//Number of To Be Confirmed
		returnMsg.append(Msg.getMsg(getCtx(), "JP_NumberOfWarnings")).append(":").append(m_ContractLog.warnNum).append(" / ");//Number of warnings
		returnMsg.append(Msg.getMsg(getCtx(), "JP_NumberOfErrors")).append(":").append(m_ContractLog.errorNum).append(" / ");//Number of errors
		returnMsg.append(Msg.getMsg(getCtx(), "JP_SkipNum_ContractContent")).append(":").append(m_ContractLog.skipContractContentNum).append(" / ");  //Number of skips(Contract Content)
		returnMsg.append(Msg.getMsg(getCtx(), "JP_SkipNum_ContractLine")).append(":").append(m_ContractLog.skipContractLineNum).append("  ");  //Number of skips(Contract Content Line)

		StringBuilder systemProcessLog = new StringBuilder("");
		systemProcessLog.append(Msg.getMsg(getCtx(), "JP_Success")).append(":").append(successNum).append(" / ");
		systemProcessLog.append(Msg.getMsg(getCtx(), "JP_Failure")).append(":").append(failureNum).append("  / ");
		systemProcessLog.append(Msg.getElement(getCtx(), "JP_ContractContent_ID")).append(":").append(processContractContentNum).append(" / ");
		systemProcessLog.append(Msg.getElement(getCtx(), "JP_ContractLine_ID")).append(":").append(processContractLineNum).append(" ");



		if(contractLogTrx !=null)
		{
			if(Util.isEmpty(m_ContractLog.getDescription()))
				m_ContractLog.setDescription(returnMsg.toString() + " [ *** System Process Log *** " + systemProcessLog.toString() + " ]");
			else
				m_ContractLog.setDescription(m_ContractLog.getDescription() +"   "+ returnMsg.toString() + " [ " + systemProcessLog.toString()+ " ]");

			m_ContractLog.save(contractLogTrx.getTrxName());
			contractLogTrx.commit();
			contractLogTrx.close();
			contractLogTrx = null;
		}

		return returnMsg.toString();

	}//doContractProcess


	/**
	 *
	 * Get Contract Process Period List
	 *
	 * @return
	 * @throws Exception
	 */
	private ArrayList<MContractProcPeriod> getContractProcPeriodList() throws Exception
	{
		ArrayList<MContractProcPeriod> contractProcPeriodList = new ArrayList<MContractProcPeriod>();

		//1 - Document Date
		if(p_JP_ContractProcessUnit.equals(AbstractContractProcess.JP_ContractProcessUnit_DocumentDate))
		{

			String getProcPeriodSql = "SELECT * FROM JP_ContractProcPeriod WHERE AD_Client_ID = ? AND DateDoc = ?  AND IsActive='Y' ";	//1,2
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				pstmt = DB.prepareStatement (getProcPeriodSql, null);
				pstmt.setInt (1, getAD_Client_ID());
				pstmt.setTimestamp(2, p_DateDoc);
				rs = pstmt.executeQuery ();
				while (rs.next ())
					contractProcPeriodList.add(new MContractProcPeriod(getCtx(), rs, get_TrxName()));

			}
			catch (Exception e)
			{
				throw new Exception(e);
			}
			finally
			{
				DB.close(rs, pstmt);
				rs = null;
				pstmt = null;
			}

		//2 - Date Acct
		}else if(p_JP_ContractProcessUnit.equals(AbstractContractProcess.JP_ContractProcessUnit_AccountDate)){

			String getProcPeriodSql = "SELECT * FROM JP_ContractProcPeriod WHERE AD_Client_ID = ? AND DateAcct = ? AND IsActive='Y' "; //1,2
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				pstmt = DB.prepareStatement (getProcPeriodSql, null);
				pstmt.setInt (1, getAD_Client_ID());
				pstmt.setTimestamp(2, p_DateAcct);
				rs = pstmt.executeQuery ();
				while (rs.next ())
					contractProcPeriodList.add(new MContractProcPeriod(getCtx(), rs, get_TrxName()));
			}
			catch (Exception e)
			{
				throw new Exception(e);
			}
			finally
			{
				DB.close(rs, pstmt);
				rs = null;
				pstmt = null;
			}

		//3 - Contract Process Period
		}else if(p_JP_ContractProcessUnit.equals(AbstractContractProcess.JP_ContractProcessUnit_ContractProcessPeriod)){

			contractProcPeriodList.add(new MContractProcPeriod(getCtx(), p_JP_ContractProcPeriod_ID, get_TrxName()));

		//4 - Contract Process Value of Contract Process Period
		}else if(p_JP_ContractProcessUnit.equals(AbstractContractProcess.JP_ContractProcessUnit_ContractProcessValueofContractProcessPeriod)){

			String getProcPeriodSql = "SELECT * FROM JP_ContractProcPeriod WHERE AD_Client_ID = ? AND JP_ContractProcessValue = ? AND IsActive='Y' ORDER BY StartDate ASC, DateAcct ASC ";//1,2
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				pstmt = DB.prepareStatement (getProcPeriodSql, null);
				pstmt.setInt (1, getAD_Client_ID());
				pstmt.setString(2, p_JP_ContractProcessValue);
				rs = pstmt.executeQuery ();
				while (rs.next ())
					contractProcPeriodList.add(new MContractProcPeriod(getCtx(), rs, get_TrxName()));
			}
			catch (Exception e)
			{
				throw new Exception(e);
			}
			finally
			{
				DB.close(rs, pstmt);
				rs = null;
				pstmt = null;
			}

		//5 - Contract Process Period Group
		}else if(p_JP_ContractProcessUnit.equals(AbstractContractProcess.JP_ContractProcessUnit_ContractProcessPeriodGroup)){

			String getProcPeriodSql = "SELECT * FROM JP_ContractProcPeriod WHERE AD_Client_ID = ? AND JP_ContractProcPeriodG_ID = ? AND IsActive='Y' ORDER BY StartDate ASC, DateAcct ASC  ";//1,2
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				pstmt = DB.prepareStatement (getProcPeriodSql, null);
				pstmt.setInt (1, getAD_Client_ID());
				pstmt.setInt(2, p_JP_ContractProcPeriodG_ID);
				rs = pstmt.executeQuery ();
				while (rs.next ())
					contractProcPeriodList.add(new MContractProcPeriod(getCtx(), rs, get_TrxName()));
			}
			catch (Exception e)
			{
				throw new Exception(e);
			}
			finally
			{
				DB.close(rs, pstmt);
				rs = null;
				pstmt = null;
			}

		//6 - Contract Process Value of Contract Process Period Group
		}else if(p_JP_ContractProcessUnit.equals(AbstractContractProcess.JP_ContractProcessUnit_ContractProcessValueofContractProcessPeriodGroup)){

			String getProcPeriodSql = "SELECT c.* FROM JP_ContractProcPeriod c INNER JOIN JP_ContractProcPeriodG g ON (c.JP_ContractProcPeriodG_ID = g.JP_ContractProcPeriodG_ID) "
												+ " WHERE c.AD_Client_ID = ? AND g.JP_ContractProcessValue = ? AND c.IsActive='Y' AND g.IsActive='Y' ORDER BY c.StartDate ASC , c.DateAcct ASC ";//1,2
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				pstmt = DB.prepareStatement (getProcPeriodSql, null);
				pstmt.setInt (1, getAD_Client_ID());
				pstmt.setString(2, p_JP_ContractProcessValue);
				rs = pstmt.executeQuery ();
				while (rs.next ())
					contractProcPeriodList.add(new MContractProcPeriod(getCtx(), rs, get_TrxName()));
			}
			catch (Exception e)
			{
				throw new Exception(e);
			}
			finally
			{
				DB.close(rs, pstmt);
				rs = null;
				pstmt = null;
			}			;
		}

		return contractProcPeriodList;
	}//

	private ArrayList<MContract> getAutoRenewContractList() throws Exception //TODO
	{
		ArrayList<MContract> list = new ArrayList<MContract>();
		StringBuilder sql = new StringBuilder("SELECT * FROM JP_Contract c WHERE c.DocStatus = 'CO' AND c.JP_ContractStatus IN ('PR' ,'UC') ")
												.append(" AND c.IsAutomaticUpdateJP='Y' AND c.JP_ContractCancelDate IS NULL")
												.append(" AND c.JP_ContractCancelDeadline < ? ");

		LocalDateTime now_LocalDateTime = new Timestamp(System.currentTimeMillis()).toLocalDateTime();
		now_LocalDateTime = now_LocalDateTime.minusDays(1);
		Timestamp now_Timestamp = Timestamp.valueOf(now_LocalDateTime);

		if(p_AD_Org_ID > 0)
			sql.append(" AND c.AD_Org_ID = ? ");

		if(p_JP_ContractCategoryL2_ID > 0)
		{
			sql.append(" AND c.JP_ContractCategoryL2_ID  = ? ");

			if(p_JP_ContractCategoryL1_ID > 0)
			{

				sql.append(" AND c.JP_ContractCategoryL1_ID  = ? ");

				if(p_JP_ContractCategory_ID > 0)
				{
					sql.append(" AND c.JP_ContractCategory_ID  = ? ");
				}
			}
		}

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql.toString(), null);
			int i = 1;
			pstmt.setTimestamp(i++, now_Timestamp);

			if(p_AD_Org_ID > 0)
				pstmt.setInt(i++, p_AD_Org_ID);

			if(p_JP_ContractCategoryL2_ID > 0)
			{
				pstmt.setInt(i++, p_JP_ContractCategoryL2_ID);

				if(p_JP_ContractCategoryL1_ID > 0)
				{

					pstmt.setInt(i++, p_JP_ContractCategoryL1_ID);
					if(p_JP_ContractCategory_ID > 0)
					{
						pstmt.setInt(i++, p_JP_ContractCategory_ID);
					}
				}
			}

			rs = pstmt.executeQuery();
			while(rs.next())
				list.add(new MContract(getCtx(), rs, null));
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, e.toString());
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}


		return list;
	}

	private ArrayList<MContract> getContractStatusUpdateList() throws Exception //TODO
	{
		ArrayList<MContract> list = new ArrayList<MContract>();
		final StringBuilder sql = new StringBuilder("SELECT * FROM JP_Contract c WHERE c.DocStatus = 'CO' AND c.JP_ContractStatus IN ('PR' ,'UC') ");

		if(p_AD_Org_ID > 0)
			sql.append(" AND c.AD_Org_ID = ? ");

		if(p_JP_ContractCategoryL2_ID > 0)
		{
			sql.append(" AND c.JP_ContractCategoryL2_ID  = ? ");

			if(p_JP_ContractCategoryL1_ID > 0)
			{

				sql.append(" AND c.JP_ContractCategoryL1_ID  = ? ");

				if(p_JP_ContractCategory_ID > 0)
				{
					sql.append(" AND c.JP_ContractCategory_ID  = ? ");
				}
			}
		}

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql.toString(), null);
			int i = 1;
			if(p_AD_Org_ID > 0)
				pstmt.setInt(i++, p_AD_Org_ID);

			if(p_JP_ContractCategoryL2_ID > 0)
			{
				pstmt.setInt(i++, p_JP_ContractCategoryL2_ID);

				if(p_JP_ContractCategoryL1_ID > 0)
				{

					pstmt.setInt(i++, p_JP_ContractCategoryL1_ID);
					if(p_JP_ContractCategory_ID > 0)
					{
						pstmt.setInt(i++, p_JP_ContractCategory_ID);
					}
				}
			}

			rs = pstmt.executeQuery();
			while(rs.next())
				list.add(new MContract(getCtx(), rs, null));
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, e.toString());
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}

		return list;
	}

	/**
	 *
	 * Get Contract Content List handler.
	 *
	 * @param procPeriod
	 * @return
	 * @throws Exception
	 */
	private ArrayList<MContractContent> getContractContentList(MContractProcPeriod procPeriod) throws Exception
	{

		if(p_JP_ContractProcessMethod.equals(MContractContent.JP_CONTRACTPROCESSMETHOD_DirectContractProcess))
		{
			return getContractContentList_DirectContractProcess(procPeriod)	;

		}else if (p_JP_ContractProcessMethod.equals(MContractContent.JP_CONTRACTPROCESSMETHOD_IndirectContractProcess)) {

			if(p_JP_IndirectContractProcType.equals(AbstractContractProcess.JP_IndirectContractProcType_ValidContractProcessScheduleInValidContractDoc))
			{
				return getContractContentList_ValidContractProcessScheduleInValidContractDoc(procPeriod)	;

			}else if(p_JP_IndirectContractProcType.equals(AbstractContractProcess.JP_IndirectContractProcType_AllValidContractProcessSchedule))
			{
				return getContractContentList_AllValidContractProcessSchedule(procPeriod);//TODO

			}

		}

		return null;
	}


	/**
	 *
	 * Get MContractContent List that is used by Direct Contract Process.
	 *
	 * @param procPeriod
	 * @return
	 * @throws Exception
	 */
	private ArrayList<MContractContent> getContractContentList_DirectContractProcess(MContractProcPeriod procPeriod) throws Exception
	{

		StringBuilder getContractContentSQL = new StringBuilder("");

		//Get Contract Content that create Base Doc(Order and Invoice).
		if(p_IsCreateBaseDocJP)
		{
			getContractContentSQL.append("SELECT cc.* FROM JP_ContractContent cc "
				+ " INNER JOIN JP_Contract c ON (cc.JP_Contract_ID = C.JP_Contract_ID)"
				+ " WHERE cc.AD_Client_ID = ?"	//1
				+ " AND c.JP_ContractType = 'PDC'"
				+ " AND c.DocStatus = 'CO' AND c.JP_ContractStatus IN ('PR','UC')"//Contract Status in 'Prepare' and 'Under Contract'
				+ " AND cc.DocStatus = 'CO' AND cc.JP_ContractProcStatus IN ('UN','IP')" //Contract Process Status in 'Unprocessed' and 'In Progress'
				+ " AND cc.JP_ContractCalender_ID = ?" //2
				+ " AND cc.JP_ContractProcDate_From <=? AND (cc.JP_ContractProcDate_To is null or cc.JP_ContractProcDate_To >=?)"//3,4
				+ " ");

		//Get Contract Content that create Derivative InOut Doc from Order
		}else if( p_DocBaseType.equals("MMS") || p_DocBaseType.equals("MMR") ){

			getContractContentSQL.append("SELECT DISTINCT cc.* FROM JP_ContractContent cc "
					+ " INNER JOIN JP_Contract c ON (cc.JP_Contract_ID = C.JP_Contract_ID)"
					+ " INNER JOIN JP_ContractLine cl ON (cc.JP_ContractContent_ID = Cl.JP_ContractContent_ID)"
					+ " WHERE cc.AD_Client_ID = ?"	//1
					+ " AND c.JP_ContractType = 'PDC'"
					+ " AND c.DocStatus = 'CO' AND c.JP_ContractStatus IN ('PR','UC')"//Contract Status in 'Prepare' and 'Under Contract'
					+ " AND cc.DocStatus = 'CO' AND cc.JP_ContractProcStatus = 'IP' " //Contract Process Status in 'In Progress'
					+ " AND cl.JP_ContractCalender_InOut_ID = ?" //2
					+ " AND cc.JP_ContractProcDate_From <=? AND (cc.JP_ContractProcDate_To is null or cc.JP_ContractProcDate_To >=?)"//3,4
					+ " AND cc.JP_CreateDerivativeDocPolicy IN ('BT','IO') ");

		//Get Contract Content that create Derivative Invoice Doc from Order
		}else if( p_DocBaseType.equals("ARI") || p_DocBaseType.equals("API") ){

			getContractContentSQL.append("SELECT DISTINCT cc.* FROM JP_ContractContent cc "
					+ " INNER JOIN JP_Contract c ON (cc.JP_Contract_ID = C.JP_Contract_ID)"
					+ " INNER JOIN JP_ContractLine cl ON (cc.JP_ContractContent_ID = Cl.JP_ContractContent_ID)"
					+ " WHERE cc.AD_Client_ID = ?"	//1
					+ " AND c.JP_ContractType = 'PDC'"
					+ " AND c.DocStatus = 'CO' AND c.JP_ContractStatus IN ('PR','UC')"//Contract Status in 'Prepare' and 'Under Contract'
					+ " AND cc.DocStatus = 'CO' AND cc.JP_ContractProcStatus = 'IP' " //Contract Process Status in 'In Progress'
					+ " AND cl.JP_ContractCalender_Inv_ID = ?" //2
					+ " AND cc.JP_ContractProcDate_From <=? AND (cc.JP_ContractProcDate_To is null or cc.JP_ContractProcDate_To >=?)"//3,4
					+ " AND cc.JP_CreateDerivativeDocPolicy IN ('BT','IV') ");
		}

		if(p_AD_Org_ID > 0)
			getContractContentSQL.append(" AND cc.AD_Org_ID = ? ");
		if(p_JP_ContractCategory_ID > 0)
			getContractContentSQL.append(" AND c.JP_ContractCategory_ID = ? ");
		if(p_C_DocType_ID > 0)
			getContractContentSQL.append(" AND cc.C_DocType_ID = ? ");
		if(p_JP_Contract_ID > 0)
			getContractContentSQL.append(" AND c.JP_Contract_ID = ? ");
		if(p_JP_ContractContent_ID > 0)
			getContractContentSQL.append(" AND cc.JP_ContractContent_ID = ? ");


		if(p_IsCreateBaseDocJP)
		{
			getContractContentSQL.append(" AND cc.DocBaseType = ? ");

		}else{

			if(p_DocBaseType.equals("MMS") ||p_DocBaseType.equals("ARI") )
				getContractContentSQL.append(" AND cc.DocBaseType ='SOO' ");
			else if(p_DocBaseType.equals("MMR") ||p_DocBaseType.equals("API") )
				getContractContentSQL.append(" AND cc.DocBaseType ='POO' ");
		}

		getContractContentSQL.append(" AND cc.JP_ContractProcessMethod ='DC' ");



		ArrayList<MContractContent> contractContentList = new ArrayList<MContractContent>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (getContractContentSQL.toString(), null);
			int i = 1;
			pstmt.setInt (i++, getAD_Client_ID());	//1
			pstmt.setInt(i++, procPeriod.getJP_ContractCalender_ID());	//2
			pstmt.setTimestamp(i++, procPeriod.getEndDate());	//3
			pstmt.setTimestamp(i++, procPeriod.getStartDate());	//4
			if(p_AD_Org_ID > 0)
				pstmt.setInt (i++, p_AD_Org_ID);
			if(p_JP_ContractCategory_ID > 0)
				pstmt.setInt (i++, p_JP_ContractCategory_ID);
			if(p_C_DocType_ID > 0)
				pstmt.setInt (i++, p_C_DocType_ID);
			if(p_JP_Contract_ID > 0)
				pstmt.setInt (i++, p_JP_Contract_ID);
			if(p_JP_ContractContent_ID > 0)
				pstmt.setInt (i++, p_JP_ContractContent_ID);


			if(p_IsCreateBaseDocJP)
			{
				pstmt.setString (i++, p_DocBaseType);

			}else {

				;

			}

			rs = pstmt.executeQuery ();
			while (rs.next ())
			{
				contractContentList.add(new MContractContent(getCtx(), rs, get_TrxName()));
			}
		}
		catch (Exception e)
		{
			throw new Exception(e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

		return contractContentList;
	}


	/**
	 *
	 * Get MContractContent List that Valid Contract Process Schedule In valid Contract Document.
	 *
	 * @param procPeriod
	 * @return
	 * @throws Exception
	 */
	private ArrayList<MContractContent> getContractContentList_ValidContractProcessScheduleInValidContractDoc(MContractProcPeriod procPeriod) throws Exception
	{

		StringBuilder getContractContentSQL = new StringBuilder("");

		//Get Contract Content that create Base Doc(Order and Invoice) and Contract Process Schedule
		if(p_IsCreateBaseDocJP || p_DocBaseType.equals("JCS") )
		{
			getContractContentSQL.append("SELECT cc.* FROM JP_ContractContent cc "
					+ " INNER JOIN JP_Contract c ON (cc.JP_Contract_ID = C.JP_Contract_ID)"
					+ " WHERE cc.AD_Client_ID = ?"	//1
					+ " AND c.JP_ContractType = 'PDC'"
					+ " AND c.DocStatus = 'CO' AND c.JP_ContractStatus IN ('PR','UC')"//Contract Status in 'Prepare' and 'Under Contract'
					+ " AND cc.DocStatus = 'CO' AND cc.JP_ContractProcStatus IN ('UN','IP')" //Contract Process Status in 'Unprocessed' and 'In Progress'
					+ " AND cc.JP_ContractCalender_ID = ?" //2
					+ " AND cc.JP_ContractProcDate_From <=? AND (cc.JP_ContractProcDate_To is null or cc.JP_ContractProcDate_To >=?)"//3,4
					+ " ");

		//Get Contract Content that create Derivative InOut Doc from Order
		}else if( p_DocBaseType.equals("MMS") || p_DocBaseType.equals("MMR") ){

			getContractContentSQL.append("SELECT DISTINCT cc.* FROM JP_ContractContent cc "
					+ " INNER JOIN JP_Contract c ON (cc.JP_Contract_ID = C.JP_Contract_ID)"
					+ " INNER JOIN JP_ContractLine cl ON (cc.JP_ContractContent_ID = Cl.JP_ContractContent_ID)"
					+ " WHERE cc.AD_Client_ID = ?"	//1
					+ " AND c.JP_ContractType = 'PDC'"
					+ " AND c.DocStatus = 'CO' AND c.JP_ContractStatus IN ('PR','UC')"//Contract Status in 'Prepare' and 'Under Contract'
					+ " AND cc.DocStatus = 'CO' AND cc.JP_ContractProcStatus = 'IP' " //Contract Process Status in 'In Progress'
					+ " AND cl.JP_ContractCalender_InOut_ID = ?" //2
					+ " AND cc.JP_ContractProcDate_From <=? AND (cc.JP_ContractProcDate_To is null or cc.JP_ContractProcDate_To >=?)"//3,4
					+ " AND cc.JP_CreateDerivativeDocPolicy IN ('BT','IO') ");

		//Get Contract Content that create Derivative Invoice Doc from Order
		}else if( p_DocBaseType.equals("ARI") || p_DocBaseType.equals("API") ){

			getContractContentSQL.append("SELECT DISTINCT cc.* FROM JP_ContractContent cc "
					+ " INNER JOIN JP_Contract c ON (cc.JP_Contract_ID = C.JP_Contract_ID)"
					+ " INNER JOIN JP_ContractLine cl ON (cc.JP_ContractContent_ID = Cl.JP_ContractContent_ID)"
					+ " WHERE cc.AD_Client_ID = ?"	//1
					+ " AND c.JP_ContractType = 'PDC'"
					+ " AND c.DocStatus = 'CO' AND c.JP_ContractStatus IN ('PR','UC')"//Contract Status in 'Prepare' and 'Under Contract'
					+ " AND cc.DocStatus = 'CO' AND cc.JP_ContractProcStatus = 'IP' " //Contract Process Status in 'In Progress'
					+ " AND cl.JP_ContractCalender_Inv_ID = ?" //2
					+ " AND cc.JP_ContractProcDate_From <=? AND (cc.JP_ContractProcDate_To is null or cc.JP_ContractProcDate_To >=?)"//3,4
					+ " AND cc.JP_CreateDerivativeDocPolicy IN ('BT','IV') ");
		}

		if(p_AD_Org_ID > 0)
			getContractContentSQL.append(" AND cc.AD_Org_ID = ? ");
		if(p_JP_ContractCategory_ID > 0)
			getContractContentSQL.append(" AND c.JP_ContractCategory_ID = ? ");
		if(p_C_DocType_ID > 0)
			getContractContentSQL.append(" AND cc.C_DocType_ID = ? ");
		if(p_JP_Contract_ID > 0)
			getContractContentSQL.append(" AND c.JP_Contract_ID = ? ");
		if(p_JP_ContractContent_ID > 0)
			getContractContentSQL.append(" AND cc.JP_ContractContent_ID = ? ");


		if(p_DocBaseType.equals("JCS"))
		{
			;//Nothing to do

		}else if(p_IsCreateBaseDocJP) {

			getContractContentSQL.append(" AND cc.DocBaseType = ? ");

		}else {

			if(p_DocBaseType.equals("MMS") ||p_DocBaseType.equals("ARI") )
				getContractContentSQL.append(" AND cc.DocBaseType ='SOO' ");
			else if(p_DocBaseType.equals("MMR") ||p_DocBaseType.equals("API") )
				getContractContentSQL.append(" AND cc.DocBaseType ='POO' ");

		}

		getContractContentSQL.append(" AND cc.JP_ContractProcessMethod ='IC' ");


		ArrayList<MContractContent> contractContentList = new ArrayList<MContractContent>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (getContractContentSQL.toString(), null);
			int i = 1;
			pstmt.setInt (i++, getAD_Client_ID());	//1
			pstmt.setInt(i++, procPeriod.getJP_ContractCalender_ID());	//2
			pstmt.setTimestamp(i++, procPeriod.getEndDate());	//3
			pstmt.setTimestamp(i++, procPeriod.getStartDate());	//4
			if(p_AD_Org_ID > 0)
				pstmt.setInt (i++, p_AD_Org_ID);
			if(p_JP_ContractCategory_ID > 0)
				pstmt.setInt (i++, p_JP_ContractCategory_ID);
			if(p_C_DocType_ID > 0)
				pstmt.setInt (i++, p_C_DocType_ID);
			if(p_JP_Contract_ID > 0)
				pstmt.setInt (i++, p_JP_Contract_ID);
			if(p_JP_ContractContent_ID > 0)
				pstmt.setInt (i++, p_JP_ContractContent_ID);


			if(p_DocBaseType.equals("JCS"))
			{
				;//Nothing to do

			}else if(p_IsCreateBaseDocJP) {

				pstmt.setString (i++, p_DocBaseType);

			}else {

				;

			}


			rs = pstmt.executeQuery ();
			while (rs.next ())
			{
				contractContentList.add(new MContractContent(getCtx(), rs, get_TrxName()));
			}
		}
		catch (Exception e)
		{
			throw new Exception(e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

		return contractContentList;
	}


	/**
	 *
	 * Get MContractContent List That All Valid Contract Process Schedule.
	 *
	 * @param procPeriod
	 * @return
	 * @throws Exception
	 */
	private ArrayList<MContractContent> getContractContentList_AllValidContractProcessSchedule(MContractProcPeriod procPeriod) throws Exception
	{
		StringBuilder getContractContentSQL = new StringBuilder("");

		//Get Contract Content that create Contract Process Schedule.
		if(p_DocBaseType.equals("JCS") )
		{
			getContractContentSQL.append("SELECT cc.* FROM JP_ContractContent cc "
					+ " INNER JOIN JP_Contract c ON (cc.JP_Contract_ID = C.JP_Contract_ID)"
					+ " WHERE cc.AD_Client_ID = ?"	//1
					+ " AND c.JP_ContractType = 'PDC'"
					+ " AND c.DocStatus = 'CO' AND c.JP_ContractStatus IN ('PR','UC')"//Contract Status in 'Prepare' and 'Under Contract'
					+ " AND cc.DocStatus = 'CO' AND cc.JP_ContractProcStatus IN ('UN')" //Contract Process Status in 'Unprocessed'
					+ " AND cc.IsScheduleCreatedJP = 'N' "
					+ " AND cc.JP_ContractCalender_ID = ?" //2
					+ " AND cc.JP_ContractProcDate_From <=? AND (cc.JP_ContractProcDate_To is null or cc.JP_ContractProcDate_To >=?)"//3,4
					+ " AND cc.JP_ContractProcessMethod ='IC' "
					+ " ");

		//Get Contract Content that create Base Doc(Order and Invoice).
		}else if(p_IsCreateBaseDocJP )	{

			getContractContentSQL.append("SELECT cc.* FROM JP_ContractContent cc "
					+ " INNER JOIN JP_Contract c ON (cc.JP_Contract_ID = c.JP_Contract_ID)"
					+ " INNER JOIN JP_ContractProcSchedule cps ON (cc.JP_ContractContent_ID = cps.JP_ContractContent_ID)"
					+ " WHERE cc.AD_Client_ID = ?"				//1
					+ " AND c.JP_ContractType = 'PDC'"
					+ " AND cps.DocStatus = 'CO' "
					+ " AND cps.IsFactCreatedJP = 'N' "
					+ " AND cps.JP_ContractCalender_ID = ?" 	//2
					+ " AND cps.JP_ContractProcPeriod_ID = ?"	//3
					+ " ");

		//Get Contract Content that create Derivative InOut Doc from Order
		}else if( p_DocBaseType.equals("MMS") || p_DocBaseType.equals("MMR") ){

			getContractContentSQL.append("SELECT DISTINCT cc.* FROM JP_ContractContent cc "
					+ " INNER JOIN JP_Contract c ON (cc.JP_Contract_ID = C.JP_Contract_ID)"
					+ " INNER JOIN JP_ContractProcSchedule cps ON (cc.JP_ContractContent_ID = cps.JP_ContractContent_ID)"
					+ " INNER JOIN JP_ContractPSInOutLine cpsl ON (cps.JP_ContractProcSchedule_ID = cpsl.JP_ContractProcSchedule_ID)"
					+ " WHERE cc.AD_Client_ID = ?"					//1
					+ " AND c.JP_ContractType = 'PDC'"
					+ " AND cps.DocStatus = 'CO' "
					+ " AND cpsl.IsFactCreatedJP = 'N' "
					+ " AND cpsl.JP_ContractCalender_InOut_ID = ?" 	//2
					+ " AND cpsl.JP_ContractProcPeriod_ID = ?"		//3
					+ " AND cps.JP_CreateDerivativeDocPolicy IN ('BT','IO') ");

		//Get Contract Content that create Derivative Invoice Doc from Order
		}else if( p_DocBaseType.equals("ARI") || p_DocBaseType.equals("API") ){

			getContractContentSQL.append("SELECT DISTINCT cc.* FROM JP_ContractContent cc "
					+ " INNER JOIN JP_Contract c ON (cc.JP_Contract_ID = C.JP_Contract_ID)"
					+ " INNER JOIN JP_ContractProcSchedule cps ON (cc.JP_ContractContent_ID = cps.JP_ContractContent_ID)"
					+ " INNER JOIN JP_ContractPSInvoiceLine cpsl ON (cps.JP_ContractProcSchedule_ID = cpsl.JP_ContractProcSchedule_ID)"
					+ " WHERE cc.AD_Client_ID = ?"					//1
					+ " AND c.JP_ContractType = 'PDC'"
					+ " AND cps.DocStatus = 'CO' "
					+ " AND cpsl.IsFactCreatedJP = 'N' "
					+ " AND cpsl.JP_ContractCalender_Inv_ID = ?" 	//2
					+ " AND cpsl.JP_ContractProcPeriod_ID = ?"		//3
					+ " AND cps.JP_CreateDerivativeDocPolicy IN ('BT','IO') ");
		}

		if(p_AD_Org_ID > 0)
			getContractContentSQL.append(" AND cps.AD_Org_ID = ? ");
		if(p_JP_ContractCategory_ID > 0)
			getContractContentSQL.append(" AND c.JP_ContractCategory_ID = ? ");
		if(p_C_DocType_ID > 0)
			getContractContentSQL.append(" AND cc.C_DocType_ID = ? ");
		if(p_JP_Contract_ID > 0)
			getContractContentSQL.append(" AND c.JP_Contract_ID = ? ");
		if(p_JP_ContractContent_ID > 0)
			getContractContentSQL.append(" AND cc.JP_ContractContent_ID = ? ");

		if(p_DocBaseType.equals("JCS"))
		{
			;//Nothing to do

		}else if(p_IsCreateBaseDocJP) {

			getContractContentSQL.append(" AND cps.DocBaseType = ? ");

		}else {

			if(p_DocBaseType.equals("MMS") ||p_DocBaseType.equals("ARI") )
				getContractContentSQL.append(" AND cc.DocBaseType ='SOO' ");
			else if(p_DocBaseType.equals("MMR") ||p_DocBaseType.equals("API") )
				getContractContentSQL.append(" AND cc.DocBaseType ='POO' ");

		}


		ArrayList<MContractContent> contractContentList = new ArrayList<MContractContent>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (getContractContentSQL.toString(), null);
			int i = 1;
			pstmt.setInt (i++, getAD_Client_ID());								//1
			pstmt.setInt(i++, procPeriod.getJP_ContractCalender_ID());			//2
			if(p_DocBaseType.equals("JCS"))
			{
				pstmt.setTimestamp(i++, procPeriod.getEndDate());				//3
				pstmt.setTimestamp(i++, procPeriod.getStartDate());				//4

			}else {
				pstmt.setInt(i++, procPeriod.getJP_ContractProcPeriod_ID());	//3
			}

			if(p_AD_Org_ID > 0)
				pstmt.setInt (i++, p_AD_Org_ID);
			if(p_JP_ContractCategory_ID > 0)
				pstmt.setInt (i++, p_JP_ContractCategory_ID);
			if(p_C_DocType_ID > 0)
				pstmt.setInt (i++, p_C_DocType_ID);
			if(p_JP_Contract_ID > 0)
				pstmt.setInt (i++, p_JP_Contract_ID);
			if(p_JP_ContractContent_ID > 0)
				pstmt.setInt (i++, p_JP_ContractContent_ID);


			if(p_DocBaseType.equals("JCS"))
			{
				;//Nothing to do

			}else if(p_IsCreateBaseDocJP) {

				pstmt.setString (i++, p_DocBaseType);

			}else {

				;

			}

			rs = pstmt.executeQuery ();
			while (rs.next ())
			{
				contractContentList.add(new MContractContent(getCtx(), rs, get_TrxName()));
			}
		}
		catch (Exception e)
		{
			throw new Exception(e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

		return contractContentList;

	}


	/**
	 *
	 * Call Process that create Base Doc from Contract Doc directly
	 *
	 * @param contractContent
	 * @param procPeriod
	 * @throws Exception
	 */
	private void callCreateBaseDocDirectly(MContractContent contractContent, MContractProcPeriod procPeriod) throws Exception
	{
		ProcessInfo pi = new ProcessInfo("CreateBaseDoc", 0);
		String className = null;
		if(p_DocBaseType.equals("SOO") || p_DocBaseType.equals("POO"))
		{
			if(Util.isEmpty(MContractProcess.get(getCtx(), contractContent.getJP_ContractProcess_ID()).getClassname()))
			{
				className = "jpiere.base.plugin.org.adempiere.process.DefaultContractProcessCreateBaseOrder";

			}else{
				className = contractContent.getJP_ContractProcess().getClassname();
			}
		}

		if(p_DocBaseType.equals("ARI") || p_DocBaseType.equals("API"))
		{
			if(Util.isEmpty(MContractProcess.get(getCtx(), contractContent.getJP_ContractProcess_ID()).getClassname()))
			{
				className = "jpiere.base.plugin.org.adempiere.process.DefaultContractProcessCreateBaseInvoice";

			}else{
				className = contractContent.getJP_ContractProcess().getClassname();
			}
		}
		pi.setClassName(className);
		pi.setAD_Client_ID(getAD_Client_ID());
		pi.setAD_User_ID(getAD_User_ID());
		pi.setAD_PInstance_ID(getAD_PInstance_ID());
		pi.setRecord_ID(contractContent.getJP_ContractContent_ID());

		//Update ProcessInfoParameter
		ArrayList<ProcessInfoParameter> list = new ArrayList<ProcessInfoParameter>();
		list.add (new ProcessInfoParameter("JP_ContractProcess_ID", contractContent.getJP_ContractProcess_ID(), null, null, null ));
		list.add (new ProcessInfoParameter("JP_ContractLog", m_ContractLog, null, null, null ));
		setProcessInfoParameter(pi, list, procPeriod);

		if(startProcess(pi))
			successNum++;
		else
			failureNum++;
	}


	/**
	 *
	 * Call Process that create Derivative Doc from Contract Doc directly
	 *
	 * @param contractContent
	 * @param procPeriod
	 * @throws Exception
	 */
	private void callCreateDerivativeDocDirectly(MContractContent contractContent, MContractProcPeriod procPeriod) throws Exception
	{
		MContractProcess[] contractProcesses =  null;

		if(p_DocBaseType.equals("MMS")|| p_DocBaseType.equals("MMR"))
		{
			contractProcesses =  contractContent.getContractProcessDerivativeInOutByCalender(procPeriod.getJP_ContractCalender_ID());

		}else if(p_DocBaseType.equals("ARI")|| p_DocBaseType.equals("API")){

			contractProcesses =  contractContent.getContractProcessDerivativeInvoiceByCalender(procPeriod.getJP_ContractCalender_ID());
		}

		processContractLineNum = processContractLineNum + contractProcesses.length;
		for(int i = 0; i < contractProcesses.length; i++)
		{
			String className = null;
			if(Util.isEmpty(contractProcesses[i].getClassname()))
			{
				if(p_DocBaseType.equals("MMS")|| p_DocBaseType.equals("MMR"))
				{
					className = "jpiere.base.plugin.org.adempiere.process.DefaultContractProcessCreateDerivativeInOut";

				}else if(p_DocBaseType.equals("ARI")|| p_DocBaseType.equals("API")){

					className = "jpiere.base.plugin.org.adempiere.process.DefaultContractProcessCreateDerivativeInvoice";
				}

			}else{
				className = contractProcesses[i].getClassname();
			}

			ProcessInfo pi = new ProcessInfo("CreateDerivativeDoc", 0);
			pi.setClassName(className);
			pi.setAD_Client_ID(getAD_Client_ID());
			pi.setAD_User_ID(getAD_User_ID());
			pi.setAD_PInstance_ID(getAD_PInstance_ID());
			pi.setRecord_ID(contractContent.getJP_ContractContent_ID());

			ArrayList<ProcessInfoParameter> list = new ArrayList<ProcessInfoParameter>();
			list.add (new ProcessInfoParameter("JP_ContractProcess_ID", contractProcesses[i].getJP_ContractProcess_ID(), null, null, null ));
			list.add (new ProcessInfoParameter("JP_ContractLog", m_ContractLog, null, null, null ));
			setProcessInfoParameter(pi, list, procPeriod);

			if(startProcess(pi))
				successNum++;
			else
				failureNum++;

		}//for

	}

	/**
	 *
	 * Call Process that create Contract Process Schdule
	 *
	 *
	 * @param contractContent
	 * @param procPeriod
	 * @throws Exception
	 */
	private void callCreateContractProcSchdule(MContractContent contractContent, MContractProcPeriod procPeriod) throws Exception
	{
		ProcessInfo pi = new ProcessInfo("CreateContractProcessSchdule", 0);
		String className = null;
		if(Util.isEmpty(MContractProcess.get(getCtx(), contractContent.getJP_ContractProcess_ID()).getJP_CreateContractPSClass()))
		{
			className = "jpiere.base.plugin.org.adempiere.process.DefaultContractProcessCreateSchedule";

		}else{
			className = contractContent.getJP_ContractProcess().getJP_CreateContractPSClass();
		}

		pi.setClassName(className);
		pi.setAD_Client_ID(getAD_Client_ID());
		pi.setAD_User_ID(getAD_User_ID());
		pi.setAD_PInstance_ID(getAD_PInstance_ID());
		pi.setRecord_ID(contractContent.getJP_ContractContent_ID());

		//Update ProcessInfoParameter
		ArrayList<ProcessInfoParameter> list = new ArrayList<ProcessInfoParameter>();
		list.add (new ProcessInfoParameter("JP_ContractProcess_ID", contractContent.getJP_ContractProcess_ID(), null, null, null ));
		list.add (new ProcessInfoParameter("JP_ContractLog", m_ContractLog, null, null, null ));
		setProcessInfoParameter(pi, list, procPeriod);

		if(startProcess(pi))
			successNum++;
		else
			failureNum++;

	}

	/***
	 *
	 * Call Process that create Base Doc from Contract Process Schdule Indirectly
	 *
	 * @param contractContent
	 * @param procPeriod
	 * @throws Exception
	 */
	private void callCreateBaseDocIndirectly(MContractContent contractContent, MContractProcPeriod procPeriod) throws Exception
	{
		ProcessInfo pi = new ProcessInfo("CreateBaseDoc", 0);
		String className = null;
		if(p_DocBaseType.equals("SOO") || p_DocBaseType.equals("POO"))
		{
			if(Util.isEmpty(MContractProcess.get(getCtx(), contractContent.getJP_ContractProcess_ID()).getJP_IndirectContractProcClass()))
			{
				className = "jpiere.base.plugin.org.adempiere.process.DefaultContractProcessCreateBaseOrderIndirectly";

			}else{
				className = contractContent.getJP_ContractProcess().getJP_IndirectContractProcClass();
			}
		}

		if(p_DocBaseType.equals("ARI") || p_DocBaseType.equals("API"))
		{
			if(Util.isEmpty(MContractProcess.get(getCtx(), contractContent.getJP_ContractProcess_ID()).getJP_IndirectContractProcClass()))
			{
				className = "jpiere.base.plugin.org.adempiere.process.DefaultContractProcessCreateBaseInvoiceIndirectly";

			}else{
				className = contractContent.getJP_ContractProcess().getJP_IndirectContractProcClass();
			}
		}
		pi.setClassName(className);
		pi.setAD_Client_ID(getAD_Client_ID());
		pi.setAD_User_ID(getAD_User_ID());
		pi.setAD_PInstance_ID(getAD_PInstance_ID());
		pi.setRecord_ID(contractContent.getJP_ContractContent_ID());

		//Update ProcessInfoParameter
		ArrayList<ProcessInfoParameter> list = new ArrayList<ProcessInfoParameter>();
		list.add (new ProcessInfoParameter("JP_ContractProcess_ID", contractContent.getJP_ContractProcess_ID(), null, null, null ));
		list.add (new ProcessInfoParameter("JP_ContractLog", m_ContractLog, null, null, null ));
		setProcessInfoParameter(pi, list, procPeriod);

		if(startProcess(pi))
			successNum++;
		else
			failureNum++;
	}


	/**
	 *
	 * Call Process that create Derivative Doc from Contract Process Schdule Indirectly
	 *
	 * @param contractContent
	 * @param procPeriod
	 * @throws Exception
	 */
	private void callCreateDerivativeDocIndirectly(MContractContent contractContent, MContractProcPeriod procPeriod) throws Exception
	{
		MContractProcess[] contractProcesses =  null;

		if(p_DocBaseType.equals("MMS")|| p_DocBaseType.equals("MMR"))
		{
			contractProcesses =  contractContent.getContractProcessDerivativeInOutByCalender(procPeriod.getJP_ContractCalender_ID());

		}else if(p_DocBaseType.equals("ARI")|| p_DocBaseType.equals("API")){

			contractProcesses =  contractContent.getContractProcessDerivativeInvoiceByCalender(procPeriod.getJP_ContractCalender_ID());
		}

		processContractLineNum = processContractLineNum + contractProcesses.length;
		for(int i = 0; i < contractProcesses.length; i++)
		{
			String className = null;
			if(Util.isEmpty(contractProcesses[i].getJP_IndirectContractProcClass()))
			{
				if(p_DocBaseType.equals("MMS")|| p_DocBaseType.equals("MMR"))
				{
					className = "jpiere.base.plugin.org.adempiere.process.DefaultContractProcessCreateDerivativeInOutIndirectly";

				}else if(p_DocBaseType.equals("ARI")|| p_DocBaseType.equals("API")){

					className = "jpiere.base.plugin.org.adempiere.process.DefaultContractProcessCreateDerivativeInvoiceIndirectly";
				}

			}else{
				className = contractProcesses[i].getJP_IndirectContractProcClass();
			}

			ProcessInfo pi = new ProcessInfo("CreateDerivativeDoc", 0);
			pi.setClassName(className);
			pi.setAD_Client_ID(getAD_Client_ID());
			pi.setAD_User_ID(getAD_User_ID());
			pi.setAD_PInstance_ID(getAD_PInstance_ID());
			pi.setRecord_ID(contractContent.getJP_ContractContent_ID());

			ArrayList<ProcessInfoParameter> list = new ArrayList<ProcessInfoParameter>();
			list.add (new ProcessInfoParameter("JP_ContractProcess_ID", contractProcesses[i].getJP_ContractProcess_ID(), null, null, null ));
			list.add (new ProcessInfoParameter("JP_ContractLog", m_ContractLog, null, null, null ));
			setProcessInfoParameter(pi, list, procPeriod);

			if(startProcess(pi))
				successNum++;
			else
				failureNum++;

		}//for

	}

	private void callAutoRenewContractProcess(MContract autoRenewContract) throws Exception //TODO
	{

		String className = null;
		if(Util.isEmpty(p_JP_ContractAutoRenewClass))
		{
			className = "jpiere.base.plugin.org.adempiere.process.DefaultAutoRenewContractProcess";

		}else{
			className = p_JP_ContractAutoRenewClass;
		}

		ProcessInfo pi = new ProcessInfo("CreateDerivativeDoc", 0);
		pi.setClassName(className);
		pi.setAD_Client_ID(getAD_Client_ID());
		pi.setAD_User_ID(getAD_User_ID());
		pi.setAD_PInstance_ID(getAD_PInstance_ID());
		pi.setRecord_ID(0);

		ArrayList<ProcessInfoParameter> list = new ArrayList<ProcessInfoParameter>();
		list.add (new ProcessInfoParameter("JP_Contract", autoRenewContract, null, null, null ));
		list.add (new ProcessInfoParameter("JP_ContractLog", m_ContractLog, null, null, null ));
		setProcessInfoParameter(pi, list, null);

		if(startProcess(pi))
			successNum++;
		else
			failureNum++;
	}

	private void callContractStatusUpdateProcess(MContract contract) throws Exception //TODO
	{
		String className = null;
		if(Util.isEmpty(p_JP_ContractStatusUpdateClass))
		{
			className = "jpiere.base.plugin.org.adempiere.process.DefaultContractStatusUpdateProcess";

		}else{
			className = p_JP_ContractAutoRenewClass;
		}

		ProcessInfo pi = new ProcessInfo("Contract status Update", 0);
		pi.setClassName(className);
		pi.setAD_Client_ID(getAD_Client_ID());
		pi.setAD_User_ID(getAD_User_ID());
		pi.setAD_PInstance_ID(getAD_PInstance_ID());
		pi.setRecord_ID(0);

		ArrayList<ProcessInfoParameter> list = new ArrayList<ProcessInfoParameter>();
		list.add (new ProcessInfoParameter("JP_Contract", contract, null, null, null ));
		list.add (new ProcessInfoParameter("JP_ContractLog", m_ContractLog, null, null, null ));
		setProcessInfoParameter(pi, list, null);

		if(startProcess(pi))
			successNum++;
		else
			failureNum++;
	}
	/**
	 *
	 * Set ProcessInfoParameter
	 *
	 * @param pi
	 * @param list
	 * @param procPeriod
	 * @throws Exception
	 */
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

	private IProcessUI processUI = null;

	/**
	 *
	 * Start Process
	 *
	 * @param pi
	 * @return
	 * @throws Exception
	 */
	private boolean startProcess(ProcessInfo pi) throws Exception
	{
		if(processUI == null)
		{
			processUI = Env.getProcessUI(getCtx());

		}

		boolean success = ProcessUtil.startJavaProcess(getCtx(), pi, Trx.get(get_TrxName(), true), false, processUI);
		if(success)
		{
			if(p_IsRecordCommitJP)
			{
				try
				{
					commitEx();
					if(contractLogTrx != null)
					{
						contractLogTrx.commit();
					}

				} catch (SQLException e) {

					throw e;
				}

			}else{

				if(contractLogTrx != null)
				{
					contractLogTrx.commit();
				}

			}

		}else{

			if(contractLogTrx != null)
			{
				MContractLogDetail logDetail = new MContractLogDetail(getCtx(), 0, m_ContractLog.get_TrxName());
				logDetail.setJP_ContractLog_ID(m_ContractLog.getJP_ContractLog_ID());
				logDetail.setJP_ContractLogMsg(MContractLogDetail.JP_CONTRACTLOGMSG_UnexpectedError);
				logDetail.setJP_ContractProcessTraceLevel(MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_Error);
				logDetail.setDescription(pi.getSummary());
				if(pi.getRecord_ID() > 0 )
					logDetail.setJP_ContractContent_ID(pi.getRecord_ID());

				logDetail.saveEx(m_ContractLog.get_TrxName());
				contractLogTrx.commit();
			}

			if(p_IsRecordCommitJP)//CONTINUOUS PROCESSING
			{
				try
				{

					rollback();//one record only

				} catch (Exception e) {

					throw e;
				}

			}else{//Finish Process

				throw new AdempiereException(pi.getSummary());
			}
		}

		return success;
	}


	/**
	 *
	 * Process Running
	 *
	 * Ref: IDEMPIERE-3799 - ProcessInfo.java
	 *
	 * @return
	 */
	private boolean isProcessRunning()
	{

		Timestamp lastRebootDate = getLastServerRebootDate();
		if (lastRebootDate == null)
			return false;

		List<MPInstance> processInstanceList = new Query(Env.getCtx(), MPInstance.Table_Name, " AD_Process_ID=? AND IsProcessing='Y' AND record_ID = ? AND Created > ? ", null)
				.setParameters(getProcessInfo().getAD_Process_ID(), getRecord_ID(), lastRebootDate)
				.setClient_ID()
				.setOnlyActiveRecords(true)
				.list();

		if (processInstanceList == null || processInstanceList.isEmpty())
		{
			return false;
		}else {

			if(processInstanceList.size() > 1)//Size == 1 is itself because Pinstance was saved already.
			{
				return true;
			}
		}

		return false;

	}

	/**
	 *
	 * Get Last Server Reboot Date
	 *
	 * @return
	 */
	private Timestamp getLastServerRebootDate()
	{
		MSession currentSession = MSession.get(Env.getCtx(), false);
		if (currentSession == null)
			return null;

		MSession lastServerSession = new Query(Env.getCtx(), MSession.Table_Name, " serverName=? AND websession=?", null)
				.setParameters(currentSession.getServerName(), "Server")
				.setOrderBy("AD_Session_ID desc")
				.setOnlyActiveRecords(true)
				.first();

		return lastServerSession.getCreated();
	}
}
