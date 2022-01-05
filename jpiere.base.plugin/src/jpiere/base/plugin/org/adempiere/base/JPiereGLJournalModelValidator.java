package jpiere.base.plugin.org.adempiere.base;

import java.util.List;
import java.util.logging.Level;

import org.compiere.acct.Fact;
import org.compiere.acct.FactLine;
import org.compiere.model.FactsValidator;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MClient;
import org.compiere.model.MJournal;
import org.compiere.model.MJournalLine;
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

		return null;
	}

	@Override
	public String docValidate(PO po, int timing)
	{
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
