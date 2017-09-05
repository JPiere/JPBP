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

package jpiere.base.plugin.org.adempiere.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.compiere.util.CCache;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

/**
 * JPIERE-0363
 *
 * @author Hideaki Hagiwara
 *
 */
public class MContractLine extends X_JP_ContractLine {
	
	public MContractLine(Properties ctx, int JP_ContractLine_ID, String trxName) 
	{
		super(ctx, JP_ContractLine_ID, trxName);
	}
	
	public MContractLine(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}
	
	/**	Cache				*/
	private static CCache<Integer,MContractLine>	s_cache = new CCache<Integer,MContractLine>(Table_Name, 20);
	
	/**
	 * 	Get from Cache
	 *	@param ctx context
	 *	@param JP_ContractLine_ID id
	 *	@return Contract Calender
	 */
	public static MContractLine get (Properties ctx, int JP_ContractLine_ID)
	{
		Integer ii = new Integer (JP_ContractLine_ID);
		MContractLine retValue = (MContractLine)s_cache.get(ii);
		if (retValue != null)
			return retValue;
		retValue = new MContractLine (ctx, JP_ContractLine_ID, null);
		if (retValue.get_ID () != 0)
			s_cache.put (JP_ContractLine_ID, retValue);
		return retValue;
	}	//	get
	
	
	/** Parent					*/
	protected MContractContent			m_parent = null;

