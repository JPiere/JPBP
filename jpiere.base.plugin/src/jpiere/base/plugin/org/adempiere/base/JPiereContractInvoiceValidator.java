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
import java.sql.Timestamp;
import java.util.List;
import java.util.logging.Level;

import org.adempiere.webui.window.FDialog;
import org.compiere.acct.DocLine;
import org.compiere.acct.DocTax;
import org.compiere.acct.Fact;
import org.compiere.acct.FactLine;
import org.compiere.model.FactsValidator;
import org.compiere.model.I_C_InvoiceLine;
import org.compiere.model.MAccount;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MClient;
import org.compiere.model.MColumn;
import org.compiere.model.MDocType;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MInvoiceTax;
import org.compiere.model.MJournal;
import org.compiere.model.MJournalLine;
import org.compiere.model.MOrder;
import org.compiere.model.MPeriod;
import org.compiere.model.MProduct;
import org.compiere.model.MRMA;
import org.compiere.model.MRefList;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.model.ProductCost;
import org.compiere.model.Query;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfo;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.MContract;
import jpiere.base.plugin.org.adempiere.model.MContractAcct;
import jpiere.base.plugin.org.adempiere.model.MContractChargeAcct;
import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractLine;
import jpiere.base.plugin.org.adempiere.model.MContractProcPeriod;
import jpiere.base.plugin.org.adempiere.model.MContractProductAcct;
import jpiere.base.plugin.org.adempiere.model.MContractTaxAcct;
import jpiere.base.plugin.org.adempiere.model.MRecognition;
import jpiere.base.plugin.org.adempiere.model.MRecognitionLine;



/**
 *  JPiere Contract Invoice Validator
 *
 *  JPIERE-0363: Contract Management
 *  JPIERE-0408: Set Counter Doc Info
 *  JPIERE-0521: Add JP_Contract_ID, JP_ContractProcPeriod_ID Columns to Fact Acct Table
 *  JPIERE-0539: Create GL Journal From Invoice
 *
 *  @author  Hideaki Hagiwara（h.hagiwara@oss-erp.co.jp）
 *
 */
public class JPiereContractInvoiceValidator extends AbstractContractValidator  implements ModelValidator,FactsValidator {

	private static CLogger log = CLogger.getCLogger(JPiereContractInvoiceValidator.class);
	private int AD_Client_ID = -1;


	@Override
	public void initialize(ModelValidationEngine engine, MClient client)
	{
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();
		engine.addModelChange(MInvoice.Table_Name, this);
		engine.addModelChange(MInvoiceLine.Table_Name, this);
		engine.addDocValidate(MInvoice.Table_Name, this);
		engine.addFactsValidate(MInvoice.Table_Name, this);

		if (log.isLoggable(Level.FINE)) log.fine("Initialize JPiereContractInvoiceValidator");

	}

	@Override
	public int getAD_Client_ID() {

		return AD_Client_ID;
	}

