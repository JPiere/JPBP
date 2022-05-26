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
import java.util.List;
import java.util.Properties;

import org.compiere.model.MCurrency;
import org.compiere.model.MDocType;
import org.compiere.model.MPriceList;
import org.compiere.model.Query;
import org.compiere.util.CCache;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

/**
 * JPIERE-0363
 *
 * @author Hideaki Hagiwara
 *
 */
public class MContractContentT extends X_JP_ContractContentT {

	public MContractContentT(Properties ctx, int JP_ContractContentT_ID, String trxName)
	{
		super(ctx, JP_ContractContentT_ID, trxName);
	}

	public MContractContentT(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}

	@Override
	protected boolean beforeSave(boolean newRecord)
	{
		//Synchronize Contract Type with Parent.
		if((newRecord && getJP_ContractT_ID() > 0) || (getJP_ContractT_ID() > 0 && is_ValueChanged(MContractContentT.COLUMNNAME_JP_ContractType)))
		{
			setJP_ContractType(getParent().getJP_ContractType());
		}

		//Mandatory Check of Contract Type
		if(Util.isEmpty(getJP_ContractType()))
		{
			Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractType")};
			log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs));
			return false;
		}

		if(newRecord)
		{
			//Check - General Contract can not have Contract Content
			if(getJP_ContractType().equals(MContractT.JP_CONTRACTTYPE_GeneralContract))
			{
				log.saveError("Error", Msg.getMsg(getCtx(), "JP_GeneralContractContent"));
				return false;
			}

			//Check - Template of Spot Contract can have only one Contract Content template.
			if(getParent() != null && getJP_ContractType().equals(MContractT.JP_CONTRACTTYPE_SpotContract)
					&& getParent().getContractContentTemplates(true, null).length > 0 )
			{
				log.saveError("Error", Msg.getMsg(getCtx(), "JP_SpotContractContentTemplate"));
				return false;
			}
		}


		if(!newRecord
				&&( is_ValueChanged(MContractContentT.COLUMNNAME_DocBaseType)
				|| is_ValueChanged(MContractContentT.COLUMNNAME_JP_BaseDocDocType_ID)
				|| (is_ValueChanged(MContractContentT.COLUMNNAME_JP_CreateDerivativeDocPolicy) && getJP_ContractType().equals(MContractT.JP_CONTRACTTYPE_PeriodContract))
				|| (is_ValueChanged(MContractContentT.COLUMNNAME_JP_ContractCalenderRef_ID) && getJP_ContractType().equals(MContractT.JP_CONTRACTTYPE_PeriodContract))
				|| (is_ValueChanged(MContractContentT.COLUMNNAME_JP_ContractProcessRef_ID) && getJP_ContractType().equals(MContractT.JP_CONTRACTTYPE_PeriodContract))  ))
		{
			MContractLineT[] lines = getContractLineTemplates(true,"");
			if(lines.length > 0)
			{
				//You can not update this field Because Doc Line is registered.
				StringBuilder msg = new StringBuilder(Msg.getMsg(getCtx(), "JP_NotUpdateForLine"));
				if(is_ValueChanged(MContractContent.COLUMNNAME_DocBaseType))
					msg.append(" : ").append(Msg.getElement(getCtx(), MContractContentT.COLUMNNAME_DocBaseType));
				else if(is_ValueChanged(MContractContent.COLUMNNAME_JP_BaseDocDocType_ID))
					msg.append(" : ").append(Msg.getElement(getCtx(), MContractContentT.COLUMNNAME_JP_BaseDocDocType_ID));
				else if(is_ValueChanged(MContractContent.COLUMNNAME_JP_CreateDerivativeDocPolicy))
					msg.append(" : ").append(Msg.getElement(getCtx(), MContractContentT.COLUMNNAME_JP_CreateDerivativeDocPolicy));
				else if(is_ValueChanged(MContractContentT.COLUMNNAME_JP_ContractCalenderRef_ID))
					msg.append(" : ").append(Msg.getElement(getCtx(), MContractContentT.COLUMNNAME_JP_ContractCalenderRef_ID));
				else if(is_ValueChanged(MContractContentT.COLUMNNAME_JP_ContractProcessRef_ID))
					msg.append(" : ").append(Msg.getElement(getCtx(), MContractContentT.COLUMNNAME_JP_ContractProcessRef_ID));

				log.saveError("Error", msg.toString());
				return false;
			}
		}


		//Check JP_BaseDocDocType_ID and DocBaseType
		if(newRecord || is_ValueChanged(MContractContentT.COLUMNNAME_JP_BaseDocDocType_ID)
				|| is_ValueChanged(MContractContentT.COLUMNNAME_DocBaseType)
				|| is_ValueChanged(MContractContent.COLUMNNAME_JP_CreateDerivativeDocPolicy))
		{
			MDocType docType = MDocType.get(getCtx(), getJP_BaseDocDocType_ID());
			setIsSOTrx(docType.isSOTrx());

			if(!getDocBaseType().equals(docType.getDocBaseType()))
			{
				log.saveError("Error", Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), MContractContentT.COLUMNNAME_JP_BaseDocDocType_ID));
				return false;
			}else{

				if(getDocBaseType().equals("POO") || getDocBaseType().equals("SOO") )
				{
					setOrderType(docType.getDocSubTypeSO());

					if(getJP_CreateDerivativeDocPolicy() != null
							&& !getJP_CreateDerivativeDocPolicy().equals(MContractContentT.JP_CREATEDERIVATIVEDOCPOLICY_Manual))
					{
						if(!docType.getDocSubTypeSO().equals(MDocType.DOCSUBTYPESO_StandardOrder)
								&& !docType.getDocSubTypeSO().equals(MDocType.DOCSUBTYPESO_Quotation)
								&& !docType.getDocSubTypeSO().equals(MDocType.DOCSUBTYPESO_Proposal) )
						{
							//Base doc DocType that you selected is available When Create Derivative Doc policy is Manual.
							log.saveError("Error", Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), MContractContentT.COLUMNNAME_JP_BaseDocDocType_ID)
													+ "  :  " + Msg.getMsg(getCtx(), "JP_BaseDocDocType_CreateDerivativeDocPolicy"));
							return false;
						}
					}

					if(getJP_CreateDerivativeDocPolicy() != null
							&& (getJP_CreateDerivativeDocPolicy().equals(MContractContentT.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceipt)
							|| getJP_CreateDerivativeDocPolicy().equals(MContractContentT.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceiptInvoice)) )
					{
						if(docType.getC_DocTypeShipment_ID() == 0)
						{
							String msg1 = Msg.getMsg(getCtx(), "JP_ToBeConfirmed") + " - " + Msg.getElement(getCtx(),"JP_BaseDocDocType_ID") ;
							String msg2 = Msg.getMsg(getCtx(), "JP_Null") + " - " + Msg.getElement(getCtx(),"C_DocTypeShipment_ID") ;
							log.saveError("Error", msg1 + " : " + msg2);
							return false;
						}
					}

					if(getJP_CreateDerivativeDocPolicy() != null
							&& (getJP_CreateDerivativeDocPolicy().equals(MContractContentT.JP_CREATEDERIVATIVEDOCPOLICY_CreateInvoice)
							|| getJP_CreateDerivativeDocPolicy().equals(MContractContentT.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceiptInvoice)) )
					{
						if(docType.getC_DocTypeInvoice_ID() == 0)
						{
							String msg1 = Msg.getMsg(getCtx(), "JP_ToBeConfirmed") + " - " + Msg.getElement(getCtx(),"JP_BaseDocDocType_ID") ;
							String msg2 = Msg.getMsg(getCtx(), "JP_Null") + " - " + Msg.getElement(getCtx(),"C_DocTypeInvoice_ID") ;
							log.saveError("Error", Msg.getMsg(getCtx(), "JP_Null") + msg1 + " : " + msg2);
							return false;
						}
					}

				}else{
					setOrderType("--");
				}

			}
		}

		//Check JP_CreateDerivativeDocPolicy
		if(newRecord || is_ValueChanged(MContractContentT.COLUMNNAME_JP_CreateDerivativeDocPolicy))
		{
			if(getJP_ContractType().equals(MContractT.JP_CONTRACTTYPE_PeriodContract) && getOrderType().equals(MContractContentT.ORDERTYPE_StandardOrder))
			{
				if(Util.isEmpty(getJP_CreateDerivativeDocPolicy()))
				{
					Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_CreateDerivativeDocPolicy")};
					String msg = Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs);
					log.saveError("Error",msg);
					return false;
				}

				if(getDocBaseType().equals("POO") || getDocBaseType().equals("SOO") )
				{
					if(!getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_Manual))
					{
						if(!getOrderType().equals(MDocType.DOCSUBTYPESO_StandardOrder)
								&& !getOrderType().equals(MDocType.DOCSUBTYPESO_Quotation)
								&& !getOrderType().equals(MDocType.DOCSUBTYPESO_Proposal) )
						{
							//Base doc DocType that you selected is available When Create Derivative Doc policy is Manual.
							log.saveError("Error", Msg.getMsg(getCtx(), "Invalid") + Msg.getElement(getCtx(), MContractContentT.JP_CREATEDERIVATIVEDOCPOLICY_Manual)
													+ "  :  " + Msg.getMsg(getCtx(), "JP_BaseDocDocType_CreateDerivativeDocPolicy"));
							return false;
						}
					}

					if(getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateShipReceiptInvoice)
							||getJP_CreateDerivativeDocPolicy().equals(MContractContent.JP_CREATEDERIVATIVEDOCPOLICY_CreateInvoice) )
					{
						int C_DocTypeShipment_ID = getJP_BaseDocDocType().getC_DocTypeShipment_ID();
						if(C_DocTypeShipment_ID > 0)
						{
							MDocType io_DocType = MDocType.get(getCtx(), C_DocTypeShipment_ID);
							if(io_DocType.get_ValueAsBoolean("IsCreateInvoiceJP"))
							{
								//Document Type for Shipment of Base Doc DocType is to create Invoice.
								String msg1 = Msg.getMsg(getCtx(), "JP_DocTypeForShipmentOfBaseDocDocType");
								String msg2 = Msg.getMsg(getCtx(),"JP_Inconsistency",new Object[]{Msg.getElement(Env.getCtx(), "JP_BaseDocDocType_ID"),Msg.getElement(Env.getCtx(), "JP_CreateDerivativeDocPolicy")});
								log.saveError("Error", msg1 +" : " + msg2);
								return false;
							}
						}
					}
				}

			}else{

				setJP_CreateDerivativeDocPolicy(null);

			}

		}

		//Check JP_ContractCalenderRef_ID
		if(newRecord || is_ValueChanged(MContractContentT.COLUMNNAME_JP_ContractCalenderRef_ID))
		{
			if(getJP_ContractType().equals(MContractT.JP_CONTRACTTYPE_PeriodContract))
			{
				if(getJP_ContractCalenderRef_ID() == 0)
				{
					Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractCalenderRef_ID")};
					String msg = Msg.getMsg(Env.getCtx(), "JP_InCaseOfPeriodContract") + Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs);
					log.saveError("Error",msg);
					return false;
				}
			}else{

				setJP_ContractCalenderRef_ID(0);

			}
		}

		//Check JP_ContractProcessRef_ID
		if(newRecord || is_ValueChanged(MContractContentT.COLUMNNAME_JP_ContractProcessRef_ID))
		{
			if(getJP_ContractType().equals(MContractT.JP_CONTRACTTYPE_PeriodContract))
			{
				if(getJP_ContractProcessRef_ID() == 0)
				{
					Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractProcessRef_ID")};
					String msg = Msg.getMsg(Env.getCtx(), "JP_InCaseOfPeriodContract") + Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs);
					log.saveError("Error",msg);
					return false;
				}
			}else {

				setJP_ContractProcessRef_ID(0);

			}
		}


		//Check Contract Acct
		if(newRecord || is_ValueChanged("DocBaseType") || is_ValueChanged("JP_CreateDerivativeDocPolicy") || is_ValueChanged("JP_Contract_Acct_ID") )
		{
			int JP_Contract_Acct_ID = getJP_Contract_Acct_ID();
			if(JP_Contract_Acct_ID > 0)
			{
				MContractAcct acctInfo = MContractAcct.get(getCtx(), JP_Contract_Acct_ID);

				//Check - in case of Crate Invoice From Recognition
				if(acctInfo.isPostingContractAcctJP() && acctInfo.isPostingRecognitionDocJP() && acctInfo.getJP_RecogToInvoicePolicy() != null
						&& !acctInfo.getJP_RecogToInvoicePolicy().equals("NO"))
				{
					if(!getDocBaseType().equals("SOO") && !getDocBaseType().equals("POO"))
					{
						//In case of create Invoice from Recognition at Contract Account Info, you must select SOO or POO at Base Doc Type.
						log.saveError("Error", Msg.getMsg(getCtx(), "JP_RecogToInvoice_SOOorPOO"));
						return false;
					}

					if(!getJP_BaseDocDocType().getDocSubTypeSO().equals("SO") && !getJP_BaseDocDocType().getDocSubTypeSO().equals("WP"))
					{
						//In case of create Invoice from Recognition at Contract Account Info, you must select Base Doc Doc Type that SO Sub Type is SO or WP.
						log.saveError("Error", Msg.getMsg(getCtx(), "JP_RecogToInvoice_SOorWP"));
						return false;
					}

					if(getJP_BaseDocDocType().getC_DocTypeShipment_ID() > 0)
					{
						MDocType shipDoc = MDocType.get(getCtx(), getJP_BaseDocDocType().getC_DocTypeShipment_ID());
						if(shipDoc.get_ValueAsBoolean("IsCreateInvoiceJP"))
						{
							String msg1 = Msg.getMsg(getCtx(),"JP_ToBeConfirmed") + " - " + Msg.getElement(Env.getCtx(), "C_DocTypeShipment_ID");
							String msg2 = Msg.getMsg(getCtx(),"JP_Inconsistency",new Object[]{Msg.getElement(Env.getCtx(), "JP_RecogToInvoicePolicy"),Msg.getElement(Env.getCtx(), "IsCreateInvoiceJP")});
							log.saveError("Error", msg1 + " : " + msg2);
							return false;
						}
					}

					if(getParent() != null && getParent().getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract) && getJP_CreateDerivativeDocPolicy() != null
						&& !getJP_CreateDerivativeDocPolicy().equals("MA") &&  !getJP_CreateDerivativeDocPolicy().equals("IO"))
					{
						//In case of create Invoice from Recognition at Contract Account Info, you must select Manual or Create Ship/Recipt at Create Derivative Doc Policy.
						log.saveError("Error", Msg.getMsg(getCtx(), "JP_RecogToInvoice_MAorIO"));
						return false;
					}
				}
			}//if(JP_Contract_Acct_ID > 0)
		}//Check Contract Acct


		//Check Contract Process Method
		if(getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract)
				&& ( newRecord || (is_ValueChanged(MContractContentT.COLUMNNAME_JP_ContractProcessMethod)
							|| is_ValueChanged(MContractContentT.COLUMNNAME_C_DocType_ID))) )
		{
			String JP_ContractProcessMethod = getJP_ContractProcessMethod();
			if(JP_ContractProcessMethod == null)
			{
				Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractProcessMethod")};
				log.saveError("Error",Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs));
				return false ;
			}

			//Check Doc Type
			if(JP_ContractProcessMethod.equals(MContractContentT.JP_CONTRACTPROCESSMETHOD_IndirectContractProcess))
			{
				MDocType contractPSDocType = MDocType.get(getCtx(), getC_DocType_ID());
				Object  obj_ContractPSDocType_ID = contractPSDocType.get_Value("JP_ContractPSDocType_ID");

				if(obj_ContractPSDocType_ID == null)
				{
					String msg0 = Msg.getElement(getCtx(), "C_DocType_ID");
					String msg1 = Msg.getMsg(getCtx(), "JP_InCaseOfIndirectContractProcess");//In case of Indirect Contract Process,
					Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractPSDocType_ID")};
					String msg2 = Msg.getMsg(Env.getCtx(),"JP_Mandatory",objs);
					log.saveError("Error",msg0 + ":" + msg1 + msg2);
					return false ;
				}
			}
		}

		if(!getJP_ContractType().equals(MContract.JP_CONTRACTTYPE_PeriodContract) && ( newRecord || is_ValueChanged(MContractContentT.COLUMNNAME_JP_ContractProcessMethod)) )
		{
			setJP_ContractProcessMethod(null);
		}


		//JPIERE-0435 Check Extend Contract Period and Renew Contract
		if(newRecord || is_ValueChanged(MContractContentT.COLUMNNAME_IsAutomaticUpdateJP))
		{
			if(getParent() != null && !getParent().isAutomaticUpdateJP() && isAutomaticUpdateJP() && getJP_ContractType().equals(MContractT.JP_CONTRACTTYPE_PeriodContract))
			{
				//You can not tick Automatic Update, Because Contract document template is not Automatic Update.
				log.saveError("Error",Msg.getMsg(getCtx(), "JP_CheckIsAutomaticUpdateJP"));
				return false ;

			}

			if(isAutomaticUpdateJP() && getJP_ContractType().equals(MContractT.JP_CONTRACTTYPE_PeriodContract))
			{

				if(Util.isEmpty(getJP_ContractC_AutoUpdatePolicy()))
				{
					Object[] objs = new Object[]{Msg.getElement(Env.getCtx(), "JP_ContractC_AutoUpdatePolicy")};
					log.saveError("Error",Msg.getMsg(getCtx(), "JP_Mandatory",objs));
					return false ;
				}

				if(getJP_ContractProcessMethod().equals(MContractContentT.JP_CONTRACTPROCESSMETHOD_IndirectContractProcess))
				{
					if(getJP_ContractC_AutoUpdatePolicy().equals(MContractContentT.JP_CONTRACTC_AUTOUPDATEPOLICY_ExtendContractProcessDate))
					{
						//You can not select "Extend Contract Process Date" of Auto update policy in case of Indirect Contract Process.
						log.saveError("Error",Msg.getMsg(getCtx(), "JP_CanNotSelect_ExtendContractProcessDate"));
						return false ;
					}

				}

			}else {

				setIsAutomaticUpdateJP(false);
				setJP_ContractC_AutoUpdatePolicy(null);

			}
		}


		//Check Price List and IsTaxIncluded
		if(newRecord || is_ValueChanged(MContractContentT.COLUMNNAME_M_PriceList_ID))
		{
			if(getM_PriceList_ID() > 0)
			{
				setM_PriceList_ID(getM_PriceList_ID());				
			}else{
				setIsTaxIncluded(false);
			}
		}

		//Check OrderType
		if(newRecord || is_ValueChanged(MContractContentT.COLUMNNAME_OrderType))
		{
			MDocType docType = MDocType.get(getCtx(), getJP_BaseDocDocType_ID());
			setIsSOTrx(docType.isSOTrx());
			if(docType.getDocBaseType().equals(MDocType.DOCBASETYPE_SalesOrder)
						|| docType.getDocBaseType().equals(MDocType.DOCBASETYPE_PurchaseOrder))
			{

					setOrderType (docType.getDocSubTypeSO());
			}else{
					setOrderType(MContractContentT.ORDERTYPE_Other);
			}
		}

		return true;
	}

	private MContractT parent = null;

	public MContractT getParent()
	{
		if(parent == null && getJP_ContractT_ID() > 0)
		{
			parent = new MContractT(getCtx(), getJP_ContractT_ID(), null);
		}

		return parent;
	}


	private MContractLineT[] m_ContractLineTemplates = null;

	public MContractLineT[] getContractLineTemplates (String whereClause, String orderClause)
	{
		StringBuilder whereClauseFinal = new StringBuilder(MContractLineT.COLUMNNAME_JP_ContractContentT_ID+"=? ");
		if (!Util.isEmpty(whereClause, true))
			whereClauseFinal.append(whereClause);
		if (orderClause.length() == 0)
			orderClause = MContractLineT.COLUMNNAME_Line;
		//
		List<MContractLineT> list = new Query(getCtx(), MContractLineT.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(get_ID())
										.setOrderBy(orderClause)
										.list();

		return list.toArray(new MContractLineT[list.size()]);
	}

	public MContractLineT[] getContractLineTemplates (boolean requery, String orderBy)
	{
		if (m_ContractLineTemplates != null && !requery) {
			set_TrxName(m_ContractLineTemplates, get_TrxName());
			return m_ContractLineTemplates;
		}
		//
		String orderClause = "";
		if (orderBy != null && orderBy.length() > 0)
			orderClause += orderBy;
		else
			orderClause += "Line";
		m_ContractLineTemplates = getContractLineTemplates(null, orderClause);
		return m_ContractLineTemplates;
	}


	public MContractLineT[] getContractLineTemplates()
	{
		return getContractLineTemplates(false, null);
	}

	/**	Cache				*/
	private static CCache<Integer,MContractContentT>	s_cache = new CCache<Integer,MContractContentT>(Table_Name, 20);

	/**
	 * 	Get from Cache
	 *	@param ctx context
	 *	@param JP_ContractContent_ID id
	 *	@return Contract Calender
	 */
	public static MContractContentT get (Properties ctx, int JP_ContractContentT_ID)
	{
		Integer ii = Integer.valueOf(JP_ContractContentT_ID);
		MContractContentT retValue = (MContractContentT)s_cache.get(ii);
		if (retValue != null)
			return retValue;
		retValue = new MContractContentT (ctx, JP_ContractContentT_ID, null);
		if (retValue.get_ID () != 0)
			s_cache.put (JP_ContractContentT_ID, retValue);
		return retValue;
	}	//	get

	public int getPrecision()
	{
		return MCurrency.getStdPrecision(getCtx(), getC_Currency_ID());
	}
	
	/**
	 * 	Set Price List (and Currency) when valid
	 * 	@param M_PriceList_ID price list
	 */
	@Override
	public void setM_PriceList_ID (int M_PriceList_ID)
	{
		MPriceList pl = MPriceList.get(getCtx(), M_PriceList_ID, null);
		if (pl != null) {
			setC_Currency_ID(pl.getC_Currency_ID());
			setIsTaxIncluded(pl.isTaxIncluded());
			super.setM_PriceList_ID(M_PriceList_ID);
		}
	}	//	setM_PriceList_ID

}
