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
import java.time.Duration;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.util.CCache;
import org.compiere.util.DB;
import org.compiere.util.Msg;


/** JPIERE-0363
*
* @author Hideaki Hagiwara
*
*/
public class MContractProcPeriod extends X_JP_ContractProcPeriod {

	private static final long serialVersionUID = 1167588861374995519L;

	public MContractProcPeriod(Properties ctx, int JP_ContractProcPeriod_ID, String trxName)
	{
		super(ctx, JP_ContractProcPeriod_ID, trxName);
	}

	public MContractProcPeriod(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}


	/**	Cache				*/
	private static CCache<Integer,MContractProcPeriod>	s_cache = new CCache<Integer,MContractProcPeriod>(Table_Name, 20);

	/**
	 * 	Get from Cache
	 *	@param ctx context
	 *	@param JP_ContractProcPeriod_ID id
	 *	@return Contract Process Period
	 */
	public static MContractProcPeriod get (Properties ctx, int JP_ContractProcPeriod_ID)
	{
		Integer ii = Integer.valueOf(JP_ContractProcPeriod_ID);
		MContractProcPeriod retValue = (MContractProcPeriod)s_cache.get(ii);
		if (retValue != null)
			return retValue;
		retValue = new MContractProcPeriod (ctx, JP_ContractProcPeriod_ID, null);
		if (retValue.get_ID () != 0)
			s_cache.put (JP_ContractProcPeriod_ID, retValue);
		return retValue;
	}	//	get


	@Override
	protected boolean beforeSave(boolean newRecord)
	{

		if(newRecord || ( is_ValueChanged("StartDate") || is_ValueChanged("EndDate") ) )
		{
			boolean isUnique = true;

			//Check Overlapping form StartDate to EndDate
			final String sql = "SELECT JP_ContractProcPeriod_ID FROM JP_ContractProcPeriod WHERE StartDate <= ? AND EndDate >=? AND JP_ContractCalender_ID=? ";
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				pstmt = DB.prepareStatement(sql, get_TrxName());
				pstmt.setTimestamp(1, getEndDate());
				pstmt.setTimestamp(2, getStartDate());
				pstmt.setInt(3, getJP_ContractCalender_ID());
				rs = pstmt.executeQuery();
				while (rs.next())
				{
					int JP_ContractProcPeriod_ID = rs.getInt(1);
					if(getJP_ContractProcPeriod_ID() == JP_ContractProcPeriod_ID)
					{
						isUnique = true;
					}else{
						isUnique = false;
						break;
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

			if(!isUnique)
			{
				log.saveError("Error", Msg.getMsg(getCtx(), "JP_OverlapPeriod"));//Overlap Period
				return false;
			}

		}



		if(newRecord || is_ValueChanged("DateAcct") || ( is_ValueChanged("StartDate") || is_ValueChanged("EndDate") ) )
		{
			if(getStartDate().compareTo(getDateAcct()) > 0 || getEndDate().compareTo(getDateAcct()) < 0)
			{
				log.saveError("Error", Msg.getMsg(getCtx(), "JP_OutsidePperiod") + " : "
						+ Msg.getElement(getCtx(), "DateAcct"));//outside the specified period
				return false;
			}
		}


		return true;
	}

	@Override
	public String toString()
	{
		return getName()+" StartDate:"+getStartDate().toString().substring(0,10) + " - EndDate:" + getEndDate().toString().substring(0,10);
	}

	public boolean isContainedBaseDocContractProcPeriod(int BaseDoc_ContractProcPeriod_ID)
	{
		return isContainedBaseDocContractProcPeriod(MContractProcPeriod.get(getCtx(), BaseDoc_ContractProcPeriod_ID));
	}


	public boolean isContainedBaseDocContractProcPeriod(MContractProcPeriod BaseDoc_ContractProcPeriod)
	{
		if(getStartDate().compareTo(BaseDoc_ContractProcPeriod.getStartDate()) >= 0
				&& getEndDate().compareTo(BaseDoc_ContractProcPeriod.getEndDate())<= 0 )
		{
			return true;
		}else{
			return false;
		}
	}

	public long getContractProcPeriodDays()
	{
		return Duration.between(getStartDate().toLocalDateTime(), getEndDate().toLocalDateTime()).toDays()+1;
	}
}
