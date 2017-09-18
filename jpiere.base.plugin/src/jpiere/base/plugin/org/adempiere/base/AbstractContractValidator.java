package jpiere.base.plugin.org.adempiere.base;

import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.util.Env;
import org.compiere.util.Msg;

import jpiere.base.plugin.org.adempiere.model.MContract;
import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractLine;
import jpiere.base.plugin.org.adempiere.model.MContractProcPeriod;

public abstract class AbstractContractValidator {
	
	protected abstract String checkHeaderContractInfoUpdate(PO po, int type);
	
	
	protected String derivativeDocHeaderCommonCheck(PO po, int type)
	{
		
		//TODO 期間契約の場合、転記日付が契約処理期間内にあるかどうかのチェック!!
		//TODO ヘッダーの処理期間と明細の処理期間があるので注意すること
		
		
		
		if( type == ModelValidator.TYPE_BEFORE_NEW
				||( type == ModelValidator.TYPE_BEFORE_CHANGE && ( po.is_ValueChanged(MContract.COLUMNNAME_JP_Contract_ID)
						||   po.is_ValueChanged(MContractContent.COLUMNNAME_JP_ContractContent_ID)
						||   po.is_ValueChanged("C_Order_ID") ) ) )
		{
			int C_Order_ID = po.get_ValueAsInt("C_Order_ID");
			
			//Check C_Order_ID
			if(C_Order_ID == 0)
			{
				po.set_ValueNoCheck("JP_Contract_ID", null);
				po.set_ValueNoCheck("JP_ContractContent_ID", null);
				po.set_ValueNoCheck("JP_ContractProcPeriod_ID", null);
				
				return null;
			}
			
			//Check JP_Contract_ID, JP_ContractContent_ID, JP_ContractProcPeriod_ID
			MOrder order = new MOrder(Env.getCtx(), C_Order_ID, po.get_TrxName());
			int JP_Contract_ID = order.get_ValueAsInt("JP_Contract_ID");
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
				po.set_ValueNoCheck("JP_ContractContent_ID", order.get_ValueAsInt("JP_ContractContent_ID"));
				po.set_ValueNoCheck("JP_ContractProcPeriod_ID", order.get_ValueAsInt("JP_ContractProcPeriod_ID"));
				
			}else if (contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_SpotContract)){
				
				/** In case of Spot Contract, order has JP_ContractContent_ID always*/
				po.set_ValueNoCheck("JP_ContractContent_ID", order.get_ValueAsInt("JP_ContractContent_ID"));
				po.set_ValueNoCheck("JP_ContractProcPeriod_ID", null);
				
			}else if (contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_GeneralContract)){
				
				po.set_ValueNoCheck("JP_ContractContent_ID", null);
				po.set_ValueNoCheck("JP_ContractProcPeriod_ID", null);
			}
		}
		
		return null;
	}
	
	protected String derivativeDocLineCommonCheck(PO po, int type)
	{
		if(type == ModelValidator.TYPE_BEFORE_NEW
				||( type == ModelValidator.TYPE_BEFORE_CHANGE && ( po.is_ValueChanged(MContractLine.COLUMNNAME_JP_ContractLine_ID)
						||   po.is_ValueChanged("C_OrderLine_ID")) ))
		{		
			int C_OrderLine_ID = po.get_ValueAsInt("C_OrderLine_ID");
			
			if(C_OrderLine_ID == 0)
			{
				po.set_ValueNoCheck("JP_ContractLine_ID", null);
				po.set_ValueNoCheck("JP_ContractProcPeriod_ID", null);
				
				return null;
				
			}
				
			MOrderLine orderLine = new MOrderLine(Env.getCtx(), C_OrderLine_ID, po.get_TrxName());
			int JP_ContractLine_ID = orderLine.get_ValueAsInt("JP_ContractLine_ID");
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
				/** In case of Period Contract, order Line has JP_ContractLine_ID and JP_ContractProcPeriod_ID always*/
				po.set_ValueNoCheck("JP_ContractLine_ID", orderLine.get_ValueAsInt("JP_ContractLine_ID"));
				po.set_ValueNoCheck("JP_ContractProcPeriod_ID", orderLine.get_ValueAsInt("JP_ContractProcPeriod_ID"));
				
			}else if (contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_SpotContract)){
				
				/** In case of Spot Contract, order has JP_ContractLine_ID sometimes*/
				po.set_ValueNoCheck("JP_ContractLine_ID", orderLine.get_ValueAsInt("JP_ContractLine_ID"));
				po.set_ValueNoCheck("JP_ContractProcPeriod_ID", null);
				
			}else if (contract.getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_GeneralContract)){
				
				po.set_ValueNoCheck("JP_ContractLine_ID", null);
				po.set_ValueNoCheck("JP_ContractProcPeriod_ID", null);
			}
			
		}//if(type == ModelValidator.TYPE_BEFORE_NEW)
		
		
		if(type == ModelValidator.TYPE_BEFORE_CHANGE)
		{		
			int C_OrderLine_ID = po.get_ValueAsInt("C_OrderLine_ID");
			int JP_ContractLine_ID = po.get_ValueAsInt("JP_ContractLine_ID");
			if(C_OrderLine_ID > 0 && JP_ContractLine_ID > 0)
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
						
						/** Check Derivative Doc Proc Period in Base Doc Proc Period */
						if(po.is_ValueChanged("JP_ContractProcPeriod_ID"))
						{
							MOrderLine orderLine = new MOrderLine(Env.getCtx(),C_OrderLine_ID ,po.get_TrxName());
							MContractProcPeriod derivativeDocPeriod = MContractProcPeriod.get(Env.getCtx(), JP_ContractProcPeriod_ID);
							if(!derivativeDocPeriod.isContainedBaseDocContractProcPeriod(orderLine.get_ValueAsInt("JP_ContractProcPeriod_ID")))
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