	public MContractContent getParent()
	{
		if (m_parent == null)
			m_parent = new MContractContent(getCtx(), getJP_ContractContent_ID(), get_TrxName());
		return m_parent;
	}	//	getParent
	
	
	@Override
	protected boolean beforeSave(boolean newRecord) 
	{
		//TODO 契約処理が開始されたら、契約カレンダーは変更できない旨のチェックロジックの実装
		//伝票が作成されたから契約カレンダーを変更されてしまうとデータに整合性がなくなｔってしいまう。
		if(!getParent().getJP_ContractProcStatus().equals(MContractContent.JP_CONTRACTPROCSTATUS_Unprocessed))
		{
			if(is_ValueChanged(MContractLine.COLUMNNAME_M_Product_ID) 
					|| is_ValueChanged(MContractLine.COLUMNNAME_QtyEntered)
					|| is_ValueChanged(MContractLine.COLUMNNAME_JP_DerivativeDocPolicy_InOut)
					|| is_ValueChanged(MContractLine.COLUMNNAME_JP_DerivativeDocPolicy_Inv)
					|| is_ValueChanged(MContractLine.COLUMNNAME_JP_ContractCalender_InOut_ID)
					|| is_ValueChanged(MContractLine.COLUMNNAME_JP_ContractCalender_Inv_ID)
					|| is_ValueChanged(MContractLine.COLUMNNAME_JP_ContractProcPeriod_InOut_ID)
					|| is_ValueChanged(MContractLine.COLUMNNAME_JP_ContractProcPeriod_Inv_ID)
					|| is_ValueChanged(MContractLine.COLUMNNAME_JP_ContractProcess_InOut_ID)
					|| is_ValueChanged(MContractLine.COLUMNNAME_JP_ContractProcess_Inv_ID)
					)
			{
				log.saveError("Error", "契約処理ステータスが未処理ではないため変更できません。");//TODO メッセージ化
				return false;
			}
		}
		
		//TODO -> 契約内容の派生伝票作成方針の変更をいつまで許可するか・・・。
		
		
		//Check Period Contract - Derivative Doc Policy
		if(getParent().getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
		{
			if(!newRecord && getParent().getJP_ContractProcStatus().equals(MContractContent.JP_CONTRACTPROCSTATUS_Unprocessed))
			{
				if(Util.isEmpty(getParent().getJP_CreateDerivativeDocPolicy()))
				{
					//Check JP_CreateDerivativeDocPolicy
					if(getParent().getOrderType().equals(MContractContent.ORDERTYPE_StandardOrder))
					{
						Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_CreateDerivativeDocPolicy")};
						String msg = Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs);
						log.saveError("Error",msg);
						return false;
					}
					
				}else{
				
					if(getParent().getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_Manual))
					{
						//Ship & Receipt
						setJP_DerivativeDocPolicy_InOut(null);
						setJP_ContractCalender_InOut_ID(0);
						setJP_ContractProcPeriod_InOut_ID(0);
						setJP_ContractProcess_InOut_ID(0);	
						
						//Invoice
						setJP_DerivativeDocPolicy_Inv(null);
						setJP_ContractCalender_Inv_ID(0);
						setJP_ContractProcPeriod_Inv_ID(0);
						setJP_ContractProcess_Inv_ID(0);
						
					}else if(getParent().getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceiptInvoice)){
						
						//Ship & Receipt
						if(Util.isEmpty(getJP_DerivativeDocPolicy_InOut()))
							{log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_DerivativeDocPolicy_InOut")}));return false;}
						if(getJP_ContractCalender_InOut_ID() == 0)
							{log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractCalender_InOut_ID")}));return false;}
						if(getJP_DerivativeDocPolicy_InOut().equals(MContractLine.JP_DERIVATIVEDOCPOLICY_INOUT_Lump))
						{
							if(getJP_ContractProcPeriod_InOut_ID() == 0)
								{log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractProcPeriod_InOut_ID")}));return false;}
						}else{
							setJP_ContractProcPeriod_InOut_ID(0);
						}					
						if(getJP_ContractProcess_InOut_ID() == 0)
							{log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractProcess_InOut_ID")}));return false;}
						
						//Invoice
						if(Util.isEmpty(getJP_DerivativeDocPolicy_Inv()))
							{log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_DerivativeDocPolicy_Inｖ")}));return false;}
						if(getJP_ContractCalender_Inv_ID() == 0)
							{log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractCalender_Inv_ID")}));return false;}		
						if(getJP_DerivativeDocPolicy_Inv().equals(MContractLine.JP_DERIVATIVEDOCPOLICY_INV_Lump))
						{
							if(getJP_ContractProcPeriod_Inv_ID() == 0)
								{log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractProcPeriod_Inv_ID")}));return false;}
						}else{
							setJP_ContractProcPeriod_Inv_ID(0);
						}
						if(getJP_ContractProcess_Inv_ID() == 0)
							{log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractProcess_Inv_ID")}));return false;}
						
						
					}else if(getParent().getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceipt)){
						
						//Ship & Receipt
						if(Util.isEmpty(getJP_DerivativeDocPolicy_InOut()))
							{log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_DerivativeDocPolicy_InOut")}));return false;}
						if(getJP_ContractCalender_InOut_ID() == 0)
							{log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractCalender_InOut_ID")}));return false;}
						if(getJP_DerivativeDocPolicy_InOut().equals(MContractLine.JP_DERIVATIVEDOCPOLICY_INOUT_Lump))
						{
							if(getJP_ContractProcPeriod_InOut_ID() == 0)
								{log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractProcPeriod_InOut_ID")}));return false;}
						}else{
							setJP_ContractProcPeriod_InOut_ID(0);
						}					
						if(getJP_ContractProcess_InOut_ID() == 0)
							{log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractProcess_InOut_ID")}));return false;}
						
						
						//Invoice
						setJP_DerivativeDocPolicy_Inv(null);
						setJP_ContractCalender_Inv_ID(0);
						setJP_ContractProcPeriod_Inv_ID(0);
						setJP_ContractProcess_Inv_ID(0);
						
					}else if(getParent().getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateInvoice)){
						
						//Ship & Receipt
						setJP_DerivativeDocPolicy_InOut(null);
						setJP_ContractCalender_InOut_ID(0);
						setJP_ContractProcPeriod_InOut_ID(0);
						setJP_ContractProcess_InOut_ID(0);	
						
						//Invoice
						if(Util.isEmpty(getJP_DerivativeDocPolicy_Inv()))
							{log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_DerivativeDocPolicy_Inｖ")}));return false;}
						if(getJP_ContractCalender_Inv_ID() == 0)
							{log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractCalender_Inv_ID")}));return false;}		
						if(getJP_DerivativeDocPolicy_Inv().equals(MContractLine.JP_DERIVATIVEDOCPOLICY_INV_Lump))
						{
							if(getJP_ContractProcPeriod_Inv_ID() == 0)
								{log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractProcPeriod_Inv_ID")}));return false;}
						}else{
							setJP_ContractProcPeriod_Inv_ID(0);
						}
						if(getJP_ContractProcess_Inv_ID() == 0)
							{log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractProcess_Inv_ID")}));return false;}
						
					}else{
						
						Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractDerivativeDocPolicy_ID")};
						String msg = Msg.getMsg(Env.getCtx(), "JP_InCaseOfPeriodContract") + Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs);
						log.saveError("Error",msg);
						return false;
	
					}//if(getParent().getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_Manual))
				
				}//if(Util.isEmpty(getParent().getJP_CreateDerivativeDocPolicy()))

			}//if(!newRecord && getParent().getJP_ContractProcStatus().equals(MContractContent.JP_CONTRACTPROCSTATUS_Unprocessed))
			
		}//if(getParent().getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
		
		//Check Spot Contract - Derivative Doc Policy
		if(getParent().getParent().getJP_ContractType().equals(MContractT.JP_CONTRACTTYPE_SpotContract))
		{
			//Ship & Receipt
			setJP_DerivativeDocPolicy_InOut(null);
			setJP_ContractCalender_InOut_ID(0);
			setJP_ContractProcPeriod_InOut_ID(0);
			setJP_ContractProcess_InOut_ID(0);	
			
			//Invoice
			setJP_DerivativeDocPolicy_Inv(null);
			setJP_ContractCalender_Inv_ID(0);
			setJP_ContractProcPeriod_Inv_ID(0);
			setJP_ContractProcess_Inv_ID(0);
		}		
		
		return true;
	}

	@Override
	protected boolean afterSave(boolean newRecord, boolean success) 
	{
		if (!success)
			return success;
		if (getParent().isProcessed())
			return success;
		
		if(newRecord || is_ValueChanged(MContractLine.COLUMNNAME_LineNetAmt))
		{
			String sql = "UPDATE JP_ContractContent cc"
					+ " SET TotalLines = "
					    + "(SELECT COALESCE(SUM(LineNetAmt),0) FROM JP_ContractLine cl WHERE cc.JP_ContractContent_ID=cl.JP_ContractContent_ID)"
					+ "WHERE JP_ContractContent_ID=?";
				int no = DB.executeUpdate(sql, new Object[]{new Integer(getJP_ContractContent_ID())}, false, get_TrxName(), 0);
				if (no != 1)
				{
					log.warning("(1) #" + no);
					return false;
				}
		}
		
		return success;
	}
	
	
}
