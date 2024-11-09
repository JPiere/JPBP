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
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Properties;

import org.adempiere.util.IProcessUI;
import org.compiere.model.MColumn;
import org.compiere.model.MInOut;
import org.compiere.model.MInOutLine;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MRefList;
import org.compiere.model.MTable;
import org.compiere.model.PO;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.MContract;
import jpiere.base.plugin.org.adempiere.model.MContractCalender;
import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractLine;
import jpiere.base.plugin.org.adempiere.model.MContractLog;
import jpiere.base.plugin.org.adempiere.model.MContractLogDetail;
import jpiere.base.plugin.org.adempiere.model.MContractPSInOutLine;
import jpiere.base.plugin.org.adempiere.model.MContractPSInvoiceLine;
import jpiere.base.plugin.org.adempiere.model.MContractPSLine;
import jpiere.base.plugin.org.adempiere.model.MContractProcPeriod;
import jpiere.base.plugin.org.adempiere.model.MContractProcSchedule;
import jpiere.base.plugin.org.adempiere.model.MContractProcess;


/** JPIERE-0363
*
* @author Hideaki Hagiwara
*
*/
public abstract class AbstractContractProcess extends SvrProcess
{
	protected int Record_ID = 0;
	protected MContractContent m_ContractContent = null;
	protected MContract m_Contract = null;
	protected MContractLog m_ContractLog = null;

	protected String p_JP_ContractProcessUnit = null;
	protected int p_JP_ContractCalender_ID = 0;
	protected int p_JP_ContractProcPeriodG_ID = 0;
	protected int p_JP_ContractProcPeriod_ID = 0;
	protected String p_JP_ContractProcessValue = null;
	protected Timestamp p_DateAcct = null;
	protected Timestamp p_DateDoc = null;
	protected Timestamp p_DateOrdered = null;
	protected Timestamp p_DatePromised = null;
	protected Timestamp p_DateInvoiced = null;
	protected String p_DocAction = null;
	protected int p_AD_Org_ID = 0;
	protected int p_JP_ContractCategory_ID = 0;
	protected int p_C_DocType_ID = 0;
	protected String p_DocBaseType = null;
	protected boolean p_IsCreateBaseDocJP = false;
	protected boolean p_IsRecordCommitJP = false;
	protected String p_JP_ContractProcessMethod = null;
	protected String p_JP_ContractProcessTraceLevel = MContractLog.JP_CONTRACTPROCESSTRACELEVEL_Warning;
	protected String p_JP_IndirectContractProcType = JP_IndirectContractProcType_AllValidContractProcessSchedule ;
	protected String p_JP_ContractProcessType = null;


	protected int p_JP_ContractProcess_ID = 0; //use to create derivative Doc

	protected IProcessUI processUI = null;

	/** JP_ContractProcessUnit */
	public static final String JP_ContractProcessUnit_ContractProcessPeriod  = "CPP";
	public static final String JP_ContractProcessUnit_ContractProcessValueofContractProcessPeriod  = "CPV";
	public static final String JP_ContractProcessUnit_AccountDate  = "DAT";
	public static final String JP_ContractProcessUnit_DocumentDate  = "DDT";
	public static final String JP_ContractProcessUnit_ContractProcessPeriodGroup  = "GPP";
	public static final String JP_ContractProcessUnit_ContractProcessValueofContractProcessPeriodGroup  = "GPV";
	public static final String JP_ContractProcessUnit_PerContractContent  = "PCC";

	public static final String JP_IndirectContractProcType_AllValidContractProcessSchedule = "AVC";
	public static final String JP_IndirectContractProcType_ValidContractProcessScheduleInValidContractDoc = "VCI";

	public static final String JP_ContractProcessType_CreateDocument = "CD";
	public static final String JP_ContractProcessType_Report = "RE";
	public static final String JP_ContractProcessType_AutoRenewContract = "AR";
	public static final String JP_ContractProcessType_ContractStatusUpdate = "CS";

	protected String p_JP_ContractTabLevel = null;
	protected  static final String JP_ContractTabLevel_Document  = "CD";
	protected  static final String JP_ContractTabLevel_Content  = "CC";

	@Override
	protected void prepare()
	{
		Record_ID = getRecord_ID();

		processUI = Env.getProcessUI(getCtx());

		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();

			if (para[i].getParameter() == null)
			{
				;

			}else if (name.equals("JP_ContractProcessUnit")){

				p_JP_ContractProcessUnit = para[i].getParameterAsString();

			}else if (name.equals("JP_ContractCalender_ID")){

				p_JP_ContractCalender_ID = para[i].getParameterAsInt();

			}else if (name.equals("JP_ContractProcPeriodG_ID")){

				p_JP_ContractProcPeriodG_ID = para[i].getParameterAsInt();

			}else if (name.equals("JP_ContractProcPeriod_ID")){

				p_JP_ContractProcPeriod_ID = para[i].getParameterAsInt();

			}else if (name.equals("JP_ContractProcessValue")){

				p_JP_ContractProcessValue = para[i].getParameterAsString();

			}else if (name.equals("DateAcct")){

				p_DateAcct = para[i].getParameterAsTimestamp();

			}else if (name.equals("DateDoc")){

				p_DateDoc = para[i].getParameterAsTimestamp();

			}else if (name.equals("DatePromised")){

				p_DatePromised = para[i].getParameterAsTimestamp();

			}else if (name.equals("DateOrdered")){

				p_DateOrdered = para[i].getParameterAsTimestamp();

			}else if (name.equals("DateInvoiced")){

				p_DateInvoiced = para[i].getParameterAsTimestamp();

			}else if (name.equals("DocAction")){

				p_DocAction = para[i].getParameterAsString();

			}else if (name.equals("AD_Org_ID")){

				p_AD_Org_ID = para[i].getParameterAsInt();

			}else if (name.equals("JP_ContractCategory_ID")){

				p_JP_ContractCategory_ID = para[i].getParameterAsInt();

			}else if (name.equals("C_DocType_ID")){

				p_C_DocType_ID = para[i].getParameterAsInt();

			}else if (name.equals("DocBaseType")){

				p_DocBaseType = para[i].getParameterAsString();
			}else if (name.equals("IsCreateBaseDocJP")){

				p_IsCreateBaseDocJP = para[i].getParameterAsBoolean();

			}else if (name.equals("IsRecordCommitJP")){

				p_IsRecordCommitJP = para[i].getParameterAsBoolean();

			}else if (name.equals("JP_ContractProcessTraceLevel")){

				p_JP_ContractProcessTraceLevel = para[i].getParameterAsString();

			}else if (name.equals("JP_ContractLog")){

				m_ContractLog = (MContractLog)para[i].getParameter();

			}else if (name.equals("JP_Contract")){

				m_Contract = (MContract)para[i].getParameter();

			}else if (name.equals("JP_ContractContent") && Record_ID == 0){

				m_ContractContent = (MContractContent)para[i].getParameter();

			}else if (name.equals("JP_ContractProcess_ID")){

				p_JP_ContractProcess_ID = para[i].getParameterAsInt();

			}else if (name.equals("JP_ContractProcessMethod")){

				p_JP_ContractProcessMethod = para[i].getParameterAsString();

			}else if (name.equals("JP_IndirectContractProcType")) {

				p_JP_IndirectContractProcType = para[i].getParameterAsString();

			}else if (name.equals("JP_ContractProcessType")) {

				p_JP_ContractProcessType = para[i].getParameterAsString();

			}else if (name.equals("JP_ContractTabLevel")){

				p_JP_ContractTabLevel = para[i].getParameterAsString();

			}else{
//				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}//if

		}//for

		if(Record_ID > 0)
		{
			if(Util.isEmpty(p_JP_ContractTabLevel))
			{
				m_ContractContent = new MContractContent(getCtx(), Record_ID, get_TrxName());
				m_Contract = m_ContractContent.getParent();

			}else if(p_JP_ContractTabLevel.equals(JP_ContractTabLevel_Document)) {

				m_Contract = new MContract(getCtx(), Record_ID, get_TrxName());

			}else if(p_JP_ContractTabLevel.equals(JP_ContractTabLevel_Content)){

				m_ContractContent = new MContractContent(getCtx(), Record_ID, get_TrxName());
				m_Contract = m_ContractContent.getParent();
			}
		}

	}

