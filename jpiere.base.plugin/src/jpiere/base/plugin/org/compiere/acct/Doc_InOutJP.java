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


package jpiere.base.plugin.org.compiere.acct;

import java.sql.ResultSet;
import java.util.ArrayList;

import org.compiere.acct.Doc_InOut;
import org.compiere.acct.Fact;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MInvoice;
import org.compiere.util.Env;

import jpiere.base.plugin.org.adempiere.model.MContractAcct;
import jpiere.base.plugin.org.adempiere.model.MContractContent;

/**
*  JPIERE-0363
*
* @author Hideaki Hagiwara
*
*/
public class Doc_InOutJP extends Doc_InOut {
	
	public Doc_InOutJP(MAcctSchema as, ResultSet rs, String trxName) 
	{
		super(as, rs, trxName);
	}
	
	
	@Override
	public ArrayList<Fact> createFacts(MAcctSchema as) 
	{
		if (!as.isAccrual())
			return super.createFacts(as);
		
		
		MInvoice invoice = (MInvoice)getPO();
		
		
		/**iDempiere Standard Posting*/
		int JP_ContractContent_ID = invoice.get_ValueAsInt("JP_ContractContent_ID");
		if(JP_ContractContent_ID == 0)
		{
			return super.createFacts(as);
		}
		
		MContractContent contractContent = MContractContent.get(getCtx(), JP_ContractContent_ID);
		if(contractContent.getJP_Contract_Acct_ID() == 0)
		{
			return super.createFacts(as);
		}
		
		MContractAcct contractAcct = MContractAcct.get(Env.getCtx(),contractContent.getJP_Contract_Acct_ID());
		if(!contractAcct.isPostingContractAcctJP())
		{
			return super.createFacts(as);
		}
		
		return null;
	}
	
}
