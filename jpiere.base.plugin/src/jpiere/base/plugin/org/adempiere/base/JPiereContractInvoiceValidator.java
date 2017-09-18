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

import org.compiere.acct.Fact;
import org.compiere.acct.FactLine;
import org.compiere.model.FactsValidator;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MClient;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
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
import jpiere.base.plugin.org.adempiere.model.MContractProcPeriod;



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
		engine.addDocValidate(MInvoiceLine.Table_Name, this);
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
		
		
		return null;
	}
	
	
	/**
	 * Order Validate
	 * 
	 * @param po
	 * @param type
	 * @return
	 */
	private String invoiceValidate(PO po, int type)
	{
		
		//Check Derivative Contract
		if(po.get_ValueAsInt("C_Order_ID") > 0 )
		{
			String msg = derivativeDocHeaderCommonCheck(po, type);
			if(!Util.isEmpty(msg))
				return msg;
		}
			
		
		
		if( type == ModelValidator.TYPE_BEFORE_NEW 
				||( type == ModelValidator.TYPE_BEFORE_CHANGE && ( po.is_ValueChanged(MContract.COLUMNNAME_JP_Contract_ID)
																	||   po.is_ValueChanged(MContractContent.COLUMNNAME_JP_ContractContent_ID)
																	||   po.is_ValueChanged(MContractProcPeriod.COLUMNNAME_JP_ContractProcPeriod_ID) ) ) )
		{
			
			String returnValue = checkHeaderContractInfoUpdate(po, type);
			
			if(!Util.isEmpty(returnValue))
				return returnValue;
			
			
			//TODO:期間契約で、基点となる伝票が受注伝票もしくは発注伝票の場合は、受注伝票もしくは発注伝票の情報は必須。（返品の事も考えてね！！）
			int JP_ContractContent_ID = po.get_ValueAsInt("JP_ContractContent_ID");
			if(JP_ContractContent_ID > 0)
			{
				MContractContent content = MContractContent.get(Env.getCtx(), JP_ContractContent_ID);
				if(content.getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
				{
					if(content.getDocBaseType().equals(MContractContent.DOCBASETYPE_SalesOrder)
							|| content.getDocBaseType().equals(MContractContent.DOCBASETYPE_PurchaseOrder))
					{
						//受注伝票もしくは発注伝票の情報は必須・・・。
						
						
					}
				}
			}
			
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
	private String invoiceLineValidate(PO po, int type)
	{
		//Check Derivative Contract
		if(po.get_ValueAsInt("C_OrderLine_ID") > 0 )
		{
			String msg = derivativeDocLineCommonCheck(po, type);
			if(!Util.isEmpty(msg))
				return msg;
		}
		
		
		//TODO:異なる契約内容の受注伝票明細が混じらないようにチェックする。
		
		return null;
	}

	@Override
	public String factsValidate(MAcctSchema schema, List<Fact> facts, PO po) 
	{
		if(po.get_TableName().equals(MInvoice.Table_Name))
		{		
			int JP_ContractContent_ID = po.get_ValueAsInt("JP_ContractContent_ID");
			if(JP_ContractContent_ID > 0)
			{
				MContractContent content = MContractContent.get(Env.getCtx(), JP_ContractContent_ID);
				MContractAcct contractAcct = MContractAcct.get(Env.getCtx(), content.getJP_Contract_Acct_ID());
				MInvoice invoice = (MInvoice)po;
				
				//Set Order Info
				if(contractAcct.isOrderInfoMandatoryJP())
				{
					for(Fact fact : facts)
					{
						FactLine[]  factLine = fact.getLines();
						for(int i = 0; i < factLine.length; i++)
						{
							if(invoice.isSOTrx())
							{
								factLine[i].set_ValueNoCheck("JP_SalesOrder_ID", invoice.getC_Order_ID());
							}else{
								factLine[i].set_ValueNoCheck("JP_PurchaseOrder_ID", invoice.getC_Order_ID());
								//Because Order is Mandetory, Relation between order doc and Invoice doc is  1: N , not permitted N : 1.
								factLine[i].set_ValueNoCheck("JP_SalesOrder_ID", invoice.getC_Order().getLink_Order_ID());
							}
						}//for
						
					}//for
					
				}//if(contractAcct.isOrderInfoMandatoryJP())
				
			}//if(JP_ContractContent_ID > 0)
		
		}//if(po.get_TableName().equals(MInvoice.Table_Name))
		
		return null;
	}

	@Override
	protected String checkHeaderContractInfoUpdate(PO po, int type) 
	{
		//Prohibit Contract info update
		if(type == ModelValidator.TYPE_BEFORE_CHANGE)
		{
			MInvoice invoice = (MInvoice)po;
			if(invoice.getLines().length > 0)
				return Msg.getMsg(Env.getCtx(), "JP_CannotChangeContractInfoForLines");//Contract Info cannot be changed because the Document have lines
		}
		
		return null;
	}
}