	@Override
	protected String doIt() throws Exception
	{
		return null;
	}


	protected Timestamp getDateDoc()
	{
		if(p_DateDoc !=null)
			return p_DateDoc;

		if(p_JP_ContractProcPeriod_ID > 0)
			return MContractProcPeriod.get(getCtx(), p_JP_ContractProcPeriod_ID).getDateDoc();


		return m_ContractContent.getDateDoc();

	}


	protected Timestamp getDateAcct()
	{
		if(p_DateAcct !=null)
			return p_DateAcct;

		if(p_JP_ContractProcPeriod_ID > 0)
			return MContractProcPeriod.get(getCtx(), p_JP_ContractProcPeriod_ID).getDateAcct();


		return m_ContractContent.getDateAcct();

	}

	protected Timestamp getDateOrdered()
	{
		if(p_DateOrdered != null)
			return p_DateOrdered;

		if(m_ContractContent.getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_SpotContract)
				&& p_JP_ContractProcessUnit.equals("PCC")
				&& ( m_ContractContent.getDocBaseType().equals("SOO") ||  m_ContractContent.getDocBaseType().equals("POO")) )
		{

			return m_ContractContent.getDateOrdered();

		}


		if(p_DateDoc != null)
			return p_DateDoc;

		if(p_JP_ContractProcPeriod_ID > 0)
			return MContractProcPeriod.get(getCtx(), p_JP_ContractProcPeriod_ID).getDateDoc();


