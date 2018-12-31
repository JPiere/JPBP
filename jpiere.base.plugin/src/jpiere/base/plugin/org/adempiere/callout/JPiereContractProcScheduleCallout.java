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

import jpiere.base.plugin.org.adempiere.model.MContractCalender;
import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractProcPeriod;
import jpiere.base.plugin.org.adempiere.model.MContractProcSchedule;

/**
 *
 *  JPiere Contract Process Schedule
 *
 *  JPIERE-0431:JPBP
 *
 * @author Hideaki Hagiwara
 *
 */
public class JPiereContractProcScheduleCallout implements IColumnCallout {

	@Override
	public String start(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{

		if(mField.getColumnName().equals("JP_ContractContent_ID"))
		{

			if( value != null)
			{
				int JP_ContractContent_ID =  Integer.parseInt(value.toString());
				MContractContent contractContent= MContractContent.get(ctx, JP_ContractContent_ID);
				GridField[] fields = mTab.getFields();
				String columnName = null;
				int columnIndex = -1;
				Object objectValue = null;
				for(int i = 0 ; i < fields.length; i++)
				{
					columnName = fields[i].getColumnName();
					columnIndex = -1;
					objectValue = null;
					if(columnName.equals("JP_ContractContent_ID")
							|| columnName.equals("JP_ContractProcSchedule_ID")
							|| columnName.equals("JP_ContractProcSchedule_UU")
							|| columnName.equals("AD_Client_ID")
							|| columnName.equals("IsActive")
							|| columnName.equals("Created")
							|| columnName.equals("CreatedBy")
							|| columnName.equals("Updated")
							|| columnName.equals("UpdatedBy")
							|| columnName.equals("TotalLines")
							|| columnName.equals("Processed")
							|| columnName.equals("ProcessedOn")
						)
					{
						continue;
					}

					if(!fields[i].isAllowCopy())
						continue;

					columnIndex = contractContent.get_ColumnIndex(columnName);
					if(columnIndex > -1)
					{
						objectValue = contractContent.get_Value(columnIndex);
						if(objectValue != null)
						{
							if(columnName.equals(MContractProcSchedule.COLUMNNAME_C_DocType_ID))
							{
								MDocType contractPSDocType = MDocType.get(ctx, ((Integer)objectValue).intValue());
								Object obj_ContractPSDocType_ID = contractPSDocType.get_Value("JP_ContractPSDocType_ID");
								if(obj_ContractPSDocType_ID != null)
									mTab.setValue(columnName, obj_ContractPSDocType_ID);

							}else {
								mTab.setValue(columnName, objectValue);
							}
						}
					}

				}//for

				//set Date
				calloutOfJP_ProcPeriod_DateAcct(ctx, WindowNo, mTab, mField, mTab.getValue("DateAcct"), null);

				//Set IsSOTrx
				Object obj_BaseDocDocType_ID = mTab.getValue("JP_BaseDocDocType_ID");
				if(obj_BaseDocDocType_ID != null)
				{
					MDocType docType = MDocType.get(ctx, ((Integer)obj_BaseDocDocType_ID).intValue());
					mTab.setValue("IsSOTrx", docType.isSOTrx());
				}else {

					mTab.setValue ("OrderType",  "--");

				}

			}

		}else if(mField.getColumnName().equals("JP_BaseDocDocType_ID")){

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

		}else if(mField.getColumnName().equals("DateAcct")) {

			return calloutOfJP_ProcPeriod_DateAcct(ctx, WindowNo, mTab, mField, value, oldValue);

		}

		return "";
	}

	/**
	 * Callout Of JP_ProcPeriod_End_Inv_Date
	 *
	 * @param ctx
	 * @param WindowNo
	 * @param mTab
	 * @param mField
	 * @param value
	 * @param oldValue
	 * @return
	 */
	private String calloutOfJP_ProcPeriod_DateAcct(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{
		Timestamp dateAcct = (Timestamp)value;
		if(dateAcct == null)
		{
			mTab.setValue("JP_ContractProcPeriod_ID", null);

		}else if(mTab.getValue("JP_ContractCalender_ID") != null){

			int JP_ContractCalender_ID = ((Integer)mTab.getValue("JP_ContractCalender_ID")).intValue();
			MContractCalender cc =MContractCalender.get(ctx, JP_ContractCalender_ID);
			MContractProcPeriod cpp = cc.getContractProcessPeriod(ctx, dateAcct);
			if(cpp != null)
			{
				mTab.setValue("JP_ContractProcPeriod_ID", cpp.getJP_ContractProcPeriod_ID());
			}
		}

		return "";
	}

}
