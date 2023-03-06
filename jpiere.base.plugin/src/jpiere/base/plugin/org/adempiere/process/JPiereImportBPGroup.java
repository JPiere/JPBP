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

import org.adempiere.model.ImportValidator;
import org.adempiere.process.ImportProcess;
import org.adempiere.util.IProcessUI;
import org.compiere.model.MBPGroup;
import org.compiere.model.ModelValidationEngine;
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
public class JPiereImportBPGroup extends SvrProcess implements ImportProcess
{
	private int	m_AD_Client_ID = 0;

	private boolean p_deleteOldImported = false;

	/**	Only validate, don't import		*/
	private boolean	p_IsValidateOnly = false;

	private IProcessUI processMonitor = null;

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
			else if (name.equals("IsValidateOnly"))
				p_IsValidateOnly = para[i].getParameterAsBoolean();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}

		m_AD_Client_ID = getProcessInfo().getAD_Client_ID();

	}	//	prepare

	/**
	 * 	Process
	 *	@return info
	 *	@throws Exception
	 */
	protected String doIt() throws Exception
	{
		processMonitor = Env.getProcessUI(getCtx());

		StringBuilder sql = null;
		int no = 0;
		StringBuilder clientCheck = new StringBuilder(" AND AD_Client_ID=").append(getAD_Client_ID());

		//Delete Old Imported data
		if (p_deleteOldImported)
		{
			sql = new StringBuilder ("DELETE FROM I_BP_GroupJP ")
				  .append("WHERE I_IsImported='Y'").append (clientCheck);
			try {
				no = DB.executeUpdate(sql.toString(), get_TrxName());
				if (log.isLoggable(Level.FINE)) log.fine("Delete Old Impored =" + no);
			}catch (Exception e) {
				throw new Exception(Msg.getMsg(getCtx(), "Error") + sql );
			}
		}

		//Reset Message
		sql = new StringBuilder ("UPDATE I_BP_GroupJP ")
				.append("SET I_ErrorMsg='' ")
				.append(" WHERE I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(String.valueOf(no));
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + sql );
		}

		ModelValidationEngine.get().fireImportValidate(this, null, null, ImportValidator.TIMING_BEFORE_VALIDATE);

		//Reverse Lookup Surrogate Key
		reverseLookupAD_Org_ID();
		reverseLookupC_BP_Group_ID();
		reverseLookupAD_PrintColor_ID();
		reverseLookupM_PriceList_ID();
		reverseLookupPO_PriceList_ID();
		reverseLookupM_DiscountSchema_ID();
		reverseLookupPO_DiscountSchema_ID();
		reverseLookupC_Dunning_ID();
		reverseLookupC_AcctSchema_ID();

		ModelValidationEngine.get().fireImportValidate(this, null, null, ImportValidator.TIMING_AFTER_VALIDATE);

		commitEx();
		if (p_IsValidateOnly)
		{
			return "Validated";
		}

		sql = new StringBuilder ("SELECT * FROM I_BP_GroupJP WHERE I_IsImported='N'")
					.append(clientCheck);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int recordsNum = 0;
		int successNewNum = 0;
		int successUpdateNum = 0;
		int failureNewNum = 0;
		int failureUpdateNum = 0;
		String records = Msg.getMsg(getCtx(), "JP_NumberOfRecords");
		String success = Msg.getMsg(getCtx(), "JP_Success");
		String failure = Msg.getMsg(getCtx(), "JP_Failure");
		String newRecord = Msg.getMsg(getCtx(), "New");
		String updateRecord = Msg.getMsg(getCtx(), "Update");

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
					if(createNewBPGroup(imp, newBPGroup))
						successNewNum++;
					else
						failureNewNum++;


				}else{//Update

					MBPGroup updateBPGroup = new MBPGroup(getCtx(), imp.getC_BP_Group_ID(), get_TrxName());
					if(updateBPGroup(imp,updateBPGroup))
						successUpdateNum++;
					else
						failureUpdateNum++;

				}

				commitEx();
				recordsNum++;
				if (processMonitor != null)
				{
					processMonitor.statusUpdate(
						newRecord + "( "+  success + " : " + successNewNum + "  /  " +  failure + " : " + failureNewNum + " ) + "
						+ updateRecord + " ( "+  success + " : " + successUpdateNum + "  /  " +  failure + " : " + failureUpdateNum+ " ) "
						);
				}

			}//while (rs.next())

		}catch (Exception e){

			log.log(Level.SEVERE, sql.toString(), e);
			throw e;

		}finally{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

		return records + recordsNum + " = "	+
					newRecord + "( "+  success + " : " + successNewNum + "  /  " +  failure + " : " + failureNewNum + " ) + "
					+ updateRecord + " ( "+  success + " : " + successUpdateNum + "  /  " +  failure + " : " + failureUpdateNum+ " ) ";

	}	//	doIt

	@Override
	public String getImportTableName() {
		return X_I_BP_GroupJP.Table_Name;
	}


	@Override
	public String getWhereClause() {
		StringBuilder msgreturn = new StringBuilder(" AND AD_Client_ID=").append(m_AD_Client_ID);
		return msgreturn.toString();
	}


	/**
	 *
	 *
	 * @param pc
	 * @param imp
	 */
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
				if(acct.getNotInvoicedReceipts_Acct() != NotInvoicedReceipts_Acct)
				{
					acct.setNotInvoicedReceipts_Acct(NotInvoicedReceipts_Acct);
					String msg = Msg.getMsg(getCtx(), "Update") + ": " + Msg.getElement(getCtx(), "NotInvoicedReceipts_Acct");

					if(Util.isEmpty(imp.getI_ErrorMsg()))
					{
						imp.setI_ErrorMsg(msg);
					}else {
						imp.setI_ErrorMsg(imp.getI_ErrorMsg()+ " / " + msg);
					}
				}
			}
		}

		//PayDiscount_Exp_Acct
		if(!Util.isEmpty(imp.getJP_PayDiscount_Exp_Value()))
		{
			int PayDiscount_Exp_Acct = JPiereValidCombinationUtil.searchCreateValidCombination (getCtx(), imp.getC_AcctSchema_ID(), imp.getJP_PayDiscount_Exp_Value(), get_TrxName());
			if(PayDiscount_Exp_Acct > 0)
			{
				imp.setPayDiscount_Exp_Acct(PayDiscount_Exp_Acct);
				if(acct.getPayDiscount_Exp_Acct() != PayDiscount_Exp_Acct)
				{
					acct.setPayDiscount_Exp_Acct(PayDiscount_Exp_Acct);
					String msg = Msg.getMsg(getCtx(), "Update") + ": " + Msg.getElement(getCtx(), "PayDiscount_Exp_Acct");

					if(Util.isEmpty(imp.getI_ErrorMsg()))
					{
						imp.setI_ErrorMsg(msg);
					}else {
						imp.setI_ErrorMsg(imp.getI_ErrorMsg()+ " / " + msg);
					}
				}
			}
		}

		//PayDiscount_Rev_Acct
		if(!Util.isEmpty(imp.getJP_PayDiscount_Rev_Value()))
		{
			int PayDiscount_Rev_Acct = JPiereValidCombinationUtil.searchCreateValidCombination (getCtx(), imp.getC_AcctSchema_ID(), imp.getJP_PayDiscount_Rev_Value(), get_TrxName());
			if(PayDiscount_Rev_Acct > 0)
			{
				imp.setPayDiscount_Rev_Acct(PayDiscount_Rev_Acct);
				if(acct.getPayDiscount_Rev_Acct() != PayDiscount_Rev_Acct)
				{
					acct.setPayDiscount_Rev_Acct(PayDiscount_Rev_Acct);
					String msg = Msg.getMsg(getCtx(), "Update") + ": " + Msg.getElement(getCtx(), "PayDiscount_Rev_Acct");

					if(Util.isEmpty(imp.getI_ErrorMsg()))
					{
						imp.setI_ErrorMsg(msg);
					}else {
						imp.setI_ErrorMsg(imp.getI_ErrorMsg()+ " / " + msg);
					}
				}
			}
		}

		//WriteOff_Acct
		if(!Util.isEmpty(imp.getJP_WriteOff_Acct_Value()))
		{
			int WriteOff_Acct = JPiereValidCombinationUtil.searchCreateValidCombination (getCtx(), imp.getC_AcctSchema_ID(), imp.getJP_WriteOff_Acct_Value(), get_TrxName());
			if(WriteOff_Acct > 0)
			{
				imp.setWriteOff_Acct(WriteOff_Acct);
				if(acct.getWriteOff_Acct() != WriteOff_Acct)
				{
					acct.setWriteOff_Acct(WriteOff_Acct);
					String msg = Msg.getMsg(getCtx(), "Update") + ": " + Msg.getElement(getCtx(), "WriteOff_Acct");

					if(Util.isEmpty(imp.getI_ErrorMsg()))
					{
						imp.setI_ErrorMsg(msg);
					}else {
						imp.setI_ErrorMsg(imp.getI_ErrorMsg()+ " / " + msg);
					}
				}
			}
		}

		//C_Receivable_Acct
		if(!Util.isEmpty(imp.getJP_Receivable_Acct_Value()))
		{
			int C_Receivable_Acct = JPiereValidCombinationUtil.searchCreateValidCombination (getCtx(), imp.getC_AcctSchema_ID(), imp.getJP_Receivable_Acct_Value(), get_TrxName());
			if(C_Receivable_Acct > 0)
			{
				imp.setC_Receivable_Acct(C_Receivable_Acct);
				if(acct.getC_Receivable_Acct() != C_Receivable_Acct)
				{
					acct.setC_Receivable_Acct(C_Receivable_Acct);
					String msg = Msg.getMsg(getCtx(), "Update") + ": " + Msg.getElement(getCtx(), "C_Receivable_Acct");

					if(Util.isEmpty(imp.getI_ErrorMsg()))
					{
						imp.setI_ErrorMsg(msg);
					}else {
						imp.setI_ErrorMsg(imp.getI_ErrorMsg()+ " / " + msg);
					}
				}
			}
		}

		//C_Prepayment_Acct
		if(!Util.isEmpty(imp.getJP_C_PrePayment_Acct_Value()))
		{
			int C_Prepayment_Acct = JPiereValidCombinationUtil.searchCreateValidCombination (getCtx(), imp.getC_AcctSchema_ID(), imp.getJP_C_PrePayment_Acct_Value(), get_TrxName());
			if(C_Prepayment_Acct > 0)
			{
				imp.setC_Prepayment_Acct(C_Prepayment_Acct);
				if(acct.getC_Prepayment_Acct() != C_Prepayment_Acct)
				{
					acct.setC_Prepayment_Acct(C_Prepayment_Acct);
					String msg = Msg.getMsg(getCtx(), "Update") + ": " + Msg.getElement(getCtx(), "C_Prepayment_Acct");

					if(Util.isEmpty(imp.getI_ErrorMsg()))
					{
						imp.setI_ErrorMsg(msg);
					}else {
						imp.setI_ErrorMsg(imp.getI_ErrorMsg()+ " / " + msg);
					}
				}
			}
		}

		//V_Liability_Acct
		if(!Util.isEmpty(imp.getJP_Liability_Acct_Value()))
		{
			int V_Liability_Acct = JPiereValidCombinationUtil.searchCreateValidCombination (getCtx(), imp.getC_AcctSchema_ID(), imp.getJP_Liability_Acct_Value(), get_TrxName());
			if(V_Liability_Acct > 0)
			{
				imp.setV_Liability_Acct(V_Liability_Acct);
				if(acct.getV_Liability_Acct() != V_Liability_Acct)
				{
					acct.setV_Liability_Acct(V_Liability_Acct);
					String msg = Msg.getMsg(getCtx(), "Update") + ": " + Msg.getElement(getCtx(), "V_Liability_Acct");

					if(Util.isEmpty(imp.getI_ErrorMsg()))
					{
						imp.setI_ErrorMsg(msg);
					}else {
						imp.setI_ErrorMsg(imp.getI_ErrorMsg()+ " / " + msg);
					}
				}
			}
		}

		//V_Prepayment_Acct
		if(!Util.isEmpty(imp.getJP_V_Prepayment_Acct_Value()))
		{
			int V_Prepayment_Acct = JPiereValidCombinationUtil.searchCreateValidCombination (getCtx(), imp.getC_AcctSchema_ID(), imp.getJP_V_Prepayment_Acct_Value(), get_TrxName());
			if(V_Prepayment_Acct > 0)
			{
				imp.setV_Prepayment_Acct(V_Prepayment_Acct);
				if(acct.getV_Prepayment_Acct() != V_Prepayment_Acct)
				{
					acct.setV_Prepayment_Acct(V_Prepayment_Acct);
					String msg = Msg.getMsg(getCtx(), "Update") + ": " + Msg.getElement(getCtx(), "V_Prepayment_Acct");

					if(Util.isEmpty(imp.getI_ErrorMsg()))
					{
						imp.setI_ErrorMsg(msg);
					}else {
						imp.setI_ErrorMsg(imp.getI_ErrorMsg()+ " / " + msg);
					}
				}
			}
		}

		acct.saveEx(get_TrxName());

	}

	/**
	 * Reverse Look up Organization From JP_Org_Value
	 *
	 **/
	private void reverseLookupAD_Org_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		String msg = new String();
		int no = 0;

		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "AD_Org_ID");
		if (processMonitor != null)	processMonitor.statusUpdate(msg);

		//Reverese Look up AD_Org ID From JP_Org_Value
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "AD_Org_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "JP_Org_Value") ;
		sql = new StringBuilder ("UPDATE I_BP_GroupJP i ")
				.append("SET AD_Org_ID=(SELECT AD_Org_ID FROM AD_org p")
				.append(" WHERE i.JP_Org_Value=p.Value AND (p.AD_Client_ID=i.AD_Client_ID or p.AD_Client_ID=0) AND p.IsSummary='N' ) ")
				.append(" WHERE i.JP_Org_Value IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + sql );
		}

		//Invalid JP_Org_Value
		msg = Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_Org_Value");
		sql = new StringBuilder ("UPDATE I_BP_GroupJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append(" WHERE AD_Org_ID = 0 AND JP_Org_Value IS NOT NULL AND JP_Org_Value <> '0' ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		if(no > 0)
		{
			commitEx();
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg );
		}

	}//reverseLookupAD_Org_ID

	/**
	 * Reverese Look up C_BP_Group_ID From Value
	 *
	 * @throws Exception
	 */
	private void reverseLookupC_BP_Group_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		String msg = new String();
		int no = 0;

		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_BP_Group_ID");
		if (processMonitor != null)	processMonitor.statusUpdate(msg);

		//Reverese Look up C_BP_Group_ID From Value
		sql = new StringBuilder ("UPDATE I_BP_GroupJP i ")
				.append("SET C_BP_Group_ID=(SELECT C_BP_Group_ID FROM C_BP_Group p")
				.append(" WHERE i.Value=p.Value AND p.AD_Client_ID=i.AD_Client_ID) ")
				.append(" WHERE i.C_BP_Group_ID IS NULL AND i.Value IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + sql );
		}

	}

	/**
	 * Reverese Look up AD_PrintColor_ID ID From JP_PrintColor_Name
	 *
	 * @throws Exception
	 */
	private void reverseLookupAD_PrintColor_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		String msg = new String();
		int no = 0;

		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "AD_PrintColor_ID");
		if (processMonitor != null)	processMonitor.statusUpdate(msg);

		//Reverese Look up AD_PrintColor_ID ID From JP_PrintColor_Name
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "AD_PrintColor_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "JP_PrintColor_Name") ;
		sql = new StringBuilder ("UPDATE I_BP_GroupJP i ")
				.append("SET AD_PrintColor_ID=(SELECT AD_PrintColor_ID FROM AD_PrintColor p")
				.append(" WHERE i.JP_PrintColor_Name=p.Name AND (p.AD_Client_ID=i.AD_Client_ID or p.AD_Client_ID=0) ) ")
				.append(" WHERE i.AD_PrintColor_ID IS NULL AND i.JP_PrintColor_Name IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + sql );
		}

		//Invalid JP_PrintColor_Name
		msg = Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_PrintColor_Name");
		sql = new StringBuilder ("UPDATE I_BP_GroupJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append(" WHERE AD_PrintColor_ID IS NULL AND JP_PrintColor_Name IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		if(no > 0)
		{
			commitEx();
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg );
		}

	}//reverseLookupAD_PrintColor_ID

	/**
	 * Reverse Look up M_PriceList_ID From JP_PriceList_Name
	 *
	 * @throws Exception
	 */
	private void reverseLookupM_PriceList_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		String msg = new String();
		int no = 0;

		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "M_PriceList_ID");
		if (processMonitor != null)	processMonitor.statusUpdate(msg);

		//Reverse Look up M_PriceList_ID From JP_PriceList_Name
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "M_PriceList_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "JP_PriceList_Name") ;
		sql = new StringBuilder ("UPDATE I_BP_GroupJP i ")
				.append("SET M_PriceList_ID=(SELECT M_PriceList_ID FROM M_PriceList p")
				.append(" WHERE i.JP_PriceList_Name=p.Name AND p.AD_Client_ID=i.AD_Client_ID ) ")
				.append(" WHERE i.M_PriceList_ID IS NULL AND i.JP_PriceList_Name IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		//Invalid JP_PriceList_Name
		msg = Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_PriceList_Name");
		sql = new StringBuilder ("UPDATE I_BP_GroupJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append(" WHERE JP_PriceList_Name IS NOT NULL AND M_PriceList_ID IS NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		if(no > 0)
		{
			commitEx();
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg );
		}

	}//reverseLookupM_PriceList_ID

	/**
	 * Reverse Look up PO_PriceList_ID From JP_PO_PriceList_Name
	 *
	 * @throws Exception
	 */
	private void reverseLookupPO_PriceList_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		String msg = new String();
		int no = 0;

		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "PO_PriceList_ID");
		if (processMonitor != null)	processMonitor.statusUpdate(msg);

		//Reverse Look up PO_PriceList_ID From JP_PO_PriceList_Name
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "PO_PriceList_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "JP_PO_PriceList_Name") ;
		sql = new StringBuilder ("UPDATE I_BP_GroupJP i ")
				.append("SET PO_PriceList_ID=(SELECT M_PriceList_ID FROM M_PriceList p")
				.append(" WHERE i.JP_PO_PriceList_Name=p.Name AND p.AD_Client_ID=i.AD_Client_ID ) ")
				.append(" WHERE i.PO_PriceList_ID IS NULL AND i.JP_PO_PriceList_Name IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		//Invalid JP_PO_PriceList_Name
		msg = Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_PO_PriceList_Name");
		sql = new StringBuilder ("UPDATE I_BP_GroupJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append(" WHERE JP_PO_PriceList_Name IS NOT NULL AND PO_PriceList_ID IS NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		if(no > 0)
		{
			commitEx();
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg );
		}

	}//reverseLookupPO_PriceList_ID

	/**
	 * Reverse Look up M_DiscountSchema_ID From JP_DiscountSchema_Name
	 *
	 * @throws Exception
	 */
	private void reverseLookupM_DiscountSchema_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		String msg = new String();
		int no = 0;

		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "M_DiscountSchema_ID");
		if (processMonitor != null)	processMonitor.statusUpdate(msg);

		//Reverse Look up M_DiscountSchema_ID From JP_DiscountSchema_Name
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "M_DiscountSchema_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "JP_DiscountSchema_Name") ;
		sql = new StringBuilder ("UPDATE I_BP_GroupJP i ")
				.append("SET M_DiscountSchema_ID=(SELECT M_DiscountSchema_ID FROM M_DiscountSchema p")
				.append(" WHERE i.JP_DiscountSchema_Name=p.Name AND p.AD_Client_ID=i.AD_Client_ID ) ")
				.append(" WHERE i.M_DiscountSchema_ID IS NULL AND i.JP_DiscountSchema_Name IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		//Invalid JP_DiscountSchema_Name
		msg = Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_DiscountSchema_Name");
		sql = new StringBuilder ("UPDATE I_BP_GroupJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append(" WHERE JP_DiscountSchema_Name IS NOT NULL AND M_DiscountSchema_ID IS NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		if(no > 0)
		{
			commitEx();
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg );
		}

	}//reverseLookupM_DiscountSchema_ID


	/**
	 * Reverse Look up PO_DiscountSchema_ID From JP_PO_DiscountSchema_Name
	 *
	 * @throws Exception
	 */
	private void reverseLookupPO_DiscountSchema_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		String msg = new String();
		int no = 0;

		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "PO_DiscountSchema_ID");
		if (processMonitor != null)	processMonitor.statusUpdate(msg);

		//Reverse Look up PO_DiscountSchema_ID From JP_PO_DiscountSchema_Name
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "PO_DiscountSchema_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "JP_PO_DiscountSchema_Name") ;
		sql = new StringBuilder ("UPDATE I_BP_GroupJP i ")
				.append("SET PO_DiscountSchema_ID=(SELECT M_DiscountSchema_ID FROM M_DiscountSchema p")
				.append(" WHERE i.JP_PO_DiscountSchema_Name=p.Name AND p.AD_Client_ID=i.AD_Client_ID ) ")
				.append(" WHERE i.PO_DiscountSchema_ID IS NULL AND i.JP_PO_DiscountSchema_Name IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		//Invalid JP_DiscountSchema_Name
		msg = Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_PO_DiscountSchema_Name");
		sql = new StringBuilder ("UPDATE I_BP_GroupJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append(" WHERE JP_PO_DiscountSchema_Name IS NOT NULL AND PO_DiscountSchema_ID IS NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		if(no > 0)
		{
			commitEx();
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg );
		}

	}//reverseLookupPO_DiscountSchema_ID

	/**
	 * Reverse Look up C_Dunning_ID From JP_Dunning_Name
	 *
	 * @throws Exception
	 */
	private void reverseLookupC_Dunning_ID() throws Exception
	{
		StringBuilder sql = new StringBuilder();
		String msg = new String();
		int no = 0;

		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_Dunning_ID");
		if (processMonitor != null)	processMonitor.statusUpdate(msg);

		//Reverse Look up C_Dunning_ID From JP_Dunning_Name
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_Dunning_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "JP_Dunning_Name") ;
		sql = new StringBuilder ("UPDATE I_BP_GroupJP i ")
				.append("SET C_Dunning_ID=(SELECT C_Dunning_ID FROM C_Dunning p")
				.append(" WHERE i.JP_Dunning_Name=p.Name AND p.AD_Client_ID=i.AD_Client_ID ) ")
				.append(" WHERE i.C_Dunning_ID IS NULL AND i.JP_Dunning_Name IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}


		//Invalid JP_Dunning_Name
		msg = Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_Dunning_Name");
		sql = new StringBuilder ("UPDATE I_BP_GroupJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append(" WHERE JP_Dunning_Name IS NOT NULL AND C_Dunning_ID IS NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		if(no > 0)
		{
			commitEx();
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg );
		}

	}//reverseLookupC_Dunning_ID


	/**
	 * Reverse look Up  C_AcctSchema_ID From JP_AcctSchema_Name
	 *
	 * @throws Exception
	 */
	private void reverseLookupC_AcctSchema_ID()throws Exception
	{
		StringBuilder sql = new StringBuilder();
		String msg = new String();
		int no = 0;

		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_AcctSchema_ID");
		if (processMonitor != null)	processMonitor.statusUpdate(msg);

		//Reverse look Up  C_AcctSchema_ID From JP_AcctSchema_Name
		msg = Msg.getMsg(getCtx(), "Matching") + " : " + Msg.getElement(getCtx(), "C_AcctSchema_ID")
		+ " - " + Msg.getMsg(getCtx(), "MatchFrom") + " : " + Msg.getElement(getCtx(), "JP_AcctSchema_Name") ;
		sql = new StringBuilder ("UPDATE I_BP_GroupJP i ")
				.append("SET C_AcctSchema_ID=(SELECT C_AcctSchema_ID FROM C_AcctSchema p")
				.append(" WHERE i.JP_AcctSchema_Name=p.Name AND p.AD_Client_ID=i.AD_Client_ID) ")
				.append(" WHERE i.C_AcctSchema_ID IS NULL AND JP_AcctSchema_Name IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + sql );
		}

		//Invalid JP_AcctSchema_Name
		msg = Msg.getMsg(getCtx(), "Invalid")+Msg.getElement(getCtx(), "JP_AcctSchema_Name");
		sql = new StringBuilder ("UPDATE I_BP_GroupJP ")
			.append("SET I_ErrorMsg='"+ msg + "'")
			.append(" WHERE C_AcctSchema_ID IS NULL AND JP_AcctSchema_Name IS NOT NULL ")
			.append(" AND I_IsImported<>'Y'").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine(msg +"=" + no);
		}catch(Exception e) {
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg +" : " + sql );
		}

		if(no > 0)
		{
			commitEx();
			throw new Exception(Msg.getMsg(getCtx(), "Error") + msg );
		}

	}//reverseLookupC_AcctSchema_ID

	/**
	 * Create Business Partner
	 *
	 * @param impBPGroup
	 * @param newBPGroup
	 * @return
	 */
	private boolean createNewBPGroup(X_I_BP_GroupJP impBPGroup, MBPGroup newBPGroup)
	{
		//Check Mandatory - Value
		if(Util.isEmpty(impBPGroup.getValue()))
		{
			Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "Value")};
			impBPGroup.setI_ErrorMsg(Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs));
			impBPGroup.setI_IsImported(false);
			impBPGroup.setProcessed(false);
			impBPGroup.saveEx(get_TrxName());
			return false;
		}

		//Check Mandatory - Name
		if(Util.isEmpty(impBPGroup.getName()))
		{
			Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "Name")};
			impBPGroup.setI_ErrorMsg(Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs));
			impBPGroup.setI_IsImported(false);
			impBPGroup.setProcessed(false);
			impBPGroup.saveEx(get_TrxName());
			return false;
		}

		ModelValidationEngine.get().fireImportValidate(this, impBPGroup, newBPGroup, ImportValidator.TIMING_BEFORE_IMPORT);

		newBPGroup.setAD_Org_ID(impBPGroup.getAD_Org_ID());
		newBPGroup.setValue(impBPGroup.getValue());
		newBPGroup.setName(impBPGroup.getName());

		if(!Util.isEmpty(impBPGroup.getDescription()))
			newBPGroup.setDescription(impBPGroup.getDescription());

		newBPGroup.setIsDefault(impBPGroup.isDefault());
		newBPGroup.setIsConfidentialInfo(impBPGroup.isConfidentialInfo());

		if(impBPGroup.getAD_PrintColor_ID() > 0)
			newBPGroup.setAD_PrintColor_ID(impBPGroup.getAD_PrintColor_ID());

		if(impBPGroup.getM_PriceList_ID() > 0)
			newBPGroup.setM_PriceList_ID(impBPGroup.getM_PriceList_ID());

		if(impBPGroup.getPO_PriceList_ID() > 0)
			newBPGroup.setPO_PriceList_ID(impBPGroup.getPO_PriceList_ID());

		if(impBPGroup.getM_DiscountSchema_ID() > 0)
			newBPGroup.setM_DiscountSchema_ID(impBPGroup.getM_DiscountSchema_ID());

		if(impBPGroup.getPO_DiscountSchema_ID() > 0)
			newBPGroup.setPO_DiscountSchema_ID(impBPGroup.getPO_DiscountSchema_ID());

		if(impBPGroup.getCreditWatchPercent() != null)
			newBPGroup.setCreditWatchPercent(impBPGroup.getCreditWatchPercent());

		if(impBPGroup.getPriceMatchTolerance() != null)
			newBPGroup.setPriceMatchTolerance(impBPGroup.getPriceMatchTolerance());

		if(impBPGroup.getC_Dunning_ID() > 0)
			newBPGroup.setC_Dunning_ID(impBPGroup.getC_Dunning_ID());

		newBPGroup.setIsActive(impBPGroup.isI_IsActiveJP());

		ModelValidationEngine.get().fireImportValidate(this, impBPGroup, newBPGroup, ImportValidator.TIMING_AFTER_IMPORT);


		try {
			newBPGroup.saveEx(get_TrxName());
		}catch (Exception e) {
			impBPGroup.setI_ErrorMsg(Msg.getMsg(getCtx(),"SaveIgnored") + Msg.getElement(getCtx(), "C_BP_Group_ID"));
			impBPGroup.setI_IsImported(false);
			impBPGroup.setProcessed(false);
			impBPGroup.saveEx(get_TrxName());
			return false;
		}

		impBPGroup.setC_BP_Group_ID(newBPGroup.getC_BP_Group_ID());

		if(!Util.isEmpty(impBPGroup.getJP_AcctSchema_Name()) && impBPGroup.getC_AcctSchema_ID() > 0)
			setBPGroupAcct(newBPGroup, impBPGroup);

		if(Util.isEmpty(impBPGroup.getI_ErrorMsg()))
		{
			impBPGroup.setI_ErrorMsg(Msg.getMsg(getCtx(), "NewRecord"));
		}else {
			impBPGroup.setI_ErrorMsg(Msg.getMsg(getCtx(), "NewRecord") + "  &  " +impBPGroup.getI_ErrorMsg());
		}

		impBPGroup.setI_IsImported(true);
		impBPGroup.setProcessed(true);
		impBPGroup.saveEx(get_TrxName());

		return true;
	}

	/**
	 * Update Business Partner Group
	 *
	 * @param impBPGroup
	 * @param updateBPGroup
	 * @return
	 */
	private boolean updateBPGroup(X_I_BP_GroupJP impBPGroup, MBPGroup updateBPGroup)
	{
		ModelValidationEngine.get().fireImportValidate(this, impBPGroup, updateBPGroup, ImportValidator.TIMING_BEFORE_IMPORT);

		updateBPGroup.setAD_Org_ID(impBPGroup.getAD_Org_ID());

		if(!Util.isEmpty(impBPGroup.getName()))
			updateBPGroup.setName(impBPGroup.getName());

		if(!Util.isEmpty(impBPGroup.getDescription()))
			updateBPGroup.setDescription(impBPGroup.getDescription());

		updateBPGroup.setIsDefault(impBPGroup.isDefault());
		updateBPGroup.setIsConfidentialInfo(impBPGroup.isConfidentialInfo());

		if(impBPGroup.getAD_PrintColor_ID() > 0)
			updateBPGroup.setAD_PrintColor_ID(impBPGroup.getAD_PrintColor_ID());

		if(impBPGroup.getM_PriceList_ID() > 0)
			updateBPGroup.setM_PriceList_ID(impBPGroup.getM_PriceList_ID());

		if(impBPGroup.getPO_PriceList_ID() > 0)
			updateBPGroup.setPO_PriceList_ID(impBPGroup.getPO_PriceList_ID());

		if(impBPGroup.getM_DiscountSchema_ID() > 0)
			updateBPGroup.setM_DiscountSchema_ID(impBPGroup.getM_DiscountSchema_ID());

		if(impBPGroup.getPO_DiscountSchema_ID() > 0)
			updateBPGroup.setPO_DiscountSchema_ID(impBPGroup.getPO_DiscountSchema_ID());

		if(impBPGroup.getCreditWatchPercent() != null)
			updateBPGroup.setCreditWatchPercent(impBPGroup.getCreditWatchPercent());

		if(impBPGroup.getPriceMatchTolerance() != null)
			updateBPGroup.setPriceMatchTolerance(impBPGroup.getPriceMatchTolerance());

		if(impBPGroup.getC_Dunning_ID() > 0)
			updateBPGroup.setC_Dunning_ID(impBPGroup.getC_Dunning_ID());

		updateBPGroup.setIsActive(impBPGroup.isI_IsActiveJP());


		ModelValidationEngine.get().fireImportValidate(this, impBPGroup, updateBPGroup, ImportValidator.TIMING_AFTER_IMPORT);

		try {
			updateBPGroup.saveEx(get_TrxName());
		}catch (Exception e) {
			impBPGroup.setI_ErrorMsg(Msg.getMsg(getCtx(),"SaveError") + Msg.getElement(getCtx(), "C_BP_Group_ID")+" :  " + e.toString());
			impBPGroup.setI_IsImported(false);
			impBPGroup.setProcessed(false);
			impBPGroup.saveEx(get_TrxName());
			return false;
		}

		if(!Util.isEmpty(impBPGroup.getJP_AcctSchema_Name()) && impBPGroup.getC_AcctSchema_ID() > 0)
			setBPGroupAcct(updateBPGroup, impBPGroup);

		if(Util.isEmpty(impBPGroup.getI_ErrorMsg()))
		{
			impBPGroup.setI_ErrorMsg(Msg.getMsg(getCtx(), "Update"));
		}else {
			impBPGroup.setI_ErrorMsg(Msg.getMsg(getCtx(), "Update") + "  &  " + impBPGroup.getI_ErrorMsg());
		}

		impBPGroup.setI_IsImported(true);
		impBPGroup.setProcessed(true);
		impBPGroup.saveEx(get_TrxName());

		return true;
	}

}	//	ImportPayment
