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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.util.DB;
import org.compiere.util.Env;


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


	@Override
	protected boolean beforeSave(boolean newRecord)
	{
		MPPPlan plan =  new MPPPlan(getCtx(), getJP_PP_Plan_ID(), get_TrxName());
		if (plan.getM_Product_ID() == getM_Product_ID() && plan.getProductionQty().signum() == getMovementQty().signum())
			setIsEndProduct(true);
		else
			setIsEndProduct(false);

		if (isEndProduct())
		{
			setQtyUsed(null);
			setMovementQty(getQtyUsed());
		}else {
			setQtyUsed(getPlannedQty());
			setMovementQty(getQtyUsed().negate());
		}

		return true;
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
