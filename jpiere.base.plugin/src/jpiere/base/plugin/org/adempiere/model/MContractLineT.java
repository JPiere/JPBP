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
public class MContractLineT extends X_JP_ContractLineT {
	
	public MContractLineT(Properties ctx, int JP_ContractLineT_ID, String trxName)
	{
		super(ctx, JP_ContractLineT_ID, trxName);
	}
	
	public MContractLineT(Properties ctx, ResultSet rs, String trxName) 
	{
		super(ctx, rs, trxName);
	}
	
	/** Parent					*/
	protected MContractContentT			m_parent = null;

	public MContractContentT getParent()
	{
		if (m_parent == null)
			m_parent = new MContractContentT(getCtx(), getJP_ContractContentT_ID(), get_TrxName());
		return m_parent;
	}	//	getParent
	
	
	@Override
	protected boolean beforeSave(boolean newRecord) 
	{		
		//Check Period Contract - Derivative Doc Policy
		if(getParent().getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
		{
			if(newRecord
					|| is_ValueChanged(MContractLineT.COLUMNNAME_JP_BaseDocLinePolicy)
					|| is_ValueChanged(MContractLineT.COLUMNNAME_JP_ProcPeriodOffs_Lump)
					|| is_ValueChanged(MContractLineT.COLUMNNAME_JP_ProcPeriodOffs_Start)
					|| is_ValueChanged(MContractLineT.COLUMNNAME_JP_ProcPeriodOffs_End)
					|| is_ValueChanged(MContractLineT.COLUMNNAME_JP_DerivativeDocPolicy_InOut)
					|| is_ValueChanged(MContractLineT.COLUMNNAME_JP_ContractCalRef_InOut_ID)
					|| is_ValueChanged(MContractLineT.COLUMNNAME_JP_ContractProcRef_InOut_ID)
					|| is_ValueChanged(MContractLineT.COLUMNNAME_JP_ProcPeriodOffs_Lump_InOut)
					|| is_ValueChanged(MContractLineT.COLUMNNAME_JP_ProcPeriodOffs_Start_InOut)
					|| is_ValueChanged(MContractLineT.COLUMNNAME_JP_ProcPeriodOffs_End_InOut)
					|| is_ValueChanged(MContractLineT.COLUMNNAME_JP_DerivativeDocPolicy_Inv)
					|| is_ValueChanged(MContractLineT.COLUMNNAME_JP_ContractCalRef_Inv_ID)
					|| is_ValueChanged(MContractLineT.COLUMNNAME_JP_ContractProcRef_Inv_ID)
					|| is_ValueChanged(MContractLineT.COLUMNNAME_JP_ProcPeriodOffs_Lump_Inv)
					|| is_ValueChanged(MContractLineT.COLUMNNAME_JP_ProcPeriodOffs_Start_Inv)
					|| is_ValueChanged(MContractLineT.COLUMNNAME_JP_ProcPeriodOffs_End_Inv)
					)
			{
				if(!beforeSavePeriodContractCheck(newRecord))
					return false;
			}
			
		}//Period Contract
		
		//Check Spot Contract - Derivative Doc Policy
		if(getParent().getParent().getJP_ContractType().equals(MContractT.JP_CONTRACTTYPE_SpotContract))
		{
			//Base Doc Line
			setJP_BaseDocLinePolicy(null);
			setJP_ProcPeriodOffs_Lump(0);	
			setJP_ProcPeriodOffs_Start(0);
			setJP_ProcPeriodOffs_End(0);
			
			//Ship & Receipt
			setJP_DerivativeDocPolicy_InOut(null);
			setJP_ContractCalRef_InOut_ID(0);
			setJP_ContractProcRef_InOut_ID(0);
			setJP_ProcPeriodOffs_Lump_InOut(0);	
			setJP_ProcPeriodOffs_Start_InOut(0);
			setJP_ProcPeriodOffs_End_InOut(0);
			
			//Invoice
			setJP_DerivativeDocPolicy_Inv(null);
			setJP_ContractCalRef_Inv_ID(0);
			setJP_ContractProcRef_Inv_ID(0);
			setJP_ProcPeriodOffs_Lump_Inv(0);	
			setJP_ProcPeriodOffs_Start_Inv(0);
			setJP_ProcPeriodOffs_End_Inv(0);
		}
		
		
		//	Charge
		if (getC_Charge_ID() != 0)
		{
			if (getM_Product_ID() != 0)
				setM_Product_ID(0);
		}
	
		
		return true;
	}
	
	@Override
	protected boolean afterSave(boolean newRecord, boolean success) 
	{
		if (!success)
			return success;
//		if (getParent().isProcessed())
//			return success;
		
		if(newRecord || is_ValueChanged(MContractLineT.COLUMNNAME_LineNetAmt))
		{
			String sql = "UPDATE JP_ContractContentT cct"
					+ " SET TotalLines = "
					    + "(SELECT COALESCE(SUM(LineNetAmt),0) FROM JP_ContractLineT clt WHERE cct.JP_ContractContentT_ID=clt.JP_ContractContentT_ID)"
					+ "WHERE JP_ContractContenTt_ID=?";
				int no = DB.executeUpdate(sql, new Object[]{new Integer(getJP_ContractContentT_ID())}, false, get_TrxName(), 0);
				if (no != 1)
				{
					log.warning("(1) #" + no);
					return false;
				}
		}
		
		return success;
	}
	
	
	/**	Cache				*/
	private static CCache<Integer,MContractLineT>	s_cache = new CCache<Integer,MContractLineT>(Table_Name, 20);
	
	/**
	 * 	Get from Cache
	 *	@param ctx context
	 *	@param JP_ContractLineT_ID id
	 *	@return Contract Calender
	 */
	public static MContractLineT get (Properties ctx, int JP_ContractLineT_ID)
	{
		Integer ii = new Integer (JP_ContractLineT_ID);
		MContractLineT retValue = (MContractLineT)s_cache.get(ii);
		if (retValue != null)
			return retValue;
		retValue = new MContractLineT (ctx, JP_ContractLineT_ID, null);
		if (retValue.get_ID () != 0)
			s_cache.put (JP_ContractLineT_ID, retValue);
		return retValue;
	}	//	get
	
	private boolean beforeSavePeriodContractCheck(boolean newRecord)
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
				
			}else{//DocBaseType IN ('API','ARI')
				
				//Base Doc Line
				if(Util.isEmpty(getJP_BaseDocLinePolicy()))//Mandetory
				{
					log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_BaseDocLinePolicy")}));
					return false;
				}
				
				if(getJP_BaseDocLinePolicy().equals("LP"))
				{
					setJP_ProcPeriodOffs_Start(0);
					setJP_ProcPeriodOffs_End(0);
				}else if(getJP_BaseDocLinePolicy().equals("PS")){
					setJP_ProcPeriodOffs_Lump(0);	
					setJP_ProcPeriodOffs_End(0);
				}else if(getJP_BaseDocLinePolicy().equals("PE")){
					setJP_ProcPeriodOffs_Lump(0);
					setJP_ProcPeriodOffs_Start(0);
				}else if(getJP_BaseDocLinePolicy().equals("PB")){
					setJP_ProcPeriodOffs_Lump(0);
					if(getJP_ProcPeriodOffs_Start() > getJP_ProcPeriodOffs_End())
					{
						log.saveError("Error",Msg.getMsg(Env.getCtx(),"Invalid") + Msg.getElement(Env.getCtx(), "JP_ProcPeriodOffs_Start")+" > " +Msg.getElement(Env.getCtx(), "JP_ProcPeriodOffs_End"));
						return false;
					}
				}
				
				
				//Ship & Receipt
				setJP_DerivativeDocPolicy_InOut(null);
				setJP_ContractCalRef_InOut_ID(0);
				setJP_ContractProcRef_InOut_ID(0);
				setJP_ProcPeriodOffs_Lump_InOut(0);	
				setJP_ProcPeriodOffs_Start_InOut(0);
				setJP_ProcPeriodOffs_End_InOut(0);
				
				//Invoice
				setJP_DerivativeDocPolicy_Inv(null);
				setJP_ContractCalRef_Inv_ID(0);
				setJP_ContractProcRef_Inv_ID(0);
				setJP_ProcPeriodOffs_Lump_Inv(0);	
				setJP_ProcPeriodOffs_Start_Inv(0);
				setJP_ProcPeriodOffs_End_Inv(0);
				
			}
			
