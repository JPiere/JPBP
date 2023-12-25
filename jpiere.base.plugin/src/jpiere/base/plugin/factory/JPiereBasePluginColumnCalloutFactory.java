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
package jpiere.base.plugin.factory;

import java.util.ArrayList;
import java.util.List;

import org.adempiere.base.IColumnCallout;
import org.adempiere.base.IColumnCalloutFactory;
import org.compiere.model.MInOut;
import org.compiere.model.MInOutLineMA;
import org.compiere.model.MInventory;
import org.compiere.model.MInventoryLineMA;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MLocation;
import org.compiere.model.MMovement;
import org.compiere.model.MMovementLineMA;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MPayment;
import org.compiere.model.MProductionLineMA;
import org.compiere.model.MRoleOrgAccess;
import org.compiere.model.MUserOrgAccess;
import org.compiere.model.MUserRoles;

import jpiere.base.plugin.org.adempiere.callout.JPiereAccessControlOrgCallout;
import jpiere.base.plugin.org.adempiere.callout.JPiereBankAcountCallout;
import jpiere.base.plugin.org.adempiere.callout.JPiereBankDataCallout;
import jpiere.base.plugin.org.adempiere.callout.JPiereBillAmountCallout;
import jpiere.base.plugin.org.adempiere.callout.JPiereBillBPartnerCallout;
import jpiere.base.plugin.org.adempiere.callout.JPiereCityCallout;
import jpiere.base.plugin.org.adempiere.callout.JPiereContractCallout;
import jpiere.base.plugin.org.adempiere.callout.JPiereContractContentCallout;
import jpiere.base.plugin.org.adempiere.callout.JPiereContractLineCallout;
import jpiere.base.plugin.org.adempiere.callout.JPiereContractLineTCallout;
import jpiere.base.plugin.org.adempiere.callout.JPiereContractOrderCallout;
import jpiere.base.plugin.org.adempiere.callout.JPiereContractPSInOutLineCallout;
import jpiere.base.plugin.org.adempiere.callout.JPiereContractPSInvoiceLineCallout;
import jpiere.base.plugin.org.adempiere.callout.JPiereContractPSLineCallout;
import jpiere.base.plugin.org.adempiere.callout.JPiereContractProcScheduleCallout;
import jpiere.base.plugin.org.adempiere.callout.JPiereDropShipBPartnerCallout;
import jpiere.base.plugin.org.adempiere.callout.JPiereEstimationCallout;
import jpiere.base.plugin.org.adempiere.callout.JPiereInOutCallout;
import jpiere.base.plugin.org.adempiere.callout.JPiereInvValAdjustCallout;
import jpiere.base.plugin.org.adempiere.callout.JPiereInvValCalCallout;
import jpiere.base.plugin.org.adempiere.callout.JPiereInvValProfileCallout;
import jpiere.base.plugin.org.adempiere.callout.JPiereInvoiceLineCallout;
import jpiere.base.plugin.org.adempiere.callout.JPiereOrderCallout;
import jpiere.base.plugin.org.adempiere.callout.JPiereRecognitionCallout;
import jpiere.base.plugin.org.adempiere.callout.JPiereReferenceTestCallout;
import jpiere.base.plugin.org.adempiere.callout.JPiereRegionCallout;
import jpiere.base.plugin.org.adempiere.callout.SupportToEnterAttributesTabColumnCallout;
import jpiere.base.plugin.org.adempiere.callout.SupportToEnterPhysicalWarehouseCallout;
import jpiere.base.plugin.org.adempiere.model.MBankDataLine;
import jpiere.base.plugin.org.adempiere.model.MBill;
import jpiere.base.plugin.org.adempiere.model.MContract;
import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractContentT;
import jpiere.base.plugin.org.adempiere.model.MContractLine;
import jpiere.base.plugin.org.adempiere.model.MContractLineT;
import jpiere.base.plugin.org.adempiere.model.MContractPSInOutLine;
import jpiere.base.plugin.org.adempiere.model.MContractPSInvoiceLine;
import jpiere.base.plugin.org.adempiere.model.MContractPSLine;
import jpiere.base.plugin.org.adempiere.model.MContractProcSchedule;
import jpiere.base.plugin.org.adempiere.model.MEstimation;
import jpiere.base.plugin.org.adempiere.model.MEstimationLine;
import jpiere.base.plugin.org.adempiere.model.MInvValAdjust;
import jpiere.base.plugin.org.adempiere.model.MInvValCal;
import jpiere.base.plugin.org.adempiere.model.MInvValProfile;
import jpiere.base.plugin.org.adempiere.model.MPPFactLineMA;
import jpiere.base.plugin.org.adempiere.model.MPPMMFactLineMA;
import jpiere.base.plugin.org.adempiere.model.MPPPlan;
import jpiere.base.plugin.org.adempiere.model.MRecognition;
import jpiere.base.plugin.org.adempiere.model.MReferenceTest;

