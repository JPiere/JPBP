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
package jpiere.base.plugin.org.adempiere.base;

import org.compiere.model.MClient;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.util.CLogger;

import jpiere.base.plugin.org.adempiere.model.MRecognition;
import jpiere.base.plugin.org.adempiere.model.MRecognitionLine;



/**
 *  JPIERE-0363: Contract Management
 *  JPiere Contract Invoice Validator
 *
 *  @author  Hideaki Hagiwara（h.hagiwara@oss-erp.co.jp）
 *
 */
public class JPiereContractRecognitionValidator implements ModelValidator {

	private static CLogger log = CLogger.getCLogger(JPiereContractRecognitionValidator.class);
	private int AD_Client_ID = -1;
	private int AD_Org_ID = -1;
	private int AD_Role_ID = -1;
	private int AD_User_ID = -1;


	@Override
	public void initialize(ModelValidationEngine engine, MClient client) 
	{
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();
		engine.addModelChange(MRecognition.Table_Name, this);
		engine.addModelChange(MRecognitionLine.Table_Name, this);
		engine.addDocValidate(MRecognition.Table_Name, this);
		engine.addDocValidate(MRecognition.Table_Name, this);

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
	public String modelChange(PO po, int type) throws Exception
	{
		if(po.get_TableName().equals(MRecognition.Table_Name))
		{
			return recognitionValidate(po, type);
			
		}else if(po.get_TableName().equals(MRecognitionLine.Table_Name)){
			
			return recognitionLineValidate(po, type);
		}
		
		return null;
	}

	@Override
	public String docValidate(PO po, int timing) 
	{
		
		return null;
	}
	
	
	/**
	 * Recognition Validate
	 * 
	 * @param po
	 * @param type
	 * @return
	 */
	private String recognitionValidate(PO po, int type)
	{
		return null;
	}

	/**
	 * Recognition Line Validate
	 * 
	 * @param po
	 * @param type
	 * @return
	 */
	private String recognitionLineValidate(PO po, int type)
	{
		return null;
	}
}
