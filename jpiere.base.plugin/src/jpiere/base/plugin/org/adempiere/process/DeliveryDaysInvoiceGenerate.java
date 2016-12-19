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
package jpiere.base.plugin.org.adempiere.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import jpiere.base.plugin.util.JPierePaymentTerms;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MBPartner;
import org.compiere.model.MClient;
import org.compiere.model.MCurrency;
import org.compiere.model.MDocType;
import org.compiere.model.MInOut;
import org.compiere.model.MInOutLine;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MInvoicePaySchedule;
import org.compiere.model.MLocation;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderPaySchedule;
import org.compiere.model.MPaymentTerm;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Language;
import org.compiere.util.Msg;
import org.compiere.util.Trx;

/**
 *	JPIERE-0154,0155
 *	Generate Invoices that add Delivery Days
 *
 *  @author Jorg Janke
 *  @author Hideaki Hagiwara (h.hagiwara@oss-erp.co.jp)
 *
 */
public class DeliveryDaysInvoiceGenerate extends SvrProcess
{
	/**	Org						*/
	private int			p_AD_Org_ID = 0;
	/**	Doc Type Order		*/
	private int 		p_C_DocTypeOrder_ID = 0;
	/**Movement Date*/
	private Timestamp  p_MovementDate_From;
	private Timestamp  p_MovementDate_To;
	/**Document Action*/
	private String p_DocAction = DocAction.ACTION_Complete;

	/**	Warehouse				*/
	private int			p_M_Warehouse_ID = 0;
	/**	BPartner Group				*/
	private int			p_C_BP_Group_ID = 0;
	/** BPartner				*/
	private int			p_C_BPartner_ID = 0;



	/**	The current Invoice	*/
	private MInvoice 	m_invoice = null;

	/**	Per Invoice Savepoint */
	private Savepoint m_savepoint = null;

	/**	The current Shipment	*/
	private MInOut	 	m_ship = null;
	/** Numner of Invoices		*/
	private int			m_created = 0;
	/**	Line Number				*/
	private int			m_line = 0;
	/**	Business Partner		*/
	private MBPartner	m_bp = null;



	Timestamp SystemDate = new Timestamp(System.currentTimeMillis());

