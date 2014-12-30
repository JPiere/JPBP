package jpiere.base.plugin.org.adempiere.model;

import java.sql.ResultSet;
import java.util.Properties;

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
