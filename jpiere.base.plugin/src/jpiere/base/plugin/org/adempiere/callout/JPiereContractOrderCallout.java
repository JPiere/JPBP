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
package jpiere.base.plugin.org.adempiere.callout;

import java.sql.Timestamp;
import java.util.Properties;

import org.adempiere.base.IColumnCallout;
import org.adempiere.util.Callback;
import org.adempiere.webui.window.FDialog;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.util.DB;
import org.compiere.util.Env;

import jpiere.base.plugin.org.adempiere.model.MContract;
import jpiere.base.plugin.org.adempiere.model.MContractCalender;
import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractLine;
import jpiere.base.plugin.org.adempiere.model.MContractProcPeriod;

/**
 *
 *  JPiere Contract Order CallOut
 *
 *  JPIERE-0363:JPBP
 *
 * @author Hideaki Hagiwara
 *
 */
public class JPiereContractOrderCallout implements IColumnCallout {

	
	private int	JP_ContractContent_ID = 0;
	private int JP_ContractLine_ID = 0;
	
	@Override
	public String start(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{
		if(mField.getColumnName().equals("JP_Contract_ID"))
		{
			if(value != null)
			{			
				int JP_Contract_ID =  ((Integer)value).intValue();
				MContract contract = MContract.get(ctx, JP_Contract_ID);
				Timestamp dateAcct = (Timestamp)mTab.getValue("DateAcct");
				
				String sql = "SELECT JP_ContractContent_ID "
						+ "FROM JP_ContractContent "
						+ "WHERE JP_Contract_ID=? "						//	1
						+ " AND AD_Org_ID = ?"
						+ " AND JP_BaseDocDocType_ID = ?"
						+ " AND DateAcct <= ? "
						+ " ORDER BY DateAcct DESC";
	
				JP_ContractContent_ID = DB.getSQLValueEx(null, sql, JP_Contract_ID, mTab.getValue("AD_Org_ID"), mTab.getValue("C_DocTypeTarget_ID"), dateAcct);
				
				if(JP_ContractContent_ID > 0)
				{
					mTab.setValue("JP_ContractContent_ID", JP_ContractContent_ID);
					if(contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
					{
						MContractContent content = MContractContent.get(ctx, JP_ContractContent_ID);
						MContractCalender calender = MContractCalender.get(ctx, content.getJP_ContractCalender_ID());
						MContractProcPeriod period = calender.getContractProcessPeriod(ctx, dateAcct);
						mTab.setValue("JP_ContractProcPeriod_ID", period.getJP_ContractProcPeriod_ID());
					}
					
					FDialog.ask(WindowNo, null, "JP_UpdateDocumentInContract", new Callback<Boolean>() 
					{
	
						@Override
						public void onCallback(Boolean result)
						{
							if(result)
							{
								updateByContractContent(ctx, WindowNo, mTab, mField, value, oldValue) ;
							}else{
								;//Noting to do
							}
				        }
	
					});//FDialog.
				}
			
			}else{
				mTab.setValue("JP_ContractContent_ID", null);
				mTab.setValue("JP_ContractProcPeriod_ID", null);
			}
			
		}else if(mField.getColumnName().equals("JP_ContractContent_ID")){
			
			if( value != null)
			{
				JP_ContractContent_ID =  ((Integer)value).intValue();
				MContractContent content = MContractContent.get(ctx, JP_ContractContent_ID);
				
				if(content.getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
				{
					Timestamp dateAcct = (Timestamp)mTab.getValue("DateAcct");
					MContractCalender calender = MContractCalender.get(ctx, content.getJP_ContractCalender_ID());
					MContractProcPeriod period = calender.getContractProcessPeriod(ctx, dateAcct);
					mTab.setValue("JP_ContractProcPeriod_ID", period.getJP_ContractProcPeriod_ID());
				}
				
				FDialog.ask(WindowNo, null, "JP_UpdateDocumentInContract", new Callback<Boolean>() //Would you like to update document in Contract content?
				{

					@Override
					public void onCallback(Boolean result)
					{
						if(result)
						{
							updateByContractContent(ctx, WindowNo, mTab, mField, value, oldValue) ;
						}else{
							;//Noting to do
						}
			        }

				});//FDialog.
				
			}else{
				mTab.setValue("JP_ContractProcPeriod_ID", null);
			}
			
		}else if(mField.getColumnName().equals("JP_ContractLine_ID")){
			if( value != null)
			{
				JP_ContractLine_ID =  ((Integer)value).intValue();
				int JP_ContractProcPeriod_ID = Env.getContextAsInt(ctx, WindowNo, "JP_ContractProcPeriod_ID");
				
				if(JP_ContractProcPeriod_ID > 0)
					mTab.setValue("JP_ContractProcPeriod_ID", JP_ContractProcPeriod_ID);
				
				FDialog.ask(WindowNo, null, "JP_UpdateDocumentInContract", new Callback<Boolean>() //Would you like to update document in Contract content?
				{

					@Override
					public void onCallback(Boolean result)
					{
						if(result)
						{
							updateByContractLine(ctx, WindowNo, mTab, mField, value, oldValue) ;
						}else{
							;//Noting to do
						}
			        }

				});//FDialog.
				
			}else{
				mTab.setValue("JP_ContractProcPeriod_ID", null);
			}
		}

		return "";
	}
	
	
	private void updateByContractContent(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{
		if(JP_ContractContent_ID == 0)
				return ;
		
		MContractContent content = MContractContent.get(ctx, JP_ContractContent_ID);
		
		if(content.getC_Opportunity_ID() > 0)
			mTab.setValue("C_Opportunity_ID", content.getC_Opportunity_ID());
		
		mTab.setValue("POReference", content.getPOReference());
		
		if(content.getSalesRep_ID() > 0)
			mTab.setValue("SalesRep_ID", content.getSalesRep_ID());
		
		mTab.setValue("IsDropShip", content.isDropShip());
		if(content.isDropShip())
		{
			if(content.getDropShip_BPartner_ID() > 0)
				mTab.setValue("DropShip_BPartner_ID", content.getDropShip_BPartner_ID());
			if(content.getDropShip_Location_ID() > 0)
				mTab.setValue("DropShip_Location_ID", content.getDropShip_Location_ID());
			if(content.getDropShip_User_ID() > 0)
				mTab.setValue("DropShip_User_ID", content.getDropShip_User_ID());
		}else{
			mTab.setValue("DropShip_BPartner_ID", null);
			mTab.setValue("DropShip_Location_ID", null);
			mTab.setValue("DropShip_User_ID", null);
		}
		
		mTab.setValue("DeliveryRule", content.getDeliveryRule());
		mTab.setValue("PriorityRule", content.getPriorityRule());
		mTab.setValue("DeliveryViaRule", content.getDeliveryViaRule());
		
		if(content.getM_Shipper_ID() > 0)
			mTab.setValue("M_Shipper_ID", content.getM_Shipper_ID());
		
		if(content.getM_FreightCategory_ID() > 0)
			mTab.setValue("M_FreightCategory_ID", content.getM_FreightCategory_ID());
		
		mTab.setValue("FreightCostRule", content.getFreightCostRule());
		mTab.setValue("FreightAmt", content.getFreightAmt());
		
		if(content.getBill_BPartner_ID() > 0)
			mTab.setValue("Bill_BPartner_ID", content.getBill_BPartner_ID());
		
		if(content.getBill_Location_ID() > 0)
			mTab.setValue("Bill_Location_ID", content.getBill_Location_ID());
		
		if(content.getBill_User_ID() > 0)
			mTab.setValue("Bill_User_ID", content.getBill_User_ID());
		
		mTab.setValue("InvoiceRule", content.getInvoiceRule());
		mTab.setValue("IsDiscountPrinted", content.isDiscountPrinted());
		
		if(content.getM_PriceList_ID() > 0)
			mTab.setValue("M_PriceList_ID", content.getM_PriceList_ID());
		
		if(content.getC_Currency_ID() > 0)
			mTab.setValue("C_Currency_ID", content.getC_Currency_ID());
		
		if(content.getC_ConversionType_ID() > 0)
			mTab.setValue("C_ConversionType_ID", content.getC_ConversionType_ID());
		
		mTab.setValue("IsTaxIncluded", content.isTaxIncluded());
		mTab.setValue("PaymentRule", content.getPaymentRule());
		
		if(content.getC_PaymentTerm_ID() > 0)
			mTab.setValue("C_PaymentTerm_ID", content.getC_PaymentTerm_ID());
		
		//Reference info
		if(content.getC_Project_ID() > 0)
			mTab.setValue("C_Project_ID", content.getC_Project_ID());
		
		
		if(content.getC_Campaign_ID() > 0)
			mTab.setValue("C_Campaign_ID", content.getC_Campaign_ID());
		
		if(content.getC_Activity_ID() > 0)
			mTab.setValue("C_Activity_ID", content.getC_Activity_ID());
		
		if(content.getUser1_ID() > 0)
			mTab.setValue("User1_ID", content.getUser1_ID());
		
		if(content.getUser2_ID() > 0)
			mTab.setValue("User2_ID", content.getUser2_ID());
	}
	
	
	private void updateByContractLine(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue)
	{
		if(JP_ContractLine_ID == 0)
			return ;
	
		MContractLine contractLine = MContractLine.get(ctx, JP_ContractLine_ID);
		mTab.setValue("QtyEntered", contractLine.getQtyEntered());
		mTab.setValue("C_UOM_ID", contractLine.getC_UOM_ID());
		mTab.setValue("QtyOrdered", contractLine.getQtyOrdered());
		mTab.setValue("PriceEntered", contractLine.getPriceEntered());
		mTab.setValue("PriceActual", contractLine.getPriceActual());
		mTab.setValue("PriceLimit", contractLine.getPriceLimit());
		mTab.setValue("C_Tax_ID", contractLine.getC_Tax_ID());
		mTab.setValue("Discount", contractLine.getDiscount());
		mTab.setValue("PriceList", contractLine.getPriceList());
		mTab.setValue("LineNetAmt", contractLine.getLineNetAmt());
		mTab.setValue("IsDescription", contractLine.isDescription());
		
		if(contractLine.getJP_LocatorFrom_ID() > 0)
		{
			mTab.setValue("JP_LocatorFrom_ID", contractLine.getJP_LocatorFrom_ID());
			mTab.setValue("JP_LocatorTo_ID", contractLine.getJP_LocatorTo_ID());
		}
		
		if(contractLine.getJP_ASI_From_ID() > 0)
		{
			mTab.setValue("JP_ASI_From_ID", contractLine.getJP_ASI_From_ID());
			mTab.setValue("JP_ASI_To_ID", contractLine.getJP_ASI_To_ID());
		}
		
		if(contractLine.getJP_Locator_ID() > 0)
			mTab.setValue("JP_Locator_ID", contractLine.getJP_Locator_ID());
		
		if(contractLine.getS_ResourceAssignment_ID() > 0)
			mTab.setValue("S_ResourceAssignment_ID", contractLine.getS_ResourceAssignment_ID());
		
		if(contractLine.getM_AttributeSetInstance_ID() > 0)
			mTab.setValue("M_AttributeSetInstance_ID", contractLine.getM_AttributeSetInstance_ID());
		
		//Reference Info
		if(contractLine.getC_Project_ID() > 0)
			mTab.setValue("C_Project_ID", contractLine.getC_Project_ID());
		
		if(contractLine.getC_ProjectPhase_ID() > 0)
			mTab.setValue("C_ProjectPhase_ID", contractLine.getC_ProjectPhase_ID());
		
		if(contractLine.getC_ProjectTask_ID() > 0)
			mTab.setValue("C_ProjectTask_ID", contractLine.getC_ProjectTask_ID());
		
		if(contractLine.getC_Campaign_ID() > 0)
			mTab.setValue("C_Campaign_ID", contractLine.getC_Campaign_ID());
		
		if(contractLine.getC_Activity_ID() > 0)
			mTab.setValue("C_Activity_ID", contractLine.getC_Activity_ID());
		
		if(contractLine.getUser1_ID() > 0)
			mTab.setValue("User1_ID", contractLine.getUser1_ID());
		
		if(contractLine.getUser2_ID() > 0)
			mTab.setValue("User2_ID", contractLine.getUser2_ID());
		
	}
}
