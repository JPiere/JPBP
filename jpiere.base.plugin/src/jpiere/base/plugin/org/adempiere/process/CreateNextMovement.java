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

import java.sql.Timestamp;
import java.util.logging.Level;

import org.compiere.model.MLocator;
import org.compiere.model.MMovement;
import org.compiere.model.MMovementLine;
import org.compiere.model.MWarehouse;
import org.compiere.model.PO;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
import org.compiere.util.Msg;

import jpiere.base.plugin.org.adempiere.model.MPhysicalWarehouse;


/**
*
* Create Next Inventory Move
*
* JPIERE-0582: Register Route of Movement.
* JPIERE-0583: Create Next Inventory Move.
*
* @author h.hagiwara(h.hagiwara@oss-erp.co.jp)
*
*/
public class CreateNextMovement extends SvrProcess {

	private static final String IsRecordRouteJP = "IsRecordRouteJP";
	private static final String JP_Processing1 = "JP_Processing1";
	
	//From Warehouse
	private static final String JP_WarehouseFrom_ID = "JP_WarehouseFrom_ID";
	private static final String JP_PhysicalWarehouseFrom_ID = "JP_PhysicalWarehouseFrom_ID";
	
	//To Warehouse
	private static final String JP_WarehouseTo_ID = "JP_WarehouseTo_ID";
	private static final String JP_PhysicalWarehouseTo_ID = "JP_PhysicalWarehouseTo_ID";
	
	//Next Warehouse
	private static final String JP_WarehouseNext_ID = "JP_WarehouseNext_ID";
	private static final String JP_PhysicalWarehouseNext_ID = "JP_PhysicalWarehouseNext_ID";
	private static final String JP_MovementDateNext = "JP_MovementDateNext";
	
	//Departure Warehouse
	private static final String JP_WarehouseDep_ID = "JP_WarehouseDep_ID";
	private static final String JP_PhysicalWarehouseDep_ID = "JP_PhysicalWarehouseDep_ID";
	
	//Destination warehouse
	private static final String JP_WarehouseDst_ID = "JP_WarehouseDst_ID";
	private static final String JP_PhysicalWarehouseDst_ID = "JP_PhysicalWarehouseDst_ID";
	private static final String JP_MovementDateDst = "JP_MovementDateDst";
	
	private static final String JP_MovementPre_ID = "JP_MovementPre_ID";
	private static final String JP_MovementNext_ID = "JP_MovementNext_ID";
	
	private int p_AD_Org_ID = 0;
	private int p_C_DocType_ID = 0;
	private int p_JP_WarehouseNext_ID = 0;
	private int p_JP_PhysicalWarehouseNext_ID = 0;
	private int p_M_Locator_ID = 0;
	private Timestamp p_JP_MovementDateNext = null;
	private int p_M_Movement_ID = 0;
	
