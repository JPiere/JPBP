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

import org.compiere.model.PO;
import org.compiere.process.DocAction;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.MContract;
import jpiere.base.plugin.org.adempiere.model.MContractCalender;
import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractLine;
import jpiere.base.plugin.org.adempiere.model.MContractLineT;
import jpiere.base.plugin.org.adempiere.model.MContractLogDetail;
import jpiere.base.plugin.org.adempiere.model.MContractProcPeriod;

/** JPIERE-0363
*
* @author Hideaki Hagiwara
*
*/
public class DefaultCreateContractByCopy extends AbstractCreateContractByCopy {

	@Override
	protected void prepare()
	{
		super.prepare();
	}

	@Override
	protected String doIt() throws Exception
	{
		super.doIt();

		if(p_JP_ContractTabLevel.equals(JP_ContractTabLevel_Document))
		{
			createContractContents();

		}else if(p_JP_ContractTabLevel.equals(JP_ContractTabLevel_Content)){

			createContractLine(to_ContractContent, from_ContractContent, false, true);

		}

		return Msg.getMsg(getCtx(), "Success");
	}

	protected void createContractContents() throws Exception
	{

		MContractContent[]  from_ContractContents = from_Contract.getContractContents();

		//Create Contract Content
		for(int i = 0 ; i < from_ContractContents.length; i++)
		{
			MContractContent to_ContractContent = new MContractContent(getCtx(), 0, get_TrxName());
			createContractContent(from_ContractContents[i], to_ContractContent,false);

		}//For i

	}//createContractContent


	protected void createContractContent(MContractContent from_ContractContent, MContractContent to_ContractContent, boolean isRenewContractContent) throws Exception
	{

		PO.copyValues(from_ContractContent, to_ContractContent);
		MContract from_Contract = new MContract(getCtx(), from_ContractContent.getJP_Contract_ID(), get_TrxName());
		to_ContractContent.setAD_Org_ID(from_Contract.getAD_Org_ID());
		to_ContractContent.setAD_OrgTrx_ID(from_Contract.getAD_OrgTrx_ID());
		to_ContractContent.setJP_Contract_ID(from_Contract.getJP_Contract_ID());
		to_ContractContent.setJP_ContractContentT_ID(from_ContractContent.getJP_ContractContentT_ID());
		to_ContractContent.setC_DocType_ID(from_ContractContent.getC_DocType_ID());
		to_ContractContent.setDocBaseType(from_ContractContent.getDocBaseType());
		to_ContractContent.setJP_BaseDocDocType_ID(from_ContractContent.getJP_BaseDocDocType_ID());
		to_ContractContent.setJP_CreateDerivativeDocPolicy(from_ContractContent.getJP_CreateDerivativeDocPolicy());

		to_ContractContent.setJP_ContractCalender_ID(from_ContractContent.getJP_ContractCalender_ID());
		to_ContractContent.setJP_ContractProcess_ID(from_ContractContent.getJP_ContractProcess_ID());
		to_ContractContent.setJP_Contract_Acct_ID(from_ContractContent.getJP_Contract_Acct_ID());
		to_ContractContent.setName(from_ContractContent.getName());

		to_ContractContent.setJP_ContractProcessMethod(from_ContractContent.getJP_ContractProcessMethod());
		to_ContractContent.setIsAutomaticUpdateJP(from_ContractContent.isAutomaticUpdateJP());
		to_ContractContent.setJP_ContractC_AutoUpdatePolicy(from_ContractContent.getJP_ContractC_AutoUpdatePolicy());
		to_ContractContent.setIsRenewedContractContentJP(false);

		to_ContractContent.setJP_CounterContractContent_ID(0);

		if(isRenewContractContent)
		{
			to_ContractContent.setJP_ContractProcDate_From(calculateDate(from_ContractContent.getJP_ContractProcDate_To(),1));
		}

		MContractCalender calender =  MContractCalender.get(getCtx(), to_ContractContent.getJP_ContractCalender_ID());
		MContractProcPeriod period = calender.getContractProcessPeriod(getCtx(), to_ContractContent.getJP_ContractProcDate_From());
		to_ContractContent.setDateDoc(period.getDateDoc());
		to_ContractContent.setDateAcct(period.getDateAcct());
		to_ContractContent.setDateInvoiced(period.getDateDoc());


		if(from_Contract.getJP_ContractType().contentEquals(MContract.JP_CONTRACTTYPE_PeriodContract))
		{
			if(!isRenewContractContent)
			{
				if(from_ContractContent.getJP_ContractProcDate_From().compareTo(period.getStartDate()) > 0)
				{
					to_ContractContent.setJP_ContractProcDate_From(from_ContractContent.getJP_ContractProcDate_From());
				}else {
					to_ContractContent.setJP_ContractProcDate_From(period.getStartDate());
				}
			}

			if(to_ContractContent.isAutomaticUpdateJP())
			{
				to_ContractContent.setJP_ContractProcDate_To(from_Contract.getJP_ContractPeriodDate_To());
			}

		}else if(from_Contract.getJP_ContractType().contentEquals(MContract.JP_CONTRACTTYPE_SpotContract)) {

			if(to_ContractContent.getOrderType().contentEquals(MContractContent.ORDERTYPE_StandardOrder)
					|| to_ContractContent.getOrderType().contentEquals(MContractContent.ORDERTYPE_Quotation))
			{
				to_ContractContent.setDatePromised(calculateDate(period.getDateAcct(),to_ContractContent.getJP_ContractContentT().getDeliveryTime_Promised())) ;
			}
		}

		to_ContractContent.setTotalLines(Env.ZERO);
		to_ContractContent.setDocStatus(DocAction.STATUS_Drafted);
		to_ContractContent.setDocAction(DocAction.ACTION_Complete);
		to_ContractContent.setIsScheduleCreatedJP(false);
		to_ContractContent.setJP_ContractProcStatus(MContractContent.JP_CONTRACTPROCSTATUS_Unprocessed);

		setDocumentNoOfContractContent(from_ContractContent, to_ContractContent);
		setBPartnerOfContractContent(from_ContractContent, to_ContractContent);
		try {
			setWarehouseOfContractContent(from_ContractContent, to_ContractContent);
		} catch (Exception e) {
			throw e;
		}

		to_ContractContent.setC_Currency_ID(to_ContractContent.getM_PriceList().getC_Currency_ID());

		to_ContractContent.setJP_PreContractContent_ID(from_ContractContent.getJP_ContractContent_ID());

		try {
			to_ContractContent.saveEx(get_TrxName());
		}catch (Exception e) {

			if(m_ContractLog == null)
			{
				throw new Exception( Msg.getMsg(getCtx(), "SaveError") + Msg.getElement(getCtx(), "CopyFrom") + " : "
						+ Msg.getElement(getCtx(), "JP_ContractContent_ID") + "_" + from_ContractContent.getDocumentNo() + " >>> " + e.getMessage() );
			}else {

				createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SaveError, null, to_ContractContent, e.getMessage());

			}
		}

