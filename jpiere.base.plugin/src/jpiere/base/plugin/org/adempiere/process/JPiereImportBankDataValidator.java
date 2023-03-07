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

import org.adempiere.model.ImportValidator;
import org.adempiere.process.ImportProcess;
import org.compiere.model.MClient;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;

import jpiere.base.plugin.org.adempiere.model.I_I_BankDataJP;

/**
 *	JPIERE-0595:Import Bank Data from I_BankDataJP
 *
 *
 *  @author Hideaki Hagiwara
 */
public class JPiereImportBankDataValidator implements ModelValidator, ImportValidator {

	@SuppressWarnings("unused")
	private static CLogger log = CLogger.getCLogger(JPiereImportBankDataValidator.class);
	private int AD_Client_ID = -1;

	@Override
	public void initialize(ModelValidationEngine engine, MClient client) 
	{
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();
		engine.addImportValidate(I_I_BankDataJP.Table_Name, this);
		
	}

	@Override
	public int getAD_Client_ID() 
	{
		return AD_Client_ID;
	}

	@Override
	public String login(int AD_Org_ID, int AD_Role_ID, int AD_User_ID) 
	{
		return null;
	}

	@Override
	public String modelChange(PO po, int type) throws Exception 
	{
		return null;
	}

	@Override
	public String docValidate(PO po, int timing)
	{
		return null;
	}

	@Override
	public void validate(ImportProcess process, Object importModel, Object targetModel, int timing) 
	{
		if(ImportValidator.TIMING_BEFORE_VALIDATE == timing)
			reverseLookupBankAccountType(process);
			
		return ;

	}
	
	private void reverseLookupBankAccountType(ImportProcess process)
	{
		@SuppressWarnings("unused")
		int no = 0;
		
		//String message = Msg.getMsg(process.getCtx(), "Matching") + " : " + Msg.getElement(process.getCtx(), "AD_Org_ID");
		
		//普通預金口座の照合
		StringBuilder sql = new StringBuilder ("UPDATE I_BankDataJP i ")
				.append("SET BankAccountType='S'")
				.append(" WHERE JP_BankAccountType in ('1','普通')")
				.append(" AND i.I_IsImported='N' AND BankAccountType IS NULL ").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), process.get_TrxName());
		}catch(Exception e) {
			//throw new Exception(Msg.getMsg(process.getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}
		
		//当座預金口座の照合
		sql = new StringBuilder ("UPDATE I_BankDataJP i ")
				.append("SET BankAccountType='C'")
				.append(" WHERE JP_BankAccountType in ('2','当座')")
				.append(" AND i.I_IsImported='N' AND BankAccountType IS NULL ").append(getWhereClause());
		try {
			no = DB.executeUpdateEx(sql.toString(), process.get_TrxName());
		}catch(Exception e) {
			//throw new Exception(Msg.getMsg(process.getCtx(), "Error")  + message + " : " + e.toString() + " : " + sql );
		}
	}
	
	private String getWhereClause() {
		StringBuilder msgreturn = new StringBuilder(" AND AD_Client_ID=").append(Env.getAD_Client_ID(Env.getCtx()));
		return msgreturn.toString();
	}
}
