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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Level;

import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.MBankData;

/**
 * JPIERE-0594 : Default Import Process of Bank Data ver.3
 * 全銀 振込入金通知[CSV]フォーマット インポート
 * 
 * @author 
 *
 */
public class DefaultBankDataImport3 extends SvrProcess{
		
	// Header Data Index
	private static final int     INDEX_HEADER_JP_BankDataType_Header = 0;
	private static final int     INDEX_HEADER_JP_BankDataClassification = 1;
	private static final int     INDEX_HEADER_JP_BankDataCodeType = 2;
	private static final int     INDEX_HEADER_JP_BankDataCreated = 3;
	private static final int     INDEX_HEADER_JP_BankDataFrom = 4;
	private static final int     INDEX_HEADER_JP_BankDataTo = 5;
	private static final int     INDEX_HEADER_RoutingNo = 6;
	private static final int     INDEX_HEADER_JP_BankName_kana = 7;
	private static final int     INDEX_HEADER_JP_BranchCode = 8;
	private static final int     INDEX_HEADER_JP_BranchName_Kana = 9;	
	private static final int     INDEX_HEADER_JP_BankAccountType = 10;
	private static final int     INDEX_HEADER_AccountNo = 11;	
	private static final int     INDEX_HEADER_JP_RequesterName = 12;
	@SuppressWarnings("unused")
	private static final int     INDEX_HEADER_DUMMY = 13;

	// Line Data Index
	private static final int     INDEX_LINE_JP_BankDataType_Line = 0;
	private static final int     INDEX_LINE_JP_BankData_ReferenceNo = 1;
	private static final int     INDEX_LINE_JP_BankData_Kanjyoubi = 2;
	private static final int     INDEX_LINE_JP_BankData_Kisanbi = 3;
	private static final int     INDEX_LINE_StmtAmt = 4;
	private static final int     INDEX_LINE_JP_BankData_Taten_StmtAmt = 5;
	private static final int     INDEX_LINE_JP_RequesterCode = 6;
	private static final int     INDEX_LINE_JP_A_Name_Kana = 7;
	private static final int     INDEX_LINE_JP_BankName_Kana = 8;	
	private static final int     INDEX_LINE_JP_BranchName_Kana = 9;
	private static final int     INDEX_LINE_JP_BankData_Torikeshikubun = 10;
	private static final int     INDEX_LINE_JP_BankDate_EDI_Info = 11;
	@SuppressWarnings("unused")
	private static final int     INDEX_LINE_DUMMY = 12;
	
	// Footer Data Index
	private static final int     INDEX_FOOTER_JP_BankDataType_Footer = 0;
	private static final int     INDEX_FOOTER_NumLines = 1;
	private static final int     INDEX_FOOTER_TotalAmt = 2;
	private static final int     INDEX_FOOTER_JP_BankData_TorikeshiNum = 3;
	private static final int     INDEX_FOOTER_JP_BankData_TorikeshiAmt = 4;
	@SuppressWarnings("unused")
	private static final int     INDEX_FOOTER_DUMMY = 5;
	
	// Data Diff
	private static final String     DATADIFF_Header = "1";
	private static final String     DATADIFF_Line = "2";
	private static final String     DATADIFF_Trailer = "8";
	@SuppressWarnings("unused")
	private static final String     DATADIFF_End = "9";
	
	/*
	 * Parameters
	 */

	/**	Client to be imported to		*/
	private int				p_AD_Client_ID = 0;

	//Bank Data
	private int p_JP_BankData_ID = 0;
	private MBankData m_BankData = null;

	//Bank Data File
	private String p_BankDataFile = null;

	@Override
	protected void prepare() 
	{
		
		p_AD_Client_ID = getAD_Client_ID();
		ProcessInfoParameter[] para = getParameter();
		for(int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (name.equals(""))
				;
			else if(name.equals("JP_BankDataFile"))
				p_BankDataFile = para[i].getParameterAsString();
			else
				log.log(Level.SEVERE, "Unknown Parameter :" + name);
			
			p_JP_BankData_ID = getRecord_ID();
			m_BankData = new MBankData(getCtx(),p_JP_BankData_ID,get_TrxName());

		}
	}

