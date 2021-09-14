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
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.logging.Level;

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
		//Check Parent processed
		if(newRecord)
		{
			MPPPlan ppPlan = getParent();
			if(ppPlan.isProcessed())
			{
				log.saveError("Error", Msg.getElement(getCtx(), MPPPlan.COLUMNNAME_Processed));
				return false;
			}
		}

		//Set C_UOM_ID
		if(newRecord || is_ValueChanged(MPPFact.COLUMNNAME_C_UOM_ID) || getC_UOM_ID() == 0 )
		{
			MProduct product = MProduct.get(getM_Product_ID());
			if(product.getC_UOM_ID() != getC_UOM_ID())
			{
				setC_UOM_ID(product.getC_UOM_ID());
			}
		}

		//Check IsEndProduct
		if(newRecord || is_ValueChanged(COLUMNNAME_IsEndProduct))
		{
			MPPPlan parent =  getParent();
			if (parent.getM_Product_ID() == getM_Product_ID())
				setIsEndProduct(true);
			else
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

	@Override
	protected boolean afterSave(boolean newRecord, boolean success)
	{
		MPPPlan parent = getParent();

		//Check End Product
		if(isEndProduct())
		{
			MPPPlanLine[] lines = parent.getPPPlanLines(" AND IsEndProduct = 'Y' ", "");
			if(lines.length != 1)
			{
				log.saveError("Error", Msg.getElement(getCtx(), COLUMNNAME_IsEndProduct) +" - " + Msg.getMsg(getCtx(), "SaveErrorNotUnique"));
				return false;
			}
		}

		//Update parent ProductionQty
		if (isEndProduct() && (newRecord || is_ValueChanged(COLUMNNAME_PlannedQty)) )
		{
			if(parent.getProductionQty().compareTo(getPlannedQty()) != 0)
			{
				String sql = "UPDATE JP_PP_Plan SET ProductionQty=? "
						+ " WHERE JP_PP_Plan_ID=?";

				int no = DB.executeUpdate(sql
							, new Object[]{getPlannedQty(), getJP_PP_Plan_ID()}
							, false, get_TrxName(), 0);
				if (no != 1)
				{
					log.saveError("DBExecuteError", sql);
					return false;
				}
			}
		}

		return true;
	}

	public MPPPlan getParent()
	{
		if(parent==null)
			parent = new MPPPlan(getCtx(),getJP_PP_Plan_ID(), get_TrxName());
		else
			parent.set_TrxName(get_TrxName());

		return parent;
	}

	public PPPlanLineFactQty getPPPlanLineFactQty(String trxName)
	{
		BigDecimal plannedQty = Env.ZERO;
		BigDecimal qtyUsed = Env.ZERO;
		BigDecimal movementQty = Env.ZERO;

		String sql = "SELECT COALESCE(SUM(fl.plannedQty),0),COALESCE(SUM(fl.qtyUsed),0), COALESCE(SUM(fl.movementQty),0) "
						+"FROM JP_PP_FactLine fl INNER JOIN JP_PP_Fact f ON (fl.JP_PP_Fact_ID = f.JP_PP_Fact_ID) "
						+"WHERE  f.JP_PP_Plan_ID = ? AND fl.JP_PP_PlanLine_ID = ? AND f.JP_PP_Status = 'CO' ";


		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, trxName);
			pstmt.setInt(1, getJP_PP_Plan_ID());
			pstmt.setInt(2, getJP_PP_PlanLine_ID());
			rs = pstmt.executeQuery();
			if (rs.next())
			{
				plannedQty = rs.getBigDecimal(1);
				qtyUsed = rs.getBigDecimal(2);
				movementQty = rs.getBigDecimal(3);
			}
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

		return new PPPlanLineFactQty(plannedQty, qtyUsed ,movementQty);
	}

	public class PPPlanLineFactQty
	{
		private BigDecimal plannedQty = Env.ZERO;
		private BigDecimal qtyUsed = Env.ZERO;
		private BigDecimal movementQty = Env.ZERO;

		public PPPlanLineFactQty(BigDecimal plannedQty, BigDecimal qtyUsed, BigDecimal movementQty )
		{
			this.plannedQty = plannedQty;
			this.qtyUsed = qtyUsed;
			this.movementQty = movementQty;
		}

		public BigDecimal getPlannedQty()
		{
			return plannedQty;
		}

		public BigDecimal getQtyUsed()
		{
			return qtyUsed;
		}

		public BigDecimal getMovementQty()
		{
			return movementQty;
		}
	}
}
