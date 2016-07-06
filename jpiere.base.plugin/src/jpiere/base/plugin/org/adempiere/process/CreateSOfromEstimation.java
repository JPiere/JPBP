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

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;

import jpiere.base.plugin.org.adempiere.model.MEstimation;
import jpiere.base.plugin.org.adempiere.model.MEstimationLine;

import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.PO;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Msg;

/**
 * JPIERE-0185 : Create SO from Estimation
 *
 * @author Hideaki Hagiwara
 *
 */
public class CreateSOfromEstimation extends SvrProcess {

	private int 		p_AD_Client_ID = 0;
	private int 		p_AD_User_ID = 0;
	private int			p_JP_Estimation_ID = 0;
	private String		p_DocAction = null;

	@Override
	protected void prepare() {

		p_AD_Client_ID =getProcessInfo().getAD_Client_ID();
		p_AD_User_ID =getProcessInfo().getAD_User_ID();
		p_JP_Estimation_ID = getRecord_ID();
		
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null){
				;
			}else if (name.equals("DocAction")){
				p_DocAction = para[i].getParameterAsString();
			}else{
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}//if

		}//for
	}

	@Override
	protected String doIt() throws Exception 
	{

		MEstimation estimation = new MEstimation(getCtx(), p_JP_Estimation_ID, get_TrxName()) ;
		MEstimationLine[] eLines = estimation.getLines();
		
		MOrder order = new MOrder(getCtx(), 0, get_TrxName()) ;
		PO.copyValues(estimation, order);
		order.setDocumentNo(null);
		order.setC_DocTypeTarget_ID(estimation.getC_DocType_ID());
		order.setDocStatus(DocAction.STATUS_Drafted);
		order.setDocAction(DocAction.ACTION_Complete);		
		order.saveEx(get_TrxName());
		
		estimation.setLink_Order_ID(order.getC_Order_ID());
		estimation.saveEx(get_TrxName());
		
		for(int i = 0; i < eLines.length; i++)
		{
			MOrderLine oLine = new MOrderLine(order);
			PO.copyValues(eLines[i], oLine);			
			oLine.saveEx(get_TrxName());
			
		}//for
		
		order.processIt(p_DocAction);
		order.saveEx(get_TrxName());
		
		return "OK";
	}

}
