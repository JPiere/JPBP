package jpiere.base.plugin.org.adempiere.callout;

import java.util.Properties;

import org.adempiere.base.IColumnCallout;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.MRegion;

public class JPiereRegionCallout implements IColumnCallout {

	@Override
	public String start(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue) {

		Integer C_Region_ID = (Integer)value;
		MRegion region = null;

		if(C_Region_ID == null){
//			mTab.setValue("RegionName", "");
		}else{
			region = MRegion.get(ctx, C_Region_ID);
			mTab.setValue("RegionName", region.getName());
		}

		return null;
	}

}
