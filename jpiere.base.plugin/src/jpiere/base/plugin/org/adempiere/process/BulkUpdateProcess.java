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

import java.util.List;
import java.util.logging.Level;

import org.adempiere.util.Callback;
import org.adempiere.util.IProcessUI;
import org.compiere.db.CConnection;
import org.compiere.model.MChangeLog;
import org.compiere.model.MClient;
import org.compiere.model.MColumn;
import org.compiere.model.MProcessPara;
import org.compiere.model.MRefList;
import org.compiere.model.MRole;
import org.compiere.model.MSession;
import org.compiere.model.MTable;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.MBulkUpdateProfile;
import jpiere.base.plugin.org.adempiere.model.MBulkUpdateProfileAccess;
import jpiere.base.plugin.org.adempiere.model.MBulkUpdateProfileLine;

/**
 * JPIERE-0621 Bulk Update Process
 *
 *  @author Hideaki Hagiwara
 *
 */
public class BulkUpdateProcess extends SvrProcess {

	private int p_JP_BulkUpdateProfile_ID = 0;
	private MBulkUpdateProfile m_BulkUpdateProfile = null;
	private MBulkUpdateProfileLine[] lines = null;
	
	private boolean isOpenDialog = false;
	private boolean isProcessingBulkUpdate = true;
	
	private String 			returnMsg = "";
	
	private IProcessUI 		processUI = null;
	private MSession session = null;
	
