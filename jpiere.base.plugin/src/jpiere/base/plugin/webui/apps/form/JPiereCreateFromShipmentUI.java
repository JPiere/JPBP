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
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.apps.form.WCreateFromWindow;
import org.adempiere.webui.component.Checkbox;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.ListItem;
import org.adempiere.webui.component.ListModelTable;
import org.adempiere.webui.component.Listbox;
import org.adempiere.webui.component.ListboxFactory;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.editor.WEditor;
import org.adempiere.webui.editor.WSearchEditor;
import org.adempiere.webui.editor.WStringEditor;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.compiere.model.GridTab;
import org.compiere.model.MColumn;
import org.compiere.model.MLocator;
import org.compiere.model.MLookup;
import org.compiere.model.MLookupFactory;
import org.compiere.model.MProduct;
import org.compiere.model.MSysConfig;
import org.compiere.model.MWarehouse;
import org.compiere.util.CLogger;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;
import org.compiere.util.Msg;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Space;
import org.zkoss.zul.Vlayout;

import jpiere.base.plugin.org.adempiere.model.MPhysicalWarehouse;

/**
 * JPIERE-0145:Create Shipment from Sales Orders
 * 
 * @author Low Heng Sin
 * @author Hideaki Hagiwara
 *
 */
public class JPiereCreateFromShipmentUI extends JPiereCreateFromShipment implements EventListener<Event>, ValueChangeListener
{

	private WCreateFromWindow window;

