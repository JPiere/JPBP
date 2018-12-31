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

import org.compiere.model.MColumn;
import org.compiere.model.Query;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;

/**
 * JPIERE-0431:Contract Process Schedule
 *
 * @author Hideaki Hagiwara
 *
 */
public class MContractPSInOutLine extends X_JP_ContractPSInOutLine {

	public MContractPSInOutLine(Properties ctx, int JP_ContractPSInOutLine_ID, String trxName)
	{
		super(ctx, JP_ContractPSInOutLine_ID, trxName);
	}

	public MContractPSInOutLine(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}

	@Override
	protected boolean beforeSave(boolean newRecord)
	{

		if(!newRecord && isFactCreatedJP() && !is_ValueChanged("IsFactCreatedJP") )
		{
			int columnCount = get_ColumnCount();
			String columnName = null;
			MColumn column = null;
			boolean isOk = true;
			for(int i = 0; i < columnCount; i++)
			{
				if(is_ValueChanged(i))
				{
					columnName = get_ColumnName(i);
					if(columnName.equals("IsFactCreatedJP"))
						continue;

					column = MColumn.get(getCtx(), Table_Name, columnName);
					if(column.isAlwaysUpdateable())
					{
						continue;
					}else {
						isOk = false;
						break;
					}
				}

			}//for

			if(!isOk)
			{
				log.saveError("Error",  Msg.getMsg(Env.getCtx(),"JP_CannotChangeField",new Object[]{Msg.getElement(Env.getCtx(), columnName)})+ " : " + Msg.getElement(getCtx(), "IsFactCreatedJP"));
				return false;
			}
		}


		//Check Contract Calender and Period
		if(newRecord  || is_ValueChanged("JP_ContractCalender_InOut_ID") || is_ValueChanged("JP_ContractProcPeriod_ID"))
		{

			if(getJP_ContractLine_ID() != 0)
			{
				if(getJP_ContractLine().getJP_ContractCalender_InOut_ID() != getJP_ContractCalender_InOut_ID())
				{
					//Different between {0} and {1}
					String msg0 = Msg.getElement(Env.getCtx(), "JP_ContractLine_ID")+" - " + Msg.getElement(Env.getCtx(), "JP_ContractCalender_InOut_ID");
					String msg1 = Msg.getElement(Env.getCtx(), "JP_ContractPSInOutLine_ID")+" - " + Msg.getElement(Env.getCtx(), "JP_ContractCalender_InOut_ID");

					log.saveError("Error", Msg.getMsg(Env.getCtx(),"JP_Different",new Object[]{msg0,msg1}));
					return false;
				}
			}

			MContractProcPeriod cpp = MContractProcPeriod.get(getCtx(), getJP_ContractProcPeriod_ID());
			if(!cpp.isContainedBaseDocContractProcPeriod(getParent().getParent().getJP_ContractProcPeriod_ID()))
			{
				log.saveError("Error", Msg.getMsg(getCtx(), "JP_OutsidePperiod") + " : "
						+ Msg.getElement(getCtx(), "JP_ContractProcPeriod_ID"));//outside the specified period
				return false;
			}

		}

		//Set JP_ContractLine_ID
		if(newRecord || is_ValueChanged("JP_ContractPSLine_ID"))
		{
			if(getJP_ContractPSLine().getJP_ContractLine_ID() !=0)
			{
				setJP_ContractLine_ID(getJP_ContractPSLine().getJP_ContractLine_ID());
			}
		}


		//Check M_Product_ID
		if(newRecord || is_ValueChanged("M_Product_ID"))
		{
			if(getJP_ContractPSLine().getM_Product_ID() != getM_Product_ID())
			{
				//Different between {0} and {1}
				String msg0 = Msg.getElement(Env.getCtx(), "JP_ContractLine_ID")+" - " + Msg.getElement(Env.getCtx(), "M_Product_ID");
				String msg1 = Msg.getElement(Env.getCtx(), "JP_ContractPSInOutLine_ID")+" - " + Msg.getElement(Env.getCtx(), "M_Product_ID");

				log.saveError("Error", Msg.getMsg(Env.getCtx(),"JP_Different",new Object[]{msg0,msg1}));
				return false;
			}
		}

		//Check C_Charge_ID
		if(newRecord || is_ValueChanged("C_Charge_ID"))
		{
			if(getJP_ContractPSLine().getC_Charge_ID() != getC_Charge_ID())
			{
				String msg0 = Msg.getElement(Env.getCtx(), "JP_ContractLine_ID")+" - " + Msg.getElement(Env.getCtx(), "C_Charge_ID");
				String msg1 = Msg.getElement(Env.getCtx(), "JP_ContractPSInOutLine_ID")+" - " + Msg.getElement(Env.getCtx(), "C_Charge_ID");

				log.saveError("Error", Msg.getMsg(Env.getCtx(),"JP_Different",new Object[]{msg0,msg1}));
				return false;
			}
		}

		//Check C_UOM_ID
		if(newRecord || is_ValueChanged("C_UOM_ID"))
		{
			int C_UOM_ID = 0;
			if(getJP_ContractPSLine().getM_Product_ID() > 0)
				C_UOM_ID = getJP_ContractPSLine().getM_Product().getC_UOM_ID();
			else
				C_UOM_ID = getJP_ContractPSLine().getC_UOM_ID();

			setC_UOM_ID(C_UOM_ID);
		}

		return true;

	}//beforeSave

	@Override
	protected boolean afterSave(boolean newRecord, boolean success) {

		if(success)
		{
			//Update Contract Process Schedule Line
			String sql = "UPDATE JP_ContractPSLine i"
				+ " SET JP_ScheduledTotalRecognizeAmt="
					+ "(SELECT COALESCE(SUM(LineNetAmt),0) FROM JP_ContractPSInOutLine il WHERE i.JP_ContractPSLine_ID=il.JP_ContractPSLine_ID) "
				    + ", JP_ScheduledTotalMovementQty = "
				    + "(SELECT COALESCE(SUM(MovementQty),0) FROM JP_ContractPSInOutLine il WHERE i.JP_ContractPSLine_ID=il.JP_ContractPSLine_ID)"
				+ "WHERE JP_ContractPSLine_ID = ?";
			int no = DB.executeUpdate(sql, new Object[]{Integer.valueOf(getJP_ContractPSLine_ID())}, false, get_TrxName(), 0);
			if (no != 1)
				log.warning("(1) #" + no);

		}

		return success;

	}//afterSave

	/** Parent					*/
	protected MContractPSLine			m_parent = null;

	public MContractPSLine getParent()
	{
		if (m_parent == null)
			m_parent = new MContractPSLine(getCtx(), getJP_ContractPSLine_ID(), get_TrxName());
		return m_parent;
	}	//	getParent

	public static MContractPSInOutLine[] getContractPSInOutLinebyInOutLine(Properties ctx, int M_InOutLine_ID, String trxName)
	{

		StringBuilder whereClauseFinal = new StringBuilder(MContractPSInOutLine.COLUMNNAME_M_InOutLine_ID+"=? " );

		List<MContractPSInOutLine> list = new Query(Env.getCtx(), MContractPSInOutLine.Table_Name, whereClauseFinal.toString(), trxName)
										.setParameters(M_InOutLine_ID)
										.setOrderBy(MContractPSInOutLine.COLUMNNAME_JP_ContractPSInOutLine_ID + " DESC ")
										.list();

		//
		return list.toArray(new MContractPSInOutLine[list.size()]);

	}

}
