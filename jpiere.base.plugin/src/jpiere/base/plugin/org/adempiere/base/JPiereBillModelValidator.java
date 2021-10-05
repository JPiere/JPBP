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

import java.sql.Timestamp;

import org.compiere.model.MClient;
import org.compiere.model.MPayment;
import org.compiere.model.MSysConfig;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;

import jpiere.base.plugin.org.adempiere.model.MBill;


/**
*
* JPIERE-0507 : JPiere Bill Model Validator
*
*
*
* @author h.hagiwara
*
*/
public class JPiereBillModelValidator implements ModelValidator {

	@SuppressWarnings("unused")
	private static CLogger log = CLogger.getCLogger(JPiereBillModelValidator.class);
	private int AD_Client_ID = -1;

	@Override
	public void initialize(ModelValidationEngine engine, MClient client)
	{
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();
		engine.addModelChange(MBill.Table_Name, this);

	}

	@Override
	public int getAD_Client_ID()
	{
		return AD_Client_ID;
	}

	@Override
	public String login(int AD_Org_ID, int AD_Role_ID, int AD_User_ID)
	{
		return null;
	}


	public static final String JP_UPDATE_LAST_BILL_INFO = "JP_UPDATE_LAST_BILL_INFO";
	private static final String JP_UPDATE_LAST_BILL_INFO_BOTH = "BOTH";
	private static final String JP_UPDATE_LAST_BILL_INFO_AR = "AR";
	private static final String JP_UPDATE_LAST_BILL_INFO_AP = "AP";

	@Override
	public String modelChange(PO po, int type) throws Exception
	{

		//JPIERE-0507
		if(type == TYPE_BEFORE_NEW)
		{
			if(po instanceof MBill)
			{
				MBill bill = (MBill)po;

				String updateLastBillInfo = MSysConfig.getValue(JP_UPDATE_LAST_BILL_INFO, JP_UPDATE_LAST_BILL_INFO_BOTH, bill.getAD_Client_ID(), bill.getAD_Org_ID());

				if(JP_UPDATE_LAST_BILL_INFO_BOTH.equals(updateLastBillInfo)
						|| (bill.isSOTrx() && JP_UPDATE_LAST_BILL_INFO_AR.equals(updateLastBillInfo))
						|| (!bill.isSOTrx() && JP_UPDATE_LAST_BILL_INFO_AP.equals(updateLastBillInfo)) )
				{

					//Set Last Bill and Last Bill Amount
					MBill lastBill = null;
					if(bill.getJP_LastBill_ID() == 0 && bill.getJPLastBillAmt().compareTo(Env.ZERO) == 0)
					{


							String whereClause = " C_BPartner_ID =? AND C_DocType_ID = ? AND AD_Org_ID =? AND C_PaymentTerm_ID = ? "
													+ " AND C_Currency_ID = ? AND DocStatus in ('CO','CL') AND JPDateBilled < ? AND IsSOTrx=? ";

							String orderClause = " JPDateBilled DESC, JP_Bill_ID DESC";

							lastBill = new Query(po.getCtx(), MBill.Table_Name, whereClause, po.get_TrxName())
									.setParameters(bill.getC_BPartner_ID(), bill.getC_DocType_ID(), bill.getAD_Org_ID(), bill.getC_PaymentTerm_ID()
													, bill.getC_Currency_ID(), bill.getJPDateBilled(), bill.isSOTrx()? "Y" : "N" )
									.setOrderBy(orderClause)
									.first();

							if(lastBill != null)
							{
								bill.setJP_LastBill_ID(lastBill.getJP_Bill_ID());
								bill.setJPLastBillAmt(lastBill.getJPBillAmt());
							}
					}//if


					//Set Last Income payment and Pay Amount
					MPayment lastPayment = null;
					if(bill.getJP_LastBill_ID() != 0 && bill.getC_Payment_ID() == 0 && bill.getJPLastPayAmt().compareTo(Env.ZERO) == 0)
					{
						String whereClause = " JP_Bill_ID =? AND DocStatus in ('CO','CL') AND IsReceipt = ? AND PayAmt > 0 ";
						String orderClause = " DateTrx DESC, C_Payment_ID DESC";

						lastPayment = new Query(po.getCtx(), MPayment.Table_Name, whereClause, po.get_TrxName())
								.setParameters(lastBill.getJP_Bill_ID(), bill.isSOTrx()? "Y" : "N" )
								.setOrderBy(orderClause)
								.first();

						if(lastPayment != null)
						{
							bill.setC_Payment_ID(lastPayment.getC_Payment_ID());
							bill.setJPLastPayAmt(lastPayment.getPayAmt());
						}
					}

					bill.setJPCarriedForwardAmt(bill.getJPLastBillAmt().subtract(bill.getJPLastPayAmt()));
					bill.setJPBillAmt(bill.getJPCarriedForwardAmt().add(bill.getOpenAmt()));


					//Set Promised Pay Date
					if(bill.getJP_PromisedPayDate() == null && bill.getC_PaymentTerm_ID() > 0)
					{
						String sql = "SELECT JP_PaymentTermDueDate(?,?) FROM DUAL";
						Timestamp dueDate = DB.getSQLValueTS(po.get_TrxName(), sql, bill.getC_PaymentTerm_ID(), bill.getDateAcct());
						bill.setJP_PromisedPayDate(dueDate);
					}


					//Set Bank Account
					if(bill.getC_BankAccount_ID() == 0)
					{
						String sql = null;

						if(bill.isSOTrx())
						{
							sql = "SELECT bs.C_BankAccount_ID FROM JP_BillSchema bs INNER JOIN C_BPartner bp ON (bs.JP_BillSchema_ID = bp.JP_BillSchema_ID)"
											+ " WHERE bp.C_BPartner_ID = ?";
						}else {

							sql = "SELECT bs.C_BankAccount_ID FROM JP_BillSchema bs INNER JOIN C_BPartner bp ON (bs.JP_BillSchema_ID = bp.JP_BillSchemaPO_ID)"
									+ " WHERE bp.C_BPartner_ID = ?";
						}

						int C_BankAccount_ID  = DB.getSQLValue(po.get_TrxName(), sql, bill.getC_BPartner_ID());
						bill.setC_BankAccount_ID(C_BankAccount_ID);

						if(bill.getC_BankAccount_ID() == 0 && lastPayment != null)
						{
							bill.setC_BankAccount_ID(lastPayment.getC_BankAccount_ID());
						}
					}

				}

			}//if(po instanceof MBill)
		}

		return null;
	}

	@Override
	public String docValidate(PO po, int timing)
	{

		return null;
	}

}
