/******************************************************************************
 * Copyright (C) 2009 Low Heng Sin                                            *
 * Copyright (C) 2009 Idalica Corporation                                     *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 *****************************************************************************/
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

package jpiere.base.plugin.webui.apps.form;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;

import org.adempiere.webui.window.FDialog;
import org.compiere.apps.IStatusBar;
import org.compiere.grid.CreateFrom;
import org.compiere.minigrid.IMiniTable;
import org.compiere.model.GridTab;
import org.compiere.model.MInOut;
import org.compiere.model.MInOutLine;
import org.compiere.model.MLocator;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MProduct;
import org.compiere.model.MWarehouse;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;
import org.compiere.util.Msg;

/**
 *  JPIERE-0145:Create Shipment from Sales Orders
 *
 *  @author Jorg Janke
 *  @version  $Id: VCreateFromShipment.java,v 1.4 2006/07/30 00:51:28 jjanke Exp $
 *
 * @author Teo Sarca, SC ARHIPAC SERVICE SRL
 * 			<li>BF [ 1896947 ] Generate invoice from Order error
 * 			<li>BF [ 2007837 ] VCreateFrom.save() should run in trx
 *
 * @author Hideaki Hagiwara
 *
 */
public abstract class JPiereCreateFromShipment extends CreateFrom
{
	protected int shipLocator_ID=0;
	protected boolean isShipFromScheduledShipLocator=true;
	protected boolean isSelectPhysicalWarehouse=false;
	protected int Doc_PhysicalWarehouse_ID = 0;

	/**
	 *  Protected Constructor
	 *  @param mTab MTab
	 */
	public JPiereCreateFromShipment(GridTab mTab)
	{
		super(mTab);
		if (log.isLoggable(Level.INFO)) log.info(mTab.toString());
	}   //  VCreateFromShipment

	/**
	 *  Dynamic Init
	 *  @return true if initialized
	 */
	public boolean dynInit() throws Exception
	{
		log.config("");
		setTitle(Msg.getMsg(Env.getCtx(), "JP_Shipment_Doc", true) + " .. " + Msg.translate(Env.getCtx(), "CreateFrom"));

		return true;
	}   //  dynInit


	private class IOLineOrderLineSummary
	{
		int C_OrderLine_ID = 0;
		BigDecimal QtyEntered = Env.ZERO;
		int C_UOM_ID = 0;

		public IOLineOrderLineSummary(int C_OrderLine_ID, BigDecimal QtyEntered, int C_UOM_ID)
		{
			this. C_OrderLine_ID =  C_OrderLine_ID;
			this.QtyEntered = QtyEntered;
			this.C_UOM_ID = C_UOM_ID;
		}
	}

