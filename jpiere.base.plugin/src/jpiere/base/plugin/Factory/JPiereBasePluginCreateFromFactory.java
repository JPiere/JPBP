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
package jpiere.base.plugin.factory;

import jpiere.base.plugin.webui.apps.form.JPiereCreateFromStatementUI;

import org.compiere.grid.ICreateFrom;
import org.compiere.grid.ICreateFromFactory;
import org.compiere.model.GridTab;
import org.compiere.model.I_C_BankStatement;

/**
 * @author Hideaki Hagiwara
 *
 */
public class JPiereBasePluginCreateFromFactory implements ICreateFromFactory
{

	@Override
	public ICreateFrom create(GridTab mTab)
	{
		String tableName = mTab.getTableName();
		if (tableName.equals(I_C_BankStatement.Table_Name))
			return new JPiereCreateFromStatementUI(mTab);

		return null;
	}

}