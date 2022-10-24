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

import org.adempiere.webui.factory.InfoManager;
import org.adempiere.webui.panel.ADForm;
import org.adempiere.webui.panel.CustomForm;
import org.adempiere.webui.panel.InfoPanel;


/**
 *  JPIERE-0514: JPiere Form Info Window
 *
 *  @author Hideaki Hagiwara（h.hagiwara@oss-erp.co.jp）
 *
 */
public class JPiereFormInfoWindow extends AbstractJPiereFormInfoWindow {

	private CustomForm form;

    public JPiereFormInfoWindow()
    {
    	form = new CustomForm();
    	form.setHeight("100%");
    	form.setWidth("100%");
    }

    @Override
    public void createFormInfoWindow(int AD_InfoWindow_ID)
    {
    	createFormInfoWindow(AD_InfoWindow_ID, null);
    }
    
    @Override
    public void createFormInfoWindow(int AD_InfoWindow_ID, String predefinedVariables)
    {
    	InfoPanel infoPanel = InfoManager.create(AD_InfoWindow_ID, predefinedVariables);
    	infoPanel.setTitle(null);
    	infoPanel.onUserQuery();

    	form.appendChild(infoPanel);
    }


	@Override
	public ADForm getForm()
	{
		return form;
	}




}