	@Override
	public String login(int AD_Org_ID, int AD_Role_ID, int AD_User_ID)
	{
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
		//JPIERE-0408:Set Counter Doc Info
		if( type == ModelValidator.TYPE_BEFORE_NEW)
		{
			MInvoice invoice = (MInvoice)po;
			if(invoice.getRef_Invoice_ID() > 0) //This is Counter doc
			{
				MInvoice counterInvoice = new MInvoice(po.getCtx(), invoice.getRef_Invoice_ID(), po.get_TrxName());
				if(counterInvoice.get_ValueAsInt("JP_Contract_ID") > 0)
				{
					MContract counterContract = MContract.get(po.getCtx(), counterInvoice.get_ValueAsInt("JP_Contract_ID"));
					if(counterContract != null && counterContract.getJP_Contract_ID() > 0 && counterContract.getJP_CounterContract_ID() > 0)
						invoice.set_ValueNoCheck("JP_Contract_ID", counterContract.getJP_CounterContract_ID());

					if(counterInvoice.get_ValueAsInt("JP_ContractContent_ID") > 0)
					{
						MContractContent counterContractContent = MContractContent.get(po.getCtx(), counterInvoice.get_ValueAsInt("JP_ContractContent_ID"));
						if(counterContractContent != null && counterContractContent.getJP_ContractContent_ID() > 0 && counterContractContent.getJP_CounterContractContent_ID() > 0)
							invoice.set_ValueNoCheck("JP_ContractContent_ID", counterContractContent.getJP_CounterContractContent_ID());
					}

					if(counterInvoice.get_ValueAsInt("JP_ContractProcPeriod_ID") > 0)
					{
						invoice.set_ValueNoCheck("JP_ContractProcPeriod_ID", counterInvoice.get_ValueAsInt("JP_ContractProcPeriod_ID") );
					}

				}//if(counterInvoice.get_ValueAsInt("JP_Contract_ID") > 0)

			}//if(invoice.getRef_Invoice_ID() > 0)

		}//JPIERE-0408:Set Counter Doc Info


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
						String nonEnterable = Msg.getMsg(Env.getCtx(), "JP_NON-ENTERABLE");//Non-enterable:
						String contractContent = Msg.getElement(Env.getCtx(), "JP_ContractContent_ID");
						String contractPeriod = Msg.getElement(Env.getCtx(), "JP_ContractProcPeriod_ID");

						String toBeConfirmed = Msg.getMsg(Env.getCtx(), "JP_ToBeConfirmed");//To Be Confirmed
						Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_Contract_ID")};
						String message = Msg.getMsg(Env.getCtx(), "JP_NOT-INOUT", objs);//It is not input in {0}

						try {
							FDialog.info(0, null, "JP_ContractManagementInfo"
								, nonEnterable + " " + contractContent + " , " + contractPeriod + " -> " + toBeConfirmed +" : " + message);
						}catch(Exception e) {
							;//ignore
						}
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
					if(content.getC_BPartner_ID() != invoice.getC_BPartner_ID() && content.getBill_BPartner_ID() != invoice.getC_BPartner_ID() )
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
					if(content.getC_BPartner_ID() != invoice.getC_BPartner_ID() && content.getBill_BPartner_ID() != invoice.getC_BPartner_ID() )
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
						String nonEnterable = Msg.getMsg(Env.getCtx(), "JP_NON-ENTERABLE");//Non-enterable:
						String contractPeriod = Msg.getElement(Env.getCtx(), "JP_ContractProcPeriod_ID");

						String toBeConfirmed = Msg.getMsg(Env.getCtx(), "JP_ToBeConfirmed");//To Be Confirmed
						MColumn column = MColumn.get(Env.getCtx(), MContract.Table_Name, MContract.COLUMNNAME_JP_ContractType);
						String spotContract = MRefList.getListName(Env.getCtx(), column.getAD_Reference_Value_ID(), MContract.JP_CONTRACTTYPE_SpotContract);

						try {
							FDialog.info(0, null, "JP_ContractManagementInfo"
								, nonEnterable + " " + contractPeriod + " -> " + toBeConfirmed + " : "+  spotContract);
						}catch(Exception e) {
							;//ignore
						}
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
						String nonEnterable = Msg.getMsg(Env.getCtx(), "JP_NON-ENTERABLE");//Non-enterable:
						String contractContent = Msg.getElement(Env.getCtx(), "JP_ContractContent_ID");
						String contractPeriod = Msg.getElement(Env.getCtx(), "JP_ContractProcPeriod_ID");

						String toBeConfirmed = Msg.getMsg(Env.getCtx(), "JP_ToBeConfirmed");//To Be Confirmed:
						MColumn column = MColumn.get(Env.getCtx(), MContract.Table_Name, MContract.COLUMNNAME_JP_ContractType);
						String generalContract = MRefList.getListName(Env.getCtx(), column.getAD_Reference_Value_ID(), MContract.JP_CONTRACTTYPE_GeneralContract);

						try {
							FDialog.info(0, null, "JP_ContractManagementInfo"
								, nonEnterable + " " +contractContent + " , " + contractPeriod + " -> " + toBeConfirmed + " : " + generalContract);
						}catch(Exception e) {
							;//ignore
						}
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
		//JPIERE-0408:Set Counter Doc Line Info
		if( type == ModelValidator.TYPE_BEFORE_NEW)
		{
			MInvoiceLine invoiceLine = (MInvoiceLine)po;
			if(invoiceLine.getRef_InvoiceLine_ID() > 0) //This is Counter doc Line
			{
				MInvoiceLine counterInvoiceLine = new MInvoiceLine(po.getCtx(), invoiceLine.getRef_InvoiceLine_ID(), po.get_TrxName());
				if(counterInvoiceLine.get_ValueAsInt("JP_ContractLine_ID") > 0)
				{
					MContractLine counterContractLine = MContractLine.get(po.getCtx(), counterInvoiceLine.get_ValueAsInt("JP_ContractLine_ID"));
					if(counterContractLine != null && counterContractLine.getJP_ContractLine_ID() > 0 && counterContractLine.getJP_CounterContractLine_ID() > 0)
						invoiceLine.set_ValueNoCheck("JP_ContractLine_ID", counterContractLine.getJP_CounterContractLine_ID());
				}

				if(counterInvoiceLine.get_ValueAsInt("JP_ContractProcPeriod_ID") > 0)
				{
					invoiceLine.set_ValueNoCheck("JP_ContractProcPeriod_ID", counterInvoiceLine.get_ValueAsInt("JP_ContractProcPeriod_ID") );
				}

			}//if(invoiceLine.getRef_InvoiceLine_ID() > 0)

		}//JPIERE-0408:Set Counter Doc Line Info


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
								String nonEnterable = Msg.getMsg(Env.getCtx(), "JP_NON-ENTERABLE");//Non-enterable:
								String contractPeriod = Msg.getElement(Env.getCtx(), "JP_ContractProcPeriod_ID");
								String toBeConfirmed = Msg.getMsg(Env.getCtx(), "JP_ToBeConfirmed");//To Be Confirmed:
								String createDerivativeDocPolicy = Msg.getElement(Env.getCtx(), "JP_CreateDerivativeDocPolicy");

								try {
									FDialog.info(0, null, "JP_ContractManagementInfo"
										, nonEnterable + " " + contractPeriod + " -> "+ toBeConfirmed + " : " +createDerivativeDocPolicy);
								}catch(Exception e) {
									;//ignore
								}
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
							String nonEnterable = Msg.getMsg(Env.getCtx(), "JP_NON-ENTERABLE");
							String contractPeriod = Msg.getElement(Env.getCtx(), "JP_ContractProcPeriod_ID");

							String toBeConfirmed = Msg.getMsg(Env.getCtx(), "JP_ToBeConfirmed");//To Be Confirmed
							MColumn column = MColumn.get(Env.getCtx(), MContract.Table_Name, MContract.COLUMNNAME_JP_ContractType);
							String spotContract = MRefList.getListName(Env.getCtx(), column.getAD_Reference_Value_ID(), MContract.JP_CONTRACTTYPE_SpotContract);

							try {
								FDialog.info(0, null, "JP_ContractManagementInfo"
									, nonEnterable + " " + contractPeriod + " -> " + toBeConfirmed + " : " +  spotContract);
							}catch(Exception e) {
								;//ignore
							}
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
							String nonEnterable = Msg.getMsg(Env.getCtx(), "JP_NON-ENTERABLE");
							String cLine = Msg.getElement(Env.getCtx(), "JP_ContractLine_ID");
							String contractPeriod = Msg.getElement(Env.getCtx(), "JP_ContractProcPeriod_ID");

							String toBeConfirmed = Msg.getMsg(Env.getCtx(), "JP_ToBeConfirmed");//To Be Confirmed
							MColumn column = MColumn.get(Env.getCtx(), MContract.Table_Name, MContract.COLUMNNAME_JP_ContractType);
							String generalContract = MRefList.getListName(Env.getCtx(), column.getAD_Reference_Value_ID(), MContract.JP_CONTRACTTYPE_GeneralContract);

							try {
								FDialog.info(0, null, "JP_ContractManagementInfo"
									, nonEnterable + " " +cLine + " , " + contractPeriod + " -> " + toBeConfirmed + " : " + generalContract);
							}catch(Exception e) {
								;//ignore
							}
						}
					}

				}

			}else{

				if(invoiceLine.get_ValueAsInt("JP_ContractProcPeriod_ID") != 0)
				{
					invoiceLine.set_ValueNoCheck("JP_ContractProcPeriod_ID", null);
					if(pInfo == null)
					{
						String nonEnterable = Msg.getMsg(Env.getCtx(), "JP_NON-ENTERABLE");
						String contractPeriod = Msg.getElement(Env.getCtx(), "JP_ContractProcPeriod_ID");

						String toBeConfirmed = Msg.getMsg(Env.getCtx(), "JP_ToBeConfirmed");//To Be Confirmed
						Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractLine_ID")};
						String message = Msg.getMsg(Env.getCtx(), "JP_NOT-INOUT", objs);

						try {
							FDialog.info(0, null, "JP_ContractManagementInfo"
								, nonEnterable + " " + contractPeriod + " -> " + toBeConfirmed + " : " + message);
						}catch(Exception e) {
							;//ignore
						}
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
		//JPIERE-0521: Add JP_Contract_ID, JP_ContractProcPeriod_ID Columns to Fact Acct Table
		if(po.get_TableName().equals(MInvoice.Table_Name))
		{
			MInvoice invoice = (MInvoice)po;

			int JP_Contract_ID = invoice.get_ValueAsInt("JP_Contract_ID");
			int JP_ContractContent_ID = invoice.get_ValueAsInt("JP_ContractContent_ID");
			int JP_ContractProcPeriod_ID = invoice.get_ValueAsInt("JP_ContractProcPeriod_ID");

			int JP_Order_ID = 0;
			if(invoice.getC_Order_ID() > 0)
			{
				JP_Order_ID = invoice.getC_Order_ID();
			}else if(invoice.getM_RMA_ID() > 0){
				int M_RMA_ID = invoice.getM_RMA_ID();
				MRMA rma = new MRMA (Env.getCtx(), M_RMA_ID, invoice.get_TrxName());
				JP_Order_ID = rma.get_ValueAsInt("JP_Order_ID");
			}

			for(Fact fact : facts)
			{
				FactLine[]  factLine = fact.getLines();
				for(int i = 0; i < factLine.length; i++)
				{
					if(JP_Order_ID > 0)
						factLine[i].set_ValueNoCheck("JP_Order_ID", JP_Order_ID);

					if(JP_Contract_ID > 0)
						factLine[i].set_ValueNoCheck("JP_Contract_ID", JP_Contract_ID);

					if(JP_ContractContent_ID > 0)
						factLine[i].set_ValueNoCheck("JP_ContractContent_ID", JP_ContractContent_ID);

					if(JP_ContractProcPeriod_ID > 0)
						factLine[i].set_ValueNoCheck("JP_ContractProcPeriod_ID", JP_ContractProcPeriod_ID);

				}//for

			}//for

		}//JPIERE-0521


		//JPIERE-0539: Create GL Journal From Invoice
		if(!po.get_ValueAsBoolean(MInvoice.COLUMNNAME_Posted)) //Check of repost to avoid duplicate processing
		{
			int JP_ContractContent_ID = po.get_ValueAsInt("JP_ContractContent_ID");
			if(JP_ContractContent_ID > 0 )
			{
				MContractContent m_Content = MContractContent.get(Env.getCtx(), JP_ContractContent_ID);
				int JP_Contract_Acct_ID = m_Content.getJP_Contract_Acct_ID();
				if(JP_Contract_Acct_ID > 0)
				{
					MContractAcct m_ContractAcct = MContractAcct.get(Env.getCtx(), JP_Contract_Acct_ID);
					if(m_ContractAcct.isPostingContractAcctJP() && m_ContractAcct.isPostingGLJournalJP())
					{
						if(MContractAcct.JP_GLJOURNAL_JOURNALPOLICY_BothItemLineAndNoConfigWillNotCreateGLJournal.equals(m_ContractAcct.getJP_GLJournal_JournalPolicy()))
						{
							String msg = createGLJournal((MInvoice)po, m_ContractAcct, schema, facts);
							if(!Util.isEmpty(msg))
								return msg;
						}
					}
				}
			}
		}

		return null;
	}

	private String createGLJournal(MInvoice m_Invoice, MContractAcct m_ContractAcct, MAcctSchema m_AcctSchema, List<Fact> facts)
	{

		//Judge DocType of Invoice whether create GL Journal.
		MDocType docType = MDocType.get(m_Invoice.getC_DocTypeTarget_ID());
		if(!docType.getDocBaseType().equals(MDocType.DOCBASETYPE_ARInvoice)
				&& !docType.getDocBaseType().equals(MDocType.DOCBASETYPE_APInvoice))
		{
			return null;
		}

		//Judge whether create GL Journal
		if(!isCreateGLJournal(m_Invoice, m_ContractAcct, m_AcctSchema))
			return null;


		//Get DocType of GL Journal
		int JP_DocTypeGLJournal_ID = docType.get_ValueAsInt("JP_DocTypeGLJournal_ID");
		if(JP_DocTypeGLJournal_ID == 0)
		{
			JP_DocTypeGLJournal_ID = MDocType.getDocType(MDocType.DOCBASETYPE_GLJournal);
		}
		MDocType docTypeGL = MDocType.get(JP_DocTypeGLJournal_ID);

		//Get DateDoc of GL Journal
		Timestamp p_DateDoc = null;
		if(MContractAcct.JP_GLJOURNAL_DATEDOCSELECT_FixedDate.equals(m_ContractAcct.getJP_GLJournal_DateDocSelect()))
		{
			p_DateDoc = m_ContractAcct.getJP_GLJournal_DateDoc();
		}else if(MContractAcct.JP_GLJOURNAL_DATEDOCSELECT_AccountDateOfInvoice.equals(m_ContractAcct.getJP_GLJournal_DateDocSelect())) {

			p_DateDoc = m_Invoice.getDateAcct();
		}

		//Get DateAcct of GL Journal
		Timestamp p_DateAcct = null;
		if(MContractAcct.JP_GLJOURNAL_DATEACCTSELECT_FixedDate.equals(m_ContractAcct.getJP_GLJournal_DateAcctSelect()))
		{
			p_DateAcct = m_ContractAcct.getJP_GLJournal_DateAcct();
		}else if(MContractAcct.JP_GLJOURNAL_DATEACCTSELECT_AccountDateOfInvoice.equals(m_ContractAcct.getJP_GLJournal_DateAcctSelect())) {

			p_DateAcct = m_Invoice.getDateAcct();
		}

		//validate period
		int C_Period_ID = MPeriod.getC_Period_ID(m_Invoice.getCtx(), p_DateAcct, m_Invoice.getAD_Org_ID());
		if (C_Period_ID == 0)
		{
			return Msg.getMsg(m_Invoice.getCtx(), "PeriodNotFound") + " : " + DisplayType.getDateFormat().format(p_DateAcct);
		}


		//Get Fact
		Fact fact = null;
		for(Fact f : facts)
		{
			if(m_AcctSchema.getC_AcctSchema_ID() == f.getAcctSchema().getC_AcctSchema_ID())
			{
				fact = f;
			}
		}


		//Create GL Journal
		MJournal m_Journal = new MJournal(m_Invoice.getCtx(), 0, m_Invoice.get_TrxName());
		PO.copyValues(m_Invoice, m_Journal);
		m_Journal.setAD_Org_ID(m_Invoice.getAD_Org_ID());
		m_Journal.setC_AcctSchema_ID(m_AcctSchema.getC_AcctSchema_ID());
		m_Journal.setC_DocType_ID(JP_DocTypeGLJournal_ID);
		if(docTypeGL.isDocNoControlled()) {
			m_Journal.setDocumentNo(null);
		}else {
			m_Journal.setDocumentNo(m_Invoice.getDocumentNo() + "-" + m_AcctSchema.getC_AcctSchema_ID());
		}
		m_Journal.setGL_Category_ID(docTypeGL.getGL_Category_ID());
		m_Journal.setPostingType(MJournal.POSTINGTYPE_Actual);
		m_Journal.setDateDoc(p_DateDoc);
		m_Journal.setDateAcct(p_DateAcct);
		m_Journal.setC_Period_ID(C_Period_ID);
		m_Journal.setDescription(m_Invoice.getDocumentInfo());
		m_Journal.setC_Currency_ID(m_AcctSchema.getC_Currency_ID());
		m_Journal.setDocStatus(DocAction.STATUS_Drafted);
		m_Journal.setDocAction(DocAction.ACTION_Complete);

		int columnIndex = m_Journal.get_ColumnIndex("JP_Order_ID");
		if(columnIndex > -1)
			m_Journal.set_ValueNoCheck("JP_Order_ID", m_Invoice.getC_Order_ID());

		columnIndex = m_Journal.get_ColumnIndex("JP_Invoice_ID");
		if(columnIndex > -1)
			m_Journal.set_ValueNoCheck("JP_Invoice_ID", m_Invoice.getC_Invoice_ID());

		m_Journal.saveEx(m_Invoice.get_TrxName());


		//Craete GL Journal Line
		FactLine[]  factLines = fact.getLines();
		MInvoiceLine[] iLines = m_Invoice.getLines();
		MJournalLine glLine = null;
		MAccount m_AccountDR = null;
		MAccount m_AccountCR = null;
		int line = 0;
		for(MInvoiceLine iLine : iLines)
		{
			for(FactLine factLine : factLines)
			{
				if(iLine.getC_InvoiceLine_ID() != factLine.getLine_ID())
					continue;

				if(!isCreateGLJournalLine(m_Invoice, iLine, m_ContractAcct, m_AcctSchema))
					continue;

				//Create GL Journal Line
				if(m_Invoice.isSOTrx())//AR Invoice
				{
					if(iLine.getM_Product_ID() > 0)
					{
						m_AccountDR = getP_Revenue_Acct(m_Invoice, iLine, m_ContractAcct, m_AcctSchema);
						m_AccountCR = getJP_GL_Revenue_Acct(m_Invoice,iLine, m_ContractAcct, m_AcctSchema);

						if(factLine.getAccount_ID() != m_AccountDR.getAccount_ID())
						{
							if(!m_AcctSchema.isTradeDiscountPosted())
								return Msg.getMsg(Env.getCtx(), "JP_UnexpectedError") + " : Unexpected Account of Product - !MAcctSchema["+ m_AcctSchema.getName() +"].isTradeDiscountPosted()" ;

							m_AccountDR = getP_TradeDiscountGrant_Acct(m_Invoice, iLine, m_ContractAcct, m_AcctSchema);
							if(factLine.getAccount_ID() != m_AccountDR.getAccount_ID())
							{
								return Msg.getMsg(Env.getCtx(), "JP_UnexpectedError") + " : Unexpected Account of Product - FactLine.getAccount_ID() != P_TradeDiscountGrant_Acct";
							}else {
								m_AccountCR = getJP_GL_TradeDiscountGrant_Acct(m_Invoice, iLine, m_ContractAcct, m_AcctSchema, factLine);
							}
						}

					}else if(iLine.getC_Charge_ID() > 0) {

						m_AccountDR = getCh_Expense_Acct(m_Invoice, iLine, m_ContractAcct, m_AcctSchema);
						m_AccountCR = getJP_GL_Ch_Expense_Acct(m_Invoice,iLine, m_ContractAcct, m_AcctSchema);

						if(factLine.getAccount_ID() != m_AccountDR.getAccount_ID())
						{
							if(!m_AcctSchema.isTradeDiscountPosted())
								return Msg.getMsg(Env.getCtx(), "JP_UnexpectedError") + " : Unexpected Account of Charge - !MAcctSchema["+ m_AcctSchema.getName() +"]+.isTradeDiscountPosted()" ;

							m_AccountDR = getP_TradeDiscountGrant_Acct(m_Invoice, iLine, m_ContractAcct, m_AcctSchema);
							if(factLine.getAccount_ID() != m_AccountDR.getAccount_ID())
							{
								return Msg.getMsg(Env.getCtx(), "JP_UnexpectedError") + " : Unexpected Account of Charge - FactLine.getAccount_ID() != P_TradeDiscountGrant_Acct" ;
							}else {
								m_AccountCR = getJP_GL_TradeDiscountGrant_Acct(m_Invoice, iLine, m_ContractAcct, m_AcctSchema, factLine);
							}
						}
					}

					// Dr
					line++;
					glLine = journalLineFactory(m_Journal, iLine);
					glLine.setLine(line*10);
					glLine.setQty(iLine.getQtyInvoiced().negate());
					glLine.setAccount_ID(m_AccountDR.getAccount_ID());
					glLine.setAmtSourceDr(factLine.getAmtAcctCr());
					glLine.setAmtAcctDr(factLine.getAmtAcctCr());
					glLine.setAmtSourceCr(factLine.getAmtAcctDr());
					glLine.setAmtAcctCr(factLine.getAmtAcctDr());
					glLine.saveEx(m_Invoice.get_TrxName());

					//Cr
					line++;
					glLine = journalLineFactory(m_Journal, iLine);
					glLine.setLine(line*10);
					glLine.setQty(iLine.getQtyInvoiced());
					glLine.setAccount_ID(m_AccountCR.getAccount_ID());
					glLine.setAmtSourceDr(factLine.getAmtAcctDr());
					glLine.setAmtAcctDr(factLine.getAmtAcctDr());
					glLine.setAmtSourceCr(factLine.getAmtAcctCr());
					glLine.setAmtAcctCr(factLine.getAmtAcctCr());
					glLine.saveEx(m_Invoice.get_TrxName());


				}else {//AP Invoice

					if(iLine.getM_Product_ID() > 0)
					{
						m_AccountDR = getJP_GL_Expense_Acct(m_Invoice,iLine, m_ContractAcct, m_AcctSchema);
						m_AccountCR = getP_Expense_Acct(m_Invoice,iLine, m_ContractAcct, m_AcctSchema);
						if(factLine.getAccount_ID() != m_AccountCR.getAccount_ID())
						{
							if(!m_AcctSchema.isTradeDiscountPosted())
								return Msg.getMsg(Env.getCtx(), "JP_UnexpectedError") + " : Unexpected Account of Product - !MAcctSchema["+ m_AcctSchema.getName() +"].isTradeDiscountPosted()" ;

							m_AccountCR = getP_TradeDiscountRec_Acct(m_Invoice, iLine, m_ContractAcct, m_AcctSchema);
							if(factLine.getAccount_ID() != m_AccountCR.getAccount_ID())
							{
								return Msg.getMsg(Env.getCtx(), "JP_UnexpectedError") + " : Unexpected Account of Product - FactLine.getAccount_ID() != P_TradeDiscountRec_Acct" ;
							}else {
								m_AccountDR = getJP_GL_TradeDiscountRec_Acct(m_Invoice, iLine, m_ContractAcct, m_AcctSchema, factLine);
							}
						}

					}else if(iLine.getC_Charge_ID() > 0) {
						m_AccountDR = getJP_GL_Ch_Expense_Acct(m_Invoice,iLine, m_ContractAcct, m_AcctSchema);
						m_AccountCR = getCh_Expense_Acct(m_Invoice,iLine, m_ContractAcct, m_AcctSchema);

						if(factLine.getAccount_ID() != m_AccountCR.getAccount_ID())
						{
							if(!m_AcctSchema.isTradeDiscountPosted())
								return Msg.getMsg(Env.getCtx(), "JP_UnexpectedError") + " : Unexpected Account of Charge - !MAcctSchema["+ m_AcctSchema.getName() +"].isTradeDiscountPosted()" ;

							m_AccountCR = getP_TradeDiscountRec_Acct(m_Invoice, iLine, m_ContractAcct, m_AcctSchema);
							if(factLine.getAccount_ID() != m_AccountCR.getAccount_ID())
							{
								return  Msg.getMsg(Env.getCtx(), "JP_UnexpectedError") + " : Unexpected Account of Charge - FactLine.getAccount_ID() != P_TradeDiscountRec_Acct" ;
							}else {
								m_AccountDR = getJP_GL_TradeDiscountRec_Acct(m_Invoice, iLine, m_ContractAcct, m_AcctSchema, factLine);
							}
						}
					}

					//Dr
					line++;
					glLine = journalLineFactory(m_Journal, iLine);
					glLine.setLine(line*10);
					glLine.setQty(iLine.getQtyInvoiced());
					glLine.setAccount_ID(m_AccountDR.getAccount_ID());
					glLine.setAmtSourceDr(factLine.getAmtAcctDr());
					glLine.setAmtAcctDr(factLine.getAmtAcctDr());
					glLine.setAmtSourceCr(factLine.getAmtAcctCr());
					glLine.setAmtAcctCr(factLine.getAmtAcctCr());
					glLine.saveEx(m_Invoice.get_TrxName());

					//Cr
					line++;
					glLine = journalLineFactory(m_Journal, iLine);
					glLine.setLine(line*10);
					glLine.setQty(iLine.getQtyInvoiced().negate());
					glLine.setAccount_ID(m_AccountCR.getAccount_ID());
					glLine.setAmtSourceDr(factLine.getAmtAcctCr());
					glLine.setAmtAcctDr(factLine.getAmtAcctCr());
					glLine.setAmtSourceCr(factLine.getAmtAcctDr());
					glLine.setAmtAcctCr(factLine.getAmtAcctDr());
					glLine.saveEx(m_Invoice.get_TrxName());

				}

			}//for FactLine

		}//for MInvoiceLine


		//Crate GL Journal Line for Tax adjust
		if(isCreateTaxAdjustGLJournalLine(m_Invoice, m_ContractAcct, m_AcctSchema))
		{
			MInvoiceTax[] iTaxes = m_Invoice.getTaxes(true);
			for(MInvoiceTax iTax : iTaxes)
			{
				if(iTax.getTaxAmt().compareTo(Env.ZERO) == 0)
					continue;

				FactLine factLine = null;
				for(int i = 0; i < factLines.length; i++)
				{
					if(iTax.getC_Tax_ID() == factLines[i].getC_Tax_ID()
							&& factLines[i].getLine_ID() == 0)
					{
						factLine = factLines[i];
						break;
					}
				}

				if(factLine == null)
					continue;

				if(m_Invoice.isSOTrx())
				{
					m_AccountCR = getJP_GL_TaxDue_Acct(m_Invoice, iTax, m_ContractAcct, m_AcctSchema);
					if(m_AccountCR == null)
						continue;

					m_AccountDR = getT_TaxDue_Acct(m_Invoice, iTax, m_ContractAcct, m_AcctSchema);

					//Create GL Journal Line - Dr
					line++;
					glLine = journalLineFactory(m_Journal, null);
					glLine.setLine(line*10);
					glLine.setAccount_ID(m_AccountDR.getAccount_ID());
					glLine.setAmtSourceDr(factLine.getAmtAcctCr());
					glLine.setAmtAcctDr(factLine.getAmtAcctCr());
					glLine.setAmtSourceCr(factLine.getAmtAcctDr());
					glLine.setAmtAcctCr(factLine.getAmtAcctDr());
					glLine.setQty(Env.ZERO);
					glLine.saveEx(m_Invoice.get_TrxName());


					//Create GL Journal Line - Cr
					line++;
					glLine = journalLineFactory(m_Journal, null);
					glLine.setLine(line*10);
					glLine.setAccount_ID(m_AccountCR.getAccount_ID());
					glLine.setAmtSourceDr(factLine.getAmtAcctDr());
					glLine.setAmtAcctDr(factLine.getAmtAcctDr());
					glLine.setAmtSourceCr(factLine.getAmtAcctCr());
					glLine.setAmtAcctCr(factLine.getAmtAcctCr());
					glLine.setQty(Env.ZERO);
					glLine.saveEx(m_Invoice.get_TrxName());

				}else {

					if(iTax.getC_Tax().isSalesTax())
					{
						m_AccountDR = getJP_GL_TaxExpense_Acct(m_Invoice, iTax, m_ContractAcct, m_AcctSchema);
						if(m_AccountDR == null)
							continue;

					}else {

						m_AccountDR = getJP_GL_TaxCredit_Acct(m_Invoice, iTax, m_ContractAcct, m_AcctSchema);
						if(m_AccountDR == null)
							continue;
					}

					if(iTax.getC_Tax().isSalesTax()){
						m_AccountCR = getT_TaxExpense_Acct(m_Invoice, iTax, m_ContractAcct, m_AcctSchema);
					}else {
						m_AccountCR = getT_TaxCredit_Acct(m_Invoice, iTax, m_ContractAcct, m_AcctSchema);
					}

					//Create GL Journal Line - Dr
					line++;
					glLine  = journalLineFactory(m_Journal, null);
					glLine.setLine(line*10);
					glLine.setAccount_ID(m_AccountDR.getAccount_ID());
					glLine.setAmtSourceDr(factLine.getAmtAcctDr());
					glLine.setAmtAcctDr(factLine.getAmtAcctDr());
					glLine.setAmtSourceCr(factLine.getAmtAcctCr());
					glLine.setAmtAcctCr(factLine.getAmtAcctCr());
					glLine.setQty(Env.ZERO);
					glLine.saveEx(m_Invoice.get_TrxName());


					//Create GL Journal Line - Cr
					line++;
					glLine  = journalLineFactory(m_Journal, null);
					glLine.setLine(line*10);
					glLine.setAccount_ID(m_AccountCR.getAccount_ID());
					glLine.setAmtSourceDr(factLine.getAmtAcctCr());
					glLine.setAmtAcctDr(factLine.getAmtAcctCr());
					glLine.setAmtSourceCr(factLine.getAmtAcctDr());
					glLine.setAmtAcctCr(factLine.getAmtAcctDr());
					glLine.setQty(Env.ZERO);
					glLine.saveEx(m_Invoice.get_TrxName());

				}

			}

		}//Crate GL Journal Line for Tax adjust

		return null;
	}

	private MJournalLine journalLineFactory(MJournal m_Journal, MInvoiceLine m_InvoiceLine)
	{
		if(m_Journal == null || m_Journal.getGL_Journal_ID() == 0)
			return null;

		MJournalLine glLine = new MJournalLine(m_Journal.getCtx(), 0, m_Journal.get_TrxName());

		if(m_InvoiceLine != null)
			PO.copyValues(m_InvoiceLine, glLine);
		glLine.setAD_Org_ID(m_Journal.getAD_Org_ID());
		glLine.setGL_Journal_ID(m_Journal.getGL_Journal_ID());
		glLine.setDateAcct(m_Journal.getDateAcct());
		glLine.setCurrencyRate(Env.ONE);
		glLine.setC_Currency_ID(m_Journal.getC_Currency_ID());

		if(m_InvoiceLine != null)
			glLine.setC_UOM_ID(m_InvoiceLine.getC_UOM_ID());
		else
			glLine.setC_UOM_ID(100);

		return glLine;
	}

	private boolean isCreateGLJournal(MInvoice m_Invoice, MContractAcct m_ContractAcct, MAcctSchema m_AcctSchema)
	{
		boolean isCreateGLJournal = false;
		MInvoiceLine[] lines = m_Invoice.getLines();
		for(MInvoiceLine line : lines)
		{
			if(isCreateGLJournalLine(m_Invoice, line, m_ContractAcct,m_AcctSchema))
			{
				isCreateGLJournal = true;
				break;
			}
		}

		if(isCreateGLJournal)
			return true;

		if(isCreateTaxAdjustGLJournalLine(m_Invoice, m_ContractAcct,m_AcctSchema))
			return true;

		return false;
	}

	private boolean isCreateGLJournalLine(MInvoice m_Invoice, MInvoiceLine m_InvoiceLine, MContractAcct m_ContractAcct, MAcctSchema m_AcctSchema )
	{
		int M_Product_ID = m_InvoiceLine.getM_Product_ID();
		int C_Charge_ID = m_InvoiceLine.getC_Charge_ID();
		MAccount m_Account = null;
		if(m_Invoice.isSOTrx())
		{
			if(M_Product_ID > 0){
				m_Account = getJP_GL_Revenue_Acct(m_Invoice, m_InvoiceLine, m_ContractAcct, m_AcctSchema);
			}else if(C_Charge_ID > 0) {
				m_Account = getJP_GL_Ch_Expense_Acct(m_Invoice, m_InvoiceLine, m_ContractAcct, m_AcctSchema);
			}

		}else {

			if(M_Product_ID > 0){
				 m_Account = getJP_GL_Expense_Acct(m_Invoice, m_InvoiceLine, m_ContractAcct, m_AcctSchema);
			}else if(C_Charge_ID > 0) {
				m_Account = getJP_GL_Ch_Expense_Acct(m_Invoice, m_InvoiceLine, m_ContractAcct, m_AcctSchema);
			}
		}

		if(m_Account != null)
			return true;

		return false;
	}

	private boolean isCreateTaxAdjustGLJournalLine(MInvoice m_Invoice, MContractAcct m_ContractAcct, MAcctSchema m_AcctSchema )
	{

		MInvoiceTax[] iTaxes = m_Invoice.getTaxes(true);
		if(iTaxes.length == 0)
			return false;

		boolean isCreateTaxAdjustGLJournalLine = false;

		MAccount m_Account = null;
		for(MInvoiceTax iTax : iTaxes)
		{
			if(iTax.getTaxAmt().compareTo(Env.ZERO) == 0)
				continue;

			if(m_Invoice.isSOTrx()){
				m_Account = getJP_GL_TaxDue_Acct(m_Invoice, iTax, m_ContractAcct, m_AcctSchema);
			}else {
				if(iTax.getC_Tax().isSalesTax()) {
					m_Account = getJP_GL_TaxExpense_Acct(m_Invoice, iTax, m_ContractAcct, m_AcctSchema);
				}else {
					m_Account = getJP_GL_TaxCredit_Acct(m_Invoice, iTax, m_ContractAcct, m_AcctSchema);
				}
			}

			if(m_Account != null)
			{
				isCreateTaxAdjustGLJournalLine = true;
				break;
			}
		}//for

		if(isCreateTaxAdjustGLJournalLine)
			return true;

		return false;
	}

	private MAccount getJP_GL_Revenue_Acct(MInvoice m_Invoice, MInvoiceLine m_InvoiceLine, MContractAcct m_ContractAcct, MAcctSchema m_AcctSchema)
	{
		int M_Product_ID = m_InvoiceLine.getM_Product_ID();
		if(M_Product_ID > 0)
		{
			MProduct m_Product = MProduct.get(M_Product_ID);
			if(MProduct.PRODUCTTYPE_Item.equals(m_Product.getProductType()))
			{
				return null;
			}

			MContractProductAcct m_ContractProductAcct = m_ContractAcct.getContractProductAcct(m_Product.getM_Product_Category_ID(), m_AcctSchema.getC_AcctSchema_ID(), false);
			if(m_ContractProductAcct == null)
				return null;

			int JP_GL_Revenue_Acct = m_ContractProductAcct.getJP_GL_Revenue_Acct();
			if(JP_GL_Revenue_Acct == 0)
				return null;

			return MAccount.get(m_Invoice.getCtx(), JP_GL_Revenue_Acct);

		}

		return null;
	}

	private MAccount getJP_GL_TradeDiscountGrant_Acct(MInvoice m_Invoice, MInvoiceLine m_InvoiceLine, MContractAcct m_ContractAcct, MAcctSchema m_AcctSchema, FactLine factLine)
	{
		int M_Product_ID = m_InvoiceLine.getM_Product_ID();
		if(M_Product_ID > 0)
		{
			MProduct m_Product = MProduct.get(M_Product_ID);
			if(MProduct.PRODUCTTYPE_Item.equals(m_Product.getProductType()))
			{
				return null;
			}

			MContractProductAcct m_ContractProductAcct = m_ContractAcct.getContractProductAcct(m_Product.getM_Product_Category_ID(), m_AcctSchema.getC_AcctSchema_ID(), false);
			if(m_ContractProductAcct == null)
				return null;

			int JP_GL_TradeDiscountGrant_Acct = m_ContractProductAcct.getJP_GL_TradeDiscountGrant_Acct();
			if(JP_GL_TradeDiscountGrant_Acct == 0)
				return getP_TradeDiscountGrant_Acct(m_Invoice, m_InvoiceLine, m_ContractAcct, m_AcctSchema);

			return MAccount.get(m_Invoice.getCtx(), JP_GL_TradeDiscountGrant_Acct);

		}

		return factLine.getAccount();
	}

	private MAccount getP_Revenue_Acct(MInvoice m_Invoice, MInvoiceLine m_InvoiceLine, MContractAcct m_ContractAcct, MAcctSchema m_AcctSchema)
	{
		int M_Product_ID = m_InvoiceLine.getM_Product_ID();
		if(M_Product_ID > 0)
		{
			MProduct m_Product = MProduct.get(M_Product_ID);
			if(MProduct.PRODUCTTYPE_Item.equals(m_Product.getProductType()))
			{
				return null;
			}

			MContractProductAcct m_ContractProductAcct = m_ContractAcct.getContractProductAcct(m_Product.getM_Product_Category_ID(), m_AcctSchema.getC_AcctSchema_ID(), false);
			if(m_ContractProductAcct == null)
				return null;

			int P_Revenue_Acct = m_ContractProductAcct.getP_Revenue_Acct();
			if(P_Revenue_Acct == 0)
			{
				//Get Default Account
				return getProductCost(m_InvoiceLine).getAccount(ProductCost.ACCTTYPE_P_Revenue,m_AcctSchema);
			}

			return MAccount.get(m_Invoice.getCtx(), P_Revenue_Acct);

		}

		return null;
	}

	private MAccount getP_TradeDiscountGrant_Acct(MInvoice m_Invoice, MInvoiceLine m_InvoiceLine, MContractAcct m_ContractAcct, MAcctSchema m_AcctSchema)
	{
		if(m_InvoiceLine.getM_Product_ID() > 0)
		{
			MContractProductAcct contractProductAcct = m_ContractAcct.getContractProductAcct(m_InvoiceLine.getM_Product().getM_Product_Category_ID(), m_AcctSchema.getC_AcctSchema_ID(), false);
			if(contractProductAcct != null && contractProductAcct.getP_TradeDiscountGrant_Acct() > 0)
			{
				return MAccount.get(m_Invoice.getCtx(),contractProductAcct.getP_TradeDiscountGrant_Acct());
			}else{

				DocLine docLine = new DocLine (m_InvoiceLine , null);
				return docLine.getAccount(ProductCost.ACCTTYPE_P_TDiscountGrant, m_AcctSchema);
			}

		}else {

			return MAccount.get(m_Invoice.getCtx(), m_AcctSchema.getAcctSchemaDefault().getP_TradeDiscountGrant_Acct());
		}

	}

	private MAccount getJP_GL_Expense_Acct(MInvoice m_Invoice, MInvoiceLine m_InvoiceLine, MContractAcct m_ContractAcct, MAcctSchema m_AcctSchema)
	{
		int M_Product_ID = m_InvoiceLine.getM_Product_ID();
		if(M_Product_ID > 0)
		{
			MProduct m_Product = MProduct.get(M_Product_ID);
			if(MProduct.PRODUCTTYPE_Item.equals(m_Product.getProductType()))
			{
				return null;
			}

			MContractProductAcct m_ContractProductAcct = m_ContractAcct.getContractProductAcct(m_Product.getM_Product_Category_ID(), m_AcctSchema.getC_AcctSchema_ID(), false);
			if(m_ContractProductAcct == null)
				return null;

			int JP_GL_Expense_Acct = m_ContractProductAcct.getJP_GL_Expense_Acct();
			if(JP_GL_Expense_Acct == 0)
				return null;

			return MAccount.get(m_Invoice.getCtx(), JP_GL_Expense_Acct);

		}
		return null;
	}

	private MAccount getP_Expense_Acct(MInvoice m_Invoice, MInvoiceLine m_InvoiceLine, MContractAcct m_ContractAcct, MAcctSchema m_AcctSchema)
	{
		int M_Product_ID = m_InvoiceLine.getM_Product_ID();
		if(M_Product_ID > 0)
		{
			MProduct m_Product = MProduct.get(M_Product_ID);
			if(MProduct.PRODUCTTYPE_Item.equals(m_Product.getProductType()))
			{
				return null;
			}

			MContractProductAcct m_ContractProductAcct = m_ContractAcct.getContractProductAcct(m_Product.getM_Product_Category_ID(), m_AcctSchema.getC_AcctSchema_ID(), false);
			if(m_ContractProductAcct == null)
				return null;

			int P_Expense_Acct = m_ContractProductAcct.getP_Expense_Acct();
			if(P_Expense_Acct == 0)
			{
				//Get Default Account
				return getProductCost(m_InvoiceLine).getAccount(ProductCost.ACCTTYPE_P_Expense, m_AcctSchema);
			}

			return MAccount.get(m_Invoice.getCtx(), P_Expense_Acct);

		}

		return null;
	}

	private MAccount getP_TradeDiscountRec_Acct(MInvoice m_Invoice, MInvoiceLine m_InvoiceLine, MContractAcct m_ContractAcct, MAcctSchema m_AcctSchema)
	{
		if(m_InvoiceLine.getM_Product_ID() > 0)
		{
			MContractProductAcct contractProductAcct = m_ContractAcct.getContractProductAcct(m_InvoiceLine.getM_Product().getM_Product_Category_ID(), m_AcctSchema.getC_AcctSchema_ID(), false);
			if(contractProductAcct != null && contractProductAcct.getP_TradeDiscountRec_Acct() > 0)
			{
				return MAccount.get(m_Invoice.getCtx(),contractProductAcct.getP_TradeDiscountRec_Acct());
			}else{

				DocLine docLine = new DocLine (m_InvoiceLine , null);
				return docLine.getAccount(ProductCost.ACCTTYPE_P_TDiscountRec, m_AcctSchema);
			}

		}else {

			return MAccount.get(m_Invoice.getCtx(), m_AcctSchema.getAcctSchemaDefault().getP_TradeDiscountRec_Acct());
		}

	}

	private MAccount getJP_GL_TradeDiscountRec_Acct(MInvoice m_Invoice, MInvoiceLine m_InvoiceLine, MContractAcct m_ContractAcct, MAcctSchema m_AcctSchema, FactLine factLine)
	{
		int M_Product_ID = m_InvoiceLine.getM_Product_ID();
		if(M_Product_ID > 0)
		{
			MProduct m_Product = MProduct.get(M_Product_ID);
			if(MProduct.PRODUCTTYPE_Item.equals(m_Product.getProductType()))
			{
				return null;
			}

			MContractProductAcct m_ContractProductAcct = m_ContractAcct.getContractProductAcct(m_Product.getM_Product_Category_ID(), m_AcctSchema.getC_AcctSchema_ID(), false);
			if(m_ContractProductAcct == null)
				return null;

			int JP_GL_TradeDiscountRec_Acct = m_ContractProductAcct.getJP_GL_TradeDiscountRec_Acct();
			if(JP_GL_TradeDiscountRec_Acct == 0)
				return getP_TradeDiscountRec_Acct(m_Invoice, m_InvoiceLine, m_ContractAcct, m_AcctSchema);

			return MAccount.get(m_Invoice.getCtx(), JP_GL_TradeDiscountRec_Acct);

		}

		return factLine.getAccount();
	}

	private MAccount getJP_GL_Ch_Expense_Acct(MInvoice m_Invoice, MInvoiceLine m_InvoiceLine, MContractAcct m_ContractAcct, MAcctSchema m_AcctSchema)
	{
		MContractChargeAcct m_ContractChargeAcct = m_ContractAcct.getContracChargeAcct(m_InvoiceLine.getC_Charge_ID(), m_AcctSchema.getC_AcctSchema_ID(), false);
		if(m_ContractChargeAcct == null)
			return null;

		int JP_GL_Ch_Expense_Acct = m_ContractChargeAcct.getJP_GL_Ch_Expense_Acct();
		if(JP_GL_Ch_Expense_Acct == 0)
			return null;

		return MAccount.get(m_Invoice.getCtx(), JP_GL_Ch_Expense_Acct);
	}

	private MAccount getCh_Expense_Acct(MInvoice m_Invoice, MInvoiceLine m_InvoiceLine, MContractAcct m_ContractAcct, MAcctSchema m_AcctSchema)
	{
		MContractChargeAcct m_ContractChargeAcct = m_ContractAcct.getContracChargeAcct(m_InvoiceLine.getC_Charge_ID(), m_AcctSchema.getC_AcctSchema_ID(), false);
		if(m_ContractChargeAcct == null || m_ContractChargeAcct.getCh_Expense_Acct() == 0)
		{
			//Get Default Account
			return getProductCost(m_InvoiceLine).getAccount(ProductCost.ACCTTYPE_P_Expense, m_AcctSchema);

		}else {

			return MAccount.get(m_Invoice.getCtx(), m_ContractChargeAcct.getCh_Expense_Acct());

		}
	}

	private MAccount getJP_GL_TaxDue_Acct(MInvoice m_Invoice, MInvoiceTax m_InvoiceTax, MContractAcct m_ContractAcct, MAcctSchema m_AcctSchema)
	{
		MContractTaxAcct m_ContractTaxAcct = m_ContractAcct.getContracTaxAcct(m_InvoiceTax.getC_Tax_ID(), m_AcctSchema.getC_AcctSchema_ID(),false);
		if(m_ContractTaxAcct == null)
			return null;

		int JP_GL_TaxDue_Acct = m_ContractTaxAcct.getJP_GL_TaxDue_Acct();
		if(JP_GL_TaxDue_Acct == 0)
			return null;

		return MAccount.get(m_Invoice.getCtx(), JP_GL_TaxDue_Acct);
	}

	private MAccount getT_TaxDue_Acct(MInvoice m_Invoice, MInvoiceTax m_InvoiceTax, MContractAcct m_ContractAcct, MAcctSchema m_AcctSchema)
	{
		MContractTaxAcct m_ContractTaxAcct = m_ContractAcct.getContracTaxAcct(m_InvoiceTax.getC_Tax_ID(), m_AcctSchema.getC_AcctSchema_ID(),false);
		if(m_ContractTaxAcct != null && m_ContractTaxAcct.getT_Due_Acct() > 0)
		{
			return MAccount.get(m_Invoice.getCtx(), m_ContractTaxAcct.getT_Due_Acct());

		}else{

			DocTax docTax = new DocTax (m_InvoiceTax.getC_Tax_ID(), "", Env.ZERO, Env.ZERO, Env.ZERO, m_Invoice.isSOTrx());
			return docTax.getAccount(DocTax.ACCTTYPE_TaxDue, m_AcctSchema);
		}
	}

	private MAccount getJP_GL_TaxCredit_Acct(MInvoice m_Invoice, MInvoiceTax m_InvoiceTax, MContractAcct m_ContractAcct, MAcctSchema m_AcctSchema)
	{
		MContractTaxAcct m_ContractTaxAcct = m_ContractAcct.getContracTaxAcct(m_InvoiceTax.getC_Tax_ID(), m_AcctSchema.getC_AcctSchema_ID(),false);
		if(m_ContractTaxAcct == null)
			return null;

		int JP_GL_TaxCredit_Acct = m_ContractTaxAcct.getJP_GL_TaxCredit_Acct();
		if(JP_GL_TaxCredit_Acct == 0)
			return null;

		return MAccount.get(m_Invoice.getCtx(), JP_GL_TaxCredit_Acct);
	}

	private MAccount getT_TaxCredit_Acct(MInvoice m_Invoice, MInvoiceTax m_InvoiceTax, MContractAcct m_ContractAcct, MAcctSchema m_AcctSchema)
	{
		MContractTaxAcct m_ContractTaxAcct = m_ContractAcct.getContracTaxAcct(m_InvoiceTax.getC_Tax_ID(), m_AcctSchema.getC_AcctSchema_ID(),false);
		if(m_ContractTaxAcct != null && m_ContractTaxAcct.getT_Credit_Acct() > 0)
		{
			return MAccount.get(m_Invoice.getCtx(), m_ContractTaxAcct.getT_Credit_Acct());

		}else{

			DocTax docTax = new DocTax (m_InvoiceTax.getC_Tax_ID(), "", Env.ZERO, Env.ZERO, Env.ZERO, m_Invoice.isSOTrx());
			return docTax.getAccount(DocTax.ACCTTYPE_TaxCredit, m_AcctSchema);
		}
	}

	private MAccount getJP_GL_TaxExpense_Acct(MInvoice m_Invoice, MInvoiceTax m_InvoiceTax, MContractAcct m_ContractAcct, MAcctSchema m_AcctSchema)
	{
		MContractTaxAcct m_ContractTaxAcct = m_ContractAcct.getContracTaxAcct(m_InvoiceTax.getC_Tax_ID(), m_AcctSchema.getC_AcctSchema_ID(),false);
		if(m_ContractTaxAcct == null)
			return null;

		int JP_GL_TaxExpense_Acct = m_ContractTaxAcct.getJP_GL_TaxExpense_Acct();
		if(JP_GL_TaxExpense_Acct == 0)
			return null;

		return MAccount.get(m_Invoice.getCtx(), JP_GL_TaxExpense_Acct);
	}

	private MAccount getT_TaxExpense_Acct(MInvoice m_Invoice, MInvoiceTax m_InvoiceTax, MContractAcct m_ContractAcct, MAcctSchema m_AcctSchema)
	{
		MContractTaxAcct m_ContractTaxAcct = m_ContractAcct.getContracTaxAcct(m_InvoiceTax.getC_Tax_ID(), m_AcctSchema.getC_AcctSchema_ID(),false);
		if(m_ContractTaxAcct != null && m_ContractTaxAcct.getT_Expense_Acct() > 0)
		{
			return MAccount.get(m_Invoice.getCtx(), m_ContractTaxAcct.getT_Expense_Acct());

		}else{

			DocTax docTax = new DocTax (m_InvoiceTax.getC_Tax_ID(), "", Env.ZERO, Env.ZERO, Env.ZERO, m_Invoice.isSOTrx());
			return docTax.getAccount(DocTax.ACCTTYPE_TaxExpense, m_AcctSchema);
		}
	}

	private ProductCost getProductCost(MInvoiceLine m_InvoiceLine)
	{
		ProductCost	m_productCost = new ProductCost (Env.getCtx(),
					m_InvoiceLine.getM_Product_ID(), m_InvoiceLine.getM_AttributeSetInstance_ID(), m_InvoiceLine.get_TrxName());

		return m_productCost;
	}	//	getProductCost
}
