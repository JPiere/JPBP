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
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Msg;

/**
 *  JPIERE-0192:Bulk update Movement Line Locator
 *
 *	@author Hideaki Hagiwara
 *
 */
public class BulkUpdateMovementLineLocator extends SvrProcess
{
	/**	The Order				*/
	private int		p_M_Movement_ID = 0;
	private int		p_M_Locator_From_ID = 0;
	private int		p_M_Locator_To_ID = 0;

	/**
	 *  Prepare - e.g., get Parameters.
	 */
	protected void prepare()
	{
		p_M_Movement_ID = getRecord_ID();

		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
			else if (name.equals("M_Locator_ID"))
				p_M_Locator_From_ID = ((BigDecimal)para[i].getParameter()).intValue();
			else if (name.equals("M_LocatorTo_ID"))
				p_M_Locator_To_ID = ((BigDecimal)para[i].getParameter()).intValue();
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

		if(p_M_Locator_From_ID == 0 && p_M_Locator_To_ID==0)
		{
			throw new Exception(Msg.getMsg(getCtx(), "JP_Mandatory_LocatorFromTo"));//Please input either Locator From or Locator To
		}


		MMovement movement = new MMovement (getCtx(), p_M_Movement_ID, get_TrxName());

		int no = 0;
		String msg = Msg.getMsg(getCtx(), "JP_CheckASI");//Please check AttributeSetInstance.

		for (MMovementLine mLine : movement.getLines(false))
		{

			if(p_M_Locator_From_ID > 0)
			{
				mLine.setM_Locator_ID(p_M_Locator_From_ID);
			}

			if(p_M_Locator_To_ID > 0)
			{
				mLine.setM_LocatorTo_ID(p_M_Locator_To_ID);
			}

			if(mLine.getM_AttributeSetInstance_ID()!=mLine.getM_AttributeSetInstanceTo_ID())
			{
				addBufferLog(0, null, null, msg+" "+ mLine.getParent().getDocumentNo() + "_" + mLine.getLine() , mLine.get_Table_ID(), mLine.get_ID());
			}

			no++;

			mLine.saveEx();
		}

		//
		return "@Copied@=" + no;
	}	//	doIt

}	//	CopyFromOrder
