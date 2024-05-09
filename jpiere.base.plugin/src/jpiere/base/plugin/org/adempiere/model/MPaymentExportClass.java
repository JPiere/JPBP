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
 * JPIERE-0615 : Payment Export Class
 * 
 * @author h.hagiwara
 *
 */
public class MPaymentExportClass extends X_JP_PaymentExportClass {

	private static final long serialVersionUID = -1267471755160108038L;

	public MPaymentExportClass(Properties ctx, int JP_PaymentExportClass_ID, String trxName) 
	{
		super(ctx, JP_PaymentExportClass_ID, trxName);
	}

	public MPaymentExportClass(Properties ctx, int JP_PaymentExportClass_ID, String trxName, String... virtualColumns) 
	{
		super(ctx, JP_PaymentExportClass_ID, trxName, virtualColumns);
	}

	public MPaymentExportClass(Properties ctx, String JP_PaymentExportClass_UU, String trxName) 
	{
		super(ctx, JP_PaymentExportClass_UU, trxName);
	}

	public MPaymentExportClass(Properties ctx, String JP_PaymentExportClass_UU, String trxName, String... virtualColumns) 
	{
		super(ctx, JP_PaymentExportClass_UU, trxName, virtualColumns);
	}

	public MPaymentExportClass(Properties ctx, ResultSet rs, String trxName) 
	{
		super(ctx, rs, trxName);
	}

}
