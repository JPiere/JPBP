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

import org.compiere.util.Msg;

import jpiere.base.plugin.org.adempiere.model.MContractLine;
import jpiere.base.plugin.org.adempiere.model.MContractLogDetail;
import jpiere.base.plugin.org.adempiere.model.MContractProcPeriod;

/** 
* JPIERE-0363
*
* @author Hideaki Hagiwara
*
*/
public class DefaultContractProcessNotCreateDerivativeInOut extends AbstractContractProcess {

	@Override
	protected void prepare() 
	{

		super.prepare();
	}

	@Override
	protected String doIt() throws Exception 
	{		
		super.doIt();

		int JP_ContractProcPeriod_ID = getJP_ContractProctPeriod_ID();
		
		if(JP_ContractProcPeriod_ID == 0)
		{
			String descriptionMsg = Msg.getMsg(getCtx(), "NotFound") + " : " + Msg.getElement(getCtx(), "JP_ContractProcPeriod_ID");
			createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_UnexpectedError, null,  null, descriptionMsg);
			return "";
		}
		
		MContractLine[] contractLines = m_ContractContent.getLines(true, "");
		for(int i = 0; i < contractLines.length; i++)
		{
			
			//Check Contract Process
			if(contractLines[i].getJP_ContractProcess_InOut_ID() != getJP_ContractProcess_ID())
				continue;
			
			//Check Contract Calender
			MContractProcPeriod processPeriod = MContractProcPeriod.get(getCtx(), JP_ContractProcPeriod_ID);
			if(contractLines[i].getJP_ContractCalender_InOut_ID() != processPeriod.getJP_ContractCalender_ID())
				continue;
			
			createContractLogDetail(MContractLogDetail.JP_CONTRACTLOGMSG_SkippedForCreateDerivativeDocManually,contractLines[i], null, null);
			
		}
		
		return "";
	}
	
}
