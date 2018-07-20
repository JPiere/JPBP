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

import java.util.List;

import org.adempiere.exceptions.AdempiereException;
import org.adempiere.webui.window.FDialog;
import org.compiere.acct.Fact;
import org.compiere.acct.FactLine;
import org.compiere.model.FactsValidator;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MClient;
import org.compiere.model.MDocType;
import org.compiere.model.MInOut;
import org.compiere.model.MInOutLine;
import org.compiere.model.MOrder;
import org.compiere.model.MPeriod;
import org.compiere.model.MRMA;
import org.compiere.model.MRMALine;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfo;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.MContract;
import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractLine;
import jpiere.base.plugin.org.adempiere.model.MContractProcPeriod;
import jpiere.base.plugin.org.adempiere.model.MRecognition;
import jpiere.base.plugin.org.adempiere.model.MRecognitionLine;



/**
 *  JPIERE-0363: Contract Management
 *  JPiere Contract InOut Validator
 *
 *  @author  Hideaki Hagiwara（h.hagiwara@oss-erp.co.jp）
 *
 */
public class JPiereContractInOutValidator extends AbstractContractValidator  implements ModelValidator,FactsValidator {

	private static CLogger log = CLogger.getCLogger(JPiereContractInOutValidator.class);
	private int AD_Client_ID = -1;
	private int AD_Org_ID = -1;
	private int AD_Role_ID = -1;
	private int AD_User_ID = -1;


	@Override
	public void initialize(ModelValidationEngine engine, MClient client)
	{
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();
		engine.addModelChange(MInOut.Table_Name, this);
		engine.addModelChange(MInOutLine.Table_Name, this);
		engine.addDocValidate(MInOut.Table_Name, this);
		engine.addFactsValidate(MInOut.Table_Name, this);
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
		if(po.get_TableName().equals(MInOut.Table_Name))
		{
			return inOutValidate(po, type);

		}else if(po.get_TableName().equals(MInOutLine.Table_Name)){

			return inOutLineValidate(po, type);
		}

		return null;
	}

	@Override
	public String docValidate(PO po, int timing)
	{

		if(timing == ModelValidator.TIMING_BEFORE_PREPARE)
		{
			MInOut inout = (MInOut)po;
			int JP_Contract_ID = inout.get_ValueAsInt("JP_Contract_ID");
			if(JP_Contract_ID <= 0)
				return null;

			MContract contract = MContract.get(Env.getCtx(), JP_Contract_ID);
			if(contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
			{
				int JP_ContractContent_ID = inout.get_ValueAsInt("JP_ContractContent_ID");
				MContractContent content = MContractContent.get(Env.getCtx(), JP_ContractContent_ID);

				//Check Mandetory - JP_ContractProcPeriod_ID
				MInOutLine[] lines = inout.getLines();
				int JP_ContractLine_ID = 0;
				int JP_ContractProcPeriod_ID = 0;

				for(int i = 0; i < lines.length; i++)
				{
					int ReversalLine_ID = lines[i].getReversalLine_ID();
					if(ReversalLine_ID > 0)
					{
						MInOutLine ReversalLine = new MInOutLine(Env.getCtx(),ReversalLine_ID, po.get_TrxName());
						lines[i].set_ValueNoCheck("JP_ContractLine_ID", ReversalLine.get_Value("JP_ContractLine_ID"));
						lines[i].set_ValueNoCheck("JP_ContractProcPeriod_ID", ReversalLine.get_Value("JP_ContractProcPeriod_ID"));
						lines[i].saveEx(po.get_TrxName());
					}

					JP_ContractLine_ID = lines[i].get_ValueAsInt("JP_ContractLine_ID");
					JP_ContractProcPeriod_ID = lines[i].get_ValueAsInt("JP_ContractProcPeriod_ID");
					if(JP_ContractLine_ID > 0 && JP_ContractProcPeriod_ID <= 0)
					{
						if(content.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceipt)
								||content.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceiptInvoice))
						{
							return Msg.getMsg(Env.getCtx(), "FillMandatory") + Msg.getElement(Env.getCtx(), MContractProcPeriod.COLUMNNAME_JP_ContractProcPeriod_ID)
							+ " - " + Msg.getElement(Env.getCtx(),  MInOutLine.COLUMNNAME_Line) + " : " + lines[i].getLine();
						}
					}

				}//for i

			}//if

		}//TIMING_BEFORE_PREPARE



