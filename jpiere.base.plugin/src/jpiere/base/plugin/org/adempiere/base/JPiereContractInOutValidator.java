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
		
		String msg = derivativeDocHeaderCommonCheck(po, type);	
		if(!Util.isEmpty(msg))
			return msg;
		
		if( type == ModelValidator.TYPE_BEFORE_NEW
				||( type == ModelValidator.TYPE_BEFORE_CHANGE && ( po.is_ValueChanged(MContract.COLUMNNAME_JP_Contract_ID)
						||   po.is_ValueChanged(MContractContent.COLUMNNAME_JP_ContractContent_ID)
						||   po.is_ValueChanged("C_Order_ID") ) ) )
		{
			
			String returnValue = checkHeaderContractInfoUpdate(po, type);
			
			if(!Util.isEmpty(returnValue))
				return returnValue;
		}//Type
		
		return null;
	}
	
	
	protected String checkHeaderContractInfoUpdate(PO po, int type) 
	{
		MInOut io = (MInOut)po;			
		
		//Prohibit update
		if(type == ModelValidator.TYPE_BEFORE_CHANGE)
		{
			if(io.getLines().length > 0)
				return Msg.getMsg(Env.getCtx(), "JP_CannotChangeContractInfoForLines");//Contract Info cannot be changed because the Document have lines
		}
		
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
						||   po.is_ValueChanged("C_OrderLine_ID")) ))
		{
			MInOutLine ioLine = (MInOutLine)po;			
			int C_OrderLine_ID = ioLine.getC_OrderLine_ID();
			
				
			MOrderLine orderLine = new MOrderLine(Env.getCtx(), C_OrderLine_ID, ioLine.get_TrxName());
			int JP_ContractLine_ID = orderLine.get_ValueAsInt("JP_ContractLine_ID");
				
			MContractLine contractLine = MContractLine.get(Env.getCtx(), JP_ContractLine_ID);
			MContractAcct contractAcct = MContractAcct.get(Env.getCtx(), contractLine.getParent().getJP_Contract_Acct_ID());
			
			//Check Relation of Contract Cotent
			if(contractLine.getJP_ContractContent_ID() != ioLine.getParent().get_ValueAsInt("JP_ContractContent_ID"))
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
			
			
		}//if(type == ModelValidator.TYPE_BEFORE_NEW)
		
		return null;
	}

}