		return m_ContractContent.getDateOrdered();

	}

	protected Timestamp getDateInvoiced()
	{
		if(p_DateInvoiced != null)
			return p_DateInvoiced;

		if(m_ContractContent.getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_SpotContract)
				&& p_JP_ContractProcessUnit.equals("PCC")
				&& ( m_ContractContent.getDocBaseType().equals("API") ||  m_ContractContent.getDocBaseType().equals("ARI")) )
		{

			return m_ContractContent.getDateInvoiced();

		}

		if(p_DateDoc != null)
			return p_DateDoc;

		if(p_JP_ContractProcPeriod_ID > 0)
			return MContractProcPeriod.get(getCtx(), p_JP_ContractProcPeriod_ID).getDateDoc();

		return  m_ContractContent.getDateInvoiced();

	}

	protected Timestamp getOrderHeaderDatePromised(Timestamp dateFrom)
	{
		if(p_DatePromised != null)
			return p_DatePromised;

//		if(m_ContractContent.getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_SpotContract)
//				&& p_JP_ContractProcessUnit.equals("PCC")
//				&& ( m_ContractContent.getDocBaseType().equals("SOO") ||  m_ContractContent.getDocBaseType().equals("POO")) )
//		{
//
//			return m_ContractContent.getDatePromised();
//
//		}

		if(dateFrom != null)
		{
			LocalDateTime dateAcctLocal = dateFrom.toLocalDateTime();
			dateAcctLocal = dateAcctLocal.plusDays(m_ContractContent.getDeliveryTime_Promised());
			return Timestamp.valueOf(dateAcctLocal) ;
		}

		if(getDateAcct() != null )
		{
			LocalDateTime dateAcctLocal = getDateAcct().toLocalDateTime();
			dateAcctLocal = dateAcctLocal.plusDays(m_ContractContent.getDeliveryTime_Promised());
			return Timestamp.valueOf(dateAcctLocal) ;
		}

		if(getDateDoc() != null )
		{
			LocalDateTime dateAcctLocal = getDateDoc().toLocalDateTime();
			dateAcctLocal = dateAcctLocal.plusDays(m_ContractContent.getDeliveryTime_Promised());
			return Timestamp.valueOf(dateAcctLocal) ;
		}

		return null;
	}

	protected Timestamp getOrderLineDatePromised(MContractLine m_Contractline)
	{

		if(m_ContractContent.getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_SpotContract)
				&& p_JP_ContractProcessUnit.equals("PCC")
				&& ( m_ContractContent.getDocBaseType().equals("SOO") ||  m_ContractContent.getDocBaseType().equals("POO")) )
		{
			if(m_Contractline != null && m_Contractline.getDatePromised() != null)
				return m_Contractline.getDatePromised();
		}

		if(m_Contractline != null)
		{
			LocalDateTime dateAcctLocal = getDateAcct().toLocalDateTime();
			dateAcctLocal = dateAcctLocal.plusDays(m_Contractline.getDeliveryTime_Promised());
			return Timestamp.valueOf(dateAcctLocal) ;

		}else{

			if(p_DatePromised != null)
				return p_DatePromised;
		}

		return null;
	}

	protected String getDocAction()
	{
		if(!Util.isEmpty(p_DocAction))
			return p_DocAction;

		if(m_ContractContent.getJP_ContractProcess_ID () > 0 )
		{
			MContractProcess contractProcess = MContractProcess.get(getCtx(), m_ContractContent.getJP_ContractProcess_ID ());
			if(!Util.isEmpty(contractProcess.getDocAction()))
			{
				return contractProcess.getDocAction();
			}
		}

		return null;
	}


	protected int getJP_ContractProctPeriod_ID()
	{
		if(p_JP_ContractProcPeriod_ID > 0)
			return p_JP_ContractProcPeriod_ID;

		if(p_JP_ContractCalender_ID > 0)
		{
			MContractCalender cal = MContractCalender.get(getCtx(), p_JP_ContractCalender_ID);
			MContractProcPeriod period = cal.getContractProcessPeriod(getCtx(), getDateAcct());
			return period.getJP_ContractProcPeriod_ID();
		}


		if(p_DocBaseType != null && (p_DocBaseType.equals("SOO") || p_DocBaseType.equals("POO")) )
		{
			if(m_ContractContent != null && m_ContractContent.getJP_ContractCalender_ID() > 0)
			{
				MContractCalender cal = MContractCalender.get(getCtx(), m_ContractContent.getJP_ContractCalender_ID() );
				MContractProcPeriod period = cal.getContractProcessPeriod(getCtx(), getDateAcct());
				return period.getJP_ContractProcPeriod_ID();
			}
		}

		return 0;
	}

	protected MContractProcPeriod getBaseDocContractProcPeriodFromDerivativeDocContractProcPeriod(int Derivative_ContractProcPeriod_ID)
	{
		MContractCalender calender = MContractCalender.get(getCtx(), m_ContractContent.getJP_ContractCalender_ID());
		if(calender == null)
			return null;

		MContractProcPeriod  derivativeDocContractProcPeriod = MContractProcPeriod.get(getCtx(), Derivative_ContractProcPeriod_ID);
		if(derivativeDocContractProcPeriod == null)
			return null;


		return calender.getContractProcessPeriod(getCtx(), derivativeDocContractProcPeriod.getStartDate(), derivativeDocContractProcPeriod.getEndDate());
	}

	protected int getJP_ContractProcess_ID()
	{
		return p_JP_ContractProcess_ID;
	}

	protected void updateContractProcStatus()
	{
		if(p_IsCreateBaseDocJP)
		{
			if(m_ContractContent.getJP_ContractProcStatus().equals(MContractContent.JP_CONTRACTPROCSTATUS_Unprocessed))
			{
				m_ContractContent.setJP_ContractProcStatus(MContractContent.JP_CONTRACTPROCSTATUS_InProgress);
				m_ContractContent.saveEx(get_TrxName());
			}
		}
	}


	private int Reference_ContractLogMsg = 0;

	protected void createContractLogDetail(String ContractLogMsg, MContractLine ContractLine, PO po, String descriptionMsg)
	{
		/** A **/
		if(ContractLogMsg.equals(MContractLogDetail.JP_CONTRACTLOGMSG_CreatedDocument))//A1
		{
			createContractLogDetail(ContractLogMsg, ContractLine, po, descriptionMsg, MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_Information);

		}else if(ContractLogMsg.equals(MContractLogDetail.JP_CONTRACTLOGMSG_CreatedDocumentLine)) {//A2;

			createContractLogDetail(ContractLogMsg, ContractLine, po, descriptionMsg, MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_Information);


		/** B **/
		}else if(ContractLogMsg.equals(MContractLogDetail.JP_CONTRACTLOGMSG_SkippedContractProcessForOverlapContractProcessPeriod)) {//B1;

			createContractLogDetail(ContractLogMsg, ContractLine, po, descriptionMsg, MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_Information);

		}else if(ContractLogMsg.equals(MContractLogDetail.JP_CONTRACTLOGMSG_AllContractContentLineWasSkipped)) {//B";

			createContractLogDetail(ContractLogMsg, ContractLine, po, descriptionMsg, MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_Information);

		}else if(ContractLogMsg.equals(MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForCreateDocLineIsFalse)) {//B3;

			createContractLogDetail(ContractLogMsg, ContractLine, po, descriptionMsg, MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_Information);

		}else if(ContractLogMsg.equals(MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForOutsideOfTheDerivativeDocPeriod )) {//B4;

			createContractLogDetail(ContractLogMsg, ContractLine, po, descriptionMsg, MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_Information);

		}else if(ContractLogMsg.equals(MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForOutsideOfTheBaseDocLinePeriod )) {//B5;

			createContractLogDetail(ContractLogMsg, ContractLine, po, descriptionMsg, MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_Information);

		}else if(ContractLogMsg.equals(MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForCreateDerivativeDocManually )) {//B6;

			createContractLogDetail(ContractLogMsg, ContractLine, po, descriptionMsg, MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_Information);

		}else if(ContractLogMsg.equals(MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForDocumentStatusOfOrderIsNotCompleted )) {//B7;

			createContractLogDetail(ContractLogMsg, ContractLine, po, descriptionMsg, MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_ToBeConfirmed);

		}else if(ContractLogMsg.equals(MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForQtyOfContractLineIsZero )) {//B8;

			createContractLogDetail(ContractLogMsg, ContractLine, po, descriptionMsg, MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_Information);

		}else if(ContractLogMsg.equals(MContractLogDetail.JP_CONTRACTLOGMSG_Skipped )) {//B9;

			createContractLogDetail(ContractLogMsg, ContractLine, po, descriptionMsg, MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_Information);

		}else if(ContractLogMsg.equals(	MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForNotPeriodContract)) {//BA

			createContractLogDetail(ContractLogMsg, ContractLine, po, descriptionMsg, MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_Warning);

		}else if(ContractLogMsg.equals(	MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForDifferentContractProcess)) {//BB

			createContractLogDetail(ContractLogMsg, ContractLine, po, descriptionMsg, MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_Information);

		}else if(ContractLogMsg.equals(	MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForDifferentContractCalender)) {//BC

			createContractLogDetail(ContractLogMsg, ContractLine, po, descriptionMsg, MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_Information);

		}else if(ContractLogMsg.equals(	MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForBaseDocIsNotCreated)) { //BD

			createContractLogDetail(ContractLogMsg, ContractLine, po, descriptionMsg, MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_Information);

		}else if(ContractLogMsg.equals(	MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForBaseDocIsCreated)) { //BE";

			createContractLogDetail(ContractLogMsg, ContractLine, po, descriptionMsg, MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_Information);

		}else if(ContractLogMsg.equals(	MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForDocumentStatusIsNotCompleted)) { //BF";

			createContractLogDetail(ContractLogMsg, ContractLine, po, descriptionMsg, MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_Information);

		}else if(ContractLogMsg.equals(	MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForNoContractShipReceiptSchedule)) { //BH";

			createContractLogDetail(ContractLogMsg, ContractLine, po, descriptionMsg, MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_Information);

		}else if(ContractLogMsg.equals(	MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForNoContractProcessScheduleLines)) { //BG";

			createContractLogDetail(ContractLogMsg, ContractLine, po, descriptionMsg, MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_Information);

		}else if(ContractLogMsg.equals(	MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForNoContractInvoiceSchedule)) { //BI";

			createContractLogDetail(ContractLogMsg, ContractLine, po, descriptionMsg, MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_Information);

		/** C **/
		}else if(ContractLogMsg.equals(MContractLogDetail.JP_CONTRACTLOGMSG_CouldNotCreateInvoiceForInvoicedPartly )) {//C1;

			createContractLogDetail(ContractLogMsg, ContractLine, po, descriptionMsg, MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_ToBeConfirmed);

		}else if(ContractLogMsg.equals(MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForQtyToDeliver )) {//C2;

			createContractLogDetail(ContractLogMsg, ContractLine, po, descriptionMsg, MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_ToBeConfirmed);

		}else if(ContractLogMsg.equals(MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForQtyToRecognized )) {//C3;

			createContractLogDetail(ContractLogMsg, ContractLine, po, descriptionMsg, MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_ToBeConfirmed);

		/** S **/
		}else if(ContractLogMsg.equals(MContractLogDetail.JP_CONTRACTLOGMSG_ContractStatusUpdated)) {//S1;

			createContractLogDetail(ContractLogMsg, ContractLine, po, descriptionMsg, MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_ToBeConfirmed);

		}else if(ContractLogMsg.equals(MContractLogDetail.JP_CONTRACTLOGMSG_ContractProcessStatusUpdated )) {//S2;

			createContractLogDetail(ContractLogMsg, ContractLine, po, descriptionMsg, MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_ToBeConfirmed);

		}else if(ContractLogMsg.equals(MContractLogDetail.JP_CONTRACTLOGMSG_AutomaticUpdatedOfTheContract )) {//S3;

			createContractLogDetail(ContractLogMsg, ContractLine, po, descriptionMsg, MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_ToBeConfirmed);

		}else if(ContractLogMsg.equals(MContractLogDetail.JP_CONTRACTLOGMSG_ExtendContractProcessDateOfContractContent )) {//S4;

			createContractLogDetail(ContractLogMsg, ContractLine, po, descriptionMsg, MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_ToBeConfirmed);

		}else if(ContractLogMsg.equals(MContractLogDetail.JP_CONTRACTLOGMSG_RenewTheContractContent )) {//S5;

			createContractLogDetail(ContractLogMsg, ContractLine, po, descriptionMsg, MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_ToBeConfirmed);

		/** W **/
		}else if(ContractLogMsg.equals(MContractLogDetail.JP_CONTRACTLOGMSG_NotFoundLocator)) {//W1;

			createContractLogDetail(ContractLogMsg, ContractLine, po, descriptionMsg, MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_Warning);

		}else if(ContractLogMsg.equals(MContractLogDetail.JP_CONTRACTLOGMSG_OverOrderedQuantity )) {//W2;

			createContractLogDetail(ContractLogMsg, ContractLine, po, descriptionMsg, MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_Warning);

		}else if(ContractLogMsg.equals(MContractLogDetail.JP_CONTRACTLOGMSG_Warning )) {//W9;

			createContractLogDetail(ContractLogMsg, ContractLine, po, descriptionMsg, MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_Warning);

		}else if(ContractLogMsg.equals(MContractLogDetail.JP_CONTRACTLOGMSG_SaveError )) {//Z1;

			createContractLogDetail(ContractLogMsg, ContractLine, po, descriptionMsg, MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_Error);

		}else if(ContractLogMsg.equals(MContractLogDetail.JP_CONTRACTLOGMSG_DocumentActionError )) {//Z2;

			createContractLogDetail(ContractLogMsg, ContractLine, po, descriptionMsg, MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_Error);

		}else if(ContractLogMsg.equals(MContractLogDetail.JP_CONTRACTLOGMSG_UnexpectedError)) {//ZZ;

			createContractLogDetail(ContractLogMsg, ContractLine, po, descriptionMsg, MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_Error);

		}else {

			createContractLogDetail(ContractLogMsg, ContractLine, po, descriptionMsg, MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_Information);

		}

	}

	protected void createContractLogDetail(String ContractLogMsg, MContractLine ContractLine, PO po, String descriptionMsg, String TraceLevel)
	{
		//No Log
		if(p_JP_ContractProcessTraceLevel.equals(MContractLog.JP_CONTRACTPROCESSTRACELEVEL_NoLog))
		{
			if(Reference_ContractLogMsg == 0)
			{
				MTable JP_ContractLogDetail = MTable.get(getCtx(), MContractLogDetail.Table_Name);
				MColumn[] columns = JP_ContractLogDetail.getColumns(false);
				for(int i = 0; i < columns.length; i++)
				{
					if(columns[i].getColumnName().equals(MContractLogDetail.COLUMNNAME_JP_ContractLogMsg))
					{
						int AD_Reference_Value_ID = columns[i].getAD_Reference_Value_ID();
						Reference_ContractLogMsg = AD_Reference_Value_ID;
						break;
					}
				}
			}

			String logMsg = MRefList.getListName(getCtx(), Reference_ContractLogMsg, ContractLogMsg);
			if(po != null)
			{
				if(po instanceof DocAction)
				{
					DocAction doc = (DocAction)po;
					addBufferLog(0, null, null, logMsg + " ---> " + Msg.getMsg(getCtx(), "DocumentNo") +" : "+ doc.getDocumentNo(), po.get_Table_ID(), po.get_ID());

				}else{

					addBufferLog(0, null, null, logMsg , po.get_Table_ID(), po.get_ID());
				}

			}else{

				if(Util.isEmpty(descriptionMsg))
					addLog(logMsg + " - " + ContractLine.toString());
				else
					addLog(logMsg + " - " + descriptionMsg);
			}

			return ;

		}//NoLog

		if(TraceLevel == null )
			TraceLevel = MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_Information;

		/** Count up of counter */
		if(ContractLogMsg.equals(MContractLogDetail.JP_CONTRACTLOGMSG_CreatedDocument)) //A1
		{
			m_ContractLog.createDocNum++;
			DocAction doc = (DocAction)po;
			addBufferLog(0, null, null, Msg.getMsg(getCtx(), "DocumentNo") +" : "+ doc.getDocumentNo(), po.get_Table_ID(), po.get_ID());

			if(processUI != null)
				processUI.statusUpdate(Msg.getMsg(getCtx(), "JP_CreateDocNum") + " : " + (m_ContractLog.createDocNum));

		}else if(ContractLogMsg.equals(MContractLogDetail.JP_CONTRACTLOGMSG_Skipped)//B9
				|| ContractLogMsg.equals(MContractLogDetail.JP_CONTRACTLOGMSG_SkippedContractProcessForOverlapContractProcessPeriod)//B1
				|| ContractLogMsg.equals(MContractLogDetail.JP_CONTRACTLOGMSG_AllContractContentLineWasSkipped)//B2
				|| ContractLogMsg.equals(MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForCreateDocLineIsFalse)//B3
				|| ContractLogMsg.equals(MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForOutsideOfTheDerivativeDocPeriod)//B4
				|| ContractLogMsg.equals(MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForOutsideOfTheBaseDocLinePeriod)//B5
				|| ContractLogMsg.equals(MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForCreateDerivativeDocManually)//B6
				|| ContractLogMsg.equals(MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForDocumentStatusOfOrderIsNotCompleted)//B7
				|| ContractLogMsg.equals(MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForQtyOfContractLineIsZero)//B8
				|| ContractLogMsg.equals(MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForNotPeriodContract)//BA
				|| ContractLogMsg.equals(MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForDifferentContractProcess)//BB
				|| ContractLogMsg.equals(MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForDifferentContractCalender)//BC
				|| ContractLogMsg.equals(MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForBaseDocIsNotCreated)//BD
				|| ContractLogMsg.equals(MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForBaseDocIsCreated)//BE
				|| ContractLogMsg.equals(MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForDocumentStatusIsNotCompleted)//BF
				|| ContractLogMsg.equals(MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForNoContractProcessScheduleLines)//BG
				|| ContractLogMsg.equals(MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForNoContractShipReceiptSchedule)//BH
				|| ContractLogMsg.equals(MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForNoContractInvoiceSchedule)//BI
				|| ContractLogMsg.equals(MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForQtyToDeliver)//C2
				|| ContractLogMsg.equals(MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForQtyToRecognized)//C3

				) {

			if(ContractLine == null)
			{
				m_ContractLog.skipContractContentNum++;
			}else{
				m_ContractLog.skipContractLineNum++;
			}


		}


		if(TraceLevel.equals(MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_ToBeConfirmed) ) {

			m_ContractLog.confirmNum++;

		}else if(TraceLevel.equals(MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_Warning)){

			m_ContractLog.warnNum++;

		}else if(TraceLevel.equals(MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_Error)){

			m_ContractLog.errorNum++;

		}


		/** Check traceLevel */
		if(p_JP_ContractProcessTraceLevel.equals(MContractLog.JP_CONTRACTPROCESSTRACELEVEL_Information))
		{
			;//Nothing to do. All create contract log.

		}else if(p_JP_ContractProcessTraceLevel.equals(MContractLog.JP_CONTRACTPROCESSTRACELEVEL_ToBeConfirmed)){

			if(TraceLevel.equals(MContractLog.JP_CONTRACTPROCESSTRACELEVEL_Information))
				return ;

		}else if(p_JP_ContractProcessTraceLevel.equals(MContractLog.JP_CONTRACTPROCESSTRACELEVEL_Warning)){

			if(TraceLevel.equals(MContractLog.JP_CONTRACTPROCESSTRACELEVEL_Information)
					|| TraceLevel.equals(MContractLog.JP_CONTRACTPROCESSTRACELEVEL_ToBeConfirmed))
				return ;

		}else if(p_JP_ContractProcessTraceLevel.equals(MContractLog.JP_CONTRACTPROCESSTRACELEVEL_Error)){

			if(TraceLevel.equals(MContractLog.JP_CONTRACTPROCESSTRACELEVEL_Information)
					|| TraceLevel.equals(MContractLog.JP_CONTRACTPROCESSTRACELEVEL_ToBeConfirmed)
					|| TraceLevel.equals(MContractLog.JP_CONTRACTPROCESSTRACELEVEL_Warning))
				return ;

		}


		/** Create contract Log Detail */
		MContractLogDetail logDetail = new MContractLogDetail(getCtx(), 0, m_ContractLog.get_TrxName());
		logDetail.setJP_ContractLog_ID(m_ContractLog.getJP_ContractLog_ID());
		logDetail.setJP_ContractLogMsg(ContractLogMsg);
		if(descriptionMsg != null)
			logDetail.setDescription(descriptionMsg);

		//Set Contract Info
		if(m_ContractContent != null)
		{
			logDetail.setJP_Contract_ID(m_ContractContent.getJP_Contract_ID());
			logDetail.setJP_ContractContent_ID(m_ContractContent.getJP_ContractContent_ID());

		}else if(m_Contract != null) {

			logDetail.setJP_Contract_ID(m_Contract.getJP_Contract_ID());
		}

		if(ContractLine != null)
			logDetail.setJP_ContractLine_ID(ContractLine.getJP_ContractLine_ID());

		//Set Process Info
		logDetail.setJP_ContractProcPeriod_ID(getJP_ContractProctPeriod_ID());
		logDetail.setJP_ContractProcess_ID(getJP_ContractProcess_ID());
		logDetail.setJP_ContractProcessTraceLevel(TraceLevel);

		//Set Reference Info
		if(po != null)
		{
			logDetail.set_ValueNoCheck("AD_Table_ID", po.get_Table_ID());
			logDetail.set_ValueNoCheck("Record_ID", po.get_ID());

		}else{

			logDetail.saveEx(m_ContractLog.get_TrxName());
			return ;
		}

		if(po.get_TableName().equals(MOrder.Table_Name))
		{
			MOrder order = (MOrder)po;
			logDetail.setC_Order_ID(order.getC_Order_ID());

		}else if(po.get_TableName().equals(MOrderLine.Table_Name)){

			MOrderLine orderLine = (MOrderLine)po;
			logDetail.setC_Order_ID(orderLine.getC_Order_ID());
			logDetail.setC_OrderLine_ID(orderLine.getC_OrderLine_ID());

		}else if(po.get_TableName().equals(MInOut.Table_Name)){

			MInOut inout = (MInOut)po;
			logDetail.setC_Order_ID(inout.getC_Order_ID());
			logDetail.setM_InOut_ID(inout.getM_InOut_ID());

		}else if(po.get_TableName().equals(MInOutLine.Table_Name)){

			MInOutLine ioLine = (MInOutLine)po;
			logDetail.setC_OrderLine_ID(ioLine.getC_OrderLine_ID());
			logDetail.setM_InOut_ID(ioLine.getM_InOut_ID());
			logDetail.setM_InOutLine_ID(ioLine.getM_InOutLine_ID());

		}else if(po.get_TableName().equals(MInvoice.Table_Name)){

			MInvoice invoice = (MInvoice)po;
			logDetail.setC_Order_ID(invoice.getC_Order_ID());
			logDetail.setC_Invoice_ID(invoice.getC_Invoice_ID());

		}else if(po.get_TableName().equals(MInvoiceLine.Table_Name)){

			MInvoiceLine invoiceLine = (MInvoiceLine)po;
			logDetail.setC_OrderLine_ID(invoiceLine.getC_OrderLine_ID());
			logDetail.setC_Invoice_ID(invoiceLine.getC_Invoice_ID());
			logDetail.setC_InvoiceLine_ID(invoiceLine.getC_Invoice_ID());

		}else if(po.get_TableName().equals(MContractProcSchedule.Table_Name)){

			MContractProcSchedule contractProcSchdule = (MContractProcSchedule)po;
			logDetail.setC_Order_ID(contractProcSchdule.getC_Order_ID());
			logDetail.setC_Invoice_ID(contractProcSchdule.getC_Invoice_ID());
			logDetail.setJP_ContractProcSchedule_ID(contractProcSchdule.getJP_ContractProcSchedule_ID());

		}else if(po.get_TableName().equals(MContractPSLine.Table_Name)){

			MContractPSLine contractPSLine = (MContractPSLine)po;
			logDetail.setC_Order_ID(contractPSLine.getParent().getC_Order_ID());
			logDetail.setC_Invoice_ID(contractPSLine.getParent().getC_Invoice_ID());
			logDetail.setC_OrderLine_ID(contractPSLine.getC_OrderLine_ID());
			logDetail.setC_InvoiceLine_ID(contractPSLine.getC_InvoiceLine_ID());
			logDetail.setJP_ContractProcSchedule_ID(contractPSLine.getJP_ContractProcSchedule_ID());
			logDetail.setJP_ContractPSLine_ID(contractPSLine.getJP_ContractPSLine_ID());

		}else if(po.get_TableName().equals(MContractPSInOutLine.Table_Name)){

			MContractPSInOutLine contractPSInOutLine = (MContractPSInOutLine)po;
			logDetail.setC_Order_ID(contractPSInOutLine.getJP_ContractProcSchedule().getC_Order_ID());
			logDetail.setC_OrderLine_ID(contractPSInOutLine.getJP_ContractPSLine().getC_OrderLine_ID());
			logDetail.setM_InOutLine_ID(contractPSInOutLine.getM_InOutLine_ID());
			MInOutLine ioLine = new MInOutLine(getCtx(), contractPSInOutLine.getM_InOutLine_ID(), get_TrxName());
			logDetail.setM_InOut_ID(ioLine.getM_InOut_ID());
			logDetail.setJP_ContractProcSchedule_ID(contractPSInOutLine.getJP_ContractProcSchedule_ID());
			logDetail.setJP_ContractPSLine_ID(contractPSInOutLine.getJP_ContractPSLine_ID());

		}else if(po.get_TableName().equals(MContractPSInvoiceLine.Table_Name)){

			MContractPSInvoiceLine contractPSInvoiceLine = (MContractPSInvoiceLine)po;
			logDetail.setC_Order_ID(contractPSInvoiceLine.getJP_ContractProcSchedule().getC_Order_ID());
			logDetail.setC_OrderLine_ID(contractPSInvoiceLine.getJP_ContractPSLine().getC_OrderLine_ID());
			logDetail.setC_InvoiceLine_ID(contractPSInvoiceLine.getC_InvoiceLine_ID());
			MInvoiceLine iLine = new MInvoiceLine(getCtx(), contractPSInvoiceLine.getC_InvoiceLine_ID(), get_TrxName());
			logDetail.setC_Invoice_ID(iLine.getC_Invoice_ID());
			logDetail.setJP_ContractProcSchedule_ID(contractPSInvoiceLine.getJP_ContractProcSchedule_ID());
			logDetail.setJP_ContractPSLine_ID(contractPSInvoiceLine.getJP_ContractPSLine_ID());

		}

		logDetail.saveEx(m_ContractLog.get_TrxName());

	}//createContractLogDetail


	static public String getSkipReason_CreateBaseOrderLine(Properties ctx, MContractContent contractContent, MContractLine contractLine, int JP_ContractProcPeriod_ID, boolean isCheckOverlap, String trxName)
	{
		if(contractContent == null)
			return MContractLogDetail.JP_CONTRACTLOGMSG_UnexpectedError;

		if(contractLine == null)
			return MContractLogDetail.JP_CONTRACTLOGMSG_UnexpectedError;


		if(!contractLine.isCreateDocLineJP())
			return MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForCreateDocLineIsFalse;


		if(!contractContent.getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
			return MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForNotPeriodContract;

		//Check Overlap
		if(isCheckOverlap)
		{
			MOrderLine[] oLines = contractLine.getOrderLineByContractPeriod(ctx, JP_ContractProcPeriod_ID, trxName);
			if(oLines != null && oLines.length > 0)
				return MContractLogDetail.JP_CONTRACTLOGMSG_SkippedContractProcessForOverlapContractProcessPeriod;
		}

		//Check Base Doc Line
		if(contractLine.getJP_BaseDocLinePolicy() != null &&
				( contractContent.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_Manual)
				||  !contractContent.getOrderType().equals(MContractContent.ORDERTYPE_StandardOrder)) )
		{
			//Lump
			if(contractLine.getJP_BaseDocLinePolicy().equals(MContractLine.JP_BASEDOCLINEPOLICY_LumpOnACertainPointOfContractProcessPeriod))
			{
				MContractProcPeriod lump_ContractProcPeriod = MContractProcPeriod.get(ctx, contractLine.getJP_ProcPeriod_Lump_ID());
				if(!lump_ContractProcPeriod.isContainedBaseDocContractProcPeriod(JP_ContractProcPeriod_ID))
				{
					return MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForOutsideOfTheBaseDocLinePeriod;
				}
			}

			//Start Period
			if(contractLine.getJP_BaseDocLinePolicy().equals(MContractLine.JP_BASEDOCLINEPOLICY_FromStartContractProcessPeriod)
					||contractLine.getJP_BaseDocLinePolicy().equals(MContractLine.JP_BASEDOCLINEPOLICY_FromStartContractProcessPeriodToEnd) )
			{
				MContractProcPeriod contractLine_Period = MContractProcPeriod.get(ctx, contractLine.getJP_ProcPeriod_Start_ID());
				MContractProcPeriod process_Period = MContractProcPeriod.get(ctx, JP_ContractProcPeriod_ID);
				if(contractLine_Period.getStartDate().compareTo(process_Period.getStartDate()) <= 0 )
				{
					;//This is OK. contractLine_Period.StartDate <= process_Period.StartDate
				}else{

					return MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForOutsideOfTheBaseDocLinePeriod;

				}
			}

			//End Period
			if(contractLine.getJP_BaseDocLinePolicy().equals(MContractLine.JP_BASEDOCLINEPOLICY_ToEndContractProcessPeriod)
					||contractLine.getJP_BaseDocLinePolicy().equals(MContractLine.JP_BASEDOCLINEPOLICY_FromStartContractProcessPeriodToEnd) )
			{
				MContractProcPeriod contractLine_Period = MContractProcPeriod.get(ctx, contractLine.getJP_ProcPeriod_End_ID());
				MContractProcPeriod process_Period = MContractProcPeriod.get(ctx, JP_ContractProcPeriod_ID);
				if(contractLine_Period.getEndDate().compareTo(process_Period.getEndDate()) >= 0)
				{
					;//This is OK.  contractLine_Period.EndDate >= process_Period.EndDate
				}else{

					return MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForOutsideOfTheBaseDocLinePeriod;

				}
			}

			return null;

		}//Check Base Doc Line


		//ignore Base doc line info because carete Derivative Doc
		//Check Derivative Ship/Recipt Doc Line
		if(contractContent.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceipt) ||
				contractContent.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceiptInvoice) )
		{

			//Lump
			if(contractLine.getJP_DerivativeDocPolicy_InOut().equals(MContractLine.JP_DERIVATIVEDOCPOLICY_INOUT_LumpOnACertainPointOfContractProcessPeriod))
			{
				MContractProcPeriod lump_ContractProcPeriod = MContractProcPeriod.get(ctx, contractLine.getJP_ProcPeriod_Lump_InOut_ID());
				if(!lump_ContractProcPeriod.isContainedBaseDocContractProcPeriod(JP_ContractProcPeriod_ID))
				{
					return MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForOutsideOfTheDerivativeDocPeriod;
				}
			}

			//Start Period
			if(contractLine.getJP_DerivativeDocPolicy_InOut().equals(MContractLine.JP_DERIVATIVEDOCPOLICY_INOUT_FromStartContractProcessPeriod)
					||contractLine.getJP_DerivativeDocPolicy_InOut().equals(MContractLine.JP_DERIVATIVEDOCPOLICY_INOUT_FromStartContractProcessPeriodToEnd) )
			{
				MContractProcPeriod contractLine_Period = MContractProcPeriod.get(ctx, contractLine.getJP_ProcPeriod_Start_InOut_ID());
				MContractProcPeriod process_Period = MContractProcPeriod.get(ctx, JP_ContractProcPeriod_ID);
				if(contractLine_Period.getStartDate().compareTo(process_Period.getStartDate()) <= 0
						|| contractLine_Period.getStartDate().compareTo(process_Period.getEndDate()) <= 0)
				{
					;//This is OK. process_Period.StartDate  >=  contractLine_Period.StartDate <= process_Period.EndDate
				}else{

					return MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForOutsideOfTheBaseDocLinePeriod;

				}
			}

			//End Period
			if(contractLine.getJP_DerivativeDocPolicy_InOut().equals(MContractLine.JP_DERIVATIVEDOCPOLICY_INOUT_ToEndContractProcessPeriod)
					||contractLine.getJP_DerivativeDocPolicy_InOut().equals(MContractLine.JP_DERIVATIVEDOCPOLICY_INOUT_FromStartContractProcessPeriodToEnd) )
			{
				MContractProcPeriod contractLine_Period = MContractProcPeriod.get(ctx, contractLine.getJP_ProcPeriod_End_InOut_ID());
				MContractProcPeriod process_Period = MContractProcPeriod.get(ctx, JP_ContractProcPeriod_ID);
				if(contractLine_Period.getEndDate().compareTo(process_Period.getEndDate()) >= 0
						|| contractLine_Period.getEndDate().compareTo(process_Period.getStartDate()) >= 0)
				{
					;//This is OK.  process_Period.StartDate  <=  contractLine_Period.EndDate >= process_Period.EndDate
				}else{

					return MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForOutsideOfTheBaseDocLinePeriod;

				}
			}
		}

		//Check Derivative Invoice Doc Line
		if(contractContent.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateInvoice) ||
				contractContent.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceiptInvoice) )
		{
			//Lump
			if(contractLine.getJP_DerivativeDocPolicy_Inv().equals(MContractLine.JP_DERIVATIVEDOCPOLICY_INV_LumpOnACertainPointOfContractProcessPeriod))
			{
				MContractProcPeriod lump_ContractProcPeriod = MContractProcPeriod.get(ctx,contractLine.getJP_ProcPeriod_Lump_Inv_ID());
				if(!lump_ContractProcPeriod.isContainedBaseDocContractProcPeriod(JP_ContractProcPeriod_ID))
				{
					return MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForOutsideOfTheDerivativeDocPeriod;
				}
			}

			//Start Period
			if(contractLine.getJP_DerivativeDocPolicy_Inv().equals(MContractLine.JP_DERIVATIVEDOCPOLICY_INV_FromStartContractProcessPeriod)
					||contractLine.getJP_DerivativeDocPolicy_Inv().equals(MContractLine.JP_DERIVATIVEDOCPOLICY_INV_FromStartContractProcessPeriodToEnd) )
			{
				MContractProcPeriod contractLine_Period = MContractProcPeriod.get(ctx, contractLine.getJP_ProcPeriod_Start_Inv_ID());
				MContractProcPeriod process_Period = MContractProcPeriod.get(ctx, JP_ContractProcPeriod_ID);
				if(contractLine_Period.getStartDate().compareTo(process_Period.getStartDate()) <= 0
						|| contractLine_Period.getStartDate().compareTo(process_Period.getEndDate()) <= 0 )
				{
					;//This is OK. process_Period.StartDate  >=  contractLine_Period.StartDate <= process_Period.EndDate
				}else{

					return MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForOutsideOfTheBaseDocLinePeriod;

				}
			}

			//End Period
			if(contractLine.getJP_DerivativeDocPolicy_Inv().equals(MContractLine.JP_DERIVATIVEDOCPOLICY_INV_ToEndContractProcessPeriod)
					|| contractLine.getJP_DerivativeDocPolicy_Inv().equals(MContractLine.JP_DERIVATIVEDOCPOLICY_INV_FromStartContractProcessPeriodToEnd) )
			{
				MContractProcPeriod contractLine_Period = MContractProcPeriod.get(ctx, contractLine.getJP_ProcPeriod_End_Inv_ID());
				MContractProcPeriod process_Period = MContractProcPeriod.get(ctx, JP_ContractProcPeriod_ID);
				if(contractLine_Period.getEndDate().compareTo(process_Period.getEndDate()) >= 0
						|| contractLine_Period.getEndDate().compareTo(process_Period.getStartDate()) >= 0 )
				{
					;//This is OK.  process_Period.StartDate  <=  contractLine_Period.EndDate >= process_Period.EndDate

				}else{

					return MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForOutsideOfTheBaseDocLinePeriod;

				}
			}
		}

		return null;
	}

	static public String getSkipReason_CreateBaseInvoiceLine(Properties ctx, MContractContent contractContent, MContractLine contractLine, int JP_ContractProcPeriod_ID, boolean isCheckOverlap, String trxName)
	{
		if(contractContent == null)
			return MContractLogDetail.JP_CONTRACTLOGMSG_UnexpectedError;

		if(contractLine == null)
			return MContractLogDetail.JP_CONTRACTLOGMSG_UnexpectedError;


		if(!contractLine.isCreateDocLineJP())
			return MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForCreateDocLineIsFalse;

		if(!contractContent.getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
			return MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForNotPeriodContract;

		//Check Overlap
		if(isCheckOverlap)
		{
			MInvoiceLine[] iLines = contractLine.getInvoiceLineByContractPeriod(ctx, JP_ContractProcPeriod_ID, trxName);
			if(iLines != null && iLines.length > 0)
			{
				return MContractLogDetail.JP_CONTRACTLOGMSG_SkippedContractProcessForOverlapContractProcessPeriod;
			}
		}


		//Check Base Doc Line
		if(contractLine.getJP_BaseDocLinePolicy() != null)
		{
			//Lump
			if(contractLine.getJP_BaseDocLinePolicy().equals(MContractLine.JP_BASEDOCLINEPOLICY_LumpOnACertainPointOfContractProcessPeriod))
			{
				MContractProcPeriod lump_ContractProcPeriod = MContractProcPeriod.get(ctx, contractLine.getJP_ProcPeriod_Lump_ID());
				if(!lump_ContractProcPeriod.isContainedBaseDocContractProcPeriod(JP_ContractProcPeriod_ID))
				{
					return MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForOutsideOfTheBaseDocLinePeriod;
				}
			}

			//Start Period
			if(contractLine.getJP_BaseDocLinePolicy().equals(MContractLine.JP_BASEDOCLINEPOLICY_FromStartContractProcessPeriod)
					||contractLine.getJP_BaseDocLinePolicy().equals(MContractLine.JP_BASEDOCLINEPOLICY_FromStartContractProcessPeriodToEnd) )
			{
				MContractProcPeriod contractLine_Period = MContractProcPeriod.get(ctx, contractLine.getJP_ProcPeriod_Start_ID());
				MContractProcPeriod process_Period = MContractProcPeriod.get(ctx, JP_ContractProcPeriod_ID);
				if(contractLine_Period.getStartDate().compareTo(process_Period.getStartDate()) <= 0)
				{
					;//This is OK. contractLine_Period.StartDate <= process_Period.StartDate
				}else{

					return MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForOutsideOfTheBaseDocLinePeriod;
				}
			}

			//End Period
			if(contractLine.getJP_BaseDocLinePolicy().equals(MContractLine.JP_BASEDOCLINEPOLICY_ToEndContractProcessPeriod)
					||contractLine.getJP_BaseDocLinePolicy().equals(MContractLine.JP_BASEDOCLINEPOLICY_FromStartContractProcessPeriodToEnd) )
			{
				MContractProcPeriod contractLine_Period = MContractProcPeriod.get(ctx, contractLine.getJP_ProcPeriod_End_ID());
				MContractProcPeriod process_ContractProcPeriod = MContractProcPeriod.get(ctx, JP_ContractProcPeriod_ID);
				if(contractLine_Period.getEndDate().compareTo(process_ContractProcPeriod.getEndDate()) >= 0)
				{
					;//This is OK. contractLine_Period.EndDate >= process_Period.EndDate
				}else{

					return MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForOutsideOfTheBaseDocLinePeriod;
				}
			}

		}

		return null;
	}

	static public String getSkipReason_CreateBaseDocIndirectly(Properties ctx, MContractProcSchedule contractProcSchedule, int JP_ContractProcPeriod_ID, boolean isCheckOverlap, String trxName)
	{

		if(contractProcSchedule.isFactCreatedJP())
		{
			return MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForBaseDocIsCreated;//BE
		}

		if(!contractProcSchedule.getDocStatus().equals(DocAction.STATUS_Completed))
		{
			return MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForDocumentStatusIsNotCompleted;//BF
		}

		MContractPSLine[] contractPSLines = contractProcSchedule.getContractPSLines();
		if(contractPSLines.length <= 0)
		{
			return MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForNoContractProcessScheduleLines;//BG

		}

		//Check Overlap
		if(isCheckOverlap)
		{
			MContractContent cc = MContractContent.get(ctx, contractProcSchedule.getJP_ContractContent_ID());
			if(cc.getDocBaseType().equals(MContractContent.DOCBASETYPE_SalesOrder) || cc.getDocBaseType().equals(MContractContent.DOCBASETYPE_PurchaseOrder))
			{
				MOrder[] oLines = cc.getOrderByContractPeriod(ctx, JP_ContractProcPeriod_ID, trxName);
				if(oLines != null && oLines.length > 0)
				{
					return MContractLogDetail.JP_CONTRACTLOGMSG_SkippedContractProcessForOverlapContractProcessPeriod;
				}

			}else if(cc.getDocBaseType().equals(MContractContent.DOCBASETYPE_ARInvoice) || cc.getDocBaseType().equals(MContractContent.DOCBASETYPE_APInvoice)) {

				MInvoice[] iLines = cc.getInvoiceByContractPeriod(ctx, JP_ContractProcPeriod_ID, trxName);
				if(iLines != null && iLines.length > 0)
				{
					return MContractLogDetail.JP_CONTRACTLOGMSG_SkippedContractProcessForOverlapContractProcessPeriod;
				}
			}
		}

		return null;
	}

	static public String getSkipReason_CreateDerivativeInOutLine(Properties ctx, MContractContent contractContent, MContractLine contractLine, MOrderLine orderLine
														, int JP_ContractProcPeriod_ID,int JP_ContractProcess_ID, boolean isCheckOverlap, boolean isCheckContractProcess, String trxName)
	{
		if(contractContent == null)
			return MContractLogDetail.JP_CONTRACTLOGMSG_UnexpectedError;

		if(contractLine == null)
			return MContractLogDetail.JP_CONTRACTLOGMSG_UnexpectedError;


		//Check Contract Process
		if(isCheckContractProcess)
		{
			if(contractLine.getJP_ContractProcess_InOut_ID() != JP_ContractProcess_ID)
			{
				return MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForDifferentContractProcess;
			}
		}

		//Check Contract Calender
		MContractProcPeriod processPeriod = MContractProcPeriod.get(ctx, JP_ContractProcPeriod_ID);
		if(contractLine.getJP_ContractCalender_InOut_ID() != processPeriod.getJP_ContractCalender_ID())
		{
			return MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForDifferentContractCalender;
		}


		if(!contractLine.isCreateDocLineJP())
		{
			return MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForCreateDocLineIsFalse;
		}

		//Skip Qty ZERO
		if(contractLine.getMovementQty().compareTo(Env.ZERO) == 0)
		{
			return MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForQtyOfContractLineIsZero;
		}


		//Check Overlap
		if(isCheckOverlap)
		{
			MInOutLine[] ioLines = contractLine.getInOutLineByContractPeriod(ctx, JP_ContractProcPeriod_ID, trxName);
			if(ioLines != null && ioLines.length > 0)
			{
				return MContractLogDetail.JP_CONTRACTLOGMSG_SkippedContractProcessForOverlapContractProcessPeriod;
			}
		}

		//Check Derivative Ship/Recipt Doc Line
		if(contractContent.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceipt) ||
				contractContent.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceiptInvoice) )
		{

			//Lump
			if(contractLine.getJP_DerivativeDocPolicy_InOut().equals(MContractLine.JP_DERIVATIVEDOCPOLICY_INOUT_LumpOnACertainPointOfContractProcessPeriod))
			{
				MContractProcPeriod lump_ContractProcPeriod = MContractProcPeriod.get(ctx, contractLine.getJP_ProcPeriod_Lump_InOut_ID());
				if(!lump_ContractProcPeriod.isContainedBaseDocContractProcPeriod(JP_ContractProcPeriod_ID))
				{
					return MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForOutsideOfTheDerivativeDocPeriod;
				}
			}

			//Start Period
			if(contractLine.getJP_DerivativeDocPolicy_InOut().equals(MContractLine.JP_DERIVATIVEDOCPOLICY_INOUT_FromStartContractProcessPeriod)
					||contractLine.getJP_DerivativeDocPolicy_InOut().equals(MContractLine.JP_DERIVATIVEDOCPOLICY_INOUT_FromStartContractProcessPeriodToEnd) )
			{
				MContractProcPeriod contractLine_Period = MContractProcPeriod.get(ctx, contractLine.getJP_ProcPeriod_Start_InOut_ID());
				MContractProcPeriod process_Period = MContractProcPeriod.get(ctx, JP_ContractProcPeriod_ID);
				if(contractLine_Period.getStartDate().compareTo(process_Period.getStartDate()) <= 0)
				{
					;//This is OK. contractLine_Period.StartDate <= process_Period.StartDate
				}else{

					return MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForOutsideOfTheBaseDocLinePeriod;
				}
			}

			//End Period
			if(contractLine.getJP_DerivativeDocPolicy_InOut().equals(MContractLine.JP_DERIVATIVEDOCPOLICY_INOUT_ToEndContractProcessPeriod)
					||contractLine.getJP_DerivativeDocPolicy_InOut().equals(MContractLine.JP_DERIVATIVEDOCPOLICY_INOUT_FromStartContractProcessPeriodToEnd) )
			{
				MContractProcPeriod contractLine_Period = MContractProcPeriod.get(ctx, contractLine.getJP_ProcPeriod_End_InOut_ID());
				MContractProcPeriod process_Period = MContractProcPeriod.get(ctx, JP_ContractProcPeriod_ID);
				if(contractLine_Period.getEndDate().compareTo(process_Period.getEndDate()) >= 0)
				{
					;///This is OK. contractLine_Period.EndDate >= process_Period.EndDate
				}else{

					return MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForOutsideOfTheBaseDocLinePeriod;
				}
			}
		}


		if(orderLine != null)
		{
			BigDecimal movementQty = contractLine.getMovementQty();
			BigDecimal qtyToDeliver = orderLine.getQtyOrdered().subtract(orderLine.getQtyDelivered());
			if(qtyToDeliver.compareTo(movementQty) < 0)
			{
				return MContractLogDetail.JP_CONTRACTLOGMSG_OverOrderedQuantity;
			}
		}

		return null;
	}

	static public String getSkipReason_CreateDerivativeInOutIndirectly(Properties ctx, MContractProcSchedule contractProcSchedule, int JP_ContractProcPeriod_ID, String trxName)
	{
		if(!contractProcSchedule.isFactCreatedJP())
		{
			return MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForBaseDocIsNotCreated;//BD
		}

		if(contractProcSchedule.getC_Order_ID() == 0)
		{
			return MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForBaseDocIsNotCreated;//BD
		}

		if(!contractProcSchedule.getDocStatus().equals(DocAction.STATUS_Completed))
		{
			return MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForDocumentStatusIsNotCompleted;//BF
		}


		MContractPSInOutLine[] contractPSInOutLines = contractProcSchedule.getContractPSInOutLines(JP_ContractProcPeriod_ID, false);
		if(contractPSInOutLines.length <= 0)
		{
			return MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForNoContractShipReceiptSchedule;//BH
		}

		return null;
	}

	static public String getSkipReason_CreateDerivativeInvoiceLine(Properties ctx, MContractContent contractContent, MContractLine contractLine, MOrderLine orderLine
															, int JP_ContractProcPeriod_ID,int JP_ContractProcess_ID, boolean isCheckOverlap, boolean isCheckContractProcess, String trxName)
	{
		if(contractContent == null)
			return MContractLogDetail.JP_CONTRACTLOGMSG_UnexpectedError;

		if(contractLine == null)
			return MContractLogDetail.JP_CONTRACTLOGMSG_UnexpectedError;

		//Check Contract Process
		if(isCheckContractProcess)
		{
			if(contractLine.getJP_ContractProcess_Inv_ID() != JP_ContractProcess_ID)
			{
				return MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForDifferentContractProcess;
			}
		}


		//Check Contract Calender
		MContractProcPeriod processPeriod = MContractProcPeriod.get(ctx, JP_ContractProcPeriod_ID);
		if(contractLine.getJP_ContractCalender_Inv_ID() != processPeriod.getJP_ContractCalender_ID())
		{
			return MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForDifferentContractCalender;
		}

		if(!contractLine.isCreateDocLineJP())
		{
			return MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForCreateDocLineIsFalse;
		}

		//Skip Qty ZERO
		if(contractLine.getQtyInvoiced().compareTo(Env.ZERO) == 0)
		{
			return MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForQtyOfContractLineIsZero;
		}


		//Check Overlap
		if(isCheckOverlap)
		{
			MInvoiceLine[] iLines = contractLine.getInvoiceLineByContractPeriod(ctx, JP_ContractProcPeriod_ID, trxName);
			if(iLines != null && iLines.length > 0)
			{
				return MContractLogDetail.JP_CONTRACTLOGMSG_SkippedContractProcessForOverlapContractProcessPeriod;
			}
		}


		//Check Derivative Invoice Doc Line
		//Lump
		if(contractLine.getJP_DerivativeDocPolicy_Inv().equals(MContractLine.JP_DERIVATIVEDOCPOLICY_INV_LumpOnACertainPointOfContractProcessPeriod))
		{
			MContractProcPeriod lump_ContractProcPeriod = MContractProcPeriod.get(ctx,contractLine.getJP_ProcPeriod_Lump_Inv_ID());
			if(!lump_ContractProcPeriod.isContainedBaseDocContractProcPeriod(JP_ContractProcPeriod_ID))
			{
				return MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForOutsideOfTheDerivativeDocPeriod;
			}
		}

		//Start Period
		if(contractLine.getJP_DerivativeDocPolicy_Inv().equals(MContractLine.JP_DERIVATIVEDOCPOLICY_INV_FromStartContractProcessPeriod)
				||contractLine.getJP_DerivativeDocPolicy_Inv().equals(MContractLine.JP_DERIVATIVEDOCPOLICY_INV_FromStartContractProcessPeriodToEnd) )
		{
			MContractProcPeriod contractLine_Period = MContractProcPeriod.get(ctx, contractLine.getJP_ProcPeriod_Start_Inv_ID());
			MContractProcPeriod process_Period = MContractProcPeriod.get(ctx, JP_ContractProcPeriod_ID);
			if(contractLine_Period.getStartDate().compareTo(process_Period.getStartDate()) <= 0)
			{
				;//This is OK. contractLine_Period.StartDate <= process_Period.StartDate
			}else{

				return MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForOutsideOfTheBaseDocLinePeriod;
			}
		}

		//End Period
		if(contractLine.getJP_DerivativeDocPolicy_Inv().equals(MContractLine.JP_DERIVATIVEDOCPOLICY_INV_ToEndContractProcessPeriod)
				|| contractLine.getJP_DerivativeDocPolicy_Inv().equals(MContractLine.JP_DERIVATIVEDOCPOLICY_INV_FromStartContractProcessPeriodToEnd) )
		{
			MContractProcPeriod contractLine_Period = MContractProcPeriod.get(ctx, contractLine.getJP_ProcPeriod_End_Inv_ID());
			MContractProcPeriod process_Period = MContractProcPeriod.get(ctx, JP_ContractProcPeriod_ID);
			if(contractLine_Period.getEndDate().compareTo(process_Period.getEndDate()) >= 0)
			{
				;//This is OK. contractLine_Period.EndDate >= process_Period.EndDate

			}else{

				return MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForOutsideOfTheBaseDocLinePeriod;
			}
		}

		//Check Over Qty
		if(orderLine != null)
		{
			BigDecimal qtyInvoiced = contractLine.getQtyInvoiced();
			BigDecimal qtyToInvoice = orderLine.getQtyOrdered().subtract(orderLine.getQtyInvoiced());
			if(qtyToInvoice.compareTo(qtyInvoiced) < 0)
			{
				return MContractLogDetail.JP_CONTRACTLOGMSG_OverOrderedQuantity;
			}
		}

		return null;
	}

	static public String getSkipReason_CreateDerivativeInvoiceIndirectly(Properties ctx, MContractProcSchedule contractProcSchedule, int JP_ContractProcPeriod_ID, String trxName)
	{
		if(!contractProcSchedule.isFactCreatedJP())
		{
			return MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForBaseDocIsNotCreated;//BD
		}

		if(contractProcSchedule.getC_Order_ID() == 0)
		{
			return MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForBaseDocIsNotCreated;//BD
		}

		if(!contractProcSchedule.getDocStatus().equals(DocAction.STATUS_Completed))
		{
			return MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForDocumentStatusIsNotCompleted;//BF
		}


		MContractPSInvoiceLine[] contractPSInvoiceLines = contractProcSchedule.getContractPSInvoiceLines(JP_ContractProcPeriod_ID, false);
		if(contractPSInvoiceLines.length <= 0)
		{
			return MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForNoContractInvoiceSchedule;//BI
		}

		return null;
	}
}
