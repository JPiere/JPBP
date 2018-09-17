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
import java.util.Properties;
import java.util.logging.Level;

import org.adempiere.util.IProcessUI;
import org.adempiere.util.ProcessUtil;
import org.compiere.model.MPeriod;
import org.compiere.model.MPeriodControl;
import org.compiere.model.MRefList;
import org.compiere.process.ProcessInfo;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.CacheMgt;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Trx;
import org.compiere.util.Util;

/**
*  JPIERE-0424 Calendar Bulk Open - Close
*
* @author Hideaki Hagiwara
*
*/
public class JPiereCalendarBulkOpenClose extends SvrProcess {

	private int p_AD_Client_ID = 0;
	private int p_C_Calendar_ID = 0;
	private Timestamp p_EndDate = null;
	private String p_DocBaseType = null;
	private String p_PeriodAction = null;
	private IProcessUI processUI = null;

	@Override
	protected void prepare()
	{
		p_AD_Client_ID =getProcessInfo().getAD_Client_ID();
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null){
				;
			}else if (name.equals("C_Calendar_ID")){
				p_C_Calendar_ID = para[i].getParameterAsInt();
			}else if (name.equals("EndDate")){
				p_EndDate = (Timestamp)para[i].getParameter();
			}else if(name.equals("DocBaseType")){
				p_DocBaseType = para[i].getParameterAsString();
			}else if(name.equals("PeriodAction")){
				p_PeriodAction = para[i].getParameterAsString();
			}else{
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}//if
		}//for

	}

	@Override
	protected String doIt() throws Exception
	{
		processUI = Env.getProcessUI(getCtx());

		MPeriod[] periods = getPeriodByCalendar(getCtx(), p_EndDate, p_C_Calendar_ID, get_TrxName());

		if(Util.isEmpty(p_DocBaseType))
		{
			ProcessInfo pi = new ProcessInfo("Period Open - Close", 0);
			pi.setClassName("org.compiere.process.PeriodStatus");
			pi.setAD_Client_ID(getAD_Client_ID());
			pi.setAD_User_ID(getAD_User_ID());
			pi.setAD_PInstance_ID(getAD_PInstance_ID());

			ArrayList<ProcessInfoParameter> list = new ArrayList<ProcessInfoParameter>();
			list.add (new ProcessInfoParameter("PeriodAction", p_PeriodAction, null, null, null ));
			ProcessInfoParameter[] pars = new ProcessInfoParameter[list.size()];
			list.toArray(pars);
			pi.setParameter(pars);

			String msg = null;
			for(int i = 0; i < periods.length; i++)
			{
				pi.setRecord_ID(periods[i].getC_Period_ID());
				boolean success = ProcessUtil.startJavaProcess(getCtx(), pi, Trx.get(get_TrxName(), true), false, processUI);
				if(success)
				{
					msg = periods[i].getName() + " - " +MRefList.getListName(getCtx(), 176, p_PeriodAction);

				}else {
					msg = Msg.getMsg(getCtx(), "JP_Failure") + " : " + periods[i].getName() + " - " +MRefList.getListName(getCtx(), 176, p_PeriodAction);
				}

				if(processUI != null)
					processUI.statusUpdate(msg);

				addLog(msg);
			}

		}else {

			String msg = null;
			for(int i = 0; i < periods.length; i++)
			{
				MPeriodControl periodControl = getPeriodControl(getCtx(), periods[i].getC_Period_ID(), p_DocBaseType, get_TrxName());
				if(periodControl != null)
				{
					//	Open
					if (MPeriodControl.PERIODACTION_OpenPeriod.equals(p_PeriodAction))
						periodControl.setPeriodStatus(MPeriodControl.PERIODSTATUS_Open);
					//	Close
					if (MPeriodControl.PERIODACTION_ClosePeriod.equals(p_PeriodAction))
						periodControl.setPeriodStatus(MPeriodControl.PERIODSTATUS_Closed);
					//	Close Permanently
					if (MPeriodControl.PERIODACTION_PermanentlyClosePeriod.equals(p_PeriodAction))
						periodControl.setPeriodStatus(MPeriodControl.PERIODSTATUS_PermanentlyClosed);

					periodControl.setPeriodAction(MPeriodControl.PERIODACTION_NoAction);

					boolean success = periodControl.save(get_TrxName());
					if(success && processUI != null)
					{
						msg = periods[i].getName() + " - " + MRefList.getListName(getCtx(), 183, p_DocBaseType) +  " - " +MRefList.getListName(getCtx(), 176, p_PeriodAction);
					}else {
						msg = Msg.getMsg(getCtx(), "JP_Failure") + " : " +periods[i].getName() + " - " + MRefList.getListName(getCtx(), 183, p_DocBaseType) +  " - " +MRefList.getListName(getCtx(), 176, p_PeriodAction);
					}

					if(processUI != null)
						processUI.statusUpdate(msg);

					addLog(msg);

					//Reset Cache
					CacheMgt.get().reset("C_PeriodControl", 0);
					CacheMgt.get().reset("C_Period", periodControl.getC_Period_ID());
				}

			}//For
		}


		return Msg.getMsg(getCtx(), "Success");
	}


	/**
	 *
	 * @param ctx
	 * @param EndDate
	 * @param C_Calendar_ID
	 * @param trxName
	 * @return MPeriod
	 */
	private static MPeriod[] getPeriodByCalendar(Properties ctx, Timestamp EndDate, int C_Calendar_ID, String trxName)
	{

		ArrayList<MPeriod> list = new ArrayList<MPeriod>();

		String sql = "SELECT * "
			+ "FROM C_Period "
			+ "WHERE C_Year_ID IN "
				+ "(SELECT C_Year_ID FROM C_Year WHERE C_Calendar_ID= ?)"
			+ " AND EndDate <= ? ORDER BY EndDate ";

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, trxName);
            pstmt.setInt (1, C_Calendar_ID);
            pstmt.setTimestamp (2, EndDate);
			rs = pstmt.executeQuery();
			while (rs.next())
			{
				list.add(new MPeriod(ctx, rs, trxName));
			}
		}
		catch (SQLException e)
		{
//			log.log(Level.SEVERE, "DateAcct=" + DateAcct, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}

		MPeriod[] periods = new MPeriod[list.size()];
		list.toArray(periods);

		return periods;
	}

	private static MPeriodControl getPeriodControl(Properties ctx, int C_Period_ID, String DocBaseType, String trxName)
	{

		MPeriodControl retValue = null;

		String sql = "SELECT * FROM C_PeriodControl WHERE C_Period_ID=? AND DocBaseType=? ";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, trxName);
			pstmt.setInt(1, C_Period_ID);
			pstmt.setString(2, DocBaseType);
			rs = pstmt.executeQuery();
			if (rs.next())
			{
				retValue = new MPeriodControl (ctx, rs, trxName);
			}
		}
		catch (Exception e)
		{
//			log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

		return retValue;
	}
}
