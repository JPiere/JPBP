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
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.model.MDocType;
import org.compiere.model.MFactAcct;
import org.compiere.model.MPeriod;
import org.compiere.model.MProduct;
import org.compiere.model.MQuery;
import org.compiere.model.MSysConfig;
import org.compiere.model.MUOM;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.model.PrintInfo;
import org.compiere.model.Query;
import org.compiere.print.MPrintFormat;
import org.compiere.print.ReportEngine;
import org.compiere.process.DocAction;
import org.compiere.process.DocOptions;
import org.compiere.process.DocumentEngine;
import org.compiere.process.ProcessInfo;
import org.compiere.process.ServerProcessCtl;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

/**
 * JPIERE-0501:JPiere PP Plan
 *
 * @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 *
 */
public class MPPPlan extends X_JP_PP_Plan implements DocAction,DocOptions
{

	public MPPPlan(Properties ctx, int JP_PP_Plan_ID, String trxName)
	{
		super(ctx, JP_PP_Plan_ID, trxName);
	}

	public MPPPlan(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}


	@Override
	protected boolean beforeSave(boolean newRecord)
	{
		//Set C_UOM_ID
		if(newRecord || is_ValueChanged(MPPPlan.COLUMNNAME_C_UOM_ID) || getC_UOM_ID() == 0)
		{
			MProduct product = MProduct.get(getM_Product_ID());
			if(product.getC_UOM_ID() != getC_UOM_ID())
			{
				setC_UOM_ID(product.getC_UOM_ID());
			}
		}

		//Rounding Production Qty
		if(newRecord || is_ValueChanged(MPPPlan.COLUMNNAME_ProductionQty))
		{
			boolean isStdPrecision = MSysConfig.getBooleanValue(MPPDoc.JP_PP_UOM_STDPRECISION, true, getAD_Client_ID(), getAD_Org_ID());
			MUOM uom = MUOM.get(getC_UOM_ID());
			setProductionQty(getProductionQty().setScale(isStdPrecision ? uom.getStdPrecision() : uom.getCostingPrecision(), RoundingMode.HALF_UP));
		}

		return true;
	}

	@Override
	protected boolean afterSave(boolean newRecord, boolean success)
	{
		//Update Line Qty
		if(!newRecord && is_ValueChanged(MPPFact.COLUMNNAME_ProductionQty))
		{
			boolean isStdPrecision = MSysConfig.getBooleanValue(MPPDoc.JP_PP_UOM_STDPRECISION, true, getAD_Client_ID(), getAD_Org_ID());
			MUOM uom = null;

			BigDecimal newQty = getProductionQty();
			BigDecimal oldQty = (BigDecimal)get_ValueOld(MPPPlan.COLUMNNAME_ProductionQty) ;
			BigDecimal rate = Env.ONE;
			if(oldQty != null && oldQty.compareTo(Env.ZERO) != 0)
				rate = newQty.divide(oldQty, 4, RoundingMode.HALF_UP);

			MPPPlanLine[] lines = getPPPlanLines(true, null);
			for(MPPPlanLine line : lines)
			{
				if(line.isEndProduct())
				{
					line.setPlannedQty(getProductionQty());
					line.setQtyUsed(null);
					line.setMovementQty(getProductionQty());

				}else {
					uom = MUOM.get(line.getC_UOM_ID());
					oldQty = line.getPlannedQty();
					newQty = oldQty.multiply(rate).setScale(isStdPrecision ? uom.getStdPrecision() : uom.getCostingPrecision(), RoundingMode.HALF_UP);
					line.setPlannedQty(newQty);
					line.setQtyUsed(newQty);
					line.setMovementQty(newQty.negate());
				}
				line.saveEx(get_TrxName());
			}
		}

		return true;
	}

