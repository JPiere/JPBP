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
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MDocType;
import org.compiere.model.MInOutLine;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MOrderLine;
import org.compiere.model.MUOM;
import org.compiere.model.PO;
import org.compiere.process.DocAction;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;

import jpiere.base.plugin.org.adempiere.model.MContractCalender;
import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractLine;
import jpiere.base.plugin.org.adempiere.model.MContractLogDetail;
import jpiere.base.plugin.org.adempiere.model.MContractPSInOutLine;
import jpiere.base.plugin.org.adempiere.model.MContractPSInvoiceLine;
import jpiere.base.plugin.org.adempiere.model.MContractPSLine;
import jpiere.base.plugin.org.adempiere.model.MContractProcPeriod;
import jpiere.base.plugin.org.adempiere.model.MContractProcSchedule;

/**
* JPIERE-0431:Default Contract Process that create Contract Process Schedule
*
* @author Hideaki Hagiwara
*
*/
public class DefaultContractProcessCreateSchedule extends AbstractContractProcess
{

	@Override
	protected void prepare()
	{
		super.prepare();
	}

	@Override
	protected String doIt() throws Exception
	{
		super.doIt();

		MContractCalender contractCalender = MContractCalender.get(getCtx(), m_ContractContent.getJP_ContractCalender_ID());
		if(m_ContractContent.getJP_ContractProcDate_To()==null)
		{
			String descriptionMsg = Msg.getMsg(getCtx(), "NotFound") + " : " + Msg.getElement(getCtx(), "JP_ContractProcDate_To");
			createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_UnexpectedError, null,  null, descriptionMsg);
			return "";
		}

		int JP_ContractProcPeriod_ID = getJP_ContractProctPeriod_ID();
		MContractProcPeriod firstContractProcPeriod = contractCalender.getContractProcessPeriod(getCtx(), m_ContractContent.getJP_ContractProcDate_From());
		MContractProcPeriod endContractProcPeriod = contractCalender.getContractProcessPeriod(getCtx(), m_ContractContent.getJP_ContractProcDate_To());
		MContractProcPeriod contractProcPeriod = null;

		boolean isOK = false;

		if(JP_ContractProcPeriod_ID == 0)
		{
			int i = 1;
			do
			{
				contractProcPeriod = contractCalender.getContractProcessPeriod(getCtx(), m_ContractContent.getJP_ContractProcDate_From(), null, i);

				if(contractProcPeriod.getJP_ContractProcPeriod_ID( ) == firstContractProcPeriod.getJP_ContractProcPeriod_ID()
						&& contractProcPeriod.getJP_ContractProcPeriod_ID( ) == endContractProcPeriod.getJP_ContractProcPeriod_ID())
				{
					isOK = createContractProcSchedule(contractProcPeriod, true,true);

				}else if(contractProcPeriod.getJP_ContractProcPeriod_ID( ) == firstContractProcPeriod.getJP_ContractProcPeriod_ID()
						&& contractProcPeriod.getJP_ContractProcPeriod_ID( ) != endContractProcPeriod.getJP_ContractProcPeriod_ID()) {

					isOK = createContractProcSchedule(contractProcPeriod, true,false);

				}else if(contractProcPeriod.getJP_ContractProcPeriod_ID( ) != firstContractProcPeriod.getJP_ContractProcPeriod_ID()
						&& contractProcPeriod.getJP_ContractProcPeriod_ID( ) == endContractProcPeriod.getJP_ContractProcPeriod_ID()) {

					isOK = createContractProcSchedule(contractProcPeriod, false,true);

				}else if(contractProcPeriod.getJP_ContractProcPeriod_ID( ) != firstContractProcPeriod.getJP_ContractProcPeriod_ID()
						&& contractProcPeriod.getJP_ContractProcPeriod_ID( ) != endContractProcPeriod.getJP_ContractProcPeriod_ID()) {

					isOK = createContractProcSchedule(contractProcPeriod, false,false);
				}

				i++;

			}while(contractProcPeriod.getJP_ContractProcPeriod_ID( ) != endContractProcPeriod.getJP_ContractProcPeriod_ID());

		}else {

			contractProcPeriod = MContractProcPeriod.get(getCtx(), JP_ContractProcPeriod_ID);

			if(contractProcPeriod.getJP_ContractProcPeriod_ID( ) == firstContractProcPeriod.getJP_ContractProcPeriod_ID()
					&& contractProcPeriod.getJP_ContractProcPeriod_ID( ) == endContractProcPeriod.getJP_ContractProcPeriod_ID())
			{
				isOK = createContractProcSchedule(contractProcPeriod, true,true);

			}else if(contractProcPeriod.getJP_ContractProcPeriod_ID( ) == firstContractProcPeriod.getJP_ContractProcPeriod_ID()
					&& contractProcPeriod.getJP_ContractProcPeriod_ID( ) != endContractProcPeriod.getJP_ContractProcPeriod_ID()) {

				isOK = createContractProcSchedule(contractProcPeriod, true,false);

			}else if(contractProcPeriod.getJP_ContractProcPeriod_ID( ) != firstContractProcPeriod.getJP_ContractProcPeriod_ID()
					&& contractProcPeriod.getJP_ContractProcPeriod_ID( ) == endContractProcPeriod.getJP_ContractProcPeriod_ID()) {

				isOK = createContractProcSchedule(contractProcPeriod, false,true);

			}else if(contractProcPeriod.getJP_ContractProcPeriod_ID( ) != firstContractProcPeriod.getJP_ContractProcPeriod_ID()
					&& contractProcPeriod.getJP_ContractProcPeriod_ID( ) != endContractProcPeriod.getJP_ContractProcPeriod_ID()) {

				isOK = createContractProcSchedule(contractProcPeriod, false,false);
			}

		}

