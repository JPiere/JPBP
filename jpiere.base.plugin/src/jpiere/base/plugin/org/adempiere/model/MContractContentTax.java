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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import org.compiere.model.MCharge;
import org.compiere.util.DB;
import org.compiere.util.Env;


/**
* JPIERE-0541: Calculate Contract Content Tax
*
* @author Hideaki Hagiwara
*
*/
public class MContractContentTax extends X_JP_ContractContentTax {

	/** Cached Precision			*/
	private Integer		m_precision = null;

	public MContractContentTax(Properties ctx, int JP_ContractContentTax_ID, String trxName)
	{
		super(ctx, JP_ContractContentTax_ID, trxName);
	}

	public MContractContentTax(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}

	public static MContractContentTax get (MContractLine line, int precision, boolean oldTax, String trxName)
	{
		MContractContentTax retValue = null;
		if (line == null || line.getJP_ContractContent_ID() == 0)
		{
			//s_log.fine("No Contract Content");
			return null;
		}
		int C_Tax_ID = line.getC_Tax_ID();
		boolean isOldTax = oldTax && line.is_ValueChanged(MContractContentTax.COLUMNNAME_C_Tax_ID);
		if (isOldTax)
		{
			Object old = line.get_ValueOld(MContractContentTax.COLUMNNAME_C_Tax_ID);
			if (old == null)
			{
				//s_log.fine("No Old Tax");
				return null;
			}
			C_Tax_ID = ((Integer)old).intValue();
		}
		if (C_Tax_ID == 0)
		{
			if (!line.isDescription())
				;//s_log.fine("No Tax");

			return null;
		}

		String sql = "SELECT * FROM JP_ContractContentTax WHERE JP_ContractContent_ID=? AND C_Tax_ID=?";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (sql, trxName);
			pstmt.setInt (1, line.getJP_ContractContent_ID());
			pstmt.setInt (2, C_Tax_ID);
			rs = pstmt.executeQuery ();
			if (rs.next ())
				retValue = new MContractContentTax (line.getCtx(), rs, trxName);
		}
		catch (Exception e)
		{
			//s_log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}
		if (retValue != null)
		{
			retValue.setPrecision(precision);
			retValue.set_TrxName(trxName);
			//if (s_log.isLoggable(Level.FINE)) s_log.fine("(old=" + oldTax + ") " + retValue);
			return retValue;
		}
		// If the old tax was required and there is no MOrderTax for that
		// return null, and not create another MOrderTax - teo_sarca [ 1583825 ]
		else {
			if (isOldTax)
				return null;
		}

		//	Create New
		retValue = new MContractContentTax(line.getCtx(), 0, trxName);
		retValue.set_TrxName(trxName);
		retValue.setClientOrg(line);
		retValue.setJP_ContractContent_ID(line.getJP_ContractContent_ID());
		retValue.setC_Tax_ID(line.getC_Tax_ID());
		retValue.setPrecision(precision);
		retValue.setIsTaxIncluded(line.getParent().isTaxIncluded());
		//JPIERE-0369:Start
		if(line.getC_Charge_ID() != 0)
		{
			MCharge charge = MCharge.get(Env.getCtx(), line.getC_Charge_ID());
			if(!charge.isSameTax())
			{
				retValue.setIsTaxIncluded(charge.isTaxIncluded());
			}else {
				retValue.setIsTaxIncluded(line.getParent().isTaxIncluded());
			}
		}
		//JPiere-0369:finish
		//if (s_log.isLoggable(Level.FINE)) s_log.fine("(new) " + retValue);
		return retValue;
	}	//	get

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
	}	//	setPrecision
}
