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

import java.util.Collection;

import jpiere.base.plugin.org.adempiere.model.MCorporation;

import org.adempiere.model.GenericPO;
import org.compiere.model.MBPartner;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.process.SvrProcess;

/**
 *  DUNSナンバー一括コピープロセス.
 *
 *  @author Hideaki Hagiwara（萩原 秀明:h.hagiwara@oss-erp.co.jp）
 *
 */
public class DunsNoCopyInfoWindow extends SvrProcess {

	@Override
	protected void prepare() {
		;
	}

	@Override
	protected String doIt() throws Exception {

		String whereClause = "EXISTS (SELECT T_Selection_ID FROM T_Selection WHERE T_Selection.AD_PInstance_ID=? " +
							"AND T_Selection.T_Selection_ID = JP_Corporation.JP_Corporation_ID)";

		Collection<GenericPO> genericPOs = new Query(getCtx(), MCorporation.Table_Name, whereClause, get_TrxName())
									.setClient_ID()
									.setParameters(new Object[]{getAD_PInstance_ID()})
									.list();

		int bpNum = 0;
		for(PO po : genericPOs)
		{
			MCorporation corp = new MCorporation(getCtx(), po.get_ID(),get_TrxName());
			String DUNS = corp.getDUNS();
			MBPartner[] BPs = corp.getBPartners();

			for(int i = 0; i < BPs.length; i++)
			{

				BPs[i].setDUNS(DUNS);
				BPs[i].saveEx(get_TrxName());
				String msg = BPs[i].getValue() + "_" + BPs[i].getName();
				//addBufferLog(getAD_PInstance_ID(), null, null, msg, MBPartner.Table_ID, BPs[i].get_ID());
				bpNum++;
			}
		}


		return "Corporations = " + genericPOs.size() + " - Update Gross Business Partner Num = "+ bpNum ;
	}

}
