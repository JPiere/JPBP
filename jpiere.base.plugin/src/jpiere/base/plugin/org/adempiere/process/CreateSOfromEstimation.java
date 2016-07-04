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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;

import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Msg;

/**
 * JPIERE-0185 : Create SO from Estimation
 *
 * @author Hideaki Hagiwara
 *
 */
public class CreateSOfromEstimation extends SvrProcess {

	private int 		p_AD_Client_ID = 0;
	private int 		p_AD_User_ID = 0;
	private Timestamp	p_DateValue = null;

	@Override
	protected void prepare() {

		p_AD_Client_ID =getProcessInfo().getAD_Client_ID();
		p_AD_User_ID =getProcessInfo().getAD_User_ID();
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null){
				;
			}else if (name.equals("DateValue")){
				p_DateValue = para[i].getParameterAsTimestamp();
			}else{
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}//if

		}//for
	}

	@Override
	protected String doIt() throws Exception {

		 
		return "OK";
	}

}
