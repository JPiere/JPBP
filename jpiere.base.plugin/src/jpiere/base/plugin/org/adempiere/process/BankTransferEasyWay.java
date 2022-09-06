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


import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.logging.Level;

import org.compiere.model.MBankAccount;
import org.compiere.model.MBankStatement;
import org.compiere.model.MBankStatementLine;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.AdempiereUserError;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;
 
/**
 *  
 *  JPIERE-0577:JPBP - Bank/Cash Transfer (Easy way)
 *  Bank/Cash Transfer let money transfer between Banks (Easy way)
 *  
 *                 
 *	@author h.hagiwara(h.hagiwara@oss-erp.co.jp)
 *	
 **/
public class BankTransferEasyWay extends SvrProcess
{
	private int 			p_From_C_BankAccount_ID = 0;	// Bank Account From
	private int 			p_To_C_BankAccount_ID= 0;		// Bank Account To
	private int			p_C_Charge_ID = 0;				// Charge to be used as bridge
	private BigDecimal 	p_Amount = Env.ZERO;  			// Amount to be transfered between the accounts
	private int			p_HandlingCharge_ID = 0;		//
	private BigDecimal 	p_HandlingChargeAmt = Env.ZERO;	//
	private int			p_HandlingChargeTax_ID = 0;		//
	private String			p_Name = "";
	private String 		p_Description= "";				// Description
	private Timestamp		p_StatementDate = null;  		// Date Statement
	private Timestamp		p_DateAcct = null;  			// Date Account
	private String			p_DocAction = null;

	private MBankAccount mBankAccountFrom = null;
	private MBankAccount mBankAccountTo = null;
	
