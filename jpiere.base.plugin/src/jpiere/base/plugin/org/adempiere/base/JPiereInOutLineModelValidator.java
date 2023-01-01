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
import java.util.logging.Level;

import org.adempiere.webui.window.Dialog;
import org.compiere.model.MClient;
import org.compiere.model.MDocType;
import org.compiere.model.MInOutConfirm;
import org.compiere.model.MInOutLine;
import org.compiere.model.MLocator;
import org.compiere.model.MOrderLine;
import org.compiere.model.MSysConfig;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfo;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

/**
*
* JPiere InOut Line Model Validator
*
* JPIERE-0211: Check Product of Order Line and InOut Line.
* JPIERE-0212: Check InOutLineConfirm
* JPIERE-0225: Match PO control
* JPIERE-0294: Explode BOM
* JPIERE-0317: Physical Warehouse
* JPIERE-0376: Check Over Qty Delivered
* JPIERE-0573: Copy Communication Column
*
* @author h.hagiwara
*
*/
public class JPiereInOutLineModelValidator implements ModelValidator {

	private static CLogger log = CLogger.getCLogger(JPiereInOutLineModelValidator.class);
	private int AD_Client_ID = -1;

	@Override
	public void initialize(ModelValidationEngine engine, MClient client)
	{
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();
		engine.addModelChange(MInOutLine.Table_Name, this);

		if (log.isLoggable(Level.FINE)) log.fine("Initialize JPiereInOutLineModelValidator");

	}

