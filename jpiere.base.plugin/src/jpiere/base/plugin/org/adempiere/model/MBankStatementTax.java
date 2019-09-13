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

package jpiere.base.plugin.org.adempiere.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.compiere.model.MTax;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.util.Env;

/**
 *  JPiere Bank Statement Tax Model
 *
 *  JPIERE-0012: Bank Statment Tax
 *
 *  @author  Hideaki Hagiwara（hagiwara@oss-erp.co.jp）
 *
 */

public class MBankStatementTax extends X_JP_BankStatementTax {

	public MBankStatementTax(Properties ctx, int JP_BankStatementTax_ID,
			String trxName) {
		super(ctx, JP_BankStatementTax_ID, trxName);
	}

	public MBankStatementTax(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	/** Tax							*/
	private MTax 		m_tax = null;
	/** Cached Precision			*/
	private Integer		m_precision = null;

	/**
	 * 	Get Precision
	 * 	@return Returns the precision or 2
	 */
	public int getPrecision ()
	{
		if (m_precision == null)
			return 2;
		return m_precision.intValue();
	}	//	getPrecision

	/**
	 * 	Set Precision
	 *	@param precision The precision to set.
	 */
	protected void setPrecision (int precision)
	{
		m_precision = Integer.valueOf(precision);
	}	//	set

	/**
	 * 	Get Tax
	 *	@return tax
	 */
	protected MTax getTax()
	{
		if (m_tax == null)
			m_tax = MTax.get(getCtx(), getC_Tax_ID());
		return m_tax;
	}	//	getTax

	/**
	 * 	String Representation
	 *	@return info
	 */
	public String toString ()
	{
		StringBuilder sb = new StringBuilder ("MBankStatementTax[");
		sb.append("C_BankStatementLine_ID=").append(getC_BankStatementLine_ID())
			.append(",C_Tax_ID=").append(getC_Tax_ID())
			.append(", Base=").append(getTaxBaseAmt()).append(",Tax=").append(getTaxAmt())
			.append ("]");
		return sb.toString ();
	}	//	toString

	public static PO get (Properties ctx, int C_BankStatementLine_ID)
	{
		final String whereClause = "C_BankStatementLine_ID=? AND AD_Client_ID=?";
		PO retValue = new Query(ctx,I_JP_BankStatementTax.Table_Name,whereClause,null)
		.setParameters(C_BankStatementLine_ID,Env.getAD_Client_ID(ctx))
		.firstOnly();
		return retValue;
	}	//	get

}
