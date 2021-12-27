package jpiere.base.plugin.org.adempiere.base;

import java.util.List;
import java.util.logging.Level;

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


/**
 *  JPiere Contract Match Invoice Model Validator
 *
 *  JPIERE-0363: Contract Management
 *  JPIERE-0521: Add JP_Contract_ID, JP_ContractProcPeriod_ID Columns to Fact Acct Table
 *
 *
 *  @author  Hideaki Hagiwara（h.hagiwara@oss-erp.co.jp）
 *
 */
public class JPiereContractMatchInvModelValidator implements ModelValidator,FactsValidator {

	private static CLogger log = CLogger.getCLogger(JPiereContractMatchInvModelValidator.class);
	private int AD_Client_ID = -1;

	private static final String JP_CONTRACT_ID ="JP_Contract_ID";
	private static final String JP_CONTRACT_CONTENT_ID ="JP_ContractContent_ID";
	private static final String JP_CONTRACT_PROC_PERIOD_ID ="JP_ContractProcPeriod_ID";
	private static final String JP_ORDER_ID ="JP_Order_ID";

	@Override
	public void initialize(ModelValidationEngine engine, MClient client)
	{
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();
		engine.addModelChange(MMatchInv.Table_Name, this);
		engine.addDocValidate(MMatchInv.Table_Name, this);
		engine.addFactsValidate(MMatchInv.Table_Name, this);

		if (log.isLoggable(Level.FINE)) log.fine("Initialize JPiereContractMatchInvModelValidator");
	}

	@Override
	public int getAD_Client_ID() {
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
		if( type == ModelValidator.TYPE_BEFORE_NEW)
		{
			MMatchInv matchInv = (MMatchInv)po;
			MInvoiceLine invoiceLine = new MInvoiceLine(Env.getCtx(), matchInv.getC_InvoiceLine_ID(), po.get_TrxName());
			MInvoice invoice = new MInvoice(Env.getCtx(),invoiceLine.getC_Invoice_ID(), po.get_TrxName());

			MInOutLine inoutLine = new MInOutLine(Env.getCtx(), matchInv.getM_InOutLine_ID(), po.get_TrxName());
			MInOut inout = new MInOut(Env.getCtx(),inoutLine.getM_InOut_ID(), po.get_TrxName());

			int inv_ContractContent_ID = invoice.get_ValueAsInt(JP_CONTRACT_CONTENT_ID);
			int io_ContractContent_ID = inout.get_ValueAsInt(JP_CONTRACT_CONTENT_ID);

			if(inv_ContractContent_ID == 0 && io_ContractContent_ID == 0)
			{
				return null;

			}else if(inv_ContractContent_ID > 0 && io_ContractContent_ID <= 0){

				String msg = Msg.getMsg(Env.getCtx(),"JP_Inconsistency",new Object[]{Msg.getElement(Env.getCtx(), JP_CONTRACT_CONTENT_ID),Msg.getElement(Env.getCtx(), JP_CONTRACT_CONTENT_ID)});
				return msg;

			}else if(inv_ContractContent_ID <= 0 && io_ContractContent_ID > 0){

				String msg = Msg.getMsg(Env.getCtx(),"JP_Inconsistency",new Object[]{Msg.getElement(Env.getCtx(), JP_CONTRACT_CONTENT_ID),Msg.getElement(Env.getCtx(), JP_CONTRACT_CONTENT_ID)});
				return msg;
			}

			if(inv_ContractContent_ID != io_ContractContent_ID)
			{
				String msg = Msg.getMsg(Env.getCtx(),"JP_Inconsistency",new Object[]{Msg.getElement(Env.getCtx(), JP_CONTRACT_CONTENT_ID),Msg.getElement(Env.getCtx(), JP_CONTRACT_CONTENT_ID)});
				return msg;
			}

		}

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

			int JP_Contract_ID = 0;
			int inv_Contract_ID = invoice.get_ValueAsInt(JP_CONTRACT_ID);
			int io_Contract_ID = inout.get_ValueAsInt(JP_CONTRACT_ID);
			if(inv_Contract_ID > 0)
			{
				JP_Contract_ID = inv_Contract_ID;
			}else if(io_Contract_ID > 0) {
				JP_Contract_ID = io_Contract_ID;
			}

			int JP_ContractContent_ID = 0;
			int inv_ContractContent_ID = invoice.get_ValueAsInt(JP_CONTRACT_CONTENT_ID);
			int io_ContractContent_ID = inout.get_ValueAsInt(JP_CONTRACT_CONTENT_ID);
			if(inv_ContractContent_ID > 0)
			{
				JP_ContractContent_ID = inv_ContractContent_ID;
			}else if(io_ContractContent_ID > 0) {
				JP_ContractContent_ID = io_ContractContent_ID;
			}

			int JP_ContractProcPeriod_ID = 0;
			int inv_ContractProcPeriod_ID = invoice.get_ValueAsInt(JP_CONTRACT_PROC_PERIOD_ID);
			int io_ContractProcPeriod_ID = inout.get_ValueAsInt(JP_CONTRACT_PROC_PERIOD_ID);
			if(inv_ContractProcPeriod_ID > 0)
			{
				JP_ContractProcPeriod_ID = inv_ContractProcPeriod_ID;
			}else if(io_ContractProcPeriod_ID > 0) {
				JP_ContractProcPeriod_ID = io_ContractProcPeriod_ID;
			}

			int JP_Order_ID = 0;
			if(invoice.getC_Order_ID() > 0)
			{
				JP_Order_ID = invoice.getC_Order_ID();

			}else if(invoice.getM_RMA_ID() > 0){

				int M_RMA_ID = invoice.getM_RMA_ID();
				MRMA rma = new MRMA (Env.getCtx(), M_RMA_ID, po.get_TrxName());
				JP_Order_ID = rma.get_ValueAsInt(JP_ORDER_ID);

			}else if(inout.getC_Order_ID() > 0){

				JP_Order_ID = inout.getC_Order_ID();

			}else if(inout.getM_RMA_ID() > 0){

				int M_RMA_ID = inout.getM_RMA_ID();
				MRMA rma = new MRMA (Env.getCtx(), M_RMA_ID, po.get_TrxName());
				JP_Order_ID = rma.get_ValueAsInt(JP_ORDER_ID);
			}

			for(Fact fact : facts)
			{
				FactLine[]  factLine = fact.getLines();
				for(int i = 0; i < factLine.length; i++)
				{
					if(JP_Order_ID > 0)
						factLine[i].set_ValueNoCheck(JP_ORDER_ID, JP_Order_ID);

					if(JP_Contract_ID > 0)
						factLine[i].set_ValueNoCheck(JP_CONTRACT_ID, JP_Contract_ID);

					if(JP_ContractContent_ID > 0)
						factLine[i].set_ValueNoCheck(JP_CONTRACT_CONTENT_ID, JP_ContractContent_ID);

					if(JP_ContractProcPeriod_ID > 0)
						factLine[i].set_ValueNoCheck(JP_CONTRACT_PROC_PERIOD_ID, JP_ContractProcPeriod_ID);

				}//for

			}//for

		}//if

		return null;
	}

}
