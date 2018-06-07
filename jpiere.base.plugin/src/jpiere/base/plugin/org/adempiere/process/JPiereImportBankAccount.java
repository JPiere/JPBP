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

import org.compiere.model.MBankAccount;
import org.compiere.model.X_C_BankAccount_Acct;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.X_I_BankAccountJP;
import jpiere.base.plugin.util.JPiereValidCombinationUtil;
import jpiere.base.plugin.util.ZenginCheck;

/**
 * 	JPIERE-0397:Import Bank Account
 *
 *  @author Hideaki Hagiwara
 *
 */
public class JPiereImportBankAccount extends SvrProcess
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
			sql = new StringBuilder ("DELETE I_BankAccountJP ")
				  .append("WHERE I_IsImported='Y'").append (clientCheck);
			no = DB.executeUpdate(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("Delete Old Impored =" + no);
		}


		//Update AD_Org ID From JP_Org_Value
		sql = new StringBuilder ("UPDATE I_BankAccountJP i ")
				.append("SET AD_Org_ID=(SELECT AD_Org_ID FROM AD_org p")
				.append(" WHERE i.JP_Org_Value=p.Value AND (p.AD_Client_ID=i.AD_Client_ID or p.AD_Client_ID=0) )")
				.append(" WHERE i.JP_Org_Value IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(clientCheck);
		try {
			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("Found Organization=" + no);

		}catch(Exception e) {

			throw new Exception(Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "JP_CouldNotUpdate")
					+ "Update AD_Org_ID From JP_Org_Value");

		}


		//Update C_Bank_ID From JP_Bank_Name
		sql = new StringBuilder ("UPDATE I_BankAccountJP i ")
				.append("SET C_Bank_ID=(SELECT C_Bank_ID FROM C_Bank p")
				.append(" WHERE i.JP_Bank_Name=p.Name AND p.AD_Client_ID=i.AD_Client_ID) ")
				.append(" WHERE i.C_Bank_ID IS NULL AND JP_Bank_Name IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(clientCheck);
		try {

			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("Found Bank=" + no);

		}catch(Exception e) {

			throw new Exception(Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "JP_CouldNotUpdate")
					+ "Update C_Bank_ID From JP_Bank_Name");

		}


		//Update C_BankAccount_ID From Value
		sql = new StringBuilder ("UPDATE I_BankAccountJP i ")
				.append("SET C_BankAccount_ID=(SELECT C_BankAccount_ID FROM C_BankAccount p")
				.append(" WHERE i.Value=p.Value AND p.AD_Client_ID=i.AD_Client_ID) ")
				.append(" WHERE i.C_BankAccount_ID IS NULL AND Value IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(clientCheck);
		try {

			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("Found Bank Account=" + no);

		}catch(Exception e) {

			throw new Exception(Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "JP_CouldNotUpdate")
					+ "Update C_BankAccount_ID From Value");

		}


		//Update C_Currency_ID From ISO_Code
		sql = new StringBuilder ("UPDATE I_BankAccountJP i ")
				.append("SET C_Currency_ID=(SELECT C_Currency_ID FROM C_Currency p")
				.append(" WHERE i.ISO_Code=p.ISO_Code AND (p.AD_Client_ID=i.AD_Client_ID OR p.AD_Client_ID=0) ) ")
				.append(" WHERE i.C_Currency_ID IS NULL AND ISO_Code IS NOT NULL")
				.append(" AND i.I_IsImported='N'").append(clientCheck);
		try {

			no = DB.executeUpdateEx(sql.toString(), get_TrxName());
			if (log.isLoggable(Level.FINE)) log.fine("Found Currency=" + no);

		}catch(Exception e) {

			throw new Exception(Msg.getMsg(getCtx(), "Error") + Msg.getMsg(getCtx(), "JP_CouldNotUpdate")
					+ "Update C_Currency_ID From ISO_Code");

		}

		//Update AD_AcctSchema_ID From JP_AcctSchema_Name
		sql = new StringBuilder ("UPDATE I_BankAccountJP i ")
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
		sql = new StringBuilder ("SELECT * FROM I_BankAccountJP WHERE I_IsImported='N'")
					.append(clientCheck);
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try
		{
			pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
			rs = pstmt.executeQuery();
			while (rs.next())
			{
				X_I_BankAccountJP imp = new X_I_BankAccountJP (getCtx (), rs, get_TrxName());

				boolean isNew = true;
				if(imp.getC_BankAccount_ID()!=0){
					isNew =false;
				}

				if(isNew)//Create
				{
					MBankAccount newBankAccount = new MBankAccount(getCtx(), 0, get_TrxName());

					//AD_Org_ID
					if(imp.getAD_Org_ID() > 0)
					{
						newBankAccount.setAD_Org_ID(imp.getAD_Org_ID());
					}else {
						imp.setI_ErrorMsg(Msg.getMsg(getCtx(), "Error")+ " AD_Org_ID = 0");
						imp.setI_IsImported(false);
						imp.setProcessed(false);
						imp.saveEx(get_TrxName());
						continue;
					}

					//C_Bank_ID
					if(imp.getC_Bank_ID() > 0)
					{
						newBankAccount.setC_Bank_ID(imp.getC_Bank_ID());
					}else {
						imp.setI_ErrorMsg(Msg.getMsg(getCtx(), "Error")+ " C_Bank_ID = 0");
						imp.setI_IsImported(false);
						imp.setProcessed(false);
						imp.saveEx(get_TrxName());
						continue;
					}

					//Value
					if(!Util.isEmpty(imp.getValue()))
					{
						newBankAccount.setValue(imp.getValue());
					}else {
						Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "Value")};
						imp.setI_ErrorMsg(Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs));
						imp.setI_IsImported(false);
						imp.setProcessed(false);
						imp.saveEx(get_TrxName());
						commitEx();
						continue;
					}

					//Name
					if(!Util.isEmpty(imp.getName()))
					{
						newBankAccount.setName(imp.getName());
					}else {
						Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "Name")};
						imp.setI_ErrorMsg(Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs));
						imp.setI_IsImported(false);
						imp.setProcessed(false);
						imp.saveEx(get_TrxName());
						commitEx();
						continue;
					}

					//Description
					if(!Util.isEmpty(imp.getDescription()))
						newBankAccount.setValue(imp.getDescription());

					//IsDefault
					newBankAccount.setIsDefault(imp.isDefault());

					//JP_BranchCode
					if(!Util.isEmpty(imp.getJP_BranchCode()))
					{
						String jp_BranchCode = imp.getJP_BranchCode();
						if(jp_BranchCode.length()!=ZenginCheck.JP_BranchCode)
						{
							//{0} is {1} characters.
							Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_BranchCode"),ZenginCheck.JP_BranchCode};
							imp.setI_ErrorMsg(Msg.getMsg(Env.getCtx(),"JP_Characters",objs));
							imp.setI_IsImported(false);
							imp.setProcessed(false);
							commitEx();
							continue;
						}

						if(!ZenginCheck.numStringCheck(jp_BranchCode))
						{
							//You can not use this String : {0}.
							Object[] objs = new Object[]{jp_BranchCode};
							imp.setI_ErrorMsg(Msg.getElement(getCtx(), "JP_BranchCode") + " : " + Msg.getMsg(Env.getCtx(),"JP_CanNotUseString",objs));
							imp.setI_IsImported(false);
							imp.setProcessed(false);
							imp.saveEx(get_TrxName());
							commitEx();
							continue;
						}

						newBankAccount.set_ValueNoCheck("JP_BranchCode", imp.getJP_BranchCode());
					}

					//JP_BranchName_Kana
					if(!Util.isEmpty(imp.getJP_BranchName_Kana()))
					{
						String jp_BranchName_Kana = imp.getJP_BranchName_Kana();
						for(int i = 0; i < jp_BranchName_Kana.length(); i++)
						{
							if(!ZenginCheck.charCheck(jp_BranchName_Kana.charAt(i)))
							{
								//You can not use this character : {0}.
								Object[] objs = new Object[]{jp_BranchName_Kana.charAt(i)};
								imp.setI_ErrorMsg(Msg.getElement(getCtx(), "JP_BranchName_Kana") + " : " + Msg.getMsg(Env.getCtx(),"JP_CanNotUseChar",objs));
								imp.setI_IsImported(false);
								imp.setProcessed(false);
								imp.saveEx(get_TrxName());
								commitEx();

							}
						}//for


						if(jp_BranchName_Kana.length() > ZenginCheck.JP_BranchName_Kana)
						{
							//{0} is less than {1} characters.
							Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_BankName_Kana"),ZenginCheck.JP_BranchName_Kana};
							imp.setI_ErrorMsg(Msg.getElement(getCtx(), "JP_BranchName_Kana") + " : " + Msg.getMsg(Env.getCtx(),"JP_LessThanChars",objs));
							imp.setI_IsImported(false);
							imp.setProcessed(false);
							commitEx();
							continue;
						}

						newBankAccount.set_ValueNoCheck("JP_BranchName_Kana", imp.getJP_BranchName_Kana());
					}

					//AccountNo
					if(!Util.isEmpty(imp.getAccountNo()))
					{
						newBankAccount.setAccountNo(imp.getAccountNo());
					}else {
						imp.setI_ErrorMsg(Msg.getMsg(getCtx(), "Error")+ " Account No is Empty");
						imp.setI_IsImported(false);
						imp.setProcessed(false);
						imp.saveEx(get_TrxName());
						continue;
					}

					//JP_RequesterCode
					if(!Util.isEmpty(imp.getJP_RequesterCode()))
					{
						String jp_RequesterCode = imp.getJP_RequesterCode();
						if(jp_RequesterCode.length()!=ZenginCheck.JP_RequesterCode)
						{
							//{0} is {1} characters.
							Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_RequesterCode"),ZenginCheck.JP_RequesterCode};
							imp.setI_ErrorMsg(Msg.getMsg(Env.getCtx(),"JP_Characters",objs));
							imp.setI_IsImported(false);
							imp.setProcessed(false);
							commitEx();
							continue;
						}

						if(!ZenginCheck.numStringCheck(jp_RequesterCode))
						{
							//You can not use this String : {0}.
							Object[] objs = new Object[]{jp_RequesterCode};
							imp.setI_ErrorMsg(Msg.getElement(getCtx(), "JP_RequesterCode") + " : " + Msg.getMsg(Env.getCtx(),"JP_CanNotUseString",objs));
							imp.setI_IsImported(false);
							imp.setProcessed(false);
							imp.saveEx(get_TrxName());
							commitEx();
							continue;
						}

						newBankAccount.set_ValueNoCheck("JP_RequesterCode", imp.getJP_RequesterCode());
					}

					//JP_RequesterName
					if(!Util.isEmpty(imp.getJP_RequesterName()))
					{
						String jp_RequesterName = imp.getJP_RequesterName();
						for(int i = 0; i < jp_RequesterName.length(); i++)
						{
							if(!ZenginCheck.charCheck(jp_RequesterName.charAt(i)))
							{
								//You can not use this character : {0}.
								Object[] objs = new Object[]{jp_RequesterName.charAt(i)};
								imp.setI_ErrorMsg(Msg.getElement(getCtx(), "JP_RequesterName") + " : " + Msg.getMsg(Env.getCtx(),"JP_CanNotUseChar",objs));
								imp.setI_IsImported(false);
								imp.setProcessed(false);
								imp.saveEx(get_TrxName());
								commitEx();
								continue;
							}
						}//for

						if(jp_RequesterName.length() > ZenginCheck.JP_RequesterName)
						{
							//{0} is less than {1} characters.
							Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_BankName_Kana"),ZenginCheck.JP_RequesterName};
							imp.setI_ErrorMsg(Msg.getElement(getCtx(), "JP_RequesterName") + " : " + Msg.getMsg(Env.getCtx(),"JP_LessThanChars",objs));
							imp.setI_IsImported(false);
							imp.setProcessed(false);
							commitEx();
							continue;
						}

						newBankAccount.set_ValueNoCheck("JP_RequesterName", imp.getJP_RequesterName());
					}

					//BBAN
					if(!Util.isEmpty(imp.getBBAN()))
						newBankAccount.setBBAN(imp.getBBAN());

					//IBAN
					if(!Util.isEmpty(imp.getIBAN()))
						newBankAccount.setIBAN(imp.getIBAN());

					//C_Currency_ID
					if(imp.getC_Currency_ID() > 0) {
						newBankAccount.setC_Currency_ID(imp.getC_Currency_ID());
					}else {
						Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "C_Currency_ID")};
						imp.setI_ErrorMsg(Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs));
						imp.setI_IsImported(false);
						imp.setProcessed(false);
						imp.saveEx(get_TrxName());
						commitEx();
						continue;
					}

					//BankAccountType
					if(!Util.isEmpty(imp.getBankAccountType()))
					{
						newBankAccount.setBankAccountType(imp.getBankAccountType());
					}else {
						Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "BankAccountType")};
						imp.setI_ErrorMsg(Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs));
						imp.setI_IsImported(false);
						imp.setProcessed(false);
						imp.saveEx(get_TrxName());
						commitEx();
						continue;
					}

					//CreditLimit
					if(imp.getCreditLimit().compareTo(Env.ZERO)> 0)
						newBankAccount.setCreditLimit(imp.getCreditLimit());

					//CurrentBalance
					if(imp.getCurrentBalance().compareTo(Env.ZERO)> 0)
						newBankAccount.setCurrentBalance(imp.getCurrentBalance());

					//IsActive
					newBankAccount.setIsActive(imp.isI_IsActiveJP());
					newBankAccount.saveEx(get_TrxName());

					imp.setC_BankAccount_ID(newBankAccount.getC_BankAccount_ID());
					imp.setI_ErrorMsg(Msg.getMsg(getCtx(), "NewRecord"));
					imp.setI_IsImported(true);
					imp.setProcessed(true);

					if(!Util.isEmpty(imp.getJP_AcctSchema_Name()) && imp.getC_AcctSchema_ID() > 0)
						setBankAccountAcct(newBankAccount, imp);

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

					MBankAccount updateBankAccount = new MBankAccount(getCtx(), imp.getC_BankAccount_ID(), get_TrxName());

