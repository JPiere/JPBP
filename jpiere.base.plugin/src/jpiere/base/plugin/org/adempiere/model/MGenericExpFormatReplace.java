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
import java.util.Properties;


/**
 * JPIERE-0628 Generic Export Format
 *
 *  @author Hideaki Hagiwara
 *
 */
public class MGenericExpFormatReplace extends X_JP_GenericExpFormat_Replace {

	private static final long serialVersionUID = 4850058454308356291L;

	public MGenericExpFormatReplace(Properties ctx, int JP_GenericExpFormat_Replace_ID, String trxName) 
	{
		super(ctx, JP_GenericExpFormat_Replace_ID, trxName);
	}

	public MGenericExpFormatReplace(Properties ctx, int JP_GenericExpFormat_Replace_ID, String trxName, String... virtualColumns) 
	{
		super(ctx, JP_GenericExpFormat_Replace_ID, trxName, virtualColumns);
	}

	public MGenericExpFormatReplace(Properties ctx, String JP_GenericExpFormat_Replace_UU, String trxName) 
	{
		super(ctx, JP_GenericExpFormat_Replace_UU, trxName);
	}

	public MGenericExpFormatReplace(Properties ctx, String JP_GenericExpFormat_Replace_UU, String trxName, String... virtualColumns) 
	{
		super(ctx, JP_GenericExpFormat_Replace_UU, trxName, virtualColumns);
	}

	public MGenericExpFormatReplace(Properties ctx, ResultSet rs, String trxName) 
	{
		super(ctx, rs, trxName);
	}

	@Override
	protected boolean beforeSave(boolean newRecord)
	{
		return true;
	}

	
}
