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
import org.compiere.model.MMatchInv;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;

/**
 * JPIERE-0197:Process to reverse invoice matching  at Designation date
 * @author hengsin
 * @author Hideaki Hagiwara
 *
 */
public class JPiereMatchInvReverse extends SvrProcess {
	private int		p_M_MatchInv_ID = 0;
	private Timestamp reversalDate = null;

	@Override
	protected void prepare() {
		p_M_MatchInv_ID = getRecord_ID();
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
			log.info ("M_MatchInv_ID=" + p_M_MatchInv_ID);

		MMatchInv inv = new MMatchInv (getCtx(), p_M_MatchInv_ID, get_TrxName());
		if (inv.get_ID() != p_M_MatchInv_ID)
			throw new AdempiereException("@NotFound@ @M_MatchInv_ID@ " + p_M_MatchInv_ID);

		if (inv.isProcessed())
		{
			if (reversalDate == null) {
				reversalDate = new Timestamp(System.currentTimeMillis());
			}
			if (!inv.reverse(reversalDate))
				throw new AdempiereException("Failed to reverse invoice matching");
		}
		return "@OK@";
	}

}
