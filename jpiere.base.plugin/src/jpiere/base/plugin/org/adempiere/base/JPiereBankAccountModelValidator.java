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
package jpiere.base.plugin.org.adempiere.base;

import jpiere.base.plugin.util.ZenginCheck;

import org.compiere.model.MBankAccount;
import org.compiere.model.MClient;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Msg;

public class JPiereBankAccountModelValidator implements ModelValidator {

	private static CLogger log = CLogger.getCLogger(JPiereBankAccountModelValidator.class);
	private int AD_Client_ID = -1;
	private int AD_Org_ID = -1;
	private int AD_Role_ID = -1;
	private int AD_User_ID = -1;

	@Override
	public void initialize(ModelValidationEngine engine, MClient client) {
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();
		engine.addModelChange(MBankAccount.Table_Name, this);

	}

	@Override
	public int getAD_Client_ID() {
		return AD_Client_ID;
	}

	@Override
	public String login(int AD_Org_ID, int AD_Role_ID, int AD_User_ID) {
		this.AD_Org_ID = AD_Org_ID;
		this.AD_Role_ID = AD_Role_ID;
		this.AD_User_ID = AD_User_ID;

		return null;
	}

	@Override
	public String modelChange(PO po, int type) throws Exception {
		//JPIERE-0102
		if(type == ModelValidator.TYPE_BEFORE_NEW || type == ModelValidator.TYPE_BEFORE_CHANGE)
		{
			MBankAccount bankAcct = (MBankAccount)po;
			String jp_RequesterName = (String)bankAcct.get_Value("JP_RequesterName");
			if(jp_RequesterName != null)
			{
				for(int i = 0; i < jp_RequesterName.length(); i++)
				{
					if(!ZenginCheck.charCheck(jp_RequesterName.charAt(i)))
					{
						return "「" + jp_RequesterName.charAt(i) + "」は使えない文字です。";
					}
				}//for

				if(jp_RequesterName.length() > ZenginCheck.JP_RequesterName)
				{
					return Msg.getElement(Env.getCtx(), "JP_BankName_Kana") + "は" + ZenginCheck.JP_RequesterName + "以内です。";
				}


				String jp_RequesterCode = (String)bankAcct.get_Value("JP_RequesterCode");
				if(jp_RequesterCode.length()!=ZenginCheck.JP_RequesterCode)
				{
					return Msg.getElement(Env.getCtx(), "JP_RequesterCode") + "は" + ZenginCheck.JP_RequesterCode + "桁です。";
				}

				if(!ZenginCheck.numStringCheck(jp_RequesterCode))
				{
					return Msg.getElement(Env.getCtx(), "JP_RequesterCode") + "に半角数値以外の文字が使用されています。";
				}


				String jp_BranchCode = (String)bankAcct.get_Value("JP_BranchCode");
				if(jp_BranchCode.length()!=ZenginCheck.JP_BranchCode)
				{
					return Msg.getElement(Env.getCtx(), "JP_BranchCode") + "は" + ZenginCheck.JP_BranchCode + "桁です。";
				}

				if(!ZenginCheck.numStringCheck(jp_BranchCode))
				{
					return Msg.getElement(Env.getCtx(), "JP_BranchCode") + "に半角数値以外の文字が使用されています。";
				}


				String jp_BranchName_Kana = (String)bankAcct.get_Value("JP_BranchName_Kana");
				for(int i = 0; i < jp_BranchName_Kana.length(); i++)
				{
					if(!ZenginCheck.charCheck(jp_BranchName_Kana.charAt(i)))
					{
						return "「" + jp_BranchName_Kana.charAt(i) + "」は使えない文字です。";
					}
				}//for

				if(jp_BranchName_Kana.length() > ZenginCheck.JP_BranchName_Kana)
				{
					return Msg.getElement(Env.getCtx(), "jp_BranchName_Kana") + "は" + ZenginCheck.JP_BranchName_Kana + "以内です。";
				}


				String accountNo = (String)bankAcct.getAccountNo();
				if(accountNo.length()!=ZenginCheck.JP_AccountNo)
				{
					return Msg.getElement(Env.getCtx(), "AccountNo") + "は" + ZenginCheck.JP_AccountNo + "桁です。";
				}

				if(!ZenginCheck.numStringCheck(accountNo))
				{
					return Msg.getElement(Env.getCtx(), "AccountNo") + "に半角数値以外の文字が使用されています。";
				}

				bankAcct.setPaymentExportClass(ZenginCheck.PAYMENT_EXPORT_CLASS);

			}//if(characters != null)
		}

		return null;
	}

	@Override
	public String docValidate(PO po, int timing) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

}
