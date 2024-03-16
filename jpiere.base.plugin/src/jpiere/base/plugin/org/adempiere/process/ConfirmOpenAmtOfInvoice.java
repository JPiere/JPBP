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

import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.compiere.model.MCurrency;
import org.compiere.model.MInvoice;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
import org.compiere.util.Msg;

/**
 *	JPIERE-0612: Confirm Open Amount of Invoice
 *
 *  @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 */
public class ConfirmOpenAmtOfInvoice extends SvrProcess {

	private int C_Invoice_ID = 0;
	
	@Override
	protected void prepare() 
	{
		C_Invoice_ID = getRecord_ID();
	}

	@Override
	protected String doIt() throws Exception 
	{
		if(C_Invoice_ID == 0)
			throw new Exception(Msg.getMsg(getCtx(), "FillMandatory") + Msg.getElement(getCtx(), "C_Invoice_ID"));
		
		MInvoice m_Invoice = new MInvoice(getCtx(), C_Invoice_ID, get_TrxName());
		BigDecimal openAmt = m_Invoice.getOpenAmt(true, null, true);
		
		DecimalFormat format = new DecimalFormat();
		format.setMinimumFractionDigits(MCurrency.getStdPrecision(Env.getCtx(), m_Invoice.getC_Currency_ID()));
		
		MCurrency m_Currency = MCurrency.get(m_Invoice.getC_Currency_ID());
		addLog(Msg.getElement(getCtx(), "OpenAmt") + " " + m_Currency.getCurSymbol() + " " + format.format(openAmt));
		
		return "";
	}

}
