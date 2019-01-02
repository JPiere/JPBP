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

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.logging.Level;

import org.compiere.model.MOrgInfo;
import org.compiere.model.MWarehouse;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.MContract;
import jpiere.base.plugin.org.adempiere.model.MContractCalender;
import jpiere.base.plugin.org.adempiere.model.MContractCalenderList;
import jpiere.base.plugin.org.adempiere.model.MContractCalenderRef;
import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractContentT;
import jpiere.base.plugin.org.adempiere.model.MContractLine;
import jpiere.base.plugin.org.adempiere.model.MContractLineT;
import jpiere.base.plugin.org.adempiere.model.MContractProcPeriod;
import jpiere.base.plugin.org.adempiere.model.MContractT;

/**
*  JPIERE-0363
*
* @author Hideaki Hagiwara
*
*/
public abstract class AbstractCreateContractFromTemplate extends SvrProcess {


	protected MContract m_Contract = null;
	protected MContractContent m_ContractContent = null;
	protected MContractT m_ContractTemplate = null;
	protected MContractContentT[] m_ContractContentTemplates = null;

	protected String p_JP_ContractTabLevel = null;
	protected  static final String JP_ContractTabLevel_Document  = "CD";
	protected  static final String JP_ContractTabLevel_Content  = "CC";


	int Record_ID = 0;


	@Override
	protected void prepare()
	{
		Record_ID = getRecord_ID();
		if(Record_ID > 0)
		{

			ProcessInfoParameter[] para = getParameter();
			for (int i = 0; i < para.length; i++)
			{
				String name = para[i].getParameterName();

				if (para[i].getParameter() == null)
				{
					;

				}else if (name.equals("JP_ContractTabLevel")){

					p_JP_ContractTabLevel = para[i].getParameterAsString();

				}else{
					log.log(Level.SEVERE, "Unknown Parameter: " + name);
				}//if
			}//for


			if(p_JP_ContractTabLevel.equals(JP_ContractTabLevel_Document))
			{
				m_Contract = new MContract(getCtx(), Record_ID, get_TrxName());
				m_ContractTemplate = new MContractT(getCtx(),m_Contract.getJP_ContractT_ID(), get_TrxName());
				m_ContractContentTemplates = m_ContractTemplate.getContractContentTemplates();

			}else if(p_JP_ContractTabLevel.equals(JP_ContractTabLevel_Content)){

				m_ContractContent = new MContractContent(getCtx(), Record_ID, get_TrxName());
				m_Contract = m_ContractContent.getParent();
			}


		}else{
			log.log(Level.SEVERE, "Record_ID <= 0 ");
		}
	}

	@Override
	protected String doIt() throws Exception
	{
		if(m_Contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_GeneralContract))
		{
			throw new Exception("JP_GeneralContractContent");//General Contract can not have Contract Content.
		}

