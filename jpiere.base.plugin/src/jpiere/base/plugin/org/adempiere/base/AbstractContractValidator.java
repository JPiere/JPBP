package jpiere.base.plugin.org.adempiere.base;

import org.compiere.model.MInOutLine;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MRMA;
import org.compiere.model.MRMALine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.util.Env;
import org.compiere.util.Msg;

import jpiere.base.plugin.org.adempiere.model.MContract;
import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractLine;
import jpiere.base.plugin.org.adempiere.model.MContractProcPeriod;
import jpiere.base.plugin.org.adempiere.model.MRecognitionLine;

public abstract class AbstractContractValidator {	
	
	
	/**
	 * Use M_InOut AND C_Invoice AND JP_Recognition
	 * 
	 * 
	 * @param po
	 * @param type
	 * @return
	 */
	protected String derivativeDocHeaderCommonCheck(PO po, int type)
	{

		if( type == ModelValidator.TYPE_BEFORE_NEW
				||( type == ModelValidator.TYPE_BEFORE_CHANGE && ( po.is_ValueChanged(MContract.COLUMNNAME_JP_Contract_ID)
						||   po.is_ValueChanged(MContractContent.COLUMNNAME_JP_ContractContent_ID)
						||   po.is_ValueChanged("C_Order_ID")
						||   po.is_ValueChanged("M_RMA_ID") ) ) )
		{
			int C_Order_ID = po.get_ValueAsInt("C_Order_ID");
			int M_RMA_ID = po.get_ValueAsInt("M_RMA_ID");
			
			//Check C_Order_ID and M_RMA_ID
			if(C_Order_ID == 0 && M_RMA_ID == 0)
			{
				po.set_ValueNoCheck("JP_Contract_ID", null);
				po.set_ValueNoCheck("JP_ContractContent_ID", null);
				po.set_ValueNoCheck("JP_ContractProcPeriod_ID", null);
				
				return null;
			}
			
			//Check JP_Contract_ID, JP_ContractContent_ID, JP_ContractProcPeriod_ID
			PO baseDoc = null;			
			if(C_Order_ID > 0)
				baseDoc = new MOrder(Env.getCtx(), C_Order_ID, po.get_TrxName());
			else
				baseDoc = new MRMA(Env.getCtx(), M_RMA_ID, po.get_TrxName());
					
			int JP_Contract_ID = baseDoc.get_ValueAsInt("JP_Contract_ID");
			
			if(JP_Contract_ID == 0)
			{
				po.set_ValueNoCheck("JP_ContractContent_ID", null);
				po.set_ValueNoCheck("JP_ContractProcPeriod_ID", null);
				
				return null;			
			}
			
			po.set_ValueNoCheck("JP_Contract_ID", JP_Contract_ID);
			MContract contract = MContract.get(Env.getCtx(), JP_Contract_ID);
			if(contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
			{				
				/** In case of Period Contract, order has JP_ContractContent_ID and JP_ContractProcPeriod_ID always*/
				int JP_ContractContent_ID = baseDoc.get_ValueAsInt("JP_ContractContent_ID");
				int JP_ContractProcPeriod_ID = baseDoc.get_ValueAsInt("JP_ContractProcPeriod_ID");
				if(JP_ContractContent_ID <= 0)
				{
					Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractContent_ID")};
					return Msg.getMsg(Env.getCtx(), "JP_InCaseOfPeriodContract") + Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs);

				}else if(JP_ContractProcPeriod_ID <= 0){
					
					Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractProcPeriod_ID")};
					return Msg.getMsg(Env.getCtx(), "JP_InCaseOfPeriodContract") + Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs);
					
				}else{
					MContractContent content = MContractContent.get(Env.getCtx(),  JP_ContractContent_ID);
					MContractProcPeriod period = MContractProcPeriod.get(Env.getCtx(),  JP_ContractProcPeriod_ID);
					if(content.getJP_ContractCalender_ID() != period.getJP_ContractCalender_ID())
					{
						 //Inconsistency between JP_ContractContent_ID and JP_ContractProcPeriod_ID
						String msg = Msg.getMsg(Env.getCtx(),"JP_Inconsistency",new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractContent_ID"),Msg.getElement(Env.getCtx(), "JP_ContractProcPeriod_ID")});
						return msg;
					}
				}
				po.set_ValueNoCheck("JP_ContractContent_ID",JP_ContractContent_ID);
				po.set_ValueNoCheck("JP_ContractProcPeriod_ID", JP_ContractProcPeriod_ID);
				
				
				
			}else if (contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_SpotContract)){
				
				/** In case of Spot Contract, order has JP_ContractContent_ID always*/
				po.set_ValueNoCheck("JP_ContractContent_ID", baseDoc.get_ValueAsInt("JP_ContractContent_ID"));
				po.set_ValueNoCheck("JP_ContractProcPeriod_ID", null);
				
			}else if (contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_GeneralContract)){
				
				po.set_ValueNoCheck("JP_ContractContent_ID", null);
				po.set_ValueNoCheck("JP_ContractProcPeriod_ID", null);
			}
		}
		
		return null;
	}
	
	
	/**
	 * 
	 * Use M_InOutLine AND C_InvoiceLine AND AND JP_Recognition
	 * 
	 * @param po
	 * @param type
	 * @return
	 */
	protected String derivativeDocLineCommonCheck(PO po, int type)
	{
		if(type == ModelValidator.TYPE_BEFORE_NEW
				||( type == ModelValidator.TYPE_BEFORE_CHANGE 
					&& ( po.is_ValueChanged(MContractLine.COLUMNNAME_JP_ContractLine_ID)
						||   po.is_ValueChanged("JP_ContractProcPeriod_ID")
						||   po.is_ValueChanged("C_OrderLine_ID")
						||   po.is_ValueChanged("M_RMALine_ID") ) ))
		{		
			int C_OrderLine_ID = po.get_ValueAsInt("C_OrderLine_ID");
			int M_RMALine_ID = po.get_ValueAsInt("M_RMALine_ID");
			
			if(C_OrderLine_ID == 0 && M_RMALine_ID == 0)
			{
				po.set_ValueNoCheck("JP_ContractLine_ID", null);
				po.set_ValueNoCheck("JP_ContractProcPeriod_ID", null);
				
				return null;
			}
			
			
			PO baseDocLine = null;
			
			if(C_OrderLine_ID > 0 && M_RMALine_ID == 0)
				baseDocLine  = new MOrderLine(Env.getCtx(), C_OrderLine_ID, po.get_TrxName());
			else
				baseDocLine  = new MRMALine(Env.getCtx(), M_RMALine_ID, po.get_TrxName());

			int JP_ContractLine_ID = baseDocLine.get_ValueAsInt("JP_ContractLine_ID");
			if(JP_ContractLine_ID == 0)
			{
				po.set_ValueNoCheck("JP_ContractLine_ID", null);
				po.set_ValueNoCheck("JP_ContractProcPeriod_ID", null);
				return null;
			}
				
			MContractLine contractLine = MContractLine.get(Env.getCtx(), JP_ContractLine_ID);
			MContractContent content = contractLine.getParent();
			MContract contract = contractLine.getParent().getParent();
			
			//Check JP_ContractLine_ID, JP_ContractProcPeriod_ID
			po.set_ValueNoCheck("JP_ContractLine_ID", JP_ContractLine_ID);

			if(contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
			{
				//Set JP_ContractProcPeriod_ID Mandetory When Change
				if(type == ModelValidator.TYPE_BEFORE_CHANGE)
				{
					int JP_ContractProcPeriod_ID = po.get_ValueAsInt("JP_ContractProcPeriod_ID");
					if(po.get_TableName().equals(MInOutLine.Table_Name) || po.get_TableName().equals(MRecognitionLine.Table_Name))
					{
						if(content.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceipt)
								||content.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceiptInvoice))
						{
							if(JP_ContractProcPeriod_ID <= 0)
							{
								Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractProcPeriod_ID")};
								return Msg.getMsg(Env.getCtx(), "JP_InCaseOfPeriodContract") + Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs);
								
							}
						}
						
					}else if(po.get_TableName().equals(MInvoiceLine.Table_Name)){
						
						if(content.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateInvoice)
								||content.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceiptInvoice))
						{
							if(JP_ContractProcPeriod_ID <= 0)
							{
								Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractProcPeriod_ID")};
								return Msg.getMsg(Env.getCtx(), "JP_InCaseOfPeriodContract") + Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs);
								
							}
						}
						
					}
				}//if(type == ModelValidator.TYPE_BEFORE_CHANGE)
				
				//Both BEFORE_NEW and BEFORE_CHANGE
				if(po.get_TableName().equals(MInOutLine.Table_Name) || po.get_TableName().equals(MRecognitionLine.Table_Name))
				{
					
					if(content.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceipt)
							||content.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceiptInvoice))
					{
						if(M_RMALine_ID > 0)
						{
							int JP_ContractProcPeriod_ID = baseDocLine.get_ValueAsInt("JP_ContractProcPeriod_ID");
							if(JP_ContractProcPeriod_ID > 0)
								po.set_ValueNoCheck("JP_ContractProcPeriod_ID", JP_ContractProcPeriod_ID);
							
						}else{
							
							;//Transfer check & process to the individual Contract Validator
							
						}
						
					}else{
						
						po.set_ValueNoCheck("JP_ContractProcPeriod_ID", null);
						
					}
					
				}else if(po.get_TableName().equals(MInvoiceLine.Table_Name)){
					
					
					if(content.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateInvoice)
							||content.getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceiptInvoice))
					{
						
						;//Transfer check & process to the individual Contract Validator
						
					}else{
						
						po.set_ValueNoCheck("JP_ContractProcPeriod_ID", null);
						
					}
					
				}
				
			}else if (contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_SpotContract)){
				
				po.set_ValueNoCheck("JP_ContractProcPeriod_ID", null);
				
			}else if (contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_GeneralContract)){
				
				po.set_ValueNoCheck("JP_ContractLine_ID", null);
				po.set_ValueNoCheck("JP_ContractProcPeriod_ID", null);
			}
			
		}//if(type == ModelValidator.TYPE_BEFORE_NEW)
		
		
		
		return null;
		
	}
	
	
}
