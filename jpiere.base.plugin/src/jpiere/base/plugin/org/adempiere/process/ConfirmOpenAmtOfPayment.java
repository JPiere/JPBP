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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.logging.Level;

import org.compiere.model.MCurrency;
import org.compiere.model.MPayment;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;

/**
 *	JPIERE-0613: Confirm Open Amount of Payment
 *
 *  @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 */
public class ConfirmOpenAmtOfPayment extends SvrProcess {

	private int C_Payment_ID = 0;
	
	@Override
	protected void prepare() 
	{
		C_Payment_ID = getRecord_ID();
	}

	@Override
	protected String doIt() throws Exception 
	{
		if(C_Payment_ID == 0)
			throw new Exception(Msg.getMsg(getCtx(), "FillMandatory") + Msg.getElement(getCtx(), "C_Payment_ID"));
		
		MPayment m_Payment = new MPayment(getCtx(), C_Payment_ID, get_TrxName());
		
		String sql = "SELECT PaymentAvailable(?) FROM DUAL ";
		BigDecimal openAmt = Env.ZERO;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, C_Payment_ID);
			rs = pstmt.executeQuery();
			if (rs.next())
				openAmt = rs.getBigDecimal(1);
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

		if(!m_Payment.isReceipt())
			openAmt = openAmt.negate();
		
		DecimalFormat format = new DecimalFormat();
		format.setMinimumFractionDigits(MCurrency.getStdPrecision(Env.getCtx(), m_Payment.getC_Currency_ID()));
		
		MCurrency m_Currency = MCurrency.get(m_Payment.getC_Currency_ID());
		addLog(Msg.getElement(getCtx(), "OpenAmt") + " " + m_Currency.getCurSymbol() + " " + format.format(openAmt));
		
		return "";
	}

}
