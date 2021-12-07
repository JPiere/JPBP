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
import java.util.Locale;
import java.util.logging.Level;

import org.adempiere.util.IProcessUI;
import org.compiere.model.MClient;
import org.compiere.model.MTable;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
import org.compiere.util.Language;
import org.compiere.util.Msg;

import jpiere.base.plugin.org.adempiere.model.MContractCalender;
import jpiere.base.plugin.org.adempiere.model.MContractCalenderList;
import jpiere.base.plugin.org.adempiere.model.MContractCalenderRef;
import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractContentT;
import jpiere.base.plugin.org.adempiere.model.MContractLine;
import jpiere.base.plugin.org.adempiere.model.MContractProcPeriod;
import jpiere.base.plugin.org.adempiere.model.MContractProcPeriodG;


/**
 *
 * JPIERE-0363 - Contract Management
 * JPIERE-0517 - Create Contract Calendar at Contract Doc
 *
 * Create Contract Period Process
 *
 * @author Hideaki Hagiwara
 *
 */
public class CreateContractProcessPeriod extends SvrProcess {

	private int 	p_JP_ContractCalender_ID = 0;
	private String p_JP_ContractCalendar_Value = null;
	private String p_JP_ContractCalendar_Name = null;
	private boolean p_IsCreateCProcPeriodGJP = false;
	private String p_JP_CProcPeriodG_Value = null;
	private String p_JP_CProcPeriodG_Name = null;
	private String p_JP_ContractProcessValue = null;

	private int	p_JP_ContractProcPeriodG_ID = 0;


