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

import org.compiere.model.PO;
import org.compiere.process.DocAction;
import org.compiere.util.Env;
import org.compiere.util.Msg;

import jpiere.base.plugin.org.adempiere.model.MContractContent;
import jpiere.base.plugin.org.adempiere.model.MContractLine;
import jpiere.base.plugin.org.adempiere.model.MContractLineT;

/** JPIERE-0363
*
* @author Hideaki Hagiwara
*
*/
public class DefaultCreateContractByCopy extends AbstractCreateContractByCopy {

	@Override
	protected void prepare()
	{
		super.prepare();
	}

	@Override
	protected String doIt() throws Exception
	{
		super.doIt();

		if(p_JP_ContractTabLevel.equals(JP_ContractTabLevel_Document))
		{
			createContractContent();

		}else if(p_JP_ContractTabLevel.equals(JP_ContractTabLevel_Content)){

			createContractLine(to_ContractContent, from_ContractContent, true);

		}

		return Msg.getMsg(getCtx(), "Success");
	}

	protected void createContractContent() throws Exception
	{

		MContractContent[]  from_ContractContents = from_Contract.getContractContents();

		//Create Contract Content
		for(int i = 0 ; i < from_ContractContents.length; i++)
		{
			MContractContent to_ContractContent = new MContractContent(getCtx(), 0, get_TrxName());
			PO.copyValues(from_ContractContents[i], to_ContractContent);
			to_ContractContent.setAD_Org_ID(to_Contract.getAD_Org_ID());
			to_ContractContent.setAD_OrgTrx_ID(to_Contract.getAD_OrgTrx_ID());
			to_ContractContent.setJP_Contract_ID(to_Contract.get_ID());
			to_ContractContent.setJP_ContractContentT_ID(from_ContractContents[i].getJP_ContractContentT_ID());
			to_ContractContent.setJP_Contract_Acct_ID(from_ContractContents[i].getJP_Contract_Acct_ID());
			to_ContractContent.setDateDoc(from_ContractContents[i].getDateDoc());
			to_ContractContent.setDateAcct(from_ContractContents[i].getDateAcct());
			to_ContractContent.setDatePromised(from_ContractContents[i].getDatePromised()) ;
			to_ContractContent.setDateInvoiced(from_ContractContents[i].getDateInvoiced());

			to_ContractContent.setTotalLines(Env.ZERO);
			to_ContractContent.setDocStatus(DocAction.STATUS_Drafted);
			to_ContractContent.setDocAction(DocAction.ACTION_Complete);
			to_ContractContent.setIsScheduleCreatedJP(false);
			to_ContractContent.setJP_ContractProcStatus(MContractContent.JP_CONTRACTPROCSTATUS_Unprocessed);

			setDocumentNoOfContractContent(from_ContractContents[i], to_ContractContent);
			setBPartnerOfContractContent(from_ContractContents[i], to_ContractContent);
			try {
				setWarehouseOfContractContent(from_ContractContents[i], to_ContractContent);
			} catch (Exception e) {
				throw e;
			}

			to_ContractContent.setC_Currency_ID(to_ContractContent.getM_PriceList().getC_Currency_ID());
			try {
				to_ContractContent.saveEx(get_TrxName());
			}catch (Exception e) {
				throw new Exception( Msg.getMsg(getCtx(), "SaveError") + Msg.getElement(getCtx(), "CopyFrom") + " : "
						+ Msg.getElement(getCtx(), "JP_ContractContent_ID") + "_" + from_ContractContents[i].getDocumentNo() + " >>> " + e.getMessage() );
			}

			try {
				createContractLine(to_ContractContent, from_ContractContents[i], false);
			}catch (Exception e) {
				throw new Exception( Msg.getMsg(getCtx(), "Error") + Msg.getElement(getCtx(), "CopyFrom") + " : "
						+ Msg.getElement(getCtx(), "JP_ContractContent_ID") + "_" + from_ContractContents[i].getDocumentNo() + " >>> " + e.getMessage() );
			}

		}//For i

	}//createContractContent


	protected void createContractLine(MContractContent to_ContractContent, MContractContent from_ContractContent, boolean isReSetPeriod) throws Exception
	{
		MContractLine[] to_ContractLines = to_ContractContent.getLines();
		if(to_ContractLines.length > 0)
		{
			throw new Exception(Msg.getMsg(getCtx(), "JP_ContractContentLineCreated"));//Contract Content Line has already been created
		}


		//Create Contract Content Line
		MContractLine[] from_ContractLines = from_ContractContent.getLines();
		for(int i = 0; i < from_ContractLines.length; i++)
		{
			MContractLine to_ContractLine = new MContractLine(getCtx(), 0, get_TrxName());
			PO.copyValues(from_ContractLines[i], to_ContractLine);
			to_ContractLine.setAD_Org_ID(to_ContractContent.getAD_Org_ID());
			to_ContractLine.setAD_OrgTrx_ID(to_ContractContent.getAD_OrgTrx_ID());
			to_ContractLine.setDateOrdered(to_ContractContent.getDateOrdered());
			to_ContractLine.setDatePromised(to_ContractLine.getDatePromised()) ;
			to_ContractLine.setJP_ContractContent_ID(to_ContractContent.getJP_ContractContent_ID());
			to_ContractLine.setJP_ContractLineT_ID(from_ContractLines[i].getJP_ContractLineT_ID());

			if(isReSetPeriod)
			{
				setBaseDocLineProcPeriod(to_ContractLine, MContractLineT.get(getCtx(), to_ContractLine.getJP_ContractLineT_ID()));
				setDerivativeInOutLineProcPeriod(to_ContractLine, MContractLineT.get(getCtx(), to_ContractLine.getJP_ContractLineT_ID()));
				setDerivativeInvoiceLineProcPeriod(to_ContractLine, MContractLineT.get(getCtx(), to_ContractLine.getJP_ContractLineT_ID()));
			}

			try
			{
				to_ContractLine.saveEx(get_TrxName());
			}catch (Exception e) {
				throw new Exception(Msg.getMsg(getCtx(), "SaveError") + Msg.getElement(getCtx(), "CopyFrom") + " : "
						+ Msg.getElement(getCtx(), "JP_ContractLine_ID") + "_" + from_ContractLines[i].getLine() + " >>> " + e.getMessage() );
			}

		}//For i

	}//createContractLine

}