	@Override
	protected void prepare() 
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
			else if (name.equals(MBulkUpdateProfile.COLUMNNAME_JP_BulkUpdateProfile_ID))
				p_JP_BulkUpdateProfile_ID = para[i].getParameterAsInt();
			else
				MProcessPara.validateUnknownParameter(getProcessInfo().getAD_Process_ID(), para[i]);
		}

		processUI = Env.getProcessUI(getCtx());
		session = MSession.get (getCtx());
	}

	@Override
	protected String doIt() throws Exception 
	{
		
		//Mandatory check
		if(p_JP_BulkUpdateProfile_ID == 0)
			p_JP_BulkUpdateProfile_ID = getRecord_ID();
		
		if(p_JP_BulkUpdateProfile_ID == 0)
			throw new Exception(Msg.getMsg(getCtx(), "FillMandatory") + Msg.getElement(getCtx(), MBulkUpdateProfile.COLUMNNAME_JP_BulkUpdateProfile_ID));
		
		m_BulkUpdateProfile = new MBulkUpdateProfile(getCtx(), p_JP_BulkUpdateProfile_ID, get_TrxName());
		
		
		//Role check
		MBulkUpdateProfileAccess[] m_AccessRoles = m_BulkUpdateProfile.getAccessRoles();
		MRole m_LoginRole = MRole.getDefault();
		boolean isRoleOK = false;
		for(MBulkUpdateProfileAccess m_AccessRole : m_AccessRoles)
		{
			if(m_LoginRole.getAD_Role_ID() == m_AccessRole.getAD_Role_ID())
			{
				isRoleOK = true;
				break;
			}
		}
		
		if(!isRoleOK)
			throw new Exception(Msg.getMsg(getCtx(), "AccessTableNoUpdate", false));//You don't have the privileges
		
		
		//Confirm of Execution
		if(processUI != null && !MBulkUpdateProfile.JP_CONFIRMOFEXECUTION_Nothing.equals(m_BulkUpdateProfile.getJP_ConfirmOfExecution()))
		{
			isOpenDialog = true;
			isProcessingBulkUpdate = true;
			
			MColumn m_Column = MColumn.get(getCtx(), MBulkUpdateProfile.Table_Name,  MBulkUpdateProfile.COLUMNNAME_JP_ConfirmOfExecution);
			String host = Msg.getElement(getCtx(), "Host") + " = " + getServerInfo();
			String db =  Msg.getElement(getCtx(), "DatabaseInfo") + " = " + getDatabaseInfo();
			String tenant = Msg.getElement(getCtx(), "AD_Client_ID") + " = " + MClient.get(getAD_Client_ID()).getName();
			String value = Msg.getElement(getCtx(), "Value") + " = " +  m_BulkUpdateProfile.getValue();
			String name = Msg.getElement(getCtx(), "Name") + " = " +  m_BulkUpdateProfile.get_Translation("Name");
			String msg = "["+Msg.getMsg(getCtx(), "JP_ToBeConfirmed")+"]"
								+ System.lineSeparator() + host
								+ System.lineSeparator() + db
								+ System.lineSeparator() + tenant
								+ System.lineSeparator() + value 
								+ System.lineSeparator() + name
								+ System.lineSeparator() 
								+ System.lineSeparator() + Msg.getElement(getCtx(), MBulkUpdateProfile.COLUMNNAME_JP_ConfirmOfExecution) 
														   + " : " + MRefList.getListName(getCtx(),m_Column.getAD_Reference_Value_ID() , m_BulkUpdateProfile.getJP_ConfirmOfExecution());
			
			if(MBulkUpdateProfile.JP_CONFIRMOFEXECUTION_Confirm.equals(m_BulkUpdateProfile.getJP_ConfirmOfExecution()))
			{
				processUI.ask(msg, new Callback<Boolean>() {
	
					@Override
					public void onCallback(Boolean result)
					{
						if (result)
						{
							try {
								returnMsg = bulkUpdate();
							}catch (Exception e) {
								returnMsg = e.getMessage();
							}finally {
								isProcessingBulkUpdate = false;
							}
	
						}else{
							isProcessingBulkUpdate = false;
							returnMsg = Msg.getMsg(getCtx(), "ProcessCancelled");
						}
			        }
				});//Dialog.
			
			}else {
				
				processUI.askForInput(msg, new Callback<String>() {
					
					@Override
					public void onCallback(String result)
					{
						if (!Util.isEmpty(result))
						{
							boolean isOK = false;
							if(MBulkUpdateProfile.JP_CONFIRMOFEXECUTION_Host.equals(m_BulkUpdateProfile.getJP_ConfirmOfExecution()))
							{
								if(result.equals(getServerInfo()))
									isOK = true;
								
							}else if(MBulkUpdateProfile.JP_CONFIRMOFEXECUTION_DataBase.equals(m_BulkUpdateProfile.getJP_ConfirmOfExecution())) {
								
								if(result.equals(getDatabaseInfo()))
									isOK = true;
								
							}else if(MBulkUpdateProfile.JP_CONFIRMOFEXECUTION_SearchKey.equals(m_BulkUpdateProfile.getJP_ConfirmOfExecution())) {
								
								if(result.equals(m_BulkUpdateProfile.getValue()))
									isOK = true;
								
							}
							
							if(isOK)
							{
								try {
									returnMsg = bulkUpdate();
								}catch (Exception e) {
									returnMsg = e.getMessage();
								}finally {
									isProcessingBulkUpdate = false;
								}
								
							}else {
								
								returnMsg = Msg.getMsg(getCtx(), "ParameterMissing") + " - " + Msg.getMsg(getCtx(), "JP_Process_Cannot_Perform");
							}
	
							isProcessingBulkUpdate = false;
							
						}else{
							isProcessingBulkUpdate = false;
							returnMsg = Msg.getMsg(getCtx(), "ParameterMissing") + " - " + Msg.getMsg(getCtx(), "JP_Process_Cannot_Perform");
						}
			        }
				});//Dialog.
				
			}

		}else{
			
			returnMsg = bulkUpdate();
			
			isOpenDialog = false;
			isProcessingBulkUpdate = false;

		}//Confirm of Execution
		
		//Keep the thread.
		while (isOpenDialog && isProcessingBulkUpdate)
		{
			Thread.sleep(1000*2);
		}

		if(!Util.isEmpty(returnMsg))
		{
			throw new Exception(returnMsg);
		}
		
		return Msg.getElement(getCtx(), MBulkUpdateProfileLine.COLUMNNAME_JP_BulkUpdateProfileLine_ID) + " : " + lines.length ;
	}
	
	private static String getServerInfo()
	{
		StringBuilder sb = new StringBuilder();
		CConnection cc = CConnection.get();
		//  Host
		sb.append(cc.getAppsHost());
		
		//
		return sb.toString();
	}   //  getServerInfo
	
	private static String getDatabaseInfo()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(CConnection.get().getDbHost()).append(":")
			.append(CConnection.get().getDbPort()).append("/")
			.append(CConnection.get().getDbName());
		
