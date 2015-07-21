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

import jpiere.base.plugin.util.JPierePaymentTerms;

import org.compiere.model.MClient;
import org.compiere.model.MPaymentTerm;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.Env;

public class JPierePaymentTermModelValidator implements ModelValidator {

	private static CLogger log = CLogger.getCLogger(JPierePaymentTermModelValidator.class);
	private int AD_Client_ID = -1;
	private int AD_Org_ID = -1;
	private int AD_Role_ID = -1;
	private int AD_User_ID = -1;

	@Override
	public void initialize(ModelValidationEngine engine, MClient client) {
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();
		engine.addModelChange(MPaymentTerm.Table_Name, this);

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
		//JPIERE-0105
		if(type == ModelValidator.TYPE_BEFORE_NEW || type == ModelValidator.TYPE_BEFORE_CHANGE)
		{
			MPaymentTerm paymentTerm = (MPaymentTerm)po;
			Integer JP_PaymentTerms_ID = (Integer)paymentTerm.get_Value("JP_PaymentTerms_ID");
			if(JP_PaymentTerms_ID == null || JP_PaymentTerms_ID.intValue()==0)
			{
				return null;
			}else{

				if(!paymentTerm.isDueFixed()){
					return "固定日付をOFFにする場合は複数支払条件フィールドを空にして下さい。";//TODO 多言語化
				}

				Boolean IsPaymentTerms = (Boolean)paymentTerm.get_Value("IsPaymentTermsJP");
				if(IsPaymentTerms.booleanValue())
				{
					return "複数支払条件フラグをONにする場合は複数支払条件フィールドを空にして下さい。";//TODO 多言語化
				}

				MPaymentTerm[] paymentTerms = JPierePaymentTerms.getPaymentTerms(Env.getCtx(), JP_PaymentTerms_ID);
				for(int i = 0 ; i < paymentTerms.length; i++)
				{
					if(paymentTerm.getFixMonthCutoff() == paymentTerms[i].getFixMonthCutoff()
							&& paymentTerm.getC_PaymentTerm_ID() != paymentTerms[i].getC_PaymentTerm_ID())
					{
						return "締切日付が同じ支払条件が既に存在します。";//TODO 多言語化
					}

					;
				}//for

			}//if

		}//if

		return null;
	}

	@Override
	public String docValidate(PO po, int timing) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

}
