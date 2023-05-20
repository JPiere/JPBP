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

import org.compiere.model.MJournalLine;
import org.compiere.model.MTax;
import org.compiere.util.DB;
import org.compiere.util.Util;


/**
 * JPIERE-0544: GL Journal Tax
 *
 * @author Hideaki Hagiwara
 *
 */
public class MGLJournalTax extends X_JP_GLJournalTax {

	public MGLJournalTax(Properties ctx, int JP_GLJournalTax_ID, String trxName)
	{
		super(ctx, JP_GLJournalTax_ID, trxName);
	}

	public MGLJournalTax(Properties ctx, int JP_GLJournalTax_ID, String trxName, String... virtualColumns) 
	{
		super(ctx, JP_GLJournalTax_ID, trxName, virtualColumns);
	}

	public MGLJournalTax(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}

	static public MGLJournalTax get(MJournalLine line, int precision, boolean oldTax, String trxName)
	{
		MGLJournalTax retValue = null;
		if (line == null || line.getGL_Journal_ID() == 0)
		{
			//s_log.fine("No Contract Content");
			return null;
		}
		
		int C_Tax_ID = line.get_ValueAsInt(MGLJournalTax.COLUMNNAME_C_Tax_ID);
		String JP_SOPOType = line.get_ValueAsString(MGLJournalTax.COLUMNNAME_JP_SOPOType);
		
		if(C_Tax_ID == 0 || Util.isEmpty(JP_SOPOType))
		{
			return null;
		}
		
		if(oldTax)
		{
			if(line.is_ValueChanged(MGLJournalTax.COLUMNNAME_C_Tax_ID))
			{
				Object old_C_Tax_ID = line.get_ValueOld(MGLJournalTax.COLUMNNAME_C_Tax_ID);
				if(old_C_Tax_ID == null)
					return null;
				
				C_Tax_ID = ((Integer)old_C_Tax_ID).intValue();
			}

			if(line.is_ValueChanged(MGLJournalTax.COLUMNNAME_JP_SOPOType))
			{
				Object old_JP_SOPOType = line.get_ValueOld(MGLJournalTax.COLUMNNAME_JP_SOPOType);
				if(old_JP_SOPOType == null)
					return null;
				
				JP_SOPOType = (String)old_JP_SOPOType;
			}	
		}
		
		if (C_Tax_ID == 0)
		{
			return null;
		}

		String sql = "SELECT * FROM JP_GLJournalTax WHERE GL_Journal_ID=? AND JP_SOPOType=? AND C_Tax_ID=? AND AD_Org_ID =?";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (sql, trxName);
			pstmt.setInt (1, line.getGL_Journal_ID());
			pstmt.setString(2, JP_SOPOType);
			pstmt.setInt (3, C_Tax_ID);
			pstmt.setInt (4, line.getAD_Org_ID());
			rs = pstmt.executeQuery ();
			if (rs.next ())
				retValue = new MGLJournalTax (line.getCtx(), rs, trxName);
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
			if (oldTax)
				return null;
		}

		//	Create New
		retValue = new MGLJournalTax(line.getCtx(), 0, trxName);
		retValue.set_TrxName(trxName);
		retValue.setClientOrg(line);
		retValue.setGL_Journal_ID(line.getGL_Journal_ID());
		retValue.setJP_SOPOType(JP_SOPOType);
		retValue.setC_Tax_ID(C_Tax_ID);
		retValue.setPrecision(precision);
		retValue.setIsTaxIncluded(true);

		return retValue;
	}
	
	/** Tax							*/
	@SuppressWarnings("unused")
	private MTax 		m_tax = null;
	/** Cached Precision			*/
	private Integer		m_precision = null;


	/**
	 * 	Get Precision
	 * 	@return Returns the precision or 2
	 */
	@SuppressWarnings("unused")
	private int getPrecision ()
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
