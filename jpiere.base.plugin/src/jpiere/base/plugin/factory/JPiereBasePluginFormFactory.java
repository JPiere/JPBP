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

package jpiere.base.plugin.factory;

import java.util.logging.Level;

import org.adempiere.webui.factory.IFormFactory;
import org.adempiere.webui.panel.ADForm;
import org.adempiere.webui.panel.IFormController;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.zkoss.zul.Html;
import org.zkoss.zul.Vlayout;

import jpiere.base.plugin.webui.apps.form.AbstractJPiereFormInfoWindow;


/**
 *  JPiere Base Plugin Form Factory
 *
 *  @author Hideaki Hagiwara（h.hagiwara@oss-erp.co.jp）
 *
 */
public class JPiereBasePluginFormFactory implements IFormFactory{

	private static final CLogger log = CLogger.getCLogger(JPiereBasePluginFormFactory.class);

	@Override
	public ADForm newFormInstance(String formName)
	{
		Object form = null;
	     if (formName.startsWith("jpiere.base.plugin"))
	     {
	           ClassLoader cl = getClass().getClassLoader();
	           Class<?> clazz = null;

			  try
			  {
				clazz = cl.loadClass(formName);
		      }
			  catch (Exception e)
			  {
			    if (log.isLoggable(Level.INFO))
			       log.log(Level.INFO, e.getLocalizedMessage(), e);
		            return null;
			  }
		         try
			  {
			    form = clazz.getDeclaredConstructor().newInstance();
			  }
			  catch (Exception e)
			  {
			     if (log.isLoggable(Level.WARNING))
				log.log(Level.WARNING, e.getLocalizedMessage(), e);
		      }

		      if (form != null) {
			     if (form instanceof ADForm) {
			    	 return (ADForm)form;
			     }
			     else if (form instanceof IFormController) {
					IFormController controller = (IFormController) form;
					ADForm adForm = controller.getForm();
					adForm.setICustomForm(controller);
					return adForm;
			     }
		     }

	     }else  if (formName.startsWith("JP_PivotWindow_ID=")){

	    	 ADForm adForm = new ADForm()
	    	 {
				@Override
				protected void initForm()
				{
					Vlayout div = new Vlayout();
					this.appendChild(div);
					div.setStyle("font-size: 12px;line-height: 18px;border: 1px solid #dddddd; padding: 2px; margin: 2px");
					div.appendChild(new Html(Msg.getMsg(Env.getCtx(), "JP_PivotWindow_JPiereSupporter")));//Pivot Window use library of ZK Pivottable that is Commercial License.
					div.appendChild(new Html(Msg.getMsg(Env.getCtx(), "JP_SupporterURL")));
					div.appendChild(new Html(Msg.getMsg(Env.getCtx(), "JP_PivotWindow_Demo")));//You can try Pivot Window at JPiere Demo site.
					div.appendChild(new Html(Msg.getMsg(Env.getCtx(), "JP_DemoSiteURL")));//<p>JPiere Demo Site: <a href="http://jpiere.net/webui/" target="_blank">http://jpiere.net/webui/</a></p>


				}
			};

			return adForm;

	     }else  if (formName.startsWith("AD_InfoWindow_ID=")){

	    	 String[] para = formName.split(";");
	    	 int AD_InfoWindow_ID = Integer.valueOf(para[0].substring("AD_InfoWindow_ID=".length())).intValue();
	    	 String predefinedVariables = null;
	    	 
	    	 if(para.length > 1)
	    		 predefinedVariables = para[1].replace(",", "\n");

			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			Class<?> clazz = null;
			if (loader != null) {
	    		try
	    		{
	        		clazz = loader.loadClass("jpiere.base.plugin.webui.apps.form.JPiereFormInfoWindow");
	    		}
	    		catch (Exception e)
	    		{
	    			if (log.isLoggable(Level.INFO))
	    				log.log(Level.INFO, e.getLocalizedMessage(), e);
	    		}
			}
			if (clazz == null) {
				loader = this.getClass().getClassLoader();
				try
	    		{
	    			//	Create instance w/o parameters
	        		clazz = loader.loadClass("jpiere.base.plugin.webui.apps.form.JPiereFormInfoWindow");
	    		}
	    		catch (Exception e)
	    		{
	    			if (log.isLoggable(Level.INFO))
	    				log.log(Level.INFO, e.getLocalizedMessage(), e);
	    		}
			}

			if (clazz != null) {
				try
	    		{
	    			form = clazz.getDeclaredConstructor().newInstance();
	    		}
	    		catch (Exception e)
	    		{
	    			if (log.isLoggable(Level.WARNING))
	    				log.log(Level.WARNING, e.getLocalizedMessage(), e);
	    		}
			}

			if (form != null) {
				if (form instanceof AbstractJPiereFormInfoWindow ) {
					AbstractJPiereFormInfoWindow  controller = (AbstractJPiereFormInfoWindow) form;
					controller.createFormInfoWindow(AD_InfoWindow_ID, predefinedVariables);
					ADForm adForm = controller.getForm();
					adForm.setICustomForm(controller);
					return adForm;
				}
			}

	     }

	     return null;
	}


}
