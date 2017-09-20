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
import org.compiere.model.MOrderLine;
import org.compiere.model.MRMALine;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.MContract;
import jpiere.base.plugin.org.adempiere.model.MContractAcct;
import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractLine;
import jpiere.base.plugin.org.adempiere.model.MContractProcPeriod;



/**
 *  JPIERE-0363: Contract Management
 *  JPiere Contract InOut Validator
 *
 *  @author  Hideaki Hagiwara（h.hagiwara@oss-erp.co.jp）
 *
 */
public class JPiereContractInOutValidator extends AbstractContractValidator  implements ModelValidator {

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
			MInOut io = (MInOut)po;
			int JP_Contract_ID = io.get_ValueAsInt("JP_Contract_ID");
			if(JP_Contract_ID <= 0)
				return null;
			
			MContract contract = MContract.get(Env.getCtx(), JP_Contract_ID);
			if(contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
			{
				
				//Check Mandetory - JP_ContractProcPeriod_ID
				MInOutLine[] lines = io.getLines();
				int JP_ContractLine_ID = 0;
				int JP_ContractProcPeriod_ID = 0;
				for(int i = 0; i < lines.length; i++)
				{
					JP_ContractLine_ID = lines[i].get_ValueAsInt("JP_ContractLine_ID");
					JP_ContractProcPeriod_ID = lines[i].get_ValueAsInt("JP_ContractProcPeriod_ID");
					if(JP_ContractLine_ID > 0 && JP_ContractProcPeriod_ID <= 0)
					{
						return Msg.getMsg(Env.getCtx(), "FillMandatory") + Msg.getElement(Env.getCtx(), MContractProcPeriod.COLUMNNAME_JP_ContractProcPeriod_ID)
													+ " - " + Msg.getElement(Env.getCtx(),  MInOutLine.COLUMNNAME_Line) + " : " + lines[i].getLine();
					}
				}
				
			}
			
		}//TIMING_BEFORE_PREPARE
		
		return null;
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
		
		String msg = derivativeDocLineCommonCheck(po, type);	
		if(!Util.isEmpty(msg))
			return msg;
		
		if(type == ModelValidator.TYPE_BEFORE_NEW
				||( type == ModelValidator.TYPE_BEFORE_CHANGE && ( po.is_ValueChanged(MContractLine.COLUMNNAME_JP_ContractLine_ID)
						||   po.is_ValueChanged("C_OrderLine_ID") ||   po.is_ValueChanged("M_RMALine_ID") ) ))
		{
			MInOutLine ioLine = (MInOutLine)po;			
			int C_OrderLine_ID = ioLine.getC_OrderLine_ID();
			int M_RMALine_ID = ioLine.getM_RMALine_ID();
			
			PO baseDoc = null;
			if(C_OrderLine_ID > 0)
				baseDoc = new MOrderLine(Env.getCtx(), C_OrderLine_ID, ioLine.get_TrxName());
			else
				baseDoc = new MRMALine(Env.getCtx(), M_RMALine_ID, ioLine.get_TrxName());

				
			int JP_ContractLine_ID = baseDoc.get_ValueAsInt("JP_ContractLine_ID");
				
			MContractLine contractLine = MContractLine.get(Env.getCtx(), JP_ContractLine_ID);
			MContractAcct contractAcct = MContractAcct.get(Env.getCtx(), contractLine.getParent().getJP_Contract_Acct_ID());
			
			//Check Relation of Contract Cotent
			if(contractLine.getJP_ContractContent_ID() != ioLine.getParent().get_ValueAsInt("JP_ContractContent_ID"))
			{
				//You can select Contract Content Line that is belong to Contract content
				return Msg.getMsg(Env.getCtx(), "Invalid") +" - " +Msg.getElement(Env.getCtx(), "JP_ContractLine_ID") + Msg.getMsg(Env.getCtx(), "JP_Diff_ContractContentLine");
			}
			
			
			//Check Order Info Mandetory
			if(contractAcct.isOrderInfoMandatoryJP())
			{
				if(ioLine.getC_OrderLine().getC_Order_ID() != ioLine.getParent().getC_Order_ID())
					return "注文情報必須の契約の場合、異なる受発注伝票の明細を含める事はできません。";//TODO メッセージ化
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
}
