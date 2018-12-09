/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2006 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
package jpiere.base.plugin.org.adempiere.model;

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

import java.io.File;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.model.MDocType;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.Query;
import org.compiere.process.DocAction;
import org.compiere.process.DocOptions;
import org.compiere.process.DocumentEngine;
import org.compiere.util.Env;
import org.compiere.util.Util;

/**
 * JPIERE-0431:Contract Process Schedule
 *
 * @author Hideaki Hagiwara
 *
 */
public class MContractProcSchedule extends X_JP_ContractProcSchedule implements DocAction,DocOptions
{
	/**
	 *
	 */
	private static final long serialVersionUID = -7588955558162632796L;

    /** Standard Constructor */
    public MContractProcSchedule (Properties ctx, int JP_ContractProcSchedule_ID, String trxName)
    {
      super (ctx, JP_ContractProcSchedule_ID, trxName);

    }

    /** Load Constructor */
    public MContractProcSchedule (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }


	/**
	 * 	Get Document Info
	 *	@return document info (untranslated)
	 */
	public String getDocumentInfo()
	{
		MDocType dt = MDocType.get(getCtx(), 0);
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
		/**
		MDocType dt = MDocType.get(getCtx(), getC_DocTypeTarget_ID());

		//	Std Period open?
		if (!MPeriod.isOpen(getCtx(), getDateAcct(), dt.getDocBaseType()))
		{
			m_processMsg = "@PeriodClosed@";
			return DocAction.STATUS_Invalid;
		}
				**/

		MContractPSLine[] contractPSLines = getContractPSLines();
		if (contractPSLines.length == 0)
		{
			m_processMsg = "@NoLines@";
			return DocAction.STATUS_Invalid;
		}

		//	Add up Amounts
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_PREPARE);
		if (m_processMsg != null)
			return DocAction.STATUS_Invalid;
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
		if (log.isLoggable(Level.INFO)) log.info("approveIt - " + toString());
	//	setIsApproved(true);
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

		// setDefiniteDocumentNo();

