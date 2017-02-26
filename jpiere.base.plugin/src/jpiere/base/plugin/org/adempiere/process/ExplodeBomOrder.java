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

import org.compiere.process.SvrProcess;
import org.compiere.util.Msg;

import jpiere.base.plugin.org.adempiere.model.MOrderJP;

/**
 * JPIERE-0294
 * 
 * @author Hideaki Hagiwara
 *
 */
public class ExplodeBomOrder extends SvrProcess {
	
	int C_Order_ID = 0;
	
	@Override
	protected void prepare() 
	{
		C_Order_ID = getRecord_ID();
		
	}
	
	@Override
	protected String doIt() throws Exception 
	{

		MOrderJP order = new MOrderJP(getCtx(),C_Order_ID, get_TrxName());
		boolean isOK = order.explodeBOM();
		String msg = null;
		
		if(isOK)
			msg = Msg.getMsg(getCtx(), "Success");
		else
			msg = Msg.getMsg(getCtx(), "JP_NoProductExplodeBOM");
		
		return msg;
			
	}
	
}
