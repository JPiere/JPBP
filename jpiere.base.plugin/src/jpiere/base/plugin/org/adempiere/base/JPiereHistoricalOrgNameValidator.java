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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Level;

import org.compiere.model.MClient;
import org.compiere.model.MJournal;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.process.DocAction;
import org.compiere.util.CLogger;
import org.compiere.util.DB;

import jpiere.base.plugin.org.adempiere.model.MOrgHistory;


/**
 * JPIERE-0447:Org History
 *
 * @author Hideaki Hagiwara
 *
 */
public class JPiereHistoricalOrgNameValidator implements ModelValidator {

	private static CLogger log = CLogger.getCLogger(JPiereHistoricalOrgNameValidator.class);
	private int AD_Client_ID = -1;
	public final static String CCOLUMNNAME_JP_Org_History_Name = "JP_Org_History_Name";

	@Override
	public void initialize(ModelValidationEngine engine, MClient client)
	{
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();

		ArrayList<String> list = new ArrayList<String>();
		String sql = "SELECT TableName FROM AD_Table t INNER JOIN AD_Column c ON (t.AD_Table_ID=c.AD_Table_ID)"
														+ "	WHERE t.IsView='N' AND c.ColumnName = '"+CCOLUMNNAME_JP_Org_History_Name +"'";
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

		if(type == ModelValidator.TYPE_BEFORE_NEW || type == ModelValidator.TYPE_BEFORE_CHANGE)
		{
			int columnIndex = po.get_ColumnIndex(CCOLUMNNAME_JP_Org_History_Name);
			if(columnIndex > -1)
			{
				Timestamp date = null;
				boolean isSetOrgHistoryName = true;
				if(po instanceof DocAction)
				{

					if(po.get_ColumnIndex(MJournal.COLUMNNAME_DateAcct) > -1)
					{

						if(po.is_ValueChanged(MJournal.COLUMNNAME_AD_Org_ID) || po.is_ValueChanged(MJournal.COLUMNNAME_DateAcct) )
							date = (Timestamp)po.get_Value(MJournal.COLUMNNAME_DateAcct);
						else
							isSetOrgHistoryName = false;

					}else if(po.get_ColumnIndex(MJournal.COLUMNNAME_DateDoc) > -1) {

						if(po.is_ValueChanged(MJournal.COLUMNNAME_AD_Org_ID) || po.is_ValueChanged(MJournal.COLUMNNAME_DateDoc) )
							date = (Timestamp)po.get_Value(MJournal.COLUMNNAME_DateDoc);
						else
							isSetOrgHistoryName = false;

					}else {

						if(po.is_ValueChanged(MJournal.COLUMNNAME_AD_Org_ID))
							date = po.getCreated();
						else
							isSetOrgHistoryName = false;

					}

				}else{

					if(po.is_ValueChanged(MJournal.COLUMNNAME_AD_Org_ID))
						date = po.getCreated();
					else
						isSetOrgHistoryName = false;
				}

				if(isSetOrgHistoryName)
				{
					po.set_ValueNoCheck(CCOLUMNNAME_JP_Org_History_Name, MOrgHistory.getOrgHistoryName(po.getCtx(), po.getAD_Org_ID(), date, po.get_TrxName()) );
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
