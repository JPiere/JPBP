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

import org.compiere.acct.Doc_Invoice;
import org.compiere.acct.Fact;
import org.compiere.model.MAcctSchema;

/** JPIERE-0363
*
* @author Hideaki Hagiwara
*
*/
public class Doc_InvoiceJP extends Doc_Invoice {
	
	public Doc_InvoiceJP(MAcctSchema as, ResultSet rs, String trxName) 
	{
		super(as, rs, trxName);
	}

	@Override
	public ArrayList<Fact> createFacts(MAcctSchema as) 
	{
		return super.createFacts(as);
	}
	
	
	
}
