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
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;


/**
 * JPIERE-0501:JPiere PP Plan Line
 *
 * @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 *
 */
public class MPPPlanLine extends X_JP_PP_PlanLine {

	public MPPPlanLine(Properties ctx, int JP_PP_PlanLine_ID, String trxName)
	{
		super(ctx, JP_PP_PlanLine_ID, trxName);
	}

	public MPPPlanLine(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}


	protected MPPPlan parent = null;

	@Override
	protected boolean beforeSave(boolean newRecord)
	{
		//SetAD_Org_ID
		if(newRecord)
		{
			setAD_Org_ID(getParent().getAD_Org_ID());
		}

		//Check Parent processed
		if(newRecord)
		{
			if(getParent().isProcessed())
			{
				log.saveError("Error", Msg.getElement(getCtx(), MPPPlan.COLUMNNAME_Processed));
				return false;
			}
		}

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
				setJP_QtyUsedFact(null);
				setMovementQty(getPlannedQty());
				if(newRecord)
				{
					setJP_MovementQtyFact(Env.ZERO);
				}
			}
		}else {

			if(newRecord || is_ValueChanged(COLUMNNAME_PlannedQty))
			{
				boolean isStdPrecision = MSysConfig.getBooleanValue(MPPDoc.JP_PP_UOM_STDPRECISION, true, getAD_Client_ID(), getAD_Org_ID());
				MUOM uom = MUOM.get(getC_UOM_ID());
				setPlannedQty(getPlannedQty().setScale(isStdPrecision ? uom.getStdPrecision() : uom.getCostingPrecision(), RoundingMode.HALF_UP));
				setQtyUsed(getPlannedQty());
				setMovementQty(getQtyUsed().negate());
				if(newRecord)
				{
					setJP_QtyUsedFact(Env.ZERO);
					setJP_MovementQtyFact(Env.ZERO);
				}
			}
		}

		return true;
	}

	@Override
	protected boolean afterSave(boolean newRecord, boolean success)
	{
		//Update parent ProductionQty
		if (isEndProduct() && (newRecord || is_ValueChanged(COLUMNNAME_PlannedQty)) )
		{

			int no = updateParentProductionQty(get_TrxName());
			if (no != 1)
			{
				log.saveError("DBExecuteError", "MPPPlanLine#afterSave() -> updateParentProductionQty()");
				return false;
			}

		}

		return true;
	}

	@Override
	protected boolean afterDelete(boolean success)
	{
		int no = updateParentProductionQty(get_TrxName());
		if (no != 1)
		{
			log.saveError("DBExecuteError", "MPPPlanLine#afterDelete() -> updateParentProductionQty()");
			return false;
		}

		MPPPlanLine[] lines = getParent().getPPPlanLines(true, null);
		if(lines.length == 0)
		{
			String sql = "UPDATE JP_PP_Plan SET IsCreated='N' WHERE JP_PP_Plan_ID=? ";

			no = DB.executeUpdate(sql
						, new Object[]{getJP_PP_Plan_ID()}
						, false, get_TrxName(), 0);

			if (no != 1)
			{
				log.saveError("DBExecuteError", "MPPPlanLine#afterDelete()");
				return false;
			}
		}

		return true;
	}

	private int updateParentProductionQty(String trxName)
	{
		String sql = "UPDATE JP_PP_Plan SET ProductionQty=(SELECT COALESCE(SUM(MovementQty),0) FROM JP_PP_PlanLine WHERE JP_PP_Plan_ID=? AND IsEndProduct='Y') "
				+ " WHERE JP_PP_Plan_ID=?";

		int no = DB.executeUpdate(sql
					, new Object[]{getJP_PP_Plan_ID(), getJP_PP_Plan_ID()}
					, false, trxName, 0);
		return no;
	}

	public MPPPlan getParent()
	{
		if(parent==null)
			parent = new MPPPlan(getCtx(),getJP_PP_Plan_ID(), get_TrxName());
		else
			parent.set_TrxName(get_TrxName());

		return parent;
	}

}