	/**
	 *  Load Data - Order
	 *  @param C_Order_ID Order
	 *  @param forInvoice true if for invoice vs. delivery qty
	 */
	protected Vector<Vector<Object>> getOrderData (int C_Order_ID, boolean forInvoice)
	{

		//Objective of this SQL is to exclude Order Lines that are contained Shipment Lines already.
		StringBuilder preSQL = new StringBuilder("SELECT iol.C_OrderLine_ID, SUM(iol.QtyEntered), iol.C_UOM_ID FROM M_InOutLine iol INNER JOIN M_InOut io ON(io.M_InOut_ID = iol.M_InOut_ID) "
													+" WHERE iol.M_InOut_ID=? GROUP BY C_OrderLine_ID, C_UOM_ID");
		PreparedStatement prePSTMT = null;
		ResultSet preRS = null;
		ArrayList<IOLineOrderLineSummary> IOLineOrderLineSummary_list = new ArrayList<IOLineOrderLineSummary>();
		int M_InOut_ID = ((Integer) getGridTab().getValue("M_InOut_ID")).intValue();
		try{

			prePSTMT = DB.prepareStatement(preSQL.toString(), null);
			prePSTMT.setInt(1, M_InOut_ID);
			preRS = prePSTMT.executeQuery();
			while (preRS.next())
				IOLineOrderLineSummary_list.add(new IOLineOrderLineSummary (preRS.getInt(1), preRS.getBigDecimal(2), preRS.getInt(3)));

		}catch (SQLException e){
			log.log(Level.SEVERE, preSQL.toString(), e);
//			throw new DBException(e, preSQL.toString());
		}finally{
			DB.close(preRS, prePSTMT);
			preRS = null; prePSTMT = null;
		}


		/**
		 *  Selected        - 0
		 *  Qty             - 1
		 *  Multiplier		- 2
		 *  C_UOM_ID        - 3
		 *  UOM Symbol Name - 4
		 *  M_Locator_ID    - 5
		 *  Locator Value   - 6
		 *  M_Product_ID    - 7
		 *  Product or Chage Name - 8
		 *  Product Value   - 9
		 *  OrderLine       - 10
		 *  ORder Line No   - 11
		 *  JP_PhysicalWarehouse_ID - 12
		 *  PhysicalWarehouse - Name
		 */
		if (log.isLoggable(Level.CONFIG)) log.config("C_Order_ID=" + C_Order_ID);
		p_order = new MOrder (Env.getCtx(), C_Order_ID, null);      //  save

		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		StringBuilder sql = new StringBuilder("SELECT"
				+ " l.QtyOrdered - l.QtyDelivered" //1
				+ " ,CASE WHEN l.QtyOrdered=0 THEN 0 ELSE l.QtyEntered/l.QtyOrdered END "	//	2 - multiplier
				+ " ,l.C_UOM_ID,COALESCE(uom.UOMSymbol,uom.Name)"			//	3..4
				+ " ,l.JP_Locator_ID, loc.Value " // 5..6
				+ " ,COALESCE(l.M_Product_ID,0),COALESCE(p.Name,c.Name) " //	7..8
				+ " ,p.Value AS ProductValue " // 9
				+ " ,l.C_OrderLine_ID,l.Line "	//	10..11
				+ " ,loc.JP_PhysicalWarehouse_ID, pwh.name " //12..13
				+ "FROM C_OrderLine l");
		sql.append(" LEFT OUTER JOIN M_Product p ON (l.M_Product_ID=p.M_Product_ID)"
				+ " LEFT OUTER JOIN M_Locator loc on (l.JP_Locator_ID=loc.M_Locator_ID)"
				+ " LEFT OUTER JOIN JP_PhysicalWarehouse pwh on (loc.JP_PhysicalWarehouse_ID=pwh.JP_PhysicalWarehouse_ID)"
				+ " LEFT OUTER JOIN C_Charge c ON (l.C_Charge_ID=c.C_Charge_ID)");
		if (Env.isBaseLanguage(Env.getCtx(), "C_UOM"))
			sql.append(" LEFT OUTER JOIN C_UOM uom ON (l.C_UOM_ID=uom.C_UOM_ID)");
		else
			sql.append(" LEFT OUTER JOIN C_UOM_Trl uom ON (l.C_UOM_ID=uom.C_UOM_ID AND uom.AD_Language='")
			.append(Env.getAD_Language(Env.getCtx())).append("')");
		//
		sql.append(" WHERE l.C_Order_ID=? ");		//	#1
		if(isSelectPhysicalWarehouse)
			sql.append(" AND loc.JP_PhysicalWarehouse_ID=? ");
		sql.append(" ORDER BY l.Line ");
		//
		if (log.isLoggable(Level.FINER)) log.finer(sql.toString());
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql.toString(), null);
			pstmt.setInt(1, C_Order_ID);
			if(isSelectPhysicalWarehouse)
				pstmt.setInt(2, Doc_PhysicalWarehouse_ID);
			rs = pstmt.executeQuery();
			boolean isContain = false;
			while (rs.next())
			{
				isContain = false;
				for(IOLineOrderLineSummary olSum : IOLineOrderLineSummary_list)
				{
					if(olSum.C_OrderLine_ID == rs.getInt(10)
							&& olSum.C_UOM_ID == rs.getInt(3) )
					{
						isContain = true;
						BigDecimal qtyOrdered = rs.getBigDecimal(1);
						BigDecimal multiplier = rs.getBigDecimal(2);
						BigDecimal qtyEntered = qtyOrdered.multiply(multiplier).subtract(olSum.QtyEntered);
						if(qtyEntered.compareTo(Env.ZERO)==0)
							break;

						Vector<Object> line = new Vector<Object>();
						line.add(new Boolean(false));           //  0-Selection
						KeyNamePair pp = new KeyNamePair(rs.getInt(10), rs.getString(11));
						line.add(pp);                           //  1-OrderLine
						line.add(qtyEntered);  //  2-Qty
						pp = new KeyNamePair(rs.getInt(3), rs.getString(4).trim());
						line.add(pp);                           //  3-UOM
						// Add product
						line.add(rs.getString(9));				// 4-Product Value
						pp = new KeyNamePair(rs.getInt(7), rs.getString(8));
						line.add(pp);                           //  5-Product Name
						// Add locator
						pp = new KeyNamePair(rs.getInt(5), rs.getString(6));
						line.add(pp);// 6-Locator
						// Add Physical Warehouse
						pp = new KeyNamePair(rs.getInt(12), rs.getString(13));
						line.add(pp);// 7-Phsical Warehouse

						data.add(line);
						break;
					}
				}
				if(isContain)
					continue;


				Vector<Object> line = new Vector<Object>();
				line.add(new Boolean(false));           //  0-Selection
				KeyNamePair pp = new KeyNamePair(rs.getInt(10), rs.getString(11));
				line.add(pp);                           //  1-OrderLine
				BigDecimal qtyOrdered = rs.getBigDecimal(1);
				BigDecimal multiplier = rs.getBigDecimal(2);
				BigDecimal qtyEntered = qtyOrdered.multiply(multiplier);
				line.add(qtyEntered);  //  2-Qty
				 pp = new KeyNamePair(rs.getInt(3), rs.getString(4).trim());
				line.add(pp);                           //  3-UOM
				// Add product
				line.add(rs.getString(9));				// 4-Product Value
				pp = new KeyNamePair(rs.getInt(7), rs.getString(8));
				line.add(pp);                           //  5-Product Name
				// Add locator
				pp = new KeyNamePair(rs.getInt(5), rs.getString(6));
				line.add(pp);// 6-Locator

				// Add Physical Warehouse
				pp = new KeyNamePair(rs.getInt(12), rs.getString(13));
				line.add(pp);// 7-Phsical Warehouse

				data.add(line);
			}
		}
		catch (SQLException e)
		{
			log.log(Level.SEVERE, sql.toString(), e);
			//throw new DBException(e, sql.toString());
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}
		return data;
	}   //  LoadOrder

	/**
	 * Get KeyNamePair for Locator.
	 * If no locator specified or the specified locator is not valid (e.g. warehouse not match),
	 * a default one will be used.
	 * @param M_Locator_ID
	 * @return KeyNamePair
	 */
	protected KeyNamePair getLocatorKeyNamePair(int M_Locator_ID)
	{
		MLocator locator = null;

		// Load desired Locator
		if (M_Locator_ID > 0)
		{
			locator = MLocator.get(Env.getCtx(), M_Locator_ID);
			// Validate warehouse
			if (locator != null && locator.getM_Warehouse_ID() != getM_Warehouse_ID())
			{
				locator = null;
			}
		}else{
			KeyNamePair pp = null ;
			pp = new KeyNamePair(0, "");
			return pp;
		}

		// Try to use default locator from Order Warehouse
		if (locator == null && p_order != null && p_order.getM_Warehouse_ID() == getM_Warehouse_ID())
		{
			MWarehouse wh = MWarehouse.get(Env.getCtx(), p_order.getM_Warehouse_ID());
			if (wh != null)
			{
				locator = wh.getDefaultLocator();
			}
		}
		// Try to get from locator field
		if (locator == null)
		{
			if (shipLocator_ID > 0)
			{
				locator = MLocator.get(Env.getCtx(), shipLocator_ID);
			}
		}
		// Validate Warehouse
		if (locator == null || locator.getM_Warehouse_ID() != getM_Warehouse_ID())
		{
			locator = MWarehouse.get(Env.getCtx(), getM_Warehouse_ID()).getDefaultLocator();
		}

		KeyNamePair pp = null ;
		if (locator != null)
		{
			pp = new KeyNamePair(locator.get_ID(), locator.getValue());
		}
		return pp;
	}

	/**
	 *  List number of rows selected
	 */
	public void info(IMiniTable miniTable, IStatusBar statusBar)
	{

	}   //  infoInvoice

	protected void configureMiniTable (IMiniTable miniTable)
	{
		miniTable.setColumnClass(0, Boolean.class, false);    	 //  Selection
		miniTable.setColumnClass(1, String.class, true);    	 //  Order Line
		miniTable.setColumnClass(2, BigDecimal.class, false);    //  Qty
		miniTable.setColumnClass(3, String.class, true);         //  UOM
		miniTable.setColumnClass(4, String.class, true); 		//  Product Value
		miniTable.setColumnClass(5, String.class, true);   		//  Product Name
		miniTable.setColumnClass(6, String.class, false); 		 //  Locator
		miniTable.setColumnClass(7, String.class, false); 		 //  Physical Warehouse

		//  Table UI
		miniTable.autoSize();

	}

	protected Vector<String> getOISColumnNames()
	{
		//  Header Info
	    Vector<String> columnNames = new Vector<String>(8);
	    columnNames.add(Msg.getMsg(Env.getCtx(), "Select"));
	    columnNames.add(Msg.getElement(Env.getCtx(), "Line", true));
	    columnNames.add(Msg.translate(Env.getCtx(), "Quantity"));
	    columnNames.add(Msg.translate(Env.getCtx(), "C_UOM_ID"));
	    columnNames.add(Msg.getElement(Env.getCtx(), "ProductValue", false));
	    columnNames.add(Msg.translate(Env.getCtx(), "M_Product_ID"));
	    columnNames.add(Msg.getMsg(Env.getCtx(), "JP_ScheduledShipLocator"));
	    columnNames.add(Msg.getElement(Env.getCtx(), "JP_PhysicalWarehouse_ID"));

	    return columnNames;
	}

	/**
	 *  Save - Create Invoice Lines
	 *  @return true if saved
	 */
	public boolean save(IMiniTable miniTable, String trxName)
	{
		int M_Locator_ID = shipLocator_ID;
		if (!isShipFromScheduledShipLocator && M_Locator_ID == 0) 	//Check Locator
		{
			FDialog.error(0, Msg.getMsg(Env.getCtx(), "FillMandatory") + Msg.getElement(Env.getCtx(), "M_Locator_ID"));
			return false;

		}else{

			for (int i = 0; i < miniTable.getRowCount(); i++)
			{
				if (((Boolean)miniTable.getValueAt(i, 0)).booleanValue())
				{
					KeyNamePair pp = (KeyNamePair) miniTable.getValueAt(i, 6); // Locator
					int JP_ScheduledShipLocator_ID = pp.getKey();
					if(JP_ScheduledShipLocator_ID == 0 && M_Locator_ID == 0)
					{
						pp = (KeyNamePair) miniTable.getValueAt(i, 1); // OrderLine

						FDialog.error(0, Msg.getMsg(Env.getCtx(), "FillMandatory") + Msg.getElement(Env.getCtx(), "M_Locator_ID")
											+ System.lineSeparator()
											+ Msg.getElement(Env.getCtx(), "Line")
											+ " : " + pp.getName());
						return false;
					}
				}
			}//for

		}//if (M_Locator_ID == 0) 	//Check Locator

		// Get Shipment
		int M_InOut_ID = ((Integer) getGridTab().getValue("M_InOut_ID")).intValue();
		MInOut inout = new MInOut(Env.getCtx(), M_InOut_ID, trxName);
		if (log.isLoggable(Level.CONFIG)) log.config(inout + ", C_Locator_ID=" + M_Locator_ID);

		// Lines
		for (int i = 0; i < miniTable.getRowCount(); i++)
		{
			if (((Boolean)miniTable.getValueAt(i, 0)).booleanValue())
			{
				// variable values
				BigDecimal QtyEntered = (BigDecimal) miniTable.getValueAt(i, 2); // Qty
				KeyNamePair pp = (KeyNamePair) miniTable.getValueAt(i, 3); // UOM
				int C_UOM_ID = pp.getKey();

				pp = (KeyNamePair) miniTable.getValueAt(i, 5); // Product
				int M_Product_ID = pp.getKey();

				pp = (KeyNamePair) miniTable.getValueAt(i, 6); // Locator
				int JP_ScheduledShipLocator＿ID = pp.getKey();

				int C_OrderLine_ID = 0;
				pp = (KeyNamePair) miniTable.getValueAt(i, 1); // OrderLine
				if (pp != null)
					C_OrderLine_ID = pp.getKey();

				//	Precision of Qty UOM
				int precision = 2;
				if (M_Product_ID != 0)
				{
					MProduct product = MProduct.get(Env.getCtx(), M_Product_ID);
					precision = product.getUOMPrecision();
				}
				QtyEntered = QtyEntered.setScale(precision, BigDecimal.ROUND_HALF_DOWN);
				//
				if (log.isLoggable(Level.FINE)) log.fine("Line QtyEntered=" + QtyEntered
						+ ", Product=" + M_Product_ID+ ", OrderLine=" + C_OrderLine_ID);

				//	Create new InOut Line
				MInOutLine iol = new MInOutLine (inout);
				iol.setM_Product_ID(M_Product_ID, C_UOM_ID);	//	Line UOM
				iol.setQty(QtyEntered);							//	Movement/Entered
				//
				MOrderLine ol = null;
				if (C_OrderLine_ID != 0)
				{
					iol.setC_OrderLine_ID(C_OrderLine_ID);
					ol = new MOrderLine (Env.getCtx(), C_OrderLine_ID, trxName);

					//JPIERE-0294
					iol.set_ValueNoCheck("JP_ProductExplodeBOM_ID", ol.get_Value("JP_ProductExplodeBOM_ID") );

					if (ol.getQtyEntered().compareTo(ol.getQtyOrdered()) != 0)
					{
						iol.setMovementQty(QtyEntered
								.multiply(ol.getQtyOrdered())
								.divide(ol.getQtyEntered(), 12, BigDecimal.ROUND_HALF_UP));
						iol.setC_UOM_ID(ol.getC_UOM_ID());
					}
					iol.setM_AttributeSetInstance_ID(ol.getM_AttributeSetInstance_ID());
					iol.setDescription(ol.getDescription());
					//
					iol.setC_Project_ID(ol.getC_Project_ID());
					iol.setC_ProjectPhase_ID(ol.getC_ProjectPhase_ID());
					iol.setC_ProjectTask_ID(ol.getC_ProjectTask_ID());
					iol.setC_Activity_ID(ol.getC_Activity_ID());
					iol.setC_Campaign_ID(ol.getC_Campaign_ID());
					iol.setAD_OrgTrx_ID(ol.getAD_OrgTrx_ID());
					iol.setUser1_ID(ol.getUser1_ID());
					iol.setUser2_ID(ol.getUser2_ID());
				}


				//	Charge
				if (M_Product_ID == 0)
				{
					if (ol != null && ol.getC_Charge_ID() != 0)			//	from order
						iol.setC_Charge_ID(ol.getC_Charge_ID());
				}
				// Set locator
				if(isShipFromScheduledShipLocator && JP_ScheduledShipLocator＿ID > 0)
				{
					iol.setM_Locator_ID(JP_ScheduledShipLocator＿ID);
				}else if(M_Locator_ID > 0){
					iol.setM_Locator_ID(M_Locator_ID);
				}else{
					return false;
				}

				int aa = iol.getLine();
				iol.saveEx();

			}   //   if selected
		}   //  for all rows

		/**
		 *  Update Header
		 *  - if linked to another order/invoice/rma - remove link
		 *  - if no link set it
		 */
		if (p_order != null && p_order.getC_Order_ID() != 0 && inout.getC_Order_ID()==0)
		{
			inout.setC_Order_ID (p_order.getC_Order_ID());
			inout.setAD_OrgTrx_ID(p_order.getAD_OrgTrx_ID());
			inout.setC_Project_ID(p_order.getC_Project_ID());
			inout.setC_Campaign_ID(p_order.getC_Campaign_ID());
			inout.setC_Activity_ID(p_order.getC_Activity_ID());
			inout.setUser1_ID(p_order.getUser1_ID());
			inout.setUser2_ID(p_order.getUser2_ID());

			if ( p_order.isDropShip() )
			{
				inout.setM_Warehouse_ID( p_order.getM_Warehouse_ID() );
				inout.setIsDropShip(p_order.isDropShip());
				inout.setDropShip_BPartner_ID(p_order.getDropShip_BPartner_ID());
				inout.setDropShip_Location_ID(p_order.getDropShip_Location_ID());
				inout.setDropShip_User_ID(p_order.getDropShip_User_ID());
			}
		}

		inout.saveEx();
		return true;

	}   //  saveInvoice


	protected Vector<Vector<Object>> getOrderData (int C_Order_ID, boolean forInvoice, int M_Locator_ID)
	{
		shipLocator_ID = M_Locator_ID;
		return getOrderData (C_Order_ID, forInvoice);
	}

}
