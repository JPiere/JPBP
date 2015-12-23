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
import jpiere.base.plugin.org.adempiere.model.MGroupCorporations;
import jpiere.base.plugin.org.adempiere.model.MReferenceTest;

import org.adempiere.base.IModelFactory;
import org.compiere.model.PO;
import org.compiere.util.Env;

/**
 *  JPiere Base Plugin Model Factory
 *
 *  JPIERE-0024:JPBP:Corporation Master & Corporation Group Master
 *  JPIERE-0106:JPBP:Bill
 *
 *  @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 *
 */
public class JPiereBasePluginModelFactory implements IModelFactory {

	@Override
	public Class<?> getClass(String tableName) {
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
		}

		return null;
	}

	@Override
	public PO getPO(String tableName, int Record_ID, String trxName) {
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
		}


		return null;
	}

	@Override
	public PO getPO(String tableName, ResultSet rs, String trxName) {
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
		}

		return null;
	}

}
