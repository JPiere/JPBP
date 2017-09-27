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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.model.MInOutLine;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MOrderLine;
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
		if(newRecord)
		{
			setC_BPartner_ID(getParent().getC_BPartner_ID());
			setC_BPartner_Location_ID(getParent().getC_BPartner_Location_ID());
		}
		
		
		//Check update.
		if(getParent().getParent().getJP_ContractT().equals(MContract.JP_CONTRACTTYPE_PeriodContract)
				&& !getParent().getJP_ContractProcStatus().equals(MContractContent.JP_CONTRACTPROCSTATUS_Unprocessed))
		{
			if(is_ValueChanged(MContractLine.COLUMNNAME_M_Product_ID) 
					|| is_ValueChanged(MContractLine.COLUMNNAME_C_Charge_ID)
					|| is_ValueChanged(MContractLine.COLUMNNAME_IsCreateDocLineJP)
					|| is_ValueChanged(MContractLine.COLUMNNAME_QtyEntered)
					|| is_ValueChanged(MContractLine.COLUMNNAME_C_UOM_ID)//IsUpdatable = 'N'
					|| is_ValueChanged(MContractLine.COLUMNNAME_QtyOrdered)
					|| is_ValueChanged(MContractLine.COLUMNNAME_MovementQty)
					|| is_ValueChanged(MContractLine.COLUMNNAME_QtyInvoiced)
					|| is_ValueChanged(MContractLine.COLUMNNAME_JP_DerivativeDocPolicy_InOut)
					|| is_ValueChanged(MContractLine.COLUMNNAME_JP_DerivativeDocPolicy_Inv)
					|| is_ValueChanged(MContractLine.COLUMNNAME_JP_ContractCalender_InOut_ID)
					|| is_ValueChanged(MContractLine.COLUMNNAME_JP_ContractCalender_Inv_ID)
					|| is_ValueChanged(MContractLine.COLUMNNAME_JP_ProcPeriod_Lump_InOut_ID)
					|| is_ValueChanged(MContractLine.COLUMNNAME_JP_ProcPeriod_Start_InOut_ID)
					|| is_ValueChanged(MContractLine.COLUMNNAME_JP_ProcPeriod_End_InOut_ID)
					|| is_ValueChanged(MContractLine.COLUMNNAME_JP_ProcPeriod_Lump_Inv_ID)
					|| is_ValueChanged(MContractLine.COLUMNNAME_JP_ProcPeriod_Start_Inv_ID)
					|| is_ValueChanged(MContractLine.COLUMNNAME_JP_ProcPeriod_End_Inv_ID)
					|| is_ValueChanged(MContractLine.COLUMNNAME_JP_ContractProcess_InOut_ID)
					|| is_ValueChanged(MContractLine.COLUMNNAME_JP_ContractProcess_Inv_ID)
					)
			{				
				StringBuilder msg = new StringBuilder(Msg.getMsg(getCtx(), "JP_ContractLineUpdate_PeriodContract"));
				if(is_ValueChanged(MContractLine.COLUMNNAME_M_Product_ID))
					msg = msg.append(" ").append(Msg.getElement(getCtx(), MContractLine.COLUMNNAME_M_Product_ID));
				else if(is_ValueChanged(MContractLine.COLUMNNAME_C_Charge_ID))
					msg = msg.append(" ").append(Msg.getElement(getCtx(), MContractLine.COLUMNNAME_C_Charge_ID));
				else if(is_ValueChanged(MContractLine.COLUMNNAME_IsCreateDocLineJP))
					msg = msg.append(" ").append(Msg.getElement(getCtx(), MContractLine.COLUMNNAME_IsCreateDocLineJP));
				else if(is_ValueChanged(MContractLine.COLUMNNAME_QtyEntered))
					msg = msg.append(" ").append(Msg.getElement(getCtx(), MContractLine.COLUMNNAME_QtyEntered));
				else if(is_ValueChanged(MContractLine.COLUMNNAME_C_UOM_ID))//IsUpdatable = 'N'
					msg = msg.append(" ").append(Msg.getElement(getCtx(), MContractLine.COLUMNNAME_C_UOM_ID));
				else if(is_ValueChanged(MContractLine.COLUMNNAME_QtyOrdered))
					msg = msg.append(" ").append(Msg.getElement(getCtx(), MContractLine.COLUMNNAME_QtyOrdered));
				else if(is_ValueChanged(MContractLine.COLUMNNAME_MovementQty))
					msg = msg.append(" ").append(Msg.getElement(getCtx(), MContractLine.COLUMNNAME_MovementQty));
				else if(is_ValueChanged(MContractLine.COLUMNNAME_QtyInvoiced))
					msg = msg.append(" ").append(Msg.getElement(getCtx(), MContractLine.COLUMNNAME_QtyInvoiced));
				else if(is_ValueChanged(MContractLine.COLUMNNAME_JP_DerivativeDocPolicy_InOut))
					msg = msg.append(" ").append(Msg.getElement(getCtx(), MContractLine.COLUMNNAME_JP_DerivativeDocPolicy_InOut));
				else if(is_ValueChanged(MContractLine.COLUMNNAME_JP_DerivativeDocPolicy_Inv))
					msg = msg.append(" ").append(Msg.getElement(getCtx(), MContractLine.COLUMNNAME_JP_DerivativeDocPolicy_Inv));
				else if(is_ValueChanged(MContractLine.COLUMNNAME_JP_ContractCalender_InOut_ID))
					msg = msg.append(" ").append(Msg.getElement(getCtx(), MContractLine.COLUMNNAME_JP_ContractCalender_InOut_ID));
				else if(is_ValueChanged(MContractLine.COLUMNNAME_JP_ContractCalender_Inv_ID))
					msg = msg.append(" ").append(Msg.getElement(getCtx(), MContractLine.COLUMNNAME_JP_ContractCalender_Inv_ID));
				else if(is_ValueChanged(MContractLine.COLUMNNAME_JP_ProcPeriod_Lump_InOut_ID))
					msg = msg.append(" ").append(Msg.getElement(getCtx(), MContractLine.COLUMNNAME_JP_ProcPeriod_Lump_InOut_ID));
				else if(is_ValueChanged(MContractLine.COLUMNNAME_JP_ProcPeriod_Start_InOut_ID))
					msg = msg.append(" ").append(Msg.getElement(getCtx(), MContractLine.COLUMNNAME_JP_ProcPeriod_Start_InOut_ID));
				else if(is_ValueChanged(MContractLine.COLUMNNAME_JP_ProcPeriod_End_InOut_ID))
					msg = msg.append(" ").append(Msg.getElement(getCtx(), MContractLine.COLUMNNAME_JP_ProcPeriod_End_InOut_ID));
				else if(is_ValueChanged(MContractLine.COLUMNNAME_JP_ProcPeriod_Lump_Inv_ID))
					msg = msg.append(" ").append(Msg.getElement(getCtx(), MContractLine.COLUMNNAME_JP_ProcPeriod_Lump_Inv_ID));
				else if(is_ValueChanged(MContractLine.COLUMNNAME_JP_ProcPeriod_Start_Inv_ID))
					msg = msg.append(" ").append(Msg.getElement(getCtx(), MContractLine.COLUMNNAME_JP_ProcPeriod_Start_Inv_ID));
				else if(is_ValueChanged(MContractLine.COLUMNNAME_JP_ProcPeriod_End_Inv_ID))
					msg = msg.append(" ").append(Msg.getElement(getCtx(), MContractLine.COLUMNNAME_JP_ProcPeriod_End_Inv_ID));
				else if( is_ValueChanged(MContractLine.COLUMNNAME_JP_ContractProcess_InOut_ID))
					msg = msg.append(" ").append(Msg.getElement(getCtx(), MContractLine.COLUMNNAME_JP_ContractProcess_InOut_ID));
				else if( is_ValueChanged(MContractLine.COLUMNNAME_JP_ContractProcess_Inv_ID))
					msg = msg.append(" ").append(Msg.getElement(getCtx(), MContractLine.COLUMNNAME_JP_ContractProcess_Inv_ID));
				
				log.saveError("Error", msg.toString());
				return false;
			}
			
		}		
		
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
					
				}else if( getParent().getDocBaseType().equals("SOO") || getParent().getDocBaseType().equals("POO") ){
				
					/** Policy of Create Derivative Doc is Manual */
					if(getParent().getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_Manual))
					{
						//Ship & Receipt
						setJP_DerivativeDocPolicy_InOut(null);
						setJP_ContractCalender_InOut_ID(0);
						setJP_ProcPeriod_Lump_InOut_ID(0);
						setJP_ProcPeriod_Start_InOut_ID(0);
						setJP_ProcPeriod_End_InOut_ID(0);
						setJP_ContractProcess_InOut_ID(0);	
						
						//Invoice
						setJP_DerivativeDocPolicy_Inv(null);
						setJP_ContractCalender_Inv_ID(0);
						setJP_ProcPeriod_Lump_Inv_ID(0);
						setJP_ProcPeriod_Start_Inv_ID(0);
						setJP_ProcPeriod_End_Inv_ID(0);
						setJP_ContractProcess_Inv_ID(0);
					
					
					/** Policy of Create Derivative Doc is Ship & Receipt & Invoice */
					}else if(getParent().getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceiptInvoice)){
						
						//Ship & Receipt
						if(Util.isEmpty(getJP_DerivativeDocPolicy_InOut()))
						{
							log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_DerivativeDocPolicy_InOut")}));
							return false;
						}
						
						if(getJP_ContractCalender_InOut_ID() == 0)
						{
							log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractCalender_InOut_ID")}));
							return false;
						}
						
						if(getJP_DerivativeDocPolicy_InOut().equals("LP")|| getJP_DerivativeDocPolicy_InOut().equals("PS")
								 || getJP_DerivativeDocPolicy_InOut().equals("PE") || getJP_DerivativeDocPolicy_InOut().equals("PB"))
						{
							if(getJP_DerivativeDocPolicy_InOut().equals("LP"))
							{
								if(getJP_ProcPeriod_Lump_InOut_ID() == 0)
								{
									log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ProcPeriod_Lump_InOut_ID")}));
									return false;
								}else{
									
									MContractProcPeriod period = MContractProcPeriod.get(getCtx(), getJP_ProcPeriod_Lump_InOut_ID());
									if(getParent().getJP_ContractProcDate_From().compareTo(period.getStartDate()) > 0
											|| (getParent().getJP_ContractProcDate_To() != null && getParent().getJP_ContractProcDate_To().compareTo(period.getEndDate()) < 0) )
									{
										//Outside the Contract Process Period
										log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_OutsideContractProcessPeriod") + " : " + Msg.getElement(getCtx(), "JP_ProcPeriod_Lump_InOut_ID"));
										return false;
									}
								}
								setJP_ProcPeriod_Start_InOut_ID(0);
								setJP_ProcPeriod_End_InOut_ID(0);
							}//LP
							
							if(getJP_DerivativeDocPolicy_InOut().equals("PS") || getJP_DerivativeDocPolicy_InOut().equals("PB"))
							{
								if(getJP_ProcPeriod_Start_InOut_ID() == 0)
								{
									log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ProcPeriod_Start_InOut_ID")}));
									return false;
								}else{
									
									MContractProcPeriod period = MContractProcPeriod.get(getCtx(), getJP_ProcPeriod_Start_InOut_ID());
									if(getParent().getJP_ContractProcDate_From().compareTo(period.getStartDate()) > 0
											|| (getParent().getJP_ContractProcDate_To() != null && getParent().getJP_ContractProcDate_To().compareTo(period.getEndDate()) < 0) )
									{
										//Outside the Contract Process Period
										log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_OutsideContractProcessPeriod") + " : " + Msg.getElement(getCtx(), "JP_ProcPeriod_Start_InOut_ID"));
										return false;
									}
								}
								setJP_ProcPeriod_Lump_InOut_ID(0);
								if(getJP_DerivativeDocPolicy_InOut().equals("PS"))
									setJP_ProcPeriod_End_InOut_ID(0);
							}//PS,PB	
							
							if(getJP_DerivativeDocPolicy_InOut().equals("PE") || getJP_DerivativeDocPolicy_InOut().equals("PB"))
							{
								if(getJP_ProcPeriod_End_InOut_ID() == 0)
								{
									log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ProcPeriod_End_InOut_ID")}));
									return false;
								}else{
									
									MContractProcPeriod period = MContractProcPeriod.get(getCtx(), getJP_ProcPeriod_End_InOut_ID());
									if(getParent().getJP_ContractProcDate_From().compareTo(period.getStartDate()) > 0
											|| (getParent().getJP_ContractProcDate_To() != null && getParent().getJP_ContractProcDate_To().compareTo(period.getEndDate()) < 0) )
									{
										//Outside the Contract Process Period
										log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_OutsideContractProcessPeriod") + " : " + Msg.getElement(getCtx(), "JP_ProcPeriod_End_InOut_ID"));
										return false;
									}
								}
								
								setJP_ProcPeriod_Lump_InOut_ID(0);
								
								if(getJP_DerivativeDocPolicy_InOut().equals("PE"))
								{
									setJP_ProcPeriod_Start_InOut_ID(0);
								}
								
								if(getJP_DerivativeDocPolicy_InOut().equals("PB"))
								{
									;//TODO StartとEndを比較して、日付の大小をチェックするロジックの実装
								}								
							}//PS,PB
							
						}else{
							setJP_ProcPeriod_Lump_InOut_ID(0);
							setJP_ProcPeriod_Start_InOut_ID(0);
							setJP_ProcPeriod_End_InOut_ID(0);
						}
						
						if(getJP_ContractProcess_InOut_ID() == 0)
						{
							log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractProcess_InOut_ID")}));
							return false;
						}
						
						
						//Invoice
						if(Util.isEmpty(getJP_DerivativeDocPolicy_Inv()))
						{
							log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_DerivativeDocPolicy_Inｖ")}));
							return false;
						}
						
						if(getJP_ContractCalender_Inv_ID() == 0)
						{
							log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractCalender_Inv_ID")}));
							return false;
						}
						
						if(getJP_DerivativeDocPolicy_Inv().equals("LP")|| getJP_DerivativeDocPolicy_Inv().equals("PS")
								 || getJP_DerivativeDocPolicy_Inv().equals("PE") || getJP_DerivativeDocPolicy_Inv().equals("PB"))
						{
							if(getJP_DerivativeDocPolicy_Inv().equals("LP"))
							{
								if(getJP_ProcPeriod_Lump_Inv_ID() == 0)
								{
									log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ProcPeriod_Lump_Inv_ID")}));
									return false;
								}else{
									
									MContractProcPeriod period = MContractProcPeriod.get(getCtx(), getJP_ProcPeriod_Lump_Inv_ID());
									if(getParent().getJP_ContractProcDate_From().compareTo(period.getStartDate()) > 0
											|| (getParent().getJP_ContractProcDate_To() != null && getParent().getJP_ContractProcDate_To().compareTo(period.getEndDate()) < 0) )
									{
										//Outside the Contract Process Period
										log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_OutsideContractProcessPeriod") + " : " + Msg.getElement(getCtx(), "JP_ProcPeriod_Lump_Inv_ID"));
										return false;
									}
								}
								setJP_ProcPeriod_Start_Inv_ID(0);
								setJP_ProcPeriod_End_Inv_ID(0);
							}//LP
							
							if(getJP_DerivativeDocPolicy_Inv().equals("PS") || getJP_DerivativeDocPolicy_Inv().equals("PB"))
							{
								if(getJP_ProcPeriod_Start_Inv_ID() == 0)
								{
									log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ProcPeriod_Start_Inv_ID")}));
									return false;
								}else{
									
									MContractProcPeriod period = MContractProcPeriod.get(getCtx(), getJP_ProcPeriod_Start_Inv_ID());
									if(getParent().getJP_ContractProcDate_From().compareTo(period.getStartDate()) > 0
											|| (getParent().getJP_ContractProcDate_To() != null && getParent().getJP_ContractProcDate_To().compareTo(period.getEndDate()) < 0) )
									{
										//Outside the Contract Process Period
										log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_OutsideContractProcessPeriod") + " : " + Msg.getElement(getCtx(), "JP_ProcPeriod_Start_Inv_ID"));
										return false;
									}
								}
								setJP_ProcPeriod_Lump_Inv_ID(0);
								if(getJP_DerivativeDocPolicy_Inv().equals("PS"))
									setJP_ProcPeriod_End_Inv_ID(0);
							}//PS,PB	
							
							if(getJP_DerivativeDocPolicy_Inv().equals("PE") || getJP_DerivativeDocPolicy_Inv().equals("PB"))
							{
								if(getJP_ProcPeriod_End_Inv_ID() == 0)
								{
									log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ProcPeriod_End_Inv_ID")}));
									return false;
								}else{
									
									MContractProcPeriod period = MContractProcPeriod.get(getCtx(), getJP_ProcPeriod_End_Inv_ID());
									if(getParent().getJP_ContractProcDate_From().compareTo(period.getStartDate()) > 0
											|| (getParent().getJP_ContractProcDate_To() != null && getParent().getJP_ContractProcDate_To().compareTo(period.getEndDate()) < 0) )
									{
										//Outside the Contract Process Period
										log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_OutsideContractProcessPeriod") + " : " + Msg.getElement(getCtx(), "JP_ProcPeriod_End_Inv_ID"));
										return false;
									}
								}
								
								setJP_ProcPeriod_Lump_Inv_ID(0);
								
								if(getJP_DerivativeDocPolicy_Inv().equals("PE"))
								{
									setJP_ProcPeriod_Start_Inv_ID(0);
								}
								
								if(getJP_DerivativeDocPolicy_Inv().equals("PB"))
								{
									;//TODO StartとEndを比較して、日付の大小をチェックするロジックの実装
								}	
							}//PS,PB
							
						}else{
							setJP_ProcPeriod_Lump_Inv_ID(0);
							setJP_ProcPeriod_Start_Inv_ID(0);
							setJP_ProcPeriod_End_Inv_ID(0);
						}
						
						
						if(getJP_ContractProcess_Inv_ID() == 0)
						{
							log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractProcess_Inv_ID")}));
							return false;
						}
						
						
					/** Policy of Create Derivative Doc is Ship & Receipt */
					}else if(getParent().getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceipt)){
						
						//Ship & Receipt
						if(Util.isEmpty(getJP_DerivativeDocPolicy_InOut()))
						{
							log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_DerivativeDocPolicy_InOut")}));
							return false;
						}
						
						if(getJP_ContractCalender_InOut_ID() == 0)
						{
							log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractCalender_InOut_ID")}));
							return false;
						}
						
						if(getJP_DerivativeDocPolicy_InOut().equals("LP")|| getJP_DerivativeDocPolicy_InOut().equals("PS")
								 || getJP_DerivativeDocPolicy_InOut().equals("PE") || getJP_DerivativeDocPolicy_InOut().equals("PB"))
						{
							if(getJP_DerivativeDocPolicy_InOut().equals("LP"))
							{
								if(getJP_ProcPeriod_Lump_InOut_ID() == 0)
								{
									log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ProcPeriod_Lump_InOut_ID")}));
									return false;
								}else{
									
									MContractProcPeriod period = MContractProcPeriod.get(getCtx(), getJP_ProcPeriod_Lump_InOut_ID());
									if(getParent().getJP_ContractProcDate_From().compareTo(period.getStartDate()) > 0
											|| (getParent().getJP_ContractProcDate_To() != null && getParent().getJP_ContractProcDate_To().compareTo(period.getEndDate()) < 0) )
									{
										//Outside the Contract Process Period
										log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_OutsideContractProcessPeriod") + " : " + Msg.getElement(getCtx(), "JP_ProcPeriod_Lump_InOut_ID"));
										return false;
									}
								}
								setJP_ProcPeriod_Start_InOut_ID(0);
								setJP_ProcPeriod_End_InOut_ID(0);
							}//LP
							
							if(getJP_DerivativeDocPolicy_InOut().equals("PS") || getJP_DerivativeDocPolicy_InOut().equals("PB"))
							{
								if(getJP_ProcPeriod_Start_InOut_ID() == 0)
								{
									log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ProcPeriod_Start_InOut_ID")}));
									return false;
								}else{
									
									MContractProcPeriod period = MContractProcPeriod.get(getCtx(), getJP_ProcPeriod_Start_InOut_ID());
									if(getParent().getJP_ContractProcDate_From().compareTo(period.getStartDate()) > 0
											|| (getParent().getJP_ContractProcDate_To() != null && getParent().getJP_ContractProcDate_To().compareTo(period.getEndDate()) < 0) )
									{
										//Outside the Contract Process Period
										log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_OutsideContractProcessPeriod") + " : " + Msg.getElement(getCtx(), "JP_ProcPeriod_Start_InOut_ID"));
										return false;
									}
								}
								setJP_ProcPeriod_Lump_InOut_ID(0);
								if(getJP_DerivativeDocPolicy_InOut().equals("PS"))
									setJP_ProcPeriod_End_InOut_ID(0);
							}//PS,PB	
							
							if(getJP_DerivativeDocPolicy_InOut().equals("PE") || getJP_DerivativeDocPolicy_InOut().equals("PB"))
							{
								if(getJP_ProcPeriod_End_InOut_ID() == 0)
								{
									log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ProcPeriod_End_InOut_ID")}));
									return false;
								}else{
									
									MContractProcPeriod period = MContractProcPeriod.get(getCtx(), getJP_ProcPeriod_End_InOut_ID());
									if(getParent().getJP_ContractProcDate_From().compareTo(period.getStartDate()) > 0
											|| (getParent().getJP_ContractProcDate_To() != null && getParent().getJP_ContractProcDate_To().compareTo(period.getEndDate()) < 0) )
									{
										//Outside the Contract Process Period
										log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_OutsideContractProcessPeriod") + " : " + Msg.getElement(getCtx(), "JP_ProcPeriod_End_InOut_ID"));
										return false;
									}
								}
								
								setJP_ProcPeriod_Lump_InOut_ID(0);
								
								if(getJP_DerivativeDocPolicy_InOut().equals("PE"))
								{
									setJP_ProcPeriod_Start_InOut_ID(0);
								}
								
								if(getJP_DerivativeDocPolicy_InOut().equals("PB"))
								{
									;//TODO StartとEndを比較して、日付の大小をチェックするロジックの実装
								}	
							}//PS,PB
							
						}else{
							setJP_ProcPeriod_Lump_InOut_ID(0);
							setJP_ProcPeriod_Start_InOut_ID(0);
							setJP_ProcPeriod_End_InOut_ID(0);
						}
						
						if(getJP_ContractProcess_InOut_ID() == 0)
						{
							log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractProcess_InOut_ID")}));
							return false;
						}
						
						
						//Invoice
						setJP_DerivativeDocPolicy_Inv(null);
						setJP_ContractCalender_Inv_ID(0);
						setJP_ProcPeriod_Lump_Inv_ID(0);
						setJP_ProcPeriod_Start_Inv_ID(0);
						setJP_ProcPeriod_End_Inv_ID(0);
						setJP_ContractProcess_Inv_ID(0);
						
						
					/** Policy of Create Derivative Doc is Invoice */
					}else if(getParent().getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateInvoice)){
						
						//Ship & Receipt
						setJP_DerivativeDocPolicy_InOut(null);
						setJP_ContractCalender_InOut_ID(0);
						setJP_ProcPeriod_Lump_InOut_ID(0);
						setJP_ProcPeriod_Start_InOut_ID(0);
						setJP_ProcPeriod_End_InOut_ID(0);
						setJP_ContractProcess_InOut_ID(0);	
						
						//Invoice
						if(Util.isEmpty(getJP_DerivativeDocPolicy_Inv()))
						{
							log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_DerivativeDocPolicy_Inｖ")}));
							return false;
						}
						
						if(getJP_ContractCalender_Inv_ID() == 0)
						{
							log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractCalender_Inv_ID")}));
							return false;
						}
						
						if(getJP_DerivativeDocPolicy_Inv().equals("LP")|| getJP_DerivativeDocPolicy_Inv().equals("PS")
								 || getJP_DerivativeDocPolicy_Inv().equals("PE") || getJP_DerivativeDocPolicy_Inv().equals("PB"))
						{
							if(getJP_DerivativeDocPolicy_Inv().equals("LP"))
							{
								if(getJP_ProcPeriod_Lump_Inv_ID() == 0)
								{
									log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ProcPeriod_Lump_Inv_ID")}));
									return false;
								}else{
									
									MContractProcPeriod period = MContractProcPeriod.get(getCtx(), getJP_ProcPeriod_Lump_Inv_ID());
									if(getParent().getJP_ContractProcDate_From().compareTo(period.getStartDate()) > 0
											|| (getParent().getJP_ContractProcDate_To() != null && getParent().getJP_ContractProcDate_To().compareTo(period.getEndDate()) < 0) )
									{
										//Outside the Contract Process Period
										log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_OutsideContractProcessPeriod") + " : " + Msg.getElement(getCtx(), "JP_ProcPeriod_Lump_Inv_ID"));
										return false;
									}
								}
								setJP_ProcPeriod_Start_Inv_ID(0);
								setJP_ProcPeriod_End_Inv_ID(0);
							}//LP
							
							if(getJP_DerivativeDocPolicy_Inv().equals("PS") || getJP_DerivativeDocPolicy_Inv().equals("PB"))
							{
								if(getJP_ProcPeriod_Start_Inv_ID() == 0)
								{
									log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ProcPeriod_Start_Inv_ID")}));
									return false;
								}else{
									
									MContractProcPeriod period = MContractProcPeriod.get(getCtx(), getJP_ProcPeriod_Start_Inv_ID());
									if(getParent().getJP_ContractProcDate_From().compareTo(period.getStartDate()) > 0
											|| (getParent().getJP_ContractProcDate_To() != null && getParent().getJP_ContractProcDate_To().compareTo(period.getEndDate()) < 0) )
									{
										//Outside the Contract Process Period
										log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_OutsideContractProcessPeriod") + " : " + Msg.getElement(getCtx(), "JP_ProcPeriod_Start_Inv_ID"));
										return false;
									}
								}
								setJP_ProcPeriod_Lump_Inv_ID(0);
								if(getJP_DerivativeDocPolicy_Inv().equals("PS"))
									setJP_ProcPeriod_End_Inv_ID(0);
							}//PS,PB	
							
							if(getJP_DerivativeDocPolicy_Inv().equals("PE") || getJP_DerivativeDocPolicy_Inv().equals("PB"))
							{
								if(getJP_ProcPeriod_End_Inv_ID() == 0)
								{
									log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ProcPeriod_End_Inv_ID")}));
									return false;
								}else{
									
									MContractProcPeriod period = MContractProcPeriod.get(getCtx(), getJP_ProcPeriod_End_Inv_ID());
									if(getParent().getJP_ContractProcDate_From().compareTo(period.getStartDate()) > 0
											|| (getParent().getJP_ContractProcDate_To() != null && getParent().getJP_ContractProcDate_To().compareTo(period.getEndDate()) < 0) )
									{
										//Outside the Contract Process Period
										log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_OutsideContractProcessPeriod") + " : " + Msg.getElement(getCtx(), "JP_ProcPeriod_End_Inv_ID"));
										return false;
									}
								}
								
								setJP_ProcPeriod_Lump_Inv_ID(0);
								
								if(getJP_DerivativeDocPolicy_Inv().equals("PE"))
								{
									setJP_ProcPeriod_Start_Inv_ID(0);
								}
								
								if(getJP_DerivativeDocPolicy_Inv().equals("PB"))
								{
									;//TODO StartとEndを比較して、日付の大小をチェックするロジックの実装
								}	
							}//PS,PB
							
						}else{
							setJP_ProcPeriod_Lump_Inv_ID(0);
							setJP_ProcPeriod_Start_Inv_ID(0);
							setJP_ProcPeriod_End_Inv_ID(0);
						}
						
						
						if(getJP_ContractProcess_Inv_ID() == 0)
						{
							log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractProcess_Inv_ID")}));
							return false;
						}
						
					}else{
						
						Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractDerivativeDocPolicy_ID")};
						String msg = Msg.getMsg(Env.getCtx(), "JP_InCaseOfPeriodContract") + Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs);
						log.saveError("Error",msg);
						return false;
	
					}//if(getParent().getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_Manual))
				
				}else{
					
					//Ship & Receipt
					setJP_DerivativeDocPolicy_InOut(null);
					setJP_ContractCalender_InOut_ID(0);
					setJP_ProcPeriod_Lump_InOut_ID(0);
					setJP_ProcPeriod_Start_InOut_ID(0);
					setJP_ProcPeriod_End_InOut_ID(0);
					setJP_ContractProcess_InOut_ID(0);	
					
					//Invoice
					setJP_DerivativeDocPolicy_Inv(null);
					setJP_ContractCalender_Inv_ID(0);
					setJP_ProcPeriod_Lump_Inv_ID(0);
					setJP_ProcPeriod_Start_Inv_ID(0);
					setJP_ProcPeriod_End_Inv_ID(0);
					setJP_ContractProcess_Inv_ID(0);
				}

			}//if(!newRecord && getParent().getJP_ContractProcStatus().equals(MContractContent.JP_CONTRACTPROCSTATUS_Unprocessed))
			
		}//if(getParent().getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract))
		
		//Check Spot Contract - Derivative Doc Policy
		if(getParent().getParent().getJP_ContractType().equals(MContractT.JP_CONTRACTTYPE_SpotContract))
		{
			//Ship & Receipt
			setJP_DerivativeDocPolicy_InOut(null);
			setJP_ContractCalender_InOut_ID(0);
			setJP_ProcPeriod_Lump_InOut_ID(0);
			setJP_ProcPeriod_Start_InOut_ID(0);
			setJP_ProcPeriod_End_InOut_ID(0);
			setJP_ContractProcess_InOut_ID(0);	
			
			//Invoice
			setJP_DerivativeDocPolicy_Inv(null);
			setJP_ContractCalender_Inv_ID(0);
			setJP_ProcPeriod_Lump_Inv_ID(0);
			setJP_ProcPeriod_Start_Inv_ID(0);
			setJP_ProcPeriod_End_Inv_ID(0);
			setJP_ContractProcess_Inv_ID(0);
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
	
	
	public MOrderLine[] getOrderLineByContractPeriod(Properties ctx, int JP_ContractProcPeriod_ID, String trxName)
	{
		ArrayList<MOrderLine> list = new ArrayList<MOrderLine>();
		final String sql = "SELECT ol.* FROM C_OrderLine ol  INNER JOIN  C_Order o ON(o.C_Order_ID = ol.C_Order_ID) "
					+ " WHERE ol.JP_ContractLine_ID=? AND ol.JP_ContractProcPeriod_ID=? AND o.DocStatus NOT IN ('VO','RE')";
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, trxName);
			pstmt.setInt(1, get_ID());
			pstmt.setInt(2, JP_ContractProcPeriod_ID);
			rs = pstmt.executeQuery();
			while(rs.next())
				list.add(new MOrderLine(getCtx(), rs, trxName));
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}
		
		MOrderLine[] iLines = new MOrderLine[list.size()];
		list.toArray(iLines);
		return iLines;
	}
	
	
	public MInOutLine[] getInOutLineByContractPeriod(Properties ctx, int JP_ContractProcPeriod_ID, String trxName)
	{
		ArrayList<MInOutLine> list = new ArrayList<MInOutLine>();
		final String sql = "SELECT iol.* FROM M_InOutLine iol  INNER JOIN  M_InOut io ON(io.M_InOut_ID = iol.M_InOut_ID) "
					+ " WHERE iol.JP_ContractLine_ID=? AND iol.JP_ContractProcPeriod_ID=? AND io.DocStatus NOT IN ('VO','RE')";
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, trxName);
			pstmt.setInt(1, get_ID());
			pstmt.setInt(2, JP_ContractProcPeriod_ID);
			rs = pstmt.executeQuery();
			while(rs.next())
				list.add(new MInOutLine(getCtx(), rs, trxName));
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}
		
		MInOutLine[] iLines = new MInOutLine[list.size()];
		list.toArray(iLines);
		return iLines;
	}
	
	public MInvoiceLine[] getInvoiceLineByContractPeriod(Properties ctx, int JP_ContractProcPeriod_ID, String trxName)
	{
		ArrayList<MInvoiceLine> list = new ArrayList<MInvoiceLine>();
		final String sql = "SELECT il.* FROM C_InvoiceLine il  INNER JOIN  C_Invoice i ON(i.C_Invoice_ID = il.C_Invoice_ID) "
					+ " WHERE il.JP_ContractLine_ID=? AND il.JP_ContractProcPeriod_ID=? AND i.DocStatus NOT IN ('VO','RE')";
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, trxName);
			pstmt.setInt(1, get_ID());
			pstmt.setInt(2, JP_ContractProcPeriod_ID);
			rs = pstmt.executeQuery();
			while(rs.next())
				list.add(new MInvoiceLine(getCtx(), rs, trxName));
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}
		
		MInvoiceLine[] iLines = new MInvoiceLine[list.size()];
		list.toArray(iLines);
		return iLines;
	}

	@Override
	public String toString() 
	{
	      StringBuffer sb = new StringBuffer ("JP_ContractLine_ID[")
	    	        .append(get_ID()).append("]");
	 	  return sb.toString();
	}
	
	
}
