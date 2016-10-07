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

package jpiere.base.plugin.org.adempiere.webui.editor;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

import org.adempiere.base.IDisplayTypeFactory;
import org.adempiere.base.Service;
import org.adempiere.webui.ValuePreference;
import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.component.NumberBox;
import org.adempiere.webui.editor.WEditor;
import org.adempiere.webui.editor.WEditorPopupMenu;
import org.adempiere.webui.event.ContextMenuEvent;
import org.adempiere.webui.event.ContextMenuListener;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.window.WFieldRecordInfo;
import org.compiere.model.GridField;
import org.compiere.model.MCurrency;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Language;
import org.compiere.util.Util;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;

/**
 *
 * @author  <a href="mailto:agramdass@gmail.com">Ashley G Ramdass</a>
 * @date    Mar 11, 2007
 * @version $Revision: 0.10 $
 *
 * @author Low Heng Sin
 * @author Cristina Ghita, www.arhipac.ro
 *  	   <li> BF [3058780] WNumberEditor allow only BigDecimal
 *  	   @see https://sourceforge.net/tracker/?func=detail&aid=3058780&group_id=176962&atid=955896
 */
public class WNumberEditorJP extends WEditor implements ContextMenuListener
{
    public static final String[] LISTENER_EVENTS = {Events.ON_CHANGE, Events.ON_OK};

    public static final int MAX_DISPLAY_LENGTH = 35;
    public static final int MIN_DISPLAY_LENGTH = 11;

    private Object oldValue;

	private int displayType;

	private boolean tableEditor;

	private String originalStyle;

    public WNumberEditorJP()
    {
    	this("Number", false, false, true, DisplayType.Number, "");
    }

    /**
    *
    * @param gridField
    */
    public WNumberEditorJP(GridField gridField)
    {
    	this(false, gridField);
    }

    /**
     *
     * @param gridField
     */
    public WNumberEditorJP(boolean tableEditor, GridField gridField)
    {
        super(new NumberBox(gridField.getDisplayType() == DisplayType.Integer, tableEditor),
                gridField);
        this.displayType = gridField.getDisplayType();
        this.tableEditor = tableEditor;
        init();
    }

    /**
     *
     * @param gridField
     * @param integral
     */
    public WNumberEditorJP(GridField gridField, boolean integral)
    {
        super(new NumberBox(integral), gridField);
        this.displayType = integral ? DisplayType.Integer : DisplayType.Number;
        init();
    }

    /**
     *
     * @param columnName
     * @param mandatory
     * @param readonly
     * @param updateable
     * @param displayType
     * @param title
     */
    public WNumberEditorJP(String columnName, boolean mandatory, boolean readonly, boolean updateable,
			int displayType, String title)
    {
		super(new NumberBox(displayType == DisplayType.Integer), columnName, title, null, mandatory,
				readonly, updateable);
		this.displayType = displayType;
		init();
	}

	private void init()
    {
		setChangeEventWhenEditing (true);
		if (gridField != null)
		{
			getComponent().setTooltiptext(gridField.getDescription());
	        int displayLength = gridField.getDisplayLength();
	        if (displayLength > MAX_DISPLAY_LENGTH)
	            displayLength = MAX_DISPLAY_LENGTH;
	        else if (displayLength <= 0 || displayLength < MIN_DISPLAY_LENGTH)
	        	displayLength = MIN_DISPLAY_LENGTH;
	        if (!tableEditor)
	        	getComponent().getDecimalbox().setCols(displayLength);
//	        else
//	        	getComponent().getDecimalbox().setCols(0);
		}

		if (DisplayType.isID(displayType))
			displayType = DisplayType.Integer;
		else if (!DisplayType.isNumeric(displayType))
			displayType = DisplayType.Number;
		// IDEMPIERE-989
		Language lang = AEnv.getLanguage(Env.getCtx());
		DecimalFormat format = DisplayType.getNumberFormat(displayType, lang);
		if (gridField != null && gridField.getFormatPattern() != null)
			getComponent().getDecimalbox().setFormat(gridField.getFormatPattern());
		else
			getComponent().getDecimalbox().setFormat(format.toPattern());
		getComponent().getDecimalbox().setLocale(lang.getLocale());
		getComponent().setFormat(format);

		popupMenu = new WEditorPopupMenu(false, false, isShowPreference());
    	addChangeLogMenu(popupMenu);

    	originalStyle = getComponent().getDecimalbox().getStyle();
    }

	/**
	 * Event handler
	 * @param event
	 */
    public void onEvent(Event event)
    {
    	if (Events.ON_CHANGE.equalsIgnoreCase(event.getName()) || Events.ON_OK.equalsIgnoreCase(event.getName()))
    	{
	        Object newValue = getComponent().getValue();
	        if (oldValue == null && newValue == null) {
	        	return;
	        }

	        if (displayType == DisplayType.Integer) {
		        if (newValue != null && newValue instanceof BigDecimal) {
		        	newValue = new Integer(((BigDecimal)newValue).intValue());
		        }
		        if (oldValue != null && oldValue instanceof BigDecimal) {
		        	oldValue = new Integer(((BigDecimal)oldValue).intValue());
		        }
	        }

	        if (oldValue != null && newValue != null && oldValue.equals(newValue))
	        {
	    	    return;
	    	}

	        //IDEMPIERE-2553 - Enter amounts without decimal separator
	        if(displayType == DisplayType.Amount || displayType == DisplayType.CostPrice){
	        	if (newValue != null && newValue instanceof BigDecimal) {
	        		newValue = addDecimalPlaces((BigDecimal)newValue);
		        }
	        }

	        ValueChangeEvent changeEvent = new ValueChangeEvent(this, this.getColumnName(), oldValue, newValue);
	        super.fireValueChange(changeEvent);
	        oldValue = getComponent().getValue(); // IDEMPIERE-963 - check again the value could be changed by callout
    	}
    }