	/**
	 * 	Get Document Info
	 *	@return document info (untranslated)
	 */
	public String getDocumentInfo()
	{
		return getValue() + "_" + getName();
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
		int JP_PP_Plan_ID = getJP_PP_Plan_ID();
		MQuery query = new MQuery(Table_Name);
		query.addRestriction( COLUMNNAME_JP_PP_Plan_ID, MQuery.EQUAL, Integer.valueOf(JP_PP_Plan_ID));

		int AD_PrintFormat_ID = getC_DocType().getAD_PrintFormat_ID();
		MPrintFormat pf = new  MPrintFormat(getCtx(), AD_PrintFormat_ID, get_TrxName());

		PrintInfo info = new PrintInfo("0", 0, 0, 0);
		ReportEngine re = new ReportEngine(getCtx(), pf, query, info);

		if(pf.getJasperProcess_ID() > 0)
		{
			ProcessInfo pi = new ProcessInfo ("", pf.getJasperProcess_ID());
			pi.setRecord_ID ( getJP_PP_Plan_ID() );
			pi.setIsBatch(true);

			ServerProcessCtl.process(pi, null);

			return pi.getPDFReport();

		}

		return re.getPDF(file);
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


		MDocType dt = MDocType.get(getCtx(), getC_DocType_ID());

		//	Std Period open?
		if (!MPeriod.isOpen(getCtx(), getDateAcct(), dt.getDocBaseType(), getAD_Org_ID()))
		{
			m_processMsg = "@PeriodClosed@";
			return DocAction.STATUS_Invalid;
		}

//		MLine[] lines = getLines(false);
//		if (lines.length == 0)
//		{
//			m_processMsg = "@NoLines@";
//			return DocAction.STATUS_Invalid;
//		}

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

//		 setDefiniteDocumentNo();

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
	private void setDefiniteDocumentNo()
	{
//		MDocType dt = MDocType.get(getCtx(), getC_DocType_ID());
//		if (dt.isOverwriteDateOnComplete()) {
//			setDateInvoiced(TimeUtil.getDay(0));
//			if (getDateAcct().before(getDateInvoiced())) {
//				setDateAcct(getDateInvoiced());
//				MPeriod.testPeriodOpen(getCtx(), getDateAcct(), getC_DocType_ID(), getAD_Org_ID());
//			}
//		}
//		if (dt.isOverwriteSeqOnComplete()) {
//			String value = null;
//			int index = p_info.getColumnIndex("C_DocType_ID");
//			if (index == -1)
//				index = p_info.getColumnIndex("C_DocTypeTarget_ID");
//			if (index != -1)		//	get based on Doc Type (might return null)
//				value = DB.getDocumentNo(get_ValueAsInt(index), get_TrxName(), true);
//			if (value != null) {
//				setDocumentNo(value);
//			}
//		}
	}


	/**
	 * 	Void Document.
	 * 	Same as Close.
	 * 	@return true if success
	 */
	public boolean voidIt()
	{
		if (log.isLoggable(Level.INFO)) log.info("voidIt - " + toString());
		// Before Void
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_VOID);
		if (m_processMsg != null)
			return false;

		MFactAcct.deleteEx(MPPPlan.Table_ID, getJP_PP_Plan_ID(), get_TrxName());
		setPosted(true);


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
		if (log.isLoggable(Level.INFO)) log.info("closeIt - " + toString());

		setProcessed(true);//Special specification For Contract Document to update Field in case of DocStatus == 'CO'
		setDocAction(DOCACTION_None);

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
		if (log.isLoggable(Level.INFO)) log.info(toString());
		// Before reActivate
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_REACTIVATE);
		if (m_processMsg != null)
			return false;

		MFactAcct.deleteEx(MPPPlan.Table_ID, getJP_PP_Plan_ID(), get_TrxName());
		setPosted(false);

