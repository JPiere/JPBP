package jpiere.base.plugin.factory;

import jpiere.base.plugin.org.adempiere.process.I_CreateBill;
import jpiere.base.plugin.org.adempiere.process.I_CreateBillFactory;

public class JPiereBasePluginCreateBillFactory implements I_CreateBillFactory {

	@Override
	public I_CreateBill getCreateBill(String className)
	{

		if(className == null || className.equals("jpiere.base.plugin.org.adempiere.process.DefaultCreateBill"))
		{

			Class<?> clazz = null;
			I_CreateBill createBill = null;
			try
			{
				clazz = Class.forName("jpiere.base.plugin.org.adempiere.process.DefaultCreateBill");
				if(clazz != null)
					createBill = (I_CreateBill)clazz.getDeclaredConstructor().newInstance();

			}catch (Exception e) {
				;
			}

			return createBill;

		}

		return null;
	}

}
