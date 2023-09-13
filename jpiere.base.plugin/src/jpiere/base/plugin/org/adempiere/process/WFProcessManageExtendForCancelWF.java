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

import org.compiere.model.MProcessPara;
import org.compiere.model.MTable;
import org.compiere.model.PO;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.wf.MWFProcess;
import org.compiere.wf.WFProcessManage;

/**
 * JPIERE-0607: Cancel Workflow.
 *
 *  Ref: Manage Workflow Process(WFProcessManage.java)
 *
 *  @author H.Hagiwara
 *
 */
public class WFProcessManageExtendForCancelWF extends WFProcessManage {
	
	private static final String COLUMNNAME_JP_CancelWFAction = "JP_CancelWFAction";
	private static final String COLUMNNAME_JP_CancelWFStatus = "JP_CancelWFStatus";
	 
	/**	Abort It				*/	
	private boolean		p_IsAbort = false;
	/** Record					*/
	private int			p_AD_WF_Process_ID = 0;
		
	
	protected void prepare()
	{
		super.prepare();
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
			else if (name.equals("IsAbort"))
				p_IsAbort = "Y".equals(para[i].getParameter());
			else
				MProcessPara.validateUnknownParameter(getProcessInfo().getAD_Process_ID(), para[i]);
		}
		p_AD_WF_Process_ID = getRecord_ID();
	}

	protected String doIt() throws Exception
	{	
		super.doIt();
		
		//JPIERE-0607 Cancel WF
		if(p_IsAbort)
		{
			MWFProcess process = new MWFProcess (getCtx(), p_AD_WF_Process_ID, get_TrxName());
			int AD_Table_ID = process.getAD_Table_ID();
			MTable m_Table = MTable.get(AD_Table_ID);
			int Record_ID = process.getRecord_ID();
			PO po = m_Table.getPO(Record_ID, get_TrxName());
			po.set_ValueNoCheck(COLUMNNAME_JP_CancelWFAction, null);
			po.set_ValueNoCheck(COLUMNNAME_JP_CancelWFStatus, null);
			po.saveEx(get_TrxName());
		}
		
		
		return "OK";
	}
}
