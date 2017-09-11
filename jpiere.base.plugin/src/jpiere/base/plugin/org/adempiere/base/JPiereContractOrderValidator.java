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
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Msg;

import jpiere.base.plugin.org.adempiere.model.MContract;
import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractLine;
import jpiere.base.plugin.org.adempiere.model.MContractProcPeriod;


/**
 *  JPIERE-0363: Contract Management
 *  JPiere Contract Order Validator
 *
 *  @author  Hideaki Hagiwara（h.hagiwara@oss-erp.co.jp）
 *
 */
public class JPiereContractOrderValidator implements ModelValidator {

	private static CLogger log = CLogger.getCLogger(JPiereContractOrderValidator.class);
	private int AD_Client_ID = -1;
	private int AD_Org_ID = -1;
	private int AD_Role_ID = -1;
	private int AD_User_ID = -1;


	@Override
	public void initialize(ModelValidationEngine engine, MClient client) 
	{
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();
		engine.addModelChange(MOrder.Table_Name, this);
		engine.addModelChange(MOrderLine.Table_Name, this);
		engine.addDocValidate(MOrder.Table_Name, this);
		engine.addDocValidate(MOrderLine.Table_Name, this);

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
		if(po.get_TableName().equals(MOrder.Table_Name))
		{
			return orderValidate(po, type);
			
		}else if(po.get_TableName().equals(MOrderLine.Table_Name)){
			
			return orderLineValidate(po, type);
		}
		
		return null;
	}

