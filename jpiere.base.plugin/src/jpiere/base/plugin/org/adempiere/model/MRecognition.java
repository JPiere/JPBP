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

import java.io.File;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.adempiere.exceptions.BPartnerNoAddressException;
import org.adempiere.exceptions.PeriodClosedException;
import org.compiere.model.MBPartner;
import org.compiere.model.MBPartnerLocation;
import org.compiere.model.MCurrency;
import org.compiere.model.MDocType;
import org.compiere.model.MInOut;
import org.compiere.model.MInOutConfirm;
import org.compiere.model.MInOutLine;
import org.compiere.model.MInOutLineConfirm;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MOrg;
import org.compiere.model.MPeriod;
import org.compiere.model.MPriceList;
import org.compiere.model.MPriceListVersion;
import org.compiere.model.MRMA;
import org.compiere.model.MRMALine;
import org.compiere.model.MRefList;
import org.compiere.model.MSysConfig;
import org.compiere.model.MTax;
import org.compiere.model.MTaxProvider;
import org.compiere.model.MUser;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.print.MPrintFormat;
import org.compiere.print.ReportEngine;
import org.compiere.process.DocAction;
import org.compiere.process.DocOptions;
import org.compiere.process.DocumentEngine;
import org.compiere.process.ProcessInfo;
import org.compiere.process.ServerProcessCtl;
import org.compiere.util.CCache;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;


/**
 * JPIERE-0363
 *
 * @author Hideaki Hagiwara
 *
 */
