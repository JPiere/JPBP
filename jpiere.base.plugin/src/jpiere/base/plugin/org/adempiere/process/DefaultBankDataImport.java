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
import java.util.logging.Level;

import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Msg;

import jpiere.base.plugin.org.adempiere.model.MBankData;

/**
 * JPIERE-0303 : Default Import Process of Bank Data(Up to Ver8.2)
 * 全銀　振込データ[固定長]フォーマット インポート
 * 
 * 
 * @author 
 *
 */
public class DefaultBankDataImport extends SvrProcess{
		
	// Header Data Index
	private static final int     INDEX_HEADER_JP_BankDataType_Header = 0;
	private static final int     INDEX_HEADER_JP_BankDataClassification = 1;
	private static final int     INDEX_HEADER_JP_BankDataCodeType = 2;
	private static final int     INDEX_HEADER_JP_RequesterCode = 3;
	private static final int     INDEX_HEADER_JP_RequesterName = 4;
	private static final int     INDEX_HEADER_PayDate = 5;
	private static final int     INDEX_HEADER_RoutingNo = 6;
	private static final int     INDEX_HEADER_JP_BankName_kana = 7;
	private static final int     INDEX_HEADER_JP_BranchCode = 8;
	private static final int     INDEX_HEADER_JP_BranchName_Kana = 9;
	private static final int     INDEX_HEADER_JP_BankAccountType = 10;
	private static final int     INDEX_HEADER_AccountNo = 11;

	// Line Data Index
	private static final int     INDEX_LINE_JP_BankDataType_Line = 0;
	private static final int     INDEX_LINE_RoutingNo = 1;
	private static final int     INDEX_LINE_JP_BankName_Kana = 2;
	private static final int     INDEX_LINE_JP_BranchCode = 3;
	private static final int     INDEX_LINE_JP_BranchName_Kana = 4;
	private static final int     INDEX_LINE_JP_ClearingHouse = 5;
	private static final int     INDEX_LINE_JP_BankAccountType = 6;
	private static final int     INDEX_LINE_AccountNo = 7;
	private static final int     INDEX_LINE_JP_A_Name_Kana = 8;
	private static final int     INDEX_LINE_StmtAmt = 9;
	private static final int     INDEX_LINE_JP_BankDataNewCode = 10;
	private static final int     INDEX_LINE_JP_BankDataCustomerCode1 = 11;
	private static final int     INDEX_LINE_JP_BankDataCustomerCode2 = 12;

	// Footer Data Index
	private static final int     INDEX_FOOTER_JP_BankDataType_Footer = 0;
	private static final int     INDEX_FOOTER_NumLines = 1;
	private static final int     INDEX_FOOTER_TotalAmt = 2;
	
	// Import Data Columns
	private static final int     NUMBER_HEADER_Columns = 12;
	private static final int     NUMBER_LINE_Columns = 13;
	private static final int     NUMBER_FOOTER_Columns = 3;
	
	// Import Data Digit Number
	private static final int     HEADER_JP_BankDataType_Header = 1;
	private static final int     HEADER_JP_BankDataClassification = 2;
	private static final int     HEADER_JP_BankDataCodeType = 1;
	private static final int     HEADER_JP_RequesterCode = 10;
	private static final int     HEADER_JP_RequesterName = 40;
	private static final int     HEADER_PayDate = 4;
	private static final int     HEADER_RoutingNo = 4;
	private static final int     HEADER_JP_BankName_kana = 15;
	private static final int     HEADER_JP_BranchCode = 3;
	private static final int     HEADER_JP_BranchName_Kana = 15;
	private static final int     HEADER_JP_BankAccountType = 1;
	private static final int     HEADER_AccountNo = 7;
	