		return Msg.getMsg(getCtx(), "Success");
	}

	protected Timestamp calculateDate(Timestamp baseDate, int addNum)
	{
		LocalDateTime datePromisedLocal = baseDate.toLocalDateTime();
		datePromisedLocal = datePromisedLocal.plusDays(addNum);

		return Timestamp.valueOf(datePromisedLocal);
	}

	protected void setContractContentProcDate(MContractContent contractContent, MContractContentT contentTemplate)
	{
		//Set Contract Calender
		int JP_ContractCalenderRef_ID = contentTemplate.getJP_ContractCalenderRef_ID();
		if(JP_ContractCalenderRef_ID > 0)
		{
			MContractCalenderRef  contractCalenderRef = MContractCalenderRef.get(getCtx(), JP_ContractCalenderRef_ID);
			MContractCalenderList[] contractCalenderLists = contractCalenderRef.getContractCalenderList(getCtx(), true, get_TrxName());
			if(contractCalenderLists.length==1)
				contractContent.setJP_ContractCalender_ID(contractCalenderLists[0].getJP_ContractCalender_ID());
		}

		//Set JP_ContractProcDate_From
		if(contentTemplate.getJP_ContractProcPOffset() == 0)
		{
			contractContent.setJP_ContractProcDate_From(contractContent.getParent().getJP_ContractPeriodDate_From());

		}else{

			if(contractContent.getJP_ContractCalender_ID() > 0)
			{
				MContractCalender calender = MContractCalender.get(getCtx(), contractContent.getJP_ContractCalender_ID());
				MContractProcPeriod period = calender.getContractProcessPeriod(getCtx(), contractContent.getParent().getJP_ContractPeriodDate_From(), null, contentTemplate.getJP_ContractProcPOffset());
				contractContent.setJP_ContractProcDate_From(period.getStartDate());

			} else if(contractContent.getJP_ContractProcDate_From() == null) {

				contractContent.setJP_ContractProcDate_From(contractContent.getParent().getJP_ContractPeriodDate_From());

			}

		}

		//Set JP_ContractProcDate_To
		if(contractContent.getJP_ContractCalender_ID() > 0)
		{
			MContractCalender calender = MContractCalender.get(getCtx(), contractContent.getJP_ContractCalender_ID());
			MContractProcPeriod period = calender.getContractProcessPeriod(getCtx(), contractContent.getJP_ContractProcDate_From(), null, contentTemplate.getJP_ContractProcPeriodNum());
			if(contractContent.getParent().getJP_ContractPeriodDate_To() == null)
			{
				if(contentTemplate.getJP_ContractProcPeriodNum() == 0)
				{
					if(contentTemplate.getJP_ContractProcessMethod().equals(MContractContentT.JP_CONTRACTPROCESSMETHOD_IndirectContractProcess))
					{
						contractContent.setJP_ContractProcDate_To(period.getEndDate());
					}else {
						contractContent.setJP_ContractProcDate_To(null);
					}

				}else {

					contractContent.setJP_ContractProcDate_To(period.getEndDate());
				}

			}else{

				if(contractContent.getParent().getJP_ContractPeriodDate_To().compareTo(period.getEndDate()) >= 0)
				{
					contractContent.setJP_ContractProcDate_To(period.getEndDate());

				}else{

					contractContent.setJP_ContractProcDate_To(contractContent.getParent().getJP_ContractPeriodDate_To());
				}

			}

		}else {

			if(contractContent.getParent().getJP_ContractPeriodDate_To() != null)
			{
				contractContent.setJP_ContractProcDate_To(contractContent.getParent().getJP_ContractPeriodDate_To());

			}else {

				if(contentTemplate.getJP_ContractProcessMethod().equals(MContractContentT.JP_CONTRACTPROCESSMETHOD_IndirectContractProcess))
				{
					contractContent.setJP_ContractProcDate_To(contractContent.getJP_ContractProcDate_From());
				}else {
					contractContent.setJP_ContractProcDate_To(null);
				}
			}
		}

	}

	protected void setBaseDocLineProcPeriod(MContractLine contractLine, MContractLineT lineTemplate)
	{

		if(Util.isEmpty(contractLine.getJP_BaseDocLinePolicy()))
		{
			contractLine.setJP_ProcPeriod_Lump_ID(0);
			contractLine.setJP_ProcPeriod_Start_ID(0);
			contractLine.setJP_ProcPeriod_End_ID(0);
			return ;
		}

		if(contractLine.getJP_BaseDocLinePolicy().equals("LP"))
		{
			int processPeriodOffset = lineTemplate.getJP_ProcPeriodOffs_Lump();
			if(processPeriodOffset > 0)
				processPeriodOffset++;
			else
				processPeriodOffset--;


			MContractCalender calender = MContractCalender.get(getCtx(), contractLine.getParent().getJP_ContractCalender_ID());
			MContractProcPeriod period = calender.getContractProcessPeriod(getCtx(),contractLine.getParent().getJP_ContractProcDate_From(), null , processPeriodOffset);
			if(period != null)
				contractLine.setJP_ProcPeriod_Lump_ID(period.getJP_ContractProcPeriod_ID());
		}

		if(contractLine.getJP_BaseDocLinePolicy().equals("PS") || contractLine.getJP_BaseDocLinePolicy().equals("PB"))
		{
			int processPeriodOffset = lineTemplate.getJP_ProcPeriodOffs_Start();
			if(processPeriodOffset > 0)
				processPeriodOffset++;
			else
				processPeriodOffset--;


			MContractCalender calender = MContractCalender.get(getCtx(), contractLine.getParent().getJP_ContractCalender_ID());
			MContractProcPeriod period = calender.getContractProcessPeriod(getCtx(),contractLine.getParent().getJP_ContractProcDate_From(), null , processPeriodOffset);
			if(period != null)
				contractLine.setJP_ProcPeriod_Start_ID(period.getJP_ContractProcPeriod_ID());
		}

		if(contractLine.getJP_BaseDocLinePolicy().equals("PE") || contractLine.getJP_BaseDocLinePolicy().equals("PB"))
		{
			int processPeriodOffset = lineTemplate.getJP_ProcPeriodOffs_End();
			if(processPeriodOffset > 0)
				processPeriodOffset++;
			else
				processPeriodOffset--;

			MContractCalender calender = MContractCalender.get(getCtx(), contractLine.getParent().getJP_ContractCalender_ID());
			MContractProcPeriod period = calender.getContractProcessPeriod(getCtx(),contractLine.getParent().getJP_ContractProcDate_From(), null , processPeriodOffset);
			if(period != null)
				contractLine.setJP_ProcPeriod_End_ID(period.getJP_ContractProcPeriod_ID());
		}
	}

	protected void setDerivativeInOutLineProcPeriod(MContractLine contractLine, MContractLineT lineTemplate)
	{
		if(!contractLine.getParent().getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceipt)
				&& !contractLine.getParent().getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceiptInvoice))
		{
			contractLine.setJP_ContractCalender_InOut_ID(0);
			contractLine.setJP_ProcPeriod_Lump_InOut_ID(0);
			contractLine.setJP_ProcPeriod_Start_InOut_ID(0);
			contractLine.setJP_ProcPeriod_End_InOut_ID(0);
			return ;
		}

		if(contractLine.getJP_ContractCalender_InOut_ID() == 0)
		{
			int JP_ContractCalRef_InOut_ID = lineTemplate.getJP_ContractCalRef_InOut_ID();
			MContractCalenderRef  contractCalenderRef = MContractCalenderRef.get(getCtx(), JP_ContractCalRef_InOut_ID);
			MContractCalenderList[] contractCalenderLists = contractCalenderRef.getContractCalenderList(getCtx(), true, get_TrxName());
			if(contractCalenderLists.length==1)
			{
				contractLine.setJP_ContractCalender_InOut_ID(contractCalenderLists[0].getJP_ContractCalender_ID());
			}
		}


		if(contractLine.getJP_ContractCalender_InOut_ID() != 0)
		{
			if(contractLine.getJP_DerivativeDocPolicy_InOut().equals("LP"))
			{

				int processPeriodOffset = lineTemplate.getJP_ProcPeriodOffs_Lump_InOut();
				if(processPeriodOffset > 0)
					processPeriodOffset++;
				else
					processPeriodOffset--;

				MContractCalender calender = MContractCalender.get(getCtx(), contractLine.getJP_ContractCalender_InOut_ID());
				MContractProcPeriod period = calender.getContractProcessPeriod(getCtx(), contractLine.getParent().getJP_ContractProcDate_From() , null ,processPeriodOffset);
				if(period != null)
					contractLine.setJP_ProcPeriod_Lump_InOut_ID(period.getJP_ContractProcPeriod_ID());

			}

			if(contractLine.getJP_DerivativeDocPolicy_InOut().equals("PS") || contractLine.getJP_DerivativeDocPolicy_InOut().equals("PB"))
			{
				int processPeriodOffset = lineTemplate.getJP_ProcPeriodOffs_Start_InOut();
				if(processPeriodOffset > 0)
					processPeriodOffset++;
				else
					processPeriodOffset--;

				MContractCalender calender = MContractCalender.get(getCtx(), contractLine.getJP_ContractCalender_InOut_ID());
				MContractProcPeriod period = calender.getContractProcessPeriod(getCtx(), contractLine.getParent().getJP_ContractProcDate_From() , null ,processPeriodOffset);
				if(period != null)
					contractLine.setJP_ProcPeriod_Start_InOut_ID(period.getJP_ContractProcPeriod_ID());
			}

			if(contractLine.getJP_DerivativeDocPolicy_InOut().equals("PE") || contractLine.getJP_DerivativeDocPolicy_InOut().equals("PB"))
			{
				int processPeriodOffset = lineTemplate.getJP_ProcPeriodOffs_End_InOut();
				if(processPeriodOffset > 0)
					processPeriodOffset++;
				else
					processPeriodOffset--;

				MContractCalender calender = MContractCalender.get(getCtx(), contractLine.getJP_ContractCalender_InOut_ID());
				MContractProcPeriod period = calender.getContractProcessPeriod(getCtx(), contractLine.getParent().getJP_ContractProcDate_From() , null ,processPeriodOffset);
				if(period != null)
					contractLine.setJP_ProcPeriod_End_InOut_ID(period.getJP_ContractProcPeriod_ID());

			}
		}

	}//setDerivativeInOutLineProcPeriod

	protected void setDerivativeInvoiceLineProcPeriod(MContractLine contractLine, MContractLineT lineTemplate)
	{

		if(!contractLine.getParent().getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateInvoice)
				&& !contractLine.getParent().getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceiptInvoice))
		{
			contractLine.setJP_ContractCalender_Inv_ID(0);
			contractLine.setJP_ProcPeriod_Lump_Inv_ID(0);
			contractLine.setJP_ProcPeriod_Start_Inv_ID(0);
			contractLine.setJP_ProcPeriod_End_Inv_ID(0);
			return ;
		}

		if(contractLine.getJP_ContractCalender_Inv_ID() == 0)
		{
			int JP_ContractCalRef_Inv_ID = lineTemplate.getJP_ContractCalRef_Inv_ID();

			MContractCalenderRef  contractCalenderRef = MContractCalenderRef.get(getCtx(), JP_ContractCalRef_Inv_ID);
			MContractCalenderList[] contractCalenderLists = contractCalenderRef.getContractCalenderList(getCtx(), true, get_TrxName());
			if(contractCalenderLists.length==1)
			{
				contractLine.setJP_ContractCalender_Inv_ID(contractCalenderLists[0].getJP_ContractCalender_ID());
			}
		}


		if(contractLine.getJP_ContractCalender_Inv_ID() != 0)
		{
			if(contractLine.getJP_DerivativeDocPolicy_Inv().equals("LP"))
			{
				int processPeriodOffset = lineTemplate.getJP_ProcPeriodOffs_Lump_Inv();
				if(processPeriodOffset > 0)
					processPeriodOffset++;
				else
					processPeriodOffset--;

				MContractCalender calender = MContractCalender.get(getCtx(), contractLine.getJP_ContractCalender_Inv_ID());
				MContractProcPeriod period = calender.getContractProcessPeriod(getCtx(), contractLine.getParent().getJP_ContractProcDate_From() , null ,processPeriodOffset);
				if(period != null)
					contractLine.setJP_ProcPeriod_Lump_Inv_ID(period.getJP_ContractProcPeriod_ID());

			}

			if(contractLine.getJP_DerivativeDocPolicy_Inv().equals("PS") || contractLine.getJP_DerivativeDocPolicy_Inv().equals("PB"))
			{
				int processPeriodOffset = lineTemplate.getJP_ProcPeriodOffs_Start_Inv();
				if(processPeriodOffset > 0)
					processPeriodOffset++;
				else
					processPeriodOffset--;

				MContractCalender calender = MContractCalender.get(getCtx(), contractLine.getJP_ContractCalender_Inv_ID());
				MContractProcPeriod period = calender.getContractProcessPeriod(getCtx(),contractLine.getParent().getJP_ContractProcDate_From() , null ,processPeriodOffset);
				if(period != null)
					contractLine.setJP_ProcPeriod_Start_Inv_ID(period.getJP_ContractProcPeriod_ID());
			}

			if(contractLine.getJP_DerivativeDocPolicy_Inv().equals("PE") || contractLine.getJP_DerivativeDocPolicy_Inv().equals("PB"))
			{
				int processPeriodOffset = lineTemplate.getJP_ProcPeriodOffs_End_Inv();
				if(processPeriodOffset > 0)
					processPeriodOffset++;
				else
					processPeriodOffset--;

				MContractCalender calender = MContractCalender.get(getCtx(), contractLine.getJP_ContractCalender_Inv_ID());
				MContractProcPeriod period = calender.getContractProcessPeriod(getCtx(), contractLine.getParent().getJP_ContractProcDate_From() , null ,processPeriodOffset);
				if(period != null)
					contractLine.setJP_ProcPeriod_End_Inv_ID(period.getJP_ContractProcPeriod_ID());

			}
		}

	}//setDerivativeInvoiceLineProcPeriod

	protected void setWarehouseOfContractContent(MContractContentT from, MContractContent to) throws Exception
	{
		if(from.getDocBaseType().equals(MContractContent.DOCBASETYPE_APInvoice)
				|| from.getDocBaseType().equals(MContractContent.DOCBASETYPE_ARInvoice) )
			return ;

		if(from.getM_Warehouse() != null && from.getM_Warehouse().getAD_Org_ID() == to.getAD_Org_ID())
		{
			to.setM_Warehouse_ID(from.getM_Warehouse_ID());

		}else{

			if(MOrgInfo.get(null, to.getAD_Org_ID(),get_TrxName()).getM_Warehouse_ID() != 0)
			{
				to.setM_Warehouse_ID(MOrgInfo.get(null, to.getAD_Org_ID(),get_TrxName()).getM_Warehouse_ID());

			}else{

				MWarehouse[] warehouses =  MWarehouse.getForOrg(getCtx(), from.getAD_Org_ID());
				if(warehouses.length > 0)
				{
					to.setM_Warehouse_ID(warehouses[0].getM_Warehouse_ID());

				}else {

					throw new Exception(Msg.getMsg(Env.getCtx(),"NotFound") + Msg.getElement(getCtx(), "M_Warehouse_ID") + " : "
										+ Msg.getElement(getCtx(), "JP_ContractContent_ID") + " - "+  to.getDocumentNo());

				}

			}

		}
	}

}