		//Create Recognition When Ship/Receipt Complete
		if(timing == ModelValidator.TIMING_AFTER_COMPLETE)
		{
			int JP_Contract_ID = po.get_ValueAsInt("JP_Contract_ID");
			if(JP_Contract_ID <= 0)
				return null;

			MContract contract = MContract.get(Env.getCtx(), JP_Contract_ID);
			if(!contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract)
					&& !contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_SpotContract))
				return null;

			int JP_ContractContent_ID = po.get_ValueAsInt("JP_ContractContent_ID");
			if(JP_ContractContent_ID <= 0)
				return null;

			MContractContent content = MContractContent.get(Env.getCtx(), JP_ContractContent_ID);
			if(!content.getJP_Contract_Acct().isPostingRecognitionDocJP())
				return null;


			/** Create Recognition*/
			MInOut io = (MInOut)po;
			String trxName = po.get_TrxName();
			boolean isReversal = io.isReversal();
			if(isReversal)
			{
				int original_InOut_ID = io.getReversal_ID();
				reversalRecognition(io, timing, true, original_InOut_ID);

			}else{// Create Recognition

				MOrder order = null;
				MRecognition recognition = null;
				boolean isRMA = false;
				if(io.getC_Order_ID() > 0)
				{
					order = new MOrder(po.getCtx(), io.getC_Order_ID(), trxName);
					MDocType orderDocType = MDocType.get(po.getCtx(), order.getC_DocTypeTarget_ID());
					if(orderDocType.get_ValueAsInt("JP_DocTypeRecognition_ID") == 0)
						return null;

					recognition = new MRecognition (order, orderDocType.get_ValueAsInt("JP_DocTypeRecognition_ID") , io.getDateAcct());//JPIERE-0295

				}else if(io.getM_RMA_ID() > 0){

					isRMA = true;
					MRMA rma = new MRMA(Env.getCtx(),io.getM_RMA_ID(),trxName);
					int JP_Order_ID = rma.get_ValueAsInt("JP_Order_ID");
					if(JP_Order_ID == 0)
						return null;

					order = new MOrder(po.getCtx(), JP_Order_ID, trxName);
					MDocType orderDocType = MDocType.get(po.getCtx(), order.getC_DocTypeTarget_ID());
					if(orderDocType.get_ValueAsInt("JP_DocTypeRecognition_ID") == 0)
						return null;

					recognition = new MRecognition (order, orderDocType.get_ValueAsInt("JP_DocTypeRecognition_ID") , io.getDateAcct());//JPIERE-0295
					MDocType odt = MDocType.get(order.getCtx(), rma.getC_DocType_ID());
					if (odt != null)
					{
						int C_DocTypeTarget_ID = odt.get_ValueAsInt("JP_DocTypeRecognition_ID");
						if (C_DocTypeTarget_ID <= 0)
							throw new AdempiereException("@NotFound@ @C_DocTypeInvoice_ID@ - @C_DocType_ID@:"+odt.get_Translation(MDocType.COLUMNNAME_Name));

						recognition.setC_DocTypeTarget_ID(C_DocTypeTarget_ID);
					}
					recognition.setM_RMA_ID(io.getM_RMA_ID());
				}

				recognition.setM_InOut_ID(io.getM_InOut_ID());
				recognition.setMovementDate(io.getMovementDate());
				if (!recognition.save(trxName))
				{
					return Msg.getMsg(Env.getCtx(),"JP_CouldNotCreate") + " " + Msg.getElement(Env.getCtx(),"JP_Recognition_ID") +" : "+ io.getDocumentInfo();
				}

				MInOutLine[] sLines = io.getLines(false);
				for (int i = 0; i < sLines.length; i++)
				{
					MInOutLine sLine = sLines[i];
					//
					MRecognitionLine rcogLine = new MRecognitionLine(recognition);
					rcogLine.setRecogLine(sLine);
					if(isRMA)
					{
						int M_RMALine_ID = sLine.getM_RMALine_ID();
						MRMALine rmaLine = new MRMALine(Env.getCtx(),M_RMALine_ID, trxName);
						int JP_OrderLine_ID = rmaLine.get_ValueAsInt("JP_OrderLine_ID");
						rcogLine.setC_OrderLine_ID(JP_OrderLine_ID);
					}
					rcogLine.set_ValueNoCheck("JP_ProductExplodeBOM_ID", sLine.get_Value("JP_ProductExplodeBOM_ID"));//JPIERE-0295
					//	Qty = Delivered
					if (sLine.sameOrderLineUOM())
						rcogLine.setQtyEntered(sLine.getQtyEntered());
					else
						rcogLine.setQtyEntered(sLine.getMovementQty());
					rcogLine.setQtyInvoiced(sLine.getMovementQty());
					rcogLine.setJP_QtyRecognized(sLine.getMovementQty());
					rcogLine.setJP_TargetQtyRecognized(sLine.getMovementQty());
					rcogLine.setJP_ContractLine_ID(sLine.get_ValueAsInt("JP_ContractLine_ID"));
					rcogLine.setJP_ContractProcPeriod_ID(sLine.get_ValueAsInt("JP_ContractProcPeriod_ID"));
					if (!rcogLine.save(trxName))
					{
						return Msg.getMsg(Env.getCtx(),"JP_CouldNotCreate") + " " + Msg.getElement(Env.getCtx(),"JP_RecognitionLine_ID") +" : "+ recognition.getDocumentInfo();
					}

				}//for


				String docAction = content.getJP_Contract_Acct().getDocAction();
				if(docAction == null)
				{
					;//Noting to do. DocStatus is Draft
				}else{

					if (!recognition.processIt(docAction))
						throw new AdempiereException("Failed when processing document - " + recognition.getProcessMsg());

				}

				if (!recognition.getDocStatus().equals(DocAction.STATUS_Completed))
				{
					recognition.saveEx(trxName);
				}
			}// Create Recognition

		}//if(timing == ModelValidator.TIMING_AFTER_COMPLETE)


		if(timing == ModelValidator.TIMING_AFTER_REVERSEACCRUAL
				|| timing == ModelValidator.TIMING_AFTER_REVERSEACCRUAL
				|| timing == ModelValidator.TIMING_AFTER_VOID )
		{
			MInOut io = (MInOut)po;
			reversalRecognition(io, timing, false, io.getM_InOut_ID());

		}//if(timing == ModelValidator.TIMING_AFTER_REVERSEACCRUAL

		return null;
	}

	private boolean reversalRecognition(MInOut io, int timing, boolean isReversal, int original_InOut_ID)
	{
		String trxName = io.get_TrxName();
		MRecognition[] recogs = MRecognition.getRecognitionsByInOut(Env.getCtx(), io.getM_InOut_ID(), isReversal, original_InOut_ID, trxName);
		for(int i = 0; i < recogs.length; i++)
		{
			MRecognition recog = recogs[i];
			if(recog.getDocStatus().equals(DocAction.STATUS_Completed))
			{
				if(timing == ModelValidator.TIMING_AFTER_REVERSEACCRUAL)
				{
					recog.processIt(DocAction.ACTION_Reverse_Accrual);

				}else if(timing == ModelValidator.TIMING_AFTER_REVERSECORRECT){

					recog.processIt(DocAction.ACTION_Reverse_Correct);

				}else{

					recog.processIt(DocAction.ACTION_Reverse_Accrual);

				}

			}else{

				if(!recog.getDocStatus().equals(DocAction.ACTION_Complete))
				{
					recog.processIt(DocAction.ACTION_Void);
				}else{

					if(MPeriod.isOpen(Env.getCtx(), recog.getDateAcct(), recog.getC_DocType().getDocBaseType(), io.getAD_Org_ID()))
					{
						recog.processIt(DocAction.ACTION_Reverse_Correct);
					}else{
						recog.processIt(DocAction.ACTION_Reverse_Accrual);
					}

				}

			}

			recog.saveEx(trxName);

		}//for i

		return true;
	}

	/**
	 * Order Validate
	 *
	 * @param po
	 * @param type
	 * @return
	 */
	private String inOutValidate(PO po, int type)
	{
		//JPIERE-0408:Set Counter Doc Info
		if( type == ModelValidator.TYPE_BEFORE_NEW)
		{
			MInOut io = (MInOut)po;
			if(io.getRef_InOut_ID() > 0) //This is Counter doc
			{
				MInOut counterIO = new MInOut(po.getCtx(), io.getRef_InOut_ID(), po.get_TrxName());
				if(counterIO.get_ValueAsInt("JP_Contract_ID") > 0)
				{
					if(io.get_ValueAsInt("JP_Contract_ID") <= 0)
					{
						MContract counterContract = MContract.get(po.getCtx(), counterIO.get_ValueAsInt("JP_Contract_ID"));
						if(counterContract != null && counterContract.getJP_Contract_ID() > 0 && counterContract.getJP_CounterContract_ID() > 0)
							io.set_ValueNoCheck("JP_Contract_ID", counterContract.getJP_CounterContract_ID());
					}

					if(counterIO.get_ValueAsInt("JP_ContractContent_ID") > 0)
					{
						if(io.get_ValueAsInt("JP_ContractContent_ID") <= 0)
						{
							MContractContent counterContractContent = MContractContent.get(po.getCtx(), counterIO.get_ValueAsInt("JP_ContractContent_ID"));
							if(counterContractContent != null && counterContractContent.getJP_ContractContent_ID() > 0 && counterContractContent.getJP_CounterContractContent_ID() > 0)
								io.set_ValueNoCheck("JP_ContractContent_ID", counterContractContent.getJP_CounterContractContent_ID());
						}
					}

					if(counterIO.get_ValueAsInt("JP_ContractProcPeriod_ID") > 0)
					{
						if(io.get_ValueAsInt("JP_ContractProcPeriod_ID") <= 0)
						{
							io.set_ValueNoCheck("JP_ContractProcPeriod_ID", counterIO.get_ValueAsInt("JP_ContractProcPeriod_ID") );
						}
					}

				}//if(counterIO.get_ValueAsInt("JP_Contract_ID") > 0)

			}//if(io.getRef_InOut_ID() > 0)

		}//JPIERE-0408:Set Counter Doc Info


		String msg = derivativeDocHeaderCommonCheck(po, type);
		if(!Util.isEmpty(msg))
			return msg;

		if( type == ModelValidator.TYPE_BEFORE_NEW
				||( type == ModelValidator.TYPE_BEFORE_CHANGE && ( po.is_ValueChanged(MContract.COLUMNNAME_JP_Contract_ID)
						||   po.is_ValueChanged(MContractContent.COLUMNNAME_JP_ContractContent_ID)
						||   po.is_ValueChanged("C_Order_ID")
						||   po.is_ValueChanged("M_RMA_ID") ) ) )
		{

			MInOut io = (MInOut)po;
			int JP_Contract_ID = io.get_ValueAsInt(MContract.COLUMNNAME_JP_Contract_ID);
			if(JP_Contract_ID > 0)
			{
				MContract contract = MContract.get(Env.getCtx(), JP_Contract_ID);
				//Check to Change Contract Info
				if(type == ModelValidator.TYPE_BEFORE_CHANGE && contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
				{

					MInOutLine[] contractInvoiceLines = getInOutLinesWithContractLine(io);
					if(contractInvoiceLines.length > 0)
					{
						//Contract Info can not be changed because the document contains contract Info lines.
						return Msg.getMsg(Env.getCtx(), "JP_CannotChangeContractInfoForLines");
					}
				}
			}

		}//Type

		return null;
	}



	/**
	 * Order Line Validate
	 *
	 * @param po
	 * @param type
	 * @return
	 */
	private String inOutLineValidate(PO po, int type)
	{
		//JPIERE-0408:Set Counter Doc Line Info
		if( type == ModelValidator.TYPE_BEFORE_NEW)
		{
			MInOutLine ioLine = (MInOutLine)po;
			if(ioLine.getRef_InOutLine_ID() > 0) //This is Counter doc Line
			{
				MInOutLine counterIOLine = new MInOutLine(po.getCtx(), ioLine.getRef_InOutLine_ID(), po.get_TrxName());
				if(counterIOLine.get_ValueAsInt("JP_ContractLine_ID") > 0)
				{
					if(ioLine.get_ValueAsInt("JP_ContractLine_ID") <= 0)
					{
						MContractLine counterContractLine = MContractLine.get(po.getCtx(), counterIOLine.get_ValueAsInt("JP_ContractLine_ID"));
						if(counterContractLine != null && counterContractLine.getJP_ContractLine_ID() > 0 && counterContractLine.getJP_CounterContractLine_ID() > 0)
							ioLine.set_ValueNoCheck("JP_ContractLine_ID", counterContractLine.getJP_CounterContractLine_ID());
					}
				}

				if(counterIOLine.get_ValueAsInt("JP_ContractProcPeriod_ID") > 0)
				{
					if(ioLine.get_ValueAsInt("JP_ContractProcPeriod_ID") <= 0)
					{
						ioLine.set_ValueNoCheck("JP_ContractProcPeriod_ID", counterIOLine.get_ValueAsInt("JP_ContractProcPeriod_ID") );
					}

				}

			}//if(ioLine.getRef_InOutLine_ID() > 0)

		}//JPIERE-0408:Set Counter Doc Line Info


		String msg = derivativeDocLineCommonCheck(po, type);
		if(!Util.isEmpty(msg))
			return msg;

		/** Ref:JPiereContractInvoiceValidator  AND JPiereContractRecognitionValidator */
		if(type == ModelValidator.TYPE_BEFORE_NEW
				||( type == ModelValidator.TYPE_BEFORE_CHANGE && ( po.is_ValueChanged(MContractLine.COLUMNNAME_JP_ContractLine_ID)
						||  po.is_ValueChanged("C_OrderLine_ID") ||  po.is_ValueChanged("M_RMALine_ID") ||  po.is_ValueChanged("JP_ContractProcPeriod_ID")) ))
		{
			MInOutLine ioLine = (MInOutLine)po;
			int JP_ContractLine_ID = ioLine.get_ValueAsInt("JP_ContractLine_ID");
			int JP_ContractContent_ID = ioLine.getParent().get_ValueAsInt("JP_ContractContent_ID");
			int JP_Contract_ID = ioLine.getParent().get_ValueAsInt("JP_Contract_ID");

			if(JP_Contract_ID <= 0)
				return null;

			MContract contract = MContract.get(Env.getCtx(), JP_Contract_ID);
			if(!contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract)
					&& !contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_SpotContract))
				return null;

			/** Common Check Period Contract & Spot Contract */
			int C_OrderLine_ID = ioLine.getC_OrderLine_ID();
			int M_RMALine_ID = ioLine.getM_RMALine_ID();
			if(C_OrderLine_ID <= 0 && M_RMALine_ID <= 0)
			{
				Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "C_OrderLine_ID") + " or " + Msg.getElement(Env.getCtx(), "M_RMALine_ID")};
				return Msg.getMsg(Env.getCtx(), "JP_InCaseOfPeriodContractAndSpotContract") + Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs);
			}

			if(ioLine.getC_OrderLine_ID() > 0)
			{
				//You can not bundle different Order document.
				if(ioLine.getC_OrderLine().getC_Order_ID() != ioLine.getParent().getC_Order_ID())
					return Msg.getMsg(Env.getCtx(), "JP_InCaseOfPeriodContractAndSpotContract") + Msg.getMsg(Env.getCtx(),"JP_CanNotBundleDifferentOrder");

			}else if(ioLine.getM_RMALine_ID() > 0){

				if(ioLine.getM_RMALine().getM_RMA_ID() != ioLine.getParent().getM_RMA_ID())
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
			int ioLine_ContractProcPeriod_ID = ioLine.get_ValueAsInt("JP_ContractProcPeriod_ID");
			MContractContent content = MContractContent.get(Env.getCtx(), JP_ContractContent_ID);
			if(contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
			{

				if(content.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceipt)
						||content.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceiptInvoice))
				{
					if(type == ModelValidator.TYPE_BEFORE_CHANGE)
					{
						//Check Mandetory
						if(ioLine_ContractProcPeriod_ID <= 0)
						{
							Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractProcPeriod_ID")};
							return Msg.getMsg(Env.getCtx(), "JP_InCaseOfCreateDerivativeDocPolicy") + Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs);
						}

						//Check Contract Process Period - Calender
						MContractProcPeriod ioLine_ContractProcPeriod = MContractProcPeriod.get(Env.getCtx(), ioLine_ContractProcPeriod_ID);
						if(ioLine_ContractProcPeriod.getJP_ContractCalender_ID() != contractLine.getJP_ContractCalender_InOut_ID())
						{
							//Please select the Contract Process Period that belong to Calender of Contract Content line.
							return Msg.getMsg(Env.getCtx(), "JP_SelectContractProcPeriodBelongToContractLine");
						}

						//Check valid Contract Period
						MInOut inOut =ioLine.getParent();
						MContractProcPeriod ioPeriod = MContractProcPeriod.get(Env.getCtx(), inOut.get_ValueAsInt("JP_ContractProcPeriod_ID"));
						if(ioPeriod.getStartDate().compareTo(ioLine_ContractProcPeriod.getStartDate()) > 0
								|| (ioPeriod.getEndDate() != null && ioPeriod.getEndDate().compareTo(ioLine_ContractProcPeriod.getEndDate()) < 0) )
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

			return null;

	}

	private MInOutLine[] getInOutLinesWithContractLine(MInOut io)
	{
		String whereClauseFinal = "M_InOut_ID=? AND JP_ContractLine_ID IS NOT NULL ";
		List<MInOutLine> list = new Query(Env.getCtx(), MInOutLine.Table_Name, whereClauseFinal, io.get_TrxName())
										.setParameters(io.getM_InOut_ID())
										.setOrderBy(MInOutLine.COLUMNNAME_Line)
										.list();
		return list.toArray(new MInOutLine[list.size()]);
	}

	@Override
	public String factsValidate(MAcctSchema schema, List<Fact> facts, PO po)
	{
		if(po.get_TableName().equals(MInOut.Table_Name))
		{
			int JP_ContractContent_ID = po.get_ValueAsInt("JP_ContractContent_ID");
			if(JP_ContractContent_ID > 0)
			{
				MInOut inOut = (MInOut)po;
				//Set Order Info
				for(Fact fact : facts)
				{
					FactLine[]  factLine = fact.getLines();
					for(int i = 0; i < factLine.length; i++)
					{
						if(inOut.getC_Order_ID() > 0)
						{
							factLine[i].set_ValueNoCheck("JP_Order_ID", inOut.getC_Order_ID());
						}else if(inOut.getM_RMA_ID() > 0){
							int M_RMA_ID = inOut.getM_RMA_ID();
							MRMA rma = new MRMA (Env.getCtx(),M_RMA_ID,null);
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
