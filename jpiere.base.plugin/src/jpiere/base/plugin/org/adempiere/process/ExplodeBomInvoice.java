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

import org.compiere.process.SvrProcess;
import org.compiere.util.Msg;

import jpiere.base.plugin.org.adempiere.model.MInvoiceJP;

/**
 * JPIERE-0295
 * 
 * @author Hideaki Hagiwara
 *
 */
public class ExplodeBomInvoice extends SvrProcess {
	
	int C_Invoice_ID = 0;
	
	@Override
	protected void prepare() 
	{
		C_Invoice_ID = getRecord_ID();
		
	}
	
	@Override
	protected String doIt() throws Exception 
	{

		MInvoiceJP invoice = new MInvoiceJP(getCtx(),C_Invoice_ID, get_TrxName());
		boolean isOK = invoice.explodeBOM();
		
		if(isOK)
			return Msg.getElement(getCtx(), "Success");
		else
			return Msg.getElement(getCtx(), "JP_NoProductExplodeBOM");
			
	}
	
}
