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

import org.compiere.model.I_C_DocType;
import org.compiere.model.I_C_Tax;
import org.compiere.model.MBPartner;
import org.compiere.model.MDocType;
import org.compiere.model.MTax;
import org.compiere.util.CCache;
import org.compiere.util.Env;
import org.compiere.util.Msg;


/**
 * JPIERE-0107 Bill Schema Model
 * JPIERE-0277 Payment Request Model
 * JPIERE-0508 Recalculate Bill Tax
 *
 *  @author Hideaki Hagiwara
 *
 */
public class MBillSchema extends X_JP_BillSchema {

	public MBillSchema(Properties ctx, int JP_BillSchema_ID, String trxName) {
		super(ctx, JP_BillSchema_ID, trxName);
	}

	public MBillSchema(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	@Override
	protected boolean beforeSave(boolean newRecord)
	{

		if(newRecord || is_ValueChanged(COLUMNNAME_C_DocType_ID))
		{
			I_C_DocType docType = getC_DocType();
			if(!(docType.getDocBaseType().equals("JPB") ||docType.getDocBaseType().equals("JPP")))
			{
				log.saveError("Error", Msg.getMsg(getCtx(), "JP_DocTypeIncorrect"));
				return false;
			}
		}

		if(isTaxRecalculateJP())
		{
			if(getJP_TaxAdjust_DocType_ID()==0)
			{
				log.saveError("FillMandatory", Msg.getElement(getCtx(), COLUMNNAME_JP_TaxAdjust_DocType_ID));
				return false;
			}

			if(newRecord || is_ValueChanged(COLUMNNAME_JP_TaxAdjust_DocType_ID))
			{
				I_C_DocType docType = MDocType.get(getJP_TaxAdjust_DocType_ID());
				if(isSOTrx() && (docType.getDocBaseType().equals("ARI") || docType.getDocBaseType().equals("ARC")) )
				{
					;//Noting to do;

				}else if(!isSOTrx() && (docType.getDocBaseType().equals("API") || docType.getDocBaseType().equals("APC")) ){

					;//Noting to do;

				}else {

					log.saveError("Error", Msg.getMsg(getCtx(), "JP_DocTypeIncorrect") + " - " +Msg.getElement(getCtx(), COLUMNNAME_JP_TaxAdjust_DocType_ID));
					return false;
				}
			}


			if(getJP_TaxAdjust_Tax_ID()==0)
			{
				log.saveError("FillMandatory", Msg.getElement(getCtx(), COLUMNNAME_JP_TaxAdjust_Tax_ID));
				return false;
			}

			if(newRecord || is_ValueChanged(COLUMNNAME_JP_TaxAdjust_Tax_ID))
			{
				I_C_Tax tax = MTax.get(getJP_TaxAdjust_Tax_ID());
				if(tax.getRate().compareTo(Env.ZERO) != 0)
				{
					//If you would like to adjust tax, you should to apply Zero tax rate to this field.
					log.saveError("Error", Msg.getMsg(getCtx(), "JP_TaxAdjust_ZERO_TaxRate"));
					return false;
				}

			}

			if(getJP_TaxAdjust_PriceList_ID()==0)
			{
				log.saveError("FillMandatory", Msg.getElement(getCtx(), COLUMNNAME_JP_TaxAdjust_PriceList_ID));
				return false;
			}

		}

		return true;
	}


	/**	Cache				*/
	private static CCache<Integer,MBillSchema>	s_cache = new CCache<Integer,MBillSchema>(Table_Name, 20);

	/**
	 * 	Get MBillSchema
	 */
	public static MBillSchema get (int JP_BillSchema_ID)
	{
		Integer ii = Integer.valueOf(JP_BillSchema_ID);
		MBillSchema retValue = (MBillSchema)s_cache.get(ii);
		if (retValue != null)
			return retValue;
		retValue = new MBillSchema (Env.getCtx(), JP_BillSchema_ID, null);
		if (retValue.get_ID () != 0)
			s_cache.put (JP_BillSchema_ID, retValue);
		return retValue;
	}	//	get


	public static MBillSchema getBillSchemaBP(int C_BPartner_ID, boolean isSOTrx)
	{
		if(C_BPartner_ID == 0)
			return null;

		MBPartner bp = MBPartner.get(Env.getCtx(), C_BPartner_ID);
		int JP_BillSchema_ID = 0;
		if(isSOTrx)
			JP_BillSchema_ID = bp.get_ValueAsInt("JP_BillSchema_ID");
		else
			JP_BillSchema_ID = bp.get_ValueAsInt("JP_BillSchemaPO_ID");

		if(JP_BillSchema_ID == 0)
		{
			return null;

		}else {

			return get(JP_BillSchema_ID);
		}
	}

}
