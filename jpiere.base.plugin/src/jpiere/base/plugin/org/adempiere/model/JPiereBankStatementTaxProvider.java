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
package jpiere.base.plugin.org.adempiere.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.compiere.model.I_C_TaxProvider;
import org.compiere.model.MBPartner;
import org.compiere.model.MBankStatementLine;
import org.compiere.model.MOrderTax;
import org.compiere.model.MRMALine;
import org.compiere.model.MTax;
import org.compiere.model.MTaxProvider;
import org.compiere.util.CCache;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;

/**
 *  JPiere Bank Statement Tax Provider
 *
 *  @author  Hideaki Hagiwara（萩原 秀明:h.hagiwara@oss-erp.co.jp）
 *  @version  $Id: JPiereBankStatementTaxProvider.java,v 1.0 2014/08/20
 *
 */
public class JPiereBankStatementTaxProvider {

	/**	Logger							*/
	protected transient CLogger	log = CLogger.getCLogger (getClass());

	/**	Cache						*/
	private static CCache<Integer,MBPartner> s_cache	= new CCache<Integer,MBPartner>("C_BPartner", 40, 5);	//	5 minutes





	/*********************************************************************************************************
	 * Bank Statement
	 *
	 */

	public boolean updateHeaderTax(MTaxProvider provider, MBankStatementLine line){

		return true;

	}

	public boolean updateBankStatementTax(MTaxProvider provider, MBankStatementLine line) {
		MBankStatementTax tax = getMBankStatementTax (line, line.getC_Currency().getStdPrecision(), false, line.get_TrxName());
		if (tax != null) {
			if (!calculateTaxFromBankStatementLine(line,tax))
				return false;
			if (tax.getTaxAmt().signum() != 0) {
				if (!tax.save(line.get_TrxName()))
					return false;
			}else {
				if(line.isProcessed() && !line.is_ValueChanged("Processed")){
					if (!tax.is_new() && !tax.delete(true, tax.get_TrxName()))
						return false;
				}else{
					if (!tax.is_new() && !tax.delete(false, tax.get_TrxName()))
						return false;
				}
			}
		}


    	return true;
	}


	public boolean recalculateTax(MTaxProvider provider, MBankStatementLine line, boolean newRecord)
	{
		if (!newRecord && line.is_ValueChanged(MRMALine.COLUMNNAME_C_Tax_ID) && !line.getParent().isProcessed())
		{
			if (!updateBankStatementTax(line, true))
				return false;
		}

		if(!updateBankStatementTax(provider, line))
			return false;

        return updateHeaderTax(provider, line);
	}

	private boolean updateBankStatementTax(MBankStatementLine  line, boolean oldTax){
		MBankStatementTax tax = getMBankStatementTax (line, line.getC_Currency().getStdPrecision(), oldTax, line.get_TrxName());

		try{
			if (!tax.is_new() && !tax.delete(false, tax.get_TrxName()))
				return false;
		}catch(Exception e){
			return true;
		}

	    return true;
	}

	/**
	 * Calculate Tax from Line
	 *
	 * This is a special specification of Bank Statement Tax.
	 *
	 * 1:Line Level tax calculation only
	 * 2:Tax Included calculation only
	 *
	 */

	private boolean calculateTaxFromBankStatementLine (MBankStatementLine line, MBankStatementTax m_bankStatementTax)
	{
		BigDecimal taxBaseAmt = line.getChargeAmt().abs();
		BigDecimal taxAmt = Env.ZERO;

		MTax tax = MTax.get(m_bankStatementTax.getCtx(), m_bankStatementTax.getC_Tax_ID());

		boolean isSOTrx = false;
		if(line.getChargeAmt().compareTo(Env.ZERO) > 0){
			isSOTrx = true;
		}
		m_bankStatementTax.setIsSOTrx(isSOTrx);

		RoundingMode roundingMode = JPiereBankStatementTaxProvider.getRoundingMode(line.getC_BPartner_ID(), isSOTrx, tax.getC_TaxProvider());
		taxAmt = calculateTax(tax, taxBaseAmt, true, line.getC_Currency().getStdPrecision(), roundingMode);
		m_bankStatementTax.setTaxAmt(taxAmt);
		m_bankStatementTax.setTaxBaseAmt (taxBaseAmt.subtract(taxAmt));

		return true;
	}	//	calculateTaxFromLines


