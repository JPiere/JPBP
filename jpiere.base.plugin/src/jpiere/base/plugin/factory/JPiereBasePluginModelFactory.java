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

import java.sql.ResultSet;

import jpiere.base.plugin.org.adempiere.model.MBill;
import jpiere.base.plugin.org.adempiere.model.MBillLine;
import jpiere.base.plugin.org.adempiere.model.MBillSchema;
import jpiere.base.plugin.org.adempiere.model.MCorporation;
import jpiere.base.plugin.org.adempiere.model.MCorporationGroup;
import jpiere.base.plugin.org.adempiere.model.MDeliveryDays;
import jpiere.base.plugin.org.adempiere.model.MGroupCorporations;
import jpiere.base.plugin.org.adempiere.model.MInvValAdjust;
import jpiere.base.plugin.org.adempiere.model.MInvValAdjustLine;
import jpiere.base.plugin.org.adempiere.model.MInvValCal;
import jpiere.base.plugin.org.adempiere.model.MInvValCalLine;
import jpiere.base.plugin.org.adempiere.model.MInvValCalLog;
import jpiere.base.plugin.org.adempiere.model.MInvValProfile;
import jpiere.base.plugin.org.adempiere.model.MInvValProfileOrg;
import jpiere.base.plugin.org.adempiere.model.MInventoryDiffQtyLog;
import jpiere.base.plugin.org.adempiere.model.MOrderJP;
import jpiere.base.plugin.org.adempiere.model.MProductCategoryG;
import jpiere.base.plugin.org.adempiere.model.MProductCategoryGLine;
import jpiere.base.plugin.org.adempiere.model.MProductCategoryL1;
import jpiere.base.plugin.org.adempiere.model.MProductCategoryL2;
import jpiere.base.plugin.org.adempiere.model.MProductGroup;
import jpiere.base.plugin.org.adempiere.model.MProductGroupLine;
import jpiere.base.plugin.org.adempiere.model.MReferenceTest;
import jpiere.base.plugin.org.adempiere.model.MSalesRegionG;
import jpiere.base.plugin.org.adempiere.model.MSalesRegionGLine;
import jpiere.base.plugin.org.adempiere.model.MSalesRegionL1;
import jpiere.base.plugin.org.adempiere.model.MSalesRegionL2;

import org.adempiere.base.IModelFactory;
import org.compiere.model.MOrder;
import org.compiere.model.PO;
import org.compiere.util.Env;

/**
 *  JPiere Base Plugin Model Factory
 *
 *  JPIERE-0024:JPBP:Corporation Master & Corporation Group Master
 *  JPIERE-0106:JPBP:Bill
 *  JPIERE-0148,0149,0150	Product category Group
 *  JPIERE-0151,0152,0153   Sales Region Group & Delivery Days from Warehouse
 *  JPIERE-0160,0161,0163	Inventory Valuation Calculate
 *
 *  @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 *
 */
public class JPiereBasePluginModelFactory implements IModelFactory {

