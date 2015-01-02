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

import org.adempiere.base.IModelValidatorFactory;
import org.compiere.model.ModelValidator;

/**
 *  JPiere Bank Statement Tax Model Validator Factory
 *
 *  @author Hideaki Hagiwara（萩原 秀明:h.hagiwara@oss-erp.co.jp）
 *  @version  $Id: JPiereBankStatementTaxModelValidatorFactory.java,v 1.0 2014/08/20
 *
 */
public class JPiereBasePluginModelValidatorFactory implements IModelValidatorFactory {

	/**
	 * default constructor
	 */
	public JPiereBasePluginModelValidatorFactory() {
	}

	/* (non-Javadoc)
	 * @see org.adempiere.base.IModelValidatorFactory#newModelValidatorInstance(java.lang.String)
	 */
	@Override
	public ModelValidator newModelValidatorInstance(String className) {
		ModelValidator validator = null;

		if (className.startsWith("jpiere.base.plugin")) {
			Class<?> clazz = null;

			//use context classloader if available
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			if (classLoader != null) {
				try {
					clazz = classLoader.loadClass(className);
				}
				catch (ClassNotFoundException ex) {
				}
			}
			if (clazz == null) {
				classLoader = this.getClass().getClassLoader();
				try {
					clazz = classLoader.loadClass(className);
				}
				catch (ClassNotFoundException ex) {
				}
			}
			if (clazz != null) {
				try {
					validator = (ModelValidator)clazz.newInstance();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				new Exception("Failed to load model validator class " + className).printStackTrace();
			}
		}

		return validator;
	}

}
