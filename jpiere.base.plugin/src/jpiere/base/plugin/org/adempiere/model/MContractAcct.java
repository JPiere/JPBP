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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.model.MBPartnerLocation;
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
		if(newRecord || is_ValueChanged(MContractAcct.COLUMNNAME_IsPostingContractAcctJP))
		{
			if(!get_ValueAsBoolean(MContractAcct.COLUMNNAME_IsPostingContractAcctJP))
			{
				setIsPostingRecognitionDocJP(false);
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
		Integer ii = new Integer (JP_Contract_Acct_ID);
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

		HashMap<Integer, MContractBPAcct> map = new HashMap<Integer, MContractBPAcct>();
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
				MContractBPAcct contractBPAcct =  new MContractBPAcct (getCtx(), rs, get_TrxName());
				map.put(contractBPAcct.getC_AcctSchema_ID(), contractBPAcct);
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
	
	
	public MContractProductAcct getMContractProductAcct(int M_Product_Category_ID,  int C_AcctSchema_ID, boolean reload)
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

		HashMap<Integer, HashMap<Integer, MContractProductAcct>> map = new HashMap<Integer, HashMap<Integer, MContractProductAcct>>();
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
				MContractProductAcct contractProductAcct =  new MContractProductAcct (getCtx(), rs, get_TrxName());
				HashMap<Integer, MContractProductAcct> innerMap = new HashMap<Integer, MContractProductAcct>();
				innerMap.put(contractProductAcct.getC_AcctSchema_ID(), contractProductAcct);
				map.put(contractProductAcct.getM_Product_Category_ID(), innerMap);
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
	
	
	
	
	
}
