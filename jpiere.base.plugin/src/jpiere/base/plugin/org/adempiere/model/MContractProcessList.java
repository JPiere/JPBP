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


/** JPIERE-0363
*
* @author Hideaki Hagiwara
*
*/
public class MContractProcessList extends X_JP_ContractProcessList {
	
	private static final long serialVersionUID = -6726525077090390262L;

	public MContractProcessList(Properties ctx, int JP_ContractProcessList_ID, String trxName) 
	{
		super(ctx, JP_ContractProcessList_ID, trxName);
	}
	
	public MContractProcessList(Properties ctx, ResultSet rs, String trxName) 
	{
		super(ctx, rs, trxName);
	}
	
}