		// After reActivate
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_REACTIVATE);
		if (m_processMsg != null)
			return false;

		setDocAction(DOCACTION_Complete);
		setProcessed(false);

		return true;
	}	//	reActivateIt


	/*************************************************************************
	 * 	Get Summary
	 *	@return Summary of Document
	 */
	public String getSummary()
	{
		return getValue()+"_"+getName();
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
		return Env.ZERO;
	}	//	getApprovalAmt


	/**
	 * 	Get Document Currency
	 *	@return C_Currency_ID
	 */
	public int getC_Currency_ID()
	{
		return 0;
	}

	@Override
	public int customizeValidActions(String docStatus, Object processing, String orderType, String isSOTrx,
			int AD_Table_ID, String[] docAction, String[] options, int index)
	{
		return index;
	}


	private MPPPlanLine[] m_PPPlanLines = null;

	/**
	 * Get PP Plan Lines
	 *
	 * @param whereClause
	 * @param orderClause
	 * @return
	 */
	public MPPPlanLine[] getPPPlanLines (String whereClause, String orderClause)
	{
		StringBuilder whereClauseFinal = new StringBuilder(MPPPlanLine.COLUMNNAME_JP_PP_Plan_ID+"=? ");
		if (!Util.isEmpty(whereClause, true))
			whereClauseFinal.append(whereClause);
		if (orderClause.length() == 0)
			orderClause = MPPPlanLine.COLUMNNAME_Line;
		//
		List<MPPPlanLine> list = new Query(getCtx(), MPPPlanLine.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(get_ID())
										.setOrderBy(orderClause)
										.list();

		return list.toArray(new MPPPlanLine[list.size()]);

	}

	/**
	 * Get PP Plan Lines
	 *
	 *
	 * @param requery
	 * @param orderBy
	 * @return
	 */
	public MPPPlanLine[] getPPPlanLines(boolean requery, String orderBy)
	{
		if (m_PPPlanLines != null && !requery) {
			set_TrxName(m_PPPlanLines, get_TrxName());
			return m_PPPlanLines;
		}
		//
		String orderClause = "";
		if (orderBy != null && orderBy.length() > 0)
			orderClause += orderBy;
		else
			orderClause += MPPPlanLine.COLUMNNAME_Line;

		m_PPPlanLines = getPPPlanLines(null, orderClause);
		return m_PPPlanLines;
	}

	/**
	 * Get PP Plan Lines
	 *
	 *
	 * @return
	 */
	public MPPPlanLine[] getPPPlanLines()
	{
		return getPPPlanLines(false, null);

	}



	private MPPFact[] m_PPFacts = null;

	/**
	 * Get PP Facts
	 *
	 * @param whereClause
	 * @param orderClause
	 * @return
	 */
	public MPPFact[] getPPFacts (String whereClause, String orderClause)
	{
		StringBuilder whereClauseFinal = new StringBuilder(MPPFact.COLUMNNAME_JP_PP_Plan_ID+"=? ");
		if (!Util.isEmpty(whereClause, true))
			whereClauseFinal.append(whereClause);
		if (orderClause.length() == 0)
			orderClause = MPPFact.COLUMNNAME_DocumentNo;
		//
		List<MPPFact> list = new Query(getCtx(), MPPFact.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(get_ID())
										.setOrderBy(orderClause)
										.list();

		return list.toArray(new MPPFact[list.size()]);

	}

	/**
	 * Get PP Facts
	 *
	 *
	 * @param requery
	 * @param orderBy
	 * @return
	 */
	public MPPFact[] getPPFacts(boolean requery, String orderBy)
	{
		if (m_PPFacts != null && !requery) {
			set_TrxName(m_PPFacts, get_TrxName());
			return m_PPFacts;
		}
		//
		String orderClause = "";
		if (orderBy != null && orderBy.length() > 0)
			orderClause += orderBy;
		else
			orderClause += MPPFact.COLUMNNAME_DocumentNo;

		m_PPFacts = getPPFacts(null, orderClause);
		return m_PPFacts;
	}

	/**
	 * Get PP Facts
	 *
	 *
	 * @return
	 */
	public MPPFact[] getPPFacts()
	{
		return getPPFacts(false, null);

	}

	/**
	 *
	 * Create Fact from Plan
	 *
	 * @return
	 */
	public String createFact(String trxName)
	{

		MPPFact[] ppFacts = getPPFacts(true, null);
		for(MPPFact ppFact : ppFacts)
		{
			if( ppFact.getJP_PP_Status().equals(MPPFact.JP_PP_STATUS_WorkInProgress)
				|| ppFact.getJP_PP_Status().equals(MPPFact.JP_PP_STATUS_NotYetStarted))
			{
				//You cannot create a new PP Fact if you have a PP Fact that has not been complete.
				return Msg.getMsg(getCtx(), "JP_PP_CannotCreateFact");
			}
		}

		MPPPlanLine[] ppPLines = getPPPlanLines(true, null);
		MPPFact ppFact = new MPPFact(getCtx(), 0, get_TrxName());
		PO.copyValues(this, ppFact);
		ppFact.setDocumentNo(null);
		ppFact.setJP_PP_Doc_ID(getJP_PP_Doc_ID());
		ppFact.setJP_PP_Plan_ID(getJP_PP_Plan_ID());
		ppFact.setValue(getValue());
		ppFact.setAD_Org_ID(getAD_Org_ID());
		if(getJP_PP_PlanT_ID() == 0)
		{
			ppFact.setC_DocType_ID(MDocType.getDocType("JDF"));
		}else {
			ppFact.setC_DocType_ID(getJP_PP_PlanT().getC_DocTypeTarget_ID());
		}
		ppFact.setDocumentNo(null);
		ppFact.setMovementDate(getDateAcct());
		if(ppPLines.length > 0)
			ppFact.setIsCreated("Y");
		ppFact.setProductionQty(Env.ZERO);
		ppFact.saveEx(get_TrxName());

		ppFact.createFactLineFromPlanLine(trxName);

		return null;
	}

}	//	MPPDoc
