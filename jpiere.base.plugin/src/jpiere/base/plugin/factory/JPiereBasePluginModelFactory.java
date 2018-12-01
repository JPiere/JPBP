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

import java.lang.reflect.Constructor;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.logging.Level;

import org.adempiere.base.IModelFactory;
import org.compiere.model.MInOutConfirm;
import org.compiere.model.MInvoice;
import org.compiere.model.MOrder;
import org.compiere.model.PO;
import org.compiere.util.CCache;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.MInOutConfirmJP;
import jpiere.base.plugin.org.adempiere.model.MInvoiceJP;
import jpiere.base.plugin.org.adempiere.model.MOrderJP;

/**
 *  JPiere Base Plugin Model Factory
 *
 *  @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 *
 */
public class JPiereBasePluginModelFactory implements IModelFactory {

	private static CCache<String,Class<?>> s_classCache = new CCache<String,Class<?>>(null, "PO_Class", 20, false);
	private final static CLogger s_log = CLogger.getCLogger(JPiereBasePluginModelFactory.class);

	@Override
	public Class<?> getClass(String tableName)
	{
		if(tableName.startsWith("JP"))
		{
			if (tableName.endsWith("_Trl"))
				return null;

			//check cache
			Class<?> cache = s_classCache.get(tableName);
			if (cache != null)
			{
				//Object.class indicate no generated PO class for tableName
				if (cache.equals(Object.class))
					return null;
				else
					return cache;
			}

			String className = tableName;
			int index = className.indexOf('_');
			if (index > 0)
			{
				if (index < 3)		//	AD_, A_
					 className = className.substring(index+1);
				/* DELETEME: this part is useless - teo_sarca, [ 1648850 ]
				else
				{
					String prefix = className.substring(0,index);
					if (prefix.equals("Fact"))		//	keep custom prefix
						className = className.substring(index+1);
				}
				*/
			}
			//	Remove underlines
			className = Util.replace(className, "_", "");

			//	Search packages
			StringBuffer name = new StringBuffer("jpiere.base.plugin.org.adempiere.model").append(".M").append(className);
			Class<?> clazz = getPOclass(name.toString(), tableName);
			if (clazz != null)
			{
				s_classCache.put(tableName, clazz);
				return clazz;
			}


			//	Adempiere Extension
			clazz = getPOclass("jpiere.base.plugin.org.adempiere.model.X_" + tableName, tableName);
			if (clazz != null)
			{
				s_classCache.put(tableName, clazz);
				return clazz;
			}

		}else{
			if(tableName.equals(MOrder.Table_Name)){
				return MOrderJP.class;
			}else if(tableName.equals(MInOutConfirm.Table_Name)){
				return MInOutConfirmJP.class;			//JPIERE-0208
			}else if(tableName.equals(MInvoice.Table_Name)){
				return MInvoiceJP.class;			//JPIERE-0295
			}
		}

		return null;
	}

