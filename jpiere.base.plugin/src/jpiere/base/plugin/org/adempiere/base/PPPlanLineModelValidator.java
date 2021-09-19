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
package jpiere.base.plugin.org.adempiere.base;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.compiere.model.MClient;
import org.compiere.model.MSysConfig;
import org.compiere.model.MUOM;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Msg;

import jpiere.base.plugin.org.adempiere.model.I_JP_PP_PlanLine;
import jpiere.base.plugin.org.adempiere.model.MPPDoc;
import jpiere.base.plugin.org.adempiere.model.MPPPlanLine;


/**
 * JPIERE-0501:JPiere PP Plan Line Model Validator
 *
 * @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 *
 */
public class PPPlanLineModelValidator implements ModelValidator {

	private static CLogger log = CLogger.getCLogger(PPPlanLineModelValidator.class);
	private int AD_Client_ID = -1;

	@Override
	public void initialize(ModelValidationEngine engine, MClient client)
	{
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();
		engine.addModelChange(MPPPlanLine.Table_Name, this);
		;
	}

	@Override
	public int getAD_Client_ID()
	{
		return AD_Client_ID;
	}

	@Override
	public String login(int AD_Org_ID, int AD_Role_ID, int AD_User_ID)
	{
		return null;
	}

	@Override
	public String modelChange(PO po, int type) throws Exception
	{
		if(type == ModelValidator.TYPE_AFTER_NEW || type == ModelValidator.TYPE_AFTER_CHANGE || type == ModelValidator.TYPE_AFTER_DELETE)
		{
			//Set Name for Tree
			if(po instanceof I_JP_PP_PlanLine)
			{
				if(type == ModelValidator.TYPE_AFTER_CHANGE && !po.is_ValueChanged(MPPPlanLine.COLUMNNAME_PlannedQty))
				{
					;//Noting to do;
				}else {

					I_JP_PP_PlanLine i_PO = (I_JP_PP_PlanLine)po;
					String sql = "UPDATE JP_PP_Plan SET NAME = JP_NAME || ' [' || ? || '/' || "
							+ " (SELECT COALESCE(SUM(MovementQty),0) FROM JP_PP_PlanLine WHERE JP_PP_Plan_ID=? AND IsEndProduct='Y') || ']' "
							+ " WHERE JP_PP_Plan_ID=?";

					BigDecimal factQty = i_PO.getJP_MovementQtyFact();
					boolean isStdPrecision = MSysConfig.getBooleanValue(MPPDoc.JP_PP_UOM_STDPRECISION, true, i_PO.getAD_Client_ID(), i_PO.getAD_Org_ID());
					MUOM uom = MUOM.get(i_PO.getC_UOM_ID());
					factQty = factQty.setScale(isStdPrecision ? uom.getStdPrecision() : uom.getCostingPrecision(), RoundingMode.HALF_UP);

					int no = DB.executeUpdate(sql
								, new Object[]{factQty, i_PO.getJP_PP_Plan_ID(), i_PO.getJP_PP_Plan_ID()}
								, false, po.get_TrxName(), 0);

					if (no != 1)
					{
						log.saveError("DBExecuteError", "PPPlanLineModelValidator -> modelChange()");
						return Msg.getMsg(po.getCtx(), "DBExecuteError") + "PPPlanLineModelValidator -> modelChange()";
					}
				}
			}
		}

		return null;
	}

	@Override
	public String docValidate(PO po, int timing)
	{
		return null;
	}

}
