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

import jpiere.base.plugin.org.adempiere.model.MEstimation;
import jpiere.base.plugin.org.adempiere.model.MEstimationLine;
import jpiere.base.plugin.org.adempiere.model.MOrderJP;

/**
 * JPIERE-0185 : Create SO from Estimation
 *
 * @author Hideaki Hagiwara
 *
 */
public class CreateSOfromEstimation extends SvrProcess {

	private int			p_JP_Estimation_ID = 0;
	private int			p_JP_DocTypeSO_ID = 0;
	private String			p_DocAction = null;
	private MEstimation 	estimation = null;
	private IProcessUI 		processUI = null;
	private boolean 		isCreateSO = false;
	private boolean 		isOpenDialog = false;
	private boolean 		isAskAnswer = true;
	private String 			errorMsg = "";
	private String 			returnMsg = "";

	@Override
	protected void prepare()
	{

		p_JP_Estimation_ID = getRecord_ID();

		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null){
				;
			}else if (name.equals("JP_DocTypeSO_ID")){
				p_JP_DocTypeSO_ID = para[i].getParameterAsInt();
			}else if (name.equals("DocAction")){
				p_DocAction = para[i].getParameterAsString();
			}else{
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}//if

		}//for

		processUI = Env.getProcessUI(getCtx());
		estimation = new MEstimation(getCtx(), p_JP_Estimation_ID, get_TrxName()) ;

	}

	@Override
	protected String doIt() throws Exception
	{
		if(p_JP_DocTypeSO_ID==0)
		{
			errorMsg = Msg.getMsg(getCtx(), "FillMandatory") + " : " + Msg.getElement(getCtx(), "JP_DocTypeSO_ID") + System.lineSeparator();
		}

		if(estimation.getC_BPartner_ID() == 0)
		{
			errorMsg = errorMsg + Msg.getMsg(getCtx(), "FillMandatory") + " : " + Msg.getElement(getCtx(), "C_BPartner_ID") + System.lineSeparator();
		}

		if(estimation.getC_BPartner_Location_ID() == 0)
		{
			errorMsg = errorMsg + Msg.getMsg(getCtx(), "FillMandatory") + " : " + Msg.getElement(getCtx(), "C_BPartner_Location_ID")+ System.lineSeparator();
		}

		if(!Util.isEmpty(errorMsg))
		{
			throw new Exception(errorMsg);
		}


		if(processUI != null && estimation.getLink_Order_ID() != 0)
		{
			isOpenDialog = true;
			//Already Sales Order created, Do you want to create Sales Order again?
			processUI.ask("JP_CreateSOfromEstimationAgain", new Callback<Boolean>() {

				@Override
				public void onCallback(Boolean result)
				{
					if (result)
					{
						try {
							returnMsg = createSO();
						}catch (Exception e) {
							returnMsg = e.getMessage();
						}finally {
							isCreateSO = true;
						}

					}else{
						isAskAnswer = false;
					}
		        }

			});//FDialog.

		}else{
			returnMsg = createSO();
			isCreateSO = true;
		}


		while (isOpenDialog && isAskAnswer && !isCreateSO)
		{
			Thread.sleep(1000*2);
		}

		if(!Util.isEmpty(returnMsg))
		{
			throw new Exception(returnMsg);
		}

		if(isCreateSO)
			addBufferLog(0, null, null, returnMsg, MOrder.Table_ID, estimation.getLink_Order_ID());

		return returnMsg;

	}

	private String createSO()
	{
		MEstimationLine[] eLines = estimation.getLines();

		MOrderJP order = new MOrderJP(getCtx(), 0, get_TrxName()) ;
		PO.copyValues(estimation, order);
		order.setAD_Org_ID(estimation.getAD_Org_ID());
		order.setDocumentNo(null);
		order.setC_DocTypeTarget_ID(p_JP_DocTypeSO_ID);
		order.setDocStatus(DocAction.STATUS_Drafted);
		order.setDocAction(DocAction.ACTION_Complete);
		order.setRef_Order_ID(0);
		order.setLink_Order_ID(0);
		order.saveEx(get_TrxName());

		estimation.setLink_Order_ID(order.getC_Order_ID());
		estimation.setJP_DocTypeSO_ID(p_JP_DocTypeSO_ID);
		estimation.saveEx(get_TrxName());

		for(int i = 0; i < eLines.length; i++)
		{
			MOrderLine oLine = new MOrderLine(order);
			PO.copyValues(eLines[i], oLine);
			oLine.setAD_Org_ID(eLines[i].getAD_Org_ID());
			oLine.setRef_OrderLine_ID(0);
			oLine.setLink_OrderLine_ID(0);
			oLine.saveEx(get_TrxName());

			//Don't set for edit(Delete) Order Line.
//			eLines[i].setLink_OrderLine_ID(oLine.getC_OrderLine_ID());
//			eLines[i].saveEx(get_TrxName());

		}//for

		if(!Util.isEmpty(p_DocAction))
		{
			order.processIt(p_DocAction);//CO or PR
			order.saveEx(get_TrxName());
		}

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



}
