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
import java.sql.Timestamp;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MBPartner;
import org.compiere.model.MBPartnerLocation;
import org.compiere.model.MCountry;
import org.compiere.model.MCurrency;
import org.compiere.model.MDocType;
import org.compiere.model.MFactAcct;
import org.compiere.model.MInvoice;
import org.compiere.model.MLocation;
import org.compiere.model.MOrder;
import org.compiere.model.MPayment;
import org.compiere.model.MPeriod;
import org.compiere.model.MPriceList;
import org.compiere.model.MPriceListVersion;
import org.compiere.model.MQuery;
import org.compiere.model.MRMA;
import org.compiere.model.MSysConfig;
import org.compiere.model.MTax;
import org.compiere.model.MUser;
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
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.base.IJPiereTaxProvider;
import jpiere.base.plugin.util.JPiereUtil;

/**
 *	JPIERE-0183: Estimation
 *
 *  @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 *
 */
public class MEstimation extends X_JP_Estimation implements DocAction,DocOptions
{
	/**
	 *
	 */
	private static final long serialVersionUID = -7588955558162632796L;

	/**	Estimation Lines					*/
	protected MEstimationLine[] 	m_lines = null;
	/**	Tax Lines					*/
	protected MEstimationTax[] 	m_taxes = null;

	public MEstimation(Properties ctx, int JP_Estimation_ID, String trxName)
	{
		super(ctx, JP_Estimation_ID, trxName);
	}

	public MEstimation(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}

	/**
	 * 	Get Document Info
	 *	@return document info (untranslated)
	 */
	public String getDocumentInfo()
	{
		MDocType dt = MDocType.get(getCtx(), getC_DocTypeTarget_ID());
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
	 *
	 *	Thanks - Takanobu Tsuchii
	 */
	public File createPDF (File file)
	{
		// set query to search this document
		int m_docid = getJP_Estimation_ID();
		MQuery query = new MQuery(Table_Name);
		query.addRestriction( COLUMNNAME_JP_Estimation_ID, MQuery.EQUAL, Integer.valueOf(m_docid));

		// get Print Format
		//int AD_PrintFormat_ID = 1000133;
		//System.out.print(getC_DocTypeTarget_ID());
		int AD_PrintFormat_ID = getC_DocTypeTarget().getAD_PrintFormat_ID();
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
				pi.setRecord_ID ( getJP_Estimation_ID() );
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

		MDocType dt = MDocType.get(getCtx(), getC_DocTypeTarget_ID());

		//	Std Period open?
		if (!MPeriod.isOpen(getCtx(), getDateAcct(), dt.getDocBaseType(), getAD_Org_ID()))
		{
			m_processMsg = "@PeriodClosed@";
			return DocAction.STATUS_Invalid;
		}
		MEstimationLine[] lines = getLines();
		if (lines.length == 0)
		{
			m_processMsg = "@NoLines@";
			return DocAction.STATUS_Invalid;
		}

		//JPIERE-0279:Check Estimation Amount Consistency
		String msg =  amountConsistencyCheck();
		if(!Util.isEmpty(msg))
		{
			m_processMsg = msg;
			return DocAction.STATUS_Invalid;
		}

		if (!calculateTaxTotal())
		{
			m_processMsg = "Error calculating tax";
			return DocAction.STATUS_Invalid;
		}


		m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_PREPARE);
		if (m_processMsg != null)
			return DocAction.STATUS_Invalid;
		m_justPrepared = true;
		if (!DOCACTION_Complete.equals(getDocAction()))
			setDocAction(DOCACTION_Complete);
		return DocAction.STATUS_InProgress;
	}	//	prepareIt


	/**
	 * 	Calculate Tax and Total
	 * 	@return true if tax total calculated
	 */
	public boolean calculateTaxTotal()
	{
		log.fine("");
		//	Delete Taxes
		DB.executeUpdateEx("DELETE FROM JP_EstimationTax WHERE JP_Estimation_ID = " + getJP_Estimation_ID(), get_TrxName());
		m_taxes = null;

		MTax[] taxes = getTaxes();
		for (MTax tax : taxes)
		{
			IJPiereTaxProvider taxCalculater = JPiereUtil.getJPiereTaxProvider(tax);
			if (taxCalculater == null)
				throw new AdempiereException(Msg.getMsg(getCtx(), "TaxNoProvider"));

			//MTaxProvider provider = new MTaxProvider(tax.getCtx(), tax.getC_TaxProvider_ID(), tax.get_TrxName());
			if (!taxCalculater.calculateEstimationTaxTotal(null, this))
				return false;
		}
		return true;
	}	//	calculateTaxTotal


