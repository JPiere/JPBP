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
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MAllocationHdr;
import org.compiere.model.MAllocationLine;
import org.compiere.model.MClient;
import org.compiere.model.MDocType;
import org.compiere.model.MInOut;
import org.compiere.model.MInOutLine;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MLocator;
import org.compiere.model.MOrder;
import org.compiere.model.MPayment;
import org.compiere.model.MSysConfig;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.process.DocAction;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.MDeliveryDays;
import jpiere.base.plugin.org.adempiere.model.MInvoiceJP;

/**
 *
 * JPiere InOut Model Validator
 *
 * JPIERE-0219:Create Invoice When Ship/Receipt Complete
 * JPIERE-0229:Inspection basis
 * JPIERE-0295:Explode BOM
 * JPIERE-0317:Physical Warehouse - check same physical warehouse between locator and document.
 * JPIERE-0490:Copy Subject and Remarks
 *
 * @author h.hagiwara
 *
 */
public class JPiereInOutModelValidator implements ModelValidator {

	private static CLogger log = CLogger.getCLogger(JPiereInOutModelValidator.class);
	private int AD_Client_ID = -1;

	@Override
	public void initialize(ModelValidationEngine engine, MClient client)
	{
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();
		engine.addModelChange(MInOut.Table_Name, this);
		engine.addDocValidate(MInOut.Table_Name, this);

		if (log.isLoggable(Level.FINE)) log.fine("Initialize JPiereInOutModelValidator");

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

		//JPIERE-0229
		if(type == ModelValidator.TYPE_BEFORE_NEW || type == ModelValidator.TYPE_BEFORE_CHANGE )
		{
			MInOut io = (MInOut)po;
			if(io.get_Value("JP_ScheduledInOutDate") == null)
			{
				io.set_ValueNoCheck("JP_ScheduledInOutDate", io.getMovementDate());
			}

			if(type == ModelValidator.TYPE_BEFORE_NEW
					|| (type == ModelValidator.TYPE_BEFORE_CHANGE
						&&( io.is_ValueChanged("JP_ScheduledInOutDate")	|| io.is_ValueChanged("C_BPartner_Location_ID") || io.is_ValueChanged("M_Warehouse_ID")) ))
			{
				String trxName = po.get_TrxName();
				MDocType ioDocType = MDocType.get(po.getCtx(), io.getC_DocType_ID());
				if(ioDocType.get_ValueAsBoolean("IsInspectionInvoiceJP") && io.getC_Order_ID() > 0)
				{
					MOrder order = new MOrder(po.getCtx(), io.getC_Order_ID(), trxName);
					MDocType orderDocType = MDocType.get(po.getCtx(), order.getC_DocTypeTarget_ID());
					if(orderDocType.getDocSubTypeSO().equals(MOrder.DocSubTypeSO_OnCredit)
							|| orderDocType.getDocSubTypeSO().equals(MOrder.DocSubTypeSO_POS)
							|| orderDocType.getDocSubTypeSO().equals(MOrder.DocSubTypeSO_Prepay))
					{
						;//Nothing to do because InOut Doc is created automatically.
					}else{
						Timestamp invoiceDate = MDeliveryDays.getInvoiceDate(io, ioDocType.get_ValueAsBoolean("IsHolidayNotInspectionJP"), "JP_ScheduledInOutDate");
						io.setDateAcct(invoiceDate);
						if(MSysConfig.getBooleanValue("JP_INSPECTION_MOVEMENTDATE", false, io.getAD_Client_ID(), io.getAD_Org_ID()))
						{
							io.setMovementDate(invoiceDate);
						}
					}

				}

			}//if(type == ModelValidator.TYPE_BEFORE_NEW)

		}//JPiere-0229

		//JPIERE-0490 : Copy Subject and Remarks
		if(type == ModelValidator.TYPE_BEFORE_NEW || po.is_ValueChanged("C_Order_ID"))
		{
			MInOut io = (MInOut)po;
			if(io.getC_Order_ID() > 0)
			{
				MOrder order = new MOrder(po.getCtx(),io.getC_Order_ID(), po.get_TrxName());
				if(Util.isEmpty(io.get_ValueAsString("JP_Subject")))
				{
					io.set_ValueNoCheck("JP_Subject", order.get_ValueAsString("JP_Subject"));
				}

				if(Util.isEmpty(io.getDescription()))
				{
					io.setDescription(order.getDescription());
				}

				if(Util.isEmpty(io.get_ValueAsString("JP_Remarks")))
				{
					io.set_ValueNoCheck("JP_Remarks", order.get_ValueAsString("JP_Remarks"));
				}

			}
		}


		return null;
	}

