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
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MBPartner;
import org.compiere.model.MDocType;
import org.compiere.model.MDocTypeCounter;
import org.compiere.model.MInOut;
import org.compiere.model.MInOutLine;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MInvoicePaySchedule;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MOrderPaySchedule;
import org.compiere.model.MOrg;
import org.compiere.model.MOrgInfo;
import org.compiere.model.MProduct;
import org.compiere.model.MProject;
import org.compiere.model.MStorageOnHand;
import org.compiere.model.MSysConfig;
import org.compiere.model.MWarehouse;
import org.compiere.model.PO;
import org.compiere.process.DocAction;
import org.compiere.process.DocOptions;
import org.compiere.process.DocumentEngine;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.eevolution.model.MPPProductBOM;
import org.eevolution.model.MPPProductBOMLine;

/**
 * JPIERE-0142
 * JPIERE-0294
 *
 * @author Hideaki Hagiwara
 *
 */
public class MOrderJP extends MOrder implements DocOptions {

	public MOrderJP(Properties ctx, int C_Order_ID, String trxName) {
		super(ctx, C_Order_ID, trxName);
	}

	public MOrderJP(MProject project, boolean IsSOTrx, String DocSubTypeSO) {
		super(project, IsSOTrx, DocSubTypeSO);
	}

