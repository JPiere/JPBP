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

import org.compiere.model.MClient;
import org.compiere.model.MInOut;
import org.compiere.model.MInOutLine;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Msg;

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
public class JPiereContractInOutValidator implements ModelValidator {

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
		engine.addDocValidate(MInOutLine.Table_Name, this);

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
		if( type == ModelValidator.TYPE_BEFORE_NEW
				||( type == ModelValidator.TYPE_BEFORE_CHANGE && ( po.is_ValueChanged(MContract.COLUMNNAME_JP_Contract_ID)
						||   po.is_ValueChanged(MContractContent.COLUMNNAME_JP_ContractContent_ID)
						||   po.is_ValueChanged("C_Order_ID") ) ) )
		{
			
			MInOut io = (MInOut)po;			
			int C_Order_ID = io.getC_Order_ID();
			
			//Prohibit update
			if(type == ModelValidator.TYPE_BEFORE_CHANGE)
			{
				if(io.getLines().length > 0)
					return Msg.getMsg(Env.getCtx(), "JP_CannotChangeContractInfoForLines");//Contract Info cannot be changed because the Document have lines
			}
			
			//Check C_Order_ID
			if(C_Order_ID == 0)
			{
				io.set_ValueNoCheck("JP_Contract_ID", null);
				io.set_ValueNoCheck("JP_ContractContent_ID", null);
				io.set_ValueNoCheck("JP_ContractProcPeriod_ID", null);
				
				return null;
			}
			
			//Check JP_Contract_ID, JP_ContractContent_ID, JP_ContractProcPeriod_ID
			MOrder order = new MOrder(Env.getCtx(), C_Order_ID, io.get_TrxName());
			int JP_Contract_ID = order.get_ValueAsInt("JP_Contract_ID");
			if(JP_Contract_ID == 0)
			{
				io.set_ValueNoCheck("JP_ContractContent_ID", null);
				io.set_ValueNoCheck("JP_ContractProcPeriod_ID", null);
				
				return null;			
			}
			
			io.set_ValueNoCheck("JP_Contract_ID", JP_Contract_ID);
			MContract contract = MContract.get(Env.getCtx(), JP_Contract_ID);
			if(contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
			{
				/** In case of Period Contract, order has JP_ContractContent_ID and JP_ContractProcPeriod_ID always*/
				io.set_ValueNoCheck("JP_ContractContent_ID", order.get_ValueAsInt("JP_ContractContent_ID"));
				io.set_ValueNoCheck("JP_ContractProcPeriod_ID", order.get_ValueAsInt("JP_ContractProcPeriod_ID"));
				
			}else if (contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_SpotContract)){
				
				/** In case of Spot Contract, order has JP_ContractContent_ID always*/
				io.set_ValueNoCheck("JP_ContractContent_ID", order.get_ValueAsInt("JP_ContractContent_ID"));
				io.set_ValueNoCheck("JP_ContractProcPeriod_ID", null);
				
			}else if (contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_GeneralContract)){
				
				io.set_ValueNoCheck("JP_ContractContent_ID", null);
				io.set_ValueNoCheck("JP_ContractProcPeriod_ID", null);
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
		if(type == ModelValidator.TYPE_BEFORE_NEW
				||( type == ModelValidator.TYPE_BEFORE_CHANGE && ( po.is_ValueChanged(MContractLine.COLUMNNAME_JP_ContractLine_ID)
						||   po.is_ValueChanged("C_OrderLine_ID")) ))
		{
			MInOutLine ioLine = (MInOutLine)po;			
			int C_OrderLine_ID = ioLine.getC_OrderLine_ID();
			
			if(C_OrderLine_ID == 0)
			{
				ioLine.set_ValueNoCheck("JP_ContractLine_ID", null);
				ioLine.set_ValueNoCheck("JP_ContractProcPeriod_ID", null);
				
				return null;
				
			}
				
			MOrderLine orderLine = new MOrderLine(Env.getCtx(), C_OrderLine_ID, ioLine.get_TrxName());
			int JP_ContractLine_ID = orderLine.get_ValueAsInt("JP_ContractLine_ID");
			if(JP_ContractLine_ID == 0)
			{
				ioLine.set_ValueNoCheck("JP_ContractLine_ID", null);
				ioLine.set_ValueNoCheck("JP_ContractProcPeriod_ID", null);
				return null;
				
			}
				
			MContractLine contractLine = MContractLine.get(Env.getCtx(), JP_ContractLine_ID);
			MContract contract = contractLine.getParent().getParent();
			MContractAcct contractAcct = MContractAcct.get(Env.getCtx(), contractLine.getParent().getJP_Contract_Acct_ID());
			
			//Check Relation of Contract Cotent
			if(contractLine.getJP_ContractContent_ID() == ioLine.get_ValueAsInt("JP_ContractContent_ID"))
			{
				//You can select Contract Content Line that is belong to Contract content
				return Msg.getMsg(Env.getCtx(), "Invalid") + Msg.getElement(Env.getCtx(), "JP_ContractLine_ID") + Msg.getMsg(Env.getCtx(), "JP_Diff_ContractContentLine");
			}
			
			
			//Check Order Info Mandetory
			if(contractAcct.isOrderInfoMandatoryJP())
			{
				if(ioLine.getC_OrderLine().getC_Order_ID() != ioLine.getParent().getC_Order_ID())
					return "注文情報必須の契約の場合、異なる受発注伝票の明細を含める事はできません。";//TODO メッセージ化
			}
			
			
			//Check JP_ContractLine_ID, JP_ContractProcPeriod_ID
			ioLine.set_ValueNoCheck("JP_ContractLine_ID", JP_ContractLine_ID);

			if(contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
			{
				ioLine.set_ValueNoCheck("JP_ContractLine_ID", orderLine.get_ValueAsInt("JP_ContractLine_ID"));
				ioLine.set_ValueNoCheck("JP_ContractProcPeriod_ID", null);
				
			}else if (contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_SpotContract)){
				
				ioLine.set_ValueNoCheck("JP_ContractLine_ID", null);
				ioLine.set_ValueNoCheck("JP_ContractProcPeriod_ID", null);
				
			}else if (contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_GeneralContract)){
				
				ioLine.set_ValueNoCheck("JP_ContractLine_ID", null);
				ioLine.set_ValueNoCheck("JP_ContractProcPeriod_ID", null);
			}
			
		}//if(type == ModelValidator.TYPE_BEFORE_NEW)
		
		
		if(type == ModelValidator.TYPE_BEFORE_CHANGE)
		{
			MInOutLine ioLine = (MInOutLine)po;			
			int C_OrderLine_ID = ioLine.getC_OrderLine_ID();
			int JP_ContractLine_ID = ioLine.get_ValueAsInt("JP_ContractLine_ID");
			if(C_OrderLine_ID > 0 && JP_ContractLine_ID > 0)
			{
				MContractLine contractLine = MContractLine.get(Env.getCtx(), JP_ContractLine_ID);
				if(contractLine.getParent().getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
				{
					/** Check Mandetory Proc Period When Period Contract */
					int JP_ContractProcPeriod_ID = ioLine.get_ValueAsInt("JP_ContractProcPeriod_ID");
					if(JP_ContractProcPeriod_ID <= 0)
					{
						Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractProcPeriod_ID")};
						return Msg.getMsg(Env.getCtx(), "JP_InCaseOfPeriodContract") + Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs);
					}else{
						
						/** Check Derivative Doc Proc Period in Base Doc Proc Period */
						if(ioLine.is_ValueChanged("JP_ContractProcPeriod_ID"))
						{
							MOrderLine orderLine = new MOrderLine(Env.getCtx(),C_OrderLine_ID ,ioLine.get_TrxName());
							MContractProcPeriod derivativeDocPeriod = MContractProcPeriod.get(Env.getCtx(), JP_ContractProcPeriod_ID);
							if(!derivativeDocPeriod.isContainedBaseDocContractProcPeriod(orderLine.get_ValueAsInt("JP_ContractProcPeriod_ID")))
							{
								//Contract Period that is derivative doc line is not corresponding with Contract Period that is base doc line.
								return Msg.getMsg(Env.getCtx(), "JP_CorrespondingContractProcPeriod");
							}
							
						}//if(ioLine.is_ValueChanged("JP_ContractProcPeriod_ID"))
						
					}//if(JP_ContractProcPeriod_ID <= 0)
					
				}//if(contractLine.getParent().getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
				
			}//if(C_OrderLine_ID > 0 && JP_ContractLine_ID > 0)
			
		}//if(type == ModelValidator.TYPE_BEFORE_CHANGE)
		
		return null;
	}
}
