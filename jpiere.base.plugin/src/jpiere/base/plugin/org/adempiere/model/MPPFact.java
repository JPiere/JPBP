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
import org.compiere.model.MProduction;
import org.compiere.model.MProductionLine;
import org.compiere.model.MProductionLineMA;
import org.compiere.model.MSysConfig;
import org.compiere.model.MUOM;
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
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.MPPPlanLine.PPPlanLineFactQty;

/**
 * JPIERE-0501:JPiere PP Fact
 *
 * @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 *
 */
public class MPPFact extends X_JP_PP_Fact implements DocAction,DocOptions
{

	public MPPFact(Properties ctx, int JP_PP_Fact_ID, String trxName)
	{
		super(ctx, JP_PP_Fact_ID, trxName);
	}

	public MPPFact(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}

	private MPPPlan m_PPPlan = null;

	public MPPPlan getParent()
	{
		if(m_PPPlan == null)
			m_PPPlan = new MPPPlan(getCtx(), getJP_PP_Plan_ID(), get_TrxName());
		else
			m_PPPlan.set_TrxName(get_TrxName());

		return m_PPPlan;
	}


	@Override
	protected boolean beforeSave(boolean newRecord)
	{
		//Check Parent processed
		if(newRecord)
		{
			if(getParent().isProcessed())
			{
				log.saveError("Error", Msg.getElement(getCtx(), MPPFact.COLUMNNAME_Processed));
				return false;
			}
		}

		//Set M_Product_ID
		if(newRecord || is_ValueChanged(MPPFact.COLUMNNAME_M_Product_ID) || getM_Product_ID() == 0)
		{

			setM_Product_ID(getParent().getM_Product_ID());
		}

		//Set C_UOM_ID
		if(newRecord || is_ValueChanged(MPPFact.COLUMNNAME_C_UOM_ID) || getC_UOM_ID() == 0)
		{
			MProduct product = MProduct.get(getM_Product_ID());
			if(product.getC_UOM_ID() != getC_UOM_ID())
			{
				setC_UOM_ID(product.getC_UOM_ID());
			}
		}

		//Set JP_PP_Workload_UOM_ID
		if(newRecord || is_ValueChanged(MPPFact.COLUMNNAME_JP_PP_Workload_UOM_ID) || getJP_PP_Workload_UOM_ID() == 0)
		{
			setC_UOM_ID(getParent().getJP_PP_Workload_UOM_ID());
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
			BigDecimal oldQty = (BigDecimal)get_ValueOld(MPPFact.COLUMNNAME_ProductionQty) ;
			BigDecimal rate = Env.ONE;
			if(oldQty != null && oldQty.compareTo(Env.ZERO) != 0)
				rate = newQty.divide(oldQty, 4, RoundingMode.HALF_UP);

			MPPFactLine[] lines = getPPFactLines(true, null);
			for(MPPFactLine line : lines)
			{
				if(line.isEndProduct())
				{
					line.setMovementQty(newQty);
				}else {
					uom = MUOM.get(line.getC_UOM_ID());
					oldQty = line.getQtyUsed();
					newQty = oldQty.multiply(rate).setScale(isStdPrecision ? uom.getStdPrecision() : uom.getCostingPrecision(), RoundingMode.HALF_UP);
					line.setQtyUsed(newQty);
				}
				line.saveEx(get_TrxName());
			}
		}

		if(newRecord || is_ValueChanged(MPPFact.COLUMNNAME_JP_PP_Workload_Fact))
		{

			String sql = "UPDATE JP_PP_Plan SET JP_PP_Workload_Fact=(SELECT SUM(JP_PP_Workload_Fact) FROM JP_PP_Fact WHERE JP_PP_Plan_ID =?) "
								+ " WHERE JP_PP_Plan_ID=?";

			int no = DB.executeUpdate(sql
										, new Object[]{ getJP_PP_Plan_ID(), getJP_PP_Plan_ID()}
										, false, get_TrxName(), 0);

			if (no != 1)
			{
					log.saveError("DBExecuteError", sql);
					return false;
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
		return null;
	}

	/**
	 * 	Create PDF file
	 *	@param file output file
	 *	@return file if success
	 */
	public File createPDF (File file)
	{
		return null;
	}


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
	}

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
		setProcessing(false);
		return true;
	}

