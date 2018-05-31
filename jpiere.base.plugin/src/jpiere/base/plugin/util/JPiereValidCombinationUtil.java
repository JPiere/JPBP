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
package jpiere.base.plugin.util;

import java.util.Properties;

import org.compiere.model.MAcctSchema;
import org.compiere.model.MAcctSchemaElement;
import org.compiere.model.MElementValue;
import org.compiere.model.PO;
import org.compiere.model.X_C_ValidCombination;
import org.compiere.util.Env;

/**
 * 	JPIERE-0393:Import Warehouse
 *
 *  @author Hideaki Hagiwara
 *
 */
public class JPiereValidCombinationUtil {


	/**
	 *
	 * @param ctx
	 * @param C_AcctSchema_ID
	 * @param ElementValue
	 * @param trxName
	 * @return -1:Unexpected Error, C_ValidCombination_ID
	 */
	public static int searchCreateValidCombination (Properties ctx, int C_AcctSchema_ID, String ElementValue, String trxName )
	{
		MAcctSchema as = MAcctSchema.get(ctx, C_AcctSchema_ID, trxName);

		MAcctSchemaElement  ase =as.getAcctSchemaElement(MAcctSchemaElement.ELEMENTTYPE_Account);
		int C_Element_ID = ase.getC_Element_ID();

		String WhereClause = " Value='" +ElementValue + "' AND IsSummary='N' AND C_Element_ID=" + C_Element_ID + " AND AD_Client_ID=" +Env.getAD_Client_ID(Env.getCtx());

		int[]  C_ElementValue_IDs = PO.getAllIDs("C_ElementValue", WhereClause, trxName);
		if(C_ElementValue_IDs.length==0)
		{
			return -1;

		}else if(C_ElementValue_IDs.length == 1) {

			int C_ElementValue_ID = C_ElementValue_IDs[0];
			String WhereClause2 = "C_AcctSchema_ID="+ C_AcctSchema_ID +  " AND Account_ID=" + C_ElementValue_ID + " AND AD_Org_ID=0 AND C_BPartner_ID IS NULL AND M_Product_ID IS NULL "
											+ " AND AD_Client_ID=" +Env.getAD_Client_ID(Env.getCtx());

			int[]  C_ValidCombination_IDs = PO.getAllIDs("C_ValidCombination", WhereClause2, trxName);
			if(C_ValidCombination_IDs == null || C_ValidCombination_IDs.length == 0)
			{
				MElementValue ev = new MElementValue(ctx, C_ElementValue_ID, trxName);

				X_C_ValidCombination vc = new X_C_ValidCombination(ctx, 0, trxName);
				vc.setCombination("*-"+ev.getValue()+"-_-_");
				vc.setDescription("*-"+ev.getName()+"-_-_");
				vc.setAD_Org_ID(0);
				vc.setAccount_ID(C_ElementValue_ID);
				vc.setC_BPartner_ID(0);
				vc.setM_Product_ID(0);
				vc.saveEx(trxName);

				return vc.getC_ValidCombination_ID();

			}else if(C_ValidCombination_IDs.length >= 1) {

				return C_ValidCombination_IDs[0];

			}else {
				return -1;
			}

		}else {
			return -1 ;
		}

	}



}
