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
package jpiere.base.plugin.org.adempiere.process;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Level;

import org.compiere.model.I_C_NonBusinessDay;
import org.compiere.model.MLocator;
import org.compiere.model.MOrgInfo;
import org.compiere.model.MTable;
import org.compiere.model.MTree;
import org.compiere.model.MTree_Node;
import org.compiere.model.MWarehouse;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.model.X_C_NonBusinessDay;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
import org.compiere.util.Msg;

import jpiere.base.plugin.org.adempiere.model.MPPDoc;
import jpiere.base.plugin.org.adempiere.model.MPPDocT;
import jpiere.base.plugin.org.adempiere.model.MPPPlan;
import jpiere.base.plugin.org.adempiere.model.MPPPlanLine;
import jpiere.base.plugin.org.adempiere.model.MPPPlanLineT;
import jpiere.base.plugin.org.adempiere.model.MPPPlanT;


/**
 * JPIERE-0502: Create JPiere PP Doc from Template
 *
 * @author Hideaki Hagiwara
 *
 */
public class PPCreateDocFromTemplate extends SvrProcess {

	private int p_Record_ID = 0;
	private BigDecimal p_CoefficientQty = Env.ZERO;
	private Timestamp p_JP_PP_ScheduledStart = null;
	private MTable m_Table = null;


