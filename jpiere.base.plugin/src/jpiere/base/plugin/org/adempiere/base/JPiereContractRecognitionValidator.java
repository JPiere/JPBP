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
import java.util.logging.Level;

import org.adempiere.webui.window.Dialog;
import org.compiere.model.MClient;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.model.Query;
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
 *  JPiere Contract Recognition Validator
 *
 *  JPIERE-0363: Contract Management
 *
 *
 *  @author  Hideaki Hagiwara（h.hagiwara@oss-erp.co.jp）
 *
 */
public class JPiereContractRecognitionValidator extends AbstractContractValidator  implements ModelValidator {

	private static CLogger log = CLogger.getCLogger(JPiereContractRecognitionValidator.class);
	private int AD_Client_ID = -1;


	@Override
	public void initialize(ModelValidationEngine engine, MClient client)
	{
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();
		engine.addModelChange(MRecognition.Table_Name, this);
		engine.addModelChange(MRecognitionLine.Table_Name, this);
		engine.addDocValidate(MRecognition.Table_Name, this);

		if (log.isLoggable(Level.FINE)) log.fine("Initialize JPiereContractRecognitionValidator");

	}

	@Override
	public int getAD_Client_ID()
	{
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
		if(po.get_TableName().equals(MRecognition.Table_Name))
		{
			return recognitionValidate(po, type);

		}else if(po.get_TableName().equals(MRecognitionLine.Table_Name)){

			return recognitionLineValidate(po, type);
		}

		return null;
	}

	@Override
	public String docValidate(PO po, int timing)
	{
		if(timing == ModelValidator.TIMING_BEFORE_PREPARE)
		{
			MRecognition recog = (MRecognition)po;
			int JP_Contract_ID = recog.get_ValueAsInt("JP_Contract_ID");
			if(JP_Contract_ID <= 0)
			{
				return Msg.getMsg(Env.getCtx(), "FillMandatory") + Msg.getElement(Env.getCtx(), MRecognition.COLUMNNAME_JP_Contract_ID);
			}

			MContract contract = MContract.get(Env.getCtx(), JP_Contract_ID);
			if(contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
			{
				int JP_ContractContent_ID = recog.getJP_ContractContent_ID();
				MContractContent content = MContractContent.get(Env.getCtx(), JP_ContractContent_ID);

				//Check Mandetory - JP_ContractProcPeriod_ID
				MRecognitionLine[] lines = recog.getLines();
				int JP_ContractLine_ID = 0;
				int JP_ContractProcPeriod_ID = 0;
				for(int i = 0; i < lines.length; i++)
				{
					JP_ContractLine_ID = lines[i].get_ValueAsInt("JP_ContractLine_ID");
					JP_ContractProcPeriod_ID = lines[i].get_ValueAsInt("JP_ContractProcPeriod_ID");
					if(JP_ContractLine_ID > 0 && JP_ContractProcPeriod_ID <= 0)
					{
						if(content.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceipt)
								||content.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceiptInvoice))
						{
							return Msg.getMsg(Env.getCtx(), "FillMandatory") + Msg.getElement(Env.getCtx(), MContractProcPeriod.COLUMNNAME_JP_ContractProcPeriod_ID)
														+ " - " + Msg.getElement(Env.getCtx(),  MRecognitionLine.COLUMNNAME_Line) + " : " + lines[i].getLine();
						}
					}
				}//For
			}

		}//TIMING_BEFORE_PREPARE

