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
package jpiere.base.plugin.util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Properties;

import org.compiere.model.MPaymentTerm;
import org.compiere.util.DB;
import org.compiere.util.Env;

public class JPierePaymentTerms {

	public static MPaymentTerm[] getPaymentTerms(Properties ctx, int C_PaymentTerm_ID)
	{

		ArrayList<MPaymentTerm> list = new ArrayList<MPaymentTerm>();
		String sql = "SELECT * FROM C_PaymentTerm"
				+ " WHERE JP_PaymentTerms_ID=? AND IsActive='Y'"
				+ " ORDER BY FixMonthCutoff";

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, null);
			pstmt.setInt(1, C_PaymentTerm_ID);
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add(new MPaymentTerm (ctx, rs, null));
		}
		catch (Exception e)
		{
//			slog.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

		MPaymentTerm[] paymentTerms = new MPaymentTerm[list.size()];
		list.toArray(paymentTerms);
		return paymentTerms;
	}



	public static MPaymentTerm getPaymentTerm(Properties ctx, int C_PaymentTerm_ID, Timestamp date)
	{
		MPaymentTerm[] paymentTerms = JPierePaymentTerms.getPaymentTerms(Env.getCtx(), C_PaymentTerm_ID);
		String dateString = new SimpleDateFormat("dd").format(date);
		Integer dateInt = new Integer(dateString);

		for(int i = 0; i < paymentTerms.length; i++)
		{
			int FixMonthCutoff = paymentTerms[i].getFixMonthCutoff();
			if(dateInt.intValue() <= FixMonthCutoff)
			{
				return paymentTerms[i];
			}
		}

		return null;
	}

}