    /**
     * IDEMPIERE-2553 - Enter amounts without decimal separator
     * @param oldValue
     * @return
     */
    public BigDecimal addDecimalPlaces(BigDecimal oldValue){
    	if(oldValue.toString().contains("."))
    		return oldValue;

    	int decimalPlaces = Env.getContextAsInt(Env.getCtx(), "AutomaticDecimalPlacesForAmoun");
    	if(decimalPlaces <= 0)
    		return oldValue;

    	BigDecimal divisor;
    	if (decimalPlaces == 2) // most common case
    		divisor = Env.ONEHUNDRED;
    	else if (decimalPlaces == 1)
    		divisor = BigDecimal.TEN;
    	else
    		divisor = BigDecimal.TEN.pow(decimalPlaces);
    	BigDecimal newValue = oldValue.divide(divisor, decimalPlaces, BigDecimal.ROUND_HALF_UP);
    	return newValue;
    } //getAddDecimalPlaces

    @Override
	public NumberBox getComponent() {
		return (NumberBox) component;
	}

	@Override
	public boolean isReadWrite() {
		return getComponent().isEnabled();
	}

	@Override
	public void setReadWrite(boolean readWrite) {
		getComponent().setEnabled(readWrite);
	}

	@Override
    public String getDisplay()
    {
        return getComponent().getText();
    }

    @Override
    public Object getValue()
    {
        return getComponent().getValue();
    }

    @Override
    public void setValue(Object value)
    {
    	if (value == null)
    		oldValue = null;
    	else if (value instanceof BigDecimal)
    	{ 										//JPIERE-3 Modify WNumberEditor#setValue() by Hideaki Hagiwara
    		oldValue = (BigDecimal) value;
    		if(gridField != null && displayType==DisplayType.Amount)
    		{
	    		DecimalFormat format = null;
				List<IDisplayTypeFactory> factoryList = Service.locator().list(IDisplayTypeFactory.class).getServices();
				for(IDisplayTypeFactory factory : factoryList){
					format = factory.getNumberFormat(displayType, null, gridField.getFormatPattern());
				}

	    		format.setMinimumFractionDigits(MCurrency.getStdPrecision(Env.getCtx(), getC_Currency_ID()));
	    		getComponent().getDecimalbox().setFormat(format.toPattern());
	    		getComponent().setFormat(format);

    		}else if(gridField != null && displayType==DisplayType.CostPrice){
	    		DecimalFormat format = null;
				List<IDisplayTypeFactory> factoryList = Service.locator().list(IDisplayTypeFactory.class).getServices();
				for(IDisplayTypeFactory factory : factoryList){
					format = factory.getNumberFormat(displayType, null, gridField.getFormatPattern());
				}

	    		format.setMinimumFractionDigits(MCurrency.getCostingPrecision(Env.getCtx(), getC_Currency_ID()));
	    		getComponent().getDecimalbox().setFormat(format.toPattern());
	    		getComponent().setFormat(format);
    		}

    	} 										//JPiere-3 Finish
    	else if (value instanceof Number)
    		oldValue = BigDecimal.valueOf(((Number)value).doubleValue());
    	else
    		oldValue = new BigDecimal(value.toString());
    	getComponent().setValue(oldValue);
    }

    @Override
    public String[] getEvents()
    {
        return LISTENER_EVENTS;
    }

    /**
     * Handle context menu events
     * @param evt
     */
    public void onMenu(ContextMenuEvent evt)
	{
	 	if (WEditorPopupMenu.PREFERENCE_EVENT.equals(evt.getContextEvent()))
		{
			if (isShowPreference())
				ValuePreference.start (getComponent(), this.getGridField(), getValue());
			return;
		}
	 	else if (WEditorPopupMenu.CHANGE_LOG_EVENT.equals(evt.getContextEvent()))
		{
			WFieldRecordInfo.start(gridField);
		}
	}

	@Override
	public void setTableEditor(boolean b) {
		super.setTableEditor(b);
		getComponent().setTableEditorMode(b);
	}


	/* (non-Javadoc)
	 * @see org.adempiere.webui.editor.WEditor#setFieldStyle(java.lang.String)
	 */
	@Override
	protected void setFieldStyle(String style) {
		StringBuilder s = new StringBuilder(originalStyle);
		if (!(s.charAt(s.length()-1)==';'))
			s.append(";");
		if (!Util.isEmpty(style))
			s.append(style);
		getComponent().getDecimalbox().setStyle(s.toString());
	}

	/**
	* Get Currency for precision
	*
	* JPIERE-3 Add WNumberEditor#getC_Currency_ID()
	*
	* @author Hideaki Hagiwara
	*/
	public int getC_Currency_ID() {

		int C_Currency_ID = Env.getContextAsInt(Env.getCtx(), getGridField().getWindowNo(), "C_Currency_ID");
		if(C_Currency_ID == 0)
			C_Currency_ID = Env.getContextAsInt(Env.getCtx(), getGridField().getWindowNo(), 0, "C_Currency_ID");
		if(C_Currency_ID == 0)
			C_Currency_ID = Env.getContextAsInt(Env.getCtx(), "$C_Currency_ID");

		return C_Currency_ID;
	}

}
