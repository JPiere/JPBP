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
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.logging.Level;

import org.compiere.model.MAcctSchema;
import org.compiere.model.MCharge;
import org.compiere.model.MClient;
import org.compiere.model.MCurrency;
import org.compiere.model.MDocType;
import org.compiere.model.MLocator;
import org.compiere.model.MOrderLine;
import org.compiere.model.MOrgInfo;
import org.compiere.model.MProduct;
import org.compiere.model.MSysConfig;
import org.compiere.model.MTax;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.model.ProductCost;
import org.compiere.process.DocAction;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;

import jpiere.base.plugin.org.adempiere.model.JPiereTaxProvider;
import jpiere.base.plugin.util.JPiereUtil;

/**
*
* JPiere Order Line model Validator
*
* JPIERE-0165: Tax amt at Line and IJPiereTaxProvider
* JPIERE-0202: Set Product Cost at Order Line and Estmation Line
* JPIERE-0207: Check Re-Active at Order
* JPIERE-0227: Common Warehouse
* JPIERE-0317: Physical Warehouse
* JPIERE-0334: Locator Level Reserved
* JPIERE-0369: Mix Tax Include or Exclude at Line a Doc.
* JPIERE-0375: Check Over Qty Invoiced
* JPIERE-0376: Check Over Qty Delivered
* JPIERE-0377: Check Over Qty Recognized
* JPIERE-0409: Set Counter Doc Line Info
*
* @author h.hagiwara
*
*/
public class JPiereOrderLineModelValidator implements ModelValidator {

	private static CLogger log = CLogger.getCLogger(JPiereOrderLineModelValidator.class);

	//Qty
	static final String PROHIBIT_CHANGE_QTY = "PCQ";
	static final String ALLOW_INCREASE_QTY = "AIQ";
	static final String ALLOW_CHANGE_QTY_WHEN_RESERVED = "ACR";

	//Amt
	static final String ALLOW_CHANGE_AMT_WHEN_NOT_INVOICED = "ANI";

	static final String NON = "NON";


	private int AD_Client_ID = -1;