	@Override
	public Class<?> getClass(String tableName) {

		if(tableName.startsWith("JP"))
		{
			if(tableName.equals(MCorporation.Table_Name)){
				return MCorporation.class;
			}else if(tableName.equals(MCorporationGroup.Table_Name)){
				return MCorporationGroup.class;
			}else if(tableName.equals(MGroupCorporations.Table_Name)){
				return MGroupCorporations.class;
			}else if(tableName.equals(MBill.Table_Name)){
				return MBill.class;
			}else if(tableName.equals(MBillLine.Table_Name)){
				return MBillLine.class;
			}else if(tableName.equals(MReferenceTest.Table_Name)){
				return MReferenceTest.class;
			}else if(tableName.equals(MBillSchema.Table_Name)){
				return MBillSchema.class;
			}else if(tableName.equals(MProductCategoryL1.Table_Name)){
				return MProductCategoryL1.class;
			}else if(tableName.equals(MProductCategoryL2.Table_Name)){
				return MProductCategoryL2.class;
			}else if(tableName.equals(MProductCategoryG.Table_Name)){
				return MProductCategoryG.class;
			}else if(tableName.equals(MProductCategoryGLine.Table_Name)){
				return MProductCategoryGLine.class;
			}else if(tableName.equals(MProductGroup.Table_Name)){
				return MProductGroup.class;
			}else if(tableName.equals(MProductGroupLine.Table_Name)){
				return MProductGroupLine.class;
			}else if(tableName.equals(MSalesRegionL2.Table_Name)){	//JPIERE-0151
				return MSalesRegionL2.class;
			}else if(tableName.equals(MSalesRegionL1.Table_Name)){	//JPIERE-0151
				return MSalesRegionL1.class;
			}else if(tableName.equals(MSalesRegionG.Table_Name)){	//JPIERE-0152
				return MSalesRegionG.class;
			}else if(tableName.equals(MSalesRegionGLine.Table_Name)){//JPIERE-0152
				return MSalesRegionGLine.class;
			}else if(tableName.equals(MDeliveryDays.Table_Name)){	//JPIERE-0153
				return MDeliveryDays.class;
			}else if(tableName.equals(MInvValProfile.Table_Name)){	//JPIERE-0160
				return MInvValProfile.class;
			}else if(tableName.equals(MInvValProfileOrg.Table_Name)){	//JPIERE-0160
				return MInvValProfileOrg.class;
			}else if(tableName.equals(MInvValCal.Table_Name)){	//JPIERE-0161
				return MInvValCal.class;
			}else if(tableName.equals(MInvValCalLine.Table_Name)){	//JPIERE-0161
				return MInvValCalLine.class;
			}else if(tableName.equals(MInvValCalLog.Table_Name)){	//JPIERE-0161
				return MInvValCalLog.class;
			}else if(tableName.equals(MInvValAdjust.Table_Name)){	//JPIERE-0163
				return MInvValAdjust.class;
			}else if(tableName.equals(MInvValAdjustLine.Table_Name)){	//JPIERE-0163
				return MInvValAdjustLine.class;
			}else if(tableName.equals(MInventoryDiffQtyLog.Table_Name)){	//JPIERE-0163
				return MInventoryDiffQtyLog.class;
			}

		}else{
			if(tableName.equals(MOrder.Table_Name)){
				return MOrderJP.class;
			}
		}

		return null;
	}

