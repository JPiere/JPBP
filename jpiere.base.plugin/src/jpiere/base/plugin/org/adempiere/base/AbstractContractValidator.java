package jpiere.base.plugin.org.adempiere.base;

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
				if(po.get_ValueAsInt("C_Order_ID") == 0 && po.get_ValueAsInt("M_RMA_ID")  == 0)
				{
					return "期間契約の場合、受発注伝票もしくは返品受付依頼伝票の入力は必須です。";//TODO:メッセージ化
				}
				
				/** In case of Period Contract, order has JP_ContractContent_ID and JP_ContractProcPeriod_ID always*/
				po.set_ValueNoCheck("JP_ContractContent_ID", baseDoc.get_ValueAsInt("JP_ContractContent_ID"));
				po.set_ValueNoCheck("JP_ContractProcPeriod_ID", baseDoc.get_ValueAsInt("JP_ContractProcPeriod_ID"));
				
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
				||( type == ModelValidator.TYPE_BEFORE_CHANGE && ( po.is_ValueChanged(MContractLine.COLUMNNAME_JP_ContractLine_ID)
						||   po.is_ValueChanged("C_OrderLine_ID")
						||   po.is_ValueChanged("M_RMALine_ID")) ))
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
			
			if(C_OrderLine_ID > 0)
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
			MContract contract = contractLine.getParent().getParent();
			
			//Check JP_ContractLine_ID, JP_ContractProcPeriod_ID
			po.set_ValueNoCheck("JP_ContractLine_ID", JP_ContractLine_ID);

			if(contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
			{
				po.set_ValueNoCheck("JP_ContractLine_ID", baseDocLine.get_ValueAsInt("JP_ContractLine_ID"));
				if(M_RMALine_ID > 0)
					po.set_ValueNoCheck("JP_ContractProcPeriod_ID", baseDocLine.get_ValueAsInt("JP_ContractProcPeriod_ID"));
				
			}else if (contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_SpotContract)){
				
				po.set_ValueNoCheck("JP_ContractLine_ID", baseDocLine.get_ValueAsInt("JP_ContractLine_ID"));
				po.set_ValueNoCheck("JP_ContractProcPeriod_ID", null);
				
			}else if (contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_GeneralContract)){
				
				po.set_ValueNoCheck("JP_ContractLine_ID", null);
				po.set_ValueNoCheck("JP_ContractProcPeriod_ID", null);
			}
			
		}//if(type == ModelValidator.TYPE_BEFORE_NEW)
		
		
		
		//Check Contract Process Period In case of Period Contract
		if(type == ModelValidator.TYPE_BEFORE_CHANGE)
		{		
			int C_OrderLine_ID = po.get_ValueAsInt("C_OrderLine_ID");
			int M_RMALine_ID = po.get_ValueAsInt("M_RMALine_ID");
			int JP_ContractLine_ID = po.get_ValueAsInt("JP_ContractLine_ID");
			if( (C_OrderLine_ID > 0 || M_RMALine_ID > 0) && JP_ContractLine_ID > 0)
			{
				MContractLine contractLine = MContractLine.get(Env.getCtx(), JP_ContractLine_ID);
				if(contractLine.getParent().getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
				{
					/** Check Mandetory Proc Period When Period Contract */
					int JP_ContractProcPeriod_ID = po.get_ValueAsInt("JP_ContractProcPeriod_ID");
					if(JP_ContractProcPeriod_ID <= 0)
					{
						Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractProcPeriod_ID")};
						return Msg.getMsg(Env.getCtx(), "JP_InCaseOfPeriodContract") + Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs);
						
					}else{
						
						/** Check Derivative Doc Process Period is corresponding with Base Doc Process Period */
						if(po.is_ValueChanged("JP_ContractProcPeriod_ID"))
						{
							PO poLine = null;
							if(C_OrderLine_ID > 0)
								poLine = new MOrderLine(Env.getCtx(),C_OrderLine_ID ,po.get_TrxName());
							else
								poLine = new MRMALine(Env.getCtx(),C_OrderLine_ID ,po.get_TrxName());
							
							MContractProcPeriod derivativeDocPeriod = MContractProcPeriod.get(Env.getCtx(), JP_ContractProcPeriod_ID);
							if(!derivativeDocPeriod.isContainedBaseDocContractProcPeriod(poLine.get_ValueAsInt("JP_ContractProcPeriod_ID")))
							{
								//Contract Period that is derivative doc line is not corresponding with Contract Period that is base doc line.
								return Msg.getMsg(Env.getCtx(), "JP_CorrespondingContractProcPeriod");
							}
							
						}//if(ioLine.is_ValueChanged("JP_ContractProcPeriod_ID"))
						
					}//if(JP_ContractProcPeriod_ID <= 0)
					
				}//if(contractLine.getParent().getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
				
			}//if(C_OrderLine_ID > 0 && JP_ContractLine_ID > 0)
			
		}//if(type == ModelValidator.TYPE_BEFORE_CHANGE)
		
		return null;
		
	}
	
	
}
