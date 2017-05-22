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
 * JPIERE-0159 : Crate Stock Org Time Stamp
 *
 * @author Hideaki Hagiwara
 *
 */
public class CreateStockOrgTimeStamp extends SvrProcess {

	private int 		p_AD_Client_ID = 0;
	private int 		p_AD_User_ID = 0;
	private Timestamp	p_DateValue = null;
	private boolean		p_isDeleteDataOnlyJP = false;

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
			}else if (name.equals("isDeleteDataOnlyJP")){
				p_isDeleteDataOnlyJP = para[i].getParameterAsBoolean();
			}else{
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}//if

		}//for
	}

	@Override
	protected String doIt() throws Exception {

		 Calendar calendar = Calendar.getInstance();
		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


		//YYYY-MM-DD HH24:MI:SS.mmmm  JDBC Timestamp format
		StringBuilder DateValue = new StringBuilder(p_DateValue.toString());
		StringBuilder DateValue_00 = new StringBuilder("TO_DATE('").append(DateValue.substring(0,10)).append(" 00:00:00','YYYY-MM-DD HH24:MI:SS')");
		StringBuilder DateValue_24 = new StringBuilder("TO_DATE('").append(DateValue.substring(0,10)).append(" 24:00:00','YYYY-MM-DD HH24:MI:SS')");

		StringBuilder sqlDelete = new StringBuilder ("DELETE JP_StockOrg ")
										.append(" WHERE DateValue=").append(DateValue_00)
										.append(" AND AD_Client_ID=").append(p_AD_Client_ID);
		int deleteNo = DB.executeUpdateEx(sqlDelete.toString(), get_TrxName());
		
		int insertedNo = 0;
		if(!p_isDeleteDataOnlyJP)
		{
			StringBuilder sql = new StringBuilder ("INSERT INTO JP_StockOrg ")
			.append("(AD_Client_ID, AD_Org_ID, IsActive, Created, CreatedBy, Updated, UpdatedBy,")
			.append(" DateValue, M_Product_ID, QtyBook) ")
			.append("SELECT t.AD_Client_ID, l.AD_Org_ID, 'Y',")		//AD_Client_ID, AD_Org_ID, IsActive,
			.append("TO_DATE('").append(sdf.format(calendar.getTime()) + "' ,'YYYY-MM-DD HH24:MI:SS')")	//Created
			.append("," + p_AD_User_ID + ",")															//CreatedBy
			.append("TO_DATE('").append(sdf.format(calendar.getTime()) + "' ,'YYYY-MM-DD HH24:MI:SS')")	//Updated
			.append("," + p_AD_User_ID + ",")															//UpdatedBy
			.append(DateValue_00).append(", t.M_Product_ID, SUM(t.MovementQty) ")	//DateValue, M_Product_ID, QtyBook
			.append("FROM M_Transaction t")
			.append(" INNER JOIN M_Locator l ON (t.M_Locator_ID=l.M_Locator_ID) ")
			.append("WHERE t.AD_Client_ID=").append(p_AD_Client_ID)
			.append(" AND t.MovementDate <").append(DateValue_24)
			.append(" GROUP BY t.AD_Client_ID, l.AD_Org_ID, t.M_Product_ID");
			insertedNo = DB.executeUpdateEx(sql.toString(), get_TrxName());
		}

		String deleted = Msg.getMsg(getCtx(), "Deleted");
		String inserted = Msg.getMsg(getCtx(), "Inserted");
		String retVal = null;
		retVal = deleted + " : " + deleteNo + " / " +inserted + " : " + insertedNo;
		addLog(retVal);

		return retVal;
	}

}
