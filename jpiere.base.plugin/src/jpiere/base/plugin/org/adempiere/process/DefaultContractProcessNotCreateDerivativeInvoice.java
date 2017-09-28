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

package jpiere.base.plugin.org.adempiere.process;

import jpiere.base.plugin.org.adempiere.model.MContractLine;
import jpiere.base.plugin.org.adempiere.model.MContractLogDetail;

/** 
* JPIERE-0363
*
* @author Hideaki Hagiwara
*
*/
public class DefaultContractProcessNotCreateDerivativeInvoice extends AbstractContractProcess {

	@Override
	protected void prepare() 
	{

		super.prepare();
	}

	@Override
	protected String doIt() throws Exception 
	{		
		super.doIt();
		createContractLogDetail("B6", null, null, null);
		MContractLine[] contractLines = m_ContractContent.getLines(true, "");
		for(int i = 0; i < contractLines.length; i++)
		{
			if(contractLines[i].getJP_ContractProcess_Inv_ID() == getJP_ContractProcess_ID())
			{
				createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForCreateDerivativeDocManually, contractLines[i], null, null);
			}
		}
		
		return "";
	}
	
}
