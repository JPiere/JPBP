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


package jpiere.base.plugin.org.compiere.acct;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.logging.Level;

import org.compiere.acct.Doc_InOut;
import org.compiere.acct.Fact;
import org.compiere.acct.FactLine;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MInOut;
import org.compiere.model.MRMA;
import org.compiere.util.Env;

import jpiere.base.plugin.org.adempiere.model.MContractAcct;
import jpiere.base.plugin.org.adempiere.model.MContractContent;

/**
*  JPIERE-0363
*
* @author Hideaki Hagiwara
*
*/
public class Doc_InOutJP extends Doc_InOut {
	
	public Doc_InOutJP(MAcctSchema as, ResultSet rs, String trxName) 
	{
		super(as, rs, trxName);
	}

	@Override
	public ArrayList<Fact> createFacts(MAcctSchema as) 
	{
		if (!as.isAccrual())
			return super.createFacts(as);
		
		
		MInOut inOut = (MInOut)getPO();
		
		
		/**iDempiere Standard Posting*/
		int JP_ContractContent_ID = inOut.get_ValueAsInt("JP_ContractContent_ID");
		if(JP_ContractContent_ID == 0)
		{
			return super.createFacts(as);
		}
		
		MContractContent contractContent = MContractContent.get(getCtx(), JP_ContractContent_ID);
		if(contractContent.getJP_Contract_Acct_ID() == 0)
		{
			return super.createFacts(as);
		}
		
		MContractAcct contractAcct = MContractAcct.get(Env.getCtx(),contractContent.getJP_Contract_Acct_ID());
		if(!contractAcct.isPostingContractAcctJP())
		{
			return super.createFacts(as);
		}
		
		/**JPiere Posting Logic*/
		ArrayList<Fact> facts = new ArrayList<Fact>();
		//  create Fact Header
		Fact fact = new Fact(this, as, Fact.POST_Actual);
		
		if(!contractAcct.isPostingRecognitionDocJP())
		{
			if (getDocumentType().equals(DOCTYPE_MatShipment) && isSOTrx()) //Sales - Shipment
			{
				;//TODO
				
			}else if ( getDocumentType().equals(DOCTYPE_MatReceipt) && isSOTrx() ){//Sales - Return
				
				;//TODO
				
			}else if (getDocumentType().equals(DOCTYPE_MatReceipt) && !isSOTrx()){//Purchasing - Receipt
				
				;//TOD
				
			}else if (getDocumentType().equals(DOCTYPE_MatShipment) && !isSOTrx()){ //Purchasing - return
			
				;//TOD
						;
			}else{
				p_Error = "DocumentType unknown: " + getDocumentType();
				log.log(Level.SEVERE, p_Error);
				return null;
			}
			
			FactLine[]  factLine = fact.getLines();
			for(int i = 0; i < factLine.length; i++)
			{
				if(inOut.getC_Order_ID() > 0)
				{
					factLine[i].set_ValueNoCheck("JP_Order_ID", inOut.getC_Order_ID());
				}else if(inOut.getM_RMA_ID() > 0){
					int M_RMA_ID = inOut.getM_RMA_ID();
					MRMA rma = new MRMA (Env.getCtx(),M_RMA_ID,null);
					int JP_Order_ID = rma.get_ValueAsInt("JP_Order_ID");
					if(JP_Order_ID > 0)
						factLine[i].set_ValueNoCheck("JP_Order_ID", JP_Order_ID);
				}
				
				factLine[i].set_ValueNoCheck("JP_ContractContent_ID", JP_ContractContent_ID);
			}//for
			
		}else{
			;//Noting to do. Posting Recognition Doc Only.
		}
		
		facts.add(fact);
		return facts;
	}
	
}
