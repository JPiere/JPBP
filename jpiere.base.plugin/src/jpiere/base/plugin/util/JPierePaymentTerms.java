/******************************************************************************
 * Product: JPiere(ジェイピエール) - JPiere Base Plugin                       *
 * Copyright (C) Hideaki Hagiwara All Rights Reserved.                        *
 * このプログラムはGNU Gneral Public Licens Version2のもと公開しています。    *
 * このプログラムは自由に活用してもらう事を期待して公開していますが、         *
 * いかなる保証もしていません。                                               *
 * 著作権は萩原秀明(h.hagiwara@oss-erp.co.jp)が保持し、サポートサービスは     *
 * 株式会社オープンソース・イーアールピー・ソリューションズで                 *
 * 提供しています。サポートをご希望の際には、                                 *
 * 株式会社オープンソース・イーアールピー・ソリューションズまでご連絡下さい。 *
 * http://www.oss-erp.co.jp/                                                  *
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
