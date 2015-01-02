/******************************************************************************
 * Product: JPiere(ジェイピエール) - JPiere Base Plugin                       *
 * Copyright (C) Hideaki Hagiwara All Rights Reserved.                        *
 * このプログラムはGNU Gneral Public Licens Version2のもと公開しています。    *
 * このプログラムは自由に活用してもらう事を期待して公開していますが、         *
 * いかなる保証もしていません。                                               *
 * 著作権は萩原秀明(h.hagiwara@oss-erp.co.jp)が保持し、サポートサービスは     *
 * 株式会社オープンソース・イーアールピー・ソリューションズで                 *
 * 提供しています。サポートをご希望の際には、                                 *
 * 株式会社オープンソース・イーアールピー・ソリューションズまでご連絡下さい。 *
 * http://www.oss-erp.co.jp/                                                  *
 *****************************************************************************/
package jpiere.base.plugin.factory;

import java.util.logging.Level;

import org.adempiere.base.ITaxProviderFactory;
import org.adempiere.base.equinox.EquinoxExtensionLocator;
import org.adempiere.model.ITaxProvider;
import org.compiere.util.CLogger;

/**
 * JPiere Tax Provider Factory
 *
 *
 * @author Hideaki Hagiwara（萩原 秀明:h.hagiwara@oss-erp.co.jp）
 * @version  $Id: JPiereTaxProviderFactory.java,v 1.0 2014/08/20
 *
 */
public class JPiereBasePluginTaxProviderFactory implements ITaxProviderFactory {

	private final static CLogger s_log = CLogger.getCLogger(JPiereBasePluginTaxProviderFactory.class);

	@Override
	public ITaxProvider newTaxProviderInstance(String className) {

		if(className.startsWith("jpiere.base.plugin")){


			ITaxProvider myCalculator = EquinoxExtensionLocator.instance().locate(ITaxProvider.class, className, null).getExtension();
			if (myCalculator == null)
			{
				//fall back to dynamic java class loading
				try
				{
					Class<?> ppClass = Class.forName(className);
					if (ppClass != null)
						myCalculator = (ITaxProvider) ppClass.newInstance();
				}
				catch (Error e1)
				{   //  NoClassDefFound
					s_log.log(Level.SEVERE, className + " - Error=" + e1.getMessage());
					return null;
				}
				catch (Exception e2)
				{
					s_log.log(Level.SEVERE, className, e2);
					return null;
				}
			}
			if (myCalculator == null)
			{
				s_log.log(Level.SEVERE, "Not found in extension registry and classpath");
				return null;
			}

			return myCalculator;
		}

		return null;
	}
}
