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
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;

import org.compiere.model.X_C_NonBusinessDay;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Msg;

/**
 *
 * JPIERE-0269:Create Not Business Day
 *
 * @author
 *
 */
public class CreateNonBusinessDay extends SvrProcess {

	private int p_AD_Clinet_ID = 0;

	private int p_C_Calendar_ID= 0;

	private Timestamp p_startDate = null;
	private Timestamp p_endDate = null;

	private int p_AD_Org_ID = 0;

	private int p_C_Coutry_ID = 0;

	private String p_Name = null;

	private boolean onSunday;
	private boolean onMonday;
	private boolean onTuesday;
	private boolean onWednesday;
	private boolean onThursday;
	private boolean onFriday;
	private boolean onSaturday;

	@Override
	protected void prepare() {

		p_AD_Clinet_ID = getAD_Client_ID();

		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null){
				;
			}else if (name.equals("C_Calendar_ID")){
				p_C_Calendar_ID = para[i].getParameterAsInt();

			}else if (name.equals("StartDate")){
				p_startDate = para[i].getParameterAsTimestamp();
				p_endDate = para[i].getParameter_ToAsTimestamp();

			}else if(name.equals("AD_Org_ID")){
				p_AD_Org_ID = para[i].getParameterAsInt();

			}else if(name.equals("C_Country_ID")){
				p_C_Coutry_ID = para[i].getParameterAsInt();

			}else if(name.equals("Name")){
				p_Name = para[i].getParameterAsString();


			}else if(name.equals("OnMonday")){
				onMonday = para[i].getParameterAsBoolean();
			}else if(name.equals("OnTuesday")){
				onTuesday = para[i].getParameterAsBoolean();
			}else if(name.equals("OnWednesday")){
				onWednesday = para[i].getParameterAsBoolean();
			}else if(name.equals("OnThursday")){
				 onThursday= para[i].getParameterAsBoolean();
			}else if(name.equals("OnFriday")){
				onFriday = para[i].getParameterAsBoolean();
			}else if(name.equals("OnSaturday")){
				onSaturday = para[i].getParameterAsBoolean();
			}else if(name.equals("OnSunday")){
				onSunday = para[i].getParameterAsBoolean();

            }else{
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}
		}

	}

	@Override
	protected String doIt() throws Exception {

	    int recordCount = 0;
		try{

		    Calendar calendarOfTargetDate = Calendar.getInstance();
		    calendarOfTargetDate.setTime(p_startDate);

		    Calendar calendarOfTargetEndDate = Calendar.getInstance();
		    calendarOfTargetEndDate.setTime(p_endDate);


		    boolean isFinished = false;
		    while( !isFinished ){
		    	if( isHoliday( calendarOfTargetDate ) )
		    	{
		    		Date hDate = calendarOfTargetDate.getTime();
		    		Timestamp holidayDate = new Timestamp(hDate.getTime());


		    		if(isExistHoliday(holidayDate, p_C_Coutry_ID, p_AD_Org_ID))
		    		{
		    			;//Nothing to do, because of Already exist holiday;
		    		}else{
			    		X_C_NonBusinessDay nonBizDay = new X_C_NonBusinessDay(getCtx(), 0, get_TrxName() );
			    		nonBizDay.setAD_Org_ID(p_AD_Org_ID);
			    		nonBizDay.setC_Calendar_ID(p_C_Calendar_ID);
			    		nonBizDay.setName(p_Name);
			    		nonBizDay.setC_Country_ID(p_C_Coutry_ID);
			    		nonBizDay.setDate1(holidayDate);
			    		nonBizDay.setIsActive(true);

			    		nonBizDay.saveEx(get_TrxName());

			    		recordCount++;
		    		}
		    	}

		    	calendarOfTargetDate.add(Calendar.DAY_OF_MONTH, 1 );

		    	if( calendarOfTargetDate.after(calendarOfTargetEndDate) ){
		    		isFinished = true;
		    	}
		    }
		}catch( Exception e ){
			log.log(Level.SEVERE, e.toString());
		}

		return Msg.getMsg(getCtx(), "Created") +" : " +String.valueOf(recordCount) ;
	}


	protected boolean isHoliday( Calendar calendar ){
		boolean isHoliday = false;

		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
    	if( dayOfWeek == Calendar.SUNDAY && onSunday )				isHoliday = true;
    	else if( dayOfWeek == Calendar.MONDAY && onMonday )			isHoliday = true;
    	else if( dayOfWeek == Calendar.TUESDAY && onTuesday )		isHoliday = true;
    	else if( dayOfWeek == Calendar.WEDNESDAY && onWednesday )	isHoliday = true;
    	else if( dayOfWeek == Calendar.THURSDAY && onThursday )		isHoliday = true;
    	else if( dayOfWeek == Calendar.FRIDAY && onFriday )			isHoliday = true;
    	else if( dayOfWeek == Calendar.SATURDAY && onSaturday )		isHoliday = true;

		return isHoliday;
	}



	private boolean isExistHoliday(Timestamp holidayDate, int C_Country_ID, int AD_Org_ID )
	{
		StringBuilder sql = new StringBuilder("SELECT Date1 FROM C_NonBusinessDay");
		if(C_Country_ID > 0)
			sql.append(" WHERE AD_Client_ID=? AND C_Calendar_ID=? AND AD_Org_ID=? AND C_Country_ID=? AND Date1=? " );
		else
			sql.append(" WHERE AD_Client_ID=? AND C_Calendar_ID=? AND AD_Org_ID=? AND C_Country_ID IS NULL AND Date1=? " );

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (sql.toString(), null);
			pstmt.setInt(1, p_AD_Clinet_ID);
			pstmt.setInt(2, p_C_Calendar_ID);
			pstmt.setInt(3, AD_Org_ID);
			if(C_Country_ID > 0)
			{
				pstmt.setInt(4, C_Country_ID);
				pstmt.setTimestamp(5, holidayDate);
			}else{
				pstmt.setTimestamp(4, holidayDate);
			}

			rs = pstmt.executeQuery ();
			if (rs.next ())
				return true;

		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, e.toString());
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}


		return false;
	}

}