	private Timestamp p_DateContract_From;
	private Timestamp p_DateContract_To;
	private int p_Year = 0;
	private int p_Month = 0;
	private int p_Day = 0;
	private String p_DateFormat;
	private String p_DateDocList = "E";
	private int p_MonthOffset = 0;
	private int p_DayOffset = 0;
	private boolean isDueFixed = true;

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
			}else if (name.equals("JP_ContractCalender_ID")){
				p_JP_ContractCalender_ID = para[i].getParameterAsInt();
			}else if (name.equals("JP_ContractCalendar_Value")){
				p_JP_ContractCalendar_Value =  para[i].getParameterAsString();
			}else if (name.equals("JP_ContractCalendar_Name")){
				p_JP_ContractCalendar_Name =  para[i].getParameterAsString();
			}else if (name.equals("IsCreateCProcPeriodGJP")){
				p_IsCreateCProcPeriodGJP = para[i].getParameterAsBoolean();
			}else if (name.equals("JP_CProcPeriodG_Value")){
				p_JP_CProcPeriodG_Value =  para[i].getParameterAsString();
			}else if (name.equals("JP_CProcPeriodG_Name")){
				p_JP_CProcPeriodG_Name =  para[i].getParameterAsString();
			}else if (name.equals("JP_ContractProcessValue")){
				p_JP_ContractProcessValue =  para[i].getParameterAsString();
			}else if (name.equals("JP_ContractProcPeriodG_ID")){
				p_JP_ContractProcPeriodG_ID = para[i].getParameterAsInt();
			}else if (name.equals("DateContract")){
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
			}else if (name.equals("DateAcct")){
				p_DateDocList = (String) para[i].getParameter();
			}else if (name.equals("JP_MonthOffset")){
				p_MonthOffset = para[i].getParameterAsInt();
			}else if (name.equals("JP_DayOffset")){
				p_DayOffset = para[i].getParameterAsInt();
			}else if (name.equals("IsDueFixed")){
				isDueFixed = para[i].getParameterAsBoolean();
			}else{
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}
		}
	}

	MContractContent m_ContractContent = null;

	@Override
	protected String doIt() throws Exception
	{

		int AD_Table_ID = getTable_ID();
		if(AD_Table_ID == 0)
		{
			throw new Exception(Msg.getMsg(getCtx(), "JP_UnexpectedError") + " AD_Table_ID == 0");
		}

		MTable m_Table = MTable.get(AD_Table_ID);
		if(m_Table.getTableName().equals(MContractProcPeriodG.Table_Name))
		{
			p_JP_ContractProcPeriodG_ID = getRecord_ID();

		}else if(m_Table.getTableName().equals(MContractContent.Table_Name)) {

			m_ContractContent = new MContractContent(getCtx(), getRecord_ID(), get_TrxName());

			if(p_JP_ContractCalender_ID == 0)
			{
				MContractCalender m_ContractCalendar = new MContractCalender(getCtx(), 0 , get_TrxName());
				m_ContractCalendar.setValue(p_JP_ContractCalendar_Value);
				m_ContractCalendar.setName(p_JP_ContractCalendar_Name);
				m_ContractCalendar.setJP_ContractCategory_ID(m_ContractContent.getParent().getJP_ContractCategory_ID());
				m_ContractCalendar.setJP_Contract_ID(m_ContractContent.getJP_Contract_ID());
				m_ContractCalendar.setJP_ContractContent_ID(m_ContractContent.getJP_ContractContent_ID());
				m_ContractCalendar.saveEx(get_TrxName());
				p_JP_ContractCalender_ID = m_ContractCalendar.getJP_ContractCalender_ID();
				p_IsCreateCProcPeriodGJP = true;
			}

			if(p_IsCreateCProcPeriodGJP)
			{
				MContractProcPeriodG m_ContractProcPeriodG = new MContractProcPeriodG(getCtx(), 0 , get_TrxName());
				m_ContractProcPeriodG.setJP_ContractCalender_ID(p_JP_ContractCalender_ID);
				m_ContractProcPeriodG.setValue(p_JP_CProcPeriodG_Value);
				m_ContractProcPeriodG.setName(p_JP_CProcPeriodG_Name);
				m_ContractProcPeriodG.setJP_ContractProcessValue(p_JP_ContractProcessValue);
				m_ContractProcPeriodG.saveEx(get_TrxName());
				p_JP_ContractProcPeriodG_ID = m_ContractProcPeriodG.getJP_ContractProcPeriodG_ID();
			}

			if(p_JP_ContractProcPeriodG_ID == 0)
			{
				throw new Exception(Msg.getMsg(getCtx(), "JP_UnexpectedError") + " JP_ContractProcPeriodG_ID == 0");
			}

		}else if(m_Table.getTableName().equals(MContractLine.Table_Name)) {

		}else {
			throw new Exception(Msg.getMsg(getCtx(), "JP_UnexpectedError"));
		}



		if(isDueFixed)
		{
			if( p_DayOffset > 31 || p_DayOffset < 0)
			{
				throw new Exception(Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "JP_DayOffset"));
			}
		}


		MContractProcPeriodG m_ContractCalenderG = new MContractProcPeriodG(getCtx(), p_JP_ContractProcPeriodG_ID, get_TrxName());
		if(p_Year == 0 &&  p_Month == 0 && p_Day == 0)
		{
			throw new Exception(Msg.getMsg(getCtx(), "FillMandatory") + Msg.getElement(getCtx(), "JP_ContractProcPeriod_ID"));
		}

		if(p_Year < 0 || p_Month < 0 || p_Day < 0)
		{
			throw new Exception(Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "JP_DayOffset"));
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
		LocalDateTime docDate = null;

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
			{
				procPeriod.setDateAcct(Timestamp.valueOf(endDate));
				if(isDueFixed)
				{
					if(p_DayOffset == 31)
					{
						docDate = endDate.plusMonths(p_MonthOffset+1).withDayOfMonth(1).minusDays(1);
					}else if(p_DayOffset == 0){
						docDate = endDate.plusMonths(p_MonthOffset);
					}else{

						try {
							docDate = endDate.plusMonths(p_MonthOffset).withDayOfMonth(p_DayOffset);
						} catch (Exception e) {
							throw new Exception(Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "JP_DayOffset"));
						}

					}

				}else{
					docDate = endDate.plusMonths(p_MonthOffset).plusDays(p_DayOffset);
				}

			}else if(p_DateDocList.equals("S")){
				procPeriod.setDateAcct(Timestamp.valueOf(startDate));
				if(isDueFixed)
				{
					if(p_DayOffset == 31)
					{
						docDate = startDate.plusMonths(p_MonthOffset+1).withDayOfMonth(1).minusDays(1);
					}else if(p_DayOffset == 0){
						docDate = startDate.plusMonths(p_MonthOffset);
					}else{

						try {
							docDate = startDate.plusMonths(p_MonthOffset).withDayOfMonth(p_DayOffset);
						} catch (Exception e) {
							throw new Exception(Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "JP_DayOffset"));
						}

					}
				}else{
					docDate = startDate.plusMonths(p_MonthOffset).plusDays(p_DayOffset);
				}
			}
			procPeriod.setDateDoc(Timestamp.valueOf(docDate));
			procPeriod.setJP_ContractProcPeriodG_ID(p_JP_ContractProcPeriodG_ID);
			procPeriod.setJP_ContractCalender_ID(m_ContractCalenderG.getJP_ContractCalender_ID());
			procPeriod.saveEx(get_TrxName());
			if (processMonitor != null)
			{
				processMonitor.statusUpdate(procPeriod.toString());
			}


			startDate = endDate.plusDays(1);
			endDate= startDate.plusYears(p_Year).plusMonths(p_Month).plusDays(p_Day).minusDays(1);

		} while (!isBreak && startDate.compareTo(dateContract_To) < 0);


		//After
		if(m_Table.getTableName().equals(MContractProcPeriodG.Table_Name))
		{


		}else if(m_Table.getTableName().equals(MContractContent.Table_Name)) {

			m_ContractContent.setJP_ContractCalender_ID(p_JP_ContractCalender_ID);
			m_ContractContent.saveEx(get_TrxName());

			int JP_ContractContentT_ID = m_ContractContent.getJP_ContractContentT_ID();
			if(JP_ContractContentT_ID > 0)
			{
				MContractContentT cct = new MContractContentT(getCtx(), JP_ContractContentT_ID, get_TrxName());
				int JP_ContractCalenderRef_ID = cct.getJP_ContractCalenderRef_ID();
				if(JP_ContractCalenderRef_ID > 0)
				{
					MContractCalenderRef m_ContractCalenderRef = MContractCalenderRef.get(getCtx(), JP_ContractCalenderRef_ID);
					MContractCalenderList[] list = m_ContractCalenderRef.getContractCalenderList(getCtx(), true, get_TrxName());
					boolean isRegistered = false;
					for(MContractCalenderList ccl : list)
					{
						if(ccl.getJP_ContractCalender_ID() == p_JP_ContractCalender_ID)
						{
							isRegistered = true;
							break;
						}
					}

					if(!isRegistered)
					{
						MContractCalenderList ccl = new MContractCalenderList(getCtx(), 0, get_TrxName());
						ccl.setJP_ContractCalenderRef_ID(JP_ContractCalenderRef_ID);
						ccl.setJP_ContractCalender_ID(p_JP_ContractCalender_ID);
						ccl.saveEx(get_TrxName());
					}
				}
			}


		}else if(m_Table.getTableName().equals(MContractLine.Table_Name)) {

		}else {
			throw new Exception(Msg.getMsg(getCtx(), "JP_UnexpectedError"));
		}

		return Msg.getMsg(getCtx(), "OK");
	}

}
