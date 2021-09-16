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

import java.math.RoundingMode;
import java.sql.ResultSet;
import java.util.Properties;

import org.compiere.model.MProduct;
import org.compiere.model.MSysConfig;
import org.compiere.model.MUOM;
import org.compiere.util.Env;

/**
 * JPIERE-0502: JPiere PP Doc Template
 *
 * @author Hideaki Hagiwara
 *
 */
public class MPPPlanLineT extends X_JP_PP_PlanLineT {

	public MPPPlanLineT(Properties ctx, int JP_PP_PlanLineT_ID, String trxName)
	{
		super(ctx, JP_PP_PlanLineT_ID, trxName);
	}

	public MPPPlanLineT(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}

	@Override
	protected boolean beforeSave(boolean newRecord)
	{
		//Set C_UOM_ID
		if(newRecord || is_ValueChanged(MPPPlanLine.COLUMNNAME_C_UOM_ID) || getC_UOM_ID() == 0 )
		{
			MProduct product = MProduct.get(getM_Product_ID());
			if(product.getC_UOM_ID() != getC_UOM_ID())
			{
				setC_UOM_ID(product.getC_UOM_ID());
			}
		}

		//Check IsEndProduct
		if (getParent().getM_Product_ID() == getM_Product_ID() &&
				(getParent().getProductionQty().signum() == getPlannedQty().signum()
				|| getParent().getProductionQty().compareTo(Env.ZERO) == 0
				|| getPlannedQty().compareTo(Env.ZERO) == 0 ))
		{
			setIsEndProduct(true);
		}else {
			setIsEndProduct(false);
		}

		//Convert Qty & Rounding Qty
		if (isEndProduct())
		{
			if(newRecord || is_ValueChanged(COLUMNNAME_PlannedQty))
			{
				boolean isStdPrecision = MSysConfig.getBooleanValue(MPPDoc.JP_PP_UOM_STDPRECISION, true, getAD_Client_ID(), getAD_Org_ID());
				MUOM uom = MUOM.get(getC_UOM_ID());
				setPlannedQty(getPlannedQty().setScale(isStdPrecision ? uom.getStdPrecision() : uom.getCostingPrecision(), RoundingMode.HALF_UP));
				setQtyUsed(null);
				setMovementQty(getPlannedQty());
			}
		}else {

			if(newRecord || is_ValueChanged(COLUMNNAME_PlannedQty))
			{
				boolean isStdPrecision = MSysConfig.getBooleanValue(MPPDoc.JP_PP_UOM_STDPRECISION, true, getAD_Client_ID(), getAD_Org_ID());
				MUOM uom = MUOM.get(getC_UOM_ID());
				setPlannedQty(getPlannedQty().setScale(isStdPrecision ? uom.getStdPrecision() : uom.getCostingPrecision(), RoundingMode.HALF_UP));
				setQtyUsed(getPlannedQty());
				setMovementQty(getQtyUsed().negate());
			}
		}

		return true;
	}

	protected MPPPlanT parent = null;

	public MPPPlanT getParent()
	{
		if(parent==null)
			parent = new MPPPlanT(getCtx(),getJP_PP_PlanT_ID(), get_TrxName());
		else
			parent.set_TrxName(get_TrxName());

		return parent;
	}

}
