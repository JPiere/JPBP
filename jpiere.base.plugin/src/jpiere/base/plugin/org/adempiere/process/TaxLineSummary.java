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

import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Util;

/**
 * JPIERE-0171:Check List of Tax Line Summary
 *
 * @author Hideaki Hagiwara
 *
 */
public class TaxLineSummary extends SvrProcess {


	private int			p_AD_Client_ID;

	private Timestamp	p_DateAcct_From = null;	//Mandatory
	private Timestamp	p_DateAcct_To = null;	//Mandatory
	private int			p_AD_Org_ID = 0;
	private boolean		p_IsSOTrx = true;			//Mandatory
	private String		p_Posted = null;



	@Override
	protected void prepare() {

		p_AD_Client_ID =getProcessInfo().getAD_Client_ID();
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null){
				;
			}else if (name.equals("DateAcct")){
				p_DateAcct_From = (Timestamp)para[i].getParameter();
				p_DateAcct_To = (Timestamp)para[i].getParameter_To();

			}else if(name.equals("AD_Org_ID")){
				p_AD_Org_ID = para[i].getParameterAsInt();

			}else if(name.equals("Posted")){
				p_Posted = para[i].getParameterAsString();
			}else if(name.equals("IsSOTrx")){
				p_IsSOTrx = para[i].getParameterAsBoolean();

			}else{
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}//if
		}//for

	}

	@Override
	protected String doIt() throws Exception {
		String DateFrom = p_DateAcct_From.toString();
		String DateTo = p_DateAcct_To.toString();

		StringBuilder DateValue_from = new StringBuilder("TO_DATE('").append(DateFrom.substring(0,10)).append(" 00:00:00','YYYY-MM-DD HH24:MI:SS')");
		StringBuilder DateValue_to = new StringBuilder("TO_DATE('").append(DateTo.substring(0,10)).append(" 00:00:00','YYYY-MM-DD HH24:MI:SS') + CAST('1Day' AS INTERVAL)");

		StringBuilder sql = new StringBuilder ("INSERT INTO T_TaxLineSumJP ")
							.append("(AD_PInstance_ID, DateAcct, AD_Client_ID, AD_Org_ID,C_DocType_ID,IsSOTrx ,C_Tax_ID, C_Currency_ID, DocStatus, JP_Posted, TaxbaseAmt, TaxAmt)")
							.append(" SELECT ").append(getAD_PInstance_ID()).append(", ").append(DateValue_from.toString())
								.append(", AD_Client_ID, AD_Org_ID,C_DocType_ID,IsSOTrx ,C_Tax_ID, C_Currency_ID, DocStatus, Posted, SUM(taxbaseamt),SUM(taxamt)")
							.append(" FROM JP_TaxLine ")
							.append(" WHERE AD_Client_ID = " + p_AD_Client_ID)
							.append(" AND DateAcct >=" + DateValue_from.toString())
							.append(" AND DateAcct < " + DateValue_to.toString());

						if(p_AD_Org_ID != 0)
						{
							sql = sql.append(" AND AD_Org_ID=").append(p_AD_Org_ID);
						}

						if(p_IsSOTrx)
						{
							sql = sql.append(" AND IsSOTrx='Y'");
						}else{
							sql = sql.append(" AND IsSOTrx='N'");
						}

						if(!Util.isEmpty(p_Posted))
						{
							sql = sql.append(" AND Posted='"+p_Posted+"'");
						}

						sql = sql.append(" GROUP BY AD_Client_ID, AD_Org_ID,C_DocType_ID,IsSOTrx,C_Tax_ID, C_Currency_ID,DocStatus, Posted ;");


		int InsertRecords = DB.executeUpdateEx(sql.toString(), get_TrxName());
		if (log.isLoggable(Level.FINE)) log.fine("Inserted Records=" + InsertRecords);


		return "Inserted Records=" + InsertRecords;
	}

}
