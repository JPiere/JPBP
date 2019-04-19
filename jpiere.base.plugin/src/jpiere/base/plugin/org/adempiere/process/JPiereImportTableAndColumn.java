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
import java.util.logging.Level;

import org.adempiere.model.ImportValidator;
import org.adempiere.process.ImportProcess;
import org.adempiere.util.IProcessUI;
import org.adempiere.util.ProcessUtil;
import org.compiere.model.MColumn;
import org.compiere.model.MEntityType;
import org.compiere.model.MLanguage;
import org.compiere.model.MTable;
import org.compiere.model.M_Element;
import org.compiere.model.ModelValidationEngine;
import org.compiere.process.ProcessInfo;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Trx;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.X_I_TableColumnJP;

/**
 * 	JPIERE-0438:Import Table And Column
 *
 *  @author Hideaki Hagiwara
 *
 */
public class JPiereImportTableAndColumn extends SvrProcess  implements ImportProcess
{
	/**	Client to be imported to		*/
	private int		 m_AD_Client_ID = 0;

	private boolean p_deleteOldImported = false;

	/**	Only validate, don't import		*/
	private boolean p_IsValidateOnly = false;

	private IProcessUI processMonitor = null;

	private String message = null;

	private static String BLANK = "blank";

	/**
	 *  Prepare - e.g., get Parameters.
	 */
	protected void prepare()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (name.equals("DeleteOldImported"))
				p_deleteOldImported = "Y".equals(para[i].getParameter());
			else if (name.equals("IsValidateOnly"))
				p_IsValidateOnly = para[i].getParameterAsBoolean();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}

		m_AD_Client_ID = getProcessInfo().getAD_Client_ID();

	}	//	prepare

	/**
	 * 	Process
	 *	@return info
	 *	@throws Exception
	 */
	protected String doIt() throws Exception
	{
		processMonitor = Env.getProcessUI(getCtx());

		StringBuilder sql = null;
		int no = 0;
		String clientCheck = getWhereClause();


		//Delete Old Imported data
		if (p_deleteOldImported)
		{
			sql = new StringBuilder ("DELETE I_TableColumnJP ")
				  .append("WHERE I_IsImported='Y'").append (clientCheck);
			no = DB.executeUpdate(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("Delete Old Impored =" + no);
		}

		//Reset Message
		sql = new StringBuilder ("UPDATE I_TableColumnJP ")
				.append("SET I_ErrorMsg='' ")
				.append(" WHERE I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(String.valueOf(no));
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + sql );
		}

		ModelValidationEngine.get().fireImportValidate(this, null, null, ImportValidator.TIMING_BEFORE_VALIDATE);

		//Reverse Lookup Surrogate Key
		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "AD_Table_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupAD_Table_ID())
			commitEx();
		else
			return message;


		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "AD_Window_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupAD_Window_ID())
			commitEx();
		else
			return message;

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "PO_Window_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupPO_Window_ID())
			commitEx();
		else
			return message;


		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "AD_Element_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupAD_Element_ID())
			commitEx();
		else
			return message;


		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "AD_Column_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupAD_Column_ID())
			commitEx();
		else
			return message;

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "AD_Reference_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupAD_Reference_ID())
			commitEx();
		else
			return message;


		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "AD_Reference_Value_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupAD_Reference_Value_ID())
			commitEx();
		else
			return message;

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "AD_Val_Rule_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupAD_Val_Rule_ID())
			commitEx();
		else
			return message;

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "AD_Process_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupAD_Process_ID())
			commitEx();
		else
			return message;


		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "AD_Chart_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupAD_Chart_ID())
			commitEx();
		else
			return message;

		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "PA_DashboardContent_ID");
		if(processMonitor != null)	processMonitor.statusUpdate(message);
		if(reverseLookupPA_DashboardContent_ID())
			commitEx();
		else
			return message;


		ModelValidationEngine.get().fireImportValidate(this, null, null, ImportValidator.TIMING_AFTER_VALIDATE);


		commitEx();
		if (p_IsValidateOnly)
		{
			return "Validated";
		}

		//
		sql = new StringBuilder ("SELECT * FROM I_TableColumnJP WHERE I_IsImported='N'")
					.append(clientCheck).append(" ORDER BY AD_Table_ID,TableName, AD_Column_ID, ColumnName ");
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int recordsNum = 0;
		int successNum = 0;
		int failureNum = 0;
		String records = Msg.getMsg(getCtx(), "JP_NumberOfRecords");
		String success = Msg.getMsg(getCtx(), "JP_Success");
		String failure = Msg.getMsg(getCtx(), "JP_Failure");

		try
		{
			pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
			rs = pstmt.executeQuery();
			String preTableName = null;
			int preAD_Table_ID = 0;

			X_I_TableColumnJP impData = null;

			while (rs.next())
			{
				impData = new X_I_TableColumnJP(getCtx (), rs, get_TrxName());
				recordsNum++;

				//Element
				if(Util.isEmpty(impData.getColumnName()) && impData.getAD_Element_ID() == 0)
				{
					impData.setI_ErrorMsg(Msg.getMsg(getCtx(),"FillMandatory") + " : "+ Msg.getElement(getCtx(), "AD_Element_ID"));
					impData.setI_IsImported(false);
					impData.setProcessed(false);
					impData.saveEx(get_TrxName());
					continue;

				}else if(!Util.isEmpty(impData.getColumnName()) && impData.getAD_Element_ID() == 0){

					if(!createElement(impData))
					{
						failureNum++;
						continue;
					}

				}else {

					updateElement(impData,null);

				}

				if(impData.getAD_Element_ID() == 0)
				{
					failureNum++;
					impData.setI_ErrorMsg(Msg.getMsg(getCtx(),"Error") + " : "+ Msg.getElement(getCtx(), "AD_Element_ID"));
					impData.setI_IsImported(false);
					impData.setProcessed(false);
					impData.saveEx(get_TrxName());
					continue;
				}

				//Table
				if(!Util.isEmpty(impData.getTableName()) && impData.getAD_Table_ID() == 0)
				{
					if(!impData.getTableName().equals(preTableName))
					{
						insertTable(impData);
						preTableName = impData.getTableName();
						preAD_Table_ID = impData.getAD_Table_ID();
					}else {
						impData.setAD_Table_ID(preAD_Table_ID);
					}

				}else if(impData.getAD_Table_ID() > 0){

					if(impData.getAD_Table_ID() != preAD_Table_ID)
					{
						updateTable(impData);
						preAD_Table_ID = impData.getAD_Table_ID();
					}
				}

				if(Util.isEmpty(impData.getTableName()) && impData.getAD_Table_ID() == 0)//Element Only
				{

					successNum++;
					impData.setI_IsImported(true);
					impData.setProcessed(true);
					impData.saveEx(get_TrxName());
					continue;

				}else if(impData.getAD_Table_ID()==0){

					failureNum++;
					impData.setI_ErrorMsg(Msg.getMsg(getCtx(),"Error") + " : "+ Msg.getElement(getCtx(), "AD_Table_ID"));
					impData.setI_IsImported(false);
					impData.setProcessed(false);
					impData.saveEx(get_TrxName());
					continue;
				}

				//Column
				if(createUpdateColumn(impData))
				{
					successNum++;
					impData.setI_IsImported(true);
					impData.setProcessed(true);

				}else {

					failureNum++;
					impData.setI_IsImported(false);
					impData.setProcessed(false);

				}

				impData.saveEx(get_TrxName());

				commitEx();

			}//while (rs.next())

		}catch (Exception e){
			log.log(Level.SEVERE, sql.toString(), e);
			throw e;
		}finally{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

		return records + recordsNum + "( "+  success + " : " + successNum + "  /  " +  failure + " : " + failureNum + " )";

	}	//	doIt


	//@Override
	public String getImportTableName()
	{
		return X_I_TableColumnJP.Table_Name;
	}

	@Override
	public String getWhereClause() {
		StringBuilder msgreturn = new StringBuilder(" AND AD_Client_ID=").append(m_AD_Client_ID);
		return msgreturn.toString();
	}

	private boolean createElement(X_I_TableColumnJP impData)
	{
		M_Element element = null;

		if(impData.getAD_Element_ID() == 0)
		{
			final String sql = "SELECT * FROM AD_Element WHERE ColumnName=? ";
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				pstmt = DB.prepareStatement(sql, get_TrxName());
				pstmt.setString(1, impData.getColumnName());
				rs = pstmt.executeQuery();
				if (rs.next())
				{
					element = new M_Element(getCtx(), rs, get_TrxName());
				}
			}
			catch (Exception e)
			{
				log.log(Level.SEVERE, sql, e);
			}
			finally
			{
				DB.close(rs, pstmt);
				rs = null; pstmt = null;
			}
		}else {
			element = new M_Element(getCtx(), impData.getAD_Element_ID(), get_TrxName());
		}

		if(element == null)
		{
			element = new M_Element(getCtx(), 0, get_TrxName());
			element.setColumnName(impData.getColumnName());
			if(!Util.isEmpty(impData.getAD_Element_UU()))
			{
				element.setAD_Element_UU(impData.getAD_Element_UU());
			}


			//Name(Mandatory)
			if(Util.isEmpty(impData.getJP_Element_Name()))
			{
				element.setName(element.getColumnName());
			}else {
				element.setName(impData.getJP_Element_Name());
			}

			//PrintName(Mandatory)
			if(Util.isEmpty(impData.getJP_Element_PrintName()))
			{
				element.setPrintName(element.getName());
			}else {
				element.setPrintName(impData.getJP_Element_PrintName());
			}

			element.setDescription(impData.getJP_Element_Description());
			element.setHelp(impData.getJP_Element_Help());
			element.setPO_Name(impData.getJP_Element_PO_Name());
			element.setPO_PrintName(impData.getJP_Element_PO_PrintName());
			element.setPO_Description(impData.getJP_Element_PO_Description());
			element.setPO_Help(impData.getJP_Element_PO_Help());
			if(Util.isEmpty(impData.getJP_Element_EntityType()))
			{
				element.setEntityType("U");
			}else {

				MEntityType entityTye = MEntityType.get(getCtx(), impData.getJP_Element_EntityType());
				if(entityTye == null)
				{
					element.setEntityType("U");
				}else if(entityTye.getAD_EntityType_ID() == 0) {
					element.setEntityType("U");
				}else {
					element.setEntityType(impData.getJP_Element_EntityType());
				}
			}

			try {
				element.saveEx(get_TrxName());
			}catch (Exception e) {
				impData.setI_ErrorMsg(Msg.getMsg(getCtx(),"SaveError") + " : "+ Msg.getElement(getCtx(), "AD_Element_ID") + " : " + e.toString());
				impData.setI_IsImported(false);
				impData.setProcessed(false);
				impData.saveEx(get_TrxName());
				return false;
			}


			impData.setAD_Element_ID(element.getAD_Element_ID());
			updateElementTrl(impData, element);

		}else {

			impData.setAD_Element_ID(element.getAD_Element_ID());
			return updateElement(impData, element);
		}


		return true;
	}

	private boolean updateElement(X_I_TableColumnJP impData, M_Element element)
	{
		boolean isUpdate = false;

		if(element == null)
		{
			final String sql = "SELECT * FROM AD_Element WHERE AD_Element_ID=? ";
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				pstmt = DB.prepareStatement(sql, get_TrxName());
				pstmt.setInt(1, impData.getAD_Element_ID());
				rs = pstmt.executeQuery();
				if (rs.next())
				{
					element = new M_Element(getCtx(), rs, get_TrxName());
				}
			}
			catch (Exception e)
			{
				log.log(Level.SEVERE, sql, e);
			}
			finally
			{
				DB.close(rs, pstmt);
				rs = null; pstmt = null;
			}
		}

		if(!Util.isEmpty(impData.getAD_Element_UU()) && !impData.getAD_Element_UU().equals(element.getAD_Element_UU()))
		{
			element.setAD_Element_UU(impData.getAD_Element_UU());
			isUpdate = true;
		}

		//Name(Mandatory)
		if(!Util.isEmpty(impData.getJP_Element_Name()) && !impData.getJP_Element_Name().equals(element.getName()))
		{
			element.setName(impData.getJP_Element_Name());
			isUpdate = true;
		}

		//PrintName(Mandatory)
		if(!Util.isEmpty(impData.getJP_Element_PrintName()) && !impData.getJP_Element_PrintName().equals(element.getPrintName()))
		{
			element.setPrintName(impData.getJP_Element_PrintName());
			isUpdate = true;
		}

		//Description
		if(BLANK.equalsIgnoreCase(impData.getJP_Element_Description()))
		{
			if(element.getDescription() != null)
			{
				element.setDescription(null);
				isUpdate = true;
			}

		}else if(!Util.isEmpty(impData.getJP_Element_Description()) && !impData.getJP_Element_Description().equals(element.getDescription())){
			element.setDescription(impData.getJP_Element_Description());
			isUpdate = true;
		}

		//Help
		if(BLANK.equalsIgnoreCase(impData.getJP_Element_Help()))
		{
			if(element.getHelp() != null )
			{
				element.setHelp(null);
				isUpdate = true;
			}

		}else if(!Util.isEmpty(impData.getJP_Element_Help()) && !impData.getJP_Element_Help().equals(element.getHelp())){
			element.setHelp(impData.getJP_Element_Help());
			isUpdate = true;
		}

		//PO_Name
		if(BLANK.equalsIgnoreCase(impData.getJP_Element_PO_Name()))
		{
			if(element.getPO_Name() != null )
			{
				element.setPO_Name(null);
				isUpdate = true;
			}

		}else if(!Util.isEmpty(impData.getJP_Element_PO_Name()) && !impData.getJP_Element_PO_Name().equals(element.getPO_Name())){
			element.setPO_Name(impData.getJP_Element_PO_Name());
			isUpdate = true;
		}

		//PO_PrintName
		if(BLANK.equalsIgnoreCase(impData.getJP_Element_PO_PrintName()))
		{
			if(element.getPO_PrintName() != null)
			{
				element.setPO_PrintName(null);
				isUpdate = true;
			}

		}else if(!Util.isEmpty(impData.getJP_Element_PO_PrintName()) && !impData.getJP_Element_PO_PrintName().equals(element.getPO_PrintName())){
			element.setPO_PrintName(impData.getJP_Element_PO_PrintName());
			isUpdate = true;
		}

		//PO_Description
		if(BLANK.equalsIgnoreCase(impData.getJP_Element_PO_Description()))
		{
			if(element.getPO_Description() != null)
			{
				element.setPO_Description(null);
				isUpdate = true;
			}

		}else if(!Util.isEmpty(impData.getJP_Element_PO_Description()) && !impData.getJP_Element_PO_Description().equals(element.getPO_Description())){
			element.setPO_Description(impData.getJP_Element_PO_Description());
			isUpdate = true;
		}

		//PO_Help
		if(BLANK.equalsIgnoreCase(impData.getJP_Element_PO_Help()))
		{
			if(element.getPO_Help() != null )
			{
				element.setPO_Help(null);
				isUpdate = true;
			}

		}else if(!Util.isEmpty(impData.getJP_Element_PO_Help()) && !impData.getJP_Element_PO_Help().equals(element.getPO_Help())){
			element.setPO_Help(impData.getJP_Element_PO_Help());
			isUpdate = true;
		}

		//Placeholder
		if(BLANK.equalsIgnoreCase(impData.getJP_Element_Placeholder()))
		{
			if(element.getPlaceholder() != null)
			{
				element.setPlaceholder(null);
				isUpdate = true;
			}

		}else if(!Util.isEmpty(impData.getJP_Element_Placeholder()) && !impData.getJP_Element_Placeholder().equals(element.getPlaceholder())){
			element.setPlaceholder(impData.getJP_Element_Placeholder());
			isUpdate = true;
		}

		//Entity Type
		if(!Util.isEmpty(impData.getJP_Element_EntityType()) && !impData.getJP_Element_EntityType().equals(element.getEntityType()))
		{
			if(!("D").equals(element.getEntityType()))
			{
				MEntityType entityTye = MEntityType.get(getCtx(), impData.getJP_Element_EntityType());
				if(entityTye == null)
				{
					;
				}else if(entityTye.getAD_EntityType_ID() == 0) {
					;
				}else {
					element.setEntityType(impData.getJP_Element_EntityType());
					isUpdate = true;
				}
			}
		}

		if(isUpdate)
		{
			try {
				element.saveEx(get_TrxName());
			}catch (Exception e) {
				impData.setI_ErrorMsg(Msg.getMsg(getCtx(),"SaveError") + " : "+ Msg.getElement(getCtx(), "AD_Element_ID") + " : " + e.toString());
				impData.setI_IsImported(false);
				impData.setProcessed(false);
				impData.saveEx(get_TrxName());
				return false;
			}

		}

		updateElementTrl(impData, element);

		return true;

	}

	private void updateElementTrl(X_I_TableColumnJP impData, M_Element element)
	{
		if(Util.isEmpty(impData.getAD_Language()))
			return ;

		MLanguage lang = MLanguage.get(getCtx(), impData.getAD_Language());
		if(lang == null)
		{
			impData.setI_ErrorMsg(Msg.getMsg(getCtx(),"Invalid") + " : "+ Msg.getElement(getCtx(), "AD_Language"));
			return ;

		}else if (lang.getAD_Language_ID() == 0) {

			impData.setI_ErrorMsg(Msg.getMsg(getCtx(),"Invalid") + " : "+ Msg.getElement(getCtx(), "AD_Language"));
			return ;
		}

		boolean isUpdate = false;
		StringBuilder sql = new StringBuilder("UPDATE AD_Element_Trl SET ");
		int i = 0;


		//Name(Mandatory)
		if(!Util.isEmpty(impData.getJP_Element_Trl_Name()) && !impData.getJP_Element_Trl_Name().equals(element.get_Translation("Name")) )
		{
			sql = sql.append("Name='").append(impData.getJP_Element_Trl_Name()).append("'");
			isUpdate = true;
			i++;
		}

		//AD_Element_Trl_UU
		if(!Util.isEmpty(impData.getAD_Element_Trl_UU()))
		{
			if(i > 0)
				sql = sql.append(",");
			sql = sql.append("AD_Element_Trl_UU='").append(impData.getAD_Element_Trl_UU()).append("'");
			isUpdate = true;
			i++;
		}

		//PrintName(Mandatory)
		if(!Util.isEmpty(impData.getJP_Element_Trl_PrintName()) && !impData.getJP_Element_Trl_PrintName().equals(element.get_Translation("PrintName")))
		{
			if(i > 0)
				sql = sql.append(",");
			sql = sql.append("PrintName='").append(impData.getJP_Element_Trl_PrintName()).append("'");
			isUpdate = true;
			i++;
		}


		//Description
		if(BLANK.equalsIgnoreCase(impData.getJP_Element_Trl_Description()))
		{
			if(!Util.isEmpty(element.get_Translation("Description",impData.getAD_Language(),true,false)))
			{
				if(i > 0)
					sql = sql.append(",");
				sql = sql.append("Description=null");
				isUpdate = true;
				i++;
			}

		}else if(!Util.isEmpty(impData.getJP_Element_Trl_Description()) && !impData.getJP_Element_Trl_Description().equals(element.get_Translation("Description"))){

			if(i > 0)
				sql = sql.append(",");
			sql = sql.append("Description='").append(impData.getJP_Element_Trl_Description()).append("'");
			isUpdate = true;
			i++;
		}

		//Help
		if(BLANK.equalsIgnoreCase(impData.getJP_Element_Trl_Help()))
		{
			if(!Util.isEmpty(element.get_Translation("Help",impData.getAD_Language(),true,false)))
			{
				if(i > 0)
					sql = sql.append(",");
				sql = sql.append("Help=null");
				isUpdate = true;
				i++;
			}

		}else if(!Util.isEmpty(impData.getJP_Element_Trl_Help()) && !impData.getJP_Element_Trl_Help().equals(element.get_Translation("Help"))){

			if(i > 0)
				sql = sql.append(",");
			sql = sql.append("Help='").append(impData.getJP_Element_Trl_Help()).append("'");
			isUpdate = true;
			i++;
		}

		//PO_Name
		if(BLANK.equalsIgnoreCase(impData.getJP_Element_Trl_PO_Name()))
		{
			if(!Util.isEmpty(element.get_Translation("PO_Name",impData.getAD_Language(),true,false)))
			{
				if(i > 0)
					sql = sql.append(",");
				sql = sql.append("PO_Name=null");
				isUpdate = true;
				i++;
			}

		}else if(!Util.isEmpty(impData.getJP_Element_Trl_PO_Name()) && !impData.getJP_Element_Trl_PO_Name().equals(element.get_Translation("PO_Name"))){

			if(i > 0)
				sql = sql.append(",");
			sql = sql.append("PO_Name='").append(impData.getJP_Element_Trl_PO_Name()).append("'");
			isUpdate = true;
			i++;
		}

		//PO_PrintName
		if(BLANK.equalsIgnoreCase(impData.getJP_Element_Trl_PO_PrintName()))
		{
			if(!Util.isEmpty(element.get_Translation("PO_PrintName",impData.getAD_Language(),true,false)))
			{
				if(i > 0)
					sql = sql.append(",");
				sql = sql.append("PO_PrintName=null");
				isUpdate = true;
				i++;
			}

		}else if(!Util.isEmpty(impData.getJP_Element_Trl_PO_PrintName()) && !impData.getJP_Element_Trl_PO_PrintName().equals(element.get_Translation("PO_PrintName"))){

			if(i > 0)
				sql = sql.append(",");
			sql = sql.append("PO_PrintName='").append(impData.getJP_Element_Trl_PO_PrintName()).append("'");
			isUpdate = true;
			i++;
		}

		//PO_Description
		if(BLANK.equalsIgnoreCase(impData.getJP_Element_Trl_PO_Description()))
		{
			if(!Util.isEmpty(element.get_Translation("PO_Description",impData.getAD_Language(),true,false)))
			{
				if(i > 0)
					sql = sql.append(",");
				sql = sql.append("PO_Description=null");
				isUpdate = true;
				i++;
			}

		}else if(!Util.isEmpty(impData.getJP_Element_Trl_PO_Description()) && !impData.getJP_Element_Trl_PO_Description().equals(element.get_Translation("PO_Description"))){

			if(i > 0)
				sql = sql.append(",");
			sql = sql.append("PO_Description='").append(impData.getJP_Element_Trl_PO_Description()).append("'");
			isUpdate = true;
			i++;
		}

		//PO_Help
		if(BLANK.equalsIgnoreCase(impData.getJP_Element_Trl_PO_Help()))
		{
			if(!Util.isEmpty(element.get_Translation("PO_Help",impData.getAD_Language(),true,false)))
			{
				if(i > 0)
					sql = sql.append(",");
				sql = sql.append("PO_Help=null");
				isUpdate = true;
				i++;
			}

		}else if(!Util.isEmpty(impData.getJP_Element_Trl_PO_Help()) && !impData.getJP_Element_Trl_PO_Help().equals(element.get_Translation("PO_Help"))){

			if(i > 0)
				sql = sql.append(",");
			sql = sql.append("PO_Help='").append(impData.getJP_Element_Trl_PO_Help()).append("'");
			isUpdate = true;
			i++;
		}


		//PO_Help
		if(BLANK.equalsIgnoreCase(impData.getJP_Element_Trl_PO_Help()))
		{
			if(!Util.isEmpty(element.get_Translation("Placeholder",impData.getAD_Language(),true,false)))
			{
				if(i > 0)
					sql = sql.append(",");
				sql = sql.append("Placeholder=null");
				isUpdate = true;
				i++;
			}

		}else if(!Util.isEmpty(impData.getJP_Element_Trl_Placeholder()) && !impData.getJP_Element_Trl_Placeholder().equals(element.get_Translation("Placeholder"))){

			if(i > 0)
				sql = sql.append(",");
			sql = sql.append("Placeholder='").append(impData.getJP_Element_Trl_PO_Help()).append("'");
			isUpdate = true;
			i++;
		}

		if(isUpdate)
		{
			sql = sql.append(" WHERE AD_Element_ID= ? AND AD_Language=?");
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
				pstmt.setInt(1, impData.getAD_Element_ID());
				pstmt.setString(2, impData.getAD_Language());
				pstmt.executeUpdate();
			}
			catch (Exception e)
			{
				log.log(Level.SEVERE, sql.toString(), e);
			}
			finally
			{
				DB.close(rs, pstmt);
				rs = null; pstmt = null;
			}
		}

	}

	/**
	 *
	 * Insert Table
	 *
	 * @param imp
	 * @return
	 */
	private boolean insertTable(X_I_TableColumnJP imp)
	{
		MTable table = new MTable(getCtx(), 0, get_TrxName());
		table.setTableName(imp.getTableName());

		//AD_Table_UU
		if(!Util.isEmpty(imp.getAD_Table_UU()))
			table.setAD_Table_UU(imp.getAD_Table_UU());

		//Name(Mandatory)
		if(Util.isEmpty(imp.getJP_Table_Name()))
		{
			table.setName(imp.getTableName());
		}else {
			table.setName(imp.getJP_Table_Name());
		}

		if(!Util.isEmpty(imp.getJP_Table_Description()))
			table.setDescription(imp.getJP_Table_Description());

		if(!Util.isEmpty(imp.getJP_Table_Help()))
			table.setHelp(imp.getJP_Table_Help());

		if(Util.isEmpty(imp.getAccessLevel()))
		{
			table.setAccessLevel("7");
		}else {
			table.setAccessLevel(imp.getAccessLevel());
		}

		if(!Util.isEmpty(imp.getIsView()))
			table.setIsView("Y".equals(imp.getIsView()));

		if(!Util.isEmpty(imp.getIsChangeLog()))
			table.setIsChangeLog("Y".equals(imp.getIsChangeLog()));

		if(!Util.isEmpty(imp.getIsDeleteable()))
			table.setIsDeleteable("Y".equals(imp.getIsDeleteable()));

		if(!Util.isEmpty(imp.getIsHighVolume()))
			table.setIsHighVolume("Y".equals(imp.getIsHighVolume()));

		if(imp.getAD_Window_ID() > 0)
			table.setAD_Window_ID(imp.getAD_Window_ID());

		if(imp.getPO_Window_ID() > 0)
			table.setPO_Window_ID(imp.getPO_Window_ID());

		if(Util.isEmpty(imp.getJP_Table_EntityType()))
		{
			table.setEntityType("U");
		}else {

			MEntityType entityTye = MEntityType.get(getCtx(), imp.getJP_Table_EntityType());
			if(entityTye == null)
			{
				table.setEntityType("U");
			}else if(entityTye.getAD_EntityType_ID() == 0) {
				table.setEntityType("U");
			}else {
				table.setEntityType(imp.getJP_Table_EntityType());
			}
		}

		try {
			table.saveEx(get_TrxName());
		}catch (Exception e) {
			imp.setI_ErrorMsg(Msg.getMsg(getCtx(),"SaveError") + " : "+ Msg.getElement(getCtx(), "AD_Table_ID") + " : " + e.toString());
			imp.setI_IsImported(false);
			imp.setProcessed(false);
			imp.saveEx(get_TrxName());
			return false;
		}

		imp.setAD_Table_ID(table.getAD_Table_ID());

		return true;
	}

	/**
	 *
	 * Update Table
	 *
	 * @param imp
	 * @return
	 */
	private boolean updateTable(X_I_TableColumnJP imp)
	{
		MTable table = new MTable(getCtx(), imp.getAD_Table_ID(), get_TrxName());
		boolean isUpdate = false;

		//AD_Table_UU
		if(!Util.isEmpty(imp.getAD_Table_UU()) && !imp.getAD_Table_UU().equals(table.getAD_Table_UU()))
		{
			table.setAD_Table_UU(imp.getAD_Table_UU());
			isUpdate = true;
		}

		//Name
		if(!Util.isEmpty(imp.getJP_Table_Name()) && !imp.getJP_Table_Name().equals(table.getName()))
		{
			table.setName(imp.getJP_Table_Name());
			isUpdate = true;
		}

		//Description
		if(BLANK.equalsIgnoreCase(imp.getJP_Table_Description()))
		{
			if(table.getDescription() != null)
			{
				table.setDescription(null);
				isUpdate = true;
			}

		}else if(!Util.isEmpty(imp.getJP_Table_Description()) && !imp.getJP_Table_Description().equals(table.getDescription())){

			table.setDescription(imp.getJP_Table_Description());
			isUpdate = true;
		}

		//Help
		if(BLANK.equalsIgnoreCase(imp.getJP_Table_Help()))
		{
			if(table.getHelp() != null)
			{
				table.setHelp(null);
				isUpdate = true;
			}

		}else if(!Util.isEmpty(imp.getJP_Table_Help()) && !imp.getJP_Table_Help().equals(table.getHelp())){

			table.setHelp(imp.getJP_Table_Help());
			isUpdate = true;
		}


		//AccessLevel(Mandatory)
		if(!Util.isEmpty(imp.getAccessLevel()) && !imp.getAccessLevel().equals(table.getAccessLevel())){
			table.setAccessLevel(imp.getAccessLevel());
			isUpdate = true;
		}

		//IsView
		if(!Util.isEmpty(imp.getIsView()) && "Y".equals(imp.getIsView()) != table.isView())
		{
			table.setIsView("Y".equals(imp.getIsView()));
			isUpdate = true;
		}

		//IsChangeLog
		if(!Util.isEmpty(imp.getIsChangeLog()) && "Y".equals(imp.getIsChangeLog()) != table.isChangeLog())
		{
			table.setIsChangeLog("Y".equals(imp.getIsChangeLog()));
			isUpdate = true;
		}

		//IsDeleteable
		if(!Util.isEmpty(imp.getIsDeleteable()) && "Y".equals(imp.getIsDeleteable()) != table.isDeleteable())
		{
			table.setIsDeleteable("Y".equals(imp.getIsDeleteable()));
			isUpdate = true;
		}

		//IsHighVolume
		if(!Util.isEmpty(imp.getIsHighVolume()) && "Y".equals(imp.getIsHighVolume()) != table.isHighVolume())
		{
			table.setIsHighVolume("Y".equals(imp.getIsHighVolume()));
			isUpdate = true;
		}

		//AD_Window_ID
		if(BLANK.equalsIgnoreCase(imp.getJP_Window_Name()))
		{
			if(table.getAD_Window_ID() > 0)
			{
				table.setAD_Window_ID(0);
				isUpdate = true;
			}

		}else if(imp.getAD_Window_ID() > 0 && imp.getAD_Window_ID() != table.getAD_Window_ID()){
			table.setAD_Window_ID(imp.getAD_Window_ID());
			isUpdate = true;
		}

		//PO_Window_ID
		if(BLANK.equalsIgnoreCase(imp.getJP_PO_Window_Name()))
		{
			if(table.getPO_Window_ID() > 0)
			{
				table.setPO_Window_ID(0);
				isUpdate = true;
			}

		}else if(imp.getPO_Window_ID() > 0 && imp.getPO_Window_ID() != table.getPO_Window_ID()){
			table.setPO_Window_ID(imp.getPO_Window_ID());
			isUpdate = true;
		}

		//Entity Type
		if(!Util.isEmpty(imp.getJP_Table_EntityType()) && !imp.getJP_Table_EntityType().equals(table.getEntityType()))
		{
			if(!("D").equals(table.getEntityType()))
			{
				MEntityType entityTye = MEntityType.get(getCtx(), imp.getJP_Table_EntityType());
				if(entityTye == null)
				{
					;
				}else if(entityTye.getAD_EntityType_ID() == 0) {
					;
				}else {
					table.setEntityType(imp.getJP_Table_EntityType());
					isUpdate = true;
				}
			}
		}

		if(isUpdate)
		{
			try {
				table.saveEx(get_TrxName());
			}catch (Exception e) {
				imp.setI_ErrorMsg(Msg.getMsg(getCtx(),"SaveError") + " : "+ Msg.getElement(getCtx(), "AD_Table_ID") + " : " + e.toString());
				imp.setI_IsImported(false);
				imp.setProcessed(false);
				imp.saveEx(get_TrxName());
				return false;
			}
		}

		return true;

	}

	private boolean createUpdateColumn(X_I_TableColumnJP impData)
	{
		MColumn column = null;
		boolean isUpdate = false;
		boolean isColumnSync = false;

		if(impData.getAD_Column_ID() > 0)
		{
			column = new MColumn(getCtx(), impData.getAD_Column_ID() , get_TrxName());
		}else {

			MTable table = new MTable(getCtx(), impData.getAD_Table_ID(), get_TrxName());
			MColumn[] columns = table.getColumns(true);
			for(int i = 0; i < columns.length; i++)
			{
				if(columns[i].getColumnName().equalsIgnoreCase(impData.getColumnName()))
				{
					column = columns[i];
					break;
				}
			}

			if(column == null)
			{
				column = new MColumn(getCtx(), 0 , get_TrxName());
				isUpdate = true;
				isColumnSync = true;
			}

			if(impData.getAD_Element_ID() == 0)
			{
				impData.setI_ErrorMsg(Msg.getMsg(getCtx(),"FillMandatory") + " : "+ Msg.getElement(getCtx(), "AD_Element_ID"));
				return false;

			}else {
				column.setColumnName(impData.getAD_Element().getColumnName());
			}
		}

		if(column.getAD_Column_ID() == 0)
		{
			column.setAD_Table_ID(impData.getAD_Table_ID());
			column.setAD_Element_ID(impData.getAD_Element_ID());
		}

		//AD_Column_UU
		if(!Util.isEmpty(impData.getAD_Column_UU()) && !impData.getAD_Column_UU().equals(column.getAD_Column_UU()))
		{
			column.setAD_Column_UU(impData.getAD_Column_UU());
			isUpdate = true;
		}

		//Name(Mandatory)
		if(column.getAD_Column_ID() == 0 && Util.isEmpty(impData.getJP_Column_Name())) //New Record
		{
			column.setName(impData.getAD_Element().getName());

		}else if(!Util.isEmpty(impData.getJP_Column_Name()) && !impData.getJP_Column_Name().equals(column.getName()) ){

			column.setName(impData.getJP_Column_Name());
			isUpdate = true;
		}

		//Description
		if(BLANK.equalsIgnoreCase(impData.getJP_Column_Description()))
		{
			if(column.getDescription() != null)
			{
				column.setDescription(null);
				isUpdate = true;
			}

		}else if(!Util.isEmpty(impData.getJP_Column_Description()) && !impData.getJP_Column_Description().equals(column.getDescription())){
			column.setDescription(impData.getJP_Column_Description());
			isUpdate = true;
		}

		//Help
		if(BLANK.equalsIgnoreCase(impData.getJP_Column_Help()))
		{
			if(column.getHelp() != null)
			{
				column.setHelp(null);
				isUpdate = true;
			}

		}else if(!Util.isEmpty(impData.getJP_Column_Help()) && !impData.getJP_Column_Help().equals(column.getHelp())){
			column.setHelp(impData.getJP_Column_Help());
			isUpdate = true;
		}

		//Placeholder
		if(BLANK.equalsIgnoreCase(impData.getJP_Column_Placeholder()))
		{
			if(column.getPlaceholder() != null)
			{
				column.setPlaceholder(null);
				isUpdate = true;
			}

		}else if(!Util.isEmpty(impData.getJP_Column_Placeholder()) && !impData.getJP_Column_Placeholder().equals(column.getPlaceholder())){
			column.setPlaceholder(impData.getJP_Column_Placeholder());
			isUpdate = true;
		}

		//ColumnSQL
		if(BLANK.equalsIgnoreCase(impData.getColumnSQL()))
		{
			if(column.getColumnSQL() != null)
			{
				column.setColumnSQL(null);
				isUpdate = true;
			}

		}else if(!Util.isEmpty(impData.getColumnSQL()) && !impData.getColumnSQL().equals(column.getColumnSQL())){
			column.setColumnSQL(impData.getColumnSQL());
			isUpdate = true;
		}

		//Version(Mandatory)
		if(impData.getVersion() != null && impData.getVersion().compareTo(Env.ZERO) != 0 && impData.getVersion() != column.getVersion())
		{
			column.setVersion(impData.getVersion());
			isUpdate = true;
		}

		//AD_Reference_ID(Mandatory)
		if(column.getAD_Column_ID() == 0)//New Record
		{
			if(impData.getAD_Reference_ID() == 0)
			{
				impData.setI_ErrorMsg(Msg.getMsg(getCtx(),"FillMandatory") + " : "+ Msg.getElement(getCtx(), "AD_Reference_ID"));
				return false;
			}
		}

		if(impData.getAD_Reference_ID() > 0 && impData.getAD_Reference_ID() != column.getAD_Reference_ID())
		{
			column.setAD_Reference_ID(impData.getAD_Reference_ID());
			isUpdate = true;
		}

		//AD_Reference_Value_ID
		if(BLANK.equalsIgnoreCase(impData.getJP_Reference_Value_Name()))
		{
			if(column.getAD_Reference_Value_ID() > 0)
			{
				column.setAD_Reference_Value_ID(0);
				isUpdate = true;
			}

		}else if(impData.getAD_Reference_Value_ID() > 0 && impData.getAD_Reference_Value_ID() != column.getAD_Reference_Value_ID()){

			column.setAD_Reference_Value_ID(impData.getAD_Reference_Value_ID());
			isUpdate = true;
		}

		//AD_Val_Rule_ID
		if(BLANK.equalsIgnoreCase(impData.getJP_Val_Rule_Name()))
		{
			if(column.getAD_Val_Rule_ID() > 0)
			{
				column.setAD_Val_Rule_ID(0);
				isUpdate = true;
			}

		}else if(impData.getAD_Val_Rule_ID() > 0 && impData.getAD_Val_Rule_ID() != column.getAD_Val_Rule_ID()){
			column.setAD_Val_Rule_ID(impData.getAD_Val_Rule_ID());
			isUpdate = true;
		}

		//FieldLength
		if(impData.getFieldLength() !=0 && impData.getFieldLength() != column.getFieldLength())
		{
			column.setFieldLength(impData.getFieldLength());
			isUpdate = true;
			isColumnSync = true;
		}

		//IsKey
		if(!Util.isEmpty(impData.getIsKey()) && "Y".equals(impData.getIsKey()) != column.isKey())
		{
			column.setIsKey("Y".equals(impData.getIsKey()));
			isUpdate = true;
		}

		//IsParent
		if(!Util.isEmpty(impData.getIsParent()) && "Y".equals(impData.getIsParent()) != column.isParent())
		{
			column.setIsParent("Y".equals(impData.getIsParent()));
			isUpdate = true;
		}

		//IsMandatory
		if(!Util.isEmpty(impData.getIsMandatory()) && "Y".equals(impData.getIsMandatory()) != column.isMandatory())
		{
			column.setIsMandatory("Y".equals(impData.getIsMandatory()));
			isUpdate = true;
			isColumnSync = true;
		}

		//IsUpdateable
		if(!Util.isEmpty(impData.getIsUpdateable()) && "Y".equals(impData.getIsUpdateable()) != column.isUpdateable())
		{
			column.setIsUpdateable("Y".equals(impData.getIsUpdateable()));
			isUpdate = true;
		}

		//IsAlwaysUpdateable
		if(!Util.isEmpty(impData.getIsAlwaysUpdateable()) && "Y".equals(impData.getIsAlwaysUpdateable()) != column.isAlwaysUpdateable())
		{
			column.setIsAlwaysUpdateable("Y".equals(impData.getIsAlwaysUpdateable()));
			isUpdate = true;
		}

		//IsIdentifier
		if(!Util.isEmpty(impData.getIsIdentifier()) && "Y".equals(impData.getIsIdentifier()) != column.isIdentifier())
		{
			column.setIsIdentifier("Y".equals(impData.getIsIdentifier()));
			isUpdate = true;
		}

		//SeqNo
		if(impData.getSeqNo() != 0 && impData.getSeqNo() != column.getSeqNo())
		{
			column.setSeqNo(impData.getSeqNo());
			isUpdate = true;
		}

		//IsSelectionColumn
		if(!Util.isEmpty(impData.getIsSelectionColumn()) && "Y".equals(impData.getIsSelectionColumn()) != column.isSelectionColumn())
		{
			column.setIsSelectionColumn("Y".equals(impData.getIsSelectionColumn()));
			isUpdate = true;
		}

		//SeqNoSelection
		if(impData.getSeqNoSelection() != 0  && impData.getSeqNoSelection() != column.getSeqNoSelection())
		{
			column.setSeqNoSelection(impData.getSeqNoSelection());
			isUpdate = true;
		}

		//VFormat
		if(BLANK.equalsIgnoreCase(impData.getVFormat()))
		{
			if(column.getVFormat() != null)
			{
				column.setVFormat(null);
				isUpdate = true;
			}

		}else if(!Util.isEmpty(impData.getVFormat()) && !impData.getVFormat().equals(column.getVFormat())) {
			column.setVFormat(impData.getVFormat());
			isUpdate = true;
		}

		//FormatPattern
		if(BLANK.equalsIgnoreCase(impData.getFormatPattern()))
		{
			if(column.getFormatPattern() != null)
			{
				column.setFormatPattern(null);
				isUpdate = true;
			}

		}else if(!Util.isEmpty(impData.getFormatPattern()) && !impData.getFormatPattern().equals(column.getFormatPattern())){

			column.setFormatPattern(impData.getFormatPattern());
			isUpdate = true;
		}

		//ValueMin
		if(BLANK.equalsIgnoreCase(impData.getValueMin()))
		{
			if(column.getValueMin() != null)
			{
				column.setValueMin(null);
				isUpdate = true;
			}

		}else if(!Util.isEmpty(impData.getValueMin()) && !impData.getValueMin().equals(column.getValueMin())){
			column.setValueMin(impData.getValueMin());
			isUpdate = true;
		}

		//ValueMax
		if(BLANK.equalsIgnoreCase(impData.getValueMax()))
		{
			if(column.getValueMax() != null)
			{
				column.setValueMax(null);
				isUpdate = true;
			}

		}else if(!Util.isEmpty(impData.getValueMax()) && !impData.getValueMax().equals(column.getValueMax())){

			column.setValueMax(impData.getValueMax());
			isUpdate = true;
		}

		//AD_Process_ID
		if(BLANK.equalsIgnoreCase(impData.getJP_Process_Value()))
		{
			if(column.getAD_Process_ID() > 0)
			{
				column.setAD_Process_ID(0);
				isUpdate = true;
			}

		}else if(impData.getAD_Process_ID() != column.getAD_Process_ID()){
			column.setAD_Process_ID(impData.getAD_Process_ID());
			isUpdate = true;
		}

		//IsToolbarButton
		if(!Util.isEmpty(impData.getIsToolbarButton()) && !impData.getIsToolbarButton().equals(column.getIsToolbarButton()))
		{
			column.setIsToolbarButton(impData.getIsToolbarButton());
			isUpdate = true;
		}

		//AD_Chart_ID
		if(BLANK.equalsIgnoreCase(impData.getJP_Chart_Name()))
		{
			if(column.getAD_Chart_ID() > 0)
			{
				column.setAD_Chart_ID(0);
				isUpdate = true;
			}

		}else if(impData.getAD_Chart_ID() != column.getAD_Chart_ID()){

			column.setAD_Chart_ID(impData.getAD_Chart_ID());
			isUpdate = true;
		}

		//PA_DashboardContent_ID
		if(BLANK.equalsIgnoreCase(impData.getJP_DashboardContent_Name()))
		{
			if(column.getPA_DashboardContent_ID() > 0)
			{
				column.setPA_DashboardContent_ID(0);
				isUpdate = true;
			}

		}else if(impData.getPA_DashboardContent_ID() != column.getPA_DashboardContent_ID()){

			column.setPA_DashboardContent_ID(impData.getPA_DashboardContent_ID());
			isUpdate = true;

		}

		//FKConstraintName
		if(!Util.isEmpty(impData.getFKConstraintName()) && !impData.getFKConstraintName().equals(column.getFKConstraintName()))
		{
			column.setFKConstraintName(impData.getFKConstraintName());
			isUpdate = true;
		}

		//FKConstraintType
		if(!Util.isEmpty(impData.getFKConstraintType()) && !impData.getFKConstraintType().equals(column.getFKConstraintType()))
		{
			column.setFKConstraintType(impData.getFKConstraintType());
			isUpdate = true;
			isColumnSync = true;
		}

		//Placeholder
		if(BLANK.equalsIgnoreCase(impData.getJP_Column_Placeholder()))
		{
			if(column.getPlaceholder() != null)
			{
				column.setPlaceholder(null);
				isUpdate = true;
			}

		}else if(!Util.isEmpty(impData.getJP_Column_Placeholder()) && !impData.getJP_Column_Placeholder().equals(column.getPlaceholder())){

			column.setPlaceholder(impData.getJP_Column_Placeholder());
			isUpdate = true;
		}

		//DefaultValue
		if(BLANK.equalsIgnoreCase(impData.getDefaultValue()))
		{
			if(column.getDefaultValue() != null)
			{
				column.setDefaultValue(null);
				isUpdate = true;
			}

		}else if(!Util.isEmpty(impData.getDefaultValue()) && !impData.getDefaultValue().equals(column.getDefaultValue())){
			column.setDefaultValue(impData.getDefaultValue());
			isUpdate = true;
		}

		//ReadOnlyLogic
		if(BLANK.equalsIgnoreCase(impData.getReadOnlyLogic()))
		{
			if(column.getReadOnlyLogic() != null)
			{
				column.setReadOnlyLogic(null);
				isUpdate = true;
			}

		}else if(!Util.isEmpty(impData.getReadOnlyLogic()) && !impData.getReadOnlyLogic().equals(column.getReadOnlyLogic())){
			column.setReadOnlyLogic(impData.getReadOnlyLogic());
			isUpdate = true;
		}

		//MandatoryLogic
		if(BLANK.equalsIgnoreCase(impData.getMandatoryLogic()))
		{
			if(column.getMandatoryLogic() != null)
			{
				column.setMandatoryLogic(null);
				isUpdate = true;
			}

		}else if(!Util.isEmpty(impData.getMandatoryLogic()) && !impData.getMandatoryLogic().equals(column.getMandatoryLogic())){
			column.setMandatoryLogic(impData.getMandatoryLogic());
			isUpdate = true;
		}

		//IsAutocomplete
		if(!Util.isEmpty(impData.getIsAutocomplete()) && "Y".equals(impData.getIsAutocomplete()) != column.isAutocomplete())
		{
			column.setIsAutocomplete("Y".equals(impData.getIsAutocomplete()));
			isUpdate = true;
		}

		//IsAllowCopy
		if(!Util.isEmpty(impData.getIsAllowCopy()) && "Y".equals(impData.getIsAllowCopy()) != column.isAllowCopy())
		{
			column.setIsAllowCopy("Y".equals(impData.getIsAllowCopy()));
			isUpdate = true;
		}

		//IsAllowLogging
		if(!Util.isEmpty(impData.getIsAllowLogging()) && "Y".equals(impData.getIsAllowLogging()) != column.isAllowLogging())
		{
			column.setIsAllowLogging("Y".equals(impData.getIsAllowLogging()));
			isUpdate = true;
		}

		//IsSecure
		if(!Util.isEmpty(impData.getIsSecure()) && "Y".equals(impData.getIsSecure()) != column.isSecure())
		{
			column.setIsSecure("Y".equals(impData.getIsSecure()));
			isUpdate = true;
		}

		//IsHtml
		if(!Util.isEmpty(impData.getIsHtml()) && "Y".equals(impData.getIsHtml()) != column.isHtml())
		{
			column.setIsHtml("Y".equals(impData.getIsHtml()));
			isUpdate = true;
		}

		//IsTranslated
		if(!Util.isEmpty(impData.getIsTranslated()) && "Y".equals(impData.getIsTranslated()) != column.isTranslated())
		{
			column.setIsTranslated("Y".equals(impData.getIsTranslated()));
			isUpdate = true;
		}

		//Callout
		//MandatoryLogic
		if(BLANK.equalsIgnoreCase(impData.getCallout()))
		{
			if(column.getCallout() != null)
			{
				column.setCallout(null);
				isUpdate = true;
			}

		}else if(!Util.isEmpty(impData.getCallout()) && !impData.getCallout().equals(column.getCallout())){
			column.setCallout(impData.getCallout());
			isUpdate = true;
		}

		//Entity Type
		if(column.getAD_Column_ID() == 0)
		{
			if( Util.isEmpty(impData.getJP_Table_EntityType()))
			{
				column.setEntityType("U");

			}else {

				MEntityType entityTye = MEntityType.get(getCtx(), impData.getJP_Column_EntityType());
				if(entityTye == null)
				{
					column.setEntityType("U");
				}else if(entityTye.getAD_EntityType_ID() == 0) {
					column.setEntityType("U");
				}else {
					column.setEntityType(impData.getJP_Column_EntityType());
				}
			}
		}else {

			if(!Util.isEmpty(impData.getJP_Column_EntityType()) && !impData.getJP_Column_EntityType().equals(column.getEntityType()))
			{
				if(!("D").equals(column.getEntityType()))
				{

					MEntityType entityTye = MEntityType.get(getCtx(), impData.getJP_Column_EntityType());
					if(entityTye == null)
					{
						;
					}else if(entityTye.getAD_EntityType_ID() == 0) {
						;
					}else {
						column.setEntityType(impData.getJP_Column_EntityType());
						isUpdate = true;
					}
				}
			}
		}

		if(isUpdate)
		{
			try {
				column.saveEx(get_TrxName());
			}catch (Exception e) {
				impData.setI_ErrorMsg(Msg.getMsg(getCtx(),"SaveError") + " : "+ Msg.getElement(getCtx(), "AD_Column_ID") + " : " + e.toString());
				impData.setI_IsImported(false);
				impData.setProcessed(false);
				impData.saveEx(get_TrxName());
				return false;
			}
		}

		if(impData.getAD_Column_ID() == 0)
			impData.setAD_Column_ID(column.getAD_Column_ID());

		if(isColumnSync)
		{
			if(impData.getAD_Table().isView())
				return true;

			if(!Util.isEmpty(column.getColumnSQL()))
				return true;

			if(column.getAD_Reference_ID() == DisplayType.Chart)
				return true;

			ProcessInfo pi = new ProcessInfo("Synchronize Column", 0);
			pi.setClassName("org.compiere.process.ColumnSync");
			pi.setAD_Client_ID(getAD_Client_ID());
			pi.setAD_User_ID(getAD_User_ID());
			pi.setAD_PInstance_ID(getAD_PInstance_ID());
			pi.setRecord_ID(column.getAD_Column_ID());
			ProcessInfoParameter[] pars = new ProcessInfoParameter[0];
			pi.setParameter(pars);
			boolean success = ProcessUtil.startJavaProcess(getCtx(), pi, Trx.get(get_TrxName(), true), false, processUI);
			if(!success)
			{
				impData.setI_ErrorMsg(Msg.getMsg(getCtx(),"ProcessFailed") + " : Synchronize Column");
			}
		}

		return true;
	}

	/**
	 * Reverese Look up  AD_Table_ID From Name
	 *
	 * @throws Exception
	 */
	private boolean reverseLookupAD_Table_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		int no = 0;

		sql = new StringBuilder ("UPDATE I_TableColumnJP i ")
				.append("SET AD_Table_ID=(SELECT AD_Table_ID FROM AD_Table p")
				.append(" WHERE Upper(i.TableName)=Upper(p.TableName) AND p.AD_Client_ID=i.AD_Client_ID) ")
				.append(" WHERE i.AD_Table_ID IS NULL AND i.TableName IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());

		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}

		return true;

	}//reverseLookupAD_Table_ID


	/**
	 * Reverse lookup AD_Window_ID From JP_Window_Name
	 *
	 * @throws Exception
	 */
	private boolean reverseLookupAD_Window_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		int no = 0;

		sql = new StringBuilder ("UPDATE I_TableColumnJP i ")
			.append("SET AD_Window_ID=(SELECT AD_Window_ID FROM AD_Window p")
			.append(" WHERE i.JP_Window_Name=p.Name AND i.AD_Client_ID=p.AD_Client_ID) ")
			.append("WHERE i.AD_Window_ID IS NULL AND i.JP_Window_Name IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}

		//Invalid JP_Window_Name
		message = Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "JP_Window_Name");
		sql = new StringBuilder ("UPDATE I_TableColumnJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append("WHERE AD_Window_ID IS NULL AND JP_Window_Name IS NOT NULL")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}

		return true;
	}//reverseLookupAD_Window_ID


	/**
	 * Reverse lookup PO_Window_ID From JP_PO_Window_Name
	 *
	 * @throws Exception
	 */
	private boolean reverseLookupPO_Window_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		int no = 0;

		sql = new StringBuilder ("UPDATE I_TableColumnJP i ")
			.append("SET PO_Window_ID=(SELECT AD_Window_ID FROM AD_Window p")
			.append(" WHERE i.JP_PO_Window_Name=p.Name AND i.AD_Client_ID=p.AD_Client_ID) ")
			.append("WHERE i.PO_Window_ID IS NULL AND i.JP_PO_Window_Name IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine( message + "=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}

		//Invalid JP_PO_Window_Name
		message = Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "JP_PO_Window_Name");
		sql = new StringBuilder ("UPDATE I_TableColumnJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append("WHERE PO_Window_ID IS NULL AND JP_PO_Window_Name IS NOT NULL")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}

		return true;

	}//reverseLookupPO_Window_ID


	/**
	 * Reverse lookup AD_Element_ID From ColumnName
	 *
	 * @throws Exception
	 */
	private boolean reverseLookupAD_Element_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		int no = 0;

		sql = new StringBuilder ("UPDATE I_TableColumnJP i ")
			.append("SET AD_Element_ID=(SELECT AD_Element_ID FROM AD_Element p")
			.append(" WHERE Upper(i.ColumnName)=Upper(p.ColumnName) AND i.AD_Client_ID=p.AD_Client_ID) ")
			.append("WHERE i.AD_Element_ID IS NULL AND i.ColumnName IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine( message + "=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}

		return true;

	}//reverseLookupAD_Element_ID


	/**
	 * Reverse lookup AD_Column_ID From ColumnName
	 *
	 * @throws Exception
	 */
	private boolean reverseLookupAD_Column_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		int no = 0;

		sql = new StringBuilder ("UPDATE I_TableColumnJP i ")
			.append("SET AD_Column_ID=(SELECT AD_Column_ID FROM AD_Column p")
			.append(" WHERE Upper(i.ColumnName)=Upper(p.ColumnName) AND i.AD_Client_ID=p.AD_Client_ID AND i.AD_Table_ID = p.AD_Table_ID ) ")
			.append("WHERE i.AD_Column_ID IS NULL AND i.ColumnName IS NOT NULL ")
			.append(" AND i.I_IsImported<>'Y' ").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine( message + "=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}

		return true;

	}//reverseLookupAD_Column_ID


	/**
	 * Reverse lookup AD_Reference_ID From JP_Reference_Name
	 *
	 * @throws Exception
	 */
	private boolean reverseLookupAD_Reference_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		int no = 0;

		sql = new StringBuilder ("UPDATE I_TableColumnJP i ")
			.append("SET AD_Reference_ID=(SELECT AD_Reference_ID FROM AD_Reference p")
			.append(" WHERE i.JP_Reference_Name=p.Name AND i.AD_Client_ID=p.AD_Client_ID AND p.validationType='D') ")
			.append("WHERE i.AD_Reference_ID IS NULL AND i.JP_Reference_Name IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine( message + "=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}

		//Invalid JP_Reference_Name
		message = Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "JP_Reference_Name");
		sql = new StringBuilder ("UPDATE I_TableColumnJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append("WHERE AD_Reference_ID IS NULL AND JP_Reference_Name IS NOT NULL")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}

		return true;

	}//reverseLookupAD_Reference_ID


	/**
	 * Reverse lookup AD_Reference_Value_ID From JP_Reference_Name
	 *
	 * @throws Exception
	 */
	private boolean reverseLookupAD_Reference_Value_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		int no = 0;

		sql = new StringBuilder ("UPDATE I_TableColumnJP i ")
			.append("SET AD_Reference_Value_ID=(SELECT AD_Reference_ID FROM AD_Reference p")
			.append(" WHERE i.JP_Reference_Value_Name=p.Name AND i.AD_Client_ID=p.AD_Client_ID AND p.validationType IN ('L','T') ) ")
			.append("WHERE i.AD_Reference_Value_ID IS NULL AND i.JP_Reference_Value_Name IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine( message + "=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}

		//Invalid JP_Reference_Value_Name
		message = Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "JP_Reference_Value_Name");
		sql = new StringBuilder ("UPDATE I_TableColumnJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append("WHERE AD_Reference_Value_ID IS NULL AND JP_Reference_Value_Name IS NOT NULL")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}

		return true;

	}//reverseLookupAD_Reference_Value_ID


	/**
	 * Reverse lookup AD_Val_Rule_ID From JP_Val_Rule_Name
	 *
	 * @throws Exception
	 */
	private boolean reverseLookupAD_Val_Rule_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		int no = 0;

		sql = new StringBuilder ("UPDATE I_TableColumnJP i ")
			.append("SET AD_Val_Rule_ID=(SELECT AD_Val_Rule_ID FROM AD_Val_Rule p")
			.append(" WHERE i.JP_Val_Rule_Name=p.Name AND i.AD_Client_ID=p.AD_Client_ID) ")
			.append("WHERE i.AD_Val_Rule_ID IS NULL AND i.JP_Val_Rule_Name IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine( message + "=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}

		//Invalid JP_Val_Rule_Name
		message = Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "JP_Val_Rule_Name");
		sql = new StringBuilder ("UPDATE I_TableColumnJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append("WHERE AD_Val_Rule_ID IS NULL AND JP_Val_Rule_Name IS NOT NULL")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}

		return true;

	}//reverseLookupAD_Val_Rule_ID



	/**
	 * Reverse lookup AD_Process_ID From JP_Process_Value
	 *
	 * @throws Exception
	 */
	private boolean reverseLookupAD_Process_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		int no = 0;

		sql = new StringBuilder ("UPDATE I_TableColumnJP i ")
			.append("SET AD_Process_ID=(SELECT AD_Process_ID FROM AD_Process p")
			.append(" WHERE i.JP_Process_Value=p.Value AND i.AD_Client_ID=p.AD_Client_ID) ")
			.append("WHERE i.AD_Process_ID IS NULL AND i.JP_Process_Value IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine( message + "=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}

		//Invalid JP_Process_Value
		message = Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "JP_Process_Value");
		sql = new StringBuilder ("UPDATE I_TableColumnJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append("WHERE AD_Process_ID IS NULL AND JP_Process_Value IS NOT NULL")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}

		return true;

	}//reverseLookupAD_Process_ID


	/**
	 * Reverse lookup AD_Chart_ID From JP_Chart_Name
	 *
	 * @throws Exception
	 */
	private boolean reverseLookupAD_Chart_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		int no = 0;

		sql = new StringBuilder ("UPDATE I_TableColumnJP i ")
			.append("SET AD_Chart_ID=(SELECT AD_Chart_ID FROM AD_Chart p")
			.append(" WHERE i.JP_Chart_Name=p.Name AND i.AD_Client_ID=p.AD_Client_ID) ")
			.append("WHERE i.AD_Chart_ID IS NULL AND i.JP_Chart_Name IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine( message + "=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}

		//Invalid JP_Chart_Name
		message = Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "JP_Chart_Name");
		sql = new StringBuilder ("UPDATE I_TableColumnJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append("WHERE AD_Chart_ID IS NULL AND JP_Chart_Name IS NOT NULL")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}

		return true;

	}//reverseLookupAD_Chart_ID


	/**
	 * Reverse lookup PA_DashboardContent_ID From JP_DashboardContent_Name
	 *
	 * @throws Exception
	 */
	private boolean reverseLookupPA_DashboardContent_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		int no = 0;

		sql = new StringBuilder ("UPDATE I_TableColumnJP i ")
			.append("SET PA_DashboardContent_ID=(SELECT PA_DashboardContent_ID FROM PA_DashboardContent p")
			.append(" WHERE i.JP_DashboardContent_Name=p.Name AND i.AD_Client_ID=p.AD_Client_ID) ")
			.append("WHERE i.PA_DashboardContent_ID IS NULL AND i.JP_DashboardContent_Name IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine( message + "=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}

		//Invalid JP_DashboardContent_Name
		message = Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "JP_DashboardContent_Name");
		sql = new StringBuilder ("UPDATE I_TableColumnJP ")
			.append("SET I_ErrorMsg='"+ message + "'")
			.append("WHERE PA_DashboardContent_ID IS NULL AND JP_DashboardContent_Name IS NOT NULL")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}

		return true;

	}//reverseLookupPA_DashboardContent_ID

}	//	Import Table And Column