	@Override
	public int getAD_Client_ID() {
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


			//JPIERE-0212:Check InOutLineConfirm
			if(MSysConfig.getBooleanValue("JP_CHECK_INOUTLINE_CONFIRM", false,  iol.getAD_Client_ID(), iol.getAD_Org_ID()))
			{
				MInOutConfirm[] ioConfirms =  iol.getParent().getConfirmations(true);
				if(ioConfirms.length > 0)
				{
					if(type == ModelValidator.TYPE_BEFORE_NEW)
					{
						return Msg.getMsg(iol.getCtx(), "JP_CanNotAddLineForConfirmations");//You can not add a line because of Confirmations.

					} else if(type == ModelValidator.TYPE_BEFORE_CHANGE && iol.is_ValueChanged("QtyEntered")
							&& !iol.getParent().getDocAction().equals(DocAction.ACTION_Void)){

						if(iol.getParent().getC_DocType().isSplitWhenDifference())
						{
							;// Can not check. Because, In Case of Split , InOut Cnfirm update QtyEntered When Complete. JPBP #108 -  2017/9/30
						}else{
							return Msg.getMsg(iol.getCtx(), "JP_CanNotChangeQtyForConfirmations");//You can not change Qty because of Confirmations.
						}
					}
				}
			}


			//JPIERE-0317 Physical Warehouse - check same physical warehouse between locator and document.
			if(MSysConfig.getBooleanValue("JP_INOUT_PHYWH_LOCATOR_CHECK", true, iol.getAD_Client_ID(), iol.getAD_Org_ID()))
			{
				if(type == ModelValidator.TYPE_BEFORE_NEW || type == ModelValidator.TYPE_BEFORE_CHANGE && iol.is_ValueChanged("M_Locator_ID"))
				{
					int io_PhysicalWarehouse_ID = iol.getParent().get_ValueAsInt("JP_PhysicalWarehouse_ID");
					if(io_PhysicalWarehouse_ID != 0)
					{
						MLocator loc = MLocator.get(iol.getCtx(), iol.getM_Locator_ID());
						int loc_PhysicalWarehouse_ID =  loc.get_ValueAsInt("JP_PhysicalWarehouse_ID");
						if(loc_PhysicalWarehouse_ID != 0 && loc_PhysicalWarehouse_ID != io_PhysicalWarehouse_ID)
						{
							return Msg.getMsg(iol.getCtx(), "JP_PhyWarehouseLocatorConflict");//Conflict Physical Warehouse between document and Locator
						}
					}
				}
			}//JPIERE-0317 Physical Warehouse

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

						if(iol.getParent().isDropShip() && iol.getC_OrderLine().getC_Order().getC_DocType().getDocBaseType().equals(MDocType.DOCBASETYPE_PurchaseOrder)
								&& iol.getC_OrderLine().getLink_OrderLine_ID() > 0) //Progress on Dropship process.
						{
							;//Noting to do. besause This check is not covered Dropship process. - 2018/6/8

						}else {

							return Msg.getMsg(iol.getCtx(), "JP_Can_Not_Match_Because_DocType") +
									Msg.getMsg(iol.getCtx(), "JP_MMS_MATCH_SOO_ONLY");//MMS of Doc Base Type can match SOO of Doc Base type only.
						}

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


		//JPIERE-0294: Explode BOM
		if(type == ModelValidator.TYPE_BEFORE_NEW ||
				(type == ModelValidator.TYPE_BEFORE_CHANGE && po.is_ValueChanged("M_Product_ID") ) )
		{
			MInOutLine iol = (MInOutLine)po;
			if(iol.getM_Product_ID() == 0)
			{
				iol.set_ValueNoCheck("JP_ProductExplodeBOM_ID", null);

			}else if(iol.getC_OrderLine_ID() > 0) {

				MOrderLine ol = new MOrderLine(iol.getCtx(),iol.getC_OrderLine_ID(),iol.get_TrxName());
				if(ol.get_Value("JP_ProductExplodeBOM_ID") != null)
				{

					if(iol.getM_Product_ID() == ol.getM_Product_ID())
					{
						iol.set_ValueNoCheck("JP_ProductExplodeBOM_ID", ol.get_Value("JP_ProductExplodeBOM_ID"));
					}else {

						return Msg.getMsg(iol.getCtx(), "JP_ProductOfOrderAndInOutDiffer");
					}
				}else {
					iol.set_ValueNoCheck("JP_ProductExplodeBOM_ID", null);
				}

			}else if(iol.getM_RMALine_ID() > 0) {

				MInOutLine originalIOLine = new MInOutLine(iol.getCtx(), iol.getM_RMALine().getM_InOutLine_ID(),iol.get_TrxName());
				if(originalIOLine.get_Value("JP_ProductExplodeBOM_ID") != null)
				{

					if(iol.getM_Product_ID() == originalIOLine.getM_Product_ID())
					{
						iol.set_ValueNoCheck("JP_ProductExplodeBOM_ID", originalIOLine.get_Value("JP_ProductExplodeBOM_ID"));
					}else {

						//Different between {0} and {1}
						String msg0 = Msg.getElement(Env.getCtx(), "M_RMALine_ID")+" - " + Msg.getElement(Env.getCtx(), "M_Product_ID");
						String msg1 = Msg.getElement(Env.getCtx(), "M_InOutLine_ID")+" - " + Msg.getElement(Env.getCtx(), "M_Product_ID");
						return Msg.getMsg(Env.getCtx(),"JP_Different",new Object[]{msg0,msg1});
					}
				}else {
					iol.set_ValueNoCheck("JP_ProductExplodeBOM_ID", null);
				}
			}
		}


		//JPIERE-0376:Check Over Qty Delivered
		if(type == ModelValidator.TYPE_BEFORE_NEW ||
				(type == ModelValidator.TYPE_BEFORE_CHANGE && po.is_ValueChanged("MovementQty") ) )
		{
			MInOutLine iol = (MInOutLine)po;
			ProcessInfo pInfo = Env.getProcessInfo(Env.getCtx());
			if(pInfo == null && iol.getC_OrderLine_ID() > 0)
			{
				BigDecimal movementQty  = iol.getMovementQty();
				BigDecimal qtyDelivered = iol.getC_OrderLine().getQtyDelivered();
				BigDecimal qtyOrdered = iol.getC_OrderLine().getQtyOrdered();
				BigDecimal qtyToDelivere = qtyOrdered.subtract(qtyDelivered);
				if(qtyOrdered.signum() >= 0)
				{

					if(movementQty.compareTo(qtyToDelivere) > 0)
					{
						try {
							Dialog.info(0, null, "JP_ToBeConfirmed", Msg.getMsg(po.getCtx(), "JP_Over_QtyDelivered_Possibility")
									+" : "+ iol.getParent().getDocumentNo() +  " - " + iol.getLine());
						}catch(Exception e) {
							;//ignore
						}

					}

				}else {

					if(movementQty.compareTo(qtyToDelivere) < 0)
					{
						try {
							Dialog.info(0, null, "JP_ToBeConfirmed", Msg.getMsg(po.getCtx(), "JP_Over_QtyDelivered_Possibility")
									+" : "+ iol.getParent().getDocumentNo() +  " - " + iol.getLine());
						}catch(Exception e) {
							;//ignore
						}
					}
				}

			}else if(pInfo == null && iol.getM_RMALine_ID() > 0) {

				BigDecimal movementQty  = iol.getMovementQty();
				BigDecimal qtyDelivered = iol.getM_RMALine().getQtyDelivered();
				BigDecimal qtyRMA = iol.getM_RMALine().getQty();
				BigDecimal qtyToDeliver = qtyRMA.subtract(qtyDelivered);

				if(qtyRMA.signum() >= 0)
				{
					if(movementQty.compareTo(qtyToDeliver) > 0)
					{
						try {
							Dialog.info(0, null, "JP_ToBeConfirmed", Msg.getMsg(po.getCtx(), "JP_Over_QtyDelivered_Possibility")
									+" : "+ iol.getParent().getDocumentNo() +  " - " + iol.getLine());
						}catch(Exception e) {
							;//ignore
						}
					}

				}else {

					if(movementQty.compareTo(qtyToDeliver) < 0)
					{
						try {
							Dialog.info(0, null, "JP_ToBeConfirmed", Msg.getMsg(po.getCtx(), "JP_Over_QtyDelivered_Possibility")
									+" : "+ iol.getParent().getDocumentNo() +  " - " + iol.getLine());
						}catch(Exception e) {
							;//ignore
						}
					}
				}

			}

		}//JPIERE-0376
		
		
		//JPIERE-0573 : Copy Subject and Remarks,Communication Column.
		if(type == ModelValidator.TYPE_BEFORE_NEW || po.is_ValueChanged("C_OrderLine_ID"))
		{
			MInOutLine iol = (MInOutLine)po;
			if(iol.getC_OrderLine_ID() > 0)
			{
				MOrderLine order = new MOrderLine(po.getCtx(),iol.getC_OrderLine_ID(), po.get_TrxName());

				if(Util.isEmpty(iol.get_ValueAsString("JP_CommunicationColumn")))
				{
					iol.set_ValueNoCheck("JP_CommunicationColumn", order.get_ValueAsString("JP_CommunicationColumn"));
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
