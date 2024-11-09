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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;

import org.adempiere.base.Service;
import org.adempiere.exceptions.AdempiereException;
import org.adempiere.util.ProcessUtil;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.taskdefs.Zip;
import org.compiere.model.MClient;
import org.compiere.model.MClientInfo;
import org.compiere.model.MColumn;
import org.compiere.model.MPInstance;
import org.compiere.model.MPInstancePara;
import org.compiere.model.MProcess;
import org.compiere.model.MProcessPara;
import org.compiere.model.MQuery;
import org.compiere.model.MStorageProvider;
import org.compiere.model.MTable;
import org.compiere.process.ProcessInfo;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.tools.FileUtil;
import org.compiere.util.DB;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Trx;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.MGenericExpFormat;
import jpiere.base.plugin.org.adempiere.model.MGenericExpFormatReplace;
import jpiere.base.plugin.org.adempiere.model.MGenericExpFormatRow;

/**
 * JPIERE-0628 Generic Export Process
 *
 *  @author Hideaki Hagiwara
 *
 */
public class GenericExportProcess extends SvrProcess {

	private ProcessInfoParameter[] para = null;
	private int p_JP_GenericExpFormat_ID = 0;
	private MGenericExpFormat m_MGenericExpFormat = null;
	private MGenericExpFormatRow[] m_GEFRows = null;
	private int p_JP_StorageAttachment_ID  = 0;
	