	@Override
	public String docValidate(PO po, int timing)
	{

		if(!po.get_TableName().equals(MInOut.Table_Name))
			return null;


		//JPIERE-0317  Physical Warehouse - check same physical warehouse between locator and document.
		if(timing == ModelValidator.TIMING_BEFORE_PREPARE
				&& MSysConfig.getBooleanValue("JP_INOUT_PHYWH_LOCATOR_CHECK", true, po.getAD_Client_ID(), po.getAD_Org_ID()))
		{
			MInOut io = (MInOut)po;
			int io_PhysicalWarehouse_ID = io.get_ValueAsInt("JP_PhysicalWarehouse_ID");
			if(io_PhysicalWarehouse_ID != 0)
			{
				MInOutLine[] ioLines =  io.getLines();
				MLocator loc = null;
				int loc_PhysicalWarehouse_ID = 0;
				for(int i = 0 ; i < ioLines.length; i++)
				{
					loc = MLocator.get(io.getCtx(), ioLines[i].getM_Locator_ID());
					loc_PhysicalWarehouse_ID =  loc.get_ValueAsInt("JP_PhysicalWarehouse_ID");
					if(loc_PhysicalWarehouse_ID != 0 && loc_PhysicalWarehouse_ID != io_PhysicalWarehouse_ID)
					{
							return Msg.getMsg(io.getCtx(), "JP_PhyWarehouseLocatorConflict") //Conflict Physical Warehouse between document and Locator
									+ Msg.getElement(io.getCtx(), "Line") + " : " +ioLines[i].getLine();
					}
				}//for
			}
		}//JPIERE-0317


		//JPIERE-0229
		if(timing == ModelValidator.TIMING_BEFORE_COMPLETE)
		{
			MInOut io = (MInOut)po;
			String trxName = po.get_TrxName();
			boolean isReversal = io.isReversal();
			MDocType ioDocType = MDocType.get(po.getCtx(), io.getC_DocType_ID());

			if(!isReversal && ioDocType.get_ValueAsBoolean("IsInspectionInvoiceJP") && io.getC_Order_ID() > 0)
			{
				MOrder order = new MOrder(po.getCtx(), io.getC_Order_ID(), trxName);
				MDocType orderDocType = MDocType.get(po.getCtx(), order.getC_DocTypeTarget_ID());
				if(orderDocType.getDocSubTypeSO().equals(MOrder.DocSubTypeSO_OnCredit)
						|| orderDocType.getDocSubTypeSO().equals(MOrder.DocSubTypeSO_POS)
						|| orderDocType.getDocSubTypeSO().equals(MOrder.DocSubTypeSO_Prepay))
				{
					;//Nothing to do because InOut Doc is created automatically.
				}else{
					Timestamp invoiceDate = MDeliveryDays.getInvoiceDate(io, ioDocType.get_ValueAsBoolean("IsHolidayNotInspectionJP"), "ShipDate");
					io.setDateAcct(invoiceDate);
					if(MSysConfig.getBooleanValue("JP_INSPECTION_MOVEMENTDATE", false, io.getAD_Client_ID(), io.getAD_Org_ID()))
					{
						io.setMovementDate(invoiceDate);
					}
				}

			}
		}//JPiere-0229


		//JPIERE-0219:Create Invoice When Ship/Receipt Complete
		if( (po.get_ValueAsBoolean(MInOut.COLUMNNAME_IsSOTrx) && timing == MSysConfig.getIntValue("JP_INOUT_TIMING_OF_CREATE_AR_INVOICE", 9, po.getAD_Client_ID(), po.getAD_Org_ID()))
				|| (!po.get_ValueAsBoolean(MInOut.COLUMNNAME_IsSOTrx) && timing == MSysConfig.getIntValue("JP_INOUT_TIMING_OF_CREATE_AP_INVOICE", 9, po.getAD_Client_ID(), po.getAD_Org_ID())) )
		{
			MInOut io = (MInOut)po;
			String trxName = po.get_TrxName();
			boolean isReversal = io.isReversal();
			MDocType ioDocType = MDocType.get(po.getCtx(), io.getC_DocType_ID());
			if(ioDocType.get_ValueAsBoolean("IsCreateInvoiceJP"))
			{
				if(io.getC_Order_ID()==0)
					return null;

				MOrder order = new MOrder(po.getCtx(), io.getC_Order_ID(), trxName);
				MDocType orderDocType = MDocType.get(po.getCtx(), order.getC_DocTypeTarget_ID());
				if(orderDocType.getDocSubTypeSO().equals(MOrder.DocSubTypeSO_OnCredit)
						|| orderDocType.getDocSubTypeSO().equals(MOrder.DocSubTypeSO_POS)
						|| orderDocType.getDocSubTypeSO().equals(MOrder.DocSubTypeSO_Prepay))
				{

					;//Noting to DO

				}else{//Create invoice



					if(orderDocType.getC_DocTypeInvoice_ID() == 0)
						return null;

					MInvoiceJP invoice = new MInvoiceJP (order, orderDocType.getC_DocTypeInvoice_ID(), io.getDateAcct());//JPIERE-0295
					if (!invoice.save(trxName))
					{
						log.warning("Could not create Invoice: "+ io.getDocumentInfo());
						return null;
					}

					MInOutLine[] sLines = io.getLines(false);
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
						if (!iLine.save(io.get_TrxName()))
						{
							log.warning("Could not create Invoice Line from Shipment Line: "+ invoice.getDocumentInfo());
							return null;
						}
						//
						sLine.setIsInvoiced(true);
						if (!sLine.save(trxName))
						{
							log.warning("Could not update Shipment line: " + sLine);
						}
					}//for

					if (!invoice.processIt(DocAction.ACTION_Complete))
						throw new AdempiereException("Failed when processing document - " + invoice.getProcessMsg());

					invoice.saveEx(trxName);
					if (!invoice.getDocStatus().equals(DocAction.STATUS_Completed))
					{
						log.warning("Could not Completed Invoice: "+ invoice.getDocumentInfo());
						return null;
					}

					//Allocation
					if(!isReversal && order.getC_Payment_ID() > 0)
					{

						MPayment payment = new MPayment(io.getCtx(),order.getC_Payment_ID(), trxName);
						if(!payment.isAllocated() && payment.getC_Order_ID()== order.getC_Order_ID() && payment.isPrepayment()
								&& (payment.getDocStatus().equals(DocAction.STATUS_Completed) || payment.getDocStatus().equals(DocAction.STATUS_Closed))
								&& (invoice.getC_Currency_ID() == payment.getC_Currency_ID()) )
						{
							BigDecimal payAmt = payment.getPayAmt();
							BigDecimal allocatedAmt = payment.getAllocatedAmt();
							BigDecimal allocatAmt = payAmt;
							if(allocatedAmt == null)
								allocatedAmt = Env.ZERO;

							if(payment.isReceipt()){
								allocatAmt = payAmt.subtract(allocatedAmt);
								allocatAmt = invoice.getGrandTotal().compareTo(allocatAmt) > 0 ? allocatAmt : invoice.getGrandTotal();
							}else if(!payment.isReceipt()){
								allocatAmt = payAmt.add(allocatedAmt);
								allocatAmt = invoice.getGrandTotal().compareTo(allocatAmt) > 0 ? allocatAmt : invoice.getGrandTotal();
								allocatAmt = allocatAmt.negate();
							}

							if((payment.isReceipt() && allocatAmt.compareTo(Env.ZERO) > 0)
									|| (!payment.isReceipt() && allocatAmt.compareTo(Env.ZERO) < 0) )
							{
								MAllocationHdr alloc = new MAllocationHdr(io.getCtx(), false, invoice.getDateAcct(), invoice.getC_Currency_ID(),
											Msg.translate(io.getCtx(), "C_Payment_ID")	+ ": " + payment.getDocumentNo(), trxName);
								alloc.setAD_Org_ID(invoice.getAD_Org_ID());
								alloc.setDateAcct(invoice.getDateAcct()); // in case date acct is different from datetrx in payment; IDEMPIERE-1532 tbayen
								if (!alloc.save(trxName))
								{
									log.severe("Allocations not created");
									return null;
								}

								MAllocationLine aLine = new MAllocationLine (alloc, allocatAmt, Env.ZERO, Env.ZERO, Env.ZERO);
								aLine.setDocInfo(invoice.getC_BPartner_ID(), order.getC_Order_ID(), invoice.getC_Invoice_ID());
								aLine.setPaymentInfo(payment.getC_Payment_ID(), 0);
								if (!aLine.save(trxName))
									log.warning("P.Allocations - line not saved");

								if (!alloc.processIt(DocAction.ACTION_Complete))
									throw new AdempiereException("Failed when processing document - " + alloc.getProcessMsg());
								if (!alloc.save(trxName))
								{
									log.severe("Allocation not Save after Complete");
									return null;
								}
							}
						}

					}//Allocation

				}//Create invoice

			}//if(ioDocType.get_ValueAsBoolean("IsCreateInvoiceJP"))

		}//JPiere-0219


		return null;
	}

}
