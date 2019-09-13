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
package jpiere.base.plugin.org.adempiere.base;

import java.util.logging.Level;

import org.compiere.model.MBankStatement;
import org.compiere.model.MBankStatementLine;
import org.compiere.model.MClient;
import org.compiere.model.MDocType;
import org.compiere.model.MPayment;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.process.DocAction;
import org.compiere.util.CLogger;
import org.compiere.util.Msg;


/**
 *  JPiere BankStatement Model Validator
 *
 *  JPIERE-0091: Check of Payment and Bank Statement
 *  JPIERE-0217: Complete Bank Statement, When Payment Completed
 *
 *  @author  Hideaki Hagiwara（h.hagiwara@oss-erp.co.jp）
 *
 */
public class JPiereBankStatementModelValidator implements ModelValidator {

	private static CLogger log = CLogger.getCLogger(JPiereBankStatementModelValidator.class);
	private int AD_Client_ID = -1;


	@Override
	public void initialize(ModelValidationEngine engine, MClient client)
	{
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();
//		engine.addModelChange(MBankStatement.Table_Name, this);
		engine.addDocValidate(MBankStatement.Table_Name, this);

		if (log.isLoggable(Level.FINE)) log.fine("Initialize JPiereBankStatementModelValidator");

	}

	@Override
	public int getAD_Client_ID() {

		return AD_Client_ID;
	}

	@Override
	public String login(int AD_Org_ID, int AD_Role_ID, int AD_User_ID)
	{
		return null;
	}

	@Override
	public String modelChange(PO po, int type) throws Exception {

		return null;
	}

	@Override
	public String docValidate(PO po, int timing) {

		//JPIERE-0091
		if(timing == ModelValidator.TIMING_BEFORE_COMPLETE)
		{
			MBankStatement bs = (MBankStatement)po;
			MBankStatementLine[] bsls = bs.getLines(false) ;
			for(int i = 0; i < bsls.length; i++)
			{
				if(bsls[i].getC_Payment_ID() > 0)
				{
					MPayment payment = new MPayment(po.getCtx(), bsls[i].getC_Payment_ID(),  po.get_TrxName());
					String dosStatus =  payment.getDocStatus();
					if(dosStatus.equals(DocAction.STATUS_Voided) || dosStatus.equals(DocAction.STATUS_Reversed))
					{
						//Payment was Voided or Reversed
						return Msg.getElement(po.getCtx(),"Line") + " : " + bsls[i].getLine() + " - " + Msg.getMsg(po.getCtx(), "JP_PaymentVOorRE");
					}

					if(payment.isReceipt())
					{

						if(bsls[i].getTrxAmt().compareTo(payment.getPayAmt()) != 0)
						{
							return Msg.getElement(po.getCtx(),"Line") + " : " + bsls[i].getLine() + " - " + Msg.getMsg(po.getCtx(), "JP_AmtNotSamePaymentAndBSLine");
						}
					}else{
						if(bsls[i].getTrxAmt().compareTo(payment.getPayAmt().negate()) != 0)
						{
							return Msg.getElement(po.getCtx(),"Line") + " : " + bsls[i].getLine() + " - " + Msg.getMsg(po.getCtx(), "JP_AmtNotSamePaymentAndBSLine");
						}
					}
				}//if
			}//for
		}

		//JPIERE-0217:JPBP
		if(timing == ModelValidator.TIMING_AFTER_COMPLETE)
		{
			MBankStatement bs = (MBankStatement)po;
			MBankStatementLine[] bsls = bs.getLines(false) ;
			for(int i = 0; i < bsls.length; i++)
			{
				if(bsls[i].getC_Payment_ID() > 0)
				{
					MPayment payment = new MPayment(po.getCtx(), bsls[i].getC_Payment_ID(),  po.get_TrxName());
					if(payment.getDocStatus().equals(DocAction.STATUS_Voided) || payment.getDocStatus().equals(DocAction.STATUS_Reversed)
							|| payment.getDocStatus().equals(DocAction.STATUS_Invalid) )
					{
						return Msg.getElement(po.getCtx(),"Line") + " : " + bsls[i].getLine() + " - " + Msg.getMsg(po.getCtx(), "JP_NotValidDocStatus");//Not Valid Doc Status

					}else if(!payment.getDocStatus().equals(DocAction.STATUS_Completed) && !payment.getDocStatus().equals(DocAction.STATUS_Closed)){

						MDocType dt = new MDocType(po.getCtx(), payment.getC_DocType_ID(),  po.get_TrxName());
						if(dt.get_ValueAsBoolean("IsReconcileCompleteJP"))
						{
							payment.setDateAcct(bs.getDateAcct());
							if(payment.processIt(DocAction.ACTION_Complete))
								payment.saveEx(po.get_TrxName());
							else
								return Msg.getElement(po.getCtx(),"Line") + " : " + bsls[i].getLine() + " - " + Msg.getMsg(po.getCtx(), "JP_UnexpectedErrorPaymentComplete");

							if(!payment.getDocStatus().equals(DocAction.STATUS_Completed))
								return Msg.getElement(po.getCtx(),"Line") + " : " + bsls[i].getLine() + " - " + Msg.getMsg(po.getCtx(), "JP_UnexpectedErrorPaymentComplete");

						}else{
							return Msg.getElement(po.getCtx(),"Line") + " : " + bsls[i].getLine() + " - " + Msg.getMsg(po.getCtx(), "JP_IncompletePayment");
						}

					}
				}//if(bsls[i].getC_Payment_ID() > 0)

			}//for
		}



		return null;
	}

}
