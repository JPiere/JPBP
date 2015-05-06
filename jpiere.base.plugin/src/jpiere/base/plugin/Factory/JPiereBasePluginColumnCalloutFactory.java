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

import java.util.ArrayList;
import java.util.List;

import jpiere.base.plugin.org.adempiere.callout.JPiereBankAcountCallout;
import jpiere.base.plugin.org.adempiere.callout.JPiereCityCallout;
import jpiere.base.plugin.org.adempiere.callout.JPiereRegionCallout;

import org.adempiere.base.IColumnCallout;
import org.adempiere.base.IColumnCalloutFactory;
import org.compiere.model.MLocation;
import org.compiere.model.MPayment;

/**
 * @author Hideaki Hagiwara
 *
 */
public class JPiereBasePluginColumnCalloutFactory implements IColumnCalloutFactory {

	@Override
	public IColumnCallout[] getColumnCallouts(String tableName, String columnName) {

		List<IColumnCallout> list = new ArrayList<IColumnCallout>();

		if(tableName.equals(MPayment.Table_Name) && columnName.equals(MPayment.COLUMNNAME_C_BankAccount_ID))
		{
			list.add(new JPiereBankAcountCallout());
		}else if(tableName.equals(MLocation.Table_Name) && columnName.equals(MLocation.COLUMNNAME_C_Region_ID)){
			list.add(new JPiereRegionCallout());
		}else if(tableName.equals(MLocation.Table_Name) && columnName.equals(MLocation.COLUMNNAME_C_City_ID)){
			list.add(new JPiereCityCallout());
		}

		return list != null ? list.toArray(new IColumnCallout[0]) : new IColumnCallout[0];
	}

}
