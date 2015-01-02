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

package jpiere.base.plugin.org.adempiere.model;

import java.sql.ResultSet;
import java.util.Properties;

/**
 *  Group Corporations(所属法人マスタ) Model.
 *
 *  @author Hideaki Hagiwara（萩原 秀明:h.hagiwara@oss-erp.co.jp）
 *
 */
public class MGroupCorporations extends X_JP_GroupCorporations {

	public MGroupCorporations(Properties ctx, int JP_GroupCorporations_ID,
			String trxName) {
		super(ctx, JP_GroupCorporations_ID, trxName);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public MGroupCorporations(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO 自動生成されたコンストラクター・スタブ
	}


	private MCorporationGroup m_parent = null;

	@Override
	protected boolean beforeSave(boolean newRecord)
	{
		MCorporationGroup parent = getParent();
		MCorporation[] gc = parent.getCorporations();
		for(int i= 0; i < gc.length; i++)
		{
			if(gc[i].getJP_Corporation_ID()==getJP_Corporation_ID())
			{
				log.saveError("Error","既にその法人は登録されています。");
				return false;
			}
		}
		return true;
	}

	public MCorporationGroup getParent()
	{
		if(m_parent == null)
			m_parent = new MCorporationGroup(getCtx(), getJP_CorporationGroup_ID(), get_TrxName());

		return m_parent;
	}

}
