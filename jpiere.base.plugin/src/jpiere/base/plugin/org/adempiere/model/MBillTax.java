package jpiere.base.plugin.org.adempiere.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import org.compiere.model.MInvoiceTax;
import org.compiere.model.MTax;
import org.compiere.util.DB;

public class MBillTax extends X_JP_BillTax {

	public MBillTax(Properties ctx, int JP_BillTax_ID, String trxName)
	{
		super(ctx, JP_BillTax_ID, trxName);
	}

	public MBillTax(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}

	public static MBillTax get (MBillLine billLine, MInvoiceTax invoiceTax, int precision, String trxName)
	{
		MBillTax retValue = null;
		MTax mTax = MTax.get(invoiceTax.getC_Tax_ID());

		String sql = "SELECT * FROM JP_BillTax WHERE JP_Bill_ID=? AND C_Tax_ID=? AND IsTaxIncluded=? ";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, trxName);
			pstmt.setInt(1, billLine.getJP_Bill_ID());
			pstmt.setInt(2, invoiceTax.getC_Tax_ID());
			pstmt.setString(3, invoiceTax.isTaxIncluded()? "Y" : "N");
			rs = pstmt.executeQuery();
			if (rs.next())
			{
				retValue = new MBillTax(billLine.getCtx(), rs , trxName);
			}
		}
		catch (Exception e)
		{
			//s_log.log(Level.SEVERE, sql, e);//
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

		if (retValue != null)
		{
			retValue.set_TrxName(trxName);
			retValue.setPrecision(precision);

			return retValue;

		}

		//	Create New
		retValue = new MBillTax(billLine.getCtx(), 0, trxName);
		retValue.set_TrxName(trxName);
		retValue.setClientOrg(billLine);
		retValue.setJP_Bill_ID(billLine.getJP_Bill_ID());
		retValue.setC_Tax_ID(invoiceTax.getC_Tax_ID());
		retValue.setPrecision(precision);
		retValue.setIsTaxIncluded(billLine.getC_Invoice().isTaxIncluded());
		retValue.setIsDocumentLevel(mTax.isDocumentLevel());

		return retValue;
	}	//	get


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
	}	//	setPrecision
}
