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
import org.compiere.model.MInOut;
import org.compiere.model.MInOutLine;
import org.compiere.model.MRMA;
import org.compiere.model.MRMALine;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.Env;

import jpiere.base.plugin.org.adempiere.model.MContract;
import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractProcPeriod;




/**
 *  JPIERE-0363: Contract Management
 *  JPiere Contract RMA Validator
 *
 *  @author  Hideaki Hagiwara（h.hagiwara@oss-erp.co.jp）
 *
 */
public class JPiereContractRMAValidator implements ModelValidator {

	private static CLogger log = CLogger.getCLogger(JPiereContractRMAValidator.class);
	private int AD_Client_ID = -1;
	private int AD_Org_ID = -1;
	private int AD_Role_ID = -1;
	private int AD_User_ID = -1;


	@Override
	public void initialize(ModelValidationEngine engine, MClient client) 
	{
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();
		engine.addModelChange(MRMA.Table_Name, this);
		engine.addModelChange(MRMALine.Table_Name, this);
		engine.addDocValidate(MRMA.Table_Name, this);

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
		if(po.get_TableName().equals(MRMA.Table_Name))
		{
			return rmaValidate(po, type);
			
		}else if(po.get_TableName().equals(MRMALine.Table_Name)){
			
			return rmaLineValidate(po, type);
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
	private String rmaValidate(PO po, int type)
	{

		if( type == ModelValidator.TYPE_BEFORE_NEW 
				||( type == ModelValidator.TYPE_BEFORE_CHANGE && po.is_ValueChanged(MRMA.COLUMNNAME_InOut_ID) ) )
		{
			
			int M_InOut_ID = po.get_ValueAsInt(MRMA.COLUMNNAME_InOut_ID);
			if(M_InOut_ID <= 0)
			{
				po.set_ValueNoCheck("JP_Contract_ID", null);
				po.set_ValueNoCheck("JP_ContractContent_ID", null);
				po.set_ValueNoCheck("JP_ContractProcPeriod_ID", null);
				
				return null;
			}
			
			MInOut io = new MInOut(Env.getCtx(),M_InOut_ID, po.get_TrxName());
			int JP_Contract_ID = io.get_ValueAsInt("JP_Contract_ID");
			if(JP_Contract_ID <= 0)
			{
				po.set_ValueNoCheck("JP_Contract_ID", null);
				po.set_ValueNoCheck("JP_ContractContent_ID", null);
				po.set_ValueNoCheck("JP_ContractProcPeriod_ID", null);
				
				return null;
				
			}else{
				
				po.set_ValueNoCheck("JP_Contract_ID", JP_Contract_ID);
				po.set_ValueNoCheck("JP_ContractContent_ID", io.get_ValueAsInt("JP_ContractContent_ID"));
				po.set_ValueNoCheck("JP_ContractProcPeriod_ID", io.get_ValueAsInt("JP_ContractProcPeriod_ID"));
				
			}
		}
		
		return null;
	}

	
	/**
	 * Recognition Line Validate
	 * 
	 * @param po
	 * @param type
	 * @return
	 */
	private String rmaLineValidate(PO po, int type)
	{
		if( type == ModelValidator.TYPE_BEFORE_NEW 
				||( type == ModelValidator.TYPE_BEFORE_CHANGE && po.is_ValueChanged(MRMALine.COLUMNNAME_M_InOutLine_ID) ) )
		{
			
			int M_InOutLine_ID = po.get_ValueAsInt(MRMALine.COLUMNNAME_M_InOutLine_ID);
			if(M_InOutLine_ID <= 0)
			{
				po.set_ValueNoCheck("JP_ContractLine_ID", null);
				po.set_ValueNoCheck("JP_ContractProcPeriod_ID", null);
				
				return null;
			}
			
			MInOutLine ioLine = new MInOutLine(Env.getCtx(),M_InOutLine_ID, po.get_TrxName());
			int JP_ContractLine_ID = ioLine.get_ValueAsInt("JP_ContractLine_ID");
			if(JP_ContractLine_ID <= 0)
			{
				po.set_ValueNoCheck("JP_ContractLine_ID", null);
				po.set_ValueNoCheck("JP_ContractProcPeriod_ID", null);
				
				return null;
				
			}else{
				
				po.set_ValueNoCheck("JP_ContractLine_ID", ioLine.get_ValueAsInt("JP_ContractLine_ID"));
				po.set_ValueNoCheck("JP_ContractProcPeriod_ID", ioLine.get_ValueAsInt("JP_ContractProcPeriod_ID"));
				
			}
		}
		
		return null;
	}


}