	@Override
	protected void prepare() 
	{
		para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();

			if (para[i].getParameter() == null){
				;
			}else if (name.equals(MGenericExpFormat.COLUMNNAME_JP_GenericExpFormat_ID)){
				p_JP_GenericExpFormat_ID = para[i].getParameterAsInt();
			}
		}
	}

	@Override
	protected String doIt() throws Exception 
	{
		
		if(p_JP_GenericExpFormat_ID == 0)
			return Msg.getMsg(getCtx(), "FillMandatory") + Msg.getElement(getCtx(), MGenericExpFormat.COLUMNNAME_JP_GenericExpFormat_ID);

		m_MGenericExpFormat = new MGenericExpFormat(getCtx(), p_JP_GenericExpFormat_ID, get_TrxName());
		
		
		//Check JPiere Attachement File Storage Provider
		if(m_MGenericExpFormat.isAttachmentFileJP())
		{
			MClient m_Client = MClient.get(getAD_Client_ID());
			MClientInfo m_ClientInfo = m_Client.getInfo();
			p_JP_StorageAttachment_ID = m_ClientInfo.get_ValueAsInt("JP_StorageAttachment_ID");
			if(p_JP_StorageAttachment_ID == 0)
			{
				throw new Exception(Msg.getMsg(getCtx(), "NotFound") + Msg.getElement(getCtx(), "JP_StorageAttachment_ID"));
			}
		}
		
		
		//Confirm Name of Attachment file.
		/**
		if(m_MGenericExpFormat.isAttachmentFileJP())
		{
			String fName = getFileName();
			
			if(m_MGenericExpFormat.isZipJP())
			{
				fName = fName +".zip";
			}else {
				fName = fName + "." + getFileExtension();
			}
			
			ArrayList<MAttachmentFileRecord>  afrList = MAttachmentFileRecord.getAttachmentFileRecordPO(getCtx(), MGenericExpFormat.Table_ID, p_JP_GenericExpFormat_ID, false, get_TrxName());
			boolean isError = false;
			for(MAttachmentFileRecord afr : afrList)
			{
				if(fName.equals(afr.getJP_AttachmentFileName()))
				{
					isError = true;
					break;
				}
			}
			
			if(isError)
			{
				//A file with the same name is already attached.
				throw new Exception(Msg.getMsg(getCtx(), "JP_AttachmentFileSameName"));
			}
		}*/
		
		//Check Column
		if(m_GEFRows == null)
			m_GEFRows = m_MGenericExpFormat.getRows();
		
		MColumn col = null;
		for(MGenericExpFormatRow m_GEFRow : m_GEFRows )
		{
			if(MGenericExpFormatRow.JP_EXPORTTYPE_Variable.equals(m_GEFRow.getJP_ExportType()))
			{
				col = MColumn.get(m_GEFRow.getAD_Column_ID());
				if(m_MGenericExpFormat.getAD_Table_ID() != col.getAD_Table_ID())
				{
					throw new Exception(Msg.getMsg(getCtx(), "JP_Inconsistency", new Object[]{Msg.getElement(getCtx(), "AD_Table_ID"),Msg.getElement(getCtx(), "AD_Column_ID")}) + " : " + col.getColumnName());
				}
				
				
				if(MColumn.get(m_GEFRow.getAD_Column_ID()).isSecure())
				{
					//CCan not export the date of Secure content
					throw new Exception(Msg.getMsg(getCtx(), "JP_CouldNotExportIsSecure") + " : " + col.getColumnName());
				}
					
			}
			
		}
		
		
		//Run the process for inserting export data to the target table.
		if(m_MGenericExpFormat.getAD_Process_ID() > 0)
		{
			runProcess() ;
		}
		
		
		//Create export file
		File file = createExportFile() ;
		File zipFile = null;
		
		
		//ZIP Compression
		if(m_MGenericExpFormat.isZipJP())
		{
			String temp = System.getProperty("java.io.tmpdir");
			String zipfileName = getFileName();

			zipFile = new File(temp+ File.separator + zipfileName +".zip");
			if(zipFile.exists())
			{
				try {
					Files.delete(zipFile.toPath());
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
			
			File zipFolder = new File(temp+ File.separator + zipfileName);
			if(zipFolder.exists())
			{
				try {
					FileUtil.deleteFolderRecursive(zipFolder);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
			zipFolder.mkdirs();
			
			File destZipFile = new File(zipFolder + File.separator + zipfileName + "." + getFileExtension());
			Files.copy(file.toPath(), destZipFile.toPath());

			//create the compressed packages
			Zip zipper = new Zip();
		    zipper.setDestFile(zipFile);
		    zipper.setBasedir(zipFolder);
		    //zipper.setIncludes(includesdir.replace(" ", "*"));
		    zipper.setUpdate(true);
		    zipper.setCompress(true);
		    zipper.setCaseSensitive(false);
		    zipper.setFilesonly(false);
		    zipper.setTaskName("zip");
		    zipper.setTaskType("zip");
		    zipper.setProject(new Project());
		    zipper.setOwningTarget(new Target());
		    zipper.execute();
		}
		
		
		//Attach file.
		/**
		if(m_MGenericExpFormat.isAttachmentFileJP())
		{
			MAttachmentFileRecord afr = new MAttachmentFileRecord(getCtx(),0,get_TrxName());
			afr.setAD_Table_ID(MGenericExpFormat.Table_ID);
			afr.setRecord_ID(m_MGenericExpFormat.getJP_GenericExpFormat_ID());
			
			try 
			{
				if(m_MGenericExpFormat.isZipJP())
				{
					afr.setJP_AttachmentFileName(zipFile.getName());
					afr.setJP_MediaContentType("application/x-zip-compressed");
					afr.setJP_MediaFormat("zip");
					afr.upLoadLFile(Files.readAllBytes(zipFile.toPath()));
					
				}else {
					
					afr.setJP_AttachmentFileName(file.getName());
					if("csv".equals(m_MGenericExpFormat.getJP_FileExtension()))
						afr.setJP_MediaContentType("text/csv");
					else if("txt".equals(m_MGenericExpFormat.getJP_FileExtension()))
						afr.setJP_MediaContentType("text/plain");
					else
						afr.setJP_MediaContentType("text/plain");
					
					afr.setJP_MediaFormat(Util.isEmpty(m_MGenericExpFormat.getJP_FileExtension()) ? defaultExtension : m_MGenericExpFormat.getJP_FileExtension());
					afr.upLoadLFile(Files.readAllBytes(file.toPath()));
				}
				afr.saveEx(get_TrxName());
				
			}catch (Exception e) {
				
				//An error occurred while attaching the file
				addBufferLog(0, null, null, Msg.getMsg(getCtx(), "JP_AttachmentFileError") + " " + e.toString()
															, MGenericExpFormat.Table_ID, p_JP_GenericExpFormat_ID);
				
			}finally {
				;
			}
			
		}*/
		
		
		//File to the Storage.
		if(m_MGenericExpFormat.getAD_StorageProvider_ID() > 0)
		{
			try
			{
				boolean isExportFile = false;
				I_GenericExportStorageProvider efs = getExportFileStore() ;
				if(m_MGenericExpFormat.isZipJP())
				{
					isExportFile = efs.exportFile(m_MGenericExpFormat, zipFile);
				}else {
					isExportFile = efs.exportFile(m_MGenericExpFormat, file);
				}
				
				if(!isExportFile)
				{
					//Failed to save file
					addBufferLog(0, null, null, Msg.getMsg(getCtx(), "JP_FailedToSaveFile"), MStorageProvider.Table_ID, m_MGenericExpFormat.getAD_StorageProvider_ID() );
				}
				
			}catch (Exception e) {
				
				//Failed to save file
				addBufferLog(0, null, null, Msg.getMsg(getCtx(), "JP_FailedToSaveFile") + " " + e.toString()
									, MStorageProvider.Table_ID, m_MGenericExpFormat.getAD_StorageProvider_ID() );
			}
		}
		
		
		//Export file.
		if(processUI != null)
		{
			if(m_MGenericExpFormat.isZipJP())
			{
				processUI.download(zipFile);
				
			}else {
				processUI.download(file);
			}
		}
		
		return "@ProcessOK@";
	}

	/**
	 * 
	 * Run the process for inserting export data to the target table.
	 * 
	 * @return
	 * @throws Exception
	 */
	private boolean runProcess() throws Exception
	{
		ProcessInfo pi = new ProcessInfo("Generic Export Process", m_MGenericExpFormat.getAD_Process_ID());
		MProcess m_Process = MProcess.get(m_MGenericExpFormat.getAD_Process_ID());
		pi.setClassName(m_Process.getClassname());
		pi.setAD_Client_ID(getAD_Client_ID());
		pi.setAD_User_ID(getAD_User_ID());
		pi.setAD_PInstance_ID(getAD_PInstance_ID());
		pi.setRecord_ID(m_MGenericExpFormat.getJP_GenericExpFormat_ID());
		pi.setTable_ID(MGenericExpFormat.Table_ID);
		
		MProcessPara[] params = m_Process.getParameters();
		boolean isContainParam = false;
		ArrayList<ProcessInfoParameter> list = new ArrayList<ProcessInfoParameter>();
		for(int i = 0; i < para.length; i++)
		{
			if(para[i].getParameterName().equals(MGenericExpFormat.COLUMNNAME_JP_GenericExpFormat_ID))
			{
				continue;
				
			}else{
				
				isContainParam = false;
				for(MProcessPara param :params)
				{
					if(para[i].getParameterName().equals(param.getColumnName()))
					{
						isContainParam = true;
						break;
					}
				}
				
				if(isContainParam) 
				{
					list.add (new ProcessInfoParameter(para[i].getParameterName (), para[i].getParameter(), para[i].getParameter_To(), para[i].getInfo(), para[i].getInfo_To()));
				}else {
					MProcessPara.validateUnknownParameter(m_MGenericExpFormat.getAD_Process_ID(), para[i]);
				}
			}
		}//for

		ProcessInfoParameter[] pars = new ProcessInfoParameter[list.size()];
		list.toArray(pars);
		pi.setParameter(pars);
		
		if(processUI == null)
			processUI = Env.getProcessUI(getCtx());

		return ProcessUtil.startJavaProcess(getCtx(), pi, Trx.get(get_TrxName(), true), false, processUI);
		
	}
	
	
	/**
	 * 
	 * Create Export File.
	 * 
	 * @return
	 * @throws Exception
	 */
	private File createExportFile() throws Exception
	{
		if(m_GEFRows == null)
			m_GEFRows = m_MGenericExpFormat.getRows();
		
		MGenericExpFormatRow m_GEFRow = null;
		MColumn m_Column = null;
		String stringData = null;
		
		//CharacterSet
		String characterSet = m_MGenericExpFormat.getCharacterSet();
		if(Util.isEmpty(characterSet))
			characterSet = "Shift_JIS";//Default
		
		//_Line Break Char
		String line_end = m_MGenericExpFormat.getJP_LineBreakChar();
		if(Util.isEmpty(line_end))
			line_end = Env.NL;
		
		if(line_end.equalsIgnoreCase("CL") || line_end.equalsIgnoreCase("\\r\\n"))
		{
			line_end = "\r\n";
		}else if(line_end.equalsIgnoreCase("CR") || line_end.equalsIgnoreCase("\\r")) {
			line_end = "\r";
		}else if(line_end.equalsIgnoreCase("LF") || line_end.equalsIgnoreCase("\\n")) {
			line_end = "\n";
		}
		
		String downLoadFileName = getFileName();
		String extension =  getFileExtension();
		
		String temp = System.getProperty("java.io.tmpdir");
		File downLoadFile = new File(temp+ File.separator + downLoadFileName + "." + extension);
		if(downLoadFile.exists())
		{
			try {
				Files.delete(downLoadFile.toPath());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		PrintWriter p_writer   = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(downLoadFile),characterSet)));
		
		
		//Create Header
		StringBuffer line = new StringBuffer();
		if(m_MGenericExpFormat.isHeaderRequiredJP())
		{
			String header = null;
			for(int i = 0;  i < m_GEFRows.length; i++ )
			{
				m_GEFRow = m_GEFRows[i];
				if(i > 0)
				{
					if(MGenericExpFormat.FORMATTYPE_CommaSeparated.equals(m_MGenericExpFormat.getFormatType())){//CSV
						line.append(",");
					}else if(MGenericExpFormat.FORMATTYPE_TabSeparated.equals(m_MGenericExpFormat.getFormatType())) { //Tab
						line.append("\t");
					}else if(MGenericExpFormat.FORMATTYPE_CustomSeparatorChar.equals(m_MGenericExpFormat.getFormatType())){//Custom
						line.append(m_MGenericExpFormat.getSeparatorChar());
					}else if(MGenericExpFormat.FORMATTYPE_FixedPosition.equals(m_MGenericExpFormat.getFormatType())){//Fix Position
						;//Nothing to do;
					}
				}
				
				header = m_GEFRow.getJP_Header();
				if(MGenericExpFormat.FORMATTYPE_FixedPosition.equals(m_MGenericExpFormat.getFormatType()))
						header = adjustFixedPosition(header, m_GEFRow);
				line.append(header);
			}
			p_writer.write(line.toString());
			p_writer.write(line_end);
		}
		
		
		//Create SQL
		String TableName = MTable.getTableName(getCtx(), m_MGenericExpFormat.getAD_Table_ID());
		StringBuilder sql = null;
		if(m_MGenericExpFormat.getAD_Process_ID() > 0)
		{
		
			sql = new StringBuilder("SELECT * FROM " )
											.append(TableName)
											.append(" WHERE AD_PInstance_ID = ? ");
			
			if(!Util.isEmpty(m_MGenericExpFormat.getWhereClause()))
				sql.append(" AND ").append(m_MGenericExpFormat.getWhereClause());
			
			if(!Util.isEmpty(m_MGenericExpFormat.getOrderByClause()))
				sql.append(" ORDER BY ").append(m_MGenericExpFormat.getOrderByClause());
		
		}else {
			
			//We have to Delete MPInstancePara of JP_GenericExpFormat_ID column to get the correct MQuery
			String paraTrxName = Trx.createTrxName("GEP-");
			MPInstance m_Pinstance = new MPInstance(getCtx(), getAD_PInstance_ID(), paraTrxName);
			int SeqNo = 0;
			try 
			{
				MPInstancePara[] m_Parameters = m_Pinstance.getParameters();
				for(MPInstancePara para : m_Parameters)
				{
					if(para.getParameterName().equals(MGenericExpFormat.COLUMNNAME_JP_GenericExpFormat_ID))
					{
						SeqNo = para.getSeqNo();
						para.delete(true);//Delete once and revert immediately
						break;
					}
				}
				
				Trx trx =Trx.get(paraTrxName, false);
				trx.commit();
				
				MQuery mQuery = MQuery.get(getCtx(), getAD_PInstance_ID(), TableName);
				
				//Create SQL
				sql = new StringBuilder("SELECT * FROM " )
						.append(TableName)
						.append(" WHERE AD_Client_ID = ? ");
				
				if(!Util.isEmpty(m_MGenericExpFormat.getWhereClause()))
					mQuery.addRestriction(m_MGenericExpFormat.getWhereClause());
				
				if(!Util.isEmpty(mQuery.getWhereClause()))
					sql.append(" AND ").append(mQuery.getWhereClause());
				
				if(!Util.isEmpty(m_MGenericExpFormat.getOrderByClause()))
					sql.append(" ORDER BY ").append(m_MGenericExpFormat.getOrderByClause());
			
			}catch (Exception e) {
				
				p_writer.close();
				throw e;
				
			}finally {
				
				//Revert MPInstancePara
				m_Pinstance.createParameter(SeqNo, MGenericExpFormat.COLUMNNAME_JP_GenericExpFormat_ID, m_MGenericExpFormat.getJP_GenericExpFormat_ID());
				Trx trx =Trx.get(paraTrxName, false);
				trx.commit();
				
			}
		}

		//Write row.
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int counter = 0;
		try
		{
			pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
			int index = 1;
			
			if(m_MGenericExpFormat.getAD_Process_ID() > 0)
				pstmt.setInt(index++, getAD_PInstance_ID());
			else
				pstmt.setInt(index++, getAD_Client_ID());
			
			rs = pstmt.executeQuery();
			while(rs.next())
			{
				counter++;
				if(processUI != null)
					processUI.statusUpdate(String.valueOf(counter));
				
				line = new StringBuffer();
				for(int i = 0;  i < m_GEFRows.length; i++ )
				{
					m_GEFRow = m_GEFRows[i];
					
					if(i > 0)
					{
						if(MGenericExpFormat.FORMATTYPE_CommaSeparated.equals(m_MGenericExpFormat.getFormatType()))//CSV
							line.append(",");
						else if(MGenericExpFormat.FORMATTYPE_TabSeparated.equals(m_MGenericExpFormat.getFormatType())) //Tab
							line.append("\t");
						else if(MGenericExpFormat.FORMATTYPE_CustomSeparatorChar.equals(m_MGenericExpFormat.getFormatType()))//Custom
							line.append(m_MGenericExpFormat.getSeparatorChar());
						else if(MGenericExpFormat.FORMATTYPE_FixedPosition.equals(m_MGenericExpFormat.getFormatType()))//Fix Position
							;//Nothing to do;
					}
					
					if(MGenericExpFormatRow.JP_EXPORTTYPE_Variable.equals(m_GEFRow.getJP_ExportType()))//Variable = Column
					{
						m_Column = MColumn.get(m_GEFRow.getAD_Column_ID());
						
						//Format String
						if(!Util.isEmpty(m_GEFRow.getDataFormat()))
						{
							if(DisplayType.isDate(m_Column.getAD_Reference_ID()))
							{
								Timestamp ts = rs.getTimestamp(m_Column.getColumnName());
								String format = m_GEFRow.getDataFormat();
								SimpleDateFormat sdf = new SimpleDateFormat(format);
								stringData= sdf.format(ts);
								 
							}else if(DisplayType.isNumeric(m_Column.getAD_Reference_ID())){
								
								BigDecimal bd = rs.getBigDecimal(m_Column.getColumnName());
								String format = m_GEFRow.getDataFormat();
								DecimalFormat df = new DecimalFormat(format);
								stringData= df.format(bd);
								
							}else {
								
								stringData = rs.getString(m_Column.getColumnName());
								
							}
							 
							 
						}else {
							
							stringData = rs.getString(m_Column.getColumnName());
						
						}
						
						//Data Processing
						if(stringData == null)
							stringData = "";
						
						//Replace
						MGenericExpFormatReplace[] replaces = m_GEFRow.getReplaces(false);
						for(MGenericExpFormatReplace m_replace :replaces)
						{
							if(MGenericExpFormatReplace.JP_REPLACEMETHOD_String.equals(m_replace.getJP_ReplaceMethod()))
							{
								stringData = stringData.replace(m_replace.getJP_TargetString(), Util.isEmpty(m_replace.getJP_ReplacementString())? "" : m_replace.getJP_ReplacementString());
								
							}else if(MGenericExpFormatReplace.JP_REPLACEMETHOD_Regex.equals(m_replace.getJP_ReplaceMethod())) {
								
								stringData = stringData.replaceAll(m_replace.getJP_TargetString(), Util.isEmpty(m_replace.getJP_ReplacementString())? "" : m_replace.getJP_ReplacementString());
							}
						}
							
						//String length adjustment
						if(m_GEFRow.getFieldLength() > 0)
						{
							if(stringData.length() > m_GEFRow.getFieldLength())
								stringData = stringData.substring(0,  m_GEFRow.getFieldLength());
						}
						
						//Escap Delimiter.
						if(MGenericExpFormat.FORMATTYPE_CommaSeparated.equals(m_MGenericExpFormat.getFormatType()))//CSV
						{
							if(m_GEFRow.isEscapSeparatorCharJP() && stringData.contains(","))
								stringData = stringData.replace("," , "\\,");//TODO エスケープ処理になっているか要確認
							
						}else if(MGenericExpFormat.FORMATTYPE_TabSeparated.equals(m_MGenericExpFormat.getFormatType())){ //Tab
							
							
							if(m_GEFRow.isEscapSeparatorCharJP() && stringData.contains("\t"))
								stringData = stringData.replace("\t" , "\\t");//TODO エスケープ処理になっているか要確認
							
						}else if(MGenericExpFormat.FORMATTYPE_CustomSeparatorChar.equals(m_MGenericExpFormat.getFormatType())){ //Custom
							
							String regex =  "["+m_MGenericExpFormat.getSeparatorChar()+ "]";
							if(m_GEFRow.isEscapSeparatorCharJP() && stringData.matches(regex))
								stringData = stringData.replaceAll(regex , "\\"+regex );//TODO エスケープ処理になっているか要確認
							
						}else if(MGenericExpFormat.FORMATTYPE_FixedPosition.equals(m_MGenericExpFormat.getFormatType())){ //Fix Position
							
							;//Nothing to do;
							
						}
						
						//Escap enclosing char.
						if(MGenericExpFormat.FORMATTYPE_FixedPosition.equals(m_MGenericExpFormat.getFormatType()))
						{
							;//Nothing to do
							
						}else {
							
							if(MGenericExpFormat.JP_ENCLOSINGCHAR_Nothing.equals(m_MGenericExpFormat.getJP_EnclosingChar()))
							{
								;//Nothing to do
								
							}else if(m_GEFRow.isEscapEnclosingCharJP()) {
								
								if(MGenericExpFormat.JP_ENCLOSINGCHAR_DoubleQuotes.equals(m_MGenericExpFormat.getJP_EnclosingChar()))
								{
									if(stringData.contains("\""))
										stringData = stringData.replace("\"", "\\\"");
									
								}else {
									
									if(stringData.contains(m_MGenericExpFormat.getJP_EnclosingChar()))
										stringData = stringData.replace(m_MGenericExpFormat.getJP_EnclosingChar(), "\\"+m_MGenericExpFormat.getJP_EnclosingChar());
									
								}
							}
						}
						
						if(MGenericExpFormat.FORMATTYPE_FixedPosition.equals(m_MGenericExpFormat.getFormatType()))
						{
							stringData = adjustFixedPosition(stringData , m_GEFRow);
							
						}else {
							
							//Enclosing
							if(MGenericExpFormat.JP_ENCLOSINGCHAR_DoubleQuotes.equals(m_MGenericExpFormat.getJP_EnclosingChar()))
							{
								if(m_GEFRow.isEncloseWithEnclosingCharaJP())
									stringData = "\""+stringData+"\"";
								
							}else if(MGenericExpFormat.JP_ENCLOSINGCHAR_Nothing.equals(m_MGenericExpFormat.getJP_EnclosingChar())) {
								
								;//Nothing to do.
								
							}else {
								
								if(m_GEFRow.isEncloseWithEnclosingCharaJP())
								{
									String enclosingChar = m_MGenericExpFormat.getJP_EnclosingChar();
									stringData = enclosingChar +stringData+ enclosingChar;
								}
							}	
						}
						
						line.append(stringData);
						
						
					}else if(MGenericExpFormatRow.JP_EXPORTTYPE_Constant.equals(m_GEFRow.getJP_ExportType()) ){//Constant
						
						if(MGenericExpFormat.FORMATTYPE_FixedPosition.equals(m_MGenericExpFormat.getFormatType()))
						{
							String value = adjustFixedPosition(m_GEFRow.getConstantValue() , m_GEFRow);
							line.append(value);
							
						}else {
							
							if(!Util.isEmpty(m_GEFRow.getConstantValue()))
							{
								stringData = m_GEFRow.getConstantValue();
								
								//Enclosing
								if(MGenericExpFormat.JP_ENCLOSINGCHAR_DoubleQuotes.equals(m_MGenericExpFormat.getJP_EnclosingChar()))
								{
									if(m_GEFRow.isEncloseWithEnclosingCharaJP())
										stringData = "\""+stringData+"\"";
									
								}else if(MGenericExpFormat.JP_ENCLOSINGCHAR_Nothing.equals(m_MGenericExpFormat.getJP_EnclosingChar())) {
									
									;//Nothing to do.
									
								}else {
									
									if(m_GEFRow.isEncloseWithEnclosingCharaJP())
									{
										String enclosingChar = m_MGenericExpFormat.getJP_EnclosingChar();
										stringData = enclosingChar +stringData+ enclosingChar;
									}
								}
								
								line.append(stringData);
							}
						}
						
					}else if(MGenericExpFormatRow.JP_EXPORTTYPE_Blank.equals(m_GEFRow.getJP_ExportType()) ) {//Blank
						
						if(MGenericExpFormat.FORMATTYPE_FixedPosition.equals(m_MGenericExpFormat.getFormatType()))
						{
							String value = adjustFixedPosition("" , m_GEFRow);
							line.append(value);
							
						}else {
							
							//Enclosing
							if(MGenericExpFormat.JP_ENCLOSINGCHAR_DoubleQuotes.equals(m_MGenericExpFormat.getJP_EnclosingChar()))
							{
								if(m_GEFRow.isEncloseWithEnclosingCharaJP())
									stringData = "\"\"";
								
							}else if(MGenericExpFormat.JP_ENCLOSINGCHAR_Nothing.equals(m_MGenericExpFormat.getJP_EnclosingChar())) {
								
								;//Nothing to do.
								
							}else {
								
								if(m_GEFRow.isEncloseWithEnclosingCharaJP())
								{
									String enclosingChar = m_MGenericExpFormat.getJP_EnclosingChar();
									stringData = enclosingChar + enclosingChar;
								}
							}
						}
					}
				}
				
				p_writer.write(line.toString());
				p_writer.write(line_end);
			}
			p_writer.flush();
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, e.toString());
			throw e;
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
			p_writer.close();
		}
		
		return downLoadFile;
	}
	
	private String adjustFixedPosition(String value , MGenericExpFormatRow m_GEFRow)
	{
		if(value == null)
			value ="";
		
		int valueLength = value.length();
		int fieldLength = m_GEFRow.getFieldLength();
		
		if(valueLength == fieldLength)
		{
			;//Nothing to do
			
		}else if(valueLength > fieldLength) {
			
			value = value.substring(0, fieldLength);
		
		}else {
			
			String paddingChar = null;
			if(MGenericExpFormatRow.JP_PADDINGCHAR_Space.equals(m_GEFRow.getJP_PaddingChar()))
			{
				paddingChar =  " ";
				
			}else if(MGenericExpFormatRow.JP_PADDINGCHAR_Zero0.equals(m_GEFRow.getJP_PaddingChar())) {
				
				paddingChar = "0";
				
			}else if(MGenericExpFormatRow.JP_PADDINGCHAR_Asterisk.equals(m_GEFRow.getJP_PaddingChar())) {
				
				paddingChar = "*";
				
			}else {
			
				paddingChar = m_GEFRow.getJP_PaddingChar();
			}
			
			int diff = fieldLength - valueLength;
			String paddingString = "";
			for(int i = 0; i < diff; i++)
			{
				paddingString = paddingString + paddingChar;
			}
			
			if(MGenericExpFormatRow.JP_PADDINGTYPE_RightJustified.equals(m_GEFRow.getJP_PaddingType()))
			{
				value = paddingString + value;
				
			}else if(MGenericExpFormatRow.JP_PADDINGTYPE_LeftJustified.equals(m_GEFRow.getJP_PaddingType())) {
				
				value = value + paddingString;
			}
			
		}
		
		return value;
	}
	
	private String fileName = null;
	
	private String getFileName()
	{
		if(fileName != null)
			return fileName;
		
		if(Util.isEmpty(m_MGenericExpFormat.getFileNamePattern())) 
		{
			fileName = m_MGenericExpFormat.getValue();
		} else {
			fileName = FileUtil.parseTitle(getCtx(), m_MGenericExpFormat.getFileNamePattern(), MPInstance.Table_ID ,getAD_PInstance_ID(), 0, get_TrxName());
		}
		
		return fileName;
	}
	
	private String fileExtension = null;
	
	private String getFileExtension()
	{
		if(fileExtension != null)
			return fileExtension;
		
		if(Util.isEmpty(m_MGenericExpFormat.getJP_FileExtension())) 
		{
			fileExtension = "txt";
		} else {
			fileExtension = m_MGenericExpFormat.getJP_FileExtension();
		}
		
		return fileExtension;
	}
	
	
	private I_GenericExportStorageProvider getExportFileStore() 
	{
		I_GenericExportStorageProvider store = Service.locator().locate(I_GenericExportStorageProvider.class).getService();
		if (store == null) {
			throw new AdempiereException("Not found : Generic Export Storage Provider ");
		}
		
		return store;
	}
}


