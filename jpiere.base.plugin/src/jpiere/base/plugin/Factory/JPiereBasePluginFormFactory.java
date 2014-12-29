package jpiere.base.plugin.factory;

import java.util.logging.Level;

import org.adempiere.webui.factory.IFormFactory;
import org.adempiere.webui.panel.ADForm;
import org.adempiere.webui.panel.IFormController;
import org.compiere.util.CLogger;

public class JPiereBasePluginFormFactory implements IFormFactory{

	private static final CLogger log = CLogger.getCLogger(JPiereBasePluginFormFactory.class);

	@Override
	public ADForm newFormInstance(String formName) {
		Object form = null;
	     if (formName.startsWith("jpiere.base.plugin")) {
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
			    form = clazz.newInstance();
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
	     }
	     return null;
	}


}
