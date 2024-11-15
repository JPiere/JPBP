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

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Properties;

import org.compiere.util.CCache;
import org.compiere.util.Msg;


/** JPIERE-0363
*
* @author Hideaki Hagiwara
*
*/
public class MContractExtendPeriod extends X_JP_ContractExtendPeriod {

	private static final long serialVersionUID = 4459407024636353742L;

	public MContractExtendPeriod(Properties ctx, int JP_ContractExtendPeriod_ID, String trxName)
	{
		super(ctx, JP_ContractExtendPeriod_ID, trxName);
	}

	public MContractExtendPeriod(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}


	@Override
	protected boolean beforeSave(boolean newRecord)
	{
		if(newRecord || is_ValueChanged("JP_Day") || is_ValueChanged("IsDueFixed"))
		{
			if(isDueFixed())
			{
				if(getJP_Day() > 31 || getJP_Day() <= 0)
				{
					log.saveError("Error", Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "JP_Day"));
					return false;
				}
			}
		}

		return true;
	}


	/**	Cache				*/
	private static CCache<Integer,MContractExtendPeriod>	s_cache = new CCache<Integer,MContractExtendPeriod>(Table_Name, 20);

	/**
	 * 	Get from Cache
	 *	@param ctx context
	 *	@param JP_ContractExxtendPeriod_ID id
	 *	@return Contract Process Period
	 */
	public static MContractExtendPeriod get (Properties ctx, int JP_ContractExxtendPeriod_ID)
	{
		Integer ii = Integer.valueOf(JP_ContractExxtendPeriod_ID);
		MContractExtendPeriod retValue = (MContractExtendPeriod)s_cache.get(ii);
		if (retValue != null)
			return retValue;
		retValue = new MContractExtendPeriod (ctx, JP_ContractExxtendPeriod_ID, null);
		if (retValue.get_ID () != 0)
			s_cache.put (JP_ContractExxtendPeriod_ID, retValue);
		return retValue;
	}	//	get


	public Timestamp calculateNewPeriodEndDate(Timestamp old_PeriodEndDate)
	{
		return calculateNewPeriodEndDate(old_PeriodEndDate.toLocalDateTime());
	}

	public Timestamp calculateNewPeriodEndDate(LocalDateTime old_PeriodEndDate)
	{
		if(isDueFixed())
		{
			old_PeriodEndDate = old_PeriodEndDate.plusYears(getJP_Year()).plusMonths(getJP_Month());
			if(getJP_Day() == 31)
			{
				return Timestamp.valueOf(old_PeriodEndDate.plusMonths(1).withDayOfMonth(1).minusDays(1));
			}else if(getJP_Day() == 0){
				return Timestamp.valueOf(old_PeriodEndDate);
			}else{
				return Timestamp.valueOf(old_PeriodEndDate.withDayOfMonth(getJP_Day()));
			}

		}else{
		   return Timestamp.valueOf(old_PeriodEndDate.plusYears(getJP_Year()).plusMonths(getJP_Month()).plusDays(getJP_Day()) );
		}
	}

}
