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

import java.sql.Timestamp;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MMatchPO;
import org.compiere.model.MOrderLine;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;

/**
 * JPIERE-0196:Process to reverse PO Matching at Designation date
 * @author Hideaki Hagiwara
 *
 */
public class JPiereMatchPOReverse extends SvrProcess {
	private int		p_M_MatchPO_ID = 0;
	private Timestamp reversalDate = null;


	@Override
	protected void prepare() {
		p_M_MatchPO_ID = getRecord_ID();
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null && para[i].getParameter_To() == null)
				;
			else if (name.equals("DateAcct"))
			{
				reversalDate = (Timestamp)para[i].getParameter();
			}
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
	}

	/**
	 *	@return message
	 *	@throws Exception
	 */
	@Override
	protected String doIt() throws Exception {
		if (log.isLoggable(Level.INFO))
			log.info ("M_MatchPO_ID=" + p_M_MatchPO_ID);

		MMatchPO po = new MMatchPO (getCtx(), p_M_MatchPO_ID, get_TrxName());
		if (po.get_ID() != p_M_MatchPO_ID)
			throw new AdempiereException("@NotFound@ @M_MatchPO_ID@ " + p_M_MatchPO_ID);

		//JPiere logic
		MOrderLine orderLine = null;
		boolean isMatchReceipt = (po.getM_InOutLine_ID() != 0);
		if (isMatchReceipt)
		{
			orderLine = new MOrderLine (getCtx(), po.getC_OrderLine_ID(), get_TrxName());
			orderLine.setQtyReserved(orderLine.getQtyReserved().add(po.getQty()));
		}

		if (po.isProcessed())
		{
			if (reversalDate == null) {
				reversalDate = new Timestamp(System.currentTimeMillis());
			}
			if (!po.reverse(reversalDate))
				throw new AdempiereException("Failed to reverse matching");
		}
		return "@OK@";
	}

}
