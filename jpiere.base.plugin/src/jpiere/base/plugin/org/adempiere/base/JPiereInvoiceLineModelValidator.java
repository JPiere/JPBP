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

import jpiere.base.plugin.org.adempiere.model.JPiereTaxProvider;
import jpiere.base.plugin.util.JPiereUtil;

import org.compiere.model.MClient;
import org.compiere.model.MCurrency;
import org.compiere.model.MDocType;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MTax;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Msg;

public class JPiereInvoiceLineModelValidator implements ModelValidator {

	private static CLogger log = CLogger.getCLogger(JPiereInvoiceLineModelValidator.class);
	private int AD_Client_ID = -1;
	private int AD_Org_ID = -1;
	private int AD_Role_ID = -1;
	private int AD_User_ID = -1;

	@Override
	public void initialize(ModelValidationEngine engine, MClient client) {
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();
		engine.addModelChange(MInvoiceLine.Table_Name, this);

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

		//JPIERE-0165:
		if(type == ModelValidator.TYPE_BEFORE_NEW ||
				(type == ModelValidator.TYPE_BEFORE_CHANGE && (po.is_ValueChanged("LineNetAmt")|| po.is_ValueChanged("C_Tax_ID"))))
		{
			BigDecimal taxAmt = Env.ZERO;
			MInvoiceLine il = (MInvoiceLine)po;
			MTax m_tax = MTax.get(Env.getCtx(), il.getC_Tax_ID());

			IJPiereTaxProvider taxCalculater = JPiereUtil.getJPiereTaxProvider(m_tax);
			if(taxCalculater != null)
			{
				taxAmt = taxCalculater.calculateTax(m_tax, il.getLineNetAmt(), il.isTaxIncluded()
						, MCurrency.getStdPrecision(po.getCtx(), il.getParent().getC_Currency_ID())
						, JPiereTaxProvider.getRoundingMode(il.getParent().getC_BPartner_ID(), il.getParent().isSOTrx(), m_tax.getC_TaxProvider()));
			}else{
				taxAmt = m_tax.calculateTax(il.getLineNetAmt(), il.isTaxIncluded(), MCurrency.getStdPrecision(il.getCtx(), il.getParent().getC_Currency_ID()));
			}

			if(il.isTaxIncluded())
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

					if(!il.getM_RMALine().getM_RMA().getC_DocType().getDocBaseType().equals(MDocType.DOCBASETYPE_SalesOrder))//SOO
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

					if(!il.getM_RMALine().getM_RMA().getC_DocType().getDocBaseType().equals(MDocType.DOCBASETYPE_PurchaseOrder))//POO
					{
						return Msg.getMsg(il.getCtx(), "JP_Can_Not_Match_Because_DocType") +
								Msg.getMsg(il.getCtx(), "JP_ARC_MATCH_POO_ONLY");//ARC of Doc Base Type can match POO of Doc Base type only.
					}
				}
			}
		}

		//JPIERE-0223:Match Inv control-Check Qty signum -> Can not Implement because of reverse
//		if(type == ModelValidator.TYPE_BEFORE_NEW ||
//				(type == ModelValidator.TYPE_BEFORE_CHANGE && po.is_ValueChanged("QtyInvoiced")) )
//		{
//			MInvoiceLine il = (MInvoiceLine)po;
//
//			//Check Receipt/Shipment
//			if(il.getM_InOutLine_ID() > 0)
//			{
//				if(il.getQtyInvoiced().signum() != il.getM_InOutLine().getMovementQty().signum())
//				{
//					return Msg.getMsg(il.getCtx(), "JP_Diff_Signum_MovementQty_QtyInvoiced");//Different signum between MovementQty and QtyInvoiced.
//				}
//			}
//
//			//Check PO/SO
//			if(il.getC_OrderLine_ID() > 0)
//			{
//				if(il.getQtyInvoiced().signum() != il.getC_OrderLine().getQtyOrdered().signum())
//				{
//					return Msg.getMsg(il.getCtx(), "JP_Diff_Signum_QtyOrdered_QtyInvoiced");//Different signum between QtyOrdered and QtyInvoiced.
//				}
//			}
//
//			//Check Return
//			if(il.getM_RMALine_ID() > 0)
//			{
//				if(il.getQtyInvoiced().signum() != il.getM_RMALine().getQty().signum())
//				{
//					return Msg.getMsg(il.getCtx(), "JP_Diff_Signum_RMAQty_QtyInvoiced");//Different signum between RMA Qty and QtyInvoiced.
//				}
//			}
//
//		}

		return null;
	}

	@Override
	public String docValidate(PO po, int timing) {

		return null;
	}

}