	@Override
	public PO getPO(String tableName, int Record_ID, String trxName) {

		if(tableName.startsWith("JP"))
		{
			Class<?> clazz = getClass(tableName);
			if (clazz == null)
			{
				return null;
			}

			boolean errorLogged = false;
			try
			{
				Constructor<?> constructor = null;
				try
				{
					constructor = clazz.getDeclaredConstructor(new Class[]{Properties.class, int.class, String.class});
				}
				catch (Exception e)
				{
					String msg = e.getMessage();
					if (msg == null)
						msg = e.toString();
					s_log.warning("No transaction Constructor for " + clazz + " (" + msg + ")");
				}

				PO po = constructor!=null ? (PO)constructor.newInstance(new Object[] {Env.getCtx(), new Integer(Record_ID), trxName}) : null;
				return po;
			}
			catch (Exception e)
			{
				if (e.getCause() != null)
				{
					Throwable t = e.getCause();
					s_log.log(Level.SEVERE, "(id) - Table=" + tableName + ",Class=" + clazz, t);
					errorLogged = true;
					if (t instanceof Exception)
						s_log.saveError("Error", (Exception)e.getCause());
					else
						s_log.saveError("Error", "Table=" + tableName + ",Class=" + clazz);
				}
				else
				{
					s_log.log(Level.SEVERE, "(id) - Table=" + tableName + ",Class=" + clazz, e);
					errorLogged = true;
					s_log.saveError("Error", "Table=" + tableName + ",Class=" + clazz);
				}
			}
			if (!errorLogged)
				s_log.log(Level.SEVERE, "(id) - Not found - Table=" + tableName
					+ ", Record_ID=" + Record_ID);
			return null;

//
//			if(tableName.equals(MCorporation.Table_Name)){
//				return  new MCorporation(Env.getCtx(), Record_ID, trxName);
//			}else if(tableName.equals(MCorporationGroup.Table_Name)){
//				return  new MCorporationGroup(Env.getCtx(), Record_ID, trxName);
//			}else if(tableName.equals(MGroupCorporations.Table_Name)){
//				return  new MGroupCorporations(Env.getCtx(), Record_ID, trxName);
//			}else if(tableName.equals(MBill.Table_Name)){
//				return  new MBill(Env.getCtx(), Record_ID, trxName);
//			}else if(tableName.equals(MBillLine.Table_Name)){
//				return  new MBillLine(Env.getCtx(), Record_ID, trxName);
//			}else if(tableName.equals(MReferenceTest.Table_Name)){
//				return  new MReferenceTest(Env.getCtx(), Record_ID, trxName);
//			}else if(tableName.equals(MBillSchema.Table_Name)){
//				return  new MBillSchema(Env.getCtx(), Record_ID, trxName);
//			}else if(tableName.equals(MProductCategoryL1.Table_Name)){
//				return  new MProductCategoryL1(Env.getCtx(), Record_ID, trxName);
//			}else if(tableName.equals(MProductCategoryL2.Table_Name)){
//				return  new MProductCategoryL2(Env.getCtx(), Record_ID, trxName);
//			}else if(tableName.equals(MProductCategoryG.Table_Name)){
//				return  new MProductCategoryG(Env.getCtx(), Record_ID, trxName);
//			}else if(tableName.equals(MProductCategoryGLine.Table_Name)){
//				return  new MProductCategoryGLine(Env.getCtx(), Record_ID, trxName);
//			}else if(tableName.equals(MProductGroup.Table_Name)){
//				return  new MProductGroup(Env.getCtx(), Record_ID, trxName);
//			}else if(tableName.equals(MProductGroupLine.Table_Name)){
//				return  new MProductGroupLine(Env.getCtx(), Record_ID, trxName);
//			}else if(tableName.equals(MSalesRegionL2.Table_Name)){	//JPIERE-0151
//				return  new MSalesRegionL2(Env.getCtx(), Record_ID, trxName);
//			}else if(tableName.equals(MSalesRegionL1.Table_Name)){	//JPIERE-0151
//				return  new MSalesRegionL1(Env.getCtx(), Record_ID, trxName);
//			}else if(tableName.equals(MSalesRegionG.Table_Name)){	//JPIERE-0152
//				return  new MSalesRegionG(Env.getCtx(), Record_ID, trxName);
//			}else if(tableName.equals(MSalesRegionGLine.Table_Name)){//JPIERE-0152
//				return  new MSalesRegionGLine(Env.getCtx(), Record_ID, trxName);
//			}else if(tableName.equals(MDeliveryDays.Table_Name)){	//JPIERE-0153
//				return  new MDeliveryDays(Env.getCtx(), Record_ID, trxName);
//			}else if(tableName.equals(MInvValProfile.Table_Name)){	//JPIERE-0160
//				return  new MInvValProfile(Env.getCtx(), Record_ID, trxName);
//			}else if(tableName.equals(MInvValProfileOrg.Table_Name)){	//JPIERE-0160
//				return  new MInvValProfileOrg(Env.getCtx(), Record_ID, trxName);
//			}else if(tableName.equals(MInvValCal.Table_Name)){	//JPIERE-0161
//				return  new MInvValCal(Env.getCtx(), Record_ID, trxName);
//			}else if(tableName.equals(MInvValCalLine.Table_Name)){	//JPIERE-0161
//				return  new MInvValCalLine(Env.getCtx(), Record_ID, trxName);
//			}else if(tableName.equals(MInvValCalLog.Table_Name)){	//JPIERE-0161
//				return  new MInvValCalLog(Env.getCtx(), Record_ID, trxName);
//			}else if(tableName.equals(MInvValAdjust.Table_Name)){	//JPIERE-0163
//				return  new MInvValAdjust(Env.getCtx(), Record_ID, trxName);
//			}else if(tableName.equals(MInvValAdjustLine.Table_Name)){	//JPIERE-0163
//				return  new MInvValAdjustLine(Env.getCtx(), Record_ID, trxName);
//			}else if(tableName.equals(MInventoryDiffQtyLog.Table_Name)){	//JPIERE-0163
//				return  new MInventoryDiffQtyLog(Env.getCtx(), Record_ID, trxName);
//			}else if(tableName.equals(MEstimation.Table_Name)){	//JPIERE-0183
//				return  new MEstimation(Env.getCtx(), Record_ID, trxName);
//			}else if(tableName.equals(MEstimationLine.Table_Name)){	//JPIERE-0183
//				return  new MEstimationLine(Env.getCtx(), Record_ID, trxName);
//			}else if(tableName.equals(MEstimationTax.Table_Name)){	//JPIERE-0183
//				return  new MEstimationTax(Env.getCtx(), Record_ID, trxName);
//			}else if(tableName.equals(MBankDataSchema.Table_Name)){//JPIERE-0301
//				return new MBankDataSchema(Env.getCtx(), Record_ID, trxName);
//			}else if(tableName.equals(MBankData.Table_Name)){//JPIERE-0302
//				return new MBankData(Env.getCtx(), Record_ID, trxName);
//			}else if(tableName.equals(MBankDataLine.Table_Name)){	//JPIERE-0302
//				return new MBankDataLine(Env.getCtx(), Record_ID, trxName);
//			}else if(tableName.equals(MPhysicalWarehouse.Table_Name)){	//JPIERE-0317
//				return new MPhysicalWarehouse(Env.getCtx(), Record_ID, trxName);
//			}else if(tableName.equals(MContractProcPeriod.Table_Name)){	//JPIERE-0363
//				return new MContractProcPeriod(Env.getCtx(), Record_ID, trxName);
//			}

		}else{
			if(tableName.equals(MOrder.Table_Name)){
				return  new MOrderJP(Env.getCtx(), Record_ID, trxName);
			}else if(tableName.equals(MInOutConfirm.Table_Name)){
				return new MInOutConfirmJP(Env.getCtx(), Record_ID, trxName);			//JPIERE-0208
			}else if(tableName.equals(MInvoice.Table_Name)){
				return new MInvoiceJP(Env.getCtx(), Record_ID, trxName);			//JPIERE-0295
			}
		}

		return null;
	}