		if(isOK)
		{
			if(!m_ContractContent.isScheduleCreatedJP())
			{
				m_ContractContent.setIsScheduleCreatedJP(true);
				try {
					m_ContractContent.saveEx(get_TrxName());
				}catch (Exception e) {
					createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SaveError, null, null, e.getMessage());
					throw e;
				}

			}

		}

		return "";

	}//doIt

	/**
	 *
	 * Create Contract Proc Schedule
	 *
	 * @param contractProcPeriod
	 * @param isFirstPeriod
	 * @param isLastPeriod
	 * @param loopCounter
	 * @return
	 */
	private boolean createContractProcSchedule(MContractProcPeriod contractProcPeriod, boolean isFirstPeriod, boolean isLastPeriod)
	{

		boolean isOK = false;


		//Check Overlap Header
		MContractProcSchedule[] contractProcSchedules = m_ContractContent.getContractProcScheduleByContractPeriod(Env.getCtx(), contractProcPeriod.getJP_ContractProcPeriod_ID(), get_TrxName());
		if(contractProcSchedules != null && contractProcSchedules.length > 0)
		{
			p_JP_ContractProcPeriod_ID = contractProcPeriod.getJP_ContractProcPeriod_ID();
			createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SkippedContractProcessForOverlapContractProcessPeriod, null,  contractProcSchedules[0], null);
			return false;
		}//Check Overlap


		/** Pre check - Pre judgment create Document or not. */
		MContractLine[] 	m_lines = m_ContractContent.getLines();
		boolean isCreateDocLine = false;
		for(int i = 0; i < m_lines.length; i++)
		{
			if(!isCreateContractPSLine(m_lines[i], contractProcPeriod.getJP_ContractProcPeriod_ID(), false))
				continue;

			isCreateDocLine = true;
			break;
		}


		if(!isCreateDocLine)
		{
			createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_AllContractContentLineWasSkipped, null, null, null);
			return false;
		}


		/** Create Contract Process Schedule Header */
		MContractProcSchedule contractProcSchedule = new MContractProcSchedule(getCtx(), 0, get_TrxName());
		PO.copyValues(m_ContractContent, contractProcSchedule);
		contractProcSchedule.setJP_ContractProcPeriod_ID(contractProcPeriod.getJP_ContractProcPeriod_ID());
		contractProcSchedule.setAD_Org_ID(m_ContractContent.getAD_Org_ID());
		contractProcSchedule.setJP_ContractContent_ID(m_ContractContent.getJP_ContractContent_ID());
		contractProcSchedule.setDocAction(getDocAction());
		contractProcSchedule.setDocStatus(DocAction.STATUS_Drafted);

		//Date
		if(isFirstPeriod)
		{
			contractProcSchedule.setDateDoc(m_ContractContent.getDateDoc());
			contractProcSchedule.setDateOrdered(m_ContractContent.getDateDoc());
			contractProcSchedule.setDateInvoiced(m_ContractContent.getDateDoc());

			if(m_ContractContent.getDateAcct().compareTo(contractProcPeriod.getStartDate()) > 0
					&& m_ContractContent.getDateAcct().compareTo(contractProcPeriod.getEndDate()) < 0	)
			{
				contractProcSchedule.setDateAcct(m_ContractContent.getDateAcct());
			}else {
				contractProcSchedule.setDateAcct(contractProcPeriod.getDateAcct());
			}


		}else {
			contractProcSchedule.setDateDoc(contractProcPeriod.getDateDoc());
			contractProcSchedule.setDateOrdered(contractProcPeriod.getDateDoc());
			contractProcSchedule.setDateInvoiced(contractProcPeriod.getDateDoc());
			contractProcSchedule.setDateAcct(contractProcPeriod.getDateAcct());
		}
		contractProcSchedule.setDatePromised(getOrderHeaderDatePromised(contractProcSchedule.getDateAcct()));

		//Doc Type
		MDocType contractContentDocType = MDocType.get(getCtx(), m_ContractContent.getC_DocType_ID());
		Object obj_ContractPSDocType_ID = contractContentDocType.get_Value("JP_ContractPSDocType_ID");
		if(obj_ContractPSDocType_ID == null)
		{

			int JP_ContractPSDocType_ID = getDefaultContractPSDocType_ID();
			if(JP_ContractPSDocType_ID == 0)
			{
				createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_UnexpectedError, null, null, Msg.getMsg(getCtx(), "NotFound") + " : " + Msg.getElement(getCtx(), "C_DocType_ID"));
				return false;

			}else {
				contractProcSchedule.setC_DocType_ID(JP_ContractPSDocType_ID);
			}

		}else {

			contractProcSchedule.setC_DocType_ID(((Integer)obj_ContractPSDocType_ID).intValue());

		}

		//other
		contractProcSchedule.setDocumentNo(contractProcSchedule.getDocumentNo() + "-" + contractProcPeriod.getName());

		try {
			contractProcSchedule.saveEx(get_TrxName());
		} catch (AdempiereException e) {
			createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SaveError, null, null, e.getMessage());
			throw e;
		}finally {
			;
		}

		createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_CreatedDocument, null, contractProcSchedule, null);

		isOK = createContractPSLines(contractProcSchedule, contractProcPeriod, isFirstPeriod, isLastPeriod);

		if(isOK)
		{
			if(getDocAction() != null)
			{
				if(contractProcSchedule.processIt(getDocAction()))
				{
					try {
						contractProcSchedule.saveEx(get_TrxName());
					} catch (AdempiereException e) {
						createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SaveError, null, null, e.getMessage());
						throw e;
					}finally {
						;
					}

				}else {
					createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_DocumentActionError, null, contractProcSchedule, null);
				}
			}
		}



		return isOK;

	}//createContractProcSchedule


	/**
	 * Create Contract Process Schedule Lines
	 *
	 * @param contractProcSchedule
	 * @param contractProcessPeriod
	 * @param isFirstPeriod
	 * @param isLastPeriod
	 * @return
	 */
	private boolean createContractPSLines(MContractProcSchedule contractProcSchedule, MContractProcPeriod contractProcessPeriod, boolean isFirstPeriod, boolean isLastPeriod)
	{

		MContractLine[] contractLines = m_ContractContent.getLines();
		for(int i = 0; i < contractLines.length; i++)
		{
			if(!isCreateContractPSLine(contractLines[i], contractProcessPeriod.getJP_ContractProcPeriod_ID(), true))
				continue;


			MContractPSLine contractPSLine = new MContractPSLine(getCtx(), 0, get_TrxName());
			PO.copyValues(contractLines[i], contractPSLine);
			contractPSLine.setAD_Org_ID(m_ContractContent.getAD_Org_ID());
			contractPSLine.setJP_ContractProcSchedule_ID(contractProcSchedule.getJP_ContractProcSchedule_ID());
			contractPSLine.setJP_ContractLine_ID(contractLines[i].getJP_ContractLine_ID());

			//Set Qty
			MUOM uom = null;
			if(contractLines[i].getM_Product_ID() > 0)
				uom = MUOM.get(getCtx(),contractLines[i].getM_Product().getC_UOM_ID());
			else
				uom = MUOM.get(getCtx(),contractLines[i].getC_UOM_ID());

			if(isFirstPeriod && isLastPeriod)
			{
				contractPSLine.setQtyEntered(contractLines[i].getQtyEntered());
				contractPSLine.setC_UOM_ID(contractLines[i].getC_UOM_ID());
				contractPSLine.setQtyOrdered(contractLines[i].getQtyOrdered());

			}else if(isFirstPeriod && !isLastPeriod){

				if(m_ContractContent.getJP_ContractProcDate_From().compareTo(contractProcessPeriod.getStartDate()) == 0)
				{
					contractPSLine.setQtyEntered(contractLines[i].getQtyEntered());
					contractPSLine.setC_UOM_ID(contractLines[i].getC_UOM_ID());
					contractPSLine.setQtyOrdered(contractLines[i].getQtyOrdered());

				}else {//per diem

					BigDecimal umerator = new BigDecimal(getContractDays(m_ContractContent.getJP_ContractProcDate_From(), contractProcessPeriod.getEndDate()));
					BigDecimal denominator = new BigDecimal(contractProcessPeriod.getContractProcPeriodDays());

					BigDecimal qtyOrdered = umerator.divide(denominator, uom.getStdPrecision()+2, RoundingMode.HALF_UP).multiply(contractLines[i].getQtyOrdered());
					qtyOrdered = qtyOrdered.divide(Env.ONE,uom.getStdPrecision(), RoundingMode.HALF_UP);
					contractPSLine.setQtyEntered(qtyOrdered);
					contractPSLine.setC_UOM_ID(uom.getC_UOM_ID());
					contractPSLine.setQtyOrdered(qtyOrdered);

				}

			}else if(!isFirstPeriod && isLastPeriod){

				if(m_ContractContent.getJP_ContractProcDate_To().compareTo(contractProcessPeriod.getEndDate()) == 0)
				{
					contractPSLine.setQtyEntered(contractLines[i].getQtyEntered());
					contractPSLine.setC_UOM_ID(contractLines[i].getC_UOM_ID());
					contractPSLine.setQtyOrdered(contractLines[i].getQtyOrdered());

				}else {//per diem

					BigDecimal umerator = new BigDecimal(getContractDays(contractProcessPeriod.getStartDate(), m_ContractContent.getJP_ContractProcDate_To()));
					BigDecimal denominator = new BigDecimal(contractProcessPeriod.getContractProcPeriodDays());

					BigDecimal qtyOrdered = umerator.divide(denominator, uom.getStdPrecision()+2, RoundingMode.HALF_UP).multiply(contractLines[i].getQtyOrdered());
					qtyOrdered = qtyOrdered.divide(Env.ONE,uom.getStdPrecision(), RoundingMode.HALF_UP);
					contractPSLine.setQtyEntered(qtyOrdered);
					contractPSLine.setC_UOM_ID(uom.getC_UOM_ID());
					contractPSLine.setQtyOrdered(qtyOrdered);

				}

			}else if(!isFirstPeriod && !isLastPeriod) {
				contractPSLine.setQtyEntered(contractLines[i].getQtyEntered());
				contractPSLine.setC_UOM_ID(contractLines[i].getC_UOM_ID());
				contractPSLine.setQtyOrdered(contractLines[i].getQtyOrdered());
			}

			//Set Price
			if(contractLines[i].getC_UOM_ID() == uom.getC_UOM_ID())
				contractPSLine.setPriceEntered(contractLines[i].getPriceEntered());
			else
				contractPSLine.setPriceEntered(contractLines[i].getPriceActual());

			contractPSLine.setPriceActual(contractLines[i].getPriceActual());
			contractPSLine.setLineNetAmt(contractPSLine.getQtyOrdered().multiply(contractPSLine.getPriceActual()));

			try {
				contractPSLine.saveEx(get_TrxName());
				createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_CreatedDocumentLine, contractLines[i], contractProcSchedule, null);
			} catch (AdempiereException e) {
				createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SaveError, contractLines[i], contractProcSchedule, e.getMessage());
				throw e;
			}finally {
				;
			}

			if(m_ContractContent.getJP_CreateDerivativeDocPolicy() == null || m_ContractContent.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_Manual))
				continue;

			if(m_ContractContent.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceipt)
					|| m_ContractContent.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceiptInvoice) )
			{
				createContractPSInOutLines(contractLines[i], contractPSLine, contractProcessPeriod);
			}

			if(m_ContractContent.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateInvoice)
					|| m_ContractContent.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceiptInvoice) )
			{
				createContractPSInvoiceLines(contractLines[i], contractPSLine, contractProcessPeriod);
			}

		}//for

		return true;

	}//createContractPSLines


	/**
	 * Create Contract Process Schedule Ship/Reciept Lines
	 *
	 * @param contractLine
	 * @param contractPSLine
	 * @param baseDocContractProcPeriod
	 * @return
	 */
	private boolean createContractPSInOutLines(MContractLine contractLine, MContractPSLine contractPSLine, MContractProcPeriod baseDocContractProcPeriod)
	{
		MContractCalender contractCalender = MContractCalender.get(getCtx(), contractLine.getJP_ContractCalender_InOut_ID());
		if(contractLine.getJP_DerivativeDocPolicy_InOut().equals(MContractLine.JP_DERIVATIVEDOCPOLICY_INOUT_ForTheDurationOfContractProcessPeriod))
		{
			MContractProcPeriod contractProcPeriod = null;
			MContractProcPeriod endContractProcPeriod = contractCalender.getContractProcessPeriod(getCtx(), m_ContractContent.getJP_ContractProcDate_To());
			int i = 1;
			do
			{
				contractProcPeriod = contractCalender.getContractProcessPeriod(getCtx(), m_ContractContent.getJP_ContractProcDate_From(), null, i);
				if(contractProcPeriod.isContainedBaseDocContractProcPeriod(baseDocContractProcPeriod))
				{
					if(i == 1)
					{
						if(contractProcPeriod.getJP_ContractProcPeriod_ID( ) == endContractProcPeriod.getJP_ContractProcPeriod_ID())
							createContractPSInOutLine(contractLine, contractPSLine, baseDocContractProcPeriod, contractProcPeriod, true, true, i);
						else
							createContractPSInOutLine(contractLine, contractPSLine, baseDocContractProcPeriod, contractProcPeriod, true, false, i);
					}else {
						if(contractProcPeriod.getJP_ContractProcPeriod_ID( ) == endContractProcPeriod.getJP_ContractProcPeriod_ID())
							createContractPSInOutLine(contractLine, contractPSLine, baseDocContractProcPeriod, contractProcPeriod, false, true, i);
						else
							createContractPSInOutLine(contractLine, contractPSLine, baseDocContractProcPeriod, contractProcPeriod, false, false,i);
					}
				}

				i++;

			}while(contractProcPeriod.getJP_ContractProcPeriod_ID( ) != endContractProcPeriod.getJP_ContractProcPeriod_ID());

		}

		return true;

	}//createContractPSInOutLines

	/**
	 * Create Contract Process Schedule Ship/Reciept Line
	 *
	 * @param contractLine
	 * @param contractPSLine
	 * @param baseDocContractProcPeriod
	 * @param contractProcPeriod
	 * @param isFirstPeriod
	 * @param isLastPeriod
	 * @param loopCounter
	 * @return
	 */
	private boolean createContractPSInOutLine(MContractLine contractLine, MContractPSLine contractPSLine, MContractProcPeriod baseDocContractProcPeriod
													, MContractProcPeriod contractProcPeriod, boolean isFirstPeriod, boolean isLastPeriod, int loopCounter)
	{

		if(!isCreateContractPSInOutLine(contractPSLine, baseDocContractProcPeriod.getJP_ContractProcPeriod_ID(), false))
		{
			createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_AllContractContentLineWasSkipped, null, contractPSLine, null);
			return true;
		}

		MContractPSInOutLine contractPSInOutLine = new MContractPSInOutLine(getCtx(), 0, get_TrxName());
		PO.copyValues(contractLine, contractPSInOutLine);
		contractPSInOutLine.setAD_Org_ID(m_ContractContent.getAD_Org_ID());
		contractPSInOutLine.setJP_ContractLine_ID(contractLine.getJP_ContractLine_ID());
		contractPSInOutLine.setJP_ContractPSLine_ID(contractPSLine.getJP_ContractPSLine_ID());
		contractPSInOutLine.setJP_ContractProcPeriod_ID(contractProcPeriod.getJP_ContractProcPeriod_ID());
		contractPSInOutLine.setJP_ContractProcSchedule_ID(contractPSLine.getJP_ContractProcSchedule_ID());

		//Set Qty
		MUOM uom = null;
		if(contractLine.getM_Product_ID() > 0)
			uom = MUOM.get(getCtx(),contractLine.getM_Product().getC_UOM_ID());
		else
			uom = MUOM.get(getCtx(),contractLine.getC_UOM_ID());

		if(isFirstPeriod && isLastPeriod)
		{
			contractPSInOutLine.setQtyEntered(contractLine.getMovementQty());
			contractPSInOutLine.setC_UOM_ID(uom.getC_UOM_ID());
			contractPSInOutLine.setMovementQty(contractLine.getMovementQty());

		}else if(isFirstPeriod && !isLastPeriod){

			if(m_ContractContent.getJP_ContractProcDate_From().compareTo(contractProcPeriod.getStartDate()) == 0)
			{
				contractPSInOutLine.setQtyEntered(contractLine.getMovementQty());
				contractPSInOutLine.setC_UOM_ID(uom.getC_UOM_ID());
				contractPSInOutLine.setMovementQty(contractLine.getMovementQty());

			}else {//per diem

				BigDecimal umerator = new BigDecimal(getContractDays(m_ContractContent.getJP_ContractProcDate_From(), contractProcPeriod.getEndDate()));
				BigDecimal denominator = new BigDecimal(contractProcPeriod.getContractProcPeriodDays());

				BigDecimal qtyEntered = umerator.divide(denominator, uom.getStdPrecision()+2, RoundingMode.HALF_UP).multiply(contractLine.getMovementQty());
				qtyEntered = qtyEntered.divide(Env.ONE,uom.getStdPrecision(), RoundingMode.HALF_UP);
				contractPSInOutLine.setQtyEntered(qtyEntered);
				contractPSInOutLine.setC_UOM_ID(uom.getC_UOM_ID());
				contractPSInOutLine.setMovementQty(qtyEntered);

			}

		}else if(!isFirstPeriod && isLastPeriod){

			if(m_ContractContent.getJP_ContractProcDate_To().compareTo(contractProcPeriod.getEndDate()) == 0)
			{
				contractPSInOutLine.setQtyEntered(contractLine.getMovementQty());
				contractPSInOutLine.setC_UOM_ID(uom.getC_UOM_ID());
				contractPSInOutLine.setMovementQty(contractLine.getMovementQty());

			}else {//per diem

				BigDecimal umerator = new BigDecimal(getContractDays(contractProcPeriod.getStartDate(), m_ContractContent.getJP_ContractProcDate_To()));
				BigDecimal denominator = new BigDecimal(contractProcPeriod.getContractProcPeriodDays());

				BigDecimal qtyEntered = umerator.divide(denominator, uom.getStdPrecision()+2, RoundingMode.HALF_UP).multiply(contractLine.getMovementQty());
				qtyEntered = qtyEntered.divide(Env.ONE,uom.getStdPrecision(), RoundingMode.HALF_UP);
				contractPSInOutLine.setQtyEntered(qtyEntered);
				contractPSInOutLine.setC_UOM_ID(uom.getC_UOM_ID());
				contractPSInOutLine.setMovementQty(qtyEntered);

			}

		}else if(!isFirstPeriod && !isLastPeriod) {
			contractPSInOutLine.setQtyEntered(contractLine.getMovementQty());
			contractPSInOutLine.setC_UOM_ID(uom.getC_UOM_ID());
			contractPSInOutLine.setMovementQty(contractLine.getMovementQty());
		}

		//Set Price
		contractPSInOutLine.setPriceEntered(contractPSLine.getPriceActual());
		contractPSInOutLine.setPriceActual(contractPSLine.getPriceActual());
		contractPSInOutLine.setLineNetAmt(contractPSInOutLine.getMovementQty().multiply(contractPSInOutLine.getPriceActual()));

		try {
			contractPSInOutLine.saveEx(get_TrxName());
			createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_CreatedDocumentLine, contractLine, contractPSInOutLine, null);
		} catch (AdempiereException e) {
			createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SaveError, contractLine, contractPSLine, e.getMessage());
			throw e;
		}finally {
			;
		}

		return true;
	}


	/**
	 * Create Contract Process Schedule Invoice Lines
	 *
	 * @param contractLine
	 * @param contractPSLine
	 * @param baseDocContractProcPeriod
	 * @return
	 */
	private boolean createContractPSInvoiceLines(MContractLine contractLine, MContractPSLine contractPSLine, MContractProcPeriod baseDocContractProcPeriod)
	{
		MContractCalender contractCalender = MContractCalender.get(getCtx(), contractLine.getJP_ContractCalender_Inv_ID());

		if(contractLine.getJP_DerivativeDocPolicy_InOut().equals(MContractLine.JP_DERIVATIVEDOCPOLICY_INV_ForTheDurationOfContractProcessPeriod))
		{
			MContractProcPeriod contractProcPeriod = null;
			MContractProcPeriod endContractProcPeriod = contractCalender.getContractProcessPeriod(getCtx(), m_ContractContent.getJP_ContractProcDate_To());

			int i = 1;
			do
			{
				contractProcPeriod = contractCalender.getContractProcessPeriod(getCtx(), m_ContractContent.getJP_ContractProcDate_From(), null, i);
				if(contractProcPeriod.isContainedBaseDocContractProcPeriod(baseDocContractProcPeriod))
				{
					if(i == 1)
					{
						if(contractProcPeriod.getJP_ContractProcPeriod_ID( ) == endContractProcPeriod.getJP_ContractProcPeriod_ID())
							createContractPSInvoiceLine(contractLine, contractPSLine, baseDocContractProcPeriod, contractProcPeriod, true, true, i);
						else
							createContractPSInvoiceLine(contractLine, contractPSLine, baseDocContractProcPeriod, contractProcPeriod, true, false, i);
					}else {
						if(contractProcPeriod.getJP_ContractProcPeriod_ID( ) == endContractProcPeriod.getJP_ContractProcPeriod_ID())
							createContractPSInvoiceLine(contractLine, contractPSLine, baseDocContractProcPeriod, contractProcPeriod, false, true, i);
						else
							createContractPSInvoiceLine(contractLine, contractPSLine, baseDocContractProcPeriod, contractProcPeriod, false, false,i);
					}
				}

				i++;

			}while(contractProcPeriod.getJP_ContractProcPeriod_ID( ) != endContractProcPeriod.getJP_ContractProcPeriod_ID());

		}

		return true;

	}//createContractPSInvoiceLines


	/**
	 *
	 * Create Contract Process Schedule Invoice Line
	 *
	 * @param contractLine
	 * @param contractPSLine
	 * @param baseDocContractProcPeriod
	 * @param contractProcPeriod
	 * @param isFirstPeriod
	 * @param isLastPeriod
	 * @param loopCounter
	 * @return
	 */
	private boolean createContractPSInvoiceLine( MContractLine contractLine, MContractPSLine contractPSLine, MContractProcPeriod baseDocContractProcPeriod
															, MContractProcPeriod contractProcPeriod, boolean isFirstPeriod, boolean isLastPeriod, int loopCounter)
	{
		if(!isCreateContractPSInvoiceLine(contractPSLine, baseDocContractProcPeriod.getJP_ContractProcPeriod_ID(), false))
		{
			createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_AllContractContentLineWasSkipped, null, contractPSLine, null);
			return true;
		}

		MContractPSInvoiceLine contractPSInvoiceLine = new MContractPSInvoiceLine(getCtx(), 0, get_TrxName());
		PO.copyValues(contractLine, contractPSInvoiceLine);
		contractPSInvoiceLine.setAD_Org_ID(m_ContractContent.getAD_Org_ID());
		contractPSInvoiceLine.setJP_ContractLine_ID(contractLine.getJP_ContractLine_ID());
		contractPSInvoiceLine.setJP_ContractPSLine_ID(contractPSLine.getJP_ContractPSLine_ID());
		contractPSInvoiceLine.setJP_ContractProcPeriod_ID(contractProcPeriod.getJP_ContractProcPeriod_ID());
		contractPSInvoiceLine.setJP_ContractProcSchedule_ID(contractPSLine.getJP_ContractProcSchedule_ID());

		MUOM uom = null;
		if(contractLine.getM_Product_ID() > 0)
			uom = MUOM.get(getCtx(),contractLine.getM_Product().getC_UOM_ID());
		else
			uom = MUOM.get(getCtx(),contractLine.getC_UOM_ID());

		//Set Qty
		if(isFirstPeriod && isLastPeriod)
		{
			contractPSInvoiceLine.setQtyEntered(contractLine.getQtyInvoiced());
			contractPSInvoiceLine.setC_UOM_ID(uom.getC_UOM_ID());
			contractPSInvoiceLine.setQtyInvoiced(contractLine.getQtyInvoiced());

		}else if(isFirstPeriod && !isLastPeriod){

			if(m_ContractContent.getJP_ContractProcDate_From().compareTo(contractProcPeriod.getStartDate()) == 0)
			{
				contractPSInvoiceLine.setQtyEntered(contractLine.getQtyInvoiced());
				contractPSInvoiceLine.setC_UOM_ID(uom.getC_UOM_ID());
				contractPSInvoiceLine.setQtyInvoiced(contractLine.getQtyInvoiced());

			}else {//per diem

				BigDecimal umerator = new BigDecimal(getContractDays(m_ContractContent.getJP_ContractProcDate_From(), contractProcPeriod.getEndDate()));
				BigDecimal denominator = new BigDecimal(contractProcPeriod.getContractProcPeriodDays());

				BigDecimal qtyEntered = umerator.divide(denominator, uom.getStdPrecision()+2, RoundingMode.HALF_UP).multiply(contractLine.getQtyInvoiced());
				qtyEntered = qtyEntered.divide(Env.ONE,uom.getStdPrecision(), RoundingMode.HALF_UP);
				contractPSInvoiceLine.setQtyEntered(qtyEntered);
				contractPSInvoiceLine.setC_UOM_ID(uom.getC_UOM_ID());
				contractPSInvoiceLine.setQtyInvoiced(qtyEntered);

			}

		}else if(!isFirstPeriod && isLastPeriod){

			if(m_ContractContent.getJP_ContractProcDate_To().compareTo(contractProcPeriod.getEndDate()) == 0)
			{
				contractPSInvoiceLine.setQtyEntered(contractLine.getQtyInvoiced());
				contractPSInvoiceLine.setC_UOM_ID(uom.getC_UOM_ID());
				contractPSInvoiceLine.setQtyInvoiced(contractLine.getQtyInvoiced());

			}else {//per diem

				BigDecimal umerator = new BigDecimal(getContractDays(contractProcPeriod.getStartDate(), m_ContractContent.getJP_ContractProcDate_To()));
				BigDecimal denominator = new BigDecimal(contractProcPeriod.getContractProcPeriodDays());

				BigDecimal qtyEntered = umerator.divide(denominator, uom.getStdPrecision()+2, RoundingMode.HALF_UP).multiply(contractLine.getQtyInvoiced());
				qtyEntered = qtyEntered.divide(Env.ONE,uom.getStdPrecision(), RoundingMode.HALF_UP);
				contractPSInvoiceLine.setQtyEntered(qtyEntered);
				contractPSInvoiceLine.setC_UOM_ID(uom.getC_UOM_ID());
				contractPSInvoiceLine.setQtyInvoiced(qtyEntered);

			}

		}else if(!isFirstPeriod && !isLastPeriod) {
			contractPSInvoiceLine.setQtyEntered(contractLine.getQtyInvoiced());
			contractPSInvoiceLine.setC_UOM_ID(uom.getC_UOM_ID());
			contractPSInvoiceLine.setQtyInvoiced(contractLine.getQtyInvoiced());
		}

		//Set Price
		contractPSInvoiceLine.setPriceEntered(contractPSLine.getPriceActual());
		contractPSInvoiceLine.setPriceActual(contractPSLine.getPriceActual());
		contractPSInvoiceLine.setLineNetAmt(contractPSInvoiceLine.getQtyInvoiced().multiply(contractPSInvoiceLine.getPriceActual()));

		try {
			contractPSInvoiceLine.saveEx(get_TrxName());
			createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_CreatedDocumentLine, contractLine, contractPSInvoiceLine, null);
		} catch (AdempiereException e) {
			createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SaveError, contractLine, contractPSLine, e.getMessage());
			throw e;
		}finally {
			;
		}

		return true;

	}//createContractPSInvoiceLine

	private long getContractDays(Timestamp startDate, Timestamp endDate)
	{
		return Duration.between(startDate.toLocalDateTime(), endDate.toLocalDateTime()).toDays();

	}//getContractDays


	private boolean isCreateContractPSLine(MContractLine contractLine, int JP_ContractProcPeriod_ID, boolean isCreateLog)
	{

		//Base Doc is Order
		if(m_ContractContent.getDocBaseType().equals(MContractContent.DOCBASETYPE_SalesOrder)
				|| m_ContractContent.getDocBaseType().equals(MContractContent.DOCBASETYPE_PurchaseOrder) )
		{
			String logMsg = AbstractContractProcess.getSkipReason_CreateBaseOrderLine(getCtx(), m_ContractContent, contractLine, JP_ContractProcPeriod_ID, true, get_TrxName());

			if(logMsg == null)
			{
				return true;

			}

			if(isCreateLog)
			{
				if (logMsg.equals(MContractLogDetail.JP_CONTRACTLOGMSG_SkippedContractProcessForOverlapContractProcessPeriod))
				{
					MOrderLine[] oLines = contractLine.getOrderLineByContractPeriod(getCtx(), JP_ContractProcPeriod_ID, get_TrxName());
					if(oLines != null && oLines.length > 0)
					{
						createContractLogDetail(logMsg, contractLine, oLines[0], null);
					}

				}else {

					createContractLogDetail(logMsg, contractLine, null, null);

				}
			}

			return false;

		}
		//Base Doc is Invoice
		else if(m_ContractContent.getDocBaseType().equals(MContractContent.DOCBASETYPE_ARInvoice)
				|| m_ContractContent.getDocBaseType().equals(MContractContent.DOCBASETYPE_APInvoice) )
		{

			String logMsg = AbstractContractProcess.getSkipReason_CreateBaseInvoiceLine(getCtx(), m_ContractContent, contractLine, JP_ContractProcPeriod_ID, true, get_TrxName());

			if(logMsg == null)
			{
				return true;

			}

			if(isCreateLog)
			{
				if (logMsg.equals(MContractLogDetail.JP_CONTRACTLOGMSG_SkippedContractProcessForOverlapContractProcessPeriod))
				{
					MInvoiceLine[] iLines = contractLine.getInvoiceLineByContractPeriod(getCtx(), JP_ContractProcPeriod_ID, get_TrxName());
					if(iLines != null && iLines.length > 0)
						createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SkippedContractProcessForOverlapContractProcessPeriod, contractLine, iLines[0], null);


				}else {

					createContractLogDetail(logMsg, contractLine, null, null);

				}
			}

			return false;


		}//Base Doc is Invoice

		return true;

	}//isCreateContractPSLine


	private boolean isCreateContractPSInOutLine(MContractPSLine contractPSLine, int JP_ContractProcPeriod_ID, boolean isCreateLog)
	{
		int JP_ContractLine_ID = contractPSLine.getJP_ContractLine_ID();
		if(JP_ContractLine_ID == 0)
			return false;

		MContractLine contractLine = MContractLine.get(getCtx(), JP_ContractLine_ID);


		String logMsg = getSkipReason_CreateDerivativeInOutLine(getCtx(), m_ContractContent, contractLine, null , JP_ContractProcPeriod_ID, getJP_ContractProcess_ID(), false, false, get_TrxName());

		if(logMsg == null)
		{
			return true;

		}

		if(isCreateLog)
		{
			if (logMsg.equals(MContractLogDetail.JP_CONTRACTLOGMSG_SkippedContractProcessForOverlapContractProcessPeriod))
			{
				//Check Overlap
				MInOutLine[] ioLines = contractLine.getInOutLineByContractPeriod(getCtx(), JP_ContractProcPeriod_ID, get_TrxName());
				if(ioLines != null && ioLines.length > 0)
				{
					createContractLogDetail(logMsg, contractLine, ioLines[0], null);

				}

			}else {

				createContractLogDetail(logMsg, contractLine, null, null);

			}

		}

		return false;

	}//isCreateContractPSInOutLine

	private boolean isCreateContractPSInvoiceLine(MContractPSLine contractPSLine, int JP_ContractProcPeriod_ID, boolean isCreateLog)
	{

		int JP_ContractLine_ID = contractPSLine.getJP_ContractLine_ID();
		if(JP_ContractLine_ID == 0)
			return false;

		MContractLine contractLine = MContractLine.get(getCtx(), JP_ContractLine_ID);


		String logMsg = getSkipReason_CreateDerivativeInvoiceLine(getCtx(), m_ContractContent, contractLine, null , JP_ContractProcPeriod_ID, getJP_ContractProcess_ID(), false, false, get_TrxName());

		if(logMsg == null)
		{
			return true;

		}

		if(isCreateLog)
		{
			if (logMsg.equals(MContractLogDetail.JP_CONTRACTLOGMSG_SkippedContractProcessForOverlapContractProcessPeriod))
			{
				//Check Overlap
				MInvoiceLine[] iLines = contractLine.getInvoiceLineByContractPeriod(getCtx(), JP_ContractProcPeriod_ID, get_TrxName());
				if(iLines != null && iLines.length > 0)
				{
					createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SkippedContractProcessForOverlapContractProcessPeriod, contractLine, iLines[0], null);
				}

			}else {

				createContractLogDetail(logMsg, contractLine, null, null);

			}

		}

		return false;


	}//isCreateContractPSInvoiceLine


	public int getDefaultContractPSDocType_ID ()
	{
		int JP_ContractPSDocType_ID = 0;
		String sql = "SELECT C_DocType_ID FROM C_DocType WHERE AD_Client_ID=? AND DocBaseType='JCS' AND IsDefault='Y' ORDER BY C_DocType_ID DESC";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (sql, null);
			pstmt.setInt (1, getAD_Client_ID());
			rs = pstmt.executeQuery ();
			if (rs.next ())
			{
				JP_ContractPSDocType_ID = rs.getInt(1);
			}
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, "getDefaultContractPSDocType", e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

		return JP_ContractPSDocType_ID;
	}	//	getDefaultContractPSDocType_ID
}
