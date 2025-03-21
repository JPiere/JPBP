package jpiere.base.plugin.org.adempiere.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.model.MCharge;
import org.compiere.model.MOrderLine;
import org.compiere.model.MOrderTax;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;

/**
 * JPIERE-0369 Tax-included lines and Tax-excluded lines a Order
 *
 *
 * @author h.hagiwara
 *
 */
public class MOrderTaxJP extends MOrderTax {

	private static final long serialVersionUID = 8431766493223431760L;

	public MOrderTaxJP(Properties ctx, int ignored, String trxName)
	{
		super(ctx, ignored, trxName);
	}

	public MOrderTaxJP(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}

	public static MOrderTax get (MOrderLine line, int precision,
			boolean oldTax, String trxName)
		{
			MOrderTaxJP retValue = null;
			if (line == null || line.getC_Order_ID() == 0)
			{
				s_log.fine("No Order");
				return null;
			}
			int C_Tax_ID = line.getC_Tax_ID();
			boolean isOldTax = oldTax && line.is_ValueChanged(MOrderTax.COLUMNNAME_C_Tax_ID);
			if (isOldTax)
			{
				Object old = line.get_ValueOld(MOrderTax.COLUMNNAME_C_Tax_ID);
				if (old == null)
				{
					s_log.fine("No Old Tax");
					return null;
				}
				C_Tax_ID = ((Integer)old).intValue();
			}
			if (C_Tax_ID == 0)
			{
				if (!line.isDescription())
					s_log.fine("No Tax");
				return null;
			}

			String sql = "SELECT * FROM C_OrderTax WHERE C_Order_ID=? AND C_Tax_ID=?";
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				pstmt = DB.prepareStatement (sql, trxName);
				pstmt.setInt (1, line.getC_Order_ID());
				pstmt.setInt (2, C_Tax_ID);
				rs = pstmt.executeQuery ();
				if (rs.next ())
					retValue = new MOrderTaxJP (line.getCtx(), rs, trxName);
			}
			catch (Exception e)
			{
				s_log.log(Level.SEVERE, sql, e);
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
				if (s_log.isLoggable(Level.FINE)) s_log.fine("(old=" + oldTax + ") " + retValue);
				return retValue;
			}
			// If the old tax was required and there is no MOrderTax for that
			// return null, and not create another MOrderTax - teo_sarca [ 1583825 ]
			else {
				if (isOldTax)
					return null;
			}

			//	Create New
			retValue = new MOrderTaxJP(line.getCtx(), 0, trxName);
			retValue.set_TrxName(trxName);
			retValue.setClientOrg(line);
			retValue.setC_Order_ID(line.getC_Order_ID());
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
			if (s_log.isLoggable(Level.FINE)) s_log.fine("(new) " + retValue);
			return retValue;
		}	//	get

		/**	Static Logger	*/
		private static CLogger	s_log	= CLogger.getCLogger (MOrderTaxJP.class);
}
