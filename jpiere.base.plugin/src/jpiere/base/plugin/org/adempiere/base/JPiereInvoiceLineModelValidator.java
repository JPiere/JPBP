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

import org.adempiere.webui.window.FDialog;
import org.compiere.model.MCharge;
import org.compiere.model.MClient;
import org.compiere.model.MCurrency;
import org.compiere.model.MDocType;
import org.compiere.model.MInOutLine;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MOrderLine;
import org.compiere.model.MTax;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.process.ProcessInfo;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Msg;

import jpiere.base.plugin.org.adempiere.model.JPiereTaxProvider;
import jpiere.base.plugin.util.JPiereUtil;

/**
 *  JPiere Invoice Line Model Validator
 *
 *  JPIERE-0165: Invoice Line Tax
 *  JPIERE-0223: Restrict Match Inv.
 *  JPIERE-0295: Explode BOM
 *  JPIERE-0369: Mix Include Tax and Exclude Tax Line a Doc.
 *  JPIERE-0375: Check Over Qty Invoice
 *  JPIERE-0409:Set Counter Doc Line Info
 *
 *  @author  Hideaki Hagiwara（h.hagiwara@oss-erp.co.jp）
 *
 */
public class JPiereInvoiceLineModelValidator implements ModelValidator {

	private static CLogger log = CLogger.getCLogger(JPiereInvoiceLineModelValidator.class);
	private int AD_Client_ID = -1;