	@Override
	protected String doIt() throws Exception {
		StringBuffer err = new StringBuffer();
		
		if (p_BankDataFile == null || p_BankDataFile.length() == 0)
			return Msg.getMsg(getCtx(), "File invalid");//File invalid
		try
		{
			File file = new File(p_BankDataFile);
			
			//  Must be a file
			if (file.isDirectory())
			{
				err.append("Path is not a file. - " + file.getAbsolutePath());
				log.log(Level.SEVERE, err.toString());
				return Msg.getMsg(getCtx(), "Path is not a file");//File path is not a file
			}
			//  if exists
			if (!file.exists())
			{
				err.append("No file in the folder. - " + file.getAbsolutePath());
				log.log(Level.SEVERE, err.toString());
				return Msg.getMsg(getCtx(), "No file in the folder");//No file in the Folder
			}
		
			FileInputStream fs = null;
			InputStreamReader isr = null;
			BufferedReader br = null;
			
			try
			{
				// read header
				fs = new FileInputStream(file);
				//isr = new InputStreamReader(fs, "UTF8");
				isr = new InputStreamReader(fs, "SJIS");
				br = new BufferedReader(isr);
				
				String tmplinedata;
        		StringBuilder sql = null;
        		int lineNo = 10;
        		int no = 0;

        		tmplinedata = br.readLine();
        		
	            while (tmplinedata != null)
	            {
	            	String str = tmplinedata;
	            	String dataDiff = str.substring(0, 1);
	            	
	            	// Header Record
	            	if (dataDiff.equals(DATADIFF_Header))
	            	{
	            		
	            		String[] header = parseCSV(str);
	            		
	            		sql = new StringBuilder ("UPDATE JP_BankData ")
	            				.append("SET ")
	            						.append(" Updated = COALESCE (Updated, SysDate),")
	            						.append(" UpdatedBy = ").append(Env.getAD_User_ID(getCtx())).append(",")	
	            						.append(" JP_BankDataType_Header = '").append(header[INDEX_HEADER_JP_BankDataType_Header]).append("',")		//1
	            						.append(" JP_BankDataClassification = '").append(header[INDEX_HEADER_JP_BankDataClassification]).append("',")	//2
	            						.append(" JP_BankDataCodeType = '").append(header[INDEX_HEADER_JP_BankDataCodeType]).append("',")				//3
	            						.append(" JP_BankDataCreated = '").append(header[INDEX_HEADER_JP_BankDataCreated]).append("',")				//4
	            						.append(" JP_BankDataFrom = '").append(header[INDEX_HEADER_JP_BankDataFrom]).append("',")						//5
	            						.append(" JP_BankDataTo = '").append(header[INDEX_HEADER_JP_BankDataTo]).append("',")							//6
	            						.append(" RoutingNo = '").append(header[INDEX_HEADER_RoutingNo]).append("',")									//7
	            						.append(" JP_BankName_Kana = '").append(header[INDEX_HEADER_JP_BankName_kana]).append("',")						//8
	            						.append(" JP_BranchCode = '").append(header[INDEX_HEADER_JP_BranchCode]).append("',")							//9
	            						.append(" JP_BranchName_Kana = '").append(header[INDEX_HEADER_JP_BranchName_Kana]).append("',")				//10
	            						.append(" JP_BankAccountType = '").append(header[INDEX_HEADER_JP_BankAccountType]).append("',")				//11
	            						.append(" AccountNo = '").append(header[INDEX_HEADER_AccountNo]).append("',")									//12
	            						.append(" JP_RequesterName = '").append(header[INDEX_HEADER_JP_RequesterName]).append("' ")						//13
	            						.append(" WHERE JP_BankData_ID =").append(p_JP_BankData_ID);
	            		no = DB.executeUpdateEx(sql.toString(), get_TrxName());
	            		if (log.isLoggable(Level.FINE)) log.fine("Reset=" + no);
	            	}
	            	
	            	
	            	// Line Record
	            	else if (dataDiff.equals(DATADIFF_Line))
	            	{	            		
	            		String[] line = parseCSV(str);
	            		
	            		sql = new StringBuilder ("INSERT INTO JP_BankDataLine ")
	            				.append(" (AD_Client_ID, AD_Org_ID, IsActive, Created, CreatedBy, Updated, UpdatedBy, ")
	            				.append(" JP_BankDataType_Line, JP_BankData_ReferenceNo, JP_BankData_Kanjyoubi, JP_BankData_Kisanbi, StmtAmt,")										//1 - 5
	            				.append(" JP_BankData_Taten_StmtAmt, JP_RequesterCode, JP_A_Name_Kana, JP_BankName_kana, JP_BranchName_Kana,") 										//6 - 10
	            				.append(" JP_BankData_Torikeshikubun, JP_BankData_EDI_Info, ") 																						//11 - 12
	            				.append(" Line, JP_BankData_ID, JP_BankDataLine_ID, JP_BankDataLine_UU, StatementLineDate, DateAcct, ValutaDate, TrxAmt ) ")
	            				
	            				.append(" VALUES (")
	            				.append(p_AD_Client_ID).append(",")																													//AD_Client_ID
	            				.append(m_BankData.getAD_Org_ID()).append(",")																										//AD_Org_ID
	            				.append(" 'Y',")																																	//IsActive
	            				.append(" SysDate,")																																//Created
	            				.append(Env.getAD_User_ID(getCtx())).append(",")																									//CreatedBy
	            				.append(" SysDate,")																																//Updated
	            				.append(Env.getAD_User_ID(getCtx())).append(",")																									//UpdatedBy
	            				
	            				.append(" '").append(line[INDEX_LINE_JP_BankDataType_Line]).append("',")																			//1
	            				.append(" '").append(line[INDEX_LINE_JP_BankData_ReferenceNo]).append("',")																		//2
	            				.append(" '").append(line[INDEX_LINE_JP_BankData_Kanjyoubi]).append("',")																			//3
	            				.append(" '").append(line[INDEX_LINE_JP_BankData_Kisanbi]).append("',")																			//4	            				
	            				.append(" ").append(line[INDEX_LINE_StmtAmt]).append(",")																							//5
	            				.append(" '").append(line[INDEX_LINE_JP_BankData_Taten_StmtAmt]).append("',")																		//6
	            				.append(" '").append(line[INDEX_LINE_JP_RequesterCode]).append("',")																				//7
	            				.append(" '").append(line[INDEX_LINE_JP_A_Name_Kana]).append("',")																					//8
	            				.append(" '").append(line[INDEX_LINE_JP_BankName_Kana]).append("',")																				//9
	            				.append(" '").append(line[INDEX_LINE_JP_BranchName_Kana]).append("',")																				//10
	            				.append(" '").append(line[INDEX_LINE_JP_BankData_Torikeshikubun]).append("',")																	//11
	            				;
	            		
	            				if(line.length == 11)
	            					sql.append(" '',");																																	//12
	            				else
	            					sql.append(" '").append(line[INDEX_LINE_JP_BankDate_EDI_Info]).append("',");																	//12
	            				
	            				sql.append(" ").append(lineNo).append(",")																												//Line
	            				.append(" ").append(p_JP_BankData_ID).append(",")																										//JP_BankDate_ID
	            				.append(" ").append(DB.getNextID(p_AD_Client_ID, "JP_BankDataLine", get_TrxName())).append(",")														//JP_BankdateLine_ID
	            				.append(" generate_uuid(), ")																															//JP_BankDateLine_UU
	            				.append(" ").append("TO_DATE('").append(m_BankData.getStatementDate().toString().substring(0, 4))														//StatementLineDate
	            													.append(line[INDEX_LINE_JP_BankData_Kanjyoubi].substring(2, 6)).append(" 00:00:00','YYYYMMDD HH24:MI:SS'),")
	            				.append(" ").append("TO_DATE('").append(m_BankData.getDateAcct().toString().substring(0, 4))															//DateAcct
	            													.append(line[INDEX_LINE_JP_BankData_Kanjyoubi].substring(2, 6)).append(" 00:00:00','YYYYMMDD HH24:MI:SS'),")
	            				.append(" ").append("TO_DATE('").append(m_BankData.getDateAcct().toString().substring(0, 4))															//ValutaDate
	            													.append(line[INDEX_LINE_JP_BankData_Kanjyoubi].substring(2, 6)).append(" 00:00:00','YYYYMMDD HH24:MI:SS'),")
	            				.append(" ").append(line[INDEX_LINE_StmtAmt])																										//trx Amt
	            				.append(");");
	            		no = DB.executeUpdateEx(sql.toString(), get_TrxName());
	            		if (log.isLoggable(Level.FINE)) log.fine("Reset=" + no);
	            		
	            		lineNo+=10;
	            	}
	            	
	            	// Trailer(Footer) Record
	            	else if (dataDiff.equals(DATADIFF_Trailer))
	            	{
	            		String[] footer = parseCSV(str);
	            		
	            		sql = new StringBuilder ("UPDATE JP_BankData ")
	            				.append("SET")
	            						.append(" JP_BankDataType_Footer = '").append(footer[INDEX_FOOTER_JP_BankDataType_Footer]).append("',")									//1
	            						.append(" NumLines = '").append(footer[INDEX_FOOTER_NumLines]).append("',")																	//2
	            						.append(" TotalAmt = '").append(footer[INDEX_FOOTER_TotalAmt]).append("',")																	//3
	            						.append(" JP_BankData_TorikeshiNum = '").append(footer[INDEX_FOOTER_JP_BankData_TorikeshiNum]).append("',")								//4
	            						.append(" JP_BankData_TorikeshiAmt = '").append(footer[INDEX_FOOTER_JP_BankData_TorikeshiAmt]).append("'")								//5
	            						.append(" WHERE JP_BankData_ID =").append(p_JP_BankData_ID);
	            		no = DB.executeUpdateEx(sql.toString(), get_TrxName());
	            		if (log.isLoggable(Level.FINE)) log.fine("Reset=" + no);
	            	}
	            	
	            	// End Record is not process.
            		tmplinedata = br.readLine();

	            }
			}
			catch (Exception e)
			{
				System.out.println(e);
				log.log(Level.SEVERE, e.getLocalizedMessage(), e);
				return Msg.getMsg(getCtx(), "Error") + " : " +  e.toString();
			}
			finally
			{
				fs.close();
				isr.close();
				br.close();
			}
		}
		catch (Exception e)
		{
			System.out.println(e);
			log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			return Msg.getMsg(getCtx(), "Error") + " : " +  e.toString();
		}
		return Msg.getMsg(getCtx(), "ProcessOK") + " : WPayImportProcess()";
	}
	
	
	private String[] parseCSV(String str)
	{
		if(Util.isEmpty(str))
			return null;
		
		String[] returnStrings  = null;
		char delimiter = ',';
		
		if(str.contains("\""))
		{
			final char QUOTE = '"';
			ArrayList<String> list = new ArrayList<String>();
			StringBuilder data = new StringBuilder();
			boolean ignorDelimiter = false;
			
			for(int i = 0; i < str.length(); i++)
			{
				if (str.charAt(i) == delimiter && !ignorDelimiter)
				{
					if(data.length() == 0)
					{
						list.add("");
						
					}else {
						
						list.add(data.toString());
						data = new StringBuilder();
					}
					
				}else {
					
					if (str.charAt(i) == QUOTE && data.length() == 0 && !ignorDelimiter)
					{
						ignorDelimiter = true;
						continue;
					}else if (str.charAt(i) == QUOTE ){
						
						ignorDelimiter = false;
						continue;
					}
					
					data.append(str.charAt(i));
				}
			}
			
			//Last Data
			if(data.length() == 0)
			{
				list.add("");
			}else {
				list.add(data.toString());
			}
			
			returnStrings = new String[list.size()];
			list.toArray(returnStrings);
			return returnStrings;
			
		}else {
			returnStrings  = str.split(",");
		}
		
		return returnStrings;
	}
}
