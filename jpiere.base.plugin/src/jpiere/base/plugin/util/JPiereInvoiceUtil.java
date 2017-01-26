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
package jpiere.base.plugin.util;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Properties;

import org.compiere.model.MAllocationHdr;
import org.compiere.model.MAllocationLine;
import org.compiere.model.MInvoice;
import org.compiere.util.Env;

public class JPiereInvoiceUtil {
	
	/**
	 * 	Get Open Invoice Amount Point of Time
	 * 	@param ctx
	 *  @param C_Invoice_ID
	 * 	@param Point Of Time expect DateAcct
	 * 	@param creditMemoAdjusted
	 *  @param trxName
	 *  @return Open Amt Point of Time
	 */
	public static BigDecimal getOpenAmtPointOfTime (Properties ctx, MInvoice invoice, Timestamp pointOfTime, boolean creditMemoAdjusted, String trxName )
	{
		
		if( pointOfTime == null )
		{
			return invoice.getOpenAmt(creditMemoAdjusted,null);
		}

		BigDecimal allocatedAmtPointOfTime = Env.ZERO;

		// Get Allocated Amount at point of time(DateAcct)
		MAllocationHdr[] allocationHdr = MAllocationHdr.getOfInvoice( ctx, invoice.getC_Invoice_ID(), trxName );
		Timestamp allocatedDate = null;
		for( int i = 0; i < allocationHdr.length; i++ )
		{
			allocatedDate = allocationHdr[i].getDateAcct();
			if(allocatedDate.after(pointOfTime))
				continue;
			
			
			MAllocationLine[] allocationLine = allocationHdr[i].getLines(false);
			for( int j = 0; j < allocationLine.length; j++ )
			{
				if(allocationLine[j].getC_Invoice_ID() != invoice.getC_Invoice_ID())
					continue;

				allocatedAmtPointOfTime = allocatedAmtPointOfTime.add(allocationLine[j].getAmount()).add(allocationLine[j].getDiscountAmt()).add(allocationLine[j].getWriteOffAmt());
				
			}//for j
			
		}//for i

		
		if (allocatedAmtPointOfTime.signum() < 0 )
			allocatedAmtPointOfTime = allocatedAmtPointOfTime.abs();	//	is absolute
		
		BigDecimal m_openAmt = invoice.getGrandTotal().subtract(allocatedAmtPointOfTime);
		
		if (!creditMemoAdjusted)
			return m_openAmt;
		if (invoice.isCreditMemo())
			return m_openAmt.negate();
		
		return m_openAmt;
		
	}	//	getOpenAmtPointOfTime
	
}
