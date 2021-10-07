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
import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.model.MBPartner;
import org.compiere.model.MDocType;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MPayment;
import org.compiere.model.MPeriod;
import org.compiere.model.MRefList;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.process.DocAction;
import org.compiere.process.DocOptions;
import org.compiere.process.DocumentEngine;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;

/**
 *	MBill
 *
 *	JPIERE-0106:JPBP:Bill
 *
 *  @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 */
public class MBill extends X_JP_Bill implements DocAction,DocOptions
{
	/**
	 *
	 */
	private static final long serialVersionUID = -7588955558162632796L;


	public MBill(Properties ctx, int JP_Bill_ID, String trxName) {
		super(ctx, JP_Bill_ID, trxName);
	}

	public MBill(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}


	/**
	 * 	Get Document Info
	 *	@return document info (untranslated)
	 */
	public String getDocumentInfo()
	{
		MDocType dt = MDocType.get(getCtx(), getC_DocType_ID());
		return dt.getNameTrl() + " " + getDocumentNo();
	}	//	getDocumentInfo

	/**
	 * 	Create PDF
	 *	@return File or null
	 */
	public File createPDF ()
	{
		try
		{
			File temp = File.createTempFile(get_TableName()+get_ID()+"_", ".pdf");
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
	//	ReportEngine re = ReportEngine.get (getCtx(), ReportEngine.INVOICE, getC_Invoice_ID());
	//	if (re == null)
			return null;
	//	return re.getPDF(file);
	}	//	createPDF


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
	}	//	processIt

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
	//	setProcessing(false);
		return true;
	}	//	unlockIt

	/**
	 * 	Invalidate Document
	 * 	@return true if success
	 */
	public boolean invalidateIt()
	{
		if (log.isLoggable(Level.INFO)) log.info("invalidateIt - " + toString());
	//	setDocAction(DOCACTION_Prepare);
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

		MDocType dt = MDocType.get(getCtx(), getC_DocType_ID());

		//	Std Period open?
		if(!MPeriod.isOpen(getCtx(), getJPDateBilled(), dt.getDocBaseType(), getAD_Org_ID()))
		{
			m_processMsg = "@PeriodClosed@";
			return DocAction.STATUS_Invalid;
		}
		MBillLine[] lines = getLines(false);
		if (lines.length == 0)
		{
			m_processMsg = "@NoLines@";
			return DocAction.STATUS_Invalid;
		}

		//	Add up Amounts
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_PREPARE);
		if (m_processMsg != null)
			return DocAction.STATUS_Invalid;
		m_justPrepared = true;
	//	if (!DOCACTION_Complete.equals(getDocAction()))
	//		setDocAction(DOCACTION_Complete);
		return DocAction.STATUS_InProgress;
	}	//	prepareIt

	/**
	 * 	Approve Document
	 * 	@return true if success
	 */
	public boolean  approveIt()
	{
		if (log.isLoggable(Level.INFO)) log.info("approveIt - " + toString());
		setIsApproved(true);
		return true;
	}	//	approveIt

	/**
	 * 	Reject Approval
	 * 	@return true if success
	 */
	public boolean rejectIt()
	{
		if (log.isLoggable(Level.INFO)) log.info("rejectIt - " + toString());
	//	setIsApproved(false);
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


		m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_COMPLETE);
		if (m_processMsg != null)
			return DocAction.STATUS_Invalid;

		//	Implicit Approval
		if (!isApproved())
			approveIt();


		boolean isCreateInvoice = false;
		MBillTax[] taxes = getTaxes(true);
		for(MBillTax tax : taxes)
		{
			if(tax.getJP_TaxAdjust_TaxAmt().compareTo(Env.ZERO) != 0)
			{
				isCreateInvoice = true;
				break;
			}
		}

		if(isCreateInvoice)
		{
			MBillSchema billSchema = MBillSchema.getBillSchemaBP(getC_BPartner_ID());
			if(billSchema == null)
			{
				m_processMsg  = Msg.getMsg(getCtx(), "NotFound") + Msg.getElement(getCtx(), MBillSchema.COLUMNNAME_JP_BillSchema_ID, isSOTrx());
				return DocAction.STATUS_Invalid;
			}

			if(billSchema.isTaxRecalculateJP())
			{
				MInvoice invoice = new MInvoice(getCtx(), 0, get_TrxName());
				PO.copyValues(this, invoice);
				invoice.setC_Invoice_ID(0);
				invoice.setAD_Org_ID(getAD_Org_ID());
				invoice.setSalesRep_ID(getSalesRep_ID());
				invoice.setC_BPartner_ID(getC_BPartner_ID());
				invoice.setC_BPartner_Location_ID(getC_BPartner_Location_ID());
				invoice.setAD_User_ID(getAD_User_ID());
				invoice.setDateInvoiced(getJPDateBilled());
				invoice.setDateAcct(getDateAcct());
				invoice.setC_DocTypeTarget_ID(billSchema.getJP_TaxAdjust_DocType_ID());
				invoice.setC_DocType_ID(billSchema.getJP_TaxAdjust_DocType_ID());
				invoice.setM_PriceList_ID(billSchema.getJP_TaxAdjust_PriceList_ID());
				invoice.setDescription(billSchema.getJP_TaxAdjust_Description());
				invoice.setDocumentNo(null);
				invoice.setPaymentRule(getPaymentRule());
				invoice.setC_PaymentTerm_ID(getC_PaymentTerm_ID());
				invoice.set_ValueNoCheck(COLUMNNAME_JP_Bill_ID, getJP_Bill_ID());
				if(!invoice.save(get_TrxName()))
				{
					m_processMsg = Msg.getMsg(getCtx(), "SaveError") + Msg.getElement(getCtx(), COLUMNNAME_JP_TaxAdjust_Invoice_ID, isSOTrx());
					return DocAction.STATUS_Invalid;
				}

				setJP_TaxAdjust_Invoice_ID(invoice.getC_Invoice_ID());

				MDocType docType = MDocType.get(billSchema.getJP_TaxAdjust_DocType_ID());
				int line = 10;
				for(MBillTax tax : taxes)
				{
					if(tax.getJP_TaxAdjust_TaxAmt().compareTo(Env.ZERO) == 0)
						continue;

					MInvoiceLine iLine = new MInvoiceLine(getCtx(), 0, get_TrxName());
					iLine.setC_Invoice_ID(invoice.getC_Invoice_ID());
					iLine.setInvoice(invoice);
					iLine.setLine(line);
					iLine.setC_Charge_ID(billSchema.getJP_TaxAdjust_Charge_ID());
					iLine.setQty(Env.ONE);
					iLine.setC_UOM_ID(100);
					if(docType.getDocBaseType().equals("ARC") || docType.getDocBaseType().equals("APC"))
					{
						iLine.setPrice(tax.getJP_TaxAdjust_TaxAmt().negate());
					}else {
						iLine.setPrice(tax.getJP_TaxAdjust_TaxAmt());
					}
					iLine.setC_Tax_ID(billSchema.getJP_TaxAdjust_Tax_ID());

					if(!iLine.save(get_TrxName()))
					{
						m_processMsg = Msg.getMsg(getCtx(), "SaveError") + Msg.getElement(getCtx(), COLUMNNAME_JP_TaxAdjust_Invoice_ID, isSOTrx())
													+ " - "+ Msg.getElement(getCtx(), MInvoiceLine.COLUMNNAME_C_InvoiceLine_ID, isSOTrx());
						return DocAction.STATUS_Invalid;
					}

					line = line + 10;

					tax.setJP_TaxAdjust_InvoiceLine_ID(iLine.getC_InvoiceLine_ID());
					if(!tax.save(get_TrxName()))
					{
						m_processMsg = Msg.getMsg(getCtx(), "SaveError") + Msg.getElement(getCtx(), MBillTax.COLUMNNAME_JP_TaxAdjust_InvoiceLine_ID, isSOTrx());
						return DocAction.STATUS_Invalid;
					}

				}//for

				if(invoice.processIt(ACTION_Complete))
				{
					MBillLine billLine = new MBillLine(getCtx(), 0, get_TrxName());
					billLine.setJP_Bill_ID(getJP_Bill_ID());
					billLine.setAD_Org_ID(getAD_Org_ID());
					billLine.setDescription(billSchema.getJP_TaxAdjust_Description());

					String sql = "SELECT NVL(MAX(Line),0)+10 AS DefaultValue FROM JP_BillLine WHERE JP_Bill_ID=?";
					line = DB.getSQLValue(get_TrxName(), sql, getJP_Bill_ID());
					billLine.setLine(line);
					billLine.setC_Invoice_ID(invoice.getC_Invoice_ID());
					billLine.setIsTaxAdjustLineJP(true);
					if(!billLine.save(get_TrxName()))
					{
						m_processMsg = Msg.getMsg(getCtx(), "SaveError") + Msg.getElement(getCtx(), MBillLine.COLUMNNAME_IsTaxAdjustLineJP, isSOTrx());
						return DocAction.STATUS_Invalid;
					}

				}else {

					m_processMsg = Msg.getMsg(getCtx(), "ProcessRunError") + " - "+ Msg.getElement(getCtx(), MBill.COLUMNNAME_JP_TaxAdjust_Invoice_ID, isSOTrx())
					 														+ " - "+ MRefList.getListName(getCtx(), 135, ACTION_Complete);
					return DocAction.STATUS_Invalid;

				}

			}

		}

		//	User Validation
		String valid = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_COMPLETE);
		if (valid != null)
		{
			m_processMsg = valid;
			return DocAction.STATUS_Invalid;
		}

		setProcessed(true);
		setDocAction(DOCACTION_Close);
		return DocAction.STATUS_Completed;
	}	//	completeIt


	/**
	 * 	Void Document.
	 * 	Same as Close.
	 * 	@return true if success
	 */
	public boolean voidIt()
	{
		if (log.isLoggable(Level.INFO)) log.info("voidIt - " + toString());

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
			|| DOCSTATUS_NotApproved.equals(getDocStatus())
			|| DOCSTATUS_Completed.equals(getDocStatus()))
		{
			// Before Void
			m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_VOID);
			if (m_processMsg != null)
				return false;

			//	Set lines to 0
			MBillLine[] lines = getLines(false);
			MInvoice invoice = null;
			for (int i = 0; i < lines.length; i++)
			{
				MBillLine line = lines[i];

				invoice = new MInvoice(getCtx(),line.getC_Invoice_ID(), get_TrxName());

				Integer JP_Bill_ID = (Integer)invoice.get_Value("JP_Bill_ID");
				if(JP_Bill_ID != null && JP_Bill_ID.intValue()== getJP_Bill_ID())
				{
					invoice.set_ValueNoCheck("JP_Bill_ID", null);
					invoice.save(get_TrxName());
				}
			}//for
			addDescription(Msg.getMsg(getCtx(), "Voided"));
		}
		else
		{
			return false;
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
	 * 	Cancel not delivered Qunatities
	 * 	@return true if success
	 */
	public boolean closeIt()
	{
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_CLOSE);
		if (m_processMsg != null)
			return false;


		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_CLOSE);
		if (m_processMsg != null)
			return false;

		setDocAction(DOCACTION_None);

		return true;
	}	//	closeIt

	/**
	 * 	Reverse Correction
	 * 	@return true if success
	 */
	public boolean reverseCorrectIt()
	{
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_REVERSECORRECT);
		if (m_processMsg != null)
			return false;

		if(!reverse(ACTION_Reverse_Correct))
			return false;


		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_REVERSECORRECT);
		if (m_processMsg != null)
			return false;

		setProcessed(true);
		setDocAction(DOCACTION_None);

		return true;
	}	//	reverseCorrectionIt

	/**
	 * 	Reverse Accrual - none
	 * 	@return true if success
	 */
	public boolean reverseAccrualIt()
	{

		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_REVERSEACCRUAL);
		if (m_processMsg != null)
			return false;

		if(!reverse(ACTION_Reverse_Accrual))
			return false;

		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_REVERSEACCRUAL);
		if (m_processMsg != null)
			return false;

		setProcessed(true);
		setDocAction(DOCACTION_None);

		return true;
	}	//	reverseAccrualIt

	private boolean reverse(String docAction)
	{
		int C_Invoice_ID =	getJP_TaxAdjust_Invoice_ID();
		if(C_Invoice_ID > 0)
		{
			MInvoice invoice = new MInvoice(getCtx(), C_Invoice_ID, get_TrxName());
			if(invoice.processIt(docAction))
			{
				if(!invoice.save(get_TrxName()))
				{
					m_processMsg = Msg.getMsg(getCtx(), "SaveError") + Msg.getElement(getCtx(), MBill.COLUMNNAME_JP_TaxAdjust_Invoice_ID, isSOTrx());
					return false;
				}
			}else {

				m_processMsg = Msg.getMsg(getCtx(), "ProcessRunError") + " - "+ Msg.getElement(getCtx(), MBill.COLUMNNAME_JP_TaxAdjust_Invoice_ID, isSOTrx())
				+ " - "+ MRefList.getListName(getCtx(), 135,  docAction);

				return false;
			}
		}

		return voidIt();
	}


	/**
	 * 	Re-activate
	 * 	@return true if success
	 */
	public boolean reActivateIt()
	{
		if (log.isLoggable(Level.INFO)) log.info("reActivateIt - " + toString());
	//	setProcessed(false);
		if (reverseCorrectIt())
			return true;
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


	@Override
	public int customizeValidActions(String docStatus, Object processing, String orderType, String isSOTrx, int AD_Table_ID,
			String[] docAction, String[] options, int index) {

		if(docStatus.equals(DocAction.STATUS_Drafted))
		{
			return index;
		}else if(docStatus.equals(DocAction.STATUS_Completed)){

			if(isTaxRecalculateJP() && getJP_TaxAdjust_Invoice_ID() > 0)
			{
				options[index++] = DocAction.ACTION_Reverse_Accrual;
				options[index++] = DocAction.ACTION_Reverse_Correct;

			}else {

				options[index++] = DocAction.ACTION_Void;

			}

			return index;
		}

		return index;
	}


	@Override
	protected boolean beforeSave(boolean newRecord) {

		if(newRecord || is_ValueChanged("JP_LastBill_ID"))
		{
			if(getJP_LastBill_ID() == 0)
			{
				;//Noting to do;

			}else {

				if(getJPLastBillAmt().compareTo(Env.ZERO) == 0)
				{
					MBill lastbill = new MBill(getCtx(),getJP_LastBill_ID(),get_TrxName());
					if(lastbill.getDocStatus().equals(DocAction.STATUS_Completed) || lastbill.getDocStatus().equals(DocAction.STATUS_Closed))
					{
						setJPLastBillAmt(lastbill.getJPBillAmt());
					}else{
						log.saveError("Error", Msg.getMsg(getCtx(), "JP_InvalidDocStatus") + " - " + Msg.getElement(getCtx(), COLUMNNAME_JP_LastBill_ID));
						return false;
					}
				}

			}
		}

		if(newRecord || is_ValueChanged("C_Payment_ID"))
		{
			if(getC_Payment_ID() == 0)
			{
				;//Noting to do;

			}else {


				if(getJPLastPayAmt().compareTo(Env.ZERO) == 0)
				{
					MPayment lastPayment = new MPayment(getCtx(),getC_Payment_ID(),get_TrxName());
					if(lastPayment.getDocStatus().equals(DocAction.STATUS_Completed) || lastPayment.getDocStatus().equals(DocAction.STATUS_Closed))
					{
						setJPLastPayAmt(lastPayment.getPayAmt());
					}else{
						log.saveError("Error", Msg.getMsg(getCtx(), "JP_InvalidDocStatus") + " - " + Msg.getElement(getCtx(), COLUMNNAME_C_Payment_ID));
						return false;
					}
				}

			}
		}

		//JPIERE-0508
		if(newRecord || is_ValueChanged(COLUMNNAME_C_BPartner_ID))
		{
			MBillLine[] lines = getLines();
			if(lines.length > 0)
			{
				//You cannot change because there are some lines already.
				log.saveError("Error", Msg.getMsg(getCtx(), "JP_CannotChangeForLines") + " - " + Msg.getElement(getCtx(), COLUMNNAME_C_BPartner_ID));
				return false;
			}

			MBPartner bp = MBPartner.get(getCtx(), getC_BPartner_ID());
			int JP_BillSchema_ID = bp.get_ValueAsInt(MBillSchema.COLUMNNAME_JP_BillSchema_ID);
			if(JP_BillSchema_ID == 0)
			{
				setIsTaxRecalculateJP(false);
			}else {

				MBillSchema bs = MBillSchema.get(JP_BillSchema_ID);
				setIsTaxRecalculateJP(bs.isTaxRecalculateJP());
			}
		}

		setJPCarriedForwardAmt(getJPLastBillAmt().subtract(getJPLastPayAmt()));
		setJPBillAmt(getJPCarriedForwardAmt().add(getOpenAmt()));

		return true;
	}

	/**
	 * 	Get Bill Lines of Bill
	 * 	@param whereClause starting with AND
	 * 	@return lines
	 */
	private MBillLine[] getLines (String whereClause)
	{
		String whereClauseFinal = "JP_Bill_ID=? ";
		if (whereClause != null)
			whereClauseFinal += whereClause;
		List<MBillLine> list = new Query(getCtx(), I_JP_BillLine.Table_Name, whereClauseFinal, get_TrxName())
										.setParameters(getJP_Bill_ID())
										.setOrderBy(I_JP_BillLine.COLUMNNAME_Line)
										.list();
		return list.toArray(new MBillLine[list.size()]);
	}	//	getLines

	/**	Bill Lines			*/
	private MBillLine[]	m_lines;

	/**
	 * 	Get Bill Lines
	 * 	@param requery
	 * 	@return lines
	 */
	public MBillLine[] getLines (boolean requery)
	{
		if (m_lines == null || m_lines.length == 0 || requery)
			m_lines = getLines(null);
		set_TrxName(m_lines, get_TrxName());
		return m_lines;
	}	//	getLines

	/**
	 * 	Get Lines of Bill
	 * 	@return lines
	 */
	public MBillLine[] getLines()
	{
		return getLines(false);
	}	//	getLines

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
	 * Get Current Open Amt
	 *
	 * @return Total Open Amt
	 */
	public BigDecimal getCurrentOpenAmt()
	{
		getLines(true);
		BigDecimal openAmt = Env.ZERO;
		MInvoice inv = null;
		for(int i = 0 ; i < m_lines.length; i++)
		{
			inv = new MInvoice(getCtx(), m_lines[i].getC_Invoice_ID(), get_TrxName());
			openAmt = openAmt.add(inv.getOpenAmt(true, null));
		}

		return openAmt;
	}

	/**	Bill Taxes			*/
	private MBillTax[]	m_taxes;

	/**
	 * 	Get Taxes
	 *	@param requery requery
	 *	@return array of taxes
	 */
	public MBillTax[] getTaxes (boolean requery)
	{
		if (m_taxes != null && !requery)
			return m_taxes;

		final String whereClause = MBillTax.COLUMNNAME_JP_Bill_ID+"=?";
		List<MBillTax> list = new Query(getCtx(), MBillTax.Table_Name, whereClause, get_TrxName())
										.setParameters(get_ID())
										.list();
		m_taxes = list.toArray(new MBillTax[list.size()]);
		return m_taxes;
	}	//	getTaxes

}	//	DocActionTemplate
