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
package jpiere.base.plugin.org.adempiere.callout;

import java.util.Properties;

import org.adempiere.base.IColumnCallout;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.MProduct;
import org.compiere.process.DocAction;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.MReferenceTest;

/**
 *
 * JPIERE-0084
 *
 * @author Hideaki Hagiwara
 *
 */
public class JPiereReferenceTestCallout implements IColumnCallout {

	@Override
	public String start(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{

		if(mField.getColumnName().equals(MReferenceTest.COLUMNNAME_JP_ReferenceTest_ID))
		{
			GridField button = mTab.getField(MReferenceTest.COLUMNNAME_Processing);
			String docStatus = (String)mTab.getValue("DocStatus");
			if(!Util.isEmpty(docStatus) && docStatus.equals(DocAction.STATUS_Completed))
			{
				button.getVO().IsReadOnly = true;
			}else {
				button.getVO().IsReadOnly = false;
			}

			if(!Util.isEmpty(docStatus) && docStatus.equals(DocAction.STATUS_Voided))
			{
				mTab.getVO().IsReadOnly  = true;
			}else {
				mTab.getVO().IsReadOnly  = false;
			}

		}else if(mField.getColumnName().equals(MReferenceTest.COLUMNNAME_DocStatus)) {

			String docStatus = (String)value;
			GridField button = mTab.getField(MReferenceTest.COLUMNNAME_Processing);
			if(!Util.isEmpty(docStatus) && docStatus.equals(DocAction.STATUS_Completed))
			{
				button.getVO().IsReadOnly = true;
			}else {
				button.getVO().IsReadOnly = false;
			}

			if(!Util.isEmpty(docStatus) && docStatus.equals(DocAction.STATUS_Voided))
			{
				mTab.getVO().IsReadOnly  = true;
			}else {
				mTab.getVO().IsReadOnly  = false;
			}


		}else if(mField.getColumnName().equals(MReferenceTest.COLUMNNAME_M_Product_ID)){

			if(value != null)
			{
				Integer M_Product_ID = (Integer)value;
				MProduct product = new MProduct(ctx,M_Product_ID.intValue(), null );
				mTab.setValue("M_Product_Category_ID", product.getM_Product_Category_ID());
			}

		}else if(mField.getColumnName().equals(MReferenceTest.COLUMNNAME_JP_BPartner_Multi)){

			int numLines = mField.getNumLines();
			if(numLines==1)
				numLines = 9;
			
			if(value != null)
			{
				String[] lists = value.toString().split(",");
				if(lists.length > numLines && numLines > 1)
				{
					String JP_Multi_List = "";
					for(int i = 0 ; i < numLines; i++)
					{
						JP_Multi_List = JP_Multi_List+lists[i]+",";
					}
					mTab.setValue(MReferenceTest.COLUMNNAME_JP_BPartner_Multi, JP_Multi_List);
					mTab.fireDataStatusEEvent("Warning", Msg.getMsg(Env.getCtx(), "JP_SelectUpTo", new Object[] {String.valueOf(numLines)} ), false);
				}
			}
			
		}else if(mField.getColumnName().equals(MReferenceTest.COLUMNNAME_JP_Product_Multi)){
			
			int numLines = mField.getNumLines();
			if(numLines==1)
				numLines = 9;
			
			if(value != null)
			{
				String[] lists = value.toString().split(",");
				if(lists.length > numLines && numLines > 1)
				{
					String JP_Multi_List = "";
					for(int i = 0 ; i < numLines; i++)
					{
						JP_Multi_List = JP_Multi_List+lists[i]+",";
					}
					mTab.setValue(MReferenceTest.COLUMNNAME_JP_Product_Multi, JP_Multi_List);
					mTab.fireDataStatusEEvent("Warning", Msg.getMsg(Env.getCtx(), "JP_SelectUpTo", new Object[] {String.valueOf(numLines)} ), false);
				}
			}
			
		}else if(mField.getColumnName().equals(MReferenceTest.COLUMNNAME_JP_Multi_List)){

			int numLines = mField.getNumLines();
			if(numLines==1)
				numLines = 5;
			
			if(value != null)
			{
				String[] lists = value.toString().split(",");
				if(lists.length > numLines && numLines > 1)
				{
					String JP_Multi_List = "";
					for(int i = 0 ; i < numLines; i++)
					{
						JP_Multi_List = JP_Multi_List+lists[i]+",";
					}
					mTab.setValue(MReferenceTest.COLUMNNAME_JP_Multi_List, JP_Multi_List);
					mTab.fireDataStatusEEvent("Warning", Msg.getMsg(Env.getCtx(), "JP_SelectUpTo", new Object[] {String.valueOf(numLines)} ), false);
				}
			}
			
		}

		return null;
	}

}
