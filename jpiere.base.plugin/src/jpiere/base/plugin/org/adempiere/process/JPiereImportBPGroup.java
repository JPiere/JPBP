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

import org.compiere.model.MBPGroup;
import org.compiere.model.X_C_BP_Group_Acct;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.X_I_BP_GroupJP;
import jpiere.base.plugin.util.JPiereValidCombinationUtil;

/**
 * 	JPIERE-0396:Import Business Partner Group
 *
 *  @author Hideaki Hagiwara
 *
 */
public class JPiereImportBPGroup extends SvrProcess
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
			sql = new StringBuilder ("DELETE I_BP_GroupJP ")
				  .append("WHERE I_IsImported='Y'").append (clientCheck);
			no = DB.executeUpdate(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("Delete Old Impored =" + no);
		}


		//Update AD_Org ID From JP_Org_Value
		sql = new StringBuilder ("UPDATE I_BP_GroupJP i ")
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


		//Update C_BP_Group_ID From Value
		sql = new StringBuilder ("UPDATE I_BP_GroupJP i ")
				.append("SET C_BP_Group_ID=(SELECT C_BP_Group_ID FROM C_BP_Group p")
				.append(" WHERE i.Value=p.Value AND p.AD_Client_ID=i.AD_Client_ID) ")
				.append(" WHERE i.C_BP_Group_ID IS NULL AND i.Value IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(clientCheck);
		try {

			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("Found Business Partner Group=" + no);

		}catch(Exception e) {

			throw new Exception(Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "JP_CouldNotUpdate")
					+ "Update C_BP_Group_ID From Value");

		}

		//Update AD_PrintColor_ID From JP_PrintColor_Name
		sql = new StringBuilder ("UPDATE I_BP_GroupJP i ")
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

		//Update M_PriceList_ID From JP_PriceList_Name
		sql = new StringBuilder ("UPDATE I_BP_GroupJP i ")
				.append("SET M_PriceList_ID=(SELECT M_PriceList_ID FROM M_PriceList p")
				.append(" WHERE i.JP_PriceList_Name=p.Name AND p.AD_Client_ID=i.AD_Client_ID ) ")
				.append(" WHERE i.M_PriceList_ID IS NULL AND i.JP_PriceList_Name IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(clientCheck);
		try {

			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("Found PriceList=" + no);

		}catch(Exception e) {

			throw new Exception(Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "JP_CouldNotUpdate")
					+ "Update M_PriceList_ID From JP_PriceList_Name");
		}

		//Update PO_PriceList_ID From JP_PO_PriceList_Name
		sql = new StringBuilder ("UPDATE I_BP_GroupJP i ")
				.append("SET PO_PriceList_ID=(SELECT M_PriceList_ID FROM M_PriceList p")
				.append(" WHERE i.JP_PO_PriceList_Name=p.Name AND p.AD_Client_ID=i.AD_Client_ID ) ")
				.append(" WHERE i.PO_PriceList_ID IS NULL AND i.JP_PO_PriceList_Name IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(clientCheck);
		try {

			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("Found PO PriceList=" + no);

		}catch(Exception e) {

			throw new Exception(Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "JP_CouldNotUpdate")
					+ "Update PO_PriceList_ID From JP_PO_PriceList_Name");
		}


		//Update M_DiscountSchema_ID From JP_DiscountSchema_Name
		sql = new StringBuilder ("UPDATE I_BP_GroupJP i ")
				.append("SET M_DiscountSchema_ID=(SELECT M_DiscountSchema_ID FROM M_DiscountSchema p")
				.append(" WHERE i.JP_DiscountSchema_Name=p.Name AND p.AD_Client_ID=i.AD_Client_ID ) ")
				.append(" WHERE i.M_DiscountSchema_ID IS NULL AND i.JP_DiscountSchema_Name IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(clientCheck);
		try {

			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("Found Discount Schema=" + no);

		}catch(Exception e) {

			throw new Exception(Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "JP_CouldNotUpdate")
					+ "Update M_DiscountSchema_ID From JP_DiscountSchema_Name");
		}


		//Update PO_DiscountSchema_ID From JP_PO_DiscountSchema_Name
		sql = new StringBuilder ("UPDATE I_BP_GroupJP i ")
				.append("SET PO_DiscountSchema_ID=(SELECT M_DiscountSchema_ID FROM M_DiscountSchema p")
				.append(" WHERE i.JP_PO_DiscountSchema_Name=p.Name AND p.AD_Client_ID=i.AD_Client_ID ) ")
				.append(" WHERE i.PO_DiscountSchema_ID IS NULL AND i.JP_PO_DiscountSchema_Name IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(clientCheck);
		try {

			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("Found PO Discount Schema=" + no);

		}catch(Exception e) {

			throw new Exception(Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "JP_CouldNotUpdate")
					+ "Update PO_DiscountSchema_ID From JP_PO_DiscountSchema_Name");
		}

		//Update C_Dunning_ID From JP_Dunning_Name
		sql = new StringBuilder ("UPDATE I_BP_GroupJP i ")
				.append("SET C_Dunning_ID=(SELECT C_Dunning_ID FROM C_Dunning p")
				.append(" WHERE i.JP_Dunning_Name=p.Name AND p.AD_Client_ID=i.AD_Client_ID ) ")
				.append(" WHERE i.C_Dunning_ID IS NULL AND i.JP_Dunning_Name IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(clientCheck);
		try {

			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("Found PO Discount Schema=" + no);

		}catch(Exception e) {

			throw new Exception(Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "JP_CouldNotUpdate")
					+ "Update C_Dunning_ID From JP_Dunning_Name");
		}


		//Update AD_AcctSchema_ID From JP_AcctSchema_Name
		sql = new StringBuilder ("UPDATE I_BP_GroupJP i ")
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
		sql = new StringBuilder ("SELECT * FROM I_BP_GroupJP WHERE I_IsImported='N'")
					.append(clientCheck);
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try
		{
			pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
			rs = pstmt.executeQuery();
			while (rs.next())
			{
				X_I_BP_GroupJP imp = new X_I_BP_GroupJP (getCtx (), rs, get_TrxName());

				boolean isNew = true;
				if(imp.getC_BP_Group_ID()!=0){
					isNew =false;
				}

				if(isNew)//Create
				{
					MBPGroup newBPGroup = new MBPGroup(getCtx(), 0, get_TrxName());
					newBPGroup.setAD_Org_ID(imp.getAD_Org_ID());
					newBPGroup.setValue(imp.getValue());
					newBPGroup.setName(imp.getName());

					if(!Util.isEmpty(imp.getDescription()))
						newBPGroup.setDescription(imp.getDescription());

					newBPGroup.setIsDefault(imp.isDefault());
					newBPGroup.setIsConfidentialInfo(imp.isConfidentialInfo());

					if(imp.getAD_PrintColor_ID() > 0)
						newBPGroup.setAD_PrintColor_ID(imp.getAD_PrintColor_ID());

					if(imp.getM_PriceList_ID() > 0)
						newBPGroup.setM_PriceList_ID(imp.getM_PriceList_ID());

					if(imp.getPO_PriceList_ID() > 0)
						newBPGroup.setPO_PriceList_ID(imp.getPO_PriceList_ID());

					if(imp.getM_DiscountSchema_ID() > 0)
						newBPGroup.setM_DiscountSchema_ID(imp.getM_DiscountSchema_ID());

					if(imp.getPO_DiscountSchema_ID() > 0)
						newBPGroup.setPO_DiscountSchema_ID(imp.getPO_DiscountSchema_ID());

					if(imp.getCreditWatchPercent() != null)
						newBPGroup.setCreditWatchPercent(imp.getCreditWatchPercent());

					if(imp.getPriceMatchTolerance() != null)
						newBPGroup.setPriceMatchTolerance(imp.getPriceMatchTolerance());

					if(imp.getC_Dunning_ID() > 0)
						newBPGroup.setC_Dunning_ID(imp.getC_Dunning_ID());

					newBPGroup.saveEx(get_TrxName());

					imp.setC_BP_Group_ID(newBPGroup.getC_BP_Group_ID());
					imp.setI_ErrorMsg("New Record");
					imp.setI_IsImported(true);
					imp.setProcessed(true);

					if(!Util.isEmpty(imp.getJP_AcctSchema_Name()) && imp.getC_AcctSchema_ID() > 0)
						setBPGroupAcct(newBPGroup, imp);

				}else{//Update

					MBPGroup updateBPGroup = new MBPGroup(getCtx(), imp.getC_BP_Group_ID(), get_TrxName());
					updateBPGroup.setAD_Org_ID(imp.getAD_Org_ID());
					updateBPGroup.setName(imp.getName());

					if(!Util.isEmpty(imp.getDescription()))
						updateBPGroup.setValue(imp.getDescription());
					updateBPGroup.setIsDefault(imp.isDefault());
					updateBPGroup.setIsConfidentialInfo(imp.isConfidentialInfo());

					if(imp.getAD_PrintColor_ID() > 0)
						updateBPGroup.setAD_PrintColor_ID(imp.getAD_PrintColor_ID());

					if(imp.getM_PriceList_ID() > 0)
						updateBPGroup.setM_PriceList_ID(imp.getM_PriceList_ID());

					if(imp.getPO_PriceList_ID() > 0)
						updateBPGroup.setPO_PriceList_ID(imp.getPO_PriceList_ID());

					if(imp.getM_DiscountSchema_ID() > 0)
						updateBPGroup.setM_DiscountSchema_ID(imp.getM_DiscountSchema_ID());

					if(imp.getPO_DiscountSchema_ID() > 0)
						updateBPGroup.setPO_DiscountSchema_ID(imp.getPO_DiscountSchema_ID());

					if(imp.getCreditWatchPercent() != null)
						updateBPGroup.setCreditWatchPercent(imp.getCreditWatchPercent());

					if(imp.getPriceMatchTolerance() != null)
						updateBPGroup.setPriceMatchTolerance(imp.getPriceMatchTolerance());

					if(imp.getC_Dunning_ID() > 0)
						updateBPGroup.setC_Dunning_ID(imp.getC_Dunning_ID());

					updateBPGroup.saveEx(get_TrxName());

					if(!Util.isEmpty(imp.getJP_AcctSchema_Name()) && imp.getC_AcctSchema_ID() > 0)
						setBPGroupAcct(updateBPGroup, imp);

					imp.setI_ErrorMsg("Update Record");
					imp.setI_IsImported(true);
					imp.setProcessed(true);

				}

				imp.saveEx(get_TrxName());

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


	private void setBPGroupAcct(MBPGroup pc, X_I_BP_GroupJP imp)
	{

		X_C_BP_Group_Acct acct = null;

		String WhereClause = " C_AcctSchema_ID=" +imp.getC_AcctSchema_ID() + " AND C_BP_Group_ID=" + pc.getC_BP_Group_ID() + " AND AD_Client_ID=" +Env.getAD_Client_ID(Env.getCtx());

		StringBuilder sql = new StringBuilder ("SELECT * FROM C_BP_Group_Acct WHERE " + WhereClause);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
			rs = pstmt.executeQuery();

			if (rs.next())
			{
				acct = new X_C_BP_Group_Acct (getCtx (), rs, get_TrxName());
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


		//NotInvoicedReceipts_Acct
		if(!Util.isEmpty(imp.getJP_NotInvoicedReceipts_Value()))
		{
			int NotInvoicedReceipts_Acct = JPiereValidCombinationUtil.searchCreateValidCombination (getCtx(), imp.getC_AcctSchema_ID(), imp.getJP_NotInvoicedReceipts_Value(), get_TrxName());
			if(NotInvoicedReceipts_Acct > 0)
			{
				imp.setNotInvoicedReceipts_Acct(NotInvoicedReceipts_Acct);
				acct.setNotInvoicedReceipts_Acct(NotInvoicedReceipts_Acct);
			}
		}

		//PayDiscount_Exp_Acct
		if(!Util.isEmpty(imp.getJP_PayDiscount_Exp_Value()))
		{
			int PayDiscount_Exp_Acct = JPiereValidCombinationUtil.searchCreateValidCombination (getCtx(), imp.getC_AcctSchema_ID(), imp.getJP_PayDiscount_Exp_Value(), get_TrxName());
			if(PayDiscount_Exp_Acct > 0)
			{
				imp.setPayDiscount_Exp_Acct(PayDiscount_Exp_Acct);
				acct.setPayDiscount_Exp_Acct(PayDiscount_Exp_Acct);
			}
		}

		//PayDiscount_Rev_Acct
		if(!Util.isEmpty(imp.getJP_PayDiscount_Rev_Value()))
		{
			int PayDiscount_Rev_Acct = JPiereValidCombinationUtil.searchCreateValidCombination (getCtx(), imp.getC_AcctSchema_ID(), imp.getJP_PayDiscount_Rev_Value(), get_TrxName());
			if(PayDiscount_Rev_Acct > 0)
			{
				imp.setPayDiscount_Rev_Acct(PayDiscount_Rev_Acct);
				acct.setPayDiscount_Rev_Acct(PayDiscount_Rev_Acct);
			}
		}

		//WriteOff_Acct
		if(!Util.isEmpty(imp.getJP_WriteOff_Acct_Value()))
		{
			int WriteOff_Acct = JPiereValidCombinationUtil.searchCreateValidCombination (getCtx(), imp.getC_AcctSchema_ID(), imp.getJP_WriteOff_Acct_Value(), get_TrxName());
			if(WriteOff_Acct > 0)
			{
				imp.setWriteOff_Acct(WriteOff_Acct);
				acct.setWriteOff_Acct(WriteOff_Acct);
			}
		}

		//C_Receivable_Acct
		if(!Util.isEmpty(imp.getJP_Receivable_Acct_Value()))
		{
			int C_Receivable_Acct = JPiereValidCombinationUtil.searchCreateValidCombination (getCtx(), imp.getC_AcctSchema_ID(), imp.getJP_Receivable_Acct_Value(), get_TrxName());
			if(C_Receivable_Acct > 0)
			{
				imp.setC_Receivable_Acct(C_Receivable_Acct);
				acct.setC_Receivable_Acct(C_Receivable_Acct);
			}
		}

		//C_Prepayment_Acct
		if(!Util.isEmpty(imp.getJP_C_PrePayment_Acct_Value()))
		{
			int C_Prepayment_Acct = JPiereValidCombinationUtil.searchCreateValidCombination (getCtx(), imp.getC_AcctSchema_ID(), imp.getJP_C_PrePayment_Acct_Value(), get_TrxName());
			if(C_Prepayment_Acct > 0)
			{
				imp.setC_Prepayment_Acct(C_Prepayment_Acct);
				acct.setC_Prepayment_Acct(C_Prepayment_Acct);
			}
		}

		//V_Liability_Acct
		if(!Util.isEmpty(imp.getJP_Liability_Acct_Value()))
		{
			int V_Liability_Acct = JPiereValidCombinationUtil.searchCreateValidCombination (getCtx(), imp.getC_AcctSchema_ID(), imp.getJP_Liability_Acct_Value(), get_TrxName());
			if(V_Liability_Acct > 0)
			{
				imp.setV_Liability_Acct(V_Liability_Acct);
				acct.setV_Liability_Acct(V_Liability_Acct);
			}
		}

		//V_Prepayment_Acct
		if(!Util.isEmpty(imp.getJP_V_Prepayment_Acct_Value()))
		{
			int V_Prepayment_Acct = JPiereValidCombinationUtil.searchCreateValidCombination (getCtx(), imp.getC_AcctSchema_ID(), imp.getJP_V_Prepayment_Acct_Value(), get_TrxName());
			if(V_Prepayment_Acct > 0)
			{
				imp.setV_Prepayment_Acct(V_Prepayment_Acct);
				acct.setV_Prepayment_Acct(V_Prepayment_Acct);
			}
		}

		acct.saveEx(get_TrxName());

	}


}	//	ImportPayment
