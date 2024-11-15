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
public class MContractCancelTerm extends X_JP_ContractCancelTerm {

	private static final long serialVersionUID = 5383388285777608254L;

	public MContractCancelTerm(Properties ctx, int JP_ContractCancelTerm_ID, String trxName)
	{
		super(ctx, JP_ContractCancelTerm_ID, trxName);
	}

	public MContractCancelTerm(Properties ctx, ResultSet rs, String trxName)
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
	private static CCache<Integer,MContractCancelTerm>	s_cache = new CCache<Integer,MContractCancelTerm>(Table_Name, 20);

	/**
	 * 	Get from Cache
	 *	@param ctx context
	 *	@param JP_ContractCancelTerm_ID id
	 *	@return Contract Process Period
	 */
	public static MContractCancelTerm get (Properties ctx, int JP_ContractCancelTerm_ID)
	{
		Integer ii = Integer.valueOf(JP_ContractCancelTerm_ID);
		MContractCancelTerm retValue = (MContractCancelTerm)s_cache.get(ii);
		if (retValue != null)
			return retValue;
		retValue = new MContractCancelTerm (ctx, JP_ContractCancelTerm_ID, null);
		if (retValue.get_ID () != 0)
			s_cache.put (JP_ContractCancelTerm_ID, retValue);
		return retValue;
	}	//	get

	public Timestamp calculateCancelDeadLine(Timestamp JP_ContractPeriodDate_To)
	{
		return calculateCancelDeadLine(JP_ContractPeriodDate_To.toLocalDateTime());
	}

	public Timestamp calculateCancelDeadLine(LocalDateTime JP_ContractPeriodDate_To)
	{
		if(isDueFixed())
		{
			JP_ContractPeriodDate_To = JP_ContractPeriodDate_To.minusYears(getJP_Year()).minusMonths(getJP_Month());
			if(getJP_Day() == 31)
			{
				return Timestamp.valueOf(JP_ContractPeriodDate_To.plusMonths(1).withDayOfMonth(1).minusDays(1));
			}else if(getJP_Day() == 0){
				return Timestamp.valueOf(JP_ContractPeriodDate_To);
			}else{
				return Timestamp.valueOf(JP_ContractPeriodDate_To.withDayOfMonth(getJP_Day()) );
			}
		}else{
		   return Timestamp.valueOf(JP_ContractPeriodDate_To.minusYears(getJP_Year()).minusMonths(getJP_Month()).minusDays(getJP_Day()) );
		}
	}
}
