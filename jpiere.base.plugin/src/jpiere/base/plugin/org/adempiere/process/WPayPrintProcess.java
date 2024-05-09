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

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.logging.Level;

import org.adempiere.util.IProcessUI;
import org.compiere.model.MBankAccount;
import org.compiere.model.MPaySelection;
import org.compiere.model.MPaySelectionCheck;
import org.compiere.model.MSysConfig;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.PaymentExport;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.MPaymentExportClass;

/**
 * JPIERE-0272: Payment Data Export by Process at Pay Selection Window
 * JPIERE-0615: Payment Export Class
 *
 * @author Hiroshi Iwama
 *
 */

public class WPayPrintProcess extends SvrProcess{

	/*
	 * Parameters
	 */

	//PaySelection
	private int p_C_PaySelection_ID = 0;

	//Payment Rule
	private String p_PaymentRule = null;
	private int p_C_BankAccount_ID = 0;
	private int p_JP_PaymentExportClass_ID = 0;
	private String JP_LineEnd = null;
	IProcessUI processUI = null;

	//Target PaymentExportClass
	public String paymentExportClass = null;

	//Target PaySelectionCheck(s)
	public MPaySelectionCheck[]     m_checks = null;


	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
		for(int i = 0; i < para.length; i++){
			String name = para[i].getParameterName();
			if(para[i].getParameterName() == null && para[i].getParameter_To() == null){
				;
			}
			else if(name.equals("PaymentRule")){
				p_PaymentRule = para[i].getParameterAsString();
			}
			else if(name.equals("JP_PaymentExportClass_ID")){
				p_JP_PaymentExportClass_ID = para[i].getParameterAsInt();
			}
			else{
				log.log(Level.SEVERE, "Unknown Paramtere :" + name);
			}
			//get the current record ID.
			p_C_PaySelection_ID = getRecord_ID();

			processUI = Env.getProcessUI(getCtx());
		}

	}

	@Override
	protected String doIt() throws Exception {

		//get the current PaySelection record
		MPaySelection paySelection = new MPaySelection(getCtx(), p_C_PaySelection_ID, get_TrxName());
		//get the relevant BankAccount
		p_C_BankAccount_ID = paySelection.getC_BankAccount_ID();
		MBankAccount bankAccount = new MBankAccount(getCtx(), p_C_BankAccount_ID, get_TrxName());
		
		if(p_JP_PaymentExportClass_ID > 0)
		{
			MPaymentExportClass m_PEC =new MPaymentExportClass(getCtx(), p_JP_PaymentExportClass_ID, get_TrxName());
			paymentExportClass = m_PEC.getPaymentExportClass();
			if(!Util.isEmpty(m_PEC.getJP_LineEnd()))
			{
				if(MPaymentExportClass.JP_LINEEND_CRLF.equals(m_PEC.getJP_LineEnd()))
				{
					JP_LineEnd = "\r\n";
				}else if(MPaymentExportClass.JP_LINEEND_CR.equals(m_PEC.getJP_LineEnd())) {
					JP_LineEnd = "\r";
				}else if(MPaymentExportClass.JP_LINEEND_LF.equals(m_PEC.getJP_LineEnd())) {
					JP_LineEnd = "\n";
				}
			}
			
		}else {
		
			//get the target PaymentExportClass from the relevant BankAccount
			paymentExportClass = bankAccount.getPaymentExportClass();
		
		}
		
		if(Util.isEmpty(JP_LineEnd))
		{
			String line_end = MSysConfig.getValue("JP_JAPAN_PAYMENT_EXPORT_LINE_END", Env.NL,  Env.getAD_Client_ID(Env.getCtx()), 0);
			if(Util.isEmpty(line_end))
			{
				JP_LineEnd = Env.NL;
			}else {
				
				if(line_end.equalsIgnoreCase("CRLF") || line_end.equalsIgnoreCase("\\r\\n")){
					JP_LineEnd = "\r\n";
				}else if(line_end.equalsIgnoreCase("CR") || line_end.equalsIgnoreCase("\\r")) {
					JP_LineEnd = "\r";
				}else if(line_end.equalsIgnoreCase("LF") || line_end.equalsIgnoreCase("\\n")) {
					JP_LineEnd = "\n";
				}else {
					JP_LineEnd = Env.NL;
				}
			}
		}
		

		//checking the variables and get the relevant PaySelectionChecks
		if(!getChecks(p_PaymentRule)){
			return Msg.getMsg(getCtx(), "FindZeroRecords");//No Records found
		}

		//Get Payment Export Class
		int no = 0;
		StringBuffer err = new StringBuffer(JP_LineEnd);
		
		try
		{

			//Create temporary file
			File tempFile = File.createTempFile("paymentExport", ".txt");


			if (paymentExportClass == null || paymentExportClass.trim().length() == 0) {
				paymentExportClass = "org.compiere.util.GenericPaymentExport";
			}
			//	Get the instance
			PaymentExport custom = null;
			try
			{
				Class<?> clazz = Class.forName(paymentExportClass);
				custom = (PaymentExport)clazz.getDeclaredConstructor().newInstance();
				no = custom.exportToFile(m_checks, tempFile, err);
			}
			catch (ClassNotFoundException e)
			{
				no = -1;
				err.append(Msg.getMsg(getCtx(), "JP_ClassNotFound") + paymentExportClass + " - " + e.toString());
				log.log(Level.SEVERE, err.toString(), e);
			}
			catch (Exception e)
			{
				no = -1;
				err.append(Msg.getMsg(getCtx(), "Error") + paymentExportClass + " check log, " + e.toString());
				log.log(Level.SEVERE, err.toString(), e);
			}
			
			if (no >= 0) {

				//Download the exported file

				String AccountNo = bankAccount.getAccountNo();
				String payDateString = paySelection.getPayDate().toString().substring(0,10).replace("-", "");
				LocalDateTime dateTime = LocalDateTime.now();

				int downloadNum = 0;
				if( paySelection.get_ColumnIndex("JP_DownloadNum") > 0)
				{
					downloadNum = paySelection.get_ValueAsInt("JP_DownloadNum");
					downloadNum++;
					paySelection.set_ValueNoCheck("JP_DownloadNum", downloadNum);
					paySelection.saveEx(get_TrxName());
				}

				Path inputPath = FileSystems.getDefault().getPath(tempFile.getAbsolutePath());
//				Path outputPath = FileSystems.getDefault().getPath(tempFile.getParent() + "\\ExpPayment_"
//									+ AccountNo + "_" + payDateString + (downloadNum == 0 ? "": "_" + downloadNum)
//									+ "_" + dateTime.truncatedTo(ChronoUnit.SECONDS).toString().replace(":", "").replace("-", "") + ".txt");
				Path outputPath = FileSystems.getDefault().getPath("ExpPayment_"
						+ AccountNo + "_" + payDateString + (downloadNum == 0 ? "": "_" + downloadNum)
						+ "_" + dateTime.truncatedTo(ChronoUnit.SECONDS).toString().replace(":", "").replace("-", "") + ".txt");

				Files.copy(inputPath, outputPath);
				tempFile.delete();

				processUI.download(new File(outputPath.toString()));

//				//get the comma-separated PaySelectionChecks
//				StringBuilder PaySelectionCheckList = new StringBuilder();
//				for(int i = 0; i < m_checks.length; i++){
//					if(i != 0){
//						PaySelectionCheckList.append(",").append(m_checks[i].getC_PaySelectionCheck_ID());
//					}else{
//						PaySelectionCheckList.append(m_checks[i].getC_PaySelectionCheck_ID());
//					}
//				}
//
//				//Update its IsExported flag
//				StringBuilder updateSQL = new StringBuilder("UPDATE C_PaySelectionCheck SET IsExported = 'Y' WHERE C_PaySelectionCheck_ID in (")
//											.append(PaySelectionCheckList)
//											.append(")");
//				DB.executeUpdate(updateSQL.toString(), get_TrxName());

			}
		}
		catch (Exception e)
		{
			System.out.println(e);
			log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			return Msg.getMsg(getCtx(), "Error") + " : " +  e.toString();
		}
		
		if(no == -1)
		{
			throw new Exception(err.toString());
		}

		return Msg.getMsg(getCtx(), "ProcessOK") + " : " + paySelection.getName();
	}

	private boolean getChecks(String PaymentRule) {
		if(p_C_PaySelection_ID <= 0 || p_C_BankAccount_ID <= 0 || p_PaymentRule == null){
			return false;
		}

		if (log.isLoggable(Level.CONFIG)) log.config("C_PaySelection_ID=" + p_C_PaySelection_ID + ", PaymentRule=" +  PaymentRule);

		//get the target PaySelectionCheck
		m_checks = MPaySelectionCheck.get(p_C_PaySelection_ID, p_PaymentRule, null);

		if(m_checks == null || m_checks.length == 0){
			return false;
		}

		return true;
	}

}
