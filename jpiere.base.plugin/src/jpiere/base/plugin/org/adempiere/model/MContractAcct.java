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

import org.compiere.model.MPeriod;
import org.compiere.util.CCache;
import org.compiere.util.DB;
import org.compiere.util.DisplayType;
import org.compiere.util.Msg;
import org.compiere.util.Util;


/**
 *
 * JPIERE-0363: Contract Management
 * JPIERE-0536: Journal Policy of Recognition Doc if no accounting config
 * JPIERE-0539: Create GL Journal From Invoice
 *
 * @author Hideaki Hagiwara
 *
 **/
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

		if(newRecord
				|| is_ValueChanged(MContractAcct.COLUMNNAME_IsPostingRecognitionDocJP)
				|| is_ValueChanged(MContractAcct.COLUMNNAME_JP_Recognition_JournalPolicy) )
		{
			if(!isPostingRecognitionDocJP())
			{
				setDocAction(null);
				setIsSplitWhenDifferenceJP(false);
				setJP_RecogToInvoicePolicy(null);
				setJP_Recognition_JournalPolicy(null);

			}else {

				if(getDocBaseType().equals("SOO") || getDocBaseType().equals("POO"))
				{
					if(Util.isEmpty(getJP_Recognition_JournalPolicy()))
					{
						log.saveError("Error", Msg.getMsg(getCtx(), "FillMandatory") + Msg.getElement(getCtx(), COLUMNNAME_JP_Recognition_JournalPolicy) );
						return false;
					}

				}else {

					setDocAction(null);
					setIsSplitWhenDifferenceJP(false);
					setJP_RecogToInvoicePolicy(null);
					setJP_Recognition_JournalPolicy(null);
				}
			}
		}

		if(newRecord
				|| is_ValueChanged(MContractAcct.COLUMNNAME_IsPostingGLJournalJP)
				|| is_ValueChanged(MContractAcct.COLUMNNAME_JP_GLJournal_JournalPolicy)
				|| is_ValueChanged(MContractAcct.COLUMNNAME_JP_GLJournal_DateDocSelect)
				|| is_ValueChanged(MContractAcct.COLUMNNAME_JP_GLJournal_DateDoc)
				|| is_ValueChanged(MContractAcct.COLUMNNAME_JP_GLJournal_DateAcctSelect)
				|| is_ValueChanged(MContractAcct.COLUMNNAME_JP_GLJournal_DateAcct) )
		{
			if(!isPostingGLJournalJP())
			{
				setJP_GLJournal_JournalPolicy(null);
				setJP_GLJournal_DateDocSelect(null);
				setJP_GLJournal_DateDoc(null);
				setJP_GLJournal_DateAcctSelect(null);
				setJP_GLJournal_DateAcct(null);

			}else {

				if(Util.isEmpty(getJP_GLJournal_JournalPolicy()))
				{
					log.saveError("Error", Msg.getMsg(getCtx(), "FillMandatory") + Msg.getElement(getCtx(), COLUMNNAME_JP_GLJournal_JournalPolicy) );
					return false;
				}

				if(Util.isEmpty(getJP_GLJournal_DateDocSelect()))
				{
					log.saveError("Error", Msg.getMsg(getCtx(), "FillMandatory") + Msg.getElement(getCtx(), COLUMNNAME_JP_GLJournal_DateDocSelect) );
					return false;
				}

				if(JP_GLJOURNAL_DATEACCTSELECT_FixedDate.equals(getJP_GLJournal_DateDocSelect()))
				{
					if(getJP_GLJournal_DateDoc() == null)
					{
						log.saveError("Error", Msg.getMsg(getCtx(), "FillMandatory") + Msg.getElement(getCtx(), COLUMNNAME_JP_GLJournal_DateDoc) );
						return false;
					}

				}else {
					setJP_GLJournal_DateAcct(null);
				}

				if(Util.isEmpty(getJP_GLJournal_DateAcctSelect()))
				{
					log.saveError("Error", Msg.getMsg(getCtx(), "FillMandatory") + Msg.getElement(getCtx(), COLUMNNAME_JP_GLJournal_DateAcctSelect) );
					return false;
				}

				if(JP_GLJOURNAL_DATEACCTSELECT_FixedDate.equals(getJP_GLJournal_DateAcctSelect()))
				{
					if(getJP_GLJournal_DateAcct() == null)
					{
						log.saveError("Error", Msg.getMsg(getCtx(), "FillMandatory") + Msg.getElement(getCtx(), COLUMNNAME_JP_GLJournal_DateAcct) );
						return false;
					}

					//validate period
					int C_Period_ID = MPeriod.getC_Period_ID(getCtx(), getJP_GLJournal_DateAcct(), getAD_Org_ID());
					if (C_Period_ID == 0)
					{
						log.saveError("PeriodNotFound", " : " + DisplayType.getDateFormat().format(getJP_GLJournal_DateAcct()));
						return false;
					}

				}else {
					setJP_GLJournal_DateAcct(null);
				}
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


	//String key = "M_Product_Category_ID" + "C_AcctSchema_ID"
	HashMap<String, MContractProductAcct> contractProductAcct = null;

	public MContractProductAcct getContractProductAcct(int M_Product_Category_ID,  int C_AcctSchema_ID, boolean reload)
	{
		if (reload || contractProductAcct == null || contractProductAcct.size() == 0)
			getAllContractProductAccts (reload);

		if(contractProductAcct == null || contractProductAcct.size() == 0)
			return null;

		String key = String.valueOf(M_Product_Category_ID) + String.valueOf(C_AcctSchema_ID);
		if(contractProductAcct.containsKey(key))
		{
			return contractProductAcct.get(key);

		}else{
			return null;
		}
	}

	public HashMap<String, MContractProductAcct> getAllContractProductAccts (boolean reload)
	{
		if (reload || contractProductAcct == null || contractProductAcct.size() == 0)
			;
		else
			return contractProductAcct;

		contractProductAcct = new HashMap<String, MContractProductAcct>();
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
				String key = String.valueOf(productAcct.getM_Product_Category_ID()) + String.valueOf(productAcct.getC_AcctSchema_ID());
				contractProductAcct.put(key, productAcct);
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



	//String key = "C_Tax_ID" + "C_AcctSchema_ID"
	HashMap<String, MContractTaxAcct> contractTaxAcct = null;

	public MContractTaxAcct getContracTaxAcct(int C_Tax_ID,  int C_AcctSchema_ID, boolean reload)
	{
		if (reload || contractTaxAcct == null || contractTaxAcct.size() == 0)
			getAllContractTaxAccts (reload);

		if(contractTaxAcct == null || contractTaxAcct.size() == 0)
			return null;

		String key = String.valueOf(C_Tax_ID) + String.valueOf(C_AcctSchema_ID);
		if(contractTaxAcct.containsKey(key))
		{
			return contractTaxAcct.get(key);

		}else{
			return null;
		}
	}

	public HashMap<String, MContractTaxAcct> getAllContractTaxAccts (boolean reload)
	{
		if (reload || contractTaxAcct == null || contractTaxAcct.size() == 0)
			;
		else
			return contractTaxAcct;

		contractTaxAcct = new HashMap<String, MContractTaxAcct>();
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
				String key = String.valueOf(taxAcct.getC_Tax_ID()) + String.valueOf(taxAcct.getC_AcctSchema_ID());
				contractTaxAcct.put(key, taxAcct);
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


	//String key = "C_Charge_ID" + "C_AcctSchema_ID"
	HashMap<String, MContractChargeAcct> contractChargeAcct = null;

	public MContractChargeAcct getContracChargeAcct(int C_Charge_ID,  int C_AcctSchema_ID, boolean reload)
	{
		if (reload || contractChargeAcct == null || contractChargeAcct.size() == 0)
			getAllContractChargeAccts (reload);

		if(contractChargeAcct == null || contractChargeAcct.size() == 0)
			return null;

		String key = String.valueOf(C_Charge_ID) + String.valueOf(C_AcctSchema_ID);
		if(contractChargeAcct.containsKey(key))
		{
			return contractChargeAcct.get(key);

		}else{
			return null;
		}
	}


	public HashMap<String, MContractChargeAcct> getAllContractChargeAccts (boolean reload)
	{
		if (reload || contractChargeAcct == null || contractChargeAcct.size() == 0)
			;
		else
			return contractChargeAcct;

		contractChargeAcct = new HashMap<String, MContractChargeAcct>();
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
				String key = String.valueOf(chargeAcct.getC_Charge_ID()) + String.valueOf(chargeAcct.getC_AcctSchema_ID());
				contractChargeAcct.put(key, chargeAcct);
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