	@Override
	protected void prepare()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if ("JP_PP_DocT_ID".equals(name))
				p_Record_ID = para[i].getParameterAsInt();
			else if ("QtyEntered".equals(name))
				p_CoefficientQty = para[i].getParameterAsBigDecimal();
			else if ("JP_PP_ScheduledStart".equals(name))
				p_JP_PP_ScheduledStart  =  para[i].getParameterAsTimestamp();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}

		if(p_Record_ID == 0)
			p_Record_ID = getRecord_ID();

		m_Table = MTable.get(getTable_ID());
	}

	private int M_Locator_ID = 0;
	private MPPDoc ppDoc = null;
	private MPPDocT ppDocT = null;

	@Override
	protected String doIt() throws Exception
	{
		if(m_Table.getTableName().equals(MPPDocT.Table_Name))
		{
			createPPDoc();
			createPlan();
			updateTree();
			addBufferLog(0, null, null, ppDoc.getDocumentInfo(), MPPDoc.Table_ID, ppDoc.getJP_PP_Doc_ID());

		}else if(m_Table.getTableName().equals(MPPDoc.Table_Name)) {

			ppDoc = new MPPDoc(getCtx(), p_Record_ID, get_TrxName());
			if(ppDoc.getJP_PP_DocT_ID() == 0)
			{
				throw new Exception(Msg.getMsg(getCtx(), "NotFound")+ " " + Msg.getElement(getCtx(), MPPDoc.COLUMNNAME_JP_PP_DocT_ID) );
			}

			if(ppDoc.getProductionQty().compareTo(Env.ZERO)==0)
			{
				throw new Exception(Msg.getElement(getCtx(), MPPDoc.COLUMNNAME_ProductionQty) + " = 0" );
			}

			p_CoefficientQty = ppDoc.getProductionQty();

			ppDocT = new MPPDocT(getCtx(), ppDoc.getJP_PP_DocT_ID(), get_TrxName());


			createPlan();
			updateTree();

		}else if(m_Table.getTableName().equals(MPPPlan.Table_Name)) {

			MPPPlan ppPlan = new MPPPlan(getCtx(), p_Record_ID, get_TrxName());
			if(ppPlan.getJP_PP_PlanT_ID() == 0)
			{
				throw new Exception(Msg.getMsg(getCtx(), "NotFound")+ " " + Msg.getElement(getCtx(), MPPPlan.COLUMNNAME_JP_PP_PlanT_ID) );
			}

			if(ppPlan.getProductionQty().compareTo(Env.ZERO)==0)
			{
				throw new Exception(Msg.getElement(getCtx(), MPPDoc.COLUMNNAME_ProductionQty) + " = 0" );
			}

			MPPPlanT ppPlanT = new MPPPlanT(getCtx(), ppPlan.getJP_PP_PlanT_ID(), get_TrxName());

			BigDecimal planQty = ppPlan.getProductionQty();
			BigDecimal templateQty = ppPlanT.getProductionQty();;
			BigDecimal rate = Env.ONE;
			if(templateQty != null && templateQty.compareTo(Env.ZERO) != 0)
				rate = planQty.divide(templateQty, 4, RoundingMode.HALF_UP);

			p_CoefficientQty = rate;
			createPlanLine(ppPlan, ppPlanT);
		}

		return "@OK@";
	}

	private LocalDateTime JP_PP_ScheduledStart = null;


	private MPPDoc createPPDoc()
	{
		ppDoc = new  MPPDoc(getCtx(), 0, get_TrxName());
		ppDocT = new MPPDocT(getCtx(), p_Record_ID, get_TrxName());

		PO.copyValues(ppDocT, ppDoc);

		//Copy mandatory column to make sure
		ppDoc.setJP_PP_DocT_ID(p_Record_ID);
		ppDoc.setAD_Org_ID(ppDocT.getAD_Org_ID());
		ppDoc.setM_Product_ID(ppDocT.getM_Product_ID());
		ppDoc.setQtyEntered(p_CoefficientQty);
		ppDoc.setC_UOM_ID(ppDocT.getC_UOM_ID());
		ppDoc.setProductionQty(p_CoefficientQty.multiply(ppDocT.getProductionQty()));
		ppDoc.setC_DocType_ID(ppDocT.getC_DocType_ID());

		LocalDateTime toDay = p_JP_PP_ScheduledStart.toLocalDateTime();

		//TODO 日次調整のテスト
		while (isNonBusinessDay(toDay))
		{
			toDay = toDay.plusDays(1);
		}
		JP_PP_ScheduledStart = toDay;
		ppDoc.setJP_PP_ScheduledStart(Timestamp.valueOf(JP_PP_ScheduledStart));

		int JP_ProductionDays = ppDocT.getJP_ProductionDays();
		while (JP_ProductionDays > 1 )
		{
			toDay = toDay.plusDays(1);
			if(isBusinessDay(toDay))
				JP_ProductionDays--;
		}

		ppDoc.setJP_PP_ScheduledEnd(Timestamp.valueOf(toDay));
		ppDoc.setDateAcct(Timestamp.valueOf(toDay));

		ppDoc.setValue(ppDocT.getValue());
		ppDoc.setName(ppDocT.getName());
		ppDoc.setDocStatus(DocAction.STATUS_Drafted);
		ppDoc.setDocAction(DocAction.ACTION_Complete);
		ppDoc.setJP_PP_Status(MPPDoc.JP_PP_STATUS_NotYetStarted);
		ppDoc.setJP_Processing1("N");
		ppDoc.setJP_Processing2("N");
		ppDoc.setJP_Processing3("N");
		ppDoc.setJP_Processing4("N");
		ppDoc.setJP_Processing5("N");
		ppDoc.setJP_Processing6("N");
		ppDoc.saveEx(get_TrxName());

		return ppDoc;
	}

	private MPPPlan createPlan() throws Exception
	{
		MPPPlanT[] ppPlanTs = ppDocT.getPPPlanTs(true, null);
		MPPPlan ppPlan = null;
		for(MPPPlanT ppPlanT : ppPlanTs)
		{
			ppPlan = new  MPPPlan(getCtx(), 0, get_TrxName());
			PO.copyValues(ppPlanT, ppPlan);

			//Copy mandatory column to make sure
			ppPlan.setJP_PP_Doc_ID(ppDoc.getJP_PP_Doc_ID());
			ppPlan.setAD_Org_ID(ppDoc.getAD_Org_ID());
			ppPlan.setJP_PP_PlanT_ID(ppPlanT.getJP_PP_PlanT_ID());
			ppPlan.setSeqNo(ppPlanT.getSeqNo());
			ppPlan.setIsSummary(ppPlanT.isSummary());
			ppPlan.setM_Product_ID(ppPlanT.getM_Product_ID());
			if(ppPlan.getAD_Org_ID() == ppPlanT.getAD_Org_ID())
			{
				ppPlan.setM_Locator_ID(ppPlanT.getM_Locator_ID());

			}else if (M_Locator_ID != 0){

				ppPlan.setM_Locator_ID(M_Locator_ID);

			}else {

				M_Locator_ID = searchLocator(ppPlan.getAD_Org_ID());
				if(M_Locator_ID > 0)
				{
					ppPlan.setM_Locator_ID(M_Locator_ID);
				}else {
					throw new Exception(Msg.getMsg(getCtx(), "JP_PP_NotFoundLocatorCopyTemplate"));
				}
			}// set Locator

			ppPlan.setC_DocType_ID(ppPlanT.getC_DocType_ID());
			ppPlan.setValue(ppPlanT.getValue());
			ppPlan.setName(ppPlanT.getName());
			ppPlan.setProductionQty(p_CoefficientQty.multiply(ppPlanT.getProductionQty()));
			ppPlan.setC_UOM_ID(ppPlanT.getC_UOM_ID());
			ppPlan.setJP_PP_Workload_Plan(ppPlanT.getJP_PP_Workload_Plan());
			ppPlan.setJP_PP_Workload_UOM_ID(ppPlanT.getJP_PP_Workload_UOM_ID());

			int offset = ppPlanT.getJP_DayOffset();

			LocalDateTime startDay = JP_PP_ScheduledStart;

			//TODO 日次調整のテスト
			while (offset >= 0 )
			{
				if(isBusinessDay(startDay))
				{
					if(offset == 0)
					{
						;//Noting to do;
					}else {
						startDay = startDay.plusDays(1);
					}
					offset--;
				}else {
					startDay = startDay.plusDays(1);
				}
			}

			ppPlan.setJP_PP_ScheduledStart(Timestamp.valueOf(startDay));

			int JP_ProductionDays = ppPlanT.getJP_ProductionDays();
			while (JP_ProductionDays > 1 )
			{
				startDay = startDay.plusDays(1);
				if(isBusinessDay(startDay))
					JP_ProductionDays--;
			}

			ppPlan.setJP_PP_ScheduledEnd(Timestamp.valueOf(startDay));
			ppPlan.setDateAcct(Timestamp.valueOf(startDay));

			ppPlan.setDocStatus(DocAction.STATUS_Drafted);
			ppPlan.setDocAction(DocAction.ACTION_Complete);
			ppPlan.setJP_PP_Status(MPPDoc.JP_PP_STATUS_NotYetStarted);
			ppPlan.setJP_Processing1("N");
			ppPlan.setJP_Processing2("N");
			ppPlan.setJP_Processing3("N");
			ppPlan.setJP_Processing4("N");
			ppPlan.setJP_Processing5("N");
			ppPlan.setJP_Processing6("N");
			ppPlan.saveEx(get_TrxName());

			createPlanLine(ppPlan, ppPlanT);

		}

		return ppPlan;
	}

	private int searchLocator(int AD_Org_ID)
	{
		MOrgInfo oInfo = MOrgInfo.get(AD_Org_ID);
		if(oInfo.getM_Warehouse_ID() > 0)
		{
			MWarehouse wh = MWarehouse.get(oInfo.getM_Warehouse_ID());
			MLocator[] locs = wh.getLocators(false);
			boolean isOK = false;

			for(MLocator loc : locs)
			{
				if(loc.isDefault())
				{
					M_Locator_ID =loc.getM_Locator_ID();
					return M_Locator_ID;
				}
			}

			if(!isOK)
			{
				//set First locator
				for(MLocator loc : locs)
				{
					M_Locator_ID =loc.getM_Locator_ID();
					return M_Locator_ID;
				}
			}

		}

		return 0;
	}

	private boolean createPlanLine(MPPPlan ppPlan, MPPPlanT ppPlanT) throws Exception
	{
		MPPPlanLineT[] ppPlanLineTs = ppPlanT.getPPPlanLineTs(true, null);
		MPPPlanLine ppPlanLine = null;
		for(MPPPlanLineT ppPlanLineT : ppPlanLineTs)
		{
			ppPlanLine = new  MPPPlanLine(getCtx(), 0, get_TrxName());
			PO.copyValues(ppPlanLineT, ppPlanLine);

			//Copy mandatory column to make sure
			ppPlanLine.setAD_Org_ID(ppPlan.getAD_Org_ID());
			ppPlanLine.setJP_PP_Plan_ID(ppPlan.getJP_PP_Plan_ID());
			ppPlanLine.setJP_PP_PlanLineT_ID(ppPlanLineT.getJP_PP_PlanLineT_ID());
			ppPlanLine.setLine(ppPlanLineT.getLine());
			ppPlanLine.setM_Product_ID(ppPlanLineT.getM_Product_ID());
			ppPlanLine.setIsEndProduct(ppPlanLineT.isEndProduct());
			ppPlanLine.setPlannedQty(p_CoefficientQty.multiply(ppPlanLineT.getPlannedQty()));
			ppPlanLine.setC_UOM_ID(ppPlanLineT.getC_UOM_ID());
			if(ppPlanLineT.isEndProduct())
				ppPlanLine.setQtyUsed(null);
			else
				ppPlanLine.setQtyUsed(p_CoefficientQty.multiply(ppPlanLineT.getQtyUsed()));
			ppPlanLine.setMovementQty(p_CoefficientQty.multiply(ppPlanLineT.getMovementQty()));

			if(ppPlanLine.getAD_Org_ID() == ppPlanLineT.getAD_Org_ID())
			{
				ppPlanLine.setM_Locator_ID(ppPlanLineT.getM_Locator_ID());
			}else if (M_Locator_ID != 0){

				ppPlanLine.setM_Locator_ID(M_Locator_ID);

			}else {

				M_Locator_ID = searchLocator(ppPlanLine.getAD_Org_ID());
				if(M_Locator_ID > 0)
				{
					ppPlanLine.setM_Locator_ID(M_Locator_ID);
				}else {
					throw new Exception(Msg.getMsg(getCtx(), "JP_PP_NotFoundLocatorCopyTemplate"));
				}
			}

			ppPlanLine.setJP_Processing1("N");
			ppPlanLine.setJP_Processing2("N");
			ppPlanLine.setJP_Processing3("N");
			ppPlanLine.saveEx(get_TrxName());

		}


		if(ppPlanT.isCreatePPFactJP())
		{
			ppPlan.createFact(get_TrxName());
		}

		return true;
	}

	private boolean updateTree()
	{
		int p_AD_TreeFrom_ID = MTree.getDefaultAD_Tree_ID(getAD_Client_ID(), "JP_PP_PlanT_ID");
		int p_AD_TreeTo_ID = MTree.getDefaultAD_Tree_ID(getAD_Client_ID(), "JP_PP_Plan_ID");
		MTree treeFrom =  new MTree(getCtx(), p_AD_TreeFrom_ID, get_TrxName());
		MTree treeTo =  new MTree(getCtx(), p_AD_TreeTo_ID, get_TrxName());

		MPPPlan[] ppPlans = ppDoc.getPPPlans(true, null);
		for(int i = 0;  i < ppPlans.length ; i++)
		{
			MTree_Node nodeTo = MTree_Node.get(treeTo, ppPlans[i].getJP_PP_Plan_ID());
			MTree_Node nodeFrom = MTree_Node.get(treeFrom, ppPlans[i].getJP_PP_PlanT_ID());

			for(int j = 0; j < ppPlans.length ; j++)
			{
				if(nodeFrom.getParent_ID() == 0)
				{
					nodeTo.setParent_ID(0);
					nodeTo.setSeqNo(nodeFrom.getSeqNo());
					nodeTo.save(get_TrxName());
					break;

				}else if(nodeFrom.getParent_ID() == ppPlans[j].getJP_PP_PlanT_ID()){

					nodeTo.setParent_ID(ppPlans[j].getJP_PP_Plan_ID());
					nodeTo.setSeqNo(nodeFrom.getSeqNo());
					nodeTo.save(get_TrxName());
					break;
				}

			}//for j
		}//for i

		return true;
	}

	int p_C_Country_ID = 0;//TODO 国の判定が必要だな!

	private TreeSet<Timestamp> nonBusinessDays = null;
	private boolean isNonBusinessDay(LocalDateTime toDay)
	{
		getNonBusinessDays(toDay);
		return nonBusinessDays.contains(toDay);

	}

	private boolean isBusinessDay(LocalDateTime toDay)
	{
		getNonBusinessDays(toDay);
		return !nonBusinessDays.contains(toDay);
	}

	private TreeSet<Timestamp> getNonBusinessDays(LocalDateTime toDay)
	{
		if(nonBusinessDays == null)
		{
			List<X_C_NonBusinessDay> list_NonBusinessDays = null;

			nonBusinessDays = new TreeSet<Timestamp>();
			StringBuilder whereClause = null;
			StringBuilder orderClause = null;
			ArrayList<Object> list_parameters  = new ArrayList<Object>();
			Object[] parameters = null;

			LocalDateTime toDayMin = LocalDateTime.of(toDay.toLocalDate(), LocalTime.MIN);

			whereClause = new StringBuilder(" AD_Client_ID=? ");
			list_parameters.add(Env.getAD_Client_ID(getCtx()));

			//C_Calendar_ID
			whereClause = whereClause.append(" AND C_Calendar_ID = ? ");
			list_parameters.add(ppDocT.getJP_NonBusinessDayCalendar_ID());

			//Date1
			whereClause = whereClause.append(" AND Date1 >= ? AND IsActive='Y' ");
			list_parameters.add(Timestamp.valueOf(toDayMin));

			//C_Country_ID
			if(p_C_Country_ID == 0)
			{
				whereClause = whereClause.append(" AND C_Country_ID IS NULL ");

			}else {
				whereClause = whereClause.append(" AND ( C_Country_ID IS NULL OR C_Country_ID = ? ) ");
				list_parameters.add(p_C_Country_ID);
			}

			parameters = list_parameters.toArray(new Object[list_parameters.size()]);
			orderClause = new StringBuilder("Date1");


			list_NonBusinessDays = new Query(Env.getCtx(), I_C_NonBusinessDay.Table_Name, whereClause.toString(), null)
												.setParameters(parameters)
												.setOrderBy(orderClause.toString())
												.list();

			LocalDateTime nonBusinessDayMin = null;
			for(X_C_NonBusinessDay m_NonBusinessDays : list_NonBusinessDays )
			{
				nonBusinessDayMin = LocalDateTime.of(m_NonBusinessDays.getDate1().toLocalDateTime().toLocalDate(), LocalTime.MIN);
				nonBusinessDays.add(Timestamp.valueOf(nonBusinessDayMin));
			}
		}

		return nonBusinessDays;
	}
}
