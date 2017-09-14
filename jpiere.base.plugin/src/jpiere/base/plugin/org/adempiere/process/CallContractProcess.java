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
import java.util.ArrayList;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.adempiere.util.ProcessUtil;
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
	private int p_JP_ContractCategory_ID = 0;
	private int p_C_DocType_ID = 0;
	private String p_DocBaseType = null;
	boolean p_IsCreateBaseDocJP = false;
	protected boolean p_IsRecordCommitJP = false;
	
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
			}else if (name.equals("JP_ContractCategory_ID")){
				p_JP_ContractCategory_ID = para[i].getParameterAsInt();
			}else if (name.equals("C_DocType_ID")){
				p_C_DocType_ID = para[i].getParameterAsInt();
				p_JP_ContractCategory_ID = para[i].getParameterAsInt();
			}else if (name.equals("DocBaseType")){
				p_DocBaseType = para[i].getParameterAsString();
			}else if (name.equals("IsCreateBaseDocJP")){
				p_IsCreateBaseDocJP = para[i].getParameterAsBoolean();
			}else if (name.equals("IsRecordCommitJP")){
				p_IsRecordCommitJP = para[i].getParameterAsBoolean();	
			}else{
//				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}//if

		}//fo
		
	}
	
	@Override
	protected String doIt() throws Exception 
	{
		if(p_JP_ContractProcessUnit == null)
		{
			throw new Exception(Msg.getMsg(getCtx(), "FillMandatory") + Msg.getElement(getCtx(), "JP_ContractProcessUnit"));
		}
		
		//Process is kicked from the window by Per Contract Content.
		if(p_JP_ContractProcessUnit.equals(AbstractContractProcess.JP_ContractProcessUnit_PerContractContent))
		{
			int Record_ID = getRecord_ID();
			if(Record_ID > 0)
			{
				MContractContent contractContent = new MContractContent(getCtx(),Record_ID, get_TrxName());
				if(contractContent.getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_SpotContract))
				{
					callCreateBaseDoc(contractContent, null);
					
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
						callCreateBaseDoc(contractContent, period);
						
					//Create Derivative Doc Contract process	
					}else{
						
						callCreateDerivativeDoc(contractContent, period);
						
					}
				}
				
			}else{
				log.log(Level.SEVERE, "Record_ID <= 0 ");//TODO エラー処理
			}
		
		//Process is kicked from periodical contract process.
		}else{
			
			//Get Contract Process Period
			ArrayList<MContractProcPeriod> contractProcPeriodList = getContractProcPeriodList();
			
			for(MContractProcPeriod procPeriod : contractProcPeriodList)
			{	
				//Get Contract Content from Contract Process Period
				ArrayList<MContractContent> contractContentList = getContractContentList(procPeriod);
				
				//Create Base Doc contract process
				if(p_IsCreateBaseDocJP)
				{
					
					for(MContractContent contractContent : contractContentList)
					{
						callCreateBaseDoc(contractContent, procPeriod);
					}
				
				//Create Derivative Doc Contract process
				}else{
					
					for(MContractContent contractContent : contractContentList)
					{
						callCreateDerivativeDoc(contractContent, procPeriod);
					}
						
				}

			}//for
			
		}//if
		
		return Msg.getMsg(getCtx(), "Success");//TODO リターンメッセージ ---> 処理契約期間数、処理契約内容件数、伝票作成件数、警告件数、エラー件数  くらいかな？
		
	}//doIt()
	
	
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
			
			String getProcPeriodSql = "SELECT * FROM JP_ContractProcPeriod WHERE AD_Client_ID = ? AND JP_ContractProcessValue = ? AND IsActive='Y' ";//1,2
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
			
			String getProcPeriodSql = "SELECT * FROM JP_ContractProcPeriod WHERE AD_Client_ID = ? AND JP_ContractProcPeriodG_ID = ? AND IsActive='Y' ";//1,2
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
												+ " WHERE c.AD_Client_ID = ? AND g.JP_ContractProcessValue = ? AND c.IsActive='Y' AND g.IsActive='Y' ";//1,2
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
	
	
	private ArrayList<MContractContent> getContractContentList(MContractProcPeriod procPeriod) throws Exception
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
				+ " AND c.DocStatus = 'CO' AND cc.JP_ContractProcStatus IN ('UN','IP')" //Contract Process Status in 'Unprocessed' and 'In Progress'
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
					+ " AND c.DocStatus = 'CO' AND cc.JP_ContractProcStatus IN ('UN','IP')" //Contract Process Status in 'Unprocessed' and 'In Progress'
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
					+ " AND c.DocStatus = 'CO' AND cc.JP_ContractProcStatus IN ('UN','IP')" //Contract Process Status in 'Unprocessed' and 'In Progress'
					+ " AND cl.JP_ContractCalender_Inv_ID = ?" //2
					+ " AND cc.JP_ContractProcDate_From <=? AND (cc.JP_ContractProcDate_To is null or cc.JP_ContractProcDate_To >=?)"//3,4
					+ " AND cc.JP_CreateDerivativeDocPolicy IN ('BT','IV') ");	
		}
		
		if(p_AD_Org_ID > 0)
			getContractContentSQL.append(" AND cc.AD_Org_ID = ? ");
		if(p_JP_ContractCategory_ID > 0)
			getContractContentSQL.append(" AND c.JP_ContractCategory_ID = ?");		
		if(p_C_DocType_ID > 0)
			getContractContentSQL.append(" AND cc.C_DocType_ID = ?");
		
		if(p_IsCreateBaseDocJP)
		{
			getContractContentSQL.append(" AND cc.DocBaseType = ?");
			
		}else{
			
			if(p_DocBaseType.equals("MMS") ||p_DocBaseType.equals("ARI") )
				getContractContentSQL.append(" AND cc.DocBaseType ='SOO'");
			else if(p_DocBaseType.equals("MMR") ||p_DocBaseType.equals("API") )
				getContractContentSQL.append(" AND cc.DocBaseType ='POO'");
		}
		
		ArrayList<MContractContent> contractContentList = new ArrayList<MContractContent>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (getContractContentSQL.toString(), null);
			int i = 1;
			pstmt.setInt (i++, getAD_Client_ID());	//1
			pstmt.setInt(i++, procPeriod.getJP_ContractCalender_ID());	//2
			pstmt.setTimestamp(i++, procPeriod.getStartDate());	//3
			pstmt.setTimestamp(i++, procPeriod.getEndDate());	//4
			if(p_AD_Org_ID > 0)
				pstmt.setInt (i++, p_AD_Org_ID);			
			if(p_JP_ContractCategory_ID > 0)
				pstmt.setInt (i++, p_JP_ContractCategory_ID);	
			if(p_C_DocType_ID > 0)
				pstmt.setInt (i++, p_C_DocType_ID);		
			if(p_IsCreateBaseDocJP)
				pstmt.setString (i++, p_DocBaseType);	
			
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
	
	
	private void callCreateBaseDoc(MContractContent contractContent, MContractProcPeriod procPeriod)
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
		list.add (new ProcessInfoParameter("JP_ContractProcess_ID", contractContent.getJP_ContractProcess(), null, null, null ));
		setProcessInfoParameter(pi, list, procPeriod);
		
		startProcess(pi);
		
	}
	
	
	private void callCreateDerivativeDoc(MContractContent contractContent, MContractProcPeriod procPeriod)
	{
		MContractProcess[] contractProcesses =  null;

		if(p_DocBaseType.equals("MMS")|| p_DocBaseType.equals("MMR"))
		{
			contractProcesses =  contractContent.getContractProcessDerivativeInOutByCalender(procPeriod.getJP_ContractCalender_ID()); 
			
		}else if(p_DocBaseType.equals("ARI")|| p_DocBaseType.equals("API")){
			
			contractProcesses =  contractContent.getContractProcessDerivativeInvoiceByCalender(procPeriod.getJP_ContractCalender_ID());
		}
		
		
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
			setProcessInfoParameter(pi, list, procPeriod);
			
			startProcess(pi);
			
		}//for
		
	}
	
	private void setProcessInfoParameter(ProcessInfo pi, ArrayList<ProcessInfoParameter> list ,MContractProcPeriod procPeriod)
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
	
	private void startProcess(ProcessInfo pi)
	{
		boolean isOK = ProcessUtil.startJavaProcess(getCtx(), pi, Trx.get(get_TrxName(), true), false, Env.getProcessUI(getCtx()));
		if(isOK)
		{				
			if(p_IsRecordCommitJP)
			{
				try 
				{
					commitEx();
				} catch (SQLException e) {
					
					e.printStackTrace();// TODO ログに書き込む!?
				}
			}
			
		}else{
			
			if(!p_IsRecordCommitJP)
			{
				throw new AdempiereException(pi.getSummary());
			}
		}
		
	}
}
