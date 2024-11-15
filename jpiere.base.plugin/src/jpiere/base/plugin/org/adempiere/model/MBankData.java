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
package jpiere.base.plugin.org.adempiere.model;

import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

import org.compiere.model.Query;
import org.compiere.util.Util;

/**
 * JPIERE-0302
 * 
 * 
 * @author h.hagiwara
 *
 */
public class MBankData extends X_JP_BankData {
	
	private static final long serialVersionUID = 456285359247595710L;

	public MBankData(Properties ctx, int JP_BankData_ID, String trxName) 
	{
		super(ctx, JP_BankData_ID, trxName);
	}
	
	public MBankData(Properties ctx, ResultSet rs, String trxName) 
	{
		super(ctx, rs, trxName);
	}
	
	
	protected MBankDataLine[] 	m_lines = null;
	
	public MBankDataLine[] getLines (String whereClause, String orderClause)
	{
		StringBuilder whereClauseFinal = new StringBuilder(MBankDataLine.COLUMNNAME_JP_BankData_ID+"=? ");
		if (!Util.isEmpty(whereClause, true))
			whereClauseFinal.append(whereClause);
		if (orderClause.length() == 0)
			orderClause = MBankDataLine.COLUMNNAME_Line;
		//
		List<MBankDataLine> list = new Query(getCtx(), I_JP_BankDataLine.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(getJP_BankData_ID())
										.setOrderBy(orderClause)
										.list();

		return list.toArray(new MBankDataLine[list.size()]);		
	}	//	getLines

	public MBankDataLine[] getLines (boolean requery, String orderBy)
	{
		if (m_lines != null && !requery) {
			set_TrxName(m_lines, get_TrxName());
			return m_lines;
		}
		//
		String orderClause = "";
		if (orderBy != null && orderBy.length() > 0)
			orderClause += orderBy;
		else
			orderClause += "Line";
		m_lines = getLines(null, orderClause);
		return m_lines;
	}	//	getLines
	
	public MBankDataLine[] getLines()
	{
		return getLines(false, null);
	}	//	getLines

}