	private static final int     LINE_JP_BankDataType_Line = 1;
	private static final int     LINE_RoutingNo = 4;
	private static final int     LINE_JP_BankName_Kana = 15;
	private static final int     LINE_JP_BranchCode = 3;
	private static final int     LINE_JP_BranchName_Kana = 15;
	private static final int     LINE_JP_ClearingHouse = 4;
	private static final int     LINE_JP_BankAccountType = 1;
	private static final int     LINE_AccountNo = 7;
	private static final int     LINE_JP_A_Name_Kana = 30;
	private static final int     LINE_StmtAmt = 10;
	private static final int     LINE_JP_BankDataNewCode = 1;
	private static final int     LINE_JP_BankDataCustomerCode1 = 10;
	private static final int     LINE_JP_BankDataCustomerCode2 = 10;
	
	private static final int     FOOTER_JP_BankDataType_Footer = 1;
	private static final int     FOOTER_NumLines = 6;
	private static final int     FOOTER_TotalAmt = 12;

	// Import Data Position
	private static final int     START_POSITION_HEADER_JP_BankDataType_Header = 0;
	private static final int     START_POSITION_HEADER_JP_BankDataClassification = START_POSITION_HEADER_JP_BankDataType_Header + HEADER_JP_BankDataType_Header;
	private static final int     START_POSITION_HEADER_JP_BankDataCodeType = START_POSITION_HEADER_JP_BankDataClassification + HEADER_JP_BankDataClassification;
	private static final int     START_POSITION_HEADER_JP_RequesterCode = START_POSITION_HEADER_JP_BankDataCodeType + HEADER_JP_BankDataCodeType;
	private static final int     START_POSITION_HEADER_JP_RequesterName = START_POSITION_HEADER_JP_RequesterCode + HEADER_JP_RequesterCode;
	private static final int     START_POSITION_HEADER_PayDate = START_POSITION_HEADER_JP_RequesterName + HEADER_JP_RequesterName;
	private static final int     START_POSITION_HEADER_RoutingNo = START_POSITION_HEADER_PayDate + HEADER_PayDate;
	private static final int     START_POSITION_HEADER_JP_BankName_Kana = START_POSITION_HEADER_RoutingNo + HEADER_RoutingNo;
	private static final int     START_POSITION_HEADER_JP_BranchCode = START_POSITION_HEADER_JP_BankName_Kana + HEADER_JP_BankName_kana;
	private static final int     START_POSITION_HEADER_JP_BranchName_Kana = START_POSITION_HEADER_JP_BranchCode + HEADER_JP_BranchCode;
	private static final int     START_POSITION_HEADER_JP_BankAccountType = START_POSITION_HEADER_JP_BranchName_Kana + HEADER_JP_BranchName_Kana;
	private static final int     START_POSITION_HEADER_AccountNo = START_POSITION_HEADER_JP_BankAccountType + HEADER_JP_BankAccountType;
	
	private static final int     END_POSITION_HEADER_JP_BankDataType_Header = START_POSITION_HEADER_JP_BankDataType_Header + HEADER_JP_BankDataType_Header;
	private static final int     END_POSITION_HEADER_JP_BankDataClassification = START_POSITION_HEADER_JP_BankDataClassification + HEADER_JP_BankDataClassification;
	private static final int     END_POSITION_HEADER_JP_BankDataCodeType = START_POSITION_HEADER_JP_BankDataCodeType + HEADER_JP_BankDataCodeType;
	private static final int     END_POSITION_HEADER_JP_RequesterCode = START_POSITION_HEADER_JP_RequesterCode + HEADER_JP_RequesterCode;
	private static final int     END_POSITION_HEADER_JP_RequesterName = START_POSITION_HEADER_JP_RequesterName + HEADER_JP_RequesterName;
	private static final int     END_POSITION_HEADER_PayDate = START_POSITION_HEADER_PayDate + HEADER_PayDate;
	private static final int     END_POSITION_HEADER_RoutingNo = START_POSITION_HEADER_RoutingNo + HEADER_RoutingNo;
	private static final int     END_POSITION_HEADER_JP_BankName_Kana = START_POSITION_HEADER_JP_BankName_Kana + HEADER_JP_BankName_kana;
	private static final int     END_POSITION_HEADER_JP_BranchCode = START_POSITION_HEADER_JP_BranchCode + HEADER_JP_BranchCode;
	private static final int     END_POSITION_HEADER_JP_BranchName_Kana = START_POSITION_HEADER_JP_BranchName_Kana + HEADER_JP_BranchName_Kana;
	private static final int     END_POSITION_HEADER_JP_BankAccountType = START_POSITION_HEADER_JP_BankAccountType + HEADER_JP_BankAccountType;
	private static final int     END_POSITION_HEADER_AccountNo = START_POSITION_HEADER_AccountNo + HEADER_AccountNo;
	