	@Override
	public PO getPO(String tableName, ResultSet rs, String trxName) {

		if(tableName.startsWith("JP"))
		{
			Class<?> clazz = getClass(tableName);
			if (clazz == null)
			{
				return null;
			}

			boolean errorLogged = false;
			try
			{
				Constructor<?> constructor = clazz.getDeclaredConstructor(new Class[]{Properties.class, ResultSet.class, String.class});
				PO po = (PO)constructor.newInstance(new Object[] {Env.getCtx(), rs, trxName});
				return po;
			}
			catch (Exception e)
			{
				s_log.log(Level.SEVERE, "(rs) - Table=" + tableName + ",Class=" + clazz, e);
				errorLogged = true;
				s_log.saveError("Error", "Table=" + tableName + ",Class=" + clazz);
			}
			if (!errorLogged)
				s_log.log(Level.SEVERE, "(rs) - Not found - Table=" + tableName);
			return null;

//
//			if(tableName.equals(MCorporation.Table_Name)){
//				return  new MCorporation(Env.getCtx(), rs, trxName);
//			}else if(tableName.equals(MCorporationGroup.Table_Name)){
//				return  new MCorporationGroup(Env.getCtx(), rs, trxName);
//			}else if(tableName.equals(MGroupCorporations.Table_Name)){
//				return  new MGroupCorporations(Env.getCtx(), rs, trxName);
//			}else if(tableName.equals(MBill.Table_Name)){
//				return  new MBill(Env.getCtx(), rs, trxName);
//			}else if(tableName.equals(MBillLine.Table_Name)){
//				return  new MBillLine(Env.getCtx(), rs, trxName);
//			}else if(tableName.equals(MReferenceTest.Table_Name)){
//				return  new MReferenceTest(Env.getCtx(), rs, trxName);
//			}else if(tableName.equals(MBillSchema.Table_Name)){
//				return  new MBillSchema(Env.getCtx(), rs, trxName);
//			}else if(tableName.equals(MProductCategoryL1.Table_Name)){
//				return  new MProductCategoryL1(Env.getCtx(), rs, trxName);
//			}else if(tableName.equals(MProductCategoryL2.Table_Name)){
//				return  new MProductCategoryL2(Env.getCtx(), rs, trxName);
//			}else if(tableName.equals(MProductCategoryG.Table_Name)){
//				return  new MProductCategoryG(Env.getCtx(), rs, trxName);
//			}else if(tableName.equals(MProductCategoryGLine.Table_Name)){
//				return  new MProductCategoryGLine(Env.getCtx(), rs, trxName);
//			}else if(tableName.equals(MProductGroup.Table_Name)){
//				return  new MProductGroup(Env.getCtx(), rs, trxName);
//			}else if(tableName.equals(MProductGroupLine.Table_Name)){
//				return  new MProductGroupLine(Env.getCtx(), rs, trxName);
//			}else if(tableName.equals(MSalesRegionL2.Table_Name)){	//JPIERE-0151
//				return  new MSalesRegionL2(Env.getCtx(), rs, trxName);
//			}else if(tableName.equals(MSalesRegionL1.Table_Name)){	//JPIERE-0151
//				return  new MSalesRegionL1(Env.getCtx(), rs, trxName);
//			}else if(tableName.equals(MSalesRegionG.Table_Name)){	//JPIERE-0152
//				return  new MSalesRegionG(Env.getCtx(), rs, trxName);
//			}else if(tableName.equals(MSalesRegionGLine.Table_Name)){//JPIERE-0152
//				return  new MSalesRegionGLine(Env.getCtx(), rs, trxName);
//			}else if(tableName.equals(MDeliveryDays.Table_Name)){	//JPIERE-0153
//				return  new MDeliveryDays(Env.getCtx(), rs, trxName);
//			}else if(tableName.equals(MInvValProfile.Table_Name)){	//JPIERE-0160
//				return  new MInvValProfile(Env.getCtx(), rs, trxName);
//			}else if(tableName.equals(MInvValProfileOrg.Table_Name)){	//JPIERE-0160
//				return  new MInvValProfileOrg(Env.getCtx(), rs, trxName);
//			}else if(tableName.equals(MInvValCal.Table_Name)){	//JPIERE-0161
//				return  new MInvValCal(Env.getCtx(), rs, trxName);
//			}else if(tableName.equals(MInvValCalLine.Table_Name)){	//JPIERE-0161
//				return  new MInvValCalLine(Env.getCtx(), rs, trxName);
//			}else if(tableName.equals(MInvValCalLog.Table_Name)){	//JPIERE-0161
//				return  new MInvValCalLog(Env.getCtx(), rs, trxName);
//			}else if(tableName.equals(MInvValAdjust.Table_Name)){	//JPIERE-0163
//				return  new MInvValAdjust(Env.getCtx(), rs, trxName);
//			}else if(tableName.equals(MInvValAdjustLine.Table_Name)){	//JPIERE-0163
//				return  new MInvValAdjustLine(Env.getCtx(), rs, trxName);
//			}else if(tableName.equals(MInventoryDiffQtyLog.Table_Name)){	//JPIERE-0163
//				return  new MInventoryDiffQtyLog(Env.getCtx(), rs, trxName);
//			}else if(tableName.equals(MEstimation.Table_Name)){	//JPIERE-0183
//				return  new MEstimation(Env.getCtx(), rs, trxName);
//			}else if(tableName.equals(MEstimationLine.Table_Name)){	//JPIERE-0183
//				return  new MEstimationLine(Env.getCtx(), rs, trxName);
//			}else if(tableName.equals(MEstimationTax.Table_Name)){	//JPIERE-0183
//				return  new MEstimationTax(Env.getCtx(), rs, trxName);
//			}else if(tableName.equals(MBankDataSchema.Table_Name)){//JPIERE-0301
//				return new MBankDataSchema(Env.getCtx(), rs, trxName);
//			}else if(tableName.equals(MBankData.Table_Name)){//JPIERE-0302
//				return new MBankData(Env.getCtx(), rs, trxName);
//			}else if(tableName.equals(MBankDataLine.Table_Name)){	//JPIERE-0302
//				return new MBankDataLine(Env.getCtx(), rs, trxName);
//			}else if(tableName.equals(MPhysicalWarehouse.Table_Name)){	//JPIERE-0317
//				return new MPhysicalWarehouse(Env.getCtx(), rs, trxName);
//			}else if(tableName.equals(MContractProcPeriod.Table_Name)){	//JPIERE-0363
//				return new MContractProcPeriod(Env.getCtx(), rs, trxName);
//			}



		}else{
			if(tableName.equals(MOrder.Table_Name)){
				return  new MOrderJP(Env.getCtx(), rs, trxName);
			}else if(tableName.equals(MInOutConfirm.Table_Name)){
				return new MInOutConfirmJP(Env.getCtx(), rs, trxName);			//JPIERE-0208
			}else if(tableName.equals(MInvoice.Table_Name)){
				return new MInvoiceJP(Env.getCtx(), rs, trxName);			//JPIERE-0295
			}
		}

		return null;
	}


	/**
	 * Get PO class
	 * @param className fully qualified class name
	 * @param tableName Optional. If specified, the loaded class will be validated for that table name
	 * @return class or null
	 */
	private Class<?> getPOclass (String className, String tableName)
	{
		try
		{
			Class<?> clazz = Class.forName(className);
			// Validate if the class is for specified tableName
			if (tableName != null)
			{
				String classTableName = clazz.getField("Table_Name").get(null).toString();
				if (!tableName.equals(classTableName))
				{
					if (s_log.isLoggable(Level.FINEST)) s_log.finest("Invalid class for table: " + className+" (tableName="+tableName+", classTableName="+classTableName+")");
					return null;
				}
			}
			//	Make sure that it is a PO class
			Class<?> superClazz = clazz.getSuperclass();
			while (superClazz != null)
			{
				if (superClazz == PO.class)
				{
					if (s_log.isLoggable(Level.FINE)) s_log.fine("Use: " + className);
					return clazz;
				}
				superClazz = superClazz.getSuperclass();
			}
		}
		catch (Exception e)
		{
		}
		if (s_log.isLoggable(Level.FINEST)) s_log.finest("Not found: " + className);
		return null;
	}	//	getPOclass
}
