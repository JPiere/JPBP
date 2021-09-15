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

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.compiere.model.MStorageOnHand;
import org.compiere.model.MTable;
import org.compiere.model.Query;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

/**
 * JPIERE-0501:JPiere PP Fact Line MA
 *
 * @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 *
 */
public class MPPFactLineMA extends X_JP_PP_FactLineMA {

	public MPPFactLineMA(Properties ctx, int JP_PP_FactLineMA_ID, String trxName)
	{
		super(ctx, JP_PP_FactLineMA_ID, trxName);
	}

	public MPPFactLineMA(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}

	/**
	 * Parent constructor
	 * @param parent
	 * @param asi
	 * @param qty
	 * @param dateMaterialPolicy
	 */
	public MPPFactLineMA( MPPFactLine parent, int asi, BigDecimal qty,Timestamp dateMaterialPolicy)	{
		super(parent.getCtx(),0,parent.get_TrxName());
		setM_AttributeSetInstance_ID(asi);
		setJP_PP_FactLine_ID(parent.get_ID());
		setMovementQty(qty);
		setAD_Org_ID(parent.getAD_Org_ID());
		if (dateMaterialPolicy == null)
		{
			if (asi > 0)
			{
				dateMaterialPolicy = MStorageOnHand.getDateMaterialPolicy(parent.getM_Product_ID(), asi, parent.get_TrxName());
			}
			if (dateMaterialPolicy == null)
			{
				dateMaterialPolicy = parent.getJP_PP_Fact().getMovementDate();
			}
		}
		setDateMaterialPolicy(dateMaterialPolicy);
	}

	@Override
	public void setDateMaterialPolicy(Timestamp DateMaterialPolicy) {
		if (DateMaterialPolicy != null)
			DateMaterialPolicy = Util.removeTime(DateMaterialPolicy);
		super.setDateMaterialPolicy(DateMaterialPolicy);
	}

	public static MPPFactLineMA get( MPPFactLine parent, int asi, Timestamp dateMPolicy )  {
		String where = " JP_PP_FactLine_ID = ? AND M_AttributeSetInstance_ID = ? ";
		if(dateMPolicy==null){
			dateMPolicy = new Timestamp(new Date().getTime());
		}
		where = where + "AND DateMaterialPolicy = trunc(cast(? as date))";

		MPPFactLineMA lineMA = MTable.get(parent.getCtx(), MPPFactLineMA.Table_Name).createQuery(where, parent.get_TrxName())
		.setParameters(parent.getJP_PP_FactLine_ID(), asi,dateMPolicy).first();

		if (lineMA != null)
			return lineMA;
		else
			return new MPPFactLineMA( parent,asi, Env.ZERO,dateMPolicy);
	}

	/**
	 * 	Get Material Allocations for Line
	 *	@param ctx context
	 *	@param M_ProductionLine_ID line
	 *	@param trxName trx
	 *	@return allocations
	 */
	public static MPPFactLineMA[] get (Properties ctx, int JP_PP_FactLine_ID, String trxName)
	{

		Query query = MTable.get(ctx, MPPFactLineMA.Table_Name)
							.createQuery(MPPFactLineMA.COLUMNNAME_JP_PP_FactLine_ID+"=?", trxName);
		query.setParameters(JP_PP_FactLine_ID);
		List<MPPFactLineMA> list = query.list();
		MPPFactLineMA[] retValue = list.toArray (new MPPFactLineMA[0]);
		return retValue;
	}	//	get

	@Override
	protected boolean beforeSave(boolean newRecord)
	{
		MPPFactLine parentLine = new MPPFactLine(getCtx(), getJP_PP_FactLine_ID(), get_TrxName());
		MPPFact prodParent = new MPPFact(getCtx(), parentLine.getJP_PP_Fact_ID(), get_TrxName());

		if (newRecord && prodParent.isProcessed()) {
			log.saveError("ParentComplete", Msg.translate(getCtx(), "JP_PP_Fact_ID"));
			return false;
		}
		return true;
	}

}
