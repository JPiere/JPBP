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

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.compiere.model.PO;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;

import jpiere.base.plugin.org.adempiere.model.MContractAcct;
import jpiere.base.plugin.org.adempiere.model.MContractBPAcct;
import jpiere.base.plugin.org.adempiere.model.MContractChargeAcct;
import jpiere.base.plugin.org.adempiere.model.MContractProductAcct;
import jpiere.base.plugin.org.adempiere.model.MContractTaxAcct;


/** 
* JPIERE-0363
*
* @author Hideaki Hagiwara
*
*/
public class CopyContractAcct extends SvrProcess {
	
	/** Copy from*/
	private int			p_JP_Contract_Acct_ID_From = 0;
	
	/** Copy to*/
	private int         p_JP_Contract_Acct_ID_To = 0;
	/**	Acct Schema					*/
	private int			p_C_AcctSchema_ID = 0;
	
	private String		p_JP_CopyContractAcct = null;
	
	@Override
	protected void prepare() 
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
			else if (name.equals("JP_Contract_Acct_ID"))
				p_JP_Contract_Acct_ID_From = para[i].getParameterAsInt();
			else if (name.equals("C_AcctSchema_ID"))
				p_C_AcctSchema_ID = para[i].getParameterAsInt();
			else if (name.equals("JP_CopyContractAcct"))
				p_JP_CopyContractAcct = para[i].getParameterAsString();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
		