	@Override
	public void initialize(ModelValidationEngine engine, MClient client)
	{
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();
		engine.addModelChange(MOrderLine.Table_Name, this);

		if (log.isLoggable(Level.FINE)) log.fine("Initialize JPiereOrderLineModelValidator");

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

		//JPIERE-0165
		if(type == ModelValidator.TYPE_BEFORE_NEW ||
				(type == ModelValidator.TYPE_BEFORE_CHANGE && (po.is_ValueChanged("LineNetAmt")|| po.is_ValueChanged("C_Tax_ID"))))
		{
			BigDecimal taxAmt = Env.ZERO;
			MOrderLine ol = (MOrderLine)po;
			MTax m_tax = MTax.get(Env.getCtx(), ol.getC_Tax_ID());
			if(m_tax == null)
				return null;

			IJPiereTaxProvider taxCalculater = JPiereUtil.getJPiereTaxProvider(m_tax);
			//JPIERE-0369:Start
			boolean isTaxIncluded = ol.isTaxIncluded();
			if(ol.getC_Charge_ID() != 0)
			{
	    		MCharge charge = MCharge.get(Env.getCtx(), ol.getC_Charge_ID());
	    		if(!charge.isSameTax())
	    		{
	    			isTaxIncluded = charge.isTaxIncluded();
	    		}
			}

			if(taxCalculater != null)
			{
				taxAmt = taxCalculater.calculateTax(m_tax, ol.getLineNetAmt(), isTaxIncluded	//JPIERE-0369
						, MCurrency.getStdPrecision(po.getCtx(), ol.getParent().getC_Currency_ID())
						, JPiereTaxProvider.getRoundingMode(ol.getParent().getC_BPartner_ID(), ol.getParent().isSOTrx(), m_tax.getC_TaxProvider()));
			}else{
				taxAmt = m_tax.calculateTax(ol.getLineNetAmt(), isTaxIncluded, MCurrency.getStdPrecision(ol.getCtx(), ol.getParent().getC_Currency_ID()));//JPIERE-0369
			}

			if(isTaxIncluded)//JPIERE-0369
			{
				ol.set_ValueNoCheck("JP_TaxBaseAmt",  ol.getLineNetAmt().subtract(taxAmt));
			}else{
				ol.set_ValueNoCheck("JP_TaxBaseAmt",  ol.getLineNetAmt());
			}

			ol.set_ValueOfColumn("JP_TaxAmt", taxAmt);
		}//JPiere-0165

		//JPIERE-0202:Set cost to OrderLine automatically.
		if(type == ModelValidator.TYPE_BEFORE_NEW || type == ModelValidator.TYPE_BEFORE_CHANGE)
		{
			String config = MSysConfig.getValue("JPIERE_SET_COST_TO_ORDER-LINE", "NO", Env.getAD_Client_ID(Env.getCtx()));
			MOrderLine ol = (MOrderLine)po;

			if(ol.getM_Product_ID() != 0  && !config.equals("NO")
					&& (type == ModelValidator.TYPE_BEFORE_NEW || ol.is_ValueChanged("M_Product_ID") || ol.is_ValueChanged("QtyOrdered") || ol.is_ValueChanged("JP_ScheduledCost") ) )
			{
				BigDecimal cost = (BigDecimal)ol.get_Value("JP_ScheduledCost");
				if(cost == null)
					cost = Env.ZERO;

				if( (cost.compareTo(Env.ZERO)==0 && type == ModelValidator.TYPE_BEFORE_NEW)
						|| (type == ModelValidator.TYPE_BEFORE_CHANGE && ol.is_ValueChanged("M_Product_ID") && !ol.is_ValueChanged("JP_ScheduledCost")) )
				{
					if(config.equals("BT"))//Both SO and PO
						setScheduledCost(ol);
					else if(config.equals("SO") && ol.getParent().isSOTrx())
						setScheduledCost(ol);
					else if(config.equals("PO") && !ol.getParent().isSOTrx())
						setScheduledCost(ol);

					cost = (BigDecimal)ol.get_Value("JP_ScheduledCost");
				}

				if(config.equals("BT"))//Both SO and PO
					ol.set_ValueNoCheck("JP_ScheduledCostLineAmt", cost.multiply(ol.getQtyOrdered()));
				else if(config.equals("SO") && ol.getParent().isSOTrx())
					ol.set_ValueNoCheck("JP_ScheduledCostLineAmt", cost.multiply(ol.getQtyOrdered()));
				else if(config.equals("PO") && !ol.getParent().isSOTrx())
					ol.set_ValueNoCheck("JP_ScheduledCostLineAmt", cost.multiply(ol.getQtyOrdered()));

			}else if(ol.getM_Product_ID() == 0){
				ol.set_ValueNoCheck("JP_ScheduledCost", Env.ZERO);
				ol.set_ValueNoCheck("JP_ScheduledCostLineAmt", Env.ZERO);
			}

		}else if(type == ModelValidator.TYPE_AFTER_CHANGE){

			if(po.is_ValueChanged("JP_ScheduledCost"))
			{
				MOrderLine ol = (MOrderLine)po;
				String sql = "UPDATE C_Order i"
						+ " SET JP_ScheduledCostTotalLines = "
						    + "(SELECT COALESCE(SUM(JP_ScheduledCostLineAmt),0) FROM C_OrderLine il WHERE i.C_Order_ID=il.C_Order_ID)"
						+ "WHERE C_Order_ID = ?";
				int no = DB.executeUpdate(sql, new Object[]{Integer.valueOf(ol.getC_Order_ID())}, false, ol.get_TrxName(), 0);
				if (no != 1)
					return "Error";
			}

		}//JPiere-0202

		//JPIEERE-0207:Order Re-Activate Check
		if(type == ModelValidator.TYPE_BEFORE_CHANGE)
		{
			MOrderLine ol = (MOrderLine)po;
			boolean isSOTrx = ol.getParent().isSOTrx() ;
			String  QTY_CHECK = NON;
			if(isSOTrx)
				QTY_CHECK = MSysConfig.getValue("JP_SO_REACTIVATE_QTY_CHECK", NON, ol.getAD_Client_ID(), ol.getAD_Org_ID());
			else
				QTY_CHECK = MSysConfig.getValue("JP_PO_REACTIVATE_QTY_CHECK", NON, ol.getAD_Client_ID(), ol.getAD_Org_ID());

			//Check Qty
			if(QTY_CHECK.equals(NON))
			{
				;//Nothing to do

			}else if(QTY_CHECK.equals(PROHIBIT_CHANGE_QTY) || ol.get_ValueAsInt("JP_MovementLine_ID") > 0){

				if(ol.is_ValueChanged("QtyOrdered") && !ol.getParent().getDocAction().equals(DocAction.ACTION_Void) && !ol.getParent().getDocAction().equals(DocAction.ACTION_Close) &&
						(ol.getQtyReserved().compareTo(Env.ZERO) != 0 || ol.getQtyDelivered().compareTo(Env.ZERO) != 0 || ol.getQtyInvoiced().compareTo(Env.ZERO) != 0 ))
				{
					if(ol.get_ValueAsInt("JP_MovementLine_ID") > 0)
						return  Msg.getMsg(Env.getCtx(), "JP_CanNotChangeQtyForMM");//You can not change Qty. Because Inventory Move Doc created.

					if(isSOTrx)
						return  Msg.getMsg(Env.getCtx(), "JP_CanNotChangeAmountForQtyReservedSO");//You can not Change Qty. Because of Reserved Qty
					else
						return  Msg.getMsg(Env.getCtx(), "JP_CanNotChangeAmountForQtyReservedPO");//You can not Change Qty. Because of Reserved Qty
				}

			}else if(QTY_CHECK.equals(ALLOW_INCREASE_QTY)){

				if(ol.is_ValueChanged("QtyOrdered") && !ol.getParent().getDocAction().equals(DocAction.ACTION_Void) && !ol.getParent().getDocAction().equals(DocAction.ACTION_Close) &&
						( ol.getQtyOrdered().compareTo(ol.getQtyDelivered()) < 0 || ol.getQtyOrdered().compareTo(ol.getQtyInvoiced()) < 0 ))
					return Msg.getMsg(Env.getCtx(), "JP_CanNotChangeQtyLessThanQtyDeliveredOrQtyInvoiced");//You can not change Qty less than Delivered or Invoiced Qty.

			}else if(QTY_CHECK.equals(ALLOW_CHANGE_QTY_WHEN_RESERVED)){

				if(ol.is_ValueChanged("QtyOrdered") && !ol.getParent().getDocAction().equals(DocAction.ACTION_Void) && !ol.getParent().getDocAction().equals(DocAction.ACTION_Close) &&
						( ol.getQtyDelivered().compareTo(Env.ZERO) != 0 || ol.getQtyInvoiced().compareTo(Env.ZERO) != 0 ))
					return Msg.getMsg(Env.getCtx(), "JP_CanNotChangeQtyForQtyDeliveredOrQtyInvoiced");//You can not change Qty. Because Delivered or Invoiced Qty are not 0.

			}


			String  AMT_CHECK = NON;
			if(isSOTrx)
				AMT_CHECK = MSysConfig.getValue("JP_SO_REACTIVATE_AMT_CHECK", NON, ol.getAD_Client_ID(), ol.getAD_Org_ID());
			else
				AMT_CHECK = MSysConfig.getValue("JP_PO_REACTIVATE_AMT_CHECK", NON, ol.getAD_Client_ID(), ol.getAD_Org_ID());


			if(AMT_CHECK.equals(NON))
			{
				;//Nothing to do

			}else if(AMT_CHECK.equals(ALLOW_CHANGE_AMT_WHEN_NOT_INVOICED))
			{
				if((ol.is_ValueChanged("PriceEntered") || ol.is_ValueChanged("C_Tax_ID"))  && ol.getQtyInvoiced().compareTo(Env.ZERO) != 0
						&& !ol.getParent().getDocAction().equals(DocAction.ACTION_Void) && !ol.getParent().getDocAction().equals(DocAction.ACTION_Close))
					return Msg.getMsg(Env.getCtx(), "JP_CanNotChangeAmountForQtyInvoiced");//You can not change Amount. Because invoice was issued.
			}

		}//JPiere-0207:Order Re-Activate Check


		//JPIERE-0227 Common Warehouse & JPIERE-0317 Physical Warehouse
		if(type == ModelValidator.TYPE_BEFORE_NEW ||
				( type == ModelValidator.TYPE_BEFORE_CHANGE &&
					( po.is_ValueChanged("JP_LocatorFrom_ID") || po.is_ValueChanged("JP_LocatorTo_ID")
							|| po.is_ValueChanged("JP_ASI_From_ID") || po.is_ValueChanged("JP_ASI_To_ID") || po.is_ValueChanged("M_Product_ID") )) )
		{
			MOrderLine oLine = (MOrderLine)po;

			//Check Stock Item.
			if(oLine.getM_Product_ID() == 0 || !(oLine.getM_Product().getProductType().equals(MProduct.PRODUCTTYPE_Item) && oLine.getM_Product().isStocked()) )
			{
				oLine.set_ValueNoCheck("JP_Locator_ID", null);
				oLine.set_ValueNoCheck("JP_LocatorFrom_ID", null);
				oLine.set_ValueNoCheck("JP_LocatorTo_ID", null);
				oLine.set_ValueNoCheck("JP_ASI_From_ID", null);
				oLine.set_ValueNoCheck("JP_ASI_To_ID", null);
			}

			int JP_LocatorFrom_ID = oLine.get_ValueAsInt("JP_LocatorFrom_ID");

			//Check Same Corporation Locator.
			if(oLine.get_ValueAsInt("JP_LocatorFrom_ID") !=0 && (type == ModelValidator.TYPE_BEFORE_NEW ||po.is_ValueChanged("JP_LocatorFrom_ID")) )
			{

				MLocator fromLocator =  MLocator.get(oLine.getCtx(), JP_LocatorFrom_ID);
				MOrgInfo fromLocatorOrgInfo = MOrgInfo.get(oLine.getCtx(), fromLocator.getAD_Org_ID(), oLine.get_TrxName());
				MOrgInfo lineOrgInfo = MOrgInfo.get(oLine.getCtx(), oLine.getAD_Org_ID(), oLine.get_TrxName());
				if(fromLocatorOrgInfo.get_ValueAsInt("JP_Corporation_ID") != lineOrgInfo.get_ValueAsInt("JP_Corporation_ID"))
				{
					return Msg.getMsg(Env.getCtx(), "JP_CanNotCreateMMForDiffCorp");//You can not create Material Movement doc. Because of different corporation Locator.
				}
			}

			int JP_LocatorTo_ID = oLine.get_ValueAsInt("JP_LocatorTo_ID");
			MDocType docType = MDocType.get(oLine.getCtx(), oLine.getParent().getC_DocTypeTarget_ID());
			int JP_DocTypeMM_ID = docType.get_ValueAsInt("JP_DocTypeMM_ID");

			//Check to Create MM Doc.
			if(JP_DocTypeMM_ID > 0 && (JP_LocatorFrom_ID != 0 || JP_LocatorTo_ID !=0) )
			{
				//Check Already created.
				if(oLine.get_ValueAsInt("JP_MovementLine_ID") > 0)
					return Msg.getMsg(Env.getCtx(), "JP_CanNotChangeMMInfoForMM");//You can not change Inventory Move Info. Because Inventory Move Doc created.

				//lack of Information to create MM Doc
				if(JP_LocatorFrom_ID > 0 && JP_LocatorTo_ID==0)
					return Msg.getMsg(Env.getCtx(), "JP_PleaseInputToField")+Msg.getElement(Env.getCtx(), "JP_LocatorTo_ID") ;//Please input a value into the field.
				if(JP_LocatorFrom_ID == 0 && JP_LocatorTo_ID > 0)
					return Msg.getMsg(Env.getCtx(), "JP_PleaseInputToField")+Msg.getElement(Env.getCtx(), "JP_LocatorFrom_ID") ;//Please input a value into the field.
				if(JP_LocatorFrom_ID == JP_LocatorTo_ID)
					return Msg.getMsg(Env.getCtx(), "JP_SameLocatorMM");//You are goring to create Inventory Move Doc at same Locator.
				if(!MLocator.get(oLine.getCtx(), JP_LocatorFrom_ID).get_Value("JP_PhysicalWarehouse_ID").equals(MLocator.get(oLine.getCtx(), JP_LocatorTo_ID).get_Value("JP_PhysicalWarehouse_ID")))
					return Msg.getMsg(Env.getCtx(), "JP_CanNotCreateMMforDiffPhyWH");//You can not create Inventory move doc at Sales Order because of different Physical Warehouse.
				if(!MLocator.get(oLine.getCtx(), JP_LocatorFrom_ID).get_Value("M_LocatorType_ID").equals(MLocator.get(oLine.getCtx(), JP_LocatorTo_ID).get_Value("M_LocatorType_ID")))
					return Msg.getMsg(Env.getCtx(), "JP_CanNotCreateMMforDiffLocatorType");//You can not create Inventory move doc at Sales Order because of different Locator Type.


			}else{

				if(JP_LocatorFrom_ID != 0 || JP_LocatorTo_ID !=0)
				{
					return Msg.getMsg(Env.getCtx(), "JP_CanNotCreateMMforDocType");//You can not create Inventory move doc because of missing Doc Type setting.
				}
			}
		}//JPiere-0227

		//JPIERE-0334 Locator Level Reserved
		if( type == ModelValidator.TYPE_BEFORE_CHANGE && (po.is_ValueChanged("JP_Locator_ID") || po.is_ValueChanged("QtyReserved")) )
		{
			MOrderLine oLine = (MOrderLine)po;
			int now_Locator_ID = oLine.get_ValueAsInt("JP_Locator_ID");
			int old_Locator_ID = oLine.get_ValueOldAsInt("JP_Locator_ID");
			Timestamp now_DateReserved  = (Timestamp)oLine.get_Value("JP_DateReserved");
//			Timestamp old_DateReserved  = (Timestamp)oLine.get_ValueOld("JP_DateReserved");
			BigDecimal now_QtyReserved = (BigDecimal)oLine.getQtyReserved();
//			BigDecimal old_QtyReserved = (BigDecimal)oLine.get_ValueOld("QtyReserved");

			if(now_Locator_ID <= 0)
			{
				oLine.set_ValueNoCheck("JP_DateReserved", null);

			}else if(now_QtyReserved.compareTo(Env.ZERO) != 0) {

				if(now_DateReserved == null || old_Locator_ID != now_Locator_ID )
				{
					oLine.set_ValueNoCheck("JP_DateReserved", new Timestamp(Calendar.getInstance().getTimeInMillis()));
				}
			}
		}//JPiere-0334


		//JPIERE-0375:Check Over Qty Invoiced
		if(type == ModelValidator.TYPE_BEFORE_CHANGE && po.is_ValueChanged("QtyInvoiced"))
		{
			MOrderLine ol = (MOrderLine)po;
			if ( (ol.getParent().isSOTrx() && MSysConfig.getBooleanValue("JP_CHECK_ORVER_QTYINVOICED_SO", false, ol.getAD_Client_ID(), ol.getAD_Org_ID()) )
				  ||
				 (!ol.getParent().isSOTrx()	&& MSysConfig.getBooleanValue("JP_CHECK_ORVER_QTYINVOICED_PO", false, ol.getAD_Client_ID(), ol.getAD_Org_ID()) )
		       )
			{
				BigDecimal qtyOrdered = ol.getQtyOrdered();
				BigDecimal qtyInvoiced  = ol.getQtyInvoiced();

				if(qtyOrdered.signum() >= 0)
				{
					if(qtyInvoiced.compareTo(qtyOrdered) > 0)
					{
						return Msg.getMsg(po.getCtx(), "JP_Over_QtyInvoiced") + " : "+ ol.getParent().getDocumentNo() +  " - " + ol.getLine();
					}

				}else {

					if(qtyInvoiced.compareTo(qtyOrdered) < 0)
					{
						return Msg.getMsg(po.getCtx(), "JP_Over_QtyInvoiced") + " : "+ ol.getParent().getDocumentNo() +  " - " + ol.getLine();
					}
				}
			}

		}//JPiere-0375

		//JPIERE-0376:Check Over Qty Delivered
		if(type == ModelValidator.TYPE_BEFORE_CHANGE && po.is_ValueChanged("QtyDelivered"))
		{
			MOrderLine ol = (MOrderLine)po;
			if ( (ol.getParent().isSOTrx() && MSysConfig.getBooleanValue("JP_CHECK_ORVER_QTYDELIVERED_SO", false, ol.getAD_Client_ID(), ol.getAD_Org_ID()) )
				  ||
				 (!ol.getParent().isSOTrx()	&& MSysConfig.getBooleanValue("JP_CHECK_ORVER_QTYDELIVERED_PO", false, ol.getAD_Client_ID(), ol.getAD_Org_ID()) )
		       )
			{
				BigDecimal qtyOrdered = ol.getQtyOrdered();
				BigDecimal qtyDelivered  = ol.getQtyDelivered();

				if(qtyOrdered.signum() >= 0)
				{
					if(qtyDelivered.compareTo(qtyOrdered) > 0)
					{
						return Msg.getMsg(po.getCtx(), "JP_Over_QtyDelivered") + " : "+ ol.getParent().getDocumentNo() +  " - " + ol.getLine();
					}

				}else {

					if(qtyDelivered.compareTo(qtyOrdered) < 0)
					{
						return Msg.getMsg(po.getCtx(), "JP_Over_QtyDelivered") + " : "+ ol.getParent().getDocumentNo() +  " - " + ol.getLine();
					}
				}
			}

		}//JPiere-0376

		//JPIERE-0377:Check Over Qty Recognized
		if(type == ModelValidator.TYPE_BEFORE_CHANGE && po.is_ValueChanged("JP_QtyRecognized"))
		{
			MOrderLine ol = (MOrderLine)po;
			if ( (ol.getParent().isSOTrx() && MSysConfig.getBooleanValue("JP_CHECK_ORVER_QTYRECOGNIZED_SO", false, ol.getAD_Client_ID(), ol.getAD_Org_ID()) )
				  ||
				 (!ol.getParent().isSOTrx()	&& MSysConfig.getBooleanValue("JP_CHECK_ORVER_QTYRECOGNIZED_PO", false, ol.getAD_Client_ID(), ol.getAD_Org_ID()) )
		       )
			{
				BigDecimal qtyOrdered = ol.getQtyOrdered();
				BigDecimal qtyRecognized  = (BigDecimal)ol.get_Value("JP_QtyRecognized");

				if(qtyOrdered.signum() >= 0)
				{
					if(qtyRecognized.compareTo(qtyOrdered) > 0)
					{
						return Msg.getMsg(po.getCtx(), "JP_Over_QtyRecognized") + " : "+ ol.getParent().getDocumentNo() +  " - " + ol.getLine();
					}

				}else {

					if(qtyRecognized.compareTo(qtyOrdered) < 0)
					{
						return Msg.getMsg(po.getCtx(), "JP_Over_QtyRecognized") + " : "+ ol.getParent().getDocumentNo() +  " - " + ol.getLine();
					}
				}
			}

		}//JPiere-0377


		//JPIERE-0409:Set Counter Doc Line Info
		if( type == ModelValidator.TYPE_BEFORE_NEW || type == ModelValidator.TYPE_BEFORE_CHANGE )
		{
			MOrderLine orderLine = (MOrderLine)po;
			if(orderLine.getRef_OrderLine_ID() > 0) //This is Counter doc Line
			{
				MOrderLine counterOrderLine = new MOrderLine(po.getCtx(), orderLine.getRef_OrderLine_ID(), po.get_TrxName());
				orderLine.setPriceEntered(counterOrderLine.getPriceEntered());
				orderLine.setC_UOM_ID(counterOrderLine.getC_UOM_ID());
				orderLine.setPriceActual(counterOrderLine.getPriceActual());
			}//if(orderLine.getRef_OrderLine_ID() > 0)

		}//JPIERE-0409:Set Counter Doc Line Info

		return null;
	}

	@Override
	public String docValidate(PO po, int timing) {

		return null;
	}

	//JPIERE-0202
	private void setScheduledCost(MOrderLine ol)
	{
		MAcctSchema as = MAcctSchema.get(Env.getCtx(), Env.getContextAsInt(Env.getCtx(), "$C_AcctSchema_ID"));
		BigDecimal cost = getProductCosts(ol, as, ol.getAD_Org_ID(), true);
		ol.set_ValueNoCheck("JP_ScheduledCost", cost);
	}

	//JPIERE-0202
	private BigDecimal getProductCosts (MOrderLine ol, MAcctSchema as, int AD_Org_ID, boolean zeroCostsOK)
	{
		ProductCost pc = new ProductCost (Env.getCtx(), ol.getM_Product_ID(), ol.getM_AttributeSetInstance_ID(), ol.get_TrxName());
		pc.setQty(Env.ONE);
		int C_OrderLine_ID = ol.getC_OrderLine_ID();
		String costingMethod = null;
		BigDecimal costs = pc.getProductCosts(as, AD_Org_ID, costingMethod, C_OrderLine_ID, zeroCostsOK);
		if (costs != null)
			return costs;
		return Env.ZERO;
	}//  getProductCosts

}