	private MBankStatementTax getMBankStatementTax (MBankStatementLine line, int precision,
			boolean oldTax, String trxName)
		{
			MBankStatementTax retValue = null;
			if (line == null || line.getC_BankStatement_ID() == 0)
			{
				return null;
			}
			int C_Tax_ID = ((Integer)line.get_Value("C_Tax_ID")).intValue();
			boolean isOldTax = oldTax && line.is_ValueChanged(MOrderTax.COLUMNNAME_C_Tax_ID);
			if (isOldTax)
			{
				Object old = line.get_ValueOld(MOrderTax.COLUMNNAME_C_Tax_ID);
				if (old == null)
				{
					return null;
				}
				C_Tax_ID = ((Integer)old).intValue();
			}
			if (C_Tax_ID == 0)
			{
				return null;
			}

			String sql = "SELECT * FROM JP_BankStatementTax WHERE C_BankStatementLine_ID=? AND C_Tax_ID=?";
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				pstmt = DB.prepareStatement (sql, trxName);
				pstmt.setInt (1, line.getC_BankStatementLine_ID());
				pstmt.setInt (2, C_Tax_ID);
				rs = pstmt.executeQuery ();
				if (rs.next ())
					retValue = new MBankStatementTax  (line.getCtx(), rs, trxName);
			}
			catch (Exception e)
			{

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
				return retValue;
			}
			// If the old tax was required and there is no MOrderTax for that
			// return null, and not create another MOrderTax - teo_sarca [ 1583825 ]
			else {
				if (isOldTax)
					return null;
			}

			//	Create New
			retValue = new MBankStatementTax(line.getCtx(), 0, trxName);
			retValue.set_TrxName(trxName);
			retValue.setAD_Org_ID(line.getAD_Org_ID());
			retValue.setC_BankStatement_ID(line.getC_BankStatement_ID());
			retValue.setC_BankStatementLine_ID(line.getC_BankStatementLine_ID());
			retValue.setC_Tax_ID(C_Tax_ID);
			retValue.setPrecision(precision);
			retValue.setIsTaxIncluded(true);// true

			return retValue;
		}	//	get



	/*********************************************************************************************************
	 * Other Method
	 *
	 */


	public static RoundingMode getRoundingMode(int C_BPartner_ID, boolean isSOTrx, I_C_TaxProvider provider)
	{

		RoundingMode roundingMode = null;

		if(C_BPartner_ID != 0)
		{
			Integer key = new Integer (C_BPartner_ID);
			MBPartner bp = (MBPartner) s_cache.get (key);
			if (bp == null)
				bp = MBPartner.get(Env.getCtx(), C_BPartner_ID);
			if (bp.get_ID () != 0)
				s_cache.put (key, bp);

			if(isSOTrx){
				Object SO_TaxRounding = bp.get_Value("SO_TaxRounding");
				if(SO_TaxRounding != null)
					roundingMode = RoundingMode.valueOf(new Integer(SO_TaxRounding.toString()).intValue());
			}else{
				Object PO_TaxRounding = bp.get_Value("PO_TaxRounding");
				if(PO_TaxRounding != null)
					roundingMode = RoundingMode.valueOf(new Integer(PO_TaxRounding.toString()).intValue());
			}
		}

		if(roundingMode == null){

			if(provider != null && provider.getAccount() != null){
				String roundingModeString = provider.getAccount();
				if(roundingModeString.equals("UP"))
					return RoundingMode.UP;
				else if(roundingModeString.equals("DOWN"))
					return RoundingMode.DOWN;
				else if(roundingModeString.equals("CEILING"))
					return RoundingMode.CEILING;
				else if(roundingModeString.equals("FLOOR"))
					return RoundingMode.FLOOR;
				else if(roundingModeString.equals("HALF_UP"))
					return RoundingMode.HALF_UP;
				else if(roundingModeString.equals("HALF_DOWN"))
					return RoundingMode.HALF_DOWN;
				else if(roundingModeString.equals("HALF_EVEN"))
					return RoundingMode.HALF_EVEN;
				else if(roundingModeString.equals("UNNECESSARY"))
					return RoundingMode.UNNECESSARY;
				else
					return RoundingMode.DOWN;
			}

			roundingMode = RoundingMode.DOWN;
		}

		return roundingMode;
	}


	public static BigDecimal calculateTax (MTax m_tax, BigDecimal amount, boolean taxIncluded, int scale, RoundingMode roundingMode)
	{
		//	Null Tax
		if (m_tax.isZeroTax())
			return Env.ZERO;


		BigDecimal multiplier = m_tax.getRate().divide(Env.ONEHUNDRED, 12, RoundingMode.HALF_UP);

		BigDecimal tax = null;
		if (!taxIncluded)	//	$100 * 6 / 100 == $6 == $100 * 0.06
		{
			tax = amount.multiply (multiplier);
		}
		else			//	$106 - ($106 / (100+6)/100) == $6 == $106 - ($106/1.06)
		{
			multiplier = multiplier.add(Env.ONE);
			BigDecimal base = amount.divide(multiplier, 12, RoundingMode.HALF_UP);
			tax = amount.subtract(base);
		}
		BigDecimal finalTax = tax.setScale(scale, roundingMode);

		return finalTax;
	}	//	calculateTax
}
