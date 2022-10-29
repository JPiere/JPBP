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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.compiere.model.MBPBankAccount;
import org.compiere.model.MClient;
import org.compiere.model.MInvoice;
import org.compiere.model.MPaySelectionLine;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.process.DocAction;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;

/**
*
* JPiere Payment Selection Lin Model Validator
*
* JPIERE-0580: Select BP Bank Account
* JPIERE-0581: Auto Calculate Bank Account Fee at Payment Selection Line.
*
* @author h.hagiwara
*
*/
public class JPierePaySelectionLineModelValidator implements ModelValidator {

	private static CLogger log = CLogger.getCLogger(JPierePaymentTermModelValidator.class);
	private int AD_Client_ID = -1;
	
	@Override
	public void initialize(ModelValidationEngine engine, MClient client) 
	{
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();
		engine.addModelChange(MPaySelectionLine.Table_Name, this);

		if (log.isLoggable(Level.FINE)) log.fine("Initialize JPierePaySelectionLineModelValidator");

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

	private static final String JP_BP_BANKkACCOUNT_ID = "JP_BP_BankAccount_ID";
	
	@Override
	public String modelChange(PO po, int type) throws Exception 
	{
		if(type == ModelValidator.TYPE_BEFORE_NEW || type == ModelValidator.TYPE_BEFORE_CHANGE)
		{
			MPaySelectionLine m_paySelectionLine = (MPaySelectionLine)po;
			if(type == ModelValidator.TYPE_BEFORE_NEW || m_paySelectionLine.is_ValueChanged(MPaySelectionLine.COLUMNNAME_C_Invoice_ID))
			{
				MInvoice m_Invoice = new MInvoice(Env.getCtx(), m_paySelectionLine.getC_Invoice_ID(), po.get_TrxName());
				int JP_BP_BankAccount_ID = m_Invoice.get_ValueAsInt(JP_BP_BANKkACCOUNT_ID);
				if(JP_BP_BankAccount_ID != 0)
				{
					m_paySelectionLine.set_ValueNoCheck("JP_BP_BankAccount_ID", JP_BP_BankAccount_ID);
					
				}else {
					
					String sql = "SELECT bpbc.C_BP_BankAccount_ID FROM C_BP_BANKACCOUNT bpbc "
							+ "WHERE bpbc.C_BPartner_ID= ? "
							+ " and bpbc.IsActive='Y' "
							+ " and bpbc.IsACH='Y' "
							+ "order by bpbc.IsDefault DESC, bpbc.Created ASC ";
					
					PreparedStatement pstmt = null;
					ResultSet rs = null;
					try
					{
						pstmt = DB.prepareStatement(sql, null);
						pstmt.setInt(1, m_Invoice.getC_BPartner_ID());
						rs = pstmt.executeQuery();

						if (rs.next())
						{
							JP_BP_BankAccount_ID = rs.getInt(1);
							if(JP_BP_BankAccount_ID != 0)
							{
								m_paySelectionLine.set_ValueNoCheck(JP_BP_BANKkACCOUNT_ID, JP_BP_BankAccount_ID);
							}
						}
						
					}catch (SQLException e){
						log.log(Level.SEVERE, sql, e);
					}finally {
						DB.close(rs, pstmt);
						rs = null;
						pstmt = null;
					}
				}
				
			}else if(m_paySelectionLine.is_ValueChanged(JP_BP_BANKkACCOUNT_ID)) {
				
				int JP_BP_BankAccount_ID = m_paySelectionLine.get_ValueAsInt(JP_BP_BANKkACCOUNT_ID);
				if(JP_BP_BankAccount_ID > 0)
				{
					MBPBankAccount bpa = new MBPBankAccount(Env.getCtx(),JP_BP_BankAccount_ID,po.get_TrxName());
					MInvoice inv = new MInvoice(Env.getCtx(), m_paySelectionLine.getC_Invoice_ID(), po.get_TrxName());
					if (bpa.getC_BPartner_ID() != inv.getC_BPartner_ID())
					{
						//Different between {0} and {1}
						String msg0 = Msg.getElement(Env.getCtx(), JP_BP_BANKkACCOUNT_ID);
						String msg1 = Msg.getElement(Env.getCtx(), "C_BPartner_ID");
						return Msg.getMsg(Env.getCtx(),"JP_Different",new Object[]{msg0,msg1});
					}
				}
				
			}
			
		}else if(type == ModelValidator.TYPE_AFTER_DELETE) {
			
			MPaySelectionLine m_paySelectionLine = (MPaySelectionLine)po;
			boolean IsAutoCalBankTransferfeeJP = m_paySelectionLine.get_ValueAsBoolean("IsAutoCalBankTransferfeeJP");
			if(IsAutoCalBankTransferfeeJP)
			{
				MInvoice m_Invoice = new MInvoice(Env.getCtx(), m_paySelectionLine.getC_Invoice_ID(), po.get_TrxName());
				if(m_Invoice.getDocStatus().equals(DocAction.STATUS_Completed))
				{
					boolean isOK = m_Invoice.processIt(DocAction.ACTION_Reverse_Correct);
					if(isOK)
					{
						m_Invoice.saveEx(po.get_TrxName());
					}else {
						//Could not reverse Invoice of Auto calculated bank transfer fee. Please reverse it before delete.
						return Msg.getMsg(Env.getCtx(), "JP_Reverse_AutoCalBankTransferFeeInvoice") + " : " + m_Invoice.getDocumentNo();
					}
				}
			}
			
		}

		return null;
	}

	@Override
	public String docValidate(PO po, int timing) 
	{
		return null;
	}

}
