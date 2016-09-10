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

import org.compiere.model.MMovement;
import org.compiere.model.MMovementLine;
import org.compiere.model.PO;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;

/**
 *  JPIERE-0191:Copy Movement Lines
 *
 *	@author Hideaki Hagiwara
 *
 */
public class CopyFromMovement extends SvrProcess
{
	/**	The Order				*/
	private int		p_M_Movement_ID = 0;

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
			else if (name.equals("M_Movement_ID"))
				p_M_Movement_ID = ((BigDecimal)para[i].getParameter()).intValue();
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
		int To_M_Movement_ID = getRecord_ID();
		if (log.isLoggable(Level.INFO)) log.info("From M_Movement_ID=" + p_M_Movement_ID + " to " + To_M_Movement_ID);
		if (To_M_Movement_ID == 0)
			throw new IllegalArgumentException("Target M_Movement_ID == 0");
		if (p_M_Movement_ID == 0)
			throw new IllegalArgumentException("Source M_Movement_ID == 0");
		MMovement from = new MMovement (getCtx(), p_M_Movement_ID, get_TrxName());
		MMovement to = new MMovement (getCtx(), To_M_Movement_ID, get_TrxName());

		int no = 0;
		//
		for (MMovementLine fromLine : from.getLines(false))
		{
			if (!fromLine.isActive())
				continue;

			MMovementLine toLine = new MMovementLine(to);
			PO.copyValues(fromLine, toLine);
			toLine.setM_Movement_ID(to.getM_Movement_ID());
			toLine.setProcessed(false);
			toLine.setTargetQty(Env.ZERO);
			toLine.setScrappedQty(Env.ZERO);
			toLine.setConfirmedQty(Env.ZERO);
			toLine.setReversalLine_ID(0);
			toLine.setDD_OrderLine_ID(0);

			toLine.saveEx();
			no++;
		}

		//
		return "@Copied@=" + no;
	}	//	doIt

}	//	CopyFromOrder
