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
package jpiere.base.plugin.org.adempiere.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;

import org.compiere.model.MProductCategory;
import org.compiere.model.X_M_Product_Category_Acct;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.X_I_ProductCategoryJP;
import jpiere.base.plugin.util.JPiereValidCombinationUtil;

/**
 * 	JPIERE-0395:Import Product Category
 *
 *  @author Hideaki Hagiwara
 *
 */
public class JPiereImportProductCategory extends SvrProcess
{

	private boolean p_deleteOldImported = false;

	/**
	 *  Prepare - e.g., get Parameters.
	 */
	protected void prepare()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (name.equals("DeleteOldImported"))
				p_deleteOldImported = "Y".equals(para[i].getParameter());
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
	}	//	prepare

	/**
	 * 	Process
	 *	@return info
	 *	@throws Exception
	 */
	protected String doIt() throws Exception
	{
		StringBuilder sql = null;
		int no = 0;
		StringBuilder clientCheck = new StringBuilder(" AND AD_Client_ID=").append(getAD_Client_ID());


		//Delete Old Imported data
		if (p_deleteOldImported)
		{
			sql = new StringBuilder ("DELETE I_ProductCategoryJP ")
				  .append("WHERE I_IsImported='Y'").append (clientCheck);
			no = DB.executeUpdate(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("Delete Old Impored =" + no);
		}


		//Update AD_Org ID From JP_Org_Value
		sql = new StringBuilder ("UPDATE I_ProductCategoryJP i ")
				.append("SET AD_Org_ID=(SELECT AD_Org_ID FROM AD_org p")
				.append(" WHERE i.JP_Org_Value=p.Value AND (p.AD_Client_ID=i.AD_Client_ID or p.AD_Client_ID=0) ) ")
				.append(" WHERE i.JP_Org_Value IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(clientCheck);
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("Found Organization=" + no);

		}catch(Exception e) {

			throw new Exception(Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "JP_CouldNotUpdate")
					+ "Update AD_Org_ID From JP_Org_Value");

		}


		//Update M_Product_Category_ID From Value
		sql = new StringBuilder ("UPDATE I_ProductCategoryJP i ")
				.append("SET M_Product_Category_ID=(SELECT M_Product_Category_ID FROM M_Product_Category p")
				.append(" WHERE i.Value=p.Value AND p.AD_Client_ID=i.AD_Client_ID) ")
				.append(" WHERE i.M_Product_Category_ID IS NULL AND i.Value IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(clientCheck);
		try {

			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("Found Product Category=" + no);

		}catch(Exception e) {

			throw new Exception(Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "JP_CouldNotUpdate")
					+ "Update M_Product_Category_ID From Value");

		}


		//Update JP_ProductCategoryL1_ID From JP_ProductCategoryL1_Value
		sql = new StringBuilder ("UPDATE I_ProductCategoryJP i ")
				.append("SET JP_ProductCategoryL1_ID=(SELECT JP_ProductCategoryL1_ID FROM JP_ProductCategoryL1 p")
				.append(" WHERE i.JP_ProductCategoryL1_Value=p.Value AND p.AD_Client_ID=i.AD_Client_ID) ")
				.append(" WHERE i.JP_ProductCategoryL1_ID IS NULL AND i.JP_ProductCategoryL1_Value IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(clientCheck);
		try {

			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("Found Product Category L1=" + no);

		}catch(Exception e) {

			throw new Exception(Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "JP_CouldNotUpdate")
					+ "Update JP_ProductCategoryL1_ID From JP_ProductCategoryL1_Value");

		}


		//Update A_Asset_Group_ID From JP_Asset_Group_Name
		sql = new StringBuilder ("UPDATE I_ProductCategoryJP i ")
				.append("SET A_Asset_Group_ID=(SELECT A_Asset_Group_ID FROM A_Asset_Group p")
				.append(" WHERE i.JP_Asset_Group_Name=p.Name AND p.AD_Client_ID=i.AD_Client_ID) ")
				.append(" WHERE i.A_Asset_Group_ID IS NULL AND i.JP_Asset_Group_Name IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(clientCheck);
		try {

			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("Found Asset Group=" + no);

		}catch(Exception e) {

			throw new Exception(Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "JP_CouldNotUpdate")
					+ "Update A_Asset_Group_ID From JP_Asset_Group_Name");

		}



		//Update AD_PrintColor_ID From JP_PrintColor_Name
		sql = new StringBuilder ("UPDATE I_ProductCategoryJP i ")
				.append("SET AD_PrintColor_ID=(SELECT AD_PrintColor_ID FROM AD_PrintColor p")
				.append(" WHERE i.JP_PrintColor_Name=p.Name AND (p.AD_Client_ID=i.AD_Client_ID or p.AD_Client_ID=0) ) ")
				.append(" WHERE i.AD_PrintColor_ID IS NULL AND i.JP_PrintColor_Name IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(clientCheck);
		try {

			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("Found Print Color=" + no);

		}catch(Exception e) {

			throw new Exception(Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "JP_CouldNotUpdate")
					+ "Update AD_PrintColor_ID From JP_PrintColor_Name");

		}

		//Update AD_AcctSchema_ID From JP_AcctSchema_Name
		sql = new StringBuilder ("UPDATE I_ProductCategoryJP i ")
				.append("SET C_AcctSchema_ID=(SELECT C_AcctSchema_ID FROM C_AcctSchema p")
				.append(" WHERE i.JP_AcctSchema_Name=p.Name AND p.AD_Client_ID=i.AD_Client_ID) ")
				.append(" WHERE i.C_AcctSchema_ID IS NULL AND JP_AcctSchema_Name IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(clientCheck);
		try {

			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("Found Acct Schema=" + no);

		}catch(Exception e) {

			throw new Exception(Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "JP_CouldNotUpdate")
					+ "Update AD_AcctSchema_ID From JP_AcctSchema_Name");

		}


		commitEx();

		//
		sql = new StringBuilder ("SELECT * FROM I_ProductCategoryJP WHERE I_IsImported='N'")
					.append(clientCheck);
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try
		{
			pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
			rs = pstmt.executeQuery();
			while (rs.next())
			{
				X_I_ProductCategoryJP imp = new X_I_ProductCategoryJP (getCtx (), rs, get_TrxName());

				boolean isNew = true;
				if(imp.getM_Product_Category_ID()!=0){
					isNew =false;
				}

				if(isNew)//Create
				{
					//Check Mandatory - Value
					if(Util.isEmpty(imp.getValue()))
					{
						Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "Value")};
						imp.setI_ErrorMsg(Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs));
						imp.setI_IsImported(false);
						imp.setProcessed(false);
						imp.saveEx(get_TrxName());
						commitEx();
						continue;
					}

					//Check Mandatory - Name
					if(Util.isEmpty(imp.getName()))
					{
						Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "Name")};
						imp.setI_ErrorMsg(Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs));
						imp.setI_IsImported(false);
						imp.setProcessed(false);
						imp.saveEx(get_TrxName());
						commitEx();
						continue;
					}

					MProductCategory newProductCategory = new MProductCategory(getCtx(), 0, get_TrxName());
					newProductCategory.setAD_Org_ID(imp.getAD_Org_ID());
					newProductCategory.setValue(imp.getValue());
					newProductCategory.setName(imp.getName());

					if(!Util.isEmpty(imp.getDescription()))
						newProductCategory.setValue(imp.getDescription());
					newProductCategory.setIsDefault(imp.isDefault());
					newProductCategory.setIsSelfService(imp.isSelfService());

					if(imp.getJP_ProductCategoryL1_ID() > 0)
						newProductCategory.set_ValueNoCheck("JP_ProductCategoryL1_ID", imp.getJP_ProductCategoryL1_ID());
					newProductCategory.setMMPolicy(imp.getMMPolicy());

					if(imp.getPlannedMargin()!=null)
						newProductCategory.setPlannedMargin(imp.getPlannedMargin());

					if(imp.getA_Asset_Group_ID() > 0)
						newProductCategory.setA_Asset_Group_ID(imp.getA_Asset_Group_ID());

					if(imp.getAD_PrintColor_ID() > 0)
						newProductCategory.setAD_PrintColor_ID(imp.getAD_PrintColor_ID());

					newProductCategory.setIsActive(imp.isI_IsActiveJP());
					newProductCategory.saveEx(get_TrxName());
					commitEx();

					imp.setM_Product_Category_ID(newProductCategory.getM_Product_Category_ID());
					imp.setI_ErrorMsg(Msg.getMsg(getCtx(), "NewRecord"));
					imp.setI_IsImported(true);
					imp.setProcessed(true);

					if(!Util.isEmpty(imp.getJP_AcctSchema_Name()) && imp.getC_AcctSchema_ID() > 0)
						setProductCategoryAcct(newProductCategory, imp);

				}else{//Update

					//Check Mandatory - Value
					if(Util.isEmpty(imp.getValue()))
					{
						Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "Value")};
						imp.setI_ErrorMsg(Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs));
						imp.setI_IsImported(false);
						imp.setProcessed(false);
						imp.saveEx(get_TrxName());
						commitEx();
						continue;
					}

					MProductCategory updateProductCategory = new MProductCategory(getCtx(), imp.getM_Product_Category_ID(), get_TrxName());
					updateProductCategory.setAD_Org_ID(imp.getAD_Org_ID());
					updateProductCategory.setName(imp.getName());

					if(!Util.isEmpty(imp.getDescription()))
						updateProductCategory.setValue(imp.getDescription());
					updateProductCategory.setIsDefault(imp.isDefault());
					updateProductCategory.setIsSelfService(imp.isSelfService());

					if(imp.getJP_ProductCategoryL1_ID() > 0)
						updateProductCategory.set_ValueNoCheck("JP_ProductCategoryL1_ID", imp.getJP_ProductCategoryL1_ID());
					updateProductCategory.setMMPolicy(imp.getMMPolicy());

					if(imp.getPlannedMargin()!=null)
						updateProductCategory.setPlannedMargin(imp.getPlannedMargin());

					if(imp.getA_Asset_Group_ID() > 0)
						updateProductCategory.setA_Asset_Group_ID(imp.getA_Asset_Group_ID());

					if(imp.getAD_PrintColor_ID() > 0)
						updateProductCategory.setAD_PrintColor_ID(imp.getAD_PrintColor_ID());

					updateProductCategory.setIsActive(imp.isI_IsActiveJP());
					updateProductCategory.saveEx(get_TrxName());
					commitEx();

					if(!Util.isEmpty(imp.getJP_AcctSchema_Name()) && imp.getC_AcctSchema_ID() > 0)
						setProductCategoryAcct(updateProductCategory, imp);

					imp.setI_ErrorMsg("Update Record");
					imp.setI_IsImported(true);
					imp.setProcessed(true);

				}

				imp.saveEx(get_TrxName());
				commitEx();

			}//while (rs.next())

		}catch (Exception e){
			log.log(Level.SEVERE, sql.toString(), e);
		}finally{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

		return "";
	}	//	doIt


	private void setProductCategoryAcct(MProductCategory pc, X_I_ProductCategoryJP imp)
	{

		X_M_Product_Category_Acct acct = null;

		String WhereClause = " C_AcctSchema_ID=" +imp.getC_AcctSchema_ID() + " AND M_Product_Category_ID=" + pc.getM_Product_Category_ID() + " AND AD_Client_ID=" +Env.getAD_Client_ID(Env.getCtx());

		StringBuilder sql = new StringBuilder ("SELECT * FROM M_Product_Category_Acct WHERE " + WhereClause);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
			rs = pstmt.executeQuery();

			if (rs.next())
			{
				acct = new X_M_Product_Category_Acct (getCtx (), rs, get_TrxName());
			}

		}catch (Exception e){

			log.log(Level.SEVERE, sql.toString(), e);

		}finally{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}


		if(acct == null)
			return ;


		if(!Util.isEmpty(imp.getCostingMethod()))
				acct.setCostingMethod(imp.getCostingMethod());

		if(!Util.isEmpty(imp.getCostingLevel()))
			acct.setCostingLevel(imp.getCostingLevel());

		//P_Asset_Acct
		if(!Util.isEmpty(imp.getJP_P_Asset_Acct_Value()))
		{
			int P_Asset_Acct = JPiereValidCombinationUtil.searchCreateValidCombination (getCtx(), imp.getC_AcctSchema_ID(), imp.getJP_P_Asset_Acct_Value(), get_TrxName());
			if(P_Asset_Acct > 0)
			{
				imp.setP_Asset_Acct(P_Asset_Acct);
				acct.setP_Asset_Acct(P_Asset_Acct);
			}
		}

		//P_Expense_Acct
		if(!Util.isEmpty(imp.getJP_P_Expense_Acct_Value()))
		{
			int P_Expense_Acct = JPiereValidCombinationUtil.searchCreateValidCombination (getCtx(), imp.getC_AcctSchema_ID(), imp.getJP_P_Expense_Acct_Value(), get_TrxName());
			if(P_Expense_Acct > 0)
			{
				imp.setP_Expense_Acct(P_Expense_Acct);
				acct.setP_Expense_Acct(P_Expense_Acct);
			}
		}

		//P_CostAdjustment_Acct
		if(!Util.isEmpty(imp.getJP_CostAdjustment_Value()))
		{
			int P_CostAdjustment_Acct = JPiereValidCombinationUtil.searchCreateValidCombination (getCtx(), imp.getC_AcctSchema_ID(), imp.getJP_CostAdjustment_Value(), get_TrxName());
			if(P_CostAdjustment_Acct > 0)
			{
				imp.setP_CostAdjustment_Acct(P_CostAdjustment_Acct);
				acct.setP_CostAdjustment_Acct(P_CostAdjustment_Acct);
			}
		}

		//P_InventoryClearing_Acct
		if(!Util.isEmpty(imp.getJP_InventoryClearing_Value()))
		{
			int P_InventoryClearing_Acct = JPiereValidCombinationUtil.searchCreateValidCombination (getCtx(), imp.getC_AcctSchema_ID(), imp.getJP_InventoryClearing_Value(), get_TrxName());
			if(P_InventoryClearing_Acct > 0)
			{
				imp.setP_InventoryClearing_Acct(P_InventoryClearing_Acct);
				acct.setP_InventoryClearing_Acct(P_InventoryClearing_Acct);
			}
		}

		//P_COGS_Acct
		if(!Util.isEmpty(imp.getJP_COGS_Acct_Value()))
		{
			int P_COGS_Acct = JPiereValidCombinationUtil.searchCreateValidCombination (getCtx(), imp.getC_AcctSchema_ID(), imp.getJP_COGS_Acct_Value(), get_TrxName());
			if(P_COGS_Acct > 0)
			{
				imp.setP_COGS_Acct(P_COGS_Acct);
				acct.setP_COGS_Acct(P_COGS_Acct);
			}
		}

		//P_Revenue_Acct
		if(!Util.isEmpty(imp.getJP_P_Revenue_Acct_Value()))
		{
			int P_Revenue_Acct = JPiereValidCombinationUtil.searchCreateValidCombination (getCtx(), imp.getC_AcctSchema_ID(), imp.getJP_P_Revenue_Acct_Value(), get_TrxName());
			if(P_Revenue_Acct > 0)
			{
				imp.setP_Revenue_Acct(P_Revenue_Acct);
				acct.setP_Revenue_Acct(P_Revenue_Acct);
			}
		}

		//P_PurchasePriceVariance_Acct
		if(!Util.isEmpty(imp.getJP_PO_PriceVariance_Value()))
		{
			int P_PurchasePriceVariance_Acct = JPiereValidCombinationUtil.searchCreateValidCombination (getCtx(), imp.getC_AcctSchema_ID(), imp.getJP_PO_PriceVariance_Value(), get_TrxName());
			if(P_PurchasePriceVariance_Acct > 0)
			{
				imp.setP_PurchasePriceVariance_Acct(P_PurchasePriceVariance_Acct);
				acct.setP_PurchasePriceVariance_Acct(P_PurchasePriceVariance_Acct);
			}
		}

		//P_InvoicePriceVariance_Acct
		if(!Util.isEmpty(imp.getJP_InvoicePriceVariance_Value()))
		{
			int P_InvoicePriceVariance_Acct = JPiereValidCombinationUtil.searchCreateValidCombination (getCtx(), imp.getC_AcctSchema_ID(), imp.getJP_InvoicePriceVariance_Value(), get_TrxName());
			if(P_InvoicePriceVariance_Acct > 0)
			{
				imp.setP_InvoicePriceVariance_Acct(P_InvoicePriceVariance_Acct);
				acct.setP_InvoicePriceVariance_Acct(P_InvoicePriceVariance_Acct);
			}
		}

		//P_TradeDiscountRec_Acct
		if(!Util.isEmpty(imp.getJP_P_TradeDiscountRec_Value()))
		{
			int P_TradeDiscountRec_Acct = JPiereValidCombinationUtil.searchCreateValidCombination (getCtx(), imp.getC_AcctSchema_ID(), imp.getJP_P_TradeDiscountRec_Value(), get_TrxName());
			if(P_TradeDiscountRec_Acct > 0)
			{
				imp.setP_TradeDiscountRec_Acct(P_TradeDiscountRec_Acct);
				acct.setP_TradeDiscountRec_Acct(P_TradeDiscountRec_Acct);
			}
		}

		//P_TradeDiscountGrant_Acct
		if(!Util.isEmpty(imp.getJP_P_TradeDiscountGrant_Value()))
		{
			int P_TradeDiscountGrant_Acct = JPiereValidCombinationUtil.searchCreateValidCombination (getCtx(), imp.getC_AcctSchema_ID(), imp.getJP_P_TradeDiscountGrant_Value(), get_TrxName());
			if(P_TradeDiscountGrant_Acct > 0)
			{
				imp.setP_TradeDiscountGrant_Acct(P_TradeDiscountGrant_Acct);
				acct.setP_TradeDiscountGrant_Acct(P_TradeDiscountGrant_Acct);
			}
		}

		//P_RateVariance_Acct
		if(!Util.isEmpty(imp.getJP_RateVariance_Acct_Value()))
		{
			int P_RateVariance_Acct = JPiereValidCombinationUtil.searchCreateValidCombination (getCtx(), imp.getC_AcctSchema_ID(), imp.getJP_RateVariance_Acct_Value(), get_TrxName());
			if(P_RateVariance_Acct > 0)
			{
				imp.setP_RateVariance_Acct(P_RateVariance_Acct);
				acct.setP_RateVariance_Acct(P_RateVariance_Acct);
			}
		}

		//P_AverageCostVariance_Acct
		if(!Util.isEmpty(imp.getJP_AverageCostVariance_Value()))
		{
			int P_AverageCostVariance_Acct = JPiereValidCombinationUtil.searchCreateValidCombination (getCtx(), imp.getC_AcctSchema_ID(), imp.getJP_AverageCostVariance_Value(), get_TrxName());
			if(P_AverageCostVariance_Acct > 0)
			{
				imp.setP_AverageCostVariance_Acct(P_AverageCostVariance_Acct);
				acct.setP_AverageCostVariance_Acct(P_AverageCostVariance_Acct);
			}
		}

		//P_LandedCostClearing_Acct
		if(!Util.isEmpty(imp.getJP_LandedCostClearing_Value()))
		{
			int P_LandedCostClearing_Acct = JPiereValidCombinationUtil.searchCreateValidCombination (getCtx(), imp.getC_AcctSchema_ID(), imp.getJP_LandedCostClearing_Value(), get_TrxName());
			if(P_LandedCostClearing_Acct > 0)
			{
				imp.setP_LandedCostClearing_Acct(P_LandedCostClearing_Acct);
				acct.setP_LandedCostClearing_Acct(P_LandedCostClearing_Acct);
			}
		}

		acct.saveEx(get_TrxName());

	}


}	//	ImportPayment
