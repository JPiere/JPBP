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
package jpiere.base.plugin.org.adempiere.base;

import java.math.BigDecimal;
import java.util.List;

import org.adempiere.webui.window.FDialog;
import org.compiere.acct.Fact;
import org.compiere.acct.FactLine;
import org.compiere.model.FactsValidator;
import org.compiere.model.I_C_InvoiceLine;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MClient;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MOrder;
import org.compiere.model.MRMA;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfo;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.MContract;
import jpiere.base.plugin.org.adempiere.model.MContractAcct;
import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractLine;
import jpiere.base.plugin.org.adempiere.model.MContractProcPeriod;
import jpiere.base.plugin.org.adempiere.model.MRecognition;
import jpiere.base.plugin.org.adempiere.model.MRecognitionLine;



/**
 *  JPIERE-0363: Contract Management
 *  JPiere Contract Invoice Validator
 *
 *  @author  Hideaki Hagiwara（h.hagiwara@oss-erp.co.jp）
 *
 */
public class JPiereContractInvoiceValidator extends AbstractContractValidator  implements ModelValidator,FactsValidator {

	private static CLogger log = CLogger.getCLogger(JPiereContractInvoiceValidator.class);
	private int AD_Client_ID = -1;
	private int AD_Org_ID = -1;
	private int AD_Role_ID = -1;
	private int AD_User_ID = -1;


	@Override
	public void initialize(ModelValidationEngine engine, MClient client)
	{
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();
		engine.addModelChange(MInvoice.Table_Name, this);
		engine.addModelChange(MInvoiceLine.Table_Name, this);
		engine.addDocValidate(MInvoice.Table_Name, this);
		engine.addFactsValidate(MInvoice.Table_Name, this);

	}

	@Override
	public int getAD_Client_ID() {

		return AD_Client_ID;
	}

	@Override
	public String login(int AD_Org_ID, int AD_Role_ID, int AD_User_ID) {
		this.AD_Org_ID = AD_Org_ID;
		this.AD_Role_ID = AD_Role_ID;
		this.AD_User_ID = AD_User_ID;

		return null;
	}

	@Override
	public String modelChange(PO po, int type) throws Exception
	{
		if(po.get_TableName().equals(MInvoice.Table_Name))
		{
			return invoiceValidate(po, type);

		}else if(po.get_TableName().equals(MInvoiceLine.Table_Name)){

			return invoiceLineValidate(po, type);
		}

		return null;
	}

	@Override
	public String docValidate(PO po, int timing)
	{
		if(timing == ModelValidator.TIMING_BEFORE_PREPARE)
		{
			MInvoice invoice = (MInvoice)po;
			int JP_Contract_ID = invoice.get_ValueAsInt("JP_Contract_ID");
			if(JP_Contract_ID <= 0)
				return null;

			MContract contract = MContract.get(Env.getCtx(), JP_Contract_ID);
			if(contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
			{
				int JP_ContractContent_ID = invoice.get_ValueAsInt("JP_ContractContent_ID");
				MContractContent content = MContractContent.get(Env.getCtx(), JP_ContractContent_ID);

				//Check Mandetory - JP_ContractProcPeriod_ID
				MInvoiceLine[] lines = invoice.getLines();
				int JP_ContractLine_ID = 0;
				int JP_ContractProcPeriod_ID = 0;
				for(int i = 0; i < lines.length; i++)
				{
					JP_ContractLine_ID = lines[i].get_ValueAsInt("JP_ContractLine_ID");
					JP_ContractProcPeriod_ID = lines[i].get_ValueAsInt("JP_ContractProcPeriod_ID");
					if(JP_ContractLine_ID > 0 && JP_ContractProcPeriod_ID <= 0)
					{
						if(content.getDocBaseType().equals("API") ||  content.getDocBaseType().equals("ARI")
								|| (content.getJP_CreateDerivativeDocPolicy() != null &&
								        ( content.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateInvoice)
										||content.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceiptInvoice)) ) )
						{
							return Msg.getMsg(Env.getCtx(), "FillMandatory") + Msg.getElement(Env.getCtx(), MContractProcPeriod.COLUMNNAME_JP_ContractProcPeriod_ID)
														+ " - " + Msg.getElement(Env.getCtx(),  MInvoiceLine.COLUMNNAME_Line) + " : " + lines[i].getLine();
						}
					}
				}

			}

		}//TIMING_BEFORE_PREPARE

