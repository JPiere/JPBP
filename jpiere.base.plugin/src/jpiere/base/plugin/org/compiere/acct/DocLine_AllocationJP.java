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

import java.math.BigDecimal;

import org.compiere.acct.Doc;
import org.compiere.acct.DocLine_Allocation;
import org.compiere.model.MAllocationLine;
import org.compiere.util.DB;

/**
 * IDEMPIERE-4083:currency rate by document or by transaction
 *
 * @author h.hagiwara
 *
 */
public class DocLine_AllocationJP extends DocLine_Allocation {

	public DocLine_AllocationJP(MAllocationLine line, Doc doc)
	{
		super(line, doc);
	}

	public void setC_ConversionType_ID(int C_ConversionType_ID)
	{
		super.setC_ConversionType_ID(C_ConversionType_ID);
	}


	public void setCurrencyRate(BigDecimal currencyRate)
	{
		super.setCurrencyRate(currencyRate);
	}

	
	/**
	 * 	Get Invoice C_Currency_ID
	 * 
	 * 継承しているDocLine_Allocationが、trxNameをNullで処理しており、Invoiceの登録からAllocationの転記処理まで一連のトランザクションで処理している場合、
	 * 正しい通貨が取得できず、為替差損益の処理が実行されてしまい、エラーが表示される。
	 * POS受注などの場合が該当する。
	 * iDempiereの本家が修正されるまの一時的なパッチとしてオーバーライド修正する。
	 * 
	 *	@return 0 if no invoice -1 if not found
	 */
	@Override
	public int getInvoiceC_Currency_ID()
	{
		if (getC_Invoice_ID() == 0)
			return 0;
		String sql = "SELECT C_Currency_ID "
			+ "FROM C_Invoice "
			+ "WHERE C_Invoice_ID=?";
		return  DB.getSQLValue(getPO().get_TrxName(), sql, getC_Invoice_ID());

	}	//	getInvoiceC_Currency_ID



}
