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
import java.util.Properties;

import org.compiere.model.I_C_DocType;
import org.compiere.util.Msg;


/**
 * JPIERE-0107 Bill Schema Model
 * JPIERE-0277 Payment Request Model
 *
 *  @author Hideaki Hagiwara
 *
 */
public class MBillSchema extends X_JP_BillSchema {

	public MBillSchema(Properties ctx, int JP_BillSchema_ID, String trxName) {
		super(ctx, JP_BillSchema_ID, trxName);
	}

	public MBillSchema(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {

		if(newRecord || is_ValueChanged("C_DocType_ID"))
		{
			I_C_DocType docType = getC_DocType();
			if(!(docType.getDocBaseType().equals("JPB") ||docType.getDocBaseType().equals("JPP")))
			{
				log.saveError("Error", Msg.getMsg(getCtx(), "JP_DocTypeIncorrect"));
				return false;
			}
		}


		return true;
	}




}