	public JPiereCreateFromShipmentUI(GridTab tab)
	{
		super(tab);
		log.info(getGridTab().toString());

		window = new WCreateFromWindow(this, getGridTab().getWindowNo());

		p_WindowNo = getGridTab().getWindowNo();

		try
		{
			if (!dynInit())
				return;
			zkInit();
			setInitOK(true);
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE, "", e);
			setInitOK(false);
			throw new AdempiereException(e.getMessage());
		}
		AEnv.showWindow(window);
	}

	/** Window No               */
	private int p_WindowNo;

	/**	Logger			*/
	private CLogger log = CLogger.getCLogger(getClass());

	protected Label bPartnerLabel = new Label();
	protected WEditor bPartnerField;

	protected Label orderLabel = new Label();
	protected Listbox orderField = ListboxFactory.newDropdownListbox();

	protected Checkbox sameWarehouseCb = new Checkbox();
	
	protected Checkbox shipFromScheduledShipLocatorCb = new Checkbox();
	
	protected Checkbox selectPhysicalWarehouseCb = new Checkbox();
	
	protected Label locatorLabel = new Label();
	protected WSearchEditor locatorField = null;
	
	protected Label upcLabel = new Label();
	protected WStringEditor upcField = new WStringEditor();

	/**
	 *  Dynamic Init
	 *  @throws Exception if Lookups cannot be initialized
	 *  @return true if initialized
	 */
	public boolean dynInit() throws Exception
	{
		log.config("");

		super.dynInit();

		window.setTitle(getTitle());

		sameWarehouseCb.setSelected(true);
		sameWarehouseCb.addActionListener(this);
		
		shipFromScheduledShipLocatorCb.setSelected(true);
		shipFromScheduledShipLocatorCb.addActionListener(this);
		
		boolean checkSelectPhyWH = MSysConfig.getBooleanValue("JP_CREATE_FROM_SHIP_SELECT_PHYHW_CHECK", false, Env.getAD_Client_ID(Env.getCtx()),Env.getAD_Org_ID(Env.getCtx()) );
		selectPhysicalWarehouseCb.setSelected(checkSelectPhyWH);
		isSelectPhysicalWarehouse = checkSelectPhyWH;
		selectPhysicalWarehouseCb.addActionListener(this);
		
		//  load Locator
		int AD_Column_ID = MColumn.getColumn_ID("M_InOutLine", "M_Locator_ID");
		MLookup lookupLocator = MLookupFactory.get(Env.getCtx(), p_WindowNo, 0, AD_Column_ID, DisplayType.Search);
		locatorField = new WSearchEditor("M_Locator_ID", true, false, true, lookupLocator);
		Doc_PhysicalWarehouse_ID = Env.getContextAsInt(Env.getCtx(), p_WindowNo, "JP_PhysicalWarehouse_ID");
		if(Doc_PhysicalWarehouse_ID == 0)
		{
			MWarehouse wh = MWarehouse.get(Env.getCtx(), Env.getContextAsInt(Env.getCtx(),p_WindowNo, "M_Warehouse_ID"));
			if (wh != null)
			{
				MLocator locator = wh.getDefaultLocator();
				if(locator != null)
				{
					locatorField.setValue(locator.getM_Locator_ID());
					shipLocator_ID = locator.getM_Locator_ID();
				}
			}
		}else{
			MPhysicalWarehouse phyWH = MPhysicalWarehouse.get(Env.getCtx(), Doc_PhysicalWarehouse_ID);
			MLocator locator = phyWH.getDefaultLocator(MWarehouse.get(Env.getCtx(), Env.getContextAsInt(Env.getCtx(),p_WindowNo, "M_Warehouse_ID")) );
			if(locator != null)
			{
				locatorField.setValue(locator.getM_Locator_ID());
				shipLocator_ID = locator.getM_Locator_ID();
			}
		}
		locatorField.addValueChangeListener(this);

		initBPartner(false);
		bPartnerField.addValueChangeListener(this);
		locatorLabel.setMandatory(true);

		upcField = new WStringEditor ("UPC", false, false, true, 10, 30, null, null);
		upcField.getComponent().addEventListener(Events.ON_CHANGE, this);

		return true;
	}   //  dynInit

	protected void zkInit() throws Exception
	{
       	bPartnerLabel.setText(Msg.getElement(Env.getCtx(), "C_BPartner_ID"));
		orderLabel.setText(Msg.getElement(Env.getCtx(), "C_Order_ID", true));
		locatorLabel.setText(Msg.getMsg(Env.getCtx(), "JP_ShipLocator"));
        sameWarehouseCb.setText(Msg.getMsg(Env.getCtx(), "JP_FromSameWarehouseOnly", true));
        sameWarehouseCb.setTooltiptext(Msg.getMsg(Env.getCtx(), "JP_FromSameWarehouseOnly", true));
        shipFromScheduledShipLocatorCb.setText(Msg.getMsg(Env.getCtx(), "JP_ShipFromScheduledShipLocator", true));
        selectPhysicalWarehouseCb.setText(Msg.getMsg(Env.getCtx(), "JP_SelectByPhyWH", true));
        
        upcLabel.setText(Msg.getElement(Env.getCtx(), "UPC", false));

		Vlayout vlayout = new Vlayout();
		vlayout.setVflex("1");
		vlayout.setWidth("98%");
    	Panel parameterPanel = window.getParameterPanel();
		parameterPanel.appendChild(vlayout);

		Grid parameterStdLayout = GridFactory.newGridLayout();
    	vlayout.appendChild(parameterStdLayout);

		Rows rows = (Rows) parameterStdLayout.newRows();
		Row row = rows.newRow();
		row.appendCellChild(bPartnerLabel.rightAlign(),2);			//2
		if (bPartnerField != null) 
		{
			row.appendCellChild(bPartnerField.getComponent(), 10);	//12
			bPartnerField.fillHorizontal();
		}

		row = rows.newRow();
		row.appendCellChild(new Space(),2);				//2
		row.appendCellChild(sameWarehouseCb,3);			//5
		row.appendCellChild(new Space());				//6
		row.appendCellChild(orderLabel.rightAlign(),2);	//8
		row.appendCellChild(orderField,4);				//12
		orderField.setHflex("1");
		
		row = rows.newRow();
		row.appendCellChild(new Space(),2);			
		row.appendCellChild(shipFromScheduledShipLocatorCb,3);
       	isShipFromScheduledShipLocator = shipFromScheduledShipLocatorCb.isSelected();
		row.appendCellChild(new Space());
		row.appendCellChild(locatorLabel.rightAlign(),2);
		row.appendCellChild(locatorField.getComponent(),4);
		locatorField.fillHorizontal();
		
		row = rows.newRow();
		row.appendCellChild(new Space(),2);	
		row.appendCellChild(selectPhysicalWarehouseCb,3);
		isSelectPhysicalWarehouse = selectPhysicalWarehouseCb.isSelected();
		if(Doc_PhysicalWarehouse_ID==0)
			selectPhysicalWarehouseCb.setVisible(false);
			
		row.appendCellChild(new Space());
		row.appendCellChild(upcLabel.rightAlign(),2);
		row.appendCellChild(upcField.getComponent(),4);
		upcField.fillHorizontal();

	}

	private boolean 	m_actionActive = false;

	/**
	 *  Action Listener
	 *  @param e event
	 * @throws Exception
	 */
	public void onEvent(Event e) throws Exception
	{
		if (m_actionActive)
			return;
		m_actionActive = true;

		//  Order
		if (e.getTarget().equals(orderField))
		{
			KeyNamePair pp = orderField.getSelectedItem().toKeyNamePair();
			if (pp == null || pp.getKey() == 0)
				;
			else
			{
				int C_Order_ID = pp.getKey();
				loadOrder(C_Order_ID, false, locatorField.getValue()!=null?((Integer)locatorField.getValue()).intValue():0);
			}
		}
		//sameWarehouseCb
        else if (e.getTarget().equals(sameWarehouseCb))
        {
        	initBPOrderDetails(((Integer)bPartnerField.getValue()).intValue(), false);
        }
		//shipFromScheduledShipLocatorCb
        else if (e.getTarget().equals(shipFromScheduledShipLocatorCb))
        {
           	isShipFromScheduledShipLocator = shipFromScheduledShipLocatorCb.isSelected();
        }
        else if (e.getTarget().equals(selectPhysicalWarehouseCb))
        {
        	isSelectPhysicalWarehouse = selectPhysicalWarehouseCb.isSelected(); 
    		ListItem selectedListItem = orderField.getSelectedItem();
    		int C_Order_ID = ((Integer)selectedListItem.getValue()).intValue();
    		if(C_Order_ID > 0)
    			loadOrder(C_Order_ID, false, locatorField.getValue()!=null?((Integer)locatorField.getValue()).intValue():0);
    		else if(C_Order_ID <= 0)
    			orderField.setSelectedIndex(0);
    		
    		orderField.addActionListener(this);
        }
		else if (e.getTarget().equals(upcField.getComponent()))
		{
			checkProductUsingUPC();
		}

		m_actionActive = false;
	}

	/**
	 * Checks the UPC value and checks if the UPC matches any of the products in the
	 * list.
	 */
	private void checkProductUsingUPC()
	{
		String upc = upcField.getDisplay();
		//DefaultTableModel model = (DefaultTableModel) dialog.getMiniTable().getModel();
		ListModelTable model = (ListModelTable) window.getWListbox().getModel();

		// Lookup UPC
		List<MProduct> products = MProduct.getByUPC(Env.getCtx(), upc, null);
		for (MProduct product : products)
		{
			int row = findProductRow(product.get_ID());
			if (row >= 0)
			{
				BigDecimal qty = (BigDecimal)model.getValueAt(row, 2);
				model.setValueAt(qty, row, 2);
				model.setValueAt(Boolean.TRUE, row, 0);
				model.updateComponent(row, row);
			}
		}
		upcField.setValue("");
	}

	/**
	 * Finds the row where a given product is. If the product is not found
	 * in the table -1 is returned.
	 * @param M_Product_ID
	 * @return  Row of the product or -1 if non existing.
	 *
	 */
	private int findProductRow(int M_Product_ID)
	{	
		//DefaultTableModel model = (DefaultTableModel)dialog.getMiniTable().getModel();
		ListModelTable model = (ListModelTable) window.getWListbox().getModel();		
		KeyNamePair kp;
		for (int i=0; i<model.getRowCount(); i++) {
			kp = (KeyNamePair)model.getValueAt(i, 5);
			if (kp.getKey()==M_Product_ID) {
				return(i);
			}
		}
		return(-1);
	}

	/**
	 *  Change Listener
	 *  @param e event
	 */
	public void valueChange (ValueChangeEvent e)
	{
		if (log.isLoggable(Level.CONFIG)) log.config(e.getPropertyName() + "=" + e.getNewValue());

		//  BPartner - load Order/Invoice/Shipment
		if (e.getPropertyName().equals("C_BPartner_ID"))
		{
			int C_BPartner_ID = 0;
			if (e.getNewValue() != null){
				C_BPartner_ID = ((Integer)e.getNewValue()).intValue();
			}

			initBPOrderDetails (C_BPartner_ID, true);
		}else if(e.getPropertyName().equals("M_Locator_ID")){
			if (e.getNewValue() != null){
				shipLocator_ID = ((Integer)e.getNewValue()).intValue();
			}else{
				shipLocator_ID = 0;
			}
		}
		window.tableChanged(null);
	}   //  vetoableChange

	/**************************************************************************
	 *  Load BPartner Field
	 *  @param forInvoice true if Invoices are to be created, false receipts
	 *  @throws Exception if Lookups cannot be initialized
	 */
	protected void initBPartner (boolean forInvoice) throws Exception
	{
		//  load BPartner
		int AD_Column_ID = 3499;        //  C_Invoice.C_BPartner_ID
		MLookup lookup = MLookupFactory.get (Env.getCtx(), p_WindowNo, 0, AD_Column_ID, DisplayType.Search);
		bPartnerField = new WSearchEditor ("C_BPartner_ID", true, true, true, lookup);
		//
		int C_BPartner_ID = Env.getContextAsInt(Env.getCtx(), p_WindowNo, "C_BPartner_ID");
		bPartnerField.setValue(new Integer(C_BPartner_ID));

		//  initial loading
		initBPOrderDetails(C_BPartner_ID, forInvoice);
	}   //  initBPartner

	/**
	 *  Load PBartner dependent Order/Invoice/Shipment Field.
	 *  @param C_BPartner_ID BPartner
	 *  @param forInvoice for invoice
	 */
	protected void initBPOrderDetails (int C_BPartner_ID, boolean forInvoice)
	{
		if (log.isLoggable(Level.CONFIG)) log.config("C_BPartner_ID=" + C_BPartner_ID);
		KeyNamePair pp = new KeyNamePair(0,"");
		//  load PO Orders - Closed, Completed
		orderField.removeActionListener(this);
		orderField.removeAllItems();
		orderField.addItem(pp);

		ArrayList<KeyNamePair> list = loadOrderData(C_BPartner_ID, forInvoice, sameWarehouseCb.isSelected());
		int C_Order_ID = Env.getContextAsInt(Env.getCtx(), p_WindowNo, "C_Order_ID");
		int i = 0;
		for(KeyNamePair knp : list)
		{
			i++;
			orderField.addItem(knp);
			if(knp.getKey()==C_Order_ID && C_Order_ID > 0)
			{
				orderField.setSelectedIndex(i);
				loadOrder(C_Order_ID, false, locatorField.getValue()!=null?((Integer)locatorField.getValue()).intValue():0);
			}
		}
		
		if(C_Order_ID <= 0)
			orderField.setSelectedIndex(0);
		
		orderField.addActionListener(this);

	}   //  initBPartnerOIS

	/**
	 *  Load Data - Order
	 *  @param C_Order_ID Order
	 *  @param forInvoice true if for invoice vs. delivery qty
	 *  @param M_Locator_ID
	 */
	protected void loadOrder (int C_Order_ID, boolean forInvoice, int M_Locator_ID)
	{
		loadTableOIS(getOrderData(C_Order_ID, forInvoice, M_Locator_ID));
	}   //  LoadOrder


	/**
	 *  Load Order/Invoice/Shipment data into Table
	 *  @param data data
	 */
	protected void loadTableOIS (Vector<?> data)
	{
		window.getWListbox().clear();

		//  Remove previous listeners
		window.getWListbox().getModel().removeTableModelListener(window);
		//  Set Model
		ListModelTable model = new ListModelTable(data);
		model.addTableModelListener(window);
		window.getWListbox().setData(model, getOISColumnNames());
		//

		configureMiniTable(window.getWListbox());
	}   //  loadOrder

	public void showWindow()
	{
		window.setVisible(true);
	}

	public void closeWindow()
	{
		window.dispose();
	}

	@Override
	public Object getWindow() {
		return window;
	}
}