	public MOrderJP(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	@Override
	public int customizeValidActions(String docStatus, Object processing, String orderType, String isSOTrx,
			int AD_Table_ID, String[] docAction, String[] options, int index) {

		if(isSOTrx.equals("Y") && (orderType.equals(MDocType.DOCSUBTYPESO_Proposal)
				|| orderType.equals(MDocType.DOCSUBTYPESO_Quotation)))
		{
			index = 0; //initialize the index
			options[index++] = DocumentEngine.ACTION_Prepare;
			options[index++] = DocumentEngine.ACTION_Void;
			return index;
		}

		if (docStatus.equals(DocumentEngine.STATUS_Drafted) || docStatus.equals(DocumentEngine.STATUS_InProgress))
		{
			index = 0; //initialize the index
			options[index++] = DocumentEngine.ACTION_Void;
			options[index++] = DocumentEngine.ACTION_Prepare;
			options[index++] = DocumentEngine.ACTION_Complete;

			return index;
		}

		return index;
	}


	@Override
	public boolean explodeBOM() {
		boolean retValue = false;
		String where = "AND IsActive='Y' AND EXISTS "
			+ "(SELECT * FROM M_Product p WHERE C_OrderLine.M_Product_ID=p.M_Product_ID"
			+ " AND	p.IsBOM='Y' AND p.IsVerified='Y' AND p.IsStocked='N')";
		//
		String sql = "SELECT COUNT(*) FROM C_OrderLine "
			+ "WHERE C_Order_ID=? " + where;
		int count = DB.getSQLValue(get_TrxName(), sql, getC_Order_ID());
		while (count != 0)
		{
			retValue = true;
			renumberLines (1000);		//	max 999 bom items

			//	Order Lines with non-stocked BOMs
			MOrderLine[] lines = getLines (where, MOrderLine.COLUMNNAME_Line);
			for (int i = 0; i < lines.length; i++)
			{
				MOrderLine line = lines[i];
				MProduct product = MProduct.get (getCtx(), line.getM_Product_ID());
				if (log.isLoggable(Level.FINE)) log.fine(product.getName());
				//	New Lines
				int lineNo = line.getLine ();
				MPPProductBOM bom = MPPProductBOM.getDefault(product, get_TrxName());
				if (bom == null)
					continue;

				boolean isRemain = MSysConfig.getBooleanValue("JP_REMAIN_EXPLODE_PRODUCT_LINE", false, getAD_Client_ID(), getAD_Org_ID());
				boolean isFirstLine = true;
				int JP_ProductExplodeBOM_ID = line.getM_Product_ID();

				for (MPPProductBOMLine bomLine : bom.getLines())
				{
					if(isRemain || !isFirstLine)
					{
						MOrderLine newLine = new MOrderLine(this);
						newLine.setLine(++lineNo);

						//JPIERE-0294
						newLine.set_ValueNoCheck("JP_ProductExplodeBOM_ID", JP_ProductExplodeBOM_ID);

						newLine.setM_Product_ID(bomLine.getM_Product_ID(), true);
						newLine.setQty(line.getQtyOrdered().multiply(bomLine.getQtyBOM()));
						if (bom.getDescription() != null)
							newLine.setDescription(bom.getDescription());
						newLine.setPrice();
						newLine.save(get_TrxName());

					}else{

						//JPIERE-0294
						line.set_ValueNoCheck("JP_ProductExplodeBOM_ID", JP_ProductExplodeBOM_ID);

						line.setM_Product_ID(bomLine.getM_Product_ID(), true);

						//Reset once
						line.setM_AttributeSetInstance_ID (0);
						line.setPrice (Env.ZERO);
						line.setPriceLimit (Env.ZERO);
						line.setPriceList (Env.ZERO);
						line.setLineNetAmt (Env.ZERO);
						line.setFreightAmt (Env.ZERO);

						//Set again
						line.setQty(line.getQtyOrdered().multiply(bomLine.getQtyBOM()));
						String description  =line.getDescription ();
						if (bom.getDescription() != null)
							line.setDescription(description + " : " +bom.getDescription());
						line.setPrice();
						line.save (get_TrxName());
						isFirstLine = false;
					}
				}//for

				//	Convert into Comment Line
				if(isRemain)
				{
					//JPIERE-0294
					line.set_ValueNoCheck("JP_ProductExplodeBOM_ID", line.getM_Product_ID());

					line.setM_Product_ID (0);
					line.setM_AttributeSetInstance_ID (0);
					line.setPrice (Env.ZERO);
					line.setPriceLimit (Env.ZERO);
					line.setPriceList (Env.ZERO);
					line.setLineNetAmt (Env.ZERO);
					line.setFreightAmt (Env.ZERO);

					String description = product.getName ();
					if (product.getDescription () != null)
						description += " " + product.getDescription ();
					if (line.getDescription () != null)
						description += " " + line.getDescription ();
					line.setDescription (description);
					line.save (get_TrxName());
				}

			}	//	for all lines with BOM

			m_lines = null;		//	force requery
			count = DB.getSQLValue (get_TrxName(), sql, getC_Invoice_ID ());
			renumberLines (10);
		}	//	while count != 0
		return retValue;
	}

	@Override
	protected MInOut createShipment(MDocType dt, Timestamp movementDate) {//JPIERE-0295

		if (log.isLoggable(Level.INFO)) log.info("For " + dt);
		MInOut shipment = new MInOut (this, dt.getC_DocTypeShipment_ID(), movementDate);
	//	shipment.setDateAcct(getDateAcct());
		if (!shipment.save(get_TrxName()))
		{
			m_processMsg = "Could not create Shipment";
			return null;
		}
		//
		MOrderLine[] oLines = getLines(true, null);
		for (int i = 0; i < oLines.length; i++)
		{
			MOrderLine oLine = oLines[i];
			//
			MInOutLine ioLine = new MInOutLine(shipment);
			ioLine.set_ValueNoCheck("JP_ProductExplodeBOM_ID", oLine.get_Value("JP_ProductExplodeBOM_ID"));//JPIERE-0295

			//	Qty = Ordered - Delivered
			BigDecimal MovementQty = oLine.getQtyOrdered().subtract(oLine.getQtyDelivered());
			//	Location
			int M_Locator_ID = MStorageOnHand.getM_Locator_ID (oLine.getM_Warehouse_ID(),
					oLine.getM_Product_ID(), oLine.getM_AttributeSetInstance_ID(),
					MovementQty, get_TrxName());
			if (M_Locator_ID == 0)		//	Get default Location
			{
				MWarehouse wh = MWarehouse.get(getCtx(), oLine.getM_Warehouse_ID());
				M_Locator_ID = wh.getDefaultLocator().getM_Locator_ID();
			}
			//
			ioLine.setOrderLine(oLine, M_Locator_ID, MovementQty);
			ioLine.setQty(MovementQty);
			if (oLine.getQtyEntered().compareTo(oLine.getQtyOrdered()) != 0)
				ioLine.setQtyEntered(MovementQty
					.multiply(oLine.getQtyEntered())
					.divide(oLine.getQtyOrdered(), 6, RoundingMode.HALF_UP));
			if (!ioLine.save(get_TrxName()))
			{
				m_processMsg = "Could not create Shipment Line";
				return null;
			}
		}
		// added AdempiereException by zuhri
		if (!shipment.processIt(DocAction.ACTION_Complete))
			throw new AdempiereException("Failed when processing document - " + shipment.getProcessMsg());
		// end added
		shipment.saveEx(get_TrxName());
		if (!DOCSTATUS_Completed.equals(shipment.getDocStatus()))
		{
			m_processMsg = "@M_InOut_ID@: " + shipment.getProcessMsg();
			return null;
		}
		return shipment;
	}


	@Override
	protected MInvoice createInvoice (MDocType dt, MInOut shipment, Timestamp invoiceDate)//JPIERE-0295
	{
		if (log.isLoggable(Level.INFO)) log.info(dt.toString());
		MInvoice invoice = new MInvoice (this, dt.getC_DocTypeInvoice_ID(), invoiceDate);
		if (!invoice.save(get_TrxName()))
		{
			m_processMsg = "Could not create Invoice";
			return null;
		}

		//	If we have a Shipment - use that as a base
		if (shipment != null)
		{
			if (!INVOICERULE_AfterDelivery.equals(getInvoiceRule()))
				setInvoiceRule(INVOICERULE_AfterDelivery);
			//
			MInOutLine[] sLines = shipment.getLines(false);
			for (int i = 0; i < sLines.length; i++)
			{
				MInOutLine sLine = sLines[i];
				//
				MInvoiceLine iLine = new MInvoiceLine(invoice);
				iLine.setShipLine(sLine);

				iLine.set_ValueNoCheck("JP_ProductExplodeBOM_ID", sLine.get_Value("JP_ProductExplodeBOM_ID"));//JPIERE-0295

				//	Qty = Delivered
				if (sLine.sameOrderLineUOM())
					iLine.setQtyEntered(sLine.getQtyEntered());
				else
					iLine.setQtyEntered(sLine.getMovementQty());
				iLine.setQtyInvoiced(sLine.getMovementQty());
				if (!iLine.save(get_TrxName()))
				{
					m_processMsg = "Could not create Invoice Line from Shipment Line";
					return null;
				}
				//
				sLine.setIsInvoiced(true);
				if (!sLine.save(get_TrxName()))
				{
					log.warning("Could not update Shipment line: " + sLine);
				}
			}
		}
		else	//	Create Invoice from Order
		{
			if (!INVOICERULE_Immediate.equals(getInvoiceRule()))
				setInvoiceRule(INVOICERULE_Immediate);
			//
			MOrderLine[] oLines = getLines();
			for (int i = 0; i < oLines.length; i++)
			{
				MOrderLine oLine = oLines[i];
				//
				MInvoiceLine iLine = new MInvoiceLine(invoice);
				iLine.setOrderLine(oLine);

				iLine.set_ValueNoCheck("JP_ProductExplodeBOM_ID", oLine.get_Value("JP_ProductExplodeBOM_ID"));//JPIERE-0295

				//	Qty = Ordered - Invoiced
				iLine.setQtyInvoiced(oLine.getQtyOrdered().subtract(oLine.getQtyInvoiced()));
				if (oLine.getQtyOrdered().compareTo(oLine.getQtyEntered()) == 0)
					iLine.setQtyEntered(iLine.getQtyInvoiced());
				else
					iLine.setQtyEntered(iLine.getQtyInvoiced().multiply(oLine.getQtyEntered())
						.divide(oLine.getQtyOrdered(), 12, RoundingMode.HALF_UP));
				if (!iLine.save(get_TrxName()))
				{
					m_processMsg = "Could not create Invoice Line from Order Line";
					return null;
				}
			}
		}

		// Copy payment schedule from order to invoice if any
		for (MOrderPaySchedule ops : MOrderPaySchedule.getOrderPaySchedule(getCtx(), getC_Order_ID(), 0, get_TrxName())) {
			MInvoicePaySchedule ips = new MInvoicePaySchedule(getCtx(), 0, get_TrxName());
			PO.copyValues(ops, ips);
			ips.setC_Invoice_ID(invoice.getC_Invoice_ID());
			ips.setAD_Org_ID(ops.getAD_Org_ID());
			ips.setProcessing(ops.isProcessing());
			ips.setIsActive(ops.isActive());
			if (!ips.save()) {
				m_processMsg = "ERROR: creating pay schedule for invoice from : "+ ops.toString();
				return null;
			}
		}

		// added AdempiereException by zuhri
		if (!invoice.processIt(DocAction.ACTION_Complete))
			throw new AdempiereException("Failed when processing document - " + invoice.getProcessMsg());
		// end added
		invoice.saveEx(get_TrxName());
		setC_CashLine_ID(invoice.getC_CashLine_ID());
		if (!DOCSTATUS_Completed.equals(invoice.getDocStatus()))
		{
			m_processMsg = "@C_Invoice_ID@: " + invoice.getProcessMsg();
			return null;
		}
		return invoice;
	}	//	createInvoice


	@Override
	protected MOrder createCounterDoc()//JPIERE-0295
	{
		//	Is this itself a counter doc ?
		if (getRef_Order_ID() != 0)
			return null;

		//	Org Must be linked to BPartner
		MOrg org = MOrg.get(getCtx(), getAD_Org_ID());
		int counterC_BPartner_ID = org.getLinkedC_BPartner_ID(get_TrxName());
		if (counterC_BPartner_ID == 0)
			return null;
		//	Business Partner needs to be linked to Org
		MBPartner bp = new MBPartner (getCtx(), getC_BPartner_ID(), get_TrxName());
		int counterAD_Org_ID = bp.getAD_OrgBP_ID();
		if (counterAD_Org_ID == 0)
			return null;

		MBPartner counterBP = new MBPartner (getCtx(), counterC_BPartner_ID, null);
		MOrgInfo counterOrgInfo = MOrgInfo.get(getCtx(), counterAD_Org_ID, get_TrxName());
		if (log.isLoggable(Level.INFO)) log.info("Counter BP=" + counterBP.getName());

		//	Document Type
		int C_DocTypeTarget_ID = 0;
		MDocTypeCounter counterDT = MDocTypeCounter.getCounterDocType(getCtx(), getC_DocType_ID());
		if (counterDT != null)
		{
			if (log.isLoggable(Level.FINE)) log.fine(counterDT.toString());
			if (!counterDT.isCreateCounter() || !counterDT.isValid())
				return null;
			C_DocTypeTarget_ID = counterDT.getCounter_C_DocType_ID();
		}
		else	//	indirect
		{
			C_DocTypeTarget_ID = MDocTypeCounter.getCounterDocType_ID(getCtx(), getC_DocType_ID());
			if (log.isLoggable(Level.FINE)) log.fine("Indirect C_DocTypeTarget_ID=" + C_DocTypeTarget_ID);
			if (C_DocTypeTarget_ID <= 0)
				return null;
		}
		//	Deep Copy
		MOrder counter = copyFrom (this, getDateOrdered(),
			C_DocTypeTarget_ID, !isSOTrx(), true, false, get_TrxName());
		//
		counter.setAD_Org_ID(counterAD_Org_ID);
		counter.setM_Warehouse_ID(counterOrgInfo.getM_Warehouse_ID());
		//
//		counter.setBPartner(counterBP); // was set on copyFrom
		counter.setDatePromised(getDatePromised());		// default is date ordered
		//	References (Should not be required)
		counter.setSalesRep_ID(getSalesRep_ID());
		counter.saveEx(get_TrxName());

		//	Update copied lines
		MOrderLine[] counterLines = counter.getLines(true, null);
		for (int i = 0; i < counterLines.length; i++)
		{
			MOrderLine counterLine = counterLines[i];
			counterLine.setOrder(counter);	//	copies header values (BP, etc.)
			counterLine.set_ValueNoCheck("JP_ProductExplodeBOM_ID", counterLines[i].get_Value("JP_ProductExplodeBOM_ID"));//JPIERE-0295
			counterLine.setPrice();
			counterLine.setTax();
			counterLine.saveEx(get_TrxName());
		}
		if (log.isLoggable(Level.FINE)) log.fine(counter.toString());

		//	Document Action
		if (counterDT != null)
		{
			if (counterDT.getDocAction() != null)
			{
				counter.setDocAction(counterDT.getDocAction());
				// added AdempiereException by zuhri
				if (!counter.processIt(counterDT.getDocAction()))
					throw new AdempiereException("Failed when processing document - " + counter.getProcessMsg());
				// end added
				counter.saveEx(get_TrxName());
			}
		}
		return counter;
	}	//	createCounterDoc
}