//		AdempiereDatabase db = DB.getDatabase();
//		sb.append(" (").append(db.getName());
		
		return sb.toString();
	}   //  getDatabaseInfo

	private String bulkUpdate() throws Exception
	{
		lines =  m_BulkUpdateProfile.getLines();
		if(lines.length == 0)
			return "No Lines";
		
		String msg = null;
		for(MBulkUpdateProfileLine line : lines)
		{
			if(MBulkUpdateProfileLine.JP_BULKUPDATETYPE_SQLUPDATE.equals(line.getJP_BulkUpdateType()))
			{
				msg = sqlUpdate(line);
				
			}else if(MBulkUpdateProfileLine.JP_BULKUPDATETYPE_ReplaceString.equals(line.getJP_BulkUpdateType())) {
				
				msg = replaceString(line);
				
			}else if(MBulkUpdateProfileLine.JP_BULKUPDATETYPE_ReplaceStringRegex.equals(line.getJP_BulkUpdateType())) {
				
				msg = replaceStringRegex(line);
				
			}else if(MBulkUpdateProfileLine.JP_BULKUPDATETYPE_MaskingExcludeFirstSomeCharacters.equals(line.getJP_BulkUpdateType())) {
				
				msg = maskingSomeCharacters(line);
				
			}else if(MBulkUpdateProfileLine.JP_BULKUPDATETYPE_MaskingExcludeLastSomeCharacters.equals(line.getJP_BulkUpdateType())) {
				
				msg = maskingSomeCharacters(line);
				
			}else if(MBulkUpdateProfileLine.JP_BULKUPDATETYPE_MaskingExcludeFirstLastSomeCharacters.equals(line.getJP_BulkUpdateType())) {
				
				msg = maskingSomeCharacters(line);
			}
			
			if(MBulkUpdateProfileLine.JP_BULKUPDATECOMMITTYPE_Line.equals(line.getJP_BulkUpdateCommitType())) 
				commitEx();
			
			if(!Util.isEmpty(msg))
				break;
		}
		
		return msg;
	}
	
	private String sqlUpdate(MBulkUpdateProfileLine line)
	{
		String returnMsg = null;
		StringBuilder sql = null;
		MTable m_Table = null;
		
		try 
		{
			m_Table = MTable.get(line.getAD_Table_ID());
			sql = new StringBuilder("UPDATE ").append(m_Table.getTableName())
									.append(" SET ").append(line.getJP_UpdateSetClause())
									.append(" WHERE ").append(" AD_Client_ID=").append(getAD_Client_ID())
									.append(Util.isEmpty(line.getWhereClause()) ? "" : " AND " + line.getWhereClause());
			
			
			if(processUI != null)
				processUI.statusUpdate(m_Table.getTableName() + "  " + sql.toString());
			
			int updated = DB.executeUpdateEx(sql.toString(), get_TrxName());
			
			if(MBulkUpdateProfileLine.JP_BULKUPDATECOMMITTYPE_Record.equals(line.getJP_BulkUpdateCommitType())) 
				commitEx();
			
			addLog("["+Msg.getElement(getCtx(), "Line") + "] " + line.getLine()
					+ " / ["+ Msg.getElement(getCtx(), "AD_Table_ID") + "] " + m_Table.getTableName() 
							+ " / [" +  Msg.getMsg(getCtx(), "JP_UpdatedRecords") + "] " + updated 
							+ " / [SQL] " + sql.toString());
			
		}catch (Exception e) {
			
			returnMsg = "["+Msg.getElement(getCtx(), "Line") + "] " + line.getLine()
							+ " / ["+ Msg.getElement(getCtx(), "AD_Table_ID") + "] " + m_Table.getTableName() 
							+ " / ["+ Msg.getMsg(getCtx(), "Error") + "] " + e.toString()
							+ " / [SQL] " + sql.toString();
			return returnMsg;
		}
		
		return returnMsg;
	}
	
	private String replaceString(MBulkUpdateProfileLine line) 
	{
		String returnMsg = null;
		
		MTable m_Table = MTable.get(line.getAD_Table_ID());
		MColumn m_Column = MColumn.get(line.getAD_Column_ID());
		String columnName = m_Column.getColumnName();
		
		boolean isLoging = judgementLoging(line, m_Table, m_Column);
		
		PO[] m_POs = getPOs(line);
		
		String oldValue = null;
		String newValue = null;
		PO po_ErrorLog = null;
		int counter = 0;
		int updated = 0;
		try 
		{
			for(PO m_PO : m_POs)
			{
				counter++;
				if(processUI != null)
					processUI.statusUpdate(m_Table.getTableName() + " - " + columnName + "  " + counter + " / " +m_POs.length);
				
				po_ErrorLog = m_PO;
				oldValue = m_PO.get_ValueAsString(columnName);
				newValue = line.getJP_ReplacementString();
				
				if(Util.isEmpty(line.getJP_TargetString()))
				{
					if(!m_PO.set_ValueNoCheck(columnName, newValue))
						throw new Exception(Msg.getMsg(getCtx(), "JP_CouldNotSetNewValue") + " : " + newValue);//Could not set new value
					
				}else {

					if(Util.isEmpty(oldValue))
					{
						continue;
						
					}else {
						
						newValue = oldValue.replace(line.getJP_TargetString(), line.getJP_ReplacementString());
						if(!m_PO.set_ValueNoCheck(columnName, newValue))
							throw new Exception(Msg.getMsg(getCtx(), "JP_CouldNotSetNewValue") + " : " + newValue);//Could not set new value
					}
				}
				
				if(oldValue == null && newValue == null)
				{
					continue;
					
				}else if(oldValue != null){
					
					if(oldValue.equals(newValue))
						continue;
					
				}else if(newValue != null){
					
					if(newValue.equals(oldValue))
						continue;
					
				}else {
					
					;//Noting to do
					
				}
				
				m_PO.saveEx(get_TrxName());
				updated++;
				
				if(isLoging)
					registerChangeLog (line, m_Table, m_Column, m_PO, oldValue, newValue);
				
				if(MBulkUpdateProfileLine.JP_BULKUPDATECOMMITTYPE_Record.equals(line.getJP_BulkUpdateCommitType())) 
					commitEx();
				
			}//for
		
			addLog("["+Msg.getElement(getCtx(), "Line") + "] " + line.getLine()
					+ " / ["+ Msg.getElement(getCtx(), "AD_Table_ID") + "] " + m_Table.getTableName() 
					+ " / ["+ Msg.getElement(getCtx(), "AD_Column_ID") + "] " + columnName 
					+ " / [" +  Msg.getMsg(getCtx(), "JP_UpdatedRecords") + "] " + updated 
					+ " / [" + Msg.getMsg(getCtx(), "JP_TargetRecords") + "] " + m_POs.length);
			
		}catch (Exception e) {
			
			returnMsg = "["+Msg.getElement(getCtx(), "Line") + "] " + line.getLine()
							+ " / ["+ Msg.getElement(getCtx(), "AD_Table_ID") + "] " + m_Table.getTableName() 
							+ " / ["+ Msg.getElement(getCtx(), "AD_Column_ID") + "] " + columnName 
							+ " / ["+ Msg.getMsg(getCtx(), "Error") + "] " + e.toString()
							+ " / ["+ Msg.getMsg(getCtx(), "Who") + "] " + po_ErrorLog.toString() ;
			return returnMsg;
			
		}finally {
			;
		}
		
		return returnMsg;
	}
	
	private String replaceStringRegex(MBulkUpdateProfileLine line)
	{
		String returnMsg = null;
		
		MTable m_Table = MTable.get(line.getAD_Table_ID());
		MColumn m_Column = MColumn.get(line.getAD_Column_ID());
		String columnName = m_Column.getColumnName();
		
		boolean isLoging = judgementLoging(line, m_Table, m_Column);
		
		PO[] m_POs = getPOs(line);
		
		String oldValue = null;
		String newValue = null;
		PO po_ErrorLog = null;
		int counter = 0;
		int updated = 0;
		try 
		{
			for(PO m_PO : m_POs)
			{
				counter++;
				if(processUI != null)
					processUI.statusUpdate(m_Table.getTableName() + " - " + columnName + "  " + counter + " / " +m_POs.length);
				
				po_ErrorLog = m_PO;
				oldValue = m_PO.get_ValueAsString(columnName);
				newValue = line.getJP_ReplacementString();
				
				if(Util.isEmpty(oldValue))
				{
					continue;
					
				}else {
					
					newValue = oldValue.replaceAll(line.getJP_TargetString(), Util.isEmpty(newValue)? "" : line.getJP_ReplacementString());
					if(!m_PO.set_ValueNoCheck(columnName, newValue))
						throw new Exception(Msg.getMsg(getCtx(), "JP_CouldNotSetNewValue") + " : " + newValue);//Could not set new value
				}
					
				if(newValue != null){
					
					if(newValue.equals(oldValue))
						continue;
					
				}else {
					
					;//Noting to do
					
				}
				
				m_PO.saveEx(get_TrxName());
				updated++;
				
				if(isLoging)
					registerChangeLog (line, m_Table, m_Column, m_PO, oldValue, newValue);
				
				if(MBulkUpdateProfileLine.JP_BULKUPDATECOMMITTYPE_Record.equals(line.getJP_BulkUpdateCommitType())) 
					commitEx();
				
			}//for
			
			addLog("["+Msg.getElement(getCtx(), "Line") + "] " + line.getLine()
						+ " / ["+ Msg.getElement(getCtx(), "AD_Table_ID") + "] " + m_Table.getTableName() 
						+ " / ["+ Msg.getElement(getCtx(), "AD_Column_ID") + "] " + columnName 
						+ " / [" +  Msg.getMsg(getCtx(), "JP_UpdatedRecords") + "] " + updated 
						+ " / [" + Msg.getMsg(getCtx(), "JP_TargetRecords") + "] " + m_POs.length);
		
		}catch (Exception e) {
			
			returnMsg = "["+Msg.getElement(getCtx(), "Line") + "] " + line.getLine()
							+ " / ["+ Msg.getElement(getCtx(), "AD_Table_ID") + "] " + m_Table.getTableName() 
							+ " / ["+ Msg.getElement(getCtx(), "AD_Column_ID") + "] " + columnName 
							+ " / ["+ Msg.getMsg(getCtx(), "Error") + "] " + e.toString()
							+ " / ["+ Msg.getMsg(getCtx(), "Who") + "] " + po_ErrorLog.toString() ;
			return returnMsg;
			
		}finally {
			;
		}
		
		return returnMsg;
	}
	
	private String maskingSomeCharacters(MBulkUpdateProfileLine line)
	{
		String returnMsg = null;
		
		MTable m_Table = MTable.get(line.getAD_Table_ID());
		MColumn m_Column = MColumn.get(line.getAD_Column_ID());
		String columnName = m_Column.getColumnName();
		
		boolean isLoging = judgementLoging(line, m_Table, m_Column);
		
		PO[] m_POs = getPOs(line);
		
		String oldValue = null;
		String newValue = null;
		PO po_ErrorLog = null;
		int counter = 0;
		int updated = 0;
		try 
		{
			for(PO m_PO : m_POs)
			{
				counter++;
				if(processUI != null)
					processUI.statusUpdate(m_Table.getTableName() + " - " + columnName + "  " + counter + " / " +m_POs.length);
				
				po_ErrorLog = m_PO;
				oldValue = m_PO.get_ValueAsString(columnName);
				if(Util.isEmpty(oldValue))
					continue;
				
				newValue = masking(oldValue, line);
				if(oldValue.equals(newValue))
					continue;
				
				if(!m_PO.set_ValueNoCheck(columnName, newValue))
					throw new Exception(Msg.getMsg(getCtx(), "JP_CouldNotSetNewValue") + " : " + newValue);//Could not set new value
				
				m_PO.saveEx(get_TrxName());
				updated++;
				
				if(isLoging)
					registerChangeLog (line, m_Table, m_Column, m_PO, oldValue, newValue);
				
				if(MBulkUpdateProfileLine.JP_BULKUPDATECOMMITTYPE_Record.equals(line.getJP_BulkUpdateCommitType())) 
					commitEx();
				
			}//for
			
			addLog("["+Msg.getElement(getCtx(), "Line") + "] " + line.getLine()
						+ " / ["+ Msg.getElement(getCtx(), "AD_Table_ID") + "] " + m_Table.getTableName() 
						+ " / ["+ Msg.getElement(getCtx(), "AD_Column_ID") + "] " + columnName 
						+ " / [" +  Msg.getMsg(getCtx(), "JP_UpdatedRecords") + "] " + updated 
						+ " / [" + Msg.getMsg(getCtx(), "JP_TargetRecords") + "] " + m_POs.length);
		
		}catch (Exception e) {
			
			returnMsg = "["+Msg.getElement(getCtx(), "Line") + "] " + line.getLine()
							+ " / ["+ Msg.getElement(getCtx(), "AD_Table_ID") + "] " + m_Table.getTableName() 
							+ " / ["+ Msg.getElement(getCtx(), "AD_Column_ID") + "] " + columnName 
							+ " / ["+ Msg.getMsg(getCtx(), "Error") + "] " + e.toString()
							+ " / ["+ Msg.getMsg(getCtx(), "Who") + "] " + po_ErrorLog.toString() ;
			return returnMsg;
			
		}finally {
			;
		}
		
		return returnMsg;
	}
	
	private PO[] getPOs(MBulkUpdateProfileLine line)
	{
		List<PO> list = new Query(getCtx(), line.getAD_Table().getTableName(), line.getWhereClause(), get_TrxName())
				.setClient_ID(true)
				.list();
		
		return list.toArray(new PO[list.size()]);
	}
	
	private boolean judgementLoging(MBulkUpdateProfileLine m_Line, MTable m_Table, MColumn m_Column)
	{
		//if("Password".equals(m_Column.getColumnName()))
		//	return false;
		
		MColumn et = m_Table.getColumn(MTable.COLUMNNAME_EntityType);
		if(et != null)
			return false;
		
		if(m_Line.isChangeLog())
		{
			if(m_Column.isAllowLogging())
			{
				if(m_Table.isChangeLog())
				{
					return false;
					
				}else {
					
					MRole m_Role = MRole.getDefault();
					if(m_Role.isChangeLog())
					{
						return false;
						
					}else {
					
						return true;
					}
				}
				
			}else {
				return true;
			}
			
		}else {
			
			return false;
		}
		
	}
	
	private MChangeLog registerChangeLog (MBulkUpdateProfileLine m_Line, MTable m_Table, MColumn m_Column, PO po, Object OldValue, Object NewValue)
	{
		boolean hasKeyColumnID = po.columnExists(m_Table.getTableName()+"_ID");
		MChangeLog cl = new MChangeLog(getCtx(), 
				0, get_TrxName(), session.getAD_Session_ID(),
				m_Column.getAD_Table_ID(), m_Column.getAD_Column_ID(), hasKeyColumnID? po.get_ID() : 0, po.get_UUID(), po.getAD_Client_ID(), po.getAD_Org_ID(),
				OldValue, NewValue, MChangeLog.EVENTCHANGELOG_Update);
		
		try
		{
			if (cl.saveCrossTenantSafe())
				return cl;
		
		}catch (Exception e){
			
			log.log(Level.SEVERE, "AD_ChangeLog_ID=" + cl.getAD_ChangeLog_ID()
				+ ", AD_Session_ID=" + session.getAD_Session_ID()
				+ ", AD_Table_ID=" + m_Column.getAD_Table_ID() + ", AD_Column_ID=" + m_Column.getAD_Column_ID(), e);
			return null;
		}
		log.log(Level.SEVERE, "AD_ChangeLog_ID=" + cl.getAD_ChangeLog_ID()
			+ ", AD_Session_ID=" + session.getAD_Session_ID()
			+ ", AD_Table_ID=" + m_Column.getAD_Table_ID() + ", AD_Column_ID=" + m_Column.getAD_Column_ID());
		return null;
	}
	
	private String masking(String oldValue, MBulkUpdateProfileLine m_Line)
	{
		String newValue = "";
		String JP_MaskingString = m_Line.getJP_MaskingString();
		String JP_MaskingType = m_Line.getJP_MaskingType();
		int JP_NumOfCharExcludeMasking = m_Line.getJP_NumOfCharExcludeMasking();
		int oldValueLength = oldValue.length();
		int maskedStringLength = 0;
		String maskedString = "";
		
		if(JP_NumOfCharExcludeMasking == 0) //All Masking
		{
			if(MBulkUpdateProfileLine.JP_MASKINGTYPE_MatchTheNumberOfCharactersToBeMasked.equals(JP_MaskingType))
			{
				for(int i = 0; oldValueLength > i; i++)
					newValue = newValue + JP_MaskingString;
				
			}else if(MBulkUpdateProfileLine.JP_MASKINGTYPE_MatchMaskingString.equals(JP_MaskingType)) {
				
				newValue = JP_MaskingString;
			}
		
		}else {
			
			if(MBulkUpdateProfileLine.JP_BULKUPDATETYPE_MaskingExcludeFirstSomeCharacters.equals(m_Line.getJP_BulkUpdateType()))
			{
				if(JP_NumOfCharExcludeMasking >= oldValueLength)//Do not masking
				{
					newValue = oldValue;
					
				}else{//masking
					
					maskedStringLength = oldValueLength - JP_NumOfCharExcludeMasking;
					if(MBulkUpdateProfileLine.JP_MASKINGTYPE_MatchTheNumberOfCharactersToBeMasked.equals(JP_MaskingType))
					{
						for(int i = 0; maskedStringLength > i; i++)
							maskedString = maskedString + JP_MaskingString;
						
					}else if(MBulkUpdateProfileLine.JP_MASKINGTYPE_MatchMaskingString.equals(JP_MaskingType)) {
						
						maskedString = JP_MaskingString;
					}
					
					newValue = oldValue.substring(0, JP_NumOfCharExcludeMasking);
					newValue = newValue + maskedString;
				}
				
			}else if(MBulkUpdateProfileLine.JP_BULKUPDATETYPE_MaskingExcludeLastSomeCharacters.equals(m_Line.getJP_BulkUpdateType())) {
				
				if(JP_NumOfCharExcludeMasking >= oldValueLength)//Do not masking
				{
					newValue = oldValue;
					
				}else{//masking
					
					maskedStringLength = oldValueLength - JP_NumOfCharExcludeMasking;
					if(MBulkUpdateProfileLine.JP_MASKINGTYPE_MatchTheNumberOfCharactersToBeMasked.equals(JP_MaskingType))
					{
						for(int i = 0; maskedStringLength > i; i++)
							maskedString = maskedString + JP_MaskingString;
						
					}else if(MBulkUpdateProfileLine.JP_MASKINGTYPE_MatchMaskingString.equals(JP_MaskingType)) {
						
						maskedString = JP_MaskingString;
					}
					
					newValue = oldValue.substring(maskedStringLength, oldValueLength);
					newValue = maskedString + newValue;	
				}
				
			}else if(MBulkUpdateProfileLine.JP_BULKUPDATETYPE_MaskingExcludeFirstLastSomeCharacters.equals(m_Line.getJP_BulkUpdateType())) {
					
				if((JP_NumOfCharExcludeMasking*2) >= oldValueLength)//Do not masking
				{
					newValue = oldValue;
					
				}else {//Masking
					
					String prefix = oldValue.substring(0, JP_NumOfCharExcludeMasking);
					maskedStringLength = oldValueLength - (JP_NumOfCharExcludeMasking*2);
					String suffix = oldValue.substring(prefix.length() + maskedStringLength, oldValueLength);
					if(MBulkUpdateProfileLine.JP_MASKINGTYPE_MatchTheNumberOfCharactersToBeMasked.equals(JP_MaskingType))
					{
						for(int i = 0; maskedStringLength > i; i++)
							maskedString = maskedString + JP_MaskingString;
						
					}else if(MBulkUpdateProfileLine.JP_MASKINGTYPE_MatchMaskingString.equals(JP_MaskingType)) {
						
						maskedString = JP_MaskingString;
					}
					
					newValue = prefix + maskedString + suffix;	
				
				}
				
			}
				
		}//if(JP_NumOfCharExcludeMasking == 0) 

		return newValue;
	}
}
