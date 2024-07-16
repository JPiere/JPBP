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
package jpiere.base.plugin.org.adempiere.base;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.logging.Level;

import org.compiere.model.MClient;
import org.compiere.model.MSysConfig;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.DB;


/**
 * JPIERE-0619: DB Transaction of Auto control DocumentNo
 *
 * @author Hideaki Hagiwara
 *
 */
public class JPiereDocumentNoTrxValidator implements ModelValidator {

	private static CLogger log = CLogger.getCLogger(JPiereDocumentNoTrxValidator.class);
	private int AD_Client_ID = -1;
	public final static String COLUMNNAME_DOCUMENTNO = "DocumentNo";

	@Override
	public void initialize(ModelValidationEngine engine, MClient client)
	{
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();

		ArrayList<String> list = new ArrayList<String>();
		String sql = "SELECT TableName FROM AD_Table t INNER JOIN AD_Column c ON (t.AD_Table_ID=c.AD_Table_ID)"
														+ "	WHERE t.IsView='N' AND c.ColumnName = '"+COLUMNNAME_DOCUMENTNO +"'";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, null);
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add(rs.getString(1));
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

		for(String tableName : list)
		{
			engine.addModelChange(tableName, this);
		}

	}

	@Override
	public int getAD_Client_ID()
	{
		return AD_Client_ID;
	}

	@Override
	public String login(int AD_Org_ID, int AD_Role_ID, int AD_User_ID)
	{
		return null;
	}

	@Override
	public String modelChange(PO po, int type) throws Exception
	{

		if(type == ModelValidator.TYPE_BEFORE_NEW && MSysConfig.getBooleanValue("JP_DOCUMENTNO_TRX", true, po.getAD_Client_ID()))
		{
			int columnIndex = po.get_ColumnIndex(COLUMNNAME_DOCUMENTNO);
			if(columnIndex > -1)
			{
				String value = (String)po.get_Value(columnIndex);
				if (value != null && value.startsWith("<") && value.endsWith(">"))
					value = null;
				if (value == null || value.length() == 0)
				{
					int dt = po.get_ColumnIndex("C_DocTypeTarget_ID");
					if (dt == -1)
						dt = po.get_ColumnIndex("C_DocType_ID");
					if (dt != -1)		//	get based on Doc Type (might return null)
						value = DB.getDocumentNo(po.get_ValueAsInt(dt), null, false, po);
					if (value == null)	//	not overwritten by DocType and not manually entered
						value = DB.getDocumentNo(getAD_Client_ID(), po.get_TableName(), null, po);
					po.set_ValueNoCheck(COLUMNNAME_DOCUMENTNO, value);
				}
			}
		}

		return null;
	}

	@Override
	public String docValidate(PO po, int timing)
	{
		return null;
	}



}
