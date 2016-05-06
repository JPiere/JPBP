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

import org.compiere.model.MDocType;
import org.compiere.model.MOrder;
import org.compiere.model.MProject;
import org.compiere.process.DocOptions;
import org.compiere.process.DocumentEngine;

/**
 * JPIERE-0142
 * 
 * @author Hideaki Hagiwara
 *
 */
public class MOrderJP extends MOrder implements DocOptions {
	
	public MOrderJP(Properties ctx, int C_Order_ID, String trxName) {
		super(ctx, C_Order_ID, trxName);
	}
	
	public MOrderJP(MProject project, boolean IsSOTrx, String DocSubTypeSO) {
		super(project, IsSOTrx, DocSubTypeSO);
	}
	
	public MOrderJP(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}
	
	@Override
	public int customizeValidActions(String docStatus, Object processing, String orderType, String isSOTrx,
			int AD_Table_ID, String[] docAction, String[] options, int index) {
		
		if(isSOTrx.equals("Y") && (orderType.equals(MDocType.DOCSUBTYPESO_Proposal)
				|| orderType.equals(MDocType.DOCSUBTYPESO_Quotation)))
		{
			index = 0; //initialize the index
			options[index++] = DocumentEngine.ACTION_Prepare; 
			options[index++] = DocumentEngine.ACTION_Void; 
			return index;
		}
		
//		if (docStatus.equals(DocumentEngine.STATUS_Completed)) 
//		{
//			index = 0; //initialize the index
//			options[index++] = DocumentEngine.ACTION_Close; 
//			options[index++] = DocumentEngine.ACTION_ReActivate;
//			return index;
//		}
		
		return index;
	}
	
}