	@Override
	public PO getPO(String tableName, int Record_ID, String trxName) {

		if(tableName.startsWith("JP"))
		{

			if(tableName.equals(MCorporation.Table_Name)){
				return  new MCorporation(Env.getCtx(), Record_ID, trxName);
			}else if(tableName.equals(MCorporationGroup.Table_Name)){
				return  new MCorporationGroup(Env.getCtx(), Record_ID, trxName);
			}else if(tableName.equals(MGroupCorporations.Table_Name)){
				return  new MGroupCorporations(Env.getCtx(), Record_ID, trxName);
			}else if(tableName.equals(MBill.Table_Name)){
				return  new MBill(Env.getCtx(), Record_ID, trxName);
			}else if(tableName.equals(MBillLine.Table_Name)){
				return  new MBillLine(Env.getCtx(), Record_ID, trxName);
			}else if(tableName.equals(MReferenceTest.Table_Name)){
				return  new MReferenceTest(Env.getCtx(), Record_ID, trxName);
			}else if(tableName.equals(MBillSchema.Table_Name)){
				return  new MBillSchema(Env.getCtx(), Record_ID, trxName);
			}else if(tableName.equals(MProductCategoryL1.Table_Name)){
				return  new MProductCategoryL1(Env.getCtx(), Record_ID, trxName);
			}else if(tableName.equals(MProductCategoryL2.Table_Name)){
				return  new MProductCategoryL2(Env.getCtx(), Record_ID, trxName);
			}else if(tableName.equals(MProductCategoryG.Table_Name)){
				return  new MProductCategoryG(Env.getCtx(), Record_ID, trxName);
			}else if(tableName.equals(MProductCategoryGLine.Table_Name)){
				return  new MProductCategoryGLine(Env.getCtx(), Record_ID, trxName);
			}else if(tableName.equals(MProductGroup.Table_Name)){
				return  new MProductGroup(Env.getCtx(), Record_ID, trxName);
			}else if(tableName.equals(MProductGroupLine.Table_Name)){
				return  new MProductGroupLine(Env.getCtx(), Record_ID, trxName);
			}else if(tableName.equals(MSalesRegionL2.Table_Name)){	//JPIERE-0151
				return  new MSalesRegionL2(Env.getCtx(), Record_ID, trxName);
			}else if(tableName.equals(MSalesRegionL1.Table_Name)){	//JPIERE-0151
				return  new MSalesRegionL1(Env.getCtx(), Record_ID, trxName);
			}else if(tableName.equals(MSalesRegionG.Table_Name)){	//JPIERE-0152
				return  new MSalesRegionG(Env.getCtx(), Record_ID, trxName);
			}else if(tableName.equals(MSalesRegionGLine.Table_Name)){//JPIERE-0152
				return  new MSalesRegionGLine(Env.getCtx(), Record_ID, trxName);
			}else if(tableName.equals(MDeliveryDays.Table_Name)){	//JPIERE-0153
				return  new MDeliveryDays(Env.getCtx(), Record_ID, trxName);
			}else if(tableName.equals(MInvValProfile.Table_Name)){	//JPIERE-0160
				return  new MInvValProfile(Env.getCtx(), Record_ID, trxName);
			}else if(tableName.equals(MInvValProfileOrg.Table_Name)){	//JPIERE-0160
				return  new MInvValProfileOrg(Env.getCtx(), Record_ID, trxName);
			}else if(tableName.equals(MInvValCal.Table_Name)){	//JPIERE-0161
				return  new MInvValCal(Env.getCtx(), Record_ID, trxName);
			}else if(tableName.equals(MInvValCalLine.Table_Name)){	//JPIERE-0161
				return  new MInvValCalLine(Env.getCtx(), Record_ID, trxName);
			}else if(tableName.equals(MInvValCalLog.Table_Name)){	//JPIERE-0161
				return  new MInvValCalLog(Env.getCtx(), Record_ID, trxName);
			}else if(tableName.equals(MInvValAdjust.Table_Name)){	//JPIERE-0163
				return  new MInvValAdjust(Env.getCtx(), Record_ID, trxName);
			}else if(tableName.equals(MInvValAdjustLine.Table_Name)){	//JPIERE-0163
				return  new MInvValAdjustLine(Env.getCtx(), Record_ID, trxName);
			}else if(tableName.equals(MInventoryDiffQtyLog.Table_Name)){	//JPIERE-0163
				return  new MInventoryDiffQtyLog(Env.getCtx(), Record_ID, trxName);
			}

		}else{
			if(tableName.equals(MOrder.Table_Name)){
				return  new MOrderJP(Env.getCtx(), Record_ID, trxName);
			}
		}

		return null;
	}