		try {
			if(isRenewContractContent)
			{
				createContractLine(to_ContractContent, from_ContractContent, isRenewContractContent, true);
			}else {
				createContractLine(to_ContractContent, from_ContractContent, isRenewContractContent, false);
			}

		}catch (Exception e) {

			if(m_ContractLog == null)
			{
				throw new Exception( Msg.getMsg(getCtx(), "Error") + Msg.getElement(getCtx(), "CopyFrom") + " : "
						+ Msg.getElement(getCtx(), "JP_ContractContent_ID") + "_" + from_ContractContent.getDocumentNo() + " >>> " + e.getMessage() );
			}else {

				createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_UnexpectedError, null, to_ContractContent, e.getMessage());

			}
		}

	}


	protected void createContractLine(MContractContent to_ContractContent, MContractContent from_ContractContent, boolean isRenewContractContent, boolean isReSetPeriod) throws Exception
	{
		MContractLine[] to_ContractLines = to_ContractContent.getLines();
		if(to_ContractLines.length > 0)
		{
			throw new Exception(Msg.getMsg(getCtx(), "JP_ContractContentLineCreated"));//Contract Content Line has already been created
		}


		//Create Contract Content Line
		MContractLine[] from_ContractLines = from_ContractContent.getLines();
		for(int i = 0; i < from_ContractLines.length; i++)
		{
			if(isRenewContractContent && from_ContractLines[i].getJP_ContractL_AutoUpdatePolicy().equals(MContractLine.JP_CONTRACTL_AUTOUPDATEPOLICY_NotTakeOverToRenewTheContract))
			{
				continue;
			}

			if(isRenewContractContent && from_ContractLines[i].getJP_ContractLineT_ID() == 0)
			{
				if(from_ContractLines[i].getJP_ProcPeriod_End_ID() != 0
						|| from_ContractLines[i].getJP_ProcPeriod_End_InOut_ID() != 0
						|| from_ContractLines[i].getJP_ProcPeriod_End_Inv_ID() != 0
						|| from_ContractLines[i].getJP_ProcPeriod_Lump_ID() != 0
						|| from_ContractLines[i].getJP_ProcPeriod_Lump_InOut_ID() != 0
						|| from_ContractLines[i].getJP_ProcPeriod_Lump_Inv_ID() != 0 )
				{
					if(m_ContractLog != null)
					{
						createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_Skipped, from_ContractLines[i], to_ContractContent
								, Msg.getMsg(getCtx(), "JP_CouldNotTakeOverToRenewTheContract") + " - " + "JP_ContractLineT_ID() == 0", MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_ToBeConfirmed);
					}
					continue;
				}

			}

			MContractLine to_ContractLine = new MContractLine(getCtx(), 0, get_TrxName());
			PO.copyValues(from_ContractLines[i], to_ContractLine);
			to_ContractLine.setAD_Org_ID(to_ContractContent.getAD_Org_ID());
			to_ContractLine.setAD_OrgTrx_ID(to_ContractContent.getAD_OrgTrx_ID());
			to_ContractLine.setDateOrdered(to_ContractContent.getDateOrdered());
			to_ContractLine.setDatePromised(to_ContractLine.getDatePromised()) ;
			to_ContractLine.setJP_ContractContent_ID(to_ContractContent.getJP_ContractContent_ID());
			to_ContractLine.setJP_ContractLineT_ID(from_ContractLines[i].getJP_ContractLineT_ID());

			if(isReSetPeriod)
			{

				if(from_ContractLines[i].getJP_ContractLineT_ID() == 0)
				{
					if(!Util.isEmpty(from_ContractLines[i].getJP_BaseDocLinePolicy()))
					{
						to_ContractLine.setJP_BaseDocLinePolicy(MContractLine.JP_BASEDOCLINEPOLICY_ForTheDurationOfContractProcessPeriod);
						to_ContractLine.setJP_ProcPeriod_Lump_ID(0);
						to_ContractLine.setJP_ProcPeriod_Start_ID(0);
						to_ContractLine.setJP_ProcPeriod_End_ID(0);
					}

					if(!Util.isEmpty(from_ContractLines[i].getJP_DerivativeDocPolicy_InOut()))
					{
						to_ContractLine.setJP_DerivativeDocPolicy_InOut(MContractLine.JP_DERIVATIVEDOCPOLICY_INOUT_ForTheDurationOfContractProcessPeriod);
						to_ContractLine.setJP_ProcPeriod_Lump_InOut_ID(0);
						to_ContractLine.setJP_ProcPeriod_Start_InOut_ID(0);
						to_ContractLine.setJP_ProcPeriod_End_InOut_ID(0);
					}

					if(!Util.isEmpty(from_ContractLines[i].getJP_DerivativeDocPolicy_Inv()))
					{
						to_ContractLine.setJP_DerivativeDocPolicy_Inv(MContractLine.JP_DERIVATIVEDOCPOLICY_INV_ForTheDurationOfContractProcessPeriod);
						to_ContractLine.setJP_ProcPeriod_Lump_Inv_ID(0);
						to_ContractLine.setJP_ProcPeriod_Start_Inv_ID(0);
						to_ContractLine.setJP_ProcPeriod_End_Inv_ID(0);
					}

				}

				setBaseDocLineProcPeriod(to_ContractLine, MContractLineT.get(getCtx(), to_ContractLine.getJP_ContractLineT_ID()));
				setDerivativeInOutLineProcPeriod(to_ContractLine, MContractLineT.get(getCtx(), to_ContractLine.getJP_ContractLineT_ID()));
				setDerivativeInvoiceLineProcPeriod(to_ContractLine, MContractLineT.get(getCtx(), to_ContractLine.getJP_ContractLineT_ID()));
			}

			try
			{
				to_ContractLine.saveEx(get_TrxName());
			}catch (Exception e) {

				if(m_ContractLog == null)
				{
					throw new Exception(Msg.getMsg(getCtx(), "SaveError") + Msg.getElement(getCtx(), "CopyFrom") + " : "
							+ Msg.getElement(getCtx(), "JP_ContractLine_ID") + "_" + from_ContractLines[i].getLine() + " >>> " + e.getMessage() );
				}else {
					createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SaveError, from_ContractLines[i], to_ContractContent, e.getMessage());
				}

			}

			if(m_ContractLog != null)
			{
				createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_CreatedDocumentLine, to_ContractLine, to_ContractContent
																	, Msg.getMsg(getCtx(), "JP_Success"), MContractLogDetail.JP_CONTRACTPROCESSTRACELEVEL_Information);
			}

		}//For i

	}//createContractLine

}