//					if(imp.getAD_Org_ID() > 0)
//						updateBankAccount.setAD_Org_ID(imp.getAD_Org_ID());

					if(!Util.isEmpty(imp.getName()))
						updateBankAccount.setName(imp.getName());

					if(!Util.isEmpty(imp.getDescription()))
						updateBankAccount.setValue(imp.getDescription());

					updateBankAccount.setIsDefault(imp.isDefault());

					if(!Util.isEmpty(imp.getJP_BranchCode()))
						updateBankAccount.set_ValueNoCheck("JP_BranchCode", imp.getJP_BranchCode());

					if(!Util.isEmpty(imp.getJP_BranchName_Kana()))
						updateBankAccount.set_ValueNoCheck("JP_BranchName_Kana", imp.getJP_BranchName_Kana());

					if(!Util.isEmpty(imp.getAccountNo()))
						updateBankAccount.setAccountNo(imp.getAccountNo());

					if(!Util.isEmpty(imp.getJP_RequesterCode()))
						updateBankAccount.set_ValueNoCheck("JP_RequesterCode", imp.getJP_RequesterCode());

					if(!Util.isEmpty(imp.getJP_RequesterName()))
						updateBankAccount.set_ValueNoCheck("JP_RequesterName", imp.getJP_RequesterName());

					if(!Util.isEmpty(imp.getBBAN()))
						updateBankAccount.setBBAN(imp.getBBAN());

					if(!Util.isEmpty(imp.getIBAN()))
						updateBankAccount.setIBAN(imp.getIBAN());

					if(imp.getC_Currency_ID() > 0)
						updateBankAccount.setC_Currency_ID(imp.getC_Currency_ID());

					if(!Util.isEmpty(imp.getBankAccountType()))
						updateBankAccount.setBankAccountType(imp.getBankAccountType());

					if(imp.getCreditLimit().compareTo(Env.ZERO)> 0)
						updateBankAccount.setCreditLimit(imp.getCreditLimit());

					if(imp.getCurrentBalance().compareTo(Env.ZERO)> 0)
						updateBankAccount.setCurrentBalance(imp.getCurrentBalance());

					updateBankAccount.setIsActive(imp.isI_IsActiveJP());
					updateBankAccount.saveEx(get_TrxName());

					if(!Util.isEmpty(imp.getJP_AcctSchema_Name()) && imp.getC_AcctSchema_ID() > 0)
						setBankAccountAcct(updateBankAccount, imp);

					imp.setI_ErrorMsg(Msg.getMsg(getCtx(), "Update"));
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


	private void setBankAccountAcct(MBankAccount pc, X_I_BankAccountJP imp)
	{

		X_C_BankAccount_Acct acct = null;

		String WhereClause = " C_AcctSchema_ID=" +imp.getC_AcctSchema_ID() + " AND C_BankAccount_ID=" + pc.getC_BankAccount_ID() + " AND AD_Client_ID=" +Env.getAD_Client_ID(Env.getCtx());

		StringBuilder sql = new StringBuilder ("SELECT * FROM C_BankAccount_Acct WHERE " + WhereClause);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
			rs = pstmt.executeQuery();

			if (rs.next())
			{
				acct = new X_C_BankAccount_Acct (getCtx (), rs, get_TrxName());
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

		//B_Asset_Acct
		if(!Util.isEmpty(imp.getJP_B_Asset_Acct_Value()))
		{
			int B_Asset_Acct = JPiereValidCombinationUtil.searchCreateValidCombination (getCtx(), imp.getC_AcctSchema_ID(), imp.getJP_B_Asset_Acct_Value(), get_TrxName());
			if(B_Asset_Acct > 0)
			{
				imp.setB_Asset_Acct(B_Asset_Acct);
				acct.setB_Asset_Acct(B_Asset_Acct);
			}
		}

		//B_InTransit_Acct
		if(!Util.isEmpty(imp.getJP_InTransit_Acct_Value()))
		{
			int B_InTransit_Acct = JPiereValidCombinationUtil.searchCreateValidCombination (getCtx(), imp.getC_AcctSchema_ID(), imp.getJP_InTransit_Acct_Value(), get_TrxName());
			if(B_InTransit_Acct > 0)
			{
				imp.setB_InTransit_Acct(B_InTransit_Acct);
				acct.setB_InTransit_Acct(B_InTransit_Acct);
			}
		}

		//B_PaymentSelect_Acct
		if(!Util.isEmpty(imp.getJP_PaymentSelect_Value()))
		{
			int B_PaymentSelect_Acct = JPiereValidCombinationUtil.searchCreateValidCombination (getCtx(), imp.getC_AcctSchema_ID(), imp.getJP_PaymentSelect_Value(), get_TrxName());
			if(B_PaymentSelect_Acct > 0)
			{
				imp.setB_PaymentSelect_Acct(B_PaymentSelect_Acct);
				acct.setB_PaymentSelect_Acct(B_PaymentSelect_Acct);
			}
		}

		//B_UnallocatedCash_Acct
		if(!Util.isEmpty(imp.getJP_UnallocatedCash_Value()))
		{
			int B_UnallocatedCash_Acct = JPiereValidCombinationUtil.searchCreateValidCombination (getCtx(), imp.getC_AcctSchema_ID(), imp.getJP_UnallocatedCash_Value(), get_TrxName());
			if(B_UnallocatedCash_Acct > 0)
			{
				imp.setB_UnallocatedCash_Acct(B_UnallocatedCash_Acct);
				acct.setB_UnallocatedCash_Acct(B_UnallocatedCash_Acct);
			}
		}

		//B_InterestExp_Acct
		if(!Util.isEmpty(imp.getJP_InterestExp_Acct_Value()))
		{
			int B_InterestExp_Acct = JPiereValidCombinationUtil.searchCreateValidCombination (getCtx(), imp.getC_AcctSchema_ID(), imp.getJP_InterestExp_Acct_Value(), get_TrxName());
			if(B_InterestExp_Acct > 0)
			{
				imp.setB_InterestExp_Acct(B_InterestExp_Acct);
				acct.setB_InterestExp_Acct(B_InterestExp_Acct);
			}
		}

		//B_InterestRev_Acct
		if(!Util.isEmpty(imp.getJP_InterestRev_Acct_Value()))
		{
			int B_InterestRev_Acct = JPiereValidCombinationUtil.searchCreateValidCombination (getCtx(), imp.getC_AcctSchema_ID(), imp.getJP_InterestRev_Acct_Value(), get_TrxName());
			if(B_InterestRev_Acct > 0)
			{
				imp.setB_InterestRev_Acct(B_InterestRev_Acct);
				acct.setB_InterestRev_Acct(B_InterestRev_Acct);
			}
		}

		acct.saveEx(get_TrxName());

	}


}	//	ImportPayment
