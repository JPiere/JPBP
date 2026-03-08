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
/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2006 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
package jpiere.base.plugin.org.adempiere.process;

import org.adempiere.base.IServiceReferenceHolder;
import org.adempiere.base.Service;
import org.adempiere.util.ProcessUtil;
import org.compiere.model.MProcessPara;
import org.compiere.model.MQuery;
import org.compiere.model.MTable;
import org.compiere.model.PO;
import org.compiere.model.PrintInfo;

import org.compiere.print.MPrintFormat;
import org.compiere.print.ReportEngine;
import org.compiere.print.ReportViewerProvider;
import org.compiere.process.ProcessInfo;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Trx;
import org.compiere.util.Util;


/**
 *  JPIERE-0655
 *  Generic Report Process for Print Icon
 *  @author h.hagiwara
 */
@org.adempiere.base.annotation.Process
public class GenericReportProcessForPrintIcon extends SvrProcess
{
	
	private int  p_AD_PrintFormat_ID = 0;
	private String p_ViewerType = null;
	
	/**
	 *  Prepare - e.g., get Parameters.
	 */
	protected void prepare()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null && para[i].getParameter_To() == null)
				;
			else if (name.equals("AD_PrintFormat_ID"))
				p_AD_PrintFormat_ID = para[i].getParameterAsInt();
			else if (name.equals("JP_ReportViewerType"))
				p_ViewerType = para[i].getParameterAsString();
			else
				MProcessPara.validateUnknownParameter(getProcessInfo().getAD_Process_ID(), para[i]);
		}
		
		processUI = Env.getProcessUI(getCtx());		
	}	//	prepare

	/**
	 *  Perform process.
	 *  @return Message
	 *  @throws Exception
	 */
	protected String doIt() throws java.lang.Exception
	{
		if(p_AD_PrintFormat_ID == 0)
			throw new Exception(Msg.getMsg(Env.getCtx(), "FillMandatory")+ Msg.getElement(getCtx(), "AD_PrintFormat_ID"));
		
		MTable m_Table = MTable.get(getTable_ID());
		PO po = m_Table.getPO(getRecord_ID(), null);
		MPrintFormat format = MPrintFormat.get (getCtx(), p_AD_PrintFormat_ID, false);		

		//Select Viewer
		if(("JR").equals(p_ViewerType) && format.getJasperProcess_ID() > 0 )
		{			
			ProcessInfo jr_pi = new ProcessInfo(format.get_Translation("Name"), format.getJasperProcess_ID(), getTable_ID(), getRecord_ID(), getRecord_UU());
			jr_pi.setAD_User_ID (Env.getAD_User_ID(getCtx()));
			jr_pi.setAD_Client_ID (Env.getAD_Client_ID(getCtx()));
			ProcessUtil.startJavaProcess(Env.getCtx(), jr_pi, Trx.get(Trx.createTrxName("GenericReportProcess"), false), true, processUI);
			
		}else if(Util.isEmpty(p_ViewerType) || p_ViewerType.equals("ZK")) {
			
			PrintInfo info = new PrintInfo(po.toString(), po.get_Table_ID(), po.get_ID(), po.get_UUID());
			MQuery query = new MQuery(m_Table.getTableName());
			query.addRestriction(m_Table.getTableName()+"_ID", MQuery.EQUAL, getRecord_ID());
			ReportEngine re = new ReportEngine(getCtx(), format, query, info);
			preview(re);
			
		}else {
			throw new Exception(Msg.getMsg(Env.getCtx(), "JP_UnexpectedError") + " : Could not find Report Viewer ");
		}
				
		return "OK";
	}	//	doIt
	

	/**
	 * Launch viewer for report
	 * @param re
	 */
	public static void preview(ReportEngine re)
	{
		ReportViewerProvider viewer = getReportViewerProvider();
		viewer.openViewer(re);
	}

	private static IServiceReferenceHolder<ReportViewerProvider> s_reportViewerProviderReference = null;
	
	/**
	 * Get report viewer provider
	 * @return {@link ReportViewerProvider}
	 */
	public static synchronized ReportViewerProvider getReportViewerProvider() {
		ReportViewerProvider viewer = null;
		if (s_reportViewerProviderReference != null) {
			viewer = s_reportViewerProviderReference.getService();
			if (viewer != null)
				return viewer;
		}
		IServiceReferenceHolder<ReportViewerProvider> viewerReference = Service.locator().locate(ReportViewerProvider.class).getServiceReference();
		if (viewerReference != null) {
			s_reportViewerProviderReference = viewerReference;
			viewer = viewerReference.getService();
		}
		return viewer;
	}
}
