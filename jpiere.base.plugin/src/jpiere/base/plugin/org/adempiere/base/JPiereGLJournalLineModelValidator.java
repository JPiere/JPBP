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

import java.math.BigDecimal;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MClient;
import org.compiere.model.MCurrency;
import org.compiere.model.MJournalLine;
import org.compiere.model.MTax;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.model.JPiereTaxProvider;
import jpiere.base.plugin.org.adempiere.model.MGLJournalTax;
import jpiere.base.plugin.util.JPiereUtil;

/**
 * 
 * JPIERE-0544: Calculate Tax Amount automatically at GL Journal.
 * 
 * @author h.hagiwara
 *
 */
public class JPiereGLJournalLineModelValidator implements ModelValidator {

	@SuppressWarnings("unused")
	private static CLogger log = CLogger.getCLogger(JPiereGLJournalLineModelValidator.class);
	private int AD_Client_ID = -1;
	
	@Override
	public void initialize(ModelValidationEngine engine, MClient client) 
	{
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();

		engine.addModelChange(MJournalLine.Table_Name, this);
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

		//JPIERE-0544: Calculate Tax at GL Journal Line.
		if(type == ModelValidator.TYPE_BEFORE_NEW ||
				(type == ModelValidator.TYPE_BEFORE_CHANGE && (po.is_ValueChanged("AmtSourceDr")|| po.is_ValueChanged("AmtSourceCr")
																		|| po.is_ValueChanged("C_Tax_ID")|| po.is_ValueChanged("JP_SOPOType") || po.is_ValueChanged("AD_Org_ID") )))
		{
			MJournalLine jl = (MJournalLine)po;
			int C_Tax_ID = jl.get_ValueAsInt("C_Tax_ID");
			String JP_SOPOType = jl.get_ValueAsString("JP_SOPOType") ;
			
			if(C_Tax_ID == 0 )
			{
				jl.set_ValueNoCheck("JP_SOPOType",  null);
				jl.set_ValueNoCheck("JP_TaxBaseAmt",  Env.ZERO);
				jl.set_ValueNoCheck("JP_TaxAmt", Env.ZERO);
			
			}else if(Util.isEmpty(JP_SOPOType) || "N".equals(JP_SOPOType)) {
				
				jl.set_ValueNoCheck("JP_SOPOType",  "N");
				if(jl.get_Value("JP_TaxBaseAmt") == null)
				{
					jl.set_ValueNoCheck("JP_TaxBaseAmt",  Env.ZERO);
					jl.set_ValueNoCheck("JP_TaxAmt", Env.ZERO);
				}else if(jl.get_Value("JP_TaxAmt") == null){
					jl.set_ValueNoCheck("JP_TaxAmt", Env.ZERO);
				}
				
			}else {
				
				//Pre-Check to Auto Tax Calculation
				if(jl.getAmtSourceDr().compareTo(Env.ZERO) != 0 && jl.getAmtSourceCr().compareTo(Env.ZERO) != 0)
				{
					//If you would like to calculate the tax amount automatically, please enter the amount in either the debit or the credit.
					return Msg.getMsg(Env.getCtx(), "JP_GLJournalTax_Either-Debit-Credit");
				}
				
				if(jl.getAmtSourceDr().compareTo(Env.ZERO) == 0 && jl.getAmtSourceCr().compareTo(Env.ZERO) == 0)
				{
					//If you would like to calculate the tax amount automatically, please enter the amount in either the debit or the credit.
					return Msg.getMsg(Env.getCtx(), "JP_GLJournalTax_Either-Debit-Credit");
				}
				
				MTax m_tax = MTax.get(C_Tax_ID);
				if("S".equals(JP_SOPOType))
				{
					if(m_tax.getSOPOType().equals("B") || m_tax.getSOPOType().equals("S"))
					{
						;//Noting to do
					}else {
						//Different between SO/PO Type of Journal and SO/PO Type of Tax.
						return Msg.getMsg(Env.getCtx(), "JP_GLJournalTax_SOPOType");
					}
					
				}else if("P".equals(JP_SOPOType)) {
					
					if(m_tax.getSOPOType().equals("B") || m_tax.getSOPOType().equals("P"))
					{
						;//Noting to do
					}else {
						//Different between SO/PO Type of Journal and SO/PO Type of Tax.
						return Msg.getMsg(Env.getCtx(), "JP_GLJournalTax_SOPOType");
					}
				}
				
				if(m_tax.isSummary())
				{
					//You can not use Tax of Summary Level at Journal.
					return Msg.getMsg(Env.getCtx(), "JP_GLJournalTax_SummaryLevelTax");
				}
				
	
				//Calculate Tax at GL Journal Line
				BigDecimal amt = Env.ZERO;
				BigDecimal taxAmt = Env.ZERO;
				
				if(JP_SOPOType.equals("S"))
				{
					amt = jl.getAmtSourceCr().subtract(jl.getAmtSourceDr());
				}else if(JP_SOPOType.equals("P")){
					amt = jl.getAmtSourceDr().subtract(jl.getAmtSourceCr());
				}
				
				if(Env.ZERO.compareTo(amt) == 0)
				{
					jl.set_ValueNoCheck("JP_TaxBaseAmt",  Env.ZERO);
					jl.set_ValueNoCheck("JP_TaxAmt", Env.ZERO);
					
				}else {
					
					IJPiereTaxProvider taxCalculater = JPiereUtil.getJPiereTaxProvider(m_tax);
					if(taxCalculater != null)
					{
						taxAmt = taxCalculater.calculateTax(m_tax, amt, true
								, MCurrency.getStdPrecision(po.getCtx(), jl.getParent().getC_Currency_ID())
								, JPiereTaxProvider.getRoundingMode(jl.getC_BPartner_ID(), JP_SOPOType == "S"? true : false, m_tax.getC_TaxProvider()));
					}else{
						taxAmt = m_tax.calculateTax(amt, true, MCurrency.getStdPrecision(jl.getCtx(), jl.getParent().getC_Currency_ID()));
					}
	
					jl.set_ValueNoCheck("JP_TaxBaseAmt",  amt.subtract(taxAmt));
					jl.set_ValueNoCheck("JP_TaxAmt", taxAmt);
				}
			}
			
		}//JPIERE-0544: Calculate Tax at GL Journal Line.
		
		
		//JPIERE-0544: Calculate GL Journal Tax.
		if(type == ModelValidator.TYPE_AFTER_NEW || type == ModelValidator.TYPE_AFTER_CHANGE)
		{
			MJournalLine jl = (MJournalLine)po;
			
			if (type == ModelValidator.TYPE_AFTER_NEW 
					|| jl.is_ValueChanged(MGLJournalTax.COLUMNNAME_AD_Org_ID)
					|| jl.is_ValueChanged(MGLJournalTax.COLUMNNAME_C_Tax_ID)
					|| jl.is_ValueChanged(MGLJournalTax.COLUMNNAME_JP_SOPOType)
					|| jl.is_ValueChanged(MJournalLine.COLUMNNAME_AmtSourceDr)
					|| jl.is_ValueChanged(MJournalLine.COLUMNNAME_AmtSourceCr))
			{
				
				MTax m_tax = new MTax(jl.getCtx(), jl.get_ValueAsInt(MGLJournalTax.COLUMNNAME_C_Tax_ID), jl.get_TrxName());
				IJPiereTaxProvider taxCalculater = JPiereUtil.getJPiereTaxProvider(m_tax);
				if (taxCalculater == null)
					throw new AdempiereException(Msg.getMsg(jl.getCtx(), "TaxNoProvider"));
				
				boolean success = taxCalculater.recalculateTax(null, jl, type == ModelValidator.TYPE_AFTER_NEW ? true : false);
		    	if(!success)
		    		return "Error";
			}
			
		}//JPIERE-0544: Calculate GL Journal Tax.
		
		
		//JPIERE-0544: Calculate GL Journal Tax.
		if(type == ModelValidator.TYPE_AFTER_DELETE)
		{
			MJournalLine jl = (MJournalLine)po;
			
			MTax m_tax = new MTax(jl.getCtx(), jl.get_ValueAsInt(MGLJournalTax.COLUMNNAME_C_Tax_ID), jl.get_TrxName());
			IJPiereTaxProvider taxCalculater = JPiereUtil.getJPiereTaxProvider(m_tax);
			if (taxCalculater == null)
				throw new AdempiereException(Msg.getMsg(jl.getCtx(), "TaxNoProvider"));
			
			boolean success = taxCalculater.recalculateTax(null, jl, type == ModelValidator.TYPE_AFTER_NEW ? true : false);
	    	if(!success)
	    		return "Error";
	    	
		}//JPIERE-0544: Calculate GL Journal Tax.
		
		return null;
	}

	@Override
	public String docValidate(PO po, int timing)
	{
		return null;
	}
	

}