	private static final int     START_POSITION_LINE_JP_BankDataType_Line = 0;
	private static final int     START_POSITION_LINE_RoutingNo = START_POSITION_LINE_JP_BankDataType_Line + LINE_JP_BankDataType_Line;
	private static final int     START_POSITION_LINE_JP_BankName_Kana = START_POSITION_LINE_RoutingNo + LINE_RoutingNo;
	private static final int     START_POSITION_LINE_JP_BranchCode = START_POSITION_LINE_JP_BankName_Kana + LINE_JP_BankName_Kana;
	private static final int     START_POSITION_LINE_JP_BranchName_Kana = START_POSITION_LINE_JP_BranchCode + LINE_JP_BranchCode;
	private static final int     START_POSITION_LINE_JP_ClearingHouse = START_POSITION_LINE_JP_BranchName_Kana + LINE_JP_BranchName_Kana;
	private static final int     START_POSITION_LINE_JP_BankAccountType = START_POSITION_LINE_JP_ClearingHouse + LINE_JP_ClearingHouse;
	private static final int     START_POSITION_LINE_AccountNo = START_POSITION_LINE_JP_BankAccountType + LINE_JP_BankAccountType;
	private static final int     START_POSITION_LINE_JP_A_Name_Kana = START_POSITION_LINE_AccountNo + LINE_AccountNo;
	private static final int     START_POSITION_LINE_StmtAmt = START_POSITION_LINE_JP_A_Name_Kana + LINE_JP_A_Name_Kana;
	private static final int     START_POSITION_LINE_JP_BankDataNewCode =START_POSITION_LINE_StmtAmt + LINE_StmtAmt;
	private static final int     START_POSITION_LINE_JP_BankDataCustomerCode1 = START_POSITION_LINE_JP_BankDataNewCode + LINE_JP_BankDataNewCode;
	private static final int     START_POSITION_LINE_JP_BankDataCustomerCode2 = START_POSITION_LINE_JP_BankDataCustomerCode1 + LINE_JP_BankDataCustomerCode1;
			
			
	private static final int     END_POSITION_LINE_JP_BankDataType_Line = START_POSITION_LINE_JP_BankDataType_Line + LINE_JP_BankDataType_Line;
	private static final int     END_POSITION_LINE_RoutingNo = START_POSITION_LINE_RoutingNo + LINE_RoutingNo;
	private static final int     END_POSITION_LINE_JP_BankName_Kana = START_POSITION_LINE_JP_BankName_Kana + LINE_JP_BankName_Kana;
	private static final int     END_POSITION_LINE_JP_BranchCode = START_POSITION_LINE_JP_BranchCode + LINE_JP_BranchCode;
	private static final int     END_POSITION_LINE_JP_BranchName_Kana = START_POSITION_LINE_JP_BranchName_Kana + LINE_JP_BranchName_Kana;
	private static final int     END_POSITION_LINE_JP_ClearingHouse = START_POSITION_LINE_JP_ClearingHouse + LINE_JP_ClearingHouse;
	private static final int     END_POSITION_LINE_JP_BankAccountType = START_POSITION_LINE_JP_BankAccountType + LINE_JP_BankAccountType;
	private static final int     END_POSITION_LINE_AccountNo = START_POSITION_LINE_AccountNo + LINE_AccountNo;
	private static final int     END_POSITION_LINE_JP_A_Name_Kana = START_POSITION_LINE_JP_A_Name_Kana + LINE_JP_A_Name_Kana;
	private static final int     END_POSITION_LINE_StmtAmt = START_POSITION_LINE_StmtAmt + LINE_StmtAmt;
	private static final int     END_POSITION_LINE_JP_BankDataNewCode =START_POSITION_LINE_JP_BankDataNewCode + LINE_JP_BankDataNewCode;
	private static final int     END_POSITION_LINE_JP_BankDataCustomerCode1 = START_POSITION_LINE_JP_BankDataCustomerCode1 + LINE_JP_BankDataCustomerCode1;
	private static final int     END_POSITION_LINE_JP_BankDataCustomerCode2 = START_POSITION_LINE_JP_BankDataCustomerCode2 + LINE_JP_BankDataCustomerCode2;
	
