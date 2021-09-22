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
package jpiere.base.plugin.org.adempiere.callout;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Properties;

import org.adempiere.base.IColumnCallout;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.MAttributeSetInstance;
import org.compiere.model.MDocType;
import org.compiere.model.MInOut;
import org.compiere.model.MInOutLine;
import org.compiere.model.MInOutLineMA;
import org.compiere.model.MInventory;
import org.compiere.model.MInventoryLine;
import org.compiere.model.MInventoryLineMA;
import org.compiere.model.MMovementLine;
import org.compiere.model.MMovementLineMA;
import org.compiere.model.MProduction;
import org.compiere.model.MProductionLine;
import org.compiere.model.MProductionLineMA;
import org.compiere.model.MStorageOnHand;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.MPPFactLine;
import jpiere.base.plugin.org.adempiere.model.MPPFactLineMA;


/**
 * JPIERE-0503: Support to enter Attributes Tab Call out
 *
 * @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 *
 */
public class SupportToEnterAttributesTabColumnCallout implements IColumnCallout {


	@Override
	public String start(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{
		String msg = null;

		if(mField.getColumnName().equals("M_AttributeSetInstance_ID"))
		{
			msg = callFromM_AttributeSetInstance_ID(ctx, WindowNo, mTab, mField, value, oldValue);

		}else if(mField.getColumnName().equals("DateMaterialPolicy")) {

			msg = callFromDateMaterialPolicy(ctx, WindowNo, mTab, mField, value, oldValue);

		}

		return msg;

	}
	public String callFromDateMaterialPolicy(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{

		//DateMaterialPolicy
		if(value == null || Util.isEmpty(value.toString()))
		{
			return null;

		}

		int M_AttributeSetInstance_ID = Integer.valueOf(mTab.get_ValueAsString("M_AttributeSetInstance_ID")).intValue();
		if(M_AttributeSetInstance_ID <= 0)
			return null;

		int M_Product_ID = 0;
		int M_Locator_ID = 0;
		Timestamp DateMaterialPolicy = (Timestamp)value;

		String tableName = mTab.getTableName();

		//JPiere PP Doc
		if(tableName.equals(MPPFactLineMA.Table_Name))
		{
			int JP_PP_FactLine_ID = Integer.valueOf(mTab.get_ValueAsString(MPPFactLineMA.COLUMNNAME_JP_PP_FactLine_ID)).intValue();
			MPPFactLine line = new MPPFactLine(ctx, JP_PP_FactLine_ID, null);
			M_Product_ID = line.getM_Product_ID();
			M_Locator_ID = line.getM_Locator_ID();

			//MovementQty(+)
			if(line.getMovementQty().signum() > 0)
			{
				mTab.setValue(MPPFactLineMA.COLUMNNAME_MovementQty,line.getMovementQty());

			//MovementQty(-)
			}else {

				MStorageOnHand storageOnHand = MStorageOnHand.get(ctx, M_Locator_ID, M_Product_ID, M_AttributeSetInstance_ID,DateMaterialPolicy, null);
				if(storageOnHand == null)
				{
					MAttributeSetInstance asi =   new MAttributeSetInstance(ctx, M_AttributeSetInstance_ID, null);
					return Msg.getMsg(ctx, "InsufficientQtyAvailable") +  asi.getDescription()
									+ " - "+  Msg.getElement(ctx, MPPFactLineMA.COLUMNNAME_DateMaterialPolicy) +" : "+ DateMaterialPolicy.toString().substring(0, 10)
									+ " - " +  Msg.getElement(ctx, "QtyOnHand") + " : " + 0;
				}else {

					BigDecimal qtyOnHand = storageOnHand.getQtyOnHand();
					if(qtyOnHand.compareTo(line.getQtyUsed()) >= 0)
					{
						mTab.setValue(MPPFactLineMA.COLUMNNAME_MovementQty,line.getMovementQty());
					}else {

						mTab.setValue(MPPFactLineMA.COLUMNNAME_MovementQty,qtyOnHand.negate());
						MAttributeSetInstance asi =   new MAttributeSetInstance(ctx, M_AttributeSetInstance_ID, null);
						return Msg.getMsg(ctx, "InsufficientQtyAvailable") +  asi.getDescription()
									+ " - "+  Msg.getElement(ctx, MPPFactLineMA.COLUMNNAME_DateMaterialPolicy) +" : "+ DateMaterialPolicy.toString().substring(0, 10)
									+ " - "+  Msg.getElement(ctx, "QtyOnHand") + " : " + qtyOnHand.toString();
					}
				}
			}

			return null;

		//MProduction
		}else if(tableName.equals(MProductionLineMA.Table_Name)) {

			int M_ProductionLine_ID = Integer.valueOf(mTab.get_ValueAsString(MProductionLineMA.COLUMNNAME_M_ProductionLine_ID)).intValue();
			MProductionLine line = new MProductionLine(ctx, M_ProductionLine_ID, null);
			//MProduction doc = new MProduction(ctx, line.getM_Production_ID(), null);
			M_Product_ID = line.getM_Product_ID();
			M_Locator_ID = line.getM_Locator_ID();

			//MovementQty(+)
			if(line.getMovementQty().signum() > 0)
			{
				mTab.setValue(MProductionLineMA.COLUMNNAME_MovementQty,line.getMovementQty());

			//MovementQty(-)
			}else {

				MStorageOnHand storageOnHand = MStorageOnHand.get(ctx, M_Locator_ID, M_Product_ID, M_AttributeSetInstance_ID,DateMaterialPolicy, null);
				if(storageOnHand == null)
				{
					MAttributeSetInstance asi =   new MAttributeSetInstance(ctx, M_AttributeSetInstance_ID, null);
					return Msg.getMsg(ctx, "InsufficientQtyAvailable") +  asi.getDescription()
									+ " - "+  Msg.getElement(ctx, MProductionLineMA.COLUMNNAME_DateMaterialPolicy) +" : "+ DateMaterialPolicy.toString().substring(0, 10)
									+ " - " +  Msg.getElement(ctx, "QtyOnHand") + " : " + 0;
				}else {

					BigDecimal qtyOnHand = storageOnHand.getQtyOnHand();
					if(qtyOnHand.compareTo(line.getQtyUsed()) >= 0)
					{
						mTab.setValue(MProductionLineMA.COLUMNNAME_MovementQty,line.getMovementQty());
					}else {

						mTab.setValue(MProductionLineMA.COLUMNNAME_MovementQty,qtyOnHand.negate());
						MAttributeSetInstance asi =   new MAttributeSetInstance(ctx, M_AttributeSetInstance_ID, null);
						return Msg.getMsg(ctx, "InsufficientQtyAvailable") +  asi.getDescription()
									+ " - "+  Msg.getElement(ctx, MProductionLineMA.COLUMNNAME_DateMaterialPolicy) +" : "+ DateMaterialPolicy.toString().substring(0, 10)
									+ " - "+  Msg.getElement(ctx, "QtyOnHand") + " : " + qtyOnHand.toString();
					}
				}
			}

			return null;

		//MInOut
		}else if(tableName.equals(MInOutLineMA.Table_Name)) {

			int M_InOutLine_ID = Integer.valueOf(mTab.get_ValueAsString(MInOutLineMA.COLUMNNAME_M_InOutLine_ID)).intValue();
			MInOutLine line = new MInOutLine(ctx, M_InOutLine_ID, null);
			MInOut io = line.getParent();
			M_Product_ID = line.getM_Product_ID();
			M_Locator_ID = line.getM_Locator_ID();

			//Receipt & Customer Retern
			if(MDocType.get(io.getC_DocType_ID()).getDocBaseType().equals(MDocType.DOCBASETYPE_MaterialReceipt))
			{
				//MovementQty(+)
				if(line.getMovementQty().signum() > 0)
				{
					mTab.setValue(MInOutLineMA.COLUMNNAME_MovementQty,line.getMovementQty());

				//MovementQty(-)
				}else {

					MStorageOnHand storageOnHand = MStorageOnHand.get(ctx, M_Locator_ID, M_Product_ID, M_AttributeSetInstance_ID,DateMaterialPolicy, null);
					if(storageOnHand == null)
					{
						MAttributeSetInstance asi =   new MAttributeSetInstance(ctx, M_AttributeSetInstance_ID, null);
						return Msg.getMsg(ctx, "InsufficientQtyAvailable") +  asi.getDescription()
										+ " - "+  Msg.getElement(ctx, MInOutLineMA.COLUMNNAME_DateMaterialPolicy) +" : "+ DateMaterialPolicy.toString().substring(0, 10)
										+ " - " +  Msg.getElement(ctx, "QtyOnHand") +" : "+ 0;
					}else {

						BigDecimal qtyOnHand = storageOnHand.getQtyOnHand();
						if(qtyOnHand.compareTo(line.getMovementQty().negate()) >= 0)
						{
							mTab.setValue(MInOutLineMA.COLUMNNAME_MovementQty,line.getMovementQty());
						}else {

							mTab.setValue(MInOutLineMA.COLUMNNAME_MovementQty, qtyOnHand.negate());
							MAttributeSetInstance asi =   new MAttributeSetInstance(ctx, M_AttributeSetInstance_ID, null);
							return Msg.getMsg(ctx, "InsufficientQtyAvailable") +  asi.getDescription()
										+ " - "+  Msg.getElement(ctx, MInOutLineMA.COLUMNNAME_DateMaterialPolicy) +" : "+ DateMaterialPolicy.toString().substring(0, 10)
										+ " - "+  Msg.getElement(ctx, "QtyOnHand") + " : " + qtyOnHand.toString();
						}
					}
				}

			//Shipment & Vendor Return
			}else if(MDocType.get(io.getC_DocType_ID()).getDocBaseType().equals(MDocType.DOCBASETYPE_MaterialDelivery)) {

				//MovementQty(+)
				if(line.getMovementQty().signum() > 0)
				{

					MStorageOnHand storageOnHand = MStorageOnHand.get(ctx, M_Locator_ID, M_Product_ID, M_AttributeSetInstance_ID,DateMaterialPolicy, null);
					if(storageOnHand == null)
					{
						MAttributeSetInstance asi =   new MAttributeSetInstance(ctx, M_AttributeSetInstance_ID, null);
						return Msg.getMsg(ctx, "InsufficientQtyAvailable") +  asi.getDescription()
										+ " - "+  Msg.getElement(ctx, MInOutLineMA.COLUMNNAME_DateMaterialPolicy) +" : "+ DateMaterialPolicy.toString().substring(0, 10)
										+ " - " +  Msg.getElement(ctx, "QtyOnHand") +" : "+ 0;
					}else {

						BigDecimal qtyOnHand = storageOnHand.getQtyOnHand();
						if(qtyOnHand.compareTo(line.getMovementQty()) >= 0)
						{
							mTab.setValue(MInOutLineMA.COLUMNNAME_MovementQty,line.getMovementQty());
						}else {

							mTab.setValue(MInOutLineMA.COLUMNNAME_MovementQty, qtyOnHand);
							MAttributeSetInstance asi =   new MAttributeSetInstance(ctx, M_AttributeSetInstance_ID, null);
							return Msg.getMsg(ctx, "InsufficientQtyAvailable") +  asi.getDescription()
										+ " - "+  Msg.getElement(ctx, MInOutLineMA.COLUMNNAME_DateMaterialPolicy) +" : "+ DateMaterialPolicy.toString().substring(0, 10)
										+ " - "+  Msg.getElement(ctx, "QtyOnHand") + " : " + qtyOnHand.toString();
						}
					}

				//MovementQty(-)
				}else {

					mTab.setValue(MPPFactLineMA.COLUMNNAME_MovementQty,line.getMovementQty());

				}
			}


		//MMovement
		}else if(tableName.equals(MMovementLineMA.Table_Name)) {

			int M_MovementLine_ID = Integer.valueOf(mTab.get_ValueAsString(MMovementLineMA.COLUMNNAME_M_MovementLine_ID)).intValue();
			MMovementLine line = new MMovementLine(ctx, M_MovementLine_ID, null);
			M_Product_ID = line.getM_Product_ID();
			M_Locator_ID = line.getM_Locator_ID();

			//MovementQty(+)
			if(line.getMovementQty().signum() > 0)
			{
				MStorageOnHand storageOnHand = MStorageOnHand.get(ctx, M_Locator_ID, M_Product_ID, M_AttributeSetInstance_ID, DateMaterialPolicy, null);
				if(storageOnHand == null)
				{
					MAttributeSetInstance asi =   new MAttributeSetInstance(ctx, M_AttributeSetInstance_ID, null);
					return Msg.getMsg(ctx, "InsufficientQtyAvailable") +  asi.getDescription()
									+ " - "+  Msg.getElement(ctx, MMovementLineMA.COLUMNNAME_DateMaterialPolicy) +" : "+ DateMaterialPolicy.toString().substring(0, 10)
									+ " - " +  Msg.getElement(ctx, "QtyOnHand") +" : "+ 0;
				}else {

					BigDecimal qtyOnHand = storageOnHand.getQtyOnHand();
					if(qtyOnHand.compareTo(line.getMovementQty()) >= 0)
					{
						mTab.setValue(MMovementLineMA.COLUMNNAME_MovementQty,line.getMovementQty());
					}else {

						mTab.setValue(MMovementLineMA.COLUMNNAME_MovementQty, qtyOnHand);
						MAttributeSetInstance asi =   new MAttributeSetInstance(ctx, M_AttributeSetInstance_ID, null);
						return Msg.getMsg(ctx, "InsufficientQtyAvailable") +  asi.getDescription()
									+ " - "+  Msg.getElement(ctx, MMovementLineMA.COLUMNNAME_DateMaterialPolicy) +" : "+ DateMaterialPolicy.toString().substring(0, 10)
									+ " - "+  Msg.getElement(ctx, "QtyOnHand") + " : " + qtyOnHand.toString();
					}
				}

			//MovementQty(-)
			}else {

				mTab.setValue(MMovementLineMA.COLUMNNAME_MovementQty,line.getMovementQty());
			}

		//MInventory
		}else if(tableName.equals(MInventoryLineMA.Table_Name)) {

			int M_InventoryLine_ID = Integer.valueOf(mTab.get_ValueAsString(MInventoryLineMA.COLUMNNAME_M_InventoryLine_ID)).intValue();
			MInventoryLine line = new MInventoryLine(ctx, M_InventoryLine_ID, null);
			MInventory parent = line.getParent();
			M_Product_ID = line.getM_Product_ID();
			M_Locator_ID = line.getM_Locator_ID();
			MDocType docType = MDocType.get(parent.getC_DocType_ID());

			//Pyshical Inventory
			if(docType.getDocSubTypeInv().equals(MDocType.DOCSUBTYPEINV_PhysicalInventory))
			{
				;// Not impliment now

			//Internal Use
			}else if(docType.getDocSubTypeInv().equals(MDocType.DOCSUBTYPEINV_InternalUseInventory)) {

				//QtyInternalUse(+)
				if(line.getQtyInternalUse().signum() > 0)
				{
					MStorageOnHand storageOnHand = MStorageOnHand.get(ctx, M_Locator_ID, M_Product_ID, M_AttributeSetInstance_ID, DateMaterialPolicy, null);
					if(storageOnHand == null)
					{
						MAttributeSetInstance asi =   new MAttributeSetInstance(ctx, M_AttributeSetInstance_ID, null);
						return Msg.getMsg(ctx, "InsufficientQtyAvailable") +  asi.getDescription()
										+ " - "+  Msg.getElement(ctx, MInventoryLineMA.COLUMNNAME_DateMaterialPolicy) +" : "+ DateMaterialPolicy.toString().substring(0, 10)
										+ " - " +  Msg.getElement(ctx, "QtyOnHand") +" : "+ 0;
					}else {

						BigDecimal qtyOnHand = storageOnHand.getQtyOnHand();
						if(qtyOnHand.compareTo(line.getQtyInternalUse()) >= 0)
						{
							mTab.setValue(MInventoryLineMA.COLUMNNAME_MovementQty,line.getQtyInternalUse());
						}else {

							mTab.setValue(MInventoryLineMA.COLUMNNAME_MovementQty, qtyOnHand);
							MAttributeSetInstance asi =   new MAttributeSetInstance(ctx, M_AttributeSetInstance_ID, null);
							return Msg.getMsg(ctx, "InsufficientQtyAvailable") +  asi.getDescription()
										+ " - "+  Msg.getElement(ctx, MInventoryLineMA.COLUMNNAME_DateMaterialPolicy) +" : "+ DateMaterialPolicy.toString().substring(0, 10)
										+ " - "+  Msg.getElement(ctx, "QtyOnHand") + " : " + qtyOnHand.toString();
						}
					}


				//QtyInternalUse(-)
				}else {

					mTab.setValue(MMovementLineMA.COLUMNNAME_MovementQty,line.getMovementQty());

				}

			}else if(docType.getDocSubTypeInv().equals(MDocType.DOCSUBTYPEINV_CostAdjustment)) {

				;//Not need

			}

		}

		return null;
	}

	public String callFromM_AttributeSetInstance_ID(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{

		int M_AttributeSetInstance_ID = 0;
		int M_Product_ID = 0;
		int M_Locator_ID = 0;
		Timestamp DateMaterialPolicy = null;

		if(value == null || Util.isEmpty(value.toString()))
		{
			return null;

		}else {

			M_AttributeSetInstance_ID = Integer.valueOf(value.toString());
			if(M_AttributeSetInstance_ID <= 0)
				return null;
		}

		String tableName = mTab.getTableName();

		//JPiere PP Doc
		if(tableName.equals(MPPFactLineMA.Table_Name))
		{

			int JP_PP_FactLine_ID = Integer.valueOf(mTab.get_ValueAsString(MPPFactLineMA.COLUMNNAME_JP_PP_FactLine_ID)).intValue();
			MPPFactLine line = new MPPFactLine(ctx, JP_PP_FactLine_ID, null);
			M_Product_ID = line.getM_Product_ID();
			M_Locator_ID = line.getM_Locator_ID();

			//MovementQty(+) : In case of By-Product
			if(line.getMovementQty().signum() > 0)
			{
				mTab.setValue(MPPFactLineMA.COLUMNNAME_DateMaterialPolicy, line.getParent().getMovementDate());
				mTab.setValue(MPPFactLineMA.COLUMNNAME_MovementQty,line.getMovementQty());

			}else {

				DateMaterialPolicy = MStorageOnHand.getDateMaterialPolicy(M_Product_ID, M_AttributeSetInstance_ID, M_Locator_ID, null);
				if(DateMaterialPolicy == null)
				{
					mTab.setValue(MPPFactLineMA.COLUMNNAME_DateMaterialPolicy, line.getParent().getMovementDate());
					mTab.setValue(MPPFactLineMA.COLUMNNAME_MovementQty, line.getMovementQty());
					MAttributeSetInstance asi =   new MAttributeSetInstance(ctx, M_AttributeSetInstance_ID, null);
					return Msg.getMsg(ctx, "InsufficientQtyAvailable") +  asi.getDescription() + " : " +  Msg.getElement(ctx, "QtyOnHand") +" 0 ";

				}else {

					mTab.setValue(MPPFactLineMA.COLUMNNAME_DateMaterialPolicy, DateMaterialPolicy);
					MStorageOnHand storageOnHand = MStorageOnHand.get(ctx, M_Locator_ID, M_Product_ID, M_AttributeSetInstance_ID, DateMaterialPolicy, null);
					BigDecimal qtyOnHand = storageOnHand.getQtyOnHand();
					if(qtyOnHand.compareTo(line.getQtyUsed()) >= 0)
					{
						mTab.setValue(MPPFactLineMA.COLUMNNAME_MovementQty,line.getMovementQty());
					}else {

						mTab.setValue(MPPFactLineMA.COLUMNNAME_MovementQty,qtyOnHand.negate());
						MAttributeSetInstance asi =   new MAttributeSetInstance(ctx, M_AttributeSetInstance_ID, null);
						return Msg.getMsg(ctx, "InsufficientQtyAvailable") +  asi.getDescription()
											+ " - "+  Msg.getElement(ctx, MPPFactLineMA.COLUMNNAME_DateMaterialPolicy) + " : " + DateMaterialPolicy.toString().substring(0, 10)
											+ " - "+  Msg.getElement(ctx, "QtyOnHand") + " : " + qtyOnHand.toString();
					}
				}
			}

			return null;

		//MProduction
		}else if(tableName.equals(MProductionLineMA.Table_Name)) {

			int M_ProductionLine_ID = Integer.valueOf(mTab.get_ValueAsString(MProductionLineMA.COLUMNNAME_M_ProductionLine_ID)).intValue();
			MProductionLine line = new MProductionLine(ctx, M_ProductionLine_ID, null);
			MProduction doc = new MProduction(ctx, line.getM_Production_ID(), null);
			M_Product_ID = line.getM_Product_ID();
			M_Locator_ID = line.getM_Locator_ID();

			//MovementQty(+) : In case of By-Product
			if(line.getMovementQty().signum() > 0)
			{
				mTab.setValue(MProductionLineMA.COLUMNNAME_DateMaterialPolicy, doc.getMovementDate());
				mTab.setValue(MProductionLineMA.COLUMNNAME_MovementQty,line.getMovementQty());

			}else {

				DateMaterialPolicy = MStorageOnHand.getDateMaterialPolicy(M_Product_ID, M_AttributeSetInstance_ID, M_Locator_ID, null);
				if(DateMaterialPolicy == null)
				{
					mTab.setValue(MProductionLineMA.COLUMNNAME_DateMaterialPolicy, doc.getMovementDate());
					mTab.setValue(MProductionLineMA.COLUMNNAME_MovementQty, line.getMovementQty());
					MAttributeSetInstance asi =   new MAttributeSetInstance(ctx, M_AttributeSetInstance_ID, null);
					return Msg.getMsg(ctx, "InsufficientQtyAvailable") +  asi.getDescription() + " : " +  Msg.getElement(ctx, "QtyOnHand") +" 0 ";

				}else {

					mTab.setValue(MProductionLineMA.COLUMNNAME_DateMaterialPolicy, DateMaterialPolicy);
					MStorageOnHand storageOnHand = MStorageOnHand.get(ctx, M_Locator_ID, M_Product_ID, M_AttributeSetInstance_ID, DateMaterialPolicy, null);
					BigDecimal qtyOnHand = storageOnHand.getQtyOnHand();
					if(qtyOnHand.compareTo(line.getQtyUsed()) >= 0)
					{
						mTab.setValue(MProductionLineMA.COLUMNNAME_MovementQty,line.getMovementQty());
					}else {

						mTab.setValue(MProductionLineMA.COLUMNNAME_MovementQty,qtyOnHand.negate());
						MAttributeSetInstance asi =   new MAttributeSetInstance(ctx, M_AttributeSetInstance_ID, null);
						return Msg.getMsg(ctx, "InsufficientQtyAvailable") +  asi.getDescription()
											+ " - "+  Msg.getElement(ctx, MProductionLineMA.COLUMNNAME_DateMaterialPolicy) + " : " + DateMaterialPolicy.toString().substring(0, 10)
											+ " - "+  Msg.getElement(ctx, "QtyOnHand") + " : " + qtyOnHand.toString();
					}
				}
			}

			return null;


		//MInOut
		}else if(tableName.equals(MInOutLineMA.Table_Name)) {

			int M_InOutLine_ID = Integer.valueOf(mTab.get_ValueAsString(MInOutLineMA.COLUMNNAME_M_InOutLine_ID)).intValue();
			MInOutLine line = new MInOutLine(ctx, M_InOutLine_ID, null);
			MInOut parent = line.getParent();
			M_Product_ID = line.getM_Product_ID();
			M_Locator_ID = line.getM_Locator_ID();

			//Receipt & Customer Retern
			if(MDocType.get(parent.getC_DocType_ID()).getDocBaseType().equals(MDocType.DOCBASETYPE_MaterialReceipt))
			{
				//MovementQty(+)
				if(line.getMovementQty().signum() > 0)
				{
					mTab.setValue(MInOutLineMA.COLUMNNAME_DateMaterialPolicy, parent.getMovementDate());
					mTab.setValue(MInOutLineMA.COLUMNNAME_MovementQty,line.getMovementQty());

				//MovementQty(-)
				}else {

					DateMaterialPolicy = MStorageOnHand.getDateMaterialPolicy(M_Product_ID, M_AttributeSetInstance_ID, M_Locator_ID, null);
					if(DateMaterialPolicy == null)
					{
						mTab.setValue(MPPFactLineMA.COLUMNNAME_DateMaterialPolicy, line.getParent().getMovementDate());
						mTab.setValue(MPPFactLineMA.COLUMNNAME_MovementQty, line.getMovementQty());
						MAttributeSetInstance asi =   new MAttributeSetInstance(ctx, M_AttributeSetInstance_ID, null);
						return Msg.getMsg(ctx, "InsufficientQtyAvailable") +  asi.getDescription() + " : " +  Msg.getElement(ctx, "QtyOnHand") +" 0 ";

					}else {

						mTab.setValue(MPPFactLineMA.COLUMNNAME_DateMaterialPolicy, DateMaterialPolicy);
						MStorageOnHand storageOnHand = MStorageOnHand.get(ctx, M_Locator_ID, M_Product_ID, M_AttributeSetInstance_ID, DateMaterialPolicy, null);
						BigDecimal qtyOnHand = storageOnHand.getQtyOnHand();
						if(qtyOnHand.compareTo(line.getMovementQty().negate()) >= 0)
						{
							mTab.setValue(MInOutLineMA.COLUMNNAME_MovementQty,line.getMovementQty());
						}else {

							mTab.setValue(MInOutLineMA.COLUMNNAME_MovementQty,qtyOnHand.negate());
							MAttributeSetInstance asi =   new MAttributeSetInstance(ctx, M_AttributeSetInstance_ID, null);
							return Msg.getMsg(ctx, "InsufficientQtyAvailable") +  asi.getDescription()
												+ " - "+  Msg.getElement(ctx, MInOutLineMA.COLUMNNAME_DateMaterialPolicy) + " : " + DateMaterialPolicy.toString().substring(0, 10)
												+ " - "+  Msg.getElement(ctx, "QtyOnHand") + " : " + qtyOnHand.toString();
						}
					}

				}

			//Shipment & Vendor Return
			}else if(MDocType.get(parent.getC_DocType_ID()).getDocBaseType().equals(MDocType.DOCBASETYPE_MaterialDelivery)) {

				//MovementQty(+)
				if(line.getMovementQty().signum() > 0)
				{

					DateMaterialPolicy = MStorageOnHand.getDateMaterialPolicy(M_Product_ID, M_AttributeSetInstance_ID, M_Locator_ID, null);
					if(DateMaterialPolicy == null)
					{
						mTab.setValue(MInOutLineMA.COLUMNNAME_DateMaterialPolicy, line.getParent().getMovementDate());
						mTab.setValue(MInOutLineMA.COLUMNNAME_MovementQty, line.getMovementQty());
						MAttributeSetInstance asi =   new MAttributeSetInstance(ctx, M_AttributeSetInstance_ID, null);
						return Msg.getMsg(ctx, "InsufficientQtyAvailable") +  asi.getDescription() + " : " +  Msg.getElement(ctx, "QtyOnHand") +" 0 ";

					}else {

						mTab.setValue(MInOutLineMA.COLUMNNAME_DateMaterialPolicy, DateMaterialPolicy);
						MStorageOnHand storageOnHand = MStorageOnHand.get(ctx, M_Locator_ID, M_Product_ID, M_AttributeSetInstance_ID, DateMaterialPolicy, null);
						BigDecimal qtyOnHand = storageOnHand.getQtyOnHand();
						if(qtyOnHand.compareTo(line.getMovementQty()) >= 0)
						{
							mTab.setValue(MInOutLineMA.COLUMNNAME_MovementQty,line.getMovementQty());
						}else {

							mTab.setValue(MInOutLineMA.COLUMNNAME_MovementQty,qtyOnHand);
							MAttributeSetInstance asi =   new MAttributeSetInstance(ctx, M_AttributeSetInstance_ID, null);
							return Msg.getMsg(ctx, "InsufficientQtyAvailable") +  asi.getDescription()
												+ " - "+  Msg.getElement(ctx, MInOutLineMA.COLUMNNAME_DateMaterialPolicy) + " : " + DateMaterialPolicy.toString().substring(0, 10)
												+ " - "+  Msg.getElement(ctx, "QtyOnHand") + " : " + qtyOnHand.toString();
						}
					}


				//MovementQty(-)
				}else {

					mTab.setValue(MInOutLineMA.COLUMNNAME_DateMaterialPolicy, parent.getMovementDate());
					mTab.setValue(MPPFactLineMA.COLUMNNAME_MovementQty,line.getMovementQty());

				}
			}


		//MMovement
		}else if(tableName.equals(MMovementLineMA.Table_Name)) {

			int M_MovementLine_ID = Integer.valueOf(mTab.get_ValueAsString(MMovementLineMA.COLUMNNAME_M_MovementLine_ID)).intValue();
			MMovementLine line = new MMovementLine(ctx, M_MovementLine_ID, null);
			M_Product_ID = line.getM_Product_ID();
			M_Locator_ID = line.getM_Locator_ID();

			//MovementQty(+)
			if(line.getMovementQty().signum() > 0)
			{
				DateMaterialPolicy = MStorageOnHand.getDateMaterialPolicy(M_Product_ID, M_AttributeSetInstance_ID, M_Locator_ID, null);
				if(DateMaterialPolicy == null)
				{
					mTab.setValue(MInOutLineMA.COLUMNNAME_DateMaterialPolicy, line.getParent().getMovementDate());
					mTab.setValue(MInOutLineMA.COLUMNNAME_MovementQty, line.getMovementQty());
					MAttributeSetInstance asi =   new MAttributeSetInstance(ctx, M_AttributeSetInstance_ID, null);
					return Msg.getMsg(ctx, "InsufficientQtyAvailable") +  asi.getDescription() + " : " +  Msg.getElement(ctx, "QtyOnHand") +" 0 ";

				}else {

					mTab.setValue(MInOutLineMA.COLUMNNAME_DateMaterialPolicy, DateMaterialPolicy);
					MStorageOnHand storageOnHand = MStorageOnHand.get(ctx, M_Locator_ID, M_Product_ID, M_AttributeSetInstance_ID, DateMaterialPolicy, null);
					BigDecimal qtyOnHand = storageOnHand.getQtyOnHand();
					if(qtyOnHand.compareTo(line.getMovementQty()) >= 0)
					{
						mTab.setValue(MInOutLineMA.COLUMNNAME_MovementQty,line.getMovementQty());
					}else {

						mTab.setValue(MInOutLineMA.COLUMNNAME_MovementQty,qtyOnHand);
						MAttributeSetInstance asi =   new MAttributeSetInstance(ctx, M_AttributeSetInstance_ID, null);
						return Msg.getMsg(ctx, "InsufficientQtyAvailable") +  asi.getDescription()
											+ " - "+  Msg.getElement(ctx, MInOutLineMA.COLUMNNAME_DateMaterialPolicy) + " : " + DateMaterialPolicy.toString().substring(0, 10)
											+ " - "+  Msg.getElement(ctx, "QtyOnHand") + " : " + qtyOnHand.toString();
					}
				}

			//MovementQty(-)
			}else {

				DateMaterialPolicy = MStorageOnHand.getDateMaterialPolicy(M_Product_ID, M_AttributeSetInstance_ID, M_Locator_ID, null);
				if(DateMaterialPolicy == null)
				{
					mTab.setValue(MInOutLineMA.COLUMNNAME_DateMaterialPolicy, line.getParent().getMovementDate());

				}else {

					mTab.setValue(MInOutLineMA.COLUMNNAME_DateMaterialPolicy, DateMaterialPolicy);

				}

				mTab.setValue(MInOutLineMA.COLUMNNAME_MovementQty,line.getMovementQty());

				return null;

			}


		//MInventory
		}else if(tableName.equals(MInventoryLineMA.Table_Name)) {

			int M_InventoryLine_ID = Integer.valueOf(mTab.get_ValueAsString(MInventoryLineMA.COLUMNNAME_M_InventoryLine_ID)).intValue();
			MInventoryLine line = new MInventoryLine(ctx, M_InventoryLine_ID, null);
			MInventory parent = line.getParent();
			M_Product_ID = line.getM_Product_ID();
			M_Locator_ID = line.getM_Locator_ID();
			MDocType docType = MDocType.get(parent.getC_DocType_ID());

			//Receipt & Customer Retern
			if(docType.getDocSubTypeInv().equals(MDocType.DOCSUBTYPEINV_PhysicalInventory))
			{
				;// Not impliment now
			}else if(docType.getDocSubTypeInv().equals(MDocType.DOCSUBTYPEINV_InternalUseInventory)) {

				//QtyInternalUse(+)
				if(line.getQtyInternalUse().signum() > 0)
				{
					DateMaterialPolicy = MStorageOnHand.getDateMaterialPolicy(M_Product_ID, M_AttributeSetInstance_ID, M_Locator_ID, null);
					if(DateMaterialPolicy == null)
					{
						mTab.setValue(MInventoryLineMA.COLUMNNAME_DateMaterialPolicy, line.getParent().getMovementDate());
						mTab.setValue(MInventoryLineMA.COLUMNNAME_MovementQty, line.getQtyInternalUse());
						MAttributeSetInstance asi =   new MAttributeSetInstance(ctx, M_AttributeSetInstance_ID, null);
						return Msg.getMsg(ctx, "InsufficientQtyAvailable") +  asi.getDescription() + " : " +  Msg.getElement(ctx, "QtyOnHand") +" 0 ";

					}else {

						mTab.setValue(MInventoryLineMA.COLUMNNAME_DateMaterialPolicy, DateMaterialPolicy);
						MStorageOnHand storageOnHand = MStorageOnHand.get(ctx, M_Locator_ID, M_Product_ID, M_AttributeSetInstance_ID, DateMaterialPolicy, null);
						BigDecimal qtyOnHand = storageOnHand.getQtyOnHand();
						if(qtyOnHand.compareTo(line.getQtyInternalUse()) >= 0)
						{
							mTab.setValue(MInventoryLineMA.COLUMNNAME_MovementQty,line.getQtyInternalUse());

						}else {

							mTab.setValue(MInventoryLineMA.COLUMNNAME_MovementQty,qtyOnHand);
							MAttributeSetInstance asi =   new MAttributeSetInstance(ctx, M_AttributeSetInstance_ID, null);
							return Msg.getMsg(ctx, "InsufficientQtyAvailable") +  asi.getDescription()
												+ " - "+  Msg.getElement(ctx, MInventoryLineMA.COLUMNNAME_DateMaterialPolicy) + " : " + DateMaterialPolicy.toString().substring(0, 10)
												+ " - "+  Msg.getElement(ctx, "QtyOnHand") + " : " + qtyOnHand.toString();
						}
					}

				//QtyInternalUse(-)
				}else {

					DateMaterialPolicy = MStorageOnHand.getDateMaterialPolicy(M_Product_ID, M_AttributeSetInstance_ID, M_Locator_ID, null);
					if(DateMaterialPolicy == null)
					{
						mTab.setValue(MInventoryLineMA.COLUMNNAME_DateMaterialPolicy, line.getParent().getMovementDate());

					}else {

						mTab.setValue(MInventoryLineMA.COLUMNNAME_DateMaterialPolicy, DateMaterialPolicy);

					}

					mTab.setValue(MInventoryLineMA.COLUMNNAME_MovementQty,line.getQtyInternalUse());

				}

			}else if(docType.getDocSubTypeInv().equals(MDocType.DOCSUBTYPEINV_CostAdjustment)) {

				;//Not need;

			}
		}

		return null;
	}

}
