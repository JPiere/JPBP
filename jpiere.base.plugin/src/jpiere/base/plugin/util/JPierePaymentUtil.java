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
import org.compiere.model.MPayment;
import org.compiere.util.Env;

/**
 * JPIERE-0456,0457 Open Amount of Payment at that time.
 *
 *
 * @author h.hagiwara
 *
 */
public class JPierePaymentUtil {

	/**
	 * 	Get Open Payment Amount Now
	 * 	@param ctx
	 *  @param MPayment
	 *  @param trxName
	 *  @return Open Amt of Payment Now
	 */
	public static BigDecimal getOpenAmtNow (Properties ctx, MPayment payment, String trxName )
	{

		BigDecimal allocatedNow = payment.getAllocatedAmt();
		BigDecimal m_openAmt = Env.ZERO;

		if(allocatedNow == null)
			allocatedNow = Env.ZERO;

		if (payment.isReceipt())//Income Payment
		{
			m_openAmt = payment.getPayAmt().subtract(allocatedNow);

		}else { //Out goings Paymens

			allocatedNow = allocatedNow.negate();
			m_openAmt = payment.getPayAmt().subtract(allocatedNow);
			m_openAmt = m_openAmt.negate();

		}



		return m_openAmt;

	}	//	getOpenAmtNow

	/**
	 * 	Get Open Payment Amount Point of Time
	 * 	@param ctx
	 *  @param MPayment
	 * 	@param Point Of Time expect DateAcct
	 *  @param trxName
	 *  @return Open Amt Point of Time
	 */
	public static BigDecimal getOpenAmtPointOfTime (Properties ctx, MPayment payment, Timestamp pointOfTime, String trxName )
	{

		if( pointOfTime == null )
		{
			return getOpenAmtNow(ctx, payment, trxName );
		}

		BigDecimal allocatedAmtPointOfTime = Env.ZERO;
		BigDecimal m_openAmt = Env.ZERO;

		// Get Allocated Amount at point of time(DateAcct)
		MAllocationHdr[] allocationHdr = MAllocationHdr.getOfPayment( ctx, payment.getC_Payment_ID(), trxName );
		Timestamp allocatedDate = null;
		for( int i = 0; i < allocationHdr.length; i++ )
		{
			allocatedDate = allocationHdr[i].getDateAcct();
			if(allocatedDate.after(pointOfTime))
				continue;


			MAllocationLine[] allocationLine = allocationHdr[i].getLines(false);
			for( int j = 0; j < allocationLine.length; j++ )
			{
				if(allocationLine[j].getC_Payment_ID() != payment.getC_Payment_ID())
					continue;

				allocatedAmtPointOfTime = allocatedAmtPointOfTime.add(allocationLine[j].getAmount());

			}//for j

		}//for i


		if (payment.isReceipt())//Income Payment
		{

			m_openAmt = payment.getPayAmt().subtract(allocatedAmtPointOfTime);

		}else { //Out goings Paymens

			allocatedAmtPointOfTime = allocatedAmtPointOfTime.negate();
			m_openAmt = payment.getPayAmt().subtract(allocatedAmtPointOfTime);
			m_openAmt = m_openAmt.negate();
		}



		return m_openAmt;

	}	//	getOpenAmtPointOfTime

}
