package jpiere.base.plugin.org.adempiere.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.model.MCharge;
import org.compiere.model.MRMALine;
import org.compiere.model.MRMATax;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;

/**
 * JPIERE-0369 Tax-included lines and Tax-excluded lines a Tax
 *
 *
 * @author h.hagiwara
 *
 */
public class MRMATaxJP extends MRMATax {

	public MRMATaxJP(Properties ctx, int ignored, String trxName)
	{
		super(ctx, ignored, trxName);
	}

	public MRMATaxJP(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}

	public static MRMATax get (MRMALine line, int precision,
			boolean oldTax, String trxName)
		{
			MRMATaxJP retValue = null;
			if (line == null || line.getM_RMA_ID() == 0)
			{
				s_log.fine("No RMA");
				return null;
			}
			int C_Tax_ID = line.getC_Tax_ID();
			boolean isOldTax = oldTax && line.is_ValueChanged(MRMATax.COLUMNNAME_C_Tax_ID);
			if (isOldTax)
			{
				Object old = line.get_ValueOld(MRMATax.COLUMNNAME_C_Tax_ID);
				if (old == null)
				{
					s_log.fine("No Old Tax");
					return null;
				}
				C_Tax_ID = ((Integer)old).intValue();
			}
			if (C_Tax_ID == 0)
			{
				s_log.fine("No Tax");
				return null;
			}

			String sql = "SELECT * FROM M_RMATax WHERE M_RMA_ID=? AND C_Tax_ID=?";
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				pstmt = DB.prepareStatement (sql, trxName);
				pstmt.setInt (1, line.getM_RMA_ID());
				pstmt.setInt (2, C_Tax_ID);
				rs = pstmt.executeQuery ();
				if (rs.next ())
					retValue = new MRMATaxJP (line.getCtx(), rs, trxName);
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
			retValue = new MRMATaxJP(line.getCtx(), 0, trxName);
			retValue.set_TrxName(trxName);
			retValue.setClientOrg(line);
			retValue.setM_RMA_ID(line.getM_RMA_ID());
			retValue.setC_Tax_ID(line.getC_Tax_ID());
			retValue.setPrecision(precision);
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
		}

		/**	Static Logger	*/
		private static CLogger	s_log	= CLogger.getCLogger (MRMATaxJP.class);
}
