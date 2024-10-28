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

import org.compiere.util.Util;

/**
 * 
 * JPIERE-0084 All Reference Window for Test
 * 
 * @author h.hagiwara
 *
 */
public class MReferenceTest extends X_JP_ReferenceTest {

	public MReferenceTest(Properties ctx, int JP_ReferenceTest_ID,String trxName) {
		super(ctx, JP_ReferenceTest_ID, trxName);
	}

	public MReferenceTest(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

    public MReferenceTest (Properties ctx, int JP_ReferenceTest_ID, String trxName, String ... virtualColumns)
    {
    	 super(ctx, JP_ReferenceTest_ID, trxName, virtualColumns);
    }
	
	@Override
	protected boolean beforeSave(boolean newRecord)
	{
		if(newRecord || is_ValueChanged(MReferenceTest.COLUMNNAME_JP_Multi_List))
		{
			String JP_Multi_List = getJP_Multi_List();
			if(!Util.isEmpty(JP_Multi_List))
			{
				String[] lists =JP_Multi_List.split(",");
				if(lists.length > 5)
				{
					log.saveError("Error", "〇●〇の選択は5語までにして下さい。");
					return false;
				}
			}
		}
		
		return super.beforeSave(newRecord);
	}

	@Override
	protected boolean afterSave(boolean newRecord, boolean success)
	{

		return super.afterSave(newRecord, success);
	}



}
