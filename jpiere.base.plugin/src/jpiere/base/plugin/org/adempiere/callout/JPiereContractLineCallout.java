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

import java.util.Properties;

import org.adempiere.base.IColumnCallout;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.util.Msg;

import jpiere.base.plugin.org.adempiere.model.MContractCalender;
import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractLine;
import jpiere.base.plugin.org.adempiere.model.MContractLineT;
import jpiere.base.plugin.org.adempiere.model.MContractProcPeriod;

/**
 *
 *  JPiere Contract Content CallOut
 *
 *  JPIERE-0363:JPBP
 *
 * @author Hideaki Hagiwara
 *
 */
public class JPiereContractLineCallout implements IColumnCallout {

	@Override
	public String start(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{

		if(mField.getColumnName().equals("JP_ContractCalender_InOut_ID"))
		{
			if( value != null && mTab.getValue("JP_DerivativeDocPolicy_InOut").equals(MContractLine.JP_DERIVATIVEDOCPOLICY_INOUT_Lump))
			{
				int JP_ContractContent_ID =  ((Integer)mTab.getValue("JP_ContractContent_ID")).intValue();
				int JP_ContractLineT_ID = ((Integer)mTab.getValue("JP_ContractLineT_ID")).intValue();
				MContractContent content= MContractContent.get(ctx, JP_ContractContent_ID);
				MContractLineT lineTemplate = MContractLineT.get(ctx, JP_ContractLineT_ID);
				
				if(content.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceipt)
						|| content.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceiptInvoice))
				{
					int processPeriodOffset = lineTemplate.getJP_ContractProcPOffset_InOut();
					if(processPeriodOffset > 0)
						processPeriodOffset++;
					else
						processPeriodOffset--;
					
					
					int JP_ContractCalender_ID = ((Integer)value).intValue();
					MContractCalender calender = MContractCalender.get(ctx, JP_ContractCalender_ID);
					MContractProcPeriod period = calender.getContractProcessPeriod(ctx,content.getJP_ContractProcDate_From() ,processPeriodOffset);
					if(period == null)
						return Msg.getMsg(ctx, "NotFound") +" : " +Msg.getElement(ctx, "JP_ContractProcPeriod_ID");
					
					mTab.setValue ("JP_ContractProcPeriod_InOut_ID", period.getJP_ContractProcPeriod_ID());
				}
			}
			
		}else if(mField.getColumnName().equals("JP_ContractCalender_Inv_ID")){
			
			if( value != null && mTab.getValue("JP_DerivativeDocPolicy_Inv").equals(MContractLine.JP_DERIVATIVEDOCPOLICY_INV_Lump))
			{
				int JP_ContractContent_ID =  ((Integer)mTab.getValue("JP_ContractContent_ID")).intValue();
				int JP_ContractLineT_ID = ((Integer)mTab.getValue("JP_ContractLineT_ID")).intValue();
				MContractContent content= MContractContent.get(ctx, JP_ContractContent_ID);
				MContractLineT lineTemplate = MContractLineT.get(ctx, JP_ContractLineT_ID);
				
				if(content.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateInvoice)
						|| content.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceiptInvoice))
				{
					int processPeriodOffset = lineTemplate.getJP_ContractProcPOffset_Inv();
					if(processPeriodOffset > 0)
						processPeriodOffset++;
					else
						processPeriodOffset--;
					
					
					int JP_ContractCalender_ID = ((Integer)value).intValue();
					MContractCalender calender = MContractCalender.get(ctx, JP_ContractCalender_ID);
					MContractProcPeriod period = calender.getContractProcessPeriod(ctx,content.getJP_ContractProcDate_From() ,processPeriodOffset);
					if(period == null)
						return Msg.getMsg(ctx, "NotFound") +" : " +Msg.getElement(ctx, "JP_ContractProcPeriod_ID");
					
					mTab.setValue ("JP_ContractProcPeriod_Inv_ID", period.getJP_ContractProcPeriod_ID());
				}
			}
			
		}

		return "";
	}
	
}
