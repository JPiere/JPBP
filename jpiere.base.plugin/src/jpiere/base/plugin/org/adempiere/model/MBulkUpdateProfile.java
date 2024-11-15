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

/**
 * JPIERE-0621 Bulk Update Profile
 *
 *  @author Hideaki Hagiwara
 *
 */
public class MBulkUpdateProfile extends X_JP_BulkUpdateProfile {

	private static final long serialVersionUID = -1995859719381746297L;


	public MBulkUpdateProfile(Properties ctx, int JP_BulkUpdateProfile_ID, String trxName) 
	{
		super(ctx, JP_BulkUpdateProfile_ID, trxName);
	}

	public MBulkUpdateProfile(Properties ctx, int JP_BulkUpdateProfile_ID, String trxName, String... virtualColumns) 
	{
		super(ctx, JP_BulkUpdateProfile_ID, trxName, virtualColumns);
	}

	public MBulkUpdateProfile(Properties ctx, String JP_BulkUpdateProfile_UU, String trxName)
	{
		super(ctx, JP_BulkUpdateProfile_UU, trxName);
	}

	public MBulkUpdateProfile(Properties ctx, String JP_BulkUpdateProfile_UU, String trxName, String... virtualColumns) 
	{
		super(ctx, JP_BulkUpdateProfile_UU, trxName, virtualColumns);
	}

	public MBulkUpdateProfile(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}
	
	public MBulkUpdateProfileLine[] getLines ()
	{
		StringBuilder whereClauseFinal = new StringBuilder(MBulkUpdateProfileLine.COLUMNNAME_JP_BulkUpdateProfile_ID+"=? AND IsActive='Y'");
		String 	orderClause = MBulkUpdateProfileLine.COLUMNNAME_Line;
		
		List<MBulkUpdateProfileLine> list = new Query(getCtx(), MBulkUpdateProfileLine.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(get_ID())
										.setOrderBy(orderClause)
										.list();
		
		return list.toArray(new MBulkUpdateProfileLine[list.size()]);		
	}	//	getLines

	
	public MBulkUpdateProfileAccess[] getAccessRoles ()
	{
		StringBuilder whereClauseFinal = new StringBuilder(MBulkUpdateProfileLine.COLUMNNAME_JP_BulkUpdateProfile_ID+"=? AND IsActive='Y'");
		String 	orderClause = MBulkUpdateProfileAccess.COLUMNNAME_JP_BulkUpdateProfileAccess_ID;
		
		List<MBulkUpdateProfileAccess> list = new Query(getCtx(), MBulkUpdateProfileAccess.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(get_ID())
										.setOrderBy(orderClause)
										.list();
		
		return list.toArray(new MBulkUpdateProfileAccess[list.size()]);		
	}	//	getRoleAccesses
	
}
