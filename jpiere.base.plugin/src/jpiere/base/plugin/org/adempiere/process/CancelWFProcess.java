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
package jpiere.base.plugin.org.adempiere.process;

import org.compiere.model.MRefList;
import org.compiere.model.MTable;
import org.compiere.model.PO;
import org.compiere.process.DocAction;
import org.compiere.process.SvrProcess;
import org.compiere.util.Msg;

/**
 *  JPIERE-0607: Cancel Workflow.
 *  Do void or reverse.
 *
 *  @author Hideaki Hagiwara（h.hagiwara@oss-erp.co.jp）
 *
 */
public class CancelWFProcess extends SvrProcess {

	 private static final String COLUMNNAME_JP_CancelWFAction = "JP_CancelWFAction";
	 private static final String COLUMNNAME_JP_CancelWFStatus = "JP_CancelWFStatus";
	 
	@Override
	protected void prepare() 
	{
		;
	}

	@Override
	protected String doIt() throws Exception
	{
		int AD_Table_ID = getTable_ID();
		MTable m_Table = MTable.get(AD_Table_ID);
		int Record_ID = getRecord_ID();
		
		PO po = m_Table.getPO(Record_ID, get_TrxName());
		String  JP_CancelWFAction = po.get_ValueAsString(COLUMNNAME_JP_CancelWFAction) ;
		if(po instanceof DocAction)
		{
			DocAction docAction = (DocAction)po;
			docAction.processIt(JP_CancelWFAction);
			po.set_ValueNoCheck(COLUMNNAME_JP_CancelWFAction, null);
			po.set_ValueNoCheck(COLUMNNAME_JP_CancelWFStatus, null);
			docAction.saveEx();
		}
		
		//AD_Reference_ID=135(_Document Action)		
		return Msg.getElement(getCtx(), COLUMNNAME_JP_CancelWFAction) + " : " + JP_CancelWFAction + "(" + MRefList.getListName(getCtx(), 135, JP_CancelWFAction) + ")" ;
	}

}
