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
package jpiere.base.plugin.org.adempiere.process;

import java.math.BigDecimal;
import java.util.logging.Level;

import jpiere.base.plugin.org.adempiere.model.MEstimation;

import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;

/**
 *  JPIERE-0186: Copy Estimation Lines
 *
 *	@author Hideaki Hagiwara
 */

public class CopyFromEstimation extends SvrProcess
{
	/**	The Estimation 		*/
	private int		p_JP_Estimation_ID = 0;

	/**
	 *  Prepare - e.g., get Parameters.
	 */
	protected void prepare()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
			else if (name.equals("JP_Estimation_ID"))
				p_JP_Estimation_ID = ((BigDecimal)para[i].getParameter()).intValue();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
	}	//	prepare

	/**
	 *  Perform process.
	 *  @return Message (clear text)
	 *  @throws Exception if not successful
	 */
	protected String doIt() throws Exception
	{
		int To_JP_Estimation_ID = getRecord_ID();
		if (log.isLoggable(Level.INFO)) log.info("From JP_Estimation_ID=" + p_JP_Estimation_ID + " to " + To_JP_Estimation_ID);
		if (To_JP_Estimation_ID == 0)
			throw new IllegalArgumentException("Target JP_Estimation_ID == 0");
		if (p_JP_Estimation_ID == 0)
			throw new IllegalArgumentException("Source JP_Estimation_ID == 0");
		MEstimation from = new MEstimation (getCtx(), p_JP_Estimation_ID, get_TrxName());
		MEstimation to = new MEstimation (getCtx(), To_JP_Estimation_ID, get_TrxName());
		//
		int no = to.copyLinesFrom (from, false, false);		//	no Attributes
		//
		return "@Copied@=" + no;
	}	//	doIt

}	//	CopyFromOrder
