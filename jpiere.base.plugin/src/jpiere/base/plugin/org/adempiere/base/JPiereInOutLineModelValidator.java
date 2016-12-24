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


import org.compiere.model.MClient;
import org.compiere.model.MDocType;
import org.compiere.model.MInOutConfirm;
import org.compiere.model.MInOutLine;
import org.compiere.model.MSysConfig;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.Msg;

public class JPiereInOutLineModelValidator implements ModelValidator {

	private static CLogger log = CLogger.getCLogger(JPiereInOutLineModelValidator.class);
	private int AD_Client_ID = -1;
	private int AD_Org_ID = -1;
	private int AD_Role_ID = -1;
	private int AD_User_ID = -1;

	@Override
	public void initialize(ModelValidationEngine engine, MClient client) {
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();
		engine.addModelChange(MInOutLine.Table_Name, this);

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
	public String modelChange(PO po, int type) throws Exception
	{


		if(type == ModelValidator.TYPE_BEFORE_NEW || type == ModelValidator.TYPE_BEFORE_CHANGE)
		{
			//JPIERE-0211
			MInOutLine iol = (MInOutLine)po;
			if(iol.getC_OrderLine_ID() > 0 && (iol.is_ValueChanged("M_Product_ID") || iol.is_ValueChanged("C_OrderLine_ID")))
			{
				if(iol.getM_Product_ID() != iol.getC_OrderLine().getM_Product_ID())
				{
					return Msg.getMsg(iol.getCtx(), "JP_ProductOfOrderAndInOutDiffer");//Product of Ship/Receipt Line is different from Product of Order Line
				}

			}


			//JPIERE-0212
			if(MSysConfig.getBooleanValue("JP_CHECK_INOUTLINE_CONFIRM", false,  iol.getAD_Client_ID(), iol.getAD_Org_ID()))
			{
				MInOutConfirm[] ioConfirms =  iol.getParent().getConfirmations(false);
				if(ioConfirms.length > 0)
				{
					if(type == ModelValidator.TYPE_BEFORE_NEW)
					{
						return Msg.getMsg(iol.getCtx(), "JP_CanNotAddLineForConfirmations");//You can not add a line because of Confirmations.

					}else if(type == ModelValidator.TYPE_BEFORE_CHANGE && iol.is_ValueChanged("QtyEntered")){

						return Msg.getMsg(iol.getCtx(), "JP_CanNotChangeQtyForConfirmations");//You can not change Qty because of Confirmations.
					}
				}
			}

		}


		//JPIERE-0225:Match PO control - Check Doc Base Type
		if(type == ModelValidator.TYPE_BEFORE_NEW ||
				(type == ModelValidator.TYPE_BEFORE_CHANGE && po.is_ValueChanged("C_OrderLine_ID")) )
		{
			MInOutLine iol = (MInOutLine)po;
			if(iol.getC_OrderLine_ID() > 0)
			{
				if(iol.getParent().getC_DocType().getDocBaseType().equals(MDocType.DOCBASETYPE_MaterialReceipt))//MMR
				{
					if(!iol.getC_OrderLine().getC_Order().getC_DocType().getDocBaseType().equals(MDocType.DOCBASETYPE_PurchaseOrder))//POO
					{
						return Msg.getMsg(iol.getCtx(), "JP_Can_Not_Match_Because_DocType") +
								Msg.getMsg(iol.getCtx(), "JP_MMR_MATCH_POO_ONLY");//MMR of Doc Base Type can match POO of Doc Base type only.
					}
				}else if(iol.getParent().getC_DocType().getDocBaseType().equals(MDocType.DOCBASETYPE_MaterialDelivery)){//MMS

					if(!iol.getC_OrderLine().getC_Order().getC_DocType().getDocBaseType().equals(MDocType.DOCBASETYPE_SalesOrder))//SOO
					{
						return Msg.getMsg(iol.getCtx(), "JP_Can_Not_Match_Because_DocType") +
								Msg.getMsg(iol.getCtx(), "JP_MMS_MATCH_SOO_ONLY");//MMS of Doc Base Type can match SOO of Doc Base type only.
					}
				}

			}
		}

		//JPIERE-0225:Match PO Return control - Check Doc Base Type
		if(type == ModelValidator.TYPE_BEFORE_NEW ||
				(type == ModelValidator.TYPE_BEFORE_CHANGE && po.is_ValueChanged("M_RMALine_ID")) )
		{
			MInOutLine iol = (MInOutLine)po;
			if(iol.getM_RMALine_ID() > 0)
			{
				if(iol.getParent().getC_DocType().getDocBaseType().equals(MDocType.DOCBASETYPE_MaterialReceipt))//MMR
				{
					if( !(iol.getM_RMALine().getM_RMA().getC_DocType().getDocBaseType().equals(MDocType.DOCBASETYPE_SalesOrder) //SOO
							&& iol.getM_RMALine().getM_RMA().getC_DocType().getDocSubTypeSO().equals(MDocType.DOCSUBTYPESO_ReturnMaterial)) )//RM
					{
						return Msg.getMsg(iol.getCtx(), "JP_Can_Not_Match_Because_DocType");//You can not Match Because of wrong Doc Type.
					}
				}else if(iol.getParent().getC_DocType().getDocBaseType().equals(MDocType.DOCBASETYPE_MaterialDelivery)){//MMS

					if( !(iol.getM_RMALine().getM_RMA().getC_DocType().getDocBaseType().equals(MDocType.DOCBASETYPE_PurchaseOrder) //POO
							&& iol.getM_RMALine().getM_RMA().getC_DocType().getDocSubTypeSO().equals(MDocType.DOCSUBTYPESO_ReturnMaterial)) )//RM
					{
						return Msg.getMsg(iol.getCtx(), "JP_Can_Not_Match_Because_DocType");//You can not Match Because of wrong Doc Type.
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
