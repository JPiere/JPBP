package jpiere.base.plugin.org.adempiere.base;

import java.util.List;

import org.compiere.acct.Fact;
import org.compiere.acct.FactLine;
import org.compiere.model.FactsValidator;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MClient;
import org.compiere.model.MInOut;
import org.compiere.model.MInOutLine;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MMatchInv;
import org.compiere.model.MRMA;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Msg;

public class JPiereContractMatchInvModelValidator implements ModelValidator,FactsValidator {

	private static CLogger log = CLogger.getCLogger(JPiereContractMatchInvModelValidator.class);
	private int AD_Client_ID = -1;
	private int AD_Org_ID = -1;
	private int AD_Role_ID = -1;
	private int AD_User_ID = -1;

	@Override
	public void initialize(ModelValidationEngine engine, MClient client) {
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();
		engine.addModelChange(MMatchInv.Table_Name, this);
		engine.addDocValidate(MMatchInv.Table_Name, this);
		engine.addFactsValidate(MMatchInv.Table_Name, this);
	}

	@Override
	public int getAD_Client_ID() {
		return AD_Client_ID;
	}

	@Override
	public String login(int AD_Org_ID, int AD_Role_ID, int AD_User_ID) {
		this.AD_Org_ID = AD_Org_ID;
		this.AD_Role_ID = AD_Role_ID;
		this.AD_User_ID = AD_User_ID;

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
		if(timing == ModelValidator.TIMING_BEFORE_COMPLETE)
		{
			;
		}
		return null;
	}

	@Override
	public String factsValidate(MAcctSchema schema, List<Fact> facts, PO po) 
	{
		if(po.get_TableName().equals(MMatchInv.Table_Name))
		{
			MMatchInv matchInv = (MMatchInv)po;
			MInvoiceLine invoiceLine = new MInvoiceLine(Env.getCtx(), matchInv.getC_InvoiceLine_ID(), po.get_TrxName());
			MInvoice invoice = new MInvoice(Env.getCtx(),invoiceLine.getC_Invoice_ID(), po.get_TrxName());
			
			MInOutLine inoutLine = new MInOutLine(Env.getCtx(), matchInv.getM_InOutLine_ID(), po.get_TrxName());
			MInOut inout = new MInOut(Env.getCtx(),inoutLine.getM_InOut_ID(), po.get_TrxName());
			
			int inv_ContractContent_ID = invoice.get_ValueAsInt("JP_ContractContent_ID");
			int io_ContractContent_ID = inout.get_ValueAsInt("JP_ContractContent_ID");
			
			if(inv_ContractContent_ID == 0 && io_ContractContent_ID == 0)
			{
				return null;
				
			}else if(inv_ContractContent_ID > 0 && io_ContractContent_ID <= 0){
				
				String msg = Msg.getMsg(Env.getCtx(),"JP_Inconsistency",new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractContent_ID"),Msg.getElement(Env.getCtx(), "JP_ContractContent_ID")});
				return msg;
			
			}else if(inv_ContractContent_ID <= 0 && io_ContractContent_ID > 0){
				
				String msg = Msg.getMsg(Env.getCtx(),"JP_Inconsistency",new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractContent_ID"),Msg.getElement(Env.getCtx(), "JP_ContractContent_ID")});
				return msg;
			}
			
			if(inv_ContractContent_ID != io_ContractContent_ID)
			{
				String msg = Msg.getMsg(Env.getCtx(),"JP_Inconsistency",new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractContent_ID"),Msg.getElement(Env.getCtx(), "JP_ContractContent_ID")});
				return msg;
			}
			
			int inv_Order_ID = invoice.getC_Order_ID();
			
			//Set Order Info
			for(Fact fact : facts)
			{
				FactLine[]  factLine = fact.getLines();
				for(int i = 0; i < factLine.length; i++)
				{
					if(invoice.getC_Order_ID() > 0)
					{
						factLine[i].set_ValueNoCheck("JP_Order_ID", inv_Order_ID);
					}else if(invoice.getM_RMA_ID() > 0){
						int M_RMA_ID = invoice.getM_RMA_ID();
						MRMA rma = new MRMA (Env.getCtx(),M_RMA_ID,po.get_TrxName());
						int JP_Order_ID = rma.get_ValueAsInt("JP_Order_ID");
						if(JP_Order_ID > 0)
							factLine[i].set_ValueNoCheck("JP_Order_ID", JP_Order_ID);
					}
					
					factLine[i].set_ValueNoCheck("JP_ContractContent_ID", inv_ContractContent_ID);
				}//for
				
			}//for
					
		}//if
		
		return null;
	}

}