		if(timing == ModelValidator.TIMING_AFTER_VOID
				|| timing == ModelValidator.TIMING_AFTER_REVERSEACCRUAL
				|| timing == ModelValidator.TIMING_AFTER_REVERSECORRECT)
		{
			MInvoice invoice = (MInvoice)po;
			int JP_ContractContent_ID = invoice.get_ValueAsInt("JP_ContractContent_ID");
			if(JP_ContractContent_ID > 0)
			{
				MContractContent content = MContractContent.get(Env.getCtx(), JP_ContractContent_ID);
				MContractAcct acctInfo = MContractAcct.get(Env.getCtx(), content.getJP_Contract_Acct_ID());
				if(acctInfo.isPostingContractAcctJP() && acctInfo.isPostingContractAcctJP()
						&& acctInfo.getJP_RecogToInvoicePolicy() != null
						&& !acctInfo.getJP_RecogToInvoicePolicy().equals(MContractAcct.JP_RECOGTOINVOICEPOLICY_NotCreateInvoiceFromRecognition))
				{

					StringBuilder sql = new StringBuilder("UPDATE ")
					.append(MRecognition.Table_Name)
					.append(" SET C_Invoice_ID = null WHERE C_Invoice_ID=?");
					DB.executeUpdate(sql.toString(), invoice.getC_Invoice_ID(), po.get_TrxName());

					MInvoiceLine[] iLines = invoice.getLines();
					for(int i = 0; i < iLines.length; i++)
					{
						StringBuilder sqLine = new StringBuilder("UPDATE ")
						.append(MRecognitionLine.Table_Name)
						.append(" SET C_InvoiceLine_ID = null WHERE C_InvoiceLine_ID=?");
						DB.executeUpdate(sqLine.toString(), iLines[i].getC_InvoiceLine_ID(), po.get_TrxName());
					}//for i
				}
			}//if(JP_ContractContent_ID > 0)

		}//Void

		if(timing == ModelValidator.TIMING_BEFORE_CLOSE)
		{
			int JP_ContractContent_ID = po.get_ValueAsInt("JP_ContractContent_ID");
			if(JP_ContractContent_ID > 0 )
			{
				MContractContent content = MContractContent.get(Env.getCtx(), JP_ContractContent_ID);
				int JP_Contract_Acct_ID = content.getJP_Contract_Acct_ID();
				if(JP_Contract_Acct_ID > 0)
				{
					MContractAcct acct = MContractAcct.get(Env.getCtx(), JP_Contract_Acct_ID);
					if(acct.isPostingContractAcctJP() && acct.isPostingRecognitionDocJP() &&
							acct.getJP_RecogToInvoicePolicy() != null && acct.getJP_RecogToInvoicePolicy().equals(MContractAcct.JP_RECOGTOINVOICEPOLICY_AfterRecognition))
					{
						int JP_Recognition_ID = po.get_ValueAsInt("JP_Recognition_ID");
						MRecognition recog = new MRecognition(Env.getCtx(), JP_Recognition_ID , po.get_TrxName());
						if(!recog.getDocStatus().equals(DocAction.STATUS_Closed))
						{
							//In case of Policy of Create Invoice From Recognition is After Recognition, You have to close Recognition Doc, before Invoice doc close.
							String msg = Msg.getMsg(Env.getCtx(), "JP_AfterRecognition_InvoiceDocStatusCloseError");
							return msg;
						}
					}

				}

			}

		}//Close

