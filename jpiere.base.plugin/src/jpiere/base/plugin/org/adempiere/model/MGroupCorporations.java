/******************************************************************************
 * Product: JPiere                                                            *
 * Copyright (C) Hideaki Hagiwara (h.hagiwara@oss-erp.co.jp)                  *
 *                                                                            *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY.                          *
 * See the GNU General Public License for more details.                       *
 *                                                                            *
 * JPiere is maintained by OSS ERP Solutions Co., Ltd.                        *
 * (http://www.oss-erp.co.jp)                                                 *
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