		p_JP_Contract_Acct_ID_To = getRecord_ID();
	}
	
	@Override
	protected String doIt() throws Exception 
	{
		MContractAcct from = MContractAcct.get(getCtx(),p_JP_Contract_Acct_ID_From);
		MContractAcct to = MContractAcct.get(getCtx(),p_JP_Contract_Acct_ID_To);
			
		if(p_JP_CopyContractAcct.equals("BP"))
		{
			copyBPAcct(from,to);
		}else if(p_JP_CopyContractAcct.equals("PD")){
			copyProductAcct(from,to);
		}else if(p_JP_CopyContractAcct.equals("CH")){
			copyChargeAcct(from,to);
		}else if(p_JP_CopyContractAcct.equals("TX")){
			copyTaxAcct(from,to);
		}else if(p_JP_CopyContractAcct.equals("AL")){
			copyBPAcct(from,to);
			copyProductAcct(from,to);
			copyChargeAcct(from,to);
			copyTaxAcct(from,to);
		}
		
		return "OK";
	}	
	
	//Copy Contract BP Acct	
	private String copyBPAcct(MContractAcct from ,MContractAcct to)  
	{	
		HashMap<Integer, MContractBPAcct> bpAcctFromMaps = from.getAllContractBPAccts(true);
		MContractBPAcct bpAcctFrom = null;

		for(Map.Entry<Integer, MContractBPAcct> entryFrom : bpAcctFromMaps.entrySet()) 
		{
			bpAcctFrom = entryFrom.getValue();
			if(bpAcctFrom.getC_AcctSchema_ID()==p_C_AcctSchema_ID)
			{
				HashMap<Integer, MContractBPAcct> bpAcctToMaps = to.getAllContractBPAccts(true);
				boolean isOk = false;
				for(Map.Entry<Integer, MContractBPAcct> entryTo : bpAcctToMaps.entrySet()) 
				{
					if(bpAcctFrom.getC_AcctSchema_ID()==entryTo.getKey().intValue())
					{
						MContractBPAcct bpAcctTo = entryTo.getValue();
						PO.copyValues(bpAcctFrom, bpAcctTo);
						bpAcctTo.setJP_Contract_Acct_ID(p_JP_Contract_Acct_ID_To);
						bpAcctTo.saveEx(get_TrxName());
					    isOk= true ;
						break;
					}
				}
				
				if(!isOk)
				{
					MContractBPAcct newBPAcct = new MContractBPAcct(getCtx(), 0, get_TrxName());
					PO.copyValues(bpAcctFrom, newBPAcct);
					newBPAcct.setJP_Contract_Acct_ID(p_JP_Contract_Acct_ID_To);
					newBPAcct.saveEx(get_TrxName());
				}
				
			}else{
				continue;
			}
		}
		
		
		return "OK";
	}
	
	//Copy Contract Product Acct
	private String copyProductAcct(MContractAcct from ,MContractAcct to)  
	{			
		HashMap<Integer,HashMap<Integer,MContractProductAcct>> productAcctFromMaps = from.getAllContractProductAccts(true);
		HashMap<Integer,MContractProductAcct> pAcctFrom1 = null;
		
		for(Map.Entry<Integer,HashMap<Integer,MContractProductAcct>>  entryFrom1 : productAcctFromMaps.entrySet()) 
		{
			pAcctFrom1 = entryFrom1.getValue();
			MContractProductAcct productAcctFrom= null;
			boolean isOk = false;
			for(Map.Entry<Integer,MContractProductAcct>  entryFrom2 : pAcctFrom1.entrySet()) 
			{
				productAcctFrom = entryFrom2.getValue();
				if(productAcctFrom.getC_AcctSchema_ID()==p_C_AcctSchema_ID)
				{
					HashMap<Integer,HashMap<Integer,MContractProductAcct>>  productAcctToMaps = to.getAllContractProductAccts(true);
					HashMap<Integer,MContractProductAcct> pAccTo1 = null;
					for(Map.Entry<Integer,HashMap<Integer,MContractProductAcct>>  entryTo1 : productAcctToMaps.entrySet()) 
					{
						pAccTo1 = entryTo1.getValue();
						MContractProductAcct productAcctTo= null;
						
						for(Map.Entry<Integer,MContractProductAcct>  entryTo2 : pAccTo1.entrySet()) 
						{
							productAcctTo = entryTo2.getValue();
							if(productAcctFrom.getC_AcctSchema_ID() == productAcctTo.getC_AcctSchema_ID()
									|| productAcctFrom.getM_Product_Category_ID() == productAcctTo.getM_Product_Category_ID())
							{
								PO.copyValues(productAcctFrom, productAcctTo);
								productAcctTo.setJP_Contract_Acct_ID(p_JP_Contract_Acct_ID_To);
								productAcctTo.saveEx(get_TrxName());
							    isOk= true ;
								break;
							}
							
						}//for(Map.Entry<Integer,MContractProductAcct>  entryTo2 : pAccTo1.entrySet()) 
						
					}//for(Map.Entry<Integer,HashMap<Integer,MContractProductAcct>>  entryTo1 : productAcctToMaps.entrySet()) 
					
				}//if(productAcctFrom.getC_AcctSchema_ID()==p_C_AcctSchema_ID)
				
			}//for(Map.Entry<Integer,MContractProductAcct>  entryFrom2 : pAcctFrom1.entrySet())
			
			if(!isOk)
			{
				MContractProductAcct newProductAcct = new MContractProductAcct(getCtx(), 0, get_TrxName());
				PO.copyValues(productAcctFrom, newProductAcct);
				newProductAcct.setJP_Contract_Acct_ID(p_JP_Contract_Acct_ID_To);
				newProductAcct.saveEx(get_TrxName());
			}
		}
		
		return "OK";
	}
		
	
	//Copy Contract charge Acct
	private String copyChargeAcct(MContractAcct from ,MContractAcct to)  
	{	

		HashMap<Integer,HashMap<Integer,MContractChargeAcct>> chargeAcctFromMaps = from.getAllContractChargeAccts(true);
		HashMap<Integer,MContractChargeAcct> cAcctFrom1 = null;
		
		for(Map.Entry<Integer,HashMap<Integer,MContractChargeAcct>>  entryFrom1 : chargeAcctFromMaps.entrySet()) 
		{
			cAcctFrom1 = entryFrom1.getValue();
			MContractChargeAcct chargeAcctFrom= null;
			boolean isOk = false;
			for(Map.Entry<Integer,MContractChargeAcct>  entryFrom2 : cAcctFrom1.entrySet()) 
			{
				chargeAcctFrom = entryFrom2.getValue();
				if(chargeAcctFrom.getC_AcctSchema_ID()==p_C_AcctSchema_ID)
				{
					HashMap<Integer,HashMap<Integer,MContractChargeAcct>>  chargeAcctToMaps = to.getAllContractChargeAccts(true);
					HashMap<Integer,MContractChargeAcct> cAccTo1 = null;
					for(Map.Entry<Integer,HashMap<Integer,MContractChargeAcct>>  entryTo1 : chargeAcctToMaps.entrySet()) 
					{
						cAccTo1 = entryTo1.getValue();
						MContractChargeAcct chargeAcctTo= null;
						
						for(Map.Entry<Integer,MContractChargeAcct>  entryTo2 : cAccTo1.entrySet()) 
						{
							chargeAcctTo = entryTo2.getValue();
							if(chargeAcctFrom.getC_AcctSchema_ID() == chargeAcctTo.getC_AcctSchema_ID()
									|| chargeAcctFrom.getC_Charge_ID() == chargeAcctTo.getC_Charge_ID())
							{
								PO.copyValues(chargeAcctFrom, chargeAcctTo);
								chargeAcctTo.setJP_Contract_Acct_ID(p_JP_Contract_Acct_ID_To);
								chargeAcctTo.saveEx(get_TrxName());
							    isOk= true ;
								break;
							}
							
						}//for
						
					}//for
					
				}//if
				
			}//for
			
			if(!isOk)
			{
				MContractChargeAcct newChargeAcct = new MContractChargeAcct(getCtx(), 0, get_TrxName());
				PO.copyValues(chargeAcctFrom, newChargeAcct);
				newChargeAcct.setJP_Contract_Acct_ID(p_JP_Contract_Acct_ID_To);
				newChargeAcct.saveEx(get_TrxName());
			}
			
		}//for
		
		return "OK";
	}
	
	
	//Copy Contract Tax Acct
	private String copyTaxAcct(MContractAcct from ,MContractAcct to)  
	{	

		HashMap<Integer,HashMap<Integer,MContractTaxAcct>> taxAcctFromMaps = from.getAllContractTaxAccts(true);
		HashMap<Integer,MContractTaxAcct> tAcctFrom1 = null;
		
		for(Map.Entry<Integer,HashMap<Integer,MContractTaxAcct>>  entryFrom1 : taxAcctFromMaps.entrySet()) 
		{
			tAcctFrom1 = entryFrom1.getValue();
			MContractTaxAcct taxAcctFrom= null;
			boolean isOk = false;
			for(Map.Entry<Integer,MContractTaxAcct>  entryFrom2 : tAcctFrom1.entrySet()) 
			{
				taxAcctFrom = entryFrom2.getValue();
				if(taxAcctFrom.getC_AcctSchema_ID()==p_C_AcctSchema_ID)
				{
					HashMap<Integer,HashMap<Integer,MContractTaxAcct>>  taxAcctToMaps = to.getAllContractTaxAccts(true);
					HashMap<Integer,MContractTaxAcct> tAccTo1 = null;
					for(Map.Entry<Integer,HashMap<Integer,MContractTaxAcct>>  entryTo1 : taxAcctToMaps.entrySet()) 
					{
						tAccTo1 = entryTo1.getValue();
						MContractTaxAcct taxAcctTo= null;
						
						for(Map.Entry<Integer,MContractTaxAcct>  entryTo2 : tAccTo1.entrySet()) 
						{
							taxAcctTo = entryTo2.getValue();
							if(taxAcctFrom.getC_AcctSchema_ID() == taxAcctTo.getC_AcctSchema_ID()
									|| taxAcctFrom.getC_Tax_ID() == taxAcctTo.getC_Tax_ID())
							{
								PO.copyValues(taxAcctFrom, taxAcctTo);
								taxAcctTo.setJP_Contract_Acct_ID(p_JP_Contract_Acct_ID_To);
								taxAcctTo.saveEx(get_TrxName());
							    isOk= true ;
								break;
							}
							
						}//for
						
					}//for
					
				}//if
				
			}//for
			
			if(!isOk)
			{
				MContractTaxAcct newTaxAcct = new MContractTaxAcct(getCtx(), 0, get_TrxName());
				PO.copyValues(taxAcctFrom, newTaxAcct);
				newTaxAcct.setJP_Contract_Acct_ID(p_JP_Contract_Acct_ID_To);
				newTaxAcct.saveEx(get_TrxName());
			}
			
		}//for
		
		return "OK";
	}
	
}
