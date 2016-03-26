package jpiere.base.plugin.util;

import java.util.List;

import jpiere.base.plugin.org.adempiere.base.IJPiereTaxProvider;
import jpiere.base.plugin.org.adempiere.base.IJPiereTaxProviderFactory;

import org.adempiere.base.Service;
import org.compiere.model.MTax;

public class JPiereUtil {

	public JPiereUtil() {
		;
	}

	public static IJPiereTaxProvider getJPiereTaxProvider(MTax m_tax)
	{
		String className = m_tax.getC_TaxProvider().getC_TaxProviderCfg().getTaxProviderClass();
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
