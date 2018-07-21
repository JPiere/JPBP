package jpiere.base.plugin.util;

import java.util.List;

import org.adempiere.base.Service;
import org.compiere.model.MTax;
import org.compiere.util.Util;

import jpiere.base.plugin.org.adempiere.base.IJPiereTaxProvider;
import jpiere.base.plugin.org.adempiere.base.IJPiereTaxProviderFactory;

public class JPiereUtil {

	public JPiereUtil() {
		;
	}

	public static IJPiereTaxProvider getJPiereTaxProvider(MTax m_tax)
	{
		String className = m_tax.getC_TaxProvider().getC_TaxProviderCfg().getTaxProviderClass();
		if(Util.isEmpty(className))
			className = "jpiere.base.plugin.org.adempiere.model.JPiereTaxProvider";//JPiere Default Tax Provider

		IJPiereTaxProvider calculator = null;
		List<IJPiereTaxProviderFactory> factoryList = Service.locator().list(IJPiereTaxProviderFactory.class).getServices();
		if (factoryList != null)
		{
			for (IJPiereTaxProviderFactory factory : factoryList)
			{
				calculator = factory.newJPiereTaxProviderInstance(className);
				if (calculator != null)
				{
					return calculator;
				}

			}//For
		}

		return null;
	}
}
