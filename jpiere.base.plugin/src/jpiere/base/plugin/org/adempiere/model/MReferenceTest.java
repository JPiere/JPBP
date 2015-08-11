/******************************************************************************
 * Product: iDempiere - Lab				 			                          *
 * Copyright (C) Hideaki Hagiwara All Rights Reserved.                        *
 * このプログラムはGNU Gneral Public Licens Version2です。                    *
 * このプログラムは萩原秀明が個人の研究用に作成しているもので、			      *
 * いかなる保証もしていません。                                               *
 * 著作権は萩原秀明(h.hagiwara@oss-erp.co.jp)が保持しています。				  *
 *****************************************************************************/
package jpiere.base.plugin.org.adempiere.model;

import java.sql.ResultSet;
import java.util.Properties;

public class MReferenceTest extends X_JP_ReferenceTest {

	public MReferenceTest(Properties ctx, int JP_ReferenceTest_ID,
			String trxName) {
		super(ctx, JP_ReferenceTest_ID, trxName);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public MReferenceTest(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {
		// TODO 自動生成されたメソッド・スタブ
		return super.beforeSave(newRecord);
	}

	@Override
	protected boolean afterSave(boolean newRecord, boolean success) {
		// TODO 自動生成されたメソッド・スタブ
		return super.afterSave(newRecord, success);
	}



}