public class MRecognition extends X_JP_Recognition implements DocAction,DocOptions
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -9210893813732918522L;

	/**
	 * 	Get Payments Of BPartner
	 *	@param ctx context
	 *	@param C_BPartner_ID id
	 *	@param trxName transaction
	 *	@return array
	 */
	public static MRecognition[] getOfBPartner (Properties ctx, int C_BPartner_ID, String trxName)
	{
		List<MRecognition> list = new Query(ctx, Table_Name, COLUMNNAME_C_BPartner_ID+"=?", trxName)
									.setParameters(C_BPartner_ID)
									.list();
		return list.toArray(new MRecognition[list.size()]);
	}	//	getOfBPartner

	/**
	 * 	Create new Recognition by copying
	 * 	@param from Recognition
	 * 	@param dateDoc date of the document date
	 *  @param acctDate original account date 
	 * 	@param C_DocTypeTarget_ID target doc type
	 * 	@param isSOTrx sales order
	 * 	@param counter create counter links
	 * 	@param trxName trx
	 * 	@param setOrder set Order links
	 *	@return Recognition
	 */
	public static MRecognition copyFrom (MRecognition from, Timestamp dateDoc, Timestamp dateAcct,
		int C_DocTypeTarget_ID, boolean isSOTrx, boolean counter,
		String trxName, boolean setOrder)
	{
		return copyFrom (from, dateDoc, dateAcct,
				C_DocTypeTarget_ID, isSOTrx, counter,
				trxName, setOrder,null);
	}

	/**
	 * 	Create new Recognition by copying
	 * 	@param from invoice
	 * 	@param dateDoc date of the document date
	 *  @param acctDate original account date 
	 * 	@param C_DocTypeTarget_ID target doc type
	 * 	@param isSOTrx sales order
	 * 	@param counter create counter links
	 * 	@param trxName trx
	 * 	@param setOrder set Order links
	 *  @param Document Number for reversed invoices
	 *	@return Recognition
	 */
	public static MRecognition copyFrom (MRecognition from, Timestamp dateDoc, Timestamp dateAcct,
		int C_DocTypeTarget_ID, boolean isSOTrx, boolean counter,
		String trxName, boolean setOrder, String documentNo)
	{
		MRecognition to = new MRecognition (from.getCtx(), 0, trxName);
		PO.copyValues (from, to, from.getAD_Client_ID(), from.getAD_Org_ID());
		to.set_ValueNoCheck ("JP_Recognition_ID", I_ZERO);
		to.set_ValueNoCheck ("DocumentNo", documentNo);
		to.setJP_Contract_ID(from.getJP_Contract_ID());
		to.setJP_ContractContent_ID(from.getJP_ContractContent_ID());
		to.setJP_ContractProcPeriod_ID(from.getJP_ContractProcPeriod_ID());
		to.setC_Order_ID(from.getC_Order_ID());
		to.setM_InOut_ID(from.getM_InOut_ID());
		//
		to.setDocStatus (DOCSTATUS_Drafted);		//	Draft
		to.setDocAction(DOCACTION_Complete);
		//
		to.setC_DocType_ID(0);
		to.setC_DocTypeTarget_ID (C_DocTypeTarget_ID);
		to.setIsSOTrx(isSOTrx);
		//
		to.setDateInvoiced (dateDoc);
		to.setDateAcct (dateAcct);
		to.setDatePrinted(null);
		to.setIsPrinted (false);
		//
		to.setIsApproved (false);

		//
		//	Amounts are updated by trigger when adding lines
		to.setGrandTotal(Env.ZERO);
		to.setTotalLines(Env.ZERO);
		//
		to.setPosted (false);
		to.setProcessed (false);
		//[ 1633721 ] Reverse Documents- Processing=Y
		to.setProcessing(false);
		//	delete references

		if (counter)
		{
			MOrg org = MOrg.get(from.getCtx(), from.getAD_Org_ID());
			int counterC_BPartner_ID = org.getLinkedC_BPartner_ID(trxName);
			if (counterC_BPartner_ID == 0)
				return null;
			to.setBPartner(MBPartner.get(from.getCtx(), counterC_BPartner_ID));
			//	Try to find Order link
			if (from.getC_Order_ID() != 0)
			{
				MOrder peer = new MOrder (from.getCtx(), from.getC_Order_ID(), from.get_TrxName());
				if (peer.getRef_Order_ID() != 0)
					to.setC_Order_ID(peer.getRef_Order_ID());
			}
			// Try to find RMA link
			if (from.getM_RMA_ID() != 0)
			{
				MRMA peer = new MRMA (from.getCtx(), from.getM_RMA_ID(), from.get_TrxName());
				if (peer.getRef_RMA_ID() > 0)
					to.setM_RMA_ID(peer.getRef_RMA_ID());
			}
			//
		}
//		else
//			to.setRef_Invoice_ID(0);

		to.saveEx(trxName);
//		if (counter)
//			from.setRef_Invoice_ID(to.getC_Invoice_ID());

		//	Lines
		if (to.copyLinesFrom(from, counter, setOrder) == 0)
			throw new IllegalStateException("Could not create Recognition Lines");

		return to;
	}
	
	/** 
	 *  @deprecated
	 * 	Create new Recognition by copying
	 * 	@param from invoice
	 * 	@param dateDoc date of the document date
	 * 	@param C_DocTypeTarget_ID target doc type
	 * 	@param isSOTrx sales order
	 * 	@param counter create counter links
	 * 	@param trxName trx
	 * 	@param setOrder set Order links
	 *	@return Recognition
	 */
	public static MRecognition copyFrom (MRecognition from, Timestamp dateDoc,
		int C_DocTypeTarget_ID, boolean isSOTrx, boolean counter,
		String trxName, boolean setOrder)
	{
		MRecognition to = copyFrom ( from, dateDoc, dateDoc,
				 C_DocTypeTarget_ID, isSOTrx, counter,
				trxName, setOrder);
		return to;
	}	//	copyFrom

	/**
	 * 	Get PDF File Name
	 *	@param documentDir directory
	 * 	@param JP_Recognition_ID invoice
	 *	@return file name
	 */
	public static String getPDFFileName (String documentDir, int JP_Recognition_ID)
	{
		StringBuilder sb = new StringBuilder (documentDir);
		if (sb.length() == 0)
			sb.append(".");
		if (!sb.toString().endsWith(File.separator))
			sb.append(File.separator);
		sb.append("JP_Recognition_ID_")
			.append(JP_Recognition_ID)
			.append(".pdf");
		return sb.toString();
	}	//	getPDFFileName


	/**
	 * 	Get MRecognition from Cache
	 *	@param ctx context
	 *	@param JP_Recognition_ID id
	 *	@return MRecognition
	 */
	public static MRecognition get (Properties ctx, int JP_Recognition_ID)
	{
		Integer key = new Integer (JP_Recognition_ID);
		MRecognition retValue = (MRecognition) s_cache.get (key);
		if (retValue != null)
			return retValue;
		retValue = new MRecognition (ctx, JP_Recognition_ID, null);
		if (retValue.get_ID () != 0)
			s_cache.put (key, retValue);
		return retValue;
	} //	get

	/**	Cache						*/
	private static CCache<Integer,MRecognition>	s_cache	= new CCache<Integer,MRecognition>(Table_Name, 20, 2);	//	2 minutes


	/**************************************************************************
	 * 	Recognition Constructor
	 * 	@param ctx context
	 * 	@param JP_Recognition_ID invoice or 0 for new
	 * 	@param trxName trx name
	 */
	public MRecognition (Properties ctx, int JP_Recognition_ID, String trxName)
	{
		super (ctx, JP_Recognition_ID, trxName);
		if (JP_Recognition_ID == 0)
		{
			setDocStatus (DOCSTATUS_Drafted);		//	Draft
			setDocAction (DOCACTION_Complete);


			setDateInvoiced (new Timestamp (System.currentTimeMillis ()));
			setDateAcct (new Timestamp (System.currentTimeMillis ()));

			setTotalLines (Env.ZERO);
			setGrandTotal (Env.ZERO);
			//
			setIsSOTrx (true);
			setIsTaxIncluded (false);
			setIsApproved (false);

			setSendEMail (false);
			setIsPrinted (false);
			setIsSelfService(false);
			setPosted(false);
			super.setProcessed (false);
			setProcessing(false);
		}
	}	//	MRecognition

	/**
	 *  Load Constructor
	 *  @param ctx context
	 *  @param rs result set record
	 *	@param trxName transaction
	 */
	public MRecognition (Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}	//	MRecognition

	/** TODO なんだこのメソッドは?? どんな使い道があるのか？　いらないんじゃない？
	 * 
	 * 	Create Recognition from Order
	 *	@param order order
	 *	@param C_DocTypeTarget_ID target document type
	 *	@param DateAcct date or null
	 */
	public MRecognition (MOrder order, int C_DocTypeTarget_ID, Timestamp DateAcct)
	{
		this (order.getCtx(), 0, order.get_TrxName());
		setClientOrg(order);
		setOrder(order);	//	set base settings
		//
		if (C_DocTypeTarget_ID <= 0)
		{
			MDocType odt = MDocType.get(order.getCtx(), order.getC_DocType_ID());
			if (odt != null)
			{
				C_DocTypeTarget_ID = odt.get_ValueAsInt("JP_DocTypeRecognition_ID");
				if (C_DocTypeTarget_ID <= 0)
					throw new AdempiereException("@NotFound@ @C_DocTypeInvoice_ID@ - @C_DocType_ID@:"+odt.get_Translation(MDocType.COLUMNNAME_Name));
			}
		}
		setC_DocTypeTarget_ID(C_DocTypeTarget_ID);
		setC_DocType_ID(C_DocTypeTarget_ID);
		setDateInvoiced(DateAcct);
		setDateAcct(DateAcct);
		//
		setSalesRep_ID(order.getSalesRep_ID());
		//
		setC_BPartner_ID(order.getBill_BPartner_ID());
		setC_BPartner_Location_ID(order.getBill_Location_ID());
		setAD_User_ID(order.getBill_User_ID());
		
		//Contract Info
		setJP_Contract_ID(order.get_ValueAsInt("JP_Contract_ID"));
		setJP_ContractContent_ID(order.get_ValueAsInt("JP_ContractContent_ID"));
		setJP_ContractProcPeriod_ID(order.get_ValueAsInt("JP_ContractProcPeriod_ID"));
		
	}	//	MRecognition


	/**	Invoice Lines			*/
	private MRecognitionLine[]	m_lines;
	/**	Invoice Taxes			*/
	private MRecognitionTax[]	m_taxes;
	/**	Logger			*/
	private static CLogger s_log = CLogger.getCLogger(MRecognition.class);

	/**
	 * 	Overwrite Client/Org if required
	 * 	@param AD_Client_ID client
	 * 	@param AD_Org_ID org
	 */
	public void setClientOrg (int AD_Client_ID, int AD_Org_ID)
	{
		super.setClientOrg(AD_Client_ID, AD_Org_ID);
	}	//	setClientOrg

	/**
	 * 	Set Business Partner Defaults & Details
	 * 	@param bp business partner
	 */
	public void setBPartner (MBPartner bp)
	{
		if (bp == null)
			return;

		setC_BPartner_ID(bp.getC_BPartner_ID());
		//	Set Defaults
		int ii = 0;	
		//
		if (isSOTrx())
			ii = bp.getM_PriceList_ID();
		else
			ii = bp.getPO_PriceList_ID();
		if (ii != 0)
			setM_PriceList_ID(ii);

		//	Set Locations
		MBPartnerLocation[] locs = bp.getLocations(false);
		if (locs != null)
		{
			for (int i = 0; i < locs.length; i++)
			{
				if ((locs[i].isBillTo() && isSOTrx())
				|| (locs[i].isPayFrom() && !isSOTrx()))
					setC_BPartner_Location_ID(locs[i].getC_BPartner_Location_ID());
			}
			//	set to first
			if (getC_BPartner_Location_ID() == 0 && locs.length > 0)
				setC_BPartner_Location_ID(locs[0].getC_BPartner_Location_ID());
		}
		if (getC_BPartner_Location_ID() == 0)
			log.log(Level.SEVERE, new BPartnerNoAddressException(bp).getLocalizedMessage()); //TODO: throw exception?

		//	Set Contact
		MUser[] contacts = bp.getContacts(false);
		if (contacts != null && contacts.length > 0)	//	get first User
			setAD_User_ID(contacts[0].getAD_User_ID());
	}	//	setBPartner

	/**
	 * 	Set Order References
	 * 	@param order order
	 */
	public void setOrder (MOrder order)
	{
		if (order == null)
			return;

		setC_Order_ID(order.getC_Order_ID());
		setIsSOTrx(order.isSOTrx());
		setIsDiscountPrinted(order.isDiscountPrinted());
		setIsSelfService(order.isSelfService());
		setSendEMail(order.isSendEMail());
		//
		setM_PriceList_ID(order.getM_PriceList_ID());
		setIsTaxIncluded(order.isTaxIncluded());
		setC_Currency_ID(order.getC_Currency_ID());
		setC_ConversionType_ID(order.getC_ConversionType_ID());
		//
		setPOReference(order.getPOReference());
		setDescription(order.getDescription());
		setDateOrdered(order.getDateOrdered());
		//
		setBill_BPartner_ID(order.getBill_BPartner_ID());
		setBill_Location_ID(order.getBill_Location_ID());
		setBill_User_ID(order.getBill_User_ID());
		//
		setAD_OrgTrx_ID(order.getAD_OrgTrx_ID());
		setC_Project_ID(order.getC_Project_ID());
		setC_Campaign_ID(order.getC_Campaign_ID());
		setC_Activity_ID(order.getC_Activity_ID());
		setUser1_ID(order.getUser1_ID());
		setUser2_ID(order.getUser2_ID());
	}	//	setOrder

	/**
	 * 	Set Shipment References
	 * 	@param ship shipment
	 */
	public void setShipment (MInOut ship)
	{
		if (ship == null)
			return;

		setIsSOTrx(ship.isSOTrx());
		//
		MBPartner bp = new MBPartner (getCtx(), ship.getC_BPartner_ID(), null);
		setBPartner (bp);
		//
		setAD_User_ID(ship.getAD_User_ID());
		//
		setSendEMail(ship.isSendEMail());
		//
		setPOReference(ship.getPOReference());
		setDescription(ship.getDescription());
		setDateOrdered(ship.getDateOrdered());
		//
		setAD_OrgTrx_ID(ship.getAD_OrgTrx_ID());
		setC_Project_ID(ship.getC_Project_ID());
		setC_Campaign_ID(ship.getC_Campaign_ID());
		setC_Activity_ID(ship.getC_Activity_ID());
		setUser1_ID(ship.getUser1_ID());
		setUser2_ID(ship.getUser2_ID());
		//
		if (ship.getC_Order_ID() != 0)
		{
			setC_Order_ID(ship.getC_Order_ID());
			MOrder order = new MOrder (getCtx(), ship.getC_Order_ID(), get_TrxName());
			setIsDiscountPrinted(order.isDiscountPrinted());
			setM_PriceList_ID(order.getM_PriceList_ID());
			setIsTaxIncluded(order.isTaxIncluded());
			setC_Currency_ID(order.getC_Currency_ID());
			setC_ConversionType_ID(order.getC_ConversionType_ID());
			//
			MDocType dt = MDocType.get(getCtx(), order.getC_DocType_ID());
			if (dt.getC_DocTypeInvoice_ID() != 0)
				setC_DocTypeTarget_ID(dt.getC_DocTypeInvoice_ID());
			// Overwrite Recognition BPartner
			setC_BPartner_ID(order.getBill_BPartner_ID());
			// Overwrite Recognition Address
			setC_BPartner_Location_ID(order.getBill_Location_ID());
			// Overwrite Contact
			setAD_User_ID(order.getBill_User_ID());
			//
		}
        // Check if Shipment/Receipt is based on RMA
        if (ship.getM_RMA_ID() != 0)
        {
            setM_RMA_ID(ship.getM_RMA_ID());

            MRMA rma = new MRMA(getCtx(), ship.getM_RMA_ID(), get_TrxName());
            // Retrieves the invoice DocType
            MDocType dt = MDocType.get(getCtx(), rma.getC_DocType_ID());
            if (dt.getC_DocTypeInvoice_ID() != 0)
            {
                setC_DocTypeTarget_ID(dt.getC_DocTypeInvoice_ID());
            }
            setIsSOTrx(rma.isSOTrx());

            MOrder rmaOrder = rma.getOriginalOrder();
            if (rmaOrder != null) {
                setM_PriceList_ID(rmaOrder.getM_PriceList_ID());
                setIsTaxIncluded(rmaOrder.isTaxIncluded());
                setC_Currency_ID(rmaOrder.getC_Currency_ID());
                setC_ConversionType_ID(rmaOrder.getC_ConversionType_ID());
                setC_BPartner_Location_ID(rmaOrder.getBill_Location_ID());
            }
        }

	}	//	setShipment


	/**
	 * 	Get Grand Total
	 * 	@param creditMemoAdjusted adjusted for CM (negative)
	 *	@return grand total
	 */
	public BigDecimal getGrandTotal (boolean creditMemoAdjusted)
	{
		if (!creditMemoAdjusted)
			return super.getGrandTotal();
		//
		BigDecimal amt = getGrandTotal();
		if (isCreditMemo())
			return amt.negate();
		return amt;
	}	//	getGrandTotal

	/**
	 * 	Get Total Lines
	 * 	@param creditMemoAdjusted adjusted for CM (negative)
	 *	@return total lines
	 */
	public BigDecimal getTotalLines (boolean creditMemoAdjusted)
	{
		if (!creditMemoAdjusted)
			return super.getTotalLines();
		//
		BigDecimal amt = getTotalLines();
		if (isCreditMemo())
			return amt.negate();
		return amt;
	}	//	getTotalLines

	/**
	 * 	Get Invoice Lines of Invoice
	 * 	@param whereClause starting with AND
	 * 	@return lines
	 */
	private MRecognitionLine[] getLines (String whereClause)
	{
		String whereClauseFinal = "JP_Recognition_ID=? ";
		if (whereClause != null)
			whereClauseFinal += whereClause;
		List<MRecognitionLine> list = new Query(getCtx(), I_JP_RecognitionLine.Table_Name, whereClauseFinal, get_TrxName())
										.setParameters(getJP_Recognition_ID())
										.setOrderBy(I_JP_RecognitionLine.COLUMNNAME_Line)
										.list();
		return list.toArray(new MRecognitionLine[list.size()]);
	}	//	getLines

	/**
	 * 	Get Invoice Lines
	 * 	@param requery
	 * 	@return lines
	 */
	public MRecognitionLine[] getLines (boolean requery)
	{
		if (m_lines == null || m_lines.length == 0 || requery)
			m_lines = getLines(null);
		set_TrxName(m_lines, get_TrxName());
		return m_lines;
	}	//	getLines

	/**
	 * 	Get Lines of Invoice
	 * 	@return lines
	 */
	public MRecognitionLine[] getLines()
	{
		return getLines(false);
	}	//	getLines


	/**
	 * 	Renumber Lines
	 *	@param step start and step
	 */
	public void renumberLines (int step)
	{
		int number = step;
		MRecognitionLine[] lines = getLines(false);
		for (int i = 0; i < lines.length; i++)
		{
			MRecognitionLine line = lines[i];
			line.setLine(number);
			line.saveEx();
			number += step;
		}
		m_lines = null;
	}	//	renumberLines

	/**
	 * 	Copy Lines From other Invoice.
	 *	@param otherRecognition invoice
	 * 	@param counter create counter links
	 * 	@param setOrder set order links
	 *	@return number of lines copied
	 */
	public int copyLinesFrom (MRecognition otherRecognition, boolean counter, boolean setOrder)
	{
		if (isProcessed() || isPosted() || otherRecognition == null)
			return 0;
		MRecognitionLine[] fromLines = otherRecognition.getLines(false);
		int count = 0;
		for (int i = 0; i < fromLines.length; i++)
		{
			MRecognitionLine line = new MRecognitionLine (getCtx(), 0, get_TrxName());
			MRecognitionLine fromLine = fromLines[i];
			if (counter)	//	header
				PO.copyValues (fromLine, line, getAD_Client_ID(), getAD_Org_ID());
			else
				PO.copyValues (fromLine, line, fromLine.getAD_Client_ID(), fromLine.getAD_Org_ID());
			line.setJP_Recognition_ID(getJP_Recognition_ID());
			line.setRecognition(this);
			line.set_ValueNoCheck ("JP_RecognitionLine_ID", I_ZERO);	// new
			
			line.setC_OrderLine_ID(fromLine.getC_OrderLine_ID());
			line.setM_RMALine_ID(fromLine.getM_RMALine_ID());
			line.setM_InOutLine_ID(fromLine.getM_InOutLine_ID());
			line.setJP_ContractLine_ID(fromLine.getJP_ContractLine_ID());
			line.setJP_ContractProcPeriod_ID(fromLine.getJP_ContractProcPeriod_ID());
			
			line.setQtyEntered(fromLine.getQtyEntered());
			line.setQtyInvoiced(fromLine.getQtyInvoiced());
			line.setJP_QtyRecognized(fromLine.getJP_QtyRecognized());
			line.setJP_TargetQtyRecognized(fromLine.getJP_TargetQtyRecognized());
			
			line.setM_AttributeSetInstance_ID(0);
			line.setS_ResourceAssignment_ID(0);
			//	New Tax
			if (getC_BPartner_ID() != otherRecognition.getC_BPartner_ID())
				line.setTax();	//	recalculate
			//
			if (counter)
			{
//				line.setRef_InvoiceLine_ID(fromLine.getC_InvoiceLine_ID());
				if (fromLine.getC_OrderLine_ID() != 0)
				{
//					MRecognitionLine peer = new MRecognitionLine(getCtx(), fromLine.getC_OrderLine_ID(), get_TrxName());
//					if (peer.getRef_OrderLine_ID() != 0)
//						line.setC_OrderLine_ID(peer.getRef_OrderLine_ID());
				}
				line.setM_InOutLine_ID(0);
				if (fromLine.getM_InOutLine_ID() != 0)
				{
//					MRecognitionLine peer = new MRecognitionLine (getCtx(), fromLine.getM_InOutLine_ID(), get_TrxName());
//					if (peer.getRef_InOutLine_ID() != 0)
//						line.setM_InOutLine_ID(peer.getRef_InOutLine_ID());
				}
			}
			//
			line.setProcessed(false);
			if (line.save(get_TrxName()))
				count++;
			//	Cross Link
			if (counter)
			{
//				fromLine.setRef_InvoiceLine_ID(line.getC_InvoiceLine_ID());
//				fromLine.saveEx(get_TrxName());
			}

			// end MZ
		}
		if (fromLines.length != count)
			log.log(Level.SEVERE, "Line difference - From=" + fromLines.length + " <> Saved=" + count);
		return count;
	}	//	copyLinesFrom

	/** Reversal Flag		*/
	private boolean m_reversal = false;

	/**
	 * 	Set Reversal
	 *	@param reversal reversal
	 */
	private void setReversal(boolean reversal)
	{
		m_reversal = reversal;
	}	//	setReversal
	/**
	 * 	Is Reversal
	 *	@return reversal
	 */
	public boolean isReversal()
	{
		return m_reversal;
	}	//	isReversal

	/**
	 * 	Get Taxes
	 *	@param requery requery
	 *	@return array of taxes
	 */
	public MRecognitionTax[] getTaxes (boolean requery)
	{
		if (m_taxes != null && !requery)
			return m_taxes;

		final String whereClause = MRecognitionTax.COLUMNNAME_JP_Recognition_ID+"=?";
		List<MRecognitionTax> list = new Query(getCtx(), I_JP_RecognitionTax.Table_Name, whereClause, get_TrxName())
										.setParameters(get_ID())
										.list();
		m_taxes = list.toArray(new MRecognitionTax[list.size()]);
		return m_taxes;
	}	//	getTaxes

	/**
	 * 	Add to Description
	 *	@param description text
	 */
	public void addDescription (String description)
	{
		String desc = getDescription();
		if (desc == null)
			setDescription(description);
		else{
			StringBuilder msgd = new StringBuilder(desc).append(" | ").append(description);
			setDescription(msgd.toString());
		}
	}	//	addDescription

	/**
	 * 	Is it a Credit Memo?
	 *	@return true if CM
	 */
	public boolean isCreditMemo()
	{
		MDocType dt = MDocType.get(getCtx(),
			getC_DocType_ID()==0 ? getC_DocTypeTarget_ID() : getC_DocType_ID());
		return MDocType.DOCBASETYPE_APCreditMemo.equals(dt.getDocBaseType())
			|| MDocType.DOCBASETYPE_ARCreditMemo.equals(dt.getDocBaseType());
	}	//	isCreditMemo

	/**
	 * 	Set Processed.
	 * 	Propergate to Lines/Taxes
	 *	@param processed processed
	 */
	public void setProcessed (boolean processed)
	{
		super.setProcessed (processed);
		if (get_ID() == 0)
			return;
		StringBuilder set = new StringBuilder("SET Processed='")
		.append((processed ? "Y" : "N"))
		.append("' WHERE JP_Recognition_ID=").append(getJP_Recognition_ID());
		
		StringBuilder msgdb = new StringBuilder("UPDATE JP_RecognitionLine ").append(set);
		int noLine = DB.executeUpdate(msgdb.toString(), get_TrxName());
		msgdb = new StringBuilder("UPDATE JP_RecognitionTax ").append(set);
		int noTax = DB.executeUpdate(msgdb.toString(), get_TrxName());
		m_lines = null;
		m_taxes = null;
		if (log.isLoggable(Level.FINE)) log.fine(processed + " - Lines=" + noLine + ", Tax=" + noTax);
	}	//	setProcessed




	/**************************************************************************
	 * 	Before Save
	 *	@param newRecord new
	 *	@return true
	 */
	protected boolean beforeSave (boolean newRecord)
	{
		
		if(newRecord || is_ValueChanged("M_InOut_ID"))
		{
			MInOut io = new MInOut(getCtx(), getM_InOut_ID(), get_TrxName());
			int JP_ContractContent_ID = io.get_ValueAsInt(MRecognition.COLUMNNAME_JP_ContractContent_ID);
			MContractContent contractContent = MContractContent.get(getCtx(), JP_ContractContent_ID);
			if(!contractContent.getJP_Contract_Acct().isPostingRecognitionDocJP())
			{
				//This Ship/Receipt document can not create Recognition doc.
				log.saveError("Error", Msg.getMsg(getCtx(), "JP_InOutDocCannotCreateRecog"));
				return false;
			}
			
			if(io.getC_Order_ID() == 0 && io.getM_RMA_ID() == 0)
			{
				//This Ship/Receipt document can not create Recognition doc.
				log.saveError("Error", "入出荷伝票が受発注伝票もしくは返品受付依頼伝票と結びついていません");//TODO メッセージ化
				return false;
			
			}else if(io.getC_Order_ID() > 0){
				
				MOrder order = new MOrder(getCtx(), io.getC_Order_ID(), get_TrxName());
				setC_Order_ID(order.getC_Order_ID());
				setDateOrdered(order.getDateOrdered());
				
			}else if(io.getM_RMA_ID() > 0){
			
				MRMA rma = new MRMA(getCtx(), io.getM_RMA_ID(), get_TrxName());
				setM_RMA_ID(rma.getM_RMA_ID());				
			}
			
			//Should be Comment out Because Recognitiond document can not create from InOut when DocAciton Complete
//			if(!io.getDocStatus().equals(DocAction.STATUS_Completed)
//					&& !getM_InOut().getDocStatus().equals(DocAction.STATUS_Closed))
//			{
//				//Document Status of Ship/Receipt doc must be Complete or Close
//				log.saveError("Error", Msg.getMsg(getCtx(), "JP_RecogInOutDocStatus"));
//				return false;
//			}
		}
		
		
		if(newRecord || is_ValueChanged("C_DocTypeTarget_ID"))
		{
			if(getC_Order_ID() > 0)
			{
				if(isSOTrx())
				{
					if(!(getC_DocTypeTarget().getDocBaseType().equals("JPR")))
					{
						log.saveError("Error", "返品受付伝票の場合はJPRの伝票タイプです。");//TODO メッセージ化
						return false;  
					}
				}else{
					if(!(getC_DocTypeTarget().getDocBaseType().equals("JPX")))
					{
						log.saveError("Error", "返品依頼伝票の場合はJPXの伝票タイプです。");//TODO メッセージ化
						return false;  
					}				
				}
			}else if(getM_RMA_ID() > 0){
				if(isSOTrx())
				{
				  if(!(getC_DocTypeTarget().getDocBaseType().equals("JPS")))
				  {
						log.saveError("Error", "返品受付伝票の場合はJPSの伝票タイプです。");//TODO メッセージ化
						return false;  
				  }
				}else{
				  if(!(getC_DocTypeTarget().getDocBaseType().equals("JPY")))
				  {
						log.saveError("Error", "返品依頼伝票の場合はJPYの伝票タイプです。");//TODO メッセージ化
						return false;  
				  }				
				}	
			}
		}
		
		
		log.fine("");
		//	No Partner Info - set Template
		if (getC_BPartner_ID() == 0)
			setBPartner(MBPartner.getTemplate(getCtx(), getAD_Client_ID()));
		if (getC_BPartner_Location_ID() == 0)
			setBPartner(new MBPartner(getCtx(), getC_BPartner_ID(), null));

		//	Price List
		if (getM_PriceList_ID() == 0)
		{
			int ii = Env.getContextAsInt(getCtx(), "#M_PriceList_ID");
			if (ii != 0)
			{
				MPriceList pl = new MPriceList(getCtx(), ii, null);
				if (isSOTrx() == pl.isSOPriceList())
					setM_PriceList_ID(ii);
			}
			
			if (getM_PriceList_ID() == 0)
			{
				String sql = "SELECT M_PriceList_ID FROM M_PriceList WHERE AD_Client_ID=? AND IsSOPriceList=? AND IsActive='Y' ORDER BY IsDefault DESC";
				ii = DB.getSQLValue (null, sql, getAD_Client_ID(), isSOTrx());
				if (ii != 0)
					setM_PriceList_ID (ii);
			}
		}

		//	Currency
		if (getC_Currency_ID() == 0)
		{
			String sql = "SELECT C_Currency_ID FROM M_PriceList WHERE M_PriceList_ID=?";
			int ii = DB.getSQLValue (null, sql, getM_PriceList_ID());
			if (ii != 0)
				setC_Currency_ID (ii);
			else
				setC_Currency_ID(Env.getContextAsInt(getCtx(), "#C_Currency_ID"));
		}

		//	Sales Rep
		if (getSalesRep_ID() == 0)
		{
			int ii = Env.getContextAsInt(getCtx(), "#SalesRep_ID");
			if (ii != 0)
				setSalesRep_ID (ii);
		}

		//	Document Type
		if(!newRecord || is_ValueChanged("C_DocTypeTarget_ID"))
		{
			setC_DocType_ID(getC_DocTypeTarget_ID());
		}

		// IDEMPIERE-1597 Price List and Date must be not-updateable
		if (!newRecord && (is_ValueChanged(COLUMNNAME_M_PriceList_ID) || is_ValueChanged(COLUMNNAME_DateInvoiced))) {
			int cnt = DB.getSQLValueEx(get_TrxName(), "SELECT COUNT(*) FROM JP_RecognitionLine WHERE JP_Recognition_ID=? AND M_Product_ID>0", getJP_Recognition_ID());
			if (cnt > 0) {
				if (is_ValueChanged(COLUMNNAME_M_PriceList_ID)) {
					log.saveError("Error", Msg.getMsg(getCtx(), "CannotChangePlIn"));
					return false;
				}
				if (is_ValueChanged(COLUMNNAME_DateInvoiced)) {
					MPriceList pList =  MPriceList.get(getCtx(), getM_PriceList_ID(), null);
					MPriceListVersion plOld = pList.getPriceListVersion((Timestamp)get_ValueOld(COLUMNNAME_DateInvoiced));
					MPriceListVersion plNew = pList.getPriceListVersion((Timestamp)get_Value(COLUMNNAME_DateInvoiced));
					if (plNew == null || !plNew.equals(plOld)) {
						log.saveError("Error", Msg.getMsg(getCtx(), "CannotChangeDateInvoiced"));
						return false;
					}
				}
			}
		}

		return true;
	}	//	beforeSave

	/**
	 * 	Before Delete
	 *	@return true if it can be deleted
	 */
	protected boolean beforeDelete ()
	{
		if (getC_Order_ID() != 0)
		{
			//Load invoice lines for afterDelete()
			getLines();	
		}
		return true;
	}	//	beforeDelete
	
	/**
	 * After Delete
	 * @param success success
	 * @return deleted
	 */
	protected boolean afterDelete(boolean success) {
		
		if (!success)
			return success;
					
		return true;
	} //afterDelete

	/**
	 * 	String Representation
	 *	@return info
	 */
	public String toString ()
	{
		StringBuilder sb = new StringBuilder ("MRecognition[")
			.append(get_ID()).append("-").append(getDocumentNo())
			.append(",GrandTotal=").append(getGrandTotal());
		if (m_lines != null)
			sb.append(" (#").append(m_lines.length).append(")");
		sb.append ("]");
		return sb.toString ();
	}	//	toString

	/**
	 * 	Get Document Info
	 *	@return document info (untranslated)
	 */
	public String getDocumentInfo()
	{
		MDocType dt = MDocType.get(getCtx(), getC_DocType_ID());
		StringBuilder msgreturn = new StringBuilder().append(dt.getNameTrl()).append(" ").append(getDocumentNo());
		return msgreturn.toString();
	}	//	getDocumentInfo


	/**
	 * 	After Save
	 *	@param newRecord new
	 *	@param success success
	 *	@return success
	 */
	protected boolean afterSave (boolean newRecord, boolean success)
	{
		if (!success || newRecord)
			return success;

		if (is_ValueChanged("AD_Org_ID"))
		{
			StringBuilder sql = new StringBuilder("UPDATE JP_RecognitionLine ol")
				.append(" SET AD_Org_ID =")
					.append("(SELECT AD_Org_ID")
					.append(" FROM JP_Recognition o WHERE ol.JP_Recognition_ID=o.JP_Recognition_ID) ")
				.append("WHERE JP_Recognition_ID=").append(getJP_Recognition_ID());
			int no = DB.executeUpdate(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("Lines -> #" + no);
		}
		return true;
	}	//	afterSave


	/**
	 * 	Set Price List (and Currency) when valid
	 * 	@param M_PriceList_ID price list
	 */
	@Override
	public void setM_PriceList_ID (int M_PriceList_ID)
	{
		MPriceList pl = MPriceList.get(getCtx(), M_PriceList_ID, null);
		if (pl != null) {
			setC_Currency_ID(pl.getC_Currency_ID());
			super.setM_PriceList_ID(M_PriceList_ID);
		}
	}	//	setM_PriceList_ID



	/**
	 * 	Get Document Status
	 *	@return Document Status Clear Text
	 */
	public String getDocStatusName()
	{
		return MRefList.getListName(getCtx(), 131, getDocStatus());
	}	//	getDocStatusName


	/**************************************************************************
	 * 	Create PDF
	 *	@return File or null
	 */
	public File createPDF ()
	{
		try
		{
			StringBuilder msgfile = new StringBuilder().append(get_TableName()).append(get_ID()).append("_");
			File temp = File.createTempFile(msgfile.toString(), ".pdf");
			return createPDF (temp);
		}
		catch (Exception e)
		{
			log.severe("Could not create PDF - " + e.getMessage());
		}
		return null;
	}	//	getPDF

	/**
	 * 	Create PDF file
	 *	@param file output file
	 *	@return file if success
	 */
	public File createPDF (File file)
	{
		ReportEngine re = ReportEngine.get (getCtx(), ReportEngine.INVOICE, getJP_Recognition_ID(), get_TrxName());
		if (re == null)
			return null;
		MPrintFormat format = re.getPrintFormat();
		// We have a Jasper Print Format
		// ==============================
		if(format.getJasperProcess_ID() > 0)	
		{
			ProcessInfo pi = new ProcessInfo ("", format.getJasperProcess_ID());
			pi.setRecord_ID ( getJP_Recognition_ID() );
			pi.setIsBatch(true);
			
			ServerProcessCtl.process(pi, null);
			
			return pi.getPDFReport();
		}
		// Standard Print Format (Non-Jasper)
		// ==================================
		return re.getPDF(file);
	}	//	createPDF

	/**
	 * 	Get PDF File Name
	 *	@param documentDir directory
	 *	@return file name
	 */
	public String getPDFFileName (String documentDir)
	{
		return getPDFFileName (documentDir, getJP_Recognition_ID());
	}	//	getPDFFileName

	/**
	 *	Get ISO Code of Currency
	 *	@return Currency ISO
	 */
	public String getCurrencyISO()
	{
		return MCurrency.getISO_Code (getCtx(), getC_Currency_ID());
	}	//	getCurrencyISO

	/**
	 * 	Get Currency Precision
	 *	@return precision
	 */
	public int getPrecision()
	{
		return MCurrency.getStdPrecision(getCtx(), getC_Currency_ID());
	}	//	getPrecision


	/**************************************************************************
	 * 	Process document
	 *	@param processAction document action
	 *	@return true if performed
	 */
	public boolean processIt (String processAction)
	{
		m_processMsg = null;
		DocumentEngine engine = new DocumentEngine (this, getDocStatus());
		return engine.processIt (processAction, getDocAction());
	}	//	process

	/**	Process Message 			*/
	private String		m_processMsg = null;
	/**	Just Prepared Flag			*/
	private boolean		m_justPrepared = false;

	/**
	 * 	Unlock Document.
	 * 	@return true if success
	 */
	public boolean unlockIt()
	{
		if (log.isLoggable(Level.INFO)) log.info("unlockIt - " + toString());
		setProcessing(false);
		return true;
	}	//	unlockIt

	/**
	 * 	Invalidate Document
	 * 	@return true if success
	 */
	public boolean invalidateIt()
	{
		if (log.isLoggable(Level.INFO)) log.info("invalidateIt - " + toString());
		setDocAction(DOCACTION_Prepare);
		return true;
	}	//	invalidateIt

	/**
	 *	Prepare Document
	 * 	@return new status (In Progress or Invalid)
	 */
	public String prepareIt()
	{
		if (log.isLoggable(Level.INFO)) log.info(toString());
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_PREPARE);
		if (m_processMsg != null)
			return DocAction.STATUS_Invalid;

		MPeriod.testPeriodOpen(getCtx(), getDateAcct(), getC_DocTypeTarget_ID(), getAD_Org_ID());

		//	Lines
		MRecognitionLine[] lines = getLines(true);
		if (lines.length == 0)
		{
			m_processMsg = "@NoLines@";
			return DocAction.STATUS_Invalid;
		}

		
		//TODO この下のチェックメソッドはいらないよね？
//		if (!calculateTaxTotal())	//	setTotals
//		{
//			m_processMsg = "Error calculating Tax";
//			return DocAction.STATUS_Invalid;
//		}

		m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_PREPARE);
		if (m_processMsg != null)
			return DocAction.STATUS_Invalid;

		//	Add up Amounts
		m_justPrepared = true;
		if (!DOCACTION_Complete.equals(getDocAction()))
			setDocAction(DOCACTION_Complete);
		return DocAction.STATUS_InProgress;
	}	//	prepareIt


	/**
	 * 	Approve Document
	 * 	@return true if success
	 */
	public boolean  approveIt()
	{
		if (log.isLoggable(Level.INFO)) log.info(toString());
		setIsApproved(true);
		return true;
	}	//	approveIt

	/**
	 * 	Reject Approval
	 * 	@return true if success
	 */
	public boolean rejectIt()
	{
		if (log.isLoggable(Level.INFO)) log.info(toString());
		setIsApproved(false);
		return true;
	}	//	rejectIt

	/**
	 * 	Complete Document
	 * 	@return new status (Complete, In Progress, Invalid, Waiting ..)
	 */
	public String completeIt()
	{
		//	Re-Check
		if (!m_justPrepared)
		{
			String status = prepareIt();
			m_justPrepared = false;
			if (!DocAction.STATUS_InProgress.equals(status))
				return status;
		}

		// Set the definite document number after completed (if needed)
		setDefiniteDocumentNo();

		m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_COMPLETE);
		if (m_processMsg != null)
			return DocAction.STATUS_Invalid;

		//	Implicit Approval
		if (!isApproved())
			approveIt();
		if (log.isLoggable(Level.INFO)) log.info(toString());
		StringBuilder info = new StringBuilder();
		
		//	Update Order Lines or RMA Lines
		MRecognitionLine[] lines = getLines(false);
		boolean isDiffQty = false;
		for (int i = 0; i < lines.length; i++)
		{
			MRecognitionLine line = lines[i];
			if(isDiffQty == false && line.getJP_QtyRecognized().compareTo(line.getJP_TargetQtyRecognized()) != 0)
				isDiffQty = true;
			
			//Update JP_QtyRecognized Order Line
			MOrderLine ol = null;
			if (line.getC_OrderLine_ID() != 0 && line.getM_RMALine_ID() == 0)
			{
				ol = new MOrderLine (getCtx(), line.getC_OrderLine_ID(), get_TrxName());
				BigDecimal JP_QtyRecognized = (BigDecimal)ol.get_Value("JP_QtyRecognized");
				if(JP_QtyRecognized == null)
					JP_QtyRecognized = Env.ZERO;
				
				if (line.getJP_QtyRecognized() != null)
				{
					ol.set_ValueNoCheck("JP_QtyRecognized", JP_QtyRecognized.add(line.getJP_QtyRecognized() ));
				}
				
				if (!ol.save(get_TrxName()))
				{
					m_processMsg = "Could not update Order Line";
					return DocAction.STATUS_Invalid;
				}
			}

			//Update JP_QtyRecognized RMA Line
			if (line.getM_RMALine_ID() != 0)
			{
				MRMALine rmaLine = new MRMALine (getCtx(),line.getM_RMALine_ID(), get_TrxName());
				BigDecimal JP_QtyRecognized = (BigDecimal)rmaLine.get_Value("JP_QtyRecognized");
				if(JP_QtyRecognized == null)
					JP_QtyRecognized = Env.ZERO;
				
				if (line.getJP_QtyRecognized() != null)
				{
					rmaLine.set_ValueNoCheck("JP_QtyRecognized", JP_QtyRecognized.add(line.getJP_QtyRecognized() ));
				}
				
				if (!rmaLine.save(get_TrxName()))
				{
					m_processMsg = "Could not update RMA Line";
					return DocAction.STATUS_Invalid;
				}
			}
					
		}//	for i

		if(isDiffQty)
		{
			MContractContent content = MContractContent.get(getCtx(), getJP_ContractContent_ID());
			MContractAcct acct = MContractAcct.get(getCtx(),content.getJP_Contract_Acct_ID());
			if(acct != null && acct.isPostingContractAcctJP() && acct.isPostingRecognitionDocJP() && acct.isSplitWhenDifferenceJP())
			{
				splitRecognition(lines);
			}
			
		}
		
		//	User Validation
		String valid = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_COMPLETE);
		if (valid != null)
		{
			m_processMsg = valid;
			return DocAction.STATUS_Invalid;
		}

		m_processMsg = info.toString().trim();
		setProcessed(true);
		setDocAction(DOCACTION_Close);
		return DocAction.STATUS_Completed;
	}	//	completeIt

	/* Save array of documents to process AFTER completing this one */
	ArrayList<PO> docsPostProcess = new ArrayList<PO>();


	public ArrayList<PO> getDocsPostProcess() {
		return docsPostProcess;
	}

	/**
	 * 	Set the definite document number after completed
	 */
	private void setDefiniteDocumentNo() {
		if (isReversal() && ! MSysConfig.getBooleanValue(MSysConfig.Invoice_ReverseUseNewNumber, true, getAD_Client_ID())) // IDEMPIERE-1771
			return;
		MDocType dt = MDocType.get(getCtx(), getC_DocType_ID());
		if (dt.isOverwriteDateOnComplete()) {
			setDateInvoiced(new Timestamp (System.currentTimeMillis()));
			if (getDateAcct().before(getDateInvoiced())) {
				setDateAcct(getDateInvoiced());
				MPeriod.testPeriodOpen(getCtx(), getDateAcct(), getC_DocType_ID(), getAD_Org_ID());
			}
		}
		if (dt.isOverwriteSeqOnComplete()) {
			String value = DB.getDocumentNo(getC_DocType_ID(), get_TrxName(), true, this);
			if (value != null)
				setDocumentNo(value);
		}
	}


	/**
	 * 	Void Document.
	 * 	@return true if success
	 */
	public boolean voidIt()
	{
		if (log.isLoggable(Level.INFO)) log.info(toString());
		
		if (DOCSTATUS_Closed.equals(getDocStatus())
			|| DOCSTATUS_Reversed.equals(getDocStatus())
			|| DOCSTATUS_Voided.equals(getDocStatus()))
		{
			m_processMsg = "Document Closed: " + getDocStatus();
			setDocAction(DOCACTION_None);
			return false;
		}

		//	Not Processed
		if (DOCSTATUS_Drafted.equals(getDocStatus())
			|| DOCSTATUS_Invalid.equals(getDocStatus())
			|| DOCSTATUS_InProgress.equals(getDocStatus())
			|| DOCSTATUS_Approved.equals(getDocStatus())
			|| DOCSTATUS_NotApproved.equals(getDocStatus()) )
		{
			// Before Void
			m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_VOID);
			if (m_processMsg != null)
				return false;
			
			//	Set lines to 0
			MRecognitionLine[] lines = getLines(false);
			for (int i = 0; i < lines.length; i++)
			{
				MRecognitionLine line = lines[i];
				BigDecimal old = line.getQtyInvoiced();
				if (old.compareTo(Env.ZERO) != 0)
				{
					line.setQty(Env.ZERO);
					line.setTaxAmt(Env.ZERO);
					line.setLineNetAmt(Env.ZERO);
					line.setLineTotalAmt(Env.ZERO);
					StringBuilder msgadd = new StringBuilder(Msg.getMsg(getCtx(), "Voided")).append(" (").append(old).append(")");
					line.addDescription(msgadd.toString());
					line.saveEx(get_TrxName());
				}
			}
			addDescription(Msg.getMsg(getCtx(), "Voided"));
		}
		else
		{
			boolean accrual = false;
			try 
			{
				MPeriod.testPeriodOpen(getCtx(), getDateAcct(), getC_DocType_ID(), getAD_Org_ID());
			}
			catch (PeriodClosedException e) 
			{
				accrual = true;
			}
			
			if (accrual)
				return reverseAccrualIt();
			else
				return reverseCorrectIt();
		}

		// After Void
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_VOID);
		if (m_processMsg != null)
			return false;

		setProcessed(true);
		setDocAction(DOCACTION_None);
		return true;
	}	//	voidIt

	/**
	 * 	Close Document.
	 * 	@return true if success
	 */
	public boolean closeIt()
	{
		if (log.isLoggable(Level.INFO)) log.info(toString());
		// Before Close
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_CLOSE);
		if (m_processMsg != null)
			return false;

		if(!getM_InOut().getDocStatus().equals(DocAction.STATUS_Closed))
		{
			//You have to close Ship/Receipt Doc, before Recognition doc close.
			m_processMsg = Msg.getMsg(getCtx(), "JP_RecogDocStatusCloseError");
			return false;
		}
		
		
		setProcessed(true);
		setDocAction(DOCACTION_None);

		// After Close
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_CLOSE);
		if (m_processMsg != null)
			return false;
		return true;
	}	//	closeIt

	/**
	 * 	Reverse Correction - same date
	 * 	@return true if success
	 */
	public boolean reverseCorrectIt()
	{
		if (log.isLoggable(Level.INFO)) log.info(toString());
		// Before reverseCorrect
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_REVERSECORRECT);
		if (m_processMsg != null)
			return false;

		MRecognition reversal = reverse(false);
		if (reversal == null)
			return false;

		// After reverseCorrect
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_REVERSECORRECT);
		if (m_processMsg != null)
			return false;

		m_processMsg = reversal.getDocumentNo();
		
		return true;
	}	//	reverseCorrectIt

	private MRecognition reverse(boolean accrual) {
		Timestamp reversalDate = accrual ? Env.getContextAsDate(getCtx(), "#Date") : getDateAcct();
		if (reversalDate == null) {
			reversalDate = new Timestamp(System.currentTimeMillis());
		}
		Timestamp reversalDateRecognitioned = accrual ? reversalDate : getDateAcct();
		
		MPeriod.testPeriodOpen(getCtx(), reversalDate, getC_DocType_ID(), getAD_Org_ID());


		//
		load(get_TrxName());	//	reload allocation reversal info

		//	Deep Copy
		MRecognition reversal = null;
		if (MSysConfig.getBooleanValue(MSysConfig.Invoice_ReverseUseNewNumber, true, getAD_Client_ID()))
			reversal = copyFrom (this, reversalDateRecognitioned, reversalDate, getC_DocType_ID(), isSOTrx(), false, get_TrxName(), true);
		else 
			reversal = copyFrom (this, reversalDateRecognitioned, reversalDate, getC_DocType_ID(), isSOTrx(), false, get_TrxName(), true, getDocumentNo()+"^");
		if (reversal == null)
		{
			m_processMsg = "Could not create Invoice Reversal";
			return null;
		}
		reversal.setReversal(true);

		//	Reverse Line Qty
		MRecognitionLine[] rLines = reversal.getLines(true);
		for (int i = 0; i < rLines.length; i++)
		{
			MRecognitionLine rLine = rLines[i];
			rLine.setQtyEntered(rLine.getQtyEntered().negate());
			rLine.setQtyInvoiced(rLine.getQtyInvoiced().negate());
			rLine.setJP_QtyRecognized(rLine.getJP_QtyRecognized().negate());
			rLine.setJP_TargetQtyRecognized(rLine.getJP_TargetQtyRecognized().negate());
			rLine.setLineNetAmt(rLine.getLineNetAmt().negate());
			if (rLine.getTaxAmt() != null && rLine.getTaxAmt().compareTo(Env.ZERO) != 0)
				rLine.setTaxAmt(rLine.getTaxAmt().negate());
			if (rLine.getLineTotalAmt() != null && rLine.getLineTotalAmt().compareTo(Env.ZERO) != 0)
				rLine.setLineTotalAmt(rLine.getLineTotalAmt().negate());
			if (!rLine.save(get_TrxName()))
			{
				m_processMsg = "Could not correct Invoice Reversal Line";
				return null;
			}
		}
		reversal.setC_Order_ID(getC_Order_ID());
		StringBuilder msgadd = new StringBuilder("{->").append(getDocumentNo()).append(")");
		reversal.addDescription(msgadd.toString());
		//FR1948157
		reversal.setReversal_ID(getJP_Recognition_ID());
		reversal.saveEx(get_TrxName());
		//
		reversal.docsPostProcess = this.docsPostProcess;
		this.docsPostProcess = new ArrayList<PO>();
		//
		if (!reversal.processIt(DocAction.ACTION_Complete))
		{
			m_processMsg = "Reversal ERROR: " + reversal.getProcessMsg();
			return null;
		}
		//
		
		reversal.closeIt();
		reversal.setProcessing (false);
		reversal.setDocStatus(DOCSTATUS_Reversed);
		reversal.setDocAction(DOCACTION_None);
		reversal.saveEx(get_TrxName());
		//
		msgadd = new StringBuilder("(").append(reversal.getDocumentNo()).append("<-)");
		addDescription(msgadd.toString());

		setProcessed(true);
		setReversal_ID(reversal.getJP_Recognition_ID());
		setDocStatus(DOCSTATUS_Reversed);	//	may come from void
		setDocAction(DOCACTION_None);

		
		return reversal;
	}



	/**
	 * 	Reverse Accrual - none
	 * 	@return false
	 */
	public boolean reverseAccrualIt()
	{
		if (log.isLoggable(Level.INFO)) log.info(toString());
		// Before reverseAccrual
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_REVERSEACCRUAL);
		if (m_processMsg != null)
			return false;

		MRecognition reversal = reverse(true);
		if (reversal == null)
			return false;
		
		// After reverseAccrual
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_REVERSEACCRUAL);
		if (m_processMsg != null)
			return false;

		m_processMsg = reversal.getDocumentNo();
		
		return true;
	}	//	reverseAccrualIt

	/**
	 * 	Re-activate
	 * 	@return false
	 */
	public boolean reActivateIt()
	{
		if (log.isLoggable(Level.INFO)) log.info(toString());
		// Before reActivate
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_REACTIVATE);
		if (m_processMsg != null)
			return false;

		// After reActivate
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_REACTIVATE);
		if (m_processMsg != null)
			return false;


		return false;
	}	//	reActivateIt


	/*************************************************************************
	 * 	Get Summary
	 *	@return Summary of Document
	 */
	public String getSummary()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(getDocumentNo());
		//	: Grand Total = 123.00 (#1)
		sb.append(": ").
			append(Msg.translate(getCtx(),"GrandTotal")).append("=").append(getGrandTotal())
			.append(" (#").append(getLines(false).length).append(")");
		//	 - Description
		if (getDescription() != null && getDescription().length() > 0)
			sb.append(" - ").append(getDescription());
		return sb.toString();
	}	//	getSummary

	/**
	 * 	Get Process Message
	 *	@return clear text error message
	 */
	public String getProcessMsg()
	{
		return m_processMsg;
	}	//	getProcessMsg

	/**
	 * 	Get Document Owner (Responsible)
	 *	@return AD_User_ID
	 */
	public int getDoc_User_ID()
	{
		return getSalesRep_ID();
	}	//	getDoc_User_ID

	/**
	 * 	Get Document Approval Amount
	 *	@return amount
	 */
	public BigDecimal getApprovalAmt()
	{
		return getGrandTotal();
	}	//	getApprovalAmt

	/**
	 *
	 * @param rma
	 */
	public void setRMA(MRMA rma)
	{
		setM_RMA_ID(rma.getM_RMA_ID());
        setAD_Org_ID(rma.getAD_Org_ID());
        setDescription(rma.getDescription());
        setC_BPartner_ID(rma.getC_BPartner_ID());
        setSalesRep_ID(rma.getSalesRep_ID());

        setGrandTotal(rma.getAmt());
        setIsSOTrx(rma.isSOTrx());
        setTotalLines(rma.getAmt());

//        MRecognition originalInvoice = rma.getOriginalInvoice(); TODO とりあえずコメントアウトでエラー回避
//
//        if (originalInvoice == null)
//        {
//            throw new IllegalStateException("Not invoiced - RMA: " + rma.getDocumentNo());
//        }
//
//        setC_BPartner_Location_ID(originalInvoice.getC_BPartner_Location_ID());
//        setAD_User_ID(originalInvoice.getAD_User_ID());
//        setC_Currency_ID(originalInvoice.getC_Currency_ID());
//        setIsTaxIncluded(originalInvoice.isTaxIncluded());
//        setM_PriceList_ID(originalInvoice.getM_PriceList_ID());
//        setC_Project_ID(originalInvoice.getC_Project_ID());
//        setC_Activity_ID(originalInvoice.getC_Activity_ID());
//        setC_Campaign_ID(originalInvoice.getC_Campaign_ID());
//        setUser1_ID(originalInvoice.getUser1_ID());
//        setUser2_ID(originalInvoice.getUser2_ID());
	}

	/**
	 * 	Document Status is Complete or Closed
	 *	@return true if CO, CL or RE
	 */
	public boolean isComplete()
	{
		String ds = getDocStatus();
		return DOCSTATUS_Completed.equals(ds)
			|| DOCSTATUS_Closed.equals(ds)
			|| DOCSTATUS_Reversed.equals(ds);
	}	//	isComplete

	/**
	 * Get original order
	 * @return order
	 */
	public MOrder getOriginalOrder()
	{
		if (getM_RMA_ID() > 0)
		{
			MRMA rma = new MRMA(getCtx(), getM_RMA_ID(), get_TrxName());
			MOrder originalOrder = rma.getOriginalOrder();
			if (originalOrder != null)
				return originalOrder;
			
			//TODO とりあえずコメントアウトでエラー回避
//			MRecognition originalInvoice = rma.getOriginalInvoice();
//			if (originalInvoice.getC_Order_ID() > 0)
//			{
//				originalOrder = new MOrder(getCtx(), originalInvoice.getC_Order_ID(), get_TrxName());
//				if (originalOrder != null)
//					return originalOrder;
//			}
		}
		else if (getC_Order_ID() > 0)
			return new MOrder(getCtx(), getC_Order_ID(), get_TrxName());
		return null;
	}

	/**
	 * Set process message
	 * @param processMsg
	 */
	public void setProcessMessage(String processMsg)
	{
		m_processMsg = processMsg;
	}
	
	/**
	 * Get tax providers
	 * @return array of tax provider
	 */
	public MTaxProvider[] getTaxProviders()
	{
		Hashtable<Integer, MTaxProvider> providers = new Hashtable<Integer, MTaxProvider>();
		MRecognitionLine[] lines = getLines();
		for (MRecognitionLine line : lines)
		{
            MTax tax = new MTax(line.getCtx(), line.getC_Tax_ID(), line.get_TrxName());
            MTaxProvider provider = providers.get(tax.getC_TaxProvider_ID());
            if (provider == null)
            	providers.put(tax.getC_TaxProvider_ID(), new MTaxProvider(tax.getCtx(), tax.getC_TaxProvider_ID(), tax.get_TrxName()));
		}
		
		MTaxProvider[] retValue = new MTaxProvider[providers.size()];
		providers.values().toArray(retValue);
		return retValue;
	}

	/** Returns C_DocType_ID (or C_DocTypeTarget_ID if C_DocType_ID is not set) */
	public int getDocTypeID()
	{
		return getC_DocType_ID() > 0 ? getC_DocType_ID() : getC_DocTypeTarget_ID();
	}
	
	static public MRecognition[] getRecognitionsByInOut(Properties ctx, int M_InOut_ID, boolean isReversal, int original_InOut_ID, String trxName)
	{
		ArrayList<MRecognition> list = new ArrayList<MRecognition>();
		final String sql = "SELECT * FROM JP_Recognition WHERE M_InOut_ID=? AND DocStatus NOT IN ('VO','RE','CL')";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, trxName);
			if(isReversal)
			{
				pstmt.setInt(1, original_InOut_ID);
			}else{
				pstmt.setInt(1, M_InOut_ID);
			}
			rs = pstmt.executeQuery();
			while(rs.next())
				list.add(new MRecognition(ctx, rs, trxName));
		}
		catch (Exception e)
		{
//			Log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}
		
		
		MRecognition[] recogs = new MRecognition[list.size()];
		list.toArray(recogs);
		return recogs;
	}

	@Override
	public int customizeValidActions(String docStatus, Object processing, String orderType, String isSOTrx,
			int AD_Table_ID, String[] docAction, String[] options, int index) 
	{
		if(docStatus.equals(DocAction.STATUS_Completed))
		{
			index = 0; //initialize the index
			options[index++] = DocumentEngine.ACTION_Close;
			options[index++] = DocumentEngine.ACTION_Reverse_Accrual;
			options[index++] = DocumentEngine.ACTION_Reverse_Correct;
			return index;
		}

		if(docStatus.equals(DocAction.STATUS_Drafted))
		{
			index = 0; //initialize the index
			options[index++] = DocumentEngine.ACTION_Prepare;
			options[index++] = DocumentEngine.ACTION_Void;
			options[index++] = DocumentEngine.ACTION_Complete;
			return index;
		}

		return index;

	}

	private void splitRecognition (MRecognitionLine[] lines)
	{
		MRecognition split = new MRecognition (getCtx(), 0, get_TrxName());
		PO.copyValues(this,split );
		split.setAD_Org_ID(getAD_Org_ID());
		split.setDocumentNo(null);
		split.setJP_Recognition_SplitFrom_ID(getJP_Recognition_ID());
		split.setReversal_ID(0);
		split.setJP_Contract_ID(getJP_Contract_ID());
		split.setJP_ContractContent_ID(getJP_ContractContent_ID());
		split.setJP_ContractProcPeriod_ID(getJP_ContractProcPeriod_ID());
		split.setDocStatus(DocAction.STATUS_Drafted);
		split.setDocAction(DocAction.ACTION_Complete);
		split.setTotalLines(Env.ZERO);
		split.setGrandTotal(Env.ZERO);
		split.saveEx();
		
		setJP_Recognition_SplitFrom_ID(split.getJP_Recognition_ID());
		
		for (int i = 0; i < lines.length; i++)
		{
			MRecognitionLine line = lines[i];
			BigDecimal differenceQty = line.getJP_TargetQtyRecognized().subtract(line.getJP_QtyRecognized());
			if (differenceQty.compareTo(Env.ZERO) == 0)
				continue;

			MRecognitionLine splitLine = new MRecognitionLine (getCtx(), 0, get_TrxName());
			PO.copyValues(line,splitLine);
			splitLine.setJP_Recognition_ID(split.getJP_Recognition_ID());
			splitLine.setAD_Org_ID(line.getAD_Org_ID());
			splitLine.setC_OrderLine_ID(line.getC_OrderLine_ID());
			splitLine.setM_RMALine_ID(line.getM_RMALine_ID());
			splitLine.setM_InOutLine_ID(line.getM_InOutLine_ID());
			splitLine.setLine(line.getLine());
			
			splitLine.setJP_TargetQtyRecognized(differenceQty);
			splitLine.setQty(differenceQty);
			
			splitLine.setJP_RecogLine_SplitFrom_ID(line.getJP_RecognitionLine_ID());
			splitLine.saveEx();
		}
		
	}	//	splitRecognition

	
}	//	MRecognition