	private static final int     START_POSITION_FOOTER_JP_BankDataType_Footer = 0;
	private static final int     START_POSITION_FOOTER_NumLines = START_POSITION_FOOTER_JP_BankDataType_Footer + FOOTER_JP_BankDataType_Footer;
	private static final int     START_POSITION_FOOTER_TotalAmt = START_POSITION_FOOTER_NumLines + FOOTER_NumLines;
	
	private static final int     END_POSITION_FOOTER_JP_BankDataType_Footer = START_POSITION_FOOTER_JP_BankDataType_Footer + FOOTER_JP_BankDataType_Footer;
	private static final int     END_POSITION_FOOTER_NumLines = START_POSITION_FOOTER_NumLines + FOOTER_NumLines;
	private static final int     END_POSITION_FOOTER_TotalAmt = START_POSITION_FOOTER_TotalAmt + FOOTER_TotalAmt;
	
	
	// Data Diff
	private static final String     DATADIFF_Header = "1";
	private static final String     DATADIFF_Line = "2";
	private static final String     DATADIFF_Trailer = "8";
	//private static final String     DATADIFF_End = "9";
	
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
				isr = new InputStreamReader(fs, "UTF8");//TODO
				br = new BufferedReader(isr);
				
				String tmplinedata;
        		StringBuilder sql = null;
        		int lineNo = 10;
        		int no = 0;
        		//StringBuilder clientCheck = new StringBuilder(" AND AD_Client_ID=").append(m_AD_Client_ID);

        		tmplinedata = br.readLine();
        		
