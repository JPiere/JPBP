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
package jpiere.base.plugin.org.adempiere.base;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;

import org.compiere.model.MClient;
import org.compiere.model.MMovement;
import org.compiere.model.MMovementLine;
import org.compiere.model.MOrder;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.process.DocAction;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;


/**
*
* JPiere Movement model Validator
*
* JPIERE-0227: Common Warehouse
* JPIERE-0582: Register Route of Movement
* 
* @author h.hagiwara(h.hagiwara@oss-erp.co.jp)
*
*/
public class JPiereMovementModelValidator implements ModelValidator {

	private static CLogger log = CLogger.getCLogger(JPiereMovementModelValidator.class);
	private int AD_Client_ID = -1;

	@Override
	public void initialize(ModelValidationEngine engine, MClient client)
	{
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();
		engine.addModelChange(MMovement.Table_Name, this);
		engine.addDocValidate(MMovement.Table_Name, this);

		if (log.isLoggable(Level.FINE)) log.fine("Initialize JPiereMovementModelValidator");

	}

	@Override
	public int getAD_Client_ID()
	{
		return AD_Client_ID;
	}

	@Override
	public String login(int AD_Org_ID, int AD_Role_ID, int AD_User_ID)
	{
		return null;
	}

	
	private static final String IsRecordRouteJP = "IsRecordRouteJP";
	
	//From Warehouse
	private static final String JP_WarehouseFrom_ID = "JP_WarehouseFrom_ID";
	private static final String JP_PhysicalWarehouseFrom_ID = "JP_PhysicalWarehouseFrom_ID";
	
	//To Warehouse
	private static final String JP_WarehouseTo_ID = "JP_WarehouseTo_ID";
	private static final String JP_PhysicalWarehouseTo_ID = "JP_PhysicalWarehouseTo_ID";
	
	//Next Warehouse
	@SuppressWarnings("unused")
	private static final String JP_WarehouseNext_ID = "JP_WarehouseNext_ID";
	@SuppressWarnings("unused")
	private static final String JP_PhysicalWarehouseNext_ID = "JP_PhysicalWarehouseNext_ID";
	@SuppressWarnings("unused")
	private static final String JP_MovementDateNext = "JP_MovementDateNext";
	
	//Departure Warehouse
	private static final String JP_WarehouseDep_ID = "JP_WarehouseDep_ID";
	private static final String JP_PhysicalWarehouseDep_ID = "JP_PhysicalWarehouseDep_ID";
	private static final String JP_MovementDateDep = "JP_MovementDateDep";
	
	//Destination warehouse
	private static final String JP_WarehouseDst_ID = "JP_WarehouseDst_ID";
	private static final String JP_PhysicalWarehouseDst_ID = "JP_PhysicalWarehouseDst_ID";
	private static final String JP_MovementDateDst = "JP_MovementDateDst";
	
