package jpiere.base.plugin.org.adempiere.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.adempiere.base.IModelFactory;
import org.adempiere.base.Service;
import org.adempiere.model.GenericPO;
import org.adempiere.util.IProcessUI;
import org.adempiere.util.ProcessUtil;
import org.compiere.model.MFactAcct;
import org.compiere.model.MTable;
import org.compiere.model.PO;
import org.compiere.process.ProcessInfo;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Trx;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.X_I_GLJournalJP;

/**
 * 	JPIERE-0411:Create Reversing Entry
 *
 *  @author Hideaki Hagiwara
 *
 */
public class CreateReversingEntry extends SvrProcess {

	private int p_C_AcctSchema_ID = 0;

	private int p_AD_Org_ID = 0;

	private Timestamp p_DateAcct_From = null;

	private Timestamp p_DateAcct_To = null;

	private int p_AD_Table_ID = 0;

	private int p_Record_ID = 0;

	private int p_C_DocType_ID = 0;

	private String p_DocAction = null;

	private IProcessUI 	processMonitor = null;

	@Override
	protected void prepare()
	{

		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null){
				;
			}else if (name.equals("C_AcctSchema_ID")){
				p_C_AcctSchema_ID = para[i].getParameterAsInt();
			}else if (name.equals("AD_Org_ID")) {
				p_AD_Org_ID  = para[i].getParameterAsInt();
			}else if (name.equals("DateAcct")) {
				p_DateAcct_From = (Timestamp)para[i].getParameter();
				p_DateAcct_To = (Timestamp)para[i].getParameter_To();
			}else if (name.equals("AD_Table_ID")) {
				p_AD_Table_ID = para[i].getParameterAsInt();
			}else if (name.equals("Record_ID")) {
				p_Record_ID = para[i].getParameterAsInt();
			}else if (name.equals("C_DocType_ID")) {
				p_C_DocType_ID = para[i].getParameterAsInt();
			}else if (name.equals("DocAction")){
				p_DocAction = para[i].getParameterAsString();
			}else{
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}//if

		}//for

		processMonitor = Env.getProcessUI(getCtx());

	}

	@Override
	protected String doIt() throws Exception
	{
		//Delete I_GLJournalJP
		String deleteSQL = "DELETE I_GLJournalJP WHERE AD_Client_ID=?";
		int deleteNum = DB.executeUpdate(deleteSQL, getAD_Client_ID(), get_TrxName());
		commitEx();

		//Get Target data of Fact_Acct Table.
		MFactAcct[] targetFact = getTargetReversingEntry();

		//Create Reversing Entory in I_GLJournalJP Table
		String JP_DataMigration_Identifier = null;
		String preJP_DataMigration_Identifier = null;
		String preDocumentNo = null;
		StringBuffer sql = new StringBuffer("SELECT JP_DataMigration_Identifier FROM GL_Journal WHERE AD_Client_ID=? AND C_AcctSchema_ID=? AND JP_DataMigration_Identifier=?");
		boolean isSkip = false;

		int recordsNum = targetFact.length;
		int skipNum = 0;
		int errorNum = 0;
		int successNum = 0;
		String records = Msg.getMsg(getCtx(), "JP_NumberOfRecords");
		String skipRecords = Msg.getMsg(getCtx(), "JP_NumberOfSkipRecords");
		String errorRecords = Msg.getMsg(getCtx(), "JP_NumberOfUnexpectedErrorRecords");
		String success = Msg.getMsg(getCtx(), "JP_Success");

		X_I_GLJournalJP impJournal = null;

		for(int i = 0; i < targetFact.length; i++)
		{
			impJournal = null;
			JP_DataMigration_Identifier = "RE-"+targetFact[i].getAD_Table_ID() + "-" + targetFact[i].getRecord_ID();
			if(JP_DataMigration_Identifier.equals(preJP_DataMigration_Identifier))
			{
				if(isSkip)
				{
					skipNum++;
					continue;//Created Reversing Entory already.
				}else {

					impJournal = new X_I_GLJournalJP(getCtx(),0,get_TrxName());
					impJournal.setDocumentNo(preDocumentNo);
				}

			}else{

				PreparedStatement pstmt = null;
				ResultSet rs = null;
				try
				{
					pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
					pstmt.setInt(1, getAD_Client_ID());
					pstmt.setInt(2, p_C_AcctSchema_ID);
					pstmt.setString(3, JP_DataMigration_Identifier);
					rs = pstmt.executeQuery();
					if (rs.next())
						isSkip = true;//Created Reversing Entory already.
					else
						isSkip = false;//Create Reversing Entory

				}
				catch (Exception e)
				{
					log.log(Level.SEVERE, sql.toString(), e);
				}
				finally
				{
					DB.close(rs, pstmt);
					rs = null;
					pstmt = null;
				}

				if (processMonitor != null)
				{
					processMonitor.statusUpdate(
						records + " : " + recordsNum + " = "
						+ skipRecords + " : " + skipNum + " + "
						+ errorRecords + " : " + errorNum + " + "
						+ success + " : " + successNum
						);
				}
			}


			if(!isSkip)
			{
				if(impJournal == null)
					impJournal = new X_I_GLJournalJP(getCtx(),0,get_TrxName());

				if(createReversingEntory(targetFact[i], impJournal, JP_DataMigration_Identifier))
				{
					successNum++;
				}else {
					addLog(Msg.getMsg(getCtx(), "Error") + " -> " + JP_DataMigration_Identifier);
					errorNum++;
				}

			}else {
				skipNum++;
			}

			preJP_DataMigration_Identifier = JP_DataMigration_Identifier;
			if(impJournal!=null)
				preDocumentNo = impJournal.getDocumentNo();

		}//for

		commitEx();

		addLog(records + " : " + recordsNum + " = "
				+ skipRecords + " : " + skipNum + " + "
				+ errorRecords + " : " + errorNum + " + "
				+ success + " : " + successNum
				);

		//Import GL Jouranl from I_GLJournalJP
		if(!Util.isEmpty(p_DocAction))
		{
			ProcessInfo pi = new ProcessInfo("CreateBaseDoc", 0);
			String className =  "jpiere.base.plugin.org.adempiere.process.JPiereImportGLJournal";
			pi.setClassName(className);
			pi.setAD_Client_ID(getAD_Client_ID());
			pi.setAD_User_ID(getAD_User_ID());
			pi.setAD_PInstance_ID(getAD_PInstance_ID());
			pi.setRecord_ID(0);

			//Update ProcessInfoParameter
			ArrayList<ProcessInfoParameter> list = new ArrayList<ProcessInfoParameter>();
			list.add (new ProcessInfoParameter("DeleteOldImported", false, null, null, null ));
			list.add (new ProcessInfoParameter("IsValidateOnly", false, null, null, null ));
			list.add (new ProcessInfoParameter("DocAction", p_DocAction, null, null, null ));
			list.add (new ProcessInfoParameter("JP_CollateGLJournalPolicy", JPiereImportGLJournal.JP_CollateGLJournalPolicy_DataMigrationIdentifier, null, null, null ));
			list.add (new ProcessInfoParameter("JP_ReimportPolicy", JPiereImportGLJournal.JP_ReimportPolicy_NotImport, null, null, null ));
			list.add (new ProcessInfoParameter("IsReleaseDocControlledJP", true, null, null, null ));

			ProcessInfoParameter[] pars = new ProcessInfoParameter[list.size()];
			list.toArray(pars);
			pi.setParameter(pars);

			if(!ProcessUtil.startJavaProcess(getCtx(), pi, Trx.get(get_TrxName(), true), false, processUI))
				return Msg.getMsg(getCtx(), "ProcessRunError");
		}

		return Msg.getMsg(getCtx(), "ProcessOK");
	}

	private MFactAcct[] getTargetReversingEntry()
	{
		ArrayList<MFactAcct> list = new ArrayList<MFactAcct>();
		StringBuffer sql = new StringBuffer("SELECT * FROM FACT_ACCT WHERE DateAcct >= ? AND DateAcct <=? AND AD_Client_ID=? AND C_AcctSchema_ID=? AND PostingType='A'");//1 ... 4
		if(p_AD_Org_ID > 0)
		{
			sql.append(" AND AD_Org_ID=?");
		}

		if(p_AD_Table_ID > 0)
		{
			sql.append(" AND AD_Table_ID=?");
		}else {
			sql.append(" AND AD_Table_ID<>224");//224 = GL_Journal -> To except GL_Journal from reversing entory is better at data migration generally.
		}

		if(p_AD_Table_ID > 0 && p_Record_ID > 0)
		{
			sql.append(" AND Record_ID=?");
		}

		sql.append(" ORDER BY DateAcct, AD_Table_ID, Record_ID, Fact_Acct_ID");

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
			pstmt.setTimestamp(1, p_DateAcct_From);
			pstmt.setTimestamp(2, p_DateAcct_To);
			pstmt.setInt(3, getAD_Client_ID());
			pstmt.setInt(4, p_C_AcctSchema_ID);

			int i = 4;
			if(p_AD_Org_ID > 0)
			{
				i++;
				pstmt.setInt(i, p_AD_Org_ID);
			}

			if(p_AD_Table_ID > 0)
			{
				i++;
				pstmt.setInt(i, p_AD_Table_ID);
			}

			if(p_AD_Table_ID > 0 && p_Record_ID > 0)
			{
				i++;
				pstmt.setInt(i, p_Record_ID);
			}

			rs = pstmt.executeQuery();
			while (rs.next())
				list.add(new MFactAcct (getCtx(), rs, get_TrxName()));
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql.toString(), e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

		MFactAcct[] m_accounts = new MFactAcct[list.size()];
		list.toArray(m_accounts);
		return m_accounts;

	}

	private boolean createReversingEntory(MFactAcct targetFact, X_I_GLJournalJP impJournal, String JP_DataMigration_Identifier)
	{
		PO.copyValues(targetFact, impJournal);
		impJournal.setAD_Org_ID(targetFact.getAD_Org_ID());
		impJournal.setJP_DataMigration_Identifier(JP_DataMigration_Identifier);
		impJournal.setLine(targetFact.getFact_Acct_ID());
		impJournal.setJP_ConversionType_Value("S");
		impJournal.setC_DocType_ID(p_C_DocType_ID);
		impJournal.setJP_Description_Header(targetFact.getDescription());//TODO セットする情報を要検討
		impJournal.setJP_Description_Line(targetFact.getDescription());
		impJournal.setAmtSourceDr(impJournal.getAmtSourceDr().negate());
		impJournal.setAmtSourceCr(impJournal.getAmtSourceCr().negate());
		impJournal.setAmtAcctDr(impJournal.getAmtAcctDr().negate());
		impJournal.setAmtAcctCr(impJournal.getAmtAcctCr().negate());

		if(Util.isEmpty(impJournal.getDocumentNo()))
			impJournal.setDocumentNo(getDocumentNo(targetFact,JP_DataMigration_Identifier));

		impJournal.saveEx(get_TrxName());

		return true;
	}

	private String getDocumentNo(MFactAcct targetFact, String JP_DataMigration_Identifier)
	{
		MTable table = MTable.get(getCtx(), targetFact.getAD_Table_ID());
		String tableName =table.getTableName();

		PO po = null;
		List<IModelFactory> factoryList = Service.locator().list(IModelFactory.class).getServices();
		if (factoryList != null)
		{
			for(IModelFactory factory : factoryList)
			{
				po = factory.getPO(tableName, targetFact.getRecord_ID(), get_TrxName());
				if (po != null)
				{
					if (po.get_ID() != targetFact.getRecord_ID() && targetFact.getRecord_ID() > 0)
						po = null;
					else
						break;
				}
			}
		}

		if (po == null)
		{
			po = new GenericPO(tableName, getCtx(), targetFact.getRecord_ID(), get_TrxName());
			if (po.get_ID() != targetFact.getRecord_ID() && targetFact.getRecord_ID() > 0)
				po = null;
		}

		if (po == null)
		{
			return null;
		}else {

			if(po.get_ColumnIndex("DocumentNo") >= 0)
			{
				return po.get_ValueAsString("DocumentNo");

			}else if(po.get_ColumnIndex("Name") >= 0) {

				return Msg.getElement(getCtx(), "Record_ID")+ ":" + targetFact.getRecord_ID() +  "-" + po.get_ValueAsString("Name");

			}

		}

		return JP_DataMigration_Identifier;
	}

}
