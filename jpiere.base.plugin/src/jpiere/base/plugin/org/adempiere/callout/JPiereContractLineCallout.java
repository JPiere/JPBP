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
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.MContractCalender;
import jpiere.base.plugin.org.adempiere.model.MContractCalenderList;
import jpiere.base.plugin.org.adempiere.model.MContractCalenderRef;
import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractLineT;
import jpiere.base.plugin.org.adempiere.model.MContractProcPeriod;
import jpiere.base.plugin.org.adempiere.model.MContractProcessList;
import jpiere.base.plugin.org.adempiere.model.MContractProcessRef;

/**
 *
 *  JPiere Contract Content Line CallOut
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
			return calloutOfJP_ContractCalender_InOut_ID(ctx, WindowNo, mTab, mField, value, oldValue);

		}else if(mField.getColumnName().equals("JP_ContractCalender_Inv_ID")){

			return calloutOfJP_ContractCalender_Inv_ID(ctx, WindowNo, mTab, mField, value, oldValue);

		}else if(mField.getColumnName().equals("JP_ContractLineT_ID")){

			return calloutOfJP_ContractLineT_ID(ctx, WindowNo, mTab, mField, value, oldValue);

		}else if(mField.getColumnName().equals("JP_ProcPeriod_Lump_ID")){

			return calloutOfJP_ProcPeriod_Lump_ID(ctx, WindowNo, mTab, mField, value, oldValue);

		}else if(mField.getColumnName().equals("JP_ProcPeriod_Lump_Date")){

			return calloutOfJP_ProcPeriod_Lump_Date(ctx, WindowNo, mTab, mField, value, oldValue);

		}else if(mField.getColumnName().equals("JP_ProcPeriod_Start_ID")){

			return calloutOfJP_ProcPeriod_Start_ID(ctx, WindowNo, mTab, mField, value, oldValue);

		}else if(mField.getColumnName().equals("JP_ProcPeriod_Start_Date")){

			return calloutOfJP_ProcPeriod_Start_Date(ctx, WindowNo, mTab, mField, value, oldValue);

		}else if(mField.getColumnName().equals("JP_ProcPeriod_End_ID")){

			return calloutOfJP_ProcPeriod_End_ID(ctx, WindowNo, mTab, mField, value, oldValue);

		}else if(mField.getColumnName().equals("JP_ProcPeriod_End_Date")){

			return calloutOfJP_ProcPeriod_End_Date(ctx, WindowNo, mTab, mField, value, oldValue);

		}else if(mField.getColumnName().equals("JP_ProcPeriod_Lump_InOut_ID")) {

			return calloutOfJP_ProcPeriod_Lump_InOut_ID(ctx, WindowNo, mTab, mField, value, oldValue);

		}else if(mField.getColumnName().equals("JP_ProcPeriod_Lump_InOut_Date")) {

			return calloutOfJP_ProcPeriod_Lump_InOut_Date(ctx, WindowNo, mTab, mField, value, oldValue);

		}else if(mField.getColumnName().equals("JP_ProcPeriod_Start_InOut_ID")) {

			return calloutOfJP_ProcPeriod_Start_InOut_ID(ctx, WindowNo, mTab, mField, value, oldValue);

		}else if(mField.getColumnName().equals("JP_ProcPeriod_Start_InOut_Date")) {

			return calloutOfJP_ProcPeriod_Start_InOut_Date(ctx, WindowNo, mTab, mField, value, oldValue);

		}else if(mField.getColumnName().equals("JP_ProcPeriod_End_InOut_ID")) {

			return calloutOfJP_ProcPeriod_End_InOut_ID(ctx, WindowNo, mTab, mField, value, oldValue);

		}else if(mField.getColumnName().equals("JP_ProcPeriod_End_InOut_Date")) {

			return calloutOfJP_ProcPeriod_End_InOut_Date(ctx, WindowNo, mTab, mField, value, oldValue);

		}else if(mField.getColumnName().equals("JP_ProcPeriod_Lump_Inv_ID")) {

			return calloutOfJP_ProcPeriod_Lump_Inv_ID(ctx, WindowNo, mTab, mField, value, oldValue);

		}else if(mField.getColumnName().equals("JP_ProcPeriod_Lump_Inv_Date")) {

			return calloutOfJP_ProcPeriod_Lump_Inv_Date(ctx, WindowNo, mTab, mField, value, oldValue);

		}else if(mField.getColumnName().equals("JP_ProcPeriod_Start_Inv_ID")) {

			return calloutOfJP_ProcPeriod_Start_Inv_ID(ctx, WindowNo, mTab, mField, value, oldValue);

		}else if(mField.getColumnName().equals("JP_ProcPeriod_Start_Inv_Date")) {

			return calloutOfJP_ProcPeriod_Start_Inv_Date(ctx, WindowNo, mTab, mField, value, oldValue);

		}else if(mField.getColumnName().equals("JP_ProcPeriod_End_Inv_ID")) {

			return calloutOfJP_ProcPeriod_End_Inv_ID(ctx, WindowNo, mTab, mField, value, oldValue);

		}else if(mField.getColumnName().equals("JP_ProcPeriod_End_Inv_Date")) {

			return calloutOfJP_ProcPeriod_End_Inv_Date(ctx, WindowNo, mTab, mField, value, oldValue);

		}

		return "";
	}


	/**
	 * Callout Of JP_ContractCalender_InOut_ID Column
	 *
	 * @param ctx
	 * @param WindowNo
	 * @param mTab
	 * @param mField
	 * @param value
	 * @param oldValue
	 * @return
	 */
	private String calloutOfJP_ContractCalender_InOut_ID(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{
		if( value != null && mTab.getValue("JP_DerivativeDocPolicy_InOut") != null
				&& mTab.getValue("JP_DerivativeDocPolicy_InOut").equals("LP"))
		{
			int JP_ContractContent_ID =  ((Integer)mTab.getValue("JP_ContractContent_ID")).intValue();
			Object obj_JP_ContractLineT_ID = mTab.getValue("JP_ContractLineT_ID");
			if(obj_JP_ContractLineT_ID  != null)
			{
				int JP_ContractLineT_ID = ((Integer)obj_JP_ContractLineT_ID).intValue();
				MContractContent content= MContractContent.get(ctx, JP_ContractContent_ID);
				MContractLineT lineTemplate = MContractLineT.get(ctx, JP_ContractLineT_ID);

				if(content.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceipt)
						|| content.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceiptInvoice))
				{
					int processPeriodOffset = lineTemplate.getJP_ProcPeriodOffs_Lump_InOut();
					if(processPeriodOffset > 0)
						processPeriodOffset++;
					else
						processPeriodOffset--;


					int JP_ContractCalender_ID = ((Integer)value).intValue();
					MContractCalender calender = MContractCalender.get(ctx, JP_ContractCalender_ID);
					MContractProcPeriod period = calender.getContractProcessPeriod(ctx,content.getJP_ContractProcDate_From() , null ,processPeriodOffset);
					if(period == null)
						return Msg.getMsg(ctx, "NotFound") +" : " +Msg.getElement(ctx, "JP_ProcPeriod_Lump_InOut_ID");

					mTab.setValue ("JP_ProcPeriod_Lump_InOut_ID", period.getJP_ContractProcPeriod_ID());
					mTab.setValue ("JP_ProcPeriod_Lump_InOut_Date", period.getDateAcct());
				}
			}
		}

		if( value != null && mTab.getValue("JP_DerivativeDocPolicy_InOut") != null
				&& (mTab.getValue("JP_DerivativeDocPolicy_InOut").equals("PS") || mTab.getValue("JP_DerivativeDocPolicy_InOut").equals("PB")) )
		{
			int JP_ContractContent_ID =  ((Integer)mTab.getValue("JP_ContractContent_ID")).intValue();
			Object obj_JP_ContractLineT_ID = mTab.getValue("JP_ContractLineT_ID");
			if(obj_JP_ContractLineT_ID  != null)
			{
				int JP_ContractLineT_ID = ((Integer)obj_JP_ContractLineT_ID).intValue();
				MContractContent content= MContractContent.get(ctx, JP_ContractContent_ID);
				MContractLineT lineTemplate = MContractLineT.get(ctx, JP_ContractLineT_ID);

				if(content.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceipt)
						|| content.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceiptInvoice))
				{
					int processPeriodOffset = lineTemplate.getJP_ProcPeriodOffs_Start_InOut();
					if(processPeriodOffset > 0)
						processPeriodOffset++;
					else
						processPeriodOffset--;


					int JP_ContractCalender_ID = ((Integer)value).intValue();
					MContractCalender calender = MContractCalender.get(ctx, JP_ContractCalender_ID);
					MContractProcPeriod period = calender.getContractProcessPeriod(ctx,content.getJP_ContractProcDate_From() , null ,processPeriodOffset);
					if(period == null)
						return Msg.getMsg(ctx, "NotFound") +" : " +Msg.getElement(ctx, "JP_ProcPeriod_Start_InOut_ID");

					mTab.setValue ("JP_ProcPeriod_Start_InOut_ID", period.getJP_ContractProcPeriod_ID());
					mTab.setValue ("JP_ProcPeriod_Start_InOut_Date", period.getStartDate());
				}
			}
		}

		if( value != null  && mTab.getValue("JP_DerivativeDocPolicy_InOut") != null
				&& (mTab.getValue("JP_DerivativeDocPolicy_InOut").equals("PE") || mTab.getValue("JP_DerivativeDocPolicy_InOut").equals("PB")) )
		{
			int JP_ContractContent_ID =  ((Integer)mTab.getValue("JP_ContractContent_ID")).intValue();
			Object obj_JP_ContractLineT_ID = mTab.getValue("JP_ContractLineT_ID");
			if(obj_JP_ContractLineT_ID  != null)
			{
				int JP_ContractLineT_ID = ((Integer)obj_JP_ContractLineT_ID).intValue();
				MContractContent content= MContractContent.get(ctx, JP_ContractContent_ID);
				MContractLineT lineTemplate = MContractLineT.get(ctx, JP_ContractLineT_ID);

				if(content.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceipt)
						|| content.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceiptInvoice))
				{
					int processPeriodOffset = lineTemplate.getJP_ProcPeriodOffs_End_InOut();
					if(processPeriodOffset > 0)
						processPeriodOffset++;
					else
						processPeriodOffset--;


					int JP_ContractCalender_ID = ((Integer)value).intValue();
					MContractCalender calender = MContractCalender.get(ctx, JP_ContractCalender_ID);
					MContractProcPeriod period = calender.getContractProcessPeriod(ctx,content.getJP_ContractProcDate_From() , null ,processPeriodOffset);
					if(period == null)
						return Msg.getMsg(ctx, "NotFound") +" : " +Msg.getElement(ctx, "JP_ProcPeriod_End_InOut_ID");

					mTab.setValue ("JP_ProcPeriod_End_InOut_ID", period.getJP_ContractProcPeriod_ID());
					mTab.setValue ("JP_ProcPeriod_Endt_InOut_Date", period.getEndDate());
				}
			}
		}

		return "";
	}

	/**
	 *
	 * Callout Of JP_ContractCalender_Inv_ID Column
	 *
	 * @param ctx
	 * @param WindowNo
	 * @param mTab
	 * @param mField
	 * @param value
	 * @param oldValue
	 * @return
	 */
	private String calloutOfJP_ContractCalender_Inv_ID(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{
		if( value != null && mTab.getValue("JP_DerivativeDocPolicy_Inv") != null
				&& mTab.getValue("JP_DerivativeDocPolicy_Inv").equals("LP"))
		{
			int JP_ContractContent_ID =  ((Integer)mTab.getValue("JP_ContractContent_ID")).intValue();
			Object obj_JP_ContractLineT_ID = mTab.getValue("JP_ContractLineT_ID");
			if(obj_JP_ContractLineT_ID  != null)
			{
				int JP_ContractLineT_ID = ((Integer)obj_JP_ContractLineT_ID).intValue();
				MContractContent content= MContractContent.get(ctx, JP_ContractContent_ID);
				MContractLineT lineTemplate = MContractLineT.get(ctx, JP_ContractLineT_ID);

				if(content.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateInvoice)
						|| content.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceiptInvoice))
				{
					int processPeriodOffset = lineTemplate.getJP_ProcPeriodOffs_Lump_Inv();
					if(processPeriodOffset > 0)
						processPeriodOffset++;
					else
						processPeriodOffset--;


					int JP_ContractCalender_ID = ((Integer)value).intValue();
					MContractCalender calender = MContractCalender.get(ctx, JP_ContractCalender_ID);
					MContractProcPeriod period = calender.getContractProcessPeriod(ctx,content.getJP_ContractProcDate_From(), null ,processPeriodOffset);
					if(period == null)
						return Msg.getMsg(ctx, "NotFound") +" : " +Msg.getElement(ctx, "JP_ProcPeriod_Lump_Inv_ID");

					mTab.setValue ("JP_ProcPeriod_Lump_Inv_ID", period.getJP_ContractProcPeriod_ID());
					mTab.setValue ("JP_ProcPeriod_Lump_Inv_Date", period.getDateAcct());
				}
			}
		}

		if( value != null && mTab.getValue("JP_DerivativeDocPolicy_Inv") != null
				&& (mTab.getValue("JP_DerivativeDocPolicy_Inv").equals("PS") ||  mTab.getValue("JP_DerivativeDocPolicy_Inv").equals("PB")) )
		{
			int JP_ContractContent_ID =  ((Integer)mTab.getValue("JP_ContractContent_ID")).intValue();
			Object obj_JP_ContractLineT_ID = mTab.getValue("JP_ContractLineT_ID");
			if(obj_JP_ContractLineT_ID  != null)
			{
				int JP_ContractLineT_ID = ((Integer)obj_JP_ContractLineT_ID).intValue();
				MContractContent content= MContractContent.get(ctx, JP_ContractContent_ID);
				MContractLineT lineTemplate = MContractLineT.get(ctx, JP_ContractLineT_ID);

				if(content.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateInvoice)
						|| content.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceiptInvoice))
				{
					int processPeriodOffset = lineTemplate.getJP_ProcPeriodOffs_Start_Inv();
					if(processPeriodOffset > 0)
						processPeriodOffset++;
					else
						processPeriodOffset--;


					int JP_ContractCalender_ID = ((Integer)value).intValue();
					MContractCalender calender = MContractCalender.get(ctx, JP_ContractCalender_ID);
					MContractProcPeriod period = calender.getContractProcessPeriod(ctx,content.getJP_ContractProcDate_From(), null ,processPeriodOffset);
					if(period == null)
						return Msg.getMsg(ctx, "NotFound") +" : " +Msg.getElement(ctx, "JP_ProcPeriod_Start_Inv_ID");

					mTab.setValue ("JP_ProcPeriod_Start_Inv_ID", period.getJP_ContractProcPeriod_ID());
					mTab.setValue ("JP_ProcPeriod_Start_Inv_Date", period.getStartDate());
				}
			}
		}


		if( value != null && mTab.getValue("JP_DerivativeDocPolicy_Inv") != null
				&& (mTab.getValue("JP_DerivativeDocPolicy_Inv").equals("PE") ||  mTab.getValue("JP_DerivativeDocPolicy_Inv").equals("PB")) )
		{
			int JP_ContractContent_ID =  ((Integer)mTab.getValue("JP_ContractContent_ID")).intValue();
			Object obj_JP_ContractLineT_ID = mTab.getValue("JP_ContractLineT_ID");
			if(obj_JP_ContractLineT_ID  != null)
			{
				int JP_ContractLineT_ID = ((Integer)obj_JP_ContractLineT_ID).intValue();
				MContractContent content= MContractContent.get(ctx, JP_ContractContent_ID);
				MContractLineT lineTemplate = MContractLineT.get(ctx, JP_ContractLineT_ID);

				if(content.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateInvoice)
						|| content.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceiptInvoice))
				{
					int processPeriodOffset = lineTemplate.getJP_ProcPeriodOffs_End_Inv();
					if(processPeriodOffset > 0)
						processPeriodOffset++;
					else
						processPeriodOffset--;


					int JP_ContractCalender_ID = ((Integer)value).intValue();
					MContractCalender calender = MContractCalender.get(ctx, JP_ContractCalender_ID);
					MContractProcPeriod period = calender.getContractProcessPeriod(ctx,content.getJP_ContractProcDate_From(), null ,processPeriodOffset);
					if(period == null)
						return Msg.getMsg(ctx, "NotFound") +" : " +Msg.getElement(ctx, "JP_ProcPeriod_End_Inv_ID");

					mTab.setValue ("JP_ProcPeriod_End_Inv_ID", period.getJP_ContractProcPeriod_ID());
					mTab.setValue ("JP_ProcPeriod_End_Inv_Date", period.getEndDate());
				}
			}
		}

		return "";
	}



	/**
	 * Callout of JP_ContractLineT_ID Column
	 *
	 * @param ctx
	 * @param WindowNo
	 * @param mTab
	 * @param mField
	 * @param value
	 * @param oldValue
	 * @return
	 */
	private String calloutOfJP_ContractLineT_ID(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{
		if( value != null)
		{
			int JP_ContractContent_ID =  ((Integer)mTab.getValue("JP_ContractContent_ID")).intValue();
			MContractContent contractContent = MContractContent.get(Env.getCtx(), JP_ContractContent_ID);

			int JP_ContractLineT_ID =  Integer.parseInt(value.toString());
			MContractLineT lineTemplate= MContractLineT.get(ctx, JP_ContractLineT_ID);

			GridField[] fields = mTab.getFields();
			String columnName = null;
			int columnIndex = -1;
			Object objectValue = null;
			for(int i = 0 ; i < fields.length; i++)
			{
				columnName = fields[i].getColumnName();
				columnIndex = -1;
				objectValue = null;
				if(columnName.equals("JP_ContractLineT_ID")
						|| columnName.equals("JP_ContractContent_ID")
						|| columnName.equals("JP_ContractLine_UU")
						|| columnName.equals("AD_Client_ID")
						|| columnName.equals("AD_Org_ID")
						|| columnName.equals("AD_OrgTrx_ID")
						|| columnName.equals("IsActive")
						|| columnName.equals("Created")
						|| columnName.equals("CreatedBy")
						|| columnName.equals("Updated")
						|| columnName.equals("UpdatedBy")
					)
				{
					continue;
				}

				if(!fields[i].isAllowCopy())
					continue;

				columnIndex = lineTemplate.get_ColumnIndex(columnName);
				if(columnIndex > -1)
				{
					objectValue = lineTemplate.get_Value(columnIndex);
					if(objectValue != null)
					{
						mTab.setValue(columnName, objectValue);
					}

				}

			}//for


			if(mTab.getValue("JP_BaseDocLinePolicy") != null)
			{
				if(mTab.getValue("JP_BaseDocLinePolicy").toString().equals("LP"))
				{
					int processPeriodOffset = lineTemplate.getJP_ProcPeriodOffs_Lump();
					if(processPeriodOffset > 0)
						processPeriodOffset++;
					else
						processPeriodOffset--;


					MContractCalender calender = MContractCalender.get(Env.getCtx(), contractContent.getJP_ContractCalender_ID());
					MContractProcPeriod period = calender.getContractProcessPeriod(Env.getCtx(),contractContent.getJP_ContractProcDate_From(), null , processPeriodOffset);
					if(period != null)
						mTab.setValue("JP_ProcPeriod_Lump_ID",period.getJP_ContractProcPeriod_ID());
				}

				if(mTab.getValue("JP_BaseDocLinePolicy").toString().equals("PS") || mTab.getValue("JP_BaseDocLinePolicy").toString().equals("PB"))
				{
					int processPeriodOffset = lineTemplate.getJP_ProcPeriodOffs_Start();
					if(processPeriodOffset > 0)
						processPeriodOffset++;
					else
						processPeriodOffset--;


					MContractCalender calender = MContractCalender.get(Env.getCtx(), contractContent.getJP_ContractCalender_ID());
					MContractProcPeriod period = calender.getContractProcessPeriod(Env.getCtx(),contractContent.getJP_ContractProcDate_From(), null , processPeriodOffset);
					if(period != null)
						mTab.setValue("JP_ProcPeriod_Start_ID",period.getJP_ContractProcPeriod_ID());
				}

				if(mTab.getValue("JP_BaseDocLinePolicy").toString().equals("PE") || mTab.getValue("JP_BaseDocLinePolicy").toString().equals("PB"))
				{
					int processPeriodOffset = lineTemplate.getJP_ProcPeriodOffs_End();
					if(processPeriodOffset > 0)
						processPeriodOffset++;
					else
						processPeriodOffset--;


					MContractCalender calender = MContractCalender.get(Env.getCtx(), contractContent.getJP_ContractCalender_ID());
					MContractProcPeriod period = calender.getContractProcessPeriod(Env.getCtx(),contractContent.getJP_ContractProcDate_From(), null , processPeriodOffset);
					if(period != null)
						mTab.setValue("JP_ProcPeriod_End_ID",period.getJP_ContractProcPeriod_ID());
				}

			}//if(mTab.getValue("JP_BaseDocLinePolicy") != null)


			int JP_ContractCalRef_InOut_ID = lineTemplate.getJP_ContractCalRef_InOut_ID();
			if(JP_ContractCalRef_InOut_ID > 0 && !Util.isEmpty(contractContent.getJP_CreateDerivativeDocPolicy()) )
			{
				MContractCalenderRef  contractCalenderRef = MContractCalenderRef.get(Env.getCtx(), JP_ContractCalRef_InOut_ID);
				MContractCalenderList[] contractCalenderLists = contractCalenderRef.getContractCalenderList(Env.getCtx(), true, null);
				if(contractCalenderLists.length==1)
				{
					if(contractContent.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceipt)
							|| contractContent.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceiptInvoice))
					{
						mTab.setValue("JP_ContractCalender_InOut_ID",contractCalenderLists[0].getJP_ContractCalender_ID());

						if(mTab.getValue("JP_DerivativeDocPolicy_InOut").toString().equals("LP"))
						{

							int processPeriodOffset = lineTemplate.getJP_ProcPeriodOffs_Lump_InOut();
							if(processPeriodOffset > 0)
								processPeriodOffset++;
							else
								processPeriodOffset--;

							MContractCalender calender = MContractCalender.get(Env.getCtx(), Integer.parseInt(mTab.getValue("JP_ContractCalender_InOut_ID").toString()));
							MContractProcPeriod period = calender.getContractProcessPeriod(Env.getCtx(), contractContent.getJP_ContractProcDate_From() , null ,processPeriodOffset);
							if(period != null)
								mTab.setValue("JP_ProcPeriod_Lump_InOut_ID",period.getJP_ContractProcPeriod_ID());

						}

						if(mTab.getValue("JP_DerivativeDocPolicy_InOut").toString().equals("PS") || mTab.getValue("JP_DerivativeDocPolicy_InOut").toString().equals("PB"))
						{
							int processPeriodOffset = lineTemplate.getJP_ProcPeriodOffs_Start_InOut();
							if(processPeriodOffset > 0)
								processPeriodOffset++;
							else
								processPeriodOffset--;

							MContractCalender calender = MContractCalender.get(Env.getCtx(), Integer.parseInt(mTab.getValue("JP_ContractCalender_InOut_ID").toString()));
							MContractProcPeriod period = calender.getContractProcessPeriod(Env.getCtx(),contractContent.getJP_ContractProcDate_From() , null ,processPeriodOffset);
							if(period != null)
								mTab.setValue("JP_ProcPeriod_Start_InOut_ID",period.getJP_ContractProcPeriod_ID());
						}

						if(mTab.getValue("JP_DerivativeDocPolicy_InOut").toString().equals("PE") || mTab.getValue("JP_DerivativeDocPolicy_InOut").toString().equals("PB"))
						{
							int processPeriodOffset = lineTemplate.getJP_ProcPeriodOffs_End_InOut();
							if(processPeriodOffset > 0)
								processPeriodOffset++;
							else
								processPeriodOffset--;

							MContractCalender calender = MContractCalender.get(Env.getCtx(), Integer.parseInt(mTab.getValue("JP_ContractCalender_InOut_ID").toString()));
							MContractProcPeriod period = calender.getContractProcessPeriod(Env.getCtx(),contractContent.getJP_ContractProcDate_From() , null ,processPeriodOffset);
							if(period != null)
								mTab.setValue("JP_ProcPeriod_End_InOut_ID",period.getJP_ContractProcPeriod_ID());

						}

					}

				}//if(contractCalenderLists.length==1)

			}//if(JP_ContractCalRef_InOut_ID > 0)


			int JP_ContractCalRef_Inv_ID = lineTemplate.getJP_ContractCalRef_Inv_ID();
			if(JP_ContractCalRef_Inv_ID > 0 && !Util.isEmpty(contractContent.getJP_CreateDerivativeDocPolicy()))
			{
				MContractCalenderRef  contractCalenderRef = MContractCalenderRef.get(Env.getCtx(), JP_ContractCalRef_Inv_ID);
				MContractCalenderList[] contractCalenderLists = contractCalenderRef.getContractCalenderList(Env.getCtx(), true, null);
				if(contractCalenderLists.length==1)
				{
					if(contractContent.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateInvoice)
							|| contractContent.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceiptInvoice))
					{

						mTab.setValue("JP_ContractCalender_Inv_ID",contractCalenderLists[0].getJP_ContractCalender_ID());

						if(mTab.getValue("JP_DerivativeDocPolicy_Inv").toString().equals("LP"))
						{
							int processPeriodOffset = lineTemplate.getJP_ProcPeriodOffs_Lump_Inv();
							if(processPeriodOffset > 0)
								processPeriodOffset++;
							else
								processPeriodOffset--;

							MContractCalender calender = MContractCalender.get(Env.getCtx(), Integer.parseInt(mTab.getValue("JP_ContractCalender_Inv_ID").toString()));
							MContractProcPeriod period = calender.getContractProcessPeriod(Env.getCtx(), contractContent.getJP_ContractProcDate_From() , null ,processPeriodOffset);
							if(period != null)
								mTab.setValue("JP_ProcPeriod_Lump_Inv_ID",period.getJP_ContractProcPeriod_ID());

						}

						if(mTab.getValue("JP_DerivativeDocPolicy_Inv").toString().equals("PS") || mTab.getValue("JP_DerivativeDocPolicy_Inv").toString().equals("PB"))
						{
							int processPeriodOffset = lineTemplate.getJP_ProcPeriodOffs_Start_Inv();
							if(processPeriodOffset > 0)
								processPeriodOffset++;
							else
								processPeriodOffset--;

							MContractCalender calender = MContractCalender.get(Env.getCtx(), Integer.parseInt(mTab.getValue("JP_ContractCalender_Inv_ID").toString()));
							MContractProcPeriod period = calender.getContractProcessPeriod(Env.getCtx(),contractContent.getJP_ContractProcDate_From() , null ,processPeriodOffset);
							if(period != null)
								mTab.setValue("JP_ProcPeriod_Start_Inv_ID",period.getJP_ContractProcPeriod_ID());
						}

						if(mTab.getValue("JP_DerivativeDocPolicy_Inv").toString().equals("PE") || mTab.getValue("JP_DerivativeDocPolicy_Inv").toString().equals("PB"))
						{
							int processPeriodOffset = lineTemplate.getJP_ProcPeriodOffs_End_Inv();
							if(processPeriodOffset > 0)
								processPeriodOffset++;
							else
								processPeriodOffset--;

							MContractCalender calender = MContractCalender.get(Env.getCtx(), Integer.parseInt(mTab.getValue("JP_ContractCalender_Inv_ID").toString()));
							MContractProcPeriod period = calender.getContractProcessPeriod(Env.getCtx(),contractContent.getJP_ContractProcDate_From() , null ,processPeriodOffset);
							if(period != null)
								mTab.setValue("JP_ProcPeriod_End_Inv_ID",period.getJP_ContractProcPeriod_ID());

						}

					}

				}//if(contractCalenderLists.length==1)

			}//if(JP_ContractCalRef_Inv_ID > 0)

			int JP_ContractProcRef_InOut_ID = lineTemplate.getJP_ContractProcRef_InOut_ID();
			if(JP_ContractProcRef_InOut_ID > 0)
			{
				MContractProcessRef  contractProcessRef = MContractProcessRef.get(Env.getCtx(), JP_ContractProcRef_InOut_ID);
				MContractProcessList[] contractProcessLists = contractProcessRef.getContractProcessList(Env.getCtx(), true, null);
				if(contractProcessLists.length==1)
				{
					mTab.setValue("JP_ContractProcess_InOut_ID",contractProcessLists[0].getJP_ContractProcess_ID());
				}
			}

			int JP_ContractProcRef_Inv_ID = lineTemplate.getJP_ContractProcRef_Inv_ID();
			if(JP_ContractProcRef_Inv_ID > 0)
			{
				MContractProcessRef  contractProcessRef = MContractProcessRef.get(Env.getCtx(), JP_ContractProcRef_Inv_ID);
				MContractProcessList[] contractProcessLists = contractProcessRef.getContractProcessList(Env.getCtx(), true, null);
				if(contractProcessLists.length==1)
				{
					mTab.setValue("JP_ContractProcess_Inv_ID",contractProcessLists[0].getJP_ContractProcess_ID());
				}
			}

		}//if( value != null)

		return "";
	}

	/**
	 * Callout Of JP_ProcPeriod_Lump_ID
	 *
	 * @param ctx
	 * @param WindowNo
	 * @param mTab
	 * @param mField
	 * @param value
	 * @param oldValue
	 * @return
	 */
	private String calloutOfJP_ProcPeriod_Lump_ID(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{

		mTab.setValue("JP_ProcPeriod_Lump_Date", null);
		return "";

	}

	/**
	 * Callout Of JP_ProcPeriod_Lump_Date
	 *
	 * @param ctx
	 * @param WindowNo
	 * @param mTab
	 * @param mField
	 * @param value
	 * @param oldValue
	 * @return
	 */
	private String calloutOfJP_ProcPeriod_Lump_Date(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{
		Timestamp JP_ProcPeriod_Lump_Date = (Timestamp)value;
		if(JP_ProcPeriod_Lump_Date == null)
		{
			mTab.setValue("JP_ProcPeriod_Lump_ID", null);

		}else {

			if(mTab.getParentTab().getValue("JP_ContractCalender_ID") == null)
			{
				mTab.setValue("JP_ProcPeriod_Lump_Date", null);

				//Please enter {0} field first.
				Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractCalender_ID")};
				return Msg.getMsg(Env.getCtx(), "JP_EnterFirst", objs);
			}

			int JP_ContractCalender_ID = ((Integer)mTab.getParentTab().getValue("JP_ContractCalender_ID")).intValue();
			MContractCalender cc =MContractCalender.get(ctx, JP_ContractCalender_ID);
			MContractProcPeriod cpp = cc.getContractProcessPeriod(ctx, JP_ProcPeriod_Lump_Date);
			if(cpp != null)
			{
				mTab.setValue("JP_ProcPeriod_Lump_ID", cpp.getJP_ContractProcPeriod_ID());
			}
		}

		return "";
	}


	/**
	 * Call out of JP_ProcPeriod_Start_ID
	 *
	 * @param ctx
	 * @param WindowNo
	 * @param mTab
	 * @param mField
	 * @param value
	 * @param oldValue
	 * @return
	 */
	private String calloutOfJP_ProcPeriod_Start_ID(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{

		mTab.setValue("JP_ProcPeriod_Start_Date", null);
		return "";

	}

	/**
	 * Call out of JP_ProcPeriod_Start_Date
	 *
	 * @param ctx
	 * @param WindowNo
	 * @param mTab
	 * @param mField
	 * @param value
	 * @param oldValue
	 * @return
	 */
	private String calloutOfJP_ProcPeriod_Start_Date(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{
		Timestamp JP_ProcPeriod_Start_Date = (Timestamp)value;
		if(JP_ProcPeriod_Start_Date == null)
		{
			mTab.setValue("JP_ProcPeriod_Start_ID", null);

		}else {

			if(mTab.getParentTab().getValue("JP_ContractCalender_ID") == null)
			{
				mTab.setValue("JP_ProcPeriod_Start_Date", null);

				//Please enter {0} field first.
				Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractCalender_ID")};
				return Msg.getMsg(Env.getCtx(), "JP_EnterFirst", objs);
			}

			int JP_ContractCalender_ID = ((Integer)mTab.getParentTab().getValue("JP_ContractCalender_ID")).intValue();
			MContractCalender cc =MContractCalender.get(ctx, JP_ContractCalender_ID);
			MContractProcPeriod cpp = cc.getContractProcessPeriod(ctx, JP_ProcPeriod_Start_Date);
			if(cpp != null)
			{
				mTab.setValue("JP_ProcPeriod_Start_ID", cpp.getJP_ContractProcPeriod_ID());
			}
		}

		return "";
	}

	/**
	 * Call out of JP_ProcPeriod_End_ID
	 *
	 * @param ctx
	 * @param WindowNo
	 * @param mTab
	 * @param mField
	 * @param value
	 * @param oldValue
	 * @return
	 */
	private String calloutOfJP_ProcPeriod_End_ID(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{

		mTab.setValue("JP_ProcPeriod_End_Date", null);
		return "";

	}

	/**
	 * Call out of JP_ProcPeriod_End_Date
	 *
	 * @param ctx
	 * @param WindowNo
	 * @param mTab
	 * @param mField
	 * @param value
	 * @param oldValue
	 * @return
	 */
	private String calloutOfJP_ProcPeriod_End_Date(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{
		Timestamp JP_ProcPeriod_End_Date = (Timestamp)value;
		if(JP_ProcPeriod_End_Date == null)
		{
			mTab.setValue("JP_ProcPeriod_Lump_ID", null);

		}else {

			if(mTab.getParentTab().getValue("JP_ContractCalender_ID") == null)
			{
				mTab.setValue("JP_ProcPeriod_End_Date", null);

				//Please enter {0} field first.
				Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractCalender_ID")};
				return Msg.getMsg(Env.getCtx(), "JP_EnterFirst", objs);
			}

			int JP_ContractCalender_ID = ((Integer)mTab.getParentTab().getValue("JP_ContractCalender_ID")).intValue();
			MContractCalender cc =MContractCalender.get(ctx, JP_ContractCalender_ID);
			MContractProcPeriod cpp = cc.getContractProcessPeriod(ctx, JP_ProcPeriod_End_Date);
			if(cpp != null)
			{
				mTab.setValue("JP_ProcPeriod_End_ID", cpp.getJP_ContractProcPeriod_ID());
			}
		}

		return "";
	}

	/**
	 * Callout Of JP_ProcPeriod_Lump_InOut_ID
	 *
	 * @param ctx
	 * @param WindowNo
	 * @param mTab
	 * @param mField
	 * @param value
	 * @param oldValue
	 * @return
	 */
	private String calloutOfJP_ProcPeriod_Lump_InOut_ID(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{

		mTab.setValue("JP_ProcPeriod_Lump_InOut_Date", null);
		return "";

	}

	/**
	 * Callout Of JP_ProcPeriod_Lump_InOut_Date
	 *
	 * @param ctx
	 * @param WindowNo
	 * @param mTab
	 * @param mField
	 * @param value
	 * @param oldValue
	 * @return
	 */
	private String calloutOfJP_ProcPeriod_Lump_InOut_Date(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{
		Timestamp JP_ProcPeriod_Lump_InOut_Date = (Timestamp)value;
		if(JP_ProcPeriod_Lump_InOut_Date == null)
		{
			mTab.setValue("JP_ProcPeriod_Lump_InOut_ID", null);

		}else if(mTab.getValue("JP_ContractCalender_InOut_ID") != null){

			int JP_ContractCalender_ID = ((Integer)mTab.getValue("JP_ContractCalender_InOut_ID")).intValue();
			MContractCalender cc =MContractCalender.get(ctx, JP_ContractCalender_ID);
			MContractProcPeriod cpp = cc.getContractProcessPeriod(ctx, JP_ProcPeriod_Lump_InOut_Date);
			if(cpp != null)
			{
				mTab.setValue("JP_ProcPeriod_Lump_InOut_ID", cpp.getJP_ContractProcPeriod_ID());
			}

		}else if(mTab.getValue("JP_ContractCalender_InOut_ID") == null){

				mTab.setValue("JP_ProcPeriod_Lump_InOut_Date", null);

				//Please enter {0} field first.
				Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractCalender_InOut_ID")};
				return Msg.getMsg(Env.getCtx(), "JP_EnterFirst", objs);
		}

		return "";
	}

	/**
	 * Callout Of JP_ProcPeriod_Start_InOut_ID
	 *
	 * @param ctx
	 * @param WindowNo
	 * @param mTab
	 * @param mField
	 * @param value
	 * @param oldValue
	 * @return
	 */
	private String calloutOfJP_ProcPeriod_Start_InOut_ID(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{

		mTab.setValue("JP_ProcPeriod_Start_InOut_Date", null);
		return "";

	}

	/**
	 * Callout Of JP_ProcPeriod_Start_InOut_Date
	 *
	 * @param ctx
	 * @param WindowNo
	 * @param mTab
	 * @param mField
	 * @param value
	 * @param oldValue
	 * @return
	 */
	private String calloutOfJP_ProcPeriod_Start_InOut_Date(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{
		Timestamp JP_ProcPeriod_Start_InOut_Date = (Timestamp)value;
		if(JP_ProcPeriod_Start_InOut_Date == null)
		{
			mTab.setValue("JP_ProcPeriod_Start_InOut_ID", null);

		}else if(mTab.getValue("JP_ContractCalender_InOut_ID") != null){

			int JP_ContractCalender_ID = ((Integer)mTab.getValue("JP_ContractCalender_InOut_ID")).intValue();
			MContractCalender cc =MContractCalender.get(ctx, JP_ContractCalender_ID);
			MContractProcPeriod cpp = cc.getContractProcessPeriod(ctx, JP_ProcPeriod_Start_InOut_Date);
			if(cpp != null)
			{
				mTab.setValue("JP_ProcPeriod_Start_InOut_ID", cpp.getJP_ContractProcPeriod_ID());
			}

		}else if(mTab.getValue("JP_ContractCalender_InOut_ID") == null){

			mTab.setValue("JP_ProcPeriod_Start_InOut_Date", null);

			//Please enter {0} field first.
			Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractCalender_InOut_ID")};
			return Msg.getMsg(Env.getCtx(), "JP_EnterFirst", objs);
		}

		return "";
	}

	/**
	 * Callout Of JP_ProcPeriod_End_InOut_ID
	 *
	 * @param ctx
	 * @param WindowNo
	 * @param mTab
	 * @param mField
	 * @param value
	 * @param oldValue
	 * @return
	 */
	private String calloutOfJP_ProcPeriod_End_InOut_ID(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{

		mTab.setValue("JP_ProcPeriod_End_InOut_Date", null);
		return "";

	}

	/**
	 * Callout Of JP_ProcPeriod_End_InOut_Date
	 *
	 * @param ctx
	 * @param WindowNo
	 * @param mTab
	 * @param mField
	 * @param value
	 * @param oldValue
	 * @return
	 */
	private String calloutOfJP_ProcPeriod_End_InOut_Date(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{
		Timestamp JP_ProcPeriod_End_InOut_Date = (Timestamp)value;
		if(JP_ProcPeriod_End_InOut_Date == null)
		{
			mTab.setValue("JP_ProcPeriod_End_InOut_ID", null);

		}else if(mTab.getValue("JP_ContractCalender_InOut_ID") != null){

			int JP_ContractCalender_ID = ((Integer)mTab.getValue("JP_ContractCalender_InOut_ID")).intValue();
			MContractCalender cc =MContractCalender.get(ctx, JP_ContractCalender_ID);
			MContractProcPeriod cpp = cc.getContractProcessPeriod(ctx, JP_ProcPeriod_End_InOut_Date);
			if(cpp != null)
			{
				mTab.setValue("JP_ProcPeriod_End_InOut_ID", cpp.getJP_ContractProcPeriod_ID());
			}
		}else if(mTab.getValue("JP_ContractCalender_InOut_ID") == null){

			mTab.setValue("JP_ProcPeriod_End_InOut_Date", null);

			//Please enter {0} field first.
			Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractCalender_InOut_ID")};
			return Msg.getMsg(Env.getCtx(), "JP_EnterFirst", objs);
		}

		return "";
	}

	/**
	 * Callout Of JP_ProcPeriod_Lump_Inv_ID
	 *
	 * @param ctx
	 * @param WindowNo
	 * @param mTab
	 * @param mField
	 * @param value
	 * @param oldValue
	 * @return
	 */
	private String calloutOfJP_ProcPeriod_Lump_Inv_ID(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{

		mTab.setValue("JP_ProcPeriod_Lump_Inv_Date", null);
		return "";

	}

	/**
	 * Callout Of JP_ProcPeriod_Lump_Inv_Date
	 *
	 * @param ctx
	 * @param WindowNo
	 * @param mTab
	 * @param mField
	 * @param value
	 * @param oldValue
	 * @return
	 */
	private String calloutOfJP_ProcPeriod_Lump_Inv_Date(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{
		Timestamp JP_ProcPeriod_Lump_Inv_Date = (Timestamp)value;
		if(JP_ProcPeriod_Lump_Inv_Date == null)
		{
			mTab.setValue("JP_ProcPeriod_Lump_Inv_ID", null);

		}else if(mTab.getValue("JP_ContractCalender_Inv_ID") != null){

			int JP_ContractCalender_ID = ((Integer)mTab.getValue("JP_ContractCalender_Inv_ID")).intValue();
			MContractCalender cc =MContractCalender.get(ctx, JP_ContractCalender_ID);
			MContractProcPeriod cpp = cc.getContractProcessPeriod(ctx, JP_ProcPeriod_Lump_Inv_Date);
			if(cpp != null)
			{
				mTab.setValue("JP_ProcPeriod_Lump_Inv_ID", cpp.getJP_ContractProcPeriod_ID());
			}

		}else if(mTab.getValue("JP_ContractCalender_Inv_ID") == null){

			mTab.setValue("JP_ProcPeriod_Lump_Inv_Date", null);

			//Please enter {0} field first.
			Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractCalender_Inv_ID")};
			return Msg.getMsg(Env.getCtx(), "JP_EnterFirst", objs);
		}

		return "";
	}

	/**
	 * Callout Of JP_ProcPeriod_Start_Inv_ID
	 *
	 * @param ctx
	 * @param WindowNo
	 * @param mTab
	 * @param mField
	 * @param value
	 * @param oldValue
	 * @return
	 */
	private String calloutOfJP_ProcPeriod_Start_Inv_ID(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{

		mTab.setValue("JP_ProcPeriod_Start_Inv_Date", null);
		return "";

	}

	/**
	 * Callout Of JP_ProcPeriod_Start_Inv_Date
	 *
	 * @param ctx
	 * @param WindowNo
	 * @param mTab
	 * @param mField
	 * @param value
	 * @param oldValue
	 * @return
	 */
	private String calloutOfJP_ProcPeriod_Start_Inv_Date(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{
		Timestamp JP_ProcPeriod_Start_Inv_Date = (Timestamp)value;
		if(JP_ProcPeriod_Start_Inv_Date == null)
		{
			mTab.setValue("JP_ProcPeriod_Start_Inv_ID", null);

		}else if(mTab.getValue("JP_ContractCalender_Inv_ID") != null){

			int JP_ContractCalender_ID = ((Integer)mTab.getValue("JP_ContractCalender_Inv_ID")).intValue();
			MContractCalender cc =MContractCalender.get(ctx, JP_ContractCalender_ID);
			MContractProcPeriod cpp = cc.getContractProcessPeriod(ctx, JP_ProcPeriod_Start_Inv_Date);
			if(cpp != null)
			{
				mTab.setValue("JP_ProcPeriod_Start_Inv_ID", cpp.getJP_ContractProcPeriod_ID());
			}

		}else if(mTab.getValue("JP_ContractCalender_Inv_ID") == null){

			mTab.setValue("JP_ProcPeriod_Start_Inv_Date", null);

			//Please enter {0} field first.
			Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractCalender_Inv_ID")};
			return Msg.getMsg(Env.getCtx(), "JP_EnterFirst", objs);
		}

		return "";
	}

	/**
	 * Callout Of JP_ProcPeriod_End_Inv_ID
	 *
	 * @param ctx
	 * @param WindowNo
	 * @param mTab
	 * @param mField
	 * @param value
	 * @param oldValue
	 * @return
	 */
	private String calloutOfJP_ProcPeriod_End_Inv_ID(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{

		mTab.setValue("JP_ProcPeriod_End_Inv_Date", null);
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
	private String calloutOfJP_ProcPeriod_End_Inv_Date(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{
		Timestamp JP_ProcPeriod_End_Inv_Date = (Timestamp)value;
		if(JP_ProcPeriod_End_Inv_Date == null)
		{
			mTab.setValue("JP_ProcPeriod_End_Invt_ID", null);

		}else if(mTab.getValue("JP_ContractCalender_Inv_ID") != null){

			int JP_ContractCalender_ID = ((Integer)mTab.getValue("JP_ContractCalender_Inv_ID")).intValue();
			MContractCalender cc =MContractCalender.get(ctx, JP_ContractCalender_ID);
			MContractProcPeriod cpp = cc.getContractProcessPeriod(ctx, JP_ProcPeriod_End_Inv_Date);
			if(cpp != null)
			{
				mTab.setValue("JP_ProcPeriod_End_Inv_ID", cpp.getJP_ContractProcPeriod_ID());
			}

		}else if(mTab.getValue("JP_ContractCalender_Inv_ID") == null){

			mTab.setValue("JP_ProcPeriod_Start_End_Date", null);

			//Please enter {0} field first.
			Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractCalender_Inv_ID")};
			return Msg.getMsg(Env.getCtx(), "JP_EnterFirst", objs);
		}

		return "";
	}

}
