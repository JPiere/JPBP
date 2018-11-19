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
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.util.CCache;
import org.compiere.util.DB;


/** JPIERE-0363
*
* @author Hideaki Hagiwara
*
*/
public class MContractAcct extends X_JP_Contract_Acct {

	public MContractAcct(Properties ctx, int JP_Contract_Acct_ID, String trxName)
	{
		super(ctx, JP_Contract_Acct_ID, trxName);
	}

	public MContractAcct(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}

	@Override
	protected boolean beforeSave(boolean newRecord)
	{
		//Check Base Doc Type
		if(newRecord || is_ValueChanged(MContractAcct.COLUMNNAME_DocBaseType))
		{
			if(getDocBaseType().equals("SOO")
					|| getDocBaseType().equals("ARI"))
			{
				setIsSOTrx(true);

			}else if(getDocBaseType().equals("POO")
					|| getDocBaseType().equals("API"))
			{
				setIsSOTrx(false);
			}

		}


		//Check IsPostingContractAcctJP and IsPostingRecognitionDocJP
		if(newRecord ||( is_ValueChanged(MContractAcct.COLUMNNAME_IsPostingContractAcctJP)
						|| is_ValueChanged(MContractAcct.COLUMNNAME_DocBaseType) ))
		{
			if(getDocBaseType().equals(MContractAcct.DOCBASETYPE_ARInvoice)
					|| getDocBaseType().equals(MContractAcct.DOCBASETYPE_APInvoice)
					|| !isPostingContractAcctJP())
			{
				setIsPostingRecognitionDocJP(false);
				setDocAction(null);
				setIsSplitWhenDifferenceJP(false);
				setJP_RecogToInvoicePolicy(null);
			}
		}

		if(newRecord || is_ValueChanged(MContractAcct.COLUMNNAME_IsPostingRecognitionDocJP))
		{
			if(!isPostingRecognitionDocJP())
			{
				setDocAction(null);
				setIsSplitWhenDifferenceJP(false);
				setJP_RecogToInvoicePolicy(null);
			}
		}

		return true;
	}


	/**	Cache				*/
	private static CCache<Integer, MContractAcct>	s_cache = new CCache<Integer, MContractAcct>(Table_Name, 20);

	/**
	 * 	Get from Cache
	 *	@param ctx context
	 *	@param JP_Contract_Acct_ID id
	 *	@return Contract Acct
	 */
	public static MContractAcct get (Properties ctx, int JP_Contract_Acct_ID)
	{
		Integer ii = Integer.valueOf(JP_Contract_Acct_ID);
		MContractAcct retValue = (MContractAcct)s_cache.get(ii);
		if (retValue != null)
			return retValue;
		retValue = new MContractAcct (ctx, JP_Contract_Acct_ID, null);
		if (retValue.get_ID () != 0)
			s_cache.put (JP_Contract_Acct_ID, retValue);
		return retValue;
	}	//	get



	HashMap<Integer, MContractBPAcct> contractBPAcct = null;

	public MContractBPAcct getContractBPAcct(int C_AcctSchema_ID, boolean reload)
	{
		if (reload || contractBPAcct == null || contractBPAcct.size() == 0)
			getAllContractBPAccts (reload);

		if(contractBPAcct == null || contractBPAcct.size() == 0)
			return null;

		if(contractBPAcct.containsKey(C_AcctSchema_ID))
		{
			return contractBPAcct.get(C_AcctSchema_ID);
		}else{
			return null;
		}
	}