	/**
	 * 	Invalidate Document
	 * 	@return true if success
	 */
	public boolean invalidateIt()
	{
		setDocAction(DOCACTION_Prepare);
		return true;
	}

	/**
	 *	Prepare Document
	 * 	@return new status (In Progress or Invalid)
	 */
	public String prepareIt()
	{
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


		m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_PREPARE);
		if (m_processMsg != null)
			return DocAction.STATUS_Invalid;

		m_justPrepared = true;
		if (!DOCACTION_Complete.equals(getDocAction()))
			setDocAction(DOCACTION_Complete);
		return DocAction.STATUS_InProgress;
	}


	/**
	 * 	Approve Document
	 * 	@return true if success
	 */
	public boolean  approveIt()
	{
		setIsApproved(true);
		return true;
	}


	/**
	 * 	Reject Approval
	 * 	@return true if success
	 */
	public boolean rejectIt()
	{
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

		MPPFactLine[] ppFactLines = getPPFactLines(true, null);
		MPPFactLineMA[] ppFactLineMAs = null;
		BigDecimal productionQty = getProductionQty();
		for(MPPFactLine ppFactLine : ppFactLines)
		{
			if( ppFactLine.getM_Product_ID() == getM_Product_ID()
					&& ppFactLine.isEndProduct())
			{
				productionQty = ppFactLine.getMovementQty();
				break;
			}
		}

		setProductionQty(productionQty);

		if(getM_Production_ID() == 0)
		{
			if(ppFactLines.length > 0)
			{
				MProduction pp = new MProduction(getCtx(), 0 , get_TrxName());
				PO.copyValues(this, pp);
				pp.setAD_Org_ID(getAD_Org_ID());
				pp.set_ValueNoCheck(MPPFact.COLUMNNAME_JP_PP_Fact_ID, getJP_PP_Fact_ID());
				pp.setDocumentNo(null);
				pp.setDatePromised(getMovementDate());
				pp.setMovementDate(getMovementDate());
				pp.setProductionQty(productionQty);
				pp.setIsActive(true);
				pp.setProcessed(false);
				pp.setPosted(false);
				pp.setDocStatus(DocAction.STATUS_Drafted);
				pp.setDocAction(DocAction.ACTION_Complete);
				pp.setIsCreated("Y");
				pp.saveEx(get_TrxName());
				setM_Production_ID(pp.getM_Production_ID());
				setJP_PP_Status(MPPFact.JP_PP_STATUS_Completed);

				for(MPPFactLine ppFactLine : ppFactLines)
				{
					MProductionLine ppLine = new MProductionLine(getCtx(), 0 , get_TrxName());
					PO.copyValues(ppFactLine, ppLine);
					ppLine.setM_Production_ID(pp.getM_Production_ID());
					ppLine.setAD_Org_ID(pp.getAD_Org_ID());
					ppLine.saveEx(get_TrxName());

					ppFactLineMAs = ppFactLine.getPPFactLineMAs();
					for(MPPFactLineMA ppFactLineMA : ppFactLineMAs)
					{
						MProductionLineMA ppLineMA = new MProductionLineMA(getCtx(), 0 ,get_TrxName());
						PO.copyValues(ppFactLineMA, ppLineMA);
						ppLineMA.setM_ProductionLine_ID(ppLine.getM_ProductionLine_ID());
						ppLineMA.setM_AttributeSetInstance_ID(ppFactLineMA.getM_AttributeSetInstance_ID());
						ppLineMA.setDateMaterialPolicy(ppFactLineMA.getDateMaterialPolicy());
						ppLineMA.setMovementQty(ppFactLineMA.getMovementQty());
						ppLineMA.saveEx(get_TrxName());
					}
				}

				pp.processIt(DocAction.ACTION_Complete);

			}
		}

		setJP_PP_Status(JP_PP_STATUS_Completed);

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
//	private void setDefiniteDocumentNo()
//	{
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
//	}


	/**
	 * 	Void Document.
	 * 	Same as Close.
	 * 	@return true if success
	 */
	public boolean voidIt()
	{
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_VOID);
		if (m_processMsg != null)
			return false;

		if (DOCSTATUS_Closed.equals(getDocStatus())
				|| DOCSTATUS_Reversed.equals(getDocStatus())
				|| DOCSTATUS_Voided.equals(getDocStatus()))
		{
			m_processMsg = "Document Closed: " + getDocStatus();
			setDocAction(DOCACTION_None);
			return false;
		}

		MFactAcct.deleteEx(MPPFact.Table_ID, getJP_PP_Fact_ID(), get_TrxName());
		setPosted(true);

		MPPFactLine[] lines = getPPFactLines();
		for(MPPFactLine line : lines)
		{
			;//TODO Processedフラグをつける
		}

		setJP_PP_Status(JP_PP_STATUS_Void);

		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_VOID);
		if (m_processMsg != null)
			return false;

		setProcessed(true);
		setDocAction(DOCACTION_None);

		return true;
	}

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

		if(getM_Production_ID() != 0)
		{
			MProduction pp = new MProduction(getCtx(), getM_Production_ID(), get_TrxName());
			if(pp.getDocStatus().equals(DocAction.STATUS_Completed))
			{
				pp.processIt(DocAction.ACTION_Close);
				pp.saveEx(get_TrxName());
			}
		}

		// After Close
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_CLOSE);
		if (m_processMsg != null)
			return false;

		setProcessed(true);
		setDocAction(DOCACTION_None);

		return true;
	}

	/**
	 * 	Reverse Correction
	 * 	@return true if success
	 */
	public boolean reverseCorrectIt()
	{
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_REVERSECORRECT);
		if (m_processMsg != null)
			return false;

		if(getM_Production_ID() != 0)
		{
			MProduction pp = new MProduction(getCtx(), getM_Production_ID(), get_TrxName());
			if(pp.getDocStatus().equals(DocAction.STATUS_Completed))
			{
				pp.processIt(DocAction.ACTION_Reverse_Correct);
				pp.saveEx(get_TrxName());
			}
		}

		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_REVERSECORRECT);
		if (m_processMsg != null)
			return false;

		setProcessed(true);
		setDocAction(DOCACTION_None);

		return true;
	}


	/**
	 * 	Reverse Accrual - none
	 * 	@return true if success
	 */
	public boolean reverseAccrualIt()
	{
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_REVERSECORRECT);
		if (m_processMsg != null)
			return false;

		if(getM_Production_ID() != 0)
		{
			MProduction pp = new MProduction(getCtx(), getM_Production_ID(), get_TrxName());
			if(pp.getDocStatus().equals(DocAction.STATUS_Completed))
			{
				pp.processIt(DocAction.ACTION_Reverse_Accrual);
				pp.saveEx(get_TrxName());
			}
		}

		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_REVERSECORRECT);
		if (m_processMsg != null)
			return false;

		setProcessed(true);
		setDocAction(DOCACTION_None);

		return true;
	}

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

		MFactAcct.deleteEx(MPPFact.Table_ID, getJP_PP_Fact_ID(), get_TrxName());
		setPosted(false);

		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_REACTIVATE);
		if (m_processMsg != null)
			return false;

		setDocAction(DOCACTION_Complete);
		setProcessed(false);

		return false;
	}


	/*************************************************************************
	 * 	Get Summary
	 *	@return Summary of Document
	 */
	public String getSummary()
	{
		return getValue()+"_"+getName();
	}


	/**
	 * 	Get Process Message
	 *	@return clear text error message
	 */
	public String getProcessMsg()
	{
		return m_processMsg;
	}

	/**
	 * 	Get Document Owner (Responsible)
	 *	@return AD_User_ID
	 */
	public int getDoc_User_ID()
	{
		return getSalesRep_ID();
	}

	/**
	 * 	Get Document Approval Amount
	 *	@return amount
	 */
	public BigDecimal getApprovalAmt()
	{
		return Env.ZERO;
	}


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
		if (docStatus.equals(DocumentEngine.STATUS_Drafted) || docStatus.equals(DocumentEngine.STATUS_InProgress))
		{
			index = 0;
			options[index++] = DocumentEngine.ACTION_Void;
			options[index++] = DocumentEngine.ACTION_Prepare;
			options[index++] = DocumentEngine.ACTION_Complete;

			return index;

		}else if(docStatus.equals(DocumentEngine.STATUS_Completed)) {

			if(getM_Production_ID() == 0)
			{
				index = 0;
				options[index++] = DocumentEngine.ACTION_Void;
				options[index++] = DocumentEngine.ACTION_Close;
				options[index++] = DocumentEngine.ACTION_ReActivate;

			}else {

				index = 0;
				options[index++] = DocumentEngine.ACTION_Close;
				options[index++] = DocumentEngine.ACTION_Reverse_Accrual;
				options[index++] = DocumentEngine.ACTION_Reverse_Correct;
			}


			return index;
		}

		return index;
	}


	private MPPFactLine[] m_PPFactLines = null;

	/**
	 * Get PP Fact Lines
	 *
	 * @param whereClause
	 * @param orderClause
	 * @return
	 */
	public MPPFactLine[] getPPFactLines (String whereClause, String orderClause)
	{
		StringBuilder whereClauseFinal = new StringBuilder(MPPFactLine.COLUMNNAME_JP_PP_Fact_ID+"=? ");
		if (!Util.isEmpty(whereClause, true))
			whereClauseFinal.append(whereClause);
		if (orderClause.length() == 0)
			orderClause = MPPFactLine.COLUMNNAME_Line;
		//
		List<MPPFactLine> list = new Query(getCtx(), MPPFactLine.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(get_ID())
										.setOrderBy(orderClause)
										.list();

		return list.toArray(new MPPFactLine[list.size()]);

	}

	/**
	 * Get PP Fact Lines
	 *
	 *
	 * @param requery
	 * @param orderBy
	 * @return
	 */
	public MPPFactLine[] getPPFactLines(boolean requery, String orderBy)
	{
		if (m_PPFactLines != null && !requery) {
			set_TrxName(m_PPFactLines, get_TrxName());
			return m_PPFactLines;
		}
		//
		String orderClause = "";
		if (orderBy != null && orderBy.length() > 0)
			orderClause += orderBy;
		else
			orderClause += MPPFactLine.COLUMNNAME_Line;

		m_PPFactLines = getPPFactLines(null, orderClause);
		return m_PPFactLines;
	}

	/**
	 * Get PP Fact Lines
	 *
	 *
	 * @return
	 */
	public MPPFactLine[] getPPFactLines()
	{
		return getPPFactLines(false, null);

	}


	public String createFactLineFromPlanLine(String trxName)
	{
		MPPPlan ppPlan = new MPPPlan(getCtx(), getJP_PP_Plan_ID(), trxName);
		MPPPlanLine[] ppPLines = ppPlan.getPPPlanLines();
		MPPFactLine ppFLine = null;

		PPPlanLineFactQty factQty = null;
		BigDecimal plannedQty = Env.ZERO;
		BigDecimal qtyUsed = Env.ZERO;
		BigDecimal movementQty = Env.ZERO;
		for(MPPPlanLine ppPLine : ppPLines)
		{
			factQty = ppPLine.getPPPlanLineFactQty(get_TrxName());

			ppFLine = new MPPFactLine(getCtx(), 0 , get_TrxName());
			PO.copyValues(ppPLine, ppFLine);
			ppFLine.setJP_PP_Fact_ID(getJP_PP_Fact_ID());
			ppFLine.setJP_PP_PlanLine_ID(ppPLine.getJP_PP_PlanLine_ID());
			ppFLine.setLine(ppPLine.getLine());
			ppFLine.setAD_Org_ID(getAD_Org_ID());
			ppFLine.setIsEndProduct(ppPLine.isEndProduct());
			if(ppPLine.isEndProduct())
			{
				plannedQty = ppPLine.getPlannedQty().subtract(factQty.getMovementQty());
				if(plannedQty.signum() == 0)
					plannedQty = Env.ZERO;
				qtyUsed = null;
				movementQty = plannedQty;
			}else {
				plannedQty = ppPLine.getPlannedQty().add(factQty.getMovementQty());
				if(plannedQty.signum() == 0)
				{
					plannedQty = Env.ZERO;
					qtyUsed = Env.ZERO;
					movementQty = Env.ZERO;
				}else {
					qtyUsed = plannedQty;
					movementQty = plannedQty.negate();
				}
			}

			ppFLine.setPlannedQty(plannedQty);
			ppFLine.setQtyUsed(qtyUsed);
			ppFLine.setMovementQty(movementQty);

			ppFLine.saveEx(get_TrxName());
		}

		return null;
	}
}	//	MPPDoc
