package jpiere.base.plugin.org.adempiere.callout;

import java.util.Properties;

import org.adempiere.base.IColumnCallout;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.MCity;

public class JPiereCityCallout implements IColumnCallout {

	@Override
	public String start(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue) {

		Integer C_City_ID = (Integer)value;
		MCity city = null;

		if(C_City_ID == null){
//			mTab.setValue("City", "");
		}else{
			city = MCity.get(ctx, C_City_ID);
			mTab.setValue("City", city.getName());
		}

		return null;
	}

}
