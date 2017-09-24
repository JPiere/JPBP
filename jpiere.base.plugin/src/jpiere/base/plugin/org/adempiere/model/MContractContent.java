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
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.model.MDocType;
import org.compiere.model.MFactAcct;
import org.compiere.model.MInOut;
import org.compiere.model.MInvoice;
import org.compiere.model.MOrder;
import org.compiere.model.MPeriod;
import org.compiere.model.MPriceList;
import org.compiere.model.MQuery;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PrintInfo;
import org.compiere.model.Query;
import org.compiere.print.MPrintFormat;
import org.compiere.print.ReportEngine;
import org.compiere.process.DocAction;
import org.compiere.process.DocOptions;
import org.compiere.process.DocumentEngine;
import org.compiere.process.ProcessInfo;
import org.compiere.process.ServerProcessCtl;
import org.compiere.util.CCache;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;



/** JPIERE-0363
*
* @author Hideaki Hagiwara
*
*/
public class MContractContent extends X_JP_ContractContent implements DocAction,DocOptions
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7588955558162632796L;


	public MContractContent(Properties ctx, int JP_Contract_ID, String trxName) 
	{
		super(ctx, JP_Contract_ID, trxName);
	}
	
	public MContractContent(Properties ctx, ResultSet rs, String trxName) 
	{
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
		// set query to search this document
		int m_docid = getJP_Contract_ID();
		MQuery query = new MQuery(Table_Name);
		query.addRestriction( COLUMNNAME_JP_Contract_ID, MQuery.EQUAL, new Integer(m_docid));
	
		// get Print Format
		//int AD_PrintFormat_ID = 1000133;
		//System.out.print(getC_DocTypeTarget_ID());
		int AD_PrintFormat_ID = getC_DocType().getAD_PrintFormat_ID();
		MPrintFormat pf = new  MPrintFormat(getCtx(), AD_PrintFormat_ID, get_TrxName());
	
		// set PrintInfo (temp)
		PrintInfo info = new PrintInfo("0", 0, 0, 0);
	
		// Create ReportEngine
		//ReportEngine re = ReportEngine.get(getCtx(), ReportEngine.JPE,  getJP_Estimation_ID(), get_TrxName());
		ReportEngine re = new ReportEngine(getCtx(), pf, query, info);
	
		// For JaperReport
		//System.out.print("PrintFormat: " + re.getPrintFormat().get_ID());
		//MPrintFormat format = re.getPrintFormat();
		// We have a Jasper Print Format
		// ==============================
		if(pf.getJasperProcess_ID() > 0)
		{
			ProcessInfo pi = new ProcessInfo ("", pf.getJasperProcess_ID());
			pi.setRecord_ID ( getJP_Contract_ID() );
			pi.setIsBatch(true);

			ServerProcessCtl.process(pi, null);

			return pi.getPDFReport();

		}
		// Standard Print Format (Non-Jasper)
		// ==================================

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
		
		//Check Lines
		if(getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
		{
			MContractLine[] lines = getLines();
			if (lines.length == 0)
			{
				m_processMsg = "@NoLines@";
				return DocAction.STATUS_Invalid;
			}
			
			if( (getDocBaseType().equals("SOO") || getDocBaseType().equals("POO"))
					&& getOrderType().equals(MContractContent.ORDERTYPE_StandardOrder) 
					&& !getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_Manual))
			{
				for(int i = 0; i < lines.length; i++)
				{
					if(!getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateInvoice))
					{
						if(lines[i].getJP_DerivativeDocPolicy_InOut() == null)
						{
							m_processMsg = Msg.getMsg(getCtx(), "FillMandatory") + Msg.getElement(getCtx(), MContractLine.COLUMNNAME_JP_DerivativeDocPolicy_InOut)
												+ " - " + Msg.getElement(getCtx(),  MContractLine.COLUMNNAME_Line) + " : " + lines[i].getLine();
							return DocAction.STATUS_Invalid;
						}
						
						if(lines[i].getJP_ContractCalender_InOut_ID() == 0)
						{
							m_processMsg = Msg.getMsg(getCtx(), "FillMandatory") + Msg.getElement(getCtx(), MContractLine.COLUMNNAME_JP_ContractCalender_InOut_ID)
												+ " - " + Msg.getElement(getCtx(),  MContractLine.COLUMNNAME_Line) + " : " + lines[i].getLine();
							return DocAction.STATUS_Invalid;
						}						
						
						if(lines[i].getJP_ContractProcess_InOut_ID() == 0)
						{
							m_processMsg = Msg.getMsg(getCtx(), "FillMandatory") + Msg.getElement(getCtx(), MContractLine.COLUMNNAME_JP_ContractProcess_InOut_ID)
												+ " - " + Msg.getElement(getCtx(),  MContractLine.COLUMNNAME_Line) + " : " + lines[i].getLine();
							return DocAction.STATUS_Invalid;
						}							
						
						if(lines[i].getJP_DerivativeDocPolicy_InOut().equals(MContractLine.JP_DERIVATIVEDOCPOLICY_INOUT_LumpOnACertainPointOfContractProcessPeriod)
								&& lines[i].getJP_ContractProcPeriod_InOut_ID() == 0)
						{
							m_processMsg = Msg.getMsg(getCtx(), "FillMandatory") + Msg.getElement(getCtx(), MContractLine.COLUMNNAME_JP_ContractProcPeriod_InOut_ID)
												+ " - " + Msg.getElement(getCtx(),  MContractLine.COLUMNNAME_Line) + " : " + lines[i].getLine();
							return DocAction.STATUS_Invalid;
						}
					}
					
					
					if(!getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceipt))
					{
						if(lines[i].getJP_DerivativeDocPolicy_Inv() == null)
						{
							m_processMsg = Msg.getMsg(getCtx(), "FillMandatory") + Msg.getElement(getCtx(), MContractLine.COLUMNNAME_JP_DerivativeDocPolicy_Inv)
												+ " - " + Msg.getElement(getCtx(),  MContractLine.COLUMNNAME_Line) + " : " + lines[i].getLine();
							return DocAction.STATUS_Invalid;
						}
						
						
						
						if(lines[i].getJP_ContractCalender_Inv_ID() == 0)
						{
							m_processMsg = Msg.getMsg(getCtx(), "FillMandatory") + Msg.getElement(getCtx(), MContractLine.COLUMNNAME_JP_ContractCalender_Inv_ID)
												+ " - " + Msg.getElement(getCtx(),  MContractLine.COLUMNNAME_Line) + " : " + lines[i].getLine();
							return DocAction.STATUS_Invalid;
						}	
						
						if(lines[i].getJP_ContractProcess_Inv_ID() == 0)
						{
							m_processMsg = Msg.getMsg(getCtx(), "FillMandatory") + Msg.getElement(getCtx(), MContractLine.COLUMNNAME_JP_ContractProcess_Inv_ID)
												+ " - " + Msg.getElement(getCtx(),  MContractLine.COLUMNNAME_Line) + " : " + lines[i].getLine();
							return DocAction.STATUS_Invalid;
						}	
						
						if(lines[i].getJP_DerivativeDocPolicy_Inv().equals(MContractLine.JP_DERIVATIVEDOCPOLICY_INV_LumpOnACertainPointOfContractProcessPeriod)
							&&	lines[i].getJP_ContractProcPeriod_Inv_ID() == 0)
						{
							m_processMsg = Msg.getMsg(getCtx(), "FillMandatory") + Msg.getElement(getCtx(), MContractLine.COLUMNNAME_JP_ContractProcPeriod_Inv_ID)
												+ " - " + Msg.getElement(getCtx(),  MContractLine.COLUMNNAME_Line) + " : " + lines[i].getLine();
							return DocAction.STATUS_Invalid;
						}
					}
					
					
				}//for i
			}
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

		 setDefiniteDocumentNo();

		m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_COMPLETE);
		if (m_processMsg != null)
			return DocAction.STATUS_Invalid;
		
		
		//Implicit Approval
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
		if(getJP_ContractProcStatus().equals(MContractContent.JP_CONTRACTPROCSTATUS_Suspend))
			setJP_ContractProcStatus(MContractContent.JP_CONTRACTPROCSTATUS_InProgress);
		
		return DocAction.STATUS_Completed;
	}	//	completeIt
	
	/**
	 * 	Set the definite document number after completed
	 */
	private void setDefiniteDocumentNo() {
		MDocType dt = MDocType.get(getCtx(), getC_DocType_ID());
		if (dt.isOverwriteDateOnComplete()) {
			setDateAcct(new Timestamp (System.currentTimeMillis()));
			MPeriod.testPeriodOpen(getCtx(), getDateAcct(), getC_DocType_ID(), getAD_Org_ID());
			
		}
		if (dt.isOverwriteSeqOnComplete()) {
			String value = null;
			int index = p_info.getColumnIndex("C_DocType_ID");
			if (index != -1)		//	get based on Doc Type (might return null)
				value = DB.getDocumentNo(get_ValueAsInt(index), get_TrxName(), true);
			if (value != null) {
				setDocumentNo(value);
			}
		}
	}
	
	public void setProcessed (boolean processed)
	{
		super.setProcessed (processed);
		if (get_ID() == 0)
			return;
		StringBuilder set = new StringBuilder("SET Processed='")
		.append((processed ? "Y" : "N"))
		.append("' WHERE JP_ContractContent_ID=").append(getJP_ContractContent_ID());
		
		StringBuilder msgdb = new StringBuilder("UPDATE JP_ContractLine ").append(set);
		int noLine = DB.executeUpdate(msgdb.toString(), get_TrxName());
		m_lines = null;

		if (log.isLoggable(Level.FINE)) log.fine(processed + " - Lines=" + noLine);
	}	//	setProcessed

	/**
	 * 	Void Document.
	 * 	Same as Close.
	 * 	@return true if success 
	 */
	public boolean voidIt()
	{
		if (log.isLoggable(Level.INFO)) log.info(toString());
		// Before Void
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_VOID);
		if (m_processMsg != null)
			return false;

		MFactAcct.deleteEx(MEstimation.Table_ID, getJP_Contract_ID(), get_TrxName());
		setPosted(true);

		// After Void
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_VOID);
		if (m_processMsg != null)
			return false;

		setProcessed(true);
		setJP_ContractProcStatus(MContractContent.JP_CONTRACTPROCSTATUS_Invalid);
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
		setJP_ContractProcStatus(MContractContent.JP_CONTRACTPROCSTATUS_Processed);
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

		MFactAcct.deleteEx(MEstimation.Table_ID, getJP_Contract_ID(), get_TrxName());
		setPosted(false);

		// After reActivate
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_REACTIVATE);
		if (m_processMsg != null)
			return false;

		setDocAction(DOCACTION_Complete);
		setProcessed(false);
		if(getJP_ContractProcStatus().equals(MContractContent.JP_CONTRACTPROCSTATUS_InProgress))
			setJP_ContractProcStatus(MContractContent.JP_CONTRACTPROCSTATUS_Suspend);

		return true;
	}	//	reActivateIt
	
	
	/*************************************************************************
	 * 	Get Summary
	 *	@return Summary of Document
	 */
	public String getSummary()
	{
		return getDocumentNo();
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
		return getTotalLines();
	}	//	getApprovalAmt

	
	@Override
	public int customizeValidActions(String docStatus, Object processing, String orderType, String isSOTrx,
			int AD_Table_ID, String[] docAction, String[] options, int index) 
	{
		if(docStatus.equals(DocAction.STATUS_Completed))
		{
			index = 0; //initialize the index
			options[index++] = DocumentEngine.ACTION_Close;
			options[index++] = DocumentEngine.ACTION_Void;
			options[index++] = DocumentEngine.ACTION_ReActivate;
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

	@Override
	protected boolean beforeSave(boolean newRecord) 
	{
		//Check - General Contract can not have Contract Content
		if(newRecord)
		{
			if(getParent().getJP_ContractType().equals(MContractT.JP_CONTRACTTYPE_GeneralContract))
			{
				log.saveError("Error", Msg.getMsg(getCtx(), "JP_GeneralContractContent"));
				return false;
			}
			
			if(getParent().getDocStatus().equals(DocAction.STATUS_Closed) || getParent().getDocStatus().equals(DocAction.STATUS_Voided)
					|| getParent().getDocStatus().equals(DocAction.STATUS_Reversed))
			{
				//You can not create Contract Content for Document status of Contract Document.
				log.saveError("Error", Msg.getMsg(getCtx(), "JP_NotCreateContractContentForDocStatus"));
				return false;
			}
		}
		
		//For callout of Product in Line
		if(newRecord || is_ValueChanged("DateDoc"))
		{
			setDateInvoiced(getDateDoc());
		}
		
		//Check overlap of Contract process date in Same contract content tempalete
		MContract contract = getParent();
		if(contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract) && 
				( newRecord || is_ValueChanged(MContractContent.COLUMNNAME_JP_ContractProcDate_From) ||  is_ValueChanged(MContractContent.COLUMNNAME_JP_ContractProcDate_To)))
		{
			MContractContent[] contractContents = contract.getContractContents();
			for(int i = 0; i < contractContents.length; i++)
			{
				//Self
				if(contractContents[i].getJP_ContractContent_ID() == getJP_ContractContent_ID())
					continue;
				
				//Diff Template
				if(contractContents[i].getJP_ContractContentT_ID() != getJP_ContractContentT_ID())
					continue;
				
				//Invalid status
				if(contractContents[i].getJP_ContractProcStatus().equals(MContractContent.JP_CONTRACTPROCSTATUS_Invalid))
					continue;
				
				//Check
				if(contractContents[i].getJP_ContractProcDate_To() != null &&  getJP_ContractProcDate_To() != null)
				{
					if(contractContents[i].getJP_ContractProcDate_From().compareTo(getJP_ContractProcDate_To()) <= 0
							&& contractContents[i].getJP_ContractProcDate_To().compareTo(getJP_ContractProcDate_From()) >= 0 )
					{
						//Overlap of Contract process date in same contract content template.
						log.saveError("Error", Msg.getMsg(getCtx(), "JP_OverlapOfContractProcessDate"));
						return false;
					}		
				
				}else if(contractContents[i].getJP_ContractProcDate_To() != null){
					
					if(contractContents[i].getJP_ContractProcDate_To().compareTo(getJP_ContractProcDate_From()) >= 0)
					{
						//overlap of Contract process date in Same contract content template
						log.saveError("Error", Msg.getMsg(getCtx(), "JP_OverlapOfContractProcessDate"));
						return false;
					}		
						
				}else if(getJP_ContractProcDate_To() != null){
					
					if(contractContents[i].getJP_ContractProcDate_From().compareTo(getJP_ContractProcDate_To()) <= 0)
					{
						//overlap of Contract process date in Same contract content template
						log.saveError("Error", Msg.getMsg(getCtx(), "JP_OverlapOfContractProcessDate"));
						return false;
					}
					
				}else{ //contractContents[i].getJP_ContractProcDate_To() == null && getJP_ContractProcDate_To() == null
					
					//overlap of Contract process date in Same contract content template
					log.saveError("Error", Msg.getMsg(getCtx(), "JP_OverlapOfContractProcessDate"));
					return false;
					
				}
				
			}
		}//Check overlap of Contract process date in Same contract content tempalete
		
		
		//Can not update for Not Unprocecced.
		if(!getJP_ContractProcStatus().equals(MContractContent.JP_CONTRACTPROCSTATUS_Unprocessed))
		{
			if(is_ValueChanged(MContractContent.COLUMNNAME_DocBaseType) 
					|| is_ValueChanged(MContractContent.COLUMNNAME_JP_BaseDocDocType_ID)
					|| is_ValueChanged(MContractContent.COLUMNNAME_JP_CreateDerivativeDocPolicy)
					|| is_ValueChanged(MContractContent.COLUMNNAME_JP_ContractCalender_ID)
					|| is_ValueChanged(MContractContent.COLUMNNAME_JP_ContractProcess_ID)
					|| is_ValueChanged(MContractContent.COLUMNNAME_JP_Contract_Acct_ID)
					|| is_ValueChanged(MContractContent.COLUMNNAME_C_BPartner_ID))
			{
				//You can not update this field because Contract Process Status is not Unprocecced.
				StringBuilder msg = new StringBuilder(Msg.getMsg(getCtx(), "JP_NotUpdateForContractProcessStatus"));
				if(is_ValueChanged(MContractContent.COLUMNNAME_DocBaseType))
					msg.append(" : ").append(Msg.getElement(getCtx(), MContractContent.COLUMNNAME_DocBaseType));
				else if(is_ValueChanged(MContractContent.COLUMNNAME_JP_BaseDocDocType_ID))
					msg.append(" : ").append(Msg.getElement(getCtx(), MContractContent.COLUMNNAME_JP_BaseDocDocType_ID));
				else if(is_ValueChanged(MContractContent.COLUMNNAME_JP_CreateDerivativeDocPolicy))
					msg.append(" : ").append(Msg.getElement(getCtx(), MContractContent.COLUMNNAME_JP_CreateDerivativeDocPolicy));
				else if(is_ValueChanged(MContractContent.COLUMNNAME_JP_ContractCalender_ID))
					msg.append(" : ").append(Msg.getElement(getCtx(), MContractContent.COLUMNNAME_JP_ContractCalender_ID));				
				else if(is_ValueChanged(MContractContent.COLUMNNAME_JP_ContractProcess_ID))
					msg.append(" : ").append(Msg.getElement(getCtx(), MContractContent.COLUMNNAME_JP_ContractProcess_ID));
				else if(is_ValueChanged(MContractContent.COLUMNNAME_JP_Contract_Acct_ID))
					msg.append(" : ").append(Msg.getElement(getCtx(), MContractContent.COLUMNNAME_JP_Contract_Acct_ID));				
				else if(is_ValueChanged(MContractContent.COLUMNNAME_C_BPartner_ID))
					msg.append(" : ").append(Msg.getElement(getCtx(), MContractContent.COLUMNNAME_C_BPartner_ID));	
				
				log.saveError("Error", msg.toString());
				return false;
			}
		}
		
		
		//Check DateOrdered
		if(!newRecord && is_ValueChanged(MContractContent.COLUMNNAME_DateOrdered) && getDateOrdered() != null)
		{
			MContractLine[] line =  getLines();
			for(int i = 0; i < line.length; i++)
			{
				line[i].setDateOrdered(getDateOrdered());
				line[i].saveEx(get_TrxName());
			}
		}
		
		
		//Check JP_BaseDocDocType_ID and DocBaseType
		if(newRecord || is_ValueChanged(MContractContentT.COLUMNNAME_JP_BaseDocDocType_ID)
				|| is_ValueChanged(MContractContentT.COLUMNNAME_DocBaseType))
		{
			MDocType docType = MDocType.get(getCtx(), getJP_BaseDocDocType_ID());
			setIsSOTrx(docType.isSOTrx());
			
			if(!getDocBaseType().equals(docType.getDocBaseType()))
			{
				log.saveError("Error", Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), MContractContentT.COLUMNNAME_JP_BaseDocDocType_ID));
				return false;
				
			}else{
				
				if(getDocBaseType().equals("POO") || getDocBaseType().equals("SOO") )
				{
					setOrderType(docType.getDocSubTypeSO());
					if(getJP_CreateDerivativeDocPolicy() != null
							&& !getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_Manual))
					{
						if(!docType.getDocSubTypeSO().equals(MDocType.DOCSUBTYPESO_StandardOrder)
								&& !docType.getDocSubTypeSO().equals(MDocType.DOCSUBTYPESO_Quotation)
								&& !docType.getDocSubTypeSO().equals(MDocType.DOCSUBTYPESO_Proposal) )
						{
							//Base doc DocType that you selected is available When Create Derivative Doc policy is Manual.
							log.saveError("Error", Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), MContractContentT.COLUMNNAME_JP_BaseDocDocType_ID)
													+ "   " + Msg.getMsg(getCtx(), "JP_BaseDocDocType_CreateDerivativeDocPolicy"));
							return false;
						}
					}
					
				}else{
					setOrderType("--");
				}
				
			}
		}
		
		
		//Check JP_CreateDerivativeDocPolicy
		if(newRecord || is_ValueChanged(MContractContent.COLUMNNAME_JP_CreateDerivativeDocPolicy))
		{
			if(getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract) 
					&& ( getDocBaseType().equals("SOO") || getDocBaseType().equals("POO") ) )
			{
				if(Util.isEmpty(getJP_CreateDerivativeDocPolicy()))
				{
					Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_CreateDerivativeDocPolicy")};
					String msg = Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs);
					log.saveError("Error",msg);
					return false;
				}
				
				if(getDocBaseType().equals("POO") || getDocBaseType().equals("SOO") )
				{
					if(getJP_CreateDerivativeDocPolicy() != null
							&& !getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_Manual))
					{
						if(!getOrderType().equals(MDocType.DOCSUBTYPESO_StandardOrder)
								&& !getOrderType().equals(MDocType.DOCSUBTYPESO_Quotation)
								&& !getOrderType().equals(MDocType.DOCSUBTYPESO_Proposal) )
						{
							//Base doc DocType that you selected is available When Create Derivative Doc policy is Manual.
							log.saveError("Error", Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), MContractContentT.JP_CREATEDERIVATIVEDOCPOLICY_Manual)
													+ "   " + Msg.getMsg(getCtx(), "JP_BaseDocDocType_CreateDerivativeDocPolicy"));
							return false;
						}
					}
					
				}
			}else{
				setJP_CreateDerivativeDocPolicy(null);
			}
			
		}
		
		
		//Check JP_ContractCalender_ID
		if(newRecord)
		{
			;//We can not check. because Create Contract content from template process can not set JP_ContractCalender_ID automatically.
		}else{
			
			if(getParent().getJP_ContractType().equals(MContractT.JP_CONTRACTTYPE_PeriodContract))
			{
				if(getJP_ContractCalender_ID() == 0)
				{
					Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractCalender_ID")};
					String msg = Msg.getMsg(Env.getCtx(), "JP_InCaseOfPeriodContract") + Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs);
					log.saveError("Error",msg);
					return false;
				}
				
			}
		}
		
		
		//Check Contract Process Period and Automatic Update
		if(getParent().getJP_ContractType().equals(MContractT.JP_CONTRACTTYPE_PeriodContract))
		{
			//Check JP_ContractProcDate_From
			if(getJP_ContractProcDate_From() == null)
			{
				Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractProcDate_From")};
				String msg = Msg.getMsg(Env.getCtx(), "JP_InCaseOfPeriodContract") + Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs);
				log.saveError("Error",msg);
				return false;
				
			}else{
				
				if(getJP_ContractProcDate_From().compareTo(getParent().getJP_ContractPeriodDate_From()) < 0 )
				{
					log.saveError("Error",Msg.getMsg(getCtx(),"JP_OutsidePperiod") + " : " + Msg.getElement(getCtx(), "JP_ContractProcDate_From"));
					return false;
				}
				
			}
			
			
			//JP_ContractProcDate_To and isAutomaticUpdateJP())
			if(getParent().isAutomaticUpdateJP())
			{
				setJP_ContractProcDate_To(getParent().getJP_ContractPeriodDate_To());

			}else{
				
				if(isAutomaticUpdateJP())
				{
					//You can not Automatic update, because Contract document is not Automatic update.
					log.saveError("Error",Msg.getMsg(getCtx(), "JP_IsAutomaticUpdateJP_UpdateError"));
					return false;
				}
				
			}
			
			
			if(getJP_ContractProcDate_To() != null)
			{
				if(getJP_ContractProcDate_To().compareTo(getParent().getJP_ContractPeriodDate_To()) > 0 )
				{
					log.saveError("Error",Msg.getMsg(getCtx(),"JP_OutsidePperiod") + " : " + Msg.getElement(getCtx(), "JP_ContractProcDate_To"));
					return false;
				}
			}
			
		}else{
			setJP_ContractProcDate_From(null);
			setJP_ContractProcDate_To(null);
		}
		
		
		//Check JP_ContractProcess_ID()
		if(newRecord)
		{
			;//We can not check. because Create Contract content from template process can not set JP_ContractProcess_ID automatically.
		}else{
			
			if(getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
			{
				if(getJP_ContractProcess_ID() == 0)
				{
					Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractProcess_ID")};
					String msg = Msg.getMsg(Env.getCtx(), "JP_InCaseOfPeriodContract") + Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs);
					log.saveError("Error",msg);
					return false;
				}
				
			}else{
				;//Noting to do.
			}
			
			if(is_ValueChanged("JP_ContractProcess_ID"))
			{
				MContractProcess contractProcess = MContractProcess.get(getCtx(), getJP_ContractProcess_ID());
				if(!contractProcess.getDocBaseType().equals(getDocBaseType()) || !contractProcess.isCreateBaseDocJP())
				{
					log.saveError("Error", Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "JP_ContractProcess_ID")
						+ " and  " + Msg.getElement(getCtx(), "DocBaseType"));
					return false;
				}
			}
		}	
		
		
		//Check Price List and IsSotrx
		if(newRecord || is_ValueChanged("M_PriceList_ID") || is_ValueChanged("IsSOTrx"))
		{
			MPriceList pricelist = MPriceList.get(getCtx(), getM_PriceList_ID(), get_TrxName());
			if(pricelist.isSOPriceList() != isSOTrx())
			{
				log.saveError("Error", Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), "M_PriceList_ID")
										+ " and  " + Msg.getElement(getCtx(), "IsSOTrx"));
				return false;
			}
		}
		
		
		
		return true;
	}
	
	
	
	

	@Override
	protected boolean afterSave(boolean newRecord, boolean success) 
	{
		//	Sync Lines
		if (   is_ValueChanged("AD_Org_ID")
		    || is_ValueChanged(MOrder.COLUMNNAME_C_BPartner_ID)
		    || is_ValueChanged(MOrder.COLUMNNAME_C_BPartner_Location_ID)
		    || is_ValueChanged(MOrder.COLUMNNAME_DateOrdered)
		    || is_ValueChanged(MOrder.COLUMNNAME_DatePromised)
		    || is_ValueChanged(MOrder.COLUMNNAME_M_Warehouse_ID)
		    || is_ValueChanged(MOrder.COLUMNNAME_M_Shipper_ID)
		    || is_ValueChanged(MOrder.COLUMNNAME_C_Currency_ID)) {
			MContractLine[] lines = getLines();
			for (MContractLine line : lines) {
				if (is_ValueChanged("AD_Org_ID"))
					line.setAD_Org_ID(getAD_Org_ID());
				if (is_ValueChanged(MOrder.COLUMNNAME_C_BPartner_ID))
					line.setC_BPartner_ID(getC_BPartner_ID());
				if (is_ValueChanged(MOrder.COLUMNNAME_C_BPartner_Location_ID))
					line.setC_BPartner_Location_ID(getC_BPartner_Location_ID());
				if (is_ValueChanged(MOrder.COLUMNNAME_DateOrdered))
					line.setDateOrdered(getDateOrdered());
				if (is_ValueChanged(MOrder.COLUMNNAME_DatePromised))
					line.setDatePromised(getDatePromised());
				line.saveEx();
			}
		}
		
		return true;
	}

	//Cache parent
	private MContract parent = null;
	
	public MContract getParent()
	{
		if(parent == null)
		{
			parent = new MContract(getCtx(), getJP_Contract_ID(), null);
		}
		
		return parent;
	}
	
	//Reset Parent Cache
	public void setParent(MContract contract)
	{
			parent = contract;
	}
	
	private MContractLine[] 	m_lines = null;
	
	public MContractLine[] getLines (String whereClause, String orderClause)
	{
		StringBuilder whereClauseFinal = new StringBuilder(MContractLine.COLUMNNAME_JP_ContractContent_ID+"=? ");
		if (!Util.isEmpty(whereClause, true))
			whereClauseFinal.append(whereClause);
		if (orderClause.length() == 0)
			orderClause = MContractLine.COLUMNNAME_Line;
		
		List<MContractLine> list = new Query(getCtx(), MContractLine.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(get_ID())
										.setOrderBy(orderClause)
										.list();

		//
		return list.toArray(new MContractLine[list.size()]);		
	}	//	getLines

	public MContractLine[] getLines (boolean requery, String orderBy)
	{
		if (m_lines != null && !requery) {
			set_TrxName(m_lines, get_TrxName());
			return m_lines;
		}
		//
		String orderClause = "";
		if (orderBy != null && orderBy.length() > 0)
			orderClause += orderBy;
		else
			orderClause += "Line";
		m_lines = getLines(null, orderClause);
		return m_lines;
	}	//	getLines


	public MContractLine[] getLines()
	{
		return getLines(false, null);
	}	//	getLines
	
	
	/**	Cache				*/
	private static CCache<Integer,MContractContent>	s_cache = new CCache<Integer,MContractContent>(Table_Name, 20);
	
	/**
	 * 	Get from Cache
	 *	@param ctx context
	 *	@param JP_ContractContent_ID id
	 *	@return Contract Calender
	 */
	public static MContractContent get (Properties ctx, int JP_ContractContent_ID)
	{
		Integer ii = new Integer (JP_ContractContent_ID);
		MContractContent retValue = (MContractContent)s_cache.get(ii);
		if (retValue != null)
			return retValue;
		retValue = new MContractContent (ctx, JP_ContractContent_ID, null);
		if (retValue.get_ID () != 0)
			s_cache.put (JP_ContractContent_ID, retValue);
		return retValue;
	}	//	get
	
	
	/**
	 * 
	 * @param ctx
	 * @param JP_ContractProcPeriod_ID
	 * @return
	 */
	public MOrder[] getOrderByContractPeriod(Properties ctx, int JP_ContractProcPeriod_ID, String trxName)
	{
		ArrayList<MOrder> list = new ArrayList<MOrder>();
		final String sql = "SELECT * FROM C_Order WHERE JP_ContractContent_ID=? AND JP_ContractProcPeriod_ID=? AND DocStatus NOT IN ('VO','RE')";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, trxName);
			pstmt.setInt(1, get_ID());
			pstmt.setInt(2, JP_ContractProcPeriod_ID);
			rs = pstmt.executeQuery();
			while(rs.next())
				list.add(new MOrder(getCtx(), rs, trxName));
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}
		
		
		MOrder[] orderes = new MOrder[list.size()];
		list.toArray(orderes);
		return orderes;
	}
	
	public MInvoice[] getInvoiceByContractPeriod(Properties ctx, int JP_ContractProcPeriod_ID, String trxName)
	{
		ArrayList<MInvoice> list = new ArrayList<MInvoice>();
		final String sql = "SELECT * FROM C_Invoice WHERE JP_ContractContent_ID=? AND JP_ContractProcPeriod_ID=? AND DocStatus NOT IN ('VO','RE')";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, trxName);
			pstmt.setInt(1, get_ID());
			pstmt.setInt(2, JP_ContractProcPeriod_ID);
			rs = pstmt.executeQuery();
			while(rs.next())
				list.add(new MInvoice(getCtx(), rs, trxName));
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}
		
		
		MInvoice[] invoices = new MInvoice[list.size()];
		list.toArray(invoices);
		return invoices;
	}
	
	public MInOut[] getInOutByContractPeriod(Properties ctx, int JP_ContractProcPeriod_ID, String trxName)
	{
		ArrayList<MInOut> list = new ArrayList<MInOut>();
		final String sql = "SELECT * FROM M_InOut WHERE JP_ContractContent_ID=? AND JP_ContractProcPeriod_ID=? AND DocStatus NOT IN ('VO','RE')";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, trxName);
			pstmt.setInt(1, get_ID());
			pstmt.setInt(2, JP_ContractProcPeriod_ID);
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add(new MInOut(getCtx(), rs, trxName));
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}
		
		
		MInOut[] inOuts = new MInOut[list.size()];
		list.toArray(inOuts);
		return inOuts;
	}
	
	
	public MContractProcess[] getContractProcessDerivativeInOutByCalender(int JP_ContractCalender_ID)
	{
		ArrayList<MContractProcess> list = new ArrayList<MContractProcess>();
		final String sql = "SELECT DISTINCT JP_ContractProcess_InOut_ID FROM JP_ContractLine WHERE JP_ContractContent_ID=? AND JP_ContractCalender_InOut_ID = ?";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, get_ID());
			pstmt.setInt(2, JP_ContractCalender_ID);
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add(MContractProcess.get(getCtx(), rs.getInt(1)));
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}
		
		MContractProcess[] 	processes = new MContractProcess[list.size()];
		list.toArray(processes);
		return processes;
	}
	
	
	public MContractProcess[] getContractProcessDerivativeInvoiceByCalender(int JP_ContractCalender_ID)
	{
		ArrayList<MContractProcess> list = new ArrayList<MContractProcess>();
		final String sql = "SELECT DISTINCT JP_ContractProcess_Inv_ID FROM JP_ContractLine WHERE JP_ContractContent_ID=? AND JP_ContractCalender_Inv_ID = ?";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, get_ID());
			pstmt.setInt(2, JP_ContractCalender_ID);
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add(MContractProcess.get(getCtx(), rs.getInt(1)));
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}
		
		MContractProcess[] 	processes = new MContractProcess[list.size()];
		list.toArray(processes);
		return processes;
	}
	
}	//	MContractContent