	@Override
	public void initialize(ModelValidationEngine engine, MClient client)
	{
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();
		engine.addModelChange(MInvoiceLine.Table_Name, this);

		if (log.isLoggable(Level.FINE)) log.fine("Initialize JPiereInvoiceLineModelValidator");

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

		//JPIERE-0165:Invoice Line Tax
		if(type == ModelValidator.TYPE_BEFORE_NEW ||
				(type == ModelValidator.TYPE_BEFORE_CHANGE && (po.is_ValueChanged("LineNetAmt")|| po.is_ValueChanged("C_Tax_ID"))))
		{
			BigDecimal taxAmt = Env.ZERO;
			MInvoiceLine il = (MInvoiceLine)po;
			MTax m_tax = MTax.get(Env.getCtx(), il.getC_Tax_ID());

			IJPiereTaxProvider taxCalculater = JPiereUtil.getJPiereTaxProvider(m_tax);
			//JPIERE-0369:Start
			boolean isTaxIncluded = il.isTaxIncluded();
			if(il.getC_Charge_ID() != 0)
			{
	    		MCharge charge = MCharge.get(Env.getCtx(), il.getC_Charge_ID());
	    		if(!charge.isSameTax())
	    		{
	    			isTaxIncluded = charge.isTaxIncluded();
	    		}
			}

			if(taxCalculater != null)
			{
				taxAmt = taxCalculater.calculateTax(m_tax, il.getLineNetAmt(), isTaxIncluded //JPIERE-0369
						, MCurrency.getStdPrecision(po.getCtx(), il.getParent().getC_Currency_ID())
						, JPiereTaxProvider.getRoundingMode(il.getParent().getC_BPartner_ID(), il.getParent().isSOTrx(), m_tax.getC_TaxProvider()));
			}else{
				taxAmt = m_tax.calculateTax(il.getLineNetAmt(), isTaxIncluded, MCurrency.getStdPrecision(il.getCtx(), il.getParent().getC_Currency_ID()));//JPIERE-0369
			}

			if(isTaxIncluded) //JPiere-0369:finish
			{
				il.set_ValueNoCheck("JP_TaxBaseAmt",  il.getLineNetAmt().subtract(taxAmt));
			}else{
				il.set_ValueNoCheck("JP_TaxBaseAmt",  il.getLineNetAmt());
			}

			il.set_ValueOfColumn("JP_TaxAmt", taxAmt);
		}

		//JPIERE-0223:Match Inv control-Check Doc Base Type
		if(type == ModelValidator.TYPE_BEFORE_NEW ||
				(type == ModelValidator.TYPE_BEFORE_CHANGE &&
					(po.is_ValueChanged("M_InOutLine_ID") || po.is_ValueChanged("C_OrderLine_ID") || po.is_ValueChanged("M_RMALine_ID")) ) )
		{
			MInvoiceLine il = (MInvoiceLine)po;

			//Check Receipt/Shipment
			if(il.getM_InOutLine_ID() > 0 && !il.getParent().isSOTrx())//PO
			{
				if(il.getParent().getC_DocTypeTarget().getDocBaseType().equals(MDocType.DOCBASETYPE_APInvoice))//API
				{
					if(!il.getM_InOutLine().getM_InOut().getC_DocType().getDocBaseType().equals(MDocType.DOCBASETYPE_MaterialReceipt))//MMR
					{
						return Msg.getMsg(il.getCtx(), "JP_Can_Not_Match_Because_DocType") +
								Msg.getMsg(il.getCtx(), "JP_API_MATCH_MMR_ONLY");//API of Doc Base Type can match MMR of Doc Base type only.
					}
				}else if(il.getParent().getC_DocTypeTarget().getDocBaseType().equals(MDocType.DOCBASETYPE_APCreditMemo)){//APC

					if(!il.getM_InOutLine().getM_InOut().getC_DocType().getDocBaseType().equals(MDocType.DOCBASETYPE_MaterialDelivery))//MMS
					{
						return Msg.getMsg(il.getCtx(), "JP_Can_Not_Match_Because_DocType") +
								Msg.getMsg(il.getCtx(), "JP_APC_MATCH_MMS_ONLY");//API of Doc Base Type can match MMS of Doc Base type only.
					}
				}

			}else if(il.getM_InOutLine_ID() > 0 && il.getParent().isSOTrx()){//SO

				if(il.getParent().getC_DocTypeTarget().getDocBaseType().equals(MDocType.DOCBASETYPE_ARInvoice))//ARI
				{
					if(!il.getM_InOutLine().getM_InOut().getC_DocType().getDocBaseType().equals(MDocType.DOCBASETYPE_MaterialDelivery))//MMS
					{
						return Msg.getMsg(il.getCtx(), "JP_Can_Not_Match_Because_DocType") +
								Msg.getMsg(il.getCtx(), "JP_ARI_MATCH_MMS_ONLY");//ARI of Doc Base Type can match MMS of Doc Base type only.
					}
				}else if(il.getParent().getC_DocTypeTarget().getDocBaseType().equals(MDocType.DOCBASETYPE_ARCreditMemo)){//ARC

					if(!il.getM_InOutLine().getM_InOut().getC_DocType().getDocBaseType().equals(MDocType.DOCBASETYPE_MaterialReceipt))//MMR
					{
						return Msg.getMsg(il.getCtx(), "JP_Can_Not_Match_Because_DocType") +
								Msg.getMsg(il.getCtx(), "JP_ARC_MATCH_MMR_ONLY");//ARI of Doc Base Type can match MMR of Doc Base type only.
					}
				}
			}

			//Check PO/SO
			if(il.getC_OrderLine_ID() > 0 && !il.getParent().isSOTrx())//PO
			{
				if(il.getParent().getC_DocTypeTarget().getDocBaseType().equals(MDocType.DOCBASETYPE_APInvoice))//API
				{
					if(!il.getC_OrderLine().getC_Order().getC_DocTypeTarget().getDocBaseType().equals(MDocType.DOCBASETYPE_PurchaseOrder))//POO
					{
						return Msg.getMsg(il.getCtx(), "JP_Can_Not_Match_Because_DocType") +
								Msg.getMsg(il.getCtx(), "JP_API_MATCH_POO_ONLY");//API of Doc Base Type can match POO of Doc Base type only.
					}
				}else if(il.getParent().getC_DocTypeTarget().getDocBaseType().equals(MDocType.DOCBASETYPE_APCreditMemo)){//APC

					//APC of Doc Base Type can not match Purchase Order. Please try to match with RMA.
					return Msg.getMsg(il.getCtx(), "JP_Can_Not_Match_Because_DocType") +
							Msg.getMsg(il.getCtx(), "JP_APC_MATCH_RMA_ONLY");

				}

			}else if(il.getC_OrderLine_ID() > 0 && il.getParent().isSOTrx()){//SO
				if(il.getParent().getC_DocTypeTarget().getDocBaseType().equals(MDocType.DOCBASETYPE_ARInvoice))//ARI
				{
					if(!il.getC_OrderLine().getC_Order().getC_DocTypeTarget().getDocBaseType().equals(MDocType.DOCBASETYPE_SalesOrder))//SOO
					{
						return Msg.getMsg(il.getCtx(), "JP_Can_Not_Match_Because_DocType") +
								Msg.getMsg(il.getCtx(), "JP_ARI_MATCH_SOO_ONLY");//ARI of Doc Base Type can match SOO of Doc Base type only.
					}
				}else if(il.getParent().getC_DocTypeTarget().getDocBaseType().equals(MDocType.DOCBASETYPE_ARCreditMemo)){//ARC

					//ARC of Doc Base Type can not match Sales Order. Please try to match with RMA.
					return Msg.getMsg(il.getCtx(), "JP_Can_Not_Match_Because_DocType") +
							Msg.getMsg(il.getCtx(), "JP_ARC_MATCH_RMA_ONLY");
				}
			}

			//Check Return
			if(il.getM_RMALine_ID() > 0 && !il.getParent().isSOTrx())//PO
			{
				if(il.getParent().getC_DocTypeTarget().getDocBaseType().equals(MDocType.DOCBASETYPE_APInvoice))//API
				{
					return Msg.getMsg(il.getCtx(), "JP_Can_Not_Match_Because_DocType") +
							Msg.getMsg(il.getCtx(), "JP_RMA_MATCH_APC_ONLY");//RMA can match APC of Doc Base type only.

				}else if(il.getParent().getC_DocTypeTarget().getDocBaseType().equals(MDocType.DOCBASETYPE_APCreditMemo)){//APC

					if( !(il.getM_RMALine().getM_RMA().getC_DocType().getDocBaseType().equals(MDocType.DOCBASETYPE_PurchaseOrder) //POO
							&& il.getM_RMALine().getM_RMA().getC_DocType().getDocSubTypeSO().equals(MDocType.DOCSUBTYPESO_ReturnMaterial)) )//RM

					{
						return Msg.getMsg(il.getCtx(), "JP_Can_Not_Match_Because_DocType") +
								Msg.getMsg(il.getCtx(), "JP_APC_MATCH_MMS_ONLY");//API of Doc Base Type can match MMS of Doc Base type only.
					}
				}
			}else if(il.getM_RMALine_ID() > 0 && il.getParent().isSOTrx()){//SO

				if(il.getParent().getC_DocTypeTarget().getDocBaseType().equals(MDocType.DOCBASETYPE_ARInvoice))//ARI
				{
					return Msg.getMsg(il.getCtx(), "JP_Can_Not_Match_Because_DocType") +
							Msg.getMsg(il.getCtx(), "JP_RMA_MATCH_ARC_ONLY");//RMA can match ARC of Doc Base type only.

				}else if(il.getParent().getC_DocTypeTarget().getDocBaseType().equals(MDocType.DOCBASETYPE_ARCreditMemo)){//ARC

					if( !(il.getM_RMALine().getM_RMA().getC_DocType().getDocBaseType().equals(MDocType.DOCBASETYPE_SalesOrder) //SOO
							&& il.getM_RMALine().getM_RMA().getC_DocType().getDocSubTypeSO().equals(MDocType.DOCSUBTYPESO_ReturnMaterial)) )//RM
					{
						return Msg.getMsg(il.getCtx(), "JP_Can_Not_Match_Because_DocType");//ARC of Doc Base Type can match POO of Doc Base type only.
					}
				}
			}
		}


		//JPIERE-0295: Explode BOM
		if(type == ModelValidator.TYPE_BEFORE_NEW ||
				(type == ModelValidator.TYPE_BEFORE_CHANGE && po.is_ValueChanged("M_Product_ID") ) )
		{
			MInvoiceLine il = (MInvoiceLine)po;
			if(il.getM_Product_ID() == 0)
			{
				il.set_ValueNoCheck("JP_ProductExplodeBOM_ID", null);

			}else if(il.getC_OrderLine_ID() > 0) {

				MOrderLine ol = new MOrderLine(il.getCtx(),il.getC_OrderLine_ID(),il.get_TrxName());
				if(ol.get_Value("JP_ProductExplodeBOM_ID") != null)
				{

					if(il.getM_Product_ID() == ol.getM_Product_ID())
					{
						il.set_ValueNoCheck("JP_ProductExplodeBOM_ID", ol.get_Value("JP_ProductExplodeBOM_ID"));
					}else {

						//Different between {0} and {1}
						String msg0 = Msg.getElement(Env.getCtx(), "C_OrderLine_ID")+" - " + Msg.getElement(Env.getCtx(), "M_Product_ID");
						String msg1 = Msg.getElement(Env.getCtx(), "C_InvoiceLine_ID")+" - " + Msg.getElement(Env.getCtx(), "M_Product_ID");
						return Msg.getMsg(Env.getCtx(),"JP_Different",new Object[]{msg0,msg1});
					}
				}else {
					il.set_ValueNoCheck("JP_ProductExplodeBOM_ID", null);
				}

			}else if(il.getM_InOutLine_ID() > 0) {

				MInOutLine iol = new MInOutLine(il.getCtx(),il.getM_InOutLine_ID(),il.get_TrxName());
				if(iol.get_Value("JP_ProductExplodeBOM_ID") != null)
				{

					if(il.getM_Product_ID() == iol.getM_Product_ID())
					{
						il.set_ValueNoCheck("JP_ProductExplodeBOM_ID", iol.get_Value("JP_ProductExplodeBOM_ID"));
					}else {

						//Different between {0} and {1}
						String msg0 = Msg.getElement(Env.getCtx(), "M_InOutLine_ID")+" - " + Msg.getElement(Env.getCtx(), "M_Product_ID");
						String msg1 = Msg.getElement(Env.getCtx(), "C_InvoiceLine_ID")+" - " + Msg.getElement(Env.getCtx(), "M_Product_ID");
						return Msg.getMsg(Env.getCtx(),"JP_Different",new Object[]{msg0,msg1});
					}
				}else {
					il.set_ValueNoCheck("JP_ProductExplodeBOM_ID", null);
				}
			}
		}



		//JPIERE-0375:Check Over Qty Invoice
		if(type == ModelValidator.TYPE_BEFORE_NEW ||
				(type == ModelValidator.TYPE_BEFORE_CHANGE && po.is_ValueChanged("QtyInvoiced") ) )
		{
			MInvoiceLine il = (MInvoiceLine)po;
			ProcessInfo pInfo = Env.getProcessInfo(Env.getCtx());
			if(pInfo == null && il.getC_OrderLine_ID() > 0)
			{
				BigDecimal invoiceQtyInvoiced  = il.getQtyInvoiced();
				BigDecimal orderQtyInvoiced = il.getC_OrderLine().getQtyInvoiced();
				BigDecimal qtyOrdered = il.getC_OrderLine().getQtyOrdered();
				BigDecimal qtyToInvoice = qtyOrdered.subtract(orderQtyInvoiced);
				if(qtyOrdered.signum() >= 0)
				{

					if(invoiceQtyInvoiced.compareTo(qtyToInvoice) > 0)
					{
						try {
							FDialog.info(0, null, "JP_ToBeConfirmed", Msg.getMsg(po.getCtx(), "JP_Over_QtyInvoiced_Possibility")
									+" : "+ il.getParent().getDocumentNo() +  " - " + il.getLine());
						}catch(Exception e) {
							;//ignore
						}

					}

				}else {

					if(invoiceQtyInvoiced.compareTo(qtyToInvoice) < 0)
					{
						try {
							FDialog.info(0, null, "JP_ToBeConfirmed", Msg.getMsg(po.getCtx(), "JP_Over_QtyInvoiced_Possibility")
									+" : "+ il.getParent().getDocumentNo() +  " - " + il.getLine());
						}catch(Exception e) {
							;//ignore
						}
					}
				}

			}else if(pInfo == null && il.getM_RMALine_ID() > 0) {

				BigDecimal invoiceQtyInvoiced  = il.getQtyInvoiced();
				BigDecimal rmaQtyInvoiced = il.getM_RMALine().getQtyInvoiced();
				BigDecimal qtyRMA = il.getM_RMALine().getQty();
				BigDecimal qtyToInvoice = qtyRMA.subtract(rmaQtyInvoiced);

				if(qtyRMA.signum() >= 0)
				{
					if(invoiceQtyInvoiced.compareTo(qtyToInvoice) > 0)
					{
						try {
							FDialog.info(0, null, "JP_ToBeConfirmed", Msg.getMsg(po.getCtx(), "JP_Over_QtyInvoiced_Possibility")
									+" : "+ il.getParent().getDocumentNo() +  " - " + il.getLine());
						}catch(Exception e) {
							;//ignore
						}
					}

				}else {

					if(invoiceQtyInvoiced.compareTo(qtyToInvoice) < 0)
					{
						try {
							FDialog.info(0, null, "JP_ToBeConfirmed", Msg.getMsg(po.getCtx(), "JP_Over_QtyInvoiced_Possibility")
									+" : "+ il.getParent().getDocumentNo() +  " - " + il.getLine());
						}catch(Exception e) {
							;//ignore
						}
					}
				}

			}

		}

		//JPIERE-0409:Set Counter Doc Line Info
		if( type == ModelValidator.TYPE_BEFORE_NEW || type == ModelValidator.TYPE_BEFORE_CHANGE )
		{
			MInvoiceLine invoiceLine = (MInvoiceLine)po;
			if(invoiceLine.getRef_InvoiceLine_ID() > 0) //This is Counter doc Line
			{
				MInvoiceLine counterOrderLine = new MInvoiceLine(po.getCtx(), invoiceLine.getRef_InvoiceLine_ID(), po.get_TrxName());
				invoiceLine.setPriceEntered(counterOrderLine.getPriceEntered());
				invoiceLine.setC_UOM_ID(counterOrderLine.getC_UOM_ID());
				invoiceLine.setPriceActual(counterOrderLine.getPriceActual());
				invoiceLine.setLineNetAmt(counterOrderLine.getLineNetAmt());
			}

		}//JPIERE-0409:Set Counter Doc Line Info

		return null;
	}

	@Override
	public String docValidate(PO po, int timing) {

		return null;
	}

}
