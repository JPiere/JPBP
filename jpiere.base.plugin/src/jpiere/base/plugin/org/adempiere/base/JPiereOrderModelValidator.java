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

import java.util.List;

import org.compiere.model.I_M_Movement;
import org.compiere.model.MClient;
import org.compiere.model.MDocType;
import org.compiere.model.MMovement;
import org.compiere.model.MMovementLine;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.process.DocAction;
import org.compiere.util.CLogger;

public class JPiereOrderModelValidator implements ModelValidator {

	private static CLogger log = CLogger.getCLogger(JPiereOrderModelValidator.class);
	private int AD_Client_ID = -1;
	private int AD_Org_ID = -1;
	private int AD_Role_ID = -1;
	private int AD_User_ID = -1;

	@Override
	public void initialize(ModelValidationEngine engine, MClient client) {
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();
		engine.addModelChange(MOrder.Table_Name, this);
		engine.addDocValidate(MOrder.Table_Name, this);

	}

	@Override
	public int getAD_Client_ID() {
		return AD_Client_ID;
	}

	@Override
	public String login(int AD_Org_ID, int AD_Role_ID, int AD_User_ID) {
		this.AD_Org_ID = AD_Org_ID;
		this.AD_Role_ID = AD_Role_ID;
		this.AD_User_ID = AD_User_ID;

		return null;
	}

	@Override
	public String modelChange(PO po, int type) throws Exception {

		//For Simple Input Window(JPIERE-0146/JPIERE-0147)
		if(type == ModelValidator.TYPE_BEFORE_NEW
				|| (type == ModelValidator.TYPE_BEFORE_CHANGE && po.is_ValueChanged("C_DocTypeTarget_ID"))
				|| (type == ModelValidator.TYPE_BEFORE_CHANGE && po.is_ValueChanged("M_PriceList_ID")))
		{
			MOrder order = (MOrder)po;
			order.setIsSOTrx(order.getC_DocTypeTarget().isSOTrx());
			order.setIsTaxIncluded(order.getM_PriceList().isTaxIncluded());
			order.setC_Currency_ID(order.getM_PriceList().getC_Currency_ID());
		}

		return null;
	}

	@Override
	public String docValidate(PO po, int timing)
	{
		//JPIERE-0227 Create Logical Inventory Move Doc
		if(timing == ModelValidator.TIMING_AFTER_COMPLETE)
		{
			MOrder order = (MOrder)po;
			MDocType docType = MDocType.get(order.getCtx(), order.getC_DocTypeTarget_ID());
			int JP_DocTypeMM_ID = docType.get_ValueAsInt("JP_DocTypeMM_ID");

			if(JP_DocTypeMM_ID > 0 && order.isSOTrx()) //Sales Order Only
			{
				MOrderLine[] oLines = order.getLines();
				MMovement mm = null;
				for(int i = 0; i < oLines.length; i++)
				{
					int JP_LocatorFrom_ID =  oLines[i].get_ValueAsInt("JP_LocatorFrom_ID");
					int JP_LocatorTo_ID =  oLines[i].get_ValueAsInt("JP_LocatorTo_ID");
					int JP_MovementLine_ID = oLines[i].get_ValueAsInt("JP_MovementLine_ID");
					if(JP_LocatorFrom_ID > 0 && JP_LocatorTo_ID > 0 && JP_MovementLine_ID == 0)
					{
						if(mm == null)
						{
							mm = new MMovement(order.getCtx(), 0, order.get_TrxName());
							mm.setAD_Org_ID(order.getAD_Org_ID());
							mm.setC_DocType_ID(JP_DocTypeMM_ID);

							mm.setMovementDate(order.getDateOrdered());
							mm.setSalesRep_ID(order.getSalesRep_ID());
							mm.setC_BPartner_ID(order.getC_BPartner_ID());
							mm.setC_BPartner_Location_ID(order.getC_BPartner_Location_ID());
							mm.setAD_User_ID(order.getAD_User_ID());
							mm.setPOReference(order.getPOReference());

							mm.setC_Project_ID(order.getC_Project_ID());
							mm.setC_Activity_ID(order.getC_Activity_ID());
							mm.setC_Campaign_ID(order.getC_Campaign_ID());

							mm.set_ValueNoCheck("JP_Order_ID", order.getC_Order_ID());
							mm.saveEx(order.get_TrxName());
						}

						MMovementLine line = new MMovementLine(mm);
						line.setLine(oLines[i].getLine());
						line.setDescription(oLines[i].getDescription());
						line.setM_Product_ID(oLines[i].getM_Product_ID());
						line.setM_Locator_ID(JP_LocatorFrom_ID);
						line.setM_LocatorTo_ID(JP_LocatorTo_ID);
						line.setM_AttributeSetInstance_ID(oLines[i].get_ValueAsInt("JP_ASI_From_ID"));
						line.setM_AttributeSetInstanceTo_ID(oLines[i].get_ValueAsInt("JP_ASI_To_ID"));
						line.setMovementQty(oLines[i].getQtyOrdered());
						line.saveEx(order.get_TrxName());
						oLines[i].set_ValueNoCheck("JP_MovementLine_ID", line.getM_MovementLine_ID());
						oLines[i].saveEx(order.get_TrxName());
					}
				}//for

				if(mm !=null)
				{
					mm.processIt(DocAction.ACTION_Complete);//Complete only because of this process instead of quotation.
					mm.saveEx(order.get_TrxName());
					mm = null;
				}

			}

		}

		//JPIERE-0227 Reverse Logical Inventory Move Doc
		if(timing == ModelValidator.TIMING_AFTER_VOID
				||timing == ModelValidator.TIMING_AFTER_REVERSEACCRUAL || timing == ModelValidator.TIMING_AFTER_REVERSECORRECT )
		{
			MOrder order = (MOrder)po;
			if(order.isSOTrx())
			{
				final String whereClause = "SELECT JP_Order_ID =? ";
				List<MMovement> list = new Query(order.getCtx(), I_M_Movement.Table_Name, whereClause, order.get_TrxName())
												.setParameters(order.getC_Order_ID())
												.list();

				for(MMovement mm : list)
				{
					if(mm.getDocStatus().equals(DocAction.STATUS_Completed))
					{
						if(timing == ModelValidator.TIMING_AFTER_REVERSEACCRUAL)
							mm.processIt(DocAction.ACTION_Reverse_Accrual);
						else
							mm.processIt(DocAction.ACTION_Reverse_Correct);

						mm.saveEx(order.get_TrxName());
					}

				}
			}

		}//JPIERE-0227


		return null;
	}

}