	@Override
	public String docValidate(PO po, int timing) 
	{
		
		//TODO 伝票の明細行作成対象の契約内容明細がある場合、その全ての明細行が含まれているかどうかのチェック。
		
		return null;
	}
	
	
	/**
	 * Order Validate
	 * 
	 * @param po
	 * @param type
	 * @return
	 */
	private String orderValidate(PO po, int type)
	{
		if( type == ModelValidator.TYPE_BEFORE_NEW 
				||( type == ModelValidator.TYPE_BEFORE_CHANGE && ( po.is_ValueChanged(MContract.COLUMNNAME_JP_Contract_ID)
																	||   po.is_ValueChanged(MContractContent.COLUMNNAME_JP_ContractContent_ID)
																	||   po.is_ValueChanged(MContractProcPeriod.COLUMNNAME_JP_ContractProcPeriod_ID) ) ) )
		{
			MOrder order = (MOrder)po;
			if(type == ModelValidator.TYPE_BEFORE_CHANGE)
			{
				if(order.getLines().length > 0)
					return Msg.getMsg(Env.getCtx(), "JP_CannotChangeContractInfoForLines");//Contract Info cannot be changed because the Document have lines
			}
			
			//Check JP_Contract_ID
			int JP_Contract_ID = order.get_ValueAsInt(MContract.COLUMNNAME_JP_Contract_ID);
			if(JP_Contract_ID > 0)
			{
				MContract contract = MContract.get(Env.getCtx(), JP_Contract_ID);
				//Period Contract
				if(contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
				{
					/**Check JP_ContractContent_ID 
					 * Mandetory Period Contract AND Spot Contract.
					 * In case of General Contract, JP_ContractContent_ID should be null;
					 */
					int JP_ContractContent_ID = order.get_ValueAsInt(MContractContent.COLUMNNAME_JP_ContractContent_ID);
					if(JP_ContractContent_ID <= 0)
					{
						Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractContent_ID")};
						return Msg.getMsg(Env.getCtx(), "JP_InCaseOfPeriodContract") + Msg.getMsg(Env.getCtx(),"JP_Mandatory", objs);
						
					}else{
						
						MContractContent content = MContractContent.get(Env.getCtx(), JP_ContractContent_ID);
						
						//Check BP
						if(content.getC_BPartner_ID() != order.getC_BPartner_ID())
						{
							return "契約内容の取引先と異なります。";//TODO メッセージ化
						}
						
						//Check Contract
						if(contract.getJP_Contract_ID() != content.getJP_Contract_ID())
						{
							//You selected different contract Document.
							return Msg.getMsg(Env.getCtx(), "JP_Diff_ContractDocument");
						}
						
						/** Check JP_ContractProcPeriod_ID
						 *  Mandetory Period Contract 
						 *  In case of Spot Contract or General Contract, JP_ContractProcPeriod_ID should be null;
						 */
						int JP_ContractProcPeriod_ID = order.get_ValueAsInt(MContractProcPeriod.COLUMNNAME_JP_ContractProcPeriod_ID);
						if(JP_ContractProcPeriod_ID <= 0)
						{
							Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractProcPeriod_ID")};
							return Msg.getMsg(Env.getCtx(), "JP_InCaseOfPeriodContract") + Msg.getMsg(Env.getCtx(),"JP_Mandatory", objs);
							
						}else{

							MContractProcPeriod period = MContractProcPeriod.get(Env.getCtx(), JP_ContractProcPeriod_ID);
							
							//Check Calender
							if(content.getJP_ContractCalender_ID() != period.getJP_ContractCalender_ID() )
							{
								return "契約内容の契約カレンダーと選択した契約処理期間の契約カレンダーが一致しません。。";//TODO メッセージ化
							}
							
							
							//TODO 受注伝票の契約処理期間が、契約期間に含まれている事の確認
						
						}
					}
				
				//Spot Contract
				}else if(contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_SpotContract)){
					
					/**Check JP_ContractContent_ID 
					 * Mandetory Period Contract AND Spot Contract.
					 * In case of General Contract, JP_ContractContent_ID should be null;
					 */
					int JP_ContractContent_ID = order.get_ValueAsInt(MContractContent.COLUMNNAME_JP_ContractContent_ID);
					if(JP_ContractContent_ID <= 0)
					{
						Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractContent_ID")};
						return Msg.getMsg(Env.getCtx(), "JP_InCaseOfSpotContract") + Msg.getMsg(Env.getCtx(),"JP_Mandatory", objs);
						
					}else{
						
						MContractContent content = MContractContent.get(Env.getCtx(), JP_ContractContent_ID);
						
						//Check BP
						if(content.getC_BPartner_ID() != order.getC_BPartner_ID())
						{
							return "契約内容の取引先と異なります。";//TODO メッセージ化
						}
						
						//Check Contract
						if(contract.getJP_Contract_ID() != content.getJP_Contract_ID())
						{
							//You selected different contract Document.
							return Msg.getMsg(Env.getCtx(), "JP_Diff_ContractDocument");
						}
						
					}
					
					/** In case of Spot Contract or General Contract, JP_ContractProcPeriod_ID should be null; */
					order.set_ValueNoCheck("JP_ContractProcPeriod_ID", null);
				
				//General Contract
				}else if(contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_GeneralContract)){
					
					//Check BP
					if(contract.getC_BPartner_ID() != order.getC_BPartner_ID())
					{
						return "契約書の取引先と異なります。";//TODO メッセージ化
					}
					
					/** In case of General Contract, JP_ContractContent_ID AND JP_ContractProcPeriod_ID should be null;*/
					order.set_ValueNoCheck("JP_ContractContent_ID", null);
					order.set_ValueNoCheck("JP_ContractProcPeriod_ID", null);						
				}
				
			}else{//JP_Contract_ID <= 0
			
				order.set_ValueNoCheck("JP_Contract_ID", null);
				order.set_ValueNoCheck("JP_ContractContent_ID", null);
				order.set_ValueNoCheck("JP_ContractProcPeriod_ID", null);
				
			}//if(JP_Contract_ID > 0)
		
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
	private String orderLineValidate(PO po, int type)
	{
		if(type == ModelValidator.TYPE_BEFORE_NEW
				||( type == ModelValidator.TYPE_BEFORE_CHANGE && ( po.is_ValueChanged(MContractLine.COLUMNNAME_JP_ContractLine_ID)
						||   po.is_ValueChanged(MContractProcPeriod.COLUMNNAME_JP_ContractProcPeriod_ID) ) ))
		{
			MOrderLine oLine = (MOrderLine)po;
			int JP_ContractLine_ID = oLine.get_ValueAsInt(MContractLine.COLUMNNAME_JP_ContractLine_ID);
			if(JP_ContractLine_ID > 0)
			{
				MContractLine contractLine = MContractLine.get(Env.getCtx(), JP_ContractLine_ID);
				MContract contract = contractLine.getParent().getParent();
				
				//Check Contract Cotent
				if(contractLine.getJP_ContractContent_ID() == oLine.get_ValueAsInt("JP_ContractContent_ID"))
				{
					//You can select Contract Content Line that is belong to Contract content
					return Msg.getMsg(Env.getCtx(), "Invalid") + Msg.getElement(Env.getCtx(), "JP_ContractLine_ID") + Msg.getMsg(Env.getCtx(), "JP_Diff_ContractContentLine");
				}
				
				
				//Check Period Contract
				if(contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
				{
					int JP_ContractProcPeriod_ID = oLine.get_ValueAsInt(MContractProcPeriod.COLUMNNAME_JP_ContractProcPeriod_ID);
					if(JP_ContractProcPeriod_ID <= 0)
					{
						oLine.set_ValueOfColumn("JP_ContractProcPeriod_ID", oLine.getParent().get_ValueAsInt(MContractProcPeriod.COLUMNNAME_JP_ContractProcPeriod_ID));
					}
					
					//TODO 品目マスタチェック -- 契約内容明細の品目マスタと同じかどうかのチェック(品目マスタがnullではない場合)

					//TODO 料金タイプマスタチェック -- 契約内容明細の料金タイプと同じかどうかのチェック(品目マスタがnullの場合)
					
					//TODO 契約内容明細×契約処理期間が2重登録されていない事のチェック.でもクローズやボイド、キャンセルの場合はOKでも・・・
					
					//TODO 受注伝票明細の契約処理期間が、契約期間に含まれている事の確認
					
				//Check Spot Contract
				}else if(contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_SpotContract)){
					
					oLine.set_ValueNoCheck("JP_ContractProcPeriod_ID", null);
				
				//Check General Contract
				}else if(contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_GeneralContract)){
					
					oLine.set_ValueNoCheck("JP_ContractLine_ID", null);
					oLine.set_ValueNoCheck("JP_ContractProcPeriod_ID", null);
				}
				
			}else{
				
				oLine.set_ValueNoCheck("JP_ContractProcPeriod_ID", null);
			}
		}
		return null;
	}
}
