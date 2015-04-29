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

import org.compiere.model.MClient;
import org.compiere.model.MPayment;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.util.CLogger;


/**
 *  JPiere Payment Model Validator
 *
 *  @author  Hideaki Hagiwara（萩原 秀明:h.hagiwara@oss-erp.co.jp）
 *  @version  $Id: JPierePaymentModelValidator.java,v 1.0 2015/04/29
 *
 */
public class JPierePaymentModelValidator implements ModelValidator {

	private static CLogger log = CLogger.getCLogger(JPierePaymentModelValidator.class);
	private int AD_Client_ID = -1;
	private int AD_Org_ID = -1;
	private int AD_Role_ID = -1;
	private int AD_User_ID = -1;


	@Override
	public void initialize(ModelValidationEngine engine, MClient client) {
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();
//		engine.addModelChange(MPayment.Table_Name, this);
		engine.addDocValidate(MPayment.Table_Name, this);

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

		return null;
	}

	@Override
	public String docValidate(PO po, int timing) {

		//JPIERE-0091
		if(timing == ModelValidator.TIMING_BEFORE_VOID ||
				timing == ModelValidator.TIMING_BEFORE_REVERSEACCRUAL ||
				timing == ModelValidator.TIMING_BEFORE_REVERSECORRECT ||
				timing == ModelValidator.TIMING_AFTER_VOID ||
				timing == ModelValidator.TIMING_AFTER_REVERSEACCRUAL ||
				timing == ModelValidator.TIMING_AFTER_REVERSECORRECT )
		{
			MPayment payment = (MPayment)po;
			if(payment.isReconciled())
			{
				return "出納帳に記帳されています。出納帳を先にボイドして下さい。";
			}
		}

		return null;
	}

}