	public MTax[] getTaxes()
	{
		Hashtable<Integer, MTax> taxes = new Hashtable<Integer, MTax>();
		MEstimationLine[] lines = getLines();
		for (MEstimationLine line : lines)
		{
            MTax tax = taxes.get(line.getC_Tax_ID());
            if (tax == null)
            {
            	tax = MTax.get(getCtx(), line.getC_Tax_ID());
            	taxes.put(tax.getC_Tax_ID(), tax);
            }
		}

		MTax[] retValue = new MTax[taxes.size()];
		taxes.values().toArray(retValue);

		return retValue;
	}//getTaxes()


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

		//	Implicit Approval
	//	if (!isApproved())
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
	private void setDefiniteDocumentNo() {
		MDocType dt = MDocType.get(getCtx(), getC_DocType_ID());
		if (dt.isOverwriteDateOnComplete()) {
			setDateOrdered(new Timestamp (System.currentTimeMillis()));
			if (getDateAcct().before(getDateOrdered())) {
				setDateAcct(getDateOrdered());
				MPeriod.testPeriodOpen(getCtx(), getDateAcct(), getC_DocType_ID(), getAD_Org_ID());
			}
		}
		if (dt.isOverwriteSeqOnComplete()) {
			if (this.getProcessedOn().signum() == 0) {
				String value = DB.getDocumentNo(getC_DocType_ID(), get_TrxName(), true, this);
				if (value != null)
					setDocumentNo(value);
			}
		}
	}


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

		MFactAcct.deleteEx(MEstimation.Table_ID, getJP_Estimation_ID(), get_TrxName());
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

		MFactAcct.deleteEx(MEstimation.Table_ID, getJP_Estimation_ID(), get_TrxName());
		setPosted(false);

