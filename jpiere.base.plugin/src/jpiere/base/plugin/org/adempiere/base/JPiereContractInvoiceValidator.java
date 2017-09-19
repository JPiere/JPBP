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
import org.compiere.model.I_C_InvoiceLine;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MClient;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
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
	 * Invoice Header Validate
	 * 
	 * @param po
	 * @param type
	 * @return
	 */
	private String invoiceValidate(PO po, int type)
	{
		
		//Check Derivative Doc
		if(po.get_ValueAsInt("C_Order_ID") > 0 )
		{
			String msg = derivativeDocHeaderCommonCheck(po, type);
			if(!Util.isEmpty(msg))
				return msg;
		}
			
		
		//Check Base Doc
		if( type == ModelValidator.TYPE_BEFORE_NEW 
				||( type == ModelValidator.TYPE_BEFORE_CHANGE && ( po.is_ValueChanged(MContract.COLUMNNAME_JP_Contract_ID)
																	||   po.is_ValueChanged(MContractContent.COLUMNNAME_JP_ContractContent_ID)
																	||   po.is_ValueChanged(MContractProcPeriod.COLUMNNAME_JP_ContractProcPeriod_ID) ) ) )
		{
			
			MInvoice invoice = (MInvoice)po;
			
			//Check Contract Info
			int JP_Contract_ID = invoice.get_ValueAsInt(MContract.COLUMNNAME_JP_Contract_ID);
			if(JP_Contract_ID <= 0)
			{
				invoice.set_ValueNoCheck("JP_Contract_ID", null);
				invoice.set_ValueNoCheck("JP_ContractContent_ID", null);
				invoice.set_ValueNoCheck("JP_ContractProcPeriod_ID", null);
				return null;
			}
			
			//Check to Change Contract Info
			MContract contract = MContract.get(Env.getCtx(), JP_Contract_ID);
			if(type == ModelValidator.TYPE_BEFORE_CHANGE && contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
			{	
				MInvoiceLine[] contractInvoiceLines = getInvoiceLines(invoice, " JP_ContractLine_ID IS NOT NULL ");
				if(contractInvoiceLines.length > 0)
				{
					//Contract Info can not be changed because the document contains contract Info lines.
					String msg = Msg.getMsg(Env.getCtx(), "JP_CannotChangeContractInfoForLines");
					return msg;
				}
			}
			
			//Check BP
			if(contract.getC_BPartner_ID() != invoice.getC_BPartner_ID())
			{
				//Different business partner between Contract Content and Document.
				return Msg.getMsg(Env.getCtx(), "JP_DifferentBusinessPartner_ContractContent");
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

						MContractProcPeriod period = MContractProcPeriod.get(Env.getCtx(), JP_ContractProcPeriod_ID);
						
						//Check Contract Calender
						if(content.getJP_ContractCalender_ID() != period.getJP_ContractCalender_ID() )
						{
							//Contract Calender that belong to selected contract period does not accord with Contract Calender of Contract content.
							return Msg.getMsg(Env.getCtx(), "JP_DifferentContractCalender");
						}
						
						//Check Contract Period
						if(content.getJP_ContractProcDate_From().compareTo(period.getStartDate()) > 0 
								|| (content.getJP_ContractProcDate_To() != null && content.getJP_ContractProcDate_To().compareTo(period.getEndDate()) < 0) )
						{
							//Outside the Contract Process Period.
							return Msg.getMsg(Env.getCtx(), "JP_OutsideContractProcessPeriod") + " " + Msg.getMsg(Env.getCtx(), "Invalid") + Msg.getElement(Env.getCtx(), "JP_ContractProcPeriod_ID");
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
					
				}
				
				/** In case of Spot Contract or General Contract, JP_ContractProcPeriod_ID should be null; */
				invoice.set_ValueNoCheck("JP_ContractProcPeriod_ID", null);
			
			//Check General Contract
			}else if(contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_GeneralContract)){
				
				/** In case of General Contract, JP_ContractContent_ID AND JP_ContractProcPeriod_ID should be null;*/
				invoice.set_ValueNoCheck("JP_ContractContent_ID", null);
				invoice.set_ValueNoCheck("JP_ContractProcPeriod_ID", null);						
			}
			
		}//Check Base Doc
		
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

	private MInvoiceLine[] getInvoiceLines(MInvoice invoice, String whereClause)
	{
		String whereClauseFinal = "C_Invoice_ID=? ";
		if (whereClause != null)
			whereClauseFinal += whereClause;
		List<MInvoiceLine> list = new Query(Env.getCtx(), I_C_InvoiceLine.Table_Name, whereClauseFinal, invoice.get_TrxName())
										.setParameters(invoice.getC_Invoice_ID())
										.setOrderBy(I_C_InvoiceLine.COLUMNNAME_Line)
										.list();
		return list.toArray(new MInvoiceLine[list.size()]);
	}
	
}