/**
 *  JPiere Base Plugin Callout Factory
 *
 *
 *  @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 *
 */
public class JPiereBasePluginColumnCalloutFactory implements IColumnCalloutFactory {

	@Override
	public IColumnCallout[] getColumnCallouts(String tableName, String columnName) {

		List<IColumnCallout> list = new ArrayList<IColumnCallout>();

		if(tableName.startsWith("JP"))
		{
			if(tableName.equals(MBill.Table_Name))
			{

				if(columnName.equals(MBill.COLUMNNAME_C_BPartner_ID))
				{
					list.add(new JPiereBillBPartnerCallout());

				}else if(columnName.equals(MBill.COLUMNNAME_JP_LastBill_ID)
							|| columnName.equals(MBill.COLUMNNAME_JPLastBillAmt)
							|| columnName.equals(MBill.COLUMNNAME_C_Payment_ID)
							|| columnName.equals(MBill.COLUMNNAME_JPLastPayAmt))
				{
					list.add(new JPiereBillAmountCallout());
				}
			}else if(tableName.equals(MInvValProfile.Table_Name)){
				if(columnName.equals(MInvValProfile.COLUMNNAME_C_AcctSchema_ID))
				{
					list.add(new JPiereInvValProfileCallout());
				}
			}else if(tableName.equals(MInvValCal.Table_Name)){	//JPIERE-0161
				if(columnName.equals(MInvValCal.COLUMNNAME_JP_InvValProfile_ID)
						|| columnName.equals(MInvValCal.COLUMNNAME_DateValue))
				{
					list.add(new JPiereInvValCalCallout());
				}
			}else if(tableName.equals(MInvValAdjust.Table_Name)){	//JPIERE-0163
				if(columnName.equals(MInvValAdjust.COLUMNNAME_JP_InvValProfile_ID)
						|| columnName.equals(MInvValCal.COLUMNNAME_DateValue))
				{
					list.add(new JPiereInvValAdjustCallout());
				}
			}else if(tableName.equals(MEstimation.Table_Name)){	//JPIERE-0183
				if(columnName.equals(MEstimation.COLUMNNAME_JP_DocTypeSO_ID)
						|| columnName.equals(MEstimation.COLUMNNAME_C_DocTypeTarget_ID)
						|| columnName.equals(MEstimation.COLUMNNAME_C_Opportunity_ID)
						|| columnName.equals(MEstimation.COLUMNNAME_M_PriceList_ID) ||  columnName.equals(MEstimation.COLUMNNAME_DateOrdered)  ||  columnName.equals(MEstimation.COLUMNNAME_JP_Estimation_ID))
				{
					list.add(new JPiereEstimationCallout());
				}

			}else if(tableName.equals(MEstimationLine.Table_Name)){	//JPIERE-0227

				if(columnName.equals("JP_LocatorFrom_ID") || columnName.equals("JP_LocatorTo_ID"))
				{
					list.add(new JPiereEstimationCallout());
				}

			}else if(tableName.equals(MBankDataLine.Table_Name)){	//JPIERE-0302
				if(columnName.equals(MBankDataLine.COLUMNNAME_TrxAmt)
						|| columnName.equals(MEstimation.COLUMNNAME_C_Invoice_ID)
						|| columnName.equals(MEstimation.COLUMNNAME_JP_Bill_ID)
						|| columnName.equals(MEstimation.COLUMNNAME_C_Payment_ID) )
				{
					list.add(new JPiereBankDataCallout());
				}
			}else if(tableName.equals(MContract.Table_Name)){	//JPIERE-0363

				if(columnName.equals("JP_ContractT_ID") || columnName.equals("JP_ContractPeriodDate_To")
						|| columnName.equals("JP_ContractCancelTerm_ID") || columnName.equals("JP_ContractCancelDate") || columnName.equals("JP_ContractCancelOfferDate") )
				{
					list.add(new JPiereContractCallout());
				}
			}else if(tableName.equals(MContractContent.Table_Name)
					||tableName.equals(MContractContentT.Table_Name) ){	//JPIERE-0363

				if(columnName.equals("JP_BaseDocDocType_ID")
						|| columnName.equals("JP_ContractCalender_ID")
						|| columnName.equals("JP_ContractContentT_ID")
						|| columnName.equals("M_PriceList_ID")
						|| columnName.equals("DateDoc")
						|| columnName.equals("DateInvoiced")
						|| columnName.equals("JP_ContractContent_ID")
						|| columnName.equals("C_BPartner_ID")
						|| columnName.equals("DocBaseType")
						)
				{
					list.add(new JPiereContractContentCallout());
				}

				if(tableName.equals(MContractContent.Table_Name))
				{
					if(columnName.equals("IsAutomaticUpdateJP"))
					{
						list.add(new JPiereContractContentCallout());
					}
				}

			}else if(tableName.equals(MContractLine.Table_Name)){//JPIERE-0363

				if(columnName.equals("JP_ContractCalender_InOut_ID")
						|| columnName.equals("JP_ContractCalender_Inv_ID")
						|| columnName.equals("JP_ContractLineT_ID")
						|| columnName.equals("JP_ProcPeriod_Lump_ID")	//JPIERE-0428
						|| columnName.equals("JP_ProcPeriod_Lump_Date")	//JPIERE-0428
						|| columnName.equals("JP_ProcPeriod_Start_ID")//JPIERE-0428
						|| columnName.equals("JP_ProcPeriod_Start_Date")//JPIERE-0428
						|| columnName.equals("JP_ProcPeriod_End_ID")//JPIERE-0428
						|| columnName.equals("JP_ProcPeriod_End_Date")//JPIERE-0428
						|| columnName.equals("JP_ProcPeriod_Lump_InOut_ID")//JPIERE-0428
						|| columnName.equals("JP_ProcPeriod_Lump_InOut_Date")//JPIERE-0428
						|| columnName.equals("JP_ProcPeriod_Start_InOut_ID")//JPIERE-0428
						|| columnName.equals("JP_ProcPeriod_Start_InOut_Date")//JPIERE-0428
						|| columnName.equals("JP_ProcPeriod_End_InOut_ID")//JPIERE-0428
						|| columnName.equals("JP_ProcPeriod_End_InOut_Date")//JPIERE-0428
						|| columnName.equals("JP_ProcPeriod_Lump_Inv_ID")//JPIERE-0428
						|| columnName.equals("JP_ProcPeriod_Lump_Inv_Date")//JPIERE-0428
						|| columnName.equals("JP_ProcPeriod_Start_Inv_ID")//JPIERE-0428
						|| columnName.equals("JP_ProcPeriod_Start_Inv_Date")//JPIERE-0428
						|| columnName.equals("JP_ProcPeriod_End_Inv_ID")//JPIERE-0428
						|| columnName.equals("JP_ProcPeriod_End_Inv_Date")//JPIERE-0428
						)
				{
					list.add(new JPiereContractLineCallout());
				}

			}else if(tableName.equals(MContractLineT.Table_Name)){//JPIERE-0427

				if(columnName.equals("JP_ContractLineT_ID") || columnName.equals("M_PriceList_ID") || columnName.equals("DocBaseType"))
				{
					list.add(new JPiereContractLineTCallout());
				}

			}else if(tableName.equals(MRecognition.Table_Name)){//JPIERE-0364

				if(columnName.equals("M_InOut_ID") || columnName.equals("M_PriceList_ID") ||  columnName.equals("DateInvoiced") || columnName.equals("JP_Recognition_ID") )
				{
					list.add(new JPiereRecognitionCallout());
				}

			}else if(tableName.equals(MContractProcSchedule.Table_Name)){//JPIERE-0431

				if(columnName.equals("JP_ContractContent_ID") || columnName.equals("DateAcct") || columnName.equals("JP_BaseDocDocType_ID"))
				{
					list.add(new JPiereContractProcScheduleCallout());
				}

			}else if(tableName.equals(MContractPSLine.Table_Name)){//JPIERE-0431

				if(columnName.equals("JP_ContractLine_ID") )
				{
					list.add(new JPiereContractPSLineCallout());
				}

			}else if(tableName.equals(MContractPSInOutLine.Table_Name)){//JPIERE-0431

				if(columnName.equals("JP_ContractPSLine_ID")
						|| columnName.equals("QtyEntered")
						|| columnName.equals("PriceEntered"))
				{
					list.add(new JPiereContractPSInOutLineCallout());
				}

			}else if(tableName.equals(MContractPSInvoiceLine.Table_Name)){//JPIERE-0431

				if(columnName.equals("JP_ContractPSLine_ID")
						|| columnName.equals("QtyEntered")
						|| columnName.equals("PriceEntered") )
				{
					list.add(new JPiereContractPSInvoiceLineCallout());
				}

			}else if(tableName.equals(MReferenceTest.Table_Name)) {//JPIERE-0084

				if(columnName.equals(MReferenceTest.COLUMNNAME_JP_ReferenceTest_ID)
						|| columnName.equals(MReferenceTest.COLUMNNAME_DocStatus)
						|| columnName.equals(MReferenceTest.COLUMNNAME_M_Product_ID) )
				{
					list.add(new JPiereReferenceTestCallout());
				}

			}

		}else{

			if(tableName.equals(MPayment.Table_Name))
			{
				if(columnName.equals(MPayment.COLUMNNAME_C_BankAccount_ID))
					list.add(new JPiereBankAcountCallout());

			}else if(tableName.equals(MLocation.Table_Name)){

				if(columnName.equals(MLocation.COLUMNNAME_C_Region_ID))
					list.add(new JPiereRegionCallout());
				else if(columnName.equals(MLocation.COLUMNNAME_C_City_ID))
					list.add(new JPiereCityCallout());

			}else if(tableName.equals(MOrder.Table_Name)){

				if(columnName.equals(MOrder.COLUMNNAME_DropShip_BPartner_ID))
				{
					list.add(new JPiereDropShipBPartnerCallout());
				}else if(columnName.equals("JP_Contract_ID") || columnName.equals("JP_ContractContent_ID")) { //JPIERE-0363

					list.add(new JPiereContractOrderCallout());
				}

			}else if(tableName.equals(MOrderLine.Table_Name)){	//JPIERE-0227

				if(columnName.equals("JP_LocatorFrom_ID") || columnName.equals("JP_LocatorTo_ID"))
				{
					list.add(new JPiereOrderCallout());

				}else if(columnName.equals("JP_ContractLine_ID")) { //JPIERE-0363

					list.add(new JPiereContractOrderCallout());
				}

			}else if(tableName.equals(MInOut.Table_Name)){

				if(columnName.equals(MInOut.COLUMNNAME_C_Order_ID))
					list.add(new JPiereInOutCallout());
				else if(columnName.equals(MOrder.COLUMNNAME_DropShip_BPartner_ID))
					list.add(new JPiereDropShipBPartnerCallout());

			}else if(tableName.equals(MInvoice.Table_Name)){

				if(columnName.equals("JP_Contract_ID") || columnName.equals("JP_ContractContent_ID")) { //JPIERE-0363

					list.add(new JPiereContractOrderCallout());
				}

			}else if(tableName.equals(MInvoiceLine.Table_Name)){

				if(columnName.equals("JP_ContractLine_ID")) { //JPIERE-0363

					list.add(new JPiereContractOrderCallout());

				}else if (columnName.equals("C_OrderLine_ID")) { //JPIERE-0381

					list.add(new JPiereInvoiceLineCallout());
				}
				
			}else if(tableName.equals(MUserOrgAccess.Table_Name) 	//JPIERE-0546
					|| tableName.equals(MRoleOrgAccess.Table_Name)  //JPIERE-0547
					|| tableName.equals(MUserRoles.Table_Name)){	//JPIERE-0548
				
				if(columnName.equals("AD_Org_ID")) { 

					list.add(new JPiereAccessControlOrgCallout());	//JPIERE-0549
				}
			}
		}

		//JPIERE-0503 : Support to enter DateMaterialPolicy
		//JPIERE-0609 : Workprocess & Create Material Movement From PP Fact Doc.
		if((tableName.equals(MInOutLineMA.Table_Name)
				|| tableName.equals(MProductionLineMA.Table_Name)
				|| tableName.equals(MMovementLineMA.Table_Name)
				|| tableName.equals(MInventoryLineMA.Table_Name)
				|| tableName.equals(MPPFactLineMA.Table_Name)
				|| tableName.equals(MPPMMFactLineMA.Table_Name))
				&& (columnName.equals("M_AttributeSetInstance_ID")
						|| columnName.equals("DateMaterialPolicy"))
				)
		{
			list.add(new SupportToEnterAttributesTabColumnCallout());
		}
		
		//JPIERE-0588 : Support to enter Physical Warehouse from Org Warehouse
		//JPIERE-0609 : Workprocess & Create Material Movement From PP Fact Doc.
		if((tableName.equals(MInOut.Table_Name)
				|| tableName.equals(MInventory.Table_Name)
				|| tableName.equals(MMovement.Table_Name)
				|| tableName.equals(MPPPlan.Table_Name) )
				&& (columnName.equals("M_Warehouse_ID")
						|| columnName.equals("JP_Warehouse_ID")
						|| columnName.equals("JP_WarehouseFrom_ID")
						|| columnName.equals("JP_WarehouseTo_ID"))
				)
		{
			list.add(new SupportToEnterPhysicalWarehouseCallout());
		}

		return list != null ? list.toArray(new IColumnCallout[0]) : new IColumnCallout[0];
	}

}