	/**
	 *  Prepare - e.g., get Parameters.
	 */
	protected void prepare()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (name.equals("From_C_BankAccount_ID"))
				p_From_C_BankAccount_ID = para[i].getParameterAsInt();
			else if (name.equals("To_C_BankAccount_ID"))
				p_To_C_BankAccount_ID = para[i].getParameterAsInt();
			else if (name.equals("C_Charge_ID"))
				p_C_Charge_ID = para[i].getParameterAsInt();
			else if (name.equals("Amount"))
				p_Amount = ((BigDecimal)para[i].getParameter());
			else if (name.equals("HandlingCharge_ID"))
				p_HandlingCharge_ID = para[i].getParameterAsInt();
			else if (name.equals("HandlingChargeAmt"))
				p_HandlingChargeAmt = ((BigDecimal)para[i].getParameter());
			else if (name.equals("HandlingChargeTax_ID"))
				p_HandlingChargeTax_ID = para[i].getParameterAsInt();
			else if (name.equals("Name"))
				p_Name = (String)para[i].getParameter();
			else if (name.equals("Description"))
				p_Description = (String)para[i].getParameter();
			else if (name.equals("StatementDate"))
				p_StatementDate = (Timestamp)para[i].getParameter();
			else if (name.equals("DateAcct"))
				p_DateAcct = (Timestamp)para[i].getParameter();
			else if (name.equals("DocAction"))
				p_DocAction = (String)para[i].getParameter();
			else
				log.log(Level.SEVERE, "prepare - Unknown Parameter: " + name);
		}
	}	//	prepare

	/**
	 *  Perform process.
	 *  @return Message (translated text)
	 *  @throws Exception if not successful
	 */
	protected String doIt() throws Exception
	{

		if (p_To_C_BankAccount_ID == 0 || p_From_C_BankAccount_ID == 0)
			throw new AdempiereUserError (Msg.parseTranslation(getCtx(), "@FillMandatory@: @To_C_BankAccount_ID@, @From_C_BankAccount_ID@"));

		if (p_To_C_BankAccount_ID == p_From_C_BankAccount_ID)
			throw new AdempiereUserError (Msg.getMsg(getCtx(), "BankFromToMustDiffer"));
		
		
		if (p_C_Charge_ID == 0)
			throw new AdempiereUserError (Msg.parseTranslation(getCtx(), "@FillMandatory@ @C_Charge_ID@"));
	
		if (p_Amount.signum() == 0)
			throw new AdempiereUserError (Msg.parseTranslation(getCtx(), "@FillMandatory@ @Amount@"));

		//	Login Date
		if (p_StatementDate == null)
			p_StatementDate = Env.getContextAsDate(getCtx(), Env.DATE);
		if (p_StatementDate == null)
			p_StatementDate = new Timestamp(System.currentTimeMillis());			

		if (p_DateAcct == null)
			p_DateAcct = p_StatementDate;

		mBankAccountFrom = new MBankAccount(getCtx(),p_From_C_BankAccount_ID, get_TrxName());
		mBankAccountTo = new MBankAccount(getCtx(),p_To_C_BankAccount_ID, get_TrxName());
		
		if(mBankAccountFrom.getC_Currency_ID() != mBankAccountTo.getC_Currency_ID())
		{
			String msg = Msg.getElement(getCtx(), "C_BankAccount_ID") +  " : " + Msg.getMsg(getCtx(), "JP_DifferentCurrency");
			throw new AdempiereUserError(msg);
		}
		
		if(mBankAccountFrom.getAD_Org_ID() == 0)
		{
			//Could not create Bank Statement because Organization of Bank Account is *;
			throw new AdempiereUserError(Msg.getMsg(getCtx(), "JP_BankTransfer_BankAccount") + " : " + mBankAccountFrom.getName());
		}
		
		if(mBankAccountTo.getAD_Org_ID() == 0 )
		{
			//Could not create Bank Statement because Organization of Bank Account is *;
			throw new AdempiereUserError(Msg.getMsg(getCtx(), "JP_BankTransfer_BankAccount") + " : " + mBankAccountTo.getName());
		}
		
		if(p_HandlingChargeAmt != null && p_HandlingChargeAmt.compareTo(Env.ZERO) != 0 && p_HandlingCharge_ID == 0)
		{
			//If you enter the Handling Charge Amount, You have to enter the Handling Charge.
			throw new AdempiereUserError(Msg.getMsg(getCtx(), "JP_BankTransfer_HandlingCharge"));
		}
		
		if(p_HandlingChargeAmt != null && p_HandlingChargeAmt.compareTo(Env.ZERO) != 0 && p_HandlingChargeTax_ID == 0)
		{
			//If you enter the Handling Charge Amount, You have to enter the Handling Charge Tax.
			throw new AdempiereUserError(Msg.getMsg(getCtx(), "JP_BankTransfer_HandlingChargeTax"));
		}
		
		generateBankTransfer();
		return "@Success@";
	}	//	doIt
	

	/**
	 * Generate BankTransfer()
	 *
	 */
	private void generateBankTransfer()
	{		
		//From
		MBankStatement mBSfrom = new MBankStatement(getCtx(), 0 ,  get_TrxName());
		mBSfrom.setAD_Org_ID(mBankAccountFrom.getAD_Org_ID());
		mBSfrom.setC_BankAccount_ID(p_From_C_BankAccount_ID);
		mBSfrom.setName(p_Name);
		mBSfrom.setDescription(p_Description);
		mBSfrom.setStatementDate(p_StatementDate);
		mBSfrom.setDateAcct(p_DateAcct);
		mBSfrom.saveEx(get_TrxName());
		
		MBankStatementLine mBSLineFrom = new MBankStatementLine(mBSfrom);
		mBSLineFrom.setLine(10);
		mBSLineFrom.setC_Currency_ID(mBankAccountFrom.getC_Currency_ID());
		mBSLineFrom.setStmtAmt(p_Amount.negate());
		mBSLineFrom.setTrxAmt(Env.ZERO);
		mBSLineFrom.setChargeAmt(p_Amount.negate());
		mBSLineFrom.setC_Charge_ID(p_C_Charge_ID);
		mBSLineFrom.setInterestAmt(Env.ZERO);
		mBSLineFrom.saveEx(get_TrxName());
		
		
		if(p_HandlingCharge_ID != 0 &&  p_HandlingChargeAmt != null &&  p_HandlingChargeAmt.compareTo(Env.ZERO) != 0)
		{
			MBankStatementLine mBSLineHandlingCharge = new MBankStatementLine(mBSfrom);
			mBSLineHandlingCharge.setLine(20);
			mBSLineHandlingCharge.setC_Currency_ID(mBankAccountFrom.getC_Currency_ID());
			mBSLineHandlingCharge.setStmtAmt(p_HandlingChargeAmt.negate());
			mBSLineHandlingCharge.setTrxAmt(Env.ZERO);
			mBSLineHandlingCharge.setChargeAmt(p_HandlingChargeAmt.negate());
			mBSLineHandlingCharge.setC_Charge_ID(p_HandlingCharge_ID);
			mBSLineHandlingCharge.setInterestAmt(Env.ZERO);
			if(p_HandlingChargeTax_ID > 0)
			{
				mBSLineHandlingCharge.set_ValueNoCheck("C_Tax_ID", p_HandlingChargeTax_ID);
				mBSLineHandlingCharge.set_ValueNoCheck("JP_SOPOType", "P");
			}
			mBSLineHandlingCharge.saveEx(get_TrxName());
		}
		
		if(!Util.isEmpty(p_DocAction) && (DocAction.ACTION_Complete.equals(p_DocAction) || DocAction.ACTION_Prepare.equals(p_DocAction))  )
		{
			mBSfrom.processIt(p_DocAction);
			mBSfrom.saveEx(get_TrxName());
		}
		
		addBufferLog(0, null, null, Msg.getElement(getCtx(), "C_BankStatement_ID") + " : "+ mBankAccountFrom.getName(), MBankStatement.Table_ID, mBSLineFrom.getC_BankStatement_ID());

		//To
		MBankStatement mBSto = new MBankStatement(getCtx(), 0 ,  get_TrxName());
		mBSto.setAD_Org_ID(mBankAccountTo.getAD_Org_ID());
		mBSto.setC_BankAccount_ID(p_To_C_BankAccount_ID);
		mBSto.setName(p_Name);
		mBSto.setDescription(p_Description);
		mBSto.setStatementDate(p_StatementDate);
		mBSto.setDateAcct(p_DateAcct);
		mBSto.saveEx(get_TrxName());
		
		MBankStatementLine mBSLineTo = new MBankStatementLine(mBSto);
		mBSLineTo.setLine(10);
		mBSLineTo.setC_Currency_ID(mBankAccountTo.getC_Currency_ID());
		mBSLineTo.setStmtAmt(p_Amount);
		mBSLineTo.setTrxAmt(Env.ZERO);
		mBSLineTo.setChargeAmt(p_Amount);
		mBSLineTo.setC_Charge_ID(p_C_Charge_ID);
		mBSLineTo.setInterestAmt(Env.ZERO);
		mBSLineTo.saveEx(get_TrxName());

		if(!Util.isEmpty(p_DocAction) && (DocAction.ACTION_Complete.equals(p_DocAction) || DocAction.ACTION_Prepare.equals(p_DocAction))  )
		{
			mBSto.processIt(p_DocAction);
			mBSto.saveEx(get_TrxName());
		}
		
		addBufferLog(0, null, null, Msg.getElement(getCtx(), "C_BankStatement_ID") + " : "+ mBankAccountTo.getName(), MBankStatement.Table_ID, mBSLineTo.getC_BankStatement_ID());

		return;

	}  //  generateBankTransfer
	
}	//	BankTransfer
