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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.logging.Level;

import org.adempiere.util.IProcessUI;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Msg;

/**
 * JPIERE-0413:Collate Migration Data with Migration Data
 *
 *
 * @author h.hagiwara
 *
 */
public class CollateMigrationDataWithMD extends SvrProcess {

	private int				p_AD_Client_ID = 0;
	private Timestamp			p_DateDoc_From = null;
	private Timestamp			p_DateDoc_To = null;
	private boolean			p_IsNonCollationOnly = true;
	private IProcessUI processMonitor = null;
	private String message = null;

	@Override
	protected void prepare()
	{
		p_AD_Client_ID = getAD_Client_ID();
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (name.equals("DateDoc"))
			{
				p_DateDoc_From = para[i].getParameterAsTimestamp();
			    p_DateDoc_To = para[i].getParameter_ToAsTimestamp();

			}else if (name.equals("IsNonCollationOnlyJP")){
				p_IsNonCollationOnly = para[i].getParameterAsBoolean();
			}else {
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}
		}

	}

	@Override
	protected String doIt() throws Exception
	{

		//Invoice
		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "JP_Invoice_DocumentNo");
		if(processMonitor != null)	processMonitor.statusUpdate(message);

		StringBuilder sql = new StringBuilder ("UPDATE JP_DataMigrationLine i ")
				.append("SET JP_DataMigration_Invoice_ID=(SELECT MAX(JP_DataMigration_ID) FROM JP_DataMigration p")
				.append(" WHERE i.JP_Invoice_DocumentNo=p.DocumentNo AND i.AD_Client_ID=p.AD_Client_ID) ")
				.append(" FROM JP_DataMigration dm ")
				.append(" WHERE i.JP_DataMigration_ID = dm.JP_DataMigration_ID AND dm.DateDoc >= ? AND dm.DateDoc <= ? AND i.AD_Client_ID=? AND i.JP_Invoice_DocumentNo IS NOT NULL ");
		if(p_IsNonCollationOnly)
			sql.append(" AND i.JP_DataMigration_Invoice_ID IS NULL ");

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int no = 0;
		try
		{
			pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
			pstmt.setTimestamp(1, p_DateDoc_From);
			pstmt.setTimestamp(2, p_DateDoc_To);
			pstmt.setInt(3, p_AD_Client_ID);
			no = pstmt.executeUpdate();

		}
		catch (Exception e)
		{
			throw new Exception(Msg.getMsg(getCtx(), "Error") + e.toString() +" : " + sql );
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

		//Payment
		message = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "JP_Payment_DocumentNo");
		if(processMonitor != null)	processMonitor.statusUpdate(message);

		sql = new StringBuilder ("UPDATE JP_DataMigrationLine i ")
				.append("SET JP_DataMigration_Payment_ID=(SELECT MAX(JP_DataMigration_ID) FROM JP_DataMigration p")
				.append(" WHERE i.JP_Payment_DocumentNo=p.DocumentNo AND i.AD_Client_ID=p.AD_Client_ID) ")
				.append(" FROM JP_DataMigration dm ")
				.append(" WHERE i.JP_DataMigration_ID = dm.JP_DataMigration_ID AND dm.DateDoc >= ? AND dm.DateDoc <= ? AND i.AD_Client_ID=? AND i.JP_Payment_DocumentNo IS NOT NULL ");
		if(p_IsNonCollationOnly)
			sql.append(" AND i.JP_DataMigration_Payment_ID IS NULL ");

		pstmt = null;
		rs = null;

		try
		{
			pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
			pstmt.setTimestamp(1, p_DateDoc_From);
			pstmt.setTimestamp(2, p_DateDoc_To);
			pstmt.setInt(3, p_AD_Client_ID);
			no = no + pstmt.executeUpdate();

		}
		catch (Exception e)
		{
			throw new Exception(Msg.getMsg(getCtx(), "Error") + e.toString() +" : " + sql );
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}


		return Msg.getMsg(getCtx(), "Success") + " " + Msg.getMsg(getCtx(), "JP_NumberOfRecords") + " : " + no;
	}


}