	@SuppressWarnings("unused")
	private static final String JP_MovementPre_ID = "JP_MovementPre_ID";
	@SuppressWarnings("unused")
	private static final String JP_MovementNext_ID = "JP_MovementNext_ID";
	
	
	@Override
	public String modelChange(PO po, int type) throws Exception
	{
		//JPIERE-0582
		if(type ==  ModelValidator.TYPE_BEFORE_NEW || type ==  ModelValidator.TYPE_BEFORE_CHANGE)
		{
			if(!po.get_ValueAsBoolean(IsRecordRouteJP))
			{
				po.set_ValueNoCheck(JP_WarehouseDep_ID, null);
				po.set_ValueNoCheck(JP_PhysicalWarehouseDep_ID, null);
				po.set_ValueNoCheck(JP_MovementDateDep, null);
				
				//po.set_ValueNoCheck(JP_MovementPre_ID, null); Don't not clear.
				//po.set_ValueNoCheck(JP_MovementNext_ID, null); Don't not clear.
				
				po.set_ValueNoCheck(JP_WarehouseDst_ID, null);
				po.set_ValueNoCheck(JP_PhysicalWarehouseDst_ID, null);
				po.set_ValueNoCheck(JP_MovementDateDst, null);
			}
		}
		
		return null;
	}

	
	@Override
	public String docValidate(PO po, int timing)
	{
		//JPIERE-0227
		if(timing ==  ModelValidator.TIMING_BEFORE_CLOSE)
		{
			if(po instanceof MMovement)
			{
				int JP_Order_ID = po.get_ValueAsInt("JP_Order_ID");
				if(JP_Order_ID > 0)
				{
					MOrder order = new MOrder(po.getCtx(),JP_Order_ID, po.get_TrxName());
					if(!order.getDocStatus().equals(DocAction.STATUS_Closed))
					{
						//You can not close Movement document, because Document status of Sales order is not close.
						return Msg.getMsg(po.getCtx(), "JP_Order_MM_CloseCheck");
					}
				}

			}
		}
		
		//JPIERE-0582: Register Route of Movement - Check Warehouse consistency.
		if(timing ==  ModelValidator.TIMING_BEFORE_PREPARE)
		{
			if(po.get_ValueAsBoolean(IsRecordRouteJP))
			{
				MMovement mm = (MMovement)po;
				
				int int_WarehouseFrom_ID = mm.get_ValueAsInt(JP_WarehouseFrom_ID);
				int int_PhysicalWarehouseFrom_ID = mm.get_ValueAsInt(JP_PhysicalWarehouseFrom_ID);
				int int_WarehouseTo_ID = mm.get_ValueAsInt(JP_WarehouseTo_ID);
				int int_PhysicalWarehouseTo_ID = mm.get_ValueAsInt(JP_PhysicalWarehouseTo_ID);
				StringBuffer sb = new StringBuffer();
				
				if(int_WarehouseFrom_ID != 0)
				{
					String sql = "SELECT * FROM M_MovementLine ml INNER JOIN M_Locator loc ON (ml.M_Locator_ID = loc.M_Locator_ID) "
									+ " WHERE M_Movement_ID = ? AND loc.M_Warehouse_ID <> ? ORDER BY ml.line";
					
					PreparedStatement pstmt = null;
					ResultSet rs = null;
					try
					{
						pstmt = DB.prepareStatement(sql, mm.get_TrxName());
						pstmt.setInt(1, mm.getM_Movement_ID());
						pstmt.setInt(2, int_WarehouseFrom_ID);
						rs = pstmt.executeQuery();
						
						while (rs.next())
						{
							MMovementLine line = new MMovementLine(mm.getCtx(),rs, mm.get_TrxName());
							sb.append(line.getLine()).append(" / ");
						}
						
						if(!Util.isEmpty(sb.toString()))
						{
							String msg0 = Msg.getElement(Env.getCtx(), JP_WarehouseFrom_ID);
							String msg1 = Msg.getElement(Env.getCtx(), "M_Locator_ID");
							String msg = Msg.getMsg(Env.getCtx(),"JP_Different",new Object[]{msg0,msg1});//Different between {0} and {1}
							return msg + Msg.getElement(mm.getCtx(), MMovementLine.COLUMNNAME_Line) + " : " + sb.toString();
						}
					}
					catch (Exception e)
					{
						log.log(Level.SEVERE, sql, e);
					}
					finally
					{
						DB.close(rs, pstmt);
						rs = null;
						pstmt = null;
					}
				}
				
				if(int_PhysicalWarehouseFrom_ID != 0)
				{
					String sql = "SELECT * FROM M_MovementLine ml INNER JOIN M_Locator loc ON (ml.M_Locator_ID = loc.M_Locator_ID) "
							+ " WHERE M_Movement_ID = ? AND loc.JP_PhysicalWarehouse_ID <> ? ORDER BY ml.line";
					
					PreparedStatement pstmt = null;
					ResultSet rs = null;
					try
					{
						pstmt = DB.prepareStatement(sql, mm.get_TrxName());
						pstmt.setInt(1, mm.getM_Movement_ID());
						pstmt.setInt(2, int_PhysicalWarehouseFrom_ID);
						rs = pstmt.executeQuery();
						while (rs.next())
						{
							MMovementLine line = new MMovementLine(mm.getCtx(),rs, mm.get_TrxName());
							sb.append(line.getLine()).append(" / ");
						}
						
						if(!Util.isEmpty(sb.toString()))
						{
							String msg0 = Msg.getElement(Env.getCtx(), JP_PhysicalWarehouseFrom_ID);
							String msg1 = Msg.getElement(Env.getCtx(), "M_Locator_ID");
							String msg = Msg.getMsg(Env.getCtx(),"JP_Different",new Object[]{msg0,msg1});//Different between {0} and {1}
							return msg + Msg.getElement(mm.getCtx(), MMovementLine.COLUMNNAME_Line) + " : " + sb.toString();
						}
					}
					catch (Exception e)
					{
						log.log(Level.SEVERE, sql, e);
					}
					finally
					{
						DB.close(rs, pstmt);
						rs = null;
						pstmt = null;
					}
				}
				
				if(int_WarehouseTo_ID != 0)
				{
					String sql = "SELECT * FROM M_MovementLine ml INNER JOIN M_Locator loc ON (ml.M_LocatorTo_ID = loc.M_Locator_ID) "
							+ " WHERE M_Movement_ID = ? AND loc.M_Warehouse_ID <> ? ORDER BY ml.line";		
					
					PreparedStatement pstmt = null;
					ResultSet rs = null;
					try
					{
						pstmt = DB.prepareStatement(sql, mm.get_TrxName());
						pstmt.setInt(1, mm.getM_Movement_ID());
						pstmt.setInt(2, int_WarehouseTo_ID);
						rs = pstmt.executeQuery();
						while (rs.next())
						{
							MMovementLine line = new MMovementLine(mm.getCtx(),rs, mm.get_TrxName());
							sb.append(line.getLine()).append(" / ");
						}
						
						if(!Util.isEmpty(sb.toString()))
						{
							String msg0 = Msg.getElement(Env.getCtx(), JP_WarehouseTo_ID);
							String msg1 = Msg.getElement(Env.getCtx(), "M_LocatorTo_ID");
							String msg = Msg.getMsg(Env.getCtx(),"JP_Different",new Object[]{msg0,msg1});//Different between {0} and {1}
							return msg + Msg.getElement(mm.getCtx(), MMovementLine.COLUMNNAME_Line) + " : " + sb.toString();
						}
					}
					catch (Exception e)
					{
						log.log(Level.SEVERE, sql, e);
					}
					finally
					{
						DB.close(rs, pstmt);
						rs = null;
						pstmt = null;
					}
				}
				
				
				if(int_PhysicalWarehouseTo_ID != 0)
				{
					String sql = "SELECT * FROM M_MovementLine ml INNER JOIN M_Locator loc ON (ml.M_LocatorTo_ID = loc.M_Locator_ID) "
							+ " WHERE M_Movement_ID = ? AND loc.JP_PhysicalWarehouse_ID <> ? ORDER BY ml.line";	
					
					PreparedStatement pstmt = null;
					ResultSet rs = null;
					try
					{
						pstmt = DB.prepareStatement(sql, mm.get_TrxName());
						pstmt.setInt(1, mm.getM_Movement_ID());
						pstmt.setInt(2, int_PhysicalWarehouseTo_ID);
						rs = pstmt.executeQuery();
						while (rs.next())
						{
							MMovementLine line = new MMovementLine(mm.getCtx(),rs, mm.get_TrxName());
							sb.append(line.getLine()).append(" / ");
						}
						
						if(!Util.isEmpty(sb.toString()))
						{
							String msg0 = Msg.getElement(Env.getCtx(), JP_PhysicalWarehouseTo_ID);
							String msg1 = Msg.getElement(Env.getCtx(), "M_LocatorTo_ID");
							String msg = Msg.getMsg(Env.getCtx(),"JP_Different",new Object[]{msg0,msg1});//Different between {0} and {1}
							return msg + Msg.getElement(mm.getCtx(), MMovementLine.COLUMNNAME_Line) + " : " + sb.toString();
						}
					}
					catch (Exception e)
					{
						log.log(Level.SEVERE, sql, e);
					}
					finally
					{
						DB.close(rs, pstmt);
						rs = null;
						pstmt = null;
					}
				}
				
				
			}
		}

		return null;
	}



}
