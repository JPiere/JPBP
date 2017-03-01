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
 * JPIERE-0302 Import Bank Data Window
 * 
 * 
 * @author h.hagiwara
 *
 */
public class MBankDataLine extends X_JP_BankDataLine {
	
	public MBankDataLine(Properties ctx, int JP_BankDataLine_ID, String trxName) 
	{
		super(ctx, JP_BankDataLine_ID, trxName);
	}
	
	public MBankDataLine(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}
	
	//TODO:明細を更新した際に、ヘッダの合計フィールドの更新
	
}