	@Override
	protected void prepare() 
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null){
				;
			}else if (name.equals("C_DocType_ID")){
				p_C_DocType_ID = para[i].getParameterAsInt();
			}else if (name.equals(JP_MovementDateNext)){
				p_JP_MovementDateNext = para[i].getParameterAsTimestamp();
			}else if (name.equals(JP_WarehouseNext_ID)){
				p_JP_WarehouseNext_ID = para[i].getParameterAsInt();
			}else if (name.equals(JP_PhysicalWarehouseNext_ID)){
				p_JP_PhysicalWarehouseNext_ID = para[i].getParameterAsInt();
			}else if (name.equals("M_Locator_ID")){
				p_M_Locator_ID = para[i].getParameterAsInt();
			}else{
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}//if
		}//for

		p_M_Movement_ID = getRecord_ID();
	}

	@Override
	protected String doIt() throws Exception
	{
		MMovement from = new MMovement (getCtx(), p_M_Movement_ID, get_TrxName());
		
		if(from.get_ValueAsInt(JP_MovementNext_ID) != 0)
		{
			MMovement next_mm = new MMovement(getCtx(), from.get_ValueAsInt(JP_MovementNext_ID), get_TrxName());
			addBufferLog(0, null, null, next_mm.getDocumentNo(),MMovement.Table_ID, next_mm.getM_Movement_ID());
			return Msg.getMsg(getCtx(), "JP_AlreadyRegistered");
		}
		
		MMovementLine[] lines = from.getLines(true);
		if(lines.length <= 0)
		{
			addLog("");	//for popup window
			return Msg.getMsg(getCtx(), "NoLines");
		}
		
		if(!from.getDocStatus().equals((DocAction.STATUS_Drafted))
				&& !from.getDocStatus().equals((DocAction.STATUS_InProgress))
				&& !from.getDocStatus().equals((DocAction.STATUS_Completed))
				&& !from.getDocStatus().equals((DocAction.STATUS_Closed))
				)
		{
			addLog("");	//for popup window
			return Msg.getMsg(getCtx(), "JP_InvalidDocStatus");
		}
		
		
		/** 
		 * Get Parameter 
		 **/
		if(p_JP_WarehouseNext_ID == 0)
		{
			p_JP_WarehouseNext_ID = from.get_ValueAsInt(JP_WarehouseNext_ID);
		}else {
			from.set_ValueNoCheck(JP_WarehouseNext_ID, p_JP_WarehouseNext_ID);
		}
		
		if(p_JP_PhysicalWarehouseNext_ID == 0)
		{
			p_JP_PhysicalWarehouseNext_ID = from.get_ValueAsInt(JP_PhysicalWarehouseNext_ID);
		}else {
			from.set_ValueNoCheck(JP_PhysicalWarehouseNext_ID, p_JP_PhysicalWarehouseNext_ID);
		}
		
		if(p_C_DocType_ID == 0)
		{
			p_C_DocType_ID = from.getC_DocType_ID();
		}
		
		if(p_JP_MovementDateNext == null)
		{
			Object obj__JP_MovementDateNext = from.get_Value(JP_MovementDateNext);
			if(obj__JP_MovementDateNext == null)
			{
				p_JP_MovementDateNext = from.getMovementDate();
				from.set_ValueNoCheck(JP_MovementDateNext, p_JP_MovementDateNext);
			}else {
				
				p_JP_MovementDateNext = (Timestamp)obj__JP_MovementDateNext;
			}
			
		}else {
			from.set_ValueNoCheck(JP_MovementDateNext, p_JP_MovementDateNext);
		}
		
		if(p_M_Locator_ID == 0)
		{
			if(p_JP_WarehouseNext_ID != 0)
			{
				MWarehouse m_wh = MWarehouse.get(p_JP_WarehouseNext_ID);
				if (p_JP_PhysicalWarehouseNext_ID != 0)
				{
					
					MPhysicalWarehouse m_pwh = MPhysicalWarehouse.get(getCtx(), p_JP_PhysicalWarehouseNext_ID, get_TrxName());
					MLocator locator =	m_pwh.getDefaultLocator(m_wh);
					if(locator != null)
					{
						p_M_Locator_ID = locator.getM_Locator_ID();
					}
					
				}else {
					
					MLocator locator = m_wh.getDefaultLocator();
					if(locator != null)
					{
						p_M_Locator_ID = locator.getM_Locator_ID();
					}
				}
			}
		}
		
		if(p_M_Locator_ID == 0)
		{
			throw new Exception(Msg.getMsg(getCtx(), "NotFound")+ Msg.getElement(getCtx(), "M_Locator_ID") );
		}
		
		if(p_AD_Org_ID == 0)
		{
			if(from.get_ValueAsInt(JP_WarehouseTo_ID) != 0)
			{
				p_AD_Org_ID = MWarehouse.get(from.get_ValueAsInt(JP_WarehouseTo_ID)).getAD_Org_ID();
			}else {
				p_AD_Org_ID = from.getAD_Org_ID();
			}
		}
		
		
		
		/** 
		 * Check data
		 **/
		if(from.get_ValueAsBoolean(IsRecordRouteJP) && from.get_ValueAsInt(JP_WarehouseTo_ID) == 0 && from.get_ValueAsInt(JP_PhysicalWarehouseTo_ID) == 0)
		{
			Object[] objs = new Object[]{Msg.getElement(getCtx(), "JP_WarehouseTo_ID") ,Msg.getElement(getCtx(), "JP_PhysicalWarehouseTo_ID")};
			String msg = Msg.getMsg(Env.getCtx(), "JP_Set_Either", objs);
			throw new Exception(msg);
		}
		
		
		if(from.get_ValueAsBoolean(IsRecordRouteJP) && from.get_ValueAsInt(JP_WarehouseNext_ID) == 0 && from.get_ValueAsInt(JP_PhysicalWarehouseNext_ID) == 0)
		{
			Object[] objs = new Object[]{Msg.getElement(getCtx(), "JP_WarehouseNext_ID") ,Msg.getElement(getCtx(), "JP_PhysicalWarehouseNext_ID")};
			String msg = Msg.getMsg(Env.getCtx(), "JP_Set_Either", objs);
			throw new Exception(msg);
		}
		
		if(p_JP_MovementDateNext.compareTo(from.getMovementDate()) < 0)
		{
			throw new Exception(Msg.getMsg(getCtx(), "Error") + Msg.getElement(getCtx(), "MovementDate") + " > " + Msg.getElement(getCtx(), "JP_MovementDateNext"));
		}
		
		
		MMovement to = new MMovement (getCtx(), 0, get_TrxName());
		
		
		/** 
		 * Copy 
		 **/
		PO.copyValues(from, to);
		
		//Copy Depature
		if(from.get_ValueAsInt(JP_WarehouseDep_ID) == 0) 
		{
			to.set_ValueNoCheck(JP_WarehouseDep_ID, null);
		}else {
			to.set_ValueNoCheck(JP_WarehouseDep_ID, from.get_ValueAsInt(JP_WarehouseDep_ID)); 
		}
		
		if(from.get_ValueAsInt(JP_PhysicalWarehouseDep_ID) == 0) 
		{
			to.set_ValueNoCheck(JP_PhysicalWarehouseDep_ID, null);
		}else {
			to.set_ValueNoCheck(JP_PhysicalWarehouseDep_ID, from.get_ValueAsInt(JP_PhysicalWarehouseDep_ID)); 
		}
		
		//Copy Destination
		if(from.get_ValueAsInt(JP_WarehouseDst_ID) == 0) 
		{
			to.set_ValueNoCheck(JP_WarehouseDst_ID, null);
		}else {
			to.set_ValueNoCheck(JP_WarehouseDst_ID, from.get_ValueAsInt(JP_WarehouseDst_ID)); 
		}
		
		if(from.get_ValueAsInt(JP_PhysicalWarehouseDst_ID) == 0) 
		{
			to.set_ValueNoCheck(JP_PhysicalWarehouseDst_ID, null);
		}else {
			to.set_ValueNoCheck(JP_PhysicalWarehouseDst_ID, from.get_ValueAsInt(JP_PhysicalWarehouseDst_ID)); 
		}
		to.set_ValueNoCheck(JP_MovementDateDst, from.get_Value(JP_MovementDateDst));
		
		
		/** 
		 * Set 
		 **/
		to.setAD_Org_ID(p_AD_Org_ID);
		to.setC_DocType_ID(p_C_DocType_ID);
		to.setDocumentNo("");
		to.setMovementDate(p_JP_MovementDateNext);
		
		//Set Warehouse(From) from Warehouse(To)
		if(from.get_ValueAsInt(JP_WarehouseTo_ID) == 0)
		{
			to.set_ValueNoCheck(JP_WarehouseFrom_ID, null);
		}else {
			to.set_ValueNoCheck(JP_WarehouseFrom_ID, from.get_ValueAsInt(JP_WarehouseTo_ID));
		}
		
		if(from.get_ValueAsInt(JP_PhysicalWarehouseTo_ID) == 0)
		{
			to.set_ValueNoCheck(JP_PhysicalWarehouseFrom_ID, null);
		}else {
			to.set_ValueNoCheck(JP_PhysicalWarehouseFrom_ID, from.get_ValueAsInt(JP_PhysicalWarehouseTo_ID));
		}
		
		
		//Set Warehouse(To) From Warehouse(Next)
		if(from.get_ValueAsInt(JP_WarehouseNext_ID) == 0)
		{
			to.set_ValueNoCheck(JP_WarehouseTo_ID, null);
		}else {
			to.set_ValueNoCheck(JP_WarehouseTo_ID, from.get_ValueAsInt(JP_WarehouseNext_ID));
		}
		
		if(from.get_ValueAsInt(JP_PhysicalWarehouseNext_ID) == 0)
		{
			to.set_ValueNoCheck(JP_PhysicalWarehouseTo_ID, null);
		}else {
			to.set_ValueNoCheck(JP_PhysicalWarehouseTo_ID, from.get_ValueAsInt(JP_PhysicalWarehouseNext_ID));
		}
		
		
		//Set Next Warehouse
		to.set_ValueNoCheck(JP_WarehouseNext_ID, null);
		to.set_ValueNoCheck(JP_PhysicalWarehouseNext_ID, null);
		to.set_ValueNoCheck(JP_MovementDateNext, null);
		
		to.set_ValueNoCheck(JP_MovementPre_ID, from.getM_Movement_ID());
		to.set_ValueNoCheck(IsRecordRouteJP, from.get_ValueAsBoolean(IsRecordRouteJP));
		to.saveEx(get_TrxName());
		
		from.set_ValueNoCheck(JP_Processing1, "Y");
		from.set_ValueNoCheck(JP_MovementNext_ID,  to.getM_Movement_ID());		
		from.saveEx(get_TrxName());
		
		
		for (MMovementLine fromLine : from.getLines(false))
		{
			if (!fromLine.isActive())
				continue;

			MMovementLine toLine = new MMovementLine(to);
			PO.copyValues(fromLine, toLine);
			toLine.setM_Movement_ID(to.getM_Movement_ID());
			toLine.setAD_Org_ID(to.getAD_Org_ID());
			toLine.setM_Product_ID(fromLine.getM_Product_ID());
			toLine.setM_Locator_ID(fromLine.getM_LocatorTo_ID());
			toLine.setM_LocatorTo_ID(p_M_Locator_ID);
			toLine.setM_AttributeSetInstance_ID(fromLine.getM_AttributeSetInstanceTo_ID());
			toLine.setM_AttributeSetInstanceTo_ID(fromLine.getM_AttributeSetInstanceTo_ID());
			toLine.setMovementQty(fromLine.getMovementQty());
			
			toLine.setProcessed(false);
			toLine.setTargetQty(Env.ZERO);
			toLine.setScrappedQty(Env.ZERO);
			toLine.setConfirmedQty(Env.ZERO);
			toLine.setReversalLine_ID(0);
			toLine.setDD_OrderLine_ID(0);

			toLine.saveEx();
		}
		
		addBufferLog(0, null, null, to.getDocumentNo(),MMovement.Table_ID, to.getM_Movement_ID());
		
		return Msg.getMsg(getCtx(), "Created") + " : " +to.getDocumentNo();
	}

}
