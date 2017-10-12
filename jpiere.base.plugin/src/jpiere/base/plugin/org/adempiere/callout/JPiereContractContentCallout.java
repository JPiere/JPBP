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
import java.util.Properties;

import org.adempiere.base.IColumnCallout;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.MDocType;
import org.compiere.util.Env;
import org.compiere.util.Msg;

import jpiere.base.plugin.org.adempiere.model.MContract;
import jpiere.base.plugin.org.adempiere.model.MContractCalender;
import jpiere.base.plugin.org.adempiere.model.MContractContentT;
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
public class JPiereContractContentCallout implements IColumnCallout {

	@Override
	public String start(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{

		if(mField.getColumnName().equals("JP_BaseDocDocType_ID"))
		{
			if( value == null)
			{
				mTab.setValue ("OrderType",  "--");
			}else{
				
				Integer JP_BaseDocDocType_ID = (Integer)value;
				MDocType docType = MDocType.get(ctx, JP_BaseDocDocType_ID.intValue());
				mTab.setValue("IsSOTrx", docType.isSOTrx());
				
				if(docType.getDocBaseType().equals(MDocType.DOCBASETYPE_SalesOrder)
						|| docType.getDocBaseType().equals(MDocType.DOCBASETYPE_PurchaseOrder))
				{
					String DocSubTypeSO = docType.getDocSubTypeSO();
					mTab.setValue ("OrderType", DocSubTypeSO);
					
					if(!docType.getDocSubTypeSO().equals(MDocType.DOCSUBTYPESO_StandardOrder)
							&& !docType.getDocSubTypeSO().equals(MDocType.DOCSUBTYPESO_Quotation)
							&& !docType.getDocSubTypeSO().equals(MDocType.DOCSUBTYPESO_Proposal) )
					{
						String JP_ContractType = (String)Env.getContext(ctx, WindowNo, "JP_ContractType");
						if(JP_ContractType.equals("PDC"))
							mTab.setValue("JP_CreateDerivativeDocPolicy", "MA");
						else if(JP_ContractType.equals("STC"))
							mTab.setValue("JP_CreateDerivativeDocPolicy", null);
					}
					
				}else{
					mTab.setValue ("OrderType", "--");	
					mTab.setValue("JP_CreateDerivativeDocPolicy", null);
				}
				

			}
			
		}else if(mField.getColumnName().equals("JP_ContractCalender_ID")){
			
			if( value != null)
			{
				int JP_ContractContentT_ID =  ((Integer)mTab.getValue("JP_ContractContentT_ID")).intValue();
				MContractContentT contentTemplate= MContractContentT.get(ctx, JP_ContractContentT_ID);
				int JP_Contract_ID = ((Integer)mTab.getValue("JP_Contract_ID")).intValue();
				MContract contract = MContract.get(ctx, JP_Contract_ID);
				
				//Calculate JP_ContractProcDate_From
				if(contentTemplate.getJP_ContractProcPOffset()==0)
				{
					mTab.setValue ("JP_ContractProcDate_From", contract.getJP_ContractPeriodDate_From());
					
				}else{
					
					int processPeriodOffset = contentTemplate.getJP_ContractProcPOffset();
					if(processPeriodOffset > 0)
						processPeriodOffset++;
					else
						processPeriodOffset--;
					
					int JP_ContractCalender_ID = ((Integer)value).intValue();
					MContractCalender calender = MContractCalender.get(ctx, JP_ContractCalender_ID);
					MContractProcPeriod period = calender.getContractProcessPeriod(ctx, contract.getJP_ContractPeriodDate_From(), null ,processPeriodOffset);
					if(period == null)
						return Msg.getMsg(ctx, "NotFound") +" : " +Msg.getElement(ctx, "JP_ContractProcPeriod_ID");
					
					mTab.setValue ("JP_ContractProcDate_From", period.getStartDate());
				}

				//Calculate JP_ContractProcDate_To
				if(contentTemplate.getJP_ContractProcPeriodNum()==0)
				{
					mTab.setValue ("JP_ContractProcDate_To", contract.getJP_ContractPeriodDate_To());
				}else{
					
					int JP_ContractCalender_ID = ((Integer)value).intValue();
					MContractCalender calender = MContractCalender.get(ctx, JP_ContractCalender_ID);
					MContractProcPeriod period = calender.getContractProcessPeriod(ctx, (Timestamp)mTab.getValue("JP_ContractProcDate_From"), null
																		,contentTemplate.getJP_ContractProcPeriodNum());	
					
					if(period == null)
						return Msg.getMsg(ctx, "NotFound") +" : " +Msg.getElement(ctx, "JP_ContractProcPeriod_ID");
					
					mTab.setValue ("JP_ContractProcDate_To", period.getEndDate());
				}
			}
			
		}

		return "";
	}
	
}