		// After reActivate
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_REACTIVATE);
		if (m_processMsg != null)
			return false;

		setDocAction(DOCACTION_Complete);
		setProcessed(false);

		return true;
	}	//	reActivateIt

	/**
	 * 	Set Processed.
	 * 	Propagate to Lines/Taxes
	 *	@param processed processed
	 */
	public void setProcessed (boolean processed)
	{
		super.setProcessed (processed);
//		if (get_ID() == 0)
//			return;
//		String set = "SET Processed='"
//			+ (processed ? "Y" : "N")
//			+ "' WHERE JP_Estimation_ID =" + getJP_Estimation_ID();
//		int noLine = DB.executeUpdateEx("UPDATE JP_EstimationLine " + set, get_TrxName());
//		int noTax = DB.executeUpdateEx("UPDATE JP_EstimationTax " + set, get_TrxName());
//		m_lines = null;
//		m_taxes = null;
//		if (log.isLoggable(Level.FINE)) log.fine("setProcessed - " + processed + " - Lines=" + noLine + ", Tax=" + noTax);
	}	//	setProcessed


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
			append(Msg.translate(getCtx(),"GrandTotal")).append("=").append(getGrandTotal());
		if (m_lines != null)
			sb.append(" (#").append(m_lines.length).append(")");
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
	 * 	Get Currency Precision
	 *	@return precision
	 */
	public int getPrecision()
	{
		return MCurrency.getStdPrecision(getCtx(), getC_Currency_ID());
	}	//	getPrecision

	/**************************************************************************
	 * 	Get Lines of Order
	 * 	@param whereClause where clause or null (starting with AND)
	 * 	@param orderClause order clause
	 * 	@return lines
	 */
	public MEstimationLine[] getLines (String whereClause, String orderClause)
	{
		//red1 - using new Query class from Teo / Victor's MDDOrder.java implementation
		StringBuilder whereClauseFinal = new StringBuilder(MEstimationLine.COLUMNNAME_JP_Estimation_ID+"=? ");
		if (!Util.isEmpty(whereClause, true))
			whereClauseFinal.append(whereClause);
		if (orderClause.length() == 0)
			orderClause = MEstimationLine.COLUMNNAME_Line;
		//
		List<MEstimationLine> list = new Query(getCtx(), I_JP_EstimationLine.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(get_ID())
										.setOrderBy(orderClause)
										.list();
		for (MEstimationLine ol : list) {
			ol.setHeaderInfo(this);
		}
		//
		return list.toArray(new MEstimationLine[list.size()]);
	}	//	getLines

	/**
	 * 	Get Lines of Order
	 * 	@param requery requery
	 * 	@param orderBy optional order by column
	 * 	@return lines
	 */
	public MEstimationLine[] getLines (boolean requery, String orderBy)
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

	/**
	 * 	Get Lines of Order.
	 * 	(used by web store)
	 * 	@return lines
	 */
	public MEstimationLine[] getLines()
	{
		return getLines(false, null);
	}	//	getLines

	/**
	 * 	Get Taxes of Order
	 *	@param requery requery
	 *	@return array of taxes
	 */
	public MEstimationTax[] getTaxes(boolean requery)
	{
		if (m_taxes != null && !requery)
			return m_taxes;
		//
		List<MEstimationTax> list = new Query(getCtx(), I_JP_EstimationTax.Table_Name, "JP_Estimation_ID=?", get_TrxName())
									.setParameters(get_ID())
									.list();
		m_taxes = list.toArray(new MEstimationTax[list.size()]);
		return m_taxes;
	}

	/**
	 * 	Before Delete
	 *	@return true of it can be deleted
	 */
	@Override
	protected boolean beforeDelete ()
	{
		if (isProcessed())
			return false;
		// automatic deletion of lines is driven by model cascade definition in dictionary - see IDEMPIERE-2060
		return true;
	}	//	beforeDelete


	@Override
	protected boolean beforeSave(boolean newRecord)
	{

		if( newRecord || is_ValueChanged("C_DocTypeTarget_ID") )
		{
			setC_DocType_ID(getC_DocTypeTarget_ID());
		}

		if( newRecord || is_ValueChanged("JP_DocTypeSO_ID") )
		{
			if(getJP_DocTypeSO_ID()==0)
			{
				setOrderType(MDocType.DOCSUBTYPESO_StandardOrder);
			}else{
				MDocType dt = MDocType.get(getCtx(), getJP_DocTypeSO_ID());
				setOrderType(dt.getDocSubTypeSO());
			}

		}

		if( (newRecord  && getC_BPartner_ID() > 0 ) || (is_ValueChanged("C_BPartner_ID") && getC_BPartner_ID() > 0) )
		{
			MBPartner bp = MBPartner.get(getCtx(), getC_BPartner_ID());

			if(Util.isEmpty(getJP_BP_Name()))
			{
				setJP_BP_Name(bp.getName2());
			}

			if(Util.isEmpty(getJP_BP_Address()))
			{
				int C_BPartner_Location_ID = getC_BPartner_Location_ID();
				if(C_BPartner_Location_ID==0)
				{
					MBPartnerLocation[] bpLocations =  bp.getLocations(false);
					for(int i = 0; i < bpLocations.length; i++)
					{
						if(bpLocations[i].isActive())
						{
							C_BPartner_Location_ID = bpLocations[i].getC_BPartner_Location_ID();
						}
					}
				}

				if(C_BPartner_Location_ID >0)
				{
					MBPartnerLocation bpLocation = new MBPartnerLocation(getCtx(),C_BPartner_Location_ID,get_TrxName());
					MLocation loc = bpLocation.getLocation(false);
					MCountry country = MCountry.get(getCtx(), loc.getC_Country_ID());
					String address = "";
					String postal = loc.getPostal();
					if(!Util.isEmpty(postal))
					{
						String postalAdd = loc.getPostal_Add();
						if(!Util.isEmpty(postalAdd))
						{
							if(country.getCountryCode().equals("JP"))
								postal = "〒" + postal + "-" + postalAdd;
							else
								postal = postal + "-" + postalAdd;

						}else{

							if(country.getCountryCode().equals("JP"))
								postal = "〒" + postal + "-0000";
						}

					}

					if(!Util.isEmpty(postal))
						address = postal + " ";

					if(!Util.isEmpty(loc.getAddress1()))
						address = address + loc.getAddress1() ;

					if(!Util.isEmpty(loc.getAddress2()))
						address = address + loc.getAddress2() ;

					if(!Util.isEmpty(address))
						setJP_BP_Address(address);

				}

			}//Address

			if(Util.isEmpty(getJP_BP_User_Name()))
			{
				MUser[] users = MUser.getOfBPartner(getCtx(), bp.getC_BPartner_ID(),get_TrxName());
				for(int i = 0; i < users.length; i++)
				{
					if(users[i].isActive())
					{
						setJP_BP_User_Name(users[i].getName());
						break;
					}
				}

			}
		}

		// IDEMPIERE-1597 Price List and Date must be not-updateable
		if (!newRecord && (is_ValueChanged(COLUMNNAME_M_PriceList_ID) || is_ValueChanged(COLUMNNAME_DateOrdered))) {
			int cnt = DB.getSQLValueEx(get_TrxName(), "SELECT COUNT(*) FROM JP_EstimationLine WHERE JP_Estimation_ID=? AND M_Product_ID>0", getJP_Estimation_ID());
			if (cnt > 0) {
				if (is_ValueChanged(COLUMNNAME_M_PriceList_ID)) {
					log.saveError("Error", Msg.getMsg(getCtx(), "CannotChangePl"));
					return false;
				}
				if (is_ValueChanged(COLUMNNAME_DateOrdered)) {
					MPriceList pList =  MPriceList.get(getCtx(), getM_PriceList_ID(), null);
					MPriceListVersion plOld = pList.getPriceListVersion((Timestamp)get_ValueOld(COLUMNNAME_DateOrdered));
					MPriceListVersion plNew = pList.getPriceListVersion((Timestamp)get_Value(COLUMNNAME_DateOrdered));
					if (plNew == null || !plNew.equals(plOld)) {
						log.saveError("Error", Msg.getMsg(getCtx(), "CannotChangeDateOrdered"));
						return false;
					}
				}
			}
		}

		return true;
	}

	@Override
	protected boolean afterSave(boolean newRecord, boolean success)
	{
		//Sync Lines
		if (   is_ValueChanged("AD_Org_ID")
		    || is_ValueChanged(MEstimation.COLUMNNAME_C_BPartner_ID)
		    || is_ValueChanged(MEstimation.COLUMNNAME_C_BPartner_Location_ID)
		    || is_ValueChanged(MEstimation.COLUMNNAME_DateOrdered)
		    || is_ValueChanged(MEstimation.COLUMNNAME_DatePromised)
		    || is_ValueChanged(MEstimation.COLUMNNAME_M_Warehouse_ID)
		    || is_ValueChanged(MEstimation.COLUMNNAME_M_Shipper_ID)
		    || is_ValueChanged(MEstimation.COLUMNNAME_C_Currency_ID))
		{
			MEstimationLine[] lines = getLines();
			for (MEstimationLine line : lines) {
				if (is_ValueChanged("AD_Org_ID"))
					line.setAD_Org_ID(getAD_Org_ID());
				if (is_ValueChanged(MEstimation.COLUMNNAME_C_BPartner_ID))
					line.setC_BPartner_ID(getC_BPartner_ID());
				if (is_ValueChanged(MEstimation.COLUMNNAME_C_BPartner_Location_ID))
					line.setC_BPartner_Location_ID(getC_BPartner_Location_ID());
				if (is_ValueChanged(MEstimation.COLUMNNAME_DateOrdered))
					line.setDateOrdered(getDateOrdered());
				if (is_ValueChanged(MEstimation.COLUMNNAME_DatePromised))
					line.setDatePromised(getDatePromised());
				if (is_ValueChanged(MEstimation.COLUMNNAME_M_Warehouse_ID))
					line.setM_Warehouse_ID(getM_Warehouse_ID());
				if (is_ValueChanged(MEstimation.COLUMNNAME_M_Shipper_ID))
					line.setM_Shipper_ID(getM_Shipper_ID());
				if (is_ValueChanged(MEstimation.COLUMNNAME_C_Currency_ID))
					line.setC_Currency_ID(getC_Currency_ID());
				line.saveEx();
			}
		}

		return true;
	}

	public int copyLinesFrom (MEstimation otherOrder, boolean counter, boolean copyASI)
	{
		if (isProcessed() || isPosted() || otherOrder == null)
			return 0;
		MEstimationLine[] fromLines = otherOrder.getLines(false, null);
		int count = 0;
		for (int i = 0; i < fromLines.length; i++)
		{
			MEstimationLine line = new MEstimationLine (this);
			PO.copyValues(fromLines[i], line, getAD_Client_ID(), getAD_Org_ID());
			line.setJP_Estimation_ID(getJP_Estimation_ID());
			//
			line.setQtyDelivered(Env.ZERO);
			line.setQtyInvoiced(Env.ZERO);
			line.setQtyReserved(Env.ZERO);
			line.setDateDelivered(null);
			line.setDateInvoiced(null);
			//
			line.setEstimation(this);
			line.set_ValueNoCheck ("JP_EstimationLine_ID", I_ZERO);	//	new
			//	References
			if (!copyASI)
			{
				line.setM_AttributeSetInstance_ID(0);
				line.setS_ResourceAssignment_ID(0);
			}

//			if (counter)
//				line.setRef_OrderLine_ID(fromLines[i].getC_OrderLine_ID());
//			else
//				line.setRef_OrderLine_ID(0);

			// don't copy linked lines
			line.setLink_OrderLine_ID(0);
			//	Tax
//			if (getC_BPartner_ID() != otherOrder.getC_BPartner_ID())
//				line.setTax();		//	recalculate
			//
			//
//			line.setProcessed(false);
			if (line.save(get_TrxName()))
				count++;
			//	Cross Link
//			if (counter)
//			{
//				fromLines[i].setRef_OrderLine_ID(line.getC_OrderLine_ID());
//				fromLines[i].saveEx(get_TrxName());
//			}
		}
		if (fromLines.length != count)
			log.log(Level.SEVERE, "Line difference - From=" + fromLines.length + " <> Saved=" + count);
		return count;
	}	//	copyLinesFrom


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

	boolean JP_ESTIMATION_ORDER_AMT_CHECK = false;
	boolean JP_ESTIMATION_INVOICE_AMT_CHECK = false;
	boolean JP_ESTIMATION_BILL_AMT_CHECK = false;
	boolean JP_ESTIMATION_PAYMENT_AMT_CHECK = false;
	boolean JP_ESTIMATION_RMA_AMT_CHECK = false;

	/**
	 *
	 * @return null of String(if Error)
	 */
	public String amountConsistencyCheck()
	{
		if(getRef_Order_ID() > 0)
			JP_ESTIMATION_ORDER_AMT_CHECK = MSysConfig.getBooleanValue("JP_ESTIMATION_ORDER_AMT_CHECK", false, getAD_Client_ID(), getAD_Org_ID());

		if(getC_Invoice_ID() > 0)
			JP_ESTIMATION_INVOICE_AMT_CHECK = MSysConfig.getBooleanValue("JP_ESTIMATION_INVOICE_AMT_CHECK", false, getAD_Client_ID(), getAD_Org_ID());

		if(getJP_Bill_ID() > 0)
			JP_ESTIMATION_BILL_AMT_CHECK = MSysConfig.getBooleanValue("JP_ESTIMATION_BILL_AMT_CHECK", false, getAD_Client_ID(), getAD_Org_ID());

		if(getC_Payment_ID() > 0)
			JP_ESTIMATION_PAYMENT_AMT_CHECK = MSysConfig.getBooleanValue("JP_ESTIMATION_PAYMENT_AMT_CHECK", false, getAD_Client_ID(), getAD_Org_ID());

		if(getM_RMA_ID() > 0)
			JP_ESTIMATION_RMA_AMT_CHECK = MSysConfig.getBooleanValue("JP_ESTIMATION_RMA_AMT_CHECK", false, getAD_Client_ID(), getAD_Org_ID());



		if(JP_ESTIMATION_ORDER_AMT_CHECK)
		{
			MOrder order = new MOrder( getCtx(), getRef_Order_ID(), get_TrxName() );
			if(order.getGrandTotal().compareTo(getGrandTotal()) != 0)
			{
				return Msg.getMsg(getCtx(), "JP_EstimationAmountConsistencyError") + " : " + order.getDocumentInfo();
			}
		}

		if(JP_ESTIMATION_INVOICE_AMT_CHECK)
		{
			MInvoice invoice = new MInvoice( getCtx(), getC_Invoice_ID(), get_TrxName() );
			if(invoice.getGrandTotal().compareTo(getGrandTotal()) != 0)
			{
				return Msg.getMsg(getCtx(), "JP_EstimationAmountConsistencyError") + " : " + invoice.getDocumentInfo();
			}
		}

		if(JP_ESTIMATION_BILL_AMT_CHECK)
		{
			MBill bill = new MBill( getCtx(), getJP_Bill_ID(), get_TrxName() );
			if(bill.getGrandTotal().compareTo(getGrandTotal()) != 0)
			{
				return Msg.getMsg(getCtx(), "JP_EstimationAmountConsistencyError") + " : " + bill.getDocumentInfo();
			}
		}

		if(JP_ESTIMATION_PAYMENT_AMT_CHECK)
		{
			MPayment payment = new MPayment( getCtx(), getC_Payment_ID(), get_TrxName() );
			if(payment.getPayAmt().compareTo(getGrandTotal()) != 0)
			{
				return Msg.getMsg(getCtx(), "JP_EstimationAmountConsistencyError") + " : " + payment.getDocumentInfo();
			}
		}

		if(JP_ESTIMATION_RMA_AMT_CHECK)
		{
			MRMA rma = new MRMA( getCtx(), getM_RMA_ID(), get_TrxName() );
			if(rma.getAmt().compareTo(getTotalLines()) != 0)
			{
				return Msg.getMsg(getCtx(), "JP_EstimationAmountConsistencyError") + " : " + rma.getDocumentInfo();
			}
		}

		return "";

	}

}	//MEstimation
