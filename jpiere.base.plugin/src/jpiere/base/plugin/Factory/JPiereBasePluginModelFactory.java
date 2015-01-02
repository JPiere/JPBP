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

import java.sql.ResultSet;

import jpiere.base.plugin.org.adempiere.model.MCorporation;
import jpiere.base.plugin.org.adempiere.model.MCorporationGroup;
import jpiere.base.plugin.org.adempiere.model.MGroupCorporations;

import org.adempiere.base.IModelFactory;
import org.compiere.model.PO;
import org.compiere.util.Env;

/**
 *  JPiere Base Plugin Model Factory
 *
 *  @author Hideaki Hagiwara（萩原 秀明:h.hagiwara@oss-erp.co.jp）
 *
 */
public class JPiereBasePluginModelFactory implements IModelFactory {

	@Override
	public Class<?> getClass(String tableName) {
		if(tableName.equals(MCorporation.Table_Name)){
			return MCorporation.class;
		}else if(tableName.equals(MCorporationGroup.Table_Name)){
			return MCorporationGroup.class;
		}else if(tableName.equals(MGroupCorporations.Table_Name)){
			return MGroupCorporations.class;
		}

		return null;
	}

	@Override
	public PO getPO(String tableName, int Record_ID, String trxName) {
		if(tableName.equals(MCorporation.Table_Name)){
			return  new MCorporation(Env.getCtx(), Record_ID, trxName);
		}else if(tableName.equals(MCorporationGroup.Table_Name)){
			return  new MCorporationGroup(Env.getCtx(), Record_ID, trxName);
		}else if(tableName.equals(MGroupCorporations.Table_Name)){
			return  new MGroupCorporations(Env.getCtx(), Record_ID, trxName);
		}

		return null;
	}

	@Override
	public PO getPO(String tableName, ResultSet rs, String trxName) {
		if(tableName.equals(MCorporation.Table_Name)){
			return  new MCorporation(Env.getCtx(), rs, trxName);
		}else if(tableName.equals(MCorporationGroup.Table_Name)){
			return  new MCorporationGroup(Env.getCtx(), rs, trxName);
		}else if(tableName.equals(MGroupCorporations.Table_Name)){
			return  new MGroupCorporations(Env.getCtx(), rs, trxName);
		}

		return null;
	}

}
