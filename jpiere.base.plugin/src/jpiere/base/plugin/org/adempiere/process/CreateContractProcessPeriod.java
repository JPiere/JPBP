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

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.Locale;

import org.adempiere.util.IProcessUI;
import org.compiere.model.MClient;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
import org.compiere.util.Language;
import org.compiere.util.Msg;

import jpiere.base.plugin.org.adempiere.model.MContractProcPeriod;
import jpiere.base.plugin.org.adempiere.model.MContractProcPeriodG;


/** JPIERE-0363
 * 
 * Create Contract Period Process
 *
 * @author Hideaki Hagiwara
 *
 */
public class CreateContractProcessPeriod extends SvrProcess {
	
	private int	p_ContractProcPeriodG_ID = 0;
	private Timestamp p_DateContract_From;
	private Timestamp p_DateContract_To;
	private int p_Year = 0;
	private int p_Month = 0;
	private int p_Day = 0;
	private String p_DateFormat;
	private String p_DateDocList = "E";
	
	@Override
	protected void prepare() 
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
			{
				;
			}else if (name.equals("DateContract"))
			{
				p_DateContract_From = para[i].getParameterAsTimestamp();
				p_DateContract_To = para[i].getParameter_ToAsTimestamp();
			}else if(name.equals("JP_Year")){
				p_Year = para[i].getParameterAsInt();
			}else if(name.equals("JP_Month")){
				p_Month = para[i].getParameterAsInt();
			}else if(name.equals("JP_Day")){
				p_Day = para[i].getParameterAsInt();
			}else if (name.equals("DateFormat")){
				p_DateFormat = (String) para[i].getParameter();
			}else if (name.equals("DateDoc")){
				p_DateDocList = (String) para[i].getParameter();
			}else{
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}
		}			
		p_ContractProcPeriodG_ID = getRecord_ID();
	}
	
	@Override
	protected String doIt() throws Exception 
	{
		MContractProcPeriodG contractCalender = MContractProcPeriodG.get(getCtx(), p_ContractProcPeriodG_ID);
		if(p_Year == 0 &&  p_Month == 0 && p_Day == 0)
		{
			return Msg.getMsg(getCtx(), "FillMandatory") + Msg.getElement(getCtx(), "JP_ContractProcPeriod_ID");
		}
		
		if(p_Year < 0 || p_Month < 0 || p_Day < 0)
		{
			return Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "JP_ContractProcPeriod_ID");
		}
		
		
		MClient client = MClient.get(getCtx());
		Locale locale = client.getLocale();
		if (locale == null && Language.getLoginLanguage() != null)
			locale = Language.getLoginLanguage().getLocale();
		if (locale == null)
			locale = Env.getLanguage(getCtx()).getLocale();
		
		if ( p_DateFormat == null || p_DateFormat.equals("") )
			p_DateFormat = "MMM-yy";
		SimpleDateFormat formatter = new SimpleDateFormat(p_DateFormat, locale);
		
		LocalDateTime dateContract_From = p_DateContract_From.toLocalDateTime();	
		LocalDateTime dateContract_To = p_DateContract_To.toLocalDateTime();
		if(dateContract_From.compareTo(dateContract_To) >= 0)
			return Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "DateContract");
		
		LocalDateTime startDate= p_DateContract_From.toLocalDateTime();
		LocalDateTime endDate= startDate.plusYears(p_Year).plusMonths(p_Month).plusDays(p_Day).minusDays(1);
		
		boolean isBreak = false;
		IProcessUI processMonitor = Env.getProcessUI(getCtx());
		
		do 
		{
			if(endDate.compareTo(dateContract_To) > 0)
			{
				endDate = dateContract_To;
				isBreak = true;
			}
			
			String name = formatter.format(Timestamp.valueOf(startDate));
			
			MContractProcPeriod procPeriod = new MContractProcPeriod(getCtx(), 0, get_TrxName());
			procPeriod.setStartDate(Timestamp.valueOf(startDate));
			procPeriod.setEndDate(Timestamp.valueOf(endDate));
			procPeriod.setName(name);
			if(p_DateDocList.equals("E"))
				procPeriod.setDateDoc(Timestamp.valueOf(endDate));
			else if(p_DateDocList.equals("S"))
				procPeriod.setDateDoc(Timestamp.valueOf(startDate));
			procPeriod.setJP_ContractProcPeriodG_ID(p_ContractProcPeriodG_ID);
			procPeriod.setJP_ContractCalender_ID(contractCalender.getJP_ContractCalender_ID());
			procPeriod.saveEx(get_TrxName());
			if (processMonitor != null)
			{
				processMonitor.statusUpdate(procPeriod.toString());
			}
			
			
			startDate = endDate.plusDays(1);
			endDate= startDate.plusYears(p_Year).plusMonths(p_Month).plusDays(p_Day).minusDays(1);
			
		} while (!isBreak && startDate.compareTo(dateContract_To) < 0);
		
		
		return Msg.getMsg(getCtx(), "OK");
	}
	
}
