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

import java.util.logging.Level;

import org.compiere.model.MProcess;
import org.compiere.model.MTable;
import org.compiere.model.PO;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Msg;
import org.compiere.util.Util;
import org.compiere.wf.MWFActivity;
import org.compiere.wf.MWorkflow;

/**
 *  JPIERE-0607: Cancel Workflow.
 *  Start Cancel Workflow.
 *
 *  @author Hideaki Hagiwara（h.hagiwara@oss-erp.co.jp）
 *
 */
public class CancelWFStart extends SvrProcess {
	
	 private static final String COLUMNNAME_JP_CancelWFAction = "JP_CancelWFAction";
	 private static final String COLUMNNAME_JP_CancelWFStatus = "JP_CancelWFStatus";
	 private static final String COLUMNNAME_JP_CancelWorkflow_ID = "JP_CancelWorkflow_ID";
	 
	private String p_DocAction = null;
	
	@Override
	protected void prepare() 
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
			{
				;
			}else if (name.equals("DocAction")){

				p_DocAction = para[i].getParameterAsString();

			}else {
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}
		}
	}

	@Override
	protected String doIt() throws Exception
	{
		if(Util.isEmpty(p_DocAction))
		{
			throw new Exception(Msg.getMsg(getCtx(), "FillMandatory") + Msg.getElement(getCtx(), "DocAction"));
		}
		
		
		int AD_Table_ID = getTable_ID();
		int Record_ID = getRecord_ID();
		
		//Check to avoid applying twice
		String wfStatus = MWFActivity.getActiveInfo(getCtx(), AD_Table_ID, Record_ID);
		if (wfStatus != null)
		{
			throw new Exception(Msg.getMsg(getCtx(), "WFActiveForRecord")+wfStatus);
		}
		
		
		//Set _JP_CancelWFAction AND _JP_CancelWFStatus
		MTable m_Table = MTable.get(AD_Table_ID);
		PO po = m_Table.getPO(Record_ID, get_TrxName());
		if(po.get_ColumnIndex(COLUMNNAME_JP_CancelWFAction) > -1 && po.get_ColumnIndex(COLUMNNAME_JP_CancelWFStatus) > -1 )
		{
			po.set_ValueNoCheck(COLUMNNAME_JP_CancelWFAction, p_DocAction);
			po.set_ValueNoCheck(COLUMNNAME_JP_CancelWFStatus, DocAction.STATUS_InProgress);
			if(po instanceof DocAction)
			{
				po.saveEx(get_TrxName());

			}else {
				throw new Exception("It is not an instance of DocAction.");
			}
			
		}else {
			
			if(po.get_ColumnIndex(COLUMNNAME_JP_CancelWFAction) > -1 )
			{
				throw new Exception(Msg.getMsg(getCtx(), "NotFound") + Msg.getElement(getCtx(), COLUMNNAME_JP_CancelWFStatus) );
				
			}else if(po.get_ColumnIndex(COLUMNNAME_JP_CancelWFStatus) > -1 ) {
				
				throw new Exception(Msg.getMsg(getCtx(), "NotFound") + Msg.getElement(getCtx(), COLUMNNAME_JP_CancelWFAction) );
				
			}else {
			
				throw new Exception(Msg.getMsg(getCtx(), "NotFound") + Msg.getElement(getCtx(), COLUMNNAME_JP_CancelWFAction) + " & " + Msg.getElement(getCtx(), COLUMNNAME_JP_CancelWFStatus) );
			}
			
		}
		
		//Launch the Cancel workflow
		int AD_Process_ID = getProcessInfo().getAD_Process_ID();		
		MProcess m_Process = MProcess.get(AD_Process_ID);
		String string_JP_CancelWorkflow_ID = m_Process.get_ValueAsString(COLUMNNAME_JP_CancelWorkflow_ID);
		int  JP_CancelWorkflow_ID = Integer.parseInt(string_JP_CancelWorkflow_ID);
		if(JP_CancelWorkflow_ID == 0)
		{
			throw new Exception(Msg.getMsg(getCtx(), "NotFound") + Msg.getElement(getCtx(), COLUMNNAME_JP_CancelWorkflow_ID) );
		}
		MWorkflow wf = MWorkflow.get (getCtx(), JP_CancelWorkflow_ID);
		wf.start(getProcessInfo(), get_TrxName());
		
		return Msg.getElement(getCtx(), COLUMNNAME_JP_CancelWFAction);
	}

}