		return null;
	}


	/**
	 * Invoice Header Validate
	 *
	 * @param po
	 * @param type
	 * @return
	 */
	private String invoiceValidate(PO po, int type)
	{

		//Check Derivative Doc
		if(po.get_ValueAsInt("C_Order_ID") > 0 || po.get_ValueAsInt("M_RMA_ID") > 0 )
		{
			String msg = derivativeDocHeaderCommonCheck(po, type);
			if(!Util.isEmpty(msg))
				return msg;

			return null;
		}


		//Check Base Doc
		if( type == ModelValidator.TYPE_BEFORE_NEW
				||( type == ModelValidator.TYPE_BEFORE_CHANGE && ( po.is_ValueChanged(MContract.COLUMNNAME_JP_Contract_ID)
																	||   po.is_ValueChanged(MContractContent.COLUMNNAME_JP_ContractContent_ID)
																	||   po.is_ValueChanged(MContractProcPeriod.COLUMNNAME_JP_ContractProcPeriod_ID)
																	||   po.is_ValueChanged(MOrder.COLUMNNAME_C_DocTypeTarget_ID) ) ) )
		{

			MInvoice invoice = (MInvoice)po;
			ProcessInfo pInfo = Env.getProcessInfo(Env.getCtx());

			//Check Contract Info
			int JP_Contract_ID = invoice.get_ValueAsInt(MContract.COLUMNNAME_JP_Contract_ID);
			if(JP_Contract_ID <= 0)
			{
				if(invoice.get_ValueAsInt("JP_ContractContent_ID") != 0
						|| invoice.get_ValueAsInt("JP_ContractProcPeriod_ID") != 0)
				{
					invoice.set_ValueNoCheck("JP_ContractContent_ID", null);
					invoice.set_ValueNoCheck("JP_ContractProcPeriod_ID", null);					
					if(pInfo == null)
					{
						FDialog.info(0, null, "契約管理情報", "契約内容、契約処理期間は入力できません - 契約書が入力されていないため");//TODO メッセージ化
					}
				}
				
				return null;
			}

			//Check to Change Contract Info
			MContract contract = MContract.get(Env.getCtx(), JP_Contract_ID);
			if(type == ModelValidator.TYPE_BEFORE_CHANGE && contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
			{
				MInvoiceLine[] contractInvoiceLines = getInvoiceLinesWithContractLine(invoice);
				if(contractInvoiceLines.length > 0)
				{
					//Contract Info can not be changed because the document contains contract Info lines.
					String msg = Msg.getMsg(Env.getCtx(), "JP_CannotChangeContractInfoForLines");
					return msg;
				}
			}


			//Check Period Contract
			if(contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
			{
				/**
				 * Check JP_ContractContent_ID
				 * Mandetory Period Contract AND Spot Contract.
				 * In case of General Contract, JP_ContractContent_ID should be null;
				 */
				int JP_ContractContent_ID = invoice.get_ValueAsInt(MContractContent.COLUMNNAME_JP_ContractContent_ID);
				if(JP_ContractContent_ID <= 0)
				{
					Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractContent_ID")};
					return Msg.getMsg(Env.getCtx(), "JP_InCaseOfPeriodContract") + Msg.getMsg(Env.getCtx(),"JP_Mandatory", objs);

				}else{

					MContractContent content = MContractContent.get(Env.getCtx(), JP_ContractContent_ID);

					//Check Contract
					if(contract.getJP_Contract_ID() != content.getJP_Contract_ID())
					{
						//You selected different contract Document.
						return Msg.getMsg(Env.getCtx(), "JP_Diff_ContractDocument");
					}

					//Check BP
					if(content.getC_BPartner_ID() != invoice.getC_BPartner_ID())
					{
						//Different business partner between Contract Content and Document.
						return Msg.getMsg(Env.getCtx(), "JP_DifferentBusinessPartner_ContractContent");
					}

					//Check Doc Type comment out because Interruption in case of ARC and APC
//					if(content.getJP_BaseDocDocType_ID() != invoice.getC_DocTypeTarget_ID())
//					{
//						MDocType docType = MDocType.get(Env.getCtx(), content.getJP_BaseDocDocType_ID());
//						//Please select the Document Type that is same as Contract content.
//						return Msg.getMsg(Env.getCtx(), "JP_SelectDocTypeSameAsContractContent")  + " -> " + docType.getNameTrl();
//					}


					/**
					 * Check JP_ContractProcPeriod_ID
					 *  Mandetory Period Contract
					 *  In case of Spot Contract or General Contract, JP_ContractProcPeriod_ID should be null;
					 */
					int JP_ContractProcPeriod_ID = invoice.get_ValueAsInt(MContractProcPeriod.COLUMNNAME_JP_ContractProcPeriod_ID);
					if(JP_ContractProcPeriod_ID <= 0)
					{
						Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractProcPeriod_ID")};
						return Msg.getMsg(Env.getCtx(), "JP_InCaseOfPeriodContract") + Msg.getMsg(Env.getCtx(),"JP_Mandatory", objs);

					}else{

						MContractProcPeriod docContractProcPeriod = MContractProcPeriod.get(Env.getCtx(), JP_ContractProcPeriod_ID);

						//Check Contract Calender
						if(content.getJP_ContractCalender_ID() != docContractProcPeriod.getJP_ContractCalender_ID() )
						{
							//Contract Calender that belong to selected contract period does not accord with Contract Calender of Contract content.
							return Msg.getMsg(Env.getCtx(), "JP_DifferentContractCalender");
						}

						//Check Contain Contract Period
						if(content.getJP_ContractProcDate_To() == null)
						{
							if(content.getJP_ContractProcDate_From().compareTo(docContractProcPeriod.getEndDate()) <= 0)
							{
								;//This is OK
							}else{
								//Outside the Contract Process Period.
								return Msg.getMsg(Env.getCtx(), "JP_OutsideContractProcessPeriod") + " " + Msg.getMsg(Env.getCtx(), "Invalid") + Msg.getElement(Env.getCtx(), "JP_ContractProcPeriod_ID");
							}
						}else{
							if(content.getJP_ContractProcDate_From().compareTo(docContractProcPeriod.getEndDate()) <= 0
									&& (content.getJP_ContractProcDate_To().compareTo(docContractProcPeriod.getStartDate()) >= 0) )
							{
								;//This is OK
							}else{
								//Outside the Contract Process Period.
								return Msg.getMsg(Env.getCtx(), "JP_OutsideContractProcessPeriod") + " " + Msg.getMsg(Env.getCtx(), "Invalid") + Msg.getElement(Env.getCtx(), "JP_ContractProcPeriod_ID");
							}
						}
					}
				}

			//Check Spot Contract
			}else if(contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_SpotContract)){

				/**
				 * Check JP_ContractContent_ID
				 * Mandetory Period Contract AND Spot Contract.
				 * In case of General Contract, JP_ContractContent_ID should be null;
				 */
				int JP_ContractContent_ID = invoice.get_ValueAsInt(MContractContent.COLUMNNAME_JP_ContractContent_ID);
				if(JP_ContractContent_ID <= 0)
				{
					Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractContent_ID")};
					return Msg.getMsg(Env.getCtx(), "JP_InCaseOfSpotContract") + Msg.getMsg(Env.getCtx(),"JP_Mandatory", objs);

				}else{

					MContractContent content = MContractContent.get(Env.getCtx(), JP_ContractContent_ID);

					//Check Contract
					if(contract.getJP_Contract_ID() != content.getJP_Contract_ID())
					{
						//You selected different contract Document.
						return Msg.getMsg(Env.getCtx(), "JP_Diff_ContractDocument");
					}

					//Check BP
					if(content.getC_BPartner_ID() != invoice.getC_BPartner_ID())
					{
						//Different business partner between Contract Content and Document.
						return Msg.getMsg(Env.getCtx(), "JP_DifferentBusinessPartner_ContractContent");
					}

					//Check Doc Type comment out because Interruption in case of ARC and APC
//					if(content.getJP_BaseDocDocType_ID() != invoice.getC_DocTypeTarget_ID())
//					{
//						MDocType docType = MDocType.get(Env.getCtx(), content.getJP_BaseDocDocType_ID());
//						//Please select the Document Type that is same as Contract content.
//						return Msg.getMsg(Env.getCtx(), "JP_SelectDocTypeSameAsContractContent")  + " -> " + docType.getNameTrl();
//					}
				}

				/** In case of Spot Contract or General Contract, JP_ContractProcPeriod_ID should be null; */
				if(invoice.get_ValueAsInt("JP_ContractProcPeriod_ID") != 0)
				{
					invoice.set_ValueNoCheck("JP_ContractProcPeriod_ID", null);
					if(pInfo == null)
					{
						FDialog.info(0, null, "契約管理情報", "契約処理期間は入力できません -> スポット契約");//TODO メッセージ化
					}
				}

			//Check General Contract
			}else if(contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_GeneralContract)){

				/** In case of General Contract, JP_ContractContent_ID AND JP_ContractProcPeriod_ID should be null;*/
				if(invoice.get_ValueAsInt("JP_ContractContent_ID") != 0
						|| invoice.get_ValueAsInt("JP_ContractProcPeriod_ID") != 0)
				{
					invoice.set_ValueNoCheck("JP_ContractContent_ID", null);
					invoice.set_ValueNoCheck("JP_ContractProcPeriod_ID", null);
					if(pInfo == null)
					{
						FDialog.info(0, null, "契約管理情報", "契約内容、契約処理期間は入力できません -> 一般契約");//TODO メッセージ化
					}
				}
			}

		}//Check Base Doc


		//Check Delete
		if( type == ModelValidator.TYPE_AFTER_DELETE)
		{
			MInvoice invoice = (MInvoice)po;
			int JP_ContractContent_ID = invoice.get_ValueAsInt("JP_ContractContent_ID");
			if(JP_ContractContent_ID > 0)
			{
				MContractContent content = MContractContent.get(Env.getCtx(), JP_ContractContent_ID);
				MContractAcct acctInfo = MContractAcct.get(Env.getCtx(), content.getJP_Contract_Acct_ID());
				if(acctInfo.isPostingContractAcctJP() && acctInfo.isPostingContractAcctJP()
						&& acctInfo.getJP_RecogToInvoicePolicy() != null
						&& !acctInfo.getJP_RecogToInvoicePolicy().equals(MContractAcct.JP_RECOGTOINVOICEPOLICY_NotCreateInvoiceFromRecognition))
				{

					StringBuilder sql = new StringBuilder("UPDATE ")
					.append(MRecognition.Table_Name)
					.append(" SET C_Invoice_ID = null WHERE C_Invoice_ID=?");
					DB.executeUpdate(sql.toString(), invoice.getC_Invoice_ID(), po.get_TrxName());

					MInvoiceLine[] iLines = invoice.getLines();
					for(int i = 0; i < iLines.length; i++)
					{
						StringBuilder sqLine = new StringBuilder("UPDATE ")
						.append(MRecognitionLine.Table_Name)
						.append(" SET C_InvoiceLine_ID = null WHERE C_InvoiceLine_ID=?");
						DB.executeUpdate(sqLine.toString(), iLines[i].getC_InvoiceLine_ID(), po.get_TrxName());
					}//for i
				}
			}//if(JP_ContractContent_ID > 0)

		}

		return null;

	}

	/**
	 * Invoice Line Validate
	 *
	 * @param po
	 * @param type
	 * @return
	 */
	private String invoiceLineValidate(PO po, int type)
	{
		//Check Derivative Contract
		if(po.get_ValueAsInt("C_OrderLine_ID") > 0 || po.get_ValueAsInt("M_RMALine_ID") > 0)
		{
			String msg = derivativeDocLineCommonCheck(po, type);
			if(!Util.isEmpty(msg))
				return msg;

			/** Ref:JPiereContractInOutValidator AND JPiereContractRecognitionValidator*/
			if(type == ModelValidator.TYPE_BEFORE_NEW
					||( type == ModelValidator.TYPE_BEFORE_CHANGE && ( po.is_ValueChanged(MContractLine.COLUMNNAME_JP_ContractLine_ID)
							||   po.is_ValueChanged("C_OrderLine_ID") ||   po.is_ValueChanged("M_RMALine_ID") ) ))
			{
				MInvoiceLine invoiceLine = (MInvoiceLine)po;
				int JP_Contract_ID = invoiceLine.getParent().get_ValueAsInt("JP_Contract_ID");
				int JP_ContractContent_ID = invoiceLine.getParent().get_ValueAsInt("JP_ContractContent_ID");
				int JP_ContractLine_ID = invoiceLine.get_ValueAsInt("JP_ContractLine_ID");

				if(JP_Contract_ID <= 0)
					return null;

				MContract contract = MContract.get(Env.getCtx(), JP_Contract_ID);
				if(!contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract)
						&& !contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_SpotContract))
					return null;


				//Check Period Contract & Spot Contract fron now on
				int C_OrderLine_ID = invoiceLine.getC_OrderLine_ID();
				int M_RMALine_ID = invoiceLine.getM_RMALine_ID();
				if(C_OrderLine_ID <= 0 && M_RMALine_ID <= 0)
					return null;

				//Check Single Order or RMA
				if(invoiceLine.getParent().getC_Order_ID() > 0 && invoiceLine.getC_OrderLine_ID() > 0)
				{
					//You can not bundle different Order document.
					if(invoiceLine.getC_OrderLine().getC_Order_ID() != invoiceLine.getParent().getC_Order_ID())
						return Msg.getMsg(Env.getCtx(), "JP_InCaseOfPeriodContractAndSpotContract") + Msg.getMsg(Env.getCtx(),"JP_CanNotBundleDifferentOrder");

				}else if(invoiceLine.getParent().getM_RMA_ID() > 0 && invoiceLine.getM_RMALine_ID() > 0){

					//You can not bundle different RMA document.
					if(invoiceLine.getM_RMALine().getM_RMA_ID() != invoiceLine.getParent().getM_RMA_ID())
						return Msg.getMsg(Env.getCtx(), "JP_InCaseOfPeriodContractAndSpotContract") + Msg.getMsg(Env.getCtx(),"JP_CanNotBundleDifferentRMA");
				}

				if(JP_ContractLine_ID <= 0)
					return null;

				MContractLine contractLine = MContractLine.get(Env.getCtx(), JP_ContractLine_ID);

				//Check Relation of Contract Cotent
				if(contractLine.getJP_ContractContent_ID() != JP_ContractContent_ID)
				{
					//You can select Contract Content Line that is belong to Contract content
					return Msg.getMsg(Env.getCtx(), "Invalid") +" - " +Msg.getElement(Env.getCtx(), "JP_ContractLine_ID") + Msg.getMsg(Env.getCtx(), "JP_Diff_ContractContentLine");
				}


				//Check Contract Process Period
				int invoiceLine_ContractProcPeriod_ID = invoiceLine.get_ValueAsInt("JP_ContractProcPeriod_ID");
				MContractContent content = MContractContent.get(Env.getCtx(), JP_ContractContent_ID);
				if(contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
				{
					if(content.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateInvoice)
							||content.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceiptInvoice))
					{
						if(type == ModelValidator.TYPE_BEFORE_CHANGE)
						{
							//Check Mandetory
							if(invoiceLine_ContractProcPeriod_ID <= 0)
							{
								Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractProcPeriod_ID")};
								return Msg.getMsg(Env.getCtx(), "JP_InCaseOfCreateDerivativeDocPolicy") + Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs);
							}

							//Check Contract Process Period - Calender
							MContractProcPeriod invoiceLine_ContractProcPeriod = MContractProcPeriod.get(Env.getCtx(), invoiceLine_ContractProcPeriod_ID);
							if(invoiceLine_ContractProcPeriod.getJP_ContractCalender_ID() != contractLine.getJP_ContractCalender_InOut_ID())
							{
								//Please select the Contract Process Period that belong to Calender of Contract Content line.
								return Msg.getMsg(Env.getCtx(), "JP_SelectContractProcPeriodBelongToContractLine");
							}

							//Check valid Contract Period
							MInvoice invoice =invoiceLine.getParent();
							MContractProcPeriod invoicePeriod = MContractProcPeriod.get(Env.getCtx(), invoice.get_ValueAsInt("JP_ContractProcPeriod_ID"));
							if(invoicePeriod.getStartDate().compareTo(invoiceLine_ContractProcPeriod.getStartDate()) > 0
									|| (invoicePeriod.getEndDate() != null && invoicePeriod.getEndDate().compareTo(invoiceLine_ContractProcPeriod.getEndDate()) < 0) )
							{
								//Outside the Contract Process Period.
								return Msg.getMsg(Env.getCtx(), "JP_OutsideContractProcessPeriod") + " " + Msg.getMsg(Env.getCtx(), "Invalid") + Msg.getElement(Env.getCtx(), "JP_ContractProcPeriod_ID");
							}
						}

					}else{

						if(po.get_ValueAsInt("JP_ContractProcPeriod_ID") != 0)
						{
							po.set_ValueNoCheck("JP_ContractProcPeriod_ID", null);
							ProcessInfo pInfo = Env.getProcessInfo(Env.getCtx());
							if(pInfo == null)
							{
								FDialog.info(0, null, "契約管理情報", "契約処理期間は入力できません -> 派生伝票作成方針により");//TODO メッセージ化
							}
						}
					}

				}

			}//if(type == ModelValidator.TYPE_BEFORE_NEW)
		}

		//Check Base Doc
		else if(type == ModelValidator.TYPE_BEFORE_NEW
				||( type == ModelValidator.TYPE_BEFORE_CHANGE && ( po.is_ValueChanged(MContractLine.COLUMNNAME_JP_ContractLine_ID)
						||   po.is_ValueChanged(MContractProcPeriod.COLUMNNAME_JP_ContractProcPeriod_ID) ) ))
		{
			/** Ref:JPiereContractOrderValidator */
			MInvoiceLine invoiceLine = (MInvoiceLine)po;
			ProcessInfo pInfo = Env.getProcessInfo(Env.getCtx());
			
			int JP_ContractLine_ID = invoiceLine.get_ValueAsInt(MContractLine.COLUMNNAME_JP_ContractLine_ID);
			if(JP_ContractLine_ID > 0)
			{
				MContractLine contractLine = MContractLine.get(Env.getCtx(), JP_ContractLine_ID);
				MContract contract = contractLine.getParent().getParent();

				//Check Contract Content
				if(contractLine.getJP_ContractContent_ID() != invoiceLine.getParent().get_ValueAsInt("JP_ContractContent_ID"))
				{
					//You can select Contract Content Line that is belong to Contract content
					return Msg.getMsg(Env.getCtx(), "Invalid") + Msg.getElement(Env.getCtx(), "JP_ContractLine_ID") +" : "+ Msg.getMsg(Env.getCtx(), "JP_Diff_ContractContentLine");
				}


				//Check Period Contract
				if(contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
				{
					//Check Contract Process Period
					int JP_ContractProcPeriod_ID = invoiceLine.get_ValueAsInt(MContractProcPeriod.COLUMNNAME_JP_ContractProcPeriod_ID);
					int parent_ContractProcPeriod_ID = invoiceLine.getParent().get_ValueAsInt(MContractProcPeriod.COLUMNNAME_JP_ContractProcPeriod_ID);
					if(JP_ContractProcPeriod_ID <= 0)
					{
						invoiceLine.set_ValueOfColumn("JP_ContractProcPeriod_ID", parent_ContractProcPeriod_ID);

					}else if (JP_ContractProcPeriod_ID != parent_ContractProcPeriod_ID){

						//Contract process period does not accord with header Contract process period.
						return Msg.getMsg(Env.getCtx(), "JP_DifferentContractProcPeriod");
					}

				//Check Spot Contract
				}else if(contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_SpotContract)){

					if(invoiceLine.get_ValueAsInt("JP_ContractProcPeriod_ID") != 0)
					{
						invoiceLine.set_ValueNoCheck("JP_ContractProcPeriod_ID", null);
						if(pInfo == null)
						{
							FDialog.info(0, null, "契約管理情報", "契約処理期間は入力できません -> スポット契約");//TODO メッセージ化
						}					
					}

				//Check General Contract
				}else if(contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_GeneralContract)){

					if(invoiceLine.get_ValueAsInt("JP_ContractLine_ID") != 0
							|| invoiceLine.get_ValueAsInt("JP_ContractProcPeriod_ID") != 0)
					{
						invoiceLine.set_ValueNoCheck("JP_ContractLine_ID", null);
						invoiceLine.set_ValueNoCheck("JP_ContractProcPeriod_ID", null);
						if(pInfo == null)
						{
							FDialog.info(0, null, "契約管理情報", "契約内容明細と契約処理期間は入力できません -> 一般契約");//TODO メッセージ化
						}						
					}
					
				}

			}else{

				if(invoiceLine.get_ValueAsInt("JP_ContractProcPeriod_ID") != 0)
				{
					invoiceLine.set_ValueNoCheck("JP_ContractProcPeriod_ID", null);
					if(pInfo == null)
					{
						FDialog.info(0, null, "契約管理情報", "契約処理期間は入力できません -> 契約内容明細が未入力");//TODO メッセージ化
					}
				}
				
			}


		}


		//Check Recognition Qty = Invoiced Qty
		if(type == ModelValidator.TYPE_BEFORE_NEW ||
				type == ModelValidator.TYPE_BEFORE_CHANGE )
		{
			int JP_RecognitionLine_ID = po.get_ValueAsInt(MRecognitionLine.COLUMNNAME_JP_RecognitionLine_ID);
			if(JP_RecognitionLine_ID > 0)
			{
				if(type == ModelValidator.TYPE_BEFORE_NEW || po.is_ValueChanged("QtyEntered")
						|| po.is_ValueChanged("QtyInvoiced")
						|| po.is_ValueChanged(MRecognitionLine.COLUMNNAME_JP_RecognitionLine_ID))
				{
					MInvoiceLine iLine = (MInvoiceLine)po;
					MRecognitionLine rLine = new MRecognitionLine(Env.getCtx(), JP_RecognitionLine_ID, po.get_TrxName());
					BigDecimal rLine_QtyInvoiced = rLine.getQtyInvoiced();
					BigDecimal iLine_QtyInvoiced = iLine.getQtyInvoiced();
					if(rLine_QtyInvoiced.abs().compareTo(iLine_QtyInvoiced.abs()) == 0
							|| iLine_QtyInvoiced.compareTo(Env.ZERO) == 0)
					{
						;//Noting to do
					}else{
						//Different Quantity between {0} and {1}
						String msg0 = Msg.getElement(Env.getCtx(), "C_InvoiceLine_ID")+" - " + Msg.getElement(Env.getCtx(), "QtyInvoiced");
						String msg1 = Msg.getElement(Env.getCtx(), "JP_RecognitionLine_ID")+" - " + Msg.getElement(Env.getCtx(), "JP_QtyRecognized");
						return Msg.getMsg(Env.getCtx(),"JP_DifferentQty",new Object[]{msg0,msg1});
					}
				}
			}
		}//Check Recognition Qty = Invoiced Qty

		//Delete Invoice Line Info at Recognition Line
		if(type == ModelValidator.TYPE_AFTER_DELETE)
		{
			MInvoiceLine iLine = (MInvoiceLine)po;
			MInvoice invoice = iLine.getParent();
			int JP_ContractContent_ID = invoice.get_ValueAsInt("JP_ContractContent_ID");
			if(JP_ContractContent_ID > 0)
			{
				MContractContent content = MContractContent.get(Env.getCtx(), JP_ContractContent_ID);
				MContractAcct acctInfo = MContractAcct.get(Env.getCtx(), content.getJP_Contract_Acct_ID());
				if(acctInfo.isPostingContractAcctJP() && acctInfo.isPostingContractAcctJP()
						&& acctInfo.getJP_RecogToInvoicePolicy() != null
						&& !acctInfo.getJP_RecogToInvoicePolicy().equals(MContractAcct.JP_RECOGTOINVOICEPOLICY_NotCreateInvoiceFromRecognition))
				{
					StringBuilder sqLine = new StringBuilder("UPDATE ")
					.append(MRecognitionLine.Table_Name)
					.append(" SET C_InvoiceLine_ID = null WHERE C_InvoiceLine_ID=?");
					DB.executeUpdate(sqLine.toString(), iLine.getC_InvoiceLine_ID(), po.get_TrxName());
				}
			}

		}//TYPE_AFTER_DELETE

		return null;
	}

	private MInvoiceLine[] getInvoiceLinesWithContractLine(MInvoice invoice)
	{
		String whereClauseFinal = "C_Invoice_ID=? AND JP_ContractLine_ID IS NOT NULL ";
		List<MInvoiceLine> list = new Query(Env.getCtx(), I_C_InvoiceLine.Table_Name, whereClauseFinal, invoice.get_TrxName())
										.setParameters(invoice.getC_Invoice_ID())
										.setOrderBy(I_C_InvoiceLine.COLUMNNAME_Line)
										.list();
		return list.toArray(new MInvoiceLine[list.size()]);
	}

	@Override
	public String factsValidate(MAcctSchema schema, List<Fact> facts, PO po)
	{
		if(po.get_TableName().equals(MInvoice.Table_Name))
		{
			int JP_ContractContent_ID = po.get_ValueAsInt("JP_ContractContent_ID");
			if(JP_ContractContent_ID > 0)
			{
				MInvoice invoice = (MInvoice)po;
				//Set Order Info
				for(Fact fact : facts)
				{
					FactLine[]  factLine = fact.getLines();
					for(int i = 0; i < factLine.length; i++)
					{
						if(invoice.getC_Order_ID() > 0)
						{
							factLine[i].set_ValueNoCheck("JP_Order_ID", invoice.getC_Order_ID());
						}else if(invoice.getM_RMA_ID() > 0){
							int M_RMA_ID = invoice.getM_RMA_ID();
							MRMA rma = new MRMA (Env.getCtx(),M_RMA_ID,po.get_TrxName());
							int JP_Order_ID = rma.get_ValueAsInt("JP_Order_ID");
							if(JP_Order_ID > 0)
								factLine[i].set_ValueNoCheck("JP_Order_ID", JP_Order_ID);
						}

						factLine[i].set_ValueNoCheck("JP_ContractContent_ID", JP_ContractContent_ID);
					}//for

				}//for

			}//if(JP_ContractContent_ID > 0)

		}//if(po.get_TableName().equals(MInvoice.Table_Name))

		return null;
	}

}