		m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_COMPLETE);
		if (m_processMsg != null)
			return DocAction.STATUS_Invalid;

		//	Implicit Approval
		if (!isApproved())
			approveIt();
		if (log.isLoggable(Level.INFO)) log.info(toString());
		//

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
	 * 	Set the definite document number after completed
	 */
	/*
	private void setDefiniteDocumentNo() {
		MDocType dt = MDocType.get(getCtx(), getC_DocType_ID());
		if (dt.isOverwriteDateOnComplete()) {
			setDateInvoiced(TimeUtil.getDay(0));
			if (getDateAcct().before(getDateInvoiced())) {
				setDateAcct(getDateInvoiced());
				MPeriod.testPeriodOpen(getCtx(), getDateAcct(), getC_DocType_ID(), getAD_Org_ID());
			}
		}
		if (dt.isOverwriteSeqOnComplete()) {
			String value = null;
			int index = p_info.getColumnIndex("C_DocType_ID");
			if (index == -1)
				index = p_info.getColumnIndex("C_DocTypeTarget_ID");
			if (index != -1)		//	get based on Doc Type (might return null)
				value = DB.getDocumentNo(get_ValueAsInt(index), get_TrxName(), true);
			if (value != null) {
				setDocumentNo(value);
			}
		}
	}
	*/

	/**
	 * 	Void Document.
	 * 	Same as Close.
	 * 	@return true if success
	 */
	public boolean voidIt()
	{
		if (log.isLoggable(Level.INFO)) log.info("voidIt - " + toString());
		return closeIt();
	}	//	voidIt

	/**
	 * 	Close Document.
	 * 	Cancel not delivered Qunatities
	 * 	@return true if success
	 */
	public boolean closeIt()
	{
		if (log.isLoggable(Level.INFO)) log.info("closeIt - " + toString());

		//	Close Not delivered Qty
	//	setDocAction(DOCACTION_None);
		return true;
	}	//	closeIt

	/**
	 * 	Reverse Correction
	 * 	@return true if success
	 */
	public boolean reverseCorrectIt()
	{
		if (log.isLoggable(Level.INFO)) log.info("reverseCorrectIt - " + toString());
		return false;
	}	//	reverseCorrectionIt

	/**
	 * 	Reverse Accrual - none
	 * 	@return true if success
	 */
	public boolean reverseAccrualIt()
	{
		if (log.isLoggable(Level.INFO)) log.info("reverseAccrualIt - " + toString());
		return false;
	}	//	reverseAccrualIt

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
	//	sb.append(getDocumentNo());
		//	: Total Lines = 123.00 (#1)
	//	sb.append(": ")
	//		.append(Msg.translate(getCtx(),"TotalLines")).append("=").append(getTotalLines())
	//		.append(" (#").append(getLines(false).length).append(")");
		//	 - Description
	//	if (getDescription() != null && getDescription().length() > 0)
	//		sb.append(" - ").append(getDescription());
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

	@Override
	public BigDecimal getApprovalAmt()
	{
		return getTotalLines();
	}

	@Override
	public int customizeValidActions(String docStatus, Object processing, String orderType, String isSOTrx,
			int AD_Table_ID, String[] docAction, String[] options, int index)
	{
//		if(isSOTrx.equals("Y") && (orderType.equals(MDocType.DOCSUBTYPESO_Proposal)
//				|| orderType.equals(MDocType.DOCSUBTYPESO_Quotation)))
//		{
//			index = 0; //initialize the index
//			options[index++] = DocumentEngine.ACTION_Prepare;
//			options[index++] = DocumentEngine.ACTION_Void;
//			return index;
//		}
//
//		if (docStatus.equals(DocumentEngine.STATUS_Drafted) || docStatus.equals(DocumentEngine.STATUS_InProgress))
//		{
//			index = 0; //initialize the index
//			options[index++] = DocumentEngine.ACTION_Void;
//			options[index++] = DocumentEngine.ACTION_Prepare;
//			options[index++] = DocumentEngine.ACTION_Complete;
//
//			return index;
//		}

		return index;
	}


	@Override
	protected boolean beforeSave(boolean newRecord)
	{
		//For callout of Product in Line And Doc Date Management
		if(newRecord || is_ValueChanged("DateDoc"))
		{
			setDateInvoiced(getDateDoc());
			setDateOrdered(getDateDoc());
		}

		if(newRecord)
		{
			MContractContent contractContent = MContractContent.get(getCtx(), getJP_ContractContent_ID());
			setJP_Contract_ID(contractContent.getJP_Contract_ID());
			setJP_ContractProcess_ID(contractContent.getJP_ContractProcess_ID());
			setJP_ContractCalender_ID(contractContent.getJP_ContractCalender_ID());
			setDocBaseType(contractContent.getDocBaseType());
			setJP_BaseDocDocType_ID(contractContent.getJP_BaseDocDocType_ID());

			setIsSOTrx(contractContent.isSOTrx());
			setOrderType (contractContent.getOrderType());

			setJP_CreateDerivativeDocPolicy(contractContent.getJP_CreateDerivativeDocPolicy());

		}else {

			if(is_ValueChanged("JP_Contract_ID")
					|| is_ValueChanged("JP_ContractContent_ID")
					|| is_ValueChanged("JP_ContractProcess_ID")
					|| is_ValueChanged("JP_ContractCalender_ID")
					|| is_ValueChanged("DocBaseType")
					|| is_ValueChanged("JP_BaseDocDocType_ID")
					|| is_ValueChanged("JP_CreateDerivativeDocPolicy")
				)
			{
				log.saveError("Error", "変更する事ができません");
				return false;
			}

		}


		return true;
	}


	private MContractPSLine[] 	m_ContractPSLines = null;

	/**
	 *
	 * Get Contract Process Lines
	 *
	 * @param whereClause
	 * @param orderClause
	 * @return
	 */
	public MContractPSLine[] getContractPSLines (String whereClause, String orderClause)
	{
		StringBuilder whereClauseFinal = new StringBuilder(MContractPSLine.COLUMNNAME_JP_ContractProcSchedule_ID+"=? ");
		if (!Util.isEmpty(whereClause, true))
			whereClauseFinal.append(whereClause);
		if (orderClause.length() == 0)
			orderClause = MContractPSLine.COLUMNNAME_Line;

		List<MContractPSLine> list = new Query(getCtx(), MContractPSLine.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(get_ID())
										.setOrderBy(orderClause)
										.list();

		//
		return list.toArray(new MContractPSLine[list.size()]);
	}	//	getContractPSLines

	/**
	 *
	 * Get Contract Process Lines
	 *
	 * @param requery
	 * @param orderBy
	 * @return
	 */
	public MContractPSLine[] getContractPSLines (boolean requery, String orderBy)
	{
		if (m_ContractPSLines != null && !requery) {
			set_TrxName(m_ContractPSLines, get_TrxName());
			return m_ContractPSLines;
		}
		//
		String orderClause = "";
		if (orderBy != null && orderBy.length() > 0)
			orderClause += orderBy;
		else
			orderClause += "Line";
		m_ContractPSLines = getContractPSLines(null, orderClause);
		return m_ContractPSLines;
	}	//	getContractPSLines


	/**
	 *
	 * Get Contract Process Lines
	 *
	 * @return
	 */
	public MContractPSLine[] getContractPSLines()
	{
		return getContractPSLines(false, null);
	}	//	getContractPSLines


	/**
	 *
	 * Get Contract Process Schedule Ship/Receipt Lines
	 *
	 * @param JP_ContractProcPeriod_ID
	 * @param isFactCreatedJP
	 * @return
	 */
	public MContractPSInOutLine[] getContractPSInOutLines(int JP_ContractProcPeriod_ID, boolean isFactCreatedJP)
	{
		StringBuilder whereClauseFinal = new StringBuilder(MContractPSInOutLine.COLUMNNAME_JP_ContractProcSchedule_ID + "=? AND "
														+ MContractPSInOutLine.COLUMNNAME_JP_ContractProcPeriod_ID + "=? AND IsFactCreatedJP=" + (isFactCreatedJP? "'Y'":"'N'"));

		List<MContractPSInOutLine> list = new Query(getCtx(), MContractPSInOutLine.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(get_ID(),JP_ContractProcPeriod_ID)
										.setOrderBy(MContractPSInOutLine.COLUMNNAME_Line)
										.list();

		return list.toArray(new MContractPSInOutLine[list.size()]);
	}	//	getContractPSInOutLines

	/**
	 *
	 * Get Contract Process Schedule Ship/Receipt Lines
	 *
	 * @param JP_ContractProcPeriod_ID
	 * @return
	 */
	public MContractPSInOutLine[] getContractPSInOutLines(int JP_ContractProcPeriod_ID)
	{
		StringBuilder whereClauseFinal = new StringBuilder(MContractPSInOutLine.COLUMNNAME_JP_ContractProcSchedule_ID + "=? AND"
																			+ MContractPSInOutLine.COLUMNNAME_JP_ContractProcPeriod_ID + "=?");

		List<MContractPSInOutLine> list = new Query(getCtx(), MContractPSInOutLine.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(get_ID(),JP_ContractProcPeriod_ID)
										.setOrderBy(MContractPSInOutLine.COLUMNNAME_Line)
										.list();

		return list.toArray(new MContractPSInOutLine[list.size()]);
	}	//	getContractPSInOutLines

	/**
	 *
	 * Get Contract Process Schedule Ship/Receipt Lines
	 *
	 * @return
	 */
	public MContractPSInOutLine[] getContractPSInOutLines()
	{
		StringBuilder whereClauseFinal = new StringBuilder(MContractPSInOutLine.COLUMNNAME_JP_ContractProcSchedule_ID + "=?");

		List<MContractPSInOutLine> list = new Query(getCtx(), MContractPSInOutLine.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(get_ID())
										.setOrderBy(MContractPSInOutLine.COLUMNNAME_Line)
										.list();

		return list.toArray(new MContractPSInOutLine[list.size()]);
	}	//	getContractPSInOutLines


	/**
	 *
	 * Get Contract Process Schedule Invoice Lines
	 *
	 * @param JP_ContractProcPeriod_ID
	 * @param isFactCreatedJP
	 * @return
	 */
	public MContractPSInvoiceLine[] getContractPSInvoiceLines(int JP_ContractProcPeriod_ID, boolean isFactCreatedJP)
	{
		StringBuilder whereClauseFinal = new StringBuilder(MContractPSInvoiceLine.COLUMNNAME_JP_ContractProcSchedule_ID + "=? AND "
														+ MContractPSInvoiceLine.COLUMNNAME_JP_ContractProcPeriod_ID + "=? AND IsFactCreatedJP=" + (isFactCreatedJP? "'Y'":"'N'"));

		List<MContractPSInvoiceLine> list = new Query(getCtx(), MContractPSInvoiceLine.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(get_ID(),JP_ContractProcPeriod_ID)
										.setOrderBy(MContractPSInvoiceLine.COLUMNNAME_Line)
										.list();

		return list.toArray(new MContractPSInvoiceLine[list.size()]);
	}	//	getContractPSInvoiceLines

	/**
	 *
	 * Get Contract Process Schedule Invoice Lines
	 *
	 * @param JP_ContractProcPeriod_ID
	 * @return
	 */
	public MContractPSInvoiceLine[] getContractPSInvoiceLines(int JP_ContractProcPeriod_ID)
	{
		StringBuilder whereClauseFinal = new StringBuilder(MContractPSInvoiceLine.COLUMNNAME_JP_ContractProcSchedule_ID + "=? AND"
																			+ MContractPSInvoiceLine.COLUMNNAME_JP_ContractProcPeriod_ID + "=?");

		List<MContractPSInvoiceLine> list = new Query(getCtx(), MContractPSInvoiceLine.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(get_ID(),JP_ContractProcPeriod_ID)
										.setOrderBy(MContractPSInvoiceLine.COLUMNNAME_Line)
										.list();

		return list.toArray(new MContractPSInvoiceLine[list.size()]);
	}	//	getContractPSInvoiceLines

	/**
	 *
	 * Get Contract Process Schedule Invoice Lines
	 *
	 * @return
	 */
	public MContractPSInvoiceLine[] getContractPSInvoiceLines()
	{
		StringBuilder whereClauseFinal = new StringBuilder(MContractPSInvoiceLine.COLUMNNAME_JP_ContractProcSchedule_ID + "=?");

		List<MContractPSInvoiceLine> list = new Query(getCtx(), MContractPSInvoiceLine.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(get_ID())
										.setOrderBy(MContractPSInvoiceLine.COLUMNNAME_Line)
										.list();

		return list.toArray(new MContractPSInvoiceLine[list.size()]);
	}	//	getContractPSInvoiceLines

	/**
	 *
	 * Get Contract Process Scheudles
	 *
	 * @param JP_ContractContent_ID
	 * @param JP_ContractProcPeriod_ID
	 * @param trxName
	 * @return
	 */
	static public MContractProcSchedule[] getMContractProcSchedules(int JP_ContractContent_ID, int JP_ContractProcPeriod_ID, String trxName)
	{
		StringBuilder whereClauseFinal = new StringBuilder(MContractProcSchedule.COLUMNNAME_JP_ContractContent_ID+"=? AND " + MContractProcSchedule.COLUMNNAME_JP_ContractProcPeriod_ID + "=?" );

		List<MContractProcSchedule> list = new Query(Env.getCtx(), MContractProcSchedule.Table_Name, whereClauseFinal.toString(), trxName)
										.setParameters(JP_ContractContent_ID,JP_ContractProcPeriod_ID)
										.setOrderBy(MContractProcSchedule.COLUMNNAME_DocumentNo)
										.list();

		//
		return list.toArray(new MContractProcSchedule[list.size()]);
	}

}	//	MContractProcSchedule
