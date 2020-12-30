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



}