	@Override
	public PO getPO(String tableName, ResultSet rs, String trxName) {

		if(tableName.startsWith("JP"))
		{

			if(tableName.equals(MCorporation.Table_Name)){
				return  new MCorporation(Env.getCtx(), rs, trxName);
			}else if(tableName.equals(MCorporationGroup.Table_Name)){
				return  new MCorporationGroup(Env.getCtx(), rs, trxName);
			}else if(tableName.equals(MGroupCorporations.Table_Name)){
				return  new MGroupCorporations(Env.getCtx(), rs, trxName);
			}else if(tableName.equals(MBill.Table_Name)){
				return  new MBill(Env.getCtx(), rs, trxName);
			}else if(tableName.equals(MBillLine.Table_Name)){
				return  new MBillLine(Env.getCtx(), rs, trxName);
			}else if(tableName.equals(MReferenceTest.Table_Name)){
				return  new MReferenceTest(Env.getCtx(), rs, trxName);
			}else if(tableName.equals(MBillSchema.Table_Name)){
				return  new MBillSchema(Env.getCtx(), rs, trxName);
			}else if(tableName.equals(MProductCategoryL1.Table_Name)){
				return  new MProductCategoryL1(Env.getCtx(), rs, trxName);
			}else if(tableName.equals(MProductCategoryL2.Table_Name)){
				return  new MProductCategoryL2(Env.getCtx(), rs, trxName);
			}else if(tableName.equals(MProductCategoryG.Table_Name)){
				return  new MProductCategoryG(Env.getCtx(), rs, trxName);
			}else if(tableName.equals(MProductCategoryGLine.Table_Name)){
				return  new MProductCategoryGLine(Env.getCtx(), rs, trxName);
			}else if(tableName.equals(MProductGroup.Table_Name)){
				return  new MProductGroup(Env.getCtx(), rs, trxName);
			}else if(tableName.equals(MProductGroupLine.Table_Name)){
				return  new MProductGroupLine(Env.getCtx(), rs, trxName);
			}else if(tableName.equals(MSalesRegionL2.Table_Name)){	//JPIERE-0151
				return  new MSalesRegionL2(Env.getCtx(), rs, trxName);
			}else if(tableName.equals(MSalesRegionL1.Table_Name)){	//JPIERE-0151
				return  new MSalesRegionL1(Env.getCtx(), rs, trxName);
			}else if(tableName.equals(MSalesRegionG.Table_Name)){	//JPIERE-0152
				return  new MSalesRegionG(Env.getCtx(), rs, trxName);
			}else if(tableName.equals(MSalesRegionGLine.Table_Name)){//JPIERE-0152
				return  new MSalesRegionGLine(Env.getCtx(), rs, trxName);
			}else if(tableName.equals(MDeliveryDays.Table_Name)){	//JPIERE-0153
				return  new MDeliveryDays(Env.getCtx(), rs, trxName);
			}else if(tableName.equals(MInvValProfile.Table_Name)){	//JPIERE-0160
				return  new MInvValProfile(Env.getCtx(), rs, trxName);
			}else if(tableName.equals(MInvValProfileOrg.Table_Name)){	//JPIERE-0160
				return  new MInvValProfileOrg(Env.getCtx(), rs, trxName);
			}else if(tableName.equals(MInvValCal.Table_Name)){	//JPIERE-0161
				return  new MInvValCal(Env.getCtx(), rs, trxName);
			}else if(tableName.equals(MInvValCalLine.Table_Name)){	//JPIERE-0161
				return  new MInvValCalLine(Env.getCtx(), rs, trxName);
			}else if(tableName.equals(MInvValCalLog.Table_Name)){	//JPIERE-0161
				return  new MInvValCalLog(Env.getCtx(), rs, trxName);
			}else if(tableName.equals(MInvValAdjust.Table_Name)){	//JPIERE-0163
				return  new MInvValAdjust(Env.getCtx(), rs, trxName);
			}else if(tableName.equals(MInvValAdjustLine.Table_Name)){	//JPIERE-0163
				return  new MInvValAdjustLine(Env.getCtx(), rs, trxName);
			}else if(tableName.equals(MInventoryDiffQtyLog.Table_Name)){	//JPIERE-0163
				return  new MInventoryDiffQtyLog(Env.getCtx(), rs, trxName);
			}

		}else{
			if(tableName.equals(MOrder.Table_Name)){
				return  new MOrderJP(Env.getCtx(), rs, trxName);
			}
		}

		return null;
	}

}
