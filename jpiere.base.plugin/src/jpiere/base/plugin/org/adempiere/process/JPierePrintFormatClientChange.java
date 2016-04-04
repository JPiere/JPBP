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

import java.util.logging.Level;

import org.compiere.print.MPrintFormat;
import org.compiere.print.MPrintFormatItem;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;

/**
 * 
 * JPIERE-0167:
 * 
 * @author Hideaki Hagiwara
 *
 */
public class JPierePrintFormatClientChange extends SvrProcess
{
	/**	Print Format Parameter		*/
	private int		p_AD_PrintFormat_ID = -1;
	
	
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
			else if (name.equals("AD_PrintFormat_ID"))
				p_AD_PrintFormat_ID = para[i].getParameterAsBigDecimal().intValue();
			else
				log.log(Level.SEVERE, "prepare - Unknown Parameter: " + name);
		}
	}	//	prepare

	/**
	 *  Perform process.
	 *  @return Message (clear text)
	 *  @throws Exception if not successful
	 */
	protected String doIt() throws Exception
	{
		if (p_AD_PrintFormat_ID < 0)
			throw new IllegalArgumentException ("Invalid AD_PrintFormat_ID=" + p_AD_PrintFormat_ID);
			
		String sql = "UPDATE AD_PrintFormat SET ad_client_id=0, ad_org_id=0	 WHERE AD_PrintFormat_ID="+p_AD_PrintFormat_ID; 
		int no = DB.executeUpdate(sql.toString(), get_TrxName());
		
		sql = "UPDATE AD_PrintFormat_Trl SET ad_client_id=0, ad_org_id=0 WHERE AD_PrintFormat_ID="+p_AD_PrintFormat_ID; 
		no = DB.executeUpdate(sql.toString(), get_TrxName());

		sql = "UPDATE AD_PrintFormatItem SET ad_client_id=0, ad_org_id=0 WHERE AD_PrintFormat_ID="+p_AD_PrintFormat_ID; 
		no = DB.executeUpdate(sql.toString(), get_TrxName());
		
	
		MPrintFormat pf = MPrintFormat.get(getCtx(), p_AD_PrintFormat_ID, false);
		MPrintFormatItem[] pfis = pf.getAllItems();
		for(int i = 0; i < pfis.length; i++)
		{
			sql = "UPDATE AD_PrintFormatItem_Trl SET ad_client_id=0, ad_org_id=0 WHERE AD_PrintFormatItem_ID="+pfis[i].getAD_PrintFormatItem_ID(); 
			no = DB.executeUpdate(sql.toString(), get_TrxName());;
		}
		
		
		return "OK";
	}	//	doIt

}	//	JPierePrintFormatClientChange
