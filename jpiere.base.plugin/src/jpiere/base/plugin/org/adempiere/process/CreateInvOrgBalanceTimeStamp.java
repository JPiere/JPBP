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
 * JPIERE-0162 : Crate Inventory Org Balance Time Stamp
 *
 * @author Hideaki Hagiwara
 *
 */
public class CreateInvOrgBalanceTimeStamp extends SvrProcess {

	private int 		p_AD_Client_ID = 0;
	private int			p_C_AcctSchema_ID = 0;
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
			}else if (name.equals("C_AcctSchema_ID")){
				p_C_AcctSchema_ID = para[i].getParameterAsInt();
			}else if (name.equals("DateValue")){
				p_DateValue = para[i].getParameterAsTimestamp();
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
		StringBuilder DateValue_24 = new StringBuilder("TO_DATE('").append(DateValue.substring(0,10)).append(" 00:00:00','YYYY-MM-DD HH24:MI:SS') + CAST('1Day' AS INTERVAL)");

		StringBuilder sqlDelete = new StringBuilder ("DELETE JP_InvOrgBalance ")
										.append(" WHERE DateValue=").append(DateValue_00)
										.append(" AND C_AcctSchema_ID=").append(p_C_AcctSchema_ID)
										.append(" AND AD_Client_ID=").append(p_AD_Client_ID);
		int deleteNo = DB.executeUpdateEx(sqlDelete.toString(), get_TrxName());

		StringBuilder sql = new StringBuilder ("INSERT INTO JP_InvOrgBalance ")
		.append("(AD_Client_ID, AD_Org_ID, IsActive, Created, CreatedBy, Updated, UpdatedBy,")
		.append(" C_AcctSchema_ID, DateValue, M_Product_ID, Account_ID, QtyBook, AmtAcctDr, AmtAcctCr, AmtAcctBalance) ")
		.append("SELECT f.AD_Client_ID, f.AD_Org_ID, 'Y',")		//AD_Client_ID, AD_Org_ID, IsActive,
		.append("TO_DATE('").append(sdf.format(calendar.getTime()) + "' ,'YYYY-MM-DD HH24:MI:SS')")	//Created
		.append("," + p_AD_User_ID + ",")															//CreatedBy
		.append("TO_DATE('").append(sdf.format(calendar.getTime()) + "' ,'YYYY-MM-DD HH24:MI:SS')")	//Updated
		.append("," + p_AD_User_ID + ",")															//UpdatedBy
		.append("f.C_AcctSchema_ID,")									//C_AcctSchema_ID
		.append(DateValue_00).append(", f.M_Product_ID, f.Account_ID,")	//DateValue, M_Product_ID, Account_ID,
		.append("sum(f.Qty),sum(f.AmtAcctDr) ,sum(f.AmtAcctCr) , sum(f.AmtAcctDr-f.AmtAcctCr)")		//QtyBook,AmtAcctDr,AmtAcctCr
		.append("FROM FACT_ACCT f")
		.append(" INNER JOIN M_Product_Acct pa ON(pa.M_Product_ID = f.M_Product_ID and pa.C_AcctSchema_ID=f.C_AcctSchema_ID) ")
		.append(" INNER JOIN C_ValidCombination vc ON(pa.P_Asset_Acct = vc.C_ValidCombination_ID ) ")
		.append("WHERE f.AD_Client_ID=").append(p_AD_Client_ID)
		.append(" AND f.C_AcctSchema_ID=").append(p_C_AcctSchema_ID)
		.append(" AND f.Account_ID = vc.Account_ID ")
		.append(" AND f.DateAcct <").append(DateValue_24)
		.append(" GROUP BY f.AD_Client_ID,f.AD_Org_ID,f.C_AcctSchema_ID, f.M_Product_ID, f.Account_ID");
		int insertedNo = DB.executeUpdateEx(sql.toString(), get_TrxName());

		String deleted = Msg.getMsg(getCtx(), "Deleted");
		String inserted = Msg.getMsg(getCtx(), "Inserted");
		String retVal = null;
		if(deleteNo == 0)
			retVal = inserted + " : " + insertedNo;
		else
			retVal = deleted + " : " + deleteNo + " / " +inserted + " : " + insertedNo;
		addLog(retVal);

		return retVal;
	}

}
