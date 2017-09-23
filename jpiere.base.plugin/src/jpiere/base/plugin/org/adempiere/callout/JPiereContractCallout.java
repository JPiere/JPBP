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
package jpiere.base.plugin.org.adempiere.callout;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Properties;

import org.adempiere.base.IColumnCallout;
import org.adempiere.util.Callback;
import org.adempiere.webui.window.FDialog;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.util.Msg;

import jpiere.base.plugin.org.adempiere.model.MContractCancelTerm;
import jpiere.base.plugin.org.adempiere.model.MContractExtendPeriod;
import jpiere.base.plugin.org.adempiere.model.MContractT;

/**
 *
 *  JPiere Contract CallOut
 *
 *  JPIERE-0363:JPBP
 *
 * @author Hideaki Hagiwara
 *
 */
public class JPiereContractCallout implements IColumnCallout {

	@Override
	public String start(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{

		if(mField.getColumnName().equals("JP_ContractT_ID") && value != null)
		{

			FDialog.ask(WindowNo, null, "JP_UpdateContractByTemplate", new Callback<Boolean>() //Would you like to update contract document by template?
			{

				@Override
				public void onCallback(Boolean result)
				{
					if(result)
					{
						updateContract(ctx, WindowNo, mTab, mField, value, oldValue) ;
					}else{
						;//Noting to do
					}
		        }

			});//FDialog.
			
		}else if(mField.getColumnName().equals("JP_ContractPeriodDate_To") && value != null){
						
			boolean IsAutomaticUpdateJP = (boolean)mTab.getValue("IsAutomaticUpdateJP");
			
			if(IsAutomaticUpdateJP && mTab.getValue("JP_ContractCancelTerm_ID") != null)
			{
				mTab.setValue("JP_ContractCancelDeadline", calculateCancelDeadLine(ctx, mTab, (Timestamp)value) );
			}
			
		}else if(mField.getColumnName().equals("JP_ContractCancelTerm_ID") && value != null){
			
			boolean IsAutomaticUpdateJP = (boolean)mTab.getValue("IsAutomaticUpdateJP");
			
			if(IsAutomaticUpdateJP && mTab.getValue("JP_ContractPeriodDate_To") != null)
			{
				mTab.setValue("JP_ContractCancelDeadline", calculateCancelDeadLine(ctx, mTab, (Timestamp)mTab.getValue("JP_ContractPeriodDate_To") ) );
			}
			
		}else if(mField.getColumnName().equals("JP_ContractCancelDate") && value != null){

			GridField  G_ContractPeriodDate_To = mTab.getField("JP_ContractPeriodDate_To");
			Timestamp JP_ContractPeriodDate_To = (Timestamp)G_ContractPeriodDate_To.getValue();
			Timestamp JP_ContractCancelDate = (Timestamp)value;
			
			if(JP_ContractPeriodDate_To == null)
				return "";
			
			if(JP_ContractCancelDate.compareTo(JP_ContractPeriodDate_To) < 0 )
			{
				mTab.setValue("JP_ContractCancelDate", (Timestamp)oldValue);
				//You can not enter contract cancel date before contract Period data(to).
				return Msg.getMsg(ctx, "JP_ContractCancelDate_UpdateError");
				
			}else{
				
				mTab.setValue("JP_ContractPeriodDate_To", (Timestamp)value);
				
			}
	
		}

		return "";
	}
	
	
	private void updateContract(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{
		MContractT contractTemplate = MContractT.get(ctx, ((Integer)value).intValue());
		mTab.setValue("JP_ContractType", contractTemplate.getJP_ContractType());
		mTab.setValue("JP_ContractCategory_ID", contractTemplate.getJP_ContractCategory_ID());
		mTab.setValue("C_DocType_ID", contractTemplate.getC_DocType_ID());
		mTab.setValue("Name", contractTemplate.getName());
		mTab.setValue("Description", contractTemplate.getDescription());
		mTab.setValue("IsAutomaticUpdateJP",contractTemplate.isAutomaticUpdateJP());
		if(contractTemplate.isAutomaticUpdateJP())
		{
			mTab.setValue("JP_ContractCancelTerm_ID", contractTemplate.getJP_ContractCancelTerm_ID());
			mTab.setValue("JP_ContractExtendPeriod_ID", contractTemplate.getJP_ContractExtendPeriod_ID());
			if(mTab.getValue("JP_ContractPeriodDate_From") != null && mTab.getValue("JP_ContractPeriodDate_To") == null)
			{
				LocalDateTime JP_ContractPeriodDate_From = ((Timestamp)mTab.getValue("JP_ContractPeriodDate_From")).toLocalDateTime();
				Timestamp JP_ContractPeriodDate_To = calculatePeriodEndDate(ctx, mTab, JP_ContractPeriodDate_From.minusDays(1) );
				
				mTab.setValue("JP_ContractPeriodDate_To", JP_ContractPeriodDate_To);
			}
		}
	}
	
	private Timestamp calculatePeriodEndDate(Properties ctx, GridTab mTab, LocalDateTime old_PeriodEndDate)
	{
		int JP_ContractExtendPeriod_ID = ((Integer)mTab.getValue("JP_ContractExtendPeriod_ID")).intValue();
		MContractExtendPeriod contractExtendPeriod = MContractExtendPeriod.get(ctx, JP_ContractExtendPeriod_ID);
		return contractExtendPeriod.calculateNewPeriodEndDate(old_PeriodEndDate);
	}
	
	private Timestamp calculateCancelDeadLine(Properties ctx, GridTab mTab, Timestamp JP_ContractPeriodDate_To)
	{
		int JP_ContractCancelTerm_ID = ((Integer)mTab.getValue("JP_ContractCancelTerm_ID")).intValue();
		MContractCancelTerm cancelTerm =  MContractCancelTerm.get(ctx, JP_ContractCancelTerm_ID);	
		return cancelTerm.calculateCancelDeadLine(JP_ContractPeriodDate_To);
	}

}