	            while (tmplinedata != null)
	            {
	            	String str = tmplinedata;
	            	String dataDiff = str.substring(START_POSITION_HEADER_JP_BankDataType_Header, END_POSITION_HEADER_JP_BankDataType_Header);
	            	
	            	// Header Record
	            	if (dataDiff.equals(DATADIFF_Header))
	            	{
	            		String header[] = new String[NUMBER_HEADER_Columns];

	            		header[INDEX_HEADER_JP_BankDataType_Header] = str.substring(START_POSITION_HEADER_JP_BankDataType_Header, END_POSITION_HEADER_JP_BankDataType_Header);
	            		header[INDEX_HEADER_JP_BankDataClassification] = str.substring(START_POSITION_HEADER_JP_BankDataClassification, END_POSITION_HEADER_JP_BankDataClassification);
	            		header[INDEX_HEADER_JP_BankDataCodeType] = str.substring(START_POSITION_HEADER_JP_BankDataCodeType, END_POSITION_HEADER_JP_BankDataCodeType);
	            		header[INDEX_HEADER_JP_RequesterCode] = str.substring(START_POSITION_HEADER_JP_RequesterCode, END_POSITION_HEADER_JP_RequesterCode);
	            		header[INDEX_HEADER_JP_RequesterName] = str.substring(START_POSITION_HEADER_JP_RequesterName, END_POSITION_HEADER_JP_RequesterName);
	            		header[INDEX_HEADER_PayDate] = str.substring(START_POSITION_HEADER_PayDate, END_POSITION_HEADER_PayDate);
	            		header[INDEX_HEADER_RoutingNo] = str.substring(START_POSITION_HEADER_RoutingNo, END_POSITION_HEADER_RoutingNo);
	            		header[INDEX_HEADER_JP_BankName_kana] = str.substring(START_POSITION_HEADER_JP_BankName_Kana, END_POSITION_HEADER_JP_BankName_Kana);
	            		header[INDEX_HEADER_JP_BranchCode] = str.substring(START_POSITION_HEADER_JP_BranchCode, END_POSITION_HEADER_JP_BranchCode);
	            		header[INDEX_HEADER_JP_BranchName_Kana] = str.substring(START_POSITION_HEADER_JP_BranchName_Kana, END_POSITION_HEADER_JP_BranchName_Kana);
	            		header[INDEX_HEADER_JP_BankAccountType] = str.substring(START_POSITION_HEADER_JP_BankAccountType, END_POSITION_HEADER_JP_BankAccountType);
	            		header[INDEX_HEADER_AccountNo] = str.substring(START_POSITION_HEADER_AccountNo, END_POSITION_HEADER_AccountNo);

	            		sql = new StringBuilder ("UPDATE JP_BankData ")
	            				.append("SET AD_Client_ID = COALESCE (AD_Client_ID, ").append(p_AD_Client_ID).append("),")
	            						.append(" AD_Org_ID = COALESCE (AD_Org_ID, 0),")
	            						.append(" IsActive = COALESCE (IsActive, 'Y'),")
	            						.append(" Created = COALESCE (Created, SysDate),")
	            						.append(" CreatedBy = COALESCE (CreatedBy, 0),")
	            						.append(" Updated = COALESCE (Updated, SysDate),")
	            						.append(" UpdatedBy = COALESCE (UpdatedBy, 0),")
	            						.append(" JP_BankDataType_Header = '").append(header[INDEX_HEADER_JP_BankDataType_Header]).append("',")
	            						.append(" JP_BankDataClassification = '").append(header[INDEX_HEADER_JP_BankDataClassification]).append("',")
	            						.append(" JP_BankDataCodeType = '").append(header[INDEX_HEADER_JP_BankDataCodeType]).append("',")
	            						.append(" JP_RequesterCode = '").append(header[INDEX_HEADER_JP_RequesterCode]).append("',")
	            						.append(" JP_RequesterName = '").append(header[INDEX_HEADER_JP_RequesterName]).append("',")
	            						.append(" PayDate = TO_DATE('"+ m_BankData.getStatementDate().toString().substring(0, 4) + header[INDEX_HEADER_PayDate]).append("','YYYYMMDD'),")
	            						.append(" RoutingNo = '").append(header[INDEX_HEADER_RoutingNo]).append("',")
	            						.append(" JP_BankName_Kana = '").append(header[INDEX_HEADER_JP_BankName_kana]).append("',")
	            						.append(" JP_BranchCode = '").append(header[INDEX_HEADER_JP_BranchCode]).append("',")
	            						.append(" JP_BranchName_Kana = '").append(header[INDEX_HEADER_JP_BranchName_Kana]).append("',")
	            						.append(" JP_BankAccountType = '").append(header[INDEX_HEADER_JP_BankAccountType]).append("',")
	            						.append(" AccountNo = '").append(header[INDEX_HEADER_AccountNo]).append("' ")
	            						.append("WHERE JP_BankData_ID =").append(p_JP_BankData_ID);
	            		no = DB.executeUpdateEx(sql.toString(), get_TrxName());
	            		if (log.isLoggable(Level.FINE)) log.fine("Reset=" + no);
	            	}
	            	
	            	
	            	// Line Record
	            	else if (dataDiff.equals(DATADIFF_Line))
	            	{	            		
	            		String line[] = new String[NUMBER_LINE_Columns];
	            		
	            		line[INDEX_LINE_JP_BankDataType_Line] = str.substring(START_POSITION_LINE_JP_BankDataType_Line, END_POSITION_LINE_JP_BankDataType_Line);//0
	            		line[INDEX_LINE_RoutingNo] = str.substring(START_POSITION_LINE_RoutingNo , END_POSITION_LINE_RoutingNo);									//1
	            		line[INDEX_LINE_JP_BankName_Kana] = str.substring(START_POSITION_LINE_JP_BankName_Kana , END_POSITION_LINE_JP_BankName_Kana);				//2
	            		line[INDEX_LINE_JP_BranchCode] = str.substring(START_POSITION_LINE_JP_BranchCode , END_POSITION_LINE_JP_BranchCode);						//3
	            		line[INDEX_LINE_JP_BranchName_Kana] = str.substring(START_POSITION_LINE_JP_BranchName_Kana , END_POSITION_LINE_JP_BranchName_Kana);		//4
	            		line[INDEX_LINE_JP_ClearingHouse] = str.substring(START_POSITION_LINE_JP_ClearingHouse , END_POSITION_LINE_JP_ClearingHouse);				//5
	            		line[INDEX_LINE_JP_BankAccountType] = str.substring(START_POSITION_LINE_JP_BankAccountType , END_POSITION_LINE_JP_BankAccountType);		//6
	            		line[INDEX_LINE_AccountNo] = str.substring(START_POSITION_LINE_AccountNo , END_POSITION_LINE_AccountNo);									//7
	            		line[INDEX_LINE_JP_A_Name_Kana] = str.substring(START_POSITION_LINE_JP_A_Name_Kana , END_POSITION_LINE_JP_A_Name_Kana);					//8
	            		line[INDEX_LINE_StmtAmt] = str.substring(START_POSITION_LINE_StmtAmt , END_POSITION_LINE_StmtAmt);											//9
	            		line[INDEX_LINE_JP_BankDataNewCode] = str.substring(START_POSITION_LINE_JP_BankDataNewCode , END_POSITION_LINE_JP_BankDataNewCode);						//10
	            		line[INDEX_LINE_JP_BankDataCustomerCode1] = str.substring(START_POSITION_LINE_JP_BankDataCustomerCode1 , END_POSITION_LINE_JP_BankDataCustomerCode1);	//11
	            		line[INDEX_LINE_JP_BankDataCustomerCode2] = str.substring(START_POSITION_LINE_JP_BankDataCustomerCode2 , END_POSITION_LINE_JP_BankDataCustomerCode2);	//12
	            		
	            		sql = new StringBuilder ("INSERT INTO JP_BankDataLine ")
	            				.append(" (AD_Client_ID, AD_Org_ID, IsActive, Created, CreatedBy, Updated, UpdatedBy, ")
	            				.append(" JP_BankDataType_Line, RoutingNo, JP_BankName_Kana, JP_BranchCode, JP_BranchName_Kana, JP_ClearingHouse, ")//0 - 5
	            				.append(" JP_BankAccountType, AccountNo, JP_A_Name_Kana, StmtAmt, TrxAmt,") // 6 - 10
	            				.append(" Line, JP_BankData_ID, JP_BankDataLine_ID, JP_BankDataLine_UU, StatementLineDate,DateAcct, ValutaDate ) ")
	            				
	            				.append(" VALUES (")
	            				.append(p_AD_Client_ID).append(",")
	            				.append(m_BankData.getAD_Org_ID()).append(",")
	            				.append(" 'Y',")
	            				.append(" SysDate,")
	            				.append(" COALESCE((SELECT CreatedBy FROM JP_BankData WHERE JP_BankData_ID = ").append(p_JP_BankData_ID).append("), 0),")
	            				.append(" SysDate,")
	            				.append(" COALESCE((SELECT UpdatedBy FROM JP_BankData WHERE JP_BankData_ID = ").append(p_JP_BankData_ID).append("), 0),")
	            				
	            				.append(" '").append(line[INDEX_LINE_JP_BankDataType_Line]).append("',")	//0
	            				.append(" '").append(line[INDEX_LINE_RoutingNo]).append("',")				//1
	            				.append(" '").append(line[INDEX_LINE_JP_BankName_Kana]).append("',")		//2
	            				.append(" '").append(line[INDEX_LINE_JP_BranchCode]).append("',")			//3
	            				.append(" '").append(line[INDEX_LINE_JP_BranchName_Kana]).append("',")	//4
	            				.append(" '").append(line[INDEX_LINE_JP_ClearingHouse]).append("',")		//5
	            				
	            				.append(" '").append(line[INDEX_LINE_JP_BankAccountType]).append("',")	//6
	            				.append(" '").append(line[INDEX_LINE_AccountNo]).append("',")				//7
	            				.append(" '").append(line[INDEX_LINE_JP_A_Name_Kana]).append("',")		//8
	            				.append(" ").append(line[INDEX_LINE_StmtAmt]).append(",")					//9
	            				.append(" ").append(line[INDEX_LINE_StmtAmt]).append(",")					//10
	            				.append(" ").append(lineNo).append(",")
	            				.append(" ").append(p_JP_BankData_ID).append(",")
	            				.append(" ").append(DB.getNextID(p_AD_Client_ID, "JP_BankDataLine", get_TrxName())).append(",")
	            				.append(" generate_uuid(), ")
	            				.append(" ").append("TO_DATE('").append(m_BankData.getStatementDate().toString().substring(0, 10)).append(" 00:00:00','YYYY-MM-DD HH24:MI:SS'),")
	            				.append(" ").append("TO_DATE('").append(m_BankData.getDateAcct().toString().substring(0, 10)).append(" 00:00:00','YYYY-MM-DD HH24:MI:SS'),")
	            				.append(" ").append("TO_DATE('").append(m_BankData.getDateAcct().toString().substring(0, 10)).append(" 00:00:00','YYYY-MM-DD HH24:MI:SS')")
	            				.append(");");
	            		no = DB.executeUpdateEx(sql.toString(), get_TrxName());
	            		if (log.isLoggable(Level.FINE)) log.fine("Reset=" + no);
	            		
	            		lineNo+=10;
	            	}
	            	
	            	// Trailer(Footer) Record
	            	else if (dataDiff.equals(DATADIFF_Trailer))
	            	{
	            		String footer[] = new String[NUMBER_FOOTER_Columns];
	            		
	            		footer[INDEX_FOOTER_JP_BankDataType_Footer] = str.substring(START_POSITION_FOOTER_JP_BankDataType_Footer, END_POSITION_FOOTER_JP_BankDataType_Footer);
	            		footer[INDEX_FOOTER_NumLines] = str.substring(START_POSITION_FOOTER_NumLines, END_POSITION_FOOTER_NumLines);
	            		footer[INDEX_FOOTER_TotalAmt] = str.substring(START_POSITION_FOOTER_TotalAmt, END_POSITION_FOOTER_TotalAmt);
	            		sql = new StringBuilder ("UPDATE JP_BankData ")
	            				.append("SET")
	            						.append(" JP_BankDataType_Footer = '").append(footer[INDEX_FOOTER_JP_BankDataType_Footer]).append("',")
	            						.append(" NumLines = '").append(footer[INDEX_FOOTER_NumLines]).append("',")
	            						.append(" TotalAmt = '").append(footer[INDEX_FOOTER_TotalAmt]).append("'")
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
}
