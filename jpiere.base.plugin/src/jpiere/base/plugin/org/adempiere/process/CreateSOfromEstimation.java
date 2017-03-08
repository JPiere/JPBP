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

import jpiere.base.plugin.org.adempiere.model.MEstimation;
import jpiere.base.plugin.org.adempiere.model.MEstimationLine;

import org.adempiere.util.Callback;
import org.adempiere.util.IProcessUI;
import org.compiere.model.MOpportunity;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.PO;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

/**
 * JPIERE-0185 : Create SO from Estimation
 *
 * @author Hideaki Hagiwara
 *
 */
public class CreateSOfromEstimation extends SvrProcess {

	private int			p_JP_Estimation_ID = 0;
	private String		p_DocAction = null;
	private MEstimation estimation = null;
	IProcessUI processUI = null;
	String msg = "";
	
	@Override
	protected void prepare() {

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

		processUI = Env.getProcessUI(getCtx());
		estimation = new MEstimation(getCtx(), p_JP_Estimation_ID, get_TrxName()) ;
		
		if(estimation.getJP_DocTypeSO_ID()==0)
		{
			msg = Msg.getMsg(getCtx(), "FillMandatory") + " : " + Msg.getElement(getCtx(), "JP_DocTypeSO_ID") + System.lineSeparator();
		}
		
		if(estimation.getC_BPartner_ID() == 0)
		{
			msg = msg + Msg.getMsg(getCtx(), "FillMandatory") + " : " + Msg.getElement(getCtx(), "C_BPartner_ID") + System.lineSeparator();
		}
		
		if(estimation.getC_BPartner_Location_ID() == 0)
		{
			msg = msg + Msg.getMsg(getCtx(), "FillMandatory") + " : " + Msg.getElement(getCtx(), "C_BPartner_Location_ID")+ System.lineSeparator();
		}		
		
		if(Util.isEmpty(msg) && processUI != null && estimation.getLink_Order_ID() != 0)
		{
			//Already Sales Order created, Do you want to create Sales Order again?
			processUI.ask(Msg.getMsg(getCtx(), "JP_CreateSOfromEstimationAgain"), new Callback<Boolean>() {

				@Override
				public void onCallback(Boolean result)
				{
					if (result)
					{
						createSO();
					}else{
						;
					}
		        }

			});//FDialog.
		}
		
		if(!Util.isEmpty(msg) && processUI != null)
		{
			//Already Sales Order created, Do you want to create Sales Order again?
			processUI.ask(Msg.getMsg(getCtx(), msg), new Callback<Boolean>() {

				@Override
				public void onCallback(Boolean result)
				{
					;
		        }

			});//FDialog.
		}
		
	}

	private String createSO()
	{		
		MEstimationLine[] eLines = estimation.getLines();

		MOrder order = new MOrder(getCtx(), 0, get_TrxName()) ;
		PO.copyValues(estimation, order);
		order.setAD_Org_ID(estimation.getAD_Org_ID());
		order.setDocumentNo(null);
		order.setC_DocTypeTarget_ID(estimation.getJP_DocTypeSO_ID());
		order.setDocStatus(DocAction.STATUS_Drafted);
		order.setDocAction(DocAction.ACTION_Complete);
		order.saveEx(get_TrxName());

		estimation.setLink_Order_ID(order.getC_Order_ID());
		estimation.saveEx(get_TrxName());

		for(int i = 0; i < eLines.length; i++)
		{
			MOrderLine oLine = new MOrderLine(order);
			PO.copyValues(eLines[i], oLine);
			oLine.setAD_Org_ID(eLines[i].getAD_Org_ID());
			oLine.saveEx(get_TrxName());

		}//for

		order.processIt(p_DocAction);
		order.saveEx(get_TrxName());

		if(estimation.getC_Opportunity_ID()!=0)
		{
			MOpportunity op = new MOpportunity(getCtx(),estimation.getC_Opportunity_ID(), get_TrxName());
			op.setC_Order_ID(order.getC_Order_ID());
			int C_SalesStages[] = PO.getAllIDs("C_SalesStage", "AD_Client_ID = " + getAD_Client_ID() +" AND Probability = 100 AND IsClosed='Y' AND IsWon = 'Y' AND IsActive='Y' ORDER BY Value ASC", get_TrxName());
			if(C_SalesStages.length > 0)
				op.setC_SalesStage_ID(C_SalesStages[0]);
			op.setCloseDate(order.getDateOrdered());
			op.saveEx(get_TrxName());
		}

		return order.getDocumentInfo();
	}

	@Override
	protected String doIt() throws Exception
	{
		if(processUI == null || estimation.getLink_Order_ID() == 0)
		{
			if(estimation.getJP_DocTypeSO_ID()==0)
			{
				throw new Exception(Msg.getMsg(getCtx(), "FillMandatory") + " : " + Msg.getElement(getCtx(), "JP_DocTypeSO_ID"));
			}
			
			if(estimation.getC_BPartner_ID()==0)
			{
				throw new Exception(Msg.getMsg(getCtx(), "FillMandatory") + " : " + Msg.getElement(getCtx(), "C_BPartner_ID"));
			}
			
			
			if(estimation.getC_BPartner_Location_ID()==0)
			{
				throw new Exception(Msg.getMsg(getCtx(), "FillMandatory") + " : " + Msg.getElement(getCtx(), "C_BPartner_Location_ID"));
			}
			
			return createSO();
		}

		return "";
	}

}