	/**
	 *  Prepare - e.g., get Parameters.
	 */
	protected void prepare()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
			else if (name.equals("AD_Org_ID"))
				p_AD_Org_ID = para[i].getParameterAsInt();
			else if (name.equals("C_DocType_ID"))
				p_C_DocTypeOrder_ID = para[i].getParameterAsInt();
			else if (name.equals("MovementDate"))
			{
				p_MovementDate_From = (Timestamp)para[i].getParameter();
				p_MovementDate_To = (Timestamp)para[i].getParameter_To();
//				if(p_MovementDate_To!=null)
//				{
//					Calendar cal = Calendar.getInstance();
//					cal.setTimeInMillis(p_MovementDate_To.getTime());
//					cal.add(Calendar.DAY_OF_MONTH, 1);
//					p_MovementDate_To = new Timestamp(cal.getTimeInMillis());
//				}
			}else if(name.equals("DocAction")){
				p_DocAction = para[i].getParameterAsString();
			}
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}

	}	//	prepare

	/**
	 * 	Generate Invoices
	 *	@return info
	 *	@throws Exception
	 */
	protected String doIt () throws Exception
	{
		//Get Order Doc that shiped and not invoice
		List<MOrder> oList = new ArrayList<MOrder>();

		if(getParameter().length > 0)//From Process
		{
			StringBuilder getInOutSQL;
			getInOutSQL = new StringBuilder("SELECT distinct o.* FROM M_InOut io ")
								.append("INNER JOIN C_DocType dt on(io.C_DocType_ID = dt.C_DocType_ID) ")
								.append("INNER JOIN C_BPartner bp on(io.C_BPartner_ID = bp.C_BPartner_ID) ")
								.append("INNER JOIN M_InOutLine iol on(io.M_InOut_ID = iol.M_InOut_ID) ")
								.append("INNER JOIN C_OrderLine ol on(iol.C_OrderLine_ID = ol.C_OrderLine_ID) ")
								.append("INNER JOIN C_Order o on(ol.C_Order_ID=o.C_Order_ID) ")
								.append("INNER JOIN C_DocType odt on(o.C_DocType_ID=odt.C_DocType_ID) ")
									.append("WHERE io.DocStatus in ('CO','CL') ")
									.append("AND io.IsSOTrx ='Y' ")
									.append("AND io.IsInDispute = 'N' ")
									.append("AND dt.DocBaseType = 'MMS' ")
									.append("AND iol.isinvoiced = 'N' ")
									.append("AND ol.QtyDelivered<>ol.QtyInvoiced ")
									.append("AND io.AD_Client_ID="+getAD_Client_ID())
								;


			if(p_AD_Org_ID!=0)
			{
				getInOutSQL.append(" AND io.AD_Org_ID="+p_AD_Org_ID);
			}

			if(p_M_Warehouse_ID!=0)
			{
				getInOutSQL.append(" AND io.M_Warehouse_ID="+p_M_Warehouse_ID);
			}

			if(p_C_BP_Group_ID!=0)
			{
				getInOutSQL.append(" AND bp.C_BP_Group_ID="+p_C_BP_Group_ID);
			}

			if(p_C_BPartner_ID!=0)
			{
				getInOutSQL.append(" AND io.C_BPartner_ID="+p_C_BPartner_ID);
			}

			if(p_MovementDate_From != null)
			{
				getInOutSQL.append(" AND io.MovementDate > '").append(new SimpleDateFormat("yyyy-MM-dd").format(p_MovementDate_From)).append("'");
			}

			if(p_MovementDate_To != null)
			{
				getInOutSQL.append(" AND io.MovementDate < '").append(new SimpleDateFormat("yyyy-MM-dd").format(p_MovementDate_To)).append("'");
			}

			if(p_C_DocTypeOrder_ID != 0)
			{
				getInOutSQL.append(" AND odt.C_DocType_ID = "+p_C_DocTypeOrder_ID);
			}

			getInOutSQL.append(" ORDER BY o.M_Warehouse_ID, o.PriorityRule, o.C_BPartner_ID, o.Bill_Location_ID, o.C_Order_ID");

			PreparedStatement getInOutPSTMT = null;
			ResultSet getInOutRS = null;

			try
			{
				getInOutPSTMT = DB.prepareStatement (getInOutSQL.toString(), get_TrxName());
				getInOutRS = getInOutPSTMT.executeQuery ();

				while (getInOutRS.next ())
					oList.add(new MOrder (getCtx(), getInOutRS, get_TrxName()));

			}catch(Exception e){
				throw new AdempiereException(e);
			}finally{
				DB.close(getInOutRS, getInOutPSTMT);
				getInOutRS = null;
				getInOutPSTMT = null;
			}

		}else{//From Info Window
			String whereClause = "EXISTS (SELECT T_Selection_ID FROM T_Selection WHERE T_Selection.AD_PInstance_ID=? " +
					"AND T_Selection.T_Selection_ID = C_Order.C_Order_ID)";

			oList = new Query(getCtx(), MOrder.Table_Name, whereClause, get_TrxName())
											.setClient_ID()
											.setParameters(new Object[]{getAD_PInstance_ID()})
											.list();
		}

		if(oList.size() < 1)
		{
			return Msg.getMsg(getCtx(), "NotFound");//* Not found *
		}

		for(MOrder order : oList)
		{
			MInOut[] shipments = order.getShipments();
			for (int i = 0; i < shipments.length; i++)
			{
				MInOut ship = shipments[i];

				if(!ship.getDocStatus().equals(DocAction.STATUS_Completed)
						&& !ship.getDocStatus().equals(DocAction.STATUS_Closed))
				{
					continue;
				}

				Timestamp dateInvoiced = ship.getDateAcct();

				//JPIERE-0229 Comment out
//				MDocType shipDocType = MDocType.get(ship.getCtx(), ship.getC_DocType_ID());
//				Timestamp dateInvoiced = MDeliveryDays.getInvoiceDate(ship, shipDocType.get_ValueAsBoolean("IsHolidayNotInspectionJP"));
//
//				if(!dateInvoiced.equals(ship.getDateAcct()))
//				{
//					//Repost Sipment document at dateInvoiced for principle of matching costs with revenues
//					ship.setDateAcct(dateInvoiced);
//					ship.saveEx(get_TrxName());
//					Doc.postImmediate(MAcctSchema.getClientAcctSchema(Env.getCtx(), ship.getAD_Client_ID()),
//														ship.get_Table_ID(), ship.get_ID(), true, get_TrxName());
//				}

				MInOutLine[] shipLines = ship.getLines(false);
				for (int j = 0; j < shipLines.length; j++)
				{
					MInOutLine shipLine = shipLines[j];
					if (!order.isOrderLine(shipLine.getC_OrderLine_ID()))
						continue;
					if (!shipLine.isInvoiced())
						createLine (order, ship, shipLine,dateInvoiced);
				}
				completeInvoice();
			}
		}

		StringBuilder msgreturn = new StringBuilder("@Created@ = ").append(m_created);
		return msgreturn.toString();
	}	//	doIt

	/**
	 * 	Create Invoice Line from Shipment
	 *	@param order order
	 *	@param ship shipment header
	 *	@param sLine shipment line
	 */
	private void createLine (MOrder order, MInOut ship, MInOutLine sLine,Timestamp dateInvoiced)
	{
		if (m_invoice == null)
		{
			try {
				if (m_savepoint != null)
					Trx.get(get_TrxName(), false).releaseSavepoint(m_savepoint);
				m_savepoint = Trx.get(get_TrxName(), false).setSavepoint(null);
			} catch (SQLException e) {
				throw new AdempiereException(e);
			}
			m_invoice = new MInvoice (order, 0, dateInvoiced);
			if (!m_invoice.save())
				throw new IllegalStateException(Msg.getMsg(getCtx(), "SaveError"));//Could not save changes:
		}

		//	Create Shipment Comment Line
		if (m_ship == null
			|| m_ship.getM_InOut_ID() != ship.getM_InOut_ID())
		{
			MDocType dt = MDocType.get(getCtx(), ship.getC_DocType_ID());
			if (m_bp == null || m_bp.getC_BPartner_ID() != ship.getC_BPartner_ID())
				m_bp = new MBPartner (getCtx(), ship.getC_BPartner_ID(), get_TrxName());

			//	Reference: Delivery: 12345 - 12.12.12
			MClient client = MClient.get(getCtx(), order.getAD_Client_ID ());
			String AD_Language = client.getAD_Language();
			if (client.isMultiLingualDocument() && m_bp.getAD_Language() != null)
				AD_Language = m_bp.getAD_Language();
			if (AD_Language == null)
				AD_Language = Language.getBaseAD_Language();
			java.text.SimpleDateFormat format = DisplayType.getDateFormat
				(DisplayType.Date, Language.getLanguage(AD_Language));
			StringBuilder reference = new StringBuilder().append(dt.getPrintName(m_bp.getAD_Language()))
				.append(": ").append(ship.getDocumentNo())
				.append(" - ").append(format.format(ship.getMovementDate()));
			m_ship = ship;
			//
			MInvoiceLine line = new MInvoiceLine (m_invoice);
			line.setIsDescription(true);
			line.setDescription(reference.toString());
			line.setLine(m_line + sLine.getLine() - 2);
			if (!line.save())
				throw new IllegalStateException(Msg.getMsg(getCtx(), "SaveError"));//Could not save changes:
			//	Optional Ship Address if not Bill Address
			if (order.getBill_Location_ID() != ship.getC_BPartner_Location_ID())
			{
				MLocation addr = MLocation.getBPLocation(getCtx(), ship.getC_BPartner_Location_ID(), null);
				line = new MInvoiceLine (m_invoice);
				line.setIsDescription(true);
				line.setDescription(addr.toString());
				line.setLine(m_line + sLine.getLine() - 1);
				if (!line.save())
					throw new IllegalStateException(Msg.getMsg(getCtx(), "SaveError"));//Could not save changes:
			}
		}
		//
		MInvoiceLine line = new MInvoiceLine (m_invoice);
		line.setShipLine(sLine);
		if (sLine.sameOrderLineUOM())
			line.setQtyEntered(sLine.getQtyEntered());
		else
			line.setQtyEntered(sLine.getMovementQty());
		line.setQtyInvoiced(sLine.getMovementQty());
		line.setLine(m_line + sLine.getLine());
		if (!line.save())
			throw new IllegalStateException(Msg.getMsg(getCtx(), "SaveError"));//Could not save changes:
		//	Link
		sLine.setIsInvoiced(true);
		if (!sLine.save())
			throw new IllegalStateException(Msg.getMsg(getCtx(), "SaveError"));//Could not save changes:

		if (log.isLoggable(Level.FINE)) log.fine(line.toString());
	}	//	createLine

	/**
	 * 	Complete Invoice
	 */
	private void completeInvoice()
	{

		MOrder order = new MOrder(getCtx(), m_invoice.getC_Order_ID(), get_TrxName());
		if (order != null) {
			m_invoice.setPaymentRule(order.getPaymentRule());
			MPaymentTerm paymentTerm = new MPaymentTerm(getCtx(), order.getC_PaymentTerm_ID(), get_TrxName());
			if(paymentTerm.get_ValueAsBoolean("IsPaymentTermsJP"))
			{
				MPaymentTerm[] paymentTerms = JPierePaymentTerms.getPaymentTerms(getCtx(),m_invoice.getC_BPartner_ID());
				Timestamp dateAcct = m_invoice.getDateAcct();
				String dateString = new SimpleDateFormat("dd").format(dateAcct);
				Integer dateInt = new Integer(dateString);

				for(int i = 0; i < paymentTerms.length; i++)
				{
					int FixMonthCutoff = paymentTerms[i].getFixMonthCutoff();
					if(dateInt.intValue() < FixMonthCutoff)
					{
						m_invoice.setC_PaymentTerm_ID(paymentTerms[i].get_ID());
						break;
					}
				}

			}else{
				m_invoice.setC_PaymentTerm_ID(order.getC_PaymentTerm_ID());
			}
			m_invoice.saveEx();
			m_invoice.load(m_invoice.get_TrxName()); // refresh from DB
			// copy payment schedule from order if invoice doesn't have a current payment schedule
			MOrderPaySchedule[] opss = MOrderPaySchedule.getOrderPaySchedule(getCtx(), order.getC_Order_ID(), 0, get_TrxName());
			MInvoicePaySchedule[] ipss = MInvoicePaySchedule.getInvoicePaySchedule(getCtx(), m_invoice.getC_Invoice_ID(), 0, get_TrxName());
			if (ipss.length == 0 && opss.length > 0) {
				BigDecimal ogt = order.getGrandTotal();
				BigDecimal igt = m_invoice.getGrandTotal();
				BigDecimal percent = Env.ONE;
				if (ogt.compareTo(igt) != 0)
					percent = igt.divide(ogt, 10, BigDecimal.ROUND_HALF_UP);
				MCurrency cur = MCurrency.get(order.getCtx(), order.getC_Currency_ID());
				int scale = cur.getStdPrecision();

				for (MOrderPaySchedule ops : opss) {
					MInvoicePaySchedule ips = new MInvoicePaySchedule(getCtx(), 0, get_TrxName());
					PO.copyValues(ops, ips);
					if (percent != Env.ONE) {
						BigDecimal propDueAmt = ops.getDueAmt().multiply(percent);
						if (propDueAmt.scale() > scale)
							propDueAmt = propDueAmt.setScale(scale, BigDecimal.ROUND_HALF_UP);
						ips.setDueAmt(propDueAmt);
					}
					ips.setC_Invoice_ID(m_invoice.getC_Invoice_ID());
					ips.setAD_Org_ID(ops.getAD_Org_ID());
					ips.setProcessing(ops.isProcessing());
					ips.setIsActive(ops.isActive());
					ips.saveEx();
				}
				m_invoice.validatePaySchedule();
				m_invoice.saveEx();
			}


			if(!p_DocAction.equals(DocAction.ACTION_Complete))
				p_DocAction = DocAction.ACTION_Prepare;

			if (!m_invoice.processIt(p_DocAction))
			{
				log.warning("completeInvoice - failed: " + m_invoice);
				addBufferLog(0, null, null,"completeInvoice - failed: " + m_invoice,m_invoice.get_Table_ID(),m_invoice.getC_Invoice_ID());
				throw new IllegalStateException(Msg.getMsg(getCtx(), "ProcessFailed") +" "+ m_invoice + " - " + m_invoice.getProcessMsg());

			}
			m_invoice.saveEx();

			String message = Msg.parseTranslation(getCtx(), "@InvoiceProcessed@ " + m_invoice.getDocumentNo());
			addBufferLog(m_invoice.getC_Invoice_ID(), m_invoice.getDateInvoiced(), null, message, m_invoice.get_Table_ID(), m_invoice.getC_Invoice_ID());
			m_created++;
		}//if (order != null)

		m_invoice = null;
		m_ship = null;
		m_line = 0;

	}	//	completeInvoice

}	//	InvoiceGenerate
