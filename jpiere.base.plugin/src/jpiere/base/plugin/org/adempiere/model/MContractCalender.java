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

package jpiere.base.plugin.org.adempiere.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.util.CCache;
import org.compiere.util.DB;

/**
 * JPIERE-0363
 *
 * @author Hideaki Hagiwara
 *
 */
public class MContractCalender extends X_JP_ContractCalender {
	
	public MContractCalender(Properties ctx, int JP_ContractCalender_ID, String trxName) 
	{
		super(ctx, JP_ContractCalender_ID, trxName);
	}
	
	public MContractCalender(Properties ctx, ResultSet rs, String trxName) 
	{
		super(ctx, rs, trxName);
	}
	
	
	/**	Cache				*/
	private static CCache<Integer,MContractCalender>	s_cache = new CCache<Integer,MContractCalender>(Table_Name, 20);
	
	/**
	 * 	Get from Cache
	 *	@param ctx context
	 *	@param JP_ContractCalender_ID id
	 *	@return Contract Calender
	 */
	public static MContractCalender get (Properties ctx, int JP_ContractCalender_ID)
	{
		Integer ii = new Integer (JP_ContractCalender_ID);
		MContractCalender retValue = (MContractCalender)s_cache.get(ii);
		if (retValue != null)
			return retValue;
		retValue = new MContractCalender (ctx, JP_ContractCalender_ID, null);
		if (retValue.get_ID () != 0)
			s_cache.put (JP_ContractCalender_ID, retValue);
		return retValue;
	}	//	get
	
	
	/**
	 * 
	 * @param ctx
	 * @param date_From
	 * @param processPeriodNum
	 * @return
	 */
	public MContractProcPeriod getContractProcessPeriod(Properties ctx, Timestamp date_From, Timestamp date_To, int processPeriodNum)
	{
		if(date_From == null)
			return null;
		
		int JP_ContractProcPeriod_ID = 0;
		
		if(processPeriodNum == 0)
		{			
			final String sql = "SELECT JP_ContractProcPeriod_ID FROM JP_ContractProcPeriod "
												+ "WHERE StartDate <= ? AND EndDate >=? AND JP_ContractCalender_ID=? ";
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				pstmt = DB.prepareStatement(sql, get_TrxName());
				pstmt.setTimestamp(1, date_From);
				if(date_To==null)
					pstmt.setTimestamp(2, date_From);
				else
					pstmt.setTimestamp(2, date_To);
				pstmt.setInt(3, getJP_ContractCalender_ID());
				rs = pstmt.executeQuery();
				if (rs.next())
				{
					JP_ContractProcPeriod_ID = rs.getInt(1);
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
			
		}else if (processPeriodNum > 0){
			
			final String sql = "SELECT JP_ContractProcPeriod_ID FROM JP_ContractProcPeriod "
												+ "WHERE EndDate >=? AND JP_ContractCalender_ID=? "
												+ " ORDER BY EndDate ASC";
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				pstmt = DB.prepareStatement(sql, get_TrxName());
				pstmt.setTimestamp(1, date_From);
				pstmt.setInt(2, getJP_ContractCalender_ID());
				pstmt.setMaxRows(processPeriodNum);
				rs = pstmt.executeQuery();
				int i = 0;
				while (rs.next())
				{
					i++;
					if(i == processPeriodNum)
					{
						JP_ContractProcPeriod_ID = rs.getInt(1);
					}

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

		}else{
			
			processPeriodNum = processPeriodNum *-1;
			
			final String sql = "SELECT JP_ContractProcPeriod_ID FROM JP_ContractProcPeriod "
											+ "WHERE StartDate <=? AND JP_ContractCalender_ID=? "
											+ " ORDER BY StartDate DESC ";
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				pstmt = DB.prepareStatement(sql, get_TrxName());
				pstmt.setTimestamp(1, date_From);
				pstmt.setInt(2, getJP_ContractCalender_ID());
				pstmt.setMaxRows(processPeriodNum);
				rs = pstmt.executeQuery();
				int i = 0;
				while (rs.next())
				{
					i++;
					if(i == processPeriodNum)
					{
						JP_ContractProcPeriod_ID = rs.getInt(1);
					}

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
		
		if(JP_ContractProcPeriod_ID == 0)
		{
			return null;
		}else{
			return new MContractProcPeriod(ctx, JP_ContractProcPeriod_ID, get_TrxName());
		}
	}
	
	public MContractProcPeriod getContractProcessPeriod(Properties ctx, Timestamp date_From, Timestamp date_To)
	{
		return getContractProcessPeriod(ctx, date_From,  date_To, 0);
	}
	
	public MContractProcPeriod getContractProcessPeriod(Properties ctx, Timestamp date_From)
	{
		return getContractProcessPeriod(ctx, date_From, null, 0);
	}
}
