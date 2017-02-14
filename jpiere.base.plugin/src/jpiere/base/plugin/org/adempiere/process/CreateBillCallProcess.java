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


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.adempiere.base.Service;
import org.compiere.model.MBPartner;
import org.compiere.model.MInvoice;
import org.compiere.model.MPaymentTerm;
import org.compiere.model.Query;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;

import jpiere.base.plugin.org.adempiere.model.MBillSchema;

/**
 * JPIERE-0107 Create Bill by Process
 * JPIERE-0275 Create Bill by Manual
 * JPIERE-0276 Create Payment Request by Manual
 * JPIERE-0278 Craate Payment Request by Process
 *
 *  @author Hideaki Hagiwara
 *
 *
 *	modified by Hiroshi Iwama
 */
public class CreateBillCallProcess extends SvrProcess
{
	/**True if called from info window, and False otherwise.*/
	boolean isCalledFromInfoWindow = false;

	/**Invoices selected in the info window.*/
	ArrayList<MInvoice> p_selectedInvoices = null;

	int p_JP_BillSchema_ID = 0;
	
	int p_C_PaymentTerm_ID = 0;
	
	boolean p_IsSOTrx = false;
	
	/**
	 *  Prepare.
	 */
	protected void prepare()
	{

		String whereClause = "EXISTS (SELECT T_Selection_ID FROM T_Selection WHERE T_Selection.AD_PInstance_ID=? " +
				"AND T_Selection.T_Selection_ID = C_Invoice.C_Invoice_ID)";

		Collection<MInvoice> selectedInvoices = new Query(getCtx(), "C_Invoice", whereClause, get_TrxName())
						.setClient_ID()
						.setParameters(new Object[]{getAD_PInstance_ID()})
						.list();

		 //If invoices are selected, then this is considered to be called from info window and from process otherwise.
		if(selectedInvoices.size() != 0)
		{
			isCalledFromInfoWindow = true;
			p_selectedInvoices = (ArrayList<MInvoice>) selectedInvoices;
		}

	}//	prepare

	/**
	 *  Perform process.
	 *  @return Message (variables are parsed)
	 *  @throws Exception if not successful
	 */
	protected String doIt() throws Exception
	{
		if(isCalledFromInfoWindow)
		{			
			int C_BPartner_ID = p_selectedInvoices.get(0).getC_BPartner_ID();
			MBPartner bPartner = new MBPartner(getCtx(), C_BPartner_ID, get_TrxName());
			ProcessInfoParameter[] para =  getParameter();
			for(int i = 0; i < para.length; i++)
			{
				String name = para[i].getParameterName();
				if(para[i].getParameter() == null)
				{
					;
				}else if(name.equals("IsSOTrx")){
					p_IsSOTrx = para[i].getParameterAsString().equals("Y");
				}
				
			}//for
			
			if(p_IsSOTrx)
				p_JP_BillSchema_ID = bPartner.get_ValueAsInt("JP_BillSchema_ID");
			else 
				p_JP_BillSchema_ID = bPartner.get_ValueAsInt("JP_BillSchemaPO_ID");
			
			p_C_PaymentTerm_ID = p_selectedInvoices.get(0).getC_PaymentTerm_ID();
			
		}else{//Called From Process
			
			ProcessInfoParameter[] para =  getParameter();
			for(int i = 0; i < para.length; i++)
			{
				String name = para[i].getParameterName();
				if(para[i].getParameter() == null)
				{
					;
				}else if(name.equals("IsSOTrx")){
					p_IsSOTrx = para[i].getParameterAsString().equals("Y");
				}else if(name.equals("JP_BillSchema_ID")){
					p_JP_BillSchema_ID = para[i].getParameterAsInt();
				}else if(name.equals("C_PaymentTerm_ID")){
					p_C_PaymentTerm_ID = para[i].getParameterAsInt();
				}
				
			}//for
		}
		
		
		MBillSchema billSchema = new MBillSchema(getCtx(),p_JP_BillSchema_ID, get_TrxName());
		MPaymentTerm paymentTerm = new MPaymentTerm(getCtx(),p_C_PaymentTerm_ID, get_TrxName());
		String createBillMessage = null;
		List<I_CreateBillFactory> factories = Service.locator().list(I_CreateBillFactory.class).getServices();
		if (factories != null)
		{
			
			for(I_CreateBillFactory factory : factories)
			{
				I_CreateBill createBill = factory.getCreateBill(billSchema.getClassname());
				if(createBill != null)
				{
					createBillMessage = createBill.createBills(getCtx(), getAD_PInstance_ID(), this, getParameter()
														, billSchema, paymentTerm, p_IsSOTrx, isCalledFromInfoWindow, get_TrxName());		
					break;
				}
			}//for
		}

		return createBillMessage;

	}	//	doIt
	
}
