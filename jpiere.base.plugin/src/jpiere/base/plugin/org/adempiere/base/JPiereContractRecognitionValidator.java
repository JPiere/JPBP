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

import org.compiere.model.MClient;
import org.compiere.model.MInOut;
import org.compiere.model.MInOutLine;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.MContract;
import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractProcPeriod;
import jpiere.base.plugin.org.adempiere.model.MRecognition;
import jpiere.base.plugin.org.adempiere.model.MRecognitionLine;



/**
 *  JPIERE-0363: Contract Management
 *  JPiere Contract Recognition Validator
 *
 *  @author  Hideaki Hagiwara（h.hagiwara@oss-erp.co.jp）
 *
 */
public class JPiereContractRecognitionValidator extends AbstractContractValidator  implements ModelValidator {

	private static CLogger log = CLogger.getCLogger(JPiereContractRecognitionValidator.class);
	private int AD_Client_ID = -1;
	private int AD_Org_ID = -1;
	private int AD_Role_ID = -1;
	private int AD_User_ID = -1;


	@Override
	public void initialize(ModelValidationEngine engine, MClient client) 
	{
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();
		engine.addModelChange(MRecognition.Table_Name, this);
		engine.addModelChange(MRecognitionLine.Table_Name, this);
		engine.addDocValidate(MRecognition.Table_Name, this);

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
				return null;
			
			MContract contract = MContract.get(Env.getCtx(), JP_Contract_ID);
			if(contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
			{
				
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
						return Msg.getMsg(Env.getCtx(), "FillMandatory") + Msg.getElement(Env.getCtx(), MContractProcPeriod.COLUMNNAME_JP_ContractProcPeriod_ID)
													+ " - " + Msg.getElement(Env.getCtx(),  MRecognitionLine.COLUMNNAME_Line) + " : " + lines[i].getLine();
					}
				}
				
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
						||   po.is_ValueChanged("C_Order_ID") ) ) )
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
