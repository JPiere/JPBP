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

import jpiere.base.plugin.webui.apps.form.JPiereCreateFromRMAInOutUI;
import jpiere.base.plugin.webui.apps.form.JPiereCreateFromRMAOrder;
import jpiere.base.plugin.webui.apps.form.JPiereCreateFromRMAOrderUI;
import jpiere.base.plugin.webui.apps.form.JPiereCreateFromShipmentUI;
import jpiere.base.plugin.webui.apps.form.JPiereCreateFromStatementUI;

import org.compiere.grid.ICreateFrom;
import org.compiere.grid.ICreateFromFactory;
import org.compiere.model.GridTab;
import org.compiere.model.I_C_BankStatement;
import org.compiere.model.MDocType;
import org.compiere.model.MInOut;
import org.compiere.model.MRMA;
import org.compiere.util.Env;

/**
 * JPIERE-0091,0145,234
 *
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
		{
			return new JPiereCreateFromStatementUI(mTab);	//JPIERE-0091
			
		}else if(tableName.equals(MInOut.Table_Name)){
			
			Integer C_DocType_ID = (Integer)mTab.getField("C_DocType_ID").getValue();
			MDocType docType = MDocType.get(Env.getCtx(), C_DocType_ID.intValue());
			
			if(mTab.getGridWindow().isSOTrx())
			{

				if(docType.getDocBaseType().equals(MDocType.DOCBASETYPE_MaterialDelivery))
					return new JPiereCreateFromShipmentUI(mTab);//JPIERE-0145
				else if (docType.getDocBaseType().equals(MDocType.DOCBASETYPE_MaterialReceipt))
					return new JPiereCreateFromRMAInOutUI(mTab);//JPIERE-0234
				
			}else{
				
				if(docType.getDocBaseType().equals(MDocType.DOCBASETYPE_MaterialDelivery))
					return new JPiereCreateFromRMAInOutUI(mTab);//JPIERE-0234
				else if (docType.getDocBaseType().equals(MDocType.DOCBASETYPE_MaterialReceipt))
					return null;
			}
		}else if(tableName.equals(MRMA.Table_Name)){
			
			return new JPiereCreateFromRMAOrderUI(mTab); //JPIERE-0235
		}

		return null;
	}

}