		//getParent().getJP_CreateDerivativeDocPolicy() is Not Null
		}else{
		
			if(getParent().getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_Manual))
			{
				//Base Doc Line
				if(Util.isEmpty(getJP_BaseDocLinePolicy()))//Mandetory
				{
					log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_BaseDocLinePolicy")}));
					return false;
				}
				
				if(getJP_BaseDocLinePolicy().equals("LP"))
				{
					setJP_ProcPeriodOffs_Start(0);
					setJP_ProcPeriodOffs_End(0);
				}else if(getJP_BaseDocLinePolicy().equals("PS")){
					setJP_ProcPeriodOffs_Lump(0);	
					setJP_ProcPeriodOffs_End(0);
				}else if(getJP_BaseDocLinePolicy().equals("PE")){
					setJP_ProcPeriodOffs_Lump(0);
					setJP_ProcPeriodOffs_Start(0);
				}else if(getJP_BaseDocLinePolicy().equals("PB")){
					setJP_ProcPeriodOffs_Lump(0);
					if(getJP_ProcPeriodOffs_Start() > getJP_ProcPeriodOffs_End())
					{
						log.saveError("Error",Msg.getMsg(Env.getCtx(),"Invalid") + Msg.getElement(Env.getCtx(), "JP_ProcPeriodOffs_Start")+" > " +Msg.getElement(Env.getCtx(), "JP_ProcPeriodOffs_End"));
						return false;
					}
				}
				
				
				//Ship & Receipt
				setJP_DerivativeDocPolicy_InOut(null);
				setJP_ContractCalRef_InOut_ID(0);
				setJP_ContractProcRef_InOut_ID(0);
				setJP_ProcPeriodOffs_Lump_InOut(0);	
				setJP_ProcPeriodOffs_Start_InOut(0);
				setJP_ProcPeriodOffs_End_InOut(0);
				
				//Invoice
				setJP_DerivativeDocPolicy_Inv(null);
				setJP_ContractCalRef_Inv_ID(0);
				setJP_ContractProcRef_Inv_ID(0);
				setJP_ProcPeriodOffs_Lump_Inv(0);	
				setJP_ProcPeriodOffs_Start_Inv(0);
				setJP_ProcPeriodOffs_End_Inv(0);
				
			}else if(getParent().getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceiptInvoice)){
				
				//Base Doc Line
				setJP_BaseDocLinePolicy(null);
				setJP_ProcPeriodOffs_Lump(0);	
				setJP_ProcPeriodOffs_Start(0);
				setJP_ProcPeriodOffs_End(0);
				
				//Ship & Receipt
				if(Util.isEmpty(getJP_DerivativeDocPolicy_InOut()))//Mandetory
				{
					log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_DerivativeDocPolicy_InOut")}));
					return false;
				}
				
				if(getJP_ContractCalRef_InOut_ID() == 0)//Mandetory
				{
					log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractCalRef_InOut_ID")}));
					return false;
				}
				
				if(getJP_DerivativeDocPolicy_InOut().equals("LP") || getJP_DerivativeDocPolicy_InOut().equals("PS")
						 || getJP_DerivativeDocPolicy_InOut().equals("PE") || getJP_DerivativeDocPolicy_InOut().equals("PB"))
				{
					if(getJP_ContractProcRef_InOut_ID() == 0)//Mandetory
					{
						log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractProcRef_InOut_ID")}));
						return false;
					}
					
					if(getJP_DerivativeDocPolicy_InOut().equals("LP"))
					{
						setJP_ProcPeriodOffs_Start_InOut(0);
						setJP_ProcPeriodOffs_End_InOut(0);
					}else if(getJP_DerivativeDocPolicy_InOut().equals("PS")){
						setJP_ProcPeriodOffs_Lump_InOut(0);	
						setJP_ProcPeriodOffs_End_InOut(0);
					}else if(getJP_DerivativeDocPolicy_InOut().equals("PE")){
						setJP_ProcPeriodOffs_Lump_InOut(0);
						setJP_ProcPeriodOffs_Start_InOut(0);
					}else if(getJP_DerivativeDocPolicy_InOut().equals("PB")){
						setJP_ProcPeriodOffs_Lump_InOut(0);
						if(getJP_ProcPeriodOffs_Start_InOut() > getJP_ProcPeriodOffs_End_InOut())
						{
							log.saveError("Error",Msg.getMsg(Env.getCtx(),"Invalid") + Msg.getElement(Env.getCtx(), "JP_ProcPeriodOffs_Start_InOut")+" > " +Msg.getElement(Env.getCtx(), "JP_ProcPeriodOffs_End_InOut"));
							return false;
						}
					}
					
				}else{
					setJP_ProcPeriodOffs_Lump_InOut(0);	
					setJP_ProcPeriodOffs_Start_InOut(0);
					setJP_ProcPeriodOffs_End_InOut(0);
				}
				
				
				
				//Invoice
				if(Util.isEmpty(getJP_DerivativeDocPolicy_Inv()))
				{
					log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_DerivativeDocPolicy_Inｖ")}));
					return false;
				}
				
				if(getJP_ContractCalRef_Inv_ID() == 0)
				{
					log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractCalRef_Inv_ID")}));
					return false;
				}
				
				if(getJP_DerivativeDocPolicy_Inv().equals("LP") || getJP_DerivativeDocPolicy_Inv().equals("PS")
						 || getJP_DerivativeDocPolicy_Inv().equals("PE") || getJP_DerivativeDocPolicy_Inv().equals("PB"))
				{
					if(getJP_ContractProcRef_Inv_ID() == 0)
					{
						log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractProcRef_Inv_ID")}));
						return false;
					}
					
					if(getJP_DerivativeDocPolicy_Inv().equals("LP"))
					{
						setJP_ProcPeriodOffs_Start_Inv(0);
						setJP_ProcPeriodOffs_End_Inv(0);
					}else if(getJP_DerivativeDocPolicy_Inv().equals("PS")){
						setJP_ProcPeriodOffs_Lump_Inv(0);	
						setJP_ProcPeriodOffs_End_Inv(0);
					}else if(getJP_DerivativeDocPolicy_Inv().equals("PE")){
						setJP_ProcPeriodOffs_Lump_Inv(0);
						setJP_ProcPeriodOffs_Start_Inv(0);
					}else if(getJP_DerivativeDocPolicy_Inv().equals("PB")){
						setJP_ProcPeriodOffs_Lump_Inv(0);
						if(getJP_ProcPeriodOffs_Start_Inv() > getJP_ProcPeriodOffs_End_Inv())
						{
							log.saveError("Error",Msg.getMsg(Env.getCtx(),"Invalid") + Msg.getElement(Env.getCtx(), "JP_ProcPeriodOffs_Start_Inv")+" > " +Msg.getElement(Env.getCtx(), "JP_ProcPeriodOffs_End_Inv"));
							return false;
						}
					}
					
				}else{
					setJP_ProcPeriodOffs_Lump_Inv(0);	
					setJP_ProcPeriodOffs_Start_Inv(0);
					setJP_ProcPeriodOffs_End_Inv(0);
				}
				
				
			}else if(getParent().getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceipt)){
				
				//Base Doc Line
				setJP_BaseDocLinePolicy(null);
				setJP_ProcPeriodOffs_Lump(0);	
				setJP_ProcPeriodOffs_Start(0);
				setJP_ProcPeriodOffs_End(0);
				
				//Ship & Receipt
				if(Util.isEmpty(getJP_DerivativeDocPolicy_InOut()))
				{
					log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_DerivativeDocPolicy_InOut")}));
					return false;
				}
				
				if(getJP_ContractCalRef_InOut_ID() == 0)
				{
					log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractCalRef_InOut_ID")}));
					return false;
				}
				
				if(getJP_DerivativeDocPolicy_InOut().equals("LP") || getJP_DerivativeDocPolicy_InOut().equals("PS")
						 || getJP_DerivativeDocPolicy_InOut().equals("PE") || getJP_DerivativeDocPolicy_InOut().equals("PB"))
				{
					if(getJP_ContractProcRef_InOut_ID() == 0)//Mandetory
					{
						log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractProcRef_InOut_ID")}));
						return false;
					}
					
					if(getJP_DerivativeDocPolicy_InOut().equals("LP"))
					{
						setJP_ProcPeriodOffs_Start_InOut(0);
						setJP_ProcPeriodOffs_End_InOut(0);
					}else if(getJP_DerivativeDocPolicy_InOut().equals("PS")){
						setJP_ProcPeriodOffs_Lump_InOut(0);	
						setJP_ProcPeriodOffs_End_InOut(0);
					}else if(getJP_DerivativeDocPolicy_InOut().equals("PE")){
						setJP_ProcPeriodOffs_Lump_InOut(0);
						setJP_ProcPeriodOffs_Start_InOut(0);
					}else if(getJP_DerivativeDocPolicy_InOut().equals("PB")){
						setJP_ProcPeriodOffs_Lump_InOut(0);
						if(getJP_ProcPeriodOffs_Start_InOut() > getJP_ProcPeriodOffs_End_InOut())
						{
							log.saveError("Error",Msg.getMsg(Env.getCtx(),"Invalid") + Msg.getElement(Env.getCtx(), "JP_ProcPeriodOffs_Start_InOut")+" > " +Msg.getElement(Env.getCtx(), "JP_ProcPeriodOffs_End_InOut"));
							return false;
						}
					}
					
				}else{
					setJP_ProcPeriodOffs_Lump_InOut(0);	
					setJP_ProcPeriodOffs_Start_InOut(0);
					setJP_ProcPeriodOffs_End_InOut(0);
				}
				
				//Invoice
				setJP_DerivativeDocPolicy_Inv(null);
				setJP_ContractCalRef_Inv_ID(0);
				setJP_ContractProcRef_Inv_ID(0);
				setJP_DerivativeDocPolicy_Inv(null);
				setJP_ContractCalRef_Inv_ID(0);
				setJP_ContractProcRef_Inv_ID(0);
				setJP_ProcPeriodOffs_Lump_Inv(0);	
				setJP_ProcPeriodOffs_Start_Inv(0);
				setJP_ProcPeriodOffs_End_Inv(0);
				
				
			}else if(getParent().getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateInvoice)){
				
				//Base Doc Line
				setJP_BaseDocLinePolicy(null);
				setJP_ProcPeriodOffs_Lump(0);	
				setJP_ProcPeriodOffs_Start(0);
				setJP_ProcPeriodOffs_End(0);
				
				//Ship & Receipt
				setJP_DerivativeDocPolicy_InOut(null);
				setJP_ContractCalRef_InOut_ID(0);
				setJP_ContractProcRef_InOut_ID(0);
				setJP_ProcPeriodOffs_Lump_InOut(0);	
				setJP_ProcPeriodOffs_Start_InOut(0);
				setJP_ProcPeriodOffs_End_InOut(0);
				
				//Invoice
				if(Util.isEmpty(getJP_DerivativeDocPolicy_Inv()))
				{
					log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_DerivativeDocPolicy_Inｖ")}));
					return false;
				}
				
				if(getJP_ContractCalRef_Inv_ID() == 0)
				{
					log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractCalRef_Inv_ID")}));
					return false;
				}		
				
				if(getJP_DerivativeDocPolicy_Inv().equals("LP") || getJP_DerivativeDocPolicy_Inv().equals("PS")
						 || getJP_DerivativeDocPolicy_Inv().equals("PE") || getJP_DerivativeDocPolicy_Inv().equals("PB"))
				{
					if(getJP_ContractProcRef_Inv_ID() == 0)
					{
						log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractProcRef_Inv_ID")}));
						return false;
					}
					
					if(getJP_DerivativeDocPolicy_Inv().equals("LP"))
					{
						setJP_ProcPeriodOffs_Start_Inv(0);
						setJP_ProcPeriodOffs_End_Inv(0);
					}else if(getJP_DerivativeDocPolicy_Inv().equals("PS")){
						setJP_ProcPeriodOffs_Lump_Inv(0);	
						setJP_ProcPeriodOffs_End_Inv(0);
					}else if(getJP_DerivativeDocPolicy_Inv().equals("PE")){
						setJP_ProcPeriodOffs_Lump_Inv(0);
						setJP_ProcPeriodOffs_Start_Inv(0);
					}else if(getJP_DerivativeDocPolicy_Inv().equals("PB")){
						setJP_ProcPeriodOffs_Lump_Inv(0);
						if(getJP_ProcPeriodOffs_Start_Inv() > getJP_ProcPeriodOffs_End_Inv())
						{
							log.saveError("Error",Msg.getMsg(Env.getCtx(),"Invalid") + Msg.getElement(Env.getCtx(), "JP_ProcPeriodOffs_Start_Inv")+" > " +Msg.getElement(Env.getCtx(), "JP_ProcPeriodOffs_End_Inv"));
							return false;
						}
					}
					
				}else{
					setJP_ProcPeriodOffs_Lump_Inv(0);	
					setJP_ProcPeriodOffs_Start_Inv(0);
					setJP_ProcPeriodOffs_End_Inv(0);
				}
				
			}else{
				
				Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractDerivativeDocPolicy_ID")};
				String msg = Msg.getMsg(Env.getCtx(), "JP_InCaseOfPeriodContract") + Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs);
				log.saveError("Error",msg);
				return false;

			}//if(getParent().getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_Manual))
		
		}//if(Util.isEmpty(getParent().getJP_CreateDerivativeDocPolicy()))
		
		return true;
	}
}