		return null;
	}


	/**
	 * Recognition Validate
	 *
	 * @param po
	 * @param type
	 * @return
	 */
	private String recognitionValidate(PO po, int type)
	{
		String msg = derivativeDocHeaderCommonCheck(po, type);
		if(!Util.isEmpty(msg))
			return msg;

		if( type == ModelValidator.TYPE_BEFORE_NEW
				||( type == ModelValidator.TYPE_BEFORE_CHANGE && ( po.is_ValueChanged(MContract.COLUMNNAME_JP_Contract_ID)
						||   po.is_ValueChanged(MContractContent.COLUMNNAME_JP_ContractContent_ID)
						||   po.is_ValueChanged("C_Order_ID")
						||   po.is_ValueChanged("M_RMA_ID") ) ) )
		{
			MRecognition recog = (MRecognition)po;
			int JP_Contract_ID = recog.get_ValueAsInt(MContract.COLUMNNAME_JP_Contract_ID);
			MContract contract = MContract.get(Env.getCtx(), JP_Contract_ID);

			//Check to Change Contract Info
			if(type == ModelValidator.TYPE_BEFORE_CHANGE && contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
			{
				MRecognitionLine[] contractInvoiceLines = getRecognitionLinesWithContractLine(recog);
				if(contractInvoiceLines.length > 0)
				{
					//Contract Info can not be changed because the document contains contract Info lines.
					return Msg.getMsg(Env.getCtx(), "JP_CannotChangeContractInfoForLines");
				}
			}

			//JP_Contract_ID is Mandetory field in Recognition doc
			if(!contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract)
					&& !contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_SpotContract))
			{
				msg = Msg.getMsg(Env.getCtx(), "Invalid")+ " "+Msg.getElement(Env.getCtx(), "JP_Contract_ID") + "  "
					+ Msg.getMsg(Env.getCtx(), "JP_ToBeConfirmed") + "  " + Msg.getElement(Env.getCtx(), "JP_ContractType");
				return msg;
			}

			//JP_ContractContent_ID is Mandetory field in Recognition doc.
			MContractContent content = MContractContent.get(Env.getCtx(), po.get_ValueAsInt("JP_ContractContent_ID"));
			if(!content.getJP_Contract_Acct().isPostingRecognitionDocJP())
			{
				msg = Msg.getMsg(Env.getCtx(), "Invalid")+ " "+Msg.getElement(Env.getCtx(), "JP_ContractContent_ID") + "  "
						+ Msg.getMsg(Env.getCtx(), "JP_ToBeConfirmed") + "  " + Msg.getElement(Env.getCtx(), "JP_Contract_Acct_ID");
				return msg;
			}

		}//Type

		return null;
	}



	/**
	 * Recognition Line Validate
	 *
	 * @param po
	 * @param type
	 * @return
	 */
	private String recognitionLineValidate(PO po, int type)
	{
		String msg = derivativeDocLineCommonCheck(po, type);
		if(!Util.isEmpty(msg))
			return msg;

		/** Ref:JPiereContractInOutValidator AND JPiereContractInvoiceValidator*/
		if(type == ModelValidator.TYPE_BEFORE_NEW
				||( type == ModelValidator.TYPE_BEFORE_CHANGE && ( po.is_ValueChanged(MContractLine.COLUMNNAME_JP_ContractLine_ID)
						||   po.is_ValueChanged("C_OrderLine_ID") ||   po.is_ValueChanged("M_RMALine_ID") ) ))
		{
			MRecognitionLine recogLine = (MRecognitionLine)po;
			int JP_Contract_ID = recogLine.getParent().get_ValueAsInt("JP_Contract_ID");
			int JP_ContractContent_ID = recogLine.getParent().get_ValueAsInt("JP_ContractContent_ID");
			int JP_ContractLine_ID = recogLine.get_ValueAsInt("JP_ContractLine_ID");

			if(JP_Contract_ID <= 0)
				return null;

			MContract contract = MContract.get(Env.getCtx(), JP_Contract_ID);
			if(!contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract)
					&& !contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_SpotContract))
				return null;


			//Check Period Contract & Spot Contract fron now on
			int C_OrderLine_ID = recogLine.getC_OrderLine_ID();
			int M_RMALine_ID = recogLine.getM_RMALine_ID();
			int M_InOutLine_ID = recogLine.getM_InOutLine_ID();
			if(C_OrderLine_ID == 0 && M_RMALine_ID == 0 && M_InOutLine_ID == 0)
				return null;

			if(C_OrderLine_ID <= 0 && M_RMALine_ID <= 0 && M_InOutLine_ID != 0)
			{
				Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "C_OrderLine_ID") + " or " + Msg.getElement(Env.getCtx(), "M_RMALine_ID")};
				return Msg.getMsg(Env.getCtx(), "JP_InCaseOfPeriodContractAndSpotContract") + Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs);
			}

			//Check Single Order or RMA
			if(recogLine.getParent().getC_Order_ID() > 0 && recogLine.getC_OrderLine_ID() > 0)
			{
				//You can not bundle different Order document.
				if(recogLine.getC_OrderLine().getC_Order_ID() != recogLine.getParent().getC_Order_ID())
					return Msg.getMsg(Env.getCtx(), "JP_InCaseOfPeriodContractAndSpotContract") + Msg.getMsg(Env.getCtx(),"JP_CanNotBundleDifferentOrder");

			}else if(recogLine.getParent().getM_RMA_ID() > 0 && recogLine.getM_RMALine_ID() > 0){

				//You can not bundle different RMA document.
				if(recogLine.getM_RMALine().getM_RMA_ID() != recogLine.getParent().getM_RMA_ID())
					return  Msg.getMsg(Env.getCtx(), "JP_InCaseOfPeriodContractAndSpotContract") + Msg.getMsg(Env.getCtx(),"JP_CanNotBundleDifferentRMA");
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
			int recogLine_ContractProcPeriod_ID = recogLine.get_ValueAsInt("JP_ContractProcPeriod_ID");
			MContractContent content = MContractContent.get(Env.getCtx(), JP_ContractContent_ID);
			if(contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
			{
				if(content.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceipt)
						||content.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceiptInvoice))
				{
					if(type == ModelValidator.TYPE_BEFORE_CHANGE)
					{
						//Check Mandetory
						if(recogLine_ContractProcPeriod_ID <= 0)
						{
							Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractProcPeriod_ID")};
							return Msg.getMsg(Env.getCtx(), "JP_InCaseOfCreateDerivativeDocPolicy") + Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs);
						}

						//Check Contract Process Period - Calender
						MContractProcPeriod recogLine_ContractProcPeriod = MContractProcPeriod.get(Env.getCtx(), recogLine_ContractProcPeriod_ID);
						if(recogLine_ContractProcPeriod.getJP_ContractCalender_ID() != contractLine.getJP_ContractCalender_InOut_ID())
						{
							//Please select the Contract Process Period that belong to Calender of Contract Content line.
							return Msg.getMsg(Env.getCtx(), "JP_SelectContractProcPeriodBelongToContractLine");
						}

						//Check valid Contract Period
						MRecognition recog =recogLine.getParent();
						MContractProcPeriod recogPeriod = MContractProcPeriod.get(Env.getCtx(), recog.get_ValueAsInt("JP_ContractProcPeriod_ID"));
						if(recogPeriod.getStartDate().compareTo(recogLine_ContractProcPeriod.getStartDate()) > 0
								|| (recogPeriod.getEndDate() != null && recogPeriod.getEndDate().compareTo(recogLine_ContractProcPeriod.getEndDate()) < 0) )
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
								Dialog.info(0, null, "JP_ContractManagementInfo"
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


	private MRecognitionLine[] getRecognitionLinesWithContractLine(MRecognition recog)
	{
		String whereClauseFinal = "JP_Recognition_ID=? AND JP_ContractLine_ID IS NOT NULL ";
		List<MRecognitionLine> list = new Query(Env.getCtx(), MRecognitionLine.Table_Name, whereClauseFinal, recog.get_TrxName())
										.setParameters(recog.getM_InOut_ID())
										.setOrderBy(MRecognitionLine.COLUMNNAME_Line)
										.list();
		return list.toArray(new MRecognitionLine[list.size()]);
	}

}