	public HashMap<Integer, MContractBPAcct>  getAllContractBPAccts (boolean reload)
	{
		if (reload || contractBPAcct == null || contractBPAcct.size() == 0)
			;
		else
			return contractBPAcct;

		contractBPAcct = new HashMap<Integer, MContractBPAcct>();
		final String sql = "SELECT * FROM JP_Contract_BP_Acct WHERE JP_Contract_Acct_ID=? AND IsActive='Y'";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, getJP_Contract_Acct_ID());
			rs = pstmt.executeQuery();
			while (rs.next())
			{
				MContractBPAcct bpAcct =  new MContractBPAcct (getCtx(), rs, get_TrxName());
				contractBPAcct.put(bpAcct.getC_AcctSchema_ID(), bpAcct);
			}
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}

		return contractBPAcct;
	}	//	getContractBPAcct


	//M_Product_Category_ID and C_AcctSchema_ID
	HashMap<Integer, HashMap<Integer, MContractProductAcct>> contractProductAcct = null;


	public MContractProductAcct getContractProductAcct(int M_Product_Category_ID,  int C_AcctSchema_ID, boolean reload)
	{
		if (reload || contractProductAcct == null || contractProductAcct.size() == 0)
			getAllContractProductAccts (reload);

		if(contractProductAcct == null || contractProductAcct.size() == 0)
			return null;

		if(contractProductAcct.containsKey(M_Product_Category_ID))
		{
			if(contractProductAcct.get(M_Product_Category_ID).containsKey(C_AcctSchema_ID))
			{
				return contractProductAcct.get(M_Product_Category_ID).get(C_AcctSchema_ID);
			}else{
				return null;
			}

		}else{
			return null;
		}
	}


	public HashMap<Integer, HashMap<Integer, MContractProductAcct>> getAllContractProductAccts (boolean reload)
	{
		if (reload || contractProductAcct == null || contractProductAcct.size() == 0)
			;
		else
			return contractProductAcct;

		contractProductAcct = new HashMap<Integer, HashMap<Integer, MContractProductAcct>>();
		final String sql = "SELECT * FROM JP_Contract_Product_Acct WHERE JP_Contract_Acct_ID=? AND IsActive='Y'";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, getJP_Contract_Acct_ID());
			rs = pstmt.executeQuery();
			while (rs.next())
			{
				MContractProductAcct productAcct =  new MContractProductAcct (getCtx(), rs, get_TrxName());
				HashMap<Integer, MContractProductAcct> innerMap = new HashMap<Integer, MContractProductAcct>();
				innerMap.put(productAcct.getC_AcctSchema_ID(), productAcct);
				contractProductAcct.put(productAcct.getM_Product_Category_ID(), innerMap);
			}
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}

		return contractProductAcct;
	}



	//C_Tax_ID and C_AcctSchema_ID
	HashMap<Integer, HashMap<Integer, MContractTaxAcct>> contractTaxAcct = null;


	public MContractTaxAcct getContracTaxAcct(int C_Tax_ID,  int C_AcctSchema_ID, boolean reload)
	{
		if (reload || contractTaxAcct == null || contractTaxAcct.size() == 0)
			getAllContractTaxAccts (reload);

		if(contractTaxAcct == null || contractTaxAcct.size() == 0)
			return null;

		if(contractTaxAcct.containsKey(C_Tax_ID))
		{
			if(contractTaxAcct.get(C_Tax_ID).containsKey(C_AcctSchema_ID))
			{
				return contractTaxAcct.get(C_Tax_ID).get(C_AcctSchema_ID);
			}else{
				return null;
			}

		}else{
			return null;
		}
	}


	public HashMap<Integer, HashMap<Integer, MContractTaxAcct>> getAllContractTaxAccts (boolean reload)
	{
		if (reload || contractTaxAcct == null || contractTaxAcct.size() == 0)
			;
		else
			return contractTaxAcct;

		contractTaxAcct = new HashMap<Integer, HashMap<Integer, MContractTaxAcct>>();
		final String sql = "SELECT * FROM JP_Contract_Tax_Acct WHERE JP_Contract_Acct_ID=? AND IsActive='Y'";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, getJP_Contract_Acct_ID());
			rs = pstmt.executeQuery();
			while (rs.next())
			{
				MContractTaxAcct taxAcct =  new MContractTaxAcct (getCtx(), rs, get_TrxName());
				HashMap<Integer, MContractTaxAcct> innerMap = new HashMap<Integer, MContractTaxAcct>();
				innerMap.put(taxAcct.getC_AcctSchema_ID(), taxAcct);
				contractTaxAcct.put(taxAcct.getC_Tax_ID(), innerMap);
			}
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}

		return contractTaxAcct;
	}


	//C_Charge_ID and C_AcctSchema_ID
	HashMap<Integer, HashMap<Integer, MContractChargeAcct>> contractChargeAcct = null;


	public MContractChargeAcct getContracChargeAcct(int C_Charge_ID,  int C_AcctSchema_ID, boolean reload)
	{
		if (reload || contractChargeAcct == null || contractChargeAcct.size() == 0)
			getAllContractChargeAccts (reload);

		if(contractChargeAcct == null || contractChargeAcct.size() == 0)
			return null;

		if(contractChargeAcct.containsKey(C_Charge_ID))
		{
			if(contractChargeAcct.get(C_Charge_ID).containsKey(C_AcctSchema_ID))
			{
				return contractChargeAcct.get(C_Charge_ID).get(C_AcctSchema_ID);
			}else{
				return null;
			}

		}else{
			return null;
		}
	}


	public HashMap<Integer, HashMap<Integer, MContractChargeAcct>> getAllContractChargeAccts (boolean reload)
	{
		if (reload || contractChargeAcct == null || contractChargeAcct.size() == 0)
			;
		else
			return contractChargeAcct;

		contractChargeAcct = new HashMap<Integer, HashMap<Integer, MContractChargeAcct>>();
		final String sql = "SELECT * FROM JP_Contract_Charge_Acct WHERE JP_Contract_Acct_ID=? AND IsActive='Y'";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, getJP_Contract_Acct_ID());
			rs = pstmt.executeQuery();
			while (rs.next())
			{
				MContractChargeAcct chargeAcct =  new MContractChargeAcct (getCtx(), rs, get_TrxName());
				HashMap<Integer, MContractChargeAcct> innerMap = new HashMap<Integer, MContractChargeAcct>();
				innerMap.put(chargeAcct.getC_AcctSchema_ID(), chargeAcct);
				contractChargeAcct.put(chargeAcct.getC_Charge_ID(), innerMap);
			}
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}

		return contractChargeAcct;
	}

}
