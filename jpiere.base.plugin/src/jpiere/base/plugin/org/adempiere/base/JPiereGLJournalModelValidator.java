package jpiere.base.plugin.org.adempiere.base;

import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Level;

import org.compiere.acct.Fact;
import org.compiere.acct.FactLine;
import org.compiere.model.FactsValidator;
import org.compiere.model.I_GL_Journal;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MClient;
import org.compiere.model.MJournal;
import org.compiere.model.MJournalLine;
import org.compiere.model.MSysConfig;
import org.compiere.model.ModelValidationEngine;
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
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Msg;

import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractProcPeriod;


/**
*
* JPiere GL Journal Model Validator
*
* JPIERE-0522: Add JP_Contract_ID, JP_ContractContent_ID, JP_ContractProcPeriod_ID, JP_Order_ID Columns to GL_Journal&GL JournalLine Table
* JPIERE-0554: GL Journal Balance Check
*
* @author h.hagiwara
*
*/
public class JPiereGLJournalModelValidator implements ModelValidator,FactsValidator{

	private static CLogger log = CLogger.getCLogger(JPiereGLJournalModelValidator.class);
	private int AD_Client_ID = -1;

	@Override
	public void initialize(ModelValidationEngine engine, MClient client)
	{
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();

		engine.addModelChange(MJournal.Table_Name, this);
		engine.addModelChange(MJournalLine.Table_Name, this);
		engine.addDocValidate(MJournal.Table_Name, this);
		engine.addFactsValidate(MJournal.Table_Name, this);

		if (log.isLoggable(Level.FINE)) log.fine("Initialize JPiereGLJournalModelValidator");

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

	private static final String JP_CONTRACT_ID ="JP_Contract_ID";
	private static final String JP_CONTRACT_CONTENT_ID ="JP_ContractContent_ID";
	private static final String JP_CONTRACT_PROC_PERIOD_ID ="JP_ContractProcPeriod_ID";
	private static final String JP_ORDER_ID ="JP_Order_ID";

	@Override
	public String modelChange(PO po, int type) throws Exception
	{
		//JPIERE-0522
		if( type == ModelValidator.TYPE_BEFORE_NEW
				|| (type == ModelValidator.TYPE_BEFORE_CHANGE && (po.is_ValueChanged(JP_CONTRACT_ID) || po.is_ValueChanged(JP_CONTRACT_CONTENT_ID) || po.is_ValueChanged(JP_CONTRACT_PROC_PERIOD_ID)) ) )
		{
			int JP_Contract_ID = po.get_ValueAsInt(JP_CONTRACT_ID);
			if(JP_Contract_ID == 0)
			{
				po.set_ValueNoCheck(JP_CONTRACT_CONTENT_ID, null);
				po.set_ValueNoCheck(JP_CONTRACT_PROC_PERIOD_ID, null);

			}else {

				int JP_ContractContent_ID = po.get_ValueAsInt(JP_CONTRACT_CONTENT_ID);
				if(JP_ContractContent_ID == 0)
				{
					po.set_ValueNoCheck(JP_CONTRACT_PROC_PERIOD_ID, null);

				}else {

					MContractContent m_contractContent = MContractContent.get(po.getCtx(), JP_ContractContent_ID);
					if(JP_Contract_ID != m_contractContent.getJP_Contract_ID())
					{
						 //Inconsistency between JP_Contract_ID and JP_ContractContent_ID
						String msg = Msg.getMsg(Env.getCtx(),"JP_Inconsistency",new Object[]{Msg.getElement(Env.getCtx(), JP_CONTRACT_ID),Msg.getElement(Env.getCtx(), JP_CONTRACT_CONTENT_ID)});
						return msg;
					}

					int JP_ContractProcPeriod_ID = po.get_ValueAsInt(JP_CONTRACT_PROC_PERIOD_ID);
					if(JP_ContractProcPeriod_ID != 0)
					{
						MContractProcPeriod period = MContractProcPeriod.get(Env.getCtx(),  JP_ContractProcPeriod_ID);
						if(m_contractContent.getJP_ContractCalender_ID() != period.getJP_ContractCalender_ID())
						{
							 //Inconsistency between JP_ContractContent_ID and JP_ContractProcPeriod_ID
							String msg = Msg.getMsg(Env.getCtx(),"JP_Inconsistency",new Object[]{Msg.getElement(Env.getCtx(), JP_CONTRACT_CONTENT_ID),Msg.getElement(Env.getCtx(), JP_CONTRACT_PROC_PERIOD_ID)});
							return msg;
						}
					}
				}
			}
		}

		//JPIERE-0539:　Reversal Doc - Inherit header of original Doc.
		if( type == ModelValidator.TYPE_BEFORE_NEW)
		{
			if(po instanceof I_GL_Journal)
			{
				int Reversal_ID = po.get_ValueAsInt("Reversal_ID");
				if(Reversal_ID > 0)
				{
					MJournal originalJournal = new MJournal(po.getCtx(), Reversal_ID, po.get_TrxName());
					int JP_Contract_ID = originalJournal.get_ValueAsInt("JP_Contract_ID");
					int JP_ContractContent_ID = originalJournal.get_ValueAsInt("JP_ContractContent_ID");
					int JP_ContractProcPeriod_ID = originalJournal.get_ValueAsInt("JP_ContractProcPeriod_ID");
					int JP_Order_ID = originalJournal.get_ValueAsInt("JP_Order_ID");
					int JP_Invoice_ID = originalJournal.get_ValueAsInt("JP_Invoice_ID");
					
					if(JP_Contract_ID != 0)
						po.set_ValueNoCheck("JP_Contract_ID", JP_Contract_ID);
					if(JP_ContractContent_ID != 0)
						po.set_ValueNoCheck("JP_ContractContent_ID", JP_ContractContent_ID);
					if(JP_ContractProcPeriod_ID != 0)
						po.set_ValueNoCheck("JP_ContractProcPeriod_ID", JP_ContractProcPeriod_ID);
					if(JP_Order_ID != 0)
						po.set_ValueNoCheck("JP_Order_ID", JP_Order_ID);
					if(JP_Invoice_ID != 0)
						po.set_ValueNoCheck("JP_Invoice_ID", JP_Invoice_ID);
				}
			}
		}
						
		return null;
	}

	@Override
	public String docValidate(PO po, int timing)
	{
		if(timing == ModelValidator.TIMING_BEFORE_PREPARE)
		{
			if(po instanceof I_GL_Journal)
			{
				I_GL_Journal journal = (I_GL_Journal)po;
				
				//JPIERE-0554: GL Journal Balance Check
				String JP_GL_JOURNAL_BALANCE_CHECK_POSTINGTYPE = MSysConfig.getValue("JP_GL_JOURNAL_BALANCE_CHECK_POSTINGTYPE", "A", po.getAD_Client_ID());
				String postingType = journal.getPostingType();
				if(JP_GL_JOURNAL_BALANCE_CHECK_POSTINGTYPE.contains(postingType))
				{
					if (journal.getTotalDr().compareTo(journal.getTotalCr()) != 0)
					{
						return Msg.getMsg(Env.getCtx(), "UnbalancedJornal");
					}
				}
			}
		}
		
		//JPIERE-0544: GL Journal Tax Auto Calculate.
		//In case of Auto Tax Calculation not applicable, JP_TaxBaseAmt and JP_TaxAmt at Reversal doc copy from Origin doc.
		if(timing == ModelValidator.TIMING_BEFORE_CLOSE)
		{
			if(po instanceof I_GL_Journal)
			{
				MJournal reversalGLJournal = (MJournal)po;
				int reversal_ID = reversalGLJournal.getReversal_ID();
				if(reversal_ID > 0)
				{
					MJournalLine[] reverse_glLines = reversalGLJournal.getLines(true);
					MJournal originalGLJournal = new MJournal(po.getCtx(),reversal_ID, po.get_TrxName());
					MJournalLine[] original_glLines = originalGLJournal.getLines(true);
					for(int i = 0; i < original_glLines.length; i++)
					{
						String JP_SOPOType = original_glLines[i].get_ValueAsString("JP_SOPOType");
						if("N".equals(JP_SOPOType))//Auto Tax Calculation not applicable
						{
							int C_Tax_ID = original_glLines[i].get_ValueAsInt("C_Tax_ID");
							if(C_Tax_ID > 0)
							{
								reverse_glLines[i].set_ValueNoCheck("C_Tax_ID", C_Tax_ID);
							}
							
							reverse_glLines[i].set_ValueNoCheck("JP_SOPOType", "N");
							
							Object obj_TaxBaseAmt =	original_glLines[i].get_Value("JP_TaxBaseAmt");
							if(obj_TaxBaseAmt != null)
							{
								BigDecimal JP_TaxBaseAmt = (BigDecimal)obj_TaxBaseAmt;
								reverse_glLines[i].set_ValueNoCheck("JP_TaxBaseAmt", JP_TaxBaseAmt.negate());
							}else {
								reverse_glLines[i].set_ValueNoCheck("JP_TaxBaseAmt", Env.ZERO);
							}
							
							Object obj_TaxAmt =	original_glLines[i].get_Value("JP_TaxAmt");
							if(obj_TaxAmt != null)
							{
								BigDecimal JP_TaxAmt = (BigDecimal)obj_TaxAmt;
								reverse_glLines[i].set_ValueNoCheck("JP_TaxAmt", JP_TaxAmt.negate());						
							}else {
								reverse_glLines[i].set_ValueNoCheck("JP_TaxAmt", Env.ZERO);	
							}
							
							BigDecimal qty = original_glLines[i].getQty();
							if(qty != null)
								reverse_glLines[i].setQty(qty.negate());
							
							Object obj_PriceActual = original_glLines[i].get_Value("JP_PriceActual");
							if(obj_PriceActual != null)
							{
								reverse_glLines[i].set_ValueNoCheck("JP_PriceActual", obj_PriceActual);						
							}else {
								reverse_glLines[i].set_ValueNoCheck("JP_PriceActual", Env.ZERO);	
							}
							
							reverse_glLines[i].saveEx(po.get_TrxName());
						}
					}//for
				}
			}
		}
		
		return null;
	}

	@Override
	public String factsValidate(MAcctSchema schema, List<Fact> facts, PO po)
	{
		//JPIERE-0522
		if(po.get_TableName().equals(MJournal.Table_Name))
		{
			int JP_Order_ID = po.get_ValueAsInt(JP_ORDER_ID);
			int JP_Contract_ID = po.get_ValueAsInt(JP_CONTRACT_ID);
			int JP_ContractContent_ID = po.get_ValueAsInt(JP_CONTRACT_CONTENT_ID);
			int JP_ContractProcPeriod_ID = po.get_ValueAsInt(JP_CONTRACT_PROC_PERIOD_ID);

			for(Fact fact : facts)
			{
				FactLine[]  factLine = fact.getLines();
				PO m_GLJournalLine = null;
				for(int i = 0; i < factLine.length; i++)
				{

					if(factLine[i].getDocLine() == null)
						continue;
					
					m_GLJournalLine = factLine[i].getDocLine().getPO();

					if(m_GLJournalLine.get_ValueAsInt(JP_ORDER_ID) > 0)
					{
						factLine[i].set_ValueNoCheck(JP_ORDER_ID, m_GLJournalLine.get_ValueAsInt(JP_ORDER_ID));

					}else if(JP_Order_ID > 0) {

						factLine[i].set_ValueNoCheck(JP_ORDER_ID, JP_Order_ID);
					}

					if(m_GLJournalLine.get_ValueAsInt(JP_CONTRACT_ID) > 0)
					{
						factLine[i].set_ValueNoCheck(JP_CONTRACT_ID, m_GLJournalLine.get_ValueAsInt(JP_CONTRACT_ID));
					}else if(JP_Contract_ID > 0) {
						factLine[i].set_ValueNoCheck(JP_CONTRACT_ID, JP_Contract_ID);
					}

					if(m_GLJournalLine.get_ValueAsInt(JP_CONTRACT_CONTENT_ID) > 0)
					{
						factLine[i].set_ValueNoCheck(JP_CONTRACT_CONTENT_ID, m_GLJournalLine.get_ValueAsInt(JP_CONTRACT_CONTENT_ID));
					}else if(JP_ContractContent_ID > 0) {
						factLine[i].set_ValueNoCheck(JP_CONTRACT_CONTENT_ID, JP_ContractContent_ID);
					}

					if(m_GLJournalLine.get_ValueAsInt(JP_CONTRACT_PROC_PERIOD_ID) > 0)
					{
						factLine[i].set_ValueNoCheck(JP_CONTRACT_PROC_PERIOD_ID, m_GLJournalLine.get_ValueAsInt(JP_CONTRACT_PROC_PERIOD_ID));
					}else if(JP_ContractProcPeriod_ID > 0) {
						factLine[i].set_ValueNoCheck(JP_CONTRACT_PROC_PERIOD_ID, JP_ContractProcPeriod_ID);
					}

				}//for

			}//for

		}

		return null;
	}

}
