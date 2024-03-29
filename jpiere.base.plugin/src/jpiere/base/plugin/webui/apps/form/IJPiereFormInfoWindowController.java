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

/**
 *  JPIERE-0514: Interface JPiere Form Info Window Controller
 *
 *  @author Hideaki Hagiwara（h.hagiwara@oss-erp.co.jp）
 *
 */
public interface IJPiereFormInfoWindowController
{
	/**
	 * Called by org.adempiere.webui.panel.ADForm.openForm(int)
	 * @return
	 */
	public void createFormInfoWindow(int AD_InfoWindow_ID);
	
	public void createFormInfoWindow(int AD_InfoWindow_ID, String predefinedVariables);


}
